package paral_calc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Maksimov Pavel
 */
public class Specimen_Arrays 
{
    /** Paths and extensions of special files
     */
    public static final String TASK_PATH      = "./user/task_db";
    public static final String TASK_EXTENSION = "seca";
    
    /** Hexagonal close packing */
    public static final byte HEXAGONAL_CLOSE_PACKING = 0;
    
    /** Simple cubic packing */
    public static final byte SIMPLE_CUBIC_PACKING    = 1;
    
    /** File with information about task*/
    public static String task_file;
    
    /** Name of file for writing of simulation results */
    public static String write_file_name;
    
    Properties task_properties;
    
    /** Name of file with information about specimen geometrical properties */
    public static String specimen_file;

    /** Name of file with information about initial conditions */
    public static String init_cond_file;

    /** Name of file with information about boundary conditions */
    public static String bound_cond_file;
    
    /** Name of file with information about type of loading for each facet */
    public static String bound_grains_file_name;
    
    /** Name of file with information about properties of tanks */
    public static String tanks_file_name;
    
    /** Materials and types of loading for each facet*/
    public static String[] mat_name, mech_load_type, heat_load_type;
    
    /** Value of time step */
    public static float time_step;

    /** Total time of numerical experiment */
    public static float total_time;
    
    /** Number of time steps*/
    public static long step_number;

    /** Number of output files */
    public static int output_file_number;
    
    /** Variable responsible for simulation of heat flows propagation */
    public static byte simulate_heat_flows;
    
    /** Variable responsible for simulation of mechanical flows propagation */
    public static byte simulate_mech_flows;
    
    /** Variable responsible for simulation of recrystallization */
    public static byte simulate_recryst;
    
    /** Variable responsible for simulation of crack generation */
    public static byte simulate_cracks;
    
    /** Variable responsible for simulation of heat expansion */
    public static byte simulate_heat_expansion;
    
    /** Variable responsible for calculation of force moments at each time step:
     * if calc_force_moments = 0 then force moments are not calculated,
     * if calc_force_moments = 1 then force moments are calculated. */
    public static byte calc_force_moments;
    
    /** Minimal number of neighbour cells in adjacent grain 
     * necessary for cell transition to this adjacent grain */
    public static int min_neighbours_number;
    
    /** Type of interaction of boundary cells with neighbours:
     * 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
     * 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
     * 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell. */
    public static byte bound_interaction_type ;
    
    /** Name of file with information about grain structure of specimen */
    public static String grains_file;
    
    /** Specimen size in direction of axis X */
    public static float specimen_size_X;

    /** Specimen size in direction of axis Y */
    public static float specimen_size_Y;
  
    /** Specimen size in direction of axis Z */
    public static float specimen_size_Z;
    
    /** Cell number in direction of axis X */
    public static int cell_number_X;
    
    /** Cell number in direction of axis Y */
    public static int cell_number_Y;
    
    /** Cell number in direction of axis Z */
    public static int cell_number_Z;
    
    /** Cell size */
    public static float cell_size;
    
    /** Type of cell packing */
    public static byte packing_type;
    
    /** Number of cells at 1st coordination sphere */
    public static int neighb1S_number;
    
    /** Number of cells at 1st, 2nd and 3rd coordination spheres */
    public static int neighb3D_number;
    
    /** Number of grains in specimen */
    public static int grain_number = 0;
    
    /** Initial number of grains */
    public static int init_grain_number = 0;
    
    /** Number of cells in specimen */
    public static int cell_number = 0;
    
    /** Number of boundary cells in specimen */
    public static int bound_cell_number = 0;
    
    /** Array of cell temperatures */
    public static float[] temperatures;
    
    /** Array of names of materials */
    public static String[] materials;
    
    // Indices of grains
    public static int[] grain_indices;
    
    // Orientation angles of grain crystal lattice
    public static float[][] grain_angles;
    
    // Densities of dislocations in grains
    public static float[] grainDislDensities;
    
    // Average densities of dislocations in grains
    public static float[] grainAverageDislDensities;
        
    // Deviations of dislocation density in grains
    public static float[] grainDislDensityDeviations;
    
    // Types of grains: 0 -"grain" of interacting outer boundary cells,
    //                  1 - grain of inner cells,
    //                  2 -"grain" of adiabatic outer boundary cells
    public static byte[] grain_types;
    
    /** Types of grain according to its role in recrystallization process:
     * 0 - initial grain; 1 - new grain; 2 - twinning grain.
     */
    public static byte[] grain_rec_types;
    
    /** Indices of grains, from which twinning grains grow
     */
    public static int[] root_grain_indices;
    
    // Cell initial parameters
    public static float[] initialThermalEnergies;
    public static float[] initialMechEnergies;
    public static float[] initialTemperatures;
    public static int[]   grainIndices;
    public static byte[]  cellTypes;
    public static float[] initialTorsEnergies;
    
    /** Parameters of boundary cells */
    public static int   bound_cell_indices[];
    public static float bound_cell_min_mech_energies[];
    public static float bound_cell_max_mech_energies[];
    public static float bound_cell_min_heat_energies[];
    public static float bound_cell_heat_energies[];
    public static byte  bound_cell_time_function_types[];
    public static float bound_cell_load_time_portions[];
    public static float bound_cell_relax_time_portions[];
    
    public static final String MATERIALS_PATH     = "./user/mat_db";
    public static final String MATERIAL_EXTENSION = "mtrl";
    
    /** Parameters of materials */
    public static float[] mod_elast;
    public static float[] density;
    public static float[] heatExpansionCoeff;
    public static float[] yieldStrain;
    public static float[] ultimateStrain;
    public static float[] energy_coeff;
    public static float[] specific_heat_capacity;
    public static float[] thermal_conductivity;
    public static float[] thermal_conduct_bound;
    public static float[] phonon_portion;
    public static float[] lowTemperThreshold;
    public static float[] highTemperThreshold;
    public static float[] actEnergy;
    public static float[] angleLimitHAGB;
    public static float[] energyHAGB;
    public static float[] maxMobility;
    public static float[] dislMaxMobility;
    public static float[] mechMaxMobility;
    public static float[] mod_shear;
    public static float[] lattice_parameter;
    public static float[] disl_distr_coeff;
    public static float[] yield_state_coeff;
    public static float[] ultimate_state_coeff;
    public static float[] part_vol_fraction;
    public static float[] part_radius;
    public static float[] molar_mass;
    public static float[] threshold_stress;
    public static float[] torsion_energy_coeff;
    public static float[] torsion_energy_coeff_gr_bound;
    public static float[] torsion_energy_coeff_gb_region;
    
    // the low temperature of recrystallization (generation of new grains)
    public static float[] low_tempr_recryst;
     
    // the high temperature of recrystallization (generation of new grains)
    public static float[] high_tempr_recryst;
    
    // the low temperature of twinning
    public static float[] low_tempr_twinning;
        
    // the high temperature of twinning
    public static float[] high_tempr_twinning;
        
    // the maximal probability of recrystallization (generation of new grains)
    public static float[] max_prob_recryst;
    
    // the maximal probability of twinning
    public static float[] max_prob_twinning;
    
    // minimal density of dislocations
    public static float[] min_disl_density;
    
    // Lengths of lattice vectors for grains
    public static float[] lattice_vector_A_lengths;
    public static float[] lattice_vector_B_lengths;
    public static float[] lattice_vector_C_lengths;
    
    // Angles between lattice vectors for grains
    public static float[] lattice_angles_vecA_vecB;
    public static float[] lattice_angles_vecB_vecC;
    public static float[] lattice_angles_vecC_vecA;
    
    // Coefficients of lattice anisotropy for grains
    public static float[] lattice_anis_coeff;
    
    /** The constructor creates representative of class
     * @param task_name Name of task
     */
    public Specimen_Arrays(String task_name)
    {
        // File with information about parameters of executed task
        task_file = TASK_PATH + "/" + task_name + "." + TASK_EXTENSION;
        
        // Name of file for writing of simulation results
        write_file_name = TASK_PATH + "/" + task_name + "/" + task_name;
        
        // Reading of values of fields of the class from the file
        readTask(task_file);
        
      // System.out.println("Name of task: "+task_file);
        
        // Loading of information about specimen
        loadStructure(specimen_file);
        
      // System.out.println("Name of specimen: "+specimen_file);
        
        // Creation of grains: reading of parameters of each grain from the file
        createGrains();
        
        // Reading of initial conditions of task
        readInitCond(init_cond_file);
        
        // Creation of boundary conditions of task
        readBoundCond(bound_cond_file);
        
        // Setting of properties of cells according to its materials
        setCellMaterialParams();
    }
    
    /** The method creates representative of the class.
     * @param args description of task
     */
    public static void main(String[] args)
    {
        /* Creation of representative of class for simulation of recrystallization
        */
        Specimen_Arrays specArrays = new Specimen_Arrays(args[0]);
    }
    
    /** The method reads values of fields of the class from the file.
     * @param file_name name of file containing values of fields of the class
     */
    private void readTask(String file_name)
    {
        task_properties = new Properties();
        
        String string;
        StringTokenizer st;
        
        int facet_number = 6;
        int tank_number  = 6;
        
        mat_name       = new String[facet_number + tank_number];
        mech_load_type = new String[facet_number + tank_number];
        heat_load_type = new String[facet_number + tank_number];
        int st_counter = 0;
        
        String geom_param;
        
        try 
        {
            FileInputStream fin = new FileInputStream(file_name);
            task_properties.load(fin);
            fin.close();
            
            // Reading of file names
            specimen_file           = task_properties.getProperty("specimen_file");
            init_cond_file          = task_properties.getProperty("init_cond_file");
            bound_cond_file         = task_properties.getProperty("bound_cond_file");
            bound_grains_file_name  = task_properties.getProperty("bound_grains_file_name");
            
            // Reading of task parameters
            time_step               = (new Float  (task_properties.getProperty("time_step"))).floatValue();
            total_time              = (new Float  (task_properties.getProperty("total_time"))).floatValue();
            output_file_number      = (new Integer(task_properties.getProperty("output_file_number"))).intValue();
            cell_size               = (new Float  (task_properties.getProperty("cell_size"))).floatValue();
            
            step_number             = (long)Math.round(total_time/time_step);

            // Reading of variable responsible for simulation of heat flows propagation
            simulate_heat_flows     = (new Byte(task_properties.getProperty("simulate_heat_flows"))).byteValue();
            
            // Reading of variable responsible for simulation of mechanical flows propagation
            simulate_mech_flows     = (new Byte(task_properties.getProperty("simulate_mech_flows"))).byteValue();
            
            // Reading of variable responsible for simulation of recrystallization
            simulate_recryst        = (new Byte(task_properties.getProperty("simulate_recryst"))).byteValue();
            
            // Reading of variable responsible for simulation of crack generation
            simulate_cracks         = (new Byte(task_properties.getProperty("simulate_cracks"))).byteValue();

            // Reading of variable responsible for simulation of heat expansion
            simulate_heat_expansion = (new Byte(task_properties.getProperty("simulate_heat_expansion"))).byteValue();
            
            // Reading of variable responsible for calculation of force moments at each time step 
            calc_force_moments      = (new Byte(task_properties.getProperty("calc_force_moments"))).byteValue();
            
            // Minimal number of neighbour cells in adjacent grain necessary for cell transition to this adjacent grain
            //  min_neighbours_number   = (new Integer(task_properties.getProperty("min_neighbours_number"))).intValue();
          
            // Type of interaction of boundary cells with neighbours:
            // 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
            // 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
            // 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
            bound_interaction_type = (new Byte(task_properties.getProperty("bound_interaction_type"))).byteValue();
          
            BufferedReader br = new BufferedReader(new FileReader(bound_grains_file_name));
            
            System.out.println("======== FACETS AND TANKS ========");
            
            while(br.ready())
            {
              string = br.readLine();
              st = new StringTokenizer(string);
                
              if(st.hasMoreTokens() & st_counter < facet_number)
              {
                string = st.nextToken();
                
                if(!string.equals("#"))
                {
                  mat_name      [st_counter] = string;
                  mech_load_type[st_counter] = st.nextToken();
                  heat_load_type[st_counter] = st.nextToken();
                  st_counter++;
                
                  System.out.println("Facet # "+st_counter+": "+mat_name[st_counter-1]+" "+mech_load_type[st_counter-1]+" "+heat_load_type[st_counter-1]);
                }
              }
            }
            
            br.close();
            
            st_counter = 0;
            
            // Reading of name of file with properties of tanks
            tanks_file_name = task_properties.getProperty("tanks_file");
            
            br = new BufferedReader(new FileReader(tanks_file_name));
            
            while(br.ready())
            {
              string = br.readLine();
              st = new StringTokenizer(string);
                
              if(st.hasMoreTokens() & st_counter < tank_number)
              {
                string = st.nextToken();
                
                if(!string.equals("#"))
                {
                  mat_name      [facet_number + st_counter] = string;
                  geom_param = st.nextToken();
                  geom_param = st.nextToken();
                  geom_param = st.nextToken();
                  geom_param = st.nextToken();
                  geom_param = st.nextToken();
                  geom_param = st.nextToken();
                  mech_load_type[facet_number + st_counter] = st.nextToken();
                  heat_load_type[facet_number + st_counter] = st.nextToken();
                  st_counter++;
                
                  System.out.println("Tank  # "+st_counter+": "+mat_name[facet_number + st_counter-1]+" "+mech_load_type[facet_number + st_counter-1]+" "+
                                     heat_load_type[facet_number + st_counter-1]);
                }
              }
            }
            
            System.out.println("================+++++++++++++++===============");
            
            br.close();
        } 
        catch (IOException io_ex) 
        {
            System.out.println("ERROR: " + io_ex);
        }
    }
    
    /** The method loads information about sizes of cell,
     * sizes and physical properties of specimen.
     * @param file_name name of file keeping all the information about specimen.
     */
    private void loadStructure(String file_name)
    {
        try
        {
            task_properties = new Properties();
            FileInputStream fin = new FileInputStream(file_name);
            task_properties.load(fin);
            fin.close();

            // Reading of data about geometry of specimen
            grains_file               =              task_properties.getProperty("grains_file");

            specimen_size_X           = (new Float  (task_properties.getProperty("specimen_size_X"))).floatValue();
            specimen_size_Y           = (new Float  (task_properties.getProperty("specimen_size_Y"))).floatValue();
            specimen_size_Z           = (new Float  (task_properties.getProperty("specimen_size_Z"))).floatValue();

            cell_number_X             = (new Integer(task_properties.getProperty("cell_number_X"))).intValue();
            cell_number_Y             = (new Integer(task_properties.getProperty("cell_number_Y"))).intValue();
            cell_number_Z             = (new Integer(task_properties.getProperty("cell_number_Z"))).intValue();

            packing_type              = (new Byte   (task_properties.getProperty("packing_type"))).byteValue();
            
            // Minimal number of neighbour cells in adjacent grain necessary for cell transition to this adjacent grain
            min_neighbours_number     = (new Integer(task_properties.getProperty("min_neighbours_number"))).intValue();
            
            System.out.println();
            System.out.println("Name of read file with information about specimen structure: "+file_name);
        }
        catch(IOException io_ex) 
        {
            System.out.println("ERROR: "+io_ex);
        }       

        if(packing_type == HEXAGONAL_CLOSE_PACKING)
        {
            neighb1S_number = 12;
            neighb3D_number = 54;
            System.out.println("Type of packing of cells: hexagonal close packing.");
        }

        if(packing_type == SIMPLE_CUBIC_PACKING)
        {
            neighb1S_number =  6;
            neighb3D_number = 26;
            System.out.println("Type of packing of cells: simple cubic packing.");
        }
        
        // TEST
        System.out.println("Number of cells at 1st coordination sphere:  "+neighb1S_number);
        System.out.println("Number of cells at 1-3 coordination spheres: "+neighb3D_number);
        System.out.println();
        System.out.println("min_neighbours_number  = "+min_neighbours_number);
    }
    
    /** The method creates grains, each of which has parameters 
     * read from file.
     */
    private void createGrains()
    {
        /* Block 5 */
        String grainParameters = new String("");
        String first_token;
        StringTokenizer st;
        
        // Orientation angles of grain crystal lattice
        float grainAngle1;
        float grainAngle2;
        float grainAngle3;
        
        // Name of material of grain
        String material_name;
        
        // Density of dislocations in grain
        float grainDislDensity;
        
        // Average density of dislocations in grain
        float grainAverageDislDensity;
        
        // Deviation of dislocation density in grain
        float grainDislDensityDeviation;
        
        // Type of grain: 0 -"grain" of interacting outer boundary cells,
        //                1 - grain of inner cells,
        //                2 -"grain" of adiabatic outer boundary cells
        byte grain_type;
        
        // Type of grain according to its role in recrystallization process:
        // 0 - initial grain; 1 - new grain; 2 - twinning grain.
        byte grain_rec_type;
        
        // Index of grain, from which current twinning grain grows
        int root_grain_index;
        
        // grain index
        int grain_index;
        
        // number of grains
        grain_number = 0;
        
        // Maximal number of possible grains is equal to the total number of cells
        int max_grain_number = (cell_number_X*cell_number_Y*cell_number_Z)*4;// /100; // 
        
        // initial number of grains
        init_grain_number = 0;
            
        try
        {
            // New buffering character-input stream
            BufferedReader br = new BufferedReader(new FileReader(grains_file));
            
            while(br.ready())
            {
                grainParameters = br.readLine();
                st = new StringTokenizer(grainParameters);
                
                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                      grain_number++;
                }
            }
            
            init_grain_number = grain_number;
            grain_number = Math.max(init_grain_number, max_grain_number);
            
            br.close();
            
            grain_indices              = new int   [grain_number];
            materials                  = new String[grain_number];
            grain_angles               = new float [grain_number][3];
            grainDislDensities         = new float [grain_number];
            grainAverageDislDensities  = new float [grain_number];
            grainDislDensityDeviations = new float [grain_number];
            grain_types                = new byte  [grain_number];
            grain_rec_types            = new byte  [grain_number];
            root_grain_indices         = new int   [grain_number];
            
            // New buffering character-input stream
            br = new BufferedReader(new FileReader(grains_file));
            
            System.out.println("Name of read file with information about grain properties: "+grains_file);
            
            while(br.ready())
            {
                grainParameters = br.readLine();
                st = new StringTokenizer(grainParameters);
                
                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                    {
                        // Reading of grain parameters from file
                        grain_index                  = (new Integer(first_token)).intValue();
                        material_name                = st.nextToken();
                        grainAngle1                  = (new Float  (st.nextToken())).floatValue();
                        grainAngle2                  = (new Float  (st.nextToken())).floatValue();
                        grainAngle3                  = (new Float  (st.nextToken())).floatValue();
                        grainDislDensity             = (new Float  (st.nextToken())).floatValue();
                        grainAverageDislDensity      = (new Float  (st.nextToken())).floatValue();
                        grainDislDensityDeviation    = (new Float  (st.nextToken())).floatValue();
                        grain_type                   = (new Byte   (st.nextToken())).byteValue();
                        grain_rec_type               = (new Byte   (st.nextToken())).byteValue();
                        root_grain_index             = (new Integer(st.nextToken())).intValue();
                        
                        grain_indices[grain_index - 1]              = grain_index;
                        materials[grain_index - 1]                  = material_name;
                        grain_angles[grain_index - 1][0]            = grainAngle1;
                        grain_angles[grain_index - 1][1]            = grainAngle2;
                        grain_angles[grain_index - 1][2]            = grainAngle3;
                        grainDislDensities[grain_index - 1]         = grainDislDensity;
                        grainAverageDislDensities[grain_index - 1]  = grainAverageDislDensity;
                        grainDislDensityDeviations[grain_index - 1] = grainDislDensityDeviation;
                        grain_types[grain_index - 1]                = grain_type;
                        grain_rec_types[grain_index - 1]            = grain_rec_type;
                        root_grain_indices[grain_index - 1]         = root_grain_index;
                        
                        // TEST
                        System.out.println("Grain # "+grain_index+": grain_type = "+grain_type);
                    }
                }
            }
            
            System.out.println();
            
            for(int gr_counter = init_grain_number; gr_counter < grain_number; gr_counter++)
            {
                grain_indices[gr_counter]              = gr_counter + 1;
                materials[gr_counter]                  = materials[0];
                grain_angles[gr_counter][0]            = 0;
                grain_angles[gr_counter][1]            = 0;
                grain_angles[gr_counter][2]            = 0;
                grainDislDensities[gr_counter]         = 0;
                grainAverageDislDensities[gr_counter]  = 0;
                grainDislDensityDeviations[gr_counter] = 0;
                grain_types[gr_counter]                = UICommon.INNER_CELL;
                grain_rec_types[gr_counter]            = UICommon.NEW_GRAIN;
                root_grain_indices[gr_counter]         = 0;
            }
            
            br.close();            
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        
        System.out.println("Initial number of grains: "+init_grain_number);
        System.out.println("Maximal number of grains: "+grain_number);
        System.out.println();
    }
    
    /** The method reads values of physical parameters of each cellular automaton from file.
     * @param file_name name of file containing information about values 
     *        of physical parameters of each cellular automaton
     */
    private void readInitCond(String file_name)
    {      
        cell_number = 0;
        int cell_index  = -1;
        
        String string = "";
        StringTokenizer st;
        String first_token;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file_name));
            
            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                      cell_number++;
                }
            }
            
            br.close();
            
            br = new BufferedReader(new FileReader(file_name));
            System.out.println("Name of read init_cond_file:    "+file_name);
            
            initialThermalEnergies = new float[cell_number];
            initialMechEnergies    = new float[cell_number];
            initialTemperatures    = new float[cell_number];
            grainIndices           = new int  [cell_number];
            cellTypes              = new byte [cell_number];
            initialTorsEnergies    = new float[cell_number];
            
            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                    {
                        cell_index++;
                        
                        // Reading of physical parameters of current cell
                        initialThermalEnergies[cell_index] = (new Float(first_token).floatValue());
                        initialMechEnergies   [cell_index] = (new Float(st.nextToken()).floatValue());
                        initialTemperatures   [cell_index] = (new Float(st.nextToken()).floatValue());
                        grainIndices          [cell_index] = (new Integer(st.nextToken()).intValue());
                        cellTypes             [cell_index] = (new Byte(st.nextToken()).byteValue());
                        
                        if(st.hasMoreTokens())
                          initialTorsEnergies[cell_index]  = (new Float(st.nextToken()).floatValue());
                        else
                          initialTorsEnergies[cell_index]  = (float)0.0;
                        
                      //  System.out.println("T["+cell_index+"] = "+initialTemperatures[cell_index]);
                    }
                }
            }

            System.out.println("Number of cells: "+cell_number+" = "+cell_number_X+"*"+cell_number_Y+"*"+cell_number_Z+
                               " = "+cell_number_X*cell_number_Y*cell_number_Z);
            
            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
       
    /** The method reads values of parameters of external boundary cells from file.
     * @param file_name name of file containing values of parameters of external boundary cells
     */
    private void readBoundCond(String file_name)
    {
        String string = "";
        bound_cell_number = 0;
        int bound_cell_index  = -1;
        
        StringTokenizer st;
        String first_token;
        
        try
        {  
            BufferedReader br = new BufferedReader(new FileReader(file_name));
            
            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                      bound_cell_number++;
                }
            }
            
            br.close();
            
            bound_cell_indices             = new int  [bound_cell_number];
            bound_cell_min_mech_energies   = new float[bound_cell_number];
            bound_cell_max_mech_energies   = new float[bound_cell_number];
            bound_cell_min_heat_energies   = new float[bound_cell_number];
            bound_cell_heat_energies       = new float[bound_cell_number];
            
            bound_cell_time_function_types = new byte [bound_cell_number];
            bound_cell_load_time_portions  = new float[bound_cell_number];
            bound_cell_relax_time_portions = new float[bound_cell_number];
            
            br = new BufferedReader(new FileReader(file_name));
            
            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                    {
                        bound_cell_index++;
                        
                        // Reading of single index of external boundary cell from the file
                        bound_cell_indices[bound_cell_index]       = new Integer(first_token).intValue();
                    
                        // Reading of the values of thermal energy, mechanical energy 
                        // and temperature of external boundary cell from the file
                        bound_cell_min_mech_energies[bound_cell_index] = new Float(st.nextToken()).floatValue();
                        bound_cell_max_mech_energies[bound_cell_index] = new Float(st.nextToken()).floatValue();
                        bound_cell_min_heat_energies[bound_cell_index] = new Float(st.nextToken()).floatValue();
                        bound_cell_heat_energies[bound_cell_index]     = new Float(st.nextToken()).floatValue();
                        
                        bound_cell_time_function_types[bound_cell_index] = new Byte (st.nextToken()).byteValue();
                        bound_cell_load_time_portions[bound_cell_index]  = new Float(st.nextToken()).floatValue();
                        bound_cell_relax_time_portions[bound_cell_index] = new Float(st.nextToken()).floatValue();
                    }
                }
            }
            
          //  System.out.println("Bound cell # 5781: T = "+bound_cell_heat_energies[5781]);
            
            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        
        System.out.println("Number of boundary cells: "+bound_cell_number);
    }
    
    /** The method sets of properties of cells according to its materials.
     */
    private void setCellMaterialParams()
    {
        Properties material_properties = new Properties();
        FileInputStream fin;
        
        mod_elast = new float[grain_number];
        density = new float[grain_number];
        heatExpansionCoeff = new float[grain_number];
        yieldStrain = new float[grain_number];
        ultimateStrain = new float[grain_number];
        energy_coeff = new float[grain_number];
        specific_heat_capacity = new float[grain_number];
        thermal_conductivity = new float[grain_number];
        thermal_conduct_bound = new float[grain_number];
        phonon_portion = new float[grain_number];
        lowTemperThreshold = new float[grain_number];
        highTemperThreshold = new float[grain_number];
        actEnergy = new float[grain_number];
        angleLimitHAGB = new float[grain_number];
        energyHAGB = new float[grain_number];
        maxMobility = new float[grain_number];
        dislMaxMobility = new float[grain_number];
        mechMaxMobility = new float[grain_number];
        mod_shear = new float[grain_number];
        lattice_parameter = new float[grain_number];
        disl_distr_coeff = new float[grain_number];
        yield_state_coeff = new float[grain_number];
        ultimate_state_coeff = new float[grain_number];
        part_vol_fraction = new float[grain_number];
        part_radius = new float[grain_number];
        molar_mass = new float[grain_number];
        threshold_stress = new float[grain_number];
        torsion_energy_coeff = new float[grain_number];
        torsion_energy_coeff_gr_bound = new float[grain_number];
        torsion_energy_coeff_gb_region = new float[grain_number];
        
        // the low temperature of recrystallization (generation of new grains)
        low_tempr_recryst   = new float[grain_number];
        
        // the high temperature of recrystallization (generation of new grains)
        high_tempr_recryst  = new float[grain_number];
        
        // the low temperature of twinning
        low_tempr_twinning  = new float[grain_number];
        
        // the high temperature of twinning
        high_tempr_twinning = new float[grain_number];
        
        // the maximal probability of recrystallization (generation of new grains)
        max_prob_recryst    = new float[grain_number];
        
        // the maximal probability of twinning
        max_prob_twinning   = new float[grain_number];
        
        // minimal density of dislocations
        min_disl_density    = new float[grain_number];
        
        // Lengths of lattice vectors for grains
        lattice_vector_A_lengths = new float[grain_number];
        lattice_vector_B_lengths = new float[grain_number];
        lattice_vector_C_lengths = new float[grain_number];
    
        // Angles between lattice vectors for grains
        lattice_angles_vecA_vecB = new float[grain_number];
        lattice_angles_vecB_vecC = new float[grain_number];
        lattice_angles_vecC_vecA = new float[grain_number];

        // Coefficients of lattice anisotropy for grains
        lattice_anis_coeff       = new float[grain_number];
        
        try
        {
            for (int grain_counter = 0; grain_counter < grain_number; grain_counter++)
            {
              fin = new FileInputStream(MATERIALS_PATH+"/"+materials[grain_counter]+"."+MATERIAL_EXTENSION);
              material_properties.load(fin);
              
              // Reading of data about materials.  
              
              // System.out.println("./task/"+material_file+".txt");
              // System.out.println("material = "+material_file+" state = "+material_state);
              // System.out.println("material_properties= "+material_properties);
              mod_elast[grain_counter]               = (new Float(material_properties.getProperty(UICommon.MOD_ELAST_PROPERTIES))).floatValue();
              density[grain_counter]                 = (new Float(material_properties.getProperty(UICommon.DENSITY_PROPERTIES))).floatValue();
              heatExpansionCoeff[grain_counter]      = (new Float(material_properties.getProperty(UICommon.HEAT_EXPANSION_COEFF_PROPERTIES))).floatValue();
              yieldStrain[grain_counter]             = (new Float(material_properties.getProperty(UICommon.YIELD_STRAIN_PROPERTIES))).floatValue();
              ultimateStrain[grain_counter]          = (new Float(material_properties.getProperty(UICommon.ULTIMATE_STRAIN_PROPERTIES))).floatValue();
              energy_coeff[grain_counter]            = (new Float(material_properties.getProperty(UICommon.ENERGY_COEFF_PROPERTIES))).floatValue();
              specific_heat_capacity[grain_counter]  = (new Float(material_properties.getProperty(UICommon.SPECIFIC_HEAT_CAPACITY_PROPERTIES))).floatValue();
              thermal_conductivity[grain_counter]    = (new Float(material_properties.getProperty(UICommon.THERMAL_CONDUCTIVITY_PROPERTIES))).floatValue();
              thermal_conduct_bound[grain_counter]   = (new Float(material_properties.getProperty(UICommon.THERMAL_CONDUCT_BOUND_PROPERTIES))).floatValue();
              phonon_portion[grain_counter]          = (new Float(material_properties.getProperty(UICommon.PHONON_PORTION_PROPERTIES))).floatValue();
              lowTemperThreshold[grain_counter]      = (new Float(material_properties.getProperty(UICommon.LOW_TEMPER_THR_VALUE_PROPERTIES))).floatValue();
              highTemperThreshold[grain_counter]     = (new Float(material_properties.getProperty(UICommon.HIGH_TEMPER_THR_VALUE_PROPERTIES))).floatValue();
              actEnergy[grain_counter]               = (new Float(material_properties.getProperty(UICommon.ACT_ENERGY_PROPERTIES))).floatValue();
              angleLimitHAGB[grain_counter]          = (new Float(material_properties.getProperty(UICommon.ANGLE_LIMIT_HAGB_PROPERTIES))).floatValue();
              energyHAGB[grain_counter]              = (new Float(material_properties.getProperty(UICommon.ENERGY_HAGB_PROPERTIES))).floatValue();
              maxMobility[grain_counter]             = (new Float(material_properties.getProperty(UICommon.MAX_MOBILITY_PROPERTIES))).floatValue();
              dislMaxMobility[grain_counter]         = (new Float(material_properties.getProperty(UICommon.DISL_MAX_MOBILITY_PROPERTIES))).floatValue();
              mechMaxMobility[grain_counter]         = (new Float(material_properties.getProperty(UICommon.MECH_MAX_MOBILITY_PROPERTIES))).floatValue();
              mod_shear[grain_counter]               = (new Float(material_properties.getProperty(UICommon.MOD_SHEAR_PROPERTIES))).floatValue();
              lattice_parameter[grain_counter]       = (new Float(material_properties.getProperty(UICommon.LATTICE_PARAMETER_PROPERTIES))).floatValue();
              disl_distr_coeff[grain_counter]        = (new Float(material_properties.getProperty(UICommon.DISL_DISTR_COEFF_PROPERTIES))).floatValue();
              yield_state_coeff[grain_counter]       = (new Float(material_properties.getProperty(UICommon.YIELD_STATE_COEFF_PROPERTIES))).floatValue();
              ultimate_state_coeff[grain_counter]    = (new Float(material_properties.getProperty(UICommon.ULTIMATE_STATE_COEFF_PROPERTIES))).floatValue();
              part_vol_fraction[grain_counter]       = (new Float(material_properties.getProperty(UICommon.PART_VOL_FRACTION_PROPERTIES))).floatValue();
           // part_surf_energy[grain_counter]        = (new Float(material_properties.getProperty("part_surf_energy"))).doubleValue();
              part_radius[grain_counter]             = (new Float(material_properties.getProperty(UICommon.PART_RADIUS_PROPERTIES))).floatValue();
              molar_mass[grain_counter]              = (new Float(material_properties.getProperty(UICommon.MOLAR_MASS_PROPERTIES))).floatValue();
              threshold_stress[grain_counter]        = (new Float(material_properties.getProperty(UICommon.THRESHOLD_STRESS_PROPERTIES))).floatValue();
              torsion_energy_coeff[grain_counter]    = (new Float(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_PROPERTIES))).floatValue();
              torsion_energy_coeff_gr_bound[grain_counter]  = (new Float(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_GB_1_PROPERTIES))).floatValue();         
              torsion_energy_coeff_gb_region[grain_counter] = (new Float(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_GB_2_PROPERTIES))).floatValue();
              
              // the low temperature of recrystallization (generation of new grains)
              low_tempr_recryst[grain_counter]       = lowTemperThreshold[grain_counter];
              
              // the high temperature of recrystallization (generation of new grains)
              high_tempr_recryst[grain_counter]      = highTemperThreshold[grain_counter];
              
              // the low temperature of twinning
              low_tempr_twinning[grain_counter]      = (new Float(material_properties.getProperty(UICommon.MIN_TWIN_TEMPERATURE_NAME))).floatValue();
              
              // the high temperature of twinning
              high_tempr_twinning[grain_counter]     = (new Float(material_properties.getProperty(UICommon.TWINNING_TEMPERATURE_NAME))).floatValue();
              
              // the maximal probability of recrystallization (generation of new grains)
              max_prob_recryst[grain_counter]        = (new Float(material_properties.getProperty(UICommon.MAXIMAL_PROB_RECRYST_NAME))).floatValue();
        
              // the maximal probability of twinning
              max_prob_twinning[grain_counter]       = (new Float(material_properties.getProperty(UICommon.MAXIMAL_PROB_TWINNING_NAME))).floatValue();
              
              // minimal density of dislocations
              min_disl_density[grain_counter]        = (new Float(material_properties.getProperty(UICommon.MINIMAL_DISL_DENSITY_NAME))).floatValue();
        
              // Lengths of lattice vectors for grains
              lattice_vector_A_lengths[grain_counter] = (new Float(material_properties.getProperty(UICommon.LATTICE_VECTOR_A_LENGTH_PROPERTIES))).floatValue();
              lattice_vector_B_lengths[grain_counter] = (new Float(material_properties.getProperty(UICommon.LATTICE_VECTOR_B_LENGTH_PROPERTIES))).floatValue();
              lattice_vector_C_lengths[grain_counter] = (new Float(material_properties.getProperty(UICommon.LATTICE_VECTOR_C_LENGTH_PROPERTIES))).floatValue();
              
              // Angles between lattice vectors for grains
              lattice_angles_vecA_vecB[grain_counter] = (new Float(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_A_VEC_B_PROPERTIES))).floatValue();
              lattice_angles_vecB_vecC[grain_counter] = (new Float(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_B_VEC_C_PROPERTIES))).floatValue();
              lattice_angles_vecC_vecA[grain_counter] = (new Float(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_C_VEC_A_PROPERTIES))).floatValue();
              
              // Coefficients of lattice anisotropy for grains
              lattice_anis_coeff      [grain_counter] = (new Float(material_properties.getProperty(UICommon.LATTICE_ANISOTROPY_COEFF_PROPERTIES))).floatValue();
              
              fin.close();
            }
            
            // System.out.println("Name of read file with material properties: "+Common.MATERIALS_PATH+"/"+material_file+"."+Common.MATERIAL_EXTENSION);
        }
        catch(IOException io_ex) 
        {
            System.out.println("ERROR: "+io_ex);
        }
    }
}
