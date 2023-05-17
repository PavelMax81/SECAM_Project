package cellcore;
import java.util.*;
import java.io.*;
import calcmath.*;
import recGeometry.*;
import util.*;
import grainsCore.*;
import JSci.maths.matrices.*;
// import javax.swing.JTextArea;

/*
 * @(#)SpecimenR3_HCP.java version 1.1.3.3;       Apr 2009
 *
 * Copyright (c) 2002-2009 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for information about specimen3D for simulation of recrystallization
 *
 *=============================================================
 * Last changes :
 *      11 February 2009 by Pavel V. Maksimov
 *         (rules of joining of cells to grains are changed (method "calcRecrystallization()");
 *          list of grain indices of cells is replaces by array of grain indices);
 *      19 February 2009 by Pavel V. Maksimov
 *         (rules of joining of cells to grains are changed in method "calcRecrystallization()":
 *          cell can transit to adjacent grain containing more than 2 neighbours at 1st sphere
 *          even if this number is less than number of such neighbours in grain containing this cell;
 *          heat exchange is not simulated when thermal equilibrium is reached:
 *          in current version it is reached at time step #50000);
 *      02 April 2009 by Pavel V. Maksimov
 *         (determination of type of cell location in grain: internal or external);
 *      03 April 2009 by Pavel V. Maksimov
 *         (accurate calculation of grain thermal energy);
 *      08 April 2009 by Pavel V. Maksimov 
 *         (taking into account of grain elastic energy concerning energy of dislocations
 *          when switch probability is calculated);
 *      09 April 2009 by Pavel V. Maksimov
 *         (change of formula for calculation of grain boundary mobility: difference of temperature 
 *          on grain boundary stands instead of current temperature of grain boundary);
 *      28 April 2011 by Pavel Maksimov
           (creation of methods for calculation of displacement vectors, strain tensor and shear components);
 *      29 April 2011 by Pavel Maksimov and Dmitry Moiseenko (method calcHeatFlows_T() should be optimized!!!);
 *      13 February 2015 by Pavel Maksimov (recrystallization is allowed for material in coating).
 *      File version 1.1.3.5
 */

/** Class for info about specimen3D for simulation of recrystallization
 * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.1.3.5 - Feb 2015
 */
public class SpecimenR3_HCP extends ArrayList
{
    /** Field for loading of initial conditions of numerical experiment */
    Properties task_properties;
    
    /** Initial conditions of task */
    InitialConditions initCond;
    
    /** Boundary conditions of task */
    BoundaryConditions boundCond;
    
    /** Name of file with information about grain structure of specimen */
    String grains_file;
    
    /** Material of bulk. */
    String bulk_material;
    
    /** State of bulk. */
    byte bulk_state;
    
    /** Number of automata with inclusion materials. */
    int incl_number;
    
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
    
    /** Volume of cell*/
    double cell_volume;
    
    /** Surface of cell*/
    double cell_surface;
    
    /** Number of cells of specimen3D in direction of X-axis.
     */
    int cellNumberI;
        
    /** Number of cells of specimen3D in direction of Y-axis.
     */
    int cellNumberJ;
                
    /** Number of cells of specimen3D in direction of Z-axis.
     */
    int cellNumberK;

    /** Type of packing of cells in specimen (SCP or HCP) */
    byte packing_type;

    /** Number of neighbour cells at 1st coordination sphere of cell */
    int neighb1S_number;

    /** Number of neighbour cells at 1st-3rd coordination spheres of cell */
    int neighb3D_number;      

    /** Volume fraction of particles */
    double particles_volume_fraction;
    
    /** Radius of particle */
    double particle_radius;
    
    /** Minimal number of neighbour cells in adjacent grain necessary
     * for transition of "central" cell to adjacent grain */
    int min_neighbours_number;

    /** Type of interaction of boundary cells with neighbours */
    byte bound_interaction_type;

    /** Effective rate of response on external action */
    double response_rate;
    
    /** the coefficient for calculation of cell torsion energy change */
    double torsion_energy_coeff;
    
    /** Value of time step */
    double time_step;
    
    /** Number of time steps */
    long step_number;
    
    /** Current time step */
    long step_counter;
    
    /** Number of output files */
    long output_file_number;

    /** This variable is responsible for simulation of heat expansion:
     * if it equals 0 then heat expansion is not simulated,
     * if it equals 1 then heat expansion is simulated.
     */
    byte simulate_heat_expansion;
    
    /** This variable determines whether 
     * the number of defects is taken in account for calculation of mobility.
     */
    protected byte defects_influence;

    /** This variable is responsible for calculation of force moments at each time step:
     * if it equals 0 then force moments are not calculated,
     * if it equals 1 then force moments are calculated.
     */
    byte calc_force_moments;
    
    BufferedWriter bw, bw1, bw2, bw3, bw_total, bw_tors;
    
    BufferedReader br;
    
    /** Sizes of finite element evaluated in corresponding automaton sizes */
    int elementSizeK, elementSizeJ, elementSizeI;
    
    /** List of grains */
    ArrayList grainsCluster;
    
    /** List of initial temperatures of cells */
    ArrayList initTemperatures;

    /** Array of dislocation densities of cellular automata */
    protected double[] disl_densities;
    
    /** 2D array of indices of neighbour cells at 1st coordination sphere:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index
     */
    int[][] neighbours1S;

    /** 2D array of indices of neighbour cells at 1st-3rd coordination spheres:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index
     */
    int[][] neighbours3D;
    
    // Array of single indices of cell neighbours at 1st coordination sphere
    int[] neighbSingleIndices;
    
    // Parameters used in both methods "calcHeatFlows_T" and "calcRecrystallization"
    int index;         // single index of "current" cell
    RecCellR3 cell;    // temporary field corresponding to "current" cell
    Three cell_indices; // triple index of "current" cell
    RecCellR3[] neighbCells; // neighbour automata of the "current" cell
    double angle_diff; // misorientation angle of neighbour cells
    double newTemperature; // New temperature of cell
    
    // Parameters used in method "calcHeatFlows_T" only
    int indexI;      //
    int indexJ;      // Indices of cell
    int indexK;      //    
    double heatCond;
    double heatCap;
    double oldTemperature;
    double heatInflux;    
//    int neighbours_in_adj_grains;    
    RecCellR3 newCell;
    int heat_step_number;
    int recryst_step_number; 
    int mech_step_number;
    int crack_step_number;

 /*
    byte state;
    String cellState;
    String cellIntState;
    String fileName;
    double strain;
    double yieldStrain;
    double probSwitch;
    double rand;
 */
    int[] neighbIndices;
    double[] heatConds;
    double[] heatCaps;
    double[] temprs;
//    double[] neighb_grain_indices;
//  Three[] neighbIndices;  
    ArrayList newCells;
        
    // Parameters used in method "calcRecrystallization" only
    int cellID; // number of grain containing "current" cell
    RecCellR3 neighbCell; // neighbour cell
    Cluster grain;  // temporary field corresponding to grain containing "current" cell
    Cluster neighbGrain; // temporary field corresponding to grain adjacent to "current" grain
    Cluster newGrain;
    Cluster newNeighbGrain;
    int neighbGrainID;    
    double energyHAGB;         // parameter of the material simulated
    double angleHAGB;          // parameter of the material simulated
    double specBoundEnergy;    // specific boundary energy
    double maxMobility;        // parameter of the material simulated
    double dislMaxMobility;    // parameter of the material simulated
    double mechMaxMobility;    // parameter of the material simulated
    double motive_force;       // pressure of the current grain to adjacent one 
    double heat_motive_force;  // pressure of the current grain to adjacent one under heat recrystallization
    double mech_motive_force;  // pressure of the current grain to adjacent one under mechanical recrystallization
    double disl_motive_force;  // pressure of the current grain to adjacent one under cold recrystallization
    double mobility_exponent;  // exponential power in mobility expression 
    double velocity;           // velocity of grain boundary motion
    double temperature;        // cell temperature
    double probSwitch;         // switch probability of cell
    double rand;               // random number to switch
 // double cellElasticEnergy;  // mechanical (elastic) energy of cell
    double grainThermalEnergy; // total thermal energy of grain
    double grain_mech_energy;  // total mechanical energy of grain
 // double grainTemperature;   // temperature of grain
    int grainSize;             // size of grain
    int total_switch_number;   // total number of cell switches
    int period_switch_number;  // number of cell switches in certain time period
    long last_written_step_index = -1; // index of last time step when file with results was created
    
    double[] probs; // Array of probabilities of transition of cell to each of grains
    int[] neighbIndicesInOtherGrains; // array of indices of cells located in adjacent grains
    ArrayList newGrainsCluster;
    int[] newIDOfCells; // array of ID of cells
    
    // Parameters used in method "calcMechEnergyFlows" only
    double oldMechEnergy;
    double[] mech_energies;
 //   ArrayList mechInflux;
    double[] mechInfluxes;
    double[] strains;
    Cluster[] grains;  // Array of grains

    // Basis vectors of crystal lattice of specimen material
    protected VectorR3 basis_vector_X, basis_vector_Y, basis_vector_Z;

    // List of displacement toposes perpendicular to cell boundaries
    ArrayList[] bound_norm_toposes;

    // List of displacement toposes normal to cell boundaries at previous time step
//    ArrayList old_bound_norm_toposes;

    VectorR3 cell_coord = new VectorR3();

    // Coordinates of neighbour cell
    VectorR3 neighb_coord = new VectorR3();

    // Normal vector for boundary of current cell and its neighbour
    // directed from centre of current cell to centre of neighbour
    VectorR3 bound_vect = new VectorR3();

    // Summary displacement vector of current cell
    VectorR3 displ_vector = new VectorR3();

    // Point at centre of boundary facet of current neighbour cells
    PointR3 bound_point;

    // Topos consisting of boundary point and vector of normal displacement of boundary
    ToposR3 bound_norm_topos;

    // Vectors from "cell1S" to "central" cell
    VectorR3[] cell1S_vectors;

    // Array of boolean variables: if cell is damaged
    // then corresponding element equals "true", else - "false".
    boolean[] damaged_cells;

    // Total number of damaged cells
    int total_damaged_cells_number;

    // Variable responsible for account of anisotropy of simulated medium
    boolean anisotropy;
        
    // Vector of anisotropy of simulated medium
    VectorR3 anis_vector;
        
    // Coefficient of anisotropy of simulated medium
    double coeff_anisotropy;
    
    /** Initial dislocation density */
    final double init_disl_density = 1.0E14;
    
    /** List of all cell boundaries */
    ArrayList cell_bounds;
    
    /** List of all neighbour cell pairs */
    ArrayList cell_pairs;
    
    /** Integer, which is larger that number of cells and is equal to 10 in integer power */
    int superior_integer;
    
    /** Array of stresses of cells at previous time step */
    double[] previous_stresses;
    
    /** Array of cell boundary stresses */
    double[][] cell_bound_stresses;
   
    /** Array of cell boundary velocities of deformation */
    double[][] cell_bound_velocities;
       
    /** Array of cell boundary central points */
    PointR3[][] cell_bound_centres;
    
    /** Array of cell boundary normal vectors */
    VectorR3[][] cell_bound_norm_vectors;
    
    /** Array of logical values responsible for recording of parameters of each cell boundary */
    boolean[][] cell_bound_records;
    
    /** Total influx of mechanical energy to specimen */
    double total_mech_energy_influx;
    
    /** Variable responsible for calculation of misorientation angle of grains */
    boolean sum_of_abs_values_of_angles;
    
    /** Array of values of mechanical energy of each cell at previous time step */
    double[] prev_mech_energies;
   
    /** Array of values of mechanical energy of each neighbour cell at previous time step */
    double[][] neighb_prev_mech_energies;
    
    /** Array of values of heat energy of each cell at previous time step */
    double[] prev_heat_energies;
   
    /** Array of values of heat energy of each neighbour cell at previous time step */
    double[][] neighb_prev_heat_energies;
    
    /** Type of loading of specimen boundaries */
    String loading_type;
    
    /** Period of loading (in time steps) for cyclic conditions */
    long load_period;
    
    /** Period of loading absence (in time steps) for cyclic conditions */
    long no_load_period;
    
    /** Total sum of switch probabilities at current time step */
    double total_prob_sum = 0;
        
    /**  Maximal value of switch probability at current time step */
    double max_prob = 0;
    
    /** Counter of switches */
    int switch_counter = 0;
    
    // indices of grains for each cell
    int[] grain_indices;
        
    // Indices of grains containing neighbour cells and current cell
    int[] neighb_grain_indices;
    
    // Total number of cell switches under recrystallization
    int total_switch_counter;
         
    // Total number of grains
    int grain_number;
    
    // Total heat energy influx to inner cells
    double total_inner_heat_influx = 0;
            
    // Total heat energy influx to outer cells
    double total_outer_heat_influx = 0;
    
    // current heat energy influx to inner cells
    double current_inner_heat_influx = 0;
            
    // current heat energy influx to outer cells
    double current_outer_heat_influx = 0;
    
    // Total mechanical energy of interacted cells
    double total_mech_energy = 0;
    
    // List of coordinates of grain embryos
    ArrayList gr_embryo_coord;
    
    // Number of grains generated in the interior of old grains
    int new_grain_number = 0; 
    
    boolean calc_force_mom_new = true; // false; // 
    
    /** The constructor creates specimen3D according to information about sizes of cell,
     * sizes and physical properties of specimen, which is read from the given file.
     * @param specimen_file            name of file with all information about specimen
     * @param init_cond_file           name of file of initial conditions
     * @param bound_cond_file          name of file of boundary conditions
     * @param _time_step               the value of time step
     * @param _total_time              the value of total time of numerical experiment
     * @param _output_file_number      number of output files with information about results at certain time steps
     * @param _simulate_heat_expansion variable switching simulation of heat expansion
     * @param _calc_force_moments      variable switching calculation of force moments
     * @param _response_rate           effective rate of response on external action response_rate     
     * @param _bound_interaction_type   type of interaction of boundary cells with neighbours
     * @param _min_neighb_number        minimal number of neighbour cells in adjacent grain necessary
     *                                 for transition of "central" cell to adjacent grain
     * @param _anisotropy              variable responsible for account of anisotropy of simulated medium
     * @param _anis_vector             vector of anisotropy of simulated medium
     * @param _coeff_anisotropy        the coefficient of anisotropy of simulated medium
     * @param _torsion_energy_coeff    the coefficient for calculation of cell torsion energy change
     */
    public SpecimenR3_HCP(String specimen_file,          String init_cond_file,     String bound_cond_file, 
                          double _time_step,             double _total_time,        long _output_file_number,
                          byte _simulate_heat_expansion, byte _calc_force_moments,  byte _defects_influence,
                          double _response_rate,         byte _bound_interact_type, int _min_neighb_number,   boolean _anisotropy, 
                          VectorR3 _anis_vector,         double _coeff_anisotropy,  double _torsion_energy_coeff)
    {
        /* Block 4 */
        // Loading of information about specimen.
        loadStructure(specimen_file);
        
        time_step               = _time_step;
        step_number             = (long)Math.round(_total_time/time_step);
        output_file_number      = _output_file_number;
        simulate_heat_expansion = _simulate_heat_expansion;
        defects_influence       = _defects_influence;
        calc_force_moments      = _calc_force_moments;
        response_rate           = _response_rate;
        torsion_energy_coeff    = _torsion_energy_coeff;        
        bound_interaction_type  = _bound_interact_type;
        min_neighbours_number   = _min_neighb_number;
        
        // Loading of information about specimen anisotropy
        anisotropy = _anisotropy;
        
        if(anisotropy)
        {
          anis_vector = new VectorR3(_anis_vector);
          coeff_anisotropy = _coeff_anisotropy;
        }
        
        // Creation of list of grains
        grainsCluster = new ArrayList(0);

        // Vectors from "cell1S" to "central" cell
        cell1S_vectors = new VectorR3[neighb1S_number];

        // Initialization of vectors from "cell1S" to "central" cell
        for(int vec_counter = 0; vec_counter<neighb1S_number; vec_counter++)
            cell1S_vectors[vec_counter] = new VectorR3(0, 0, 0);
        
        // Calculation of volume of cell in case of HCP
        calcCellVolume(packing_type);
                
        // Creation of grains: reading of parameters of each grain from the file
        createGrains();

        // Calculation of numbers of cells on directions of X-, Y- and Z-axes.
        cellNumberI = (int)Math.round(specimen_size_X/cell_size_X);
        cellNumberJ = (int)Math.round(specimen_size_Y/cell_size_Y);
        cellNumberK = (int)Math.round(specimen_size_Z/cell_size_Z);

        System.out.println("cellNumberI = " + cellNumberI);
        // text_area.moveCaretPosition(index);

        System.out.println("cellNumberI = " + cellNumberI);
        System.out.println("cellNumberJ = " + cellNumberJ);
        System.out.println("cellNumberK = " + cellNumberK);
        
        System.out.println("defects_influence = "+defects_influence);
        
        StringTokenizer st;
        
        // Index of grain
        int grainID;
        
        // Reading of information about bulk material and bulk state.
        try
        {  
            br = new BufferedReader(new FileReader(specimen_file));
            
            System.out.println("Name of read file:    "+specimen_file);
            
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
        Three indices;
        int cell_index;
        
        RecCellR3 incl_cell;
        Three incl_cell_indices;
        
        String incl_cell_material;
        byte incl_cell_state;
        String incl_cell_string;
        int incl_cell_index;

        // Single indices of cells at 1st coordinational sphere
        neighbours1S = new int[cellNumberI*cellNumberJ*cellNumberK][neighb1S_number];

        // Single indices of cells at 1st-3rd coordinational spheres
        neighbours3D = new int[cellNumberI*cellNumberJ*cellNumberK][neighb3D_number];
        
        // threshold stress for phase transition of cell material
     //   double threshold_stress = 1.0E100;

        /* Block 6 */
        // Creation of specimen as a net of CA. 
        for (int k_index = 0; k_index < cellNumberK; k_index++)
        { 
          System.out.println("Starting creation of "+k_index+" plane...");
          
          for (int j_index = 0; j_index < cellNumberJ; j_index++)
          {
            for (int i_index = 0; i_index < cellNumberI; i_index++)
            {
                // Creation of triple index of cell3D.
                indices = new Three (i_index, j_index, k_index);
                
                cell_index = i_index + j_index*cellNumberI + 
                             k_index*cellNumberI*cellNumberJ;
                
                // Creation of the new cell3D.
                defCellR3 = new RecCellR3(cell_size_X, cell_size_Y, cell_size_Z, 
                                          indices, bulk_material, packing_type);

                // Setting of indices of neighbours at 1st coordination sphere
                setNeighbours1S(cell_index);

                // Setting of indices of neighbours at 1st, 2nd and 3rd coordination spheres in case of SCP
                if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                    setNeighbours3D_SCP(cell_index);

                // Setting of indices of neighbours at 1st, 2nd and 3rd coordination spheres in case of HCP
                if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                    setNeighbours3D_HCP(cell_index);

                // Calculation and setting of cell coordinates
                defCellR3.setCoordinates(calcCoordinates(i_index, j_index, k_index));
                
                // Setting of torsion energy coefficient of current cell
             //   defCellR3.setTorsionEnergyCoeff(torsion_energy_coeff);
                
                // Setting of threshold stress for phase transition of cell material
             //   defCellR3.setThresholdStress(threshold_stress);
             
             //   defCellR3.setNeighbourIndicesHCP(neighbours1S[cell_index]);
                
                // Addition of the new cell3D to the specimen3D.
                add(defCellR3);
            } 
          }
          
          System.out.println("Finished creation of "+k_index+" plane!");
        }
        
        System.out.println("Specimen size= "+this.size());
        
        // Creation of list of all cell boundaries
        cell_pairs = new ArrayList(0);
        cell_bounds = new ArrayList(0);
        
        // Cell boundary
     //   CellBoundary cell_bound;
        
        // Neighbour cell
        RecCellR3 neighb_cell;
        
        // Cell coordinates
        VectorR3 cell_coord;
        
        int neighb1S_index;
        int pair_integer;
        int pair_integer_2;
        
        // Integer, which is larger than number of cells and is equal to 10 in integer power
        superior_integer = (new Double(10000*Math.floor(Math.log10(this.size())))).intValue();
        
        // Centre of cell boundary
        PointR3 bound_centre;
        
        // Normal vector of cell boundary
        VectorR3 bound_norm_vector;
        
        // Array of centres of cell boundaries with neighbours
        PointR3[] bound_centres = new PointR3[neighb1S_number];
        
        // Array of centres of normal vectors of cell boundaries with neighbours
        VectorR3[] bound_norm_vectors = new VectorR3[neighb1S_number];
        
        // Array of cell boundary stresses
        cell_bound_stresses = new double[size()][neighb1S_number];
        
        // Array of cell boundary velocities of deformation
        cell_bound_velocities = new double[size()][neighb1S_number];
        
        // Array of cell boundary central points
        cell_bound_centres = new PointR3[size()][neighb1S_number];
        
        // Array of cell boundary normal vectors
        cell_bound_norm_vectors = new VectorR3[size()][neighb1S_number];
        
        for(cell_index = 0; cell_index<size(); cell_index++)
        {
          for(int neighb_index = 0; neighb_index<neighb1S_number; neighb_index++)
          {
            neighb1S_index = neighbours1S[cell_index][neighb_index];
            
            if(neighb1S_index > -1)
            {
                
            //    pair_integer = cell_index*superior_integer + neighb1S_index;
              //  pair_integer_2 = neighb1S_index*superior_integer + cell_index;
                
            //    if(!cell_pairs.contains(pair_integer) & !cell_pairs.contains(pair_integer_2))
                {
                    cell = (RecCellR3)get(cell_index);
                    neighb_cell = (RecCellR3)get(neighb1S_index);
                    
                    cell_coord = cell.getCoordinates();
                    neighb_coord = neighb_cell.getCoordinates();
                            
                    bound_centre = new PointR3(0.5*(cell_coord.getX() + neighb_coord.getX()),
                                               0.5*(cell_coord.getY() + neighb_coord.getY()),
                                               0.5*(cell_coord.getZ() + neighb_coord.getZ()));

                    bound_centres[neighb_index] = bound_centre;
                    
                    bound_norm_vector = residial(neighb_coord, cell_coord);                    
                    bound_norm_vector.normalize();
                    
                    bound_norm_vectors[neighb_index] = bound_norm_vector;
                    
                    // TEST
          //          System.out.println(cell_index+" "+neighb1S_index+" "+bound_centre.getX()+" "+bound_centre.getY()+" "+bound_centre.getZ());
                    
                  //  cell_bound = new CellBoundary((RecCellR3)get(cell_index), (RecCellR3)get(neighb1S_index), cell_index, neighb1S_index);
                  //  cell_pairs.add(pair_integer);
                  //  cell_bounds.add(cell_bound);
                    
                    // TEST
             //       System.out.println(cell_bounds.size()+" "+cell_index+" "+neighb1S_index);
                    
                    cell_bound_centres[cell_index][neighb_index] = bound_centre;
                    cell_bound_norm_vectors[cell_index][neighb_index] = bound_norm_vector;
                    
                    // Writing of current boundary stress to array of cell boundary stresses
                    cell_bound_stresses[index][neighb_index] = 0;
   
                    // Writing of current boundary velocity to array of cell boundary velocities
                    cell_bound_velocities[index][neighb_index] = 0; 
                }
            }
          }
         
          cell.setBoundCentres(bound_centres);
          cell.setBoundNormVectors(bound_norm_vectors);
        }
        
   //     bound_centres      = cell.getBoundCentres();
   //     bound_norm_vectors = cell.getBoundNormVectors();
        
   //     System.out.println("Number of cell boundaries: "+cell_bounds.size());
        
        // Reading of indices, type of material and state of each cell with inclusion material.
        // Change of properties of corresponding cells.
        try
        {
            br = new BufferedReader(new FileReader(specimen_file));
            
            System.out.println("Name of read file:    "+specimen_file);
            
            String bulk = br.readLine();
            
            for (int string_counter = 0; string_counter < incl_number; string_counter++)
            {
                // String of file with indices, type and state of material of cell with inclusion.
                incl_cell_string = br.readLine();
                
                st = new StringTokenizer(incl_cell_string);                
                
                int incl_cell_index_I = (new Integer(st.nextToken())).intValue();
                int incl_cell_index_J = (new Integer(st.nextToken())).intValue();
                int incl_cell_index_K = (new Integer(st.nextToken())).intValue();
                incl_cell_material = st.nextToken();
                incl_cell_state   = (new Byte(st.nextToken())).byteValue();
                
                System.out.print(incl_cell_index_I+" ");
                System.out.print(incl_cell_index_J+" ");
                System.out.print(incl_cell_index_K+" ");
                System.out.print(incl_cell_material+" ");
                System.out.println(incl_cell_state);
                
                // Change of properties of cells with inclusion material
                incl_cell_indices = new Three(incl_cell_index_I, incl_cell_index_J, incl_cell_index_K);
                incl_cell_index = calcSingleIndex(incl_cell_indices);
                incl_cell = new RecCellR3((RecCellR3)get(incl_cell_index));
              //  incl_cell.setMaterial(incl_cell_material, incl_cell_state);
                
                incl_cell.setState(incl_cell_state);
                set(incl_cell_index, incl_cell);
            }
            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        /* Block 7 */

        // Creation of initial conditions
        initCond  = new InitialConditions(init_cond_file);
  
        // Creation of boundary conditions
        boundCond = new BoundaryConditions(bound_cond_file);

        // TEST
        System.out.println("bound_cond_file: "+bound_cond_file);
    /*
        ArrayList boundaryIndices         = new ArrayList(boundCond.getBoundaryIndices());
        ArrayList boundaryThermalEnergies = new ArrayList(boundCond.getBoundaryThermalEnergies());
        ArrayList boundaryMechEnergies    = new ArrayList(boundCond.getBoundaryMechEnergies());

        int index = 0;
        double heat_param=0;
        double mech_param=0;

        for(int cell_counter = 0; cell_counter<boundaryIndices.size(); cell_counter++)
        {
            index      = ((Integer)boundaryIndices.get(cell_counter)).intValue();
            mech_param = ((Double)boundaryMechEnergies.get(cell_counter)).doubleValue();

            System.out.println("Cell # "+index+". Mech. param = "+mech_param);
        }
     */
        // END OF TEST
        
        // Setting of initial conditions
        setInitialConditions(initCond);

        /* Block 8 */

        // Distribution of 3D cells on clusters/grains
        for(int cellCounter = 0; cellCounter < size(); cellCounter++)
        {
            // Current cell
            defCellR3 = (RecCellR3)get(cellCounter);

      //      if(defCellR3.getInitialTemperature()>0)
      //      System.out.println(defCellR3.getInitialTemperature()+" "+defCellR3.getThermalEnergy());
                    
            // Obtaining of index (ID) of grain containing cell
            grainID = defCellR3.getGrainIndex();
            
            if(grainID>0)
            {
                // Obtaining of grain with this index (ID)
                grain = (Cluster)grainsCluster.get(grainID-1);
            
                // Setting of Euler angles
                defCellR3.setEulerAngles(grain.getAngles());
                
                // Setting of material of cell
                defCellR3.setMaterial(grain.getMaterial());                
                defCellR3.setMaterialFile(grain.getMaterialName());
                
          //      System.out.println("material = "+grain.getMaterialName()+"; current_torsion_energy_coeff = "+defCellR3.getCurrentTorsionEnergyCoeff());
            
                defCellR3.setType(defCellR3.getType());
                
                // Setting of type of grain containing cell
                defCellR3.setGrainType(grain.getType());

                // Setting of density of dislocations of grain containing cell
                defCellR3.setDislDensity(grain.getDislDensity());
                
                // Setting of HAGB energy of cell
                defCellR3.setEnergyHAGB(grain.getEnergyHAGB());
                
                // Calculation of atom number for cell
                defCellR3.calcAtomNumber();
                
                // Calculation of defect number for cell
                defCellR3.calcDefectNumber();
                
                // TEST
                if(cellCounter == size()/100)
                {                    
                    System.out.println("--------=============------------===========---------------=====");
                    System.out.println("cellCounter = "+cellCounter+"; material = "+grain.getMaterialName()+
                                     "; atom number = "+defCellR3.getAtomNumber()+"; defect_number = "+defCellR3.getDefectNumber());
                    System.out.println("--------=============------------===========---------------=====");
                }                
            }
        }

      //  setBoundaryConditions(boundCond, true);

        // Setting of energies of grains and cells
        for(int cellCounter = 0; cellCounter < size(); cellCounter++)
        {
            // Current cell
            defCellR3 = (RecCellR3)get(cellCounter);

      //      if(defCellR3.getInitialTemperature()>0)
      //      System.out.println(defCellR3.getInitialTemperature()+" "+defCellR3.getThermalEnergy());

            // Obtaining of index (ID) of grain containing cell
            grainID = defCellR3.getGrainIndex();

            if(grainID>0)
            {
                // Obtaining of grain with this index (ID)
                grain = (Cluster)grainsCluster.get(grainID-1);

          //      defCellR3.setInitialTemperature(0);

                // Calculation of cell thermal energy, which is changed due to heat expansion
           //     defCellR3.calcThermalEnergy();

         //       defCellR3.setInitialTemperature(defCellR3.getTemperature());

                // Addition of cell single index to list of indices of cells
                //those belong to this grain
                grain.add(cellCounter);

                // Change of grain thermal energy
                grain.addThermalEnergy(defCellR3.getThermalEnergy());

                // Change of grain mechanical energy
                grain.addElasticEnergy(defCellR3.getMechEnergy());

                // Setting of grain with added cell into cluster of grains
            //    grainsCluster.set(grainID-1, grain);
            }
        }

        int cellsInGrains = 0; // total number of cells in grains
        int grainCapacity; // capacity of grain
     //   calcCellVolumeHCP(); // calculation of cell volume
        
        // Average value of dislocation density
        double aver_disl_density = 0;
        
        // Maximal deviation of dislocation density from its average value
        double max_deviation = 0;
        
        // Mean square deviation of dislocation density
        double mean_square_deviation = 0;
        
        // Coefficient for calculation of minimal and maximal values
        double coeff = 0.4;
        double min_value, max_value;
    //    double coeff_1 = 0.7;
    //    double coeff_2 = 0.1;
        double[] cell_distr_values;
           
        System.out.println();
        System.out.println("Determination of dislocation densities for cells will be realized.");
        
        grain_number = grainsCluster.size();
        
        // Determination of dislocation densities for cells
        for (int grainCounter = 0; grainCounter < grain_number; grainCounter++)
        {
          grain = (Cluster)grainsCluster.get(grainCounter);            
          grainCapacity = grain.size();
            
          if(grainCounter < grain_number - 12)
          {
            // Average dislocation density for current grain
            aver_disl_density = grain.getAverageDislDensity();
            
            // Maximal deviation of dislocation density from its average value
            max_deviation = grain.getDislDensityDeviation();
            
            // Mean square deviation of dislocation density for current grain
            mean_square_deviation = Math.min(max_deviation*coeff, aver_disl_density);
            
            if(mean_square_deviation > max_deviation)
                max_deviation = Math.min(mean_square_deviation*coeff, aver_disl_density);
            
            // Minimal and maximal values of dislocation density for cells from current grain
            min_value = aver_disl_density - max_deviation;
            max_value = aver_disl_density + max_deviation;
            
            // Array of stochastic values of dislocation density for all cells in current grain
            cell_distr_values = new double[grainCapacity+2];
            
            // Stochastic determination of dislocation density for each cell from current grain
            cell_distr_values = createStochasticSeries1(min_value, max_value, mean_square_deviation, grainCapacity);
            
        //    System.out.println("\nGrain # "+(grainCounter+1)+" includes "+grainCapacity+" cells.");
                    
            for(int cell_counter = 0; cell_counter < grainCapacity; cell_counter++)
            {
                cell_index = (int)grain.get(cell_counter);
                defCellR3  = (RecCellR3)get(cell_index);
                defCellR3.setDislDensity(cell_distr_values[cell_counter]);
            //    set(cell_index, defCellR3);
                
         //       if(cell_counter % 100 == 0 | cell_counter == grainCapacity - 1)
         //         System.out.println("Cell # "+cell_counter+" with index "+cell_index+" from grain # "+defCellR3.getGrainIndex()+" has dislocation density: "+defCellR3.getDislDensity());
            }
            
        //    System.out.println("Minimal dislocation density: "+min_value);
         //   System.out.println("Maximal dislocation density: "+max_value);
         //   System.out.println("Average dislocation density: "+cell_distr_values[grainCapacity]);
        //    System.out.println("Mean square deviation :      "+cell_distr_values[grainCapacity+1]);
          }
            
          cellsInGrains = cellsInGrains + grainCapacity;
            
          if(grainCapacity > 0)
          System.out.println("Number of cells in grain # "+(grainCounter+1)+": "+grainCapacity+
                             "; thermal energy: "+grain.getThermalEnergy()+
                             "; elastic energy: "+grain.getElasticEnergy()+
                             "; material: " + grain.getMaterialName());//*grainCapacity*getCellVolumeHCP());
        }
        
        System.out.println("Number of cells in all grains: "+cellsInGrains);
    }
    
    /** The constructor creates specimen3D according to sample specimen.
     * @param _specimenR3 sample specimen
     */
    public SpecimenR3_HCP(SpecimenR3_HCP _specimenR3)
    {
        super(0);
        
        // Numbers of cells on directions of X-, Y- and Z-axes.
        cellNumberI = _specimenR3.getCellNumberI();
        cellNumberJ = _specimenR3.getCellNumberJ();
        cellNumberK = _specimenR3.getCellNumberK();
        
        cell_size_X = _specimenR3.get_cell_size_X();
        cell_size_Y = _specimenR3.get_cell_size_Y();
        cell_size_Z = _specimenR3.get_cell_size_Z();
        
        step_number = _specimenR3.getStepNumber();
        time_step   = _specimenR3.getTimeStep();
        output_file_number = _specimenR3.getOutputFileNumber();
        
//        RecCellR3 cellR3;               
//        System.out.println("Specimen copy:");                
        for(int cellCounter=0; cellCounter<_specimenR3.size(); cellCounter++)
        {
  //          cellR3 = new RecCellR3((RecCellR3)_specimenR3.get(cellCounter));
  //          System.out.print(cellCounter+" "+cellR3.getTemperature()+"   ");
            
            add(_specimenR3.get(cellCounter));
        }
//        System.out.println();
        
        setGrainsCluster(_specimenR3.getGrainsCluster());
        initTemperatures          = _specimenR3.getInitTemperatures();

        packing_type              = _specimenR3.getPackingType();
        neighb1S_number           = _specimenR3.getNeighb1SNumber();
        neighb3D_number           = _specimenR3.getNeighb3DNumber();
        
        particles_volume_fraction = _specimenR3.getParticlesVolumeFraction();
        particle_radius           = _specimenR3.getParticleRadius();
        min_neighbours_number     = _specimenR3.getMinNeighboursNumber();
        bound_interaction_type    = _specimenR3.getBoundInteractionType();
        calc_force_moments        = _specimenR3.getCalcForceMomState();
        simulate_heat_expansion   = _specimenR3.getHeatExpState();
        defects_influence         = _specimenR3.getDefectsInfluence();
        response_rate             = _specimenR3.getResponseRate();        
        
        // Loading of information about specimen anisotropy
        anisotropy                = _specimenR3.getAnisPresence();
        
        if(anisotropy)
        {
          anis_vector             = _specimenR3.getAnisVector();
          coeff_anisotropy        = _specimenR3.getAnisCoeff();
        }
        
        setNeighbours1S(_specimenR3.getNeighbours1S());
        setNeighbours3D(_specimenR3.getNeighbours3D());
        boundCond                 = _specimenR3.getBoundaryConditions();

        setBasisVectors(_specimenR3.getBasisVectors());
        cell1S_vectors            = _specimenR3.getCell1SVectors();
        
        cell_bounds               = _specimenR3.getCellBoundaries();
        cell_pairs                = _specimenR3.getCellPairs();
        superior_integer          = _specimenR3.getSuperiorInteger();
    //    torsion_energy_coeff      = _specimenR3.getTorsionEnergyCoeff();
        
        previous_stresses         = _specimenR3.getPreviousStresses();
        cell_bound_stresses       = _specimenR3.getCellBoundStresses();
        cell_bound_velocities     = _specimenR3.getCellBoundVelocities();
        cell_bound_centres        = _specimenR3.getCellBoundCentres();
        cell_bound_norm_vectors   = _specimenR3.getCellBoundNormVectors();
        
        // TEST
    //  System.out.println("The specimen is copied.");
    //  System.out.println("Cell #      0. Initial temperature = "+initTemperatures.get(0));
    //  System.out.println(Nghb. # "+initTemperatures.size()+". Initial temperature = "+initTemperatures.get(initTemperatures.size()-1));
    }
    
    /** The method creates "microspecimen" with certain sizes inside "macrospecimen".
     * @param _specimenR3 "macrospecimen"
     * @param sizeFE_X size of finite element along X-axis.
     * @param sizeFE_Y size of finite element along Y-axis.
     * @param sizeFE_Z size of finite element along Z-axis.
     */
    public SpecimenR3_HCP(SpecimenR3_HCP _specimenR3, double sizeFE_X, double sizeFE_Y, double sizeFE_Z)
    {
        super(0);
        
        // Time step
        time_step   = _specimenR3.getTimeStep();

        // Cell sizes
        cell_size_X = _specimenR3.get_cell_size_X();
        cell_size_Y = _specimenR3.get_cell_size_Y();
        cell_size_Z = _specimenR3.get_cell_size_Z();
        
        // Setting of sizes of finite element evaluated in corresponding automaton sizes
        cellNumberI = (int)Math.round(sizeFE_X/cell_size_X);
        cellNumberJ = (int)Math.round(sizeFE_Y/cell_size_Y);
        cellNumberK = (int)Math.round(sizeFE_Z/cell_size_Z);
        
        initTemperatures          = _specimenR3.getInitTemperatures();

        packing_type              = _specimenR3.getPackingType();
        neighb1S_number           = _specimenR3.getNeighb1SNumber();
        neighb3D_number           = _specimenR3.getNeighb3DNumber();

        particles_volume_fraction = _specimenR3.getParticlesVolumeFraction();
        particle_radius           = _specimenR3.getParticleRadius();
        min_neighbours_number     = _specimenR3.getMinNeighboursNumber();
        bound_interaction_type    = _specimenR3.getBoundInteractionType();
        calc_force_moments        = _specimenR3.getCalcForceMomState();
        simulate_heat_expansion   = _specimenR3.getHeatExpState();
        defects_influence         = _specimenR3.getDefectsInfluence();
        response_rate             = _specimenR3.getResponseRate();        
        
        // Loading of information about specimen anisotropy
        anisotropy                = _specimenR3.getAnisPresence();
        
        if(anisotropy)
        {
          anis_vector             = _specimenR3.getAnisVector();
          coeff_anisotropy        = _specimenR3.getAnisCoeff();
        }
        
        setNeighbours1S(_specimenR3.getNeighbours1S());
        setNeighbours3D(_specimenR3.getNeighbours3D());
        setGrainsCluster(_specimenR3.getGrainsCluster());
        setBasisVectors(_specimenR3.getBasisVectors());
        
        cell1S_vectors            = _specimenR3.getCell1SVectors();
        boundCond                 = _specimenR3.getBoundaryConditions();
        
        cell_bounds               = _specimenR3.getCellBoundaries();
        cell_pairs                = _specimenR3.getCellPairs();
        superior_integer          = _specimenR3.getSuperiorInteger();
//        torsion_energy_coeff      = _specimenR3.getTorsionEnergyCoeff();
        
        previous_stresses         = _specimenR3.getPreviousStresses();
        cell_bound_stresses       = _specimenR3.getCellBoundStresses();
        cell_bound_velocities     = _specimenR3.getCellBoundVelocities();
        cell_bound_centres        = _specimenR3.getCellBoundCentres();
        cell_bound_norm_vectors   = _specimenR3.getCellBoundNormVectors();
    }
    
    /** The method returns list of all cell boundaries.
     * @return list of all cell boundaries
     */
    public ArrayList getCellBoundaries()
    {
        return cell_bounds;        
    }    
    
    /** The method returns list of all pairs of neighbour cells.
     * @return list of all cell boundaries
     */
    public ArrayList getCellPairs()
    {
        return cell_pairs;        
    }
    
    /** The method returns integer, which is larger that number of cells and is equal to 10 in integer power.
     * @return integer, which is larger that number of cells and is equal to 10 in integer power
     */
    public int getSuperiorInteger()
    {        
        return superior_integer;
    }
    
    /** The method returns array of stresses of cells at previous time step.
     * @return array of stresses of cells at previous time step
     */
    public double[] getPreviousStresses()
    {       
        return previous_stresses;
    }
    
    /** The method returns array of cell boundary stresses.
     * @return array of stresses of cell boundary stresses
     */
    public double[][] getCellBoundStresses()
    {
        return cell_bound_stresses;
    }
   
    /** The method returns array of cell boundary velocities of deformation.
     * @return array of stresses of cell boundary velocities of deformation
     */
    public double[][] getCellBoundVelocities()
    {              
        return cell_bound_velocities;
    }
       
    /** The method returns array of cell boundary central points.
     * @return array of stresses of cell boundary central points
     */
    public PointR3[][] getCellBoundCentres()
    {        
        return cell_bound_centres;
    }
    
    /** The method returns array of cell boundary normal vectors.
     * @return array of stresses of cell boundary normal vectors
     */
    public VectorR3[][] getCellBoundNormVectors()
    {        
        return cell_bound_norm_vectors;    
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
        indexI = triple_index.getI();
        indexJ = triple_index.getJ();
        indexK = triple_index.getK();
            
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
    
    /** The method calculates single index of cell3D with certain triple index.
     * @param indexI 1st index of cell3D
     * @param indexJ 2nd index of cell3D
     * @param indexK 3rd index of cell3D
     * @return single index of cell3D.
     */
    public int calcSingleIndex(int indexI, int indexJ, int indexK)
    {
        // Single index of cell3D.
        int single_index = -1;
            
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

    /** The method calculates indices of cells at 1st, 2nd and 3rd coordination spheres of given cell
     * in case of hexagonal close packing of automata.
     * @param _intIndex index of given cell
     * @see neighbours3D
     */
    private void setNeighbours3D_HCP(int _intIndex)
    {   
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
        
        VectorR3 cell_coordinates = new VectorR3();
        VectorR3 neighb_coordinates;
        VectorR3 distance;
        boolean test_distances = false;
        
        if(index1 == cellNumberI/2 & index2 == cellNumberJ/2 & index3 == cellNumberK/2)
        {
            test_distances = true;
            cell_coordinates = calcCoordinates(index1, index2, index3);
            System.out.println();
            System.out.println("Testing of distances between neighbours and central cell for HCP");
        }
        
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
                
                if(test_distances)
                {
                    neighb_coordinates = calcCoordinates(neighbour_indexI, neighbour_indexJ, neighbour_indexK);
                    distance = residial(neighb_coordinates, cell_coordinates);
                    System.out.println("Neighbour # "+neighb_counter+". Distance from central cell: "+distance.getLength());
                }
            }
            else
                neighbours3D[_intIndex][neighb_counter] = GrainsHCPCommon.CELL_OUTSIDE_OF_SPECIMEN;//-3;
        }
        
        if(test_distances)
        {
            System.out.println();
            test_distances = false;
        }
    }

    /** The method returns indices of cubic cells 
     * at 1st-3rd coordination spheres of given cubic cell.
     * @param _intIndex index of given cell
     * @see neighbours3D
     */
    private void setNeighbours3D_SCP(int _intIndex)
    {
        // Triple index of cell
        Three _tripleIndex = calcTripleIndex(_intIndex, cellNumberI, cellNumberJ, cellNumberK);

        // Indices of considered cell
        int index1 = _tripleIndex.getI();
        int index2 = _tripleIndex.getJ();
        int index3 = _tripleIndex.getK();

        // Triple indices of cells at 1st, 2nd and 3rd coordinational spheres
        Three[] neighbours = new Three[26];

        // Triple indices of cubic cells at 1st coordinational sphere
        neighbours[ 0] = new Three(index1  , index2  , index3-1);
        neighbours[ 1] = new Three(index1-1, index2  , index3  );
        neighbours[ 2] = new Three(index1  , index2-1, index3  );
        neighbours[ 3] = new Three(index1  , index2+1, index3  );
        neighbours[ 4] = new Three(index1+1, index2  , index3  );
        neighbours[ 5] = new Three(index1  , index2  , index3+1);

        // Triple indices of cubic cells at 2nd coordinational sphere
        neighbours[ 6] = new Three(index1-1, index2  , index3-1);
        neighbours[ 7] = new Three(index1  , index2-1, index3-1);
        neighbours[ 8] = new Three(index1  , index2+1, index3-1);
        neighbours[ 9] = new Three(index1+1, index2  , index3-1);
        neighbours[10] = new Three(index1-1, index2-1, index3  );
        neighbours[11] = new Three(index1-1, index2+1, index3  );
        neighbours[12] = new Three(index1+1, index2-1, index3  );
        neighbours[13] = new Three(index1+1, index2+1, index3  );
        neighbours[14] = new Three(index1-1, index2  , index3+1);
        neighbours[15] = new Three(index1  , index2-1, index3+1);
        neighbours[16] = new Three(index1  , index2+1, index3+1);
        neighbours[17] = new Three(index1+1, index2  , index3+1);

        // Triple indices of cubic cells at 3rd coordinational sphere
        neighbours[18] = new Three(index1-1, index2-1, index3-1);
        neighbours[19] = new Three(index1-1, index2+1, index3-1);
        neighbours[20] = new Three(index1+1, index2-1, index3-1);
        neighbours[21] = new Three(index1+1, index2+1, index3-1);
        neighbours[22] = new Three(index1-1, index2-1, index3+1);
        neighbours[23] = new Three(index1-1, index2+1, index3+1);
        neighbours[24] = new Three(index1+1, index2-1, index3+1);
        neighbours[25] = new Three(index1+1, index2+1, index3+1);

        int neighbour_indexI;
        int neighbour_indexJ;
        int neighbour_indexK;        
        
        VectorR3 cell_coordinates = new VectorR3();
        VectorR3 neighb_coordinates;
        VectorR3 distance;
        boolean test_distances = false;
        
        if(index1 == cellNumberI/2 & index2 == cellNumberJ/2 & index3 == cellNumberK/2)
        {
            test_distances = true;
            cell_coordinates = calcCoordinates(index1, index2, index3);
            System.out.println();
            System.out.println("Testing of distances between neighbours and central cell for SCP");
        }
        
        // If each triple index of neighbour cell is within boundaries then
        // the single index of the cell is calculated, else
        // this cell is deleted from the array of neighbours (single index = -3).
        for (int neighb_counter = 0; neighb_counter < 26; neighb_counter++)
        {
            neighbour_indexI = neighbours[neighb_counter].getI();
            neighbour_indexJ = neighbours[neighb_counter].getJ();
            neighbour_indexK = neighbours[neighb_counter].getK();

            if((neighbour_indexI > -1)&(neighbour_indexI < cellNumberI)&
               (neighbour_indexJ > -1)&(neighbour_indexJ < cellNumberJ)&
               (neighbour_indexK > -1)&(neighbour_indexK < cellNumberK))
            {
                neighbours3D[_intIndex][neighb_counter] = 
                        neighbour_indexI + neighbour_indexJ*cellNumberI + neighbour_indexK*cellNumberI*cellNumberJ;
                
                if(test_distances)
                {
                    neighb_coordinates = calcCoordinates(neighbour_indexI, neighbour_indexJ, neighbour_indexK);
                    distance = residial(neighb_coordinates, cell_coordinates);
                    System.out.println("Neighbour # "+neighb_counter+". Distance from central cell: "+distance.getLength());
                }
            }
            else
                neighbours3D[_intIndex][neighb_counter] = GrainsHCPCommon.CELL_OUTSIDE_OF_SPECIMEN;//-3;//
        }
        
        if(test_distances)
        {
            System.out.println();
            test_distances = false;
        }
    }

    /** The method returns indices of cells at 1st coordination sphere of given cell.
     * @param _intIndex index of given cell
     * @see neighbours3D
     */
    private void setNeighbours1S(int _intIndex)
    {
        // Triple index of cell
        Three _tripleIndex = calcTripleIndex(_intIndex, cellNumberI, cellNumberJ, cellNumberK);
            
        // Indices of considered cell
        int index1 = _tripleIndex.getI();
        int index2 = _tripleIndex.getJ();
        int index3 = _tripleIndex.getK();
            
        // Triple indices of cells at 1st coordinational sphere
        Three[] neighbours = new Three[neighb1S_number];
        
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
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

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
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
               
            if((neighbour_indexI > -1)&(neighbour_indexI < cellNumberI)&
               (neighbour_indexJ > -1)&(neighbour_indexJ < cellNumberJ)&
               (neighbour_indexK > -1)&(neighbour_indexK < cellNumberK))
            {
                neighbours1S[_intIndex][neighb_counter] = neighbour_indexI + neighbour_indexJ*cellNumberI + 
                                                          neighbour_indexK*cellNumberI*cellNumberJ;
            }
            else
                neighbours1S[_intIndex][neighb_counter] = -3;

        //    System.out.print(neighbours1S[_intIndex][neighb_counter]+" ");
        }

   //     System.out.println();
    }
    
    /** The method returns single indices of neighbours of cell with certain triple index.
     * @param indexI first index of cell
     * @param indexJ second index of cell
     * @param indexK third index of cell
     * @return single indices of neighbours of cell with certain triple index
     */
    public int[] neighbour_indices(int indexI, int indexJ, int indexK)
    {
        cellNumberK = getCellNumberK();
        cellNumberJ = getCellNumberJ();
        cellNumberI = getCellNumberI();
        
        // Triple indices of cell neighbours.
        Three[] tripleNeighbIndices = new Three[neighb1S_number];
        
        tripleNeighbIndices[0] = new Three(indexI>0 ? indexI-1 : 0, indexJ, indexK);   
        tripleNeighbIndices[1] = new Three(indexI<cellNumberI-1 ? indexI+1 : cellNumberI-1, indexJ, indexK);
        tripleNeighbIndices[2] = new Three(indexI, indexJ>0 ? indexJ-1 : 0, indexK);
        tripleNeighbIndices[3] = new Three(indexI, indexJ<cellNumberJ-1 ? indexJ+1 : cellNumberJ-1, indexK);
        tripleNeighbIndices[4] = new Three(indexI, indexJ, indexK>0 ? indexK-1 : 0);
        tripleNeighbIndices[5] = new Three(indexI, indexJ, indexK<cellNumberK-1 ? indexK+1 : cellNumberK-1);
              
        // Single indices of cell neighbours.
        int[] singleNeighbIndices = new int[neighb1S_number];
        
        // Calculation of single indices of cell neighbours.
        for (int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
        {
            singleNeighbIndices[neighbCounter] = calcSingleIndex(tripleNeighbIndices[neighbCounter]);            
        }
                
        // Calculation of single index of neighbouring cell.
        return singleNeighbIndices;
    }
    
    /** The method returns array of single indices of neighbours of cell with certain single index 
     * (in case of hexagonal close packing of cells).
     * If neighbour cell is not located in neighbour grains then its index in the array is replaced by (-1).
     * @param cellIndex single index of cell
     * @return array of single indices of neighbour cells in other grains
     */
    public int[] neighbourIndicesInOtherGrains(int cellIndex)
    {
        // Considered cell
        cell = (RecCellR3)get(cellIndex);
        
        // ID of grain containing considered cell
        cellID = cell.getGrainIndex();
        
        // Array of single indices of cell neighbours at 1st coordination sphere
        neighbSingleIndices = neighbours1S[cellIndex];
        
        // Array of cell neighbours at 1st coordination sphere
        RecCellR3[] neighbCells1S = new RecCellR3[neighb1S_number];
        
        // List of single indices of neighbours in other grains
        int[] neighbCellsInOtherGrains = new int[neighb1S_number];
        
        // Calculation of single indices of cell neighbours in other grains
        for (int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
        if(neighbSingleIndices[neighbCounter] > -1)
        {
            // Obtaining of neighbour cell at 1st coordination sphere
            neighbCells1S[neighbCounter] = (RecCellR3)get(neighbSingleIndices[neighbCounter]);
              
            // Checking of belonging of neighbour cell to adjacent grain
            if((neighbCells1S[neighbCounter].getGrainIndex() != cellID)&
               (neighbCells1S[neighbCounter].getGrainType()==cell.getGrainType()))
                neighbCellsInOtherGrains[neighbCounter] = neighbSingleIndices[neighbCounter];            
            else
                neighbCellsInOtherGrains[neighbCounter] = -1;
              
            // Checking of belonging of neighbour cell to specimen
            if(neighbCells1S[neighbCounter].getGrainType()!=cell.getGrainType())
                neighbCellsInOtherGrains[neighbCounter] = -2;
        }
        else
            neighbCellsInOtherGrains[neighbCounter] = -2;
        
        return neighbCellsInOtherGrains;
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
            grains_file               =             task_properties.getProperty("grains_file");

            specimen_size_X           = (new Double(task_properties.getProperty("specimen_size_X"))).doubleValue();
            specimen_size_Y           = (new Double(task_properties.getProperty("specimen_size_Y"))).doubleValue();
            specimen_size_Z           = (new Double(task_properties.getProperty("specimen_size_Z"))).doubleValue();

            int element_number_X      = (new Integer(task_properties.getProperty("element_number_X"))).intValue();
            int element_number_Y      = (new Integer(task_properties.getProperty("element_number_Y"))).intValue();
            int element_number_Z      = (new Integer(task_properties.getProperty("element_number_Z"))).intValue();

            elementSizeI              = (new Integer(task_properties.getProperty("cell_number_X"))).intValue();
            elementSizeJ              = (new Integer(task_properties.getProperty("cell_number_Y"))).intValue();
            elementSizeK              = (new Integer(task_properties.getProperty("cell_number_Z"))).intValue();

            element_size_X            = specimen_size_X/element_number_X;
            element_size_Y            = specimen_size_Y/element_number_Y;
            element_size_Z            = specimen_size_Z/element_number_Z;

            cell_size_X               = element_size_X/elementSizeI;
            cell_size_Y               = element_size_Y/elementSizeJ;
            cell_size_Z               = element_size_Z/elementSizeK;

            packing_type              = (new Byte(task_properties.getProperty("packing_type"))).byteValue();
            
            particles_volume_fraction = (new Double(task_properties.getProperty("particles_volume_fraction"))).doubleValue();
            particle_radius           = (new Double(task_properties.getProperty("particle_radius"))).doubleValue();
            
            // Type of interaction of boundary cells with neighbours
            bound_interaction_type    = (new Byte(task_properties.getProperty("bound_interaction_type"))).byteValue();
            
            // Minimal number of neighbour cells in adjacent grain necessary for cell transition to this adjacent grain
            min_neighbours_number     = (new Integer(task_properties.getProperty("min_neighbours_number"))).intValue();
            
            // Variable responsible for account of anisotropy of simulated medium        
            Byte anis_presence = (new Byte(task_properties.getProperty("anisotropy"))).byteValue();
            
            if(anis_presence==0)
                anisotropy = false;
            else
                anisotropy = true;
            
            // Vector of anisotropy of simulated medium
            double anis_vector_X = (new Double(task_properties.getProperty("anis_vector_X"))).doubleValue();
            double anis_vector_Y = (new Double(task_properties.getProperty("anis_vector_Y"))).doubleValue();
            double anis_vector_Z = (new Double(task_properties.getProperty("anis_vector_Z"))).doubleValue();
            
            anis_vector = new VectorR3(anis_vector_X, anis_vector_Y, anis_vector_Z);
            
            // Coefficient of anisotropy of simulated medium
            coeff_anisotropy = (new Double(task_properties.getProperty("spec_anis_coeff"))).doubleValue();

            System.out.println();
            System.out.println("Name of read file:    "+file_name);
        }
        catch(IOException io_ex) 
        {
            System.out.println("ERROR: "+io_ex);
        }       

        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
        {
            neighb1S_number = 12;
            neighb3D_number = 54;
            System.out.println("Type of packing of cells: hexagonal close packing.");

            // Basis vectors of crystal lattice of simulated material (HCP)
            basis_vector_X = new VectorR3(1, 0, 0);
            basis_vector_Y = new VectorR3(0.5, Math.sqrt(3.0/4.0), 0);
            basis_vector_Z = new VectorR3(0.5, Math.sqrt(1/12.0), Math.sqrt(2.0/3.0));
        }

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
        {
            neighb1S_number =  6;
            neighb3D_number = 26;
            System.out.println("Type of packing of cells: simple cubic packing.");

            // Basis vectors of crystal lattice of simulated material (SCP)
            basis_vector_X = new VectorR3(1, 0, 0);
            basis_vector_Y = new VectorR3(0, 1, 0);
            basis_vector_Z = new VectorR3(0, 0, 1);
        }
        
        System.out.println("Number of cells at 1st coordination sphere:  "+neighb1S_number);
        System.out.println("Number of cells at 1-3 coordination spheres: "+neighb3D_number);
        System.out.println();

        // TEST
        System.out.println("bound_interaction_type = "+bound_interaction_type);
        System.out.println("min_neighbours_number  = "+min_neighbours_number);

        // Reading of number of automata with materials of inclusions
   //     incl_number             = (new Integer(task_properties.getProperty("incl_number"))).intValue();        
        
        // Reading of variable responsible for heat expansion
        // (transition of heat energy part to mechanical energy)
        // calc_heat_expansion = (new Byte(task_properties.getProperty("calc_heat_expansion"))).byteValue();
    }

    /** The method returns basis vectors of crystal lattice of specimen material.
     * @return basis vectors of crystal lattice of specimen material
     */
    public VectorR3[] getBasisVectors()
    {
        VectorR3[] basis_vectors = new VectorR3[3];

        basis_vectors[0] = basis_vector_X;
        basis_vectors[1] = basis_vector_Y;
        basis_vectors[2] = basis_vector_Z;

        return basis_vectors;
    }

    /** The method sets basis vectors of crystal lattice of specimen material.
     * @param basis vectors of crystal lattice of specimen material
     */
    private void setBasisVectors(VectorR3[] basis_vectors)
    {
        basis_vector_X = new VectorR3(basis_vectors[0]);
        basis_vector_Y = new VectorR3(basis_vectors[1]);
        basis_vector_Z = new VectorR3(basis_vectors[2]);
    }

    /** The method returns type of interaction of boundary cells with neighbours
     * @return type of interaction of boundary cells with neighbours
     */
    public byte getBoundInteractionType()
    {
        return bound_interaction_type;
    }

    /** The method sets type of interaction of boundary cells with neighbours
     * @param _bound_interaction_type type of interaction of boundary cells with neighbours
     */
    public void setBoundInteractionType(byte _bound_interaction_type)
    {
        bound_interaction_type = _bound_interaction_type;
    }

    /** The method returns effective rate of response on external action.
     * @return effective rate of response on external action
     */
    public double getResponseRate()
    {
        return response_rate;
    }

    /** The method sets effective rate of response on external action.
     * @param _response_rate effective rate of response on external action
     */
    public void setResponseRate(double _response_rate)
    {
        response_rate = _response_rate;
    }

    /** The method returns specimen size in direction of X-axis.
     * @return specimen size in direction of X-axis 
     */
    public double get_specimen_size_X()
    {
        return specimen_size_X;
    }

    /** The method returns specimen size in direction of Y-axis.
     * @return specimen size in direction of Y-axis 
     */
    public double get_specimen_size_Y()
    {
        return specimen_size_Y;
    }
  
    /** The method returns specimen size in direction of Z-axis.
     * @return specimen size in direction of Z-axis 
     */
    public double get_specimen_size_Z()
    {
        return specimen_size_Z;
    }

    /** The method returns finite element size in direction of X-axis.
     * @return finite element size in direction of X-axis
     */
    public double get_element_size_X()
    {
        return element_size_X;
    }

    /** The method returns finite element size in direction of Y-axis.
     * @return finite element size in direction of Y-axis
     */
    public double get_element_size_Y()
    {
        return element_size_Y;
    }
  
    /** The method returns finite element size in direction of Z-axis.
     * @return finite element size in direction of Z-axis
     */
    public double get_element_size_Z()
    {
        return element_size_Z;
    }
    
    /** The method returns cell size in direction of X-axis.
     * @return cell size in direction of X-axis
     */
    public double get_cell_size_X()
    {
        return cell_size_X;
    }

    /** The method returns cell size in direction of Y-axis.
     * @return cell size in direction of Y-axis
     */
    public double get_cell_size_Y()
    {
        return cell_size_Y;
    }
  
    /** The method returns cell size in direction of Z-axis.
     * @return cell size in direction of Z-axis
     */
    public double get_cell_size_Z()
    {
        return cell_size_Z;
    }
    
    /** The method returns number of cells of specimen3D in direction of X-axis.
     * @return number of cells of specimen3D in direction of X-axis
     */
    public int getCellNumberI()
    {
        return cellNumberI;
    }
        
    /** The method returns number of cells of specimen3D in direction of Y-axis.
     * @return number of cells of specimen3D in direction of Y-axis
     */
    public int getCellNumberJ()
    {
        return cellNumberJ;
    }
                
    /** The method returns number of cells of specimen3D in direction of Z-axis.
     * @return number of cells of specimen3D in direction of Z-axis
     */
    public int getCellNumberK()
    {
        return cellNumberK;
    }

    /** The method returns type of packing of cells in specimen (SCP or HCP).
     * @return type of packing of cells in specimen (SCP or HCP)
     */
    public byte getPackingType()
    {
        return packing_type;
    }

    /** The method returns number of neighbour cells at 1st coordination sphere of cell.
     * @return number of neighbour cells at 1st coordination sphere of cell
     */
    public int getNeighb1SNumber()
    {
        return neighb1S_number;
    }

    /** The method returns number of neighbour cells at 1st-3rd coordination spheres of cell.
     * @return number of neighbour cells at 1st-3rd coordination spheres of cell
     */
    public int getNeighb3DNumber()
    {
        return neighb3D_number;
    }

    /** The method returns volume fraction of particles.
     * @return volume fraction of particles
     */
    public double getParticlesVolumeFraction()
    {
        return particles_volume_fraction;
    }   
    
    /** The method returns radius of particle.
     * @return radius of particle
     */
    public double getParticleRadius()
    {
        return particle_radius;
    }
    
    /** The method returns minimal number of neighbour cells in adjacent grain 
     * necessary for transition of "central" cell to adjacent grain.
     * @return minimal number of neighbour cells in adjacent grain 
     *         necessary for transition of "central" cell to adjacent grain
     */
    public int getMinNeighboursNumber()
    {
        return min_neighbours_number;
    }   
    
    /** The method returns value of time step.
     * @return value of time step
     */
    public double getTimeStep()
    {
        return time_step;
    }    
    
    /** The method returns the coefficient for calculation of cell torsion energy change.
     * @return the coefficient for calculation of cell torsion energy change
     */
    /*
    public double getTorsionEnergyCoeff()
    {
        return torsion_energy_coeff;
    }
    */
    
    /** The method returns number of time steps.
     * @return number of time steps
     */
    public long getStepNumber()
    {
        return step_number;
    }
    
    /** The method returns number of output files.
     * @return number of output files
     */
    public long getOutputFileNumber()
    {
        return output_file_number;
    }

    /** The method returns state of variable "simulate_heat_expansion".
     * @return state of variable "simulate_heat_expansion"
     */
    public byte getHeatExpState()
    {
        return simulate_heat_expansion;
    }
    
    /** The method returns state of variable "defects_influence", which determines whether 
     * the number of defects is taken in account for calculation of mobility.
     * @return state of variable "defects_influence"
     */
    public byte getDefectsInfluence()
    {
        return defects_influence;
    }
    
    /** The method sets state of variable "defects_influence", which determines whether 
     * the number of defects is taken in account for calculation of mobility.
     * @param _defects_influence state of variable "defects_influence"
     */
    public void getDefectsInfluence(byte _defects_influence)
    {
        defects_influence = _defects_influence;
    }
    
    /** The method returns state of variable "calc_force_moments"
     * @return state of variable "calc_force_moments"
     */
    public byte getCalcForceMomState()
    {
        return calc_force_moments;
    }

    /** The method sets state of variable "simulate_heat_expansion".
     * @param _simulate_heat_expansion state of variable "simulate_heat_expansion"
     */
    public void setHeatExpState(byte _simulate_heat_expansion)
    {
        simulate_heat_expansion = _simulate_heat_expansion;
    }

    /** The method sets state of variable "calc_force_moments"
     * @param _calc_force_moments state of variable "calc_force_moments"
     */
    public void setCalcForceMomState(byte _calc_force_moments)
    {
        calc_force_moments = _calc_force_moments;
    }

    /** The method returns list of initial temperatures of cells 
     * @return list of initial temperatures of cells 
     */
    public ArrayList getInitTemperatures()
    {
        return initTemperatures;
    }
    
    /** The method returns 2D array of indices of neighbour cells at 1st coordination sphere:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index.
     * @return 2D array of indices of neighbour cells at 1st coordination sphere
     */
    public int[][] getNeighbours1S()
    {
        return neighbours1S;
    }

    /** The method returns 2D array of indices of neighbour cells at 1st-3rd coordination spheres:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index.
     * @return 2D array of indices of neighbour cells at 1st-3rd coordination spheres
     */
    public int[][] getNeighbours3D()
    {
        return neighbours3D;
    }
        
    /** The method sets 2D array of indices of neighbour cells at 1st coordination sphere:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index.
     * @param _neighbours3D 2D array of indices of neighbour cells at 1st coordination sphere
     */
    private void setNeighbours1S(int[][] _neighbours1S)
    {
        neighbours1S = _neighbours1S;
    }

    /** The method sets 2D array of indices of neighbour cells at 1st-3rd coordination spheres:
     * 1st index is responsible for central cell index,
     * 2nd - for neighbour cell index.
     * @param _neighbours3D 2D array of indices of neighbour cells at 1st-3rd coordination spheres
     */
    private void setNeighbours3D(int[][] _neighbours3D)
    {
        neighbours3D = _neighbours3D;
    }
    
    /** The method sets value of time step.
     * @param _time_step value of time step
     */
    public void setTimeStep(double _time_step)
    {
        time_step = _time_step;
    }    
    
    /** The method sets the coefficient for calculation of cell torsion energy change.
     * @param _torsion_energy_coeff the coefficient for calculation of cell torsion energy change
     */
    /*
    public void setTorsionEnergyCoeff(double _torsion_energy_coeff)
    {
        torsion_energy_coeff = _torsion_energy_coeff;
    }
    */
    
    /** The method sets number of time steps.
     * @param _step_number number of time steps
     */
    public void setStepNumber(long _step_number)
    {
        step_number = _step_number;
    }
    
    /** The method sets number of output files.
     * @param _output_file_number number of output files
     */
    public void setOutputFileNumber(long _output_file_number)
    {
        output_file_number = _output_file_number;
    }
    
    /** The method returns number of cells of specimen3D in direction of X-axis.
     * @param _cellNumberI number of cells of specimen3D in direction of X-axis
     */
    public void setCellNumberI(int _cellNumberI)
    {
        cellNumberI = _cellNumberI;
    }
        
    /** The method sets number of cells of specimen3D in direction of Y-axis.
     * @param _cellNumberJ number of cells of specimen3D in direction of Y-axis
     */
    public void setCellNumberJ(int _cellNumberJ)
    {
        cellNumberJ = _cellNumberJ;
    }
                
    /** The method sets number of cells of specimen3D in direction of Z-axis.
     * @param _cellNumberK number of cells of specimen3D in direction of Z-axis
     */
    public void setCellNumberK(int _cellNumberK)
    {
        cellNumberK = _cellNumberK;
    }
    
    /** The method returns total number of cells consisting the specimen.
     * @return total number of cells
     */
    public int cellNumber()
    {
        return cellNumberI*cellNumberJ*cellNumberK;
    }
    
    /** The method returns list of grains included in specimen
     * @return list of grains included in specimen
     */
    public ArrayList getGrainsCluster()
    {
        return grainsCluster;
    }
    
    /** The method sets list of grains included in specimen
     * @param _grainsCluster list of grains included in specimen
     */
    public void setGrainsCluster(ArrayList _grainsCluster)
    {
        grainsCluster = new ArrayList(_grainsCluster);
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
        
        // ID of grain
        int grainID;
        
        // Orientation angles of grain crystal lattice
        double grainAngle1;
        double grainAngle2;
        double grainAngle3;
        
        // Specific elastic energy of grain (J/m3)
        double grain_energy = 0.0;
        
        // Name of material of grain
        String material_name;
        
        // Material of grain
        Material material;
        
        // Density of dislocations in grain
        double grainDislDensity;
        
        // Average density of dislocations in grain
        double grainAverageDislDensity;
        
        // Deviation of dislocation density in grain
        double grainDislDensityDeviation;
        
        // Type of grain: 0 -"grain" of interacting outer boundary cells,
        //                1 - grain of inner cells,
        //                2 -"grain" of adiabatic outer boundary cells
        byte grain_type;
        
        // List of all materials
        ArrayList materials = new ArrayList(0);
                
        // List of names of all materials
        ArrayList material_names = new ArrayList(0);
        
        VectorR3 lat_vect_a, lat_vect_b, lat_vect_c;
        double cos_a_b, cos_b_c, cos_c_a;
        
        try
        {
            // New buffering character-input stream
            br = new BufferedReader(new FileReader(grains_file));
            
            System.out.println("Name of read file:    "+grains_file);
            
            while(br.ready())
            {                
                grainParameters = br.readLine();
                st = new StringTokenizer(grainParameters);                
                
                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                    {
                        // Creation of grain as a list of cells
                        grain = new Cluster();
                
                        // Reading of grain parameters from file
                        grainID        = (new Integer(first_token)).intValue();
                        material_name  = st.nextToken();
                        grainAngle1    = (new Double (st.nextToken())).doubleValue();
                        grainAngle2    = (new Double (st.nextToken())).doubleValue();
                        grainAngle3    = (new Double (st.nextToken())).doubleValue();
                        grainDislDensity          = (new Double (st.nextToken())).doubleValue();
                        grainAverageDislDensity   = (new Double (st.nextToken())).doubleValue();
                        grainDislDensityDeviation = (new Double (st.nextToken())).doubleValue();
                        grain_type     = (new Byte (st.nextToken())).byteValue();
                        
                        // Setting of these parameters to grain
                        if(material_names.contains(material_name))
                        {                            
                            grain.setMaterial((Material)materials.get((int)material_names.indexOf(material_name)));
                        }
                        else
                        {                            
                            material = new Material(material_name);
                            grain.setMaterial(material);
                            material_names.add(material_name);
                            materials.add(material);
                        }
                        
                        grain.setMaterialName(material_name);
                        grain.setID(grainID);
                        grain.setAngles(grainAngle1, grainAngle2, grainAngle3);
                        grain.setDislDensity(grainDislDensity);
                        grain.setAverageDislDensity(grainAverageDislDensity);
                        grain.setDislDensityDeviation(grainDislDensityDeviation);
                        grain.setType(grain_type);

                        // Determination of energy of dislocations contained in grain
                        grain.calcDislEnergy();
                        
                        
                        // Addition of grain to list of grains
                        grainsCluster.add(grain);
                        
                        // TEST
                        lat_vect_a = grain.getLatticeVectorA();
                        lat_vect_b = grain.getLatticeVectorB();
                        lat_vect_c = grain.getLatticeVectorC();
                        
                        cos_a_b = cosinus(lat_vect_a, lat_vect_b);
                        cos_b_c = cosinus(lat_vect_b, lat_vect_c);
                        cos_c_a = cosinus(lat_vect_c, lat_vect_a);
                                
                        System.out.println("Grain # "+grainID+". type: "+grain_type+"; grainAngle1: "+grainAngle1+"; grainAngle2: "+grainAngle2+"; grainAngle3: "+grainAngle3+
                                           " vol.fraction of particles: "+grain.getParticleVolumeFraction());
                        System.out.println("  lat_vect_a: "+lat_vect_a.writeToString()+"; cos_a_b = "+cos_a_b);
                        System.out.println("  lat_vect_b: "+lat_vect_b.writeToString()+"; cos_b_c = "+cos_b_c);
                        System.out.println("  lat_vect_c: "+lat_vect_c.writeToString()+"; cos_c_a = "+cos_c_a);
                    }
                }
            }
         // System.out.println((string_number - 1) + " " + densities.get(string_number - 1));
            br.close();            
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method sets initial values of physical properties for each cell.
     * @param init_cond initial values of physical properties for each cell.
     */
    private void setInitialConditions(InitialConditions init_cond)
    {
     // RecCellR3 cell;
        
        initTemperatures    = new ArrayList(init_cond.getInitialTemperatures());
        ArrayList initThermalEnergies = new ArrayList(init_cond.getInitialThermalEnergies());
        ArrayList initMechEnergies    = new ArrayList(init_cond.getInitialMechEnergies());
  //      ArrayList heatConductivities  = new ArrayList(init_cond.getHeatConductivities());
  //      ArrayList heatCapacities      = new ArrayList(init_cond.getHeatCapacities());
  //      ArrayList densities           = new ArrayList(init_cond.getDensities());
  //      ArrayList heatExpCoeffs       = new ArrayList(init_cond.getHeatExpCoeffs());
  //      ArrayList yieldStrains        = new ArrayList(init_cond.getYieldStrains());
        ArrayList grainIndices        = new ArrayList(init_cond.getGrainIndices());
        ArrayList cellTypes           = new ArrayList(init_cond.getCellTypes());
        
        ArrayList initTorsEnergies    = new ArrayList(init_cond.getInitialTorsEnergies());
        
        int cell_counter;

        System.out.println("initTemperatures size: "+initTemperatures.size());
        System.out.println("Spec. sizes: "+cellNumberI+" "+cellNumberJ+" "+cellNumberK);
        
        // Variable is responsible for linear distribution of initial temperatures.
        boolean linear_distribution = false;// true; // 
        VectorR3 coordinates;
        double coord_Z;
        double min_coord_Z = calcCoordinates(0, 0, 0).getZ();
        double max_coord_Z = calcCoordinates(cellNumberI, cellNumberJ, cellNumberK).getZ();
        double temperature, 
               min_tempr = 300, 
               max_tempr = 30300;
        
        for(int cell_index_K = 0; cell_index_K < cellNumberK; cell_index_K++)
        for(int cell_index_J = 0; cell_index_J < cellNumberJ; cell_index_J++)
        for(int cell_index_I = 0; cell_index_I < cellNumberI; cell_index_I++)
        {
       //     System.out.println("Cell indices: "+cell_index_I+" "+cell_index_J+" "+cell_index_K);

            cell_counter = calcSingleIndex(cell_index_I, cell_index_J, cell_index_K);
            cell = (RecCellR3)get(cell_counter);
            
            // Specifying of physical properties of current cell
            cell.setInitialTemperature(0);
            
            if(!linear_distribution)
                cell.setTemperature       (((Double)initTemperatures.get(cell_counter)).doubleValue());
            else
            {
                 coordinates = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
                 coord_Z = coordinates.getZ();
                 temperature = max_tempr - (max_tempr - min_tempr)*(coord_Z - min_coord_Z)/(max_coord_Z - min_coord_Z);
                 cell.setTemperature(temperature);
            }
            
       //     cell.setThermalEnergy     (((Double)initThermalEnergies.get(cell_counter)).doubleValue());
            cell.calcThermalEnergy();
            cell.setInitialTemperature(cell.getTemperature());
            
            // Setting of heat influx to the cell
            cell.setHeatInflux(0);
            
            cell.setMechEnergy        (((Double)initMechEnergies.get(cell_counter)).doubleValue());
            cell.calcMechStressAndStrain();

            cell.setGrainIndex        (((Integer)grainIndices.get(cell_counter)).intValue());// - 1);
            
      //    if(LoadProperties.SHOW_GRAIN_BOUNDS==1)
            cell.setType          (((Byte)cellTypes.get(cell_counter)).byteValue());
            
            cell.setTorsionEnergy(((Double)initTorsEnergies.get(cell_counter)).doubleValue());
        //    System.out.println("Cell # "+cell_counter+"; type: "+cell.getType());
        }
    }

    /** The method sets values of physical properties for boundary cells.
     * @param bound_cond values of physical properties for boundary cells.
     * @param write_to_file variable responsible for choice of writing or not
     * @param step_counter number of current time step
     */
    public void setBoundaryConditions(BoundaryConditions bound_cond, boolean write_to_file, long step_counter)
    {
        boundCond = new BoundaryConditions(bound_cond);
        
        // Lists of single indices, temperatures, thermal energies,
        // mechanical energies of boundary cells
        ArrayList boundaryIndices           = boundCond.getBoundaryIndices();
        ArrayList boundMinThermalParams     = boundCond.getBoundaryMinThermalEnergies();
        ArrayList boundThermalParams        = boundCond.getBoundaryThermalEnergies();
        ArrayList boundMinMechParams        = boundCond.getBoundaryMinMechEnergies();
        ArrayList boundMechParams           = boundCond.getBoundaryMechEnergies();
        ArrayList bound_time_function_types = boundCond.getBoundTimeFunctionTypes();
        ArrayList bound_load_time_portions  = boundCond.getBoundLoadTimePortions();
        ArrayList bound_relax_time_portions = boundCond.getBoundRelaxTimePortions();
        
        double min_thermal_energy = 0, thermalEnergy = 0;
        double bound_min_stress   = 0, bound_stress  = 0;
        double bound_min_strain   = 0, bound_strain  = 0;
        double mechEnergy         = 0;
        byte   bound_time_function_type = -1;
        double bound_load_time_portion  = 0;
        double bound_relax_time_portion = 0;
        
        double init_temperature = 0;
        int    cell_index;
        
        for(int bound_cell_counter = 0; bound_cell_counter < boundaryIndices.size(); bound_cell_counter++)
        {
          cell_index       = (int)boundaryIndices.get(bound_cell_counter);
          bound_min_strain = (double)boundMinMechParams.get(bound_cell_counter);
          bound_strain     = (double)boundMechParams.get(bound_cell_counter);
          
          //  TEST
       //   if(bound_min_strain > 0.0 | bound_strain > 0.0)
       //     System.out.println("Boundary cell # "+cell_index+": min_mech_param = "+bound_min_strain+"; mech_param = "+bound_strain);
        }
        
        if(step_counter == 1)
          System.out.println("\nstep_counter = "+step_counter+". Boundary conditions are read!!!\n");
        
        // Type of loading (constant, periodic, cycle)
        loading_type   = "";// Common.CONSTANT_LOADING; // Common.CYCLE_LOADING; // 
        
        // Period of loading (in time steps) for cyclic conditions
        load_period    = step_number;// step_number/10;// step_number/2;// step_number/5; // step_number/3;
    
        // Period of loading absence (in time steps) for cyclic conditions
        no_load_period = step_number - load_period;
        
        if(write_to_file)
        {
            System.out.println("Type of loading: "+loading_type);
        
            if(loading_type == Common.CYCLE_LOADING)
            {
                System.out.println("Period of loading:         "+load_period+" time steps.");
                System.out.println("Period of loading absence: "+no_load_period+" time steps.");
            }
        }

        // Indices of neighbours1S of boundary cell
        int[] bound_neighbours = new int[neighb1S_number];

        // Neighbours1S of boundary cell
        RecCellR3 neighb1S;

        // Average modulus of elasticity of neighbours1S of boundary cell
        double neighb1S_mod_elast = 0;

        // Average maximal boundary mobility of neighbours1S of boundary cell
        double neighb1S_max_mobility = 0;

        // Number of neighbours1S of boundary cell
        int boundCA_neighb1S_number = 0;

        int bound_cells_number = 0;

        int interacted_bound_cells_number = 0;
        
        // Standard number of interacted internal cells
        int standard_boundCA_neighb1S_number = 3;

        int index=0;
        double heat_param=0;
        double mech_param=0;
        
        // Average mechanical energy of inner boundary neighbour cell
        double neighb1S_aver_mech_energy = 0;
        
        boolean mech_loading = false;
        
        // The ratio of smoothness of specimen surface
        double[] smoothness_ratio = new double[6];
        double smoothness_ratio_sum = 0;
        
        // Number of cells at facets
        int[] facet_cell_number = new int[6];
        int total_facet_cell_number = 0;
        
        // Number of adiabatic cells at facets
        int[] adiab_cell_number = new int[6];
        int total_adiab_cell_number = 0;
        
        // Index of neighbour cell
        int neighb_cell_index = -1;
        
        double neighb1S_init_temperature = 0;
        
        for(int facet_counter = 0; facet_counter < 6; facet_counter ++)
        {
            smoothness_ratio[facet_counter] = 1.0;// 0.75;// 0.75 - facet_counter*0.1;
            smoothness_ratio_sum += smoothness_ratio[facet_counter];
            
            facet_cell_number[facet_counter] = 0;
            adiab_cell_number[facet_counter] = 0;
        }
        
        double rand;
        
        int grain_index  = 0;
        byte grain_type  = 0;        
        int grain_number = grains.length;
        int facet_index = -1;
        
      //  System.out.println("step_counter = "+step_counter+". Number of grains: "+grain_number);
        
        // Realization of setting of smoothness of specimen surface
        if(step_counter == 1)
        {
          if(smoothness_ratio_sum > 0.0) 
          {
            if(smoothness_ratio_sum < 6.0)
            for(int cell_counter = 0; cell_counter < boundaryIndices.size(); cell_counter++)
            {
              cell_index = new Integer((Integer)boundaryIndices.get(cell_counter)).intValue();
              cell = (RecCellR3)get(cell_index);
              
              grain_index = cell.getGrainIndex();
              grain_type  = cell.getGrainType();
              
              facet_index = grain_index + 11 - grain_number;
              
              if(facet_index < 6)
              {    
                facet_cell_number[facet_index]++;
                total_facet_cell_number++;
                
                rand = Math.random();
              
                if(grain_type != Common.ADIABATIC_ADIABATIC_CELL)
                if(smoothness_ratio[facet_index] < 1.0)
                if(rand >     0.5*(smoothness_ratio[facet_index] - 1000.0*(1.0 - adiab_cell_number[facet_index]*1.0/facet_cell_number[facet_index] - smoothness_ratio[facet_index])))
                if(rand <= 1- 0.5*(smoothness_ratio[facet_index] - 1000.0*(1.0 - adiab_cell_number[facet_index]*1.0/facet_cell_number[facet_index] - smoothness_ratio[facet_index])))
                {
                  cell.setGrainType(Common.ADIABATIC_ADIABATIC_CELL);
                
                  adiab_cell_number[facet_index]++;
                  total_adiab_cell_number++;
                }
              }
            }
          }
          else
          {
            for(int cell_counter = 0; cell_counter < boundaryIndices.size(); cell_counter++)
            {
              cell_index = new Integer((Integer)boundaryIndices.get(cell_counter)).intValue();
              cell = (RecCellR3)get(cell_index);
              
              grain_index = cell.getGrainIndex();
              grain_type  = cell.getGrainType();
              
              facet_index = grain_index + 11 - grain_number;
              
              if(facet_index < 6)
              { 
                facet_cell_number[facet_index]++;
                total_facet_cell_number++;
              
                cell.setGrainType(Common.ADIABATIC_ADIABATIC_CELL);
              
                adiab_cell_number[facet_index]++;
                total_adiab_cell_number++;
              }
            }
          }
          
          System.out.println("Left facet includes   "+facet_cell_number[0]+" cells; adiabatic cells: "+adiab_cell_number[0]+"; ratio: real: "+adiab_cell_number[0]*1.0/facet_cell_number[0]+"; assigned: "+(1 - smoothness_ratio[0]));
          System.out.println("Front facet includes  "+facet_cell_number[1]+" cells; adiabatic cells: "+adiab_cell_number[1]+"; ratio: real: "+adiab_cell_number[1]*1.0/facet_cell_number[1]+"; assigned: "+(1 - smoothness_ratio[1]));
          System.out.println("Top facet includes    "+facet_cell_number[2]+" cells; adiabatic cells: "+adiab_cell_number[2]+"; ratio: real: "+adiab_cell_number[2]*1.0/facet_cell_number[2]+"; assigned: "+(1 - smoothness_ratio[2]));
          System.out.println("Bottom facet includes "+facet_cell_number[3]+" cells; adiabatic cells: "+adiab_cell_number[3]+"; ratio: real: "+adiab_cell_number[3]*1.0/facet_cell_number[3]+"; assigned: "+(1 - smoothness_ratio[3]));
          System.out.println("Back facet includes   "+facet_cell_number[4]+" cells; adiabatic cells: "+adiab_cell_number[4]+"; ratio: real: "+adiab_cell_number[4]*1.0/facet_cell_number[4]+"; assigned: "+(1 - smoothness_ratio[4]));
          System.out.println("Right facet includes  "+facet_cell_number[5]+" cells; adiabatic cells: "+adiab_cell_number[5]+"; ratio: real: "+adiab_cell_number[5]*1.0/facet_cell_number[5]+"; assigned: "+(1 - smoothness_ratio[5]));
          System.out.println("Total number of cells at loaded facets:           "+total_facet_cell_number);
          System.out.println("Total number of adiabatic cells at loaded facets: "+total_adiab_cell_number);
        }
        
        for(int cell_counter = 0; cell_counter < boundaryIndices.size(); cell_counter++)
        {
            mechEnergy = 0;
            temperature = 0;
            thermalEnergy = 0;
            neighb1S_mod_elast        = 0;
            neighb1S_max_mobility     = 0;
            neighb1S_aver_mech_energy = 0;
            neighb1S_init_temperature = 0;
            boundCA_neighb1S_number   = 0;

            cell_index = new Integer((Integer)boundaryIndices.get(cell_counter)).intValue();
            cell_indices = calcTripleIndex(cell_index, cellNumberI+2, cellNumberJ+2, cellNumberK+2);            
            cell = (RecCellR3)get(cell_index);
            
            mech_loading = false;

            // Calculation of energy influx corresponding to strain of boundary cell
            if(cell.getGrainType() != Common.ADIABATIC_ADIABATIC_CELL)
            {                
                bound_neighbours = neighbours1S[cell_index];

                for(int neighb_counter = 0; neighb_counter<neighb1S_number; neighb_counter++)
                if(bound_neighbours[neighb_counter]>-1)
                {   
                    neighb1S = (RecCellR3)get(bound_neighbours[neighb_counter]);

                    if((neighb1S.getGrainType()==Common.INNER_CELL)|
                       (neighb1S.getGrainType()==Common.LAYER_CELL))
                    {
                        neighb1S_mod_elast        += neighb1S.get_mod_elast();
                        neighb1S_max_mobility     += neighb1S.getMaxMobility();    
                        neighb1S_aver_mech_energy += neighb1S.getMechEnergy();
                        neighb1S_init_temperature += (double)initTemperatures.get(bound_neighbours[neighb_counter]);
                   //     neighb1S_init_temperature += neighb1S.getInitialTemperature();
                        
                        boundCA_neighb1S_number++;
                    }
                }
                
                if(boundCA_neighb1S_number>0)
                {
                    neighb1S_mod_elast        = neighb1S_mod_elast/boundCA_neighb1S_number;
                    neighb1S_max_mobility     = neighb1S_max_mobility/boundCA_neighb1S_number;
                    neighb1S_aver_mech_energy = neighb1S_aver_mech_energy/boundCA_neighb1S_number;
                    neighb1S_init_temperature = neighb1S_init_temperature/boundCA_neighb1S_number;
                }

                if((cell.getGrainType() == Common.ADIABATIC_TEMPERATURE_CELL)|
                   (cell.getGrainType() == Common.STRAIN_TEMPERATURE_CELL)|
                   (cell.getGrainType() == Common.STRESS_TEMPERATURE_CELL))
                {
                    init_temperature         = ((Double)boundMinThermalParams.get(cell_counter)).doubleValue();
                    temperature              = ((Double)boundThermalParams.get(cell_counter)).doubleValue();
                    bound_time_function_type = ((Byte)bound_time_function_types.get(cell_counter)).byteValue();
                    bound_load_time_portion  = ((Double)bound_load_time_portions.get(cell_counter)).doubleValue();
                    bound_relax_time_portion = ((Double)bound_relax_time_portions.get(cell_counter)).doubleValue();
                    
             //     init_temperature         = neighb1S_init_temperature;
             //     init_temperature         = 300.0;
                    
              //      bound_time_function_type = Common.CONSTANT_LOADING_BYTE_VALUE;
                    
              //      temperature   =  init_temperature + (temperature - init_temperature)*bound_time_function(step_counter, loading_type);
                    temperature = init_temperature + (temperature - init_temperature)*
                                  bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion);
                    
                    cell.setInitialTemperature(cell.getTemperature());
                    cell.setTemperature(temperature);
                    cell.calcThermalEnergy();
                  //  cell.setInitialTemperature(temperature);
                    
                    cell.setHeatInflux(0);
                  //  cell.addHeatInflux();
                }

                if((cell.getGrainType() == Common.ADIABATIC_THERMAL_CELL)|
                   (cell.getGrainType() == Common.STRAIN_THERMAL_CELL)|
                   (cell.getGrainType() == Common.STRESS_THERMAL_CELL))
                {
                    min_thermal_energy       = ((Double)boundMinThermalParams.get(cell_counter)).doubleValue();
                    thermalEnergy            = ((Double)boundThermalParams.get(cell_counter)).doubleValue();
                    bound_time_function_type = ((Byte)bound_time_function_types.get(cell_counter)).byteValue();
                    bound_load_time_portion  = ((Double)bound_load_time_portions.get(cell_counter)).doubleValue();
                    bound_relax_time_portion = ((Double)bound_relax_time_portions.get(cell_counter)).doubleValue();
                    
                    // Influx of heat energy to cell per time step
                    if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE | bound_time_function_type == Common.PERIODIC_LOADING_BYTE_VALUE)
                      thermalEnergy = time_step*(min_thermal_energy + (thermalEnergy - min_thermal_energy)*bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion));
                    
                    if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE_2)
                    //  thermalEnergy = thermalEnergy*time_step*(2*bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion) - 1);
                      thermalEnergy = time_step*(0.5*(min_thermal_energy+thermalEnergy) + 0.5*(thermalEnergy - min_thermal_energy)*
                                     (2*bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion) - 1));
                    
                    cell.setHeatInflux(0);
                  //  cell.addHeatInflux(cell.getThermalEnergy() - thermalEnergy);
                    
                    cell.setInitialTemperature(0);
                    cell.setThermalEnergy(thermalEnergy);
                  //  cell.addThermalEnergy(thermalEnergy);
                    cell.calcTemperature();
                  //  cell.setInitialTemperature(cell.getTemperature());
                }

                if((cell.getGrainType() == Common.STRAIN_ADIABATIC_CELL)|
                   (cell.getGrainType() == Common.STRAIN_TEMPERATURE_CELL)|
                   (cell.getGrainType() == Common.STRAIN_THERMAL_CELL))
                {
                    mech_loading = true;
                    
                    // Boundary strain per second
                    bound_min_strain         = ((Double)boundMinMechParams.get(cell_counter)).doubleValue();
                    bound_strain             = ((Double)boundMechParams.get(cell_counter)).doubleValue();
                    bound_time_function_type = ((Byte)bound_time_function_types.get(cell_counter)).byteValue();
                    bound_load_time_portion  = ((Double)bound_load_time_portions.get(cell_counter)).doubleValue();
                    bound_relax_time_portion = ((Double)bound_relax_time_portions.get(cell_counter)).doubleValue();
                    
                    if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE | bound_time_function_type == Common.PERIODIC_LOADING_BYTE_VALUE)
                      bound_strain = bound_min_strain + (bound_strain - bound_min_strain)*bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion);
                    
                    if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE_2)
                      bound_strain = 0.5*(bound_strain + bound_min_strain) + 0.5*(bound_strain - bound_min_strain)*
                                    (2*bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion) - 1);
                    
                    // Boundary strain per time step
                    bound_strain = bound_strain*time_step;
                                   
                //    if(boundCA_neighb1S_number!=0)
                        // The number of internal neighbours is taken into account to avoid asymmetry of loading.
                        // bound_strain = bound_strain*standard_boundCA_neighb1S_number/boundCA_neighb1S_number;
                     //   bound_strain = bound_strain*boundCA_neighb1S_number/standard_boundCA_neighb1S_number;
                      //  bound_stress = cell_size_X*bound_strain*neighb1S_mod_elast/(neighb1S_max_mobility*time_step);
                    
                    // bound_stress = bound_strain*neighb1S_mod_elast;
                    
                    bound_stress = bound_strain*cell.get_mod_elast();
                            
                    // Boundary mechanical energy
                    mechEnergy    = bound_stress*getCellVolume();
                }

                if((cell.getGrainType() == Common.STRESS_ADIABATIC_CELL)|
                   (cell.getGrainType() == Common.STRESS_TEMPERATURE_CELL)|
                   (cell.getGrainType() == Common.STRESS_THERMAL_CELL))
                {
                    mech_loading = true;
                    
                    // Boundary stress
                    bound_min_stress         = ((Double)boundMinMechParams.get(cell_counter)).doubleValue();
                    bound_stress             = ((Double)boundMechParams.get(cell_counter)).doubleValue();
                    bound_time_function_type = ((Byte)bound_time_function_types.get(cell_counter)).byteValue();
                    bound_load_time_portion  = ((Double)bound_load_time_portions.get(cell_counter)).doubleValue();
                    bound_relax_time_portion = ((Double)bound_relax_time_portions.get(cell_counter)).doubleValue();
                    
                    if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE | bound_time_function_type == Common.PERIODIC_LOADING_BYTE_VALUE)
                      bound_stress = bound_min_stress + (bound_stress - bound_min_stress)*bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion);
                    
                    if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE_2)
                      bound_stress = 0.5*(bound_stress + bound_min_stress) + 0.5*(bound_stress - bound_min_stress)*
                                    (2*bound_time_function(step_counter, bound_time_function_type, bound_load_time_portion, bound_relax_time_portion) - 1);
                      
                    // Boundary stress per time step
                    bound_stress = bound_stress*time_step;
                    
              //      if(boundCA_neighb1S_number!=0)
                        // The number of internal neighbours is taken into account to avoid asymmetry of loading.
                        // bound_stress = bound_stress*standard_boundCA_neighb1S_number/boundCA_neighb1S_number;
                 //       bound_stress = bound_stress*boundCA_neighb1S_number/standard_boundCA_neighb1S_number;
                    
                    if(neighb1S_mod_elast>0)
                      //  bound_strain  = bound_stress*neighb1S_max_mobility*time_step/(cell_size_X*neighb1S_mod_elast);
                        bound_strain  = bound_stress/neighb1S_mod_elast;
                    else
                        bound_strain  = 0;
                   
                   if(cell.get_mod_elast() != 0.0)
                     bound_strain  = bound_stress/cell.get_mod_elast();
                   else
                     bound_strain = 0;

                   mechEnergy    = bound_stress*getCellVolume();
                }
/*------------------------------------------------------------------------------------------------------------------------------------*/
    
                if(mech_loading)
          //      if(loading_type == Common.CONSTANT_LOADING)
                {
                    // Constant influx of mechanical energy to boundary cell
                    cell.addMechEnergy(mechEnergy);
                    
                    // Calculation of mechanical stress of boundary cell
                    cell.calcMechStressAndStrain();
                    
                    // Calculation of the number of boundary cells
                    bound_cells_number++;
                    
                    // TEST
                //    if(step_counter == 1)
                 //     System.out.println("Step # "+step_counter+". Boundary cell # "+cell_index+". coordinates: ("+cell.getCoordinates().writeToString()+"). bound_strain = "+bound_strain+"; mechEnergy = "+mechEnergy);
                }
              /*  
                if(mech_loading)
                if(loading_type == Common.CYCLE_LOADING)
                {
               // if(mechEnergy != 0 & Math.abs(mechEnergy) > 1.0E-300)
                  if(true)
                  {
                    // Influx of mechanical energy to boundary cell
                    cell.addMechEnergy(mechEnergy);
                    
                    // Calculation of mechanical stress and strain of boundary cell
                    cell.calcMechStressAndStrain();  
                  }
                  else
                  {
                    // Mechanical energy to boundary cell becomes equal to zero
                    cell.setMechEnergy(0);
                    
                    // Calculation of mechanical stress and strain of boundary cell: these values become equal to zero
                    cell.calcMechStressAndStrain();
                    
                 //   temperature =  300;
                 //   cell.setTemperature(temperature);
                 //   cell.setThermalEnergy(0);
                 //   cell.setInitialTemperature(temperature);
                  }
                  
                  // Calculation of the number of boundary cells
                  bound_cells_number++;                    
                }
                */
            }
            else
            {
                cell.setMechEnergy(0);
                cell.setThermalEnergy(0);
                cell.calcMechStressAndStrain();
                cell.setStrain(0);
                
        //      System.out.println("Cell # "+cell_index+". Mech. stress: "+cell.getMechStress());
            }
            
         //   if(cell.getMechEnergy()!=0)
           //     System.out.println("Cell # "+cell_index+". Mech. energy: "+cell.getMechEnergy());
            
            set(cell_index, cell);
            
        //    System.out.println(cell.getTemperature()+" "+cell.getThermalEnergy());
        }

        if(write_to_file)
            System.out.println("Number of interacted boundary cells: "+bound_cells_number);//interacted_bound_cells_number);//
    }
    
    /** The method returns boundary conditions of task.
     * @return boundary conditions of task
     */
    public BoundaryConditions getBoundaryConditions()
    {
    //    System.out.println("boundConditions.getBoundaryIndices()"
     //                      +boundConditions.getBoundaryIndices());
        return boundCond;
    }
    
    /** The method calculates temperature of each automaton from parallelepiped
     * as a linear function of its centre coordinares.
     * @param writeFileName name of file for writing
     * @param temperatures  temperatures in vertices from parallelepiped
     */
    public void setTemperatures(String writeFileName, double[] temperatures)
    {
        // Sizes of parallelepiped evaluated in corresponding automaton sizes
        elementSizeK = getCellNumberK();
        elementSizeJ = getCellNumberJ(); 
        elementSizeI = getCellNumberI();
   /*     
        Three cell_indices; 
        int index;
        RecCellR3 cell;
*/
        LineFunctionR3 lineFunc;
  //      double temperature;
        double coordZ;
        double coordY;
        double coordX; 
        
        String fileName = new String(writeFileName+"."+Common.TEMPERATURES_FILE_EXTENSION);
       
        try
        {
            bw = new BufferedWriter(new FileWriter(fileName));
            
            // Spatial cycle on automata located in given finite element.
            for (indexK = 0; indexK < elementSizeK; indexK++)        
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {      
                // Creation of 3D line function.
                lineFunc = new LineFunctionR3(temperatures[0], temperatures[1], temperatures[2], temperatures[3],
                                          temperatures[4], temperatures[5], temperatures[6], temperatures[7]);
                
                // Calculation of triple index of the cell with given indices.
                cell_indices = new Three(indexI, indexJ, indexK);
            
                // Call of the cell with given indices from list of all cells of specimen.
                index = calcSingleIndex(cell_indices);
                    
                cell = (RecCellR3)get(index);
            
                // Coordinates of CA centre in given volume of 1x1x1 size 
                coordZ = (indexK+0.5)/elementSizeK;
                coordY = (indexJ+0.5)/elementSizeJ;
                coordX = (indexI+0.5)/elementSizeI;
            
                // Calculation of the temperature of the cell with given indices.
                temperature = lineFunc.calcValue(coordZ, coordY, coordX);
            
                bw.write((indexI+0.5)+" "+ (indexJ+0.5)+" "+temperature);
                bw.newLine();
            
                cell.setTemperature(temperature);            
         //       set(index, cell);
            } 
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        
        try
        {
            bw.flush();
            bw.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }    
    
    /** The method performs heat flows between automata located in given parallelepiped
     * and changes automata temperatures. 
     * Then information on cell temperatures is written to file.
     * @param writeFileName name of file for writing
     * @param writeToFile if true then data of simulation are written to file
     * @param step_counter number of time step
     */
    public void calcHeatFlows_T(String writeFileName, boolean writeToFile, long step_counter)
    {
        /* Block 13 */

        if(writeToFile)
        try
        {
            bw  = new BufferedWriter(new FileWriter(writeFileName+"_"+(step_counter+1)+"."+Common.TEMPERATURES_FILE_EXTENSION));
       //   bw1 = new BufferedWriter(new FileWriter(writeFileName+"_"+(step_counter+1)+"_states_points.txt"));
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        
        double oldCellThermalEnergy;
        double oldCellMechEnergy;
        byte cell_type = -1;
        byte neighb_type = -1;

        // neighbour automata of the "current" cell
        neighbCells = new RecCellR3[neighb1S_number];
        
        boolean neighbour_heating, cell_heating,
                inner_cell, outer_interacted_cell, 
                inner_neighbour, outer_interacted_neighbour;

        boolean heat_exchange = false;

        // Index of adjacent grain containing this neighbour cell
        int neighbCellGrainIndex;
                
        double[] cell_euler_angles = new double[3];
        double[] nghb_euler_angles = new double[3];
        
        // Current heat influxes to cells
        double[] heat_influxes = new double[cellNumberI*cellNumberJ*cellNumberK];
        
        // SIMULATION OF "HEAT" TIME STEPS
        for(int stepCounter = 0; stepCounter < heat_step_number; stepCounter++)
        {          
            // TEST
            if(writeToFile)
                System.out.println("Heat transfer is simulated...");

         //   bound_norm_toposes.clear();

            // Spatial cycle on automata located in CA micro specimen 
            // Heat exchange between neighbouring automata
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {               
              // Calculation of single index (in ArrayList)
              // of the cell with current triple indices  
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
                 
              // Obtaining of cell and its neighbours from list of cells
              cell = (RecCellR3)get(index);
              
              // Heat energy of current cell at previous time step
              prev_heat_energies[index] = cell.getThermalEnergy();
                 
              // Index of grain containing this cell
              cell_type = cell.getGrainType();

              // Determination of presence of interrelation of cell with neighbours
              cell_heating = (cell_type!=Common.ADIABATIC_ADIABATIC_CELL)
                            &(cell_type!=Common.STRAIN_ADIABATIC_CELL)
                            &(cell_type!=Common.STRESS_ADIABATIC_CELL);
                 
              if(cell_heating)
              {
                 // Single indices of neighbours at 1st coordination sphere
                 neighbIndices = neighbours1S[index];
                 
                 // Creation of array of neighbour cells   
                 for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                 {
                     if(neighbIndices[cellCounter] > -1)
                     {
                         neighbCells[cellCounter] = (RecCellR3)get(neighbIndices[cellCounter]);
                         neighb_prev_heat_energies[index][cellCounter] = neighbCells[cellCounter].getThermalEnergy();
                     }
                     else
                     {
                         neighbCells[cellCounter] = cell;
                         neighb_prev_heat_energies[index][cellCounter] = 0;
                     }
                 }
                 
                 // Obtaining of heat capacities of "central" cell
                 heatCap = cell.get_specific_heat_capacity();
                
                 // Obtaining of heat capacities of neighbour cells
                 for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                     heatCaps[cellCounter] = neighbCells[cellCounter].get_specific_heat_capacity();
                
                 // Obtaining of temperatures of "central" cell
                 oldTemperature = cell.getTemperature();
                 
                 // Obtaining of temperatures of neighbour cells
                 for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                     temprs[cellCounter] = neighbCells[cellCounter].getTemperature();
                                  
                 heatInflux = 0;
             //    neighbours_in_adj_grains = 0;
                 
                 // Change of temperature of cell depending on index of grain containing neighbour cell
                 //(grain index = -1 if the cell does not belong to one of grains)                 
                 for(int neighbCounter = 0; neighbCounter < neighbCells.length; neighbCounter++)
                 {
                   // Obtaining of index of adjacent grain containing this neighbour cell
                   neighbCellGrainIndex = neighbCells[neighbCounter].getGrainIndex();
                   
                   // Obtaining of type of adjacent grain containing this neighbour cell
                   neighb_type = neighbCells[neighbCounter].getGrainType();
                   
                   neighbour_heating = (neighb_type!=Common.ADIABATIC_ADIABATIC_CELL)&
                                       (neighb_type!=Common.STRESS_ADIABATIC_CELL)&
                                       (neighb_type!=Common.STRAIN_ADIABATIC_CELL);
                   
                   inner_neighbour = (neighb_type==Common.INNER_CELL)|(neighb_type==Common.LAYER_CELL);

                   inner_cell      = (cell_type==Common.INNER_CELL)|(cell_type==Common.LAYER_CELL);

                   outer_interacted_neighbour = neighbour_heating&(!inner_neighbour);
                                              
                   outer_interacted_cell      = cell_heating&(!inner_cell);
                   
                   if(bound_interaction_type == Common.FROM_BOUNDARY_TO_NONE_NEIGHBOURS)
                       heat_exchange = (inner_cell&inner_neighbour)|
                                       (inner_cell&outer_interacted_neighbour);

                   if(bound_interaction_type == Common.FROM_BOUNDARY_TO_INNER_NEIGHBOURS)
                       heat_exchange = (inner_cell&inner_neighbour)|
                                       (inner_cell&outer_interacted_neighbour)|
                                       (outer_interacted_cell&inner_neighbour);

                   if(bound_interaction_type == Common.FROM_BOUNDARY_TO_ALL_NEIGHBOURS)
                       heat_exchange = (inner_cell&inner_neighbour)|
                                       (inner_cell&outer_interacted_neighbour)|
                                       (outer_interacted_cell&inner_neighbour)|
                                       (outer_interacted_cell&outer_interacted_neighbour);
                   
                   cell_euler_angles = cell.getEulerAngles();
                   nghb_euler_angles = neighbCells[neighbCounter].getEulerAngles();
                   
                   if(heat_exchange)
                   {
                     if((neighbCellGrainIndex==cell.getGrainIndex())|!inner_neighbour|!inner_cell)
                     {
                       // Obtaining of heat conductivities of "central" cell
                       heatCond = cell.get_thermal_conductivity();
                
                       // Obtaining of heat conductivities of neighbour cell in the same grain
                       heatConds[neighbCounter] = neighbCells[neighbCounter].get_thermal_conductivity();                     
                     
                       // Calculation of heat influx to the "central" cell from neighbour cells
                       heatInflux += (0.5/neighb1S_number)*(cell_surface/get_cell_size_X())*Math.sqrt(heatConds[neighbCounter]*heatCond)*
                                    (temprs[neighbCounter]-oldTemperature)*time_step;
                     }
                     else                 
                     { 
                    //   angle_diff = Math.abs(cell.getEulerAngles()[0]-neighbCells[neighbCounter].getEulerAngles()[0]);

                       // Calculation of difference of angles of considered and neighbour cells
                    //   angle_diff =(Math.abs(cell_euler_angles[0] - nghb_euler_angles[0])+
                      //              Math.abs(cell_euler_angles[1] - nghb_euler_angles[1])+
                        //            Math.abs(cell_euler_angles[2] - nghb_euler_angles[2]))/3;
                       
                       angle_diff = Math.abs(cell_euler_angles[0] - nghb_euler_angles[0]);
                    
                       // Obtaining of heat conductivities of "central" cell
                    //   heatCond = cell.get_thermal_conductivity()*Math.exp(cell.get_phonon_portion()*angle_diff);
                
                       // Obtaining of heat conductivities of neighbour cell in adjacent grain
                    // heatConds[neighbCounter] = neighbCells[neighbCounter].get_thermal_conductivity()*
                   //                             Math.exp(-neighbCells[neighbCounter].get_phonon_portion()*angle_diff);
                       
                       heatConds[neighbCounter] = Math.sqrt(cell.get_thermal_conductivity()*neighbCells[neighbCounter].get_thermal_conductivity())*
                                                  Math.exp(-0.5*(cell.get_phonon_portion() + neighbCells[neighbCounter].get_phonon_portion())*angle_diff);
                
                     
                       // Calculation of heat influx to the "central" cell from neighbour cells
                    //   heatInflux = heatInflux + (0.5/neighb1S_number)*(cell_surface/get_cell_size_X())*(heatConds[neighbCounter]+heatCond)*
                     //               (temprs[neighbCounter]-oldTemperature)*time_step;
                       
                       heatInflux += (0.5/neighb1S_number)*(cell_surface/get_cell_size_X())*heatConds[neighbCounter]*(temprs[neighbCounter]-oldTemperature)*time_step;

                       // TEST
                    //   System.out.println(cell.getGrainIndex()+" "+neighbCellGrainIndex+" "+
                      //                    cell.getEulerAngles()[0]+" "+neighbCells[neighbCounter].getEulerAngles()[0]+" "+angle_diff);
                       
                   //    neighbours_in_adj_grains++;
                     }
                   }
                 }
                 
                 // Calculation of new temperature of the "central" cell
                 newTemperature = oldTemperature + heatInflux/(heatCap*getCellVolume()*cell.get_density());
                 
                 // Current heat influx for the cell
              //   heat_influxes[index] = heatInflux;
                 
                 // Addition of heat influx to the cell
                 cell.addHeatInflux(heatInflux);
                
                 // Creation of cell with new temperature and addition of this cell 
                 // to list of new cells
                 newCell = new RecCellR3(cell);
                            
           //      newCell.setInitialTemperature(oldTemperature);
                 newCell.setTemperature(newTemperature);

          //       newCell.calcThermalEnergy();
                
                 newCells.set(index, newCell);
              }
            }
            
            //TEST
     //       System.out.println();

            // Type of grain containing current cell
            int grain_type;

            int neighb_index;

            RecCellR3 neighb_cell;
            
            // Total heat energy influx to inner cells
            current_inner_heat_influx = 0;
            
            // Total heat energy influx to outer cells
            current_outer_heat_influx = 0;

            // Spatial cycle over automata located in this micro specimen 
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {   
              // Calculation of single index of the cell with given indices.
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
                
              // Change of cell properties.
              cell = (RecCellR3)newCells.get(index);
                
              if(cell.getGrainIndex()>0)
              {
                // Previous value of cell thermal energy
                oldCellThermalEnergy = cell.getThermalEnergy();

                // Previous value of cell mechanical energy
                oldCellMechEnergy = cell.getMechEnergy();
                
                // Calculation of new value of thermal energy of cell
                cell.calcThermalEnergy();

                // Calculation of new values of strain, thermal energy and temperature of cell
                // as a result of heat expansion
                if(simulate_heat_expansion==1)
                {
                  //  if(cell_type == Common.INNER_CELL | cell_type == Common.OUTER_CELL)
                    {
                        cell.calcStrainT();
                    }
                    
                    // Should be optimized!!!


                    /*
                    displ_vector = new VectorR3();
                    
                    for(int neighb_counter = 0; neighb_counter<neighb1S_number; neighb_counter++)
                    {
                        neighb_index = neighbours3D[index][neighb_counter];

                        if(neighb_index>-1)
                        {
                            neighb_cell = (RecCellR3)newCells.get(neighb_index);

                            cell_coord = cell.getCoordinates();
                            neighb_coord = neighb_cell.getCoordinates();

                            // Calculation of unit normal vector for boundary of current cell and its neighbour
                            bound_vect = residial(neighb_coord, cell_coord);

                            // Calculation of displacement vector for boundary of current cell and its neighbour
                            bound_vect.multiply(cell.getStrain() - neighb_cell.getStrain());

                            // Addition of boundary displacement vector to summary displacement vector of current cell
                            displ_vector.add(bound_vect);

                            // Point at centre of boundary facet of current neighbour cells
                            bound_point = new PointR3(0.5*(cell_coord.getX()+neighb_coord.getX()),
                                                      0.5*(cell_coord.getY()+neighb_coord.getY()),
                                                      0.5*(cell_coord.getZ()+neighb_coord.getZ()));

                            // Topos consisting of boundary point and vector of normal displacement of boundary
                            bound_norm_topos = new ToposR3(bound_point, bound_vect);

                            // Addition of topos to the list
                            bound_norm_toposes.add(bound_norm_topos);
                        }
                    }
                    
                    cell.setDisplVector(displ_vector);
                    */
                }

                // Calculation of new value of stress of cell
             //   cell.calcMechStressAndStrain();

                // Setting of new temperature of cell as its initial temperature at
                // next time step
                cell.setInitialTemperature(cell.getTemperature());

                grain_type = cell.getGrainType();
                
                // Determination of state of inner or layer cell
                if((grain_type == Common.INNER_CELL)|
                   (grain_type == Common.LAYER_CELL))
                    cell.determineState();
                
                // Obtaining of grain containing the cell
                grain = (Cluster)grainsCluster.get(cell.getGrainIndex()-1);
                
                // Change of grain thermal energy
                grain.addThermalEnergy(cell.getThermalEnergy()-oldCellThermalEnergy);
                
                // Change of grain mechanical energy
                grain.addElasticEnergy(cell.getMechEnergy()-oldCellMechEnergy);
                
                if(grain_type == Common.INNER_CELL | grain_type == Common.LAYER_CELL)
                {
                    current_inner_heat_influx += cell.getHeatInflux();
              //      inner_cell_number++;
                }
                else
                if(grain_type != Common.ADIABATIC_ADIABATIC_CELL)
                {
                    current_outer_heat_influx += cell.getHeatInflux();
               //     outer_interact_cell_number++;
                }
                
                cell.setHeatInflux(0);
                
                //TEST
          //      if(indexK == 2)if(indexJ == 25)if(indexI == 25)
          //          System.out.println("Cell #"+index+": Tn-1 = "+cell.getInitialTemperature()+" Tn = "+cell.getTemperature()+" Thermal energy: "+cell.getThermalEnergy());
                
                // Replacing of the cell with single index in list of cells
                // by the same cell with changed temperature                
                set(index, cell);
              }
            }
            
            // Calculation of total influxes of heat energy
            total_inner_heat_influx += current_inner_heat_influx;
            total_outer_heat_influx += current_outer_heat_influx;
            
            // Probabilistic change of states of cells
            // Printing of parameters of cells to file
            for(int cellCounter = 0; cellCounter < size(); cellCounter++)
            {                   
                cell = (RecCellR3)get(cellCounter);
                
                // Indices of cell
                cell_indices = cell.getIndices();
                
                indexI = cell_indices.getI();
                indexJ = cell_indices.getJ();
                indexK = cell_indices.getK();
         
        /*        
                state = cell.getState();
                                
                // Current value of cell strain.
                strain = cell.calcStrainT();
        
                // Value of material yield strain of cell.
                yieldStrain = cell.get_yieldStrain();
        
                // Probability of switching.
                probSwitch = 0.0;
        
                // Calculation of switch probability for bulk.
                if(state == 0)
                {            
                    probSwitch = Math.exp(-yieldStrain/strain);
                    
                    // Determination of cell state.
                    rand = Math.random();
        
                    if (rand < probSwitch)
                    {
                        cell.setState((byte)1);
                    }
                }
                
                // Calculation of switch probability for interlayer.
                if(state == 2)
                {            
                    probSwitch = Math.exp(-yieldStrain/strain);
                    
                    // Determination of cell state.
                    rand = Math.random();
        
                    if (rand < probSwitch)
                    {
                        cell.setState((byte)3);
                    }
                }
                
                // Calculation of switch probability for surface.
                if(state == 4)
                {            
                    probSwitch = Math.exp(-yieldStrain/strain);
                    
                    // Determination of cell state.
                    rand = Math.random();
        
                    if (rand < probSwitch)
                    {
                        cell.setState((byte)5);
                    }
                }    
     */            
                        
                // Writing of cell temperatures and states to file.        
                
          //    if((indexI > 0)&(indexI < elementSizeI-1))
          //    if((indexJ > 0)&(indexI < elementSizeJ-1))
                if(writeToFile)
                if(stepCounter == heat_step_number-1)
          //    if((indexK > 0)&(indexK < elementSizeK-1))
                if(cell.getGrainIndex()>0)
                try
                {                    
                 // bw.write(indexI+".5 "+indexJ+".5 "+indexK+".5 "+cell.getTemperature());
                 // bw.write(indexI+".5 "+indexJ+".5 "+cell.getTemperature()+" "+cell.getMechEnergy());
                    
                    writeToFilePoints(bw, cell_indices, ((Long)Math.round(cell.getTemperature())).intValue());
               //     bw.write(cell.getTemperature()+"");
               //     bw.write(" "+cell.getMechEnergy());
                    bw.write(" "+cell.getGrainType()+" "+cell.getType());
                    bw.newLine();
                    bw.flush();
                    
    //                if((indexK > 0) & (indexK < elementSizeK-1))
    //                  bw1.write(indexI+".5 "+indexJ+".5 "+(indexK-1)+".5 "+cell.getState());
                    
    //                bw1.newLine();
    //                bw1.flush();
                    
                }
                catch(IOException io_exc)
                {
                    System.out.println("Error: " + io_exc);
                }
                
                // Calculation of single index of the cell with given indices.
            //    index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
                
                // Replacing of the cell with given single index in list of cells 
                // by the same cell with changed temperature and state.     
            //    set(index, cell);
            }
       /*                 
            if(writeToFile)
            try
            {
                setStates(writeFileName+"_"+(stepCounter+1)+"_");
         //       bw.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }   
        */
            if(writeToFile)
            if(stepCounter == heat_step_number-1)
            try
            {                
                bw.close();
//              bw1.close();
            }        
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
        }
        // FINISH OF "HEAT" TIME STEPS
    }
    
    /** The method performs flows of mechanical energy between cells located in given space region.
     * The information about cell parameters can be written to the file.
     * @param writeFileName name of file for writing
     * @param writeToFile if true then data of simulation are written to file
     * @param calc_bound_parameters logical variable responsible for calculation of parameters (stress, velocity) of cell boundary
     * @param step_counter number of time step
     */
    public void calcMechEnergyFlows(String writeFileName, boolean writeToFile, boolean calc_bound_parameters, long step_counter)
    {
        /* Block 14 */
        
        if(writeToFile)
        try
        {
            bw  = new BufferedWriter(new FileWriter(writeFileName+"_"+(step_counter+1)+"."+Common.STRESSES_FILE_EXTENSION));
       //   bw1 = new BufferedWriter(new FileWriter(writeFileName+"_"+(step_counter+1)+"_states_points.txt"));
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        
        // Local variables
    //    double oldCellMechEnergy;
        int grain_index = -1;
        int neighb_cell_index;
        Three neighb_cell_indices;
        int neighbCellGrainIndex = -1;
        byte neighb_cell_type = -1;
        double boundary_temperature;
        double cell_stress, neighb_stress = 0;
        byte cell_grain_type;
        double displ=0;
        
        int cell_number = elementSizeI*elementSizeJ*elementSizeK;

     // mechInfluxes = new double[elementSizeI*elementSizeJ*elementSizeK];

        // Array of strains of cells
        strains      = new double[cell_number];
        
     // boolean var1, var2, var3;

        boolean neighbour_loading, cell_loading,
                inner_cell, outer_interacted_cell,
                inner_neighbour, outer_interacted_neighbour;
        
        boolean mech_energy_exchange = false;
        
        // Moduli of elasticity of neighbour cells
        double mod_elast, neighb_mod_elast;
        
        // Clearing of list of boundary toposes
       // bound_norm_toposes.clear();
        
     //   int cell_bound_counter = 0;
        
        // Boundary displacement vector for SCP
        byte[] unit_bound_norm_vector = new byte[3];
        
        // Cosinus of angle between vector of energy flow and anisotropy vector
        double cosinus;
        
        // Array of toposes normal to cell facets
        double[] bound_topos = new double[6];
            
        // Stresses of neighbour cells
        double[] stresses = new double[neighb1S_number];
        
        // Neighbour cell
        RecCellR3 neighb_cell;

        // Index of neighbour cell
        int neighb_index;
        
        // Integer corresponding to indices of central and neighbour cells
        int pair_integer;
        
        // Integer corresponding to indices of neighbour and central cells
        int pair_integer_2;
        
        // Index of cell boundary
        int cell_bound_index = -1;                    

        // cell boundary
     //   CellBoundary cell_bound;
        
        // Array of stresses at cell boundaries
        double[] bound_stresses;
        
        // Array of strain velocities at cell boundaries
        double[] bound_velocities;
        
        // List of values of misorientation angles
        ArrayList angle_diffs = new ArrayList(0);
        
     //   boolean calc_bound_parameters = false;// true;//
        
        // Maximal and minimal values of mechanical energy for neighbour cells at previous time step
        double prev_max_neighb_mech_energy = -1.0E308;
        double prev_min_neighb_mech_energy =  1.0E308;
        
        // Maximal and minimal values of mechanical energy for neighbour cells at current time step
        double max_neighb_mech_energy = -1.0E308;
        double min_neighb_mech_energy =  1.0E308;        
        
        // mechanical energy of current neighbour cell at current time step
        double neighb_mech_energy  = 0;
        
        // Total number of atoms in pair of interacted cells
        int total_atom_number   = 0;
        
        // Total number of defects in pair of interacted cells
        int total_defect_number = 0;
        
        // number of atoms in each interacted cell
        long cell_atom_number = 0, cell_defect_number = 0;
              
        // number of defects in each interacted cell
        long neighb_atom_number = 0, neighb_defect_number = 0;
        
        // System.out.println("\ndefects_influence = "+defects_influence+"\n");
        
        // START OF "MECHANICAL" TIME STEPS
        for(int stepCounter = 0; stepCounter < mech_step_number; stepCounter++)
        {
          // TEST
          // if(writeToFile)
          //   System.out.println("Time step # "+step_counter+": mechanical energy transfer is simulated...");            
        /*
            if(calc_bound_parameters)
              System.out.println("Cell boundary parameters are calculated...");
            else
              System.out.println("Cell boundary parameters are NOT calculated...");
         */
            // Spatial cycle on automata located in CA micro specimen 
            // Mechanical energy exchange between neighbouring automata
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
           //   cell_bound_counter = 0;

              // Calculation of single index (in ArrayList)
              // of the cell with current triple indices  
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
              
              // Obtaining of cell and its neighbours from list of cells
              cell = (RecCellR3)get(index);
              
              cell_stress = cell.getMechStress();
              
              previous_stresses[index] = cell_stress;
              
              // Previous value of cell mechanical energy
              prev_mech_energies[index] = cell.getMechEnergy();
          /*
              if(index == 23000)//1590)
              {
                  System.out.print("calcMechEnergyFlows: cell index: "+index+"; moment_module = "+cell.getForceMoment().getLength());
                  System.out.println("; energyHAGB: "+cell.getEnergyHAGB());
              }
           */
              displ_vector.setX(0);
              displ_vector.setY(0);
              displ_vector.setZ(0);
              
              cell.setDisplVector(displ_vector);
              
              // mechInfluxes[index] = 0;
              strains[index] = 0;
              
              cell_grain_type = cell.getGrainType();
              
              cell_loading = (cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
                            &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                            &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL);
              
              // Index of grain containing this cell
              grain_index = cell.getGrainIndex();
              
              // TEST
           //   if(cell_stress != 0 & index == 2701 & (cell_grain_type == Common.INNER_CELL | cell_grain_type == Common.LAYER_CELL))
           //   if(index == 2701)
           //       System.out.println("Cell # " + index+" ("+indexI+", "+indexJ+", "+indexK+") is loaded!!! cell_loading = "+cell_loading+
           //                          "; stress = "+cell_stress+"; grain_index = "+grain_index+"; cell_grain_type = "+cell_grain_type);
              
              if(cell_loading & grain_index > 0)
              {
                //  System.out.println("cell_grain_type = "+cell_grain_type);

                // Single indices of neighbours at 1st coordination sphere
                neighbIndices = neighbours1S[index];

                // Cell temperature
                temperature = cell.getTemperature();
                
                // Obtaining of grain containing considered cell
                grain = (Cluster)grainsCluster.get(grain_index-1);
                
                // Obtaining of grain containing considered cell for consequence of time steps                 
                newGrain = (Cluster)newGrainsCluster.get(grain_index-1);
                
                // Obtaining of transition limit to high angle grain boundary regime
                angleHAGB  = grain.getAngleLimitHAGB();
                energyHAGB = grain.getEnergyHAGB();
                
                // TEST
             //   System.out.println("angleHAGB = "+angleHAGB+"; energyHAGB = " + energyHAGB);
                
                // Calculation of velocities of mechanical energy flows from/to neighbour cells
                for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                {
                  neighb_cell_index = neighbours1S[index][neighbCounter];

                  if((neighb_cell_index>-1)&(neighb_cell_index<size()))
                  {
                    //TEST
                    // System.out.println(index+" "+mechInflux[index]);

                    // Obtaining of neighbour cell
                    neighbCell = (RecCellR3)get(neighb_cell_index);
                    
                    // mechanical energy of current neighbour cell at previous time step
                    neighb_prev_mech_energies[index][neighbCounter] = neighbCell.getMechEnergy();
                    
                    // Obtaining of index of grain containing this neighbour cell
                    neighbCellGrainIndex = neighbCell.getGrainIndex();
                    
                    // Obtaining of type of neighbour cell
                    neighb_cell_type = neighbCell.getGrainType();
                    
                    // Indices of neighbour cell
                    neighb_cell_indices = calcTripleIndex(neighb_cell_index, cellNumberI+2, cellNumberJ+2, cellNumberK+2);
                    
                    neighbour_loading = (neighb_cell_type!=Common.ADIABATIC_ADIABATIC_CELL)&
                                        (neighb_cell_type!=Common.ADIABATIC_TEMPERATURE_CELL)&
                                        (neighb_cell_type!=Common.ADIABATIC_THERMAL_CELL);
                    
                    inner_neighbour = (neighb_cell_type==Common.INNER_CELL)|(neighb_cell_type==Common.LAYER_CELL);

                    inner_cell      = (cell_grain_type==Common.INNER_CELL)|(cell_grain_type==Common.LAYER_CELL);

                    outer_interacted_neighbour = neighbour_loading&(!inner_neighbour);

                    outer_interacted_cell      = cell_loading&(!inner_cell);

                    if(bound_interaction_type == Common.FROM_BOUNDARY_TO_NONE_NEIGHBOURS)
                        mech_energy_exchange = (inner_cell&inner_neighbour)|
                                               (inner_cell&outer_interacted_neighbour);

                    if(bound_interaction_type == Common.FROM_BOUNDARY_TO_INNER_NEIGHBOURS)
                        mech_energy_exchange = (inner_cell&inner_neighbour)|
                                               (inner_cell&outer_interacted_neighbour)|
                                               (outer_interacted_cell&inner_neighbour);

                    if(bound_interaction_type == Common.FROM_BOUNDARY_TO_ALL_NEIGHBOURS)
                        mech_energy_exchange = (inner_cell&inner_neighbour)|
                                               (inner_cell&outer_interacted_neighbour)|
                                               (outer_interacted_cell&inner_neighbour)|
                                               (outer_interacted_cell&outer_interacted_neighbour);
                    
                    // TEST
                 //   System.out.println("Cell # "+index+". Nghb. # "+neighb_cell_index+". mech_energy_exchange = "+mech_energy_exchange+". indexI = "+indexI);

                    if(mech_energy_exchange & neighbCellGrainIndex > 0)
                    {
                      // TEST
                   //   System.out.println("mech_energy_exchange = "+mech_energy_exchange);
                   //   System.out.println("Cell # " + index+" interacts with its neighbour with index "+neighb_cell_index);
                      
                      // Obtaining of grain containing neighbour cell
                      neighbGrain = (Cluster)grainsCluster.get(neighbCellGrainIndex-1);
                      
                      if(inner_cell&inner_neighbour)
                      {
                        // Calculation of difference of angles of considered and neighbour cells
                        //  angle_diff = Math.abs(grain.getAngle1() - neighbGrain.getAngle1());
                        if(sum_of_abs_values_of_angles)
                        {
                          // general case
                        //  angle_diff = (Math.abs(grain.getAngle1() - neighbGrain.getAngle1())+
                          //              Math.abs(grain.getAngle2() - neighbGrain.getAngle2())+
                            //            Math.abs(grain.getAngle3() - neighbGrain.getAngle3()))/3;
                          
                          angle_diff = Math.abs(grain.getAngle1() - neighbGrain.getAngle1());
                        }
                        else
                        {            
                          // case of triple conjunction of grains
                          angle_diff = (Math.abs(grain.getAngle1() - neighbGrain.getAngle1())+
                                        Math.abs(grain.getAngle2() - neighbGrain.getAngle2()))*
                                        Math.abs(grain.getAngle3() - neighbGrain.getAngle3())/2;
                        }                    
                      
                        // TEST
                        /*
                        if(step_counter == 10 | step_counter%10000 == 0)
                        if(!angle_diffs.contains(angle_diff))
                        {
                            angle_diffs.add(angle_diff);
                            System.out.println("Grains ## "+grain_index+" and "+neighbCellGrainIndex+" have misorientation angle "+angle_diff);
                        }
                        */
                        // END OF TEST
                      
                        //Obtaining of maximal value of grain boundary mobility
                        //    mechMaxMobility = response_rate/grain.getModElast();
                        //    mechMaxMobility = 2*response_rate/(grain.getModElast()+neighbGrain.getModElast());
                        
                        mod_elast = grain.getModElast();
                        neighb_mod_elast = neighbGrain.getModElast();
                        
                        if((mod_elast == 0)|(neighb_mod_elast == 0))
                            mechMaxMobility = 0;
                        else
                        {
                          if(mod_elast == neighb_mod_elast)
                            mechMaxMobility = response_rate/mod_elast;
                          else
                            mechMaxMobility = (response_rate/Math.sqrt(mod_elast*neighb_mod_elast))*
                                      Math.exp(-(mod_elast-neighb_mod_elast)*(mod_elast-neighb_mod_elast)/(mod_elast*neighb_mod_elast));
                        }
                        
                        if(angle_diff == 0)// | neighbCell.getGrainType()==Common.LAYER_CELL)
                        {
                          neighb_stress = neighbCell.getMechStress();
                          motive_force = neighb_stress - cell_stress;
                          
                          // TEST
                       //   if(cell_stress != 0.0 | neighb_stress != 0.0 | neighb_prev_mech_energies[index][neighbCounter] != 0.0)
                       //     System.out.println("Cell # "+index+". Nghb. # "+neighb_cell_index+". mech_energy_exchange = "+mech_energy_exchange+". indexI = "+indexI+
                       //                        "; cell_stress = "+cell_stress+"; neighb_stress = "+neighb_stress+"; neighb_mech_energy = "+neighb_prev_mech_energies[index][neighbCounter]);
                          
                          if(defects_influence == (byte)1 & grain_index != neighbCellGrainIndex)
                          {
                            boundary_temperature = (neighbCell.getTemperature() + temperature)/2;
                          
                            specBoundEnergy = 0;
                            mobility_exponent = 0;
                          
                            cell_atom_number   = cell.getAtomNumber();
                            cell_defect_number = cell.getDefectNumber();
                          
                            neighb_atom_number   = neighbCell.getAtomNumber();
                            neighb_defect_number = neighbCell.getDefectNumber();
                          
                      //    total_atom_number   = cell_atom_number + neighb_atom_number;
                      //    total_defect_number = cell.getDefectNumber() + neighbCell.getDefectNumber();
                          
                            if(cell_atom_number > 0 | neighb_atom_number > 0)
                            {
                              if(cell_defect_number > 0 | neighb_defect_number > 0)
                              {
                                  if(cell_atom_number > 0 & cell_defect_number > 0)
                                      mobility_exponent += - cell_defect_number*Math.log((cell_atom_number*1.0)/cell_defect_number);
                                  else
                                      mobility_exponent += 0;
                                  
                                  if(neighb_atom_number > 0 & neighb_defect_number > 0)
                                      mobility_exponent += neighb_defect_number*Math.log((neighb_atom_number*1.0)/neighb_defect_number);
                                  else
                                      mobility_exponent += 0;
                              }
                              else
                                  mobility_exponent = 0;
                              
                              if(boundary_temperature!=0)
                              {
                                  mobility_exponent =  mobility_exponent/(Task.BOLTZMANN_CONST*boundary_temperature);
                                  
                                  // Calculation of velocity of grain boundary movement
                                  velocity = mechMaxMobility*motive_force*Math.pow(Math.E, mobility_exponent);
                              }
                              else
                                  velocity = 0;
                            }
                            else
                            {
                         //     mobility_exponent = 0;
                              
                         //     boundary_temperature = (neighbCell.getTemperature() + temperature)/2;
                              
                              cell.setState    (Common.DAMAGED_CELL);
                              cell.setGrainType(Common.OUTER_CELL);
                              
                              neighbCell.setState    (Common.DAMAGED_CELL);
                              neighbCell.setGrainType(Common.OUTER_CELL);
                              
                              velocity = 0;
                            }
                          }
                          else
                          {
                        //      mobility_exponent = 0;
                              
                        //      boundary_temperature = (neighbCell.getTemperature() + temperature)/2;                              
                              velocity = mechMaxMobility*motive_force;
                          }
                        //  System.out.println("angleHAGB = "+angleHAGB+"; angle_diff = "+angle_diff+"; energyHAGB = " + energyHAGB);
                        
                        // TEST
                     //   if(motive_force != 0.0)
                     //     System.out.println("Cell # "+index+". Nghb. # "+neighb_cell_index+". mech_energy_exchange = "+mech_energy_exchange+". motive_force = "+motive_force+". indexI = "+indexI);
                          
                        }
                        else
                        if(angle_diff<= angleHAGB)
                        {
                          energyHAGB = Math.sqrt(cell.getEnergyHAGB() * neighbCell.getEnergyHAGB());
                          
                          //Calculation of specific energy of boundary between grains
                          specBoundEnergy = (energyHAGB*angle_diff/angleHAGB)*(1 - Math.log(angle_diff/angleHAGB));// 0;// 
                          
                          neighb_stress = neighbCell.getMechStress();
                          motive_force  = neighb_stress - cell_stress;
                          
                          mobility_exponent = 0;
                          
                          cell_atom_number   = cell.getAtomNumber();
                          cell_defect_number = cell.getDefectNumber();
                          
                          neighb_atom_number   = neighbCell.getAtomNumber();
                          neighb_defect_number = neighbCell.getDefectNumber();
                          
                        //  total_atom_number   = cell.getAtomNumber()   + neighbCell.getAtomNumber();
                        //  total_defect_number = cell.getDefectNumber() + neighbCell.getDefectNumber();
                          
                          boundary_temperature = (neighbCell.getTemperature() + temperature)/2;
                          
                          mobility_exponent += -(getCellSurface()/neighb1S_number)*specBoundEnergy;
                          
                          // TEST
                        //  if(motive_force != 0.0)
                        //    System.out.println("Cell # "+index+". Nghb. # "+neighb_cell_index+". mech_energy_exchange = "+mech_energy_exchange+". motive_force = "+motive_force+". indexI = "+indexI);
                          
                          if(defects_influence == (byte)1)
                          {
                            if(cell_atom_number > 0 | neighb_atom_number > 0)
                            {
                              if(cell_defect_number > 0 | neighb_defect_number > 0)
                              {
                                  if(cell_atom_number > 0 & cell_defect_number > 0)
                                      mobility_exponent += - cell_defect_number*Math.log((cell_atom_number*1.0)/cell_defect_number);
                                  else
                                      mobility_exponent += 0;
                                  
                                  if(neighb_atom_number > 0 & neighb_defect_number > 0)
                                      mobility_exponent += neighb_defect_number*Math.log((neighb_atom_number*1.0)/neighb_defect_number);
                                  else
                                      mobility_exponent += 0;
                              }
                              else
                                  mobility_exponent = 0;
                              
                              if(boundary_temperature!=0)
                              {    
                              //    mobility_exponent = Math.min(0, mobility_exponent)/(Task.BOLTZMANN_CONST*boundary_temperature);
                                  
                                  mobility_exponent = mobility_exponent/(Task.BOLTZMANN_CONST*boundary_temperature);
                                  
                                  // Calculation of velocity of grain boundary movement
                                  velocity = mechMaxMobility*motive_force*Math.pow(Math.E, mobility_exponent);
                                      
                              }
                              else
                                  velocity = 0;
                            }
                            else
                            {
                           //   mobility_exponent = 0;
                              
                              cell.setState    (Common.DAMAGED_CELL);
                              cell.setGrainType(Common.OUTER_CELL);
                              
                              neighbCell.setState    (Common.DAMAGED_CELL);
                              neighbCell.setGrainType(Common.OUTER_CELL);
                              
                              velocity = 0;
                            }
                          }
                          else
                          {
                              if(boundary_temperature!=0)
                              {
                               //   mobility_exponent = Math.min(0, mobility_exponent)/(Task.BOLTZMANN_CONST*boundary_temperature);
                                  
                                  mobility_exponent = mobility_exponent/(Task.BOLTZMANN_CONST*boundary_temperature);
                              
                                  // Calculation of velocity of grain boundary movement
                                  velocity = mechMaxMobility*motive_force*Math.pow(Math.E, mobility_exponent);
                              }
                              else
                                  velocity = 0;
                          }
                        }
                        else
                        {
                          velocity = 0;
                          
                        //  System.out.println("angleHAGB = "+angleHAGB+"; angle_diff = "+angle_diff+"; energyHAGB = " + energyHAGB);
                        }
                      }
                      else
                      {
                          neighb_stress = neighbCell.getMechStress();
                          motive_force = neighb_stress - cell_stress;
                          
                          specBoundEnergy = 0;
                          mobility_exponent = 0;

                          velocity = mechMaxMobility*motive_force;
                      }
                    }
                    else
                    {
                      velocity = 0;
                      motive_force = 0;
                    }
                    
                  //  if(motive_force != 0.0)
                  //    System.out.println("Cell # "+index+". Nghb. # "+neighb_cell_index+". mech_energy_exchange = "+mech_energy_exchange+". velocity = "+velocity+
                  //                       ". motive_force = "+motive_force+". indexI = "+indexI);
                    
                    // Calculation of displacement of central cell determined by interaction with current neighbour
                    displ = velocity*time_step;
                        
                    // Calculation of boundary displacement vector for SCP and HCP
                    if(false)//(packing_type == Common.SIMPLE_CUBIC_PACKING)//
                    {
                        // Calculation of boundary displacement vector for SCP
                        unit_bound_norm_vector = calcUnitBoundNormVectorSCP(index, neighb_cell_index);

                        // TEST
                  //      System.out.println("Unit vector coordinates: "+ unit_bound_norm_vector[0]+" "+unit_bound_norm_vector[1]+" "+unit_bound_norm_vector[2]);
                        
                        bound_vect.setX(displ*unit_bound_norm_vector[0]);
                        bound_vect.setY(displ*unit_bound_norm_vector[1]);
                        bound_vect.setZ(displ*unit_bound_norm_vector[2]);
                    }
                    else
                    {
                        // Calculation of boundary displacement vector for HCP

                        // Coordinates of central cell (in cell sizes)
                        cell_coord = cell.getCoordinates();
                        
                        // Coordinates of neighbour cell (in cell sizes)
                        neighb_coord = neighbCell.getCoordinates();

                        // Calculation of unit normal vector for boundary of current cell and its neighbour
                        bound_vect = residial(neighb_coord, cell_coord);
                        
                        if(anisotropy)
                        {
                            //  Calculation of cosinus of angle formed by vector from considered cell
                            // to neighbour cell in considered cluster and vector of outer anisotropy
                            cosinus = cosinus(bound_vect, anis_vector); 
                            
                            // Redetermination of displacement according to anisotropy vector
                            // displ = displ*Math.pow(coeff_anisotropy, cosinus-1);
                            displ = displ*Math.pow(coeff_anisotropy, Math.abs(cosinus)-1);
                        }
                        
                        // Calculation of displacement vector for boundary of current cell and its neighbour
                        bound_vect.multiply(displ);

                        // TEST
                   //     System.out.println("bound_vect = "+bound_vect.getX()+" "+bound_vect.getY()+" "+ bound_vect.getZ());
                    }

                    // Addition of boundary displacement vector to summary displacement vector of current cell
                    displ_vector.add(bound_vect);
                        
                    // Point at centre of boundary facet of current neighbour cells
                    bound_point.setX(0.5*(cell_coord.getX()+neighb_coord.getX()));
                    bound_point.setY(0.5*(cell_coord.getY()+neighb_coord.getY()));
                    bound_point.setZ(0.5*(cell_coord.getZ()+neighb_coord.getZ()));
                    
                    // Topos consisting of boundary point and vector of normal displacement of boundary
                    bound_norm_topos = new ToposR3();
                    bound_norm_topos.setStartPointR3(bound_point);
                    bound_norm_topos.setVectorR3(bound_vect);                    

                    if(step_counter == 0)
                    {
                        // Addition of topos to the list
                        bound_norm_toposes[index] = new ArrayList(0);
                        
                        for (int topos_counter = 0; topos_counter<neighb1S_number; topos_counter++)
                            bound_norm_toposes[index].add(bound_topos);
                    }
                    
                    //   Obtaining of cell boundary from the list and
                    // setting of stress and velocity of deformation front of cell boundary
              /*
               *    if(neighb_cell_index > -1)
                    {
                        pair_integer   = index*superior_integer + neighb_cell_index;
                        pair_integer_2 = neighb_cell_index*superior_integer + index;
                        
                        cell = (RecCellR3)get(index);
                        neighb_cell = (RecCellR3)get(neighb_cell_index);
                        
                        // Obtaining of cell boundary from the list
                        if(cell_pairs.contains(pair_integer))
                        {
                            cell_bound_index = cell_pairs.indexOf(pair_integer);
                        }
                        
                        if(cell_pairs.contains(pair_integer_2))
                        {
                            cell_bound_index = cell_pairs.indexOf(pair_integer_2);
                        }
                        
                        cell_bound = (CellBoundary)cell_bounds.get(cell_bound_index);
                        
                        // Setting of stress and velocity of deformation front of cell boundary
                        cell_bound.setStress(motive_force);
                        cell_bound.setVelocity(velocity);
                        
                        cell_bounds.set(cell_bound_index, cell_bound);
                        
                        if(cell_bound_index == 15)
                            System.out.println("step_counter: "+(step_counter+1)+"; cell_bound_count: "+cell_bound_index+"; cell_stress: "+cell.getMechStress()+"; neighb_cell_stress: "+neighb_cell.getMechStress()+"; bound_stress: "+motive_force);
                    }
              */
                    if(calc_bound_parameters)
                    {
                        if(velocity == 0)
                            motive_force = 0;
                    /*
                        // Writing of current boundary stress to array of cell boundary stresses
                        cell_bound_stresses[index][neighbCounter] = motive_force;
   
                        // Writing of current boundary velocity to array of cell boundary velocities
                        cell_bound_velocities[index][neighbCounter] = velocity;       
                    */
                        // Setting of stress and strain velocity at cell boundary
                   //     bound_stresses   = cell.getBoundStresses();
                   //     bound_velocities = cell.getBoundVelocities();
                    
                  //      bound_stresses[neighbCounter]   = motive_force;
                  //      bound_velocities[neighbCounter] = velocity;
                    
                        cell.setBoundStress(neighbCounter, motive_force);
                        cell.setBoundVelocity(neighbCounter, velocity);
                    
                        //  TEST
                        /*
                          neighb_stress = neighbCell.getMechStress();
                          motive_force = neighb_stress - cell_stress;
                          
                          if(step_counter == step_number-1)
                          {
                            System.out.println(index+" "+neighb_cell_index+" "+bound_stresses[neighbCounter]+" "+cell_stress+" "+neighb_stress);
                            System.out.println(index+" "+neighb_cell_index+" "+bound_stresses[neighbCounter]+" "+cell.getMechStress()+" "+neighbCell.getMechStress());
                          }
                          */
                        //  END OF TEST
                    }
                            
                    bound_topos[0] = bound_point.getX();
                    bound_topos[1] = bound_point.getY();
                    bound_topos[2] = bound_point.getZ();
                    bound_topos[3] = bound_vect.getX();
                    bound_topos[4] = bound_vect.getY();
                    bound_topos[5] = bound_vect.getZ();
                    
                //    bound_norm_toposes[index].set(neighbCounter, bound_topos);

                    /*
                    if(step_counter == 0)
                    {
                        // Addition of topos to the list
                    //    bound_norm_toposes[index].add(bound_norm_topos);
                        
                        bound_norm_toposes[index] = new ArrayList(0);
                        for (int topos_counter = 0; topos_counter<neighb1S_number; topos_counter++)
                            bound_norm_toposes[index].add(new ToposR3());

                        bound_norm_toposes[index].set(neighbCounter, bound_topos);
                    }
                    else
                    {
                        // Setting of topos to certain position in the list
                        bound_norm_toposes[index].set(neighbCounter, bound_norm_topos);

                        // Counter of cell boundaries
                        cell_bound_counter++;
                    }
                  */

                    // Calculation of strain of central cell determined by interaction with all neighbours
                    strains[index]+= displ/cell_size_X;//1.0E-6;//
                    
                    // Calculation of mechanical flow between central cell and its neihbour
               //     mechInfluxes[index]+= /* 0.5 */ Math.abs(strain)*getCellVolume()*motive_force;//+
                    //                  getCellVolumeHCP()*motive_force*cell.getStrain();
                    
                    //TEST
                    //    if(mechInfluxes[index]!=0)
                    //    System.out.println("mechInflux = "+mechInfluxes[index]);
                    
                //    if(strains[index] != 0 & indexI > 1 & indexI < 23)
                //      System.out.println("Cell # " + index+". Nghb # "+neighb_cell_index+". strain = "+strains[index]+". indexI = "+indexI);
                    
                    if(!inner_neighbour)
                        total_mech_energy_influx += displ/cell_size_X*getCellVolume()*cell.get_mod_elast();
                  }
                }
              }
              else
              {
                  displ = 0;
                  
                  for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                  {
                      neighb_cell_index = neighbours1S[index][neighbCounter];

                      if((neighb_cell_index>-1)&(neighb_cell_index<size()))
                      {
                          // TEST
                       //   System.out.println("neighb_cell_index = "+neighb_cell_index);
                          // END OF TEST
                          
                          // Obtaining of neighbour cell in adjacent grain
                          neighbCell = (RecCellR3)get(neighb_cell_index);
                          
                          // mechanical energy of current neighbour cell at previous time step
                          neighb_prev_mech_energies[index][neighbCounter] = neighbCell.getMechEnergy();                    

                          // Coordinates of central cell
                          cell_coord = cell.getCoordinates();
                          
                          // Coordinates of neighbour cell
                          neighb_coord = neighbCell.getCoordinates();
                          
                          // Calculation of unit normal vector for boundary of current cell and its neighbour
                          bound_vect = residial(neighb_coord, cell_coord);
                          
                          // Calculation of displacement vector for boundary of current cell and its neighbour
                          bound_vect.multiply(displ);
                          
                          // Addition of boundary displacement vector to summary displacement vector of current cell
                          displ_vector.add(bound_vect);
                          
                          // Point at centre of boundary facet of current neighbour cells
                          bound_point.setX(0.5*(cell_coord.getX()+neighb_coord.getX()));
                          bound_point.setY(0.5*(cell_coord.getY()+neighb_coord.getY()));
                          bound_point.setZ(0.5*(cell_coord.getZ()+neighb_coord.getZ()));
                          
                          // Topos consisting of boundary point and vector of normal displacement of boundary
                          bound_norm_topos.setStartPointR3(bound_point);
                          bound_norm_topos.setVectorR3(bound_vect);
                          
                          //   Obtaining of cell boundary from the list and
                          // setting of stress and velocity of deformation front of cell boundary
                      /*
                          if(neighb_cell_index > -1)
                          {
                              pair_integer   = index*superior_integer + neighb_cell_index;
                              pair_integer_2 = neighb_cell_index*superior_integer + index;
                           
                              // Obtaining of cell boundary from the list
                              if(cell_pairs.contains(pair_integer))
                                  cell_bound_index = cell_pairs.indexOf(pair_integer);
                       
                              if(cell_pairs.contains(pair_integer_2))
                                  cell_bound_index = cell_pairs.indexOf(pair_integer_2);
                       
                              cell_bound = (CellBoundary)cell_bounds.get(cell_bound_index);
                        
                              // Setting of stress and velocity of deformation front of cell boundary
                              cell_bound.setStress(0);
                              cell_bound.setVelocity(0);
                        
                              cell_bounds.set(cell_bound_index, cell_bound);
                          }
                       */
                          if(calc_bound_parameters)
                          {
                              motive_force = 0;
                              velocity = 0;
                          /*    
                              // Writing of current boundary stress to array of cell boundary stresses
                              cell_bound_stresses[index][neighbCounter] = motive_force;
   
                              // Writing of current boundary velocity to array of cell boundary velocities
                              cell_bound_velocities[index][neighbCounter] = velocity;   
                          */
                          
                              // Setting of stress at cell boundary
                          /*
                              bound_stresses = cell.getBoundStresses();
                              bound_stresses[neighbCounter] = motive_force;
                          
                              cell.setBoundStresses(bound_stresses);
                          
                              // Setting of strain velocity at cell boundary
                              bound_velocities                = cell.getBoundVelocities();
                              bound_velocities[neighbCounter] = velocity;
                   
                              cell.setBoundVelocities(bound_velocities);
                          */
                              
                              cell.setBoundStress(neighbCounter, motive_force);
                              cell.setBoundVelocity(neighbCounter, velocity);
                          }
                          
                          bound_topos[0] = bound_point.getX();
                          bound_topos[1] = bound_point.getY();
                          bound_topos[2] = bound_point.getZ();
                          bound_topos[3] = bound_vect.getX();
                          bound_topos[4] = bound_vect.getY();
                          bound_topos[5] = bound_vect.getZ();
                          
                     //     bound_norm_toposes[index].set(neighbCounter, bound_topos);

                          /*
                          if(step_counter == 0)
                          {
                              // Addition of topos to the list
                              //    bound_norm_toposes[index].add(bound_norm_topos);
                              bound_norm_toposes[index] = new ArrayList(0);
                              for (int topos_counter = 0; topos_counter<neighb1S_number; topos_counter++)
                                  bound_norm_toposes[index].add(new ToposR3());

                              bound_norm_toposes[index].set(neighbCounter, bound_norm_topos);
                          }
                          else
                          {
                              // Setting of topos to certain position in the list
                              bound_norm_toposes[index].set(neighbCounter, bound_norm_topos);

                              // Counter of cell boundaries
                              cell_bound_counter++;
                          }
                           */
                      }
                  }

            //    mechInfluxes[index] = 0;
                  strains[index] = 0;
                  
                  //  TEST
                  if(displ_vector.getLength() != 0)
                      System.out.println("Cell # "+index+" with grain index "+cell.getGrainIndex()+": ERROR!!! Displacement vector must be equal to 0 but its length equals "+displ_vector.getLength());              
              }
              
             // TEST
             // if(mechInfluxes[index]!=0)
             //   System.out.println("mechInflux = "+mechInfluxes[index]);

              // Setting of displacement vector to cell
              cell.setDisplVector(displ_vector);
            }

            //TEST
     //       System.out.println();
            
            // Setting of zero elastic energies to grains
            for(int grain_counter = 0; grain_counter < grains.length; grain_counter++)
                grains[grain_counter].setElasticEnergy(0);

            // Total energy of cells
     //       double start_total_energy=0, final_total_energy=0, influxSum=0;
              
            // Spatial cycle over cells located in this micro specimen 
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
              // Calculation of single index of the cell with given indices.
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
              
              // Obtaining of cell
              cell = (RecCellR3)newCells.get(index);

              cell_grain_type = cell.getGrainType();

              if((cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
                &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL))
              {
            //      start_total_energy+=cell.getMechEnergy();

                  // Calculation of new value of cell strain
                  cell.addStrain(strains[index]);
                  
                  // Calculation of new value of mechanical energy of cell
                  cell.calcElasticEnergy(strains[index]);
                  
                  // Calculation of total mechanical energy of interacted cells
                  total_mech_energy += cell.getMechEnergy();
                  
                  // TEST
                //  if(strains[index] != 0.0 & cell.getGrainType() == Common.INNER_CELL & index == 2701)
                //    System.out.println("Cell # "+index+"; strain_change = "+strains[index]+"; stress = "+cell.getMechStress());

             //     if(strains[index]!=0)
              //    System.out.println("Elastic energy is changed!!!");
                  
              //    cell.addMechEnergy(mechInfluxes[index]);
             
                  // Calculation of mechanical stress in cell
               //   cell.calcMechStressAndStrain();
                  
                  // Determination of cell state
                  if((cell_grain_type == Common.INNER_CELL)|
                     (cell_grain_type == Common.LAYER_CELL))
                  {
                      cell.determineState();
                      
                      // TEST
                    //  System.out.println("Current cell # " + index+". stress = "+cell.getMechStress()+"; indexI = "+indexI);
                  }
              
                  // Obtaining of grain containing the cell
                  //      grain = (Cluster)grainsCluster.get(cell.getGrainIndex());
                  
                  // Change of grain mechanical energy
                  //      grain.addElasticEnergy(cell.getMechEnergy());
                  
                  if(cell.getGrainIndex() > 0 & cell.getGrainIndex() <= grains.length)
                      grains[cell.getGrainIndex()-1].addElasticEnergy(cell.getMechEnergy());
              
                  //TEST
                  //      if(indexK == 2)if(indexJ == 25)if(indexI == 25)
                  //          System.out.println("Cell #"+index+": Tn-1 = "+cell.getInitialTemperature()+" Tn = "+cell.getTemperature()+" Thermal energy: "+cell.getThermalEnergy());
                
                  // Replacing of the cell with single index in list of cells 
                  // by the same cell with changed temperature
                  // set(index, cell);

            //    influxSum = influxSum+mechInfluxes[index];
            //    final_total_energy+=cell.getMechEnergy();
              }
            }

            // Spatial cycle over automata located in this micro specimen:
            // calculation of stress tensors for each cell
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
              // Calculation of single index of the cell with given indices.
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
              
              // Change of cell properties.
              cell = (RecCellR3)newCells.get(index);
              
              cell_grain_type = cell.getGrainType();
              
              // Obtaining of stresses of neighbour cells of current cell
           //   if((cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
           //     &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
           //     &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL))
              if( cell_grain_type == Common.INNER_CELL | cell_grain_type == Common.LAYER_CELL)
              {
                if(step_counter == 1)
                  calcSummaryForceMoment(index);
              
                for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                {
                  neighb_index = neighbours3D[index][neighb_counter];
                    
                  if(neighb_index>-1)
                  {
                    neighb_cell = (RecCellR3)get(neighb_index);
                    stresses[neighb_counter] = neighb_cell.getMechStress() - cell.getMechStress();
                  }
                  else
                    stresses[neighb_counter] = 0;
                }
                  
                // Calculation of stress tensor of current cell
                cell.calcStressTensor(stresses, cell1S_vectors);
                  
                // Calculation of stress vector of current cell
                if(calc_force_mom_new)
                {
                  cell.calcStressVector(stresses, cell1S_vectors);
                  
                  if(step_counter % 100 == 0)
                  if(index == 726)
                  {
                    System.out.println("Cell # 726. Stress = "+cell.getMechStress()+"; Stress vector = ("+cell.getStressVector().writeToString()+").");
                    System.out.println("Neighb.index | cell1S_stress   |  cell1S_stresses  |      cell1S_vectors  ");
                    
                    for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                    {
                        neighb_index = neighbours3D[index][neighb_counter];
                    
                        if(neighb_index>-1)
                        {
                          neighb_cell = (RecCellR3)get(neighb_index);
                          
                      //  if(stresses[neighb_counter] != 0)
                          System.out.println(neighbours3D[index][neighb_counter]+"        | "+neighb_cell.getMechStress()+" | "+stresses[neighb_counter]+" | "+cell1S_vectors[neighb_counter].writeToString()+" | ");
                        }
                    }
                    
                    System.out.println();
                  }
                }
              }
            }
            
        /*
            System.out.println("Initial total energy of cells: "+start_total_energy);
            System.out.println("Total number of influxes of elastic energy: "+(index+1));
            System.out.println("Sum of inner flows of elastic energy: "+influxSum);
            System.out.println("Current total energy of cells: "+final_total_energy);
        */
        
            // Number of crack embryos
            int crack_counter = 0;
            
            // Array of indices of cell neighbours
            int[] neighb_indices = new int[neighb1S_number];
            
            // Coordinates of central cell and its neighbours
            VectorR3[] cell_coordinates = new VectorR3[neighb1S_number+1];
            
            // Vectors from neighbour cells to central cell
            VectorR3[] neighb_vectors = new VectorR3[neighb1S_number];
            
            // Mechanical energies of central cell and its neighbours
            mech_energies = new double[neighb1S_number+1];
            
            // Gradients of mechanical energy of central cell and its neighbours
            double[] energy_gradients = new double[neighb1S_number+1];
            
            // Resultant vector
            VectorR3 result_vector;
            
            // Writing of cell mechanical energies to file
            if(writeToFile)
            if(stepCounter == mech_step_number-1)
            try
            {
              for(int cellCounter = 0; cellCounter < size(); cellCounter++)
              {                   
                cell = (RecCellR3)get(cellCounter);
                
                // Indices of cell
                cell_indices = cell.getIndices();  
                
            //    if((cell_indices.getI()==cellNumberI-2)|(cell_indices.getI()==1))
                {
                  writeToFilePoints(bw, cell_indices, ((Integer)Math.round(cell.getGrainIndex())).intValue());
                
            //    bw.write(" "+cell.getMechEnergy()+" "+cell.getMechStress()+" "+cell.getMaterialFile());
            //    bw.write(" "+cell.getMechEnergy());
                  bw.write(" "+cell.getMechStress()+" "+cell.getGrainType()+""+cell.getType());
                  bw.newLine();
                }
         /*
                if(cell.getState() == 1)
                {                    
                    crack_counter++;
                    
                    // Single indices of cell neighbours at 1s1 coordination sphere
                    neighb_indices = neighbours1S[cellCounter];
                    
                    bw.write(cellCounter+" ");
                    bw.flush();
                    
                    // Coordinates of cell
                    cell_coordinates[0] = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());
                    
                    // Mechanical energy of cell
                    mech_energies[0] = cell.getMechEnergy();
                    
                    for(int neighb_counter = 0; neighb_counter<neighb_indices.length; neighb_counter++)
                    if(neighb_indices[neighb_counter]>-1)
                    {
                        cell = (RecCellR3)get(neighb_indices[neighb_counter]);
                        
                        // Indices of neighbour cell
                        cell_indices = cell.getIndices();
                        
                        // Coordinates of neighbour cell
                        cell_coordinates[neighb_counter+1] = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());
                        
                        // Mechanical energy of neighbour cell
                        mech_energies[neighb_counter+1] = cell.getMechEnergy();
                    
                        // Vector from central cell to neighbour cell
                        neighb_vectors[neighb_counter] = residial(cell_coordinates[neighb_counter+1], cell_coordinates[0]);
                        
                        // Gradients of mechanical energy of central cell and its neighbour
                        energy_gradients[neighb_counter] = mech_energies[0] - mech_energies[neighb_counter+1];
                    }
                    else
                    {
                        neighb_vectors  [neighb_counter] = new VectorR3(0, 0, 0);
                        
                        energy_gradients[neighb_counter] = 0;
                    }
                    
                    // Calculation of resultant vector
                    result_vector = calcResultVector(neighb_vectors, energy_gradients);
                        
                    bw.write(result_vector.getX()+" "+result_vector.getY()+" "+result_vector.getZ()+" 0.0 0.0 0.0");
                    bw.flush();
                    bw.newLine();
                }
                */
              }
              
       //       bw.write("Number of embryos of cracks: "+crack_counter);
              bw.close();
           // bw1.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
            
            // Cycle over grains where "old" grains are replaced by "new" grains
            for(int grainCounter = 0; grainCounter < grains.length; grainCounter++)
            {
                // Obtaining of "new" grain from "new" grains cluster
           //     newGrain = (Cluster)newGrainsCluster.get(grainCounter);
                
                // Replacing of "old" grain by "new" one
           //     grainsCluster.set(grainCounter, newGrain);
                
                if(writeToFile)
                if(stepCounter == mech_step_number-1) 
                {
             //       System.out.println("Grain # "+(grainCounter+1)+
              //        ". Elastic energy: "+grains[grainCounter].getElasticEnergy());
                }
            }

            if(writeToFile)
                System.out.println("Number of grains: "+grains.length);
        }
        // FINISH OF "MECHANICAL" TIME STEPS
    }

    /** Calculation of cell boundary normal vector for SCP
     * @param cell_index
     * @param neighb_cell_index
     */
    public byte[] calcUnitBoundNormVectorSCP(int cell_index, int neighb_cell_index)
    {
        byte[] unit_bound_norm_vector = new byte[3];

        cell_indices = calcTripleIndex(cell_index, cellNumberI, cellNumberJ, cellNumberK);
        Three neighb_indices = calcTripleIndex(neighb_cell_index, cellNumberI, cellNumberJ, cellNumberK);

        int vector_coord_X = neighb_indices.getI() - cell_indices.getI();
        int vector_coord_Y = neighb_indices.getJ() - cell_indices.getJ();
        int vector_coord_Z = neighb_indices.getK() - cell_indices.getK();

        if(Math.abs(vector_coord_X)+Math.abs(vector_coord_Y)+Math.abs(vector_coord_Z) == 1)
        {
            unit_bound_norm_vector[0] = (byte)(vector_coord_X);
            unit_bound_norm_vector[1] = (byte)(vector_coord_Y);
            unit_bound_norm_vector[2] = (byte)(vector_coord_Z);

            // TEST
          //  System.out.println("Unit vector coordinates: "+ unit_bound_norm_vector[0]+" "+unit_bound_norm_vector[1]+" "+unit_bound_norm_vector[2]);
        }
        else
        {
            System.out.println("These cells are not neighbours at 1st coordination sphere in SCP!!!");
            return null;
        }

        return unit_bound_norm_vector;
    }

    /** The method performs flows of mechanical energy between automata
     * located in given parallelepiped.
     * Every boundary cell can interact with its neighbour boundary cell.
     * Then information on cell mechanical energies is written to file.
     * @param writeFileName name of file for writing
     * @param writeToFile if true then data of simulation are written to file
     * @param step_counter number of time step
     */
    public void calcAllMechEnergyFlows(String writeFileName, boolean writeToFile, int step_counter)
    {
        if(writeToFile)
        try
        {
            bw  = new BufferedWriter(new FileWriter(writeFileName+"_"+(step_counter+1)+"."+Common.STRESSES_FILE_EXTENSION));
       //   bw1 = new BufferedWriter(new FileWriter(writeFileName+"_"+(step_counter+1)+"_states_points.txt"));
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }

        // Local variables
    //    double oldCellMechEnergy;
        int grain_index = -1;
        int neighb_cell_index;
        Three neighb_cell_indices;
        int neighbCellGrainIndex = -1;
        byte neighb_cell_type = -1;
        double boundary_temperature;
        double force_1, force_2;
        byte cell_grain_type;

        // START OF "MECHANICAL" TIME STEPS
        for(int stepCounter = 0; stepCounter < mech_step_number; stepCounter++)
        {
            // TEST
            if(writeToFile)
                System.out.println("Mechanical energy transfer is simulated...");

            // Spatial cycle on automata located in CA micro specimen
            // Heat exchange between neighbouring automata
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
              // Calculation of single index (in ArrayList)
              // of the cell with current triple indices
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
              
              // Obtaining of cell and its neighbours from list of cells
              cell = (RecCellR3)get(index);
              
              mechInfluxes[index]= 0;
              
              cell_grain_type = cell.getGrainType();
              
              if((cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
                &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL))
         //     if((indexJ>0)|(cell.getMechEnergy()!=0))
              {
                // Index of grain containing this cell
                grain_index = cell.getGrainIndex();

                // Single indices of neighbours at 1st coordination sphere
                neighbIndices = neighbours1S[index];

             // Creation of array of neighbour cells
       /*
                for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                {
                    if(neighbIndices[cellCounter] > -1)
                    {
                        neighbCells[cellCounter] = (RecCellR3)get(neighbIndices[cellCounter]);

                   //     if(neighbCells[cellCounter].getGrainType()==0)
                            // Mechanical energy cannot go out of specimen.
                  //          neighbCells[cellCounter] = cell;
                    }
                }
        */

                temperature = cell.getTemperature();

                // Obtaining of grain containing considered cell
                grain = (Cluster)grainsCluster.get(grain_index-1);

                // Obtaining of grain containing considered cell for consequence of time steps
                newGrain = (Cluster)newGrainsCluster.get(grain_index-1);

                //  Calculation of velocities of movement of grain boundaries and corresponding
                // probabilities of transition of considered cell to each grain containing neighbour cell
                for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                {
                  neighb_cell_index = neighbours1S[index][neighbCounter];

              //    neighb_cell_indices = calcTripleIndex(neighb_cell_index, cellNumberI, cellNumberJ, cellNumberK);

                  // Neighbour cell is in the specimen
             //   if((neighb_cell_indices.getI() > -1)&(neighb_cell_indices.getI() < cellNumberI))
             //   if((neighb_cell_indices.getJ() > -1)&(neighb_cell_indices.getJ() < cellNumberJ))
             //   if((neighb_cell_indices.getK() > -1)&(neighb_cell_indices.getK() < cellNumberK))
                  if((neighb_cell_index>-1)&(neighb_cell_index<size()))
                  {
                    //TEST
                    // System.out.println(index+" "+mechInflux[index]);

                    // Obtaining of neighbour cell in adjacent grain
                    neighbCell = (RecCellR3)get(neighb_cell_index);

                    // Obtaining of index of adjacent grain containing this neighbour cell
                    neighbCellGrainIndex = neighbCell.getGrainIndex();

                    // Obtaining of type of neighbour cell
                    neighb_cell_type = neighbCell.getGrainType();

                    neighb_cell_indices = calcTripleIndex(neighb_cell_index, cellNumberI+2, cellNumberJ+2, cellNumberK+2);

                    if((neighb_cell_type!=Common.ADIABATIC_ADIABATIC_CELL)
                      &(neighb_cell_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                      &(neighb_cell_type!=Common.ADIABATIC_THERMAL_CELL))
                    if((cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
                      &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                      &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL))
                    {
                      // Obtaining of grain containing neighbour cell
                      neighbGrain = (Cluster)grainsCluster.get(neighbCellGrainIndex-1);

                      // Calculation of difference of angles of considered and neighbour cells
                  //  angle_diff = Math.abs(grain.getAngle1() - neighbGrain.getAngle1());

                      angle_diff =(Math.abs(grain.getAngle1() - neighbGrain.getAngle1())+
                                   Math.abs(grain.getAngle2() - neighbGrain.getAngle2())+
                                   Math.abs(grain.getAngle3() - neighbGrain.getAngle3()))/3;

                      //Obtaining of transition limit to high angle grain boundary regime
                      angleHAGB = grain.getAngleLimitHAGB();

                      if(angle_diff == 0)
                      {
                          //Obtaining of maximal value of grain boundary mobility
                       // mechMaxMobility = response_rate/grain.getModElast();
                          mechMaxMobility = 2*response_rate/(grain.getModElast()+neighbGrain.getModElast());

                      /*
                          force_1 = Math.sqrt(Math.abs(2*neighbCell.get_mod_elast()*neighbCell.getMechEnergy()/getCellVolume()));
                          
                          if(neighbCell.getMechEnergy()<0)
                              force_1 = -force_1;

                          force_2 = Math.sqrt(Math.abs(2*cell.get_mod_elast()*cell.getMechEnergy()/getCellVolume()));
                          if(cell.getMechEnergy()<0)
                              force_2 = -force_2;                 
                      */

                          force_1 = neighbCell.getMechStress();
                          force_2 = cell.getMechStress();
                          
                          // Calculation of pressure (motive force) on grain boundary
                          motive_force = force_1 - force_2;

                          specBoundEnergy = 0;
                          mobility_exponent = 0;

                          velocity = mechMaxMobility*motive_force;
                      }
                      else
                      if(angle_diff <= angleHAGB)
                      {
                        // Obtaining of specific energy of high angle grain boundary for neighbour cell
                        energyHAGB = grain.getEnergyHAGB();

                        //Calculation of specific energy of boundary between grains
                        specBoundEnergy = 0;//(energyHAGB*angle_diff/angleHAGB)*(1 - Math.log(angle_diff/angleHAGB));

                        //Obtaining of maximal value of grain boundary mobility
                     // mechMaxMobility = response_rate/grain.getModElast();
                        mechMaxMobility = 2*response_rate/(grain.getModElast()+neighbGrain.getModElast());

                   /*
                        force_1 = Math.sqrt(Math.abs(2*neighbCell.get_mod_elast()*neighbCell.getMechEnergy()/getCellVolume()));
                        if(neighbCell.getMechEnergy()<0)
                            force_1 = -force_1;

                        force_2 = Math.sqrt(Math.abs(2*cell.get_mod_elast()*cell.getMechEnergy()/getCellVolume()));
                        if(cell.getMechEnergy()<0)
                            force_2 = -force_2;
                  
                     */

                        force_1 = neighbCell.getMechStress();
                        force_2 = cell.getMechStress();
                          
                        // Calculation of pressure (motive force) on grain boundary
                        motive_force = force_1 - force_2;

                        boundary_temperature = (neighbCell.getTemperature()+temperature)/2;

                        if(boundary_temperature!=0)
                        {
                            mobility_exponent = -(getCellSurface()/neighb1S_number)*specBoundEnergy/(Task.BOLTZMANN_CONST*boundary_temperature);

                            // Calculation of velocity of grain boundary movement
                            velocity = mechMaxMobility*motive_force*Math.pow(Math.E, mobility_exponent);
                        }
                        else
                            velocity = 0;
                      }
                      else
                        velocity = 0;
                    }
                    else
                        velocity = 0;

                      // Calculation of probability of transition of cell to neighbour grain
                      mechInfluxes[index]+= 0.5*Math.abs(velocity)*time_step*getCellVolume()*motive_force/cell_size_X;//+
                      //                  getCellVolumeHCP()*motive_force*cell.getStrain();
                  }
                }
                //TEST
             //   System.out.println(index+" "+mechInflux[index]);
              }
              else
                  mechInfluxes[index]= 0;
            }

            //TEST
     //       System.out.println();

            // Setting of zero elastic energies to grains
            for(int grain_counter = 0; grain_counter < grains.length; grain_counter++)
                grains[grain_counter].setElasticEnergy(0);

            // Spatial cycle over automata located in this micro specimen:
            // calculation of elastic energy, stress and strain for each cell
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
              // Calculation of single index of the cell with given indices.
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;

              // Change of cell properties.
              cell = (RecCellR3)newCells.get(index);

              cell_grain_type = cell.getGrainType();

              if((cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
                &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL))
              {
                  // Setting of new value of mechanical energy of cell
                  cell.setMechEnergy(cell.getMechEnergy()+mechInfluxes[index]);

                  // Calculation of mechanical stress in cell
                  cell.calcMechStressAndStrain();

                  // Determination of cell state
                  if((cell_grain_type == Common.INNER_CELL)|
                     (cell_grain_type == Common.LAYER_CELL))
                      cell.determineState();

                  // Change of grain thermal energy
                  grains[cell.getGrainIndex()-1].addElasticEnergy(cell.getMechEnergy());
              }
            }
            
            // Stresses of neighbour cells
            double[] stresses = new double[neighb1S_number];
            
            // Neighbour cell
            RecCellR3 neighb_cell;
            
            // Spatial cycle over automata located in this micro specimen:
            // calculation of stress tensors for each cell
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
              // Calculation of single index of the cell with given indices.
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
              
              // Change of cell properties.
              cell = (RecCellR3)newCells.get(index);
              
              cell_grain_type = cell.getGrainType();
              
              // Obtaining of stresses of neighbour cells of current cell
              if((cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
                &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL))
              {
                  for(int neighb_counter = 0; neighb_counter<neighb1S_number; neighb_counter++)
                  {
                      neighb_cell = (RecCellR3)get(neighbours3D[index][neighb_counter]);
                      stresses[neighb_counter] = neighb_cell.getMechStress();
                  }
                  
                  // Calculation of stress tensor of current cell
                  cell.calcStressTensor(stresses, cell1S_vectors);
              }
            }
            
            // Number of crack embryos
            int crack_counter = 0;
            
            // Array of indices of cell neighbours
            int[] neighb_indices = new int[neighb1S_number];
            
            // Coordinates of central cell and its neighbours
            VectorR3[] cell_coordinates = new VectorR3[neighb1S_number+1];
            
            // Vectors from neighbour cells to central cell
            VectorR3[] neighb_vectors = new VectorR3[neighb1S_number];
            
            // Mechanical energies of central cell and its neighbours
            mech_energies = new double[neighb1S_number+1];
            
            // Gradients of mechanical energy of central cell and its neighbours
            double[] energy_gradients = new double[neighb1S_number];
            
            // Resultant vector
            VectorR3 result_vector;
            
            // Writing of cell mechanical energies to file
            if(writeToFile)
            if(stepCounter == mech_step_number-1)
            try
            {
              for(int cellCounter = 0; cellCounter < size(); cellCounter++)
              {
                cell = (RecCellR3)get(cellCounter);

                // Indices of cell
                cell_indices = cell.getIndices();

            //    if((cell_indices.getI()==cellNumberI-2)|(cell_indices.getI()==1))
                {
                  writeToFilePoints(bw, cell_indices, ((Integer)Math.round(cell.getGrainIndex())).intValue());

            //    bw.write(" "+cell.getMechEnergy()+" "+cell.getMechStress()+" "+cell.getMaterialFile());
            //    bw.write(" "+cell.getMechEnergy());
                  bw.write(" "+cell.getMechStress()+" "+cell.getGrainType()+""+cell.getType());
                  bw.newLine();
                }
              }

       //       bw.write("Number of embryos of cracks: "+crack_counter);
              bw.close();
           // bw1.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }

            // Cycle over grains where "old" grains are replaced by "new" grains
            if(false)
            for(int grainCounter = 0; grainCounter < grains.length; grainCounter++)
            if((writeToFile)&(stepCounter == mech_step_number-1))
                System.out.println("Grain # "+(grainCounter+1)+
                          ". Elastic energy: "+grains[grainCounter].getElasticEnergy());
        }
        // FINISH OF "MECHANICAL" TIME STEPS
    }

    /** The method simulates recrystallization process.
     * @param writeFileName name of file for writing of simulation results
     * @param writeToFile if true then data of simulation are written to file
     * @param step_counter number of time step
     * @param equilibrium_state variable responsible for equilibrium state of specimen
     */
    public void calcRecrystallization(String writeFileName, boolean writeToFile, 
                                      long step_counter, byte equilibrium_state)
    {         
        /* Block 16 */
        
        // Recrystallization velocity
        velocity = 0;

        // Switch probability
        probSwitch = 0;
        
        // Index of grain containing neighbour cell
        int neighbCellGrainIndex;
        
        // Number of neighbours in grain containing considered cell
   //     int neighbsInThisGrain;
        
        // Sum of switch probabilities
        double probSum;

        // Total number of cells in specimen
        int spec_size = size();
        
        // Numbers of neighbour cells at 1st coordination sphere in grains
     //   int[] numbers_of_cells_in_grains = new int[grainsCluster.size()];
        
        // Index of adjacent grain containing neighbour cell
    //    int neighb_gr_index = -1;
        
        // The variable shows type of cell location in grain: internal or boundary.
    //    boolean cell_on_boundary = false;
        
        // The variable shows type of cell location in specimen: internal or boundary.
     //   boolean specimen_boundary_cell = false;
        
        // Single index of neighbour cell
        int neighb_cell_index;
        
        // Temperature on boundary between neighbour cells
        double boundary_temperature = 0;
                
        // Current number of grains
        int grain_number;
        
        // Number of possible switches at current time step
        int possible_switch_number = 0;
        
        // Variable responsible for presence of neighbour in adjacent grain
   //     boolean neighb1S_in_adj_grain;
        
        // Type of grain containing current cell
        byte cell_type=0;
        
        // Type of grain containing current neighbour cell
        byte neighb_type=0;
        
        // Variable for calculation of number of cells, 
        //which can be switched at current time step
     //   boolean switch_is_possible = true;

        // Volume fraction of pinning particles
        double part_vol_fracture;

        // Radius of pinning particle
        double part_radius;

        // Pinning force
        double zener_force;

        // Sign of motive force
        byte sign;

        // Moduli of elasticity of neighbour cells
  //      double mod_elast, neighb_mod_elast;
        
        // Mobility of grain boundary under "mechanical" recrystallization
   //     double _mechMaxMobility = 0;
        
        // Minimal number of neighbour cells in adjacent grain necessary for cell transition (grain growth)
        int min_neighb_num_for_recryst = 4;// Math.max(3, min_neighbours_number - 3);
        
        // The number of cells within grain boundaries
        int grain_bound_cell_number = 0;

        // The sum of heat motive forces at grain boundaries
        double total_heat_motive_force = 0;

        // The sum of mechanical motive forces at grain boundaries
        double total_mech_motive_force = 0;
            
        // The sum of dislocation motive forces at grain boundaries without Zener pinning particles
        double total_disl_no_zener_motive_force = 0;
             
        // The sum of dislocation motive forces at grain boundaries
        double total_disl_motive_force = 0;

        // The sum of Zener pinning forces at grain boundaries
        double total_zener_force = 0;

        // The sum of velocities of grain boundaries
        double total_velocity = 0;

        // Maximal velocity of grain boundary
        double max_velocity = 0;

        // The sum of switch probabilities
        double total_prob = 0;
        
        // Materials of current cell and its neighbour cell
        String cell_material="", neighb_material="";
        
        // Counter of cell switches
        switch_counter = 0;
        
        // Probabilities of joining of current cell to adjacent grains containing neighbour cells 
        probs = new double[neighb1S_number+1];
        
        // Array of indices of grains containing neighbour cells and current cell
        neighb_grain_indices = new int[neighb1S_number + 1];
        
        // indices of grains for each cell
        int[] grain_indices = new int[spec_size];
        
        // types of grains for each cell
        byte[] grain_types   = new byte[spec_size];
        
        // Materials of grains for each cell
        String[] materials = new String[spec_size];
        
        // Cell temperatures
        double temperatures[] = new double[spec_size];
        
        // Cell initial temperatures
        double init_temperatures[]     = new double[spec_size];
        
        // Array of orientation angles for all grains
        double[] grain_angles = new double[grainsCluster.size()];
        
        // Array of highest misorientation angles for grains
        double[] angleHAGB = new double[grainsCluster.size()];

        // Array of maximal values of grain boundary mobilities
        // heat mobilities
        double[] maxMobility = new double[grainsCluster.size()];
        
        // dislocation mobilities
        double[] dislMaxMobility = new double[grainsCluster.size()];
        
        // mechanical mobilities
        double[] mechMaxMobility = new double[grainsCluster.size()];
        
        // thermal energies of cells
        double thermal_energies[] = new double[spec_size];
        
        // dislocation energies of cells
        double disl_energies[] = new double[spec_size];
        
        // mechanical energies of cells
        double mech_energies[] = new double[spec_size];
        
        // Array of indices of cells located in specimen (not at outer boundaries)
    //    int[] spec_cell_indices;
        
        // New index of cell as aresult of recrystallization
        int new_grain_index = 0;
        
        // Cell volume
        double cell_volume = getCellVolume();
        
        // Creation of arrays of parameters necessary for simulation of recrystallization
        for (int cellCounter = 0; cellCounter < spec_size; cellCounter++)
        {
          // current cell
          cell                          = (RecCellR3)get(cellCounter);
              
          // index of grain containing current cell
          grain_indices[cellCounter]    = cell.getGrainIndex();
          cellID                        = grain_indices[cellCounter];
              
          // type of grain containing current cell
          grain_types[cellCounter]      = cell.getGrainType();
          cell_type                     = grain_types[cellCounter];
          
          // Material of grain containing current cell
          materials[cellCounter]        = cell.getMaterialFile();
           
          // Cell temperature
          temperatures[cellCounter]     = cell.getTemperature();
          
          // Cell initial temperature
          init_temperatures[cellCounter]     = (Double)initTemperatures.get(cellCounter);
          
          // Obtaining of grain containing considered cell
          grain                         = (Cluster)grainsCluster.get(cellID - 1);
          
          // orientation angles for grains
          grain_angles[cellID - 1]      = (grain.getAngle1() + grain.getAngle2() + grain.getAngle3())/3;
           
          // highest misorientation angles for grains
          angleHAGB[cellID - 1]        = grain.getAngleLimitHAGB();
          
          // thermal energy of current cell
          thermal_energies[cellCounter] = cell.getThermalEnergy();
            
          // mechanical energy of current cell
          mech_energies[cellCounter]    = cell.getMechEnergy();
            
          // dislocation energy of current cell
          disl_energies[cellCounter]    = grain.getDislEnergy();
            
          if(cell_type == Common.INNER_CELL | cell_type == Common.LAYER_CELL)
          {
            // maximal values of grain boundary mobilities
            // heat mobilities
            maxMobility[cellID - 1]      = grain.getMaxMobility();
            
            // dislocation mobilities
            dislMaxMobility[cellID - 1]  = grain.getDislMaxMobility();
            
            // mechanical mobilities
            mechMaxMobility[cellID - 1]  = grain.getMechMaxMobility();
          }
          else
          {
            // maximal values of grain boundary mobilities
            // heat mobilities
            maxMobility[cellID - 1]      = 0;
            
            // dislocation mobilities
            dislMaxMobility[cellID - 1]  = 0;
            
            // mechanical mobilities
            mechMaxMobility[cellID - 1]  = 0;
          }
        }

        //---------- START OF "RECRYSTALLIZATION" TIME STEPS -------------------
        for(int stepCounter = 0; stepCounter<recryst_step_number; stepCounter++)
        {
            // Total sum of switch probabilities at current time step
            total_prob_sum = 0;
            
            // Maximal value of switch probability at current time step
            max_prob = 0;
                         
            // Current number of grains
            grain_number = 0;

            //TEST
            if(writeToFile)
              System.out.println("Recrystallization is simulated...");

            // The number of cells within grain boundaries
            grain_bound_cell_number = 0;

            // The sum of heat motive forces at grain boundaries
            total_heat_motive_force = 0;

            // The sum of mechanical motive forces at grain boundaries
            total_mech_motive_force = 0;
            
            // The sum of dislocation motive forces at grain boundaries without Zener pinning particles
            total_disl_no_zener_motive_force = 0;
                    
            // The sum of dislocation motive forces at grain boundaries
            total_disl_motive_force = 0;

            // The sum of Zener pinning forces at grain boundaries
            total_zener_force = 0;

            // The sum of velocities of grain boundaries
            total_velocity = 0;

            // Maximal velocity of grain boundary
            max_velocity = 0;

             // The sum of switch probabilities
            total_prob = 0;
            
            // Materials of current cell and its neighbour cell
            cell_material   = "";
            neighb_material = "";

            // Spatial cycle over cellular automata net 
            for (int cellCounter = 0; cellCounter < spec_size; cellCounter++)
            {
              // index of grain containing current cell
              cellID        = grain_indices[cellCounter];
              
              // Type of grain containing current cell
              cell_type     = grain_types[cellCounter];
              
              // Material of grain containing current cell
              cell_material = materials[cellCounter];

              // Variable for determination of possibility of cell switch at current time step
              //  switch_is_possible = true;
              
              // Sum of switch probabilities
              probSum = 0;
                
              // If current cell is located in specimen
              if(cell_type == Common.INNER_CELL | cell_type == Common.LAYER_CELL)
              {
                // Cell temperature
                temperature = temperatures[cellCounter];
                
                // Array of single indices of cell neighbours at 1st coordination sphere
                neighbSingleIndices = neighbours1S[cellCounter];
                
                // Indices of grains containing neighbour cells and current cell
                neighb_grain_indices = new int[neighb1S_number + 1];
                  
                // The last element of the array contains grain index for current cell
                neighb_grain_indices[neighb1S_number] = cellID;
                
                // Index of grain containing neighbour cell
                neighbCellGrainIndex = -1;
                
                // Probability of cell transition to adjacent grain
                probSwitch = 0;
                  
                // Probabilities of cell transition to adjacent grains and probability
                // of cell stay in the same grain
                probs = new double[neighb1S_number + 1];
                
                //  Calculation of velocities of movement of grain boundaries and corresponding 
                // probabilities of transition of considered cell to each grain containing neighbour cell
                for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                {         
                  // Индекс соседнего элемента
                  neighb_cell_index = neighbSingleIndices[neighbCounter];
                   
                  // Obtaining of index of grain containing this neighbour cell
                  neighbCellGrainIndex = grain_indices[neighb_cell_index];
                      
                  // Type of neighbour cell: 0 - adiabatic outer cell,
                  //                         1 - inner cell,
                  //                         2 - interacting outer cell.
                  neighb_type     = grain_types[neighb_cell_index];
                    
                  if(neighb_type == Common.INNER_CELL | neighb_type == Common.LAYER_CELL)
                  {
                    // Материал соседнего элемента
                    neighb_material = materials[neighb_cell_index];
                      
                    // Index of grain containing current neighbour cell is added to the array
                    neighb_grain_indices[neighbCounter] = neighbCellGrainIndex;
                    
                      // Calculation of difference of angles of considered and neighbour cells
                 //   angle_diff = Math.abs(grain.getAngle1() - neighbGrain.getAngle1());

                 //   angle_diff =(Math.abs(grain.getAngle1() - neighbGrain.getAngle1())+
                 //                Math.abs(grain.getAngle2() - neighbGrain.getAngle2())+
                 //                Math.abs(grain.getAngle3() - neighbGrain.getAngle3()))/3;
                    angle_diff = Math.abs(grain_angles[cellID - 1] - grain_angles[neighbCellGrainIndex - 1]);
                    
                    // if difference of angles equals 0 it means that neighbour cell is in the same grain that current cell
                    if(angle_diff == 0)
                    {
                        // Calculation of pressure (motive force) on grain boundary
                     // motive_force = (cell.getThermalEnergy() - neighbCell.getThermalEnergy())/getCellVolumeHCP();
                   
                        // Heat, mechanical and dislocation  motive forces are calculated.
                        heat_motive_force = (thermal_energies[cellCounter] - thermal_energies[neighb_cell_index])/getCellVolume();
                        mech_motive_force = (Math.abs(mech_energies[cellCounter]) - Math.abs(mech_energies[neighb_cell_index]))/getCellVolume();
                        disl_motive_force = disl_energies[cellCounter] - disl_energies[neighb_cell_index];

                        // specific energy of boundary between grains
                        specBoundEnergy = 0;
                        
                        // the value for calculation of tjtal mobility
                        mobility_exponent = 0;
                        
                        // Velocity of boundary movement
                        velocity = maxMobility    [cellID - 1]*heat_motive_force + 
                                   mechMaxMobility[cellID - 1]*mech_motive_force + 
                                   dislMaxMobility[cellID - 1]*disl_motive_force;
                    
                        // The number of cells within grain boundaries
                   //     grain_bound_cell_number++;
                        
                        // The sum of heat motive forces at grain boundaries
                        total_heat_motive_force += Math.abs(heat_motive_force);

                        // The sum of mechanical motive forces at grain boundaries
                        total_mech_motive_force += Math.abs(mech_motive_force);

                        // The sum of dislocation motive forces at grain boundaries
                        total_disl_motive_force += Math.abs(disl_motive_force);
                        
                        // The sum of velocities of grain boundaries
                        total_velocity += Math.abs(velocity);

                        // Maximal velosity is determined
                        max_velocity = Math.max(Math.abs(velocity), max_velocity);
                      }
                      else
                      {
                        // Малоугловая граница
                        if(angle_diff <= angleHAGB[cellID - 1])
                        {
                          // Obtaining of specific energy of high angle grain boundary for neighbour cell
                          energyHAGB = grain.getEnergyHAGB();
                        
                          //Calculation of specific energy of boundary between grains
                          specBoundEnergy = (energyHAGB*angle_diff/angleHAGB[cellID - 1])*(1 - Math.log(angle_diff/angleHAGB[cellID - 1]));
                        
                          // Calculation of pressure (motive force) on grain boundary is the sum of
                          // heat, mechanical and dislocation motive forces.                           
                          heat_motive_force = (thermal_energies[cellCounter] - thermal_energies[neighb_cell_index])/getCellVolume();
                          mech_motive_force = (Math.abs(mech_energies[cellCounter]) - Math.abs(mech_energies[neighb_cell_index]))/getCellVolume();
                          disl_motive_force =  disl_energies[cellCounter] - disl_energies[neighb_cell_index];
                        
                          // Calculation of temperature at boundary
                          if(equilibrium_state == 0)
                            // for nonequilibrium state
                            boundary_temperature = (temperatures[neighb_cell_index] + temperatures[cellCounter])/2;
                          else
                            // for equilibrium state
                              boundary_temperature = (temperatures[neighb_cell_index] - init_temperatures[neighb_cell_index]+
                                                      temperatures[cellCounter]       - init_temperatures[cellCounter])/2;  
                        
                          if(boundary_temperature!=0)
                          {
                            // Exponent for calculation of velocity
                            mobility_exponent = -(getCellSurface()/neighb1S_number)*specBoundEnergy/(Task.BOLTZMANN_CONST*boundary_temperature);

                            // Calculation of velocity of grain boundary movement
                            velocity = (maxMobility[cellID - 1]*heat_motive_force + mechMaxMobility[cellID - 1]*mech_motive_force +
                                        dislMaxMobility[cellID - 1]*disl_motive_force)*Math.pow(Math.E, mobility_exponent);
                          }
                          else
                            velocity = 0;
                        
                          // The number of cells within grain boundaries
                   //     grain_bound_cell_number++;
                        
                          // The sum of heat motive forces at grain boundaries
                          total_heat_motive_force += Math.abs(heat_motive_force);

                          // The sum of mechanical motive forces at grain boundaries
                          total_mech_motive_force += Math.abs(mech_motive_force);

                          // The sum of dislocation motive forces at grain boundaries
                          total_disl_motive_force += Math.abs(disl_motive_force);

                          // The sum of velocities of grain boundaries
                          total_velocity += Math.abs(velocity);

                          // Maximal velosity is determined
                          max_velocity = Math.max(Math.abs(velocity), max_velocity);
                        }
                        else 
                           velocity = 0;
                      }
                    
                      // Calculation of probability of transition of cell to neighbour grain
                      probSwitch = Math.max(0, -velocity)*time_step/cell_size_X;
                      
                      // The value of probability must be in the segment [0; 1].
                   //   probSwitch = Math.min(1, probSwitch);
                      
                      // Printing of information 
                  //    if(false)
                      if( probSwitch>1 | probSwitch<0)
                      {
                          System.out.println();
                          System.out.println("ERROR!!! The value of switch probability cannot be out of the segment [0; 1]. But probSwitch = "+probSwitch);
                          System.out.println("step_counter = "+step_counter+
                                             "\n cellCounter       = "+cellCounter+      "; grain index          = "+cellID+              "; grain type  = "+cell_type+
                                             "\n neighb_cell_index = "+neighb_cell_index+"; neighbCellGrainIndex = "+neighbCellGrainIndex+"; neighb_type = "+neighb_type+
                                             "\n maxMobility       = "+maxMobility+      "; heat_motive_force    = "+heat_motive_force+
                                             "\n mechMaxMobility   = "+mechMaxMobility[cellCounter]+ "; mech_motive_force    = "+mech_motive_force+
                                             "\n dislMaxMobility   = "+dislMaxMobility+  "; disl_motive_force    = "+disl_motive_force+
                                             "\n specBoundEnergy   = "+specBoundEnergy+  "; boundary_temperature = "+boundary_temperature);
                          System.out.println  (" mobility_exponent = "+mobility_exponent+"; velocity             = "+velocity);
                          System.out.println("PLEASE, decrease the value of grain boundary mobility or the value of time step!!!");
                          System.out.println();
                          step_number = 0;
                      }
                      
                        // Calculation of probability of cell transition to grain containing
                        // considered neighbour cell
                   //   probs[neighbCellGrainIndex-1] = probs[neighbCellGrainIndex-1]+probSwitch;
                   //   probs[neighbCounter] = probSwitch;
                        probs[neighbCounter] = probSwitch;
                      
                        probSum += probSwitch;
                      }
                    }
                
                    probs[neighb1S_number] = 1 - probSum;
                
                    // Sum of switch probabilities cannot be less than 0 or larger than 1
                    if(probSum < 0 | probSum > 1)
                    {
                      System.out.println();
                      System.out.println("ERROR!!! The sum of switch probabilities cannot be out of the segment [0; 1]. But probSum = "+probSum);
                      System.out.println("step_counter = "+step_counter+
                                         "\n cellCounter       = "+cellCounter+      "; grain index          = "+cellID+              "; grain type  = "+cell_type+
                                         "\n maxMobility       = "+maxMobility+      "; heat_motive_force    = "+heat_motive_force+
                                         "\n mechMaxMobility   = "+mechMaxMobility[cellCounter]+  "; mech_motive_force    = "+mech_motive_force+
                                         "\n dislMaxMobility   = "+dislMaxMobility+  "; disl_motive_force    = "+disl_motive_force+
                                         "\n specBoundEnergy   = "+specBoundEnergy+  "; boundary_temperature = "+boundary_temperature);
                      System.out.println  (" mobility_exponent = "+mobility_exponent+"; velocity             = "+velocity);
                      System.out.println("PLEASE, decrease the value of grain boundary mobility or the value of time step!!!");
                      System.out.println();
                      step_number = 0;
                    }
                  
                  // The method realizes stochastic transition of cell to one of adjacent grains.
                  // New method without "if" conditions is used.
                  // The method returns array position of grain index of chosen neighbour at 1st coordination sphere
                  int chosen_neighb1S_index = realizeCellStochSwitch_2(probs, 4);
                  
                  // New index of grain for current cell
                  new_grain_index  =  neighb_grain_indices[chosen_neighb1S_index];
                  
                  // if cell is switched
                  if(new_grain_index != cellID)
                  {
                      // counter of cell switches
                      switch_counter++;
                
                      // Obtaining of grain containing neighbour cell for consequence of time steps
                      newNeighbGrain = (Cluster)newGrainsCluster.get(new_grain_index - 1);
            
                      // Joining of cell to adjacent grain
                      newNeighbGrain.add(cellCounter);
                  
                      // Addition of cell thermal energy to adjacent grain
                      newNeighbGrain.addThermalEnergy(cell.getThermalEnergy());

                      // Addition of cell elastic energy to adjacent grain
                      newNeighbGrain.addElasticEnergy(cell.getMechEnergy());
                
                      // Grain, which contained current cell at current time step
                      newGrain   = (Cluster)newGrainsCluster.get(cellID - 1);
                      
                      // Removing of cell from grain
                      //  if(newGrain.contains(cellCounter))
                      newGrain.remove(newGrain.indexOf(cellCounter));
                
                      // Extracting of cell thermal energy from grain
                      newGrain.addThermalEnergy(-cell.getThermalEnergy());

                      // Extracting of cell elastic energy from grain
                      newGrain.addElasticEnergy(-cell.getMechEnergy());
                      
                      // Change of cell ID in list of ID of all cells
                      newIDOfCells[cellCounter] = new_grain_index;
                  }
                  else
                      newIDOfCells[cellCounter] = cellID;
              }
            }

            // The average of heat motive forces at grain boundaries
            double average_heat_motive_force = total_heat_motive_force/grain_bound_cell_number;

            // The average of mechanical motive forces at grain boundaries
            double average_mech_motive_force = total_mech_motive_force/grain_bound_cell_number;

            // The average of dislocation motive forces at grain boundaries without Zener pinning particles
            double average_disl_no_zener_motive_force = total_disl_no_zener_motive_force/grain_bound_cell_number;

            // The average of dislocation motive forces at grain boundaries
            double average_disl_motive_force = total_disl_motive_force/grain_bound_cell_number;

            // The average of Zener pinning forces at grain boundaries
            double average_zener_force = total_zener_force/grain_bound_cell_number;

            // The average of velocities of grain boundaries
            double average_velocity = total_velocity/grain_bound_cell_number;

            // The average switch probability
            double average_prob_switch = total_prob_sum/possible_switch_number;//0.5*total_prob_sum/grain_bound_cell_number;

            // Exponent of power of 10 in standard representation
            // of total number of time steps
            int max_exponent = (int)Math.floor(Math.log10(step_number));

            /* Writing of information to file*/
            // Exponent of power of 10 in standard representation
            // of number of current time step
            int exponent = 0;

            if(step_counter > 0)
                exponent     = (int)Math.floor(Math.log10(step_counter));

            // String of zeros
            String zeros = "";

            for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                zeros = zeros + "0";

            try
            {
                bw3.write(zeros+step_counter+" "+average_heat_motive_force+" "+average_mech_motive_force+" "+
                          average_disl_no_zener_motive_force+" "+average_disl_motive_force+" "+average_zener_force+" "+
                          average_velocity+" "+max_velocity+" "+average_prob_switch+" "+max_prob);
             //   bw3.write(" "+time_step+" "+cell_size_X);

                bw3.newLine();
                bw3.flush();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error of recording of information about average values of motive forces at grain boundaries to the file!");
            }
            //END OF TEST

            //TEST
            if(writeToFile)
        //  if(stepCounter == recryst_step_number-1)
            {
                System.out.println("Step # "+(step_counter+1)+". Maximal number of cell switches:    "+possible_switch_number);
                System.out.println("Step # "+(step_counter+1)+". Average probability of cell switch: "+average_prob_switch);
           //     System.out.println("Step # "+(step_counter+1)+". Maximal probability of cell switch: "+max_prob);
            }
            //END OF TEST
                        
            // Spatial cycle over cellular automata net: setting of new ID and Euler angles for all cells
            for (int cellCounter = 0; cellCounter < spec_size; cellCounter++)
            {
              cell = (RecCellR3)get(cellCounter);
              
              // номер зерна
              cellID = cell.getGrainIndex();
              
              new_grain_index = newIDOfCells[cellCounter];
              
              // тип зерна
           //   cell_type = cell.getGrainType();
              cell_type                     = grain_types[cellCounter];
              
              // элемент внутри образца
              if(cell_type == Common.INNER_CELL | cell_type == Common.LAYER_CELL)
              if(new_grain_index != cellID)
              {
                // Получение зерна с новыми параметрами из списка 
                newGrain = (Cluster)newGrainsCluster.get(new_grain_index - 1);
                cell.setGrainIndex(new_grain_index);
                
                // Setting of Euler angles for current grain 
                cell.setEulerAngles(newGrain.getAngles());
                
                // Новый индекс зерна, содержащего данный элемент
                grain_indices[cellCounter]    = new_grain_index;
              }
            }
          
            //TEST
            if(writeToFile)
            if(stepCounter == recryst_step_number-1)
              System.out.println();

            // Type of grain (cluster of cells)
            byte grain_type;
                 
            // Cycle over grains where "old" grains are replaced by "new" grains
            for(int grainCounter = 0; grainCounter < grainsCluster.size(); grainCounter++)
            {
              // Obtaining of "new" grain from "new" cluster of grains
              newGrain = (Cluster)newGrainsCluster.get(grainCounter);

              // Тип зерна
              grain_type = newGrain.getType();
              
              // Число элементов в зерне
              grainSize = newGrain.size();
              
         //     if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
              {
                // Replacing of "old" grain by "new" one
                if(grain_type==Common.INNER_CELL | grain_type == Common.LAYER_CELL)
                    grainsCluster.set(grainCounter, newGrain);
                
                //TEST
                // Тепловая энергия зерна
                grainThermalEnergy = newGrain.getThermalEnergy();
                
                // Механическая энергия зерна
                grain_mech_energy = newGrain.getElasticEnergy();

                if(writeToFile)
                if(stepCounter == recryst_step_number-1)
                if(grainSize!=0)
               // if((grain_mech_energy!=0)|(grainThermalEnergy!=0))
                {
                    grain_number++;

              //      System.out.println("Grain # "+(grainCounter+1)+
                //                       "; type: "+grain_type+
                  //                     "; size: "+grainSize+
                    //                   "; specific heat energy: "+grainThermalEnergy/grainSize+
                      //                 "; elastic energy: "+grain_mech_energy);//*grainSize*getCellVolumeHCP());
                }
              }
            }            
            
            // Общее число переключений
            total_switch_number+= switch_counter;
            
            // Number of cell switches at time steps after last recording to file
            period_switch_number+= switch_counter;
            
            //TEST
            if(writeToFile)
            if(stepCounter == recryst_step_number-1)
            {
                System.out.println();
                System.out.println("Number of grains at time step "+step_counter+": "+grain_number);
                System.out.println("Number of cells switched at time step "+step_counter+": "+switch_counter);
              //  System.out.println("Number of cell switches at time steps "+(step_counter-step_number/output_file_number+2)+
              //                           " - "+(step_counter+1)+": "+period_switch_number);
                System.out.println("Number of cell switches at time steps "+(last_written_step_index+1)+
                                         " - "+step_counter+": "+period_switch_number);
                System.out.println("Total number of cell switches: "+total_switch_number);
                System.out.println();
                
                period_switch_number = 0;
                last_written_step_index = step_counter;
            }
            
            // Spatial cycle on cells
            // At each step of the cycle identifiers of type of cell location in grain are written
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
                // Calculation of triple index of the cell with given indices.
                cell_indices = new Three(indexI, indexJ, indexK);
                
                // Call of the cell with given indices from list of all cells of specimen.
                index = calcSingleIndex(cell_indices);
            
                // Obtaining of cell from list of cells
                cell = (RecCellR3)get(index);
                         
                // Obtaining of ID of grain containing cell
                cellID = cell.getGrainIndex();
                        
                // Obtaining of type of grain containing cell
                cell_type = cell.getGrainType();
                        
                if(cell_type==Common.INNER_CELL | cell_type==Common.LAYER_CELL)
                {
                  //  writeToFilePoints(bw, cell_indices, cellID);
                            
                    // Writing of identifier of type of cell location in grain
                    if(cell.getLocationOnBoundary())
                    {
                        if(cell.getLocationOnSpecBoundary())
                            // Элемент на границе зерна и образца
                            cell.setType(Common.INNER_BOUNDARY_INTERGRANULAR_CELL);
                        else
                            // Элемент на границе зерна внутри образца
                            cell.setType(Common.INTERGRANULAR_CELL);
                    }
                    else
                    {
                        if(cell.getLocationOnSpecBoundary())
                            // Элемент внутри зерна на границе образца
                            cell.setType(Common.INNER_BOUNDARY_CELL);
                        else
                            // Элемент внутри зерна и внутри образца
                            cell.setType(Common.INTRAGRANULAR_CELL);
                    }
                }
            }
        }
        //---------- END OF "RECRYSTALLIZATION" TIME STEPS -------------------
    }
    
    /** The method simulates recrystallization process.
     * @param writeFileName name of file for writing of simulation results
     * @param writeToFile if true then data of simulation are written to file
     * @param step_counter number of time step
     * @param equilibrium_state variable responsible for equilibrium state of specimen
     */
    public void calcRecrystForParalCalc(String writeFileName, boolean writeToFile, 
                                        long step_counter, byte equilibrium_state)
    {   
        // Recrystallization velocity
        velocity = 0;

        // Switch probability
        probSwitch = 0;
        
        // Index of grain containing neighbour cell
        int neighbCellGrainIndex;
        
        // Sum of switch probabilities
        double probSum;

        // Total number of cells in specimen
        int spec_size = size();
        
        // Single index of neighbour cell
        int neighb_cell_index;
        
        // Temperature on boundary between neighbour cells
        double boundary_temperature = 0;
          
        // Type of grain containing current cell
        byte cell_type = 0;
        
        // Type of grain containing current neighbour cell
        byte neighb_cell_type = 0;
        
        // Probabilities of joining of current cell to adjacent grains containing neighbour cells 
        probs = new double[neighb1S_number+1];
        
        // Array of indices of grains containing neighbour cells and current cell
        neighb_grain_indices = new int[neighb1S_number + 1];
        
        // indices of grains for each cell
        grain_indices = new int[spec_size];
              
        // Total number of grains 
        grain_number = grainsCluster.size();
        
        // Cell temperatures
        double temperatures[] = new double[spec_size];
        
        // Cell initial temperatures
        double init_temperatures[]     = new double[spec_size];
        
        // Array of orientation angles for all grains
        double[] grain_angles = new double[grain_number];
        
        // types of grains for all cells
        byte[] grain_types   = new byte[spec_size];
        
        // Array of highest misorientation angles for grains
        double[] angleHAGB = new double[grain_number];

        // Array of maximal values of grain boundary mobilities
        // heat mobilities
        double[] heatMaxMobility = new double[grain_number];
        
        // dislocation mobilities
        double[] dislMaxMobility = new double[grain_number];
        
        // mechanical mobilities
        double[] mechMaxMobility = new double[grain_number];
        
        // Maximal boundary energies
        double maxBoundEnergy[] = new double[grain_number];
        
        // thermal energies of cells
        double thermal_energies[] = new double[spec_size];
        
        // dislocation energies of cells
        double disl_energies[] = new double[spec_size];
        
        // mechanical energies of cells
        double mech_energies[] = new double[spec_size];
        
        // New index of cell as aresult of recrystallization
        int new_grain_index = 0;
        
        // array position of grain index of chosen neighbour at 1st coordination sphere
        int chosen_neighb1S_index;
        
        // Heat, mechanical and dislocation mobilities
        double heat_max_mobility = 0;
        double mech_max_mobility = 0;
        double disl_max_mobility = 0;
        
        // maximal specific energy of grain boundary
        double max_bound_energy  = 0;
        
        // maximal misorientation angle at grain boundary
        double max_misor_angle   = 0;
        
        // Cell volume
        cell_volume = getCellVolume();
        
        // Cell surface
        cell_surface = getCellSurface();
        
        Double velocity_D;
        
        double anis_coeff;
        
        //   anis_coeff = 100.0*growth_vector.getLength();
        //   anis_coeff = 8000.0*gr_vec_length;
        //   anis_coeff = 40000.0;
        //   anis_coeff = 1.0E12;
        
        // Векторы a и b, рост зёрен вдоль которых максимально вероятен
        VectorR3 gr_vector_a = new VectorR3();
        VectorR3 gr_vector_b = new VectorR3();
        VectorR3 gr_vector_c = new VectorR3();
        
    //    gr_vector_a = new VectorR3( 2, 0, 1);
    //    gr_vector_c = new VectorR3(-2, 0, 1);
        
    //    gr_vector_a = new VectorR3( Math.sqrt(2.0/3.0),  0.0,            Math.sqrt(1.0/3.0));
    //    gr_vector_b = new VectorR3(-Math.sqrt(1.0/6.0),  Math.sqrt(0.5), Math.sqrt(1.0/3.0));
    //    gr_vector_c = new VectorR3(-Math.sqrt(1.0/6.0), -Math.sqrt(0.5), Math.sqrt(1.0/3.0));
        
    //    Neighbour #  6; cell1S_vector =  0.0                  1.0 0.0
    //    Neighbour #  7; cell1S_vector =  0.8660254037844375  -0.5 0.0
    //    Neighbour #  9; cell1S_vector = -0.5773502691896262   0.0 0.8164965809277263
    //    Neighbour # 10; cell1S_vector =  0.28867513459481486 -0.5 0.8164965809277263
    //    Neighbour # 11; cell1S_vector =  0.28867513459481486  0.5 0.8164965809277263

    //    gr_vector_a = new VectorR3(-Math.sqrt(1.0/3.0),   0.0, Math.sqrt(2.0/3.0));
    //    gr_vector_b = new VectorR3( Math.sqrt(1.0/12.0), -0.5, Math.sqrt(2.0/3.0));
    //    gr_vector_c = new VectorR3( Math.sqrt(1.0/12.0),  0.5, Math.sqrt(2.0/3.0));
        
    //    gr_vector_a = new VectorR3( 0.0,                 1.0,  0.0);
    //    gr_vector_b = new VectorR3( Math.sqrt(3.0/4.0), -0.5,  0.0);
    //    gr_vector_c = new VectorR3(-Math.sqrt(1.0/3.0),  0.0,  Math.sqrt(2.0/3.0));
    
    //    gr_vector_a.setCoordinates(Math.sqrt(3)/2, 0.5, 0);
    //    gr_vector_b.setCoordinates(0, 2, 1);
    //    gr_vector_c.setCoordinates(Math.sqrt(3)/2, -0.5, 0);
    
    //    gr_vector_a.setCoordinates(1.0, 0.0, 0.0);
    //    gr_vector_b.setCoordinates(0.0, 1.0, 0.0);
    //    gr_vector_c.setCoordinates(0.0, 0.0, 1.0);
    
    //    double[] gr_vec_length_a = new double[2];
    //    double[] gr_vec_length_b = new double[2];
    //    double[] gr_vec_length_c = new double[2];
        
    //    gr_vec_length_a[0] = 1.0;
    //    gr_vec_length_b[0] = 0.1;
    //    gr_vec_length_c[0] = 0.1;
        
       // gr_vec_length_a[1] = 0.01;
       // gr_vec_length_b[1] = 0.1;
       // gr_vec_length_c[1] = 1.0;

    //    gr_vec_length_a[1] = 0.1;
    //    gr_vec_length_b[1] = 1.0;
    //    gr_vec_length_c[1] = 0.1;
        
     //   gr_vec_length_a = 1.0*Math.pow(anis_coeff, 175.0/256.0);
     //   gr_vec_length_b = 0.1;
     //   gr_vec_length_c = 0.1*Math.pow(anis_coeff,  65.0/81.0);
        
     //   gr_vec_length_a = 1.0*Math.pow(anis_coeff, 1.0 - 6561.0/65536.0);
     //   gr_vec_length_b = 0.1;
     //   gr_vec_length_c = 0.1*Math.pow(anis_coeff, 1.0 -  256.0/6561.0);
        
     //   gr_vec_length_a = 1.0*Math.pow(anis_coeff, -Math.log(81.0/256.0));
     //   gr_vec_length_b = 0.1;
     //   gr_vec_length_c = 1.0*Math.pow(anis_coeff, -Math.log(16.0/81.0));
        
        RecCellR3 cell, cell1S;
        int cell_index;
        int[] neighb1S_indices = new int[neighb1S_number];
        
        if(step_counter == 1)
        { 
          System.out.println();
        //  System.out.println("1st anisotropy vector: "+gr_vector_a.writeToString());
        //  System.out.println("2nd anisotropy vector: "+gr_vector_b.writeToString());
        //  System.out.println("3rd anisotropy vector: "+gr_vector_c.writeToString());
        
          cell_index = calcSingleIndex(new Three(cellNumberI/2, cellNumberJ/2, cellNumberK/2));
          cell = (RecCellR3)get(cell_index);
          
          for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
          {
            // Neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
            neighb1S_indices[neighb1S_counter] = neighbours1S[cell_index][neighb1S_counter];
            
            // Calculation of vector from "central" cell to "cell1S"
            cell1S = (RecCellR3)get(neighb1S_indices[neighb1S_counter]);
            cell1S_vectors[neighb1S_counter] = residial(cell1S.getCoordinates(), cell.getCoordinates());
          }
          
          for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          {
            System.out.println();
            System.out.println("Neighbour # "+neighb_counter+"; cell1S_vector = "+cell1S_vectors[neighb_counter].writeToString());
          //  System.out.println("    cosinus_a = "+cosinus(cell1S_vectors[neighb_counter], gr_vector_a));
          //  System.out.println("    cosinus_b = "+cosinus(cell1S_vectors[neighb_counter], gr_vector_b));
          //  System.out.println("    cosinus_c = "+cosinus(cell1S_vectors[neighb_counter], gr_vector_c));
          }
          
          System.out.println();
        }
        
        // Вектор направления роста зерна
        VectorR3 growth_vector;
        
        // Косинусы углов направления роста зерна с векторами a и b
        double cosinus_a = 0;
        double cosinus_b = 0;
        double cosinus_c = 0;
        
        // Координаты текущего элемента и его соседа на 1-й сфере
        VectorR3 cell_coord;
        VectorR3 neighb_coord;
        
        // Triple indices of neighbour cell
        Three neighb_indices;
        
        new_grain_index = -1;
        
        // Calculation of new values of dislocation density for new grains
        for(int grain_counter = 0; grain_counter < grain_number; grain_counter++)
        {
          // Obtaining of grain
          grain = (Cluster)grainsCluster.get(grain_counter);
          
        //  if(grain.getRecrystType() != Common.INITIAL_GRAIN)
          {
            // Increase of dislocation_density per time step
            grain.setDislDensity(grain.getDislDensity() + grain.getMinDislDensity());
            
            // Calculation of dislocation energy for current grain
            grain.calcDislEnergy();
            
            grainsCluster.set(grain_counter, grain);
          }
        }
        
        // Creation of arrays of parameters necessary for simulation of recrystallization
        for (int cellCounter = 0; cellCounter < spec_size; cellCounter++)
        {
          // current cell
          cell                          = (RecCellR3)get(cellCounter);
             
          // index of grain containing current cell
          grain_indices[cellCounter]    = cell.getGrainIndex();
          cellID                        = grain_indices[cellCounter];
              
          // type of grain containing current cell
          grain_types[cellCounter]     = cell.getGrainType();
          cell_type                    = grain_types[cellCounter];
          
          // Cell temperature
          temperatures[cellCounter]     = cell.getTemperature();
          
          // Cell initial temperature
          init_temperatures[cellCounter]     = (Double)initTemperatures.get(cellCounter);
          
          // Obtaining of grain containing considered cell
          grain                         = (Cluster)grainsCluster.get(cellID - 1);
          
          // orientation angles for grains
          grain_angles[cellID - 1]      = (grain.getAngle1() + grain.getAngle2() + grain.getAngle3())/3;
           
          // thermal energy of current cell
          thermal_energies[cellCounter] = cell.getThermalEnergy();
            
          // mechanical energy of current cell
          mech_energies[cellCounter]    = cell.getMechEnergy();
            
          // dislocation energy of current cell
          disl_energies[cellCounter]    = grain.getDislEnergy();
            
          if(cell_type == Common.INNER_CELL | cell_type == Common.LAYER_CELL)
          {
            // maximal values of grain boundary mobilities
            // heat mobilities
            heatMaxMobility[cellID - 1]  = grain.getMaxMobility();
            
            // dislocation mobilities
            dislMaxMobility[cellID - 1]  = grain.getDislMaxMobility();
            
            // mechanical mobilities
            mechMaxMobility[cellID - 1]  = grain.getMechMaxMobility();
            
            // maximal boundary energy
            maxBoundEnergy[cellID - 1]   = grain.getEnergyHAGB();
            
            // highest misorientation angles for grains
            angleHAGB[cellID - 1]        = grain.getAngleLimitHAGB();
          }
          else
          {
            // maximal values of grain boundary mobilities
            // heat mobilities
            heatMaxMobility[cellID - 1]  = 0;
            
            // dislocation mobilities
            dislMaxMobility[cellID - 1]  = 0;
            
            // mechanical mobilities
            mechMaxMobility[cellID - 1]  = 0;
            
            // maximal boundary energy
            maxBoundEnergy[cellID - 1]   = 0;
            
            // highest misorientation angles for grains
            angleHAGB[cellID - 1]        = 0;
          }
        }
        
        // Single index of current cell
        int cellCounter = 0;
        
        // Expected number of cell switches at current step
        // (total sum of switch probabilities at current time step)
    //    int current_prob_sum = 0;
    
        // Variable for calculation of average forces of recrystallization
        boolean calc_aver_forces = true;
        
        // Number of boundaries between internal cells
        int inner_bound_number = 0;
                    
        double aver_heat_motive_force = 0;
        double aver_mech_motive_force = 0;
        double aver_disl_motive_force = 0;
                    
        double max_heat_motive_force  = 0;
        double max_mech_motive_force  = 0;
        double max_disl_motive_force  = 0;
        
        double aver_prob_switch = 0;
        double max_prob_switch = 0;
        
        VectorR3 embryo_coord;
        
        double max_abs_cosinus;
        double min_abs_cosinus;
        double diff_cosinus;
        
        double max_cosinus = 0;
        double min_cosinus = 1;
        
        double heat_vel,  disl_vel;
        double heat_prob, disl_prob;
        
        double max_heat_prob  = 0;
        double aver_heat_prob = 0;
        
        double max_disl_prob  = 0;
        double aver_disl_prob = 0;
        
        double rand;
        
        double lattice_parameter;
                        
        // Lengths of 3 vectors of lattice anisotropy
        double lattice_vector_A_length;
        double lattice_vector_B_length;
        double lattice_vector_C_length;
        
        // Angles between vectors of lattice anisotropy
        double lattice_angle_vecA_vecB;
        double lattice_angle_vecB_vecC;
        double lattice_angle_vecC_vecA;
                        
        double gr_vec_length;
        int gr_vec_number = 0;
        
        boolean anis_account = true; // false; // 
        boolean rand_choice  = false; // true; // 
        
        //---------- START OF "RECRYSTALLIZATION" TIME STEPS -------------------
        for(int stepCounter = 0; stepCounter < recryst_step_number; stepCounter++)
        {
       //     current_prob_sum = 0;
            inner_bound_number = 0;
            
            aver_heat_motive_force = 0;
            aver_mech_motive_force = 0;
            aver_disl_motive_force = 0;
                    
            max_heat_motive_force  = 0;
            max_mech_motive_force  = 0;
            max_disl_motive_force  = 0;
            
            max_prob_switch  = 0;
            aver_prob_switch = 0;
            
            max_heat_prob  = 0;
            aver_heat_prob = 0;
            
            // Counter of cell switches
            switch_counter = 0;

            // Spatial cycle over cellular automata net 
            for(int cell_index_K = 1; cell_index_K < cellNumberK - 1; cell_index_K++)
            for(int cell_index_J = 1; cell_index_J < cellNumberJ - 1; cell_index_J++)
            for(int cell_index_I = 1; cell_index_I < cellNumberI - 1; cell_index_I++)
            {
              // Single index of current cell
              cellCounter = calcSingleIndex(cell_index_I, cell_index_J, cell_index_K);
              
              // Координаты текущего элемента
              cell_coord = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
              
              // index of grain containing current cell
              cellID        = grain_indices[cellCounter];
              
              // Type of grain containing current cell
              cell_type     = grain_types[cellCounter];
              
              // Sum of switch probabilities
              probSum = 0;
                
              // If current cell is located in specimen
              if(cell_type == Common.INNER_CELL | cell_type == Common.LAYER_CELL)
              {
                // Cell temperature
                temperature = temperatures[cellCounter];
                
                // Array of single indices of cell neighbours at 1st coordination sphere
                neighbSingleIndices = neighbours1S[cellCounter];
                
                // Indices of grains containing neighbour cells and current cell
                neighb_grain_indices = new int[neighb1S_number + 1];
                  
                // The last element of the array contains grain index for current cell
                neighb_grain_indices[neighb1S_number] = cellID;
                
                // Index of grain containing neighbour cell
                neighbCellGrainIndex = -1;
                
                // Probability of cell transition to adjacent grain
                probSwitch = 0;
                  
                // Probabilities of cell transition to adjacent grains and probability
                // of cell stay in the same grain
                probs = new double[neighb1S_number + 1];
                
                //  Calculation of velocities of movement of grain boundaries and corresponding 
                // probabilities of transition of considered cell to each grain containing neighbour cell
                for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                {
                    // Индекс соседнего элемента
                    neighb_cell_index = neighbSingleIndices[neighbCounter];
                    
                    // Neighbour cell
                    neighbCell = (RecCellR3)get(neighb_cell_index);
                    
                    // Тройной индекс соседнего элемента
                    neighb_indices = calcTripleIndex(neighb_cell_index, cellNumberI, cellNumberJ, cellNumberK);
                    
                    // Координаты соседнего элемента
                    neighb_coord = calcCoordinates(neighb_indices.getI(), neighb_indices.getJ(), neighb_indices.getK());
                    
                    // Obtaining of index of grain containing this neighbour cell
                    neighbCellGrainIndex = grain_indices[neighb_cell_index];
                    
                    // Type of grain containing current neighbour cell
                    neighb_cell_type     = grain_types[neighb_cell_index];
                      
                    // Index of grain containing current neighbour cell is added to the array
                    neighb_grain_indices[neighbCounter] = neighbCellGrainIndex;
                    
                    // Вектор возможного роста зерна
                    growth_vector = residial(cell_coord, neighb_coord);
                    
                 // embryo_coord = (VectorR3)gr_embryo_coord.get(cellID - 1);
                    embryo_coord = (VectorR3)gr_embryo_coord.get(neighbCellGrainIndex - 1);
                    
                    grain = (Cluster)grainsCluster.get(grain_indices[cellCounter] - 1);
                    
                    VectorR3 lat_vect_A_1 = grain.getLatticeVectorA();
                    
                    grain = (Cluster)grainsCluster.get(neighbCellGrainIndex - 1);
                    
                    VectorR3 lat_vect_A_2 = grain.getLatticeVectorA();
                    
                    // Calculation of difference of angles of considered and neighbour cells
             //       angle_diff = 1.0E-10 + Math.abs(grain_angles[cellID - 1] - grain_angles[neighbCellGrainIndex - 1]);
                    angle_diff = 1.0E-30 + Math.acos(cosinus(lat_vect_A_1, lat_vect_A_2))*180/Math.PI;
                    
                    // Вычисление вектора возможного роста зерна
                  //  if(embryo_coord.getLength() > 0)
                    //  growth_vector = residial(cell_coord, embryo_coord);
                 //   else
                   //   growth_vector = new VectorR3(0, 0, 0);
                    
                    // Length of growth vector
                    gr_vec_length = growth_vector.getLength();
                    
                    // mobilities at boundary
                    heat_max_mobility = Math.sqrt(heatMaxMobility[cellID - 1]*heatMaxMobility[neighbCellGrainIndex - 1]);
                    mech_max_mobility = Math.sqrt(mechMaxMobility[cellID - 1]*mechMaxMobility[neighbCellGrainIndex - 1]);
                    disl_max_mobility = Math.sqrt(dislMaxMobility[cellID - 1]*dislMaxMobility[neighbCellGrainIndex - 1]);
                    
                //    if(heat_max_mobility >0)
                  //      System.out.println("heat_max_mobility = "+heat_max_mobility);
                    
                //    if(mech_max_mobility >0)
                  //      System.out.println("mech_max_mobility = "+mech_max_mobility);
                    
                    // Heat, mechanical and dislocation  motive forces are calculated.
                    heat_motive_force = (thermal_energies[cellCounter] - thermal_energies[neighb_cell_index])/cell_volume;
                    mech_motive_force = (mech_energies[cellCounter] - mech_energies[neighb_cell_index])/cell_volume;
                    disl_motive_force = disl_energies[cellCounter] - disl_energies[neighb_cell_index];
                    
                    Double mech_motive_force_D = new Double(mech_motive_force);
                    
                    if(false)
                    if(mech_motive_force_D.isNaN())
                    {
                        System.out.println("ERROR!!! mech_motive_force = NaN!!! mech_energies["+cellCounter+"] = "+mech_energies[cellCounter]+
                                "; mech_energies["+neighb_cell_index+"] = "+mech_energies[neighb_cell_index]);
                    }
                  //  if(disl_motive_force != 0)
                  //    System.out.println("disl_motive_force = "+disl_motive_force);
                    
                    // Velocity of boundary movement
               //   velocity = heat_max_mobility*heat_motive_force + mech_max_mobility*mech_motive_force + 
               //              disl_max_mobility*disl_motive_force;
                    
                    // maximal specific energy of grain boundary
                    max_bound_energy = Math.sqrt(maxBoundEnergy[cellID - 1]*maxBoundEnergy[neighbCellGrainIndex - 1]);
                       
                    // maximal misorientation angle at grain boundary
                    max_misor_angle  = 1.0E-10 + Math.sqrt(angleHAGB[cellID - 1]*angleHAGB[neighbCellGrainIndex - 1]);
                    
                    //Calculation of specific energy of boundary between grains
                    specBoundEnergy = (max_bound_energy*angle_diff/max_misor_angle)*(1 - Math.log(angle_diff/max_misor_angle));
                    
                    // Calculation of temperature at boundary
                    boundary_temperature = 1.0E-10 + (temperatures[neighb_cell_index] + temperatures[cellCounter])/2;
                    
                    // Exponent for calculation of velocity of grain boundary
                    mobility_exponent = -(cell_surface/neighb1S_number)*specBoundEnergy/(Task.BOLTZMANN_CONST*boundary_temperature);
                    
                    // Calculation of velocity of grain boundary movement
                    velocity = (1 - Math.abs(Math.signum((neighb_cell_type - Common.INNER_CELL)*(neighb_cell_type - Common.LAYER_CELL))))*
                                disl_max_mobility*disl_motive_force*Math.pow(Math.E, mobility_exponent);
                    
                    heat_vel = (1 - Math.abs(Math.signum((neighb_cell_type - Common.INNER_CELL)*(neighb_cell_type - Common.LAYER_CELL))))*
                               (mech_max_mobility*mech_motive_force + heat_max_mobility*heat_motive_force)*Math.pow(Math.E, mobility_exponent);
                            
                    // Для углов разориентации больше максимального скорость границы равна 0.
                    velocity *= (1 + Math.signum(max_misor_angle - angle_diff))*0.5;
                    heat_vel *= (1 + Math.signum(max_misor_angle - angle_diff))*0.5;
                    
                    // Calculation of probability of transition of cell to neighbour grain
                    disl_prob = 0.5*(Math.signum(velocity) - 1)*velocity*time_step/cell_size_X;
                    heat_prob = 0.5*(Math.signum(heat_vel) - 1)*heat_vel*time_step/cell_size_X;
                    
                    if(gr_vec_length == 0)
                      probs[neighbCounter] = 0.0;
                        
                    if(gr_vec_length > 0)
                    {
                    //  System.out.println("Distance from embryo of grain # "+neighbCellGrainIndex+" to grain boundary: "+growth_vector.getLength());
                      
                      // Lattice vectors for adjacent grain 
                      gr_vector_a = grain.getLatticeVectorA();
                      gr_vector_b = grain.getLatticeVectorB();
                      gr_vector_c = grain.getLatticeVectorC();
                        
                      // Косинусы углов направления роста зерна с векторами a и b
                      cosinus_a = cosinus(growth_vector, gr_vector_a);
                      cosinus_b = cosinus(growth_vector, gr_vector_b);
                      cosinus_c = cosinus(growth_vector, gr_vector_c);
                      
                      cosinus_a = Math.abs(cosinus_a);
                      cosinus_b = Math.abs(cosinus_b);
                      cosinus_c = Math.abs(cosinus_c);
                      
                      max_abs_cosinus = Math.max(cosinus_a, Math.max(cosinus_b, cosinus_c));                   
                      min_abs_cosinus = Math.min(cosinus_a, Math.min(cosinus_b, cosinus_c));
                      
                      if(max_abs_cosinus >  1) max_abs_cosinus =  1;
                      if(max_abs_cosinus < -1) max_abs_cosinus = -1;
                      
                      if(min_abs_cosinus >  1) min_abs_cosinus =  1;
                      if(min_abs_cosinus < -1) min_abs_cosinus = -1;
                      
                      if(max_abs_cosinus > max_cosinus)  max_cosinus = max_abs_cosinus;
                      if(min_abs_cosinus < min_cosinus)  min_cosinus = min_abs_cosinus;
                      
                      gr_vec_length = 1;
                      gr_vec_number = 0;
                      
                      // Lengths of 3 vectors of lattice anisotropy
                      lattice_vector_A_length = grain.getLatticeVector_A_Length();
                      lattice_vector_B_length = grain.getLatticeVector_B_Length();
                      lattice_vector_C_length = grain.getLatticeVector_C_Length();
                      
                      anis_coeff = grain.getLatticeAnisCoeff();
                      
                      if(anis_coeff != neighbCell.getLatticeAnisCoeff())
                      {
                        System.out.println("ERROR!!! grain anis_coeff = "+anis_coeff+"; cell anis_coeff = "+neighbCell.getLatticeAnisCoeff());
                        neighbCell.setLatticeAnisCoeff(anis_coeff);
                      }
                      
                      if(Math.abs(max_abs_cosinus - cosinus_a) < 1.0E-3) 
                      {
                        //  gr_vec_length *= gr_vec_length_a[neighbCellGrainIndex % 2];
                          gr_vec_length *= lattice_vector_A_length;
                          
                          gr_vec_number++;
                      }
                      
                      if(Math.abs(max_abs_cosinus - cosinus_b) < 1.0E-3) 
                      {
                        //  gr_vec_length *= gr_vec_length_b[neighbCellGrainIndex % 2];
                          gr_vec_length *= lattice_vector_B_length;
                          
                          gr_vec_number++;
                      }
                      
                      if(Math.abs(max_abs_cosinus - cosinus_c) < 1.0E-3) 
                      {
                        //  gr_vec_length *= gr_vec_length_c[neighbCellGrainIndex % 2];
                          gr_vec_length *= lattice_vector_C_length;
                          
                          gr_vec_number++;
                      }
                      
                      if(gr_vec_number > 0)
                        gr_vec_length = Math.pow(gr_vec_length, 1.0/gr_vec_number);
                      else
                      {
                        System.out.println("ERROR!!! Number of anisotropy vectors with the same values of cosinus with growth vector = "+gr_vec_number);
                        gr_vec_length = 0.0;
                      }
                      
                   //   diff_cosinus = 2*max_abs_cosinus*max_abs_cosinus + min_abs_cosinus*min_abs_cosinus - 
                   //                    cosinus_a*cosinus_a - cosinus_b*cosinus_b - cosinus_c*cosinus_c;
                      
                   //   diff_cosinus = Math.abs(cosinus_a*cosinus_a - cosinus_b*cosinus_b);
                    
                   //   diff_cosinus = cosinus_c*cosinus_c;
                      
                   //   if(diff_cosinus > 1.00001)
                   //      System.out.println("ERROR!!! diff_cosinus = "+diff_cosinus+" > 1.00001 !!!");
                      
                   //   if(diff_cosinus > 1.0) 
                   //      diff_cosinus = 1.0;
                      
                   //   if(diff_cosinus < -0.00001)
                   //      System.out.println("ERROR!!! diff_cosinus = "+diff_cosinus+" < -0.00001 !!!");
                      
                   //   if(diff_cosinus < 0.0) 
                   //      diff_cosinus = 0.0;
                    
                      // Вычисление вероятности переключения исходя из наиболее вероятных направлений роста зерна
                //    probs[neighbCounter] *= Math.pow(cosinus_a*cosinus_a - cosinus_b*cosinus_b, 2);
                    
                  //  probs[neighbCounter] *= Math.exp(8*diff_cosinus)/Math.exp(6);
                  //  probs[neighbCounter] *= (Math.exp(8*diff_cosinus)-1)/(Math.exp(8)-1);
                      
                   //   anis_coeff = 100.0*growth_vector.getLength();
                   //   anis_coeff = 8000.0*gr_vec_length;
                   //   anis_coeff = 40000.0;
                   //   anis_coeff = 1.0E5;
                      
                      anis_account = true; // false; // 
                      rand_choice  = false; // true; // 
                      
                      // Old grains grow without account of anisotropy
                      if(neighbCellGrainIndex <= grain_number - 1)
                      {
                      //  anis_account = false;
                        
                      //  probs[neighbCounter] *= 0.1;
                      }
                      else
                      {
                      //  if(growth_vector.getLength() > 5.0)
                       //   anis_account = true;
                        
                     //   probs[neighbCounter] *= 0.1;
                     //   probs[neighbCounter] = Math.max(0.01, probs[neighbCounter]);
                        
                     //   if(probs[neighbCounter] > 0)
                     //     System.out.println("prob.sw. = "+probs[neighbCounter] );
                      }
                      
                      if(anis_account)
                      {
                      //  probs[neighbCounter] = Math.max(0.08, probs[neighbCounter]);
                          
                      //  probs[neighbCounter] *= 1.0E7;
                                
                     //   System.out.println("Prob.sw. = "+probs[neighbCounter]+"; growth_vector = "+growth_vector.writeToString()+
                     //                      "; specBoundEnergy = "+specBoundEnergy+"; angle_diff = "+angle_diff);
                        
                        if(rand_choice)
                        {
                          rand = Math.random();
                          
                          if(rand < 0.333)
                          {
                            velocity *= Math.pow(anis_coeff, cosinus_a - 1);
                            heat_vel *= Math.pow(anis_coeff, cosinus_a - 1);
                          }
                        
                          if(rand >= 0.333 & rand < 0.667)
                          {
                            velocity *= Math.pow(anis_coeff, cosinus_b - 1);
                            heat_vel *= Math.pow(anis_coeff, cosinus_b - 1);
                          }
                        
                          if(rand >= 0.667)
                          {
                            velocity *= Math.pow(anis_coeff, cosinus_c - 1);
                            heat_vel *= Math.pow(anis_coeff, cosinus_c - 1);
                          }
                        }
                        else
                        {
                      //  disl_prob *= Math.pow(anis_coeff, -Math.pow(max_abs_cosinus - 1, 2)/Math.pow(1 - Math.sqrt(1.0/6.0), 2));
                      //  disl_prob *= Math.pow(anis_coeff, 1.5*(2*max_abs_cosinus*max_abs_cosinus - 1));
                      //  disl_prob *= Math.pow(anis_coeff, max_abs_cosinus - 1);
                      //  velocity *= Math.pow(anis_coeff, max_abs_cosinus - 1);
                      //  velocity *= gr_vec_length*Math.pow(anis_coeff, Math.pow(max_abs_cosinus, 1) - 1);
                      //  velocity *= gr_vec_length*Math.pow(anis_coeff, Math.log(Math.pow(max_abs_cosinus, 8)));
                          velocity *= gr_vec_length*Math.exp(anis_coeff*(max_abs_cosinus - 1));
                          heat_vel *= gr_vec_length*Math.exp(anis_coeff*(max_abs_cosinus - 1));
                        }
                        
                        velocity_D = new Double(velocity + heat_vel);
                        
                        if(velocity_D.isNaN())
                        {
                          velocity = 0.0;
                          heat_vel = 0.0;
                          
                          lattice_parameter = grain.getLatticeParameter();
                          
                          // Angles between vectors of lattice anisotropy
                          lattice_angle_vecA_vecB = grain.getLatticeAngle_vecA_vecB();
                          lattice_angle_vecB_vecC = grain.getLatticeAngle_vecB_vecC();
                          lattice_angle_vecC_vecA = grain.getLatticeAngle_vecC_vecA();
                          
                          System.out.println("ERROR!!! velocity = NaN; gr_vec_length = "+gr_vec_length+"; anis_coeff = "+anis_coeff+"; max_abs_cosinus = "+max_abs_cosinus);
                          System.out.println("lattice_parameter = "+lattice_parameter);
                          System.out.println("lattice_vector_A_length = "+lattice_vector_A_length+"; lattice_vector_B_length = "+lattice_vector_B_length+"; lattice_vector_C_length = "+lattice_vector_C_length);
                          System.out.println("lattice_angle_vecA_vecB = "+lattice_angle_vecA_vecB+"; lattice_angle_vecB_vecC = "+lattice_angle_vecB_vecC+"; lattice_angle_vecC_vecA = "+lattice_angle_vecC_vecA);
                        }
                      }
                      
                      // Calculation of probability of transition of cell to neighbour grain
                      disl_prob = 0.5*(Math.signum(velocity) - 1)*velocity*time_step/cell_size_X;
                      heat_prob = 0.5*(Math.signum(heat_vel) - 1)*heat_vel*time_step/cell_size_X;
                      
                      probs[neighbCounter] = 0.5*(Math.signum(velocity + heat_vel) - 1)*(velocity + heat_vel)*time_step/cell_size_X;
                    
                     //   System.out.println("Prob.sw. = "+probs[neighbCounter]+"; max_abs_cosinus = "+max_abs_cosinus+
                     //           "; grain_angles[cellID - 1] = "+grain_angles[cellID - 1]+"; grain_angles[neighbCellGrainIndex - 1] = "+grain_angles[neighbCellGrainIndex - 1]);
                    }
                    
                    // TEST
                    if(neighb_cell_type != Common.INNER_CELL & neighb_cell_type != Common.LAYER_CELL)
                    {
                      if(probs[neighbCounter] > 0)
                      {
                          probs[neighbCounter] = 0;
                          System.out.println("ERROR!!! Switch from interior to facet is impossible. But prob.switch = "+probs[neighbCounter]);
                      }
                    }
                    else
                    if(false)
                    if(probs[neighbCounter] > 0 & disl_motive_force != 0)
                    if(cellID != neighbCellGrainIndex & neighbCellGrainIndex > 4 & cellID <= 4)
                      System.out.println("Cell # "+cellCounter+" can be switched from gr.# "+cellID+" to gr.# "+neighbCellGrainIndex+
                                         "; disl_motive_force = "+disl_motive_force+"; disl_max_mobility = "+disl_max_mobility+"; velocity = "+velocity+"; Switch prob.: "+probs[neighbCounter]);
                    
                    // Sum of switch probabilities for each neighbour
                    probSum += probs[neighbCounter];
                    
                    // TEST
                    if(calc_aver_forces)
                 // if(cellID != neighbCellGrainIndex)
                    // If current cell is located in specimen
                    if(neighb_cell_type == Common.INNER_CELL | neighb_cell_type == Common.LAYER_CELL)
                    {
                      inner_bound_number++;
                        
                      aver_heat_motive_force += Math.abs(heat_motive_force);
                      aver_mech_motive_force += Math.abs(mech_motive_force);
                      aver_disl_motive_force += Math.abs(disl_motive_force);
                      
                      if(Math.abs(heat_motive_force) > max_heat_motive_force)
                        max_heat_motive_force = Math.abs(heat_motive_force);
                      
                      if(Math.abs(mech_motive_force) > max_mech_motive_force)
                        max_mech_motive_force = Math.abs(mech_motive_force);
                      
                      if(Math.abs(disl_motive_force) > max_disl_motive_force)
                        max_disl_motive_force = Math.abs(disl_motive_force);
                      
                      aver_prob_switch += probs[neighbCounter];
                      
                      if(probs[neighbCounter] > max_prob_switch)
                        max_prob_switch = probs[neighbCounter];
                      
                      aver_heat_prob   += heat_prob;
                      
                      if(heat_prob > max_heat_prob)
                        max_heat_prob = heat_prob;
                      
                      aver_disl_prob   += disl_prob;
                      
                      if(disl_prob > max_disl_prob)
                        max_disl_prob = disl_prob;
                    }
                }
                
              //  if(probSum > 0)
                //  System.out.println("probSum = "+probSum);
                
                // Sum of switch probabilities cannot be less than 0 or larger than 1
                if(probSum < 0 | probSum > 1)
                {
                      System.out.println();
                      System.out.println("ERROR!!! The sum of switch probabilities cannot be out of the segment [0; 1]. But probSum = "+probSum);
                      System.out.println("step_counter = "+step_counter+"; cellCounter = "+cellCounter+"; grain index = "+cellID+"; grain type = "+cell_type);
                      System.out.println("PLEASE, decrease the value of grain boundary mobility or the value of time step!!!");
                      System.out.println();
                      step_number = 0;
                }
                
                // Вероятность того, что элемент останется в том же зерне
                probs[neighb1S_number] = 1 - probSum;
                
                // The method realizes stochastic transition of cell to one of adjacent grains.
                // New method without "if" conditions is used.
                // The method returns array position of grain index of chosen neighbour at 1st coordination sphere
                chosen_neighb1S_index = realizeCellStochSwitch_2(probs, 3);
                  
                // New index of grain for current cell
                new_grain_index  =  neighb_grain_indices[chosen_neighb1S_index];
                  
                // Cell is switched: index of grain, which will contain current  at next time step, is changed.
                newIDOfCells[cellCounter] = new_grain_index;
                
                // текущий номер зерна
                cellID = grain_indices[cellCounter];
                
                // TEST
                if(false)
                if(cellID != new_grain_index & probSum > 0)
                    System.out.println("Cell # "+cellCounter+" can be switched from gr.# "+cellID+" to gr.# "+new_grain_index+". Switch prob.: "+probs[chosen_neighb1S_index]);
              }
            }
            
            // Spatial cycle over cellular automata net: setting of new ID and Euler angles for all cells
            for(int cell_index_K = 1; cell_index_K < cellNumberK - 1; cell_index_K++)
            for(int cell_index_J = 1; cell_index_J < cellNumberJ - 1; cell_index_J++)
            for(int cell_index_I = 1; cell_index_I < cellNumberI - 1; cell_index_I++)
            {
              // Single index of current cell
              cellCounter = calcSingleIndex(cell_index_I, cell_index_J, cell_index_K);
              
              cell = (RecCellR3)get(cellCounter);
              
              // текущий номер зерна
              cellID = grain_indices[cellCounter];
              
              // будущий номер зерна
              new_grain_index = newIDOfCells[cellCounter];
              
              // Индексы "старого" и "нового" зёрен не должны совпадать для переключения элемента
              if(cellID != new_grain_index)
              {
                // Cell must be in the interior of specimen 
                if(grain_types[cellCounter] == Common.INNER_CELL)
                {
                  // Calculation of the number of cell switches
                  switch_counter++;
                  
                  // Старый индекс зерна, содержащего данный элемент, меняется на новый
                  grain_indices[cellCounter]    = new_grain_index;
                  cell.setGrainIndex(new_grain_index);
                  
                  // Получение зерна со старыми параметрами из списка 
                  grain    = (Cluster)newGrainsCluster.get(cellID - 1);
                  
                  // Получение зерна с новыми параметрами из списка 
                  newGrain = (Cluster)newGrainsCluster.get(new_grain_index - 1);
                  
                  // Setting of Euler angles for current grain 
                  cell.setEulerAngles(newGrain.getAngles());
                  
                  // Setting of new dislocation of defects to current cell
                  cell.setDislDensity(newGrain.getDislDensity());
                  
                //  System.out.println("New disl.density = "+newGrain.getDislDensity());
                  
                  // Joining of cell to adjacent grain
                  newGrain.add(cellCounter);
                  
                  // Addition of cell thermal energy to adjacent grain
                  newGrain.addThermalEnergy(cell.getThermalEnergy());

                  // Addition of cell elastic energy to adjacent grain
                  newGrain.addElasticEnergy(cell.getMechEnergy());
                
                  // Removing of cell from old grain
                  grain.remove(grain.indexOf(cellCounter));
                
                  // Extracting of cell thermal energy from grain
                  grain.addThermalEnergy(-cell.getThermalEnergy());

                  // Extracting of cell elastic energy from grain
                  grain.addElasticEnergy(-cell.getMechEnergy());
                }
            //    else
              //    System.out.println("Types of 'new' and 'old' grains must be equal to each other!!! "
                //                   + "But 'old' grain type = "+grain_types[cellID - 1]+"; 'new' grain type = "+grain_types[new_grain_index - 1]);
              }
            }
            
            // Total number of cell switches
            total_switch_counter += switch_counter;
            
            aver_heat_motive_force = aver_heat_motive_force/inner_bound_number;
            aver_mech_motive_force = aver_mech_motive_force/inner_bound_number;
            aver_disl_motive_force = aver_disl_motive_force/inner_bound_number;
            
            aver_prob_switch = 2*aver_prob_switch/inner_bound_number;
            aver_heat_prob   = 2*aver_heat_prob/inner_bound_number;
            aver_disl_prob   = 2*aver_disl_prob/inner_bound_number;
            
            if(writeToFile | step_counter % 100 == 0)
            {
                System.out.println("========-------- Step # "+step_counter+" =========---------");
                System.out.println("Number of internal cell facets: "+inner_bound_number);
                System.out.println("min_cosinus = "+min_cosinus+"; max_cosinus = "+max_cosinus);
                System.out.println("========--------===========----------=========---------");
                System.out.println("Average heat motive force:  "+aver_heat_motive_force);
                System.out.println("Average mech. motive force: "+aver_mech_motive_force);
                System.out.println("Average disl. motive force: "+aver_disl_motive_force);
                System.out.println("---------------------------");
                System.out.println("Maximal heat motive force:  "+max_heat_motive_force);
                System.out.println("Maximal mech. motive force: "+max_mech_motive_force);
                System.out.println("Maximal disl. motive force: "+max_disl_motive_force);
                System.out.println("========--------===========----------=========---------");
                System.out.println("Average heat  switch probability for internal cell facet: "+aver_heat_prob);
                System.out.println("Average disl. switch probability for internal cell facet: "+aver_disl_prob);
                System.out.println("Average total switch probability for internal cell facet: "+aver_prob_switch);
                System.out.println("---------------------------");
                System.out.println("Maximal heat  switch probability for internal cell facet: "+max_heat_prob);
                System.out.println("Maximal disl. switch probability for internal cell facet: "+max_disl_prob);
                System.out.println("Maximal total switch probability for internal cell facet: "+max_prob_switch);
          
                System.out.println("========--------===========----------=========---------");
             // System.out.println("Expected total number of cell switches:           "+total_prob_sum);
             // System.out.println("Average probability of cell switch for all steps: "+total_prob_sum/((cellNumberI-2)*(cellNumberJ-2)*(cellNumberK-2)*(stepCounter+1)));
                System.out.println("Number of cell switches at current step: "+switch_counter);
                System.out.println("Total number of cell switches:           "+total_switch_counter);
                System.out.println();
            }
        }
        //---------- END OF "RECRYSTALLIZATION" TIME STEPS -------------------
    }
    
    /** The method realizes stochastic transition of cell to one of adjacent grains.
     * @param cellCounter index of cell
     * @param probs array of probabilities of cell transition to neighbour grains
     */
    private void realizeCellStochSwitch(int cellCounter, double[] probs)
    {        
        // Realization of possibility of cell transition to adjacent grain
        
        // Random number on segment [0, 1]
        rand = Math.random();
                
        // Current sum of switch probabilities
        double currentProbSum = 0;
        
        //  Joining of considered cell to one of adjacent grains according to
        // switch probabilities
        for(int grainCounter = 0; grainCounter < probs.length; grainCounter++)
        {   
            // Probability of transition of cell to corresponding neighbour grain  
            probSwitch = probs[grainCounter];
            
            // sum of switch probabilities
            total_prob_sum+=probSwitch;
              
            //Calculation of maximal switch probability
            if(probSwitch>max_prob) // & probSwitch<=1 & probSwitch>=0)
                max_prob=probSwitch;

            // Transition of cell to neighbour grain
            if((rand > currentProbSum)&(rand < currentProbSum + probSwitch))
            {
                // counter of cell switches
                switch_counter++;
                
                // Obtaining of grain containing neighbour cell for consequence of time steps
                newNeighbGrain = (Cluster)newGrainsCluster.get(grainCounter);
                
                // Joining of cell to adjacent grain
                newNeighbGrain.add(cellCounter);
                        
                // Addition of cell thermal energy to adjacent grain
                newNeighbGrain.addThermalEnergy(cell.getThermalEnergy());

                // Addition of cell elastic energy to adjacent grain
                newNeighbGrain.addElasticEnergy(cell.getMechEnergy());
                    
                // Removing of cell from grain
                //  if(newGrain.contains(cellCounter))
                newGrain.remove(newGrain.indexOf(cellCounter));
                
                // Extracting of cell thermal energy from grain
                newGrain.addThermalEnergy(-cell.getThermalEnergy());

                // Extracting of cell elastic energy from grain
                newGrain.addElasticEnergy(-cell.getMechEnergy());
                       
                // Change of cell ID in list of ID of all cells
                newIDOfCells[cellCounter] = grainCounter;
                     
                // End of this cycle
                grainCounter = probs.length;
            }
            
            // Current sum of switch probabilities is increased
            currentProbSum = currentProbSum + probSwitch;
        }
    }
    
    /** The method realizes stochastic transition of cell to one of adjacent grains.
     * @param probs array of probabilities of cell transition to grains
     */
    private int realizeCellStochSwitch_2(double[] probs, int min_neighb_number)
    {
        // Число соседних элементов в текущем зерне
        int[] neighb_cell_numbers_in_grains = new int[grain_number];
        
        // Индекс зерна, содержащего текущий элемент 
        int neighb_grain_index;
        
        double probSum = 0;
        
        if(probs[probs.length-1] < 1)
        {
          for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
          {
            neighb_grain_index = neighb_grain_indices[neighb_counter];
            
            if(neighb_grain_index > 0 & neighb_grain_index <= grain_number)
                neighb_cell_numbers_in_grains[neighb_grain_index-1]++;
          }
        
          for(int neighb_counter = 0; neighb_counter < probs.length - 1; neighb_counter++)
          {
            neighb_grain_index = neighb_grain_indices[neighb_counter];
            
            if(neighb_grain_index == grain_number & probs[neighb_counter] > 0 & false)
            {
                System.out.print("Number of neighbours in grain # "+neighb_grain_index+": "+neighb_cell_numbers_in_grains[neighb_grain_index-1]);
                System.out.println(". Switch probability: "+probs[neighb_counter]);
            }
            
            if(neighb_grain_index > 0 & neighb_grain_index <= grain_number)
            {
                probs[neighb_counter] *= 0.5*(1 - Math.signum(min_neighb_number - 0.5 - neighb_cell_numbers_in_grains[neighb_grain_index-1]));
                probSum += probs[neighb_counter];
            }
          }
          
          probs[probs.length - 1] = 1 - probSum;
        }
        
        // Realization of possibility of cell transition to adjacent grain
        
        // Random number from segment [0, 1]
        rand = Math.random();
                
        // Sums of switch probabilities (14 values for HCP: 0, p0, p0+p1, p0+p1+...+p11, 1)
        double currentProbSum[] = new double[probs.length + 1];
        
        currentProbSum[0] = 0;
        
        // Calculation of sums of switch probabilities
        for(int neighbCounter = 1; neighbCounter <= probs.length; neighbCounter++)
            currentProbSum[neighbCounter] = currentProbSum[neighbCounter - 1] + probs[neighbCounter - 1];
        
        if(currentProbSum[probs.length-1] > 1.0)
            System.out.println("ERROR!!! Sum of switch probabilities must be in the segment [0; 1]. But the sum # "+(probs.length-1)+"equals "+currentProbSum[probs.length-1]);
        
    //    currentProbSum[probs.length] = 1;
        
        // Array of function values: if the value equals 1 then cell must be joined to corresponding grain,
        // if the value equals 0 then cell cannot be joined to this grain.
        byte[] switch_function = new byte[probs.length];
        
        // index of neighbour1S in grain that will contain current cell at next time step
        int chosen_neighb1S_index = 0;
                
        //  Joining of considered cell to one of adjacent grains according to switch probabilities
        for(int neighbCounter = 0; neighbCounter < switch_function.length; neighbCounter++)
        {   
            // Calculation of function values
            switch_function[neighbCounter] = (byte)(0.5 - 0.5*Math.signum(rand - currentProbSum[neighbCounter])*Math.signum(rand - currentProbSum[neighbCounter+1]));

            // counter of neighbour at 1st coordination sphere
            chosen_neighb1S_index += switch_function[neighbCounter]*neighbCounter;
        }
           
        return chosen_neighb1S_index;
    }
    
    /** The method performs heat flows between automata located in given parallelepiped
     * and changes automata temperatures according to constant heat flows to boundary automata. 
     * Then information on cell temperatures is written to file.
     * @param writeFileName name of file for writing
     */
    public void calcHeatFlows_Q(String writeFileName)
    {        
     // Sizes of finite element evaluated in corresponding automaton sizes
        elementSizeK = getCellNumberK();
        elementSizeJ = getCellNumberJ(); 
        elementSizeI = getCellNumberI();                
        
        // TIME STEPS        
        int[] neighbIndex = new int[0];

        neighbCells = new RecCellR3[neighb1S_number];
        neighbIndex = new int[neighb1S_number];
        heatConds = new double[neighb1S_number];
        heatCaps = new double[neighb1S_number];
        temprs = new double[neighb1S_number];
        heatConds = new double[neighb1S_number];
        heatCaps = new double[neighb1S_number];
        temprs = new double[neighb1S_number];

        String fileName = new String(writeFileName+"_empty."+Common.TEMPERATURES_FILE_EXTENSION);
        
        boolean writeToFile = false; 
        boolean changeStates = false;         
          
        if(Math.floor(Math.log10(step_number)) == Math.log10(step_number))
        {
            step_number++;       
        }
              
        System.out.println("Step number = "+step_number);
        System.out.println("Time step = "+time_step);
        
            
        for(int stepCounter = 0; stepCounter < step_number; stepCounter++)
        {
            writeToFile = false;
            changeStates = false;       
            
            // Choice of time steps when data are written into files.
            if(step_number > 100)
            {             
                if((stepCounter+1) % (step_number/output_file_number) == 0)
                  writeToFile = true;                   
            }
            else
              writeToFile = true;
            
            
            // Choice of time steps when states of cells are changed.
            if(step_number > 100)
            {    
                if((stepCounter+1) % 1 == 0)          
                   changeStates = true;
            }
            else
              changeStates = true;
            
            if(changeStates)
            System.out.println("Number of time step = "+(stepCounter+1));
            
            if(writeToFile)
            { 
                try
                {
                    bw  = new BufferedWriter(new FileWriter(writeFileName+"_"+(stepCounter+1)+"_temperatures."+Common.TEMPERATURES_FILE_EXTENSION));                   
                    bw1 = new BufferedWriter(new FileWriter(writeFileName+"_"+(stepCounter+1)+"_states_points."+Common.STATES_FILE_EXTENSION));
                }
                catch(IOException io_exc)
                {
                    System.out.println("Error: " + io_exc);
                }
            }
            
            // Spatial cycle on automata located in CA micro specimen 
            // Heat exchange between neighbouring automata
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {               
              // Calculation of single index (in ArrayList)
              // of the cell having triple indices  
                 index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;

                 System.arraycopy(neighbour_indices(indexI, indexJ, indexK), 0, neighbIndex, 0, neighb1S_number);

               // System.arraycopy(neighbour_indices(indexI, indexJ, indexK), 0, neighbIndex, 0, neighb1S_number);
                
                // Obtaining of cell and its neighbours from list of cells.
                cell = new RecCellR3((RecCellR3)get(index));
                
                for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                {   
                    neighbCells[cellCounter] = new RecCellR3((RecCellR3)get(neighbIndex[cellCounter]));
                }
                
                // Obtaining of heat conductivities of "central" cell
                heatCond = cell.get_thermal_conductivity();
                
                // Obtaining of heat conductivities of neighbour cells
                for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                {  heatConds[cellCounter] = neighbCells[cellCounter].get_thermal_conductivity();}
                
                // Obtaining of heat capacities of "central" cell
                heatCap = cell.get_specific_heat_capacity();
                
                // Obtaining of heat capacities of neighbour cells
                for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                {  
                    heatCaps[cellCounter] = neighbCells[cellCounter].get_specific_heat_capacity();
                }
                
                // Obtaining of temperatures of "central" cell
                oldTemperature = cell.getTemperature();
                        
                // Obtaining of temperatures of neighbour cells
                for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                {  
                    temprs[cellCounter] = neighbCells[cellCounter].getTemperature();
                }
            
                //------------------------------------------------------------
                // Calculation of heat influx to the "central" cell 
                // from neighbouring cells
                heatInflux = 0;
                for(int cellCounter = 0; cellCounter < neighbCells.length; cellCounter++)
                {
                    heatInflux = heatInflux + (1.0/24)*(cell_surface/get_cell_size_X())*(heatConds[cellCounter]+heatCond)*
                                (temprs[cellCounter]-oldTemperature)*time_step;
                }
                
                // Calculation of new temperature of the "central" cell
                newTemperature = oldTemperature + heatInflux/
                                (heatCap*cell.getVolume()*cell.get_density());
                
                // Creation of cell with new temperature and addition of this cell 
                // to list of new cells
                newCell = new RecCellR3(cell);
                newCell.setTemperature(newTemperature);
                
                newCells.set(index, newCell);
                       
            }
            
            // Spatial cycle over automata located in this micro specimen 
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {   
                // Calculation of single index of the cell with given indices.
                index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;
                
                // Change of cell properties.
                cell = new RecCellR3((RecCellR3)newCells.get(index));  
                
                // Replacing of the cell with single index in list of cells 
                // by the same cell with changed temperature                
                set(index, cell);
                                           
                // Writing of cell temperatures and states to files.
                if(writeToFile)
                if(indexK < elementSizeK-1)
                try
                {                       
                    bw.write(indexI+".5 "+indexJ+".5 "+indexK+".5 "+cell.getTemperature());                    
                    bw.write(" "+cell.getGrainIndex());
                    bw.write(" "+cell.get_mod_elast());
                    bw.write(" "+cell.getHeatStrain());
               //     bw.write(" "+cell.calcElasticEnergy());
                    bw.write(" "+cell.getThermalEnergy());
                    bw.newLine();
                    bw.flush();
               
                    bw1.write(indexI+".5 "+indexJ+".5 "+indexK+".5 "+cell.getState());
                    bw1.newLine();
                    bw1.flush();
                }
                catch(IOException io_exc)
                {
                    System.out.println("Error: " + io_exc);
                }                
            }
            
//            setBoundaryConditions(boundCond);
                                
            // Probabilistic change of states of cells
            if(changeStates)
            for(int cellCounter = 0; cellCounter < size(); cellCounter++)
            {
                cell = new RecCellR3((RecCellR3)get(cellCounter));
                byte state = cell.getState();
                if(state % 2 == 0)
                  switchState(cell);
            }            
           
            if(writeToFile)
            try
            {   
                bw.close();
                bw1.close();
            }    
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
        }
    }    

    /** The method simulates physical process
     * @param writeFileName name of file for writing of simulation results
     * @param heat_flows parameter responsible for starting of heat transfer simulation
     * @param mech_flows parameter responsible for starting of mechanical loading simulation
     * @param recryst parameter responsible for starting of recrystallization simulation
     * @param cracks parameter responsible for starting of crack formation simulation
     * @param equilibrium_state variable responsible for equilibrium state of specimen
     */         
    public void calcProcess(String writeFileName, byte heat_flows, byte mech_flows, 
                            byte recryst, byte cracks, byte equilibrium_state)
    {
        /* Block 11*/
        
        // Sizes of finite element evaluated in corresponding automaton sizes
        elementSizeK = getCellNumberK();
        elementSizeJ = getCellNumberJ(); 
        elementSizeI = getCellNumberI();
        
        // Parameters used in method "calcHeatFlows_T"
        neighbIndices = new int[neighb1S_number];
        heatConds = new double[neighb1S_number];
        heatCaps = new double[neighb1S_number];
        temprs = new double[neighb1S_number];
     //   neighb_grain_indices = new double[neighb1S_number];
        newCells = new ArrayList();
        heat_step_number = 1;     // number of time steps when heat transfer is simulated
        recryst_step_number = 1;  // number of time steps when recrystallization is simulated
        mech_step_number = 1;     // number of time steps when heat transfer is simulated
        crack_step_number = 1;     // number of time steps when crack propagation is simulated
    //  neighbIndices = new Three[neighb1S_number];
    //  fileName = new String(writeFileName+"_empty."+Common.TEMPERATURES_FILE_EXTENSION);
        
        // Total number of cells in specimen (including boundary cells)
        int spec_size = size();

        // Parameters used in method "calcRecrystallization"
        // Array of probabilities of transition of cell to each of grains
        probs = new double[grainsCluster.size()];
        newGrainsCluster = new ArrayList(grainsCluster);

        // Creation of array of ID of cells
        newIDOfCells = new int[spec_size];
        
    //    mechInfluxes = new double[spec_size];

        // Calculation of cell volume
        calcCellVolume(packing_type);
        
        // Calculation of cell surface area
        calcCellSurface(packing_type);
        
        // Logical variable responsible for calculation of parameters (stress, velocity) of cell boundary
        boolean calc_bound_parameters = true; // false;//         
        
        // Logical value responsible for writing of cell parameters to file
        boolean writeToFile = false;
        
        // Logical value responsible for writing of parameters (stress, velocity) of cell boundary to file
        boolean writeCellBoundsToFile = false;
        
        // Logical value responsible for loading in first half of time period
        boolean half_time_loading = true; // false; //         
        
        total_switch_number = 0; // total number of cell switches
        period_switch_number = 0;// number of cell switches in certain time period
        
        // Total number of damaged cells
        total_damaged_cells_number = 0;
        
        sum_of_abs_values_of_angles = true; // false; // 
        
        //  newTemperature = 400.0;
        
        // Increasing of time step number if cell boundary parameters must be calculated
        if(calc_bound_parameters)
            step_number++;
        
        System.out.println("The number of time steps: "+step_number);//*(heat_step_number+recryst_step_number)/2);
        System.out.println("The value of time step: "+time_step);
        
        // Creation of new list of cells and array of grain indices of cells
        for (int cellCounter = 0; cellCounter<spec_size; cellCounter++)
        {
            cell = (RecCellR3)get(cellCounter);
            cellID = cell.getGrainIndex();
            
            // Creation of new list of cells
            newCells.add(cell);

            // Creation of list of ID of cells
            newIDOfCells[cellCounter] = cellID-1;
        }
        
        // Array of grains
     // grains = new Cluster[grainsCluster.size()];
        grains = new Cluster[size()];

        // Total number of grains
        grain_number = grainsCluster.size();
        
        // Initialization of array of grains
        for(int grain_counter = 0; grain_counter < size(); grain_counter++)
        {
            if(grain_counter < grainsCluster.size())
                grains[grain_counter] = (Cluster)grainsCluster.get(grain_counter);
            else
                grains[grain_counter] = new Cluster();
            
       //     System.out.println(grains[grain_counter].getThermalEnergy()+" "+
      //                         grains[grain_counter].getElasticEnergy());

  //          grains[grain_counter].setElasticEnergy(0);
        }
        
        // Current number of grains
        int old_grain_number = grain_number;
            
        // Variable for search of maximal value of cell temperature at current time step
        double max_temperature = 0;

        // Variable for search of maximal value of cell mechanical energy at current time step
        double max_mech_stress = 0;

        // Variable for search of minimal value of cell mechanical energy at current time step
        double min_temperature = 0;

        // Variable for search of minimal value of cell mechanical energy at current time step
        double min_mech_stress = 0;

        // Variables for search of maximal values of cell coordinates
        double max_coord_X = 0;
        double max_coord_Y = 0;
        double max_coord_Z = 0;

        // Temperature of current cell
        double cell_temperature = 0;

        // Stress of current cell
        double cell_mech_stress = 0;

        // Principal stress of current cell
        double principal_stress = 0;

        // Force moment of current cell
        VectorR3 force_moment;

        // Component Z of force moment
        double force_moment_Z = 0;

        // Sign of force moment
        byte force_moment_sign = 0;

        // Coordinates of current cell
        double coord_X = 0;
        double coord_Y = 0;
        double coord_Z = 0;

        VectorR3 cell_coordinates;

        // Variable responsible for realization of search of maximal values
        // of coordinates of cells
        boolean search_max_coordinates = true;

        int grain_size = 0;
        double grain_heat_energy = 0;
        int grain_type;
        
        double lattice_parameter;
        double lattice_anis_coeff;
        
        // Lengths of 3 vectors of lattice anisotropy
        double lattice_vector_A_length;
        double lattice_vector_B_length;
        double lattice_vector_C_length;
        
        // Angles between vectors of lattice anisotropy
        double lattice_angle_vecA_vecB;
        double lattice_angle_vecB_vecC;
        double lattice_angle_vecC_vecA;
        
        VectorR3 lat_vect_A;
        VectorR3 lat_vect_B;
        VectorR3 lat_vect_C;
        
//        double grain_mech_energy = 0;
        int specimen_size = 0;
        double total_heat_energy = 0;
        double total_grain_mech_energy = 0;
        double total_cell_mech_energy = 0;
        
        // Total torsion energy of cells
        double total_tors_energy = 0;
        
        // Number of cells in the interior of specimen
        int inner_cell_number=0;
        
        System.out.println();
        System.out.println("Time step # 0");
        System.out.println();

        // Calculation of force moments for all cells
        for(int cell_counter=0; cell_counter<spec_size; cell_counter++)
        {
            cell = (RecCellR3)get(cell_counter);
            grain_type = cell.getGrainType();

            if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
            {
              // Setting of new value of force moment
              if(!calc_force_mom_new)
              {
                if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                  cell.setForceMoment(calcSummaryForceMoment(cell_counter, 0));

                if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                  cell.setForceMoment(calcSummaryForceMomentSCP(cell_counter));
              }
                
              if(calc_force_mom_new)
                cell.setForceMoment(calcSummaryForceMoment(cell_counter));
            }
        }
        
        for(int grainCounter = 0; grainCounter < grains.length; grainCounter++)
          //      if(grains[grainCounter].getType()==Common.INNER_CELL)
        {
            // Number of cells in grain
            grain_size = grains[grainCounter].size();

            // Grain heat energy
            grain_heat_energy = grains[grainCounter].getThermalEnergy();

            // Grain mechanical energy
            grain_mech_energy = grains[grainCounter].getElasticEnergy();

            // Grain type
            grain_type = grains[grainCounter].getType();

            if(grain_size > 0)
            {
              System.out.print  ("  Grn #"+(grainCounter+1)+
                                 " type: "+grain_type+
                                 " size: "+grain_size+
                                 " sp.ht.energy: "+grain_heat_energy/Math.max(1, grain_size)+
                                 " el.energy: "+grain_mech_energy);/*grainSize*getCellVolumeHCP()*///+
                                //   "; eff. velocity "+grains[grainCounter].getMechMaxMobility());
              
              lattice_parameter  = grains[grainCounter].getLatticeParameter();
              lattice_anis_coeff = grains[grainCounter].getLatticeAnisCoeff();
                          
              // Lengths of 3 vectors of lattice anisotropy
              lattice_vector_A_length = grains[grainCounter].getLatticeVector_A_Length();
              lattice_vector_B_length = grains[grainCounter].getLatticeVector_B_Length();
              lattice_vector_C_length = grains[grainCounter].getLatticeVector_C_Length();
              
              // Angles between vectors of lattice anisotropy
              lattice_angle_vecA_vecB = grains[grainCounter].getLatticeAngle_vecA_vecB();
              lattice_angle_vecB_vecC = grains[grainCounter].getLatticeAngle_vecB_vecC();
              lattice_angle_vecC_vecA = grains[grainCounter].getLatticeAngle_vecC_vecA();
              
              lat_vect_A = grains[grainCounter].getLatticeVectorA();
              lat_vect_B = grains[grainCounter].getLatticeVectorB();
              lat_vect_C = grains[grainCounter].getLatticeVectorC();
              
            //  System.out.print(" anis_cf: "+lattice_anis_coeff+" lat_prm: "+lattice_parameter);
              System.out.print  (" lat_vec.lengths: A= "+lattice_vector_A_length+" B= "+lattice_vector_B_length+" C= "+lattice_vector_C_length);
              System.out.println(" lat_angles: AB= "+lattice_angle_vecA_vecB+" BC= "+lattice_angle_vecB_vecC+" CA= "+lattice_angle_vecC_vecA);
              System.out.println("   lattice vector A: "+lat_vect_A.writeToString());
              System.out.println("   lattice vector B: "+lat_vect_B.writeToString());
              System.out.println("   lattice vector C: "+lat_vect_C.writeToString());
            }
        }
        
        // Module of force moment of cellular automaton
     //   double moment_module = 0;

        // List of displacement toposes perpendicular to cell boundaries
        bound_norm_toposes = new ArrayList[spec_size];

        for(int cell_counter = 0; cell_counter<spec_size; cell_counter++)
            bound_norm_toposes[cell_counter] = new ArrayList(neighb1S_number);
        
        // Writing of file with initial parameters of cells
        writeResultsFile(true, writeFileName, -1, heat_flows, mech_flows, recryst, cracks);
        
        System.out.println();

        // Name of file with information about portions of inner cells in each state
        // at each time step
        String graph_states_file = writeFileName+"_graph_states.txt";
        
        // Creation of stream for this file
        try
        {
            bw1 = new BufferedWriter(new FileWriter(graph_states_file));
            bw3 = new BufferedWriter(new FileWriter(writeFileName+"_aver_forces.txt"));
            bw_total = new BufferedWriter(new FileWriter(writeFileName+"_total_energies.txt"));

            bw3.write("# Each string contains:\n"
                    + "#  1. Step counter;\n"
                    + "#  2. The average of heat motive forces at grain boundaries;\n"
                    + "#  3. The average of mechanical motive forces at grain boundaries;\n"
                    + "#  4. The average of dislocation motive forces at grain boundaries without Zener pinning particles;\n"
                    + "#  5. The average of dislocation motive forces at grain boundaries;\n"
                    + "#  6. The average of Zener pinning forces at grain boundaries;\n"
                    + "#  7. The average of velocities of grain boundaries;\n"
                    + "#  8. The maximal velocity of grain boundary;\n"
                    + "#  9. The average probability of cell switch;\n"
                    + "# 10. The maximal probability of cell switch.\n");
            
            bw_total.write("# Each string contains:\n"
                    + "# 1. current time;\n"
                    + "# 2. total influx of mechanical energy;\n"
                    + "# 3. total mechanical energy of inner cells (without inner boundary cells);\n"
                    + "# 4. total torsion energy of inner cells (without inner boundary cells);\n"
                    + "# 5. current thermal energy influx to outer cells;\n"
                    + "# 6. current thermal energy influx to inner cells;\n"
                    + "# 7. total thermal energy influx to outer cells;\n"
                    + "# 8. total thermal energy influx to inner cells.\n");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }

        // Writing of information about portions of inner cells in each state
        // at zero time step
        createGraphStates(-1);

        // List of displacement toposes normal to cell boundaries at previous time step
    //    old_bound_norm_toposes = new ArrayList(0);

        // Cell coordinates
        cell_coord = new VectorR3();

        // Coordinates of neighbour cell
        neighb_coord = new VectorR3();

        // Normal vector for boundary of current cell and its neighbour
        // directed from centre of current cell to centre of neighbour
        bound_vect = new VectorR3();

        // Summary displacement vector of current cell
        displ_vector = new VectorR3();

        // Point at centre of boundary facet of current neighbour cells
        bound_point = new PointR3(0,0,0);

        // Topos consisting of boundary point and vector of normal displacement of boundary
        bound_norm_topos = new ToposR3();

        // Array of boolean variables: if cell is damaged
        // then corresponding element equals "true", else - "false".
        damaged_cells = new boolean[cellNumberI*cellNumberJ*cellNumberK];

        for(int cell_counter = 0; cell_counter<damaged_cells.length; cell_counter++)
          damaged_cells[cell_counter] = false;

        /* Block 12 */  
        /*
        // Variable responsible for account of anisotropy of simulated medium
        anisotropy = true;//false;//
        
        // Vector of anisotropy of simulated medium
        anis_vector = new VectorR3(0, 1, 1); //(0, 1, -1);//
        
        // Coefficient of anisotropy of simulated medium
        coeff_anisotropy = 10;
        */
        
        // Array of stresses of cells at previous time step
        previous_stresses = new double[size()];
        
        // Array of logical values responsible for recording of parameters of each cell boundary
        cell_bound_records = new boolean[size()][neighb1S_number];
        
        writeCellBoundsFile(writeFileName, -1);
           
        total_mech_energy_influx = 0;
        
        /** Array of values of mechanical energy of each cell at previous time step */
        prev_mech_energies = new double[size()];
   
        /** Array of values of mechanical energy of each neighbour cell at previous time step */
        neighb_prev_mech_energies = new double[size()][neighb1S_number];
        
        /** Array of values of heat energy of each cell at previous time step */
        prev_heat_energies = new double[size()];
   
        /** Array of values of heat energy of each neighbour cell at previous time step */
        neighb_prev_heat_energies = new double[size()][neighb1S_number];
        
        // Maximal and minimal values of mechanical energy for neighbour cells at previous time step
        double prev_max_neighb_mech_energy = -1.0E308;
        double prev_min_neighb_mech_energy =  1.0E308;
            
        // Maximal and minimal values of mechanical energy for neighbour cells at current time step
        double max_neighb_mech_energy      = -1.0E308;
        double min_neighb_mech_energy      =  1.0E308;
        
        // Maximal and minimal values of mechanical energy for neighbour cells at previous time step
        double prev_max_neighb_heat_energy = -1.0E308;
        double prev_min_neighb_heat_energy =  1.0E308;
            
        // Maximal and minimal values of mechanical energy for neighbour cells at current time step
        double max_neighb_heat_energy      = -1.0E308;
        double min_neighb_heat_energy      =  1.0E308;
            
        double neighb_heat_energy = 0;
        
        // mechanical energy of current neighbour cell at current time step
        double neighb_mech_energy = 0;
        
        // mechanical energy of current cell at current time step
        double cell_mech_energy = 0;
        
        // heat energy of current cell at current time step
        double cell_heat_energy = 0;
        
        int neighb_cell_index;
        
        // Number of damaged cells
        int dam_cell_number = 0;
        
        // Number of cells damaged at current time step
        int curr_dam_cell_num = 0;
        
        // Total number of cell switches
        total_switch_counter = 0;
              
        // Expected total number of cell switches
        total_prob_sum = 0;
        
        // ==============--------------==================-----------------
        // Variables for simulation of generation of embryos of new grains in the interior of old grains
        
        // Variable, which determines simulation of generation of embryos of new grains in the interior of old grains
        boolean recryst_new_grains     = false;
        
        if(recryst == Common.EXECUTE_METHOD) 
          recryst_new_grains = true;
          
        // Temperature of start of generation of embryos of new grains in the interior of old grains
        double low_tempr_recryst = 0;
                         
        // Maximal temperature of generation of embryos of new grains in the interior of old grains
        double high_tempr_recryst = 0;
        
        // Average temperature of generation of embryos of new grains in the interior of old grains
        double aver_tempr_recryst = 0;
        
        // Maximal probability of recrystallization
        double max_prob_recryst = 0;
        
        // Maximal probability of twinning
        double max_prob_twinning = 0;
        
        // Temperature of finish of grain twinning
        double min_twinning_temperature = 0;
            
        // Temperature of start of grain twinning
        double twinning_temperature = 0;
        
        // Random value for stochastic switch of cell under recrystallization
        double rand_recryst = 0;
        
        // volume portion of particles
        double part_vol_fraction = 0;
        
        // average volume portion of particles
        double aver_part_vol_fraction = 0;
            
        // Index of current neighbour cell
        int neighb_index = 0;
            
        // Current neighbour cell
        RecCellR3 neighb_cell;
            
        // location type of grain containing current neighbour cell
        byte neighb_grain_type;
            
        // Grain containing current cell and grain generated in the interior of old grain
        Cluster old_grain, new_grain;
            
        // Variable, which determines cell switch to new grain
        boolean cell_switch = false;
        
        int cell_index_I = 0, cell_index_J = 0, cell_index_K = 0;
        
        int old_grain_index;
        
        boolean embryo_generation, double_grain_generation;
        
        byte old_grain_rec_type, neighb_grain_rec_type;
        
        // Number of new grains
        int new_gr_number = 6;
        
        // Numbers of twinning grains grown grom each new grain
        int[] neighb_grain_twin_numbers = new int[2*new_gr_number];
        
        for(int new_gr_counter = 0; new_gr_counter < 2*new_gr_number; new_gr_counter++)
          neighb_grain_twin_numbers[new_gr_counter] = 0;
            
        // List of indices of cells containing embryos of new grains
        Cluster new_grain_embryos = new Cluster();
            
        // Number of grains generated in the interior of old grains
        new_grain_number = 0; 
        
        int cells_joined_to_new_embryos = 0;
        int cells_joined_to_existing_embryos = 0;
        
        int cell_grain_index   = -1;
        int neighb_grain_index = -1;
        
        int new_single_grain_number = 0;
        int new_double_grain_number = 0;
        
        int error_num = 0;
        // ==============--------------==================-----------------
        
        // Total number of grains 
        grain_number = grainsCluster.size();
        
        // List of coordinates of grain embryos
        gr_embryo_coord = new ArrayList(grain_number);
            
        System.out.println("Number of grains: "+grain_number);
        
        for(int gr_index = 0; gr_index < grain_number; gr_index++)
        {
            System.out.println("Grain # "+gr_index);
            gr_embryo_coord.add(new VectorR3(0, 0, 0));
        }
        
        boolean calcRecrystForParalCalc = true;
        boolean calcRecrystallization   = false;
        
        boolean testing;
        
        // Variable responsible for calculation of torsion energy for each cell
        boolean calc_torsion_energy = true;// false;// 
        
        // Variable responsible for calculation of total torsion energy of cells
        boolean calc_total_energy = true;// false;//
        
        // Variable responsible for calculation of cell angle velocities and torsion angles
        boolean calc_angle_velocity = true;// false;//
        
        // Angle velocity of cell
        VectorR3 angle_velocity = new VectorR3();
        
        // Change of cell torsion angle
        VectorR3 torsion_angle_change = new VectorR3();
        
        // Type of cell location
        byte cell_loc_type = -1;
        
        // cell torsion energy change
        double torsion_energy_change = 0;
        
        // total cell torsion energy
        double total_cell_torsion_energy = 0;
        
        // Number of atoms in current cell
        int atom_number   = 0;
        
        // Number of defects in current cell
        int defect_number = 0;
        
        // Total number of atoms and defects in current cell
        int total_number  = 0;
        
        // Coefficient for calculation of current value of dislocation density
        double coeff;
        
        // Initial mechanical energy of cell
        double init_mech_energy = 0;
        
        // Setting of boundary conditions
      //  setBoundaryConditions(boundCond, writeToFile, step_counter);
        
        // Simulation of process       
        if(heat_flows+mech_flows+recryst+cracks>0)
        for(step_counter = 1; step_counter <= step_number; step_counter++)
        {
            if(writeToFile)
                writeCellBoundsToFile = true;
            else
                writeCellBoundsToFile = false;
            
            writeToFile = false;
        //    writeCellBoundsToFile = false;
            
            if(output_file_number==0)
            {
                System.out.println("The number of output files equals 0!!!");
                System.out.println("The processes are not simulated!!!");
                
                heat_flows = Common.IGNORE_METHOD;
                mech_flows = Common.IGNORE_METHOD;
                cracks     = Common.IGNORE_METHOD;
                recryst    = Common.IGNORE_METHOD;
            }
            else
            {
              // Choice of time steps when data are written into files.
              if(step_number > output_file_number)
              {
              //  if(step_counter == 1 | step_counter == 2)
                //  writeToFile = true;
                  
             //   if(step_counter <= 100)
             //   if(step_counter % 10 == 0)
             //       writeToFile = true;

                if(step_counter <= 1000)
                if(step_counter % 100 == 0)
             //   if(step_counter % 10 == 0)
                    writeToFile = true;

                if(step_counter <= 10000)
                if(step_counter % 1000 == 0)
                    writeToFile = true;

                if(step_counter <= 100000)
                if(step_counter % 10000 == 0)
                    writeToFile = true;
                
                if(step_counter % (step_number/output_file_number) == 0)
                    writeToFile = true;
                
                // Information about cell boundaries must be written to file at next time step.
                if(step_counter-1 <= 100)
                if(step_counter-1 % 10 == 0)
                    writeCellBoundsToFile = true;

                if(step_counter-1 <= 1000)
                if(step_counter-1 % 100 == 0)  
                    writeCellBoundsToFile = true;

                if(step_counter-1 <= 10000)
                if(step_counter-1 % 1000 == 0)
                    writeCellBoundsToFile = true;

                if(step_counter-1 <= 100000)
                if(step_counter-1 % 10000 == 0)
                    writeCellBoundsToFile = true;
                
                if(step_counter-1 % (step_number/output_file_number) == 0)
                    writeCellBoundsToFile = true;
                
                writeCellBoundsToFile = false;
              }
              else
              {
                  writeToFile = true;
                  writeCellBoundsToFile = true;
              }
              
              if(!calc_bound_parameters)
              {
                  writeCellBoundsToFile = false;
                  
              //    System.out.println("Information about cell boundaries is NOT written to files...");
              }
            }
            
            if(writeToFile)
            {
              System.out.println("Time step # "+step_counter);
              
           //   System.out.println("torsion_energy_coeff      = " + torsion_energy_coeff);
           //   System.out.println("bound_interaction_type  = " + bound_interaction_type);
             // System.out.println("min_neighbours_number   = " + min_neighbours_number);              
              
              if(anisotropy)
              {
                  System.out.println("Anisotropic medium is simulated...");
                  System.out.println("Anisotropy vector:     ("+anis_vector.getX()+"; "+anis_vector.getY()+"; "+anis_vector.getZ()+").");
                  System.out.println("Anisotropy coefficient: "+coeff_anisotropy);
              }
            }
            
          //  cell = (RecCellR3)get(2701);
               
          //  System.out.println("Cell # 2701; stress = "+cell.getMechStress()+"; grain type: "+cell.getGrainType());
              
         //   System.out.println("Cell # 2701; stress = "+cell.getMechStress());
          //  System.out.println();
            
          //  sortBoundNormToposes();
          /*
            // Keeping of old values of toposes normal to cell boundaries
            old_bound_norm_toposes = new ArrayList(0);

            for(int topos_counter = 0; topos_counter<bound_norm_toposes.size(); topos_counter++)
               old_bound_norm_toposes.add(bound_norm_toposes.get(topos_counter));
           */
            
            // Setting of boundary conditions
            setBoundaryConditions(boundCond, writeToFile, step_counter);
            
            // Simulation of heat energy transfer
            if(heat_flows==Common.EXECUTE_METHOD)
            {
                if(writeToFile)
                    System.out.println("Heat transfer is simulated...");            
                
                calcHeatFlows_T(writeFileName, false, step_counter);
            }
            
            // Simulation of mechanical energy transfer
            if(mech_flows==Common.EXECUTE_METHOD)
            {
                if(writeToFile)
                    System.out.println("Mechanical energy transfer is simulated...");

             //           cell = (RecCellR3)get(1590);
               //         System.out.println("mech_flows: cell index: "+1590+"; disl. density: "+cell.getDislDensity()+"; energyHAGB: "+cell.getEnergyHAGB());
                  
               calcMechEnergyFlows(writeFileName, false, calc_bound_parameters, step_counter);
          //   calcAllMechEnergyFlows(writeFileName, false, step_counter);
          
               
              //  if(writeToFile)
               //     System.out.println("Number of cell boundaries: "+bound_norm_toposes.size());
            }
            
            // Setting of boundary conditions
          //  setBoundaryConditions(boundCond, writeToFile, step_counter);
            
            // Simulation of crack formation
            if(cracks==Common.EXECUTE_METHOD)
            if(false)
            {
                if(writeToFile)
                    System.out.println("Crack formation is simulated...");

                /* Block 15 */
             //   calcCrackFormation(writeFileName, false, step_counter);
                curr_dam_cell_num = calcCrackPropagation();
                dam_cell_number  += curr_dam_cell_num;
                
                if(writeToFile)
                {
                    System.out.println("Number of cells damaged at time step #"+step_counter+": "+curr_dam_cell_num);
                    System.out.println("Total number of damaged cells:        "+dam_cell_number);
                }
            }
            
            if(recryst==Common.EXECUTE_METHOD)
            {
              calcRecrystForParalCalc = true; // false; // 
              calcRecrystallization   = false; // true; //
            }
            else
            {
              calcRecrystForParalCalc = false; // true; // 
              calcRecrystallization   = false; // true; //
            }
            
            // Simulation of recrystallization
            if(recryst==Common.EXECUTE_METHOD)
            {
                if(false)//writeToFile)
                    System.out.println("Recrystallization is simulated...");
                
                // TEST
                if(step_counter == 1)
                {
                    System.out.println("min_neighbours_number = "+min_neighbours_number);
                    System.out.println("equilibrium_state = "+equilibrium_state);
               //     System.out.println("particles_volume_fraction = "+particles_volume_fraction);
               //     System.out.println("particle_radius = "+particle_radius);         
                }
                
                if(calcRecrystallization)
                    calcRecrystallization(writeFileName, writeToFile, step_counter, equilibrium_state);
                
                if(calcRecrystForParalCalc)
                    calcRecrystForParalCalc(writeFileName, writeToFile, step_counter, equilibrium_state);
            }
            
            // Maximal and minimal values of mechanical energy for neighbour cells at previous time step
            prev_max_neighb_mech_energy = -1.0E308;
            prev_min_neighb_mech_energy =  1.0E308;
            
            // Maximal and minimal values of mechanical energy for neighbour cells at current time step
            max_neighb_mech_energy      = -1.0E308;
            min_neighb_mech_energy      =  1.0E308;
            
            // Maximal and minimal values of mechanical energy for neighbour cells at previous time step
            prev_max_neighb_heat_energy = -1.0E308;
            prev_min_neighb_heat_energy =  1.0E308;
            
            // Maximal and minimal values of mechanical energy for neighbour cells at current time step
            max_neighb_heat_energy      = -1.0E308;
            min_neighb_heat_energy      =  1.0E308;
            
            // Spatial cycle over automata located in this micro specimen:
            // comparison of previous and current gradients of mechanical energy
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
                // Calculation of single index of the cell with given indices
                index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;

                // Current central cell
                cell = (RecCellR3)newCells.get(index);
                
                testing = false;// true;// 
                
                if(testing)
                for(int neighbCounter = 0; neighbCounter<neighb1S_number; neighbCounter++)
                {
                  neighb_cell_index = neighbours1S[index][neighbCounter];

                  if((neighb_cell_index>-1)&(neighb_cell_index<size()))
                  {
                    // Search of minimal and maximal previous values of mechanical energy for neighbours
                    if(neighb_prev_mech_energies[index][neighbCounter] >= prev_max_neighb_mech_energy)
                        prev_max_neighb_mech_energy = neighb_prev_mech_energies[index][neighbCounter];
                    
                    if(neighb_prev_mech_energies[index][neighbCounter] <= prev_min_neighb_mech_energy)
                        prev_min_neighb_mech_energy = neighb_prev_mech_energies[index][neighbCounter];
                    
                    // Search of minimal and maximal previous values of heat energy for neighbours
                    if(neighb_prev_heat_energies[index][neighbCounter] >= prev_max_neighb_heat_energy)
                        prev_max_neighb_heat_energy = neighb_prev_heat_energies[index][neighbCounter];
                    
                    if(neighb_prev_heat_energies[index][neighbCounter] <= prev_min_neighb_heat_energy)
                        prev_min_neighb_heat_energy = neighb_prev_heat_energies[index][neighbCounter];
                      
                    // Current neighbour cell
                    neighbCell = (RecCellR3)get(neighb_cell_index);
                    
                    // mechanical energy of neighbour cell
                    neighb_mech_energy = neighbCell.getMechEnergy();
                    
                    // Search of minimal and maximal current values of mechanical energy for neighbours
                    if(neighb_mech_energy >= max_neighb_mech_energy)
                        max_neighb_mech_energy = neighb_mech_energy;
                    
                    if(neighb_mech_energy <= min_neighb_mech_energy)
                        min_neighb_mech_energy = neighb_mech_energy;
                    
                    // heat energy of neighbour cell
                    neighb_heat_energy = neighbCell.getThermalEnergy();
                    
                    // Search of minimal and maximal current values of heat energy for neighbours
                    if(neighb_heat_energy >= max_neighb_heat_energy)
                        max_neighb_heat_energy = neighb_heat_energy;
                    
                    if(neighb_heat_energy <= min_neighb_heat_energy)
                        min_neighb_heat_energy = neighb_heat_energy;
                  }
                }
                
                if(testing)
                {
                  cell_mech_energy = cell.getMechEnergy();                
                  cell_heat_energy = cell.getThermalEnergy();
                }
                               
                // Comparison of previous and current gradients of mechanical energy for central cell and its neighbours 
                if(testing)
                if(prev_mech_energies[index] <= prev_max_neighb_mech_energy & cell_mech_energy > prev_max_neighb_mech_energy)
                {
                    System.out.println();
                    System.out.println("STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!");
                    System.out.println("step # "+step_counter);
                    System.out.println("cell # "+index+": prev.mech.en. = "+prev_mech_energies[index]+"; curr.mech.en. = "+cell_mech_energy);                    
                    System.out.println("prev_max_neighb_mech_energy = "+prev_max_neighb_mech_energy+"; max_neighb_mech_energy = "+max_neighb_mech_energy);
                    System.out.println("cell coordinates: ("+cell.getCoordinates().writeToString()+"); indices: ("+cell.getIndices().writeToString()+").");
                    System.out.println();
                    System.out.println("Please, decrease the value of time step!");
                    System.out.println();
                        
                    step_number = 0;
                    writeToFile = true;
                }
                
                if(testing)
                if(prev_mech_energies[index] > prev_min_neighb_mech_energy & cell_mech_energy < prev_min_neighb_mech_energy)              
                {
                    System.out.println();
                    System.out.println("STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!");
                    System.out.println("step # "+step_counter);
                    System.out.println("cell # "+index+": prev.mech.en. = "+prev_mech_energies[index]+"; curr.mech.en. = "+cell_mech_energy);
                    System.out.println("prev_min_neighb_mech_energy = "+prev_min_neighb_mech_energy+"; min_neighb_mech_energy = "+min_neighb_mech_energy);
                    System.out.println("cell coordinates: ("+cell.getCoordinates().writeToString()+"); indices: ("+cell.getIndices().writeToString()+").");
                    System.out.println();
                    System.out.println("Please, decrease the value of time step!");
                    System.out.println();
                    
                    step_number = 0;
                    writeToFile = true;
                }
                
                // Comparison of previous and current gradients of heat energy for central cell and its neighbours 
                if(testing)
                if(prev_heat_energies[index] < prev_max_neighb_heat_energy & cell_heat_energy > prev_max_neighb_heat_energy)
                {
                    System.out.println();
                    System.out.println("STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!");
                    System.out.println("step # "+step_counter);
                    System.out.println("cell # "+index+": prev.heat.en. = "+prev_heat_energies[index]+"; curr.heat.en. = "+cell_heat_energy);                    
                    System.out.println("prev_max_neighb_heat_energy = "+prev_max_neighb_heat_energy+"; max_neighb_heat_energy = "+max_neighb_heat_energy);
                    System.out.println("cell coordinates: ("+cell.getCoordinates().writeToString()+"); indices: ("+cell.getIndices().writeToString()+").");
                    System.out.println();
                    System.out.println("Please, decrease the value of time step!");
                    System.out.println();
                        
                    step_number = 0;
                    writeToFile = true;
                }
                
                if(testing)
             //   if(prev_min_neighb_heat_energy > 0)
                if(prev_heat_energies[index] > prev_min_neighb_heat_energy & cell_heat_energy < prev_min_neighb_heat_energy)
                {
                    System.out.println();
                    System.out.println("STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!STOP!!!");
                    System.out.println("step # "+step_counter);
                    System.out.println("cell # "+index+": prev.heat.en. = "+prev_heat_energies[index]+"; curr.heat.en. = "+cell_heat_energy);
                    System.out.println("prev_min_neighb_heat_energy = "+prev_min_neighb_heat_energy+"; min_neighb_heat_energy = "+min_neighb_heat_energy);
                    System.out.println("cell coordinates: ("+cell.getCoordinates().writeToString()+"); indices: ("+cell.getIndices().writeToString()+").");
                    System.out.println();
                    System.out.println("Please, decrease the value of time step!");
                    System.out.println();
                    
                    step_number = 0;
                    writeToFile = true;
                }
            }

            // Variable responsible for calculation of torsion energy for each cell
            calc_torsion_energy = true;// false;// 
            
            // Variable responsible for calculation of total torsion energy of cells
            calc_total_energy = true;// false;//
            
            // Variable responsible for calculation of cell angle velocities and torsion angles
            calc_angle_velocity = true;// false;//
            
            // Angle velocity of cell
            angle_velocity = new VectorR3();
            
            // Change of cell torsion angle
            torsion_angle_change = new VectorR3();
            
            // Type of cell location
            cell_loc_type = -1;
            
            // cell torsion energy change
            torsion_energy_change = 0;
            
            // total cell torsion energy
            total_cell_torsion_energy = 0;
            
            // Number of atoms in current cell
            atom_number   = 0;
            
            // Number of defects in current cell
            defect_number = 0;
            
            // Total number of atoms and defects in current cell
            total_number  = 0;
            
            // Coefficient for calculation of current value of dislocation density
            coeff = 0;
            
            // Random value for stochastic switch of cell under recrystallization
            rand_recryst = 0;
            
            // Index of current neighbour cell
            neighb_index = 0;
            
            // Variable, which determines cell switch to new grain
            cell_switch = false;
            
            cell_index_I = 0; 
            cell_index_J = 0; 
            cell_index_K = 0;
            
            /* Block 17 */
            if(writeToFile | calc_force_moments == 1 | calc_angle_velocity)
            if((recryst+mech_flows+heat_flows >= Common.EXECUTE_METHOD))
            {
              inner_cell_number      = 0;
              total_tors_energy      = 0;
              total_cell_mech_energy = 0;
              
           //   if(calc_angle_velocity)
            //  if(step_counter % 100 == 0)
            //      System.out.println("Step # "+step_counter+". Torsion angles are calculated.");
            
              if(writeToFile)
              {
                if(calc_force_moments==1)
                  System.out.println("Force moments are calculated at each time step.");
                else
                  System.out.println("Force moments are calculated at time steps when files with output data are created.");
              }
              
              // Calculation of principal stresses, force moments and
              // dislocation dencities of cellular automata
              for(int cell_counter=0; cell_counter<spec_size; cell_counter++)
              {
                cell = (RecCellR3)get(cell_counter);
                grain_type = cell.getGrainType();
                
                if(grain_type==Common.INNER_CELL | grain_type==Common.LAYER_CELL)
                {
                    // Previous absolute value of instant force moment
                 //   moment_module = cell.getInstantForceMoment().getLength();
                    
                    // Previous absolute value of force moment
                //    moment_module = cell.getForceMoment().getLength();
                    
                    // Calculation and setting of new value of cell force moment
                    if(writeToFile | calc_force_moments==1)
                    {
                      if(!calc_force_mom_new)
                      {
                        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                          cell.setForceMoment(calcSummaryForceMoment(cell_counter, step_counter));
                    
                        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                          cell.setForceMoment(calcSummaryForceMomentSCP(cell_counter));
                      }
                      
                      if(calc_force_mom_new)
                        cell.setForceMoment(calcSummaryForceMoment(cell_counter));
                      
                      // Calculation of change of cell torsion angle
                      torsion_angle_change = new VectorR3(cell.getForceMoment());
                      torsion_angle_change.multiply(32.0/(Math.PI*cell.getModShear()));    
                      
                      // Addition of torsion angle change to previous value of torsion angle
                      cell.addTorsionAngle(torsion_angle_change);
                    }
                    
                    if(writeToFile | calc_angle_velocity)
                    {
                        // Calculation of current angle velocity of cell
                        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                          angle_velocity = calcSummaryAngleVelocity(cell_counter, (byte)3);
                        
                        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                          angle_velocity = calcSummaryAngleVelocity(cell_counter, (byte)3);
                        
                        // Setting of the value of angle velocity
                        cell.setAngleVelocity(angle_velocity);
                        
                        // Calculation of change of cell torsion angle
                     //   torsion_angle_change = new VectorR3(angle_velocity);          
                     //   torsion_angle_change.multiply(time_step);
                        
                        // Addition of torsion angle change to previous value of torsion angle
                      //  cell.addTorsionAngle(torsion_angle_change);
                        
                        if(writeToFile)
                        {
                          // Calculation of instant specific force moment
                          force_moment = new VectorR3(angle_velocity);
                          force_moment.multiply(time_step);
                          
                          force_moment.multiply(Math.PI*cell.getModShear()/32);
                       // force_moment.multiply(Math.PI*cell.getModShear()*cell_size_X*cell_size_Y*cell_size_Z/(32*getCellVolume()));
                                                
                          cell.setInstantForceMoment(force_moment);
                        }
                        
                        if(false)
                        if(cell_counter == 7834)
                        {
                            System.out.println("Cell # "+cell_counter+" coord: "+cell.getCoordinates());
                            System.out.println("Mech.energy =          "+cell.getMechEnergy());
                            System.out.println("Modulus of shear =     "+cell.getModShear());
                            System.out.println("Instant moment =       "+ force_moment.writeToString());//cell.getInstantForceMoment().writeToString());
                            System.out.println("Old moment     =       "+cell.getForceMoment().writeToString());
                            System.out.println("angle_velocity =       "+angle_velocity.writeToString());
                            System.out.println("torsion_angle_change = "+torsion_angle_change.writeToString());
                        }
                        
                     //   calc_torsion_energy = false;
                        
                        if(calc_torsion_energy & calc_force_moments==1)
                        {
                            if(cell_counter == 726 & step_counter%100 == 0)
                              init_mech_energy = cell.getMechEnergy();
                            
                            // Calculation of cell torsion energy change
                            cell.calcTorsionEnergyChange(torsion_angle_change);
                            
                            if(cell_counter == 726 & step_counter%100 == 0)
                            {
                                System.out.println("=================================");
                                System.out.println("Step # "+step_counter+". Cell # "+cell_counter+". Mom^2 = "+Math.pow(cell.getForceMoment().getLength(), 2));
                                System.out.println("coeff_diss = "+cell.getCurrentTorsionEnergyCoeff()+"; mod_shear = "+cell.getModShear()+"; cell_volume = "+cell.getVolume());
                                System.out.println("tors_energy_change = "+cell.getTorsionEnergyChange()/cell.getVolume()+"; spec.tors_energy = "+cell.getTorsionEnergy()/cell.getVolume());
                                System.out.println("init.spec.mech_energy = "+init_mech_energy/cell.getVolume()+"; spec.mech_energy = "+cell.getMechEnergy()/cell.getVolume());
                                System.out.println("=================================");
                            }
                            
                            // calculation of total torsion energy of all cells
                            total_cell_torsion_energy += cell.getTorsionEnergy();
                        }
                    }
                    
                    if(calc_force_moments==1)
                    {
                      // Coefficient for calculation of current value of dislocation density
                      coeff = 1.0E-9;// 1.0E-5;// 
                      
                      // Calculation of new number of defects for a cell
                      cell.addDefectNumber(coeff, torsion_angle_change.getLength());
                      
                      if(false)
                      if(recryst == Common.EXECUTE_METHOD)
                      {
                        // Account of dislocation density for calculation of activation energy of grain boundary
                        if(init_disl_density > 0 & cell.getDislDensity() != init_disl_density)
                            energyHAGB = cell.getEnergyHAGB()*Math.exp(1.0E-4*(1.0 - cell.getDislDensity()/init_disl_density));
                        else
                            energyHAGB = 0;
                        
                        cell.setEnergyHAGB(energyHAGB);
                      }
                    }
                    
                    cell_switch = false;
                    
                    old_grain_index = -1;
                    
                    cell_temperature = cell.getTemperature();
                    
                  //  recryst_new_grains = false;
                    
                    // simulation of generation of embryos of new grains in the interior of old grains
                    if(recryst_new_grains)
                    if(cell.getGrainType()==Common.INNER_CELL)
                    {
                      old_grain_index    = cell.getGrainIndex();
                      old_grain_rec_type = grains[old_grain_index - 1].getRecrystType();
                      
                      rand_recryst = 0;
                      
                      embryo_generation       = false;
                      double_grain_generation = false;
                      
                      // If current cell belongs to "old" grain then it can be switched to "new" grain.
                    //  if(old_grain_index <= old_grain_number)
                      if(old_grain_rec_type == Common.INITIAL_GRAIN)
                      {
                        low_tempr_recryst    = cell.getLowTemperThreshold();
                        high_tempr_recryst   = cell.getHighTemperThreshold();
                        min_twinning_temperature = cell.getMinTwinTemperature();
                        twinning_temperature     = cell.getTwinningTemperature();
                        
                        // Maximal probability of recrystallization
                        max_prob_recryst       = grains[old_grain_index - 1].getMaxProbRecryst();
                        
                        // Maximal probability of twinning
                        max_prob_twinning      = grains[old_grain_index - 1].getMaxProbTwinning();
                        
                        // volume portion of particles for current grain
                        part_vol_fraction      = cell.getParticleVolumeFraction();
                        
                        // average volume portion of particles
                        aver_part_vol_fraction = grains[old_grain_index - 1].getAverParticleVolumeFraction();
                        
                      //  System.out.println("Grain # "+old_grain_index+". max_prob_recryst = "+max_prob_recryst+"; max_prob_twinning = "+max_prob_twinning);
                      //  System.out.println("   low_tempr_recryst = "+low_tempr_recryst+"; high_tempr_recryst = "+high_tempr_recryst);
                      //  System.out.println("   min_twinning_temperature = "+min_twinning_temperature+"; twinning_temperature = "+twinning_temperature);
                      //  System.out.println("   part_vol_fraction = "+part_vol_fraction+"; aver_part_vol_fraction = "+aver_part_vol_fraction);
                      //  System.out.println();
                        
                        // The more the cell temperature the more the probability of embryo generation
                        if(low_tempr_recryst < cell_temperature & cell_temperature < high_tempr_recryst)
                        {
                          embryo_generation       = true;
                          double_grain_generation = false;
                          
                          // Average temperature of generation of embryos of new grains in the interior of old grains
                          aver_tempr_recryst = (low_tempr_recryst + high_tempr_recryst)/2;
                          
                          if(cell_temperature < aver_tempr_recryst)
                            rand_recryst = (cell_temperature - low_tempr_recryst)/(aver_tempr_recryst - low_tempr_recryst);
                          else
                            rand_recryst = (high_tempr_recryst - cell_temperature)/(high_tempr_recryst - aver_tempr_recryst);
                          
                          rand_recryst*= max_prob_recryst;
                        }
                        
                        if(min_twinning_temperature < cell_temperature & cell_temperature < twinning_temperature)
                        if((embryo_generation & Math.random() < 0.5) | !embryo_generation)
                        {
                          embryo_generation       = false;
                          double_grain_generation = true;
                          
                          // Calculation of switch probability for double grains according to volume portion of particles
                       // rand_recryst = Math.exp(1.0/(twinning_temperature - cell_temperature));
                        
                          // Average temperature of twinning
                          aver_tempr_recryst = (min_twinning_temperature + twinning_temperature)/2;
                          
                          if(cell_temperature < aver_tempr_recryst)
                            rand_recryst = (cell_temperature - min_twinning_temperature)/(aver_tempr_recryst - min_twinning_temperature);
                          else
                            rand_recryst = (twinning_temperature - cell_temperature)/(twinning_temperature - aver_tempr_recryst);
                          
                          rand_recryst *= max_prob_twinning;
                        }
                        
                       // rand_recryst *= Math.max(0, 0.5*part_vol_fraction/aver_part_vol_fraction);
                                
                        //----------------------------------------------------------
                        // TEST
                        if(false)
                        {
                          cell_indices = cell.getIndices();
                        
                          cell_index_I = cell_indices.getI();
                          cell_index_J = cell_indices.getJ();
                          cell_index_K = cell_indices.getK();
                        
                          if(cell_index_I == getCellNumberI()/2 & cell_index_J == getCellNumberJ()/2 & cell_index_K == getCellNumberK()/2)
                            rand_recryst = 1.0;
                          else
                          if(new_grain_number == 0)
                            rand_recryst = 0.0;
                        }
                        
                        // Limitation of the number of new grains
                        if(false)
                        if((new_grain_number == 1 & cell_temperature <  low_tempr_recryst)
                          |(new_grain_number == 2 & cell_temperature >= low_tempr_recryst))
                        {
                          rand_recryst = 0;
                      //    System.out.println("cell_index_I = "+cell_index_I+"; CellNumberI = "+getCellNumberI()+"; cell_index_J = "+cell_index_J+"; CellNumberJ = "+getCellNumberJ());
                        }
                        //----------------------------------------------------------
                        
                        if(rand_recryst > Math.random())
                        {
                          // Current cell must become an embryo of "new" grain.
                          cell_switch = true;// false;// 
                          
                          // Checking of cells at 1st coordination sphere: for generation of embryo
                          // all neigbour cells must belong to old grains.
                          
                          // Creation of embryos of new grains
                        //  if(false)
                        //  if(new_grain_number < 10)
                          if(embryo_generation)
                          for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                          {
                            // Index of current neighbour cell
                            neighb_index = neighbours1S[cell_counter][neighb_counter];
                            
                            if(neighb_index > -1)
                            {
                              neighb_cell  = (RecCellR3)get(neighb_index);
                            
                              if(neighb_cell.getGrainIndex() > old_grain_number)
                              {
                                cell_switch = false;
                                neighb_counter = neighb1S_number;
                              }
                              else
                                cell_switch = true;
                            }
                            
                            // The number of new grains is limited.
                            if(new_single_grain_number >= new_gr_number & false)
                              cell_switch = false;
                          }
                          
                          // Creation of embryos at boundaries of new grains
                          if(double_grain_generation)
                        //  if(new_grain_number >= 10 & new_grain_number < 20)
                          {
                            cell_switch = false;
                         //   System.out.println("cell_index_I = "+cell_index_I+"; cell_index_J = "+cell_index_J+"; rand_recryst = "+rand_recryst);
                          
                            for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                            {
                              neighb_index = neighbours1S[cell_counter][neighb_counter];
                            
                              if(neighb_index > -1)
                              {
                                neighb_cell           = (RecCellR3)get(neighb_index);
                                neighb_grain_type     = neighb_cell.getGrainType();
                                neighb_grain_index    = neighb_cell.getGrainIndex();
                                neighb_grain_rec_type = grains[neighb_grain_index - 1].getRecrystType();
                              
                                // Twinning from twins is not allowed.
                                if(neighb_grain_rec_type == Common.TWINNING_GRAIN)
                                {
                                    cell_switch = false;
                                    neighb_counter = neighb1S_number;
                                }
                                else
                                if(neighb_grain_type == Common.INNER_CELL)
                                {
                                //  if(neighb_grain_index <= old_grain_number)
                                //  if(false)
                                  if(neighb_grain_rec_type == Common.INITIAL_GRAIN)
                                    cell_switch = false;
                                  
                                  // Twin grains can grow from new grains only
                                  if(neighb_grain_rec_type == Common.NEW_GRAIN)
                                  // The only twin grain can grow from each new grain
                               // if(neighb_grain_twin_numbers[neighb_grain_index - old_grain_number - 1] < 1)
                                  {
                                //  neighb_grain_twin_numbers[neighb_grain_index - old_grain_number - 1]++;
                                            
                                    cell_switch   = true;
                                    neighb_counter = neighb1S_number;
                                  }
                                }
                              }
                            }
                            
                          //  if(new_double_grain_number >= 12)
                            //  cell_switch = false;
                          }
                            
                          if(cell_switch)
                            // Current cell becomes an embryo of "new" grain.
                            new_grain_embryos.add(cell_counter);
                        }
                      }
                      else
                      {
                        if(new_grain_embryos.contains(cell_counter))
                          // Current cell is an embryo of "new" grain, and each cell at 1st coordination sphere of the embryo 
                          // can be joined to this grain.
                          cell_switch = true;
                      }
                      
                      // Generation of new grain embryo in current cell
                      if(cell_switch)
                      {
                        if(old_grain_index <= old_grain_number)
                        {
                          // Increase of number of new grains
                          new_grain_number++;
                          grain_number++;
                          cell_grain_index = grain_number;
                          
                          if(embryo_generation)
                          {
                            new_single_grain_number++;
                            
                            System.out.println();
                            System.out.println("NEW GRAIN!!! Step # "+step_counter+". New embryo # "+new_grain_number+" is generated! rand_recryst = "+rand_recryst+"; max_prob_recryst = "+max_prob_recryst);
                            System.out.print  ("cell_temperature = "+cell.getTemperature()+"; low_tempr_recryst = "+low_tempr_recryst+"; high_tempr_recryst = "+high_tempr_recryst);
                            System.out.println("; part_vol_fraction = "+part_vol_fraction+"; aver_part_vol_fraction = "+aver_part_vol_fraction);
                            System.out.println("Grain index: "+cell_grain_index+"; old_grain_number = "+old_grain_number+
                                               "; new_single_grain_number = "+new_single_grain_number+"; new_double_grain_number = "+new_double_grain_number);
                          }
                          
                          if(double_grain_generation)
                          {
                            new_double_grain_number++;
                            
                            System.out.println();
                            System.out.println("TWINNING!!! Step # "+step_counter+". New embryo # "+new_grain_number+" is generated! rand_recryst = "+rand_recryst+"; max_prob_twinning = "+max_prob_twinning);
                            System.out.print  ("cell_temperature = "+cell.getTemperature()+"; min_twinning_temperature = "+min_twinning_temperature+"; twinning_temperature = "+twinning_temperature);
                            System.out.println("; part_vol_fraction = "+part_vol_fraction+"; aver_part_vol_fraction = "+aver_part_vol_fraction);
                            System.out.println("Grain index: "+cell_grain_index+"; old_grain_number = "+old_grain_number+"; neighb_grain_index = "+neighb_grain_index+
                                               "; new_single_grain_number = "+new_single_grain_number+"; new_double_grain_number = "+new_double_grain_number);
                          }
                          
                          // grain containing current cell at current time step
                          old_grain = (Cluster)grainsCluster.get(cell.getGrainIndex() - 1);
                        
                          // setting of new grain index
                          cell.setGrainIndex(cell_grain_index);
                          
                          // creation of new empty grain
                          grains[cell_grain_index - 1] = new Cluster(old_grain);
                          grains[cell_grain_index - 1].clear();
                        
                          // setting of index of new grain
                          grains[cell_grain_index - 1].setID(cell_grain_index);
                          
                          // Addition of coordinates of new grain embryo to the list
                          gr_embryo_coord.add(cell.getCoordinates());
                          
                          // setting of location type of grain
                          grains[cell_grain_index - 1].setType(old_grain.getType());
                          
                          // Setting of new Euler angles for new grain
                          angleHAGB = old_grain.getAngleLimitHAGB();
                        //  angleHAGB = 0;
                          angleHAGB = angleHAGB/60.0;
                          
                        //  double[] old_angles = old_grain.getAngles();
                          
                          grains[cell_grain_index - 1].setAngles(angleHAGB*Math.random(), angleHAGB*Math.random(), angleHAGB*Math.random());
                          
                          System.out.println("  angle_1: "+grains[cell_grain_index - 1].getAngle1()+
                                             "; angle_2: "+grains[cell_grain_index - 1].getAngle2()+
                                             "; angle_3: "+grains[cell_grain_index - 1].getAngle3());
                          
                          // TEST
                          VectorR3 lat_vect_a = grains[cell_grain_index - 1].getLatticeVectorA();
                          VectorR3 lat_vect_b = grains[cell_grain_index - 1].getLatticeVectorB();
                          VectorR3 lat_vect_c = grains[cell_grain_index - 1].getLatticeVectorC();
                        
                          double cos_a_b = cosinus(lat_vect_a, lat_vect_b);
                          double cos_b_c = cosinus(lat_vect_b, lat_vect_c);
                          double cos_c_a = cosinus(lat_vect_c, lat_vect_a);
                            
                          System.out.println("  lat_vect_a: "+lat_vect_a.writeToString()+"; cos_a_b = "+cos_a_b);
                          System.out.println("  lat_vect_b: "+lat_vect_b.writeToString()+"; cos_b_c = "+cos_b_c);
                          System.out.println("  lat_vect_c: "+lat_vect_c.writeToString()+"; cos_c_a = "+cos_c_a);
                        //  System.out.println();
                          
                          // Setting of new Euler angles for current cell
                          cell.setEulerAngles(grains[cell_grain_index - 1].getAngles());
                          
                          // Setting of lattice parameters of new grain
                          grains[cell_grain_index - 1].setLatticeAngle_vecA_vecB(old_grain.getLatticeAngle_vecA_vecB());
                          grains[cell_grain_index - 1].setLatticeAngle_vecB_vecC(old_grain.getLatticeAngle_vecB_vecC());
                          grains[cell_grain_index - 1].setLatticeAngle_vecC_vecA(old_grain.getLatticeAngle_vecC_vecA());
                          
                          // Setting of parameters for new grain
                          if(embryo_generation)
                          {
                            // Setting of lattice parameters for new grain
                            if(cell_grain_index % 3 == 0)
                            {
                              grains[cell_grain_index - 1].setLatticeVector_A_Length(old_grain.getLatticeVector_A_Length());
                              grains[cell_grain_index - 1].setLatticeVector_B_Length(old_grain.getLatticeVector_B_Length());
                              grains[cell_grain_index - 1].setLatticeVector_C_Length(old_grain.getLatticeVector_C_Length());
                            }
                            
                            if(cell_grain_index % 3 == 1)
                            {
                              grains[cell_grain_index - 1].setLatticeVector_A_Length(old_grain.getLatticeVector_B_Length());
                              grains[cell_grain_index - 1].setLatticeVector_B_Length(old_grain.getLatticeVector_C_Length());
                              grains[cell_grain_index - 1].setLatticeVector_C_Length(old_grain.getLatticeVector_A_Length());
                            }
                            
                            if(cell_grain_index % 3 == 2)
                            {
                              grains[cell_grain_index - 1].setLatticeVector_A_Length(old_grain.getLatticeVector_C_Length());
                              grains[cell_grain_index - 1].setLatticeVector_B_Length(old_grain.getLatticeVector_A_Length());
                              grains[cell_grain_index - 1].setLatticeVector_C_Length(old_grain.getLatticeVector_B_Length());
                            }
                            
                            // Setting of grain type according to its role in recrystallization process
                            grains[cell_grain_index - 1].setRecrystType(Common.NEW_GRAIN);
                          }
                          
                          // Setting of parameters for twinning grain
                          if(double_grain_generation)
                          {
                            // Setting of lattice parameters for twinning grain
                          /*
                            if(cell_grain_index % 2 == 0 & false)
                            {
                              grains[cell_grain_index - 1].setLatticeVector_A_Length(old_grain.getLatticeVector_C_Length());
                              grains[cell_grain_index - 1].setLatticeVector_B_Length(old_grain.getLatticeVector_A_Length());
                              grains[cell_grain_index - 1].setLatticeVector_C_Length(old_grain.getLatticeVector_B_Length());
                            }
                          
                            if(cell_grain_index % 2 == 1 | true)
                            {
                              grains[cell_grain_index - 1].setLatticeVector_A_Length(old_grain.getLatticeVector_B_Length());
                              grains[cell_grain_index - 1].setLatticeVector_B_Length(old_grain.getLatticeVector_C_Length());
                              grains[cell_grain_index - 1].setLatticeVector_C_Length(old_grain.getLatticeVector_A_Length());
                            }
                          */
                            
                            // Lengths of lattice vestors for twin grain are chosen according to the lengths of its root grain
                            if(cell_grain_index % 2 == 0)
                            {
                              grains[cell_grain_index - 1].setLatticeVector_A_Length(grains[neighb_grain_index - 1].getLatticeVector_B_Length());
                              grains[cell_grain_index - 1].setLatticeVector_B_Length(grains[neighb_grain_index - 1].getLatticeVector_C_Length());
                              grains[cell_grain_index - 1].setLatticeVector_C_Length(grains[neighb_grain_index - 1].getLatticeVector_A_Length());
                            }
                            
                            if(cell_grain_index % 2 == 1)
                            {
                              grains[cell_grain_index - 1].setLatticeVector_A_Length(grains[neighb_grain_index - 1].getLatticeVector_C_Length());
                              grains[cell_grain_index - 1].setLatticeVector_B_Length(grains[neighb_grain_index - 1].getLatticeVector_A_Length());
                              grains[cell_grain_index - 1].setLatticeVector_C_Length(grains[neighb_grain_index - 1].getLatticeVector_B_Length());
                            }
                            
                            // Setting of grain type according to its role in recrystallization process
                            grains[cell_grain_index - 1].setRecrystType(Common.TWINNING_GRAIN);
                            
                            // Setting of index of grain, from which twin grain grows
                            if(grains[neighb_grain_index - 1].getRecrystType() == Common.NEW_GRAIN)
                              grains[cell_grain_index - 1].setRootGrainIndex(neighb_grain_index);
                            else
                              grains[cell_grain_index - 1].setRootGrainIndex(grains[neighb_grain_index - 1].getRootGrainIndex());
                          }
                          
                          cell.setLatticeVector_A_Length(grains[cell_grain_index - 1].getLatticeVector_A_Length());
                          cell.setLatticeVector_B_Length(grains[cell_grain_index - 1].getLatticeVector_B_Length());
                          cell.setLatticeVector_C_Length(grains[cell_grain_index - 1].getLatticeVector_C_Length());
                            
                          cell.setLatticeAngle_vecA_vecB(grains[cell_grain_index - 1].getLatticeAngle_vecA_vecB());
                          cell.setLatticeAngle_vecB_vecC(grains[cell_grain_index - 1].getLatticeAngle_vecB_vecC());
                          cell.setLatticeAngle_vecC_vecA(grains[cell_grain_index - 1].getLatticeAngle_vecC_vecA());
                          
                          grains[cell_grain_index - 1].setLatticeAnisCoeff(old_grain.getLatticeAnisCoeff());
                          
                          cell.setLatticeAnisCoeff(grains[cell_grain_index - 1].getLatticeAnisCoeff());
                
                          // setting of location type of cell
                          cell.setType(cell.getType());
                
                          // Setting of type of grain containing cell
                          cell.setGrainType(grains[cell_grain_index - 1].getType());

                          // Setting of density of dislocations of grain containing cell
                          cell.setDislDensity(old_grain.getMinDislDensity());
                          grains[cell_grain_index - 1].setDislDensity(old_grain.getMinDislDensity());
                          
                          // Calculation of dislocation energy of grain containing current cell
                          grains[cell_grain_index - 1].calcDislEnergy();
                
                          // Setting of HAGB energy of cell
                          cell.setEnergyHAGB(grains[cell_grain_index - 1].getEnergyHAGB());
                          
                          // Setting of zero force moment, angle velocity, torsion energy
                          cell.setForceMoment(new VectorR3());
                          cell.setInstantForceMoment(new VectorR3());
                          cell.setAngleVelocity(new VectorR3());
                          cell.setTorsionAngle(new VectorR3());
                          cell.setTorsionEnergy(0);
                          cell.setTorsionEnergyChange(0);
                
                          // Calculation of atom number for cell
                          cell.calcAtomNumber();
                
                          // Calculation of defect number for cell
                          cell.calcDefectNumber();
                          
                          // Addition of cell single index to list of indices of cells
                          //those belong to this grain
                          grains[cell_grain_index - 1].add(cell_counter);
                        
                          // Removing of current cell from old grain
                          old_grain.remove(old_grain.indexOf(cell_counter));

                          // Change of old and new grains thermal energy
                          grains[cell_grain_index - 1].addThermalEnergy(cell.getThermalEnergy());
                          old_grain.addThermalEnergy(-cell.getThermalEnergy());

                          // Change of old and new grains mechanical energy
                          grains[cell_grain_index - 1].addElasticEnergy(cell.getMechEnergy());
                          old_grain.addElasticEnergy(-cell.getMechEnergy());
                        
                          // Addition of new grain to list of grains
                          grainsCluster.   add(grains[cell_grain_index - 1]);
                          newGrainsCluster.add(grains[cell_grain_index - 1]);
                        }
                     //   else
                        // Addition of cells at 1st coordination sphere to new grain
                        if(cell_grain_index > 0)
                        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                        {
                            // Index of current neighbour cell
                            neighb_index = neighbours1S[cell_counter][neighb_counter];
                            
                            if(neighb_index > -1 & neighb_index < spec_size)
                        //    if(!new_grain_embryos.contains(neighb_index))
                            {
                              // current neighbour cell
                              neighb_cell = (RecCellR3)get(neighb_index);
                              
                              // location type of grain containing current neighbour cell
                              neighb_grain_type = neighb_cell.getGrainType();
                              
                              // Index of grain containing current neighbour cell
                              neighb_grain_index = neighb_cell.getGrainIndex();
                
                              // joining of current neighbour cell to new grain
                           //   if(neighb_cell.getMaterialFile() == cell.getMaterialFile())
                              if(neighb_grain_type==Common.INNER_CELL)// | neighb_grain_type==Common.LAYER_CELL)
                          //  if(neighb_cell.getTemperature() > low_tempr_recryst)
                              if(neighb_grain_index <= old_grain_number & neighb_grain_index > 0)
                              {
                                // Calculation of number of cells joined to existing grains
                                if(old_grain_index > old_grain_number)
                                  cells_joined_to_existing_embryos++;
                                else
                                  cells_joined_to_new_embryos++;
                                
                                // grain containing current neighbour cell at current time step
                                old_grain = (Cluster)grainsCluster.get(neighb_grain_index - 1);
                                
                                // setting of new grain index
                                neighb_cell.setGrainIndex(cell_grain_index);
                                
                                // Setting of new Euler angles
                                neighb_cell.setEulerAngles(grains[cell_grain_index - 1].getAngles());
                
                                // setting of type of current neighbour cell
                                neighb_cell.setType(grains[cell_grain_index - 1].getType());
                
                                // Setting of type of grain containing current neighbour cell
                                neighb_cell.setGrainType(grains[cell_grain_index - 1].getType());

                                // Setting of density of dislocations of grain containing current neighbour cell
                                neighb_cell.setDislDensity(old_grain.getMinDislDensity());
                                
                                // Setting of HAGB energy of current neighbour cell
                                neighb_cell.setEnergyHAGB(grains[cell_grain_index - 1].getEnergyHAGB());
                                
                                // Calculation of portion of inclusion particles for current cell 
                                // and its neighbours at 1st coordinaton sphere
                                calcParticleVolumeFraction1S(neighb_index, old_grain_number);
                                
                                // Setting of zero force moment, angle velocity, torsion energy
                                neighb_cell.setForceMoment(new VectorR3());
                                neighb_cell.setInstantForceMoment(new VectorR3());
                                neighb_cell.setAngleVelocity(new VectorR3());
                                neighb_cell.setTorsionAngle(new VectorR3());
                                neighb_cell.setTorsionEnergy(0);
                                neighb_cell.setTorsionEnergyChange(0);
                                
                                // Calculation of atom number for current neighbour cell
                                neighb_cell.calcAtomNumber();
                                
                                // Calculation of defect number for current neighbour cell
                                neighb_cell.calcDefectNumber();
                                
                                // Addition of current neighbour cell single index to list of indices of cells
                                //those belong to this grain
                                grains[cell_grain_index - 1].add(neighb_index);
                                
                                // Removing of current current neighbour cell from old grain
                                old_grain.remove(old_grain.indexOf(neighb_index));
                                
                                // Change of old and new grains thermal energy
                                grains[cell_grain_index - 1].addThermalEnergy(neighb_cell.getThermalEnergy());
                                old_grain.addThermalEnergy(-neighb_cell.getThermalEnergy());
                                
                                // Change of old and new grains mechanical energy
                                grains[cell_grain_index - 1].addElasticEnergy(neighb_cell.getMechEnergy());
                                old_grain.addElasticEnergy(-neighb_cell.getMechEnergy());
                                
                                // Setting of cell with changed parameters to the list
                                set(neighb_index, neighb_cell);
                                
                                // Addition of new grain to list of grains
                                grainsCluster.set   (cell_grain_index - 1, grains[cell_grain_index - 1]);
                                newGrainsCluster.set(cell_grain_index - 1, grains[cell_grain_index - 1]);
                                
                                neighb_index = neighbours1S[cell_counter][neighb_counter];
                                
                             //   System.out.println("Embryo # "+new_grain_number+" is generated in cell # "+cell_counter+" with part_vol_fr = "+cell.getParticleVolumeFraction()+
                             //                      " and contains cell # "+neighb_index+" with part_vol_fr = "+neighb_cell.getParticleVolumeFraction());
                              }
                            }
                        }
                        
                        //TEST
                        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
                        {
                          // Index of current neighbour cell
                          neighb_index = neighbours1S[cell_counter][neighb_counter];
                            
                          if(neighb_index > -1 & neighb_index < spec_size)
                      //  if(!new_grain_embryos.contains(neighb_index))
                          {
                            // current neighbour cell
                            neighb_cell = (RecCellR3)get(neighb_index);
                              
                            // location type of grain containing current neighbour cell
                            neighb_grain_type  = neighb_cell.getGrainType();
                            
                            // Index of grain containing neighbour cell
                            neighb_grain_index = neighb_cell.getGrainIndex();
                            
                            if(neighb_grain_type == Common.INNER_CELL | neighb_grain_type == Common.LAYER_CELL)
                            if(neighb_grain_index > old_grain_number & cell_grain_index > old_grain_number & neighb_grain_index != cell_grain_index)
                            {
                          //    System.out.println("Embryo of double grain is generated!!!"+
                            //          " cell_grain_index = "+cell_grain_index+"; neighb_grain_index = "+neighb_grain_index);
                              error_num++;
                            }
                          }
                        }
                      }
                    }
                    
                    // Setting of cell with changed parameters to the list
                    set(cell_counter, cell);
                    
                    // Type of cell location
                    cell_loc_type = cell.getType();
                    
               //     if(step_counter == step_number)
               //     if(writeToFile)
               //     if(calc_total_energy)
               //     if(cell_loc_type != Common.OUTER_CELL & 
                 //      cell_loc_type != Common.INNER_BOUNDARY_CELL &
                   //    cell_loc_type != Common.INNER_BOUNDARY_INTERGRANULAR_CELL &
                     //  cell_loc_type != Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL)
                    {
                   //     total_tors_energy+= cell.calcTorsionEnergy();
                        total_cell_mech_energy+= cell.getMechEnergy();
                        inner_cell_number++;
                    }
                }
              }
              
              // TEST
              if(writeToFile)
              {
                  System.out.println("Time step: "+step_counter+"; total elastic energy: "+total_cell_mech_energy);
                  System.out.println("           "+step_counter+"; total torsion energy: "+total_cell_torsion_energy);
               //   System.out.println("Number of new grains at current step: "+new_grain_number);
                  System.out.println("Total number of new grains: "+new_grain_embryos.size()+" = "+new_grain_number);
                  System.out.println("Total number of grains:     "+grain_number+" = "+old_grain_number+" + "+new_grain_number);
                  System.out.println("Number of cells joined to new embryos:      "+cells_joined_to_new_embryos);
                  System.out.println("Number of cells joined to existing embryos: "+cells_joined_to_existing_embryos);
              }
            }
            
            // Simulation of crack formation
            if(cracks==Common.EXECUTE_METHOD)
            {
                if(writeToFile)
                    System.out.println("Crack formation is simulated...");

                /* Block 15 */
             //   calcCrackFormation(writeFileName, false, step_counter);
                curr_dam_cell_num = calcCrackPropagation();
                dam_cell_number  += curr_dam_cell_num;
                
                if(writeToFile)
                {
                    System.out.println("Number of cells damaged at time step #"+step_counter+": "+curr_dam_cell_num);
                    System.out.println("Total number of damaged cells:        "+dam_cell_number);
                }
            }

            /* Block 18 */
            //Writing of simulation results to file
            if(writeToFile)
            {
                grain_size        = 0;
                grain_heat_energy = 0;
                grain_mech_energy = 0;
                specimen_size     = 0;
                total_heat_energy = 0;
                total_grain_mech_energy = 0;
                
            //    total_cell_mech_energy = 0;
            //    total_tors_energy = 0;
                
                // Number of cells in the interior of specimen
            //    inner_cell_number=0;

                // Printing of information about grains
                for(int grainCounter = 0; grainCounter < grains.length; grainCounter++)
          //      if(grains[grainCounter].getType()==Common.INNER_CELL)
                {
                    // Number of cells in grain
                    grain_size = grains[grainCounter].size();

                    // Grain heat energy
                    grain_heat_energy = grains[grainCounter].getThermalEnergy();

                    // Grain mechanical energy
                    grain_mech_energy = grains[grainCounter].getElasticEnergy();
                    
                    // Grain type
                    grain_type = grains[grainCounter].getType();
                    
                    lattice_parameter  = grains[grainCounter].getLatticeParameter();
                    lattice_anis_coeff = grains[grainCounter].getLatticeAnisCoeff();
                          
                    // Lengths of 3 vectors of lattice anisotropy
                    lattice_vector_A_length = grains[grainCounter].getLatticeVector_A_Length();
                    lattice_vector_B_length = grains[grainCounter].getLatticeVector_B_Length();
                    lattice_vector_C_length = grains[grainCounter].getLatticeVector_C_Length();
                     
                    // Angles between vectors of lattice anisotropy
                    lattice_angle_vecA_vecB = grains[grainCounter].getLatticeAngle_vecA_vecB();
                    lattice_angle_vecB_vecC = grains[grainCounter].getLatticeAngle_vecB_vecC();
                    lattice_angle_vecC_vecA = grains[grainCounter].getLatticeAngle_vecC_vecA();
                    
                    lat_vect_A = grains[grainCounter].getLatticeVectorA();
                    lat_vect_B = grains[grainCounter].getLatticeVectorB();
                    lat_vect_C = grains[grainCounter].getLatticeVectorC();
                    
                 // if(recryst    != Common.EXECUTE_METHOD)
                 // if(grain_size != 0)
                 // if((grain_mech_energy!=0)|(grain_heat_energy!=0))
                 //   if(grainCounter<15)
                    if(grains.length >= 200)
                    {
                     // if((grainCounter+1) % (grains.length/20) == 0 | 
                     //     grain_type != Common.INNER_CELL & grain_type != Common.LAYER_CELL)
                      if(grain_size > 0)
                      if(grainCounter < grainsCluster.size())
                      {
                          // TEST
                          double cos_a_b = cosinus(lat_vect_A, lat_vect_B);
                          double cos_b_c = cosinus(lat_vect_B, lat_vect_C);
                          double cos_c_a = cosinus(lat_vect_C, lat_vect_A);
                          
                          System.out.print("Grn # "+(grainCounter+1)+
                                           " tp: "+grain_type+
                                           " sz: "+grain_size+
                                           " sp.ht.en: "+grain_heat_energy/Math.max(1, grain_size)+
                                           " el.en: "+grain_mech_energy);
                                        //   " embr.coord.: "+((VectorR3)gr_embryo_coord.get(grainCounter)).toString());/*grainSize*getCellVolumeHCP()*///+
                                        // "; eff. velocity "+grains[grainCounter].getMechMaxMobility()); 
                          
                          System.out.println("   angle_1: "+grains[grainCounter].getAngle1()+
                                             "   angle_2: "+grains[grainCounter].getAngle2()+
                                             "   angle_3: "+grains[grainCounter].getAngle3());
                          
                        //  System.out.print(" anis_cf: "+lattice_anis_coeff+" lat_prm: "+lattice_parameter);
                          System.out.print("   lat_vec.lengths: A = "+lattice_vector_A_length+" B = "+lattice_vector_B_length+" C = "+lattice_vector_C_length);
                          System.out.println(" lat_angles: AB = "+lattice_angle_vecA_vecB+" BC = "+lattice_angle_vecB_vecC+" CA = "+lattice_angle_vecC_vecA);
                          System.out.println("   lattice vector A: "+lat_vect_A.writeToString()+"; cos_AB = "+cos_a_b);
                          System.out.println("   lattice vector B: "+lat_vect_B.writeToString()+"; cos_BC = "+cos_b_c);
                          System.out.println("   lattice vector C: "+lat_vect_C.writeToString()+"; cos_CA = "+cos_c_a);
                      }
                    }
                    else
                    // if(grains.length == 15)
                    {
                  //    if(grain_type != Common.ADIABATIC_ADIABATIC_CELL)
                      if(grain_size > 0)
                      if(grainCounter < grainsCluster.size())
                      {
                          // TEST
                          double cos_a_b = cosinus(lat_vect_A, lat_vect_B);
                          double cos_b_c = cosinus(lat_vect_B, lat_vect_C);
                          double cos_c_a = cosinus(lat_vect_C, lat_vect_A);
                          
                          System.out.print("Grn # "+(grainCounter+1)+
                                           " tp: "+grain_type+
                                           " sz: "+grain_size+
                                           " sp.ht.en: "+grain_heat_energy/Math.max(1, grain_size)+
                                           " el.en: "+grain_mech_energy);
                                        //   " embr.coord.: "+((VectorR3)gr_embryo_coord.get(grainCounter)).toString());/*grainSize*getCellVolumeHCP()*///+
                                        // "; eff. velocity "+grains[grainCounter].getMechMaxMobility()); 
                          
                          System.out.println("   angle_1: "+grains[grainCounter].getAngle1()+
                                             "   angle_2: "+grains[grainCounter].getAngle2()+
                                             "   angle_3: "+grains[grainCounter].getAngle3());
                          
                        //  System.out.print(" anis_cf: "+lattice_anis_coeff+" lat_prm: "+lattice_parameter);
                          System.out.print("   lat_vec.lengths: A = "+lattice_vector_A_length+" B = "+lattice_vector_B_length+" C = "+lattice_vector_C_length);
                          System.out.println(" lat_angles: AB = "+lattice_angle_vecA_vecB+" BC = "+lattice_angle_vecB_vecC+" CA = "+lattice_angle_vecC_vecA);
                          System.out.println("   lattice vector A: "+lat_vect_A.writeToString()+"; cos_AB = "+cos_a_b);
                          System.out.println("   lattice vector B: "+lat_vect_B.writeToString()+"; cos_BC = "+cos_b_c);
                          System.out.println("   lattice vector C: "+lat_vect_C.writeToString()+"; cos_CA = "+cos_c_a);
                      }
                    }
                    
                    // Number of cells in specimen (including outer boundary cells)
                    specimen_size += grain_size;
                    
                    // Specimen heat energy (including outer boundary cells)
                    total_heat_energy += grain_heat_energy;
                    
                    // Specimen mechanical energy (including outer boundary cells)
                    total_grain_mech_energy += grain_mech_energy;  
                }
                
            /*
                // Calculation of total torsion energy and total mechanical energy of cells
                for(int cell_counter=0; cell_counter<specimen_size; cell_counter++)
                {
                    cell = (RecCellR3)get(cell_counter);
                    grain_type = cell.getGrainType();

                    if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
                    {
                        if(step_counter == step_number)
                        if(calc_total_energy)
                        if(mech_flows == Common.EXECUTE_METHOD)
                        {
                            total_tors_energy+= cell.calcTorsionEnergy();
                            total_cell_mech_energy+= cell.getMechEnergy();
                            inner_cell_number++;
                        }
                    }
                }
            */
                
                System.out.println();
                System.out.println("Total number of cells:          "+specimen_size+" = "+size());
                System.out.println("Total thermal energy:           "+total_heat_energy);
                System.out.println("Total elastic energy of grains: "+total_grain_mech_energy);
                System.out.println("Average thermal energy of cell: "+total_heat_energy/specimen_size);
                System.out.println("Average elastic energy of cell: "+total_grain_mech_energy/specimen_size);                
                System.out.println("---------------------------");
                System.out.println("Number of cases when adjacent embryos of new grains contain the same cell: "+error_num);
                System.out.println();
                
              //  if(step_counter == step_number)
              //  if(calc_total_energy)
                if(mech_flows == Common.EXECUTE_METHOD | heat_flows == Common.EXECUTE_METHOD)
                try    
                {   
                    bw_total.write("\n"+(long)Math.round(step_counter*time_step*1.0E15)/1.0E15+
                                   " "+total_mech_energy_influx+" "+total_mech_energy+
                                   " "+total_tors_energy+" "+current_outer_heat_influx+" "+current_inner_heat_influx+
                                   " "+total_outer_heat_influx+" "+total_inner_heat_influx);
                    bw_total.flush();
                    
                    System.out.println("Number of inner cells:          "+inner_cell_number);                    
                    System.out.println("Total elastic energy of cells:  "+total_mech_energy);
                    System.out.println("Total influx of elastic energy: "+total_mech_energy_influx);
                    System.out.println("Total torsion energy of cells:  "+total_cell_torsion_energy);
                  //  System.out.println("Current influx of thermal energy to outer cells: "+current_outer_heat_influx);
                  //  System.out.println("Current influx of thermal energy to inner cells: "+current_inner_heat_influx);
                  //  System.out.println("Total influx of thermal energy to outer cells:   "+total_outer_heat_influx);
                  //  System.out.println("Total influx of thermal energy to inner cells:   "+total_inner_heat_influx);
                  //  System.out.println("Total torsion energy of cells:  "+total_tors_energy);
                    System.out.println();
                }
                catch(IOException io_exc)
                {
                    System.out.println("Error!!!"+io_exc);
                }
                
                writeResultsFile(writeToFile, writeFileName, step_counter-1, heat_flows, mech_flows, recryst, cracks);

                // writeCellBoundsFile(writeFileName, step_counter);
                
                // Writing of information about portions of inner cells in each state
                // at current time step
                createGraphStates(step_counter-1);
            }
            
            if(calc_bound_parameters)
            if(writeCellBoundsToFile)
                writeCellBoundsFile(writeFileName, step_counter-1);
        }
        
        // TEST
        System.out.println("bound_interaction_type = "+bound_interaction_type);
        System.out.println("min_neighbours_number = "+min_neighbours_number);
        System.out.println("response_rate = "+response_rate);
        System.out.println("---------------------------");
        System.out.println("Number of errors when adjacent embryos of new grains contain the same cell: "+error_num);

        try
        {
            bw1.close();
            bw3.close();
            bw_total.close();
            System.out.println("File "+writeFileName+"_total_energies.txt is created.");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }
    }
     
    /** The method creates embryo of grain in the cell 
     * with the high absolute value of the force moment vector.
     * @param cellR3 considered cell
     * @param mom_thresh_value threshold value of the force moment
     */
    public void generateGrain(RecCellR3 cellR3, double mom_thresh_value)
    {        
        double abs_force_moment = cellR3.calcAbsForceMoment();
        int grain_number = grainsCluster.size();
        Cluster new_grain;
        
        if(abs_force_moment > mom_thresh_value)
        {
            new_grain = new Cluster();
            cellR3.setGrainIndex(grain_number+1);
            new_grain.add(cellR3);            
        }
    }

    /** The method writes information about  parameters of each CA at current time step
     * to the file.
     * @param writeToFile variable responsible for writing to file
     * @param writeFileName name of file for writing of results
     * @param step_counter number of time step
     * @param heat_flows variable responsible for calculation of heat transfer
     * @param mech_flows variable responsible for calculation of mechanical energy transfer
     * @param recryst variable responsible for calculation of recrystallization
     * @param cracks variable responsible for calculation of process of crack propagation
     */
    private void writeResultsFile(boolean writeToFile, String writeFileName, long step_counter,
                                 byte heat_flows, byte mech_flows, byte recryst, byte cracks)
    {
        
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation
        // of number of current time step
        int exponent = 0;

        if(step_counter+1 > 0)
            exponent     = (int)Math.floor(Math.log10(step_counter+1));

        // String of zeros
        String zeros = "";

        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";

        if(step_counter > 0)
            exponent     = (int)Math.floor(Math.log10(step_counter));

        // String of zeros
        String zeros_old = "";

        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros_old = zeros_old + "0";
        
        BufferedWriter bw_bounds;
        BufferedWriter bw_inst_mom;
        BufferedWriter bw_grains;
        
        BufferedReader br_colours;
        BufferedWriter bw_colours, bw_colours_1;
        
        // Names of files with output parameters
        String res_file_name      = writeFileName+"_"+zeros+(step_counter+1)+"."+Common.RESULTS_FILE_EXTENSION;
        String bound_file_name    = writeFileName+"_"+zeros+(step_counter+1)+"_bounds."+Common.RESULTS_FILE_EXTENSION;
        String torsion_file_name  = writeFileName+"_"+zeros+(step_counter+1)+"_torsion."+Common.RESULTS_FILE_EXTENSION;
        String inst_mom_file_name = writeFileName+"_"+zeros+(step_counter+1)+"_inst_mom."+Common.RESULTS_FILE_EXTENSION;
        String grains_file_name   = writeFileName+".grn";
        
        String colours_file_name     = writeFileName+".clrs";
        String new_colours_file_name   = writeFileName+"_"+zeros+(step_counter+1)+".clrs";
        String new_colours_file_name_1 = writeFileName+"_diff.clrs";
        
        try
        {
            bw          = new BufferedWriter(new FileWriter(res_file_name));
          //  bw_bounds  = new BufferedWriter(new FileWriter(bound_file_name));
            bw_tors     = new BufferedWriter(new FileWriter(torsion_file_name));
            bw_inst_mom = new BufferedWriter(new FileWriter(inst_mom_file_name));
            bw_grains   = new BufferedWriter(new FileWriter(grains_file_name));
            
            br_colours     = new BufferedReader(new FileReader(colours_file_name));
            
            bw_colours     = new BufferedWriter(new FileWriter(new_colours_file_name));
            bw_colours_1   = new BufferedWriter(new FileWriter(new_colours_file_name_1));
            
            double max_temperature = -1.0E300;
            double max_mech_stress = -1.0E300;
            double max_strain      = -1.0E300;
            double min_temperature =  1.0E300;
            double min_mech_stress =  1.0E300;
            double min_strain      =  1.0E300;

            double principal_stress;
            VectorR3 force_moment, cell_coordinates;

            // Size of specimen
            int spec_size = size();

            double cell_temperature, cell_mech_stress,
                   cell_mech_energy, cell_strain, cell_heat_energy, cell_torsion_energy;

            boolean search_extreme_values = true; // false;// 

         //   if(step_counter<0)
           //     search_extreme_values = true;            
            
            // Variable responsible for writing of components of displacement vectors for all cells            
            boolean write_displ_vectors = false; 

            double coord_X, coord_Y, coord_Z;

            double max_coord_X=0,
                   max_coord_Y=0,
                   max_coord_Z=0;
            
            // Type of grain according to character of its interaction with neighbour grains
            byte grain_type;
            
            bw.write("# Each string contains parameters of corresponding cell: \n");
            bw.write("# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 6. temperature; \n");
            bw.write("# 7. effective stress; 8. principal stress; \n"
                   + "# 9-11. 3 components of specific volume force moment vector calculated using neighbour cells at 1st sphere only; \n");
            bw.write("# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n");
        //  bw.write("# 13. strain; 14. density of dislocations. \n");
            bw.write("# 13. strain; 14. total specific torsion energy; |\n");
            bw.write("# 15. density of defects; 16. atom number; 17. defect number; 18. portion of defects.\n");
            bw.write("# Total number of grains: "+grain_number+".\n");
            
            //TEST
       //     bw.write("cell_surface: "+cell_surface);
         //   bw.newLine();
            
            bw_tors.write("# Each string contains parameters of corresponding cell: \n");
            bw_tors.write("# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; \n");
            bw_tors.write("# 6-8. 3 components of angle velocity; \n");
            bw_tors.write("# 9-11. 3 components of torsion angle vector; \n");
            bw_tors.write("# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n");
            bw_tors.write("# 13. relative power of torsion (current influx of torsion energy divided by current influx of elastic energy); \n");
            bw_tors.write("# 14. relative torsion energy (total torsion energy divided by total elastic energy); \n");
            bw_tors.write("# 15. total specific energy (sum of thermal and elastic energy divided by cell volume); \n");
            bw_tors.write("# 16. Young modulus.\n");
            
            bw_inst_mom.write("# Each string contains parameters of corresponding cell: \n");
            bw_inst_mom.write("# 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; \n");
            bw_inst_mom.write("# 6-8. 3 absolute values of components of instant specific force moment calculated using neighbour cells at 1st-3rd spheres; \n");
            bw_inst_mom.write("# 9-11. 3 components of instant specific force moment; \n");
            bw_inst_mom.write("# 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; \n");
            bw_inst_mom.write("# 13. absolute value of instant specific force moment; \n");
            bw_inst_mom.write("# 14. current influx of torsion energy; \n");
            bw_inst_mom.write("# 15. current coefficient for calculation of torsion energy change. \n");
            
            bw_grains.write
                   ("#   This file contains parameters of each cluster of cellular automata (CA) -\n" +
                    "# index, material, 3 Euler angles, dislocation density, average dislocation density, its maximal deviation, type:\n" +
                    "#   0 - cluster consists of inner CA located in surface or intermediate layers,\n" +
                    "#   1 - cluster consists of inner CA located in substrate,\n" +
                    "#   3 - cluster consists of outer boundary CA adiabatic for mechanical energy and possessing constant temperature,\n" +
                    "#   4 - cluster consists of outer boundary CA adiabatic for mechanical energy and effected by constant thermal energy influx,\n" +
                    "#   5 - cluster consists of outer boundary CA possessing constant strain rate and adiabatic for thermal energy,\n" +
                    "#   6 - cluster consists of outer boundary CA possessing constant strain rate and temperature,\n" +
                    "#   7 - cluster consists of outer boundary CA possessing constant strain rate and effected by constant thermal energy influx,\n" +
                    "#   8 - cluster consists of outer boundary CA possessing constant stress and adiabatic for thermal energy,\n" +
                    "#   9 - cluster consists of outer boundary CA possessing constant stress and temperature,\n" +
                    "#  10 - cluster consists of outer boundary CA possessing constant stress and effected by constant thermal energy influx,\n" +
                    "#  11 - cluster consists of outer boundary CA adiabatic for both mechanical and thermal parts of energy;\n"+
                    "# type of grain according to its role in recrystallization process: 0 - initial grain, 1 - new grain, 2 - twinning grain;\n" +
                    "# index of grain, from which twinning grain grows.\n");
            
            // Rounded value of temperature of current cell
            double round_cell_temperature;
            
            // Neighbour cell at 1st coordination sphere of current cell
            RecCellR3 neighb_cell;
            
            // Indices of neighbour cells at 1st coordination sphere of current cell
            int[] neighb_indices = new int[12];
            
            // Index of neighbour cell at 1st coordination sphere of current cell
            int neighb_index;
            
            // Pair of indices of neighbour cells
            ArrayList cell_pair = new ArrayList(0);
            ArrayList cell_pair1;
            
            // Variable for comparison of pairs
            boolean equal_pairs;
            
            // List of all possible pairs of indices of neighbour cells
            ArrayList neighb_cell_pairs = new ArrayList(0);

            // Total number of pairs of neighbour cells
            int cell_pair_number = 0;

            // Displacements of current cell and its neighbour
            double cell_displ = 0;
            double neighb_displ = 0;

            // Strain of current cell in the direction of normal vector with neighbour cell
            double bound_strain = 0;

            // Coordinates of current cell and its neighbour
            VectorR3 cell_coord, neighb_coord;

            // Vector normal to cell boundary
            VectorR3 bound_vect;

            // Displacement vector of current cell
            VectorR3 displ_vector = new VectorR3();

            // Coordinates of point at cell boundary
            VectorR3 bound_coord = new VectorR3();

            // Array of displacement vectors for all cells
            VectorR3[] displ_vectors = new VectorR3[spec_size];

            // Point at centre of cell boundary facet
            PointR3 bound_point;

            // Object consisting of point at centre of cell boundary facet
            // and displacement vector normal to this facet (topos)
         //   ToposR3 bound_norm_topos;
            double[] bound_norm_topos = new double[6];
            
            // Strain tensor of cell
            DoubleMatrix strain_tensor;
            
            // Sorting of list of toposes and deleting of repeated elements from it
          //  sortBoundNormToposes();
         /*
            BufferedWriter bw_bound_norm_vec = new BufferedWriter(new FileWriter(writeFileName+"_"+zeros+(step_counter+1)+"_bound_norm_vectors."+Common.RESULTS_FILE_EXTENSION));

            bw_bound_norm_vec.write("# Each string contains the following values:\n");
            bw_bound_norm_vec.write("# 1-3. the coordinates of cell boundary point;\n");
            bw_bound_norm_vec.write("# 4-6. the coordinates of cell boundary displacement vector.\n");
            
            // Writing of old and new values of toposes normal to cell boundaries            
            for(int cell_counter = 0; cell_counter<spec_size; cell_counter++)
            for(int topos_counter = 0; topos_counter<bound_norm_toposes[cell_counter].size(); topos_counter++)
            {
                
                // System.out.println(bound_norm_toposes.size()+" "+bound_norm_vec_counter);
          //      bound_norm_topos = (ToposR3)bound_norm_toposes[cell_counter].get(topos_counter);
          //      bw_bound_norm_vec.write(bound_norm_topos.getStartPointCoordX()+" "+bound_norm_topos.getStartPointCoordY()+" "+bound_norm_topos.getStartPointCoordZ()+" "+
          //                            bound_norm_topos.getX()+" "+bound_norm_topos.getY()+" "+bound_norm_topos.getZ());
          //      bw_bound_norm_vec.newLine();
          //      bw_bound_norm_vec.flush();

                // System.out.println(bound_norm_toposes.size()+" "+bound_norm_vec_counter);
                bound_norm_topos = (double[])bound_norm_toposes[cell_counter].get(topos_counter);
                bw_bound_norm_vec.write(bound_norm_topos[0]+" "+bound_norm_topos[1]+" "+bound_norm_topos[2]+" "+
                                        bound_norm_topos[3]+" "+bound_norm_topos[4]+" "+bound_norm_topos[5]);
                bw_bound_norm_vec.newLine();
                bw_bound_norm_vec.flush();
            }
            
            bw_bound_norm_vec.close();
            */

            /*
            for(int bound_norm_vec_counter = 0; bound_norm_vec_counter<bound_norm_toposes.size(); bound_norm_vec_counter++)
            {
                      
                // System.out.println(bound_norm_toposes.size()+" "+bound_norm_vec_counter);
                bound_norm_topos = (ToposR3)bound_norm_toposes.get(bound_norm_vec_counter);
                bw_bound_norm_vec.write(bound_norm_topos.getStartPointCoordX()+" "+bound_norm_topos.getStartPointCoordY()+" "+bound_norm_topos.getStartPointCoordZ()+" "+
                                      bound_norm_topos.getX()+" "+bound_norm_topos.getY()+" "+bound_norm_topos.getZ());
                bw_bound_norm_vec.newLine();
                bw_bound_norm_vec.flush();
            }            

            bw_bound_norm_vec.close();
          
            if(step_counter>-1)
            {
                BufferedWriter bw_old_bound_norm_vec = new BufferedWriter(new FileWriter(writeFileName+"_"+zeros_old+step_counter+"_bound_norm_vectors."+Common.RESULTS_FILE_EXTENSION));

                bw_old_bound_norm_vec.write("# Each string contains the following values:\n");
                bw_old_bound_norm_vec.write("# 1-3. the coordinates of cell boundary point;\n");
                bw_old_bound_norm_vec.write("# 4-6. the coordinates of cell boundary displacement vector.\n");

                // Writing of old values of toposes normal to cell boundaries
                for(int bound_norm_vec_counter = 0; bound_norm_vec_counter<old_bound_norm_toposes.size(); bound_norm_vec_counter++)
                {
                    bound_norm_topos = (ToposR3)old_bound_norm_toposes.get(bound_norm_vec_counter);
                    bw_old_bound_norm_vec.write(bound_norm_topos.getStartPointCoordX()+" "+bound_norm_topos.getStartPointCoordY()+" "+bound_norm_topos.getStartPointCoordZ()+" "+
                                      bound_norm_topos.getX()+" "+bound_norm_topos.getY()+" "+bound_norm_topos.getZ());
                    bw_old_bound_norm_vec.newLine();
                    bw_old_bound_norm_vec.flush();
                }

                bw_old_bound_norm_vec.close();
            }
            */
            
            // Number of files for writing of stress and strain tensors components            
            int displ_vec_file_number = (int)Math.min(step_number, 10); //1;// 
            
            // Choice of time steps when data are written into files.
            if(step_number > output_file_number)
            {
                if((step_counter+1) <= step_number)
                if((step_counter+1) % (step_number/displ_vec_file_number) == 0)
                    write_displ_vectors = true;
            }
            else
                write_displ_vectors = true;
            
            write_displ_vectors = false;
            
            if(write_displ_vectors)
         // if(writeToFile)
            {
                BufferedWriter bw_strains  = new BufferedWriter(new FileWriter(writeFileName+"_"+zeros+(step_counter+1)+"_strains."+Common.RESULTS_FILE_EXTENSION));                
                bw_strains.write("# Each string contains the following values:\n");
                bw_strains.write("#  0- 2. the indices of cell: i, j, k;\n");
                bw_strains.write("#  3- 5. the coordinates of cell: x, y, z;\n");
                bw_strains.write("#  6- 8. the coordinates of cell displacement vector: u_x, u_y, u_z;\n");
                bw_strains.write("#  9-11. the diagonal components of cell strain tensor: e_xx, e_yy, e_zz;\n");
                bw_strains.write("# 12-14. the non-diagonal components of cell strain tensor: e_xy=e_yx, e_yz=e_zy, e_zx=e_xz;\n");
                bw_strains.write("# 15-17. the shear components: O_xy=-O_yx, O_yz=-O_zy, O_zx=-O_xz.\n");
                bw_strains.write("# 18. the average strain e_av = (e_xx + e_yy + e_zz)/3.\n");
                
                BufferedWriter bw_stresses  = new BufferedWriter(new FileWriter(writeFileName+"_"+zeros+(step_counter+1)+"_stresses."+Common.RESULTS_FILE_EXTENSION));
                bw_stresses.write("# Each string contains the following values:\n");
                bw_stresses.write("#  0- 2. the indices of cell: i, j, k;\n");
                bw_stresses.write("#  3- 5. the coordinates of cell: x, y, z;\n");
                bw_stresses.write("#  6-14. the components of cell stress tensor: s_xx, t_xy, t_xz, t_yx, s_yy, t_yz, t_zx, t_zy, s_zz;\n");
                bw_stresses.write("# 15-17. the components of stress moment vector: M_x=t_zy-t_yz, M_y=t_xz-t_zx, M_z=t_yx-t_xy.\n");
                bw_stresses.write("# 18. the average stress s_av = (s_xx + s_yy + s_zz)/3.\n");
                bw_stresses.write("# 19. Mises stress s_m = sqrt(1/2)*sqrt[sqr(s_xx-s_yy) + sqr(s_yy-s_zz) + sqr(s_zz-s_xx) + 6*(t_yz*t_yz + t_zx*t_zx + t_yx*t_yx)].\n");
                
                // Printing of coordinates and displacement vectors for all cells;
                // calculation and printing of strain tensor components and shear components
                for(int cell_counter = 0; cell_counter<spec_size; cell_counter++)
                {
                    // Calculation of the components of the strain tensor of cell
                    calcAndWriteStrainTensorComponents(cell_counter, bw_strains);
                    writeStressTensorComponents(cell_counter, bw_stresses);
                }
                
                bw_strains.close();
                bw_stresses.close();
            }
            
            // List of displacement vectors for boundary points
            ArrayList bound_displ_vectors = new ArrayList(0);

            // List of coordinates for boundary points
            ArrayList bound_coordinates = new ArrayList(0);

            Three neighb_tr_index;

            // Pairs of indices of current cell and its neighbour
            cell_pair = new ArrayList(2);

            cell_pair.add(-1);
            cell_pair.add(-1);
            
            double mom_X=0, mom_Y=0, mom_Z=0;
            
            // Cell angle velocity
            VectorR3 angle_velocity;
            
            // Cell torsion angle
            VectorR3 torsion_angle;
            
            // Portion of defects in a cell
            double defect_portion = 0;
            double max_defect_portion = 0;
            double aver_def_portion = 0;
            
            long def_number = 0;
            long at_number  = 0;
            
            // Creation of arrays of coordinates and displacement vectors for points at cell boundaries
            for(int cellCounter = 0; cellCounter < spec_size; cellCounter++)
            {
                cell = (RecCellR3)get(cellCounter);

                cell_indices = calcTripleIndex(cellCounter, cellNumberI, cellNumberJ, cellNumberK);
              //  cell_coord = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());
                 
                cell_coord = cell.getCoordinates();

                // Pairs of indices of current cell and its neighbour
                cell_pair.set(0, cellCounter);

                neighb_indices = neighbours3D[cellCounter];

                // Cycle on neighbour cells
                for(int neighb_counter = 0; neighb_counter<neighb1S_number; neighb_counter++)
                {
                    // Index of current neighbour cell
                    neighb_index = neighb_indices[neighb_counter];
                    
                    if(neighb_index>-1)
                    {
                        cell_pair.set(1, neighb_index);

                      /*
                      //  bw_vec.write(cellCounter+" "+neighb_index+" "+cell_pair.get(0)+" "+cell_pair.get(1));
                      //  bw_vec.newLine();

                        equal_pairs = false;

                        // Testing of containing of considered pair of indices in the list.
                        // Calculation of all necessary parameters for point at cell boundary
                        for(int pair_counter = 0; pair_counter<cell_pairs.size(); pair_counter++)
                        {
                            cell_pair1 = (ArrayList)cell_pairs.get(pair_counter);

                            if(((((Integer)cell_pair.get(0)).intValue() == ((Integer)cell_pair1.get(1)).intValue())&
                               (((Integer)cell_pair.get(1)).intValue() == ((Integer)cell_pair1.get(0)).intValue()))|
                               ((((Integer)cell_pair1.get(0)).intValue() == ((Integer)cell_pair.get(0)).intValue())&
                               (((Integer)cell_pair1.get(1)).intValue() == ((Integer)cell_pair.get(1)).intValue())))
                            {
                                equal_pairs = true;
                                pair_counter = cell_pairs.size();

                                bw_vec.write(cell_pairs.size()+" equal pairs: "+cell_pair.get(0)+" "+cell_pair.get(1)+" == "+cell_pair1.get(0)+" "+cell_pair1.get(1)+"\n");
                            }
                            else

                            {
                                if(cell_pairs.size()<10)
                                    bw_vec.write(cell_pairs.size()+" diff. pairs: "+cell_pair.get(0)+" "+cell_pair.get(1)+" != "+cell_pair1.get(0)+" "+cell_pair1.get(1)+"\n");

                                equal_pairs = false;
                            }

                            bw_vec.flush();
                        }
                        */

                       // if(!equal_pairs)
                       // if(!cell_pairs.contains(cell_pair))
                        {
                            cell_pair_number++;
                            neighb_cell_pairs.add(cell_pair);

                    //        bw_vec.write(spec_size+" "+cell_pair_number+"\n");
                     //       bw_vec.flush();

                            // Neighbour cell
                            neighb_cell  = (RecCellR3)get(neighb_index);

                            // Indices and coordinates of neighbour
                            neighb_tr_index = calcTripleIndex(neighb_index, cellNumberI, cellNumberJ, cellNumberK);
                           // neighb_coord = calcCoordinates(neighb_tr_index.getI(), neighb_tr_index.getJ(), neighb_tr_index.getK());
                            neighb_coord = neighb_cell.getCoordinates();

                            // Calculation of coordinates of point at cell boundary
                            bound_coord = new VectorR3(cell_coord);
                            bound_coord.add(neighb_coord);
                            bound_coord.multiply(0.5);

                        //    if(!bound_coordinates.contains(bound_coord))
                            
                            {
                             /*
                              // Calculation of displacement vector for given point at cell boundary
                              displ_vector = new VectorR3(displ_vectors[cellCounter]);
                              displ_vector.add(displ_vectors[neighb_index]);
                              displ_vector.multiply(0.5);

                              // Addition of the vectors to corresponding arrays
                              bound_displ_vectors.add(displ_vector);
                              bound_coordinates.add(bound_coord);
                          
                              bw_vec.write(neighb_cell_pairs.size()+" "+cellCounter+" ("+cell_indices.getI()+" "+cell_indices.getJ()+" "+cell_indices.getK()+") "+
                                      neighb_index+" ("+neighb_tr_index.getI()+" "+neighb_tr_index.getJ()+" "+neighb_tr_index.getK()+") ");
                                        //   ((ArrayList)cell_pairs.get(cell_pair_number-1)).get(0)+" "+((ArrayList)cell_pairs.get(cell_pair_number-1)).get(1)+" ");

                              bw_vec.write("centre: "+cell_coord.getX()+" "+cell_coord.getY()+" "+cell_coord.getZ()+" ");
                              bw_vec.write("neighb: "+neighb_coord.getX()+" "+neighb_coord.getY()+" "+neighb_coord.getZ()+" ");
                              bw_vec.write("bound: "+bound_coord.getX()+" "+bound_coord.getY()+" "+bound_coord.getZ()+" ");
                              bw_vec.write("displ.vector: "+displ_vector.getX()+" "+displ_vector.getY()+" "+displ_vector.getZ()+"\n");
                           */
                            }
                        }
                    }
                }

                //bw_vec.newLine();
               // bw_vec.flush();
            }

       //     ToposMeshR3 topos_meshR3 = new ToposMeshR3(bound_displ_vectors, cell_pairs, bound_coordinates,
          //                                             writeFileName+"_"+zeros+(step_counter+1)+"_vectors."+Common.RESULTS_FILE_EXTENSION);
           
            // Printing of necessary parameters of cells to the file
            for(int cellCounter = 0; cellCounter < spec_size; cellCounter++)
            {
                cell = (RecCellR3)get(cellCounter);

                neighb_indices = neighbours3D[cellCounter];

                // Indices of cell
                cell_indices = cell.getIndices();

                indexI = cell_indices.getI();
                indexJ = cell_indices.getJ();
                indexK = cell_indices.getK();

                grain_type = cell.getGrainType();

                // Writing of cell temperatures and states to file
                // if(cell.getGrainType()!=Common.ADIABATIC_ADIABATIC_CELL)
                // if(cell.getGrainType()==Common.INNER_CELL)
                {
                    // TEST
                    // bw.write(cellCounter+" ");
                    
                    cell_temperature = cell.getTemperature();
                    cell_mech_stress = cell.getMechStress();//cell.getAverageStress();//
                    cell_strain      = cell.getStrain();
                    cell_heat_energy = cell.getThermalEnergy();

                    bw.write(grain_type+" ");
                    bw.write(cell.getType()+" ");

           //       writeToFilePoints(bw, cell_indices, ((Integer)Math.round(cell.getGrainIndex())).intValue());
                    writeToFilePoints(bw, cell_indices, ((Integer)cell.getGrainIndex()).intValue());

                    // Rounding of the value of cell temperature
                    round_cell_temperature = (int)Math.round(cell_temperature*1000.0)/1000.0;
                    
                 // bw.write(" "+round_cell_temperature+" "+cell_mech_stress);
                    bw.write(" "+cell_temperature+" "+cell_mech_stress);

                //    cell_mech_energy = cell.getMechEnergy();
                //    cell_mech_strain = cell.getStrain();

              //      bw.write(" "+round_cell_temperature+" "+cell_heat_energy+" "+cell_mech_stress);
              //      bw.write(" "+round_cell_temperature+" "+cell_mech_energy+" "+cell_mech_stress+" "+cell_mech_strain);

                    // Calculation and writing of principal stress and force moment coordinates of cell to file
               //     if((mech_flows==Common.EXECUTE_METHOD)|(heat_flows==Common.EXECUTE_METHOD))
                    {
                        if(writeToFile)
                        {
                            if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
                            {
                                principal_stress = calcPrincipalStress(cellCounter);
                             //   force_moment     = cell.getForceMoment();
                                
                                mom_X = cell.getForceMomentX();
                                mom_Y = cell.getForceMomentY();
                                mom_Z = cell.getForceMomentZ();

                                // Calculation of cell dislocation density
                                /* calcDislDensity(cellCounter, force_moment);
                                 ..... */

                                bw.write(" "+principal_stress+" "+mom_X+" "+mom_Y+" "+mom_Z);

                                // Recording of components of moments calculated according to stress tensor of current cell
                            //    bw.write(" "+principal_stress+ " "+
                              //          cell.getMomentX()*cell_surface/neighb1S_number+" "+
                                //        cell.getMomentY()*cell_surface/neighb1S_number+" "+
                                  //      cell.getMomentZ()*cell_surface/neighb1S_number);
                            }
                            else
                                bw.write(" 0 0 0 0");
                            
                      //    bw.write(" "+cell.getState()+" "+cell.getStrain()+" "+cell.getDislDensity());//+" aver_stress = "+cell.getAverageStress());
                            bw.write(" "+cell.getState()+" "+cell.getStrain()+" "+cell.getTorsionEnergy()/getCellVolume());
                            
                            at_number  = cell.getAtomNumber();
                            def_number = cell.getDefectNumber();
                            
                            defect_portion = (def_number*1.0)/(at_number + def_number);
                            
                            aver_def_portion += defect_portion;
                            
                            if(defect_portion > max_defect_portion)
                                max_defect_portion = defect_portion;
                            
                            bw.write(" | "+cell.getDislDensity()+" "+at_number+" "+def_number+" "+defect_portion);
                        }
                    }
                    
                    /*-----------------------------------------------------------------------------------------*/
                    bw_tors.write(grain_type+" ");
                    bw_tors.write(cell.getType()+" ");
                    
                    writeToFilePoints(bw_tors, cell_indices, ((Integer)cell.getGrainIndex()).intValue());
                    
                    double rel_tors_energy_change = 0;
                    double rel_tors_energy= 0;
                    double cell_mech_energy_change = 0;
                    double cell_tors_energy_change = 0;
                    
                    // Calculation and writing of principal stress and force moment coordinates of cell to file
                    if((mech_flows==Common.EXECUTE_METHOD)|(heat_flows==Common.EXECUTE_METHOD))
                    {
                        if(writeToFile)
                        {
                            if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
                            {
                               // angle_velocity = cell.getAngleVelocity();
                                
                                // 6-8. 3 components of angle velocity
                                bw_tors.write(" "+cell.getAngleVelocityX()+" "+cell.getAngleVelocityY()+" "+cell.getAngleVelocityZ());
                                
                                // 9-11. 3 components of torsion angle vector
                                bw_tors.write(" "+cell.getTorsionAngleX()+" "+cell.getTorsionAngleY()+" "+cell.getTorsionAngleZ());
                                
                                cell_mech_energy_change = Math.abs(cell.getMechEnergyChange());
                                cell_tors_energy_change = Math.abs(cell.getTorsionEnergyChange());
                            
                                if(cell_tors_energy_change + cell_mech_energy_change != 0)
                                  rel_tors_energy_change = cell_tors_energy_change/(cell_tors_energy_change + cell_mech_energy_change);
                            
                                cell_mech_energy    = Math.abs(cell.getMechEnergy());
                                cell_torsion_energy = Math.abs(cell.getTorsionEnergy());
                            
                                if(cell_torsion_energy + cell_mech_energy != 0)
                                  rel_tors_energy = cell_torsion_energy/(cell_torsion_energy + cell_mech_energy);
                            
                                bw_tors.write(" "+cell.getState()+" "/*cell_mech_energy_change+" "+cell_tors_energy_change+" "+*/+
                                              rel_tors_energy_change+" "+rel_tors_energy);//+" aver_stress = "+cell.getAverageStress());
                                
                                bw_tors.write(" "+(cell.getThermalEnergy()+cell.getMechEnergy())/cell_volume+" "+cell.get_mod_elast());
                            }
                            else
                                bw_tors.write(" 0 0 0 0 0 0 "+cell.getState()+" 0 0 0");                            
                            
                        }
                    }
                    /*-----------------------------------------------------------------------------------------*/
                    
                    bw_inst_mom.write(grain_type+" ");
                    bw_inst_mom.write(cell.getType()+" ");
                    
                    writeToFilePoints(bw_inst_mom, cell_indices, ((Integer)cell.getGrainIndex()).intValue());
                    
                  //  VectorR3 inst_force_moment;
                    double inst_force_mom_X = 0;
                    double inst_force_mom_Y = 0;
                    double inst_force_mom_Z = 0;
                    
                    double abs_inst_force_mom_X = 0;
                    double abs_inst_force_mom_Y = 0;
                    double abs_inst_force_mom_Z = 0;
                    double abs_inst_force_mom   = 0;
                    
                    // Calculation and writing of principal stress and force moment coordinates of cell to file
                    if((mech_flows==Common.EXECUTE_METHOD)|(heat_flows==Common.EXECUTE_METHOD))
                    {
                        if(writeToFile)
                        {
                            if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
                            {
                              //  inst_force_moment = cell.getInstantForceMoment();
                                
                                inst_force_mom_X        = cell.getInstantForceMomentX();
                                inst_force_mom_Y        = cell.getInstantForceMomentY();
                                inst_force_mom_Z        = cell.getInstantForceMomentZ();
                                
                                abs_inst_force_mom_X    = Math.abs(inst_force_mom_X);
                                abs_inst_force_mom_Y    = Math.abs(inst_force_mom_Y);
                                abs_inst_force_mom_Z    = Math.abs(inst_force_mom_Z);
                                
                                abs_inst_force_mom      = Math.sqrt(inst_force_mom_X*inst_force_mom_X + inst_force_mom_Y*inst_force_mom_Y + inst_force_mom_Z*inst_force_mom_Z);
                                cell_tors_energy_change = cell.getTorsionEnergyChange();
                                
                                // 6-8. 3 absolute values of components of instant specific force moment;
                                bw_inst_mom.write(" "+abs_inst_force_mom_X+" "+abs_inst_force_mom_Y+" "+abs_inst_force_mom_Z);
                                
                                // 9-11. 3 components of instant specific force moment;
                                bw_inst_mom.write(" "+inst_force_mom_X+" "+inst_force_mom_Y+" "+inst_force_mom_Z);
                                
                                // 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged;
                                // 13. absolute value of instant specific force moment;
                                // 14. current influx of torsion energy.
                                bw_inst_mom.write(" "+cell.getState()+" "+abs_inst_force_mom+" "+cell_tors_energy_change);
                                
                                // 15. current coefficient for calculation of torsion energy change.
                                bw_inst_mom.write(" "+cell.getCurrentTorsionEnergyCoeff());
                                
                                
                            //    System.out.println("CurrentTorsionEnergyCoeff = "+cell.getCurrentTorsionEnergyCoeff());
                            }
                            else
                                bw_inst_mom.write(" 0 0 0 0 0 0 "+cell.getState()+" 0 0 0");
                        }
                    }
                    
                    /*-----------------------------------------------------------------------------------------*/
                    // Search of minimal and maximal values of temperature and stress
                    // for inner cells of specimen
         //           if(cell.getGrainType() == Common.INNER_CELL)
                    if(search_extreme_values)
                    {
                      // Cell coordinates
                      cell_coordinates = calcCoordinates(cell_indices.getI(), cell_indices.getJ(), cell_indices.getK());

                      coord_X = cell_coordinates.getX();
                      coord_Y = cell_coordinates.getY();
                      coord_Z = cell_coordinates.getZ();

                      // Search of maximal values of coordinates of cells
                      if(coord_X > max_coord_X) max_coord_X = coord_X;
                      if(coord_Y > max_coord_Y) max_coord_Y = coord_Y;
                      if(coord_Z > max_coord_Z) max_coord_Z = coord_Z;
                      
                      if(grain_type!=Common.ADIABATIC_ADIABATIC_CELL & 
                         grain_type!=Common.STRAIN_ADIABATIC_CELL &
                         grain_type!=Common.STRESS_ADIABATIC_CELL)
                      {
                        // Search of minimal value of cell temperature
                        if(cell_temperature < min_temperature) min_temperature = cell_temperature;

                        // Search of maximal value of cell temperature
                        if(cell_temperature > max_temperature) max_temperature = cell_temperature;
                      }
                      
                      if(grain_type!=Common.ADIABATIC_ADIABATIC_CELL & 
                         grain_type!=Common.ADIABATIC_TEMPERATURE_CELL &
                         grain_type!=Common.ADIABATIC_THERMAL_CELL)
                      {
                        // Search of minimal value of cell stress
                        if(cell_mech_stress < min_mech_stress) min_mech_stress = cell_mech_stress;

                        // Search of minimal value of cell strain
                        if(cell_strain < min_strain)           min_strain = cell_strain;

                        // Search of maximal value of cell stress
                        if(cell_mech_stress > max_mech_stress) max_mech_stress = cell_mech_stress;

                        // Search of maximal value of cell strain
                        if(cell_strain > max_strain)           max_strain = cell_strain;
                      }
                    }

                    bw.newLine();
                    bw.flush();
                    
                    bw_tors.newLine();
                    bw_tors.flush();
                    
                    bw_inst_mom.newLine();
                    bw_inst_mom.flush();
                }
            }

            if(search_extreme_values)
            {
                // Writing of maximal values of coordinates,
                // minimal and maximal values of temperature and stress
        /*
                bw.write("# "+max_coord_X+" "+max_coord_Y+" "+max_coord_Z+" "+
                              min_temperature+" "+max_temperature+" "+
                              min_mech_stress+" "+max_mech_stress);
         */

                bw2 = new BufferedWriter(new FileWriter(writeFileName+"."+Common.EXTREME_VALUES_FILE_EXTENSION));

                bw2.write("#  Minimal value of temperature for outer boundary cells and inner cells at 0 time step");
                bw2.newLine();
                bw2.write("min_temperature    = "+min_temperature);
                bw2.newLine();
                bw2.newLine();
                bw2.write("#  Maximal value of temperature for outer boundary cells and inner cells at 0 time step");
                bw2.newLine();
                bw2.write("max_temperature    = "+max_temperature);
                bw2.newLine();
                bw2.newLine();
                bw2.write("#  Minimal value of stress for outer boundary cells and inner cells at 0 time step");
                bw2.newLine();
                bw2.write("min_mech_stress    = "+min_mech_stress);
                bw2.newLine();
                bw2.newLine();
                bw2.write("#  Maximal value of stress for outer boundary cells and inner cells at 0 time step");
                bw2.newLine();
                bw2.write("max_mech_stress    = "+max_mech_stress);
                bw2.newLine();
                bw2.newLine();
                bw2.write("#  Minimal value of strain for outer boundary cells and inner cells at 0 time step");
                bw2.newLine();
                bw2.write("min_strain         = "+min_strain);
                bw2.newLine();
                bw2.newLine();
                bw2.write("#  Maximal value of strain for outer boundary cells and inner cells at 0 time step");
                bw2.newLine();
                bw2.write("max_strain         = "+max_strain);
             //   bw2.newLine();
             //   bw2.newLine();
             //   search_extreme_values = false;
                
                bw2.flush();
            }
            
            if(writeToFile)
            {
                aver_def_portion = aver_def_portion/spec_size;
            //    bw4.write("Step # "+(step_counter+1)+": max_defect_portion = "+max_defect_portion+"; aver_def_portion = "+aver_def_portion);
            //    bw4.newLine();
            //    bw4.flush();
            }
            
            // Parameters of current grain
            String gr_material;
            double[] angles = new double[3];
            double disl_density;
            double aver_disl_density;
            double disl_density_deviation;
            byte energy_type;
            byte gr_rec_type;
            int root_gr_index;
            
            // Total number of initial, new and twinning grains
            int init_gr_number = 0;
            int new_gr_number  = 0;
            int twin_gr_number = 0;
            
            for(int grain_counter = 0; grain_counter < grain_number; grain_counter++)
            {
                // Parameters of current grain
                gr_material            = grains[grain_counter].getMaterialName();
                angles                 = grains[grain_counter].getAngles();
                disl_density           = grains[grain_counter].getDislDensity();
                aver_disl_density      = grains[grain_counter].getAverageDislDensity();
                disl_density_deviation = grains[grain_counter].getDislDensityDeviation();
                energy_type            = grains[grain_counter].getType();
                gr_rec_type            = grains[grain_counter].getRecrystType();
                root_gr_index          = grains[grain_counter].getRootGrainIndex();
                        
                bw_grains.write((grain_counter+1)+" "+gr_material+" "+angles[0]+" "+angles[1]+" "+angles[2]+" "+
                                disl_density+" "+aver_disl_density+" "+disl_density_deviation+" "+energy_type+" "+
                                gr_rec_type+" "+root_gr_index+"\n");
                bw_grains.flush();
                
                if(gr_rec_type == Common.INITIAL_GRAIN)
                    init_gr_number++;
                
                if(gr_rec_type == Common.NEW_GRAIN)
                    new_gr_number++;
                
                if(gr_rec_type == Common.TWINNING_GRAIN)
                    twin_gr_number++;
            }
            
            bw_grains.write("\n# Total number of grains = sum of numbers of initial, new and twinning grains:\n"+
                              "# "+grain_number+" = "+init_gr_number+" + "+new_gr_number+" + "+twin_gr_number);
            
            String string;
            StringTokenizer st;
            
           // Total number of colours
            int col_number    = 0;
            
            // Number of initial grains
          //  int old_gr_number = 0;
            
            // Number of colours of initial grains
            int init_grain_col_number = 0;
            
            // Number of colours of new grains
            int new_grain_col_number = 0;
            
            int colour_index = 0;
            int max_colour_index = 0;
            int grain_index  = 0;
            
            int new_gr_counter = 0;
            int twin_gr_counter = 0;
            
            // Array of grain colours
            int[] grain_colours   = new int[grain_number];
            int[] grain_colours_1 = new int[grain_number];
            
            if(br_colours.ready())
            {
                string = br_colours.readLine();
                st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                  col_number    = new Integer(st.nextToken()).intValue();
                  string        = st.nextToken();
                  string        = st.nextToken();
                  init_grain_col_number = new Integer(st.nextToken()).intValue();
                  new_grain_col_number  = new Integer(st.nextToken()).intValue();
                }
                
                bw_colours.write(col_number+" "+grain_number+" "+init_gr_number+" "+init_grain_col_number+" "+new_grain_col_number+"\n");
                bw_colours.flush();
                
                bw_colours_1.write(col_number+" "+grain_number+" "+init_gr_number+" "+init_grain_col_number+" "+new_grain_col_number+"\n");
                bw_colours_1.flush();
                
                for(int col_counter = 0; col_counter < col_number; col_counter++)
                {
                  string = br_colours.readLine();
                  
                  bw_colours.write(string+"\n");
                  bw_colours.flush();
                  
                  bw_colours_1.write(string+"\n");
                  bw_colours_1.flush();
                }
                
                for(int gr_counter = 0; gr_counter < init_gr_number; gr_counter++)
                {
                  string = br_colours.readLine();
                  
                  bw_colours.write(string+"\n");
                  bw_colours.flush();
                  
                  bw_colours_1.write(string+"\n");
                  bw_colours_1.flush();
                  
                  if(!string.equals(""))
                  {    
                    st = new StringTokenizer(string);
                  
                    grain_index  = new Integer(st.nextToken()).intValue();
                    colour_index = new Integer(st.nextToken()).intValue();
                  }
                  
                  if(grain_index > init_gr_number)// - 12)
                    colour_index = 0;
                  else
                    colour_index++;
                  
                  // Calculation of number of colours necessary for initial grain structure
                  if(colour_index > max_colour_index)
                    max_colour_index = colour_index;
                  
                  if(grain_index - 1 == gr_counter)
                  {
                    grain_colours[grain_index - 1]   = colour_index;
                    grain_colours_1[grain_index - 1] = colour_index;
                  }
                  else
                    System.out.println("ERROR!!! grain_index = "+grain_index+"; gr_counter = "+gr_counter);
                }
                
                for(int gr_counter = init_gr_number; gr_counter < grain_number; gr_counter++)
                {
                  gr_rec_type = grains[gr_counter].getRecrystType();
                  
                  if(gr_rec_type == Common.NEW_GRAIN)
                  {
                  //  grain_colours[gr_counter] = (int)Math.round(col_number*0.5*(1.0 + Math.random()));
                      
                    new_gr_counter++;
                    
                  //  grain_colours[gr_counter] = max_colour_index + (new_gr_counter - 1) % (col_number - max_colour_index);
                    
                    grain_colours[gr_counter]   = init_grain_col_number + (new_gr_counter - 1) % new_grain_col_number;
                    grain_colours_1[gr_counter] = init_grain_col_number + (new_gr_counter + twin_gr_counter - 1) % new_grain_col_number;
                  }
                          
                  if(gr_rec_type == Common.TWINNING_GRAIN)
                  {
                    twin_gr_counter++;
                    root_gr_index = grains[gr_counter].getRootGrainIndex();
                    
                    grain_colours[gr_counter]   = grain_colours[root_gr_index - 1];
                    grain_colours_1[gr_counter] = init_grain_col_number + (new_gr_counter + twin_gr_counter - 1) % new_grain_col_number;
                  }
                  
                  if(gr_rec_type == Common.INITIAL_GRAIN)
                  {
                      System.out.println("ERROR!!! New grain # "+(gr_counter + 1)+" has wrong type: gr_rec_type = INITIAL_GRAIN.");
                      grain_colours[gr_counter] = -1;
                      grain_colours_1[gr_counter] = -1;
                  }
                  
                  bw_colours.write((gr_counter + 1)+" "+grain_colours[gr_counter]+"\n");
                  bw_colours.flush();
                  
                  bw_colours_1.write((gr_counter + 1)+" "+grain_colours_1[gr_counter]+"\n");
                  bw_colours_1.flush();
                }
                
                bw_colours.write("\n# Total number of grains is the sum of numbers of old, new and twinning grains: \n"+
                                   "# "+grain_number+" = "+init_gr_number+" + "+new_gr_counter+" + "+twin_gr_counter);
                bw_colours.flush();
                
                bw_colours_1.write("\n# Total number of grains is the sum of numbers of old, new and twinning grains: \n"+
                                   "# "+grain_number+" = "+init_gr_number+" + "+new_gr_counter+" + "+twin_gr_counter);
                bw_colours_1.flush();
            }
            
            bw.close();
            bw_tors.close();
            bw_inst_mom.close();
            
            bw_grains.close();
            br_colours.close();
            bw_colours.close();
            bw_colours_1.close();
            
            // Writing of new information about grain colours to old file
            br_colours = new BufferedReader(new FileReader(new_colours_file_name));
            bw_colours = new BufferedWriter(new FileWriter(colours_file_name));
            
            while(br_colours.ready())
            {
              string = br_colours.readLine();
              
              bw_colours.write(string+"\n");
              bw_colours.flush();
            }
            
            br_colours.close();
            bw_colours.close();
            
            File new_colours_file = new File(new_colours_file_name);
            new_colours_file.delete();
            //------------------------------------------------------------------
            
            System.out.println("----------==========----------==========----------==========");
            System.out.println("Total number of time steps:                    "+(step_number-1));
            System.out.println("The following files are created at time step # "+zeros+(step_counter+1)+":");
            System.out.println(res_file_name);
            System.out.println(inst_mom_file_name);
            System.out.println(torsion_file_name);
            System.out.println("==========----------==========----------==========----------");
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method writes information about parameters of all cell boundaries at current time step to the file.
     * @param writeFileName name of file for writing of results
     * @param step_counter number of time step
     */
    private void writeCellBoundsFile(String writeFileName, long step_counter)
    {        
        // Exponent of power of 10 in standard representation
        // of total number of time steps
        int max_exponent = (int)Math.floor(Math.log10(step_number));

        // Exponent of power of 10 in standard representation
        // of number of current time step
        int exponent = 0;

        if(step_counter > 0)
            exponent     = (int)Math.floor(Math.log10(step_counter));

        // String of zeros
        String zeros = "";

        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros = zeros + "0";

        if(step_counter-1 > 0)
            exponent     = (int)Math.floor(Math.log10(step_counter-1));

        // String of zeros
        String zeros_old = "";

        for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
            zeros_old = zeros_old + "0";
        
        BufferedWriter bw_bounds;
        
        double coord_X, coord_Y, coord_Z;
        
        double vec_coord_X, vec_coord_Y, vec_coord_Z;
        
        // Array of stresses at cell boundary
        double[] bound_stresses;
        
        // Array of strain velocities at cell boundary
        double[] bound_velocities;
        
        int first_neighb_index;
        int second_neighb_index;
        double bound_stress   = 0;
        double bound_velocity = 0;
        
        VectorR3[] bound_vectors;
        PointR3[] bound_points;
        
        VectorR3 bound_norm_vector;
        PointR3 bound_point;
         
        RecCellR3 neighb_cell;
        int neighb_cell_index;
        
        // Integers corresponding to cell pair
        int pair_integer;
        int pair_integer_2;
        
        // List of integers corresponding to cell pairs
   //     cell_pairs = new ArrayList(0);
        
        // index og neighbour cell of neighbour cell of current cell
        int neighb_neighb_cell_index;
                
        try
        {           
          //  if(step_counter != -1)
            if(false)
            {
                bw_bounds  = new BufferedWriter(new FileWriter(writeFileName+"_"+zeros+step_counter+"_bounds."+Common.RESULTS_FILE_EXTENSION));
           
                bw_bounds.write("# Each string contains parameters of certain cell boundary:\n");
                bw_bounds.write("# 0. index of first cell; 1. index of second cell;\n");
                bw_bounds.write("# 2-4. coordinates of boundary centre; 5-7. coordinates of boundary normal vector;\n");
                bw_bounds.write("# 8. stress at boundary; 9. velocity of material strain at boundary.\n");
            }
                
            // Writing of data about cell boundaries
            for(int cell_counter = 0; cell_counter<size(); cell_counter++)
            {
              // Current cell
              cell = (RecCellR3)get(cell_counter);
              
              // Obtaining of arrays of cell boundary centres, normal vectors, stresses and material strain velocities
              bound_points     = cell.getBoundCentres();
              bound_vectors    = cell.getBoundNormVectors();
              bound_stresses   = cell.getBoundStresses();
              bound_velocities = cell.getBoundVelocities();
              
              // Cycle over cell neighbours at 1st coordination sphere
         //     if(cell.getGrainType() != Common.ADIABATIC_ADIABATIC_CELL)
              for(int neighbCounter = 0; neighbCounter<neighb1S_number; neighbCounter++)
              {
                // Index of current neighbour cell
                neighb_cell_index = neighbours1S[cell_counter][neighbCounter];
                
                if(neighb_cell_index > -1)
                { 
                  // Obtaining of neighbour cell
                  neighb_cell = (RecCellR3)get(neighb_cell_index);
                  
           //       if(neighb_cell.getGrainType() != Common.ADIABATIC_ADIABATIC_CELL)
                  {
                    // Integers corresponding to current cell pair
             //       pair_integer      = cell_counter*superior_integer + neighb_cell_index;
               //     pair_integer_2    = neighb_cell_index*superior_integer + cell_counter;
                    
                    if(step_counter == -1)
                    {
                        for(int neighb_neighb_counter = 0; neighb_neighb_counter < neighb1S_number; neighb_neighb_counter++)
                        {
                            neighb_neighb_cell_index = neighbours1S[neighb_cell_index][neighb_neighb_counter];
                            
                            if(neighb_neighb_cell_index == cell_counter)
                                cell_bound_records[neighb_cell_index][neighb_neighb_counter] = true;//false;//
                        }                        
                        
                        cell_bound_records[cell_counter][neighbCounter] = false;//true;//
                    }
                    else                        
               //   if(!cell_pairs.contains(pair_integer) & !cell_pairs.contains(pair_integer_2))
                    if(cell_bound_records[cell_counter][neighbCounter])
                    {
                  //    cell_pairs.add(pair_integer);
                      
                      // Obtaining of cell boundary centre point and normal vector
              //        bound_point       = cell_bound_centres[cell_counter][neighbCounter];
                //      bound_norm_vector = cell_bound_norm_vectors[cell_counter][neighbCounter];                      
                      bound_point       = bound_points[neighbCounter];
                      bound_norm_vector = bound_vectors[neighbCounter];
                      
                      // Rounding of coordinates of cell boundary centre point and normal vector
                      coord_X     = Math.round(1000*bound_point.getX())/1000.0;
                      coord_Y     = Math.round(1000*bound_point.getY())/1000.0;
                      coord_Z     = Math.round(1000*bound_point.getZ())/1000.0;
                
                      vec_coord_X = Math.round(1000*bound_norm_vector.getX())/1000.0;
                      vec_coord_Y = Math.round(1000*bound_norm_vector.getY())/1000.0;
                      vec_coord_Z = Math.round(1000*bound_norm_vector.getZ())/1000.0;
                      
                      // Obtaining of stress and material strain velocity at cell boundary
                   //   bound_stress      = cell_bound_stresses[cell_counter][neighbCounter];
                   //   bound_velocity    = cell_bound_velocities[cell_counter][neighbCounter];                      
                      
                      bound_stress      = bound_stresses[neighbCounter];
                      bound_velocity    = bound_velocities[neighbCounter];
                      
                      // Writing of data about cell boundary to file
                      if(false)
                      {
                        bw_bounds.write(cell_counter+" "+neighb_cell_index+" "+coord_X+" "+coord_Y+" "+coord_Z+" "+vec_coord_X+" "+vec_coord_Y+" "+vec_coord_Z+" "+bound_stress+" "+bound_velocity);
                                   
                 //     TEST
                  //      bw_bounds.write("   "+previous_stresses[cell_counter]+" "+previous_stresses[neighb_cell_index]);
                 //     END OF TEST
                      
                        bw_bounds.newLine();
                        bw_bounds.flush();
                      }
                    }
                  }
                }
              }
            }
            
            if(false)
                bw_bounds.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        
        if(false)
            System.out.println("Name of written file: "+writeFileName+"_"+zeros+step_counter+"_bounds."+Common.RESULTS_FILE_EXTENSION);
        
     /*
        try
        {            
            bw_bounds  = new BufferedWriter(new FileWriter(writeFileName+"_"+zeros+step_counter+"_bounds."+Common.RESULTS_FILE_EXTENSION));

            CellBoundary cell_bound;
            VectorR3 norm_vector;
            int first_neighb_index;
            int second_neighb_index;
            double bound_stress   = 0;
            double bound_velocity = 0;
            
            RecCellR3 first_cell, second_cell;
            int cell_pair_index;
            
            bw_bounds.write("# Each string contains parameters of certain cell boundary: 0. 1st cell index; 1. second cell index;\n");
            bw_bounds.write("# 2-4. coordinates of boundary centre; 5-7. coordinates of boundary normal vector;\n");
            bw_bounds.write("# 8. stress at boundary; 9. velocity of material movement through boundary \n");
                
            // Writing of data about cell boundaries
            for(int cell_bound_counter = 0; cell_bound_counter<cell_bounds.size(); cell_bound_counter++)
            {
                cell_pair_index  = (int)cell_pairs.get(cell_bound_counter);
                cell_bound = (CellBoundary)cell_bounds.get(cell_bound_counter);
                norm_vector = cell_bound.getNormVectorR3();
                
                first_neighb_index  = cell_bound.getFirstNeighbIndex();
                second_neighb_index = cell_bound.getSecondNeighbIndex();
                
                first_cell  = (RecCellR3)get(first_neighb_index);
                second_cell = (RecCellR3)get(second_neighb_index);
                        
                coord_X = cell_bound.getCoordX();
                coord_Y = cell_bound.getCoordY();
                coord_Z = cell_bound.getCoordZ();
                
                bound_stress   = cell_bound.getStress();
                bound_velocity = cell_bound.getVelocity();
                
            //    bw_bounds.write(cell_pair_index+" "+first_neighb_index+" "+second_neighb_index+" "+coord_X+" "+coord_Y+" "+coord_Z+" "+norm_vector.getX()+" "+norm_vector.getY()+" "+norm_vector.getZ()+"     "+
              //                  first_cell.getMechStress()+" "+second_cell.getMechStress()+" "+bound_stress+" "+bound_velocity);
                
                bw_bounds.write(first_neighb_index+" "+second_neighb_index+" "+coord_X+" "+coord_Y+" "+coord_Z+" "+norm_vector.getX()+" "+norm_vector.getY()+" "+norm_vector.getZ()+"     "+
                                bound_stress+" "+bound_velocity);
                
                bw_bounds.newLine();
                bw_bounds.flush();
                
                // TEST
             //   if(cell_bound_counter == 15)
               //     System.out.println("step_counter: "+step_counter+"; cell_bound_count: "+cell_bound_counter+"; bound_stress: "+bound_stress);
            }
            
            bw_bounds.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
        */
    }
    
    /** The method calculates summary force moment of cell according to stress vectors of neighbour cells
     * @param cell_index index of cell
     * @return summary force moment of cell
     */
    public VectorR3 calcSummaryForceMoment(int cell_index)
    {
      // Considerable cell
      RecCellR3 defCellR3 = (RecCellR3)get(cell_index);
      
      // Indices of neighbours on 1st coordination sphere of "central" cell
      int[] neighb1S_indices = neighbours1S[cell_index];
      
      // neighbours on 1st coordination sphere of "central" cell
      neighbCells = new RecCellR3[neighb1S_indices.length];
      
      // Vectors from "cell1S" to "central" cell
      cell1S_vectors = new VectorR3[neighb1S_number];
      
      for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
        cell1S_vectors[neighb1S_counter] = new VectorR3(0, 0, 0);
      
      // Vector of force moment of "central" cell corresponding to "cell1S"
      VectorR3 force_moment;
      
      // Summary force moment
      VectorR3 summary_force_moment = new VectorR3(0, 0, 0);
            
      // Size of specimen
      double spec_size = size();
      
      // Type of cell
      int en_type = defCellR3.getGrainType();
      
      // type of grain containing cell
      int grain_type;
      
      // Obtaining of all cells at 1st coordination sphere of "central" cell
      if(en_type == Common.INNER_CELL | en_type == Common.OUTER_CELL)
      for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
      if(neighb1S_indices[neighb1S_counter] > -1 & neighb1S_indices[neighb1S_counter] < spec_size)
      {
        // Neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
        neighbCells[neighb1S_counter] = (RecCellR3)get(neighb1S_indices[neighb1S_counter]);
          
        // Calculation of vector from "central" cell to "cell1S"
        cell1S_vectors[neighb1S_counter] = residial(neighbCells[neighb1S_counter].getCoordinates(), defCellR3.getCoordinates());
          
        // grain type of current neighbour cell
        grain_type = neighbCells[neighb1S_counter].getGrainType();
          
        if(grain_type != Common.ADIABATIC_ADIABATIC_CELL)
          cell1S_vectors[neighb1S_counter].normalize();
      }
      
      // Calculation of vectors of force moment for all pairs "central cell - cell1S" and summary force vector
      if(en_type == Common.INNER_CELL | en_type == Common.OUTER_CELL)
      for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
      if(neighb1S_indices[neighb1S_counter] > -1 & neighb1S_indices[neighb1S_counter] < spec_size)
      {
        // grain type of current neighbour cell
        grain_type = neighbCells[neighb1S_counter].getGrainType();
        
        if(grain_type != Common.ADIABATIC_ADIABATIC_CELL)
        {
          // Calculation of vector of force moment for current pair "central cell - cell1S"
          force_moment = calcVectorProduct(cell1S_vectors[neighb1S_counter], neighbCells[neighb1S_counter].getStressVector());
        
          // Calculation of summary force vector
          summary_force_moment.add(force_moment);
          
          // TEST
          if(step_counter % 100 == 0)
          if(cell_index == 726)
             System.out.println("Step # "+step_counter+". Cell # 726. Neighb.cell # "+neighbours1S[726][neighb1S_counter]+
                                "; coord.= ("+cell1S_vectors[neighb1S_counter].writeToString()+
                                "); stress= ("+neighbCells[neighb1S_counter].getStressVector().writeToString()+
                                "); force_mom= ("+force_moment.writeToString()+").");
        }
      }
      
      // TEST
      if(step_counter % 100 == 0)
      if(cell_index == 726)
        System.out.println("summary_force_moment = ("+summary_force_moment.writeToString()+").\n");
      
      return summary_force_moment;
    }
    
    /** The method calculates summary force moment of cell
     * @param cell_index index of cell
     * @return summary force moment of cell
     */
    public VectorR3 calcSummaryForceMoment(int cell_index, long step_counter)
    {
        // Considerable cell
        RecCellR3 defCellR3 = (RecCellR3)get(cell_index);

        //Indices of neighbours on 1st coordination sphere of "central" cell
        int[] neighb1S_indices = neighbours1S[cell_index];

        // List of indices of neighbours at 1st coordination sphere of "central" cell
        ArrayList centralCellNeihbIndices = new ArrayList(0);

        // Cell at 1st coordination sphere of "central" cell
        RecCellR3 cell1S;

        // Array of indices of neighbours of cell at 1st coordination sphere
        //of "central" cell
        int[] cell1S_neighb_indices = new int[neighb1S_number];

        // Neighbour of cell at 1st coordination sphere of "central" cell;
        //this neighbour is also located at 1st coordination sphere of "central" cell
        RecCellR3 cell1S_neighbour;

        // Stress from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
        double stress_from_cell1S_to_neighbour;

        // Vectors from "cell1S" to "central" cell
        cell1S_vectors = new VectorR3[neighb1S_number];
        
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_number; neighb1S_counter++)
            cell1S_vectors[neighb1S_counter] = new VectorR3(0, 0, 0);

        // Vector of stress from "cell1S" to its neighbour
        //located at 1st coordination sphere of "central" cell
        VectorR3 vector_from_cell1S_to_neighbour;

        // Vector of force moment of "central" cell corresponding to "cell1S"
        //and its neighbour located at 1st coordination sphere of "central" cell
        VectorR3 force_moment;

        // Summary force moment
        VectorR3 summary_force_moment = new VectorR3(0, 0, 0);

        // Size of specimen
        double spec_size = size();
        
        // type of grain containing cell
        int grain_type;

        // Neighbour cells at 1st coordination sphere of current "central" cell ("cell1S")
        neighbCells = new RecCellR3[neighb1S_indices.length];

     //   System.out.println("Cell index # "+cell_index);
        
        int test_cell_index = -1111; // calcSingleIndex(cellNumberI/2, cellNumberJ/2, cellNumberK/2); // 
        boolean testing     = false; // true; // 
        
        if(cell_index == test_cell_index)
            testing = true;
        else
            testing = false;
        
        // Obtaining of all cells at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        {
            if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<spec_size))
            {
                // Neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
                neighbCells[neighb1S_counter] = (RecCellR3)get(neighb1S_indices[neighb1S_counter]);

                // if(neighbCells[neighb1S_counter].getGrainType()==Common.INNER_CELL)
                // Calculation of vector from "central" cell to "cell1S"
                cell1S_vectors[neighb1S_counter] = residial(neighbCells[neighb1S_counter].getCoordinates(), defCellR3.getCoordinates());
                cell1S_vectors[neighb1S_counter].multiply(cell_size_X);
                
                // TEST
                if(testing)
                  System.out.println("Vector #"+neighb1S_counter+": "+cell1S_vectors[neighb1S_counter].getX()+" "+
                          cell1S_vectors[neighb1S_counter].getY()+" "+cell1S_vectors[neighb1S_counter].getZ());
                
             //   System.out.println("Vector #"+neighb1S_counter+": "+cell1S_vectors[neighb1S_counter].getX()+" "+cell1S_vectors[neighb1S_counter].getY()+" "+cell1S_vectors[neighb1S_counter].getZ());
                
                // TEST
            //    System.out.println("Neighbour index # "+neighb1S_counter+": "+cell1S_vectors[neighb1S_counter].getX()+" "+cell1S_vectors[neighb1S_counter].getY()+" "+cell1S_vectors[neighb1S_counter].getZ());
                
           //   cell1S_vectors[neighb1S_counter] = residial(calcCoordinates(neighb1S_tr_index.getI(), neighb1S_tr_index.getJ(), neighb1S_tr_index.getK()),
           //                                               calcCoordinates(cell_tr_index.getI(), cell_tr_index.getJ(), cell_tr_index.getK()));
            }

            // Addition of index of neighbour cell ("cell1S") to the list
            centralCellNeihbIndices.add(neighb1S_indices[neighb1S_counter]);
        }
        
    //    int neighb1s_counter = 0;
        
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
        // Obtaining of neighbours of each "cell1S" at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        if (neighb1S_indices[neighb1S_counter] > -1 & neighb1S_indices[neighb1S_counter] < spec_size)
        {
            // Current cell at 1st coordination sphere of "central" cell
            cell1S = neighbCells[neighb1S_counter];

            // Indices of neighbours of current cell at 1st coordination sphere of "central" cell
            cell1S_neighb_indices = neighbours1S[neighb1S_indices[neighb1S_counter]];

       //     System.out.print(neighb1S_counter+" | "+neighb1S_indices[neighb1S_counter]+" || ");

            grain_type = cell1S.getGrainType();
            
            for(int cell1S_neighb_counter = 0; cell1S_neighb_counter < cell1S_neighb_indices.length; cell1S_neighb_counter++)
            if (cell1S_neighb_indices[cell1S_neighb_counter] > -1 & cell1S_neighb_indices[cell1S_neighb_counter] < spec_size)
            if (grain_type==Common.INNER_CELL | grain_type==Common.LAYER_CELL)
            {
                // Calculation of stresses from "cell1S" to its neighbours at 1st coordination sphere
                if(centralCellNeihbIndices.contains(cell1S_neighb_indices[cell1S_neighb_counter]))
                {
                 //   System.out.print(centralCellNeihbIndices.indexOf(cell1S_neighb_indices[cell1S_neighb_counter])+" "+cell1S_neighb_indices[cell1S_neighb_counter]+" | ");

                    // "Cell1S" neighbour located at 1st coordination sphere of "central" cell
                    cell1S_neighbour = (RecCellR3)get(cell1S_neighb_indices[cell1S_neighb_counter]);

                    // Stress from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    stress_from_cell1S_to_neighbour = cell1S.getMechStress() - cell1S_neighbour.getMechStress();

                    // Vector from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour = residial(cell1S_neighbour.getCoordinates(), cell1S.getCoordinates());

                    // Normalization of vector "vector_from_cell1S_to_neighbour"
                    vector_from_cell1S_to_neighbour.normalize();
                    
                    // Calculation of vector of force from "cell1S" to its neighbour
                    // located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour.multiply(stress_from_cell1S_to_neighbour*cell_surface/neighb1S_number);
                    
                    // Calculation of force moment for pair "cell1S - cell1S neighbour"
                    force_moment = calcVectorProduct(cell1S_vectors[neighb1S_counter], vector_from_cell1S_to_neighbour);
                    
                    // TEST
                    if(false)
                    if(step_counter == 100 & cell_index == 3993)
                    {
                      double vect1_X = cell1S_vectors[neighb1S_counter].getX();
                      double vect1_Y = cell1S_vectors[neighb1S_counter].getY();
                      double vect1_Z = cell1S_vectors[neighb1S_counter].getZ();
                    
                      double vect2_X = vector_from_cell1S_to_neighbour.getX();
                      double vect2_Y = vector_from_cell1S_to_neighbour.getY();
                      double vect2_Z = vector_from_cell1S_to_neighbour.getZ();
                    
                      double coord_X = force_moment.getX();
                      double coord_Y = force_moment.getY();
                      double coord_Z = force_moment.getZ();
                      
                      int cell_Index = calcSingleIndex(cell1S.getIndices());
                    
                      System.out.println("Cell "+cell_Index+"; Nghb # "+cell1S_neighb_indices[cell1S_neighb_counter]+"; vect_1 = ("+vect1_X+"; "+vect1_Y+"; "+vect1_Z+")");
                      System.out.println("Cell "+cell_Index+"; Nghb # "+cell1S_neighb_indices[cell1S_neighb_counter]+"; vect_2 = ("+vect2_X+"; "+vect2_Y+"; "+vect2_Z+")");
                      System.out.println("Cell "+cell_Index+"; Nghb # "+cell1S_neighb_indices[cell1S_neighb_counter]+"; vect_p = ("+coord_X+"; "+coord_Y+"; "+coord_Z+")");
                    }
                    
                    // Calculation of specific force moment for pair "cell1S - cell1S neighbour"
                    force_moment.multiply(0.5/getCellVolume());

                    // Calculation of summary force moment for current cell
                    summary_force_moment.add(force_moment);
                    
                 //   neighb1s_counter++;
                }
            }
        }
        
      //  System.out.println("neighb1s_counter = "+neighb1s_counter);

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
        // Calculation of force moment according to cells at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<spec_size))
        {
            // Current cell at 1st coordination sphere of "central" cell
            cell1S = neighbCells[neighb1S_counter];

            grain_type = cell1S.getGrainType();

            for(int cell1S_counter = 0; cell1S_counter<neighb1S_indices.length; cell1S_counter++)
            if(neighb1S_counter != cell1S_counter)
            if((neighb1S_indices[cell1S_counter]>-1)&(neighb1S_indices[cell1S_counter]<spec_size))
            if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
            {
                // Calculation of stresses from "cell1S" to other neighbours
                //located at 1st coordination sphere of "defCellR3"

                // Another neighbour located at 1st coordination sphere of "central" cell
                cell1S_neighbour = neighbCells[cell1S_counter];

                // Calculation of stresses from "cell1S" to all another neighbours at 1st coordination sphere
                if(centralCellNeihbIndices.contains(neighb1S_indices[cell1S_counter]))
                {
                    // Stress from "cell1S" to another neighbour located at 1st coordination sphere of "central" cell
                    stress_from_cell1S_to_neighbour = cell1S.getMechStress()-cell1S_neighbour.getMechStress();

                    // Vector from "cell1S" to another neighbour located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour = residial(cell1S_neighbour.getCoordinates(), cell1S.getCoordinates());
                    
                    // Normalization of vector "vector_from_cell1S_to_neighbour"
                    vector_from_cell1S_to_neighbour.normalize();
                    
                    // Calculation of vector of force from "cell1S" to its neighbour
                    //located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour.multiply(stress_from_cell1S_to_neighbour*cell_surface/neighb1S_number);
                    
                    // Calculation of force moment for pair "cell1S - cell1S neighbour"
                    force_moment = calcVectorProduct(cell1S_vectors[neighb1S_counter], vector_from_cell1S_to_neighbour);

                    // Calculation of specific force moment for pair "cell1S - cell1S neighbour"
                    force_moment.multiply(1/getCellVolume());

                    // Calculation of summary force moment for current cell
                    summary_force_moment.add(force_moment);
                }
            }
        }
        
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
            cell1S_vectors[neighb1S_counter].multiply(1.0/cell_size_X);
        
        return summary_force_moment;
    }
    
    /** The method calculates summary force moment of cell for SCP of cells
     * taking into account cells at 1st-3rd coordination spheres of cell.
     * @param cell_index index of cell
     * @return summary force moment of cell
     */
    public VectorR3 calcSummaryForceMomentSCP(int cell_index)
    {
        // Considerable cell
        RecCellR3 defCellR3 = (RecCellR3)get(cell_index);

      //  System.out.println("cell_index = "+cell_index);

        //Indices of neighbours at 1st-3rd coordination spheres of "central" cell
        int[] neighb3D_indices = neighbours3D[cell_index];

        // Indices of neighbours of current cell at 1st-3rd coordination spheres of "central" cell
        int[] neighb_cell_neighb_indices;

        // List of indices of neighbours at 1st-3rd coordination spheres of "central" cell
        ArrayList centralCellNeihbIndices = new ArrayList(0);

        // Cell at 1st, 2nd or 3rd coordination sphere of "central" cell
        RecCellR3 neighb_cell;

        //   Neighbour of cell at 1st, 2nd or 3rd coordination sphere of "central" cell;
        // this neighbour is also located at 1st, 2nd or 3rd coordination sphere of "central" cell
        RecCellR3 neighb_cell_neighbour;

        // Stress from "neighb_cell" to its neighbour
        // located at 1st, 2nd or 3rd coordination sphere of "central" cell
        double stress_from_neighb_cell_to_neighbour;

        // Vectors from "neighb_cell" to "central" cell
        VectorR3[] neighb_cell_vectors = new VectorR3[neighb3D_number];

        //    Vector of stress from "cell1S" to its neighbour
        // located at 1st coordination sphere of "central" cell
        VectorR3 vector_from_neighb_cell_to_neighbour;

        //    Vector of force moment of "central" cell corresponding to "neighb_cell"
        // and its neighbour located at 1st, 2nd or 3rd coordination sphere of "central" cell
        VectorR3 force_moment;

        // Summary force moment
        VectorR3 summary_force_moment = new VectorR3(0, 0, 0);

        // Size of specimen
        double spec_size = size();

        // Grain type of neighbour cell
        int grain_type;

        // Neighbour cells at 1st-3rd coordination spheres of current "central" cell ("neighb_cell")
        neighbCells = new RecCellR3[neighb3D_indices.length];

        // Obtaining of all cells at 1st-3rd coordination spheres of "central" cell
        for(int neighb3D_counter = 0; neighb3D_counter < neighb3D_indices.length; neighb3D_counter++)
        {
            if((neighb3D_indices[neighb3D_counter]>-1)&(neighb3D_indices[neighb3D_counter]<spec_size))
            {
                // Neighbour cell at 1st-3rd coordination spheres of current "central" cell ("neighb_cell")
                neighbCells[neighb3D_counter] = (RecCellR3)get(neighb3D_indices[neighb3D_counter]);

           //   if(neighbCells[neighb3D_counter].getGrainType()==Common.INNER_CELL)
                // Calculation of vector from "central" cell to "neighb_cell"
                neighb_cell_vectors[neighb3D_counter] = residial(neighbCells[neighb3D_counter].getCoordinates(), defCellR3.getCoordinates());
                neighb_cell_vectors[neighb3D_counter].multiply(cell_size_X);
            }

            // Addition of index of neighbour cell ("neighb_cell") to the list
            centralCellNeihbIndices.add(neighb3D_indices[neighb3D_counter]);
        }

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
        // Obtaining of neighbours of each "neighb_cell" at 1st-3rd coordination spheres of "central" cell
        for(int neighb3D_counter = 0; neighb3D_counter < neighb3D_indices.length; neighb3D_counter++)
        if((neighb3D_indices[neighb3D_counter]>-1)&(neighb3D_indices[neighb3D_counter]<spec_size))
        {
            // Current cell at 1st-3rd coordination spheres of "central" cell
            neighb_cell = neighbCells[neighb3D_counter];

            // Indices of neighbours of current cell at 1st-3rd coordination spheres of "central" cell
            neighb_cell_neighb_indices = neighbours3D[neighb3D_indices[neighb3D_counter]];

       //   System.out.print(neighb1S_counter+" | "+neighb1S_indices[neighb1S_counter]+" || ");

            grain_type = neighb_cell.getGrainType();

            for(int neighb_cell_neighb_counter = 0; neighb_cell_neighb_counter<neighb_cell_neighb_indices.length; neighb_cell_neighb_counter++)
            if((neighb_cell_neighb_indices[neighb_cell_neighb_counter]>-1)&(neighb_cell_neighb_indices[neighb_cell_neighb_counter]<spec_size))
            if((grain_type==Common.INNER_CELL)|(grain_type==Common.LAYER_CELL))
            {
                // Calculation of stresses from "neighb_cell" to its neighbours at 1st-3rd coordination spheres
                if(centralCellNeihbIndices.contains(neighb_cell_neighb_indices[neighb_cell_neighb_counter]))
                {
                 //   System.out.print(centralCellNeihbIndices.indexOf(cell1S_neighb_indices[cell1S_neighb_counter])+" "+cell1S_neighb_indices[cell1S_neighb_counter]+" | ");
                    
                    // "Neighb_cell" neighbour located at 1st-3rd coordination spheres of "central" cell
                    neighb_cell_neighbour = (RecCellR3)get(neighb_cell_neighb_indices[neighb_cell_neighb_counter]);

                    // Stress from "neighb_cell" to its neighbour located at 1st-3rd coordination spheres of "central" cell
                    stress_from_neighb_cell_to_neighbour = neighb_cell.getMechStress()-neighb_cell_neighbour.getMechStress();

                    // Vector from "neighb_cell" to its neighbour located at 1st-3rd coordination spheres of "central" cell
                    vector_from_neighb_cell_to_neighbour = residial(neighb_cell_neighbour.getCoordinates(), neighb_cell.getCoordinates());

                    // Normalization of vector "vector_from_neighb_cell_to_neighbour"
                    vector_from_neighb_cell_to_neighbour.normalize();                    
                    
                    // Calculation of vector of force from "neighb_cell" to its neighbour
                    //located at 1st-3rd coordination spheres of "central" cell
                    vector_from_neighb_cell_to_neighbour.multiply(stress_from_neighb_cell_to_neighbour*cell_surface/neighb1S_number);

                    // Calculation of force moment for pair "neighb_cell - neighb_cell neighbour"
                    force_moment = calcVectorProduct(neighb_cell_vectors[neighb3D_counter], vector_from_neighb_cell_to_neighbour);

                    // Calculation of summary force moment
                    summary_force_moment.add(force_moment);
                }
            }
        }

        return summary_force_moment;
    }
    
    /** The method calculates angle velocity of cell.
     * @param cell_index index of cell
     * @return angle velocity of cell
     */
    public VectorR3 calcAngleVelocity(int cell_index)
    {
        // Considerable cell
        RecCellR3 defCellR3 = (RecCellR3)get(cell_index);

        //Indices of neighbours on 1st coordination sphere of "central" cell
        int[] neighb1S_indices = neighbours1S[cell_index];

        // List of indices of neighbours at 1st coordination sphere of "central" cell
        ArrayList centralCellNeihbIndices = new ArrayList(0);
        
        // Cell at 1st coordination sphere of "central" cell
        RecCellR3 cell1S;

        // Array of indices of neighbours of cell at 1st coordination sphere
        // of "central" cell
        int[] cell1S_neighb_indices = new int[neighb1S_number];

        // Neighbour of cell at 1st coordination sphere of "central" cell;
        // this neighbour is also located at 1st coordination sphere of "central" cell
        RecCellR3 cell1S_neighbour;

        // Velocity of material movement from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
        double velocity_from_cell1S_to_neighbour;

        // Vectors from "cell1S" to "central" cell
        cell1S_vectors = new VectorR3[neighb1S_number];

        // Vector of stress from "cell1S" to its neighbour
        // located at 1st coordination sphere of "central" cell
        VectorR3 vector_from_cell1S_to_neighbour;
        
        // Vector of angle velocity of "central" cell corresponding to "cell1S"
        // and its neighbour located at 1st coordination sphere of "central" cell
        VectorR3 angle_velocity;

        // Summary angle velocity
        VectorR3 summary_angle_velocity = new VectorR3(0, 0, 0);

        // Size of specimen
        double spec_size = size();

        // Type of grain containing current cell
        int grain_type;

        // Neighbour cells at 1st coordination sphere of current "central" cell ("cell1S")
        neighbCells = new RecCellR3[neighb1S_indices.length];
        
        // Array of velocities of material movement from "cell1S" to its neighbours
        double[] cell1S_bound_velocities = new double[12];
        
        // Length of vector between cells
        double vector_length = 0;

     //   System.out.println("Cell index # "+cell_index);
   
        // Obtaining of all cells at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        {
            if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<spec_size))
            {
                // Neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
                neighbCells[neighb1S_counter] = (RecCellR3)get(neighb1S_indices[neighb1S_counter]);

                // if(neighbCells[neighb1S_counter].getGrainType()==Common.INNER_CELL)
                // Calculation of vector from "central" cell to "cell1S"
                cell1S_vectors[neighb1S_counter] = residial(neighbCells[neighb1S_counter].getCoordinates(), defCellR3.getCoordinates());

             //   System.out.println("Vector #"+neighb1S_counter+": "+cell1S_vectors[neighb1S_counter].getX()+" "+cell1S_vectors[neighb1S_counter].getY()+" "+cell1S_vectors[neighb1S_counter].getZ());
                
                // TEST
            //    System.out.println("Neighbour index # "+neighb1S_counter+": "+cell1S_vectors[neighb1S_counter].getX()+" "+cell1S_vectors[neighb1S_counter].getY()+" "+cell1S_vectors[neighb1S_counter].getZ());
                
           //   cell1S_vectors[neighb1S_counter] = residial(calcCoordinates(neighb1S_tr_index.getI(), neighb1S_tr_index.getJ(), neighb1S_tr_index.getK()),
           //                                               calcCoordinates(cell_tr_index.getI(), cell_tr_index.getJ(), cell_tr_index.getK()));
            }

            // Addition of index of neighbour cell ("cell1S") to the list
            centralCellNeihbIndices.add(neighb1S_indices[neighb1S_counter]);
        }

        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
        // Obtaining of neighbours of each "cell1S" at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<spec_size))
        {
            // Current cell at 1st coordination sphere of "central" cell
            cell1S = neighbCells[neighb1S_counter];
            
            // Indices of neighbours of current cell at 1st coordination sphere of "central" cell
            cell1S_neighb_indices = neighbours1S[neighb1S_indices[neighb1S_counter]];
            
       //     System.out.print(neighb1S_counter+" | "+neighb1S_indices[neighb1S_counter]+" || ");
            
            grain_type = cell1S.getGrainType();
            
            cell1S_bound_velocities = cell1S.getBoundVelocities();
            
            for(int cell1S_neighb_counter = 0; cell1S_neighb_counter<cell1S_neighb_indices.length; cell1S_neighb_counter++)
            if((cell1S_neighb_indices[cell1S_neighb_counter]>-1)&(cell1S_neighb_indices[cell1S_neighb_counter]<spec_size))
            if((grain_type==Common.INNER_CELL)|
               (grain_type==Common.LAYER_CELL))
            {
                // Calculation of stresses from "cell1S" to its neighbours at 1st coordination sphere
                if(centralCellNeihbIndices.contains(cell1S_neighb_indices[cell1S_neighb_counter]))
                {
                 //   System.out.print(centralCellNeihbIndices.indexOf(cell1S_neighb_indices[cell1S_neighb_counter])+" "+cell1S_neighb_indices[cell1S_neighb_counter]+" | ");

                    // "Cell1S" neighbour located at 1st coordination sphere of "central" cell
                    cell1S_neighbour = (RecCellR3)get(cell1S_neighb_indices[cell1S_neighb_counter]);

                    // Velocity of material movement from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    velocity_from_cell1S_to_neighbour = cell1S_bound_velocities[cell1S_neighb_counter];

                    // Vector from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour = residial(cell1S_neighbour.getCoordinates(), cell1S.getCoordinates());
                    
                    vector_length = vector_from_cell1S_to_neighbour.getLength();

                    // Normalization of vector "vector_from_cell1S_to_neighbour"
                    vector_from_cell1S_to_neighbour.setX(vector_from_cell1S_to_neighbour.getX()/vector_from_cell1S_to_neighbour.getLength());
                    vector_from_cell1S_to_neighbour.setY(vector_from_cell1S_to_neighbour.getY()/vector_from_cell1S_to_neighbour.getLength());
                    vector_from_cell1S_to_neighbour.setZ(vector_from_cell1S_to_neighbour.getZ()/vector_from_cell1S_to_neighbour.getLength());

                    // Calculation of vector of velocity of material movement from "cell1S" to its neighbour
                    // located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour.setX(vector_from_cell1S_to_neighbour.getX()*velocity_from_cell1S_to_neighbour);
                    vector_from_cell1S_to_neighbour.setY(vector_from_cell1S_to_neighbour.getY()*velocity_from_cell1S_to_neighbour);
                    vector_from_cell1S_to_neighbour.setZ(vector_from_cell1S_to_neighbour.getZ()*velocity_from_cell1S_to_neighbour);

                    // Calculation of angle velocity for pair "cell1S - cell1S neighbour"
                    angle_velocity = calcVectorProduct(cell1S_vectors[neighb1S_counter], vector_from_cell1S_to_neighbour);
                    
                    angle_velocity.setX(angle_velocity.getX()/(vector_length*vector_length));
                    angle_velocity.setY(angle_velocity.getY()/(vector_length*vector_length));
                    angle_velocity.setZ(angle_velocity.getZ()/(vector_length*vector_length));
                    
                    // Calculation of summary angle velocity
                    summary_angle_velocity.setX(summary_angle_velocity.getX() + angle_velocity.getX());
                    summary_angle_velocity.setY(summary_angle_velocity.getY() + angle_velocity.getY());
                    summary_angle_velocity.setZ(summary_angle_velocity.getZ() + angle_velocity.getZ());
                }
            }
        }
        
        return summary_angle_velocity;
    }
    
    /** The method calculates angle velocity of cell according to 
     * velocities of material flows through element boundaries.
     * @param cell_index index of cell
     * @return angle velocity of cell
     */
    public VectorR3 calcSummaryAngleVelocity(int cell_index)
    {
        // Considerable cell
        RecCellR3 defCellR3 = (RecCellR3)get(cell_index);
        
        //Indices of neighbours on 1st coordination sphere of "central" cell
        int[] neighb1S_indices = neighbours1S[cell_index];
        
        // List of indices of neighbours at 1st coordination sphere of "central" cell
        ArrayList centralCellNeihbIndices = new ArrayList(0);
        
        // Cell at 1st coordination sphere of "central" cell
        RecCellR3 cell1S;
        
        // Array of indices of neighbours of cell at 1st coordination sphere
        // of "central" cell
        int[] cell1S_neighb_indices = new int[neighb1S_number];
        
        // Neighbour of cell at 1st coordination sphere of "central" cell;
        // this neighbour is also located at 1st coordination sphere of "central" cell
        RecCellR3 cell1S_neighbour;
        
        // Velocity of material movement from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
        double velocity_from_cell1S_to_neighbour;
        
        // Vectors from "cell1S" to "central" cell
        cell1S_vectors = new VectorR3[neighb1S_number];
        
        // Vector of stress from "cell1S" to its neighbour
        // located at 1st coordination sphere of "central" cell
        VectorR3 vector_from_cell1S_to_neighbour;
        
        // Vector of angle velocity of "central" cell corresponding to "cell1S"
        // and its neighbour located at 1st coordination sphere of "central" cell
        VectorR3 angle_velocity;
        
        // Summary angle velocity
        VectorR3 summary_angle_velocity = new VectorR3(0, 0, 0);
        
        // Size of specimen
        double spec_size = size();
        
        // Type of grain containing current cell
        int grain_type;
        
        // Neighbour cells at 1st coordination sphere of current "central" cell ("cell1S")
        neighbCells = new RecCellR3[neighb1S_indices.length];
        
        // Array of velocities of material movement from "cell1S" to its neighbours
        double[] cell1S_bound_velocities = new double[12];
        
        // Length of vector between cells
        double vector_length = 0;
        
        // Coordinates of centre of boundary between central cell and its neighbour
    //    PointR3 cell_bound_centre;
        
     // System.out.println("Cell index # "+cell_index);
        
        // Obtaining of all cells at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        {
            if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<spec_size))
            {
                // Neighbour cell at 1st coordination sphere of current "central" cell ("cell1S")
                neighbCells[neighb1S_counter] = (RecCellR3)get(neighb1S_indices[neighb1S_counter]);

                // if(neighbCells[neighb1S_counter].getGrainType()==Common.INNER_CELL)
                // Calculation of vector from "central" cell to "cell1S"
                cell1S_vectors[neighb1S_counter] = residial(neighbCells[neighb1S_counter].getCoordinates(), defCellR3.getCoordinates());
                
             // System.out.println("Vector #"+neighb1S_counter+": "+cell1S_vectors[neighb1S_counter].getX()+" "+cell1S_vectors[neighb1S_counter].getY()+" "+cell1S_vectors[neighb1S_counter].getZ());
                
                // TEST
             // System.out.println("Neighbour index # "+neighb1S_counter+": "+cell1S_vectors[neighb1S_counter].getX()+" "+cell1S_vectors[neighb1S_counter].getY()+" "+cell1S_vectors[neighb1S_counter].getZ());
                
             // cell1S_vectors[neighb1S_counter] = residial(calcCoordinates(neighb1S_tr_index.getI(), neighb1S_tr_index.getJ(), neighb1S_tr_index.getK()),
             //                                               calcCoordinates(cell_tr_index.getI(), cell_tr_index.getJ(), cell_tr_index.getK()));
            }
            
            // Addition of index of neighbour cell ("cell1S") to the list
            centralCellNeihbIndices.add(neighb1S_indices[neighb1S_counter]);
        }
        
      //  if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
        // Obtaining of neighbours of each "cell1S" at 1st coordination sphere of "central" cell
        for(int neighb1S_counter = 0; neighb1S_counter < neighb1S_indices.length; neighb1S_counter++)
        if((neighb1S_indices[neighb1S_counter]>-1)&(neighb1S_indices[neighb1S_counter]<spec_size))
        {
            // Current cell at 1st coordination sphere of "central" cell
            cell1S = neighbCells[neighb1S_counter];
            
            // Indices of neighbours of current cell at 1st coordination sphere of "central" cell
            cell1S_neighb_indices = neighbours1S[neighb1S_indices[neighb1S_counter]];
            
         // System.out.print(neighb1S_counter+" | "+neighb1S_indices[neighb1S_counter]+" || ");
            
            grain_type = cell1S.getGrainType();
            
            cell1S_bound_velocities = cell1S.getBoundVelocities();
            
            for(int cell1S_neighb_counter = 0; cell1S_neighb_counter<cell1S_neighb_indices.length; cell1S_neighb_counter++)
            if((cell1S_neighb_indices[cell1S_neighb_counter]>-1)&(cell1S_neighb_indices[cell1S_neighb_counter]<spec_size))
            if((grain_type==Common.INNER_CELL)|
               (grain_type==Common.LAYER_CELL))
            {
                // Calculation of velocities of material movement from "cell1S" to its neighbours at 1st coordination sphere
                if(centralCellNeihbIndices.contains(cell1S_neighb_indices[cell1S_neighb_counter]))
                {
                 //   System.out.print(centralCellNeihbIndices.indexOf(cell1S_neighb_indices[cell1S_neighb_counter])+" "+cell1S_neighb_indices[cell1S_neighb_counter]+" | ");

                    // "Cell1S" neighbour located at 1st coordination sphere of "central" cell
                    cell1S_neighbour = (RecCellR3)get(cell1S_neighb_indices[cell1S_neighb_counter]);

                    // Velocity of material movement from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    velocity_from_cell1S_to_neighbour = cell1S_bound_velocities[cell1S_neighb_counter];

                    // Vector from "cell1S" to its neighbour located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour = residial(cell1S_neighbour.getCoordinates(), cell1S.getCoordinates());
                    
                    vector_length = vector_from_cell1S_to_neighbour.getLength();
                    
                    // Normalization of vector "vector_from_cell1S_to_neighbour"
                    vector_from_cell1S_to_neighbour.setX(vector_from_cell1S_to_neighbour.getX()/vector_length);
                    vector_from_cell1S_to_neighbour.setY(vector_from_cell1S_to_neighbour.getY()/vector_length);
                    vector_from_cell1S_to_neighbour.setZ(vector_from_cell1S_to_neighbour.getZ()/vector_length);

                    // Calculation of vector of velocity of material movement from "cell1S" to its neighbour
                    // located at 1st coordination sphere of "central" cell
                    vector_from_cell1S_to_neighbour.setX(vector_from_cell1S_to_neighbour.getX()*velocity_from_cell1S_to_neighbour);
                    vector_from_cell1S_to_neighbour.setY(vector_from_cell1S_to_neighbour.getY()*velocity_from_cell1S_to_neighbour);
                    vector_from_cell1S_to_neighbour.setZ(vector_from_cell1S_to_neighbour.getZ()*velocity_from_cell1S_to_neighbour);

                    // Calculation of angle velocity for pair "cell1S - cell1S neighbour"
                    angle_velocity = calcVectorProduct(cell1S_vectors[neighb1S_counter], vector_from_cell1S_to_neighbour);
                    
                    vector_length = cell1S_vectors[neighb1S_counter].getLength();
                    
                    angle_velocity.setX(angle_velocity.getX()/(vector_length*vector_length));
                    angle_velocity.setY(angle_velocity.getY()/(vector_length*vector_length));
                    angle_velocity.setZ(angle_velocity.getZ()/(vector_length*vector_length));
                    
                    // Calculation of summary angle velocity
                    summary_angle_velocity.setX(summary_angle_velocity.getX() + angle_velocity.getX());
                    summary_angle_velocity.setY(summary_angle_velocity.getY() + angle_velocity.getY());
                    summary_angle_velocity.setZ(summary_angle_velocity.getZ() + angle_velocity.getZ());
                }
            }
        }
        
        return summary_angle_velocity;
    }

    /** The method calculates angle velocity of cell according to 
     * velocities of material flows through element boundaries.
     * @param cell_index index of cell
     * @param coord_spheres_number number of coordination spheres used for calculation
     * @return angle velocity of cell
     */
    public VectorR3 calcSummaryAngleVelocity(int cell_index, byte coord_spheres_number)
    {
        // Considerable cell
        RecCellR3 defCellR3 = (RecCellR3)get(cell_index);
        
        //Indices of neighbours of "central" cell
        int[] neighb_indices = neighbours3D[cell_index];
        
        // List of indices of neighbours at 1st-3rd coordination spheres of "central" cell
        ArrayList centralCellNeihbIndices = new ArrayList(0);
        
        // Neighbour cell
        RecCellR3 neighb_cell;
        
        // Array of indices of neighbours of neighbour cell
        int[] neighb_cell_neighb_indices = new int[neighb3D_number];
        
        // Neighbour of neighbour cell: this neighbour is also neighbour of "central" cell
        RecCellR3 neighb_cell_neighbour;
        
        // Velocity of material movement from "neighb_cell" to its neighbour
        double velocity_from_neighb_cell_to_neighbour;
        
        // Vectors from "neighb_cell" to "central" cell
        VectorR3[] cell3D_vectors = new VectorR3[neighb3D_number];
        
        // Vector of stress from "neighb_cell" to its neighbour
        // located at 1st-3rd coordination spheres of "central" cell
        VectorR3 vector_from_neighb_cell_to_neighbour;
        
        // Vector of angle velocity of "central" cell corresponding to "neighb_cell"
        // and its neighbour located at 1st-3rd coordination spheres of "central" cell
        VectorR3 angle_velocity;
        
        // Summary angle velocity
        VectorR3 summary_angle_velocity = new VectorR3(0, 0, 0);
        
        // Size of specimen
        double spec_size = size();
        
        // Type of grain containing current cell
        int grain_type;
        
        // Neighbour cells at 1st-3rd coordination spheres of current "central" cell ("neighb_cell")
        neighbCells = new RecCellR3[neighb_indices.length];
        
        // Array of velocities of material movement from "neighb_cell" to its neighbours
        double[] neighb_cell_bound_velocities = new double[neighb3D_number];
        
        // Length of vector between cells
        double vector_length = 0;
        
        // Coordinates of centre of boundary between central cell and its neighbour
    //    PointR3 cell_bound_centre;
        
     // System.out.println("Cell index # "+cell_index);
        
        // Number of neighbour cells used for calculation of torsion velocity
        int neighb_cell_number = 0;
        
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
        {
            if(coord_spheres_number == 1) neighb_cell_number = neighb1S_number;//12
            if(coord_spheres_number == 2) neighb_cell_number = 18;
            if(coord_spheres_number == 3) neighb_cell_number = 42;
            if(coord_spheres_number == 4) neighb_cell_number = neighb3D_number;//54
        }
        
        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
        {
            if(coord_spheres_number == 1) neighb_cell_number = neighb1S_number;//6
            if(coord_spheres_number == 2) neighb_cell_number = 18;
            if(coord_spheres_number == 3) neighb_cell_number = neighb3D_number;//26
            if(coord_spheres_number == 4) neighb_cell_number = 32;
        }
        
        Three neighb_cell_indices = new Three();
        
        int test_cell_index = -11111;//calcSingleIndex(cellNumberI/2, cellNumberJ/2, cellNumberK/2);
        boolean testing = false;// false;// 
        
        if(cell_index == test_cell_index)
        {
            testing = true;
            System.out.println();
            System.out.println("Cell indices: "+cellNumberI/2+" "+cellNumberJ/2+" "+cellNumberK/2);
            System.out.println();
        }
        
        // Obtaining of all neighbour cells at certain number of coordination spheres of "central" cell
        for(int neighb_counter = 0; neighb_counter < neighb_cell_number; neighb_counter++)
        {
            if((neighb_indices[neighb_counter]>-1)&(neighb_indices[neighb_counter]<spec_size))
            {
                // Neighbour cell of current "central" cell ("neighb_cell")
                neighbCells[neighb_counter] = (RecCellR3)get(neighb_indices[neighb_counter]);
                
                // Calculation of vector from "central" cell to "neighb_cell"
                cell3D_vectors[neighb_counter] = residial(neighbCells[neighb_counter].getCoordinates(), defCellR3.getCoordinates());
                
                cell3D_vectors[neighb_counter].multiply(cell_size_X);
                
                neighb_cell_indices = calcTripleIndex(neighb_indices[neighb_counter], cellNumberI, cellNumberJ, cellNumberK);
                
                // TEST
                if(testing)
                  System.out.println("Vector #"+neighb_counter+": "+cell3D_vectors[neighb_counter].getX()+" "+
                          cell3D_vectors[neighb_counter].getY()+" "+cell3D_vectors[neighb_counter].getZ()+
                          " | neighbour cell indices: "+neighb_cell_indices.writeToString()+" | ");                
            }
            
            // Addition of index of neighbour cell ("neighb_cell") to the list
            centralCellNeihbIndices.add(neighb_indices[neighb_counter]);
        }
        
        if(false)
        if(testing)
            System.out.println();
        
        // Obtaining of neighbours of each "neighb_cell" at 1st-3rd coordination spheres of "central" cell
        for(int neighb_counter = 0; neighb_counter < neighb_cell_number; neighb_counter++)
        if(neighb_indices[neighb_counter]>-1 & neighb_indices[neighb_counter]<spec_size)
        {
            // Current cell at 1st-3rd coordination spheres of "central" cell
            neighb_cell = neighbCells[neighb_counter];
            
            // Indices of neighbours of current cell at 1st-3rd coordination spheres of "central" cell
            neighb_cell_neighb_indices = neighbours1S[neighb_indices[neighb_counter]];
            
            neighb_cell_indices = calcTripleIndex(neighb_indices[neighb_counter], cellNumberI, cellNumberJ, cellNumberK);
            
            if(false)
            if(testing)
              System.out.println("Neighb cell # "+neighb_counter+". Neighbour cell index: "+neighb_indices[neighb_counter]+" | indices: "+neighb_cell_indices.writeToString());
            
            grain_type = neighb_cell.getGrainType();
            
            neighb_cell_bound_velocities = neighb_cell.getBoundVelocities();
            
            for(int neighb_cell_neighb_counter = 0; neighb_cell_neighb_counter<neighb_cell_neighb_indices.length; neighb_cell_neighb_counter++)
            if((neighb_cell_neighb_indices[neighb_cell_neighb_counter]>-1)&(neighb_cell_neighb_indices[neighb_cell_neighb_counter]<spec_size))
            // if(grain_type==Common.INNER_CELL | grain_type==Common.LAYER_CELL)
            {
                // Calculation of velocities of material movement from "neighb_cell" to its neighbours at 1st coordination sphere
                if(centralCellNeihbIndices.contains(neighb_cell_neighb_indices[neighb_cell_neighb_counter]))
                {
                    if(false)
                    if(testing)
                    {
                      neighb_cell_indices = calcTripleIndex(neighb_cell_neighb_indices[neighb_cell_neighb_counter], cellNumberI, cellNumberJ, cellNumberK);
                              
                      System.out.println("  neighb. cell # "+centralCellNeihbIndices.indexOf(neighb_cell_neighb_indices[neighb_cell_neighb_counter])+
                                         " | neighbour cell indices: "+neighb_cell_indices.writeToString()+" | ");
                    }

                    // "neighb_cell" neighbour located at 1st-3rd coordination spheres of "central" cell
                    neighb_cell_neighbour = (RecCellR3)get(neighb_cell_neighb_indices[neighb_cell_neighb_counter]);

                    // Velocity of material movement from "neighb_cell" to its neighbour located at 1st-3rd coordination spheres of "central" cell
                    velocity_from_neighb_cell_to_neighbour = neighb_cell_bound_velocities[neighb_cell_neighb_counter];

                    // Vector from "neighb_cell" to its neighbour located at 1st-3rd coordination spheres of "central" cell
                    vector_from_neighb_cell_to_neighbour = residial(neighb_cell_neighbour.getCoordinates(), neighb_cell.getCoordinates());
                   
                    // Normalization of vector "vector_from_neighb_cell_to_neighbour"
                    vector_from_neighb_cell_to_neighbour.normalize();
                    
                    // Calculation of vector of velocity of material movement from "cell1S" to its neighbour
                    // located at 1st-3rd coordination spheres of "central" cell
                    vector_from_neighb_cell_to_neighbour.multiply(velocity_from_neighb_cell_to_neighbour);
                    
                    // Calculation of angle velocity for pair "neighb_cell - neighb_cell_neighbour"
                    angle_velocity = calcVectorProduct(cell3D_vectors[neighb_counter], vector_from_neighb_cell_to_neighbour);
                    
                    vector_length = cell3D_vectors[neighb_counter].getLength();
                    angle_velocity.multiply(1/(vector_length*vector_length));
                    
                    // Calculation of summary angle velocity
                    summary_angle_velocity.add(angle_velocity);
                }
            }
            
            if(false)
            if(testing)
                System.out.println();
        }
        
        return summary_angle_velocity;
    }

    /** The method returns vector product of two vectors (vect_1 X vect_2).
     * @param vect_1 first vector
     * @param vect_2 second vector
     * @return vector product of two vectors (vect_1 X vect_2)
     */
    public VectorR3 calcVectorProduct(VectorR3 vect_1, VectorR3 vect_2)
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

    /** The method calculates principal stresses of cell with certain single index.
     * @param cell_index single index of cell
     */
    public double calcPrincipalStress(int cell_index)
    {
        // Cell neighbour at 1st coordination sphere
        RecCellR3 neighb_cell;

        // Array of indices of cell neighbours at 1st coordination sphere
        int[] neighb_indices = neighbours1S[index];

        // Coordinates of central cell and its neighbours
    //    VectorR3[] cell_coordinates = new VectorR3[neighb1S_number+1];

        // Stresses of central cell and its neighbours
        double[] stresses = new double[neighb1S_number+1];

        // Gradients of mechanical energy of central cell and its neighbours
        double[] stress_gradients = new double[neighb1S_number];

        // Principal stress of cell
        double principal_stress = 0;

        // Obtaining of cell with given single index
        RecCellR3 cellR3 = (RecCellR3)get(cell_index);
        
        // Cell type
  //      byte type = cellR3.getGrainType();

        // Calculation of principal stress for inner cell only
     //   if(type == Common.INNER_CELL)
        {
            // Cell coordinates
       //     cell_coordinates[0] = cellR3.getCoordinates();

            // Stress of cell
            stresses[0] = cellR3.getMechStress();

            for(int neighb_counter = 0; neighb_counter<neighb_indices.length; neighb_counter++)
            if(neighb_indices[neighb_counter]>-1)
            {
                neighb_cell = (RecCellR3)get(neighb_indices[neighb_counter]);

                // Mechanical stress of neighbour cell
                stresses[neighb_counter+1] = neighb_cell.getMechStress();

                // Gradient of mechanical stresses of central cell and its neighbour
                stress_gradients[neighb_counter] = stresses[neighb_counter+1] - stresses[0];

                // Calculation of sum of gradients of mechanical stresses
                principal_stress = principal_stress + stress_gradients[neighb_counter];
            }

            // Calculation of principal stress of cell
            principal_stress = principal_stress/neighb1S_number;
        }

        return principal_stress;
    }
    
    /** The method switches state of cell stochastically 
     * according to the value of cell strain.
     * @param cell considered cell
     */
    public void switchState(RecCellR3 cell)
    {
        // Current value of cell strain
        double strain = cell.getHeatStrain();
        
        // Value of material yield strain of cell
        double yieldStrain = cell.get_yieldStrain();
        
        // Probability of switching.        
        probSwitch = Math.exp(-yieldStrain/strain);
                       
        // Determination of cell state.
        rand = Math.random();
        
        if (rand < probSwitch)
        {
            cell.setState((byte)(cell.getState()+1));
        }
    }
     
    /** The method calculates average values of heat capacity and heat conductivity 
     * for whole net of cellular automata.
     * @return average values of heat capacity and heat conductivity of finite element
     */
    public double[] calcAverageParametersFE()
    {        
        double averageHeatCapacity = 0;
        double averageHeatConductivity = 0;
        
        // Spatial cycle on automata located in given finite element.
        for (indexK = 0; indexK < cellNumberK; indexK++)
        for (indexJ = 0; indexJ < cellNumberJ; indexJ++)
        for (indexI = 0; indexI < cellNumberI; indexI++)
        {
            // Calculation of triple index of the cell with given indices.
            cell_indices = new Three(indexI, indexJ, indexK);
            
            // Call of the cell with given indices from list of all cells of specimen.
            index = calcSingleIndex(cell_indices);
            cell = (RecCellR3)get(index);
            
            // Material of the cell
      //      Material material = new Material (cell.getMaterial());
            
            // Heat capacity and heat conductivity of the cell
            heatCap  = cell.get_specific_heat_capacity();
            heatCond = cell.get_thermal_conductivity();
            
            // Sum of the parameters for considered cells
            averageHeatCapacity = averageHeatCapacity + heatCap;
            averageHeatConductivity = averageHeatConductivity + heatCond;
        }
        
        // Number of cells containing in finite element
        int cellNumberFE = cellNumberI*cellNumberJ*cellNumberK;
        
        // Calculation and returning of average parameters
        averageHeatCapacity = averageHeatCapacity/cellNumberFE;
        averageHeatConductivity = averageHeatConductivity/cellNumberFE;
        
        double[] parameters = new double[2];
        
        parameters[0] = averageHeatCapacity;
        parameters[1] = averageHeatConductivity;
        
        return parameters;
    }
    
    /** The method calculates probability of change of cell state.
     * @param _cell considered cell.
     * @return probability of change of cell state
     */
    public double calcProbSwitch(RecCellR3 _cell)
    {
        byte state = _cell.getState();
        
        // Material of cell.
        Material cell_material = new Material(_cell.getMaterialFile());
        
        // Current value of cell strain.
        double strain = _cell.getHeatStrain();
        
        // Value of material yield strain of cell
        double yieldStrain = cell_material.get_yieldStrain();
                        
        // Calculation of switch probability.
        if(state == 0)
        {        
            // Probability of switching
            probSwitch = Math.exp(-yieldStrain/strain);
        }        
                
        return probSwitch;
    }
    
    /** The method calculates volume of cell3D 
     * (in case of hexagonal close packing or in case of simple cubic packing).
     * @param packing_type type of packing
     */
    private void calcCellVolume(byte packing_type)
    {
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            cell_volume = 0.6938*cell_size_X*cell_size_Y*cell_size_Z;

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            cell_volume = cell_size_X*cell_size_Y*cell_size_Z;
        
        System.out.println("calcCellVolume(): cell_volume = "+cell_volume);
    }

    /** The method calculates surface area of cell3D
     * (in case of hexagonal close packing or in case of simple cubic packing).
     * @param packing_type type of packing
     * @return surface area of cell3D
     */
    public void calcCellSurface(byte packing_type)
    {
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            cell_surface = 2*0.6938*(cell_size_X*cell_size_Y +
                                     cell_size_Y*cell_size_Z +
                                     cell_size_Z*cell_size_X);

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            cell_surface = 2*(cell_size_X*cell_size_Y +
                              cell_size_Y*cell_size_Z +
                              cell_size_Z*cell_size_X);
        
        System.out.println("calcCellSurface(): cell_surface = "+cell_surface);
    }
    
    /** The method returns volume of cell3D.
     * @return volume of cell3D
     */
    public double getCellVolume()
    {
        return cell_volume;
    }
    
    /** The method returns surface of cell3D (in case of hexagonal close packing).
     * @return surface of cell3D (in case of hexagonal close packing)
     */
    public double getCellSurface()
    {
        return cell_surface;
    } 
    
    /** Writing of data on state of cell to file.
     * @param bw stream for writing of data to file.
     * @param cellIndices triple index of cell.
     * @param cellState state of cell.
     */
    public void writeToFileSquares(BufferedWriter bw, Three cellIndices, byte cellState)
    {
        try
        {
            bw.write((cellIndices.getI()+0.50)+" "+(cellIndices.getJ()+0.50)+" "+cellState);
                
            bw.newLine();
                    
            bw.write((cellIndices.getI()+0.01)+" "+(cellIndices.getJ()+0.01)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.33)+" "+(cellIndices.getJ()+0.01)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.67)+" "+(cellIndices.getJ()+0.01)+" "+cellState);
            bw.newLine();                    
            bw.write((cellIndices.getI()+0.99)+" "+(cellIndices.getJ()+0.01)+" "+cellState);
            bw.newLine();
            
            bw.write((cellIndices.getI()+0.01)+" "+(cellIndices.getJ()+0.99)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.33)+" "+(cellIndices.getJ()+0.99)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.67)+" "+(cellIndices.getJ()+0.99)+" "+cellState);
            bw.newLine();                    
            bw.write((cellIndices.getI()+0.99)+" "+(cellIndices.getJ()+0.99)+" "+cellState);
            bw.newLine();
            
            bw.write((cellIndices.getI()+0.01)+" "+(cellIndices.getJ()+0.01)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.01)+" "+(cellIndices.getJ()+0.33)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.01)+" "+(cellIndices.getJ()+0.67)+" "+cellState);
            bw.newLine();                    
            bw.write((cellIndices.getI()+0.01)+" "+(cellIndices.getJ()+0.99)+" "+cellState);
            bw.newLine();
            
            bw.write((cellIndices.getI()+0.99)+" "+(cellIndices.getJ()+0.01)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.99)+" "+(cellIndices.getJ()+0.33)+" "+cellState);
            bw.newLine();
            bw.write((cellIndices.getI()+0.99)+" "+(cellIndices.getJ()+0.67)+" "+cellState);
            bw.newLine();                    
            bw.write((cellIndices.getI()+0.99)+" "+(cellIndices.getJ()+0.99)+" "+cellState);
            bw.newLine();
            
            bw.flush();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** Writing of data on state of cell to file.
     * @param bw stream for writing of data to file.
     * @param cellIndices triple index of cell.
     * @param cellState state of cell.
     */
    public void writeToFilePoints(BufferedWriter bw, Three cellIndices, int cellState)
    {
        // Cell radius expressed in micrometers
        double radius = 0.5;//cell_size_X/2;
            
        int index1 = cellIndices.getI();
        int index2 = cellIndices.getJ();
        int index3 = cellIndices.getK();

        // Calculation of coordinates of cell
        VectorR3 cell_coordinates = calcCoordinates(index1, index2, index3);

        double coord_X = cell_coordinates.getX();
        double coord_Y = cell_coordinates.getY();
        double coord_Z = cell_coordinates.getZ();

        // Determination of type of location of cell in grain: internal or boundary
        try
        {
            // Writing of cell coordinates and states
            bw.write(cellState+" "+(int)Math.round(coord_X*1000)/1000.0+" "+
                                   (int)Math.round(coord_Y*1000)/1000.0+" "+
                                   (int)Math.round(coord_Z*1000)/1000.0);
            
      //    bw.newLine();
            bw.flush();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }        
    }

    /** The method writes the information about portions of inner cells in each state
     * at current time step to the file.
     * @param step_counter number of current time step
     */
    public void createGraphStates(long step_counter)
    {
        int spec_size = size();

        // Cell state
        int state;

        int grain_type;

        // Variables for calculation of number of cells in each state
        // and total number of cells
        int elastic_cells_number = 0;
        int plastic_cells_number = 0;
        int damaged_cells_number = 0;
        int total_cells_number   = 0;
        
        double cur_time = 0;
        double elastic_cells_portion = 0;
        double plastic_cells_portion = 0;
        double damaged_cells_portion = 0;

        // Writing of information about portions of cells in each state
        // at current time step to the file
        for(int cell_counter=0; cell_counter<spec_size; cell_counter++)
        {
            cell = (RecCellR3)get(cell_counter);
            grain_type = cell.getGrainType();

            if((grain_type==Common.LAYER_CELL)|(grain_type==Common.INNER_CELL))
            {
                // Cell state
                state = cell.getState();

                if(state == Common.ELASTIC_CELL)
                    elastic_cells_number++;

                if(state == Common.PLASTIC_CELL)
                    plastic_cells_number++;

                if(state == Common.DAMAGED_CELL)
                    damaged_cells_number++;
            }
        }
        
        total_cells_number = elastic_cells_number + plastic_cells_number + damaged_cells_number;

        try
        {
            cur_time              = (long)Math.round(1.0E15*(step_counter+1)*time_step)/1.0E15;
            elastic_cells_portion = (long)Math.round(1.0E6*elastic_cells_number/total_cells_number)/1.0E6;
            plastic_cells_portion = (long)Math.round(1.0E6*plastic_cells_number/total_cells_number)/1.0E6;
            damaged_cells_portion = (long)Math.round(1.0E6*damaged_cells_number/total_cells_number)/1.0E6;
            
            bw1.write(cur_time+" "+elastic_cells_portion+" "+plastic_cells_portion+" "+damaged_cells_portion);

            bw1.newLine();
            bw1.flush();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }
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
        
        // Cell radius expressed in micrometers
        double radius = 0.5;//cell_size_X/2;
        
        // Calculation of coordinates of cell in case of HCP
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
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
        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
        {
            coord_X = radius*(1 + 2*index1);
            coord_Y = radius*(1 + 2*index2);
            coord_Z = radius*(1 + 2*index3);
        }
        
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

    /** The method simulates formation of cracks.     
     * @param writeFileName name of file for writing
     * @param writeToFile if true then data of simulation are written to file
     * @param step_counter number of time step
     */
    public void calcCrackFormation(String writeFileName, boolean writeToFile, long step_counter)
    {
        /* Block 14.1 */

        // Local variables
        int grain_index = -1;
        int neighb_cell_index;
        Three neighb_cell_indices;
        double boundary_temperature;
        double force_1, force_2;
        double displ=0;

        byte cell_grain_type   = -1;
        byte neighb_grain_type  = -1;
        int cell_grain_index   = -1;
        int neighb_grain_index = -1;

        int cell_number = elementSizeI*elementSizeJ*elementSizeK;

        // Influx of mechanical energy from cell to neighbour
        double influx = 0;

        // Array of influxes of mechanical energy from cells to neighbours:
        // 1st index is responsible for index of central cell, 
        // 2nd index - for number of neighbour at 1st coordination sphere 
        double[][] bound_mech_influxes = new double[cell_number][neighb1S_number];
        
        for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
            for(int neighb_counter = 0; neighb_counter<neighb1S_number; neighb_counter++)
                bound_mech_influxes[cell_counter][neighb_counter] = 0;
        
        // Array of total energy influxes from each cell to neighbours at 1st sphere
        double[] mech_influxes      = new double[cell_number];

        for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
            mech_influxes[cell_counter] = 0;
        
        boolean neighbour_loading, cell_loading,
                inner_cell, outer_interacted_cell,
                inner_neighbour, outer_interacted_neighbour;
        
        // Pointer of possibility of mechanical energy exchange
        boolean mech_energy_exchange = false;
        
        // Pointer of neighbour cell cracking
        boolean cracked_neighbour;
        
        // Pointer of current cell cracking
        boolean cell_is_damaged = false;

        // Moduli of elasticity of neighbour cells
        double mod_elast, neighb_mod_elast;

        int cell_bound_counter = 0;

        // Boundary displacement vector for SCP
        byte[] unit_bound_norm_vector = new byte[3];

        // Number of damaged cells
        int damaged_cells_number = 0;

        double generation_ratio, propagation_ratio;
        double aver_gen_ratio=0, aver_prop_ratio=0;
        int pot_embryos_number=0, pot_damages_number=0;
        double max_prop_ratio= 0;
        double max_gen_ratio = 0;
        
        // Variable responsible for presense of flows of mechanical energy 
        // from damaged cell to  neighbours
        boolean calc_mech_flows = false;// true;//
        
        // Variable responsible for location of cell from substrate at interface
        // between substrate and coating
        boolean cell_at_interface = false;

        // Variable responsible for location of cell from substrate at grain boundary
        boolean cell_at_grain_boundary = false;

        // Neighbour cell
        RecCellR3 neighb_cell;

        // Cell force moment
        VectorR3 cell_moment;

        // Neighbour cell force moment
        VectorR3 neighb_moment;

        // Resultant vector of force moment vectors
        VectorR3 res_moment;

        // Absolute value of resultant vector of force moment vectors
        double abs_res_moment=0;

        // Maximum absolute value of resultant vectors for 1st coordination sphere
        double max_abs_res_moment = 0;

    //    VectorR3 cell_coord;

        // START OF "CRACK SIMULATION" TIME STEPS
        for(int stepCounter = 0; stepCounter < crack_step_number; stepCounter++)
        {
            // TEST
            if(writeToFile)
                System.out.println("Crack propagation is simulated...");

            max_prop_ratio = 0;
            max_gen_ratio  = 0;

            // 1st spatial cycle on automata located in CA micro specimen:
            // determination of cells, which are switched to the state "crack" at current time step
            for(indexK = 0; indexK < elementSizeK; indexK++)
            for(indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for(indexI = 0; indexI < elementSizeI; indexI++)
            {
              // Calculation of single index (in ArrayList)
              // of the cell with current triple indices
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;

              // Obtaining of cell and its neighbours from list of cells
              cell = (RecCellR3)get(index);

              // Cell grain type
              cell_grain_type = cell.getGrainType();

              // Cell grain index
              cell_grain_index = cell.getGrainIndex();

           //   cell_coord = cell.getCoordinates();

              if((cell_grain_type == Common.INNER_CELL)|(cell_grain_type == Common.LAYER_CELL))
              if(!damaged_cells[index])
              {
                // Pointer of neighbour cell cracking
                cracked_neighbour = false;
                
                // Pointer of cell location at interface
                cell_at_interface = false;

                // Pointer of cell location at grain boundary
                cell_at_grain_boundary = false;

                // Search of cracked neighbour of current cell
                for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                {
                  neighb_cell_index = neighbours1S[index][neighbCounter];

                  if((neighb_cell_index>-1)&(neighb_cell_index<size()))
                  if(damaged_cells[neighb_cell_index])
                      cracked_neighbour = true;

                  neighb_cell = (RecCellR3)get(neighb_cell_index);

                  // Neighbour cell grain type
                  neighb_grain_type = neighb_cell.getGrainType();

                  // Neighbour cell grain index
                  neighb_grain_index = neighb_cell.getGrainIndex();

                  if(cell_grain_type == Common.INNER_CELL)
                  {
                      if(neighb_grain_type == Common.LAYER_CELL)
                          cell_at_interface = true;

                      if(neighb_grain_type == Common.INNER_CELL)
                      if(cell_grain_index != neighb_grain_index)
                          cell_at_grain_boundary = true;
                  }
                }

                // Crack can be generated if there is no damaged cells at 1st neighbour sphere
                if((cell_grain_type == Common.INNER_CELL)&cell_at_interface&cell_at_grain_boundary&(!cracked_neighbour))
                {
               //   generation_ratio = Math.abs(cell.getMechStress()/(cell.getUltimateStrain()*cell.get_mod_elast()));
               //   generation_ratio = (cell.calcAbsForceMoment()/getCellVolume())/(cell.getUltimateStrain()*cell.getModShear());
               //   generation_ratio = cell.calcAbsForceMoment();

                    // Cell force moment
                    cell_moment = cell.getForceMoment();

                    max_abs_res_moment = 0;// cell_moment.getLength();//

                    if(cell_moment.getLength()!=0)
                    for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                    {
                        neighb_cell_index = neighbours1S[index][neighbCounter];
                        neighb_cell = (RecCellR3)get(neighb_cell_index);

                        // Neighbour cell force moment
                        neighb_moment = neighb_cell.getForceMoment();

                        // Resultant vector of force moment vectors
                        res_moment = residial(cell_moment, neighb_moment);

                        // Absolute value of resultant vector of force moment vectors
                        abs_res_moment = res_moment.getLength();//neighb_moment.getLength();//
                 //     abs_res_moment = neighb_moment.getLength()/cell_moment.getLength();

                        if(abs_res_moment > max_abs_res_moment)
                            // Maximum absolute value of resultant vectors for 1st coordination sphere
                            max_abs_res_moment = abs_res_moment;
                    }                  

                    generation_ratio = (max_abs_res_moment/getCellVolume())/(cell.getUltimateStrain()*cell.getModShear());
                //    generation_ratio = max_abs_res_moment;

                    // Switch of cell state
                    if(generation_ratio>1e-12)
                //    if(true)
                    {
                        cell_is_damaged = true;
                        damaged_cells[index] = true;

                        // indexI = elementSizeI; indexJ = elementSizeJ; indexK = elementSizeK;
                    }

                    if(generation_ratio>max_gen_ratio)
                        max_gen_ratio = generation_ratio;
                    
                    aver_gen_ratio+=generation_ratio;
                    pot_embryos_number++;
                }

                // Crack can be propagated if there is damaged cell at 1st neighbour sphere
                if(step_counter>1000)
                if(cell_grain_type == Common.LAYER_CELL)
                if(cracked_neighbour)
                {
                //    propagation_ratio = (cell.calcAbsForceMoment()/getCellVolume())/(cell.getUltimateStrain()*cell.getModShear());

                    // Cell force moment
                    cell_moment = cell.getForceMoment();

                    max_abs_res_moment = 0;

                    if(cell_moment.getLength()!=0)
                    for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                    {
                        neighb_cell_index = neighbours1S[index][neighbCounter];
                        neighb_cell = (RecCellR3)get(neighb_cell_index);

                        // Neighbour cell force moment
                        neighb_moment = neighb_cell.getForceMoment();

                        // Resultant vector of force moment vectors
                        res_moment = residial(cell_moment, neighb_moment);

                        // Absolute value of resultant vector of force moment vectors
                        abs_res_moment = res_moment.getLength();

                    //    abs_res_moment = neighb_moment.getLength()/cell_moment.getLength();

                        if(abs_res_moment > max_abs_res_moment)
                            // Maximum absolute value of resultant vectors for 1st coordination sphere
                            max_abs_res_moment = abs_res_moment;
                    }

                    propagation_ratio = (max_abs_res_moment/getCellVolume())/(cell.getUltimateStrain()*cell.getModShear());
                //    propagation_ratio = max_abs_res_moment;

                    // Switch of cell state
                    if(propagation_ratio>1)
                    {
                        cell_is_damaged = true;
                        damaged_cells[index] = true;
                    }

                    if(propagation_ratio>max_prop_ratio)
                        max_prop_ratio = propagation_ratio;

                    aver_prop_ratio+=propagation_ratio;
                    pot_damages_number++;
                }
              }
            }

            // TEST            
            if(pot_embryos_number!=0)
            {
                aver_gen_ratio  = aver_gen_ratio/pot_embryos_number;
          //    System.out.println("pot_embryos_number: "+pot_embryos_number+"; aver_gen_ratio: "+aver_gen_ratio);
                System.out.println("pot_embryos_number: "+pot_embryos_number+"; max_gen_ratio: "+max_gen_ratio);
            }

         //   if(pot_damages_number!=0)
            {
                aver_prop_ratio = aver_prop_ratio/pot_damages_number;
          //    System.out.println("pot_damages_number: "+pot_damages_number+"; aver_prop_ratio: "+aver_prop_ratio);
                System.out.println("pot_damages_number: "+pot_damages_number+"; max_prop_ratio: "+max_prop_ratio);
            }

            // 2nd spatial cycle on automata located in CA micro specimen:
            // flows of mechanical energy from damaged cells are calculated.
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
              // Calculation of single index (in ArrayList)
              // of the cell with current triple indices
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;

              // Obtaining of cell and its neighbours from list of cells
              cell = (RecCellR3)get(index);

              mech_influxes[index] = 0;
              
              cell_grain_type = cell.getGrainType();

              if((cell_grain_type == Common.INNER_CELL)|(cell_grain_type == Common.LAYER_CELL))
              if(damaged_cells[index])
              {
                // Cell grain type
                cell_grain_type = cell.getGrainType();

                // Index of grain containing this cell
                grain_index = cell.getGrainIndex();

                // Single indices of neighbours at 1st coordination sphere
                neighbIndices = neighbours1S[index];

                // Cell temperature
                temperature = cell.getTemperature();

                // Obtaining of grain containing considered cell
                grain = (Cluster)grainsCluster.get(grain_index-1);

                // Obtaining of grain containing considered cell for consequence of time steps
                newGrain = (Cluster)newGrainsCluster.get(grain_index-1);

                // Pointer of neighbour cell cracking
                cracked_neighbour = false;

                // Calculation of velocities of mechanical energy flows to neighbour cells
                if(calc_mech_flows)
                for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
                {
                  neighb_cell_index = neighbours1S[index][neighbCounter];
                  bound_mech_influxes[index][neighbCounter] = 0;

                  if((neighb_cell_index>-1)&(neighb_cell_index<size()))
                  if(!damaged_cells[neighb_cell_index])
                  {
                    // Obtaining of neighbour cell in adjacent grain
                    neighbCell = (RecCellR3)get(neighb_cell_index);

                    // Obtaining of index of adjacent grain containing this neighbour cell
                    neighb_grain_index = neighbCell.getGrainIndex();

                    // Obtaining of type of neighbour cell
                    neighb_grain_type = neighbCell.getGrainType();

                    // Indices of neighbour cell
                    neighb_cell_indices = calcTripleIndex(neighb_cell_index, cellNumberI+2, cellNumberJ+2, cellNumberK+2);

                    if(((cell_grain_type == Common.INNER_CELL)|(cell_grain_type == Common.LAYER_CELL))&
                      ((neighb_grain_type == Common.INNER_CELL)|(neighb_grain_type == Common.LAYER_CELL)))
                    {
                      // Obtaining of grain containing neighbour cell
                      neighbGrain = (Cluster)grainsCluster.get(neighb_grain_index-1);

                      angle_diff =(Math.abs(grain.getAngle1() - neighbGrain.getAngle1())+
                                   Math.abs(grain.getAngle2() - neighbGrain.getAngle2())+
                                   Math.abs(grain.getAngle3() - neighbGrain.getAngle3()))/3;

                      // Obtaining of transition limit to high angle grain boundary regime
                      angleHAGB = grain.getAngleLimitHAGB();

                   //   if(inner_cell&inner_neighbour)
                      {
                        mod_elast = grain.getModElast();
                        neighb_mod_elast = neighbGrain.getModElast();

                        if((mod_elast == 0)|(neighb_mod_elast == 0))
                            mechMaxMobility = 0;
                        else
                        {
                          if(mod_elast == neighb_mod_elast)
                            mechMaxMobility = response_rate/mod_elast;
                          else
                            mechMaxMobility = (response_rate/Math.sqrt(mod_elast*neighb_mod_elast))*
                                      Math.exp(-(mod_elast-neighb_mod_elast)*(mod_elast-neighb_mod_elast)/(mod_elast*neighb_mod_elast));
                        }

                        if((angle_diff == 0)|(neighbCell.getGrainType()==Common.LAYER_CELL))
                        {
                          force_1 = neighbCell.getMechStress();
                          force_2 = cell.getMechStress();

                          // Calculation of pressure (motive force) on grain boundary
                          motive_force = force_2 - force_1;
                          specBoundEnergy = 0;
                          mobility_exponent = 0;
                          velocity = mechMaxMobility*motive_force;
                        }
                        else
                        if(angle_diff <= angleHAGB)
                        {
                          // Obtaining of specific energy of high angle grain boundary for neighbour cell
                          energyHAGB = grain.getEnergyHAGB();

                          //Calculation of specific energy of boundary between grains
                          specBoundEnergy = (energyHAGB*angle_diff/angleHAGB)*(1 - Math.log(angle_diff/angleHAGB));// 0;//

                          force_1 = neighbCell.getMechStress();
                          force_2 = cell.getMechStress();

                          // Calculation of pressure (motive force) on grain boundary
                          motive_force = force_2 - force_1;

                          boundary_temperature = (neighbCell.getTemperature()+temperature)/2;

                          if(boundary_temperature!=0)
                          {
                            mobility_exponent = -(getCellSurface()/neighb1S_number)*specBoundEnergy/(Task.BOLTZMANN_CONST*boundary_temperature);

                            // Calculation of velocity of grain boundary movement
                            velocity = mechMaxMobility*motive_force*Math.pow(Math.E, mobility_exponent);
                          }
                          else
                            velocity = 0;
                        }
                        else
                          velocity = 0;
                      }
                    }
                    else
                        velocity = 0;
                    
                    // Calculation of mech. energy influx from current central cell to current neighbour cell
                    influx = cell_volume*neighbCell.get_mod_elast()*velocity*time_step/cell_size_X;
                    bound_mech_influxes[index][neighbCounter] = influx;

                    // Calculation of total mech. energy influx from current central cell to its neighbour cells
                    mech_influxes[index]+= influx;
                //    mech_influxes[neighb_cell_index]+= influx;
                  }
                }

                damaged_cells_number++;
                total_damaged_cells_number++;
              }
            }
            
            // TEST
            System.out.println("Number of cells damaged at "+(step_counter+1)+" time step: "+damaged_cells_number);
            System.out.println("Total number of damaged cells: "+total_damaged_cells_number);
            System.out.println();
            
            double cell_mech_energy  = 0; // Mechanical energy of cell
            double neighb_old_energy = 0; // Old value of energy of neighbour cell
            double neighb_new_energy = 0; // New value of energy of neighbour cell
            double total_influx      = 0; // Total influx of energy to neighbour cells
            
            // 3rd spatial cycle over automata located in this micro specimen:
            // redetermination of values of mech. energy influxes from each cell
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
              total_influx = 0;

              // Calculation of single index of the cell with given indices.
              index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;

              // Obtaining of cell
              cell = (RecCellR3)newCells.get(index);
              cell_grain_type = cell.getGrainType();

              if((cell_grain_type == Common.INNER_CELL)|(cell_grain_type == Common.LAYER_CELL))
              if(damaged_cells[index])
              {
                  cell_mech_energy = cell.getMechEnergy();

                  if(cell.getGrainType()!=Common.INNER_CELL)
                  if(cell.getGrainType()!=Common.LAYER_CELL)
                      System.out.println("ERROR!!! Cell cannot be situated at specimen boundary!!!");

       //           System.out.println("Cell # "+index+"; type: "+cell.getGrainType()+"; old mech. energy: "+cell_mech_energy);

                  for(int neighb_counter = 0; neighb_counter<neighb1S_number; neighb_counter++)
                  {
                      neighb_cell_index = neighbours1S[index][neighb_counter];

                      if((neighb_cell_index>-1)&(neighb_cell_index<size()))
                      if(!damaged_cells[neighb_cell_index])
                      if(mech_influxes[index]!=0)
                      {
                          // Obtaining of neighbour cell in adjacent grain
                          neighbCell = (RecCellR3)newCells.get(neighb_cell_index);

                          // Redetermination of value of mech. energy influx from current central cell to this neighbour cell
                          bound_mech_influxes[index][neighb_counter] = bound_mech_influxes[index][neighb_counter]*cell_mech_energy/mech_influxes[index];
                          
                          neighb_old_energy = neighbCell.getMechEnergy();
                          neighb_new_energy = neighb_old_energy + bound_mech_influxes[index][neighb_counter];
                          cell_grain_type   = neighbCell.getGrainType();

                          if(false)
                          {
                              System.out.println("Neighbour cell # "+neighb_cell_index+"; type: "+cell_grain_type+"; old energy: "+neighb_old_energy+
                                                 "; influx: "+bound_mech_influxes[index][neighb_counter]+"; new energy: "+neighb_new_energy);

                              total_influx += bound_mech_influxes[index][neighb_counter];
                          }

                          // Addition of energy influx to total energy
                          neighbCell.setMechEnergy(neighb_new_energy);
                          neighbCell.calcMechStressAndStrain();
                      }
                  }

              //    System.out.println("total_influx = "+total_influx);
                  
                  cell.setGrainType(Common.ADIABATIC_ADIABATIC_CELL);
                  cell.setMechEnergy(0);
                  cell.calcMechStressAndStrain();
              }
            }
                          
            // Setting of zero elastic energies to grains
            for(int grain_counter = 0; grain_counter < grains.length; grain_counter++)
                grains[grain_counter].setElasticEnergy(0);
            
            // 4th spatial cycle: redetermination of cells
            // and calculation of grain energies
            for (indexK = 0; indexK < elementSizeK; indexK++)
            for (indexJ = 0; indexJ < elementSizeJ; indexJ++)
            for (indexI = 0; indexI < elementSizeI; indexI++)
            {
                index = indexI + cellNumberI*indexJ + cellNumberI*cellNumberJ*indexK;                
                set(index, (RecCellR3)newCells.get(index));
                
                cell = (RecCellR3)get(index);
                cell_grain_type = cell.getGrainType();

                if((cell_grain_type!=Common.ADIABATIC_ADIABATIC_CELL)
                  &(cell_grain_type!=Common.ADIABATIC_TEMPERATURE_CELL)
                  &(cell_grain_type!=Common.ADIABATIC_THERMAL_CELL))
                    grains[cell.getGrainIndex()-1].addElasticEnergy(cell.getMechEnergy());
            }

            Three cell_indices;
            int cell_index;

        //    if(writeToFile)
            try
            {
                // Exponent of power of 10 in standard representation
                // of total number of time steps
                int max_exponent = (int)Math.floor(Math.log10(step_number));

                // Exponent of power of 10 in standard representation
                // of number of current time step
                int exponent = 0;

                if(step_counter+1 > 0)
                    exponent     = (int)Math.floor(Math.log10(step_counter+1));

                // String of zeros
                String zeros = "";

                for(int zero_counter = exponent; zero_counter<max_exponent; zero_counter++)
                    zeros = zeros + "0";
                
                BufferedWriter bw_cracks = new BufferedWriter(new FileWriter(writeFileName+"_"+zeros+(step_counter+1)+"_cracks.txt"));

                // Printing of indices of crack embryos
                for(int cell_counter = 0; cell_counter<damaged_cells.length; cell_counter++)
                if(damaged_cells[cell_counter])
                {
                    cell_indices = calcTripleIndex(cell_counter, cellNumberI, cellNumberJ, cellNumberK);
                 //   cell_index = calcSingleIndex(cell_indices.getI()-1, cell_indices.getJ()-1, cell_indices.getK()-1);

                    bw_cracks.write((cell_indices.getI()-1)+" "+(cell_indices.getJ()-1)+" "+(cell_indices.getK()-1));
                    bw_cracks.newLine();
                    bw_cracks.flush();
                }
                
                bw_cracks.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error!!! "+io_exc);
            }
        }
        // FINISH OF "CRACK SIMULATION" TIME STEPS
    }
    
    /** The method simulates propagation of cracks according to gradients of force moments.
     * @return number of cells damaged at current time step
     */
    public int calcCrackPropagation()
    {
        // Force moments of current cell, neighbour cell and its resultant vector
        VectorR3 cell_moment, neighb_moment, res_moment;
        
        // Absolute value of force moment of neighbour cell 
        // and its maximum value for all neighbours
        double abs_res_moment, max_abs_res_moment = 0;
        
        // Index of neighbour cell
        int neighb_cell_index, cracked_neighb_cell_index = -1;
        
        // Neighbour cell
        RecCellR3 neighb_cell;
        
        // If propagation_ratio > crytical_ratio then cell is cracked
        double propagation_ratio;
        
        double crytical_ratio = 3.0E-3;// 1.0;// 
        
        damaged_cells = new boolean[size()];
        
        for(int cell_counter = 0; cell_counter < size(); cell_counter++)
            damaged_cells[cell_counter] = false;
        
        // 1st spatial cycle: indices of cells, which must be switched, are determined
        for(int cell_counter = 0; cell_counter < size(); cell_counter++)
        if(!damaged_cells[cell_counter])
        {
          cell = (RecCellR3)get(cell_counter);
          
          if(cell.getState() != Common.ADIABATIC_ADIABATIC_CELL)
          {
            cracked_neighb_cell_index = -1;
            cell_moment        = cell.getInstantForceMoment();
            max_abs_res_moment = 0;
            
            for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
            {
              neighb_cell_index = neighbours1S[cell_counter][neighbCounter];
              
              if(neighb_cell_index > -1)
              {
                neighb_cell       = (RecCellR3)get(neighb_cell_index);
              
                if(neighb_cell.getState() != Common.ADIABATIC_ADIABATIC_CELL)
                {
                  // Neighbour cell force moment
                  neighb_moment  = neighb_cell.getInstantForceMoment();
                
                  // Resultant vector of force moment vectors
                  res_moment     = residial(cell_moment, neighb_moment);
                
                  // Absolute value of resultant vector of force moment vectors
                  abs_res_moment = res_moment.getLength();
                
                  // Maximum absolute value of resultant vectors for 1st coordination sphere
                  if(abs_res_moment > max_abs_res_moment)
                  {
                    max_abs_res_moment        = abs_res_moment;
                    cracked_neighb_cell_index = neighb_cell_index;
                  }
                }
              }
            }
            
            if(cracked_neighb_cell_index > -1)
            {
              propagation_ratio = max_abs_res_moment/(cell.getUltimateStrain()*cell.getModShear());

              // Switch of cell state
              if(propagation_ratio > crytical_ratio)
                damaged_cells[cell_counter] = true;
              
              neighb_cell       = (RecCellR3)get(cracked_neighb_cell_index);
              propagation_ratio = max_abs_res_moment/(neighb_cell.getUltimateStrain()*neighb_cell.getModShear());
            
              // Switch of neighbour cell state
              if(propagation_ratio > crytical_ratio)
                damaged_cells[cracked_neighb_cell_index] = true;
            }            
          }
        }
        
        // 2nd spatial cycle: cells are divided by cracks from neighbours
        for(int cell_counter = 0; cell_counter < size(); cell_counter++)
        if(damaged_cells[cell_counter])
        {
          for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
          {
            neighb_cell_index = neighbours1S[cell_counter][neighbCounter];
            
            if(neighb_cell_index > -1)
            if(damaged_cells[neighb_cell_index])
            {
                neighbours1S[cell_counter][neighbCounter] = -3;
            }
          }
        }        
        
        // Counter of damaged cells
        int dam_cell_counter = 0;
        boolean damaged_cell = false;
        
        // 3rd spatial cycle: cell is switched if it is divided by cracks from all neighbours
        for(int cell_counter = 0; cell_counter < size(); cell_counter++)
        {
          damaged_cell = true;
          
          // If all neighbours are divided from the cell then this cell is damaged
          for(int neighbCounter = 0; neighbCounter < neighb1S_number; neighbCounter++)
          if(neighbours1S[cell_counter][neighbCounter] > -1)
          {
            damaged_cell = false;
            neighbCounter = neighb1S_number;
          }
          
          if(damaged_cell)
          {
            cell = (RecCellR3)get(cell_counter);
            cell.setState(Common.ADIABATIC_ADIABATIC_CELL);
            dam_cell_counter++;
          }
        }
        
        /*
        // 2nd spatial cycle: cells are switched
        for(int cell_counter = 0; cell_counter < size(); cell_counter++)
        if(damaged_cells[cell_counter])
        {
            cell = new RecCellR3();
            cell.setState(Common.ADIABATIC_ADIABATIC_CELL);
            set(cell_counter, cell);
            dam_cell_counter++;
        }
        */
        
        return dam_cell_counter;
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
        
        for(int neighb_counter = 0; neighb_counter<vectors.length; neighb_counter++)
        {
            coord_X = coord_X + vectors[neighb_counter].getX()*gradients[neighb_counter];
            coord_Y = coord_Y + vectors[neighb_counter].getY()*gradients[neighb_counter];
            coord_Z = coord_Z + vectors[neighb_counter].getZ()*gradients[neighb_counter];
        }
        
        result_vector.setX(coord_X/Math.pow(vectors[0].getLength(), 2));
        result_vector.setY(coord_Y/Math.pow(vectors[0].getLength(), 2));
        result_vector.setZ(coord_Z/Math.pow(vectors[0].getLength(), 2));
        
        return result_vector;
    }    
    
    /** Calculation of components of cell strain tensor and writing to file
     * @param cell_index cell index
     * @param bw_vec stream for writing of data
     */
    private void calcAndWriteStrainTensorComponents(int cell_index, BufferedWriter bw_vec)
    {
        boolean strain_tensor_exists = true;

        RecCellR3 neighb_cell;
        int neighb_index;
        
        // Current cell
        cell = (RecCellR3)get(cell_index);
        
        // Type of grain containing considered cell
        byte grain_type = cell.getGrainType();
        
        // Triple index of current cell
        cell_indices = calcTripleIndex(cell_index, cellNumberI, cellNumberJ, cellNumberK);

        int cell_index_I = cell_indices.getI();
        int cell_index_J = cell_indices.getJ();
        int cell_index_K = cell_indices.getK();

        // Coordinates of current cell
    //    VectorR3 cell_coord   = cell.getCoordinates();
        
        // Triple index of current neighbour cell
        Three neighb_indices;

        // Coordinates of displacement vector
     //   VectorR3 displ_vector = cell.getDisplVector();
                
        // The strain tensor of cell
        DoubleMatrix strain_tensor = cell.getStrainTensor();
        
        // Displacement vectors of neighbour cells
        VectorR3 left_cell_displ = new VectorR3();
        VectorR3 right_cell_displ = new VectorR3();
        VectorR3 front_cell_displ = new VectorR3();
        VectorR3 back_cell_displ = new VectorR3();
        VectorR3 top_cell_displ = new VectorR3();
        VectorR3 bottom_cell_displ = new VectorR3();
        
        // Strain tensor components
        double strainXX=0, strainXY=0, strainXZ=0;
        double strainYX=0, strainYY=0, strainYZ=0;
        double strainZX=0, strainZY=0, strainZZ=0;

        // Shear components
        double shearXY = 0, shearYZ = 0, shearZX = 0;
        
        // Average strain
        double aver_strain = 0;
        
        // Indices of neighbour cells
     //   int left_index = 0;
     //   int right_index = 0;
     //   int front_index = 0;
     //   int back_index = 0;
     //   int top_index = 0;
     //   int bottom_index = 0;

        // Determination of displacement vectors of neighbour cells at 1st coordination sphere
        for(int neighb_counter = 0; neighb_counter<neighb1S_number; neighb_counter++)
        {
            neighb_index = neighbours3D[cell_index][neighb_counter];

            if(neighb_index > -1)
            {
                neighb_cell  = (RecCellR3)get(neighb_index);

                neighb_indices = calcTripleIndex(neighb_index, cellNumberI, cellNumberJ, cellNumberK);
                
                if(neighb_indices.getI() == cell_index_I-1)
                {
                    left_cell_displ = neighb_cell.getDisplVector();
                //    left_index = neighb_indices.getI();
                }

                if(neighb_indices.getI() == cell_index_I+1)
                {
                    right_cell_displ = neighb_cell.getDisplVector();
                  //  right_index = neighb_indices.getI();
                }

                if(neighb_indices.getJ() == cell_index_J-1)
                {
                    front_cell_displ = neighb_cell.getDisplVector();
              //      front_index = neighb_indices.getJ();
                }

                if(neighb_indices.getJ() == cell_index_J+1)
                {
                    back_cell_displ = neighb_cell.getDisplVector();
                  //  back_index = neighb_indices.getJ();
                }

                if(neighb_indices.getK() == cell_index_K-1)
                {
                    top_cell_displ = neighb_cell.getDisplVector();
                  //  top_index = neighb_indices.getK();
                }

                if(neighb_indices.getK() == cell_index_K+1)
                {
                    bottom_cell_displ = neighb_cell.getDisplVector();
                  //  bottom_index = neighb_indices.getK();
                }
            }
            else
            {
                strain_tensor_exists = false;
            }
        }
        
        if(strain_tensor_exists)
        try
        {
          // Test condition
          if(grain_type != Common.ADIABATIC_TEMPERATURE_CELL &
             grain_type != Common.ADIABATIC_THERMAL_CELL &
             grain_type != Common.ADIABATIC_ADIABATIC_CELL)
          {
            // Calculation of strain tensor components
            strainXX = (right_cell_displ.getX() - left_cell_displ.getX())/(2*cell_size_X);
            strainYY = (back_cell_displ.getY() - front_cell_displ.getY())/(2*cell_size_Y);
            strainZZ = (bottom_cell_displ.getZ() - top_cell_displ.getZ())/(2*cell_size_Y);

            strainXY = 0.5*((back_cell_displ.getX() - front_cell_displ.getX())/(2*cell_size_Y) +
                            (right_cell_displ.getY() - left_cell_displ.getY())/(2*cell_size_X));

            strainYZ = 0.5*((bottom_cell_displ.getY() - top_cell_displ.getY())/(2*cell_size_Z) +
                            (back_cell_displ.getZ() - front_cell_displ.getZ())/(2*cell_size_Y));

            strainZX = 0.5*((right_cell_displ.getZ() - left_cell_displ.getZ())/(2*cell_size_X) +
                            (bottom_cell_displ.getX() - top_cell_displ.getX())/(2*cell_size_Z));

            strainYX = strainXY;
            strainZY = strainYZ;
            strainXZ = strainZX;
            
            aver_strain = (strainXX+strainYY+strainZZ)/3;

            // Calculation of shear components
            shearXY = 0.5*((back_cell_displ.getX() - front_cell_displ.getX())/(2*cell_size_Y) -
                           (right_cell_displ.getY() - left_cell_displ.getY())/(2*cell_size_X));

            shearYZ = 0.5*((bottom_cell_displ.getY() - top_cell_displ.getY())/(2*cell_size_Z) -
                           (back_cell_displ.getZ() - front_cell_displ.getZ())/(2*cell_size_Y));

            shearZX = 0.5*((right_cell_displ.getZ() - left_cell_displ.getZ())/(2*cell_size_X) -
                           (bottom_cell_displ.getX() - top_cell_displ.getX())/(2*cell_size_Z));
          }
          else
          {
            strainXX = 0;
            strainYY = 0;
            strainZZ = 0;
            strainXY = 0;
            strainYZ = 0;
            strainZX = 0;            
            strainYX = 0;
            strainZY = 0;
            strainXZ = 0;
            
            aver_strain = 0;

            // Calculation of shear components
            shearXY = 0;
            shearYZ = 0;
            shearZX = 0;
          }
          
          strain_tensor.setElement(0, 0, strainXX);
          strain_tensor.setElement(0, 1, strainXY);
          strain_tensor.setElement(0, 2, strainXZ);
          strain_tensor.setElement(1, 0, strainYX);
          strain_tensor.setElement(1, 1, strainYY);
          strain_tensor.setElement(1, 2, strainYZ);
          strain_tensor.setElement(2, 0, strainZX);
          strain_tensor.setElement(2, 1, strainZY);
          strain_tensor.setElement(2, 2, strainZZ);
            
          cell.setStrainTensor(strain_tensor);            

          //  bw_vec.write(cell_index+" "+cell_index_I+" "+cell_index_J+" "+cell_index_K+" ");
          //  bw_vec.write(left_index+" "+right_index+" "+front_index+" "+back_index+" "+top_index+" "+bottom_index+" ");
          bw_vec.write(/*cell.getGrainIndex()+" "+*/cell_index_I+" "+cell_index_J+" "+cell_index_K+" "+cell_coord.getX()+" "+cell_coord.getY()+" "+cell_coord.getZ()+" ");
          bw_vec.write(displ_vector.getX()+" "+displ_vector.getY()+" "+displ_vector.getZ()+" ");
          bw_vec.write(strainXX+" "+strainYY+" "+strainZZ+" "+strainXY+" "+strainYZ+" "+strainZX+" ");
          bw_vec.write(shearXY+" "+shearYZ+" "+shearZX+" "+aver_strain);
            
          bw_vec.newLine();
          bw_vec.flush();
        }
        catch(IOException io_exc)
        {
            System.out.println("Exception: "+io_exc);
        }
    }
    
    /** Writing of components of cell stress tensor to file
     * @param cell_index cell index
     * @param bw_vec stream for writing of data
     */
    private void writeStressTensorComponents(int cell_index, BufferedWriter bw_vec)
    {
        // Current cell
        cell = (RecCellR3)get(cell_index);
        
        // Triple index of current cell
        cell_indices = calcTripleIndex(cell_index, cellNumberI, cellNumberJ, cellNumberK);

        int cell_index_I = cell_indices.getI();
        int cell_index_J = cell_indices.getJ();
        int cell_index_K = cell_indices.getK();
        
        // Coordinates of current cell
      //  VectorR3 cell_coord   = cell.getCoordinates();
        
        // The strain tensor of cell
        DoubleMatrix stress_tensor = new DoubleMatrix(3, 3);
        
        // Stress tensor components
        double stressXX=0, stressXY=0, stressXZ=0;
        double stressYX=0, stressYY=0, stressYZ=0;
        double stressZX=0, stressZY=0, stressZZ=0;

        // Moment components
        double mom_X = 0, mom_Y = 0, mom_Z = 0;
        
        // Average stress of cell
        double aver_stress = 0;
        
        // Second invariant of deviator of stress tensor
        double second_invar = 0;
        
        // Mises stress of cell
        double mises_stress = 0;
        
        try
        {
            stress_tensor = cell.getStressTensor();
            
            stressXX = stress_tensor.getElement(0, 0);
            stressXY = stress_tensor.getElement(0, 1);
            stressXZ = stress_tensor.getElement(0, 2);
            
            stressYX = stress_tensor.getElement(1, 0);
            stressYY = stress_tensor.getElement(1, 1);
            stressYZ = stress_tensor.getElement(1, 2);
            
            stressZX = stress_tensor.getElement(2, 0);
            stressZY = stress_tensor.getElement(2, 1);
            stressZZ = stress_tensor.getElement(2, 2);            
            
            second_invar = ((stressXX-stressYY)*(stressXX-stressYY) + (stressZZ-stressYY)*(stressZZ-stressYY) + (stressZZ-stressXX)*(stressZZ-stressXX))/6 + 
                           ((stressYZ+stressZY)*(stressYZ+stressZY) + (stressZX+stressXZ)*(stressZX+stressXZ) + (stressXY+stressYX)*(stressXY+stressYX))/4;
            
            mises_stress = Math.sqrt(3*second_invar);
            
            mom_X    = cell.getMomentX();
            mom_Y    = cell.getMomentY();
            mom_Z    = cell.getMomentZ();
            
            aver_stress = cell.getAverageStress();
            
            bw_vec.write(cell_index_I+" "+cell_index_J+" "+cell_index_K+" ");
            bw_vec.write(cell_coord.getX()+" "+cell_coord.getY()+" "+cell_coord.getZ()+" ");
            bw_vec.write(stressXX+" "+stressXY+" "+stressXZ+" "+stressYX+" "+stressYY+" "+stressYZ+" "+stressZX+" "+stressZY+" "+stressZZ+" ");
            bw_vec.write(mom_X+" "+mom_Y+" "+mom_Z+" "+aver_stress+" "+mises_stress);
            
            bw_vec.newLine();
            bw_vec.flush();
        }
        catch(IOException io_exc)
        {
            System.out.println("Exception: "+io_exc);
        }
    }  
   
    /** Vectors from "cell1S" to "central" cell
     * @return vectors from "cell1S" to "central" cell
     */
    public VectorR3[] getCell1SVectors()
    {
        return cell1S_vectors;
    }
    
    /** The method returns cosinus of angle between two vectors.
     * @param vect_1 1st vector
     * @param vect_2 2nd vector
     * @return cosinus of angle between two vectors
     */
     private double cosinus(VectorR3 vect_1, VectorR3 vect_2)
     {            
         return scalarProduct(vect_1, vect_2)/
         Math.sqrt(scalarProduct(vect_1, vect_1)*scalarProduct(vect_2, vect_2));
     }
     
     /** The method returns scalar product of two vectors.
      * @param vect_1 1st vector
      * @param vect_2 2nd vector
      * @return scalar product of two vectors
      */
     private double scalarProduct(VectorR3 vect_1, VectorR3 vect_2)
     {            
        return vect_1.getX()*vect_2.getX() + 
               vect_1.getY()*vect_2.getY() + 
               vect_1.getZ()*vect_2.getZ(); 
     }
     
    /** The method sorts list of toposes according to starting points and
     * deletes repeated elements from it.
     */
    /*
    private void sortBoundNormToposes()
    {
        int sorted_list_size = bound_norm_toposes.size();

        PointR3 bound_coord_1, bound_coord_2;
        ToposR3 topos_1, topos_2;

        int repl_counter = 1;

        // Sorting of elements
        while(repl_counter>0)
        {
            repl_counter = 0;

            for (int bound_counter = 0; bound_counter<sorted_list_size-1; bound_counter++)
            {
                topos_1 = (ToposR3)bound_norm_toposes.get(bound_counter  );
                topos_2 = (ToposR3)bound_norm_toposes.get(bound_counter+1);

                bound_coord_1 = topos_1.getStartPointR3();
                bound_coord_2 = topos_2.getStartPointR3();

                // Sorting of pair of elements
                if(bound_coord_1.moreThan(bound_coord_2))
                {
                    bound_norm_toposes.set(bound_counter, topos_2);
                    bound_norm_toposes.set(bound_counter+1, topos_1);

                    repl_counter++;
                }

                // Deleting of repeated element
                if(bound_coord_1.equals(bound_coord_2))
                {
                    bound_norm_toposes.remove(topos_2);
                    sorted_list_size--;
                    repl_counter++;
                }
            }
        }
    }
    */
     
    /** The method returns variable responsible for account of anisotropy of simulated medium.
     * @return variable responsible for account of anisotropy of simulated medium
     */
    public boolean getAnisPresence()
    {
        return anisotropy;
    }
    
    /** The method returns coefficient of anisotropy of simulated medium.
     * @return coefficient of anisotropy of simulated medium
     */
    public double getAnisCoeff()
    {
        return coeff_anisotropy;
    }
    
    /** The method returns vector of anisotropy of simulated medium.
     * @return vector of anisotropy of simulated medium
     */
    public VectorR3 getAnisVector()
    {
        return anis_vector;
    }
    
    /** The method sets logical variable responsible for account of anisotropy of simulated medium.
     * @param _anisotropy logical variable responsible for account of anisotropy of simulated medium
     */
    public void setAnisPresence(boolean _anisotropy)
    {
        anisotropy = _anisotropy;
    }
        
    /** The method sets vector of anisotropy of simulated medium.
     * @param _anis_vector vector of anisotropy of simulated medium
     */
    public void setAnisVector(VectorR3 _anis_vector)
    {
        anis_vector = _anis_vector;
    }
    
    /** The method sets coefficient of anisotropy of simulated medium.
     * @param _coeff_anisotropy coefficient of anisotropy of simulated medium
     */
    public void setAnisCoeff(double _coeff_anisotropy)
    {
        coeff_anisotropy = _coeff_anisotropy;
    }    
    
    /** The method returns the value of the function for calculation of 
     * the parameter of boundary cell at current time step.
     * @param step_counter current time step
     * @param function_type time function of boundary parameter
     * @return the value of the function for calculation of 
     *         the parameter of boundary cell at current time step
     */
    private double bound_time_function(long step_counter, String function_type)
    {
        //   The parameter is equal to the ratio of current boundary parameter to 
        // input boundary parameter.
        double bound_param = 0;
        
        // Uniform loading 
        boolean simple_loading = false;
        
        if(function_type.equals(Common.CONSTANT_LOADING))
            simple_loading     = true;
        
        // Loading in chosen time periods
        boolean cycle_loading  = false;
        
        if(function_type.equals(Common.CYCLE_LOADING))
            cycle_loading      = true;
        
        // Loading according to periodic time function
        boolean periodic_loading  = false;
        double time_period = load_period;//1000*time_step;
        
        if(function_type.equals(Common.PERIODIC_LOADING))
            periodic_loading  = true;
          
        // Uniform loading 
        if(simple_loading)
            bound_param = 1;
        
        // Loading in chosen time periods
        if(cycle_loading)
        {
            // Period of loading absence (in time steps) for cyclic conditions
            no_load_period = step_number - load_period;
            
            if((step_counter-1)%(load_period + no_load_period) <= load_period)
                bound_param = 1;
            else
                bound_param = 0;// -1;// 
        }
        
        // Loading according to periodic time function
        if(periodic_loading)
            bound_param = 0.5+0.5*Math.sin(step_counter*time_step*2*Math.PI/time_period);
        
        return bound_param;
    }
    
    /** The method returns the value of the function for calculation of 
     * the parameter of boundary cell at current time step.
     * @param step_counter current time step
     * @param bound_time_function_type type of time function of boundary parameter
     * @param bound_load_time_portion portion of time period when boundary cells are loaded
     * @param bound_relax_time_portion portion of time period when boundary cells are relaxed
     * @return the value of the function for calculation of 
     *         the parameter of boundary cell at current time step
     */
    private double bound_time_function(long step_counter, byte bound_time_function_type, double bound_load_time_portion, double bound_relax_time_portion)
    {
        //   The parameter is equal to the ratio of current boundary parameter to 
        // input boundary parameter.
        double bound_param = 0;
        
        // Uniform loading 
        boolean simple_loading    = false;
        
        // Cycle loading: constant loading during first time period, 
        // no loading during second period
        boolean cycle_loading     = false;
        
        // Cycle loading: constant loading during first time period, 
        // constant loading with opposite sign during second period
        boolean cycle_loading_2   = false;
        
        // Loading according to periodic time function
        boolean periodic_loading  = false;
        
        if(bound_time_function_type == Common.CONSTANT_LOADING_BYTE_VALUE)// 0)// 
            simple_loading     = true;
        
        if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE)// 1)// 
            cycle_loading      = true;
        
        if(bound_time_function_type == Common.CYCLE_LOADING_BYTE_VALUE_2)// 3)// 
            cycle_loading_2    = true;
        
        if(bound_time_function_type == Common.PERIODIC_LOADING_BYTE_VALUE)// 2)// 
            periodic_loading   = true;
          
        if(!simple_loading & !cycle_loading & !periodic_loading & !cycle_loading_2)
            System.out.println("ERROR!!! Type of boundary function is not determined!");
        
        // Uniform loading 
        if(simple_loading)
            bound_param = 1;
        
        // Cycle loading: constant loading during first time period, 
        // no loading during second period
        if(cycle_loading)
        {
            load_period    = (long)Math.round((step_number-1)*bound_load_time_portion);
            
            // Period of loading absence (in time steps) for cyclic conditions
            no_load_period = (long)Math.round((step_number-1)*bound_relax_time_portion);
            
            if((step_counter-1)%(load_period + no_load_period) < load_period)
                bound_param = 1;
            else
                bound_param = 0;// -1;// 
        }
        
        // Cycle loading: constant loading during first time period, 
        // constant loading with opposite sign during second period
        if(cycle_loading_2)
        {
            load_period    = (long)Math.round((step_number-1)*bound_load_time_portion);
            
            // Period of loading absence (in time steps) for cyclic conditions
            no_load_period = (long)Math.round((step_number-1)*bound_relax_time_portion);
            
            if((step_counter-1)%(load_period + no_load_period) < load_period)
                bound_param = 1;
            else
                bound_param = 0;// -1;// 
        }
        
        // Loading according to periodic time function
        if(periodic_loading)
        {
            long load_time_period  = (long)Math.round((step_number-1)*bound_load_time_portion);
            
            // Period of loading absence (in time steps) for cyclic conditions
            long relax_time_period = (long)Math.round((step_number-1)*bound_relax_time_portion);
            
            long period_step_counter = (step_counter-1)%(load_time_period + relax_time_period);
            
            if(period_step_counter <= load_time_period)
                bound_param = Math.sin(period_step_counter*Math.PI/load_time_period);
            else
                bound_param = Math.sin(Math.PI + (period_step_counter - load_time_period)*Math.PI/relax_time_period);
            
       //     double time_period = step_number*bound_load_time_portion*time_step;
       //     bound_param = 0.5+0.5*Math.sin(step_counter*time_step*2*Math.PI/time_period);
        }
        
        return bound_param;
    }    
    
    /** The method generates series of random values
     * @param average average value for distribution of stochastic values
     * @param dispersion dispersion for distribution of stochastic values
     * @param rand_number number of random values in distribution
     */
    private double[] createStochasticSeries1(double min_value, double max_value, double dispersion, int rand_number)
    {
        boolean no_error = true;
        
        if(max_value < min_value)
        {
            System.out.println("ERROR!!! The maximal value cannot be less than the minimal value!!!\nmax_value = "+max_value+"; min_value = "+min_value);
            no_error = false;
        }
        
        double real_average   = (max_value + min_value)/2;
        double max_dispersion = Math.abs(real_average - min_value);
        
        if(dispersion < 0)
        {
            dispersion = max_dispersion*Math.random();
        }
        
        if(no_error & dispersion > max_dispersion)
        {
            System.out.println("ERROR!!! The value of mean square deviation must be <= "+max_dispersion+"; but mean square deviation = "+dispersion);
            no_error = false;
        }
        
        double random;        
        boolean finish_task = false;
        
        double[] stoch_values = new double[rand_number+2];
        
        if(max_value == min_value)
        {
            for(int rand_counter = 0; rand_counter < rand_number; rand_counter++)
              stoch_values[rand_counter] = max_value;
            
            stoch_values[rand_number]   = max_value;
            stoch_values[rand_number+1] = 0.0;
            
            finish_task = true;
        }
        else
        {
            for(int rand_counter = 0; rand_counter < rand_number+2; rand_counter++)
              stoch_values[rand_counter] = 0;
            
            finish_task = false;
        }
        
        double calc_average = 0;
        double new_calc_average = 0;
        double calc_dispersion = 0;
        
        double[] dispersion_values;
        
        double epsilon_1 = 5.0E-4;
        double epsilon_2 = 5.0E-4;
        
    //    System.out.println("Series of stochastic values: ");
        
        if(no_error & !finish_task)
        {
          for(int rand_counter = 0; rand_counter < rand_number; rand_counter++)
          {
            random = (Math.random()+Math.random()+Math.random()+Math.random()+
                      Math.random()+Math.random())/6;
                    
            stoch_values[rand_counter] = min_value + (max_value - min_value)*random;
            calc_average  += stoch_values[rand_counter];
            
       //     System.out.println(stoch_values[rand_counter]+" ");
          }
        
          calc_average = calc_average/rand_number;
        
          double coeff;
          int step_number = 1000;
          finish_task = false;
          
          for(int step_counter = 1; step_counter <= step_number; step_counter++)
          {
         //   System.out.println("step_counter = "+step_counter+".\n");
            
            coeff = (real_average - min_value)/(calc_average - min_value);
        
      //      System.out.println("Demanded average:   "+real_average);
      //      System.out.println("Calculated average: "+calc_average);
      //      System.out.println();
      //      System.out.println("The same series of stochastic values multiplied by "+coeff+":");
        
            calc_average = 0;
            calc_dispersion = 0;
          
            dispersion_values = new double[rand_number];
        
            for(int rand_counter = 0; rand_counter < rand_number; rand_counter++)
            {
              stoch_values[rand_counter] = min_value + (stoch_values[rand_counter] - min_value)*coeff;
              stoch_values[rand_counter] = Math.min(stoch_values[rand_counter], max_value);
              stoch_values[rand_counter] = Math.max(stoch_values[rand_counter], min_value);
              
              dispersion_values[rand_counter] = Math.pow(stoch_values[rand_counter] - real_average, 2);
            
       //       System.out.println("Stoch.value: "+stoch_values[rand_counter]+"; sqrt(dispersion): "+Math.sqrt(dispersion_values[rand_counter]));
            
              calc_average    += stoch_values[rand_counter];
              calc_dispersion += dispersion_values[rand_counter];
            }
        
            calc_average = calc_average/rand_number;        
            calc_dispersion = Math.sqrt(calc_dispersion/rand_number);
            
            if(Math.abs(calc_average - real_average) < epsilon_1 & Math.abs(calc_dispersion - dispersion) < epsilon_2 & !finish_task)
            {
                System.out.println("Number of steps of the algorithm: "+step_counter);
                step_counter = step_number;
                
             //   for(int rand_counter = 0; rand_counter < rand_number; rand_counter++)
               //   System.out.println("Stochastic value # "+rand_counter+": "+stoch_values[rand_counter]+"; square deviation: "+dispersion_values[rand_counter]);
                
                stoch_values[rand_number]   = calc_average;
                stoch_values[rand_number+1] = calc_dispersion;
                
                System.out.println("Demanded average:   "+real_average+"; demanded mean square deviation:   "+dispersion);
                System.out.println("Calculated average: "+calc_average+"; calculated mean square deviation: "+calc_dispersion);
                
                finish_task = true;
            }
            else
            if(!finish_task)
            {
           //   System.out.println("------===========---------===========-----------");
              coeff = Math.pow(dispersion/calc_dispersion, 2);
              new_calc_average = 0;
          
              for(int rand_counter = 0; rand_counter < rand_number; rand_counter++)
              {
                dispersion_values[rand_counter] = dispersion_values[rand_counter]*coeff;
                stoch_values[rand_counter] = real_average + Math.signum(stoch_values[rand_counter] - calc_average)*Math.sqrt(dispersion_values[rand_counter]);
                
                stoch_values[rand_counter] = Math.min(stoch_values[rand_counter], max_value);
                stoch_values[rand_counter] = Math.max(stoch_values[rand_counter], min_value);
                
                if(stoch_values[rand_counter] == min_value | stoch_values[rand_counter] == max_value)
                {
                    dispersion_values[rand_counter] = dispersion_values[rand_counter]*0.9;
                    stoch_values[rand_counter] = real_average + Math.signum(stoch_values[rand_counter] - calc_average)*Math.sqrt(dispersion_values[rand_counter]);
                }
                
                dispersion_values[rand_counter] = Math.pow(stoch_values[rand_counter] - real_average, 2);
            
                new_calc_average+= stoch_values[rand_counter];
                calc_dispersion += dispersion_values[rand_counter];
              }
          
              calc_average = new_calc_average/rand_number;        
              calc_dispersion = Math.sqrt(calc_dispersion/rand_number);
              
              if(Math.abs(calc_average - real_average) < epsilon_1 & Math.abs(calc_dispersion - dispersion) < epsilon_2)
              {
                System.out.println("Number of steps of the algorithm: "+step_counter);
                step_counter = step_number;
                
           //     for(int rand_counter = 0; rand_counter < rand_number; rand_counter++)
              //    System.out.println("Stochastic value # "+rand_counter+": "+stoch_values[rand_counter]+"; square deviation: "+dispersion_values[rand_counter]);
                
                stoch_values[rand_number]   = calc_average;
                stoch_values[rand_number+1] = calc_dispersion;
                
                System.out.println("Demanded average:   "+real_average+"; demanded mean square deviation:   "+dispersion);
                System.out.println("Calculated average: "+calc_average+"; calculated mean square deviation: "+calc_dispersion);
                
                finish_task = true;
              }
            }
          }
        }
        
        if(false)
        {
          calc_average = 0;
          calc_dispersion = 0;
        
          dispersion_values= new double[rand_number];
        
          for(int rand_counter = 0; rand_counter < rand_number; rand_counter++)
          {
            random = 2*(Math.random()+Math.random()+Math.random())/3;
                    
            dispersion_values[rand_counter] = dispersion + (real_average - min_value - dispersion)*Math.abs(random - 1.0)*Math.signum(rand_counter%2 - 0.5);
            stoch_values[rand_counter] = real_average + dispersion_values[rand_counter]*Math.signum(rand_counter%2 - 0.5);// *Math.sqrt(rand_number) // *Math.signum(rand_counter%2 - 0.5)
                    
            calc_average  += stoch_values[rand_counter];
            System.out.println(stoch_values[rand_counter]+" ");
            
            calc_dispersion += Math.pow(stoch_values[rand_counter] - real_average, 2);
          }
        
          calc_average = calc_average/rand_number;
          System.out.println("Demanded average:   "+real_average);
          System.out.println("Calculated average: "+calc_average);
        
          calc_dispersion = Math.sqrt(calc_dispersion/rand_number);
          System.out.println("Demanded mean square deviation:   "+dispersion);
          System.out.println("Calculated mean square deviation: "+calc_dispersion);
        }
        
        if(!finish_task)
        {
            stoch_values = new double[rand_number+2];
            
            for(int rand_counter = 0; rand_counter < rand_number+2; rand_counter++)
                stoch_values[rand_counter] = 0;
            
            System.out.println("Please, change input parameters!");
        }
        
     //   System.out.println("FINISH!!!\n");
        return stoch_values;
    }
    
    /** The method calculates portions of inclusion particles for current inner cell
     * and its inner neighbours at 1st coordination sphere.
     * @param cell_counter index of current inner cell
     * @param old_grain_number number of old grains
     */
    private void calcParticleVolumeFraction1S(int cell_counter, int old_grain_number)
    {
        // portion of inclusion particles for current cell
        double cell_part_vol_fraction;
        
        // portion of inclusion particles for current neighbour cell
        double nghb_part_vol_fraction;
        
        int neighb_index;
        int neighb_grain_type;
        int neighb_grain_index;
        
        RecCellR3 cur_cell = (RecCellR3)get(cell_counter);
        cell_part_vol_fraction = cur_cell.getParticleVolumeFraction();
        
        RecCellR3 neighb_cell;
        
        Double cell_mech_en_D;
        Double nghb_mech_en_D;
        
        Double cell_tors_en_D;
        Double nghb_tors_en_D;
        
        Double cell_part_vol_fraction_D;
        Double nghb_part_vol_fraction_D;
        
        if(cur_cell.getGrainType() == Common.INNER_CELL & cur_cell.getType() != Common.OUTER_CELL)
        for(int neighb_counter = 0; neighb_counter < neighb1S_number; neighb_counter++)
        {
          neighb_index = neighbours3D[cell_counter][neighb_counter];
          
          if(neighb_index > -1)
          {
            neighb_cell = (RecCellR3)get(neighb_index);
            neighb_grain_type  = neighb_cell.getGrainType();
            neighb_grain_index = neighb_cell.getGrainIndex();
            
            if(neighb_grain_type == Common.INNER_CELL & neighb_cell.getType() != Common.OUTER_CELL)
            if(neighb_grain_index <= old_grain_number)
            {
              nghb_part_vol_fraction = neighb_cell.getParticleVolumeFraction();
              
              cur_cell.setParticleVolumeFraction(Math.min(1.0, cell_part_vol_fraction + nghb_part_vol_fraction));
              
              nghb_part_vol_fraction = nghb_part_vol_fraction + cell_part_vol_fraction - cur_cell.getParticleVolumeFraction();
              
              neighb_cell.setParticleVolumeFraction(nghb_part_vol_fraction);
              
              // TEST
              cell_mech_en_D = new Double(cur_cell.getMechEnergy());
              nghb_mech_en_D = new Double(neighb_cell.getMechEnergy());
              
              cell_tors_en_D = new Double(cur_cell.getTorsionEnergy());
              nghb_tors_en_D = new Double(neighb_cell.getTorsionEnergy());
              
              cell_part_vol_fraction_D = new Double(cell_part_vol_fraction);
              nghb_part_vol_fraction_D = new Double(nghb_part_vol_fraction);
        
              if(cell_mech_en_D.isNaN() | nghb_mech_en_D.isNaN() | cell_tors_en_D.isNaN() | nghb_tors_en_D.isNaN() | 
                 cell_part_vol_fraction_D.isNaN() | nghb_part_vol_fraction_D.isNaN())
              {
                  System.out.println("ERROR!!! cell_mech_en = "+cell_mech_en_D+"; nghb_mech_en = "+nghb_mech_en_D+
                                            "; cell_tors_en = "+cell_tors_en_D+"; nghb_tors_en = "+nghb_tors_en_D+
                                            "; cell_part_vol_fraction = "+cell_part_vol_fraction_D+
                                            "; nghb_part_vol_fraction = "+nghb_part_vol_fraction_D);
              }
            }
          }
        }
    }
}