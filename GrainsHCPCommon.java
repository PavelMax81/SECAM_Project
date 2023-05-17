package grainsCore;

   /* 
    * @(#) GrainsHCPCommon.java version 1.0;       Mar 2006
    *
    * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
    * 
    * Class for info about parameters of cell and specimen for modelling of fracture. 
    *
    *=============================================================
    *  Last changes :
    *         12 Mar 2007 by Pavel V. Maksimov (the parameter "READ_FILE_NAME" and 
    *                                           "WRITE_FILE_NAME" are added)
    *            File version 1.1
    */

   /** Class for info about parameters of cell and specimen for modelling of fracture.
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.1 - Dec 2006
    */

public class GrainsHCPCommon
{
  /** Name of file with information about name of task */
  public static String TASK_PATH                     = "./user/task_db/";

  /** Name of file with information about specimen */
  public static String SPEC_PATH                     = "./user/spec_db/";

  /** Name of file with information about name of task */
  public static String TASK_NAME_FILE                = "task";

  /** Extension of file with information about name of task */
  public static String TASK_EXTENSION                = ".seca";

  /** Extension of file with information about grain colours */
  public static String COLOURS_EXTENSION             = ".clrs";
  
  /** Name of file with description of task */
//  public static String TASK_DESCRIPTION_FILE         = "grain_creation_description.txt";
    
  /** Name of file with information about model conditions */
//  public static String MODEL_CONDITIONS_FILE         = "model_conditions.txt";
  
  /** String that is written when creation of two specimens without embryos of cracks is in progress */
  public static String CREATION_OF_SPECIMENS = "Creation of two specimens without embryos of cracks is in progress.";
  
  /** String that is written when generation of embryos of cracks is in progress */
  public static String GENERATION_OF_EMBRYOS = "Generation of embryos of cracks is in progress (time step #0).";
  
  /** String that is written when time step is started */
  public static String START_OF_TIME_STEP = "START OF TIME STEP # ";
  
  /** String that is written when calculation is in progress */
  public static String CALCULATION_IS_IN_PROGRESS = "   Calculation of number of damaged neighbours at each of 3 coordination spheres " +
                                                    "of every undamaged cell and determination of the probability of damage of every undamaged cell";
  
  /** String that is written when 1st cycle of calculations is finished*/
  public static String CALCULATION_PERIOD_1 = "The period of determination of numbers of clusters (in milliseconds): ";
   
  /** String that is written when 2nd cycle of calculations is finished*/
  public static String CALCULATION_PERIOD_2 = "The period of execution of the 1st spacial cycle (in milliseconds) at time step ";
  
  /** String that is written when determination of new states of every cell is in progress */
  public static String DETERMINATION_OF_NEW_CELL_STATES = "Determination of new states of every cell is in progress.";
  
  /** String that is written when 3rd cycle of calculations is finished*/
  public static String CALCULATION_PERIOD_3 = "The period of execution of the 2nd spacial cycle (in milliseconds) at time step ";
  
  /** String that is written when 4th cycle of calculations is finished*/
  public static String CALCULATION_PERIOD_4 = "The period of execution of the 3rd spacial cycle (in milliseconds) at time step ";
  
  /** String that is written when 5th cycle of calculations is finished*/
  public static String CALCULATION_PERIOD_5 = "The period of execution of the 4th spacial cycle (in milliseconds) at time step ";
  
  /** String that is written when calculations are finished*/
  public static String TOTAL_CALCULATION_PERIOD = "The period of execution of the algorithm (milliseconds) at time step ";
  
  /** String that is written when total period of algorithm execution is printed. */
  public static String EXECUTION_PERIOD = "The period of execution of all time steps of the algorithm (milliseconds): ";
          
  /** String that is written when time step are finished*/
  public static String END_OF_TIME_STEP = "END OF TIME STEP # ";
  
  /** String that is written when number of cells in cluster is determined */
  public static String NUMBER_OF_CELLS =  "Number of cells in cluster ";
           
  /** String that is written when total number of cells in cluster is determined */
  public static String TOTAL_NUMBER_OF_CELLS = "Total number of cells in all clusters: ";
       
  /** String that is written when number of cells switched at current time step is determined */
  public static String NUMBER_OF_SWITCHED_CELLS = "The number of cells changed its state at time step #";
  
  /** String that is written when number of time step is printed */
  public static String STEP_COUNTER_EQUALS = "stepCounter = ";
  
  /** String that is written when number of clusters is determined incorrectly */
  public static String ERROR_1 = "Error of determination of number of clusters!!!";
     
  /** Number of output files for writing of dendritic growth picture */
  public static int OUTPUT_FILES_NUMBER              = 100;
    
  /** Undamaged cell*/
  public static final byte  UNDAMAGED_CELL = 0;
  
  /** Damaged cell*/
  public static final byte  DAMAGED_CELL = 1;
  
  /** Index of cell located outside of specimen */
  public static final byte  CELL_OUTSIDE_OF_SPECIMEN = -3;

  /** Constant for the case when there is no error */
  public static final int NO_ERROR = 0;

  /** Constant for the case when the number of colours is not sufficient */
  public static final int ERROR_NOT_SUFFICIENT_COLOUR_NUMBER = 1;

  // FILE DIRECTORIES
  public static final String DEND_PARAM_DIRECTION = "./user/dend_db/";
  public static final String DEND_RESULT_DIRECTION = "./user/dend_db/";

  // FILE EXTENSIONS
  public static final String DEND_PARAM_EXTENSION = "dnd";
  public static final String DEND_RESULT_EXTENSION = "dres";
  public static final String DEND_COLOR_EXTENSION = "clrs";
}