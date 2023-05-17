package paral_calc;

/*
 * @(#) UICommon.java version 1.0.0;       September 14 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for keeping all inscription in memory
 *
 *=============================================================
 *  Last changes :
 *          25 August 2010 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.2.1
 *          Edit: Add name and properties for response rate
 */

/** Class for keeping all inscription in memory
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - September 2009
 */

/**
 * Keep in memory all constants not conected with files
 */

// import java.awt.Color; 
// import javax.swing.JLabel;
// import javax.swing.JRadioButton;

public class UICommon
{
    /*
     *                          SPECIALS ENDS OF FILES
     */

     public static final String END_REC_NAME = "_rec";
     public static final String END_GEOM_NAME = "_geom";
     public static final String END_GRAINS_NAME = "_grains";

    /*
     *                           DEFAULT NAMES
     */

    public static final String DEFAULT_INIT_COND_FILE_NAME = "default_init_cond";
    public static final String DEFAULT_BOUND_COND_FILE_NAME = "default_bound_cond";
    public static final String DEFAULT_TASK_FILE_NAME = "default_task";

    /*
     *                      INTERNAL FRAME NAMES
     */

    public static final String TASK_LIST_NAME = "RecCATaskList";
    public static final String INTERFACE_FRAME_NAME = "RecCA Interface";
    public static final String SHOW_SPECIMEN_SCHEME_NAME = "Specimen scheme";
    public static final String SPECIMEN_STRUCTURE_PICTURE_FRAME_NAME = "Specimen structure";
    public static final String TASK_PARAMS_NAME = "Task parameters";
    public static final String BOUND_COND_LIST_NAME = "RecCABoundaryConditionsList";
    public static final String TANK_COND_LIST_NAME = "RecCATankConditionsList";
    public static final String BOUND_COND_PARAMS_NAME = "Parameters of boundary conditions";
    public static final String INIT_COND_PARAMS_NAME = "Parameters of initial conditions";
    public static final String INIT_COND_LIST_NAME = "RecCAInitialConditionsList";
    public static final String SPECIMEN_LIST_NAME = "RecCASpecimenList";
    public static final String SPECIMEN_PARAMS_NAME = "Specimen parameters";
    public static final String MATERIAL_LIST_NAME = "List of Materials";
    public static final String MATERIAL_PARAMS_NAME = "Material parameters";
    public static final String RESULTS_FRAME_NAME = "Results";
    public static final String MATERIAL_INFO_FRAME_NAME = "Information about material";
    public static final String PROGRESS_BAR_FRAME_NAME = "Progress bar";
    public static final String SPECIMEN_GENERATION_FRAME_NAME = "Specimen generation";
    public static final String RECRYSTALLIZATION_FRAME_NAME = "Recrystallization";
    public static final String RESULTS_LIST_FRAME_NAME = "List of files with results";
    public static final String BOUND_COND_FUNCTION_TYPE_NAME = "Types of boundary function";

    /*
     *                          BUTTON NAMES
     */

    public static final String BUTTON_OK_NAME = "OK";
    public static final String BUTTON_OTHER_PARAMETERS_NAME = "Conditions";
    public static final String BUTTON_CHANGE_NAME = "Change";
    public static final String BUTTON_CANCEL_NAME = "Cancel";
    public static final String BUTTON_TYPE_NAME = "Type";
    public static final String BUTTON_CHOOSE_NAME = "Choose";
    public static final String BUTTON_CREATE_NEW_NAME = "Create new";
    public static final String BUTTON_GENERATE_NAME = "Generate";
    public static final String BUTTON_SAVE_NAME = "Save as";
    public static final String BUTTON_FOR_SAVING_DIALOG_NAME = "Save";
    public static final String BUTTON_RUN_NAME = "Run";
    public static final String BUTTON_RESUME_NAME = "Resume";
    public static final String BUTTON_SHOW_NAME = "Show";
    public static final String BUTTON_BACK_NAME = "Back";
    public static final String BUTTON_DONE_NAME = "Done";
    public static final String BUTTON_CREATE_TABLE_NAME = "Table";
    public static final String BUTTON_NEW_MATERIAL_NAME = "Add";
    public static final String BUTTON_SHOW_SPECIMEN_SCHEME_NAME = "Show scheme";
    public static final String BUTTON_SHOW_SPECIMEN_STRUCTURE_NAME = "Show structure";
    public static final String BUTTON_START_NAME = "Start";
    public static final String BUTTON_CLOSE_NAME = "Close";
    public static final String BUTTON_STOP_NAME = "Stop";
    public static final String BUTTON_CREATE_FILES_NAME = "Create files";
    public static final String BUTTON_DAMAGED_SPECIMEN_NAME = "Damaged";
    public static final String BUTTON_DELETE_NAME = "Delete";
    public static final String BUTTON_REPAIR_NAME = "Repair";
    public static final String BUTTON_RETURN_NAME = "Return";

    /**
     *            ERROR WINDOW NAMES AND SAVING DIALOGS
     */

    public static final String ERROR_DIALOG_NAME = "Error";
    public static final String NOTHING_CHOOSED = "File is not chosen!";
    public static final String NO_MATERIAL_CHOOSED = "Material is not chosen!";
    public static final String BIG_NUMBER_OF_PHASES = "The number of phases you entered is very large!\nThe number of phases in the specimen is smaller!\nYour number of phases was changed!";
    public static final String NO_TASK_CHOOSED = "Task is not chosen!";
    public static final String NOT_ALL_PARAMETERS_INPUT = "Please, input all requested parameters!";
    public static final String NO_INIT_COND_CHOOSED = "Initial conditions are not chosen!";
    public static final String NO_BOUND_COND_CHOOSED = "Boundary conditions are not chosen!";
    public static final String NO_TANKS_CHOSEN = "Tanks are not chosen!";
    
    public static final String NO_SPECIMEN_CHOOSED = "Specimen is not chosen!";
    public static final String NO_PHASES_ENTERED = "Phases are not entered";
    public static final String SAVING_DIALOG_NAME = "Preliminary saving";
    public static final String ERROR_INPUT_ANOTHER_FILE_NAME_NAME = "Please, input another name of file!";
    public static final String SPECIMEN_GENERATION_NAME = "Generated specimen saving";
    public static final String DEFAULT_SAVE_NAME = "Parameters were saved in default directory.";
    public static final String ERROR_FILE_NAME = "File with table of materials is not found!";
    public static final String NO_METHOD_CHOOSED = "Method is not chosen!\nPlease, choose method of calculation!";
    public static final String NO_100_PERCEN_IN_TABLE = "The sum of phase portions is not equal to 1.0!\nPlease, change these parameters!";
    public static final String NO_PATH_CHOOSED = "File was not written!\nThe file name is not entered!";
    public static final String ERROR_TOP_NOT_ADIABATIC_EXCEPTION_NAME = "Top boundary is not adiabatic!\nPlease, choose material for this boundary!";
    public static final String ERROR_BOTTOM_NOT_ADIABATIC_EXCEPTION_NAME = "Bottom boundary is not adiabatic!\nPlease, choose material for this boundary!";
    public static final String ERROR_LEFT_NOT_ADIABATIC_EXCEPTION_NAME = "Left boundary is not adiabatic!\nPlease, choose material for this boundary!";
    public static final String ERROR_RIGHT_NOT_ADIABATIC_EXCEPTION_NAME = "Right boundary is not adiabatic!\nPlease, choose material for this boundary!";
    public static final String ERROR_FRONT_NOT_ADIABATIC_EXCEPTION_NAME = "Front boundary is not adiabatic!\nPlease, choose material for this boundary!";
    public static final String ERROR_BACK_NOT_ADIABATIC_EXCEPTION_NAME = "Back boundary is not adiabatic!\nPlease, choose material for this boundary!";
    public static final String ERROR_SET_MATERIALS_FOR_PHASES_EXCEPTION_NAME = "Please, set material for each phase!";

    public static final String ERROR_AVERAGE_LENGTH_BIGGER_CELL_NAME = "The average grain length must be larger than the cell size.\nPlease, input another value!";
    public static final String ERROR_AVERAGE_LENGTH_IS_NULL_EXCEPTION_NAME = "The average grain length equals 0.\nPlease, input another value!";
    public static final String ERROR_SPECIMEN_SIZE_X_IS_NULL_EXCEPTION_NAME = "The specimen size along axis X equals 0.\nPlease, input another value!";
    public static final String ERROR_SPECIMEN_SIZE_Y_IS_NULL_EXCEPTION_NAME = "The specimen size along axis Y equals 0.\nPlease, input another value!";
    public static final String ERROR_SPECIMEN_SIZE_Z_IS_NULL_EXCEPTION_NAME = "The specimen size along axis Z equals 0.\nPlease, input another value!";
    public static final String ERROR_ANGLE_RANGE_IS_VERY_BIG_EXCEPTION_NAME = "The angle range is very large!\nPlease, input another value!";
    public static final String ERROR_ANGLE_RANGE_IS_VERY_LITTLE_EXCEPTION_NAME = "The angle range is very small!\nPlease, input another value!";
    public static final String ERROR_MAXIMAL_DEVIATION_IS_VERY_BIG_EXCEPTION_NAME = "The maximal deviation is very large!\nPlease, input another value!";
    public static final String ERROR_MAXIMAL_DEVIATION_IS_VERY_LITTLE_EXCEPTION_NAME = "The maximal deviation is very small!\nPlease, input another value!";

    public static final String ERROR_ANOTHER_VALUE_OF_TIME_STEP_EXCEPTION_NAME = "The time step value is larger than maximal value!\nPlease, input another value!";
    public static final String ERROR_TOTAL_TIME_SMALLER_TIME_STEP_EXCEPTION_NAME = "The total time is smaller than the time step!\nPlease, input another values!";
    public static final String ERROR_FILE_NOT_FOUND_NAME = "File is not found!";

    /**
     *                          ERROR CATCHES
     */

    public static final String CATCH_ERROR__NAME = "Error: ";
    public static final String ERR_TABLE_UNCLEAR = "The sizes of table rows are different!";

    /**
     *       SPECIMEN PARAMETER HELP AND PROPERTIE NAMES
     */

    public static final String SPECIMEN_PARAMETERS_NAME = "Specimen parameters";
    public static final String GRAIN_STRUCTURE_NAME     = "Grain structure";
    public static final String ANISOTROPY_VECTOR_NAME   = "Anisotropy vector";

    public static final String PACKING_TYPE_NAME        = "Packing type:";
    public static final String PACKING_TYPE_PROPERTIES  = "packing_type";

    public static final String SPECIMEN_SIZE_X_NAME  = "specimen size X (m)";
    public static final String SPECIMEN_SIZE_Y_NAME  = "specimen size Y (m)";
    public static final String SPECIMEN_SIZE_Z_NAME  = "specimen size Z (m)";
    public static final String CELL_SIZE_NAME        = "cell diameter (m)";
    public static final String SURF_THICKNESS_NAME   = "surf thickness (m)";
    public static final String MIN_NEIGHBOURS_NUMBER_NAME = "min. num. of neighbours";
    public static final String ELEMENT_NUMBER_X_NAME = "FE number along X";
    public static final String ELEMENT_NUMBER_Y_NAME = "FE number along Y";
    public static final String ELEMENT_NUMBER_Z_NAME = "FE number along Z";

    public static final String COORDINATE_X_NAME = "coordinate X";
    public static final String COORDINATE_Y_NAME = "coordinate Y";
    public static final String COORDINATE_Z_NAME = "coordinate Z";
    public static final String ANISOTROPY_COEFF_NAME     = "anisotropy coefficient";
    public static final String AVERAGE_GRAIN_LENGTH_NAME = "average grain length (m)";
    public static final String NUMBER_OF_PHASES_NAME     = "number of phases";

    public static final String PARTICLE_VOLUME_FRACTION_NAME = "particle volume fraction";
    public static final String PARTICLE_RADIUS_NAME          = "particle radius";

    public static final String METHOD_NAME = "method";

    public static final String METHOD_STOCHASTIC_PROPERTIES = "stochastic";
    public static final String METHOD_REGULAR_PROPERTIES    = "regular";
    public static final String METHOD_MIXED_PROPERTIES      = "mixed";

    public static final String METHOD_STOCHASTIC_NAME = "Stochastic";
    public static final String METHOD_REGULAR_NAME    = "Regular";
    public static final String METHOD_MIXED_NAME      = "Mixed";

    public static final String SHOW_GRAIN_STRUCTURE_NAME   = "Show grain structure";

    public static final String SPECIMEN_SIZE_X_PROPERTIES  = "specimen_size_X";
    public static final String SPECIMEN_SIZE_Y_PROPERTIES  = "specimen_size_Y";
    public static final String SPECIMEN_SIZE_Z_PROPERTIES  = "specimen_size_Z";
    public static final String CELL_SIZE_PROPERTIES        = "cell_size";

    public static final String ELEMENT_NUMBER_X_PROPERTIES = "element_number_x";
    public static final String ELEMENT_NUMBER_Y_PROPERTIES = "element_number_y";
    public static final String ELEMENT_NUMBER_Z_PROPERTIES = "element_number_z";

    public static final String SURF_THICKNESS_PROPERTIES   = "surf_thickness";

    public static final String MIN_NEIGHBOURS_NUMBER_PROPERTIES = "min_neighbours_number";

    public static final String COORDINATE_X_PROPERTIES     = "coordinate_x";
    public static final String COORDINATE_Y_PROPERTIES     = "coordinate_y";
    public static final String COORDINATE_Z_PROPERTIES     = "coordinate_z";
    public static final String ANISOTROPY_COEFF_PROPERTIES = "anisotropy_coeff";

    public static final String NUMBER_OF_PHASES_PROPERTIES = "number_of_phases";

    public static final String PARTICLE_VOLUME_FRACTION_PROPERTIES = "particle_volume_fraction";
    public static final String PARTICLE_RADIUS_PROPERTIES          = "particle_radius";
    
    public static final String ANIS_VECTOR_X_NAME   = "anis_vector_x";
    public static final String ANIS_VECTOR_Y_NAME   = "anis_vector_y";
    public static final String ANIS_VECTOR_Z_NAME   = "anis_vector_z";
    public static final String SPEC_ANIS_COEFF_NAME = "spec_anis_coeff";
    
    /**
     *          MATERIAL PARAMETER HELP AND PROPERTIE NAMES
     */

    public static final String MATERIAL_PARAMETERS_NAME = "Materials:";
    
    public static final String MOD_ELAST_PROPERTIES                 = "mod_elast";
    public static final String DENSITY_PROPERTIES                   = "density";
    public static final String HEAT_EXPANSION_COEFF_PROPERTIES      = "heatExpansionCoeff";
    public static final String YIELD_STRAIN_PROPERTIES              = "yieldStrain";
    public static final String ULTIMATE_STRAIN_PROPERTIES           = "ultimateStrain";
    public static final String ENERGY_COEFF_PROPERTIES              = "energy_coeff";
    public static final String SPECIFIC_HEAT_CAPACITY_PROPERTIES    = "specific_heat_capacity";
    public static final String THERMAL_CONDUCTIVITY_PROPERTIES      = "thermal_conductivity";
    public static final String THERMAL_CONDUCT_BOUND_PROPERTIES     = "thermal_conduct_bound";
    public static final String PHONON_PORTION_PROPERTIES            = "phonon_portion";
    public static final String LOW_TEMPER_THR_VALUE_PROPERTIES      = "lowTemperThrValue";
    public static final String HIGH_TEMPER_THR_VALUE_PROPERTIES     = "highTemperThrValue";
    public static final String ACT_ENERGY_PROPERTIES                = "actEnergy";
    public static final String ANGLE_LIMIT_HAGB_PROPERTIES          = "angleLimitHAGB";
    public static final String ENERGY_HAGB_PROPERTIES               = "energyHAGB";
    public static final String MAX_MOBILITY_PROPERTIES              = "maxMobility";
    public static final String MOD_SHEAR_PROPERTIES                 = "mod_shear";
    public static final String LATTICE_PARAMETER_PROPERTIES         = "lattice_parameter";
    public static final String DISL_DISTR_COEFF_PROPERTIES          = "disl_distr_coeff";
    public static final String DISL_MAX_MOBILITY_PROPERTIES         = "dislMaxMobility";
    public static final String MECH_MAX_MOBILITY_PROPERTIES         = "mechMaxMobility";
    public static final String YIELD_STATE_COEFF_PROPERTIES         = "yield_state_coeff";
    public static final String ULTIMATE_STATE_COEFF_PROPERTIES      = "ultimate_state_coeff";
    public static final String PART_VOL_FRACTION_PROPERTIES         = "part_vol_fraction";
    public static final String PART_RADIUS_PROPERTIES               = "part_radius";
    public static final String MOLAR_MASS_PROPERTIES                = "molar_mass";
    public static final String THRESHOLD_STRESS_PROPERTIES          = "threshold_stress";
    public static final String TORSION_ENERGY_COEFF_PROPERTIES      = "torsion_energy_coeff";
    
    /** Coefficient for calculation of local torsion energy at grain boundary */
    public static final String TORSION_ENERGY_COEFF_GB_1_PROPERTIES = "torsion_energy_coeff_gb_1";
    
    /** Coefficient for calculation of local torsion energy in a region near grain boundary */
    public static final String TORSION_ENERGY_COEFF_GB_2_PROPERTIES = "torsion_energy_coeff_gb_2";

    public static final String MOD_ELAST_NAME              = "modulus of elasticity";
    public static final String DENSITY_NAME                = "density";
    public static final String HEAT_EXPANSION_COEFF_NAME   = "heat expansion coeff.";
    public static final String YIELD_STRAIN_NAME           = "yield strain";
    public static final String ULTIMATE_STRAIN_NAME        = "ultimate strain";
    public static final String ENERGY_COEFF_NAME           = "energy coefficient";
    public static final String SPECIFIC_HEAT_CAPACITY_NAME = "specific heat capacity";
    public static final String THERMAL_CONDUCTIVITY_NAME   = "thermal conductivity";
    public static final String THERMAL_CONDUCT_BOUND_NAME  = "bound. thermal conductivity";
    public static final String PHONON_PORTION_NAME         = "phonon portion";
    public static final String LOW_TEMPER_THR_VALUE_NAME   = "lower thr. value of temp-re";
    public static final String HIGH_TEMPER_THR_VALUE_NAME  = "higher thr. value of temp-re";
    public static final String ACT_ENERGY_NAME           = "activation energy";
    public static final String ANGLE_LIMIT_HAGB_NAME     = "angle limit of HAGB";
    public static final String ENERGY_HAGB_NAME          = "energy of HAGB";
    public static final String MAX_MOBILITY_NAME         = "maximal mobility";
    public static final String MOD_SHEAR_NAME            = "modulus of shear";
    public static final String LATTICE_PARAMETER_NAME    = "lattice parameter";
    public static final String DISL_DISTR_COEFF_NAME     = "disl. distrib. coeff.";
    public static final String DISL_MAX_MOBILITY_NAME    = "disl. max. mobility";
    public static final String MECH_MAX_MOBILITY_NAME    = "mech. max. mobility:";
    public static final String YIELD_STATE_COEFF_NAME    = "yield state coeff.";
    public static final String ULTIMATE_STATE_COEFF_NAME = "ultimate state coeff.";
    public static final String PART_VOL_FRACTION_NAME    = "particle volume fraction";
    public static final String PART_RADIUS_NAME          = "particle radius";
    public static final String MOLAR_MASS_NAME           = "molar mass";
    public static final String THRESHOLD_STRESS_NAME     = "threshold stress";
    public static final String TORSION_ENERGY_COEFF_NAME      = "tors.energy coeff-t for bulk";
    public static final String TORSION_ENERGY_COEFF_GB_1_NAME = "tors.energy coeff-t for gr.boundary";
    public static final String TORSION_ENERGY_COEFF_GB_2_NAME = "tors.energy coeff-t for gr.b.region";
    
    public static final String MIN_TWIN_TEMPERATURE_NAME      = "min_twin_temperature";
    public static final String TWINNING_TEMPERATURE_NAME      = "twinning_temperature";
    public static final String MAXIMAL_PROB_RECRYST_NAME      = "max_prob_recryst";
    public static final String MAXIMAL_PROB_TWINNING_NAME     = "max_prob_twinning";
    public static final String MINIMAL_DISL_DENSITY_NAME      = "min_disl_density";
    
    public static final String LATTICE_VECTOR_A_LENGTH_PROPERTIES   = "lattice_vector_A_length";
    public static final String LATTICE_VECTOR_B_LENGTH_PROPERTIES   = "lattice_vector_B_length";
    public static final String LATTICE_VECTOR_C_LENGTH_PROPERTIES   = "lattice_vector_C_length";
    
    public static final String LATTICE_ANGLE_VEC_A_VEC_B_PROPERTIES = "lattice_angle_vecA_vecB";
    public static final String LATTICE_ANGLE_VEC_B_VEC_C_PROPERTIES = "lattice_angle_vecB_vecC";
    public static final String LATTICE_ANGLE_VEC_C_VEC_A_PROPERTIES = "lattice_angle_vecC_vecA";
    
    public static final String LATTICE_ANISOTROPY_COEFF_PROPERTIES  = "lattice_anis_coeff";

    /** Names of parameter helps of Initial Condition
     */
    public static final String INITIAL_CONDITION_PARAMETERS_NAME = "Parameters of initial conditions";
    public static final String THERMAL_ENERGY_PROPERTIES         = "Thermal energy";
    public static final String MECHANICAL_ENERGY_PROPERTIES      = "Mechanical energy";
    public static final String TEMPERATURE_PROPERTIES            = "Temperature";

    /** Names of parameter helps of Boundary Condition
     */
    public static final String BOUNDARY_CONDITION_PARAMETERS_NAME = "Parameters of boundary conditions";
    public static final String HEAT_ENERGY_NAME                   = "Thermal loading";// "Velocity of heat energy influx";
    public static final String MECHANICAL_ENERGY_NAME             = "Velocity of mech. energy influx";
    public static final String THERMAL_ENERGY_NAME                = "Thermal loading";// "Velocity of heat energy influx";
    public static final String MECHANICAL_LOADING_NAME            = "Mechanical loading";
    public static final String TEMPERATURE_NAME                   = "Temperature";
    
    public static final String BOUND_TIME_FUNCTION_TYPE_NAME      = "Type of time function";
    public static final String BOUND_LOAD_TIME_PORTION_NAME       = "Loading time portion";
    public static final String BOUND_RELAX_TIME_PORTION_NAME      = "Relaxation time portion";

    public static final String TOP_NAME    = "Top facet";
    public static final String BOTTOM_NAME = "Bottom facet";
    public static final String LEFT_NAME   = "Left facet";
    public static final String RIGHT_NAME  = "Right facet";
    public static final String FRONT_NAME  = "Front facet";
    public static final String BACK_NAME   = "Back facet";

    public static final String STRESS_R_B_NAME      = "stress";
    public static final String STRAIN_R_B_NAME      = "strain";
    public static final String TEMPERATURE_R_B_NAME = "temperature";
    public static final String ENERGY_R_B_NAME      = "energy";
    public static final String N_A_R_B_NAME         = "N/A";
    
    /** Geometrical types of tanks */
    public static final String GEOMETRICAL_TYPE_OF_TANKS = "tank_geom_type";
    public static final String parallelepiped_tank       = "parallelepiped_tanks";
    public static final String vertical_ellyptic_tank    = "vertical_ellyptic_tanks";
    public static final String horizon_triangle_tank     = "horizontal_triangle_tanks";
    public static final String horizon_circle_tank       = "horizontal_circle_tanks";

    public static final String ADIOBATIC_PROPERTIES     = "This boundary is adiabatic.";
    public static final String ADIOBATIC_NOR_PROPERTIES = "This boundary is not adiabatic.";

    public static final String ADIOBATIC_NAME       = "Adiabatic";
    public static final String ADIOBATIC_NOR_NAME   = "Not adiabatic";

    public static final String INSERT_COMMENTS_NAME = "Comments:";

    public static final String TOP_MECHANICAL_ENERGY_PROPERTIES = "top_mechanical_loading";
    public static final String TOP_THERMAL_ENERGY_PROPERTIES = "top_thermal_influx";
    public static final String TOP_TEMPERATURE_PROPERTIES = "top_temperature";
    public static final String BOTTOM_MECHANICAL_ENERGY_PROPERTIES = "bottom_mechanical_loading";
    public static final String BOTTOM_THERMAL_ENERGY_PROPERTIES = "bottom_thermal_influx";
    public static final String BOTTOM_TEMPERATURE_PROPERTIES = "bottom_temperature";
    public static final String LEFT_MECHANICAL_ENERGY_PROPERTIES = "left_mechanical_loading";
    public static final String LEFT_THERMAL_ENERGY_PROPERTIES = "left_thermal_influx";
    public static final String LEFT_TEMPERATURE_PROPERTIES = "left_temperature";
    public static final String RIGHT_MECHANICAL_ENERGY_PROPERTIES = "right_mechanical_loading";
    public static final String RIGHT_THERMAL_ENERGY_PROPERTIES = "right_thermal_influx";
    public static final String RIGHT_TEMPERATURE_PROPERTIES = "right_temperature";
    public static final String FRONT_MECHANICAL_ENERGY_PROPERTIES = "front_mechanical_loading";
    public static final String FRONT_THERMAL_ENERGY_PROPERTIES = "front_thermal_influx";
    public static final String FRONT_TEMPERATURE_PROPERTIES = "front_temperature";
    public static final String BACK_MECHANICAL_ENERGY_PROPERTIES = "back_mechanical_loading";
    public static final String BACK_THERMAL_ENERGY_PROPERTIES = "back_thermal_influx";
    public static final String BACK_TEMPERATURE_PROPERTIES = "back_temperature";

    public static final String TOP_CHECK_MECHANICAL_ENERGY_PROPERTIES = "top_check_mechanical_energy";
    public static final String TOP_CHECK_THERMAL_ENERGY_PROPERTIES = "top_check_thermal_energy";
    public static final String TOP_CHECK_TEMPERATURE_PROPERTIES = "top_check_temperature";
    public static final String BOTTOM_CHECK_MECHANICAL_ENERGY_PROPERTIES = "bottom_check_mechanical_energy";
    public static final String BOTTOM_CHECK_THERMAL_ENERGY_PROPERTIES = "bottom_check_thermal_energy";
    public static final String BOTTOM_CHECK_TEMPERATURE_PROPERTIES = "bottom_check_temperature";
    public static final String LEFT_CHECK_MECHANICAL_ENERGY_PROPERTIES = "left_check_mechanical_energy";
    public static final String LEFT_CHECK_THERMAL_ENERGY_PROPERTIES = "left_check_thermal_energy";
    public static final String LEFT_CHECK_TEMPERATURE_PROPERTIES = "left_check_temperature";
    public static final String RIGHT_CHECK_MECHANICAL_ENERGY_PROPERTIES = "right_check_mechanical_energy";
    public static final String RIGHT_CHECK_THERMAL_ENERGY_PROPERTIES = "right_check_thermal_energy";
    public static final String RIGHT_CHECK_TEMPERATURE_PROPERTIES = "right_check_temperature";
    public static final String FRONT_CHECK_MECHANICAL_ENERGY_PROPERTIES = "front_check_mechanical_energy";
    public static final String FRONT_CHECK_THERMAL_ENERGY_PROPERTIES = "front_check_thermal_energy";
    public static final String FRONT_CHECK_TEMPERATURE_PROPERTIES = "front_check_temperature";
    public static final String BACK_CHECK_MECHANICAL_ENERGY_PROPERTIES = "back_check_mechanical_energy";
    public static final String BACK_CHECK_THERMAL_ENERGY_PROPERTIES = "back_check_thermal_energy";
    public static final String BACK_CHECK_TEMPERATURE_PROPERTIES = "back_check_temperature";

    public static final String BOUNDARY_TYPE_NAME = "Boundary type:";

    public static final String BOUNDARY_TYPE_PROPERTIES = "bound_interaction_type";
    public static final String BOUNDARY_FUNCTION_TYPE_PROPERTIES = "bound_function_type";

    public static final String BOUNDARY_TYPE_FIRST_NAME = "boundary cell DOES NOT CHANGE mechanical energy";
    public static final String BOUNDARY_TYPE_SECOND_NAME = "boundary cell CHANGES mechanical energy, interacting with INTERIOR cells";
    public static final String BOUNDARY_TYPE_THIRD_NAME = "boundary cell CHANGES mechanical energy, interacting with EVERY cell";

    /**
     *                      TASK PARAMETER PROPERTIES
     */

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

    public static final String TASK_TYPE_NAME = "Task type:";
    public static final String TASK_PARAMETERS_NAME = "Task parameters:";
    public static final String TASK_COMMENTS_NAME = "Task comments:";

    public static final String TASK_RESPONSE_RATE_NAME = "Response rate = ";
    public static final String TASK_RESPONSE_RATE_PROPERTIES = "response_rate";
    
  //  public static final String TORSION_ENERGY_COEFF_NAME = "Torsion energy coeff = ";
    public static final String TORSION_ENERGY_COEFF_COMMENT_PROPERTIES = "torsion_energy_coeff";
    
    public static final String ANISOTROPY_PRESENCE_PROPERTIES = "anisotropy";
    public static final String ANISOTROPY_VECTOR_X_PROPERTIES = "anis_vector_X";
    public static final String ANISOTROPY_VECTOR_Y_PROPERTIES = "anis_vector_Y";
    public static final String ANISOTROPY_VECTOR_Z_PROPERTIES = "anis_vector_Z";
    public static final String SPECIMEN_ANIS_COEFF_PROPERTIES = "specimen_anis_coeff";
    
    public static final String TIME_STEP_PROPERTIES = "time_step";
    public static final String TOTAL_TIME_PROPERTIES = "total_time";
    public static final String OUTPUT_FILE_PROPERTIES = "output_file_number";

    public static final String HEAT_TRANSFER_PROPERTIES = "simulate_heat_flows";
    public static final String RECRYSTALLIZATION_PROPERTIES = "simulate_recryst";
    public static final String MECHANICAL_ELATING_PROPERTIES = "simulate_mech_flows";
    public static final String CRACK_FORMATION_PROPERTIES = "simulate_cracks";
    public static final String EQUILIBRIUM_STATE_PROPERTIES = "equilibrium_state";
    public static final String SHOW_GRAIN_BOUNDS_PROPERTIES = "show_grain_bounds";

    public static final String TIME_STEP_NAME = "time step = ";
    public static final String TOTAL_TIME_NAME = "total time = ";
    public static final String OUTPUT_FILE_NAME = "output file number = ";

    public static final String HEAT_TRANSFER_NAME = "heat transfer";
    public static final String RECRYSTALLIZATION_NAME = "recrystallization";
    public static final String MECHANICAL_ELATING_NAME = "mechanical loading";
    public static final String CRACK_FORMATION_NAME = "crack formation";
    public static final String EQUILIBRIUM_STATE_NAME = "equilibrium state";
    public static final String SHOW_GRAIN_BOUNDS_NAME = "show grain bounds";

    public static final String CALC_HEAT_EXPANSION_NAME = "heat expansion";
    public static final String CALC_FORCE_MOMENTS_NAME  = "force moments";
    public static final String DEFECTS_INFLUENCE_NAME   = "influence of defects";
    public static final String GLOBAL_BOUNDARY_TYPE_NAME = "global boundary type";
    
    public static final String CALC_HEAT_EXPANSION_PROPERTIES = "simulate_heat_expansion";
    public static final String CALC_FORCE_MOMENTS_PROPERTIES = "calc_force_moments";
    public static final String BULK_SPECIMEN_MATERIAL_PROPERTIES = "bulk_material";
    public static final String GLOBAL_BOUNDARY_TYPE_PROPERTIES = "bound_type";
    public static final String DEFECTS_INFLUENCE_PROPERTIES= "defects_influence";

    /** Names of table columns
     */
    public static final String FIRST_COLUMN_NAME = "Phases";
    public static final String SECOND_COLUMN_NAME = "Volume fraction";
    public static final String THIRD_COLUMN_NAME = "Material";
    public static final String FOURTH_COLUMN_NAME = "Angle range";
    public static final String FIFTH_COLUMN_NAME = "Dislocation density";
    public static final String SIXS_COLUMN_NAME = "Maximal deviation";

    /**                SPECIMEN MATERIAL TABLE
     */
    public static final String FIRST_CHOOSE_NAME = "Select material";
    public static final String SECOND_CHOOSE_NAME = "Choose...";
    public static final int COLUMN_NUMBER = 5;

    /** Names of other helps
     */
    public static final String PARAMETERS_NAME = "Parameters:";
    public static final String CHECKED_POSITION = "true";
    public static final String UNCHECKED_POSITION = "false";

    /**                          COMMENTS
     */
    public static final String FIRST_COMMENT_IN_TASK_PARAMETERS = "# Files used by project 'FileCreation' for creation of files with initial and boundary conditions";
    public static final String SECOND_COMMENT_IN_TASK_PARAMETERS = "# Files with information about parameters of specimen, initial and boundary conditions";
    public static final String PACKING_TYPE_COMMENT = "# Variable responsible for type of packing of cellular automata:\n" +
            "# 0 - hexagonal close packing (HCP),\n" +
            "# 1 - simple cubic packing (SCP).";
    public static final String BOUNDARY_TYPE_COMMENT = "# Type of interaction of boundary cells with neighbours:\n"+
            "# 0 - boundary cell does not change mechanical energy because of interaction with neighbours;\n"+
            "# 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;\n"+
            "# 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.\n";
    
    public static final String BOUNDARY_FUNCTION_TYPE_COMMENT = "# Type of functional dependence of boundary cell parameter on its coordinates";
    
    /** Number of types of distribution of the values of boundary parameters on facets */
    public static final int BOUNDARY_TYPE_NUMBER = 27;
    
    /** Comments for each type of distribution of the values of boundary parameters on facets */
    public static final String[] BOUNDARY_TYPE_COMMENTS = 
    /* Type # 0*/ {"Unit function", 
    /* Type # 1*/  "Distance from boundary cell to the specimen centre",
    /* Type # 2*/  "The modified coordinate X of boundary cell",
    /* Type # 3*/  "The modified coordinate Y of boundary cell",
    /* Type # 4*/  "The modified coordinate Z of boundary cell",
    /* Type # 5*/  "Distance from boundary cell to the axis X",
    /* Type # 6*/  "Distance from boundary cell to the axis Y",
    /* Type # 7*/  "Distance from boundary cell to the axis Z",
    /* Type # 8*/  "Sum of the absolute values of boundary cell coordinates",
    /* Type # 9*/  "Sum of the absolute values of boundary cell coordinates X and Y",
    /* Type #10*/  "Sum of the absolute values of boundary cell coordinates X and Z",
    /* Type #11*/  "Sum of the absolute values of boundary cell coordinates Y and Z",
    /* Type #12*/  "The absolute value of boundary cell coordinate X",
    /* Type #13*/  "The absolute value of boundary cell coordinate Y",
    /* Type #14*/  "The absolute value of boundary cell coordinate Z",
    /* Type #15*/  "One minus 2/3 of the sum of absolute values of boundary cell coordinates",
    /* Type #16*/  "One minus the sum of absolute values of boundary cell coordinates X and Y",
    /* Type #17*/  "One minus the sum of absolute values of boundary cell coordinates X and Z",
    /* Type #18*/  "One minus the sum of absolute values of boundary cell coordinates Y and Z",
    /* Type #19*/  "One minus 2 multiplied by the absolute value of boundary cell coordinate X",
    /* Type #20*/  "One minus 2 multiplied by the absolute value of boundary cell coordinate Y",
    /* Type #21*/  "One minus 2 multiplied by the absolute value of boundary cell coordinate Z",
    /* Type #22*/  "One minus (2/sqrt(3))*(distance from boundary cell to the specimen centre)",
    /* Type #23*/  "One minus sqrt(2)*(distance from boundary cell to the axis X)",
    /* Type #24*/  "One minus sqrt(2)*(distance from boundary cell to the axis Y)",
    /* Type #25*/  "One minus sqrt(2)*(distance from boundary cell to the axis Z)",
    /* Type #26*/  "Distance from boundary cell to the origin of coordinate system"};    
    public static final String ANISOTROPY_PRESENCE_COMMENT = "# Variable responsible for account of anisotropy of simulated medium";    
    public static final String ANISOTROPY_VECTOR_COMMENT = "# Vector of anisotropy of simulated medium";
    public static final String SPECIMEN_ANIS_COEFF_COMMENT = "# Coefficient of anisotropy of simulated medium";

    /** MENU NAMES */
    public static final String SPECIMEN_MENU_NAME = "Specimen";
    public static final String CONDITION_MENU_NAME = "Conditions";
    public static final String TASK_MENU_NAME = "Task";
    public static final String RUN_MENU_NAME = "Run";
    public static final String HELP_MENU_NAME = "Help";
    public static final String RESULT_MENU_NAME = "Results";
    public static final String CHOOSE_SPECIMEN_MENU_NAME = "Choose specimen";
    public static final String CREATE_SPECIMEN_MENU_NAME = "Create specimen";
    public static final String EXIT_MENU_NAME = "Exit";
    public static final String INITIAL_CONDITION_MENU_NAME = "Initial conditions";
    public static final String BOUNDARY_CONDITION_MENU_NAME = "Boundary conditions";
    public static final String CHOOSE_TASK_MENU_NAME = "Choose task";
    public static final String CREATE_TASK_MENU_NAME = "Create task";
    public static final String RUN_PROGRAM_MENU_NAME = "Run program";
    public static final String ABOUT_MENU_NAME = "About";
    public static final String SHOW_RESULT_MENU_NAME = "Show results";
    public static final String CHOOSE_INITIAL_CONDITION_MENU_NAME = "Choose initial conditions";
    public static final String CREATE_INITIAL_CONDITION_MENU_NAME = "Create initial conditions";
    public static final String CHOOSE_BOUNDARY_CONDITION_MENU_NAME = "Choose boundary conditions";
    public static final String CREATE_BOUNDARY_CONDITION_MENU_NAME = "Create boundary conditions";
    public static final String CHOOSE_TANK_CONDITION_MENU_NAME = "Choose tank conditions";
    public static final String CREATE_TANK_CONDITION_MENU_NAME = "Create tank conditions";

    /*                  GRAIN STRUCTURE CREATEION     */
     public static final String GRAIN_STRUCTURE_CREATION_START_NAME = "CREATION OF GRAIN STRUCTURE IS STARTED!!!";
     public static final String GRAIN_STRUCTURE_CREATION_FINISH_NAME = "CREATION OF GRAIN STRUCTURE IS FINISHED!!!";

     /*                  SPECIMEN PARAMETERS TOOL TIPS     */
    public static final String CELL_DIAMETER_TOOL_TIP = "the size of cellular automaton";
    public static final String SPECIMEN_SIZE_X_TOOL_TIP = "the size of specimen along axis X";
    public static final String SPECIMEN_SIZE_Y_TOOL_TIP = "the size of specimen along axis Y";
    public static final String SPECIMEN_SIZE_Z_TOOL_TIP = "the size of specimen along axis Z";
    public static final String COORDINATE_X_TOOL_TIP = "the grain length along axis X";
    public static final String COORDINATE_Y_TOOL_TIP = "the grain length along axis Y";
    public static final String COORDINATE_Z_TOOL_TIP = "the grain length along axis Z";
    public static final String FE_NUMBER_X_TOOL_TIP = "the number of finite elements along axis X";
    public static final String FE_NUMBER_Y_TOOL_TIP = "the number of finite elements along axis Y";
    public static final String FE_NUMBER_Z_TOOL_TIP = "the number of finite elements along axis Z";
    public static final String ANISOTROPY_COEFF_TOOL_TIP = "the degree of anisotropy";
    public static final String AVERAGE_GRAIN_LENGTH_TOOL_TIP = "the average grain length";
    public static final String NUMBER_OF_PHASES_TOOL_TIP = "the number of structure elements with different material properties";
    public static final String SURF_THICKNESS_TOOL_TIP = "the thickness of surface layer";
    public static final String STOCHASTIC_METHOD_TOOL_TIP = "stochastic distribution of grain embryos";
    public static final String REGULAR_METHOD_TOOL_TIP = "regular distribution of grain embryos";
    public static final String PARTICLE_VOLUME_FRACTION_TOOL_TIP = "the volume fraction of inclusions distributed at grain boundaries";
    public static final String PARTICLE_RADIUS_TOOL_TIP = "the radius of inclusions at grain boundaries";
    public static final String MIN_NEIGHBOURS_NUMBER_TOOL_TIP = "the value responsible for grain boundary smoothness";
    
    /**                SPECIMEN MATERIAL TABLE TOOL TIPS     */
    public static final String VOLUME_FRACTION_TOOL_TIP = "the volume fraction of specified phase";
    public static final String ANGLE_RANGE_TOOL_TIP = "deviation of Euler angles of grains";
    public static final String DISLOCATION_DENSITY_TOOL_TIP = "average density of dislocations in grains";
    public static final String MAXIMAL_DEVIATION_TOOL_TIP = "deviation of dislocation density";

    /*           MAIN INTERFACE TOOL TIPS     */
    public static final String RESULTS_MENU_TOOL_TIP = "Show results of completed models...";
    public static final String RUN_MENU_TOOL_TIP = "Run program with chosen specimen, task, initial and boundary conditions...";
    public static final String HELP_MENU_TOOL_TIP = "Information about this interface...";
    public static final String CONDITION_MENU_TOOL_TIP = "Creation of boundary and initial conditions or choice from existing conditions...";
    public static final String TASK_MENU_TOOL_TIP = "Creation of task or choice from existing conditions...";
    public static final String SPECIMEN_MENU_TOOL_TIP = "Creation of specimen or choice from existing conditions...";

    /*          SAVE PARAMETERS CONDITIONS     */
    public static final String SPECIMEN_PARAM_NAME = "specimen";
    public static final String NOT_SPECIMEN_PARAM_NAME = "not specimen";
    public static final String INIT_PARAM_NAME = "init";
    public static final String NOT_INIT_PARAM_NAME = "not init";
    public static final String BOUND_PARAM_NAME = "bound";
    public static final String TANK_PARAM_NAME = "tank";
    public static final String NOT_BOUND_PARAM_NAME = "not bound";
    public static final String NOT_TANK_PARAM_NAME = "not tank";
    public static final String TASK_PARAM_NAME = "task";
    public static final String NOT_TASK_PARAM_NAME = "not task";
    public static final String RESULT_PARAM_NAME = "results";

    /*            PROGRESS FRAME ACT NAMES AND HELPS     */
    public static final String GRAIN_STRUCTURE_GENERATE_NAME = "generate_grain_structure";
    public static final String RECRYSTALLIZATION_CREATION_NAME = "recryst";
    public static final String COMPLETED_HELP_NAME = "Completed:";
    public static final String WAIT_HELP_NAME = "wait...";
    public static final String HELP_CREATE_GRAIN_STRUCTURE_NAME = "Creation of grain structure is in process...";
    public static final String HELP_START_RECRYSTALLIZATION_NAME = "Start of recrystallization...";

    /*            CONTROLL THINGS     */
    public static final String GRAIN_STRUCTURE_CREATOR_INFORMATION = "Grain structure was successfully completed.";

    // Show grains or not duirind grain structure creation
    public static String SHOW_GRAIN_STRUCTURE_VALUE = "1";
    public static String NOT_SHOW_GRAIN_STRUCTURE_VALUE = "0";
    public static String SHOW_GRAIN_STRUCTURE_PROPERTIES = "show_grain_bounds = ";

    /*
     *                      PICTURE CONSTANTS
     */

    public static int AFTER_DOT_ELEMENT_NUMBER = 2;

    /*
     *           SPECIAL SPECIMEN PARAMETER PROPERTIES
     */
    public static final String FIRST_LAYER_THICKNESS_PROPERTIES           = "layer1_thickness";
    public static final String SECOND_LAYER_THICKNESS_PROPERTIES          = "layer2_thickness";
    public static final String THIRD_LAYER_THICKNESS_PROPERTIES           = "layer3_thickness";
    public static final String SUBSTRATE_THICKNESS_PROPERTIES             = "substrate_thickness";

    public static final String FIRST_LAYER_GRAIN_SIZE_PROPERTIES          = "layer1_grain_size";
    public static final String FIRST_LAYER_GRAIN_SIZE_Y_PROPERTIES        = "layer1_grain_size_Y";
    public static final String FIRST_LAYER_GRAIN_SIZE_Z_PROPERTIES        = "layer1_grain_size_Z";
    
    public static final String UPPER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES   = "upper_layer2_grain_size";
    public static final String UPPER_SECOND_LAYER_GRAIN_SIZE_Y_PROPERTIES = "upper_layer2_grain_size_Y";
    public static final String UPPER_SECOND_LAYER_GRAIN_SIZE_Z_PROPERTIES = "upper_layer2_grain_size_Z";
    
    public static final String LOWER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES   = "lower_layer2_grain_size";
    public static final String LOWER_SECOND_LAYER_GRAIN_SIZE_Y_PROPERTIES = "lower_layer2_grain_size_Y";
    public static final String LOWER_SECOND_LAYER_GRAIN_SIZE_Z_PROPERTIES = "lower_layer2_grain_size_Z";
    
    public static final String THIRD_LAYER_GRAIN_SIZE_PROPERTIES          = "layer3_grain_size";
    public static final String THIRD_LAYER_GRAIN_SIZE_Y_PROPERTIES        = "layer3_grain_size_Y";
    public static final String THIRD_LAYER_GRAIN_SIZE_Z_PROPERTIES        = "layer3_grain_size_Z";
    
    public static final String SUBSTRATE_GRAIN_SIZE_PROPERTIES            = "substrate_grain_size";
    public static final String SUBSTRATE_GRAIN_SIZE_Y_PROPERTIES          = "substrate_grain_size_Y";
    public static final String SUBSTRATE_GRAIN_SIZE_Z_PROPERTIES          = "substrate_grain_size_Z";
    
    public static final String FIRST_LAYER_GRAIN_BOUND_THICK_PROPERTIES        = "layer1_grain_bound_region_thickness";
    public static final String UPPER_SECOND_LAYER_GRAIN_BOUND_THICK_PROPERTIES = "upper_layer2_grain_bound_region_thickness";
    public static final String LOWER_SECOND_LAYER_GRAIN_BOUND_THICK_PROPERTIES = "lower_layer2_grain_bound_region_thickness";
    public static final String THIRD_LAYER_GRAIN_BOUND_THICK_PROPERTIES        = "layer3_grain_bound_region_thickness";
    public static final String SUBSTRATE_GRAIN_BOUND_THICK_PROPERTIES          = "substrate_grain_bound_region_thickness";

    public static final String LAYER_ELEM_SIZE_PROPERTIES               = "layer2_elem_size";
    public static final String LAYER_TYPE_PROPERTIES                    = "layer2_type";
    public static final String LAYER_DIRECTION_PROPERTIES               = "layer_direction";        
    public static final String LAYER_TYPE_NO_CHOOSED                    = "Layer type or direction must be chosen!";    
    public static final String EMBRYO_DISTR_TYPE_IS_NOT_CHOSEN          = "Types of embryo distribution in layers must be chosen!";
    
    public static final byte CONTINUOUS_LAYER                    = 0;
    public static final byte HORIZONTAL_TRIANGLE_PRISMS          = 1;
    public static final byte HORIZONTAL_RIGHT_PARALLELEPIPEDS    = 2;
    public static final byte VERTICAL_SQUARE_PYRAMIDES           = 3;
    public static final byte VERTICAL_RIGHT_PARALLELEPIPEDS      = 4;    
    
    public static final byte LAYERS_ARE_PERPENDICULAR_TO_AXIS_X  = 0;
    public static final byte LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y  = 1;
    public static final byte LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z  = 2;
    
    // 0 - stochastic distribution of embryos;
    public static final byte EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE = 0;
            
    // 1 - regular distribution of embryos in centres of cubes;
    public static final byte EMBRYO_DISTRIBUTION_REGULAR_TYPE    = 1;
    
    // 2 - stochastic distribution of embryos in centres of cubes.
    public static final byte EMBRYO_DISTRIBUTION_MIXED_TYPE      = 2;
    
    // Type of distribution of grain embryos in surface layer:
    // 0 - stochastic distribution of embryos;
    // 1 - regular distribution of embryos in centres of cubes;
    // 2 - stochastic distribution of embryos in centres of cubes.
    public static final String SURFACE_LAYER_EMBRYO_DISTRIBUTION_TYPE     = "layer1_embryo_distr_type";

    // Type of distribution of grain embryos in upper intermediate layer
    public static final String UPPER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE = "upper_layer2_embryo_distr_type";

    // Type of distribution of grain embryos in lower intermediate layer
    public static final String LOWER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE = "lower_layer2_embryo_distr_type";
    
    // Type of distribution of grain embryos in upper substrate layer
    public static final String UPPER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE   = "layer3_embryo_distr_type";

    // Type of distribution of grain embryos in lower substrate layer
    public static final String LOWER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE   = "substrate_embryo_distr_type";

    /*
     * FILE COMMENTS
     */

    // Adress to file with comments
    public static String FILE_COMMENTS_ADRESS = "./user/comments.txt";

    // Specimen comments
    public static String SPECIMEN_PARAM_CELL_SIZE_COMMENT         = "SPECIMEN_PARAM_CELL_SIZE_COMMENT";
    public static String SPECIMEN_PARAM_ELEMENT_NUMBER_X_COMMENT  = "SPECIMEN_PARAM_ELEMENT_NUMBER_X_COMMENT";
    public static String SPECIMEN_PARAM_ELEMENT_NUMBER_Y_COMMENT  = "SPECIMEN_PARAM_ELEMENT_NUMBER_Y_COMMENT";
    public static String SPECIMEN_PARAM_ELEMENT_NUMBER_Z_COMMENT  = "SPECIMEN_PARAM_ELEMENT_NUMBER_Z_COMMENT";
    public static String SPECIMEN_PARAM_SURF_THICKNESS_COMMENT    = "SPECIMEN_PARAM_SURF_THICKNESS_COMMENT";
    public static String SPECIMEN_PARAM_MIN_NEIGHBOURS_NUMBER_COMMENT    = "SPECIMEN_PARAM_MIN_NEIGHBOURS_NUMBER_COMMENT";
    public static String SPECIMEN_PARAM_SPECIMEN_SIZE_X_COMMENT   = "SPECIMEN_PARAM_SPECIMEN_SIZE_X_COMMENT";
    public static String SPECIMEN_PARAM_SPECIMEN_SIZE_Y_COMMENT   = "SPECIMEN_PARAM_SPECIMEN_SIZE_Y_COMMENT";
    public static String SPECIMEN_PARAM_SPECIMEN_SIZE_Z_COMMENT   = "SPECIMEN_PARAM_SPECIMEN_SIZE_Z_COMMENT";
    public static String SPECIMEN_PARAM_COORDINATE_X_COMMENT      = "SPECIMEN_PARAM_COORDINATE_X_COMMENT";
    public static String SPECIMEN_PARAM_COORDINATE_Y_COMMENT      = "SPECIMEN_PARAM_COORDINATE_Y_COMMENT";
    public static String SPECIMEN_PARAM_COORDINATE_Z_COMMENT      = "SPECIMEN_PARAM_COORDINATE_Z_COMMENT";
    public static String SPECIMEN_PARAM_AVERAGE_GRAIN_LENGHT_COMMENT     = "SPECIMEN_PARAM_AVERAGE_GRAIN_LENGHT_COMMENT";
    public static String SPECIMEN_PARAM_ANISOTROPY_COEFF_COMMENT  = "SPECIMEN_PARAM_ANISOTROPY_COEFF_COMMENT";
    public static String SPECIMEN_PARAM_NUMBER_OF_PHASES_COMMENT  = "SPECIMEN_PARAM_NUMBER_OF_PHASES_COMMENT";
    public static String SPECIMEN_PARAM_PARTICLE_VOLUME_FRACTION_COMMENT = "SPECIMEN_PARAM_PARTICLE_VOLUME_FRACTION_COMMENT";
    public static String SPECIMEN_PARAM_PARTICLE_RADIUS_COMMENT   = "SPECIMEN_PARAM_PARTICLE_RADIUS_COMMENT";
    public static String SPECIMEN_PARAM_SHOW_GRAIN_BOUNDS_COMMENT = "SPECIMEN_PARAM_SHOW_GRAIN_BOUNDS_COMMENT";
    public static String SPECIMEN_PARAM_METHOD_COMMENT            = "SPECIMEN_PARAM_METHOD_COMMENT";
    public static String SPECIMEN_TABLE_COMMENT_FILE_SPM          = "SPECIMEN_TABLE_COMMENT_FILE_SPM";
    
    // Init cond comments
    public static String INIT_COND_COMMENT = "INIT_COND_COMMENT";
    
    // Bound cond comments
    public static String BOUND_COND_ADIABATIC_INF_COMMENT = "BOUND_COND_ADIABATIC_INF_COMMENT";
    public static String BOUND_COND_COMMENT               = "BOUND_COND_COMMENT";

    // Material comments
    public static String MATERIAL_PARAM_MOD_ELAST_COMMENT              = "MATERIAL_PARAM_MOD_ELAST_COMMENT";
    public static String MATERIAL_PARAM_DENSITY_COMMENT                = "MATERIAL_PARAM_DENSITY_COMMENT";
    public static String MATERIAL_PARAM_HEAT_EXPANSION_COEFF_COMMENT   = "MATERIAL_PARAM_HEAT_EXPANSION_COEFF_COMMENT";
    public static String MATERIAL_PARAM_YIELD_STRAIN_COMMENT           = "MATERIAL_PARAM_YIELD_STRAIN_COMMENT";
    public static String MATERIAL_PARAM_ULTIMATE_STRAIN_COMMENT        = "MATERIAL_PARAM_ULTIMATE_STRAIN_COMMENT";
    public static String MATERIAL_PARAM_ENERGY_COEFF_COMMENT           = "MATERIAL_PARAM_ENERGY_COEFF_COMMENT";
    public static String MATERIAL_PARAM_SPECIFIC_HEAT_CAPACITY_COMMENT = "MATERIAL_PARAM_SPECIFIC_HEAT_CAPACITY_COMMENT";
    public static String MATERIAL_PARAM_THERMAL_CONDUCTIVITY_COMMENT   = "MATERIAL_PARAM_THERMAL_CONDUCTIVITY_COMMENT";
    public static String MATERIAL_PARAM_THERMAL_CONDUCT_BOUND_COMMENT  = "MATERIAL_PARAM_THERMAL_CONDUCT_BOUND_COMMENT";
    public static String MATERIAL_PARAM_PHONON_PORTION_COMMENT         = "MATERIAL_PARAM_PHONON_PORTION_COMMENT";
    public static String MATERIAL_PARAM_LOW_TEMPER_THR_VALUE_COMMENT   = "MATERIAL_PARAM_LOW_TEMPER_THR_VALUE_COMMENT";
    public static String MATERIAL_PARAM_HIGH_TEMPER_THR_VALUE_COMMENT  = "MATERIAL_PARAM_HIGH_TEMPER_THR_VALUE_COMMENT";
    public static String MATERIAL_PARAM_ACT_ENERGY_COMMENT             = "MATERIAL_PARAM_ACT_ENERGY_COMMENT";
    public static String MATERIAL_PARAM_ANGLE_LIMIT_HAGB_COMMENT       = "MATERIAL_PARAM_ANGLE_LIMIT_HAGB_COMMENT";
    public static String MATERIAL_PARAM_ENERGY_HAGB_COMMENT            = "MATERIAL_PARAM_ENERGY_HAGB_COMMENT";
    public static String MATERIAL_PARAM_MAX_MOBILITY_COMMENT           = "MATERIAL_PARAM_MAX_MOBILITY_COMMENT";
    public static String MATERIAL_PARAM_DISL_MAX_MOBILITY_COMMENT      = "MATERIAL_PARAM_DISL_MAX_MOBILITY_COMMENT";
    public static String MATERIAL_PARAM_MECH_MAX_MOBILITY_COMMENT      = "MATERIAL_PARAM_MECH_MAX_MOBILITY_COMMENT";
    public static String MATERIAL_PARAM_MOD_SHEAR_COMMENT              = "MATERIAL_PARAM_MOD_SHEAR_COMMENT";
    public static String MATERIAL_PARAM_LATTICE_PARAMETER_COMMENT      = "MATERIAL_PARAM_LATTICE_PARAMETER_COMMENT";
    public static String MATERIAL_PARAM_DASL_DISTR_COEFF_COMMENT       = "MATERIAL_PARAM_DASL_DISTR_COEFF_COMMENT";

    // Task comments
    public static String TASK_FILE_CREATION_FILES_COMMENT = "TASK_FILE_CREATION_FILES_COMMENT";
    public static String TASK_PARAMETER_FILES_COMMENT = "TASK_PARAMETER_FILES_COMMENT";
    public static String TASK_PARAM_TIME_STEP_COMMENT = "TASK_PARAM_TIME_STEP_COMMENT";
    public static String TASK_PARAM_TOTAL_TIME_COMMENT = "TASK_PARAM_TOTAL_TIME_COMMENT";
    public static String TASK_PARAM_OUTPUT_FILE_NUMBER_COMMENT = "TASK_PARAM_OUTPUT_FILE_NUMBER_COMMENT";
    public static String TASK_SIMULATE_VARIABLES_COMMENT = "TASK_SIMULATE_VARIABLES_COMMENT";
    public static String TASK_STATE_SPECIMEN_VARIABLE_COMMENT = "TASK_STATE_SPECIMEN_VARIABLE_COMMENT";
    public static String TASK_PARAM_BULK_MATERIAL_COMMENT = "TASK_PARAM_BULK_MATERIAL_COMMENT";
    public static String TASK_PARAM_GENERATION_PROBABILITY_COMMENT = "TASK_PARAM_GENERATION_PROBABILITY_COMMENT";
    public static String TASK_PARAM_GROWTH_PROBABILITY_COMMENT = "TASK_PARAM_GROWTH_PROBABILITY_COMMENT";
    public static String TASK_PARAM_TURN_PROBABILITY_COMMENT = "TASK_PARAM_TURN_PROBABILITY_COMMENT";
    public static String TASK_PARAM_STRAIGHT_PROBABILITY_COMMENT = "TASK_PARAM_STRAIGHT_PROBABILITY_COMMENT";
    public static String TASK_PARAM_BRANCHING2_PROBABILITY_COMMENT = "TASK_PARAM_BRANCHING2_PROBABILITY_COMMENT";
    public static String TASK_PARAM_BRANCHING3_PROBABILITY_COMMENT = "TASK_PARAM_BRANCHING3_PROBABILITY_COMMENT";
    public static String TASK_PARAM_THICKENING_PROBABILITY_COMMENT = "TASK_PARAM_THICKENING_PROBABILITY_COMMENT";
    public static String TASK_PARAM_STEP_NUMBER_COMMENT = "TASK_PARAM_STEP_NUMBER_COMMENT";
    public static String TASK_PARAM_COEFF_INNER_ANISOTROPY_COMMENT = "TASK_PARAM_COEFF_INNER_ANISOTROPY_COMMENT";
    public static String TASK_PARAM_INNER_ANISOTROPY_VECTOR_COORD_COMMENT = "TASK_PARAM_INNER_ANISOTROPY_VECTOR_COORD_COMMENT";
    public static String TASK_PARAM_COEFF_OUTER_ANISOTROPY_COMMENT = "TASK_PARAM_COEFF_OUTER_ANISOTROPY_COMMENT";
    public static String TASK_PARAM_OUTER_ANISOTROPY_VECTOR_COORD_COMMENT = "TASK_PARAM_OUTER_ANISOTROPY_VECTOR_COORD_COMMENT";
    public static String TASK_PARAM_STOCHASTIC_EMBRYOS_COMMENT = "TASK_PARAM_STOCHASTIC_EMBRYOS_COMMENT";
    public static String TASK_PARAM_REGULAR_EMBRYOS_COMMENT = "TASK_PARAM_REGULAR_EMBRYOS_COMMENT";
    public static String TASK_PARAM_STOCH_EMBRYOS_IN_CUBES_COMMENT = "TASK_PARAM_STOCH_EMBRYOS_IN_CUBES_COMMENT";
    public static String TASK_PARAM_DISTRIBUTION_METHOD_COMMENT = "TASK_PARAM_DISTRIBUTION_METHOD_COMMENT";
    public static String TASK_PARAM_CALC_HEAT_EXPANSION_COMMENT = "# Variable responsible for simulation of heat expansion:\n" +
            "# if simulate_heat_expansion = 0 then heat expansion is not simulated,\n" +
            "# if simulate_heat_expansion = 1 then heat expansion is simulated.";
    public static String TASK_PARAM_CALC_FORCE_MOMENTS_COMMENT = "# Variable responsible for calculation of force moments at each time step:\n" +
            "# if calc_force_moments = 0 then force moments are not calculated,\n" +
            "# if calc_force_moments = 1 then force moments are calculated.";
    public static String TASK_PARAM_GLOBAL_BOUNDARY_TYPE_COMMENT = "# Variable for choice of type of loading of boundary facets:" +
            "\n# 0 - homogeneous mechanical loading and homogeneous heating," +
            "\n# 1 - mechanical loading by indentor and homogeneous heating," +
            "\n# 2 - homogeneous mechanical loading of facets and heating of circle area," +
            "\n# 3 - mechanical bending and homogeneous heating," +
            "\n# 4 - mechanical shear and homogeneous heating.\n";
    public static String TASK_RESPONSE_RATE_COMMENT = "# Effective rate of response on external action. \n";
    
    public static final String DEFECTS_INFLUENCE_COMMENT = "# Variable responsible for account of defects when calculating mobility of material:\n"
            + "# 0 - defects are not taken into account;\n"
            + "# 1 - defects are taken into account.";

//	last colors were: black, light_gray, cyan

    public static final String SHOW_STATE                    = "show_states";
    public static final String SHOW_BOUNDARY_STRUCTURE       = "show_boundary_structure";
    public static final String SHOW_GRAINS                   = "show_grains";
    
    public static final String SHOW_TEMPERATURE              = "temperature"; // "show_temperature"; // 
    public static final String SHOW_STRAIN                   = "strain"; // "show_strain"; // 
    public static final String SHOW_EFFECTIVE_STRESS         = "effective stress"; // "show_effective_stress"; // 
    public static final String SHOW_PRINCIPAL_STRESS         = "principal stress"; // "show_principal_stress"; // 
    public static final String SHOW_MOMENTS_X                = "x moments";
    public static final String SHOW_MOMENTS_Y                = "y moments";
    public static final String SHOW_MOMENTS_Z                = "z moments";    
    public static final String SHOW_DISLOCATION_DENSITIES    = "dislocation density"; // "show dislocation densities"; // 
    
    public static final String SHOW_SPEC_BOUND_STRUCTURE     = "show_specimen_boundary_structure";
    public static final String SHOW_ALL_SPECIMEN_STRUCTURE   = "show all specimen structure"; // "show all"; // 
    public static final String SHOW_SPECIMEN_GRAIN_STRUCTURE = "show_grains";
    public static final String SHOW_SPECIMEN_BOUNDARIES      = "show_boundaries";
    public static final String SHOW_STATES                   = "show_states";
    
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
    
    /** Cell located in crack */
    public static final byte CRACK_CELL = 7;
    
    /** Cell located at boundary between specimen and outer space */
    public static final byte BOUNDARY_CELL = 8;
    
    /** Cell located at boundary between specimen and outer space and at grain boundary */
    public static final byte BOUNDARY_INTERGRANULAR_CELL = 9;
    
    /** Cell located in transformed material */
    public static final byte TRANSFORMED_CELL = 10;
    
    /** Types of outer boundary cells according to types of mechanical and thermal interaction
    * of grains and possibility of grain recrystallization */
    public static final byte LAYER_CELL                 =  0;
    public static final byte INNER_CELL                 =  1;//INTERNAL_INTERACTED_CELL
    public static final byte ADIABATIC_TEMPERATURE_CELL =  3;//BOUNDARY_INTERACTED_CELL
    public static final byte ADIABATIC_THERMAL_CELL     =  4;
    public static final byte STRAIN_ADIABATIC_CELL      =  5;
    public static final byte STRAIN_TEMPERATURE_CELL    =  6;
    public static final byte STRAIN_THERMAL_CELL        =  7;
    public static final byte STRESS_ADIABATIC_CELL      =  8;
    public static final byte STRESS_TEMPERATURE_CELL    =  9;
    public static final byte STRESS_THERMAL_CELL        = 10;
    public static final byte ADIABATIC_ADIABATIC_CELL   = 11;//BOUNDARY_ADIABATIC_CELL
    
    /** Types of grain according to its role in recrystallization process:
     * 0 - initial grain; 1 - new grain; 2 - twinning grain.
     */
    public static final byte INITIAL_GRAIN  = 0;
    public static final byte NEW_GRAIN      = 1;
    public static final byte TWIN_GRAIN = 2;
}