package grainsCore;

   /* 
    * @(#) LoadProperties.java version 1.0;       Mar 2006
    *
    * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
    * 
    * Class for info about parameters of cell and specimen for modelling of fracture. 
    *
    *=============================================================
    *  Last changes :
    *         07 Feb 2007 by Pavel V. Maksimov (creation of the class)
    *            File version 1.1
    */

   /** Class for info about parameters of cell and specimen for modelling of fracture.
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.1 - Dec 2006
    */

import java.util.*;
import java.io.*;
import util.*;

/** Class for loading of properties of cell and specimen for modelling of fracture.
 *  @author Dmitrii D. Moiseenko & Pavel V. Maksimov
 *  @version 1.1 - Dec 2006
 */

public class LoadProperties 
{	
  /** Name of file with initial grain indices of each inner cell */
  public static String      INIT_COND_FILE_NAME;
  
  /** Name of file with initial geometry of specimen */
  public static String      INIT_GEOM_FILE_NAME;

  /** Name of file with information about specimen layers */
  public static String      LAYERS_FILE_NAME;
  
  /** The probability of generation of crack */
  public static double      GENERATION_PROBABILITY;

  /** The probability of growth of crack */
  public static double      GROWTH_PROBABILITY;
  
  /** The probability of turn of crack */
  public static double      TURN_PROBABILITY;
      
  /** The probability of straight growth of crack */
  public static double      STRAIGHT_PROBABILITY;
  
  /** The probability of branching of crack (2 branches) */
  public static double      BRANCHING2_PROBABILITY;
  
  /** The probability of branching of crack (3 branches) */
  public static double      BRANCHING3_PROBABILITY;
  
  /** The probability of thickening of crack */
  public static double      THICKENING_PROBABILITY;
  
  /** Coefficient of inner anisotropy */
  public static double      COEFF_INNER_ANISOTROPY;
  
  /** Coefficient of outer anisotropy */
  public static double      COEFF_OUTER_ANISOTROPY;
    
  /** Coordinates of inner anisotropy vector */
  public static double      INNER_ANISOTROPY_COORD_X;
  public static double      INNER_ANISOTROPY_COORD_Y;
  public static double      INNER_ANISOTROPY_COORD_Z;
  
  /** Coordinates of outer anisotropy vector */
  public static double      OUTER_ANISOTROPY_COORD_X;
  public static double      OUTER_ANISOTROPY_COORD_Y;
  public static double      OUTER_ANISOTROPY_COORD_Z;
  
  /** Number of time steps */
  public static int         STEP_NUMBER;
  
  /** Number of cells on X-direction */
  public static int         CELL_NUMBER_I;
  
  /** Number of cells on Y-direction */
  public static int         CELL_NUMBER_J;
  
  /** Number of cells on Z-direction */
  public static int         CELL_NUMBER_K;
  
  /** Field responsible for stochastic distribution of embryos */
  public static byte        STOCHASTIC_EMBRYOS;

  /** Field responsible for regular distribution of embryos in centres of cubes */
  public static byte        REGULAR_EMBRYOS;

  /** Field responsible for stochastic distribution of embryos in centres of cubes */
  public static byte        STOCH_EMBRYOS_IN_CUBES;

  /** Field responsible for method of distribution of embryos */
  public static byte        DISTRIBUTION_METHOD;

  /** Average sizes (in cell diameters) of grain along axes in surface layer */
  public static double      AVERAGE_GRAIN_SIZE_2, AVERAGE_GRAIN_SIZE_2_Y, AVERAGE_GRAIN_SIZE_2_Z;

  /** Average sizes (in cell diameters) of grain along axes in higher intermediate layer */
  public static double      AVERAGE_GRAIN_SIZE_3, AVERAGE_GRAIN_SIZE_3_Y, AVERAGE_GRAIN_SIZE_3_Z;

  /** Average sizes (in cell diameters) of grain along axes in lower intermediate layer */
  public static double      AVERAGE_GRAIN_SIZE_4, AVERAGE_GRAIN_SIZE_4_Y, AVERAGE_GRAIN_SIZE_4_Z;

  /** Average sizes (in cell diameters) of grain along axes in higher substrate */
  public static double      AVERAGE_GRAIN_SIZE_5, AVERAGE_GRAIN_SIZE_5_Y, AVERAGE_GRAIN_SIZE_5_Z;

  /** Average sizes (in cell diameters) of grain along axes in lower substrate */
  public static double      AVERAGE_GRAIN_SIZE, AVERAGE_GRAIN_SIZE_Y, AVERAGE_GRAIN_SIZE_Z;
  
  /** Thickness of grain boundary region in surface layer */
  public static double      GRAIN_BOUND_REGION_THICKNESS_2;
  
  /** Thickness of grain boundary region in higher intermediate layer */
  public static double      GRAIN_BOUND_REGION_THICKNESS_3;
  
  /** Thickness of grain boundary region in lower intermediate layer */
  public static double      GRAIN_BOUND_REGION_THICKNESS_4;
  
  /** Thickness of grain boundary region in higher substrate */
  public static double      GRAIN_BOUND_REGION_THICKNESS_5;
  
  /** Thickness of grain boundary region in lower substrate */
  public static double      GRAIN_BOUND_REGION_THICKNESS;
  
  /** Radius of cell for visualization */
  public static final double CELL_RADIUS = 1.0;

  /** Diameter of real cell */
  public static double CELL_SIZE;
  
  /** Size of specimen along axis X */
  public static double SPECIMEN_SIZE_X;
    
  /** Size of specimen along axis Y */
  public static double SPECIMEN_SIZE_Y;
    
  /** Size of specimen along axis Z */
  public static double SPECIMEN_SIZE_Z;

  /** Size of specimen along axis Z */
  public static byte SHOW_GRAIN_BOUNDS;

  /** Type of packing of cellular automata */
  public static byte PACKING_TYPE;

  /** Name of specimen */
  public static String SPECIMEN_NAME;
          
  /** Just the Properties of the task initial conditions
   */	
  public java.util.Properties task_properties;

  /** The constructor loads properties of the task initial conditions
   * @param file_name name of file keeping all the properties
   */
  public LoadProperties (String file_name) 
  {
    task_properties = new Properties();

    try
    {
	FileInputStream fin = new FileInputStream(file_name);
	task_properties.load(fin);
	fin.close();
        
        System.out.println();
        System.out.println("Name of read file: "+file_name);
    } 
    catch(IOException io_ex) 
    {
        System.out.println("ERROR: "+io_ex);
    }

    GENERATION_PROBABILITY   = (new Double(task_properties.getProperty("GENERATION_PROBABILITY"))).doubleValue();
    GROWTH_PROBABILITY       = (new Double(task_properties.getProperty("GROWTH_PROBABILITY"))).doubleValue();
    TURN_PROBABILITY         = (new Double(task_properties.getProperty("TURN_PROBABILITY"))).doubleValue();
    STRAIGHT_PROBABILITY     = (new Double(task_properties.getProperty("STRAIGHT_PROBABILITY"))).doubleValue();
    BRANCHING2_PROBABILITY   = (new Double(task_properties.getProperty("BRANCHING2_PROBABILITY"))).doubleValue();
    BRANCHING3_PROBABILITY   = (new Double(task_properties.getProperty("BRANCHING3_PROBABILITY"))).doubleValue();
    THICKENING_PROBABILITY   = (new Double(task_properties.getProperty("THICKENING_PROBABILITY"))).doubleValue();
    
    COEFF_INNER_ANISOTROPY   = (new Double(task_properties.getProperty("COEFF_INNER_ANISOTROPY"))).doubleValue();
    INNER_ANISOTROPY_COORD_X = (new Double(task_properties.getProperty("INNER_ANISOTROPY_COORD_X"))).doubleValue();
    INNER_ANISOTROPY_COORD_Y = (new Double(task_properties.getProperty("INNER_ANISOTROPY_COORD_Y"))).doubleValue();
    INNER_ANISOTROPY_COORD_Z = (new Double(task_properties.getProperty("INNER_ANISOTROPY_COORD_Z"))).doubleValue();    
    
    COEFF_OUTER_ANISOTROPY   = (new Double(task_properties.getProperty("COEFF_OUTER_ANISOTROPY"))).doubleValue();
    OUTER_ANISOTROPY_COORD_X = (new Double(task_properties.getProperty("OUTER_ANISOTROPY_COORD_X"))).doubleValue();
    OUTER_ANISOTROPY_COORD_Y = (new Double(task_properties.getProperty("OUTER_ANISOTROPY_COORD_Y"))).doubleValue();
    OUTER_ANISOTROPY_COORD_Z = (new Double(task_properties.getProperty("OUTER_ANISOTROPY_COORD_Z"))).doubleValue();
    
    STEP_NUMBER              = (new Integer(task_properties.getProperty("STEP_NUMBER"))).intValue();
    
    STOCHASTIC_EMBRYOS       = (new Byte(task_properties.getProperty("STOCHASTIC_EMBRYOS"))).byteValue();
    REGULAR_EMBRYOS          = (new Byte(task_properties.getProperty("REGULAR_EMBRYOS"))).byteValue();
    STOCH_EMBRYOS_IN_CUBES   = (new Byte(task_properties.getProperty("STOCH_EMBRYOS_IN_CUBES"))).byteValue();
    DISTRIBUTION_METHOD      = (new Byte(task_properties.getProperty("DISTRIBUTION_METHOD"))).byteValue();

    SPECIMEN_NAME            = (task_properties.getProperty("specimen_name"));
    INIT_COND_FILE_NAME      = (task_properties.getProperty("init_cond_input_file"));
    INIT_GEOM_FILE_NAME      = (task_properties.getProperty("init_cond_file_geometry"));
    LAYERS_FILE_NAME         = (task_properties.getProperty("layers_file_name"));

    SPECIMEN_SIZE_X          = (new Double(task_properties.getProperty("specimen_size_X"))).doubleValue();
    SPECIMEN_SIZE_Y          = (new Double(task_properties.getProperty("specimen_size_Y"))).doubleValue();
    SPECIMEN_SIZE_Z          = (new Double(task_properties.getProperty("specimen_size_Z"))).doubleValue();

    // Rounding of the values of specimen sizes
    SPECIMEN_SIZE_X          = 0.001*(int)Math.round(1000*SPECIMEN_SIZE_X);
    SPECIMEN_SIZE_Y          = 0.001*(int)Math.round(1000*SPECIMEN_SIZE_Y);
    SPECIMEN_SIZE_Z          = 0.001*(int)Math.round(1000*SPECIMEN_SIZE_Z);

    CELL_SIZE                = (new Double(task_properties.getProperty("cell_size"))).doubleValue();

    SHOW_GRAIN_BOUNDS        = (new Byte(task_properties.getProperty("show_grain_bounds"))).byteValue();
    PACKING_TYPE             = (new Byte(task_properties.getProperty("packing_type"))).byteValue();

    CELL_NUMBER_I            = (int)Math.round((SPECIMEN_SIZE_X/(2*CELL_RADIUS)-1)/Math.sqrt(3.0/4.0) + 1);
    CELL_NUMBER_J            = (int)Math.round( SPECIMEN_SIZE_Y/(2*CELL_RADIUS));
    CELL_NUMBER_K            = (int)Math.round((SPECIMEN_SIZE_Z/(2*CELL_RADIUS)-1)/Math.sqrt(2.0/3.0) + 1);

  /*
    if(PACKING_TYPE==Common.HEXAGONAL_CLOSE_PACKING)
    {
        CELL_NUMBER_I            = (int)Math.round((SPECIMEN_SIZE_X/(2*cell_radius)-1)/Math.sqrt(3.0/4.0) + 1);
        CELL_NUMBER_J            = (int)Math.round( SPECIMEN_SIZE_Y/(2*cell_radius));
        CELL_NUMBER_K            = (int)Math.round((SPECIMEN_SIZE_Z/(2*cell_radius)-1)/Math.sqrt(2.0/3.0) + 1);
    }

    if(PACKING_TYPE==Common.SIMPLE_CUBIC_PACKING)
    {
        CELL_NUMBER_I     = (int)Math.floor((SPECIMEN_SIZE_X/cell_radius+1)/2);
        CELL_NUMBER_J     = (int)Math.floor((SPECIMEN_SIZE_Y/cell_radius+1)/2);
        CELL_NUMBER_K     = (int)Math.floor((SPECIMEN_SIZE_Z/cell_radius+1)/2);
    }
  */

    try
    {
	FileInputStream fin = new FileInputStream(LAYERS_FILE_NAME);
	task_properties.load(fin);
	fin.close();

        System.out.println();
        System.out.println("Name of read file: "+LAYERS_FILE_NAME);
    }
    catch(IOException io_ex)
    {
        System.out.println("ERROR: "+io_ex);
    }

    AVERAGE_GRAIN_SIZE_2           = (new Double(task_properties.getProperty("layer1_grain_size"))).doubleValue();
    AVERAGE_GRAIN_SIZE_2_Y         = (new Double(task_properties.getProperty("layer1_grain_size_Y"))).doubleValue();
    AVERAGE_GRAIN_SIZE_2_Z         = (new Double(task_properties.getProperty("layer1_grain_size_Z"))).doubleValue();
    
    AVERAGE_GRAIN_SIZE_3           = (new Double(task_properties.getProperty("upper_layer2_grain_size"))).doubleValue();
    AVERAGE_GRAIN_SIZE_3_Y         = (new Double(task_properties.getProperty("upper_layer2_grain_size_Y"))).doubleValue();
    AVERAGE_GRAIN_SIZE_3_Z         = (new Double(task_properties.getProperty("upper_layer2_grain_size_Z"))).doubleValue();
    
    AVERAGE_GRAIN_SIZE_4           = (new Double(task_properties.getProperty("lower_layer2_grain_size"))).doubleValue();
    AVERAGE_GRAIN_SIZE_4_Y         = (new Double(task_properties.getProperty("lower_layer2_grain_size_Y"))).doubleValue();
    AVERAGE_GRAIN_SIZE_4_Z         = (new Double(task_properties.getProperty("lower_layer2_grain_size_Z"))).doubleValue();
    
    AVERAGE_GRAIN_SIZE_5           = (new Double(task_properties.getProperty("layer3_grain_size"))).doubleValue();
    AVERAGE_GRAIN_SIZE_5_Y         = (new Double(task_properties.getProperty("layer3_grain_size_Y"))).doubleValue();
    AVERAGE_GRAIN_SIZE_5_Z         = (new Double(task_properties.getProperty("layer3_grain_size_Z"))).doubleValue();
    
    AVERAGE_GRAIN_SIZE             = (new Double(task_properties.getProperty("substrate_grain_size"))).doubleValue();
    AVERAGE_GRAIN_SIZE_Y           = (new Double(task_properties.getProperty("substrate_grain_size_Y"))).doubleValue();
    AVERAGE_GRAIN_SIZE_Z           = (new Double(task_properties.getProperty("substrate_grain_size_Z"))).doubleValue();
    
    GRAIN_BOUND_REGION_THICKNESS_2 = (new Double(task_properties.getProperty("layer1_grain_bound_region_thickness"))).doubleValue();
    GRAIN_BOUND_REGION_THICKNESS_3 = (new Double(task_properties.getProperty("upper_layer2_grain_bound_region_thickness"))).doubleValue();
    GRAIN_BOUND_REGION_THICKNESS_4 = (new Double(task_properties.getProperty("lower_layer2_grain_bound_region_thickness"))).doubleValue();
    GRAIN_BOUND_REGION_THICKNESS_5 = (new Double(task_properties.getProperty("layer3_grain_bound_region_thickness"))).doubleValue();
    GRAIN_BOUND_REGION_THICKNESS   = (new Double(task_properties.getProperty("substrate_grain_bound_region_thickness"))).doubleValue();
    
    System.out.println("\nSubstrate: grain_bound_region_thickness = "+GRAIN_BOUND_REGION_THICKNESS+"\n");

    // Transition from cell diameters to cell radiuses
    AVERAGE_GRAIN_SIZE_2           = 2*AVERAGE_GRAIN_SIZE_2;
    AVERAGE_GRAIN_SIZE_2_Y         = 2*AVERAGE_GRAIN_SIZE_2_Y;
    AVERAGE_GRAIN_SIZE_2_Z         = 2*AVERAGE_GRAIN_SIZE_2_Z;
    
    AVERAGE_GRAIN_SIZE_3           = 2*AVERAGE_GRAIN_SIZE_3;
    AVERAGE_GRAIN_SIZE_3_Y         = 2*AVERAGE_GRAIN_SIZE_3_Y;
    AVERAGE_GRAIN_SIZE_3_Z         = 2*AVERAGE_GRAIN_SIZE_3_Z;
    
    AVERAGE_GRAIN_SIZE_4           = 2*AVERAGE_GRAIN_SIZE_4;
    AVERAGE_GRAIN_SIZE_4_Y         = 2*AVERAGE_GRAIN_SIZE_4_Y;
    AVERAGE_GRAIN_SIZE_4_Z         = 2*AVERAGE_GRAIN_SIZE_4_Z;
    
    AVERAGE_GRAIN_SIZE_5           = 2*AVERAGE_GRAIN_SIZE_5;
    AVERAGE_GRAIN_SIZE_5_Y         = 2*AVERAGE_GRAIN_SIZE_5_Y;
    AVERAGE_GRAIN_SIZE_5_Z         = 2*AVERAGE_GRAIN_SIZE_5_Z;
    
    AVERAGE_GRAIN_SIZE             = 2*AVERAGE_GRAIN_SIZE;
    AVERAGE_GRAIN_SIZE_Y           = 2*AVERAGE_GRAIN_SIZE_Y;
    AVERAGE_GRAIN_SIZE_Z           = 2*AVERAGE_GRAIN_SIZE_Z;
    
    GRAIN_BOUND_REGION_THICKNESS_2 = 2*GRAIN_BOUND_REGION_THICKNESS_2;
    GRAIN_BOUND_REGION_THICKNESS_3 = 2*GRAIN_BOUND_REGION_THICKNESS_3;
    GRAIN_BOUND_REGION_THICKNESS_4 = 2*GRAIN_BOUND_REGION_THICKNESS_4;
    GRAIN_BOUND_REGION_THICKNESS_5 = 2*GRAIN_BOUND_REGION_THICKNESS_5;
    GRAIN_BOUND_REGION_THICKNESS   = 2*GRAIN_BOUND_REGION_THICKNESS;

    System.out.println();
    System.out.println("Number of cells along axis X: "+CELL_NUMBER_I);
    System.out.println("Number of cells along axis Y: "+CELL_NUMBER_J);
    System.out.println("Number of cells along axis Z: "+CELL_NUMBER_K);
    System.out.println("Total number of cells: "+CELL_NUMBER_I*CELL_NUMBER_J*CELL_NUMBER_K);
    System.out.println();
  }
}