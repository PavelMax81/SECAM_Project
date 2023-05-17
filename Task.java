package cellcore;
import java.util.*;
import java.io.*;

/*
 * @(#) Task.java version 1.0.2;       Apr 2008
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for setting of task that should be performed.
 *
 *=============================================================
 *  Last changes :
 *        10 October 2008 by Dmitry D. Moiseenko & Pavel V. Maksimov 
 *         (small optimization and a few comments).
 *            File version 1.0.3
 */

/** Class for setting of task that should be performed.
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0 - Feb 2008
 */

public class Task extends Thread
{   
    /** Name of task */
    String task_name;
    
    /** Index of thread */
    String thread_index;    
    
    /** Simulated CA micro specimen */
    SpecimenR3_HCP specimenR3;
    
    /** Indices of finite element in FE macro specimen*/
    Three indicesFE;
        
    /** Number of CA along each axe of finite element */
    Three sizesFE;
    
    /** Temperatures in vertices of finite element */
    double[] temperaturesFE;
    
    /** Average heat capacity of finite element*/     
    double averageHeatCapacityFE;
    
    /** Average heat conductivity of finite element*/     
    double averageHeatConductivityFE;

    public static final double BOLTZMANN_CONST = 1.3806504E-23;
    
    /** The constructor creates task for operations with finite element.
     * @param _specimenR3 sample specimen.
     * @param temperatures temperatures in vertices of finite element 
     */
    public Task(SpecimenR3_HCP _specimenR3)//, double[] temperatures)
    {        
        specimenR3 = _specimenR3;
//        specimenR3.setBoundaryConditions(_specimenR3.getBoundaryConditions());
        
//   // BEGIN
                
        // 1) FEM part of time step:
        // Calculation of temperature in vertices of each finite element
        //.....................................
        // Here should be the code!
        //.....................................
        // FEM STEP END
        
        // SECAM part of time step: 
        /* Initialization of sizes and temperatures of CA micro specimen */        
        sizesFE   = new Three(_specimenR3.getCellNumberI(),
                              _specimenR3.getCellNumberJ(), 
                              _specimenR3.getCellNumberK());
        
     // END
    }
    
    /** The constructor creates task for operations with finite element.
     * @param _task task for operations with finite element
     */
    public Task(Task _task)
    {
        specimenR3 = _task.getSpecimenR3();
//       specimenR3.setBoundaryConditions(_task.getSpecimenR3().getBoundaryConditions());
        
        //indicesFE = new Three (_task.getIndicesFE());
        //sizesFE = new Three (_task.getSizesFE());
        //setTemperatures(_task.getTemperaturesFE());
        //averageHeatCapacityFE = _task.getAverageHeatCapacityFE();
        //averageHeatConductivityFE = _task.getAverageHeatConductivityFE();
    }
    
    /** The method executes the task.
     * @param writeFileName name of file for writing of simulation results
     * @param heat_flows parameter responsible for starting of heat transfer simulation
     * @param mech_flows parameter responsible for starting of mechanical loading simulation
     * @param recryst parameter responsible for starting of recrystallization simulation
     * @param cracks parameter responsible for starting of crack formation simulation
     * @param equilibrium_state variable responsible for equilibrium state of specimen
     */    
    public void run(String writeFileName, byte heat_flows, byte mech_flows, 
                    byte recryst, byte cracks, byte equilibrium_state)
    {
        /* Block 10 continue*/

        /** Setting of temperatures of cells included in finite element */
        //  specimenR3.setTemperatures(writeFileName, temperaturesFE);
     
        //  specimenR3.calcHeatFlows_T(writeFileName);
        //  specimenR3.calcHeatFlows_Q(writeFileName);
        //  specimenR3.setStates(writeFileName);
        System.out.println();
        System.out.println("START!!!");
        System.out.println();

        specimenR3.calcProcess(writeFileName, heat_flows, mech_flows, 
                               recryst, cracks, equilibrium_state);

        System.out.println();
        System.out.println("FINISH!!!");
        System.out.println();
        
  /** Average heat capacity and heat conductivity of finite element*/               
  //      averageHeatCapacityFE     = specimenR3.calcAverageParametersFE()[0];
  //      averageHeatConductivityFE = specimenR3.calcAverageParametersFE()[1];
        
        yield();
                
        //interrupt();
        //suspend();
        //stop();
    }
    
    /** The method returns name of thread.
     * @return index of thread
     */
    public String get_thread_index()
    {
        return thread_index;
    }
    
    /** The method returns indices of finite element.
     * @return indices of finite element
     */
    public Three getIndicesFE()
    {
        return indicesFE;
    }
        
    /** The method returns sizes of finite element.
     * @return sizes of finite element
     */
    public Three getSizesFE()
    {
        return sizesFE;
    }
    
    /** The method returns temperatures in vertices of finite element.
     * @return temperatures in vertices of finite element
     */
    public double[] getTemperaturesFE()
    {
        return temperaturesFE;
    }
    
    /** The method returns average heat capacity of finite element.
     * @return average heat capacity of finite element
     */     
    public double getAverageHeatCapacityFE()
    {
        return averageHeatCapacityFE;
    }
    
    /** The method returns average heat conductivity of finite element.
     * @return average heat conductivity of finite element
     */
    public double getAverageHeatConductivityFE()
    {
        return averageHeatConductivityFE;
    }
    
    /** The method sets indices of finite element.
     * @param _indicesFE indices of finite element
     */
    public void setIndicesFE(Three _indicesFE)
    {
        indicesFE = new Three(_indicesFE);
    }
        
    /** The method sets sizes of finite element.
     * @param _sizesFE sizes of finite element
     */
    public void setSizesFE(Three _sizesFE)
    {
        sizesFE = new Three(_sizesFE);
    }
    
    /** The method sets temperatures in vertices of finite element.
     * @param _temperatures temperatures in vertices of finite element
     */
    public void setTemperatures(double[] _temperatures)
    {
        temperaturesFE = new double[8];
        
        for(int vertex_counter = 0; vertex_counter < _temperatures.length; vertex_counter++)
        temperaturesFE[vertex_counter] = _temperatures[vertex_counter];
    }
    
    /** The method returns simulated specimen.
     * @return simulated specimen
     */
    public SpecimenR3_HCP getSpecimenR3()
    {
        return specimenR3;
    }
}