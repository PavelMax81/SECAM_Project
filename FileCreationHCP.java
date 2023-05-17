package corecalc;

import java.util.*;
import java.io.*;
import util.*;
import interf.*;
import javafx.scene.control.TextArea;
//import cellcore.*;

/*
 * @(#) FileCreationHCP.java version 1.0.1;       Apr 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation of files with information about
 * initial and boundary conditions
 *
 *=============================================================
 *  Last changes:
 *         24 February 2009 by Pavel V. Maksimov (change of methods "createInitCondFile3D()",
 *                                                "createBoundCondFile3D()", "createGrainsFile3D()");
 *         07 April    2009 by Pavel V. Maksimov (creation of file with information about initial geometry
 *                                                of specimen in method "createInitCondFile3D")
 *                                                creation of file with information about geometry of
 *                                                boundary cells in method "createBoundCondFile3D");
 *         05 October  2009 by Pavel V. Maksimov (change of method "createBoundCondFile": calculation of
 *                                                influxes of mechanical and thermal energy to boundary cell
 *                                                according to values of influxes of specific surface energies
 *                                                to corresponding facets);
 *         31 March    2011 by Pavel V. Maksimov (creation of method "createBoundCondTanksFile(...)" for recording 
 *                                                of boundary conditions including tanks to the file);
 *         04 May      2011 by Pavel V. Maksimov (correction of method "createBoundCondTanksFile(...)"for recording
 *                                                of boundary conditions including tanks to the file);
 *         13 February 2015 by Pavel V. Maksimov (correction of the method "createVarBoundCondTanksFile(...)" for thermal loading 
 *                                                and renaming of the method "boundaryMechParam(...)" to "boundaryParam(...)").
 *
 *         File version 1.0.6
 */

/** Class for creation of files with information about
 * initial and boundary conditions
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0.6 - Feb 2015
 */
public class FileCreationHCP
{
    protected Properties task_properties;

    /** Name of file with information about name of task */
    protected static String TASK_PATH                     = "./user/task_db/";

    /** Name of file with information about name of task */
    protected static String TASK_NAME_FILE                = "task";

    /** Extension of file with information about name of task */
    protected static String TASK_EXTENSION                = ".seca";

    /** Name of file with information about task parameters */
    protected static String file_name;

    /** Name of task */
    protected String task_name;

    /** Name of file with information about parameters of phases */
    protected static String phases_file_name;

    /** Name of input file for the method of creation of file
     * with information about initial conditions */
    protected static String init_cond_input_file;

    /** Name of input file for the method of creation of file
     * with information about initial conditions */
    protected static String init_params_file;

    /** Name of input file for the method of creation of file
     * with information about boundary conditions */
    protected static String bound_params_file;

    /** Name of input file with information about parameters of tanks
     */
    protected static String tanks_file;

    /** Name of file with information about parameters of boundary grains */
    protected static String bound_grains_file_name;

    /** Name of file with information about parameters of layers */
    protected static String layers_file_name;

    /** Name of file with information about initial conditions */
    protected String init_cond_file;

    /** Name of file with information about initial geometry of specimen */
    protected String init_cond_file_geometry;

    /** Name of file with information about initial grain structure of specimen */
    protected String grain_struct_file;

    /** Name of file with information about heat conductivities */
    protected String heat_conduct_file;

    /** Name of file with information about heat capacities */
    protected String heat_capacity_file;

    /** Name of file with information about boundary conditions */
    protected String bound_cond_file;

    /** Name of file with information about geometry of boundary cells */
    protected String bound_cond_file_geometry;

    /** Name of file containing parameters of each cluster of cellular automata (CA):
     * index, material, 3 Euler angles, dislocation density and type
     */
    protected String grains_file;

    /** Name of file containing parameters of each cluster of cellular automata (CA):
     * index, material, 3 Euler angles, dislocation density and type
     */
    protected String grains_file_2;

    /** Name of file containing information about colours of each cluster of cellular automata (CA)
     */
    protected String grain_colours_file;

    /** Name of file containing information about colours of each cluster of cellular automata (CA)
     */
    protected String grain_colours_file_2;

    /** Name of file with minimal and maximal values of temperature and stress
     * for initial specimen including outer boundary cells
     */
    protected String extreme_values_file;

    /** Name of file containing parameters of specimen */
    protected String specimen_file;

    /** Total number of cells in a specimen */
    protected int cellNumber;

    /** Number of CA along axis X */
    protected int cell_number_I;

    /** Number of CA along axis Y */
    protected int cell_number_J;

    /** Number of CA along axis Z */
    protected int cell_number_K;

    /** Time step */
    protected double time_step;

    /** Size of specimen along axis X */
    protected double specimen_size_X;

    /** Size of specimen along axis Y */
    protected double specimen_size_Y;

    /** Size of specimen along axis Z */
    protected double specimen_size_Z;
    
    /** minimal and maximal values of cell coordinates */
    protected double min_coord_X;
    protected double max_coord_X;
    protected double min_coord_Y;
    protected double max_coord_Y;
    protected double min_coord_Z;
    protected double max_coord_Z;

    /** Cell size */
    protected double cell_size;

    /** Bulk material of specimen */
    protected String bulk_material;

    /** Number of finite elements in direction of X-axis */
    protected int element_number_X;

    /** Number of finite elements in direction of Y-axis */
    protected int element_number_Y;

    /** Number of finite elements in direction of Z-axis */
    protected int element_number_Z;

    /** Volume fraction of particles in specimen simulated */
    protected double particles_volume_fraction;

    /** Radius of particle in specimen simulated */
    protected double particle_radius;

    /** Minimal number of neighbour cells in adjacent grain
     * necessary for transition of "central" cell to adjacent grain
     */
    protected int min_neighbours_number;

    /** Temperature on top boundary of specimen */
    protected double top_boundary_temperature;

    /** Temperature on bottom boundary of specimen */
    protected double bottom_boundary_temperature;

    // Thickness of surface layer
    protected double surf_thickness;

    // Number of grains in specimen
    protected int grain_number;

    // Heat conductivity of cell in bulk
    protected final Double bulkHeatConduct  =  401.0;

    // Heat capacity of cell in bulk
    protected final Double bulkHeatCapacity =  390.0;

    // Density of cell in bulk
    protected final Double bulkDensity      = 8940.0;

    // Heat expansion coefficient in bulk
    protected final Double bulkHeatExpCoeff = 2.0E-5;

    // Yield strain in bulk
    protected final Double bulkYieldStrain  = 0.01;

    // Modulus of elasticity in bulk
    protected final Double bulkModElast     = 1.2E11;

    // Heat conductivity of cell in intermediate layer
    protected final Double layerHeatConduct =  134.0;

    // Heat capacity of cell in intermediate layer
    protected final Double layerHeatCapacity =  264.0;

    // Density of cell in intermediate layer
    protected final Double layerDensity  = 10220.0;

    // Heat expansion coefficient in intermediate layer
    protected final Double layerHeatExpCoeff  = 5.45E-6;

    // Ultimate strain in intermediate layer
    protected final Double layerYieldStrain  = 0.033;

    // Modulus of elasticity in intermediate layer
    protected final Double layerModElast     = 3.0E11;

    // Heat conductivity of cell in surface layer
    protected final Double surfHeatConduct  =   40.0;

    // Heat capacity of cell in surface layer
    protected final Double surfHeatCapacity = 1300.0;

    // Density of cell in surface layer
    protected final Double surfDensity  = 4000.0;

    // Heat expansion coefficient in surface layer
    protected final Double surfHeatExpCoeff  = 8.0E-6;

    // Ultimate strain in surface layer
    protected final Double surfUltimateStrain  = 0.0007;

    // Modulus of elasticity in surface layer
    protected final Double surfModElast        = 3.8E11;

    /** Number of outer grains/facets */
    protected int outer_grain_number = 6;

    /** Number of phases */
    protected byte phase_number = 6;

    /** Number of specimen layers (strings in read file of initial parameters) */
    protected byte layer_number;

    /** Number of grains in lower substrate layer */
    protected int substrate_grain_number;

    /** Radius of cell */
    protected final double cell_radius = 1.0;

    /** Variable responsible for showing of cells on grain bounds
     * as atoms "H" in "RasMol" */
    protected byte show_grain_bounds;

    /** Type of interaction of boundary cells with neighbours */
    protected byte bound_interaction_type;
    
    /** Type of functional dependence of boundary cell parameter on its coordinates
     */
    protected int bound_function_type;
    
    /** Type of functional dependence of boundary cell parameter on time */
    protected byte bound_time_function_type;
    
    /** Portion of total time period when boundary cells are loaded */
    protected double bound_load_time_portion;
    
    /** The returned value is true if anisotropy of specimen is simulated. */
    protected boolean anisotropy;
       
    /** The value of the specimen anisotropy coefficient */
    protected double spec_anis_coeff;
    
    /** The coordinates of the specimen anisotropy vector */
    protected double anis_vector_X;
    protected double anis_vector_Y;
    protected double anis_vector_Z;
       
    /** The value of outer anisotropy coefficient */
    protected double outer_anis_coeff;
    
    /** The coordinates of outer anisotropy vector */
    protected double outer_anis_vector_X;
    protected double outer_anis_vector_Y;
    protected double outer_anis_vector_Z;
    
   /** If "show_grain_bounds" possesses this value
    * then grain bounds are shown in "RasMol". */
    protected final byte SHOW_GRAIN_BOUNDS      = 1;

    /** If "location_in_grain" possesses this value
    * then cell is located on grain boundary. */
    protected final byte CELL_ON_GRAIN_BOUNDARY = 1;
    
    // Variable responsible for creation of "shevron" tank
    protected final boolean SHEVRON_TASK = true; // false; // 
    
    // Variable responsible for type of determination of initial temperatures of cells
    protected final boolean LAYER_TEMPERATURES = false; // true; // 
    
    /** Field is intended for write all completed steps in progress frame */
    public TextArea completed_steps;

    /** Type of packing of cellular automata */
    protected byte packing_type;

    /** Variable for choice of type of boundary conditions */
    protected byte bound_type;

    /** The constructor creates files with information about
     * initial and boundary conditions */
    public FileCreationHCP(TextArea _completed_steps, boolean determine_grain_angles)
    {
        completed_steps = _completed_steps;

        // Number of cracks in surface layer
        int crack_number = 1;//3;

        // Name of file with short information about task for showing of results
        String task_info_file = Common.NOT_AVAILABLE;
        
        boolean parallelepiped_tanks    = false; // true; // 
        boolean vertical_ellyptic_tanks = false; // true; // 
        boolean horizon_triangle_tanks  = false; // true; // 
        boolean horizon_circle_tanks    = true;  // false;// 
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(TASK_PATH+TASK_NAME_FILE+TASK_EXTENSION));

            // Name of task
            task_name = br.readLine();

            file_name = TASK_PATH+task_name+TASK_EXTENSION;

            task_info_file = TASK_PATH+task_name+"/"+task_name+TASK_EXTENSION;
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError:"+io_exc);
        }

        readTask(file_name);
        createSpecimenFile(specimen_file, task_info_file);

        boolean no_clrs_file_error = copyGrainColoursFile();
        boolean no_grns_file_error = copyGrainsFile();
        
     // createInitCondFile3D();
     // createInitCondChessBoardFile2();
     // createInitCondChessBoardFile1();
     // createInitCondChessBoardFile();
     // createInitCondStripesFile();
     // createInitCondPyramidesFile();

        // Number of phases
     // phase_number = 5;

     
      // Basic method for creation of file of initial conditions
      // createInitCondFile3D(init_cond_input_file, init_params_file);
      
      // Method for creation of file of initial conditions 
      //  where tanks are taken into account
        try
        {
            BufferedReader br_tanks = new BufferedReader(new FileReader(tanks_file));
            
            Properties props = new Properties();
            props.load(br_tanks);
            String geom_type_of_tank = props.getProperty(UICommon.GEOMETRICAL_TYPE_OF_TANKS);
            
            parallelepiped_tanks    = geom_type_of_tank.equals(UICommon.parallelepiped_tank);
            vertical_ellyptic_tanks = geom_type_of_tank.equals(UICommon.vertical_ellyptic_tank);
            horizon_triangle_tanks  = geom_type_of_tank.equals(UICommon.horizon_triangle_tank);
            horizon_circle_tanks    = geom_type_of_tank.equals(UICommon.horizon_circle_tank);
            
            System.out.println("==================--------------------++++++++++++++++++++++++++-------------------=======================");
            
            if(parallelepiped_tanks | vertical_ellyptic_tanks | horizon_triangle_tanks | horizon_circle_tanks)
                System.out.println("Type of tanks: geom_type_of_tank = "+geom_type_of_tank);
            else
                System.out.println("ERROR!!! Type of tanks is not chosen! geom_type_of_tank = "+geom_type_of_tank);
            
            System.out.println("==================--------------------++++++++++++++++++++++++++-------------------=======================");
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
        }
        
        if(parallelepiped_tanks)
            createInitCondTanksFile3D(init_cond_input_file, init_params_file, tanks_file);
        
        if(vertical_ellyptic_tanks)
            createInitCondVerticalEllypticTanksFile3D(init_cond_input_file, init_params_file, tanks_file);
        
        if(horizon_triangle_tanks)
            createInitCondHorizonTriangleTanksFile3D(init_cond_input_file, init_params_file, tanks_file);
        
        if(horizon_circle_tanks)
            createInitCondHorizonCircleTanksFile3D(init_cond_input_file, init_params_file, tanks_file);
        
     // createInitCondCracksTensionXFile3D(init_cond_input_file, init_params_file, crack_number);

     // createInitCondCracksTensionYFile3D(init_cond_input_file, init_params_file, crack_number);

     // createBoundCondFile3D(bound_cond_file);
     // createBoundCondFile3D_2();
     // createBoundCondFile();

      /* Block 24 */

      if(bound_type==0)
      {
          /* Basic method for creation of file of boundary conditions */
      //    createBoundCondFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
        //                      cell_number_I, cell_number_J, cell_number_K);

          /* Method for creation of file of boundary conditions including parallelepiped tanks */
          //   createBoundCondTanksFile(bound_params_file, tanks_file, bound_cond_file, bound_cond_file_geometry,
         //                          cell_number_I, cell_number_J, cell_number_K);
          
          createVarBoundCondTanksFile(bound_params_file, tanks_file, bound_cond_file, bound_cond_file_geometry,
                                      cell_number_I, cell_number_J, cell_number_K);
      }

      if(bound_type==1)
       /* Type of boundary conditions: "Indentor" */
       createBoundCondCircleFile3D();

      if(bound_type==2)
       /* Type of boundary conditions: "Hot circle" */
       createBoundCondHotCircleFile3D();

      if(bound_type==3)
       /* Type of boundary conditions: "Bending" */
       createBoundCondBendingFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
                                  cell_number_I, cell_number_J, cell_number_K);

      if(bound_type==4)
       /* Type of boundary conditions: "Shear" */
       createBoundCondShearXYFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
                                  cell_number_I, cell_number_J, cell_number_K);




   //   createBoundCondShearXFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
     //                         cell_number_I, cell_number_J, cell_number_K);

   //     createBoundCondBendingFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
     //                      cell_number_I, cell_number_J, cell_number_K);

     //  createBoundCondBendingXYFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
     //                               cell_number_I, cell_number_J, cell_number_K);

  //    createBoundCondBottomHalfTensionXFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
  //                     cell_number_I, cell_number_J, cell_number_K);


    //    createBoundCondBottomHalfTensionXYFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
    //                     cell_number_I, cell_number_J, cell_number_K);

    //    createBoundCondBackHalfTensionXFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
      //                     cell_number_I, cell_number_J, cell_number_K);

    //    createBoundCondCracksBackHalfTensionXFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
      //                                            cell_number_I, cell_number_J, cell_number_K, crack_number);

     //   createBoundCondCracksTensionXFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
     //                                             cell_number_I, cell_number_J, cell_number_K, crack_number);

      //  createBoundCondCracksTensionYFile(bound_params_file, bound_cond_file, bound_cond_file_geometry,
      //                                            cell_number_I, cell_number_J, cell_number_K, crack_number);

     // createInitGrainStructureFile();
     // createInitGrainStructureFile2();
      
       /* Block 25 */

        // Variable responsible for stochastic or deterministic choice of phases
        boolean stoch_phase_choice = false;

        if(layers_file_name.equals(Common.NOT_AVAILABLE))
        {
            System.out.println("ERROR!!! File with layers is not available!!!");
            stoch_phase_choice = true;
        }

        /* The method creates file with information about each grain. */
     //   createGrainsFile3D(init_cond_input_file, phases_file_name,
       //                    bound_grains_file_name, stoch_phase_choice);
        
        // Color of grains, which will become a pore
        int pore_colour = -111;//1;//

        /* The method creates file with information about each grain including tanks. */
        createGrainsAndTanksFile3D(init_cond_input_file, phases_file_name,
                                   bound_grains_file_name, tanks_file, stoch_phase_choice, pore_colour, determine_grain_angles | no_grns_file_error);
        
     // createGrainsFile();
     // createGrainsFile2();
    }

    /** The method starts creation of files.
     * @param args an array of command-line arguments.
     */
    public static void main(java.lang.String[] args)
    {
        TextArea completed_steps = new TextArea();
        
        System.out.println("START!!!");
        
        boolean determine_grain_angles = true;// false;// 
        
        FileCreationHCP fileCreation = new FileCreationHCP(completed_steps, determine_grain_angles);
        
        System.out.println("FINISH!!!");
    }

    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     */
    public void createInitCondFile3D()
    {
        // Initial heat energy of cell
        double init_heat_energy = 0;

        // Initial mechanical energy of cell
        double init_mech_energy = 0;

        // Initial temperature of cell
        double init_temperature = 300.0;

        // Initial index of grain containing cell
        int init_grain_index = 0;

        // Coordinates of cell centre
        double[] cell_coordinates;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
         //   BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));

            bw.write(cellNumber+"");
            bw.newLine();

      //      bw1.write(cellNumber+"");
       //     bw1.newLine();

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                cell_coordinates = calcCoordinates(i_index, j_index, k_index);

                // 1st layer
              if(k_index < surf_thickness)
            //    if(cell_coordinates[2] < surf_thickness)
                {
                    init_heat_energy = 0;
                    init_mech_energy = 0;
                    init_temperature = 300.0;
                    init_grain_index = 1;
                }

                // 2nd layer
             if((k_index >= surf_thickness)&(k_index < 2*surf_thickness+1))
            //        if(cell_coordinates[2] >= surf_thickness)
           //   if(i_index >= surfThickness)
                {
                    init_heat_energy = 0;
                    init_mech_energy = 0;
                    init_temperature = 300.0;
                    init_grain_index = 2;
                }

                // Substrate
                /*
                if(k_index > 2*surf_thickness)
                {
                    init_heat_energy = 0;
                    init_mech_energy = 0;
                    init_temperature = 300.0;
                    init_grain_index = 2;
                }
                */
                bw.write(init_heat_energy+" "+init_mech_energy+" "+
                         init_temperature+" "+init_grain_index);
                bw.newLine();
                bw.flush();

          //      bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
           //             /* init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+ */ init_grain_index);
            //    bw1.newLine();
            //    bw1.flush();
            }
            bw.close();
         //   bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
      //      completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     * @param read_file_name file with information about initial grain geometry
     * @param init_params_file file with information about initial parameters
     */
    private void createInitCondFile3D(String read_file_name, String init_params_file)
    {
        BufferedReader br;
        StringTokenizer st;
        String string;
        
        // Initial heat energy of cell
        double[] init_heat_energies = new double[phase_number];
        
        // Initial mechanical energy of cell
        double[] init_mech_energies = new double[phase_number];
        
        // Initial temperature of cell
        double[] init_temperatures  = new double[phase_number];
        
        // Counter of specimen layers (strings in read file of initial parameters)
        byte layer_counter = 0;
        
        // Counter of strings in read file of initial structure
        byte string_counter = 0;
        
        // Index of grain
        int grain_index = -1;
        
        // True if process in layer structure is simulated
        boolean layer_structure = false;

        // Initial parameters of boundary cells
        double  init_heat_energy=0,
                init_mech_energy=0,
                init_temperature=0;

        try
        {
            br = new BufferedReader(new FileReader(init_params_file));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_params_file);

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        // Initial heat energy of cell
                        init_heat_energies[layer_counter] = (new Double(string)).doubleValue();

                        // Initial mechanical energy of cell
                        init_mech_energies[layer_counter] = (new Double(st.nextToken())).doubleValue();

                        // Initial temperature of cell
                        init_temperatures[layer_counter]  = (new Double(st.nextToken())).doubleValue();

                        layer_counter++;
                    }
                }
            }
            
            layer_number = layer_counter;
            layer_counter = 0;
       
            if(layer_number>1)
                layer_structure = true;

            br.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }

        // Coordinates of cell centre
        double[] cell_coordinates;
        
        boolean read_strings = true;

        // Number of grains in 1st substrate layer
        int grain_number_1 = 0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
         //   BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));
            br = new BufferedReader(new FileReader(read_file_name));
        //  BufferedReader br1= new BufferedReader(new FileReader(read_file_name_1));
            
            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+read_file_name);
            
            // Reading of number of grains in specimen
            while(read_strings)
            if(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                if(br.ready())
                {
                    string = br.readLine();
                    st = new StringTokenizer(string);
                    grain_number = new Integer(st.nextToken());
                    grain_number_1 = new Integer(st.nextToken());
                    read_strings = false;
                }
            }
            
            read_strings = true;
            
       //   br1.readLine();
       //   grain_number = new Integer(br1.readLine());
            
            // Index of grain containing cell on left outer boundary (I=0)
            int left_index  = grain_number+1;
            
            // Index of grain containing cell on front outer boundary (J=0)
            int front_index = grain_number+2;
            
            // Index of grain containing cell on top outer boundary (K=0)
            int top_index = grain_number+3;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_index = grain_number+4;
            
            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_index = grain_number+5;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_index = grain_number+6;

            // Writing of comments
            bw.write("# This file contains initial parameters of each cellular automaton (CA):");
            bw.newLine();
            bw.write("# thermal energy, mechanical energy, temperature, index of cluster containing CA,");
            bw.newLine();
            bw.write("# type of location of CA in grain and specimen:");
            bw.newLine();
            bw.write("# 2 - outer boundary CA,");
            bw.newLine();
            bw.write("# 3 - intragranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 4 - intergranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 5 - intragranular CA on specimen boundary,");
            bw.newLine();
            bw.write("# 6 - intergranular CA on specimen boundary.");
            bw.newLine();
            // Writing of total number of cells
         //   bw1.write(cellNumber+"");
         //   bw1.newLine();

            boolean write_string;

            substrate_grain_number = grain_number-layer_number+2;

            for (int k_index = 0; k_index < cell_number_K+2; k_index++)
            for (int j_index = 0; j_index < cell_number_J+2; j_index++)
            for (int i_index = 0; i_index < cell_number_I+2; i_index++)
            {
                write_string = true;

                cell_coordinates = calcCoordinates(i_index, j_index, k_index);

                if(write_string)if(i_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_index);
                //    bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(i_index==cell_number_I+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              right_index);
                //    bw1.write(right_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(j_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              front_index);
               //     bw1.write(front_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(j_index==cell_number_J+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              back_index);
                //    bw1.write(back_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(k_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              top_index);
              //      bw1.write(top_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(k_index==cell_number_K+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              bottom_index);
              //      bw1.write(bottom_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)
                {
                    string_counter++;                    
                    
                    while(read_strings)
                    if(br.ready())
                    {
                        string = br.readLine();
                        st = new StringTokenizer(string);

                        if(st.hasMoreTokens())
                        {
                            string = st.nextToken();

                            if(!string.equals("#"))
                            {
                                grain_index = (new Integer(string)).intValue();

                                if(grain_index<=substrate_grain_number)
                                {
                                    if(grain_index<=grain_number_1)
                                        bw.write(init_heat_energies[layer_number-2]+" "+
                                                 init_mech_energies[layer_number-2]+" "+
                                                 init_temperatures [layer_number-2]+" "+grain_index);
                                    else
                                        bw.write(init_heat_energies[layer_number-1]+" "+
                                                 init_mech_energies[layer_number-1]+" "+
                                                 init_temperatures [layer_number-1]+" "+grain_index);
                                }
                                else
                                    bw.write(init_heat_energies[grain_index-substrate_grain_number-1]+" "+
                                             init_mech_energies[grain_index-substrate_grain_number-1]+" "+
                                             init_temperatures [grain_index-substrate_grain_number-1]+" "+grain_index);

                                if(show_grain_bounds == 1)
                                if(st.hasMoreTokens())
                                    bw.write(" "+st.nextToken());

                                read_strings = false;
                            }
                        }
                    }

                    read_strings = true;
                }
                else
                if(show_grain_bounds == 1)
                    bw .write(" "+Common.OUTER_CELL);

                bw.newLine();
                bw.flush();
         //       bw1.newLine();
         //       bw1.flush();
            }

            //TEST
       //     bw.write (string_counter+"");
       //     bw1.write(string_counter+"");

            br.close();
            bw.close();
       //     bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
       //     completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     * @param read_file_name file with information about initial grain geometry
     * @param init_params_file file with information about initial parameters
     * @param tanks_file file with information about parameters of tanks
     */
    private void createInitCondTanksFile3D(String read_file_name, String init_params_file, String tanks_file)
    {
        System.out.println();
        System.out.println("Method FileCreationHCP.createInitCondTanksFile3D(String read_file_name, String init_params_file, String tanks_file) is started!!!");
        System.out.println();
        
        BufferedReader br;
        StringTokenizer st;
        String string;

        // Initial heat energy of cell
        double[] init_heat_energies = new double[phase_number];

        // Initial mechanical energy of cell
        double[] init_mech_energies = new double[phase_number];

        // Initial temperature of cell
        double[] init_temperatures  = new double[phase_number];

        // Counter of specimen layers (strings in read file of initial parameters)
        byte layer_counter = 0;

        // Counter of strings in read file of initial structure
        byte string_counter = 0;

        // Index of grain
        int grain_index = -1;

        // True if process in layer structure is simulated
        boolean layer_structure = false;

        // Initial parameters of boundary cells
        double  init_heat_energy=0,
                init_mech_energy=0,
                init_temperature=0;

        //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
        // "mech" parameter is equal to constant rate of stress or strain change,
        // "heat" parameter is equal to constant temperature or
        //        constant influx of specific surface heat energy.
        double _left_tank_min_coord_X = 0;
        double _left_tank_max_coord_X = 0;
        double _left_tank_min_coord_Y = 0;
        double _left_tank_max_coord_Y = 0;
        double _left_tank_min_coord_Z = 0;
        double _left_tank_max_coord_Z = 0;

        double _front_tank_min_coord_X = 0;
        double _front_tank_max_coord_X = 0;
        double _front_tank_min_coord_Y = 0;
        double _front_tank_max_coord_Y = 0;
        double _front_tank_min_coord_Z = 0;
        double _front_tank_max_coord_Z = 0;

        double _top_tank_min_coord_X = 0;
        double _top_tank_max_coord_X = 0;
        double _top_tank_min_coord_Y = 0;
        double _top_tank_max_coord_Y = 0;
        double _top_tank_min_coord_Z = 0;
        double _top_tank_max_coord_Z = 0;

        double _bottom_tank_min_coord_X = 0;
        double _bottom_tank_max_coord_X = 0;
        double _bottom_tank_min_coord_Y = 0;
        double _bottom_tank_max_coord_Y = 0;
        double _bottom_tank_min_coord_Z = 0;
        double _bottom_tank_max_coord_Z = 0;

        double _back_tank_min_coord_X = 0;
        double _back_tank_max_coord_X = 0;
        double _back_tank_min_coord_Y = 0;
        double _back_tank_max_coord_Y = 0;
        double _back_tank_min_coord_Z = 0;
        double _back_tank_max_coord_Z = 0;

        double _right_tank_min_coord_X = 0;
        double _right_tank_max_coord_X = 0;
        double _right_tank_min_coord_Y = 0;
        double _right_tank_max_coord_Y = 0;
        double _right_tank_min_coord_Z = 0;
        double _right_tank_max_coord_Z = 0;

        try
        {
            br = new BufferedReader(new FileReader(init_params_file));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_params_file);

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        // Initial heat energy of cell
                        init_heat_energies[layer_counter] = (new Double(string)).doubleValue();

                        // Initial mechanical energy of cell
                        init_mech_energies[layer_counter] = (new Double(st.nextToken())).doubleValue();

                        // Initial temperature of cell
                        init_temperatures[layer_counter]  = (new Double(st.nextToken())).doubleValue();

                        layer_counter++;
                    }
                }
            }

            layer_number = layer_counter;
            layer_counter = 0;

            if(layer_number>1)
                layer_structure = true;

            br.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }

        // Coordinates of cell centre
        double[] cell_coordinates;

        boolean read_strings = true;

        // Number of grains in surface layer
        int grain_number_1 = 0;

        // Number of grains in upper intermediate layer
        int grain_number_2 = 0;

        // Number of grains in lower intermediate layer
        int grain_number_3 = 0;

        // Number of grains in upper substrate layer
        int grain_number_4 = 0;
        
        // Number of grains in lower substrate layer
        int grain_number_5 = 0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
         //   BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));
            br = new BufferedReader(new FileReader(read_file_name));
        //  BufferedReader br1= new BufferedReader(new FileReader(read_file_name_1));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+read_file_name);

            // Reading of number of grains in specimen
            while(read_strings)
            if(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                if(br.ready())
                {
                    string = br.readLine();
                    st = new StringTokenizer(string);
                    grain_number   = new Integer(st.nextToken());
                    grain_number_1 = new Integer(st.nextToken());
                    grain_number_2 = new Integer(st.nextToken());
                    grain_number_3 = new Integer(st.nextToken());
                    grain_number_4 = new Integer(st.nextToken());
                    grain_number_5 = new Integer(st.nextToken());
                    read_strings = false;
                }
            }

            // Reading of information about boundary tanks
            TextTableFileReader reader = new TextTableFileReader(tanks_file);
            StringVector table = reader.convertToTable();

            completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary tanks:    "+tanks_file);
            System.out.println("Name of read file with parameters of boundary tanks:    "+tanks_file);

            //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
            // "mech" parameter is equal to constant rate of stress or strain change,
            // "heat" parameter is equal to constant temperature or
            //        constant influx of specific surface heat energy.
            _left_tank_min_coord_X = (new Double(table.getCell(0, 1))).doubleValue();
            _left_tank_max_coord_X = (new Double(table.getCell(0, 2))).doubleValue();
            _left_tank_min_coord_Y = (new Double(table.getCell(0, 3))).doubleValue();
            _left_tank_max_coord_Y = (new Double(table.getCell(0, 4))).doubleValue();
            _left_tank_min_coord_Z = (new Double(table.getCell(0, 5))).doubleValue();
            _left_tank_max_coord_Z = (new Double(table.getCell(0, 6))).doubleValue();

            _front_tank_min_coord_X = (new Double(table.getCell(1, 1))).doubleValue();
            _front_tank_max_coord_X = (new Double(table.getCell(1, 2))).doubleValue();
            _front_tank_min_coord_Y = (new Double(table.getCell(1, 3))).doubleValue();
            _front_tank_max_coord_Y = (new Double(table.getCell(1, 4))).doubleValue();
            _front_tank_min_coord_Z = (new Double(table.getCell(1, 5))).doubleValue();
            _front_tank_max_coord_Z = (new Double(table.getCell(1, 6))).doubleValue();

            _top_tank_min_coord_X = (new Double(table.getCell(2, 1))).doubleValue();
            _top_tank_max_coord_X = (new Double(table.getCell(2, 2))).doubleValue();
            _top_tank_min_coord_Y = (new Double(table.getCell(2, 3))).doubleValue();
            _top_tank_max_coord_Y = (new Double(table.getCell(2, 4))).doubleValue();
            _top_tank_min_coord_Z = (new Double(table.getCell(2, 5))).doubleValue();
            _top_tank_max_coord_Z = (new Double(table.getCell(2, 6))).doubleValue();

            _bottom_tank_min_coord_X = (new Double(table.getCell(3, 1))).doubleValue();
            _bottom_tank_max_coord_X = (new Double(table.getCell(3, 2))).doubleValue();
            _bottom_tank_min_coord_Y = (new Double(table.getCell(3, 3))).doubleValue();
            _bottom_tank_max_coord_Y = (new Double(table.getCell(3, 4))).doubleValue();
            _bottom_tank_min_coord_Z = (new Double(table.getCell(3, 5))).doubleValue();
            _bottom_tank_max_coord_Z = (new Double(table.getCell(3, 6))).doubleValue();

            _back_tank_min_coord_X = (new Double(table.getCell(4, 1))).doubleValue();
            _back_tank_max_coord_X = (new Double(table.getCell(4, 2))).doubleValue();
            _back_tank_min_coord_Y = (new Double(table.getCell(4, 3))).doubleValue();
            _back_tank_max_coord_Y = (new Double(table.getCell(4, 4))).doubleValue();
            _back_tank_min_coord_Z = (new Double(table.getCell(4, 5))).doubleValue();
            _back_tank_max_coord_Z = (new Double(table.getCell(4, 6))).doubleValue();

            _right_tank_min_coord_X = (new Double(table.getCell(5, 1))).doubleValue();
            _right_tank_max_coord_X = (new Double(table.getCell(5, 2))).doubleValue();
            _right_tank_min_coord_Y = (new Double(table.getCell(5, 3))).doubleValue();
            _right_tank_max_coord_Y = (new Double(table.getCell(5, 4))).doubleValue();
            _right_tank_min_coord_Z = (new Double(table.getCell(5, 5))).doubleValue();
            _right_tank_max_coord_Z = (new Double(table.getCell(5, 6))).doubleValue();

            System.out.println("_top_tank_min_coord_X:    "+_top_tank_min_coord_X);
            System.out.println("_top_tank_max_coord_X:    "+_top_tank_max_coord_X);

            read_strings = true;

       //   br1.readLine();
       //   grain_number = new Integer(br1.readLine());

            // Index of grain containing cell on left outer boundary (I=0)
            int left_index  = grain_number+1;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_index = grain_number+2;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_index = grain_number+3;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_index = grain_number+4;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_index = grain_number+5;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_index = grain_number+6;

            // Index of grain containing cell on left outer boundary (I=0)
            int left_tank_index  = grain_number+7;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_tank_index = grain_number+8;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_tank_index = grain_number+9;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_tank_index = grain_number+10;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_tank_index = grain_number+11;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_tank_index = grain_number+12;

            // Writing of comments
            bw.write("# This file contains initial parameters of each cellular automaton (CA):");
            bw.newLine();
            bw.write("# thermal energy, mechanical energy, temperature, index of cluster containing CA,");
            bw.newLine();
            bw.write("# type of location of CA in grain and specimen:");
            bw.newLine();
            bw.write("# 2 - outer boundary CA,");
            bw.newLine();
            bw.write("# 3 - intragranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 4 - intergranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 5 - intragranular CA on specimen boundary,");
            bw.newLine();
            bw.write("# 6 - intergranular CA on specimen boundary.");
            bw.newLine();
            // Writing of total number of cells
         //   bw1.write(cellNumber+"");
         //   bw1.newLine();

            boolean write_string;
            boolean tank_cell;
            boolean facet_cell;
            int tank_cell_number = 0;
            
            // Variable responsible for creation of "shevron" tank
            boolean shevron_tank = SHEVRON_TASK;
            
            // Variable responsible for type of determination of initial temperatures of cells
            boolean layer_temperatures = LAYER_TEMPERATURES;
            
            substrate_grain_number = grain_number-layer_number+2;
            
            System.out.println("Cell numbers along axes: "+ (cell_number_I+2)+" "+(cell_number_J+2)+" "+(cell_number_K+2));
            
            completed_steps.setText(completed_steps.getText()+"\n _top_tank_min_coord_X = "+_top_tank_min_coord_X+"; _top_tank_max_coord_X: "+_top_tank_max_coord_X);
            
            double[] min_cell_coordinates = calcCoordinates(1, 1, 1);
            double[] max_cell_coordinates = calcCoordinates(cell_number_I, cell_number_J, cell_number_K);
            
            completed_steps.setText(completed_steps.getText()+"\n Min. coordinates: "+ min_cell_coordinates[0]+" "+min_cell_coordinates[1]+" "+min_cell_coordinates[2]);
            completed_steps.setText(completed_steps.getText()+"\n Max. coordinates: "+ max_cell_coordinates[0]+" "+max_cell_coordinates[1]+" "+max_cell_coordinates[2]);
            
            for (int k_index = 0; k_index < cell_number_K+2; k_index++)
            for (int j_index = 0; j_index < cell_number_J+2; j_index++)
            for (int i_index = 0; i_index < cell_number_I+2; i_index++)
            {
            //    System.out.println("Cell index: "+ i_index+" "+j_index+" "+k_index);
                
                write_string = true;
                tank_cell = false;
                facet_cell = false;

                cell_coordinates = calcCoordinates(i_index, j_index, k_index);
                
                // Writing of parameters of tank containing current cell
                if(write_string)
                {
                    // If current cell is in left tank
                    if(!tank_cell)
                    if((cell_coordinates[0]>=_left_tank_min_coord_X)&(cell_coordinates[0]<=_left_tank_max_coord_X))
                    if((cell_coordinates[1]>=_left_tank_min_coord_Y)&(cell_coordinates[1]<=_left_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_left_tank_min_coord_Z)&(cell_coordinates[2]<=_left_tank_max_coord_Z))
                    {
                        if(!shevron_tank)
                        {
                          // Writing of initial parameters of cell in tank at left facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                        else
                        if(cell_coordinates[2] <= _left_tank_max_coord_Z - _left_tank_max_coord_Y + cell_coordinates[1] |
                           cell_coordinates[2] <= _left_tank_max_coord_Z + _left_tank_min_coord_Y - cell_coordinates[1])
                        {
                          // Writing of initial parameters of cell in tank at left facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                    }

                    // If current cell is in front tank
                    if(!tank_cell)
                    if((cell_coordinates[0]>=_front_tank_min_coord_X)&(cell_coordinates[0]<=_front_tank_max_coord_X))
                    if((cell_coordinates[1]>=_front_tank_min_coord_Y)&(cell_coordinates[1]<=_front_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_front_tank_min_coord_Z)&(cell_coordinates[2]<=_front_tank_max_coord_Z))
                    {
                        if(!shevron_tank)
                        {
                          // Writing of initial parameters of cell in tank at front facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                        else
                        if(cell_coordinates[0] <= _front_tank_max_coord_X - _front_tank_max_coord_Z + cell_coordinates[2] |
                           cell_coordinates[0] <= _front_tank_max_coord_X + _front_tank_min_coord_Z - cell_coordinates[2])
                        {
                          // Writing of initial parameters of cell in tank at front facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                    }

                    // If current cell is in top tank
                    if(!tank_cell)
                    if((cell_coordinates[0]>=_top_tank_min_coord_X)&(cell_coordinates[0]<=_top_tank_max_coord_X))
                    if((cell_coordinates[1]>=_top_tank_min_coord_Y)&(cell_coordinates[1]<=_top_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_top_tank_min_coord_Z)&(cell_coordinates[2]<=_top_tank_max_coord_Z))
                    {
                        if(!shevron_tank)
                        {
                          // Writing of initial parameters of cell in tank at top facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                        else
                        if(cell_coordinates[1] <= _top_tank_max_coord_Y - _top_tank_max_coord_X + cell_coordinates[0] |
                           cell_coordinates[1] <= _top_tank_max_coord_Y + _top_tank_min_coord_X - cell_coordinates[0])
                        {
                          // Writing of initial parameters of cell in tank at top facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                    }

                    // If current cell is in bottom tank
                    if(!tank_cell)
                    if((cell_coordinates[0]>=_bottom_tank_min_coord_X)&(cell_coordinates[0]<=_bottom_tank_max_coord_X))
                    if((cell_coordinates[1]>=_bottom_tank_min_coord_Y)&(cell_coordinates[1]<=_bottom_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_bottom_tank_min_coord_Z)&(cell_coordinates[2]<=_bottom_tank_max_coord_Z))
                    {
                        if(!shevron_tank)
                        {
                          // Writing of initial parameters of cell in tank at bottom facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                        else
                        if(cell_coordinates[1] <= _bottom_tank_max_coord_Y - _bottom_tank_max_coord_X + cell_coordinates[0] |
                           cell_coordinates[1] <= _bottom_tank_max_coord_Y + _bottom_tank_min_coord_X - cell_coordinates[0])
                        {
                          // Writing of initial parameters of cell in tank at bottom facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                    }

                    // If current cell is in back tank
                    if(!tank_cell)
                    if((cell_coordinates[0]>=_back_tank_min_coord_X)&(cell_coordinates[0]<=_back_tank_max_coord_X))
                    if((cell_coordinates[1]>=_back_tank_min_coord_Y)&(cell_coordinates[1]<=_back_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_back_tank_min_coord_Z)&(cell_coordinates[2]<=_back_tank_max_coord_Z))
                    {
                        if(!shevron_tank)
                        {
                          // Writing of initial parameters of cell in tank at back facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                        else
                        if(cell_coordinates[0] <= _back_tank_max_coord_X - _back_tank_max_coord_Z + cell_coordinates[2] |
                           cell_coordinates[0] <= _back_tank_max_coord_X + _back_tank_min_coord_Z - cell_coordinates[2])
                        {
                          // Writing of initial parameters of cell in tank at back facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                    }

                    // If current cell is in right tank
                    if(!tank_cell)
                    if((cell_coordinates[0]>=_right_tank_min_coord_X)&(cell_coordinates[0]<=_right_tank_max_coord_X))
                    if((cell_coordinates[1]>=_right_tank_min_coord_Y)&(cell_coordinates[1]<=_right_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_right_tank_min_coord_Z)&(cell_coordinates[2]<=_right_tank_max_coord_Z))
                    {
                        if(!shevron_tank)
                        {
                          // Writing of initial parameters of cell in tank at right facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                        else
                        if(cell_coordinates[2] <= _right_tank_max_coord_Z - _right_tank_max_coord_Y + cell_coordinates[1] |
                           cell_coordinates[2] <= _right_tank_max_coord_Z + _right_tank_min_coord_Y - cell_coordinates[1])
                        {
                          // Writing of initial parameters of cell in tank at right facet
                          bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_tank_index);
                          write_string = false;
                          tank_cell = true;
                          tank_cell_number++;
                        }
                    }
               //     System.out.println("DDD");
                }

                if(tank_cell)
                if(tank_cell_number%100 == 0)
                    completed_steps.setText(completed_steps.getText()+"\nTank cell # "+tank_cell_number+" coordinates: "+
                                      cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                
                if(i_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_index);
                     // bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }
                
                if(i_index==cell_number_I+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_index);
                     // bw1.write(right_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_index);
                     // bw1.write(front_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==cell_number_J+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_index);
                     // bw1.write(back_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_index);
                     // bw1.write(top_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==cell_number_K+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_index);
                     // bw1.write(bottom_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

          //      System.out.println("EEE");

                if(!facet_cell)
                {
                  //  string_counter++;
                  //  read_strings = true;

                 //   System.out.println("GGG");
                    
           //         System.out.println("br_ready = "+br.ready());
               //     System.out.println("read_strings = "+read_strings);

                    if(br.ready())
                    while(read_strings)                    
                    {
                     //   System.out.println("HHH");

                        string = br.readLine();
                        st = new StringTokenizer(string);

                        if(!tank_cell)
                        if(st.hasMoreTokens())
                        {
                            string = st.nextToken();

                            if(!string.equals("#"))
                            {
                                grain_index = (new Integer(string)).intValue();

                                if(grain_index<=grain_number_1)
                                    layer_counter = 0;

                                if((grain_index>grain_number_1)&
                                   (grain_index<=grain_number_1+grain_number_2))
                                    layer_counter = 1;

                                if((grain_index>grain_number_1+grain_number_2)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3))
                                    layer_counter = 2;

                                if((grain_index>grain_number_1+grain_number_2+grain_number_3)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3+grain_number_4))
                                    layer_counter = 3;

                                if(grain_index>grain_number_1+grain_number_2+grain_number_3+grain_number_4)
                                    layer_counter = 4;
                                
                                if(layer_temperatures)
                                {
                                    // Each layer has its own initial temperature.
                                    init_temperature = init_temperatures [layer_counter];
                                }
                                else
                                {   
                                    // Initial temperatures of cells are changed uniformly from top to bottom.
                                    if(max_cell_coordinates[2] != min_cell_coordinates[2])
                                      init_temperature = init_temperatures[0] + (init_temperatures[4] - init_temperatures[0])*(cell_coordinates[2] - min_cell_coordinates[2])/
                                                                                                                              (max_cell_coordinates[2] - min_cell_coordinates[2]);
                                    else
                                      init_temperature = init_temperatures [layer_counter];
                                }
                                
                                bw.write(init_heat_energies[layer_counter]+" "+init_mech_energies[layer_counter]+" "+init_temperature+" "+grain_index);

                                if(show_grain_bounds == 1)
                                {
                                  if(st.hasMoreTokens())
                                    bw.write(" "+st.nextToken());
                                  else
                                    bw.write(" "+Common.INTRAGRANULAR_CELL);
                                }

                                read_strings = false;

                            //    System.out.println("AAA");
                            }
                         //   System.out.println("BBB");
                        }

                   //     System.out.println("CCC");
                        if(tank_cell)
                            read_strings = false;
                    }

                    read_strings = true;
               //     System.out.println("FFF");
                }

                if(tank_cell | facet_cell)
                if(show_grain_bounds == 1)
                    bw .write(" "+Common.OUTER_CELL);

                bw.newLine();
                bw.flush();
         //       bw1.newLine();
         //       bw1.flush();
            }
            //TEST
       //     bw.write (string_counter+"");
       //     bw1.write(string_counter+"");

            br.close();
            bw.close();
       //     bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
       //     completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    } 
 
    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     * @param read_file_name file with information about initial grain geometry
     * @param init_params_file file with information about initial parameters
     * @param tanks_file file with information about parameters of cylinder tanks
     */
    private void createInitCondVerticalEllypticTanksFile3D(String read_file_name, String init_params_file, String tanks_file)
    {
        BufferedReader br;
        StringTokenizer st;
        String string;

        // Initial heat energy of cell
        double[] init_heat_energies = new double[phase_number];

        // Initial mechanical energy of cell
        double[] init_mech_energies = new double[phase_number];

        // Initial temperature of cell
        double[] init_temperatures  = new double[phase_number];

        // Counter of specimen layers (strings in read file of initial parameters)
        byte layer_counter = 0;

        // Counter of strings in read file of initial structure
        byte string_counter = 0;

        // Index of grain
        int grain_index = -1;

        // True if process in layer structure is simulated
        boolean layer_structure = false;

        // Initial parameters of boundary cells
        double  init_heat_energy=0,
                init_mech_energy=0,
                init_temperature=0;

        //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
        // "mech" parameter is equal to constant rate of stress or strain change,
        // "heat" parameter is equal to constant temperature or
        //        constant influx of specific surface heat energy.
        double _left_tank_min_coord_X = 0;
        double _left_tank_max_coord_X = 0;
        double _left_tank_min_coord_Y = 0;
        double _left_tank_max_coord_Y = 0;
        double _left_tank_min_coord_Z = 0;
        double _left_tank_max_coord_Z = 0;

        double _front_tank_min_coord_X = 0;
        double _front_tank_max_coord_X = 0;
        double _front_tank_min_coord_Y = 0;
        double _front_tank_max_coord_Y = 0;
        double _front_tank_min_coord_Z = 0;
        double _front_tank_max_coord_Z = 0;

        double _top_tank_min_coord_X = 0;
        double _top_tank_max_coord_X = 0;
        double _top_tank_min_coord_Y = 0;
        double _top_tank_max_coord_Y = 0;
        double _top_tank_min_coord_Z = 0;
        double _top_tank_max_coord_Z = 0;

        double _bottom_tank_min_coord_X = 0;
        double _bottom_tank_max_coord_X = 0;
        double _bottom_tank_min_coord_Y = 0;
        double _bottom_tank_max_coord_Y = 0;
        double _bottom_tank_min_coord_Z = 0;
        double _bottom_tank_max_coord_Z = 0;

        double _back_tank_min_coord_X = 0;
        double _back_tank_max_coord_X = 0;
        double _back_tank_min_coord_Y = 0;
        double _back_tank_max_coord_Y = 0;
        double _back_tank_min_coord_Z = 0;
        double _back_tank_max_coord_Z = 0;

        double _right_tank_min_coord_X = 0;
        double _right_tank_max_coord_X = 0;
        double _right_tank_min_coord_Y = 0;
        double _right_tank_max_coord_Y = 0;
        double _right_tank_min_coord_Z = 0;
        double _right_tank_max_coord_Z = 0;

        try
        {
            br = new BufferedReader(new FileReader(init_params_file));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_params_file);

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        // Initial heat energy of cell
                        init_heat_energies[layer_counter] = (new Double(string)).doubleValue();

                        // Initial mechanical energy of cell
                        init_mech_energies[layer_counter] = (new Double(st.nextToken())).doubleValue();

                        // Initial temperature of cell
                        init_temperatures[layer_counter]  = (new Double(st.nextToken())).doubleValue();

                        layer_counter++;
                    }
                }
            }

            layer_number = layer_counter;
            layer_counter = 0;

            if(layer_number>1)
                layer_structure = true;

            br.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }

        // Coordinates of cell centre
        double[] cell_coordinates;

        boolean read_strings = true;

        // Number of grains in surface layer
        int grain_number_1 = 0;

        // Number of grains in upper intermediate layer
        int grain_number_2 = 0;

        // Number of grains in lower intermediate layer
        int grain_number_3 = 0;

        // Number of grains in upper substrate layer
        int grain_number_4 = 0;
        
        // Number of grains in lower substrate layer
        int grain_number_5 = 0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
         //   BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));
            br = new BufferedReader(new FileReader(read_file_name));
        //  BufferedReader br1= new BufferedReader(new FileReader(read_file_name_1));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+read_file_name);

            // Reading of number of grains in specimen
            while(read_strings)
            if(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                if(br.ready())
                {
                    string = br.readLine();
                    st = new StringTokenizer(string);
                    grain_number   = new Integer(st.nextToken());
                    grain_number_1 = new Integer(st.nextToken());
                    grain_number_2 = new Integer(st.nextToken());
                    grain_number_3 = new Integer(st.nextToken());
                    grain_number_4 = new Integer(st.nextToken());
                    grain_number_5 = new Integer(st.nextToken());
                    read_strings = false;
                }
            }

            // Reading of information about boundary tanks
            TextTableFileReader reader = new TextTableFileReader(tanks_file);
            StringVector table = reader.convertToTable();

            completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary cylinder tanks:    "+tanks_file);
            System.out.println("Name of read file with parameters of boundary cylinder tanks:    "+tanks_file);

            //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
            // "mech" parameter is equal to constant rate of stress or strain change,
            // "heat" parameter is equal to constant temperature or
            //        constant influx of specific surface heat energy.
            _left_tank_min_coord_X = (new Double(table.getCell(0, 1))).doubleValue();
            _left_tank_max_coord_X = (new Double(table.getCell(0, 2))).doubleValue();
            _left_tank_min_coord_Y = (new Double(table.getCell(0, 3))).doubleValue();
            _left_tank_max_coord_Y = (new Double(table.getCell(0, 4))).doubleValue();
            _left_tank_min_coord_Z = (new Double(table.getCell(0, 5))).doubleValue();
            _left_tank_max_coord_Z = (new Double(table.getCell(0, 6))).doubleValue();

            _front_tank_min_coord_X = (new Double(table.getCell(1, 1))).doubleValue();
            _front_tank_max_coord_X = (new Double(table.getCell(1, 2))).doubleValue();
            _front_tank_min_coord_Y = (new Double(table.getCell(1, 3))).doubleValue();
            _front_tank_max_coord_Y = (new Double(table.getCell(1, 4))).doubleValue();
            _front_tank_min_coord_Z = (new Double(table.getCell(1, 5))).doubleValue();
            _front_tank_max_coord_Z = (new Double(table.getCell(1, 6))).doubleValue();

            _top_tank_min_coord_X = (new Double(table.getCell(2, 1))).doubleValue();
            _top_tank_max_coord_X = (new Double(table.getCell(2, 2))).doubleValue();
            _top_tank_min_coord_Y = (new Double(table.getCell(2, 3))).doubleValue();
            _top_tank_max_coord_Y = (new Double(table.getCell(2, 4))).doubleValue();
            _top_tank_min_coord_Z = (new Double(table.getCell(2, 5))).doubleValue();
            _top_tank_max_coord_Z = (new Double(table.getCell(2, 6))).doubleValue();

            _bottom_tank_min_coord_X = (new Double(table.getCell(3, 1))).doubleValue();
            _bottom_tank_max_coord_X = (new Double(table.getCell(3, 2))).doubleValue();
            _bottom_tank_min_coord_Y = (new Double(table.getCell(3, 3))).doubleValue();
            _bottom_tank_max_coord_Y = (new Double(table.getCell(3, 4))).doubleValue();
            _bottom_tank_min_coord_Z = (new Double(table.getCell(3, 5))).doubleValue();
            _bottom_tank_max_coord_Z = (new Double(table.getCell(3, 6))).doubleValue();

            _back_tank_min_coord_X = (new Double(table.getCell(4, 1))).doubleValue();
            _back_tank_max_coord_X = (new Double(table.getCell(4, 2))).doubleValue();
            _back_tank_min_coord_Y = (new Double(table.getCell(4, 3))).doubleValue();
            _back_tank_max_coord_Y = (new Double(table.getCell(4, 4))).doubleValue();
            _back_tank_min_coord_Z = (new Double(table.getCell(4, 5))).doubleValue();
            _back_tank_max_coord_Z = (new Double(table.getCell(4, 6))).doubleValue();

            _right_tank_min_coord_X = (new Double(table.getCell(5, 1))).doubleValue();
            _right_tank_max_coord_X = (new Double(table.getCell(5, 2))).doubleValue();
            _right_tank_min_coord_Y = (new Double(table.getCell(5, 3))).doubleValue();
            _right_tank_max_coord_Y = (new Double(table.getCell(5, 4))).doubleValue();
            _right_tank_min_coord_Z = (new Double(table.getCell(5, 5))).doubleValue();
            _right_tank_max_coord_Z = (new Double(table.getCell(5, 6))).doubleValue();

            System.out.println("_top_tank_min_coord_X:    "+_top_tank_min_coord_X);
            System.out.println("_top_tank_max_coord_X:    "+_top_tank_max_coord_X);

            read_strings = true;

       //   br1.readLine();
       //   grain_number = new Integer(br1.readLine());

            // Index of grain containing cell on left outer boundary (I=0)
            int left_index  = grain_number+1;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_index = grain_number+2;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_index = grain_number+3;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_index = grain_number+4;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_index = grain_number+5;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_index = grain_number+6;

            // Index of grain containing cell on left outer boundary (I=0)
            int left_tank_index  = grain_number+7;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_tank_index = grain_number+8;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_tank_index = grain_number+9;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_tank_index = grain_number+10;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_tank_index = grain_number+11;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_tank_index = grain_number+12;

            // Writing of comments
            bw.write("# This file contains initial parameters of each cellular automaton (CA):");
            bw.newLine();
            bw.write("# thermal energy, mechanical energy, temperature, index of cluster containing CA,");
            bw.newLine();
            bw.write("# type of location of CA in grain and specimen:");
            bw.newLine();
            bw.write("# 2 - outer boundary CA,");
            bw.newLine();
            bw.write("# 3 - intragranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 4 - intergranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 5 - intragranular CA on specimen boundary,");
            bw.newLine();
            bw.write("# 6 - intergranular CA on specimen boundary.");
            bw.newLine();
            // Writing of total number of cells
         //   bw1.write(cellNumber+"");
         //   bw1.newLine();

            boolean write_string;
            boolean tank_cell;
            boolean facet_cell;
            int tank_cell_number = 0;

            substrate_grain_number = grain_number-layer_number+2;
            
            System.out.println("Cell numbers along axes: "+ (cell_number_I+2)+" "+(cell_number_J+2)+" "+(cell_number_K+2));

            completed_steps.setText(completed_steps.getText()+"\n _top_tank_min_coord_X = "+_top_tank_min_coord_X+"; _top_tank_max_coord_X: "+_top_tank_max_coord_X);
            
            cell_coordinates = calcCoordinates(cell_number_I+1, cell_number_J+1, cell_number_K+1);
            
            completed_steps.setText(completed_steps.getText()+"\n Max. coordinates: "+ cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
            
            // Centres of cylinder tanks
            double left_cyl_tank_centre_coord_Y  = (_left_tank_min_coord_Y + _left_tank_max_coord_Y)/2;
            double left_cyl_tank_centre_coord_Z  = (_left_tank_min_coord_Z + _left_tank_max_coord_Z)/2;
            
            double right_cyl_tank_centre_coord_Y = (_right_tank_min_coord_Y + _right_tank_max_coord_Y)/2;
            double right_cyl_tank_centre_coord_Z = (_right_tank_min_coord_Z + _right_tank_max_coord_Z)/2;
            
            double front_cyl_tank_centre_coord_X = (_front_tank_min_coord_X + _front_tank_max_coord_X)/2;
            double front_cyl_tank_centre_coord_Z = (_front_tank_min_coord_Z + _front_tank_max_coord_Z)/2;
            
            double back_cyl_tank_centre_coord_X  = (_back_tank_min_coord_X + _back_tank_max_coord_X)/2;
            double back_cyl_tank_centre_coord_Z  = (_back_tank_min_coord_Z + _back_tank_max_coord_Z)/2;
                        
            double top_cyl_tank_centre_coord_X   = (_top_tank_min_coord_X + _top_tank_max_coord_X)/2;
            double top_cyl_tank_centre_coord_Y   = (_top_tank_min_coord_Y + _top_tank_max_coord_Y)/2;
            
            double bottom_cyl_tank_centre_coord_X= (_bottom_tank_min_coord_X + _bottom_tank_max_coord_X)/2;
            double bottom_cyl_tank_centre_coord_Y= (_bottom_tank_min_coord_Y + _bottom_tank_max_coord_Y)/2;
            
            // Radiuses of cylinder tanks
            double left_cyl_tank_radius_Y  = (_left_tank_max_coord_Y - _left_tank_min_coord_Y)/2;
            double left_cyl_tank_radius_Z  = (_left_tank_max_coord_Z - _left_tank_min_coord_Z)/2;
            
            double right_cyl_tank_radius_Y = (_right_tank_max_coord_Y - _right_tank_min_coord_Y)/2;
            double right_cyl_tank_radius_Z = (_right_tank_max_coord_Z - _right_tank_min_coord_Z)/2;
            
            double front_cyl_tank_radius_X = (_front_tank_max_coord_X - _front_tank_min_coord_X)/2;
            double front_cyl_tank_radius_Z = (_front_tank_max_coord_Z - _front_tank_min_coord_Z)/2;
            
            double back_cyl_tank_radius_X  = (_back_tank_max_coord_X - _back_tank_min_coord_X)/2;
            double back_cyl_tank_radius_Z  = (_back_tank_max_coord_Z - _back_tank_min_coord_Z)/2;
            
            double top_cyl_tank_radius_X   = (_top_tank_max_coord_X - _top_tank_min_coord_X)/2;
            double top_cyl_tank_radius_Y   = (_top_tank_max_coord_Y - _top_tank_min_coord_Y)/2;
            
            double bottom_cyl_tank_radius_X= (_bottom_tank_max_coord_X - _bottom_tank_min_coord_X)/2;
            double bottom_cyl_tank_radius_Y= (_bottom_tank_max_coord_Y - _bottom_tank_min_coord_Y)/2;
            
            // Relative coordinates (for example, at top facet: the coordinate X = 0 for point with coordinate X of center, 
            //                                                  the coordinate X = 1 for point with coordinate Y of center)
            double rel_coord_X = 0;
            double rel_coord_Y = 0;
            double rel_coord_Z = 0;
            
            // Determination of cells contained in each cylinder tank
            for (int k_index = 0; k_index < cell_number_K+2; k_index++)
            for (int j_index = 0; j_index < cell_number_J+2; j_index++)
            for (int i_index = 0; i_index < cell_number_I+2; i_index++)
            {
            //  System.out.println("Cell index: "+ i_index+" "+j_index+" "+k_index);
                write_string = true;
                tank_cell = false;
                facet_cell = false;

                cell_coordinates = calcCoordinates(i_index, j_index, k_index);
                
                // Writing of parameters of tank containing current cell
                if(write_string)
                {        
                    // If current cell is in left tank
                    if(!tank_cell)
                    if(cell_coordinates[0]>=_left_tank_min_coord_X & cell_coordinates[0]<=_left_tank_max_coord_X)
                    if(left_cyl_tank_radius_Y != 0  &  left_cyl_tank_radius_Z != 0)
                    {
                        rel_coord_Y = (cell_coordinates[1] - left_cyl_tank_centre_coord_Y)/left_cyl_tank_radius_Y;
                        rel_coord_Z = (cell_coordinates[2] - left_cyl_tank_centre_coord_Z)/left_cyl_tank_radius_Z;
                        
                        if(rel_coord_Y*rel_coord_Y + rel_coord_Z*rel_coord_Z <= 1)
                        {
                            // Writing of initial parameters of cell in tank at left facet
                            bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_tank_index);
                            write_string = false;
                            tank_cell = true;
                            tank_cell_number++;
                        }
                    }
                    
                    // If current cell is in front tank                    
                    if(!tank_cell)
                    if(cell_coordinates[1]>=_front_tank_min_coord_Y & cell_coordinates[1]<=_front_tank_max_coord_Y)
                    if(front_cyl_tank_radius_X != 0  &  front_cyl_tank_radius_Z != 0)
                    {
                        rel_coord_X = (cell_coordinates[0] - front_cyl_tank_centre_coord_X)/front_cyl_tank_radius_X;
                        rel_coord_Z = (cell_coordinates[2] - front_cyl_tank_centre_coord_Z)/front_cyl_tank_radius_Z;
                        
                        if(rel_coord_X*rel_coord_X + rel_coord_Z*rel_coord_Z <= 1)
                        {
                            // Writing of initial parameters of cell in tank at front facet
                            bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_tank_index);
                            write_string = false;
                            tank_cell = true;
                            tank_cell_number++;
                        }
                    }
                    
                    // If current cell is in top tank
                    if(!tank_cell)
                    if(cell_coordinates[2]>=_top_tank_min_coord_Z & cell_coordinates[2]<=_top_tank_max_coord_Z)
                    if(top_cyl_tank_radius_X != 0  &  top_cyl_tank_radius_Y != 0)
                    {
                        rel_coord_X = (cell_coordinates[0] - top_cyl_tank_centre_coord_X)/top_cyl_tank_radius_X;
                        rel_coord_Y = (cell_coordinates[1] - top_cyl_tank_centre_coord_Y)/top_cyl_tank_radius_Y;
                        
                        if(rel_coord_X*rel_coord_X + rel_coord_Y*rel_coord_Y <= 1)
                        {
                            // Writing of initial parameters of cell in tank at top facet
                            bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_tank_index);
                            write_string = false;
                            tank_cell    = true;
                            tank_cell_number++;
                        }
                    }
                    
                    // If current cell is in right tank
                    if(!tank_cell)
                    if(cell_coordinates[0]>=_right_tank_min_coord_X & cell_coordinates[0]<=_right_tank_max_coord_X)
                    if(right_cyl_tank_radius_Y != 0  &  right_cyl_tank_radius_Z != 0)
                    {
                        rel_coord_Y = (cell_coordinates[1] - right_cyl_tank_centre_coord_Y)/right_cyl_tank_radius_Y;
                        rel_coord_Z = (cell_coordinates[2] - right_cyl_tank_centre_coord_Z)/right_cyl_tank_radius_Z;
                        
                        if(rel_coord_Y*rel_coord_Y + rel_coord_Z*rel_coord_Z <= 1)
                        {
                            // Writing of initial parameters of cell in tank at right facet
                            bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_tank_index);
                            write_string = false;
                            tank_cell = true;
                            tank_cell_number++;
                        }
                    }
                    
                    // If current cell is in back tank                    
                    if(!tank_cell)
                    if(cell_coordinates[1]>=_back_tank_min_coord_Y & cell_coordinates[1]<=_back_tank_max_coord_Y)
                    if(back_cyl_tank_radius_X != 0  &  back_cyl_tank_radius_Z != 0)
                    {
                        rel_coord_X = (cell_coordinates[0] - back_cyl_tank_centre_coord_X)/back_cyl_tank_radius_X;
                        rel_coord_Z = (cell_coordinates[2] - back_cyl_tank_centre_coord_Z)/back_cyl_tank_radius_Z;
                        
                        if(rel_coord_X*rel_coord_X + rel_coord_Z*rel_coord_Z <= 1)
                        {
                            // Writing of initial parameters of cell in tank at back facet
                            bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_tank_index);
                            write_string = false;
                            tank_cell = true;
                            tank_cell_number++;
                        }
                    }
                    
                    // If current cell is in bottom tank
                    if(!tank_cell)
                    if(cell_coordinates[2]>=_bottom_tank_min_coord_Z & cell_coordinates[2]<=_bottom_tank_max_coord_Z)
                    if(bottom_cyl_tank_radius_X != 0  &  bottom_cyl_tank_radius_Y != 0)
                    {
                        rel_coord_X = (cell_coordinates[0] - bottom_cyl_tank_centre_coord_X)/bottom_cyl_tank_radius_X;
                        rel_coord_Y = (cell_coordinates[1] - bottom_cyl_tank_centre_coord_Y)/bottom_cyl_tank_radius_Y;
                        
                        if(rel_coord_X*rel_coord_X + rel_coord_Y*rel_coord_Y <= 1)
                        {
                            // Writing of initial parameters of cell in tank at bottom facet
                            bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_tank_index);
                            write_string = false;
                            tank_cell    = true;
                            tank_cell_number++;
                        }
                    }
                    
               //     System.out.println("DDD");
                }

                if(tank_cell)
                if(tank_cell_number%100 == 0)
                    completed_steps.setText(completed_steps.getText()+"\nTank cell # "+tank_cell_number+" coordinates: "+
                                      cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                
                if(i_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_index);
                     // bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }
                
                if(i_index==cell_number_I+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_index);
                     // bw1.write(right_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_index);
                     // bw1.write(front_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==cell_number_J+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_index);
                     // bw1.write(back_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_index);
                     // bw1.write(top_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==cell_number_K+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_index);
                     // bw1.write(bottom_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

          //      System.out.println("EEE");

                if(!facet_cell)
                {
                  //  string_counter++;
                  //  read_strings = true;

                 //   System.out.println("GGG");
                    
           //         System.out.println("br_ready = "+br.ready());
               //     System.out.println("read_strings = "+read_strings);

                    if(br.ready())
                    while(read_strings)                    
                    {
                     //   System.out.println("HHH");

                        string = br.readLine();
                        st = new StringTokenizer(string);

                        if(!tank_cell)
                        if(st.hasMoreTokens())
                        {
                            string = st.nextToken();

                            if(!string.equals("#"))
                            {
                                grain_index = (new Integer(string)).intValue();

                                if(grain_index<=grain_number_1)
                                    layer_counter = 0;

                                if((grain_index>grain_number_1)&
                                   (grain_index<=grain_number_1+grain_number_2))
                                    layer_counter = 1;

                                if((grain_index>grain_number_1+grain_number_2)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3))
                                    layer_counter = 2;

                                if((grain_index>grain_number_1+grain_number_2+grain_number_3)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3+grain_number_4))
                                    layer_counter = 3;

                                if(grain_index>grain_number_1+grain_number_2+grain_number_3+grain_number_4)
                                    layer_counter = 4;

                                    bw.write(init_heat_energies[layer_counter]+" "+init_mech_energies[layer_counter]+" "+
                                             init_temperatures [layer_counter]+" "+grain_index);

                                if(show_grain_bounds == 1)
                                if(st.hasMoreTokens())
                                    bw.write(" "+st.nextToken());

                                read_strings = false;

                            //    System.out.println("AAA");
                            }
                         //   System.out.println("BBB");
                        }

                   //     System.out.println("CCC");
                        if(tank_cell)
                            read_strings = false;
                    }

                    read_strings = true;
               //     System.out.println("FFF");
                }

                if(tank_cell | facet_cell)
                if(show_grain_bounds == 1)
                    bw .write(" "+Common.OUTER_CELL);

                bw.newLine();
                bw.flush();
         //       bw1.newLine();
         //       bw1.flush();
            }
            //TEST
       //     bw.write (string_counter+"");
       //     bw1.write(string_counter+"");

            br.close();
            bw.close();
       //     bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
       //     completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }    
    
    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     * @param read_file_name file with information about initial grain geometry
     * @param init_params_file file with information about initial parameters
     * @param tanks_file file with information about parameters of triangle tanks
     */
    private void createInitCondHorizonTriangleTanksFile3D(String read_file_name, String init_params_file, String tanks_file)
    {
        BufferedReader br;
        StringTokenizer st;
        String string;

        // Initial heat energy of cell
        double[] init_heat_energies = new double[phase_number];

        // Initial mechanical energy of cell
        double[] init_mech_energies = new double[phase_number];

        // Initial temperature of cell
        double[] init_temperatures  = new double[phase_number];

        // Counter of specimen layers (strings in read file of initial parameters)
        byte layer_counter = 0;

        // Counter of strings in read file of initial structure
        byte string_counter = 0;

        // Index of grain
        int grain_index = -1;

        // True if process in layer structure is simulated
        boolean layer_structure = false;

        // Initial parameters of boundary cells
        double  init_heat_energy=0,
                init_mech_energy=0,
                init_temperature=0;

        //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
        // "mech" parameter is equal to constant rate of stress or strain change,
        // "heat" parameter is equal to constant temperature or
        //        constant influx of specific surface heat energy.
        double _left_tank_min_coord_X = 0;
        double _left_tank_max_coord_X = 0;
        double _left_tank_min_coord_Y = 0;
        double _left_tank_max_coord_Y = 0;
        double _left_tank_min_coord_Z = 0;
        double _left_tank_max_coord_Z = 0;

        double _front_tank_min_coord_X = 0;
        double _front_tank_max_coord_X = 0;
        double _front_tank_min_coord_Y = 0;
        double _front_tank_max_coord_Y = 0;
        double _front_tank_min_coord_Z = 0;
        double _front_tank_max_coord_Z = 0;

        double _top_tank_min_coord_X = 0;
        double _top_tank_max_coord_X = 0;
        double _top_tank_min_coord_Y = 0;
        double _top_tank_max_coord_Y = 0;
        double _top_tank_min_coord_Z = 0;
        double _top_tank_max_coord_Z = 0;

        double _bottom_tank_min_coord_X = 0;
        double _bottom_tank_max_coord_X = 0;
        double _bottom_tank_min_coord_Y = 0;
        double _bottom_tank_max_coord_Y = 0;
        double _bottom_tank_min_coord_Z = 0;
        double _bottom_tank_max_coord_Z = 0;

        double _back_tank_min_coord_X = 0;
        double _back_tank_max_coord_X = 0;
        double _back_tank_min_coord_Y = 0;
        double _back_tank_max_coord_Y = 0;
        double _back_tank_min_coord_Z = 0;
        double _back_tank_max_coord_Z = 0;

        double _right_tank_min_coord_X = 0;
        double _right_tank_max_coord_X = 0;
        double _right_tank_min_coord_Y = 0;
        double _right_tank_max_coord_Y = 0;
        double _right_tank_min_coord_Z = 0;
        double _right_tank_max_coord_Z = 0;

        try
        {
            br = new BufferedReader(new FileReader(init_params_file));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_params_file);

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        // Initial heat energy of cell
                        init_heat_energies[layer_counter] = (new Double(string)).doubleValue();

                        // Initial mechanical energy of cell
                        init_mech_energies[layer_counter] = (new Double(st.nextToken())).doubleValue();

                        // Initial temperature of cell
                        init_temperatures[layer_counter]  = (new Double(st.nextToken())).doubleValue();

                        layer_counter++;
                    }
                }
            }

            layer_number = layer_counter;
            layer_counter = 0;

            if(layer_number>1)
                layer_structure = true;

            br.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }

        // Coordinates of cell centre
        double[] cell_coordinates;

        boolean read_strings = true;

        // Number of grains in surface layer
        int grain_number_1 = 0;

        // Number of grains in upper intermediate layer
        int grain_number_2 = 0;

        // Number of grains in lower intermediate layer
        int grain_number_3 = 0;

        // Number of grains in upper substrate layer
        int grain_number_4 = 0;
        
        // Number of grains in lower substrate layer
        int grain_number_5 = 0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
        //  BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));
            br = new BufferedReader(new FileReader(read_file_name));
        //  BufferedReader br1= new BufferedReader(new FileReader(read_file_name_1));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+read_file_name);

            // Reading of number of grains in specimen
            while(read_strings)
            if(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                if(br.ready())
                {
                    string = br.readLine();
                    st = new StringTokenizer(string);
                    grain_number   = new Integer(st.nextToken());
                    grain_number_1 = new Integer(st.nextToken());
                    grain_number_2 = new Integer(st.nextToken());
                    grain_number_3 = new Integer(st.nextToken());
                    grain_number_4 = new Integer(st.nextToken());
                    grain_number_5 = new Integer(st.nextToken());
                    read_strings = false;
                }
            }

            // Reading of information about boundary tanks
            TextTableFileReader reader = new TextTableFileReader(tanks_file);
            StringVector table = reader.convertToTable();

            completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary tanks:    "+tanks_file);
            System.out.println("Name of read file with parameters of boundary tanks:    "+tanks_file);

            //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
            // "mech" parameter is equal to constant rate of stress or strain change,
            // "heat" parameter is equal to constant temperature or
            //        constant influx of specific surface heat energy.
            _left_tank_min_coord_X = (new Double(table.getCell(0, 1))).doubleValue();
            _left_tank_max_coord_X = (new Double(table.getCell(0, 2))).doubleValue();
            _left_tank_min_coord_Y = (new Double(table.getCell(0, 3))).doubleValue();
            _left_tank_max_coord_Y = (new Double(table.getCell(0, 4))).doubleValue();
            _left_tank_min_coord_Z = (new Double(table.getCell(0, 5))).doubleValue();
            _left_tank_max_coord_Z = (new Double(table.getCell(0, 6))).doubleValue();

            _front_tank_min_coord_X = (new Double(table.getCell(1, 1))).doubleValue();
            _front_tank_max_coord_X = (new Double(table.getCell(1, 2))).doubleValue();
            _front_tank_min_coord_Y = (new Double(table.getCell(1, 3))).doubleValue();
            _front_tank_max_coord_Y = (new Double(table.getCell(1, 4))).doubleValue();
            _front_tank_min_coord_Z = (new Double(table.getCell(1, 5))).doubleValue();
            _front_tank_max_coord_Z = (new Double(table.getCell(1, 6))).doubleValue();

            _top_tank_min_coord_X = (new Double(table.getCell(2, 1))).doubleValue();
            _top_tank_max_coord_X = (new Double(table.getCell(2, 2))).doubleValue();
            _top_tank_min_coord_Y = (new Double(table.getCell(2, 3))).doubleValue();
            _top_tank_max_coord_Y = (new Double(table.getCell(2, 4))).doubleValue();
            _top_tank_min_coord_Z = (new Double(table.getCell(2, 5))).doubleValue();
            _top_tank_max_coord_Z = (new Double(table.getCell(2, 6))).doubleValue();

            _bottom_tank_min_coord_X = (new Double(table.getCell(3, 1))).doubleValue();
            _bottom_tank_max_coord_X = (new Double(table.getCell(3, 2))).doubleValue();
            _bottom_tank_min_coord_Y = (new Double(table.getCell(3, 3))).doubleValue();
            _bottom_tank_max_coord_Y = (new Double(table.getCell(3, 4))).doubleValue();
            _bottom_tank_min_coord_Z = (new Double(table.getCell(3, 5))).doubleValue();
            _bottom_tank_max_coord_Z = (new Double(table.getCell(3, 6))).doubleValue();

            _back_tank_min_coord_X = (new Double(table.getCell(4, 1))).doubleValue();
            _back_tank_max_coord_X = (new Double(table.getCell(4, 2))).doubleValue();
            _back_tank_min_coord_Y = (new Double(table.getCell(4, 3))).doubleValue();
            _back_tank_max_coord_Y = (new Double(table.getCell(4, 4))).doubleValue();
            _back_tank_min_coord_Z = (new Double(table.getCell(4, 5))).doubleValue();
            _back_tank_max_coord_Z = (new Double(table.getCell(4, 6))).doubleValue();

            _right_tank_min_coord_X = (new Double(table.getCell(5, 1))).doubleValue();
            _right_tank_max_coord_X = (new Double(table.getCell(5, 2))).doubleValue();
            _right_tank_min_coord_Y = (new Double(table.getCell(5, 3))).doubleValue();
            _right_tank_max_coord_Y = (new Double(table.getCell(5, 4))).doubleValue();
            _right_tank_min_coord_Z = (new Double(table.getCell(5, 5))).doubleValue();
            _right_tank_max_coord_Z = (new Double(table.getCell(5, 6))).doubleValue();

            System.out.println("_top_tank_min_coord_X:    "+_top_tank_min_coord_X);
            System.out.println("_top_tank_max_coord_X:    "+_top_tank_max_coord_X);

            read_strings = true;

       //   br1.readLine();
       //   grain_number = new Integer(br1.readLine());

            // Index of grain containing cell on left outer boundary (I=0)
            int left_index  = grain_number+1;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_index = grain_number+2;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_index = grain_number+3;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_index = grain_number+4;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_index = grain_number+5;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_index = grain_number+6;

            // Index of grain containing cell on left outer boundary (I=0)
            int left_tank_index  = grain_number+7;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_tank_index = grain_number+8;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_tank_index = grain_number+9;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_tank_index = grain_number+10;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_tank_index = grain_number+11;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_tank_index = grain_number+12;

            // Writing of comments
            bw.write("# This file contains initial parameters of each cellular automaton (CA):");
            bw.newLine();
            bw.write("# thermal energy, mechanical energy, temperature, index of cluster containing CA,");
            bw.newLine();
            bw.write("# type of location of CA in grain and specimen:");
            bw.newLine();
            bw.write("# 2 - outer boundary CA,");
            bw.newLine();
            bw.write("# 3 - intragranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 4 - intergranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 5 - intragranular CA on specimen boundary,");
            bw.newLine();
            bw.write("# 6 - intergranular CA on specimen boundary.");
            bw.newLine();
            // Writing of total number of cells
         //   bw1.write(cellNumber+"");
         //   bw1.newLine();

            boolean write_string;
            boolean tank_cell;
            boolean facet_cell;
            int tank_cell_number = 0;

            substrate_grain_number = grain_number-layer_number+2;
            
            System.out.println("Cell numbers along axes: "+ (cell_number_I+2)+" "+(cell_number_J+2)+" "+(cell_number_K+2));

            completed_steps.setText(completed_steps.getText()+"\n _top_tank_min_coord_X = "+_top_tank_min_coord_X+"; _top_tank_max_coord_X: "+_top_tank_max_coord_X);
            
            cell_coordinates = calcCoordinates(cell_number_I+1, cell_number_J+1, cell_number_K+1);
            
            completed_steps.setText(completed_steps.getText()+"\n Max. coordinates: "+ cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
            
            // Coordinates of tank centres
            double left_tank_centre_coord_Y   = (_left_tank_min_coord_Y   + _left_tank_max_coord_Y)/2;
            double front_tank_centre_coord_X  = (_front_tank_min_coord_X  + _front_tank_max_coord_X)/2;
            double top_tank_centre_coord_X    = (_top_tank_min_coord_X    + _top_tank_max_coord_X)/2;
            double bottom_tank_centre_coord_X = (_bottom_tank_min_coord_X + _bottom_tank_max_coord_X)/2;
            double back_tank_centre_coord_X   = (_back_tank_min_coord_X   + _back_tank_max_coord_X)/2;
            double right_tank_centre_coord_Y  = (_right_tank_min_coord_Y  + _right_tank_max_coord_Y)/2;
            
            double tang_coeff = 2;
                
            for (int k_index = 0; k_index < cell_number_K+2; k_index++)
            for (int j_index = 0; j_index < cell_number_J+2; j_index++)
            for (int i_index = 0; i_index < cell_number_I+2; i_index++)
            {
            //  System.out.println("Cell index: "+ i_index+" "+j_index+" "+k_index);
                
                write_string = true;
                tank_cell    = false;
                facet_cell   = false;

                cell_coordinates = calcCoordinates(i_index, j_index, k_index);
                
                // Writing of parameters of tank containing current cell
                if(write_string)
                {
                    // If current cell is in left tank
                    tang_coeff = (_left_tank_max_coord_X - _left_tank_min_coord_X)/(_left_tank_max_coord_Y - left_tank_centre_coord_Y);
                    
                    if(!tank_cell)
                    if(cell_coordinates[0] >= _left_tank_min_coord_X & cell_coordinates[0] <= _left_tank_max_coord_X)
                    if(tang_coeff*Math.abs(cell_coordinates[1] - left_tank_centre_coord_Y) <= Math.abs(cell_coordinates[0] - _left_tank_max_coord_X))
                    if(cell_coordinates[2] >= _left_tank_min_coord_Z & cell_coordinates[2] <= _left_tank_max_coord_Z)
                    {
                        // Writing of initial parameters of cell in tank at left facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in front tank
                    tang_coeff = (_front_tank_max_coord_Y - _front_tank_min_coord_Y)/(front_tank_centre_coord_X - _front_tank_min_coord_X);
                    
                    if(!tank_cell)
                    if(tang_coeff*Math.abs(cell_coordinates[0] - front_tank_centre_coord_X) <= Math.abs(cell_coordinates[1] - _front_tank_max_coord_Y))
                    if(cell_coordinates[1]>=_front_tank_min_coord_Y & cell_coordinates[1]<=_front_tank_max_coord_Y)
                    if(cell_coordinates[2]>=_front_tank_min_coord_Z & cell_coordinates[2]<=_front_tank_max_coord_Z)
                    {
                        // Writing of initial parameters of cell in tank at front facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in top tank
                    tang_coeff = (_top_tank_max_coord_Z - _top_tank_min_coord_Z)/(top_tank_centre_coord_X - _top_tank_min_coord_X);
                    
                    if(!tank_cell)
                    if(tang_coeff*Math.abs(cell_coordinates[0] - top_tank_centre_coord_X) <= Math.abs(cell_coordinates[2] - _top_tank_max_coord_Z))
                    if(cell_coordinates[1]>=_top_tank_min_coord_Y & cell_coordinates[1]<=_top_tank_max_coord_Y)
                    if(cell_coordinates[2]>=_top_tank_min_coord_Z & cell_coordinates[2]<=_top_tank_max_coord_Z)
                    {
                        // Writing of initial parameters of cell in tank at top facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in bottom tank
                    tang_coeff = (_bottom_tank_max_coord_Z - _bottom_tank_min_coord_Z)/(bottom_tank_centre_coord_X - _bottom_tank_min_coord_X);
                    
                    if(!tank_cell)
                    if(tang_coeff*Math.abs(cell_coordinates[0] - bottom_tank_centre_coord_X) <= Math.abs(cell_coordinates[2] - _bottom_tank_min_coord_Z))
                    if((cell_coordinates[1]>=_bottom_tank_min_coord_Y)&(cell_coordinates[1]<=_bottom_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_bottom_tank_min_coord_Z)&(cell_coordinates[2]<=_bottom_tank_max_coord_Z))
                    {
                        // Writing of initial parameters of cell in tank at bottom facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in back tank
                    tang_coeff = (_back_tank_max_coord_Y - _back_tank_min_coord_Y)/(back_tank_centre_coord_X - _back_tank_min_coord_X);
                    
                    if(!tank_cell)
                    if(tang_coeff*Math.abs(cell_coordinates[0] - back_tank_centre_coord_X) <= Math.abs(cell_coordinates[1] - _back_tank_min_coord_Y))
                    if((cell_coordinates[1]>=_back_tank_min_coord_Y)&(cell_coordinates[1]<=_back_tank_max_coord_Y))
                    if((cell_coordinates[2]>=_back_tank_min_coord_Z)&(cell_coordinates[2]<=_back_tank_max_coord_Z))
                    {
                        // Writing of initial parameters of cell in tank at back facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in right tank
                    tang_coeff = (_right_tank_max_coord_X - _right_tank_min_coord_X)/(_right_tank_max_coord_Y - right_tank_centre_coord_Y);
                    
                    if(!tank_cell)
                    if((cell_coordinates[0]>=_right_tank_min_coord_X)&(cell_coordinates[0]<=_right_tank_max_coord_X))
                    if(tang_coeff*Math.abs(cell_coordinates[1] - right_tank_centre_coord_Y) <= Math.abs(cell_coordinates[0] - _right_tank_min_coord_X))
                    if((cell_coordinates[2]>=_right_tank_min_coord_Z)&(cell_coordinates[2]<=_right_tank_max_coord_Z))
                    {
                        // Writing of initial parameters of cell in tank at right facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

               //     System.out.println("DDD");
                }

                if(tank_cell)
                if(tank_cell_number%100 == 0)
                    completed_steps.setText(completed_steps.getText()+"\nTank cell # "+tank_cell_number+" coordinates: "+
                                      cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                
                if(i_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_index);
                     // bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }
                
                if(i_index==cell_number_I+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_index);
                     // bw1.write(right_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_index);
                     // bw1.write(front_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==cell_number_J+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_index);
                     // bw1.write(back_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_index);
                     // bw1.write(top_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==cell_number_K+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_index);
                     // bw1.write(bottom_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

          //      System.out.println("EEE");

                if(!facet_cell)
                {
                  //  string_counter++;
                  //  read_strings = true;

                 //   System.out.println("GGG");
                    
           //         System.out.println("br_ready = "+br.ready());
               //     System.out.println("read_strings = "+read_strings);

                    if(br.ready())
                    while(read_strings)                    
                    {
                     //   System.out.println("HHH");

                        string = br.readLine();
                        st = new StringTokenizer(string);

                        if(!tank_cell)
                        if(st.hasMoreTokens())
                        {
                            string = st.nextToken();

                            if(!string.equals("#"))
                            {
                                grain_index = (new Integer(string)).intValue();

                                if(grain_index<=grain_number_1)
                                    layer_counter = 0;

                                if((grain_index>grain_number_1)&
                                   (grain_index<=grain_number_1+grain_number_2))
                                    layer_counter = 1;

                                if((grain_index>grain_number_1+grain_number_2)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3))
                                    layer_counter = 2;

                                if((grain_index>grain_number_1+grain_number_2+grain_number_3)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3+grain_number_4))
                                    layer_counter = 3;

                                if(grain_index>grain_number_1+grain_number_2+grain_number_3+grain_number_4)
                                    layer_counter = 4;

                                    bw.write(init_heat_energies[layer_counter]+" "+init_mech_energies[layer_counter]+" "+
                                             init_temperatures [layer_counter]+" "+grain_index);

                                if(show_grain_bounds == 1)
                                if(st.hasMoreTokens())
                                    bw.write(" "+st.nextToken());

                                read_strings = false;

                            //    System.out.println("AAA");
                            }
                         //   System.out.println("BBB");
                        }

                   //     System.out.println("CCC");
                        if(tank_cell)
                            read_strings = false;
                    }

                    read_strings = true;
               //     System.out.println("FFF");
                }

                if(tank_cell | facet_cell)
                if(show_grain_bounds == 1)
                    bw .write(" "+Common.OUTER_CELL);

                bw.newLine();
                bw.flush();
         //       bw1.newLine();
         //       bw1.flush();
            }
            //TEST
       //     bw.write (string_counter+"");
       //     bw1.write(string_counter+"");

            br.close();
            bw.close();
       //     bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
       //     completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }
    
    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     * @param read_file_name file with information about initial grain geometry
     * @param init_params_file file with information about initial parameters
     * @param tanks_file file with information about parameters of triangle tanks
     */
    private void createInitCondHorizonCircleTanksFile3D(String read_file_name, String init_params_file, String tanks_file)
    {
        BufferedReader br;
        StringTokenizer st;
        String string;

        // Initial heat energy of cell
        double[] init_heat_energies = new double[phase_number];

        // Initial mechanical energy of cell
        double[] init_mech_energies = new double[phase_number];

        // Initial temperature of cell
        double[] init_temperatures  = new double[phase_number];

        // Counter of specimen layers (strings in read file of initial parameters)
        byte layer_counter = 0;

        // Counter of strings in read file of initial structure
        byte string_counter = 0;

        // Index of grain
        int grain_index = -1;

        // True if process in layer structure is simulated
        boolean layer_structure = false;

        // Initial parameters of boundary cells
        double  init_heat_energy=0,
                init_mech_energy=0,
                init_temperature=0;

        //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
        // "mech" parameter is equal to constant rate of stress or strain change,
        // "heat" parameter is equal to constant temperature or
        //        constant influx of specific surface heat energy.
        double _left_tank_min_coord_X = 0;
        double _left_tank_max_coord_X = 0;
        double _left_tank_min_coord_Y = 0;
        double _left_tank_max_coord_Y = 0;
        double _left_tank_min_coord_Z = 0;
        double _left_tank_max_coord_Z = 0;

        double _front_tank_min_coord_X = 0;
        double _front_tank_max_coord_X = 0;
        double _front_tank_min_coord_Y = 0;
        double _front_tank_max_coord_Y = 0;
        double _front_tank_min_coord_Z = 0;
        double _front_tank_max_coord_Z = 0;

        double _top_tank_min_coord_X = 0;
        double _top_tank_max_coord_X = 0;
        double _top_tank_min_coord_Y = 0;
        double _top_tank_max_coord_Y = 0;
        double _top_tank_min_coord_Z = 0;
        double _top_tank_max_coord_Z = 0;

        double _bottom_tank_min_coord_X = 0;
        double _bottom_tank_max_coord_X = 0;
        double _bottom_tank_min_coord_Y = 0;
        double _bottom_tank_max_coord_Y = 0;
        double _bottom_tank_min_coord_Z = 0;
        double _bottom_tank_max_coord_Z = 0;

        double _back_tank_min_coord_X = 0;
        double _back_tank_max_coord_X = 0;
        double _back_tank_min_coord_Y = 0;
        double _back_tank_max_coord_Y = 0;
        double _back_tank_min_coord_Z = 0;
        double _back_tank_max_coord_Z = 0;

        double _right_tank_min_coord_X = 0;
        double _right_tank_max_coord_X = 0;
        double _right_tank_min_coord_Y = 0;
        double _right_tank_max_coord_Y = 0;
        double _right_tank_min_coord_Z = 0;
        double _right_tank_max_coord_Z = 0;

        try
        {
            br = new BufferedReader(new FileReader(init_params_file));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_params_file);

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        // Initial heat energy of cell
                        init_heat_energies[layer_counter] = (new Double(string)).doubleValue();

                        // Initial mechanical energy of cell
                        init_mech_energies[layer_counter] = (new Double(st.nextToken())).doubleValue();

                        // Initial temperature of cell
                        init_temperatures[layer_counter]  = (new Double(st.nextToken())).doubleValue();

                        layer_counter++;
                    }
                }
            }

            layer_number = layer_counter;
            layer_counter = 0;

            if(layer_number>1)
                layer_structure = true;

            br.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }

        // Coordinates of cell centre
        double[] cell_coordinates;

        boolean read_strings = true;

        // Number of grains in surface layer
        int grain_number_1 = 0;

        // Number of grains in upper intermediate layer
        int grain_number_2 = 0;

        // Number of grains in lower intermediate layer
        int grain_number_3 = 0;

        // Number of grains in upper substrate layer
        int grain_number_4 = 0;
        
        // Number of grains in lower substrate layer
        int grain_number_5 = 0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
        //  BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));
            br = new BufferedReader(new FileReader(read_file_name));
        //  BufferedReader br1= new BufferedReader(new FileReader(read_file_name_1));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+read_file_name);

            // Reading of number of grains in specimen
            while(read_strings)
            if(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                if(br.ready())
                {
                    string = br.readLine();
                    st = new StringTokenizer(string);
                    grain_number   = new Integer(st.nextToken());
                    grain_number_1 = new Integer(st.nextToken());
                    grain_number_2 = new Integer(st.nextToken());
                    grain_number_3 = new Integer(st.nextToken());
                    grain_number_4 = new Integer(st.nextToken());
                    grain_number_5 = new Integer(st.nextToken());
                    read_strings = false;
                }
            }

            // Reading of information about boundary tanks
            TextTableFileReader reader = new TextTableFileReader(tanks_file);
            StringVector table = reader.convertToTable();

            completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary tanks:    "+tanks_file);
            System.out.println("Name of read file with parameters of boundary tanks:    "+tanks_file);

            //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
            // "mech" parameter is equal to constant rate of stress or strain change,
            // "heat" parameter is equal to constant temperature or
            //        constant influx of specific surface heat energy.
            _left_tank_min_coord_X = (new Double(table.getCell(0, 1))).doubleValue();
            _left_tank_max_coord_X = (new Double(table.getCell(0, 2))).doubleValue();
            _left_tank_min_coord_Y = (new Double(table.getCell(0, 3))).doubleValue();
            _left_tank_max_coord_Y = (new Double(table.getCell(0, 4))).doubleValue();
            _left_tank_min_coord_Z = (new Double(table.getCell(0, 5))).doubleValue();
            _left_tank_max_coord_Z = (new Double(table.getCell(0, 6))).doubleValue();

            _front_tank_min_coord_X = (new Double(table.getCell(1, 1))).doubleValue();
            _front_tank_max_coord_X = (new Double(table.getCell(1, 2))).doubleValue();
            _front_tank_min_coord_Y = (new Double(table.getCell(1, 3))).doubleValue();
            _front_tank_max_coord_Y = (new Double(table.getCell(1, 4))).doubleValue();
            _front_tank_min_coord_Z = (new Double(table.getCell(1, 5))).doubleValue();
            _front_tank_max_coord_Z = (new Double(table.getCell(1, 6))).doubleValue();

            _top_tank_min_coord_X = (new Double(table.getCell(2, 1))).doubleValue();
            _top_tank_max_coord_X = (new Double(table.getCell(2, 2))).doubleValue();
            _top_tank_min_coord_Y = (new Double(table.getCell(2, 3))).doubleValue();
            _top_tank_max_coord_Y = (new Double(table.getCell(2, 4))).doubleValue();
            _top_tank_min_coord_Z = (new Double(table.getCell(2, 5))).doubleValue();
            _top_tank_max_coord_Z = (new Double(table.getCell(2, 6))).doubleValue();

            _bottom_tank_min_coord_X = (new Double(table.getCell(3, 1))).doubleValue();
            _bottom_tank_max_coord_X = (new Double(table.getCell(3, 2))).doubleValue();
            _bottom_tank_min_coord_Y = (new Double(table.getCell(3, 3))).doubleValue();
            _bottom_tank_max_coord_Y = (new Double(table.getCell(3, 4))).doubleValue();
            _bottom_tank_min_coord_Z = (new Double(table.getCell(3, 5))).doubleValue();
            _bottom_tank_max_coord_Z = (new Double(table.getCell(3, 6))).doubleValue();

            _back_tank_min_coord_X = (new Double(table.getCell(4, 1))).doubleValue();
            _back_tank_max_coord_X = (new Double(table.getCell(4, 2))).doubleValue();
            _back_tank_min_coord_Y = (new Double(table.getCell(4, 3))).doubleValue();
            _back_tank_max_coord_Y = (new Double(table.getCell(4, 4))).doubleValue();
            _back_tank_min_coord_Z = (new Double(table.getCell(4, 5))).doubleValue();
            _back_tank_max_coord_Z = (new Double(table.getCell(4, 6))).doubleValue();

            _right_tank_min_coord_X = (new Double(table.getCell(5, 1))).doubleValue();
            _right_tank_max_coord_X = (new Double(table.getCell(5, 2))).doubleValue();
            _right_tank_min_coord_Y = (new Double(table.getCell(5, 3))).doubleValue();
            _right_tank_max_coord_Y = (new Double(table.getCell(5, 4))).doubleValue();
            _right_tank_min_coord_Z = (new Double(table.getCell(5, 5))).doubleValue();
            _right_tank_max_coord_Z = (new Double(table.getCell(5, 6))).doubleValue();

            System.out.println("_top_tank_min_coord_X:    "+_top_tank_min_coord_X);
            System.out.println("_top_tank_max_coord_X:    "+_top_tank_max_coord_X);

            read_strings = true;

       //   br1.readLine();
       //   grain_number = new Integer(br1.readLine());

            // Index of grain containing cell on left outer boundary (I=0)
            int left_index  = grain_number+1;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_index = grain_number+2;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_index = grain_number+3;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_index = grain_number+4;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_index = grain_number+5;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_index = grain_number+6;

            // Index of grain containing cell on left outer boundary (I=0)
            int left_tank_index  = grain_number+7;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_tank_index = grain_number+8;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_tank_index = grain_number+9;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_tank_index = grain_number+10;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_tank_index = grain_number+11;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_tank_index = grain_number+12;

            // Writing of comments
            bw.write("# This file contains initial parameters of each cellular automaton (CA):");
            bw.newLine();
            bw.write("# thermal energy, mechanical energy, temperature, index of cluster containing CA,");
            bw.newLine();
            bw.write("# type of location of CA in grain and specimen:");
            bw.newLine();
            bw.write("# 2 - outer boundary CA,");
            bw.newLine();
            bw.write("# 3 - intragranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 4 - intergranular CA in the interior of specimen,");
            bw.newLine();
            bw.write("# 5 - intragranular CA on specimen boundary,");
            bw.newLine();
            bw.write("# 6 - intergranular CA on specimen boundary.");
            bw.newLine();
            // Writing of total number of cells
         //   bw1.write(cellNumber+"");
         //   bw1.newLine();

            boolean write_string;
            boolean tank_cell;
            boolean facet_cell;
            int tank_cell_number = 0;

            substrate_grain_number = grain_number-layer_number+2;
            
            System.out.println("Cell numbers along axes: "+ (cell_number_I+2)+" "+(cell_number_J+2)+" "+(cell_number_K+2));

            completed_steps.setText(completed_steps.getText()+"\n _top_tank_min_coord_X = "+_top_tank_min_coord_X+"; _top_tank_max_coord_X: "+_top_tank_max_coord_X);
            
            cell_coordinates = calcCoordinates(cell_number_I+1, cell_number_J+1, cell_number_K+1);
            
            completed_steps.setText(completed_steps.getText()+"\n Max. coordinates: "+ cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
            
            // Coordinates of tank centres and radiuses
            double left_tank_radius           = (_left_tank_max_coord_Y   - _left_tank_min_coord_Y)/2;
            double left_tank_centre_coord_Y   = (_left_tank_min_coord_Y   + _left_tank_max_coord_Y)/2;
            double left_tank_centre_coord_X   =  _left_tank_max_coord_X   - left_tank_radius;
            
            double front_tank_radius          = (_front_tank_max_coord_X  - _front_tank_min_coord_X)/2;
            double front_tank_centre_coord_X  = (_front_tank_min_coord_X  + _front_tank_max_coord_X)/2;
            double front_tank_centre_coord_Y  =  _front_tank_max_coord_Y  - front_tank_radius;
            
            double top_tank_radius            = (_top_tank_max_coord_X    - _top_tank_min_coord_X)/2;
            double top_tank_centre_coord_X    = (_top_tank_min_coord_X    + _top_tank_max_coord_X)/2;
            double top_tank_centre_coord_Z    =  _top_tank_max_coord_Z    - top_tank_radius;
            
            double bottom_tank_radius         = (_bottom_tank_max_coord_X - _bottom_tank_min_coord_X)/2;
            double bottom_tank_centre_coord_X = (_bottom_tank_min_coord_X + _bottom_tank_max_coord_X)/2;
            double bottom_tank_centre_coord_Z =  _bottom_tank_min_coord_Z + bottom_tank_radius;
            
            double back_tank_radius           = (_back_tank_max_coord_X   - _back_tank_min_coord_X)/2;
            double back_tank_centre_coord_X   = (_back_tank_min_coord_X   + _back_tank_max_coord_X)/2;
            double back_tank_centre_coord_Y   =  _back_tank_min_coord_Y   + back_tank_radius;
            
            double right_tank_radius          = (_right_tank_max_coord_Y  - _right_tank_min_coord_Y)/2;
            double right_tank_centre_coord_Y  = (_right_tank_min_coord_Y  + _right_tank_max_coord_Y)/2;
            double right_tank_centre_coord_X  =  _right_tank_min_coord_X  + right_tank_radius;
            
            // Relative coordinates (for example, at top facet: the coordinate X = 0 for point with coordinate X of center, 
            //                                                  the coordinate X = 1 for point with coordinate Y of center)
            double rel_coord_X = 0;
            double rel_coord_Y = 0;
            double rel_coord_Z = 0;
            
            for (int k_index = 0; k_index < cell_number_K+2; k_index++)
            for (int j_index = 0; j_index < cell_number_J+2; j_index++)
            for (int i_index = 0; i_index < cell_number_I+2; i_index++)
            {
            //  System.out.println("Cell index: "+ i_index+" "+j_index+" "+k_index);
                
                write_string = true;
                tank_cell    = false;
                facet_cell   = false;

                cell_coordinates = calcCoordinates(i_index, j_index, k_index);
                
                // Writing of parameters of tank containing current cell
                if(write_string)
                {
                    // If current cell is in left tank
                    rel_coord_X = cell_coordinates[0] - left_tank_centre_coord_X;
                    rel_coord_Y = cell_coordinates[1] - left_tank_centre_coord_Y;
                    
                    if(!tank_cell)
                    if(cell_coordinates[0] <= left_tank_centre_coord_X |
                       rel_coord_X*rel_coord_X + rel_coord_Y*rel_coord_Y <= left_tank_radius*left_tank_radius)
                    if(cell_coordinates[1] >= _left_tank_min_coord_Y & cell_coordinates[1] <= _left_tank_max_coord_Y)
                    if(cell_coordinates[2] >= _left_tank_min_coord_Z & cell_coordinates[2] <= _left_tank_max_coord_Z)
                    {
                        // Writing of initial parameters of cell in tank at left facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in front tank
                    rel_coord_X = cell_coordinates[0] - front_tank_centre_coord_X;
                    rel_coord_Y = cell_coordinates[1] - front_tank_centre_coord_Y;
                    
                    if(!tank_cell)
                    if(cell_coordinates[0] >= _front_tank_min_coord_X & cell_coordinates[0] <= _front_tank_max_coord_X)
                    if(cell_coordinates[1] <= front_tank_centre_coord_Y |
                       rel_coord_X*rel_coord_X + rel_coord_Y*rel_coord_Y <= front_tank_radius*front_tank_radius)
                    if(cell_coordinates[2] >= _front_tank_min_coord_Z & cell_coordinates[2] <= _front_tank_max_coord_Z)
                    {
                        // Writing of initial parameters of cell in tank at front facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in top tank
                    rel_coord_X = cell_coordinates[0] - top_tank_centre_coord_X;
                    rel_coord_Z = cell_coordinates[2] - top_tank_centre_coord_Z;
                    
                    if(!tank_cell)
                    if(cell_coordinates[0] >= _top_tank_min_coord_X & cell_coordinates[0] <= _top_tank_max_coord_X)
                    if(cell_coordinates[1] >= _top_tank_min_coord_Y & cell_coordinates[1] <= _top_tank_max_coord_Y)
                    if(cell_coordinates[2] <= top_tank_centre_coord_Z |
                       rel_coord_X*rel_coord_X + rel_coord_Z*rel_coord_Z <= top_tank_radius*top_tank_radius)
                    {
                        // Writing of initial parameters of cell in tank at top facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in bottom tank
                    rel_coord_X = cell_coordinates[0] - bottom_tank_centre_coord_X;
                    rel_coord_Z = cell_coordinates[2] - bottom_tank_centre_coord_Z;
                    
                    if(!tank_cell)
                    if(cell_coordinates[0] >= _bottom_tank_min_coord_X & cell_coordinates[0] <= _bottom_tank_max_coord_X)
                    if(cell_coordinates[1] >= _bottom_tank_min_coord_Y & cell_coordinates[1] <= _bottom_tank_max_coord_Y)
                    if(cell_coordinates[2] >= bottom_tank_centre_coord_Z |
                       rel_coord_X*rel_coord_X + rel_coord_Z*rel_coord_Z <= bottom_tank_radius*bottom_tank_radius)
                    {
                        // Writing of initial parameters of cell in tank at bottom facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in back tank
                    rel_coord_X = cell_coordinates[0] - back_tank_centre_coord_X;
                    rel_coord_Y = cell_coordinates[1] - back_tank_centre_coord_Y;
                    
                    if(!tank_cell)
                    if(cell_coordinates[0] >= _back_tank_min_coord_X & cell_coordinates[0] <= _back_tank_max_coord_X)
                    if(cell_coordinates[1] >= back_tank_centre_coord_Y |
                       rel_coord_X*rel_coord_X + rel_coord_Y*rel_coord_Y <= back_tank_radius*back_tank_radius)
                    if(cell_coordinates[2] >= _back_tank_min_coord_Z & cell_coordinates[2] <= _back_tank_max_coord_Z)
                    {
                        // Writing of initial parameters of cell in tank at back facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }

                    // If current cell is in right tank
                    rel_coord_X = cell_coordinates[0] - right_tank_centre_coord_X;
                    rel_coord_Y = cell_coordinates[1] - right_tank_centre_coord_Y;
                    
                    if(!tank_cell)
                    if(cell_coordinates[0] >= right_tank_centre_coord_X |
                       rel_coord_X*rel_coord_X + rel_coord_Y*rel_coord_Y <= right_tank_radius*right_tank_radius)
                    if(cell_coordinates[1] >= _right_tank_min_coord_Y & cell_coordinates[1] <= _right_tank_max_coord_Y)
                    if(cell_coordinates[2] >= _right_tank_min_coord_Z & cell_coordinates[2] <= _right_tank_max_coord_Z)
                    {
                        // Writing of initial parameters of cell in tank at right facet
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_tank_index);
                        write_string = false;
                        tank_cell = true;
                        tank_cell_number++;
                    }
                }

                if(tank_cell)
                if(tank_cell_number%100 == 0)
                    completed_steps.setText(completed_steps.getText()+"\nTank cell # "+tank_cell_number+" coordinates: "+
                                      cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                
                if(i_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+left_index);
                     // bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }
                
                if(i_index==cell_number_I+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+right_index);
                     // bw1.write(right_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+front_index);
                     // bw1.write(front_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(j_index==cell_number_J+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+back_index);
                     // bw1.write(back_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==0)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+top_index);
                     // bw1.write(top_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

                if(k_index==cell_number_K+1)
                {
                    if(write_string)
                    {
                        bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+bottom_index);
                     // bw1.write(bottom_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        write_string = false;
                    }
                    facet_cell = true;
                }

          //      System.out.println("EEE");

                if(!facet_cell)
                {
                  //  string_counter++;
                  //  read_strings = true;

                 //   System.out.println("GGG");
                    
           //         System.out.println("br_ready = "+br.ready());
               //     System.out.println("read_strings = "+read_strings);

                    if(br.ready())
                    while(read_strings)                    
                    {
                     //   System.out.println("HHH");

                        string = br.readLine();
                        st = new StringTokenizer(string);

                        if(!tank_cell)
                        if(st.hasMoreTokens())
                        {
                            string = st.nextToken();

                            if(!string.equals("#"))
                            {
                                grain_index = (new Integer(string)).intValue();

                                if(grain_index<=grain_number_1)
                                    layer_counter = 0;

                                if((grain_index>grain_number_1)&
                                   (grain_index<=grain_number_1+grain_number_2))
                                    layer_counter = 1;

                                if((grain_index>grain_number_1+grain_number_2)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3))
                                    layer_counter = 2;

                                if((grain_index>grain_number_1+grain_number_2+grain_number_3)&
                                   (grain_index<=grain_number_1+grain_number_2+grain_number_3+grain_number_4))
                                    layer_counter = 3;

                                if(grain_index>grain_number_1+grain_number_2+grain_number_3+grain_number_4)
                                    layer_counter = 4;

                                    bw.write(init_heat_energies[layer_counter]+" "+init_mech_energies[layer_counter]+" "+
                                             init_temperatures [layer_counter]+" "+grain_index);

                                if(show_grain_bounds == 1)
                                if(st.hasMoreTokens())
                                    bw.write(" "+st.nextToken());

                                read_strings = false;

                            //    System.out.println("AAA");
                            }
                         //   System.out.println("BBB");
                        }

                   //     System.out.println("CCC");
                        if(tank_cell)
                            read_strings = false;
                    }

                    read_strings = true;
               //     System.out.println("FFF");
                }

                if(tank_cell | facet_cell)
                if(show_grain_bounds == 1)
                    bw .write(" "+Common.OUTER_CELL);

                bw.newLine();
                bw.flush();
         //       bw1.newLine();
         //       bw1.flush();
            }
            //TEST
       //     bw.write (string_counter+"");
       //     bw1.write(string_counter+"");

            br.close();
            bw.close();
       //     bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
       //     completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }
    
    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     * Task conditions: specimen with planar crack perpendicular to axis Y
     * (in the middle of the specimen, thickness =  3 cell layers, index I<= 0,3*size_X)
     * is tensioned along axis Y.
     * @param read_file_name file with information about initial grain geometry
     * @param init_params_file file with information about initial parameters
     * @param crack_number number of cracks in surface layer
     */
    public void createInitCondCracksTensionYFile3D(String read_file_name, String init_params_file, int crack_number)
    {
        BufferedReader br;
        StringTokenizer st;
        String string, grain_index;

        // Initial heat energy of cell
        double init_heat_energy = 0;

        // Initial mechanical energy of cell
        double init_mech_energy = 0;

        // Initial temperature of cell
        double init_temperature = 300.0;

        // Counter of layers
        byte layer_counter = 0;

        try
        {
            br = new BufferedReader(new FileReader(init_params_file));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_params_file);

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        // Initial heat energy of cell
                        init_heat_energy = (new Double(string)).doubleValue();

                        // Initial mechanical energy of cell
                        init_mech_energy = (new Double(st.nextToken())).doubleValue();

                        // Initial temperature of cell
                        init_temperature = (new Double(st.nextToken())).doubleValue();

                        layer_counter++;
                    }
                }
            }

            layer_number = layer_counter;
            layer_counter = 0;
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }

        // Coordinates of cell centre
        double[] cell_coordinates;

        int string_counter = 0;

        boolean read_strings = true;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
        //    BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));
            br = new BufferedReader(new FileReader(read_file_name));
        //    BufferedReader br1= new BufferedReader(new FileReader(read_file_name_1));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+read_file_name);
            
            // Reading number of grains in specimen
            while(br.ready()&read_strings)
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        grain_number = new Integer(br.readLine());
                        read_strings = false;
                    }
                }
            }
            
       //   br1.readLine();
       //   grain_number = new Integer(br1.readLine());

            // Index of grain containing cell on left outer boundary (I=0)
            int left_index = grain_number+1;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_index = grain_number+6;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_index = grain_number+2;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_index = grain_number+5;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_index = grain_number+3;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_index = grain_number+4;

            // Writing of comments
            bw.write("#   This file contains initial parameters of each cellular automaton (CA):");
            bw.newLine();
            bw.write("# thermal energy, mechanical energy, temperature, index of cluster containing CA, cluster type.");
            bw.newLine();

            // Writing of total number of cells
        //    bw1.write(cellNumber+"");
        //    bw1.newLine();

            boolean write_string;

            for (int k_index = 0; k_index < cell_number_K+2; k_index++)
            for (int j_index = 0; j_index < cell_number_J+2; j_index++)
            for (int i_index = 0; i_index < cell_number_I+2; i_index++)
            {
                write_string = true;

                cell_coordinates = calcCoordinates(i_index, j_index, k_index);

                if(write_string)if(j_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              front_index);
            //        bw1.write(front_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(j_index==cell_number_J+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              back_index);
            //        bw1.write(back_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(i_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              left_index);
           //         bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(i_index==cell_number_I+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              right_index);
           //         bw1.write(right_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(k_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              top_index);
            //        bw1.write(top_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(k_index==cell_number_K+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              bottom_index);
            //        bw1.write(bottom_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)
                {
                    grain_index = br.readLine();

                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature);

                    if((k_index> 0)&(k_index< cell_number_K+1)&
                       (i_index> 0)&(i_index<=Math.round(cell_number_I*0.3))&
                       (j_index!=0)&(j_index!=cell_number_J+1)&
                       (((j_index+1)%(cell_number_J/(crack_number+1))==0)|
                        ( j_index   %(cell_number_J/(crack_number+1))==0)|
                        ((j_index-1)%(cell_number_J/(crack_number+1))==0))&
                        ((j_index+1)/(cell_number_J/(crack_number+1))==crack_number))//==crack_number-1))//
                    {
                        bw .write(" "+left_index);

                        if(show_grain_bounds == 1)
                            bw .write(" "+Common.OUTER_CELL);
                        // bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    }
                    else
                    {
                        string_counter++;

                        if(show_grain_bounds == 1)
                        {
                            bw .write(" "+grain_index);
                         // bw1.write(br1.readLine());
                        }
                        else
                        {
                            st = new StringTokenizer(grain_index);
                            
                            if(st.hasMoreTokens())
                                bw .write(" "+st.nextToken());
                            
                         // bw1.write(br1.readLine());
                        }
                    }
                }
                else
                {
                    if(show_grain_bounds == 1)
                        bw .write(" "+Common.OUTER_CELL);
                }

                bw.newLine();
                bw.flush();
         //       bw1.newLine();
         //       bw1.flush();
            }
            
            //TEST
            completed_steps.setText(completed_steps.getText()+"\nNumber of inner cells: "+string_counter);

            br.close();
            bw.close();
       //     bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
       //     completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with initial conditions of task:
     * surface layer, interlayer and substrate.
     * Task conditions: specimen with planar crack perpendicular to axis X
     * (in the middle of the specimen, thickness =  3 cell layers, index J<= 0,3*size_Y)
     * is tensioned along axis X.
     * @param read_file_name file with information about initial grain geometry
     * @param init_params_file file with information about initial parameters
     * @param crack_number number of cracks in surface layer
     */
    public void createInitCondCracksTensionXFile3D(String read_file_name, String init_params_file, int crack_number)
    {
        BufferedReader br;
        StringTokenizer st;
        String string, grain_index;

        // Initial heat energy of cell
        double init_heat_energy = 0;

        // Initial mechanical energy of cell
        double init_mech_energy = 0;

        // Initial temperature of cell
        double init_temperature = 300.0;

        // Counter of layers
        byte layer_counter = 0;

        try
        {
            br = new BufferedReader(new FileReader(init_params_file));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_params_file);

            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        // Initial heat energy of cell
                        init_heat_energy = (new Double(string)).doubleValue();

                        // Initial mechanical energy of cell
                        init_mech_energy = (new Double(st.nextToken())).doubleValue();

                        // Initial temperature of cell
                        init_temperature = (new Double(st.nextToken())).doubleValue();

                        layer_counter++;
                    }
                }
            }

            layer_number = layer_counter;
            layer_counter = 0;
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }

        // Coordinates of cell centre
        double[] cell_coordinates;

        int string_counter = 0;

        boolean read_strings = true;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
        //    BufferedWriter bw1 = new BufferedWriter(new FileWriter(init_cond_file_geometry));
            br = new BufferedReader(new FileReader(read_file_name));
        //    BufferedReader br1= new BufferedReader(new FileReader(read_file_name_1));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+read_file_name);

            // Reading number of grains in specimen
            while(br.ready()&read_strings)
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    string = st.nextToken();

                    if(!string.equals("#"))
                    {
                        grain_number = new Integer(br.readLine());
                        read_strings = false;
                    }
                }
            }

       //   br1.readLine();
       //   grain_number = new Integer(br1.readLine());

            // Index of grain containing cell on left outer boundary (I=0)
            int left_index  = grain_number+1;

            // Index of grain containing cell on right outer boundary (I=Imax)
            int right_index = grain_number+6;

            // Index of grain containing cell on front outer boundary (J=0)
            int front_index = grain_number+2;

            // Index of grain containing cell on back outer boundary (J=Jmax)
            int back_index  = grain_number+5;

            // Index of grain containing cell on top outer boundary (K=0)
            int top_index   = grain_number+3;

            // Index of grain containing cell on bottom outer boundary (K=Kmax)
            int bottom_index= grain_number+4;

            // Writing of comments
            bw.write("#   This file contains initial parameters of each cellular automaton (CA):");
            bw.newLine();
            bw.write("# thermal energy, mechanical energy, temperature, index of cluster containing CA, cluster type.");
            bw.newLine();

            // Writing of total number of cells
        //    bw1.write(cellNumber+"");
        //    bw1.newLine();

            boolean write_string;

            for (int k_index = 0; k_index < cell_number_K+2; k_index++)
            for (int j_index = 0; j_index < cell_number_J+2; j_index++)
            for (int i_index = 0; i_index < cell_number_I+2; i_index++)
            {
                write_string = true;

                cell_coordinates = calcCoordinates(i_index, j_index, k_index);

                if(write_string)if(i_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              left_index);
           //         bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(i_index==cell_number_I+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              right_index);
           //         bw1.write(right_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }


                if(write_string)if(j_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              front_index);
            //        bw1.write(front_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(j_index==cell_number_J+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              back_index);
            //        bw1.write(back_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(k_index==0)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              top_index);
            //        bw1.write(top_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)if(k_index==cell_number_K+1)
                {
                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature+" "+
                              bottom_index);
            //        bw1.write(bottom_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    write_string = false;
                }

                if(write_string)
                {
                    grain_index = br.readLine();

                    bw .write(init_heat_energy+" "+init_mech_energy+" "+init_temperature);

                    if((  k_index> 0)&(k_index< cell_number_K+1)&
                       (  j_index> 0)&(j_index<=Math.round(cell_number_J*0.3))&
                       (  i_index!=0)&(i_index!=cell_number_I+1)&
                       (((i_index+1) %(cell_number_I/(crack_number+1))==0)|
                        ( i_index    %(cell_number_I/(crack_number+1))==0)|
                        ((i_index-1) %(cell_number_I/(crack_number+1))==0))&
                        ((i_index+1) /(cell_number_I/(crack_number+1))==crack_number))//==crack_number-1))//
                    {
                        bw .write(" "+front_index);

                        if(show_grain_bounds == 1)
                            bw .write(" "+Common.OUTER_CELL);
                        // bw1.write(left_index+" "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                    }
                    else
                    {
                        string_counter++;
                        
                        if(show_grain_bounds == 1)
                        {
                            bw .write(" "+grain_index);
                         // bw1.write(br1.readLine());
                        }
                        else
                        {
                            st = new StringTokenizer(grain_index);

                            if(st.hasMoreTokens())
                                bw .write(" "+st.nextToken());

                         // bw1.write(br1.readLine());
                        }
                    }
                }
                else
                {
                    if(show_grain_bounds == 1)
                        bw .write(" "+Common.OUTER_CELL);
                }

                bw.newLine();
                bw.flush();
         //       bw1.newLine();
         //       bw1.flush();
            }

            //TEST
            completed_steps.setText(completed_steps.getText()+"\nNumber of inner cells: "+string_counter);

            br.close();
            bw.close();
       //     bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file);
       //     completed_steps.setText(completed_steps.getText()+"\nName of created file: "+init_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with initial conditions of task (chessboard-like distribution of
     * capacity or conductivity on surface).
     */
    public void createInitCondChessBoardFile()
    {
        // Sizes of square area with high heat conductivity
        int square_size_X = 10;
        int square_size_Y = 10;
        int square_size_Z =  1;

        int square_index_i;
        int square_index_j;
        int square_index_k;

        // Limits on indices I and J of cells with "chessboard-like"
        // distribution of capacities (conductivities)
        int square_i_min = 0;// cell_number_I/2 - square_size_X/2;
        int square_i_max = cell_number_I - 1;// /2 + square_size_X/2;
        int square_j_min = 0;// cell_number_J/2 - square_size_Y/2;
        int square_j_max = cell_number_J - 1;// /2 + square_size_Y/2;

        // Coordinates X and Y of centres of cells with high heat conductivity
        double centreCA_x, centreCA_y, centreCA_z;

        // Heat conductivity of cell in the square area
        Double squareHeatCond;

        // Heat conductivity of cell in the square area
        Double squareHeatCap;

        // Heat conductivity of cell in bulk
        Double bulkHeatCond = 401.0;

        // Heat capacity of cell in bulk
        Double bulkHeatCap  = 390.0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(heat_conduct_file));

            Integer _stringNumber = new Integer(cellNumber);

            bw.write(_stringNumber.toString());
            bw.newLine();

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                bw.write("30.0 0.0 300.0 2.0 1.0 2.0 2.0 1.0 2.0 ");

                centreCA_x = i_index+0.5;
                centreCA_y = j_index+0.5;
                centreCA_z = k_index+0.5;

          //      if((i_index > 0) & (i_index < cell_number_I - 1))
                if(k_index == 1)
                {
          //        square_index_i = (i_index - 1)/square_size_X;

                    square_index_i = i_index/square_size_X;
                    square_index_j = j_index/square_size_Y;
                    square_index_k = k_index/square_size_Z;

                    if((square_index_i + square_index_j + square_index_k)%2 == 0)
                    {
                        squareHeatCond = bulkHeatCond*(1.0 + 19.0*
                  //      Math.sin(Math.PI*((i_index - 1) % square_size_X + 0.5)/square_size_X)*
                          Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                          Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y)*
                          Math.sin(Math.PI*(k_index % square_size_Z + 0.5)/square_size_Z));
                    }
                    else
                    {
                        squareHeatCond = bulkHeatCond*(1.0 - 0.95*
                   //      Math.sin(Math.PI*((i_index - 1) % square_size_X + 0.5)/square_size_X)*
                           Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                           Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y)*
                           Math.sin(Math.PI*(k_index % square_size_Z + 0.5)/square_size_Z));
                    }

                    bw.write(squareHeatCond.toString() + " 390.0 8940.0");
                    bw1.write(centreCA_x+ " " + centreCA_y+ " " + squareHeatCond.toString());

                    bw1.newLine();
                    bw1.flush();
                }
                else
                {
                    bw.write(bulkHeatCond.toString() + " 390.0 8940.0");
             //     bw1.write(centreCA_x+ " " + centreCA_y+ " " + bulkHeatCap.toString());
                }

             /*
                bw.write(bulkHeatCap.toString() + " 8940.0");
                bw1.write(centreCA_x+ " " + centreCA_y+ " " + centreCA_z+" "+ bulkHeatCap.toString());
             */

                bw.write(" 2.0E-5 0.01");
                bw.newLine();
             //   bw1.newLine();
                bw.flush();
             //   bw1.flush();
            }

            bw.close();
            bw1.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with initial conditions of task (chessboard-like distribution of
     * density, capacity and conductivity on surface).
     */
    public void createInitCondChessBoardFile1()
    {
        // Sizes of square area with high heat conductivity
        int square_size_X = cell_number_I/5;
        int square_size_Y = cell_number_J/5;

        int square_index_i;
        int square_index_j;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));

            Integer _stringNumber = new Integer(cellNumber);

            bw.write(_stringNumber.toString());
            bw.newLine();

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                bw.write("30.0 0.0 300.0 2.0 1.0 2.0 2.0 1.0 2.0 ");

                if(k_index < surf_thickness/2+1)
                {
                    bw.write(surfHeatConduct+" "+surfHeatCapacity+" "+surfDensity+" "+
                             surfHeatExpCoeff+" "+surfUltimateStrain);
                }

                if((k_index >= surf_thickness/2+1) & (k_index < surf_thickness+1))
                {
                    square_index_i = i_index/square_size_X;
                    square_index_j = j_index/square_size_Y;

                    if((square_index_i + square_index_j)%2 == 0)
                    {
                        bw.write(surfHeatConduct+" "+surfHeatCapacity+" "+surfDensity+" "+
                                 surfHeatExpCoeff+" "+surfUltimateStrain);
                    }
                    else
                    {
                        bw.write(bulkHeatConduct+" "+bulkHeatCapacity+" "+bulkDensity+" "+
                                 bulkHeatExpCoeff+" "+bulkYieldStrain);
                    }
                }

                if(k_index >= surf_thickness+1)
                {
                    bw.write(bulkHeatConduct+" "+bulkHeatCapacity+" "+bulkDensity+" "+
                             bulkHeatExpCoeff+" "+bulkYieldStrain);
                }

                bw.newLine();
                bw.flush();
            }
            bw.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

     /** The method creates file with initial conditions of task (chessboard-like sinusoidal distribution
     * of density, capacity and conductivity on surface).
     */
    public void createInitCondChessBoardFile2()
    {
        // Sizes of square area with high heat conductivity
        int square_size_X = cell_number_I/5;
        int square_size_Y = cell_number_J/5;

        int square_index_i;
        int square_index_j;

        double squareHeatCond, squareHeatCapacity, squareDensity;

        double centreCA_x;
        double centreCA_y;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(heat_conduct_file));

            Integer _stringNumber = new Integer(cellNumber);

            bw.write(_stringNumber.toString());
            bw.newLine();

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                bw.write("30.0 0.0 300.0 2.0 1.0 2.0 2.0 1.0 2.0 ");

                square_index_i = i_index/square_size_X;
                square_index_j = j_index/square_size_Y;

                centreCA_x = i_index+0.5;
                centreCA_y = j_index+0.5;

                if(k_index < surf_thickness+1)
                {
                    if((square_index_i + square_index_j)%2 == 0)
                    {
                        squareHeatCond =     0.5*(bulkHeatConduct + surfHeatConduct) +
                                             0.5*(bulkHeatConduct - surfHeatConduct)*
                          Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                          Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y);

                        squareHeatCapacity = 0.5*(bulkHeatCapacity + surfHeatCapacity) +
                                             0.5*(bulkHeatCapacity - surfHeatCapacity)*
                          Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                          Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y);

                        squareDensity =      0.5*(bulkDensity + surfDensity) +
                                             0.5*(bulkDensity - surfDensity)*
                          Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                          Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y);
                    }
                    else
                    {
                        squareHeatCond =     0.5*(bulkHeatConduct + surfHeatConduct) -
                                             0.5*(bulkHeatConduct - surfHeatConduct)*
                           Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                           Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y);

                        squareHeatCapacity = 0.5*(bulkHeatCapacity + surfHeatCapacity) -
                                             0.5*(bulkHeatCapacity - surfHeatCapacity)*
                          Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                          Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y);

                        squareDensity =      0.5*(bulkDensity + surfDensity) -
                                             0.5*(bulkDensity - surfDensity)*
                          Math.sin(Math.PI*(i_index % square_size_X + 0.5)/square_size_X)*
                          Math.sin(Math.PI*(j_index % square_size_Y + 0.5)/square_size_Y);
                    }

                    bw.write(squareHeatCond+" "+squareHeatCapacity+" "+squareDensity);

                    if(k_index == 0)
                    {
                        bw1.write(centreCA_x + " " + centreCA_y + " " + squareHeatCond);
                        bw1.newLine();
                        bw1.flush();
                    }
                }
                else
                {
                    bw.write(bulkHeatConduct+" "+bulkHeatCapacity+" "+bulkDensity);

                    if(k_index == 0)
                    {
                        bw1.write(centreCA_x + " " + centreCA_y + " " + bulkHeatConduct);
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.write(bulkHeatExpCoeff+" "+bulkYieldStrain);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            bw1.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with information on initial grain structure of specimen:
     * specimen is divided on grains with parallelepiped form (task 1).
     */
    public void createInitGrainStructureFile()
    {
        // Sizes of grain
        int grain_size_X = 20;
        int grain_size_Y = 20;
        int grain_size_Z =  1;

        // Triple indices of grain
        int grain_index_i;
        int grain_index_j;
        int grain_index_k;

        // Single index of grain
        int grainID;

        // Printing of data about every z-plane of cells in the file.
        double radius = 1.0;

        // Coordinates of cell centre
        double coord_X;
        double coord_Y;

        double cell_mech_energy;
        double mech_energy = 2.4E-13;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
            BufferedWriter bw1  = new BufferedWriter(new FileWriter(grain_struct_file));

            Integer _stringNumber = new Integer(cellNumber);

            bw.write(_stringNumber.toString());
            bw.newLine();

            // Cycle over cells
            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Calculation of indices of grain containing cell
                grain_index_i = i_index/grain_size_X;
                grain_index_j = j_index/grain_size_Y;
                grain_index_k = k_index/grain_size_Z;

                // Calculation of single index of grain containing cell
                grainID = calcSingleIndex(grain_index_i, grain_index_j, grain_index_k, cell_number_I/grain_size_X,
                                          cell_number_J/grain_size_Y, cell_number_K/grain_size_Z);

                if((grainID/11>3)&(grainID/11<7)&(grainID%11>3)&(grainID%11<7))
                    cell_mech_energy = mech_energy*1.0E2;
                else
                    cell_mech_energy = mech_energy;

                if(k_index % 2 == 0)
                {
                    if(i_index % 2 == 0)
                    {
                        coord_X = radius*(1 + i_index*Math.sqrt(3));
                        coord_Y = radius*(1 + j_index*2);
                    }
                    else
                    {
                        coord_X = radius*(1 + i_index*Math.sqrt(3));
                        coord_Y = radius*(2 + j_index*2);
                    }
                }
                else
                {
                    if(i_index % 2 == 0)
                    {
                        coord_X = radius*(1 + 1/Math.sqrt(3) + i_index*Math.sqrt(3));
                        coord_Y = radius*(2 + j_index*2);
                    }
                    else
                    {
                        coord_X = radius*(1 + Math.sqrt(3) + 1/Math.sqrt(3)+ i_index*Math.sqrt(3));
                        coord_Y = radius*(1 + j_index*2);
                    }
                }

                bw.write("0.0 "+cell_mech_energy+" 300.0 401.0 390.0 8940.0 2.0E-5 0.01 "+grainID);
                bw1.write(coord_X+" "+coord_Y+" "+grainID);

                bw.newLine();
                bw1.newLine();

                bw.flush();
                bw1.flush();
            }

            bw.close();
            bw1.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with information on initial grain structure of specimen:
     * hexagonal grain in centre of specimen and 6 adjacent grains (task 2).
     */
    public void createInitGrainStructureFile2()
    {
        // Size of specimen along axis X
//        double spec_size_X = cell_number_I*Math.sqrt(3)/2;

        // Single index of grain
        int grainID = 0;

        // Radius of central grain
        double central_grain_radius = 30.0;

        // Radius of cell
        double radius = 0.5;

        // Coordinates of cell centre
        double coord_X;
        double coord_Y;

        // Mechanical energy of cell in central grain with ID=0
        double mech_energy = 0.0;//2.4E-14;

        // Mechanical energy of cell in central grain
        double cell_mech_energy   = 0.0;//0.01*mech_energy;

        // Mechanical energy of cell in 1st boundary grain
        double cell_mech_energy_1 = 0.0;//0.01*mech_energy*(0.5+Math.random());

        // Mechanical energy of cell in 2nd boundary grain
        double cell_mech_energy_2 = 0.0;//0.01*mech_energy*(0.5+Math.random());

        // Mechanical energy of cell in 3rd boundary grain
        double cell_mech_energy_3 = 0.0;//0.01*mech_energy*(0.5+Math.random());

        // Mechanical energy of cell in 4th boundary grain
        double cell_mech_energy_4 = 0.0;//0.01*mech_energy*(0.5+Math.random());

        // Mechanical energy of cell in 5th boundary grain
        double cell_mech_energy_5 = 0.0;//0.01*mech_energy*(0.5+Math.random());

        // Mechanical energy of cell in 6th boundary grain
        double cell_mech_energy_6 = 0.0;//0.01*mech_energy*(0.5+Math.random());

        // Temperature of cell
        double temperature = 0;

        // Temperature of cell on specimen boundary
        double bound_temperature = 300.0;

        // Temperature of cell in outer grain
        double outer_grain_temperature = 300.0;

        // Temperature of cell in central grain
        double central_grain_temperature = 300.0;

        // Heat energy of cell
        double heat_energy = 0.0;

        int cell_counter = 0;
        int central_grain_cells_number = 0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(grain_struct_file));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(bound_cond_file));

            Integer _stringNumber = new Integer(cellNumber);

            bw.write(_stringNumber.toString());
            bw.newLine();

            // Cycle over cells
            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                if(k_index % 2 == 0)
                {
                    if(i_index % 2 == 0)
                    {
                        coord_X = radius*(1 + i_index*Math.sqrt(3));
                        coord_Y = radius*(1 + j_index*2);
                    }
                    else
                    {
                        coord_X = radius*(1 + i_index*Math.sqrt(3));
                        coord_Y = radius*(2 + j_index*2);
                    }
                }
                else
                {
                    if(i_index % 2 == 0)
                    {
                        coord_X = radius*(1 + 1/Math.sqrt(3) + i_index*Math.sqrt(3));
                        coord_Y = radius*(2 + j_index*2);
                    }
                    else
                    {
                        coord_X = radius*(1 + Math.sqrt(3) + 1/Math.sqrt(3)+ i_index*Math.sqrt(3));
                        coord_Y = radius*(1 + j_index*2);
                    }
                }

                // Distribution of cells on grains.
                if(2*coord_X/Math.sqrt(3) >= 0.5*cell_number_I)
                if(2*coord_X/Math.sqrt(3) <=-coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) >= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3))+central_grain_radius*Math.sqrt(3))
                {
                    grainID = 1;
                    cell_mech_energy = cell_mech_energy_1;
                    temperature = outer_grain_temperature;
                }

                if(2*coord_X/Math.sqrt(3) >=-coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) >= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) >= 0.5*cell_number_I + 0.5*central_grain_radius*Math.sqrt(3))
                {
                    grainID = 2;
                    cell_mech_energy = cell_mech_energy_2;
                    temperature = outer_grain_temperature;
                }

                if(2*coord_X/Math.sqrt(3) <= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) >= 0.5*cell_number_I)
                if(2*coord_X/Math.sqrt(3) >= -coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)) + central_grain_radius*Math.sqrt(3))
                {
                    grainID = 3;
                    cell_mech_energy = cell_mech_energy_3;
                    temperature = outer_grain_temperature;
                }

                if(2*coord_X/Math.sqrt(3) <= 0.5*cell_number_I)
                if(2*coord_X/Math.sqrt(3) >=-coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) <= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3)) - central_grain_radius*Math.sqrt(3))
                {
                    grainID = 4;
                    cell_mech_energy = cell_mech_energy_4;
                    temperature = outer_grain_temperature;
                }

                if(2*coord_X/Math.sqrt(3) <=-coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) <= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) <= 0.5*cell_number_I - 0.5*central_grain_radius*Math.sqrt(3))
                {
                    grainID = 5;
                    cell_mech_energy = cell_mech_energy_5;
                    temperature = outer_grain_temperature;
                }

                if(2*coord_X/Math.sqrt(3) >= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3)))
                if(2*coord_X/Math.sqrt(3) <= 0.5*cell_number_I)
                if(2*coord_X/Math.sqrt(3) <= -coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)) - central_grain_radius*Math.sqrt(3))
                {
                    grainID = 6;
                    cell_mech_energy = cell_mech_energy_6;
                    temperature = outer_grain_temperature;
                }

                if(2*coord_X/Math.sqrt(3) <= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3))+central_grain_radius*Math.sqrt(3))
                if(2*coord_X/Math.sqrt(3) <= 0.5*cell_number_I + 0.5*central_grain_radius*Math.sqrt(3))
                if(2*coord_X/Math.sqrt(3) <= -coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)) + central_grain_radius*Math.sqrt(3))
                if(2*coord_X/Math.sqrt(3) >= coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1-Math.sqrt(3)) - central_grain_radius*Math.sqrt(3))
                if(2*coord_X/Math.sqrt(3) >= 0.5*cell_number_I - 0.5*central_grain_radius*Math.sqrt(3))
                if(2*coord_X/Math.sqrt(3) >= -coord_Y*Math.sqrt(3) + 0.5*cell_number_I*(1+Math.sqrt(3)) - central_grain_radius*Math.sqrt(3))
                {
                    grainID = 0;
                    cell_mech_energy = mech_energy;
                    temperature = central_grain_temperature;
                    central_grain_cells_number++;
                }

                // Creation of file with information on boundary conditions
                if(grainID == 0)
                {
                    bw2.write(cell_counter+" "+heat_energy+" "+cell_mech_energy+" "+temperature);
                    bw2.newLine();
                }

                if((i_index == 0)||(i_index == cell_number_I-1)||
                   (j_index == 0)||(j_index == cell_number_J-1))
                {
                    bw2.write(cell_counter+" "+heat_energy+" "+cell_mech_energy+" "+bound_temperature);
                    bw2.newLine();
                }

                if(cell_counter == cell_number_K*cell_number_J*cell_number_I-1)
                {
                    bw2.write(((Integer)(central_grain_cells_number+2*(cell_number_I+cell_number_J)-4)).toString());
                    bw2.newLine();
                }

                cell_counter++;

             // bw.write("0.0 "+cell_mech_energy+" 300.0 401.0 390.0 8940.0 2.0E-5 0.01 "+grainID);
                bw.write(heat_energy+" "+cell_mech_energy+" "+temperature+" "+grainID);
                bw1.write(coord_X+" "+coord_Y+" "+grainID);
            //  bw1.write(coord_X+" "+coord_Y+" "+temperature);

                bw.newLine();
                bw1.newLine();

                bw.flush();
                bw1.flush();
                bw2.flush();
            }

            bw.close();
            bw1.close();
            bw2.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with initial conditions of task; specimen consists of 3 layers:
     * 1. Al2O3 (5 CA layers); 2. Cu - Al2O3: stripes in z-section, zigzag in y-section (5 CA layers);
     * 3. Cu (20 CA layers).
     */
    public void createInitCondStripesFile()
    {
        // Width of stripe with high heat conductivity
        int stripe_size_X = cell_number_I/5;

        int stripe_index_i;

        int grain_index = 0;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));

            Integer _stringNumber = new Integer(cellNumber);

            bw.write(_stringNumber.toString());
            bw.newLine();

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                bw.write("30.0 0.0 300.0 2.0 1.0 2.0 2.0 1.0 2.0 ");

            //    bw.write(i_index+".5 "+k_index+".5 ");

                if(k_index < surf_thickness/2+1)
                {
                    bw.write(surfHeatConduct+" "+surfHeatCapacity+" "+surfDensity+" "+
                             surfHeatExpCoeff+" "+surfUltimateStrain+" "+surfModElast);

                    grain_index = 0;

                    bw.write(" "+grain_index);
                }

                if((k_index >= surf_thickness/2+1) & (k_index < surf_thickness+1))
                {
                    stripe_index_i = i_index/(stripe_size_X/2);

                    if(stripe_index_i%2 == 0)
                    {
                        if(stripe_size_X/2 - 1 - (i_index%(stripe_size_X/2)) < surf_thickness - k_index)
                            bw.write(surfHeatConduct+" "+surfHeatCapacity+" "+surfDensity+" "+
                                     surfHeatExpCoeff+" "+surfUltimateStrain+" "+surfModElast);
                        else
                            bw.write(layerHeatConduct+" "+layerHeatCapacity+" "+layerDensity+" "+
                                     layerHeatExpCoeff+" "+layerYieldStrain+" "+layerModElast);
                    }
                    else
                    {
                        if(i_index%(stripe_size_X/2) <= surf_thickness - k_index)
                            bw.write(surfHeatConduct+" "+surfHeatCapacity+" "+surfDensity+" "+
                                     surfHeatExpCoeff+" "+surfUltimateStrain+" "+surfModElast);
                        else
                            bw.write(layerHeatConduct+" "+layerHeatCapacity+" "+layerDensity+" "+
                                     layerHeatExpCoeff+" "+layerYieldStrain+" "+layerModElast);
                    }

                    grain_index = 1;

                    bw.write(" "+grain_index);
                }

                if(k_index >= surf_thickness+1)
                {
                    bw.write(bulkHeatConduct+" "+bulkHeatCapacity+" "+bulkDensity+" "+
                             bulkHeatExpCoeff+" "+bulkYieldStrain+" "+bulkModElast);

                    grain_index = 2;

                    bw.write(" "+grain_index);
                }

                bw.newLine();
                bw.flush();
            }
            bw.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with initial conditions of task; specimen consists of 3 layers:
     * 1. Al2O3 (5 CA layers);
     * 2. Cu - Al2O3: pyramides of Al2O3 with base in 6th CA layer and vertex in 10th CA layer (5 CA layers);
     * 3. Cu (20 CA layers).
     */
    public void createInitCondPyramidesFile()
    {
        // Width of stripe with high heat conductivity
        int square_size_X = cell_number_I/5;
        int square_size_Y = cell_number_I/5;

        try
        {
            BufferedWriter bw  = new BufferedWriter(new FileWriter(init_cond_file));

            Integer _stringNumber = new Integer(cellNumber);

            bw.write(_stringNumber.toString());
            bw.newLine();

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                bw.write("30.0 0.0 300.0 2.0 1.0 2.0 2.0 1.0 2.0 ");

            //    bw.write(i_index+".5 "+k_index+".5 ");

                if(k_index < surf_thickness/2+1)
                {
                    bw.write(surfHeatConduct+" "+surfHeatCapacity+" "+surfDensity+" "+
                             surfHeatExpCoeff+" "+surfUltimateStrain);
                }

                if((k_index >= surf_thickness/2+1) & (k_index < surf_thickness+1))
                {
                        if((Math.abs(square_size_X/2 - (i_index%square_size_X)) <= Math.abs(surf_thickness - k_index))&
                           (Math.abs(square_size_Y/2 - (j_index%square_size_Y)) <= Math.abs(surf_thickness - k_index)))
                            bw.write(surfHeatConduct+" "+surfHeatCapacity+" "+surfDensity+" "+
                                     surfHeatExpCoeff+" "+surfUltimateStrain);
                        else
                     //      bw.write(bulkHeatConduct+" "+bulkHeatCapacity+" "+bulkDensity+" "+
                         //            bulkHeatExpCoeff+" "+bulkYieldStrain);

                            bw.write(layerHeatConduct+" "+layerHeatCapacity+" "+layerDensity+" "+
                                     layerHeatExpCoeff+" "+layerYieldStrain);
                }

                if(k_index >= surf_thickness+1)
                {
                    bw.write(bulkHeatConduct+" "+bulkHeatCapacity+" "+bulkDensity+" "+
                             bulkHeatExpCoeff+" "+bulkYieldStrain);
                }

                bw.newLine();
                bw.flush();
            }
            bw.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with boundary conditions of task for 3D specimen.
     */
    public void createBoundCondFile3D(String _bound_cond_file)
    {
        // Index of boundary cell
        int bound_cell_index = -1;

        // Number of boundary cells
 //       Integer bound_cell_number = 2*cell_number_K*(cell_number_J+cell_number_I-2);

        Integer bound_cell_number = 2*cell_number_K*cell_number_J;

        // Temperature of boundary cell
        double boundary_temperature = 300.0;

        // Influx of heat energy to boundary cell
        double heat_energy_influx = 0.0;

        // Strain of boundary cell per time step
        double bound_strain = -1.0E-11;

        // Coordinates of boundary cell (in sizes of CA)
        double[] boundary_cell_coordinates;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(_bound_cond_file));
            BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_file_geometry));

            bw.write(bound_cell_number.toString());
            bw.newLine();

            bw1.write(bound_cell_number.toString());
            bw1.newLine();

            for(int cell_counter_K = 0; cell_counter_K < cell_number_K; cell_counter_K++)
            for(int cell_counter_J = 0; cell_counter_J < cell_number_J; cell_counter_J++)
            for(int cell_counter_I = 0; cell_counter_I < cell_number_I; cell_counter_I++)
            if ((cell_counter_I==0)|(cell_counter_I==cell_number_I-1))
            //   |(cell_counter_J==0)|(cell_counter_J==cell_number_J-1))
            {
                boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                bound_cell_index = cell_counter_K*cell_number_J*cell_number_I+
                                   cell_counter_J*cell_number_I + cell_counter_I;

                bw.write(bound_cell_index+" "+heat_energy_influx+" "+
                         bound_strain+" "+boundary_temperature);
                bw.newLine();
                bw.flush();

                bw1.write(boundary_cell_coordinates[0]+" "+
                          boundary_cell_coordinates[1]+" "+
                          boundary_cell_coordinates[2]+" "+
                          heat_energy_influx+" "+bound_strain+" "+boundary_temperature);
                bw1.newLine();
                bw1.flush();
            }

            bw.close();
            bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+_bound_cond_file);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+ bound_cond_file_geometry);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with boundary conditions of task for 3D specimen.
     */
    public void createBoundCondFile3D_2()
    {
        // Index of boundary cell
        int bound_cell_index = -1;

   //   Integer bound_cell_number = 2*cell_number_J + 2*cell_number_I - 3;
        Integer bound_cell_number = 2*(cell_number_I*cell_number_J +
                                       cell_number_J*cell_number_K +
                                       cell_number_K*cell_number_I);

        int bound_cells_number = 0;

        // Radius of "hot" circle at plane "z=0"
        double radius = (cell_number_I)*0.2;

        completed_steps.setText(completed_steps.getText()+"\n"+radius);

        // Temperature of the "hot" circle
        double high_temperature = 1300.0;

        double bound_stress = 10000.0;

        // Temperature of surface
        double low_temperature  =  300.0;

        // Coordinates of current cell in hexagonal close packing
        double[] cell_coordinates = new double[3];

        // Calculation of coordinates of cell in centre of plane "z=0"
        double[] centre_coordinates = calcCoordinates(cell_number_I/2, cell_number_J/2, 0);

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(bound_cond_file));
            BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_file_geometry));
       /*
            bw .write(bound_cell_number.toString());
            bw1.write(bound_cell_number.toString());
            bw .newLine();
            bw1.newLine();
        */
            for(int cell_counter_K = 0; cell_counter_K < cell_number_K; cell_counter_K++)
            for(int cell_counter_J = 0; cell_counter_J < cell_number_J; cell_counter_J++)
            for(int cell_counter_I = 0; cell_counter_I < cell_number_I; cell_counter_I++)
            if ((cell_counter_I == 0)|(cell_counter_I == cell_number_I-1)|
                (cell_counter_J == 0)|(cell_counter_J == cell_number_J-1)|
                (cell_counter_K == 0)|(cell_counter_K == cell_number_K-1))
            {
                // Calculation of single index of cell
                bound_cell_index = calcSingleIndex(cell_counter_I, cell_counter_J, cell_counter_K,
                                                   cell_number_I,  cell_number_J,  cell_number_K);

                // Calculation of coordinates of cell
                cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                if((cell_counter_K == 0)&
                   (Math.pow(cell_coordinates[0] - centre_coordinates[0], 2) +
                    Math.pow(cell_coordinates[1] - centre_coordinates[1], 2) < radius*radius))
                {
                     //TEST
                    completed_steps.setText(completed_steps.getText()+Math.pow(cell_coordinates[0] - centre_coordinates[0], 2) +
                                       Math.pow(cell_coordinates[1] - centre_coordinates[1], 2));

                    // Conditions of thermal task
                  //  bw .write(bound_cell_index+" 0.0 0.0 "+high_temperature);
                  //  bw1.write(Math.round(high_temperature)+" "+cell_coordinates[0]+
                  //            " "+cell_coordinates[1]+" "+cell_coordinates[2]);

                    // Conditions of mechanical task
                    bw .write(bound_cell_index+" 0.0 "+bound_stress+" "+low_temperature);
                    bw1.write(Math.round(bound_stress)+" "+cell_coordinates[0]+
                              " "+cell_coordinates[1]+" "+cell_coordinates[2]);

                    bw .newLine();
                    bw1.newLine();
                    bw .flush();
                    bw1.flush();

                    bound_cells_number++;
                }
                else
                {
                    // Conditions of thermal task
                //    bw .write(bound_cell_index+" 0.0 0.0 "+low_temperature);
                 //   bw1.write(Math.round(low_temperature)+" "+cell_coordinates[0]+
                   //           " "+cell_coordinates[1]+" "+cell_coordinates[2]);


                }

                if(cell_counter_K == cell_number_K-1)
                {
                    // Conditions of mechanical task
                    bw .write(bound_cell_index+" 0.0 "+(bound_stress*bound_cells_number/(cell_number_I*cell_number_J))+" "+low_temperature);
                    bw1.write((bound_stress*bound_cells_number/(cell_number_I*cell_number_J))+" "+cell_coordinates[0]+
                              " "+cell_coordinates[1]+" "+cell_coordinates[2]);

                    bw .newLine();
                    bw1.newLine();
                    bw .flush();
                    bw1.flush();
                }



                //bound_cells_number++;
            }

            bw .write(bound_cells_number+"");
            bw1.write(bound_cells_number+"");
            bw .flush();
            bw1.flush();
            bw .close();
            bw1.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with boundary conditions of task for 3D specimen:
     * thermal or mechanical action on circle at certain specimen facet is simulated.
     */
    private void createBoundCondCircleFile3D()
    {
        // Index of boundary cell
        int bound_cell_index = -1;
        
        // The number of effected cells
        int bound_cells_number = 0;

        // Radius of circle
        double radius = 0;
        
        // radius = 0.15*cell_number_K*2*cell_radius;
         radius = 5*cell_radius;//12*cell_radius;//
        // radius = cell_radius*0.5*(cell_number_K+cell_number_J+cell_number_I)/3.0;
        
        // Strain/stress in the circle
        double mech_param_left   = 0;
        double mech_param_front  = 0;
        double mech_param_top    = 0;
        double mech_param_bottom = 0;
        double mech_param_back   = 0;
        double mech_param_right  = 0;
        
        // Heat energy influx or temperature in the circle
        double heat_param_left = 0;
        double heat_param_front = 0;
        double heat_param_top = 0;
        double heat_param_bottom = 0;
        double heat_param_back = 0;
        double heat_param_right = 0;
        
        // Distanse from cell to centre of circle
        double dist_from_centre;
        
        completed_steps.setText(completed_steps.getText()+"\nCircle radius: "+radius);
        System.out.println("Circle radius: "+radius);
        
        try
        {
            TextTableFileReader reader = new TextTableFileReader(bound_params_file);
            StringVector table = reader.convertToTable();

            mech_param_left   = (new Double(table.getCell(0, 0))).doubleValue();
            mech_param_front  = (new Double(table.getCell(1, 0))).doubleValue();
            mech_param_top    = (new Double(table.getCell(2, 0))).doubleValue();
            mech_param_bottom = (new Double(table.getCell(3, 0))).doubleValue();
            mech_param_back   = (new Double(table.getCell(4, 0))).doubleValue();
            mech_param_right  = (new Double(table.getCell(5, 0))).doubleValue();

            heat_param_left   = (new Double(table.getCell(0, 1))).doubleValue();
            heat_param_front  = (new Double(table.getCell(1, 1))).doubleValue();
            heat_param_top    = (new Double(table.getCell(2, 1))).doubleValue();
            heat_param_bottom = (new Double(table.getCell(3, 1))).doubleValue();
            heat_param_back   = (new Double(table.getCell(4, 1))).doubleValue();
            heat_param_right  = (new Double(table.getCell(5, 1))).doubleValue();
                      
            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);
            System.out.println("Name of read file: "+bound_params_file);

            System.out.println("mech_param_top: "+mech_param_top);
            System.out.println("heat_param_top: "+heat_param_top);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            System.out.println("ERROR: "+io_ex);
        }
        
        // Coordinates of current cell in hexagonal close packing
        double[] cell_coordinates = new double[3];
        
        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_left   = calcCoordinates(       0       , cell_number_J/2, cell_number_K/2);
        
        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_front  = calcCoordinates(cell_number_I/2,        0       , cell_number_K/2);
        
        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_top    = calcCoordinates(cell_number_I/2, cell_number_J/2,        0       );
        
        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_bottom = calcCoordinates(cell_number_I/2, cell_number_J/2, cell_number_K  );
        
        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_back   = calcCoordinates(cell_number_I/2, cell_number_J  , cell_number_K/2);
        
        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_right  = calcCoordinates(cell_number_I  , cell_number_J/2, cell_number_K/2);

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(bound_cond_file));
            BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_file_geometry));

            bw.write("#   This file contains parameters of each outer boundary cellular automaton (CA):");
            bw.newLine();
            bw.write("# index of CA, mechanical parameter (strain or stress of CA), ");
            bw.newLine();
            bw.write("# thermal parameter (temperature or thermal energy influx).");
            bw.newLine();

            bw1.write("#   This file contains information about outer boundary cellular automata (CA):");
            bw1.newLine();
            bw1.write("# in 1st string - number of  outer boundary CA,");
            bw1.newLine();
            bw1.write("# in each further string - parameters of each outer boundary CA:");
            bw1.newLine();
            bw1.write("# 3 coordinates of CA, mechanical parameter (strain or stress) of CA,");
            bw1.newLine();
            bw1.write("# thermal parameter (temperature or thermal energy influx).");
            bw1.newLine();

            //TEST
            bw1.write("# Circle radius: "+radius);
            bw1.newLine();

            boolean write_to_file = true;
            
            for(int cell_counter_K = 0; cell_counter_K < cell_number_K+2; cell_counter_K++)
            for(int cell_counter_J = 0; cell_counter_J < cell_number_J+2; cell_counter_J++)
            for(int cell_counter_I = 0; cell_counter_I < cell_number_I+2; cell_counter_I++)
            if ((cell_counter_I == 0)|(cell_counter_I == cell_number_I+1)|
                (cell_counter_J == 0)|(cell_counter_J == cell_number_J+1)|
                (cell_counter_K == 0)|(cell_counter_K == cell_number_K+1))
            {
              write_to_file = true;

              // Calculation of single index of cell
              bound_cell_index = calcSingleIndex(cell_counter_I, cell_counter_J, cell_counter_K,
                                                 cell_number_I+2,  cell_number_J+2,  cell_number_K+2);

              // Calculation of coordinates of cell
              cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

              // Conditions on left facet
              if(write_to_file&(cell_counter_I == 0))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[1] - centre_coordinates_left[1], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_left[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. Y = "+cell_coordinates[1]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);
                    
                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_left+" "+heat_param_left);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                   mech_param_left+" "+heat_param_left);
                }
              }

              // Conditions on right facet
              if(write_to_file&(cell_counter_I == cell_number_I+1))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[1] - centre_coordinates_right[1], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_right[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. Y = "+cell_coordinates[1]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_right+" "+heat_param_right);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                   mech_param_right+" "+heat_param_right);
                }
              }

              // Conditions on front facet
              if(write_to_file&(cell_counter_J == 0))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_front[0], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_front[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_front+" "+heat_param_front);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                   mech_param_front+" "+heat_param_front);
                }
              }

              // Conditions on back facet
              if(write_to_file&(cell_counter_J == cell_number_J+1))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_back[0], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_back[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_back+" "+heat_param_back);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                   mech_param_back+" "+heat_param_back);
                }
              }

              // Conditions on top facet
              if(write_to_file&(cell_counter_K == 0))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_top[0], 2)+
                                             Math.pow(cell_coordinates[1] - centre_coordinates_top[1], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Y = "+cell_coordinates[1]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_top+" "+heat_param_top);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                   mech_param_top+" "+heat_param_top);
                }
              }

              // Conditions on bottom facet
              if(write_to_file&(cell_counter_K == cell_number_K+1))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_bottom[0], 2)+
                                             Math.pow(cell_coordinates[1] - centre_coordinates_bottom[1], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Y = "+cell_coordinates[1]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_bottom+" "+heat_param_bottom);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                   mech_param_bottom+" "+heat_param_bottom);
                }
              }

              if(write_to_file)
              {
                  // Writing of boundary conditions to files
                  bw .write(bound_cell_index+" 0 0");
                  bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" 0 0");
              }

              bw .newLine(); bw .flush();
              bw1.newLine(); bw1.flush();
            }

            bw1.write("# Number of effected cells in circles: "+bound_cells_number);
            bw1.newLine(); bw1.write("# Cell size: "+cell_size);
            bw .flush();   bw .close();
            bw1.flush();   bw1.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    
    /** The method creates file with boundary conditions of task for 3D specimen:
     * thermal action on circles at chosen specimen facets is simulated.
     */
    private void createBoundCondHotCircleFile3D()
    {
        // Index of boundary cell
        int bound_cell_index = -1;

        // The number of effected cells
        int bound_cells_number = 0;

        // Radius of circle
        double radius = 0;

        // radius = 0.15*cell_number_K*2*cell_radius;
        // radius = 10*cell_radius;
        // radius = cell_radius*0.5*(cell_number_K+cell_number_J+cell_number_I)/3.0;
        radius = 5*cell_radius;

        // Strain/stress in the circle
        double mech_param_left   = 0;
        double mech_param_front  = 0;
        double mech_param_top    = 0;
        double mech_param_bottom = 0;
        double mech_param_back   = 0;
        double mech_param_right  = 0;

        // Heat energy influx or temperature in the circle
        double heat_param_left   = 0;
        double heat_param_front  = 0;
        double heat_param_top    = 0;
        double heat_param_bottom = 0;
        double heat_param_back   = 0;
        double heat_param_right  = 0;

        // The temperature on facet out of circle
        double room_tmpr = 300.0;

        // Distanse from cell to centre of circle
        double dist_from_centre;

        completed_steps.setText(completed_steps.getText()+"\nCircle radius: "+radius);
        System.out.println("Circle radius: "+radius);

        try
        {
            TextTableFileReader reader = new TextTableFileReader(bound_params_file);
            StringVector table = reader.convertToTable();
            
            mech_param_left   = (new Double(table.getCell(0, 0))).doubleValue();
            mech_param_front  = (new Double(table.getCell(1, 0))).doubleValue();
            mech_param_top    = (new Double(table.getCell(2, 0))).doubleValue();
            mech_param_bottom = (new Double(table.getCell(3, 0))).doubleValue();
            mech_param_back   = (new Double(table.getCell(4, 0))).doubleValue();
            mech_param_right  = (new Double(table.getCell(5, 0))).doubleValue();
            
            heat_param_left   = (new Double(table.getCell(0, 1))).doubleValue();
            heat_param_front  = (new Double(table.getCell(1, 1))).doubleValue();
            heat_param_top    = (new Double(table.getCell(2, 1))).doubleValue();
            heat_param_bottom = (new Double(table.getCell(3, 1))).doubleValue();
            heat_param_back   = (new Double(table.getCell(4, 1))).doubleValue();
            heat_param_right  = (new Double(table.getCell(5, 1))).doubleValue();

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);
            System.out.println("Name of read file: "+bound_params_file);
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            System.out.println("ERROR: "+io_ex);
        }

        // Coordinates of current cell in hexagonal close packing
        double[] cell_coordinates = new double[3];

        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_left   = calcCoordinates(       0       , cell_number_J/2, cell_number_K/2);

        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_front  = calcCoordinates(cell_number_I/2,        0       , cell_number_K/2);

        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_top    = calcCoordinates(cell_number_I/2, cell_number_J/2,        0       );

        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_bottom = calcCoordinates(cell_number_I/2, cell_number_J/2, cell_number_K  );

        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_back   = calcCoordinates(cell_number_I/2, cell_number_J  , cell_number_K/2);

        // Calculation of coordinates of cell in centre of plane "y=0"
        double[] centre_coordinates_right  = calcCoordinates(cell_number_I  , cell_number_J/2, cell_number_K/2);

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(bound_cond_file));
            BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_file_geometry));

            bw.write("#   This file contains parameters of each outer boundary cellular automaton (CA):");
            bw.newLine();
            bw.write("# index of CA, mechanical parameter (strain or stress of CA), ");
            bw.newLine();
            bw.write("# thermal parameter (temperature or thermal energy influx).");
            bw.newLine();

            bw1.write("#   This file contains information about outer boundary cellular automata (CA):");
            bw1.newLine();
            bw1.write("# in 1st string - number of  outer boundary CA,");
            bw1.newLine();
            bw1.write("# in each further string - parameters of each outer boundary CA:");
            bw1.newLine();
            bw1.write("# 3 coordinates of CA, mechanical parameter (strain or stress) of CA,");
            bw1.newLine();
            bw1.write("# thermal parameter (temperature or thermal energy influx).");
            bw1.newLine();

            //TEST
            bw1.write("# Circle radius: "+radius);
            bw1.newLine();

            boolean write_to_file = true;

            for(int cell_counter_K = 0; cell_counter_K < cell_number_K+2; cell_counter_K++)
            for(int cell_counter_J = 0; cell_counter_J < cell_number_J+2; cell_counter_J++)
            for(int cell_counter_I = 0; cell_counter_I < cell_number_I+2; cell_counter_I++)
            if ((cell_counter_I == 0)|(cell_counter_I == cell_number_I+1)|
                (cell_counter_J == 0)|(cell_counter_J == cell_number_J+1)|
                (cell_counter_K == 0)|(cell_counter_K == cell_number_K+1))
            {
              write_to_file = true;

              // Calculation of single index of cell
              bound_cell_index = calcSingleIndex(cell_counter_I, cell_counter_J, cell_counter_K,
                                                 cell_number_I+2,  cell_number_J+2,  cell_number_K+2);

              // Calculation of coordinates of cell
              cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

              // Conditions on left facet
              if(write_to_file&(cell_counter_I == 0))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[1] - centre_coordinates_left[1], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_left[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. Y = "+cell_coordinates[1]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_left+" "+heat_param_left);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_left +" "+heat_param_left);
                }
                else
                {
                    write_to_file = false;

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_left+" "+room_tmpr);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_left +" "+room_tmpr);
                }
              }

              // Conditions on right facet
              if(write_to_file&(cell_counter_I == cell_number_I+1))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[1] - centre_coordinates_right[1], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_right[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. Y = "+cell_coordinates[1]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_right+" "+heat_param_right);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_right +" "+heat_param_right);
                }
                else
                {
                    write_to_file = false;

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_right+" "+room_tmpr);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_right +" "+room_tmpr);
                }
              }

              // Conditions on front facet
              if(write_to_file&(cell_counter_J == 0))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_front[0], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_front[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_front +" "+heat_param_front);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                 mech_param_front +" "+ heat_param_front);
                }
                else
                {
                    write_to_file = false;

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_front+" "+room_tmpr);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_front +" "+room_tmpr);
                }
              }

              // Conditions on back facet
              if(write_to_file&(cell_counter_J == cell_number_J+1))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_back[0], 2)+
                                             Math.pow(cell_coordinates[2] - centre_coordinates_back[2], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Z = "+cell_coordinates[2]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_back+" "+heat_param_back);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_back +" "+heat_param_back);
                }
                else
                {
                    write_to_file = false;

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_back+" "+room_tmpr);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_back +" "+room_tmpr);
                }
              }

              // Conditions on top facet
              if(write_to_file&(cell_counter_K == 0))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_top[0], 2)+
                                             Math.pow(cell_coordinates[1] - centre_coordinates_top[1], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Y = "+cell_coordinates[1]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_top+" "+heat_param_top);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_top +" "+heat_param_top);
                }
                else
                {
                    write_to_file = false;

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_top+" "+room_tmpr);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_top +" "+room_tmpr);
                }
              }

              // Conditions on bottom facet
              if(write_to_file&(cell_counter_K == cell_number_K+1))
              {
                dist_from_centre = Math.sqrt(Math.pow(cell_coordinates[0] - centre_coordinates_bottom[0], 2)+
                                             Math.pow(cell_coordinates[1] - centre_coordinates_bottom[1], 2));

                if(dist_from_centre <= radius)
                {
                    write_to_file = false;
                    bound_cells_number++;

                    System.out.println("Cell in circle # "+bound_cells_number+": coord. X = "+cell_coordinates[0]+
                                       "; coord. Y = "+cell_coordinates[1]+"; distance from centre = "+dist_from_centre);

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_bottom+" "+heat_param_bottom);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_bottom +" "+heat_param_bottom);
                }
                else
                {
                    write_to_file = false;

                    // Writing of boundary conditions to files
                    bw .write(bound_cell_index+" "+mech_param_bottom+" "+room_tmpr);
                    bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                  mech_param_bottom +" "+room_tmpr);
                }
              }

              if(write_to_file)
              {
                  // Writing of boundary conditions to files
                  bw .write(bound_cell_index+" 0.0 "+room_tmpr);
                  bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" 0.0 "+room_tmpr);
              }

              bw .newLine(); bw .flush();
              bw1.newLine(); bw1.flush();
            }

            bw1.write("# Number of effected cells in circles: "+bound_cells_number);
            bw1.newLine(); bw1.write("# Cell size: "+cell_size);
            bw .flush();   bw .close();
            bw1.flush();   bw1.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }
    
    /** The method creates file with boundary conditions of task for 2D specimen.
     */
    public void createBoundCondFile()
    {
        // Index of boundary cell
        int bound_cell_index = -1;

   //   Integer bound_cell_number = 2*cell_number_J + 2*cell_number_I - 3;
        Integer bound_cell_number = 2*cell_number_J;


        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(bound_cond_file));

            bw.write(bound_cell_number.toString());
            bw.newLine();
        /*
            for(int cell_index_I = 0; cell_index_I < cell_number_I; cell_index_I++)
            {
                bw.write(cell_index_I+" 0.0 0.0 300.0");
                bw.newLine();
            }
         */
            for(int string_counter = 0; string_counter < cell_number_J; string_counter++)
            {
                bound_cell_index = string_counter*cell_number_I;
                bw.write(bound_cell_index+" 0.0 0.0 1000.0");
                bw.newLine();

                bound_cell_index = (string_counter+1)*cell_number_I - 1;
                bw.write(bound_cell_index+" 0.0 0.0 300.0");
                bw.newLine();

                bw.flush();
            }
      /*
            for(int cell_index_I = 0; cell_index_I < cell_number_I; cell_index_I++)
            {
                bw.write((cell_number_I*(cell_number_J-1)+cell_index_I)+" 0.0 0.0 100.0");
                bw.newLine();
            }
      */
      //      bw.write((cell_number_I*cell_number_J/2-cell_number_I/2)+" 0.0 0.0 1000.0");

            bw.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with information on each grain for task 1.
     */
    public void createGrainsFile()
    {
        // Number of grains in specimen
        int grainNumber = 121;

        // Random number
        double rand;

        // Euler angles
        double angle_1 = 0.0;
        double angle_2 = 0.0;
        double angle_3 = 0.0;

        // Transition limit to high angle grain boundary regime
        double angleLimitHAGB = 15;

        // Specific energy of high angle grain boundary
        double energyHAGB = 1.0E-6;

        // Maximal value of grain boundary mobility
        double maxMobility = 0.0000097480;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(grains_file));
            BufferedWriter bw2= new BufferedWriter(new FileWriter(grains_file_2));

            bw.write((new Integer(grainNumber)).toString());
            bw.newLine();

            bw2.write((new Integer(grainNumber)).toString());
            bw2.newLine();

            for(int grain_counter = 0; grain_counter < grainNumber; grain_counter++)
            {
                // angle_1 = angle_1 + grain_counter*Math.PI/180;

                rand = Math.random();
                angle_1 = 90*rand;

                bw.write(grain_counter+" "+angle_1+" "+angle_2+" "+angle_3+" ");
                bw.write(angleLimitHAGB+" "+energyHAGB+" "+maxMobility);
                bw.newLine();

                bw2.write(grain_counter+" "+angle_1+" "+angle_2+" "+angle_3+" ");
                bw2.write(angleLimitHAGB+" "+energyHAGB+" "+maxMobility);
                bw2.newLine();

                bw.flush();
                bw2.flush();
            }
            bw.close();
            bw2.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with information on each grain for task 2.
     */
    public void createGrainsFile2()
    {
        // Number of grains in specimen
        int grainNumber = 7;

        // 1st Euler angle of grain #0 (central grain) is equal to
        //random number from the segment [0, 90]
        double central_grain_angle_1 = 90.0*Math.random();

        // Euler angles of grain
        double angle_1 = 0.0;
        double angle_2 = 0.0;
        double angle_3 = 0.0;

        // Transition limit to high angle grain boundary regime
        double angleLimitHAGB = 180.0;

        // Specific energy of high angle grain boundary
        double energyHAGB = 1.0E-5;

        // Maximal value of grain boundary mobility
        double maxMobility = 9.748E-6;

        // Temperature of grain
 //     double temperature;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(grains_file));
            BufferedWriter bw2= new BufferedWriter(new FileWriter(grains_file_2));

            bw.write((new Integer(grainNumber)).toString());
            bw.newLine();

            bw2.write((new Integer(grainNumber)).toString());
            bw2.newLine();

            for(int grain_counter = 0; grain_counter < grainNumber; grain_counter++)
            {
                if(grain_counter == 0)
                {
                    angle_1 = central_grain_angle_1;
               //     temperature = 1300.0;
                }
                else
                {
                    angle_1 = central_grain_angle_1 - angleLimitHAGB*(5.0/72.0) +
                              (grain_counter-1)*(5.0/36.0)*angleLimitHAGB/(grainNumber-2);
               //     temperature = 300.0;
                }

                bw.write((grain_counter+1)+" "+angle_1+" "+angle_2+" "+angle_3+" ");
                bw.write(angleLimitHAGB+" "+energyHAGB+" "+maxMobility);//+" "+temperature);
                bw.newLine();

                bw2.write((grain_counter+1)+" "+angle_1+" "+angle_2+" "+angle_3+" ");
                bw2.write(angleLimitHAGB+" "+energyHAGB+" "+maxMobility);//+" "+temperature);
                bw2.newLine();

                bw.flush();
                bw2.flush();
            }
            bw.close();
            bw2.close();
        }
        catch(IOException io_ex)
        {
            completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
        }
    }

    /** The method creates file with information about each grain.
     * @param init_cond_file_name name of file with initial conditions
     * @param phases_file_name name of file with parameters of phases
     * @param bound_grains_file_name name of file with information
     *                               about parameters of boundary grains
     * @param stoch_phase_choice variable responsible for stochastic or
     *                           deterministic choice of phases
     */
    private void createGrainsFile3D(String init_cond_file_name,
                                   String phases_file_name,
                                   String bound_grains_file_name,
                                   boolean stoch_phase_choice)
    {
        // Total number of grains including outer facets
        int total_grain_number = 0;

        // Euler angles of grain
        double angle_1 = 0.0;
        double angle_2 = 0.0;
        double angle_3 = 0.0;

        // Array of types of boundaries
        byte[] bound_types;

        // Array of materials of boundaries
        String[] bound_materials;

        String material;
      //  String bound_material = "titanium";

   //     byte bound_cell = Common.INTERACTING_OUTER_CELL;
        byte inner_cell = Common.INNER_CELL;
     //   byte outer_cell = Common.ADIABATIC_OUTER_CELL;

        // Type of cells in surface and intermediate layers
        byte layer_cell = Common.LAYER_CELL;

        int phase_number = 0;
        double[] percentages;
        String[] materials;
        double[] angle_ranges;
        double[] disl_densities;
        double[] disl_density_deviations;

        // Index of phase of grain
        int phase_index = -1;

        // Density of dislocations
        double disl_density = 0.0;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(grains_file));
            BufferedWriter bw2= new BufferedWriter(new FileWriter(grains_file_2));

            bw.write("#   This file contains parameters of each cluster of cellular automata (CA) -");
            bw.newLine();
            bw.write("# index, material, 3 Euler angles, dislocation density and type:");
            bw.newLine();
            bw.write("#  "+Common.LAYER_CELL+" - cluster consists of inner CA located in surface or intermediate layers,");
            bw.newLine();
            bw.write("#  "+Common.INNER_CELL+" - cluster consists of inner CA located in substrate,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_TEMPERATURE_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and possessing constant temperature,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_THERMAL_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and adiabatic for thermal energy,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and temperature,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant stress and adiabatic for thermal energy,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant stress and temperature,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant stress and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_ADIABATIC_CELL+" - cluster consists of outer boundary CA adiabatic for both mechanical and thermal parts of energy.");
            bw.newLine();

            bw2.write("#   This file contains parameters of each cluster of cellular automata (CA) -");
            bw2.newLine();
            bw2.write("# index, material, 3 Euler angles, dislocation density and type:");
            bw2.newLine();
            bw2.write("#  "+Common.LAYER_CELL+" - cluster consists of inner CA located in surface or intermediate layers,");
            bw2.newLine();
            bw2.write("#  "+Common.INNER_CELL+" - cluster consists of inner CA located in substrate,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_TEMPERATURE_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and possessing constant temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_THERMAL_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and adiabatic for thermal energy,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant stress and adiabatic for thermal energy,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant stress and temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant stress and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_ADIABATIC_CELL+" - cluster consists of outer boundary CA adiabatic for both mechanical and thermal parts of energy.");
            bw2.newLine();

            // Determination of parameters of boundary grains
            TextTableFileReader reader = new TextTableFileReader(bound_grains_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_grains_file_name);

            StringVector table = reader.convertToTable();

            outer_grain_number = table.getRowsNum();

            // Array of materials of boundaries
            bound_materials = new String[outer_grain_number];

            String[] bound_thermal_params = new String[outer_grain_number];

            String[] bound_mech_params = new String[outer_grain_number];

            bound_type = 0;

            // Array of types of boundaries
            bound_types       = new byte[outer_grain_number];

            for(int grain_counter=0; grain_counter<outer_grain_number; grain_counter++)
            {
                // Reading of names of materials ant types of mechanical and thermal loading
                bound_materials     [grain_counter] = table.getCell(grain_counter, 0);
                bound_mech_params   [grain_counter] = table.getCell(grain_counter, 1);
                bound_thermal_params[grain_counter] = table.getCell(grain_counter, 2);

                // Choose of type of boundary facet
                if(bound_thermal_params[grain_counter].equals(Common.ADIABATIC))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_ADIABATIC_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_ADIABATIC_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_ADIABATIC_CELL;
                }

                if(bound_thermal_params[grain_counter].equals(Common.CONSTANT_TEMPERATURE))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_TEMPERATURE_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_TEMPERATURE_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_TEMPERATURE_CELL;
                }

                if(bound_thermal_params[grain_counter].equals(Common.CONSTANT_THERMAL_ENERGY))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_THERMAL_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_THERMAL_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_THERMAL_CELL;
                }

                bound_types[grain_counter] = bound_type;
            }


            BufferedReader br = new BufferedReader(new FileReader(init_cond_file_name));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_cond_file_name);

            boolean read_strings = true;
            String string;
            StringTokenizer st;
            
            // Number of grains in 1st substrate layer
            int grain_number_1 = 0;

            // Number of grains in 2nd substrate layer
            int grain_number_2 = 0;

            // Reading number of grains in specimen
            while(read_strings)
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                {
                    st = new StringTokenizer(br.readLine());
                    string = st.nextToken();
                    grain_number = new Integer(string);
                    
                    string = st.nextToken();
                    grain_number_1 = new Integer(string);

                    read_strings = false;
                }
            }

            // Number of grains in substrate (the last layer)
            substrate_grain_number = grain_number-layer_number+2;

            // Determination of grain number including outer grains/facets
            total_grain_number = grain_number + outer_grain_number;

            reader = new TextTableFileReader(phases_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+phases_file_name);

            table = reader.convertToTable();

            // Array of phase indices of grains
            int[] phase_indices = new int[total_grain_number];

            // Number of phases in specimen
            phase_number   = table.getRowsNum();

            // TEST
        //    bw.write(grainNumber+" "+outer_grain_number+" "+phase_number);
        //    bw.newLine();

            //  Reading of percentage, material, angle range, dislocation density
            // and deviation from average dislocation density for each phase
            percentages    = new double[phase_number];
            materials      = new String[phase_number];
            angle_ranges   = new double[phase_number];
            disl_densities = new double[phase_number];
            disl_density_deviations = new double[phase_number];

            for(int phase_counter = 0; phase_counter<phase_number; phase_counter++)
            {
                percentages[phase_counter]    = (new Double(table.getCell(phase_counter, 0))).doubleValue();
                materials[phase_counter]      =  new String(table.getCell(phase_counter, 1));
                angle_ranges[phase_counter]   = (new Double(table.getCell(phase_counter, 2))).doubleValue();
                disl_densities[phase_counter] = (new Double(table.getCell(phase_counter, 3))).doubleValue();
                disl_density_deviations[phase_counter] = (new Double(table.getCell(phase_counter, 4))).doubleValue();
            }

            double rand;
            double prob_sum;

            //TEST
            completed_steps.setText(completed_steps.getText()+"\nouter_grain_number:     "+outer_grain_number);
            completed_steps.setText(completed_steps.getText()+"\ngrain_number:           "+grain_number);
            completed_steps.setText(completed_steps.getText()+"\ngrain_number_1:         "+grain_number_1);
            completed_steps.setText(completed_steps.getText()+"\nlayer_number:           "+layer_number);
            completed_steps.setText(completed_steps.getText()+"\nsubstrate_grain_number: "+substrate_grain_number);
            completed_steps.setText(completed_steps.getText()+"\ntotal_grain_number:     "+total_grain_number);
            completed_steps.setText(completed_steps.getText()+"\nphase_number:           "+phase_number);

            // Choice of phase of each grain in substrate
            if(stoch_phase_choice)
            for(int grain_counter = 1; grain_counter<=grain_number_1; grain_counter++)
            {
                prob_sum = 0;
                rand = Math.random();

                // Choice of phase
                for(int phase_counter = layer_number-2; phase_counter<phase_number-1; phase_counter++)
                {
                    prob_sum+=percentages[phase_counter];

                    if(rand < prob_sum)
                    {
                        phase_indices[grain_counter-1] = phase_counter;
                        phase_counter = phase_number;
                    }
                }
            }
            else
            for(int grain_counter = 1; grain_counter<=grain_number_1; grain_counter++)
                phase_indices[grain_counter-1] = (int)Math.floor((grain_counter-1)/
                        ((double)grain_number_1/(double)(phase_number-layer_number+1)))+
                        layer_number-2;

            if(stoch_phase_choice)
            for(int grain_counter = grain_number_1+1; grain_counter<=substrate_grain_number; grain_counter++)
            {
                prob_sum = 0;
                rand = Math.random();

                // Choice of phase
                for(int phase_counter = layer_number-1; phase_counter<phase_number; phase_counter++)
                {
                    prob_sum+=percentages[phase_counter];

                    if(rand < prob_sum)
                    {
                        phase_indices[grain_counter-1] = phase_counter;
                        phase_counter = phase_number;
                    }
                }
            }
            else
            for(int grain_counter = grain_number_1+1; grain_counter<=substrate_grain_number; grain_counter++)
                phase_indices[grain_counter-1] = (int)Math.floor((grain_counter - grain_number_1 - 1)/
                        ((double)(substrate_grain_number-grain_number_1)/(double)(phase_number-layer_number+1)))+
                        layer_number-1;



            // Choice of phase of each grain in surface and intermediate layers
            for(int grain_counter = substrate_grain_number+1; grain_counter<=grain_number; grain_counter++)
                phase_indices[grain_counter-1] = grain_counter - substrate_grain_number - 1;

            // Setting of grain parameters according to its phase
            for(int grain_counter = 1; grain_counter<=total_grain_number; grain_counter++)
            {
              if(grain_counter<=grain_number_1)
              {
                // Index of phase for current grain
                phase_index = phase_indices[grain_counter-1];

                material = materials[phase_index];
                angle_1 = angle_ranges[phase_index]*Math.random();
                disl_density = disl_densities[phase_index]*
                              (1+disl_density_deviations[phase_index]*(2*Math.random()-1));

                bw.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw.write(" "+inner_cell);

                bw2.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw2.write(" "+inner_cell);

                bw.newLine();
                bw.flush();
                bw2.newLine();
                bw2.flush();
              }

              if(grain_counter>grain_number_1)
              if(grain_counter<=substrate_grain_number)
              {
                // Index of phase for current grain
                phase_index = phase_indices[grain_counter-1];

                material = materials[phase_index];
                angle_1 = angle_ranges[phase_index]*Math.random();
                disl_density = disl_densities[phase_index]*
                              (1+disl_density_deviations[phase_index]*(2*Math.random()-1));

                bw.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw.write(" "+inner_cell);

                bw2.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw2.write(" "+inner_cell);

                bw.newLine();
                bw.flush();
                bw2.newLine();
                bw2.flush();
              }

              if((grain_counter>substrate_grain_number)&(grain_counter<=grain_number))
              {
                  // Index of phase for current grain
                phase_index = phase_indices[grain_counter-1];

                material = materials[phase_index];
                angle_1 = angle_ranges[phase_index]*Math.random();
                disl_density = disl_densities[phase_index]*
                              (1+disl_density_deviations[phase_index]*(2*Math.random()-1));

                bw.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw.write(" "+layer_cell);

                bw2.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw2.write(" "+layer_cell);

                bw.newLine();
                bw.flush();
                bw2.newLine();
                bw2.flush();
              }

              if(grain_counter>grain_number)
              {
                angle_1 = 0.0;
                material = new String(bound_materials[grain_counter-total_grain_number+outer_grain_number-1]);
                disl_density = 0.0;

                bw.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw.write(" "+bound_types[grain_counter-grain_number-1]);

                bw2.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw2.write(" "+bound_types[grain_counter-grain_number-1]);

                bw.newLine();
                bw.flush();

                bw2.newLine();
                bw2.flush();
              }

              // TEST
              System.out.println(grain_counter+" "+phase_index);
            }
            bw.close();
            bw2.close();
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
        }

        completed_steps.setText(completed_steps.getText()+"\nName of created file: "+grains_file);
        completed_steps.setText(completed_steps.getText()+"\nName of created file: "+grains_file_2+"\n");
    }

    
    /** The method creates file with information about each grain including tanks.
     * @param init_cond_file_name name of file with initial conditions
     * @param phases_file_name name of file with parameters of phases
     * @param bound_grains_file_name name of file with information
     *                               about parameters of boundary grains
     * @param tanks_file_name name of file with information
     *                        about parameters of tanks on facets
     * @param stoch_phase_choice variable responsible for stochastic or
     *                           deterministic choice of phases
     */
    private void createGrainsAndTanksFile3D(String init_cond_file_name,
                                   String phases_file_name,
                                   String bound_grains_file_name,
                                   String tanks_file_name,
                                   boolean stoch_phase_choice, boolean determine_grain_angles)
    {
        // Total number of grains including outer facets
        int total_grain_number = 0;

        // Euler angles of grain
        double angle_1 = 0.0;
        double angle_2 = 0.0;
        double angle_3 = 0.0;

        // Array of types of boundaries
        byte[] bound_types;

        // Array of materials of boundaries
        String[] bound_materials;

        String material;
      //  String bound_material = "titanium";

   //     byte bound_cell = Common.INTERACTING_OUTER_CELL;
        byte inner_cell = Common.INNER_CELL;
     //   byte outer_cell = Common.ADIABATIC_OUTER_CELL;

        // Type of cells in surface and intermediate layers
        byte layer_cell = Common.LAYER_CELL;

        int phase_number = 0;
        double[] percentages;
        String[] materials;
        double[] angle_ranges;
        double[] disl_densities;
        double[] disl_density_deviations;

        // Index of phase of grain
        int phase_index = -1;

        // Density of dislocations
        double disl_density = 0.0;

        if(determine_grain_angles)
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(grains_file));
            BufferedWriter bw2= new BufferedWriter(new FileWriter(grains_file_2));

            bw.write("#   This file contains parameters of each cluster of cellular automata (CA) -");
            bw.newLine();
            bw.write("# index, material, 3 Euler angles, dislocation density and type:");
            bw.newLine();
            bw.write("#  "+Common.LAYER_CELL+" - cluster consists of inner CA located in surface or intermediate layers,");
            bw.newLine();
            bw.write("#  "+Common.INNER_CELL+" - cluster consists of inner CA located in substrate,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_TEMPERATURE_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and possessing constant temperature,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_THERMAL_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and adiabatic for thermal energy,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and temperature,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant stress and adiabatic for thermal energy,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant stress and temperature,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant stress and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_ADIABATIC_CELL+" - cluster consists of outer boundary CA adiabatic for both mechanical and thermal parts of energy.");
            bw.newLine();

            bw2.write("#   This file contains parameters of each cluster of cellular automata (CA) -");
            bw2.newLine();
            bw2.write("# index, material, 3 Euler angles, dislocation density and type:");
            bw2.newLine();
            bw2.write("#  "+Common.LAYER_CELL+" - cluster consists of inner CA located in surface or intermediate layers,");
            bw2.newLine();
            bw2.write("#  "+Common.INNER_CELL+" - cluster consists of inner CA located in substrate,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_TEMPERATURE_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and possessing constant temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_THERMAL_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and adiabatic for thermal energy,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant stress and adiabatic for thermal energy,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant stress and temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant stress and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_ADIABATIC_CELL+" - cluster consists of outer boundary CA adiabatic for both mechanical and thermal parts of energy.");
            bw2.newLine();

            // Determination of parameters of boundary grains
            TextTableFileReader reader = new TextTableFileReader(bound_grains_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_grains_file_name);

            StringVector table = reader.convertToTable();
            
            // Determination of parameters of tanks
            TextTableFileReader tanks_reader = new TextTableFileReader(tanks_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+tanks_file_name);

            StringVector tanks_table = tanks_reader.convertToTable();

            // Number of facets
            int facet_number = table.getRowsNum();

            // Number of tanks
            int tank_number = tanks_table.getRowsNum();

            // Number of outer grains (facets and tanks)
            outer_grain_number = facet_number + tank_number;
            
            // Array of materials of outer grains (facets and tanks)
            bound_materials = new String[outer_grain_number];

            // Types of loading of facets and tanks
            String[] bound_thermal_params = new String[outer_grain_number];
            String[] bound_mech_params = new String[outer_grain_number];

            bound_type = 0;

            // Array of types of outer grains (facets and tanks)
            bound_types       = new byte[outer_grain_number];

            for(int grain_counter=0; grain_counter<outer_grain_number; grain_counter++)
            {
                // Reading of names of materials ant types of mechanical and thermal loading
                if(grain_counter < facet_number)
                {
                    bound_materials     [grain_counter] = table.getCell(grain_counter, 0);
                    bound_mech_params   [grain_counter] = table.getCell(grain_counter, 1);
                    bound_thermal_params[grain_counter] = table.getCell(grain_counter, 2);
                }
                else
                {
                    bound_materials     [grain_counter] = tanks_table.getCell(grain_counter-facet_number, 0);
                    bound_mech_params   [grain_counter] = tanks_table.getCell(grain_counter-facet_number, 7);
                    bound_thermal_params[grain_counter] = tanks_table.getCell(grain_counter-facet_number, 8);
                }

                // Choose of type of boundary facet
                if(bound_thermal_params[grain_counter].equals(Common.ADIABATIC))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_ADIABATIC_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_ADIABATIC_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_ADIABATIC_CELL;
                }

                if(bound_thermal_params[grain_counter].equals(Common.CONSTANT_TEMPERATURE))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_TEMPERATURE_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_TEMPERATURE_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_TEMPERATURE_CELL;
                }

                if(bound_thermal_params[grain_counter].equals(Common.CONSTANT_THERMAL_ENERGY))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_THERMAL_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_THERMAL_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_THERMAL_CELL;
                }

                bound_types[grain_counter] = bound_type;
            }

            BufferedReader br = new BufferedReader(new FileReader(init_cond_file_name));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_cond_file_name);

            boolean read_strings = true;
            String string;
            StringTokenizer st;

            // Number of grains in surface layer
            int grain_number_1 = 0;

            // Number of grains in upper intermediate layer
            int grain_number_2 = 0;

            // Number of grains in lower intermediate layer
            int grain_number_3 = 0;
            
            // Number of grains in upper substrate layer
            int grain_number_4 = 0;

            // Reading number of grains in specimen
            while(read_strings)
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                {
                    st = new StringTokenizer(br.readLine());

                    // Reading of the total number of grains in specimen
                    string = st.nextToken();
                    grain_number = new Integer(string);

                    // Reading of the number of grains in surface layer
                    string = st.nextToken();
                    grain_number_1 = new Integer(string);

                    // Reading of the number of grains in upper intermediate layer
                    string = st.nextToken();
                    grain_number_2 = new Integer(string);

                    // Reading of the number of grains in lower intermediate layer
                    string = st.nextToken();
                    grain_number_3 = new Integer(string);

                    // Reading of the number of grains in upper substrate layer
                    string = st.nextToken();
                    grain_number_4 = new Integer(string);

                    // Reading of the number of grains in lower substrate layer
                    string = st.nextToken();
                    substrate_grain_number = new Integer(string);

                    read_strings = false;
                }
            }

            // Total number of grains in specimen
            int inner_grain_number = grain_number_1 + grain_number_2 +
                    grain_number_3 + grain_number_4 + substrate_grain_number;

            // TEST
            if(inner_grain_number != grain_number)
                System.out.println("ERROR!!! Total number of grains is wrong!!!");

            // Determination of grain number including outer grains (facets and tanks)
            total_grain_number = grain_number + outer_grain_number;

            reader = new TextTableFileReader(phases_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+phases_file_name);
            System.out.println();
            System.out.println("Name of read file:    "+phases_file_name);
            System.out.println();

            table = reader.convertToTable();

            // Array of phase indices of grains
            int[] phase_indices = new int[grain_number];

            // Number of phases in specimen
            phase_number   = table.getRowsNum();

            // TEST
        //  bw.write(grain_number+" "+outer_grain_number+" "+phase_number);
        //  bw.newLine();

            //  Reading of percentage, material, angle range, dislocation density
            // and deviation from average dislocation density for each phase
            percentages    = new double[phase_number];
            materials      = new String[phase_number];
            angle_ranges   = new double[phase_number];
            disl_densities = new double[phase_number];
            disl_density_deviations = new double[phase_number];

            for(int phase_counter = 0; phase_counter<phase_number; phase_counter++)
            {
                percentages[phase_counter]    = (new Double(table.getCell(phase_counter, 0))).doubleValue();
                materials[phase_counter]      =  new String(table.getCell(phase_counter, 1));
                angle_ranges[phase_counter]   = (new Double(table.getCell(phase_counter, 2))).doubleValue();
                disl_densities[phase_counter] = (new Double(table.getCell(phase_counter, 3))).doubleValue();
                disl_density_deviations[phase_counter] = (new Double(table.getCell(phase_counter, 4))).doubleValue();
            }

            double rand;
            double prob_sum;

            //TEST
            completed_steps.setText(completed_steps.getText()+"\nnumber of facets and tanks:     "+outer_grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of clusters in specimen: "+grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of grains in substrate:  "+substrate_grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of layers:               "+layer_number);
            completed_steps.setText(completed_steps.getText()+"\ntotal number of clusters:       "+total_grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of phases:               "+phase_number);

            // Choice of phase of each grain in surface and intermediate layers
            for(int grain_counter = 1; grain_counter<=grain_number_1; grain_counter++)
                phase_indices[grain_counter-1] = 0;

            for(int grain_counter = grain_number_1+1; grain_counter<=grain_number_1+grain_number_2; grain_counter++)
                phase_indices[grain_counter-1] = 1;

            for(int grain_counter = grain_number_1+grain_number_2+1; grain_counter<=grain_number_1+grain_number_2+grain_number_3; grain_counter++)
                phase_indices[grain_counter-1] = 2;

            for(int grain_counter = grain_number_1+grain_number_2+grain_number_3+1; grain_counter<=grain_number_1+grain_number_2+grain_number_3+grain_number_4; grain_counter++)
                phase_indices[grain_counter-1] = 3;

            // Choice of phase of each grain in substrate
            if(stoch_phase_choice)
            {
              // Stochastic choice of phase of each grain in substrate
              for(int grain_counter = grain_number - substrate_grain_number + 1; grain_counter<=grain_number; grain_counter++)
              {
                prob_sum = 0;
                rand = Math.random();

                // Stochastic choice of phase of current grain
                for(int phase_counter = layer_number-1; phase_counter<phase_number; phase_counter++)
                {
                    prob_sum+=percentages[phase_counter];

                    if(rand < prob_sum)
                    {
                        phase_indices[grain_counter-1] = phase_counter;
                        phase_counter = phase_number;
                    }
                }
              }
            }
            else
            {
              // Deterministic choice of phase of each grain in substrate
              for(int grain_counter = grain_number - substrate_grain_number + 1; grain_counter<=grain_number; grain_counter++)
                phase_indices[grain_counter-1] = (int)Math.floor((grain_counter-grain_number+substrate_grain_number-1)/
                                                 ((double)substrate_grain_number/(phase_number-layer_number+1)))+layer_number-1;
            }
            
            // Setting of grain parameters according to its phase
            for(int grain_counter = 1; grain_counter<=total_grain_number; grain_counter++)
            {
              // Setting of inner grain parameters
              if(grain_counter <= grain_number)
              {
                // Index of phase for current grain
                phase_index = phase_indices[grain_counter-1];

                material = materials[phase_index];

                angle_1 = angle_ranges[phase_index]*Math.random();
                angle_2 = angle_ranges[phase_index]*Math.random();
                angle_3 = angle_ranges[phase_index]*Math.random();

                disl_density = disl_densities[phase_index]*
                              (1+disl_density_deviations[phase_index]*(2*Math.random()-1));

                bw.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                
                bw2.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);

                // Condition of belonging of grain to surface or intermediate layers
                if(grain_counter<=grain_number_1+grain_number_2+grain_number_3)
                {
                    bw.write(" "+layer_cell);
                    bw2.write(" "+layer_cell);
                }
                // Condition of belonging of grain to substrate layers
                else
                {
                    bw.write(" "+inner_cell);
                    bw2.write(" "+inner_cell);
                }

                bw.newLine();
                bw.flush();
                bw2.newLine();
                bw2.flush();

                // TEST
                System.out.println("Grain # "+grain_counter+" consists of phase # "+phase_index+" ("+materials[phase_index]+").");
              }

              // Setting of parameters of outer grains (specimen facets)
              if(grain_counter>grain_number)
              {
                angle_3 = angle_2 = angle_1 = 0.0;
                disl_density = 0.0;

                material = new String(bound_materials[grain_counter-grain_number-1]);

                bw.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw.write(" "+bound_types[grain_counter-grain_number-1]);

                bw2.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw2.write(" "+bound_types[grain_counter-grain_number-1]);

                bw.newLine();
                bw.flush();

                bw2.newLine();
                bw2.flush();

                // TEST
                System.out.println("Grain # "+grain_counter+" represents specimen facet and consists of "+material);
                
                completed_steps.setText(completed_steps.getText()+"\nName of 1st file with grain properties: "+grains_file);
                completed_steps.setText(completed_steps.getText()+"\nName of 2nd file with grain properties: "+grains_file_2+"\n");
                System.out.print("\nName of 1st file with information about grain properties: "+grains_file);
                System.out.print("\nName of 2nd file with information about grain properties: "+grains_file_2+"\n");
              }
            }
            bw.close();
            bw2.close();
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            System.out.println("Error: "+io_exc);
        }
        
        if(!determine_grain_angles)
        {
            completed_steps.setText(completed_steps.getText()+"\nName of 1st existing file with grain properties: "+grains_file);
            completed_steps.setText(completed_steps.getText()+"\nName of 2nd existing file with grain properties: "+grains_file_2+"\n");
            System.out.print("\nName of 1st existing file with information about grain properties: "+grains_file);
            System.out.print("\nName of 2nd existing file with information about grain properties: "+grains_file_2+"\n");
        }
    }

    /** The method creates file with information about each grain including tanks.
     * @param init_cond_file_name name of file with initial conditions
     * @param phases_file_name name of file with parameters of phases
     * @param bound_grains_file_name name of file with information
     *                               about parameters of boundary grains
     * @param tanks_file_name name of file with information
     *                        about parameters of tanks on facets
     * @param stoch_phase_choice variable responsible for stochastic or
     *                           deterministic choice of phases
     * @param pore_colour colour of grain, which will be a pore
     */
    private void createGrainsAndTanksFile3D(String init_cond_file_name, String phases_file_name, String bound_grains_file_name,
                                            String tanks_file_name, boolean stoch_phase_choice, int pore_colour, boolean determine_grain_angles)
    {
        // Total number of grains including outer facets
        int total_grain_number = 0;
        
        // Euler angles of grain
        double angle_1 = 0.0;
        double angle_2 = 0.0;
        double angle_3 = 0.0;
        
        // Array of types of boundaries
        byte[] bound_types;
        
        // Array of materials of boundaries
        String[] bound_materials;
        
        String material;
     // String bound_material = "titanium";
        
     // byte bound_cell = Common.INTERACTING_OUTER_CELL;
        byte inner_cell = Common.INNER_CELL;
     // byte outer_cell = Common.ADIABATIC_OUTER_CELL;
        
        // Type of cells in surface and intermediate layers
        byte layer_cell = Common.LAYER_CELL;
        
        int phase_number = 0;
        double[] percentages;
        String[] materials;
        double[] angle_ranges;
        double[] disl_densities;
        double[] disl_density_deviations;
        
        // Index of phase of grain
        int phase_index = -1;
        
        // Density of dislocations
        double disl_density = 0.0;
        
        if(determine_grain_angles)
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(grains_file));
            BufferedWriter bw2= new BufferedWriter(new FileWriter(grains_file_2));
            
            bw.write("#   This file contains parameters of each cluster of cellular automata (CA) -");
            bw.newLine();
            bw.write("# index, material, 3 Euler angles, dislocation density, average dislocation density, its maximal deviation and type:");
            bw.newLine();
            bw.write("#  "+Common.LAYER_CELL+" - cluster consists of inner CA located in surface or intermediate layers,");
            bw.newLine();
            bw.write("#  "+Common.INNER_CELL+" - cluster consists of inner CA located in substrate,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_TEMPERATURE_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and possessing constant temperature,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_THERMAL_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and adiabatic for thermal energy,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and temperature,");
            bw.newLine();
            bw.write("#  "+Common.STRAIN_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant stress and adiabatic for thermal energy,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant stress and temperature,");
            bw.newLine();
            bw.write("#  "+Common.STRESS_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant stress and effected by constant thermal energy influx,");
            bw.newLine();
            bw.write("#  "+Common.ADIABATIC_ADIABATIC_CELL+" - cluster consists of outer boundary CA adiabatic for both mechanical and thermal parts of energy;");
            bw.newLine();
            
            bw2.write("#   This file contains parameters of each cluster of cellular automata (CA) -");
            bw2.newLine();
            bw2.write("# index, material, 3 Euler angles, dislocation density, average dislocation density, its maximal deviation and type:");
            bw2.newLine();
            bw2.write("#  "+Common.LAYER_CELL+" - cluster consists of inner CA located in surface or intermediate layers,");
            bw2.newLine();
            bw2.write("#  "+Common.INNER_CELL+" - cluster consists of inner CA located in substrate,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_TEMPERATURE_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and possessing constant temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_THERMAL_CELL+" - cluster consists of outer boundary CA adiabatic for mechanical energy and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and adiabatic for thermal energy,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.STRAIN_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant strain rate and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_ADIABATIC_CELL+" - cluster consists of outer boundary CA possessing constant stress and adiabatic for thermal energy,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_TEMPERATURE_CELL+" - cluster consists of outer boundary CA possessing constant stress and temperature,");
            bw2.newLine();
            bw2.write("#  "+Common.STRESS_THERMAL_CELL+" - cluster consists of outer boundary CA possessing constant stress and effected by constant thermal energy influx,");
            bw2.newLine();
            bw2.write("#  "+Common.ADIABATIC_ADIABATIC_CELL+" - cluster consists of outer boundary CA adiabatic for both mechanical and thermal parts of energy;");
            bw2.newLine();

            // Determination of parameters of boundary grains
            TextTableFileReader reader = new TextTableFileReader(bound_grains_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_grains_file_name);

            StringVector table = reader.convertToTable();

            // Determination of parameters of tanks
            TextTableFileReader tanks_reader = new TextTableFileReader(tanks_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+tanks_file_name);

            StringVector tanks_table = tanks_reader.convertToTable();

            // Number of facets
            int facet_number = table.getRowsNum();

            // Number of tanks
            int tank_number = tanks_table.getRowsNum() - 1;

            // Number of outer grains (facets and tanks)
            outer_grain_number = facet_number + tank_number;

            // Array of materials of outer grains (facets and tanks)
            bound_materials = new String[outer_grain_number];

            // Types of loading of facets and tanks
            String[] bound_thermal_params = new String[outer_grain_number];
            String[] bound_mech_params    = new String[outer_grain_number];

            bound_type = 0;

            // Array of types of outer grains (facets and tanks)
            bound_types       = new byte[outer_grain_number];
            
            System.out.println("+++++++++--------------+++++++++++++++++------------------");

            for(int grain_counter=0; grain_counter<outer_grain_number; grain_counter++)
            {
                // Reading of names of materials ant types of mechanical and thermal loading
                if(grain_counter < facet_number)
                {
                    bound_materials     [grain_counter] = table.getCell(grain_counter, 0);
                    bound_mech_params   [grain_counter] = table.getCell(grain_counter, 1);
                    bound_thermal_params[grain_counter] = table.getCell(grain_counter, 2);
                }
                else
                {
                    bound_materials     [grain_counter] = tanks_table.getCell(grain_counter-facet_number, 0);
                    bound_mech_params   [grain_counter] = tanks_table.getCell(grain_counter-facet_number, 7);
                    bound_thermal_params[grain_counter] = tanks_table.getCell(grain_counter-facet_number, 8);
                }

                // Choose of type of boundary facet
                if(bound_thermal_params[grain_counter].equals(Common.ADIABATIC))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_ADIABATIC_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_ADIABATIC_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_ADIABATIC_CELL;
                }

                if(bound_thermal_params[grain_counter].equals(Common.CONSTANT_TEMPERATURE))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_TEMPERATURE_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_TEMPERATURE_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_TEMPERATURE_CELL;
                }

                if(bound_thermal_params[grain_counter].equals(Common.CONSTANT_THERMAL_ENERGY))
                {
                    if(bound_mech_params[grain_counter].equals(Common.ADIABATIC))
                        bound_type = Common.ADIABATIC_THERMAL_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRAIN))
                        bound_type = Common.STRAIN_THERMAL_CELL;

                    if(bound_mech_params[grain_counter].equals(Common.CONSTANT_STRESS))
                        bound_type = Common.STRESS_THERMAL_CELL;
                }

                bound_types[grain_counter] = bound_type;
                
                if(grain_counter < facet_number)
                    System.out.println("Facet # "+grain_counter+" has type "+bound_types[grain_counter]+".");
                else
                    System.out.println("Tank # "+grain_counter+" has type "+bound_types[grain_counter]+".");
            }
            
            System.out.println("+++++++++--------------+++++++++++++++++------------------");

            BufferedReader br = new BufferedReader(new FileReader(init_cond_file_name));

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+init_cond_file_name);

            boolean read_strings = true;
            String string;
            StringTokenizer st;

            // Number of grains in surface layer
            int grain_number_1 = 0;

            // Number of grains in upper intermediate layer
            int grain_number_2 = 0;

            // Number of grains in lower intermediate layer
            int grain_number_3 = 0;

            // Number of grains in upper substrate layer
            int grain_number_4 = 0;

            // Reading number of grains in specimen
            while(read_strings)
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                {
                    st = new StringTokenizer(br.readLine());

                    // Reading of the total number of grains in specimen
                    string = st.nextToken();
                    grain_number = new Integer(string);

                    // Reading of the number of grains in surface layer
                    string = st.nextToken();
                    grain_number_1 = new Integer(string);

                    // Reading of the number of grains in upper intermediate layer
                    string = st.nextToken();
                    grain_number_2 = new Integer(string);

                    // Reading of the number of grains in lower intermediate layer
                    string = st.nextToken();
                    grain_number_3 = new Integer(string);

                    // Reading of the number of grains in upper substrate layer
                    string = st.nextToken();
                    grain_number_4 = new Integer(string);

                    // Reading of the number of grains in lower substrate layer
                    string = st.nextToken();
                    substrate_grain_number = new Integer(string);

                    read_strings = false;
                }
            }

            // Total number of grains in specimen
            int inner_grain_number = grain_number_1 + grain_number_2 +
                    grain_number_3 + grain_number_4 + substrate_grain_number;

            // TEST
            if(inner_grain_number != grain_number)
                System.out.println("ERROR!!! Total number of grains is wrong!!!");

            // Determination of grain number including outer grains (facets and tanks)
            total_grain_number = grain_number + outer_grain_number;

            reader = new TextTableFileReader(phases_file_name);

            completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+phases_file_name);
            System.out.println();
            System.out.println("Name of read file:    "+phases_file_name);
            System.out.println();

            table = reader.convertToTable();

            // Array of phase indices of grains
            int[] phase_indices = new int[grain_number];

            // Number of phases in specimen
            phase_number   = table.getRowsNum();

            // TEST
        //  bw.write(grain_number+" "+outer_grain_number+" "+phase_number);
        //  bw.newLine();

            //  Reading of percentage, material, angle range, dislocation density
            // and deviation from average dislocation density for each phase
            percentages    = new double[phase_number];
            materials      = new String[phase_number];
            angle_ranges   = new double[phase_number];
            disl_densities = new double[phase_number];
            disl_density_deviations = new double[phase_number];

            for(int phase_counter = 0; phase_counter < phase_number; phase_counter++)
            {
                percentages[phase_counter]    = (new Double(table.getCell(phase_counter, 0))).doubleValue();
                materials[phase_counter]      =  new String(table.getCell(phase_counter, 1));
                angle_ranges[phase_counter]   = (new Double(table.getCell(phase_counter, 2))).doubleValue();
                disl_densities[phase_counter] = (new Double(table.getCell(phase_counter, 3))).doubleValue();
                disl_density_deviations[phase_counter] = (new Double(table.getCell(phase_counter, 4))).doubleValue();
            }

            double rand;
            double prob_sum;

            //TEST
            completed_steps.setText(completed_steps.getText()+"\nnumber of facets and tanks:     "+outer_grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of clusters in specimen: "+grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of grains in substrate:  "+substrate_grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of layers:               "+layer_number);
            completed_steps.setText(completed_steps.getText()+"\ntotal number of clusters:       "+total_grain_number);
            completed_steps.setText(completed_steps.getText()+"\nnumber of phases:               "+phase_number);

            // Choice of phase of each grain in surface and intermediate layers
            for(int grain_counter = 1; grain_counter<=grain_number_1; grain_counter++)
                phase_indices[grain_counter-1] = 0;

            for(int grain_counter = grain_number_1+1; grain_counter<=grain_number_1+grain_number_2; grain_counter++)
                phase_indices[grain_counter-1] = 1;

            for(int grain_counter = grain_number_1+grain_number_2+1; grain_counter<=grain_number_1+grain_number_2+grain_number_3; grain_counter++)
                phase_indices[grain_counter-1] = 2;

            for(int grain_counter = grain_number_1+grain_number_2+grain_number_3+1; grain_counter<=grain_number_1+grain_number_2+grain_number_3+grain_number_4; grain_counter++)
                phase_indices[grain_counter-1] = 3;

            // Choice of phase of each grain in substrate
            if(stoch_phase_choice)
            {
              // Stochastic choice of phase of each grain in substrate
              for(int grain_counter = grain_number - substrate_grain_number + 1; grain_counter <= grain_number; grain_counter++)
              {
                prob_sum = 0;
                rand = Math.random();

                // Stochastic choice of phase of current grain
                for(int phase_counter = layer_number-1; phase_counter<phase_number; phase_counter++)
                {
                    prob_sum+=percentages[phase_counter];

                    if(rand < prob_sum)
                    {
                        phase_indices[grain_counter-1] = phase_counter;
                        phase_counter = phase_number;
                    }
                }
              }
            }
            else
            {
              // Deterministic choice of phase of each grain in substrate
              for(int grain_counter = grain_number - substrate_grain_number + 1; grain_counter<=grain_number; grain_counter++)
                phase_indices[grain_counter-1] = (int)Math.floor((grain_counter-grain_number+substrate_grain_number-1)/
                                                 ((double)substrate_grain_number/(phase_number-layer_number+1)))+layer_number-1;
            }

            int grain_index     = -1;
            int grain_colour    = -1;
            int[] grain_colours = new int[grain_number + 12];
            int col_number      = 0;
            int gr_number       = 0;

            try
            {
                BufferedReader br_colours = new BufferedReader(new FileReader(grain_colours_file));
                System.out.println("Name of read file: "+grain_colours_file);

                while(br_colours.ready())
                {
                    string = br_colours.readLine();
                    st = new StringTokenizer(string);

                    if(st.hasMoreTokens())
                    {
                        string = st.nextToken();
                        col_number = (new Integer(string)).intValue();
                        string = st.nextToken();
                        gr_number = (new Integer(string)).intValue();
                    }
                    
                    System.out.println("Number of colours: "+col_number);
                    System.out.println("Number of grains:  "+gr_number);

                    for(int col_counter = 0; col_counter < col_number; col_counter++)
                        string = br_colours.readLine();

                    for(int gr_counter = 0; gr_counter < gr_number; gr_counter++)
                    {
                      //  System.out.print(gr_counter+" ");
                        
                        string = br_colours.readLine();
                        st = new StringTokenizer(string);
                        
                        System.out.println("Grain index and colour index: "+string);

                        if(st.hasMoreTokens())
                        {
                            string = st.nextToken();
                            grain_index = (new Integer(string)).intValue();

                            string = st.nextToken();
                            grain_colour = (new Integer(string)).intValue();

                            grain_colours[grain_index-1] = grain_colour;
                        }
                    }
                }

                br_colours.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("Exception: "+io_exc);
            }
            
            // Average angle of misorientation of adjacent grains
            double aver_angle_diff = 0;
            
            // Euler angles of all grains
            cellcore.Three[] euler_angles = new cellcore.Three[total_grain_number];

            // Setting of grain parameters according to its phase
            for(int grain_counter = 1; grain_counter<=total_grain_number; grain_counter++)
            {
              // Setting of inner grain parameters
              if(grain_counter <= grain_number)
              {
                // Index of phase for current grain
                phase_index = phase_indices[grain_counter-1];
                
                // Average angle of misorientation of adjacent grains for given phase
                aver_angle_diff = angle_ranges[phase_index]/3;

                // material of phase for current grain
                material = materials[phase_index];

                // Colour of current grain
                grain_colour = grain_colours[grain_counter-1];

                // Setting of parameters of grains, which are not pores
                if(grain_colour!=pore_colour)
                {
                    angle_1 = angle_ranges[phase_index]*Math.random();
                    angle_2 = angle_ranges[phase_index]*Math.random();
                    angle_3 = angle_ranges[phase_index]*Math.random();
                    
                    // addition of Euler angles of grain to array
                    euler_angles[grain_counter - 1] = new cellcore.Three(angle_1, angle_2, angle_3);
                    
                    disl_density = disl_densities[phase_index]*(1+disl_density_deviations[phase_index]*(2*Math.random()-1));
                    
                    bw.write(grain_counter+" "+material+" "+angle_1+" "+angle_2+" "+angle_3+" "+disl_density);//+" "+disl_densities[phase_index]+" "+disl_density_deviations[phase_index]);
                    bw.write(" "+disl_densities[phase_index]+" "+disl_density_deviations[phase_index]*disl_densities[phase_index]);
                    
                    bw2.write(grain_counter+" "+material+" "+angle_1+" "+angle_2+" "+angle_3+" "+disl_density);//+" "+disl_densities[phase_index]+" "+disl_density_deviations[phase_index]);
                    bw2.write(" "+disl_densities[phase_index]+" "+disl_density_deviations[phase_index]*disl_densities[phase_index]);
                    
                    // Condition of belonging of grain to surface or intermediate layers
                    if(grain_counter<=grain_number_1+grain_number_2+grain_number_3)
                    {
                        bw.write(" "+layer_cell+" "+Common.INITIAL_GRAIN+" 0");
                        bw2.write(" "+layer_cell+" "+Common.INITIAL_GRAIN+" 0");
                    }
                    // Condition of belonging of grain to substrate layers
                    else
                    {
                        bw.write(" "+inner_cell+" "+Common.INITIAL_GRAIN+" 0");
                        bw2.write(" "+inner_cell+" "+Common.INITIAL_GRAIN+" 0");
                    }
                }
                // Setting of parameters of pores
                else
                {
                    angle_1 = 0;
                    angle_2 = 0;
                    angle_3 = 0;
                    disl_density = 0;
                    
                    bw.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density+" "+Common.ADIABATIC_ADIABATIC_CELL+" "+Common.INITIAL_GRAIN+" 0");
                    
                    bw2.write(grain_counter+" "+material+" "+
                         angle_1+" "+angle_2+" "+angle_3+" "+disl_density+" "+Common.ADIABATIC_ADIABATIC_CELL+" "+Common.INITIAL_GRAIN+" 0");
                }
                    
                bw.newLine();
                bw.flush();
                bw2.newLine();
                bw2.flush();
                
                // TEST
                System.out.println("Grain # "+grain_counter+" consists of phase # "+phase_index+" ("+materials[phase_index]+").");
              }
              
              // Setting of parameters of outer grains (specimen facets)
              if(grain_counter > grain_number)
              {
                angle_3 = angle_2 = angle_1 = 0.0;
                disl_density = 0.0;
                
                material = new String(bound_materials[grain_counter-grain_number-1]);
                
                bw.write(grain_counter+" "+material+" "+angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw.write(" 0.0 0.0 "+bound_types[grain_counter-grain_number-1]+" "+Common.INITIAL_GRAIN+" 0");
                
                bw2.write(grain_counter+" "+material+" "+angle_1+" "+angle_2+" "+angle_3+" "+disl_density);
                bw2.write(" 0.0 0.0 "+bound_types[grain_counter-grain_number-1]+" "+Common.INITIAL_GRAIN+" 0");

                bw.newLine();
                bw.flush();

                bw2.newLine();
                bw2.flush();

                // TEST
                System.out.println("Grain # "+grain_counter+" represents specimen facet and consists of "+material);
                
              }
            }
            
            bw.close();
            bw2.close();
            
            completed_steps.setText(completed_steps.getText()+"\nName of 1st file with grain properties: "+grains_file);
            completed_steps.setText(completed_steps.getText()+"\nName of 2nd file with grain properties: "+grains_file_2+"\n");
            System.out.print("\nName of 1st created file with information about grain properties: "+grains_file);
            System.out.print("\nName of 2nd created file with information about grain properties: "+grains_file_2+"\n");
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            System.out.println("Error: "+io_exc);
        }
        
        if(!determine_grain_angles)
        {
            completed_steps.setText(completed_steps.getText()+"\nName of 1st existing file with grain properties: "+grains_file);
            completed_steps.setText(completed_steps.getText()+"\nName of 2nd existing file with grain properties: "+grains_file_2+"\n");
            System.out.print("\nName of 1st existing file with information about grain properties: "+grains_file);
            System.out.print("\nName of 2nd existing file with information about grain properties: "+grains_file_2+"\n");
        }
    }
    
    /** The method copies file with information about grain colours to task directory.
     */
    private boolean copyGrainColoursFile()
    {
        boolean no_file_error = false;
        
        String string;
        
        try
        {
            // Reader of grain indices and grain colours
            BufferedReader reader = new BufferedReader(new FileReader(grain_colours_file));

            // Writer of grain indices and grain colours
            BufferedWriter bw_gr_clrs = new BufferedWriter(new FileWriter(grain_colours_file_2));
                        
            while(reader.ready())
            {
                string = reader.readLine();                
                bw_gr_clrs.write(string);
                bw_gr_clrs.newLine();
                bw_gr_clrs.flush();
            }

            reader.close();
            bw_gr_clrs.close();
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            System.out.println("Error: "+io_exc);
            
            no_file_error = true;
        }

        completed_steps.setText(completed_steps.getText()+"\nName of read file with grain colours:    "+grain_colours_file);
        completed_steps.setText(completed_steps.getText()+"\nName of written file with grain colours: "+grain_colours_file_2+"\n");
        System.out.print("\nName of read file with grain colours:    "+grain_colours_file);
        System.out.print("\nName of written file with grain colours: "+grain_colours_file_2+"\n");
        
        return no_file_error;
    }
    
    /** The method copies file with information about grain properties to task directory.
     * @param grain_colours_file name of file with information about grain properties
     */
    private boolean copyGrainsFile()
    {
        boolean no_file_error = false;
        
        String string;

        try
        {
            // Reader of grain indices and grain colours
            BufferedReader reader = new BufferedReader(new FileReader(grains_file));

            // Writer of grain indices and grain colours
            BufferedWriter bw_gr_clrs = new BufferedWriter(new FileWriter(grains_file_2));
                        
            while(reader.ready())
            {
                string = reader.readLine();                
                bw_gr_clrs.write(string);
                bw_gr_clrs.newLine();
                bw_gr_clrs.flush();                
            }

            reader.close();
            bw_gr_clrs.close();
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            System.out.println("Error: "+io_exc);
            
            no_file_error = true;
        }

        completed_steps.setText(completed_steps.getText()+"\nName of read file with grain angles:    "+grains_file);
        completed_steps.setText(completed_steps.getText()+"\nName of written file with grain angles: "+grains_file_2+"\n");
        System.out.print("\nName of read file with grain angles:    "+grains_file);
        System.out.print("\nName of written file with grain angles: "+grains_file_2+"\n");
        
        return no_file_error;
    }

    /** The method reads values of fields of the class from the file.
     * @param file_name name of file containing values of fields of the class
     */
    private void readTask(String file_name)
    {
      task_properties = new Properties();

      try
      {
        FileInputStream fin = new FileInputStream(file_name);
        task_properties.load(fin);
        fin.close();

        completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+file_name);

        // Reading of names of files used for creation of files with initial and boundary conditions
        phases_file_name       = task_properties.getProperty("phases_file_name");
        init_cond_input_file   = task_properties.getProperty("init_cond_input_file");
        init_params_file       = task_properties.getProperty("init_params_file");
        bound_params_file      = task_properties.getProperty("bound_params_file");
        tanks_file             = task_properties.getProperty("tanks_file");
        bound_grains_file_name = task_properties.getProperty("bound_grains_file_name");
        layers_file_name       = task_properties.getProperty("layers_file_name");

        // Reading of names of files with initial and boundary conditions
        init_cond_file    = task_properties.getProperty("init_cond_file");
        bound_cond_file   = task_properties.getProperty("bound_cond_file");
        specimen_file     = task_properties.getProperty("specimen_file");

        grain_struct_file = task_properties.getProperty("grain_struct_file");
        heat_conduct_file = task_properties.getProperty("heat_conduct_file");
        heat_capacity_file= task_properties.getProperty("heat_capacity_file");
        grains_file       = task_properties.getProperty("grains_file");
        grains_file_2     = task_properties.getProperty("grains_file_2");
        bound_cond_file_geometry = task_properties.getProperty("bound_cond_file_geometry");
        init_cond_file_geometry  = task_properties.getProperty("init_cond_file_geometry");
        grain_colours_file  = task_properties.getProperty("grain_colours_file");
        grain_colours_file_2  = task_properties.getProperty("grain_colours_file_2");

        extreme_values_file = TASK_PATH+task_name+"/"+task_name+"."+Common.EXTREME_VALUES_FILE_EXTENSION;

        // Reading of time step
        time_step         = (new Double(task_properties.getProperty("time_step"))).doubleValue();

        // Reading of task parameters
        specimen_size_X   = (new Double(task_properties.getProperty("specimen_size_X"))).doubleValue();
        specimen_size_Y   = (new Double(task_properties.getProperty("specimen_size_Y"))).doubleValue();
        specimen_size_Z   = (new Double(task_properties.getProperty("specimen_size_Z"))).doubleValue();
        surf_thickness    = (new Double(task_properties.getProperty("surf_thickness"))).doubleValue();
        cell_size         = (new Double(task_properties.getProperty("cell_size"))).doubleValue();
        
        // Reading of automata packing type
        packing_type      = (new Byte(task_properties.getProperty("packing_type"))).byteValue();

        if(packing_type==Common.HEXAGONAL_CLOSE_PACKING)
        {
            cell_number_I     = (int)Math.round((specimen_size_X/(2*cell_radius)-1)/Math.sqrt(3.0/4.0) + 1);
            cell_number_J     = (int)Math.round( specimen_size_Y/(2*cell_radius));
            cell_number_K     = (int)Math.round((specimen_size_Z/(2*cell_radius)-1)/Math.sqrt(2.0/3.0) + 1);
        }

        if(packing_type==Common.SIMPLE_CUBIC_PACKING)
        {
            cell_number_I     = (int)Math.floor((specimen_size_X/cell_radius+1)/2);
            cell_number_J     = (int)Math.floor((specimen_size_Y/cell_radius+1)/2);
            cell_number_K     = (int)Math.floor((specimen_size_Z/cell_radius+1)/2);            
        }
        
        cellNumber        = (cell_number_I+2)*(cell_number_J+2)*(cell_number_K+2);

        bulk_material     = new String(task_properties.getProperty("bulk_material"));

        element_number_X          = (new Integer(task_properties.getProperty("element_number_x"))).intValue();
        element_number_Y          = (new Integer(task_properties.getProperty("element_number_y"))).intValue();
        element_number_Z          = (new Integer(task_properties.getProperty("element_number_z"))).intValue();

        particles_volume_fraction = (new Double(task_properties.getProperty(UICommon.PARTICLE_VOLUME_FRACTION_PROPERTIES))).doubleValue();
        particle_radius           = (new Double(task_properties.getProperty("particle_radius"))).doubleValue();
        min_neighbours_number     = (new Integer(task_properties.getProperty(UICommon.MIN_NEIGHBOURS_NUMBER_PROPERTIES))).intValue();
        show_grain_bounds         = (new Byte(task_properties.getProperty("show_grain_bounds"))).byteValue();

        bound_interaction_type    = (new Byte(task_properties.getProperty("bound_interaction_type"))).byteValue();
        bound_function_type       = (new Byte(task_properties.getProperty("bound_function_type"))).byteValue();
        
        // Variable for choice of type of boundary conditions
        bound_type                = (new Byte(task_properties.getProperty("bound_type"))).byteValue();
        
        byte anis_presence        = Byte.valueOf(task_properties.getProperty(UICommon.ANISOTROPY_PRESENCE_PROPERTIES)).byteValue();
        
        spec_anis_coeff           = (new Double(task_properties.getProperty(UICommon.SPECIMEN_ANIS_COEFF_PROPERTIES))).doubleValue();
        
        anis_vector_X             = (new Double(task_properties.getProperty(UICommon.ANISOTROPY_VECTOR_X_PROPERTIES))).doubleValue();
        anis_vector_Y             = (new Double(task_properties.getProperty(UICommon.ANISOTROPY_VECTOR_Y_PROPERTIES))).doubleValue();
        anis_vector_Z             = (new Double(task_properties.getProperty(UICommon.ANISOTROPY_VECTOR_Z_PROPERTIES))).doubleValue();
        
        outer_anis_coeff          = (new Double(task_properties.getProperty(UICommon.OUTER_ANIS_COEFF_PROPERTIES))).doubleValue();
        
        outer_anis_vector_X       = (new Double(task_properties.getProperty(UICommon.OUTER_ANIS_VECTOR_X_PROPERTIES))).doubleValue();
        outer_anis_vector_Y       = (new Double(task_properties.getProperty(UICommon.OUTER_ANIS_VECTOR_Y_PROPERTIES))).doubleValue();
        outer_anis_vector_Z       = (new Double(task_properties.getProperty(UICommon.OUTER_ANIS_VECTOR_Z_PROPERTIES))).doubleValue();
        
        if(anis_presence == 0)
            anisotropy = false;
        else
            anisotropy = true;
      }
      catch(IOException io_ex)
      {
          completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
      }
    }

    /** The method creates file with parameters of specimen.
     * @param specimen_file name of created file with parameters of specimen
     * @param task_info_file file with short information about task
     */
    private void createSpecimenFile(String _specimen_file, String task_info_file)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(_specimen_file));
            BufferedWriter bw1= new BufferedWriter(new FileWriter(task_info_file));

            // Power of cell size value
            int cell_size_power = (int)Math.floor(Math.log10(cell_size));

            // Number for rounding of values
            double round_number = Math.pow(10, -cell_size_power+3);

            // Value of cell size in meters
            cell_size = (1/round_number)*(int)Math.round(round_number*cell_size);

            // Rounded values of specimen sizes in meters
            double specimen_size_X_meters = (1/round_number)*(int)Math.round(round_number*(cell_number_I+2)*cell_size);
            double specimen_size_Y_meters = (1/round_number)*(int)Math.round(round_number*(cell_number_J+2)*cell_size);
            double specimen_size_Z_meters = (1/round_number)*(int)Math.round(round_number*(cell_number_K+2)*cell_size);

            specimen_size_X_meters = (1/round_number)*(int)Math.round(round_number*specimen_size_X_meters);
            specimen_size_Y_meters = (1/round_number)*(int)Math.round(round_number*specimen_size_Y_meters);
            specimen_size_Z_meters = (1/round_number)*(int)Math.round(round_number*specimen_size_Z_meters);
            
            bw1.write("# Name of file with parameters of grain structure");
            bw1.newLine();
            bw1.write("grains_file_2       = "+grains_file_2);
            bw1.newLine();
            bw1.newLine();
            bw1.write("# Name of file with minimal and maximal values of temperature and stress");
            bw1.newLine();
            bw1.write("# for initial specimen including outer boundary cells");
            bw1.newLine();
            bw1.write("extreme_values_file = "+extreme_values_file);
            bw1.newLine();
            bw1.newLine();
            bw1.write("# Name of file with parameters of specimen layers");
            bw1.newLine();
            bw1.write("layers_file_name    = "+layers_file_name);
            bw1.newLine();
            bw1.newLine();
            bw1.write("# Variable responsible for type of packing of cellular automata:");
            bw1.newLine();
            bw1.write("# 0 - hexagonal close packing (HCP),");
            bw1.newLine();
            bw1.write("# 1 - simple cubic packing (SCP).");
            bw1.newLine();
            bw1.write("packing_type = " + packing_type);
            bw1.newLine();
            bw1.newLine();
            bw1.write("# Number of cells in finite element in direction of X-axis");
            bw1.newLine();
            bw1.write("cell_number_X       = "+(cell_number_I+2));
            bw1.newLine();
            bw1.newLine();
            bw1.write("# Number of cells in finite element in direction of Y-axis");
            bw1.newLine();
            bw1.write("cell_number_Y       = "+(cell_number_J+2));
            bw1.newLine();
            bw1.newLine();
            bw1.write("# Number of cells in finite element in direction of Z-axis");
            bw1.newLine();
            bw1.write("cell_number_Z       = "+(cell_number_K+2));
            bw1.newLine();
            bw1.newLine();
            bw1.write("# Variable responsible for showing of grain boundaries");
            bw1.newLine();
            bw1.write("show_grain_bounds   = "+show_grain_bounds);
            bw1.newLine();
            bw1.newLine();
            
          //  int log10_cell_size = (int)Math.round(Math.log10(cell_size));
          //  cell_size = (int)Math.round(cell_size*Math.pow(10, 15 - log10_cell_size));
          //  cell_size = cell_size*Math.pow(10, log10_cell_size - 15);
             
            bw1.write("# Cell size, in meters");
            bw1.newLine();
            bw1.write("cell_size           = "+cell_size);
            bw1.newLine();
            
          //  bw1.newLine();
          //  bw1.write("# Total number of grains, inner ("+grain_number+") and outer ("+outer_grain_number+")");
          //  bw1.write("total_grain_number = "+(grain_number + outer_grain_number));

            bw.write(bulk_material);
            bw.newLine();
            bw.newLine();
            bw.write("# Name of file with parameters of grain structure");
            bw.newLine();
            bw.write("grains_file      = "+grains_file);
            bw.newLine();
            bw.newLine();
            bw.write("# Variable responsible for type of packing of cellular automata:");
            bw.newLine();
            bw.write("# 0 - hexagonal close packing (HCP),");
            bw.newLine();
            bw.write("# 1 - simple cubic packing (SCP).");
            bw.newLine();
            bw.write("packing_type = " + packing_type);
            bw.newLine();
            bw.newLine();
            bw.write("# Size of specimen in direction of X-axis (in meters)");
            bw.newLine();
            bw.write("specimen_size_X  =  "+specimen_size_X_meters);
            bw.newLine();
            bw.newLine();
            bw.write("# Size of specimen in direction of Y-axis (in meters)");
            bw.newLine();
            bw.write("specimen_size_Y  =  "+specimen_size_Y_meters);
            bw.newLine();
            bw.newLine();
            bw.write("# Size of specimen in direction of Z-axis (in meters)");
            bw.newLine();
            bw.write("specimen_size_Z  =  "+specimen_size_Z_meters);
            bw.newLine();
            bw.newLine();
            bw.write("# Number of finite elements in direction of X-axis");
            bw.newLine();
            bw.write("element_number_X =  "+element_number_X);
            bw.newLine();
            bw.newLine();
            bw.write("# Number of finite elements in direction of Y-axis");
            bw.newLine();
            bw.write("element_number_Y =  "+element_number_Y);
            bw.newLine();
            bw.newLine();
            bw.write("# Number of finite elements in direction of Z-axis");
            bw.newLine();
            bw.write("element_number_Z =  "+element_number_Z);
            bw.newLine();
            bw.newLine();
            bw.write("# Number of cells in finite element in direction of X-axis");
            bw.newLine();
            bw.write("cell_number_X    = "+(cell_number_I+2));
            bw.newLine();
            bw.newLine();
            bw.write("# Number of cells in finite element in direction of Y-axis");
            bw.newLine();
            bw.write("cell_number_Y    = "+(cell_number_J+2));
            bw.newLine();
            bw.newLine();
            bw.write("# Number of cells in finite element in direction of Z-axis");
            bw.newLine();
            bw.write("cell_number_Z    = "+(cell_number_K+2));
            bw.newLine();
            bw.newLine();
            bw.write("# Volume fraction of particles");
            bw.newLine();
            bw.write("particles_volume_fraction =   "+particles_volume_fraction);
            bw.newLine();
            bw.newLine();
            bw.write("# Radius of particle");
            bw.newLine();
            bw.write("particle_radius =   "+particle_radius);
            bw.newLine();
            bw.newLine();
            bw.write("# Minimal number of neighbour cells in adjacent grain necessary");
            bw.newLine();
            bw.write("# for transition of 'central' cell to adjacent grain");
            bw.newLine();
            bw.write("min_neighbours_number     = "+min_neighbours_number);
            bw.newLine();
            bw.newLine();
            bw.write("# Type of interaction of boundary cells with neighbours:");
            bw.newLine();
            bw.write("# 0 - boundary cell does not change mechanical energy because of interaction with neighbours;");
            bw.newLine();
            bw.write("# 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;");
            bw.newLine();
            bw.write("# 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.");
            bw.newLine();
            bw.write("bound_interaction_type = "+bound_interaction_type);
            bw.newLine();
            bw.newLine();
            bw.write("# Type of functional dependence of boundary cell parameter on its coordinates");
            bw.newLine();
            bw.write("bound_function_type = "+bound_function_type);
            bw.newLine();
            bw.newLine();
            bw.write("# Type of functional dependence of boundary cell parameter on time");
            bw.newLine();
            bw.write("bound_time_function_type = "+bound_time_function_type);
            bw.newLine();
            bw.newLine();
            bw.write("# Portion of total time period when boundary cells are loaded");
            bw.newLine();
            bw.write("bound_load_time_portion = "+bound_load_time_portion);
            bw.newLine();  
            bw.newLine();
            
            bw.write("# Variable responsible for account of anisotropy of simulated medium");
            bw.newLine();
            
            if(anisotropy)   
            bw.write("anisotropy        = 1");
            else             
            bw.write("anisotropy        = 0");
            bw.newLine();
            bw.newLine();
            
            bw.write("# Coefficient of anisotropy of simulated medium");
            bw.newLine();
            bw.write("spec_anis_coeff   = "+spec_anis_coeff);
            bw.newLine();
            bw.newLine();
            
            bw.write("# Vector of anisotropy of simulated medium");
            bw.newLine();
            bw.write("anis_vector_X     = "+anis_vector_X);
            bw.newLine();
            bw.write("anis_vector_Y     = "+anis_vector_Y);
            bw.newLine();
            bw.write("anis_vector_Z     = "+anis_vector_Z);
            bw.newLine();
            bw.newLine();
            
            bw.write("# Coefficient of outer anisotropy");
            bw.newLine();
            bw.write("outer_anis_coeff   = "+outer_anis_coeff);
            bw.newLine();
            bw.newLine();
            
            bw.write("# Vector of outer anisotropy");
            bw.newLine();
            bw.write("outer_anis_vector_X     = "+outer_anis_vector_X);
            bw.newLine();
            bw.write("outer_anis_vector_Y     = "+outer_anis_vector_Y);
            bw.newLine();
            bw.write("outer_anis_vector_Z     = "+outer_anis_vector_Z);
            bw.newLine();
            bw.newLine();
            
            bw.flush();
            bw.close();
            bw1.flush();
            bw1.close();

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+_specimen_file);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+task_info_file);
        }
        catch(IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
        }
    }

    /** The method calculates single index of 3D cell with certain indices
     * in 3D specimen with certain sizes.
     * @param indexI 1st index of 3D cell
     * @param indexJ 2nd index of 3D cell
     * @param indexK 3rd index of 3D cell
     * @param sizeI size of specimen (in cells) along X axis
     * @param sizeJ size of specimen (in cells) along Y axis
     * @param sizeK size of specimen (in cells) along Z axis
     * @return single index of 3D cell.
     */
    public int calcSingleIndex(int indexI, int indexJ, int indexK,
                               int sizeI,  int sizeJ,  int sizeK)
    {
        // Single index of cell3D.
        int single_index = -1;

        // Calculation of single index of cell3D.
        if ((indexI > -1)&(indexI < sizeI)&
            (indexJ > -1)&(indexJ < sizeJ)&
            (indexK > -1)&(indexK < sizeK))
        {
            single_index = indexI + sizeI*indexJ + sizeI*sizeJ*indexK;
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
        private double[] calcCoordinates(int index1, int index2, int index3)
        {
            // Coordinates of cell
            double coord_X=0, coord_Y=0, coord_Z=0;

            // Calculation of coordinates of cell for hexagonal close packing of automata
            if(packing_type==Common.HEXAGONAL_CLOSE_PACKING)
            {
              if(index3 % 3 == 0)
              {
                if(index1 % 2 == 0)
                {
                    coord_X = cell_radius*(1 + index1*Math.sqrt(3));
                    coord_Y = cell_radius*(1 + index2*2);
                }
                else
                {
                    coord_X = cell_radius*(1 + index1*Math.sqrt(3));
                    coord_Y = cell_radius*(2 + index2*2);
                }
              }

              if(index3 % 3 == 1)
              {
                if(index1 % 2 == 0)
                {
                    coord_X = cell_radius*(1 + 1/Math.sqrt(3) + index1*Math.sqrt(3));
                    coord_Y = cell_radius*(2 + index2*2);
                }
                else
                {
                    coord_X = cell_radius*(1 + 1/Math.sqrt(3)+ index1*Math.sqrt(3));
                    coord_Y = cell_radius*(1 + index2*2);
                }
              }

              if(index3 % 3 == 2)
              {
                if(index1 % 2 == 0)
                {
                    coord_X = cell_radius*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));
                    coord_Y = cell_radius*(1 + index2*2);
                }
                else
                {
                    coord_X = cell_radius*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));
                    coord_Y = cell_radius*(2 + index2*2);
                }
              }
              
              coord_Z = cell_radius*(1 + 2*index3*Math.sqrt(2.0/3.0));
            }

            // Calculation of coordinates of cell for simple cubic packing of automata
            if(packing_type==Common.SIMPLE_CUBIC_PACKING)
            {
                coord_X = cell_radius*(1 + 2*index1);
                coord_Y = cell_radius*(1 + 2*index2);
                coord_Z = cell_radius*(1 + 2*index3);
            }

            double[] coordinates = new double[3];

            coordinates[0] = coord_X;
            coordinates[1] = coord_Y;
            coordinates[2] = coord_Z;

            return coordinates;
        }

        /** This method creates file with information about properties of outer boundary cells:
         * influx of thermal energy, influx of mechanical energy, temperature.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        private void createBoundCondFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                        int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            //    Parameters characterizing mechanical and thermal loading of each facet:
            // "mechanical" parameter is equal to constant stress or constant strain,
            // "thermal"    parameter is equal to constant temperature or
            //              constant influx of specific surface heat energy.
            double _top_mechanical_param = 0;
            double _top_thermal_param = 0;

            double _bottom_mechanical_param = 0;
            double _bottom_thermal_param = 0;

            double _left_mechanical_param = 0;
            double _left_thermal_param = 0;

            double _right_mechanical_param = 0;
            double _right_thermal_param = 0;

            double _front_mechanical_param = 0;
            double _front_thermal_param = 0;

            double _back_mechanical_param = 0;
            double _back_thermal_param = 0;

            // Sizes of specimen simulated
     //       double spec_size_X = 0.5*cell_size*(3 + 1/Math.sqrt(3.0) + _cell_number_I*Math.sqrt(3.0));
       //     double spec_size_Y = 0.5*cell_size*(4 + _cell_number_J*2);
         //   double spec_size_Z = 0.5*cell_size*(3 + 2*_cell_number_K*Math.sqrt(2.0/3.0));

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);

                //    Parameters characterizing mechanical and thermal loading of each facet:
                // "mechanical" parameter is equal to constant stress or constant strain,
                // "thermal"    parameter is equal to constant temperature or
                //              constant influx of specific surface heat energy.
                _left_mechanical_param    = (new Double(table.getCell(0, 0))).doubleValue();
                _left_thermal_param       = (new Double(table.getCell(0, 1))).doubleValue();

                _front_mechanical_param   = (new Double(table.getCell(1, 0))).doubleValue();
                _front_thermal_param      = (new Double(table.getCell(1, 1))).doubleValue();

                _top_mechanical_param     = (new Double(table.getCell(2, 0))).doubleValue();
                _top_thermal_param        = (new Double(table.getCell(2, 1))).doubleValue();

                _bottom_mechanical_param  = (new Double(table.getCell(3, 0))).doubleValue();
                _bottom_thermal_param     = (new Double(table.getCell(3, 1))).doubleValue();

                _back_mechanical_param    = (new Double(table.getCell(4, 0))).doubleValue();
                _back_thermal_param       = (new Double(table.getCell(4, 1))).doubleValue();

                _right_mechanical_param   = (new Double(table.getCell(5, 0))).doubleValue();
                _right_thermal_param      = (new Double(table.getCell(5, 1))).doubleValue();

                // Division of mechanical energy influxes through facets on cells
          /*
                _left_mechanical_param    = _left_mechanical_param*spec_size_Y*spec_size_Z/(_cell_number_J*_cell_number_K);

                _front_mechanical_param   = _front_mechanical_param*spec_size_X*spec_size_Z/(_cell_number_I*_cell_number_K);

                _top_mechanical_param     = _top_mechanical_param*spec_size_X*spec_size_Y/(_cell_number_I*_cell_number_J);

                _bottom_mechanical_param  = _bottom_mechanical_param*spec_size_X*spec_size_Y/(_cell_number_I*_cell_number_J);

                _back_mechanical_param    = _back_mechanical_param*spec_size_X*spec_size_Z/(_cell_number_I*_cell_number_K);

                _right_mechanical_param   = _right_mechanical_param*spec_size_Y*spec_size_Z/(_cell_number_J*_cell_number_K);

                // Division of thermal energy influxes through facets on cells
               _left_thermal_param       = _left_thermal_param*spec_size_Y*spec_size_Z/(_cell_number_J*_cell_number_K);

               _front_thermal_param      = _front_thermal_param*spec_size_X*spec_size_Z/(_cell_number_I*_cell_number_K);

               _top_thermal_param        = _top_thermal_param*spec_size_X*spec_size_Y/(_cell_number_I*_cell_number_J);

               _bottom_thermal_param     = _bottom_thermal_param*spec_size_X*spec_size_Y/(_cell_number_I*_cell_number_J);

               _back_thermal_param       = _back_thermal_param*spec_size_X*spec_size_Z/(_cell_number_I*_cell_number_K);

               _right_thermal_param      = _right_thermal_param*spec_size_Y*spec_size_Z/(_cell_number_J*_cell_number_K);
       */
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Number of outer boundary cells
            int bound_cell_number = (_cell_number_I+2)*(_cell_number_J+2)*(_cell_number_K+2)-
                                    _cell_number_I*_cell_number_J*_cell_number_K;

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                bw.write("#   This file contains parameters of each outer boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, mechanical parameter (strain or stress of CA),");
                bw.newLine();
                bw.write("# thermal parameter (temperature or thermal energy influx).");
                bw.newLine();
                bw.flush();

                bw1.write("#   This file contains information about outer boundary cellular automata (CA):");
                bw1.newLine();
                bw1.write("# in 1st string - number of  outer boundary CA,");
                bw1.newLine();
                bw1.write("# in each further string - parameters of each outer boundary CA:");
                bw1.newLine();
                bw1.write("# 3 coordinates of CA, mechanical parameter (strain or stress) of CA,");
                bw1.newLine();
                bw1.write("# thermal parameter (temperature or thermal energy influx).");
                bw1.newLine();
                bw1.flush();
                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
                bw1.newLine();
                bw1.flush();

                boolean write_string;

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    write_string = true;

                    if(cell_counter_I==0)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" "+_left_mechanical_param+" "+_left_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                  _left_mechanical_param+" "+_left_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        write_string = false;
                    }

                    if(cell_counter_I==_cell_number_I+1)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen bottom facet
                        bw.write(bound_cell_index+" "+_right_mechanical_param+" "+_right_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                  _right_mechanical_param+" "+_right_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        write_string = false;
                    }

                    if(cell_counter_J==0)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen left facet
                        bw.write(bound_cell_index+" "+_front_mechanical_param +" "+_front_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                _front_mechanical_param +" "+ _front_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        write_string = false;
                    }

                    if(cell_counter_J==_cell_number_J+1)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen right facet
                        bw.write(bound_cell_index+" "+_back_mechanical_param +" "+_back_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                _back_mechanical_param +" "+ _back_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        write_string = false;
                    }

                    if(cell_counter_K==0)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen front facet
                        bw.write(bound_cell_index+" "+_top_mechanical_param +" "+_top_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                 _top_mechanical_param +" "+_top_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        write_string = false;
                    }

                    if(cell_counter_K==_cell_number_K+1)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen back facet
                        bw.write(bound_cell_index+" "+_bottom_mechanical_param +" "+ _bottom_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                               _bottom_mechanical_param +" "+  _bottom_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        write_string = false;
                    }

                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** This method creates file with information about properties
         * of outer and internal (in tanks on each facet) boundary cells:
         * influx of thermal energy, influx of mechanical energy, temperature.
         * @param bound_params_file file with information about conditions on each of facets
         * @param tanks_file file with information about parameters of tanks
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        private void createBoundCondTanksFile(String bound_params_file, String tanks_file, String write_file_name, String bound_cond_geometry_file,
                                              int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            //    Parameters characterizing mechanical and thermal loading of each facet:
            // "mechanical" parameter is equal to constant stress or constant strain,
            // "thermal"    parameter is equal to constant temperature or
            //              constant influx of specific surface heat energy.            
            double _left_mechanical_param = 0;
            double _left_thermal_param = 0;
            
            double _front_mechanical_param = 0;
            double _front_thermal_param = 0;
            
            double _top_mechanical_param = 0;
            double _top_thermal_param = 0;

            double _bottom_mechanical_param = 0;
            double _bottom_thermal_param = 0;

            double _back_mechanical_param = 0;
            double _back_thermal_param = 0;
            
            double _right_mechanical_param = 0;
            double _right_thermal_param = 0;

            //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
            // "mech" parameter is equal to constant rate of stress or strain change,
            // "heat" parameter is equal to constant temperature or
            //        constant influx of specific surface heat energy.
            double _left_tank_min_coord_X = 0;
            double _left_tank_max_coord_X = 0;
            double _left_tank_min_coord_Y = 0;
            double _left_tank_max_coord_Y = 0;
            double _left_tank_min_coord_Z = 0;
            double _left_tank_max_coord_Z = 0;
            double _left_tank_mech_param  = 0;
            double _left_tank_heat_param  = 0;
                        
            double _front_tank_min_coord_X = 0;
            double _front_tank_max_coord_X = 0;
            double _front_tank_min_coord_Y = 0;
            double _front_tank_max_coord_Y = 0;
            double _front_tank_min_coord_Z = 0;
            double _front_tank_max_coord_Z = 0;
            double _front_tank_mech_param  = 0;
            double _front_tank_heat_param  = 0;
            
            double _top_tank_min_coord_X = 0;
            double _top_tank_max_coord_X = 0;
            double _top_tank_min_coord_Y = 0;
            double _top_tank_max_coord_Y = 0;
            double _top_tank_min_coord_Z = 0;
            double _top_tank_max_coord_Z = 0;
            double _top_tank_mech_param  = 0;
            double _top_tank_heat_param  = 0;
            
            double _bottom_tank_min_coord_X = 0;
            double _bottom_tank_max_coord_X = 0;
            double _bottom_tank_min_coord_Y = 0;
            double _bottom_tank_max_coord_Y = 0;
            double _bottom_tank_min_coord_Z = 0;
            double _bottom_tank_max_coord_Z = 0;
            double _bottom_tank_mech_param  = 0;
            double _bottom_tank_heat_param  = 0;
            
            double _back_tank_min_coord_X = 0;
            double _back_tank_max_coord_X = 0;
            double _back_tank_min_coord_Y = 0;
            double _back_tank_max_coord_Y = 0;
            double _back_tank_min_coord_Z = 0;
            double _back_tank_max_coord_Z = 0;
            double _back_tank_mech_param  = 0;
            double _back_tank_heat_param  = 0;
            
            double _right_tank_min_coord_X = 0;
            double _right_tank_max_coord_X = 0;
            double _right_tank_min_coord_Y = 0;
            double _right_tank_max_coord_Y = 0;
            double _right_tank_min_coord_Z = 0;
            double _right_tank_max_coord_Z = 0;
            double _right_tank_mech_param  = 0;
            double _right_tank_heat_param  = 0;

            // Sizes of specimen simulated
     //       double spec_size_X = 0.5*cell_size*(3 + 1/Math.sqrt(3.0) + _cell_number_I*Math.sqrt(3.0));
       //     double spec_size_Y = 0.5*cell_size*(4 + _cell_number_J*2);
         //   double spec_size_Z = 0.5*cell_size*(3 + 2*_cell_number_K*Math.sqrt(2.0/3.0));
            
            try
            {
                // Reading ow information about boundary facets
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary facets:    "+bound_params_file);

                //    Parameters characterizing mechanical and thermal loading of each facet:
                // "mechanical" parameter is equal to constant rate of stress or strain change,
                // "thermal"    parameter is equal to constant temperature or
                //              constant influx of specific surface heat energy.
                _left_mechanical_param    = (new Double(table.getCell(0, 0))).doubleValue();
                _left_thermal_param       = (new Double(table.getCell(0, 1))).doubleValue();

                _front_mechanical_param   = (new Double(table.getCell(1, 0))).doubleValue();
                _front_thermal_param      = (new Double(table.getCell(1, 1))).doubleValue();

                _top_mechanical_param     = (new Double(table.getCell(2, 0))).doubleValue();
                _top_thermal_param        = (new Double(table.getCell(2, 1))).doubleValue();

                _bottom_mechanical_param  = (new Double(table.getCell(3, 0))).doubleValue();
                _bottom_thermal_param     = (new Double(table.getCell(3, 1))).doubleValue();

                _back_mechanical_param    = (new Double(table.getCell(4, 0))).doubleValue();
                _back_thermal_param       = (new Double(table.getCell(4, 1))).doubleValue();

                _right_mechanical_param   = (new Double(table.getCell(5, 0))).doubleValue();
                _right_thermal_param      = (new Double(table.getCell(5, 1))).doubleValue();
                
                // Reading of information about boundary tanks             
                reader = new TextTableFileReader(tanks_file);
                table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary tanks:    "+tanks_file);
                System.out.println("Name of read file with parameters of boundary tanks:    "+tanks_file);
                
                //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
                // "mech" parameter is equal to constant rate of stress or strain change,
                // "heat" parameter is equal to constant temperature or
                //        constant influx of specific surface heat energy.
                _left_tank_min_coord_X = (new Double(table.getCell(0, 1))).doubleValue();
                _left_tank_max_coord_X = (new Double(table.getCell(0, 2))).doubleValue();
                _left_tank_min_coord_Y = (new Double(table.getCell(0, 3))).doubleValue();
                _left_tank_max_coord_Y = (new Double(table.getCell(0, 4))).doubleValue();
                _left_tank_min_coord_Z = (new Double(table.getCell(0, 5))).doubleValue();
                _left_tank_max_coord_Z = (new Double(table.getCell(0, 6))).doubleValue();
                _left_tank_mech_param  = (new Double(table.getCell(0, 9))).doubleValue();
                _left_tank_heat_param  = (new Double(table.getCell(0,10))).doubleValue();
                   
                _front_tank_min_coord_X = (new Double(table.getCell(1, 1))).doubleValue();
                _front_tank_max_coord_X = (new Double(table.getCell(1, 2))).doubleValue();
                _front_tank_min_coord_Y = (new Double(table.getCell(1, 3))).doubleValue();
                _front_tank_max_coord_Y = (new Double(table.getCell(1, 4))).doubleValue();
                _front_tank_min_coord_Z = (new Double(table.getCell(1, 5))).doubleValue();
                _front_tank_max_coord_Z = (new Double(table.getCell(1, 6))).doubleValue();
                _front_tank_mech_param  = (new Double(table.getCell(1, 9))).doubleValue();
                _front_tank_heat_param  = (new Double(table.getCell(1,10))).doubleValue();                
                   
                _top_tank_min_coord_X = (new Double(table.getCell(2, 1))).doubleValue();
                _top_tank_max_coord_X = (new Double(table.getCell(2, 2))).doubleValue();
                _top_tank_min_coord_Y = (new Double(table.getCell(2, 3))).doubleValue();
                _top_tank_max_coord_Y = (new Double(table.getCell(2, 4))).doubleValue();
                _top_tank_min_coord_Z = (new Double(table.getCell(2, 5))).doubleValue();
                _top_tank_max_coord_Z = (new Double(table.getCell(2, 6))).doubleValue();
                _top_tank_mech_param  = (new Double(table.getCell(2, 9))).doubleValue();
                _top_tank_heat_param  = (new Double(table.getCell(2,10))).doubleValue();
                         
                _bottom_tank_min_coord_X = (new Double(table.getCell(3, 1))).doubleValue();
                _bottom_tank_max_coord_X = (new Double(table.getCell(3, 2))).doubleValue();
                _bottom_tank_min_coord_Y = (new Double(table.getCell(3, 3))).doubleValue();
                _bottom_tank_max_coord_Y = (new Double(table.getCell(3, 4))).doubleValue();
                _bottom_tank_min_coord_Z = (new Double(table.getCell(3, 5))).doubleValue();
                _bottom_tank_max_coord_Z = (new Double(table.getCell(3, 6))).doubleValue();
                _bottom_tank_mech_param  = (new Double(table.getCell(3, 9))).doubleValue();
                _bottom_tank_heat_param  = (new Double(table.getCell(3,10))).doubleValue();                
                
                _back_tank_min_coord_X = (new Double(table.getCell(4, 1))).doubleValue();
                _back_tank_max_coord_X = (new Double(table.getCell(4, 2))).doubleValue();
                _back_tank_min_coord_Y = (new Double(table.getCell(4, 3))).doubleValue();
                _back_tank_max_coord_Y = (new Double(table.getCell(4, 4))).doubleValue();
                _back_tank_min_coord_Z = (new Double(table.getCell(4, 5))).doubleValue();
                _back_tank_max_coord_Z = (new Double(table.getCell(4, 6))).doubleValue();
                _back_tank_mech_param  = (new Double(table.getCell(4, 9))).doubleValue();
                _back_tank_heat_param  = (new Double(table.getCell(4,10))).doubleValue();                
                
                _right_tank_min_coord_X = (new Double(table.getCell(5, 1))).doubleValue();
                _right_tank_max_coord_X = (new Double(table.getCell(5, 2))).doubleValue();
                _right_tank_min_coord_Y = (new Double(table.getCell(5, 3))).doubleValue();
                _right_tank_max_coord_Y = (new Double(table.getCell(5, 4))).doubleValue();
                _right_tank_min_coord_Z = (new Double(table.getCell(5, 5))).doubleValue();
                _right_tank_max_coord_Z = (new Double(table.getCell(5, 6))).doubleValue();
                _right_tank_mech_param  = (new Double(table.getCell(5, 9))).doubleValue();
                _right_tank_heat_param  = (new Double(table.getCell(5,10))).doubleValue();

                System.out.println("_right_tank_heat_param:    "+_right_tank_heat_param);
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;
            
            // Index of current cell
            int cell_index;

            // Coordinates of current cell
            double[] cell_coordinates;

            // Number of outer boundary cells
            int bound_cell_number = (_cell_number_I+2)*(_cell_number_J+2)*(_cell_number_K+2)-
                                    _cell_number_I*_cell_number_J*_cell_number_K;

            // Number of cells in tanks
            int tank_cell_number = 0;

            //  Number of cells on facets but out of tanks
            int facet_cell_number = 0;

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                bw.write("#   This file contains parameters of each outer boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, mechanical parameter (strain or stress of CA),");
                bw.newLine();
                bw.write("# thermal parameter (temperature or thermal energy influx).");
                bw.newLine();
                bw.flush();

                bw1.write("#   This file contains information about outer boundary cellular automata (CA):");
                bw1.newLine();
                bw1.write("# in 1st string - number of  outer boundary CA,");
                bw1.newLine();
                bw1.write("# in each further string - parameters of each outer boundary CA:");
                bw1.newLine();
                bw1.write("# 3 coordinates of CA, mechanical parameter (strain or stress) of CA,");
                bw1.newLine();
                bw1.write("# thermal parameter (temperature or thermal energy influx).");
                bw1.newLine();
                bw1.flush();
                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
                bw1.newLine();
                bw1.flush();

                boolean write_string;

                bound_cell_number = 0;

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    write_string = true;

                    // Writing of parameters of tank containing current cell
                    if(write_string)
                    {
                        cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                     cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // If current cell is in left tank
                        if(write_string)
                        if((cell_coordinates[0]>=_left_tank_min_coord_X)&(cell_coordinates[0]<=_left_tank_max_coord_X))
                        if((cell_coordinates[1]>=_left_tank_min_coord_Y)&(cell_coordinates[1]<=_left_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_left_tank_min_coord_Z)&(cell_coordinates[2]<=_left_tank_max_coord_Z))
                        {
                            // Writing of physical parameters of boundary cell on specimen left facet
                            bw.write (cell_index+" "+_left_tank_mech_param+" "+_left_tank_heat_param);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                      _left_tank_mech_param+" "+_left_tank_heat_param);

                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();
                            
                            tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                        }

                        // If current cell is in front tank
                        if(write_string)
                        if((cell_coordinates[0]>=_front_tank_min_coord_X)&(cell_coordinates[0]<=_front_tank_max_coord_X))
                        if((cell_coordinates[1]>=_front_tank_min_coord_Y)&(cell_coordinates[1]<=_front_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_front_tank_min_coord_Z)&(cell_coordinates[2]<=_front_tank_max_coord_Z))
                        {
                            // Writing of physical parameters of boundary cell on specimen front facet
                            bw.write (cell_index+" "+_front_tank_mech_param+" "+_front_tank_heat_param);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                      _front_tank_mech_param+" "+_front_tank_heat_param);

                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();

                            tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                        }

                        // If current cell is in top tank
                        if(write_string)
                        if((cell_coordinates[0]>=_top_tank_min_coord_X)&(cell_coordinates[0]<=_top_tank_max_coord_X))
                        if((cell_coordinates[1]>=_top_tank_min_coord_Y)&(cell_coordinates[1]<=_top_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_top_tank_min_coord_Z)&(cell_coordinates[2]<=_top_tank_max_coord_Z))
                        {
                            // Writing of physical parameters of boundary cell on specimen top facet
                            bw.write (cell_index+" "+_top_tank_mech_param+" "+_top_tank_heat_param);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                      _top_tank_mech_param+" "+_top_tank_heat_param);

                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();

                            tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                        }

                        // If current cell is in bottom tank
                        if(write_string)
                        if((cell_coordinates[0]>=_bottom_tank_min_coord_X)&(cell_coordinates[0]<=_bottom_tank_max_coord_X))
                        if((cell_coordinates[1]>=_bottom_tank_min_coord_Y)&(cell_coordinates[1]<=_bottom_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_bottom_tank_min_coord_Z)&(cell_coordinates[2]<=_bottom_tank_max_coord_Z))
                        {
                            // Writing of physical parameters of boundary cell on specimen bottom facet
                            bw.write (cell_index+" "+_bottom_tank_mech_param+" "+_bottom_tank_heat_param);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                      _bottom_tank_mech_param+" "+_bottom_tank_heat_param);

                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();

                            tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                        }

                        // If current cell is in back tank
                        if(write_string)
                        if((cell_coordinates[0]>=_back_tank_min_coord_X)&(cell_coordinates[0]<=_back_tank_max_coord_X))
                        if((cell_coordinates[1]>=_back_tank_min_coord_Y)&(cell_coordinates[1]<=_back_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_back_tank_min_coord_Z)&(cell_coordinates[2]<=_back_tank_max_coord_Z))
                        {
                            // Writing of physical parameters of boundary cell on specimen back facet
                            bw.write (cell_index+" "+_back_tank_mech_param+" "+_back_tank_heat_param);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                      _back_tank_mech_param+" "+_back_tank_heat_param);

                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();

                            tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                        }

                        // If current cell is in right tank
                        if(write_string)
                        if((cell_coordinates[0]>=_right_tank_min_coord_X)&(cell_coordinates[0]<=_right_tank_max_coord_X))
                        if((cell_coordinates[1]>=_right_tank_min_coord_Y)&(cell_coordinates[1]<=_right_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_right_tank_min_coord_Z)&(cell_coordinates[2]<=_right_tank_max_coord_Z))
                        {
                            // Writing of physical parameters of boundary cell on specimen right facet
                            bw.write (cell_index+" "+_right_tank_mech_param+" "+_right_tank_heat_param);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                      _right_tank_mech_param+" "+_right_tank_heat_param);

                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();

                            tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                        }
                    }

                    if(cell_counter_I==0)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" "+_left_mechanical_param+" "+_left_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                  _left_mechanical_param+" "+_left_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_I==_cell_number_I+1)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen bottom facet
                        bw.write(bound_cell_index+" "+_right_mechanical_param+" "+_right_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                  _right_mechanical_param+" "+_right_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_J==0)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen left facet
                        bw.write(bound_cell_index+" "+_front_mechanical_param +" "+_front_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                _front_mechanical_param +" "+ _front_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_J==_cell_number_J+1)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen right facet
                        bw.write(bound_cell_index+" "+_back_mechanical_param +" "+_back_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                  _back_mechanical_param +" "+ _back_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_K==0)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen front facet
                        bw.write(bound_cell_index+" "+_top_mechanical_param +" "+_top_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                 _top_mechanical_param +" "+_top_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_K==_cell_number_K+1)if(write_string)
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Writing of physical parameters of boundary cell on specimen back facet
                        bw.write(bound_cell_index+" "+_bottom_mechanical_param +" "+ _bottom_thermal_param);

                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+
                                  _bottom_mechanical_param +" "+  _bottom_thermal_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }
                }

                bw1.write("# "+bound_cell_number+" = "+facet_cell_number+" + "+tank_cell_number+"\n");
                bw1.flush();

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }
  
        /** This method creates file with information about properties
         * of outer and internal (in tanks on each facet) boundary cells:
         * influx of thermal energy, influx of mechanical energy, temperature.
         * @param bound_params_file file with information about conditions on each of facets
         * @param tanks_file file with information about parameters of tanks
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        private void createVarBoundCondTanksFile(String bound_params_file, String tanks_file, String write_file_name, String bound_cond_geometry_file,
                                              int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Initial temperature
            double init_temperature = 300.0;
            
            //    Parameters characterizing mechanical and thermal loading of each facet:
            // "mechanical" parameter is equal to constant stress or constant strain,
            // "thermal"    parameter is equal to constant temperature or
            //              constant influx of specific surface heat energy.            
            double _left_min_mechanical_param       = 0;
            double _left_mechanical_param           = 0;
            double _left_min_thermal_param          = 0;
            double _left_thermal_param              = 0;
            byte   _left_bound_time_function_type   = 0;
            double _left_bound_load_time_portion    = 0;
            double _left_bound_relax_time_portion   = 0;
            
            double _front_min_mechanical_param      = 0;
            double _front_mechanical_param          = 0;
            double _front_min_thermal_param         = 0;
            double _front_thermal_param             = 0;
            byte   _front_bound_time_function_type  = 0;
            double _front_bound_load_time_portion   = 0;
            double _front_bound_relax_time_portion  = 0;
            
            double _top_min_mechanical_param        = 0;
            double _top_mechanical_param            = 0;
            double _top_min_thermal_param           = 0;
            double _top_thermal_param               = 0;
            byte   _top_bound_time_function_type    = 0;
            double _top_bound_load_time_portion     = 0;
            double _top_bound_relax_time_portion    = 0;

            double _bottom_min_mechanical_param     = 0;
            double _bottom_mechanical_param         = 0;
            double _bottom_min_thermal_param        = 0;
            double _bottom_thermal_param            = 0;
            byte   _bottom_bound_time_function_type = 0;
            double _bottom_bound_load_time_portion  = 0;
            double _bottom_bound_relax_time_portion = 0;

            double _back_min_mechanical_param       = 0;
            double _back_mechanical_param           = 0;
            double _back_min_thermal_param          = 0;
            double _back_thermal_param              = 0;
            byte   _back_bound_time_function_type   = 0;
            double _back_bound_load_time_portion    = 0;
            double _back_bound_relax_time_portion   = 0;
            
            double _right_min_mechanical_param      = 0;
            double _right_mechanical_param          = 0;
            double _right_min_thermal_param         = 0;
            double _right_thermal_param             = 0;
            byte   _right_bound_time_function_type  = 0;
            double _right_bound_load_time_portion   = 0;
            double _right_bound_relax_time_portion  = 0;

            //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
            // "mech" parameter is equal to constant rate of stress or strain change,
            // "heat" parameter is equal to constant temperature or constant influx of specific surface heat energy.
            double _left_tank_min_coord_X = 0;
            double _left_tank_max_coord_X = 0;
            double _left_tank_min_coord_Y = 0;
            double _left_tank_max_coord_Y = 0;
            double _left_tank_min_coord_Z = 0;
            double _left_tank_max_coord_Z = 0;
            double _left_tank_min_mech_param  = 0;
            double _left_tank_mech_param      = 0;
            double _left_tank_min_heat_param  = 0;
            double _left_tank_heat_param      = 0;
            byte   _left_tank_time_function_type   = 0;
            double _left_tank_load_time_portion    = 0;
            double _left_tank_relax_time_portion   = 0;
                        
            double _front_tank_min_coord_X = 0;
            double _front_tank_max_coord_X = 0;
            double _front_tank_min_coord_Y = 0;
            double _front_tank_max_coord_Y = 0;
            double _front_tank_min_coord_Z = 0;
            double _front_tank_max_coord_Z = 0;
            double _front_tank_min_mech_param  = 0;
            double _front_tank_mech_param      = 0;
            double _front_tank_min_heat_param  = 0;
            double _front_tank_heat_param      = 0;
            byte   _front_tank_time_function_type  = 0;
            double _front_tank_load_time_portion   = 0;
            double _front_tank_relax_time_portion  = 0;
            
            double _top_tank_min_coord_X = 0;
            double _top_tank_max_coord_X = 0;
            double _top_tank_min_coord_Y = 0;
            double _top_tank_max_coord_Y = 0;
            double _top_tank_min_coord_Z = 0;
            double _top_tank_max_coord_Z = 0;
            double _top_tank_min_mech_param  = 0;
            double _top_tank_mech_param      = 0;
            double _top_tank_min_heat_param  = 0;
            double _top_tank_heat_param      = 0;
            byte   _top_tank_time_function_type    = 0;
            double _top_tank_load_time_portion     = 0;
            double _top_tank_relax_time_portion    = 0;
            
            double _bottom_tank_min_coord_X = 0;
            double _bottom_tank_max_coord_X = 0;
            double _bottom_tank_min_coord_Y = 0;
            double _bottom_tank_max_coord_Y = 0;
            double _bottom_tank_min_coord_Z = 0;
            double _bottom_tank_max_coord_Z = 0;
            double _bottom_tank_min_mech_param  = 0;
            double _bottom_tank_mech_param      = 0;
            double _bottom_tank_min_heat_param  = 0;
            double _bottom_tank_heat_param      = 0;
            byte   _bottom_tank_time_function_type = 0;
            double _bottom_tank_load_time_portion  = 0;
            double _bottom_tank_relax_time_portion = 0;
            
            double _back_tank_min_coord_X = 0;
            double _back_tank_max_coord_X = 0;
            double _back_tank_min_coord_Y = 0;
            double _back_tank_max_coord_Y = 0;
            double _back_tank_min_coord_Z = 0;
            double _back_tank_max_coord_Z = 0;
            double _back_tank_min_mech_param  = 0;
            double _back_tank_mech_param      = 0;
            double _back_tank_min_heat_param  = 0;
            double _back_tank_heat_param      = 0;
            byte   _back_tank_time_function_type   = 0;
            double _back_tank_load_time_portion    = 0;
            double _back_tank_relax_time_portion   = 0;
            
            double _right_tank_min_coord_X = 0;
            double _right_tank_max_coord_X = 0;
            double _right_tank_min_coord_Y = 0;
            double _right_tank_max_coord_Y = 0;
            double _right_tank_min_coord_Z = 0;
            double _right_tank_max_coord_Z = 0;
            double _right_tank_min_mech_param  = 0;
            double _right_tank_mech_param      = 0;
            double _right_tank_min_heat_param  = 0;
            double _right_tank_heat_param      = 0;
            byte   _right_tank_time_function_type  = 0;
            double _right_tank_load_time_portion   = 0;
            double _right_tank_relax_time_portion  = 0;

            // Sizes of specimen simulated
          //  double spec_size_X = (3 + 1/Math.sqrt(3.0) + _cell_number_I*Math.sqrt(3.0));
          //  double spec_size_Y = (4 + _cell_number_J*2);
          //  double spec_size_Z = (3 + 2*_cell_number_K*Math.sqrt(2.0/3.0));
            
            double real_specimen_size_X = specimen_size_X;
            double real_specimen_size_Y = specimen_size_Y;
            double real_specimen_size_Z = specimen_size_Z;
            
            // Variables for minimal and maximal values of cell coordinates
            double[] min_coordinates = new double[3];
            double[] max_coordinates = new double[3];
            
            min_coordinates[0] = min_coordinates[1] = min_coordinates[2] = Integer.MAX_VALUE;
            max_coordinates[0] = max_coordinates[1] = max_coordinates[2] = Integer.MIN_VALUE;
            
            // Coordinates of current cell
            double[] cell_coordinates = new double[3];
            
            // Search of minimal and maximal values of cell coordinates
            for(int cell_index_I = 1; cell_index_I < _cell_number_I+1; cell_index_I++)
            for(int cell_index_J = 1; cell_index_J < _cell_number_J+1; cell_index_J++)
            for(int cell_index_K = 1; cell_index_K < _cell_number_K+1; cell_index_K++)
            {
                //Cell coordinates
                cell_coordinates = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
                
                if(cell_coordinates[0] < min_coordinates[0]) min_coordinates[0] = cell_coordinates[0];
                if(cell_coordinates[1] < min_coordinates[1]) min_coordinates[1] = cell_coordinates[1];
                if(cell_coordinates[2] < min_coordinates[2]) min_coordinates[2] = cell_coordinates[2];
                
                if(cell_coordinates[0] > max_coordinates[0]) max_coordinates[0] = cell_coordinates[0];
                if(cell_coordinates[1] > max_coordinates[1]) max_coordinates[1] = cell_coordinates[1];
                if(cell_coordinates[2] > max_coordinates[2]) max_coordinates[2] = cell_coordinates[2];
            }
            
            // minimal and maximal values of cell coordinates
            min_coord_X = min_coordinates[0];
            max_coord_X = max_coordinates[0];
            
            min_coord_Y = min_coordinates[1];
            max_coord_Y = max_coordinates[1];
            
            min_coord_Z = min_coordinates[2];
            max_coord_Z = max_coordinates[2];
            
            specimen_size_X = max_coordinates[0] - min_coordinates[0];
            specimen_size_Y = max_coordinates[1] - min_coordinates[1];
            specimen_size_Z = max_coordinates[2] - min_coordinates[2];
            
            System.out.println("specimen_size_X = "+specimen_size_X);
            System.out.println("specimen_size_Y = "+specimen_size_Y);
            System.out.println("specimen_size_Z = "+specimen_size_Z);
            System.out.println();
            
            try
            {
                // Reading ow information about boundary facets
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary facets:    "+bound_params_file);

                //    Parameters characterizing mechanical and thermal loading of each facet:
                // "mechanical" parameter is equal to constant rate of stress or strain change,
                // "thermal"    parameter is equal to constant temperature or
                //              constant influx of specific surface heat energy.
                _left_min_mechanical_param       = (new Double(table.getCell(0, 0))).doubleValue();
                _left_mechanical_param           = (new Double(table.getCell(0, 1))).doubleValue();
                _left_min_thermal_param          = (new Double(table.getCell(0, 2))).doubleValue();
                _left_thermal_param              = (new Double(table.getCell(0, 3))).doubleValue();
                _left_bound_time_function_type   = (new Byte  (table.getCell(0, 4))).byteValue();
                _left_bound_load_time_portion    = (new Double(table.getCell(0, 5))).doubleValue();
                _left_bound_relax_time_portion   = (new Double(table.getCell(0, 6))).doubleValue();

                _front_min_mechanical_param      = (new Double(table.getCell(1, 0))).doubleValue();
                _front_mechanical_param          = (new Double(table.getCell(1, 1))).doubleValue();
                _front_min_thermal_param         = (new Double(table.getCell(1, 2))).doubleValue();
                _front_thermal_param             = (new Double(table.getCell(1, 3))).doubleValue();
                _front_bound_time_function_type  = (new Byte  (table.getCell(1, 4))).byteValue();
                _front_bound_load_time_portion   = (new Double(table.getCell(1, 5))).doubleValue();
                _front_bound_relax_time_portion  = (new Double(table.getCell(1, 6))).doubleValue();

                _top_min_mechanical_param        = (new Double(table.getCell(2, 0))).doubleValue();
                _top_mechanical_param            = (new Double(table.getCell(2, 1))).doubleValue();
                _top_min_thermal_param           = (new Double(table.getCell(2, 2))).doubleValue();
                _top_thermal_param               = (new Double(table.getCell(2, 3))).doubleValue();
                _top_bound_time_function_type    = (new Byte  (table.getCell(2, 4))).byteValue();
                _top_bound_load_time_portion     = (new Double(table.getCell(2, 5))).doubleValue();
                _top_bound_relax_time_portion    = (new Double(table.getCell(2, 6))).doubleValue();

                _bottom_min_mechanical_param     = (new Double(table.getCell(3, 0))).doubleValue();
                _bottom_mechanical_param         = (new Double(table.getCell(3, 1))).doubleValue();
                _bottom_min_thermal_param        = (new Double(table.getCell(3, 2))).doubleValue();
                _bottom_thermal_param            = (new Double(table.getCell(3, 3))).doubleValue();
                _bottom_bound_time_function_type = (new Byte  (table.getCell(3, 4))).byteValue();
                _bottom_bound_load_time_portion  = (new Double(table.getCell(3, 5))).doubleValue();
                _bottom_bound_relax_time_portion = (new Double(table.getCell(3, 6))).doubleValue();

                _back_min_mechanical_param       = (new Double(table.getCell(4, 0))).doubleValue();
                _back_mechanical_param           = (new Double(table.getCell(4, 1))).doubleValue();
                _back_min_thermal_param          = (new Double(table.getCell(4, 2))).doubleValue();
                _back_thermal_param              = (new Double(table.getCell(4, 3))).doubleValue();
                _back_bound_time_function_type   = (new Byte  (table.getCell(4, 4))).byteValue();
                _back_bound_load_time_portion    = (new Double(table.getCell(4, 5))).doubleValue();
                _back_bound_relax_time_portion   = (new Double(table.getCell(4, 6))).doubleValue();

                _right_min_mechanical_param      = (new Double(table.getCell(5, 0))).doubleValue();
                _right_mechanical_param          = (new Double(table.getCell(5, 1))).doubleValue();
                _right_min_thermal_param         = (new Double(table.getCell(5, 2))).doubleValue(); 
                _right_thermal_param             = (new Double(table.getCell(5, 3))).doubleValue();
                _right_bound_time_function_type  = (new Byte  (table.getCell(5, 4))).byteValue();
                _right_bound_load_time_portion   = (new Double(table.getCell(5, 5))).doubleValue();
                _right_bound_relax_time_portion  = (new Double(table.getCell(5, 6))).doubleValue();
                
                // Reading of information about boundary tanks             
                reader = new TextTableFileReader(tanks_file);
                table = reader.convertToTable();
                
                completed_steps.setText(completed_steps.getText()+"\nName of read file with parameters of boundary tanks:    "+tanks_file);
                System.out.println("Name of read file with parameters of boundary tanks:    "+tanks_file);
                
                //    Parameters characterizing mechanical and thermal loading and geometry of each tank:
                // "mech" parameter is equal to constant rate of stress or strain change,
                // "heat" parameter is equal to constant temperature or
                //        constant influx of specific surface heat energy.
                
                _left_tank_min_coord_X = (new Double(table.getCell(0, 1))).doubleValue();
                _left_tank_max_coord_X = (new Double(table.getCell(0, 2))).doubleValue();
                _left_tank_min_coord_Y = (new Double(table.getCell(0, 3))).doubleValue();
                _left_tank_max_coord_Y = (new Double(table.getCell(0, 4))).doubleValue();
                _left_tank_min_coord_Z = (new Double(table.getCell(0, 5))).doubleValue();
                _left_tank_max_coord_Z = (new Double(table.getCell(0, 6))).doubleValue();
                _left_tank_min_mech_param  = (new Double(table.getCell(0, 9))).doubleValue();
                _left_tank_mech_param      = (new Double(table.getCell(0,10))).doubleValue();
                _left_tank_min_heat_param  = (new Double(table.getCell(0,11))).doubleValue();
                _left_tank_heat_param      = (new Double(table.getCell(0,12))).doubleValue();
                _left_tank_time_function_type = (new Byte(table.getCell(0,13))).byteValue();
                _left_tank_load_time_portion  = (new Double(table.getCell(0,14))).doubleValue();
                _left_tank_relax_time_portion = (new Double(table.getCell(0,15))).doubleValue();
                   
                _front_tank_min_coord_X = (new Double(table.getCell(1, 1))).doubleValue();
                _front_tank_max_coord_X = (new Double(table.getCell(1, 2))).doubleValue();
                _front_tank_min_coord_Y = (new Double(table.getCell(1, 3))).doubleValue();
                _front_tank_max_coord_Y = (new Double(table.getCell(1, 4))).doubleValue();
                _front_tank_min_coord_Z = (new Double(table.getCell(1, 5))).doubleValue();
                _front_tank_max_coord_Z = (new Double(table.getCell(1, 6))).doubleValue();
                _front_tank_min_mech_param  = (new Double(table.getCell(1, 9))).doubleValue();
                _front_tank_mech_param      = (new Double(table.getCell(1,10))).doubleValue();
                _front_tank_min_heat_param  = (new Double(table.getCell(1,11))).doubleValue();
                _front_tank_heat_param      = (new Double(table.getCell(1,12))).doubleValue();
                _front_tank_time_function_type = (new Byte(table.getCell(1,13))).byteValue();
                _front_tank_load_time_portion  = (new Double(table.getCell(1,14))).doubleValue();
                _front_tank_relax_time_portion = (new Double(table.getCell(1,15))).doubleValue();
                   
                _top_tank_min_coord_X = (new Double(table.getCell(2, 1))).doubleValue();
                _top_tank_max_coord_X = (new Double(table.getCell(2, 2))).doubleValue();
                _top_tank_min_coord_Y = (new Double(table.getCell(2, 3))).doubleValue();
                _top_tank_max_coord_Y = (new Double(table.getCell(2, 4))).doubleValue();
                _top_tank_min_coord_Z = (new Double(table.getCell(2, 5))).doubleValue();
                _top_tank_max_coord_Z = (new Double(table.getCell(2, 6))).doubleValue();
                _top_tank_min_mech_param  = (new Double(table.getCell(2, 9))).doubleValue();
                _top_tank_mech_param      = (new Double(table.getCell(2,10))).doubleValue();
                _top_tank_min_heat_param  = (new Double(table.getCell(2,11))).doubleValue();
                _top_tank_heat_param      = (new Double(table.getCell(2,12))).doubleValue();
                _top_tank_time_function_type = (new Byte(table.getCell(2,13))).byteValue();
                _top_tank_load_time_portion  = (new Double(table.getCell(2,14))).doubleValue();
                _top_tank_relax_time_portion = (new Double(table.getCell(2,15))).doubleValue();
                         
                _bottom_tank_min_coord_X = (new Double(table.getCell(3, 1))).doubleValue();
                _bottom_tank_max_coord_X = (new Double(table.getCell(3, 2))).doubleValue();
                _bottom_tank_min_coord_Y = (new Double(table.getCell(3, 3))).doubleValue();
                _bottom_tank_max_coord_Y = (new Double(table.getCell(3, 4))).doubleValue();
                _bottom_tank_min_coord_Z = (new Double(table.getCell(3, 5))).doubleValue();
                _bottom_tank_max_coord_Z = (new Double(table.getCell(3, 6))).doubleValue();
                _bottom_tank_min_mech_param  = (new Double(table.getCell(3, 9))).doubleValue();
                _bottom_tank_mech_param      = (new Double(table.getCell(3,10))).doubleValue();
                _bottom_tank_min_heat_param  = (new Double(table.getCell(3,11))).doubleValue();
                _bottom_tank_heat_param      = (new Double(table.getCell(3,12))).doubleValue();
                _bottom_tank_time_function_type = (new Byte(table.getCell(3,13))).byteValue();
                _bottom_tank_load_time_portion  = (new Double(table.getCell(3,14))).doubleValue();
                _bottom_tank_relax_time_portion = (new Double(table.getCell(3,15))).doubleValue();
                
                _back_tank_min_coord_X = (new Double(table.getCell(4, 1))).doubleValue();
                _back_tank_max_coord_X = (new Double(table.getCell(4, 2))).doubleValue();
                _back_tank_min_coord_Y = (new Double(table.getCell(4, 3))).doubleValue();
                _back_tank_max_coord_Y = (new Double(table.getCell(4, 4))).doubleValue();
                _back_tank_min_coord_Z = (new Double(table.getCell(4, 5))).doubleValue();
                _back_tank_max_coord_Z = (new Double(table.getCell(4, 6))).doubleValue();
                _back_tank_min_mech_param  = (new Double(table.getCell(4, 9))).doubleValue();
                _back_tank_mech_param      = (new Double(table.getCell(4,10))).doubleValue();
                _back_tank_min_heat_param  = (new Double(table.getCell(4,11))).doubleValue();
                _back_tank_heat_param      = (new Double(table.getCell(4,12))).doubleValue();
                _back_tank_time_function_type = (new Byte(table.getCell(4,13))).byteValue();
                _back_tank_load_time_portion  = (new Double(table.getCell(4,14))).doubleValue();
                _back_tank_relax_time_portion = (new Double(table.getCell(4,15))).doubleValue();
                
                _right_tank_min_coord_X = (new Double(table.getCell(5, 1))).doubleValue();
                _right_tank_max_coord_X = (new Double(table.getCell(5, 2))).doubleValue();
                _right_tank_min_coord_Y = (new Double(table.getCell(5, 3))).doubleValue();
                _right_tank_max_coord_Y = (new Double(table.getCell(5, 4))).doubleValue();
                _right_tank_min_coord_Z = (new Double(table.getCell(5, 5))).doubleValue();
                _right_tank_max_coord_Z = (new Double(table.getCell(5, 6))).doubleValue();
                _right_tank_min_mech_param  = (new Double(table.getCell(5, 9))).doubleValue();
                _right_tank_mech_param      = (new Double(table.getCell(5,10))).doubleValue();
                _right_tank_min_heat_param  = (new Double(table.getCell(5,11))).doubleValue();
                _right_tank_heat_param      = (new Double(table.getCell(5,12))).doubleValue();
                _right_tank_time_function_type = (new Byte(table.getCell(5,13))).byteValue();
                _right_tank_load_time_portion  = (new Double(table.getCell(5,14))).doubleValue();
                _right_tank_relax_time_portion = (new Double(table.getCell(5,15))).doubleValue();
                
                System.out.println("_top_tank_mech_param:    "+_top_tank_mech_param);
                completed_steps.setText(completed_steps.getText()+"\n_top_tank_mech_param:    "+_top_tank_mech_param);
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
        //    double[] boundary_cell_coordinates;
            
            // Index of current cell
            int cell_index;
            
            // Number of outer boundary cells
            int bound_cell_number = (_cell_number_I+2)*(_cell_number_J+2)*(_cell_number_K+2)-
                                     _cell_number_I*_cell_number_J*_cell_number_K;
            
            // Number of cells in tanks
            int tank_cell_number = 0;
            
            // Numbers of cells in left, front, top, bottom, back and right tanks
            int left_tank_cell_number = 0;
            int front_tank_cell_number = 0;
            int top_tank_cell_number = 0;
            int bottom_tank_cell_number = 0;
            int back_tank_cell_number = 0;
            int right_tank_cell_number = 0;

            //  Number of cells at outer facets out of tanks
            int facet_cell_number = 0;
            
            //  Numbers of cells at left, front, top, bottom, back and right outer facets out of tanks
            int left_facet_cell_number = 0;
            int front_facet_cell_number = 0;
            int top_facet_cell_number = 0;
            int bottom_facet_cell_number = 0;
            int back_facet_cell_number = 0;
            int right_facet_cell_number = 0;
            
            // Parameter for calculation of thermal and mechanical loading of facets
            double bound_param = 0;

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                bw.write("#   This file contains parameters of each outer boundary cell: \n");
                bw.write("# 1. index; 2. minimal mechanical parameter (strain or stress); 3. maximal mechanical parameter; \n");
                bw.write("# 4. minimal thermal parameter (temperature or thermal energy influx); 5. maximal thermal parameter; \n");
                bw.write("# 6. time function type (0 - uniform loading, 1 - cycle loading, 2 - loading according to periodic function); \n");
                bw.write("# 7. loading time portion; 8. relaxation time portion. \n");
                
                // Writing of number of outer boundary cells
                bw.write("# Number of outer boundary cells: "+bound_cell_number+"\n");
                bw.flush();
                
                bw1.write("#   This file contains information about outer boundary cell: \n");
                bw1.write("# in 1st string - number of  outer boundary cells, \n");
                bw1.write("# in each further string - parameters of each outer boundary cell: \n");
                bw1.write("# 1-3. 3 coordinates; 4. minimal mechanical parameter (strain or stress); 5. maximal mechanical parameter;\n");
                bw1.write("# 6. minimal thermal parameter (temperature or thermal energy influx); 7. maximal thermal parameter; \n");
                bw1.write("# 8. time function type (0 - uniform loading, 1 - cycle loading, 2 - loading according to periodic function); \n");
                bw1.write("# 9. loading time portion; 10. relaxation time portion. \n");
                
                // Writing of number of outer boundary cells
                bw1.write("# Number of outer boundary cells: "+bound_cell_number+"\n");
                bw1.flush();

                boolean write_string;

                bound_cell_number = 0;
                
                // Initial minimal and maximal mechanical parameters of cells at boundary facets
                double old_left_min_mechanical_param     = _left_min_mechanical_param;
                double old_front_min_mechanical_param    = _front_min_mechanical_param;
                double old_top_min_mechanical_param      = _top_min_mechanical_param;
                double old_bottom_min_mechanical_param   = _bottom_min_mechanical_param;
                double old_back_min_mechanical_param     = _back_min_mechanical_param;
                double old_right_min_mechanical_param    = _right_min_mechanical_param;
                
                double old_left_mechanical_param     = _left_mechanical_param;
                double old_front_mechanical_param    = _front_mechanical_param;
                double old_top_mechanical_param      = _top_mechanical_param;
                double old_bottom_mechanical_param   = _bottom_mechanical_param;
                double old_back_mechanical_param     = _back_mechanical_param;
                double old_right_mechanical_param    = _right_mechanical_param;
                
                // Initial minimal and maximal mechanical parameters of cells in tanks
                double old_left_tank_min_mech_param      = _left_tank_min_mech_param;
                double old_front_tank_min_mech_param     = _front_tank_min_mech_param;
                double old_top_tank_min_mech_param       = _top_tank_min_mech_param;
                double old_bottom_tank_min_mech_param    = _bottom_tank_min_mech_param;
                double old_back_tank_min_mech_param      = _back_tank_min_mech_param;
                double old_right_tank_min_mech_param     = _right_tank_min_mech_param;
                
                double old_left_tank_mech_param      = _left_tank_mech_param;
                double old_front_tank_mech_param     = _front_tank_mech_param;
                double old_top_tank_mech_param       = _top_tank_mech_param;
                double old_bottom_tank_mech_param    = _bottom_tank_mech_param;
                double old_back_tank_mech_param      = _back_tank_mech_param;
                double old_right_tank_mech_param     = _right_tank_mech_param;
                
                // Initial minimal and maximal thermal parameters of cells at boundary facets
                double old_left_min_thermal_param        = _left_min_thermal_param;
                double old_front_min_thermal_param       = _front_min_thermal_param;
                double old_top_min_thermal_param         = _top_min_thermal_param;
                double old_bottom_min_thermal_param      = _bottom_min_thermal_param;
                double old_back_min_thermal_param        = _back_min_thermal_param;
                double old_right_min_thermal_param       = _right_min_thermal_param;
                
                double old_left_thermal_param        = _left_thermal_param;
                double old_front_thermal_param       = _front_thermal_param;
                double old_top_thermal_param         = _top_thermal_param;
                double old_bottom_thermal_param      = _bottom_thermal_param;
                double old_back_thermal_param        = _back_thermal_param;
                double old_right_thermal_param       = _right_thermal_param;
                
                // Initial minimal and maximal thermal parameters of cells in tanks
                double old_left_tank_min_heat_param   = _left_tank_min_heat_param;
                double old_front_tank_min_heat_param  = _front_tank_min_heat_param;
                double old_top_tank_min_heat_param    = _top_tank_min_heat_param;
                double old_bottom_tank_min_heat_param = _bottom_tank_min_heat_param;
                double old_back_tank_min_heat_param   = _back_tank_min_heat_param;
                double old_right_tank_min_heat_param  = _right_tank_min_heat_param;
                
                double old_left_tank_heat_param   = _left_tank_heat_param;
                double old_front_tank_heat_param  = _front_tank_heat_param;
                double old_top_tank_heat_param    = _top_tank_heat_param;
                double old_bottom_tank_heat_param = _bottom_tank_heat_param;
                double old_back_tank_heat_param   = _back_tank_heat_param;
                double old_right_tank_heat_param  = _right_tank_heat_param;
                
                //   The type of dependence of the mechanical and thermal parameters of 
                // the boundary cell on the cell coordinates
                bound_function_type = Common.COORDINATE_Y_VALUE;
                
                System.out.println("FileCreation: function_type = "+bound_function_type);
                completed_steps.setText(completed_steps.getText()+"\nFileCreation: function_type = "+bound_function_type);
            //    completed_steps.setText(completed_steps.getText()+"\n_left_thermal_param         = "+_left_thermal_param);

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {   
                    //Cell coordinates
                    cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);
                    
                    cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2) + cell_counter_J*(_cell_number_I+2) + cell_counter_I;
                    
                    // Parameter for calculation of thermal and mechanical loading of facets
                //    bound_param = boundaryParam(cell_coordinates[0] - cell_radius, 
                //                                cell_coordinates[1] - cell_radius, 
                //                                cell_coordinates[2] - cell_radius, bound_function_type);     
                    
                    bound_param = boundaryParam(cell_coordinates[0] - min_coordinates[0],
                                                cell_coordinates[1] - min_coordinates[1], 
                                                cell_coordinates[2] - min_coordinates[2], bound_function_type);
                    
                 //   if(_left_thermal_param   == 0 | _front_thermal_param == 0 | _top_thermal_param   == 0 |
                  //     _bottom_thermal_param == 0 | _back_thermal_param  == 0 | _right_thermal_param == 0)
                  //  if(cell_counter_I == 0 & cell_counter_J == 0 & cell_counter_K == 0)
                    if(bound_param*bound_param > 1)
                    {
                        System.out.println();
                        System.out.println("ERROR!!! bound_param = "+bound_param);
                        System.out.println("\ncell coordinates: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]);
                        System.out.println("\nspecimen sizes:   "+specimen_size_X+" "+specimen_size_Y+" "+specimen_size_Z);
                        System.out.println();
                        
                     //   completed_steps.setText(completed_steps.getText()+"\n\nERROR!!! bound_param = "+bound_param+
                      //          "\ncell coordinates = "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+"\n\n");
                        
                     //   bw1.write("\n# ERROR!!! bound_param = "+bound_param+"\n");
                    }
                    
               //     bound_param = 1;
                    
                    // Change of boundary mechanical parameters according to corresponding formula
                    _left_min_mechanical_param    = _left_min_mechanical_param*  bound_param;
                    _front_min_mechanical_param   = _front_min_mechanical_param* bound_param;
                    _top_min_mechanical_param     = _top_min_mechanical_param*   bound_param;
                    _bottom_min_mechanical_param  = _bottom_min_mechanical_param*bound_param;
                    _back_min_mechanical_param    = _back_min_mechanical_param*  bound_param;
                    _right_min_mechanical_param   = _right_min_mechanical_param* bound_param;
                    
                    _left_mechanical_param    = _left_mechanical_param*  bound_param;
                    _front_mechanical_param   = _front_mechanical_param* bound_param;
                    _top_mechanical_param     = _top_mechanical_param*   bound_param;
                    _bottom_mechanical_param  = _bottom_mechanical_param*bound_param;
                    _back_mechanical_param    = _back_mechanical_param*  bound_param;
                    _right_mechanical_param   = _right_mechanical_param* bound_param;
                /*    
                    _left_tank_min_mech_param     = _left_tank_min_mech_param*   bound_param;
                    _front_tank_min_mech_param    = _front_tank_min_mech_param*  bound_param;
                    _top_tank_min_mech_param      = _top_tank_min_mech_param*    bound_param;
                    _bottom_tank_min_mech_param   = _bottom_tank_min_mech_param* bound_param;
                    _back_tank_min_mech_param     = _back_tank_min_mech_param*   bound_param;
                    _right_tank_min_mech_param    = _right_tank_min_mech_param*  bound_param;
                    
                    _left_tank_mech_param     = _left_tank_mech_param*   bound_param;
                    _front_tank_mech_param    = _front_tank_mech_param*  bound_param;
                    _top_tank_mech_param      = _top_tank_mech_param*    bound_param;
                    _bottom_tank_mech_param   = _bottom_tank_mech_param* bound_param;
                    _back_tank_mech_param     = _back_tank_mech_param*   bound_param;
                    _right_tank_mech_param    = _right_tank_mech_param*  bound_param;
                */    
               //     bound_param = -1;
                    
                    // Change of boundary thermal parameters according to corresponding formula
                 /*
                    _left_thermal_param       = _left_thermal_param  -  (_left_thermal_param   - init_temperature)*0.5*(1 + bound_param);
                    _front_thermal_param      = _front_thermal_param -  (_front_thermal_param  - init_temperature)*0.5*(1 + bound_param);
                    _top_thermal_param        = _top_thermal_param -    (_top_thermal_param    - init_temperature)*0.5*(1 + bound_param);
                    _bottom_thermal_param     = _bottom_thermal_param - (_bottom_thermal_param - init_temperature)*0.5*(1 + bound_param);
                    _back_thermal_param       = _back_thermal_param -   (_back_thermal_param   - init_temperature)*0.5*(1 + bound_param);
                    _right_thermal_param      = _right_thermal_param -  (_right_thermal_param  - init_temperature)*0.5*(1 + bound_param);
                     
                    _left_tank_heat_param     = _left_tank_heat_param  -  (_left_tank_heat_param   - init_temperature)*0.5*(1 + bound_param);
                    _front_tank_heat_param    = _front_tank_heat_param  - (_front_tank_heat_param  - init_temperature)*0.5*(1 + bound_param);
                    _top_tank_heat_param      = _top_tank_heat_param  -   (_top_tank_heat_param    - init_temperature)*0.5*(1 + bound_param);
                    _bottom_tank_heat_param   = _bottom_tank_heat_param - (_bottom_tank_heat_param - init_temperature)*0.5*(1 + bound_param);
                    _back_tank_heat_param     = _back_tank_heat_param  -  (_back_tank_heat_param   - init_temperature)*0.5*(1 + bound_param);
                    _right_tank_heat_param    = _right_tank_heat_param  - (_right_tank_heat_param  - init_temperature)*0.5*(1 + bound_param);
                 
                  
                    _left_thermal_param       = init_temperature + (_left_thermal_param   - init_temperature)*bound_param;
                    _front_thermal_param      = init_temperature + (_front_thermal_param  - init_temperature)*bound_param;
                    _top_thermal_param        = init_temperature + (_top_thermal_param    - init_temperature)*bound_param;
                    _bottom_thermal_param     = init_temperature + (_bottom_thermal_param - init_temperature)*bound_param;
                    _back_thermal_param       = init_temperature + (_back_thermal_param   - init_temperature)*bound_param;
                    _right_thermal_param      = init_temperature + (_right_thermal_param  - init_temperature)*bound_param;
                    
                    _left_tank_heat_param     = init_temperature + (_left_tank_heat_param   - init_temperature)*bound_param;
                    _front_tank_heat_param    = init_temperature + (_front_tank_heat_param  - init_temperature)*bound_param;
                    _top_tank_heat_param      = init_temperature + (_top_tank_heat_param    - init_temperature)*bound_param;
                    _bottom_tank_heat_param   = init_temperature + (_bottom_tank_heat_param - init_temperature)*bound_param;
                    _back_tank_heat_param     = init_temperature + (_back_tank_heat_param   - init_temperature)*bound_param;
                    _right_tank_heat_param    = init_temperature + (_right_tank_heat_param  - init_temperature)*bound_param;
                 */
                    
                    write_string = true;

                    // Writing of parameters of tank containing current cell
                    if(write_string)
                    {
                        // If current cell is in left tank
                        if(write_string)
                        if((cell_coordinates[0]>=_left_tank_min_coord_X)&(cell_coordinates[0]<=_left_tank_max_coord_X))
                        if((cell_coordinates[1]>=_left_tank_min_coord_Y)&(cell_coordinates[1]<=_left_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_left_tank_min_coord_Z)&(cell_coordinates[2]<=_left_tank_max_coord_Z))
                        {
                            tank_cell_number++;
                            left_tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                            
                            // Writing of physical parameters of boundary cell on specimen left facet
                            bw.write(cell_index+" "+_left_tank_min_mech_param+" "+_left_tank_mech_param+" "+
                                                    _left_tank_min_heat_param+" "+_left_tank_heat_param);
                            bw.write(" "+_left_tank_time_function_type+" "+_left_tank_load_time_portion+" "+_left_tank_relax_time_portion);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                    _left_tank_min_mech_param+" "+_left_tank_mech_param+" "+
                                    _left_tank_min_heat_param+" "+_left_tank_heat_param);
                            bw1.write(" "+_left_tank_time_function_type+" "+_left_tank_load_time_portion+" "+_left_tank_relax_time_portion);
                            
                            if(tank_cell_number%1000 == 0)
                            {
                                completed_steps.setText(completed_steps.getText()+"\nTank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _left_tank_min_mech_param:    "+_left_tank_min_mech_param+"; _left_tank_mech_param:    "+_left_tank_mech_param+
                                                    "; _left_tank_min_heat_param:    "+_left_tank_min_heat_param+"; _left_tank_heat_param:    "+_left_tank_heat_param);
                                
                                System.out.println     ("Tank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _left_tank_min_mech_param:    "+_left_tank_min_mech_param+"; _left_tank_mech_param:    "+_left_tank_mech_param+
                                                    "; _left_tank_min_heat_param:    "+_left_tank_min_heat_param+"; _left_tank_heat_param:    "+_left_tank_heat_param);
                            }
                            
                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();                            
                        }

                        // If current cell is in front tank
                        if(write_string)
                        if((cell_coordinates[0]>=_front_tank_min_coord_X)&(cell_coordinates[0]<=_front_tank_max_coord_X))
                        if((cell_coordinates[1]>=_front_tank_min_coord_Y)&(cell_coordinates[1]<=_front_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_front_tank_min_coord_Z)&(cell_coordinates[2]<=_front_tank_max_coord_Z))
                        {
                            tank_cell_number++;
                            front_tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                            
                            // Writing of physical parameters of boundary cell on specimen front facet
                            bw.write (cell_index+" "+_front_tank_min_mech_param+" "+_front_tank_mech_param+" "+
                                                     _front_tank_min_heat_param+" "+_front_tank_heat_param+" ");
                            bw.write(" "+_front_tank_time_function_type+" "+_front_tank_load_time_portion+" "+_front_tank_relax_time_portion);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                     _front_tank_min_mech_param+" "+_front_tank_mech_param+" "+
                                                     _front_tank_min_heat_param+" "+_front_tank_heat_param+" ");
                            bw1.write(" "+_front_tank_time_function_type+" "+_front_tank_load_time_portion+" "+_front_tank_relax_time_portion);
                            
                            if(tank_cell_number%1000 == 0)
                            {
                                completed_steps.setText(completed_steps.getText()+"\nTank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _front_tank_min_mech_param:    "+_front_tank_min_mech_param+"; _front_tank_mech_param:    "+_front_tank_mech_param+
                                                    "; _front_tank_min_heat_param:    "+_front_tank_min_heat_param+"; _front_tank_heat_param:    "+_front_tank_heat_param);
                                
                                System.out.println     ("Tank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _front_tank_min_mech_param:    "+_front_tank_min_mech_param+"; _front_tank_mech_param:    "+_front_tank_mech_param+
                                                    "; _front_tank_min_heat_param:    "+_front_tank_min_heat_param+"; _front_tank_heat_param:    "+_front_tank_heat_param);
                            }
                            
                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();
                        }

                        // If current cell is in top tank
                        if(write_string)
                        if((cell_coordinates[0]>=_top_tank_min_coord_X)&(cell_coordinates[0]<=_top_tank_max_coord_X))
                        if((cell_coordinates[1]>=_top_tank_min_coord_Y)&(cell_coordinates[1]<=_top_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_top_tank_min_coord_Z)&(cell_coordinates[2]<=_top_tank_max_coord_Z))
                        {
                            tank_cell_number++;
                            top_tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                            
                            // Writing of physical parameters of boundary cell on specimen top facet
                            bw.write (cell_index+" "+_top_tank_min_mech_param+" "+_top_tank_mech_param+" "+
                                                     _top_tank_min_heat_param+" "+_top_tank_heat_param+" ");
                            bw.write(" "+_top_tank_time_function_type+" "+_top_tank_load_time_portion+" "+_top_tank_relax_time_portion);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                     _top_tank_min_mech_param+" "+_top_tank_mech_param+" "+
                                                     _top_tank_min_heat_param+" "+_top_tank_heat_param+" ");
                            bw1.write(" "+_top_tank_time_function_type+" "+_top_tank_load_time_portion+" "+_top_tank_relax_time_portion);
                            
                            if(tank_cell_number%1000 == 0)
                            {
                                completed_steps.setText(completed_steps.getText()+"\nTank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _top_tank_min_mech_param:    "+_top_tank_min_mech_param+"; _top_tank_mech_param:    "+_top_tank_mech_param+
                                                    "; _top_tank_min_heat_param:    "+_top_tank_min_heat_param+"; _top_tank_heat_param:    "+_top_tank_heat_param);
                                
                                System.out.println     ("Tank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _top_tank_min_mech_param:    "+_top_tank_min_mech_param+"; _top_tank_mech_param:    "+_top_tank_mech_param+
                                                    "; _top_tank_min_heat_param:    "+_top_tank_min_heat_param+"; _top_tank_heat_param:    "+_top_tank_heat_param);
                            }
                            
                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();
                        }

                        // If current cell is in bottom tank
                        if(write_string)
                        if((cell_coordinates[0]>=_bottom_tank_min_coord_X)&(cell_coordinates[0]<=_bottom_tank_max_coord_X))
                        if((cell_coordinates[1]>=_bottom_tank_min_coord_Y)&(cell_coordinates[1]<=_bottom_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_bottom_tank_min_coord_Z)&(cell_coordinates[2]<=_bottom_tank_max_coord_Z))
                        {
                            tank_cell_number++;
                            bottom_tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                            
                            // Writing of physical parameters of boundary cell on specimen bottom facet
                            bw.write (cell_index+" "+_bottom_tank_min_mech_param+" "+_bottom_tank_mech_param+" "+
                                                     _bottom_tank_min_heat_param+" "+_bottom_tank_heat_param+" ");
                            bw.write(" "+_bottom_tank_time_function_type+" "+_bottom_tank_load_time_portion+" "+_bottom_tank_relax_time_portion);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                     _bottom_tank_min_mech_param+" "+_bottom_tank_mech_param+" "+
                                                     _bottom_tank_min_heat_param+" "+_bottom_tank_heat_param+" ");
                            bw1.write(" "+_bottom_tank_time_function_type+" "+_bottom_tank_load_time_portion+" "+_bottom_tank_relax_time_portion);
                            
                            if(tank_cell_number%1000 == 0)
                            {
                                completed_steps.setText(completed_steps.getText()+"\nTank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _bottom_tank_min_mech_param:    "+_bottom_tank_min_mech_param+"; _bottom_tank_mech_param:    "+_bottom_tank_mech_param+
                                                    "; _bottom_tank_min_heat_param:    "+_bottom_tank_min_heat_param+"; _bottom_tank_heat_param:    "+_bottom_tank_heat_param);
                                
                                System.out.println     ("Tank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _bottom_tank_min_mech_param:    "+_bottom_tank_min_mech_param+"; _bottom_tank_mech_param:    "+_bottom_tank_mech_param+
                                                    "; _bottom_tank_min_heat_param:    "+_bottom_tank_min_heat_param+"; _bottom_tank_heat_param:    "+_bottom_tank_heat_param);
                            }
                            
                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();
                        }

                        // If current cell is in back tank
                        if(write_string)
                        if((cell_coordinates[0]>=_back_tank_min_coord_X)&(cell_coordinates[0]<=_back_tank_max_coord_X))
                        if((cell_coordinates[1]>=_back_tank_min_coord_Y)&(cell_coordinates[1]<=_back_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_back_tank_min_coord_Z)&(cell_coordinates[2]<=_back_tank_max_coord_Z))
                        {
                            tank_cell_number++;
                            back_tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                            
                            // Writing of physical parameters of boundary cell on specimen back facet
                            bw.write (cell_index+" "+_back_tank_min_mech_param+" "+_back_tank_mech_param+" "+
                                                     _back_tank_min_heat_param+" "+_back_tank_heat_param+" ");
                            bw.write(" "+_back_tank_time_function_type+" "+_back_tank_load_time_portion+" "+_back_tank_relax_time_portion);

                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                     _back_tank_min_mech_param+" "+_back_tank_mech_param+" "+
                                                     _back_tank_min_heat_param+" "+_back_tank_heat_param+" ");
                            bw1.write(" "+_back_tank_time_function_type+" "+_back_tank_load_time_portion+" "+_back_tank_relax_time_portion);
                            
                            if(tank_cell_number%1000 == 0)
                            {
                                completed_steps.setText(completed_steps.getText()+"\nTank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _back_tank_min_mech_param:    "+_back_tank_min_mech_param+"; _back_tank_mech_param:    "+_back_tank_mech_param+
                                                    "; _back_tank_min_heat_param:    "+_back_tank_min_heat_param+"; _back_tank_heat_param:    "+_back_tank_heat_param);
                                
                                System.out.println     ("Tank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _back_tank_min_mech_param:    "+_back_tank_min_mech_param+"; _back_tank_mech_param:    "+_back_tank_mech_param+
                                                    "; _back_tank_min_heat_param:    "+_back_tank_min_heat_param+"; _back_tank_heat_param:    "+_back_tank_heat_param);
                            }
                            
                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();
                        }
                        
                        // If current cell is in right tank
                        if(write_string)
                        if((cell_coordinates[0]>=_right_tank_min_coord_X)&(cell_coordinates[0]<=_right_tank_max_coord_X))
                        if((cell_coordinates[1]>=_right_tank_min_coord_Y)&(cell_coordinates[1]<=_right_tank_max_coord_Y))
                        if((cell_coordinates[2]>=_right_tank_min_coord_Z)&(cell_coordinates[2]<=_right_tank_max_coord_Z))
                        {
                            tank_cell_number++;
                            right_tank_cell_number++;
                            bound_cell_number++;
                            write_string = false;
                            
                            // Writing of physical parameters of boundary cell on specimen right facet
                            bw.write (cell_index+" "+_right_tank_min_mech_param+" "+_right_tank_mech_param+" "+
                                                     _right_tank_min_heat_param+" "+_right_tank_heat_param+" ");
                            bw.write(" "+_right_tank_time_function_type+" "+_right_tank_load_time_portion+" "+_right_tank_relax_time_portion);
                            
                            bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                                     _right_tank_min_mech_param+" "+_right_tank_mech_param+" "+
                                                     _right_tank_min_heat_param+" "+_right_tank_heat_param+" ");
                            bw1.write(" "+_right_tank_time_function_type+" "+_right_tank_load_time_portion+" "+_right_tank_relax_time_portion);
                            
                            if(tank_cell_number%1000 == 0)
                            {
                                completed_steps.setText(completed_steps.getText()+"\nTank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _right_tank_min_mech_param:    "+_right_tank_min_mech_param+"; _right_tank_mech_param:    "+_right_tank_mech_param+
                                                    "; _right_tank_min_heat_param:    "+_right_tank_min_heat_param+"; _right_tank_heat_param:    "+_right_tank_heat_param);
                                
                                System.out.println     ("Tank cell: "+cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+
                                                    "; _right_tank_min_mech_param:    "+_right_tank_min_mech_param+"; _right_tank_mech_param:    "+_right_tank_mech_param+
                                                    "; _right_tank_min_heat_param:    "+_right_tank_min_heat_param+"; _right_tank_heat_param:    "+_right_tank_heat_param);
                            }
                                                        
                            bw.newLine();
                            bw.flush();
                            bw1.newLine();
                            bw1.flush();
                        }
                    }
                    
                    // Conditions on left facet
                    if(cell_counter_I==0)if(write_string)
                    {   
                        // Writing of physical parameters of boundary cell on specimen left facet
                        bw.write(cell_index+" "+_left_min_mechanical_param+" "+_left_mechanical_param+" "+_left_min_thermal_param+" "+_left_thermal_param+" "+
                                _left_bound_time_function_type+" "+_left_bound_load_time_portion+" "+_left_bound_relax_time_portion);
                        
                        bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                  _left_min_mechanical_param+" "+_left_mechanical_param+" "+_left_min_thermal_param+" "+_left_thermal_param+" "+
                                  _left_bound_time_function_type+" "+_left_bound_load_time_portion+" "+_left_bound_relax_time_portion);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        left_facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_I==_cell_number_I+1)if(write_string)
                    {
                        // Writing of physical parameters of boundary cell on specimen right facet
                        bw.write(cell_index+" "+_right_min_mechanical_param+" "+_right_mechanical_param+" "+_right_min_thermal_param+" "+_right_thermal_param+" "+
                                _right_bound_time_function_type+" "+_right_bound_load_time_portion+" "+_right_bound_relax_time_portion);
                        
                        bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                  _right_min_mechanical_param+" "+_right_mechanical_param+" "+_right_min_thermal_param+" "+_right_thermal_param+" "+
                                  _right_bound_time_function_type+" "+_right_bound_load_time_portion+" "+_right_bound_relax_time_portion);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        right_facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_J==0)if(write_string)
                    {
                        // Writing of physical parameters of boundary cell on specimen front facet
                        bw.write(cell_index+" "+_front_min_mechanical_param+" "+_front_mechanical_param+" "+_front_min_thermal_param+" "+_front_thermal_param+" "+
                                _front_bound_time_function_type+" "+_front_bound_load_time_portion+" "+_front_bound_relax_time_portion);
                        
                        bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                  _front_min_mechanical_param+" "+_front_mechanical_param+" "+_front_min_thermal_param+" "+_front_thermal_param+" "+
                                  _front_bound_time_function_type+" "+_front_bound_load_time_portion+" "+_front_bound_relax_time_portion);
                        
                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        front_facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_J==_cell_number_J+1)if(write_string)
                    {
                        // Writing of physical parameters of boundary cell on specimen back facet
                        bw.write(cell_index+" "+_back_min_mechanical_param+" "+_back_mechanical_param+" "+_back_min_thermal_param+" "+_back_thermal_param+" "+
                                _back_bound_time_function_type+" "+_back_bound_load_time_portion+" "+_back_bound_relax_time_portion);
                        
                        bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                  _back_min_mechanical_param+" "+_back_mechanical_param+" "+_back_min_thermal_param+" "+_back_thermal_param+" "+
                                  _back_bound_time_function_type+" "+_back_bound_load_time_portion+" "+_back_bound_relax_time_portion);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        back_facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_K==0)if(write_string)
                    {
                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(cell_index+" "+_top_min_mechanical_param+" "+_top_mechanical_param+" "+_top_min_thermal_param+" "+_top_thermal_param+" "+
                                _top_bound_time_function_type+" "+_top_bound_load_time_portion+" "+_top_bound_relax_time_portion);
                        
                        bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                  _top_min_mechanical_param+" "+_top_mechanical_param+" "+_top_min_thermal_param+" "+_top_thermal_param+" "+
                                  _top_bound_time_function_type+" "+_top_bound_load_time_portion+" "+_top_bound_relax_time_portion);
                        
                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();

                        facet_cell_number++;
                        top_facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }

                    if(cell_counter_K==_cell_number_K+1)if(write_string)
                    {
                        // Writing of physical parameters of boundary cell on specimen bottom facet
                        bw.write(cell_index+" "+_bottom_min_mechanical_param+" "+_bottom_mechanical_param+" "+_bottom_min_thermal_param+" "+_bottom_thermal_param+" "+
                                _bottom_bound_time_function_type+" "+_bottom_bound_load_time_portion+" "+_bottom_bound_relax_time_portion);
                        
                        bw1.write(cell_coordinates[0]+" "+cell_coordinates[1]+" "+cell_coordinates[2]+" "+
                                  _bottom_min_mechanical_param+" "+_bottom_mechanical_param+" "+_bottom_min_thermal_param+" "+_bottom_thermal_param+" "+
                                  _bottom_bound_time_function_type+" "+_bottom_bound_load_time_portion+" "+_bottom_bound_relax_time_portion);
                        
                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                        
                        facet_cell_number++;
                        bottom_facet_cell_number++;
                        bound_cell_number++;
                        write_string = false;
                    }
                    
                    // Comeback to initial boundary mechanical parameters
                    _left_min_mechanical_param    = old_left_min_mechanical_param;
                    _front_min_mechanical_param   = old_front_min_mechanical_param;
                    _top_min_mechanical_param     = old_top_min_mechanical_param;
                    _bottom_min_mechanical_param  = old_bottom_min_mechanical_param;
                    _back_min_mechanical_param    = old_back_min_mechanical_param;
                    _right_min_mechanical_param   = old_right_min_mechanical_param;
                    
                    _left_mechanical_param        = old_left_mechanical_param;
                    _front_mechanical_param       = old_front_mechanical_param;
                    _top_mechanical_param         = old_top_mechanical_param;
                    _bottom_mechanical_param      = old_bottom_mechanical_param;
                    _back_mechanical_param        = old_back_mechanical_param;
                    _right_mechanical_param       = old_right_mechanical_param;
                    
                    _left_tank_min_mech_param     = old_left_tank_min_mech_param;
                    _front_tank_min_mech_param    = old_front_tank_min_mech_param;
                    _top_tank_min_mech_param      = old_top_tank_min_mech_param;
                    _bottom_tank_min_mech_param   = old_bottom_tank_min_mech_param;
                    _back_tank_min_mech_param     = old_back_tank_min_mech_param;
                    _right_tank_min_mech_param    = old_right_tank_min_mech_param;
                    
                    _left_tank_mech_param         = old_left_tank_mech_param;
                    _front_tank_mech_param        = old_front_tank_mech_param;
                    _top_tank_mech_param          = old_top_tank_mech_param;
                    _bottom_tank_mech_param       = old_bottom_tank_mech_param;
                    _back_tank_mech_param         = old_back_tank_mech_param;
                    _right_tank_mech_param        = old_right_tank_mech_param;
                    
                    // Comeback to initial boundary thermal parameters
                    _left_min_thermal_param       = old_left_min_thermal_param;
                    _front_min_thermal_param      = old_front_min_thermal_param;
                    _top_min_thermal_param        = old_top_min_thermal_param;
                    _bottom_min_thermal_param     = old_bottom_min_thermal_param;
                    _back_min_thermal_param       = old_back_min_thermal_param;
                    _right_min_thermal_param      = old_right_min_thermal_param;
                    
                    _left_thermal_param       = old_left_thermal_param;
                    _front_thermal_param      = old_front_thermal_param;
                    _top_thermal_param        = old_top_thermal_param;
                    _bottom_thermal_param     = old_bottom_thermal_param;
                    _back_thermal_param       = old_back_thermal_param;
                    _right_thermal_param      = old_right_thermal_param;
                    
                    _left_tank_min_heat_param     = old_left_tank_min_heat_param;
                    _front_tank_min_heat_param    = old_front_tank_min_heat_param;
                    _top_tank_min_heat_param      = old_top_tank_min_heat_param;
                    _bottom_tank_min_heat_param   = old_bottom_tank_min_heat_param;
                    _back_tank_min_heat_param     = old_back_tank_min_heat_param;
                    _right_tank_min_heat_param    = old_right_tank_min_heat_param;
                    
                    _left_tank_heat_param     = old_left_tank_heat_param;
                    _front_tank_heat_param    = old_front_tank_heat_param;
                    _top_tank_heat_param      = old_top_tank_heat_param;
                    _bottom_tank_heat_param   = old_bottom_tank_heat_param;
                    _back_tank_heat_param     = old_back_tank_heat_param;
                    _right_tank_heat_param    = old_right_tank_heat_param;
                }
                
                bw.write("# \n");
                bw.write("# Total number of boundary cells ("+bound_cell_number+") = outer facet cells ("+facet_cell_number+") + tank cells ("+tank_cell_number+").\n");
                bw.write("# \n");
                bw.write("#   Total number of outer facet cells ("+facet_cell_number+") = \n"+
                         "# = number of cells at left facet     ("+left_facet_cell_number+") + \n"+
                         "# + number of cells at right facet    ("+right_facet_cell_number+") + \n"+
                         "# + number of cells at front facet    ("+front_facet_cell_number+") + \n"+
                         "# + number of cells at back facet     ("+back_facet_cell_number+") + \n"+
                         "# + number of cells at top facet      ("+top_facet_cell_number+") + \n"+
                         "# + number of cells at bottom facet   ("+bottom_facet_cell_number+").\n");
                bw.write("# \n");
                bw.write("#   Total number of cells in tanks    ("+tank_cell_number+") = \n"+
                         "# = number of cells at left facet     ("+left_tank_cell_number+") + \n"+
                         "# + number of cells at right facet    ("+right_tank_cell_number+") + \n"+
                         "# + number of cells at front facet    ("+front_tank_cell_number+") + \n"+
                         "# + number of cells at back facet     ("+back_tank_cell_number+") + \n"+
                         "# + number of cells at top facet      ("+top_tank_cell_number+") + \n"+
                         "# + number of cells at bottom facet   ("+bottom_tank_cell_number+").");
                bw.flush();
                
                bw1.write("# \n");
                bw1.write("# Total number of boundary cells ("+bound_cell_number+") = outer facet cells ("+facet_cell_number+") + tank cells ("+tank_cell_number+").\n");
                bw1.write("# \n");
                bw1.write("#   Total number of outer facet cells ("+facet_cell_number+") = \n"+
                         "# = number of cells at left facet     ("+left_facet_cell_number+") + \n"+
                         "# + number of cells at right facet    ("+right_facet_cell_number+") + \n"+
                         "# + number of cells at front facet    ("+front_facet_cell_number+") + \n"+
                         "# + number of cells at back facet     ("+back_facet_cell_number+") + \n"+
                         "# + number of cells at top facet      ("+top_facet_cell_number+") + \n"+
                         "# + number of cells at bottom facet   ("+bottom_facet_cell_number+").\n");
                bw1.write("# \n");
                bw1.write("#   Total number of cells in tanks    ("+tank_cell_number+") = \n"+
                         "# = number of cells at left facet     ("+left_tank_cell_number+") + \n"+
                         "# + number of cells at right facet    ("+right_tank_cell_number+") + \n"+
                         "# + number of cells at front facet    ("+front_tank_cell_number+") + \n"+
                         "# + number of cells at back facet     ("+back_tank_cell_number+") + \n"+
                         "# + number of cells at top facet      ("+top_tank_cell_number+") + \n"+
                         "# + number of cells at bottom facet   ("+bottom_tank_cell_number+").");
                bw1.flush();
                
                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\n\n_top_bound_time_function_type = "+_top_bound_time_function_type);
            completed_steps.setText(completed_steps.getText()+"\n_top_bound_load_time_portion    = "+_top_bound_load_time_portion+"\n");
            completed_steps.setText(completed_steps.getText()+"\n_top_bound_relax_time_portion   = "+_top_bound_relax_time_portion+"\n");
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
            
            specimen_size_X = real_specimen_size_X;
            specimen_size_Y = real_specimen_size_Y;
            specimen_size_Z = real_specimen_size_Z;
        }

        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of bending.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        private void createBoundCondBendingFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Index of outer boundary cell
            int bound_cell_index = 0;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates = new double[3];

            // Number of outer boundary cells
       //     int bound_cell_number = (_cell_number_I+2)*(_cell_number_J+2)*(_cell_number_K+2)-
       //                             _cell_number_I*_cell_number_J*_cell_number_K;

            int bound_cell_number = 2*_cell_number_K*_cell_number_J+2*_cell_number_K*_cell_number_I;

            //    Parameters characterizing mechanical and thermal loading of each facet:
            // "mechanical" parameter is equal to constant stress or constant strain,
            // "thermal"    parameter is equal to constant temperature or
            //              constant influx of specific surface heat energy.
            double _left_mechanical_param = 0;
            double _left_thermal_param = 0;

            double _right_mechanical_param = 0;
            double _right_thermal_param = 0;

            double _front_mechanical_param = 0;
            double _front_thermal_param = 0;

            double _back_mechanical_param = 0;
            double _back_thermal_param = 0;

            double _top_mechanical_param = 0;
            double _top_thermal_param = 0;

            double _bottom_mechanical_param = 0;
            double _bottom_thermal_param = 0;

            double mech_param = 0;
            double thermal_param = 0;

            // Minimal coordinate Z for boundary cell
            double min_coord_Z = calcCoordinates(0, 0, 1)[2];

            // Maximal coordinate Z for boundary cell
            double max_coord_Z = calcCoordinates(0, 0, _cell_number_K)[2];
            
            // Minimal coordinate X for boundary cell
            double min_coord_X = calcCoordinates(1, 0, 0)[0];

            // Maximal coordinate X for boundary cell
            double max_coord_X = calcCoordinates(_cell_number_I, 0, 0)[0];

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);

                //    Parameters characterizing mechanical and thermal loading of each facet:
                // "mechanical" parameter is equal to constant stress or constant strain,
                // "thermal"    parameter is equal to constant temperature or
                //              constant influx of specific surface heat energy.
                _left_mechanical_param    = (new Double(table.getCell(0, 0))).doubleValue();
                _left_thermal_param       = (new Double(table.getCell(0, 1))).doubleValue();

                _front_mechanical_param   = (new Double(table.getCell(1, 0))).doubleValue();
                _front_thermal_param      = (new Double(table.getCell(1, 1))).doubleValue();

                _top_mechanical_param     = (new Double(table.getCell(2, 0))).doubleValue();
                _top_thermal_param        = (new Double(table.getCell(2, 1))).doubleValue();

                _bottom_mechanical_param  = (new Double(table.getCell(3, 0))).doubleValue();
                _bottom_thermal_param     = (new Double(table.getCell(3, 1))).doubleValue();

                _back_mechanical_param    = (new Double(table.getCell(4, 0))).doubleValue();
                _back_thermal_param       = (new Double(table.getCell(4, 1))).doubleValue();

                _right_mechanical_param   = (new Double(table.getCell(5, 0))).doubleValue();
                _right_thermal_param      = (new Double(table.getCell(5, 1))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                bw.write("#   This file contains parameters of each outer boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, mechanical parameter (strain or stress of CA), ");
                bw.newLine();
                bw.write("# thermal parameter (temperature or thermal energy influx).");
                bw.newLine();
                bw.flush();

                bw1.write("#   This file contains information about outer boundary cellular automata (CA):");
                bw1.newLine();
                bw1.write("# in 1st string - number of  outer boundary CA,");
                bw1.newLine();
                bw1.write("# in each further string - parameters of each outer boundary CA:");
                bw1.newLine();
                bw1.write("# 3 coordinates of CA, mechanical parameter (strain or stress) of CA,");
                bw1.newLine();
                bw1.write("# thermal parameter (temperature or thermal energy influx).");
                bw1.newLine();
                bw1.flush();

                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
                bw1.newLine();
                bw1.flush();

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                       cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                    // Coordinates of boundary cell
                    boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                    // Conditions on left facet
                    if(cell_counter_K > 0 & cell_counter_K < _cell_number_K+1)
                    if(cell_counter_J > 0 & cell_counter_J < _cell_number_J+1)
                    if( cell_counter_I == 0)
                    {
                        // Strain/stress of boundary cell
                     //   mech_param = _left_mechanical_param*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        
                        mech_param = _left_mechanical_param*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z);
                        
                        // Temperature or thermal energy of current cell
                        thermal_param = _left_thermal_param;
                    }
                    
                    // Conditions on right facet
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if(cell_counter_I==_cell_number_I+1)
                    {
                        // Strain/stress of boundary cell
                     //   mech_param = _right_mechanical_param*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        
                        mech_param = _right_mechanical_param*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z);
                        
                        // Temperature or thermal energy of current cell
                        thermal_param = _right_thermal_param;
                    }
                    
                    // Conditions on front facet
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if( cell_counter_J==0)
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    {
                        // Strain/stress of boundary cell
                      //  mech_param = _front_mechanical_param*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        
                        mech_param = _front_mechanical_param*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z);

                        // Temperature or thermal energy of current cell
                        thermal_param = _front_thermal_param;
                    }
                    
                    // Conditions on back facet
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if( cell_counter_J==_cell_number_J+1)
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    {
                        // Strain/stress of boundary cell
                     //   mech_param = _back_mechanical_param*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        
                        mech_param = _back_mechanical_param*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z);
                        
                        // Temperature or thermal energy of current cell
                        thermal_param = _back_thermal_param;                        
                    }
                    
                    // Conditions on top facet
                    if( cell_counter_K==0)
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    {
                        // Strain/stress of boundary cell
                     //   mech_param = _top_mechanical_param*(1 - 2*(boundary_cell_coordinates[0]-min_coord_X)/(max_coord_X-min_coord_X));
                        
                        mech_param = _top_mechanical_param*(boundary_cell_coordinates[0]-min_coord_X)/(max_coord_X-min_coord_X);
                        
                        // Temperature or thermal energy of current cell
                        thermal_param = _top_thermal_param;
                    }

                    // Conditions on bottom facet
                    if( cell_counter_K==_cell_number_K+1)
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    {
                        // Strain/stress of boundary cell
                      //  mech_param = _bottom_mechanical_param*(1 - 2*(boundary_cell_coordinates[0]-min_coord_X)/(max_coord_X-min_coord_X));
                        
                        mech_param = _bottom_mechanical_param*(boundary_cell_coordinates[0]-min_coord_X)/(max_coord_X-min_coord_X);
                        
                        // Temperature or thermal energy of current cell
                        thermal_param = _bottom_thermal_param;
                    }
                    
                    // Writing of physical parameters of boundary cell on the facet
                    bw.write(bound_cell_index+" "+mech_param+" "+thermal_param);
                    bw.newLine();  bw.flush();
                    bw1.write(boundary_cell_coordinates[0]+" "+boundary_cell_coordinates[1]+" "+boundary_cell_coordinates[2]+" "+mech_param+" "+thermal_param);
                    bw1.newLine(); bw1.flush();
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of shear.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        public void createBoundCondShearXFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Number of outer boundary cells
       //     int bound_cell_number = (_cell_number_I+2)*(_cell_number_J+2)*(_cell_number_K+2)-
       //                             _cell_number_I*_cell_number_J*_cell_number_K;

            int bound_cell_number = 2*_cell_number_K*_cell_number_J;

            double max_strain=0, strain=0;

            // Minimal coordinate Z for boundary cell
            double min_coord_Z = calcCoordinates(0, 0, 1)[2];

            // Maximal coordinate Z for boundary cell
            double max_coord_Z = calcCoordinates(0, 0, _cell_number_K)[2];

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);

                max_strain = (new Double(table.getCell(0, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal energy influx per time step,");
                bw.newLine();
                bw.write("# strain per time step, temperature.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();

                boolean write_string;

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    write_string = true;

                    // Conditions on left facet
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    {
                      if(cell_counter_I==0)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain of boundary cell
                        strain = -max_strain*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain+" 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+
                                  strain+" 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                      
                      if(cell_counter_I==_cell_number_I+1)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain of boundary cell
                        strain = max_strain*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain+" 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+
                                  strain+" 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of bending along axes X, Y.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        public void createBoundCondBendingXYFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Number of outer boundary cells
       //     int bound_cell_number = (_cell_number_I+2)*(_cell_number_J+2)*(_cell_number_K+2)-
       //                             _cell_number_I*_cell_number_J*_cell_number_K;

            int bound_cell_number = 2*_cell_number_K*_cell_number_J + 2*_cell_number_K*_cell_number_I;

            double max_strain=0, strain=0;

            // Minimal coordinate Z for boundary cell
            double min_coord_Z = calcCoordinates(0, 0, 1)[2];

            // Maximal coordinate Z for boundary cell
            double max_coord_Z = calcCoordinates(0, 0, _cell_number_K)[2];

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);

                max_strain = (new Double(table.getCell(0, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal energy influx per time step,");
                bw.newLine();
                bw.write("# strain per time step, temperature.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    // Conditions on left and right facets
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain of boundary cell
                        strain = max_strain*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain+" 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+
                                  strain+" 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    // Conditions on front and back facets
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J==0)|(cell_counter_J==_cell_number_J+1))
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain of boundary cell
                        strain = max_strain*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain+" 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+
                                  strain+" 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of shear along axes X and Y.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        private void createBoundCondShearXYFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Number of outer boundary cells
            int bound_cell_number = 2*(_cell_number_J*_cell_number_K+_cell_number_I*_cell_number_K);

            double max_mech_param_left = 0;
            double max_mech_param_front = 0;
            double max_mech_param_top = 0;
            double max_mech_param_bottom = 0;
            double max_mech_param_back = 0;
            double max_mech_param_right = 0;
            
            double max_heat_param_left = 0;
            double max_heat_param_front = 0;
            double max_heat_param_top = 0;
            double max_heat_param_bottom = 0;
            double max_heat_param_back = 0;
            double max_heat_param_right = 0;
            
            double mech_param = 0, heat_param = 0;

            // Maximal coordinate Z for boundary cell
            double max_coord_Z = calcCoordinates(0, 0, _cell_number_K)[2];
                   
            // Minimal coordinate Z for boundary cell
            double min_coord_Z = calcCoordinates(0, 0, 1)[2];
     
            // Maximal coordinate Y for boundary cell
            double max_coord_Y = 0;
            max_coord_Y = Math.max(max_coord_Y, Math.max(calcCoordinates(0, _cell_number_J, 0)[1], calcCoordinates(1, _cell_number_J, 0)[1]));
            max_coord_Y = Math.max(max_coord_Y, Math.max(calcCoordinates(0, _cell_number_J, 1)[1], calcCoordinates(1, _cell_number_J, 1)[1]));
            max_coord_Y = Math.max(max_coord_Y, Math.max(calcCoordinates(0, _cell_number_J, 2)[1], calcCoordinates(1, _cell_number_J, 2)[1]));

            // Minimal coordinate Y for boundary cell
            double min_coord_Y = max_coord_Y;
            min_coord_Y = Math.min(min_coord_Y, Math.min(calcCoordinates(0, 1, 0)[1], calcCoordinates(1, 1, 0)[1]));
            min_coord_Y = Math.min(min_coord_Y, Math.min(calcCoordinates(0, 1, 1)[1], calcCoordinates(1, 1, 1)[1]));
            min_coord_Y = Math.min(min_coord_Y, Math.min(calcCoordinates(0, 1, 2)[1], calcCoordinates(1, 1, 2)[1]));

            // Maximal coordinate X for boundary cell
            double max_coord_X = 0;
            max_coord_X = Math.max(max_coord_X, calcCoordinates(_cell_number_I, 0, 0)[0]);
            max_coord_X = Math.max(max_coord_X, calcCoordinates(_cell_number_I, 0, 1)[0]);
            max_coord_X = Math.max(max_coord_X, calcCoordinates(_cell_number_I, 0, 2)[0]);
            
            // Minimal coordinate X for boundary cell
            double min_coord_X = max_coord_X;
            min_coord_X = Math.min(min_coord_X, calcCoordinates(0, 0, 0)[0]);
            min_coord_X = Math.min(min_coord_X, calcCoordinates(0, 0, 1)[0]);
            min_coord_X = Math.min(min_coord_X, calcCoordinates(0, 0, 2)[0]);

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);
                                
                max_mech_param_left   = (new Double(table.getCell(0, 0))).doubleValue();
                max_mech_param_front  = (new Double(table.getCell(1, 0))).doubleValue();
                max_mech_param_top    = (new Double(table.getCell(2, 0))).doubleValue();
                max_mech_param_bottom = (new Double(table.getCell(3, 0))).doubleValue();
                max_mech_param_back   = (new Double(table.getCell(4, 0))).doubleValue();
                max_mech_param_right  = (new Double(table.getCell(5, 0))).doubleValue();
                
                max_heat_param_left   = (new Double(table.getCell(0, 1))).doubleValue();
                max_heat_param_front  = (new Double(table.getCell(1, 1))).doubleValue();
                max_heat_param_top    = (new Double(table.getCell(2, 1))).doubleValue();
                max_heat_param_bottom = (new Double(table.getCell(3, 1))).doubleValue();
                max_heat_param_back   = (new Double(table.getCell(4, 1))).doubleValue();
                max_heat_param_right  = (new Double(table.getCell(5, 1))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));


                bw.write("#   This file contains parameters of each outer boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, mechanical parameter (strain or stress of CA), ");
                bw.newLine();
                bw.write("# thermal parameter (temperature or thermal energy influx).");
                bw.newLine();

                bw1.write("#   This file contains information about outer boundary cellular automata (CA):");
                bw1.newLine();
                bw1.write("# in 1st string - number of  outer boundary CA,");
                bw1.newLine();
                bw1.write("# in each further string - parameters of each outer boundary CA:");
                bw1.newLine();
                bw1.write("# 3 coordinates of CA, mechanical parameter (strain or stress) of CA,");
                bw1.newLine();
                bw1.write("# thermal parameter (temperature or thermal energy influx).");
                bw1.newLine();

                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    // Conditions on left and right facets
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    {
                      // Conditions on left facet
                      if(cell_counter_I==0)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain/stress of boundary cell
                        mech_param = -max_mech_param_left*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        heat_param =  max_heat_param_left;
                                
                        // Writing of physical parameters of boundary cell on specimen facet
                        bw.write(bound_cell_index+" "+mech_param+" "+heat_param);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+mech_param+" "+heat_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                      
                      // Conditions on right facet
                      if(cell_counter_I==_cell_number_I+1)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain/stress of boundary cell
                        mech_param = max_mech_param_right*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        heat_param = max_heat_param_right;
                        
                        // Writing of physical parameters of boundary cell on specimen facet
                        bw.write(bound_cell_index+" "+mech_param+" "+heat_param);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+mech_param+" "+heat_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                    }

                    // Conditions on front and back facets
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    {
                      // Conditions on front facet
                      if(cell_counter_J==0)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain/stress of boundary cell
                        mech_param = -max_mech_param_front*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        heat_param =  max_heat_param_front;

                        // Writing of physical parameters of boundary cell on specimen facet
                        bw.write(bound_cell_index+" "+mech_param+" "+heat_param);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+mech_param+" "+heat_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }

                      // Conditions on back facet
                      if(cell_counter_J==_cell_number_J+1)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain/stress of boundary cell
                        mech_param = max_mech_param_back*(1 - 2*(boundary_cell_coordinates[2]-min_coord_Z)/(max_coord_Z-min_coord_Z));
                        heat_param = max_heat_param_back;

                        // Writing of physical parameters of boundary cell on specimen facet
                        bw.write(bound_cell_index+" "+mech_param+" "+heat_param);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+mech_param+" "+heat_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                    }

                    // Conditions on top and bottom facets
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    {
                      // Conditions on top facet
                      if(cell_counter_K==0)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain/stress of boundary cell
                        mech_param = -max_mech_param_top*(1 - 2*(boundary_cell_coordinates[0]-min_coord_X)/(max_coord_X-min_coord_X));
                        heat_param =  max_heat_param_top;

                        // Writing of physical parameters of boundary cell on specimen facet
                        bw.write(bound_cell_index+" "+mech_param+" "+heat_param);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+mech_param+" "+heat_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }

                      // Conditions on bottom facet
                      if(cell_counter_K==_cell_number_K+1)
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Strain/stress of boundary cell
                        mech_param = max_mech_param_bottom*(1 - 2*(boundary_cell_coordinates[0]-min_coord_X)/(max_coord_X-min_coord_X));
                        heat_param = max_heat_param_bottom;

                        // Writing of physical parameters of boundary cell on specimen facet
                        bw.write(bound_cell_index+" "+mech_param+" "+heat_param);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" "+mech_param+" "+heat_param);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of tension of bottom half of specimen along axis X.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        public void createBoundCondBottomHalfTensionXFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;


            // Cell strain
            double strain=0;

            // Number of boundary cells
            int bound_cell_number = 2*_cell_number_J*_cell_number_K;

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);

                strain = (new Double(table.getCell(0, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal parameter,");
                bw.newLine();
                bw.write("# mechanical parameter.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    // Conditions on left and right facets
                    if((cell_counter_K> 0)&(cell_counter_K <= _cell_number_K/2))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    if((cell_counter_K> _cell_number_K/2)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+strain);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }


        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of tension of bottom half of specimen along axes X, Y.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        public void createBoundCondBottomHalfTensionXYFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;


            // Cell strain
            double strain=0;

            // Number of boundary cells
            int bound_cell_number = 2*_cell_number_J*_cell_number_K + 2*_cell_number_I*_cell_number_K;

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);

                strain = (new Double(table.getCell(0, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal parameter,");
                bw.newLine();
                bw.write("# mechanical parameter.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    // Conditions on left and right facets
                    if((cell_counter_K> 0)&(cell_counter_K <= _cell_number_K/2))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    if((cell_counter_K> _cell_number_K/2)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+strain);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    // Conditions on front and back facets
                    if((cell_counter_K> 0)&(cell_counter_K <= _cell_number_K/2))
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    if((cell_counter_J==0)|(cell_counter_J==_cell_number_J+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    if((cell_counter_K> _cell_number_K/2)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    if((cell_counter_J==0)|(cell_counter_J==_cell_number_J+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+strain);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of tension of bottom half of specimen along axis X.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         */
        public void createBoundCondBackHalfTensionXFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Cell strain
            double strain=0;

            // Number of boundary cells
            int bound_cell_number = 2*_cell_number_J*_cell_number_K;

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);

                strain = (new Double(table.getCell(0, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal parameter,");
                bw.newLine();
                bw.write("# mechanical parameter.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    // Conditions on left and right facets
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J <= _cell_number_J/2))                    
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> _cell_number_J/2)&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+strain);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of tension of bottom half of specimen along axis X.
         * Outer boundary cells can consist planar cracks perpendicular to axis of tension
         * in back half of specimen.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         * @param crack_number   number of planar cracks perpendicular to axis of tension
         *                       in back half of specimen
         */
        public void createBoundCondCracksBackHalfTensionXFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K, int crack_number)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Cell strain
            double strain=0;

            // Number of boundary cells
            int bound_cell_number = 0;//2*_cell_number_J*_cell_number_K;

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);
                System.out.println("Name of read file:    "+bound_params_file);

                strain = (new Double(table.getCell(0, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
                System.out.println("ERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal parameter,");
                bw.newLine();
                bw.write("# mechanical parameter.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
        /*
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();
         */

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    // Conditions on left and right facets
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J<=Math.round(_cell_number_J*0.5)))
                    {
                      if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 0");

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 0");

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                      else
                      if((cell_counter_I%(_cell_number_I/(crack_number+1))==0)&
                         (cell_counter_I/(_cell_number_I/(crack_number+1))==crack_number-1))//<=crack_number))
                      {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+0);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+0);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                      }
                    }

                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> Math.round(_cell_number_J*0.5))&(cell_counter_J< _cell_number_J+1))
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {                      
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+strain);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+strain);

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }
        
        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of tension of specimen along axis Y.
         * Outer boundary cells can consist planar cracks perpendicular to axis of tension
         * in left half of specimen.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         * @param crack_number   number of planar cracks perpendicular to axis of tension
         *                       in left half of specimen
         */
        public void createBoundCondCracksTensionYFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K, int crack_number)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Cell strain
            double strain=0;

            // Number of boundary cells
            int bound_cell_number = 0;//2*_cell_number_J*_cell_number_K;

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);
                System.out.println("Name of read file:    "+bound_params_file);

                strain = (new Double(table.getCell(1, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
                System.out.println("ERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal parameter,");
                bw.newLine();
                bw.write("# mechanical parameter.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
        /*
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();
         */
                boolean bound_cell_is_built = false;

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    bound_cell_is_built = false;
                    
                    // Conditions on front and back facets
                    if(!bound_cell_is_built)
                    if((cell_counter_J==0)|(cell_counter_J==_cell_number_J+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on front and back facets
                        bw.write(bound_cell_index+" 0 "+strain);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+strain);
                        
                        bound_cell_is_built = true;
                        
                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    // Conditions on left and right facets
                    if(!bound_cell_is_built)
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on left and right facets
                        bw.write(bound_cell_index+" 0 "+0);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+0);

                        bound_cell_is_built = true;

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                    
                    // Conditions on top and bottom facets
                    if(!bound_cell_is_built)
                    if((cell_counter_K==0)|(cell_counter_K==_cell_number_K+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on top and bottom facets
                        bw.write(bound_cell_index+" 0 "+0);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+0);

                        bound_cell_is_built = true;

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    // Simulation of cracks
                    if(!bound_cell_is_built)
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_I> 0)&(cell_counter_I< _cell_number_I+1))
                    if((cell_counter_I>0)&(cell_counter_I<=Math.round(cell_number_I*0.3)))
                    if(((cell_counter_J+1)%(_cell_number_J/(crack_number+1))==0)|
                       ( cell_counter_J   %(_cell_number_J/(crack_number+1))==0)|
                       ((cell_counter_J-1)%(_cell_number_J/(crack_number+1))==0))
                    if ((cell_counter_J+1)/(_cell_number_J/(crack_number+1))==crack_number)//<=crack_number)//
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+0);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+0);

                        bound_cell_is_built = true;

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }


        /** This method creates file with information about properties of outer boundary cells
         * (influx of thermal energy, influx of mechanical energy, temperature)
         * for simulation of tension of specimen along axis X.
         * Outer boundary cells can consist planar cracks perpendicular to axis of tension
         * in left half of specimen.
         * @param bound_params_file file with information about conditions on each of facets
         * @param write_file_name file with information about properties of boundary cells
         * @param bound_cond_geometry_file file with information about geometry of outer boundary cells
         * @param _cell_number_I number of cells along axis X
         * @param _cell_number_J number of cells along axis Y
         * @param _cell_number_K number of cells along axis Z
         * @param crack_number   number of planar cracks perpendicular to axis of tension
         *                       in left half of specimen
         */
        public void createBoundCondCracksTensionXFile(String bound_params_file, String write_file_name, String bound_cond_geometry_file,
                                               int _cell_number_I, int _cell_number_J, int _cell_number_K, int crack_number)
        {
            // Index of outer boundary cell
            int bound_cell_index;

            // Coordinates of outer boundary cell
            double[] boundary_cell_coordinates;

            // Cell strain
            double strain=0;

            // Number of boundary cells
         //   int bound_cell_number = 0;//2*_cell_number_J*_cell_number_K;

            try
            {
                TextTableFileReader reader = new TextTableFileReader(bound_params_file);
                StringVector table = reader.convertToTable();

                completed_steps.setText(completed_steps.getText()+"\nName of read file:    "+bound_params_file);
                System.out.println("Name of read file:    "+bound_params_file);

                strain = (new Double(table.getCell(0, 0))).doubleValue();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
                System.out.println("ERROR: "+io_ex);
            }

            try
            {
                BufferedWriter bw = new BufferedWriter(new FileWriter(write_file_name));
                BufferedWriter bw1= new BufferedWriter(new FileWriter(bound_cond_geometry_file));

                // Writing of number of outer boundary cells
                bw.write("#   This file contains parameters of each boundary cellular automaton (CA):");
                bw.newLine();
                bw.write("# index of CA, thermal parameter,");
                bw.newLine();
                bw.write("# mechanical parameter.");
                bw.newLine();
                bw.flush();

                // Writing of number of outer boundary cells
        /*
                bw1.write(bound_cell_number+"");
         //       bw1.write(" "+_cell_number_I+" "+_cell_number_J+" "+_cell_number_K);
                bw1.newLine();
                bw1.flush();
         */
                boolean bound_cell_is_built = false;

                for(int cell_counter_K = 0; cell_counter_K < _cell_number_K+2; cell_counter_K++)
                for(int cell_counter_J = 0; cell_counter_J < _cell_number_J+2; cell_counter_J++)
                for(int cell_counter_I = 0; cell_counter_I < _cell_number_I+2; cell_counter_I++)
                {
                    bound_cell_is_built = false;

                    // Conditions on left and right facets
                    if(!bound_cell_is_built)
                    if((cell_counter_I==0)|(cell_counter_I==_cell_number_I+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on left and right facets
                        bw.write(bound_cell_index+" 0 "+strain);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+strain);

                        bound_cell_is_built = true;

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    // Conditions on front and back facets
                    if(!bound_cell_is_built)
                    if((cell_counter_J==0)|(cell_counter_J==_cell_number_J+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on front and back facets
                        bw.write(bound_cell_index+" 0 "+0);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+0);

                        bound_cell_is_built = true;

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    // Conditions on top and bottom facets
                    if(!bound_cell_is_built)
                    if((cell_counter_K==0)|(cell_counter_K==_cell_number_K+1))
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on top and bottom facets
                        bw.write(bound_cell_index+" 0 "+0);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+0);

                        bound_cell_is_built = true;

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }

                    // Simulation of cracks
                    if(!bound_cell_is_built)
                    if((cell_counter_K> 0)&(cell_counter_K< _cell_number_K+1))
                    if((cell_counter_J> 0)&(cell_counter_J<=Math.round(cell_number_J*0.3)))
                    if((cell_counter_I!=0)&(cell_counter_I!=cell_number_I+1))
                    if(((cell_counter_I+1)%(_cell_number_I/(crack_number+1))==0)|
                       ( cell_counter_I   %(_cell_number_I/(crack_number+1))==0)|
                       ((cell_counter_I-1)%(_cell_number_I/(crack_number+1))==0))
                    if ((cell_counter_I+1)/(_cell_number_I/(crack_number+1))==crack_number)//<=crack_number)//
                    {
                        bound_cell_index = cell_counter_K*(_cell_number_J+2)*(_cell_number_I+2)+
                                           cell_counter_J*(_cell_number_I+2) + cell_counter_I;

                        // Coordinates of boundary cell
                        boundary_cell_coordinates = calcCoordinates(cell_counter_I, cell_counter_J, cell_counter_K);

                        // Writing of physical parameters of boundary cell on specimen top facet
                        bw.write(bound_cell_index+" 0 "+0);

                        bw1.write(boundary_cell_coordinates[0]+" "+
                                  boundary_cell_coordinates[1]+" "+
                                  boundary_cell_coordinates[2]+" 0 "+0);

                        bound_cell_is_built = true;

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                }

                bw.close();
                bw1.close();
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+write_file_name);
            completed_steps.setText(completed_steps.getText()+"\nName of created file: "+bound_cond_geometry_file);
        }

        /** The method creates file in "PDB" format.
         * Grain boundaries are represented by gray cells.
         * @param step_number number of time step corresponding to read file
         * @param read_file_name name of read file with results
         * @param write_file_name name of written file (with extension ".pdb")
         */
        public void createFilePDBGrainBounds(int step_number, String read_file_name, String write_file_name)
    {
        String string;

        // Coefficient of magnification in "RasMol"
        double magnif_coeff = 1.25;

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

        // Type of cell location in grain: internal or boundary
        byte location_in_grain = -1;

        // Type of cell
        byte type = -1;

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

            // Reading of the number of strings of the file from the file
            int string_number = new Integer(br.readLine()).intValue();

         //   for (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                 // Reading of coordinates and physical parameters of current cell
                    grain_index  = new Integer(st.nextToken()).intValue();//-1;

                    coordinate_X = new Double(st.nextToken()).doubleValue();
                    coordinate_Y = new Double(st.nextToken()).doubleValue();
                    coordinate_Z = new Double(st.nextToken()).doubleValue();

                    temperature  = (int)Math.round(new Double(st.nextToken()).doubleValue());
                    mech_energy  = (int)Math.round(new Double(st.nextToken()).doubleValue());

                    location_in_grain = new Byte(st.nextToken()).byteValue();
                    type         = new Byte(st.nextToken()).byteValue();

                    // Redetermination of cell coordinates
                    coordinate_X = magnif_coeff*coordinate_X;
                    coordinate_Y = magnif_coeff*coordinate_Y;
                    coordinate_Z = magnif_coeff*coordinate_Z;

                    if(type == 1)
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
                        if(show_grain_bounds == SHOW_GRAIN_BOUNDS)
                        if(location_in_grain == CELL_ON_GRAIN_BOUNDARY)
                        {
                            cell_state = "H  ";
                        }
                    }
                    else
                        cell_state = "H  ";


                    // Printing of data concerning all grains
               //     if (value == 6) // Printing of data concerning one of grains
                    if(type == 1) // Printing of data concerning inner grains
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

        public void setCellNumberI(int _cellNumberI)
        {
            cell_number_I = _cellNumberI;
        }

        public void setCellNumberJ(int _cellNumberJ)
        {
            cell_number_J = _cellNumberJ;
        }

        public void setCellNumberK(int _cellNumberK)
        {
            cell_number_K = _cellNumberK;
        }

        public void setTopBoundaryTemperature(double top_temp)
        {
            top_boundary_temperature = top_temp;
        }

        public void setBottomBoundaryTemperature(double bottom_temp)
        {
            bottom_boundary_temperature = bottom_temp;
        }
        
        /** The method calculates the parameter of boundary cell.
         * @param coord_X coordinate X of boundary cell
         * @param coord_Y coordinate Y of boundary cell
         * @param coord_Z coordinate Z of boundary cell
         * @param function_type the type of dependence of the parameter of 
         *                      the boundary cell on the cell coordinates
         * @return the parameter of boundary cell
         */
        private double boundaryParam(double coord_X, double coord_Y, double coord_Z, int function_type)
        {
            // the mechanical parameter of boundary cell
            double bound_mech_param = 1;
            
            // Modified cell coordinates
            double mod_coord_X = coord_X;
            double mod_coord_Y = coord_Y;
            double mod_coord_Z = coord_Z;
            
            if(function_type == Common.UNIT_FUNCTION)
            {
                bound_mech_param = 1;
            }            
            
            if(function_type == Common.DISTANCE_FROM_ORIGIN)
            {
                // Cell coordinates relative to specimen sizes
                mod_coord_X = coord_X/specimen_size_X;
                mod_coord_Y = coord_Y/specimen_size_Y;
                mod_coord_Z = coord_Z/specimen_size_Z;
                
                // Distance from the boundary cell centre to the origin of coordinate system
                bound_mech_param = Math.sqrt(1.0/3.0)*Math.sqrt(mod_coord_X*mod_coord_X + mod_coord_Y*mod_coord_Y + mod_coord_Z*mod_coord_Z);                   
            }
            
            if(function_type == Common.DISTANCE_FROM_CENTRE)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // Distance from the boundary cell centre to the specimen centre
                bound_mech_param = Math.sqrt(4.0/3.0*(mod_coord_X*mod_coord_X + mod_coord_Y*mod_coord_Y + mod_coord_Z*mod_coord_Z));
            }
            
            if(function_type == Common.COORDINATE_X_VALUE)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
             //   mod_coord_X = coord_X/specimen_size_X - 0.5;
                
                //  The modified coordinate X of the boundary cell centre
                // in coordinate system with the origin in the specimen centre
             //   bound_mech_param = 2*mod_coord_X;
                
                //-----------------------------------------------
                //  Cell coordinates relative to specimen sizes 
                mod_coord_X = coord_X/specimen_size_X;
                
                bound_mech_param = mod_coord_X;
            }
            
            if(function_type == Common.COORDINATE_Y_VALUE)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
              //  mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                
                //  The modified coordinate Y of the boundary cell centre
                // in coordinate system with the origin in the specimen centre
             //  bound_mech_param = 2*mod_coord_Y;
                
                //-----------------------------------------------
                //  Cell coordinates relative to specimen sizes 
                mod_coord_Y = coord_Y/specimen_size_Y;
                
                bound_mech_param = mod_coord_Y;
            }            
            
            if(function_type == Common.COORDINATE_Z_VALUE)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
             //   mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                //  The modified coordinate Z of the boundary cell centre
                // in coordinate system with the origin in the specimen centre
             //   bound_mech_param = 2*mod_coord_Z;
                
                //-----------------------------------------------
                //  Cell coordinates relative to specimen sizes 
                mod_coord_Z = coord_Z/specimen_size_Z;
                
                bound_mech_param = mod_coord_Z;
            }
            
            if(function_type == Common.DISTANCE_FROM_AXIS_X)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // Distance from the boundary cell centre to the axis X
                // of the coordinate system with the origin in the specimen centre
                bound_mech_param = Math.sqrt(2*mod_coord_Y*mod_coord_Y + 2*mod_coord_Z*mod_coord_Z);                   
            }
            
            if(function_type == Common.DISTANCE_FROM_AXIS_Y)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // Distance from the boundary cell centre to the axis Y
                // of the coordinate system with the origin in the specimen centre
                bound_mech_param = Math.sqrt(2*mod_coord_X*mod_coord_X + 2*mod_coord_Z*mod_coord_Z);                   
            }
            
            if(function_type == Common.DISTANCE_FROM_AXIS_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                
                // Distance from the boundary cell centre to the axis Z 
                // of the coordinate system with the origin in the specimen centre
                bound_mech_param = Math.sqrt(2*mod_coord_X*mod_coord_X + 2*mod_coord_Y*mod_coord_Y);                   
            }
            
            if(function_type == Common.SUM_OF_ALL_ABS_COORDINATES)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // Sum of absolute values of all coordinates
                bound_mech_param = (2.0/3.0)*(Math.abs(mod_coord_X) + Math.abs(mod_coord_Y) + Math.abs(mod_coord_Z));
            }            
            
            if(function_type == Common.SUM_OF_ABS_COORD_X_AND_ABS_COORD_Y)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // Sum of absolute values of coordinates X and Y
                bound_mech_param = Math.abs(mod_coord_X) + Math.abs(mod_coord_Y);
            }            
            
            if(function_type == Common.SUM_OF_ABS_COORD_X_AND_ABS_COORD_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // Sum of absolute values of coordinates X and Z
                bound_mech_param = Math.abs(mod_coord_X) + Math.abs(mod_coord_Z);
            }            
            
            if(function_type == Common.SUM_OF_ABS_COORD_Y_AND_ABS_COORD_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // Sum of absolute values of coordinates Y and Z
                bound_mech_param = Math.abs(mod_coord_Y) + Math.abs(mod_coord_Z);
            }            
            
            if(function_type == Common.ABS_COORD_X)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // The absolute value of coordinate X
                bound_mech_param = 2*Math.abs(mod_coord_X);
            }            
            
            if(function_type == Common.ABS_COORD_Y)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // The absolute value of coordinate Y
                bound_mech_param = 2*Math.abs(mod_coord_Y);
            }            
            
            if(function_type == Common.ABS_COORD_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // The absolute value of coordinate Z
                bound_mech_param = 2*Math.abs(mod_coord_Z);
            }
            
            
            if(function_type == Common.ONE_MINUS_SUM_OF_ALL_ABS_COORDINATES)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus 2/3 of the sum of absolute values of all coordinates
                bound_mech_param = 1-(2.0/3.0)*(Math.abs(mod_coord_X) + Math.abs(mod_coord_Y) + Math.abs(mod_coord_Z));
            }            
            
            if(function_type == Common.ONE_MINUS_SUM_OF_ABS_COORD_X_AND_ABS_COORD_Y)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus the sum of absolute values of coordinates X and Y
                bound_mech_param = 1-Math.abs(mod_coord_X) - Math.abs(mod_coord_Y);
            }            
            
            if(function_type == Common.ONE_MINUS_SUM_OF_ABS_COORD_X_AND_ABS_COORD_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus the sum of absolute values of coordinates X and Z
                bound_mech_param = 1-Math.abs(mod_coord_X) - Math.abs(mod_coord_Z);
            }            
            
            if(function_type == Common.ONE_MINUS_SUM_OF_ABS_COORD_Y_AND_ABS_COORD_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus the sum of absolute values of coordinates Y and Z
                bound_mech_param = 1-Math.abs(mod_coord_Y) - Math.abs(mod_coord_Z);
            }            
            
            if(function_type == Common.ONE_MINUS_ABS_COORD_X)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus 2 multiplied by the absolute value of coordinate X
                bound_mech_param = 1-2*Math.abs(mod_coord_X);
            }            
            
            if(function_type == Common.ONE_MINUS_ABS_COORD_Y)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus 2 multiplied by the absolute value of coordinate Y
                bound_mech_param = 1-2*Math.abs(mod_coord_Y);
            }            
            
            if(function_type == Common.ONE_MINUS_ABS_COORD_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus 2 multiplied by the absolute value of coordinate Z
                bound_mech_param = 1-2*Math.abs(mod_coord_Z);
            }                
            
            if(function_type == Common.ONE_MINUS_DISTANCE_FROM_CENTRE)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus (2/sqrt(3))*(distance from the boundary cell centre to the specimen centre)
                bound_mech_param = 1 - Math.sqrt(4.0/3.0*(mod_coord_X*mod_coord_X + mod_coord_Y*mod_coord_Y + mod_coord_Z*mod_coord_Z));
            }
            
            if(function_type == Common.ONE_MINUS_DISTANCE_FROM_AXIS_X)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus sqrt(2)*(distance from the boundary cell centre to the axis X)
                // of the coordinate system with the origin in the specimen centre
                bound_mech_param = 1 - Math.sqrt(2*mod_coord_Y*mod_coord_Y + 2*mod_coord_Z*mod_coord_Z);                   
            }
            
            if(function_type == Common.ONE_MINUS_DISTANCE_FROM_AXIS_Y)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Z = coord_Z/specimen_size_Z - 0.5;
                
                // One minus sqrt(2)*(distance from the boundary cell centre to the axis Y)
                // of the coordinate system with the origin in the specimen centre
                bound_mech_param = 1 - Math.sqrt(2*mod_coord_X*mod_coord_X + 2*mod_coord_Z*mod_coord_Z);                   
            }
            
            if(function_type == Common.ONE_MINUS_DISTANCE_FROM_AXIS_Z)
            {
                //  Cell coordinates relative to specimen sizes 
                // in coordinate system with the origin in the specimen centre
                mod_coord_X = coord_X/specimen_size_X - 0.5;
                mod_coord_Y = coord_Y/specimen_size_Y - 0.5;
                
                // One minus sqrt(2)*(distance from the boundary cell centre to the axis Z)
                // of the coordinate system with the origin in the specimen centre
                bound_mech_param = 1 - Math.sqrt(2*mod_coord_X*mod_coord_X + 2*mod_coord_Y*mod_coord_Y);
            }
            
          //  bound_mech_param = 1;
             
            return bound_mech_param;
        }
}