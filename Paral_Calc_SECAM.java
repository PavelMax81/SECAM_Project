package paral_calc;

/**
 * @(#) Paral_Calc_SECAM.java version 1.0.0;       2 April 2015
 *
 * Copyright (c) 2002-2016 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for simulation of heat transfer on the base of SECA method
 *
 *=============================================================
 *  Last changes :
 *          09 March 2016 by Pavel V. Maksimov (realization of time cycle: kernel is started at each time step).
 *          11 March 2016 by Pavel V. Maksimov (comments are added).
 *          15 March 2016 by Pavel V. Maksimov (arguments are input from command line; 
 *                                              two types of cell packing can be chosen; 
 *                                              work-items and work groups have 1D indices;
 *                                              the total number of cells can be less then the number of work-items).
 *          21 April 2016 by Pavel V. Maksimov (error is corrected: index of array "init_temprs[]" in kernel_1 was equal to (-3)
 *                                              if element was out of specimen; now in this case the index is equal to N, 
 *                                              N is the total number of elements).
 * 
 *          File version 1.0.4
 */

// Import of methods from class jocl.CL
import static org.jocl.CL.*;

// Import of packages
import org.jocl.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;
import recGeometry.*;
import cellcore.*;
import util.Common;

/** Class for simulation of heat transfer on the base of SECA method.
 * Применяется методика распараллеливания вычислительного процесса на базе OpenCL.
 *  @author Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0.4 - April 2016
 */
public class Paral_Calc_SECAM
{  
    /** Name of task */
    private static String TASK_NAME;
          
    /** Константы, отвечающие за тип упаковки элементов:
       0 - гексагональная (HCP), 1 - кубическая (SCP). */
    private final static byte HEXAGONAL_CLOSE_PACKING = 0;
    private final static byte SIMPLE_CUBIC_PACKING    = 1;
    
    /** Переменная, отвечающая за тип упаковки элементов */
    private static byte packing_type;
    
    // Число элементов на 1-й координационной сфере.
    private static int neighb1S_number;
    
    // Массив индексов элементов на 1-й координационной сфере для всех элементов.
    private static int[][] neighbours1S;
    
    // Cell diameter in meters
    private static double CELL_SIZE        = (double)1.0E-6;
    
    // Total number of cells
    private static int cell_number;
    
    // Общее число элементов должно быть кратным размеру work-group:
    // каждый work-item обрабатывает один элемент
    private static int work_item_number;
                
    // Number of cells along axes for all specimen
    private static int cell_number_I;//       =  64;// 16;// 
    private static int cell_number_J;//       =  64;//  8;// 
    private static int cell_number_K;//       =  64;// 16;// 
    
    // Number of work-items in a work-group
    private final static int LOCAL_CELL_NUMBER = 1024; // 256; // 
    
    // Number of time steps
    private static long STEP_NUMBER;//         = 10000;
    
    // Number of files for recording of data
    private static int FILE_NUMBER;
    
    // The value of time step (in seconds)
    private static double TIME_STEP                  = (double)   0.0;// 1.0E-9;
    
    // Initial temperature
    private final static double INITIAL_TEMPERATURE  = (double)   0.0;// 300.0;
    
    // Boundary temperature
    private final static double BOUNDARY_TEMPERATURE = (double)   0.0;// 1500.0;
    
    // Parameters of specimen material (aluminium):
    // heat conductivity
    private final static double HEAT_CONDUCTIVITY    = (double)   0.0;// 237.0;
    
    // heat capacity for ZrO2 // Al
    private final static double HEAT_CAPACITY        = (double) 70.04;//930.0;
    
    // coefficient of thermal expansion
    private final static double HEAT_EXPANSION_COEFF = (double)   0.0;// 2.29E-5;
    
    // density for ZrO2 // Al
    private final static double DENSITY              = (double)5680.0;//2700.0;
    
    // Boltzmann constant
    private final static double BOLTZMANN_CONST      = (double)   1.3806504E-23;
    
    // highest misorientation angle
    private final static double MAX_ORIENTATION_ANGLE = (double)  0.0;// 15.0;// 45.0; //
    
    // maximal value of grain boundary heat mobility
    private final static double HEAT_MAX_MOBILITY     = (double)  0.0;// 1.0E-6; // 1.0E-7; //
    
    // maximal grain boundary energy
    private final static double GRAIN_BOUNDARY_ENERGY = (double)  0.0;// 2.0E-7;
    
    // Young modulus for ZrO2
    private final static double MOD_ELAST             = (double)  1.72E11;
    
    // Cell location types:
    // cell in a specimen
 //   private final static byte INTERNAL_CELL =  3;
    
    // outer cell on specimen boundary
//    private final static byte BOUNDARY_CELL =  2;
    
    /** Types of cell according to its location in grain and in specimen */
    /** Cell located in outer boundary of specimen */
    public static final byte OUTER_CELL    = UICommon.OUTER_CELL;
    
    /** Cell located in the interior of grain */
    public static final byte INTRAGRANULAR_CELL = UICommon.INTRAGRANULAR_CELL;

    /** Cell located at grain boundary in the interior of specimen */
    public static final byte INTERGRANULAR_CELL      = UICommon.INTERGRANULAR_CELL;

    /** Cell located at specimen boundary */
    public static final byte INNER_BOUNDARY_CELL          = UICommon.INNER_BOUNDARY_CELL;

    /** Cell located at specimen boundary and at grain boundary */
    public static final byte INNER_BOUNDARY_INTERGRANULAR_CELL = UICommon.INNER_BOUNDARY_INTERGRANULAR_CELL;
    
    /** Cell located in crack */
    public static final byte CRACK_CELL = UICommon.CRACK_CELL;
    
    /** Cell located in transformed material */
    public static final byte TRANSFORMED_CELL = UICommon.TRANSFORMED_CELL;
    
    /** Cell located at boundary between specimen and outer space */
    public static final byte BOUNDARY_CELL = UICommon.BOUNDARY_CELL;
    
    /** Cell located at boundary between specimen and outer space and at grain boundary */
    public static final byte BOUNDARY_INTERGRANULAR_CELL = UICommon.BOUNDARY_INTERGRANULAR_CELL;
            
    /** Types of outer boundary cells according to types of mechanical and thermal interaction
    * of grains and possibility of grain recrystallization */
    public static final byte LAYER_CELL                 = UICommon.LAYER_CELL;
    public static final byte INNER_CELL                 = UICommon.INNER_CELL; // INTERNAL_INTERACTED_CELL
    public static final byte ADIABATIC_TEMPERATURE_CELL = UICommon.ADIABATIC_TEMPERATURE_CELL; // BOUNDARY_INTERACTED_CELL
    public static final byte ADIABATIC_THERMAL_CELL     = UICommon.ADIABATIC_THERMAL_CELL;
    public static final byte STRAIN_ADIABATIC_CELL      = UICommon.STRAIN_ADIABATIC_CELL;
    public static final byte STRAIN_TEMPERATURE_CELL    = UICommon.STRAIN_TEMPERATURE_CELL;
    public static final byte STRAIN_THERMAL_CELL        = UICommon.STRAIN_THERMAL_CELL;
    public static final byte STRESS_ADIABATIC_CELL      = UICommon.STRESS_ADIABATIC_CELL;
    public static final byte STRESS_TEMPERATURE_CELL    = UICommon.STRESS_TEMPERATURE_CELL;
    public static final byte STRESS_THERMAL_CELL        = UICommon.STRESS_THERMAL_CELL;
    public static final byte ADIABATIC_ADIABATIC_CELL   = UICommon.ADIABATIC_ADIABATIC_CELL; // BOUNDARY_ADIABATIC_CELL
    
    /** Types of grain according to its role in recrystallization process:
     * 0 - initial grain; 1 - new grain; 2 - twin grain.
     */
    public static final byte INITIAL_GRAIN  = UICommon.INITIAL_GRAIN;
    public static final byte NEW_GRAIN      = UICommon.NEW_GRAIN;
    public static final byte TWIN_GRAIN     = UICommon.TWIN_GRAIN;
    
    // Arrays of parameters of cells from specimen
    public static Specimen_Arrays specArrays;
    
    /** Number of grains in specimen */
    private static int grain_number;
    
    /** Minimal number of neighbour cells in adjacent grain 
     * necessary for joining of cell to this adjacent grain */ 
    private static int min_neighbours_number;
    
    /** Constant loading during all numerical experiment */
    public static final byte CONSTANT_LOADING_BYTE_VALUE  = 0;
    
    /** Cycle loading: constant loading during first time period, 
     * no loading during second period */
    public static final byte CYCLE_LOADING_BYTE_VALUE     = 1;
    
    /** Loading according to periodic time function */
    public static final byte PERIODIC_LOADING_BYTE_VALUE  = 2;
    
    /** Cycle loading: constant loading during first time period, 
     * constant loading with opposite sign during second period */
    public static final byte CYCLE_LOADING_BYTE_VALUE_2   = 3;
    
    /** coordinates of lattice vectors for grains */
    private static double[][] lattice_vectors;
    
    // Number of parameters in output files
    private static final int param_number = 25;
    
    // 3 arrays of coordinates X, Y, Z of cells
    private static double[][] cell_coords;
    
    // array of relative changes of volumes of all cells
    private static double[] rel_vol_changes;
    
    // Buffered writers for parameters from "<...>_jocl.res"
    private static BufferedWriter bw_temperatures;
    private static BufferedWriter bw_spec_elast_energies;
    private static BufferedWriter bw_crack_values;
    private static BufferedWriter bw_force_moments_X;
    private static BufferedWriter bw_force_moments_Y;
    private static BufferedWriter bw_force_moments_Z;
    private static BufferedWriter bw_spec_diss_energies;
    private static BufferedWriter bw_spec_diss_en_influxes;
    private static BufferedWriter bw_rel_total_diss_energies;
    
    // Buffered writers for parameters from "<...>_jocl_stresses.res"
    private static BufferedWriter bw_def_densities;
    private static BufferedWriter bw_abs_stresses;
    private static BufferedWriter bw_def_density_changes;
    private static BufferedWriter bw_stresses_X;
    private static BufferedWriter bw_stresses_Y;
    private static BufferedWriter bw_stresses_Z;
    private static BufferedWriter bw_rel_diss_energies;
    private static BufferedWriter bw_rel_diss_en_influxes;
    private static BufferedWriter bw_rel_diss_en_changes;
    
    // Buffered writers for parameters from "<...>_jocl_torsion.res"
    private static BufferedWriter bw_torsion_velocity_X;
    private static BufferedWriter bw_torsion_velocity_Y;
    private static BufferedWriter bw_torsion_velocity_Z;
    private static BufferedWriter bw_torsion_angle_X;
    private static BufferedWriter bw_torsion_angle_Y;
    private static BufferedWriter bw_torsion_angle_Z;
    private static BufferedWriter bw_abs_torsion_velocity;
    private static BufferedWriter bw_abs_torsion_angle;
    
    /** Constructor of the class.
     * @param args 
     */
    public Paral_Calc_SECAM(String[] args)
    { 
      try
      {
        // Buffered writers for parameters from "<...>_jocl.res"
        bw_temperatures            = new BufferedWriter(new FileWriter(""));
        bw_spec_elast_energies     = new BufferedWriter(new FileWriter(""));
        bw_crack_values            = new BufferedWriter(new FileWriter(""));
        bw_force_moments_X         = new BufferedWriter(new FileWriter(""));
        bw_force_moments_Y         = new BufferedWriter(new FileWriter(""));
        bw_force_moments_Z         = new BufferedWriter(new FileWriter(""));
        bw_spec_diss_energies      = new BufferedWriter(new FileWriter(""));
        bw_spec_diss_en_influxes   = new BufferedWriter(new FileWriter(""));
        bw_rel_total_diss_energies = new BufferedWriter(new FileWriter(""));
        
        // Buffered writers for parameters from "<...>_jocl_stresses.res"
        bw_def_densities           = new BufferedWriter(new FileWriter(""));
        bw_abs_stresses            = new BufferedWriter(new FileWriter(""));
        bw_def_density_changes     = new BufferedWriter(new FileWriter(""));
        bw_stresses_X              = new BufferedWriter(new FileWriter(""));
        bw_stresses_Y              = new BufferedWriter(new FileWriter(""));
        bw_stresses_Z              = new BufferedWriter(new FileWriter(""));
        bw_rel_diss_energies       = new BufferedWriter(new FileWriter(""));
        bw_rel_diss_en_influxes    = new BufferedWriter(new FileWriter(""));
        bw_rel_diss_en_changes     = new BufferedWriter(new FileWriter(""));
        
        // Buffered writers for parameters from "<...>_jocl_torsion.res"
        bw_torsion_velocity_X      = new BufferedWriter(new FileWriter(""));
        bw_torsion_velocity_Y      = new BufferedWriter(new FileWriter(""));
        bw_torsion_velocity_Z      = new BufferedWriter(new FileWriter(""));
        bw_torsion_angle_X         = new BufferedWriter(new FileWriter(""));
        bw_torsion_angle_Y         = new BufferedWriter(new FileWriter(""));
        bw_torsion_angle_Z         = new BufferedWriter(new FileWriter(""));
        bw_abs_torsion_velocity    = new BufferedWriter(new FileWriter(""));
        bw_abs_torsion_angle       = new BufferedWriter(new FileWriter(""));
      }
      catch(IOException io_exc)
      {
        System.out.println("ERROR in Paral_Calc_SECAM(String[] args): "+io_exc);
      }
        
      startParalCalc(args);
    }
    
    /** The main method of the class
     * @param args string parameters from command line
     */
    public static void main(String args[])
    {
      Paral_Calc_SECAM paral_calc = new Paral_Calc_SECAM(args);
      
      // startParalCalc(args);
    }
    
    /** The method realizes start of parallel calculations.
     * @param args 
     */
    private static void startParalCalc(String[] args)
    {
      if(args.length > 2)
        {
          if(args.length < 6)
          {
            System.out.println("============================================");
            System.out.println("Please, input 7 parameters:");
            System.out.println("0. task name;");
            System.out.println("1. number of cells along axis X;");
            System.out.println("2. number of cells along axis Y;");
            System.out.println("3. number of cells along axis Z;");
            System.out.println("4. type of packing of cells (SCP or HCP);");
            System.out.println("5. number of time steps;");
            System.out.println("6. number of files with output data.");
            System.out.println("============================================");
          }
          else
          {
            System.out.println("============================================");
            System.out.println("These 7 input parameters are:");
            System.out.println("0. task name;");
            System.out.println("1. number of cells along axis X;");
            System.out.println("2. number of cells along axis Y;");
            System.out.println("3. number of cells along axis Z;");
            System.out.println("4. type of packing of cells (SCP or HCP);");
            System.out.println("5. number of time steps;");
            System.out.println("6. number of files with output data.");
            System.out.println("============================================");
          
            // Setting of number of cells along axes
            cell_number_I = (new Integer(args[1])).intValue();
            cell_number_J = (new Integer(args[2])).intValue();
            cell_number_K = (new Integer(args[3])).intValue();
            
            // Setting of type of packing of cells
            if(args[4].equals("SCP")) packing_type = SIMPLE_CUBIC_PACKING;
            if(args[4].equals("HCP")) packing_type = HEXAGONAL_CLOSE_PACKING;
            
            // Setting of type of packing of cells
            if(packing_type != SIMPLE_CUBIC_PACKING & packing_type != HEXAGONAL_CLOSE_PACKING)
            {
                // Stochastic choice of packing type
                double rand = Math.random();
            
                if(rand < 0.5) 
                {
                    packing_type = SIMPLE_CUBIC_PACKING;
                    System.out.println("Type of cell packing is chosen stochastically: SCP");
                }
                else
                {
                    packing_type = HEXAGONAL_CLOSE_PACKING;
                    System.out.println("Type of cell packing is chosen stochastically: HCP");
                }
            }
            
            // Setting of number of time steps
            STEP_NUMBER   = (new Integer(args[5])).intValue();
          
            // Setting of number of files with output data
            FILE_NUMBER   = (new Integer(args[6])).intValue();
          }
        }
        else
        {
          // Creation of arrays of parameters of cells from specimen
          specArrays = new Specimen_Arrays(args[0]);
          
          // Name of task
          TASK_NAME = new String(args[0]);
            
          // Type of packing of cells
          packing_type  = specArrays.packing_type;
          
          // Setting of number of time steps
          STEP_NUMBER   = specArrays.step_number;
          
          // Setting of number of files with output data
          FILE_NUMBER   = specArrays.output_file_number;
          
          // Values of time step and cell size
          TIME_STEP = specArrays.time_step;
          CELL_SIZE = specArrays.cell_size;
          
          // TEST
          System.out.println("TIME_STEP = "+TIME_STEP+" s");
          System.out.println("CELL_SIZE = "+CELL_SIZE+" m");
          
          // In case of SCP the number of neighbours at 1st coordination sphere is equal to 6.
          if(packing_type == SIMPLE_CUBIC_PACKING)
            neighb1S_number = 6;
          
          // In case of HCP the number of neighbours at 1st coordination sphere is equal to 12.
          if(packing_type == HEXAGONAL_CLOSE_PACKING)
            neighb1S_number = 12;
          
          // Choice of method of calculation
          boolean parallelCalcHeatFlows   = false;// true; // 
          boolean parallelCalcHeatRecryst = true; // false;// 
          
          try
          {
            // The method simulates heat propagation in cellular automaton basing on OpenCL.
            if(parallelCalcHeatFlows)   parallelCalcHeatFlows();
            
            // The method simulates heat recrystallization in cellular automaton basing on OpenCL.
            if(parallelCalcHeatRecryst) parallelCalcHeatRecryst();
          }
          catch(org.jocl.CLException cl_exc)
          {
            System.out.println("CLException! "+cl_exc);
          }
        }
    }
    
    /** The method calculates summary force moment of cell according to stress vectors of neighbour cells
     * @param cell_index index of cell
     * @param en_types array of cell energy types
     * @param stresses_X array of coordinates X of stress vectors for all cells
     * @param stresses_Y array of coordinates Y of stress vectors for all cells
     * @param stresses_Z array of coordinates Z of stress vectors for all cells
     * @return summary force moment of cell
     */
    public static VectorR3 calcSummaryForceMoment(int cell_index, byte[] en_types, double stresses_X[], double stresses_Y[], double stresses_Z[])
    { 
      // Indices of neighbours on 1st coordination sphere of "central" cell
      int[] neighb1S_indices = new int[neighb1S_number];
      
      for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        neighb1S_indices[neighb_counter] = neighbours1S[neighb_counter][cell_index];
      
      // Coordinates of current cell
      VectorR3 cell_coordinates = new VectorR3(cell_coords[0][cell_index], cell_coords[1][cell_index], cell_coords[2][cell_index]);
      
      // Coordinates of neighbour cells
      VectorR3[] coordinates1S = new VectorR3[neighb1S_number];
      
      // Index of neighbour cell
      int neighb1S_index = -1;
      
      for(int cell1S_counter = 0; cell1S_counter < neighb1S_number; cell1S_counter++)
      {
        neighb1S_index = neighb1S_indices[cell1S_counter];
          
        coordinates1S[cell1S_counter] = new VectorR3(cell_coords[0][neighb1S_index], cell_coords[1][neighb1S_index], cell_coords[2][neighb1S_index]);
      }
      
      // Vectors from "cell1S" to "central" cell
      VectorR3[] cell1S_vectors = new VectorR3[neighb1S_number];
      
      for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
        cell1S_vectors[neighb1S_counter] = new VectorR3(0, 0, 0);
      
      VectorR3[] stresses = new VectorR3[neighb1S_number];
      
      // Vector of force moment of "central" cell corresponding to "cell1S"
      VectorR3 force_moment;
      
      // Summary force moment
      VectorR3 summary_force_moment = new VectorR3(0, 0, 0);
            
      // Size of specimen
      double spec_size = cell_number_I*cell_number_J*cell_number_K;
      
      // type of grain containing cell
      int grain_type;
      
      // Obtaining of all cells at 1st coordination sphere of "central" cell
      if(en_types[cell_index] == INNER_CELL | en_types[cell_index] == OUTER_CELL)
      for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
      if(neighb1S_indices[neighb1S_counter] > -1 & neighb1S_indices[neighb1S_counter] < spec_size)
      {
        // Index of neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
        neighb1S_index = neighb1S_indices[neighb1S_counter];
        
        // Calculation of vector from "central" cell to "cell1S"
        cell1S_vectors[neighb1S_counter] = residial(coordinates1S[neighb1S_counter], cell_coordinates);
          
        // grain type of current neighbour cell
        grain_type = en_types[neighb1S_index];
          
        if(grain_type != Common.ADIABATIC_ADIABATIC_CELL)
          cell1S_vectors[neighb1S_counter].normalize();
      }
      
      // Calculation of vectors of force moment for all pairs "central cell - cell1S" and summary force vector
      if(en_types[cell_index] == INNER_CELL | en_types[cell_index] == OUTER_CELL)
      for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
      if(neighb1S_indices[neighb1S_counter] > -1 & neighb1S_indices[neighb1S_counter] < spec_size)
      {
        // Neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
        neighb1S_index = neighb1S_indices[neighb1S_counter];
        
        // grain type of current neighbour cell
        grain_type = en_types[neighb1S_index];
        
        // Stress vector for current neighbour cell
        stresses[neighb1S_counter] = new VectorR3(stresses_X[neighb1S_index], stresses_Y[neighb1S_index], stresses_Z[neighb1S_index]);
        
        if(grain_type != Common.ADIABATIC_ADIABATIC_CELL)
        {
          // Calculation of vector of force moment for current pair "central cell - cell1S"
          force_moment = calcVectorProduct(cell1S_vectors[neighb1S_counter], stresses[neighb1S_counter]);
        
          // Calculation of summary force vector
          summary_force_moment.add(force_moment);
        }
      }
      
      return summary_force_moment;
    }
    
    /** The method returns vector product of two vectors (vect_1 X vect_2).
     * @param vect_1 first vector
     * @param vect_2 second vector
     * @return vector product of two vectors (vect_1 X vect_2)
     */
    public static VectorR3 calcVectorProduct(VectorR3 vect_1, VectorR3 vect_2)
    {
        // Coordinates of 1st vector
        double vect1_X = vect_1.getX();
        double vect1_Y = vect_1.getY();
        double vect1_Z = vect_1.getZ();
        
        // Coordinates of 2nd vector
        double vect2_X = vect_2.getX();
        double vect2_Y = vect_2.getY();
        double vect2_Z = vect_2.getZ();
        
        // Coordinates of vector product
        double coord_X = vect1_Y*vect2_Z - vect1_Z*vect2_Y;
        double coord_Y = vect1_Z*vect2_X - vect1_X*vect2_Z;
        double coord_Z = vect1_X*vect2_Y - vect1_Y*vect2_X;
        
        return new VectorR3(coord_X, coord_Y, coord_Z);
    }
    
    /** The method simulates heat propagation in cellular automaton basing on OpenCL.
     */
    public static void parallelCalcHeatFlows()
    {
        // Total number of cells
        cell_number   = specArrays.cell_number;// cell_number_I*cell_number_J*cell_number_K;// 
        
        // Number of cells along axes
        cell_number_I = specArrays.cell_number_X;
        cell_number_J = specArrays.cell_number_Y;
        cell_number_K = specArrays.cell_number_Z;
        
        System.out.println("===================START====================");
        System.out.println("Number of cells along axis X: "+cell_number_I);
        System.out.println("Number of cells along axis Y: "+cell_number_J);
        System.out.println("Number of cells along axis Z: "+cell_number_K);
        
        if(packing_type == SIMPLE_CUBIC_PACKING)
        System.out.println("Type of cell packing:         SCP (simple cubic packing)");
        
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
        System.out.println("Type of cell packing:         HCP (hexagonal close packing)");
        
        System.out.println("Number of time steps:         "+STEP_NUMBER);
        System.out.println("Number of created files:      "+FILE_NUMBER);
        System.out.println("============================================");
        System.out.println("Total number of cells:        "+cell_number);
        
        double log2_cell_number = Math.log(cell_number)/Math.log(2.0);
        
        if(Math.floor(log2_cell_number) < log2_cell_number)
            log2_cell_number = Math.floor(log2_cell_number);
        
        // Размер work-group (число элементов, обрабатываемых в рамках одной work-group)
        int work_group_size = Math.min(LOCAL_CELL_NUMBER, (int)Math.pow(2, (int)log2_cell_number));
        
        // Общее число work-group
        int work_group_number = Math.max(work_group_size, cell_number)/work_group_size;
        
        if(cell_number%work_group_size != 0)
            work_group_number++;
        
        // Общее число элементов должно быть кратным размеру work-group:
        // каждый work-item обрабатывает один элемент
        work_item_number = work_group_size*work_group_number;
        
        System.out.println("Total number of work-items:   "+work_item_number);
        System.out.println("Size of work-group:           "+work_group_size);
        System.out.println("Number of work-groups:        "+work_group_number);
        System.out.println("============================================");
        
        // Array of cell single indices i, j, k (for recording of results)
        int[] indices_I = new int[cell_number];
        int[] indices_J = new int[cell_number];
        int[] indices_K = new int[cell_number];
        
        // Array of cell location types
        byte[] loc_types = new byte[work_item_number];
        
        // Array of cell energy types (the last element of the array corresponds to energy type of outer cells)
        byte[] en_types  = new byte[work_item_number+1];
        
        // Single indices of neighbours of all cells
        neighbours1S = new int[neighb1S_number][work_item_number];
        
        // Misorientation angles for grains containing neighbour cells divided by maximal angle of misorientation
        double[][] spec_angle_diff     = new double[neighb1S_number][work_item_number];
        
        // Maximal mobilities at grain boundaries
        double[][] neighb_heat_max_mob = new double[neighb1S_number][work_item_number];
        
        // Specific energies of grain boundaries with neighbour cells
        double[][] spec_bound_energies = new double[neighb1S_number][work_item_number];
         
        // Energies of grain boundaries with neighbour cells divided by kT
        double[][] bound_energies      = new double[neighb1S_number][work_item_number];
        
        // Energies of grain boundaries with neighbour cells divided by kT
        double[][] bound_velocities    = new double[neighb1S_number][work_item_number];
        
        // Probabilities of joining of current cell to adjacent grains containing neighbour cells
        double[][] probs               = new double[neighb1S_number][work_item_number];
        
        // Sums of switch probabilities for certain number of neighbours 
        double[][] prob_sums           = new double[neighb1S_number+2][work_item_number];
        
        for(int cell_counter = 0; cell_counter < work_item_number; cell_counter++)
        {
            prob_sums[0][cell_counter] = (double)0.0;
            prob_sums[neighb1S_number+1][cell_counter] = (double)1.0;
        }
        
        // Cell surface calculation
        double cell_surface   = calcCellSurface(packing_type);
        
        // Cell volume calculation
        double cell_volume    = calcCellVolume(packing_type);        
        
        // Index of cell neighbour
        int neighb_id[]     = new int[1]; 
            neighb_id[0]    = -1;
        
        // Variable for testing
        int step_counter[]  = new int[6]; 
            step_counter[0] = step_counter[1] = step_counter[2] = -1;
            step_counter[3] = step_counter[4] = step_counter[5] = -1;
        
        // Factor necessary for calculation of heat influx: 
        // произведение площади границы элементов и величины временного шага, делённое на
        // произведение линейного размера элемента и его объёма
        double factor[] = new double[1];
        factor[0] = cell_surface*TIME_STEP/(CELL_SIZE*neighb1S_number*cell_volume);
        
        // Array of initial temperatures of cells (the last element of the array corresponds to 
        // temperature of outer neighbour cells: it is equal to the temperature of current central cell)
        double init_temprs[]     = new double[work_item_number+1];
        
        // Array of heat capacities of cells
        double heat_caps[]       = new double[work_item_number];
        
        // Array of heat conductivities of cells
        double heat_conds[]      = new double[work_item_number];
        
        // Array of heat expansion coefficients of cells
    //  double heat_exp_coeffs[] = new double[work_item_number];  
    
        // Array of densities of cells
        double densities[]       = new double[work_item_number];
        
        // Array of temperatures of cells
        double temperatures[]    = new double[work_item_number];
        
        // Array of heat influxes to cells from neighbours
        double heat_influxes[]   = new double[work_item_number];
        
        //---------------- Parameters for recrystallization ----------------------------------
        
        // Total number of grains 
        grain_number = specArrays.grain_number;
        
     //   if((cell_number_K - 2) % 3 == 0) grain_number = (cell_number_K - 2)/3 + 7;
     //   else                             grain_number = (cell_number_K - 2)/3 + 8;
        
        // initial indices of grains for each cell
        int[] init_grain_indices = new int[work_item_number];
        
        // final indices of grains for each cell
        int[] grain_indices = new int[work_item_number];
        
        // Index of current grain
        int grain_index = 1;
        
        // Array of orientation angles for all grains
        double[] grain_angles = new double[grain_number];
        
        // types of grains
        byte[] grain_types   = new byte[grain_number];
        
        // highest misorientation angle
        double[] angleHAGB = new double[1];
        angleHAGB[0]      = specArrays.angleLimitHAGB[0];//MAX_ORIENTATION_ANGLE;//
        
        // --- Arrays of maximal values of grain boundary mobilities ---
        // heat mobilities
        double[] heatMaxMobility = new double[grain_number];
        
        // dislocation mobilities
        double[] dislMaxMobility = new double[grain_number];
        
        // mechanical mobilities
        double[] mechMaxMobility = new double[grain_number];
        
        // Maximal boundary energies
        double[] maxBoundEnergy = new double[grain_number];
        
        // thermal energies of cells
        double thermal_energies[] = new double[work_item_number];
        
        // dislocation energies of cells
        double disl_energies[] = new double[work_item_number];
        
        // mechanical energies of cells
        double mech_energies[] = new double[work_item_number];
         
        // Indices of boundary cells
        int[] bound_cell_indices = specArrays.bound_cell_indices;
        
        //-------------------------------------------------------------------------
        
        // Printing of cell geometrical parameters
        System.out.println("cell size:    " + CELL_SIZE);
        System.out.println("cell surface: " + cell_surface);
        System.out.println("cell volume:  " + cell_volume);
        
        // single index of cell
        int cell_index_1D = -1;
        
        boolean inner_cell = false;
        
        // Setting of initial temperatures of outer cell
        init_temprs[work_item_number] = (double)0;
        
        // Setting of type of energy interaction of boundary cell with outer neighbour cell
        en_types[work_item_number]    = ADIABATIC_ADIABATIC_CELL;
        
      //  System.out.println("cell_number_I = "+cell_number_I);
      //  System.out.println("cell_number_J = "+cell_number_J);
      //  System.out.println("cell_number_K = "+cell_number_K);
            
        // Cycle on cell indices i, j, k (spatial cycle)
        for (int cell_index_K = 0; cell_index_K < cell_number_K; cell_index_K++)
        for (int cell_index_J = 0; cell_index_J < cell_number_J; cell_index_J++)
        for (int cell_index_I = 0; cell_index_I < cell_number_I; cell_index_I++)
        {
            inner_cell = true;
            
            // single index of current cell
         // cell_index_1D++;
            cell_index_1D = cell_number_I*cell_number_J*cell_index_K + cell_number_I*cell_index_J + cell_index_I;
            
            // Setting of indices of neighbours at 1st coordination sphere for cells with current index
            setNeighbours1S(cell_index_1D);
             
            // Current grain index
            grain_index                = specArrays.grainIndices[cell_index_1D];
            
            // initial index of grain containing cell
            init_grain_indices[cell_index_1D] = grain_index;
            
            // final index of grain containing cell
            grain_indices[cell_index_1D]      = grain_index;
            
            // Setting of temperature 
            init_temprs[cell_index_1D] = specArrays.initialTemperatures[cell_index_1D];
            
         // System.out.println("init_temprs["+cell_index_1D+"] = "+init_temprs[cell_index_1D]);
              
            // Setting of location type
            loc_types[cell_index_1D]   = specArrays.cellTypes[cell_index_1D];
              
            // Setting of energy type
            en_types[cell_index_1D]    = specArrays.grain_types[grain_index - 1];
              
            // Values of heat capacities of cells
            heat_caps[cell_index_1D]       = specArrays.specific_heat_capacity[grain_index-1];
            
            // Values of heat conductivities of cells
            heat_conds[cell_index_1D]      = specArrays.thermal_conductivity[grain_index-1];
              
            // Values of densities of cells
            densities[cell_index_1D]       = specArrays.density[grain_index-1];
             
            // type of grain containing cell
            grain_types[grain_index-1]     = specArrays.grain_types[grain_index - 1];
              
            // maximal value of grain boundary heat mobility
            heatMaxMobility[grain_index-1] = specArrays.maxMobility[grain_index-1];
        
            // maximal grain boundary energy
            maxBoundEnergy[grain_index-1]  = specArrays.energyHAGB[grain_index-1];
              
            // orientation angle for grain containing cell
            grain_angles[grain_index-1] = specArrays.grain_angles[grain_index-1][0];
            
            // highest misorientation angle for grain containing cell
         //   angleHAGB[grain_index-1]       = (double)45.0;
            
            // 1st, 2nd and 3rd indices of cell with current single index
            indices_I[cell_index_1D]       = cell_index_I;
            indices_J[cell_index_1D]       = cell_index_J;
            indices_K[cell_index_1D]       = cell_index_K;
            
            System.out.print("Cell # "+cell_index_1D+": gr.ind.= "+init_grain_indices[cell_index_1D]+"; T="+init_temprs[cell_index_1D]);
            System.out.print("; ht cap.= "+heat_caps[cell_index_1D]+": ht.cond= "+heat_conds[cell_index_1D]+"; dnst="+densities[cell_index_1D]);
            System.out.println("; max mob.= "+heatMaxMobility[grain_index-1]+": max.bnd.en= "+maxBoundEnergy[grain_index-1]+"; angle="+grain_angles[grain_index-1]);
        }
        
        for(cell_index_1D = cell_number; cell_index_1D < work_item_number; cell_index_1D++)
        {
            // Setting of indices of neighbours at 1st coordination sphere for cells with current index
            setNeighbours1S(cell_index_1D);
            
            // Setting of types of location of cells in a specimen
            loc_types[cell_index_1D]   = OUTER_CELL;
            
            // Setting of types of energy interaction with neighbours for cells in a specimen
            en_types[cell_index_1D]    = ADIABATIC_ADIABATIC_CELL;
            
            // Setting of initial temperatures of cells in a specimen
            init_temprs[cell_index_1D] = (double)0;//INITIAL_TEMPERATURE;//
            
            // Values of heat capacities of cells
            heat_caps[cell_index_1D]   = (double)1;
            
            // Values of heat conductivities of cells
            heat_conds[cell_index_1D]  = (double)1;
            
            // Values of heat expansion coefficients of cells
       //   heat_exp_coeffs[cell_index_1D] = (double)HEAT_EXPANSION_COEFF;
            
            // Values of densities of cells
            densities[cell_index_1D]   = (double)1;
            
            // Current grain index
            grain_index = grain_number;
                    
            // initial index of grain containing cell
            init_grain_indices[cell_index_1D] = grain_index;
            
            // final index of grain containing cell
            grain_indices[cell_index_1D]      = grain_index;
            
            // highest misorientation angle for grain containing cell
          //  angleHAGB[grain_index-1]       = (double)45.0;

            // orientation angle for grain containing cell
            grain_angles[grain_index-1]    = (double)0.0;
            
            // type of grain containing cell
            grain_types[grain_index-1]     = (byte)ADIABATIC_ADIABATIC_CELL;
            
            // maximal value of grain boundary heat mobility
            heatMaxMobility[grain_index-1] = (double)0.0;
        
            // maximal grain boundary energy
            maxBoundEnergy[grain_index-1]  = (double)0.0;
        }
        
        // Current boundary cell index
        int bound_cell_index;
        
        // Setting of temperatures for boundary cells
        for(int bound_cell_counter = 0; bound_cell_counter < bound_cell_indices.length; bound_cell_counter++)
        {
            bound_cell_index = bound_cell_indices[bound_cell_counter];
            
            // Setting of temperature for current boundary cell
            init_temprs[bound_cell_index] = specArrays.bound_cell_heat_energies[bound_cell_counter];
        }
        
        // Setting of types of energy interaction with neighbours for cells in a specimen
        en_types[work_item_number]    = ADIABATIC_ADIABATIC_CELL;
        
        // Setting of initial temperatures of cells in a specimen
        init_temprs[work_item_number] = (double)0;//INITIAL_TEMPERATURE;//
        
        // ---------- Pointers to arrays of cell parameters -------------
        // Pointer to array of initial temperatures of cells
        Pointer init_temprs_ptr     = Pointer.to(init_temprs);
        
        // Pointer to array of heat capacities of cells
        Pointer heat_caps_ptr       = Pointer.to(heat_caps);
        
        // Pointer to array of heat conductivities of cells
        Pointer heat_conds_ptr      = Pointer.to(heat_conds);  
        
        // Pointer to array of densities of cells
        Pointer densities_ptr       = Pointer.to(densities);
        
        // Pointer to array of energy types of cells
        Pointer en_types_ptr        = Pointer.to(en_types);
        
        // Pointers to arrays of indices of cell neighbours
        Pointer[] neighbours1S_ptr  = new Pointer[neighb1S_number];
        
        // Arrays of neighbour cells with corresponding index for all cells from specimen
        for (int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
            neighbours1S_ptr[neighb_counter] = Pointer.to(neighbours1S[neighb_counter]);
        
        // Pointer to array of cell temperatures calculated in the method
        Pointer temperatures_ptr    = Pointer.to(temperatures);
        
        // Pointer to array of cell heat influxes temperatures calculated in the method
        Pointer heat_influxes_ptr   = Pointer.to(heat_influxes);
        
        // Pointer to current index of cell neighbour
        Pointer neighb_id_ptr       = Pointer.to(neighb_id);
        
        // Pointer to factor necessary for calculation of heat influx
        Pointer factor_ptr          = Pointer.to(factor);
        
        // Pointer to variable for testing
        Pointer step_counter_ptr    = Pointer.to(step_counter);
        
        // Pointer to array of heat expansion coefficients of cells
    //    Pointer heat_exp_coeffs_ptr = Pointer.to(heat_exp_coeffs);
        
        // The platform, device type and device number that will be used
        final int  platformIndex = 0;
        final long deviceType    = CL_DEVICE_TYPE_GPU; // CL_DEVICE_TYPE_ALL; // 
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

        // Printing of the number of platforms
        for(int counter = 0; counter < numPlatformsArray.length; counter++)
          if(numPlatformsArray[counter]>0)
            System.out.println("The number of platforms: numPlatformsArray["+counter+"] = "+numPlatformsArray[counter]);
        
        // Platform ID is obtained
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        
        // IDs of available platforms are obtained
        clGetPlatformIDs(platforms.length, platforms, null);
        
        // The platform from the list is obtained
        cl_platform_id platform = platforms[platformIndex];
        
        // Printing of the platform ID
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
        
        // Printing of the number of devices at platforms
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
        
            // Printing of the device ID
            System.out.println("Device ID: "+device);
        }
        
        // Creation of the context for the selected devices
        cl_context context = clCreateContext(contextProperties, numDevices, devices, null, null, null);
        
        // Creation of the command queue for the selected device
     //   cl_command_queue commandQueue = clCreateCommandQueue(context, devices[0], 0, null);
        cl_command_queue commandQueue = clCreateCommandQueue(context, devices[0], 0, null);
       
        // Special ratios for calculation of heat influxes
     //   double ratio = (double)1.0/((INNER_CELL - ADIABATIC_TEMPERATURE_CELL)*(INNER_CELL - ADIABATIC_ADIABATIC_CELL));
     //   byte  ratio_2 = ADIABATIC_ADIABATIC_CELL - ADIABATIC_TEMPERATURE_CELL;
     
        // Step number
        long step_number = STEP_NUMBER;
        
        // Program text determining work of kernel
        String prog_string = "__kernel void sampleKernel\n"+
                // Kernel variables are: 
                // 1. array of initial temperatures of cells; 
                // 2. array of heat capacities of cells;
                // 3. array of heat conductivities of cells; 
                // 4. array of densities of cells;
                // 5-(neighb1S_number+4). arrays of indices of neighbour cells (neighb1S_number for each cell) for all cells;
                // neighb1S_number+ 5. array of energy types for all cells; 
                // neighb1S_number+ 6. array of calculated temperatures of cells;
                // neighb1S_number+ 7. array of heat influxes to cells; 
                // neighb1S_number+ 8. index of current neighbour cell
                // neighb1S_number+ 9. the value for calculation of cell temperature; 
                // neighb1S_number+10. the value for testing.
        "(            __global       double *init_temprs,     __global const double *heat_cap,       \n"+
        "             __global const double *heat_cond,       __global const double *densities,      \n"; 
        
        // Addition of variables responcible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string += 
        "             __global const int   *neighbours1S_"+neighb_counter+",\n";
        
        prog_string += 
        "             __global const char  *en_types,        __global double       *temperatures,   \n"+
        "             __global double       *heat_influxes,   __global int         *neighb_id,      \n"+
        "             __global double       *factor,          __global int         *step_counter)\n"+
        "{   \n"+
         // Index of current cell
        "    int cell_index = get_global_id(0);\n"+
        "    \n"+
     //   "    if(cell_index < "+cell_number+")\n"+
     //   "    { \n"+
        "      heat_influxes[cell_index] = \n";
        
        // Calculation of heat influx from neighbour cells to current cell
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          prog_string += 
        "                         (double)signbit(en_types[cell_index] - (double)"+ADIABATIC_ADIABATIC_CELL+")*"+
                                 "(double)signbit(en_types[neighb_id_"+neighb_counter+"] - (double)"+ADIABATIC_ADIABATIC_CELL+")*\n"+
        "                         (double)signbit(en_types[cell_index] - (double)"+ADIABATIC_TEMPERATURE_CELL+")*"+
                                 "(init_temprs[neighb_id_"+neighb_counter+"] - init_temprs[cell_index])";
          
          if(neighb_counter < neighb1S_number-1)
            prog_string +=  " +\n";
          else
            prog_string +=  ";\n";
          
          prog_string += 
        "        \n";
        }
        
        prog_string += 
//        "      __syncthreads();\n"+
        "      \n"+
                // Calculation of new temperature of cell according to heat influx
        "      temperatures[cell_index] = init_temprs[cell_index] + heat_influxes[cell_index] * heat_cond[cell_index] * factor[0]/(heat_cap[cell_index] * densities[cell_index]);\n"+
//        "      __syncthreads();\n"+
        "      \n"+
                // New temperature value becomes the initial value at next time step
        "      init_temprs[cell_index] = temperatures[cell_index];\n"+
//        "      __syncthreads();\n"+
     //   "    } \n"+
        "    \n"+
                // Test variables are used to keep the number of work-items and the number of work-groups.
        "    step_counter[0] = get_global_size(0);\n"+
        "    step_counter[3] = get_num_groups(0);// get_local_size(0);// \n"+
        "}";
        
        // Printing of the code text to the file
        try
        {
            String file_name   = "./user/paral_codes/paral_code.txt";
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
        
        // Obtaining of current time
        Date date_0 = new Date();
        
        // Allocate the memory objects for the input- and output data        
        // Creation of the memory objects for arrays of initial temperatures, heat capacities, 
        // heat conductivities and densities of cells
        cl_mem memObject_init_temprs    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, init_temprs_ptr, null);
         
        cl_mem memObject_heat_caps      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, heat_caps_ptr, null);
        
        cl_mem memObject_heat_conds     = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, heat_conds_ptr, null);
        
        cl_mem memObject_densities      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, densities_ptr, null);
        
        // Creation of the memory object for indices of neighbour cells
        cl_mem[] memObject_neighbours1S = new cl_mem[neighb1S_number];
        
        for (int point_counter = 0; point_counter<neighb1S_number; point_counter++)
            memObject_neighbours1S[point_counter]  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int*work_item_number, neighbours1S_ptr[point_counter], null);
        
        // Creation of the memory object for energy types of cells
        cl_mem memObject_en_types       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * work_item_number, en_types_ptr, null);
        
        // Creation of the memory object for temperatures of cells
        cl_mem memObject_temperatures   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, temperatures_ptr, null);
        
        // Creation of the memory object for heat influxes to cells
        cl_mem memObject_heat_influxes  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, heat_influxes_ptr, null);
        
        // Creation of the memory object for index of current neighbour cell
        cl_mem memObject_neighb_id      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int, neighb_id_ptr, null);
        
        // Creation of the memory object for the value necessary for calculation of cell temperature
        cl_mem memObject_factor         = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double, factor_ptr, null);
        
        // Creation of the memory object for test value
        cl_mem memObject_step_counter   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    6*Sizeof.cl_int, step_counter_ptr, null);
        
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program  = clCreateProgramWithSource(context, 1, new String[]{prog_string}, new long[]{2*8192} /* null */, null);
        
        // Building of the program
        clBuildProgram(program, 0, null, null, null, null);
        
        // Creation of the kernel
        cl_kernel kernel = clCreateKernel(program, "sampleKernel", null);
        
        // Setting of the argument values to the kernel.
        clSetKernelArg(kernel,  0, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
        clSetKernelArg(kernel,  1, Sizeof.cl_mem, Pointer.to(memObject_heat_caps));
        clSetKernelArg(kernel,  2, Sizeof.cl_mem, Pointer.to(memObject_heat_conds));
        clSetKernelArg(kernel,  3, Sizeof.cl_mem, Pointer.to(memObject_densities));
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
            clSetKernelArg(kernel,  neighb_counter+4, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
        
        clSetKernelArg(kernel, neighb1S_number+4, Sizeof.cl_mem, Pointer.to(memObject_en_types));
        clSetKernelArg(kernel, neighb1S_number+5, Sizeof.cl_mem, Pointer.to(memObject_temperatures));
        clSetKernelArg(kernel, neighb1S_number+6, Sizeof.cl_mem, Pointer.to(memObject_heat_influxes));
        
        clSetKernelArg(kernel, neighb1S_number+7, Sizeof.cl_mem, Pointer.to(memObject_neighb_id));
        clSetKernelArg(kernel, neighb1S_number+8, Sizeof.cl_mem, Pointer.to(memObject_factor));
        clSetKernelArg(kernel, neighb1S_number+9, Sizeof.cl_mem, Pointer.to(memObject_step_counter));
        
        // Setting of the work-item sizes at each dimension 
        long global_work_size[] = new long[]{work_item_number};
        long local_work_size[]  = new long[]{work_group_size};
        
     //   long global_work_size[] = new long[]{cell_number_I, cell_number_J, cell_number_K};
     //   long local_work_size[]  = new long[]{LOCAL_CELL_NUMBER_I, LOCAL_CELL_NUMBER_J, LOCAL_CELL_NUMBER_K};
        
        // Printing of the number of time steps
        System.out.println();
        System.out.println("Number of time steps: "+step_number);
        
        // Переменные для вычисления времени, необходимого для выполнения программы
        Date start_date = new Date();
        Date inter_date = new Date();
        Date inter_date_1 = new Date();

        long time_period_exec = 0;
        long time_period_read = 0;
        
        // Current cell coordinates
        double[] coords = new double[3];
        
        // Current cell grain index
        grain_index = 1;
        
        // Boolean value regulating output data recording
        boolean write_to_file; 
        
        // Поток для записи в файл
        BufferedWriter bw;
        
        // Number of files with output data
        int file_number = FILE_NUMBER;
        
        // Name of file with output data
        String file_name;
        
        // Counter of output files 
        int file_counter = 0;
        
        // String with zeros
        String zeros = "";
        
        // Порядок общего числа временных шагов
        double max_power = Math.floor(Math.log10(1.0*step_number));
        
        // Порядок номера текущего временного шага
        double cur_power;
        
        // Execution of the kernel at each time step
        for(int stepCounter = 1; stepCounter <= step_number; stepCounter++)
        {
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the kernel at current time step
            clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Calculation of the time period of the kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Boolean variable determining recording to file:
            // данные записываются в файлы через равные промежутки времени
          //  write_to_file = (stepCounter%(step_number/file_number) == 0)|(stepCounter == step_number); 
            write_to_file = ((stepCounter*file_number)%step_number < file_number);
            
            if(write_to_file | stepCounter == step_number)
            {
                // Obtaining of current time
                inter_date = new Date();
                
                System.out.println("Time step # "+stepCounter);
                
                // Reading of the output data (temperatures and heat influxes) from a buffer object to host memory.
                clEnqueueReadBuffer(commandQueue, memObject_temperatures, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, temperatures_ptr, 0, null, null);
        
                clEnqueueReadBuffer(commandQueue, memObject_heat_influxes, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, heat_influxes_ptr, 0, null, null);
                
                // Obtaining of current time
                inter_date_1 = new Date();
                
                // Calculation of the time period of reading of the output data
                time_period_read += inter_date_1.getTime()- inter_date.getTime();
                
                // Recording of output data to the file
                try
                {
                  if(write_to_file)
                  {
                    // Counter of current file
                    file_counter++;
                    
                    // Определение числа нулей перед номером шага в имени выходного файла
                    zeros = "";
                    cur_power = Math.floor(Math.log10(1.0*stepCounter));
                    
                    for(int zero_counter = 0; zero_counter < max_power-cur_power; zero_counter++)
                        zeros = zeros +"0";
                    
                    file_name = "./task_db/paral_heating/paral_heating_"+zeros+stepCounter+"_jocl.res";
                    
                    // The name of file for recording is determined
                    bw = new BufferedWriter(new FileWriter(file_name));
                    
                    // TEST
                    System.out.println("Size of array of cell temperatures: "+temperatures.length);
                    System.out.println("T[0] = "+temperatures[0]);
                //    System.out.println("T["+(cell_number-1)+"] = "+temperatures[cell_number-1]);
                //    System.out.println("T["+cell_number+"] = "+temperatures[cell_number]);
                    System.out.println("T["+(temperatures.length-1)+"] = "+temperatures[temperatures.length-1]);
                    
                    // Printing of parameters of cells to the file
                    for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
                //    for(int cell_counter = 0; cell_counter < work_item_number; cell_counter++)
                    {
                        // Calculation of coordinates of current cell
                        coords = calcCoordinates(indices_I[cell_counter], indices_J[cell_counter], indices_K[cell_counter]);
                        
                        bw.write(en_types[cell_counter]+" "+loc_types[cell_counter]+" "+grain_index+" "+
                                 coords[0]+" "+coords[1]+" "+coords[2]+" "+temperatures[cell_counter]+" 0 0 0 0 0 0 0 0\n");
                        bw.flush();
                    }
                    
                    bw.close();
            
                    System.out.println();
                    System.out.println("File # "+(file_counter - 1)+" ("+file_name+") is created.");
                  }
                  
                  if(stepCounter == step_number)
                  {
                      System.out.println("===========================================================");
                      
                      for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
                    //  for(int cell_counter = 0; cell_counter < work_item_number; cell_counter++)
                      {
                        if(indices_I[cell_counter] == cell_number_I/2)
                        if(indices_J[cell_counter] == cell_number_J/2)
                        {
                          // Printing of parameters of cells to screen
                          System.out.println("Cell # "+cell_counter+"; ["+indices_I[cell_counter]+
                                             ", "+indices_J[cell_counter]+", "+indices_K[cell_counter]+"]; type = "+en_types[cell_counter]+
                                             "; temperature = "+temperatures[cell_counter]+"; heat influx = "+heat_influxes[cell_counter]);
                        }
                      }
                      
                      System.out.println("===========================================================");
                  }
                }
                catch(IOException io_exc)
                {
                    System.out.println("IOException: " + io_exc);
                }
            }
        }
        
        // Current time
        inter_date = new Date();
        
        // Reading of the output data
        // Enqueued commands are read from a buffer object to host memory.
        clEnqueueReadBuffer(commandQueue, memObject_step_counter, CL_TRUE, 0, 6*Sizeof.cl_int, step_counter_ptr, 0, null, null);
        
        // Current time
        inter_date_1 = new Date();
        
        // Time period of reading of the output data
        time_period_read += inter_date_1.getTime()- inter_date.getTime();
        
        System.out.println("Period of parallel code execution:      "+time_period_exec+" ms.");
        System.out.println("Period of data reading after execution: "+time_period_read+" ms.");
        System.out.println();
     // System.out.println("Global work size:  " + global_work_size[0]);//+" "+global_work_size[1]+" "+global_work_size[2]);
        System.out.println("Global work size:  " + step_counter[0]);//+" "+ step_counter[1]+" "+ step_counter[2]);
        System.out.println("Local work size:   " + local_work_size[0]);//+" "+local_work_size[1]+" "+local_work_size[2]);
        System.out.println("Work group number: " + step_counter[3]);//+" "+ step_counter[4]+" "+ step_counter[5]);
        
        // Current time
        inter_date_1 = new Date();
        
        // Release of all memory objects 
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
        
        // Current time
        Date finish_date = new Date();
        
        // Time period of parallel code execution
        double time_period;
        
        time_period = finish_date.getTime()- date_0.getTime();
        System.out.println("Total period of parallel code execution:  "+time_period+" ms.");
        System.out.println("====================END=====================\n");
    }
    
    /** The method simulates heat propagation and heat recrystallization in cellular automaton basing on OpenCL.
     */
    public static void parallelCalcHeatRecryst()
    {
        // Total number of cells
        cell_number   = specArrays.cell_number;// cell_number_I*cell_number_J*cell_number_K;// 
        
        // Number of cells along axes
        cell_number_I = specArrays.cell_number_X;
        cell_number_J = specArrays.cell_number_Y;
        cell_number_K = specArrays.cell_number_Z;
        
        System.out.println("===================START====================");
        System.out.println("Number of cells along axis X: "+cell_number_I);
        System.out.println("Number of cells along axis Y: "+cell_number_J);
        System.out.println("Number of cells along axis Z: "+cell_number_K);
        
        // Total number of grains 
        grain_number = specArrays.grain_number;
        
        if(packing_type == SIMPLE_CUBIC_PACKING)
          System.out.println("Type of cell packing:         SCP (simple cubic packing)");
        
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
          System.out.println("Type of cell packing:         HCP (hexagonal close packing)");
        
        System.out.println("Number of time steps:         "+STEP_NUMBER);
        System.out.println("Number of created files:      "+FILE_NUMBER);
        System.out.println("============================================");
        System.out.println("Total number of cells:        "+cell_number);
        
        // Логарифм по основанию 2 общего числа элементов
        double log2_cell_number = Math.log(cell_number)/Math.log(2.0);
        
        if(Math.floor(log2_cell_number) < log2_cell_number)
          log2_cell_number = Math.floor(log2_cell_number);
        
        // Step number
        long step_number = STEP_NUMBER;
        
        // Размер work-group (число элементов, обрабатываемых в рамках одной work-group)
        int work_group_size = Math.min(LOCAL_CELL_NUMBER, (int)Math.pow(2, (int)log2_cell_number))/2;
        
        // Общее число work-group
        int work_group_number = Math.max(work_group_size, cell_number)/work_group_size;
        
        if(cell_number%work_group_size != 0)
            work_group_number++;
        
        // Общее число элементов должно быть кратным размеру work-group:
        // каждый work-item обрабатывает один элемент
        work_item_number = work_group_size*work_group_number;
        
        System.out.println("Total number of work-items:   "+work_item_number);
        System.out.println("Size of work-group:           "+work_group_size);
        System.out.println("Number of work-groups:        "+work_group_number);
        System.out.println("============================================");
        
        // Array of cell single indices i, j, k (for recording of results)
        int[] indices_I = new int[cell_number];
        int[] indices_J = new int[cell_number];
        int[] indices_K = new int[cell_number];
        
        // Array of cell location types
        byte[] loc_types = new byte[work_item_number];
        
        // Array of cell location types at next time step
        byte[] new_loc_types = new byte[work_item_number];
        
        // Array of cell energy types (the last element of the array corresponds to energy type of outer cells)
        byte[] en_types  = new byte[work_item_number+1];
        
        // Array of new energy types of cells (the last element of the array corresponds to energy type of outer cells)
        byte[] new_en_types  = new byte[work_item_number+1];
        
        // Single indices of neighbours of all cells
        neighbours1S = new int[neighb1S_number][work_item_number];
        
        // Misorientation angles for grains containing neighbour cells divided by maximal angle of misorientation
        double[][] spec_angle_diff     = new double[neighb1S_number][work_item_number];
        
        // Maximal mobilities at grain boundaries
        double[][] neighb_heat_max_mob = new double[neighb1S_number][work_item_number];
        
        // Specific energies of grain boundaries with neighbour cells
        double[][] spec_bound_energies = new double[neighb1S_number][work_item_number];
         
        // Energies of grain boundaries with neighbour cells divided by kT
        double[][] bound_energies      = new double[neighb1S_number][work_item_number];
        
        // Energies of grain boundaries with neighbour cells divided by kT
        double[][] bound_velocities    = new double[neighb1S_number][work_item_number];
        
        // Probabilities of joining of current cell to adjacent grains containing neighbour cells
        double[][] probs               = new double[neighb1S_number][work_item_number];
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number;  neighb_counter++)
        for(int cell_counter = 0;   cell_counter   < work_item_number; cell_counter++)
            probs[neighb_counter][cell_counter] = (double)0.0;
        
        // Sums of switch probabilities for certain number of neighbours 
        double[][] prob_sums           = new double[neighb1S_number+2][work_item_number];
        
        // Array of 0 and 1 responsible for possibility of cell transition to adjacent grain
        byte[][] poss_switches        = new byte[neighb1S_number][work_item_number];
        
        // Array of random numbers
        double[] rand_nums             = new double[work_item_number];
        
        for(int cell_counter = 0; cell_counter < work_item_number; cell_counter++)
        {
            prob_sums[0][cell_counter] = (double)0.0;
            prob_sums[neighb1S_number+1][cell_counter] = (double)1.0;
            rand_nums[cell_counter] = (double)Math.random();
        }
        
        // Cell surface calculation
        double cell_surface   = calcCellSurface(packing_type);
        
        // Cell volume calculation
        double cell_volume    = calcCellVolume(packing_type);        
        
        // Index of cell neighbour
        int neighb_id[]     = new int[1]; 
            neighb_id[0]    = -1;
        
        // Variable for testing
        int step_counter[]  = new int[6]; 
            step_counter[0] = step_counter[1] = step_counter[2] = -1;
            step_counter[3] = step_counter[4] = step_counter[5] = -1;
        
        // Factor necessary for calculation of heat influx: 
        // произведение площади границы элементов и величины временного шага, делённое на
        // произведение линейного размера элемента и его объёма
        double factor[] = new double[1];
        factor[0] = (double)0.5*cell_surface*TIME_STEP/(CELL_SIZE*neighb1S_number*cell_volume);
        
        // Array of initial temperatures of cells (the last element of the array corresponds to 
        // temperature of outer neighbour cells: it is equal to the temperature of current central cell)
        double init_temprs[]     = new double[work_item_number+1];
        
        // Array of minimal temperatures of boundary cells
        double bound_cell_min_temprs[] = new double[work_item_number + 1];
        double bound_cell_max_temprs[] = new double[work_item_number + 1];
        
        // Array of heat capacities of cells
        double heat_caps[]       = new double[work_item_number];
        
        // Array of heat conductivities of cells
        double heat_conds[]      = new double[work_item_number];
        
        // Array of heat expansion coefficients of cells
        double heat_exp_coeffs[] = new double[grain_number];
        
        // Array of Young modules of grains
        double mod_elast[]       = new double[grain_number];
        
        // Array of Young modules of transformed cells
        double new_mod_elast[]   = new double[grain_number];
        
        // Array of Young modules of cells
        double cell_mod_elast[] = new double[work_item_number];
        
        // Array of shear modules of cells
        double mod_shear[]       = new double[grain_number];
        
        // array of lattice parameters of grains
        double lattice_parameters[] = new double[grain_number];
        
        // array of initial defect densities of cells
        double init_def_densities[] = new double[work_item_number];
        
        // array of changes of defect densities of cells
        double def_density_changes[] = new double[work_item_number];
        
        // array of relative dissipation energy in comparison with total mechanical energy
        double rel_diss_energies[] = new double[work_item_number];
        
        // array of relative changes of dissipation energy in comparison with current influx of total mechanical energy
        double rel_diss_en_influxes[] = new double[work_item_number];
        
        // array of relative changes of dissipation energy in comparison with total mechanical energy
        double rel_diss_en_changes[] = new double[work_item_number];
        
        // array of dissipation coefficients for grains
        double coeff_diss[] = new double[grain_number];
        
        // array of final defect densities of cells
        double final_def_densities[] = new double[work_item_number];
        
        // Array of initial heat strains of cells
        double init_heat_strains[] = new double[work_item_number];
        
        // Array of heat strains of cells
        double heat_strains[]    = new double[work_item_number];
        
        // Array of heat stresses of cells
        double heat_stresses[]    = new double[work_item_number];
    
        // Array of densities of cells
        double densities[]       = new double[work_item_number];
        
        // Array of temperatures of cells
        double temperatures[]    = new double[work_item_number];
        
        // Array of heat influxes to cells from neighbours
        double heat_influxes[]   = new double[work_item_number];
        
        // Array of mechanical energy influxes to cells from neighbours
        double mech_influxes[]   = new double[work_item_number];
        
        // Array of scalar stresses at cell boundaries
        double stress[][] = new double[neighb1S_number][work_item_number];
        
        for(int bound_counter = 0; bound_counter < neighb1S_number;  bound_counter++)
        for(int cell_counter = 0;  cell_counter  < work_item_number; cell_counter++)
          stress[bound_counter][cell_counter] = (double)0.0;
        
        // Array of coordinates of stress vectors for all cells
        double stresses_X[] = new double[work_item_number];
        double stresses_Y[] = new double[work_item_number];
        double stresses_Z[] = new double[work_item_number];
        
        //---------------- Parameters for simulation of recrystallization ----------------------------------
        
        // Minimal number of neighbour cells in adjacent grain
        // necessary for joining of cell to this adjacent grain
        min_neighbours_number = specArrays.min_neighbours_number;
        
        // initial indices of grains for each cell
        int[] init_grain_indices = new int[work_item_number];
        
        // final indices of grains for each cell
        int[] grain_indices = new int[work_item_number];
        
        // Index of current grain
        int grain_index = 0;
        
        // types of grains
        byte[] grain_types   = new byte[grain_number];
        
        // types of grains according to its role in recrystallization process: initial and final for time step
        byte[] init_grain_rec_types   = new byte[grain_number];
        byte[] grain_rec_types        = new byte[grain_number];
        
        // Indices of grains, from which twin grains grow
        int[] root_grain_indices = new int[grain_number];
        
        // Indices of new grains where embryos of twin grains appear
        int[] twin_emb_root_gr_indices = new int[work_item_number];
        
        // Array of 1st orientation angles for all grains
        double[] grain_angles_1 = new double[grain_number];
        
        // Array of 2nd orientation angles for all grains
        double[] grain_angles_2 = new double[grain_number];
        
        // Array of 3rd orientation angles for all grains
        double[] grain_angles_3 = new double[grain_number];
        
        // Array of highest misorientation angles for all grains
        double[] max_grain_angles = new double[grain_number];
        
        // highest misorientation angle
        double[] angleHAGB = new double[1];
        angleHAGB[0]      = specArrays.angleLimitHAGB[0];//MAX_ORIENTATION_ANGLE;//
        
        // --- Arrays of maximal values of grain boundary mobilities ---
        // heat mobilities
        double[] heatMaxMobility = new double[grain_number];
        
        // dislocation mobilities
        double[] dislMaxMobility = new double[grain_number];
        
        // mechanical mobilities
        double[] mechMaxMobility = new double[grain_number];
        
        // Maximal boundary energies
        double[] maxBoundEnergy = new double[grain_number];
        
        // dislocation energies of grains
        double disl_energies[] = new double[grain_number];
        
        // Indices of boundary cells
        int[] bound_cell_indices = new int[specArrays.bound_cell_number];
        
        for(int bound_cell_counter = 0; bound_cell_counter < specArrays.bound_cell_number; bound_cell_counter++)
          bound_cell_indices[bound_cell_counter] = specArrays.bound_cell_indices[bound_cell_counter];
        
        // Phonon portions of grains
        double[] phonon_portions  = new double[grain_number];
        
        // the low temperature of recrystallization (generation of new grains)
        double[] low_tempr_recryst   = new double[grain_number];
        
        // the high temperature of recrystallization (generation of new grains)
        double[] high_tempr_recryst  = new double[grain_number];
        
        // the low temperature of twinning
        double[] low_tempr_twinning  = new double[grain_number];
        
        // the high temperature of twinning
        double[] high_tempr_twinning = new double[grain_number];
        
        // the maximal probability of recrystallization (generation of new grains)
        double[] max_prob_recryst    = new double[grain_number];
        
        // the maximal probability of twinning
        double[] max_prob_twinning   = new double[grain_number];
        
        // minimal density of dislocations
        double[] min_disl_density    = new double[grain_number];
        
        // minimal energy of dislocations
        double[] min_disl_energies    = new double[grain_number];
        
        // the array of probabilities of recrystallization (generation of new grains)
        double[] prob_new_grain      = new double[work_item_number];
        
        // the array of probabilities of twinning
        double[] prob_twinning       = new double[work_item_number];
        
        // Presences of new grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
        byte[] new_grain_embryos    = new byte[work_item_number];
        
        // Presences of twin grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
        byte[] twin_grain_embryos   = new byte[work_item_number];
        
        // Lengths of lattice vectors for grains
        double[] lattice_vector_A_lengths = new double[grain_number];
        double[] lattice_vector_B_lengths = new double[grain_number];
        double[] lattice_vector_C_lengths = new double[grain_number];
        
        // Angles between lattice vectors for grains
        double[] lattice_angles_vecA_vecB = new double[grain_number];
        double[] lattice_angles_vecB_vecC = new double[grain_number];
        double[] lattice_angles_vecC_vecA = new double[grain_number];
        
        // Coefficients of lattice anisotropy for grains
        double[] lattice_anis_coeff       = new double[grain_number];
        
        // coordinates of lattice vectors for grains
        lattice_vectors        = new double[9][grain_number];
        
        //---------------- Parameters for simulation of mechanical loading ----------------------------------
        // Array of initial mechanical energies of cells
        double init_mech_energies[] = new double[work_item_number + 1];
        init_mech_energies[work_item_number] = 0;
        
        // Array of mechanical energies of cells
        double mech_energies[]    = new double[work_item_number + 1];
        mech_energies[work_item_number] = 0;
        
        // Array of initial dissipation energies of cells
        double init_diss_energies[] = new double[work_item_number];
        
        // Array of dissipation energies of cells
        double diss_energies[]    = new double[work_item_number];
        
        // Array of influxes of dissipation energy to cells
        double diss_en_influxes[] = new double[work_item_number];
        
        /** Parameters of boundary cells */
        // Array of influxes of specific mechanical energy to boundary cells per time step
        double bound_mech_influxes[] = new double[work_item_number];
        
        // Array of minimal influxes of specific mechanical energy to boundary cells per time step
        double bound_min_mech_influxes[] = new double[work_item_number];
        
        // Array of maximal influxes of specific mechanical energy to boundary cells per time step
        double bound_max_mech_influxes[] = new double[work_item_number];
        
        // Array of types of boundary conditions: 
        // 0 - constant loading during all numerical experiment;
        // 1 - cycle loading: constant loading during first time period, no loading during second period;
        // 2 - loading according to periodic time function;
        // 3 - cycle loading: constant loading during first time period, constant loading with opposite sign during second period.
        byte  bound_cell_time_function_types[] = new byte [work_item_number];
        
        // Array of time periods of loading
        double bound_cell_load_time_portions[]  = new double[work_item_number];
        
        // Array of time periods when loading is absent or it has opposite sign
        double bound_cell_relax_time_portions[] = new double[work_item_number];
        
        // Array of force moments for all cells
        VectorR3[] summary_force_moment = new VectorR3[cell_number];
        //-------------------------------------------------------------------------
        
        // Printing of cell geometrical parameters
        System.out.println("cell size:    " + CELL_SIZE);
        System.out.println("cell surface: " + cell_surface);
        System.out.println("cell volume:  " + cell_volume);
        System.out.println("-----------------------------------------");
        System.out.println("Grain number: " + grain_number);
        
        // single index of cell
        int cell_index_1D = -1;
        
      //  boolean inner_cell = false;
        
        // Setting of initial temperatures of outer cell
        init_temprs[work_item_number] = (double)0;
        
        // Setting of type of energy interaction of boundary cell with outer neighbour cell
        en_types[work_item_number]    = ADIABATIC_ADIABATIC_CELL;
        new_en_types[work_item_number]= ADIABATIC_ADIABATIC_CELL;
        
        // 3 arrays of coordinates X, Y, Z of cells
        cell_coords = new double[3][work_item_number+1];
        
        // current cell coordinates
        double[] coords = new double[3];
        
        // 3 arrays of coordinates X, Y, Z of force moments of cells
        double[][] force_moments = new double[3][work_item_number];
        
        // Array of absolute values of force moments of cells
        double[] abs_mom = new double[work_item_number];
        
        // Arrays of components of angle velocities of cells
        double[] angle_velocity_X   = new double[work_item_number];
        double[] angle_velocity_Y   = new double[work_item_number];
        double[] angle_velocity_Z   = new double[work_item_number];
               
        // Array of absolute values of angle velocities of cells
        double[] abs_angle_velocity = new double[work_item_number];
        
        // Arrays of components of torsion angles of cells
        double[] torsion_angle_X    = new double[work_item_number];
        double[] torsion_angle_Y    = new double[work_item_number];
        double[] torsion_angle_Z    = new double[work_item_number];
               
        // Array of absolute values of torsion angles of cells
        double[] abs_torsion_angle  = new double[work_item_number];
        
        // The number, which determines whether the cell become cracked
        int[] crack_sum = new int[work_item_number];
        
      //  System.out.println("cell_number_I = "+cell_number_I);
      //  System.out.println("cell_number_J = "+cell_number_J);
      //  System.out.println("cell_number_K = "+cell_number_K);
      
        // array of values for crack generation
        double[] crack_values = new double[work_item_number];
        
        // Array with the variable responsible for simulation of crack generation
        byte[] simulate_cracks = new byte[1];
        simulate_cracks[0] = specArrays.simulate_cracks;
      
        // Setting of properties of initial grains
        for(int grain_counter = 0; grain_counter < specArrays.init_grain_number; grain_counter++)
        {
          // type of grain
          grain_types[grain_counter]     = specArrays.grain_types[grain_counter];
            
          // type of grain according to its role in recrystallization process: initial and final
          grain_rec_types[grain_counter]      = specArrays.grain_rec_types[grain_counter];
          init_grain_rec_types[grain_counter] = specArrays.grain_rec_types[grain_counter];
          
          // 1st orientation angle of grain
          grain_angles_1[grain_counter]       = specArrays.grain_angles[grain_counter][0];
          
          // 2nd orientation angle of grain
          grain_angles_2[grain_counter]       = specArrays.grain_angles[grain_counter][1];
          
          // 3rd orientation angle of grain
          grain_angles_3[grain_counter]       = specArrays.grain_angles[grain_counter][2];
          
          // maximal misorientation angle
          max_grain_angles[grain_counter]     = specArrays.angleLimitHAGB[grain_counter];
          
          // array of dissipation coefficients for grains
          coeff_diss[grain_counter] = specArrays.torsion_energy_coeff[grain_counter];
        }
        
        // Setting of properties of new grains
        for(int grain_counter = specArrays.init_grain_number; grain_counter < grain_number; grain_counter++)
        {
          // type of new grain
          grain_types[grain_counter]     = (byte)ADIABATIC_ADIABATIC_CELL;
            
          // type of new grain according to its role in recrystallization process: initial and final
          grain_rec_types[grain_counter]      = NEW_GRAIN;
          init_grain_rec_types[grain_counter] = NEW_GRAIN;
          
          // 1st, 2nd and 3rd orientation angle of grain
          grain_angles_1[grain_counter]       = (double)0.0;
          grain_angles_2[grain_counter]       = (double)0.0;
          grain_angles_3[grain_counter]       = (double)0.0;
          
          // maximal misorientation angle
          max_grain_angles[grain_counter]     = (double)0.0;
          
          // array of dissipation coefficients for grains
          coeff_diss[grain_counter]           = (double)0.0;
        }
        
        // Maximal values of cell coordinates 
        double max_coord_X = (double)-1.0E300;
        double max_coord_Y = (double)-1.0E300;
        double max_coord_Z = (double)-1.0E300;
        
        // Minimal values of cell coordinates 
        double min_coord_X = (double)1.0E300;
        double min_coord_Y = (double)1.0E300;
        double min_coord_Z = (double)1.0E300;
        
        // Cycle on cell indices i, j, k (spatial cycle)
        for (int cell_index_K = 0; cell_index_K < cell_number_K; cell_index_K++)
        for (int cell_index_J = 0; cell_index_J < cell_number_J; cell_index_J++)
        for (int cell_index_I = 0; cell_index_I < cell_number_I; cell_index_I++)
        {
          //  inner_cell = true;
            
            // single index of current cell
         // cell_index_1D++;
            cell_index_1D = cell_number_I*cell_number_J*cell_index_K + cell_number_I*cell_index_J + cell_index_I;
            
            // Setting of indices of neighbours at 1st coordination sphere for cells with current index
            setNeighbours1S(cell_index_1D);
             
            // Current grain index
            grain_index                = specArrays.grainIndices[cell_index_1D];
            
            // initial index of grain containing cell
            init_grain_indices[cell_index_1D] = grain_index;
            
            // final index of grain containing cell
            grain_indices[cell_index_1D]      = grain_index;
            
            // Setting of temperature 
            init_temprs[cell_index_1D] = specArrays.initialTemperatures[cell_index_1D];
            
            // Setting of minimal temperature for boundary cell
            bound_cell_min_temprs[cell_index_1D] = (double)0;
            
            // Setting of maximal temperature for boundary cell
            bound_cell_max_temprs[cell_index_1D] = (double)0;
            
           // System.out.println("init_temprs["+cell_index_1D+"] = "+init_temprs[cell_index_1D]);
         
            // Setting of mechanical energies of cells
            mech_energies[cell_index_1D]      = specArrays.initialMechEnergies[cell_index_1D];
            init_mech_energies[cell_index_1D] = mech_energies[cell_index_1D];
            
            // Setting of values of dissipation energies of cells
            init_diss_energies[cell_index_1D] = (double)0;
            diss_energies[cell_index_1D]      = (double)0;
            diss_en_influxes[cell_index_1D]   = (double)0;
            
            // Setting of minimal influx of specific mechanical energy to boundary cell
            bound_min_mech_influxes[cell_index_1D] = (double)0;
            
            // Setting of maximal influx of specific mechanical energy to boundary cell
            bound_max_mech_influxes[cell_index_1D] = (double)0;
            
            // Array of types of boundary conditions
            bound_cell_time_function_types[cell_index_1D] = (byte) 0;
        
            // Array of time periods (relative to total time period) of loading
            bound_cell_load_time_portions[cell_index_1D]  = (double)0;
        
            // Array of time periods (relative to total time period) when loading is absent or it has opposite sign
            bound_cell_relax_time_portions[cell_index_1D] = (double)0;
            
            // Setting of location type
            loc_types[cell_index_1D]   = specArrays.cellTypes[cell_index_1D];
            
            // Top cell is located at specimen boundary with outer space
            if(cell_index_K == 1)
            if(cell_index_I > 2 & cell_index_I < cell_number_I - 3 &
               cell_index_J > 2 & cell_index_J < cell_number_J - 3)
              loc_types[cell_index_1D] = BOUNDARY_CELL;
            
            // Setting of location type at next time step
            new_loc_types[cell_index_1D] = specArrays.cellTypes[cell_index_1D];
            
            // Setting of energy type for cell
            en_types[cell_index_1D]      = specArrays.grain_types[grain_index - 1];
            new_en_types[cell_index_1D]  = specArrays.grain_types[grain_index - 1];
            
            // Setting of energy type for grain containing cell
            grain_types[grain_index - 1] = specArrays.grain_types[grain_index - 1];
            
            if(en_types[cell_index_1D] == LAYER_CELL)
            {
              en_types[cell_index_1D]      = INNER_CELL;
              new_en_types[cell_index_1D]  = INNER_CELL;
              grain_types[grain_index - 1] = INNER_CELL;
            }
              
            // Values of heat capacities of cells
            heat_caps[cell_index_1D]       = specArrays.specific_heat_capacity[grain_index-1];
            
            // Values of heat conductivities of cells
            heat_conds[cell_index_1D]      = specArrays.thermal_conductivity[grain_index-1];
              
            // Values of densities of cells
            densities[cell_index_1D]       = specArrays.density[grain_index-1];
             
            // type of grain according to its role in recrystallization process: initial and final
            grain_rec_types[grain_index-1]      = specArrays.grain_rec_types[grain_index - 1];
            init_grain_rec_types[grain_index-1] = specArrays.grain_rec_types[grain_index - 1];
            
            // Indices of grains, from which twinning grains grow
            root_grain_indices[grain_index-1] = specArrays.root_grain_indices[grain_index - 1];
            
            // Indices of new grains where embryos of twin grains appear
            twin_emb_root_gr_indices[cell_index_1D] = specArrays.root_grain_indices[grain_index - 1];
            
            // maximal value of grain boundary heat mobility
            heatMaxMobility[grain_index-1] = specArrays.maxMobility[grain_index-1];
            
            // dislocation mobilities
            dislMaxMobility[grain_index-1] = specArrays.dislMaxMobility[grain_index-1];
        
            // mechanical mobilities
            mechMaxMobility[grain_index-1] = specArrays.mechMaxMobility[grain_index-1];
        
            // maximal grain boundary energy
            maxBoundEnergy[grain_index-1]  = specArrays.energyHAGB[grain_index-1];
            
            // dislocation densities of grains
            disl_energies[grain_index-1]   = (double)-1.0*specArrays.disl_distr_coeff[grain_index-1]*
                                              specArrays.grainDislDensities[grain_index-1]*specArrays.mod_shear[grain_index-1]*
                                              specArrays.lattice_parameter[grain_index-1]*specArrays.lattice_parameter[grain_index-1];
            
            // 1st orientation angle for grain containing cell
            grain_angles_1[grain_index-1]    = specArrays.grain_angles[grain_index-1][0];
            
            // 2nd orientation angle for grain containing cell
            grain_angles_2[grain_index-1]    = specArrays.grain_angles[grain_index-1][1];
            
            // 3rd orientation angle for grain containing cell
            grain_angles_3[grain_index-1]    = specArrays.grain_angles[grain_index-1][2];
            
            if(en_types[cell_index_1D] == ADIABATIC_ADIABATIC_CELL | en_types[cell_index_1D] == ADIABATIC_TEMPERATURE_CELL |
               en_types[cell_index_1D] == STRAIN_ADIABATIC_CELL    | en_types[cell_index_1D] == STRESS_ADIABATIC_CELL)
            {
                grain_angles_1[grain_index-1] = 3*angleHAGB[0];
                grain_angles_2[grain_index-1] = 3*angleHAGB[0];
                grain_angles_3[grain_index-1] = 3*angleHAGB[0];
            }
            
            // highest misorientation angle for current grain
            max_grain_angles[grain_index - 1] = specArrays.angleLimitHAGB[grain_index - 1];
            
            if(en_types[cell_index_1D] == ADIABATIC_ADIABATIC_CELL)
                heat_conds[cell_index_1D] = (double)0;
            
            // heat expansion coefficient of cell
            if(specArrays.simulate_heat_expansion == 1)
              heat_exp_coeffs[grain_index-1] = specArrays.heatExpansionCoeff[grain_index-1];
            else
              heat_exp_coeffs[grain_index-1] = (byte)0;
            
            // Young modulus of cell
            mod_elast[grain_index-1]           = specArrays.mod_elast[grain_index-1];
            
            // shear modulus of cell
            mod_shear[grain_index-1]           = specArrays.mod_shear[grain_index-1];
            
            // array of lattice parameters of grains
            lattice_parameters[grain_index-1] = specArrays.lattice_parameter[grain_index-1];
        
            // array of initial defect densities of cells
            init_def_densities[cell_index_1D] = (double)0;
            
            // array of changes of defect densities of cells
            def_density_changes[cell_index_1D] = (double)0;
            
            // array of final defect densities of cells
            final_def_densities[cell_index_1D] = (double)0;
            
            // Phonon portion of grain containing cell
            phonon_portions[grain_index-1]     = specArrays.phonon_portion[grain_index-1];
            
            // the low temperature of recrystallization (generation of new grains)
            low_tempr_recryst[grain_index-1]   = specArrays.low_tempr_recryst[grain_index-1];
            
            // the high temperature of recrystallization (generation of new grains)
            high_tempr_recryst[grain_index-1]  = specArrays.high_tempr_recryst[grain_index-1];
            
            // the low temperature of twinning
            low_tempr_twinning[grain_index-1]  = specArrays.low_tempr_twinning[grain_index-1];
            
            // the high temperature of twinning
            high_tempr_twinning[grain_index-1] = specArrays.high_tempr_twinning[grain_index-1];
            
            // the maximal probability of recrystallization (generation of new grains)
            max_prob_recryst[grain_index-1]    = specArrays.max_prob_recryst[grain_index-1];
            
            // the maximal probability of twinning
            max_prob_twinning[grain_index-1]   = specArrays.max_prob_twinning[grain_index-1];
            
            // minimal density of dislocations
            min_disl_density[grain_index-1]    = specArrays.min_disl_density[grain_index-1];
            
            // the array of probabilities of recrystallization (generation of new grains)
            prob_new_grain[cell_index_1D]      = (double)0;
            
            // the array of probabilities of twinning
            prob_twinning[cell_index_1D]       = (double)0;
            
            // Presences of new grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
            new_grain_embryos[cell_index_1D]   = (byte)0;
        
            // Presences of twin grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
            twin_grain_embryos[cell_index_1D]  = (byte)0;
        
            // 1st, 2nd and 3rd indices of cell with current single index
            indices_I[cell_index_1D]       = cell_index_I;
            indices_J[cell_index_1D]       = cell_index_J;
            indices_K[cell_index_1D]       = cell_index_K;
            
            // Calculation of coordinates of current cell
            coords = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
            
            cell_coords[0][cell_index_1D] = (double)coords[0];
            cell_coords[1][cell_index_1D] = (double)coords[1];
            cell_coords[2][cell_index_1D] = (double)coords[2];
            
            // Determination of maximal coordinates for cells
            if(cell_coords[0][cell_index_1D] > max_coord_X)
              max_coord_X = cell_coords[0][cell_index_1D];
            
            if(cell_coords[1][cell_index_1D] > max_coord_Y)
              max_coord_Y = cell_coords[1][cell_index_1D];
            
            if(cell_coords[2][cell_index_1D] > max_coord_Z)
              max_coord_Z = cell_coords[2][cell_index_1D];
            
            // Determination of minimal coordinates for cells
            if(cell_coords[0][cell_index_1D] < min_coord_X)
              min_coord_X = cell_coords[0][cell_index_1D];
            
            if(cell_coords[1][cell_index_1D] < min_coord_Y)
              min_coord_Y = cell_coords[1][cell_index_1D];
            
            if(cell_coords[2][cell_index_1D] < min_coord_Z)
              min_coord_Z = cell_coords[2][cell_index_1D];
            
            // 3 arrays of coordinates X, Y, Z of force moments of cells
            force_moments[0][cell_index_1D] = 0;
            force_moments[1][cell_index_1D] = 0;
            force_moments[2][cell_index_1D] = 0;
            
            abs_mom[cell_index_1D] = 0;
            
            // Arrays of components of angle velocities of cells
            angle_velocity_X[cell_index_1D] = 0;
            angle_velocity_Y[cell_index_1D] = 0;
            angle_velocity_Z[cell_index_1D] = 0;
            
            // Arrays of components of torsion angles of cells
            torsion_angle_X[cell_index_1D] = 0;
            torsion_angle_Y[cell_index_1D] = 0;
            torsion_angle_Z[cell_index_1D] = 0;
        
            // Array of absolute values of angle velocities of cells
            abs_angle_velocity[cell_index_1D] = 0;
            
            // Array of absolute values of torsion angles of cells
            abs_torsion_angle[cell_index_1D] = 0;
            
            // The number, which determines whether the cell become cracked
            crack_sum[cell_index_1D] = 0;
            
            crack_values[cell_index_1D] = 0;
            
            // Array of coordinates of stress vectors for all cells
            stresses_X[cell_index_1D] = 0;
            stresses_Y[cell_index_1D] = 0;
            stresses_Z[cell_index_1D] = 0;
        }
        
        for(cell_index_1D = cell_number; cell_index_1D < work_item_number; cell_index_1D++)
        {
            // Setting of indices of neighbours at 1st coordination sphere for cells with current index
            setNeighbours1S(cell_index_1D);
            
            // Setting of types of location of cells in a specimen
            loc_types[cell_index_1D]   = OUTER_CELL;
            
            // Setting of location type at next time step
            new_loc_types[cell_index_1D] = OUTER_CELL;
            
            // Setting of types of energy interaction with neighbours for cells in a specimen
            en_types[cell_index_1D]    = ADIABATIC_ADIABATIC_CELL;
            
            // Setting of initial temperatures of cells in a specimen
            init_temprs[cell_index_1D] = (double)0;// INITIAL_TEMPERATURE; // 
            
            // Setting of minimal temperature for boundary cell
            bound_cell_min_temprs[cell_index_1D] = (double)0;
            
            // Setting of maximal temperature for boundary cell
            bound_cell_max_temprs[cell_index_1D] = (double)0;
            
            // Setting of mechanical energies of cells
            mech_energies[cell_index_1D]      = (double)0;
            init_mech_energies[cell_index_1D] = (double)0;
            
            // Setting of values of dissipation energies of cells
            init_diss_energies[cell_index_1D] = (double)0;
            diss_energies[cell_index_1D]      = (double)0;
            diss_en_influxes[cell_index_1D]   = (double)0;
            
            // Setting of influx of specific mechanical energy to boundary cell
            bound_mech_influxes[cell_index_1D] = (double)0;
            
            // Array of types of boundary conditions
            bound_cell_time_function_types[cell_index_1D] = (byte) 0;
            
            // Array of time periods of loading
            bound_cell_load_time_portions[cell_index_1D]  = (double)0;
            
            // Array of time periods when loading is absent or it has opposite sign
            bound_cell_relax_time_portions[cell_index_1D] = (double)0;
            
            // Values of heat capacities of cells
            heat_caps[cell_index_1D]   = (double)1;
            
            // Values of heat conductivities of cells
            heat_conds[cell_index_1D]  = (double)0;
            
            // Values of densities of cells
            densities[cell_index_1D]   = (double)1;
            
            // the array of probabilities of recrystallization (generation of new grains)
            prob_new_grain[cell_index_1D] = (double)0;
            
            // the array of probabilities of twinning
            prob_twinning[cell_index_1D] = (double)0;
            
            // Presences of new grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
            new_grain_embryos[cell_index_1D]   = (byte)0;
        
            // Presences of twin grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
            twin_grain_embryos[cell_index_1D]  = (byte)0;
            
            // coordinates of current cell
            cell_coords[0][cell_index_1D] = 0;
            cell_coords[1][cell_index_1D] = 0;
            cell_coords[2][cell_index_1D] = 0;
            
            // 3 arrays of coordinates X, Y, Z of force moments of cells
            force_moments[0][cell_index_1D] = 0;
            force_moments[1][cell_index_1D] = 0;
            force_moments[2][cell_index_1D] = 0;
            
            abs_mom[cell_index_1D] = 0;
            
            // Arrays of components of angle velocities of cells
            angle_velocity_X[cell_index_1D] = 0;
            angle_velocity_Y[cell_index_1D] = 0;
            angle_velocity_Z[cell_index_1D] = 0;
            
            // Arrays of components of torsion angles of cells
            torsion_angle_X[cell_index_1D] = 0;
            torsion_angle_Y[cell_index_1D] = 0;
            torsion_angle_Z[cell_index_1D] = 0;
            
            // Array of absolute values of torsion angles of cells
            abs_torsion_angle[cell_index_1D] = 0;
        
            // Array of absolute values of angle velocities of cells
            abs_angle_velocity[cell_index_1D] = 0;
            
            // The number, which determines whether the cell become cracked
            crack_sum[cell_index_1D] = 0;
            
            // Current grain index
            grain_index = grain_number;
            
            // Values of heat expansion coefficients of cells
            heat_exp_coeffs[grain_index-1] = (double)0;
            
            // Young modulus of cell
            mod_elast[grain_index-1]       = (double)1;
            
            // Young modules of transformed cell
            new_mod_elast[grain_index-1]   = (double)1;
            
            // Array of Young modules of cells
            cell_mod_elast[cell_index_1D]  = (double)1;
            
            // shear modulus of cell
            mod_shear[grain_index-1]       = (double)1;
            
            // array of lattice parameters of grains
            lattice_parameters[grain_index-1] = (double)1.0E30;
            
            // array of initial defect densities of cells
            init_def_densities[cell_index_1D] = (double)0;
            
            // array of changes of defect densities of cells
            def_density_changes[cell_index_1D] = (double)0;
            
            // array of final defect densities of cells
            final_def_densities[cell_index_1D] = (double)0;

            // Phonon portion of grain containing cell
            phonon_portions[grain_index-1] = (double)0;
                    
            // initial index of grain containing cell
            init_grain_indices[cell_index_1D] = grain_index;
            
            // final index of grain containing cell
            grain_indices[cell_index_1D]      = grain_index;
            
            // highest misorientation angle for grain containing cell
          //  angleHAGB[grain_index-1]       = (double)45.0;

            // 1st, 2nd and 3rd orientation angles for grain containing cell
            grain_angles_1[grain_index-1]    = 3*angleHAGB[0];//(double)0.0;//
            grain_angles_2[grain_index-1]    = 3*angleHAGB[0];//(double)0.0;//
            grain_angles_3[grain_index-1]    = 3*angleHAGB[0];//(double)0.0;//
            
            // highest misorientation angle for current grain
            max_grain_angles[grain_index - 1] = specArrays.angleLimitHAGB[grain_index - 1];
            
            // type of grain containing cell
            grain_types[grain_index-1]     = (byte)ADIABATIC_ADIABATIC_CELL;
            
            // type of grain according to its role in recrystallization process: initial and final
            grain_rec_types[grain_index-1]      = INITIAL_GRAIN;
            init_grain_rec_types[grain_index-1] = INITIAL_GRAIN;
            
            // Indices of grains, from which twinning grains grow
            root_grain_indices[grain_index-1] = 0;
            
            // Indices of new grains where embryos of twin grains appear
            twin_emb_root_gr_indices[cell_index_1D] = 0;
            
            // maximal value of grain boundary heat mobility
            heatMaxMobility[grain_index-1] = (double)0.0;
            
            // dislocation mobilities
            dislMaxMobility[grain_index-1] = (double)0.0;
        
            // mechanical mobilities
            mechMaxMobility[grain_index-1] = (double)0.0;
            
            // maximal grain boundary energy
            maxBoundEnergy[grain_index-1]  = (double)0.0;
            
            // dislocation densities of grains
            disl_energies[grain_index-1]   = (double)0.0;
            
            // the low temperature of recrystallization (generation of new grains)
            low_tempr_recryst[grain_index-1]   = (double)0;
            
            // the high temperature of recrystallization (generation of new grains)
            high_tempr_recryst[grain_index-1]  = (double)0;
            
            // the low temperature of twinning
            low_tempr_twinning[grain_index-1]  = (double)0;
            
            // the high temperature of twinning
            high_tempr_twinning[grain_index-1] = (double)0;
            
            // the maximal probability of recrystallization (generation of new grains)
            max_prob_recryst[grain_index-1]    = (double)0;
            
            // the maximal probability of twinning
            max_prob_twinning[grain_index-1]   = (double)0;
            
            // minimal density of dislocations
            min_disl_density[grain_index-1]    = (double)0;
            
            crack_values[cell_index_1D] = 0;
            
            // Array of coordinates of stress vectors for all cells
            stresses_X[cell_index_1D] = 0;
            stresses_Y[cell_index_1D] = 0;
            stresses_Z[cell_index_1D] = 0;
        }
        
        // Coordinates of all possible vectors of local growth of grain (the number of vectors is equal to the number of cell facets)
        double[] growth_vectors_coordinates = new double[3*neighb1S_number];
        
        // Calculation of all these coordinates.
        growth_vectors_coordinates = calcGrowthVectCoordinates();
        
        // random number
        double rand;
        
       // for(int grain_counter = 0; grain_counter < specArrays.init_grain_number; grain_counter++)
        for(int grain_counter = 0; grain_counter < grain_number; grain_counter++)
        {
            // maximal value of grain boundary heat mobility
            heatMaxMobility[grain_counter] = specArrays.maxMobility[grain_counter];
            
            // dislocation mobilities
            dislMaxMobility[grain_counter] = specArrays.dislMaxMobility[grain_counter];
        
            // mechanical mobilities
            mechMaxMobility[grain_counter] = specArrays.mechMaxMobility[grain_counter];
        
            // maximal grain boundary energy
            maxBoundEnergy[grain_counter]  = specArrays.energyHAGB[grain_counter];
            
            // highest misorientation angle for current grain
            max_grain_angles[grain_counter] = specArrays.angleLimitHAGB[grain_counter];
            
            // orientation angles for grain containing cell
            if(grain_counter < specArrays.init_grain_number)
            {
              grain_angles_1[grain_counter]    = specArrays.grain_angles[grain_counter][0];
              grain_angles_2[grain_counter]    = specArrays.grain_angles[grain_counter][1];
              grain_angles_3[grain_counter]    = specArrays.grain_angles[grain_counter][2];
            }
            else
            {
              grain_angles_1[grain_counter] = (double)Math.random()*max_grain_angles[grain_counter]; // /(double)3.0;
              grain_angles_2[grain_counter] = (double)Math.random()*max_grain_angles[grain_counter];
              grain_angles_3[grain_counter] = (double)Math.random()*max_grain_angles[grain_counter];
            }
            
            // heat expansion coefficient of cell
            if(specArrays.simulate_heat_expansion == 1)
              heat_exp_coeffs[grain_counter] = specArrays.heatExpansionCoeff[grain_counter];
            else
              heat_exp_coeffs[grain_counter] = (byte)0;
            
            // Young modulus of cell
            mod_elast[grain_counter]           = specArrays.mod_elast[grain_counter];
            
            // Young modules of transformed material
            new_mod_elast[grain_counter]       = 0.5*mod_elast[grain_counter];
            
            // shear modulus of cell
            mod_shear[grain_counter]           = specArrays.mod_shear[grain_counter];
            
            // array of lattice parameters of grains
            lattice_parameters[grain_index-1]  = specArrays.lattice_parameter[grain_counter];
            
            if(lattice_parameters[grain_index-1] == (double)0.0)
              lattice_parameters[grain_index-1] = (double)1.0E30;
            
            // Phonon portion of grain containing cell
            phonon_portions[grain_counter]     = specArrays.phonon_portion[grain_counter];
            
            // the low temperature of recrystallization (generation of new grains)
            low_tempr_recryst[grain_counter]   = specArrays.low_tempr_recryst[grain_counter];
            
            // the high temperature of recrystallization (generation of new grains)
            high_tempr_recryst[grain_counter]  = specArrays.high_tempr_recryst[grain_counter];
            
            // the low temperature of twinning
            low_tempr_twinning[grain_counter]  = specArrays.low_tempr_twinning[grain_counter];
            
            // the high temperature of twinning
            high_tempr_twinning[grain_counter] = specArrays.high_tempr_twinning[grain_counter];
            
            // the maximal probability of recrystallization (generation of new grains)
            max_prob_recryst[grain_counter]    = specArrays.max_prob_recryst[grain_counter];
            
            // the maximal probability of twinning
            max_prob_twinning[grain_counter]   = specArrays.max_prob_twinning[grain_counter];
            
            // minimal density of dislocations
            min_disl_density[grain_counter]    = specArrays.min_disl_density[grain_counter];
            
            // dislocation densities of grains
            disl_energies[grain_counter]   = (double)-1.0*specArrays.disl_distr_coeff[grain_counter]*
                                              specArrays.grainDislDensities[grain_counter]*specArrays.mod_shear[grain_counter]*
                                              specArrays.lattice_parameter[grain_counter]*specArrays.lattice_parameter[grain_counter];
            
            // Minimal energy of dislocations
            min_disl_energies[grain_counter]   = (double)-1.0*specArrays.disl_distr_coeff[grain_counter]*
                                              min_disl_density[grain_counter]*specArrays.mod_shear[grain_counter]*
                                              specArrays.lattice_parameter[grain_counter]*specArrays.lattice_parameter[grain_counter];
            
            rand = Math.random();
            
            // Lengths of lattice vectors for grains
            if(rand < 1.0/3.0)
            {
                lattice_vector_A_lengths[grain_counter] = specArrays.lattice_vector_A_lengths[grain_counter];
                lattice_vector_B_lengths[grain_counter] = specArrays.lattice_vector_B_lengths[grain_counter];
                lattice_vector_C_lengths[grain_counter] = specArrays.lattice_vector_C_lengths[grain_counter];
            }
            
            if(rand >= 1.0/3.0 & rand < 2.0/3.0)
            {
                lattice_vector_A_lengths[grain_counter] = specArrays.lattice_vector_B_lengths[grain_counter];
                lattice_vector_B_lengths[grain_counter] = specArrays.lattice_vector_C_lengths[grain_counter];
                lattice_vector_C_lengths[grain_counter] = specArrays.lattice_vector_A_lengths[grain_counter];
            }
            
            if(rand >= 2.0/3.0)
            {
                lattice_vector_A_lengths[grain_counter] = specArrays.lattice_vector_C_lengths[grain_counter];
                lattice_vector_B_lengths[grain_counter] = specArrays.lattice_vector_A_lengths[grain_counter];
                lattice_vector_C_lengths[grain_counter] = specArrays.lattice_vector_B_lengths[grain_counter];
            }
            
            rand = Math.random();
              
            // Angles between lattice vectors for grains
            if(rand < 1.0/3.0)
            {
              lattice_angles_vecA_vecB[grain_counter] = specArrays.lattice_angles_vecA_vecB[grain_counter];
              lattice_angles_vecB_vecC[grain_counter] = specArrays.lattice_angles_vecB_vecC[grain_counter];
              lattice_angles_vecC_vecA[grain_counter] = specArrays.lattice_angles_vecC_vecA[grain_counter];
            }
            
            if(rand >= 1.0/3.0 & rand < 2.0/3.0)
            {
              lattice_angles_vecA_vecB[grain_counter] = specArrays.lattice_angles_vecB_vecC[grain_counter];
              lattice_angles_vecB_vecC[grain_counter] = specArrays.lattice_angles_vecC_vecA[grain_counter];
              lattice_angles_vecC_vecA[grain_counter] = specArrays.lattice_angles_vecA_vecB[grain_counter];
            }
            
            if(rand >= 2.0/3.0)
            {
              lattice_angles_vecA_vecB[grain_counter] = specArrays.lattice_angles_vecC_vecA[grain_counter];
              lattice_angles_vecB_vecC[grain_counter] = specArrays.lattice_angles_vecA_vecB[grain_counter];
              lattice_angles_vecC_vecA[grain_counter] = specArrays.lattice_angles_vecB_vecC[grain_counter];
            }
              
            // Coefficients of lattice anisotropy for grains
            lattice_anis_coeff[grain_counter]       = specArrays.lattice_anis_coeff[grain_counter];
            
            // calculation of coordinates of lattice vectors for current grain
            calcLatticeVectCoordinates(grain_counter, lattice_angles_vecA_vecB[grain_counter], 
                                                      lattice_angles_vecB_vecC[grain_counter], 
                                                      lattice_angles_vecC_vecA[grain_counter]);
            
            // TEST
            if(false)
            {
              if(Math.random() < 0.5)//(grain_counter % 2 == 0)
              {
                lattice_vectors[3][grain_counter] = lattice_vectors[0][grain_counter];
                lattice_vectors[4][grain_counter] = lattice_vectors[1][grain_counter];
                lattice_vectors[5][grain_counter] = lattice_vectors[2][grain_counter];
              }
              else
              {
                lattice_vectors[6][grain_counter] = lattice_vectors[0][grain_counter];
                lattice_vectors[7][grain_counter] = lattice_vectors[1][grain_counter];
                lattice_vectors[8][grain_counter] = lattice_vectors[2][grain_counter];
              }
            }
            
            // parameters of initial grains
            if(grain_counter < specArrays.init_grain_number + 10 | grain_counter > grain_number - 10)
              System.out.println("Grain # "+(grain_counter + 1)+
              //    ". disl_energy= "+disl_energies[grain_counter]+"; disl_density= "+specArrays.grainDislDensities[grain_counter]+
              //    "; min_disl_energy = "+min_disl_energies[grain_counter]+"; min_disl_density= "+min_disl_density[grain_counter]+
              //                   "; disl_distr_coeff = "+specArrays.disl_distr_coeff[grain_counter]+
              //                   "; disl.density = "+specArrays.grainDislDensities[grain_counter]+
              //                   "; mod_shear = "+specArrays.mod_shear[grain_counter]+
              //                   "; mod_elast = "+mod_elast[grain_counter]+
              //                   "; max_bound_energy = "+maxBoundEnergy[grain_counter]+
              //                   "; grain_angle_0 = "+grain_angles[grain_counter]+
              //                     "; lattice_param = "+specArrays.lattice_parameter[grain_counter]);
                      "; lengths: A="+lattice_vector_A_lengths[grain_counter]+
                      "; B="+lattice_vector_B_lengths[grain_counter]+
                      "; C="+lattice_vector_C_lengths[grain_counter]+
              //        "; angles: A_B= "+lattice_angles_vecA_vecB[grain_counter]+
              //        "; B_C= "+lattice_angles_vecB_vecC[grain_counter]+
              //        "; C_A= "+lattice_angles_vecC_vecA[grain_counter]+
              //        "; anis_cf= "+lattice_anis_coeff[grain_counter]);
                    "; lat.vecs: A=("+lattice_vectors[0][grain_counter]+", "+lattice_vectors[1][grain_counter]+", "+lattice_vectors[2][grain_counter]+
                             "); B=("+lattice_vectors[3][grain_counter]+", "+lattice_vectors[4][grain_counter]+", "+lattice_vectors[5][grain_counter]+
                             "); C=("+lattice_vectors[6][grain_counter]+", "+lattice_vectors[7][grain_counter]+", "+lattice_vectors[8][grain_counter]+"); "+
                    "lat_prm="+lattice_parameters[grain_counter]+"; cf_diss="+coeff_diss[grain_counter]);
        }
        
        // TEST
      //  System.out.println("DEFECT ENERGIES OF GRAINS");
      //  for(int grain_counter = 0; grain_counter<grain_number; grain_counter++)
      //    System.out.println("Grain # "+(grain_counter+1)+": defect energy = "+disl_energies[grain_counter]);
      //  System.out.println("--------==================---------------------=======================------------");
        
        // Current boundary cell index
        int bound_cell_index;
        
        // Index of loaded facet:
        // 0 - left; 1 - front; 2 - top; 3 - bottom; 4 - back; 5 - right.
        int facet_index;
        
        int case_counter = 0;
        
        int cyclic_cell_counter = 0;
        
        // Setting of temperatures for boundary cells
        for(int bound_cell_counter = 0; bound_cell_counter < bound_cell_indices.length; bound_cell_counter++)
        {
            // Index of current boundary cell
            bound_cell_index = bound_cell_indices[bound_cell_counter];
            
            // Index of grain containing current boundary cell
            grain_index = specArrays.grainIndices[bound_cell_index];
            
            // Index of facet containing current boundary cell
            facet_index = grain_index - specArrays.init_grain_number + 11;
            
            if(facet_index < 0 | facet_index > specArrays.mech_load_type.length - 1)
            {
              case_counter++;
            //  System.out.println("Case # "+case_counter+": bound_cell_index = "+bound_cell_index+"; grain_index = "+grain_index+"; grain_number = "+grain_number+"; facet_index = "+facet_index);
            }
            else
            {
             // if(facet_index > 5)
               // System.out.println("facet_index: "+facet_index+"; mech_load_type: "+specArrays.mech_load_type[facet_index]+"; grain_index = "+grain_index+"; bound_cell_index = "+bound_cell_index+"; init_grain_number = "+specArrays.init_grain_number);  
              
              // Setting of influx of specific mechanical energy for current boundary cell
              if(specArrays.mech_load_type[facet_index].equals(UICommon.STRAIN_R_B_NAME))
              {
                bound_min_mech_influxes[bound_cell_index] = specArrays.bound_cell_min_mech_energies[bound_cell_counter]*mod_elast[grain_index-1]*TIME_STEP;
                bound_max_mech_influxes[bound_cell_index] = specArrays.bound_cell_max_mech_energies[bound_cell_counter]*mod_elast[grain_index-1]*TIME_STEP;
              }
              
              // Setting of the value of mechanical energy influx per time step for current boundary cell
              if(specArrays.mech_load_type[facet_index].equals(UICommon.STRESS_R_B_NAME))
              {
                bound_min_mech_influxes[bound_cell_index] = specArrays.bound_cell_min_mech_energies[bound_cell_counter]*TIME_STEP;
                bound_max_mech_influxes[bound_cell_index] = specArrays.bound_cell_max_mech_energies[bound_cell_counter]*TIME_STEP;
              }
              
              // Setting of the initial value of mechanical energy for current boundary cell
              init_mech_energies[bound_cell_index] += bound_mech_influxes[bound_cell_index];
              
              // Setting of temperature for current boundary cell if initial value of temperature is assigned
              if(specArrays.heat_load_type[facet_index].equals(UICommon.TEMPERATURE_R_B_NAME))
              {
                bound_cell_min_temprs[bound_cell_index]   = specArrays.bound_cell_min_heat_energies[bound_cell_counter];
                bound_cell_max_temprs[bound_cell_index]   = specArrays.bound_cell_heat_energies[bound_cell_counter];
                
                // init_temprs[bound_cell_index]       = specArrays.bound_cell_heat_energies[bound_cell_counter];
              }
              
              // Setting of temperature for current boundary cell if initial value of heat energy is assigned
              if(specArrays.heat_load_type[facet_index].equals(UICommon.ENERGY_R_B_NAME))
              {
                bound_cell_min_temprs[bound_cell_index]   = specArrays.bound_cell_min_heat_energies[bound_cell_counter];
                bound_cell_max_temprs[bound_cell_index]   = specArrays.bound_cell_heat_energies[bound_cell_counter];
                
                // init_temprs[bound_cell_index]       = specArrays.bound_cell_heat_energies[bound_cell_counter]/
                //                                      (heat_caps[bound_cell_index]*densities[bound_cell_index]*cell_volume);
              }
            }
            
          //  if(bound_cell_index == 5781)
            //    System.out.println("Cell # 5781: facet_index = "+facet_index+"; heat_load_type = "+specArrays.heat_load_type[facet_index]+"; T = "+init_temprs[bound_cell_index]);
            
            // Array of types of boundary conditions
            bound_cell_time_function_types[bound_cell_index] = specArrays.bound_cell_time_function_types[bound_cell_counter];
            
            // Array of time periods of loading
            bound_cell_load_time_portions[bound_cell_index]  = specArrays.bound_cell_load_time_portions[bound_cell_counter];
            
            // Array of time periods when loading is absent or it has opposite sign
            bound_cell_relax_time_portions[bound_cell_index] = specArrays.bound_cell_relax_time_portions[bound_cell_counter];
            
            if(bound_cell_time_function_types[bound_cell_index] == CYCLE_LOADING_BYTE_VALUE)
            if(grain_index == 9 & false)
            {
              System.out.println("grain_index= "+grain_index+"; gr.num="+specArrays.init_grain_number+" Facet #"+facet_index+"; bound.cell # "+bound_cell_counter+"; index="+bound_cell_index+
                             "; min.mech.influx="+bound_min_mech_influxes[bound_cell_index]+" Pa; mech.influx="+bound_max_mech_influxes[bound_cell_index]+" Pa; "+
                             "min.tempr.="+bound_cell_min_temprs[bound_cell_index]+" K; max.tempr.="+bound_cell_max_temprs[bound_cell_index]+" K; "+
                             "time func.type:"+bound_cell_time_function_types[bound_cell_index]+"; load portion="+bound_cell_load_time_portions[bound_cell_index]+" "+
                             "; relax.portion="+bound_cell_relax_time_portions[bound_cell_index]);
              
              cyclic_cell_counter++;
            }
        }
     //   System.out.println("Cell # 5781: T = "+init_temprs[5781]);
        
        System.out.println("Number of cells with cyclic conditions: "+cyclic_cell_counter);
        System.out.println("Number of wrong cases: "+case_counter);
        
        // Setting of types of energy interaction with neighbours for cells in a specimen
        en_types[work_item_number]    = ADIABATIC_ADIABATIC_CELL;
        new_en_types[work_item_number]= ADIABATIC_ADIABATIC_CELL;
        
        // Setting of initial temperatures of cells in a specimen
        init_temprs[work_item_number] = (double)0;//INITIAL_TEMPERATURE;//
        
        bound_cell_min_temprs[work_item_number] = (double)0;//INITIAL_TEMPERATURE;//
        bound_cell_max_temprs[work_item_number] = (double)0;//INITIAL_TEMPERATURE;//
        
        // ---------- Creation of pairs of indices of cells necessary for calculation of force moments -------------
        // Double array of cells at 1st coordination sphere from pairs is created: 
        // even index corresponds to 1st element in pair,
        // odd index corresponds  to 2nd element in pair.
        int[][] cell_pairs = new int[4*neighb1S_number][work_item_number];
        cell_pairs = createCell1SPairs();
        
        int cell_pair_0;
        int cell_pair_1;
        int total_pair_number = 0;
        
        // TEST
    //    System.out.println("==========++++++++++++------------==============++++++++++++++----------------");
        
        if(false)
        for(int cell_index = 0; cell_index < cell_number; cell_index++)
        {
        //  System.out.println("Cell # "+cell_index);
          
          for(int pair_index = 0; pair_index < 2*neighb1S_number; pair_index++)
          {
            cell_pair_0 = cell_pairs[2*pair_index][cell_index];
            cell_pair_1 = cell_pairs[2*pair_index + 1][cell_index];
            
            if((cell_pair_0 == cell_number & cell_pair_1 != cell_number)|
               (cell_pair_0 != cell_number & cell_pair_1 == cell_number))
              System.out.println("ERROR!!! Cell pair # "+(pair_index+1)+" is wrong!!!");
            
            if(cell_pair_0 < cell_number & cell_pair_1 < cell_number)
            {
              total_pair_number++;
              
            //  System.out.println("Pair # "+(pair_index+1)+": "+cell_pair_0+", "+cell_pair_1);
            }
            else
            if(cell_pair_0 != cell_number | cell_pair_1 != cell_number)
              System.out.println("ERROR!!! Cell # "+cell_pair_0+" or cell # "+cell_pair_1+" is not neighbour of cell # "+cell_index);
          }
          
        //  System.out.println();
        }
        
      //  System.out.println("Total number of cell pairs necessary for calculation of force moments: "+total_pair_number);
        
        // TEST
      //  System.out.println("==========++++++++++++------------==============++++++++++++++----------------");
        
        // ---------- Pointers to arrays of cell parameters -------------
        // Pointer to array of initial temperatures of cells
        Pointer init_temprs_ptr        = Pointer.to(init_temprs);
        
        // Pointer to array of minimal temperatures of boundary cells
        Pointer bound_cell_min_temprs_ptr    = Pointer.to(bound_cell_min_temprs);
        
        // Pointer to array of maximal temperatures of boundary cells
        Pointer bound_cell_max_temprs_ptr    = Pointer.to(bound_cell_max_temprs);
        
        // Pointer to array of mechanical energies of cells
        Pointer mech_energies_ptr      = Pointer.to(mech_energies);
        
        // Pointer to array of initial mechanical energies of cells
        Pointer init_mech_energies_ptr = Pointer.to(init_mech_energies);
        
        // Pointer to array of initial dissipation energies of cells
        Pointer init_diss_energies_ptr = Pointer.to(init_diss_energies);
        
        // Pointer to array of dissipation energies of cells
        Pointer diss_energies_ptr      = Pointer.to(diss_energies);
        
        // Pointer to array of influxes of dissipation energy to cells
        Pointer diss_en_influxes_ptr   = Pointer.to(diss_en_influxes);
        
        // Pointer to array of influxes of specific mechanical energy to boundary cells per time step
        Pointer bound_mech_influxes_ptr = Pointer.to(bound_mech_influxes);
        
        // Pointer to array of minimal influxes of specific mechanical energy to boundary cells per time step
        Pointer bound_min_mech_influxes_ptr = Pointer.to(bound_min_mech_influxes);
        
        // Pointer to array of maximal influxes of specific mechanical energy to boundary cells per time step
        Pointer bound_max_mech_influxes_ptr = Pointer.to(bound_max_mech_influxes);
        
        // Pointer to array of types of boundary conditions
        Pointer bound_cell_time_function_types_ptr = Pointer.to(bound_cell_time_function_types);
        
        // Pointer to array of time periods of loading
        Pointer bound_cell_load_time_portions_ptr  = Pointer.to(bound_cell_load_time_portions);
        
        // Pointer to array of time periods when loading is absent or it has opposite sign
        Pointer bound_cell_relax_time_portions_ptr = Pointer.to(bound_cell_relax_time_portions);
        
        // Pointer to array of heat capacities of cells
        Pointer heat_caps_ptr          = Pointer.to(heat_caps);
        
        // Pointer to array of heat conductivities of cells
        Pointer heat_conds_ptr         = Pointer.to(heat_conds);  
        
        // Pointer to array of heat expansion coefficients of cells
        Pointer heat_exp_coeffs_ptr    = Pointer.to(heat_exp_coeffs);
        
        // Pointer to array of Young modules of cells
        Pointer mod_elast_ptr = Pointer.to(mod_elast);
        
        // Pointer to array of Young modules of transformed cells
        Pointer new_mod_elast_ptr = Pointer.to(new_mod_elast);
        
        // Pointer to array of Young modules of cells
        Pointer cell_mod_elast_ptr = Pointer.to(cell_mod_elast);
        
        // Pointer to array of shear modules of cells
        Pointer mod_shear_ptr = Pointer.to(mod_shear);
        
        // Pointer to array of lattice parameters of grains
        Pointer lattice_parameters_ptr = Pointer.to(lattice_parameters);
        
        // Pointer to array of initial defect densities of cells
        Pointer init_def_densities_ptr = Pointer.to(init_def_densities);
        
        // Pointer to array of changes of defect densities of cells
        Pointer def_density_changes_ptr = Pointer.to(def_density_changes);
        
        // Pointer to array of relative changes of dissipation energy in comparison with total mechanical energy
        Pointer rel_diss_en_changes_ptr = Pointer.to(rel_diss_en_changes);
        
        // Pointer to array of relative dissipation energy in comparison with total mechanical energy
        Pointer rel_diss_energies_ptr = Pointer.to(rel_diss_energies);
        
        // Pointer to array of relative changes of dissipation energy in comparison with current influx of total mechanical energy
        Pointer rel_diss_en_influxes_ptr = Pointer.to(rel_diss_en_influxes);
        
        // Pointer to array of dissipation coefficients for grains
        Pointer coeff_diss_ptr = Pointer.to(coeff_diss);
        
        // Pointer to array of final defect densities of cells
        Pointer final_def_densities_ptr = Pointer.to(final_def_densities);
        
        // Pointer to array of phonon portions of grains
        Pointer phonon_portions_ptr = Pointer.to(phonon_portions);
        
        // Pointer to array of heat strains of cells
        Pointer heat_strains_ptr    = Pointer.to(heat_strains);
        
        // Pointer to array of initial heat strains of cells
        Pointer init_heat_strains_ptr = Pointer.to(init_heat_strains);
        
        // Pointer to array of densities of cells
        Pointer densities_ptr       = Pointer.to(densities);
        
        // Pointer to array of energy types of cells
        Pointer en_types_ptr        = Pointer.to(en_types);
        
        // Pointer to array of cell location types
        Pointer loc_types_ptr        = Pointer.to(loc_types);
        
        // Pointer to array of cell location types at next time step
        Pointer new_loc_types_ptr        = Pointer.to(new_loc_types);
        
        // Pointer to array of new energy types of cells
        Pointer new_en_types_ptr    = Pointer.to(new_en_types);
        
        // Pointers to arrays of indices of neighbour cells for all cells from specimen
        Pointer[] neighbours1S_ptr  = new Pointer[neighb1S_number];
        
        for (int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          neighbours1S_ptr[neighb_counter] = Pointer.to(neighbours1S[neighb_counter]);
        
        // Pointers to arrays of cells from pairs necessary for calculation of force moments
        Pointer[] cell_pairs1S_ptr  = new Pointer[4*neighb1S_number];
        
        for (int cell_pair_counter = 0; cell_pair_counter < 4*neighb1S_number; cell_pair_counter++)
          cell_pairs1S_ptr[cell_pair_counter] = Pointer.to(cell_pairs[cell_pair_counter]);
        
        // Pointers to coordinates X, Y and Z of cells
        Pointer cell_coords_X_ptr  = Pointer.to(cell_coords[0]);
        Pointer cell_coords_Y_ptr  = Pointer.to(cell_coords[1]);
        Pointer cell_coords_Z_ptr  = Pointer.to(cell_coords[2]);
        
        // Pointers to 3 arrays of coordinates X, Y, Z of force moments of cells
        Pointer force_moments_X_ptr  = Pointer.to(force_moments[0]);
        Pointer force_moments_Y_ptr  = Pointer.to(force_moments[1]);
        Pointer force_moments_Z_ptr  = Pointer.to(force_moments[2]);
        
        // Pointer to array of absolute values of force moments of cells
        Pointer abs_mom_ptr  = Pointer.to(abs_mom);
        
        // Pointers to arrays of components of angle velocities of cells
        Pointer angle_velocity_X_ptr   = Pointer.to(angle_velocity_X);
        Pointer angle_velocity_Y_ptr   = Pointer.to(angle_velocity_Y);
        Pointer angle_velocity_Z_ptr   = Pointer.to(angle_velocity_Z);
        
        // Pointer to array of absolute values of angle velocities of cells
        Pointer abs_angle_velocity_ptr = Pointer.to(abs_angle_velocity);
        
        // Pointers to arrays of components of torsion angles of cells
        Pointer torsion_angle_X_ptr   = Pointer.to(torsion_angle_X);
        Pointer torsion_angle_Y_ptr   = Pointer.to(torsion_angle_Y);
        Pointer torsion_angle_Z_ptr   = Pointer.to(torsion_angle_Z);
        
        // Pointer to array of absolute values of torsion angles of cells
        Pointer abs_torsion_angle_ptr = Pointer.to(abs_torsion_angle);
        
        // Pointer to array of numbers, each of which determines whether the cell become cracked
        Pointer crack_sum_ptr = Pointer.to(crack_sum);
        
        // Pointer to array of cell temperatures calculated in the method
        Pointer temperatures_ptr    = Pointer.to(temperatures);
        
        // Pointer to array of cell heat influxes temperatures calculated in the method
        Pointer heat_influxes_ptr   = Pointer.to(heat_influxes);
        
        // Pointer to array of mechanical energy influxes to cells from neighbours
        Pointer mech_influxes_ptr   = Pointer.to(mech_influxes);
        
        // Pointer to current index of cell neighbour
        Pointer neighb_id_ptr       = Pointer.to(neighb_id);
        
        // Pointer to factor necessary for calculation of heat influx
        Pointer factor_ptr          = Pointer.to(factor);
        
        // Pointer to variable for testing
     //   Pointer step_counter_ptr    = Pointer.to(step_counter);
        
        // Pointer to array of heat expansion coefficients of cells
        // Pointer heat_exp_coeffs_ptr = Pointer.to(heat_exp_coeffs);
        
        // Pointers to arrays of parameters of neighbour cells
        Pointer[] neighb_heat_max_mob_ptr  = new Pointer[neighb1S_number];
        Pointer[] bound_energies_ptr       = new Pointer[neighb1S_number];
        Pointer[] bound_velocities_ptr     = new Pointer[neighb1S_number];
        Pointer[] probs_ptr                = new Pointer[neighb1S_number];
        Pointer[] prob_sums_ptr            = new Pointer[neighb1S_number+2];
        
        // Pointer to array of 0 and 1 responsible for possibility of cell transition to adjacent grain
        Pointer[] poss_switches_ptr        = new Pointer[neighb1S_number];
        
        // Pointers to arrays of misorientation angles for grains 
        // containing neighbour cells divided by maximal angle of misorientation
        Pointer[] spec_angle_diff_ptr  = new Pointer[neighb1S_number];
        
        // Pointers to arrays of specific energies of grain boundaries with neighbour cells
        Pointer[] spec_bound_energies_ptr  = new Pointer[neighb1S_number];
             
        // Pointers to arrays of parameters of neighbour cells
        for (int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
            spec_angle_diff_ptr[neighb_counter]     = Pointer.to(spec_angle_diff[neighb_counter]);
            neighb_heat_max_mob_ptr[neighb_counter] = Pointer.to(neighb_heat_max_mob[neighb_counter]);
            spec_bound_energies_ptr[neighb_counter] = Pointer.to(spec_bound_energies[neighb_counter]);
            bound_energies_ptr[neighb_counter]      = Pointer.to(bound_energies[neighb_counter]);
            bound_velocities_ptr[neighb_counter]    = Pointer.to(bound_velocities[neighb_counter]);
            probs_ptr[neighb_counter]               = Pointer.to(probs[neighb_counter]);
            prob_sums_ptr[neighb_counter]           = Pointer.to(prob_sums[neighb_counter]);
            poss_switches_ptr[neighb_counter]       = Pointer.to(poss_switches[neighb_counter]);
        }
        
        prob_sums_ptr[neighb1S_number]              = Pointer.to(prob_sums[neighb1S_number]);
        prob_sums_ptr[neighb1S_number+1]            = Pointer.to(prob_sums[neighb1S_number+1]);
        
        // Pointer to array of random numbers
        Pointer rand_nums_ptr = Pointer.to(rand_nums);
    
        // Pointer to array of highest misorientation angles for grains 
        Pointer angleHAGB_ptr       = Pointer.to(angleHAGB);
        
        // Pointer to array of 1st orientation angles for grains
        Pointer grain_angles_1_ptr    = Pointer.to(grain_angles_1);
        
        // Pointer to array of 2nd orientation angles for grains
        Pointer grain_angles_2_ptr    = Pointer.to(grain_angles_2);
        
        // Pointer to array of 3rd orientation angles for grains
        Pointer grain_angles_3_ptr    = Pointer.to(grain_angles_3);
        
        // Pointer to array of highest misorientation angles for grains
        Pointer max_grain_angles_ptr    = Pointer.to(max_grain_angles);
        
        // Pointer to array of grain types
        Pointer grain_types_ptr     = Pointer.to(grain_types);
        
        // Pointer to array of grain types according to its role in recrystallization process: initial and final
        Pointer grain_rec_types_ptr      = Pointer.to(grain_rec_types);
        Pointer init_grain_rec_types_ptr = Pointer.to(init_grain_rec_types);
        
        // Pointer to array of indices of grains, from which twinning grains grow
        Pointer root_grain_indices_ptr     = Pointer.to(root_grain_indices);
        
        // Pointer to array of indices of new grains where embryos of twin grains appear
        Pointer twin_emb_root_gr_indices_ptr     = Pointer.to(twin_emb_root_gr_indices);
        
        // Pointer to array of initial indices of grains
        Pointer init_grain_indices_ptr = Pointer.to(init_grain_indices);
        
        // Pointer to array of final indices of grains
        Pointer grain_indices_ptr      = Pointer.to(grain_indices);
        
        // Pointer to array of maximal values of grain boundary heat mobility
        Pointer heatMaxMobility_ptr    = Pointer.to(heatMaxMobility);
        
        // Pointer to array of maximal values of grain boundary dislocation mobility
        Pointer dislMaxMobility_ptr    = Pointer.to(dislMaxMobility);
        
        // Pointer to array of maximal values of grain boundary mechanical mobility
        Pointer mechMaxMobility_ptr    = Pointer.to(mechMaxMobility);
        
        // Pointer to array of maximal values of grain boundary energy
        Pointer maxBoundEnergy_ptr     = Pointer.to(maxBoundEnergy);
        
        // Pointer to array of dislocation energies of grains
        Pointer disl_energies_ptr     = Pointer.to(disl_energies);
        
        // Pointer to array of minimal dislocation energies of grains
        Pointer min_disl_energies_ptr     = Pointer.to(min_disl_energies);
        
        // Pointer to the low temperature of recrystallization (generation of new grains)
        Pointer low_tempr_recryst_ptr   = Pointer.to(low_tempr_recryst);
        
        // Pointer to the high temperature of recrystallization (generation of new grains)
        Pointer high_tempr_recryst_ptr  = Pointer.to(high_tempr_recryst);
        
        // Pointer to the low temperature of twinning
        Pointer low_tempr_twinning_ptr  = Pointer.to(low_tempr_twinning);
        
        // Pointer to the high temperature of twinning
        Pointer high_tempr_twinning_ptr = Pointer.to(high_tempr_twinning);
        
        // Pointer to the maximal probability of recrystallization (generation of new grains)
        Pointer max_prob_recryst_ptr    = Pointer.to(max_prob_recryst);
        
        // Pointer to the maximal probability of twinning
        Pointer max_prob_twinning_ptr   = Pointer.to(max_prob_twinning);
        
        // Pointer to the array of probabilities of recrystallization (generation of new grains)
        Pointer prob_new_grain_ptr      = Pointer.to(prob_new_grain);
        
        // Pointer to the array of probabilities of twinning
        Pointer prob_twinning_ptr       = Pointer.to(prob_twinning);
        
        // Pointer to the array of presences of new grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
        Pointer new_grain_embryos_ptr   = Pointer.to(new_grain_embryos);
        
        // Pointer to the array of presences of twin grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
        Pointer twin_grain_embryos_ptr   = Pointer.to(twin_grain_embryos);
        
        // Pointer to the array of the values of minimal density of dislocations
        Pointer min_disl_density_ptr    = Pointer.to(min_disl_density);
        
        // Pointers to arrays of lengths of lattice vectors for grains
        Pointer lattice_vector_A_lengths_ptr = Pointer.to(lattice_vector_A_lengths);
        Pointer lattice_vector_B_lengths_ptr = Pointer.to(lattice_vector_B_lengths);
        Pointer lattice_vector_C_lengths_ptr = Pointer.to(lattice_vector_C_lengths);
        
        // Pointer to coefficients of lattice anisotropy for grains
        Pointer lattice_anis_coeff_ptr       = Pointer.to(lattice_anis_coeff);
        
        // coordinates of lattice vectors for grains
        Pointer[] lattice_vectors_ptr = new Pointer[9];
        
        for(int ptr_counter = 0; ptr_counter < 9; ptr_counter++)
          lattice_vectors_ptr[ptr_counter] = Pointer.to(lattice_vectors[ptr_counter]);
        
        // Pointer to coordinates of all possible vectors of local growth of grain (the number of vectors is equal to the number of cell facets)
        Pointer growth_vectors_coordinates_ptr       = Pointer.to(growth_vectors_coordinates);
        
        // Pointer to array with the variable responsible for simulation of crack generation
        Pointer simulate_cracks_ptr = Pointer.to(simulate_cracks);
        
        // Pointer to array of values for crack generation
        Pointer crack_values_ptr = Pointer.to(crack_values);
        
        // Pointers to arrays of stresses at cell boundaries
        Pointer[] stresses_ptr  = new Pointer[neighb1S_number];
        
        for (int bound_counter = 0; bound_counter < neighb1S_number; bound_counter++)
          stresses_ptr[bound_counter] = Pointer.to(stress[bound_counter]);
        
        // Pointers to arrays of coordinates of stress vectors for all cells
        Pointer stresses_X_ptr = Pointer.to(stresses_X);
        Pointer stresses_Y_ptr = Pointer.to(stresses_Y);
        Pointer stresses_Z_ptr = Pointer.to(stresses_Z);        
        
        //----------------------------------------------------------------------        
        // The platform, device type and device number that will be used
        final int  platformIndex = 0;
        final long deviceType    = CL_DEVICE_TYPE_GPU; // CL_DEVICE_TYPE_ALL; // 
        final int  deviceIndex   = 0;
        
        System.out.println("deviceType = "+deviceType);
        
        // Enable exceptions and subsequently omit error checks in this sample
        setExceptionsEnabled(true);
        
        // The number of platforms
        int numPlatforms = 0;

        // The number of platforms is obtained
        int numPlatformsArray[] = new int[1];
        
       // numPlatformsArray[0] = 1;
        
        // The list of available platforms is obtained
        clGetPlatformIDs(0, null, numPlatformsArray);
        
        // The number of platforms
        numPlatforms = numPlatformsArray[0];
        
        System.out.println("Number of platforms: "+numPlatforms);

        // Printing of the number of platforms
        for(int counter = 0; counter < numPlatformsArray.length; counter++)
          if(numPlatformsArray[counter]>0)
            System.out.println("The number of platforms: numPlatformsArray["+counter+"] = "+numPlatformsArray[counter]);
        
        // Platform ID is obtained
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        
        // IDs of available platforms are obtained
        clGetPlatformIDs(platforms.length, platforms, null);
        
        // The platform from the list is obtained
        cl_platform_id platform = platforms[platformIndex];
        
        // Printing of the platform ID
        System.out.println("Platform ID: "+platform);

        // The context properties (Java port of cl_context_properties) are initialized
        cl_context_properties contextProperties = new cl_context_properties();
        
        // Addition of the specified property to these properties
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Array of devices on the platform
        int numDevicesArray[] = new int[1];
        
        // The list of devices available on a platform is obtained
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        
        // The number of devices at platform
        int numDevices = numDevicesArray[0];
        
        // Printing of the number of devices at platforms
        for(int counter = 0; counter < numDevicesArray.length; counter++)
            if(numDevicesArray[counter]>0)
                System.out.println("The number of devices at platform: numDevicesArray["+counter+"] = "+numDevicesArray[counter]);
        
        // Array of device IDs
        cl_device_id devices[] = new cl_device_id[numDevices];
        
        // The array of device IDs available on platform is obtained
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        
        // Device ID
        cl_device_id device = devices[deviceIndex];
        
        // Obtaining of the list of devices
        for(int device_index = 0; device_index < numDevicesArray[0]; device_index++)
        {
            // The device ID is obtained from the list
            device = devices[device_index];
        
            // Printing of the device ID
            System.out.println("Device ID: "+device);
        }
        
        // Create a context for the selected device
     // cl_context context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);
        
        // Creation of the context for the selected devices
        cl_context context = clCreateContext(contextProperties, numDevices, devices, null, null, null);
        
        // Number of command queues
        int com_queue_number = devices.length;
        
        // Creation of the command queue for the selected device
        cl_command_queue commandQueue[] = new cl_command_queue[com_queue_number];
     
        for(int com_queue_counter = 0; com_queue_counter < com_queue_number; com_queue_counter++)
        {
          commandQueue[com_queue_counter] = clCreateCommandQueue(context, devices[com_queue_counter], 0, null);
        }
        
        // Program text determining work of kernel for realization of cyclic boundary conditions
        String prog_string_0 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64 : enable \n"+
        "\n"+
        "__kernel void kernel_0(__global       long   *step_counter,            __global const char   *time_function_types,     \n"+
        "                       __global const double *load_time_portions,      __global const double *relax_time_portions,     \n"+
        "                       __global const double *bound_min_temprs,        __global const double *bound_max_temprs,        \n"+
        "                       __global const double *bound_min_mech_influxes, __global const double *bound_max_mech_influxes, \n"+
        "                       __global       double *init_temprs,             __global       double *bound_mech_influxes)\n"+
        "{\n"+
        "  int cell_index = get_global_id(0);\n"+
        "  \n"+
        "  int load_step_number_in_period  = (int)rint((double)"+step_number+"*load_time_portions[cell_index]);\n"+
        "  int relax_step_number_in_period = (int)rint((double)"+step_number+"*relax_time_portions[cell_index]);\n"+
        "  \n"+
        "  long step_number_in_period      = load_step_number_in_period + relax_step_number_in_period;\n"+
        "  \n"+
        "  long step_index_in_period       = (step_counter[0] - (long)1) % step_number_in_period;\n"+
        "  \n"+
        "  double bound_tempr = (double)signbit(-0.5 + time_function_types[cell_index])*bound_max_temprs[cell_index] + \n"+
        "                       (double)signbit((float)-0.5 + fabs(time_function_types[cell_index] - (float)"+Common.CYCLE_LOADING_BYTE_VALUE+"))* \n"+
        "                   ((double)signbit( step_index_in_period - load_step_number_in_period + (double)0.5)*bound_max_temprs[cell_index] + \n"+
        "                    (double)signbit(-step_index_in_period + load_step_number_in_period - (double)0.5)*bound_min_temprs[cell_index]);\n"+
        "  \n"+
        "  double init_tempr = (double)signbit(bound_max_temprs[cell_index] - (double)1.0E-10)*init_temprs[cell_index] + \n"+
        "                      (double)signbit((double)1.0E-10 - bound_max_temprs[cell_index])*bound_tempr;\n"+
        "  \n"+
        "  init_temprs[cell_index] = init_tempr;\n"+
        "  \n"+
        "  bound_mech_influxes[cell_index] = (double)signbit(-0.5 + time_function_types[cell_index])*bound_max_mech_influxes[cell_index] + \n"+
        "                                    (double)signbit((float)-0.5 + fabs(time_function_types[cell_index] - (float)"+Common.CYCLE_LOADING_BYTE_VALUE+"))* \n"+
        "                   ((double)signbit( step_index_in_period - load_step_number_in_period + (double)0.5)*bound_max_mech_influxes[cell_index] + \n"+
        "                    (double)signbit(-step_index_in_period + load_step_number_in_period - (double)0.5)*bound_min_mech_influxes[cell_index]);\n"+
        "  \n"+
        "  barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}\n";
        
     //   double ratio = (double)1.0/((INNER_CELL - ADIABATIC_TEMPERATURE_CELL)*(INNER_CELL - ADIABATIC_ADIABATIC_CELL));
     //   byte  ratio_2 = ADIABATIC_ADIABATIC_CELL - ADIABATIC_TEMPERATURE_CELL;
     
        // Program text determining work of kernel
        String prog_string_1 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_1\n"+
                // Kernel variables are: 
                // 1. array of initial temperatures of cells; 
                // 2. array of heat capacities of cells;
                // 3. array of heat conductivities of cells; 
                // 4. array of densities of cells;
                // 5 -- (neighb1S_number+4). arrays of indices of neighbour cells (neighb1S_number for each cell) for all cells;
                // neighb1S_number+ 5. array of energy types for all cells; 
                // neighb1S_number+ 6. array of calculated temperatures of cells;
                // neighb1S_number+ 7. array of heat influxes to cells; 
                // neighb1S_number+ 8. index of current neighbour cell
                // neighb1S_number+ 9. the value for calculation of cell temperature; 
                // neighb1S_number+10. the value for testing.
        "(            __global const double *init_temprs,      __global const double *heat_cap,     \n"+
        "             __global const double *heat_cond,        __global const double *densities,    \n"; 
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_1 += 
        "             __global const int   *neighbours1S_"+neighb_counter+",\n";
        
        prog_string_1 += 
        "             __global const char   *en_types,         __global       double *temperatures, \n"+
        "             __global double       *heat_influxes,    __global const int    *neighb_id,    \n"+
        "             __global const double *factor,           __global       long   *step_counter, \n"+
        "             __global const double *heat_exp_coeff,   __global const int    *grain_indices,\n"+
        "             __global const double *init_heat_strain, __global       double *heat_strain_change,  \n"+
        "             __global const double *phonon_portion,   __global const double *grain_angles) \n"+
        "{   \n"+
         // Index of current cell
        "    int cell_index  = get_global_id(0);\n"+
        "    int grain_index = grain_indices[cell_index] - 1;\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_1 +=
        "    int neighb_id_"+neighb_counter+" = neighbours1S_"+ neighb_counter +"[cell_index];\n";
                
        prog_string_1 +=        
        "    \n"+
        "    heat_influxes[cell_index] = \n";
        
        // Calculation of heat influx from neighbour cells to current cell
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          prog_string_1 += 
        "                 (double)(1.0 - signbit(abs(en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*"+
                         "(double)(1.0 - signbit(abs(en_types[neighb_id_"+neighb_counter+"] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "                 (double)(1.0 - signbit(abs(en_types[cell_index] - "+STRESS_ADIABATIC_CELL+") - 0.5))*"+
                         "(double)(1.0 - signbit(abs(en_types[neighb_id_"+neighb_counter+"] - "+STRESS_ADIABATIC_CELL+") - 0.5))*\n"+
        "                 (double)(1.0 - signbit(abs(en_types[cell_index] - "+STRAIN_ADIABATIC_CELL+") - 0.5))*"+
                         "(double)(1.0 - signbit(abs(en_types[neighb_id_"+neighb_counter+"] - "+STRAIN_ADIABATIC_CELL+") - 0.5))*\n"+
        "                 (double)(1.0 - signbit(abs(en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*\n"+
        "                 (init_temprs[neighb_id_"+neighb_counter+"] - init_temprs[cell_index])*"+
                         "sqrt(heat_cond[cell_index]*heat_cond[neighb_id_"+neighb_counter+"])*\n"+
        "                 exp(-0.5*(phonon_portion[grain_index] + phonon_portion[grain_indices[neighb_id_"+neighb_counter+"] - 1])*"+
                              "(1.0 - signbit(abs(en_types[neighb_id_"+neighb_counter+"] - "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*\n"+
        "                     fabs(grain_angles[grain_index] - grain_angles[grain_indices[neighb_id_"+neighb_counter+"] - 1]))";
          
          if(neighb_counter < neighb1S_number-1)
            prog_string_1 +=  "+\n";
          else
            prog_string_1 +=  ";\n";
          
          prog_string_1 += 
        "      \n";
        }
        
        prog_string_1 += 
//        "    __syncthreads();\n"+
        "    \n"+
             // Calculation of new temperature of cell according to heat influx
        "    temperatures[cell_index] = init_temprs[cell_index] + heat_influxes[cell_index] *factor[0]/(heat_cap[cell_index] * densities[cell_index]);\n"+
//        "    __syncthreads();\n"+
        "    \n"+
             // Calculation of cell strain due to heat expansion
        "    heat_strain_change[cell_index] = heat_exp_coeff[grain_index]*(temperatures[cell_index] - init_temprs[cell_index]);\n"+
    //  "    heat_strain_change[cell_index] = heat_exp_coeff[grain_index]*heat_influxes[cell_index]*factor[0]/(heat_cap[cell_index]*densities[cell_index]);\n"+
    //    "    __syncthreads();\n"+
        "    \n"+
             // Test variables are used to keep the number of work-items and the number of work-groups.
        "    step_counter[0] = get_global_size(0);\n"+
    //  "    step_counter[0] = get_num_groups(0);// get_local_size(0);\n"+
        "    \n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        // Определяются новые значения температуры и деформации вследствие теплового расширения.
        String prog_string_2 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_2(__global double *init_temprs,      __global double *temperatures,\n"+
        "                       __global double *init_heat_strain, __global double *heat_strain_change)"+
        "{\n"+
        "    int cell_index                 = get_global_id(0);\n"+
        "    init_temprs[cell_index]        = temperatures[cell_index];\n"+
        "    \n"+
        "    double init_strain = init_heat_strain[cell_index];\n"+
        "    init_heat_strain[cell_index] = init_strain + heat_strain_change[cell_index];\n"+
   //   "    __syncthreads();\n"+
        "    \n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        // Factor before angle difference of adjacent grains
        double angle_diff_factor = 1.0/60.0;
        
        // Grain growth under recrystallization is simulated.
        String prog_string_3_1 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_3_1(__global const int *init_grain_indices, __global const double *grain_angles,\n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 += 
        "                           __global const int *neighbours1S_"+neighb_counter+",\n";
        
        // Addition of variables responsible for arrays of misorientation angles with neighbours
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 += 
        "                             __global double *spec_angle_diff_"+neighb_counter+",\n";
        
        // Addition of variables responsible for arrays of maximal mobilities at grain boundaries
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 += 
        "                               __global double *neighb_heat_max_mob_"+neighb_counter+",\n";
        
        // Specific energies of grain boundaries with neighbour cells
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 += 
        "                                 __global double *spec_bound_energies_"+neighb_counter+",\n";
        
        // Energies of grain boundaries with neighbour cells divided by kT
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 += 
        "                               __global double *bound_energies_"+neighb_counter+",\n";
        
        // velocities of grain growth at cell boundaries
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 += 
        "                             __global double *bound_velocities_"+neighb_counter+",\n";
        
        // probabilities of cell switch
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 += 
        "                           __global double *prob_"+neighb_counter+",\n";
        
        // Array of maximal heat mobilities for grains and array of maximal boundary energies
        prog_string_3_1 += 
        "                         __global const double *heat_max_mob, __global const double *max_bound_energy, \n"+
        "                         __global const double *init_temprs,  __global const char   *en_types,\n"+
        "                         __global const double *disl_max_mob, __global const double *mech_max_mob,\n"+
        "                         __global const double *disl_energy,  __global const double *mech_energy, \n";
        
        // Arrays of lengths and coordinates of lattice vectors and anisotropy coefficient
        prog_string_3_1 += 
        "                       __global double *lat_vec_A_length, __global double *lat_vec_B_length, __global double *lat_vec_C_length,\n"+
        "                       __global double *lat_vec_A_x,      __global double *lat_vec_A_y,      __global double *lat_vec_A_z,\n"+
        "                       __global double *lat_vec_B_x,      __global double *lat_vec_B_y,      __global double *lat_vec_B_z,\n"+
        "                       __global double *lat_vec_C_x,      __global double *lat_vec_C_y,      __global double *lat_vec_C_z,\n"+
        "                       __global double *lat_anis_coeff,   __global double *gr_vect_coord)\n";
            
        prog_string_3_1 += 
        "{\n"+
        "    int cell_index    = get_global_id(0);\n"+
        "    int init_gr_index = init_grain_indices[cell_index] - 1;\n\n";
              
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_1 +=
        "    //--------- neighbour cell # "+neighb_counter+" -----------------------------\n"+
        // Indices of neighbours
        "    int neighb_index_"+neighb_counter+" = neighbours1S_"+ neighb_counter +"[cell_index];\n"+
                  
        // Indices of grains containing current central cell and its neighbours
        "    int neighb_gr_indices_"+neighb_counter+" = init_grain_indices[neighb_index_"+neighb_counter+"] - 1;\n"+
                  
        // Misorientation angles for grains containing neighbour cells divided by maximal angle of misorientation
        "    spec_angle_diff_"+neighb_counter+"[cell_index] = "+angle_diff_factor+"*fabs(grain_angles[init_gr_index] - grain_angles[neighb_gr_indices_"+neighb_counter+"])/"+angleHAGB[0]+";\n\n"+
        
        // Specific energies of grain boundaries with neighbour cells
        "    spec_bound_energies_"+neighb_counter+"[cell_index] = sqrt(max_bound_energy[init_gr_index] * max_bound_energy[neighb_gr_indices_"+neighb_counter+"])*\n"+
        "                                    signbit(spec_angle_diff_"+neighb_counter+"[cell_index] - 1.0)*\n"+
        "                                    spec_angle_diff_"+neighb_counter+"[cell_index]*(1.0 - log(1.0E-10 + spec_angle_diff_"+neighb_counter+"[cell_index]));\n\n"+
                          
        // Maximal heat mobilities at boundaries of current cell with neighbour cells
        "    neighb_heat_max_mob_"+neighb_counter+"[cell_index] = sqrt(heat_max_mob[init_gr_index] * heat_max_mob[neighb_gr_indices_"+neighb_counter+"]);\n\n"+
        
        // energies of grain boundaries divided by kT for calculation of grain boundary mobility for each neighbour
        "    bound_energies_"+neighb_counter+"[cell_index] = spec_bound_energies_"+neighb_counter+"[cell_index]*"+(cell_surface/neighb1S_number)+"/"+
                                            "("+BOLTZMANN_CONST+"*sqrt((1.0E-10 + init_temprs[cell_index])*(1.0E-10 + init_temprs[neighb_index_"+neighb_counter+"])));\n\n"+
        
        // velocities of grain growth at cell boundaries
        "    bound_velocities_"+neighb_counter+"[cell_index] = signbit(init_gr_index - 0.5)*\n"+
        "                       (1.0 - signbit(init_temprs[cell_index]*init_temprs[neighb_index_"+neighb_counter+"] - 1.0E-10))*\n"+
        "                       (1.0 - signbit(1.0 - spec_angle_diff_"+neighb_counter+"[cell_index]))*\n"+
        "                       (1.0 - signbit(abs(neighb_gr_indices_"+neighb_counter+" - init_gr_index) - 0.5))*\n"+
        "                       (1.0 - signbit(abs(en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*"+
                               "(1.0 - signbit(abs(en_types[neighb_index_"+neighb_counter+"] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "                       (1.0 - signbit(abs(en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*"+
                               "(1.0 - signbit(abs(en_types[neighb_index_"+neighb_counter+"] - "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*\n"+
        "                       (1.0 - signbit(abs(en_types[cell_index] - "+STRAIN_ADIABATIC_CELL+") - 0.5))*"+
                               "(1.0 - signbit(abs(en_types[neighb_index_"+neighb_counter+"] - "+STRAIN_ADIABATIC_CELL+") - 0.5))*\n"+
        "                 signbit(spec_angle_diff_"+neighb_counter+"[cell_index] - 1.0)*exp(-bound_energies_"+neighb_counter+"[cell_index])*\n"+
        "          (signbit( - init_temprs[cell_index] + init_temprs[neighb_index_"+neighb_counter+"])*\n"+
        "           neighb_heat_max_mob_"+neighb_counter+"[cell_index]*( - init_temprs[cell_index] + init_temprs[neighb_index_"+neighb_counter+"])*"+HEAT_CAPACITY+"*"+DENSITY+" + \n"+
        "           sqrt(disl_max_mob[init_gr_index]*disl_max_mob[neighb_gr_indices_"+neighb_counter+"])*(disl_energy[init_gr_index] - disl_energy[neighb_gr_indices_"+neighb_counter+"]));// + \n"+
        "     //    sqrt(mech_max_mob[init_gr_index]*mech_max_mob[neighb_gr_indices_"+neighb_counter+"])*(fabs(mech_energy[cell_index]) - fabs(mech_energy[neighb_index_"+neighb_counter+"])));\n\n"+
        "    \n"+
        
        // Account of lattice anisotropy
        // cosinuses of angles between corresponding growth vectors and vectors of lattice anisotropy
        "    double cosinus_A_"+neighb_counter+" = fabs(gr_vect_coord["+ 3*neighb_counter+     "]*lat_vec_A_x[neighb_gr_indices_"+neighb_counter+"] + \n"+
        "                                              gr_vect_coord["+(3*neighb_counter + 1)+"]*lat_vec_A_y[neighb_gr_indices_"+neighb_counter+"] + \n"+
        "                                              gr_vect_coord["+(3*neighb_counter + 2)+"]*lat_vec_A_z[neighb_gr_indices_"+neighb_counter+"]); \n"+
        "    \n"+
        "    double cosinus_B_"+neighb_counter+" = fabs(gr_vect_coord["+ 3*neighb_counter+     "]*lat_vec_B_x[neighb_gr_indices_"+neighb_counter+"] + \n"+
        "                                              gr_vect_coord["+(3*neighb_counter + 1)+"]*lat_vec_B_y[neighb_gr_indices_"+neighb_counter+"] + \n"+
        "                                              gr_vect_coord["+(3*neighb_counter + 2)+"]*lat_vec_B_z[neighb_gr_indices_"+neighb_counter+"]); \n"+
        "    \n"+
        "    double cosinus_C_"+neighb_counter+" = fabs(gr_vect_coord["+ 3*neighb_counter+     "]*lat_vec_C_x[neighb_gr_indices_"+neighb_counter+"] + \n"+
        "                                              gr_vect_coord["+(3*neighb_counter + 1)+"]*lat_vec_C_y[neighb_gr_indices_"+neighb_counter+"] + \n"+
        "                                              gr_vect_coord["+(3*neighb_counter + 2)+"]*lat_vec_C_z[neighb_gr_indices_"+neighb_counter+"]); \n"+
        "    \n"+
        "    double max_cosinus_"+neighb_counter+" = (signbit(cosinus_B_"+neighb_counter+" - cosinus_A_"+neighb_counter+" - 1.0E-6)* \n"+
        "                                            signbit(cosinus_C_"+neighb_counter+" - cosinus_A_"+neighb_counter+" - 1.0E-6)*cosinus_A_"+neighb_counter+" + \n"+
        "                                            signbit(cosinus_C_"+neighb_counter+" - cosinus_B_"+neighb_counter+" - 1.0E-6)* \n"+
        "                                            signbit(cosinus_A_"+neighb_counter+" - cosinus_B_"+neighb_counter+" - 1.0E-6)*cosinus_B_"+neighb_counter+" + \n"+
        "                                            signbit(cosinus_A_"+neighb_counter+" - cosinus_C_"+neighb_counter+" - 1.0E-6)* \n"+
        "                                            signbit(cosinus_B_"+neighb_counter+" - cosinus_C_"+neighb_counter+" - 1.0E-6)*cosinus_C_"+neighb_counter+")/ \n"+
        "                                       (signbit(cosinus_B_"+neighb_counter+" - cosinus_A_"+neighb_counter+" - 1.0E-6)* \n"+
        "                                        signbit(cosinus_C_"+neighb_counter+" - cosinus_A_"+neighb_counter+" - 1.0E-6) +\n"+
        "                                        signbit(cosinus_C_"+neighb_counter+" - cosinus_B_"+neighb_counter+" - 1.0E-6)* \n"+
        "                                        signbit(cosinus_A_"+neighb_counter+" - cosinus_B_"+neighb_counter+" - 1.0E-6) +\n"+
        "                                        signbit(cosinus_A_"+neighb_counter+" - cosinus_C_"+neighb_counter+" - 1.0E-6)* \n"+
        "                                        signbit(cosinus_B_"+neighb_counter+" - cosinus_C_"+neighb_counter+" - 1.0E-6));\n"+
        "    \n"+
        "    double gr_vec_length_"+neighb_counter+" = \n"+
        "            pow((1.0 + signbit(fabs(max_cosinus_"+neighb_counter+" - cosinus_A_"+neighb_counter+") - 1.0E-6)*(lat_vec_A_length[neighb_gr_indices_"+neighb_counter+"] - 1))* \n"+
        "                (1.0 + signbit(fabs(max_cosinus_"+neighb_counter+" - cosinus_B_"+neighb_counter+") - 1.0E-6)*(lat_vec_B_length[neighb_gr_indices_"+neighb_counter+"] - 1))* \n"+
        "                (1.0 + signbit(fabs(max_cosinus_"+neighb_counter+" - cosinus_C_"+neighb_counter+") - 1.0E-6)*(lat_vec_C_length[neighb_gr_indices_"+neighb_counter+"] - 1)), \n"+
        "                 1.0/(signbit(fabs(max_cosinus_"+neighb_counter+" - cosinus_A_"+neighb_counter+") - 1.0E-6) + "+
        "                      signbit(fabs(max_cosinus_"+neighb_counter+" - cosinus_B_"+neighb_counter+") - 1.0E-6) + "+
        "                      signbit(fabs(max_cosinus_"+neighb_counter+" - cosinus_C_"+neighb_counter+") - 1.0E-6)));\n"+
        "    \n"+
        "    double anis_factor_"+neighb_counter+" = gr_vec_length_"+neighb_counter+"*exp(lat_anis_coeff[neighb_gr_indices_"+neighb_counter+"]*(max_cosinus_"+neighb_counter+" - 1));\n"+
        "    \n"+
        
        // switch probabilities at cell boundaries
        "    prob_"+neighb_counter+"[cell_index] = -signbit(bound_velocities_"+neighb_counter+"[cell_index])*anis_factor_"+neighb_counter+"*"+
                                                    "bound_velocities_"+neighb_counter+"[cell_index]*"+TIME_STEP+"/"+CELL_SIZE+";\n\n";
        
        // switch probabilities at cell boundaries
      //  "    prob_"+neighb_counter+"[cell_index] = -signbit(bound_velocities_"+neighb_counter+"[cell_index])*"+
      //                                              "bound_velocities_"+neighb_counter+"[cell_index]*"+TIME_STEP+"/"+CELL_SIZE+";\n\n";
        
        prog_string_3_1 +=
//        "    __syncthreads();\n"+
        "    \n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
      
        String prog_string_3_2 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
    //    "// #include <stdio.h>      /* printf, scanf, puts, NULL */ \n" +
    //    "// #include <stdlib.h>     /* srand, rand */ \n" +
    //    "// #include <time.h>       /* time */ \n\n"+
        "__kernel void kernel_3_2(__global const int *init_grain_indices, __global int *curr_gr_neighb_num,\n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_2 += 
        "                         __global const int *neighbours1S_"+neighb_counter+",\n";
        
        // Addition of variables responsible for arrays of switch probabilities
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_2 += 
        "                           __global const double *prob_"+neighb_counter+",\n";
        
        // Addition of variables responsible for arrays of sums of switch probabilities
        for(int neighb_counter = 0; neighb_counter < neighb1S_number+2; neighb_counter++)
          prog_string_3_2 += 
        "                             __global double *prob_sum_"+neighb_counter+",\n";
        
        // Addition of variables responsible for array of 0 and 1:
        // 0 - cell transition to corresponding grain is impossible, 1 - possible.
        for(int neighb_counter = 0; neighb_counter < neighb1S_number-1; neighb_counter++)
          prog_string_3_2 += 
        "                           __global char *poss_switch_"+neighb_counter+",\n";
        
          prog_string_3_2 += 
        "                           __global char *poss_switch_"+(neighb1S_number-1)+")\n";
      
          prog_string_3_2 += 
        "{\n"+
        "    int cell_index          = get_global_id(0);\n\n";
     //   "    int init_gr_index       = init_grain_indices[cell_index] - 1;\n\n";
              
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          prog_string_3_2 +=
      //  "    //--------- neighbour cell # "+neighb_counter+" -----------------------------\n"+
          // Indices of neighbours
        "    int neighb_index_"+neighb_counter+" = neighbours1S_"+ neighb_counter +"[cell_index];\n";
                  
          // Indices of grains containing current central cell and its neighbours
     //   "  int neighb_gr_indices_"+neighb_counter+" = init_grain_indices[neighb_index_"+neighb_counter+"] - 1;\n\n";
        }
      
        prog_string_3_2 +=
        "    \n"+
        "    // Calculation of number of neighbours in each adjacent grain.\n"+
        "    // If this number is less then the minimal one then cell cannot join to corresponding grain.\n";
        
        // Calculation of number of neighbours in each adjacent grain.
        // If this number is less then the minimal one then cell cannot join to corresponding grain.
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          prog_string_3_2 +=
        "    poss_switch_"+neighb_counter+"[cell_index] = (char)signbit("+min_neighbours_number+" - 0.5 - (\n";
          
          for(int pair_counter = 0; pair_counter < neighb1S_number; pair_counter++)
          {
            prog_string_3_2 +=
        "              signbit(abs(init_grain_indices[neighb_index_"+neighb_counter+"] - init_grain_indices[neighb_index_"+pair_counter+"]) - 0.5)";
            
            if(pair_counter < neighb1S_number-1)
              prog_string_3_2 += " + \n";
            else
              prog_string_3_2 += "));\n\n";
          }
        }
       
        prog_string_3_2 +=
        "    double pr_0  = poss_switch_0[cell_index]*prob_0[cell_index];\n"+
        "    double pr_1  = poss_switch_1[cell_index]*prob_1[cell_index];\n"+
        "    double pr_2  = poss_switch_2[cell_index]*prob_2[cell_index];\n"+
        "    double pr_3  = poss_switch_3[cell_index]*prob_3[cell_index];\n"+
        "    double pr_4  = poss_switch_4[cell_index]*prob_4[cell_index];\n"+
        "    double pr_5  = poss_switch_5[cell_index]*prob_5[cell_index];\n"+
        "    double pr_6  = poss_switch_6[cell_index]*prob_6[cell_index];\n"+
        "    double pr_7  = poss_switch_7[cell_index]*prob_7[cell_index];\n"+
        "    double pr_8  = poss_switch_8[cell_index]*prob_8[cell_index];\n"+
        "    double pr_9  = poss_switch_9[cell_index]*prob_9[cell_index];\n"+
        "    double pr_10 = poss_switch_10[cell_index]*prob_10[cell_index];\n"+
        "    double pr_11 = poss_switch_11[cell_index]*prob_11[cell_index];\n\n";
        
        prog_string_3_2 +=
        "    // Calculation of sums of probabilities of joining to each adjacent grain.\n";
        
        prog_string_3_2 += 
        "    double pr_sum_0 = 0.0;\n\n";
        
        for(int prob_counter = 1; prob_counter <= neighb1S_number; prob_counter++)
        {
          prog_string_3_2 += 
        "    double pr_sum_"+prob_counter+" = ";
          
          for(int curr_prob_counter = 0; curr_prob_counter < prob_counter; curr_prob_counter++)
          {
          //  prog_string_3_2 += "poss_switch_"+curr_prob_counter+"[cell_index]*prob_"+curr_prob_counter+"[cell_index]";
            prog_string_3_2 += "pr_"+curr_prob_counter;
          //  prog_string_3_2 += "0";
              
            if(curr_prob_counter < prob_counter - 1)
            {
              prog_string_3_2 += " + ";
              
            //  if((curr_prob_counter+1) %3 == 0)
              //  prog_string_3_2 += "\n                             ";
            }
            else
              prog_string_3_2 += ";\n\n";
          } 
        }
        
        // Calculation of neighbours in grain containing current cell at current time step
        prog_string_3_2 +=
        "    \n"+
        "    // Calculation of neighbours in grain containing current cell at current time step\n";
        
        prog_string_3_2 +=
        "    curr_gr_neighb_num[cell_index] = \n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
            prog_string_3_2 +=
        "              signbit(abs(init_grain_indices[cell_index] - init_grain_indices[neighb_index_"+neighb_counter+"]) - 0.5)";
            
            if(neighb_counter < neighb1S_number-1)
              prog_string_3_2 += " + \n";
            else
              prog_string_3_2 += ";\n\n";
        }
        
        for(int prob_counter = 1; prob_counter <= neighb1S_number; prob_counter++)
          prog_string_3_2+=
        "    prob_sum_"+prob_counter+"[cell_index] = pr_sum_"+prob_counter+"*signbit(1.0 - exp(pr_sum_"+neighb1S_number+"))/\n"+
        "                     (1.0 + signbit((double)curr_gr_neighb_num[cell_index] - "+min_neighbours_number+")*\n"+
        "                            signbit(1.0 - exp(pr_sum_"+neighb1S_number+" - 1.0E-4))*(-1.0 + 1.001*pr_sum_"+neighb1S_number+"));\n"+
        "    \n";
        
        prog_string_3_2 += 
        "    prob_sum_"+(neighb1S_number+1)+"[cell_index] = 1.0;\n\n"+
        "    \n"+
//        "    __syncthreads();\n"+
        "    \n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        // Program for realization of cell switch
        String prog_string_3_3 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_3_3(__global const int *init_grain_indices, __global int *grain_indices,\n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_3 += 
        "                         __global const int *neighbours1S_"+neighb_counter+",\n";
        
        // Addition of variables responsible for arrays of sums of switch probabilities
        for(int neighb_counter = 0; neighb_counter < neighb1S_number+2; neighb_counter++)
          prog_string_3_3 += 
        "                           __global const double *prob_sum_"+neighb_counter+",\n";
        
        prog_string_3_3 +=
        "                         __global const double *random_number)\n"+
        "{\n";
                
        prog_string_3_3 +=
        "    int cell_index = get_global_id(0);\n\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          prog_string_3_3 +=
      //  "    //--------- neighbour cell # "+neighb_counter+" -----------------------------\n"+
          // Indices of neighbours
        "    int neighb_index_"+neighb_counter+" = neighbours1S_"+ neighb_counter +"[cell_index];\n";
                  
          // Indices of grains containing current central cell and its neighbours
     //   "  int neighb_gr_indices_"+neighb_counter+" = init_grain_indices[neighb_index_"+neighb_counter+"] - 1;\n\n";
        }
        
        prog_string_3_3 +=
        "    \n"+
        "    // Determination of index of grain, which will contain current cell at next time step\n"+
        "    double rand = random_number[cell_index];\n\n"+
        "    grain_indices[cell_index] = 1 + (int)signbit(rand - prob_sum_1[cell_index])*(init_grain_indices[neighb_index_0] - 1) + \n";
        
        for(int neighb_counter = 1; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_3_3 +=
        "                                    (1 - (int)signbit(rand - prob_sum_"+neighb_counter+"[cell_index]))*"
                                               + "(int)signbit(rand - prob_sum_"+(neighb_counter+1)+"[cell_index])*"
                                               + "(init_grain_indices[neighb_index_"+neighb_counter+"] - 1) + \n";
        
        prog_string_3_3 +=
        "                                    (1 - (int)signbit(rand - prob_sum_"+neighb1S_number+"[cell_index]))*(init_grain_indices[cell_index] - 1);\n";
        
        prog_string_3_3+=
        "    \n"+
//        "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        String prog_string_4 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_4(__global int  *init_grain_indices,   __global const int  *grain_indices,\n" +
        "                       __global char *init_grain_rec_types, __global const char *grain_rec_types)\n" +
        "{   \n" +
        "    int  cell_index       = get_global_id(0);\n" +
        "    int  grain_index      = grain_indices[cell_index];\n" +
        "    int  init_grain_index = init_grain_indices[cell_index];\n" +
        "    \n" +
        "    init_grain_indices[cell_index] = signbit(init_grain_index - 1.5)*grain_index + signbit( - init_grain_index + 1.5)*init_grain_index;\n" +
        "    grain_index                    = init_grain_indices[cell_index];\n"+
        "    \n" +    
        "    init_grain_rec_types[grain_index - 1] = grain_rec_types[grain_index - 1];\n" +
        "    \n" +
//      "    __syncthreads();\n" +
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //=================================================================
        // Program text determining work of 5th kernel: simulation of mechanical loading
        String prog_string_5 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_5\n"+
                // Kernel variables are: 
                // 1. array of initial mechanical energies of cells; 
                // 2. array of Young modules of cells;
                // 3. array of maximal mechanical mobilities of cells; 
                // 4. array of maximal grain boundary energies;
                // 5. array of orientation angles of grains;
                // 6. array of maximal orientation angles of grains;
                // 7 -- (neighb1S_number+6). arrays of indices of neighbour cells (neighb1S_number for each cell) for all cells;
                // neighb1S_number+ 7. array of energy types for all cells; 
                // neighb1S_number+ 8. array of temperatures of cells;
                // neighb1S_number+ 9. index of current neighbour cell
                // neighb1S_number+10. index of grain containing current neighbour cell; 
                // neighb1S_number+11. array of mechanical energy influxes from neighbour cells; 
                // neighb1S_number+12. array of calculated mechanical energies of cells.
        "(            __global const double *max_bound_energy, __global const double *max_grain_angles, \n"+
        "             __global const double *grain_angles_1,   __global const double *grain_angles_2,   __global const double *grain_angles_3,";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_5 += 
        "             __global const int   *neighbours1S_"+neighb_counter+",\n";
        
        // Addition of variables responsible for misorientation angles for grains containing neighbour cells divided by maximal angle of misorientation
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_5 += 
        "             __global     double   *spec_angle_diff_"+neighb_counter+",\n";
        
        // Addition of variables responsible for specific energies of grain boundaries with neighbour cells
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_5 += 
        "             __global     double   *spec_bound_energy_"+neighb_counter+",\n";
        
        prog_string_5 += 
        "             __global const char  *en_types,           __global const int   *grain_indices)\n"+
        "{   \n"+
         // Index of current cell
        "    int cell_index  = get_global_id(0);\n"+
        "    int grain_index = grain_indices[cell_index] - 1;\n"+
        "    \n"+
        "    double angle_diff_1 = (double)0.0;\n"+
        "    double angle_diff_2 = (double)0.0;\n"+
        "    double angle_diff_3 = (double)0.0;\n"+
        "    \n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_5 +=
        // Misorientation angles for grains containing neighbour cells divided by maximal angle of misorientation
        "    angle_diff_1 = fabs(grain_angles_1[grain_index] - grain_angles_1[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1]);\n"+
        "    angle_diff_2 = fabs(grain_angles_2[grain_index] - grain_angles_2[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1]);\n"+
        "    angle_diff_3 = fabs(grain_angles_3[grain_index] - grain_angles_3[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1]);\n"+
        "    \n"+
        "    spec_angle_diff_"+neighb_counter+"[cell_index] = ((angle_diff_1 + angle_diff_2 + angle_diff_3)/(double)3.0)/\n"+
        "                            sqrt(max_grain_angles[grain_index]*max_grain_angles[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1]);\n\n"+
        
        // Specific energies of grain boundaries with neighbour cells
        "    spec_bound_energy_"+neighb_counter+"[cell_index] = \n"+
    //  "                     (1.0 - signbit(fabs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+STRAIN_ADIABATIC_CELL+") - 0.5))*\n"+
        "       signbit(abs(en_types[cell_index] - "+INNER_CELL+")*abs(en_types[cell_index] - "+LAYER_CELL+") - 0.5)*\n"+
        "       signbit(abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+INNER_CELL+")*abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+LAYER_CELL+") - 0.5)*\n"+
        "                     sqrt(max_bound_energy[grain_index] * max_bound_energy[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1])*\n"+
        "                     1.0/(1.0 + signbit(1.0 - spec_angle_diff_"+neighb_counter+"[cell_index])*(spec_angle_diff_"+neighb_counter+"[cell_index] - 1.0))*\n"+
        "                     spec_angle_diff_"+neighb_counter+"[cell_index]*(1.0 - signbit(spec_angle_diff_"+neighb_counter+"[cell_index] - 1.0)*\n"+
        "                     log(spec_angle_diff_"+neighb_counter+"[cell_index] + 1.0E-10 - 1.0E-10*signbit(1.0E-10 - spec_angle_diff_"+neighb_counter+"[cell_index])));\n\n";
        
        prog_string_5 +=
   //   "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        // Effective responce rate of simulated medium
        double responce_rate = (double)10.0; // (double)0.0; // 
        
        // Variable responsible for setting of hard boundary conditions:
        // if it is true  then outer boundary cells cannot change elastic energy under interaction with internal boundary cells,
        // if it is false then outer boundary cells can do it.
        boolean hard_bound_cond = false;
        
        if(specArrays.bound_interaction_type == (byte)0)
          hard_bound_cond = true;
        
        if(hard_bound_cond)
          System.out.println("Hard boundary conditions are realized.");
        else
          System.out.println("Soft boundary conditions are realized.");
        
        // Program text determining work of kernel 5.1
        String prog_string_5_1 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_5_1\n"+
                // Kernel variables are: 
                // 1. array of initial mechanical energies of cells; 
                // 2. array of Young modules of cells;
                // 3. array of maximal mechanical mobilities of cells; 
                // 4. array of maximal grain boundary energies;
                // 5. array of orientation angles of grains;
                // 6. array of maximal orientation angles of grains;
                // 7 -- (neighb1S_number+6). arrays of indices of neighbour cells (neighb1S_number for each cell) for all cells;
                // neighb1S_number+ 7. array of energy types for all cells; 
                // neighb1S_number+ 8. array of temperatures of cells;
                // neighb1S_number+ 9. index of current neighbour cell
                // neighb1S_number+10. index of grain containing current neighbour cell; 
                // neighb1S_number+11. array of mechanical energy influxes from neighbour cells; 
                // neighb1S_number+12. array of calculated mechanical energies of cells.
        "(            __global const double *init_mech_energies, __global const double *mod_elast, \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_5_1 += 
        "             __global const int   *neighbours1S_"+neighb_counter+",\n";
        
        // Addition of variables responsible for specific energies of grain boundaries with neighbour cells
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_5_1 += 
        "             __global const double *spec_bound_energy_"+neighb_counter+",\n";
        
        prog_string_5_1 += 
        "             __global const char   *en_types,            __global const double *init_temprs,      __global const int   *grain_indices,\n"+
        "             __global       double *mech_influxes,       __global       double *mech_energies,\n"+
        "             __global       long   *step_counter,        __global       long   *next_step_counter,\n"+
        "             __global const double *bound_mech_influxes, __global const double *heat_strains,\n"+
        "             __global const char  *time_function_types, __global const double *load_time_periods,\n"+
        "             __global const double *relax_time_periods)\n"+
        "{   \n"+
        // Index of current cell
        "    int cell_index  = get_global_id(0);\n"+
        "    int grain_index = grain_indices[cell_index] - 1;\n"+
        "    \n"+
        "    step_counter[0] = next_step_counter[0];\n"+
        "    \n"+
        "    char load_per_index = signbit(((step_counter[0] - 1) % (int)round(load_time_periods[cell_index] + relax_time_periods[cell_index])) - load_time_periods[cell_index]);\n"+
        "    \n"+
        "    double factor   = signbit((double)2.0*abs(time_function_types[cell_index] - "+CONSTANT_LOADING_BYTE_VALUE+") - 1) + \n"+
        "                     signbit((double)2.0*abs(time_function_types[cell_index] - "+CYCLE_LOADING_BYTE_VALUE+   ") - 1)*load_per_index + \n"+
        "                     signbit((double)2.0*abs(time_function_types[cell_index] - "+PERIODIC_LOADING_BYTE_VALUE+") - 1)*(1.5*load_per_index - 0.5) + \n"+
   //   "                        sinf((double)1.0*(step_counter[0] - 1)*(double)"+2*Math.PI+"/(load_time_periods[cell_index] + relax_time_periods[cell_index])) + \n"+
        "                     signbit((double)2.0*abs(time_function_types[cell_index] - "+CYCLE_LOADING_BYTE_VALUE_2+ ") - 1)*(2*load_per_index - 1);\n"+
        "    \n";
        
        if(hard_bound_cond)
          prog_string_5_1 += 
        "    // Hard boundary conditions are realized.\n";
        else
          prog_string_5_1 += 
        "    // Soft boundary conditions are realized.\n";
        
        prog_string_5_1 +=        
        "    \n"+
        "    mech_influxes[cell_index] = \n";
        
      //  if(false)
        // Calculation of mechanical energy influx from neighbour cells to current cell
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          prog_string_5_1 +=         
        "                 ((double)1.0 - signbit(abs(en_types[cell_index] -                                    "+ADIABATIC_ADIABATIC_CELL+") -   0.5))*\n"+
        "                 ((double)1.0 - signbit(abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") -   0.5))*\n"+
        "                 ((double)1.0 - signbit(abs(en_types[cell_index] -                                    "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*\n"+
        "                 ((double)1.0 - signbit(abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*\n"+
        "                 ((double)1.0 - signbit(abs(en_types[cell_index] -                                    "+ADIABATIC_THERMAL_CELL+") -     0.5))*\n"+
        "                 ((double)1.0 - signbit(abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+ADIABATIC_THERMAL_CELL+") -     0.5))*\n";
          
          if(hard_bound_cond)
            prog_string_5_1 +=  
        "                 ((double)1.0 - signbit(abs(en_types[cell_index] - "+STRAIN_ADIABATIC_CELL+") - 0.5))*\n"+
        "                 ((double)1.0 - signbit(abs(en_types[cell_index] - "+STRESS_ADIABATIC_CELL+") - 0.5))*\n";
          
          prog_string_5_1 +=  
        "                 (init_mech_energies[neighbours1S_"+ neighb_counter +"[cell_index]] + factor*bound_mech_influxes[neighbours1S_"+ neighb_counter +"[cell_index]] - \n"+
        "                  init_mech_energies[cell_index] - factor*bound_mech_influxes[cell_index])*\n"+
        "                 ((double)"+responce_rate+"/sqrt(mod_elast[grain_index] * mod_elast[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1]))*\n"+
        "                   exp(-pow(mod_elast[grain_index] - mod_elast[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1], (double)2.0)/ \n"+
        "                           (mod_elast[grain_index] * mod_elast[grain_indices[neighbours1S_"+ neighb_counter +"[cell_index]] - 1]))*\n"+
        "                   exp(-spec_bound_energy_"+neighb_counter+"[cell_index]*(double)"+(cell_surface/neighb1S_number)+"/((double)"+BOLTZMANN_CONST+"*(init_temprs[cell_index] + (double)1.0E-10)))*\n"+
        "                       mod_elast[grain_index]*(double)"+TIME_STEP+"/(double)"+CELL_SIZE;
          
          if(neighb_counter < neighb1S_number-1)
            prog_string_5_1 +=  " + \n";
          else
            prog_string_5_1 +=  ";\n";
          
          prog_string_5_1 += 
        "      \n";
        }
        
        prog_string_5_1 += 
//      "    __syncthreads();\n"+
        "    \n"+
             // Calculation of mechanical energy of cell according to mechanical energy influx
        "    mech_energies[cell_index] = init_mech_energies[cell_index] + factor*bound_mech_influxes[cell_index] + mech_influxes[cell_index] + mod_elast[grain_index]*heat_strains[cell_index];\n"+
        "    \n"+
        "    next_step_counter[0] = step_counter[0] + 1;\n"+
        "    \n"+
  //    "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        // Вычисление векторов напряжения для всех элементов клеточного автомата
        String prog_string_5_2 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_5_2(__global  double *stress_0, __global  double *stress_1, __global  double *stress_2, \n"+
        "                         __global  double *stress_3, __global  double *stress_4, __global  double *stress_5, \n"+
        "                         __global  double *stress_6, __global  double *stress_7, __global  double *stress_8, \n"+
        "                         __global  double *stress_9, __global  double *stress_10,__global  double *stress_11,\n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_5_2 += 
        "                         __global const int *neighbours1S_"+neighb_counter+",\n";
        
        prog_string_5_2 += 
        "                         __global const char *en_types, __global const double *mech_energies)\n"+
        "{\n"+
        "    // Index of current cell \n"+
        "    int cell_index = get_global_id(0);\n"+
        "    \n";
        
        // Calculation of stress vectors at cell boundaries
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          prog_string_5_2 += 
          "    \n"+
          "    stress_"+neighb_counter+"[cell_index] = \n";
          
          prog_string_5_2 += 
          "             (1.0 - signbit(abs(en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+") -   0.5))*\n"+
          "             (1.0 - signbit(abs(en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*\n"+
          "             (1.0 - signbit(abs(en_types[cell_index] - "+ADIABATIC_THERMAL_CELL+") -     0.5))*\n"+
          "             (1.0 - signbit(abs(en_types[cell_index] - "+STRAIN_ADIABATIC_CELL+") -      0.5))*\n"+
          "             (1.0 - signbit(abs(en_types[cell_index] - "+STRESS_ADIABATIC_CELL+") -      0.5))*\n"+
          "             (1.0 - signbit(abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") -   0.5))*\n"+
          "             (1.0 - signbit(abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+ADIABATIC_TEMPERATURE_CELL+") - 0.5))*\n"+
          "             (1.0 - signbit(abs(en_types[neighbours1S_"+ neighb_counter +"[cell_index]] - "+ADIABATIC_THERMAL_CELL+") -     0.5))*\n"+
          "                 (mech_energies[neighbours1S_"+ neighb_counter +"[cell_index]] - mech_energies[cell_index]);\n";
        }
        
        prog_string_5_2 += 
        "    \n"+
  //    "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        // Определение новых значений механической энергии и компонент векторов напряжений
        String prog_string_6 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_6(__global       double *init_mech_energies, __global const double *mech_energies, __global const double *neighb_vectors,\n"+
        "                       __global const double *stress_0, __global const double *stress_1, __global const double *stress_2, \n"+
        "                       __global const double *stress_3, __global const double *stress_4, __global const double *stress_5, \n"+
        "                       __global const double *stress_6, __global const double *stress_7, __global const double *stress_8, \n"+
        "                       __global const double *stress_9, __global const double *stress_10,__global const double *stress_11,\n"+
        "                       __global       double *stress_X, __global       double *stress_Y, __global       double *stress_Z)\n"+
        "{\n"+
        "    int  cell_index = get_global_id(0);\n"+
        "    \n"+
        "    init_mech_energies[cell_index] = mech_energies[cell_index];\n"+
        "    \n";
        
        prog_string_6 += 
        "    // Calculation of stress vector for current cell \n"+
        "    stress_X[cell_index] = neighb_vectors[ 0]*stress_0[cell_index] + neighb_vectors[ 3]*stress_1[cell_index] + neighb_vectors[ 6]*stress_2[cell_index]  + neighb_vectors[ 9]*stress_3[cell_index] + \n"+
        "                           neighb_vectors[12]*stress_4[cell_index] + neighb_vectors[15]*stress_5[cell_index] + neighb_vectors[18]*stress_6[cell_index]  + neighb_vectors[21]*stress_7[cell_index] + \n"+
        "                           neighb_vectors[24]*stress_8[cell_index] + neighb_vectors[27]*stress_9[cell_index] + neighb_vectors[30]*stress_10[cell_index] + neighb_vectors[33]*stress_11[cell_index]; \n"+
        "    \n"+
        "    stress_Y[cell_index] = neighb_vectors[ 1]*stress_0[cell_index] + neighb_vectors[ 4]*stress_1[cell_index] + neighb_vectors[ 7]*stress_2[cell_index]  + neighb_vectors[10]*stress_3[cell_index] + \n"+
        "                           neighb_vectors[13]*stress_4[cell_index] + neighb_vectors[16]*stress_5[cell_index] + neighb_vectors[19]*stress_6[cell_index]  + neighb_vectors[22]*stress_7[cell_index] + \n"+
        "                           neighb_vectors[25]*stress_8[cell_index] + neighb_vectors[28]*stress_9[cell_index] + neighb_vectors[31]*stress_10[cell_index] + neighb_vectors[34]*stress_11[cell_index]; \n"+
        "    \n"+
        "    stress_Z[cell_index] = neighb_vectors[ 2]*stress_0[cell_index] + neighb_vectors[ 5]*stress_1[cell_index] + neighb_vectors[ 8]*stress_2[cell_index]  + neighb_vectors[11]*stress_3[cell_index] + \n"+
        "                           neighb_vectors[14]*stress_4[cell_index] + neighb_vectors[17]*stress_5[cell_index] + neighb_vectors[20]*stress_6[cell_index]  + neighb_vectors[23]*stress_7[cell_index] + \n"+
        "                           neighb_vectors[26]*stress_8[cell_index] + neighb_vectors[29]*stress_9[cell_index] + neighb_vectors[32]*stress_10[cell_index] + neighb_vectors[35]*stress_11[cell_index]; \n"+
        "    \n"+
//      "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        // Вычисление компонент моментов сил исходя из векторов напряжений соседних элементов на 1-ой координационной сфере (новый метод)
        String prog_string_7_mom = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_7_mom(__global const double *stress_X, __global const double *stress_Y, __global const double *stress_Z, \n"+
        "                           __global const double *coord_X,  __global const double *coord_Y,  __global const double *coord_Z,  \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_7_mom += 
        "                         __global const int *neighb1S_index_"+neighb_counter+",\n";
        
        prog_string_7_mom += 
        "                         __global const char *en_types, __global double *sum_force_moment_X, __global double *sum_force_moment_Y, __global double *sum_force_moment_Z)\n"+
        "{\n"+
        "    // Index of current central cell\n" +
        "    int cell_index  = get_global_id(0);\n" +
        "    \n" +
        "    // Calculation of component X of summary force moment for current central cell\n"+
        "    sum_force_moment_X[cell_index] = (double)"+CELL_SIZE*2*cell_surface/(neighb1S_number*cell_volume)+"*\n"+
        "       (signbit(abs((en_types[cell_index] - "+INNER_CELL+")*(en_types[cell_index] - "+LAYER_CELL+")) - 0.5))*\n"+
     //   "       (1.0 - signbit(abs((en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+")*\n"+
     //   "                                  (en_types[cell_index] - "+STRAIN_ADIABATIC_CELL+")*\n"+
     //   "                                  (en_types[cell_index] - "+STRESS_ADIABATIC_CELL+")*\n"+
     //   "                                  (en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+")*\n"+
     //   "                                  (en_types[cell_index] - "+ADIABATIC_THERMAL_CELL+")) - 0.5))*\n"+
        "    (\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_7_mom +=
        "      (1.0 - signbit(abs((en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+")*\n"+
        "                         (en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_TEMPERATURE_CELL+")*\n"+
        "                         (en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_THERMAL_CELL+")) - (double)0.5))* \n"+
        "      (stress_Z[neighb1S_index_"+neighb_counter+"[cell_index]]*(coord_Y[neighb1S_index_"+neighb_counter+"[cell_index]] - coord_Y[cell_index])  - \n"+
        "       stress_Y[neighb1S_index_"+neighb_counter+"[cell_index]]*(coord_Z[neighb1S_index_"+neighb_counter+"[cell_index]] - coord_Z[cell_index])) + \n"+
        "    \n";
            
        prog_string_7_mom += 
        "    0.0);\n"+
        "    \n"+
        "    // Calculation of component Y of summary force moment for current central cell\n"+
        "    sum_force_moment_Y[cell_index] = (double)"+CELL_SIZE*2*cell_surface/(neighb1S_number*cell_volume)+"*\n"+
        "       (signbit(abs((en_types[cell_index] - "+INNER_CELL+")*(en_types[cell_index] - "+LAYER_CELL+")) - 0.5))*\n"+
     //   "       (1.0 - signbit(abs((en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+")*\n"+
     //   "                          (en_types[cell_index] - "+STRAIN_ADIABATIC_CELL+")*\n"+
     //   "                          (en_types[cell_index] - "+STRESS_ADIABATIC_CELL+")*\n"+
     //   "                          (en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+")*\n"+
     //   "                          (en_types[cell_index] - "+ADIABATIC_THERMAL_CELL+")) - 0.5))*\n"+
        "    (\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_7_mom +=
        "      (1.0 - signbit(abs((en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+")*\n"+
        "                         (en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_TEMPERATURE_CELL+")*\n"+
        "                         (en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_THERMAL_CELL+")) - 0.5))* \n"+
        "      (stress_X[neighb1S_index_"+neighb_counter+"[cell_index]]*(coord_Z[neighb1S_index_"+neighb_counter+"[cell_index]] - coord_Z[cell_index])  - \n"+
        "       stress_Z[neighb1S_index_"+neighb_counter+"[cell_index]]*(coord_X[neighb1S_index_"+neighb_counter+"[cell_index]] - coord_X[cell_index])) + \n"+
        "    \n";
        
        prog_string_7_mom += 
        "    0.0);\n"+
        "    \n"+
        "    // Calculation of component Z of summary force moment for current central cell\n"+
        "    sum_force_moment_Z[cell_index] = (double)"+CELL_SIZE*2*cell_surface/(neighb1S_number*cell_volume)+"*\n"+
        "       (signbit(abs((en_types[cell_index] - "+INNER_CELL+")*(en_types[cell_index] - "+LAYER_CELL+")) - 0.5))*\n"+
      //  "       (1.0 - signbit(abs((en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+")*\n"+
      //  "                          (en_types[cell_index] - "+STRAIN_ADIABATIC_CELL+")*\n"+
      //  "                          (en_types[cell_index] - "+STRESS_ADIABATIC_CELL+")*\n"+
      //  "                          (en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+")*\n"+
      //  "                          (en_types[cell_index] - "+ADIABATIC_THERMAL_CELL+")) - 0.5))*\n"+
        "    (\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_7_mom +=
        "      (1.0 - signbit(abs((en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+")*\n"+
        "                         (en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_TEMPERATURE_CELL+")*\n"+
        "                         (en_types[neighb1S_index_"+neighb_counter+"[cell_index]] - "+ADIABATIC_THERMAL_CELL+")) - 0.5))* \n"+
        "      (stress_Y[neighb1S_index_"+neighb_counter+"[cell_index]]*(coord_X[neighb1S_index_"+neighb_counter+"[cell_index]] - coord_X[cell_index])  - \n"+
        "       stress_X[neighb1S_index_"+neighb_counter+"[cell_index]]*(coord_Y[neighb1S_index_"+neighb_counter+"[cell_index]] - coord_Y[cell_index])) + \n"+
        "    \n";
        
        prog_string_7_mom += 
        "    (double)0.0);\n"+
        "    \n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        // Variable responcible for calculation of moments according to the values of stress vectors
        boolean new_moments = true; // false; // 
        
        //===========================================================================
        // Вычисление компонент X моментов сил исходя из 1-ой координационной сферы для каждого элемента (старый метод)
        String prog_string_7 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_7(__global const double *init_mech_energies, __global const char  *en_types,\n"+
        "         __global const double *coordinates_X, __global const double *coordinates_Y,      __global const double *coordinates_Z, \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_7 += 
        "         __global const int *neighb1S_index_"+4*neighb_counter+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+1)+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+2)+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+3)+", \n";
        
        prog_string_7 += 
        "         __global       double *sum_force_moment_X)\n"+
        "{\n"+
        "    // Index of current central cell\n" +
        "    int cell_index  = get_global_id(0);\n" +
        "\n" +
    //    "    // Indices of 2 neighbour cells at 1st sphere of current cell\n" +
    //    "    int first_neighb1S_index;\n" +
     //   "    int second_neighb1S_index;\n" +
    //    "\n" +
    //    "    // Coordinates of vector from current \"central\" cell to its current 1st neighbour\n" +
    //    "    // These coordinates are expressed in meters\n" +
     //   "    double cell1S_vector_X, cell1S_vector_Y, cell1S_vector_Z;\n" +
    //    "\n" +
    //    "    // Stress from 1st neighbour located at 1st coordination sphere of \"central\" cell to 2nd neighbour\n" +
    //    "    double stress_between_neighbours;\n" +
    //    "\n" +
    //    "    // Coordinates of vector from 1st neighbour to 2nd neighbour\n" +
    //    "    double vector_between_neighbours_X, vector_between_neighbours_Y, vector_between_neighbours_Z;"+
    //    "\n"+
    //    "    // ====== Calculation of summary force moment ======\n\n";
        
     //   "    // Calculation of variables for determination of force moment for current pair of neighbour cells"+
     //   "    first_neighb1S_index  = neighb1S_index_"+ 2*cell_pair_counter+"[cell_index];\n" +
     //   "    second_neighb1S_index = neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index];\n" +
     //   "\n" +
    //    "    cell1S_vector_X = (coordinates_X[first_neighb1S_index] - coordinates_X[cell_index])*"+CELL_SIZE+";\n" +
    //    "    cell1S_vector_Y = (coordinates_Y[first_neighb1S_index] - coordinates_Y[cell_index])*"+CELL_SIZE+";\n" +
    //    "    cell1S_vector_Z = (coordinates_Z[first_neighb1S_index] - coordinates_Z[cell_index])*"+CELL_SIZE+";\n" +
    //    "\n" +
    //    "    // Stress from 1st neighbour located at 1st coordination sphere of central cell to 2nd neighbour\n" +
   //     "    stress_between_neighbours = (mech_energies[first_neighb1S_index] - mech_energies[second_neighb1S_index]);\n" +
   //     "\n" +
  //     "    // Vector from 1st neighbour to 2nd neighbour with length  1 \n" +
    //    "    // multiplied by the stress between neighbours (Pa) and by the surface of boundary facet (m2)\n" +
    //    "    vector_between_neighbours_X = (coordinates_X[second_neighb1S_index] - coordinates_X[first_neighb1S_index])*\n" +
    //    "                                   stress_between_neighbours*"+cell_surface/(neighb1S_number*cell_volume)+";\n"+
    //    "\n" +
     //   "    vector_between_neighbours_Y = (coordinates_Y[second_neighb1S_index] - coordinates_Y[first_neighb1S_index])*\n" +
    //    "                                   stress_between_neighbours*"+cell_surface/(neighb1S_number*cell_volume)+";\n"+
    //    "\n" +
    //    "    vector_between_neighbours_Z = (coordinates_Z[second_neighb1S_index] - coordinates_Z[first_neighb1S_index])*stress_between_neighbours*"+cell_surface/(neighb1S_number*cell_volume)+";\n"+
    //    "\n"+
    
    //    "    // force_moment = calcVectorProduct(cell1S_vector, vector_between_neighbours);\n" +
    //    "    // summary_force_moment.add(force_moment);\n" +
     //   "    sum_force_moment_X[cell_index] += cell1S_vector_Y*vector_between_neighbours_Z - cell1S_vector_Z*vector_between_neighbours_Y;\n" +
     //   "    sum_force_moment_Y[cell_index] += cell1S_vector_Z*vector_between_neighbours_X - cell1S_vector_X*vector_between_neighbours_Z;\n" +
     //   "    sum_force_moment_Z[cell_index] += cell1S_vector_X*vector_between_neighbours_Y - cell1S_vector_Y*vector_between_neighbours_X;";
        
    //    prog_string_7 += 
        "    // Calculation of component X of summary force moment for current central cell\n"+
        "    sum_force_moment_X[cell_index] = "+CELL_SIZE*cell_surface/(neighb1S_number*cell_volume)+
                       "*(1.0 - signbit(abs((en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+")*(en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+")) - 0.5))*\n"+
        "    (\n";
        
        for(int cell_pair_counter = 0; cell_pair_counter < 2*neighb1S_number; cell_pair_counter++)
          prog_string_7 += 
        "      (1.0 - signbit(abs(en_types[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "      (1.0 - signbit(abs(en_types[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "      (init_mech_energies[neighb1S_index_"+  2*cell_pair_counter+"[cell_index]] - init_mech_energies[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]])*\n"+
        "      ((coordinates_Y[neighb1S_index_"+ 2*cell_pair_counter+     "[cell_index]] - coordinates_Y[cell_index])*\n"+
        "       (coordinates_Z[neighb1S_index_"+(2*cell_pair_counter+1)+  "[cell_index]] - coordinates_Z[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]]) - \n"+
        "       (coordinates_Z[neighb1S_index_"+ 2*cell_pair_counter+     "[cell_index]] - coordinates_Z[cell_index])*\n"+
        "       (coordinates_Y[neighb1S_index_"+(2*cell_pair_counter+1)+  "[cell_index]] - coordinates_Y[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]])) + \n\n";
            
        prog_string_7 += 
        "    0.0);\n"+
        "    \n"+
//      "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        // Вычисление компонент Y моментов сил исходя из 1-ой координационной сферы для каждого элемента (старый метод)
        String prog_string_7_1 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_7_1(__global const double *init_mech_energies, __global const char  *en_types,\n"+
        "         __global const double *coordinates_X,     __global const double *coordinates_Y,      __global const double *coordinates_Z, \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_7_1 += 
        "         __global const int *neighb1S_index_"+ 4*neighb_counter+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+1)+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+2)+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+3)+", \n";
        
        prog_string_7_1 += 
        "         __global double *sum_force_moment_Y)\n"+
        "{    \n"+
        "    // Index of current central cell\n" +
        "    int cell_index  = get_global_id(0);\n" +
        "    \n" +
        "    // Calculation of component Y of summary force moment for current central cell\n"+
        "    sum_force_moment_Y[cell_index] = "+CELL_SIZE*cell_surface/(neighb1S_number*cell_volume)+
              "*(1.0 - signbit(abs((en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+")*(en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+")) - 0.5))*\n"+
        "    (\n";
        
        for(int cell_pair_counter = 0; cell_pair_counter < 2*neighb1S_number; cell_pair_counter++)
          prog_string_7_1 += 
        "      (1.0 - signbit(abs(en_types[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "      (1.0 - signbit(abs(en_types[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "      (init_mech_energies[neighb1S_index_" + 2*cell_pair_counter+   "[cell_index]] - init_mech_energies[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]])*\n"+
        "      ((coordinates_Z[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]] - coordinates_Z[cell_index])*\n"+
        "       (coordinates_X[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]] - coordinates_X[neighb1S_index_"+2*cell_pair_counter+"[cell_index]]) - \n"+
        "       (coordinates_X[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]] - coordinates_X[cell_index])*\n"+
        "       (coordinates_Z[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]] - coordinates_Z[neighb1S_index_"+2*cell_pair_counter+"[cell_index]])) + \n\n";
        
        prog_string_7_1 += 
        "    0);\n"+
        "    \n"+
//        "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        // Вычисление компонент Z моментов сил исходя из 1-ой координационной сферы для каждого элемента (старый метод)
        String prog_string_7_2 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_7_2(__global const double *init_mech_energies, __global const char  *en_types,\n"+
        "         __global const double *coordinates_X,     __global const double *coordinates_Y,      __global const double *coordinates_Z, \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_7_2 += 
        "         __global const int *neighb1S_index_"+ 4*neighb_counter+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+1)+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+2)+", "+
                 "__global const int *neighb1S_index_"+(4*neighb_counter+3)+", \n";
        
        prog_string_7_2 += 
        "         __global double *sum_force_moment_Z)\n"+
        "{    \n"+
        "    // Index of current central cell\n" +
        "    int cell_index  = get_global_id(0);\n" +
        "    \n" +
        "    // Calculation of component Z of summary force moment for current central cell\n"+
        "    sum_force_moment_Z[cell_index] = "+CELL_SIZE*cell_surface/(neighb1S_number*cell_volume)+
                   "*(1.0 - signbit(abs((en_types[cell_index] - "+ADIABATIC_ADIABATIC_CELL+")*(en_types[cell_index] - "+ADIABATIC_TEMPERATURE_CELL+")) - 0.5))*\n"+
        "    (\n";
        
        for(int cell_pair_counter = 0; cell_pair_counter < 2*neighb1S_number; cell_pair_counter++)
          prog_string_7_2 += 
        "      (1.0 - signbit(abs(en_types[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "      (1.0 - signbit(abs(en_types[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]] - "+ADIABATIC_ADIABATIC_CELL+") - 0.5))*\n"+
        "      (init_mech_energies[neighb1S_index_" + 2*cell_pair_counter+"[cell_index]] - init_mech_energies[neighb1S_index_"   +(2*cell_pair_counter+1)+"[cell_index]])*\n"+
        "      ((coordinates_X[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]] - coordinates_X[cell_index])*\n"+
        "       (coordinates_Y[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]] - coordinates_Y[neighb1S_index_"+2*cell_pair_counter+"[cell_index]]) - \n"+
        "       (coordinates_Y[neighb1S_index_"+ 2*cell_pair_counter+   "[cell_index]] - coordinates_Y[cell_index])*\n"+
        "       (coordinates_X[neighb1S_index_"+(2*cell_pair_counter+1)+"[cell_index]] - coordinates_X[neighb1S_index_"+2*cell_pair_counter+"[cell_index]])) + \n\n";
        
        prog_string_7_2 += 
        "    0);\n"+
        "    \n"+
//        "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        double coeff_defects = (double)1.0E-9;// 0.001;// 
        
        // Variable is responsible for change of mechanical energy due to change of torsion energy of cell
        boolean change_mech_energy = true; // false; //  
        
        // Variable is responsible for application of new method of mechanical energy change due to change of torsion energy of cell
        boolean mech_en_change_new = true; // false; // 
        
        // Variables for choice of criterion of crack generation
        boolean crack_criterion_mom  = true; // false; // 
        boolean crack_criterion_mech = false; // true; // 
        
        // The threshold value for the mechanical energy governing the process of crack generation
        double mech_en_threshold = 3.0E8;
        
        // Вычисление изменения механической энергии на основе значений моментов сил каждого элемента
        String prog_string_8 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_8( \n"+
        "         __global const char  *en_types,      __global const int   *grain_indices, __global const double *mod_shear, \n"+
        "         __global const double *mom_X,         __global const double *mom_Y,         __global const double *mom_Z, \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8 += 
        "         __global const int   *neighbours1S_"+neighb_counter+",\n";
        
        prog_string_8 += 
        "         __global const double *init_mech_en,  __global       double *mech_energies,      __global       double *diss_en_change, \n"+
        "         __global const char   *loc_types,     __global       double *abs_mom,            __global       double *crack_value, \n"+
        "         __global const double *lat_param,     __global       double *def_density_change, __global const double *coeff_diss, \n"+
        "         __global       double *angle_vel_X,   __global       double *angle_vel_Y,        __global       double *angle_vel_Z,  __global double *abs_angle_vel, \n"+
        "         __global       double *tors_angle_X,  __global       double *tors_angle_Y,       __global       double *tors_angle_Z, __global double *abs_tors_angle, \n"+
        "         __global const double *mech_influxes, __global       double *relative_diss_en_change)\n"+
        "{\n"+
        "    // Index of current central cell\n" +
        "    int cell_index  = get_global_id(0);\n" +
        "    \n"+
        "    abs_mom[cell_index] = mom_X[cell_index]*mom_X[cell_index] + mom_Y[cell_index]*mom_Y[cell_index] + mom_Z[cell_index]*mom_Z[cell_index];\n"+
        "    \n"+
        "    diss_en_change[cell_index] = (1.0 - signbit(0.5 - abs((en_types[cell_index] - "+LAYER_CELL+")*(en_types[cell_index] - "+INNER_CELL+"))))*\n"+
        "                                  abs_mom[cell_index]*coeff_diss[grain_indices[cell_index] - 1]*(double)"+64.0/Math.PI+"/mod_shear[grain_indices[cell_index] - 1];\n"+
        "    \n";
        
        if(!change_mech_energy)
        {
          prog_string_8 += 
        "    mech_energies[cell_index] = init_mech_en[cell_index];\n"+
        "    \n";
        }
        else
        {
          if(!mech_en_change_new)
          {
            prog_string_8 += 
        "    mech_energies[cell_index] = (1.0 - 2.0*signbit(init_mech_en[cell_index]))*\n"+
        "                                (1.0 - signbit(fabs(init_mech_en[cell_index]) - diss_en_change[cell_index]))*\n"+
        "                                          fabs(fabs(init_mech_en[cell_index]) - diss_en_change[cell_index]);\n"+
        "    \n"+
        "    double real_diss_en_change = signbit( fabs(init_mech_en[cell_index]) - diss_en_change[cell_index])*fabs(init_mech_en[cell_index]) + \n"+
        "                                 signbit(-fabs(init_mech_en[cell_index]) + diss_en_change[cell_index])*diss_en_change[cell_index];"+
        "    \n"+
        "    diss_en_change[cell_index] = real_diss_en_change;\n"+
        "    \n";
          }
          else
          {
              prog_string_8 += 
        "    double diss_en_ch       = diss_en_change[cell_index];\n"+
        "    double total_abs_energy = fabs(init_mech_en[cell_index]) + diss_en_change[cell_index];\n"+
        "    \n"+
        "    mech_energies[cell_index]  = init_mech_en[cell_index]*fabs(init_mech_en[cell_index])/(total_abs_energy + (double)1.0E-30*signbit(total_abs_energy - (double)1.0E-30));\n"+
        "    \n"+
        "    diss_en_change[cell_index] = diss_en_ch*fabs(init_mech_en[cell_index])/(total_abs_energy + (double)1.0E-30*signbit(total_abs_energy - (double)1.0E-30));\n"+
        "    \n";
          }
        }
        
        prog_string_8 += 
        "    // Calculation of relative change of dissipation energy in comparison with current change of total energy \n"+
        "    relative_diss_en_change[cell_index] = diss_en_change[cell_index]/(fabs(mech_influxes[cell_index]) + diss_en_change[cell_index] + (double)1.0E-30*signbit(fabs(mech_influxes[cell_index]) + diss_en_change[cell_index] - (double)1.0E-30));\n"+
     //   "    \n"+
     //   "    double max_0 = (double)-1.0E10;\n"+
        "    \n"+
     //   "    double diff_mom_X, diff_mom_Y, diff_mom_Z; \n"+
     //   "    double neighb_abs_mom, mom_cosinus; // ,abs_diff_mom_sqr;\n"+
        "    char neighb_loc_type; \n"+
     //   "    double threshold_0, threshold; \n"+
     //   "    \n"+
     //   "    threshold = (double)0.5; // 0.99; // *signbit(fabs((double)1.0*loc_types[cell_index] - (double)"+BOUNDARY_CELL+") - (double)0.5) + \n"+
     //   "             // (double)1.00E30*signbit((double)0.5 - fabs((double)1.0*loc_types[cell_index] - (double)"+BOUNDARY_CELL+")); \n"+
        "    \n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8 += 
        "    int neighb1S_"+neighb_counter+" = neighbours1S_"+ neighb_counter +"[cell_index];"+
        "    \n"+
        "    double abs_diff_mom_sqr_"+neighb_counter+" = \n"+
        "       //   signbit(fabs(((double)1.0*en_types[cell_index] - (double)"+INNER_CELL+")*((double)1.0*en_types[cell_index] - (double)"+LAYER_CELL+")) - (double)0.5)*\n"+
        "       //   signbit(fabs(((double)1.0*en_types[neighb1S_"+neighb_counter+"] - (double)"+INNER_CELL+")*((double)1.0*en_types[neighb1S_"+neighb_counter+"] - (double)"+LAYER_CELL+")) - (double)0.5)*\n"+
        "             (mom_X[neighb1S_"+neighb_counter+"] - mom_X[cell_index])*(mom_X[neighb1S_"+neighb_counter+"] - mom_X[cell_index]) + \n"+
        "             (mom_Y[neighb1S_"+neighb_counter+"] - mom_Y[cell_index])*(mom_Y[neighb1S_"+neighb_counter+"] - mom_Y[cell_index]) + \n"+
        "             (mom_Z[neighb1S_"+neighb_counter+"] - mom_Z[cell_index])*(mom_Z[neighb1S_"+neighb_counter+"] - mom_Z[cell_index]);  \n"+
        "    \n"+
     //   "    double neighb_abs_mom_"+neighb_counter+" = \n"+
     //   "              (double)    (mom_X[neighb1S_"+neighb_counter+"]*mom_X[neighb1S_"+neighb_counter+"] +\n"+
     //   "                          mom_Y[neighb1S_"+neighb_counter+"]*mom_Y[neighb1S_"+neighb_counter+"] +\n"+
     //   "                          mom_Z[neighb1S_"+neighb_counter+"]*mom_Z[neighb1S_"+neighb_counter+"]); \n"+
     //   "    \n"+
     //   "    double mom_cosinus_"+neighb_counter+" = signbit((double)1.0E-6 - abs_mom[cell_index]*neighb_abs_mom_"+neighb_counter+")*\n"+
     //   "                                          (mom_X[cell_index]*mom_X[neighb1S_"+neighb_counter+"] + \n"+
     //   "                                           mom_Y[cell_index]*mom_Y[neighb1S_"+neighb_counter+"] + \n"+
     //   "                                           mom_Z[cell_index]*mom_Z[neighb1S_"+neighb_counter+"])/ \n"+
     //   "                   (signbit(abs_mom[cell_index]*neighb_abs_mom_"+neighb_counter+" - (double)1.0E-6) + abs_mom[cell_index]*neighb_abs_mom_"+neighb_counter+"); \n"+
        "    \n"+
   /*   "    neighb_loc_type = loc_types[neighb1S_"+neighb_counter+"];\n"+        
        "    \n"+
        "    threshold_0   = threshold;// + ((double)2000.5 - threshold)* // ((double)1.01 - threshold)*\n"+
        "                           //      (double)signbit(fabs((double)1.0*neighb_loc_type - (double)"+CRACK_CELL+") - (double)0.5); // * \n"+
        "                           //    (double)signbit((double)0.5 - fabs((double)1.0*loc_types[cell_index] - (double)"+BOUNDARY_CELL+"));\n"+
        "    \n"+
        "    // Variable responsible for cracking of cell \n"+
        "    char crack_precursor_"+neighb_counter+" = \n"+
        "         signbit(fabs((double)1.0*en_types[cell_index] - (double)"+INNER_CELL+") - (double)0.5)*\n"+
        "      //   signbit(fabs((double)1.0*en_types[neighb1S_"+neighb_counter+"] - (double)"+INNER_CELL+") - (double)0.5)*\n"+
        "         signbit( (double)0.5 - (double)fabs(\n"+
        "          //       ((double)1.0*neighb_loc_type - (double)"+CRACK_CELL+")* \n"+
        "                 ((double)1.0*neighb_loc_type - (double)"+INNER_BOUNDARY_CELL+")* \n"+
        "                 ((double)1.0*neighb_loc_type - (double)"+INNER_BOUNDARY_INTERGRANULAR_CELL+")* \n"+
        "              //    (double)(signbit((double)fabs(loc_types[cell_index] - (double)"+BOUNDARY_CELL+") - (double)0.5) + \n"+
        "              //            signbit((double)0.5 - (double)fabs((double)1.0*neighb_loc_type - (double)"+BOUNDARY_CELL+")))* \n"+
        "              //   ((double)1.0*loc_types[cell_index] - (double)"+CRACK_CELL+")*\n"+
        "                 ((double)1.0*loc_types[cell_index] - (double)"+INNER_BOUNDARY_CELL+")*\n"+
        "                 ((double)1.0*loc_types[cell_index] - (double)"+INNER_BOUNDARY_INTERGRANULAR_CELL+")))* \n"+
        "         signbit((double)fabs(loc_types[cell_index] - (double)"+BOUNDARY_CELL+") - (double)0.5)* \n"+
        "         signbit(threshold_0 - abs_diff_mom_sqr_"+neighb_counter+"/(abs_mom[cell_index]  + neighb_abs_mom_"+neighb_counter+"));\n"+
        "     //  signbit((double)1.0E6 - abs_diff_mom_sqr_"+neighb_counter+"); // (abs_diff_mom_sqr/((double)1.0E-10 + abs_mom[cell_index]))* \n"+
        "            //   ((double)0.5 - 0.5*mom_cosinus_"+neighb_counter+")*((double)0.5 - 0.5*mom_cosinus_"+neighb_counter+")*\n"+
        "            //   ((double)0.5 - 0.5*mom_cosinus_"+neighb_counter+")*((double)0.5 - 0.5*mom_cosinus_"+neighb_counter+"));\n"+
    */  "    \n";
     /*   
        prog_string_8 += 
    //  "  int saved_crack_sum   = 100*signbit(fabs((double)1.0*loc_types[cell_index] - (double)"+CRACK_CELL+") - (double)0.5);\n"+
        "  int saved_crack_sum   = 100; // crack_sum[cell_index];\n"+
        "  crack_sum[cell_index] = saved_crack_sum*signbit(fabs((double)1.0*loc_types[cell_index] - (double)"+CRACK_CELL+") - (double)0.5) + \n"+
        "                                        1*signbit((double)0.5 - fabs((double)1.0*loc_types[cell_index] - (double)"+CRACK_CELL+"))*(\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number - 1; neighb_counter++)
          prog_string_8 += "crack_precursor_"+neighb_counter+" + \n";
                
        prog_string_8   += "crack_precursor_"+(neighb1S_number - 1)+"); \n";
     */ 
        // Criterion of crack generation is based on the components of the force moment. 
        if(crack_criterion_mom)
        {
          prog_string_8 += 
        "    \n"+
        "    crack_value[cell_index] = \n"+
    //  "          (double)1.0*signbit(fabs((double)1.0*loc_types[cell_index] - (double)"+BOUNDARY_CELL+") - (double)0.5)*\n"+
        "          (double)1.0*signbit((double)-1.0E-12 - diss_en_change[cell_index])*((double)1.0/(double)"+neighb1S_number+")*\n"+
        "       signbit(fabs(((double)1.0*en_types[cell_index] - (double)"+INNER_CELL+")*((double)1.0*en_types[cell_index] - (double)"+LAYER_CELL+")) - (double)0.5)*\n"+
     // "       signbit(fabs((double)1.0*en_types[cell_index] - (double)"+INNER_CELL+") - (double)0.5)*\n"+
        "       (  // (double)sqr\n";
        
          for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
            prog_string_8 +=
      
    //  "                     ((double)-1.0 + (double)2.0*abs_diff_mom_sqr_"+neighb_counter+"/ \n"+
    //  "            (abs_mom[cell_index] + (mom_X[neighb1S_"+neighb_counter+"]*mom_X[neighb1S_"+neighb_counter+"] +\n"+
    //  "                                    mom_Y[neighb1S_"+neighb_counter+"]*mom_Y[neighb1S_"+neighb_counter+"] +\n"+
    //  "                                    mom_Z[neighb1S_"+neighb_counter+"]*mom_Z[neighb1S_"+neighb_counter+"]) + (double)1.0)) + \n";
      
        "            (abs_diff_mom_sqr_"+neighb_counter+"/ \n"+
        "              (abs_mom[cell_index] + (mom_X[neighb1S_"+neighb_counter+"]*mom_X[neighb1S_"+neighb_counter+"] +\n"+
        "                                      mom_Y[neighb1S_"+neighb_counter+"]*mom_Y[neighb1S_"+neighb_counter+"] +\n"+
        "                                      mom_Z[neighb1S_"+neighb_counter+"]*mom_Z[neighb1S_"+neighb_counter+"]) + (double)1.0E-12)) + \n";
      
        
          prog_string_8 += 
        "                           (double)0.0); // *(double)log((double)1.0 + fabs(mech_energies[cell_index])); // /((double)1.0 + fabs(mech_energies[cell_index])); // *diss_en_change[cell_index]/((double)1.0 + fabs(init_mech_en[cell_index])); //  \n"+
        "    \n";
        }
        
        // Criterion of crack generation is based on the value of the mechanical energy. 
        if(crack_criterion_mech)
        {
          prog_string_8 += 
        "    crack_value[cell_index] = "+
     //   "          (double)1.0*signbit(fabs((double)1.0*loc_types[cell_index] - (double)"+BOUNDARY_CELL+") - (double)0.5)*\n"+
        "       signbit(fabs(((double)1.0*en_types[cell_index] - (double)"+INNER_CELL+")*((double)1.0*en_types[cell_index] - (double)"+LAYER_CELL+")) - (double)0.5)*\n"+
        "       mech_energies[cell_index];\n"+
        "    \n";
        }
        
        prog_string_8 += 
        "    def_density_change[cell_index] = signbit(fabs(((double)1.0*en_types[cell_index] - (double)"+INNER_CELL+")*((double)1.0*en_types[cell_index] - (double)"+LAYER_CELL+")) - (double)0.5)* \n"+
        "                    (double)"+(32.0*coeff_defects/(Math.PI*Math.PI))+"*(double)sqrt(abs_mom[cell_index])/(mod_shear[grain_indices[cell_index] - 1]*lat_param[grain_indices[cell_index] - 1]*lat_param[grain_indices[cell_index] - 1]);\n"+
        "    \n"+
        "    // Calculation of components of angle velocity for current cell \n"+
        "    angle_vel_X[cell_index] = mom_X[cell_index]*(double)"+32.0/(Math.PI*TIME_STEP)+"/mod_shear[grain_indices[cell_index] - 1];\n"+
        "    angle_vel_Y[cell_index] = mom_Y[cell_index]*(double)"+32.0/(Math.PI*TIME_STEP)+"/mod_shear[grain_indices[cell_index] - 1];\n"+
        "    angle_vel_Z[cell_index] = mom_Z[cell_index]*(double)"+32.0/(Math.PI*TIME_STEP)+"/mod_shear[grain_indices[cell_index] - 1];\n"+
        "    \n"+
        "    // Calculation of absolute angle velocity for current cell \n"+
        "    abs_angle_vel[cell_index] = sqrt(angle_vel_X[cell_index]*angle_vel_X[cell_index] + angle_vel_Y[cell_index]*angle_vel_Y[cell_index] + angle_vel_Z[cell_index]*angle_vel_Z[cell_index]);\n"+
        "    \n"+
        "    // Current values of components of torsion angle for current cell \n"+
        "    double current_tors_angle_X = tors_angle_X[cell_index];\n"+
        "    double current_tors_angle_Y = tors_angle_Y[cell_index];\n"+
        "    double current_tors_angle_Z = tors_angle_Z[cell_index];\n"+
        "    \n"+
        "    // Calculation of components of torsion angle for current cell \n"+
        "    tors_angle_X[cell_index] = current_tors_angle_X + angle_vel_X[cell_index]*(double)"+TIME_STEP+";\n"+
        "    tors_angle_Y[cell_index] = current_tors_angle_Y + angle_vel_Y[cell_index]*(double)"+TIME_STEP+";\n"+
        "    tors_angle_Z[cell_index] = current_tors_angle_Z + angle_vel_Z[cell_index]*(double)"+TIME_STEP+";\n"+
        "    \n"+
        "    // Calculation of absolute torsion angle for current cell \n"+
        "    abs_tors_angle[cell_index] = sqrt(tors_angle_X[cell_index]*tors_angle_X[cell_index] + tors_angle_Y[cell_index]*tors_angle_Y[cell_index] + tors_angle_Z[cell_index]*tors_angle_Z[cell_index]);\n"+
        "    \n"+
  //    "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
 
        
        //===========================================================================
        // Вычисление значений диссипационной энергии и её текущих притоков на основе значений моментов сил каждого элемента
        String prog_string_8_1 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_8_1( \n"+
        "         __global       double *init_mech_energy, __global const double *mech_energy,       __global const double *init_diss_energy, \n"+
        "         __global const double *diss_en_change,   __global       double *final_diss_energy, __global const double *crack_value, \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8_1 += 
        "         __global const int   *neighbours1S_"+neighb_counter+",\n";
        
        prog_string_8_1 += 
        "         __global const char  *loc_types,        __global const char  *en_types, \n"+
        "         __global       char  *new_loc_types,    __global       char  *new_en_types, \n"+
        "         __global const double *rand,             __global const char  *crack_formation, \n"+
        "         __global       double *init_def_density, __global       double *final_def_density, __global const double *def_density_change,\n"+
        "         __global       double *rel_diss_energy,  __global       double *rel_diss_en_influx)\n"+
        "{\n"+
        "    // Index of current central cell\n" +
        "    int cell_index  = get_global_id(0);\n" +
        "    \n"+
        "    init_mech_energy[cell_index] = mech_energy[cell_index];\n"+
        "    \n"+
        "    // Calculation of final energy of dissipation \n"+
        "    final_diss_energy[cell_index] = init_diss_energy[cell_index] + diss_en_change[cell_index];\n"+
        "    \n"+
        "    // Calculation of relative dissipation energy in comparison with total energy \n"+
        "    rel_diss_energy[cell_index] = final_diss_energy[cell_index]/(fabs(mech_energy[cell_index]) + final_diss_energy[cell_index] + signbit((double)-1.0E-30 + fabs(mech_energy[cell_index]) + final_diss_energy[cell_index]));\n"+
        "    \n"+
        "    // Calculation of relative change of dissipation energy in comparison with total energy \n"+
        "    rel_diss_en_influx[cell_index] = diss_en_change[cell_index]/(fabs(mech_energy[cell_index]) + final_diss_energy[cell_index] + signbit((double)-1.0E-30 + fabs(mech_energy[cell_index]) + final_diss_energy[cell_index]));\n"+
        "    \n"+
        "    // Calculation of final density of defects \n"+
        "    final_def_density[cell_index] = init_def_density[cell_index] + def_density_change[cell_index];\n"+
        "    \n"+
        "    init_def_density[cell_index]  = final_def_density[cell_index];\n"+
        "    \n";
        
        // Crack is generated if the value calculated on the base of the force moment is larger than the threshold value.
        if(crack_criterion_mom)
        {
          prog_string_8_1 += 
        "    // Crack generation \n"+
        "    double crack_threshold = (double)10.0; // (double)0.99;// (double)0.5 + (double)10.0*rand[cell_index];// + (double)1.0E30*signbit(mech_energy[cell_index] - (double)1.0E9); // \n"+
        "    \n"+
        "    double transform_thr   = (double)0.95;\n"+
        "    \n";
          
   /* + "    int total_crack_sum = // signbit((double)1.0E7 - mech_energy[cell_index])*\n"+
        "                          // signbit((double)1.0E-5 - final_diss_energy[cell_index])*\n"+
        "       // signbit(final_diss_energy[cell_index]/(mech_energy[cell_index] + final_diss_energy[cell_index]) - (double)1.0E-12)*\n"+
        "        (crack_sum[cell_index] + // 6*signbit(fabs((double)1.0*loc_types[cell_index] - (double)"+BOUNDARY_CELL+") - (double)0.5) + \n"+
        "                          0*(\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8_1 += 
        "         signbit(fabs((double)1.0*loc_types[neighbours1S_"+neighb_counter+"[cell_index]] - (double)"+CRACK_CELL+") - (double)0.5) + \n";
      */  
          
          prog_string_8_1 += 
       // "         0));\n"+
      //  "    \n"+
      //  "    int neighb_crack_number = // signbit((double)1.0E7 - mech_energy[cell_index])*\n"+
      //  "                              signbit((double)1.0E-5 - final_diss_energy[cell_index])*\n"+
      //  "        signbit(final_diss_energy[cell_index]/(mech_energy[cell_index] + final_diss_energy[cell_index]) - (double)1.0E-12)*\n"+
      //  "                              (total_crack_sum - crack_sum[cell_index]);\n"+
      //  "    int crack_presence      = (int)1*signbit((double)0.5 - (double)1.0*neighb_crack_number);\n"+
        "    int crack_presence        = signbit((double)0.5 - crack_formation[0])*signbit(crack_threshold - crack_value[cell_index]);\n"+
        "    \n"+
        "    int new_struct_presence   = signbit((double)0.5 - crack_formation[0])*signbit(transform_thr - crack_value[cell_index]);\n"+
        "    \n";
        }
        
        // Crack is generated if the mechanical energy is larger than the threshold value.
        if(crack_criterion_mech)
        {
            prog_string_8_1 += 
        "    // Crack generation \n"+
        "    double crack_threshold = "+mech_en_threshold+";\n"+
        "    \n";
        
            prog_string_8_1 += 
        "    int crack_presence        = signbit((double)0.5 - crack_formation[0])*signbit(crack_threshold - crack_value[cell_index]);\n"+
        "    \n";
        }
        
        prog_string_8_1 += 
        "    \n"+
        "    // Change of mechanical energy due to structure transformation \n"+
        "    // init_mech_energy[cell_index] = signbit(new_struct_presence - (double)0.5)*mech_energy[cell_index] + \n"+
        "    //                                signbit((double)0.5 - new_struct_presence)*(double)0.5*mech_energy[cell_index];\n"+
     //   "    \n"+
      //  "    double random            = (double)0.0E1*rand[cell_index]*neighb_crack_number;// crack_presence;//\n"+
      //  "    double max_crack_number  = (double)0.5 + random; // 2.5 + random;\n"+
      //  "    new_loc_types[cell_index]  = loc_types[cell_index];\n"+
    //    "    new_loc_types[cell_index] = signbit((double)total_crack_sum - max_crack_number)*loc_types[cell_index] + \n"+
    //    "                                signbit(max_crack_number - (double)total_crack_sum)*"+CRACK_CELL+";\n"+
        
        // Change of location type if cell is cracked
        "    new_loc_types[cell_index] = signbit(crack_presence - (double)0.5)*loc_types[cell_index] + \n"+
        "                                signbit((double)0.5 - crack_presence)*"+CRACK_CELL+";\n"+
        "    \n"+     
        // Change of location type if cell is transformed
        "    new_loc_types[cell_index] = signbit(new_struct_presence - (double)0.5)*loc_types[cell_index] + \n"+
        "                                signbit((double)0.5 - new_struct_presence)*"+TRANSFORMED_CELL+";\n"+
    //  "    new_loc_types[cell_index] = signbit((double)total_crack_sum - (double)1.5)*loc_types[cell_index] + \n"+
    //  "                                signbit((double)1.5 - (double)total_crack_sum)*"+CRACK_CELL+";\n"+
        "    \n"+
    //  "    new_en_types[cell_index]  = en_types[cell_index];\n"+
    //    "    new_en_types[cell_index]  = signbit((double)total_crack_sum - max_crack_number)*en_types[cell_index] + \n"+
    //    "                                signbit(max_crack_number - (double)total_crack_sum)*"+ADIABATIC_ADIABATIC_CELL+";\n"+
        "    \n"+
        "    new_en_types[cell_index]  = signbit(crack_presence - (double)0.5)*en_types[cell_index] + \n"+
        "                                signbit((double)0.5 - crack_presence)*"+ADIABATIC_ADIABATIC_CELL+";\n"+
        "    \n"+
     //   "    crack_sum[cell_index] = 100*signbit(fabs((double)1.0*new_loc_types[cell_index] - (double)"+CRACK_CELL+") - (double)0.5) + \n"+
     //   "                total_crack_sum*signbit((double)0.5 - fabs((double)1.0*new_loc_types[cell_index] - (double)"+CRACK_CELL+"));\n"+
     //   "    \n"+
//        "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        // Distribution of energy of each cracked cell among its neighbours at 1st coordination sphere
        String prog_string_8_2 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_8_2( \n"+
        "         __global const double *init_mech_energy, __global const char  *loc_types, __global const char  *en_types, \n";
        
        // Addition of variables responsible for arrays of neighbour indices
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8_2 += 
        "         __global const int   *neighbours1S_"+neighb_counter+",\n";
        
        prog_string_8_2 += 
        "         __global       double *mech_energy)\n"+
        "{\n"+
        "    int cell_index = get_global_id(0);\n"+
        "    \n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8_2 += 
        "    int neighb1S_"+neighb_counter+" = neighbours1S_"+neighb_counter+"[cell_index];\n";
        
        // Change of elastic energy if cell is transformed
        prog_string_8_2 += 
        "    \n"+
        "    mech_energy[cell_index] = signbit((double)0.5 - fabs((double)1.0*loc_types[cell_index] - "+CRACK_CELL+"))* \n"+
        "   //        (signbit((double)0.5 - fabs((double)1.0*loc_types[cell_index] - "+TRANSFORMED_CELL+"))*\n"+
        "   //           (init_mech_energy[cell_index] +   \n"+
        "   //           (signbit(fabs((double)1.0*en_types[cell_index] - "+INNER_CELL+") - (double)0.5) + signbit(fabs((double)1.0*en_types[cell_index] - "+LAYER_CELL+") - (double)0.5))*(\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8_2 += 
        "    //           signbit(fabs((double)1.0*loc_types[neighb1S_"+neighb_counter+"] - "+TRANSFORMED_CELL+") - (double)0.5)*init_mech_energy[neighb1S_"+neighb_counter+"]*0.5 + \n";
         
        prog_string_8_2 += 
        "   //            (double)0.0)/(double)"+neighb1S_number+") + \n"+
        "   //         signbit((double)-0.5 + fabs((double)1.0*loc_types[cell_index] - "+TRANSFORMED_CELL+"))*init_mech_energy[cell_index]*0.5);\n";
        
        // Change of elastic energy if cell is cracked
        prog_string_8_2 += 
        "    \n"+
        "  //  mech_energy[cell_index] = signbit((double)0.5 - fabs((double)1.0*loc_types[cell_index] - "+CRACK_CELL+"))* \n"+
        "             (init_mech_energy[cell_index] + \n"+
        "               \n"+
        "              (signbit(fabs((double)1.0*en_types[cell_index] - "+INNER_CELL+") - (double)0.5) + signbit(fabs((double)1.0*en_types[cell_index] - "+LAYER_CELL+") - (double)0.5))*(\n";
        
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          prog_string_8_2 += 
        "               signbit(fabs((double)1.0*loc_types[neighb1S_"+neighb_counter+"] - "+CRACK_CELL+") - (double)0.5)*init_mech_energy[neighb1S_"+neighb_counter+"] + \n";
         
        prog_string_8_2 += 
        "               (double)0.0)/(double)"+neighb1S_number+");\n"+
        "    \n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}\n";
        
        //===========================================================================
        // Определение новых значений механической энергии, энергии диссипации и энергетических типов элементов
        String prog_string_9 = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_9(__global const double *final_elast_energy, __global double *init_elast_energy, \n"+
        "                       __global const double *final_diss_energy,  __global double *init_diss_energy,  \n"+
        "                       __global const char   *new_en_types,       __global char   *en_types,\n"+
        "                       __global const char   *new_loc_types,      __global char   *loc_types)\n"+
        "{\n"+
        "    int cell_index = get_global_id(0);\n"+
        "    \n"+
        "    init_elast_energy[cell_index] = final_elast_energy[cell_index];\n"+
        "    \n"+
        "    init_diss_energy[cell_index]  = final_diss_energy[cell_index];\n"+
        "    \n"+
        "    en_types[cell_index]          = new_en_types[cell_index];\n"+
        "    \n"+
        "    loc_types[cell_index]         = new_loc_types[cell_index];\n"+
        "    \n"+
   //   "    __syncthreads();\n"+
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        //===========================================================================
        // Variable responsible for recrystallization under high temperatures
        boolean high_temperature_criterion = false; // true;  // 
        
        // Variable responsible for recrystallization under low temperatures (near melting point)
        boolean low_temperature_criterion  = true;  // false; // 
        
        // Variable responsible for recrystallization caused by high density of defects
        boolean defect_criterion           = false; // true;  // 
        
        // Calculation of probabilities of generation of new grains and twinning
        String prog_string_A = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_A(__global const int    *init_grain_indices, __global const char   *en_types,\n" +
        "                       __global const char   *grain_rec_types,    __global const double *temperatures,\n" +
        "                       __global const double *low_tempr_recryst,  __global const double *high_tempr_recryst,\n" +
        "                       __global const double *low_tempr_twinning, __global const double *high_tempr_twinning,\n" +
        "                       __global const double *max_prob_recryst,   __global const double *max_prob_twinning,\n" +
        "                       __global const int    *neighbours1S_0,     __global const int    *neighbours1S_1,\n" +
        "                       __global const int    *neighbours1S_2,     __global const int    *neighbours1S_3,\n" +
        "                       __global const int    *neighbours1S_4,     __global const int    *neighbours1S_5,\n" +
        "                       __global const int    *neighbours1S_6,     __global const int    *neighbours1S_7,\n" +
        "                       __global const int    *neighbours1S_8,     __global const int    *neighbours1S_9,\n" +
        "                       __global const int    *neighbours1S_10,    __global const int    *neighbours1S_11,\n" +
        "                       __global const double *random,             __global       double *prob_new_grain,\n" +
        "                       __global       double *prob_twinning,      __global       char   *new_grain_embryos,\n" +
        "                       __global       char   *twin_grain_embryos, __global       int    *root_grain_indices,\n" +
        "                       __global const double *final_def_density)\n" +
        "{   \n" +
        "    int cell_index         = get_global_id(0);\n" +
        "    int old_grain_index    = init_grain_indices[cell_index] - 1;\n" +
        "    char old_grain_rec_type = grain_rec_types[old_grain_index];\n" +
        "    \n" +    
        "    double low_tempr  = low_tempr_recryst[old_grain_index]; \n" +
        "    double high_tempr = high_tempr_recryst[old_grain_index];\n" +
        "    \n" +
        "    double aver_tempr_recryst = high_tempr_recryst[old_grain_index] + (double)1.0; // 0.5*(low_tempr + high_tempr); //  \n" +
        "    double aver_tempr_twinning = high_tempr_twinning[old_grain_index] + (double)1.0; // 0.5*(low_tempr_twinning[old_grain_index] + high_tempr_twinning[old_grain_index]);\n" +
        "    \n";
        
        if(high_temperature_criterion)
            prog_string_A +=
        "    // Probability of generation of new grain in current cell\n" +
        "    prob_new_grain[cell_index] = max_prob_recryst[old_grain_index]*signbit(abs(en_types[cell_index] - "+INNER_CELL+") - 0.5)* // signbit(fabs(old_grain_rec_type - "+INITIAL_GRAIN+") - 0.5)*\n" +
        "                signbit(low_tempr - temperatures[cell_index])*signbit(temperatures[cell_index] - high_tempr)*\n" +
        "               (signbit(temperatures[cell_index] - aver_tempr_recryst)*(temperatures[cell_index] - low_tempr)/(aver_tempr_recryst - low_tempr) + \n" +
        "                signbit(aver_tempr_recryst - temperatures[cell_index])*(high_tempr - temperatures[cell_index])/(high_tempr - aver_tempr_recryst))*\n" +
        "    signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_0 [cell_index]] - 1] - "+INITIAL_GRAIN+") + \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_1 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_2 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_3 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_4 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_5 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_6 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_7 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_8 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_9 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_10[cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_11[cell_index]] - 1] - "+INITIAL_GRAIN+") - 0.5)*signbit((double)new_grain_embryos[cell_index] - (double)0.5);\n" +
        "    \n" +
        "    // Probability of generation of twinning grain at new grain boundary in current cell\n" +
        "    prob_twinning[cell_index] = max_prob_twinning[old_grain_index]*signbit(abs(en_types[cell_index] - "+INNER_CELL+") - 0.5)*signbit(fabs(old_grain_rec_type - "+INITIAL_GRAIN+") - 0.5)*\n" +
        "                signbit(low_tempr_twinning[old_grain_index] - temperatures[cell_index])*signbit(temperatures[cell_index] - high_tempr_twinning[old_grain_index])*\n" +
        "               (signbit(temperatures[cell_index] - aver_tempr_twinning)*(0.4 + 0.6*(temperatures[cell_index] - low_tempr_twinning[old_grain_index])/(aver_tempr_twinning - low_tempr_twinning[old_grain_index])) + \n" +
        "                signbit(aver_tempr_twinning - temperatures[cell_index])*(high_tempr_twinning[old_grain_index] - temperatures[cell_index])/(high_tempr_twinning[old_grain_index] - aver_tempr_twinning))*\n" +
        "    signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_0 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_1 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_2 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_3 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_4 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_5 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_6 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_7 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_8 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_9 [cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_10[cell_index]] - 1] - "+NEW_GRAIN+")*\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_11[cell_index]] - 1] - "+NEW_GRAIN+") - (double)0.5)*signbit((double)twin_grain_embryos[cell_index] - (double)0.5);\n" +
        "    \n";
        
        if(defect_criterion)
            prog_string_A +=
        "    // Probability of generation of new grain in current cell\n" +
        "    prob_new_grain[cell_index] = max_prob_recryst[old_grain_index]*signbit(abs(en_types[cell_index] - "+INNER_CELL+") - (double)0.5)*\n" +
        "      //          signbit(low_tempr - temperatures[cell_index])*signbit(temperatures[cell_index] - high_tempr)*\n" +
        "                exp((double)-1.0E13/(final_def_density[cell_index] + (double)1.0));\n" +
        "    \n"+
        "    // Probability of generation of twinning grain at new grain boundary in current cell\n" +
        "    prob_twinning[cell_index] = max_prob_twinning[old_grain_index]*signbit(abs(en_types[cell_index] - "+INNER_CELL+") - (double)0.5)*\n" +
        "      //          signbit(low_tempr_twinning[old_grain_index] - temperatures[cell_index])*signbit(temperatures[cell_index] - high_tempr_twinning[old_grain_index])*\n" +
        "                exp((double)-1.0E13/(final_def_density[cell_index] + (double)1.0));\n" +
        "    \n";
        
        if(low_temperature_criterion)
            prog_string_A +=
        "    // Probability of generation of new grain in current cell\n" +
        "    prob_new_grain[cell_index] = max_prob_recryst[old_grain_index]*signbit(old_grain_index - 0.5)*signbit(abs(en_types[cell_index] - "+INNER_CELL+") - 0.5)*signbit(abs(old_grain_rec_type - "+INITIAL_GRAIN+") - 0.5)*\n" +
        "              (signbit(low_tempr - temperatures[cell_index])*signbit(temperatures[cell_index] - high_tempr)*(high_tempr - temperatures[cell_index])/(high_tempr - low_tempr) + \n" +
        "               signbit(temperatures[cell_index] - low_tempr))* // signbit(old_grain_index - 0.5)*\n" +
        "    signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_0 [cell_index]] - 1] - "+INITIAL_GRAIN+") + \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_1 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_2 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_3 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_4 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_5 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_6 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_7 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_8 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_9 [cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_10[cell_index]] - 1] - "+INITIAL_GRAIN+") +\n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_11[cell_index]] - 1] - "+INITIAL_GRAIN+") - 0.5)*signbit((double)new_grain_embryos[cell_index] - (double)0.5);\n" +
        "    \n"+
        "    prob_twinning[cell_index] = (double)0.0;\n"+
        "    \n";
        
        prog_string_A +=
        "    // Presence of new grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here\n" +
        "    new_grain_embryos[cell_index]  = (char)signbit(random[cell_index] - prob_new_grain[cell_index]);\n" +
        "\n" +
        "    // Presence of twin grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here\n" +
        "    char twin_grain_embryo = (char)signbit(random[cell_index] - prob_twinning[cell_index]); // *(char)sighbit(new_grain_embryos[cell_index] - 0.5);\n" +
        "\n"+
        "    root_grain_indices[cell_index] = twin_grain_embryo*\n"+
        "           (signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_0 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_0 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_1 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_1 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_2 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_2 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_3 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_3 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_4 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_4 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_5 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_5 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_6 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_6 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_7 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_7 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_8 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_8 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_9 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_9 [cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_10[cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_10[cell_index]] + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_11[cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)*init_grain_indices[neighbours1S_11[cell_index]])/ \n"+
        "           (signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_0 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_1 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_2 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_3 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_4 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_5 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_6 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_7 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_8 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_9 [cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_10[cell_index]] - 1] - "+NEW_GRAIN+") - 0.5) + \n"+
        "            signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_11[cell_index]] - 1] - "+NEW_GRAIN+") - 0.5)); \n"+
        "    \n"+
        "    twin_grain_embryos[cell_index] = twin_grain_embryo; // (char)signbit(fabs(grain_rec_types[root_grain_indices[cell_index] - 1] - "+NEW_GRAIN+") - 0.5);\n"+
        "    \n"+
//      "    __syncthreads();\n" +
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n"+
        "}";
        
        //===========================================================================
        // Addition of cells at 1st coordination sphere of embryos to new grains
        String prog_string_B = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_B(__global const int   *init_grain_indices, __global const char  *energy_types,\n"+
        "                       __global const char  *new_grain_embryos,  __global const char  *twin_grain_embryos,\n"+
        "                       __global const char  *init_gr_rec_types,\n"+
        "                       __global const int   *neighbours1S_0,     __global const int   *neighbours1S_1,\n" +
        "                       __global const int   *neighbours1S_2,     __global const int   *neighbours1S_3,\n" +
        "                       __global const int   *neighbours1S_4,     __global const int   *neighbours1S_5,\n" +
        "                       __global const int   *neighbours1S_6,     __global const int   *neighbours1S_7,\n" +
        "                       __global const int   *neighbours1S_8,     __global const int   *neighbours1S_9,\n" +
        "                       __global const int   *neighbours1S_10,    __global const int   *neighbours1S_11,\n" +
        "                       __global       int   *grain_indices,      __global       char  *grain_rec_types,\n"+
        "                       __global       int   *root_grain_indices, __global       double *grain_angles,\n"+
        "                       __global       double *max_grain_angles,   __global       double *heat_max_mob,\n"+
        "                       __global       double *max_bound_energy,   __global       double *disl_max_mobility,\n"+
        "                       __global       double *mech_max_mobility,  __global       double *disl_energies,\n"+
        "                       __global       double *min_disl_energies,  __global const double *random,\n"+
        "                       __global       double *lat_vec_A_length,   __global       double *lat_vec_B_length, __global double *lat_vec_C_length,\n"+
        "                       __global       double *lat_vec_A_x,        __global       double *lat_vec_A_y,      __global double *lat_vec_A_z,\n"+
        "                       __global       double *lat_vec_B_x,        __global       double *lat_vec_B_y,      __global double *lat_vec_B_z,\n"+
        "                       __global       double *lat_vec_C_x,        __global       double *lat_vec_C_y,      __global double *lat_vec_C_z,\n"+
        "                       __global       double *lattice_anis_coeff, __global       double *lattice_parameter,\n"+
        "                       __global       double *defect_density,     __global       double *min_defect_density)\n"+
        "{   \n"+
        "    int cell_index          = get_global_id(0);\n" +
        "    int init_grain_index    = init_grain_indices[cell_index] - 1;\n" +
        "    \n"+
        "    char init_gr_rec_type   = init_gr_rec_types[init_grain_index];\n" +
        "    char energy_type        = energy_types[cell_index];\n" +
        "    \n"+
        "    //   The variable equals 1 if the cell is in the interior of specimen and it is an embryo of new grain or \n"+
        "    // it is contained in initial grain and there is the only embryo of new grain at 1st sphere, otherwise the variable equals 0.\n"+
        "    char new_gr_embryo = // (char)0;\n"+
        "                          signbit(init_grain_index - 0.5)*signbit(abs(energy_type - "+INNER_CELL+") - 0.5)* // signbit(0.5 - abs(init_gr_rec_type - "+NEW_GRAIN+"))* \n"+
        "                             (signbit(0.5 - new_grain_embryos[cell_index]) + \n"+
        "                              signbit(new_grain_embryos[cell_index] - 0.5)* \n"+
        "                                 signbit(0.5 - (signbit(0.5 - new_grain_embryos[neighbours1S_0 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_1 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_2 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_3 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_4 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_5 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_6 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_7 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_8 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_9 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_10[cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_11[cell_index]])))*\n"+
        "                                 signbit(-1.5 +(signbit(0.5 - new_grain_embryos[neighbours1S_0 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_1 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_2 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_3 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_4 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_5 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_6 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_7 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_8 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_9 [cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_10[cell_index]]) + \n"+
        "                                                signbit(0.5 - new_grain_embryos[neighbours1S_11[cell_index]]))));\n"+
        "    \n"+
        "    //   The variable equals 1 if the cell is in the interior of specimen and it is an embryo of twin grain or \n"+
        "    // it is contained in initial grain and there is the only embryo of twin grain at 1st sphere, otherwise the variable equals 0.\n"+
        "    char twin_gr_embryo = // (char)0;\n"+
        "                  signbit(init_grain_index - 0.5)*signbit(abs(energy_type - "+INNER_CELL+") - 0.5)*signbit(abs(init_gr_rec_type - "+INITIAL_GRAIN+") - 0.5)*signbit(-0.5 + new_gr_embryo)*\n"+
        "                             (signbit(0.5 - twin_grain_embryos[cell_index]) + \n"+
        "                              signbit(twin_grain_embryos[cell_index] - 0.5)*\n"+
        "                                 signbit(0.5 - (signbit(0.5 - twin_grain_embryos[neighbours1S_0 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_1 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_2 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_3 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_4 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_5 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_6 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_7 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_8 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_9 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_10[cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_11[cell_index]])))*\n"+
        "                                 signbit(-1.5 +(signbit(0.5 - twin_grain_embryos[neighbours1S_0 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_1 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_2 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_3 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_4 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_5 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_6 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_7 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_8 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_9 [cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_10[cell_index]]) + \n"+
        "                                                signbit(0.5 - twin_grain_embryos[neighbours1S_11[cell_index]]))));\n"+
        "    \n"+
        "    // New grain index for current cell that can join to new or twin grain \n"+
        "    grain_indices[cell_index] = // init_grain_indices[cell_index]; \n"+
        "                                signbit( 0.5 - signbit(0.5 - new_grain_embryos[cell_index]) - signbit(0.5 - twin_grain_embryos[cell_index]))*init_grain_indices[cell_index] + \n"+
        "                                signbit(-0.5 + signbit(0.5 - new_grain_embryos[cell_index]) + signbit(0.5 - twin_grain_embryos[cell_index]))*\n"+
     //   "                                signbit(init_grain_index - 0.5)*\n"+
        "                                  (signbit(new_gr_embryo - 0.5)*signbit(twin_gr_embryo - 0.5)*init_grain_indices[cell_index] + \n"+
        "                                   signbit(0.5 - new_gr_embryo)* // signbit(twin_gr_embryo - 0.5)*\n"+
        "                                     (signbit(0.5 - new_grain_embryos[neighbours1S_0 [cell_index]])*init_grain_indices[neighbours1S_0 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_1 [cell_index]])*init_grain_indices[neighbours1S_1 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_2 [cell_index]])*init_grain_indices[neighbours1S_2 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_3 [cell_index]])*init_grain_indices[neighbours1S_3 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_4 [cell_index]])*init_grain_indices[neighbours1S_4 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_5 [cell_index]])*init_grain_indices[neighbours1S_5 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_6 [cell_index]])*init_grain_indices[neighbours1S_6 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_7 [cell_index]])*init_grain_indices[neighbours1S_7 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_8 [cell_index]])*init_grain_indices[neighbours1S_8 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_9 [cell_index]])*init_grain_indices[neighbours1S_9 [cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_10[cell_index]])*init_grain_indices[neighbours1S_10[cell_index]] + \n"+
        "                                      signbit(0.5 - new_grain_embryos[neighbours1S_11[cell_index]])*init_grain_indices[neighbours1S_11[cell_index]])+ \n"+
        "    \n"+
        "                                   signbit(0.5 - twin_gr_embryo)*signbit(new_gr_embryo - 0.5)*\n"+
        "                                     (signbit(0.5 - twin_grain_embryos[neighbours1S_0 [cell_index]])*init_grain_indices[neighbours1S_0 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_1 [cell_index]])*init_grain_indices[neighbours1S_1 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_2 [cell_index]])*init_grain_indices[neighbours1S_2 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_3 [cell_index]])*init_grain_indices[neighbours1S_3 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_4 [cell_index]])*init_grain_indices[neighbours1S_4 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_5 [cell_index]])*init_grain_indices[neighbours1S_5 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_6 [cell_index]])*init_grain_indices[neighbours1S_6 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_7 [cell_index]])*init_grain_indices[neighbours1S_7 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_8 [cell_index]])*init_grain_indices[neighbours1S_8 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_9 [cell_index]])*init_grain_indices[neighbours1S_9 [cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_10[cell_index]])*init_grain_indices[neighbours1S_10[cell_index]] + \n"+
        "                                      signbit(0.5 - twin_grain_embryos[neighbours1S_11[cell_index]])*init_grain_indices[neighbours1S_11[cell_index]])); \n"+
        "    \n"+
        "    // New type of grain according to its role in recrystallization process \n"+
      //  "    grain_rec_types[grain_indices[cell_index] - 1]    = ((char)signbit(0.5 - fabs(twin_gr_embryo + new_gr_embryo - 1)))*init_gr_rec_type + \n"+
      //  "                                                        new_gr_embryo* (char)signbit((double)twin_gr_embryo - 0.5)*(char)"+NEW_GRAIN+" + \n"+
      //  "                                                        twin_gr_embryo*(char)signbit((double)new_gr_embryo  - 0.5)*(char)"+TWIN_GRAIN+";\n"+
        "    grain_rec_types[grain_indices[cell_index] - 1]    = // "+INITIAL_GRAIN+";\n"+
        "                   signbit(grain_indices[cell_index] - "+specArrays.init_grain_number+" - 0.5)*"+INITIAL_GRAIN+" + \n"+
        "                                                        signbit(0.5 - grain_indices[cell_index] + "+specArrays.init_grain_number+")*\n"+
        "                  (signbit(new_gr_embryo - 0.5)*signbit(twin_gr_embryo - 0.5)*init_gr_rec_type + \n"+
        "                   signbit(0.5 - new_gr_embryo)*"+NEW_GRAIN+" + \n"+
        "                   signbit(0.5 - twin_gr_embryo)*signbit(new_gr_embryo - 0.5)*"+TWIN_GRAIN+");\n"+
        "    \n"+
//      "    __syncthreads();\n"+
        "    // Parameters of initial grain \n"+
        "    double init_grain_angle        = grain_angles[init_grain_index];\n"+
        "    double init_max_grain_angle    = max_grain_angles[init_grain_index];\n"+
        "    double init_heat_max_mob       = heat_max_mob[init_grain_index];\n"+
        "    double init_max_bound_energy   = max_bound_energy[init_grain_index];\n"+
        "    double init_disl_max_mobility  = disl_max_mobility[init_grain_index];\n"+
        "    double init_mech_max_mobility  = mech_max_mobility[init_grain_index];\n"+
        "    double init_min_disl_energy    = min_disl_energies[init_grain_index];\n"+
        "    double init_disl_energy        = disl_energies[init_grain_index];\n"+
        "    double init_lattice_param      = lattice_parameter[init_grain_index];\n"+
        "    double init_lattice_anis_coeff = lattice_anis_coeff[init_grain_index];\n"+
        "    double init_defect_density     = defect_density[init_grain_index];\n"+
        "    double init_min_defect_density = min_defect_density[init_grain_index];\n"+
        "    \n"+
    //  "    // Setting of properties of new or twin grain \n"+
    //  "    grain_angles      [grain_indices[cell_index] - 1] = (char)signbit( 0.5 - new_grain_embryos[cell_index])*random[cell_index]*max_grain_angles[init_grain_index]/(double)90.0 + \n"+
    //  "                                                        (char)signbit(-0.5 + new_grain_embryos[cell_index])*init_grain_angle;\n"+
    //  "    \n"+
        "    // Determination of parameters of new grain \n"+
        "    max_grain_angles  [grain_indices[cell_index] - 1] = init_max_grain_angle;\n"+
        "    heat_max_mob      [grain_indices[cell_index] - 1] = init_heat_max_mob;\n"+
        "    max_bound_energy  [grain_indices[cell_index] - 1] = init_max_bound_energy;\n"+
        "    disl_max_mobility [grain_indices[cell_index] - 1] = init_disl_max_mobility;\n"+
        "    mech_max_mobility [grain_indices[cell_index] - 1] = init_mech_max_mobility;\n"+
        "    min_disl_energies [grain_indices[cell_index] - 1] = init_min_disl_energy;\n"+
        "    lattice_parameter [grain_indices[cell_index] - 1] = init_lattice_param;\n"+
        "    lattice_anis_coeff[grain_indices[cell_index] - 1] = init_lattice_anis_coeff;\n"+
        "    min_defect_density[grain_indices[cell_index] - 1] = init_min_defect_density;\n"+
        "    \n"+  
        "    disl_energies     [grain_indices[cell_index] - 1] = // init_disl_energy; \n"+
        "       ((char)signbit((double) 1.0*abs(grain_rec_types[grain_indices[cell_index] - 1] - "+INITIAL_GRAIN+") - 0.5))*init_disl_energy + \n"+
        "       ((char)signbit((double)-1.0*abs(grain_rec_types[grain_indices[cell_index] - 1] - "+INITIAL_GRAIN+") + 0.5))*init_min_disl_energy; \n"+
        "    \n"+
                
                
      //  "    defect_density    [grain_indices[cell_index] - 1] = init_defect_density; \n"+
      //  "       ((char)signbit((double) 1.0*abs(grain_rec_types[grain_indices[cell_index] - 1] - "+INITIAL_GRAIN+") - 0.5))*init_defect_density + \n"+
      //  "       ((char)signbit((double)-1.0*abs(grain_rec_types[grain_indices[cell_index] - 1] - "+INITIAL_GRAIN+") + 0.5))*init_min_defect_density; \n"+
                
                
        "    \n"+
        "    // Index of grain, from which current twin grain grows: if the grain is not twin then the index equals 0. \n"+
        "    int  root_gr_index      = root_grain_indices[grain_indices[cell_index] - 1] - signbit((double)0.5 - root_grain_indices[grain_indices[cell_index] - 1]);\n" +
        "    \n"+
//      "    __syncthreads();\n" +
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n"+
        "    \n"+
    /*
        "    // Determination of lengths of lattice vectors \n"+
        "    double vec_A_length = "+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+INITIAL_GRAIN+")))*lat_vec_A_length[init_grain_index] + \n"+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+NEW_GRAIN+")))*\n"+
        "           ((char)signbit(fabs(init_gr_rec_type - "+INITIAL_GRAIN+") - 0.5)*\n"+
        "                               (signbit((double)(       grain_indices[cell_index]%3) - 0.5)*lat_vec_A_length[init_grain_index] + \n"+
        "                                signbit((double)fabs(1 - grain_indices[cell_index]%3) - 0.5)*lat_vec_B_length[init_grain_index] + \n"+
        "                                signbit((double)fabs(2 - grain_indices[cell_index]%3) - 0.5)*lat_vec_C_length[init_grain_index]) + \n"+
        "            (char)signbit(fabs(init_gr_rec_type - "+NEW_GRAIN+") - 0.5)*lat_vec_A_length[init_grain_index]) + \n"+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+TWIN_GRAIN+")))*\n"+
        "           ((char)signbit(fabs((init_gr_rec_type - "+INITIAL_GRAIN+")*(init_gr_rec_type - "+NEW_GRAIN+")) - 0.5)*\n"+
        "                               (signbit((double)-0.5 + grain_indices[cell_index]%2)*lat_vec_B_length[root_gr_index] + \n"+
        "                                signbit((double) 0.5 - grain_indices[cell_index]%2)*lat_vec_C_length[root_gr_index]) + \n"+
        "            (char)signbit(fabs(init_gr_rec_type - "+TWIN_GRAIN+") - 0.5)*lat_vec_A_length[init_grain_index]); \n"+
        "    \n"+
        "    double vec_B_length = "+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+INITIAL_GRAIN+")))*lat_vec_B_length[init_grain_index] + \n"+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+NEW_GRAIN+")))*\n"+
        "           ((char)signbit(fabs(init_gr_rec_type - "+INITIAL_GRAIN+") - 0.5)*\n"+
        "                               (signbit((double)(       grain_indices[cell_index]%3) - 0.5)*lat_vec_B_length[init_grain_index] + \n"+
        "                                signbit((double)fabs(1 - grain_indices[cell_index]%3) - 0.5)*lat_vec_C_length[init_grain_index] + \n"+
        "                                signbit((double)fabs(2 - grain_indices[cell_index]%3) - 0.5)*lat_vec_A_length[init_grain_index]) + \n"+
        "            (char)signbit(fabs(init_gr_rec_type - "+NEW_GRAIN+") - 0.5)*lat_vec_B_length[init_grain_index]) + \n"+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+TWIN_GRAIN+")))*\n"+
        "           ((char)signbit(fabs((init_gr_rec_type - "+INITIAL_GRAIN+")*(init_gr_rec_type - "+NEW_GRAIN+")) - 0.5)*\n"+
        "                               (signbit((double)-0.5 + grain_indices[cell_index]%2)*lat_vec_C_length[root_gr_index] + \n"+
        "                                signbit((double) 0.5 - grain_indices[cell_index]%2)*lat_vec_A_length[root_gr_index]) + \n"+
        "            (char)signbit(fabs(init_gr_rec_type - "+TWIN_GRAIN+") - 0.5)*lat_vec_B_length[init_grain_index]); \n"+
        "    \n"+
        "    double vec_C_length = "+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+INITIAL_GRAIN+")))*lat_vec_C_length[init_grain_index] + \n"+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+NEW_GRAIN+")))*\n"+
        "           ((char)signbit(fabs(init_gr_rec_type - "+INITIAL_GRAIN+") - 0.5)*\n"+
        "                               (signbit((double)(       grain_indices[cell_index]%3) - 0.5)*lat_vec_C_length[init_grain_index] + \n"+
        "                                signbit((double)fabs(1 - grain_indices[cell_index]%3) - 0.5)*lat_vec_A_length[init_grain_index] + \n"+
        "                                signbit((double)fabs(2 - grain_indices[cell_index]%3) - 0.5)*lat_vec_B_length[init_grain_index]) + \n"+
        "            (char)signbit(fabs(init_gr_rec_type - "+NEW_GRAIN+") - 0.5)*lat_vec_C_length[init_grain_index]) + \n"+
        "                         ((char)signbit((double)-0.5 + abs(grain_rec_types[grain_indices[cell_index] - 1] - "+TWIN_GRAIN+")))*\n"+
        "           ((char)signbit(fabs((init_gr_rec_type - "+INITIAL_GRAIN+")*(init_gr_rec_type - "+NEW_GRAIN+")) - 0.5)*\n"+
        "                               (signbit((double)-0.5 + grain_indices[cell_index]%2)*lat_vec_A_length[root_gr_index] + \n"+
        "                                signbit((double) 0.5 - grain_indices[cell_index]%2)*lat_vec_B_length[root_gr_index]) + \n"+
        "            (char)signbit(fabs(init_gr_rec_type - "+TWIN_GRAIN+") - 0.5)*lat_vec_C_length[init_grain_index]); \n"+
        "    \n"+
        "    double init_lat_vec_A_length = lat_vec_A_length[init_grain_index]; \n"+
        "    double init_lat_vec_B_length = lat_vec_B_length[init_grain_index]; \n"+
        "    double init_lat_vec_C_length = lat_vec_C_length[init_grain_index]; \n"+
        "    \n"+
        "    lat_vec_A_length[grain_indices[cell_index] - 1] = vec_A_length + signbit(vec_A_length - 1.0E-9)*init_lat_vec_A_length;\n"+
        "    lat_vec_B_length[grain_indices[cell_index] - 1] = vec_B_length + signbit(vec_B_length - 1.0E-9)*init_lat_vec_B_length;\n"+
        "    lat_vec_C_length[grain_indices[cell_index] - 1] = vec_C_length + signbit(vec_C_length - 1.0E-9)*init_lat_vec_C_length;\n"+
        "    \n"+   
        "    char unequal_A = signbit(1.0E-9 - fabs(lat_vec_A_length[grain_indices[cell_index] - 1] - lat_vec_B_length[grain_indices[cell_index] - 1])*fabs(lat_vec_A_length[grain_indices[cell_index] - 1] - lat_vec_C_length[grain_indices[cell_index] - 1]));\n"+
        "    char unequal_B = signbit(1.0E-9 - fabs(lat_vec_B_length[grain_indices[cell_index] - 1] - lat_vec_A_length[grain_indices[cell_index] - 1])*fabs(lat_vec_B_length[grain_indices[cell_index] - 1] - lat_vec_C_length[grain_indices[cell_index] - 1]));\n"+ 
        "    char unequal_C = signbit(1.0E-9 - fabs(lat_vec_C_length[grain_indices[cell_index] - 1] - lat_vec_A_length[grain_indices[cell_index] - 1])*fabs(lat_vec_C_length[grain_indices[cell_index] - 1] - lat_vec_B_length[grain_indices[cell_index] - 1]));\n"+ 
        "    \n"+
        "    vec_A_length = lat_vec_A_length[grain_indices[cell_index] - 1]*unequal_A + (1 - unequal_A)*init_lat_vec_A_length;;\n"+
        "    vec_B_length = lat_vec_B_length[grain_indices[cell_index] - 1]*unequal_B + (1 - unequal_B)*init_lat_vec_B_length;;\n"+
        "    vec_C_length = lat_vec_C_length[grain_indices[cell_index] - 1]*unequal_C + (1 - unequal_C)*init_lat_vec_C_length;;\n"+
        "    \n"+
        "    lat_vec_A_length[grain_indices[cell_index] - 1] = vec_A_length + signbit(vec_A_length - 1.0E-9)*init_lat_vec_A_length;\n"+
        "    lat_vec_B_length[grain_indices[cell_index] - 1] = vec_B_length + signbit(vec_B_length - 1.0E-9)*init_lat_vec_B_length;\n"+
        "    lat_vec_C_length[grain_indices[cell_index] - 1] = vec_C_length + signbit(vec_C_length - 1.0E-9)*init_lat_vec_C_length;\n"+
        "    \n"+
        "    double init_lat_vec_A_x = lat_vec_A_x[init_grain_index];\n"+
        "    double init_lat_vec_A_y = lat_vec_A_y[init_grain_index];\n"+
        "    double init_lat_vec_A_z = lat_vec_A_z[init_grain_index];\n"+
        "    \n"+
        "    double init_lat_vec_B_x = lat_vec_B_x[init_grain_index];\n"+
        "    double init_lat_vec_B_y = lat_vec_B_y[init_grain_index];\n"+
        "    double init_lat_vec_B_z = lat_vec_B_z[init_grain_index];\n"+
        "    \n"+
        "    double init_lat_vec_C_x = lat_vec_C_x[init_grain_index];\n"+
        "    double init_lat_vec_C_y = lat_vec_C_y[init_grain_index];\n"+
        "    double init_lat_vec_C_z = lat_vec_C_z[init_grain_index];\n"+
        "    \n"+
        "    // Determination of coordinates of normalized lattice vectors \n"+
        "    lat_vec_A_x[grain_indices[cell_index] - 1] = init_lat_vec_A_x; // signbit(0.5 - grain_indices[cell_index]%2)*init_lat_vec_B_x + signbit(-0.5 + grain_indices[cell_index]%2)*init_lat_vec_C_x; // \n"+
        "    lat_vec_A_y[grain_indices[cell_index] - 1] = init_lat_vec_A_y; // signbit(0.5 - grain_indices[cell_index]%2)*init_lat_vec_B_y + signbit(-0.5 + grain_indices[cell_index]%2)*init_lat_vec_C_y; // \n"+
        "    lat_vec_A_z[grain_indices[cell_index] - 1] = init_lat_vec_A_z; // signbit(0.5 - grain_indices[cell_index]%2)*init_lat_vec_B_z + signbit(-0.5 + grain_indices[cell_index]%2)*init_lat_vec_C_z; // \n"+
        "    \n"+
        "    lat_vec_B_x[grain_indices[cell_index] - 1] = init_lat_vec_B_x;\n"+
        "    lat_vec_B_y[grain_indices[cell_index] - 1] = init_lat_vec_B_y;\n"+
        "    lat_vec_B_z[grain_indices[cell_index] - 1] = init_lat_vec_B_z;\n"+
        "    \n"+
        "    lat_vec_C_x[grain_indices[cell_index] - 1] = init_lat_vec_C_x;\n"+
        "    lat_vec_C_y[grain_indices[cell_index] - 1] = init_lat_vec_C_y;\n"+
        "    lat_vec_C_z[grain_indices[cell_index] - 1] = init_lat_vec_C_z;\n"+
        "    \n"+
     */
        "}\n";
        
        //===========================================================================
        // Calculation of probabilities of generation of new grains in twin grains
        String prog_string_C = 
        "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n"+
        "\n"+
        "__kernel void kernel_C(__global const int   *init_grain_indices, __global const char  *en_types,\n" +
        "                       __global const char  *grain_rec_types,    __global const double *temperatures,\n" +
        "                       __global const double *low_tempr_recryst,  __global const double *high_tempr_recryst,\n" +
        "                       __global const double *max_prob_recryst,   __global const double *random,\n" +
        "                       __global const int   *neighbours1S_0,     __global const int   *neighbours1S_1,\n" +
        "                       __global const int   *neighbours1S_2,     __global const int   *neighbours1S_3,\n" +
        "                       __global const int   *neighbours1S_4,     __global const int   *neighbours1S_5,\n" +
        "                       __global const int   *neighbours1S_6,     __global const int   *neighbours1S_7,\n" +
        "                       __global const int   *neighbours1S_8,     __global const int   *neighbours1S_9,\n" +
        "                       __global const int   *neighbours1S_10,    __global const int   *neighbours1S_11,\n" +
        "                       __global       double *prob_new_grain,     __global       char  *new_grain_embryos)\n" +
        "{\n" +
        "    int cell_index         = get_global_id(0);\n" +
        "    int old_grain_index    = init_grain_indices[cell_index] - 1;\n" +
        "    char old_grain_rec_type = grain_rec_types[old_grain_index];\n" +
        "    \n" +
        "    double low_tempr  = (double)1200.0; // low_tempr_recryst[old_grain_index];\n" +
        "    double high_tempr = (double)1800.0; // high_tempr_recryst[old_grain_index];\n" +
        "    \n" +
        "    double aver_tempr_recryst = 0.5*(low_tempr + high_tempr);\n" +
        "\n" +
        "    // Probability of generation of new grain in current cell\n" +
        "    prob_new_grain[cell_index] = 100*max_prob_recryst[old_grain_index]*signbit(abs(en_types[cell_index] - "+INNER_CELL+") - 0.5)*\n"+
        "                  signbit(0.5 - abs(old_grain_rec_type - "+INITIAL_GRAIN+"))*signbit((double)new_grain_embryos[cell_index] - 0.5)*\n" +
        "                  signbit(low_tempr - temperatures[cell_index])*signbit(temperatures[cell_index] - high_tempr)*\n" +
     //   "               (signbit(temperatures[cell_index] - aver_tempr_recryst)*(temperatures[cell_index] - low_tempr)/(aver_tempr_recryst - low_tempr) + \n" +
     //   "                signbit(aver_tempr_recryst - temperatures[cell_index])*(high_tempr - temperatures[cell_index])/(high_tempr - aver_tempr_recryst))*\n" +
        "    signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_0 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_1 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_2 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_3 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_4 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_5 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_6 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_7 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_8 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_9 [cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_10[cell_index]] - 1] - "+NEW_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_11[cell_index]] - 1] - "+NEW_GRAIN+")*(-1) + 0.5 - 100)*\n" +
        "    signbit(abs(grain_rec_types[init_grain_indices[neighbours1S_0 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_1 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_2 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_3 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_4 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_5 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_6 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_7 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_8 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_9 [cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_10[cell_index]] - 1] - "+INITIAL_GRAIN+")* \n" +
        "            abs(grain_rec_types[init_grain_indices[neighbours1S_11[cell_index]] - 1] - "+INITIAL_GRAIN+") - 0.5 - 100);\n" +
        "    \n" +
        "    // Presence of new grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here\n" +
        "    char new_grain_embryo = signbit(0.5 - new_grain_embryos[cell_index]) + signbit(-0.5 + new_grain_embryos[cell_index])*signbit(random[cell_index] - prob_new_grain[cell_index]);\n" +
        "    new_grain_embryos[cell_index]  = new_grain_embryo;\n" +
        "    \n" +
//      "    __syncthreads();\n" +
        "    barrier(CLK_GLOBAL_MEM_FENCE);\n" +
        "}";
        
        // Printing of codes to files
        try
        {
            // Creation of directory for files with codes
            File paral_code_dir = new File("./user/paral_codes");
            paral_code_dir.mkdir();
            
            String file_name_0         = "./user/paral_codes/paral_code_0.txt";
            BufferedWriter bw_prog_0   = new BufferedWriter(new FileWriter(file_name_0));
            bw_prog_0.write(prog_string_0);
            bw_prog_0.flush();
            bw_prog_0.close();
            System.out.println("Code #0 is written to file "+file_name_0);
            
            String file_name_1         = "./user/paral_codes/paral_code_1.txt";
            BufferedWriter bw_prog_1   = new BufferedWriter(new FileWriter(file_name_1));
            bw_prog_1.write(prog_string_1);
            bw_prog_1.flush();
            bw_prog_1.close();
            System.out.println("Code #1 is written to file "+file_name_1);
            
            String file_name_2       = "./user/paral_codes/paral_code_2.txt";
            BufferedWriter bw_prog_2 = new BufferedWriter(new FileWriter(file_name_2));
            bw_prog_2.write(prog_string_2);
            bw_prog_2.flush();
            bw_prog_2.close();
            System.out.println("Code #2 is written to file "+file_name_2);
            
            String file_name_3_1       = "./user/paral_codes/paral_code_3_1.txt";
            BufferedWriter bw_prog_3_1 = new BufferedWriter(new FileWriter(file_name_3_1));
            bw_prog_3_1.write(prog_string_3_1);
            bw_prog_3_1.flush();
            bw_prog_3_1.close();
            System.out.println("Code #3.1 is written to file "+file_name_3_1);
            
            String file_name_3_2       = "./user/paral_codes/paral_code_3_2.txt";
            BufferedWriter bw_prog_3_2 = new BufferedWriter(new FileWriter(file_name_3_2));
            bw_prog_3_2.write(prog_string_3_2);
            bw_prog_3_2.flush();
            bw_prog_3_2.close();
            System.out.println("Code #3.2 is written to file "+file_name_3_2);
            
            String file_name_3_3       = "./user/paral_codes/paral_code_3_3.txt";
            BufferedWriter bw_prog_3_3 = new BufferedWriter(new FileWriter(file_name_3_3));
            bw_prog_3_3.write(prog_string_3_3);
            bw_prog_3_3.flush();
            bw_prog_3_3.close();
            System.out.println("Code #3.3 is written to file "+file_name_3_3);
            
            String file_name_4       = "./user/paral_codes/paral_code_4.txt";
            BufferedWriter bw_prog_4 = new BufferedWriter(new FileWriter(file_name_4));
            bw_prog_4.write(prog_string_4);
            bw_prog_4.flush();
            bw_prog_4.close();
            System.out.println("Code #4 is written to file "+file_name_4);
            
            String file_name_5       = "./user/paral_codes/paral_code_5.txt";
            BufferedWriter bw_prog_5 = new BufferedWriter(new FileWriter(file_name_5));
            bw_prog_5.write(prog_string_5);
            bw_prog_5.flush();
            bw_prog_5.close();
            System.out.println("Code #5 is written to file "+file_name_5);
            
            String file_name_5_1     = "./user/paral_codes/paral_code_5_1.txt";
            BufferedWriter bw_prog_5_1 = new BufferedWriter(new FileWriter(file_name_5_1));
            bw_prog_5_1.write(prog_string_5_1);
            bw_prog_5_1.flush();
            bw_prog_5_1.close();
            System.out.println("Code #5.1 is written to file "+file_name_5_1);
            
            String file_name_5_2     = "./user/paral_codes/paral_code_5_2.txt";
            BufferedWriter bw_prog_5_2 = new BufferedWriter(new FileWriter(file_name_5_2));
            bw_prog_5_2.write(prog_string_5_2);
            bw_prog_5_2.flush();
            bw_prog_5_2.close();
            System.out.println("Code #5.2 is written to file "+file_name_5_2);
            
            String file_name_6       = "./user/paral_codes/paral_code_6.txt";
            BufferedWriter bw_prog_6 = new BufferedWriter(new FileWriter(file_name_6));
            bw_prog_6.write(prog_string_6);
            bw_prog_6.flush();
            bw_prog_6.close();
            System.out.println("Code #6 is written to file "+file_name_6);
            
            String file_name_7       = "./user/paral_codes/paral_code_7.txt";
            BufferedWriter bw_prog_7 = new BufferedWriter(new FileWriter(file_name_7));
            bw_prog_7.write(prog_string_7);
            bw_prog_7.flush();
            bw_prog_7.close();
            System.out.println("Code #7 is written to file "+file_name_7);
            
            String file_name_7_1       = "./user/paral_codes/paral_code_7_1.txt";
            BufferedWriter bw_prog_7_1 = new BufferedWriter(new FileWriter(file_name_7_1));
            bw_prog_7_1.write(prog_string_7_1);
            bw_prog_7_1.flush();
            bw_prog_7_1.close();
            System.out.println("Code #7.1 is written to file "+file_name_7_1);
            
            String file_name_7_2       = "./user/paral_codes/paral_code_7_2.txt";
            BufferedWriter bw_prog_7_2 = new BufferedWriter(new FileWriter(file_name_7_2));
            bw_prog_7_2.write(prog_string_7_2);
            bw_prog_7_2.flush();
            bw_prog_7_2.close();
            System.out.println("Code #7.2 is written to file "+file_name_7_2);
            
            String file_name_7_mom     = "./user/paral_codes/paral_code_7_mom.txt";
            BufferedWriter bw_prog_7_mom = new BufferedWriter(new FileWriter(file_name_7_mom));
            bw_prog_7_mom.write(prog_string_7_mom);
            bw_prog_7_mom.flush();
            bw_prog_7_mom.close();
            System.out.println("Code #7_mom is written to file "+file_name_7_mom);
            
            String file_name_8       = "./user/paral_codes/paral_code_8.txt";
            BufferedWriter bw_prog_8 = new BufferedWriter(new FileWriter(file_name_8));
            bw_prog_8.write(prog_string_8);
            bw_prog_8.flush();
            bw_prog_8.close();
            System.out.println("Code #8 is written to file "+file_name_8);
            
            String file_name_8_1     = "./user/paral_codes/paral_code_8_1.txt";
            BufferedWriter bw_prog_8_1 = new BufferedWriter(new FileWriter(file_name_8_1));
            bw_prog_8_1.write(prog_string_8_1);
            bw_prog_8_1.flush();
            bw_prog_8_1.close();
            System.out.println("Code #8.1 is written to file "+file_name_8_1);
            
            String file_name_8_2     = "./user/paral_codes/paral_code_8_2.txt";
            BufferedWriter bw_prog_8_2 = new BufferedWriter(new FileWriter(file_name_8_2));
            bw_prog_8_2.write(prog_string_8_2);
            bw_prog_8_2.flush();
            bw_prog_8_2.close();
            System.out.println("Code #8.2 is written to file "+file_name_8_2);
            
            String file_name_9       = "./user/paral_codes/paral_code_9.txt";
            BufferedWriter bw_prog_9 = new BufferedWriter(new FileWriter(file_name_9));
            bw_prog_9.write(prog_string_9);
            bw_prog_9.flush();
            bw_prog_9.close();
            System.out.println("Code #9 is written to file "+file_name_9);
            
            String file_name_A       = "./user/paral_codes/paral_code_A.txt";
            BufferedWriter bw_prog_A = new BufferedWriter(new FileWriter(file_name_A));
            bw_prog_A.write(prog_string_A);
            bw_prog_A.flush();
            bw_prog_A.close();
            System.out.println("Code #A is written to file "+file_name_A);
            
            String file_name_B       = "./user/paral_codes/paral_code_B.txt";
            BufferedWriter bw_prog_B = new BufferedWriter(new FileWriter(file_name_B));
            bw_prog_B.write(prog_string_B);
            bw_prog_B.flush();
            bw_prog_B.close();
            System.out.println("Code #B is written to file "+file_name_B);
            
            String file_name_C       = "./user/paral_codes/paral_code_C.txt";
            BufferedWriter bw_prog_C = new BufferedWriter(new FileWriter(file_name_C));
            bw_prog_C.write(prog_string_C);
            bw_prog_C.flush();
            bw_prog_C.close();
            System.out.println("Code #C is written to file "+file_name_C);
        }
        catch(IOException io_exc)
        {
            System.out.println("IOException: "+io_exc);
        }
        
        // Obtaining of current time
        Date date_0 = new Date();
            
        // ---------- Allocate the memory objects for the input- and output data ------------
        // Creation of the memory objects for arrays of initial temperatures, heat capacities, 
        // heat conductivities and densities of cells
        cl_mem memObject_init_temprs              = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * (work_item_number+1), init_temprs_ptr, null);
        
        // Memory object for array of minimal temperatures of boundary cells
        cl_mem memObject_bound_cell_min_temprs    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * (work_item_number+1), bound_cell_min_temprs_ptr, null);
        
        // Memory object for array of maximal temperatures of boundary cells
        cl_mem memObject_bound_cell_max_temprs    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * (work_item_number+1), bound_cell_max_temprs_ptr, null);    
        
        // Memory object for array of mechanical energies of cells
        cl_mem memObject_mech_energies            = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * (work_item_number + 1), mech_energies_ptr, null);
        
        // Memory object for array of initial mechanical energies of cells
        cl_mem memObject_init_mech_energies       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * (work_item_number + 1), init_mech_energies_ptr, null);
        
        // Memory object for array of initial dissipation energies of cells
        cl_mem memObject_init_diss_energies       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, init_diss_energies_ptr, null);
        
        // Memory object for array of dissipation energies of cells
        cl_mem memObject_diss_energies            = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, diss_energies_ptr, null);
        
        // Memory object for array of influxes of dissipation energy to cells
        cl_mem memObject_diss_en_influxes         = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, diss_en_influxes_ptr, null);
        
        // Memory object for array of initial defect densities of cells
        cl_mem memObject_init_def_densities       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, init_def_densities_ptr, null);
        
        // Memory object for array of changes of defect densities of cells
        cl_mem memObject_def_density_changes      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, def_density_changes_ptr, null);
        
        // Memory object for array of relative changes of dissipation energy in comparison with total mechanical energy
        cl_mem memObject_rel_diss_en_changes      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, rel_diss_en_changes_ptr, null);
        
        // Memory object for array of relative dissipation energies in comparison with total mechanical energy
        cl_mem memObject_rel_diss_energies        = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, rel_diss_energies_ptr, null);
        
        // Memory object for array of relative changes of dissipation energy in comparison with current influx of total mechanical energy
        cl_mem memObject_rel_diss_en_influxes     = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, rel_diss_en_influxes_ptr, null);
        
        // Memory object for array of dissipation coefficients for grains
        cl_mem memObject_coeff_diss              = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                   Sizeof.cl_double * grain_number, coeff_diss_ptr, null);
        
        // Memory object for array of final defect densities of cells
        cl_mem memObject_final_def_densities      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, final_def_densities_ptr, null);
        
        // Memory object for array of influxes of specific mechanical energy to boundary cells per time step
        cl_mem memObject_bound_mech_influxes      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * bound_mech_influxes.length, bound_mech_influxes_ptr, null);
        
        // Memory object for array of minimal influxes of specific mechanical energy to boundary cells per time step
        cl_mem memObject_bound_min_mech_influxes  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * bound_min_mech_influxes.length, bound_min_mech_influxes_ptr, null);
        
        // Memory object for array of maximal influxes of specific mechanical energy to boundary cells per time step
        cl_mem memObject_bound_max_mech_influxes  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * bound_max_mech_influxes.length, bound_max_mech_influxes_ptr, null);
        
        // Memory object for array of types of boundary conditions
        cl_mem memObject_bound_cell_time_function_types = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * bound_cell_time_function_types.length, bound_cell_time_function_types_ptr, null);
        
        // Memory object for array of time periods of loading
        cl_mem memObject_bound_cell_load_time_portions = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * bound_cell_load_time_portions.length, bound_cell_load_time_portions_ptr, null);
        
        // Memory object for array of time periods when loading is absent or it has opposite sign
        cl_mem memObject_bound_cell_relax_time_portions = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * bound_cell_relax_time_portions.length, bound_cell_relax_time_portions_ptr, null);
        
        // Memory object for array of heat capacities of cells
        cl_mem memObject_heat_caps             = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, heat_caps_ptr, null);
        
        // Memory object for array of heat conductivities of cells
        cl_mem memObject_heat_conds            = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, heat_conds_ptr, null);
        
        // Memory object for array of heat expansion coefficients of grains
        cl_mem memObject_heat_exp_coeffs       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, heat_exp_coeffs_ptr, null);
        
        // Memory object for array of Young modules of grains
        cl_mem memObject_mod_elast             = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, mod_elast_ptr, null);
        
        // Memory object for array of Young modules of cells
        cl_mem memObject_cell_mod_elast        = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, cell_mod_elast_ptr, null);
        
        // Memory object for array of Young modules of transformed material
        cl_mem memObject_new_mod_elast         = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, new_mod_elast_ptr, null);
        
        // Memory object for array of shear modules of grains
        cl_mem memObject_mod_shear             = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, mod_shear_ptr, null);
        
        // Memory object for array of lattice parameters of grains
        cl_mem memObject_lattice_parameters    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, lattice_parameters_ptr, null);
        
        // Memory object for array of heat strains of cells
        cl_mem memObject_heat_strains          = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, heat_strains_ptr, null);
        
        // Memory object for array of initial heat strains of cells
        cl_mem memObject_init_heat_strains     = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, init_heat_strains_ptr, null);
        
        // Memory object for array of densities of cells
        cl_mem memObject_densities             = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, densities_ptr, null);
        
        // Memory objects for indices of neighbour cells
        cl_mem[] memObject_neighbours1S        = new cl_mem[neighb1S_number];
        
        // Memory objects for Specific misorientation angles for grain boundaries
        cl_mem[] memObject_spec_angle_diff     = new cl_mem[neighb1S_number];
        
        // Memory objects for Maximal heat mobilities at grain boundaries
        cl_mem[] memObject_neighb_heat_max_mob = new cl_mem[neighb1S_number];
        
        // Memory objects for Specific grain boundary energies
        cl_mem[] memObject_spec_bound_energies = new cl_mem[neighb1S_number];
        
        // Memory objects for Grain boundary energies
        cl_mem[] memObject_bound_energies      = new cl_mem[neighb1S_number];
        
        // Memory objects for Velocities of grain boundary movement
        cl_mem[] memObject_bound_velocities    = new cl_mem[neighb1S_number];
        
        // Memory objects for Probabilities of switching of cell to neighbour grains
        cl_mem[] memObject_probs               = new cl_mem[neighb1S_number];
        
        // Memory objects for Sums of switch probabilities
        cl_mem[] memObject_prob_sums           = new cl_mem[neighb1S_number+2];
        
        // Memory objects for arrays of 0 and 1 responsible for possibility of cell transition to adjacent grain
        cl_mem[] memObject_poss_switches       = new cl_mem[neighb1S_number];
        
        // Creation of memory objects for indices and parameters of neighbour cells
        for (int mem_obj_counter = 0; mem_obj_counter<neighb1S_number; mem_obj_counter++)
        {
            memObject_neighbours1S[mem_obj_counter]        = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int*work_item_number, neighbours1S_ptr[mem_obj_counter], null);
            
            memObject_spec_angle_diff[mem_obj_counter]     = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, spec_angle_diff_ptr[mem_obj_counter], null);
            
            memObject_neighb_heat_max_mob[mem_obj_counter] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, neighb_heat_max_mob_ptr[mem_obj_counter], null);
            
            memObject_spec_bound_energies[mem_obj_counter] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, spec_bound_energies_ptr[mem_obj_counter], null);
            
            memObject_bound_energies[mem_obj_counter]      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, bound_energies_ptr[mem_obj_counter], null);
            
            memObject_bound_velocities[mem_obj_counter]    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, bound_velocities_ptr[mem_obj_counter], null);
            
            memObject_probs[mem_obj_counter]               = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, probs_ptr[mem_obj_counter], null);
            
            memObject_prob_sums[mem_obj_counter]           = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, prob_sums_ptr[mem_obj_counter], null);
            
            memObject_poss_switches[mem_obj_counter]       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * work_item_number, poss_switches_ptr[mem_obj_counter], null);
        }
        
        memObject_prob_sums[neighb1S_number]           = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, prob_sums_ptr[neighb1S_number], null);
        
        memObject_prob_sums[neighb1S_number+1]           = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, prob_sums_ptr[neighb1S_number+1], null);
        
        // Memory objects for arrays of cells from pairs necessary for calculation of force moments
        cl_mem[] memObject_cell_pairs1S       = new cl_mem[4*neighb1S_number];
        
        // Creation of memory objects for arrays of cells from pairs necessary for calculation of force moments
        for (int mem_obj_counter = 0; mem_obj_counter < 4*neighb1S_number; mem_obj_counter++)
        {
            memObject_cell_pairs1S[mem_obj_counter] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int*work_item_number, cell_pairs1S_ptr[mem_obj_counter], null);
        }
        
        // Creation of memory objects for pointers to 3 arrays of coordinates X, Y and Z of cells
        cl_mem memObject_cell_coords_X = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*(work_item_number+1), cell_coords_X_ptr, null);
        
        cl_mem memObject_cell_coords_Y = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*(work_item_number+1), cell_coords_Y_ptr, null);
        
        cl_mem memObject_cell_coords_Z = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*(work_item_number+1), cell_coords_Z_ptr, null);
        
        // Creation of memory objects for pointers to 3 arrays of coordinates X, Y, Z of force moments of cells
        cl_mem memObject_force_moments_X = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, force_moments_X_ptr, null);
        
        cl_mem memObject_force_moments_Y = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, force_moments_Y_ptr, null);
        
        cl_mem memObject_force_moments_Z = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, force_moments_Z_ptr, null);
        
        // Memory object for pointer to array of absolute values of force moments of cells
        cl_mem memObject_abs_mom = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, abs_mom_ptr, null);
        
        // Memory objects for pointers to arrays of components of angle velocities of cells
        cl_mem memObject_angle_velocity_X = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, angle_velocity_X_ptr, null);
        
        cl_mem memObject_angle_velocity_Y = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, angle_velocity_Y_ptr, null);
        
        cl_mem memObject_angle_velocity_Z = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, angle_velocity_Z_ptr, null);
        
        // Memory object for pointer to array of absolute values of angle velocities of cells
        cl_mem memObject_abs_angle_velocity = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, abs_angle_velocity_ptr, null);
        
        // Memory objects for pointers to arrays of components of torsion angles of cells
        cl_mem memObject_torsion_angle_X = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, torsion_angle_X_ptr, null);
        
        cl_mem memObject_torsion_angle_Y = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, torsion_angle_Y_ptr, null);
        
        cl_mem memObject_torsion_angle_Z = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, torsion_angle_Z_ptr, null);
        
        // Memory object for pointer to array of absolute values of torsion angles of cells
        cl_mem memObject_abs_torsion_angle = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, abs_torsion_angle_ptr, null);
        
        // Memory object for pointer to array of numbers, each of which determines whether the cell become cracked
        cl_mem memObject_crack_sum = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int*work_item_number, crack_sum_ptr, null);
        
        // Creation of the memory object for energy types of cells
        cl_mem memObject_en_types       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * (work_item_number+1), en_types_ptr, null);
        
        // Creation of the memory object for cell location types
        cl_mem memObject_loc_types      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * work_item_number, loc_types_ptr, null);
        
         // Creation of the memory object for cell location types at next time step
        cl_mem memObject_new_loc_types  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * work_item_number, new_loc_types_ptr, null);
        
        // Creation of the memory object for new energy types of cells
        cl_mem memObject_new_en_types   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * (work_item_number+1), new_en_types_ptr, null);
        
        // Creation of the memory object for temperatures of cells
        cl_mem memObject_temperatures   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, temperatures_ptr, null);
        
        // Creation of the memory object for heat influxes to cells from neighbours
        cl_mem memObject_heat_influxes  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, heat_influxes_ptr, null);
        
        // Creation of the memory object for mechanical energy influxes to cells from neighbours
        cl_mem memObject_mech_influxes  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, mech_influxes_ptr, null);
        
        // Creation of the memory object for index of current neighbour cell
        cl_mem memObject_neighb_id      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int, neighb_id_ptr, null);
        
        // Creation of the memory object for the value necessary for calculation of cell temperature
        cl_mem memObject_factor         = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double, factor_ptr, null);
        
        // Creation of the memory object for test value
      //  cl_mem memObject_step_counter;
        
        // Creation of the memory object for array of highest misorientation angles for grains 
        cl_mem memObject_angleHAGB      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double, angleHAGB_ptr, null);
        
        // Creation of the memory object for array of 1st orientation angles for grains
        cl_mem memObject_grain_angles_1   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, grain_angles_1_ptr, null);
        
        // Creation of the memory object for array of 2nd orientation angles for grains
        cl_mem memObject_grain_angles_2   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, grain_angles_2_ptr, null);
        
        // Creation of the memory object for array of 3rd orientation angles for grains
        cl_mem memObject_grain_angles_3   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, grain_angles_3_ptr, null);
        
        // Creation of the memory object for array of highest misorientation angles for grains
        cl_mem memObject_max_grain_angles   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, max_grain_angles_ptr, null);
        
        // Creation of the memory object for array of grain types
        cl_mem memObject_grain_types        = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * grain_number, grain_types_ptr, null);
        
        // Creation of the memory object for array of initial grain types according to its role in recrystallization process
        cl_mem memObject_init_grain_rec_types    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * grain_number, init_grain_rec_types_ptr, null);
        
        // Creation of the memory object for array of final grain types according to its role in recrystallization process
        cl_mem memObject_grain_rec_types    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * grain_number, grain_rec_types_ptr, null);
        
        // Creation of the memory object for array of indices of grains, from which twinning grains grow
        cl_mem memObject_root_grain_indices = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * grain_number, root_grain_indices_ptr, null);
        
        // Creation of the memory object for array of indices of new grains where embryos of twin grains appear
        cl_mem memObject_twin_emb_root_gr_indices = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * work_item_number, twin_emb_root_gr_indices_ptr, null);
        
        // Creation of the memory object for array of initial indices of grains
        cl_mem memObject_init_grain_indices = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * work_item_number, init_grain_indices_ptr, null);
        
        // Creation of the memory object for array of final indices of grains
        cl_mem memObject_grain_indices      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * work_item_number, grain_indices_ptr, null);
        
        // Creation of the memory object for array of maximal values of grain boundary heat mobility
        cl_mem memObject_heatMaxMobility    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, heatMaxMobility_ptr, null);
        
        // Creation of the memory object for array of maximal values of grain boundary dislocation mobility
        cl_mem memObject_dislMaxMobility    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, dislMaxMobility_ptr, null);
        
        // Creation of the memory object for array of maximal values of grain boundary mechanical mobility
        cl_mem memObject_mechMaxMobility    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, mechMaxMobility_ptr, null);
        
        // Creation of the memory object for array of maximal values of grain boundary energy
        cl_mem memObject_maxBoundEnergy     = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, maxBoundEnergy_ptr, null);
        
        // Creation of the memory object for array of dislocation energies of grains
        cl_mem memObject_disl_energies      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, disl_energies_ptr, null);
        
        // Creation of the memory object for array of minimal dislocation energies of grains
        cl_mem memObject_min_disl_energies  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, min_disl_energies_ptr, null);
        
        // Creation of the memory object for array of phonon portions of grains
        cl_mem memObject_phonon_portions    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, phonon_portions_ptr, null);
        
        // Creation of array of random numbers
        for(int rand_counter = 0; rand_counter < work_item_number; rand_counter++)
          rand_nums[rand_counter] = (double)Math.random();
            
        rand_nums_ptr = Pointer.to(rand_nums);
        
        // Memory object for array of random numbers
        cl_mem memObject_rand_nums = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, rand_nums_ptr, null);
        
        // Memory object for the array of the low temperatures of recrystallization (generation of new grains)
        cl_mem memObject_low_tempr_recryst   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, low_tempr_recryst_ptr, null);
        
        // Memory object for the array of the high temperatures of recrystallization (generation of new grains)
        cl_mem memObject_high_tempr_recryst  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, high_tempr_recryst_ptr, null);
        
        // Memory object for the array of the low temperatures of twinning
        cl_mem memObject_low_tempr_twinning  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, low_tempr_twinning_ptr, null);
        
        // Memory object for the array of the high temperatures of twinning
        cl_mem memObject_high_tempr_twinning = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, high_tempr_twinning_ptr, null);
        
        // Memory object for the array of the maximal probabilities of recrystallization (generation of new grains)
        cl_mem memObject_max_prob_recryst    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, max_prob_recryst_ptr, null);
        
        // Memory object for the array of the maximal probabilities of twinning
        cl_mem memObject_max_prob_twinning   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, max_prob_twinning_ptr, null);
        
        // Memory object for the array of the values of minimal density of dislocations
        cl_mem memObject_min_disl_density = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, min_disl_density_ptr, null);
        
        // Memory object for the array of the probabilities of recrystallization (generation of new grains)
        cl_mem memObject_prob_new_grain      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, prob_new_grain_ptr, null);
        
        // Memory object for the array of the probabilities of twinning
        cl_mem memObject_prob_twinning       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, prob_twinning_ptr, null);
        
        // Memory object for the array of presences of new grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
        cl_mem memObject_new_grain_embryos   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * work_item_number, new_grain_embryos_ptr, null);
        
        // Memory object for the array of presences of twin grain embryo in current cell: 0 - embryo is absent, 1 - embryo is here
        cl_mem memObject_twin_grain_embryos  = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * work_item_number, twin_grain_embryos_ptr, null);
        
        // Memory objects for array of lengths of lattice vectors for grains
        cl_mem memObject_lattice_vector_A_lengths = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, lattice_vector_A_lengths_ptr, null);
        
        cl_mem memObject_lattice_vector_B_lengths = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, lattice_vector_B_lengths_ptr, null);
        
        cl_mem memObject_lattice_vector_C_lengths = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, lattice_vector_C_lengths_ptr, null);
        
        // Memory objects for array of coefficients of lattice anisotropy for grains
        cl_mem memObject_lattice_anis_coeff       = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, lattice_anis_coeff_ptr, null);
        
        // Memory objects for arrays of coordinates of 3 lattice vectors for grains
        cl_mem[] memObject_lattice_vectors = new cl_mem[9];
        
        for(int ptr_counter = 0; ptr_counter < 9; ptr_counter++)
          memObject_lattice_vectors[ptr_counter] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * grain_number, lattice_vectors_ptr[ptr_counter], null);
        
        // Memory objects for array of coordinates of all possible vectors of local growth of grain (the number of vectors is equal to the number of cell facets)
        cl_mem memObject_growth_vectors_coordinates = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * growth_vectors_coordinates.length, growth_vectors_coordinates_ptr, null);
        
        // Memory object for array of values for crack generation
        cl_mem memObject_crack_values = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, crack_values_ptr, null);
        
        // Memory object for array with the variable responsible for simulation of crack generation
        cl_mem memObject_simulate_cracks = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char, simulate_cracks_ptr, null);
        
        // Memory objects for arrays of stresses at cell boundaries
        cl_mem[] memObject_stresses = new cl_mem[neighb1S_number];
        
        for (int bound_counter = 0; bound_counter < neighb1S_number; bound_counter++)
          memObject_stresses[bound_counter] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, stresses_ptr[bound_counter], null);
        
        // Memory objects for arrays of coordinates of stress vectors for all cells
        cl_mem memObject_stresses_X = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, stresses_X_ptr, null);
        
        cl_mem memObject_stresses_Y = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, stresses_Y_ptr, null);
        
        cl_mem memObject_stresses_Z = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double*work_item_number, stresses_Z_ptr, null);
        
        System.out.println();
        System.out.println("All memory objects are created!!!");
        System.out.println();
        
        // TEST
      //  String test_str = "1 2 3 4 5 6 7 8 9 0";
      //  System.out.println("Test string: "+test_str);
      //  System.out.println("Number of symbols in test string: "+test_str.length());
      //  System.out.println();
        //============================================================================
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        System.out.println("Number of symbols in program #0: "+prog_string_0.length());
        
        cl_program program_0 = clCreateProgramWithSource(context, 1, new String[]{prog_string_0}, new long[]{prog_string_0.length()}, null);
        
        System.out.println("Program #0 is created!");
        
        // Building of the program
        clBuildProgram(program_0, 0, null, null, null, null);
        
        System.out.println("Program #0 is built!");
        
        // Creation of the 1st kernel
        cl_kernel kernel_0 = clCreateKernel(program_0, "kernel_0", null);
        
        System.out.println("Kernel_0 is created!");
        
        //============================================================================
        //  Creates a program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        System.out.println("Number of symbols in program #1: "+prog_string_1.length());
        
        cl_program program_1 = clCreateProgramWithSource(context, 1, new String[]{prog_string_1}, new long[]{prog_string_1.length()}, null);        
        
        System.out.println("Program #1 is created!");
        
        // Building of the program
        clBuildProgram(program_1, 0, null, null, null, null);
        
        System.out.println("Program #1 is built!");
        
        // Creation of the 1st kernel
        cl_kernel kernel_1 = clCreateKernel(program_1, "kernel_1", null);
        
        System.out.println("Kernel_1 is created!");
        
        //============================================================================
        //  Creates a 2nd program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program_2 = clCreateProgramWithSource(context, 1, new String[]{prog_string_2}, new long[]{prog_string_2.length()}, null);
        
        System.out.println("Program #2 is created!");
        
        // Building of the 2nd program
        clBuildProgram(program_2, 0, null, null, null, null);
        
        System.out.println("Program #2 is built!");
        
        // Creation of the 2nd kernel
        cl_kernel kernel_2 = clCreateKernel(program_2, "kernel_2", null);
        
        System.out.println("Kernel_2 is created!");
        
        //============================================================================
        //  Creates a 3rd program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program_3_1 = clCreateProgramWithSource(context, 1, new String[]{prog_string_3_1}, new long[]{prog_string_3_1.length()}, null);
        
        // Building of the 3rd program
        clBuildProgram(program_3_1, 0, null, null, null, null);
        
        // Creation of the 3rd kernel
        cl_kernel kernel_3_1 = clCreateKernel(program_3_1, "kernel_3_1", null);
        
        System.out.println("Kernel_3_1 is created");
        
        //============================================================================
        //  Creates a 3rd program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program_3_2 = clCreateProgramWithSource(context, 1, new String[]{prog_string_3_2}, new long[]{prog_string_3_2.length()}, null);
        
        // Building of the 3rd program
        clBuildProgram(program_3_2, 0, null, null, null, null);
        
        // Creation of the 3rd kernel
        cl_kernel kernel_3_2 = clCreateKernel(program_3_2, "kernel_3_2", null);
        
        System.out.println("Kernel_3_2 is created");
        
        //============================================================================
        //  Creates a 3rd program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program_3_3 = clCreateProgramWithSource(context, 1, new String[]{prog_string_3_3}, new long[]{prog_string_3_3.length()}, null);
        
        // Building of the 3rd program
        clBuildProgram(program_3_3, 0, null, null, null, null);
        
        // Creation of the 3rd kernel
        cl_kernel kernel_3_3 = clCreateKernel(program_3_3, "kernel_3_3", null);
        
        System.out.println("Kernel_3_3 is created");
        
        //============================================================================
        //  Creates a 4th program object for a context, and loads the source code 
        // specified by the text strings in the strings array into the program object.
        cl_program program_4 = clCreateProgramWithSource(context, 1, new String[]{prog_string_4}, new long[]{prog_string_4.length()}, null);
        
        // Building of the 4th program
        clBuildProgram(program_4, 0, null, null, null, null);
        
        // Creation of the 4th kernel
        cl_kernel kernel_4 = clCreateKernel(program_4, "kernel_4", null);
        
        System.out.println("Kernel_4 is created");
        
        //============================================================================
        // Creation of 5th kernel for simulation of mechanical loading
        cl_program program_5 = clCreateProgramWithSource(context, 1, new String[]{prog_string_5}, new long[]{prog_string_5.length()}, null);
        
        // Building of the 5th program
        clBuildProgram(program_5, 0, null, null, null, null);
        
        // Creation of the 5th kernel
        cl_kernel kernel_5 = clCreateKernel(program_5, "kernel_5", null);
        
        System.out.println("Kernel_5 is created");
        
        //============================================================================
        // Creation of kernel #5.1 for simulation of mechanical loading
        cl_program program_5_1 = clCreateProgramWithSource(context, 1, new String[]{prog_string_5_1}, new long[]{prog_string_5_1.length()}, null);
        
        // Building of the program #5.1 
        clBuildProgram(program_5_1, 0, null, null, null, null);
        
        // Creation of the kernel #5.1 
        cl_kernel kernel_5_1 = clCreateKernel(program_5_1, "kernel_5_1", null);
        
        //============================================================================
        // Creation of kernel #5.2 for calculation of stress vectors for all cells
        cl_program program_5_2 = clCreateProgramWithSource(context, 1, new String[]{prog_string_5_2}, new long[]{prog_string_5_2.length()}, null);
        
        // Building of the program #5.2
        clBuildProgram(program_5_2, 0, null, null, null, null);
        
        // Creation of the kernel #5.2
        cl_kernel kernel_5_2 = clCreateKernel(program_5_2, "kernel_5_2", null);
        
        //============================================================================
        // Creation of 6th kernel for simulation of mechanical loading
        cl_program program_6 = clCreateProgramWithSource(context, 1, new String[]{prog_string_6}, new long[]{prog_string_6.length()}, null);
        
        // Building of the 6th program
        clBuildProgram(program_6, 0, null, null, null, null);
        
        // Creation of the 6th kernel
        cl_kernel kernel_6 = clCreateKernel(program_6, "kernel_6", null);
        
        System.out.println();
        System.out.println("Kernels 1-6 are created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of 7th kernel for calculation of force moments
        cl_program program_7 = clCreateProgramWithSource(context, 1, new String[]{prog_string_7}, new long[]{prog_string_7.length()}, null);
        
        // Building of the 7th program
        clBuildProgram(program_7, 0, null, null, null, null);
        
        // Creation of the 7th kernel
        cl_kernel kernel_7 = clCreateKernel(program_7, "kernel_7", null);
        
        System.out.println();
        System.out.println("Kernel 7 is created!!!");
        System.out.println();
                
        //============================================================================
        // Creation of kernel # 7.1 for calculation of force moments
        cl_program program_7_1 = clCreateProgramWithSource(context, 1, new String[]{prog_string_7_1}, new long[]{prog_string_7_1.length()}, null);
        
        // Building of the program # 7.1
        clBuildProgram(program_7_1, 0, null, null, null, null);
        
        // Creation of the kernel # 7.1
        cl_kernel kernel_7_1 = clCreateKernel(program_7_1, "kernel_7_1", null);
        
        System.out.println();
        System.out.println("Kernel 7.1 is created!!!");
        System.out.println();
                
        //============================================================================
        // Creation of kernel # 7.2 for calculation of force moments
        cl_program program_7_2 = clCreateProgramWithSource(context, 1, new String[]{prog_string_7_2}, new long[]{prog_string_7_2.length()}, null);
        
        // Building of the program # 7.2
        clBuildProgram(program_7_2, 0, null, null, null, null);
        
        // Creation of the kernel # 7.2
        cl_kernel kernel_7_2 = clCreateKernel(program_7_2, "kernel_7_2", null);
        
        System.out.println();
        System.out.println("Kernel 7.2 is created!!!");
        System.out.println();   
        
        //============================================================================
        // Creation of kernel # 7_mom for calculation of force moments
        cl_program program_7_mom = clCreateProgramWithSource(context, 1, new String[]{prog_string_7_mom}, new long[]{prog_string_7_mom.length()}, null);
        
        // Building of the program # 7_mom
        clBuildProgram(program_7_mom, 0, null, null, null, null);
        
        // Creation of the kernel # 7_mom
        cl_kernel kernel_7_mom = clCreateKernel(program_7_mom, "kernel_7_mom", null);
        
        System.out.println();
        System.out.println("Kernel # 7_mom is created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of 8th kernel for calculation of dissipation energy
        cl_program program_8 = clCreateProgramWithSource(context, 1, new String[]{prog_string_8}, new long[]{prog_string_8.length()}, null);
        
        // Building of the 8th program
        clBuildProgram(program_8, 0, null, null, null, null);
        
        // Creation of the 8th kernel
        cl_kernel kernel_8 = clCreateKernel(program_8, "kernel_8", null);
        
        System.out.println();
        System.out.println("Kernel 8 is created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of  kernel # 8_1 for calculation of dissipation energy
        cl_program program_8_1 = clCreateProgramWithSource(context, 1, new String[]{prog_string_8_1}, new long[]{prog_string_8_1.length()}, null);
        
        System.out.println();
        System.out.println("Program 8.1 is created!!!");
        
        // Building of the program # 8_1 
        clBuildProgram(program_8_1, 0, null, null, null, null);
        
        System.out.println("Program 8.1 is built!!!");
        
        // Creation of the kernel # 8_1 
        cl_kernel kernel_8_1 = clCreateKernel(program_8_1, "kernel_8_1", null);
        
        System.out.println("Kernel 8.1 is created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of  kernel # 8_2 for distribution of energy of each cracked cell among its neighbours at 1st coordination sphere
        cl_program program_8_2 = clCreateProgramWithSource(context, 1, new String[]{prog_string_8_2}, new long[]{prog_string_8_2.length()}, null);
        
        // Building of the program # 8_2
        clBuildProgram(program_8_2, 0, null, null, null, null);
        
        // Creation of the kernel # 8_2 
        cl_kernel kernel_8_2 = clCreateKernel(program_8_2, "kernel_8_2", null);
        
        System.out.println();
        System.out.println("Kernel 8.2 is created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of 9th kernel for calculation of new value of mechanical energy
        cl_program program_9 = clCreateProgramWithSource(context, 1, new String[]{prog_string_9}, new long[]{prog_string_9.length()}, null);
        
        // Building of the 9th program
        clBuildProgram(program_9, 0, null, null, null, null);
        
        // Creation of the 9th kernel
        cl_kernel kernel_9 = clCreateKernel(program_9, "kernel_9", null);
        
        System.out.println();
        System.out.println("Kernels 7-9 are created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of A-th kernel for calculation of probabilities of generation of new grains and twinning
        cl_program program_A = clCreateProgramWithSource(context, 1, new String[]{prog_string_A}, new long[]{prog_string_A.length()}, null);
        
        // Building of the A-th program
        clBuildProgram(program_A, 0, null, null, null, null);
        
        // Creation of the A-th kernel
        cl_kernel kernel_A = clCreateKernel(program_A, "kernel_A", null);
        
        System.out.println();
        System.out.println("Kernel A is created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of B-th kernel for addition of cells at 1st coordination sphere of embryos to new grains
        cl_program program_B = clCreateProgramWithSource(context, 1, new String[]{prog_string_B}, new long[]{prog_string_B.length()}, null);
        
        // Building of the B-th program
        try
        {   
            clBuildProgram(program_B, 0, null, null, null, null); 
        }
        catch(org.jocl.CLException cl_exc)
        {   
            System.out.println("EXCEPTION in program_B: "+cl_exc);
        }
        
        // Creation of the B-th kernel
        cl_kernel kernel_B = clCreateKernel(program_B, "kernel_B", null);
        
        System.out.println();
        System.out.println("Kernel B is created!!!");
        System.out.println();
        
        //============================================================================
        // Creation of C-th kernel for calculation of probabilities of generation of new grains in twin grains
        cl_program program_C = clCreateProgramWithSource(context, 1, new String[]{prog_string_C}, new long[]{prog_string_C.length()}, null);
        
        // Building of the C-th program
        try 
        {  
            clBuildProgram(program_C, 0, null, null, null, null); 
        }
        catch(org.jocl.CLException cl_exc)
        {      
            System.out.println("EXCEPTION in program_C: "+cl_exc);
        }
        
        // Creation of the C-th kernel
        cl_kernel kernel_C = clCreateKernel(program_C, "kernel_C", null);
        
        System.out.println();
        System.out.println("Kernel C is created!!!");
        System.out.println();
        
        System.out.println();
        System.out.println("All kernels are created!!!");
        System.out.println();
        
        //============================================================================
        // Setting of the work-item sizes at each dimension 
        long global_work_size[] = new long[]{work_item_number};
        long local_work_size[]  = new long[]{work_group_size};
        
        // TEST
        System.out.println("global_work_size = "+global_work_size[0]);
        System.out.println("local_work_size  = "+local_work_size[0]);
        
        // Printing of the number of time steps
        System.out.println();
        System.out.println("Number of time steps: "+step_number);
        System.out.println("===========================================================");
        
        // Переменные для вычисления времени, необходимого для выполнения программы
        Date start_date = new Date();
        Date inter_date = new Date();
        Date inter_date_1 = new Date();

        long time_period_exec = 0;
        long time_period_read = 0;
        
        // Boolean value regulating output data recording
        boolean write_to_file; 
        
        // Поток для записи в файл
        BufferedWriter bw;
        
        // Number of files with output data
        int file_number = FILE_NUMBER;
        
        // Name of file with output data
        String file_name;
        
        // Counter of output files 
        int file_counter = 0;
        
        // String with zeros
        String zeros = "";
        
        // Порядок общего числа временных шагов
        int max_power = (int)Math.floor(Math.log10(1.0*step_number));
        
        // Порядок номера текущего временного шага
        double cur_power;
        
        // Number of cell switches
        int switch_counter = 0;
        
        // Number of cell switches at current time step
        int step_switch_counter = 0;
        
        // Number of cell switches at current period between file recordings
        int period_switch_counter = 0;
        
        // dislocation density of grain
        double disl_density = 0;
        
        // Step counter
        long stepCounter[] = new long[1];
        
        stepCounter[0] = 1;
        
        // Pointer to step counter
        Pointer step_counter_ptr    = Pointer.to(stepCounter);
        
        // Memory object for step counter
        cl_mem memObject_step_counter   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_long, step_counter_ptr, null);
        
        // Next step counter
        long nextStepCounter[] = new long[1];
        nextStepCounter[0] = 1;
        
        // Pointer to next step counter
        Pointer next_step_counter_ptr    = Pointer.to(nextStepCounter);
        
        // Memory object for next step counter
        cl_mem memObject_next_step_counter   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_long, next_step_counter_ptr, null);
        
        // Counter of kernels
        int kernel_counter = -1;
        
        // Counter of command queues
        int com_queue_counter = -1;
        
        if(false)
        try
        {
          FileInputStream in;
          ZipOutputStream out = new ZipOutputStream(new FileOutputStream(specArrays.write_file_name+"_jocl.zip"));
        }
        catch(IOException io_exc)
        {
            System.out.println("IOException: "+io_exc);
        }       
        
        // Number of new grains
        int new_gr_number = 0;
        
        // Current new grain index
        int new_gr_index  = 0;
        
        // Number of twin grains
        int twin_gr_number = 0;
        
        // Current twin grain index
        int twin_gr_index  = 0;
        
        // Old grain index
        int old_gr_index     = -1;
        
        // Old recrystallization type of grain
        byte old_gr_rec_type = -1;
        
        // Number of embryos of grains in twins
        int new_grains_in_twins_number = 0;
        
        // List of new grains in twins
        ArrayList new_grains_in_twins = new ArrayList();
        
        // Array of lists of new grains in twins
        ArrayList new_grains_in_twin[] = new ArrayList[grain_number];
        
        for(int list_counter = 0; list_counter < grain_number; list_counter++)
          new_grains_in_twin[list_counter] = new ArrayList();
        
        // Recrystallization type of current grain
        byte gr_rec_type;
        
        boolean write_init_grains_file = true;  // false; // 
        boolean write_init_clrs_file   = true;  // false; // 
        boolean cycle_loading          = true;  // false; // 
        
        // Counter of cells in cracks
        int crack_counter = 0;
        
        // Counter of cells with new structure
        int new_struct_counter = 0;
        
        // TEST
        boolean testing = true; // false; // 
        
        // array of relative changes of volumes of all cells
        rel_vol_changes = new double[cell_number];
    
        // Recording of output data to the file at zero time step
        int value = 
              writeToFile(0, -1, max_power, grain_indices, disl_energies, 
                          en_types, loc_types, cell_coords, init_temprs, mech_energies, 
                          force_moments, diss_en_influxes, diss_energies, new_gr_number, twin_gr_number,
                          grain_angles_1, grain_angles_2, grain_angles_3, grain_types, 
                          grain_rec_types, root_grain_indices, write_init_grains_file, 
                          crack_values, crack_sum, stresses_X, stresses_Y, stresses_Z, def_density_changes, final_def_densities,
                          angle_velocity_X, angle_velocity_Y, angle_velocity_Z, abs_angle_velocity,
                          torsion_angle_X,  torsion_angle_Y,  torsion_angle_Z,  abs_torsion_angle,
                          rel_diss_energies, rel_diss_en_changes, rel_diss_en_influxes, mod_elast);
        
        // Array of parameter names
        String[] param_name = new String[param_number];
        
        // Buffered writers for parameters from "<...>_jocl.res"
        param_name[0] = "temperatures";
        param_name[1] = "spec_elast_energies";
        param_name[2] = "crack_values";
        param_name[3] = "force_moments_X";
        param_name[4] = "force_moments_Y";
        param_name[5] = "force_moments_Z";
        param_name[6] = "spec_diss_energies";
        param_name[7] = "spec_diss_en_influxes";
        
        // Buffered writers for parameters from "<...>_jocl_stresses.res"
        param_name[8] = "def_densities";
        param_name[9] = "abs_stresses";
        param_name[10] = "def_density_changes";
        param_name[11] = "stresses_X";
        param_name[12] = "stresses_Y";
        param_name[13] = "stresses_Z";
        param_name[14] = "rel_diss_energies";
        param_name[15] = "rel_diss_en_influxes";
        param_name[16] = "rel_diss_en_changes";
        
        // Buffered writers for parameters from "<...>_jocl_torsion.res"
        param_name[17] = "torsion_velocity_X";
        param_name[18] = "torsion_velocity_Y";
        param_name[19] = "torsion_velocity_Z";
        param_name[20] = "torsion_angle_X";
        param_name[21] = "torsion_angle_Y";
        param_name[22] = "torsion_angle_Z";
        param_name[23] = "abs_torsion_velocity";
        param_name[24] = "abs_torsion_angle";
        
        File graphs_dir;
        
        graphs_dir = new File("./user/task_db/"+TASK_NAME+"/graphs");
        graphs_dir.mkdir();
        
        System.out.println("New directory: ./user/task_db/"+TASK_NAME+"/graphs");
        
        // Creation of directories for text files with data for graphs
        for(int param_counter = 0; param_counter < param_number; param_counter++)
        {
          graphs_dir = new File("./user/task_db/"+TASK_NAME+"/graphs/"+param_name[param_counter]);
          graphs_dir.mkdir();
        
          System.out.println("New directory: ./user/task_db/"+TASK_NAME+"/graphs/"+param_name[param_counter]);
        }
        
        // Number of steps in loading period
      //  long load_period_step_number = 1000;// 10000;
        
        // Number of steps in relaxation period
      //  long relax_period_step_number = 1000;// 10000;
        
        // Number of steps in loading-relaxation period
      //  long period_step_number = load_period_step_number + relax_period_step_number;
        
        boolean decrease_time_step = true;
        
        // Execution of the kernel at each time step
        for(long step_count = 1; step_count <= step_number; step_count++)
        {
          if(step_count == 2 & decrease_time_step & false)
          {
            step_count = 1;
            decrease_time_step = false;
          }
          
          // System.gc();
          if(step_count <= 1000 & step_count%100 == 0)
            file_number = (int)step_number/100;
          else
            file_number = FILE_NUMBER;
            
          // Boolean variable determining recording to file:
          // данные записываются в файлы через равные промежутки времени
          // write_to_file = (stepCounter%(step_number/file_number) == 0)|(stepCounter == step_number); 
          write_to_file = (step_count*file_number)%step_number == 0 | step_count == step_number | (step_count <= 1000 & step_count%100 == 0);
          
        //  if(step_count % 10 == 0)
          {
              // Release of memory object with old array of random numbers
              clReleaseMemObject(memObject_rand_nums);

              // Creation of array of random numbers
              for(int rand_counter = 0; rand_counter < work_item_number; rand_counter++)
                rand_nums[rand_counter] = (double)Math.random();
              
              rand_nums_ptr = Pointer.to(rand_nums);
              
              // Memory object for array of random numbers
              memObject_rand_nums      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                  Sizeof.cl_double * work_item_number, rand_nums_ptr, null);
          }
          
        //  System.out.println("Random numbers are created at time step "+step_count);
          //=========================================================================================
          if(cycle_loading)
          {   
              clReleaseMemObject(memObject_step_counter);
                
              stepCounter[0] = step_count;
              
              // Pointer to step counter
              step_counter_ptr    = Pointer.to(stepCounter);
        
              // Memory object for step counter
              memObject_step_counter   = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_long, step_counter_ptr, null);
              
              // Setting of the argument values to the 0th kernel
              clSetKernelArg(kernel_0,  0, Sizeof.cl_mem, Pointer.to(memObject_step_counter));
              clSetKernelArg(kernel_0,  1, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_time_function_types));
              clSetKernelArg(kernel_0,  2, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_load_time_portions));
              clSetKernelArg(kernel_0,  3, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_relax_time_portions));
              clSetKernelArg(kernel_0,  4, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_min_temprs));
              clSetKernelArg(kernel_0,  5, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_max_temprs));
              clSetKernelArg(kernel_0,  6, Sizeof.cl_mem, Pointer.to(memObject_bound_min_mech_influxes));
              clSetKernelArg(kernel_0,  7, Sizeof.cl_mem, Pointer.to(memObject_bound_max_mech_influxes));
              clSetKernelArg(kernel_0,  8, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
              clSetKernelArg(kernel_0,  9, Sizeof.cl_mem, Pointer.to(memObject_bound_mech_influxes));
              
              // Obtaining of current time
              start_date = new Date();
              
              // ID of current kernel
              kernel_counter = 0;
              
              // ID of current command queue
              com_queue_counter = kernel_counter % com_queue_number;
              
              // Execution of the 1st kernel at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_0, 1, null, global_work_size, local_work_size, 0, null, null);
              
              // Obtaining of current time
              inter_date = new Date();
              
              // Calculation of the time period of the 1st kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
              
              // Reading of the output data (temperatures and heat strains) from a buffer object to host memory
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_temprs, CL_TRUE, 0,
                                Sizeof.cl_double * (work_item_number + 1), init_temprs_ptr, 0, null, null);
            
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_mech_influxes, CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, mech_influxes_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_step_counter, CL_TRUE, 0,
                                Sizeof.cl_long, step_counter_ptr, 0, null, null);
              
              // TEST
           //   System.out.println("step_count: "+step_count+"; kernel #0: stepCounter = "+stepCounter[0]+"; init_tempr[312] = "+init_temprs[312]);
          }
            
          if(specArrays.simulate_heat_flows == 1)
          {
            // Setting of the argument values to the 1st kernel
            clSetKernelArg(kernel_1,  0, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
            clSetKernelArg(kernel_1,  1, Sizeof.cl_mem, Pointer.to(memObject_heat_caps));
            clSetKernelArg(kernel_1,  2, Sizeof.cl_mem, Pointer.to(memObject_heat_conds));
            clSetKernelArg(kernel_1,  3, Sizeof.cl_mem, Pointer.to(memObject_densities));
        
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_1,  neighb_counter+4, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
            clSetKernelArg(kernel_1, neighb1S_number+ 4, Sizeof.cl_mem, Pointer.to(memObject_en_types));
            clSetKernelArg(kernel_1, neighb1S_number+ 5, Sizeof.cl_mem, Pointer.to(memObject_temperatures));
            clSetKernelArg(kernel_1, neighb1S_number+ 6, Sizeof.cl_mem, Pointer.to(memObject_heat_influxes));
            clSetKernelArg(kernel_1, neighb1S_number+ 7, Sizeof.cl_mem, Pointer.to(memObject_neighb_id));
            clSetKernelArg(kernel_1, neighb1S_number+ 8, Sizeof.cl_mem, Pointer.to(memObject_factor));
            clSetKernelArg(kernel_1, neighb1S_number+ 9, Sizeof.cl_mem, Pointer.to(memObject_step_counter));
            clSetKernelArg(kernel_1, neighb1S_number+10, Sizeof.cl_mem, Pointer.to(memObject_heat_exp_coeffs));
            clSetKernelArg(kernel_1, neighb1S_number+11, Sizeof.cl_mem, Pointer.to(memObject_grain_indices));
            clSetKernelArg(kernel_1, neighb1S_number+12, Sizeof.cl_mem, Pointer.to(memObject_init_heat_strains));
            clSetKernelArg(kernel_1, neighb1S_number+13, Sizeof.cl_mem, Pointer.to(memObject_heat_strains));
            clSetKernelArg(kernel_1, neighb1S_number+14, Sizeof.cl_mem, Pointer.to(memObject_phonon_portions));
            clSetKernelArg(kernel_1, neighb1S_number+15, Sizeof.cl_mem, Pointer.to(memObject_grain_angles_1));
            
            // ID of current kernel
            kernel_counter = 0;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // TEST
          //  System.out.println();
          //  System.out.println("Kernel 1. Cell #625. init_tempr = "+init_temprs[625]+"; init_heat_strain = "+init_heat_strains[625]);
            
            // Execution of the 1st kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_1, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Calculation of the time period of the 1st kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Reading of the output data (temperatures and heat strains) from a buffer object to host memory.
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_temperatures, CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, temperatures_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_heat_strains, CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, heat_strains_ptr, 0, null, null);
            
            if(write_to_file)
            {
              // Reading of the output data (heat influxes) from a buffer object to host memory.
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_heat_influxes, CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, heat_influxes_ptr, 0, null, null);
              
              // Reading of the output data (heat expansion coefficients) from a buffer object to host memory.
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_heat_exp_coeffs, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, heat_exp_coeffs_ptr, 0, null, null);
            }
            
            // TEST
          //  System.out.println("Kernel 1. Cell #625. curr_tempr = "+temperatures[625]+"; curr_heat_strain = "+heat_strains[625]);
          //  System.out.println();
            
            // Obtaining of current time
            inter_date_1 = new Date();
                
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            
            //=========================================================================================
            // Setting of the argument values to the 2nd kernel.
            clSetKernelArg(kernel_2,  0, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
            clSetKernelArg(kernel_2,  1, Sizeof.cl_mem, Pointer.to(memObject_temperatures));
            clSetKernelArg(kernel_2,  2, Sizeof.cl_mem, Pointer.to(memObject_init_heat_strains));
            clSetKernelArg(kernel_2,  3, Sizeof.cl_mem, Pointer.to(memObject_heat_strains));
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 2nd kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_2, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Calculation of the time period of the 2nd kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Reading of the output data (initial temperatures and heat strains for next time step) from a buffer object to host memory.
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_temprs, CL_TRUE, 0,
                            Sizeof.cl_double * (work_item_number+1), init_temprs_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_heat_strains, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, init_heat_strains_ptr, 0, null, null);
            
            // TEST
         //   System.out.println();
           // System.out.println("Kernel 2. Cell #625. init_tempr = "+init_temprs[625]+"; init_heat_strain = "+init_heat_strains[625]);
            
            // Obtaining of current time
            inter_date_1 = new Date();
                
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
          }
          
          //=========================================================================================
          if(specArrays.simulate_mech_flows == 1)
          {
              // Setting of the argument values to the 5th kernel.
              clSetKernelArg(kernel_5, 0, Sizeof.cl_mem, Pointer.to(memObject_maxBoundEnergy));
              clSetKernelArg(kernel_5, 1, Sizeof.cl_mem, Pointer.to(memObject_max_grain_angles));
              clSetKernelArg(kernel_5, 2, Sizeof.cl_mem, Pointer.to(memObject_grain_angles_1));
              clSetKernelArg(kernel_5, 3, Sizeof.cl_mem, Pointer.to(memObject_grain_angles_2));
              clSetKernelArg(kernel_5, 4, Sizeof.cl_mem, Pointer.to(memObject_grain_angles_3));
              
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                clSetKernelArg(kernel_5, neighb_counter+5, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
              
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                clSetKernelArg(kernel_5, neighb1S_number+5+neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_spec_angle_diff[neighb_counter]));
               
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                clSetKernelArg(kernel_5, 2*neighb1S_number+5+neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_spec_bound_energies[neighb_counter]));
              
              clSetKernelArg(kernel_5, 3*neighb1S_number+ 5, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_5, 3*neighb1S_number+ 6, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
              
              // ID of current kernel
              kernel_counter++;
            
              // ID of current command queue
              com_queue_counter = kernel_counter % com_queue_number;
              
              // Obtaining of current time
              start_date = new Date();
              
              // Execution of the 5th kernel at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_5, 1, null, global_work_size, local_work_size, 0, null, null);
              
              // Obtaining of current time
              inter_date = new Date();
              
              // Calculation of the time period of the 5th kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
              
              // Reading of the output data (specific mechanical energies, mech. energy influxes, initial temperatures 
              // and grain orientation angles) from a buffer object to host memory.
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_temprs, CL_TRUE, 0,
                                  Sizeof.cl_double * (work_item_number+1), init_temprs_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_grain_angles_1, CL_TRUE, 0,
                                  Sizeof.cl_double * grain_number, grain_angles_1_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_grain_angles_2, CL_TRUE, 0,
                                  Sizeof.cl_double * grain_number, grain_angles_2_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_grain_angles_3, CL_TRUE, 0,
                                  Sizeof.cl_double * grain_number, grain_angles_3_ptr, 0, null, null);
              
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                  clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_spec_bound_energies[neighb_counter], CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, spec_bound_energies_ptr[neighb_counter], 0, null, null);
              
              if(write_to_file)
              {
                // Reading of the output data (specific misorientation angles) from a buffer object to host memory.
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_spec_angle_diff[0], CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, spec_angle_diff_ptr[0], 0, null, null);
              }
              
              // Obtaining of current time
              inter_date_1 = new Date();
              
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
              
              //=========================================================================================
              // Setting of the argument values to the kernel # 5.1
              clSetKernelArg(kernel_5_1, 0, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
              clSetKernelArg(kernel_5_1, 1, Sizeof.cl_mem, Pointer.to(memObject_mod_elast));
              
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                clSetKernelArg(kernel_5_1, neighb_counter + 2, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
              
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                clSetKernelArg(kernel_5_1, neighb1S_number +2+neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_spec_bound_energies[neighb_counter]));
              
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 2, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 3, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 4, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 5, Sizeof.cl_mem, Pointer.to(memObject_mech_influxes));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 6, Sizeof.cl_mem, Pointer.to(memObject_mech_energies));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 7, Sizeof.cl_mem, Pointer.to(memObject_step_counter));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 8, Sizeof.cl_mem, Pointer.to(memObject_next_step_counter));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+ 9, Sizeof.cl_mem, Pointer.to(memObject_bound_mech_influxes));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+10, Sizeof.cl_mem, Pointer.to(memObject_heat_strains));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+11, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_time_function_types));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+12, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_load_time_portions));
              clSetKernelArg(kernel_5_1, 2*neighb1S_number+13, Sizeof.cl_mem, Pointer.to(memObject_bound_cell_relax_time_portions));
            //  clSetKernelArg(kernel_5_1, 2*neighb1S_number+14, Sizeof.cl_mem, Pointer.to(memObject_loc_types));
            //  clSetKernelArg(kernel_5_1, 2*neighb1S_number+15, Sizeof.cl_mem, Pointer.to(memObject_new_mod_elast));
            //  clSetKernelArg(kernel_5_1, 2*neighb1S_number+16, Sizeof.cl_mem, Pointer.to(memObject_cell_mod_elast));
              
              // Obtaining of current time
              start_date = new Date();
              
              // TEST
            //  System.out.println();
            //  System.out.println("Kernel 5.1. Cell #625. tempr = "+init_temprs[625]+"; heat_strain = "+heat_strains[625]+"; init_mech_energy = "+init_mech_energies[625]);
              
              // Execution of the kernel 5.1 at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_5_1, 1, null, global_work_size, local_work_size, 0, null, null);
              
              // Obtaining of current time
              inter_date = new Date();
              
              // Calculation of the time period of the 5th kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
              
               // Reading of the output data (specific mechanical energies, mech. energy influxes, initial temperatures 
              // and grain orientation angles) from a buffer object to host memory.              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_mech_influxes, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, mech_influxes_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_mech_energies, CL_TRUE, 0,
                            Sizeof.cl_double * (work_item_number+1), mech_energies_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_step_counter, CL_TRUE, 0,
                            Sizeof.cl_long, step_counter_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_next_step_counter, CL_TRUE, 0,
                            Sizeof.cl_long, next_step_counter_ptr, 0, null, null);
              
             // System.out.println("step_count: "+step_count+"; kernel #5_1: stepCounter = "+stepCounter[0]+"; nextStepCounter = "+nextStepCounter[0]);
              
              if(write_to_file)
              {
                // Reading of the output data (specific boundary energies at boundaries with neighbours) from a buffer object to host memory.
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_spec_bound_energies[0], CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, spec_bound_energies_ptr[0], 0, null, null);
                
               // clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_cell_mod_elast, CL_TRUE, 0,
               //             Sizeof.cl_double * work_item_number, cell_mod_elast_ptr, 0, null, null);
              }
              
              // TEST
            //  System.out.println("Kernel 5.1. Cell #625. mech_energy = "+mech_energies[625]);
            //  System.out.println();
              
              // Obtaining of current time
              inter_date_1 = new Date();
              
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
              
              //=========================================================================================
              // Setting of the argument values to the kernel # 5.2
              for(int bound_counter = 0; bound_counter < neighb1S_number; bound_counter++)
                clSetKernelArg(kernel_5_2, bound_counter, Sizeof.cl_mem, Pointer.to(memObject_stresses[bound_counter]));
              
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                clSetKernelArg(kernel_5_2, neighb1S_number + neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
              
              clSetKernelArg(kernel_5_2, 2*neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_5_2, 2*neighb1S_number + 1, Sizeof.cl_mem, Pointer.to(memObject_mech_energies));
              
              // Obtaining of current time
              start_date = new Date();
              
              // Execution of the kernel 5.2 at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_5_2, 1, null, global_work_size, local_work_size, 0, null, null);
              
              // Obtaining of current time
              inter_date = new Date();
              
              // Calculation of the time period of the 5th kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
              
              // Reading of the values of scalar stresses at cell boundaries
              for (int bound_counter = 0; bound_counter < neighb1S_number; bound_counter++)
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_stresses[bound_counter], CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, stresses_ptr[bound_counter], 0, null, null);
              
              // Obtaining of current time
              inter_date_1 = new Date();
              
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
              //=========================================================================================
              // Setting of the argument values to the 6th kernel.
              clSetKernelArg(kernel_6, 0, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
              clSetKernelArg(kernel_6, 1, Sizeof.cl_mem, Pointer.to(memObject_mech_energies));
              clSetKernelArg(kernel_6, 2, Sizeof.cl_mem, Pointer.to(memObject_growth_vectors_coordinates));
              
              for(int bound_counter = 0; bound_counter < neighb1S_number; bound_counter++)
                clSetKernelArg(kernel_6, 3 + bound_counter, Sizeof.cl_mem, Pointer.to(memObject_stresses[bound_counter]));
              
              clSetKernelArg(kernel_6, neighb1S_number + 3, Sizeof.cl_mem, Pointer.to(memObject_stresses_X));
              clSetKernelArg(kernel_6, neighb1S_number + 4, Sizeof.cl_mem, Pointer.to(memObject_stresses_Y));
              clSetKernelArg(kernel_6, neighb1S_number + 5, Sizeof.cl_mem, Pointer.to(memObject_stresses_Z));
              
          //    System.out.print("step_count     = "+step_count+"; stepCounter[0] = "+stepCounter[0]);
              
              // Obtaining of current time
              start_date = new Date();
            
              // Execution of the 6th kernel at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_6, 1, null, global_work_size, local_work_size, 0, null, null);
            
              // Obtaining of current time
              inter_date = new Date();
            
              // Calculation of the time period of the 6th kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
            
              // Reading of the output data (initial mechanical energies) from a buffer object to host memory.
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_mech_energies, CL_TRUE, 0,
                            Sizeof.cl_double * (work_item_number+1), init_mech_energies_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_stresses_X, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, stresses_X_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_stresses_Y, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, stresses_Y_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_stresses_Z, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, stresses_Z_ptr, 0, null, null);
              
           //   System.out.println("; nextStepCounter[0] = "+nextStepCounter[0]);
              
              // Obtaining of current time
              inter_date_1 = new Date();
                
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();   
              
              // TEST
              if(write_to_file & testing)
              {
                System.out.println("\nStep # "+step_count);
                System.out.println("Cell # 1279. Stress = "+mech_energies[1279]+"; Stress vector = ("+stresses_X[1279]+" "+stresses_Y[1279]+" "+stresses_Z[1279]+").");
                System.out.println("cell1S_index | cell1S_spec_mech_en | cell1S_stress |     cell1S_vector  ");
                  
                for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
             //   if(stress[neighb_counter][1279] != 0)
                  System.out.println(neighbours1S[neighb_counter][1279]+"      | "+mech_energies[neighbours1S[neighb_counter][1279]]+" | "+stress[neighb_counter][1279]+" | "+
                          growth_vectors_coordinates[3*neighb_counter]+" "+growth_vectors_coordinates[3*neighb_counter+1]+" "+growth_vectors_coordinates[3*neighb_counter+2]+" | ");
                      
                System.out.println();
              }
          }
          
          //=========================================================================================
          if(specArrays.calc_force_moments == 1)
          {
            if(!new_moments)
            {
              // Setting of the argument values to the 7th kernel
              clSetKernelArg(kernel_7, 0, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
              clSetKernelArg(kernel_7, 1, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_7, 2, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_X));
              clSetKernelArg(kernel_7, 3, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Y));
              clSetKernelArg(kernel_7, 4, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Z));
            
              for(int mem_obj_counter = 0; mem_obj_counter < 4*neighb1S_number; mem_obj_counter++)
                clSetKernelArg(kernel_7, mem_obj_counter+5, Sizeof.cl_mem, Pointer.to(memObject_cell_pairs1S[mem_obj_counter]));
            
              clSetKernelArg(kernel_7, 53, Sizeof.cl_mem, Pointer.to(memObject_force_moments_X));
            
              // ID of current kernel
              kernel_counter++;
            
              // ID of current command queue
              com_queue_counter = kernel_counter % com_queue_number;
              
              // Obtaining of current time
              start_date = new Date();
            
              // Execution of the 7th kernel at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_7, 1, null, global_work_size, local_work_size, 0, null, null);
            
              // Obtaining of current time
              inter_date = new Date();
            
              // Reading of the output data from a buffer object to host memory
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_force_moments_X, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, force_moments_X_ptr, 0, null, null);
            
              // Obtaining of current time
              inter_date_1 = new Date();
            
              // Calculation of the time period of the 7th kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
            
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
              //=========================================================================================
              // Setting of the argument values to the kernel # 7.1
              clSetKernelArg(kernel_7_1, 0, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
              clSetKernelArg(kernel_7_1, 1, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_7_1, 2, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_X));
              clSetKernelArg(kernel_7_1, 3, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Y));
              clSetKernelArg(kernel_7_1, 4, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Z));
            
              for(int mem_obj_counter = 0; mem_obj_counter < 4*neighb1S_number; mem_obj_counter++)
                clSetKernelArg(kernel_7_1, mem_obj_counter+5, Sizeof.cl_mem, Pointer.to(memObject_cell_pairs1S[mem_obj_counter]));
            
              clSetKernelArg(kernel_7_1, 53, Sizeof.cl_mem, Pointer.to(memObject_force_moments_Y));
            
              // ID of current kernel
           //   kernel_counter++;
            
              // ID of current command queue
              com_queue_counter = kernel_counter % com_queue_number;
            
              // Obtaining of current time
              start_date = new Date();
            
              // Execution of the kernel 7.1 at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_7_1, 1, null, global_work_size, local_work_size, 0, null, null);
            
              // Obtaining of current time
              inter_date = new Date();
            
              // Reading of the output data from a buffer object to host memory
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_force_moments_Y, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, force_moments_Y_ptr, 0, null, null);
            
              // Obtaining of current time
              inter_date_1 = new Date();
            
              // Calculation of the time period of the kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
            
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
            
              //=========================================================================================
              // Setting of the argument values to the kernel # 7.2
              clSetKernelArg(kernel_7_2, 0, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
              clSetKernelArg(kernel_7_2, 1, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_7_2, 2, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_X));
              clSetKernelArg(kernel_7_2, 3, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Y));
              clSetKernelArg(kernel_7_2, 4, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Z));
            
              for(int mem_obj_counter = 0; mem_obj_counter < 4*neighb1S_number; mem_obj_counter++)
                clSetKernelArg(kernel_7_2, mem_obj_counter+5, Sizeof.cl_mem, Pointer.to(memObject_cell_pairs1S[mem_obj_counter]));
            
              clSetKernelArg(kernel_7_2, 53, Sizeof.cl_mem, Pointer.to(memObject_force_moments_Z));
            
              // ID of current kernel
           //   kernel_counter++;
            
              // ID of current command queue
              com_queue_counter = kernel_counter % com_queue_number;
            
              // Obtaining of current time
              start_date = new Date();
            
              // Execution of the kernel 7.2 at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_7_2, 1, null, global_work_size, local_work_size, 0, null, null);
            
         //   System.out.println("Kernels 1 - 7.2 are executed at time step "+step_count);
            
              // Obtaining of current time
              inter_date = new Date();
            
              // Reading of the output data from a buffer object to host memory
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_force_moments_Z, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, force_moments_Z_ptr, 0, null, null);
            
              // Obtaining of current time
              inter_date_1 = new Date();
            
              // Calculation of the time period of the kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
            
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
            }
            else
            {
              // Setting of the argument values to the kernel # 7_mom
              clSetKernelArg(kernel_7_mom, 0, Sizeof.cl_mem, Pointer.to(memObject_stresses_X));
              clSetKernelArg(kernel_7_mom, 1, Sizeof.cl_mem, Pointer.to(memObject_stresses_Y));
              clSetKernelArg(kernel_7_mom, 2, Sizeof.cl_mem, Pointer.to(memObject_stresses_Z));
              clSetKernelArg(kernel_7_mom, 3, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_X));
              clSetKernelArg(kernel_7_mom, 4, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Y));
              clSetKernelArg(kernel_7_mom, 5, Sizeof.cl_mem, Pointer.to(memObject_cell_coords_Z));
            
              for(int mem_obj_counter = 0; mem_obj_counter < neighb1S_number; mem_obj_counter++)
                clSetKernelArg(kernel_7_mom, mem_obj_counter+6, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[mem_obj_counter]));
            
              clSetKernelArg(kernel_7_mom, neighb1S_number+6, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_7_mom, neighb1S_number+7, Sizeof.cl_mem, Pointer.to(memObject_force_moments_X));
              clSetKernelArg(kernel_7_mom, neighb1S_number+8, Sizeof.cl_mem, Pointer.to(memObject_force_moments_Y));
              clSetKernelArg(kernel_7_mom, neighb1S_number+9, Sizeof.cl_mem, Pointer.to(memObject_force_moments_Z));
            
              // ID of current kernel
           // kernel_counter++;
            
              // ID of current command queue
              com_queue_counter = kernel_counter % com_queue_number;
            
              // Obtaining of current time
              start_date = new Date();
            
              // Execution of the kernel 7.2 at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_7_mom, 1, null, global_work_size, local_work_size, 0, null, null);
            
         //   System.out.println("Kernels 1 - 7.2 are executed at time step "+step_count);
              
              // Obtaining of current time
              inter_date = new Date();
            
              // Reading of the output data from a buffer object to host memory
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_force_moments_X, CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, force_moments_X_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_force_moments_Y, CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, force_moments_Y_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_force_moments_Z, CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, force_moments_Z_ptr, 0, null, null);
            
              // Obtaining of current time
              inter_date_1 = new Date();
            
              // Calculation of the time period of the kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
            
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
              
              // Calculation of force moments with the help of non-parallel method
              for(int cell_index = 0; cell_index < cell_number; cell_index++)
                summary_force_moment[cell_index] = calcSummaryForceMoment(cell_index, en_types, stresses_X, stresses_Y, stresses_Z);
              
              if(write_to_file)
              {
                // TEST
                int neighb_index;
              
                double coord_X, coord_Y, coord_Z;
              
                for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                {
                  neighb_index = neighbours1S[neighb_counter][1279];
                
                  coord_X = cell_coords[0][neighb_index] - cell_coords[0][1279];
                  coord_Y = cell_coords[1][neighb_index] - cell_coords[1][1279];
                  coord_Z = cell_coords[2][neighb_index] - cell_coords[2][1279];
                  
                  if(testing)
                  {
               //   if(stresses_X[neighb_index] != 0 & stresses_Y[neighb_index] != 0 & stresses_Z[neighb_index] != 0)
                    System.out.println("Step # "+step_count+". Cell # 1279. Neighb.cell # "+neighb_index+
                                       "; coord.= ("+coord_X+" "+coord_Y+" "+coord_Z+
                                       "); stress= ("+stresses_X[neighb_index]+" "+stresses_Y[neighb_index]+" "+stresses_Z[neighb_index]+").");
                  }
                }
                if(testing)
                {
                  System.out.println();
                  System.out.println("Parallel version: summary_force_moment = ("+force_moments[0][1279]+" "+force_moments[1][1279]+" "+force_moments[2][1279]+").");
                  System.out.println("Non-par. version: summary_force_moment = ("+summary_force_moment[1279].getX()+" "+summary_force_moment[1279].getY()+" "+summary_force_moment[1279].getZ()+").\n");
                }
              }
              
              // Variable for calculation ofdifference between values of force moments calculated by parallel and non-parallel methods
              double delta_mom = 0;
              
              // Redetermination of force moments
              for(int cell_index = 0; cell_index < cell_number; cell_index++)
              {
               // if(false)
                if(en_types[cell_index] != INNER_CELL & en_types[cell_index] != LAYER_CELL)
                {
                //  force_moments[0][cell_index] = 0.0;
                //  force_moments[1][cell_index] = 0.0;
                //  force_moments[2][cell_index] = 0.0;
                  
                  summary_force_moment[cell_index].setX(0.0);
                  summary_force_moment[cell_index].setY(0.0);
                  summary_force_moment[cell_index].setZ(0.0);
                }
                  
                delta_mom += (force_moments[0][cell_index] - summary_force_moment[cell_index].getX())*(force_moments[0][cell_index] - summary_force_moment[cell_index].getX()) + 
                             (force_moments[1][cell_index] - summary_force_moment[cell_index].getY())*(force_moments[1][cell_index] - summary_force_moment[cell_index].getY()) + 
                             (force_moments[2][cell_index] - summary_force_moment[cell_index].getZ())*(force_moments[2][cell_index] - summary_force_moment[cell_index].getZ());
                
                force_moments[0][cell_index] = summary_force_moment[cell_index].getX();
                force_moments[1][cell_index] = summary_force_moment[cell_index].getY();
                force_moments[2][cell_index] = summary_force_moment[cell_index].getZ();
              }
              
              // Pointers to 3 arrays of coordinates X, Y, Z of force moments of cells
              force_moments_X_ptr  = Pointer.to(force_moments[0]);
              force_moments_Y_ptr  = Pointer.to(force_moments[1]);
              force_moments_Z_ptr  = Pointer.to(force_moments[2]);
              
              if(write_to_file)
                System.out.println("delta_mom = "+delta_mom+"\n");
            }
            
            //=========================================================================================
            double init_mech_energy = init_mech_energies[1279];
            
            // Setting of the argument values to the 8th kernel
            clSetKernelArg(kernel_8, 0, Sizeof.cl_mem, Pointer.to(memObject_en_types));
            clSetKernelArg(kernel_8, 1, Sizeof.cl_mem, Pointer.to(memObject_grain_indices));
            clSetKernelArg(kernel_8, 2, Sizeof.cl_mem, Pointer.to(memObject_mod_shear));
            clSetKernelArg(kernel_8, 3, Sizeof.cl_mem, Pointer.to(memObject_force_moments_X));
            clSetKernelArg(kernel_8, 4, Sizeof.cl_mem, Pointer.to(memObject_force_moments_Y));
            clSetKernelArg(kernel_8, 5, Sizeof.cl_mem, Pointer.to(memObject_force_moments_Z));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_8, neighb_counter+6, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
            clSetKernelArg(kernel_8, neighb1S_number + 6, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
            clSetKernelArg(kernel_8, neighb1S_number + 7, Sizeof.cl_mem, Pointer.to(memObject_mech_energies));
            clSetKernelArg(kernel_8, neighb1S_number + 8, Sizeof.cl_mem, Pointer.to(memObject_diss_en_influxes));
            clSetKernelArg(kernel_8, neighb1S_number + 9, Sizeof.cl_mem, Pointer.to(memObject_loc_types));
            clSetKernelArg(kernel_8, neighb1S_number +10, Sizeof.cl_mem, Pointer.to(memObject_abs_mom));
            clSetKernelArg(kernel_8, neighb1S_number +11, Sizeof.cl_mem, Pointer.to(memObject_crack_values));
            clSetKernelArg(kernel_8, neighb1S_number +12, Sizeof.cl_mem, Pointer.to(memObject_lattice_parameters));
            clSetKernelArg(kernel_8, neighb1S_number +13, Sizeof.cl_mem, Pointer.to(memObject_def_density_changes));
            clSetKernelArg(kernel_8, neighb1S_number +14, Sizeof.cl_mem, Pointer.to(memObject_coeff_diss));
            clSetKernelArg(kernel_8, neighb1S_number +15, Sizeof.cl_mem, Pointer.to(memObject_angle_velocity_X));
            clSetKernelArg(kernel_8, neighb1S_number +16, Sizeof.cl_mem, Pointer.to(memObject_angle_velocity_Y));
            clSetKernelArg(kernel_8, neighb1S_number +17, Sizeof.cl_mem, Pointer.to(memObject_angle_velocity_Z));
            clSetKernelArg(kernel_8, neighb1S_number +18, Sizeof.cl_mem, Pointer.to(memObject_abs_angle_velocity));
            clSetKernelArg(kernel_8, neighb1S_number +19, Sizeof.cl_mem, Pointer.to(memObject_torsion_angle_X));
            clSetKernelArg(kernel_8, neighb1S_number +20, Sizeof.cl_mem, Pointer.to(memObject_torsion_angle_Y));
            clSetKernelArg(kernel_8, neighb1S_number +21, Sizeof.cl_mem, Pointer.to(memObject_torsion_angle_Z));
            clSetKernelArg(kernel_8, neighb1S_number +22, Sizeof.cl_mem, Pointer.to(memObject_abs_torsion_angle));
            
            clSetKernelArg(kernel_8, neighb1S_number +23, Sizeof.cl_mem, Pointer.to(memObject_mech_influxes));
            clSetKernelArg(kernel_8, neighb1S_number +24, Sizeof.cl_mem, Pointer.to(memObject_rel_diss_en_influxes));
            
         //   System.out.println("Variables for kernel 8 are set at time step "+step_count);
            // ID of current kernel
          //  kernel_counter++;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 8th kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_8, 1, null, global_work_size, local_work_size, 0, null, null);
            
        //    System.out.println("Kernel 8 is executed at time step "+step_count);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Reading of the output data from a buffer object to host memory
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_mech_energies, CL_TRUE, 0,
                            Sizeof.cl_double * (work_item_number+1), mech_energies_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_diss_en_influxes, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, diss_en_influxes_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_en_types, CL_TRUE, 0,
                            Sizeof.cl_char * (work_item_number+1), en_types_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_loc_types, CL_TRUE, 0,
                            Sizeof.cl_char * work_item_number, loc_types_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_crack_values, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, crack_values_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_def_density_changes, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, def_density_changes_ptr, 0, null, null);
            
            if(write_to_file)
            {
              // Reading of the output data from a buffer object to host memory
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_abs_mom, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, abs_mom_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_angle_velocity_X, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, angle_velocity_X_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_angle_velocity_Y, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, angle_velocity_Y_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_angle_velocity_Z, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, angle_velocity_Z_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_abs_angle_velocity, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, abs_angle_velocity_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_torsion_angle_X, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, torsion_angle_X_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_torsion_angle_Y, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, torsion_angle_Y_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_torsion_angle_Z, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, torsion_angle_Z_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_abs_torsion_angle, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, abs_torsion_angle_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_coeff_diss, CL_TRUE, 0,
                              Sizeof.cl_double * grain_number, coeff_diss_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_rel_diss_en_influxes, CL_TRUE, 0,
                              Sizeof.cl_double * work_item_number, rel_diss_en_influxes_ptr, 0, null, null);
            }
            
            // Obtaining of current time
            inter_date_1 = new Date();
            
            // Calculation of the time period of the 8th kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            
            //===========================================================================
            // Вычисление значений диссипационной энергии и её текущих притоков на основе значений моментов сил каждого элемента
            clSetKernelArg(kernel_8_1, 0, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
            clSetKernelArg(kernel_8_1, 1, Sizeof.cl_mem, Pointer.to(memObject_mech_energies));
            clSetKernelArg(kernel_8_1, 2, Sizeof.cl_mem, Pointer.to(memObject_init_diss_energies));
            clSetKernelArg(kernel_8_1, 3, Sizeof.cl_mem, Pointer.to(memObject_diss_en_influxes));
            clSetKernelArg(kernel_8_1, 4, Sizeof.cl_mem, Pointer.to(memObject_diss_energies));
            clSetKernelArg(kernel_8_1, 5, Sizeof.cl_mem, Pointer.to(memObject_crack_values));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_8_1, neighb_counter+6, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
            clSetKernelArg(kernel_8_1, neighb1S_number+ 6, Sizeof.cl_mem, Pointer.to(memObject_loc_types));
            clSetKernelArg(kernel_8_1, neighb1S_number+ 7, Sizeof.cl_mem, Pointer.to(memObject_en_types));
            clSetKernelArg(kernel_8_1, neighb1S_number+ 8, Sizeof.cl_mem, Pointer.to(memObject_new_loc_types));
            clSetKernelArg(kernel_8_1, neighb1S_number+ 9, Sizeof.cl_mem, Pointer.to(memObject_new_en_types));
            clSetKernelArg(kernel_8_1, neighb1S_number+10, Sizeof.cl_mem, Pointer.to(memObject_rand_nums));
            clSetKernelArg(kernel_8_1, neighb1S_number+11, Sizeof.cl_mem, Pointer.to(memObject_simulate_cracks));
            clSetKernelArg(kernel_8_1, neighb1S_number+12, Sizeof.cl_mem, Pointer.to(memObject_init_def_densities));
            clSetKernelArg(kernel_8_1, neighb1S_number+13, Sizeof.cl_mem, Pointer.to(memObject_final_def_densities));
            clSetKernelArg(kernel_8_1, neighb1S_number+14, Sizeof.cl_mem, Pointer.to(memObject_def_density_changes));
            clSetKernelArg(kernel_8_1, neighb1S_number+15, Sizeof.cl_mem, Pointer.to(memObject_rel_diss_energies));
            clSetKernelArg(kernel_8_1, neighb1S_number+16, Sizeof.cl_mem, Pointer.to(memObject_rel_diss_en_changes));
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the kernel # 8.1 at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_8_1, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_diss_en_influxes, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number,  diss_en_influxes_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_diss_energies, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number,  diss_energies_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_new_loc_types, CL_TRUE, 0,
                            Sizeof.cl_char * work_item_number,   new_loc_types_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_new_en_types, CL_TRUE, 0,
                            Sizeof.cl_char*(work_item_number+1), new_en_types_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_mech_energies, CL_TRUE, 0,
                            Sizeof.cl_double * (work_item_number+1), init_mech_energies_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_final_def_densities, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, final_def_densities_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_crack_values, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, crack_values_ptr, 0, null, null);
            
            if(write_to_file)
            {
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_simulate_cracks, CL_TRUE, 0,
                            Sizeof.cl_char, simulate_cracks_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_rel_diss_energies, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, rel_diss_energies_ptr, 0, null, null);
              
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_rel_diss_en_changes, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, rel_diss_en_changes_ptr, 0, null, null);
              
              System.out.println("Crack simulation:"+simulate_cracks[0]);
            }
            
            // Obtaining of current time
            inter_date_1 = new Date();
            
            // Calculation of the time period of kernel # 8.1 execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            
            if(testing & write_to_file)
            {
                int gr_index = grain_indices[1279] - 1;
                  
                System.out.println("=================================");
                System.out.println("Step # "+step_count+". Cell # 1279. Mom^2 = "+abs_mom[1279]);
                System.out.println("coeff_diss = "+coeff_diss[gr_index]+"; mod_shear = "+mod_shear[gr_index]+"; cell_volume = "+cell_volume);
                System.out.println("tors_energy_change = "+diss_en_influxes[1279]+"; tors_energy = "+diss_energies[1279]);
                System.out.println("init_mech_energy = "+init_mech_energy+"; mech_energy = "+mech_energies[1279]);
                System.out.println("=================================");
            }
            
            //===========================================================================
            // Setting of the argument values to the kernel # 8.2 
            // (distribution of energy of each cracked cell among its neighbours at 1st coordination sphere)
            clSetKernelArg(kernel_8_2, 0, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
            clSetKernelArg(kernel_8_2, 1, Sizeof.cl_mem, Pointer.to(memObject_loc_types));
            clSetKernelArg(kernel_8_2, 2, Sizeof.cl_mem, Pointer.to(memObject_en_types));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_8_2, neighb_counter+3, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
            clSetKernelArg(kernel_8_2, neighb1S_number+3, Sizeof.cl_mem, Pointer.to(memObject_mech_energies));
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the kernel # 8.2 at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_8_2, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_mech_energies, CL_TRUE, 0,
                            Sizeof.cl_double * (work_item_number+1), mech_energies_ptr, 0, null, null);
            
            // Obtaining of current time
            inter_date_1 = new Date();
            
            // Calculation of the time period of the 9th kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            
            //===========================================================================
            // Setting of the argument values to the 9th kernel (Определение новых значений механической энергии)
            clSetKernelArg(kernel_9, 0, Sizeof.cl_mem, Pointer.to(memObject_mech_energies));
            clSetKernelArg(kernel_9, 1, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));            
            clSetKernelArg(kernel_9, 2, Sizeof.cl_mem, Pointer.to(memObject_diss_energies));
            clSetKernelArg(kernel_9, 3, Sizeof.cl_mem, Pointer.to(memObject_init_diss_energies));
            clSetKernelArg(kernel_9, 4, Sizeof.cl_mem, Pointer.to(memObject_new_en_types));
            clSetKernelArg(kernel_9, 5, Sizeof.cl_mem, Pointer.to(memObject_en_types));
            clSetKernelArg(kernel_9, 6, Sizeof.cl_mem, Pointer.to(memObject_new_loc_types));
            clSetKernelArg(kernel_9, 7, Sizeof.cl_mem, Pointer.to(memObject_loc_types));
            
            // ID of current kernel
         //   kernel_counter++;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 9th kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_9, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_mech_energies, CL_TRUE, 0,
                            Sizeof.cl_double * (work_item_number+1), init_mech_energies_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_diss_energies, CL_TRUE, 0,
                            Sizeof.cl_double * work_item_number, init_diss_energies_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_en_types, CL_TRUE, 0,
                            Sizeof.cl_char * (work_item_number+1), en_types_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_loc_types, CL_TRUE, 0,
                            Sizeof.cl_char * work_item_number, loc_types_ptr, 0, null, null);
            
            // TEST
            if(write_to_file)
            {
              crack_counter = 0;
              new_struct_counter = 0;
                
              for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
              {
                if(loc_types[cell_counter] == CRACK_CELL)
                {
                  crack_counter++;
                  
                //  System.out.println("Cell # "+cell_counter+" is cracked!!!"); // "Old state: "+en_types[cell_counter]+"; new state: "+new_en_types[cell_counter]);
                }
                
                if(loc_types[cell_counter] == TRANSFORMED_CELL)
                {
                  new_struct_counter++;
                  
                //  System.out.println("Cell # "+cell_counter+" is transformed!!!");
                }                
              }
              
              System.out.println("Number of cells with new structure at time step # "+step_count+": "+new_struct_counter);
              System.out.println("Number of cells in cracks at time step # "+step_count+": "+crack_counter);
            }
            
            // Obtaining of current time
            inter_date_1 = new Date();
            
            // Calculation of the time period of the 9th kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
          }
          
          //=========================================================================================
          if(specArrays.simulate_recryst == 1)
          {
            // if(false)
            {
              // Setting of the argument values to the A-th kernel
              clSetKernelArg(kernel_A, 0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
              clSetKernelArg(kernel_A, 1, Sizeof.cl_mem, Pointer.to(memObject_en_types));
              clSetKernelArg(kernel_A, 2, Sizeof.cl_mem, Pointer.to(memObject_init_grain_rec_types));
              clSetKernelArg(kernel_A, 3, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
              clSetKernelArg(kernel_A, 4, Sizeof.cl_mem, Pointer.to(memObject_low_tempr_recryst));
              clSetKernelArg(kernel_A, 5, Sizeof.cl_mem, Pointer.to(memObject_high_tempr_recryst));
              clSetKernelArg(kernel_A, 6, Sizeof.cl_mem, Pointer.to(memObject_low_tempr_twinning));
              clSetKernelArg(kernel_A, 7, Sizeof.cl_mem, Pointer.to(memObject_high_tempr_twinning));
              clSetKernelArg(kernel_A, 8, Sizeof.cl_mem, Pointer.to(memObject_max_prob_recryst));
              clSetKernelArg(kernel_A, 9, Sizeof.cl_mem, Pointer.to(memObject_max_prob_twinning));
            
              for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                clSetKernelArg(kernel_A, 10 + neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
              clSetKernelArg(kernel_A, 10 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_rand_nums));
              clSetKernelArg(kernel_A, 11 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_prob_new_grain));
              clSetKernelArg(kernel_A, 12 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_prob_twinning));
              clSetKernelArg(kernel_A, 13 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_new_grain_embryos));
              clSetKernelArg(kernel_A, 14 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_twin_grain_embryos));
              clSetKernelArg(kernel_A, 15 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_twin_emb_root_gr_indices));
              clSetKernelArg(kernel_A, 16 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_final_def_densities));
            
              // ID of current kernel
              kernel_counter++;
            
              // ID of current command queue
              com_queue_counter = kernel_counter % com_queue_number;
            
              // Obtaining of current time
              start_date = new Date();
            
              // Execution of the A-th kernel at current time step
              clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_A, 1, null, global_work_size, local_work_size, 0, null, null);
            
              // Obtaining of current time
              inter_date = new Date();
            
              // Reading of the output data (probabilities of generation of new grains) from a buffer object to host memory.
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_prob_new_grain, CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, prob_new_grain_ptr, 0, null, null);
            
              // Reading of the output data (probabilities of generation of twins) from a buffer object to host memory.
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_prob_twinning, CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, prob_twinning_ptr, 0, null, null);
            
              // Reading of the output data about presences of new grain embryos in cells
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_new_grain_embryos, CL_TRUE, 0,
                                Sizeof.cl_char * work_item_number, new_grain_embryos_ptr, 0, null, null);
            
              // Reading of the output data about presences of twin grain embryos in cells
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_twin_grain_embryos, CL_TRUE, 0,
                                Sizeof.cl_char * work_item_number, twin_grain_embryos_ptr, 0, null, null);
            
              // Reading of the output data about indices of new grains where embryos of twin grains appear
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_twin_emb_root_gr_indices, CL_TRUE, 0,
                                Sizeof.cl_int * work_item_number, twin_emb_root_gr_indices_ptr, 0, null, null);
               
              if(step_count > 0.5*step_number & false)
              {
                // Reading of the output data about recrystallization types of grains
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_grain_rec_types, CL_TRUE, 0,
                                Sizeof.cl_char * grain_number, init_grain_rec_types_ptr, 0, null, null);
                
                // Reading of the output data (probabilities of generation of twins) from a buffer object to host memory.
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_max_prob_recryst, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, max_prob_recryst_ptr, 0, null, null);
                
                if(write_to_file & false)
                {
                  System.out.println("=======  Kernel A  =======  Kernel A  =======");
                
                  for(int grain_counter = 0; grain_counter < new_gr_number + twin_gr_number + specArrays.init_grain_number; grain_counter++)
                  {
                    System.out.println("Grain # "+(grain_counter+1)+" has rec.type = "+init_grain_rec_types[grain_counter]+". max.prob.rec.= "+max_prob_recryst[grain_counter]);
                  }
                  System.out.println("=======  ++++++++++  =======  ++++++++++  =======");
                }
                //=========================================================================================
                
                // Setting of the argument values to the C-th kernel
                clSetKernelArg(kernel_C, 0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
                clSetKernelArg(kernel_C, 1, Sizeof.cl_mem, Pointer.to(memObject_en_types));
                clSetKernelArg(kernel_C, 2, Sizeof.cl_mem, Pointer.to(memObject_init_grain_rec_types));
                clSetKernelArg(kernel_C, 3, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));            
              //  clSetKernelArg(kernel_C, 4, Sizeof.cl_mem, Pointer.to(memObject_low_tempr_recryst));
              //  clSetKernelArg(kernel_C, 5, Sizeof.cl_mem, Pointer.to(memObject_high_tempr_recryst));
                
              //  clSetKernelArg(kernel_C, 4, Sizeof.cl_mem, Pointer.to(memObject_high_tempr_twinning));
              //  clSetKernelArg(kernel_C, 5, Sizeof.cl_mem, Pointer.to(memObject_low_tempr_recryst));
                
                clSetKernelArg(kernel_C, 4, Sizeof.cl_mem, Pointer.to(memObject_low_tempr_twinning));
                clSetKernelArg(kernel_C, 5, Sizeof.cl_mem, Pointer.to(memObject_low_tempr_recryst));
                
             //   clSetKernelArg(kernel_C, 6, Sizeof.cl_mem, Pointer.to(memObject_max_prob_recryst));
                clSetKernelArg(kernel_C, 6, Sizeof.cl_mem, Pointer.to(memObject_max_prob_twinning));
                
                clSetKernelArg(kernel_C, 7, Sizeof.cl_mem, Pointer.to(memObject_rand_nums));
            
                for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                  clSetKernelArg(kernel_C, 8 + neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
                clSetKernelArg(kernel_C, 8 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_prob_new_grain));
                clSetKernelArg(kernel_C, 9 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_new_grain_embryos));
                
                // Execution of the C-th kernel at current time step
                clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_C, 1, null, global_work_size, local_work_size, 0, null, null);
                
                // Reading of the output data about presences of new grain embryos in cells
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_new_grain_embryos, CL_TRUE, 0,
                                Sizeof.cl_char * work_item_number, new_grain_embryos_ptr, 0, null, null);
                
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_prob_new_grain, CL_TRUE, 0,
                                Sizeof.cl_double * work_item_number, prob_new_grain_ptr, 0, null, null);
                
                clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_grain_rec_types, CL_TRUE, 0,
                                Sizeof.cl_char * grain_number, init_grain_rec_types_ptr, 0, null, null);
                
                if(write_to_file & false)
                {
                  System.out.println("=======  Kernel C  =======  Kernel C  =======");
                
                  for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
                  {
                    grain_index = init_grain_indices[cell_counter];
                    
                    if(init_grain_rec_types[grain_index] == TWIN_GRAIN & prob_new_grain[cell_counter] > 0)
                    {
                      System.out.println("Grain # "+(grain_index+1)+" has rec.type = "+init_grain_rec_types[grain_index]+
                                         ". Cell # "+cell_counter+" has prob_switch = "+prob_new_grain[cell_counter]);
                    }
                  }
                  
                  System.out.println("=======  ++++++++++  =======  ++++++++++  =======");
                }
              }
              
              for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
              {
                // TEST
                if(false)
                if(cell_coords[1][cell_counter] < min_coord_Y + 0.45*(max_coord_Y - min_coord_Y) | 
                   cell_coords[1][cell_counter] > min_coord_Y + 0.55*(max_coord_Y - min_coord_Y))
                {
                    new_grain_embryos[cell_counter]  = (byte)0;
                    twin_grain_embryos[cell_counter] = (byte)0;
                    
                    prob_new_grain[cell_counter] = 0;
                }
                
                // TEST
                if(new_grain_embryos[cell_counter] == (byte)1 & twin_grain_embryos[cell_counter] == (byte)1)
                {
                    System.out.println("ERROR!!! Cell # "+cell_counter+" cannot be an embryo of new and twin grains!!!");
                    
                  //  if(Math.random() > 0.5)
                      twin_grain_embryos[cell_counter] = (byte)0;
                 //   else
                  //    new_grain_embryos[cell_counter]  = (byte)0;
                }
                
                if(new_grain_embryos[cell_counter] == (byte)1)
                {
                  if(en_types[cell_counter] == INNER_CELL)
                  {
                    old_gr_index    = init_grain_indices[cell_counter];
                    old_gr_rec_type = init_grain_rec_types[old_gr_index - 1];
                    
                    new_gr_number++;
                    new_gr_index = new_gr_number + twin_gr_number + specArrays.init_grain_number;
                    
                    init_grain_indices[cell_counter]       = new_gr_index;                      
                    init_grain_rec_types[new_gr_index - 1] = NEW_GRAIN;
                    
                    root_grain_indices[new_gr_index - 1] = 0;
                    
                    System.out.print("Step # "+step_count+". Cell # "+cell_counter+". Grain # "+new_gr_index+
                                     ". New grain embryo # "+new_gr_number);
                    
                    if(old_gr_rec_type == TWIN_GRAIN)
                    {
                      new_grains_in_twins_number++;
                      
                      if(!new_grains_in_twins.contains(new_gr_index))
                        new_grains_in_twins.add(new_gr_index);
                        
                      System.out.println(" is contained in twin grain # "+old_gr_index+" with root gr.index = "+root_grain_indices[old_gr_index - 1]+
                                         ". Number of new grains in twins: "+new_grains_in_twins_number+" = "+new_grains_in_twins.size());
                      
                      new_grains_in_twin[old_gr_index - 1].add(new_gr_index);
                    }
                    else
                      System.out.println(".");
                  }
                  else
                  {
                    new_grain_embryos[cell_counter] = (byte)0;
                    
                    System.out.println("WRONG EMBRYO OF NEW GRAIN!!! Step # "+step_count+". Cell # "+cell_counter+
                                       ". Grain # "+init_grain_indices[cell_counter]+". Grain type: "+en_types[cell_counter]);
                  }
                }
                
                if(twin_grain_embryos[cell_counter] == (byte)1 & twin_emb_root_gr_indices[cell_counter] > 0)
                {
                  if(en_types[cell_counter] == INNER_CELL)
                  {
                    twin_gr_number++;
                    twin_gr_index = new_gr_number + twin_gr_number + specArrays.init_grain_number;
                    
                    init_grain_indices[cell_counter] = twin_gr_index;
                    init_grain_rec_types[twin_gr_index - 1] = TWIN_GRAIN;
                  
                    root_grain_indices[twin_gr_index - 1] = twin_emb_root_gr_indices[cell_counter];
                    
                   // if(root_grain_indices[twin_gr_index - 1] > 0)
                      System.out.println("Step # "+step_count+". Cell # "+cell_counter+". Grain # "+twin_gr_index+
                                     ". Twin grain embryo # "+twin_gr_number+". Root grain index: "+root_grain_indices[twin_gr_index - 1]);
                  }
                  else
                  {
                    twin_grain_embryos[cell_counter] = (byte)0;
                    
                    System.out.println("WRONG EMBRYO OF TWIN GRAIN!!! Step # "+step_count+". Cell # "+cell_counter+
                                       ". Grain # "+init_grain_indices[cell_counter]+". Grain type: "+en_types[cell_counter]);
                  }
                }
                
                if(false)
                if(init_grain_indices[cell_counter] > specArrays.init_grain_number & step_count % 100 == 0)
                  System.out.println("Step # "+step_count+". The cell # "+cell_counter+" belongs to new grain # "+(init_grain_indices[cell_counter] - specArrays.init_grain_number)+
                      "; new grain index = "+init_grain_indices[cell_counter]+"; grain_rec_type = "+init_grain_rec_types[init_grain_indices[cell_counter] - 1]);
              
            //  System.out.println("ERROR!!! New grain embryo # "+new_gr_number+" cannot be generated in cell with another embryo!!! But cell # "+cell_counter+" contains embryo # "+init_grain_indices[cell_counter]);
              
                grain_types[init_grain_indices[cell_counter] - 1] = en_types[cell_counter];
              }
            
              if(false)
              {
                if(write_to_file)
                  System.out.println("\nKERNEL_A:\n");
            
                for(int grain_counter = 0; grain_counter < new_gr_number + twin_gr_number + specArrays.init_grain_number; grain_counter++)
                {
                  if(root_grain_indices[grain_counter] > 0)
                  if(init_grain_rec_types[root_grain_indices[grain_counter] - 1] != NEW_GRAIN |
                     init_grain_rec_types[grain_counter] != TWIN_GRAIN)
                    System.out.println("ERROR!!! Step # "+step_count+". Grain #"+(grain_counter + 1)+". rec.type = "+init_grain_rec_types[grain_counter]+
                                   "; root grain index: "+root_grain_indices[grain_counter]+"; root grain rec.type = "+init_grain_rec_types[root_grain_indices[grain_counter] - 1]);
              
                  if(write_to_file)
                    System.out.println("Grain # "+(grain_counter + 1)+"; en_type: "+grain_types[grain_counter]+"; rec_type: "+init_grain_rec_types[grain_counter]+
                        "; root grain index: "+root_grain_indices[grain_counter]);
                }
            
                if(write_to_file)
                  System.out.println();
              }
            
              if(new_gr_number + twin_gr_number > 0 & (step_count % 100 == 0 | write_to_file))
              {
                if(!write_to_file)
                  System.out.println("\nStep # "+step_count+".");
              
                System.out.println("\nNumber of old grains: "+specArrays.init_grain_number+"; number of new grains: "+new_gr_number+"; number of twin grains: "+twin_gr_number);
                System.out.println("Number of new grains in twins: "+new_grains_in_twins_number+" = "+new_grains_in_twins.size());
                
                if(write_to_file)
                try
                {                  
                  String string, token;
                  StringTokenizer st;
                  
                  if(write_init_clrs_file)
                  {
                    BufferedReader br_clrs      = new BufferedReader(new FileReader(specArrays.write_file_name+".clrs"));
                    BufferedWriter bw_init_clrs = new BufferedWriter(new FileWriter(specArrays.write_file_name+"_init_grains.clrs"));
                  
                    // Writing of initial grain colours to the file
                    while(br_clrs.ready())
                    {
                      string = br_clrs.readLine();
                      bw_init_clrs.write(string+"\n");
                    
                      bw_init_clrs.flush();
                    }
                  
                    write_init_clrs_file = false;
                    
                    br_clrs.close();
                    bw_init_clrs.close();
                  }
                  
                  BufferedReader br_init_clrs = new BufferedReader(new FileReader(specArrays.write_file_name+"_init_grains.clrs"));
                  BufferedWriter bw_clrs      = new BufferedWriter(new FileWriter(specArrays.write_file_name+".clrs"));
                  BufferedWriter bw_diff_clrs = new BufferedWriter(new FileWriter(specArrays.write_file_name+"_new_grains_diff.clrs"));
                  
                  int total_col_number   = 0;
                  int total_grain_number = 0;
                  int old_grain_number   = 0;
                  int old_gr_col_number  = 0;
                  int new_gr_col_number  = 0;
                  
                  if(br_init_clrs.ready())
                  {
                    string = br_init_clrs.readLine();
                    
                  //  bw_clrs.write(string+"\n");
                  //  bw_diff_clrs.write(string+"\n");
                    
                    st = new StringTokenizer(string);
                    
                    total_col_number   = new Integer(st.nextToken()).intValue();
                    total_grain_number = new Integer(st.nextToken()).intValue();
                    old_grain_number   = new Integer(st.nextToken()).intValue();
                    old_gr_col_number  = new Integer(st.nextToken()).intValue();
                    new_gr_col_number  = new Integer(st.nextToken()).intValue();
                    
                    bw_clrs.     write(total_col_number+" "+(specArrays.init_grain_number + new_gr_number + twin_gr_number)+" "+
                                       old_grain_number+" "+old_gr_col_number+" "+new_gr_col_number+"\n");
                    bw_diff_clrs.write(total_col_number+" "+(specArrays.init_grain_number + new_gr_number + twin_gr_number)+" "+
                                       old_grain_number+" "+old_gr_col_number+" "+new_gr_col_number+"\n");
                    
                    for(int string_counter = 0; string_counter < total_col_number + old_grain_number; string_counter++)
                    {
                      string = br_init_clrs.readLine();
                      
                      bw_clrs.write(string+"\n");
                      bw_diff_clrs.write(string+"\n");
                    }
                  }
                  
                  br_init_clrs.close();
                  
                  int max_col_index = old_gr_col_number - 1;
                  int new_grain_colours[] = new int[new_gr_number + twin_gr_number];
                  
                  for(int new_gr_counter = specArrays.init_grain_number + 1; new_gr_counter <= specArrays.init_grain_number + new_gr_number + twin_gr_number; new_gr_counter++)
                  {
                    bw_clrs.write(new_gr_counter+" ");
                    bw_diff_clrs.write(new_gr_counter+" ");
                    
                    if(root_grain_indices[new_gr_counter - 1] == 0)
                    {
                      max_col_index++;
                      new_grain_colours[new_gr_counter - specArrays.init_grain_number - 1] = max_col_index;
                    }
                    else
                    {
                      max_col_index++;
                      new_grain_colours[new_gr_counter - specArrays.init_grain_number - 1] = new_grain_colours[root_grain_indices[new_gr_counter - 1] - specArrays.init_grain_number - 1];
                    }
                    
                    if(new_grain_colours[new_gr_counter - specArrays.init_grain_number - 1] >= old_gr_col_number)
                    {
                        new_grain_colours[new_gr_counter - specArrays.init_grain_number - 1] = old_gr_col_number + 
                                (new_grain_colours[new_gr_counter - specArrays.init_grain_number - 1] - old_gr_col_number)%(total_col_number - old_gr_col_number);
                    }
                    
                    if(max_col_index >= old_gr_col_number)
                    {
                        max_col_index = old_gr_col_number + (max_col_index - old_gr_col_number)%(total_col_number - old_gr_col_number);
                    }
                    
                    bw_clrs.write(new_grain_colours[new_gr_counter - specArrays.init_grain_number - 1]+"\n");
                    bw_clrs.flush();
                    
                    bw_diff_clrs.write(max_col_index+"\n");
                    bw_diff_clrs.flush();
                  }
                  
                  bw_clrs.write("# Total number of grains is the sum of numbers of old, new and twinning grains: \n");
                  bw_clrs.write("# "+(specArrays.init_grain_number + new_gr_number + twin_gr_number)+" "+
                                      specArrays.init_grain_number+" "+new_gr_number+" "+twin_gr_number);
                  
                  bw_diff_clrs.write("# Total number of grains is the sum of numbers of old, new and twinning grains: \n");
                  bw_diff_clrs.write("# "+(specArrays.init_grain_number + new_gr_number + twin_gr_number)+" "+
                                           specArrays.init_grain_number+" "+new_gr_number+" "+twin_gr_number);
                  
                  bw_clrs.close();
                  bw_diff_clrs.close();
                }
                catch(IOException io_exc)
                {
                    System.out.println("ERROR!!! IOException: "+io_exc);
                }
              }
            
              if(false)
              if(step_count % 100 == 0)
              {
               // System.out.println("\nStep # "+step_count+"; number of old grains: "+specArrays.init_grain_number+"; number of new grains: "+new_gr_number);
              
                System.out.println();
              
                int[] grain_sizes = new int[new_gr_number + twin_gr_number + specArrays.init_grain_number];
              
                for(int grain_counter = 0; grain_counter < new_gr_number + twin_gr_number + specArrays.init_grain_number; grain_counter++)
                  grain_sizes[grain_counter] = 0;
              
                int total_grain_size = 0;
              
                for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
                {
                  //  init_grain_indices[cell_counter] = grain_indices[cell_counter];
                  grain_sizes[init_grain_indices[cell_counter] - 1]++;
                  
              //  if(init_grain_indices[cell_counter] > specArrays.init_grain_number)
              //    System.out.println("Cell # "+cell_counter+" belongs to grain # "+init_grain_indices[cell_counter]+".");
                }
              
                for(int grain_counter = 0; grain_counter < new_gr_number + twin_gr_number + specArrays.init_grain_number; grain_counter++)
                if(grain_sizes[grain_counter] > 0)
                {
                  total_grain_size += grain_sizes[grain_counter];
                  System.out.println("Grain # "+(grain_counter + 1)+"; size = "+grain_sizes[grain_counter]+"; rec_type = "+init_grain_rec_types[grain_counter]);
                }
              
                System.out.println();
                System.out.println("Total number of cells:           "+cell_number);
                System.out.println("Total number of cells in grains: "+total_grain_size);
                System.out.println();
              }
            
              clReleaseMemObject(memObject_init_grain_indices);
              clReleaseMemObject(memObject_init_grain_rec_types);
            
              init_grain_indices_ptr = Pointer.to(init_grain_indices);
            
              // Creation of the memory object for array of initial indices of grains
              memObject_init_grain_indices = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * work_item_number, init_grain_indices_ptr, null);
            
              init_grain_rec_types_ptr = Pointer.to(init_grain_rec_types);
            
              // Creation of the memory object for array of grain types according to its role in recrystallization process
              memObject_init_grain_rec_types    = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_char * grain_number, init_grain_rec_types_ptr, null);
            
              // Obtaining of current time
              inter_date_1 = new Date();
            
              // Calculation of the time period of the A-th kernel execution
              time_period_exec += inter_date.getTime() - start_date.getTime();
                
              // Calculation of the time period of reading of the output data
              time_period_read += inter_date_1.getTime()- inter_date.getTime();
            }
          
            //---------------------------------------------------------------------------------------
            if(true) // if(false) // 
            {
              clReleaseMemObject(memObject_root_grain_indices);
              
              root_grain_indices_ptr = Pointer.to(root_grain_indices);
            
              // Creation of the memory object for array of indices of grains, from which twinning grains grow
              memObject_root_grain_indices = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_int * grain_number, root_grain_indices_ptr, null);
            
            // Setting of the argument values to the B-th kernel
            clSetKernelArg(kernel_B, 0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
            clSetKernelArg(kernel_B, 1, Sizeof.cl_mem, Pointer.to(memObject_en_types));            
            clSetKernelArg(kernel_B, 2, Sizeof.cl_mem, Pointer.to(memObject_new_grain_embryos));
            clSetKernelArg(kernel_B, 3, Sizeof.cl_mem, Pointer.to(memObject_twin_grain_embryos));            
            clSetKernelArg(kernel_B, 4, Sizeof.cl_mem, Pointer.to(memObject_init_grain_rec_types));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_B, 5 + neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
            clSetKernelArg(kernel_B, 5 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_grain_indices));
            clSetKernelArg(kernel_B, 6 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_grain_rec_types));
            clSetKernelArg(kernel_B, 7 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_root_grain_indices));
            clSetKernelArg(kernel_B, 8 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_grain_angles_1));
            clSetKernelArg(kernel_B, 9 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_max_grain_angles));
            clSetKernelArg(kernel_B, 10 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_heatMaxMobility));
            clSetKernelArg(kernel_B, 11 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_maxBoundEnergy));
            clSetKernelArg(kernel_B, 12 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_dislMaxMobility));
            clSetKernelArg(kernel_B, 13 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_mechMaxMobility));
            clSetKernelArg(kernel_B, 14 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_disl_energies));
            clSetKernelArg(kernel_B, 15 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_min_disl_energies));
            clSetKernelArg(kernel_B, 16 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_rand_nums));
            
            clSetKernelArg(kernel_B, 17 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_lattice_vector_A_lengths));
            clSetKernelArg(kernel_B, 18 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_lattice_vector_B_lengths));
            clSetKernelArg(kernel_B, 19 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_lattice_vector_C_lengths));
            
            for(int mem_obj_counter = 0; mem_obj_counter < 9; mem_obj_counter++)
              clSetKernelArg(kernel_B, 20 + neighb1S_number + mem_obj_counter, Sizeof.cl_mem, Pointer.to(memObject_lattice_vectors[mem_obj_counter]));
            
            clSetKernelArg(kernel_B, 29 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_lattice_anis_coeff));
            clSetKernelArg(kernel_B, 30 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_lattice_parameters));
            clSetKernelArg(kernel_B, 31 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_final_def_densities));
            clSetKernelArg(kernel_B, 32 + neighb1S_number, Sizeof.cl_mem, Pointer.to(memObject_min_disl_density));
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the A-th kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_B, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Reading of the output data (new grain indices of cells joining to embryos of new grains) from a buffer object to host memory
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_grain_indices, CL_TRUE, 0,
                                Sizeof.cl_int * work_item_number, grain_indices_ptr, 0, null, null);
            
            // Reading of the output data (types of grains according to its role in recrystallization process) from a buffer object to host memory
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_grain_rec_types, CL_TRUE, 0,
                                Sizeof.cl_char * grain_number, grain_rec_types_ptr, 0, null, null);
            
            // Reading of the output data (indices of grains, from which twins grow) from a buffer object to host memory
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_root_grain_indices, CL_TRUE, 0,
                                Sizeof.cl_int * grain_number, root_grain_indices_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_grain_angles_1, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, grain_angles_1_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_max_grain_angles, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, max_grain_angles_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_heatMaxMobility, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, heatMaxMobility_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_maxBoundEnergy, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, maxBoundEnergy_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_dislMaxMobility, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, dislMaxMobility_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_mechMaxMobility, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, mechMaxMobility_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_disl_energies, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, disl_energies_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_min_disl_energies, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, min_disl_energies_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_en_types, CL_TRUE, 0,
                                Sizeof.cl_char * (work_item_number+1), en_types_ptr, 0, null, null);
            
            // Reading of parameters of crystal lattice for grains
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_lattice_vector_A_lengths, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, lattice_vector_A_lengths_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_lattice_vector_B_lengths, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, lattice_vector_B_lengths_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_lattice_vector_C_lengths, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, lattice_vector_C_lengths_ptr, 0, null, null);
            
            for(int mem_obj_counter = 0; mem_obj_counter < 9; mem_obj_counter++)
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_lattice_vectors[mem_obj_counter], CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, lattice_vectors_ptr[mem_obj_counter], 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_lattice_anis_coeff, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, lattice_anis_coeff_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_lattice_parameters, CL_TRUE, 0,
                                Sizeof.cl_double * grain_number, lattice_parameters_ptr, 0, null, null);
            
            // Obtaining of current time
            inter_date_1 = new Date();
            
            // Calculation of the time period of the A-th kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
                
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            
            // if(false)
            // if(step_count % 20 == 0)
            if(write_to_file)
            {
              System.out.print("\nStep # "+step_count+"; number of old grains: "+specArrays.init_grain_number+
                               "; number of new grains: "+new_gr_number+"; number of twin grains: "+twin_gr_number+
                               ";\nNumber of new grains in twins: "+new_grains_in_twins_number+" = "+new_grains_in_twins.size()+":\n");
              
              for(int new_gr_counter = 0; new_gr_counter < new_grains_in_twins.size(); new_gr_counter++)
                System.out.println("New grain # "+(new_gr_counter + 1)+" has index "+new_grains_in_twins.get(new_gr_counter)+".");
                      
              int[] grain_sizes = new int[specArrays.init_grain_number + new_gr_number + twin_gr_number];
              
              for(int grain_counter = 0; grain_counter < specArrays.init_grain_number + new_gr_number + twin_gr_number; grain_counter++)
                grain_sizes[grain_counter] = 0;
              
              int total_grain_size = 0;
              int new_grains_size = 0;
              int old_grains_size = 0;
              
              for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
              {
                if(grain_indices[cell_counter] < 1 | grain_indices[cell_counter] > specArrays.init_grain_number + new_gr_number + twin_gr_number)
                {  
                   System.out.println("ERROR!!! Cell # "+cell_counter+" belongs to grain # "+grain_indices[cell_counter]+
                          ". Total number of grains: "+(specArrays.init_grain_number + new_gr_number + twin_gr_number));
                }
                else
                {
                  //  grain_indices[cell_counter] = init_grain_indices[cell_counter];
                  grain_sizes[grain_indices[cell_counter] - 1]++;
                
                  if(grain_indices[cell_counter] > specArrays.init_grain_number)
                    new_grains_size++;
                  else
                    old_grains_size++;
                 
                  // energy type of grain containing current cell
                  grain_types[grain_indices[cell_counter] - 1] = en_types[cell_counter];
                
                  //  if(grain_indices[cell_counter] > specArrays.init_grain_number)
                  //    System.out.println("Cell # "+cell_counter+" belongs to grain # "+grain_indices[cell_counter]+".");
                }
              }
              
              for(int grain_counter = 0; grain_counter < specArrays.init_grain_number + new_gr_number + twin_gr_number; grain_counter++)
            //  if(grain_sizes[grain_counter] > 0)
              {
                total_grain_size += grain_sizes[grain_counter];
                
                if(new_grains_in_twins.contains(grain_counter + 1))
                  gr_rec_type = 3;
                else
                  gr_rec_type = grain_rec_types[grain_counter];
                
            //    if(grain_sizes[grain_counter] >= 30)
                System.out.println("Grn#"+(grain_counter + 1)+"; sz="+grain_sizes[grain_counter]+"; en_tp="+grain_types[grain_counter]+
                    "; rec_type="+gr_rec_type+"; rt_gr_ind="+root_grain_indices[grain_counter]+
                    "; angle_1="+grain_angles_1[grain_counter]+
                    "; angle_2="+grain_angles_2[grain_counter]+
                    "; angle_3="+grain_angles_3[grain_counter]+
                 //   "; max_angle = "+max_grain_angles[grain_counter]+"; heat_max_mob = "+heatMaxMobility[grain_counter]+
                 //   "; maxBoundEnergy = "+maxBoundEnergy[grain_counter]+
                   // "; dislMaxMob = "+dislMaxMobility[grain_counter]+ // "; mechMaxMob = "+mechMaxMobility[grain_counter]+
                   // "; disl_en = "+disl_energies[grain_counter]+"; min_disl_en = "+min_disl_energies[grain_counter]);
                    "; l_A="+lattice_vector_A_lengths[grain_counter]+"; l_B="+lattice_vector_B_lengths[grain_counter]+"; l_C="+lattice_vector_C_lengths[grain_counter]+
                    "; A=("+lattice_vectors[0][grain_counter]+","+lattice_vectors[1][grain_counter]+","+lattice_vectors[2][grain_counter]+
                    ");B=("+lattice_vectors[3][grain_counter]+","+lattice_vectors[4][grain_counter]+","+lattice_vectors[5][grain_counter]+
                    ");C=("+lattice_vectors[6][grain_counter]+","+lattice_vectors[7][grain_counter]+","+lattice_vectors[8][grain_counter]+"); "+
                    "lat_prm="+lattice_parameters[grain_counter]+"; cf_diss="+coeff_diss[grain_counter]);
             //       "; lat.an.cf="+lattice_anis_coeff[grain_counter]+
             //       "; mod_elast = "+mod_elast[grain_counter]+
             //       "; heat_exp_coeff = "+heat_exp_coeffs[grain_counter]);
                
                if(grain_sizes[grain_counter] > 0 & 
                   (grain_rec_types[grain_counter] == TWIN_GRAIN | grain_rec_types[grain_counter] == NEW_GRAIN) &
                   grain_types[grain_counter] != INNER_CELL)
                {
                    System.out.println("ERROR!!! Grain # "+grain_counter+" has energy type "+grain_types[grain_counter]+" and rec.type "+grain_rec_types[grain_counter]);
                }
              }
              
              int new_grains_in_twin_number = 0;
              int twin_gr_counter = 0;
              int new_grain_in_twin_index = 0;
              
              // Printing of data about new grains in twin grains
              if(false)
              for(int grain_counter = 0; grain_counter < specArrays.init_grain_number + new_gr_number + twin_gr_number; grain_counter++)
              {
                if(grain_rec_types[grain_counter] == TWIN_GRAIN)
                {
                  twin_gr_counter++;
                  
                  new_grains_in_twin_number = new_grains_in_twin[grain_counter].size();
                  
                  if(twin_gr_counter == 1)
                  {
                    System.out.println("======++++++======++++++======++++++======++++++======++++++");
                    System.out.println("Total number of new grains in twins: "+new_grains_in_twins_number);
                    
                    new_grains_in_twins_number = 0;
                  }
                  
                  new_grains_in_twins_number += new_grains_in_twin_number;
                      
                  if(new_grains_in_twin_number > 0)
                    System.out.print("Twin grain "+twin_gr_counter+" # "+(grain_counter + 1)+" contains "+new_grains_in_twin_number+" new grains");
                  
                  if(new_grains_in_twin_number > 0)
                  {
                    System.out.print(" with following indices:");
                    
                    for(int new_gr_counter = 0; new_gr_counter < new_grains_in_twin_number; new_gr_counter++)
                    {
                      new_grain_in_twin_index = (int)new_grains_in_twin[grain_counter].get(new_gr_counter);
                      
                      System.out.print(" "+new_grain_in_twin_index);
                      
                      if(new_gr_counter < new_grains_in_twin_number - 1) System.out.print(",");
                      else                                               System.out.println(".");
                      
                      // TEST
                      if(grain_rec_types[new_grain_in_twin_index - 1] != NEW_GRAIN)
                        System.out.println("\nERROR!!! New grain # "+new_grain_in_twin_index+" has rec.type = "+grain_rec_types[new_grain_in_twin_index - 1]+
                                           " (rec.type must be equal to "+NEW_GRAIN+").");
                    }
                  }
                //  else                  
                //    System.out.println(".");
                }
              }
              
              if(twin_gr_counter > 0)
              {
                System.out.println("Total number of new grains in twins: "+new_grains_in_twins_number);
                System.out.println("======++++++======++++++======++++++======++++++======++++++");
              }
              
              System.out.println();
              System.out.println("Total number of cells:               "+cell_number);
              System.out.println("Total number of cells in grains:     "+total_grain_size);
              System.out.println("Total number of cells in old grains: "+old_grains_size);
              System.out.println("Total number of cells in new grains: "+new_grains_size);
              System.out.println();
            }
          }
            //=========================================================================================
         // if(false)
          {
            // Setting of the argument values to the 4th kernel.
            clSetKernelArg(kernel_4,  0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
            clSetKernelArg(kernel_4,  1, Sizeof.cl_mem, Pointer.to(memObject_grain_indices));
            clSetKernelArg(kernel_4,  2, Sizeof.cl_mem, Pointer.to(memObject_init_grain_rec_types));
            clSetKernelArg(kernel_4,  3, Sizeof.cl_mem, Pointer.to(memObject_grain_rec_types));
            
            // ID of current kernel
         //   kernel_counter++;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 4th kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_4, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Calculation of the time period of the 4th kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Reading of the output data (new grain indices) from a buffer object to host memory.
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_grain_indices, CL_TRUE, 0,
                            Sizeof.cl_int * work_item_number, init_grain_indices_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_grain_rec_types, CL_TRUE, 0,
                            Sizeof.cl_char * grain_number, init_grain_rec_types_ptr, 0, null, null);
            
            // Obtaining of current time
            inter_date_1 = new Date();
                
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            
            if(step_count % 100 == 0 & false)
            {
              System.out.println("Step # "+step_count);
              
              int[] grain_sizes = new int[specArrays.init_grain_number + new_gr_number + twin_gr_number];
              int total_grain_size = 0;
              
              for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
              {
                //  init_grain_indices[cell_counter] = grain_indices[cell_counter];
                  grain_sizes[init_grain_indices[cell_counter] - 1]++;
                  
                  if(init_grain_indices[cell_counter] > specArrays.init_grain_number)
                    System.out.println("Cell # "+cell_counter+" belongs to grain # "+init_grain_indices[cell_counter]+".");
              }
              
              for(int grain_counter = 0; grain_counter < specArrays.init_grain_number + new_gr_number + twin_gr_number; grain_counter++)
              {
                total_grain_size += grain_sizes[grain_counter];
                System.out.println("Grain # "+(grain_counter + 1)+"; size = "+grain_sizes[grain_counter]+"; rec_type = "+init_grain_rec_types[grain_counter]);
              }
              
              System.out.println();
              System.out.println("Total number of cells:           "+cell_number);
              System.out.println("Total number of cells in grains: "+total_grain_size);
              System.out.println();
            }
          }
          
         // if(false)
          {
            //---------------------------------------------------------------------------------------
            // Setting of the argument values to the 3rd (1) kernel
            clSetKernelArg(kernel_3_1, 0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
            clSetKernelArg(kernel_3_1, 1, Sizeof.cl_mem, Pointer.to(memObject_grain_angles_1));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_1, neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
          
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_1, neighb1S_number+neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_spec_angle_diff[neighb_counter]));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_1, 2*neighb1S_number+neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_neighb_heat_max_mob[neighb_counter]));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_1, 3*neighb1S_number+neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_spec_bound_energies[neighb_counter]));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_1, 4*neighb1S_number+neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_bound_energies[neighb_counter]));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_1, 5*neighb1S_number+neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_bound_velocities[neighb_counter]));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_1, 6*neighb1S_number+neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_probs[neighb_counter]));
            
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+2, Sizeof.cl_mem, Pointer.to(memObject_heatMaxMobility));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+3, Sizeof.cl_mem, Pointer.to(memObject_maxBoundEnergy));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+4, Sizeof.cl_mem, Pointer.to(memObject_init_temprs));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+5, Sizeof.cl_mem, Pointer.to(memObject_en_types));
            
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+6, Sizeof.cl_mem, Pointer.to(memObject_dislMaxMobility));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+7, Sizeof.cl_mem, Pointer.to(memObject_mechMaxMobility));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+8, Sizeof.cl_mem, Pointer.to(memObject_disl_energies));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+9, Sizeof.cl_mem, Pointer.to(memObject_init_mech_energies));
            
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+10, Sizeof.cl_mem, Pointer.to(memObject_lattice_vector_A_lengths));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+11, Sizeof.cl_mem, Pointer.to(memObject_lattice_vector_B_lengths));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+12, Sizeof.cl_mem, Pointer.to(memObject_lattice_vector_C_lengths));
            
            for(int mem_obj_counter = 0; mem_obj_counter < 9; mem_obj_counter++)
              clSetKernelArg(kernel_3_1, 7*neighb1S_number + 13 + mem_obj_counter, Sizeof.cl_mem, Pointer.to(memObject_lattice_vectors[mem_obj_counter]));
            
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+22, Sizeof.cl_mem, Pointer.to(memObject_lattice_anis_coeff));
            clSetKernelArg(kernel_3_1, 7*neighb1S_number+23, Sizeof.cl_mem, Pointer.to(memObject_growth_vectors_coordinates));
            
            // ID of current kernel
          //  kernel_counter++;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 3rd kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_3_1, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
            {
              // Reading of the output data from a buffer object to host memory
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_probs[neighb_counter], CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, probs_ptr[neighb_counter], 0, null, null);
              
              // Reading of the output data (boundary energies at boundaries with neighbours) from a buffer object to host memory.
            //  clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_bound_energies[neighb_counter], CL_TRUE, 0,
            //                      Sizeof.cl_double * work_item_number, bound_energies_ptr[neighb_counter], 0, null, null);
              
              // Reading of the output data (velocities of grain boundary movement) from a buffer object to host memory.
            //  clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_bound_velocities[neighb_counter], CL_TRUE, 0,
            //                    Sizeof.cl_double * work_item_number, bound_velocities_ptr[neighb_counter], 0, null, null);
              
            }
            
            if(write_to_file)
            {
              // Reading of the output data (maximal heat mobilities at boundaries with neighbours) from a buffer object to host memory.
            //  clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_neighb_heat_max_mob[0], CL_TRUE, 0,
             //                   Sizeof.cl_double * work_item_number, neighb_heat_max_mob_ptr[0], 0, null, null);
              
              // Reading of the output data (boundary energies at boundaries with neighbours) from a buffer object to host memory.
             // clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_bound_energies[0], CL_TRUE, 0,
             //                   Sizeof.cl_double * work_item_number, bound_energies_ptr[0], 0, null, null);
              
              
              
              // Reading of the output data (maximal mechanical mobilities of grains) from a buffer object to host memory.
            //  clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_mechMaxMobility, CL_TRUE, 0,
             //                   Sizeof.cl_double * grain_number, mechMaxMobility_ptr, 0, null, null);
              
              
              // TEST
              if(false)
              for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
              {
                for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                {
                  Double prob_F              = new Double(probs[neighb_counter][cell_counter]);
              //  Double bound_energy_D = new Double(bound_energies[neighb_counter][cell_counter]);
              //  Double bound_vel_D = new Double(bound_velocities[neighb_counter][cell_counter]);
                
                  if(prob_F.isNaN())
                  {
              //    if(cell_counter % 10000 == 0)
              //    if(probs[neighb_counter][cell_counter] != 0 | prob_F.isNaN())
              //      System.out.println("ERROR!!! Prob. = NaN!!! Cell # "+cell_counter+"; prob_"+neighb_counter+" = "+probs[neighb_counter][cell_counter]);
                    
                    probs[neighb_counter][cell_counter] = (double)0;
                  }
                }
              }
            }
            
            // Obtaining of current time
            inter_date_1 = new Date();
            
            // Calculation of the time period of the 3rd kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
                
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            //=========================================================================================
            // Setting of the argument values to the 3rd (2) kernel.
         
            clSetKernelArg(kernel_3_2, 0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
            clSetKernelArg(kernel_3_2, 1, Sizeof.cl_mem, Pointer.to(memObject_grain_indices));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_2, neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_2, neighb1S_number+neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_probs[neighb_counter]));
            
            for(int prob_sum_counter = 0; prob_sum_counter < neighb1S_number+2; prob_sum_counter++)
              clSetKernelArg(kernel_3_2, 2*neighb1S_number+2+prob_sum_counter, Sizeof.cl_mem, Pointer.to(memObject_prob_sums[prob_sum_counter]));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_2, 3*neighb1S_number+4+neighb_counter, Sizeof.cl_mem, Pointer.to(memObject_poss_switches[neighb_counter]));
          
            // ID of current kernel
          //  kernel_counter++;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 3rd kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_3_2, 1, null, global_work_size, local_work_size, 0, null, null);
                        
            // Obtaining of current time
            inter_date = new Date();
            
            // Reading of the output data (sums of switch probabilities) from a buffer object to host memory.
            for(int prob_sum_counter = 0; prob_sum_counter < neighb1S_number+2; prob_sum_counter++)
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_prob_sums[prob_sum_counter], CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, prob_sums_ptr[prob_sum_counter], 0, null, null);
            
            if(write_to_file)
            for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
            if(prob_sums[neighb1S_number][cell_counter] < 0 | prob_sums[neighb1S_number][cell_counter] > 1)// | 
        //       (cell_counter == 10728 & prob_sums[neighb1S_number][cell_counter] > 0))
            {
              System.out.println("Step # "+step_count+"; kernel_3_2; cell # "+cell_counter+". ERROR!!! prob.sum = "+prob_sums[neighb1S_number][cell_counter]);
              System.out.println("T = "+init_temprs[cell_counter]);
              step_number = 0;
          
              for(int prob_counter = 0; prob_counter < neighb1S_number; prob_counter++)
                System.out.println("probs["+prob_counter+"] = "+probs[prob_counter][cell_counter]);
            }
            
            // Reading of the output data from a buffer object to host memory
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_probs[neighb_counter], CL_TRUE, 0,
                                  Sizeof.cl_double * work_item_number, probs_ptr[neighb_counter], 0, null, null);
            
            if(false)
            for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
            for(int prob_counter = 0; prob_counter < neighb1S_number; prob_counter++)
            {
              Double prob_F              = new Double(probs[prob_counter][cell_counter]);
              
              if(prob_F.isNaN())
              {
             // System.out.println("ERROR!!! NaN!!! Step # "+step_count+"; kernel_3_2; cell # "+cell_counter+"; probs["+prob_counter+"] = "+probs[prob_counter][cell_counter]);
                probs[prob_counter][cell_counter] = (double)0;
                
             // step_number = 0;
             // cell_counter = cell_number;
             // prob_counter = neighb1S_number;
              }
            }
            
            // Obtaining of current time
            inter_date_1 = new Date();
            
            // Calculation of the time period of the 3rd kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
                
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
            //=========================================================================================
            // Release of memory object with old array of random numbers
            clReleaseMemObject(memObject_rand_nums);

            // Creation of array of random numbers
            for(int rand_counter = 0; rand_counter < work_item_number; rand_counter++)
              rand_nums[rand_counter] = (double)Math.random();
            
            rand_nums_ptr = Pointer.to(rand_nums);
        
            // Memory object for array of random numbers
            memObject_rand_nums      = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                                    Sizeof.cl_double * work_item_number, rand_nums_ptr, null);
            
            // Setting of the argument values to the 3rd (3) kernel.
            clSetKernelArg(kernel_3_3, 0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
            clSetKernelArg(kernel_3_3, 1, Sizeof.cl_mem, Pointer.to(memObject_grain_indices));
            
            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
              clSetKernelArg(kernel_3_3, neighb_counter+2, Sizeof.cl_mem, Pointer.to(memObject_neighbours1S[neighb_counter]));
            
            for(int prob_sum_counter = 0; prob_sum_counter < neighb1S_number+2; prob_sum_counter++)
              clSetKernelArg(kernel_3_3, neighb1S_number+2+prob_sum_counter, Sizeof.cl_mem, Pointer.to(memObject_prob_sums[prob_sum_counter]));
            
            clSetKernelArg(kernel_3_3, 2*neighb1S_number+4, Sizeof.cl_mem, Pointer.to(memObject_rand_nums));
            
            // ID of current kernel
          //  kernel_counter++;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 3rd kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_3_3, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Reading of the output data (indices of grains) from a buffer object to host memory.
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_grain_indices, CL_TRUE, 0,
                                  Sizeof.cl_int * work_item_number, grain_indices_ptr, 0, null, null);
            
            if(write_to_file)
              // Reading of the output data (random numbers) from a buffer object to host memory.
              clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_rand_nums, CL_TRUE, 0,
                                Sizeof.cl_int * work_item_number, rand_nums_ptr, 0, null, null);
            
            // Obtaining of current time
            inter_date_1 = new Date();
              
            // Calculation of the time period of the 3rd kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
             
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
             
            // Calculation of number of cell switches
            step_switch_counter = 0;
            
            if((step_count - 1)%(STEP_NUMBER/FILE_NUMBER) == 0)
              period_switch_counter = 0;
            
            for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
            if(grain_indices[cell_counter] != init_grain_indices[cell_counter])
            {
               step_switch_counter++;
               period_switch_counter++;
               switch_counter++;
            }
            //=========================================================================================
          //  if(false)
            {
              clReleaseKernel(kernel_4);
              
              // Creation of the 4th kernel
              kernel_4 = clCreateKernel(program_4, "kernel_4", null);
              
              // Setting of the argument values to the 4th kernel.
              clSetKernelArg(kernel_4,  0, Sizeof.cl_mem, Pointer.to(memObject_init_grain_indices));
              clSetKernelArg(kernel_4,  1, Sizeof.cl_mem, Pointer.to(memObject_grain_indices));
              clSetKernelArg(kernel_4,  2, Sizeof.cl_mem, Pointer.to(memObject_init_grain_rec_types));
              clSetKernelArg(kernel_4,  3, Sizeof.cl_mem, Pointer.to(memObject_grain_rec_types));
            }
            // ID of current kernel
         //   kernel_counter++;
            
            // ID of current command queue
            com_queue_counter = kernel_counter % com_queue_number;
            
            // Obtaining of current time
            start_date = new Date();
            
            // Execution of the 4th kernel at current time step
            clEnqueueNDRangeKernel(commandQueue[com_queue_counter], kernel_4, 1, null, global_work_size, local_work_size, 0, null, null);
            
            // Obtaining of current time
            inter_date = new Date();
            
            // Calculation of the time period of the 4th kernel execution
            time_period_exec += inter_date.getTime() - start_date.getTime();
            
            // Reading of the output data (new grain indices) from a buffer object to host memory.
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_grain_indices, CL_TRUE, 0,
                           Sizeof.cl_int * work_item_number, init_grain_indices_ptr, 0, null, null);
            
            clEnqueueReadBuffer(commandQueue[com_queue_counter], memObject_init_grain_rec_types, CL_TRUE, 0,
                            Sizeof.cl_char * grain_number, init_grain_rec_types_ptr, 0, null, null);
            
            // Obtaining of current time
            inter_date_1 = new Date();
                
            // Calculation of the time period of reading of the output data
            time_period_read += inter_date_1.getTime()- inter_date.getTime();
           }
          }
          //=========================================================================================
          
          if(write_to_file)
          {
              if(hard_bound_cond)
                System.out.println("Hard boundary conditions are realized.");
              else
                System.out.println("Soft boundary conditions are realized.");
        
              write_init_grains_file = false;
              
              // Method for calculation of relative changes of volumes of all cells at current time step
              calcRelativeVolumeChanges(stresses_X, stresses_Y, stresses_Z, en_types, cell_coords, grain_indices, mod_elast);
              
              // Recording of output data to the file
              file_counter = writeToFile(step_count, file_counter, max_power, grain_indices, disl_energies, 
                          en_types, loc_types, cell_coords, init_temprs, mech_energies, 
                          force_moments, diss_en_influxes, diss_energies, new_gr_number, twin_gr_number,
                          grain_angles_1, grain_angles_2, grain_angles_3, grain_types, 
                          grain_rec_types, root_grain_indices, write_init_grains_file, 
                          crack_values, crack_sum, stresses_X, stresses_Y, stresses_Z, def_density_changes, final_def_densities,
                          angle_velocity_X, angle_velocity_Y, angle_velocity_Z, abs_angle_velocity,
                          torsion_angle_X,  torsion_angle_Y,  torsion_angle_Z,  abs_torsion_angle,
                          rel_diss_energies, rel_diss_en_changes, rel_diss_en_influxes, mod_elast);
              
              // TEST
              if(step_count % 100 == 0)
              {
                  System.out.println("Size of array of cell temperatures: "+temperatures.length);
                  System.out.println("Number of cells:                    "+cell_number);
                  System.out.println("T[0] = "+temperatures[0]+" gr.index = "+grain_indices[0]);
                    
                  if(work_item_number > cell_number)
                  {
                      System.out.println("T["+(cell_number-1)+"] = "+temperatures[cell_number-1]+" gr.index = "+grain_indices[cell_number-1]);
                      System.out.println("T["+ cell_number   +"] = "+temperatures[cell_number  ]+" gr.index = "+grain_indices[cell_number  ]);
                  }
                  
                  System.out.println("T["+(work_item_number-1)+"] = "+temperatures[work_item_number-1]+" gr.index = "+grain_indices[work_item_number-1]);
              
                  double max_prob_sum = 0.0;
                
                  // Printing of parameters of cells to the file
                  for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
               // for(int cell_counter = 0; cell_counter < work_item_number; cell_counter++)
                  {
                    if(max_prob_sum < prob_sums[neighb1S_number][cell_counter])
                       max_prob_sum = prob_sums[neighb1S_number][cell_counter];
                  }
                
                  System.out.println("max_prob_sum = "+max_prob_sum);
                  System.out.println("Number of new  grain embryos: "+new_gr_number);
                  System.out.println("Number of twin grain embryos: "+twin_gr_number);
                  System.out.println("----------------------------------------------------------");
                
                  // Expected number of cell switches
                  double expected_switch_num = 0;
                  
                  expected_switch_num = 0;
                  
                  for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
               // for(int cell_counter = 0; cell_counter < work_item_number; cell_counter++)
                  {
                     // Parameters of cells on the line from the top facet centre to the bottom facet centre are shown.
                     if((indices_I[cell_counter] == cell_number_I/2 &
                         indices_J[cell_counter] == cell_number_J/2))
                      
                     // Parameters of cells on the line from the left facet centre to the right facet centre are shown.
                   // if((indices_K[cell_counter] == cell_number_K/2 &
                   //     indices_J[cell_counter] == cell_number_J/2))
                    {
                      // Index of current grain
                      grain_index = grain_indices[cell_counter];
                     
                      // Printing of parameters of cells to screen
                      System.out.println("Cell # "+cell_counter+"; ["+indices_I[cell_counter]+
                                       ","+indices_J[cell_counter]+","+indices_K[cell_counter]+"]; type = "+en_types[cell_counter]+
                                       "; tmp = "+init_temprs[cell_counter]+
                                  //   "; ht.strain= "+heat_strains[cell_counter]+
                                       "; stress= "+mech_energies[cell_counter]+
                                       "; mech.influx= "+mech_influxes[cell_counter]+
                                       "; init.gr.index= "+init_grain_indices[cell_counter]+
                                       "; angle_1= "+grain_angles_1[grain_index - 1]+
                                       "; angle_2= "+grain_angles_2[grain_index - 1]+
                                       "; angle_3= "+grain_angles_3[grain_index - 1]+
                                       "; rel_vol_ch= "+rel_vol_changes[cell_counter]);
                                  //   "; sp.ang.df_0="+spec_angle_diff[0][cell_counter]); //+
                                  //   "; ht.mb="+neighb_heat_max_mob[0][cell_counter]+
                                  //   "; mech.mob= "+mechMaxMobility[grain_index-1]+
                                  //   "; sp.bnd.en_0="+spec_bound_energies[0][cell_counter]+
                                  //   "; bnd.en_0="+bound_energies[0][cell_counter]+
                                  //   "; bnd.vl_0="+bound_velocities[0][cell_counter]+
                                  //   "; pr_0="+probs[0][cell_counter]+
                                  //   "; pr.sum = "+prob_sums[neighb1S_number][cell_counter]+
                                  //   "; rand = "+rand_nums[cell_counter]+
                                  //   "; new gr.index = "+grain_index);
                    }
                    
                    // Expected number of cell switches
                    expected_switch_num += prob_sums[neighb1S_number][cell_counter];
                  }
                 
                  long prev_time_step = 0;
                  
                  if(file_number != 0)
                    prev_time_step = (step_count - step_number/file_number + 1);
                  else
                    System.out.println("ERROR!!! file_number = 0; step_count = "+step_count+"; file_number = "+file_number);
                  
                  System.out.println("\nExpected number of cell switches at time step # "+step_count+": "+expected_switch_num);
                  System.out.println("Number of cell switches at time step # "+step_count+":          "+step_switch_counter);
                  System.out.println("\nNumber of cell switches at time steps # "+prev_time_step+" - "+step_count+": "+period_switch_counter);
                  System.out.println("Total number of cell switches: "+switch_counter+"\n===========================================================");
              }
            }
          
            // Variable responsible for calculation of averages
            boolean calc_averages = false;// true;// 
            
            if(calc_averages)
            {
              // variable responsible for execution of method
              boolean createGraphFileJOCL = true;// false;// 
        
              if(createGraphFileJOCL)
              {
                // Name of given task
                String task_name = specArrays.task_file;
        
                // Number of time steps between records to files with results
                int record_period = 500;
        
                // Creation of files with data for building of graphs for given task and given time steps
                createGraphFilesJOCL(task_name, record_period, step_count);
              }
            }
            
            // System.gc();
        }
        
        // Current time
        inter_date = new Date();
        
        // Reading of the output data (test variables) from a buffer object to host memory
       // clEnqueueReadBuffer(commandQueue_0, memObject_step_counter, CL_TRUE, 0, 6*Sizeof.cl_int, step_counter_ptr, 0, null, null);
        
        // Current time
        inter_date_1 = new Date();
        
        // Time period of reading of the output data
        time_period_read += inter_date_1.getTime()- inter_date.getTime();
        
        System.out.println("Period of parallel code execution:      "+time_period_exec+" ms.");
        System.out.println("Period of data reading after execution: "+time_period_read+" ms.");
        System.out.println();
        System.out.println("Global work size:  " + global_work_size[0]);//+" "+global_work_size[1]+" "+global_work_size[2]);
     // System.out.println("Global work size:  " + step_counter[0]);//+" "+ step_counter[1]+" "+ step_counter[2]);
        System.out.println("Local work size:   " + local_work_size[0]);//+" "+local_work_size[1]+" "+local_work_size[2]);
     // System.out.println("Work group number: " + step_counter[3]);//+" "+ step_counter[4]+" "+ step_counter[5]);
        
        // Current time
        inter_date_1 = new Date();
        
        // Release of all memory objects 
        clReleaseMemObject(memObject_temperatures);
        clReleaseMemObject(memObject_heat_influxes);
        clReleaseMemObject(memObject_init_temprs);
        clReleaseMemObject(memObject_heat_caps);
        clReleaseMemObject(memObject_heat_conds);
        clReleaseMemObject(memObject_densities);
        clReleaseMemObject(memObject_en_types);
        clReleaseMemObject(memObject_new_en_types);
        clReleaseMemObject(memObject_loc_types);
        clReleaseMemObject(memObject_new_loc_types);
        clReleaseMemObject(memObject_neighb_id);
        clReleaseMemObject(memObject_factor);
        
        for(int counter = 0; counter<neighb1S_number; counter++)
        {
          clReleaseMemObject(memObject_neighbours1S[counter]);
          clReleaseMemObject(memObject_spec_angle_diff[counter]);
          clReleaseMemObject(memObject_neighb_heat_max_mob[counter]);
          clReleaseMemObject(memObject_spec_bound_energies[counter]);
          clReleaseMemObject(memObject_bound_energies[counter]);
          clReleaseMemObject(memObject_bound_velocities[counter]);
          clReleaseMemObject(memObject_probs[counter]);
          clReleaseMemObject(memObject_prob_sums[counter]);
          clReleaseMemObject(memObject_poss_switches[counter]);
        }
        
        clReleaseMemObject(memObject_prob_sums[neighb1S_number]);
        clReleaseMemObject(memObject_prob_sums[neighb1S_number+1]);
        clReleaseMemObject(memObject_angleHAGB);
        clReleaseMemObject(memObject_grain_angles_1);
        clReleaseMemObject(memObject_grain_angles_2);
        clReleaseMemObject(memObject_grain_angles_3);
        clReleaseMemObject(memObject_max_grain_angles);
        
        clReleaseMemObject(memObject_grain_types);
        clReleaseMemObject(memObject_init_grain_rec_types);
        clReleaseMemObject(memObject_grain_rec_types);
        clReleaseMemObject(memObject_root_grain_indices);
        clReleaseMemObject(memObject_twin_emb_root_gr_indices);
        
        clReleaseMemObject(memObject_init_grain_indices);
        clReleaseMemObject(memObject_grain_indices);
        
        clReleaseMemObject(memObject_bound_cell_time_function_types);
        clReleaseMemObject(memObject_bound_cell_load_time_portions);
        clReleaseMemObject(memObject_bound_cell_relax_time_portions);
        clReleaseMemObject(memObject_step_counter);
        clReleaseMemObject(memObject_next_step_counter);
        
        clReleaseMemObject(memObject_heatMaxMobility);
        clReleaseMemObject(memObject_dislMaxMobility);
        clReleaseMemObject(memObject_mechMaxMobility);
        
        clReleaseMemObject(memObject_phonon_portions);
        clReleaseMemObject(memObject_disl_energies);
        clReleaseMemObject(memObject_min_disl_energies);
            
        clReleaseMemObject(memObject_maxBoundEnergy);
        clReleaseMemObject(memObject_rand_nums);
        clReleaseMemObject(memObject_heat_exp_coeffs);
        clReleaseMemObject(memObject_init_heat_strains);
        clReleaseMemObject(memObject_heat_strains);
        
        clReleaseMemObject(memObject_init_mech_energies);
        clReleaseMemObject(memObject_bound_mech_influxes);
        clReleaseMemObject(memObject_bound_min_mech_influxes);
        clReleaseMemObject(memObject_bound_max_mech_influxes);
        clReleaseMemObject(memObject_mech_energies);
        
        clReleaseMemObject(memObject_bound_cell_min_temprs);
        clReleaseMemObject(memObject_bound_cell_max_temprs);
        
        clReleaseMemObject(memObject_mod_elast);
        clReleaseMemObject(memObject_mod_shear);
        clReleaseMemObject(memObject_lattice_parameters);
        
        clReleaseMemObject(memObject_init_def_densities);
        clReleaseMemObject(memObject_def_density_changes);
        clReleaseMemObject(memObject_final_def_densities);
        
        clReleaseMemObject(memObject_rel_diss_en_changes);
        clReleaseMemObject(memObject_rel_diss_energies);
        clReleaseMemObject(memObject_rel_diss_en_influxes);
        
        clReleaseMemObject(memObject_coeff_diss);
                
        clReleaseMemObject(memObject_cell_coords_X);
        clReleaseMemObject(memObject_cell_coords_Y);
        clReleaseMemObject(memObject_cell_coords_Z);
        
        for(int mem_obj_counter = 0; mem_obj_counter < 4*neighb1S_number; mem_obj_counter++)
          clReleaseMemObject(memObject_cell_pairs1S[mem_obj_counter]);
        
        clReleaseMemObject(memObject_force_moments_X);
        clReleaseMemObject(memObject_force_moments_Y);
        clReleaseMemObject(memObject_force_moments_Z);
        clReleaseMemObject(memObject_abs_mom);
        
        clReleaseMemObject(memObject_angle_velocity_X);
        clReleaseMemObject(memObject_angle_velocity_Y);
        clReleaseMemObject(memObject_angle_velocity_Z);
        clReleaseMemObject(memObject_abs_angle_velocity);
        
        clReleaseMemObject(memObject_torsion_angle_X);
        clReleaseMemObject(memObject_torsion_angle_Y);
        clReleaseMemObject(memObject_torsion_angle_Z);
        clReleaseMemObject(memObject_abs_torsion_angle);
        
        clReleaseMemObject(memObject_crack_sum);
        
        clReleaseMemObject(memObject_diss_en_influxes);
        clReleaseMemObject(memObject_diss_energies);
        clReleaseMemObject(memObject_init_diss_energies);
        
        clReleaseMemObject(memObject_low_tempr_recryst);
        clReleaseMemObject(memObject_high_tempr_recryst);
        clReleaseMemObject(memObject_low_tempr_twinning);
        clReleaseMemObject(memObject_high_tempr_twinning);
        
        clReleaseMemObject(memObject_max_prob_recryst);
        clReleaseMemObject(memObject_max_prob_twinning);
        clReleaseMemObject(memObject_min_disl_density);
        
        clReleaseMemObject(memObject_prob_new_grain);
        clReleaseMemObject(memObject_prob_twinning);
        
        clReleaseMemObject(memObject_new_grain_embryos);
        clReleaseMemObject(memObject_twin_grain_embryos);
        
        clReleaseMemObject(memObject_lattice_vector_A_lengths);
        clReleaseMemObject(memObject_lattice_vector_B_lengths);
        clReleaseMemObject(memObject_lattice_vector_C_lengths);
        
        clReleaseMemObject(memObject_lattice_anis_coeff);
        clReleaseMemObject(memObject_growth_vectors_coordinates);
        clReleaseMemObject(memObject_crack_values);
        clReleaseMemObject(memObject_simulate_cracks);
        
        for(int mem_obj_counter = 0; mem_obj_counter < 9; mem_obj_counter++)
          clReleaseMemObject(memObject_lattice_vectors[mem_obj_counter]);
        
        for(int mem_obj_counter = 0; mem_obj_counter < neighb1S_number; mem_obj_counter++)
          clReleaseMemObject(memObject_stresses[mem_obj_counter]);
        
        clReleaseMemObject(memObject_stresses_X);
        clReleaseMemObject(memObject_stresses_Y);
        clReleaseMemObject(memObject_stresses_Z);
        
        // Release of kernels and programs
        clReleaseKernel (kernel_0);
        clReleaseProgram(program_0);
        
        clReleaseKernel (kernel_1);
        clReleaseProgram(program_1);
        
        clReleaseKernel (kernel_2);
        clReleaseProgram(program_2);
        
        clReleaseKernel (kernel_3_1);
        clReleaseProgram(program_3_1);
        
        clReleaseKernel (kernel_3_2);
        clReleaseProgram(program_3_2);
        
        clReleaseKernel (kernel_3_3);
        clReleaseProgram(program_3_3);
        
        clReleaseKernel (kernel_4);
        clReleaseProgram(program_4);
        
        clReleaseKernel (kernel_5);
        clReleaseProgram(program_5);
        
        clReleaseKernel (kernel_5_1);
        clReleaseProgram(program_5_1);
        
        clReleaseKernel (kernel_5_2);
        clReleaseProgram(program_5_2);
        
        clReleaseKernel (kernel_6);
        clReleaseProgram(program_6);
        
        clReleaseKernel (kernel_7);
        clReleaseProgram(program_7);
        
        clReleaseKernel (kernel_7_1);
        clReleaseProgram(program_7_1);
        
        clReleaseKernel (kernel_7_2);
        clReleaseProgram(program_7_2);
        
        clReleaseKernel (kernel_7_mom);
        clReleaseProgram(program_7_mom);
        
        clReleaseKernel (kernel_8);
        clReleaseProgram(program_8);
        
        clReleaseKernel (kernel_8_1);
        clReleaseProgram(program_8_1);
        
        clReleaseKernel (kernel_9);
        clReleaseProgram(program_9);
        
        clReleaseKernel (kernel_A);
        clReleaseProgram(program_A);
        
        clReleaseKernel (kernel_B);
        clReleaseProgram(program_B);
        
        clReleaseKernel (kernel_C);
        clReleaseProgram(program_C);
        
        // Release of command queues and context
        for(com_queue_counter = 0; com_queue_counter < com_queue_number; com_queue_counter++)
          clReleaseCommandQueue(commandQueue[com_queue_counter]);
        
      /*
        clReleaseCommandQueue(commandQueue_3_1);
        clReleaseCommandQueue(commandQueue_3_2);
        clReleaseCommandQueue(commandQueue_3_3);
        clReleaseCommandQueue(commandQueue_4);
        clReleaseCommandQueue(commandQueue_5);
        clReleaseCommandQueue(commandQueue_6);
        clReleaseCommandQueue(commandQueue_7);
        clReleaseCommandQueue(commandQueue_7_1);
        clReleaseCommandQueue(commandQueue_7_2);
        clReleaseCommandQueue(commandQueue_8);
        clReleaseCommandQueue(commandQueue_8_1);
        clReleaseCommandQueue(commandQueue_9);
      */
        
        clReleaseContext(context);
        
        // Current time
        Date finish_date = new Date();
        
        // Time period of parallel code execution
        double time_period;
        
        time_period = finish_date.getTime()- date_0.getTime();
        System.out.println("Total period of parallel code execution:  "+time_period+" ms.");
        System.out.println("====================END=====================\n");
        
        // Creation of files for time graphs
        createTimeGraphs((long)0, step_number, 1000, TIME_STEP, TASK_NAME);
      
        System.out.println("\nFiles for time graphs are created!!!\n");
    }
    
    /** The method realizes recording of output data to the file at current time step.
     * @param step_count index of current time step
     */
    private static int writeToFile(long step_count, int file_counter, int max_power, int[] grain_indices, double[] disl_energies, 
                                   byte[] en_types, byte[] loc_types, double[][] cell_coords, double[] init_temprs, double[] mech_energies, 
                                   double[][]force_moments, double[] diss_en_influxes, double[] diss_energies, int new_gr_number, int twin_gr_number,
                                   double[] grain_angles_1, double[] grain_angles_2, double[] grain_angles_3, byte[] grain_types, 
                                   byte[] grain_rec_types, int[] root_grain_indices, 
                                   boolean write_init_grains_file, double[] crack_values, int[] crack_sum, 
                                   double[] stresses_X, double[] stresses_Y, double[] stresses_Z, double[] def_density_changes, double[] final_def_densities,
                                   double[] angle_velocity_X, double[] angle_velocity_Y, double[] angle_velocity_Z, double[] abs_angle_velocity,
                                   double[] torsion_angle_X,  double[] torsion_angle_Y,  double[] torsion_angle_Z,  double[] abs_torsion_angle,
                                   double[] rel_diss_energies, double[] rel_diss_en_changes, double[] rel_diss_en_influxes, double[] mod_elast)
    {
      System.out.println("Time step # "+step_count);
            
      String zeros = "";
      
      // Array of absolute values of stress vectors
      double[] abs_stress = new double[stresses_X.length];
      
      // Recording of output data to the file
      try
      {
        // Counter of current file
        file_counter++;
                    
        // Определение числа нулей перед номером шага в имени выходного файла
        int cur_power;
        
        if(step_count == 0)
          cur_power = 0;
        else
          cur_power = (int)Math.floor(Math.log10(1.0*step_count));
                    
        for(int zero_counter = 0; zero_counter < max_power - cur_power; zero_counter++)
          zeros = zeros +"0";
                    
        // Имя выходного файла
        String file_name   = specArrays.write_file_name+"_"+zeros+step_count+"_jocl.res";
        String file_name_1 = specArrays.write_file_name+"_"+zeros+step_count+"_jocl_stresses.res";
        String file_name_2 = specArrays.write_file_name+"_"+zeros+step_count+"_jocl_torsion.res";
                    
        // Stream for recording to the file
        BufferedWriter bw = new BufferedWriter(new FileWriter(file_name));
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(file_name_1));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(file_name_2));
                    
        bw.write("# Each string contains parameters of corresponding cell: \n" +
                 "# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; \n" +
                 "# 6. temperature; 7. specific elastic energy; 8. value for crack generation; \n" +
                 "# 9-11. 3 components of specific volume force moment vector calculated using neighbour cells at 1st sphere only; \n" +
                 "# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n" +
                 "# 13. current influx of specific dissipated energy; 14. specific dissipated energy.\n");
        
        bw1.write("# Each string contains parameters of corresponding cell: \n" +
                 "# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; \n" +
                 "# 6. defect density; 7. absolute value of stress vector; 8. change of density of defects; \n" +
                 "# 9-11. 3 components of stress vector; \n" +
                 "# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n" +                 
                 "# 13. relative dissipation energy in comparison with total mechanical energy;\n"+
                 "# 14. relative change of dissipation energy in comparison with total mechanical energy;\n"+
           //    "# 15. relative change of dissipation energy in comparison with current influx of total mechanical energy.\n");
                 "# 15. relative change of cell volume at current time step.\n");
        
        bw2.write("# Each string contains parameters of corresponding cell: \n" +
                 "# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; \n" +
                 "# 6-8. 3 components of angle velosity of torsion; \n" +
                 "# 9-11. 3 components of accumulated torsion angle; \n" +
                 "# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n" +
                 "# 13. absolute value of angle velosity of torsion; 14. absolute value of accumulated torsion angle.\n");
        
        int grain_index;
        double disl_density;
        int en_type;
        int loc_type;
        
        // Printing of parameters of cells to the file
        for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
     // for(int cell_counter = 0; cell_counter < work_item_number; cell_counter++)
        { 
            en_type  = en_types[cell_counter];
            loc_type = loc_types[cell_counter];
                  
            grain_index = grain_indices[cell_counter];
            
            // If cell is not in the specimen then its location type is changed.
            if(en_types[cell_counter] != INNER_CELL & en_types[cell_counter] != LAYER_CELL)
            {
              if(loc_types[cell_counter] != OUTER_CELL)
              {   
                if(loc_types[cell_counter] == CRACK_CELL & grain_indices[cell_counter] <= specArrays.init_grain_number - 12)
                  en_type = INNER_CELL;
                
                if(loc_types[cell_counter] != CRACK_CELL)
                {
                  loc_types[cell_counter] = OUTER_CELL;
                  loc_type = OUTER_CELL;
                }
                
                grain_index = specArrays.init_grain_number;
              }
            }
            else
            {
              if(loc_types[cell_counter] == TRANSFORMED_CELL)
              {
                loc_type = BOUNDARY_CELL;
              //  loc_types[cell_counter] = BOUNDARY_CELL;
              }
            }
              
            if(grain_index - 1 < grain_number)
            {
                // Dislocation density of grain is calculated.
                disl_density = -disl_energies[grain_index - 1]/
                                             (specArrays.disl_distr_coeff[grain_index-1]*specArrays.mod_shear[grain_index-1]*
                                              specArrays.lattice_parameter[grain_index-1]*specArrays.lattice_parameter[grain_index-1]);
            }
            else
            {
                disl_density = 0;
                
                System.out.println("ERROR!!! Cell # "+cell_counter+" is located in grain # "+grain_index+" but grain number equals "+grain_number);
            }
            
            // Printing of parameters of current cell to the file
            bw.write(en_type+" "+loc_type+" "+grain_index+" "+
                     cell_coords[0][cell_counter]+" "+cell_coords[1][cell_counter]+" "+cell_coords[2][cell_counter]+" "+
                     init_temprs[cell_counter]+" "+mech_energies[cell_counter]+" " + crack_values[cell_counter]+" "+ // crack_sum[cell_counter]+" "+ // loc_types[cell_counter]+" "+ // 
                     force_moments[0][cell_counter]+" "+force_moments[1][cell_counter]+" "+force_moments[2][cell_counter]+" 0 "+
                     
                     diss_en_influxes[cell_counter]+" "+ //mod_elast[cell_counter]+" "+ // 1.0*loc_types[cell_counter]+" "+ // 
                    
                     diss_energies[cell_counter]+"\n");// " "+prob_new_grain[cell_counter]+" "+prob_twinning[cell_counter]+" "+//
                //  (mech_energies[cell_counter]/mod_elast[grain_index - 1])+" "+disl_density+"\n");// 
                //   prob_sums[neighb1S_number][cell_counter]+"\n");
            
            bw.flush();
                
            // Absolute value of stress vector
            abs_stress[cell_counter] = (double)Math.sqrt(stresses_X[cell_counter]*stresses_X[cell_counter] + 
                                                         stresses_Y[cell_counter]*stresses_Y[cell_counter] + 
                                                         stresses_Z[cell_counter]*stresses_Z[cell_counter]);
            
            // Printing of parameters of current cell to the file
            bw1.write(en_type+" "+loc_type+" "+grain_index+" "+
                     cell_coords[0][cell_counter]+" "+cell_coords[1][cell_counter]+" "+cell_coords[2][cell_counter]+" "+
                     final_def_densities[cell_counter]+" "+abs_stress[cell_counter]+" " + def_density_changes[cell_counter]+" "+ // crack_sum[cell_counter]+" "+ // loc_types[cell_counter]+" "+ // 
                     stresses_X[cell_counter]+" "+stresses_Y[cell_counter]+" "+stresses_Z[cell_counter]+" 0 "+
                     rel_diss_energies[cell_counter]+" "+rel_diss_en_changes[cell_counter]+" "+rel_diss_en_influxes[cell_counter]+"\n");
                //   rel_diss_energies[cell_counter]+" "+rel_vol_changes[cell_counter]+" "+rel_diss_en_influxes[cell_counter]+"\n");
                  
            bw1.flush();
            
            // Printing of parameters of current cell to the file
            bw2.write(en_type+" "+loc_type+" "+grain_index+" "+
                     cell_coords[0][cell_counter]+" "+cell_coords[1][cell_counter]+" "+cell_coords[2][cell_counter]+" "+
                     angle_velocity_X[cell_counter]+" "+angle_velocity_Y[cell_counter]+" "+angle_velocity_Z[cell_counter]+" "+ 
                     torsion_angle_X[cell_counter] +" "+torsion_angle_Y[cell_counter] +" "+torsion_angle_Z[cell_counter] +" 0 "+
                     abs_angle_velocity[cell_counter]+" "+abs_torsion_angle[cell_counter]+"\n");
            
            bw2.flush();
        }
           
        bw.close();
        bw1.close();
        bw2.close();
                
        System.out.println("File # "+file_counter+" ("+file_name+") is created.\n");
        System.out.println("File # "+file_counter+" ("+file_name_1+") is created.\n");
        System.out.println("File # "+file_counter+" ("+file_name_2+") is created.\n");
        System.out.println("----------------------------------------------------------");
                
        try
        {
          if(write_init_grains_file)
          {
            // Writing of data about initial grain structure to the file
            BufferedReader br_grains     = new BufferedReader(new FileReader(specArrays.write_file_name+".grn"));
            BufferedWriter bw_old_grains = new BufferedWriter(new FileWriter(specArrays.write_file_name+"_init.grn"));
            
          //  write_init_grains_file = false;
            String string;
            
            while(br_grains.ready())
            {
              string = br_grains.readLine();
              bw_old_grains.write(string+"\n");
              bw_old_grains.flush();
            }
            
            br_grains.close();
            bw_old_grains.close();
          }
            
            // Creation of file with information about grain parameters
         //   BufferedWriter bw_grains = new BufferedWriter(new FileWriter(specArrays.write_file_name+"_all_grains.grn"));
            
            BufferedWriter bw_grains = new BufferedWriter(new FileWriter(specArrays.write_file_name+".grn"));
                  
            bw_grains.write("#   This file contains parameters of each cluster of cells -\n" +
                            "# index, material, 3 Euler angles, dislocation density, average dislocation density, its maximal deviation, type:\n" +
                            "#   0 - cluster consists of inner cells located in surface or intermediate layers,\n" +
                            "#   1 - cluster consists of inner cells located in substrate,\n" +
                            "#   3 - cluster consists of outer boundary cells adiabatic for mechanical energy and possessing constant temperature,\n" +
                            "#   4 - cluster consists of outer boundary cells adiabatic for mechanical energy and effected by constant thermal energy influx,\n" +
                            "#   5 - cluster consists of outer boundary cells possessing constant strain rate and adiabatic for thermal energy,\n" +
                            "#   6 - cluster consists of outer boundary cells possessing constant strain rate and temperature,\n" +
                            "#   7 - cluster consists of outer boundary cells possessing constant strain rate and effected by constant thermal energy influx,\n" +
                            "#   8 - cluster consists of outer boundary cells possessing constant stress and adiabatic for thermal energy,\n" +
                            "#   9 - cluster consists of outer boundary cells possessing constant stress and temperature,\n" +
                            "#  10 - cluster consists of outer boundary cells possessing constant stress and effected by constant thermal energy influx,\n" +
                            "#  11 - cluster consists of outer boundary cells adiabatic for both mechanical and thermal parts of energy;\n" +
                            "# type of grain according to its role in recrystallization process: 0 - initial grain, 1 - new grain, 2 - twinning grain;\n" +
                            "# index of grain, from which twinning grain grows.\n");
                  
            for(int gr_counter = 1; gr_counter <= specArrays.init_grain_number + new_gr_number + twin_gr_number; gr_counter++)
            {
                bw_grains.write(gr_counter+" "+specArrays.materials[gr_counter-1]+" "+grain_angles_1[gr_counter - 1]+" "+grain_angles_2[gr_counter - 1]+" "+
                                    grain_angles_2[gr_counter - 1]+" "+specArrays.grainDislDensities[gr_counter - 1]+" "+specArrays.grainAverageDislDensities[gr_counter - 1]+" "+
                                    specArrays.grainDislDensityDeviations[gr_counter - 1]+" "+grain_types[gr_counter - 1]+" "+grain_rec_types[gr_counter - 1]+" "+
                                    root_grain_indices[gr_counter - 1]+"\n");
                    
                bw_grains.flush();
            }
                  
            bw_grains.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("ERROR!!! IOException: "+io_exc);
        }
        
        if(false)
        if(step_count % 5000 == 0)
        {
            // Archivation of the file
            // input file 
            FileInputStream in  = new FileInputStream(file_name);
            //   ZipOutputStream out = new ZipOutputStream(new FileOutputStream(specArrays.write_file_name+"_jocl.zip"));
              
            // if(step_count%5000 == 0)
              
            // output file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(specArrays.write_file_name+"_"+zeros+step_count+"_jocl.zip"));
                  
            // name the file inside the zip file
            out.putNextEntry(new ZipEntry(TASK_NAME+"_"+zeros+step_count+"_jocl.txt")); 
            
            // buffer size
            byte[] b = new byte[1024];
            int count;
             
            while ((count = in.read(b)) > 0) 
            {
                out.write(b, 0, count);
                out.flush();
            }
             
            in.close();
                  
            if(step_count % 5000 == 0)
              out.close();
        }
        
        // Expected number of cell switches
        double expected_switch_num = 0;
                 
        System.out.println("===========================================================");
        expected_switch_num = 0;
      }
      catch(IOException io_exc)
      {
        System.out.println("IOException: " + io_exc);
      }
      
      // The variable responsible for start of method for analyzing of results
      boolean analyze_results = false; // true; // 
      
      if(step_count > 0)
        analyze_results = true;
              
      try
      { 
     //   if(step_count == 100)
        if(file_counter == 1)
        {
          // Buffered writers for parameters from "<...>_jocl.res"
          bw_temperatures          = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_temperatures.txt"));
          bw_temperatures.write("0.0 0.0\n");
          bw_temperatures.flush();
          
          bw_spec_elast_energies   = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_spec_elast_energies.txt"));
          bw_spec_elast_energies.write("0.0 0.0\n");
          bw_spec_elast_energies.flush();
          
          bw_crack_values          = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_crack_values.txt"));
          bw_crack_values.write("0.0 0.0\n");
          bw_crack_values.flush();
          
          bw_force_moments_X       = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_force_moments_X.txt"));
          bw_force_moments_X.write("0.0 0.0\n");
          bw_force_moments_X.flush();
          
          bw_force_moments_Y       = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_force_moments_Y.txt"));
          bw_force_moments_Y.write("0.0 0.0\n");
          bw_force_moments_Y.flush();
          
          bw_force_moments_Z       = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_force_moments_Z.txt"));
          bw_force_moments_Z.write("0.0 0.0\n");
          bw_force_moments_Z.flush();
          
          bw_spec_diss_energies    = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_spec_diss_energies.txt"));
          bw_spec_diss_energies.write("0.0 0.0\n");
          bw_spec_diss_energies.flush();
          
          bw_spec_diss_en_influxes = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_spec_diss_en_influxes.txt"));
          bw_spec_diss_en_influxes.write("0.0 0.0\n");
          bw_spec_diss_en_influxes.flush();
          
          bw_rel_total_diss_energies = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_rel_total_diss_energies.txt"));
          bw_rel_total_diss_energies.write("0.0 0.0\n");
          bw_rel_total_diss_energies.flush();
          
          // Buffered writers for parameters from "<...>_jocl_stresses.res"
          bw_def_densities         = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_def_densities.txt"));
          bw_def_densities.write("0.0 0.0\n");
          bw_def_densities.flush();
          
          bw_abs_stresses          = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_abs_stresses.txt"));
          bw_abs_stresses.write("0.0 0.0\n");
          bw_abs_stresses.flush();
          
          bw_def_density_changes   = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_def_density_changes.txt"));
          bw_def_density_changes.write("0.0 0.0\n");
          bw_def_density_changes.flush();
          
          bw_stresses_X            = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_stresses_X.txt"));
          bw_stresses_X.write("0.0 0.0\n");
          bw_stresses_X.flush();
          
          bw_stresses_Y            = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_stresses_Y.txt"));
          bw_stresses_Y.write("0.0 0.0\n");
          bw_stresses_Y.flush();
          
          bw_stresses_Z            = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_stresses_Z.txt"));
          bw_stresses_Z.write("0.0 0.0\n");
          bw_stresses_Z.flush();
          
          bw_rel_diss_energies     = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_rel_diss_energies.txt"));
          bw_rel_diss_energies.write("0.0 0.0\n");
          bw_rel_diss_energies.flush();
          
          bw_rel_diss_en_influxes  = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_rel_diss_en_influxes.txt"));
          bw_rel_diss_en_influxes.write("0.0 0.0\n");
          bw_rel_diss_en_influxes.flush();
          
          bw_rel_diss_en_changes   = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_rel_diss_en_changes.txt"));
          bw_rel_diss_en_changes.write("0.0 0.0\n");
          bw_rel_diss_en_changes.flush();
        
          // Buffered writers for parameters from "<...>_jocl_torsion.res"
          bw_torsion_velocity_X    = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_torsion_velocity_X.txt"));
          bw_torsion_velocity_X.write("0.0 0.0\n");
          bw_torsion_velocity_X.flush();
          
          bw_torsion_velocity_Y    = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_torsion_velocity_Y.txt"));
          bw_torsion_velocity_Y.write("0.0 0.0\n");
          bw_torsion_velocity_Y.flush();
          
          bw_torsion_velocity_Z    = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_torsion_velocity_Z.txt"));
          bw_torsion_velocity_Z.write("0.0 0.0\n");
          bw_torsion_velocity_Z.flush();
          
          bw_torsion_angle_X       = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_torsion_angle_X.txt"));
          bw_torsion_angle_X.write("0.0 0.0\n");
          bw_torsion_angle_X.flush();
          
          bw_torsion_angle_Y       = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_torsion_angle_Y.txt"));
          bw_torsion_angle_Y.write("0.0 0.0\n");
          bw_torsion_angle_Y.flush();
          
          bw_torsion_angle_Z       = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_torsion_angle_Z.txt"));
          bw_torsion_angle_Z.write("0.0 0.0\n");
          bw_torsion_angle_Z.flush();
          
          bw_abs_torsion_velocity  = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_abs_torsion_velocity.txt"));
          bw_abs_torsion_velocity.write("0.0 0.0\n");
          bw_abs_torsion_velocity.flush();
          
          bw_abs_torsion_angle     = new BufferedWriter(new FileWriter("./user/task_db/"+TASK_NAME+"/graphs/"+TASK_NAME+"_aver_abs_torsion_angle.txt"));
          bw_abs_torsion_angle.write("0.0 0.0\n");
          bw_abs_torsion_angle.flush();
        }
        
        String graph_file_name;
        
        if(analyze_results)
        {
          // Creation of files with distribution of parameters from "<...>_jocl.res"
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/temperatures/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, init_temprs,      "temperatures", bw_temperatures);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/spec_elast_energies/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, mech_energies,    "spec_elast_energies", bw_spec_elast_energies);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/crack_values/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, crack_values,     "crack_values", bw_crack_values);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/force_moments_X/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, force_moments[0], "force_moments_X", bw_force_moments_X);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/force_moments_Y/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, force_moments[1], "force_moments_Y", bw_force_moments_Y);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/force_moments_Z/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, force_moments[2], "force_moments_Z", bw_force_moments_Z);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/spec_diss_energies/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, diss_energies,    "spec_diss_energies", bw_spec_diss_energies);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/spec_diss_en_influxes/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, diss_en_influxes, "spec_diss_en_influxes", bw_spec_diss_en_influxes);
          
          // Creation of files with distribution of parameters from "<...>_jocl_stresses.res"
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/def_densities/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, final_def_densities, "def_densities", bw_def_densities);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/abs_stresses/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, abs_stress,          "abs_stresses", bw_abs_stresses);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/def_density_changes/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, def_density_changes, "def_density_changes", bw_def_density_changes);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/stresses_X/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, stresses_X,          "stresses_X", bw_stresses_X);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/stresses_Y/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, stresses_Y,          "stresses_Y", bw_stresses_Y);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/stresses_Z/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, stresses_Z,          "stresses_Z", bw_stresses_Z);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/rel_diss_energies/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, rel_diss_energies,   "rel_diss_energies", bw_rel_diss_energies);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/rel_diss_en_changes/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, rel_diss_en_changes, "rel_diss_en_changes", bw_rel_diss_en_changes);
          
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/rel_diss_en_influxes/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, rel_diss_en_influxes,"rel_diss_en_influxes", bw_rel_diss_en_influxes);
        
          // Creation of files with distribution of parameters from "<...>_jocl_torsion.res"
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/torsion_velocity_X/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, angle_velocity_X,   "torsion_velocity_X", bw_torsion_velocity_X);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/torsion_velocity_Y/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, angle_velocity_Y,   "torsion_velocity_Y", bw_torsion_velocity_Y);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/torsion_velocity_Z/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, angle_velocity_Z,   "torsion_velocity_Z", bw_torsion_velocity_Z);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/torsion_angle_X/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, torsion_angle_X,    "torsion_angle_X", bw_torsion_angle_X);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/torsion_angle_Y/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, torsion_angle_Y,    "torsion_angle_Y", bw_torsion_angle_Y);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/torsion_angle_Z/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, torsion_angle_Z,    "torsion_angle_Z", bw_torsion_angle_Z);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/abs_torsion_velocity/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, abs_angle_velocity, "abs_torsion_velocity", bw_abs_torsion_velocity);
        
          graph_file_name = "./user/task_db/"+TASK_NAME+"/graphs/abs_torsion_angle/"+TASK_NAME+"_"+zeros+step_count;
          analyzeResults(graph_file_name, step_count, TIME_STEP, en_types, loc_types, abs_torsion_angle,  "abs_torsion_angle", bw_abs_torsion_angle);
        }
      }
      catch(IOException io_exc)
      {
        System.out.println("Error in method Paral_Calc_SECAM.writeToFile(): "+io_exc);
      }
      
      return file_counter;
    }
    
    /* The method creates time graphs for all output parameters
    */
    private static void createTimeGraphs(long min_time_step_index, long max_time_step_index, int time_step_period, double time_step_value, String task_name)
    {        
      // Exponent of power of 10 in standard representation
      // of total number of time steps
      int max_exponent = (int)Math.floor(Math.log10(1.0*max_time_step_index));
      
      // Exponent of power of 10 in standard representation of number of current time step
      int exponent = 0;
      
      // String of zeros
      String zeros = "";
      
      // Current string from file with output data
      String curr_string;
      
      // Word from current string
      String token;
      
      StringTokenizer st;
      
      // Names of files with output parameters
      String file_name_jocl_res;
      String file_name_jocl_stresses_res;
      String file_name_jocl_torsion_res;
      
      // Number of cells in specimen
      int cell_number = 0;
      
      // Values of parameters
      int energy_type, location_type, grain_index;
      double coord_X, coord_Y, coord_Z;
      
      // Parameters from "<...>_jocl.res"
      double temperature,         spec_elast_energy,  crack_value;
      double spec_force_mom_X,    spec_force_mom_Y,   spec_force_mom_Z;
      byte state;
      double spec_diss_en_influx, spec_diss_energy;
      
      // Parameters from "<...>_jocl_stresses.res"
      double defect_density,      abs_stress,         def_density_change;
      double stress_X,            stress_Y,           stress_Z;
      double rel_diss_energy,     rel_diss_en_change, rel_diss_en_influx;
      
      // Parameters from "<...>_jocl_torsion.res"
      double tors_velocity_X,     tors_velocity_Y,    tors_velocity_Z;
      double tors_angle_X,        tors_angle_Y,       tors_angle_Z;
      double abs_tors_velocity,   abs_tors_angle;
      
      // Average values of parameters from "<...>_jocl.res"
      double aver_temperature = 0,         aver_spec_elast_energy = 0,  aver_crack_value = 0;
      double aver_spec_force_mom_X = 0,    aver_spec_force_mom_Y = 0,   aver_spec_force_mom_Z = 0;
      double aver_spec_diss_en_influx = 0, aver_spec_diss_energy = 0;
      
      // Average values of parameters from "<...>_jocl_stresses.res"
      double aver_defect_density = 0,      aver_abs_stress = 0,         aver_def_density_change = 0;
      double aver_stress_X = 0,            aver_stress_Y = 0,           aver_stress_Z = 0;
      double aver_rel_diss_energy = 0,     aver_rel_diss_en_change = 0, aver_rel_diss_en_influx = 0;
      
      // Average values of parameters from "<...>_jocl_torsion.res"
      double aver_tors_velocity_X = 0,     aver_tors_velocity_Y = 0,    aver_tors_velocity_Z = 0;
      double aver_tors_angle_X = 0,        aver_tors_angle_Y = 0,       aver_tors_angle_Z = 0;
      double aver_abs_tors_velocity = 0,   aver_abs_tors_angle = 0;
      
      double total_mech_energy = 0.0;
      double rel_total_diss_energy = 0;
      
      try
      {
        // Creation of file for graphs of parameters from files "<...>_jocl.res" 
        String graphs_jocl_res     = "./user/task_db/"+task_name+"/"+task_name+"_graphs_jocl.chart";
        BufferedWriter bw_jocl_res = new BufferedWriter(new FileWriter(graphs_jocl_res));
        
        bw_jocl_res.write("# Average values of parameters at time steps:\n" +
                          "# 0. Time Step, s;\n" +
                          "# 1. Average Temperature, K;\n" +
                          "# 2. Average Specific Elastic Energy, Pa;\n" +
                          "# 3. Average crack parameter;\n" +
                          "# 4. Average Component X of Specific Force Moment Vector, Pa;\n" +
                          "# 5. Average Component Y of Specific Force Moment Vector, Pa;\n" +
                          "# 6. Average Component Z of Specific Force Moment Vector, Pa;\n" +
                          "# 7. Average Current Influx Of Specific Dissipated Energy, Pa;\n" +
                          "# 8. Average Specific Dissipated Energy, Pa.\n");
        
        for(long time_step_index = min_time_step_index; time_step_index <= max_time_step_index; time_step_index += time_step_period)
        {
          zeros = "";
            
          if(time_step_index > 0)
            exponent = (int)Math.floor(Math.log10(1.0*time_step_index));
          else
            exponent = 0;
            
          for(int zero_counter = exponent; zero_counter < max_exponent; zero_counter++)
            zeros = zeros + "0";
        
          file_name_jocl_res          = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_index+"_jocl.res";
          BufferedReader br_jocl_res  = new BufferedReader(new FileReader(file_name_jocl_res));
          
          // Average values of parameters from "<...>_jocl.res"
          aver_temperature         = 0;
          aver_spec_elast_energy   = 0;
          aver_crack_value         = 0;
          aver_spec_force_mom_X    = 0;
          aver_spec_force_mom_Y    = 0;
          aver_spec_force_mom_Z    = 0;
          aver_spec_diss_en_influx = 0;
          aver_spec_diss_energy    = 0;
          
          // Number of cells in specimen
          cell_number = 0;
          
          while(br_jocl_res.ready())
          {
             curr_string = br_jocl_res.readLine();
             
             st = new StringTokenizer(curr_string);
             
             if(st.hasMoreTokens())
             {
               token = st.nextToken();
               
               if(!token.equals("#"))
               {
                 energy_type   = (new Integer(token)).intValue();
                 location_type = (new Integer(st.nextToken())).intValue();
                 grain_index   = (new Integer(st.nextToken())).intValue();
                 coord_X       = (new Double(st.nextToken())).doubleValue();
                 coord_Y       = (new Double(st.nextToken())).doubleValue();
                 coord_Z       = (new Double(st.nextToken())).doubleValue();
                 
                 if(energy_type != ADIABATIC_ADIABATIC_CELL & location_type != OUTER_CELL)
                 {
                   cell_number++;
                     
                   // Values of parameters from "<...>_jocl.res"
                   temperature         = (new Double(st.nextToken())).doubleValue(); 
                   spec_elast_energy   = (new Double(st.nextToken())).doubleValue(); 
                   crack_value         = (new Double(st.nextToken())).doubleValue();
                   spec_force_mom_X    = (new Double(st.nextToken())).doubleValue();
                   spec_force_mom_Y    = (new Double(st.nextToken())).doubleValue(); 
                   spec_force_mom_Z    = (new Double(st.nextToken())).doubleValue();
                   state               = (new Byte(token)).byteValue();
                   spec_diss_en_influx = (new Double(st.nextToken())).doubleValue(); 
                   spec_diss_energy    = (new Double(st.nextToken())).doubleValue();
                   
                   // Average values of parameters from "<...>_jocl.res"
                   aver_temperature         += temperature;
                   aver_spec_elast_energy   += spec_elast_energy;
                   aver_crack_value         += crack_value;
                   aver_spec_force_mom_X    += spec_force_mom_X;
                   aver_spec_force_mom_Y    += spec_force_mom_Y;
                   aver_spec_force_mom_Z    += spec_force_mom_Z;
                   aver_spec_diss_en_influx += spec_diss_en_influx;
                   aver_spec_diss_energy    += spec_diss_energy;
                 }
               }
             }
          }
          
          if(cell_number != 0)
          {
            aver_temperature         = aver_temperature/cell_number;
            aver_spec_elast_energy   = aver_spec_elast_energy/cell_number;
            aver_crack_value         = aver_crack_value/cell_number;
            aver_spec_force_mom_X    = aver_spec_force_mom_X/cell_number;
            aver_spec_force_mom_Y    = aver_spec_force_mom_Y/cell_number;
            aver_spec_force_mom_Z    = aver_spec_force_mom_Z/cell_number;
            aver_spec_diss_en_influx = aver_spec_diss_en_influx/cell_number;
            aver_spec_diss_energy    = aver_spec_diss_energy/cell_number;
          }
          
          bw_jocl_res.write(time_step_index*time_step_value+" "+aver_temperature+" "+aver_spec_elast_energy+" "+aver_crack_value+" "+
                            aver_spec_force_mom_X+" "+aver_spec_force_mom_Y+" "+aver_spec_force_mom_Z+" "+aver_spec_diss_en_influx+" "+aver_spec_diss_energy+"\n");
          
          bw_jocl_res.flush();
          
          total_mech_energy = (Math.abs(aver_spec_elast_energy) + aver_spec_diss_energy);
          
          if(total_mech_energy > 0)
            rel_total_diss_energy = Math.abs(aver_spec_diss_energy/total_mech_energy);
          else
            rel_total_diss_energy = 0.0;
                  
          bw_rel_total_diss_energies.write(time_step_index*time_step_value+" "+rel_total_diss_energy+"\n");
          bw_rel_total_diss_energies.flush();
        }
        
        bw_rel_total_diss_energies.close();
        
        //-------------------------------------------------------------------------------------------------------------
        // Creation of file for graphs of parameters from files "<...>_jocl_stresses.res" 
        String graphs_jocl_stresses_res     = "./user/task_db/"+task_name+"/"+task_name+"_graphs_jocl_stresses.chart";
        BufferedWriter bw_jocl_stresses_res = new BufferedWriter(new FileWriter(graphs_jocl_stresses_res));
        
        bw_jocl_stresses_res.write("# Average Values of Specimen Parameters at time steps:\n" +
                                     "# 0. Time Step, s;\n" +
                                     "# 1. Average density of defects, 1/m^2;\n" +
                                     "# 2. Average Absolute Value of Stress Vector, Pa;\n" +
                                     "# 3. Average change of density of defects, 1/m^2;\n" +
                                     "# 4. Average Component X of Stress Vector, Pa;\n" +
                                     "# 5. Average Component Y of Stress Vector, Pa;\n" +
                                     "# 6. Average Component Z of Stress Vector, Pa;\n" +
                                     "# 7. Average Dissipated Energy relative to the whole elastic energy;\n" +
                                     "# 8. Average Change of Dissipated Energy relative to the whole elastic energy;\n"+
                                     "# 9. Average Change of Dissipated Energy relative to current influx of total elastic energy.\n");
        
        for(long time_step_index = min_time_step_index; time_step_index <= max_time_step_index; time_step_index += time_step_period)
        {
          zeros = "";
            
          if(time_step_index > 0)
            exponent = (int)Math.floor(Math.log10(1.0*time_step_index));
          else
            exponent = 0;
            
          for(int zero_counter = exponent; zero_counter < max_exponent; zero_counter++)
            zeros = zeros + "0";
        
          file_name_jocl_stresses_res = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_index+"_jocl_stresses.res";
          BufferedReader br_jocl_stresses_res = new BufferedReader(new FileReader(file_name_jocl_stresses_res));
          
          // Number of cells in specimen
          cell_number = 0;
          
          // Average values of parameters from "<...>_jocl_stresses.res"
          aver_defect_density     = 0; 
          aver_abs_stress         = 0; 
          aver_def_density_change = 0;
          aver_stress_X           = 0; 
          aver_stress_Y           = 0; 
          aver_stress_Z           = 0;
          aver_rel_diss_energy    = 0; 
          aver_rel_diss_en_change = 0; 
          aver_rel_diss_en_influx = 0;
          
          while(br_jocl_stresses_res.ready())
          {
             curr_string = br_jocl_stresses_res.readLine();
             
             st = new StringTokenizer(curr_string);
             
             if(st.hasMoreTokens())
             {
               token = st.nextToken();
               
               if(!token.equals("#"))
               {
                 energy_type   = (new Integer(token)).intValue();
                 location_type = (new Integer(st.nextToken())).intValue();
                 grain_index   = (new Integer(st.nextToken())).intValue();
                 coord_X       = (new Double(st.nextToken())).doubleValue();
                 coord_Y       = (new Double(st.nextToken())).doubleValue();
                 coord_Z       = (new Double(st.nextToken())).doubleValue();
                 
                 if(energy_type != ADIABATIC_ADIABATIC_CELL & location_type != OUTER_CELL)
                 {
                   cell_number++;
                     
                   // Values of parameters from "<...>_jocl_stresses.res"
                   defect_density      = (new Double(st.nextToken())).doubleValue(); 
                   abs_stress          = (new Double(st.nextToken())).doubleValue(); 
                   def_density_change  = (new Double(st.nextToken())).doubleValue();
                   stress_X            = (new Double(st.nextToken())).doubleValue();
                   stress_Y            = (new Double(st.nextToken())).doubleValue(); 
                   stress_Z            = (new Double(st.nextToken())).doubleValue();
                   state               = (new Byte(token)).byteValue();
                   rel_diss_energy     = (new Double(st.nextToken())).doubleValue(); 
                   rel_diss_en_change  = (new Double(st.nextToken())).doubleValue();
                   rel_diss_en_influx  = (new Double(st.nextToken())).doubleValue();
                   
                   // Average values of parameters from "<...>_jocl_stresses.res"
                   aver_defect_density     += defect_density;
                   aver_abs_stress         += abs_stress;
                   aver_def_density_change += def_density_change;
                   aver_stress_X           += stress_X;
                   aver_stress_Y           += stress_Y;
                   aver_stress_Z           += stress_Z;
                   aver_rel_diss_energy    += rel_diss_energy;
                   aver_rel_diss_en_change += rel_diss_en_change;
                   aver_rel_diss_en_influx += rel_diss_en_influx;
                 }
               }
             }
          }
          
          if(cell_number != 0)
          {
            aver_defect_density     = aver_defect_density/cell_number;
            aver_abs_stress         = aver_abs_stress/cell_number;
            aver_def_density_change = aver_def_density_change/cell_number;
            aver_stress_X           = aver_stress_X/cell_number;
            aver_stress_Y           = aver_stress_Y/cell_number;
            aver_stress_Z           = aver_stress_Z/cell_number;
            aver_rel_diss_energy    = aver_rel_diss_energy/cell_number;
            aver_rel_diss_en_change = aver_rel_diss_en_change/cell_number;
            aver_rel_diss_en_influx = aver_rel_diss_en_influx/cell_number;
          }
          
          bw_jocl_stresses_res.write(time_step_index*time_step_value+" "+aver_defect_density+" "+aver_abs_stress+" "+aver_def_density_change+" "+
                            aver_stress_X+" "+aver_stress_Y+" "+aver_stress_Z+" "+aver_rel_diss_energy+" "+aver_rel_diss_en_change+" "+aver_rel_diss_en_influx+"\n");
          
          bw_jocl_stresses_res.flush();
        }
        
        //-------------------------------------------------------------------------------------------------------------------------
        // Creation of file for graphs of parameters from files "<...>_jocl_torsion.res" 
        String graphs_jocl_torsion_res     = "./user/task_db/"+task_name+"/"+task_name+"_graphs_jocl_torsion.chart";
        BufferedWriter bw_jocl_torsion_res = new BufferedWriter(new FileWriter(graphs_jocl_torsion_res));
        
        bw_jocl_torsion_res.write(  "# Average Values of Specimen Parameters at time steps:\n" +
                                    "# 0. Time Step, s;\n" +
                                    "# 1. Average Component X of Torsion Velocity, rad/s;\n" +
                                    "# 2. Average Component Y of Torsion Velocity, rad/s;\n" +
                                    "# 3. Average Component Z of Torsion Velocity, rad/s;\n" +
                                    "# 4. Average Component X of Torsion Angle Vector, rad;\n" +
                                    "# 5. Average Component Y of Torsion Angle Vector, rad;\n" +
                                    "# 6. Average Component Z of Torsion Angle Vector, rad;\n" +
                                    "# 7. Average absolute value of Torsion Velocity, rad/s;\n" +
                                    "# 8. Average absolute value of Torsion Angle Vector, rad.\n");
        
        for(long time_step_index = min_time_step_index; time_step_index <= max_time_step_index; time_step_index += time_step_period)
        {
          zeros = "";
            
          if(time_step_index > 0)
            exponent = (int)Math.floor(Math.log10(1.0*time_step_index));
          else
            exponent = 0;
            
          for(int zero_counter = exponent; zero_counter < max_exponent; zero_counter++)
            zeros = zeros + "0";
          
          file_name_jocl_torsion_res  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_index+"_jocl_torsion.res";
          BufferedReader br_jocl_torsion_res  = new BufferedReader(new FileReader(file_name_jocl_torsion_res));
          
          // Number of cells in specimen
          cell_number = 0;
          
          // Average values of parameters from "<...>_jocl_torsion.res"
          aver_tors_velocity_X   = 0;
          aver_tors_velocity_Y   = 0;
          aver_tors_velocity_Z   = 0;
          aver_tors_angle_X      = 0;
          aver_tors_angle_Y      = 0;
          aver_tors_angle_Z      = 0;
          aver_abs_tors_velocity = 0;
          aver_abs_tors_angle    = 0;
          
          while(br_jocl_torsion_res.ready())
          {
             curr_string = br_jocl_torsion_res.readLine();
             
             st = new StringTokenizer(curr_string);
             
             if(st.hasMoreTokens())
             {
               token = st.nextToken();
               
               if(!token.equals("#"))
               {
                 energy_type   = (new Integer(token)).intValue();
                 location_type = (new Integer(st.nextToken())).intValue();
                 grain_index   = (new Integer(st.nextToken())).intValue();
                 coord_X       = (new Double(st.nextToken())).doubleValue();
                 coord_Y       = (new Double(st.nextToken())).doubleValue();
                 coord_Z       = (new Double(st.nextToken())).doubleValue();
                 
                 if(energy_type != ADIABATIC_ADIABATIC_CELL & location_type != OUTER_CELL)
                 {
                   cell_number++;
                     
                   // Values of parameters from "<...>_jocl_torsion.res"
                   tors_velocity_X   = (new Double(st.nextToken())).doubleValue(); 
                   tors_velocity_Y   = (new Double(st.nextToken())).doubleValue(); 
                   tors_velocity_Z   = (new Double(st.nextToken())).doubleValue();
                   tors_angle_X      = (new Double(st.nextToken())).doubleValue();
                   tors_angle_Y      = (new Double(st.nextToken())).doubleValue(); 
                   tors_angle_Z      = (new Double(st.nextToken())).doubleValue();
                   state             = (new Byte(token)).byteValue();
                   abs_tors_velocity = (new Double(st.nextToken())).doubleValue(); 
                   abs_tors_angle    = (new Double(st.nextToken())).doubleValue();
                   
                   // Average values of parameters from "<...>_jocl_torsion.res"
                   aver_tors_velocity_X   += tors_velocity_X;
                   aver_tors_velocity_Y   += tors_velocity_Y;
                   aver_tors_velocity_Z   += tors_velocity_Z;
                   aver_tors_angle_X      += tors_angle_X;
                   aver_tors_angle_Y      += tors_angle_Y;
                   aver_tors_angle_Z      += tors_angle_Z;
                   aver_abs_tors_velocity += abs_tors_velocity;
                   aver_abs_tors_angle    += abs_tors_angle;
                 }
               }
             }
          }
          
          if(cell_number != 0)
          {
            aver_tors_velocity_X   = aver_tors_velocity_X/cell_number;
            aver_tors_velocity_Y   = aver_tors_velocity_Y/cell_number;
            aver_tors_velocity_Z   = aver_tors_velocity_Z/cell_number;
            aver_tors_angle_X      = aver_tors_angle_X/cell_number;
            aver_tors_angle_Y      = aver_tors_angle_Y/cell_number;
            aver_tors_angle_Z      = aver_tors_angle_Z/cell_number;
            aver_abs_tors_velocity = aver_abs_tors_velocity/cell_number;
            aver_abs_tors_angle    = aver_abs_tors_angle/cell_number;
          }
          
          bw_jocl_torsion_res.write(time_step_index*time_step_value+" "+aver_tors_velocity_X+" "+aver_tors_velocity_Y+" "+aver_tors_velocity_Z+" "+
                            aver_tors_angle_X+" "+aver_tors_angle_Y+" "+aver_tors_angle_Z+" "+aver_abs_tors_velocity+" "+aver_abs_tors_angle+"\n");
          
          bw_jocl_torsion_res.flush();
        }
        
        bw_jocl_res.close();
        bw_jocl_stresses_res.close();
        bw_jocl_torsion_res.close();
      }
      catch(IOException io_exc)
      {
        System.out.println("Error in method ParalCalc_SECAM.createTimeGraphs(): "+io_exc);
      }
    }
    
    /** The method builds the graph of distribution of data.
     * @param data array of output data
     */
    private static void analyzeResults(String file_name, long step_count, double time_step_value, byte[] en_types, byte[] loc_types, double[] data, String param_name, BufferedWriter bw_param)
    {
      // Boolean value responsible for calculation of distribution of parameters
      boolean calc_distribution = false; // true; //
      
      if(step_count == STEP_NUMBER)
          calc_distribution = true;
        
      // size of array of output data
      int data_size = loc_types.length;
      
      // number of data under analyze
      int data_number = 0;
      
      // Minimal and maximal values for array of output data
      double min = 0;
      double max = 0;
      
      // Number of intervals
      int interval_number = 100;
      
      // array of numbers of data contained in intervals
      int[] data_in_intervals = new int[interval_number];
      
      // array of ticks for intervals
      double[] ticks = new double[interval_number+1];
      
      // 1st cycle on elements of array of data
      for(int counter = 0; counter < data_size; counter++)
      if(loc_types[counter] != OUTER_CELL & en_types[counter] != ADIABATIC_ADIABATIC_CELL)
      {
        data_number++;
          
        if(data_number == 1)
        {
          min = data[counter];
          max = data[counter];
        }
              
        // determination of minimum for data  
        if(data[counter] < min)
          min = data[counter];
        
        // determination of maximum for data  
        if(data[counter] > max)
          max = data[counter];
      }
      
      // Size of each interval
      double interval_size = (double)Math.abs(max - min)/interval_number;
      
      // Determination of values of ticks
      for(int interval_counter = 0; interval_counter <= interval_number; interval_counter++)
      {
        ticks[interval_counter] = min + interval_size*interval_counter;
      }
      
      ticks[interval_number] = max;
      boolean add_to_interval = false;
      
      // 2nd cycle on elements of array of data: calculation of elements of array within each interval
      for(int data_counter = 0; data_counter < data_size; data_counter++)
      if(loc_types[data_counter] != OUTER_CELL & en_types[data_counter] != ADIABATIC_ADIABATIC_CELL)
      {
        add_to_interval = false;
        
        for(int interval_counter = 0; interval_counter < interval_number; interval_counter++)
        {
          // Calculation of the number of elements of array within current interval
          if(data[data_counter] >= ticks[interval_counter] & data[data_counter] < ticks[interval_counter + 1])
          {
            data_in_intervals[interval_counter]++;
            interval_counter = interval_number + 1;
            add_to_interval = true;
          }
        }
        
        if(data[data_counter] == max & !add_to_interval)
          data_in_intervals[interval_number - 1]++;
      }
      
      // average value
      double average_value = 0;
      
      // Calculation of average value for array of data
      for(int data_counter = 0; data_counter < data_size; data_counter++)
      if(loc_types[data_counter] != OUTER_CELL & en_types[data_counter] != ADIABATIC_ADIABATIC_CELL)
      {
        average_value += data[data_counter];
      //  data_number++;
      }
      
      if(data_number > 0)
        average_value = average_value/data_number;
      
      // mean square deviation
      double mean_sq_dev = 0;
      
      // sum of square deviations
      double sum_sq_dev = 0;
      
      // Calculation of mean square deviation
      for(int data_counter = 0; data_counter < data_size; data_counter++)
      if(loc_types[data_counter] != OUTER_CELL & en_types[data_counter] != ADIABATIC_ADIABATIC_CELL)
        sum_sq_dev += (average_value - data[data_counter])*(average_value - data[data_counter]);
      
      if(data_number > 0)
        mean_sq_dev = Math.sqrt(sum_sq_dev/data_number);
      
    //  TEST
    //  double sum = 0;
      
    //  for(int interval_counter = 0; interval_counter < interval_number; interval_counter++)
    //    sum += data_in_intervals[interval_counter]*1.0/data_size;
      
      try
      {
        bw_param.write(step_count*time_step_value+" "+average_value+"\n");
        bw_param.flush();
        
        if(step_count == STEP_NUMBER)
        {
          bw_param.close();
          
          System.out.println("File for graph of time dependence of "+param_name+" is created.");
        }
        
        if(calc_distribution)
        {
          BufferedWriter bw_graph = new BufferedWriter(new FileWriter(file_name+"_distr_"+param_name+".txt"));
        
          for(int interval_counter = 0; interval_counter < interval_number; interval_counter++)
          {
            bw_graph.write(0.5*(ticks[interval_counter] + ticks[interval_counter + 1])+" "+data_in_intervals[interval_counter]*1.0/data_size+"\n");
            bw_graph.flush();
          }
        
          bw_graph.write("# Data number:   "+data_number+"\n");
          bw_graph.write("# Average value: "+average_value+"\n");
          bw_graph.write("# Mean square deviation: "+mean_sq_dev+"\n");
          bw_graph.close();
        }
      }
      catch(IOException io_exc)
      {
        System.out.println("ERROR!!! IOException: "+io_exc);
      }
    }
    
    /** The method creates files with data for building of graphs for given task and given time steps.
     * @param task_name name of task
     * @param record_period number of time steps between records to files with results
     * @param step_number total number of time steps
     */
    private static void createGraphFilesJOCL(String task_name, int record_period, long step_number)
    {
      String string, read_file_name_1, read_file_name_2;
      String zeros = "";
      StringTokenizer st;
      
      // Exponent of power of 10 in standard representation of total number of time steps
      int max_exponent = (int)Math.floor(Math.log10(step_number));

      // Exponent of power of 10 in standard representation of current time step
      int exponent;
      
      // Parameters of cell: 
      // 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 
      // 6. temperature; 7. specific elastic energy; 8. principal stress; 
      // 9-11. 3 components of specific volume force moment vector calculated using neighbour cells at 1st sphere only; 
      // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 
      // 13. current influx of specific dissipated energy; 14. specific dissipated energy.
      byte en_type;
      byte loc_type;
      int gr_index;
      double coordinate_X, coordinate_Y, coordinate_Z;
      double temperature, spec_elast_energy, prin_stress;
      double force_moment_X, force_moment_Y, force_moment_Z;
      byte state;
      double spec_tors_en_influx, spec_tors_energy;
      double total_spec_energy;
      double rel_spec_elast_energy;
      double rel_spec_tors_energy;
      double abs_force_moment;
      double abs_force_moment_X, abs_force_moment_Y, abs_force_moment_Z;
      
      // Number of cells
      int cell_number = 0;
      double time_step = 1.0E-9;
      double cur_time = 0;
      
      // Average values
      double aver_temperature;
      double aver_spec_elast_energy;
      double aver_abs_force_moment_X;
      double aver_abs_force_moment_Y;
      double aver_abs_force_moment_Z;
      double aver_spec_tors_en_influx;
      double aver_spec_tors_energy;
      double aver_rel_spec_elast_energy;
      double aver_abs_force_moment;
      double aver_rel_spec_tors_energy;
      double elast_energy_total_portion;
      double tors_energy_total_portion;
      
      try
      {
        BufferedWriter bw_specElastEnergy  = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_spec_elast_energy.txt"));
        BufferedWriter bw_forceMomX        = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment_X.txt"));
        BufferedWriter bw_forceMomY        = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment_Y.txt"));
        BufferedWriter bw_forceMomZ        = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment_Z.txt"));
        BufferedWriter bw_specTorsEnInflux = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_spec_tors_en_influx.txt"));
        BufferedWriter bw_specTorsEnergy   = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_spec_tors_energy.txt"));
        
        BufferedWriter bw_relSpecElastEnergy = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_rel_spec_elast_energy.txt"));
        BufferedWriter bw_absForceMoment     = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment.txt"));
        BufferedWriter bw_relSpecTorsEnergy  = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_rel_spec_tors_energy.txt"));
        BufferedWriter bw_elastEnTotalPortion= new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_elast_energy_total_portion.txt"));
        BufferedWriter bw_torsEnTotalPortion = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_tors_energy_total_portion.txt"));
        
        bw_specElastEnergy.write    (0.0+" "+0.0+"\n");
        bw_forceMomX.write          (0.0+" "+0.0+"\n");
        bw_forceMomY.write          (0.0+" "+0.0+"\n");
        bw_forceMomZ.write          (0.0+" "+0.0+"\n");
        bw_specTorsEnInflux.write   (0.0+" "+0.0+"\n");
        bw_specTorsEnergy.write     (0.0+" "+0.0+"\n");
      //  bw_relSpecElastEnergy.write (0.0+" "+0.0+"\n");
        bw_absForceMoment.write     (0.0+" "+0.0+"\n");
      //  bw_relSpecTorsEnergy.write  (0.0+" "+0.0+"\n");
      //  bw_elastEnTotalPortion.write(0.0+" "+0.0+"\n");
      //  bw_torsEnTotalPortion.write (0.0+" "+0.0+"\n");
        
        for(int step_index = record_period; step_index <= step_number; step_index += record_period)
        {
          cur_time = time_step*step_index;
          cur_time = Math.round(cur_time*1.0E9)/1.0E3;
          
          System.out.println("Step # "+step_index+". Time: "+cur_time+" mcs.");
          
          exponent = (int)Math.floor(Math.log10(step_index));
                
          zeros = "";
          
          for(int zero_counter = exponent; zero_counter < max_exponent; zero_counter++)
            zeros = zeros + "0";
        
          // Input files 
          read_file_name_1 = "./user/task_db/"+task_name+"/res_3/"+task_name+"_"+zeros+step_index+"_jocl.res";
          read_file_name_2 = "./user/task_db/"+task_name+"/res_3/"+task_name+"_"+zeros+step_index+"_relValues.res";
        
          cell_number = 0;
        
          aver_temperature         = 0;
          aver_spec_elast_energy   = 0;
          aver_abs_force_moment_X  = 0;
          aver_abs_force_moment_Y  = 0;
          aver_abs_force_moment_Z  = 0;
          aver_spec_tors_en_influx = 0;
          aver_spec_tors_energy    = 0;
        
          aver_rel_spec_elast_energy = 0;
          aver_abs_force_moment      = 0;
          aver_rel_spec_tors_energy  = 0;
        
          BufferedReader br1 = new BufferedReader(new FileReader(read_file_name_1));
          BufferedReader br2 = new BufferedReader(new FileReader(read_file_name_2));
          
          while(br1.ready())
          {
            string = br1.readLine();
            st = new StringTokenizer(string);
            string = st.nextToken();
            
            if(!string.equals("#") & st.hasMoreTokens())
            {
                // Reading of coordinates and physical parameters of current cell
                en_type             = new Byte(string).byteValue();
                loc_type            = new Byte(st.nextToken()).byteValue();
                gr_index            = new Integer(st.nextToken()).intValue();
                coordinate_X        = new Double(st.nextToken()).doubleValue();
                coordinate_Y        = new Double(st.nextToken()).doubleValue();
                coordinate_Z        = new Double(st.nextToken()).doubleValue();
                temperature         = new Double(st.nextToken()).doubleValue();
                spec_elast_energy   = new Double(st.nextToken()).doubleValue();
                prin_stress         = new Double(st.nextToken()).doubleValue();
                force_moment_X      = new Double(st.nextToken()).doubleValue();
                force_moment_Y      = new Double(st.nextToken()).doubleValue();
                force_moment_Z      = new Double(st.nextToken()).doubleValue();
                state               = new Byte(st.nextToken()).byteValue();
                spec_tors_en_influx = new Double(st.nextToken()).doubleValue();
                spec_tors_energy    = new Double(st.nextToken()).doubleValue();
                
                if(en_type  == Common.INNER_CELL | en_type == Common.LAYER_CELL)
                if(loc_type != Common.OUTER_CELL)
                {
                  cell_number++;
                  
                  aver_temperature         += temperature;
                  aver_spec_elast_energy   += spec_elast_energy;
                  aver_abs_force_moment_X  += Math.abs(force_moment_X);
                  aver_abs_force_moment_Y  += Math.abs(force_moment_Y);
                  aver_abs_force_moment_Z  += Math.abs(force_moment_Z);
                  aver_spec_tors_en_influx += spec_tors_en_influx;
                  aver_spec_tors_energy    += spec_tors_energy;
                }
            }
          }
          
          elast_energy_total_portion = aver_spec_elast_energy/(aver_spec_elast_energy + aver_spec_tors_energy);
          tors_energy_total_portion  = aver_spec_tors_energy/(aver_spec_elast_energy + aver_spec_tors_energy);
          
          aver_temperature         = aver_temperature/cell_number;
          aver_spec_elast_energy   = aver_spec_elast_energy/cell_number;
          aver_abs_force_moment_X  = aver_abs_force_moment_X/cell_number;
          aver_abs_force_moment_Y  = aver_abs_force_moment_Y/cell_number;
          aver_abs_force_moment_Z  = aver_abs_force_moment_Z/cell_number;
          aver_spec_tors_en_influx = aver_spec_tors_en_influx/cell_number;
          aver_spec_tors_energy    = aver_spec_tors_energy/cell_number;
          
          aver_spec_elast_energy   = Math.round(aver_spec_elast_energy*1.0E3)/1.0E12;
          aver_abs_force_moment_X  = Math.round(aver_abs_force_moment_X*1.0E3)/1.0E12;
          aver_abs_force_moment_Y  = Math.round(aver_abs_force_moment_Y*1.0E3)/1.0E12;
          aver_abs_force_moment_Z  = Math.round(aver_abs_force_moment_Z*1.0E3)/1.0E12;
          aver_spec_tors_en_influx = Math.round(aver_spec_tors_en_influx*1.0E3)/1.0E12;
          aver_spec_tors_energy    = Math.round(aver_spec_tors_energy*1.0E3)/1.0E12;
          elast_energy_total_portion = elast_energy_total_portion*100.0;
          tors_energy_total_portion  = tors_energy_total_portion*100.0;
          
          bw_specElastEnergy.write (cur_time+" "+aver_spec_elast_energy+"\n");
          bw_forceMomX.write       (cur_time+" "+aver_abs_force_moment_X+"\n");
          bw_forceMomY.write       (cur_time+" "+aver_abs_force_moment_Y+"\n");
          bw_forceMomZ.write       (cur_time+" "+aver_abs_force_moment_Z+"\n");
          bw_specTorsEnInflux.write(cur_time+" "+aver_spec_tors_en_influx+"\n");
          bw_specTorsEnergy.write  (cur_time+" "+aver_spec_tors_energy+"\n");
          bw_elastEnTotalPortion.write(cur_time+" "+elast_energy_total_portion+"\n");
          bw_torsEnTotalPortion.write (cur_time+" "+tors_energy_total_portion+"\n");
          
          bw_specElastEnergy.flush();
          bw_forceMomX.flush();
          bw_forceMomY.flush();
          bw_forceMomZ.flush();
          bw_specTorsEnInflux.flush();
          bw_specTorsEnergy.flush();
          bw_elastEnTotalPortion.flush();
          bw_torsEnTotalPortion.flush();
          
          System.out.println("aver_spec_elast_energy = "+aver_spec_elast_energy+" GPa; aver_spec_tors_energy = "+aver_spec_tors_energy+" GPa; "+
                             "elast_energy_total_portion = "+elast_energy_total_portion+" %; tors_energy_total_portion = "+tors_energy_total_portion+" %;\n"+
                             "aver_spec_tors_en_influx = "+aver_spec_tors_en_influx+" GPa; aver_abs_force_moment_X = "+aver_abs_force_moment_X+" GPa; "+
                             "aver_abs_force_moment_Y = "+aver_abs_force_moment_Y+" GPa; aver_abs_force_moment_Z = "+aver_abs_force_moment_Z+" GPa;");
          
          cell_number = 0;
          
          while(br2.ready())
          {
            string = br2.readLine();
            st = new StringTokenizer(string);
            string = st.nextToken();
            
            if(!string.equals("#") & st.hasMoreTokens())
            {
                // Reading of coordinates and physical parameters of current cell
                en_type             = new Byte(string).byteValue();
                loc_type            = new Byte(st.nextToken()).byteValue();
                gr_index            = new Integer(st.nextToken()).intValue();
                coordinate_X        = new Double(st.nextToken()).doubleValue();
                coordinate_Y        = new Double(st.nextToken()).doubleValue();
                coordinate_Z        = new Double(st.nextToken()).doubleValue();
                temperature         = new Double(st.nextToken()).doubleValue();
                rel_spec_elast_energy = new Double(st.nextToken()).doubleValue();
                total_spec_energy     = new Double(st.nextToken()).doubleValue();
                abs_force_moment_X    = new Double(st.nextToken()).doubleValue();
                abs_force_moment_Y    = new Double(st.nextToken()).doubleValue();
                abs_force_moment_Z    = new Double(st.nextToken()).doubleValue();
                state               = new Byte(st.nextToken()).byteValue();
                abs_force_moment      = new Double(st.nextToken()).doubleValue();
                rel_spec_tors_energy  = new Double(st.nextToken()).doubleValue();
                
                if(en_type  == Common.INNER_CELL | en_type == Common.LAYER_CELL)
                if(loc_type != Common.OUTER_CELL)
                {
                  cell_number++;
                  
                  aver_rel_spec_elast_energy += rel_spec_elast_energy;
                  aver_abs_force_moment      += abs_force_moment;
                  aver_rel_spec_tors_energy  += rel_spec_tors_energy;
                }
            }
          }
          
          aver_rel_spec_elast_energy = aver_rel_spec_elast_energy/cell_number;
          aver_abs_force_moment      = aver_abs_force_moment/cell_number;
          aver_rel_spec_tors_energy  = aver_rel_spec_tors_energy/cell_number;
          
          aver_rel_spec_elast_energy = aver_rel_spec_elast_energy*1.0E2;
          aver_abs_force_moment      = Math.round(aver_abs_force_moment*1.0E3)/1.0E12;
          aver_rel_spec_tors_energy  = aver_rel_spec_tors_energy*1.0E2;
          
          bw_relSpecElastEnergy.write(cur_time+" "+aver_rel_spec_elast_energy+"\n");
          bw_absForceMoment.write    (cur_time+" "+aver_abs_force_moment+"\n");
          bw_relSpecTorsEnergy.write (cur_time+" "+aver_rel_spec_tors_energy+"\n");
          
          bw_relSpecElastEnergy.flush();
          bw_absForceMoment.flush();
          bw_relSpecTorsEnergy.flush();
          
          System.out.println("aver_abs_force_moment = "+aver_abs_force_moment+" GPa; aver_rel_spec_elast_energy = "+aver_rel_spec_elast_energy+" %; "+
                             "aver_rel_spec_tors_energy = "+aver_rel_spec_tors_energy+" %.\n");
        }
        
        bw_specElastEnergy.close();
        bw_forceMomX.close();
        bw_forceMomY.close();
        bw_forceMomZ.close();
        bw_specTorsEnInflux.close();
        bw_specTorsEnergy.close();
        bw_elastEnTotalPortion.close();
        bw_torsEnTotalPortion.close();
        bw_relSpecElastEnergy.close();
        bw_absForceMoment.close();
        bw_relSpecTorsEnergy.close();
      }
      catch(IOException io_exc)
      {
        System.out.println("ERROR!!! IOException: "+io_exc);
      }
    }
    
    /** The method calculates and returns array of pairs of cells at 1st coordination sphere 
     * necessary for calculation of local force moment.
     * @return array of pairs of cells at 1st coordination sphere 
     *         necessary for calculation of local force moment
     */
    private static int[][] createCell1SPairs()
    {
        // Triple array of pairs of cells at 1st coordination sphere 
        // necessary for calculation of local force moment for each cell is created:
        // first index is global single index of "central" cell;
        // second index is global single index of 1st or 2nd neighbour cell 
        // used for calculation of local force moment.
        int[][][] cell1S_pairs = new int[work_item_number][neighb1S_number*neighb1S_number][2];
        
        // Double array of pairs of cells at 1st coordination sphere 
        // with smaller necessary number of elements is created: 
        // even indices correspond to 1st elements in pairs,
        // odd indices correspond to 2nd elements in pairs.
        int[][] new_cell1S_pairs = new int[4*neighb1S_number][work_item_number];
        
        for(int pair_counter = 0; pair_counter < neighb1S_number*4; pair_counter++)
          for(int cell_index = 0; cell_index < work_item_number; cell_index++)
            new_cell1S_pairs[pair_counter][cell_index]     = cell_number;
        
        // Single index of current neighbour of current "central" cell
        int cell1S_index;
      
        // Single index of current neighbour of current cell at 1st coordination sphere of "central" cell
        int cell1S_neighb_index;
        
        int cell1S_pair_0, cell1S_pair_1;
        
        int real_pair_counter;
        
        int cell1S_pair_0_a, cell1S_pair_1_a;
        
        int double_pair_counter   = 0;
        
        // List of indices of neighbour cells for current cell
        ArrayList cell1S_indices;
        
        int total_pair_number = 0;
        
        boolean error = false;
        
        // TEST
        System.out.println("+++++=======-------+++++=======-------+++++=======-------+++++=======-------");
        System.out.println("work_item_number = "+work_item_number);
        System.out.println("Cell pairs for calculation of force moments");
        
        for(int cell_index = 0; cell_index < cell_number; cell_index++)
        {
          cell1S_indices = new ArrayList(0);
          
          // Obtaining of all cells at 1st coordination sphere of "central" cell
          for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
          {
            // Single index of current neighbour of current "central" cell
            cell1S_index = neighbours1S[neighb1S_counter][cell_index];
            
            if(cell1S_index > -1 & cell1S_index < cell_number)
              // Addition of index of current neighbour cell to the list
              cell1S_indices.add(cell1S_index);
          }
            
          // Obtaining of all cells at 1st coordination sphere of "central" cell
          for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
          {
            // Single index of current neighbour of current "central" cell
            cell1S_index = neighbours1S[neighb1S_counter][cell_index];
            
            if(cell1S_index > -1 & cell1S_index < cell_number)
            {
              // Obtaining of indices of neighbours of current cell at 1st coordination sphere of "central" cell
              for(int neighb1S_neighb_counter = 0; neighb1S_neighb_counter < neighb1S_number; neighb1S_neighb_counter++)
              {
                // Current index of neighbour of current cell at 1st coordination sphere of "central" cell
                cell1S_neighb_index = neighbours1S[neighb1S_neighb_counter][cell1S_index];
                
                if(cell1S_neighb_index > -1 & cell1S_neighb_index < cell_number)
                {
                  if(cell1S_indices.contains(cell1S_neighb_index))
                  {
                    cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][0] = cell1S_index;
                    cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][1] = cell1S_neighb_index;
                  }
                  else
                  {
                    cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][0] = cell_number;
                    cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][1] = cell_number;
                  }
                }
                else
                {
                    cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][0] = cell_number;
                    cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][1] = cell_number;
                }
              }
            }
            else
            {
              for(int neighb1S_neighb_counter = 0; neighb1S_neighb_counter < neighb1S_number; neighb1S_neighb_counter++)
              {
              //  System.out.println("cell_index = "+cell_index+"; pair index: "+(neighb1S_counter*neighb1S_number + neighb1S_neighb_counter)+"; ");
                
                cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][0] = cell_number;
                cell1S_pairs[cell_index][neighb1S_counter*neighb1S_number + neighb1S_neighb_counter][1] = cell_number;
              }
            }
          }
          
          cell1S_pair_0_a     = cell_number;
          cell1S_pair_1_a     = cell_number;
          double_pair_counter = 0;
          real_pair_counter   = 0;
           
          // удаление совпадающих пар
          for(int pair_counter = 0; pair_counter < neighb1S_number*neighb1S_number; pair_counter++)
          {
              cell1S_pair_0 = cell1S_pairs[cell_index][pair_counter][0];
              cell1S_pair_1 = cell1S_pairs[cell_index][pair_counter][1];
              
              if(cell1S_pair_0 < cell_number & cell1S_pair_1 < cell_number)
              {
                real_pair_counter++;
                
                for(int pair_counter_1 = pair_counter+1; pair_counter_1 < neighb1S_number*neighb1S_number; pair_counter_1++)
                {
                  cell1S_pair_0_a = cell1S_pairs[cell_index][pair_counter_1][0];
                  cell1S_pair_1_a = cell1S_pairs[cell_index][pair_counter_1][1];
                  
                  if(cell1S_pair_0_a < cell_number & cell1S_pair_1_a < cell_number)
                  {
                    if((cell1S_pair_0_a == cell1S_pair_0 & cell1S_pair_1_a == cell1S_pair_1) |
                       (cell1S_pair_0_a == cell1S_pair_1 & cell1S_pair_1_a == cell1S_pair_0))
                    {
                      cell1S_pairs[cell_index][pair_counter_1][0] = cell_number;
                      cell1S_pairs[cell_index][pair_counter_1][1] = cell_number;
                      
                      double_pair_counter++;
                    }
                  }
                }   
              }
          }
          
          // TEST
        //  System.out.println();
        //  System.out.println("Cell # "+cell_index);
          
          if(real_pair_counter != double_pair_counter)
            System.out.println("ERROR!!! Number of cell pairs = "+double_pair_counter+" but must be = "+real_pair_counter);
        //  else
        //    System.out.println("Number of cell pairs: "+double_pair_counter);
          
        //  for(int cell1S_counter = 0; cell1S_counter < cell1S_indices.size(); cell1S_counter++)
        //      System.out.println("Index of neighbour cell # "+(cell1S_counter+1)+": "+(int)cell1S_indices.get(cell1S_counter));
          
          real_pair_counter = 0;
               
          for(int pair_counter = 0; pair_counter < neighb1S_number*neighb1S_number; pair_counter++)
          {
              cell1S_pair_0 = cell1S_pairs[cell_index][pair_counter][0];
              cell1S_pair_1 = cell1S_pairs[cell_index][pair_counter][1];
              error = false;
              
              if(cell1S_pair_0 < cell_number & cell1S_pair_1 < cell_number)
              {
                 new_cell1S_pairs[2*real_pair_counter][cell_index]     = cell1S_pair_0;
                 new_cell1S_pairs[2*real_pair_counter + 1][cell_index] = cell1S_pair_1;
                 
                 real_pair_counter++;
                 total_pair_number++;
                 
             //    System.out.println("Cell # "+cell_index+"; pair # "+real_pair_counter+": "+cell1S_pair_0+", "+cell1S_pair_1);
                 error = false;
              }
              
              if(!cell1S_indices.contains(cell1S_pair_0) & cell1S_pair_0 < cell_number)
              {
                  System.out.println("ERROR!!! Cell # "+cell1S_pair_0+" is not neighbour of cell # "+cell_index);                  
                  error = true;
              }
              
              if(!cell1S_indices.contains(cell1S_pair_1) & cell1S_pair_1 < cell_number)
              {
                  System.out.println("ERROR!!! Cell # "+cell1S_pair_1+" is not neighbour of cell # "+cell_index);
                  error = true;
              }
              
              if(error)
              {
                  real_pair_counter--;
                  total_pair_number--;
              }
          }
          
          if(real_pair_counter != double_pair_counter)
            System.out.println("ERROR!!! Number of cell pairs = "+double_pair_counter+" but must be = "+real_pair_counter);
        }
        
        // Variables for calculation of array elements
        int total_pair_number_1 = 0;
        
        // Testing of elements of array of cell indices necessary for calculation of force moments
        for(int cell_index   = 0; cell_index   < cell_number;       cell_index++)
        for(int pair_counter = 0; pair_counter < neighb1S_number*2; pair_counter++)
        {
          cell1S_pair_0 = new_cell1S_pairs[2*pair_counter][cell_index];
          cell1S_pair_1 = new_cell1S_pairs[2*pair_counter + 1][cell_index];
          
          if(cell1S_pair_0 < cell_number & cell1S_pair_1 < cell_number)
            total_pair_number_1++;
          else
            if(cell1S_pair_0 != cell_number | cell1S_pair_1 != cell_number)
              System.out.println("ERROR!!! Cell # "+cell1S_pair_0+" or cell # "+cell1S_pair_1+" is not neighbour1S of cell # "+cell_index);
        }
        
        System.out.println();
        System.out.println("Total number of cell pairs: "+total_pair_number+" = "+total_pair_number_1);
        
        // TEST
        System.out.println("+++++=======-------+++++=======-------+++++=======-------+++++=======-------");
        
        return new_cell1S_pairs;
    }
    
    /** Method for calculation of the value of relative changes of cell volumes
     * @param stresses_X    array of components X of stress vectors of cells
     * @param stresses_Y    array of components Y of stress vectors of cells
     * @param stresses_Z    array of components Z of stress vectors of cells
     * @param en_types      array of energy types of cells
     * @param cell_coords   coordinates of cells
     * @param grain_indices array of single indices of cells
     * @param mod_elast     array of elastic modules of cells
     */
    private static void calcRelativeVolumeChanges(double[] stresses_X, double[] stresses_Y, double[] stresses_Z, 
                                                      byte[] en_types, double[][] cell_coords, int[] grain_indices, double[] mod_elast)
    {
        // Single index of neighbour cell at 1st coordination sphere of current cell
        int neighb_cell_index;
        
        // Components of differences of stress vectors of neighbour cells and stress vector of current cell
        double[] diff_stress_X = new double[neighb1S_number];
        double[] diff_stress_Y = new double[neighb1S_number];
        double[] diff_stress_Z = new double[neighb1S_number];
        
        // Components of unit vectors normal to facets between current cell and its neighbour cells
        double[] norm_vec_X = new double[neighb1S_number];
        double[] norm_vec_Y = new double[neighb1S_number];
        double[] norm_vec_Z = new double[neighb1S_number];
        
        // length of vector normal to facet between current cell and its neighbour cell
        double vec_length;
        
        // array of changes of specific elastic energy of cells
        double[] spec_energy_change = new double[cell_number];
        
        for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
        if(en_types[cell_counter] == INNER_CELL | en_types[cell_counter] == LAYER_CELL)
        {
          for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          {
            // index of current neighbour cell
            neighb_cell_index = neighbours1S[neighb_counter][cell_counter];
            
            if(en_types[neighb_cell_index] != ADIABATIC_ADIABATIC_CELL & 
               en_types[neighb_cell_index] != ADIABATIC_TEMPERATURE_CELL & 
               en_types[neighb_cell_index] != ADIABATIC_THERMAL_CELL)
            {
              // Components of vectors normal to facets between current cell and its neighbour cells
              norm_vec_X[neighb_counter] = cell_coords[0][neighb_cell_index] - cell_coords[0][cell_counter];
              norm_vec_Y[neighb_counter] = cell_coords[1][neighb_cell_index] - cell_coords[1][cell_counter];
              norm_vec_Z[neighb_counter] = cell_coords[2][neighb_cell_index] - cell_coords[2][cell_counter];
            
              // length of vector normal to facet between current cell and its neighbour cell
              vec_length = Math.sqrt(norm_vec_X[neighb_counter]*norm_vec_X[neighb_counter] + 
                                     norm_vec_Y[neighb_counter]*norm_vec_Y[neighb_counter] + 
                                     norm_vec_Z[neighb_counter]*norm_vec_Z[neighb_counter]);
            
              // normalization of vector normal to facet between current cell and its neighbour cell
              norm_vec_X[neighb_counter] = norm_vec_X[neighb_counter]/vec_length;
              norm_vec_Y[neighb_counter] = norm_vec_Y[neighb_counter]/vec_length;
              norm_vec_Z[neighb_counter] = norm_vec_Z[neighb_counter]/vec_length;
            
              // Components of differences of stress vectors of neighbour cells and stress vector of current cell
              diff_stress_X[neighb_counter] = stresses_X[neighb_cell_index] - stresses_X[cell_counter];
              diff_stress_Y[neighb_counter] = stresses_Y[neighb_cell_index] - stresses_Y[cell_counter];
              diff_stress_Z[neighb_counter] = stresses_Z[neighb_cell_index] - stresses_Z[cell_counter];
            
              // change of specific elastic energy of cell
              spec_energy_change[cell_counter] += diff_stress_X[neighb_counter]*norm_vec_X[neighb_counter] + 
                                                  diff_stress_Y[neighb_counter]*norm_vec_Y[neighb_counter] +
                                                  diff_stress_Z[neighb_counter]*norm_vec_Z[neighb_counter];
            }
          }
          
          // change of specific elastic energy of current cell
          spec_energy_change[cell_counter] = spec_energy_change[cell_counter]/neighb1S_number;
          
          // relative change of volume of current cell
          rel_vol_changes[cell_counter] = spec_energy_change[cell_counter]/mod_elast[grain_indices[cell_counter]];
        }
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
        
        // Cell radius expressed in cell diameters
        double radius = 0.5;//cell_size_X/2;
        
        // Calculation of coordinates of cell in case of HCP
        if(packing_type == HEXAGONAL_CLOSE_PACKING)
        {
          if(index3 % 3 == 0)
          {
            if(index1 % 2 == 0)
            {
                coord_X = radius*(1 + index1*(double)Math.sqrt(3));                
                coord_Y = radius*(1 + index2*2);
            }
            else
            {
                coord_X = radius*(1 + index1*(double)Math.sqrt(3));
                coord_Y = radius*(2 + index2*2);
            }
          }
        
          if(index3 % 3 == 1)
          {
            if(index1 % 2 == 0)
            {
                coord_X = radius*(1 + (double)1.0/(double)Math.sqrt(3) + index1*(double)Math.sqrt(3));
                coord_Y = radius*(2 + index2*2);
            }
            else
            {
                coord_X = radius*(1 + (double)1.0/(double)Math.sqrt(3)+ index1*(double)Math.sqrt(3));
                coord_Y = radius*(1 + index2*2);
            }
          }
                    
          if(index3 % 3 == 2)
          {
            if(index1 % 2 == 0)
            {
                coord_X = radius*(1 + (double)2.0/(double)Math.sqrt(3) + index1*(double)Math.sqrt(3));                        
                coord_Y = radius*(1 + index2*2);
            }
            else
            {
                coord_X = radius*(1 + (double)2.0/(double)Math.sqrt(3) + index1*(double)Math.sqrt(3));
                coord_Y = radius*(2 + index2*2);
            }
          }
        
          coord_Z = radius*(1 + (double)2.0*index3*(double)Math.sqrt(2.0/3.0));
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
        Three _tripleIndex = calcTripleIndex(_intIndex, cell_number_I, cell_number_J, cell_number_K);
            
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
        if(_intIndex < cell_number_I*cell_number_J*cell_number_K)
        for (int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {                
            neighbour_indexI = neighbours[neighb_counter].getI();            
            neighbour_indexJ = neighbours[neighb_counter].getJ();
            neighbour_indexK = neighbours[neighb_counter].getK();
               
            if((neighbour_indexI > -1)&(neighbour_indexI < cell_number_I)&
               (neighbour_indexJ > -1)&(neighbour_indexJ < cell_number_J)&
               (neighbour_indexK > -1)&(neighbour_indexK < cell_number_K))
            {
                neighbours1S[neighb_counter][_intIndex] = neighbour_indexI + neighbour_indexJ*cell_number_I + 
                                                          neighbour_indexK*cell_number_I*cell_number_J;
            }
            else
                neighbours1S[neighb_counter][_intIndex] = -3;
            
            if(neighbours1S[neighb_counter][_intIndex] >= cell_number_I*cell_number_J*cell_number_K |
               neighbours1S[neighb_counter][_intIndex] <  0)
               neighbours1S[neighb_counter][_intIndex] = cell_number_I*cell_number_J*cell_number_K;
        //    System.out.print(neighbours1S[_intIndex][neighb_counter]+" ");
        }
        else
        for (int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
            neighbours1S[neighb_counter][_intIndex] = cell_number_I*cell_number_J*cell_number_K;
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
            cell_volume = (double)0.6938*CELL_SIZE*CELL_SIZE*CELL_SIZE;

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
            cell_surface = (double)0.6938*6*CELL_SIZE*CELL_SIZE;

        if(packing_type == SIMPLE_CUBIC_PACKING)
            cell_surface = 6*CELL_SIZE*CELL_SIZE;
        
        return cell_surface;
    }
    
    /** The method calculates coordinates of lattice vectors for cluster.
     * @param lattice_angle_vecA_vecB angle between lattice vectors A and B expressed in degrees
     * @param lattice_angle_vecB_vecC angle between lattice vectors B and C expressed in degrees
     * @param lattice_angle_vecC_vecA angle between lattice vectors C and A expressed in degrees
     * @return coordinates of lattice vectors for cluster
     */
    private static void calcLatticeVectCoordinates(int grain_index, double lattice_angle_vecA_vecB, double lattice_angle_vecB_vecC, double lattice_angle_vecC_vecA)
    {
      // angles between lattice vectors expressed in radians
      double angle_a = Math.PI*lattice_angle_vecA_vecB/180.0;
      double angle_b = Math.PI*lattice_angle_vecB_vecC/180.0;
      double angle_c = Math.PI*lattice_angle_vecC_vecA/180.0;
      
      if(Math.sin(angle_a) == 0.0)
      {
        angle_a = Math.PI*0.01/180.0;
        
        System.out.println("The angle between lattice vectors A and B cannot be equal to 0! If alpha = "+angle_a+" then sin(alpha) = "+Math.sin(angle_a));
      }
      
      // coordinates of 1st vector
      lattice_vectors[0][grain_index] = (double)0.0;
      lattice_vectors[1][grain_index] = (double)1.0;
      lattice_vectors[2][grain_index] = (double)0.0;
      
      // coordinates of 2nd vector
      lattice_vectors[3][grain_index] = (double)Math.sin(angle_b);
      lattice_vectors[4][grain_index] = (double)Math.cos(angle_b);
      lattice_vectors[5][grain_index] = (double)0.0;
      
      // coordinates of 3rd vector
      lattice_vectors[6][grain_index] = (double)((Math.cos(angle_b) - Math.cos(angle_a)*Math.cos(angle_c))/Math.sin(angle_a));
      lattice_vectors[7][grain_index] = (double)Math.cos(angle_c);
      lattice_vectors[8][grain_index] = (double)Math.signum((double)Math.sin(angle_a))*
                                        (double)Math.sqrt(Math.max(0.0, 1.0 - lattice_vectors[6][grain_index]*lattice_vectors[6][grain_index] - 
                                                                             lattice_vectors[7][grain_index]*lattice_vectors[7][grain_index]));
    //  lattice_vectors[8] = ((double)1.0/(double)Math.sin(angle_a))*
      //                    (double)Math.sqrt((Math.cos(angle_a - angle_c) - Math.cos(angle_b))*(Math.cos(angle_b) - Math.cos(angle_a + angle_c)));
    }
    
    /** The method calculates coordinates of all possible vectors of local growth of grain
     * (the number of vectors is equal to the number of cell facets).
     * @return coordinates of all possible vectors of local growth of grain
     */
    private static double[] calcGrowthVectCoordinates()
    {
      // Coordinates of all possible vectors of local growth of grain (the number of vectors is equal to the number of cell facets)
      double[] growth_vectors_coordinates = new double[3*neighb1S_number];
        
      // Indices of central cell and its neighbours
      int cell_index, neighb_cell_index;
      
      // triple indices of current neighbour cell
      Three neighb_indices;
      
      // arrays of coordinates of central cell and current neighbour cells
      double[] cell_coordinates   = new double[3];
      double[] neighb_coordinates = new double[3];
      
      // Coordinates of central cell and its neighbours and vectors between centres of these cells
      VectorR3 cell_coord, neighb_coord, growth_vector;
      
      // Index of cell in the centre of specimen
      cell_index = cell_number_I*cell_number_J*(cell_number_K/2) + cell_number_I*(cell_number_J/2) + (cell_number_I/2);
      
      // array of coordinates of central cell
      cell_coordinates = calcCoordinates(cell_number_I/2, cell_number_J/2, cell_number_K/2);
      
      // Coordinates of central cell
      cell_coord = new VectorR3(cell_coordinates[0], cell_coordinates[1], cell_coordinates[2]);
      
      System.out.println("---------------=====================------------------=====================----------------");
      System.out.println("Coordinates of all possible vectors of local growth of grain");
      
      for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
      {
        // Индекс соседнего элемента
        neighb_cell_index = neighbours1S[neighbCounter][cell_index];
                    
        // Тройной индекс соседнего элемента
        neighb_indices = calcTripleIndex(neighb_cell_index, cell_number_I, cell_number_J, cell_number_K);
        
        // Массив координат соседнего элемента
        neighb_coordinates = calcCoordinates(neighb_indices.getI(), neighb_indices.getJ(), neighb_indices.getK());
        
        // Coordinates of current neighbour cell
        neighb_coord = new VectorR3(neighb_coordinates[0], neighb_coordinates[1], neighb_coordinates[2]);
        
        // Вектор возможного роста зерна
        growth_vector = residial(neighb_coord, cell_coord);
        
        // Normalization of the vector
        growth_vector.normalize();
        
        // Coordinates of current possible vector of local growth of grain
        growth_vectors_coordinates[neighbCounter*3]     = (double)growth_vector.getX();
        growth_vectors_coordinates[neighbCounter*3 + 1] = (double)growth_vector.getY();
        growth_vectors_coordinates[neighbCounter*3 + 2] = (double)growth_vector.getZ();
        
        System.out.println("Vector # "+neighbCounter+": ("+growth_vectors_coordinates[neighbCounter*3]    +"; "+
                                                           growth_vectors_coordinates[neighbCounter*3 + 1]+"; "+
                                                           growth_vectors_coordinates[neighbCounter*3 + 2]+").");
        
      //  System.out.println("Vector # "+neighbCounter+": ("+growth_vector.getX()+"; "+growth_vector.getY()+"; "+growth_vector.getZ()+").");
      }
      
      System.out.println("---------------=====================------------------=====================----------------");
      
      return growth_vectors_coordinates;
    }
    
    /** The method returns residial of two vectors (vect_1 - vect_2).
     * @param vect_1 minuend vector
     * @param vect_2 subtrahend vector
     * @return residial of two vectors (vect_1 - vect_2)
     */
    public static VectorR3 residial(VectorR3 vect_1, VectorR3 vect_2)
    {            
        return new VectorR3 (vect_1.getX() - vect_2.getX(), 
                             vect_1.getY() - vect_2.getY(),
                             vect_1.getZ() - vect_2.getZ());
    }
    
    

}
