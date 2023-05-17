package util;

/*
 * @(#) Common.java version 1.0.0;       April 21 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for keeping in memore all constants
 *
 *=============================================================
 *  Last changes :
 *          4 April 2011 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.2.1
 *          Edit: Add new constant
 */

/** Class for keeping in memory all constants
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - April 2009
 */
public class Common 
{
    /** Avogadro number is the number of atoms in 1 mol of matter */
    public static final double AVOGADRO_NUMBER = 6.022141E23;
    
    /** Paths and extensions of special files
     */
    public static final String TASK_PATH = "./user/task_db";
    public static final String SPEC_PATH = "./user/spec_db";
    public static final String MATERIALS_PATH = "./user/mat_db";
    public static final String INIT_COND_PATH = "./user/spec_db";
    public static final String INIT_COND_NAME = "/init_cond_db";
    public static final String BOUND_COND_PATH = "./user/spec_db";
    public static final String BOUND_COND_NAME = "/bound_cond_db";
    public static final String SPEC_PATH_FOR_WRITING = "./user/spec_db/";
    
    public static final String DEFAULT_INIT_COND_FILE_NAME = "default_init_cond";
    public static final String DEFAULT_BOUND_COND_FILE_NAME = "default_bound_cond";
    public static final String DEFAULT_TASK_FILE_NAME = "default_task";
    public static final String CHOOSED_TASK_FILE_NAME = "task";
    
    public static final String SPECIMEN_EXTENSION = "txt";
    public static final String SPECIMEN_INTERFACE_EXTENSION = "spc";
    public static final String SPECIMEN_PICTURE_EXTENSION = "gif";
    public static final String SPECIMEN_MATERIAL_EXTENSION = "spm";
    public static final String MATERIAL_EXTENSION = "mtrl";
    public static final String INIT_COND_EXTENSION = "txt";
    public static final String BOUND_COND_EXTENSION = "txt";
    public static final String TANK_EXTENSION = "txt";
    public static final String TASK_EXTENSION = "seca";
    public static final String TASK_COMMENTS_EXTENSION = "cmnt";
    public static final String CONTROLL_GRAIN_STRUCTURE_CREATOR_FILE_EXTENSION = "inf";
    public static final String INIT_COND_FILE_GEOMETRY_EXTENSION = "str";
    public static final String INIT_COND_INPUT_FILE_EXTENSION = "txt";
    public static final String SPECIMEN_FILE_EXTENSION = "spc";
    public static final String INIT_COND_FILE_EXTENSION = "incnd";
    public static final String BOUND_COND_FILE_EXTENSION = "bon";
    public static final String BOUND_COND_FILE_GEOMETRY_EXTENSION = "txt";
    public static final String GRAINS_FILE_EXTENSION = "grn";

    /** Field of task file parameters */
    public static final String LAYERS_FILE_NAME = "layers_file_name = ";
    public static final String TANKS_FILE_NAME = "tanks_file = ";
    public static final String INIT_PARAMS_FILE = "init_params_file = ";
    public static final String BOUND_PARAMS_FILE = "bound_params_file = ";
    public static final String BOUND_GRAINS_FILE_NAME = "bound_grains_file_name = ";
    public static final String PHASES_FILE_NAME = "phases_file_name = ";
    
    public static final String INIT_COND_FILE_GEOMETRY = "init_cond_file_geometry = ";
    public static final String INIT_COND_INPUT_FILE = "init_cond_input_file = ";
    public static final String SPECIMEN_FILE = "specimen_file = ";
    public static final String INIT_COND_FILE = "init_cond_file = ";
    public static final String BOUND_COND_FILE = "bound_cond_file = ";
    public static final String BOUND_COND_FILE_GEOMETRY = "bound_cond_file_geometry = ";
    public static final String GRAINS_FILE = "grains_file = ";
    public static final String GRAINS_FILE_2 = "grains_file_2 = ";

    public static final String FIRST_COMMENT_IN_TASK_PARAMETERS = "# Files used by project 'FileCreation' for creation of files with initial and boundary conditions";
    public static final String SECOND_COMMENT_IN_TASK_PARAMETERS = "# Files with information about parameters of specimen, initial and boundary conditions";

    /** Data associated with table of materials */
    public static final int COLUMN_NUMBER = 5;

    /** Extensions of special files */
    public static String TASK_NAME_FILE = "task";
    public static final String RESULTS_FILE_EXTENSION = "res";
    public static final String TEMPERATURES_FILE_EXTENSION = "tmprs";
    public static final String GRAIN_INDICES_FILE_EXTENSION = "grns";
    public static final String STRESSES_FILE_EXTENSION = "strs";
    public static final String STATES_FILE_EXTENSION = "states";
    public static final String EXTREME_VALUES_FILE_EXTENSION = "extr";

    /** Types of mechanical and thermal loading of boundary facets */
    public static final String ADIABATIC = "N/A";
    public static final String CONSTANT_STRAIN = "strain";
    public static final String CONSTANT_STRESS = "stress";
    public static final String CONSTANT_TEMPERATURE = "temperature";
    public static final String CONSTANT_THERMAL_ENERGY = "energy";
    
   /** Types of inner cells according to types of mechanical and thermal interaction
    * of grains and possibility of grain recrystallization */
    public static final byte LAYER_CELL                 =  0;
    public static final byte INNER_CELL                 =  1;

   /** Types of outer boundary cells according to types of mechanical and thermal interaction
    * of grains and possibility of grain recrystallization */
    public static final byte ADIABATIC_TEMPERATURE_CELL =  3;
    public static final byte ADIABATIC_THERMAL_CELL     =  4;
    public static final byte STRAIN_ADIABATIC_CELL      =  5;
    public static final byte STRAIN_TEMPERATURE_CELL    =  6;
    public static final byte STRAIN_THERMAL_CELL        =  7;
    public static final byte STRESS_ADIABATIC_CELL      =  8;
    public static final byte STRESS_TEMPERATURE_CELL    =  9;
    public static final byte STRESS_THERMAL_CELL        = 10;
    public static final byte ADIABATIC_ADIABATIC_CELL   = 11;
    
    /** Types of cell according to its location in grain and in specimen */
    /** Cell located in outer boundary of specimen */
    public static final byte OUTER_CELL    = 2;
    
    /** Cell located in the interior of grain */
    public static final byte INTRAGRANULAR_CELL = 3;

    /** Cell located at grain boundary in the interior of specimen */
    public static final byte INTERGRANULAR_CELL      = 4;

    /** Cell located at specimen boundary */
    public static final byte INNER_BOUNDARY_CELL          = 5;

    /** Cell located at specimen boundary and at grain boundary */
    public static final byte INNER_BOUNDARY_INTERGRANULAR_CELL = 6;
    
    /** Cell located in grain boundary region in the interior of specimen */
    public static final byte GRAIN_BOUNDARY_REGION_CELL             = 7;
    
    /** Cell located in grain boundary region at specimen boundary */
    public static final byte INNER_BOUND_GRAIN_BOUND_REGION_CELL         = 8;
    
    /** Types of grain according to its role in recrystallization process:
     * 0 - initial grain; 1 - new grain; 2 - twinning grain.
     */
    public static final byte INITIAL_GRAIN  = 0;
    public static final byte NEW_GRAIN      = 1;
    public static final byte TWINNING_GRAIN = 2;

    /** States of cell */
    public static final byte ELASTIC_CELL     = 0;
    public static final byte PLASTIC_CELL     = 1;
    public static final byte DAMAGED_CELL     = 2;// 1;// 

    /** String for the case when parameter is not attended */
    public static final String NOT_AVAILABLE  = "N/A";

    /** Variables for creation of sections of specimen */
    public static final byte CREATE_SECTION_X = 0;
    public static final byte CREATE_SECTION_Y = 1;
    public static final byte CREATE_SECTION_Z = 2;

    /** Parameter responsible for executing or ignoring of method */
    public static final byte IGNORE_METHOD    = 0;
    public static final byte EXECUTE_METHOD   = 1;

    /** Variables responsible for calculation of principal stresses
     * and force moments at each time step
     */
    public static final boolean DO_CALCULATE_PRINCIPAL_STRESSES = false;
    public static final boolean DO_CALCULATE_FORCE_MOMENTS = false;

    /** FILE COLUMN ARRANGMENT */
    public static final int STR_GRAIN_COLUMN_NUMBER = 0;
    public static final int RES_GRAIN_COLUMN_NUMBER = 2;
    public static final int STR_AUTOMATA_LOCATION_COLUMN_NUMBER = 4;
    public static final int RES_AUTOMATA_LOCATION_COLUMN_NUMBER = 1;
    public static final int STR_FIRST_COORDINATE_VALUE_COLUMN_NUMBER = 1;
    public static final int RES_FIRST_COORDINATE_VALUE_COLUMN_NUMBER = 3;

    /** Types of specimen */
    public static final byte MULTIGRANULAR_SPECIMEN = 1;
    public static final byte TRIPLE_LAYER_SPECIMEN  = 2;

    /** Types of intermediate layer in case of triple-layer specimen*/
    public static final byte SIMPLE_LAYER  = 0;
    public static final byte TRIANGLE_GOFR = 1;
    public static final byte SQUARE_GOFR   = 2;
    public static final byte PYRAMIDES     = 3;
    public static final byte CHESS_CUBES   = 4;

    /** Types of package of automata */
    public static final byte HEXAGONAL_CLOSE_PACKING = 0;
    public static final byte SIMPLE_CUBIC_PACKING    = 1;

    /** Types of interaction of boundary cells with neighbours */
    public static final byte FROM_BOUNDARY_TO_NONE_NEIGHBOURS  = 0;
    public static final byte FROM_BOUNDARY_TO_INNER_NEIGHBOURS = 1;
    public static final byte FROM_BOUNDARY_TO_ALL_NEIGHBOURS   = 2;
    
    /** Types of dependence of the mechanical parameter of 
     * the boundary cell on the cell coordinates 
     */
    /** Unit function */
    public static final byte UNIT_FUNCTION = 0;
    
    /** Distance from the boundary cell centre to the specimen centre */
    public static final byte DISTANCE_FROM_CENTRE = 1;
    
    /**  The modified coordinate X of the boundary cell centre
     * in coordinate system with the origin in the specimen centre 
     */
    public static final byte COORDINATE_X_VALUE   = 2;
    
    /**  The modified coordinate Y of the boundary cell centre
     * in coordinate system with the origin in the specimen centre 
     */
    public static final byte COORDINATE_Y_VALUE   = 3;
    
    /**  The modified coordinate Z of the boundary cell centre
     * in coordinate system with the origin in the specimen centre
     */
    public static final byte COORDINATE_Z_VALUE   = 4;
    
    /** Distance from the boundary cell centre to the axis X
     * of the coordinate system with the origin in the specimen centre 
     */
    public static final byte DISTANCE_FROM_AXIS_X = 5;
    
    /** Distance from the boundary cell centre to the axis Y
     * of the coordinate system with the origin in the specimen centre 
     */
    public static final byte DISTANCE_FROM_AXIS_Y = 6;
    
    /** Distance from the boundary cell centre to the axis Z
     * of the coordinate system with the origin in the specimen centre 
     */
    public static final byte DISTANCE_FROM_AXIS_Z = 7;
    
    /** Sum of the absolute values of all coordinates */
    public static final byte SUM_OF_ALL_ABS_COORDINATES = 8;
    
    /** Sum of the absolute values of coordinates X and Y */
    public static final byte SUM_OF_ABS_COORD_X_AND_ABS_COORD_Y = 9;
    
    /** Sum of the absolute values of coordinates X and Z */
    public static final byte SUM_OF_ABS_COORD_X_AND_ABS_COORD_Z = 10;
    
    /** Sum of the absolute values of coordinates Y and Z */
    public static final byte SUM_OF_ABS_COORD_Y_AND_ABS_COORD_Z = 11;
    
    /** The absolute value of coordinate X */
    public static final byte ABS_COORD_X = 12;
    
    /** The absolute value of coordinate Y */
    public static final byte ABS_COORD_Y = 13;
    
    /** The absolute value of coordinate Z */
    public static final byte ABS_COORD_Z = 14;
    
    /** One minus 2/3 of the sum of absolute values of all coordinates */
    public static final byte ONE_MINUS_SUM_OF_ALL_ABS_COORDINATES = 15;
    
    /** One minus the sum of absolute values of coordinates X and Y */
    public static final byte ONE_MINUS_SUM_OF_ABS_COORD_X_AND_ABS_COORD_Y = 16;
    
    /** One minus the sum of absolute values of coordinates X and Z */
    public static final byte ONE_MINUS_SUM_OF_ABS_COORD_X_AND_ABS_COORD_Z = 17;
    
    /** One minus the sum of absolute values of coordinates Y and Z */
    public static final byte ONE_MINUS_SUM_OF_ABS_COORD_Y_AND_ABS_COORD_Z = 18;
    
    /** One minus 2 multiplied by the absolute value of coordinate X */
    public static final byte ONE_MINUS_ABS_COORD_X = 19;
    
    /** One minus 2 multiplied by the absolute value of coordinate Y */
    public static final byte ONE_MINUS_ABS_COORD_Y = 20;
    
    /** One minus 2 multiplied by the absolute value of coordinate Z */
    public static final byte ONE_MINUS_ABS_COORD_Z = 21;
    
    /** One minus (2/sqrt(3))*(distance from the boundary cell centre to the specimen centre) */
    public static final byte ONE_MINUS_DISTANCE_FROM_CENTRE = 22; 
    
    /** One minus sqrt(2)*(distance from the boundary cell centre to the axis X) */
    public static final byte ONE_MINUS_DISTANCE_FROM_AXIS_X = 23;
    
    /** One minus sqrt(2)*(distance from the boundary cell centre to the axis Y) */
    public static final byte ONE_MINUS_DISTANCE_FROM_AXIS_Y = 24;
    
    /** One minus sqrt(2)*(distance from the boundary cell centre to the axis Z) */
    public static final byte ONE_MINUS_DISTANCE_FROM_AXIS_Z = 25;
    
    /** Distance from the boundary cell centre to the origin of coordinate system */
    public static final byte DISTANCE_FROM_ORIGIN = 26;
    
    /** Name of current task */ 
    private static String task_name;
    
    // Variables determining time function of boundary parameter
    /** Constant loading during all numerical experiment */
    public static final String CONSTANT_LOADING  = "CONSTANT_LOADING";
    
    /** Constant loading during chosen time periods */
    public static final String CYCLE_LOADING     = "CYCLE_LOADING";
    
    /** Loading according to periodic time function */
    public static final String PERIODIC_LOADING  = "PERIODIC_LOADING";
    
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
    
    /** The method returns name of current task.
     * @return name of current task
     */
    public static String getTaskName()
    {
        return task_name;
    }
    /** The method sets name of current task.
     * @param _task_name name of current task
     */
    public static void setTaskName(String _task_name)
    {
        task_name = _task_name;
    }
}