package paral_calc;

/** This program is written by Pavel V. Maksimov using JOCL - Java bindings for OpenCL.
 * Heat transfer algorithm on the base of SECA method is realized.
 * Date of creation: 2 April 2015
 * Copyright 2009 Marco Hutter - http://www.jocl.org/
 */

// package org.jocl.samples;
import static org.jocl.CL.*;
import org.jocl.*;
import java.util.*;
import java.io.*;
import cellcore.*;

/** A small JOCL sample simulates heat transfer on the base of SECA method.
 */
public class SECAM_JOCL
{
    /** The source code of the OpenCL program to execute
     */
    private static String programSource =
        "__kernel void "+
        "sampleKernel(__global const float *a,"+
        "             __global const float *b,"+
        "             __global float *c)"+
        "{"+
        "    int gid = get_global_id(0);"+
        "    c[gid]  = a[gid]*a[gid] + a[gid]*b[gid] + a[gid]*b[gid] + b[gid]*b[gid] + "+
                      "a[gid]*a[gid] + a[gid]*b[gid] + a[gid]*b[gid] + b[gid]*b[gid] + "+
                      "a[gid]*a[gid] + a[gid]*b[gid] + a[gid]*b[gid] + b[gid]*b[gid];"+
        "}";
    
    /** The source code of the OpenCL program to execute
     */
    private static String programSource1 =
        "__kernel void "+
        "sampleKernel(__global const int *i,"+            
        "             __global const int *size_I,"+
        "             __global const int *j,"+
        "             __global const int *size_J,"+            
        "             __global const int *k,"+
        "             __global int *n)"+
        "{"+
        "    int gid = get_global_id(0);"+
        "    n[gid]  = size_I[0]*size_J[0]*k[gid] + size_I[0]*j[gid] + i[gid];"+
      //  "              size_I[0]*size_J[0]*k[gid] + size_I[0]*j[gid] + i[gid] +"+
      //  "              size_I[0]*size_J[0]*k[gid] + size_I[0]*j[gid] + i[gid] +"+
      //  "              size_I[0]*size_J[0]*k[gid] + size_I[0]*j[gid] + i[gid];"+
      //  "    n[gid]  = n[gid]/4;"+
        "}";    
    
    private final static byte HEXAGONAL_CLOSE_PACKING = 0;
    private final static byte SIMPLE_CUBIC_PACKING    = 1;
    private static byte packing_type;
    private static int neighb1S_number;
    
    private static int[][] neighbours1S;

    // Cell diameter in meters
    private final static double CELL_SIZE        = 1.0E-6;
    
    // Number of cells along axes
    private final static int CELL_NUMBER_I       = 1;// 8;// 64;// 56;
    private final static int CELL_NUMBER_J       = 1;// 12;// 60;// 56;
    private final static int CELL_NUMBER_K       = 1;// 8;// 64;// 64;
    
    private final static int LOCAL_CELL_NUMBER_I = 1;// 8;// 14;
    private final static int LOCAL_CELL_NUMBER_J = 1;// 12;//  8;
    private final static int LOCAL_CELL_NUMBER_K = 1;//  8;//  8;
    
    private final static long GLOBAL_WORK_SIZE   = 1024; // CELL_NUMBER_I*CELL_NUMBER_J*CELL_NUMBER_K; // 
    private final static long LOCAL_WORK_SIZE    = LOCAL_CELL_NUMBER_I*LOCAL_CELL_NUMBER_J*LOCAL_CELL_NUMBER_K;// 1024; // GLOBAL_WORK_SIZE;// 
    
    // Number of time steps
    private final static int STEP_NUMBER         = 1;
    
    // The value of time step
    private final static double TIME_STEP        = 1.0E-9;
    
    // Initial and boundary conditions
    private final static float INITIAL_TEMPERATURE  = (float) 300.0;
    private final static float BOUNDARY_TEMPERATURE = (float)1500.0;
    
    // Parameters of aluminium
    private final static float HEAT_CONDUCTIVITY    = (float) 237.0;
    private final static float HEAT_CAPACITY        = (float) 930.0;
    private final static float HEAT_EXPANSION_COEFF = (float)   2.29E-5;
    private final static float DENSITY              = (float)2700.0;
    
    // Cell location types
    private final static byte INTERNAL_CELL =  3;
    private final static byte BOUNDARY_CELL =  2;
    
    // Cell types according to interaction with neighbours
    private final static byte INTERNAL_INTERACTED_CELL =  1;
    private final static byte BOUNDARY_INTERACTED_CELL =  3;
    private final static byte BOUNDARY_ADIABATIC_CELL  = 11;
    
    /** The entry point of this sample
     * @param args Not used
     */
    public static void main(String args[])
    {
        packing_type =  HEXAGONAL_CLOSE_PACKING;
        
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
            neighb1S_number = 12;
        
        if(packing_type == SIMPLE_CUBIC_PACKING)
            neighb1S_number = 6;
            
        boolean multiplyArrays1D  = false; // true;  // 
        boolean multiplyArrays2D  = false; // true;  // 
        boolean calcSingleIndices = false; // true;  // 
        boolean parallelCalcHeatFlows    = true;  // false; // 
        boolean nonParallelCalcHeatFlows = false; // true;  // 
        boolean parallelMatrixMultiplication = false; // true;  // 
        
        if(multiplyArrays1D)  multiplyArrays1D();
        
        if(multiplyArrays2D)  multiplyArrays2D();
        
        if(calcSingleIndices) calcSingleIndices();
        
        // The method simulates heat propagation in cellular automaton.
        if(parallelCalcHeatFlows)  
            parallelCalcHeatFlows();
        
      /*
        try
        {
            parallelCalcHeatFlows();
        }
        catch(org.jocl.CLException cl_exc)
        {
            System.out.println("CLException! "+cl_exc);
        }
      */
        
        // The method simulates heat propagation in cellular automaton.
        if(nonParallelCalcHeatFlows)  
            nonParallelCalcHeatFlows();
        
        if(parallelMatrixMultiplication)
            parallelMatrixMultiplication();
    }
    
    /** The method creates two 1D-array and multiplies elements with the same 1st index.
     */
    public static void multiplyArrays1D()
    {
        // Number of elements in arrays of input and output data
        int n = 4000000;
        
        // Maximal number of elements in array
        int n_max = 10000000;
        
        // Arrays of input and output data
        float srcArrayA[] = new float[n];
        float srcArrayB[] = new float[n];
        float dstArray[]  = new float[n];
        
        for (int i=0; i<n; i++)
        {
            srcArrayA[i] = i+1;
            srcArrayB[i] = i+1;
        }
        
        // Pointers to arrays of input and output data
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcB = Pointer.to(srcArrayB);
        Pointer dst  = Pointer.to(dstArray);
        
        // The platform, device type and device number that will be used
        final int  platformIndex = 0;
        final long deviceType    = CL_DEVICE_TYPE_ALL;
        final int  deviceIndex   = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        
        // Obtain the list of platforms available
        clGetPlatformIDs(0, null, numPlatformsArray);
        
        // The number of platforms
        int numPlatforms = numPlatformsArray[0];
        
        // TEST
        System.out.println("The number of platforms: "+numPlatforms);

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        
        // Obtain the list of platforms available
        clGetPlatformIDs(platforms.length, platforms, null);
        
        // Obtain the platform from the list
        cl_platform_id platform = platforms[platformIndex];
        
        // TEST
        System.out.println("Platform ID: "+platform);

        // Initialize the context properties - Java port of cl_context_properties
        cl_context_properties contextProperties = new cl_context_properties();
        
        // Add the specified property to these properties
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        
        // Obtain the list of devices available on a platform
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        
        // The number of devices at platform
        int numDevices = numDevicesArray[0];        
        
        // TEST
        System.out.println("The number of devices at platform: "+numDevices);
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        
        // Obtain the list of devices available on a platform
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        
        // Obtain the device from the list
        cl_device_id device = devices[deviceIndex];
        
        // TEST
        System.out.println("Device ID: "+device);

        // Create a context for the selected device
        cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);
        
        // Create a command-queue for the selected device
        cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);

        // Allocate the memory objects for the input- and output data
        cl_mem memObjects[] = new cl_mem[3];
        
        // Creation of the memory objects
        memObjects[0] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcA, null);
        
        memObjects[1] = clCreateBuffer(context, 
            CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_float * n, srcB, null);
        
        memObjects[2] = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_float * n, null, null);
        
        // TEST
        System.out.println("memObjects[0]: "+memObjects[0].toString());
        System.out.println("memObjects[1]: "+memObjects[1].toString());
        System.out.println("memObjects[2]: "+memObjects[2].toString());
                
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program = clCreateProgramWithSource(context,
            1, new String[]{ programSource }, null, null);
        
        // TEST
        System.out.println("program: "+program.toString());
        
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);
        
     //   int[] errcode_ret = new int[1];
                
        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);
        
        // Used to set the argument value for a specific argument of a kernel.
        clSetKernelArg(kernel, 0, 
            Sizeof.cl_mem, Pointer.to(memObjects[0]));
        
        clSetKernelArg(kernel, 1, 
            Sizeof.cl_mem, Pointer.to(memObjects[1]));
        
        clSetKernelArg(kernel, 2, 
            Sizeof.cl_mem, Pointer.to(memObjects[2]));
        
        // TEST
        System.out.println("Size of CL memory: "+Sizeof.cl_mem);
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        long local_work_size[]  = new long[]{1};
        
        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
            global_work_size, local_work_size, 0, null, null);
        
        // Read the output data
        // Enqueue commands to read from a buffer object to host memory.
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
            n * Sizeof.cl_float, dst, 0, null, null);
        
        // TEST
        System.out.println("Size of buffer with results: "+n * Sizeof.cl_float);
        
        // Release kernel, program, and memory objects 
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        
        // Verify the result
        boolean passed = true;
        final float epsilon = 1e-7f;
        
        for (int i=0; i<n; i++)
        {
            float x = dstArray[i];
            float y = 3*(srcArrayA[i] + srcArrayB[i])*(srcArrayA[i] + srcArrayB[i]);
            
            boolean epsilonEqual = Math.abs(x - y) <= epsilon * Math.abs(x);
            if (!epsilonEqual)
            {
                passed = false;
                break;
            }
        }
        
        System.out.println("Test "+(passed?"PASSED":"FAILED"));
        
        System.out.println("Result:");
        
        if (n <= n_max)
        for (int i=0; i<n; i++)
        if((i+1)%1000 == 0)
        {            
            System.out.println("3*("+(i+1)+"+"+(i+1)+")^2 = "+dstArray[i]);
        }
    }
    
    /** The method creates 2D-array and multiplies elements with the same 1st index.
     */
    public static void multiplyArrays2D()
    {
        // Number of elements in arrays of input and output data
        int n     =  2048*2048; // 2000000;
        int m     =          2;
        
        // Maximal number of elements in array
        int n_max = 2048*2048;
        int m_max =         2;
        
        int step  = 2048*256; // 200000;
        
        // Arrays of input and output data
        float[][] srcArrayA = new float[m][n/2];
        float[][] srcArrayB = new float[m][n/2];
        float[]   dstArrayA = new float[n/2];
        float[]   dstArrayB = new float[n/2];
        
        for (int i=0; i<m; i++)
        {
            for (int j=0; j<n/2; j++)
            {
                srcArrayA[i][j] = (j+1)/step;
                srcArrayB[i][j] = (j+1 + n/2)/step;
            }
        }
        
        // Array of pointers to arrays of input and output data
        Pointer[] srcA = new Pointer[m];
        Pointer[] srcB = new Pointer[m];
        
        for(int point_index = 0; point_index < m; point_index++)
        {
            srcA[point_index] = Pointer.to(srcArrayA[point_index]);
            srcB[point_index] = Pointer.to(srcArrayB[point_index]);
        }
        
        Pointer dstA  = Pointer.to(dstArrayA);
        Pointer dstB  = Pointer.to(dstArrayB);
        
        // The platform, device type and device number that will be used
        final int  platformIndex = 0;
        final long deviceType    = CL_DEVICE_TYPE_ALL;
        final int  deviceIndex   = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        
        // Obtain the list of platforms available
        clGetPlatformIDs(0, null, numPlatformsArray);
        
        // The number of platforms
        int numPlatforms = numPlatformsArray[0];
        
        // TEST
        System.out.println("The number of platforms: "+numPlatforms);

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        
        // Obtain the list of platforms available
        clGetPlatformIDs(platforms.length, platforms, null);
        
        // Obtain the platform from the list
        cl_platform_id platform = platforms[platformIndex];
        
        // TEST
        System.out.println("Platform ID: "+platform);

        // Initialize the context properties - Java port of cl_context_properties
        cl_context_properties contextProperties = new cl_context_properties();
        
        // Add the specified property to these properties
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        
        // Obtain the list of devices available on a platform
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        
        // The number of devices at platform
        int numDevices = numDevicesArray[0];        
        
        // TEST
        System.out.println("The number of devices at platform: "+numDevices);
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        
        // Obtain the list of devices available on a platform
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        
        // Obtain the device from the list
        cl_device_id device = devices[deviceIndex];
        
        // TEST
        System.out.println("Device ID: "+device);

        // Create a context for the selected device
        cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);
        
        // Create a command-queue for the selected device
        cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);
        
        // Allocate the memory objects for the input- and output data
        cl_mem memObjectsA[] = new cl_mem[m];
        cl_mem memObjectsB[] = new cl_mem[m];
        cl_mem memObjectA    = new cl_mem();
        cl_mem memObjectB    = new cl_mem();
        
        for(int i=0; i<m; i++)
        {
            // Creation of the memory objects for input data
            memObjectsA[i] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                           Sizeof.cl_float * n/2, srcA[i], null);
            
            memObjectsB[i] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                           Sizeof.cl_float * n/2, srcB[i], null);
            
        //    System.out.println("memObjects["+i+"]: "+memObjectsA[i].toString());
        }
        
        // Creation of the memory objects for output data
        memObjectA = clCreateBuffer(context, CL_MEM_READ_WRITE, 
                                       Sizeof.cl_float * n/2, null, null);        
        
        memObjectB = clCreateBuffer(context, CL_MEM_READ_WRITE, 
                                       Sizeof.cl_float * n/2, null, null);      
        
        // TEST        
    //    System.out.println("memObjects["+m+"]: "+memObjectA.toString());
                
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program = clCreateProgramWithSource(context, 1, new String[]{ programSource }, null, null);
        
        // TEST
        System.out.println("program: "+program.toString());
        
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);
        
        // Create the kernels
        cl_kernel kernelA = clCreateKernel(program, "sampleKernel", null);
     //   cl_kernel kernelB = clCreateKernel(program, "sampleKernel", null);
        
        for(int i=0; i<m; i++)
        {
            // Used to set the argument value for a specific argument of a kernel.
            clSetKernelArg(kernelA, i, Sizeof.cl_mem, Pointer.to(memObjectsA[i]));
        //    clSetKernelArg(kernelB, i, Sizeof.cl_mem, Pointer.to(memObjectsB[i]));
        }
        
        clSetKernelArg(kernelA, m, Sizeof.cl_mem, Pointer.to(memObjectA));
    //    clSetKernelArg(kernelB, m, Sizeof.cl_mem, Pointer.to(memObjectB));
        
        // TEST
        System.out.println("Size of CL memory: "+Sizeof.cl_mem);
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{n/2};
        long local_work_size[]  = new long[]{512};
        
        long time_period=0;
        
        // Start time
        Date start_date = new Date();
        
        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernelA, 1, null, global_work_size, local_work_size, 0, null, null);
        
        // Finish time
        Date finish_date = new Date();
        
        time_period += finish_date.getTime() - start_date.getTime();
        
        System.out.println();
        System.out.println("The period of execution of the parallel code: " + time_period + " ms.");
                
        for(int i=0; i<m; i++)
        {
            clReleaseMemObject(memObjectsA[i]);
        }
        
        // Create the kernels
        kernelA = clCreateKernel(program, "sampleKernel", null);
     //   cl_kernel kernelB = clCreateKernel(program, "sampleKernel", null);
        
        for(int i=0; i<m; i++)
        {
            // Used to set the argument value for a specific argument of a kernel.
            clSetKernelArg(kernelA, i, Sizeof.cl_mem, Pointer.to(memObjectsB[i]));
        //    clSetKernelArg(kernelB, i, Sizeof.cl_mem, Pointer.to(memObjectsB[i]));
        }
        
        clSetKernelArg(kernelA, m, Sizeof.cl_mem, Pointer.to(memObjectB));
    //    clSetKernelArg(kernelB, m, Sizeof.cl_mem, Pointer.to(memObjectB));
        
        
        start_date = new Date();
        clEnqueueNDRangeKernel(commandQueue, kernelA, 1, null, global_work_size, local_work_size, 0, null, null);
        finish_date = new Date();        
        
        time_period += finish_date.getTime() - start_date.getTime();

        System.out.println();
        System.out.println("The period of execution of the parallel code: " + time_period + " ms.");
        
        // Read the output data
        // Enqueue commands to read from a buffer object to host memory.
        clEnqueueReadBuffer(commandQueue, memObjectA, CL_TRUE, 0,  Sizeof.cl_float*(n/2), dstA, 0, null, null);
        clEnqueueReadBuffer(commandQueue, memObjectB, CL_TRUE, 0,  Sizeof.cl_float*(n/2), dstB, 0, null, null);
        
        // TEST
        System.out.println("Size of buffer with results: "+(n/2)* Sizeof.cl_float);
        
        // Release kernel, program, and memory objects 
        for(int i=0; i<m; i++)
        {
        //    clReleaseMemObject(memObjectsA[i]);
            clReleaseMemObject(memObjectsB[i]);
        }
        
        clReleaseMemObject(memObjectA);
        clReleaseMemObject(memObjectB);
      //  clReleaseKernel(kernelA);
     //   clReleaseKernel(kernelB);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        
        // Verify the result
        boolean passed = true;
        final float epsilon = 1e-7f;
        
        float x, y;
        boolean epsilonEqual;
        
        for(int j=0; j<n/2; j++)
        {
            x = dstArrayA[j];
            y = 3*(srcArrayA[0][j]+srcArrayA[1][j])*(srcArrayA[0][j]+srcArrayA[1][j]);
            
            epsilonEqual = Math.abs(x - y) <= epsilon * Math.abs(x);
            
            if (!epsilonEqual)
            {
                passed = false;
                break;
            }
        }
        
        for(int j=0; j<n/2; j++)
        {
            x = dstArrayB[j];
            y = 3*(srcArrayB[0][j]+srcArrayB[1][j])*(srcArrayB[0][j]+srcArrayB[1][j]);
            
            epsilonEqual = Math.abs(x - y) <= epsilon * Math.abs(x);
            
            if (!epsilonEqual)
            {
                passed = false;
                break;
            }
        }
        
        System.out.println("Test "+(passed?"PASSED":"FAILED"));
        
        System.out.println("Result of the parallel code:");
        
        if(m <= m_max & n <= n_max)
        {
            for(int j=0; j<n/2; j++)
            if((j+1) % step == 0)
                System.out.println("(j+1)/"+step+" = "+(j+1)/step+"; result = "+dstArrayA[j]);
            
            for(int j=0; j<n/2; j++)
            if((j+1) % step == 0)           
                System.out.println("(j+1)/"+step+" = "+(j+1+n/2)/step+"; result = "+dstArrayB[j]);
        }
        
        // Start time
        start_date = new Date();
                
        // The same operations without OpenCL        
        for(int j=0; j<n/2; j++)
        {
            dstArrayA[j] = srcArrayA[0][j]*srcArrayA[0][j] + srcArrayA[0][j]*srcArrayA[1][j] + srcArrayA[0][j]*srcArrayA[1][j] + srcArrayA[1][j]*srcArrayA[1][j] +
                           srcArrayA[0][j]*srcArrayA[0][j] + srcArrayA[0][j]*srcArrayA[1][j] + srcArrayA[0][j]*srcArrayA[1][j] + srcArrayA[1][j]*srcArrayA[1][j] +
                           srcArrayA[0][j]*srcArrayA[0][j] + srcArrayA[0][j]*srcArrayA[1][j] + srcArrayA[0][j]*srcArrayA[1][j] + srcArrayA[1][j]*srcArrayA[1][j];
            
            dstArrayB[j] = srcArrayB[0][j]*srcArrayB[0][j] + srcArrayB[0][j]*srcArrayB[1][j] + srcArrayB[0][j]*srcArrayB[1][j] + srcArrayB[1][j]*srcArrayB[1][j] +
                           srcArrayB[0][j]*srcArrayB[0][j] + srcArrayB[0][j]*srcArrayB[1][j] + srcArrayB[0][j]*srcArrayB[1][j] + srcArrayB[1][j]*srcArrayB[1][j] +
                           srcArrayB[0][j]*srcArrayB[0][j] + srcArrayB[0][j]*srcArrayB[1][j] + srcArrayB[0][j]*srcArrayB[1][j] + srcArrayB[1][j]*srcArrayB[1][j];
        }
        
        // Finish time
        finish_date = new Date();
        
        time_period = finish_date.getTime() - start_date.getTime();
        
        System.out.println();
        System.out.println("The period of execution of the non-parallel code: " + time_period + " ms.");
        
        System.out.println("\n\nResult of the non-parallel code:");
        
        if(m <= m_max & n <= n_max)
        {
            for(int j=0; j<n/2; j++)
            if((j+1) % step == 0)            
                System.out.println("(j+1)/"+step+" = "+(j+1)/step+"; result = "+dstArrayA[j]);
            
            for(int j=0; j<n/2; j++)
            if((j+1) % step == 0)
                System.out.println("(j+1)/"+step+" = "+(j+1+n/2)/step+"; result = "+dstArrayB[j]);
        }
    }
    
    /** The method creates 1D-array of indices corresponding to 3D array of triple indices.
     */
    public static void calcSingleIndices()
    {
        // Number of dimensions
      //  int dim_number    = 3;
        
        // Number of cells along axes
    //    int cell_number_I = 10;
      //  int cell_number_J = 10;
      //  int cell_number_K = 10;
        
        int[] cell_number_I = new int[1]; cell_number_I[0] = 100;
        int[] cell_number_J = new int[1]; cell_number_J[0] = 100;
        int[] cell_number_K = new int[1]; cell_number_K[0] = 100;
        
        // Number of chosen cells
        int cell_number = 2048*2048;// 1024*2048;// 
        
        // Maximal number of chosen cells
     //   int max_cell_number = 10000;
        
        // Array of cell indices i, j, k
        int[] indices_I = new int[cell_number];
        int[] indices_J = new int[cell_number];
        int[] indices_K = new int[cell_number];
        
        // Array of cell 1D indices
        int[] indices1D = new int[cell_number];

        for (int cell_index = 0; cell_index < cell_number; cell_index++)        
        {
            indices_I[cell_index] = (int)Math.floor(cell_number_I[0]*Math.random() - 1);
            indices_J[cell_index] = (int)Math.floor(cell_number_J[0]*Math.random() - 1);
            indices_K[cell_index] = (int)Math.floor(cell_number_K[0]*Math.random() - 1);
            
            if(indices_I[cell_index] < 0) indices_I[cell_index] = 0;
            if(indices_J[cell_index] < 0) indices_J[cell_index] = 0;
            if(indices_K[cell_index] < 0) indices_K[cell_index] = 0;
        }
        
        // Pointers to cell indices
        Pointer index_I_ptr = new Pointer();
        Pointer index_J_ptr = new Pointer();
        Pointer index_K_ptr = new Pointer();
        Pointer cell_number_I_ptr = new Pointer();
        Pointer cell_number_J_ptr = new Pointer();
        
        // Array of pointers to cell 1D indices
        Pointer index_1D_ptr  = Pointer.to(indices1D);        
        
        index_I_ptr = Pointer.to(indices_I);
        index_J_ptr = Pointer.to(indices_J);
        index_K_ptr = Pointer.to(indices_K);
        cell_number_I_ptr = Pointer.to(cell_number_I);
        cell_number_J_ptr = Pointer.to(cell_number_J);
        
        // The platform, device type and device number that will be used
        final int  platformIndex = 0;
        final long deviceType    = CL_DEVICE_TYPE_ALL;
        final int  deviceIndex   = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        
        // Obtain the list of platforms available
        clGetPlatformIDs(0, null, numPlatformsArray);
        
        // The number of platforms
        int numPlatforms = numPlatformsArray[0];
        
        // TEST
        System.out.println("The number of platforms: "+numPlatforms);

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        
        // Obtain the list of platforms available
        clGetPlatformIDs(platforms.length, platforms, null);
        
        // Obtain the platform from the list
        cl_platform_id platform = platforms[platformIndex];
        
        // TEST
        System.out.println("Platform ID: "+platform);

        // Initialize the context properties - Java port of cl_context_properties
        cl_context_properties contextProperties = new cl_context_properties();
        
        // Add the specified property to these properties
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        
        // Obtain the list of devices available on a platform
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        
        // The number of devices at platform
        int numDevices = numDevicesArray[0];        
        
        // TEST
        System.out.println("The number of devices at platform: "+numDevices);
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        
        // Obtain the list of devices available on a platform
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        
        // Obtain the device from the list
        cl_device_id device = devices[deviceIndex];
        
        // TEST
        System.out.println("Device ID: "+device);

        // Create a context for the selected device
        cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);
        
        // Create a command-queue for the selected device
        cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);
        
        
        String prog_string = "__kernel void "+
        "sampleKernel(__global const int *i, __global const int *j, __global const int *k,"+
        "             __global int *n)"+
        "{"+
        "    int gid = get_global_id(0);"+
        "    n[gid] = "+cell_number_I[0]+"*"+cell_number_J[0]+"*k[gid] + "+cell_number_I[0]+"*j[gid] + i[gid];"+
        "}";   
        /*
        /// Allocate the memory objects for the input- and output data        
        // Creation of the memory objects
        cl_mem memObject_indices_I = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_I_ptr, null);
        
        cl_mem memObject_size_I    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int,               cell_number_I_ptr, null);
         
        cl_mem memObject_indices_J = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_J_ptr, null);
        
        cl_mem memObject_size_J    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int,               cell_number_J_ptr, null);
        
        cl_mem memObject_indices_K = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_K_ptr, null);
        
        cl_mem memObject_indices_1D = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_1D_ptr, null);
               
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program = clCreateProgramWithSource(context, 1, new String[]{ programSource1 }, null, null);
        */
        
        // Allocate the memory objects for the input- and output data        
        // Creation of the memory objects
        cl_mem memObject_indices_I = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_I_ptr, null);
         
        cl_mem memObject_indices_J = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_J_ptr, null);
        
        cl_mem memObject_indices_K = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_K_ptr, null);
        
        cl_mem memObject_indices_1D = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * cell_number, index_1D_ptr, null);
               
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program = clCreateProgramWithSource(context, 1, new String[]{ prog_string }, null, null);
        
   //     cl_program program = clCreateProgramWithSource(context, 1, new String[]{ prog_string }, null, null);
        
        // TEST
        System.out.println("program: "+program.toString());
        
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);
        
        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);
        
        // Used to set the argument value for a specific argument of a kernel.
        clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(memObject_indices_I));
   //     clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(memObject_size_I));
        clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(memObject_indices_J));
    //    clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(memObject_size_J));
        clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(memObject_indices_K));
        clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(memObject_indices_1D));
        
        // TEST
        System.out.println("Size of CL memory: "+Sizeof.cl_mem);
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{cell_number};
        long local_work_size[]  = new long[]{512};
        
        Date start_date = new Date();
        
        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                               global_work_size, local_work_size, 0, null, null);
        
        Date finish_date = new Date();
        
        long time_period = finish_date.getTime()- start_date.getTime();
        
        // Read the output data
        // Enqueue commands to read from a buffer object to host memory.
        clEnqueueReadBuffer(commandQueue, memObject_indices_1D, CL_TRUE, 0,
                            cell_number*Sizeof.cl_int, index_1D_ptr, 0, null, null);
        
        // TEST
        System.out.println("Size of buffer with results: "+cell_number * Sizeof.cl_int);
        
        // Release kernel, program, and memory objects 
        clReleaseMemObject(memObject_indices_I);
 //       clReleaseMemObject(memObject_size_I);
        clReleaseMemObject(memObject_indices_J);
   //     clReleaseMemObject(memObject_size_J);
        clReleaseMemObject(memObject_indices_K);
        clReleaseMemObject(memObject_indices_1D);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);        
        
        System.out.println();
        System.out.println("Result of the parallel code:");
        
        for (int cell_index = 0; cell_index < cell_number; cell_index++)
        if((cell_index+1) % (cell_number/16) == 0)
        {            
            System.out.println("cell # "+(cell_index+1)+"; indices = ["+indices_I[cell_index]+", "+indices_J[cell_index]+
                                    ", "+indices_K[cell_index]+"]; index_1D = "+indices1D[cell_index]);
        }
        
        System.out.println();
        System.out.println("The period of execution of the parallel code: " + time_period + " ms.");
        
        // Start date
        start_date = new Date();
        
        // Calculation by the non-parallel code
        for(int cell_index = 0; cell_index < cell_number; cell_index++)
        {
            indices1D[cell_index] = cell_number_I[0]*cell_number_J[0]*indices_K[cell_index]+
                                    cell_number_I[0]*indices_J[cell_index]+indices_I[cell_index];// +
                         //         cell_number_I[0]*cell_number_J[0]*indices_K[cell_index]+
                        //          cell_number_I[0]*indices_J[cell_index]+indices_I[cell_index] +
                      //          cell_number_I[0]*cell_number_J[0]*indices_K[cell_index]+
                      //          cell_number_I[0]*indices_J[cell_index]+indices_I[cell_index] +
                      //        cell_number_I[0]*cell_number_J[0]*indices_K[cell_index]+
                       //       cell_number_I[0]*indices_J[cell_index]+indices_I[cell_index];
            
        //    indices1D[cell_index] = indices1D[cell_index]/4;
        }
        
        // Finish date
        finish_date = new Date();
        
        time_period = finish_date.getTime() - start_date.getTime();
        
        System.out.println();
        System.out.println("Result of the non-parallel code:");
        
        for(int cell_index = 0; cell_index < cell_number; cell_index++)
        if((cell_index+1) % (cell_number/16) == 0)
        {            
            System.out.println("cell # "+(cell_index+1)+"; indices = ["+indices_I[cell_index]+", "+indices_J[cell_index]+
                                    ", "+indices_K[cell_index]+"]; index_1D = "+indices1D[cell_index]);
        }
        
        // TEST
        System.out.println();
        System.out.println("The period of execution of the non-parallel code: " + time_period + " ms.");
    }
    
    /** The method simulates heat propagation in cellular automaton.
     */
    public static void parallelCalcHeatFlows()
    {
        // Total number of cells
        int cell_number = CELL_NUMBER_I*CELL_NUMBER_J*CELL_NUMBER_K;
                
        // Array of cell single indices i, j, k
        int[] indices_I = new int[cell_number];
        int[] indices_J = new int[cell_number];
        int[] indices_K = new int[cell_number];
        
        // Array of cell indices i, j, k
     //   int[] ind_I = new int[CELL_NUMBER_I];
     //   int[] ind_J = new int[CELL_NUMBER_J];
     //   int[] ind_K = new int[CELL_NUMBER_K];
        
        // Array of cell location types
        byte[] loc_types = new byte[cell_number];
        
        // Array of cell energy types
        byte[] en_types  = new byte[cell_number];
        
        // Single indices of neighbours of all cells
        neighbours1S = new int[neighb1S_number][cell_number];
        
        // Cell surface calculation
        double cell_surf   = calcCellSurface(packing_type);
        
        // Cell volume calculation
        double cell_volume = calcCellVolume(packing_type);        
        
        // Index of cell neighbour
        int neighb_id[]     = new int[1]; 
            neighb_id[0]    = -1;
        
        // Variable for testing
        int step_counter[]  = new int[6]; 
            step_counter[0] = step_counter[1] = step_counter[2] = -1;
            step_counter[3] = step_counter[4] = step_counter[5] = -1;
        
        // Factor necessary for calculation of heat influx
        float factor[] = new float[1];        
        factor[0] = (float)cell_surf*(float)TIME_STEP/((float)CELL_SIZE*neighb1S_number*(float)cell_volume);
        
        // Array of initial temperatures of cells
        float init_temprs[]     = new float[cell_number];
        
        // Array of heat capacities of cells
        float heat_caps[]       = new float[cell_number];
        
        // Array of heat conductivities of cells
        float heat_conds[]      = new float[cell_number];
        
        // Array of heat expansion coefficients of cells
    //  float heat_exp_coeffs[] = new float[cell_number];  
    
        // Array of densities of cells
        float densities[]       = new float[cell_number];
        
        // Array of temperatures of cells
        float temperatures[]    = new float[cell_number];
        
        // Array of heat influxes to cells from neighbours
        float heat_influxes[]   = new float[cell_number];
        
        System.out.println("cell size:    " + CELL_SIZE);
        System.out.println("cell surface: " + cell_surf);
        System.out.println("cell volume:  " + cell_volume);
        
        int cell_index_1D = -1;
        
        for (int cell_index_K = 0; cell_index_K < CELL_NUMBER_K; cell_index_K++)
        for (int cell_index_J = 0; cell_index_J < CELL_NUMBER_J; cell_index_J++)
        for (int cell_index_I = 0; cell_index_I < CELL_NUMBER_I; cell_index_I++)
        {
            cell_index_1D++;
            
            // Setting of indices of neighbours at 1st coordination sphere
            setNeighbours1S(cell_index_1D);
            
            // Types of location of cells
            loc_types[cell_index_1D]   = INTERNAL_CELL;
            
            // Types of energy interaction of cells with neighbours
            en_types[cell_index_1D]    = INTERNAL_INTERACTED_CELL;
            
            // Initial temperatures of cells
            init_temprs[cell_index_1D] = INITIAL_TEMPERATURE;
            
            // Top facet
            if(cell_index_K == 0)
            {
              init_temprs[cell_index_1D] = BOUNDARY_TEMPERATURE;
              loc_types[cell_index_1D]   = BOUNDARY_CELL;
              en_types[cell_index_1D]    = BOUNDARY_INTERACTED_CELL;
            }
            
            // Bottom facet
            if(cell_index_K == CELL_NUMBER_K-1)
            {
              init_temprs[cell_index_1D] = INITIAL_TEMPERATURE;
              loc_types[cell_index_1D]   = BOUNDARY_CELL;
              en_types[cell_index_1D]    = BOUNDARY_INTERACTED_CELL;
            }
            
            // Left, right, front and back facets
            if(cell_index_I == 0 | cell_index_I == CELL_NUMBER_I-1 |
               cell_index_J == 0 | cell_index_J == CELL_NUMBER_J-1)
            {
                loc_types[cell_index_1D] = BOUNDARY_CELL;
                en_types[cell_index_1D]  = BOUNDARY_ADIABATIC_CELL;
            }
            
            // Values of heat capacities of cells
            heat_caps[cell_index_1D]       = (float)HEAT_CAPACITY;
            
            // Values of heat conductivities of cells
            heat_conds[cell_index_1D]      = (float)HEAT_CONDUCTIVITY;
            
            // Values of heat expansion coefficients of cells
       //   heat_exp_coeffs[cell_index_1D] = (float)HEAT_EXPANSION_COEFF;
       
            // Values of densities of cells
            densities[cell_index_1D]       = (float)DENSITY;
            
            // 1st, 2nd and 3rd indices of cell with current single index
            indices_I[cell_index_1D]       = cell_index_I;
            indices_J[cell_index_1D]       = cell_index_J;
            indices_K[cell_index_1D]       = cell_index_K;
        }        
        
        // Pointers to arrays of cell parameters
        Pointer init_temprs_ptr     = Pointer.to(init_temprs);        
        Pointer heat_caps_ptr       = Pointer.to(heat_caps);
        Pointer heat_conds_ptr      = Pointer.to(heat_conds);        
        Pointer densities_ptr       = Pointer.to(densities);
        Pointer en_types_ptr        = Pointer.to(en_types);
        Pointer[] neighbours1S_ptr  = new Pointer[neighb1S_number];
        
        // Arrays of neighbour cells with corresponding index for all cells from specimen
        for (int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
            neighbours1S_ptr[neighb_counter] = Pointer.to(neighbours1S[neighb_counter]);
        
        Pointer temperatures_ptr    = Pointer.to(temperatures);
        Pointer heat_influxes_ptr   = Pointer.to(heat_influxes);
        Pointer neighb_id_ptr       = Pointer.to(neighb_id);
        Pointer factor_ptr          = Pointer.to(factor);
        Pointer step_counter_ptr    = Pointer.to(step_counter);
    //    Pointer heat_exp_coeffs_ptr = Pointer.to(heat_exp_coeffs);
        
        // The platform, device type and device number that will be used
        final int  platformIndex = 0;
        final long deviceType    = CL_DEVICE_TYPE_ALL;
        final int  deviceIndex   = 0;
        
        // Enable exceptions and subsequently omit error checks in this sample
        setExceptionsEnabled(true);
        
        // The number of platforms
        int numPlatforms = 0;

        // The number of platforms is obtained
        int numPlatformsArray[] = new int[1];
      
        // The list of available platforms is obtained
        clGetPlatformIDs(0, null, numPlatformsArray);
        
        // The number of platforms
        numPlatforms = numPlatformsArray[0];

        // TEST
        for(int counter = 0; counter < numPlatformsArray.length; counter++)
          if(numPlatformsArray[counter]>0)
            System.out.println("The number of platforms: numPlatformsArray["+counter+"] = "+numPlatformsArray[counter]);
        
        // Platform ID is obtained
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        
        // IDs of available platforms are obtained
        clGetPlatformIDs(platforms.length, platforms, null);
        
        // The platform from the list is obtained
        cl_platform_id platform = platforms[platformIndex];
        
        // TEST
        System.out.println("Platform ID: "+platform);

        // The context properties (Java port of cl_context_properties) are initialized
        cl_context_properties contextProperties = new cl_context_properties();
        
        // Addition of the specified property to these properties
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Array of devices on the platform
        int numDevicesArray[] = new int[100];
        
        // The list of devices available on a platform is obtained
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        
        // The number of devices at platform
        int numDevices = numDevicesArray[0];
        
        // TEST
        for(int counter = 0; counter < numDevicesArray.length; counter++)
            if(numDevicesArray[counter]>0)
                System.out.println("The number of devices at platform: numDevicesArray["+counter+"] = "+numDevicesArray[counter]);
        
        // Array of device IDs
        cl_device_id devices[] = new cl_device_id[numDevices];
        
        // The array of device IDs available on platform is obtained
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        
        // Device ID
        cl_device_id device;// = devices[deviceIndex];
        
        // Obtaining of the list of devices
        for(int device_index = 0; device_index < numDevicesArray[0]; device_index++)
        {
            // The device ID is obtained from the list
            device = devices[device_index];
        
            // TEST
            System.out.println("Device ID: "+device);
        }
        
        System.out.println("size_t = "+Sizeof.size_t);
 //       System.out.println("CL_PROGRAM_NUM_KERNELS: "+CL_PROGRAM_NUM_KERNELS);

        // Creation of the context for the selected devices
    //    cl_context context = clCreateContext(contextProperties, numDevices, new cl_device_id[]{device}, null, null, null);
        cl_context context = clCreateContext(contextProperties, numDevices, devices, null, null, null);
        
        // Creation of the command queue for the selected device
     //   cl_command_queue commandQueue = clCreateCommandQueue(context, devices[0], 0, null);
        cl_command_queue commandQueue = clCreateCommandQueue(context, devices[0], 0, null);
        
        // Calculation of heat influx to the "central" cell 
        // from neighbouring cells
     // heatInflux = heatInflux + (1.0/24)*(cell_surface/get_cell_size_X())*(heatConds[cellCounter]+heatCond)*
     //                         (temprs[cellCounter]-oldTemperature)*time_step;
        
        // Calculation of new temperature of the "central" cell
     // newTemperature = oldTemperature + heatInflux/(heatCap*cell.getVolume()*cell.get_density());
        
        // The number of cells
    //    int paral_cell_number = (int)cell_number;
        
        // The number of 
    //    int spec_part_number  = 1;
        /*
        if(cell_number >= (int)LOCAL_WORK_SIZE)//GLOBAL_WORK_SIZE)
        {
            paral_cell_number = (int)LOCAL_WORK_SIZE;//GLOBAL_WORK_SIZE;
            spec_part_number  = cell_number/paral_cell_number;
            
            if(cell_number % paral_cell_number > 0)
                spec_part_number++;
        }
        */
        
        // TEST
        System.out.println("\n--------============-------------===============---------------");
        
        // Information about the devices is obtained
        for(int dev_counter = 0; dev_counter<numDevices; dev_counter++)
        {
          int numValues = 2;
          int[] values = new int[numValues];
          
          System.out.println("Device #"+dev_counter);
          
          // Obtain the device from the list
          device = devices[dev_counter];
          System.out.println("Device ID: "+device);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_MAX_COMPUTE_UNITS, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("The number of parallel compute cores on the device:                     "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_MAX_WORK_GROUP_SIZE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Maximal number of work-items in a work-group executing a kernel:        "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Maximal number of dimensions for global and local work-item IDs:        "+values[0]);
          
          numValues *= values[0];
          values    = new int[numValues];
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_MAX_WORK_ITEM_SIZES, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.print  ("Maximal number of work-items specified in each dimension of work-group: ");
          
          for(int val_counter = 0; val_counter < numValues; val_counter++)
              System.out.print(values[val_counter]+" ");
          
          System.out.println();
          
          numValues = 2;
          values    = new int[numValues];
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_GLOBAL_MEM_SIZE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Size of global device memory in bytes:      "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_GLOBAL_MEM_CACHE_TYPE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          
          if(values[0] == CL_NONE)
          System.out.println("Type of global memory cache:                CL_NONE");
          
          if(values[0] == CL_READ_ONLY_CACHE)
          System.out.println("Type of global memory cache:                CL_READ_ONLY_CACHE");
          
          if(values[0] == CL_READ_WRITE_CACHE)
          System.out.println("Type of global memory cache:                CL_READ_WRITE_CACHE");
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Size of global memory cache line in bytes:  "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_GLOBAL_MEM_CACHE_SIZE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Size of global memory cache in bytes:       "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_LOCAL_MEM_TYPE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          
          if(values[0] == CL_LOCAL)
          System.out.println("Type of local memory:                       CL_LOCAL");
          
          if(values[0] == CL_GLOBAL)
          System.out.println("Type of local memory:                       CL_GLOBAL");
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_LOCAL_MEM_SIZE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Size of local memory in bytes:                         "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Maximal size of a constant buffer allocation in bytes: "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_MAX_MEM_ALLOC_SIZE, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Maximal size of memory object allocation in bytes:     "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_ADDRESS_BITS, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("The default compute device address space size in bits: "+values[0]);
          
          clGetDeviceInfo(devices[dev_counter], CL_DEVICE_MAX_CONSTANT_ARGS, Sizeof.cl_int*numValues, Pointer.to(values), null);
          System.out.println("Maximal number of arguments declared with a __constant qualifier in a kernel: "+values[0]);
          
          System.out.println("\n--------============-------------===============---------------");
        }
        
        // Special ratios for calculation of heat influxes
        double ratio   = 1.0/((INTERNAL_INTERACTED_CELL - BOUNDARY_INTERACTED_CELL)*(INTERNAL_INTERACTED_CELL - BOUNDARY_ADIABATIC_CELL));
        byte ratio_2   = BOUNDARY_ADIABATIC_CELL - BOUNDARY_INTERACTED_CELL;
        
        // Step number
        int step_number = STEP_NUMBER;
        
        System.out.println("Number of cells:                        "+cell_number);
      //  System.out.println("Number of specimen parts (cell groups): "+spec_part_number);
        
        // Program text
        String prog_string = "__kernel void sampleKernel\n"+
                // Kernel variables are: 1. array of initial temperatures of cells; 2. array of heat capacities of cells;
                // 3. array of heat conductivities of cells; 4. array of densities of cells;
                // 5-16. arrays of indices of neighbour cells (12 for each cell) for all cells;
                // 17. array of energy types for all cells; 18. array of calculated temperatures of cells;
                // 19. array of heat influxes to cells; 20. index of current neighbour cell
                // 21. the value for calculation of cell temperature; 22. the value for testing.
        "(            __global       float *init_temprs,     __global const float *heat_cap,       \n"+
        "             __global const float *heat_cond,       __global const float *densities,      \n"+ 
        "             __global const int   *neighbours1S_0,  __global const int   *neighbours1S_1, \n"+
        "             __global const int   *neighbours1S_2,  __global const int   *neighbours1S_3, \n"+
        "             __global const int   *neighbours1S_4,  __global const int   *neighbours1S_5, \n"+
        "             __global const int   *neighbours1S_6,  __global const int   *neighbours1S_7, \n"+
        "             __global const int   *neighbours1S_8,  __global const int   *neighbours1S_9, \n"+
        "             __global const int   *neighbours1S_10, __global const int   *neighbours1S_11,\n"+
        "             __global const char  *en_types,        __global float       *temperatures,   \n"+
        "             __global float       *heat_influxes,   __global int         *neighb_id,      \n"+
        "             __global float       *factor,          __global int         *step_counter)\n"+
        "{   \n"+
                // Index of current cell
        "    int cell_index;\n"+
        "    cell_index = get_global_id(0);// get_group_id(0)*get_local_size(0) + get_local_id(0);// \n"+
    //  "    cell_index = get_global_size(0)*get_global_size(1)*get_global_id(2)+ \n"+
    //  "                 get_global_size(0)*get_global_id(1) + get_global_id(0);   \n"+
        "    \n"+
                // Calculation of heat influx from neighbour cells to current cell
        "    heat_influxes[cell_index]=       ((en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_0[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_0[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_1[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_1[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_2[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_2[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_3[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_3[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_4[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_4[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_5[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_5[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_6[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_6[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_7[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_7[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_8[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_8[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_9[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_9[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_10[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_10[cell_index]])/(char)"+ratio_2+")+\n"+
        "        \n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_ADIABATIC_CELL+")*\n"+
        "                                      (en_types[cell_index] - (char)"+BOUNDARY_INTERACTED_CELL+")*(float)"+ratio+"*\n"+
        "                                      (init_temprs[neighbours1S_11[cell_index]] - init_temprs[cell_index])*\n"+
        "                                      (((char)"+BOUNDARY_ADIABATIC_CELL+" - en_types[neighbours1S_11[cell_index]])/(char)"+ratio_2+"));\n"+
        "    __syncthreads();\n"+
        "    \n"+
                // Calculation of new temperature of cell according to heat influx
        "    temperatures[cell_index] = init_temprs[cell_index] + heat_influxes[cell_index] * heat_cond[cell_index] * factor[0]/(heat_cap[cell_index] * densities[cell_index]);\n"+
        "    __syncthreads();\n"+
        "    \n"+
                // New temperature value becomes the initial value at next time step
        "    init_temprs[cell_index] = temperatures[cell_index];\n"+
        "    __syncthreads();\n"+
        "    \n"+
        "    step_counter[0] = get_global_size(0);\n"+
        "    step_counter[1] = get_global_size(1);\n"+
        "    step_counter[2] = get_global_size(2);\n"+
        "    step_counter[3] = get_num_groups(0);// get_local_size(0);// \n"+
        "    step_counter[4] = get_num_groups(1);// get_local_size(1);// \n"+
        "    step_counter[5] = get_num_groups(2);// get_local_size(2);// \n"+
        "}";
        
        try
        {
       //   String file_name = "/home/mdd/Work_MPV/Java_programs/Projects/SECAM_JOCL_Project/build/classes/task_db/paral_code.txt";
            String file_name   = "./task_db/paral_code.txt";
            BufferedWriter bw_prog  = new BufferedWriter(new FileWriter(file_name));
            bw_prog.write(prog_string);
            bw_prog.flush();
            bw_prog.close();
            
            System.out.println("Code is written to file "+file_name);
        }
        catch(IOException io_exc)
        {
            System.out.println("IOException: "+io_exc);
        }
        
        Date date_0 = new Date();
        
        int total_mem_size = 0;
        
        // Allocate the memory objects for the input- and output data        
        // Creation of the memory objects for arrays of initial temperatures, heat capacities, 
        // heat conductivities and densities of cells
        cl_mem memObject_init_temprs    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_float * cell_number, init_temprs_ptr, null);
         
        cl_mem memObject_heat_caps      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_float * cell_number, heat_caps_ptr, null);
        
        cl_mem memObject_heat_conds     = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_float * cell_number, heat_conds_ptr, null);
        
        cl_mem memObject_densities      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_float * cell_number, densities_ptr, null);
        
        total_mem_size += 4*Sizeof.cl_float * cell_number;
        
        // Creation of the memory object for indices of neighbour cells
        cl_mem[] memObject_neighbours1S = new cl_mem[neighb1S_number];
        
        for (int point_counter = 0; point_counter<neighb1S_number; point_counter++)
            memObject_neighbours1S[point_counter]  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int*cell_number, neighbours1S_ptr[point_counter], null);
        
        total_mem_size += neighb1S_number*Sizeof.cl_int * cell_number;
        
        // Creation of the memory object for energy types of cells
        cl_mem memObject_en_types       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * cell_number, en_types_ptr, null);
        
        total_mem_size += Sizeof.cl_char * cell_number;
        
        // Creation of the memory object for temperatures of cells
        cl_mem memObject_temperatures   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_float * cell_number, temperatures_ptr, null);
        
        // Creation of the memory object for heat influxes to cells
        cl_mem memObject_heat_influxes  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_float * cell_number, heat_influxes_ptr, null);
        
        total_mem_size += 2*Sizeof.cl_float*cell_number;
        
        // Creation of the memory object for index of current neighbour cell
        cl_mem memObject_neighb_id      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int, neighb_id_ptr, null);
        
        // Creation of the memory object for the value necessary for calculation of cell temperature
        cl_mem memObject_factor         = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_float, factor_ptr, null);
        
        // Creation of the memory object for test value
        cl_mem memObject_step_counter   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    6*Sizeof.cl_int, step_counter_ptr, null);
        
        total_mem_size += Sizeof.cl_float + 7*Sizeof.cl_int;
        
        // TEST
        System.out.println();
        
        Date date_1 = new Date();
        System.out.println("Period of creation of memory objects:  "+(date_1.getTime() - date_0.getTime())+" ms.");
        
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program  = clCreateProgramWithSource(context, 1, new String[]{prog_string},  new long[]{8192}, null);
        
        Date date_1a = new Date();
        System.out.println("Period of creation of program:         "+(date_1a.getTime() - date_1.getTime())+" ms.");
        
        // Building of the program
        clBuildProgram(program, 0, null, null, null, null);
        
        Date date_1b = new Date();
        System.out.println("Period of building of program:         "+(date_1b.getTime() - date_1a.getTime())+" ms.");
        
        // Creation of the kernel
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);
        
        Date date_2 = new Date();
        System.out.println("Period of creation of kernel:          "+(date_2.getTime() - date_1b.getTime())+" ms.");
        
        System.out.println("Size of a native pointer, in bytes:    "+Sizeof.cl_mem);
        
        // Setting of the argument values to the kernel.
        clSetKernelArg(kernel,  0, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
        clSetKernelArg(kernel,  1, Sizeof.cl_mem, Pointer.to(memObject_heat_caps));
        clSetKernelArg(kernel,  2, Sizeof.cl_mem, Pointer.to(memObject_heat_conds));
        clSetKernelArg(kernel,  3, Sizeof.cl_mem, Pointer.to(memObject_densities));
        
        clSetKernelArg(kernel,  4, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[0]));
        clSetKernelArg(kernel,  5, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[1]));
        clSetKernelArg(kernel,  6, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[2]));
        clSetKernelArg(kernel,  7, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[3]));
        
        clSetKernelArg(kernel,  8, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[4]));
        clSetKernelArg(kernel,  9, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[5]));
        clSetKernelArg(kernel, 10, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[6]));
        clSetKernelArg(kernel, 11, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[7]));
        
        clSetKernelArg(kernel, 12, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[8]));
        clSetKernelArg(kernel, 13, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[9]));
        clSetKernelArg(kernel, 14, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[10]));
        clSetKernelArg(kernel, 15, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[11]));
        
        clSetKernelArg(kernel, 16, Sizeof.cl_mem, Pointer.to(memObject_en_types));
        clSetKernelArg(kernel, 17, Sizeof.cl_mem, Pointer.to(memObject_temperatures));
        clSetKernelArg(kernel, 18, Sizeof.cl_mem, Pointer.to(memObject_heat_influxes));
        
        clSetKernelArg(kernel, 19, Sizeof.cl_mem, Pointer.to(memObject_neighb_id));
        clSetKernelArg(kernel, 20, Sizeof.cl_mem, Pointer.to(memObject_factor));
        clSetKernelArg(kernel, 21, Sizeof.cl_mem, Pointer.to(memObject_step_counter));
        
        Date date_3 = new Date();
        
        System.out.println("Period of setting of kernel arguments: "+(date_3.getTime() - date_2.getTime())+" ms.");
        
        // TEST
    //    System.out.println("Size of CL memory:    "+Sizeof.cl_mem);
        
        // Setting of the work-item sizes at each dimension
    //  long global_work_size[] = new long[]{GLOBAL_WORK_SIZE};
    //  long global_work_size[] = new long[]{GLOBAL_WORK_SIZE, 1, 1};
    //  long local_work_size[]  = new long[]{LOCAL_WORK_SIZE, 1, 1};
        
    //    long global_work_size[] = new long[]{CELL_NUMBER_I, CELL_NUMBER_J, CELL_NUMBER_K};
    //  long global_work_size[] = new long[]{LOCAL_CELL_NUMBER_I, LOCAL_CELL_NUMBER_J, LOCAL_CELL_NUMBER_K};
    //    long local_work_size[]  = new long[]{LOCAL_CELL_NUMBER_I, LOCAL_CELL_NUMBER_J, LOCAL_CELL_NUMBER_K};
        
        long global_work_size[] = new long[]{CELL_NUMBER_I*CELL_NUMBER_J*CELL_NUMBER_K, 1, 1};
        long local_work_size[]  = new long[]{LOCAL_CELL_NUMBER_I*LOCAL_CELL_NUMBER_J*LOCAL_CELL_NUMBER_K, 1, 1};
        
    //  long global_work_size[] = new long[]{GLOBAL_WORK_SIZE, 1024, 64};
    //  long local_work_size[]  = new long[]{LOCAL_WORK_SIZE, 4, 2};
        
        // TEST
        System.out.println();
        System.out.println("Number of time steps: "+step_number);
        
        Date start_date = new Date();
        Date inter_date = new Date();
        Date inter_date_1 = new Date();

        long time_period_exec = 0;
        long time_period_read = 0;
        
        // Current cell coordinates
        double[] coords = new double[3];
        
        // Current cell grain index
        int grain_index = 1;
        
        // Boolean value regulating output data recording
        boolean write_to_file; 
        
        BufferedWriter bw;
        
        // Number of files with output data
        int file_number = 1;
        
        // Execution of the kernel at each time step
        for(int stepCounter = 1; stepCounter <= step_number; stepCounter++)
        {
            start_date = new Date();
            
            // Execution of the kernel at current time step
            clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size, local_work_size, 0, null, null);
            
            inter_date = new Date();
            
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            write_to_file = (stepCounter%(step_number/file_number) == 0)|(stepCounter == step_number);
            
            if(write_to_file)
            {
                inter_date = new Date();
                
                // Reading of the output data (temperatures and heat influxes) from a buffer object to host memory.
                clEnqueueReadBuffer(commandQueue, memObject_temperatures, CL_TRUE, 0,
                            Sizeof.cl_float * cell_number, temperatures_ptr, 0, null, null);
        
                clEnqueueReadBuffer(commandQueue, memObject_heat_influxes, CL_TRUE, 0,
                            Sizeof.cl_float * cell_number, heat_influxes_ptr, 0, null, null);
                
                inter_date_1 = new Date();
                time_period_read += inter_date_1.getTime()- inter_date.getTime();
                
                // Recording of output data to the file
                try
                {
                    bw = new BufferedWriter(new FileWriter("./task_db/paral_heating/paral_heating_"+stepCounter+".res"));
                    
                    if(write_to_file)
                    for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
                    {
                        // Calculation of coordinates of current cell
                        coords = calcCoordinates(indices_I[cell_counter], indices_J[cell_counter], indices_K[cell_counter]);
                        
                        bw.write(en_types[cell_counter]+" "+loc_types[cell_counter]+" "+grain_index+" "+
                                 coords[0]+" "+coords[1]+" "+coords[2]+" "+temperatures[cell_counter]+" 0 0 0 0 0 0 0 0\n");
                        bw.flush();
                    }
                    
                    bw.close();
            
                    System.out.println();
                    System.out.println("File ./task_db/paral_heating/paral_heating_"+stepCounter+".res  is created.");
                    
                    if(stepCounter == step_number)
                    for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
                    {
                      if(indices_I[cell_counter] == CELL_NUMBER_I/2)
                      if(indices_J[cell_counter] == CELL_NUMBER_J/2)
                      {
                          System.out.println("Cell # "+cell_counter+"; ["+indices_I[cell_counter]+
                                             ", "+indices_J[cell_counter]+", "+indices_K[cell_counter]+"]; type = "+en_types[cell_counter]+
                                             "; temperature = "+temperatures[cell_counter]+"; heat influx = "+heat_influxes[cell_counter]);
                      }
                    }
                }
                catch(IOException io_exc)
                {
                    System.out.println("IOException: " + io_exc);
                }
            }
        }
        
        inter_date = new Date();
        
        // Reading of the output data
        // Enqueued commands are read from a buffer object to host memory.
        clEnqueueReadBuffer(commandQueue, memObject_step_counter, CL_TRUE, 0, 6*Sizeof.cl_int, step_counter_ptr, 0, null, null);
                
        inter_date_1 = new Date();
        time_period_read += inter_date_1.getTime()- inter_date.getTime();
        
        System.out.println("Period of parallel code execution:      "+time_period_exec+" ms.");
        System.out.println("Period of data reading after execution: "+time_period_read+" ms.");
        System.out.println();
        System.out.println("Global work sizes:  " + global_work_size[0]+" "+global_work_size[1]+" "+global_work_size[2]);
        System.out.println("Local work sizes:   " + local_work_size[0]+" "+local_work_size[1]+" "+local_work_size[2]);
        System.out.println("Global work sizes:  " + step_counter[0]+" "+ step_counter[1]+" "+ step_counter[2]);
        System.out.println("Work group numbers: " + step_counter[3]+" "+ step_counter[4]+" "+ step_counter[5]);
        
        // TEST
        System.out.println();
        System.out.println("Size of cl_float: "+Sizeof.cl_float);
        System.out.println("Size of buffer with input data: "+total_mem_size);
        
        inter_date_1 = new Date();
        
        // Release of memory objects 
        clReleaseMemObject(memObject_temperatures);
        clReleaseMemObject(memObject_heat_influxes);
        clReleaseMemObject(memObject_step_counter);
        clReleaseMemObject(memObject_init_temprs);
        clReleaseMemObject(memObject_heat_caps);
        clReleaseMemObject(memObject_heat_conds);
        clReleaseMemObject(memObject_densities);
        clReleaseMemObject(memObject_en_types);
        clReleaseMemObject(memObject_neighb_id);
        clReleaseMemObject(memObject_factor);
        
        for(int counter = 0; counter<neighb1S_number; counter++)
            clReleaseMemObject(memObject_neighbours1S[counter]);
        
        // Release of kernel, program, command queue and context
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        
        Date finish_date = new Date();
        double time_period;
        
        time_period = finish_date.getTime()- inter_date_1.getTime();
        System.out.println("Period of data releasing after parallel code execution: "+time_period+" ms.");
        
        time_period = finish_date.getTime()- date_0.getTime();
        System.out.println("Total period of parallel code execution:  "+time_period+" ms.\n");
    }
    
    /** The non-parallel method for simulation of heat transfer
     */
    public static void nonParallelCalcHeatFlows()
    {
        int neighb_id  = -1;
        
        int[] cell_number_I = new int[1]; cell_number_I[0] = CELL_NUMBER_I;
        int[] cell_number_J = new int[1]; cell_number_J[0] = CELL_NUMBER_J;
        int[] cell_number_K = new int[1]; cell_number_K[0] = CELL_NUMBER_K;
        
        // Number of cells
        int cell_number = cell_number_I[0]*cell_number_J[0]*cell_number_K[0];
                
        // Array of cell indices i, j, k
        int[] indices_I = new int[cell_number];
        int[] indices_J = new int[cell_number];
        int[] indices_K = new int[cell_number];        
        
        float heat_influxes[] = new float[cell_number];
        
         // Array of cell location types
        byte[] loc_types = new byte[cell_number];
        
        // Array of cell energy types
        byte[] en_types  = new byte[cell_number];
        
        float[] init_temprs   = new float[cell_number];
        float[] densities     = new float[cell_number];
        float[] temperatures  = new float[cell_number];
        float[] heat_caps     = new float[cell_number];
        float[] heat_conds    = new float[cell_number];
        
        // Single indices of neighbours of all cells
        neighbours1S = new int[neighb1S_number][cell_number];
        
        int cell_index_1D = -1;
                
        double cell_surf   = calcCellSurface(packing_type);
        double cell_volume = calcCellVolume(packing_type);
        
        System.out.println("cell size:    " + CELL_SIZE);
        System.out.println("cell surface: " + cell_surf);
        System.out.println("cell volume:  " + cell_volume);
        
        for (int cell_index_K = 0; cell_index_K < cell_number_K[0]; cell_index_K++)
        for (int cell_index_J = 0; cell_index_J < cell_number_J[0]; cell_index_J++)
        for (int cell_index_I = 0; cell_index_I < cell_number_I[0]; cell_index_I++)
        {
            cell_index_1D++;
            
            // Setting of indices of neighbours at 1st coordination sphere
            setNeighbours1S(cell_index_1D);
            
            loc_types[cell_index_1D]   = INTERNAL_CELL;
            en_types[cell_index_1D]    = INTERNAL_INTERACTED_CELL;
            init_temprs[cell_index_1D] = INITIAL_TEMPERATURE;
                    
            if(cell_index_K == 0)
            {
              init_temprs[cell_index_1D] = BOUNDARY_TEMPERATURE;
              loc_types[cell_index_1D]   = BOUNDARY_CELL;
              en_types[cell_index_1D]    = BOUNDARY_INTERACTED_CELL;
            }
            
            if(cell_index_K == CELL_NUMBER_K-1)
            {
              init_temprs[cell_index_1D] = INITIAL_TEMPERATURE;
              loc_types[cell_index_1D]   = BOUNDARY_CELL;
              en_types[cell_index_1D]    = BOUNDARY_INTERACTED_CELL;
            }
            
            if(cell_index_I == 0 | cell_index_I == CELL_NUMBER_I-1 |
               cell_index_J == 0 | cell_index_J == CELL_NUMBER_J-1)
            {
                loc_types[cell_index_1D] = BOUNDARY_CELL;
                en_types[cell_index_1D]  = BOUNDARY_ADIABATIC_CELL;
            }
            
            heat_caps[cell_index_1D]       = (float)HEAT_CAPACITY;
            heat_conds[cell_index_1D]      = (float)HEAT_CONDUCTIVITY;
            densities[cell_index_1D]       = (float)DENSITY;
            
            // 1st, 2nd and 3rd indices of cell with current single index
            indices_I[cell_index_1D]       = cell_index_I;
            indices_J[cell_index_1D]       = cell_index_J;
            indices_K[cell_index_1D]       = cell_index_K;
        }
        
        Date start_date = new Date();
        
        for(int cell_index = 0; cell_index < cell_number; cell_index++)
        {
            temperatures[cell_index] = init_temprs[cell_index];
        }
    
        for(int step_counter = 0; step_counter < STEP_NUMBER; step_counter++)
        {
          for(int cell_index = 0; cell_index < cell_number; cell_index++)
          {
            heat_influxes[cell_index] = 0;
              
            if(en_types[cell_index] == INTERNAL_INTERACTED_CELL)
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
            {
                neighb_id      = neighbours1S[neighb_counter][cell_index];
        
                if(neighb_id > -1)if(neighb_id < cell_number)
                if(en_types[neighb_id] != BOUNDARY_ADIABATIC_CELL)
                    heat_influxes[cell_index] += heat_conds[cell_index] * (temperatures[neighb_id] - temperatures[cell_index]);
            }
          }
          
          for(int cell_index = 0; cell_index < cell_number; cell_index++)
          {            
            if(en_types[cell_index] == INTERNAL_INTERACTED_CELL)
            {
                heat_influxes[cell_index] = heat_influxes[cell_index] * (float)cell_surf*(float)TIME_STEP/((float)CELL_SIZE*neighb1S_number);
                temperatures[cell_index] += heat_influxes[cell_index]/(heat_caps[cell_index] * densities[cell_index] * (float)cell_volume);
            }
          }
        }
        
        Date finish_date = new Date();
        
        double coords[] = new double[3];
        int grain_index = 1;
        
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter("./task_db/paral_heating/non_paral_heating_"+STEP_NUMBER+".res"));
        
            System.out.println();
            System.out.println("Result of the non-parallel code:");
        
            for(int cell_index = 0; cell_index < cell_number; cell_index++)
            {
                if(indices_I[cell_index] == CELL_NUMBER_I/2)
                if(indices_J[cell_index] == CELL_NUMBER_J/2)
                {   
                    System.out.println("Cell # "+cell_index+"; ["+indices_I[cell_index]+
                            ", "+indices_J[cell_index]+", "+indices_K[cell_index]+"]; type = "+en_types[cell_index]+
                            "; temperature = "+temperatures[cell_index]);
                }
                
                // Calculation of coordinates of current cell
                coords = calcCoordinates(indices_I[cell_index], indices_J[cell_index], indices_K[cell_index]);
                
                bw.write(en_types[cell_index]+" "+loc_types[cell_index]+" "+grain_index+" "+
                         coords[0]+" "+coords[1]+" "+coords[2]+" "+
                         temperatures[cell_index]+" 0 0 0 0 0 0 0 0\n");
                bw.flush();
            } 
            
            bw.close();
            
            System.out.println();
            System.out.println("File  ./task_db/non_paral_heating/paral_heating_"+STEP_NUMBER+".res  is created.");
        }
        catch(IOException io_exc)
        {
            System.out.println("IOException: " + io_exc);
        }        
        
        double time_period = finish_date.getTime()- start_date.getTime();
        System.out.println("Total period of non-parallel code execution:  "+time_period+" ms.\n");               
    }
    
    /** The method calculates the product of two matrices
     */
    public static void parallelMatrixMultiplication()
    {
        int size = 1024;
        int total_size = size*size;
        long oper_number = 0;
        
        int[] a = new int[total_size];
        int[] b = new int[total_size];
        int[] c = new int[total_size];
        
        System.out.println("Non-parallel multiplication.");
        
    //    System.out.print("A = |");
        
        for(int index_I = 0; index_I < size; index_I++)
        {
          for(int index_J = 0; index_J < size; index_J++)
          {
            a[size*index_I + index_J] = 1 + index_I;
            
        //    System.out.print(a[size*index_I + index_J]+" ");
          }
     //     System.out.println("|");
          
       //   if(index_I < size-1)
           //   System.out.print("    |");
     //     else
           //   System.out.println();
        }
        
     //   System.out.print("B = |");
        
        for(int index_I = 0; index_I < size; index_I++)
        {
          for(int index_J = 0; index_J < size; index_J++)
          {
            b[size*index_I + index_J] = 1 + index_J;
            
        //    System.out.print(b[size*index_I + index_J]+" ");
          }
      //   System.out.println("|");
          
       //   if(index_I < size-1)
           //   System.out.print("    |");
      //    else
           //   System.out.println();
        }
        
        for(int index_I = 0; index_I < size; index_I++)
        for(int index_J = 0; index_J < size; index_J++)
            c[size*index_I + index_J] = 0;
        
        Date date_1 = new Date();
        long time_1 = date_1.getTime();
        
        for(int index_I = 0; index_I < size; index_I++)
        for(int index_J = 0; index_J < size; index_J++)
        for(int index_K = 0; index_K < size; index_K++)
        {
            c[size*index_I + index_J] += a[size*index_I + index_K]*b[size*index_K + index_J];
            oper_number += 8;
        }
        
        Date date_2 = new Date();
        long time_2 = date_2.getTime();
        
        long period = time_2 - time_1;
        
        System.out.println("C = A*B");
        /*
        System.out.print("C = |");
        
        for(int index_I = 0; index_I < size; index_I++)
        {
          for(int index_J = 0; index_J < size; index_J++)
            System.out.print(c[size*index_I + index_J]+" ");
          
          System.out.println("|");
          
          if(index_I < size-1)
            System.out.print("    |");
        }
        */
        
        System.out.println("c["+(size-1)+"]["+(size-1)+"] = "+c[size*(size-1)+size-1]);
        System.out.println("Total number of operations: "+oper_number);
        System.out.println("Total period of execution:  "+period+" mcs.");
    }
    
    /** The method calculates and returns 3D coordinates of cell with certain 
     * triple index.
     * @param index1 1st index of cell
     * @param index2 2nd index of cell
     * @param index3 3rd index of cell
     * @return 3D coordinates of cell
     */
    private static double[] calcCoordinates(int index1, int index2, int index3)
    {
        // Coordinates of cell
        double coord_X=0, coord_Y=0, coord_Z=0;
        
        // Cell radius expressed in micrometers
        double radius = 0.5;//cell_size_X/2;
        
        // Calculation of coordinates of cell in case of HCP
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
        {
          if(index3 % 3 == 0)
          {
            if(index1 % 2 == 0)
            {
                coord_X = radius*(1 + index1*Math.sqrt(3));                
                coord_Y = radius*(1 + index2*2);
            }
            else
            {
                coord_X = radius*(1 + index1*Math.sqrt(3));
                coord_Y = radius*(2 + index2*2);
            }
          }
        
          if(index3 % 3 == 1)
          {
            if(index1 % 2 == 0)
            {
                coord_X = radius*(1 + 1/Math.sqrt(3) + index1*Math.sqrt(3));
                coord_Y = radius*(2 + index2*2);
            }
            else
            {
                coord_X = radius*(1 + 1/Math.sqrt(3)+ index1*Math.sqrt(3));
                coord_Y = radius*(1 + index2*2);
            }
          }
                    
          if(index3 % 3 == 2)
          {
            if(index1 % 2 == 0)
            {
                coord_X = radius*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));                        
                coord_Y = radius*(1 + index2*2);
            }
            else
            {
                coord_X = radius*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));
                coord_Y = radius*(2 + index2*2);
            }
          }
        
          coord_Z = radius*(1 + 2*index3*Math.sqrt(2.0/3.0));
        }

        // Calculation of coordinates of cell in case of SCP
        if(packing_type == SIMPLE_CUBIC_PACKING)
        {
            coord_X = radius*(1 + 2*index1);
            coord_Y = radius*(1 + 2*index2);
            coord_Z = radius*(1 + 2*index3);
        }
        
        double[] coords = new double[3];
        
        coords[0] = coord_X;
        coords[1] = coord_Y;
        coords[2] = coord_Z;
        
        return coords;
    }
       
    
    /** The method calculates triple index of cell3D with certain single index.
     * @param intIndex single index of cell3D
     * @param size_X number of allowed values of 1st element of triple index.
     * @param size_Y number of allowed values of 2nd element of triple index.
     * @param size_Z number of allowed values of 3rd element of triple index.
     * @return triple index of cell3D
     */
    public int[] calcTripleIndices(int intIndex, int size_X, int size_Y, int size_Z) 
    {
        // Triple index of cell3D.
        int[] tripleIndex = new int[3];
        
        // Calculation of triple index of cell3D.
        if ((intIndex > -1) & (intIndex < size_X*size_Y*size_Z))
        {
            tripleIndex[0] = (intIndex % (size_X*size_Y)) % size_X;
            tripleIndex[1] = (intIndex % (size_X*size_Y)) / size_X;
            tripleIndex[2] =  intIndex / (size_X*size_Y);
        }
        
        return tripleIndex;
    }    
    
    /** The method calculates triple index of cell3D with certain single index.
     * @param intIndex single index of cell3D
     * @param size_X number of allowed values of 1st element of triple index.
     * @param size_Y number of allowed values of 2nd element of triple index.
     * @param size_Z number of allowed values of 3rd element of triple index.
     * @return triple index of cell3D
     */
    public static Three calcTripleIndex(int intIndex, int size_X, int size_Y, int size_Z) 
    {
        // Triple index of cell3D.
        Three tripleIndex = new Three();
        
        // Calculation of triple index of cell3D.
        if ((intIndex > -1) & (intIndex < size_X*size_Y*size_Z))
        {
            tripleIndex.setI((intIndex % (size_X*size_Y)) % size_X);
            tripleIndex.setJ((intIndex % (size_X*size_Y)) / size_X);
            tripleIndex.setK( intIndex / (size_X*size_Y));
        }
        
        return tripleIndex;
    }
    
    /** The method returns indices of cells at 1st coordination sphere of given cell.
     * @param _intIndex index of given cell
     * @see neighbours3D
     */
    private static void setNeighbours1S(int _intIndex)
    {
        // Triple index of cell
        Three _tripleIndex = calcTripleIndex(_intIndex, CELL_NUMBER_I, CELL_NUMBER_J, CELL_NUMBER_K);
            
        // Indices of considered cell
        int index1 = _tripleIndex.getI();
        int index2 = _tripleIndex.getJ();
        int index3 = _tripleIndex.getK();
            
        // Triple indices of cells at 1st coordinational sphere
        Three[] neighbours = new Three[neighb1S_number];
        
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
        {
          if((index1 % 2 == 0)&(index3 % 3 == 0))
          {
            neighbours[ 0] = new Three(index1-1, index2-1, index3-1);
            neighbours[ 1] = new Three(index1-1, index2  , index3-1);
            neighbours[ 2] = new Three(index1  , index2  , index3-1);           
            neighbours[ 3] = new Three(index1-1, index2-1, index3  );
            neighbours[ 4] = new Three(index1-1, index2  , index3  );
            neighbours[ 5] = new Three(index1  , index2-1, index3  );
            neighbours[ 6] = new Three(index1  , index2+1, index3  );
            neighbours[ 7] = new Three(index1+1, index2-1, index3  );
            neighbours[ 8] = new Three(index1+1, index2  , index3  );
            neighbours[ 9] = new Three(index1-1, index2  , index3+1);
            neighbours[10] = new Three(index1  , index2-1, index3+1);
            neighbours[11] = new Three(index1  , index2  , index3+1);
          }
          
          if((index1 % 2 == 0)&(index3 % 3 == 1))
          {
            neighbours[ 0] = new Three(index1  , index2  , index3-1);
            neighbours[ 1] = new Three(index1  , index2+1, index3-1);
            neighbours[ 2] = new Three(index1+1, index2  , index3-1);                
            neighbours[ 3] = new Three(index1-1, index2  , index3  );
            neighbours[ 4] = new Three(index1-1, index2+1, index3  );
            neighbours[ 5] = new Three(index1  , index2-1, index3  );
            neighbours[ 6] = new Three(index1  , index2+1, index3  );
            neighbours[ 7] = new Three(index1+1, index2  , index3  );
            neighbours[ 8] = new Three(index1+1, index2+1, index3  );                
            neighbours[ 9] = new Three(index1-1, index2  , index3+1);
            neighbours[10] = new Three(index1  , index2  , index3+1);
            neighbours[11] = new Three(index1  , index2+1, index3+1);
          }
                
          if((index1 % 2 == 0)&(index3 % 3 == 2))
          {
            neighbours[ 0] = new Three(index1  , index2-1, index3-1);
            neighbours[ 1] = new Three(index1  , index2  , index3-1);
            neighbours[ 2] = new Three(index1+1, index2  , index3-1);                
            neighbours[ 3] = new Three(index1-1, index2-1, index3  );
            neighbours[ 4] = new Three(index1-1, index2  , index3  );
            neighbours[ 5] = new Three(index1  , index2-1, index3  ); 
            neighbours[ 6] = new Three(index1  , index2+1, index3  );
            neighbours[ 7] = new Three(index1+1, index2-1, index3  );
            neighbours[ 8] = new Three(index1+1, index2  , index3  );                
            neighbours[ 9] = new Three(index1  , index2  , index3+1);
            neighbours[10] = new Three(index1+1, index2-1, index3+1);
            neighbours[11] = new Three(index1+1, index2  , index3+1);
          }
                
          if((index1 % 2 == 1)&(index3 % 3 == 0))
          {
            neighbours[ 0] = new Three(index1-1, index2  , index3-1);
            neighbours[ 1] = new Three(index1-1, index2+1, index3-1);
            neighbours[ 2] = new Three(index1  , index2  , index3-1);                
            neighbours[ 3] = new Three(index1-1, index2  , index3  );
            neighbours[ 4] = new Three(index1-1, index2+1, index3  );
            neighbours[ 5] = new Three(index1  , index2-1, index3  );
            neighbours[ 6] = new Three(index1  , index2+1, index3  );
            neighbours[ 7] = new Three(index1+1, index2  , index3  );
            neighbours[ 8] = new Three(index1+1, index2+1, index3  );                
            neighbours[ 9] = new Three(index1-1, index2  , index3+1);
            neighbours[10] = new Three(index1  , index2  , index3+1);
            neighbours[11] = new Three(index1  , index2+1, index3+1);
          }
            
          if((index1 % 2 == 1)&(index3 % 3 == 1))
          {
            neighbours[ 0] = new Three(index1  , index2-1, index3-1);
            neighbours[ 1] = new Three(index1  , index2  , index3-1);
            neighbours[ 2] = new Three(index1+1, index2  , index3-1);                
            neighbours[ 3] = new Three(index1-1, index2-1, index3  );                        
            neighbours[ 4] = new Three(index1-1, index2  , index3  );
            neighbours[ 5] = new Three(index1  , index2-1, index3  );           
            neighbours[ 6] = new Three(index1  , index2+1, index3  );
            neighbours[ 7] = new Three(index1+1, index2-1, index3  );
            neighbours[ 8] = new Three(index1+1, index2  , index3  );                
            neighbours[ 9] = new Three(index1-1, index2  , index3+1);
            neighbours[10] = new Three(index1  , index2-1, index3+1);
            neighbours[11] = new Three(index1  , index2  , index3+1);
          }
                
          if((index1 % 2 == 1)&(index3 % 3 == 2))
          {
            neighbours[ 0] = new Three(index1  , index2  , index3-1);
            neighbours[ 1] = new Three(index1  , index2+1, index3-1);
            neighbours[ 2] = new Three(index1+1, index2  , index3-1);                
            neighbours[ 3] = new Three(index1-1, index2  , index3  );
            neighbours[ 4] = new Three(index1-1, index2+1, index3  );
            neighbours[ 5] = new Three(index1  , index2-1, index3  );                
            neighbours[ 6] = new Three(index1  , index2+1, index3  );
            neighbours[ 7] = new Three(index1+1, index2  , index3  );
            neighbours[ 8] = new Three(index1+1, index2+1, index3  );                
            neighbours[ 9] = new Three(index1  , index2  , index3+1);
            neighbours[10] = new Three(index1+1, index2  , index3+1);
            neighbours[11] = new Three(index1+1, index2+1, index3+1);
          }
        }

        if(packing_type == SIMPLE_CUBIC_PACKING)
        {
          // Triple indices of cubic cells at 1st coordinational sphere
          neighbours[ 0] = new Three(index1  , index2  , index3-1);
          neighbours[ 1] = new Three(index1-1, index2  , index3  );
          neighbours[ 2] = new Three(index1  , index2-1, index3  );
          neighbours[ 3] = new Three(index1  , index2+1, index3  );
          neighbours[ 4] = new Three(index1+1, index2  , index3  );
          neighbours[ 5] = new Three(index1  , index2  , index3+1);
        }
        
        int neighbour_indexI;
        int neighbour_indexJ;
        int neighbour_indexK;

    //    System.out.print("Cell # "+_intIndex+"; neighbours: ");
            
        // If each triple index of neighbour cell is within boundaries then
        // the single index of the cell is calculated, else
        // this cell is deleted from the array of neighbours (single index = -3).
        for (int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {                
            neighbour_indexI = neighbours[neighb_counter].getI();            
            neighbour_indexJ = neighbours[neighb_counter].getJ();
            neighbour_indexK = neighbours[neighb_counter].getK();
               
            if((neighbour_indexI > -1)&(neighbour_indexI < CELL_NUMBER_I)&
               (neighbour_indexJ > -1)&(neighbour_indexJ < CELL_NUMBER_J)&
               (neighbour_indexK > -1)&(neighbour_indexK < CELL_NUMBER_K))
            {
                neighbours1S[neighb_counter][_intIndex] = neighbour_indexI + neighbour_indexJ*CELL_NUMBER_I + 
                                                          neighbour_indexK*CELL_NUMBER_I*CELL_NUMBER_J;
            }
            else
                neighbours1S[neighb_counter][_intIndex] = -3;
            
            if(neighbours1S[neighb_counter][_intIndex] >= CELL_NUMBER_I*CELL_NUMBER_J*CELL_NUMBER_K)
               neighbours1S[neighb_counter][_intIndex] = -3;
        //    System.out.print(neighbours1S[_intIndex][neighb_counter]+" ");
        }

   //     System.out.println();
    }    
    
    /** The method calculates volume of cell3D 
     * (in case of hexagonal close packing or in case of simple cubic packing).
     * @param packing_type type of packing
     * @return volume of cell3D
     */
    private static double calcCellVolume(byte packing_type)
    {
        double cell_volume = 0;
        
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
            cell_volume = 0.6938*CELL_SIZE*CELL_SIZE*CELL_SIZE;

        if(packing_type == SIMPLE_CUBIC_PACKING)
            cell_volume = CELL_SIZE*CELL_SIZE*CELL_SIZE;
        
        return cell_volume;
    }

    /** The method calculates surface area of cell3D
     * (in case of hexagonal close packing or in case of simple cubic packing).
     * @param packing_type type of packing
     * @return surface area of cell3D
     */
    private static double calcCellSurface(byte packing_type)
    {
        double cell_surface = 0;
        
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
            cell_surface = 0.6938*6*CELL_SIZE*CELL_SIZE;

        if(packing_type == SIMPLE_CUBIC_PACKING)
            cell_surface = 6*CELL_SIZE*CELL_SIZE;
        
        return cell_surface;
    }
}
