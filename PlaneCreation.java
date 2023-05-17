import java.util.*;
// import java.lang.*;
import java.io.*;
import cellcore.*;
import recGeometry.*;
import util.*;
import java.util.zip.*;

/**
 * @(#) PlaneCreation.java version 1.0.2;       Apr 2009
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation of file with information on plane of section of 3D specimen
 *
 *=============================================================
 *  Last changes :
 *         13 Dec 2008 by Pavel V. Maksimov
 *            (change of method "createFilePDB");
 *         13 April 2009 by Pavel V. Maksimov
 *            (creation of methods "createGraph()", "createGraph2()", "createSurface()");
 *         17 November 2010 by Pavel V. Maksimov
 *            (creation of method "createGraphs(double, double, double)";
 *         24 Dec 2010 by Pavel V. Maksimov
 *            (creation of method "calcAbsMomentsInPlane(double _sect_coord_Y)" calculating
 *             absolute values of force moments for cells in certain plane perpendicular
 *             to chosen coordinate axis and writes them to output files).
 *
 *            File version 1.0.4.
 */

/** Class for creation of file with information on plane of section of 3D specimen
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0 - Sep 2008
 */
public class PlaneCreation
{
 //   String read_file_name  = "./task1/cracks_200.txt";
 //   String write_file_name = "./task1/cracks_200.pdb";

    String read_file_name; // = "./task/result_2009_05_03_400000_stresses.txt";
    String write_file_name;// = "./task/result_2009_05_03_400000_stresses.pdb";

//  String read_file_name  = "./task/spiral_dendrite_3000.txt";
//  String write_file_name = "./task/spiral_dendrite_3000.pdb";

//  String write_file_name = "./task/result_2009_01_14_80_x=43.pdb";
//  String write_file_name = "./task/result_2009_01_14_80_y=50.pdb";
//  String write_file_name = "./task/result_2009_01_14_80_z=20.pdb";

//  String read_file_name  = "./task/result_2008_10_23_40.txt";
//  String write_file_name = "./task/result_2008_10_23_40_z=10.txt";
    
//  static double plane_coord_X = 25.5;
    static double plane_coord_X;//= (1.577+10*1.732)/2;// 30.5;
    static double plane_coord_Y;// = 24.5;
    static double plane_coord_Z;// = (1.0 + 8*1.633)*0.5;

    static int plane_round_coord_X = 43;
    static int plane_round_coord_Y = 50;
    static int plane_round_coord_Z = 20;

    // Number of grains
    int grains_number = 32;

    // Size of cell
    static double cell_size = 1.0E-6;

    // Volume of specimen
    static double specimen_volume = 3600*30*0.6938*Math.pow(cell_size, 3);

    // Area of section of specimen by plane "z=const"
    static double section_area = 3600*Math.pow(cell_size, 2)*Math.sqrt(3.0)/2;

    /** Specimen size in direction of X-axis */
    double specimen_size_X;

    /** Specimen size in direction of Y-axis */
    double specimen_size_Y;

    /** Specimen size in direction of Z-axis */
    double specimen_size_Z;

    /** Finite element size in direction of X-axis */
    double element_size_X;

    /** Finite element size in direction of Y-axis */
    double element_size_Y;

    /** Finite element size in direction of Z-axis */
    double element_size_Z;

    /** Cell size in direction of X-axis */
    double cell_size_X;

    /** Cell size in direction of Y-axis */
    double cell_size_Y;

    /** Cell size in direction of Z-axis */
    double cell_size_Z;

    /** Cell volume */
    double cell_volume;

    /** Surface of cell3D (in case of hexagonal close packing) */
    double cell_surface;

    /** Number of cells of specimen3D in direction of X-axis */
    int cellNumberI;

    /** Number of cells of specimen3D in direction of Y-axis */
    int cellNumberJ;

    /** Number of cells of specimen3D in direction of Z-axis */
    int cellNumberK;

    /** Sizes of finite element evaluated in corresponding cell sizes */
    int elementSizeK, elementSizeJ, elementSizeI;

    /** Minimal distance between embryos expressed in cell sizes */
    double min_distance;

    /** Probability of appearance of crack embryo */
    static double EMBRYO_PROBABILITY;
    
    /** List of grains */
    ArrayList grainsCluster;

    /** 2D array of indices of neighbour cells at 1st coordination sphere:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index
     */
    int[][] neighbours1S;

    /** 2D array of indices of neighbour cells at 1st, 2nd and 3rd coordination spheres:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index (indices from  0 to 11 - for cells at 1st coordination sphere,
     *                                 indices from 12 to 41 - for cells at 2nd coordination sphere,
     *                                 indices from 42 to 53 - for cells at 3rd coordination sphere)
     */
    int[][] neighbours3D;

    /** Material of bulk. */
    String bulk_material;

    /** Specimen represented as a net of CA */
    ArrayList specimenR3;

    /** Number of grains in specimen */
    int grain_number;

    /** Cell radius */
//    public static final double radius = 1.0;
    
    /** Variable responsible for showing of cells on grain boundaries
     * as atoms "H" in "RasMol" */
    byte show_grain_bounds;

    // Type of packing of cells in specimen
    byte packing_type;
    
    // Coefficient of magnification in "RasMol"
    final double magnif_coeff = 1.0;//2.5;
    
   /** If "show_grain_bounds" possesses this value 
    * then grain bounds are shown in "RasMol".
    */
    final byte SHOW_GRAIN_BOUNDS      = 1;
    
    /** If "location_in_grain" possesses this value 
    * then cell is located on grain boundary.
    */    
    final byte CELL_ON_GRAIN_BOUNDARY = 1;
    
    /** Notation of cells in the interior of specimen */
    final String INTERIOR_CELLS = "interior";
    
    /** Notation of cells near front and back facets of specimen */
    final String FRONT_AND_BACK_CELLS = "front&back";  
    
    /** Notation of all cells in the specimen */
    final String ALL_SPECIMEN_CELLS = "all_spec_cells";
    
    /** Name of file with parameters of specimen */
    protected String specimen_file;

    /** Name of file with parameters of grain structure */
    protected String grains_file = "./task/al_gr_100n.grn";
    
    /** Name of file with parameters of task */
    protected String task_file;

    /** Name of task */
    protected String task_name;

    /** Number of time step */
    protected int time_step_index;

    /** Maximal number of time step */
    protected int max_step_number;

    /** Period of recording of files expressed in time steps */
    protected int record_period;

    /** The value of time step */
    protected double time_step;

    /** Number of files with information about results */
    int file_number;
    
    /** Required physical parameter for "RasMol" */
    protected String parameter;
    
    /** Minimal and maximum values of coordinates X, Y, Z */
    private final double COORD_MIN_VALUE = 0.0;
    private final double COORD_MAX_VALUE = 1.0E3;
    
    /** Constructor of the class
     * @param task_name name of task
     * @param _time_step_index number of time step
     * @param parameter required parameter
     */
    public PlaneCreation(String _task_name, int _time_step_index, String _parameter)
    {
        task_name       = new String(_task_name);
        max_step_number = 1000000;
        time_step_index = _time_step_index;
        parameter       = new String(_parameter);
        
     //   task_file = "./user/task_db/"+task_name+"/"+task_name+".seca";

     //   readTask(task_file);
    /*
        specimen_file = "./user/spec_db/"+task_name+"/"+task_name+".spc";

        // Loading of information about specimen.
        loadStructure(specimen_file);
    */
        cell_size_X = 1.0E-6;
        cell_size_Y = 1.0E-6;
        cell_size_Z = 1.0E-6;
        
        calcCellVolumeHCP();
        calcCellSurfaceHCP();
        
        // Period of recording of files expressed in time steps
        record_period = 100;

        // Number of files with information about results
        file_number = 2000;

        // The value of time step
        time_step = 1.0E-8;

        // Total number of time steps
        max_step_number = record_period*file_number;

        // Number of considered time step
        time_step_index = max_step_number*3/10;

        double[] coord_X = new double[3];
        coord_X[0] = 23.0;
        coord_X[1] = 26.5;
        coord_X[2] = 30.0;

        double[] coord_Y = new double[5];
        coord_Y[0] =  7.25;
        coord_Y[1] =  8.75;
        coord_Y[2] = 10.75;
        coord_Y[3] = 12.75;
        coord_Y[4] = 14.25;

        double[] coord_Z = new double[3];
        coord_Z[0] =  7.0;
        coord_Z[1] = 26.0;
        coord_Z[2] = 43.0;
        
        boolean create_graphs_Z = false;//true;//
        boolean create_time_graphs = false;//true;//
        
        if(create_graphs_Z)
        for(int time_step_counter = time_step_index; time_step_counter<=time_step_index; time_step_counter+=record_period)
        {
            System.out.println();
            
            createGraphs_Z(coord_X[0], coord_Y[1], time_step_counter);
            createGraphs_Z(coord_X[0], coord_Y[3], time_step_counter);
            
            createGraphs_Z(coord_X[1], coord_Y[0], time_step_counter);
            createGraphs_Z(coord_X[1], coord_Y[2], time_step_counter);
            createGraphs_Z(coord_X[1], coord_Y[4], time_step_counter);
            
            createGraphs_Z(coord_X[2], coord_Y[1], time_step_counter);
            createGraphs_Z(coord_X[2], coord_Y[3], time_step_counter);
        }

        if(create_time_graphs)
        for(int counter_Z = 0; counter_Z<coord_Z.length-2; counter_Z++)
        {
            createTimeGraphs(coord_X[0], coord_Y[1], coord_Z[counter_Z]);
            createTimeGraphs(coord_X[0], coord_Y[3], coord_Z[counter_Z]);
            
            createTimeGraphs(coord_X[1], coord_Y[0], coord_Z[counter_Z]);
            createTimeGraphs(coord_X[1], coord_Y[2], coord_Z[counter_Z]);
            createTimeGraphs(coord_X[1], coord_Y[4], coord_Z[counter_Z]);
            
            createTimeGraphs(coord_X[2], coord_Y[1], coord_Z[counter_Z]);
            createTimeGraphs(coord_X[2], coord_Y[3], coord_Z[counter_Z]);
        }
        
        // Number of grains in specimen
    //    grain_number = 2;
        
        /*
        double _sect_coord_X = 26.5;//23;//30;//
        double _sect_coord_Y = 10.75;//12.75;//8.75;//7.25;//14.25;//
        double _sect_coord_Z = 17.00;

        // Creation of text files for 3D-graphs (#for Surfer)
        // "strain - depth - time", "stress - depth - time",
        // "moments_X - depth - time", "moments_Y - depth - time", "moments_Z - depth - time".
     //   createGraphs(_sect_coord_X, _sect_coord_Y, _sect_coord_Z);
         
        // The method calculates values of porosity for all cells
        // and writes them to output files.
     //   calcCellPorosityValues();

        // The method calculates absolute values of force moments for cells
        // in certain plane perpendicular to chosen coordinate axis and writes them to output files.
    //    calcAbsMomentsInPlane(_sect_coord_Y);

        
        // Creation of text files for 2D-graphs (#for Grapher) "average strain - time",
        // "average stress - time", "average stress - average strain".
    //    createTimeGraphs(_sect_coord_X, _sect_coord_Y, _sect_coord_Z);

        // Number of time step
     // time_step_index = 500000;

        /*
        createGraphs_Z(_sect_coord_X, _sect_coord_Y, 490000);
        createGraphs_Z(_sect_coord_X, _sect_coord_Y, 500000);
        createGraphs_Z(_sect_coord_X, _sect_coord_Y, 510000);
         */

        // fromInitCondFileToFilePDB();        
        // createSection();
        // createFileXYZ();

        // Creation of file "RasMol"
     //   createFilePDB(time_step_index, show_grain_bounds);

        {
         /*
            String axis = "Z";
            double section_coord = 0;

            if(axis.equals("X"))
                section_coord = specimen_size_X/2;

            if(axis.equals("Y"))
                section_coord = specimen_size_Y/2;

            if(axis.equals("Z"))
                section_coord = specimen_size_Z/2;

            createSection(time_step_index, section_coord, axis);
         */
        }
        // createCurrentParamsFile();
        // createPlanePDB();

        // createGraph();
        // createGraph2();
        // createSurface();
        // createGraphTemperatures();
        // createGraphStressesOZ();
    
     //   createSpecimenR3();

        cellNumberI = 100;//cellNumberI-2;
        cellNumberJ = 100;//cellNumberJ-2;
        cellNumberK =  20;//cellNumberK-2;

        // Total number of output  files
        int total_file_number = 34;

        // Number of investigated output files
        int _file_number = 17;

        // Number of time steps running between two consequtive records in files.
        int time_step_number = 10000;

        // Number of plane of intersection of specimen (begin from 0 plane)
        int z_plane = 12;

        // Threshold value of force moment
        double thresh_value = 5.0E-19;

        // Sizes of cell
   /*
        cell_size_Y = cell_size_Y/cell_size_X;
        cell_size_Z = cell_size_Z/cell_size_X;
        cell_size_X = 1.0;
    */

     //   for(int file_counter = file_number; file_counter<=file_number; file_counter+= file_number-1)
        {
       /*     System.out.println("File # "+file_counter);
            
            createPrincipalStressesFile(file_counter*time_step_number);
            createForceMomentsFile(file_counter*time_step_number);
       */   
            
         //  createSectionZ(file_counter*time_step_number, z_plane);
        //   switchCells(file_counter*time_step_number,
        //               total_file_number*time_step_number, z_plane, thresh_value);
            
      //      System.out.println();
        }
        
        boolean create_cur_param_file = false;// true;// 
        
        if(create_cur_param_file)
        {
            int _max_step_number = 10000;
            
            task_name = "TiAlC_40X10X30_gr04_gr30_linearTemprDistr_recryst";
            createCurrentParamsFile("./user/task_db/"+task_name+"/"+task_name+"_"+_max_step_number+".res");
        }
        
        // createStressGradientsFile(1000);
        // createCrackEmbryosFile();
        // createPlanePDB();
        
        boolean create_cracks_file = false; // true;// 
        
        if(create_cracks_file)
            createCracksFile("./user/task_db/"+task_name+"/"+task_name+"_0570.res", "./user/task_db/"+task_name+"/dendrites_from_interface_300.dres");
        
        // Variable responsible for creation of file with information about distribution
        // of the logarithms of the values from given file.
        boolean create_file_with_log10values = false;// true;// 
        
        if(create_file_with_log10values)
        {
            int task_number       = 4;
            int read_file_number  = 2;
            int write_file_number = 2;
            
            String[] task_names       = new String[task_number];
            String[] read_file_names  = new String[task_number*read_file_number];
            String[] write_file_names = new String[task_number*write_file_number];
            
            task_names[0] = "steel_60X40X10_angle05_cycle_mech_load_Z";
            task_names[1] = "steel_60X40X10_angle07_cycle_mech_load_Z";
            task_names[2] = "steel_60X40X10_angle10_cycle_mech_load_Z";
            task_names[3] = "steel_60X40X10_angle15_cycle_mech_load_Z";
            
            file_number   = 20000;
            int file_step = 20000;
            
            read_file_names[0]  = "./user/task_db/"+task_names[0]+"/plane_graphs/"+"angle05_10mcs_k=01_abs_inst_mom.txt";
            read_file_names[1]  = "./user/task_db/"+task_names[0]+"/plane_graphs/"+task_names[0]+"_20mcs_k=01_rel_tors_energies.txt";
            read_file_names[2]  = "./user/task_db/"+task_names[1]+"/plane_graphs/"+"angle07_10mcs_k=01_abs_inst_mom.txt";
            read_file_names[3]  = "./user/task_db/"+task_names[1]+"/plane_graphs/"+task_names[1]+"_20mcs_k=01_rel_tors_energies.txt";
            read_file_names[4]  = "./user/task_db/"+task_names[2]+"/plane_graphs/"+"angle10_10mcs_k=01_abs_inst_mom.txt";
            read_file_names[5]  = "./user/task_db/"+task_names[2]+"/plane_graphs/"+task_names[2]+"_20mcs_k=01_rel_tors_energies.txt";
            read_file_names[6]  = "./user/task_db/"+task_names[3]+"/plane_graphs/"+"angle15_10mcs_k=01_abs_inst_mom.txt";
            read_file_names[7]  = "./user/task_db/"+task_names[3]+"/plane_graphs/"+task_names[3]+"_20mcs_k=01_rel_tors_energies.txt";
            
            write_file_names[0] = "./user/task_db/"+task_names[0]+"/plane_graphs/"+task_names[0]+"_10mcs_k=01_log10_abs_inst_mom.txt";
            write_file_names[1] = "./user/task_db/"+task_names[0]+"/plane_graphs/"+task_names[0]+"_20mcs_k=01_log10_rel_tors_energies.txt";
            write_file_names[2] = "./user/task_db/"+task_names[1]+"/plane_graphs/"+task_names[1]+"_10mcs_k=01_log10_abs_inst_mom.txt";
            write_file_names[3] = "./user/task_db/"+task_names[1]+"/plane_graphs/"+task_names[1]+"_20mcs_k=01_log10_rel_tors_energies.txt";
            write_file_names[4] = "./user/task_db/"+task_names[2]+"/plane_graphs/"+task_names[2]+"_10mcs_k=01_log10_abs_inst_mom.txt";
            write_file_names[5] = "./user/task_db/"+task_names[2]+"/plane_graphs/"+task_names[2]+"_20mcs_k=01_log10_rel_tors_energies.txt";
            write_file_names[6] = "./user/task_db/"+task_names[3]+"/plane_graphs/"+task_names[3]+"_10mcs_k=01_log10_abs_inst_mom.txt";
            write_file_names[7] = "./user/task_db/"+task_names[3]+"/plane_graphs/"+task_names[3]+"_20mcs_k=01_log10_rel_tors_energies.txt";
            
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
            for(int task_counter = 0; task_counter<task_number; task_counter++)
            for(int read_file_counter = 0; read_file_counter<read_file_number/2; read_file_counter++)
            {              
              for(int file_counter = file_step; file_counter<=file_number; file_counter+=file_step)
              { 
                read_file_name  = read_file_names [task_counter*read_file_number + read_file_counter];
                write_file_name = write_file_names[task_counter*read_file_number + read_file_counter];
                
            //    if(create_file_with_log10values)
                    // Creation of file with information about distribution of the logarithms of the values from given file
                    createFileWithLog10Values();
              }
              
              System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
            }
        }
        
        boolean createResFileForRightBasis = false;
        
        if(createResFileForRightBasis)
        {            
          int task_number = 1;
          String[] task_names = new String[task_number]; 
          task_names[0] = "alum_3grns_top_heat";
    //    task_names[1] = "alum_3grns_bending_X";
    //    task_names[2] = "alum_3grns_bending_Y";
          file_number   = 30000;
          int file_step = 1000;
          
          for(int task_counter = 0; task_counter<task_number; task_counter++)
          {            
            for(int file_counter = file_step; file_counter<=file_number; file_counter+=file_step)
            {
                if(file_counter<file_number/2)
                {
                    read_file_name  = "./user/task_db/"+task_names[task_counter]+"/"+task_names[task_counter]+"_00"+file_counter+".res";
                    write_file_name = "./user/task_db/"+task_names[task_counter]+"/right_basis/"+task_names[task_counter]+"_00"+file_counter+".res";
                }
                else
                {
                    read_file_name  = "./user/task_db/"+task_names[task_counter]+"/"+task_names[task_counter]+"_0"+file_counter+".res";
                    write_file_name = "./user/task_db/"+task_names[task_counter]+"/right_basis/"+task_names[task_counter]+"_0"+file_counter+".res";
                }
                
                createResFileForRightBasis();
            }
          }
        }
        
        boolean createGraphTemperaturesAlongOZ = false;
        
        if(createGraphTemperaturesAlongOZ)
        {
            int task_number = 1;
            String[] task_names = new String[task_number]; 
            task_names[0] = "alum_3grns_top_heat";
      //    task_names[1] = "alum_3grns_bending_X";
      //    task_names[2] = "alum_3grns_bending_Y";
        
            //   The method creates files for graphs of distribution of temperatures
            // in specimen along axis OZ.
            task_names = new String[2];
            // task_name[0] = "Cu_top_heat_1000_bottom_cool_300";
            task_names[0] = "Rubber_2_2_6_top_bottom_heat_423";
            task_names[1] = "copper_all_facets_tempr_1000";
            
            createGraphTemperaturesAlongOZ(task_names[0]);
            createGraphTemperaturesAlongOZ(task_names[1]);
        }
        
        boolean createGraphAnalyticTemperatureValues = false; //true;
        
        //The method creates file with temperature values calculated 
        // according to analytic solution of heat transfer problem for unrestricted layer.   
        if(createGraphAnalyticTemperatureValues)
        {
         //   task_name = "Rubber_2_2_6_top_bottom_heat_423";
            task_name = "copper_all_facets_tempr_1000";
            
            double time_step_value = 0;
            double[] time_periods = new double[9];            
            
            if(task_name == "Rubber_2_2_6_top_bottom_heat_423")
            {
                // The value of time step (in milliseconds)
                time_step_value = 1.2;
                
                time_periods[0] =     10*time_step_value;
                time_periods[1] =    100*time_step_value;
                time_periods[2] =   1000*time_step_value;
                time_periods[3] =  10000*time_step_value;
                time_periods[4] =  20000*time_step_value;
                time_periods[5] =  30000*time_step_value;
                time_periods[6] =  40000*time_step_value;
                time_periods[7] =  50000*time_step_value;
                time_periods[8] = 100000*time_step_value;
            }
            
            if(task_name == "copper_all_facets_tempr_1000")
            {
                // The value of time step (in nanoseconds)
                time_step_value = 1.0;
                
                time_periods[0] =   100*time_step_value;
                time_periods[1] =   200*time_step_value;
                time_periods[2] =   500*time_step_value;
                time_periods[3] =  1000*time_step_value;
                time_periods[4] =  2000*time_step_value;
                time_periods[5] =  3000*time_step_value;
                time_periods[6] =  4000*time_step_value;
                time_periods[7] =  5000*time_step_value;
                time_periods[8] = 10000*time_step_value;                
            }
            
            for(int time_period_counter = 0; time_period_counter<time_periods.length; time_period_counter++)
            {
                if(task_name == "Rubber_2_2_6_top_bottom_heat_423")
                    createGraphAnalyticTemperatureValues(time_periods[time_period_counter], time_periods[time_periods.length-1]);
                
                if(task_name == "copper_all_facets_tempr_1000")
                //    createGraphAnalyticTemperatureValues3D(time_periods[time_period_counter], 10*time_periods[time_periods.length-1]);
                createPlaneAnalyticTemperatureValues3D(time_periods[time_period_counter], 10*time_periods[time_periods.length-1]);
                
           //     compareData(time_periods[time_period_counter], 10*time_periods[time_periods.length-1]);
            }
        }
        
        boolean showSpecimenRegionInRightBasis = false;//true;//
        
        if(showSpecimenRegionInRightBasis)
        { 
          int task_number = 1;
          String[] task_names = new String[task_number];
          task_names[0] = "alum_tension_X";//"ceram_alumGr15_tension_X"; //
    //    task_names[1] = "alum_3grns_bending_X";
    //    task_names[2] = "alum_3grns_bending_Y";
          
          // Total number of time steps
          int step_number   = 100;// 100000;//
          
          // Number of time steps between neighbour files
          int file_step = 10;//60000;//
          
          double left_coord_X   =   1.1;
          double right_coord_X  = 100.7;
          double front_coord_Y  =   1.25;
          double back_coord_Y   = 101.25;
          double top_coord_Z    =   1.0;
          double bottom_coord_Z =  20.5;
          
          int left_index_I   =  3;
          int right_index_I  = 10;
          int front_index_J  =  2;
          int back_index_J   =  9;
          int top_index_K    =  4;
          int bottom_index_K =  8;
          
          for(int task_counter = 0; task_counter<task_number; task_counter++)
          {
            for(int step_counter = file_step; step_counter<=step_number; step_counter+=file_step)
            {                
                showSpecimenRegionInRightBasis(task_names[task_counter], left_coord_X, right_coord_X, front_coord_Y, back_coord_Y, 
                                               top_coord_Z, bottom_coord_Z,  step_counter, step_number);
                
           //     showSpecimenRegionInRightBasisForStressTensorFile(task_names[task_counter], left_coord_X, right_coord_X, front_coord_Y, back_coord_Y, 
             //                                  top_coord_Z, bottom_coord_Z,  step_counter, step_number);
                
           //     showSpecimenRegionInRightBasisForStrainTensorFile(task_names[task_counter], left_coord_X, right_coord_X, front_coord_Y, back_coord_Y, 
             //                                  top_coord_Z, bottom_coord_Z,  step_counter, step_number);
                
                showSpecimenRegionInRightBasisForStressTensorFile(task_names[task_counter], left_index_I, right_index_I, front_index_J, back_index_J, 
                                               top_index_K, bottom_index_K,  step_counter, step_number);
              
                showSpecimenRegionInRightBasisForStrainTensorFile(task_names[task_counter], left_index_I, right_index_I, front_index_J, back_index_J, 
                                               top_index_K, bottom_index_K,  step_counter, step_number);
            }
          }
        }
        
        boolean createResFileWithTorsionAngles = false;// true;//
        
        if(createResFileWithTorsionAngles)
        {
          int task_number = 2;
          String[] task_names = new String[task_number]; 
          task_names[0] = "copper_gr10_high_compress_Y";
          task_names[1] = "copper_gr30_high_compress_Y";
       // task_names[1] = "alum_3grns_bending_X";
       // task_names[2] = "alum_3grns_bending_Y";
          file_number   = 100000;
          int file_step = 100000;
          
          for(int task_counter = 0; task_counter<task_number; task_counter++)
          {            
            for(int file_counter = file_step; file_counter<=file_number; file_counter+=file_step)
            {
                read_file_name  = "./user/task_db/"+task_names[task_counter]+"/"+task_names[task_counter]+"_"+file_counter+".res";
                write_file_name = "./user/task_db/"+task_names[task_counter]+"/torsion_angles/"+task_names[task_counter]+"_"+file_counter+"_torsion_angles.res";
                
                createResFileWithTorsionAngles();
            }
          }
        }
        
        boolean createResFileWithLog10Moments = false;// true;// 
        
        if(createResFileWithLog10Moments)
        {
          int file_number = 4;
          String[] file_names = new String[file_number]; 
          
          file_names[0] = "steel_60X60X10_angle15_gb2_cycleStrainZ";
          file_names[1] = "steel_60X60X10_angle30_gb2_cycleStrainZ";
          file_names[2] = "steel_60X60X10_angle45_gb2_cycleStrainZ";
          file_names[3] = "steel_60X60X10_angle60_gb2_cycleStrainZ";
                  
          int step_number   = 20000; // 100000;
          
          int max_superior_value = 0;
          int superior_values[] = new int[file_number];
          boolean new_superior_values = true;
          
          superior_values[0] = 10;
          
          while(new_superior_values)
          {
            new_superior_values = false;
              
            for(int file_counter = 0; file_counter<file_number-3; file_counter++)
            { 
              read_file_name  = "./user/task_db/"+file_names[file_counter]+"/"+file_names[file_counter]+"_"+step_number+"_inst_mom.res";
              write_file_name = "./user/task_db/"+file_names[file_counter]+"/log10_inst_moments/"+file_names[file_counter]+"_"+step_number+"_log10_inst_mom.res";
                
              System.out.println("------------------------------------------------------------------------------");
              superior_values[file_counter] = createResFileWithLog10Values(superior_values[file_counter]);
            }
          
            System.out.println("========================================================================================================");
          
            for(int file_counter = 0; file_counter<file_number; file_counter++)
            if(superior_values[file_counter] > max_superior_value)
            {
              max_superior_value = superior_values[file_counter];
              new_superior_values = true;
            }
          
            if(new_superior_values)
            for(int file_counter = 0; file_counter<file_number; file_counter++)
              superior_values[file_counter] = max_superior_value;
          }
        }
        
        boolean createFileWithTorsionAnglesInPlane = false;// true;// 
        
        if(createFileWithTorsionAnglesInPlane)
        {
          int task_number = 4;
          String[] task_names = new String[task_number]; 
          
      //    task_names[0] = "copper_gr10_cycle_super_high_compress_Y";
      //    task_names[1] = "copper_gr30_cycle_super_high_compress_Y";
          
          task_names[0] = "steel_gr10_steel2_noGr_cycle_top_super_high_compress";
          task_names[1] = "steel_gr10_steel1_noGr_cycle_top_super_high_compress";
          task_names[2] = "steel_gr30_steel2_noGr_cycle_top_super_high_compress";
          task_names[3] = "steel_gr30_steel1_noGr_cycle_top_super_high_compress";
          
          file_number   = 15000; // 20000; // 1000000; // 
          int file_step = 15000; // 20000; //  100000; // 
          
          for(int task_counter = 0; task_counter<task_number; task_counter++)
          {            
            for(int file_counter = file_step; file_counter<=file_number; file_counter+=file_step)
            {
                read_file_name  = "./user/task_db/"+task_names[task_counter]+"/"+task_names[task_counter]+"_"+file_counter+"_torsion.res";
                
            //    write_file_name = "./user/task_db/"+task_names[task_counter]+"/torsion_angles/"+task_names[task_counter]+"_"+file_counter+"_torsion_angles.res";
                
                createFileWithTorsionAnglesInPlane(task_names[task_counter], file_counter);
            }
          }
        }
        
        boolean createGraphAlongGrainBoundaryStep1 = false;// true;// 
        
        if(createGraphAlongGrainBoundaryStep1)
        {
            task_name = "alum_3grns_top_fast_compress";
            file_number   = 100000;
            read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_0"+file_number+"_torsion.res";
            int grain_index;
            
            String write_file_name = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number;
            
            grain_index = 1;
            createGraphAlongGrainBoundaryStep1(grain_index, write_file_name);
            
            grain_index = 2;
            createGraphAlongGrainBoundaryStep1(grain_index, write_file_name);
            
            grain_index = 3;
            createGraphAlongGrainBoundaryStep1(grain_index, write_file_name);
        }
        
        boolean createGraphAlongGrainBoundaryStep2 = false;// true;// 
        
        if(createGraphAlongGrainBoundaryStep2)
        {
            task_name = "alum_3grns_top_fast_compress";
            file_number   = 100000;
            
            double grain1_bound_length = 1;// 41.36471498824836; // 38.420710761561935;// 43.07961740302492;  // 
            double grain2_bound_length = 1;// 38.59294983795753; // 37.4632218434064;  // 45.0016635125869441;// 
            double factor = 0;
            int grain1_index = 1;
            int grain2_index = 2;
                   
            factor = (grain1_bound_length+grain2_bound_length)/(2*grain1_bound_length);// 1;//
          //  read_file_name  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+grain1_index+"_torsion_angles_X.txt";
          //  write_file_name = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+grain1_index+"_bound_length_torsion_angles_X.txt";
            createGraphAlongGrainBoundaryStep2(grain1_index, factor);
            
            factor =  (grain1_bound_length+grain2_bound_length)/(2*grain2_bound_length);// 1;//
          //  read_file_name  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+grain2_index+"_torsion_angles_X.txt";
          //  write_file_name = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+grain2_index+"_bound_length_torsion_angles_X.txt";
            createGraphAlongGrainBoundaryStep2(grain2_index, factor);
        }
        
        // Average values along boundary are calculated.
        boolean calcAverageValuesAlongGrainBoundary = false;// true;// 
        
        if(calcAverageValuesAlongGrainBoundary)
        {
            task_name = "alum_3grns_top_compress";
            file_number   = 100000;
            int file_step = 10000;
            
            int grain1_index = 2;
            int grain2_index = 3;
            
            String read_file_name_1;
            String read_file_name_2;
            
            String[] params = {"mom_X", "mom_Y", "mom_Z", "eff_str", "pr_str"};
            
            for(int param_counter = 0; param_counter < params.length; param_counter++)
            for(int file_counter = file_step; file_counter<=file_number; file_counter+=file_step)
            {
                if(file_counter<file_number)
                {
                  read_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_moments/"+task_name+"_j=01_grains_02-03_bound_length_"+params[param_counter]+"/"+task_name+"_0"+file_counter+"_j=01_grain0"+grain1_index+"_bound_length_"+params[param_counter]+".txt";
                  read_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_moments/"+task_name+"_j=01_grains_02-03_bound_length_"+params[param_counter]+"/"+task_name+"_0"+file_counter+"_j=01_grain0"+grain2_index+"_bound_length_"+params[param_counter]+".txt";
                  write_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_moments/"+task_name+"_0"+file_counter+"_j=01_averages_bound_length_"+params[param_counter]+".txt";
                }
                else
                {
                  read_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_moments/"+task_name+"_j=01_grains_02-03_bound_length_"+params[param_counter]+"/"+task_name+"_"+file_counter+"_j=01_grain0"+grain1_index+"_bound_length_"+params[param_counter]+".txt";
                  read_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_moments/"+task_name+"_j=01_grains_02-03_bound_length_"+params[param_counter]+"/"+task_name+"_"+file_counter+"_j=01_grain0"+grain1_index+"_bound_length_"+params[param_counter]+".txt";
                  write_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_moments/"+task_name+"_"+file_counter+"_j=01_averages_bound_length_"+params[param_counter]+".txt";
                }
                
                calcAverageValuesAlongGrainBoundary(read_file_name_1, read_file_name_2);
            }
        }
        
        // Variable for choice of method of calculation of average coordinates of points from initial file
        // and values of functions in these points
        boolean calcTextFileForAveragePointsGraph = false;// true;// 
        
        if(calcTextFileForAveragePointsGraph)
        {
            task_name = "alum_3grns_top_compress";
            file_number   = 100000;
            int file_step = 100000; 
                
            String read_file_name, write_file_name;
            String[] grains = {"01", "02", "03"};
            int gr_num = grains.length;
            
            String[] params = {"tors_angle_X", "tors_angle_Y", "tors_angle_Z"};
            
            for(int file_counter = file_step; file_counter<=file_number; file_counter+=file_step)
            for(int gr_counter = 0; gr_counter < gr_num; gr_counter++)
            for(int param_counter = 0; param_counter < params.length; param_counter++)
            {
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_torsion_angles/grains_"+grains[gr_counter%gr_num]+"-"+grains[(gr_counter+1)%gr_num]+"/"+task_name+"_"+file_counter+"_j=01_grain"+grains[gr_counter%gr_num]+"_bound_length_"+params[param_counter]+".txt";
                write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_torsion_angles/grains_"+grains[gr_counter%gr_num]+"-"+grains[(gr_counter+1)%gr_num]+"/"+task_name+"_"+file_counter+"_j=01_grain"+grains[gr_counter%gr_num]+"_bound_length_"+params[param_counter]+"_average_points.txt";
                calcTextFileForAveragePointsGraph(read_file_name, write_file_name);
                
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_torsion_angles/grains_"+grains[gr_counter%gr_num]+"-"+grains[(gr_counter+1)%gr_num]+"/"+task_name+"_"+file_counter+"_j=01_grain"+grains[(gr_counter+1)%gr_num]+"_bound_length_"+params[param_counter]+".txt";
                write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_bound_length_torsion_angles/grains_"+grains[gr_counter%gr_num]+"-"+grains[(gr_counter+1)%gr_num]+"/"+task_name+"_"+file_counter+"_j=01_grain"+grains[(gr_counter+1)%gr_num]+"_bound_length_"+params[param_counter]+"_average_points.txt";
                calcTextFileForAveragePointsGraph(read_file_name, write_file_name);
            }
        }
        
        // Variable for choice of method of creation of file with distribution of values of certain parameter 
        // associated with strain tensor in chosen plane
        boolean createSurfaceOfStrainParameterValues = false;// true;// 
        
        if(createSurfaceOfStrainParameterValues)
        {
            task_name = "alumNoGr_topCyl_tension_Y";
            
            createSurfaceOfStrainParameterValues(task_name, "displ_X");
            createSurfaceOfStrainParameterValues(task_name, "displ_Y");
            createSurfaceOfStrainParameterValues(task_name, "displ_Z");
        }
        
        // Variable for choice of method of creation of file with distribution of values of certain parameter 
        // associated with stress tensor in chosen plane
        boolean createSurfaceOfStressParameterValues = false;// true;// 
        
        if(createSurfaceOfStressParameterValues)
        {
            task_name = "alumNoGr_topCyl_tension_Y";
            
            createSurfaceOfStressParameterValues(task_name, "s_XX");
            createSurfaceOfStressParameterValues(task_name, "t_XY");
            createSurfaceOfStressParameterValues(task_name, "t_XZ");
            
            createSurfaceOfStressParameterValues(task_name, "t_YX");
            createSurfaceOfStressParameterValues(task_name, "s_YY");
            createSurfaceOfStressParameterValues(task_name, "t_YZ");
            
            createSurfaceOfStressParameterValues(task_name, "t_ZX");
            createSurfaceOfStressParameterValues(task_name, "t_ZY");
            createSurfaceOfStressParameterValues(task_name, "s_ZZ");
        }   
        
        // Variable for choice of method of creation of file with distribution of 
        // values of certain parameter at cell boundaries
        boolean createSurfaceOfCellBoundParameters = false;// true;// 
        
        if(createSurfaceOfCellBoundParameters)
        {
            task_name = "alumNoGr_topSqTank_tension_XY";
            plane_coord_Z = 1.316;
            
            createSurfaceOfCellBoundParameters(task_name, plane_coord_Z, "bound_stress_X");
            createSurfaceOfCellBoundParameters(task_name, plane_coord_Z, "bound_stress_Y");
            createSurfaceOfCellBoundParameters(task_name, plane_coord_Z, "abs_bound_stress");
            
            createSurfaceOfCellBoundParameters(task_name, plane_coord_Z, "bound_velocity_X");
            createSurfaceOfCellBoundParameters(task_name, plane_coord_Z, "bound_velocity_Y");
            createSurfaceOfCellBoundParameters(task_name, plane_coord_Z, "abs_bound_velocity");            
        }        
        
        boolean createForceMomentTimeGraphs = false;// true;// 
                
        if(createForceMomentTimeGraphs)
        {
            int _max_step_number = 0;
         /*
            _max_step_number = 100000;
            file_number      = 100;
            
            // Creation of text files for 2D-graph (for Grapher) "average absolute force moment - time"
            task_name = "Al_gr10_compress_XZ";
            createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);
            createForceMomentTimeGraphs(file_number, _max_step_number, FRONT_AND_BACK_CELLS);
            
            task_name = "Al_gr30_compress_XZ";
            createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);            
            createForceMomentTimeGraphs(file_number, _max_step_number, FRONT_AND_BACK_CELLS);
         */
            
            file_number      =    100; //   150;//
            _max_step_number = 100000; // 30000;//
            
            // Creation of text files for 2D-graphs "average absolute value - time" (in Grapher or SciDavis)
          //  task_name = "cer_alGr01_copGr10_cycle_top_heating_bottom_cooling";
          //  task_name = "Cer_Algr01_cycle_super_high_compress_low_energy_HAGB";
          //  task_name = "steel_gr10_steel1_noGr_cycle_top_super_high_compress";
          //  task_name = "steel1_noGr_steel_gr05_cycle_bottom_super_high_compress";
            task_name = "steel12GBA_50X50X05_HCP_grains_along_X_tension_X";
            createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);
            
          //  task_name = "cer_alGr05_copGr10_cycle_top_heating_bottom_cooling";
          //  task_name = "Cer_Algr05_cycle_super_high_compress_low_energy_HAGB";
          //  task_name = "steel_gr10_steel2_noGr_cycle_top_super_high_compress";
          //  task_name = "steel2_noGr_steel_gr05_cycle_bottom_super_high_compress";
            task_name = "steel12GBA_50X50X05_HCP_grains_along_X_tension_Y";
            createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);
            
          //  task_name = "cer_alGr01pores_copGr10_cycle_top_heating_bottom_cooling";
          //  task_name = "Cer_Algr01_pores_cycle_super_high_compress_low_energy_HAGB";
          //  task_name = "steel_Gr30_steel1_noGr_cycle_top_super_high_compress";
          //  task_name = "steel1_noGr_steel_gr10_cycle_bottom_super_high_compress";
          //  createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);
            
          //  task_name = "cer_alGr05pores_copGr10_cycle_top_heating_bottom_cooling";
          //  task_name = "Cer_Algr05_pores_cycle_super_high_compress_low_energy_HAGB";
          //  task_name = "steel_Gr30_steel2_noGr_cycle_top_super_high_compress";
          //  task_name = "steel2_noGr_steel_gr10_cycle_bottom_super_high_compress";
          //  createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);
            
          //  task_name = "steel1_noGr_steel_gr20_cycle_bottom_super_high_compress";
          //  createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);
            
          //  task_name = "steel2_noGr_steel_gr20_cycle_bottom_super_high_compress";
          //  createForceMomentTimeGraphs(file_number, _max_step_number, INTERIOR_CELLS);
            
         /*  
            file_number      = 100000;
            _max_step_number = 1000000;
            
            task_name = "copper_gr30_super_high_compress_Y";
            createForceMomentTimeGraphs(file_number, _max_step_number, FRONT_AND_BACK_CELLS);
            
            task_name = "copper_gr10_super_high_compress_Y";
            createForceMomentTimeGraphs(file_number, _max_step_number, FRONT_AND_BACK_CELLS);
         */ 
            
         /*   
            file_number      = 200;
            _max_step_number = 20000;
            
            task_name = "copper_gr10_cycle_super_high_compress_Y_diss";
            createForceMomentTimeGraphs(file_number, _max_step_number, FRONT_AND_BACK_CELLS);
            
            task_name = "copper_gr30_cycle_super_high_compress_Y_diss";
            createForceMomentTimeGraphs(file_number, _max_step_number, FRONT_AND_BACK_CELLS);
         */
        }
        
        //   Variable for choice of method creating files for graphs of average values for cell layers perpendicular to Z axis -
        // "average effective stress - coordinate Z", "average absolute value of force moment - coordinate Z",
        // "average temperature - coordinate Z" - at certain time step
        boolean createGraphs_averageValue_coordZ = false;// true;// 
        
        if(createGraphs_averageValue_coordZ)
        {
            cell_size        = 1.0E-7;
            max_step_number  = 200000;
            time_step_number = 200000;
            
         /*
            task_name = "Cer_Algr01_cycle_super_high_compress";
            createGraphs_averageValue_coordZ(time_step_number);
            
            task_name = "Cer_Algr01_pores_cycle_super_high_compress";
            createGraphs_averageValue_coordZ(time_step_number);
            
            task_name = "Cer_Algr05_cycle_super_high_compress";
            createGraphs_averageValue_coordZ(time_step_number);
            
            task_name = "Cer_Algr05_pores_cycle_super_high_compress";
            createGraphs_averageValue_coordZ(time_step_number);
         */
            task_name = "cer_alGr01_copGr10_cycle_hot_top_cool_bottom_diss";
            createGraphs_averageValue_coordZ(time_step_number);
            
            task_name = "cer_alGr05_copGr10_cycle_hot_top_cool_bottom_diss";
            createGraphs_averageValue_coordZ(time_step_number);
            
            task_name = "cer_alGr01pores_copGr10_cycle_hot_top_cool_bottom_diss";
            createGraphs_averageValue_coordZ(time_step_number);
            
            task_name = "cer_alGr05pores_copGr10_cycle_hot_top_cool_bottom_diss";
            createGraphs_averageValue_coordZ(time_step_number);
        }

        // Variable for choice of method creating files for graphs "average_abs_inst_mom_Y-coord_X", "average_abs_inst_mom_X-coord_Y", 
        // "average_abs_inst_mom_Z-coord_X", "average_abs_inst_mom_Z-coord_Y", 
        // "average_abs_inst_mom-coord_X", "average_abs_inst_mom-coord_X" along direction of loading at given time step.
        boolean createGraphOfAverValueAlongAxis = false;// true;// 
        
        if(createGraphOfAverValueAlongAxis)
        {
            max_step_number  = 100000;
            time_step_number =  12000;
            
        //    task_name = "steel12GBA_50X50X05_HCP_grains_along_X_tension_X";
            task_name = "cer_steel_pores_gr01_steel_top2300_bottom0300_cycle";
            createGraphOfAverValueAlongAxis(time_step_number);
            
        //    task_name = "steel12GBA_50X50X05_HCP_grains_along_X_tension_Y";
        //    createGraphOfAverValueAlongAxis(time_step_number);
        }
        
        // Variable for choice of method creating file for time graphs
        // in certain section perpendicular to chosen axis or for chosen parallelepiped from specimen
        boolean createTimeGraphsForSpecimenPart = false;// true;// 
        
        // Variable responsible for creation of graphs "stress-strain", "stress-time", "strain-time", "absolute moment-time"
        boolean createGraphStressStrain         = false;// true;// 
        
        if(createTimeGraphsForSpecimenPart | createGraphStressStrain)
        {
            double section_coord_X = 0;
            double section_coord_Y = 0;
            double section_coord_Z = 7.848;// 4.582;// 6.215;// 3.766;// 5.399;// 20.096; // 18.463; // 
            
            double min_coord_X = 0;
            double max_coord_X = 0;
            double min_coord_Y = 0;
            double max_coord_Y = 0;
            double min_coord_Z = 0;
            double max_coord_Z = 0;
            
            // Number of tasks
            int task_number = 3;// 5; // 
            
            // Task names
            String[] task_names = new String[task_number];
            
            task_names[0] = "steel09G2S_gr20_60X24X60_cyclic_tension_X";
            task_names[1] = "steel09G2S_gr30X30X6_60X24X60_cyclic_tension_X";
            task_names[2] = "steel09G2S_gr2_5_60X24X60_cyclic_tension_X";
            
            System.out.println();
            System.out.println("-----------============-------------==============-----------------=================");
            
            for(int task_counter = 0; task_counter < task_number; task_counter++)
            {
                // Current task name
                task_name = task_names[task_counter];
                
                if(createTimeGraphsForSpecimenPart)
                {
                  // Minimal and maximal coordinates X, Y, Z
                  min_coord_X = COORD_MIN_VALUE; //  8.5; // 
                  max_coord_X = COORD_MAX_VALUE; // 23.5; // 
                  min_coord_Y = COORD_MIN_VALUE;  
                  max_coord_Y = COORD_MAX_VALUE;
                  min_coord_Z = COORD_MIN_VALUE; // 13.5; // 
                  max_coord_Z = COORD_MAX_VALUE; // 23.5; // 
                
                  System.out.println("Task # "+task_counter+": "+task_name);
                  System.out.println("-----------============-------------==============-----------------=================");
                
                  // Creation of time graph for the whole specimen (for certain single plane of cells from specimen)
                  createTimeGraphsForSpecimenPart(min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                }
                
                if(createGraphStressStrain)
                {
                    // Creation of files for graphs "stress-strain", "stress-time", "strain-time", "absolute moment-time"
                    createGraphStressStrain();
                }
            }
        }
        
        // Variable responsible for creation of archives
        boolean createArchives         = false;// true;// 
        
        if(createArchives)
          createArchives(1000000000, 200000, 2000, "copper_60X10X60_3gr_softCycleBending");
        
        // Variable for choice of method creating file for time graphs of torsion angle components
        // in certain section perpendicular to chosen axis
        boolean createTimeGraphsOfTorsionAnglesForSection = false;// true;// 
        
        if(createTimeGraphsOfTorsionAnglesForSection)
        {
            double section_coord_X = 0;
            double section_coord_Y = 0;
            double section_coord_Z = 1.316;
            
          /*
            task_name = "steel12GBA_50X50X05_HCP_grains_along_X_tension_X";
            createTimeGraphsForSection(section_coord_X, section_coord_Y, section_coord_Z);
        
            task_name = "steel12GBA_50X50X05_HCP_grains_along_X_tension_Y";        
            createTimeGraphsForSection(section_coord_X, section_coord_Y, section_coord_Z);
          */
            
            section_coord_Z = 7.848;// 3.766;// 5.399;// 6.215;// 
                    
            String[] task_names = new String[4];
            
            task_names[0] = "steel40X13_steel_50X50X10_HCP_grains_along_X_tension_X";
            task_names[1] = "steel40X13_steel_50X50X10_HCP_grains_along_X_tension_X_angle15";
            task_names[2] = "steel40X13_steel_50X50X10_HCP_grains_along_Y_tension_X";
            task_names[3] = "steel40X13_steel_50X50X10_HCP_grains_along_Y_tension_X_angle15";
            
            for(int task_counter = 0; task_counter<task_names.length; task_counter++)
            {
                task_name = task_names[task_counter];
                createTimeGraphsOfTorsionAnglesForSection(section_coord_X, section_coord_Y, section_coord_Z);
            }
        }
        
        boolean createTimeGraphsForStressAndStrainVectors = false;// true;// 
        
        if(createTimeGraphsForStressAndStrainVectors)
        {
            int step_number = 100000;
            int file_number =   1000;
            
            time_step = 1.0E-8;
            cell_size = 1.0E-6;
            
            int task_number = 4;
            String[] task_names = new String[task_number];
            task_names[0] = "titan_30X60X60_3gr_top_high_compress";
            task_names[1] = "titan_30X60X60_gr19_top_high_compress";
            task_names[2] = "titan_30X60X60_gr11_top_high_compress";
            task_names[3] = "titan_30X60X60_gr06_top_high_compress";
            
            try
            {
              BufferedWriter bw_grain_bound_stresses, bw_grain_bound_paral_stresses,
                             bw_gr_interior_stresses, bw_gr_interior_stresses_Z, 
                             bw_grain_bound_velocities, bw_grain_bound_paral_velocities, 
                             bw_gr_interior_velocities, bw_gr_interior_velocities_Z;
              
              for(int task_counter = 0; task_counter < task_number; task_counter++)
              {
                String write_file_grain_bound_stresses         = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_grain_bound_stresses.txt";
                String write_file_grain_bound_paral_stresses   = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_grain_bound_paral_stresses.txt";
                
                String write_file_gr_interior_stresses         = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_gr_interior_stresses.txt";    
                String write_file_gr_interior_stresses_Z       = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_gr_interior_stresses_Z.txt";                
                
                String write_file_grain_bound_velocities       = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_grain_bound_velocities.txt";
                String write_file_grain_bound_paral_velocities = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_grain_bound_paral_velocities.txt";
                
                String write_file_gr_interior_velocities       = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_gr_interior_velocities.txt";        
                String write_file_gr_interior_velocities_Z     = "./user/task_db/"+task_names[task_counter]+"/time_graphs/"+task_names[task_counter]+"_gr_interior_velocities_Z.txt";        
                
                bw_grain_bound_stresses         = new BufferedWriter(new FileWriter(write_file_grain_bound_stresses));
                bw_grain_bound_paral_stresses   = new BufferedWriter(new FileWriter(write_file_grain_bound_paral_stresses));
                
                bw_gr_interior_stresses         = new BufferedWriter(new FileWriter(write_file_gr_interior_stresses));
                bw_gr_interior_stresses_Z       = new BufferedWriter(new FileWriter(write_file_gr_interior_stresses_Z));
                
                bw_grain_bound_velocities       = new BufferedWriter(new FileWriter(write_file_grain_bound_velocities));
                bw_grain_bound_paral_velocities = new BufferedWriter(new FileWriter(write_file_grain_bound_paral_velocities));
                
                bw_gr_interior_velocities       = new BufferedWriter(new FileWriter(write_file_gr_interior_velocities));
                bw_gr_interior_velocities_Z     = new BufferedWriter(new FileWriter(write_file_gr_interior_velocities_Z));
                
                createTimeGraphsForStressAndStrainVectors(task_names[task_counter], step_number, file_number,
                                            bw_grain_bound_stresses,   bw_grain_bound_paral_stresses,
                                            bw_gr_interior_stresses,   bw_gr_interior_stresses_Z, 
                                            bw_grain_bound_velocities, bw_grain_bound_paral_velocities, 
                                            bw_gr_interior_velocities, bw_gr_interior_velocities_Z);
              }
              
        //      bw_grain_bound_stresses.close();
          //    bw_gr_interior_stresses.close();
          //    bw_grain_bound_velocities.close();
           //   bw_gr_interior_velocities.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("ERROR: "+io_exc);
            }
        }
        
        // Creation of time graphs for total energy coming to a system
        boolean createTimeGraphTotalEnergies = false;// true; // 
        
        if(createTimeGraphTotalEnergies)
        {
            time_step = 6.0E-13;
            cell_size = 1.0E-8;
            
            int task_number = 2;
            String[] task_names = new String[task_number];
            task_names[0] = "TiAlC_10X10X30_noGr_topCycle1700K_bottom1000K_heatExp";
            task_names[1] = "TiAlC_10X10X30_noGr_topCycle1700K_bottom1000K_noHeatExp";
            
            // Creation of time graphs for total energy coming to a system
            createTimeGraphTotalEnergies(task_names[0]);
            createTimeGraphTotalEnergies(task_names[1]);
        }
        
        // variable responsible for execution of method
        boolean createResultsFileJOCL = true; // false; // 
        
        if(createResultsFileJOCL)
        {
          // Number of time steps between records to files with results
          record_period = 500; // 1000;
        
          // Total number of time steps 
          int step_number = 100000;
          
          // total number of tasks
          int task_number = 3;
          
          // array of task names
          String[] task_names = new String[task_number];
          
          task_names[0] = "steel09G2S_gr20_60X24X60_cyclic_tension_X";
          task_names[1] = "steel09G2S_gr30X6X6_60X24X60_cyclic_tension_X";
          task_names[2] = "steel09G2S_gr2_5_60X24X60_cyclic_tension_X";
          
          for(int task_counter = 0; task_counter < task_number; task_counter++)
          {
            // Creation of files with changed output values for given task and given time steps
            for(int step_counter = record_period; step_counter <= step_number; step_counter += record_period)
              createResultsFileJOCL(task_names[task_counter], step_counter, step_number);
            
            System.out.println("\nFINISH!!! Files for the task '"+task_names[task_counter]+"' are created!\n");
          }
        }
        
        // variable responsible for execution of method
        boolean createGraphFileJOCL = true;// false;// 
        
        if(createGraphFileJOCL)
        {
          // Number of time steps between records to files with results
          record_period = 500; // 1000; 
        
          // Total number of time steps 
          int step_number = 100000;
          
          // The value of time step in seconds
          time_step = 1.0E-8;
          
          // total number of tasks
          int task_number = 3;
          
          // array of task names
          String[] task_names = new String[task_number];
          
          task_names[0] = "steel09G2S_gr20_60X24X60_cyclic_tension_X";
          task_names[1] = "steel09G2S_gr30X6X6_60X24X60_cyclic_tension_X";
          task_names[2] = "steel09G2S_gr2_5_60X24X60_cyclic_tension_X";
          
          // arrays of minimal and maximal coordinates Z of chosen region of specimen          
          double[] min_coord_Z = new double[task_number];
          double[] max_coord_Z = new double[task_number];
          
          // boolean value responsible for writing of minimal and maximal coordinates in the name of output file
          boolean write_coordinates = false;
                    
          for(int task_counter = 0; task_counter < task_number; task_counter++)
          {
            min_coord_Z[task_counter] = 0.0;
            max_coord_Z[task_counter] = 1.0E300;
          
            // Creation of files with data for building of graphs for given task and given time steps
            createGraphFilesJOCL(task_names[task_counter], record_period, step_number, min_coord_Z[task_counter], max_coord_Z[task_counter], time_step, write_coordinates);
            
            System.out.println("FINISH!!! Files for the task '"+task_names[task_counter]+"' are created!\n\n");
          }
        }
        
        // Variable for execution of method
        boolean createSection = false; // true; // 
        
        if(createSection)
        {
          int step_number = 20000;
          double section_coord = 0.75;
          String axis = "Y";
          
          cell_size_X = 1.0;
          cell_size_Y = 1.0;
          cell_size_Z = 1.0;
          
          int task_number = 3; // 5;// 
          
          String[] task_name = new String[task_number];
          
         /* 
          task_name[0] = "steel_17G1S_213K_60X15X50_HCP_tension_X";
          task_name[1] = "steel_17G1S_233K_60X15X50_HCP_tension_X";
          task_name[2] = "steel_17G1S_253K_60X15X50_HCP_tension_X";
          task_name[3] = "steel_17G1S_273K_60X15X50_HCP_tension_X";
          task_name[4] = "steel_17G1S_293K_60X15X50_HCP_tension_X";
          */
         
          task_name[0] = "steel_17G1S_30X05X30_gr01_gr05_top_compress";
          task_name[1] = "steel_17G1S_30X05X30_gr01_top_compress";
          task_name[2] = "steel_17G1S_30X05X30_gr05_top_compress";
         
          for(int task_counter = 0; task_counter < task_number; task_counter++)
          {
            createSection(task_name[task_counter], step_number, "force_moment_X", section_coord, axis);
            createSection(task_name[task_counter], step_number, "force_moment_Y", section_coord, axis);
            createSection(task_name[task_counter], step_number, "force_moment_Z", section_coord, axis);
          }
        }
    }
    
    /** Creation of 3D cellular automaton simulating specimen
     */
    public void createSpecimenR3()
    {
        specimenR3 = new ArrayList(0);

        // Creation of list of grains
        grainsCluster = new ArrayList(0);

        Cluster grain;

        String grainParameters;

        StringTokenizer st;

        int grainID;

        String grainMaterial;

        try
        {
            // New buffering character-input stream
            BufferedReader br = new BufferedReader(new FileReader(grains_file));

            // Reading of number of grains.
            int grainsNumber = (new Integer(br.readLine())).intValue();

            for (int grainCounter = 0; grainCounter < grainsNumber; grainCounter++)
            {
                // Creation of grain as a list of cells
                grain = new Cluster();

                grainParameters = br.readLine();
                st = new StringTokenizer(grainParameters);

                if(st.hasMoreTokens())
                {
                    // Reading of grain parameters from file
                    grainID        = (new Integer(st.nextToken())).intValue();
                    grainMaterial  = st.nextToken();

                    // Setting of these parameters to grain
                    grain.setID(grainID);
                    grain.setMaterialName(grainMaterial);
                    grain.setMaterial(new Material(grainMaterial));
                }

                // Addition of grain to list of grains
                grainsCluster.add(grain);
            }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        // Calculation of numbers of cells on directions of X-, Y- and Z-axes.
        cellNumberI = (int)Math.round(specimen_size_X/cell_size_X);
        cellNumberJ = (int)Math.round(specimen_size_Y/cell_size_Y);
        cellNumberK = (int)Math.round(specimen_size_Z/cell_size_Z);

        // Reading of information about bulk material and bulk state.
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(specimen_file));

            String bulk = br.readLine();
            st = new StringTokenizer(bulk);

            bulk_material = st.nextToken();

            System.out.println("Bulk material = " + bulk_material);

            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        RecCellR3 defCellR3;
        cellcore.Three indices;
        int cell_index;

        RecCellR3 incl_cell;
        Three incl_cell_indices;

        String incl_cell_material;
        byte incl_cell_state;
        String incl_cell_string;
        int incl_cell_index;

        // Single indices of cells at 1st, 2nd and 3rd coordination spheres
        neighbours3D = new int[cellNumberI*cellNumberJ*cellNumberK][54];

        // Single indices of cells at 1st coordination sphere
        neighbours1S = new int[cellNumberI*cellNumberJ*cellNumberK][12];

        // Cell coordinates
        VectorR3 cell_coordinates;

        // Creation of specimen as a net of CA.
        for (int k_index = 0; k_index < cellNumberK; k_index++)
        {
          System.out.println("Starting creation of "+k_index+" plane...");

          for (int j_index = 0; j_index < cellNumberJ; j_index++)
          {
            for (int i_index = 0; i_index < cellNumberI; i_index++)
            {
                // Creation of triple index of cell3D
                indices = new cellcore.Three(i_index, j_index, k_index);

                cell_index = i_index + j_index*cellNumberI +
                             k_index*cellNumberI*cellNumberJ;

                // Creation of the new cell3D.
                defCellR3 = new RecCellR3(cell_size_X, cell_size_Y, cell_size_Z,
                                          indices, bulk_material, packing_type);

                // Setting of cell neighbours at 1st, 2 nd and 3rd coordinational spheres
                setNeighbours3D(cell_index);

                // Setting of cell neighbours at 1st coordinational sphere
                setNeighbours1S(cell_index);

                // Calculation of cell coordinates
                cell_coordinates = calcCoordinates(i_index, j_index, k_index);

                defCellR3.setCoordinates(cell_coordinates);
             //   defCellR3.setNeighbourIndicesHCP(neighbours1S[cell_index]);

                // Addition of the new cell3D to the specimen3D.
                specimenR3.add(defCellR3);
            }
          }

          System.out.println("Finished creation of "+k_index+" plane!");
        }

        System.out.println(" Specimen size= "+specimenR3.size());

        // Distribution of 3D cells on clusters/grains
        for(int cellCounter = 0; cellCounter < specimenR3.size(); cellCounter++)
        {
            // Current cell
            defCellR3 = (RecCellR3)specimenR3.get(cellCounter);

            // Obtaining of index (ID) of grain containing cell
            grainID = defCellR3.getGrainIndex();

            if(grainID>-1)
            {
                // Obtaining of grain with this index (ID)
                grain = (Cluster)grainsCluster.get(grainID);

                // Setting of Euler angles
                defCellR3.setEulerAngles(grain.getAngles());

                // Setting of material of cell
                defCellR3.setMaterial(grain.getMaterial());
                defCellR3.setMaterialFile(grain.getMaterialName());

                // Calculation of cell thermal energy, which is changed due to heat expansion
                defCellR3.calcThermalEnergy();

                // Addition of cell single index to list of indices of cells
                //those belong to this grain
                grain.add(cellCounter);

                // Setting of grain with added cell into cluster of grains
                grainsCluster.set(grainID, grain);
            }
        }

        calcCellVolumeHCP();
        calcCellSurfaceHCP();
    }

    /** The method calculates volume of cell3D (in case of hexagonal close packing).
     */
    public void calcCellVolumeHCP()
    {
        cell_volume = 0.6938*cell_size_X*cell_size_Y*cell_size_Z;
    }

    /** The method calculates volume of cell3D (in case of hexagonal close packing).
     * @param cell_size the diameter of cell
     * @return volume of cell3D (in case of hexagonal close packing)
     */
    public double calcCellVolumeHCP(double cell_size)
    {
        return 0.6938*cell_size*cell_size*cell_size;
    }
    
    /** The method calculates surface of cell3D facet (in case of hexagonal close packing).
     */
    public void calcCellSurfaceHCP()
    {
        cell_surface = 0.3469*cell_size_X*cell_size_Y;
    }

    /** The method creates file with information about distribution
     * of the logarithms of the values from given file.
     */
    private void createFileWithLog10Values()
    {
        String string;
        StringTokenizer st;

        // Parameters of cell
        double coordinate_X;
        double coordinate_Y;
        double value;
        double log10_value;
        
        // Absolute value
        double abs_value;
        
        // Minimal absolute value
        double min_abs_value       = 1.0E308;
        
        // Minimal value of log10(abs_value)
        double min_log10_abs_value = 1.0E308;

        // Superior absolute value of log10(abs_value)
        int sup_abs_log10_abs_value = 0; // 29;// 2;// 

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            while(br.ready())
            {
                string = br.readLine();

                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    value        = new Double(st.nextToken()).doubleValue();

                    abs_value = value;//Math.abs(value);

                    if((abs_value < min_abs_value)&(abs_value > 0))
                        min_abs_value = abs_value;

                    if(1.0 + abs_value > 0)
                        log10_value = Math.log10(1.0 + abs_value);
                    else
                    {
                      //  System.out.println("Coordinates: ("+coordinate_X+" "+coordinate_Y+"). Mom_Z: "+value);
                        log10_value = -1.0E308;
                    }
                    
                    if(log10_value < min_log10_abs_value)
                        min_log10_abs_value = log10_value;

                    if(min_log10_abs_value + sup_abs_log10_abs_value < 0)
                    {
                        System.out.println("log10(value): "+min_log10_abs_value);
                        System.out.println("Superior abs(log10(abs_value)): "+sup_abs_log10_abs_value);
                        System.out.println("Please, increase the superior value!!!");
                        System.out.println();
                        br.close();
                    }

                    log10_value = Math.signum(value)*(sup_abs_log10_abs_value+log10_value);

                    {
                        bw.write(coordinate_X+" "+coordinate_Y+" "+log10_value);
                        bw.newLine();
                        bw.flush();
                    }
                }
            }

            System.out.println("File "+read_file_name+" is read.");
            System.out.println("File "+write_file_name+" is written.");
            System.out.println("Minimal absolute non-zero value: "+min_abs_value);
            System.out.println("Minimal log10(value):            "+min_log10_abs_value);
            System.out.println();

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file with changed output values for given task at current time step.
     * @param task_name name of task
     * @param step_index current time step
     * @param step_number total number of time steps
     */
    private void createResultsFileJOCL(String task_name, int step_index, int step_number)
    {
        System.out.println("Task name: "+task_name+"; step # "+step_index+"; total number of steps: "+step_number+"; ratio: "+(step_index*1.0)/(step_number*1.0));
        
        String string;
        String zeros = "";
        StringTokenizer st;
        
        // Exponent of power of 10 in standard representation of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation of current time step
        int exponent = (int)Math.floor(Math.log10(step_index));
        
        for(int zero_counter = exponent; zero_counter < max_exponent; zero_counter++)
          zeros = zeros + "0";
        
        // Input and output files 
        read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_index+"_jocl.res";
        write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_index+"_relValues.res";
        
        // total number of inner and outer cells
        int cell_number = 0;
        
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
        
        // Cell parameters calculated for output files
        double total_spec_energy;
        double rel_spec_elast_energy;
        double rel_spec_tors_energy;
        double abs_force_moment;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            
            bw.write("# Each string contains parameters of corresponding cell: \n" +
                    "# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; \n" +
                    "# 6. temperature; 7. relative specific elastic energy; 8. total specific energy; \n" +
                    "# 9-11. absolute values of 3 components of specific force moment calculated for the 1st coordination sphere; \n" +
                    "# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n" +
                    "# 13. absolute value of specific force moment; 14. relative specific dissipated energy. \n");
            bw.flush();

            while(br.ready())
            {
              string = br.readLine();
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
                
                // Calculation of cell parameters
                abs_force_moment = Math.sqrt(force_moment_X*force_moment_X + 
                                             force_moment_Y*force_moment_Y + force_moment_Z*force_moment_Z);
                
                spec_elast_energy = Math.abs(spec_elast_energy);
                total_spec_energy = spec_elast_energy + spec_tors_energy;
                
                if(total_spec_energy > 0.0)
                {
                  rel_spec_elast_energy = spec_elast_energy/total_spec_energy;
                  rel_spec_tors_energy  = spec_tors_energy/total_spec_energy;
                }
                else
                {
                  rel_spec_elast_energy = 0.0;
                  rel_spec_tors_energy  = 0.0;
                  
                  if(total_spec_energy < 0)
                    System.out.println("ERROR!!! Total energy must be positive!!! But total_spec_energy = "+total_spec_energy);
                }
                
                bw.write(en_type+" "+loc_type+" "+gr_index+" "+coordinate_X+" "+coordinate_Y+" "+coordinate_Z+" "+
                         temperature+" "+rel_spec_elast_energy+" "+total_spec_energy+" "+
                         Math.abs(force_moment_X)+" "+Math.abs(force_moment_Y)+" "+Math.abs(force_moment_Z)+" "+
                         state+" "+abs_force_moment+" "+rel_spec_tors_energy+"\n");
                bw.flush();
                
                cell_number++;
              }
            }
            
            System.out.println("Total number of inner and outer cells: "+cell_number);

            System.out.println("File "+read_file_name+" is read.");
            System.out.println("File "+write_file_name+" is written.");
            System.out.println();

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates files with data for building of graphs for given task and given time steps.
     * @param task_name name of task
     * @param record_period number of time steps between records to files with results
     * @param step_number total number of time steps
     * @param min_coord_Z minimal coordinate Z of analyzed region
     * @param max_coord_Z maximal coordinate Z of analyzed region
     * @param time_step the value of time step in seconds
     * @param write_coordinates boolean value responsible for writing of minimal and maximal coordinates in the name of output file
     */
    private void createGraphFilesJOCL(String task_name, int record_period, int step_number, double min_coord_Z, double max_coord_Z, 
                                      double time_step, boolean write_coordinates)
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
      
      int int_min_coord_Z = (int)Math.round(min_coord_Z);
      int int_max_coord_Z = (int)Math.round(max_coord_Z);
      
      try
      {
        // Creation of new directory for files with data for graphs
        File graphs_dir;
        
        graphs_dir = new File("./user/task_db/"+task_name+"/time_graphs");
        
        if(!graphs_dir.exists())
          graphs_dir.mkdir();
        else
        {
          graphs_dir.delete();
          graphs_dir.mkdir();
        }
        
        BufferedWriter bw_specElastEnergy;
        BufferedWriter bw_forceMomX;
        BufferedWriter bw_forceMomY;
        BufferedWriter bw_forceMomZ;
        BufferedWriter bw_specTorsEnInflux;
        BufferedWriter bw_specTorsEnergy;        
        BufferedWriter bw_relSpecElastEnergy;
        BufferedWriter bw_absForceMoment;
        BufferedWriter bw_relSpecTorsEnergy;
        BufferedWriter bw_elastEnTotalPortion;
        BufferedWriter bw_torsEnTotalPortion;
        
        if(write_coordinates)
        {
           // Creation of files with data for graphs
           bw_specElastEnergy    = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_spec_elast_energy.txt"));
           bw_forceMomX          = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_abs_force_moment_X.txt"));
           bw_forceMomY          = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_abs_force_moment_Y.txt"));
           bw_forceMomZ          = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_abs_force_moment_Z.txt"));
           bw_specTorsEnInflux   = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_spec_tors_en_influx.txt"));
           bw_specTorsEnergy     = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_spec_tors_energy.txt"));
           bw_relSpecElastEnergy = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_rel_spec_elast_energy.txt"));
           bw_absForceMoment     = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_abs_force_moment.txt"));
           bw_relSpecTorsEnergy  = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_aver_rel_spec_tors_energy.txt"));
           bw_elastEnTotalPortion= new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_elast_energy_total_portion.txt"));
           bw_torsEnTotalPortion = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+int_min_coord_Z+"-"+int_max_coord_Z+"_tors_energy_total_portion.txt"));
        }
        else
        {
           // Creation of files with data for graphs
           bw_specElastEnergy    = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_spec_elast_energy.txt"));
           bw_forceMomX          = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment_X.txt"));
           bw_forceMomY          = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment_Y.txt"));
           bw_forceMomZ          = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment_Z.txt"));
           bw_specTorsEnInflux   = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_spec_tors_en_influx.txt"));
           bw_specTorsEnergy     = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_spec_tors_energy.txt"));
           bw_relSpecElastEnergy = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_rel_spec_elast_energy.txt"));
           bw_absForceMoment     = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_abs_force_moment.txt"));
           bw_relSpecTorsEnergy  = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_rel_spec_tors_energy.txt"));
           bw_elastEnTotalPortion= new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_elast_energy_total_portion.txt"));
           bw_torsEnTotalPortion = new BufferedWriter(new FileWriter("./user/task_db/"+task_name+"/time_graphs/"+task_name+"_tors_energy_total_portion.txt"));
        }
        
        bw_specElastEnergy.write    (0.0+" "+0.0+"\n");
        bw_forceMomX.write          (0.0+" "+0.0+"\n");
        bw_forceMomY.write          (0.0+" "+0.0+"\n");
        bw_forceMomZ.write          (0.0+" "+0.0+"\n");
        bw_specTorsEnInflux.write   (0.0+" "+0.0+"\n");
        bw_specTorsEnergy.write     (0.0+" "+0.0+"\n");
        bw_relSpecElastEnergy.write (0.0+" "+1.0+"\n");
        bw_absForceMoment.write     (0.0+" "+0.0+"\n");
        bw_relSpecTorsEnergy.write  (0.0+" "+0.0+"\n");
        bw_elastEnTotalPortion.write(0.0+" "+1.0+"\n");
        bw_torsEnTotalPortion.write (0.0+" "+0.0+"\n");
        
        for(int step_index = record_period; step_index <= step_number; step_index += record_period)
        {
          cur_time = time_step*step_index;
          
        //  cur_time = Math.round(cur_time*1.0E9)/1.0E3;
          cur_time = Math.round(cur_time*1.0E9)/1.0E6;
          
          System.out.println("Task name: "+task_name+"; step # "+step_index+"; time: "+cur_time+" ms; "+
                             "total number of steps: "+step_number+"; ratio: "+(step_index*1.0)/(step_number*1.0));
          
          exponent = (int)Math.floor(Math.log10(step_index));
                
          zeros = "";
          
          for(int zero_counter = exponent; zero_counter < max_exponent; zero_counter++)
            zeros = zeros + "0";
        
          // Input files 
          read_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_index+"_jocl.res";
          read_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_index+"_relValues.res";
        
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
                if(coordinate_Z >= min_coord_Z & coordinate_Z <= max_coord_Z | !write_coordinates)
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
          
          elast_energy_total_portion = Math.abs(aver_spec_elast_energy)/(Math.abs(aver_spec_elast_energy) + aver_spec_tors_energy);
          tors_energy_total_portion  = aver_spec_tors_energy/(Math.abs(aver_spec_elast_energy) + aver_spec_tors_energy);
          
          aver_temperature         = aver_temperature/cell_number;
          aver_spec_elast_energy   = aver_spec_elast_energy/cell_number;
          aver_abs_force_moment_X  = aver_abs_force_moment_X/cell_number;
          aver_abs_force_moment_Y  = aver_abs_force_moment_Y/cell_number;
          aver_abs_force_moment_Z  = aver_abs_force_moment_Z/cell_number;
          aver_spec_tors_en_influx = aver_spec_tors_en_influx/cell_number;
          aver_spec_tors_energy    = aver_spec_tors_energy/cell_number;
          
          aver_spec_elast_energy   = Math.round(aver_spec_elast_energy*1.0E6)/1.0E12;
          aver_abs_force_moment_X  = Math.round(aver_abs_force_moment_X*1.0E6)/1.0E12;
          aver_abs_force_moment_Y  = Math.round(aver_abs_force_moment_Y*1.0E6)/1.0E12;
          aver_abs_force_moment_Z  = Math.round(aver_abs_force_moment_Z*1.0E6)/1.0E12;
          aver_spec_tors_en_influx = Math.round(aver_spec_tors_en_influx*1.0E6)/1.0E12;
          aver_spec_tors_energy    = Math.round(aver_spec_tors_energy*1.0E6)/1.0E12;
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
          
          System.out.println("aver_spec_elast_energy = "+aver_spec_elast_energy+" MPa; aver_spec_tors_energy = "+aver_spec_tors_energy+" MPa; "+
                             "elast_energy_total_portion = "+elast_energy_total_portion+" %; tors_energy_total_portion = "+tors_energy_total_portion+" %;\n"+
                             "aver_spec_tors_en_influx = "+aver_spec_tors_en_influx+" MPa; aver_abs_force_moment_X = "+aver_abs_force_moment_X+" MPa; "+
                             "aver_abs_force_moment_Y = "+aver_abs_force_moment_Y+" MPa; aver_abs_force_moment_Z = "+aver_abs_force_moment_Z+" MPa;");
          
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
                state                 = new Byte(st.nextToken()).byteValue();
                abs_force_moment      = new Double(st.nextToken()).doubleValue();
                rel_spec_tors_energy  = new Double(st.nextToken()).doubleValue();
                
                if(en_type  == Common.INNER_CELL | en_type == Common.LAYER_CELL)
                if(loc_type != Common.OUTER_CELL)
                if(coordinate_Z >= min_coord_Z & coordinate_Z <= max_coord_Z | !write_coordinates)
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
          aver_abs_force_moment      = Math.round(aver_abs_force_moment*1.0E6)/1.0E12;
          aver_rel_spec_tors_energy  = aver_rel_spec_tors_energy*1.0E2;
          
          bw_relSpecElastEnergy.write(cur_time+" "+aver_rel_spec_elast_energy+"\n");
          bw_absForceMoment.write    (cur_time+" "+aver_abs_force_moment+"\n");
          bw_relSpecTorsEnergy.write (cur_time+" "+aver_rel_spec_tors_energy+"\n");
          
          bw_relSpecElastEnergy.flush();
          bw_absForceMoment.flush();
          bw_relSpecTorsEnergy.flush();
          
          System.out.println("aver_abs_force_moment = "+aver_abs_force_moment+" MPa; aver_rel_spec_elast_energy = "+aver_rel_spec_elast_energy+" %; "+
                             "aver_rel_spec_tors_energy = "+aver_rel_spec_tors_energy+" %.");
          System.out.println("Number of cells in the analyzed region of the specimen: "+cell_number+"\n");
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
        
        System.out.println("Files for graphs in the directory './user/task_db/"+task_name+"/time_graphs' are created.");
        System.out.println();
      }
      catch(IOException io_exc)
      {
        System.out.println("ERROR!!! IOException: "+io_exc);
      }
    }
    
    /** The method creates file with parameters of cells located
     * in plane of automata along axis OX.
     * @param step_number number of time step corresponding to read file
     * @param index_X number of plane of automata along axis OX
     */
    public void createSectionX(int step_number, int index_X)
    {
        read_file_name  = "./task/stresses_2009_05_31_"+step_number+".txt";
        write_file_name = "./task/stresses_2009_05_31_"+step_number+"_x="+index_X+".txt";

        // Coordinate X of cells in plane
        plane_coord_X = (1.577+index_X*1.732);//*0.5;

        String string;

        // Parameters of cell
        int state;
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double value;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            // Reading of the number of strings of the file from the file
            int string_number = new Integer(br.readLine()).intValue();

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state        = new Integer(st.nextToken()).intValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();
                    value  = new Double(st.nextToken()).doubleValue();
                //    value  = new Double(st.nextToken()).doubleValue();
                //    value  = new Double(st.nextToken()).doubleValue();

                    coordinate_Y = coordinate_Y/2;
                    coordinate_Z = coordinate_Z/2;
                /*
                    coordinate_X = coordinate_X*1.0E6;
                    coordinate_Y = coordinate_Y*1.0E6;
                    coordinate_Z = coordinate_Z*1.0E6;
                  */
                    if (Math.abs(coordinate_X-plane_coord_X) < 1.0)//0.5)
              //      if(state!=0)
                    {
                        bw.write(coordinate_Y+" "+coordinate_Z+" "+value);
                   //     bw.write(coordinate_Y+" "+coordinate_Z+" "+state);
                        bw.newLine();

                        bw.flush();
                    }
                }
            }

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file with parameters of cells located
     * in plane of automata along axis OY.
     * @param step_number number of time step corresponding to read file
     * @param index_Y number of plane of automata along axis OY
     */
    public void createSectionY(int step_number, int index_Y)
    {
      //    read_file_name  = "./task/stresses_2009_06_23_"+step_number+".res";
      //    write_file_name = "./task/stresses_2009_06_23_"+step_number+"_y="+index_Y+".txt";

      //  read_file_name  = "./task/principal_stresses_2009_06_23_"+step_number+".txt";
      //  write_file_name = "./task/principal_stresses_2009_06_23_"+step_number+"_y="+index_Y+".txt";

        read_file_name  = "./task/force_moments_2009_06_23_"+step_number+".txt";
        write_file_name = "./task/force_moments_2009_06_23_"+step_number+"_y="+index_Y+"_coord_Z.txt";

        // Coordinate X of cells in plane
        plane_coord_Y = (1.5 + 2*index_Y)*cell_size_Y;

        String string;

        // Parameters of cell
        int state;
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double value;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            // Reading of the number of strings of the file from the file
            int string_number = new Integer(br.readLine()).intValue();

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state        = new Integer(st.nextToken()).intValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();
                    value        = new Double(st.nextToken()).doubleValue();
                    value        = new Double(st.nextToken()).doubleValue();
                    value        = new Double(st.nextToken()).doubleValue();
                /*
                    coordinate_X = coordinate_X - Math.sqrt(3.0/4.0);
                    coordinate_Z = coordinate_Z - Math.sqrt(2.0/3.0);

                    coordinate_X = coordinate_X*1.0E6;
                    coordinate_Y = coordinate_Y*1.0E6;
                    coordinate_Z = coordinate_Z*1.0E6;
                  */
                    if (Math.abs(coordinate_Y-plane_coord_Y) < cell_size_Y/2)
                    if(state<=grain_number)
                    {
                        bw.write(coordinate_X+" "+coordinate_Z+" "+value);
                   //     bw.write(coordinate_Y+" "+coordinate_Z+" "+state);
                        bw.newLine();

                        bw.flush();
                    }
                }
            }

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file with parameters of cells located
     * in plane of automata along axis OZ.
     * @param step_number number of time step corresponding to read file
     * @param index_Z number of plane of automata along axis OZ
     */
    public void createSectionZ(int step_number, int index_Z)
    {
      //  read_file_name  = "./task/stresses_2009_09_18_"+step_number+".txt";
      //  write_file_name = "./task/stresses_2009_09_18_"+step_number+"_z="+index_Z+".txt";

     //     read_file_name  = "./task/mech_load_XY_"+step_number+".res";
    //      write_file_name = "./task/mech_load_XY_"+step_number+"_z="+index_Z+".txt";

     //   read_file_name  = "./task/principal_stresses_2009_09_24_"+step_number+".txt";
     //   write_file_name = "./task/principal_stresses_2009_09_24_"+step_number+"_z="+index_Z+".txt";

        read_file_name  = "./task/force_moments_2009_09_24_"+step_number+".txt";
        write_file_name = "./task/force_moments_2009_09_24_"+step_number+"_z="+index_Z+"_coord_X.txt";
        String write_file_name_1 = "./task/force_moments_2009_09_24_"+step_number+"_z="+index_Z+"_coord_Y.txt";
        String write_file_name_2 = "./task/force_moments_2009_09_24_"+step_number+"_z="+index_Z+"_coord_Z.txt";

        // Coordinate X of cells in plane
        plane_coord_Z = (0.5 + (index_Z-1)*Math.sqrt(2.0/3.0));//*cell_size_Z;

        String string;

        // Parameters of cell
        int state;
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double temperature;
        double value, value1, value2;
        int cell_number = 0;
        byte type = -1;

        // Triple index of cell
        Three cell_indices;

        int stringCounter = 0;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(write_file_name_1));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));

            // Reading of the number of strings of the file from the file
        //    int string_number = new Integer(br.readLine()).intValue();

         //   for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            while(br.ready())
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                // Triple index of cell
                cell_indices = calcTripleIndex(stringCounter, cellNumberI, cellNumberJ, cellNumberK);

                stringCounter++;
                
                //TEST
                /*
                bw.write(cell_indices.getI()+" "+cell_indices.getJ()+" "+cell_indices.getK());
                bw.newLine();
                bw.flush();
                */
                if((cell_indices.getI()>9)&(cell_indices.getI()<cellNumberI-10))
                if((cell_indices.getJ()>9)&(cell_indices.getJ()<cellNumberJ-10))
            //    if(cell_indices.getK() == index_Z)
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state        = new Integer(st.nextToken()).intValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();
                    value        = new Double(st.nextToken()).doubleValue();
                    value1       = new Double(st.nextToken()).doubleValue();
                    value2       = new Double(st.nextToken()).doubleValue();
                 //   type         = new Byte(st.nextToken()).byteValue();
                 //   type         = new Byte(st.nextToken()).byteValue();
                 //   if(cell_indices.getK() == index_Z)
                    if (Math.abs(coordinate_Z-plane_coord_Z) <= 0.5)//*cell_size_Z)
                 //   if(state<=grain_number)
                 //   if(type==1)
                    {                        
                     //   coordinate_X = coordinate_X*1.0E6 - Math.sqrt(3.0/4.0);
                     //   coordinate_Y = coordinate_Y*1.0E6 - 1.0;
                    //    coordinate_Z = coordinate_Z*1.0E6 - Math.sqrt(2.0/3.0);

                        bw.write(coordinate_X+" "+coordinate_Y+" "+value);
                        bw1.write(coordinate_X+" "+coordinate_Y+" "+value1);
                        bw2.write(coordinate_X+" "+coordinate_Y+" "+value2);
                    
                        bw.newLine();
                        bw1.newLine();
                        bw2.newLine();

                        // Calculation of number of cells in plane
                 //       cell_number++;

                        bw.flush();
                        bw1.flush();
                        bw2.flush();
                    }
                }
            }

         //   bw.write(cell_number+"");

            br.close();
            bw.close();
            bw1.close();
            bw2.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
    
    /** The method creates file with parameters of cells for right basis.
     */
    private void createResFileForRightBasis()
    {
        String string;
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();

                        // Replacement of coordinate X and coordinate Y
                        string = tokens[3];
                        tokens[3] = tokens[4];
                        tokens[4] = string;

                        // Writing of coordinates and physical parameters of current cell
                        for(int token_counter=0; token_counter<token_number; token_counter++)
                            bw.write(tokens[token_counter]+" ");
                    }
                    else
                    {
                        bw.write(string);
                    }
                    
                    bw.newLine();
                    bw.flush();
                }
            }

            System.out.println("File '"+write_file_name+"' is written.");
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
            
    /** The method creates file with parameters of torsion angles of cells.
     */
    private void createResFileWithTorsionAngles()
    {
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        // String with parameters of current cell
        String string;
        
        // Cell parameters: components of force moment, cell radius, shear modulus
        double mom_X, mom_Y, mom_Z, abs_mom, cell_radius, shear_mod;
        
        cell_radius = 5.00E-7;
        shear_mod   = 2.65E10;
        
        // Components of vector of torsion angle of current cell and its absolute value
        double torsion_angle_X, torsion_angle_Y, torsion_angle_Z, torsion_angle;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            
            bw.write("# Each string contains parameters of corresponding CA:\n");
            bw.write("# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 6. temperature;\n");
            bw.write("# 7. effective stress; 8. principal stress; 9-11. 3 components of torsion angle;\n");
            bw.write("# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 13. strain;\n");
            bw.write("# 14. density of dislocations.\n");
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#") )// & !tokens[0].equals("11"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();

                        // Calculation of torsion angle for current cell
                        mom_X = (new Double(tokens[9])).doubleValue();
                        mom_Y = (new Double(tokens[10])).doubleValue();
                        mom_Z = (new Double(tokens[11])).doubleValue();
                        
                        torsion_angle_X = 2*mom_X/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                        torsion_angle_Y = 2*mom_Y/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                        torsion_angle_Z = 2*mom_Z/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                        
                        // Transition from radians to degrees
                        torsion_angle_X = torsion_angle_X*180/Math.PI;
                        torsion_angle_Y = torsion_angle_Y*180/Math.PI;
                        torsion_angle_Z = torsion_angle_Z*180/Math.PI;
                        
                   //     abs_mom = Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                     //   torsion_angle = Math.sqrt(torsion_angle_X*torsion_angle_X + torsion_angle_Y*torsion_angle_Y + torsion_angle_Z*torsion_angle_Z);

                        // Writing of coordinates and physical parameters of current cell
                        for(int token_counter=0; token_counter<9; token_counter++)
                            bw.write(tokens[token_counter]+" ");
                        
                        bw.write(torsion_angle_X+" "+torsion_angle_Y+" "+torsion_angle_Z+" ");
                        
                        for(int token_counter=12; token_counter<token_number; token_counter++)
                            bw.write(tokens[token_counter]+" ");
                        
                        bw.newLine();
                    }
                    
                    bw.flush();
                }
            }

            System.out.println("File '"+write_file_name+"' is written.");
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
             
     /** The method creates standard file with results changing the components of couple forces by its logarithms.
      * @param _superior_int_value initial superior positive integer for negative logarithm values
      * @return superior positive integer for negative logarithm values
     */
    private int createResFileWithLog10Values(int _superior_int_value)
    {
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        // String with parameters of current cell
        String string;
        
        // Cell parameters: components of force moment, cell radius, shear modulus
        double mom_X, mom_Y, mom_Z, abs_mom, cell_radius, shear_mod;
        
        // Components of vector of torsion angle of current cell and its absolute value
        double log10_mom_X, log10_mom_Y, log10_mom_Z;
        
        double log10_abs_inst_mom = 0; 
        double log10_tors_en_influx = 0;
        double spec_tors_en_influx = 0;
        
        int superior_int_value = _superior_int_value;
        
        boolean begin_new_cycle = true;
        
        while(begin_new_cycle)
        {
          try
          {
              begin_new_cycle = false;
              BufferedReader br = new BufferedReader(new FileReader(read_file_name));
              BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            
           //   bw.write("# Each string contains parameters of corresponding CA:\n");
           //   bw.write("# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 6. temperature;\n");
           //   bw.write("# 7. effective stress; 8. principal stress; 9-11. decimal logarithms of 3 components of couple force;\n");
           //   bw.write("# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 13. strain;\n");
           //   bw.write("# 14. density of dislocations.\n");
              
              bw.write("# Each string contains parameters of corresponding cell: \n" +
                       "# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; \n" +
                       "# 6-8. absolute values of decimal logarithms of 3 components of instant specific force moment calculated using neighbour cells at 1st-3rd spheres; \n" +
                       "# 9-11. decimal logarithms of 3 components of instant specific force moment with signs corresponding to the values of moment components;\n" +
                       "# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n" +
                       "# 13. decimal logarithm of absolute value of instant specific force moment; \n" +
                      // "# 14. decimal logarithm of current influx of torsion energy. \n");
                       "# 14. current influx of specific torsion energy. \n");
              
              while(br.ready())
              {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))// & !tokens[0].equals("11"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();

                        // Calculation of torsion angle for current cell
                        mom_X = (new Double(tokens[ 9])).doubleValue();
                        mom_Y = (new Double(tokens[10])).doubleValue();
                        mom_Z = (new Double(tokens[11])).doubleValue();
                        
                        if(mom_X == 0) log10_mom_X = 0;
                        else           log10_mom_X = Math.log10(Math.abs(mom_X)) + superior_int_value;
                        
                        if(mom_Y == 0) log10_mom_Y = 0;
                        else           log10_mom_Y = Math.log10(Math.abs(mom_Y)) + superior_int_value;
                        
                        if(mom_Z == 0) log10_mom_Z = 0;
                        else           log10_mom_Z = Math.log10(Math.abs(mom_Z)) + superior_int_value;                        
                        
                        log10_abs_inst_mom   = (new Double(tokens[13])).doubleValue();
                        spec_tors_en_influx  = (new Double(tokens[14])).doubleValue()/cell_volume;
                        
                        if(log10_abs_inst_mom   == 0) log10_abs_inst_mom   = 0;
                        else                          log10_abs_inst_mom   = Math.log10(Math.abs(log10_abs_inst_mom)) + superior_int_value;
                        
                        if(spec_tors_en_influx == 0)  log10_tors_en_influx = 0;
                        else                          log10_tors_en_influx = Math.log10(Math.abs(spec_tors_en_influx));
                                                
                        if(log10_mom_X < 0 | log10_mom_Y < 0 | log10_mom_Z < 0 | log10_abs_inst_mom < 0)
                        {
                            //TEST
                            if(!begin_new_cycle)
                                System.out.println("mom_X = "+mom_X+"; mom_Y = "+mom_Y+"; mom_Z = "+mom_Z+
                                          "; log10_abs_inst_mom = "+log10_abs_inst_mom+"; log10_tors_en_influx = "+log10_tors_en_influx+
                                          "; spec_tors_en_influx = "+spec_tors_en_influx+"; cell_volume = "+cell_volume);                            
                            //END OF TEST
                            
                            begin_new_cycle = true;
                        }
                        else
                        {
                          // Writing of coordinates and physical parameters of current cell
                          for(int token_counter=0; token_counter<6; token_counter++)
                            bw.write(tokens[token_counter]+" ");
                        
                          bw.write(log10_mom_X+" "+log10_mom_Y+" "+log10_mom_Z+" "+
                                 sign(mom_X)*log10_mom_X+" "+sign(mom_Y)*log10_mom_Y+" "+sign(mom_Z)*log10_mom_Z+" ");
                        
                          bw.write(tokens[12]+" "+log10_abs_inst_mom+" "+log10_tors_en_influx); // spec_tors_en_influx); // 
                        
                          bw.newLine();
                        }
                    }
                    
                    bw.flush();
                }
              }
              
              if(begin_new_cycle)
              {
                  System.out.println("superior_int_value = "+superior_int_value+". The value of (log10_mom + "+superior_int_value+") is negative!!!");
                  superior_int_value++;
              }
              else
                  System.out.println("superior_int_value = "+superior_int_value+". All values of (log10_mom + "+superior_int_value+") are positive!!!");
              
              System.out.println("File '"+write_file_name+"' is written.");
              br.close();
              bw.close();
          }
          catch(IOException io_exc)
          {
              System.out.println("Error: " + io_exc);
          }
        }
        
        return superior_int_value;
    }
             
    /** The method creates file with parameters of torsion angles of cells in certain plane.
     */
    private void createFileWithTorsionAnglesInPlane(String _task_name, int file_counter)
    {
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        // String with parameters of current cell
        String string;
        
        cell_size_X = 1.0E-6;//5.0E-5;//
        // cell radius, shear modulus
        double cell_radius = cell_size_X/2;//5.00E-7;
        double shear_mod   = 8.0E10; // 4.7E10;//2.65E10;
        
        System.out.println("cell radius = "+cell_radius+"\ncell volume = "+cell_volume);
        
        // Specimen sizes in cell diameters
        double spec_size_X = 100, spec_size_Y = 10, spec_size_Z = 100;
        
        // Type of cell location in specimen and in grain
        byte cell_location_type;
        
        // Cell coordinates in cell diameters
        double cell_coord_X, cell_coord_Y, cell_coord_Z;
        
        // components of force moment of cell
        double mom_X, mom_Y, mom_Z;
        
        // Components of vector of torsion angle of current cell
        double torsion_angle_X, torsion_angle_Y, torsion_angle_Z;    
        
        // Sign of certain value
        byte sign = 0;        
        
        double zoom_factor = 1.0/cell_size_X;// 1.0E6;// 
        
        double abs_mom_X = 0;
        double abs_mom_Y = 0;
        double abs_mom_Z = 0; 
        double abs_mom   = 0;
        double sign_abs_mom = 0; 
        
        double abs_torsion_angle_X = 0;
        double abs_torsion_angle_Z = 0;
        double abs_torsion_angle = 0;        
        double sign_torsion_angle = 0;        
                
        double log10_abs_mom_X = 0;
        double sign_log10_abs_mom_X = 0;
        double log10_abs_mom_Y = 0;
        double sign_log10_abs_mom_Y = 0;
        double log10_abs_mom_Z = 0;
        double sign_log10_abs_mom_Z = 0;
        double log10_abs_mom = 0;
        double sign_log10_abs_mom = 0;
        
        double log10_abs_torsion_angle_X = 0;
        double sign_log10_abs_torsion_angle_X = 0;
        double log10_abs_torsion_angle_Z = 0;
        double sign_log10_abs_torsion_angle_Z = 0;
        double log10_abs_torsion_angle = 0;
        double sign_log10_abs_torsion_angle = 0;
        
        double strain = 0;
        double log10_abs_strain = 0;
        double sign_log10_abs_strain = 0;
    /*
        String[] write_file_name = new String [26];
        
        write_file_name[ 0] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_mom_X.txt";
        write_file_name[ 1] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_tors_angle_X.txt";
        write_file_name[ 2] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_mom_Z.txt";
        write_file_name[ 3] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_tors_angle_Z.txt";
        
        write_file_name[ 4] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_abs_mom_X.txt";
        write_file_name[ 5] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_abs_tors_angle_X.txt";
        write_file_name[ 6] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_abs_mom_Z.txt";
        write_file_name[ 7] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_abs_tors_angle_Z.txt";        
        
        write_file_name[ 8] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_abs_moments.txt";
        write_file_name[ 9] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_abs_tors_angles.txt";
        write_file_name[10] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_abs_moments.txt";
        write_file_name[11] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_abs_tors_angles.txt";
        
        write_file_name[12] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_log10_abs_mom_X.txt";
        write_file_name[13] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_log10_abs_mom_Z.txt";
        write_file_name[14] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_log10_abs_torsion_angle_X.txt";
        write_file_name[15] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_log10_abs_torsion_angle_Z.txt";
        write_file_name[16] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_log10_abs_moments.txt";
        write_file_name[17] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_log10_abs_torsion_angles.txt";
        
        write_file_name[18] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_mom_X.txt";
        write_file_name[19] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_mom_Z.txt";
        write_file_name[20] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_torsion_angle_X.txt";
        write_file_name[21] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_torsion_angle_Z.txt";
        write_file_name[22] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_moments.txt";
        write_file_name[23] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_torsion_angles.txt";
        
        write_file_name[24] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_strain.txt";
        write_file_name[25] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_strain.txt";
                                    
        BufferedWriter[] bw = new BufferedWriter[26];
    */
        
        String[] write_file_name = new String [6];
        
    //    write_file_name[0] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_mom_Y.txt";
      //  write_file_name[1] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_abs_mom_Y.txt";
     //   write_file_name[2] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_mom_Y.txt";
    //    write_file_name[3] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_log10_abs_mom_Y.txt";
    //    write_file_name[3] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_sign_log10_abs_moments.txt";
     //   write_file_name[3] = "./user/task_db/"+_task_name+"/"+_task_name+"_j=01/"+_task_name+"_"+file_counter+"_j=01_location_types.txt";
        
        write_file_name[4] = "./user/task_db/"+_task_name+"/plane_graphs/"+_task_name+"_"+file_counter+"_K=13_abs_moments.txt";
        write_file_name[5] = "./user/task_db/"+_task_name+"/plane_graphs/"+_task_name+"_"+file_counter+"_K=13_log10_abs_moments.txt";
        
        BufferedWriter[] bw = new BufferedWriter[6];
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            
            for(int bw_counter = 4; bw_counter < bw.length; bw_counter++)
                bw[bw_counter] = new BufferedWriter(new FileWriter(write_file_name[bw_counter]));
        
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();
                    
                    if(!tokens[0].equals("#") )// & !tokens[0].equals("11"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        cell_location_type = (new Byte  (tokens[1])).byteValue();
                        cell_coord_X       = (new Double(tokens[3])).doubleValue();// *1.0/zoom_factor;
                        cell_coord_Y       = (new Double(tokens[4])).doubleValue();// *1.0/zoom_factor;
                        cell_coord_Z       = (new Double(tokens[5])).doubleValue();// *1.0/zoom_factor;
                        
                        if(cell_location_type != Common.OUTER_CELL &
                           cell_location_type != Common.INNER_BOUNDARY_CELL &
                           cell_location_type != Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
                     //   if(cell_coord_Y <  4.5*cell_radius)
                     //   if(cell_coord_X >= 4.0*cell_radius & cell_coord_X <= spec_size_X-4.0*cell_radius)
                     //   if(cell_coord_Z >= 4.0*cell_radius & cell_coord_Z <= spec_size_Z-4.0*cell_radius)
                        if(cell_coord_Z >= 11.11 & cell_coord_Z <= 11.12)
                        {
                            // Calculation of torsion angle for current cell
                            torsion_angle_X = (new Double(tokens[ 9])).doubleValue();
                            torsion_angle_Y = (new Double(tokens[10])).doubleValue();
                            torsion_angle_Z = (new Double(tokens[11])).doubleValue();
                            
                            mom_X = torsion_angle_X*(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius)/2;
                            mom_Y = torsion_angle_Y*(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius)/2;
                            mom_Z = torsion_angle_Z*(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius)/2;
                                    
                       //     torsion_angle_X = 2*mom_X/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                       //     torsion_angle_Y = 2*mom_Y/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                       //     torsion_angle_Z = 2*mom_Z/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                                                        
                            // Calculation of specific volume moment components
                            mom_X = mom_X *1.0/cell_volume;
                            mom_Y = mom_Y *1.0/cell_volume;
                            mom_Z = mom_Z *1.0/cell_volume;
                            
                            mom_X = mom_X *1.0/cell_size_X;
                            mom_Y = mom_Y *1.0/cell_size_X;
                            mom_Z = mom_Z *1.0/cell_size_X;
                            
                            // Transition from radians to degrees
                            torsion_angle_X = torsion_angle_X*180/Math.PI;
                            torsion_angle_Y = torsion_angle_Y*180/Math.PI;
                            torsion_angle_Z = torsion_angle_Z*180/Math.PI;
                            
                            sign = sign(mom_X + mom_Z);
                            
                            abs_mom_X = Math.abs(mom_X);
                            abs_mom_Y = Math.abs(mom_Y);
                            abs_mom_Z = Math.abs(mom_Z);
                            abs_torsion_angle_X = Math.abs(torsion_angle_X);
                            abs_torsion_angle_Z = Math.abs(torsion_angle_Z);
                            
                          //  abs_mom             = Math.sqrt(mom_X*mom_X + mom_Z*mom_Z);
                            abs_mom = Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                            abs_torsion_angle   = Math.sqrt(torsion_angle_X*torsion_angle_X + torsion_angle_Z*torsion_angle_Z);
                            sign_abs_mom        = sign*abs_mom;
                            sign_torsion_angle  = sign*abs_torsion_angle;
                            
                            log10_abs_mom_X           = Math.log10(abs_mom_X);
                            
                            if(abs_mom_Y == 0)
                                log10_abs_mom_Y = 0;
                            else
                                log10_abs_mom_Y           = Math.log10(abs_mom_Y) + 6;                            
                            
                            log10_abs_mom_Z           = Math.log10(abs_mom_Z);
                            log10_abs_torsion_angle_X = Math.log10(abs_torsion_angle_X)+6;//
                            log10_abs_torsion_angle_Z = Math.log10(abs_torsion_angle_Z)+6;//
                            log10_abs_mom             = Math.log10(abs_mom);
                            log10_abs_torsion_angle   = Math.log10(abs_torsion_angle)+6;//
                            
                        //    if(log10_abs_torsion_angle_X<0 | log10_abs_torsion_angle_Z<0)
                        //        System.out.println("ERROR!!! The values 'n + log10_abs_torsion_angle_X' and 'n + log10_abs_torsion_angle_Z' must be positive!");
                            
                        //    if(log10_abs_mom_Y<0)
                        //        System.out.println("ERROR!!! The value 'n + log10_abs_mom_Y' must be positive!");
                            
                            sign_log10_abs_mom_X           = sign(mom_X)*log10_abs_mom_X;
                            sign_log10_abs_mom_Y           = sign(mom_Y)*log10_abs_mom_Y;
                            sign_log10_abs_mom_Z           = sign(mom_Z)*log10_abs_mom_Z;
                            sign_log10_abs_torsion_angle_X = sign(torsion_angle_X)*log10_abs_torsion_angle_X;
                            sign_log10_abs_torsion_angle_Z = sign(torsion_angle_Z)*log10_abs_torsion_angle_Z;
                            sign_log10_abs_mom             = sign*log10_abs_mom;
                            sign_log10_abs_torsion_angle   = sign*log10_abs_torsion_angle;
                            
                            // Transition from meters to mcm
                        //    cell_coord_Z = (double)Math.round(cell_coord_Z*1.0E9)/1000;
                        //    cell_coord_X = (double)Math.round(cell_coord_X*1.0E9)/1000;
                        //    cell_coord_Y = (double)Math.round(cell_coord_Y*1.0E9)/1000;
                            
                            // Writing of coordinates and physical parameter of current cell
                      //      bw[0].write(cell_coord_Z+" "+cell_coord_X+" "+mom_Y+"\n");
                      //      bw[1].write(cell_coord_Z+" "+cell_coord_X+" "+abs_mom_Y+"\n");
                      //      bw[2].write(cell_coord_Z+" "+cell_coord_X+" "+(-sign_log10_abs_mom_Y)+"\n");
                      //      bw[3].write(cell_coord_Z+" "+cell_coord_X+" "+log10_abs_mom_Y+"\n");
                      //      bw[3].write(cell_coord_Z+" "+cell_coord_X+" "+(-sign_log10_abs_mom)+"\n");
                      //      bw[3].write(cell_coord_Z+" "+cell_coord_X+" "+(cell_location_type-5)+"\n"); 
                            
                            bw[4].write(cell_coord_Y+" "+cell_coord_X+" "+abs_mom+"\n");
                            bw[5].write(cell_coord_Y+" "+cell_coord_X+" "+log10_abs_mom+"\n");
                            
                        /*
                            bw[ 0].write(cell_coord_Z+" "+cell_coord_X+" "+mom_X+"\n");
                            bw[ 1].write(cell_coord_Z+" "+cell_coord_X+" "+torsion_angle_X+"\n");
                            bw[ 2].write(cell_coord_Z+" "+cell_coord_X+" "+mom_Z+"\n");
                            bw[ 3].write(cell_coord_Z+" "+cell_coord_X+" "+torsion_angle_Z+"\n");
                            
                            bw[ 4].write(cell_coord_Z+" "+cell_coord_X+" "+abs_mom_X+"\n");
                            bw[ 5].write(cell_coord_Z+" "+cell_coord_X+" "+abs_torsion_angle_X+"\n");
                            bw[ 6].write(cell_coord_Z+" "+cell_coord_X+" "+abs_mom_Z+"\n");
                            bw[ 7].write(cell_coord_Z+" "+cell_coord_X+" "+abs_torsion_angle_Z+"\n");
                            
                            bw[ 8].write(cell_coord_Z+" "+cell_coord_X+" "+abs_mom+"\n");
                            bw[ 9].write(cell_coord_Z+" "+cell_coord_X+" "+abs_torsion_angle+"\n");
                            bw[10].write(cell_coord_Z+" "+cell_coord_X+" "+sign_abs_mom+"\n");
                            bw[11].write(cell_coord_Z+" "+cell_coord_X+" "+sign_torsion_angle+"\n");
                            
                            bw[12].write(cell_coord_Z+" "+cell_coord_X+" "+log10_abs_mom_X+"\n");
                            bw[13].write(cell_coord_Z+" "+cell_coord_X+" "+log10_abs_mom_Z+"\n");
                            bw[14].write(cell_coord_Z+" "+cell_coord_X+" "+log10_abs_torsion_angle_X+"\n");
                            bw[15].write(cell_coord_Z+" "+cell_coord_X+" "+log10_abs_torsion_angle_Z+"\n");
                            bw[16].write(cell_coord_Z+" "+cell_coord_X+" "+log10_abs_mom+"\n");
                            bw[17].write(cell_coord_Z+" "+cell_coord_X+" "+log10_abs_torsion_angle+"\n");
                            
                            bw[18].write(cell_coord_Z+" "+cell_coord_X+" "+sign_log10_abs_mom_X+"\n");
                            bw[19].write(cell_coord_Z+" "+cell_coord_X+" "+sign_log10_abs_mom_Z+"\n");
                            bw[20].write(cell_coord_Z+" "+cell_coord_X+" "+sign_log10_abs_torsion_angle_X+"\n");
                            bw[21].write(cell_coord_Z+" "+cell_coord_X+" "+sign_log10_abs_torsion_angle_Z+"\n");
                            bw[22].write(cell_coord_Z+" "+cell_coord_X+" "+sign_log10_abs_mom+"\n");
                            bw[23].write(cell_coord_Z+" "+cell_coord_X+" "+sign_log10_abs_torsion_angle+"\n");
                            
                            // Strain of current cell and its logatithm
                            strain = (new Double(tokens[13])).doubleValue();
                            log10_abs_strain = Math.log10(Math.abs(strain))+2;
                            
                            if(log10_abs_strain<0)
                                System.out.println("ERROR!!! The value 'n + log10_abs_strain' should be positive!");
                            
                            sign_log10_abs_strain = sign(strain)*log10_abs_strain;
                                    
                            bw[24].write(cell_coord_Z+" "+cell_coord_X+" "+strain+"\n");
                            bw[25].write(cell_coord_Z+" "+cell_coord_X+" "+sign_log10_abs_strain+"\n");
                        */
                        }
                    }
                    
                    for(int bw_counter = 4; bw_counter<bw.length; bw_counter++)
                        bw[bw_counter].flush();
                }
            }
            
            for(int bw_counter = 4; bw_counter<bw.length; bw_counter++)
              System.out.println("File '"+write_file_name[bw_counter]+"' is written.");
            
            br.close();
            
            for(int bw_counter = 4; bw_counter<bw.length; bw_counter++)
                bw[bw_counter].close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method returns sign of the argument.
     * @param value the argument value
     * @return sign of the argument
     */
    public byte sign(double value)
    {
        byte sign = 0;
        
        if(value > 0)            sign =  1;
        if(value < 0)            sign = -1;
        
        return sign; 
    }
     
    /** The method creates file with parameters of cells located along grain boundary.
     */
    private void createGraphAlongGrainBoundaryStep1(int _grain_index, int file_number)
    {
        String write_file_name_1 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_X.txt";
        String write_file_name_2 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_Y.txt";
        String write_file_name_3 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_Z.txt";
        String write_file_name_4 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_torsion_angles_X.txt";
        String write_file_name_5 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_torsion_angles_Y.txt";
        String write_file_name_6 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_torsion_angles_Z.txt";              
            
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        // String with parameters of current cell
        String string;
        
        // Type of cell according to its location in grain and in specimen
        byte location_type = 0;
        
        // Index of grain containing cell
        int grain_index = 0;
                
        // Cell parameters: coordinates (mcm), components of force moment (J), cell radius (m), shear modulus (Pa)
        double coord_X, coord_Y, coord_Z, mom_X, mom_Y, mom_Z, abs_mom, cell_radius, shear_mod;
        
        cell_radius = 5.00E-7;
        shear_mod   = 2.65E10;
        
        // Components of vector of torsion angle of current cell and its absolute value
        double torsion_angle_X, torsion_angle_Y, torsion_angle_Z, torsion_angle;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(write_file_name_1));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
            BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
            BufferedWriter bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
            BufferedWriter bw5 = new BufferedWriter(new FileWriter(write_file_name_5));
            BufferedWriter bw6 = new BufferedWriter(new FileWriter(write_file_name_6));
            
       //     bw.write("# Each string contains parameters of corresponding CA:\n");
         //   bw.write("# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 6. temperature;\n");
           // bw.write("# 7. effective stress; 8. principal stress; 9-11. 3 components of torsion angle;\n");
       //     bw.write("# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 13. strain;\n");
         //   bw.write("# 14. density of dislocations.\n");
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#") )// & !tokens[0].equals("11"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        location_type = (new Byte(tokens[1])).byteValue();
                        grain_index   = (new Integer(tokens[2])).intValue();
                        
                        // Cell coordinates in micrometers
                        coord_X = (new Double(tokens[3])).doubleValue();
                        coord_Y = (new Double(tokens[4])).doubleValue();
                        coord_Z = (new Double(tokens[5])).doubleValue();
                        
                        if(location_type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
                        if(coord_Y < 2.5  &  coord_X < 29 &  coord_Z < 20)
                        if(grain_index == _grain_index)
                        {
                     //       coord_X = coord_X*2*cell_radius;
                       //     coord_Y = coord_Y*2*cell_radius;
                         //   coord_Z = coord_Z*2*cell_radius;
                            
                            // Calculation of torsion angle for current cell
                            mom_X = (new Double(tokens[9])).doubleValue();
                            mom_Y = (new Double(tokens[10])).doubleValue();
                            mom_Z = (new Double(tokens[11])).doubleValue();
                        
                            torsion_angle_X = 2*mom_X/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                            torsion_angle_Y = 2*mom_Y/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                            torsion_angle_Z = 2*mom_Z/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                        
                            // Transition from radians to degrees
                            torsion_angle_X = torsion_angle_X*180/Math.PI;
                            torsion_angle_Y = torsion_angle_Y*180/Math.PI;
                            torsion_angle_Z = torsion_angle_Z*180/Math.PI;
                        
                            // Writing of coordinates and physical parameters of current cell
                            bw1.write(coord_X+" "+coord_Z+" "+mom_X+"\n");
                            bw2.write(coord_X+" "+coord_Z+" "+mom_Y+"\n");
                            bw3.write(coord_X+" "+coord_Z+" "+mom_Z+"\n");
                            bw4.write(coord_X+" "+coord_Z+" "+torsion_angle_X+"\n");
                            bw5.write(coord_X+" "+coord_Z+" "+torsion_angle_Y+"\n");
                            bw6.write(coord_X+" "+coord_Z+" "+torsion_angle_Z+"\n");
                        }
                    }
                    
                    bw1.flush();
                }
            }

            System.out.println("File '"+write_file_name_1+"' is written.");
            System.out.println("File '"+write_file_name_2+"' is written.");
            System.out.println("File '"+write_file_name_3+"' is written.");
            System.out.println("File '"+write_file_name_4+"' is written.");
            System.out.println("File '"+write_file_name_5+"' is written.");
            System.out.println("File '"+write_file_name_6+"' is written.");
            br.close();
            bw1.close();
            bw2.close();
            bw3.close();
            bw4.close();
            bw5.close();
            bw6.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
  
    /** The method creates file with distribution of chosen parameter along grain boundary length.
     */
    private void createGraphAlongGrainBoundaryStep2(int _grain_index, double factor)
    {
        int token_number = 3;
        String[] tokens = new String[token_number];
        
        // String with parameters of current cell
        String string;
                        
        // Cell coordinates (mcm) and certain cell parameter
        double coord_X=0, coord_Z=0, cell_param;
        
        // Coordinates of previous cell
        double prev_cell_coord_X=0, prev_cell_coord_Z=0;
        
        // Current length of grain boundary
        double current_length = 0;
        
        int cell_counter = 0;
        
        String read_file_name[] = new String[6];
        String write_file_name[] = new String[6];
        
        read_file_name[0]  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_X.txt";
        read_file_name[1]  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_Y.txt";
        read_file_name[2]  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_Z.txt";
        read_file_name[3]  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+_grain_index+"_tors_angle_X.txt";        
        read_file_name[4]  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+_grain_index+"_tors_angle_Y.txt";
        read_file_name[5]  = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+_grain_index+"_tors_angle_Z.txt";
        
        write_file_name[0] = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bound_length_mom_X.txt";
        write_file_name[1] = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bound_length_mom_Y.txt";
        write_file_name[2] = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bound_length_mom_Z.txt";
        write_file_name[3] = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+_grain_index+"_bound_length_tors_angle_X.txt";
        write_file_name[4] = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+_grain_index+"_bound_length_tors_angle_Y.txt";
        write_file_name[5] = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_0"+file_number+"_i=01_grain0"+_grain_index+"_bound_length_tors_angle_Z.txt";
        
        ArrayList cell_parameters = new ArrayList(0);
        VectorR3 curr_cell_params, prev_cell_params;
        int change_number;
        double curr_vector_length, prev_vector_length;
        
        for(int file_counter = 3; file_counter<6; file_counter++)
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name[file_counter]));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name[file_counter]));
            
            cell_parameters = new ArrayList(0);
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        // Cell coordinates in micrometers
                        coord_X    = (new Double(tokens[0])).doubleValue();
                        coord_Z    = (new Double(tokens[1])).doubleValue();
                        cell_param = (new Double(tokens[2])).doubleValue();
                        
                        cell_parameters.add(new VectorR3(coord_X, coord_Z, cell_param));                        
                    }
                }
            }
            
            change_number = 1;
            
            while(change_number>0)
            {                
              change_number = 0;
            
              for(cell_counter = 1; cell_counter<cell_parameters.size(); cell_counter++)
              {
                prev_cell_params = (VectorR3)cell_parameters.get(cell_counter-1);
                curr_cell_params = (VectorR3)cell_parameters.get(cell_counter);
                
                prev_vector_length = prev_cell_params.getX()*prev_cell_params.getX() + prev_cell_params.getY()*prev_cell_params.getY();
                curr_vector_length = curr_cell_params.getX()*curr_cell_params.getX() + curr_cell_params.getY()*curr_cell_params.getY();
                
            //    if(curr_vector_length < prev_vector_length)
                if(curr_cell_params.getX() < prev_cell_params.getX())
            //    if(false)
                {
                    cell_parameters.set(cell_counter-1, curr_cell_params);
                    cell_parameters.set(cell_counter, prev_cell_params);
                    change_number++;
                    
                //    System.out.println("change_number = "+change_number);
                }   
                
                if(curr_cell_params.getX() == prev_cell_params.getX())
                if(curr_cell_params.getY() > prev_cell_params.getY())
                {
                    cell_parameters.set(cell_counter-1, curr_cell_params);
                    cell_parameters.set(cell_counter, prev_cell_params);
                    change_number++;
                }
              }
            }
            
            prev_cell_coord_X = 0;
            prev_cell_coord_Z = 0;
            
            for(cell_counter = 0; cell_counter<cell_parameters.size(); cell_counter++)
            {
                curr_cell_params = (VectorR3)cell_parameters.get(cell_counter);
                 
                coord_X    = curr_cell_params.getX();
                coord_Z    = curr_cell_params.getY();
                cell_param = curr_cell_params.getZ();
                 
                if(cell_counter > 0)
                    current_length = current_length + factor*Math.sqrt((coord_X-prev_cell_coord_X)*(coord_X-prev_cell_coord_X)+
                                                                       (coord_Z-prev_cell_coord_Z)*(coord_Z-prev_cell_coord_Z));
                else
                    current_length = 0;
                 
                prev_cell_coord_X = coord_X;
                prev_cell_coord_Z = coord_Z;
                
                // Writing of coordinates and physical parameters of current cell
          //      bw.write(coord_X+" "+coord_Z+" "+current_length+" "+cell_param);                
                bw.write(current_length+" "+cell_param);    
                bw.newLine();
                bw.flush();
                
             //   System.out.println(current_length+" "+coord_X+" "+coord_Z+" "+cell_param);
            }

            System.out.println("File '"+write_file_name[file_counter]+"' is written.");
            System.out.println("current_length = "+current_length+" change_number = "+change_number);
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file with parameters of cells located along grain boundary.
     */
    private void createGraphAlongGrainBoundaryStep1(int _grain_index, String write_file_name)
    {
     /* 
        String write_file_name_1 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_X.txt";
        String write_file_name_2 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_Y.txt";
        String write_file_name_3 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_mom_Z.txt";
        String write_file_name_4 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_torsion_angles_X.txt";
        String write_file_name_5 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_torsion_angles_Y.txt";
        String write_file_name_6 = "./user/task_db/"+task_name+"/torsion_angles/"+task_name+"_"+file_number+"_j=01_grain0"+_grain_index+"_bounds_torsion_angles_Z.txt";                      
     */ 
        boolean calc_log10_abs_mom_Z   = false;// true; // 
        boolean calc_log10_abs_moments = false;// true; // 
        boolean calc_abs_moments       = false;// true; // 
        
        boolean calc_mom_X             = false;// true; // 
        boolean calc_mom_Y             = false;// true; // 
        boolean calc_mom_Z             = true; // false;// 
        
        boolean calc_eff_str           = false;// true; // 
        boolean calc_pr_str            = false;// true; // 
        
        String write_file_name_7 = "";
        
        if(calc_log10_abs_mom_Z)
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_log10_abs_mom_Z.txt";
        
        if(calc_log10_abs_moments)
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_log10_abs_moments.txt";
        
        if(calc_abs_moments)
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_abs_moments.txt";
        
        if(calc_mom_X)
            //  write_file_name_7 = write_file_name+"_j=01_grain0"+_grain_index+"_mom_X.txt";
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_tors_angle_X.txt";
        
        if(calc_mom_Y)
            // write_file_name_7 = write_file_name+"_j=01_grain0"+_grain_index+"_mom_Y.txt";
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_tors_angle_Y.txt";
        
        if(calc_mom_Z)
            // write_file_name_7 = write_file_name+"_j=01_grain0"+_grain_index+"_mom_Z.txt";
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_tors_angle_Z.txt";
        
        if(calc_eff_str)
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_eff_str.txt";
        
        if(calc_pr_str)
            write_file_name_7 = write_file_name+"_i=01_grain0"+_grain_index+"_pr_str.txt";
        
        cell_size = 1.0E-6;
        
        // Superior integer non-negative value for -log10(|mom_Z|).
        int superior_value = 15;
        
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        // String with parameters of current cell
        String string;
        
        // Type of cell according to type of its interaction with neighbours
        byte energy_type = 0;
        
        // Type of cell according to its location in grain and in specimen
        byte location_type = 0;
        
        // Index of grain containing cell
        int grain_index = 0;
                
        // Cell parameters: coordinates (mcm), components of force moment (J), cell radius (m), shear modulus (Pa)
        double coord_X, coord_Y, coord_Z, mom_X, mom_Y, mom_Z, abs_mom, cell_radius, shear_mod, eff_str, pr_str;
        
        double log10_mom_Z, log10_abs_mom;
        
        cell_radius = 5.00E-7;
        shear_mod   = 2.65E10;
        
        // Components of vector of torsion angle of current cell and its absolute value
        double torsion_angle_X, torsion_angle_Y, torsion_angle_Z, torsion_angle;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
        //    BufferedWriter bw1 = new BufferedWriter(new FileWriter(write_file_name_1));
        //    BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
        //    BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
        //    BufferedWriter bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
        //    BufferedWriter bw5 = new BufferedWriter(new FileWriter(write_file_name_5));
        //    BufferedWriter bw6 = new BufferedWriter(new FileWriter(write_file_name_6));
            BufferedWriter bw7 = new BufferedWriter(new FileWriter(write_file_name_7));
            
       //     bw.write("# Each string contains parameters of corresponding CA:\n");
         //   bw.write("# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 6. temperature;\n");
           // bw.write("# 7. effective stress; 8. principal stress; 9-11. 3 components of torsion angle;\n");
       //     bw.write("# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 13. strain;\n");
         //   bw.write("# 14. density of dislocations.\n");
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();
                    
                    if(!tokens[0].equals("#") )// & !tokens[0].equals("11"))
                    {
                        energy_type = (new Byte(tokens[0])).byteValue();
                        
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        location_type = (new Byte(tokens[1])).byteValue();
                        grain_index   = (new Integer(tokens[2])).intValue();
                        
                        // Cell coordinates in micrometers
                        coord_X = (new Double(tokens[3])).doubleValue();
                        coord_Y = (new Double(tokens[4])).doubleValue();
                        coord_Z = (new Double(tokens[5])).doubleValue();
                        
                        if(energy_type   == Common.INNER_CELL)
                        if(location_type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)                    
                    //    if(coord_X < 2.5 & coord_Z > 28)
                    //    if(coord_X < 2.5 & coord_Z <= 28 & coord_Y <= 28)
                        if(coord_X < 2.5 & coord_Z <= 28 & coord_Y > 28)
                        if(grain_index == _grain_index)
                        {
                       //     coord_X = coord_X*2*cell_radius;
                         //   coord_Y = coord_Y*2*cell_radius;
                           // coord_Z = coord_Z*2*cell_radius;
                            
                            // Calculation of torsion angle for current cell
                            eff_str = (new Double(tokens[ 7])).doubleValue();
                            pr_str  = (new Double(tokens[ 8])).doubleValue();
                            
                            mom_X   = (new Double(tokens[ 9])).doubleValue();
                            mom_Y   = (new Double(tokens[10])).doubleValue();
                            mom_Z   = (new Double(tokens[11])).doubleValue();
                            
                        /*
                            torsion_angle_X = 2*mom_X/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                            torsion_angle_Y = 2*mom_Y/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                            torsion_angle_Z = 2*mom_Z/(Math.PI*shear_mod*cell_radius*cell_radius*cell_radius);
                        
                            // Transition from radians to degrees
                            torsion_angle_X = torsion_angle_X*180/Math.PI;
                            torsion_angle_Y = torsion_angle_Y*180/Math.PI;
                            torsion_angle_Z = torsion_angle_Z*180/Math.PI;
                         */
                            if(calc_log10_abs_mom_Z)
                            {
                              if(mom_Z == 0) 
                                log10_mom_Z = 0;
                              else
                              {
                                log10_mom_Z = Math.log10(Math.abs(mom_Z))+superior_value;
                            
                                if(log10_mom_Z < 0)
                                    System.out.println("ERROR!!! Please, increase the superior value for 'log10_mom_Z'!!!");
                            
                                log10_mom_Z = sign(mom_Z)*log10_mom_Z;
                              }
                            
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+log10_mom_Z+"\n");
                            }
                            
                            if(calc_mom_X)
                            {
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+mom_X+"\n");
                            }
                            
                            if(calc_mom_Y)
                            {
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+mom_Y+"\n");
                            }
                            
                            if(calc_mom_Z)
                            {
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+mom_Z+"\n");
                            }
                            
                            if(calc_eff_str)
                            {
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+eff_str+"\n");
                            }
                            
                            if(calc_pr_str)
                            {
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+pr_str+"\n");
                            }
                            
                            if(calc_log10_abs_moments)
                            {
                              abs_mom = Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                                
                              if(abs_mom == 0) 
                                log10_abs_mom = 0;
                              else
                              {
                                log10_abs_mom = Math.log10(abs_mom)+superior_value;
                            
                                if(log10_abs_mom < 0)
                                    System.out.println("ERROR!!! Please, increase the superior value for 'log10_abs_mom'!!!");                            
                              }
                              
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+log10_abs_mom+"\n");
                            }
                            
                            if(calc_abs_moments)
                            {
                              abs_mom = 1E-6*Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z)/calcCellVolume(Common.HEXAGONAL_CLOSE_PACKING, cell_size);
                                
                              bw7.write(/*energy_type+" "+location_type+" "+grain_index+ " "+coord_X+" "+*/coord_Y+" "+coord_Z+" "+abs_mom+"\n");
                            }
                            
                            // Writing of coordinates and physical parameters of current cell
                     //       bw1.write(coord_X+" "+coord_Z+" "+mom_X+"\n");
                     //       bw2.write(coord_X+" "+coord_Z+" "+mom_Y+"\n");
                     //       bw3.write(coord_X+" "+coord_Z+" "+mom_Z+"\n");
                     //       bw4.write(coord_X+" "+coord_Z+" "+torsion_angle_X+"\n");
                     //       bw5.write(coord_X+" "+coord_Z+" "+torsion_angle_Y+"\n");
                     //       bw6.write(coord_X+" "+coord_Z+" "+torsion_angle_Z+"\n");
                        }
                    }
                    
                    bw7.flush();
                }
            }

      //      System.out.println("File '"+write_file_name_1+"' is written.");
      //      System.out.println("File '"+write_file_name_2+"' is written.");
      //      System.out.println("File '"+write_file_name_3+"' is written.");
      //      System.out.println("File '"+write_file_name_4+"' is written.");
      //      System.out.println("File '"+write_file_name_5+"' is written.");
      //      System.out.println("File '"+write_file_name_6+"' is written.");
            System.out.println("File '"+write_file_name_7+"' is written.");
            br.close();
       //     bw1.close();
       //     bw2.close();
       //     bw3.close();
       //     bw4.close();
       //     bw5.close();
       //     bw6.close();
            bw7.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
  
    /** The method creates file with distribution of average values of chosen parameter
     * along grain boundary length between two sides of this boundary.
     * @param read_file_name_1 name of file with data about distribution of the parameter along 1st side of boundary
     * @param read_file_name_2 file with data about distribution of the parameter along 2nd side of boundary
     */
    private void calcAverageValuesAlongGrainBoundary(String read_file_name_1, String read_file_name_2)
    {
        cell_size    = 1.0E-6;
        
        // Scaling factor
        double scaling_factor = 1;// 1.0E-6;// 
        
        // Type of packing of cells 
        byte packing_type = Common.HEXAGONAL_CLOSE_PACKING;
        
        // Cell volume
        double cell_volume = 1;// calcCellVolume(packing_type, cell_size);// 
                
        double cur_length_1 = 0;
        double cur_length_2 = 0;
        double cur_length   = 0;
        String cur_length_token;
        
        double param_1      = 0;
        double param_2      = 0;
        double aver_param   = 0;
        String parameter_token;
        
        ArrayList length_values_1 = new ArrayList(0);
        ArrayList length_values_2 = new ArrayList(0);
        
        double[] length_values;
        
        ArrayList param_values_1  = new ArrayList(0);
        ArrayList param_values_2  = new ArrayList(0);
        double[] param_values;
        
        double[] length_values_1_array;
        double[] length_values_2_array;
        
        int param1_index=0;
        int param2_index=0;
        
        double left_param=0;
        double right_param=0;
        
        double left_length=0;
        double right_length=0;
        
        String string1, string2;
        StringTokenizer st;
        
        try
        {
            BufferedReader br1 = new BufferedReader(new FileReader(read_file_name_1));
            BufferedReader br2 = new BufferedReader(new FileReader(read_file_name_2));
            BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));
            
            while(br1.ready() & br2.ready())
            {
                string1 = br1.readLine();                
                st = new StringTokenizer(string1);
                
                if(st.hasMoreTokens())
                {
                    cur_length_token = st.nextToken();
                    parameter_token  = st.nextToken();
                    
                    cur_length_1 = (new Double(cur_length_token)).doubleValue();
                    param_1      = (new Double(parameter_token)).doubleValue();
                    
                    param_1 = param_1*scaling_factor/cell_volume;
                }
                
                string2 = br2.readLine();                
                st = new StringTokenizer(string2);
                
                if(st.hasMoreTokens())
                {
                    cur_length_token = st.nextToken();
                    parameter_token = st.nextToken();
                    
                    cur_length_2 = (new Double(cur_length_token)).doubleValue();
                    param_2      = (new Double(parameter_token)).doubleValue();
                    
                    param_2 = param_2*scaling_factor/cell_volume;
                }
                
                length_values_1.add(cur_length_1);
                length_values_2.add(cur_length_2);
                
                param_values_1.add(param_1);
                param_values_2.add(param_2);                
                
            /*
                aver_length = (cur_length_1 + cur_length_2)/2;
                aver_param  = (param_1 + param_2)/2;
                
                bw.write(aver_length+" "+aver_param+"\n");
                bw.flush();
             */
            }
            
            length_values_1_array = new double[length_values_1.size()];
            length_values_2_array = new double[length_values_2.size()];
            
            // Creation of array of length values for 1st boundary
            for(int length1_counter = 0; length1_counter<length_values_1_array.length; length1_counter++)
                length_values_1_array[length1_counter] = (double)length_values_1.get(length1_counter);
            
            // Creation of array of length values for 2nd boundary
            for(int length2_counter = 0; length2_counter<length_values_2_array.length; length2_counter++)
                length_values_2_array[length2_counter] = (double)length_values_2.get(length2_counter);
            
            length_values = new double[length_values_1_array.length + length_values_2_array.length];
            param_values  = new double[length_values_1_array.length + length_values_2_array.length];
                    
            // Creation of array of average values of the parameter in points of two lists of length values
            for(int length1_counter = 0; length1_counter<length_values_1_array.length; length1_counter++)
            {
                // Current length from 1st file
                cur_length = length_values_1_array[length1_counter];
                param1_index = length1_counter;
                  
                // Search of length values from 2nd file, which are closest to length value from 1st file from left and right sides
                for(int length2_counter = 0; length2_counter<length_values_2_array.length-1; length2_counter++)
                {
                    left_length  = length_values_2_array[length2_counter];
                    right_length = length_values_2_array[length2_counter+1];
                    
                    if((cur_length >= left_length  & cur_length <= right_length)|
                       (cur_length >= right_length & cur_length <= left_length ))
                    {
                        param2_index = length2_counter;
                        length2_counter = length_values_2_array.length;
                    }
                }
                
                // Parameter values corresponding to found length values
                left_param  = (double)param_values_2.get(param2_index);
                right_param = (double)param_values_2.get(param2_index+1);
                
                // Calculation of the parameter from 2nd file corresponding to length value from 1st file
                if(left_length == right_length)
                    param_2 = left_param;
                else
                    param_2 = left_param + (right_param - left_param)*(cur_length - left_length)/(right_length - left_length);
                
                // Calculation of average parameter
                aver_param = 0.5*((double)param_values_1.get(param1_index) + param_2);
                
                // Setting of the values of current length ang corresponding parameter to arrays
                length_values[length1_counter] = cur_length;
                param_values[length1_counter]  = aver_param;
            }
            
            for(int length2_counter = 0; length2_counter<length_values_2_array.length; length2_counter++)
            {
                // Current length from 2nd file
                cur_length = length_values_2_array[length2_counter];
                param2_index = length2_counter;
                    
                // Search of length values from 1st file, which are closest to length value from 2nd file from left and right sides
                for(int length1_counter = 0; length1_counter<length_values_1_array.length-1; length1_counter++)
                {
                    left_length  = length_values_1_array[length1_counter];
                    right_length = length_values_1_array[length1_counter+1];
                    
                    if((cur_length >= left_length  & cur_length <= right_length)|
                       (cur_length >= right_length & cur_length <= left_length ))
                    {
                        param1_index = length1_counter;
                        length1_counter = length_values_1_array.length;
                    }
                }
                
                // Parameter values corresponding to found length values
                left_param  = (double)param_values_1.get(param1_index);
                right_param = (double)param_values_1.get(param1_index+1);
                
                // Calculation of the parameter from 1st file corresponding to length value from 2nd file
                if(left_length == right_length)
                    param_1 = left_param;
                else
                    param_1 = left_param + (right_param - left_param)*(cur_length - left_length)/(right_length - left_length);
                
                // Calculation of average parameter
                aver_param = 0.5*((double)param_values_2.get(param2_index) + param_1);
                
                // Setting of the values of current length ang corresponding parameter to arrays
                length_values[length_values_1_array.length + length2_counter] = cur_length;
                param_values [length_values_1_array.length + length2_counter] = aver_param;
            }
            
            // Writing of data to file
            for(int param_counter = 0; param_counter<param_values.length/2; param_counter++)
            {
                cur_length = length_values[param_counter];
                aver_param = param_values[param_counter];
                
                bw.write(cur_length+" "+aver_param+"\n");
                bw.flush();
            }
            
            bw.close();
            System.out.println("File '"+write_file_name+"' is written.");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method calculates average coordinates of neighbour points from initial file
     * and values of certain parameter in these points.
     * @param read_file_name  name of input file
     * @param write_file_name name of output file
     */
    private void calcTextFileForAveragePointsGraph(String read_file_name, String write_file_name)
    {
        System.out.println("Method 'PlaneCreation.calcTextFileForAveragePointsGraph(...)' is started...");
        
        StringTokenizer st;
        String string, coord_token, param_token;
        
        double prev_coord   = 0;
        double curr_coord   = 0;
        double aver_coord   = 0;        
        
        double prev_param   = 0;
        double curr_param   = 0;
        double aver_param   = 0;
        
        boolean first_string_in_file = true;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            
            while(br.ready())
            {  
                if(first_string_in_file)
                {
                    string = br.readLine();
                    st = new StringTokenizer(string);
                    coord_token = st.nextToken();
                    param_token = st.nextToken();
                    
                    prev_coord = (new Double(coord_token)).doubleValue();
                    prev_param = (new Double(param_token)).doubleValue();
                    
                    first_string_in_file = false;
                }
                else
                {
                    prev_coord = curr_coord;
                    prev_param = curr_param;                    
                }
                
                string = br.readLine();
                st = new StringTokenizer(string);
                coord_token = st.nextToken();
                param_token = st.nextToken(); 
                    
                curr_coord = (new Double(coord_token)).doubleValue();
                curr_param = (new Double(param_token)).doubleValue();
                
                aver_coord = (prev_coord + curr_coord)/2;
                aver_param = (prev_param + curr_param)/2;
                
                bw.write(aver_coord+" "+aver_param+"\n");
                bw.flush();
            }
            
            br.close();
            bw.close();
            
            System.out.println("File '"+write_file_name+"' is created!");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        
        System.out.println("Method 'PlaneCreation.calcTextFileForAveragePointsGraph(...)' is finished...");
    }
    
    /** The method creates file with cell parameters parallelepiped region contained in specimen
     * for showing of this region in right basis.
     * @param left_coord_X coordinate X of left facet of region
     * @param right_coord_X coordinate X of right facet of region
     * @param front_coord_Y coordinate Y of front facet of region
     * @param back_coord_Y coordinate Y of back facet of region
     * @param top_coord_Z coordinate Z of top facet of region
     * @param bottom_coord_Z coordinate Z of bottom facet of region
     * @param step_counter current time step
     * @param step_number total number of time steps
     */
    private void showSpecimenRegionInRightBasis(String _task_name, double left_coord_X, double right_coord_X, double front_coord_Y, 
                                                double back_coord_Y, double top_coord_Z,   double bottom_coord_Z,
                                                int step_counter, int step_number)
    {
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation
        // of current time step
        int exponent = 0;

        if(step_counter >= 0)
            exponent     = (int)Math.floor(Math.log10(step_counter+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
         
        // Read file name
        read_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_"+zeros+step_counter+".res";
        
        // Name of file for weiting
        write_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_inner_region_in_right_basis/"+_task_name+"_inner_region_in_right_basis_"+zeros+step_counter+".res";
        
        String string;
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        // Cell coordinates
        double coord_X;
        double coord_Y;
        double coord_Z;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
         //   System.out.println("read_file_name: "+read_file_name);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
         //   System.out.println("write_file_name: "+write_file_name);
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        // Assignment of cell coordinates
                        coord_X = (new Double(tokens[3])).doubleValue();
                        coord_Y = (new Double(tokens[4])).doubleValue();
                        coord_Z = (new Double(tokens[5])).doubleValue();
                        
                        if((coord_X>=left_coord_X)&(coord_X<=right_coord_X))
                        if((coord_Y>=front_coord_Y)&(coord_Y<=back_coord_Y))
                        if((coord_Z>=top_coord_Z)&(coord_Z<=bottom_coord_Z))
                        {
                            // Replacement of coordinate X and coordinate Y
                            string = tokens[3];
                            tokens[3] = tokens[4];
                            tokens[4] = string;
                            
                            // Writing of coordinates and physical parameters of current cell
                            for(int token_counter=0; token_counter<token_number; token_counter++)
                                bw.write(tokens[token_counter]+" ");
                            
                            bw.newLine();
                        }
                    }
                    else
                    {
                        bw.write(string);
                        bw.newLine();
                    }
                    
                    bw.flush();
                }
            }
            
            System.out.println("File '"+read_file_name+"' is read.");
            System.out.println("File '"+write_file_name+"' is written.");
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
           
    /** The method creates file with cell stress tensor components in parallelepiped region contained in specimen
     * for showing of this region in right basis. 
     * @param left_coord_X coordinate X of left facet of region
     * @param right_coord_X coordinate X of right facet of region
     * @param front_coord_Y coordinate Y of front facet of region
     * @param back_coord_Y coordinate Y of back facet of region
     * @param top_coord_Z coordinate Z of top facet of region
     * @param bottom_coord_Z coordinate Z of bottom facet of region
     * @param step_counter current time step
     * @param step_number total number of time steps
     */
    private void showSpecimenRegionInRightBasisForStressTensorFile(String _task_name, double left_coord_X, double right_coord_X, double front_coord_Y, 
                                                double back_coord_Y, double top_coord_Z,   double bottom_coord_Z,
                                                int step_counter, int step_number)
    {
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation
        // of current time step
        int exponent = 0;

        if(step_counter >= 0)
            exponent     = (int)Math.floor(Math.log10(step_counter+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
         
        // Read file name
        read_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_"+zeros+step_counter+"_stresses.res";
        
        // Name of file for weiting
        write_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_inner_region_in_right_basis_stresses/"+_task_name+"_inner_region_in_right_basis_"+zeros+step_counter+"_stresses.res";
        
        String string;
        int token_number = 16;
        String[] tokens = new String[token_number];
        
        // Cell coordinates
        double coord_X;
        double coord_Y;
        double coord_Z;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
         //   System.out.println("read_file_name: "+read_file_name);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
         //   System.out.println("write_file_name: "+write_file_name);
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        // Assignment of cell coordinates
                        coord_X = (new Double(tokens[0])).doubleValue();
                        coord_Y = (new Double(tokens[1])).doubleValue();
                        coord_Z = (new Double(tokens[2])).doubleValue();
                        
                        if((coord_X>=left_coord_X)&(coord_X<=right_coord_X))
                        if((coord_Y>=front_coord_Y)&(coord_Y<=back_coord_Y))
                        if((coord_Z>=top_coord_Z)&(coord_Z<=bottom_coord_Z))
                        {
                            // Replacement of coordinate X and coordinate Y
                            string = tokens[0];
                            tokens[0] = tokens[1];
                            tokens[1] = string;
                            
                            // Writing of coordinates and physical parameters of current cell
                            for(int token_counter=0; token_counter<token_number; token_counter++)
                                bw.write(tokens[token_counter]+" ");
                            
                            bw.newLine();
                        }
                    }
                    else
                    {
                        bw.write(string);
                        bw.newLine();
                    }
                    
                    bw.flush();
                }
            }
            
            System.out.println("File '"+read_file_name+"' is read.");
            System.out.println("File '"+write_file_name+"' is written.");
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
             
    /** The method creates file with cell strain tensor components in parallelepiped region contained in specimen
     * for showing of this region in right basis. 
     * @param left_coord_X coordinate X of left facet of region
     * @param right_coord_X coordinate X of right facet of region
     * @param front_coord_Y coordinate Y of front facet of region
     * @param back_coord_Y coordinate Y of back facet of region
     * @param top_coord_Z coordinate Z of top facet of region
     * @param bottom_coord_Z coordinate Z of bottom facet of region
     * @param step_counter current time step
     * @param step_number total number of time steps
     */
    private void showSpecimenRegionInRightBasisForStrainTensorFile(String _task_name, double left_coord_X, double right_coord_X, double front_coord_Y, 
                                                double back_coord_Y, double top_coord_Z,   double bottom_coord_Z,
                                                int step_counter, int step_number)
    {
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation
        // of current time step
        int exponent = 0;

        if(step_counter >= 0)
            exponent     = (int)Math.floor(Math.log10(step_counter+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
         
        // Read file name
        read_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_"+zeros+step_counter+"_strains.res";
        
        // Name of file for weiting
        write_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_inner_region_in_right_basis_strains/"+_task_name+"_inner_region_in_right_basis_"+zeros+step_counter+"_strains.res";
        
        String string;
        int token_number = 15;
        String[] tokens = new String[token_number];
        
        // Cell coordinates
        double coord_X;
        double coord_Y;
        double coord_Z;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
         //   System.out.println("read_file_name: "+read_file_name);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
         //   System.out.println("write_file_name: "+write_file_name);
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        // Assignment of cell coordinates
                        coord_X = (new Double(tokens[0])).doubleValue();
                        coord_Y = (new Double(tokens[1])).doubleValue();
                        coord_Z = (new Double(tokens[2])).doubleValue();
                        
                        if((coord_X>=left_coord_X)&(coord_X<=right_coord_X))
                        if((coord_Y>=front_coord_Y)&(coord_Y<=back_coord_Y))
                        if((coord_Z>=top_coord_Z)&(coord_Z<=bottom_coord_Z))
                        {
                            // Replacement of coordinate X and coordinate Y
                            string = tokens[0];
                            tokens[0] = tokens[1];
                            tokens[1] = string;
                            
                            // Writing of coordinates and physical parameters of current cell
                            for(int token_counter=0; token_counter<token_number; token_counter++)
                                bw.write(tokens[token_counter]+" ");
                            
                            bw.newLine();
                        }
                    }
                    else
                    {
                        bw.write(string);
                        bw.newLine();
                    }
                    
                    bw.flush();
                }
            }
            
            System.out.println("File '"+read_file_name+"' is read.");
            System.out.println("File '"+write_file_name+"' is written.");
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }  
               
    /** The method creates file with cell stress tensor components in parallelepiped region contained in specimen
     * for showing of this region in right basis. 
     * @param left_index_I   index I of left facet of region
     * @param right_index_I  index I of right facet of region
     * @param front_index_J  index J of front facet of region
     * @param back_index_J   index J of back facet of region
     * @param top_index_K    index K of top facet of region
     * @param bottom_index_K index K of bottom facet of region
     * @param step_counter   current time step
     * @param step_number    total number of time steps
     */
    private void showSpecimenRegionInRightBasisForStressTensorFile(String _task_name, int left_index_I, int right_index_I, 
                                                int front_index_J, int back_index_J, int top_index_K,   int bottom_index_K,
                                                int step_counter, int step_number)
    {
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation
        // of current time step
        int exponent = 0;

        if(step_counter >= 0)
            exponent     = (int)Math.floor(Math.log10(step_counter+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
         
        // Read file name
        read_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_"+zeros+step_counter+"_stresses.res";
        
        // Name of file for weiting
        write_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_inner_region_in_right_basis_stresses/"+_task_name+"_inner_region_in_right_basis_"+zeros+step_counter+"_stresses.res";
        
        String string;
        int token_number = 19;
        String[] tokens = new String[token_number];
        
        // Cell indices
        int index_I;
        int index_J;
        int index_K;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
         //   System.out.println("read_file_name: "+read_file_name);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
         //   System.out.println("write_file_name: "+write_file_name);
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        // Assignment of cell coordinates
                        index_I = (new Integer(tokens[0])).intValue();
                        index_J = (new Integer(tokens[1])).intValue();
                        index_K = (new Integer(tokens[2])).intValue();
                        
                        if((index_I>=left_index_I)&(index_I<=right_index_I))
                        if((index_J>=front_index_J)&(index_J<=back_index_J))
                        if((index_K>=top_index_K)&(index_K<=bottom_index_K))
                        {
                            // Replacement of index I and index J
                            string = tokens[0];
                            tokens[0] = tokens[1];
                            tokens[1] = string;
                            
                            // Replacement of coordinate X and coordinate Y
                            string = tokens[3];
                            tokens[3] = tokens[4];
                            tokens[4] = string;
                            
                            // Writing of coordinates and physical parameters of current cell
                            for(int token_counter=0; token_counter<token_number; token_counter++)
                                bw.write(tokens[token_counter]+" ");
                            
                            bw.newLine();
                        }
                    }
                    else
                    {
                        bw.write(string);
                        bw.newLine();
                    }
                    
                    bw.flush();
                }
            }
            
            System.out.println("File '"+read_file_name+"' is read.");
            System.out.println("File '"+write_file_name+"' is written.");
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
             
    /** The method creates file with cell strain tensor components in parallelepiped region contained in specimen
     * for showing of this region in right basis. 
     * @param left_index_I   index I of left facet of region
     * @param right_index_I  index I of right facet of region
     * @param front_index_J  index J of front facet of region
     * @param back_index_J   index J of back facet of region
     * @param top_index_K    index K of top facet of region
     * @param bottom_index_K index K of bottom facet of region
     * @param step_counter   current time step
     * @param step_number    total number of time steps
     */
    private void showSpecimenRegionInRightBasisForStrainTensorFile(String _task_name, int left_index_I, int right_index_I, 
                                                int front_index_J, int back_index_J, int top_index_K,   int bottom_index_K,
                                                int step_counter, int step_number)
    {
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation
        // of current time step
        int exponent = 0;

        if(step_counter >= 0)
            exponent     = (int)Math.floor(Math.log10(step_counter+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
         
        // Read file name
        read_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_"+zeros+step_counter+"_strains.res";
        
        // Name of file for weiting
        write_file_name = "./user/task_db/"+_task_name+"/"+_task_name+"_inner_region_in_right_basis_strains/"+_task_name+"_inner_region_in_right_basis_"+zeros+step_counter+"_strains.res";
                
        String string;
        int token_number = 19;
        String[] tokens = new String[token_number];
        
        // Cell indices
        int index_I;
        int index_J;
        int index_K;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
         //   System.out.println("read_file_name: "+read_file_name);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
         //   System.out.println("write_file_name: "+write_file_name);
            
            while(br.ready())
            {
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    tokens[0] = st.nextToken();

                    if(!tokens[0].equals("#"))
                    {
                        // Reading of coordinates and physical parameters of current cell
                        for(int token_counter=1; token_counter<token_number; token_counter++)
                            tokens[token_counter] = st.nextToken();
                        
                        // Assignment of cell coordinates
                        index_I = (new Integer(tokens[0])).intValue();
                        index_J = (new Integer(tokens[1])).intValue();
                        index_K = (new Integer(tokens[2])).intValue();
                        
                        if((index_I>=left_index_I)&(index_I<=right_index_I))
                        if((index_J>=front_index_J)&(index_J<=back_index_J))
                        if((index_K>=top_index_K)&(index_K<=bottom_index_K))
                        {
                            // Replacement of index I and index J
                            string = tokens[0];
                            tokens[0] = tokens[1];
                            tokens[1] = string;
                            
                            // Replacement of coordinate X and coordinate Y
                            string = tokens[3];
                            tokens[3] = tokens[4];
                            tokens[4] = string;
                            
                            // Writing of coordinates and physical parameters of current cell
                            for(int token_counter=0; token_counter<token_number; token_counter++)
                                bw.write(tokens[token_counter]+" ");
                            
                            bw.newLine();
                        }
                    }
                    else
                    {
                        bw.write(string);
                        bw.newLine();
                    }
                    
                    bw.flush();
                }
            }
            
            System.out.println("File '"+read_file_name+"' is read.");
            System.out.println("File '"+write_file_name+"' is written.");
            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method switches states of cells located in plane of automata
     * according to values of force moments.
     * @param step_number number of time step corresponding to read file
     * @param index_Z number of plane of automata along axis OZ
     * @param threshold threshold value for automaton switching
     *
     */
    public void switchCells(int step_number, int total_step_number, int index_Z, double threshold)
    {
        read_file_name = "./task/force_moments_2009_09_24_"+step_number+"_z="+index_Z+"_coord_X.txt";
        write_file_name= "./task/force_moments_states_2009_09_24_"+step_number+"_z="+index_Z+"_coord_X.txt";
        
        String string;
        double prob_switch = 0;

        // Parameters of cell
        int state;
        double coordinate_X;
        double coordinate_Y;
    //    double coordinate_Z;
        double value;
        int cell_number = 0;
        int state0_number = 0;
        int state1_number = 0;
        int state2_number = 0;
        double max_value = 0;
        double sum = 0;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            while(br.ready())
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell                    
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    value        = new Double(st.nextToken()).doubleValue();
                    
                    // Calculation of switch probability   
                    prob_switch = (1-Math.exp(-value*value/(2*threshold*threshold)))*
                            step_number/total_step_number;

                    //Calculation of maximal absolute value of given parameter of cell
                    if(Math.abs(value)>max_value)
                        max_value = Math.abs(value);

                    //Calculation of sum of values
                    sum = sum+Math.abs(value);

                    // Switch of cell state
                    if(Math.random() < prob_switch)
                //    if(Math.abs(value)>threshold)
                    {
                        if(value<0)
                        {
                            bw.write(coordinate_X+" "+coordinate_Y+" 0");
                            state0_number++;
                        }

                        if(value>0)
                        {
                            bw.write(coordinate_X+" "+coordinate_Y+" 2");
                            state2_number++;
                        }
                    }
                    else
                    {
                        bw.write(coordinate_X+" "+coordinate_Y+" 1");
                        state1_number++;
                    }

                    // Calculation of number of cells in plane (test)
                    cell_number++;

                    bw.newLine();
                    bw.flush();
                }
            }

            System.out.println("Total number of cells:        "+cell_number);
            System.out.println("Number of cells in state '0': "+state0_number+"; Portion: "+state0_number*1.0/cell_number);
            System.out.println("Number of cells in state '1': "+state1_number+"; Portion: "+state1_number*1.0/cell_number);
            System.out.println("Number of cells in state '2': "+state2_number+"; Portion: "+state2_number*1.0/cell_number);
            System.out.println("Average value: "+sum/cell_number);
            System.out.println("Max. abs. value: "+max_value);

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file for graph of the dependence
     * "average area of grain section - (coordinate Z of secant plane, time step number)".
     */
    public void createSurface()
    {
        String string;

        // Parameters of cell
        double grain_index;

        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double current_coord_Z = 0.0;

        int file_number  = 61;
        int record_period = 50;
        int string_number = 108000;

        // Indices of grains intersected by current plane "z = const"
        ArrayList grain_indices_in_section;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            for(int file_counter = 0; file_counter < file_number; file_counter++)
            {
                BufferedReader br = new BufferedReader(new FileReader(read_file_name+(record_period*file_counter)+"_grains.txt"));

                current_coord_Z = 1.0;

                grain_indices_in_section = new ArrayList(0);

                for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
                {
                    string = br.readLine();

                    StringTokenizer st = new StringTokenizer(string);

                    if(st.hasMoreTokens())
                    {
                        // Reading of coordinates and physical parameters of current cell
                        grain_index  = new Double(st.nextToken()).doubleValue();
                        coordinate_X = new Double(st.nextToken()).doubleValue();
                        coordinate_Y = new Double(st.nextToken()).doubleValue();
                        coordinate_Z = new Double(st.nextToken()).doubleValue();

                        if(coordinate_Z != current_coord_Z)
                        if(!grain_indices_in_section.isEmpty())
                        {

                            bw.write(file_counter*1.0+" "+
                                     (Math.round((current_coord_Z-1)/1.632993161855452)*1.0-0.5)+" "+
                                     Math.sqrt(section_area/grain_indices_in_section.size()));
                            /*
                            bw.write(file_counter*record_period*1.0+" "+
                                     current_coord_Z+" "+
                                     Math.sqrt(section_area/grain_indices_in_section.size()));
                            */
                            bw.newLine();
                            bw.flush();

                            current_coord_Z = coordinate_Z;
                            grain_indices_in_section = new ArrayList(0);
                        }
                        else
                            current_coord_Z = coordinate_Z;

                        if(!grain_indices_in_section.contains(grain_index))
                            grain_indices_in_section.add(grain_index);
                    }
                }

                bw.write(file_counter*1.0+" "+
                        (Math.round((current_coord_Z-1)/1.632993161855452)*1.0-0.5)+" "+
                        Math.sqrt(section_area/grain_indices_in_section.size()));
                /*
                bw.write(file_counter*record_period*1.0+" "+
                        current_coord_Z+" "+
                        Math.sqrt(section_area/grain_indices_in_section.size()));
                  */
                bw.newLine();
                bw.flush();

                br.close();

                System.out.println("File # "+file_counter+" is read!");
            }

            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file with distribution of values of certain parameter 
     * associated with strain tensor in chosen plane.
     * @param task_name name of considered task
     * @param param_name name of necessary parameter
     */
    private void createSurfaceOfStrainParameterValues(String task_name, String param_name)
    {   
        System.out.println("Method 'createSurfaceOfStrainParameterValues(String task_name, String param_name)' is started!!!");
        
        String string;

        // Parameters of cell from file "*_strains.res":        
        //  0- 2. the indices of cell: i, j, k;
        int index_I=0, index_J=0, index_K=0;
        
        //  3- 5. the coordinates of cell: x, y, z;
        double coordinate_X=0, coordinate_Y=0, coordinate_Z=0;
        
        //  6- 8. the coordinates of cell displacement vector: u_x, u_y, u_z;
        double displ_X=0, displ_Y=0, displ_Z=0;
        
        //  9-11. the diagonal components of cell strain tensor: e_xx, e_yy, e_zz;
        // 12-14. the non-diagonal components of cell strain tensor: e_xy=e_yx, e_yz=e_zy, e_zx=e_xz;
        // 15-17. the shear components: O_xy=-O_yx, O_yz=-O_zy, O_zx=-O_xz.
        // 18. the average strain e_av = (e_xx + e_yy + e_zz)/3.
        
        // Number of file
        file_number   = 1;
        
        // Period (in time steps) between neighbour files
        record_period = 50000;
        
        try
        {
            for(int file_counter = 1; file_counter <= file_number; file_counter++)
            {
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_0"+record_period*file_number+"_strains.res";
                BufferedReader br = new BufferedReader(new FileReader(read_file_name));
                
                write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_0"+record_period*file_number+"_Z=0_"+param_name+".txt";
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                
                while(br.ready())
                {
                    string = br.readLine();
                    StringTokenizer st = new StringTokenizer(string);

                    if(st.hasMoreTokens())
                    {
                      string = st.nextToken();
                        
                      if(!string.equals("#"))
                      {                          
                        // Reading of indices, coordinates and physical parameters of current cell  
                        index_I = new Integer(string).intValue();
                        index_J = new Integer(st.nextToken()).intValue();
                        index_K = new Integer(st.nextToken()).intValue();                        
                                              
                        coordinate_X = new Double(st.nextToken()).doubleValue();
                        coordinate_Y = new Double(st.nextToken()).doubleValue();
                        coordinate_Z = new Double(st.nextToken()).doubleValue();
                        
                        displ_X = new Double(st.nextToken()).doubleValue();
                        displ_Y = new Double(st.nextToken()).doubleValue();
                        displ_Z = new Double(st.nextToken()).doubleValue();
                        
                        if(index_K == 1)
                        {
                          // Writing of coordinates of each cell at plane and its chosen parameter
                          bw.write(coordinate_X+" "+coordinate_Y);
                          
                          if(param_name.equals("displ_X"))
                            bw.write(" "+displ_X);
                        
                          if(param_name.equals("displ_Y"))
                            bw.write(" "+displ_Y);
                          
                          if(param_name.equals("displ_Z"))
                            bw.write(" "+displ_Z);
                          
                          bw.newLine();
                          bw.flush();
                        }
                      }
                    }
                }                
                
                br.close();
                System.out.println("File "+read_file_name+" is read!");
                
                bw.close();
                System.out.println("File "+write_file_name+" is written!");                      
            }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
    
    /** The method creates file with distribution of values of certain parameter 
     * associated with stress tensor in chosen plane.
     * @param task_name name of considered task
     * @param param_name name of necessary parameter
     */
    private void createSurfaceOfStressParameterValues(String task_name, String param_name)
    {           
        System.out.println("Method 'createSurfaceOfStressParameterValues(String task_name, String param_name)' is started!!!");
        
        String string;

        // Parameters of cell from file "*_strains.res":        
        //  0- 2. the indices of cell: i, j, k;
        int index_I=0, index_J=0, index_K=0;
        
        //  3- 5. the coordinates of cell: x, y, z;
        double coordinate_X=0, coordinate_Y=0, coordinate_Z=0;
        
        //  6-14. the components of cell stress tensor: s_xx, t_xy, t_xz, t_yx, s_yy, t_yz, t_zx, t_zy, s_zz;
        double s_XX=0, t_XY=0, t_XZ=0, t_YX=0, s_YY=0, t_YZ=0, t_ZX=0, t_ZY=0, s_ZZ=0;
        
        // 15-17. the components of stress moment vector: M_x=t_zy-t_yz, M_y=t_xz-t_zx, M_z=t_yx-t_xy.
        // 18. the average stress s_av = (s_xx + s_yy + s_zz)/3.
        
        // Number of file
        file_number   = 1;
        
        // Period (in time steps) between neighbour files
        record_period = 50000;
        
        try
        {
            for(int file_counter = 1; file_counter <= file_number; file_counter++)
            {
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_0"+record_period*file_number+"_stresses.res";
                BufferedReader br = new BufferedReader(new FileReader(read_file_name));
                
                write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_0"+record_period*file_number+"_Z=0_"+param_name+".txt";
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                
                while(br.ready())
                {
                    string = br.readLine();
                    StringTokenizer st = new StringTokenizer(string);

                    if(st.hasMoreTokens())
                    {
                      string = st.nextToken();
                        
                      if(!string.equals("#"))
                      {                          
                        // Reading of indices and coordinates of current cell
                        index_I = new Integer(string).intValue();
                        index_J = new Integer(st.nextToken()).intValue();
                        index_K = new Integer(st.nextToken()).intValue();                        
                        
                        if(index_K == 1)
                        {                      
                          coordinate_X = new Double(st.nextToken()).doubleValue();
                          coordinate_Y = new Double(st.nextToken()).doubleValue();
                          coordinate_Z = new Double(st.nextToken()).doubleValue();
                          
                          // Reading of stress tensor components of current cell
                          s_XX = new Double(st.nextToken()).doubleValue();
                          t_XY = new Double(st.nextToken()).doubleValue();
                          t_XZ = new Double(st.nextToken()).doubleValue();
                          t_YX = new Double(st.nextToken()).doubleValue();
                          s_YY = new Double(st.nextToken()).doubleValue();
                          t_YZ = new Double(st.nextToken()).doubleValue();
                          t_ZX = new Double(st.nextToken()).doubleValue();
                          t_ZY = new Double(st.nextToken()).doubleValue();
                          s_ZZ = new Double(st.nextToken()).doubleValue();
                        
                          // Writing of coordinates of each cell at plane and its chosen parameter
                          bw.write(coordinate_X+" "+coordinate_Y);
                          
                          if(param_name.equals("s_XX")) bw.write(" "+s_XX);
                          if(param_name.equals("t_XY")) bw.write(" "+t_XY);
                          if(param_name.equals("t_XZ")) bw.write(" "+t_XZ);
                          
                          if(param_name.equals("t_YX")) bw.write(" "+t_YX);
                          if(param_name.equals("s_YY")) bw.write(" "+s_YY);
                          if(param_name.equals("t_YZ")) bw.write(" "+t_YZ);
                          
                          if(param_name.equals("t_ZX")) bw.write(" "+t_ZX);
                          if(param_name.equals("t_ZY")) bw.write(" "+t_ZY);
                          if(param_name.equals("s_ZZ")) bw.write(" "+s_ZZ);
                          
                          bw.newLine();
                          bw.flush();
                        }
                      }
                    }
                }                
                
                br.close();
                System.out.println("File "+read_file_name+" is read!");
                
                bw.close();
                System.out.println("File "+write_file_name+" is written!");                      
            }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
    
    /**The method creates file with distribution of 
     * values of certain parameter at cell boundaries in chosen plane.
     * @param task_name     name of considered task
     * @param plane_coord_Z coordinate Z of chosen plane
     * @param param_name    name of necessary parameter
     */
    private void createSurfaceOfCellBoundParameters(String task_name, double plane_coord_Z, String param_name)
    {           
        System.out.println("Method 'createSurfaceOfCellBoundParameters(String task_name, double plane_coord_Z, String param_name)' is started!!!");
        
        String string;

        // Parameters of cell from file "*_bounds.res":        
        //  0-1. the single indices of neighbour cells;
        int first_cell_index=0, second_cell_index=0;
        
        //  2-4. the coordinates of cell boundary;
        double coordinate_X=0, coordinate_Y=0, coordinate_Z=0;
        
        //  5-7. coordinates of boundary normal vector;
        double bound_norm_vec_X = 0, bound_norm_vec_Y = 0, bound_norm_vec_Z = 0;
        
        // 8. stress at boundary; 
        double bound_stress = 0;
        
        // 9. velocity of material strain at boundary.
        double bound_velocity = 0;
        
        // Components of cell boundary stress
        double stress_X = 0, stress_Y = 0, stress_Z = 0;
        
        // Components of cell boundary velocity
        double velocity_X = 0, velocity_Y = 0, velocity_Z = 0;
                        
        // Number of file
        file_number   = 1;
        
        // Period (in time steps) between neighbour files
        record_period = 50000;
        
        try
        {
            for(int file_counter = 1; file_counter <= file_number; file_counter++)
            {
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_0"+record_period*file_number+"_bounds.res";
                BufferedReader br = new BufferedReader(new FileReader(read_file_name));
                
                write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_0"+record_period*file_number+"_Z="+plane_coord_Z+"_"+param_name+".txt";
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                
                while(br.ready())
                {
                    string = br.readLine();
                    StringTokenizer st = new StringTokenizer(string);

                    if(st.hasMoreTokens())
                    {
                      string = st.nextToken();
                        
                      if(!string.equals("#"))
                      {                          
                        // Reading of indices and coordinates of current cell
                        first_cell_index = new Integer(string).intValue();
                        second_cell_index = new Integer(st.nextToken()).intValue();
                        
                        coordinate_X = new Double(st.nextToken()).doubleValue();
                        coordinate_Y = new Double(st.nextToken()).doubleValue();
                        coordinate_Z = new Double(st.nextToken()).doubleValue();
                        
                        if(coordinate_Z == plane_coord_Z)
                        {
                          // Reading of the coordinates of current cell boundary centre
                          bound_norm_vec_X = new Double(st.nextToken()).doubleValue();
                          bound_norm_vec_Y = new Double(st.nextToken()).doubleValue();
                          bound_norm_vec_Z = new Double(st.nextToken()).doubleValue();
                            
                          // Reading of the parameters of current cell boundary
                          bound_stress   = new Double(st.nextToken()).doubleValue();
                          bound_velocity = new Double(st.nextToken()).doubleValue();
                          
                          // Calculation of the components of the stress vector at current cell boundary
                          stress_X   = bound_stress*bound_norm_vec_X;
                          stress_Y   = bound_stress*bound_norm_vec_Y;
                          stress_Z   = bound_stress*bound_norm_vec_Z;
                          
                          // Calculation of the components of the velocity vector at current cell boundary
                          velocity_X = bound_velocity*bound_norm_vec_X;
                          velocity_Y = bound_velocity*bound_norm_vec_Y;
                          velocity_Z = bound_velocity*bound_norm_vec_Z;
                          
                          // Writing of coordinates of each cell at plane and its chosen parameter
                          bw.write(coordinate_X+" "+coordinate_Y);
                          
                          if(param_name.equals("bound_stress_X"))   bw.write(" "+stress_X);
                          if(param_name.equals("bound_stress_Y"))   bw.write(" "+stress_Y);
                          if(param_name.equals("bound_stress_Z"))   bw.write(" "+stress_Z);
                          if(param_name.equals("abs_bound_stress")) bw.write(" "+Math.abs(bound_stress));
                          
                          if(param_name.equals("bound_velocity_X")) bw.write(" "+velocity_X);
                          if(param_name.equals("bound_velocity_Y")) bw.write(" "+velocity_Y);
                          if(param_name.equals("bound_velocity_Z")) bw.write(" "+velocity_Z);
                          if(param_name.equals("abs_bound_velocity")) bw.write(" "+Math.abs(bound_velocity));
                          
                          bw.newLine();
                          bw.flush();
                        }
                      }
                    }
                }                
                
                br.close();
                System.out.println("File "+read_file_name+" is read!");
                
                bw.close();
                System.out.println("File "+write_file_name+" is written!");                      
            }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
   
    /** The method creates file for graph of the dependence
     * "average temperature of specimen - time step number".
     */
    public void createGraphTemperatures()
    {
        String string;

        // Temperature of cell
        double temperature = 0;

        // Average temperature of specimen
        double average_temperature = 0;

        int file_number   = 61;
        int record_period = 50;
        int string_number = 108000;

        BufferedReader br;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            bw.write("0.0 300.0");
            bw.newLine();
            bw.flush();

            for(int file_counter = 1; file_counter < file_number; file_counter++)
            {
                br = new BufferedReader(new FileReader(read_file_name+(record_period*file_counter)+"_temperatures.txt"));

                average_temperature = 0;

                for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
                {
                    string = br.readLine();

                    StringTokenizer st = new StringTokenizer(string);

                    if(st.hasMoreTokens())
                    {
                        // Reading of coordinates and physical parameters of current cell
                        temperature  = new Double(st.nextToken()).doubleValue();

                        average_temperature += temperature;
                    }
                }

                average_temperature = average_temperature/string_number;

                bw.write(file_counter*record_period*1.0+" "+average_temperature);
                bw.newLine();
                bw.flush();

                br.close();

                System.out.println("File # "+file_counter+" is read!");
            }

            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file for graph of distribution of stresses
     * in specimen along axis OZ.
     */
    public void createGraphStressesAlongOZ()
    {
        String string;

        // Coordinates (in cell sizes), state, stress and strain of cell
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        int state_value;
        double stress = 0;
        double strain = 0;

        // Modulus of elasticity of aluminium
        double mod_elast_0 = 6.90E10;

        // Modulus of elasticity of ceramics
        double mod_elast_1 = 3.45E11;

        int file_number   = 30;
        int record_period = 10000;
        int string_number = 54000;

        for(int file_counter=1; file_counter<=file_number; file_counter++)
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name+"_"+(file_counter*record_period)+".txt"));
            BufferedReader br = new BufferedReader(new FileReader(read_file_name+"_"+(file_counter*record_period)+"_mech_energies.txt"));

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    state_value  = new Integer(st.nextToken()).intValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();

                    // Reading of coordinates and physical parameters of current cell
                    stress  = new Double(st.nextToken()).doubleValue();

              //      if((coordinate_X>25.0)&(coordinate_X<26.5))
                    if((coordinate_Y>28.5)&(coordinate_Y<30.5))
                    {
                        if(state_value == 0)
                            strain = stress/mod_elast_0;

                        if(state_value == 1)
                            strain = stress/mod_elast_1;

                        // Transition to micrometers
                    //    coordinate_Z = 0.5*cell_size*(coordinate_Z-1)*1.0E6;
                        coordinate_Z = 0.5*cell_size*coordinate_Z*1.0E6;
                        coordinate_X = 0.5*cell_size*coordinate_X*1.0E6;

                       // bw.write(coordinate_Z+" "+(stress)/1000.0);
                      //  bw.write(coordinate_Z+" "+strain);
                        bw.write(coordinate_X+" "+coordinate_Z+" "+strain);
                        bw.newLine();
                        bw.flush();
                    }
               }
            }

            br.close();
            bw.close();

            System.out.println("File # "+file_counter+" is read!");
            System.out.println("File # "+file_counter+" is written!");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file for graph of distribution of temperatures
     * in specimen along axis OZ.
     */
    private void createGraphTemperaturesAlongOZ(String task_name)
    {
        read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_";
        write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_temperatures_OZ/"+task_name+"_";
        
        String string;
        int cell_number = 0;

        // Cell parameters: 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 6. temperature.        
        byte energy_type;
        byte location_type;
        byte grain_index;        
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double old_coordinate_Z = -1;
        double temperature;
        
        String[] time_steps = new String[9];
        String[] time_periods = new String[9];
        
        if(task_name.equals("copper_all_facets_tempr_1000"))
        {
            // Specimen sizes (in cell sizes)
            specimen_size_X = 62;
            specimen_size_Y = 52;
            specimen_size_Z = 42;
            
            // Cell size (in meters)
            cell_size = 1.0E-6;
            
            // Time step indices
            time_steps[0] = "000100";
            time_steps[1] = "000200";
            time_steps[2] = "000500";
            time_steps[3] = "001000";
            time_steps[4] = "002000";
            time_steps[5] = "003000";
            time_steps[6] = "004000";
            time_steps[7] = "005000";
            time_steps[8] = "010000";
            
            // Time periods (in nanoseconds)
            time_periods[0] = "000100";
            time_periods[1] = "000200";
            time_periods[2] = "000500";
            time_periods[3] = "001000";
            time_periods[4] = "002000";
            time_periods[5] = "003000";
            time_periods[6] = "004000";
            time_periods[7] = "005000";
            time_periods[8] = "010000";
        }
        
        if(task_name.equals("Rubber_2_2_6_top_bottom_heat_423"))
        {
            // Specimen sizes (in cell sizes)
            specimen_size_X = 22;
            specimen_size_Y = 22;
            specimen_size_Z = 61;
            
            // Cell size (in meters)
            cell_size = 1.0E-4;
            
            // Time step indices
            time_steps[0] = "000010";
            time_steps[1] = "000100";
            time_steps[2] = "001000";
            time_steps[3] = "010000";
            time_steps[4] = "020000";
            time_steps[5] = "030000";
            time_steps[6] = "040000";
            time_steps[7] = "050000";
            time_steps[8] = "100000";
            
            // Time periods (in milliseconds)
            time_periods[0] = "000012";
            time_periods[1] = "000120";
            time_periods[2] = "001200";
            time_periods[3] = "012000";
            time_periods[4] = "024000";
            time_periods[5] = "036000";
            time_periods[6] = "048000";
            time_periods[7] = "060000";
            time_periods[8] = "120000";
        }      
        
        for(int file_counter=0; file_counter<time_steps.length; file_counter++)
        try
        {
            String current_read_file_name = read_file_name+time_steps[file_counter]+".res";
            String current_write_file_name = "";
            
            if(task_name.equals("copper_all_facets_tempr_1000"))
                current_write_file_name = write_file_name +time_periods[file_counter]+"_ns_temperatures_OZ.txt";
            
            if(task_name.equals("Rubber_2_2_6_top_bottom_heat_423"))
                current_write_file_name = write_file_name +time_periods[file_counter]+"_ms_temperatures_OZ.txt";            

            BufferedReader br = new BufferedReader(new FileReader(current_read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(current_write_file_name));
            
            cell_number = 0;
            
        //    bw.write("specimen_size_X = "+specimen_size_X+" specimen_size_Y = "+specimen_size_Y+" specimen_size_Z = "+specimen_size_Z);
          //  bw.newLine();
            
            while(br.ready())
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);
                
                string = st.nextToken();

                if(!string.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of parameters of current cell
                    energy_type   = new Byte(string).byteValue();
                    location_type = new Byte(st.nextToken()).byteValue();
                    grain_index   = new Byte(st.nextToken()).byteValue();
                    
                    // Coordinates are expressed in cell sizes.
                    coordinate_X  = new Double(st.nextToken()).doubleValue();
                    coordinate_Y  = new Double(st.nextToken()).doubleValue();
                    coordinate_Z  = new Double(st.nextToken()).doubleValue();
                    
                    if((coordinate_X>0.5*specimen_size_X-1)&(coordinate_X<0.5*specimen_size_X+1))
                    if((coordinate_Y>0.5*specimen_size_Y-1)&(coordinate_Y<0.5*specimen_size_Y+1))                    
                    if(old_coordinate_Z != coordinate_Z)
                    {   
                        old_coordinate_Z = coordinate_Z;
                        temperature   = new Double(st.nextToken()).doubleValue();
                        cell_number++;
                        
                        // Transition to meters          
                     //   coordinate_X = cell_size*coordinate_X;
                     //   coordinate_Y = cell_size*coordinate_Y;
                        coordinate_Z = cell_size*coordinate_Z;

                        bw.write(coordinate_Z+" "+temperature);
                        bw.newLine();
                        bw.flush();
                    }
               }
            }
            
            br.close();
            bw.close();
            
            System.out.println("File "+current_read_file_name+" is read!");
            System.out.println("File "+current_write_file_name+" is written!");
            System.out.println("Number of extracted cells: "+cell_number);
            System.out.println();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        cell_size = 1.0E-6;
    }    
    
    /** The method creates files for graphs "stress-strain", "stress-time", "strain-time", "absolute moment-time".
     */
    private void createGraphStressStrain()
    {
        String string, token_1;
        StringTokenizer st;
        
        double min_coord_Z = 0;// 6.215 - 0.5*Math.sqrt(2.0/3.0);// 
        double max_coord_Z = 0;// 6.215 + 0.5*Math.sqrt(2.0/3.0);// 
        
        // Coordinates (in cell sizes), state, stress and strain of cell
        int energy_type; 
        int location_type; 
        int grain_index;
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double temperature;
        double stress = 0;
        double pr_stress;
        double mom_X;
        double mom_Y;
        double mom_Z;
        double state;
        double strain;

        double abs_moment = 0;
        
        // Strain per time step
    //    double strain = 1.0E-12;
        
        time_step       =  1.0E-8;//  1.0E-9;
        file_number     =  100;
        record_period   =  1000;
        max_step_number = file_number*record_period;
        
        double total_stress = 0;
        double total_strain = 0;
        double total_moment = 0;
        int cell_number     = 0;
        double time_period  = 0;
        
        // Mod_elast for titanium
        double mod_elast                = 1.2E11; // 1.17E11; // 2e11;
        double diss_energy_portion      = 0;
        double aver_diss_energy_portion = 0;

        String write_file_name_1;
        String write_file_name_2;
        String write_file_name_3;

        BufferedReader br;
        BufferedWriter bw;
     //   BufferedWriter bw_1;
      //  BufferedWriter bw_2;
      //  BufferedWriter bw_3;
        BufferedWriter bw_load, bw_noLoad;
        
        // String of zeros
        String zeros = "";

        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(max_step_number));

        try
        {
          //  write_file_name   = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_strains&stresses.txt";
          // write_file_name   = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_strains&stresses_cylinder.txt";//_no_pores.txt";//
          write_file_name   = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_strains&stresses.txt";
          bw_load = new BufferedWriter(new FileWriter(write_file_name));
          
     //     write_file_name   = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_strains&stresses_noLoad.txt";
     //     bw_noLoad = new BufferedWriter(new FileWriter(write_file_name));
          
     //     write_file_name   = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_time&dissEnergyPortion.txt";
      //    bw = new BufferedWriter(new FileWriter(write_file_name));

        //  write_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&stresses.txt";
        //  write_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&moments_X.txt";
        //  write_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&abs_moments_X.txt";
        //  write_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&stresses_cylinder.txt";//_no_pores.txt";//
          write_file_name_1 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&stresses.txt";
        //  bw_1 = new BufferedWriter(new FileWriter(write_file_name_1));

       //   write_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&strains.txt";
        //  write_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&moments_Y.txt";
         // write_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&abs_moments_Y.txt";
        //  write_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&strains_cylinder.txt";//_no_pores.txt";//
          write_file_name_2 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&strains.txt";
        //  bw_2 = new BufferedWriter(new FileWriter(write_file_name_2));

       //   write_file_name_3 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&moments.txt";
       //   write_file_name_3 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&moments_Z.txt";
       //   write_file_name_3 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&abs_moments_Z.txt";
       //   write_file_name_3 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&moments_cylinder.txt";//_no_pores.txt";//
          write_file_name_3 = "./user/task_db/"+task_name+"/"+task_name+"_graphs/"+task_name+"_time&moments.txt";
       //   bw_3 = new BufferedWriter(new FileWriter(write_file_name_3));

       //   double indentor_radius = 6;
       //   double centre_X = 26;
       //   double centre_Y = 11.25;
       //   double distance = 0;

          for(int step_counter=0; step_counter<=max_step_number; step_counter++)
          {
            total_stress = 0;
            total_strain = 0;
            total_moment = 0;
            cell_number  = 0;
            aver_diss_energy_portion = 0;

            // Exponent of power of 10 in standard representation
            // of number of current time step
            int exponent = 0;

            if(step_counter > 0)
                exponent     = (int)Math.floor(Math.log10(step_counter));

            // String of zeros
            zeros = "";

            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";
            
            if((step_counter < record_period & step_counter % (record_period/10) == 0)|
                step_counter % record_period == 0)
            {    
              read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_counter+".res";
              br = new BufferedReader(new FileReader(read_file_name));

              while(br.ready())
              {
                string = br.readLine();

                st = new StringTokenizer(string);

                token_1 = st.nextToken();

                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    energy_type  = new Integer(token_1).intValue();
                    location_type  = new Integer(st.nextToken()).intValue();
                    
                    if(energy_type   == Common.INNER_CELL | energy_type   == Common.LAYER_CELL)
                    if(location_type != Common.INNER_BOUNDARY_CELL & location_type != Common.INNER_BOUNDARY_INTERGRANULAR_CELL &
                       location_type != Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL)
                //    if(location_type != Common.OUTER_CELL)
                //    if(energy_type   == Common.INNER_CELL)
                    {
                      // Reading of coordinates and physical parameters of current cell
                      grain_index  = new Integer(st.nextToken()).intValue();
                      coordinate_X = new Double(st.nextToken()).doubleValue();
                      coordinate_Y = new Double(st.nextToken()).doubleValue();
                      coordinate_Z = new Double(st.nextToken()).doubleValue();

                  //    if(coordinate_Z >= min_coord_Z & coordinate_Z < max_coord_Z)
                      {
                        temperature = new Double(st.nextToken()).doubleValue();
                        stress      = new Double(st.nextToken()).doubleValue();
                        pr_stress   = new Double(st.nextToken()).doubleValue();
                        mom_X       = new Double(st.nextToken()).doubleValue();
                        mom_Y       = new Double(st.nextToken()).doubleValue();
                        mom_Z       = new Double(st.nextToken()).doubleValue();
                        state       = new Byte(st.nextToken()).byteValue();
                        strain      = new Double(st.nextToken()).doubleValue();

                        abs_moment = Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                 //       stress     = Math.abs(stress);
                 //       strain     = Math.abs(strain);
                        
                        if(strain != 0) diss_energy_portion = 1 - Math.abs(stress/(mod_elast*strain));
                        else            diss_energy_portion = 0;
                        
                      //  if(diss_energy_portion < 0)// | diss_energy_portion > 1)
                        if(false)
                        {
                            System.out.println("step_counter = "+step_counter+" diss_energy_portion = " +diss_energy_portion);
                            br.close();
                        }
                        
                    //    if(strain <0)
                        if(false)
                        {
                            System.out.println("step_counter = "+step_counter+" strain = "+strain);
                            br.close();
                        }
                        
                        aver_diss_energy_portion += diss_energy_portion;
                        total_stress+=stress;
                        total_strain+=strain;
                        total_moment+=abs_moment;
                        
                        cell_number++;
                      }
                    }
                }
              }
              
              System.out.println("File "+read_file_name+" is read!");
              System.out.println("Number of cells in considered volume: "+cell_number);

         //     total_stress = Math.abs(total_stress);
         //     total_strain = Math.abs(total_strain);
              
              total_stress = - total_stress/cell_number;
              total_strain = - total_strain/cell_number;
              
              double total_diss_energy_portion = 0;
              
              if(total_strain != 0) total_diss_energy_portion = 1 - Math.abs(total_stress/(mod_elast*total_strain));
              else                  total_diss_energy_portion = 0;
                        
              aver_diss_energy_portion = aver_diss_energy_portion/cell_number;

        //    total_stress = total_stress*1.0E-8;
        //    total_strain = total_strain*1.0E-8;
        //    total_moment = total_moment*1.0E-8;
            
              br.close();

          //  time_period = Math.pow(10, -exponent-7)*Math.round(file_counter*record_period*time_step*Math.pow(10, exponent+7));
          //  time_period = Math.pow(10, -exponent-7)*Math.round(time_period*Math.pow(10, exponent+7));
          //  time_period = step_counter*time_step;
              
              // Time in microseconds
              time_period = (int)Math.round(step_counter*time_step*1.0E9)/1.0E3;
              
         //   bw.write(file_counter*record_period*strain+" "+total_stress/cell_number);
         //   bw_1.write(time_period+" "+total_stress/cell_number);
         //   bw_2.write(time_period+" "+total_strain/cell_number);
         //   bw_3.write(time_period+" "+total_moment/cell_number);
              
              // Stress in GPa
              total_stress = ((long)Math.round(total_stress)*1.0E6)/1.0E15;
          //  total_stress = Math.abs(total_stress);
              
              // Strain in %; absolute values of stress and strain
              total_strain = total_strain*100;
              
              if(step_counter <= max_step_number/2)
         //     if(step_counter <= max_step_number/5)
              {
                System.out.println("Loading: T = "+time_period+" mcs; aver_strain =  "+total_strain+" %; aver_stress = "+total_stress+" GPa.");
                bw_load  .write(total_strain+" "+total_stress);
                bw_load.newLine();
                bw_load.flush();
              }
         
              if(step_counter > max_step_number/2)
          //    if(step_counter > max_step_number/5)
              {
                System.out.println("No load: T = "+time_period+" mcs; aver_strain =  "+total_strain+" %; aver_stress = "+total_stress+" GPa.");
                bw_load.write(total_strain+" "+total_stress);
                bw_load.newLine();
                bw_load.flush();
              }
          
              
         //   System.out.println("aver_diss_energy_portion = "+aver_diss_energy_portion+"; total_diss_energy_portion = "+total_diss_energy_portion);
         //   bw.write(time_period+" "+total_diss_energy_portion+" "+aver_diss_energy_portion);
         //   bw.newLine();
         //   bw.flush();
         //   bw_1.newLine();
         //   bw_1.flush();
         //   bw_2.newLine();
         //   bw_2.flush();
         //   bw_3.newLine();
         //   bw_3.flush();
            }
          }

          System.out.println("File "+write_file_name+" is created!");
          System.out.println("--------------======================---------------------====================");
          System.out.println();
       //   bw.close();
       //   bw_1.close();
       //   bw_2.close();
       //   bw_3.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates files with data for graphs of time function
     * of average value for certain perpendicular part of specimen.
     * @param min_coord_X minimal value of coordinate X for considered specimen part
     * @param max_coord_X maximal value of coordinate X for considered specimen part
     * @param min_coord_Y minimal value of coordinate Y for considered specimen part
     * @param max_coord_Y maximal value of coordinate Y for considered specimen part
     * @param min_coord_Z minimal value of coordinate Z for considered specimen part
     * @param max_coord_Z maximal value of coordinate Z for considered specimen part
     */
    private void createTimeGraphsForSpecimenPart(double min_coord_X, double max_coord_X, 
                                                 double min_coord_Y, double max_coord_Y, 
                                                 double min_coord_Z, double max_coord_Z)
    {
        String string, token_1;
        packing_type = Common.HEXAGONAL_CLOSE_PACKING;
        
        // Distances between cell rows arranged along axes
        double step_coord_X = 0.0;
        double step_coord_Y = 0.0;
        double step_coord_Z = 0.0;
        
        if(min_coord_X == max_coord_X) step_coord_X = Math.sqrt(0.75);    // = 0.8660
        if(min_coord_Y == max_coord_Y) step_coord_Y = 1.0;
        if(min_coord_Z == max_coord_Z) step_coord_Z = Math.sqrt(2.0/3.0); // = 0.8165;
            
        // Each string contains parameters of corresponding cell:
        // 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates;
        // 6-8. 3 absolute values of components of instant specific force moment;
        // 9-11. 3 components of instant specific force moment;
        // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
        // 13. absolute value of instant specific force moment;
        // 14. current influx of torsion energy.
        byte energy_type;
        byte location_type;
        int grain_index;
        
        double coordinate_X = 0;
        double coordinate_Y = 0;
        double coordinate_Z = 0;
        
        double temperature  = 0;
        double eff_stress   = 0;
        double prn_stress   = 0;        
        double mom_X        = 0;
        double mom_Y        = 0;
        double mom_Z        = 0;        
        byte   state        = 0;        
        double strain       = 0;
        
        double abs_inst_mom_X  = 0;
        double abs_inst_mom_Y  = 0;
        double abs_inst_mom_Z  = 0;        
        double inst_mom_X      = 0;
        double inst_mom_Y      = 0;
        double inst_mom_Z      = 0;        
        double abs_inst_mom    = 0;
        double rel_tors_energy = 0;
        
        double log10_eff_stress   = 0;
        double log10_abs_inst_mom = 0;
        
        double angle_vel_X, angle_vel_Y, angle_vel_Z;
        double tors_angle_X, tors_angle_Y, tors_angle_Z;
        double rel_tors_en_change;
        double abs_tors_angle               = 0;
        double abs_angle_velocity           = 0;
        double aver_abs_angle_velocity      = 0;
        double aver_rel_tors_en_change      = 0;
        
        double aver_abs_angle_vel_X  = 0;
        double aver_abs_angle_vel_Y  = 0;
        double aver_abs_angle_vel_Z  = 0;
        double aver_abs_tors_angle_X = 0;
        double aver_abs_tors_angle_Y = 0;
        double aver_abs_tors_angle_Z = 0;
        
        // Period of recording of data in files
        int record_period     = 500;
        
        max_step_number       = 100000; // file_number*record_period;// 
        
        // Number of files
        int file_number       = max_step_number/record_period;
        
        int factor = 1; // 5; // 
        
        // Index of current file
        int file_index        = 0;
        
        // Time step of task (in seconds)
        double time_step      = 1.0E-8;
        
        // Diameter of cell (in meters)
        double cell_size      = 2.5E-6;
        
        cell_volume = calcCellVolume(cell_size);
        
        double diss_energy               = 0;
        double diss_en_velocity          = 0;
        
        double aver_diss_energy          = 0;
        double aver_diss_en_velocity     = 0;
        double aver_mech_en_velocity     = 0;
        double total_diss_energy_portion = 0;
        
        double aver_abs_strain = 0;
        double aver_abs_mom_X  = 0;
        double aver_abs_mom_Y  = 0;
        double aver_abs_mom_Z  = 0;
        double aver_abs_mom    = 0;
        
        // Average values
        double aver_abs_inst_mom_X     = 0;
        double aver_abs_inst_mom_Y     = 0;
        double aver_abs_inst_mom_Z     = 0;
        
        double total_inst_mom_X        = 0;
        double total_inst_mom_Y        = 0;
        double total_inst_mom_Z        = 0;
        
        double aver_abs_inst_mom       = 0;
        double aver_stress             = 0;
        double aver_temperature        = 0;
        
        double aver_log10_stress       = 0;
        double aver_log10_abs_inst_mom = 0;
        double aver_rel_tors_energy    = 0;
        double aver_abs_tors_angle     = 0;
        
        ArrayList diss_en_velocities   = new ArrayList(0);
        ArrayList rel_tors_en_changes  = new ArrayList(0);
        ArrayList mech_en_velocities   = new ArrayList(0);
        
        int cell_number = 0;
        
        boolean calc_averages = true; // false; // 
        
        double cur_time = 0;
        
        try
        {            
          String write_file_name_1 = "";
          String write_file_name_2 = "";
          String write_file_name_3 = "";
          String write_file_name_4 = "";
          String write_file_name_5 = "";
          String write_file_name_6 = "";
          String write_file_name_7 = "";
          String write_file_name_8 = "";
          String write_file_name_9 = "";
          String write_file_name_A = "";
          String write_file_name_B = "";
          String write_file_name_C = "";
          String write_file_name_D = "";
          String write_file_name_E = "";
          String write_file_name_F = "";
          String write_file_name_G = "";
          String write_file_name_H = "";
          String write_file_name_I = "";
          String write_file_name_J = "";
          String write_file_name_K = "";
          String write_file_name_L = "";
          String write_file_name_M = "";
          String write_file_name_N = "";
          String write_file_name_O = "";
          String write_file_name_P = "";
          String write_file_name_R = "";
          String write_file_name_Q = "";
          
          String write_file_name_region = "";
          
      //    write_file_name = "./user/task_db/"+task_name+"/graphs/"+task_name+"_Z="+section_coord_Z+"_eff_stresses.txt";
      //    write_file_name_2 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_Z="+section_coord_Z+"_strains.txt";
      //    write_file_name_3 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_Z="+section_coord_Z+"_strains&eff_stresses.txt";
      //    write_file_name = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_eff_stresses.txt";
      //    String write_file_name_2 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_strains.txt";
      //    String write_file_name_3 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_strains&eff_stresses.txt";
          
      //    <...>_grain_bodies_<...>     - files for recording of average values for cells located inside grains
      //    <...>_grain_boundaries_<...> - files for recording of average values for cells located at grain boundaries
            
      //    write_file_name_1 = "./user/task_db/"+task_name+"/new_res_2014_09_15/time_graphs/"+task_name+"_Z="+section_coord_Z+"_grain_boundaries_aver_abs_inst_mom_X.txt";
      //    write_file_name_1 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_grain_boundaries_aver_abs_inst_mom_X.txt";
      //    write_file_name_2 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_grain_boundaries_aver_abs_inst_mom_Y.txt";
      //    write_file_name_3 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_grain_boundaries_aver_abs_inst_mom_Z.txt";
      //    write_file_name_4 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_grain_boundaries_aver_log10_abs_inst_mom.txt";
      //    write_file_name_5 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_grain_boundaries_aver_log10_eff_stresses.txt";
          
          if(min_coord_X == max_coord_X)
          {
           /*
              write_file_name_3 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_strains&eff_stresses.txt";
              write_file_name_7 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_spec_diss_en_velocities.txt";
              write_file_name_8 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_spec_diss_energies.txt";
              
              
              write_file_name_B = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_total_diss_en_velocity_portions.txt";
              write_file_name_D = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_spec_abs_angle_velocities_gr_interior.txt";
              write_file_name_E = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_spec_abs_angle_velocities_grain_bound.txt";
           */
              write_file_name_1 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_inst_mom_X.txt";
              write_file_name_2 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_inst_mom_Y.txt";
              write_file_name_3 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_inst_mom_Z.txt";
              write_file_name_4 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_inst_mom.txt";
              write_file_name_5 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_spec_elast_energy.txt";
              write_file_name_6 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_diss_en_portion.txt";
              write_file_name_7 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_abs_torsion_angle.txt";
              write_file_name_8 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_spec_diss_energy.txt";
              write_file_name_9 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_abs_angle_velocity.txt";
              write_file_name_A = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+min_coord_Z+"_aver_tors_en_portions.txt";
              write_file_name_C = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_total_diss_energy_portion.txt";
              write_file_name_F = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_temperature.txt";
              write_file_name_G = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_spec_total_energy.txt";
              write_file_name_H = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_angle_velocity_X.txt";
              write_file_name_I = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_angle_velocity_Y.txt";
              write_file_name_J = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_angle_velocity_Z.txt";
              write_file_name_K = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_torsion_angle_X.txt";
              write_file_name_L = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_torsion_angle_Y.txt";
              write_file_name_M = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_torsion_angle_Z.txt";
              write_file_name_N = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_mom_X.txt";
              write_file_name_O = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_mom_Y.txt";
              write_file_name_P = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_mom_Z.txt";
              write_file_name_R = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_mom.txt";
              write_file_name_Q = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_X="+min_coord_X+"_aver_abs_strain.txt";
          }
          else
          if(min_coord_X <= COORD_MIN_VALUE & max_coord_X >= COORD_MAX_VALUE &
             min_coord_Y <= COORD_MIN_VALUE & max_coord_Y >= COORD_MAX_VALUE &
             min_coord_Z <= COORD_MIN_VALUE & max_coord_Z >= COORD_MAX_VALUE)
          {
        //    write_file_name_4 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_all_specimen_aver_log10_abs_inst_mom.txt";
        //    write_file_name_5 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_all_specimen_aver_log10_eff_stresses.txt";
        //    write_file_name_5 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_all_specimen_aver_eff_stresses.txt";
         //   write_file_name_4 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerCells_aver_abs_inst_moments.txt";
         //   write_file_name_5 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerCells_aver_effect_stresses.txt";
         //   write_file_name_6 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerCells_aver_diss_en_portions.txt";
              
         /*   write_file_name_3 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_strains&eff_stresses.txt";              
              write_file_name_5 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_eff_stresses.txt";
              
              write_file_name_7 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_spec_diss_en_velocities.txt";
              write_file_name_8 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_spec_diss_energies.txt";
              
              
              write_file_name_B = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_total_diss_en_velocity_portions.txt";
              write_file_name_D = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_spec_abs_angle_velocities_gr_interior.txt";
              write_file_name_E = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_spec_abs_angle_velocities_grain_bound.txt";
          */    
         //     write_file_name_1 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_inst_mom_X.txt";
           //   write_file_name_2 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_inst_mom_Y.txt";
             // write_file_name_3 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_inst_mom_Z.txt";
              
            /*  
              write_file_name_1 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_inst_mom_X.txt";
              write_file_name_2 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_inst_mom_Y.txt";
              write_file_name_3 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_inst_mom_Z.txt";
              write_file_name_4 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_inst_mom.txt";
              write_file_name_5 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_spec_elast_energy.txt";
              write_file_name_6 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_diss_en_portion.txt";
              write_file_name_7 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_torsion_angle.txt";
              write_file_name_8 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_spec_diss_energy.txt";
              write_file_name_9 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_angle_velocity.txt";
              write_file_name_A = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_tors_en_portions.txt";
              write_file_name_C = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_total_diss_energy_portion.txt";
              write_file_name_F = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_temperature.txt";
              write_file_name_G = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_spec_total_energy.txt";
              write_file_name_H = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_angle_velocity_X.txt";
              write_file_name_I = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_angle_velocity_Y.txt";
              write_file_name_J = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_angle_velocity_Z.txt";
              write_file_name_K = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_torsion_angle_X.txt";
              write_file_name_L = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_torsion_angle_Y.txt";
              write_file_name_M = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_torsion_angle_Z.txt";
              write_file_name_N = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_mom_X.txt";
              write_file_name_O = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_mom_Y.txt";
              write_file_name_P = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_mom_Z.txt";
              write_file_name_R = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_mom.txt";
              write_file_name_Q = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_innerSpec_aver_abs_strain.txt";
            */  
              
              write_file_name_1 = "./user/task_db/time_graphs/aver_inst_mom_X/"+task_name+"_innerSpec_aver_inst_mom_X.txt";
              write_file_name_2 = "./user/task_db/time_graphs/aver_inst_mom_Y/"+task_name+"_innerSpec_aver_inst_mom_Y.txt";
              write_file_name_3 = "./user/task_db/time_graphs/aver_inst_mom_Z/"+task_name+"_innerSpec_aver_inst_mom_Z.txt";
              write_file_name_4 = "./user/task_db/time_graphs/aver_abs_inst_mom/"+task_name+"_innerSpec_aver_abs_inst_mom.txt";
              write_file_name_5 = "./user/task_db/time_graphs/aver_spec_elast_energy/"+task_name+"_innerSpec_aver_spec_elast_energy.txt";
              write_file_name_6 = "./user/task_db/time_graphs/aver_diss_en_portion/"+task_name+"_innerSpec_aver_diss_en_portion.txt";
              write_file_name_7 = "./user/task_db/time_graphs/aver_abs_torsion_angle/"+task_name+"_innerSpec_aver_abs_torsion_angle.txt";
              write_file_name_8 = "./user/task_db/time_graphs/aver_spec_diss_energy/"+task_name+"_innerSpec_aver_spec_diss_energy.txt";
              write_file_name_9 = "./user/task_db/time_graphs/aver_abs_angle_velocity/"+task_name+"_innerSpec_aver_abs_angle_velocity.txt";
              write_file_name_A = "./user/task_db/time_graphs/aver_tors_en_portions/"+task_name+"_innerSpec_aver_tors_en_portions.txt";
              write_file_name_C = "./user/task_db/time_graphs/total_diss_energy_portion/"+task_name+"_innerSpec_total_diss_energy_portion.txt";
              write_file_name_F = "./user/task_db/time_graphs/aver_temperature/"+task_name+"_innerSpec_aver_temperature.txt";
              write_file_name_G = "./user/task_db/time_graphs/aver_spec_total_energy/"+task_name+"_innerSpec_aver_spec_total_energy.txt";
              write_file_name_H = "./user/task_db/time_graphs/aver_abs_angle_velocity_X/"+task_name+"_innerSpec_aver_abs_angle_velocity_X.txt";
              write_file_name_I = "./user/task_db/time_graphs/aver_abs_angle_velocity_Y/"+task_name+"_innerSpec_aver_abs_angle_velocity_Y.txt";
              write_file_name_J = "./user/task_db/time_graphs/aver_abs_angle_velocity_Z/"+task_name+"_innerSpec_aver_abs_angle_velocity_Z.txt";
              write_file_name_K = "./user/task_db/time_graphs/aver_abs_torsion_angle_X/"+task_name+"_innerSpec_aver_abs_torsion_angle_X.txt";
              write_file_name_L = "./user/task_db/time_graphs/aver_abs_torsion_angle_Y/"+task_name+"_innerSpec_aver_abs_torsion_angle_Y.txt";
              write_file_name_M = "./user/task_db/time_graphs/aver_abs_torsion_angle_Z/"+task_name+"_innerSpec_aver_abs_torsion_angle_Z.txt";
              write_file_name_N = "./user/task_db/time_graphs/aver_abs_mom_X/"+task_name+"_innerSpec_aver_abs_mom_X.txt";
              write_file_name_O = "./user/task_db/time_graphs/aver_abs_mom_Y/"+task_name+"_innerSpec_aver_abs_mom_Y.txt";
              write_file_name_P = "./user/task_db/time_graphs/aver_abs_mom_Z/"+task_name+"_innerSpec_aver_abs_mom_Z.txt";
              write_file_name_R = "./user/task_db/time_graphs/aver_abs_mom/"+task_name+"_innerSpec_aver_abs_mom.txt";
              write_file_name_Q = "./user/task_db/time_graphs/aver_abs_strain/"+task_name+"_innerSpec_aver_abs_strain.txt";
          }
          else
          {
              write_file_name_1 = "./user/task_db/time_graphs/aver_inst_mom_X/"+task_name+"_region_aver_inst_mom_X.txt";
              write_file_name_2 = "./user/task_db/time_graphs/aver_inst_mom_Y/"+task_name+"_region_aver_inst_mom_Y.txt";
              write_file_name_3 = "./user/task_db/time_graphs/aver_inst_mom_Z/"+task_name+"_region_aver_inst_mom_Z.txt";
              write_file_name_4 = "./user/task_db/time_graphs/aver_abs_inst_mom/"+task_name+"_region_aver_abs_inst_mom.txt";
              write_file_name_5 = "./user/task_db/time_graphs/aver_spec_elast_energy/"+task_name+"_region_aver_spec_elast_energy.txt";
              write_file_name_6 = "./user/task_db/time_graphs/aver_diss_en_portion/"+task_name+"_region_aver_diss_en_portion.txt";
              write_file_name_7 = "./user/task_db/time_graphs/aver_abs_torsion_angle/"+task_name+"_region_aver_abs_torsion_angle.txt";
              write_file_name_8 = "./user/task_db/time_graphs/aver_spec_diss_energy/"+task_name+"_region_aver_spec_diss_energy.txt";
              write_file_name_9 = "./user/task_db/time_graphs/aver_abs_angle_velocity/"+task_name+"_region_aver_abs_angle_velocity.txt";
              write_file_name_A = "./user/task_db/time_graphs/aver_tors_en_portions/"+task_name+"_region_aver_tors_en_portions.txt";
              write_file_name_C = "./user/task_db/time_graphs/total_diss_energy_portion/"+task_name+"_region_total_diss_energy_portion.txt";
              write_file_name_F = "./user/task_db/time_graphs/aver_temperature/"+task_name+"_region_aver_temperature.txt";
              write_file_name_G = "./user/task_db/time_graphs/aver_spec_total_energy/"+task_name+"_region_aver_spec_total_energy.txt";
              write_file_name_H = "./user/task_db/time_graphs/aver_abs_angle_velocity_X/"+task_name+"_region_aver_abs_angle_velocity_X.txt";
              write_file_name_I = "./user/task_db/time_graphs/aver_abs_angle_velocity_Y/"+task_name+"_region_aver_abs_angle_velocity_Y.txt";
              write_file_name_J = "./user/task_db/time_graphs/aver_abs_angle_velocity_Z/"+task_name+"_region_aver_abs_angle_velocity_Z.txt";
              write_file_name_K = "./user/task_db/time_graphs/aver_abs_torsion_angle_X/"+task_name+"_region_aver_abs_torsion_angle_X.txt";
              write_file_name_L = "./user/task_db/time_graphs/aver_abs_torsion_angle_Y/"+task_name+"_region_aver_abs_torsion_angle_Y.txt";
              write_file_name_M = "./user/task_db/time_graphs/aver_abs_torsion_angle_Z/"+task_name+"_region_aver_abs_torsion_angle_Z.txt";
              write_file_name_N = "./user/task_db/time_graphs/aver_abs_mom_X/"+task_name+"_region_aver_abs_mom_X.txt";
              write_file_name_O = "./user/task_db/time_graphs/aver_abs_mom_Y/"+task_name+"_region_aver_abs_mom_Y.txt";
              write_file_name_P = "./user/task_db/time_graphs/aver_abs_mom_Z/"+task_name+"_region_aver_abs_mom_Z.txt";
              write_file_name_R = "./user/task_db/time_graphs/aver_abs_mom/"+task_name+"_region_aver_abs_mom.txt";
              write_file_name_Q = "./user/task_db/time_graphs/aver_abs_strain/"+task_name+"_region_aver_abs_strain.txt";
          }
          
          // Creation of new folders
          File folder = new File("./user/task_db/"+task_name+"/time_graphs");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_inst_mom_X");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_inst_mom_Y");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_inst_mom_Z");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_inst_mom");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_spec_elast_energy");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_diss_en_portion");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_torsion_angle");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_spec_diss_energy");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_angle_velocity");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_tors_en_portions");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/total_diss_energy_portion");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_temperature");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_spec_total_energy");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_angle_velocity_X");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_angle_velocity_Y");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_angle_velocity_Z");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_torsion_angle_X");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_torsion_angle_Y");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_torsion_angle_Z");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_mom_X");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_mom_Y");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_mom_Z");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_mom");
          if(!folder.exists()) folder.mkdir();
          
          folder = new File("./user/task_db/time_graphs/aver_abs_strain");
          if(!folder.exists()) folder.mkdir();
          
          BufferedWriter bw1 = new BufferedWriter(new FileWriter(write_file_name_1));
          BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
          BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
       
          BufferedWriter bw7 = new BufferedWriter(new FileWriter(write_file_name_7));
          
          BufferedWriter bwA = new BufferedWriter(new FileWriter(write_file_name_A));
        /*
          BufferedWriter bwB = new BufferedWriter(new FileWriter(write_file_name_B));
          BufferedWriter bwD = new BufferedWriter(new FileWriter(write_file_name_D));
          BufferedWriter bwE = new BufferedWriter(new FileWriter(write_file_name_E));
        */
          BufferedWriter bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
          BufferedWriter bw5 = new BufferedWriter(new FileWriter(write_file_name_5));
          BufferedWriter bw6 = new BufferedWriter(new FileWriter(write_file_name_6));
          BufferedWriter bw8 = new BufferedWriter(new FileWriter(write_file_name_8));
          BufferedWriter bw9 = new BufferedWriter(new FileWriter(write_file_name_9));
          BufferedWriter bwC = new BufferedWriter(new FileWriter(write_file_name_C));
          BufferedWriter bwF = new BufferedWriter(new FileWriter(write_file_name_F));
          BufferedWriter bwG = new BufferedWriter(new FileWriter(write_file_name_G));
          BufferedWriter bwH = new BufferedWriter(new FileWriter(write_file_name_H));
          BufferedWriter bwI = new BufferedWriter(new FileWriter(write_file_name_I));
          BufferedWriter bwJ = new BufferedWriter(new FileWriter(write_file_name_J));
          BufferedWriter bwK = new BufferedWriter(new FileWriter(write_file_name_K));
          BufferedWriter bwL = new BufferedWriter(new FileWriter(write_file_name_L));
          BufferedWriter bwM = new BufferedWriter(new FileWriter(write_file_name_M));
          BufferedWriter bwN = new BufferedWriter(new FileWriter(write_file_name_N));
          BufferedWriter bwO = new BufferedWriter(new FileWriter(write_file_name_O));
          BufferedWriter bwP = new BufferedWriter(new FileWriter(write_file_name_P));
          BufferedWriter bwR = new BufferedWriter(new FileWriter(write_file_name_R));
          BufferedWriter bwQ = new BufferedWriter(new FileWriter(write_file_name_Q));
          
          // Writing of information about region geometry
          write_file_name_region = "./user/task_db/"+task_name+"/time_graphs/region_coordinates.txt";
          
          BufferedWriter bw_region = new BufferedWriter(new FileWriter(write_file_name_region));
          
          bw_region.write("Coordinates of region for graph:\n");
          bw_region.write("min_coord_X = "+min_coord_X+"\n");
          bw_region.write("max_coord_X = "+max_coord_X+"\n");
          bw_region.write("min_coord_Y = "+min_coord_Y+"\n");
          bw_region.write("max_coord_Y = "+max_coord_Y+"\n");
          bw_region.write("min_coord_Z = "+min_coord_Z+"\n");
          bw_region.write("max_coord_Z = "+max_coord_Z+"\n");
          
          bw_region.flush();
          bw_region.close();
          
          boolean inst_mom_file = true; // false;// 
          boolean torsion_file  = true; // false;// 
          boolean res_file      = true; // false;// 
       /*   
          if(inst_mom_file)
           //   read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+max_step_number+"_inst_mom.res";
              read_file_name  = "./user/task_db/"+task_name+"/"+task_name+".seca";
          
          if(torsion_file)
           //   read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+max_step_number+"_torsion.res";
              read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_0"+1000+"_torsion.res";
          
          if(res_file)
              read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_0"+1000+".res";
       */
          
          read_file_name  = "./user/task_db/"+task_name+"/"+task_name+".seca";
          
          BufferedReader br = new BufferedReader(new FileReader(read_file_name));
          
          for(int file_counter = 0; file_counter <= file_number; file_counter++)
          {
            aver_temperature    = 0;
            aver_stress         = 0;            
            aver_abs_inst_mom_X = 0;
            aver_abs_inst_mom_Y = 0;
            aver_abs_inst_mom_Z = 0;
            
            aver_abs_mom_X  = 0;
            aver_abs_mom_Y  = 0;
            aver_abs_mom_Z  = 0;
            aver_abs_mom    = 0;
            aver_abs_strain = 0;
            
            total_inst_mom_X    = 0;
            total_inst_mom_Y    = 0;
            total_inst_mom_Z    = 0;
                    
            aver_abs_inst_mom   = 0;
            cell_number         = 0;
            
            aver_log10_stress       = 0;
            aver_log10_abs_inst_mom = 0;
            aver_rel_tors_energy    = 0;
            aver_diss_energy        = 0;
            aver_diss_en_velocity   = 0;
            
            aver_mech_en_velocity        = 0;
            total_diss_energy_portion    = 0;
            aver_rel_tors_en_change      = 0;
            
            aver_abs_tors_angle          = 0;
            aver_abs_angle_velocity      = 0;
            
            aver_abs_angle_vel_X  = 0;
            aver_abs_angle_vel_Y  = 0;
            aver_abs_angle_vel_Z  = 0;
            aver_abs_tors_angle_X = 0;
            aver_abs_tors_angle_Y = 0;
            aver_abs_tors_angle_Z = 0;
            
            diss_en_velocities   = new ArrayList(0);
            rel_tors_en_changes  = new ArrayList(0);
            mech_en_velocities   = new ArrayList(0);
            
            // Exponent of power of 10 in standard representation
            // of total number of time steps
            int max_exponent = (int)Math.floor(Math.log10(max_step_number));

            // Exponent of power of 10 in standard representation
            // of number of current time step
            int exponent = 0;
            
            file_index = file_counter*record_period;

            if(file_index > 0)
                exponent     = (int)Math.floor(Math.log10(file_index));
            
            // String of zeros
            String zeros = "";
            
            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";
            
            calc_averages = false;
            
            if(res_file)
            if((file_index <= 1000 & file_index%record_period == 0)| file_index%(factor*record_period) == 0)
            {
                calc_averages = true;
                
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_" + zeros + file_index + ".res";
                br = new BufferedReader(new FileReader(read_file_name));
            }
            
            if(calc_averages & res_file)
            while(br.ready())
            {
                string = br.readLine();
                
                StringTokenizer st = new StringTokenizer(string);
                
                token_1 = st.nextToken();
                
                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = new Byte(token_1).byteValue();
                    location_type  = new Byte(st.nextToken()).byteValue();
                    grain_index    = new Integer(st.nextToken()).intValue();
                    
                //  if(location_type == Common.INTRAGRANULAR_CELL | location_type == Common.INNER_BOUNDARY_CELL)
                //  if(location_type == Common.INTERGRANULAR_CELL | location_type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
                //  if(location_type == (byte)3 | location_type == (byte)5)
                //  if(location_type == (byte)4 | location_type == (byte)6)
                    if(energy_type   == Common.INNER_CELL | energy_type   == Common.LAYER_CELL)
                //    if(location_type != Common.INNER_BOUNDARY_CELL & location_type != Common.INNER_BOUNDARY_INTERGRANULAR_CELL &
                //       location_type != Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL)
                    {
                        coordinate_X   = new Double(st.nextToken()).doubleValue();
                        coordinate_Y   = new Double(st.nextToken()).doubleValue();
                        coordinate_Z   = new Double(st.nextToken()).doubleValue();

                     //   if((coordinate_X-0.5<section_coord_X)&(coordinate_X+0.5>section_coord_X))
                     //   if((coordinate_Y-0.5<section_coord_Y)&(coordinate_Y+0.5>section_coord_Y))
                        if(coordinate_X >= min_coord_X - step_coord_X/2 & coordinate_X < max_coord_X + step_coord_X/2)
                        if(coordinate_Y >= min_coord_Y - step_coord_Y/2 & coordinate_Y < max_coord_Y + step_coord_Y/2)
                        if(coordinate_Z >= min_coord_Z - step_coord_Z/2 & coordinate_Z < max_coord_Z + step_coord_Z/2)
                        {
                          //  System.out.println("location_type = "+location_type);
                          //  System.out.println("energy_type   = "+energy_type);
                            
                            temperature = new Double(st.nextToken()).doubleValue();
                            eff_stress  = new Double(st.nextToken()).doubleValue();
                            prn_stress  = new Double(st.nextToken()).doubleValue();
                            
                            mom_X       = new Double(st.nextToken()).doubleValue();
                            mom_Y       = new Double(st.nextToken()).doubleValue();
                            mom_Z       = new Double(st.nextToken()).doubleValue();
                            
                            state       = new Byte(st.nextToken()).byteValue();
                            strain      = new Double(st.nextToken()).doubleValue();
                            diss_energy = new Double(st.nextToken()).doubleValue();
                            
                            if(diss_energy < 0)
                                System.out.println("ERROR!!! Diss. energy must be >= 0 !!!");
                         /* 
                            if(eff_stress != 0) log10_eff_stress = 29 + Math.log10(Math.abs(eff_stress));
                            else                log10_eff_stress = 0;
                            
                            if(log10_eff_stress < 0)
                                System.out.println("Choose another value added to logarithm !!! log10_eff_stress = "+log10_eff_stress);
                            else
                                log10_eff_stress = Math.signum(eff_stress)*log10_eff_stress;
                          */
                            
                            // Calculation of average values
                            aver_temperature  += temperature;
                            aver_stress       += eff_stress;
                            aver_log10_stress += log10_eff_stress;
                            aver_diss_energy  += diss_energy;
                            
                            aver_abs_strain += Math.abs(strain);
                            
                            aver_abs_mom_X  += Math.abs(mom_X);
                            aver_abs_mom_Y  += Math.abs(mom_Y);
                            aver_abs_mom_Z  += Math.abs(mom_Z);
                            
                            aver_abs_mom    += Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                            
                            cell_number++;
                        }
                    }
                    
                    if(energy_type   == Common.ADIABATIC_TEMPERATURE_CELL & location_type == Common.OUTER_CELL)
                    {
                        coordinate_X   = new Double(st.nextToken()).doubleValue();
                        coordinate_Y   = new Double(st.nextToken()).doubleValue();
                        coordinate_Z   = new Double(st.nextToken()).doubleValue();
                        
                        temperature = new Double(st.nextToken()).doubleValue();
                        
                        // Loading
                        if(((file_index-1)/10000) % 2 == 0 | file_index == 0)
                        {
                       //     heat_influx = heat_cap*mass*(1700 - temperature);
                        }
                        
                        // Relaxation
                        if(((file_index-1)/10000) % 2 == 1)
                        {
                      //      heat_influx = heat_cap*mass*(1000 - temperature);
                        }
                    }
                }
            }
            
            if(calc_averages)
            {
                System.out.println("-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -");
                System.out.println("File "+read_file_name+" is read!");
              //  System.out.print("Number of considered cells: "+cell_number);
                
                // Average value of temperature
                aver_temperature = aver_temperature/cell_number;
                
                // Average value of effective stress
                aver_stress      = aver_stress/cell_number;
                aver_stress      = -aver_stress;
             // aver_stress      = Math.abs(aver_stress);
                
              //  aver_diss_energy = aver_diss_energy/(cell_size*cell_size);
                aver_diss_energy = aver_diss_energy/cell_number;
             //   aver_diss_energy = aver_diss_energy/cell_volume;
                
                if(aver_diss_energy + Math.abs(aver_stress) != 0)
                    total_diss_energy_portion = aver_diss_energy/(aver_diss_energy + Math.abs(aver_stress));
                else
                    if(aver_diss_energy == 0)
                        total_diss_energy_portion = 0;
                
                aver_abs_strain = aver_abs_strain/cell_number;
                aver_abs_mom_X  = aver_abs_mom_X/cell_number;
                aver_abs_mom_Y  = aver_abs_mom_Y/cell_number;
                aver_abs_mom_Z  = aver_abs_mom_Z/cell_number;
                aver_abs_mom    = aver_abs_mom  /cell_number;
                
                // Time in milliseconds
             //   cur_time = (int)Math.round(file_index*time_step*1.0E9)/1.0E6;
                
                // Time in microseconds
                cur_time = (int)Math.round(file_index*time_step*1.0E9)/1.0E3;
             
                // Time in nanoseconds
            //    cur_time = (int)Math.round(file_index*time_step*1.0E15)/1.0E6;
                
                aver_temperature = (long)Math.round(aver_temperature*1.0E3)/1.0E3;
                        
                // Average effective stress in GPa
                if(aver_stress < Long.MAX_VALUE*1.0E-9)
                {
                    aver_stress = (long)Math.round(aver_stress*1.0E9)/1.0E18;
                }
                else
                {
                    System.out.println("aver_stress = "+aver_stress+"; Long.MAX_VALUE = "+Long.MAX_VALUE);
                    aver_stress = (long)Math.round(aver_stress*1.0E6)/1.0E15;
                }
                
                // Average latent energy in GPa
                if(aver_diss_energy < Long.MAX_VALUE*1.0E-9)
                {
                    aver_diss_energy = (long)Math.round(aver_diss_energy*1.0E9)/1.0E18;
                }
                else
                {
                    System.out.println("aver_diss_energy = "+aver_diss_energy+"; Long.MAX_VALUE = "+Long.MAX_VALUE);
                    aver_diss_energy = (long)Math.round(aver_diss_energy*1.0E6)/1.0E15;
                }
                
                // Average absolute value of component X of specific force moment in MPa
                if(aver_abs_mom_X < Long.MAX_VALUE*1.0E-9)
                {
                    aver_abs_mom_X = (long)Math.round(aver_abs_mom_X*1.0E9)/1.0E15;
                }
                else
                {
                    System.out.println("aver_abs_mom_X = "+aver_abs_mom_X+"; Long.MAX_VALUE = "+Long.MAX_VALUE);
                    aver_abs_mom_X = (long)Math.round(aver_abs_mom_X*1.0E6)/1.0E12;
                }
                
                // Average absolute value of component Y of specific force moment in MPa
                if(aver_abs_mom_Y < Long.MAX_VALUE*1.0E-9)
                {
                    aver_abs_mom_Y = (long)Math.round(aver_abs_mom_Y*1.0E9)/1.0E15;
                }
                else
                {
                    System.out.println("aver_abs_mom_Y = "+aver_abs_mom_Y+"; Long.MAX_VALUE = "+Long.MAX_VALUE);
                    aver_abs_mom_Y = (long)Math.round(aver_abs_mom_Y*1.0E6)/1.0E12;
                }
                
                // Average absolute value of component Z of specific force moment in MPa
                if(aver_abs_mom_Z < Long.MAX_VALUE*1.0E-9)
                {
                    aver_abs_mom_Z = (long)Math.round(aver_abs_mom_Z*1.0E9)/1.0E15;
                }
                else
                {
                    System.out.println("aver_abs_mom_Z = "+aver_abs_mom_Z+"; Long.MAX_VALUE = "+Long.MAX_VALUE);
                    aver_abs_mom_Z = (long)Math.round(aver_abs_mom_Z*1.0E6)/1.0E12;
                }
                
                // Average absolute value of specific force moment in MPa
                if(aver_abs_mom < Long.MAX_VALUE*1.0E-9)
                {
                    aver_abs_mom = (long)Math.round(aver_abs_mom*1.0E9)/1.0E15;
                }
                else
                {
                    System.out.println("aver_abs_mom = "+aver_abs_mom+"; Long.MAX_VALUE = "+Long.MAX_VALUE);
                    aver_abs_mom = (long)Math.round(aver_abs_mom*1.0E6)/1.0E12;
                }
                
                System.out.println("Time = "+cur_time+" mcs. Number of considered cells: "+cell_number+";\n"+
                                   "min_coord_X = "+min_coord_X+"; min_coord_Y = "+min_coord_Y+"; min_coord_Z = "+min_coord_Z+";\n"+
                                   "max_coord_X = "+max_coord_X+"; max_coord_Y = "+max_coord_Y+"; max_coord_Z = "+max_coord_Z+".\n");
                System.out.println("aver_temperature = "+aver_temperature+" K;   aver_stress = "+aver_stress+" GPa;\n"+
                                   "aver_diss_energy = "+aver_diss_energy+" GPa; total_diss_energy_portion = "+total_diss_energy_portion+";\n"+
                                   "aver_abs_strain  = "+aver_abs_strain+ ";    aver_abs_mom   = "+aver_abs_mom+  " MPa;\n"+
                                   "aver_abs_mom_X   = "+aver_abs_mom_X+  " MPa; aver_abs_mom_Y = "+aver_abs_mom_Y+" MPa; aver_abs_mom_Z = "+aver_abs_mom_Z+" MPa.");
             //   System.out.println("; aver_abs_tors_angle = "+aver_abs_tors_angle);
                
                bw5.write(cur_time+" "+aver_stress);
                bw5.newLine();
                bw5.flush();
                
                bw8.write(cur_time+" "+aver_diss_energy);
                bw8.newLine();
                bw8.flush();
              
                bwC.write(cur_time+" "+total_diss_energy_portion);
                bwC.newLine();
                bwC.flush();
                
                bwF.write(cur_time+" "+aver_temperature);
                bwF.newLine();
                bwF.flush();
                
                bwN.write(cur_time+" "+aver_abs_mom_X);
                bwN.newLine();
                bwN.flush();
                
                bwO.write(cur_time+" "+aver_abs_mom_Y);
                bwO.newLine();
                bwO.flush();
                
                bwP.write(cur_time+" "+aver_abs_mom_Z);
                bwP.newLine();
                bwP.flush();
                
                bwR.write(cur_time+" "+aver_abs_mom);
                bwR.newLine();
                bwR.flush();
                
                bwQ.write(cur_time+" "+aver_abs_strain);
                bwQ.newLine();
                bwQ.flush();
           /*
                aver_log10_stress         = aver_log10_stress/cell_number;
                System.out.println("File "+read_file_name+" is read! "
                        + "\nmin_coord_Z = "+min_coord_Z+"; max_coord_Z = "+max_coord_Z+
                        "; cell_number = "+cell_number+"; aver_log10_stress = "+aver_log10_stress);
                
                bw5.write(file_index*time_step+" "+aver_log10_stress);
                bw5.newLine();            bw5.flush();
            */
                br.close();                
            }
            
            // Calculation of averages for components X, Y, Z and absolute value of couple force
            calc_averages = false;
            
            cell_number = 0;
            
            if(inst_mom_file)
            if((file_index<= 1000 & file_index%record_period == 0)| file_index%(factor*record_period) == 0)
            {
                calc_averages = true;
                
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_" + zeros + file_index + "_inst_mom.res";
                br = new BufferedReader(new FileReader(read_file_name));
            }
            
          //  calc_averages = false;
            
            if(calc_averages & inst_mom_file)
            while(br.ready())
            {
                string = br.readLine();
                
                StringTokenizer st = new StringTokenizer(string);
                
                token_1 = st.nextToken();
                
                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = new Byte(token_1).byteValue();
                    location_type  = new Byte(st.nextToken()).byteValue();                    
                    grain_index    = new Integer(st.nextToken()).intValue();
                    
                //  if(location_type == Common.INTRAGRANULAR_CELL | location_type == Common.INNER_BOUNDARY_CELL)
                //  if(location_type == Common.INTERGRANULAR_CELL | location_type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
                //  if(location_type == (byte)3 | location_type == (byte)5)
                //  if(location_type == (byte)4 | location_type == (byte)6)
                    if(energy_type   == Common.INNER_CELL | energy_type   == Common.LAYER_CELL)
                //  if(location_type != Common.INNER_BOUNDARY_CELL & location_type != Common.INNER_BOUNDARY_INTERGRANULAR_CELL &
                //     location_type != Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL)
                    {
                        coordinate_X   = new Double(st.nextToken()).doubleValue();
                        coordinate_Y   = new Double(st.nextToken()).doubleValue();
                        coordinate_Z   = new Double(st.nextToken()).doubleValue();
                        
                     //   if((coordinate_X-0.5<section_coord_X)&(coordinate_X+0.5>section_coord_X))
                     //   if((coordinate_Y-0.5<section_coord_Y)&(coordinate_Y+0.5>section_coord_Y))
                        if(coordinate_X >= min_coord_X - step_coord_X/2 & coordinate_X < max_coord_X + step_coord_X/2)
                        if(coordinate_Y >= min_coord_Y - step_coord_Y/2 & coordinate_Y < max_coord_Y + step_coord_Y/2)
                        if(coordinate_Z >= min_coord_Z - step_coord_Z/2 & coordinate_Z < max_coord_Z + step_coord_Z/2)
                        {
                        //    System.out.println("coordinate_Z = "+coordinate_Z);
                            
                            abs_inst_mom_X = new Double(st.nextToken()).doubleValue();
                            abs_inst_mom_Y = new Double(st.nextToken()).doubleValue();
                            abs_inst_mom_Z = new Double(st.nextToken()).doubleValue();
                            
                            inst_mom_X     = new Double(st.nextToken()).doubleValue();
                            inst_mom_Y     = new Double(st.nextToken()).doubleValue();
                            inst_mom_Z     = new Double(st.nextToken()).doubleValue();
                            
                            state          = new Byte(st.nextToken()).byteValue();
                            abs_inst_mom    = new Double(st.nextToken()).doubleValue();
                            diss_en_velocity = new Double(st.nextToken()).doubleValue();
                            
                            diss_en_velocities.add(diss_en_velocity);
                            
                          /*  
                            if(abs_inst_mom != 0) log10_abs_inst_mom = 29 + Math.log10(Math.abs(abs_inst_mom));
                            else                  log10_abs_inst_mom = 0;
                            
                            if(log10_abs_inst_mom < 0)
                                System.out.println("Choose another value added to logarithm !!! log10_abs_inst_mom = "+log10_abs_inst_mom);
                            else
                                log10_abs_inst_mom = Math.signum(abs_inst_mom)*log10_abs_inst_mom;
                            */
                            
                            // Calculation of average values
                            aver_abs_inst_mom_X   += abs_inst_mom_X;
                            aver_abs_inst_mom_Y   += abs_inst_mom_Y;
                            aver_abs_inst_mom_Z   += abs_inst_mom_Z;
                            
                            total_inst_mom_X       += inst_mom_X;
                            total_inst_mom_Y       += inst_mom_Y;
                            total_inst_mom_Z       += inst_mom_Z;
                            
                            aver_abs_inst_mom     += abs_inst_mom;
                            aver_diss_en_velocity += diss_en_velocity;
                      //      aver_log10_abs_inst_mom += log10_abs_inst_mom;
                            
                            cell_number++;
                        }
                    }
                }
            }
            
            if(calc_averages)
            {
                aver_abs_inst_mom_X = aver_abs_inst_mom_X/cell_number;
                aver_abs_inst_mom_Y = aver_abs_inst_mom_Y/cell_number;
                aver_abs_inst_mom_Z = aver_abs_inst_mom_Z/cell_number;
                aver_abs_inst_mom   = aver_abs_inst_mom  /cell_number;
                
                total_inst_mom_X = total_inst_mom_X/cell_number;
                total_inst_mom_Y = total_inst_mom_Y/cell_number;
                total_inst_mom_Z = total_inst_mom_Z/cell_number;
                
                // Average absolute instant moment in kPa
                if(aver_abs_inst_mom < Long.MAX_VALUE*1.0E-9)
                {
                    aver_abs_inst_mom   = (long)Math.round(aver_abs_inst_mom  *1.0E9)/1.0E12;
                    aver_abs_inst_mom_X = (long)Math.round(aver_abs_inst_mom_X*1.0E9)/1.0E12;
                    aver_abs_inst_mom_Y = (long)Math.round(aver_abs_inst_mom_Y*1.0E9)/1.0E12;
                    aver_abs_inst_mom_Z = (long)Math.round(aver_abs_inst_mom_Z*1.0E9)/1.0E12;
                }
                else
                {
                    System.out.println("aver_abs_inst_mom = "+aver_abs_inst_mom+"; Long.MAX_VALUE = "+Long.MAX_VALUE);
                    aver_abs_inst_mom   = (long)Math.round(aver_abs_inst_mom  *1.0E6)/1.0E9;
                    aver_abs_inst_mom_X = (long)Math.round(aver_abs_inst_mom_X*1.0E6)/1.0E9;
                    aver_abs_inst_mom_Y = (long)Math.round(aver_abs_inst_mom_Y*1.0E6)/1.0E9;
                    aver_abs_inst_mom_Z = (long)Math.round(aver_abs_inst_mom_Z*1.0E6)/1.0E9;
                }
                
                double total_inst_mom = Math.sqrt(total_inst_mom_X*total_inst_mom_X + 
                                                  total_inst_mom_Y*total_inst_mom_Y + 
                                                  total_inst_mom_Z*total_inst_mom_Z);
                
                if(total_inst_mom < Long.MAX_VALUE*1.0E-6)
                {
                    total_inst_mom_X = (long)Math.round(total_inst_mom_X*1.0E6)/1.0E12;
                    total_inst_mom_Y = (long)Math.round(total_inst_mom_Y*1.0E6)/1.0E12;
                    total_inst_mom_Z = (long)Math.round(total_inst_mom_Z*1.0E6)/1.0E12;
                }
                else
                {
                    total_inst_mom_X = (long)Math.round(total_inst_mom_X*1.0E3)/1.0E9;
                    total_inst_mom_Y = (long)Math.round(total_inst_mom_Y*1.0E3)/1.0E9;
                    total_inst_mom_Z = (long)Math.round(total_inst_mom_Z*1.0E3)/1.0E9;
                }
                
            //    aver_abs_inst_mom_X = aver_abs_inst_mom_X/1000;
            //    aver_abs_inst_mom_Y = aver_abs_inst_mom_Y/1000;
            //    aver_abs_inst_mom_Z = aver_abs_inst_mom_Z/1000;
            //    aver_abs_inst_mom   = aver_abs_inst_mom  /1000;
                
                aver_diss_en_velocity = aver_diss_en_velocity/cell_number;
         //       aver_diss_en_velocity = aver_diss_en_velocity/cell_size;
                aver_diss_en_velocity = aver_diss_en_velocity/(cell_volume*time_step);
                
             //   aver_log10_abs_inst_mom = aver_log10_abs_inst_mom/cell_number;
            
                // Time in milliseconds
            //    cur_time = (int)Math.round(file_index*time_step*1.0E9)/1.0E6;
                
                // Time in microseconds
                cur_time = (int)Math.round(file_index*time_step*1.0E9)/1.0E3;
             
                // Time in nanoseconds
             //   cur_time = (int)Math.round(file_index*time_step*1.0E15)/1.0E6;
                
                // Time in nanoseconds
             //   cur_time = (int)Math.round(file_index*time_step*1.0E12)/1.0E3;
                
             //   System.out.println("Number of considered cells: "+cell_number+"; min_coord_Z = "+min_coord_Z+"; max_coord_Z = "+max_coord_Z+";");
             //   System.out.println("; time = "+cur_time+" mcs; aver_abs_tors_angle = "+aver_abs_tors_angle);
             //   System.out.println("; time = "+cur_time+" mcs; aver_abs_inst_mom = "+aver_abs_inst_mom+" kPa; aver_diss_en_velocity = "+aver_diss_en_velocity+" Pa/s.");
                
                System.out.println("aver_abs_inst_mom_X = "+aver_abs_inst_mom_X+" kPa; aver_abs_inst_mom_Y = "+aver_abs_inst_mom_Y+
                                   " kPa; aver_abs_inst_mom_Z = "+aver_abs_inst_mom_Z+" kPa; aver_abs_inst_mom = "+aver_abs_inst_mom+" kPa.");                
                                                          //+"; log10_aver_abs_inst_mom = "+ (29+Math.log10(aver_abs_inst_mom)));
                br.close();
            
            //    bw1.write(cur_time+" "+aver_abs_inst_mom_X);
                bw1.write(cur_time+" "+total_inst_mom_X);
                bw1.newLine();            bw1.flush();
            
            //    bw2.write(cur_time+" "+aver_abs_inst_mom_Y);
                bw2.write(cur_time+" "+total_inst_mom_Y);
                bw2.newLine();            bw2.flush();
            
            //    bw3.write(cur_time+" "+aver_abs_inst_mom_Z);
                bw3.write(cur_time+" "+total_inst_mom_Z);
                bw3.newLine();            bw3.flush();
            
                bw4.write(cur_time+" "+aver_abs_inst_mom);
                bw4.newLine();            bw4.flush();
            /*    
                bw7.write(cur_time+" "+aver_diss_en_velocity);
                bw7.newLine();            bw7.flush();
                */
                
         //       bw4.write(file_index*time_step+" "+aver_log10_abs_inst_mom);
           //     bw4.newLine();            bw4.flush();                
            }
            
            boolean read_torsion_file = true;// false;// 
            
            // Calculation of averages for components X, Y, Z and absolute value of couple force
            calc_averages = false;
            
            cell_number = 0;
            
            int cell_number_gr_interior = 0;
            int cell_number_grain_bound = 0;
                            
            double aver_spec_abs_angle_velocity_gr_interior = 0;
            double aver_spec_abs_angle_velocity_grain_bound = 0;
            double spec_total_energy = 0;
            double aver_spec_total_energy = 0;
            
            if(read_torsion_file)
            if((file_index<= 1000 & file_index%record_period == 0)| file_index%(factor*record_period) == 0)
            {
                calc_averages = true;
                
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_" + zeros + file_index + "_torsion.res";
                br = new BufferedReader(new FileReader(read_file_name));
            }
            
          //  calc_averages = false;
            
            if(calc_averages & read_torsion_file)
            while(br.ready())
            {
                string = br.readLine();
                
                StringTokenizer st = new StringTokenizer(string);
                
                token_1 = st.nextToken();
                
                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = new Byte(token_1).byteValue();
                    location_type  = new Byte(st.nextToken()).byteValue();                    
                    grain_index    = new Integer(st.nextToken()).intValue();
                    
                //  if(location_type == Common.INTRAGRANULAR_CELL | location_type == Common.INNER_BOUNDARY_CELL)
                //  if(location_type == Common.INTERGRANULAR_CELL | location_type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
                //  if(location_type == (byte)3 | location_type == (byte)5)
                //  if(location_type == (byte)4 | location_type == (byte)6)
                    if(energy_type   == Common.INNER_CELL | energy_type   == Common.LAYER_CELL)
                //  if(location_type != Common.INNER_BOUNDARY_CELL & location_type != Common.INNER_BOUNDARY_INTERGRANULAR_CELL &
                //     location_type != Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL)
                    {
                        coordinate_X   = new Double(st.nextToken()).doubleValue();
                        coordinate_Y   = new Double(st.nextToken()).doubleValue();
                        coordinate_Z   = new Double(st.nextToken()).doubleValue();
                        
                     //   if((coordinate_X-0.5<section_coord_X)&(coordinate_X+0.5>section_coord_X))
                     //   if((coordinate_Y-0.5<section_coord_Y)&(coordinate_Y+0.5>section_coord_Y))
                        if(coordinate_X >= min_coord_X - step_coord_X/2 & coordinate_X < max_coord_X + step_coord_X/2)
                        if(coordinate_Y >= min_coord_Y - step_coord_Y/2 & coordinate_Y < max_coord_Y + step_coord_Y/2)
                        if(coordinate_Z >= min_coord_Z - step_coord_Z/2 & coordinate_Z < max_coord_Z + step_coord_Z/2)
                        {
                        //  System.out.println("coordinate_Z = "+coordinate_Z);
                            angle_vel_X        = new Double(st.nextToken()).doubleValue();
                            angle_vel_Y        = new Double(st.nextToken()).doubleValue();
                            angle_vel_Z        = new Double(st.nextToken()).doubleValue();
                            
                            tors_angle_X       = new Double(st.nextToken()).doubleValue();
                            tors_angle_Y       = new Double(st.nextToken()).doubleValue();
                            tors_angle_Z       = new Double(st.nextToken()).doubleValue();
                            
                            state              = new Byte(st.nextToken()).byteValue();
                            rel_tors_en_change = new Double(st.nextToken()).doubleValue();
                            rel_tors_energy    = new Double(st.nextToken()).doubleValue();
                            spec_total_energy  = new Double(st.nextToken()).doubleValue();
                         //   rel_tors_en_changes.add(rel_tors_en_change);
                            
                         //   if(rel_tors_en_change !=0)
                        //        mech_en_velocities.add((double)diss_en_velocities.get(cell_number)/rel_tors_en_change);
                        //    else
                         //       mech_en_velocities.add((double)0);
                            
                            if(rel_tors_energy < 0)
                                System.out.println("ERROR!!! rel_tors_energy must be positive!!! rel_tors_energy = "+rel_tors_energy);
                            
                        //    rel_tors_energy = rel_tors_energy/(cell_size*cell_size);
                        //    rel_tors_energy = rel_tors_energy/(1+rel_tors_energy);
                            
                            if(rel_tors_energy<0 | rel_tors_energy >1)
                                System.out.println("ERROR!!! rel_tors_energy must be in the limits of the interval [0; 1]!!! rel_tors_energy = "+rel_tors_energy);
                            
                            abs_angle_velocity = Math.sqrt(angle_vel_X*angle_vel_X   + angle_vel_Y*angle_vel_Y   + angle_vel_Z*angle_vel_Z);
                            abs_tors_angle     = Math.sqrt(tors_angle_X*tors_angle_X + tors_angle_Y*tors_angle_Y + tors_angle_Z*tors_angle_Z);
                            
                            // Calculation of average and summary values
                            aver_abs_angle_vel_X  += Math.abs(angle_vel_X);
                            aver_abs_angle_vel_Y  += Math.abs(angle_vel_Y);
                            aver_abs_angle_vel_Z  += Math.abs(angle_vel_Z);
                            
                            aver_abs_tors_angle_X += Math.abs(tors_angle_X);
                            aver_abs_tors_angle_Y += Math.abs(tors_angle_Y);
                            aver_abs_tors_angle_Z += Math.abs(tors_angle_Z);
                            
                            aver_rel_tors_en_change += rel_tors_en_change;
                            aver_rel_tors_energy    += rel_tors_energy;
                            aver_abs_tors_angle     += abs_tors_angle;
                            aver_abs_angle_velocity += abs_angle_velocity;
                      //      aver_mech_en_velocity   += (double)mech_en_velocities.get(cell_number);
                            aver_spec_total_energy  += spec_total_energy;
                                
                            cell_number++;
                            
                            // Cell located in the interior of grain
                            if(location_type == Common.INTRAGRANULAR_CELL)
                            {
                                aver_spec_abs_angle_velocity_gr_interior += abs_angle_velocity;
                                cell_number_gr_interior++;
                            }
                            
                            // Cell located at grain boundary in the interior of specimen
                            if(location_type == Common.INTERGRANULAR_CELL)
                            {
                                aver_spec_abs_angle_velocity_grain_bound += abs_angle_velocity;
                                cell_number_grain_bound++;
                            }
                        }
                    }
                }
            }
            
            if(calc_averages)
            {
                aver_abs_inst_mom_X     = aver_abs_inst_mom_X    /cell_number;
                aver_abs_inst_mom_Y     = aver_abs_inst_mom_Y    /cell_number;
                aver_abs_inst_mom_Z     = aver_abs_inst_mom_Z    /cell_number;
                aver_abs_inst_mom       = aver_abs_inst_mom      /cell_number;
                
                aver_log10_abs_inst_mom = aver_log10_abs_inst_mom/cell_number;
                aver_rel_tors_en_change = aver_rel_tors_en_change/cell_number;
                aver_rel_tors_energy    = aver_rel_tors_energy   /cell_number;
                
                aver_abs_angle_vel_X    = aver_abs_angle_vel_X/cell_number;// (1.0E18*cell_volume*cell_number);// 
                aver_abs_angle_vel_Y    = aver_abs_angle_vel_Y/cell_number;// (1.0E18*cell_volume*cell_number);// 
                aver_abs_angle_vel_Z    = aver_abs_angle_vel_Z/cell_number;// (1.0E18*cell_volume*cell_number);// 
                aver_abs_angle_velocity = aver_abs_angle_velocity/cell_number;// (1.0E18*cell_volume*cell_number);// 
                
                aver_abs_tors_angle_X   = aver_abs_tors_angle_X/cell_number;// (1.0E18*cell_volume*cell_number);// 
                aver_abs_tors_angle_Y   = aver_abs_tors_angle_Y/cell_number;// (1.0E18*cell_volume*cell_number);// 
                aver_abs_tors_angle_Z   = aver_abs_tors_angle_Z/cell_number;// (1.0E18*cell_volume*cell_number);// 
                aver_abs_tors_angle     = aver_abs_tors_angle  /cell_number;// (1.0E18*cell_volume*cell_number);// 
                
                aver_spec_abs_angle_velocity_gr_interior = aver_spec_abs_angle_velocity_gr_interior/cell_number_gr_interior;// (1.0E18*cell_volume*cell_number_gr_interior);// 
                aver_spec_abs_angle_velocity_grain_bound = aver_spec_abs_angle_velocity_grain_bound/cell_number_grain_bound;// (1.0E18*cell_volume*cell_number_grain_bound);// 
                
                aver_spec_total_energy  = aver_spec_total_energy /cell_number;
                aver_mech_en_velocity   = aver_mech_en_velocity/(cell_number*time_step);
                
                double total_diss_en_velocity_portion = aver_diss_en_velocity/(aver_mech_en_velocity + aver_diss_en_velocity);
                
              //  System.out.println("File # "+file_index+" is read! aver_abs_inst_mom_X = "+aver_abs_inst_mom_X+"; aver_abs_inst_mom_Y = "+aver_abs_inst_mom_Y+
              //                     "; aver_abs_inst_mom_Z = "+aver_abs_inst_mom_Z+"; aver_abs_inst_mom = "+aver_abs_inst_mom);
              //  System.out.println("File "+read_file_name+" is read! \nmin_coord_Z = "+min_coord_Z+"; max_coord_Z = "+max_coord_Z+
              //                                            "; aver_log10_abs_inst_mom = "+aver_log10_abs_inst_mom+
              //                                            "; log10_aver_abs_inst_mom = "+ (29+Math.log10(aver_abs_inst_mom)));
                
                // Time in milliseconds
              //  cur_time = (int)Math.round(file_index*time_step*1.0E9)/1.0E6;
                
                // Time in microseconds
                cur_time = (int)Math.round(file_index*time_step*1.0E9)/1.0E3;
             
                // Time in nanoseconds
             //   cur_time = (int)Math.round(file_index*time_step*1.0E15)/1.0E6;
                
                aver_diss_energy = aver_stress*aver_rel_tors_energy/(1 - aver_rel_tors_energy);
                
                // Average total energy in GPa
                aver_spec_total_energy = (long)Math.round(aver_spec_total_energy*1.0E-3)/1.0E6;
                
                // Transition from radians/sec to degrees/sec
                aver_abs_angle_velocity                  = aver_abs_angle_velocity*180/Math.PI;
                aver_spec_abs_angle_velocity_gr_interior = aver_spec_abs_angle_velocity_gr_interior*180/Math.PI;
                aver_spec_abs_angle_velocity_grain_bound = aver_spec_abs_angle_velocity_grain_bound*180/Math.PI;
                aver_abs_angle_vel_X = aver_abs_angle_vel_X*180/Math.PI;
                aver_abs_angle_vel_Y = aver_abs_angle_vel_Y*180/Math.PI;
                aver_abs_angle_vel_Z = aver_abs_angle_vel_Z*180/Math.PI;
                
                // Transition from degrees/sec to degrees/mcs
                aver_abs_angle_velocity                  = (long)Math.round(aver_abs_angle_velocity*1.0E6)/1.0E12;
                aver_spec_abs_angle_velocity_gr_interior = (long)Math.round(aver_spec_abs_angle_velocity_gr_interior*1.0E6)/1.0E12;
                aver_spec_abs_angle_velocity_grain_bound = (long)Math.round(aver_spec_abs_angle_velocity_grain_bound*1.0E6)/1.0E12;
                aver_abs_angle_vel_X = (long)Math.round(aver_abs_angle_vel_X*1.0E6)/1.0E12;
                aver_abs_angle_vel_Y = (long)Math.round(aver_abs_angle_vel_Y*1.0E6)/1.0E12;
                aver_abs_angle_vel_Z = (long)Math.round(aver_abs_angle_vel_Z*1.0E6)/1.0E12;
                
                // Transition from radians to degrees*1000
                aver_abs_tors_angle   = 0.001*aver_abs_tors_angle  *180.0/Math.PI;
                aver_abs_tors_angle_X = 0.001*aver_abs_tors_angle_X*180.0/Math.PI;
                aver_abs_tors_angle_Y = 0.001*aver_abs_tors_angle_Y*180.0/Math.PI;
                aver_abs_tors_angle_Z = 0.001*aver_abs_tors_angle_Z*180.0/Math.PI;
                
                // Transition from degrees to degrees*1000
            //    aver_abs_tors_angle   = (long)Math.round(aver_abs_tors_angle*1.0E6)/1.0E9;
            //    aver_abs_tors_angle_X = (long)Math.round(aver_abs_tors_angle_X*1.0E12)/1.0E15;
            //    aver_abs_tors_angle_Y = (long)Math.round(aver_abs_tors_angle_Y*1.0E6)/1.0E9;
            //    aver_abs_tors_angle_Z = (long)Math.round(aver_abs_tors_angle_Z*1.0E6)/1.0E9;
                
             //  System.out.println("Number of considered cells: "+cell_number+"; min_coord_Z = "+min_coord_Z+"; max_coord_Z = "+max_coord_Z);
             //   System.out.println("; time = "+cur_time+" mcs; aver_abs_tors_angle = "+aver_abs_tors_angle);
                System.out.println("aver_rel_tors_energy = "+aver_rel_tors_energy+"; aver_rel_tors_en_change = "+aver_rel_tors_en_change+";");
                System.out.println("aver_stress = "+aver_stress+" MPa; aver_diss_energy = "+aver_diss_energy+" MPa.");
                System.out.println("aver_spec_total_energy = "+aver_spec_total_energy+" MPa.");
                
                System.out.println("aver_spec_abs_angle_velocity = "+aver_abs_angle_velocity+" degrees/mcs.");//" rad/(mcm^3*sec).");//
                
                System.out.println("aver_spec_abs_angle_velocity_gr_interior = "+aver_spec_abs_angle_velocity_gr_interior+" degrees/mcs."// " rad/(mcm^3*sec)."//
                                 + "\ncell_number_gr_interior = "+cell_number_gr_interior);
                System.out.println("aver_spec_abs_angle_velocity_grain_bound = "+aver_spec_abs_angle_velocity_grain_bound+" degrees/mcs."// " rad/(mcm^3*sec)."//
                                 + "\ncell_number_grain_bound = "+cell_number_grain_bound);
                
                System.out.println("aver_abs_tors_angle   = "+aver_abs_tors_angle+  " degrees*1000;");
                System.out.println("aver_abs_tors_angle_X = "+aver_abs_tors_angle_X+" degrees*1000;");
                System.out.println("aver_abs_tors_angle_Y = "+aver_abs_tors_angle_Y+" degrees*1000;");
                System.out.println("aver_abs_tors_angle_Z = "+aver_abs_tors_angle_Z+" degrees*1000;");
                
                System.out.println("aver_mech_en_velocity = "+aver_mech_en_velocity+" Pa/s; aver_diss_en_velocity = "+aver_diss_en_velocity+" Pa/s; "+
                                   "total_diss_en_velocity_portion = "+total_diss_en_velocity_portion);
                br.close();
                
         //       bw1.write(file_index*time_step+" "+aver_abs_inst_mom_X);
         //       bw1.newLine();            bw1.flush();
            
         //       bw2.write(file_index*time_step+" "+aver_abs_inst_mom_Y);
         //       bw2.newLine();            bw2.flush();
            
         //       bw3.write(file_index*time_step+" "+aver_abs_inst_mom_Z);
         //       bw3.newLine();            bw3.flush();
                
                bw6.write(cur_time+" "+aver_rel_tors_energy);
                bw6.newLine();            bw6.flush();
                
         //       bw8.write(cur_time+" "+aver_diss_energy);
           //     bw8.newLine();
             //   bw8.flush();
                
                bw7.write(cur_time+" "+aver_abs_tors_angle);
                bw7.newLine();            bw7.flush();
                
         //       bw4.write(file_index*time_step+" "+aver_log10_abs_inst_mom);
           //     bw4.newLine();            bw4.flush();
                
                bw9.write(cur_time+" "+aver_abs_angle_velocity);
                bw9.newLine();            bw9.flush();
                
                bwA.write(cur_time+" "+aver_rel_tors_en_change);
                bwA.newLine();            bwA.flush();
                
          //      bwB.write(cur_time+" "+total_diss_en_velocity_portion);
          //      bwB.newLine();            bwA.flush();
                
           //     bwD.write(cur_time+" "+aver_spec_abs_angle_velocity_gr_interior);
           //     bwD.newLine();            bwD.flush();
                
          //      bwE.write(cur_time+" "+aver_spec_abs_angle_velocity_grain_bound);
           //     bwE.newLine();            bwE.flush();
                
                  bwG.write(cur_time+" "+aver_spec_total_energy);
                  bwG.newLine();            bwG.flush();
                  
                  bwH.write(cur_time+" "+aver_abs_angle_vel_X);
                  bwH.newLine();            bwH.flush();
                  
                  bwI.write(cur_time+" "+aver_abs_angle_vel_Y);
                  bwI.newLine();            bwI.flush();
                  
                  bwJ.write(cur_time+" "+aver_abs_angle_vel_Z);
                  bwJ.newLine();            bwJ.flush();
                  
                  bwK.write(cur_time+" "+aver_abs_tors_angle_X);
                  bwK.newLine();            bwK.flush();
                  
                  bwL.write(cur_time+" "+aver_abs_tors_angle_Y);
                  bwL.newLine();            bwL.flush();
                  
                  bwM.write(cur_time+" "+aver_abs_tors_angle_Z);
                  bwM.newLine();            bwM.flush();
            }
          }
          
          System.out.println("-----------============-------------==============-----------------=================");
          
          bw1.close();
          bw2.close();
          bw3.close();
          bw4.close();
          bw5.close();
          bw6.close();
          bw7.close();
          bw8.close();
          bw9.close();
          bwA.close();
      //  bwB.close();
          bwC.close();
      //  bwD.close();
      //  bwE.close();
          bwF.close();
          bwG.close();
          bwH.close();
          bwI.close();
          bwJ.close();
          bwK.close();
          bwL.close();
          bwM.close();
          bwN.close();
          bwO.close();
          bwP.close();
          bwQ.close();
          bwR.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates archives with files.
     * @param time_step_number
     * @param file_number
     * @param archive_number
     * @param task_name
     */
    private void createArchives(int time_step_number, int file_number, int archive_number, String task_name)
    {
        // Minimal number of time steps between files
        int min_time_step_num_files = time_step_number/file_number;
        
        // Minimal number of time steps between archives
        int min_time_step_num_archives = time_step_number/archive_number;
        
        // Number of files in archive
        int archive_file_number = file_number/archive_number;
        
        System.out.println();
        System.out.println("Minimal number of time steps between files:    "+min_time_step_num_files);
        System.out.println("Minimal number of time steps between archives: "+min_time_step_num_archives);
        System.out.println("Number of files in archive:                    "+archive_file_number);
        System.out.println();
        
        int archive_counter = 0;
        
        // Порядок общего числа временных шагов
        int max_power = (int)Math.floor(Math.log10(1.0*time_step_number));
        
        // Порядок текущего числа временных шагов
        int cur_power;
        
        // Имя архивируемого файла
        String file_name;
        
        // Counter of current time step
        int step_counter;
        
        // stream for reading from input file 
        FileInputStream in;
        
        // stream for writing to output file
        ZipOutputStream out;
        
        // buffer size
        byte[] b = new byte[1024];
        int count = 0;
        
        boolean delete_files = true; // false;// 
        
        // Archivation of files
        try
        {
          // Определение числа нулей перед номером шага в имени выходного файла
          String zeros = "";
            
          for(int zero_counter = 0; zero_counter < max_power; zero_counter++)
            zeros = zeros +"0";
          
          String zeros_1 = "";
          
          cur_power = (int)Math.floor(Math.log10(1.0*min_time_step_num_archives));
          
          for(int zero_counter = 0; zero_counter < max_power-cur_power; zero_counter++)
            zeros_1 = zeros_1 +"0";
          
          // stream for writing to the zip file
          out = new ZipOutputStream(new FileOutputStream("./user/task_db/"+task_name+"/"+task_name+"_"+zeros+"0-"+zeros_1+min_time_step_num_archives+"_torsion.zip"));
          
          System.out.println("Archive # "+archive_counter+": "+"./user/task_db/"+task_name+"/"+task_name+"_"+
                        zeros+min_time_step_num_archives*archive_counter+"-"+zeros_1+min_time_step_num_archives*(archive_counter+1)+"_torsion.zip");
          
          for (int file_counter = 0; file_counter <= file_number; file_counter++)
          {
            step_counter = file_counter*min_time_step_num_files;
            
            // Определение числа нулей перед номером шага в имени выходного файла
            zeros = "";
            
            if(step_counter > 0)
              cur_power = (int)Math.floor(Math.log10(1.0*step_counter));
            else
              cur_power = 0;
          
            for(int zero_counter = 0; zero_counter < max_power-cur_power; zero_counter++)
              zeros = zeros +"0";
            
            zeros_1 = "";
          
            cur_power = (int)Math.floor(Math.log10(1.0*step_counter + 1.0*min_time_step_num_files));
          
            for(int zero_counter = 0; zero_counter < max_power-cur_power; zero_counter++)
              zeros_1 = zeros_1 +"0";
          
            // Имя архивируемого файла
            file_name = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_counter+"_torsion.res";
          
            // stream for reading from input file 
            in  = new FileInputStream(file_name);
            
            if((file_counter - 1) % archive_file_number == 0 & file_counter != 1)
            {
                archive_counter++;
                
                // stream for writing to the zip file
                out = new ZipOutputStream(new FileOutputStream("./user/task_db/"+task_name+"/"+task_name+"_"+
                        zeros+(min_time_step_num_archives*archive_counter+min_time_step_num_files)+"-"+zeros_1+min_time_step_num_archives*(archive_counter+1)+"_torsion.zip"));
                
                System.out.println("Archive # "+archive_counter+": "+"./user/task_db/"+task_name+"/"+task_name+"_"+
                        zeros+(min_time_step_num_archives*archive_counter+min_time_step_num_files)+"-"+zeros_1+min_time_step_num_archives*(archive_counter+1)+"_torsion.zip");
            }
              
            // name of the file inside the zip file
            out.putNextEntry(new ZipEntry(task_name+"_"+zeros+step_counter+"_torsion.res"));
            
            // buffer size
            b = new byte[1024];
            count = 0;
              
            while ((count = in.read(b)) > 0)
            {
                out.write(b, 0, count);
                // out.flush();
            }
              
            out.flush();
            in.close();
            
            if(delete_files)
            {
              File file = new File(file_name);
              file.delete();
            }
            
            System.out.println("File "+file_name+" is archived and deleted.");
              
            if(file_counter % archive_file_number == 0 & file_counter != 0)
            {
              out.close();
              System.out.println("Creation of archive # "+archive_counter+" is finished!\n");
            }
          }
          
          out.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("IOException: "+io_exc);
        }
    }
    
    /** The method creates time graphs for total energy coming to a system.
     * @param task_name name of task
     */
    public void createTimeGraphTotalEnergies(String task_name)
    {
        String read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_total_energies.txt";
        String write_file_name = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_aver_spec_heat_energies.txt";
        
        cell_volume = calcCellVolume(cell_size);
        int cell_number = 4070;
        
        // Parameters of material (TiAlC)
        double specific_heat_capacity  =  514.0;
        double density                 = 4500.0;
        double init_temperature        = 1000.0;
        
        // Initial specific heat energy of each inner cell
        double init_spec_heat_energy = specific_heat_capacity*density*init_temperature;
        
        String string, token;
        StringTokenizer st;
        
        double time_moment, total_mech_influx, inner_mech_influx, inner_tors_energy,
               current_outer_heat_influx, current_inner_heat_influx, total_outer_heat_influx, total_inner_heat_influx;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            
            bw.write("0.0 "+(long)Math.round(init_spec_heat_energy*1.0E-6)/1.0E3+"\n");
            
            while(br.ready())
            {
                string = br.readLine();
                
                st = new StringTokenizer(string);
                
                token = st.nextToken();
                
                if(!token.equals("#"))
                if(st.hasMoreTokens())
                {
                    time_moment               = new Double(token).doubleValue();
                    total_mech_influx         = new Double(st.nextToken()).doubleValue();
                    inner_mech_influx         = new Double(st.nextToken()).doubleValue();
                    inner_tors_energy         = new Double(st.nextToken()).doubleValue();
                    current_outer_heat_influx = new Double(st.nextToken()).doubleValue();
                    current_inner_heat_influx = new Double(st.nextToken()).doubleValue();
                    total_outer_heat_influx   = new Double(st.nextToken()).doubleValue();
                    total_inner_heat_influx   = new Double(st.nextToken()).doubleValue();
                    
                    current_outer_heat_influx = current_outer_heat_influx/(cell_volume*cell_number);
                    current_inner_heat_influx = current_inner_heat_influx/(cell_volume*cell_number);
                    total_outer_heat_influx   = init_spec_heat_energy - total_outer_heat_influx/(cell_volume*cell_number);
                    total_inner_heat_influx   = init_spec_heat_energy + total_inner_heat_influx/(cell_volume*cell_number);
                    
                    // Time in nanoseconds
                    time_moment               = (long)Math.round(time_moment*1.0E15)/1.0E6;
                    
                    // Specific heat influx in GPa
                    total_outer_heat_influx   = (long)Math.round(total_outer_heat_influx*1.0E-6)/1.0E3;
                    
                    bw.write(time_moment+" "+total_outer_heat_influx+"\n");
                    bw.flush();
                }
            }
            
            System.out.println("File "+write_file_name+" is created!");
            
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Exception: "+io_exc);
        }
    }
            
    /** The method calculates volume of cell3D for modelling of deformation.
     * @param size_X size of the cell3D in direction of axis X
     * @param size_Y size of the cell3D in direction of axis Y
     * @param size_Z size of the cell3D in direction of axis Z
     */
    public double calcCellVolume(double cell_size)
    {
        double volume = 0;
        
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            volume = 0.6938*cell_size*cell_size*cell_size;

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            volume = cell_size*cell_size*cell_size;
        
        return volume;
    }
        
    /** The method creates file for time graphs of torsion angle components
     * in certain section perpendicular to chosen axis.
     * @param section_coord_X coordinate X of considered section
     * @param section_coord_Y coordinate X of considered section
     * @param section_coord_Z coordinate X of considered section
     */
    private void createTimeGraphsOfTorsionAnglesForSection(double section_coord_X, double section_coord_Y, double section_coord_Z)
    {
        String string, token_1;
        
        // Minimal and maximal coordinates X, Y, Z
        double min_coord_X =  2.521;
        double max_coord_X = 50.152;
        double min_coord_Y =  2.75;
        double max_coord_Y = 49.75;
        double min_coord_Z = section_coord_Z;
        double max_coord_Z = section_coord_Z;
        
        // Distances between cell rows arranged along axes
        double step_coord_X = Math.sqrt(0.75);   // = 0.8660
        double step_coord_Y = 1.0;
        double step_coord_Z = Math.sqrt(2.0/3.0);// = 0.8165;
        
        // Each string contains parameters of corresponding cell:
        // 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates;
        // 6-8. 3 components of angle velocity;
        // 9-11. 3 components of torsion angle vector;
        // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
        // 13. relative power of torsion (current influx of torsion energy divided by current influx of elastic energy);
        // 14. relative torsion energy (total torsion energy divided by total elastic energy).
        int energy_type;
        int location_type;
        int grain_index;
        
        double coordinate_X = 0;
        double coordinate_Y = 0;
        double coordinate_Z = 0;
        
        double angle_vel_X  = 0;
        double angle_vel_Y  = 0;
        double angle_vel_Z  = 0;
        
        double tors_angle_X = 0;
        double tors_angle_Y = 0;
        double tors_angle_Z = 0;
        
        // absolute values of angle components
        double abs_tors_angle_X = 0;
        double abs_tors_angle_Y = 0;
        double abs_tors_angle_Z = 0;        
        double abs_tors_angle   = 0;
        
        // Number of files
        int file_number     = 2000;
        
        // Periods of recording of data in files
        int record_period   =  100;        
        int record_period_2 = 2500;
        
        // Last value of time step
        max_step_number     = file_number*record_period;
        
        // Time step of task
        double time_step    = 1.0E-9;
             
        // Average values of absolute values of angle components for section
        double aver_abs_tors_angle_X = 0;
        double aver_abs_tors_angle_Y = 0;
        double aver_abs_tors_angle_Z = 0;
        double aver_abs_tors_angle   = 0;
        
        int cell_number = 0;        
        boolean calc_averages = false;
        
        try
        {
          String write_file_name_1 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_aver_abs_tors_angle_X.txt";
          String write_file_name_2 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_aver_abs_tors_angle_Y.txt";
          String write_file_name_3 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_aver_abs_tors_angle_Z.txt";
          String write_file_name_4 = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_Z="+section_coord_Z+"_aver_abs_tors_angle.txt";
                    
          BufferedWriter bw1 = new BufferedWriter(new FileWriter(write_file_name_1));
          BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
          BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
          BufferedWriter bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
          
          read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_000000_torsion.res";
          BufferedReader br = new BufferedReader(new FileReader(read_file_name));
          
          // Exponent of power of 10 in standard representation
          // of total number of time steps
          int max_exponent = (int)Math.floor(Math.log10(max_step_number));

          // Exponent of power of 10 in standard representation
          // of number of current time step
          int exponent = 0;
          
          // Index of current file
          int file_index = 0;
          
          for(int file_counter = 0; file_counter<=file_number; file_counter++)
          {
            aver_abs_tors_angle_X = 0;
            aver_abs_tors_angle_Y = 0;
            aver_abs_tors_angle_Z = 0;
            aver_abs_tors_angle   = 0;
            
            cell_number = 0;            
            calc_averages = false;
            
            file_index = file_counter*record_period;

            if(file_index > 0)
                exponent     = (int)Math.floor(Math.log10(file_index));

            // String of zeros
            String zeros = "";

            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";
                        
            if((file_index <=  1000 & file_index %  100 == 0)| 
               (file_index <= 10000 & file_index % 1000 == 0)|
                file_index % record_period_2 == 0)
            {
                calc_averages = true;
                
                read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+file_index+"_torsion.res";
                br = new BufferedReader(new FileReader(read_file_name));
            }
            
            if(calc_averages)
            while(br.ready())
            {
                string = br.readLine();
                
                StringTokenizer st = new StringTokenizer(string);
                
                token_1 = st.nextToken();
                
                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = new Integer(token_1).intValue();
                    location_type  = new Integer(st.nextToken()).intValue();
                    grain_index    = new Integer(st.nextToken()).intValue();
                    
                    if(energy_type == 0 | energy_type == 1)
                    {
                        coordinate_X   = new Double(st.nextToken()).doubleValue();
                        coordinate_Y   = new Double(st.nextToken()).doubleValue();
                        coordinate_Z   = new Double(st.nextToken()).doubleValue();

                     //   if((coordinate_X-0.5<section_coord_X)&(coordinate_X+0.5>section_coord_X))
                     //   if((coordinate_Y-0.5<section_coord_Y)&(coordinate_Y+0.5>section_coord_Y))
                        if(coordinate_Z-0.5 < section_coord_Z & coordinate_Z+0.5 > section_coord_Z)
                        if(coordinate_X >= min_coord_X - step_coord_X/2 & coordinate_X < max_coord_X + step_coord_X/2)
                        if(coordinate_Y >= min_coord_Y - step_coord_Y/2 & coordinate_Y < max_coord_Y + step_coord_Y/2)
                        {
                            angle_vel_X  = new Double(st.nextToken()).doubleValue();
                            angle_vel_Y  = new Double(st.nextToken()).doubleValue();
                            angle_vel_Z  = new Double(st.nextToken()).doubleValue();
                            
                            tors_angle_X = new Double(st.nextToken()).doubleValue();
                            tors_angle_Y = new Double(st.nextToken()).doubleValue();
                            tors_angle_Z = new Double(st.nextToken()).doubleValue();
                            
                            abs_tors_angle = Math.sqrt(tors_angle_X*tors_angle_X + tors_angle_Y*tors_angle_Y + tors_angle_Z*tors_angle_Z);
            
                            aver_abs_tors_angle_X += Math.abs(tors_angle_X);
                            aver_abs_tors_angle_Y += Math.abs(tors_angle_Y);
                            aver_abs_tors_angle_Z += Math.abs(tors_angle_Z);
                            aver_abs_tors_angle   += abs_tors_angle;
                            
                            cell_number++;
                        }
                    }
                }
            }
            
            if(calc_averages)
            {
                aver_abs_tors_angle_X = aver_abs_tors_angle_X/cell_number;
                aver_abs_tors_angle_Y = aver_abs_tors_angle_Y/cell_number;
                aver_abs_tors_angle_Z = aver_abs_tors_angle_Z/cell_number;
                aver_abs_tors_angle   = aver_abs_tors_angle  /cell_number;
            
                System.out.println("File "+read_file_name+" is read!\n"+
                               "  aver_abs_tors_angle_X = "+aver_abs_tors_angle_X+
                               "; aver_abs_tors_angle_Y = "+aver_abs_tors_angle_Y+                        
                               "; aver_abs_tors_angle_Z = "+aver_abs_tors_angle_Z+
                               "; aver_abs_tors_angle = "  +aver_abs_tors_angle);
                br.close();
            
                bw1.write(file_index*time_step+" "+aver_abs_tors_angle_X);
                bw1.newLine();
                bw1.flush();
            
                bw2.write(file_index*time_step+" "+aver_abs_tors_angle_Y);
                bw2.newLine();
                bw2.flush();
            
                bw3.write(file_index*time_step+" "+aver_abs_tors_angle_Z);
                bw3.newLine();
                bw3.flush();
            
                bw4.write(file_index*time_step+" "+aver_abs_tors_angle);
                bw4.newLine();
                bw4.flush();
            
                System.out.println("Number of considered cells in the section: "+cell_number);
                System.out.println("Values at step # "+file_index+" are written!");
            }
          }
          
          bw1.close();
          bw2.close();
          bw3.close();
          bw4.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file for graphs "eff_stress-coord_Z", "moment_X-coord_Z"
     * and "moment_Z-coord_Z" along certain line perpendicular to plane XOY at given time step.
     * @param section_coord_X coordinate X of considered section
     * @param section_coord_Y coordinate Y of considered section
     * @param time_step_number the number of considered time step
     */
    private void createGraphs_Z(double section_coord_X, double section_coord_Y, int time_step_number)
    {
        String string, token_1;

        // Minimal and maximal coordinates Z
        double min_coord_Z =  2.0;
        double max_coord_Z = 50.0;

        // Cell parameters:
        // 0. energy type; 1. location type; 2. grain index;
        // 3-5. 3 coordinates (in cell sizes); 6. temperature;
        // 7. effective stress; 8. principal stress;
        // 9-11. 3 components of force moment vector;
        // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
        // 13. strain.
        int energy_type;
        int location_type;
        int grain_index;
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        double temperature;
        double eff_stress = 0;
        double prn_stress = 0;

        double mom_X = 0;
        double mom_Y = 0;
        double mom_Z = 0;

        double log10_mom_X = 0;
        double log10_mom_Y = 0;
        double log10_mom_Z = 0;

        byte   state = 0;
        double strain = 0;

        int cell_number = 0;

        try
        {
          // Exponent of power of 10 in standard representation
          // of total number of time steps
          int max_exponent = (int)Math.floor(Math.log10(max_step_number));

          // Exponent of power of 10 in standard representation
          // of number of current time step
          int exponent = 0;

          if(time_step_number > 0)
              exponent     = (int)Math.floor(Math.log10(time_step_number));

          // String of zeros
          String zeros = "";

          for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
              zeros = zeros + "0";

          read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+".res";
       //   read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+"_vectors.res";
          BufferedReader br = new BufferedReader(new FileReader(read_file_name));

          write_file_name = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_eff_stresses.txt";
          String write_file_name_2 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_moments_X.txt";
          String write_file_name_3 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_moments_Z.txt";
          String write_file_name_4 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_moments_Y.txt";
          String write_file_name_5 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_log10_moments_X.txt";
          String write_file_name_6 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_log10_moments_Z.txt";
          String write_file_name_7 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_log10_moments_Y.txt";
       //  String write_file_name_8 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_displacements_X.txt";
       //  String write_file_name_9 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_displacements_Z.txt";
       //  String write_file_name_10 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_X="+section_coord_X+"_Y="+section_coord_Y+"_displacements_Y.txt";

          BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));
          BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
          BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
          BufferedWriter bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
          BufferedWriter bw5 = new BufferedWriter(new FileWriter(write_file_name_5));
          BufferedWriter bw6 = new BufferedWriter(new FileWriter(write_file_name_6));
          BufferedWriter bw7 = new BufferedWriter(new FileWriter(write_file_name_7));

          while(br.ready())
          {
              string = br.readLine();
              StringTokenizer st = new StringTokenizer(string);

              token_1 = st.nextToken();

              if(!token_1.equals("#"))
              if(st.hasMoreTokens())
              {
                  // Reading of coordinates and physical parameters of current cell
                  energy_type    = new Integer(token_1).intValue();
                  location_type  = new Integer(st.nextToken()).intValue();
                  grain_index    = new Integer(st.nextToken()).intValue();

                  if((energy_type==0)|(energy_type==1))
                  {
                      coordinate_X   = new Double(st.nextToken()).doubleValue();
                      coordinate_Y   = new Double(st.nextToken()).doubleValue();
                      coordinate_Z   = new Double(st.nextToken()).doubleValue();

                      if((coordinate_Z>=min_coord_Z)&(coordinate_Z<=max_coord_Z))
                      if((coordinate_X-0.5<section_coord_X)&(coordinate_X+0.5>section_coord_X))
                      if((coordinate_Y-0.5<section_coord_Y)&(coordinate_Y+0.5>section_coord_Y))
                      {
                          temperature    = new Double(st.nextToken()).doubleValue();
                          eff_stress     = new Double(st.nextToken()).doubleValue();
                          prn_stress     = new Double(st.nextToken()).doubleValue();
                          mom_X          = new Double(st.nextToken()).doubleValue();
                          mom_Y          = new Double(st.nextToken()).doubleValue();
                          mom_Z          = new Double(st.nextToken()).doubleValue();
                          state          = new Byte(st.nextToken()).byteValue();
                          strain         = new Double(st.nextToken()).doubleValue();

                          cell_number++;

                     
                          bw2.write(coordinate_Z+" "+mom_X);
                          bw2.newLine();
                          bw2.flush();

                          bw3.write(coordinate_Z+" "+mom_Z);
                          bw3.newLine();
                          bw3.flush();

                          bw4.write(coordinate_Z+" "+mom_Y);
                          bw4.newLine();
                          bw4.flush();
                                                
                          if(mom_X!=0)
                          {
                              log10_mom_X = Math.abs(mom_X)/mom_X*(31+Math.log10(Math.abs(mom_X)));

                              if(log10_mom_X/mom_X<0)
                              {
                                  log10_mom_X = 0;
                                  System.out.println("The formula for calculation of <<log10_mom_X>> is not correct!!!");
                              }
                          }

                          if(mom_Y!=0)
                          {
                              log10_mom_Y = Math.abs(mom_Y)/mom_Y*(31+Math.log10(Math.abs(mom_Y)));
                              
                              if(log10_mom_Y/mom_Y<0)
                              {
                                  log10_mom_Y = 0;
                                  System.out.println("The formula for calculation of <<log10_mom_Y>> is not correct!!!");
                              }
                          }

                          if(mom_Z!=0)
                          {
                              log10_mom_Z = Math.abs(mom_Z)/mom_Z*(31+Math.log10(Math.abs(mom_Z)));
                              
                              if(log10_mom_Z/mom_Z<0)
                              {
                                  log10_mom_Z = 0;
                                  System.out.println("The formula for calculation of <<log10_mom_Z>> is not correct!!!");
                              }
                          }  
                          
                          bw.write(coordinate_Z+" "+eff_stress);
                          bw.newLine();
                          bw.flush();

                          
                          bw5.write(coordinate_Z+" "+log10_mom_X);
                          bw5.newLine();
                          bw5.flush();

                          bw6.write(coordinate_Z+" "+log10_mom_Z);
                          bw6.newLine();
                          bw6.flush();

                          bw7.write(coordinate_Z+" "+log10_mom_Y);
                          bw7.newLine();
                          bw7.flush();
                      }
                  }
              }
          }

          System.out.println("File "+read_file_name+" is read!");
          br.close();

          System.out.println("Number of considered cells in the section: "+cell_number);
          System.out.println("File "+write_file_name+" is changed!");
          System.out.println("File "+write_file_name_2+" is changed!");
          System.out.println("File "+write_file_name_3+" is changed!");
          System.out.println("File "+write_file_name_4+" is changed!");
          System.out.println("File "+write_file_name_5+" is changed!");
          System.out.println("File "+write_file_name_6+" is changed!");
          System.out.println("File "+write_file_name_7+" is changed!");
          System.out.println();
          
          bw.close();
          bw2.close();
          bw3.close();
          bw4.close();
          bw5.close();
          bw6.close();
          bw7.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
    
    /** The method creates file for graphs "average_abs_inst_mom_Y-coord_X", "average_abs_inst_mom_X-coord_Y", 
     * "average_abs_inst_mom_Z-coord_X", "average_abs_inst_mom_Z-coord_Y", 
     * "average_abs_inst_mom-coord_X", "average_abs_inst_mom-coord_X" along direction of loading at given time step.
     * @param time_step_number the number of considered time step
     */
    private void createGraphOfAverValueAlongAxis(int time_step_number)
    {
        String string, token_1;
        
        // Minimal and maximal coordinates X, Y, Z
        double min_coord_X =  2.0;//  2.521;
        double max_coord_X = 40.105;// 50.152;
        double min_coord_Y =  1.250;// 2.75;
        double max_coord_Y =  2.250;// 49.75;
        double min_coord_Z =  6.215;// 1.316;
        double max_coord_Z = 45.407;// 1.316;
        
        // Distances between cell rows arranged along axes
        double step_coord_X = Math.sqrt(0.75);
        double step_coord_Y = 1.0;
        double step_coord_Z = Math.sqrt(2.0/3.0);
        
        // Number of cell rows along axes X, Y, Z
        int row_number_X = (int)Math.round((max_coord_X - min_coord_X)/step_coord_X);// + 1;
        int row_number_Y = (int)Math.round((max_coord_Y - min_coord_Y)/step_coord_Y) + 1;
        int row_number_Z = (int)Math.round((max_coord_Z - min_coord_Z)/step_coord_Z) + 1;
        
        // Arrays of average values for each cell row perpendicular to axis of loading
        double[] aver_abs_inst_mom_Z_along_OX = new double[row_number_X];
        double[] aver_abs_inst_mom_Z_along_OY = new double[row_number_Y];
        
        double[] aver_abs_inst_mom_X_along_OX = new double[row_number_X];
        double[] aver_abs_inst_mom_Y_along_OY = new double[row_number_Y];
        
        double[] aver_abs_inst_mom_along_OX   = new double[row_number_X];
        double[] aver_abs_inst_mom_along_OY   = new double[row_number_Y];
        
        double[] aver_eff_stress_along_OZ     = new double[row_number_Z];
        double[] aver_tors_en_power_along_OZ  = new double[row_number_Z];        
        
        // Indices of cell in rows
        int row_index_I;
        int row_index_J;
        int row_index_K;

        // Each string contains parameters of corresponding cell:
        // 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates;
        // 6-8. 3 absolute values of components of instant specific force moment;
        // 9-11. 3 components of instant specific force moment;
        // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
        // 13. absolute value of instant specific force moment;
        // 14. current influx of torsion energy.
        int energy_type;
        int location_type;
        int grain_index;
        
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        
        double abs_inst_mom_X = 0;
        double abs_inst_mom_Y = 0;
        double abs_inst_mom_Z = 0;
        
        double inst_mom_X = 0;
        double inst_mom_Y = 0;
        double inst_mom_Z = 0;

        byte   state = 0;
        double abs_inst_mom = 0;
        
        double eff_stress, tors_en_power;

        double aver_abs_inst_mom_X = 0;
        double aver_abs_inst_mom_Y = 0;
        double aver_abs_inst_mom_Z = 0;
        double aver_abs_inst_mom   = 0;

        int cell_number = 0;
        
        try
        {
          // Exponent of power of 10 in standard representation
          // of total number of time steps
          int max_exponent = (int)Math.floor(Math.log10(max_step_number));

          // Exponent of power of 10 in standard representation
          // of number of current time step
          int exponent = 0;

          if(time_step_number > 0)
              exponent     = (int)Math.floor(Math.log10(time_step_number));

          // String of zeros
          String zeros = "";

          for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
              zeros = zeros + "0";

          read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+".res";
       //   read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+"_torsion.res";
       //   read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+"_vectors.res";
          BufferedReader br = new BufferedReader(new FileReader(read_file_name));
       /*
          String write_file_name_1 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_Z=1_aver_abs_inst_mom_Z_along_OX.txt";
          String write_file_name_2 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_Z=1_aver_abs_inst_mom_Z_along_OY.txt";
          
          String write_file_name_3 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_Z=1_aver_abs_inst_mom_X_along_OX.txt";
          String write_file_name_4 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_Z=1_aver_abs_inst_mom_Y_along_OY.txt";
          
          String write_file_name_5 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_Z=1_aver_abs_inst_mom_along_OX.txt";
          String write_file_name_6 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_Z=1_aver_abs_inst_mom_along_OY.txt";
        */
          String write_file_name_7 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_J=1_aver_eff_stresses_along_OZ.txt";
       //   String write_file_name_8 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_"+zeros+time_step_number+"_J=1_aver_tors_en_powers_along_OZ.txt";
          
        /*
          BufferedWriter bw1 = new BufferedWriter(new FileWriter(write_file_name_1));
          BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
          BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
          BufferedWriter bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
          BufferedWriter bw5 = new BufferedWriter(new FileWriter(write_file_name_5));
          BufferedWriter bw6 = new BufferedWriter(new FileWriter(write_file_name_6));
        */
          BufferedWriter bw7 = new BufferedWriter(new FileWriter(write_file_name_7));
        // BufferedWriter bw8 = new BufferedWriter(new FileWriter(write_file_name_8));

          while(br.ready())
          {
              string = br.readLine();
              StringTokenizer st = new StringTokenizer(string);

              token_1 = st.nextToken();

              if(!token_1.equals("#"))
              if(st.hasMoreTokens())
              {
                  // Reading of coordinates and physical parameters of current cell
                  energy_type    = new Integer(token_1).intValue();
                  location_type  = new Integer(st.nextToken()).intValue();
                  grain_index    = new Integer(st.nextToken()).intValue();

            //      if((energy_type==0)|(energy_type==1))
                  {
                      coordinate_X   = new Double(st.nextToken()).doubleValue();
                      coordinate_Y   = new Double(st.nextToken()).doubleValue();
                      coordinate_Z   = new Double(st.nextToken()).doubleValue();

                   //   if(coordinate_X >= min_coord_X - step_coord_X/2 & coordinate_X < max_coord_X + step_coord_X/2)
                     // if(coordinate_Y >= min_coord_Y - step_coord_Y/2 & coordinate_Y < max_coord_Y + step_coord_Y/2)
                    //  if(coordinate_Z >= min_coord_Z - step_coord_Z/2 & coordinate_Z < max_coord_Z + step_coord_Z/2)
                      if(coordinate_X >= min_coord_X & coordinate_X <= max_coord_X)
                      if(coordinate_Y >= min_coord_Y & coordinate_Y <= max_coord_Y)
                      if(coordinate_Z >= min_coord_Z & coordinate_Z <= max_coord_Z)
                      {
                      //    row_index_I    = (int)Math.round((coordinate_X - min_coord_X)/step_coord_X);
                      //    row_index_J    = (int)Math.round((coordinate_Y - min_coord_Y)/step_coord_Y);
                          row_index_K    = (int)Math.round((coordinate_Z - min_coord_Z)/step_coord_Z);
                          
                          abs_inst_mom_X = new Double(st.nextToken()).doubleValue();
                       //   abs_inst_mom_Y = new Double(st.nextToken()).doubleValue();
                          eff_stress     = new Double(st.nextToken()).doubleValue();
                          abs_inst_mom_Z = new Double(st.nextToken()).doubleValue();
                          inst_mom_X     = new Double(st.nextToken()).doubleValue();
                          inst_mom_Y     = new Double(st.nextToken()).doubleValue();
                          inst_mom_Z     = new Double(st.nextToken()).doubleValue();
                          state          = new Byte(st.nextToken()).byteValue();
                       //   abs_inst_mom   = new Double(st.nextToken()).doubleValue();
                          tors_en_power  = new Double(st.nextToken()).doubleValue();
                          
                     //     aver_abs_inst_mom_Z_along_OX[row_index_I] += abs_inst_mom_Z;
                     //     aver_abs_inst_mom_Z_along_OY[row_index_J] += abs_inst_mom_Z;
        
                     //     aver_abs_inst_mom_X_along_OX[row_index_I] += abs_inst_mom_X;
                     //     aver_abs_inst_mom_Y_along_OY[row_index_J] += abs_inst_mom_Y;
        
                     //     aver_abs_inst_mom_along_OX[row_index_I]   += abs_inst_mom;
                     //     aver_abs_inst_mom_along_OY[row_index_J]   += abs_inst_mom;
                          
                           aver_eff_stress_along_OZ   [row_index_K] += eff_stress;
                           aver_tors_en_power_along_OZ[row_index_K] += tors_en_power;
        
                           cell_number++;
                      }
                  }
              }
          }
          
          System.out.println("Number of considered cells in the section: "+cell_number+" = "+row_number_X+"*"+row_number_Z);
          System.out.println("File "+read_file_name+" is read!");
          br.close();

        /*
          for(int rowX_counter = 0; rowX_counter < row_number_X; rowX_counter++)
          {
              coordinate_X = min_coord_X + rowX_counter*step_coord_X;
              
              bw1.write(coordinate_X+" "+aver_abs_inst_mom_Z_along_OX[rowX_counter]/row_number_Y+"\n");
              bw1.flush();
              bw3.write(coordinate_X+" "+aver_abs_inst_mom_X_along_OX[rowX_counter]/row_number_Y+"\n");
              bw3.flush();
              bw5.write(coordinate_X+" "+aver_abs_inst_mom_along_OX[rowX_counter]/row_number_Y+"\n");
              bw5.flush();              
          }
          
          for(int rowY_counter = 0; rowY_counter < row_number_Y; rowY_counter++)
          {
              coordinate_Y = min_coord_Y + rowY_counter*step_coord_Y;
              
              bw2.write(coordinate_Y+" "+aver_abs_inst_mom_Z_along_OY[rowY_counter]/row_number_X+"\n");
              bw2.flush();
              bw4.write(coordinate_Y+" "+aver_abs_inst_mom_Y_along_OY[rowY_counter]/row_number_X+"\n");
              bw4.flush();
              bw6.write(coordinate_Y+" "+aver_abs_inst_mom_along_OY[rowY_counter]/row_number_X+"\n");
              bw6.flush();
          }
       */
          
          for(int row_counter_Z = 0; row_counter_Z < row_number_Z; row_counter_Z++)
          {
              coordinate_Z = min_coord_Z + row_counter_Z*step_coord_Z;
              
              bw7.write(coordinate_Z+" "+1.0E-9*aver_eff_stress_along_OZ[row_counter_Z]/row_number_X+"\n");
              bw7.flush();
            //  bw8.write(coordinate_Z+" "+aver_tors_en_power_along_OZ[row_counter_Z]/row_number_X+"\n");
            //  bw8.flush();
          }
          
      //    System.out.println("File "+write_file_name_1+" is changed!");
      //    System.out.println("File "+write_file_name_2+" is changed!");
      //    System.out.println("File "+write_file_name_3+" is changed!");
      //    System.out.println("File "+write_file_name_4+" is changed!");
      //    System.out.println("File "+write_file_name_5+" is changed!");
      //    System.out.println("File "+write_file_name_6+" is changed!");
          System.out.println("File "+write_file_name_7+" is changed!");
          System.out.println();
          
       //   System.out.println("File "+write_file_name_8+" is changed!");
       //   System.out.println();
          
      //    bw1.close();          bw2.close();          bw3.close();          bw4.close();          bw5.close();          bw6.close();
          bw7.close();
      //    bw8.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates files for graphs of average values for cell layers perpendicular to Z axis -
     * "average effective stress - coordinate Z", "average absolute value of force moment - coordinate Z",
     * "average temperature - coordinate Z" - at certain time step.
     * @param time_step_number the number of considered time step
     */
    private void createGraphs_averageValue_coordZ(int time_step_number)
    {
        System.out.println();
        System.out.println("Task name: "+task_name);
        System.out.println("Method 'createGraphs_averageValue_coordZ(int time_step_number)' is started!!!");
        
        String string, token_1;

        // Minimal and maximal coordinates Z
        double min_coord_Z =  9.0; //  5.0; // 4.5; //  8.0; // 
        double max_coord_Z = 42.5; // 47.0; //47.5; // 50.0; // 
        
        // Cell size
        cell_size = 1.0E-7;

        // Cell parameters:
        // 0. energy type; 1. location type; 2. grain index;
        // 3-5. 3 coordinates (in cell sizes); 6. temperature;
        // 7. effective stress; 8. principal stress;
        // 9-11. 3 components of force moment vector;
        // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
        // 13. strain.
        byte energy_type;
        byte location_type;
        int grain_index;
        
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        
        double temperature;
        double eff_stress = 0;
        double prn_stress = 0;

        double mom_X = 0;
        double mom_Y = 0;
        double mom_Z = 0;
        
        double tors_velocity = 0;
        double tors_angle = 0;

        byte   state = 0;
        double strain = 0;

        int plane_cell_number = 0;
        boolean first_cell = true;
        
        double current_coord_Z = -1;
        double abs_moment = -1;
        
        double aver_eff_stress = 0;
        double aver_abs_moment = 0;
        double aver_temperature = 0;
        double aver_prn_stress = 0;
        double aver_tors_velocity = 0;
        double aver_tors_angle = 0;
        
        int total_cell_number = 0;
        double total_aver_eff_stress = 0;
        double total_aver_abs_moment = 0;
        double total_aver_prn_stress = 0;
        double total_aver_tors_velocity = 0;
        double total_aver_tors_angle = 0;
        
        try
        {
          // Exponent of power of 10 in standard representation
          // of total number of time steps
          int max_exponent = (int)Math.floor(Math.log10(max_step_number));

          // Exponent of power of 10 in standard representation
          // of number of current time step
          int exponent = 0;

          if(time_step_number > 0)
              exponent     = (int)Math.floor(Math.log10(time_step_number));

          // String of zeros
          String zeros = "";

          for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
              zeros = zeros + "0";

          read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+".res";
       //   read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+"_torsion.res";
       //   read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_number+"_vectors.res";
          BufferedReader br = new BufferedReader(new FileReader(read_file_name));

          String write_file_name_1 = "./user/task_db/"+task_name+"/graphs_along_Z/"+task_name+"_"+zeros+time_step_number+"_aver_eff_stresses_along_Z.txt";
          String write_file_name_2 = "./user/task_db/"+task_name+"/graphs_along_Z/"+task_name+"_"+zeros+time_step_number+"_aver_abs_moments_along_Z.txt";
      //    String write_file_name_3 = "./user/task_db/"+task_name+"/graphs_along_Z/"+task_name+"_"+zeros+time_step_number+"_aver_prn_stresses_along_Z.txt";
          
      //    String write_file_name_1 = "./user/task_db/"+task_name+"/graphs_along_Z/"+task_name+"_"+zeros+time_step_number+"_aver_tors_velocities_along_Z.txt";
      //    String write_file_name_2 = "./user/task_db/"+task_name+"/graphs_along_Z/"+task_name+"_"+zeros+time_step_number+"_aver_tors_angles_along_Z.txt";
          String write_file_name_3 = "./user/task_db/"+task_name+"/graphs_along_Z/"+task_name+"_"+zeros+time_step_number+"_aver_temperatures_along_Z.txt";
          
          BufferedWriter bw1 = new BufferedWriter(new FileWriter(write_file_name_1));
          BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
          BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
          
          while(br.ready())
          {
              string = br.readLine();
              StringTokenizer st = new StringTokenizer(string);
              
              token_1 = st.nextToken();
              
              if(!token_1.equals("#"))
              if(st.hasMoreTokens())
              {
                  // Reading of coordinates and physical parameters of current cell
                  energy_type    = new Byte(token_1).byteValue();
                  location_type  = new Byte(st.nextToken()).byteValue();
                  grain_index    = new Integer(st.nextToken()).intValue();
                  
                  if(energy_type==Common.INNER_CELL | energy_type==Common.LAYER_CELL)
              //    if(location_type != Common.OUTER_CELL)
                  {
                      coordinate_X   = new Double(st.nextToken()).doubleValue();
                      coordinate_Y   = new Double(st.nextToken()).doubleValue();
                      coordinate_Z   = new Double(st.nextToken()).doubleValue();
                      
                      if(coordinate_Z>=min_coord_Z & coordinate_Z<=max_coord_Z)
                      if(first_cell)
                      {
                          current_coord_Z = coordinate_Z;
                          first_cell = false;
                      }                          
                      
                      if(coordinate_Z>=min_coord_Z & coordinate_Z<=max_coord_Z)
                      if(coordinate_Z != current_coord_Z)
                      {
                          aver_eff_stress  = Math.abs(aver_eff_stress)/plane_cell_number;
                          aver_abs_moment  = aver_abs_moment/plane_cell_number;
                          aver_temperature = aver_temperature/plane_cell_number;
                          aver_prn_stress  = Math.abs(aver_prn_stress)/plane_cell_number;
                          
                          aver_tors_velocity = aver_tors_velocity/plane_cell_number;
                          aver_tors_angle    = aver_tors_angle/plane_cell_number;
                          
                          // Transition of coordinate from meters to mcm
                          current_coord_Z = current_coord_Z*cell_size*1.0E6;
                          
                          // Transition of stress from Pa to MPa
                          aver_eff_stress = aver_eff_stress*1.0E-6;
                          aver_prn_stress = aver_prn_stress*1.0E-6;
                          
                          // Transition of couple force from Pa to TPa
                          aver_abs_moment = aver_abs_moment*1.0E-12;
                          
                          aver_tors_velocity = aver_tors_velocity*1.0E9;
                          aver_tors_angle    = aver_tors_angle*1.0E12;
                          
                          bw1.write(current_coord_Z+" "+aver_eff_stress);// aver_tors_velocity);// +" "+plane_cell_number);
                          bw1.newLine();
                          bw1.flush();                          
                          
                          bw2.write(current_coord_Z+" "+aver_abs_moment);// aver_tors_angle);// +" "+plane_cell_number);
                          bw2.newLine();
                          bw2.flush();
                          
                          bw3.write(current_coord_Z+" "+aver_temperature);
                          bw3.newLine();
                          bw3.flush();
                          
                //          bw3.write(current_coord_Z+" "+aver_prn_stress);//+" "+plane_cell_number);
                  //        bw3.newLine();
                    //      bw3.flush();
                          
                          System.out.println("Plane coordinate Z = "+current_coord_Z+" mcm; aver_eff_stress = "+aver_eff_stress+
                                             " MPa; aver_abs_moment = "+aver_abs_moment+" TPa; aver_temperature = "+aver_temperature+" K;"+
                                             " cell number: "+plane_cell_number+".");
                          
                     //     System.out.println("Plane coordinate Z = "+current_coord_Z+" mcm; aver_tors_velocity = "+aver_tors_velocity+
                       //                      "; aver_tors_angle = "+aver_tors_angle+" cell number: "+plane_cell_number+".");
                          
                          plane_cell_number = 0;
                          aver_eff_stress   = 0;
                          aver_prn_stress   = 0;
                          aver_abs_moment   = 0;
                          aver_temperature  = 0;
                          
                          aver_tors_velocity = 0;
                          aver_tors_angle = 0;
                          current_coord_Z   = coordinate_Z;
                      }
                      
                      if((coordinate_Z>=min_coord_Z)&(coordinate_Z<=max_coord_Z))
                      {
                          temperature    = new Double(st.nextToken()).doubleValue();
                          eff_stress     = new Double(st.nextToken()).doubleValue();
                          prn_stress     = new Double(st.nextToken()).doubleValue();
                          mom_X          = new Double(st.nextToken()).doubleValue();
                          mom_Y          = new Double(st.nextToken()).doubleValue();
                          mom_Z          = new Double(st.nextToken()).doubleValue();
                   //       state        = new Byte(st.nextToken()).byteValue();
                     //     strain       = new Double(st.nextToken()).doubleValue();
                          
                          abs_moment = Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                          
                          tors_velocity = Math.sqrt(temperature*temperature + eff_stress*eff_stress + prn_stress*prn_stress);
                          tors_angle    = Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                          
                          aver_eff_stress  += eff_stress;
                          aver_prn_stress  += prn_stress;
                          aver_abs_moment  += abs_moment;
                          aver_temperature += temperature;
                          
                          aver_tors_velocity += tors_velocity;
                          aver_tors_angle    += tors_angle;
                                  
                          plane_cell_number++;                     
                            
                          /*
                          if(mom_Z!=0)
                          {
                              log10_mom_Z = Math.abs(mom_Z)/mom_Z*(31+Math.log10(Math.abs(mom_Z)));
                              
                              if(log10_mom_Z/mom_Z<0)
                              {
                                  log10_mom_Z = 0;
                                  System.out.println("The formula for calculation of <<log10_mom_Z>> is not correct!!!");
                              }
                          }  
                          */
                          
                          total_cell_number++;
                          total_aver_prn_stress += prn_stress;
                          total_aver_eff_stress += eff_stress;
                          total_aver_abs_moment += abs_moment;
                          
                          total_aver_tors_velocity += tors_velocity;
                          total_aver_tors_angle    += tors_angle;
                      }
                  }
              }
          }
          
          coordinate_Z = current_coord_Z;
                  
          if(coordinate_Z>=min_coord_Z & coordinate_Z<=max_coord_Z)
          {
            aver_eff_stress  = Math.abs(aver_eff_stress)/plane_cell_number;
            aver_prn_stress  = Math.abs(aver_prn_stress)/plane_cell_number;
            aver_abs_moment  = aver_abs_moment/plane_cell_number;
            aver_temperature = aver_temperature/plane_cell_number;
            
            aver_tors_velocity = aver_tors_velocity/plane_cell_number;
            aver_tors_angle    = aver_tors_angle/plane_cell_number;                                  
            
            // Transition of coordinate from meters to mcm
            current_coord_Z = current_coord_Z*cell_size*1.0E6;
            
            // Transition of stress from Pa to MPa
            aver_eff_stress = aver_eff_stress*1.0E-6;
            aver_prn_stress = aver_prn_stress*1.0E-6;
            
            // Transition of couple force from Pa to TPa
            aver_abs_moment = aver_abs_moment*1.0E-12;
            
            aver_tors_velocity = aver_tors_velocity*1.0E9;
            aver_tors_angle    = aver_tors_angle*1.0E12;
          
            bw1.write(current_coord_Z+" "+aver_eff_stress);// aver_tors_velocity);//+" "+plane_cell_number);//
            bw1.newLine();
            bw1.flush();
          
            bw2.write(current_coord_Z+" "+aver_abs_moment);// aver_tors_angle);//+" "+plane_cell_number);
            bw2.newLine();
            bw2.flush();
          
            bw3.write(current_coord_Z+" "+aver_temperature);// aver_prn_stress);//+" "+plane_cell_number);
            bw3.newLine();
            bw3.flush();
            
                 System.out.println("Plane coordinate Z = "+current_coord_Z+" mcm; aver_eff_stress = "+aver_eff_stress+
                                    " MPa; aver_abs_moment = "+aver_abs_moment+" TPa; aver_temperature = "+aver_temperature+" K;"+
                                    " cell number: "+plane_cell_number+".");                       
            
        //    System.out.println("Plane coordinate Z = "+current_coord_Z+" mcm; aver_tors_velocity = "+aver_tors_velocity+
          //                     "; aver_tors_angle = "+aver_tors_angle+" cell number: "+plane_cell_number+".");
          }
                          
          total_aver_eff_stress = total_aver_eff_stress/total_cell_number;
          total_aver_prn_stress = total_aver_prn_stress/total_cell_number;
          total_aver_abs_moment = total_aver_abs_moment/total_cell_number; 
          
          // Transition of stress from Pa to GPa
          total_aver_eff_stress = total_aver_eff_stress*1.0E-9;
          total_aver_prn_stress = total_aver_prn_stress*1.0E-9;
          
          // Transition of couple force from Pa to TPa
          total_aver_abs_moment = total_aver_abs_moment*1.0E-12;
          
          System.out.println("File "+read_file_name+" is read!");
          System.out.println("File "+write_file_name_1+" is created!");
          System.out.println("File "+write_file_name_2+" is created!");
        //  System.out.println("File "+write_file_name_3+" is created!");
          System.out.println(" Total cell number: "+total_cell_number+"; total average effective stress: "+total_aver_eff_stress+" MPa.");
          System.out.println(" Total cell number: "+total_cell_number+"; total average principal stress: "+total_aver_prn_stress+" MPa.");
          System.out.println(" Total cell number: "+total_cell_number+"; total average absolute moment:  "+total_aver_abs_moment+" TPa.");
          
          System.out.println(" Total cell number: "+total_cell_number+"; total average torsion velocity: "+total_aver_tors_velocity+".");
          System.out.println(" Total cell number: "+total_cell_number+"; total average torsion angle:    "+total_aver_tors_angle+".");
          
          br.close();
          
          bw1.close();
          bw2.close();
      //    bw3.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates files for graphs "eff_stress-time", "moment_X-time" and "moment_Z-time"
     * for certain point.
     * @param point_coord_X coordinate X of considered point
     * @param point_coord_Y coordinate Y of considered point
     * @param point_coord_Z coordinate Z of considered point
     */
    private void createTimeGraphs(double point_coord_X, double point_coord_Y, double point_coord_Z)
    {
        String string, token_1;

        // Cell parameters:
        // 0. energy type; 1. location type; 2. grain index;
        // 3-5. 3 coordinates (in cell sizes); 6. temperature;
        // 7. effective stress; 8. principal stress;
        // 9-11. 3 components of force moment vector;
        // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
        // 13. strain.
        int energy_type;
        int location_type;
        int grain_index;
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        double temperature;
        double eff_stress = 0;
        double prn_stress = 0;

        double mom_X = 0;
        double mom_Y = 0;
        double mom_Z = 0;

        double log10_mom_X = 0;
        double log10_mom_Y = 0;
        double log10_mom_Z = 0;

        byte   state = 0;
        double strain = 0;

        int file_counter = 0;

        // Exponent of power of 10 in standard representation of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(max_step_number));

        // Exponent of power of 10 in standard representation of number of current time step
        int exponent = 0;

        // String of zeros
        String zeros = "";
        
        String write_file_name_2;
        String write_file_name_3;
        String write_file_name_4;
        String write_file_name_5;
        String write_file_name_6;
        String write_file_name_7;
        
        String write_file_name_8;
        String write_file_name_9;
        
        BufferedWriter bw, bw2, bw3, bw4, bw5, bw6, bw7, bw8, bw9;

        try
        {
          write_file_name = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_eff_stresses.txt";
          write_file_name_2 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_moments_X.txt";
          write_file_name_3 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_moments_Z.txt";
          write_file_name_4 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_moments_Y.txt";
          write_file_name_5 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_log10_moments_X.txt";
          write_file_name_6 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_log10_moments_Z.txt";
          write_file_name_7 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_log10_moments_Y.txt";
          
          write_file_name_8 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_t_XY.txt";
          write_file_name_9 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+point_coord_X+"_Y="+point_coord_Y+"_Z="+point_coord_Z+"_t_YX.txt";
          
          bw  = new BufferedWriter(new FileWriter(write_file_name));
          bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
          bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
          bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
          bw5 = new BufferedWriter(new FileWriter(write_file_name_5));
          bw6 = new BufferedWriter(new FileWriter(write_file_name_6));
          bw7 = new BufferedWriter(new FileWriter(write_file_name_7));
          
          bw8 = new BufferedWriter(new FileWriter(write_file_name_6));
          bw9 = new BufferedWriter(new FileWriter(write_file_name_7));
          
          for(int time_step_counter = 0; time_step_counter <= max_step_number*84/100; time_step_counter+=record_period)
          {
            zeros = "";
            
            if(time_step_counter > 0)
                exponent     = (int)Math.floor(Math.log10(time_step_counter));
            
            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";
            
            read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+time_step_counter+".res";
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            
            while(br.ready())
            {
              string = br.readLine();
              StringTokenizer st = new StringTokenizer(string);

              token_1 = st.nextToken();

              if(!token_1.equals("#"))
              if(st.hasMoreTokens())
              {
                  // Reading of coordinates and physical parameters of current cell
                  energy_type    = new Integer(token_1).intValue();
                  location_type  = new Integer(st.nextToken()).intValue();
                  grain_index    = new Integer(st.nextToken()).intValue();

                  if((energy_type==0)|(energy_type==1))
                  {
                      coordinate_X   = new Double(st.nextToken()).doubleValue();
                      coordinate_Y   = new Double(st.nextToken()).doubleValue();
                      coordinate_Z   = new Double(st.nextToken()).doubleValue();

                      if((coordinate_X-0.5<point_coord_X)&(coordinate_X+0.5>point_coord_X))
                      if((coordinate_Y-0.5<point_coord_Y)&(coordinate_Y+0.5>point_coord_Y))
                      if((coordinate_Z-0.5<point_coord_Z)&(coordinate_Z+0.5>point_coord_Z))
                      {
                          temperature    = new Double(st.nextToken()).doubleValue();
                          eff_stress     = new Double(st.nextToken()).doubleValue();
                          prn_stress     = new Double(st.nextToken()).doubleValue();
                          mom_X          = new Double(st.nextToken()).doubleValue();
                          mom_Y          = new Double(st.nextToken()).doubleValue();
                          mom_Z          = new Double(st.nextToken()).doubleValue();
                          state          = new Byte(st.nextToken()).byteValue();
                          strain         = new Double(st.nextToken()).doubleValue();

                          file_counter++;


                          if(mom_X!=0)
                          {
                              log10_mom_X = Math.abs(mom_X)/mom_X*(31+Math.log10(Math.abs(mom_X)));

                              if(log10_mom_X/mom_X<0)
                              {
                                  log10_mom_X = 0;
                                  System.out.println("The formula for calculation of <<log10_mom_X>> is not correct!!!");
                              }
                          }

                          if(mom_Y!=0)
                          {
                              log10_mom_Y = Math.abs(mom_Y)/mom_Y*(31+Math.log10(Math.abs(mom_Y)));

                              if(log10_mom_Y/mom_Y<0)
                              {
                                  log10_mom_Y = 0;
                                  System.out.println("The formula for calculation of <<log10_mom_Y>> is not correct!!!");
                              }
                          }

                          if(mom_Z!=0)
                          {
                              log10_mom_Z = Math.abs(mom_Z)/mom_Z*(31+Math.log10(Math.abs(mom_Z)));

                              if(log10_mom_Z/mom_Z<0)
                              {
                                  log10_mom_Z = 0;
                                  System.out.println("The formula for calculation of <<log10_mom_Z>> is not correct!!!");
                              }
                          }

                          bw.write(time_step_counter*time_step+" "+eff_stress);
                          bw.newLine();
                          bw.flush();

                          bw2.write(time_step_counter*time_step+" "+mom_X);
                          bw2.newLine();
                          bw2.flush();

                          bw3.write(time_step_counter*time_step+" "+mom_Z);
                          bw3.newLine();
                          bw3.flush();

                          bw4.write(time_step_counter*time_step+" "+mom_Y);
                          bw4.newLine();
                          bw4.flush();

                          bw5.write(time_step_counter*time_step+" "+log10_mom_X);
                          bw5.newLine();
                          bw5.flush();

                          bw6.write(time_step_counter*time_step+" "+log10_mom_Z);
                          bw6.newLine();
                          bw6.flush();

                          bw7.write(time_step_counter*time_step+" "+log10_mom_Y);
                          bw7.newLine();
                          bw7.flush();
                      }
                  }
              }
            }
            
         //   System.out.println("File "+read_file_name+" is read!");
            br.close();
          }

          System.out.println("Number of considered files: "+file_counter);
          System.out.println("File "+write_file_name+" is changed!");
          System.out.println("File "+write_file_name_2+" is changed!");
          System.out.println("File "+write_file_name_3+" is changed!");
          System.out.println("File "+write_file_name_4+" is changed!");
          System.out.println("File "+write_file_name_5+" is changed!");
          System.out.println("File "+write_file_name_6+" is changed!");
          System.out.println("File "+write_file_name_7+" is changed!");
          System.out.println();

          bw.close();
          bw2.close();
          bw3.close();
          bw4.close();
          bw5.close();
          bw6.close();
          bw7.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file for graph "average absolute value - time".
     * @param file_number number of considered files
     * @param _max_step_number maximal number of considered time step
     * @param cell_group name of considered group of cells in specimen
     */
    private void createForceMomentTimeGraphs(int file_number, int _max_step_number, String cell_group)
    {
        // Time step value (sec)
        time_step = 1.0E-9;
        
        // Cell diameter in meters
        double cell_size = 1.0E-7;
        
        System.out.println("The method for creation of file for graph 'average absolute value - time' is started.");
        
        String string, token_1;

        // Cell parameters:
        // 0. energy type; 1. location type; 2. grain index;
        // 3-5. 3 coordinates (in cell sizes); 6. temperature;
        // 7. effective stress; 8. principal stress;
        // 9-11. 3 components of force moment vector;
        // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
        // 13. strain.
        int energy_type;
        int location_type;
        int grain_index;
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        double temperature;
        double eff_stress = 0;
        double prn_stress = 0;

        double mom_X = 0;
        double mom_Y = 0;
        double mom_Z = 0;

        byte   state = 0;
        double strain = 0;
        
        double cur_time     = 0;
        double aver_mom_X   = 0;
        double aver_mom_Y   = 0;
        double aver_mom_Z   = 0;
        double aver_abs_mom = 0;
        double aver_tors_vel = 0;
        
        double aver_tempr      = 0;
        double aver_eff_stress = 0;
        double aver_pr_stress  = 0;
        
        int cell_number = 0;

        // Coordinates Y of cell layers near front and back facets 
        double max_front_coord_Y = 2.25;
        double min_back_coord_Y = 10.25;
        
        double min_coordinate_Z =  9.0; //  4.5; // 
        double max_coordinate_Z = 42.5; // 47.5; // 
        
        double centre_coord_X = 26.0;
        double centre_coord_Y = 11.25;
        
        double radius = 2.5;
        
        double dist_from_centre_X = 0;
        double dist_from_centre_Y = 0;        
        
        //  int file_counter = 0;
        
        // Exponent of power of 10 in standard representation of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(_max_step_number));

        // Exponent of power of 10 in standard representation of number of current time step
        int exponent = 0;
        
        // String of zeros
        String zeros = "";
        
        String write_file_name_abs_mom;
        String write_file_name_mom_X;
        String write_file_name_mom_Y;
        String write_file_name_mom_Z;
        String write_file_name_tempr;
        String write_file_name_eff_str;
        String write_file_name_pr_str;
        String write_file_name_tors_vel;
        
        BufferedWriter bw_abs_mom;
        BufferedWriter bw_mom_X;
        BufferedWriter bw_mom_Y;
        BufferedWriter bw_mom_Z;
        BufferedWriter bw_tempr;
        BufferedWriter bw_eff_str;
        BufferedWriter bw_pr_str;
        BufferedWriter bw_tors_vel;
        
        int file_step = 1;

        try
        {
          System.out.println("\nTask name: "+task_name+"\nCell group name: "+cell_group);
        
          write_file_name_abs_mom = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_spec_vol_abs_mom__time.txt";
          write_file_name_mom_X   = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_spec_vol_mom_X__time.txt";
          write_file_name_mom_Y   = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_spec_vol_mom_Y__time.txt";
          write_file_name_mom_Z   = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_spec_vol_mom_Z__time.txt";
          write_file_name_tempr   = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_tempr__time.txt";
          write_file_name_eff_str = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_eff_str__time.txt";
          write_file_name_pr_str  = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_pr_str__time.txt";
     //     write_file_name_tors_vel = "./user/task_db/"+task_name+"/time_graphs/"+task_name+"_"+cell_group+"_aver_abs_tors_vel__time.txt";
          
          bw_abs_mom  = new BufferedWriter(new FileWriter(write_file_name_abs_mom));
          bw_mom_X  = new BufferedWriter(new FileWriter(write_file_name_mom_X));
          bw_mom_Y  = new BufferedWriter(new FileWriter(write_file_name_mom_Y));
          bw_mom_Z  = new BufferedWriter(new FileWriter(write_file_name_mom_Z));
          bw_tempr    = new BufferedWriter(new FileWriter(write_file_name_tempr));
          bw_eff_str  = new BufferedWriter(new FileWriter(write_file_name_eff_str));
          bw_pr_str   = new BufferedWriter(new FileWriter(write_file_name_pr_str));
       //   bw_tors_vel = new BufferedWriter(new FileWriter(write_file_name_tors_vel));
          
          file_step = 1;
          
          for(int file_counter = 0; file_counter <= file_number; file_counter+=file_step)
          {
          //  if(file_counter == 10)
            //    file_step = 10;
              
            zeros = "";

            if(file_counter > 0)
                exponent     = (int)Math.floor(Math.log10(file_counter*_max_step_number/file_number));

            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";

            read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+file_counter*_max_step_number/file_number+".res";
       //     read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+file_counter*_max_step_number/file_number+"_torsion.res";
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            
            cell_number     = 0;
            aver_abs_mom    = 0;
            aver_mom_X      = 0;
            aver_mom_Y      = 0;
            aver_mom_Z      = 0;
            aver_tempr      = 0;
            aver_eff_stress = 0;
            aver_pr_stress  = 0;
            aver_tors_vel   = 0;

            while(br.ready())
            {
              string = br.readLine();
              StringTokenizer st = new StringTokenizer(string);

              token_1 = st.nextToken();

              if(!token_1.equals("#"))
              if(st.hasMoreTokens())
              {
                  // Reading of coordinates and physical parameters of current cell
                  energy_type    = new Integer(token_1).intValue();
                  location_type  = new Integer(st.nextToken()).intValue();
                  grain_index    = new Integer(st.nextToken()).intValue();
                  
                  if(cell_group.equals(INTERIOR_CELLS))
                  {
                      if(energy_type==Common.LAYER_CELL)// | energy_type==Common.INNER_CELL)// 
                      // if(location_type != Common.OUTER_CELL)
                  //    if(location_type == Common.INTRAGRANULAR_CELL | location_type == Common.INTERGRANULAR_CELL)
                      {
                          coordinate_X   = new Double(st.nextToken()).doubleValue();
                          coordinate_Y   = new Double(st.nextToken()).doubleValue();
                          coordinate_Z   = new Double(st.nextToken()).doubleValue();
                          
                     //     dist_from_centre_X = coordinate_X - centre_coord_X;
                     //     dist_from_centre_Y = coordinate_Y - centre_coord_Y;        
                          
                       //   if(coordinate_Z >= min_coordinate_Z & coordinate_Z <= max_coordinate_Z)
                       //   if(dist_from_centre_X*dist_from_centre_X + dist_from_centre_Y*dist_from_centre_Y < radius*radius)
                          {
                            temperature    = new Double(st.nextToken()).doubleValue();
                            eff_stress     = new Double(st.nextToken()).doubleValue();
                            prn_stress     = new Double(st.nextToken()).doubleValue();
                            mom_X          = new Double(st.nextToken()).doubleValue();
                            mom_Y          = new Double(st.nextToken()).doubleValue();
                            mom_Z          = new Double(st.nextToken()).doubleValue();
                       //     state          = new Byte(st.nextToken()).byteValue();
                       //     strain         = new Double(st.nextToken()).doubleValue();                      
                            
                            cell_number++;
                            aver_abs_mom   += Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                            aver_mom_X     += mom_X;
                            aver_mom_Y     += mom_Y;
                            aver_mom_Z     += mom_Z;
                          
                            aver_tempr     += temperature;
                            aver_eff_stress+= eff_stress;
                            aver_pr_stress += prn_stress;
                          }
                      }
                  }
                  
                  if(cell_group.equals(ALL_SPECIMEN_CELLS))
                  {
                //      if(energy_type==Common.LAYER_CELL | energy_type==Common.INNER_CELL)
                      if(location_type != Common.OUTER_CELL)
                      {
                          coordinate_X   = new Double(st.nextToken()).doubleValue();
                          coordinate_Y   = new Double(st.nextToken()).doubleValue();
                          coordinate_Z   = new Double(st.nextToken()).doubleValue();
                          temperature    = new Double(st.nextToken()).doubleValue();
                          eff_stress     = new Double(st.nextToken()).doubleValue();
                          prn_stress     = new Double(st.nextToken()).doubleValue();
                          mom_X          = new Double(st.nextToken()).doubleValue();
                          mom_Y          = new Double(st.nextToken()).doubleValue();
                          mom_Z          = new Double(st.nextToken()).doubleValue();
                          state          = new Byte(st.nextToken()).byteValue();
                          strain         = new Double(st.nextToken()).doubleValue();                      
                          
                          cell_number++;
                          aver_abs_mom   += Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                     //     aver_mom_X     += mom_X*1.0E300;
                     //     aver_mom_Y     += mom_Y*1.0E300;
                     //     aver_mom_Z     += mom_Z*1.0E300;
                          
                          aver_tempr     += temperature;
                          aver_eff_stress+= eff_stress;
                          aver_pr_stress += prn_stress;
                      }
                  }
                  
                  if(cell_group.equals(FRONT_AND_BACK_CELLS))
                  {
                      if(energy_type==Common.LAYER_CELL | energy_type==Common.INNER_CELL)
                      if(location_type == Common.INNER_BOUNDARY_CELL | location_type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
                      {
                          coordinate_X   = new Double(st.nextToken()).doubleValue();
                          coordinate_Y   = new Double(st.nextToken()).doubleValue();
                          coordinate_Z   = new Double(st.nextToken()).doubleValue();
                      
                          if(coordinate_Y < max_front_coord_Y | coordinate_Y > min_back_coord_Y)
                          {
                              temperature    = new Double(st.nextToken()).doubleValue();
                              eff_stress     = new Double(st.nextToken()).doubleValue();
                              prn_stress     = new Double(st.nextToken()).doubleValue();
                              mom_X          = new Double(st.nextToken()).doubleValue();
                              mom_Y          = new Double(st.nextToken()).doubleValue();
                              mom_Z          = new Double(st.nextToken()).doubleValue();
                              state          = new Byte(st.nextToken()).byteValue();
                              strain         = new Double(st.nextToken()).doubleValue();
                              
                              cell_number++;
                              aver_abs_mom   += Math.sqrt(mom_X*mom_X + mom_Y*mom_Y + mom_Z*mom_Z);
                         //     aver_mom_X     += mom_X*1.0E300;
                         //     aver_mom_Y     += mom_Y*1.0E300;
                         //     aver_mom_Z     += mom_Z*1.0E300;
                              
                              aver_tempr     += temperature;
                              aver_eff_stress+= eff_stress;
                              aver_pr_stress += prn_stress;
                          }
                      }
                  }
              }
            }
            
            // Cell volume in cubic meters
            double cell_volume_HCP = 1; // calcCellVolumeHCP(cell_size); // 
        
            cur_time         = file_counter*_max_step_number/file_number*time_step;            
            
            aver_abs_mom     = aver_abs_mom/(cell_number*cell_volume_HCP);
            aver_mom_X       = aver_mom_X/(cell_number*cell_volume_HCP);
            aver_mom_Y       = aver_mom_Y/(cell_number*cell_volume_HCP);
            aver_mom_Z       = aver_mom_Z/(cell_number*cell_volume_HCP);
            aver_tempr       = aver_tempr/cell_number;
            aver_eff_stress  = aver_eff_stress/cell_number;
            aver_pr_stress   = aver_pr_stress/cell_number;
            
            // Transition from seconds to microseconds
       //     cur_time         = cur_time*1.0E6;
            cur_time = (int)Math.round(cur_time*1.0E12)/1000;
            
            int cur_time_int = (int)Math.round(cur_time);
            
            String cur_time_mantissa = "";
            
            if(cur_time%1000 >= 100)
                cur_time_mantissa = new String(""+cur_time_int%1000);
            
            if(cur_time%1000 < 100 & cur_time%1000 >= 10)
                cur_time_mantissa = new String("0"+cur_time_int%1000);
            
            if(cur_time%1000 < 10)
                cur_time_mantissa = new String("00"+cur_time_int%1000);
                
            String cur_time_string = cur_time_int/1000 +"."+ cur_time_mantissa;
            
            // Transition from Pa to GPa
            aver_abs_mom     = aver_abs_mom*1.0E-9;
            
            // Transition from Pa to GPa
            aver_mom_X       = aver_mom_X*1.0E-9;
            aver_mom_Y       = aver_mom_Y*1.0E-9;
            aver_mom_Z       = aver_mom_Z*1.0E-9;
            
            // Transition from radians/s to microradians/s
        //    aver_abs_mom = aver_abs_mom*1.0E6;
            
            // Transition from Pa to MPa
            aver_eff_stress  = aver_eff_stress*1.0E-6;
            
            // Transition from Pa to MPa
            aver_pr_stress   = aver_pr_stress*1.0E-6;
            
            bw_abs_mom.write(cur_time_string+" "+aver_abs_mom);
            bw_abs_mom.newLine();
            bw_abs_mom.flush();
                    
            bw_mom_X.write(cur_time_string+" "+aver_mom_X);
            bw_mom_X.newLine();
            bw_mom_X.flush();
            
            bw_mom_Y.write(cur_time_string+" "+aver_mom_Y);
            bw_mom_Y.newLine();
            bw_mom_Y.flush();
            
            bw_mom_Z.write(cur_time_string+" "+aver_mom_Z);
            bw_mom_Z.newLine();
            bw_mom_Z.flush();
            
            bw_tempr.write(cur_time_string+" "+aver_tempr);
            bw_tempr.newLine();
            bw_tempr.flush();
            
            bw_eff_str.write(cur_time_string+" "+(-aver_eff_stress));
            bw_eff_str.newLine();
            bw_eff_str.flush();
            
            bw_pr_str.write(cur_time_string+" "+aver_pr_stress);
            bw_pr_str.newLine();
            bw_pr_str.flush();
            
         //   bw_tors_vel.write(cur_time_int+".0 "+aver_abs_mom);
           // bw_tors_vel.newLine();
          //  bw_tors_vel.flush();
            
            System.out.println("File # "+file_counter+"; cell number: "+cell_number+"; current time: "+cur_time_string+" mcs; "
                              +"aver_mom_X: "+aver_mom_X+" GPa; aver_mom_Y: "+aver_mom_Y+" GPa; aver_mom_Z: "+aver_mom_Z+" GPa; "
                              +"aver_abs_mom: "+aver_abs_mom+" GPa; aver_eff_stress: "+(-aver_eff_stress)+" MPa; aver_tempr: "+aver_tempr+" K.");
            
        //    System.out.println("File # "+file_counter+"; cell number: "+cell_number+"; current time: "+cur_time_int+".0 ns; aver_abs_tors_vel: "+aver_abs_mom);
            
            br.close();
          }
          
          bw_abs_mom.close();
          bw_mom_X.close();
          bw_mom_Y.close();
          bw_mom_Z.close();
          bw_tempr.close();
          bw_eff_str.close();
          bw_pr_str.close();
        //  bw_tors_vel.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method calculates values of porosity for all cells
     * and writes them to output files.
     */
    private void calcCellPorosityValues()
    {
        String string, token_1;
        
        // Coordinates (in cell sizes), state, stress and strain of cell
        int energy_type;
        int location_type;
        int grain_index;
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double temperature;
        double stress = 0;        
        double pr_stress=0;
        double force_mom_X=0;
        double force_mom_Y=0;
        double force_mom_Z=0;
        int state=0;
        double strain = 0;
        
        // Probability of pore appearance in cell
        double pore_prob = 0;
        
        // State of pore in cell
        double pore_state = -1;

        // Random value from 0 to 1.
        double rand = 0;
        
        // Cell strain value corresponding to 0% pore generation probability
        double min_strain = 0.009;

        // Cell strain value corresponding to 100% pore generation probability
        double max_strain = 0.012;
        
        int file_number   = 28;
        int record_period = 50000;
        int cell_number   = 0;

        try
        { 
          for(int file_counter=25; file_counter<=file_number; file_counter++)
          {            
            cell_number  = 0;
            
            // Exponent of power of 10 in standard representation
            // of total number of time steps
            int max_exponent = (int)Math.floor(Math.log10(max_step_number));

            // Exponent of power of 10 in standard representation
            // of number of current time step
            int exponent = 0;

            if(file_counter*record_period > 0)
                exponent     = (int)Math.floor(Math.log10(file_counter*record_period));

            // String of zeros
            String zeros = "";

            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";

            read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+file_counter*record_period+".res";
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            
            write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+file_counter*record_period+"_pores.res";
            BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));
            
            while(br.ready())
            {
                string = br.readLine();
                
                StringTokenizer st = new StringTokenizer(string);
                
                token_1 = st.nextToken();
                
                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = new Integer(token_1).intValue();
                    location_type  = new Integer(st.nextToken()).intValue();
                    grain_index    = new Integer(st.nextToken()).intValue();
                    
              //      if((energy_type==0)|(energy_type==1))
                    {
                        coordinate_X   = new Double(st.nextToken()).doubleValue();
                        coordinate_Y   = new Double(st.nextToken()).doubleValue();
                        coordinate_Z   = new Double(st.nextToken()).doubleValue();
                        temperature    = new Double(st.nextToken()).doubleValue();
                        stress         = new Double(st.nextToken()).doubleValue();                        
                        pr_stress      = new Double(st.nextToken()).doubleValue();                        
                        force_mom_X    = new Double(st.nextToken()).doubleValue();
                        force_mom_Y    = new Double(st.nextToken()).doubleValue();
                        force_mom_Z    = new Double(st.nextToken()).doubleValue();
                        state          = new Integer(st.nextToken()).intValue();
                        strain         = new Double(st.nextToken()).doubleValue();

                        cell_number++;
                        
                        // Pore cannot be generated in surface layer.
                        // Pore is generated when strain is in the interval between low and high threshold values.
                        if((strain>=min_strain)&(strain<=max_strain)&(grain_index != 1)&(grain_index != 4))
                        {
                            if(strain < (min_strain+max_strain)/2)
                                pore_prob = 2*(strain - min_strain)/(max_strain - min_strain);
                            else
                                pore_prob = 2*(max_strain - strain)/(max_strain - min_strain);
                            
                            pore_prob = Math.min(1, pore_prob);
                            pore_prob = Math.max(0, pore_prob);

                            rand = Math.random();

                            // Stochastic choice of cell state according to probability of pore generation
                            if(rand < pore_prob)
                                pore_state =-1;
                            else
                                pore_state = 1;
                        }
                        else
                        {
                            pore_prob = 0;
                            pore_state = 1;
                        }

                        
                        bw.write(energy_type+" "+location_type+" "+grain_index+" "+coordinate_X+" "+coordinate_Y+" "+coordinate_Z+" ");
                        bw.write(temperature+" "+stress+" "+pr_stress+" "+force_mom_X+" "+force_mom_Y+" "+force_mom_Z+" "+state+" "+pore_state+" 0.0");
                        bw.newLine();
                        bw.flush();
                    }
                }      
            }
            
            System.out.println("File # "+file_counter+" is read!");
            br.close();

            System.out.println("Number of considered cells: "+cell_number);
            System.out.println("File # "+file_counter+" is changed!");
            
            bw.close();
          }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method calculates absolute values of force moments for cells
      * in certain plane perpendicular to chosen coordinate axis and writes them to output files.
      * @param plane_coord coordinate of chosen plane
      */
    private void calcAbsMomentsInPlane(double plane_coord)
    {
        String string, token_1;

        // Coordinates (in cell sizes), state, stress and strain of cell
        int energy_type;
        int location_type;
        int grain_index;
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double temperature;
        double stress = 0;
        double pr_stress=0;
        double force_mom_X=0;
        double force_mom_Y=0;
        double force_mom_Z=0;
        int    state = 0;
        double strain = 0;

        int file_number   = 100;
        int record_period = 1000;
        int cell_number   = 0;

        double abs_mom_XZ = 0;

        try
        {
          for(int file_counter=0; file_counter<=file_number; file_counter++)
          {
            cell_number  = 0;

            // Exponent of power of 10 in standard representation
            // of total number of time steps
            int max_exponent = (int)Math.floor(Math.log10(max_step_number));

            // Exponent of power of 10 in standard representation
            // of number of current time step
            int exponent = 0;

            if(file_counter*record_period > 0)
                exponent     = (int)Math.floor(Math.log10(file_counter*record_period));

            // String of zeros
            String zeros = "";

            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";

            read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+file_counter*record_period+".res";
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));

            write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_abs_moments_XZ/"+task_name+"_"+zeros+file_counter*record_period+"_abs_moments_XZ.txt";
            BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));

            while(br.ready())
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                token_1 = st.nextToken();

                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = new Integer(token_1).intValue();
                    location_type  = new Integer(st.nextToken()).intValue();
                    grain_index    = new Integer(st.nextToken()).intValue();

                    coordinate_X   = new Double(st.nextToken()).doubleValue();
                    coordinate_Y   = new Double(st.nextToken()).doubleValue();
                    coordinate_Z   = new Double(st.nextToken()).doubleValue();

                    if((energy_type==0)|(energy_type==1))
                    if((coordinate_Y >= plane_coord-0.5)&(coordinate_Y <= plane_coord+0.5))
                    {                        
                        temperature    = new Double(st.nextToken()).doubleValue();
                        stress         = new Double(st.nextToken()).doubleValue();
                        pr_stress      = new Double(st.nextToken()).doubleValue();
                        force_mom_X    = new Double(st.nextToken()).doubleValue();
                        force_mom_Y    = new Double(st.nextToken()).doubleValue();
                        force_mom_Z    = new Double(st.nextToken()).doubleValue();
                        state          = new Integer(st.nextToken()).intValue();
                        strain         = new Double(st.nextToken()).doubleValue();

                        cell_number++;

                        // Calculation of absolute value of 2D vector (force_mom_X, force_mom_Z).
                        abs_mom_XZ = - Math.sqrt(force_mom_X*force_mom_X + force_mom_Z*force_mom_Z);

                        bw.write(coordinate_X+" "+coordinate_Z+" "+abs_mom_XZ);
                        bw.newLine();
                        bw.flush();
                    }
                }
            }

            System.out.println("File # "+file_counter+" is read!");
            br.close();

            System.out.println("Number of considered cells: "+cell_number);
            System.out.println("File # "+file_counter+" is changed!");

            bw.close();
          }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates files for 3D-graphs "strain (length, time)", "stress (length, time)" etc.
      *  in certain direction parallel to chosen axis.
      * @param section_coord_X coordinate X of considered section
      * @param section_coord_Y coordinate X of considered section
      * @param section_coord_Z coordinate X of considered section
     */
    private void createGraphs(double section_coord_X, double section_coord_Y, double section_coord_Z)
    {
        String string, token_1;
        
        // Coordinates (in cell sizes), state, stress and strain of cell
        int energy_type;
        int location_type;
        int grain_index;
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        double temperature;
        double stress = 0;
        double strain = 0;

        // These parameters must correspond to the considered task parameters:
        // 1. Maximal file number
        int file_number   = 100;

        // 2. Period of recording of file (in time steps)
        int record_period = 10000;

        max_step_number = record_period*file_number;

        // 3. The value of time step
        double time_step = 1.0E-8;
        
        double total_stress = 0;
        double total_strain = 0;
        int cell_number = 0;

        double pr_stress=0;
        double force_mom_X=0;
        double force_mom_Y=0;
        double force_mom_Z=0;
        int state=0;

        try
        {
          write_file_name = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_eff_stresses.txt";
          String write_file_name_2 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_strains.txt";
          String write_file_name_3 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_mom_X.txt";
          String write_file_name_4 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_mom_Y.txt";
          String write_file_name_5 = "./user/task_db/"+task_name+"/graphs/"+task_name+"_X="+section_coord_X+"_Y="+section_coord_Y+"_mom_Z.txt";

          BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));
          BufferedWriter bw2 = new BufferedWriter(new FileWriter(write_file_name_2));
          BufferedWriter bw3 = new BufferedWriter(new FileWriter(write_file_name_3));
          BufferedWriter bw4 = new BufferedWriter(new FileWriter(write_file_name_4));
          BufferedWriter bw5 = new BufferedWriter(new FileWriter(write_file_name_5));
          
          for(int file_counter=0; file_counter<=file_number; file_counter++)
          {
            total_stress = 0;
            cell_number  = 0;
            
            // Exponent of power of 10 in standard representation
            // of total number of time steps
            int max_exponent = (int)Math.floor(Math.log10(max_step_number));

            // Exponent of power of 10 in standard representation
            // of number of current time step
            int exponent = 0;

            if(file_counter*record_period > 0)
                exponent     = (int)Math.floor(Math.log10(file_counter*record_period));

            System.out.println();

            // String of zeros
            String zeros = "";

            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";

            read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+file_counter*record_period+".res";
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));

            while(br.ready())
            {
                string = br.readLine();
                
                StringTokenizer st = new StringTokenizer(string);
                
                token_1 = st.nextToken();
                
                if(!token_1.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = new Integer(token_1).intValue();
                    location_type  = new Integer(st.nextToken()).intValue();
                    grain_index    = new Integer(st.nextToken()).intValue();
                    
                    if((energy_type==0)|(energy_type==1))
                    {
                        coordinate_X   = new Double(st.nextToken()).doubleValue();
                        coordinate_Y   = new Double(st.nextToken()).doubleValue();
                        coordinate_Z   = new Double(st.nextToken()).doubleValue();

                        if((coordinate_X-0.5<section_coord_X)&(coordinate_X+0.5>section_coord_X))
                        if((coordinate_Y-0.5<section_coord_Y)&(coordinate_Y+0.5>section_coord_Y))
                        {
                            temperature    = new Double(st.nextToken()).doubleValue();
                            stress         = new Double(st.nextToken()).doubleValue();

                            pr_stress      = new Double(st.nextToken()).doubleValue();
                            force_mom_X    = new Double(st.nextToken()).doubleValue();
                            force_mom_Y    = new Double(st.nextToken()).doubleValue();
                            force_mom_Z    = new Double(st.nextToken()).doubleValue();
                            state          = new Integer(st.nextToken()).intValue();
                            strain         = new Double(st.nextToken()).doubleValue();

                            total_stress+=stress;
                            total_strain+=strain;
                            cell_number++;

                            if(force_mom_X > 0) force_mom_X = 1;
                            else
                            if(force_mom_X < 0) force_mom_X =-1;

                            if(force_mom_Y > 0) force_mom_Y = 1;
                            else
                            if(force_mom_Y < 0) force_mom_Y =-1;

                            if(force_mom_Z > 0) force_mom_Z = 1;
                            else
                            if(force_mom_Z < 0) force_mom_Z =-1;
                            
                            bw.write(file_counter*record_period*time_step+" "+coordinate_Z+" "+stress);
                            bw.newLine();
                            bw.flush();

                            bw2.write(file_counter*record_period*time_step+" "+coordinate_Z+" "+strain);
                            bw2.newLine();
                            bw2.flush();

                            bw3.write(file_counter*record_period*time_step+" "+coordinate_Z+" "+force_mom_X);
                            bw3.newLine();
                            bw3.flush();

                            bw4.write(file_counter*record_period*time_step+" "+coordinate_Z+" "+force_mom_Y);
                            bw4.newLine();
                            bw4.flush();

                            bw5.write(file_counter*record_period*time_step+" "+coordinate_Z+" "+force_mom_Z);
                            bw5.newLine();
                            bw5.flush();
                        }
                    }
                }
            }
            
            System.out.println("File # "+file_counter+" is read!");
            br.close();

            System.out.println("Number of considered cells in the section: "+cell_number);
       //     System.out.println("File # "+file_counter+" is changed!");
          }
          
          bw.close();
          bw2.close();
          bw3.close();
          bw4.close();
          bw5.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file in "XYZ" format.
     */
    public void createFileXYZ()
    {
        String string;

        // Parameters of cell.
        double coordinate_X;
        double coordinate_Y;
        double coordinate_Z;
        int state_value;
        String cell_state = "   ";

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            // Reading of the number of strings of the file from the file
            int string_number = new Integer(br.readLine()).intValue();

            bw.write(string_number+"");
            bw.newLine();
            bw.write("## test");
            bw.newLine();

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state_value  = new Integer(st.nextToken()).intValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();

                    if (state_value ==  0) cell_state = "N  ";
                    if (state_value ==  1) cell_state = "Cu ";
                    if (state_value ==  2) cell_state = "O  ";
                    if (state_value ==  3) cell_state = "Si ";
                    if (state_value ==  4) cell_state = "Fe ";
                    if (state_value ==  5) cell_state = "B  ";
                    if (state_value ==  6) cell_state = "Mg ";
                    if (state_value ==  7) cell_state = "It ";
                    if (state_value ==  8) cell_state = "Cd ";
                    if (state_value ==  9) cell_state = "CA ";
                    if (state_value == 10) cell_state = "Au ";
                    if (state_value == 11) cell_state = "Li ";
                    if (state_value == 12) cell_state = "I  ";
                    if (state_value == 13) cell_state = "Cl ";
                    if (state_value == 14) cell_state = "Br ";//"H  ";
                    if (state_value == 15) cell_state = "Co ";
                    if (state_value == 16) cell_state = "S  ";
                    if (state_value == 17) cell_state = "Na ";
                    if (state_value == 18) cell_state = "Pl ";
                    if (state_value == 19) cell_state = "Mn ";
                    if (state_value == 20) cell_state = "Al ";

                    bw.write(cell_state+" "+coordinate_X+" "+coordinate_Y+" "+coordinate_Z);
                    bw.newLine();
                    bw.flush();
                }
            }

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file in "PDB" format.
     */
    public void createFilePDB()
    {
     //   read_file_name  = "./task1/damaged_volume_and_crack_embryos.txt";
     //   write_file_name = "./task1/damaged_volume_and_crack_embryos.pdb";

        read_file_name  = "./task1/cracks_200.txt";
        write_file_name = "./task1/branching_cracks_200.pdb";

        String string;

        // Coordinates of cell
        double coordinate_X = -1;
        double coordinate_Y = -1;
        double coordinate_Z = -1;

        // Value of cell state
        int state_value = -1;

        // Stress in cell
        double stress;

        // Temperature of cell
        double temperature;

        // Name of cell state
        String cell_state = "H  ";//"N  ";

        // Coordinates of cell multiplied by 10000 and rounded to integer
        int coordinate_X_round;
        int coordinate_Y_round;
        int coordinate_Z_round;

        // Integer parts of cell coordinates
        int coordinate_X_int;
        int coordinate_Y_int;
        int coordinate_Z_int;

        // Fractional parts of cell coordinates
        int coordinate_X_frac;
        int coordinate_Y_frac;
        int coordinate_Z_frac;

        // Number exponents of cell coordinates
        byte num_exp_X;
        byte num_exp_Y;
        byte num_exp_Z;

        try
        {
            BufferedReader br  = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));

            // Reading of the number of strings of the file from the file
          //  int string_number = new Integer(br.readLine()).intValue();
          //  int string_number = 32000;

            // Imaginary cell with supremum of stress
        /*
            bw.write("ATOM         Al                50.0000 60.0000 10.0000      999");
            bw.newLine();
            bw.flush();
         */
       //     for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            while(br.ready())
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                 // Reading of coordinates and physical parameters of current cell
                    state_value  = new Integer(st.nextToken()).intValue();//-1;

               //     stress  = new Double(st.nextToken()).doubleValue();//in Pa

                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();//+8.165;
                //    state_value  = new Integer(st.nextToken()).intValue();//-1;
                //    temperature  = new Double(st.nextToken()).doubleValue();
                //    stress  = new Double(st.nextToken()).doubleValue();//in Pa
                /*
                    coordinate_X = coordinate_X/2;
                    coordinate_Y = coordinate_Y/2;
                    coordinate_Z = coordinate_Z/2;

                    coordinate_X = coordinate_X*2.5E6;
                    coordinate_Y = coordinate_Y*2.5E6;
                    coordinate_Z = coordinate_Z*2.5E6;

                    coordinate_X = coordinate_X*1.25;
                    coordinate_Y = coordinate_Y*1.25;
                    coordinate_Z = coordinate_Z*1.25;
                 */

                //    stress = 1.0*Math.round(stress)/1000.0;//from Pa in kPa

                    if (state_value%24 ==  0) cell_state = "H  ";//"N  ";
                    if (state_value%24 ==  1) cell_state = "Cu ";
                    if (state_value%24 ==  2) cell_state = "O  ";
                    if (state_value%24 ==  3) cell_state = "Si ";
                    if (state_value%24 ==  4) cell_state = "Fe ";
                    if (state_value%24 ==  5) cell_state = "B  ";
                    if (state_value%24 ==  6) cell_state = "Mg ";
                    if (state_value%24 ==  7) cell_state = "It ";
                    if (state_value%24 ==  8) cell_state = "Cd ";
                    if (state_value%24 ==  9) cell_state = "CA ";
                    if (state_value%24 == 10) cell_state = "Au ";
                    if (state_value%24 == 11) cell_state = "Al ";
                    if (state_value%24 == 12) cell_state = "I  ";
                    if (state_value%24 == 13) cell_state = "Cl ";
                    if (state_value%24 == 14) cell_state = "Br ";
                    if (state_value%24 == 15) cell_state = "Co ";
                    if (state_value%24 == 16) cell_state = "S  ";
                    if (state_value%24 == 17) cell_state = "Na ";
                    if (state_value%24 == 18) cell_state = "Pl ";
                    if (state_value%24 == 19) cell_state = "Mn ";
                    if (state_value%24 == 20) cell_state = "Li ";
                    if (state_value%24 == 21) cell_state = "I  ";
                    if (state_value%24 == 22) cell_state = "N  ";
                    if (state_value%24 == 23) cell_state = "Pt ";

                    // Printing of data concerning all grains
            //        if (state_value == 54) // Printing of data concerning one of grains
                    {
                        bw.write("ATOM         ");
                        bw.write(cell_state);
                        bw.write("              ");

                        coordinate_X_round = (int)Math.round(10000*coordinate_X);
                        coordinate_Y_round = (int)Math.round(10000*coordinate_Y);
                        coordinate_Z_round = (int)Math.round(10000*coordinate_Z);

                        // Obtaining of integer parts of cell coordinates
                        coordinate_X_int   = coordinate_X_round/10000;
                        coordinate_Y_int   = coordinate_Y_round/10000;
                        coordinate_Z_int   = coordinate_Z_round/10000;

                        // Obtaining of number exponents of cell coordinates
                        num_exp_X = 3;
                        if(coordinate_X_int/100 == 0) num_exp_X = (byte)(num_exp_X - 1);
                        if(coordinate_X_int/ 10 == 0) num_exp_X = (byte)(num_exp_X - 1);

                        num_exp_Y = 3;
                        if(coordinate_Y_int/100 == 0) num_exp_Y = (byte)(num_exp_Y - 1);
                        if(coordinate_Y_int/ 10 == 0) num_exp_Y = (byte)(num_exp_Y - 1);

                        num_exp_Z = 3;
                        if(coordinate_Z_int/100 == 0) num_exp_Z = (byte)(num_exp_Z - 1);
                        if(coordinate_Z_int/ 10 == 0) num_exp_Z = (byte)(num_exp_Z - 1);

                        // Obtaining of fractional parts of cell coordinates
                        coordinate_X_frac  = coordinate_X_round%10000;
                        coordinate_Y_frac  = coordinate_Y_round%10000;
                        coordinate_Z_frac  = coordinate_Z_round%10000;

                        // Writing of integer part of coordinate Y
                        for(int space_counter = 0; space_counter < 3 - num_exp_Y; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_Y_int+".");

                        // Obtaining of number exponent of fractional part of coordinate Y
                        if(coordinate_Y_frac/10 == 0) num_exp_Y = 1;
                        else
                            if(coordinate_Y_frac/100 == 0) num_exp_Y = 2;
                            else
                                if(coordinate_Y_frac/1000 == 0) num_exp_Y = 3;
                                else
                                    if(coordinate_Y_frac/10000 == 0) num_exp_Y = 4;

                        // Writing of fractional part of coordinate Y
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_Y; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_Y_frac+"");

                       // Writing of integer part of coordinate X
                        for(int space_counter = 0; space_counter < 3 - num_exp_X; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_X_int+".");

                        // Obtaining of number exponent of fractional part of coordinate X
                        if(coordinate_X_frac/10 == 0) num_exp_X = 1;
                        else
                            if(coordinate_X_frac/100 == 0) num_exp_X = 2;
                            else
                                if(coordinate_X_frac/1000 == 0) num_exp_X = 3;
                                else
                                    if(coordinate_X_frac/10000 == 0) num_exp_X = 4;

                        // Writing of fractional part of coordinate X
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_X; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_X_frac+"");

                        // Writing of integer part of coordinate Z
                        for(int space_counter = 0; space_counter < 3 - num_exp_Z; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_Z_int+".");

                        // Obtaining of number exponent of fractional part of coordinate Z
                        if(coordinate_Z_frac/10 == 0) num_exp_Z = 1;
                        else
                            if(coordinate_Z_frac/100 == 0) num_exp_Z = 2;
                            else
                                if(coordinate_Z_frac/1000 == 0) num_exp_Z = 3;
                                else
                                    if(coordinate_Z_frac/10000 == 0) num_exp_Z = 4;

                        // Writing of fractional part of coordinate Z
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_Z; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_Z_frac+"");

                        // Writing of cell state
                        bw.write("      "+state_value);

                        // Writing of cell temperature
                  //      bw.write("      "+Math.round(temperature));

                        // Writing of cell stress
                //        bw.write("      "+Math.round(stress));

                        bw.newLine();
                        bw.flush();
                    }
                }
            }

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file in "PDB" format.
     * Grain boundaries are represented by gray cells if _show_grain_bounds = 1.
     * @param step_number number of time step corresponding to read file
     * @param _show_grain_bounds variable responsible for showing of cells on grain boundaries
     *                           as atoms "H" in "RasMol"
     */
    private void createFilePDB(int step_number, byte _show_grain_bounds)
    {
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(max_step_number));

        // Exponent of power of 10 in standard representation
        // of number of current time step
        int exponent = 0;

        if(step_number > 0)
            exponent     = (int)Math.floor(Math.log10(step_number));

        // String of zeros
        String zeros = "";

        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";

     //   read_file_name  = "./task/recryst_2009_07_01_"+step_number+".res";
     //   write_file_name = "./task/recryst_2009_07_01_"+step_number+".pdb";

        read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_number+".res";
        write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_"+parameter+"_"+zeros+step_number+".pdb";

        // Representative of class for extracting of certain 3D-cells from specimen
        PlaneMaker plane_maker = new PlaneMaker(read_file_name, task_name);

        // Table of current parameters of cells
        StringVector current_params = plane_maker.getSpecimenR3();

        // Variable responsible for drawing of all cells:
        // if it equals false then boundary cells are not shown,
        // if it equals true then all cells are shown.
        boolean _show_all_cells = true;

        // the method finds minimal and maximal values of parameters
        plane_maker.findExtremeValues(_show_all_cells);

        // Cell with maximal parameters of plane
        VisualCellR3 max_cell = plane_maker.getMaxCellR3();

        // Cell with minimal parameters of plane
        VisualCellR3 min_cell = plane_maker.getMinCellR3();

        // Maximal coordinates and parameters of cells
        double  max_temperature    = max_cell.getTemperature(),
                max_eff_stress     = max_cell.getEffStress(),
                max_prin_stress    = max_cell.getPrinStress(),
                max_force_moment_X = max_cell.getForceMomentX(),
                max_force_moment_Y = max_cell.getForceMomentY(),
                max_force_moment_Z = max_cell.getForceMomentZ(),
                max_strain         = max_cell.getStrain(),
                max_discl_density  = max_cell.getDisclDensity();

        // Maximal coordinates and parameters of cells
        double  min_temperature    = min_cell.getTemperature(),
                min_eff_stress     = min_cell.getEffStress(),
                min_prin_stress    = min_cell.getPrinStress(),
                min_force_moment_X = min_cell.getForceMomentX(),
                min_force_moment_Y = min_cell.getForceMomentY(),
                min_force_moment_Z = min_cell.getForceMomentZ(),
                min_strain         = min_cell.getStrain(),
                min_discl_density  = min_cell.getDisclDensity();//0;//

        String string;

        // Type of cell according to its interaction with neighbours
        byte energy_type = -1;

        // Type of cell location in grain and specimen: internal or boundary
        byte location_type;
        
        //Index of grain containing cell
        int grain_index = -1;

        // Coordinates of cell
        double coordinate_X = -1;
        double coordinate_Y = -1;
        double coordinate_Z = -1;

        // Cell temperature
        double temperature = -1;
        
        // Cell "effective" stress
        double eff_stress = -1;
        
        // Cell principal stress
        double prin_stress = -1;

        // Components of force moment vector
        double force_moment_X = -1;
        double force_moment_Y = -1;
        double force_moment_Z = -1;

        // State of cell: elastic, plastic or damaged
        byte state;

        // Strain of cell
        double strain;

        // Density of disclinations in cell
        double discl_density;

        // Name of cell state
        String cell_state = "N  ";

        // Coordinates of cell multiplied by 10000 and rounded to integer
        int coordinate_X_round;
        int coordinate_Y_round;
        int coordinate_Z_round;

        // Integer parts of cell coordinates
        int coordinate_X_int;
        int coordinate_Y_int;
        int coordinate_Z_int;

        // Fractional parts of cell coordinates
        int coordinate_X_frac;
        int coordinate_Y_frac;
        int coordinate_Z_frac;

        // Number exponents of cell coordinates
        byte num_exp_X;
        byte num_exp_Y;
        byte num_exp_Z;

        StringTokenizer st;

        // Total number of cells
        int cell_number = current_params.getRowsNum();

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));

            // Cycle over all cells in specimen
            for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
            {
                // Type of cell location
                location_type  = (new Byte(current_params.getCell(cell_counter, 1))).byteValue();

            //    if((location_type!=Common.OUTER_CELL)|_show_all_cells)
                if((location_type==Common.INTRAGRANULAR_CELL)|(location_type==Common.INTERGRANULAR_CELL))
                {
                    // Reading of coordinates and physical parameters of current cell
                    energy_type    = (new Byte   (current_params.getCell(cell_counter, 0))).byteValue();
                    grain_index    = (new Integer(current_params.getCell(cell_counter, 2))).intValue();
                    coordinate_X   = (new Double (current_params.getCell(cell_counter, 3))).doubleValue();
                    coordinate_Y   = (new Double (current_params.getCell(cell_counter, 4))).doubleValue();
                    coordinate_Z   = (new Double (current_params.getCell(cell_counter, 5))).doubleValue();
                    temperature    = (new Double (current_params.getCell(cell_counter, 6))).doubleValue();
                    eff_stress     = (new Double (current_params.getCell(cell_counter, 7))).doubleValue();
                    prin_stress    = (new Double (current_params.getCell(cell_counter, 8))).doubleValue();
                    force_moment_X = (new Double (current_params.getCell(cell_counter, 9))).doubleValue();
                    force_moment_Y = (new Double (current_params.getCell(cell_counter,10))).doubleValue();
                    force_moment_Z = (new Double (current_params.getCell(cell_counter,11))).doubleValue();
                    state          = (new Byte   (current_params.getCell(cell_counter,12))).byteValue();
                    strain         = (new Double (current_params.getCell(cell_counter,13))).doubleValue();
                    discl_density  = (new Double (current_params.getCell(cell_counter,14))).doubleValue();

                    // Redetermination of cell coordinates 
                    coordinate_X = magnif_coeff*coordinate_X;
                    coordinate_Y = magnif_coeff*coordinate_Y;
                    coordinate_Z = magnif_coeff*coordinate_Z;
              
                    if((energy_type == Common.INNER_CELL)|(energy_type == Common.LAYER_CELL))
                    {
                        if (grain_index%22 ==  0) cell_state = "N  ";
                        if (grain_index%22 ==  1) cell_state = "Cu ";
                        if (grain_index%22 ==  2) cell_state = "O  ";
                        if (grain_index%22 ==  3) cell_state = "Si ";
                        if (grain_index%22 ==  4) cell_state = "Fe ";
                        if (grain_index%22 ==  5) cell_state = "B  ";
                        if (grain_index%22 ==  6) cell_state = "Mg ";
                        if (grain_index%22 ==  7) cell_state = "It ";
                        if (grain_index%22 ==  8) cell_state = "Cd ";
                        if (grain_index%22 ==  9) cell_state = "Na ";
                        if (grain_index%22 == 10) cell_state = "Au ";
                        if (grain_index%22 == 11) cell_state = "Al ";
                        if (grain_index%22 == 12) cell_state = "I  ";
                        if (grain_index%22 == 13) cell_state = "Cl ";
                        if (grain_index%22 == 14) cell_state = "Br ";
                        if (grain_index%22 == 15) cell_state = "Co ";
                        if (grain_index%22 == 16) cell_state = "S  ";
                        if (grain_index%22 == 17) cell_state = "Na ";
                        if (grain_index%22 == 18) cell_state = "Pl ";
                        if (grain_index%22 == 19) cell_state = "Mn ";
                        if (grain_index%22 == 20) cell_state = "Li ";
                        if (grain_index%22 == 21) cell_state = "I  ";
                        
                        // Showing of cells on grain bounds
                        if(_show_grain_bounds==SHOW_GRAIN_BOUNDS)
                        if((location_type==Common.INTERGRANULAR_CELL)|
                           (location_type==Common.INNER_BOUNDARY_INTERGRANULAR_CELL))
                            cell_state = "H  ";
                    }
                    else
                        cell_state = "H  ";
                    
                    // Printing of data concerning all grains (one grain)
               //     if (grain_index == 6)
                    // Printing of data concerning inner cells
                    if((energy_type == Common.INNER_CELL)|(energy_type == Common.LAYER_CELL))
                    {
                        bw.write("ATOM         ");
                        bw.write(cell_state);
                        bw.write("              ");

                        coordinate_X_round = (int)Math.round(10000*coordinate_X);
                        coordinate_Y_round = (int)Math.round(10000*coordinate_Y);
                        coordinate_Z_round = (int)Math.round(10000*coordinate_Z);

                        // Obtaining of integer parts of cell coordinates
                        coordinate_X_int   = coordinate_X_round/10000;
                        coordinate_Y_int   = coordinate_Y_round/10000;
                        coordinate_Z_int   = coordinate_Z_round/10000;

                        // Obtaining of number exponents of cell coordinates
                        num_exp_X = 3;
                        if(coordinate_X_int/100 == 0) num_exp_X = (byte)(num_exp_X - 1);
                        if(coordinate_X_int/ 10 == 0) num_exp_X = (byte)(num_exp_X - 1);

                        num_exp_Y = 3;
                        if(coordinate_Y_int/100 == 0) num_exp_Y = (byte)(num_exp_Y - 1);
                        if(coordinate_Y_int/ 10 == 0) num_exp_Y = (byte)(num_exp_Y - 1);

                        num_exp_Z = 3;
                        if(coordinate_Z_int/100 == 0) num_exp_Z = (byte)(num_exp_Z - 1);
                        if(coordinate_Z_int/ 10 == 0) num_exp_Z = (byte)(num_exp_Z - 1);

                        // Obtaining of fractional parts of cell coordinates
                        coordinate_X_frac  = coordinate_X_round%10000;
                        coordinate_Y_frac  = coordinate_Y_round%10000;
                        coordinate_Z_frac  = coordinate_Z_round%10000;

                        // Writing of integer part of coordinate Y
                        for(int space_counter = 0; space_counter < 3 - num_exp_Y; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_Y_int+".");

                        // Obtaining of number exponent of fractional part of coordinate Y
                        if(coordinate_Y_frac/10 == 0) num_exp_Y = 1;
                        else
                            if(coordinate_Y_frac/100 == 0) num_exp_Y = 2;
                            else
                                if(coordinate_Y_frac/1000 == 0) num_exp_Y = 3;
                                else
                                    if(coordinate_Y_frac/10000 == 0) num_exp_Y = 4;

                        // Writing of fractional part of coordinate Y
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_Y; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_Y_frac+"");

                       // Writing of integer part of coordinate X
                        for(int space_counter = 0; space_counter < 3 - num_exp_X; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_X_int+".");

                        // Obtaining of number exponent of fractional part of coordinate X
                        if(coordinate_X_frac/10 == 0) num_exp_X = 1;
                        else
                            if(coordinate_X_frac/100 == 0) num_exp_X = 2;
                            else
                                if(coordinate_X_frac/1000 == 0) num_exp_X = 3;
                                else
                                    if(coordinate_X_frac/10000 == 0) num_exp_X = 4;

                        // Writing of fractional part of coordinate X
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_X; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_X_frac+"");

                        // Writing of integer part of coordinate Z
                        for(int space_counter = 0; space_counter < 3 - num_exp_Z; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_Z_int+".");

                        // Obtaining of number exponent of fractional part of coordinate Z
                        if(coordinate_Z_frac/10 == 0) num_exp_Z = 1;
                        else
                            if(coordinate_Z_frac/100 == 0) num_exp_Z = 2;
                            else
                                if(coordinate_Z_frac/1000 == 0) num_exp_Z = 3;
                                else
                                    if(coordinate_Z_frac/10000 == 0) num_exp_Z = 4;

                        // Writing of fractional part of coordinate Z
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_Z; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_Z_frac+"");
                        
                        // Required parameter
                        double param = 0;

                        // Maximal and minimal values of parameter
                        double min_param=0, max_param=0;

                        // Writing of the value from the file:
                        // a. temperature;
                        if(parameter.equals("temperature"))
                        {
                            param     = temperature;
                            min_param = min_temperature;
                            max_param = max_temperature;
                        }

                        // b. effective stress;
                        if(parameter.equals("eff_stress"))
                        {
                            param     = eff_stress;
                            min_param = min_eff_stress;
                            max_param = max_eff_stress;
                        }

                        // c. principal stress;
                        if(parameter.equals("prin_stress"))
                        {
                            param     = prin_stress;
                            min_param = min_prin_stress;
                            max_param = max_prin_stress;
                        }

                        // d. component X of force moment vector;
                        if(parameter.equals("force_moment_X"))
                        {
                            param     = force_moment_X;
                            min_param = min_force_moment_X;
                            max_param = max_force_moment_X;

                            if(param>0) param = 1;
                            if(param<0) param =-1;

                            min_param =-1;
                            max_param = 1;
                        }

                        // e. component Y of force moment vector;
                        if(parameter.equals("force_moment_Y"))
                        {
                            param     = force_moment_Y;
                            min_param = min_force_moment_Y;
                            max_param = max_force_moment_Y;
                        }

                        // f. component Z of force moment vector;
                        if(parameter.equals("force_moment_Z"))
                        {
                            param     = force_moment_Z;
                            min_param = min_force_moment_Z;
                            max_param = max_force_moment_Z;
                        }

                        // g. state of cell: elastic, plastic or damaged.
                        if(parameter.equals("state"))
                            param = state;

                        // h. strain of cell.
                        if(parameter.equals("strain"))
                        {
                            param = strain;
                            min_param = min_strain;
                            max_param = max_strain;
                        }

                        // i. disclination density of cell.
                        if(parameter.equals("discl_density"))
                        {
                            param = discl_density;
                            min_param = min_discl_density;
                            max_param = max_discl_density;
                        }

                        // Normalization of parameter (not for state):
                        // minimum=0, maximum=1000
                        if(max_param>min_param)
                            param = 1000*(param-min_param)/(max_param-min_param);
                        else
                            System.out.println("Error of determination of extreme parameter values!!!");

                        // Writing of rounded parameter value
                        bw.write("      "+Math.round(param));//param);//

                        // Calculation of factors of parameter
               //         double[] factors = getFactors(param);

               //         bw.write("      "+factors[0]+"E"+(int)factors[1]);
                        
                        bw.newLine();
                        bw.flush();
                    }
                }
            }

            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file with information about values of the parameter
     * in points of plane.
     * @param task_name name of considered task
     * @param step_number number of time step corresponding to read file
     * @param parameter name of considered parameter
     * @param section_coord coordinate of extracted plane
     * @param axis axis perpendicular to the plane
     */
    public void createSection(String task_name, int step_number, String parameter, double section_coord, String axis)
    {
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(max_step_number));

        // Exponent of power of 10 in standard representation
        // of number of current time step
        int exponent = 0;

        if(step_number > 0)
            exponent     = (int)Math.floor(Math.log10(step_number));

        // String of zeros
        String zeros = "";

        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";

        read_file_name  = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_number+"_inst_mom.res";
        write_file_name = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_number+
                          "_"+axis+"="+Math.round(section_coord/cell_size_X)+"_inst_"+parameter+".txt";

        // Representative of class for extracting of certain 3D-cells from specimen
        PlaneMaker plane_maker = new PlaneMaker(read_file_name, task_name);

        // Table of current parameters of cells
        StringVector current_params = plane_maker.getSpecimenR3();

        // Type of cell according to its interaction with neighbours
        byte energy_type = -1;

        // Index of grain containing cell
        int grain_index = -1;

        // Type of cell location in grain and specimen: internal or boundary
        byte location_type;

        // Coordinates of cell
        double coordinate_X = -1;
        double coordinate_Y = -1;
        double coordinate_Z = -1;

        // Cell temperature
        double temperature = -1;

        // Cell "effective" stress
        double eff_stress = -1;

        // Cell principal stress
        double prin_stress = -1;

        // Components of force moment vector
        double force_moment_X = -1;
        double force_moment_Y = -1;
        double force_moment_Z = -1;

        // State of cell: elastic, plastic or damaged
        byte state;

        // Strain of cell
        double strain;
        
        // total specific torsion energy
        double spec_tors_energy;

        // Total number of cells
        int cell_number = current_params.getRowsNum();

        boolean axis_X = false,
                axis_Y = false,
                axis_Z = false;

        double coeff_HCP_X = Math.sqrt(3.0/4.0);
        double coeff_HCP_Y = 1.0;
        double coeff_HCP_Z = Math.sqrt(2.0/3.0);

        if(axis.equals("X"))
        {
            axis_X = true;
            System.out.println("section_coord_X = "+section_coord);
            System.out.println("cell_size_X     = "+cell_size_X);
        }

        if(axis.equals("Y"))
        {
            axis_Y = true;
            System.out.println("section_coord_Y = "+section_coord);
            System.out.println("cell_size_Y     = "+cell_size_Y);
        }

        if(axis.equals("Z"))
        {
            axis_Z = true;
            System.out.println("section_coord_Z = "+section_coord);
            System.out.println("cell_size_Z     = "+cell_size_Z);
        }

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));

            // Cycle over all cells in specimen
            for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
            {                
                energy_type    = (new Byte   (current_params.getCell(cell_counter, 0))).byteValue();
                location_type  = (new Byte(current_params.getCell(cell_counter, 1))).byteValue();

                if(location_type!=Common.OUTER_CELL)
                {
                    // Reading of coordinates and physical parameters of current cell
                    coordinate_X   = (new Double (current_params.getCell(cell_counter, 3))).doubleValue();
                    coordinate_Y   = (new Double (current_params.getCell(cell_counter, 4))).doubleValue();
                    coordinate_Z   = (new Double (current_params.getCell(cell_counter, 5))).doubleValue();
                    
                    coordinate_X = coordinate_X - coeff_HCP_X;
                    coordinate_Y = coordinate_Y - coeff_HCP_Y;
                    coordinate_Z = coordinate_Z - coeff_HCP_Z;

                    coordinate_X = coordinate_X*cell_size_X;
                    coordinate_Y = coordinate_Y*cell_size_X;
                    coordinate_Z = coordinate_Z*cell_size_X;

                    // Printing of data concerning inner cells in certain surface
                    if((axis_X&(coordinate_X>=section_coord-cell_size_X*coeff_HCP_X/2)&(coordinate_X<section_coord+cell_size_X*coeff_HCP_X/2))|
                       (axis_Y&(coordinate_Y>=section_coord-cell_size_Y*coeff_HCP_Y/2)&(coordinate_Y<section_coord+cell_size_Y*coeff_HCP_Y/2))|
                       (axis_Z&(coordinate_Z>=section_coord-cell_size_Z*coeff_HCP_Z/2)&(coordinate_Z<section_coord+cell_size_Z*coeff_HCP_Z/2)))
                    if((energy_type == Common.INNER_CELL)|(energy_type == Common.LAYER_CELL))
                    {
                        grain_index    = (new Integer(current_params.getCell(cell_counter, 2))).intValue();
                        temperature    = (new Double (current_params.getCell(cell_counter, 6))).doubleValue();
                        eff_stress     = (new Double (current_params.getCell(cell_counter, 7))).doubleValue();
                        prin_stress    = (new Double (current_params.getCell(cell_counter, 8))).doubleValue();
                        force_moment_X = (new Double (current_params.getCell(cell_counter, 9))).doubleValue();
                        force_moment_Y = (new Double (current_params.getCell(cell_counter,10))).doubleValue();
                        force_moment_Z = (new Double (current_params.getCell(cell_counter,11))).doubleValue();
                        state          = (new Byte   (current_params.getCell(cell_counter,12))).byteValue();
                        strain         = (new Double (current_params.getCell(cell_counter,13))).doubleValue();
                        spec_tors_energy = (new Double (current_params.getCell(cell_counter,14))).doubleValue();

                        // Required parameter
                        double param = 0;

                        // Writing of the value from the file:
                        // Index of grain containing cell
                        if(parameter.equals("grain_index"))
                            param     = grain_index;

                        // a. temperature;
                        if(parameter.equals("temperature"))
                            param     = temperature;

                        // b. effective stress;
                        if(parameter.equals("eff_stress"))
                            param     = eff_stress;

                        // c. principal stress;
                        if(parameter.equals("prin_stress"))
                            param     = prin_stress;

                        // d. component X of force moment vector;
                        if(parameter.equals("force_moment_X"))
                        {
                            // Transition to MPa
                         // param     = Math.round(force_moment_X*1.0E-3)/1.0E3;
                            
                            // Transition to kPa
                            param     = Math.round(force_moment_X)/1.0E3;

                           // if(param>0) param = 1;
                           // if(param<0) param =-1;
                        }

                        // e. component Y of force moment vector;
                        if(parameter.equals("force_moment_Y"))
                        {
                            // Transition to MPa
                         // param     = Math.round(force_moment_Y*1.0E-3)/1.0E3;
                            
                            // Transition to kPa
                            param     = Math.round(force_moment_Y)/1.0E3;
                            
                           // if(param>0) param = 1;
                           // if(param<0) param =-1;
                        }

                        // f. component Z of force moment vector;
                        if(parameter.equals("force_moment_Z"))
                        {
                            // Transition to MPa
                         // param     = Math.round(force_moment_Z*1.0E-3)/1.0E3;
                            
                            // Transition to kPa
                            param     = Math.round(force_moment_Z)/1.0E3;

                           // if(param>0) param = 1;
                           // if(param<0) param =-1;
                        }

                        // g. state of cell: elastic, plastic or damaged;
                        if(parameter.equals("state"))
                            param = state;

                        // h. strain of cell;
                        if(parameter.equals("strain"))
                            param = strain;
                        
                        // i. total specific torsion energy of cell.
                        if(parameter.equals("spec_tors_energy"))
                            param = spec_tors_energy;
                        
                        // Transformation of coordinates
                        coordinate_X = coordinate_X/cell_size_X;
                        coordinate_Y = coordinate_Y/cell_size_Y;
                        coordinate_Z = coordinate_Z/cell_size_Z;

                        // Rounding of coordinates
                        coordinate_X = (int)Math.round(1000*coordinate_X)/1000.0;
                        coordinate_Y = (int)Math.round(1000*coordinate_Y)/1000.0;
                        coordinate_Z = (int)Math.round(1000*coordinate_Z)/1000.0;
                       
                        // Writing of parameter value
                        if(axis_X)
                            bw.write(coordinate_Y+" "+coordinate_Z+" "+param);

                        if(axis_Y)
                            bw.write(coordinate_X+" "+coordinate_Z+" "+param);

                        if(axis_Z)
                            bw.write(coordinate_X+" "+coordinate_Y+" "+param);

                        bw.newLine();
                        bw.flush();
                    }
                }
            }
            
            if(parameter.equals("force_moment_X") | parameter.equals("force_moment_Y") | parameter.equals("force_moment_Z"))
            {
              bw.newLine();
              bw.write("# Coordinates are expressed in cell sizes.\n");
              bw.write("# Values of the parameter '"+parameter+"' are expressed in kPa.");
              bw.flush();
            }
                
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        System.out.println("Name of created file " + write_file_name);
    }
    
    /** The method reads values of fields of the class from the file.
     * @param file_name name of file containing values of fields of the class
     */
    private void readTask(String file_name)
    {
        Properties task_properties = new Properties();        
        
        try
        {
            FileInputStream fin = new FileInputStream(file_name);
            task_properties.load(fin);
            fin.close();
        }
        catch(IOException io_ex) 
        {
            System.out.println("ERROR: "+io_ex);
        }
        
        // Reading of variable responsible for showing of cells
        // on grain bounds as atoms "H" in "RasMol"
        show_grain_bounds       = (new Byte(task_properties.getProperty("show_grain_bounds"))).byteValue();

        // Reading of variable responsible for type of packing of cells in specimen
        packing_type            = (new Byte(task_properties.getProperty("packing_type"))).byteValue();
    }    
    
    /** The method creates file in "PDB" format.
     * Grain boundaries are represented by gray cells.
     */
    public void fromInitCondFileToFilePDB()
    {
        // Name of file with initial conditions of task (without comments!)
        read_file_name  = "./task/heat_flow.incnd";
        write_file_name = "./task/init_grain_struct.pdb";
        
        String string;

        //Index of grain containing cell
        int grain_index = -1;

        // Coordinates of cell
        double coordinate_X = -1;
        double coordinate_Y = -1;
        double coordinate_Z = -1;

        // Cell temperature
        int temperature = -1;
        
        // Cell mechanical energy
        int mech_energy = -1;
        
        // Cell heat energy
        int heat_energy = -1;
        
        // Cell single index
        int cell_index = 0;
        
        // Cell triple index
        Three cell_indices;
        
        // Cell coordinates
        VectorR3 cell_coordinates;

        // Name of cell state
        String cell_state = "N  ";

        // Coordinates of cell multiplied by 10000 and rounded to integer
        int coordinate_X_round;
        int coordinate_Y_round;
        int coordinate_Z_round;

        // Integer parts of cell coordinates
        int coordinate_X_int;
        int coordinate_Y_int;
        int coordinate_Z_int;

        // Fractional parts of cell coordinates
        int coordinate_X_frac;
        int coordinate_Y_frac;
        int coordinate_Z_frac;

        // Number exponents of cell coordinates
        byte num_exp_X;
        byte num_exp_Y;
        byte num_exp_Z;

        StringTokenizer st;

        try
        {
            BufferedReader br  = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw  = new BufferedWriter(new FileWriter(write_file_name));

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    cell_index++;
                    
                    // Reading of parameters of current cell
                    heat_energy  = (int)Math.round(new Double(st.nextToken()).doubleValue());
                    mech_energy  = (int)Math.round(new Double(st.nextToken()).doubleValue());
                    temperature  = (int)Math.round(new Double(st.nextToken()).doubleValue());
                    grain_index  = new Integer(st.nextToken()).intValue();//-1;
                    
                    // Calculation of triple cell index
                    cell_indices = calcTripleIndex(cell_index, cellNumberI, cellNumberJ, cellNumberK);
                    
                    // Calculation of cell coordinates
                    cell_coordinates = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());
                    
                    // Redetermination of cell coordinates for "pdb" file
                    coordinate_X = 2.5*cell_coordinates.getX();
                    coordinate_Y = 2.5*cell_coordinates.getY();
                    coordinate_Z = 2.5*cell_coordinates.getZ();
              
                    if(grain_index <= grain_number)
                    {
                        if (grain_index%22 ==  0) cell_state = "N  ";
                        if (grain_index%22 ==  1) cell_state = "Cu ";
                        if (grain_index%22 ==  2) cell_state = "O  ";
                        if (grain_index%22 ==  3) cell_state = "Si ";
                        if (grain_index%22 ==  4) cell_state = "Fe ";
                        if (grain_index%22 ==  5) cell_state = "B  ";
                        if (grain_index%22 ==  6) cell_state = "Mg ";
                        if (grain_index%22 ==  7) cell_state = "It ";
                        if (grain_index%22 ==  8) cell_state = "Cd ";
                        if (grain_index%22 ==  9) cell_state = "Na ";
                        if (grain_index%22 == 10) cell_state = "Au ";
                        if (grain_index%22 == 11) cell_state = "Al ";
                        if (grain_index%22 == 12) cell_state = "I  ";
                        if (grain_index%22 == 13) cell_state = "Cl ";
                        if (grain_index%22 == 14) cell_state = "Br ";
                        if (grain_index%22 == 15) cell_state = "Co ";
                        if (grain_index%22 == 16) cell_state = "S  ";
                        if (grain_index%22 == 17) cell_state = "Na ";
                        if (grain_index%22 == 18) cell_state = "Pl ";
                        if (grain_index%22 == 19) cell_state = "Mn ";
                        if (grain_index%22 == 20) cell_state = "Li ";
                        if (grain_index%22 == 21) cell_state = "I  ";
                    }
                    else
                        cell_state = "H  ";

                    // Printing of data concerning all grains
               //     if (value == 6) // Printing of data concerning one of grains
                    if(grain_index <= grain_number) // Printing of data concerning inner grains
                    {
                        bw.write("ATOM         ");
                        bw.write(cell_state);
                        bw.write("              ");

                        coordinate_X_round = (int)Math.round(10000*coordinate_X);
                        coordinate_Y_round = (int)Math.round(10000*coordinate_Y);
                        coordinate_Z_round = (int)Math.round(10000*coordinate_Z);

                        // Obtaining of integer parts of cell coordinates
                        coordinate_X_int   = coordinate_X_round/10000;
                        coordinate_Y_int   = coordinate_Y_round/10000;
                        coordinate_Z_int   = coordinate_Z_round/10000;

                        // Obtaining of number exponents of cell coordinates
                        num_exp_X = 3;
                        if(coordinate_X_int/100 == 0) num_exp_X = (byte)(num_exp_X - 1);
                        if(coordinate_X_int/ 10 == 0) num_exp_X = (byte)(num_exp_X - 1);

                        num_exp_Y = 3;
                        if(coordinate_Y_int/100 == 0) num_exp_Y = (byte)(num_exp_Y - 1);
                        if(coordinate_Y_int/ 10 == 0) num_exp_Y = (byte)(num_exp_Y - 1);

                        num_exp_Z = 3;
                        if(coordinate_Z_int/100 == 0) num_exp_Z = (byte)(num_exp_Z - 1);
                        if(coordinate_Z_int/ 10 == 0) num_exp_Z = (byte)(num_exp_Z - 1);

                        // Obtaining of fractional parts of cell coordinates
                        coordinate_X_frac  = coordinate_X_round%10000;
                        coordinate_Y_frac  = coordinate_Y_round%10000;
                        coordinate_Z_frac  = coordinate_Z_round%10000;

                        // Writing of integer part of coordinate Y
                        for(int space_counter = 0; space_counter < 3 - num_exp_Y; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_Y_int+".");

                        // Obtaining of number exponent of fractional part of coordinate Y
                        if(coordinate_Y_frac/10 == 0) num_exp_Y = 1;
                        else
                            if(coordinate_Y_frac/100 == 0) num_exp_Y = 2;
                            else
                                if(coordinate_Y_frac/1000 == 0) num_exp_Y = 3;
                                else
                                    if(coordinate_Y_frac/10000 == 0) num_exp_Y = 4;

                        // Writing of fractional part of coordinate Y
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_Y; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_Y_frac+"");

                       // Writing of integer part of coordinate X
                        for(int space_counter = 0; space_counter < 3 - num_exp_X; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_X_int+".");

                        // Obtaining of number exponent of fractional part of coordinate X
                        if(coordinate_X_frac/10 == 0) num_exp_X = 1;
                        else
                            if(coordinate_X_frac/100 == 0) num_exp_X = 2;
                            else
                                if(coordinate_X_frac/1000 == 0) num_exp_X = 3;
                                else
                                    if(coordinate_X_frac/10000 == 0) num_exp_X = 4;

                        // Writing of fractional part of coordinate X
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_X; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_X_frac+"");

                        // Writing of integer part of coordinate Z
                        for(int space_counter = 0; space_counter < 3 - num_exp_Z; space_counter++)
                            bw.write(" ");

                        bw.write(coordinate_Z_int+".");

                        // Obtaining of number exponent of fractional part of coordinate Z
                        if(coordinate_Z_frac/10 == 0) num_exp_Z = 1;
                        else
                            if(coordinate_Z_frac/100 == 0) num_exp_Z = 2;
                            else
                                if(coordinate_Z_frac/1000 == 0) num_exp_Z = 3;
                                else
                                    if(coordinate_Z_frac/10000 == 0) num_exp_Z = 4;

                        // Writing of fractional part of coordinate Z
                        for(int zero_counter = 0; zero_counter < 4 - num_exp_Z; zero_counter++)
                            bw.write("0");

                        bw.write(coordinate_Z_frac+"");

                        // Writing of the value from the file
                        bw.write("      "+temperature);

                        bw.newLine();
                        bw.flush();
                    }
                }
            }

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method creates file in "PDB" format with coordinates of cells
     * located in plane perpendicular to one of reference axes.
     */
    public void createPlanePDB()
    {
        read_file_name  = "./task1/damaged_volume_and_crack_embryos.txt";
        write_file_name = "./task1/damaged_volume_and_crack_embryos.pdb";

    //    read_file_name  = "./task1/cracks_40.txt";
    //    write_file_name = "./task1/cracks_40.pdb";

        String string;

        // Coordinates of cell
        double coordinate_X = -1;
        double coordinate_Y = -1;
        double coordinate_Z = -1;

        // State of cell
        int cell_state;

        // Coordinates of cell multiplied by 10000 and rounded to integer
        int coordinate_X_round;
        int coordinate_Y_round;
        int coordinate_Z_round;

        // Integer parts of cell coordinates
        int coordinate_X_int;
        int coordinate_Y_int;
        int coordinate_Z_int;

        // Fractional parts of cell coordinates
        int coordinate_X_frac;
        int coordinate_Y_frac;
        int coordinate_Z_frac;

        // Number exponents of cell coordinates
        byte num_exp_X;
        byte num_exp_Y;
        byte num_exp_Z;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            // Reading of the number of strings of the file from the file
            int string_number = new Integer(br.readLine()).intValue();

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
               // Reading of coordinates and physical parameters of current cell
                  cell_state   = new Integer(st.nextToken()).intValue();
                  coordinate_X = new Double(st.nextToken()).doubleValue();
                  coordinate_Y = new Double(st.nextToken()).doubleValue();
                  coordinate_Z = new Double(st.nextToken()).doubleValue();

                  coordinate_X = coordinate_X*1.0E6;
                  coordinate_Y = coordinate_Y*1.0E6;
                  coordinate_Z = coordinate_Z*1.0E6;

            //      if(Math.round(coordinate_X) == plane_round_coord_X)
            //      if(Math.round(coordinate_Y) == plane_round_coord_Y)
            //      if(Math.round(coordinate_Z) == plane_round_coord_Z)

            //      if(cell_state != 0)
                  {
                    bw.write("ATOM                          ");

                    coordinate_X_round = (int)Math.round(10000*coordinate_X);
                    coordinate_Y_round = (int)Math.round(10000*coordinate_Y);
                    coordinate_Z_round = (int)Math.round(10000*coordinate_Z);

                    // Obtaining of integer parts of cell coordinates
                    coordinate_X_int   = coordinate_X_round/10000;
                    coordinate_Y_int   = coordinate_Y_round/10000;
                    coordinate_Z_int   = coordinate_Z_round/10000;

                    // Obtaining of number exponents of cell coordinates
                    num_exp_X = 3;
                    if(coordinate_X_int/100 == 0) num_exp_X = (byte)(num_exp_X - 1);
                    if(coordinate_X_int/ 10 == 0) num_exp_X = (byte)(num_exp_X - 1);

                    num_exp_Y = 3;
                    if(coordinate_Y_int/100 == 0) num_exp_Y = (byte)(num_exp_Y - 1);
                    if(coordinate_Y_int/ 10 == 0) num_exp_Y = (byte)(num_exp_Y - 1);

                    num_exp_Z = 3;
                    if(coordinate_Z_int/100 == 0) num_exp_Z = (byte)(num_exp_Z - 1);
                    if(coordinate_Z_int/ 10 == 0) num_exp_Z = (byte)(num_exp_Z - 1);

                    // Obtaining of fractional parts of cell coordinates
                    coordinate_X_frac  = coordinate_X_round%10000;
                    coordinate_Y_frac  = coordinate_Y_round%10000;
                    coordinate_Z_frac  = coordinate_Z_round%10000;

                    // Writing of integer part of coordinate Y
                    for(int space_counter = 0; space_counter < 3 - num_exp_Y; space_counter++)
                        bw.write(" ");

                    bw.write(coordinate_Y_int+".");

                    // Obtaining of number exponent of fractional part of coordinate Y
                    if(coordinate_Y_frac/10 == 0) num_exp_Y = 1;
                    else
                        if(coordinate_Y_frac/100 == 0) num_exp_Y = 2;
                        else
                            if(coordinate_Y_frac/1000 == 0) num_exp_Y = 3;
                            else
                                if(coordinate_Y_frac/10000 == 0) num_exp_Y = 4;

                    // Writing of fractional part of coordinate Y
                    for(int zero_counter = 0; zero_counter < 4 - num_exp_Y; zero_counter++)
                        bw.write("0");

                    bw.write(coordinate_Y_frac+"");

                    // Writing of integer part of coordinate X
                    for(int space_counter = 0; space_counter < 3 - num_exp_X; space_counter++)
                        bw.write(" ");

                    bw.write(coordinate_X_int+".");

                    // Obtaining of number exponent of fractional part of coordinate X
                    if(coordinate_X_frac/10 == 0) num_exp_X = 1;
                    else
                        if(coordinate_X_frac/100 == 0) num_exp_X = 2;
                        else
                            if(coordinate_X_frac/1000 == 0) num_exp_X = 3;
                            else
                                if(coordinate_X_frac/10000 == 0) num_exp_X = 4;

                    // Writing of fractional part of coordinate X
                    for(int zero_counter = 0; zero_counter < 4 - num_exp_X; zero_counter++)
                        bw.write("0");

                    bw.write(coordinate_X_frac+"");

                    // Writing of integer part of coordinate Z
                    for(int space_counter = 0; space_counter < 3 - num_exp_Z; space_counter++)
                        bw.write(" ");

                    bw.write(coordinate_Z_int+".");

                    // Obtaining of number exponent of fractional part of coordinate Z
                    if(coordinate_Z_frac/10 == 0) num_exp_Z = 1;
                    else
                        if(coordinate_Z_frac/100 == 0) num_exp_Z = 2;
                        else
                            if(coordinate_Z_frac/1000 == 0) num_exp_Z = 3;
                            else
                                if(coordinate_Z_frac/10000 == 0) num_exp_Z = 4;

                    // Writing of fractional part of coordinate Z
                    for(int zero_counter = 0; zero_counter < 4 - num_exp_Z; zero_counter++)
                        bw.write("0");

                    bw.write(coordinate_Z_frac+"");

                    // Writing of cell state
                    bw.write("      "+cell_state);

                    bw.newLine();
                    bw.flush();
                  }
                }
            }

            br.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file with current parameters (temperatures and states) of task.
     * @param read_file 
     */
    private void createCurrentParamsFile(String read_file)
    {
        String write_file = "./user/task_db/"+task_name+"/cur_param/tempr_1000K.incnd";
        String grain_struct_file = "./user/task_db/"+task_name+"/cur_param/init_cond_rec.txt";
        String grain_struct_file2 = "./user/task_db/"+task_name+"/cur_param/"+task_name+".str";
        
        String string;
  //      String string0, string1, string2, string3, string4;
        
        cell_size = 5.0E-9;
        
        double cell_volume = calcCellVolume(Common.HEXAGONAL_CLOSE_PACKING, cell_size);
        
        // Parameters of cell
        byte energy_type, location_type;
        int grain_index;
        double coord_X, coord_Y, coord_Z;
        double temperature, eff_stress, mech_energy;
        double pr_stress, mom_X, mom_Y, mom_Z;
        byte state;
        double strain, total_tors_energy;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(grain_struct_file));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(grain_struct_file2));

            while(br.ready())
            {
                string = br.readLine();

                StringTokenizer st = new StringTokenizer(string);
                string = st.nextToken();
                
                if(!string.equals("#"))
                if(st.hasMoreTokens())                    
                {
                    // Reading of temperature and state of current cell
                    energy_type   = new Byte(string).byteValue();
                    location_type = new Byte(st.nextToken()).byteValue();
                    grain_index   = new Integer(st.nextToken()).intValue();
                    coord_X       = new Double(st.nextToken()).doubleValue();
                    coord_Y       = new Double(st.nextToken()).doubleValue();
                    coord_Z       = new Double(st.nextToken()).doubleValue();
                    temperature   = new Double(st.nextToken()).doubleValue();
                    eff_stress    = new Double(st.nextToken()).doubleValue();

                    mech_energy   = eff_stress*cell_volume;
                    
                    pr_stress = new Double(st.nextToken()).doubleValue();
                    mom_X = new Double(st.nextToken()).doubleValue();
                    mom_Y = new Double(st.nextToken()).doubleValue();
                    mom_Z = new Double(st.nextToken()).doubleValue();
                    state = new Byte(st.nextToken()).byteValue();
                    strain = new Double(st.nextToken()).doubleValue();
                    total_tors_energy = new Double(st.nextToken()).doubleValue();

               //     bw.write("0.0 "+mech_energy+" "+temperature+" "+grain_index+" "+location_type+" "+total_tors_energy);
                    bw.write("0.0 0.0 1000.0 "+grain_index+" "+location_type+"\n");
                  
                    if(location_type != 2)
                    {
                        bw1.write(grain_index+" "+location_type+"\n");
                        
                        coord_X = (int)Math.round(1000*(2*coord_X - 2.310))/1000.0;
                        coord_Y = (int)Math.round(1000*(2*coord_Y - 2.000))/1000.0;
                        coord_Z = (int)Math.round(1000*(2*coord_Z - 1.632))/1000.0;
                        bw2.write(grain_index+" "+coord_X+" "+coord_Y+" "+coord_Z+" "+location_type+"\n");
                    }
                    
                    bw.flush();
                    bw1.flush();
                    bw2.flush();
                }
            }

            br.close();
            bw.close();
            bw1.close();
            bw2.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method analyzes distribution of stress and strain rate vectors 
     * at grain boundaries and in the interior of grains
     * @param file_name name of file with information about cell locations
     */
    private void createTimeGraphsForStressAndStrainVectors(String task_name, int step_number, int file_number,
                       BufferedWriter bw_grain_bound_stresses, BufferedWriter bw_grain_bound_paral_stresses,
                       BufferedWriter bw_gr_interior_stresses, BufferedWriter bw_gr_interior_stresses_Z, 
                       BufferedWriter bw_grain_bound_velocities, BufferedWriter bw_grain_bound_paral_velocities, 
                       BufferedWriter bw_gr_interior_velocities, BufferedWriter bw_gr_interior_velocities_Z)
    {
        String res_file         = "./user/task_db/"+task_name+"/"+task_name+"_000000.res";
        System.out.println("Read file name: "+res_file);
        
        // Parameters of cell
        byte energy_type, location_type;
        int grain_index = -1;
        
        String string;
        ArrayList location_types = new ArrayList(0);
        ArrayList grain_indices  = new ArrayList(0);
        
        int cell_number = 0;
        int grain_bound_cell_number = 0;
        int gr_interior_cell_number = 0;
        
        try
        {
            BufferedReader br1         = new BufferedReader(new FileReader(res_file));
            
            while(br1.ready())
            {
                string = br1.readLine();

                StringTokenizer st = new StringTokenizer(string);
                string = st.nextToken();
                
                if(!string.equals("#"))
                if(st.hasMoreTokens())                    
                {
                    // Reading of parameters of current cell
                    energy_type   = new Byte(string).byteValue();
                    location_type = new Byte(st.nextToken()).byteValue();
                    grain_index   = new Integer(st.nextToken()).intValue();
                    
                    if(location_type == Common.INTRAGRANULAR_CELL)
                        gr_interior_cell_number++;
                        
                    if(location_type == Common.INTERGRANULAR_CELL)
                        grain_bound_cell_number++;
                    
                    location_types.add(location_type);
                    grain_indices.add(grain_index);
                    cell_number++;
                }
            }
            
            br1.close();
            
            System.out.println("Total number of cells:                           "+ cell_number);
            System.out.println("Number of inner cells:                           "+(gr_interior_cell_number + grain_bound_cell_number));
            System.out.println("Number of inner cells in the interior of grains: "+ gr_interior_cell_number);
            System.out.println("Number of inner cells at grain boundaries:       "+ grain_bound_cell_number);
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }
        
        System.out.println();
        
        String grains_file = "./user/task_db/"+task_name+"/"+task_name+".grn";
        System.out.println("Read file name: "+grains_file);
        
        double cell_grain_angle_1, cell_grain_angle_2, cell_grain_angle_3;
        
        ArrayList grain_angles_1 = new ArrayList(0);
        ArrayList grain_angles_2 = new ArrayList(0);
        ArrayList grain_angles_3 = new ArrayList(0);
        
        int total_grain_number = 0;
        
        try
        {
            BufferedReader br_grains         = new BufferedReader(new FileReader(grains_file));
            
            while(br_grains.ready())
            {
                string = br_grains.readLine();

                StringTokenizer st = new StringTokenizer(string);
                string = st.nextToken();
                
                if(!string.equals("#"))
                if(st.hasMoreTokens())                    
                {
                    // Reading of parameters of current grain
                    string = st.nextToken();
                    cell_grain_angle_1 = new Double(st.nextToken()).doubleValue();
                    cell_grain_angle_2 = new Double(st.nextToken()).doubleValue();
                    cell_grain_angle_3 = new Double(st.nextToken()).doubleValue();
                    
                    grain_angles_1.add(cell_grain_angle_1);
                    grain_angles_2.add(cell_grain_angle_2);
                    grain_angles_3.add(cell_grain_angle_3);
                    
                    total_grain_number++;
                }
            }
            
            br_grains.close();
            
            System.out.println("Total number of grains: " + total_grain_number);
            System.out.println("1st grain angles: " + grain_angles_1.get(0)+" "+grain_angles_2.get(0)+" "+grain_angles_3.get(0));
            System.out.println();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }
        
        String bound_res_file;
        
        double mod_elast          =  1.17E11;
        double max_mobility       = 10.0/mod_elast;
        double energyHAGB         =  1.0E-6;
        double angleHAGB          = 90.0;
        double specBoundEnergy    =  0.0;
        
        int cell_bound_number             = 0;
        int grain_bound_cell_bound_number = 0;
        int grain_bound_paral_cell_bound_number = 0;
        int gr_interior_cell_bound_number = 0;
        
        int cell_index                    = -1;
        int neighb_index                  = -1;
        byte cell_loc_type, neighb_loc_type;
        int cell_grain_index, neighb_grain_index;
        
        double stress = 0;
        double velocity = 0;
        
        double grain_bound_aver_stress       = 0;
        double grain_bound_paral_aver_stress = 0;
        
        double gr_interior_aver_stress   = 0;
        double gr_interior_aver_stress_Z = 0;
        
        double grain_bound_aver_velocity       = 0;
        double grain_bound_paral_aver_velocity = 0;
        
        double gr_interior_aver_velocity   = 0;
        double gr_interior_aver_velocity_Z = 0;
        
        double neighb_grain_angle_1, neighb_grain_angle_2, neighb_grain_angle_3;
        double angle_diff;
        double mobility_exponent;
        
        double boundary_temperature = 300.0;
        double facet_surface        = 0.5*0.6938*cell_size*cell_size;
        double boltz_const          = 1.3806504E-23;
        
        double norm_vec_X, norm_vec_Y, norm_vec_Z;
        double stress_X, stress_Y, stress_Z;
        double vel_X, vel_Y, vel_Z;
        
        double time_period = 0;
        int record_period = step_number/file_number;
        
        // Exponent of power of 10 in standard representation of step number
        int max_exponent = (int)Math.floor(Math.log10(step_number));
        
        // Exponent of power of 10 in standard representation of current time step
        int exponent = 0;
        
        String zeros;
        
        for(int step_counter = 0; step_counter <= step_number; step_counter += record_period)
        if(step_counter <= 10*record_period | step_counter%(10*record_period) == 0)
        try
        {
            cell_bound_number = 0;
            grain_bound_cell_bound_number = 0;
            gr_interior_cell_bound_number = 0;
            grain_bound_paral_cell_bound_number = 0;
            
            grain_bound_aver_stress         = 0;
            grain_bound_paral_aver_stress   = 0;
            gr_interior_aver_stress         = 0;
            gr_interior_aver_stress_Z       = 0;
            
            grain_bound_aver_velocity       = 0;
            grain_bound_paral_aver_velocity = 0;
            gr_interior_aver_velocity       = 0;
            gr_interior_aver_velocity_Z     = 0;
        
            zeros = "";
            
            if(step_counter+1 > 0)
                exponent     = (int)Math.floor(Math.log10(step_counter+1));
            
            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0"; 
            
            bound_res_file = "./user/task_db/"+task_name+"/"+task_name+"_"+zeros+step_counter+"_bounds.res";
            System.out.println("Read file name: "+bound_res_file);
            
            BufferedReader br2         = new BufferedReader(new FileReader(bound_res_file));
            
            while(br2.ready())
            {
                string = br2.readLine();

                StringTokenizer st = new StringTokenizer(string);
                string = st.nextToken();
                
                if(!string.equals("#"))
                if(st.hasMoreTokens())
                {
                    // Reading of indices of current cell and its current neighbour
                    cell_index      = new Integer(string).intValue();
                    neighb_index    = new Integer(st.nextToken()).intValue();
                    
                    // Location types for current pair of cells
                    cell_loc_type   = (byte)location_types.get(cell_index);
                    neighb_loc_type = (byte)location_types.get(neighb_index);
                    
                    // Grain indices for current pair of cells
                    cell_grain_index   = (int)grain_indices.get(cell_index);
                    neighb_grain_index = (int)grain_indices.get(neighb_index);
                    
                    if((cell_loc_type   == Common.INTRAGRANULAR_CELL | cell_loc_type   == Common.INTERGRANULAR_CELL)&
                       (neighb_loc_type == Common.INTRAGRANULAR_CELL | neighb_loc_type == Common.INTERGRANULAR_CELL))
                    {
                      cell_bound_number++;
                        
                      // Grain boundary cells from different grains
                      if(cell_grain_index != neighb_grain_index)
                      {
                        grain_bound_cell_bound_number++;
                        
                        for(int token_counter = 2; token_counter <= 7; token_counter++)
                            string = st.nextToken();
                        
                        // Calculation of stress at grain boundary
                        stress   = new Double(st.nextToken()).doubleValue();
                        velocity = new Double(st.nextToken()).doubleValue();
                        
                   /*
                        cell_grain_angle_1   = (double)grain_angles_1.get(cell_grain_index-1);
                        cell_grain_angle_2   = (double)grain_angles_2.get(cell_grain_index-1);
                        cell_grain_angle_3   = (double)grain_angles_3.get(cell_grain_index-1);
                        
                        neighb_grain_angle_1 = (double)grain_angles_1.get(neighb_grain_index-1);
                        neighb_grain_angle_2 = (double)grain_angles_2.get(neighb_grain_index-1);
                        neighb_grain_angle_3 = (double)grain_angles_3.get(neighb_grain_index-1);                                
                        
                        angle_diff = (Math.abs(cell_grain_angle_1 - neighb_grain_angle_1)+
                                      Math.abs(cell_grain_angle_2 - neighb_grain_angle_2)+
                                      Math.abs(cell_grain_angle_3 - neighb_grain_angle_3))/3;
                        
                        specBoundEnergy = (energyHAGB*angle_diff/angleHAGB)*(1 - Math.log(angle_diff/angleHAGB));
                        
                        mobility_exponent = -facet_surface*specBoundEnergy/(boltz_const*boundary_temperature);
                                  
                        // Calculation of velocity of grain boundary movement
                        velocity = max_mobility*stress*Math.pow(Math.E, mobility_exponent);
                   */                        
                        grain_bound_aver_stress   += Math.abs(stress);
                        grain_bound_aver_velocity += Math.abs(velocity);
                      }
                      else
                      {
                        // Interior cells
                        if(cell_loc_type != Common.INTERGRANULAR_CELL | neighb_loc_type != Common.INTERGRANULAR_CELL)
                        {
                          gr_interior_cell_bound_number++;
                        
                          for(int token_counter = 2; token_counter <= 4; token_counter++)
                            string = st.nextToken();
                          
                          norm_vec_X = new Double(st.nextToken()).doubleValue();
                          norm_vec_Y = new Double(st.nextToken()).doubleValue();
                          norm_vec_Z = new Double(st.nextToken()).doubleValue();                        
                          stress     = new Double(st.nextToken()).doubleValue();
                          velocity   = new Double(st.nextToken()).doubleValue();
                        
                      /*
                          cell_grain_angle_1   = (double)grain_angles_1.get(cell_grain_index-1);
                          cell_grain_angle_2   = (double)grain_angles_2.get(cell_grain_index-1);
                          cell_grain_angle_3   = (double)grain_angles_3.get(cell_grain_index-1);
                        
                          neighb_grain_angle_1 = (double)grain_angles_1.get(neighb_grain_index-1);
                          neighb_grain_angle_2 = (double)grain_angles_2.get(neighb_grain_index-1);
                          neighb_grain_angle_3 = (double)grain_angles_3.get(neighb_grain_index-1);
                        
                          angle_diff = (Math.abs(cell_grain_angle_1 - neighb_grain_angle_1)+
                                        Math.abs(cell_grain_angle_2 - neighb_grain_angle_2)+
                                        Math.abs(cell_grain_angle_3 - neighb_grain_angle_3))/3;
                          
                          if(angle_diff != 0)
                            System.out.println("ERROR!!! angle_diff = "+angle_diff);
                          
                          // Calculation of velocity of grain boundary movement
                          velocity = max_mobility*stress;
                      */
                          stress_Z   = stress*norm_vec_Z;
                          vel_Z      = velocity*norm_vec_Z;
                          
                          gr_interior_aver_stress     += Math.abs(stress);
                          gr_interior_aver_stress_Z   += Math.abs(stress_Z);
                          gr_interior_aver_velocity   += Math.abs(velocity);
                          gr_interior_aver_velocity_Z += Math.abs(vel_Z);
                        }
                        
                        // Grain boundary cells from the same grain
                        if(cell_loc_type == Common.INTERGRANULAR_CELL & neighb_loc_type == Common.INTERGRANULAR_CELL)
                        {
                          grain_bound_paral_cell_bound_number++;
                        
                          for(int token_counter = 2; token_counter <= 7; token_counter++)
                            string = st.nextToken();
                                                 
                          stress     = new Double(st.nextToken()).doubleValue();
                          velocity   = new Double(st.nextToken()).doubleValue();
                          
                          grain_bound_paral_aver_stress   += Math.abs(stress);
                          grain_bound_paral_aver_velocity += Math.abs(velocity);
                        }
                      }
                    }
                }
            }
            
            br2.close();
            
            // Transition of time from seconds to microseconds
            time_period = Math.round(1.0E6*step_counter*time_step);
            
            System.out.println("time_period = "+time_period+" mcs.");
            
            System.out.println("Total number of inner cell boundaries:                     "+ cell_bound_number);
            System.out.println("Number of inner cell boundaries in the interior of grains: "+ gr_interior_cell_bound_number);
            System.out.println("Number of inner cell boundaries at grain boundaries:       "+ grain_bound_cell_bound_number);
            System.out.println("Number of inner cell boundaries near grain boundaries:     "+ grain_bound_paral_cell_bound_number);
            
            // Calculation of average stresses
            grain_bound_aver_stress       = grain_bound_aver_stress/grain_bound_cell_bound_number;
            grain_bound_paral_aver_stress = grain_bound_paral_aver_stress/grain_bound_paral_cell_bound_number;
            gr_interior_aver_stress       = gr_interior_aver_stress/gr_interior_cell_bound_number;
            gr_interior_aver_stress_Z     = gr_interior_aver_stress_Z/gr_interior_cell_bound_number;
            
            // Transition of average stress from Pa to MPa
            grain_bound_aver_stress       = grain_bound_aver_stress*1.0E-6;
            grain_bound_paral_aver_stress = grain_bound_paral_aver_stress*1.0E-6;
            gr_interior_aver_stress       = gr_interior_aver_stress*1.0E-6;
            gr_interior_aver_stress_Z     = gr_interior_aver_stress_Z*1.0E-6;
            
            System.out.println("Average stress at inner cell boundaries at grain boundaries:         "+ grain_bound_aver_stress+" MPa.");
            System.out.println("Average stress at inner cell boundaries near grain boundaries:       "+ grain_bound_paral_aver_stress+" MPa.");
            System.out.println("Average stress at inner cell boundaries in the interior of grains:   "+ gr_interior_aver_stress+" MPa.");
            System.out.println("Average stress_Z at inner cell boundaries in the interior of grains: "+ gr_interior_aver_stress_Z+" MPa.");
            
            // Calculation of average velocities of deformation front
            grain_bound_aver_velocity       = grain_bound_aver_velocity/grain_bound_cell_bound_number;
            grain_bound_paral_aver_velocity = grain_bound_paral_aver_velocity/grain_bound_paral_cell_bound_number;
            gr_interior_aver_velocity       = gr_interior_aver_velocity/gr_interior_cell_bound_number;
            gr_interior_aver_velocity_Z     = gr_interior_aver_velocity_Z/gr_interior_cell_bound_number;
            
            // Transition of average velocity of deformation front from m/s to mcm/s
            grain_bound_aver_velocity       = grain_bound_aver_velocity*1.0E6;
            grain_bound_paral_aver_velocity = grain_bound_paral_aver_velocity*1.0E6;
            gr_interior_aver_velocity       = gr_interior_aver_velocity*1.0E6;
            gr_interior_aver_velocity_Z     = gr_interior_aver_velocity_Z*1.0E6;
            
            System.out.println("Average velocity at inner cell boundaries at grain boundaries:         "+ grain_bound_aver_velocity+" mcm/s.");
            System.out.println("Average velocity at inner cell boundaries near grain boundaries:       "+ grain_bound_paral_aver_velocity+" mcm/s.");
            System.out.println("Average velocity at inner cell boundaries in the interior of grains:   "+ gr_interior_aver_velocity+" mcm/s.");
            System.out.println("Average velocity_Z at inner cell boundaries in the interior of grains: "+ gr_interior_aver_velocity_Z+" mcm/s.");
            
            bw_grain_bound_stresses.write        (time_period+" "+grain_bound_aver_stress+"\n");
            bw_grain_bound_paral_stresses.write  (time_period+" "+grain_bound_paral_aver_stress+"\n");
            bw_gr_interior_stresses.write        (time_period+" "+gr_interior_aver_stress+"\n");
            bw_gr_interior_stresses_Z.write      (time_period+" "+gr_interior_aver_stress_Z+"\n");
            
            bw_grain_bound_velocities.write      (time_period+" "+grain_bound_aver_velocity+"\n");
            bw_grain_bound_paral_velocities.write(time_period+" "+grain_bound_paral_aver_velocity+"\n");
            bw_gr_interior_velocities.write      (time_period+" "+gr_interior_aver_velocity+"\n");
            bw_gr_interior_velocities_Z.write    (time_period+" "+gr_interior_aver_velocity_Z+"\n");
            
            bw_grain_bound_stresses.flush();
            bw_grain_bound_paral_stresses.flush();
            bw_gr_interior_stresses.flush();
            bw_gr_interior_stresses_Z.flush();
            bw_grain_bound_velocities.flush();
            bw_grain_bound_paral_velocities.flush();
            bw_gr_interior_velocities.flush();
            bw_gr_interior_velocities_Z.flush();
            
            if(step_counter == step_number)
            {
                bw_grain_bound_stresses.close();
                bw_grain_bound_paral_stresses.close();
                bw_gr_interior_stresses.close();
                bw_gr_interior_stresses_Z.close();
                bw_grain_bound_velocities.close();
                bw_grain_bound_paral_velocities.close();
                bw_gr_interior_velocities.close();
                bw_gr_interior_velocities_Z.close();
                
                System.out.println("Files for writing are closed.");
            }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }
        
        System.out.println();
    }

    /** The method creates file with current parameters (temperatures and states) of task.
     * @param read_file
     */
    private void createCracksFile(String read_file_all_cells, String read_file_cracks)
    {
        String write_file = "./user/task_db/"+task_name+"/"+task_name+"_9999.res";

        String string;
        String string0, string1, string2, string3, string4;
        StringTokenizer st;

        // Parameters of cell
        double temperature, mech_stress, mech_energy;
        double coord_X, coord_Y, coord_Z;
        int state, type;
        Integer index;
        int damaged_cell_counter=0;
        Integer cell_counter = new Integer(-1);
        ArrayList indices = new ArrayList(0);
        ArrayList damaged_cells = new ArrayList(0);

        boolean first_string = true;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_all_cells));
            BufferedReader br1= new BufferedReader(new FileReader(read_file_cracks));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file));

            while(br1.ready())
            {
                string = br1.readLine();
                System.out.println(string);
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        if(!first_string)
                        {
                            index   = new Integer(string);
                            indices.add(index);
                            
                       //     index   = new Integer(st.nextToken());
                         //   index   = new Integer(st.nextToken());

                            coord_X = new Double(st.nextToken());
                            coord_Y = new Double(st.nextToken());
                            coord_Z = new Double(st.nextToken());

                            damaged_cells.add("11 2 1 "+coord_X+" "+coord_Y+" "+coord_Z+" 0.0 0.0 0 0 0 0 0 0.0 0.0");
                            damaged_cell_counter++;
                        }
                        else
                            first_string = false;
                    }
                }
            }
            
            System.out.println("Number of damaged cells: "+damaged_cell_counter);

            while(br.ready())
            {
                string = br.readLine();

                st = new StringTokenizer(string);
                string0 = st.nextToken();

                if(!string0.equals("#"))
                {
                    cell_counter++;

                    if(!indices.contains(cell_counter))
                    {
                        bw.write(string);
                        bw.newLine();
                    }
                    else
                    {
                        bw.write((String)damaged_cells.get(indices.indexOf(cell_counter)));
               //         bw.write(" "+cell_counter);
                        bw.newLine();
                    }

                    bw.flush();
                }
            }

            System.out.println("Number of cells: "+(cell_counter+1));
            
            br.close();
            br1.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file for graph of the dependence
     * "number of time step - number of grains".
     */
    public void createGraph()
    {
        String string;
        StringTokenizer st;

        String word1 = "", word2 = "", word3 = "", word4 = "",
               word5 = "", word6 = "", word7 = "", word8 = "";

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            bw.write("0 " + grains_number);
            bw.newLine();
            bw.flush();

            while (br.ready())
            {
                string = br.readLine();

                st = new StringTokenizer(string);

                word1 = "";
                word2 = "";
                word3 = "";
                word4 = "";
                word5 = "";
                word6 = "";
                word7 = "";
                word8 = "";

                if(st.hasMoreTokens())
                {
                    // Reading of words from string
                    word1 = st.nextToken();

                    if(word1.equals("Number"))
                      word2 = st.nextToken();

                    if(word2.equals("of"))
                      word3 = st.nextToken();

                    if(word3.equals("grains"))
                      word4 = st.nextToken();

                    if(word4.equals("at"))
                      word5 = st.nextToken();

                    if(word5.equals("time"))
                      word6 = st.nextToken();

                    if(word6.equals("step"))
                    {
                        word7 = st.nextToken();
                        word8 = st.nextToken();

                        bw.write(word7+" "+word8);
                        bw.newLine();
                        bw.flush();
                    }
                }
            }

            br.close();
            bw.close();

            System.out.println("File "+write_file_name+" is created!");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file for graph of the dependence
     * "number of time step - average linear size of grain".
     */
    public void createGraph2()
    {
        String string;
        StringTokenizer st;

        String word1 = "", word2 = "", word3 = "", word4 = "",
               word5 = "", word6 = "";

        String time_step_number = "0";

        double current_grains_number = grains_number,
               average_grain_size = Math.pow(specimen_volume/grains_number, 1.0/3);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

            bw.write(time_step_number+" "+average_grain_size);
            bw.newLine();
            bw.flush();

            while (br.ready())
            {
                string = br.readLine();

                st = new StringTokenizer(string);

                word1 = "";
                word2 = "";
                word3 = "";
                word4 = "";
                word5 = "";
                word6 = "";

                if(st.hasMoreTokens())
                {
                    // Reading of words from string
                    word1 = st.nextToken();

                    if(word1.equals("Number"))
                      word2 = st.nextToken();

                    if(word2.equals("of"))
                      word3 = st.nextToken();

                    if(word3.equals("grains"))
                      word4 = st.nextToken();

                    if(word4.equals("at"))
                      word5 = st.nextToken();

                    if(word5.equals("time"))
                      word6 = st.nextToken();

                    if(word6.equals("step"))
                    {
                        time_step_number = st.nextToken();
                        current_grains_number = new Double(st.nextToken()).doubleValue();

                        average_grain_size = Math.pow(specimen_volume/current_grains_number, 1.0/3);

                        bw.write(time_step_number+" "+average_grain_size);
                        bw.newLine();
                        bw.flush();
                    }
                }
            }

            br.close();
            bw.close();

            System.out.println("File "+write_file_name+" is created!");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file of embryos of cracks
     */
    public void createCrackEmbryosFile()
    {
        read_file_name  = "./task/result_2009_05_03_400000_stresses.txt";
        write_file_name = "./task/embryos_new.txt";

        String string;
    /*
        double centre_coord_X = 0;
        double centre_coord_Y = 0;
        double centre_coord_Z = 0;
     */
        // Parameters of cell
        int    state;
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        double stress;

        RecCellR3 defCellR3, neighb_cell;

        // Number of crack embryos
        int embryos_counter = 0;

        // Array of indices of cell neighbours
        int[] neighb_indices = new int[54];

        // Coordinates of central cell and its neighbours
        VectorR3[] cell_coordinates = new VectorR3[55];

        // Vectors from neighbour cells to central cell
        VectorR3[] neighb_vectors = new VectorR3[54];

        // Stresses of central cell and its neighbours
        double[] stresses = new double[55];

        // Gradients of mechanical energy of central cell and its neighbours
        double[] stress_gradients = new double[54];

        // Resultant vector
        VectorR3 result_vector;

        // Indices of cell
        cellcore.Three cell_indices;

        // List of coordinates of embryos
        ArrayList embryoCoordinates = new ArrayList(0);

        // Coordinates of embryo
        VectorR3 embryo_coordinates = new VectorR3(0,0,0);

        // Vector from current cell to embryo
        VectorR3 vector_to_embryo = new VectorR3(0,0,0);

        // Maximal distance between embryos (expressed in cell radiuses)
        double max_distance = Math.sqrt(specimen_size_X*specimen_size_X+
                                        specimen_size_Y*specimen_size_Y+
                                        specimen_size_Z*specimen_size_Z);

        // Distance from current cell to embryo
        double distance_to_embryo = max_distance;

        // The number of strings in the file with information about cell parameters
        //(state, coordinates, stress)
        int string_number=0;

        // The number of cells, which can generate embryo (5000<cell_stress<5100)
        int potential_embryos_number = 0;

        // Triple indices of embryo
        Three embryo_indices;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));

            // Reading of the number of strings of the file from the file
            string_number = new Integer(br.readLine()).intValue();

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                defCellR3 = (RecCellR3)specimenR3.get(stringCounter);
                defCellR3.setState((byte)-1);

                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state        = new Integer(st.nextToken()).intValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();
                    stress       = new Double(st.nextToken()).doubleValue();

                    defCellR3.setMechEnergy(0.5*stress*stress*cell_volume/defCellR3.get_mod_elast());
                    defCellR3.setMechStress(stress);
                   // defCellR3.setCoordinates(coordinate_X, coordinate_Y, coordinate_Z);
                }
            }
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
            BufferedWriter bw1= new BufferedWriter(new FileWriter("./task/damaged_volume_and_crack_embryos_new.txt"));

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                defCellR3 = (RecCellR3)specimenR3.get(stringCounter);

                stress = defCellR3.getMechStress();

                distance_to_embryo = max_distance;

                if((stress > 5000.0)&(stress < 5100.0))
                {
                    // Indices of cell
                    cell_indices = defCellR3.getIndices();

                    // Coordinates of cell
                    cell_coordinates[0] = calcCoordinates(cell_indices.getI(),
                                     cell_indices.getJ(), cell_indices.getK());

                    coordinate_X = cell_coordinates[0].getX();
                    coordinate_Y = cell_coordinates[0].getY();
                    coordinate_Z = cell_coordinates[0].getZ();

                    potential_embryos_number++;

                    for(int embr_counter = 0; embr_counter<embryoCoordinates.size(); embr_counter++)
                    {
                        // Coordinates of current embryo
                        embryo_coordinates = (VectorR3)embryoCoordinates.get(embr_counter);

                        // Calculation of coordinates of embryo from cell to current embryo
                        vector_to_embryo.setX(embryo_coordinates.getX()-coordinate_X);
                        vector_to_embryo.setY(embryo_coordinates.getY()-coordinate_Y);
                        vector_to_embryo.setZ(embryo_coordinates.getZ()-coordinate_Z);

                        // Calculation of distance from cell to the nearest embryo
                        distance_to_embryo = Math.min(distance_to_embryo, vector_to_embryo.getLength());

                        //TEST
                        System.out.print  (potential_embryos_number+" "+stringCounter+" "+coordinate_X+" "+coordinate_Y+" "+coordinate_Z);
                        System.out.println(" Vector length: "+vector_to_embryo.getLength()+" Min. dist.: "+distance_to_embryo);
                    }

                    System.out.println();
                }

                if(stress >= 5100.0)
                {
                    defCellR3.setState((byte)0);

                    // Indices of cell
                    cell_indices = defCellR3.getIndices();

                    // Coordinates of cell
                    cell_coordinates[0] = calcCoordinates(cell_indices.getI(),
                                     cell_indices.getJ(), cell_indices.getK());
                }

                // Generation of embryos in cells where stress varies from 5000 to 5100.
                if((stress > 5000.0)&(stress < 5100.0))
                if(distance_to_embryo>min_distance)
                if(Math.random() < EMBRYO_PROBABILITY)
                {
                    embryos_counter++;

                    //TEST
                    System.out.println(potential_embryos_number+" "+stringCounter+" "+coordinate_X+" "+coordinate_Y+" "+coordinate_Z);
                    System.out.print  ("Embryo # "+embryos_counter+". ");
                    System.out.println("Distance to nearest embryo: "+distance_to_embryo);
                    System.out.println();

                    // Indices of cell
                    cell_indices = defCellR3.getIndices();

                    // Single indices of cells at 1st, 2nd and 3rd coordination spheres
                    neighb_indices = neighbours3D[stringCounter];

                    // Coordinates of cell
                    cell_coordinates[0] = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());

                    // Addition of embryo coordinates to list
                    embryoCoordinates.add(cell_coordinates[0]);

                    // Mechanical energy of cell
                    stresses[0] = defCellR3.getMechStress();

                    for(int neighb_counter = 0; neighb_counter<neighb_indices.length; neighb_counter++)
                    if(neighb_indices[neighb_counter]>-1)
                    {
                        neighb_cell = (RecCellR3)specimenR3.get(neighb_indices[neighb_counter]);

                        // Indices of neighbour cell
                        cell_indices = neighb_cell.getIndices();

                        // Coordinates of neighbour cell
                        cell_coordinates[neighb_counter+1] = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());

                        // Mechanical energy of neighbour cell
                        stresses[neighb_counter+1] = neighb_cell.getMechStress();

                        // Vector from central cell to neighbour cell
                        neighb_vectors[neighb_counter] = residial(cell_coordinates[neighb_counter+1], cell_coordinates[0]);

                        // Gradients of mechanical energy of central cell and its neighbour
                        stress_gradients[neighb_counter] = stresses[0] - stresses[neighb_counter+1];

                /*
                 * //TEST
                        bw.write(stringCounter+" ");
                        bw.write(neighb_vectors[neighb_counter].getX()+" "+
                                 neighb_vectors[neighb_counter].getY()+" "+
                                 neighb_vectors[neighb_counter].getZ()+" "+
                                 stress_gradients[neighb_counter]);
                        bw.newLine();
                        bw.flush();
                 */
                    }
                    else
                    {
                        neighb_vectors  [neighb_counter] = new VectorR3(0, 0, 0);
                        stress_gradients[neighb_counter] = 0;
                    }

                    // Calculation of resultant vector
                    result_vector = calcResultVector(neighb_vectors, stress_gradients);

                    defCellR3.setState((byte)embryos_counter);

                    // Calculation of triple indices of embryo
                    embryo_indices = calcTripleIndex(stringCounter, cellNumberI,
                                                             cellNumberJ, cellNumberK);

                    bw.write(stringCounter+" "+result_vector.getX()+" "+
                             result_vector.getY()+" "+result_vector.getZ()+" 0.0 0.0 0.0 Indices: ("+
                             embryo_indices.getI()+", "+embryo_indices.getJ()+", "+embryo_indices.getK()+")");
                    bw.newLine();
                 // bw.newLine();
                    bw.flush();
                }

                if(defCellR3.getState()!=-1)
                {
                    bw1.write(defCellR3.getState()+" "+cell_coordinates[0].getX()+" "+
                              cell_coordinates[0].getY()+" "+cell_coordinates[0].getZ());
                    bw1.newLine();
                    bw1.flush();
                }
            }

            bw.write("Number of embryos: "+embryos_counter);
            bw.close();
            bw1.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        System.out.println("Number of embryos: "+embryos_counter);
    }

    /** The method creates file with information about force moments of cells
     * @param index number of time step
     */
    public void createForceMomentsFile(int index)
    {
        read_file_name  = "./task/mech_load_XY_"+index+".res";
        write_file_name = "./task/force_moments_2009_09_24_"+index+".txt";

        String string;

        RecCellR3 defCellR3;

        //Cell coordinates
        VectorR3 coordinates;

        // Parameters of cell
        byte    state = -1;
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        double temperature;
        double stress;
        byte   type = -1;

        // The number of strings in the file with information about cell parameters
        //(state, coordinates, stress)
        int string_number=0;
        
        int stringCounter = 0;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));

            // Reading of the number of strings of the file from the file
            string_number = new Integer(br.readLine()).intValue();

         //   for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            while(br.ready())
            {                
                defCellR3 = (RecCellR3)specimenR3.get(stringCounter);
                
                stringCounter++;
                
                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state        = new Byte(st.nextToken()).byteValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();
                    temperature  = new Double(st.nextToken()).doubleValue();
                    stress       = new Double(st.nextToken()).doubleValue();
                    type         = new Byte(st.nextToken()).byteValue();
                    type         = new Byte(st.nextToken()).byteValue();

                    coordinates  = new VectorR3(coordinate_X, coordinate_Y, coordinate_Z);

                    // Setting of coordinates and stress to cell
                    defCellR3.setState(state);
                    defCellR3.setCoordinates(coordinates);
                    defCellR3.setMechStress(stress);
                    defCellR3.setType(type);
                }
            }

            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        // Summary force moment of cell
        VectorR3 summary_force_moment;

        // Coordinates of cell
        VectorR3 cell_coordinates;

        BufferedWriter bw;

        int inner_cell_counter = 0;

        double coord_X, coord_Y, coord_Z;

        try
        {
            bw = new BufferedWriter(new FileWriter(write_file_name));

            // Spatial cycle over automata
            for(int cell_counter=0; cell_counter<specimenR3.size(); cell_counter++)
            {
              // Considerable cell
              defCellR3 = (RecCellR3)specimenR3.get(cell_counter);

              type = defCellR3.getType();

              if(type==1)
              {
                inner_cell_counter++;

                //Calculation of summary force moment of cell
                summary_force_moment = calcSummaryForceMoment(cell_counter);

                // Coordinates of cell
                cell_coordinates = defCellR3.getCoordinates();

                coord_X = cell_coordinates.getX() - cell_size_X*Math.sqrt(3.0/4.0);
                coord_Y = cell_coordinates.getY() - cell_size_Y;
                coord_Z = cell_coordinates.getZ() - cell_size_Z*Math.sqrt(2.0/3.0);
                
                coord_X = coord_X/cell_size_X;
                coord_Y = coord_Y/cell_size_Y;
                coord_Z = coord_Z/cell_size_Z;

                state = defCellR3.getState();

                // Writing of cell coordinates and coordinates of summary force moment vector to file
                bw.write(state+" "+coord_X+" "+coord_Y+" "+coord_Z+" "+
                         summary_force_moment.getX()+" "+summary_force_moment.getY()+" "+summary_force_moment.getZ());
                bw.newLine();
                bw.flush();
              }
            }

      //      bw.write(inner_cell_counter+"");
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method creates file with information about force moments of cells
     * @param index number of time step
     */
    public void createStressGradientsFile(int index)
    {
        read_file_name  = "./task/stresses_2009_06_12_"+index+".strs";
        write_file_name = "./task/stress_gradients_2009_06_12_"+index+".txt";

        String string;

        RecCellR3 defCellR3;

        //Cell coordinates
        VectorR3 coordinates;

        // Parameters of cell
        int    state;
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        double stress;

        // The number of strings in the file with information about cell parameters
        //(state, coordinates, stress)
        int string_number=0;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));

            // Reading of the number of strings of the file from the file
            string_number = new Integer(br.readLine()).intValue();

            for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            {
                defCellR3 = (RecCellR3)specimenR3.get(stringCounter);

                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state        = new Integer(st.nextToken()).intValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();
                    stress       = new Double(st.nextToken()).doubleValue();

                    coordinates  = new VectorR3(coordinate_X, coordinate_Y, coordinate_Z);

                    // Setting of coordinates and stress to cell
                    defCellR3.setCoordinates(coordinates);
                    defCellR3.setMechStress(stress);
                }
            }

            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        // Stress gradient for cell and its neighbours at 1st sphere
        VectorR3 stress_gradient;

        // Coordinates of cell
        VectorR3 cell_coordinates;

        BufferedWriter bw;

        try
        {
            bw = new BufferedWriter(new FileWriter(write_file_name));

            // Spatial cycle over automata
            for(int cell_counter=0; cell_counter<specimenR3.size(); cell_counter++)
            {
                //Calculation of stress gradient for cell
                //and its neighbours at 1st coordination sphere
                stress_gradient = calcStressGradient(cell_counter);

                // Considerable cell
                defCellR3 = (RecCellR3)specimenR3.get(cell_counter);

                // Coordinates of cell
                cell_coordinates = defCellR3.getCoordinates();

                // Writing of cell coordinates and coordinates of summary force moment vector to file
                bw.write(cell_coordinates.getX()+" "+cell_coordinates.getY()+" "+cell_coordinates.getZ()+" "+
                         stress_gradient.getX()+" "+stress_gradient.getY()+" "+stress_gradient.getZ());
                bw.newLine();
                bw.flush();
            }

            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method calculates vector of gradient of stresses
     * of cell and its neighbours at 1st coordination sphere
     * @param cell_index index of cell
     * @return vector of gradient of stresses
     *         of cell and its neighbours at 1st coordination sphere
     */
    public VectorR3 calcStressGradient(int cell_index)
    {
        // Considerable "central" cell
        RecCellR3 defCellR3 = (RecCellR3)specimenR3.get(cell_index);

        // Neighbour of "central" cell at 1st coordination sphere
        RecCellR3 neighb_cell;

        //Indices of neighbours at 1st coordination sphere of "central" cell
        int[] neighb1S_indices = neighbours1S[cell_index];

        // Indices of cell
        cellcore.Three cell_indices = defCellR3.getIndices();

        // Coordinates of "central" cell and its neighbours
        //at 1st coordination sphere
        VectorR3[] cell_coordinates = new VectorR3[13];

        // Mechanical stresses of "central" cell and its neighbours
        //at 1st coordination sphere
        double[] stresses = new double[13];

        // Vector from central cell to neighbour cell
        VectorR3[] neighb_vectors = new VectorR3[12];

        // Gradients of stresses between "central" cell
        // and its neighbours at 1st coordination sphere
        double[] stress_gradients = new double[12];

        // Coordinates of cell
        cell_coordinates[0] = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());

        // Mechanical energy of cell
        stresses[0] = defCellR3.getMechStress();

        for(int neighb_counter = 0; neighb_counter<neighb1S_indices.length; neighb_counter++)
        if(neighb1S_indices[neighb_counter]>-1)
        {
            neighb_cell = (RecCellR3)specimenR3.get(neighb1S_indices[neighb_counter]);

            // Indices of neighbour cell
            cell_indices = neighb_cell.getIndices();

            // Coordinates of neighbour cell
            cell_coordinates[neighb_counter+1] = calcCoordinates(cell_indices.getI(),
                                            cell_indices.getJ(), cell_indices.getK());

            // Mechanical energy of neighbour cell
            stresses[neighb_counter+1] = neighb_cell.getMechStress();

            // Vector from central cell to neighbour cell
            neighb_vectors[neighb_counter] = residial(cell_coordinates[neighb_counter+1], cell_coordinates[0]);

            // Gradients of mechanical energy of central cell and its neighbour
            stress_gradients[neighb_counter] = stresses[0] - stresses[neighb_counter+1];

            //TEST
            System.out.print  (cell_index+" ");
            System.out.println(neighb_vectors[neighb_counter].getX()+" "+
                     neighb_vectors[neighb_counter].getY()+" "+
                     neighb_vectors[neighb_counter].getZ()+" "+
                     stress_gradients[neighb_counter]);
            System.out.println();
        }
        else
        {
            neighb_vectors  [neighb_counter] = new VectorR3(0, 0, 0);
            stress_gradients[neighb_counter] = 0;
        }

        // Calculation of resultant vector
        return calcResultVector(neighb_vectors, stress_gradients);
    }

    /** The method calculates summary force moment of cell
     * @param cell_index index of cell
     * @return summary force moment of cell
     */
    public VectorR3 calcSummaryForceMoment(int cell_index)
    {
        // Considerable cell
        RecCellR3 defCellR3 = (RecCellR3)specimenR3.get(cell_index);

        //Indices of neighbours on 1st coordination sphere of "central" cell
        int[] neighb1S_indices = neighbours1S[cell_index];

        // List of indices of neighbours at 1st coordination sphere of "central" cell
        ArrayList centralCellNeihbIndices = new ArrayList(0);

        // Cell at 1st coordination sphere of "central" cell
        RecCellR3 cell1S;

        // Array of indices of neighbours of cell at 1st coordination sphere
        //of "central" cell
        int[] cell1S_neighb_indices = new int[12];

        // Neighbour of cell at 1st coordination sphere of "central" cell;
        //this neighbour is also located at 1st coordination sphere of "central" cell
        RecCellR3 cell1S_neighbour;

        // Stress from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
        double stress_from_cell1S_to_neighbour;

        // Vectors from "cell1S" to "central" cell
        VectorR3[] cell1S_vectors = new VectorR3[12];

        // Vector of stress from "cell1S" to its neighbour
        //located at 1st coordination sphere of "central" cell
        VectorR3 vector_from_cell1S_to_neighbour;

        // Vector of force moment of "central" cell corresponding to "cell1S"
        //and its neighbour located at 1st coordination sphere of "central" cell
        VectorR3 force_moment;

        // Summary force moment
        VectorR3 summary_force_moment = new VectorR3(0, 0, 0);

        // Cells at 1st coordination sphere of current "central" cell
        RecCellR3[] neighbCells = new RecCellR3[12];

        // Obtaining of all cells at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        {
            if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<specimenR3.size()))
            {
                // Neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
                neighbCells[neighb1S_counter] = (RecCellR3)specimenR3.get(neighb1S_indices[neighb1S_counter]);

                // Calculation of vector from "central" cell to "cell1S"
                cell1S_vectors[neighb1S_counter] = residial(neighbCells[neighb1S_counter].getCoordinates(),
                                                            defCellR3.getCoordinates());
            }

            // Addition of index of neighbour cell ("cell1S") to the list
            centralCellNeihbIndices.add(neighb1S_indices[neighb1S_counter]);
        }

        // Obtaining of neighbours of each "cell1S" at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<specimenR3.size()))
        {
            // Current cell at 1st coordination sphere of "central" cell
            cell1S = neighbCells[neighb1S_counter];

            // Indices of neighbours of current cell at 1st coordination sphere of "central" cell
            cell1S_neighb_indices = neighbours1S[neighb1S_indices[neighb1S_counter]];

       //     System.out.print(neighb1S_counter+" | "+neighb1S_indices[neighb1S_counter]+" || ");

            for(int cell1S_neighb_counter = 0; cell1S_neighb_counter<cell1S_neighb_indices.length; cell1S_neighb_counter++)
            if((cell1S_neighb_indices[cell1S_neighb_counter]>-1)&(cell1S_neighb_indices[cell1S_neighb_counter]<specimenR3.size()))
            {
                // Calculation of stresses from "cell1S" to its neighbours
                //located at 1st coordination sphere of "defCellR3"
                if(centralCellNeihbIndices.contains(cell1S_neighb_indices[cell1S_neighb_counter]))
                {
                 //   System.out.print(centralCellNeihbIndices.indexOf(cell1S_neighb_indices[cell1S_neighb_counter])+" "+cell1S_neighb_indices[cell1S_neighb_counter]+" | ");

                    // "Cell1S" neighbour located at 1st coordination sphere of "central" cell
                    cell1S_neighbour = (RecCellR3)specimenR3.get(cell1S_neighb_indices[cell1S_neighb_counter]);

                    // Stress from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    stress_from_cell1S_to_neighbour = cell1S.getMechStress()-cell1S_neighbour.getMechStress();

                    // Vector from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour = residial(cell1S_neighbour.getCoordinates(), cell1S.getCoordinates());

                    // Normalization of vector "vector_from_cell1S_to_neighbour"
                    vector_from_cell1S_to_neighbour.setX(vector_from_cell1S_to_neighbour.getX()/vector_from_cell1S_to_neighbour.getLength());
                    vector_from_cell1S_to_neighbour.setY(vector_from_cell1S_to_neighbour.getY()/vector_from_cell1S_to_neighbour.getLength());
                    vector_from_cell1S_to_neighbour.setZ(vector_from_cell1S_to_neighbour.getZ()/vector_from_cell1S_to_neighbour.getLength());

                    // Calculation of vector of force from "cell1S" to its neighbour
                    //located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour.setX(vector_from_cell1S_to_neighbour.getX()*
                                                         stress_from_cell1S_to_neighbour*cell_surface);
                    vector_from_cell1S_to_neighbour.setY(vector_from_cell1S_to_neighbour.getY()*
                                                         stress_from_cell1S_to_neighbour*cell_surface);
                    vector_from_cell1S_to_neighbour.setZ(vector_from_cell1S_to_neighbour.getZ()*
                                                         stress_from_cell1S_to_neighbour*cell_surface);

                    // Calculation of force moment for pair "cell1S - cell1S neighbour"
                    force_moment = calcVectorProduct(cell1S_vectors[neighb1S_counter], vector_from_cell1S_to_neighbour);

                    // Calculation of summary force moment
                    summary_force_moment.setX(summary_force_moment.getX()+force_moment.getX());
                    summary_force_moment.setY(summary_force_moment.getY()+force_moment.getY());
                    summary_force_moment.setZ(summary_force_moment.getZ()+force_moment.getZ());
                }
            }
          //  System.out.println();
        }

    //    System.out.println();

        return summary_force_moment;
    }

    /** The method creates file with information about principal stresses of cells
     * @param index number of time step
     */
    public void createPrincipalStressesFile(int index)
    {
        read_file_name  = "./task/mech_load_XY_"+index+".res";
        write_file_name = "./task/principal_stresses_2009_09_24_"+index+".txt";

        String string;

        // Parameters of cell
        byte   state = -1;
        double coordinate_X=0;
        double coordinate_Y=0;
        double coordinate_Z=0;
        double temperature=0;
        double stress;
        byte   type = -1;

        RecCellR3 defCellR3, neighb_cell;

        // Array of indices of cell neighbours
        int[] neighb_indices = new int[54];

        // Coordinates of central cell and its neighbours
        VectorR3[] cell_coordinates = new VectorR3[55];

        // Vectors from neighbour cells to central cell
        VectorR3[] neighb_vectors = new VectorR3[54];

        // Stresses of central cell and its neighbours
        double[] stresses = new double[55];

        // Gradients of mechanical energy of central cell and its neighbours
        double[] stress_gradients = new double[54];

        // Resultant vector
   //     VectorR3 result_vector;

        // The number of strings in the file with information about cell parameters
        //(state, coordinates, stress)
        int string_number=0;
        
        int string_counter = 0;

        // Principal stress of cell
        double principal_stress = 0;

        // Cell coordinates
        VectorR3 coordinates;

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(read_file_name));

            // Reading of the number of strings of the file from the file
            string_number = new Integer(br.readLine()).intValue();

        //    for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            while(br.ready())
            {
                defCellR3 = (RecCellR3)specimenR3.get(string_counter);

                string = br.readLine();
                StringTokenizer st = new StringTokenizer(string);
                
                string_counter++;

                if(st.hasMoreTokens())
                {
                    // Reading of coordinates and physical parameters of current cell
                    state        = new Byte(st.nextToken()).byteValue();
                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();
                    temperature  = new Double(st.nextToken()).doubleValue();
                    stress       = new Double(st.nextToken()).doubleValue();
                    type         = new Byte(st.nextToken()).byteValue();
                    type         = new Byte(st.nextToken()).byteValue();

                    coordinates  = new VectorR3(coordinate_X, coordinate_Y, coordinate_Z);

                    // Setting of coordinates and stress to cell
                    defCellR3.setState(state);
                    defCellR3.setCoordinates(coordinates);
                    defCellR3.setMechStress(stress);
                    defCellR3.setType(type);
                }
            }

            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        // Counter of cells
        int cell_counter = 0;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));

         //   bw.write(string_number+"");
         //   bw.newLine();

            for (int stringCounter = 0; stringCounter < specimenR3.size(); stringCounter++)
            {
              defCellR3 = (RecCellR3)specimenR3.get(stringCounter);

              // State of cell: index of grain containing cell
              type = defCellR3.getType();

              if(type==1)
              {
                cell_counter++;

                // Coordinates of cell
                coordinates = defCellR3.getCoordinates();

                coordinate_X = coordinates.getX() - cell_size_X*Math.sqrt(3.0/4.0);
                coordinate_Y = coordinates.getY() - cell_size_Y;
                coordinate_Z = coordinates.getZ() - cell_size_Z*Math.sqrt(2.0/3.0);

                coordinate_X = coordinate_X/cell_size_X;
                coordinate_Y = coordinate_Y/cell_size_Y;
                coordinate_Z = coordinate_Z/cell_size_Z;

                // Stress of cell
                stress = defCellR3.getMechStress();

                principal_stress = 0;

                // Single indices of cells at 1st, 2nd and 3rd coordination spheres
                neighb_indices = neighbours3D[stringCounter];

                // Mechanical energy of cell
                stresses[0] = defCellR3.getMechStress();

                state = defCellR3.getState();

                for(int neighb_counter = 0; neighb_counter<neighb_indices.length; neighb_counter++)
                if(neighb_indices[neighb_counter]>-1)
                {
                    neighb_cell = (RecCellR3)specimenR3.get(neighb_indices[neighb_counter]);

                    // Mechanical energy of neighbour cell
                    stresses[neighb_counter+1] = neighb_cell.getMechStress();

                    // Gradients of mechanical energy of central cell and its neighbour
                    stress_gradients[neighb_counter] = stresses[neighb_counter+1] - stresses[0];

                    principal_stress = principal_stress + stress_gradients[neighb_counter];
                }
                else
                {
                    stress_gradients[neighb_counter] = 0;
                }

                // Calculation of principal stress of cell
                principal_stress = principal_stress/12;

              //  defCellR3.setPrincipalStress(principal_stress);

                bw.write(state+" "+coordinate_X+" "+coordinate_Y+" "+coordinate_Z+" "+principal_stress);
                bw.newLine();
                bw.flush();
              }
            }

          //  bw.write(cell_counter+"");
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

    /** The method calculates triple index of cell3D with certain single index.
     * @param intIndex single index of cell3D
     * @param size_X number of allowed values of 1st element of triple index.
     * @param size_Y number of allowed values of 2nd element of triple index.
     * @param size_Z number of allowed values of 3rd element of triple index.
     * @return triple index of cell3D
     */
    public Three calcTripleIndex(int intIndex, int size_X, int size_Y, int size_Z)
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

    /** The method loads information about sizes of cell,
     * sizes and physical properties of specimen.
     * @param file_name name of file keeping all the information about specimen.
     */
    public void loadStructure(String file_name)
    {
        Properties task_properties = new Properties();

        try
        {
            FileInputStream fin = new FileInputStream(file_name);
            task_properties.load(fin);
            fin.close();
        }
        catch(IOException io_ex)
        {
            System.out.println("ERROR: "+io_ex);
        }

        // Reading of data about geometry of specimen
        specimen_size_X         = (new Double(task_properties.getProperty("specimen_size_X"))).doubleValue();
        specimen_size_Y         = (new Double(task_properties.getProperty("specimen_size_Y"))).doubleValue();
        specimen_size_Z         = (new Double(task_properties.getProperty("specimen_size_Z"))).doubleValue();

        int element_number_X    = (new Integer(task_properties.getProperty("element_number_X"))).intValue();
        int element_number_Y    = (new Integer(task_properties.getProperty("element_number_Y"))).intValue();
        int element_number_Z    = (new Integer(task_properties.getProperty("element_number_Z"))).intValue();

        elementSizeI            = (new Integer(task_properties.getProperty("cell_number_X"))).intValue();
        elementSizeJ            = (new Integer(task_properties.getProperty("cell_number_Y"))).intValue();
        elementSizeK            = (new Integer(task_properties.getProperty("cell_number_Z"))).intValue();

//        min_distance            = (new Double(task_properties.getProperty("min_distance"))).doubleValue();
//        EMBRYO_PROBABILITY      = (new Double(task_properties.getProperty("EMBRYO_PROBABILITY"))).doubleValue();

        element_size_X = specimen_size_X/element_number_X;
        element_size_Y = specimen_size_Y/element_number_Y;
        element_size_Z = specimen_size_Z/element_number_Z;

        cell_size_X = element_size_X/elementSizeI;
        cell_size_Y = element_size_Y/elementSizeJ;
        cell_size_Z = element_size_Z/elementSizeK;
    }

    /** The method returns indices of cells at 1st, 2nd and 3rd coordination spheres of given cell.
     * @param _intIndex index of given cell
     * @see neighbours3D
     */
    public void setNeighbours1S(int _intIndex)
    {
        // Triple index of cell
        Three _tripleIndex = calcTripleIndex(_intIndex, cellNumberI, cellNumberJ, cellNumberK);

        // Indices of considered cell
        int index1 = _tripleIndex.getI();
        int index2 = _tripleIndex.getJ();
        int index3 = _tripleIndex.getK();

        // Triple indices of cells at 1st, 2nd and 3rd coordinational spheres
        Three[] neighbours = new Three[12];

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

        int neighbour_indexI;
        int neighbour_indexJ;
        int neighbour_indexK;

        // If each triple index of neighbour cell is within boundaries then
        // the single index of the cell is calculated, else
        // this cell is deleted from the array of neighbours (single index = -3).
        for (int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
        {
            neighbour_indexI = neighbours[neighb_counter].getI();
            neighbour_indexJ = neighbours[neighb_counter].getJ();
            neighbour_indexK = neighbours[neighb_counter].getK();

            if((neighbour_indexI > -1)&(neighbour_indexI < cellNumberI)&
               (neighbour_indexJ > -1)&(neighbour_indexJ < cellNumberJ)&
               (neighbour_indexK > -1)&(neighbour_indexK < cellNumberK))
            {
                neighbours1S[_intIndex][neighb_counter] = neighbour_indexI + neighbour_indexJ*cellNumberI +
                                                          neighbour_indexK*cellNumberI*cellNumberJ;
            }
            else
                neighbours1S[_intIndex][neighb_counter] = -3;
        }
    }

    /** The method returns indices of cells at 1st, 2nd and 3rd coordination spheres of given cell.
     * @param _intIndex index of given cell
     * @see neighbours3D
     */
    public void setNeighbours3D(int _intIndex)
    {
        // Triple index of cell
        // Triple index of cell
        Three _tripleIndex = calcTripleIndex(_intIndex, cellNumberI, cellNumberJ, cellNumberK);

        // Indices of considered cell
        int index1 = _tripleIndex.getI();
        int index2 = _tripleIndex.getJ();
        int index3 = _tripleIndex.getK();

        // Triple indices of cells at 1st, 2nd and 3rd coordinational spheres
        Three[] neighbours = new Three[54];

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

            neighbours[12] = new Three(index1-2, index2  , index3-1);
            neighbours[13] = new Three(index1  , index2-1, index3-1);
            neighbours[14] = new Three(index1  , index2+1, index3-1);
            neighbours[15] = new Three(index1-1, index2-1, index3+1);
            neighbours[16] = new Three(index1+1, index2  , index3+1);
            neighbours[17] = new Three(index1-1, index2+1, index3+1);

            neighbours[18] = new Three(index1-1, index2  , index3-2);
            neighbours[19] = new Three(index1  , index2-1, index3-2);
            neighbours[20] = new Three(index1-2, index2-1, index3-1);
            neighbours[21] = new Three(index1-1, index2-2, index3-1);
            neighbours[22] = new Three(index1  , index2  , index3-2);
            neighbours[23] = new Three(index1-2, index2+1, index3-1);
            neighbours[24] = new Three(index1-1, index2+1, index3-1);
            neighbours[25] = new Three(index1+1, index2-1, index3-1);
            neighbours[26] = new Three(index1+1, index2  , index3-1);
            neighbours[27] = new Three(index1-2, index2  , index3  );
            neighbours[28] = new Three(index1-1, index2-2, index3  );
            neighbours[29] = new Three(index1-2, index2-1, index3+1);
            neighbours[30] = new Three(index1-1, index2+1, index3  );
            neighbours[31] = new Three(index1-2, index2  , index3+1);
            neighbours[32] = new Three(index1+1, index2-2, index3  );
            neighbours[33] = new Three(index1  , index2-2, index3+1);
            neighbours[34] = new Three(index1+1, index2+1, index3  );
            neighbours[35] = new Three(index1  , index2+1, index3+1);
            neighbours[36] = new Three(index1+2, index2  , index3  );
            neighbours[37] = new Three(index1+1, index2-1, index3+1);
            neighbours[38] = new Three(index1+1, index2+1, index3+1);
            neighbours[39] = new Three(index1-1, index2-1, index3+2);
            neighbours[40] = new Three(index1-1, index2  , index3+2);
            neighbours[41] = new Three(index1  , index2  , index3+2);

            neighbours[42] = new Three(index1-1, index2-1, index3-2);
            neighbours[43] = new Three(index1-1, index2+1, index3-2);
            neighbours[44] = new Three(index1+1, index2  , index3-2);
            neighbours[45] = new Three(index1-2, index2-1, index3  );
            neighbours[46] = new Three(index1-2, index2+1, index3  );
            neighbours[47] = new Three(index1  , index2-2, index3  );
            neighbours[48] = new Three(index1  , index2+2, index3  );
            neighbours[49] = new Three(index1+2, index2-1, index3  );
            neighbours[50] = new Three(index1+2, index2+1, index3  );
            neighbours[51] = new Three(index1-2, index2  , index3+2);
            neighbours[52] = new Three(index1  , index2-1, index3+2);
            neighbours[53] = new Three(index1  , index2+1, index3+2);
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

                neighbours[12] = new Three(index1-1, index2  , index3-1);
            	neighbours[13] = new Three(index1+1, index2-1, index3-1);
            	neighbours[14] = new Three(index1+1, index2+1, index3-1);
            	neighbours[15] = new Three(index1-1, index2-1, index3+1);
            	neighbours[16] = new Three(index1-1, index2+1, index3+1);
            	neighbours[17] = new Three(index1+1, index2  , index3+1);

            	neighbours[18] = new Three(index1-1, index2  , index3-2);
            	neighbours[19] = new Three(index1  , index2  , index3-2);
            	neighbours[20] = new Three(index1-1, index2-1, index3-1);
            	neighbours[21] = new Three(index1  , index2-1, index3-1);
            	neighbours[22] = new Three(index1  , index2+1, index3-2);
            	neighbours[23] = new Three(index1-1, index2+1, index3-1);

                neighbours[24] = new Three(index1  , index2+2, index3-1);
            	neighbours[25] = new Three(index1+2, index2  , index3-1);
            	neighbours[26] = new Three(index1+2, index2+1, index3-1);
            	neighbours[27] = new Three(index1-2, index2  , index3  );
            	neighbours[28] = new Three(index1-1, index2-1, index3  );
            	neighbours[29] = new Three(index1-2, index2  , index3+1);

            	neighbours[30] = new Three(index1-1, index2+2, index3  );
            	neighbours[31] = new Three(index1-2, index2+1, index3+1);
            	neighbours[32] = new Three(index1+1, index2-1, index3  );
            	neighbours[33] = new Three(index1  , index2-1, index3+1);
            	neighbours[34] = new Three(index1+1, index2+2, index3  );
            	neighbours[35] = new Three(index1  , index2+2, index3+1);

                neighbours[36] = new Three(index1+2, index2  , index3  );
            	neighbours[37] = new Three(index1+1, index2-1, index3+1);
            	neighbours[38] = new Three(index1+1, index2+1, index3+1);
                neighbours[39] = new Three(index1  , index2  , index3+2);
            	neighbours[40] = new Three(index1  , index2+1, index3+2);
            	neighbours[41] = new Three(index1+1, index2  , index3+2);

            	neighbours[42] = new Three(index1-1, index2-1, index3-2);
            	neighbours[43] = new Three(index1-1, index2+1, index3-2);
            	neighbours[44] = new Three(index1+1, index2  , index3-2);
            	neighbours[45] = new Three(index1-2, index2-1, index3  );
            	neighbours[46] = new Three(index1-2, index2+1, index3  );
            	neighbours[47] = new Three(index1  , index2-2, index3  );

                neighbours[48] = new Three(index1  , index2+2, index3  );
            	neighbours[49] = new Three(index1+2, index2-1, index3  );
            	neighbours[50] = new Three(index1+2, index2+1, index3  );
            	neighbours[51] = new Three(index1-1, index2  , index3+2);
            	neighbours[52] = new Three(index1+1, index2-1, index3+2);
            	neighbours[53] = new Three(index1+1, index2+1, index3+2);
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

                neighbours[12] = new Three(index1-1, index2  , index3-1);
            	neighbours[13] = new Three(index1+1, index2-1, index3-1);
            	neighbours[14] = new Three(index1+1, index2+1, index3-1);
            	neighbours[15] = new Three(index1  , index2-1, index3+1);
            	neighbours[16] = new Three(index1  , index2+1, index3+1);
            	neighbours[17] = new Three(index1+2, index2  , index3+1);

            	neighbours[18] = new Three(index1  , index2  , index3-2);
            	neighbours[19] = new Three(index1+1, index2-1, index3-2);
            	neighbours[20] = new Three(index1-1, index2-1, index3-1);
            	neighbours[21] = new Three(index1  , index2-2, index3-1);
            	neighbours[22] = new Three(index1+1, index2  , index3-2);
            	neighbours[23] = new Three(index1-1, index2+1, index3-1);

                neighbours[24] = new Three(index1  , index2+1, index3-1);
            	neighbours[25] = new Three(index1+2, index2-1, index3-1);
            	neighbours[26] = new Three(index1+2, index2  , index3-1);
            	neighbours[27] = new Three(index1-2, index2  , index3  );
            	neighbours[28] = new Three(index1-1, index2-2, index3  );
            	neighbours[29] = new Three(index1-1, index2-1, index3+1);

            	neighbours[30] = new Three(index1-1, index2+1, index3  );
            	neighbours[31] = new Three(index1-1, index2  , index3+1);
            	neighbours[32] = new Three(index1+1, index2-2, index3  );
            	neighbours[33] = new Three(index1+1, index2-2, index3+1);
            	neighbours[34] = new Three(index1+1, index2+1, index3  );
            	neighbours[35] = new Three(index1+1, index2+1, index3+1);

                neighbours[36] = new Three(index1+2, index2  , index3  );
            	neighbours[37] = new Three(index1+2, index2-1, index3+1);
            	neighbours[38] = new Three(index1+2, index2+1, index3+1);
                neighbours[39] = new Three(index1  , index2-1, index3+2);
            	neighbours[40] = new Three(index1  , index2  , index3+2);
            	neighbours[41] = new Three(index1+1, index2  , index3+2);

                neighbours[42] = new Three(index1  , index2-1, index3-2);
            	neighbours[43] = new Three(index1  , index2+1, index3-2);
            	neighbours[44] = new Three(index1+2, index2  , index3-2);
            	neighbours[45] = new Three(index1-2, index2-1, index3  );
            	neighbours[46] = new Three(index1-2, index2+1, index3  );
            	neighbours[47] = new Three(index1  , index2-2, index3  );

                neighbours[48] = new Three(index1  , index2+2, index3  );
            	neighbours[49] = new Three(index1+2, index2-1, index3  );
            	neighbours[50] = new Three(index1+2, index2+1, index3  );
            	neighbours[51] = new Three(index1-1, index2  , index3+2);
            	neighbours[52] = new Three(index1+1, index2-1, index3+2);
            	neighbours[53] = new Three(index1+1, index2+1, index3+2);
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

                neighbours[12] = new Three(index1-2, index2  , index3-1);
            	neighbours[13] = new Three(index1  , index2-1, index3-1);
            	neighbours[14] = new Three(index1  , index2+1, index3-1);
            	neighbours[15] = new Three(index1-1, index2-1, index3+1);
            	neighbours[16] = new Three(index1-1, index2+1, index3+1);
            	neighbours[17] = new Three(index1+1, index2  , index3+1);

            	neighbours[18] = new Three(index1-1, index2  , index3-2);
            	neighbours[19] = new Three(index1  , index2  , index3-2);
            	neighbours[20] = new Three(index1-2, index2-1, index3-1);
            	neighbours[21] = new Three(index1-1, index2-1, index3-1);
            	neighbours[22] = new Three(index1  , index2+1, index3-2);
            	neighbours[23] = new Three(index1-2, index2+1, index3-1);

                neighbours[24] = new Three(index1-1, index2+2, index3-1);
            	neighbours[25] = new Three(index1+1, index2  , index3-1);
            	neighbours[26] = new Three(index1+1, index2+1, index3-1);
            	neighbours[27] = new Three(index1-2, index2  , index3  );
            	neighbours[28] = new Three(index1-1, index2-1, index3  );
            	neighbours[29] = new Three(index1-2, index2  , index3+1);

            	neighbours[30] = new Three(index1-1, index2+2, index3  );
            	neighbours[31] = new Three(index1-2, index2+1, index3+1);
            	neighbours[32] = new Three(index1+1, index2-1, index3  );
            	neighbours[33] = new Three(index1  , index2-1, index3+1);
            	neighbours[34] = new Three(index1+1, index2+2, index3  );
            	neighbours[35] = new Three(index1  , index2+2, index3+1);

                neighbours[36] = new Three(index1+2, index2  , index3  );
            	neighbours[37] = new Three(index1+1, index2-1, index3+1);
            	neighbours[38] = new Three(index1+1, index2+1, index3+1);
                neighbours[39] = new Three(index1-1, index2  , index3+2);
            	neighbours[40] = new Three(index1-1, index2+1, index3+2);
            	neighbours[41] = new Three(index1  , index2  , index3+2);

                neighbours[42] = new Three(index1-1, index2-1, index3-2);
            	neighbours[43] = new Three(index1-1, index2+1, index3-2);
            	neighbours[44] = new Three(index1+1, index2  , index3-2);
            	neighbours[45] = new Three(index1-2, index2-1, index3  );
            	neighbours[46] = new Three(index1-2, index2+1, index3  );
            	neighbours[47] = new Three(index1  , index2-2, index3  );

                neighbours[48] = new Three(index1  , index2+2, index3  );
            	neighbours[49] = new Three(index1+2, index2-1, index3  );
            	neighbours[50] = new Three(index1+2, index2+1, index3  );
            	neighbours[51] = new Three(index1-2, index2  , index3+2);
            	neighbours[52] = new Three(index1  , index2-1, index3+2);
            	neighbours[53] = new Three(index1  , index2+1, index3+2);
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

                neighbours[12] = new Three(index1-1, index2  , index3-1);
            	neighbours[13] = new Three(index1+1, index2-1, index3-1);
            	neighbours[14] = new Three(index1+1, index2+1, index3-1);
            	neighbours[15] = new Three(index1-1, index2-1, index3+1);
            	neighbours[16] = new Three(index1-1, index2+1, index3+1);
            	neighbours[17] = new Three(index1+1, index2  , index3+1);

            	neighbours[18] = new Three(index1-1, index2  , index3-2);
            	neighbours[19] = new Three(index1  , index2-1, index3-2);
            	neighbours[20] = new Three(index1-1, index2-1, index3-1);
            	neighbours[21] = new Three(index1  , index2-2, index3-1);
            	neighbours[22] = new Three(index1  , index2  , index3-2);
            	neighbours[23] = new Three(index1-1, index2+1, index3-1);

                neighbours[24] = new Three(index1  , index2+1, index3-1);
            	neighbours[25] = new Three(index1+2, index2-1, index3-1);
            	neighbours[26] = new Three(index1+2, index2  , index3-1);
            	neighbours[27] = new Three(index1-2, index2  , index3  );
            	neighbours[28] = new Three(index1-1, index2-2, index3  );
            	neighbours[29] = new Three(index1-2, index2-1, index3+1);

            	neighbours[30] = new Three(index1-1, index2+1, index3  );
            	neighbours[31] = new Three(index1-2, index2  , index3+1);
            	neighbours[32] = new Three(index1+1, index2-2, index3  );
            	neighbours[33] = new Three(index1  , index2-2, index3+1);
            	neighbours[34] = new Three(index1+1, index2+1, index3  );
            	neighbours[35] = new Three(index1  , index2+1, index3+1);

                neighbours[36] = new Three(index1+2, index2  , index3  );
            	neighbours[37] = new Three(index1+1, index2-1, index3+1);
            	neighbours[38] = new Three(index1+1, index2+1, index3+1);
                neighbours[39] = new Three(index1  , index2-1, index3+2);
            	neighbours[40] = new Three(index1  , index2  , index3+2);
            	neighbours[41] = new Three(index1+1, index2  , index3+2);

                neighbours[42] = new Three(index1-1, index2-1, index3-2);
            	neighbours[43] = new Three(index1-1, index2+1, index3-2);
            	neighbours[44] = new Three(index1+1, index2  , index3-2);
            	neighbours[45] = new Three(index1-2, index2-1, index3  );
            	neighbours[46] = new Three(index1-2, index2+1, index3  );
            	neighbours[47] = new Three(index1  , index2-2, index3  );

                neighbours[48] = new Three(index1  , index2+2, index3  );
            	neighbours[49] = new Three(index1+2, index2-1, index3  );
            	neighbours[50] = new Three(index1+2, index2+1, index3  );
            	neighbours[51] = new Three(index1-1, index2  , index3+2);
            	neighbours[52] = new Three(index1+1, index2-1, index3+2);
            	neighbours[53] = new Three(index1+1, index2+1, index3+2);
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

                neighbours[12] = new Three(index1-1, index2  , index3-1);
            	neighbours[13] = new Three(index1+1, index2-1, index3-1);
            	neighbours[14] = new Three(index1+1, index2+1, index3-1);
            	neighbours[15] = new Three(index1  , index2-1, index3+1);
            	neighbours[16] = new Three(index1  , index2+1, index3+1);
            	neighbours[17] = new Three(index1+2, index2  , index3+1);

            	neighbours[18] = new Three(index1  , index2  , index3-2);
            	neighbours[19] = new Three(index1+1, index2  , index3-2);
            	neighbours[20] = new Three(index1-1, index2-1, index3-1);
            	neighbours[21] = new Three(index1  , index2-1, index3-1);
            	neighbours[22] = new Three(index1+1, index2+1, index3-2);
            	neighbours[23] = new Three(index1-1, index2+1, index3-1);

                neighbours[24] = new Three(index1  , index2+2, index3-1);
            	neighbours[25] = new Three(index1+2, index2  , index3-1);
            	neighbours[26] = new Three(index1+2, index2+1, index3-1);
            	neighbours[27] = new Three(index1-2, index2  , index3  );
            	neighbours[28] = new Three(index1-1, index2-1, index3  );
            	neighbours[29] = new Three(index1-1, index2  , index3+1);

            	neighbours[30] = new Three(index1-1, index2+2, index3  );
            	neighbours[31] = new Three(index1-1, index2+1, index3+1);
            	neighbours[32] = new Three(index1+1, index2-1, index3  );
            	neighbours[33] = new Three(index1+1, index2-1, index3+1);
            	neighbours[34] = new Three(index1+1, index2+2, index3  );
            	neighbours[35] = new Three(index1+1, index2+2, index3+1);

                neighbours[36] = new Three(index1+2, index2  , index3  );
            	neighbours[37] = new Three(index1+2, index2-1, index3+1);
            	neighbours[38] = new Three(index1+2, index2+1, index3+1);
                neighbours[39] = new Three(index1  , index2  , index3+2);
            	neighbours[40] = new Three(index1  , index2+1, index3+2);
            	neighbours[41] = new Three(index1+1, index2  , index3+2);

                neighbours[42] = new Three(index1  , index2-1, index3-2);
            	neighbours[43] = new Three(index1  , index2+1, index3-2);
            	neighbours[44] = new Three(index1+2, index2  , index3-2);
            	neighbours[45] = new Three(index1-2, index2-1, index3  );
            	neighbours[46] = new Three(index1-2, index2+1, index3  );
            	neighbours[47] = new Three(index1  , index2-2, index3  );

                neighbours[48] = new Three(index1  , index2+2, index3  );
            	neighbours[49] = new Three(index1+2, index2-1, index3  );
            	neighbours[50] = new Three(index1+2, index2+1, index3  );
            	neighbours[51] = new Three(index1-1, index2  , index3+2);
            	neighbours[52] = new Three(index1+1, index2-1, index3+2);
            	neighbours[53] = new Three(index1+1, index2+1, index3+2);
        }

        int neighbour_indexI;
        int neighbour_indexJ;
        int neighbour_indexK;

        // If each triple index of neighbour cell is within boundaries then
        // the single index of the cell is calculated, else
        // this cell is deleted from the array of neighbours (single index = -3).
        for (int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
        {
            neighbour_indexI = neighbours[neighb_counter].getI();
            neighbour_indexJ = neighbours[neighb_counter].getJ();
            neighbour_indexK = neighbours[neighb_counter].getK();

            if((neighbour_indexI > -1)&(neighbour_indexI < cellNumberI)&
               (neighbour_indexJ > -1)&(neighbour_indexJ < cellNumberJ)&
               (neighbour_indexK > -1)&(neighbour_indexK < cellNumberK))
            {
                neighbours3D[_intIndex][neighb_counter] = neighbour_indexI + neighbour_indexJ*cellNumberI +
                        neighbour_indexK*cellNumberI*cellNumberJ;
            }
            else
                neighbours3D[_intIndex][neighb_counter] = -3;
        }
    }

    /** The method calculates single index of cell3D with certain triple index.
     * @param triple_index triple index of cell3D.
     * @return single index of cell3D.
     */
    public int calcSingleIndex(Three triple_index)
    {
        // Single index of cell3D.
        int single_index = -1;

        // Getting of indices i, j, k of given triple index.
        int indexI = triple_index.getI();
        int indexJ = triple_index.getJ();
        int indexK = triple_index.getK();

        // Calculation of single index of cell3D.
        if ((indexI > -1)&(indexI < cellNumberI)&
            (indexJ > -1)&(indexJ < cellNumberJ)&
            (indexK > -1)&(indexK < cellNumberK))
        {
            single_index = indexI + cellNumberI*indexJ +
                           cellNumberI*cellNumberJ*indexK;
        }

        return single_index;
    }

    /** The method calculates and returns 3D coordinates of cell with certain
     * triple index.
     * @param index1 1st index of cell
     * @param index2 2nd index of cell
     * @param index3 3rd index of cell
     * @return 3D coordinates of cell
     */
    private VectorR3 calcCoordinates(int index1, int index2, int index3)
    {
        // Coordinates of cell
        double coord_X=0, coord_Y=0, coord_Z=0;

        // Calculation of coordinates of cell
        if(index3 % 3 == 0)
        {
            if(index1 % 2 == 0)
            {
                coord_X = cell_size_X*(1 + index1*Math.sqrt(3));
                coord_Y = cell_size_Y*(1 + index2*2);
            }
            else
            {
                coord_X = cell_size_X*(1 + index1*Math.sqrt(3));
                coord_Y = cell_size_Y*(2 + index2*2);
            }
        }

        if(index3 % 3 == 1)
        {
            if(index1 % 2 == 0)
            {
                coord_X = cell_size_X*(1 + 1/Math.sqrt(3) + index1*Math.sqrt(3));
                coord_Y = cell_size_Y*(2 + index2*2);
            }
            else
            {
                coord_X = cell_size_X*(1 + 1/Math.sqrt(3)+ index1*Math.sqrt(3));
                coord_Y = cell_size_Y*(1 + index2*2);
            }
        }

        if(index3 % 3 == 2)
        {
            if(index1 % 2 == 0)
            {
                coord_X = cell_size_X*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));
                coord_Y = cell_size_Y*(1 + index2*2);
            }
            else
            {
                coord_X = cell_size_X*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));
                coord_Y = cell_size_Y*(2 + index2*2);
            }
        }

        coord_Z = cell_size_Z*(1 + 2*index3*Math.sqrt(2.0/3.0));

        return new VectorR3(coord_X, coord_Y, coord_Z);
    }

    /** The method returns residial of two vectors (vect_1 - vect_2).
     * @param vect_1 minuend vector
     * @param vect_2 subtrahend vector
     * @return residial of two vectors (vect_1 - vect_2)
     */
    public VectorR3 residial(VectorR3 vect_1, VectorR3 vect_2)
    {
        return new VectorR3 (vect_1.getX() - vect_2.getX(),
                             vect_1.getY() - vect_2.getY(),
                             vect_1.getZ() - vect_2.getZ());
    }

    /** The method returns vector product of two vectors (vect_1 X vect_2).
     * @param vect_1 first vector
     * @param vect_2 second vector
     * @return vector product of two vectors (vect_1 X vect_2)
     */
    public VectorR3 calcVectorProduct(VectorR3 vect_1, VectorR3 vect_2)
    {
        // Coordinates of 1st vector
        double vect1_X =  vect_1.getX();
        double vect1_Y =  vect_1.getY();
        double vect1_Z =  vect_1.getZ();

        // Coordinates of 2nd vector
        double vect2_X =  vect_2.getX();
        double vect2_Y =  vect_2.getY();
        double vect2_Z =  vect_2.getZ();

        // Coordinates of vector product
        double coord_X = vect1_Y*vect2_Z - vect1_Z*vect2_Y;
        double coord_Y = vect1_Z*vect2_X - vect1_X*vect2_Z;
        double coord_Z = vect1_X*vect2_Y - vect1_Y*vect2_X;

        return new VectorR3 (coord_X, coord_Y, coord_Z);
    }

    /** The method calculates resultant vector for vectors of gradients
     * of mechanical energy of cell and its neighbours
     * @param vectors vectors from cell to its neighbours
     * @param gradients gradients of mechanical energy of cell and its neighbours
     * @return resultant vector for vectors of gradients
     *         of mechanical energy of cell and its neighbours
     */
    public VectorR3 calcResultVector(VectorR3[] vectors, double[] gradients)
    {
        VectorR3 result_vector = new VectorR3(0, 0, 0);

        double coord_X=0, coord_Y=0, coord_Z=0;

        //   Factors correspond to vectors from cell to its neighbours:
        // these factors are used for correct calculation of resultant vector
        double factors[] = new double[54];

        for(int fact_counter = 0; fact_counter<factors.length; fact_counter++)
        {
            if(vectors[fact_counter].getLength()!=0)
                factors[fact_counter] = 1.0/vectors[fact_counter].getLength();
            else
                factors[fact_counter] = 0;
        }

        // Vectors from cell to its neighbours are normalized,
        //then resultant vector is calculated
        for(int neighb_counter = 0; neighb_counter<vectors.length; neighb_counter++)
        if(vectors[neighb_counter].getLength()!=0)
        {
            coord_X = coord_X + factors[neighb_counter]*gradients[neighb_counter]*
                                vectors[neighb_counter].getX()/vectors[neighb_counter].getLength();

            coord_Y = coord_Y + factors[neighb_counter]*gradients[neighb_counter]*
                                vectors[neighb_counter].getY()/vectors[neighb_counter].getLength();

            coord_Z = coord_Z + factors[neighb_counter]*gradients[neighb_counter]*
                                vectors[neighb_counter].getZ()/vectors[neighb_counter].getLength();
        }

        result_vector.setX(coord_X);
        result_vector.setY(coord_Y);
        result_vector.setZ(coord_Z);

        return result_vector;
    }

    /** The method returns two factors of parameter D: first one equals X,
     * second one equals Z (Z is whole number),
     * such that D = X*Y, Y equals 10 in the power of Z.
     * @return two factors of parameter D: first one equals X,
     * second one equals Z (Z is whole number),
     * such that D = X*Y, Y equals 10 in the power of Z
     * @param param required parameter
     */
    public double[] getFactors(double param)
    {
        // Whole exponent of power of 10
        double exponent = Math.floor(Math.log10(Math.abs(param)));

        // Mantissa of cell size
        double mantissa = param*Math.pow(10, -exponent);

        // Array of two returned numbers
        double[] cell_size_factors = new double[2];

        cell_size_factors[0] = mantissa;
        cell_size_factors[1] = exponent;
        
        return cell_size_factors;
    }
    
    /** The method creates sample of the class.
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args)
    {
        System.out.println("START!!!");
        Date start_date = new Date();

    //    int time_step = (new Integer(args[2])).intValue();

        // Start of task
     //   PlaneCreation plane = new PlaneCreation(args[1], time_step, args[3]);
        PlaneCreation plane = new PlaneCreation(new String(), 0, new String());

        System.out.println("FINISH!!!");
        Date finish_date = new Date();

        long time_period = finish_date.getTime() - start_date.getTime();
        
        long time_period_sec = time_period/1000;        
        long time_period_min = time_period/60000;
        long time_period_hrs = time_period/3600000;
        
        long minutes = time_period_min - time_period_hrs*60;
        long seconds = time_period_sec - time_period_min*60;
        long millisecs = time_period - time_period_sec*1000;
        
      //  System.out.println("The period of execution of the program: " + time_period + " ms");
        System.out.println("The period of execution of the program: " + time_period_hrs + " hr "+minutes+" min "+seconds+"."+millisecs+" sec\n");
    }    
    
    /**  The method creates file with temperature values calculated 
     * according to analytic solution of heat transfer problem for unrestricted layer.
     * @param time_period current time period
     * @param max_time_period maximal time period (in milliseconds)
     */
    private void createGraphAnalyticTemperatureValues(double time_period, double max_time_period)
    {   
        // Rounded current time period
        int integer_time_period = (int)Math.round(time_period);
        
        // Rounded maximal time period
        int max_integer_time_period = (int)Math.round(max_time_period);
            
        // Exponent of power of 10 in standard representation
        // of maximal time period
        int max_exponent = (int)Math.floor(Math.log10(max_integer_time_period));

        // Exponent of power of 10 in standard representation
        // of number of current time period
        int exponent = 0;

        if(integer_time_period+1 > 0)
            exponent     = (int)Math.floor(Math.log10(integer_time_period+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
            
        // File name
        String graph_temperature_analytic_values = "./user/task_db/"+task_name+"/analytic_temperature_values/analytic_temperature_values_"+zeros+integer_time_period+".txt";
                
        // Number of points where temperature values are calculated
        int point_number = 75;
        
        double half_size = 0.003061875;
        double cell_size_value = 8.165E-5;//half_size*2/point_number;
        double init_temperature = 293;
        double bound_temperature = 423;        
        double fourier = 1.0E-3*time_period*1.475E-7/(half_size*half_size);
        
        // Coordinates of points located along axis OX
        double[] coordinates = new double[point_number];
        for(int coord_counter = 0; coord_counter<point_number; coord_counter++)
            coordinates[coord_counter] = -half_size + cell_size_value*(coord_counter + 0.5);
        
        // Temperatures in each point
        double[] temperatures = new double[point_number];
        
        // The sum of particular solutions
        double solution_sum = 0;
        
        // Number of items of the sum of particular solutions
        int item_number = 1000;
        
        // Item of series
        double item = 0;
        
        // Minimal value of item
        double epsilon = 1.0E-30;
        
        // Number of items necessary for reaching of minimal value of item
        int current_item_number = 0;
        
        // The parameters for calculation of the terms
        double[] mu = new double[item_number];
        double[] a  = new double[item_number];
        
        for(int item_counter = 0; item_counter<item_number; item_counter++)
        {
            mu[item_counter] = (2*item_counter+1)*Math.PI/2;
            a[item_counter]  = 2.0/mu[item_counter];
            
            if(item_counter%2 == 1)
                a[item_counter] = -a[item_counter];
        }
        
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(graph_temperature_analytic_values));
            
            for(int coord_counter = 0; coord_counter<point_number; coord_counter++)
            {
                solution_sum = 0;
                
                for(int item_counter = 0; item_counter<item_number; item_counter++)
                {
                    item = a[item_counter]*Math.cos(mu[item_counter]*coordinates[coord_counter]/half_size)*Math.exp(-mu[item_counter]*mu[item_counter]*fourier);
                    
                    if((Math.abs(item) < epsilon)&(item>0))
                        item_counter = item_number;                    
                    else
                    {
                        solution_sum += item;
                        current_item_number = item_counter+1;
                    }
                }
                
                temperatures[coord_counter] = bound_temperature + (init_temperature - bound_temperature)*solution_sum;
                
                bw.write((coordinates[coord_counter]+half_size+0.000009175)+" "+temperatures[coord_counter]);
              //  bw.write(" "+current_item_number);
              //  bw.write(" mu1 = "+mu[0]+" mu2 =  "+mu[1]+" a1 = "+a[0]+" a2 = "+a[1]);
                bw.newLine();
                bw.flush();
            }   
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Exception: "+io_exc);
        }
    }
    
    /**  The method creates file with temperature values calculated 
     * according to analytic solution of heat transfer problem for parallelepiped.
     * @param time_period current time period
     * @param max_time_period maximal time period (in milliseconds)
     */
    private void createGraphAnalyticTemperatureValues3D(double time_period, double max_time_period)
    {   
        // Rounded current time period
        int integer_time_period = (int)Math.round(time_period);
        
        // Rounded maximal time period
        int max_integer_time_period = (int)Math.round(max_time_period);
            
        // Exponent of power of 10 in standard representation
        // of maximal time period
        int max_exponent = (int)Math.floor(Math.log10(max_integer_time_period));

        // Exponent of power of 10 in standard representation
        // of number of current time period
        int exponent = 0;

        if(integer_time_period+1 > 0)
            exponent     = (int)Math.floor(Math.log10(integer_time_period+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
            
        // File name
        String graph_temperature_analytic_values = "./user/task_db/"+task_name+"/analytic_temperature_values/analytic_temperature_values_"+zeros+integer_time_period+".txt";
                
        // Number of points where temperature values are calculated
        int point_number = 51;
        
        double cell_size_value = 8.165E-7;//1E-6;//half_size*2/point_number;
        double init_temperature = 300;
        double bound_temperature = 1000;          
        
        // Temperatures in each point
        double[] temperatures = new double[point_number];
        
        // The sum of particular solutions
        double solution_sum = 0;
        
        // Number of items of the sum of particular solutions
        int item_number = 500000;
        
        // Item of series
        double item = 0;
        
        // Minimal value of item
        double epsilon = 1.0E-200;
        
        // Number of items necessary for reaching of minimal value of item
        int current_item_number = 0;
        
        // The parameters for calculation of the terms
        double[] mu = new double[item_number];
        double[] a  = new double[item_number];
        
        // Specimen half sizes (in meters)
        double size_X = 3.0E-5;
        double size_Y = 2.5E-5;
        double size_Z = 2.04125E-5;//2.0E-5; //        
        
        // Generalixed specimen size (in meters)
        double gen_size = Math.sqrt(1/(1/(size_X*size_X)+1/(size_Y*size_Y)+1/(size_Z*size_Z)));// 1.38527129E-5;
        
        // Fourier factor
        double fourier = 1.0E-9*time_period*401.0/(390.0*8940.0*gen_size*gen_size);        
        
        // Coordinates X of points located along axis OX
        double[] coordinates = new double[point_number];
        
        for(int coord_counter = 0; coord_counter<point_number; coord_counter++)
            coordinates[coord_counter] = -size_Z + cell_size_value*coord_counter;//(coord_counter + 0.5);//
        
        for(int item_counter = 0; item_counter<item_number; item_counter++)
        {
            mu[item_counter] = (2*item_counter+1)*Math.PI/2;
            a[item_counter]  = 2.0/mu[item_counter];
            
            if(item_counter%2 == 1)
                a[item_counter] = -a[item_counter];
        }
        
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(graph_temperature_analytic_values));
            
            for(int coord_counter = 0; coord_counter<point_number; coord_counter++)
            {
                solution_sum = 0;
                
                for(int item_counter1 = 0; item_counter1<item_number; item_counter1++)
                for(int item_counter2 = 0; item_counter2<item_number; item_counter2++)
                for(int item_counter3 = 0; item_counter3<item_number; item_counter3++)
                {
                    item = a[item_counter1]*a[item_counter2]*a[item_counter3]*
                           Math.cos(mu[item_counter3]*coordinates[coord_counter]/size_Z)*
                           Math.exp(-gen_size*gen_size*(mu[item_counter1]*mu[item_counter1]/(size_X*size_X)+
                                                        mu[item_counter2]*mu[item_counter2]/(size_Y*size_Y)+
                                                        mu[item_counter3]*mu[item_counter3]/(size_Z*size_Z))*fourier);
                    
                    if((Math.abs(item) < epsilon)&(item>0))
                    {
                        item_counter1 = item_number;
                        item_counter2 = item_number;
                        item_counter3 = item_number;
                    }
                    else
                    {
                        solution_sum += item;
                        current_item_number++;
                    }
                }
                
                temperatures[coord_counter] = bound_temperature + (init_temperature - bound_temperature)*solution_sum;
                
        //        bw.write((coordinates[coord_counter]+size_Z+9.175E-8)+" "+temperatures[coord_counter]);
                bw.write((coordinates[coord_counter]+size_Z+cell_size/2)+" "+temperatures[coord_counter]);
              //  bw.write(" "+current_item_number);
              //  bw.write(" mu1 = "+mu[0]+" mu2 =  "+mu[1]+" a1 = "+a[0]+" a2 = "+a[1]);
                bw.newLine();
                bw.flush();
            }   
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Exception: "+io_exc);
        }
    }
        
    /**  The method creates file with temperature values calculated 
     * according to analytic solution of heat transfer problem for parallelepiped
     * and distributed in plane.
     * @param time_period current time period
     * @param max_time_period maximal time period (in milliseconds)
     */
    private void createPlaneAnalyticTemperatureValues3D(double time_period, double max_time_period)
    {   
        // Rounded current time period
        int integer_time_period = (int)Math.round(time_period);
        
        // Rounded maximal time period
        int max_integer_time_period = (int)Math.round(max_time_period);
            
        // Exponent of power of 10 in standard representation
        // of maximal time period
        int max_exponent = (int)Math.floor(Math.log10(max_integer_time_period));

        // Exponent of power of 10 in standard representation
        // of number of current time period
        int exponent = 0;

        if(integer_time_period+1 > 0)
            exponent     = (int)Math.floor(Math.log10(integer_time_period+1));
        
        // String of zeros
        String zeros = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";        
            
        // File name
        String graph_temperature_analytic_values = "./user/task_db/"+task_name+"/"+task_name+"_x=35_analytic_values/"+task_name+"_"+zeros+integer_time_period+".txt";
                
        // Number of points where temperature values are calculated
        int point_number_Y = 52;
        int point_number_Z = 51;
        
        double cell_size_Z = 8.165E-7;//1E-6;//half_size*2/point_number;
        double cell_size_Y = 1E-6;
        
        double init_temperature = 300;
        double bound_temperature = 1000;          
        
        // Temperatures in each point
        double[][] temperatures = new double[point_number_Z][point_number_Y];
        
        // The sum of particular solutions
        double solution_sum = 0;
        
        // Number of items of the sum of particular solutions
        int item_number = 1000000;
        
        // Item of series
        double item = 0;
        
        // Minimal value of item
        double epsilon = 1.0E-30;
        
        // Number of items necessary for reaching of minimal value of item
        int current_item_number = 0;
        
        // The parameters for calculation of the terms
        double[] mu = new double[item_number];
        double[] a  = new double[item_number];
        
        // Specimen half sizes (in meters)
        double size_X = 3.1E-5;
        double size_Y = 2.6E-5;
        double size_Z = 2.04125E-5;//2.0E-5; //        
        
        // Generalixed specimen size (in meters)
        double gen_size = Math.sqrt(1/(1/(size_X*size_X)+1/(size_Y*size_Y)+1/(size_Z*size_Z)));// 1.38527129E-5;
        
        // Fourier factor
        double fourier = 1.0E-9*time_period*401.0/(390.0*8940.0*gen_size*gen_size);        
        
        // Coordinates Z and Y of points located along axis OX
        VectorR2[][] coordinates = new VectorR2[point_number_Z][point_number_Y];
        
        for(int coord_counter_Z = 0; coord_counter_Z<point_number_Z; coord_counter_Z++)
        for(int coord_counter_Y = 0; coord_counter_Y<point_number_Y; coord_counter_Y++)
        {
            if(coord_counter_Z%3 == 1)
                coordinates[coord_counter_Z][coord_counter_Y] = new VectorR2(-size_Z + cell_size_Z*coord_counter_Z, -size_Y + cell_size_Y*coord_counter_Y);
            else
                coordinates[coord_counter_Z][coord_counter_Y] = new VectorR2(-size_Z + cell_size_Z*coord_counter_Z, -size_Y + cell_size_Y*(coord_counter_Y+0.5));
        }
        
        for(int item_counter = 0; item_counter<item_number; item_counter++)
        {
            mu[item_counter] = (2*item_counter+1)*Math.PI/2;
            a[item_counter]  = 2.0/mu[item_counter];
            
            if(item_counter%2 == 1)
                a[item_counter] = -a[item_counter];
        }
        
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(graph_temperature_analytic_values));
            
            for(int coord_counter_Z = 1; coord_counter_Z<point_number_Z-1; coord_counter_Z++)
            for(int coord_counter_Y = 1; coord_counter_Y<point_number_Y-1; coord_counter_Y++)      
            {
                solution_sum = 0;
                
                for(int item_counter1 = 0; item_counter1<item_number; item_counter1++)
                for(int item_counter2 = 0; item_counter2<item_number; item_counter2++)
                for(int item_counter3 = 0; item_counter3<item_number; item_counter3++)
                {
                    item = a[item_counter1]*a[item_counter2]*a[item_counter3]*
                           Math.cos(mu[item_counter3]*coordinates[coord_counter_Z][coord_counter_Y].getX()/size_Z)*
                           Math.cos(mu[item_counter2]*coordinates[coord_counter_Z][coord_counter_Y].getY()/size_Y)*
                           Math.exp(-gen_size*gen_size*(mu[item_counter1]*mu[item_counter1]/(size_X*size_X)+
                                                        mu[item_counter2]*mu[item_counter2]/(size_Y*size_Y)+
                                                        mu[item_counter3]*mu[item_counter3]/(size_Z*size_Z))*fourier);
                    
                    if((Math.abs(item) < epsilon)&(item>0))
                    {
                        item_counter1 = item_number;
                        item_counter2 = item_number;
                        item_counter3 = item_number;
                    }
                    else
                    {
                        solution_sum += item;
                        current_item_number++;
                    }
                }
                
                temperatures[coord_counter_Z][coord_counter_Y] = bound_temperature + (init_temperature - bound_temperature)*solution_sum;
                
        //        bw.write((coordinates[coord_counter]+size_Z+9.175E-8)+" "+temperatures[coord_counter]);
                bw.write((coordinates[coord_counter_Z][coord_counter_Y].getX()+size_Z+0.5*cell_size_Y)+" "+
                         (coordinates[coord_counter_Z][coord_counter_Y].getY()+size_Y+0.5*cell_size_Y)+" "+temperatures[coord_counter_Z][coord_counter_Y]);
              //  bw.write(" "+current_item_number);
              //  bw.write(" mu1 = "+mu[0]+" mu2 =  "+mu[1]+" a1 = "+a[0]+" a2 = "+a[1]);
                bw.newLine();
                bw.flush();
            }   
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Exception: "+io_exc);
        }
    }
    
    /** The method allows comparing data from two files 
     * with numerical and analytic solutions at certain time step.
     * @param time_period current time period
     * @param max_time_period maximal time period (in milliseconds)
     */
    private void compareData(double time_period, double max_time_period)
    {
        // Rounded current time period
        int integer_time_period = (int)Math.round(time_period);
        
        // Rounded maximal time period
        int max_integer_time_period = (int)Math.round(max_time_period);
            
        // Exponent of power of 10 in standard representation
        // of maximal time period
        int max_exponent = (int)Math.floor(Math.log10(max_integer_time_period));

        // Exponent of power of 10 in standard representation
        // of number of current time period
        int exponent = 0;

        if(integer_time_period+1 > 0)
            exponent     = (int)Math.floor(Math.log10(integer_time_period+1));
        
        // String of zeros
        String zeros = "";
        String data_string;
        String coord_string;
        String old_coord_string = "";
        
        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";
         
        // File with the values of temperatures for analytic solution
        String graph_temperature_analytic_values = "./user/task_db/"+task_name+"/analytic_temperature_values/analytic_temperature_values_"+zeros+integer_time_period+".txt";
        
        // File with the values of temperatures for numerical solution
        String graph_temperature_numerical_values = "./user/task_db/"+task_name+"/numerical_temperature_values/"+task_name+"_"+zeros+integer_time_period+"_ns_temperatures_OZ.txt";
                
        // File with the values of temperatures for both analytic and numerical solutions
        String analytic_and_numerical_values = "./user/task_db/"+task_name+"/analytic_and_numerical_values/analytic_and_numerical_values_"+zeros+integer_time_period+".txt";
        
        // Number of strings in each file        
        int string_number = 0;
        
        double init_temperature = 0;
        double bound_temperature = 0;
        
        if(task_name == "Rubber_2_2_6_top_bottom_heat_423")
        {
            string_number     =   75;
            init_temperature  =  293;
            bound_temperature =  423;
        }
        
        if(task_name == "copper_all_facets_tempr_1000")
        {
            string_number     =   51;
            init_temperature  =  300;
            bound_temperature = 1000;
        }
        
        double[] analytic_values  = new double[string_number];
        double[] numerical_values = new double[string_number];
        
        StringTokenizer st;
        
        
        double rel_analytic_value  = 0;
        double rel_numerical_value = 0;
        double relative_error = 0;
        double min_relative_error = 1;
        double max_relative_error = 0;
        double average_relative_error = 0;
        
        try
        {
            BufferedReader br1 = new BufferedReader(new FileReader(graph_temperature_analytic_values));
            BufferedReader br2 = new BufferedReader(new FileReader(graph_temperature_numerical_values));
            BufferedWriter bw = new BufferedWriter(new FileWriter(analytic_and_numerical_values));
            
            // Reading of the values of temperatures for analytic solution
            for(int string_counter = 0; string_counter < string_number; string_counter++)
            {
                data_string = br1.readLine();
                st = new StringTokenizer(data_string);
                
                if(st.hasMoreTokens())
                {
                    coord_string = st.nextToken();
                    analytic_values[string_counter] = (new Double(st.nextToken())).doubleValue();
                }
            }
            
            int string_counter = -1;
            
            // Reading of the values of temperatures for numerical solution
            while(br2.ready())
            {
                data_string = br2.readLine();
                st = new StringTokenizer(data_string);
                
                if(st.hasMoreTokens())
                {                    
                    coord_string = st.nextToken();
                    
                    if(!coord_string.equals(old_coord_string))
                    {
                        string_counter++;
                        numerical_values[string_counter] = (new Double(st.nextToken())).doubleValue();
                        
                        rel_analytic_value  = (analytic_values[string_counter]  - init_temperature)/(bound_temperature - init_temperature);
                        rel_numerical_value = (numerical_values[string_counter] - init_temperature)/(bound_temperature - init_temperature);
                        
                        if((rel_analytic_value == 0)&(rel_numerical_value == 0))
                            relative_error = 0;
                        else
                            relative_error      = Math.abs(rel_numerical_value/rel_analytic_value - 1);
                        
                        bw.write(coord_string+" "+analytic_values[string_counter]+" "+numerical_values[string_counter]+" "+relative_error);
                        
                        if(relative_error<min_relative_error)
                            min_relative_error = relative_error;
                        
                        if(relative_error>max_relative_error)
                            max_relative_error = relative_error;
                        
                        average_relative_error += relative_error;
                        
                        bw.newLine();
                        bw.flush();
                    }
                    
                    old_coord_string = new String(coord_string);
                }
            }
            
            average_relative_error = average_relative_error/(string_counter+1);
            
            bw.write("\naverage_relative_error = "+average_relative_error+"\n");
            bw.write("min_relative_error = "+min_relative_error+"\n");
            bw.write("max_relative_error = "+max_relative_error+"\n");
            
            br1.close();
            br2.close();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Exception: "+io_exc);
        }        
    }
    
    /** The method calculates volume of cell3D 
     * (in case of hexagonal close packing or in case of simple cubic packing).
     * @param packing_type type of packing
     * @param cell_size cell size
     */
    private double calcCellVolume(byte packing_type, double cell_size)
    {
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            cell_volume = 0.6938*cell_size*cell_size*cell_size;

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            cell_volume = cell_size*cell_size*cell_size;
        
        return cell_volume;
    }
}
