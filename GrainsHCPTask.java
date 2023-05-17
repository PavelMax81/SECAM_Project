    package corecalc;
   /*
    * @(#) GrainsHCPTask.java version 1.3.8;       Mar 2009
    *
    * Copyright (c) 2002-2009 BlueMap, Ltd. All Rights Reserved.
    * 
    * Class for simulation of grain structure
    *
    *=============================================================
    *  Last changes:
    *         27 Jan 2009 by Pavel V. Maksimov (in method "calcProbDamage" th rule 
    *            of calculation of thickening probability is realized);
    *         28 Jan 2009 by Pavel V. Maksimov 
    *            (exclusion of possibility of cluster closing);
    *         31 Mar 2009 by Pavel V. Maksimov 
    *            (creation of the method "generateEmbryosRegularly()",
    *             comparison of numbers of neighbours at 2nd and 3rd spheres
    *             when numbers of neighbours at 1st sphere are equal to each other).
    *         02 Apr 2009 by Pavel V. Maksimov 
    *             (creation of the method "generateEmbryosRandomly()").
    * 
    *         File version 1.3.8
    */

   /** Class for simulation of grain structure
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.3.8 - Apr 2009
    */
    import java.util.*;
    import java.io.*;
    import grainsCore.*;
    import interf.UICommon;
    import interf.UI_SpecialSpecimen;
    import recGeometry.*;
    import javafx.scene.control.TextArea;
    import util.*;

    public class GrainsHCPTask extends Thread
    {
        protected String writeFileName_2;
        protected String writeFileName;

        /** Value of state responsible for damaged cell */
        protected byte damaged_cell;
        
        /** Value of state responsible for damaged cell */
        protected byte undamaged_cell;

        /** Type of packing of cellular automata */
        protected byte packing_type;

        /** Number of cells in specimen along axis X */
        protected static int cell_number_I;
        
        /** Number of cells in specimen along axis Y */
        protected static int cell_number_J;
        
        /** Number of cells in specimen along axis Z */
        protected static int cell_number_K;

        /** Size of specimen along axis X */
        protected double specimen_size_X;

        /** Size of specimen along axis Y */
        protected double specimen_size_Y;

        /** Size of specimen along axis Z */
        protected double specimen_size_Z;
        
        /** Total number of cells in specimen */
        private static int frSpecSize;
                
        // Total number of embryos
        protected int embryos_number;

        // Number of embryos in 1st substrate layer
        protected int embryos_number_1;

        // Number of embryos in 2nd substrate layer
        protected int embryos_number_2;
        
        /** Number of time steps */
        protected int stepNumber;
                       
        /** Index of cluster containing damaged cell at 1st coordination sphere
         * at current time step
         */
        protected int clusterIndex;
        
        /** Number of cluster, which will contain the cell at next time step */
        protected int clusterNumber;
        protected int clusterNumber2;
        protected int clusterNumber3;
        protected int clusterNumber4;
        protected int clusterNumber5;
/*
        int index;      
        int neighbourIndexI;
        int neighbourIndexJ;
        int neighbourIndexK;
        int neighbour2IndexI;
        int neighbour2IndexJ;
        int neighbour2IndexK;
        int neighbour3IndexI;
        int neighbour3IndexJ;
        int neighbour3IndexK; 
   */
        /** Triple index of cell */
        protected Three indices;
        
        /** First index of cell */
        protected int index1;
        
        /** Second index of cell */
        protected int index2;
        
        /** Third index of cell */
        protected int index3;
        
     // int z_plane = 0;
        
     // int[] clusterNumbers;

        /** Random number */
        protected long long_rand;
        
        /** Minimal value of number of type "long" */
        protected long min_value;
        
        /** Maximal value of number of type "long" */
        protected long max_value;

        /** Probability of switch of cell having damaged neighbour */
        protected double probDamage;
        
        /** Probability of switch of cell, which does not have damaged neighbour
         * (generation of embryo)
         */
        protected double probGeneration;
        
        /** Coefficient of inner anisotropy */
        protected double coeff_inner_anisotropy;
        
        /** Coefficient of outer anisotropy */
        protected double coeff_outer_anisotropy;
        
        /** Average values of grain sizes (in cell diameters) along axes for lower substrate, surface layer,
         * 1st and 2nd intermediate layers and upper substrate correspondingly */
        protected double average_grain_size  , average_grain_size_Y  , average_grain_size_Z;
        protected double average_grain_size_2, average_grain_size_2_Y, average_grain_size_2_Z;
        protected double average_grain_size_3, average_grain_size_3_Y, average_grain_size_3_Z;
        protected double average_grain_size_4, average_grain_size_4_Y, average_grain_size_4_Z;
        protected double average_grain_size_5, average_grain_size_5_Y, average_grain_size_5_Z;
        
        /** Thickness of grain boundary region in surface layer */
        protected double grain_bound_region_thickness_2;
        
        /** Thickness of grain boundary region in higher intermediate layer */
        protected double grain_bound_region_thickness_3;
        
        /** Thickness of grain boundary region in lower intermediate layer */
        protected double grain_bound_region_thickness_4;
        
        /** Thickness of grain boundary region in higher substrate */
        protected double grain_bound_region_thickness_5;
        
        /** Thickness of grain boundary region in lower substrate */
        protected double grain_bound_region_thickness;

        /** Method of distribution of embryos */
        protected byte embryo_distribution_type;
        
        /** Vector of inner anisotropy */
        protected VectorR3 inner_vector;
        
        /** Vector of outer anisotropy */
        protected VectorR3 outer_vector;
        
        //  Three damagedNeighb1Three;
        
        /** 2D array of indices of neighbour cells at 1st, 2nd and 3rd coordination spheres:
         * 1st index is responsible for central cell index,
         * 2nd - for neighbour cell index (indices from  0 to 11 - for cells at 1st coordination sphere,
         *                                 indices from 12 to 41 - for cells at 1st coordination sphere,
         *                                 indices from 42 to 53 - for cells at 1st coordination sphere)
         */
        protected int[][] neighbours3D;
        
   //   int[] neighbours1S;
   //   int[] neighbours2S;
   //   int[] neighbours3S;

        /** Cluster containing all cells in the state "1" */
//        Cluster largeCluster;
        
        /** Cluster containing all cells switched in the state "1" at current time step */
   //     Cluster largeTempCluster;
        
        /** Cluster (set) of cells */
   //     Cluster cluster;

        /** List of cell clusters at current time step */
   //     ArrayList clusterList;
        
        /** List of cell clusters at next time step */
   //     ArrayList newClusters;

        /** Specimen at current time step */
        protected static GrainsHCPSpecimenR3 frSpec;
        protected GrainsHCPSpecimenR3 frSpec2;
        protected GrainsHCPSpecimenR3 frSpec3;
        protected GrainsHCPSpecimenR3 frSpec4;
        protected GrainsHCPSpecimenR3 frSpec5;
        
        /** Specimen at current time step: new cells are hot, old cells are cold */
     // GrainsHCPSpecimenR3 hotSpec;
        
        /** Specimen with cells, which must be joined to clusters at next time step */
        protected GrainsHCPSpecimenR3 frSpecFuture;
        protected GrainsHCPSpecimenR3 frSpecFuture2;
        protected GrainsHCPSpecimenR3 frSpecFuture3;
        protected GrainsHCPSpecimenR3 frSpecFuture4;
        protected GrainsHCPSpecimenR3 frSpecFuture5;
        
        /** Array of triple indices of all cells contained in specimen */
        protected Three[] cell_indices;
        
   //   VectorR3[] neighb_shifts;
        
        /** Vectors from cell to its neighbours at 1st coordination sphere */
        protected VectorR3[] resid_vectors = new VectorR3[12];
        
       /** Differences of indices of cell and neighbours at 1st coordination sphere */
 //     Three[] neighb_shifts00;
 //     Three[] neighb_shifts01;
 //     Three[] neighb_shifts10;
 //     Three[] neighb_shifts11;
                
        /** Random number */
        protected Random rand;
        
        /** Buffering character-input stream */
        protected BufferedReader br;
        
        /** Buffering character-output stream */
        protected BufferedWriter bw;
        
        /** Buffering character-output stream */
        protected BufferedWriter bw1;

        /** Buffering character-output stream for recording of information about grain colours */
        protected static BufferedWriter bw_colours;
        
        /** Name of task executed */
        protected String task_file;
        
        /** Name of specimen constructed */
        protected String specimen_file;

//      boolean writeDamagedNeighbours;

        /** Field is intended for write all completed steps in progress frame
         */
        protected TextArea completed_steps;

        /** The variable is responsible for showing of boundary cells of each grain. */
        protected byte show_grain_bounds;

        /** Number of phases for specimen consisting from layers */
        protected int phase_number = 5;
        
        /** Specimen volume expressed in cubes of cell radius */
        protected double specimen_volume;

        /** Cell size (diameter) */
        protected double cell_size;
            
        /** Average grain volume expressed in cubes of cell radius */
        protected double average_grain_volume;

        /** Cell radius in case of hexagonal close packing (HCP) of cells */
        protected final static double radius_HCP = 1.0;
        
        /** Cell radius in case of simple cubic packing (SCP) of cells */
        protected final static double radius_SCP = Math.sqrt(6.0);//3*Math.sqrt(2.0/3.0);//2.0;

        /** Indices of layers where each cell is located */
        byte[] layer_indices;

        /** Total number of grains in specimen */
        private static int total_grain_number;

        private static String task_name;
        
        protected int[] layer_grain_numbers;
        
        /** Variable responsible for direction of vector perpendicular to layer interfaces */
        protected byte layer_direction = UI_SpecialSpecimen.layer_direction;
        
        // Probability of thickening
        private double prob_thickhess = LoadProperties.THICKENING_PROBABILITY;
            
        // Probability of turning
        private double prob_turn      = LoadProperties.TURN_PROBABILITY;
        
        private long[] time_period = new long[8];
        
        // Grain embryo coordinates
        VectorR3 embryo_coords[];
        
        // maximal length of dendrite
        private double max_gr_vec_length = 0;
        
        // Periods of presence of cells near clusters
        private double[] life_periods;
        
        // time step in seconds
        private double time_step = 1.0E-6;
        
        // period of growth of clusters in seconds
        private double growth_period = 1.0E-15;

        /** The constructor generates 3-layer specimen.
         * @param create_layers variable turning on creation of surface layers
         * @param layer2_type type of intermediate layer
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of intermediate layer
         * @param _completed_steps text area
         */
        public GrainsHCPTask(boolean create_layers, byte layer2_type, double layer1_thickness, double layer2_thickness,
                             double layer3_thickness, double layer2_elem_size, TextArea _completed_steps)
        {
            /* Block 20 continue */

            // Reading of task parameters
            readTask();
            
            // Variable responsible for direction of vector perpendicular to layer interfaces
            layer_direction = UI_SpecialSpecimen.layer_direction;
            
            if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            {
              cell_number_I    = (int)Math.round(cell_number_I*radius_SCP/radius_HCP);
              cell_number_J    = (int)Math.round(cell_number_J*radius_SCP/radius_HCP);
              cell_number_K    = (int)Math.round(cell_number_K*radius_SCP/radius_HCP);
              
              specimen_size_X  = specimen_size_X*(radius_SCP/radius_HCP);
              specimen_size_Y  = specimen_size_Y*(radius_SCP/radius_HCP);
              specimen_size_Z  = specimen_size_Z*(radius_SCP/radius_HCP);
              
              layer1_thickness = layer1_thickness*(radius_SCP/radius_HCP);
              layer2_thickness = layer2_thickness*(radius_SCP/radius_HCP);
              layer3_thickness = layer3_thickness*(radius_SCP/radius_HCP);
              layer2_elem_size = layer2_elem_size*(radius_SCP/radius_HCP);
              
              average_grain_size     = average_grain_size  *(radius_SCP/radius_HCP);
              average_grain_size_Y   = average_grain_size_Y*(radius_SCP/radius_HCP);
              average_grain_size_Z   = average_grain_size_Z*(radius_SCP/radius_HCP);
              
              average_grain_size_2   = average_grain_size_2*(radius_SCP/radius_HCP);
              average_grain_size_2_Y = average_grain_size_2_Y*(radius_SCP/radius_HCP);
              average_grain_size_2_Z = average_grain_size_2_Z*(radius_SCP/radius_HCP);
              
              average_grain_size_3   = average_grain_size_3*(radius_SCP/radius_HCP);
              average_grain_size_3_Y = average_grain_size_3_Y*(radius_SCP/radius_HCP);
              average_grain_size_3_Z = average_grain_size_3_Z*(radius_SCP/radius_HCP);
              
              average_grain_size_4   = average_grain_size_4*(radius_SCP/radius_HCP);
              average_grain_size_4_Y = average_grain_size_4_Y*(radius_SCP/radius_HCP);
              average_grain_size_4_Z = average_grain_size_4_Z*(radius_SCP/radius_HCP);
              
              average_grain_size_5   = average_grain_size_5*(radius_SCP/radius_HCP);
              average_grain_size_5_Y = average_grain_size_5_Y*(radius_SCP/radius_HCP);
              average_grain_size_5_Z = average_grain_size_5_Z*(radius_SCP/radius_HCP);
              
              System.out.println("Specimen sizes are increased in "+(radius_SCP/radius_HCP)+" times.");
              System.out.println();
              System.out.println("cell_number_I    = "+cell_number_I);
              System.out.println("cell_number_J    = "+cell_number_J);
              System.out.println("cell_number_K    = "+cell_number_K);
              System.out.println();
              System.out.println("specimen_size_X  = "+specimen_size_X);
              System.out.println("specimen_size_Y  = "+specimen_size_Y);
              System.out.println("specimen_size_Z  = "+specimen_size_Z);
              System.out.println();
              System.out.println("layer1_thickness = "+layer1_thickness);
              System.out.println("layer2_thickness = "+layer2_thickness);
              System.out.println("layer3_thickness = "+layer3_thickness);
              System.out.println("layer2_elem_size = "+layer2_elem_size);              
              System.out.println();
              System.out.println("average_grain_size   = "+average_grain_size);
              System.out.println("average_grain_size_2 = "+average_grain_size_2);
              System.out.println("average_grain_size_3 = "+average_grain_size_3);
              System.out.println("average_grain_size_4 = "+average_grain_size_4);
              System.out.println("average_grain_size_5 = "+average_grain_size_5);
              System.out.println();
            }

            completed_steps = _completed_steps;

           // Creation of two specimens without embryos of cracks
            System.out.println(GrainsHCPCommon.CREATION_OF_SPECIMENS);
            
            frSpec   = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpec2  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpec3  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpec4  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpec5  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

        //  hotSpec = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

            // Specimen with cells, which must be joined to clusters at next time step
            frSpecFuture  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecFuture2 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecFuture3 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecFuture4 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecFuture5 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

            // Number of cells in specimen
            frSpecSize    = frSpec.size();

            // Setting of coordinates of vectors from cell to its neighbours at 1st coordination sphere
            setNeighb3D1SVectors();

            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            neighbours3D = new int[frSpecSize][54];

            //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
            // of each cell of specimen
            for(int cell_counter = 0; cell_counter<frSpecSize; cell_counter++)
            {
                setNeighbours3D(cell_counter);
            }

            // Setting of triple indices of all cells contained in specimen
            setTripleIndices();

           // List of clusters containing cells in the state "1"
         // clusterList = new ArrayList(0);

           // Cluster containing all cells in the state "1"
//            largeCluster = new Cluster();

            // The number of time steps
            stepNumber = LoadProperties.STEP_NUMBER;

            // The probability of generation of embryo
            probGeneration = LoadProperties.GENERATION_PROBABILITY;

         // Stochastic distribution of crack embryos in specimen (time step #0)
            System.out.println();
            System.out.println(GrainsHCPCommon.GENERATION_OF_EMBRYOS);

            // Thickness of upper surface layer where embryos cannot be generated
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;

      //      embryo_distribution_type = LoadProperties.STOCH_EMBRYOS_IN_CUBES;

            // Thickness of 1st substrate layer
      //      double substrate_layer_1_thick = (specimen_size_Z - layer_thickness);//*0.5;//0;//
            
            // Coordinates of planes - facets of specimen part where ebbryos are generated
            double min_coord_X = 0;
            double max_coord_X = 0;
            double min_coord_Y = 0;
            double max_coord_Y = 0;
            double min_coord_Z = 0;
            double max_coord_Z = 0;

            // TEST
            System.out.println("Total thickness of layers: "+layer_thickness);
            System.out.println("frSpecSize = "+frSpecSize);
            System.out.println("specimen_size_Z = "+specimen_size_Z);

            embryos_number = 0;

            if(embryo_distribution_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
              System.out.println();
              System.out.println("Method of embryos distribution: STOCHASTIC_EMBRYOS");
                // generateEmbryos(layer_thickness);
                // generateEmbryos(layer_thickness, substrate_layer_1_thick);
                // generateEmbryos(layer1_thickness, layer2_thickness, layer3_thickness, substrate_layer_1_thick);
                
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
              {
                min_coord_Y = 0;
                max_coord_Y = specimen_size_Y;
                min_coord_Z = 0;
                max_coord_Z = specimen_size_Z;
                
                min_coord_X = 0;
                max_coord_X = layer1_thickness;
                generateEmbryos(frSpec2, average_grain_size_2, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness;
                max_coord_X = layer1_thickness+layer2_thickness;
                generateEmbryos(frSpec3, average_grain_size_3, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness;
                max_coord_X = layer1_thickness+layer2_thickness;
                generateEmbryos(frSpec4, average_grain_size_4, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness+layer2_thickness;
                max_coord_X = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryos(frSpec5, average_grain_size_5, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_X = specimen_size_X;
                generateEmbryos(frSpec, average_grain_size, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
              {
                min_coord_X = 0;
                max_coord_X = specimen_size_X;                
                min_coord_Z = 0;
                max_coord_Z = specimen_size_Z;
                
                min_coord_Y = 0;
                max_coord_Y = layer1_thickness;
                generateEmbryos(frSpec2, average_grain_size_2, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness;
                generateEmbryos(frSpec3, average_grain_size_3, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness;
                generateEmbryos(frSpec4, average_grain_size_4, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness+layer2_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryos(frSpec5, average_grain_size_5, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_Y = specimen_size_Y;
                generateEmbryos(frSpec, average_grain_size, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
              {
                min_coord_X = 0;
                max_coord_X = specimen_size_X;                
                min_coord_Y = 0;
                max_coord_Y = specimen_size_Y;
                
                min_coord_Z = 0;
                max_coord_Z = layer1_thickness;
                generateEmbryos(frSpec2, average_grain_size_2, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness;
                generateEmbryos(frSpec3, average_grain_size_3, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness;
                generateEmbryos(frSpec4, average_grain_size_4, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness+layer2_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryos(frSpec5, average_grain_size_5, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_Z = specimen_size_Z;
                generateEmbryos(frSpec, average_grain_size, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
            }
            
            double average_grain_size_X,   average_grain_size_Y,   average_grain_size_Z;
            double average_grain_size_2_X, average_grain_size_2_Y, average_grain_size_2_Z;
            double average_grain_size_3_X, average_grain_size_3_Y, average_grain_size_3_Z;
            double average_grain_size_4_X, average_grain_size_4_Y, average_grain_size_4_Z;
            double average_grain_size_5_X, average_grain_size_5_Y, average_grain_size_5_Z;
            
            average_grain_size_X = 1.0*average_grain_size;
            average_grain_size_Y = 0.3*average_grain_size;
            average_grain_size_Z = 0.3*average_grain_size;
            
            average_grain_size_2_X = average_grain_size_2_Y = average_grain_size_2_Z = 1.0*average_grain_size_2;
            average_grain_size_3_X = average_grain_size_3_Y = average_grain_size_3_Z = 1.0*average_grain_size_3;
            average_grain_size_4_X = average_grain_size_4_Y = average_grain_size_4_Z = 1.0*average_grain_size_4;
            average_grain_size_5_X = average_grain_size_5_Y = average_grain_size_5_Z = 1.0*average_grain_size_5;
            
            if(embryo_distribution_type==LoadProperties.REGULAR_EMBRYOS)
            {
              System.out.println();
              System.out.println("Method of embryos distribution: REGULAR_EMBRYOS");
           // generateEmbryosRegularly(layer_thickness);
           // generateEmbryosRegularly(layer_thickness, substrate_layer_1_thick);
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
              {
                min_coord_Y = 0;
                max_coord_Y = specimen_size_Y;
                min_coord_Z = 0;
                max_coord_Z = specimen_size_Z;
                                        
                min_coord_X = 0;
                max_coord_X = layer1_thickness;
                generateEmbryosRegularly(frSpec2, average_grain_size_2_X, average_grain_size_2_Y, average_grain_size_2_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness;
                max_coord_X = layer1_thickness+layer2_thickness;
                generateEmbryosRegularly(frSpec3, average_grain_size_3_X, average_grain_size_3_Y, average_grain_size_3_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness;
                max_coord_X = layer1_thickness+layer2_thickness;
                generateEmbryosRegularly(frSpec4, average_grain_size_4_X, average_grain_size_4_Y, average_grain_size_4_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness+layer2_thickness;
                max_coord_X = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryosRegularly(frSpec5, average_grain_size_5_X, average_grain_size_5_Y, average_grain_size_5_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_X = specimen_size_X;
                generateEmbryosRegularly(frSpec, average_grain_size_X, average_grain_size_Y, average_grain_size_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
              {
                min_coord_X = 0;
                max_coord_X = specimen_size_X;                
                min_coord_Z = 0;
                max_coord_Z = specimen_size_Z;
                
                min_coord_Y = 0;
                max_coord_Y = layer1_thickness;
                generateEmbryosRegularly(frSpec2, average_grain_size_2_X, average_grain_size_2_Y, average_grain_size_2_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness;
                generateEmbryosRegularly(frSpec3, average_grain_size_3_X, average_grain_size_3_Y, average_grain_size_3_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness;
                generateEmbryosRegularly(frSpec4, average_grain_size_4_X, average_grain_size_4_Y, average_grain_size_4_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness+layer2_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryosRegularly(frSpec5, average_grain_size_5_X, average_grain_size_5_Y, average_grain_size_5_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_Y = specimen_size_Y;
                generateEmbryosRegularly(frSpec, average_grain_size_X, average_grain_size_Y, average_grain_size_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
              {
                min_coord_X = 0;
                max_coord_X = specimen_size_X;                
                min_coord_Y = 0;
                max_coord_Y = specimen_size_Y;
                
                min_coord_Z = 0;
                max_coord_Z = layer1_thickness;
                generateEmbryosRegularly(frSpec2, average_grain_size_2_X, average_grain_size_2_Y, average_grain_size_2_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness;
                generateEmbryosRegularly(frSpec3, average_grain_size_3_X, average_grain_size_3_Y, average_grain_size_3_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness;
                generateEmbryosRegularly(frSpec4, average_grain_size_4_X, average_grain_size_4_Y, average_grain_size_4_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness+layer2_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryosRegularly(frSpec5, average_grain_size_5_X, average_grain_size_5_Y, average_grain_size_5_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_Z = specimen_size_Z;
                generateEmbryosRegularly(frSpec, average_grain_size_X, average_grain_size_Y, average_grain_size_Z, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
            }

            if(embryo_distribution_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {
              System.out.println();
              System.out.println("Method of embryos distribution: STOCH_EMBRYOS_IN_CUBES");
              // generateEmbryosRandomly(layer_thickness);
              // generateEmbryosRandomly(layer_thickness, substrate_layer_1_thick);
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
              {
                min_coord_Y = 0;
                max_coord_Y = specimen_size_Y;
                min_coord_Z = 0;
                max_coord_Z = specimen_size_Z;
                
                min_coord_X = 0;
                max_coord_X = layer1_thickness;
                generateEmbryosRandomly(frSpec2, average_grain_size_2, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness;
                max_coord_X = layer1_thickness+layer2_thickness;
                generateEmbryosRandomly(frSpec3, average_grain_size_3, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness;
                max_coord_X = layer1_thickness+layer2_thickness;
                generateEmbryosRandomly(frSpec4, average_grain_size_4, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness+layer2_thickness;
                max_coord_X = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryosRandomly(frSpec5, average_grain_size_5, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_X = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_X = specimen_size_X;
                generateEmbryosRandomly(frSpec, average_grain_size, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
              {
                min_coord_X = 0;
                max_coord_X = specimen_size_X;                
                min_coord_Z = 0;
                max_coord_Z = specimen_size_Z;
                
                min_coord_Y = 0;
                max_coord_Y = layer1_thickness;
                generateEmbryosRandomly(frSpec2, average_grain_size_2, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness;
                generateEmbryosRandomly(frSpec3, average_grain_size_3, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness;
                generateEmbryosRandomly(frSpec4, average_grain_size_4, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness+layer2_thickness;
                max_coord_Y = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryosRandomly(frSpec5, average_grain_size_5, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Y = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_Y = specimen_size_Y;
                generateEmbryosRandomly(frSpec, average_grain_size, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
              
              if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
              {
                min_coord_X = 0;
                max_coord_X = specimen_size_X;                
                min_coord_Y = 0;
                max_coord_Y = specimen_size_Y;
                
                min_coord_Z = 0;
                max_coord_Z = layer1_thickness;
                generateEmbryosRandomly(frSpec2, average_grain_size_2, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness;
                generateEmbryosRandomly(frSpec3, average_grain_size_3, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness;
                generateEmbryosRandomly(frSpec4, average_grain_size_4, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness+layer2_thickness;
                max_coord_Z = layer1_thickness+layer2_thickness+layer3_thickness;
                generateEmbryosRandomly(frSpec5, average_grain_size_5, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
                
                min_coord_Z = layer1_thickness+layer2_thickness+layer3_thickness;
                max_coord_Z = specimen_size_Z;
                generateEmbryosRandomly(frSpec, average_grain_size, min_coord_X, max_coord_X, min_coord_Y, max_coord_Y, min_coord_Z, max_coord_Z);
              }
            }
            
            /* Block 21 */
            
            //   generateEmbryosOnFacets();
            
            // Fields for calculation of time periods of program execution
            Date startDate;
            Date firstDate;
            Date secondDate;
            Date thirdDate;
            Date finishDate;
            long timePeriod;
            long totalPeriod = 0;
            long clusterNumberCalcPeriod = 0;

     //     int clusterListSize;
            int damagedCellsCounter;
            int time_step_counter = 0;

            // Number of cluster of current cell at next time step
            int cell_cluster_number = undamaged_cell;

            // Real number of specimen building
            int real_step_number = 0;

            // Propagation of cracks (dendrites)
            for (int stepCounter = 1; stepCounter < stepNumber+1; stepCounter++)
            {
                //----- start of time step -------
           /*
                if(stepCounter == 1)
                    writeDamagedNeighbours = true;
                else
                    writeDamagedNeighbours = false;
            */

                startDate = new Date();
                time_step_counter++;
                clusterNumberCalcPeriod = 0;

                // Cell coordinates
                VectorR3 coordinates;
                
                boolean calc_anis_cosinus = true;

               /** Calculation of number of damaged neighbours at each of 3 coordination spheres
                * of every undamaged cell and determination of the probability of damage of every undamaged cell
                */
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    firstDate = new Date();

                    // Calculation of triple index of cell
                    indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

                    // Calculation of coordinates of cell
                    coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());

                    // If the cell is located in substrate (not in surface layers) and its state is "0"
                    // then the number of cluster that will contain the cell is calculated.
                     
                //    if(coordinates.getZ()>=layer_thickness)
                    if(frSpec.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();

                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand > LoadProperties.GROWTH_PROBABILITY*min_value)&
                           (long_rand < LoadProperties.GROWTH_PROBABILITY*max_value))
                          clusterNumber = calcClusterNumber(frSpec, cell_counter, calc_anis_cosinus);
                        else
                          clusterNumber = undamaged_cell;
                    }

                    if(frSpec2.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();

                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand > LoadProperties.GROWTH_PROBABILITY*min_value)&
                           (long_rand < LoadProperties.GROWTH_PROBABILITY*max_value))
                          clusterNumber2 = calcClusterNumber(frSpec2, cell_counter, calc_anis_cosinus);
                        else
                          clusterNumber2 = undamaged_cell;
                    }

                    if(frSpec3.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();

                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand > LoadProperties.GROWTH_PROBABILITY*min_value)&
                           (long_rand < LoadProperties.GROWTH_PROBABILITY*max_value))
                          clusterNumber3 = calcClusterNumber(frSpec3, cell_counter, calc_anis_cosinus);
                        else
                          clusterNumber3 = undamaged_cell;
                    }

                    if(frSpec4.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();

                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand > LoadProperties.GROWTH_PROBABILITY*min_value)&
                           (long_rand < LoadProperties.GROWTH_PROBABILITY*max_value))
                          clusterNumber4 = calcClusterNumber(frSpec4, cell_counter, calc_anis_cosinus);
                        else
                          clusterNumber4 = undamaged_cell;
                    }

                    if(frSpec5.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();

                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand > LoadProperties.GROWTH_PROBABILITY*min_value)&
                           (long_rand < LoadProperties.GROWTH_PROBABILITY*max_value))
                          clusterNumber5 = calcClusterNumber(frSpec5, cell_counter, calc_anis_cosinus);
                        else
                          clusterNumber5 = undamaged_cell;
                    }

                    //   Calculation of time period of determination of numbers of clusters,
                    // which will contain cells at next time step
                    timePeriod = (new Date()).getTime() - firstDate.getTime();
                    clusterNumberCalcPeriod = clusterNumberCalcPeriod + timePeriod;

                    // If the the cell should be joined to one of clusters
                    // then the cell is joined to this cluster at the end of current time step.
                    // The number of the cell is kept in temporary massive responsible for indices of cells
                    // joined to corresponding cluster at current time step.
                    if (clusterNumber != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture.set(cell_counter, clusterNumber);

                    if (clusterNumber2 != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture2.set(cell_counter, clusterNumber2);

                    if (clusterNumber3 != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture3.set(cell_counter, clusterNumber3);

                    if (clusterNumber4 != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture4.set(cell_counter, clusterNumber4);

                    if (clusterNumber5 != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture5.set(cell_counter, clusterNumber5);
                }

                //   TEST
                firstDate = new Date();
                timePeriod = firstDate.getTime() - startDate.getTime();

            //    clusterListSize = clusterList.size();
                damagedCellsCounter = 0;

                // Change of states of cells
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cell_cluster_number = frSpecFuture.get(cell_counter);

                    if(cell_cluster_number != undamaged_cell)
    /* =true */     if(frSpec.get(cell_counter) == undamaged_cell)
                    {
                        frSpec.set(cell_counter, cell_cluster_number);
          //            hotSpec.set(cell_counter, time_step_counter);
                        damagedCellsCounter++;
                    }

                    frSpecFuture.set(cell_counter, undamaged_cell);

                    cell_cluster_number = frSpecFuture2.get(cell_counter);

                    if(cell_cluster_number != undamaged_cell)
    /* =true */     if(frSpec2.get(cell_counter) == undamaged_cell)
                        frSpec2.set(cell_counter, cell_cluster_number);

                    frSpecFuture2.set(cell_counter, undamaged_cell);


                    cell_cluster_number = frSpecFuture3.get(cell_counter);

                    if(cell_cluster_number != undamaged_cell)
    /* =true */     if(frSpec3.get(cell_counter) == undamaged_cell)
                        frSpec3.set(cell_counter, cell_cluster_number);

                    frSpecFuture3.set(cell_counter, undamaged_cell);


                    cell_cluster_number = frSpecFuture4.get(cell_counter);

                    if(cell_cluster_number != undamaged_cell)
    /* =true */     if(frSpec4.get(cell_counter) == undamaged_cell)
                        frSpec4.set(cell_counter, cell_cluster_number);

                    frSpecFuture4.set(cell_counter, undamaged_cell);


                    cell_cluster_number = frSpecFuture5.get(cell_counter);

                    if(cell_cluster_number != undamaged_cell)
    /* =true */     if(frSpec5.get(cell_counter) == undamaged_cell)
                        frSpec5.set(cell_counter, cell_cluster_number);

                    frSpecFuture5.set(cell_counter, undamaged_cell);
                }

           //   TEST
                secondDate = new Date();
                timePeriod = secondDate.getTime() - firstDate.getTime();

           //   TEST
                thirdDate = new Date();
                timePeriod = thirdDate.getTime() - secondDate.getTime();

                // Number of cells in current cluster
                int clusterSize = 0;

                // Total number of cells in all clusters
                int totalSizeOfClusters = 0;

           //     int largeClusterSize = largeCluster.size();

                // Array of cluster sizes
                // (i-th element of array equals number of cells in i-th cluster, i>0)
                int[] cluster_sizes = new int[embryos_number+1];

                // Index of cluster containing current cell
                int cluster_index = 0;

                // Calculation of cluster sizes
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cluster_index = frSpec.get(cell_counter);

               //   if(cluster_index != 0)
                    if(cluster_index >= 0)
                        if(cluster_index < embryos_number+1)
                            cluster_sizes[cluster_index]++;
                        else
                            System.out.println(GrainsHCPCommon.ERROR_1);
                }

                // Printing of values of cluster sizes
                for (cluster_index = 1; cluster_index <= embryos_number; cluster_index++)
                {
                    clusterSize = cluster_sizes[cluster_index];
                    totalSizeOfClusters = totalSizeOfClusters + clusterSize;
                }

                // End of time cycle if all cells are joined to clusters
                if(totalSizeOfClusters == frSpecSize)
                {
                    stepCounter = stepNumber;
                    System.out.println("SPECIMEN is built quickly!!!");
                }

                System.out.println("<1> "+GrainsHCPCommon.TOTAL_NUMBER_OF_CELLS + totalSizeOfClusters+
                                   "; frSpecSize = "+frSpecSize+"; stepCounter = "+stepCounter+"; stepNumber = "+stepNumber);

                if(totalSizeOfClusters == frSpecSize)
                    System.out.print(" SPECIMEN is built!!!");
                else
                    System.out.print(" SPECIMEN is not built!!!");
                
          //      System.out.println(GrainsHCPCommon.NUMBER_OF_SWITCHED_CELLS+time_step_counter+": "+damagedCellsCounter);
                
                // Reduction of time cycle if there are no cells joined to clusters at current time step
          //      if(damagedCellsCounter == 0)//totalSizeOfClusters*0.001)
            //        stepCounter = stepCounter + Math.max(1, (stepNumber - stepCounter)/20);
                
                // Printing of data about every z-plane of cells in the file
                double coord_X = -1; //
                double coord_Y = -1; // Coordinates of cell
                double coord_Z = -1; //

                VectorR3 cell_coordinates;  // Three coordinates of cell

                int state = -1;
                int damageCounter = 0;
            //  int totalDamageNumber = 0;

                if((time_step_counter%(stepNumber/GrainsHCPCommon.OUTPUT_FILES_NUMBER) == 0) |
                   (totalSizeOfClusters == frSpecSize)|(stepCounter > stepNumber))
           //     for (int z_plane = 0; z_plane < cell_number_K; z_plane++)
                {
                    try
                    {
                        layer_grain_numbers = new int[5];

                        // Creation of file with information about grain structure of each specimen layer
                        if(create_layers)
                  //          layer_grain_numbers = createInitCondSimpleFileGrainsInLayers(layer1_thickness,
                    //                     layer2_thickness, layer3_thickness, layer2_type, layer2_elem_size);

                        layer_grain_numbers = createInitCondSimpleFileGrainsInLayers_2(layer1_thickness,
                                         layer2_thickness, layer3_thickness, layer2_type, layer2_elem_size);

                        // Total number of grains in specimen
                        total_grain_number = layer_grain_numbers[0]+layer_grain_numbers[1]+
                                layer_grain_numbers[2]+layer_grain_numbers[3]+layer_grain_numbers[4];

                     //   bw = new BufferedWriter(new FileWriter(writeFileName+"_"+time_step_counter+".txt"));
                        bw = new BufferedWriter(new FileWriter(writeFileName));
                        bw.write("#  The file contains information about initial conditions:");
                        bw.newLine();
                        bw.write("# in 1st string - total number of inner cellular automata;");
                        bw.newLine();
                        bw.write("# in 2nd string - total number of grains, the numbers of grains in surface layer,");
                        bw.newLine();
                        bw.write("# upper interlayer, lower interlayer, upper substrate, lower substrate;");
                        bw.newLine();
                        bw.write("# in each further string - grain index of corresponding inner CA");
                        bw.newLine();
                        bw.write("# and location type of corresponding inner CA in grain and in specimen:");
                        bw.newLine();
                        bw.write("# 2 - outer boundary CA (it does not exist here),");
                        bw.newLine();
                        bw.write("# 3 - intragranular CA in the interior of specimen,");
                        bw.newLine();
                        bw.write("# 4 - intergranular CA in the interior of specimen,");
                        bw.newLine();
                        bw.write("# 5 - intragranular CA on specimen boundary,");
                        bw.newLine();
                        bw.write("# 6 - intergranular CA on specimen boundary.");
                        bw.newLine();
                        bw.write(frSpecSize+"\n");
                    //  bw.write(embryos_number+phase_number-1+" "+embryos_number_1+"\n");
                    //  bw.write(embryos_number+" "+embryos_number_1+"\n");
                        bw.write(total_grain_number+" "+layer_grain_numbers[0]+" "+layer_grain_numbers[1]+" "+
                             layer_grain_numbers[2]+" "+layer_grain_numbers[3]+" "+layer_grain_numbers[4]+"\n");

                        bw1= new BufferedWriter(new FileWriter(writeFileName_2));
                        bw1.write("#  The file contains information about initial geometry:");
                        bw1.newLine();
                        bw1.write("# in 1st string - total number of inner cellular automata,");
                        bw1.newLine();
                        bw1.write("# specimen sizes along axes X, Y, Z expressed in radiuses of cell");
                        bw1.newLine();
                        bw1.write("# and number of grains;");
                        bw1.newLine();
                        bw1.write("# in each further string - grain index of corresponding inner CA, its coordinates");
                        bw1.newLine();
                        bw1.write("# and location type of corresponding inner CA in grain and in specimen:");
                        bw1.newLine();
                        bw1.write("# 2 - outer boundary CA,");
                        bw1.newLine();
                        bw1.write("# 3 - intragranular CA in the interior of specimen,");
                        bw1.newLine();
                        bw1.write("# 4 - intergranular CA in the interior of specimen,");
                        bw1.newLine();
                        bw1.write("# 5 - intragranular CA on specimen boundary,");
                        bw1.newLine();
                        bw1.write("# 6 - intergranular CA on specimen boundary.");
                        bw1.newLine();
                        bw1.write(frSpecSize+" "+
                                  LoadProperties.SPECIMEN_SIZE_X+" "+
                                  LoadProperties.SPECIMEN_SIZE_Y+" "+
                                  LoadProperties.SPECIMEN_SIZE_Z);

                        if(show_grain_bounds != 0)
                        //  bw1.write(" "+(embryos_number+phase_number-1));
                            bw1.write(" "+total_grain_number);

                        bw1.newLine();
                    //    bw1.write(embryos_number+"");
                    //    bw1.newLine();
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }

                    // System.out.println("The number of cells changed its state at "+time_step_counter+" time step: "+damagedCellsCounter);

                    int[] neighb1S_indices = new int[12];
                    int neghb1S_state = -1;
                    boolean grain_boundary_cell = false;
                    boolean inner_boundary_cell = false;

                    if(false)
                    {
                        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                        {
                            System.out.println("specimen_size_X = "+specimen_size_X);
                            System.out.println("specimen_size_Y = "+specimen_size_Y);
                            System.out.println("specimen_size_Z = "+specimen_size_Z);
                            
                            System.out.println("layer1_thickness = "+layer1_thickness);
                            System.out.println("layer2_thickness = "+layer2_thickness);
                            System.out.println("layer3_thickness = "+layer3_thickness);
                            System.out.println("layer2_elem_size = "+layer2_elem_size);
                        }
                        
                        if(layer2_type == Common.SIMPLE_LAYER)
                        {
                            createInitCondSimpleFile(layer1_thickness, layer2_thickness, layer3_thickness);

                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SIMPLE_LAYER");
                            System.out.println("Type of intermediate layer: SIMPLE_LAYER");
                        }

                        if(layer2_type == Common.TRIANGLE_GOFR)
                        {
                            createInitCondTriangleGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: TRIANGLE_GOFR");
                            System.out.println("Type of intermediate layer: TRIANGLE_GOFR");
                        }

                        if(layer2_type == Common.SQUARE_GOFR)
                        {
                            createInitCondSquareGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SQUARE_GOFR");
                            System.out.println("Type of intermediate layer: SQUARE_GOFR");
                        }

                        if(layer2_type == Common.PYRAMIDES)
                        {
                            createInitCondPyramidesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: PYRAMIDES");
                            System.out.println("Type of intermediate layer: PYRAMIDES");
                        }

                        if(layer2_type == Common.CHESS_CUBES)
                        {
                            createInitCondChessCubesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: CHESS_CUBES");
                            System.out.println("Type of intermediate layer: CHESS_CUBES");
                        }
                    }
                    
                    real_step_number++;

                    // Prolongation of time cycle if dendritic growth is going on
                    if(real_step_number >= stepNumber-10 & totalSizeOfClusters < frSpecSize)
                        stepCounter = stepNumber - stepNumber/4;
                    else
                    {
                      if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                      {
                        // Transition of specimen to simple cubic packing
                        showSpecimenInSCP();
                      }

                      if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                      for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                      {
                        state = frSpec.get(cell_counter);

                        // Determination of current cell indices
                        indices = cell_indices[cell_counter];

                        //System.out.println(cell_counter+" "+indices.getI() +" "+indices.getJ()+" "+indices.getK());

                        index1 = indices.getI();
                        index2 = indices.getJ();
                        index3 = indices.getK();

                        // Printing of coordinates and states of cells
                    //    if (index3 == z_plane)
                    //    {
                            cell_coordinates = calcCoordinates(index1, index2, index3);

                            coord_X = cell_coordinates.getX();
                            coord_Y = cell_coordinates.getY();
                            coord_Z = cell_coordinates.getZ();

                            grain_boundary_cell = false;
                            inner_boundary_cell = false;

                            if(state != undamaged_cell)
                            {
                                if(show_grain_bounds != 0)
                                {
                                    // Single indices of cells at 1st coordination sphere of current "central" cell
                                    neighb1S_indices = neighbours3D[cell_counter];

                                    // Checking of states of cells at 1st coordination sphere of current "central" cell
                                    for(int neihb1S_counter = 0; neihb1S_counter<12; neihb1S_counter++)
                                    {
                                        if(neighb1S_indices[neihb1S_counter]>-1)
                                        {
                                            // State of neighbour cell
                                            neghb1S_state = frSpec.get(neighb1S_indices[neihb1S_counter]);

                                            // If state of neighbour cell differs from state of current "central" cell
                                            //then this "central" cell is boundary cell
                                            if(neghb1S_state != state)
                                                grain_boundary_cell = true;
                                        }
                                        else
                                        {
                                            inner_boundary_cell = true;
                                        }
                                    }
                                }

                                try
                                {
                                    bw.write(/*"0.0 0.0 300.0 "*/state+"");
                                    bw1.write(state+" "+coord_X+" "+coord_Y+" "+coord_Z);

                                    if(show_grain_bounds!=0)
                                    {
                                        if((grain_boundary_cell)&(inner_boundary_cell))
                                            bw1.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell)&(!inner_boundary_cell))
                                            bw1.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell)&(inner_boundary_cell))
                                            bw1.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell)&(!inner_boundary_cell))
                                            bw1.write(" "+Common.INTRAGRANULAR_CELL);

                                        if((grain_boundary_cell)&(inner_boundary_cell))
                                            bw.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell)&(!inner_boundary_cell))
                                            bw.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell)&(inner_boundary_cell))
                                            bw.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell)&(!inner_boundary_cell))
                                            bw.write(" "+Common.INTRAGRANULAR_CELL);
                                    }

                                    bw1.newLine();
                                    bw1.flush();
                                    bw.newLine();
                                    bw.flush();
                                }
                                catch(IOException io_exc)
                                {
                                    System.out.println("Error: " + io_exc);
                                }
                                damageCounter++;
                            }
                    //    }
                      }
                    }

                    try
                    {
                        bw.close();
                        bw1.close();

                        System.out.println();
                        System.out.println("Name of created file: "+writeFileName);
                        System.out.println("Name of created file: "+writeFileName_2);
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }
                }
                
                //   TEST
                finishDate = new Date();
                timePeriod = finishDate.getTime() - thirdDate.getTime();

                totalPeriod = totalPeriod + timePeriod;
                
                if(totalSizeOfClusters == frSpecSize) 
                {
                    stepCounter = stepNumber + 10;
                    System.out.print(" SPECIMEN is built!!!");
                }
                else
                {
                    if(stepCounter >= stepNumber-10)
                        stepCounter = stepNumber - stepNumber/4;
                    
                    System.out.print(" SPECIMEN is NOT built!!!");
                }
                //----------- end of time step --------------------
            }
            
            System.out.println("Number of steps of specimen building: "+real_step_number);
        }

        /** The constructor generates 3-layer specimen.
         * @param create_layers variable turning on creation of surface layers
         * @param layer2_type type of intermediate layer
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of intermediate layer
         * @param _completed_steps text area
         */
        public GrainsHCPTask(boolean create_layers, byte layer2_type, double layer1_thickness, double layer2_thickness,
                             double layer3_thickness, double layer2_elem_size, TextArea _completed_steps, 
                             byte surface_layer_embryo_distr_type, byte upper_inter_layer_embryo_distr_type, byte lower_inter_layer_embryo_distr_type,
                             byte upper_substrate_embryo_distr_type, byte lower_substrate_embryo_distr_type)
        {
            /* Block 20 continue */
            
            // Reading of task parameters
            readTask();

            completed_steps = _completed_steps;

           // Creation of two specimens without embryos of cracks
            System.out.println(GrainsHCPCommon.CREATION_OF_SPECIMENS);
            
            if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            {
              cell_size        = cell_size*(radius_SCP/radius_HCP);
              
              specimen_size_X  = specimen_size_X*(radius_SCP/radius_HCP);
              specimen_size_Y  = specimen_size_Y*(radius_SCP/radius_HCP);
              specimen_size_Z  = specimen_size_Z*(radius_SCP/radius_HCP);
              
              specimen_volume  = specimen_size_X*specimen_size_Y*specimen_size_Z;
              
              cell_number_I    = (int)Math.round(cell_number_I*radius_SCP/radius_HCP);
              cell_number_J    = (int)Math.round(cell_number_J*radius_SCP/radius_HCP);
              cell_number_K    = (int)Math.round(cell_number_K*radius_SCP/radius_HCP);
              
              layer1_thickness = layer1_thickness*(radius_SCP/radius_HCP);
              layer2_thickness = layer2_thickness*(radius_SCP/radius_HCP);
              layer3_thickness = layer3_thickness*(radius_SCP/radius_HCP);
              layer2_elem_size = layer2_elem_size*(radius_SCP/radius_HCP);
                            
              average_grain_size     = average_grain_size    *(radius_SCP/radius_HCP);
              average_grain_size_Y   = average_grain_size_Y  *(radius_SCP/radius_HCP);
              average_grain_size_Z   = average_grain_size_Z  *(radius_SCP/radius_HCP);
              
              average_grain_size_2   = average_grain_size_2  *(radius_SCP/radius_HCP);
              average_grain_size_2_Y = average_grain_size_2_Y*(radius_SCP/radius_HCP);
              average_grain_size_2_Z = average_grain_size_2_Z*(radius_SCP/radius_HCP);
              
              average_grain_size_3   = average_grain_size_3  *(radius_SCP/radius_HCP);
              average_grain_size_3_Y = average_grain_size_3_Y*(radius_SCP/radius_HCP);
              average_grain_size_3_Z = average_grain_size_3_Z*(radius_SCP/radius_HCP);
              
              average_grain_size_4   = average_grain_size_4  *(radius_SCP/radius_HCP);
              average_grain_size_4_Y = average_grain_size_4_Y*(radius_SCP/radius_HCP);
              average_grain_size_4_Z = average_grain_size_4_Z*(radius_SCP/radius_HCP);
              
              average_grain_size_5   = average_grain_size_5  *(radius_SCP/radius_HCP);
              average_grain_size_5_Y = average_grain_size_5_Y*(radius_SCP/radius_HCP);
              average_grain_size_5_Z = average_grain_size_5_Z*(radius_SCP/radius_HCP);
              
              grain_bound_region_thickness_2 = grain_bound_region_thickness_2*(radius_SCP/radius_HCP);
              grain_bound_region_thickness_3 = grain_bound_region_thickness_3*(radius_SCP/radius_HCP);
              grain_bound_region_thickness_4 = grain_bound_region_thickness_4*(radius_SCP/radius_HCP);
              grain_bound_region_thickness_5 = grain_bound_region_thickness_5*(radius_SCP/radius_HCP);
              grain_bound_region_thickness   = grain_bound_region_thickness  *(radius_SCP/radius_HCP);
              
              System.out.println("Specimen sizes are increased in "+(radius_SCP/radius_HCP)+" times.");
              System.out.println();
              System.out.println("cell_size        = "+cell_size);
              System.out.println();
              System.out.println("specimen_size_X  = "+specimen_size_X);
              System.out.println("specimen_size_Y  = "+specimen_size_Y);
              System.out.println("specimen_size_Z  = "+specimen_size_Z);
              System.out.println("specimen_volume  = "+specimen_volume);
              System.out.println();
              System.out.println("cell_number_I    = "+cell_number_I);
              System.out.println("cell_number_J    = "+cell_number_J);
              System.out.println("cell_number_K    = "+cell_number_K);
              System.out.println();
              System.out.println("layer1_thickness = "+layer1_thickness);
              System.out.println("layer2_thickness = "+layer2_thickness);
              System.out.println("layer3_thickness = "+layer3_thickness);
              System.out.println("layer2_elem_size = "+layer2_elem_size);
              System.out.println();
              System.out.println("average_grain_size   = "+average_grain_size);
              System.out.println("average_grain_size_2 = "+average_grain_size_2);
              System.out.println("average_grain_size_3 = "+average_grain_size_3);
              System.out.println("average_grain_size_4 = "+average_grain_size_4);
              System.out.println("average_grain_size_5 = "+average_grain_size_5);
              System.out.println();
              System.out.println("grain_bound_region_thickness   = "+grain_bound_region_thickness);
              System.out.println("grain_bound_region_thickness_2 = "+grain_bound_region_thickness_2);
              System.out.println("grain_bound_region_thickness_3 = "+grain_bound_region_thickness_3);
              System.out.println("grain_bound_region_thickness_4 = "+grain_bound_region_thickness_4);
              System.out.println("grain_bound_region_thickness_5 = "+grain_bound_region_thickness_5);
              System.out.println();
            }

            max_gr_vec_length = 0;
            
            frSpec   = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
         //     frSpec2  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
       //     frSpec3  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
       //     frSpec4  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
       //     frSpec5  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

        //  hotSpec = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

            // Specimen with cells, which must be joined to clusters at next time step
            frSpecFuture  = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
        //    frSpecFuture2 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
        //    frSpecFuture3 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
        //    frSpecFuture4 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
        //    frSpecFuture5 = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

            // Number of cells in specimen
            frSpecSize    = frSpec.size();

            // Setting of coordinates of vectors from cell to its neighbours at 1st coordination sphere
            setNeighb3D1SVectors();

            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            neighbours3D = new int[frSpecSize][54];
            
            //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
            // of each cell of specimen
            for(int cell_counter = 0; cell_counter<frSpecSize; cell_counter++)
            {
                setNeighbours3D(cell_counter);
            }
            
            // Setting of triple indices of all cells contained in specimen
            setTripleIndices();
            
           // List of clusters containing cells in the state "1"
         // clusterList = new ArrayList(0);
            
           // Cluster containing all cells in the state "1"
//            largeCluster = new Cluster();
            
            // The number of time steps
            stepNumber = LoadProperties.STEP_NUMBER;// 1000;//
            
            // The probability of generation of embryo
            probGeneration = LoadProperties.GENERATION_PROBABILITY;
            
         // Stochastic distribution of crack embryos in specimen (time step #0)
            System.out.println();
            System.out.println(GrainsHCPCommon.GENERATION_OF_EMBRYOS);
            
            // Thickness of upper surface layer where embryos cannot be generated
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;
            
      //      embryo_distribution_type = LoadProperties.STOCH_EMBRYOS_IN_CUBES;
            
            // Thickness of 1st substrate layer
      //      double substrate_layer_1_thick = (specimen_size_Z - layer_thickness);//*0.5;//0;//
            
            // TEST
            System.out.println("Total thickness of layers: "+layer_thickness);
            System.out.println("frSpecSize = "+frSpecSize);
            System.out.println("specimen_size_Z = "+specimen_size_Z);
            
            // Indices of layers where each cell is located
            layer_indices = new byte[frSpecSize];
            
            // Determination of indices of layers where each cell is located
            determineLayerIndices(layer1_thickness, layer2_thickness, layer3_thickness, layer2_type, layer2_elem_size);
            
            embryos_number = 0;
            
            // Periods of presence of cells near clusters
            life_periods = new double[frSpecSize];
            
            // Calculation of triple indices and coordinates for all cells
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                // Period of presence of current cell near one of clusters
                life_periods[cell_counter] = 0;
            }
            
            // Surface layer
            if(surface_layer_embryo_distr_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
                System.out.println("\nMethod of embryos distribution in surface layer: STOCHASTIC_EMBRYOS");
                
                if(average_grain_size_2 < cell_size*1.5 & average_grain_size_2_Y < cell_size*1.5 & average_grain_size_2_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)1);
                else 
                    generateEmbryos(frSpec, average_grain_size_2, average_grain_size_2_Y, average_grain_size_2_Z, (byte)1);
            }
            
            if(surface_layer_embryo_distr_type==LoadProperties.REGULAR_EMBRYOS)
            {                
                System.out.println("\nMethod of embryos distribution in surface layer: REGULAR_EMBRYOS");
                
                if(average_grain_size_2 < cell_size*1.5 & average_grain_size_2_Y < cell_size*1.5 & average_grain_size_2_Z < cell_size*1.5)
                        generateEmbryosInEachCell(frSpec, (byte)1);                
                else                                       
                    generateEmbryosRegularly(frSpec, average_grain_size_2, average_grain_size_2_Y, average_grain_size_2_Z, (byte)1);
            }
            
            if(surface_layer_embryo_distr_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {                
                System.out.println("\nMethod of embryos distribution in surface layer: STOCH_EMBRYOS_IN_CUBES");
                
                if(average_grain_size_2 < cell_size*1.5 & average_grain_size_2_Y < cell_size*1.5 & average_grain_size_2_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)1);
                else
                    generateEmbryosRandomly(frSpec, average_grain_size_2, average_grain_size_2_Y, average_grain_size_2_Z, (byte)1);
            }
            
            // Upper intermediate layer
            if(upper_inter_layer_embryo_distr_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
                System.out.println("\nMethod of embryos distribution in upper intermediate layer: STOCHASTIC_EMBRYOS");      
                
                if(average_grain_size_3 < cell_size*1.5 & average_grain_size_3_Y < cell_size*1.5 & average_grain_size_3_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)2);
                else                                       
                    generateEmbryos(frSpec, average_grain_size_3, average_grain_size_3_Y, average_grain_size_3_Z, (byte)2);
            }
            
            if(upper_inter_layer_embryo_distr_type==LoadProperties.REGULAR_EMBRYOS)
            {                
                System.out.println("\nMethod of embryos distribution in upper intermediate layer: REGULAR_EMBRYOS");
                
                if(average_grain_size_3 < cell_size*1.5 & average_grain_size_3_Y < cell_size*1.5 & average_grain_size_3_Z < cell_size*1.5)
                   generateEmbryosInEachCell(frSpec, (byte)2);
                else                                      
                    generateEmbryosRegularly(frSpec, average_grain_size_3, average_grain_size_3_Y, average_grain_size_3_Z, (byte)2);
            }
            
            if(upper_inter_layer_embryo_distr_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {
                System.out.println("\nMethod of embryos distribution in upper intermediate layer: STOCH_EMBRYOS_IN_CUBES");
                
                if(average_grain_size_3 < cell_size*1.5 & average_grain_size_3_Y < cell_size*1.5 & average_grain_size_3_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)2);
                else                                       
                    generateEmbryosRandomly(frSpec, average_grain_size_3, average_grain_size_3_Y, average_grain_size_3_Z, (byte)2);
            }
            
            // Lower intermediate layer
            if(lower_inter_layer_embryo_distr_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
                System.out.println("\nMethod of embryos distribution in lower intermediate layer: STOCHASTIC_EMBRYOS");
                
                if(average_grain_size_4 < cell_size*1.5 & average_grain_size_4_Y < cell_size*1.5 & average_grain_size_4_Z < cell_size*1.5) 
                    generateEmbryosInEachCell(frSpec, (byte)3);
                else                                       
                    generateEmbryos(frSpec, average_grain_size_4, average_grain_size_4_Y, average_grain_size_4_Z, (byte)3);
            }            
            if(lower_inter_layer_embryo_distr_type==LoadProperties.REGULAR_EMBRYOS)
            {                
                System.out.println("\nMethod of embryos distribution in lower intermediate layer: REGULAR_EMBRYOS");
                
                if(average_grain_size_4 < cell_size*1.5 & average_grain_size_4_Y < cell_size*1.5 & average_grain_size_4_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)3);
                else                                       
                    generateEmbryosRegularly(frSpec, average_grain_size_4, average_grain_size_4_Y, average_grain_size_4_Z, (byte)3);
            }            
            if(lower_inter_layer_embryo_distr_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {
                System.out.println("\nMethod of embryos distribution in lower intermediate layer: STOCH_EMBRYOS_IN_CUBES");
                
                if(average_grain_size_4 < cell_size*1.5 & average_grain_size_4_Y < cell_size*1.5 & average_grain_size_4_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)3);
                else                                       
                    generateEmbryosRandomly(frSpec, average_grain_size_4, average_grain_size_4_Y, average_grain_size_4_Z, (byte)3);
            }
            
            // Upper substrate
            if(upper_substrate_embryo_distr_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
                System.out.println("\nMethod of embryos distribution in upper substrate: STOCHASTIC_EMBRYOS");
                
                if(average_grain_size_5 < cell_size*1.5 & average_grain_size_5_Y < cell_size*1.5 & average_grain_size_5_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)4);
                else                                       
                    generateEmbryos(frSpec, average_grain_size_5, average_grain_size_5_Y, average_grain_size_5_Z, (byte)4);
            }            
            if(upper_substrate_embryo_distr_type==LoadProperties.REGULAR_EMBRYOS)
            {                
                System.out.println("\nMethod of embryos distribution in upper substrate: REGULAR_EMBRYOS");
                
                if(average_grain_size_5 < cell_size*1.5 & average_grain_size_5_Y < cell_size*1.5 & average_grain_size_5_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)4);
                else                                       
                    generateEmbryosRegularly(frSpec, average_grain_size_5, average_grain_size_5_Y, average_grain_size_5_Z, (byte)4);
            }            
            if(upper_substrate_embryo_distr_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {
                System.out.println("\nMethod of embryos distribution in upper substrate: STOCH_EMBRYOS_IN_CUBES");
                
                if(average_grain_size_5 < cell_size*1.5 & average_grain_size_5_Y < cell_size*1.5 & average_grain_size_5_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)4);
                else                                       
                    generateEmbryosRandomly(frSpec, average_grain_size_5, average_grain_size_5_Y, average_grain_size_5_Z, (byte)4);
            }
            
            // Lower substrate
            if(lower_substrate_embryo_distr_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
                System.out.println("\nMethod of embryos distribution in lower substrate: STOCHASTIC_EMBRYOS");
                
                if(average_grain_size < cell_size*1.5 & average_grain_size_Y < cell_size*1.5 & average_grain_size_Z < cell_size*1.5)
                    generateEmbryosInEachCell(frSpec, (byte)5);
                else                                       
                    generateEmbryos(frSpec, average_grain_size, average_grain_size_Y, average_grain_size_Z, (byte)5);
            }            
            if(lower_substrate_embryo_distr_type==LoadProperties.REGULAR_EMBRYOS)
            {         
                if(average_grain_size < cell_size*1.5 & average_grain_size_Y < cell_size*1.5 & average_grain_size_Z < cell_size*1.5)
                {
                    System.out.println("\nMethod of embryos distribution in lower substrate: EmbryosInEachCell");
                    generateEmbryosInEachCell(frSpec, (byte)5);
                }
                else
                {
                    System.out.println("\nMethod of embryos distribution in lower substrate: REGULAR_EMBRYOS");
                    generateEmbryosRegularly(frSpec, average_grain_size, average_grain_size_Y, average_grain_size_Z, (byte)5);
                }
            }            
            if(lower_substrate_embryo_distr_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {                
                if(average_grain_size < cell_size*1.5 & average_grain_size_Y < cell_size*1.5 & average_grain_size_Z < cell_size*1.5)
                {
                    System.out.println("\nMethod of embryos distribution in lower substrate: EmbryosInEachCell");
                    generateEmbryosInEachCell(frSpec, (byte)5);
                }
                else      
                {
                    System.out.println("\nMethod of embryos distribution in lower substrate: STOCH_EMBRYOS_IN_CUBES");
                    generateEmbryosRandomly(frSpec, average_grain_size, average_grain_size_Y, average_grain_size_Z, (byte)5);
                }
            }
            
            System.out.println("______________________________________");
            System.out.println("coeff_inner_anisotropy = "+coeff_inner_anisotropy+"; "
                             + "inner_vector = ("+inner_vector.writeToString()+")");
            System.out.println("______________________________________");
            
            layer_grain_numbers = new int[5];

            // Creation of file with information about grain structure of each specimen layer
            
            if(create_layers)
              layer_grain_numbers = createInitCondSimpleFileGrainsInLayers_2(layer1_thickness,
                                         layer2_thickness, layer3_thickness, layer2_type, layer2_elem_size);
            
            // Total number of grains in specimen
            total_grain_number  = layer_grain_numbers[0]+layer_grain_numbers[1]+
                                  layer_grain_numbers[2]+layer_grain_numbers[3]+layer_grain_numbers[4];
            
            /* Block 21 */
            
            // generateEmbryosOnFacets();
            
            // Fields for calculation of time periods of program execution
            Date startDate;
            Date firstDate;
            Date secondDate;
            Date thirdDate;
            Date finishDate;
            long timePeriod;
            long totalPeriod = 0;
            long clusterNumberCalcPeriod = 0;
            long clusterSizeCalcPeriod = 0;
            long fileCreationPeriod = 0;
            long emptyCellsJoiningPeriod = 0;
            
     //     int clusterListSize;
            int damagedCellsCounter;
            int time_step_counter = 0;
            
            // Number of cluster of current cell at next time step
            int cell_cluster_number = undamaged_cell;
            
            int grain_index;
            boolean specimen_is_built = true;
            
            // Determination of presence of "empty" cells
            for(int cell_counter = 0; cell_counter<frSpec.size(); cell_counter++)
            {
                grain_index = frSpec.get(cell_counter);
                
                if(grain_index == GrainsHCPCommon.UNDAMAGED_CELL)
                {
                    specimen_is_built = false;                    
                    
                    //   System.out.println("SPECIMEN is not built!!! Cell # "+cell_counter+" is empty.");
                    cell_counter = frSpec.size();
                }
            }
            
            if(specimen_is_built)
            {
                stepNumber = 1;
                System.out.println("SPECIMEN is built!!!");
            }
            else
            {
                System.out.println("SPECIMEN is NOT built!!!");
            }
            
            // Number of cells in current cluster
            int clusterSize = 0;
            
            // Total number of cells in all clusters
            int totalSizeOfClusters = 0;
            
            // Assigned average length of dendrites
            double dend_aver_length = 0;
            
            // Variable responsible for simulation of long grains
            boolean create_long_grains = false;// true;// 
            
         //   if(LoadProperties.TURN_PROBABILITY == 0)
           //     create_long_grains = false;
            
            // Cell coordinates
            VectorR3 coordinates;
                
            boolean calc_anis_cosinus;
            
            if(create_long_grains)
            {
                // dend_aver_length = 0.5*Math.pow(frSpecSize*1.0, 1.0/3.0);
                dend_aver_length    = 0.2*frSpecSize/embryos_number;//2*specimen_size_X;
            
                // TEST
                System.out.println("Assigned average length of dendrites: "+dend_aver_length);
            }
            
         //   prob_thickhess = LoadProperties.THICKENING_PROBABILITY;
            
            // Printing of data about every z-plane of cells in the file
            double coord_X = -1; //
            double coord_Y = -1; // Coordinates of cell
            double coord_Z = -1; //

            VectorR3 cell_coordinates;  // Three coordinates of cell

            int state = -1;
            int damageCounter = 0;
            //  int totalDamageNumber = 0;
            
            // Array of cluster sizes
            // (i-th element of array equals number of cells in i-th cluster, i>0)
            int[] cluster_sizes;
            
            // Index of cluster containing current cell
            int cluster_index = 0;
            
            int[] neighb1S_indices;
            int neghb1S_state;
            boolean[] grain_boundary_cell;
            boolean[] inner_boundary_cell;
            
            // Array of cell location types:
            // 0 - inner cell (for grain and specimen); 
            // 1 - boundary cell (for grain or specimen).
            byte[] location_types;
            
            // List of all grains
            ArrayList[] grains;
            ArrayList grain;
            
            // Indicators of cells located in grain boundary regions
            boolean[] grain_bound_region_cell = new boolean[frSpecSize];
            
            for(int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                grain_bound_region_cell[cell_counter] = false;
            
            // Counter of cells located in grain boundary regions
            int grain_bound_region_cell_counter = 0;
            
            int grain_cell_number = 0;
            int grain_cell_index = -1;
            
            // Index of layer containing grains
            int layer_index = 0;
            
            // Thickness of grain boundary region in current layer
            double gbr_thickness = 0;
            
            int spec_bound_intergran_cell_number = 0;
            int intergran_cell_number = 0;
            int spec_bound_cell_number = 0;
            int intragran_cell_number = 0;
            int gr_bound_reg_cell_number = 0;
            int spec_bound_gr_bound_reg_cell_number = 0;
            int test_sum = 0;
            
            // Grain index of neighbour cell
            int neighb_grain_index;
            int gr_ind_counter;
            
            // List of indices of adjacent grains
            ArrayList neighb_grain_indices;
            
            // Number of empty cells (out of clusters)
            int empty_cells_number = 0;
            
            // TEST
            // stepNumber = 100;
            
            // Triple indices of cells
            Three tr_indices[] = new Three[frSpecSize];
            
            // Cell coordinates
            VectorR3 cell_coords[] = new VectorR3[frSpecSize];
            
            // Embryo coordinates
            embryo_coords = new VectorR3[total_grain_number];
            
            for(int counter = 0; counter < time_period.length; counter++)
              time_period[counter] = 0;
            
            boolean calcClusterNumberForAcicularStructure = false;// true;// 
        
            System.out.println("Number of grain embryos: "+total_grain_number);
            
            // Calculation of triple indices and coordinates for all cells
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                tr_indices[cell_counter]   = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);
                indices                    = tr_indices[cell_counter];
                
                cell_coords[cell_counter]  = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());
                
                neighbours3D[cell_counter] = calcNeighbours3D(cell_counter, cell_number_I, cell_number_J, cell_number_K);
                
                state = (int)frSpec.get(cell_counter);
                        
                if(state != undamaged_cell)
                {
                  embryo_coords[state - 1] = new VectorR3(cell_coords[cell_counter]);
                  
                  System.out.println("Embryo # "+state+" has coordinates ("+ embryo_coords[state - 1].writeToString()+").");
                }
            }
            
            System.out.println();
            
            // Propagation of cracks (dendrites)
            for (int stepCounter = 1; stepCounter < stepNumber+1; stepCounter++)
            {
                //----- start of time step -------
           /*
                if(stepCounter == 1)
                    writeDamagedNeighbours = true;
                else
                    writeDamagedNeighbours = false;
            */
                
                startDate = new Date();
                time_step_counter++;
          //      clusterNumberCalcPeriod = 0;

               /** Calculation of number of damaged neighbours at each of 3 coordination spheres
                * of every undamaged cell and determination of the probability of damage of every undamaged cell
                */
                if(!specimen_is_built)
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    if(create_long_grains)
                    {
                        if(totalSizeOfClusters < embryos_number*dend_aver_length)
                        {
                          //  if(layer_indices[cell_counter] != 1)
                            if(true)
                            {
                                prob_thickhess = 0;
                                prob_turn      = LoadProperties.TURN_PROBABILITY;
                            }
                            else                                
                            {
                                prob_thickhess = 0;
                                prob_turn      = 0;
                            }
                        }
                        else
                        {
                            create_long_grains = false;
                            prob_thickhess = LoadProperties.THICKENING_PROBABILITY;
                            prob_turn      = 0;
                        }
                    }
                    else
                    {
                        prob_thickhess = LoadProperties.THICKENING_PROBABILITY;
                        prob_turn      = LoadProperties.TURN_PROBABILITY;
                    }
                    
                    // firstDate = new Date();

                    // Obtaining of triple index of cell
                    indices = tr_indices[cell_counter];

                    // Calculation of coordinates of cell
                 //   coordinates = cell_coords[cell_counter];

                    // If the cell is located in substrate (not in surface layers) and its state is "0"
                    // then the number of cluster that will contain the cell is calculated.
                    if(frSpec.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();
                        
                        if(layer_indices[cell_counter] != 1)
                            calc_anis_cosinus = true;
                        else
                            calc_anis_cosinus = false;
                        
                        calc_anis_cosinus = true;
                               
                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand >= LoadProperties.GROWTH_PROBABILITY*min_value & long_rand < 0)|
                            long_rand >= (1 - LoadProperties.GROWTH_PROBABILITY)*max_value)
                        {
                          if(!calcClusterNumberForAcicularStructure)
                              clusterNumber = calcClusterNumber(frSpec, cell_counter, calc_anis_cosinus);
                          else
                              clusterNumber = calcClusterNumberForAcicularStructure(frSpec, cell_counter, calc_anis_cosinus);
                        }
                        else
                          clusterNumber = undamaged_cell;
                    }
                    else
                      // Period of presence of current cell near one of clusters
                      life_periods[cell_counter]+= time_step;

                    //  Calculation of time period of determination of numbers of clusters,
                    // which will contain cells at next time step
               //     timePeriod = (new Date()).getTime() - firstDate.getTime();
               //     clusterNumberCalcPeriod = clusterNumberCalcPeriod + timePeriod;

                    // If the the cell should be joined to one of clusters
                    // then the cell is joined to this cluster at the end of current time step.
                    // The number of the cell is kept in temporary massive responsible for indices of cells
                    // joined to corresponding cluster at current time step.
                    if (clusterNumber != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture.set(cell_counter, clusterNumber);
                }
                
            //  clusterListSize = clusterList.size();
                damagedCellsCounter = 0;

                // Change of states of cells
                if(!specimen_is_built)
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cell_cluster_number = frSpecFuture.get(cell_counter);
                    
                  //  if(frSpec.get(cell_counter) == undamaged_cell)
                      // Period of presence of current cell near one of clusters
                  //    life_periods[cell_counter] = 0;

                    if(cell_cluster_number != undamaged_cell)
                    if(frSpec.get(cell_counter) == undamaged_cell)// =true
                    {
                        frSpec.set(cell_counter, cell_cluster_number);
                        
                        // Period of presence of current cell near one of clusters
                      //  life_periods[cell_counter]++;
                        
                        damagedCellsCounter++;
                    }
                    
                    frSpecFuture.set(cell_counter, undamaged_cell);
                    
                 //   if(cell_cluster_number == undamaged_cell)
                    // Period of presence of current cell near one of clusters
                    // life_periods[cell_counter] = 0;
                }
                
                // TEST
                firstDate = new Date();
                timePeriod = firstDate.getTime() - startDate.getTime();
                clusterNumberCalcPeriod = clusterNumberCalcPeriod + timePeriod;
                
                // Number of cells in current cluster
                clusterSize = 0;

                // Total number of cells in all clusters
                totalSizeOfClusters = 0;

                // Array of cluster sizes
                // (i-th element of array equals number of cells in i-th cluster, i>0)
                cluster_sizes = new int[embryos_number+1];

                // Index of cluster containing current cell
                cluster_index = 0;

                // Calculation of cluster sizes
                if(!specimen_is_built)
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cluster_index = frSpec.get(cell_counter);

               //   if(cluster_index != 0)
                    if(cluster_index >= 0)
                        if(cluster_index < embryos_number+1)
                            cluster_sizes[cluster_index]++;
                        else
                            System.out.println(GrainsHCPCommon.ERROR_1);
                }

                // Printing of values of cluster sizes
                if(!specimen_is_built)
                for (cluster_index = 1; cluster_index <= embryos_number; cluster_index++)
                {
                    clusterSize = cluster_sizes[cluster_index];
                    totalSizeOfClusters = totalSizeOfClusters + clusterSize;
                    
                 //   System.out.println("Step #"+stepCounter+": cluster_index = "+cluster_index+"; clusterSize = "+clusterSize);
                }

                // End of time cycle if all cells are joined to clusters
                if(totalSizeOfClusters == frSpecSize)
                {
                    stepCounter = stepNumber;
                    specimen_is_built = true;                    
                }

                if(specimen_is_built)
                {
                    stepCounter = stepNumber;
                    totalSizeOfClusters = frSpecSize;
                }
                
                if(create_long_grains | totalSizeOfClusters == frSpecSize)
                  System.out.println("<2> "+ GrainsHCPCommon.TOTAL_NUMBER_OF_CELLS + totalSizeOfClusters+
                                     "; frSpecSize = "+frSpecSize+"; stepCounter = "+stepCounter+"; stepNumber = "+stepNumber+
                                     "; prob_thick = "+prob_thickhess+"; prob_turn = "+prob_turn);
                
                if(totalSizeOfClusters == frSpecSize)
                  System.out.println("SPECIMEN is built quickly!!!");
                
          //      System.out.println(GrainsHCPCommon.NUMBER_OF_SWITCHED_CELLS+time_step_counter+": "+damagedCellsCounter);

                // Reduction of time cycle if there are no cells joined to clusters at current time step
          //      if(!specimen_is_built)
           //     if(damagedCellsCounter == 0)//totalSizeOfClusters*0.001)
            //        stepCounter = stepCounter + Math.max(1, (stepNumber - stepCounter)/20);
                
           //   TEST
                secondDate = new Date();
                timePeriod = secondDate.getTime() - firstDate.getTime();
                clusterSizeCalcPeriod = clusterSizeCalcPeriod + timePeriod;
                
           //   if(specimen_is_built)
              if(specimen_is_built|
                 time_step_counter % Math.max(1, (stepNumber/Math.max(1, GrainsHCPCommon.OUTPUT_FILES_NUMBER))) == 0 |
                 totalSizeOfClusters == frSpecSize | stepCounter > stepNumber)
           //   for (int z_plane = 0; z_plane < cell_number_K; z_plane++)
                {
                    secondDate = new Date();
                    
                    try
                    {
                     /*
                        layer_grain_numbers = new int[5];

                        // Creation of file with information about grain structure of each specimen layer
                        if(create_layers)
                            layer_grain_numbers = createInitCondSimpleFileGrainsInLayers(layer1_thickness,
                                         layer2_thickness, layer3_thickness, layer2_type, layer2_elem_size);

                        // Total number of grains in specimen
                        int total_grain_number = layer_grain_numbers[0]+layer_grain_numbers[1]+
                                layer_grain_numbers[2]+layer_grain_numbers[3]+layer_grain_numbers[4];
                     */

                     //   bw = new BufferedWriter(new FileWriter(writeFileName+"_"+time_step_counter+".txt"));
                        
                        if(totalSizeOfClusters != frSpecSize)
                            bw = new BufferedWriter(new FileWriter(GrainsHCPCommon.DEND_RESULT_DIRECTION+"/grains_"+time_step_counter+".txt"));
                        else
                            bw = new BufferedWriter(new FileWriter(writeFileName));
                        
                        bw.write("#  The file contains information about initial conditions:");
                        bw.newLine();
                        bw.write("# in 1st string - total number of inner cells;");
                        bw.newLine();
                        bw.write("# in 2nd string - total number of grains, the numbers of grains in surface layer,");
                        bw.newLine();
                        bw.write("# upper interlayer, lower interlayer, upper substrate, lower substrate;");
                        bw.newLine();
                        bw.write("# in each further string - grain index of corresponding inner cell");
                        bw.newLine();
                        bw.write("# and location type of corresponding inner cell in grain and in specimen:");
                        bw.newLine();
                        bw.write("# 2 - outer boundary cell (it does not exist here),");
                        bw.newLine();
                        bw.write("# 3 - intragranular cell in the interior of specimen,");
                        bw.newLine();
                        bw.write("# 4 - intergranular cell in the interior of specimen,");
                        bw.newLine();
                        bw.write("# 5 - intragranular cell on specimen boundary,");
                        bw.newLine();
                        bw.write("# 6 - intergranular cell on specimen boundary.");
                        bw.newLine();
                        
                        if(totalSizeOfClusters != frSpecSize)
                            bw.write(totalSizeOfClusters+"\n");
                        else
                            bw.write(frSpecSize+"\n");
                        
                    //  bw.write(embryos_number+phase_number-1+" "+embryos_number_1+"\n");
                    //  bw.write(embryos_number+" "+embryos_number_1+"\n");
                        bw.write(total_grain_number+" "+layer_grain_numbers[0]+" "+layer_grain_numbers[1]+" "+
                             layer_grain_numbers[2]+" "+layer_grain_numbers[3]+" "+layer_grain_numbers[4]+"\n");

                        if(totalSizeOfClusters == frSpecSize)
                          bw1= new BufferedWriter(new FileWriter(writeFileName_2));
                        else
                          bw1= new BufferedWriter(new FileWriter(GrainsHCPCommon.DEND_RESULT_DIRECTION+"/str_"+time_step_counter+".res"));
                      
                        bw1.write("#  The file contains information about initial geometry:");
                        bw1.newLine();
                        bw1.write("# in 1st string - total number of inner cells,");
                        bw1.newLine();
                        bw1.write("# specimen sizes along axes X, Y, Z expressed in radiuses of cell");
                        bw1.newLine();
                        bw1.write("# and number of grains;");
                        bw1.newLine();
                        
                        if(totalSizeOfClusters == frSpecSize)
                        {
                          bw1.write("# in each further string - grain index of corresponding inner cell, its coordinates");
                          bw1.newLine();
                          bw1.write("# and location type of corresponding inner cell in grain and in specimen:");
                          bw1.newLine();
                          bw1.write("# 2 - outer boundary cell,");
                          bw1.newLine();
                          bw1.write("# 3 - intragranular cell in the interior of specimen,");
                          bw1.newLine();
                          bw1.write("# 4 - intergranular cell in the interior of specimen,");
                          bw1.newLine();
                          bw1.write("# 5 - intragranular cell on specimen boundary,");
                          bw1.newLine();
                          bw1.write("# 6 - intergranular cell on specimen boundary.");
                          bw1.newLine();
                        }
                        else
                        {
                          bw1.write("# in each further string - energy type, location type and grain index of corresponding inner cell");
                          bw1.newLine();
                        }
                        
                        if(totalSizeOfClusters != frSpecSize)
                            bw1.write("# "+totalSizeOfClusters+" "+
                                  LoadProperties.SPECIMEN_SIZE_X+" "+
                                  LoadProperties.SPECIMEN_SIZE_Y+" "+
                                  LoadProperties.SPECIMEN_SIZE_Z);
                        else
                            bw1.write(frSpecSize+" "+
                                  LoadProperties.SPECIMEN_SIZE_X+" "+
                                  LoadProperties.SPECIMEN_SIZE_Y+" "+
                                  LoadProperties.SPECIMEN_SIZE_Z);

                        if(show_grain_bounds != 0)
                        //  bw1.write(" "+(embryos_number+phase_number-1));
                            bw1.write(" "+total_grain_number);

                        bw1.newLine();
                    //    bw1.write(embryos_number+"");
                    //    bw1.newLine();
                      
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }

                    // System.out.println("The number of cells changed its state at "+time_step_counter+" time step: "+damagedCellsCounter);

                    neighb1S_indices = new int[12];
                    neghb1S_state = -1;
                    grain_boundary_cell = new boolean[frSpecSize];
                    inner_boundary_cell = new boolean[frSpecSize];

                    if(false)
                    {
                        if(layer2_type == Common.SIMPLE_LAYER)
                        {
                            createInitCondSimpleFile(layer1_thickness, layer2_thickness, layer3_thickness);

                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SIMPLE_LAYER");
                            System.out.println("Type of intermediate layer: SIMPLE_LAYER");
                        }

                        if(layer2_type == Common.TRIANGLE_GOFR)
                        {
                            createInitCondTriangleGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: TRIANGLE_GOFR");
                            System.out.println("Type of intermediate layer: TRIANGLE_GOFR");
                        }

                        if(layer2_type == Common.SQUARE_GOFR)
                        {
                            createInitCondSquareGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SQUARE_GOFR");
                            System.out.println("Type of intermediate layer: SQUARE_GOFR");
                        }

                        if(layer2_type == Common.PYRAMIDES)
                        {
                            createInitCondPyramidesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: PYRAMIDES");
                            System.out.println("Type of intermediate layer: PYRAMIDES");
                        }

                        if(layer2_type == Common.CHESS_CUBES)
                        {
                            createInitCondChessCubesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: CHESS_CUBES");
                            System.out.println("Type of intermediate layer: CHESS_CUBES");
                        }
                    }
                    
                    if(specimen_is_built)
                    if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                    {
                        // Transition of specimen to simple cubic packing
                        showSpecimenInSCP();
                        
                      //  frSpecSize = frSpec.size();
                    
                        System.out.println("Size of specimen in SCP: "+frSpecSize);
                    }
                    
                  //  frSpecSize = frSpec.size();
                    
                    // Array of cell location types: 
                    // 0 - inner cell (for grain and specimen); 
                    // 1 - boundary cell (for grain or specimen).
                    location_types = new byte[frSpecSize];
                    
                    System.out.println("Size of array of cell indices: "+cell_indices.length);

                    if(specimen_is_built)
                    if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                    {
                      System.out.println("Determination of cell location types is in process...");
                      
                      for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                      {
                        state = frSpec.get(cell_counter);

                        // Determination of current cell indices
                        indices = cell_indices[cell_counter];

                        //System.out.println(cell_counter+" "+indices.getI() +" "+indices.getJ()+" "+indices.getK());

                        index1 = indices.getI();
                        index2 = indices.getJ();
                        index3 = indices.getK();

                        // Printing of coordinates and states of cells
                    //    if (index3 == z_plane)
                    //    {
                      //      cell_coordinates = calcCoordinates(index1, index2, index3);
                      /*
                            cell_coordinates = cell_coords[cell_counter];

                            coord_X = cell_coordinates.getX();
                            coord_Y = cell_coordinates.getY();
                            coord_Z = cell_coordinates.getZ();
                      */
                            
                            grain_boundary_cell[cell_counter] = false;
                            inner_boundary_cell[cell_counter] = false;

                            if(state != undamaged_cell)
                            {
                              if(show_grain_bounds != 0)
                              {
                                // Single indices of cells at 1st coordination sphere of current "central" cell
                                neighb1S_indices = neighbours3D[cell_counter];

                                // Checking of states of cells at 1st coordination sphere of current "central" cell
                                for(int neihb1S_counter = 0; neihb1S_counter< 12; neihb1S_counter++)
                                {
                                    if(neighb1S_indices[neihb1S_counter]>-1)
                                    {
                                         // State of neighbour cell
                                         neghb1S_state = frSpec.get(neighb1S_indices[neihb1S_counter]);

                                         // If state of neighbour cell differs from state of current "central" cell
                                         //then this "central" cell is boundary cell
                                         if(neghb1S_state != state)
                                            grain_boundary_cell[cell_counter] = true;
                                    }
                                    else
                                        inner_boundary_cell[cell_counter] = true;
                                }
                                  
                                if(grain_boundary_cell[cell_counter]|inner_boundary_cell[cell_counter])
                                    location_types[cell_counter] = 1;
                                else
                                    location_types[cell_counter] = 0;
                             /*
                                try
                                {
                                    bw.write(state+"");
                                    bw1.write(state+" "+coord_X+" "+coord_Y+" "+coord_Z);

                                    if(show_grain_bounds!=0)
                                    {
                                        if((grain_boundary_cell[cell_counter])&(inner_boundary_cell[cell_counter]))
                                            bw1.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell[cell_counter])&(!inner_boundary_cell[cell_counter]))
                                            bw1.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell[cell_counter])&(inner_boundary_cell[cell_counter]))
                                            bw1.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell[cell_counter])&(!inner_boundary_cell[cell_counter]))
                                            bw1.write(" "+Common.INTRAGRANULAR_CELL);

                                        if((grain_boundary_cell[cell_counter])&(inner_boundary_cell[cell_counter]))
                                            bw.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell[cell_counter])&(!inner_boundary_cell[cell_counter]))
                                            bw.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell[cell_counter])&(inner_boundary_cell[cell_counter]))
                                            bw.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell[cell_counter])&(!inner_boundary_cell[cell_counter]))
                                            bw.write(" "+Common.INTRAGRANULAR_CELL);
                                    }

                                    bw1.newLine();
                                    bw1.flush();
                                    bw.newLine();
                                    bw.flush();
                                }
                                catch(IOException io_exc)
                                {
                                    System.out.println("Error: " + io_exc);
                                }
                             */
                                
                                damageCounter++;
                              }
                            }
                    //    }
                      }
                      
                      System.out.println("Determination of cell location types is finished!");
                    }
                    
                    // List of all grains
                    grains = new ArrayList[total_grain_number];
                    
                    grain = new ArrayList(0);
                    
                    // Indicators of cells located in grain boundary regions
                    grain_bound_region_cell = new boolean[frSpecSize];
                    
                    for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                      grain_bound_region_cell[cell_counter] = false;
                    
                    // Counter of cells located in grain boundary regions
                    grain_bound_region_cell_counter = 0;
                    
                    grain_cell_number = 0;
                    grain_cell_index = -1;
                    
                    // Index of layer containing grains
                    layer_index = 0;
                    
                    // Thickness of grain boundary region in current layer
                    gbr_thickness = 0;
                    
                    for(int grain_counter = 0; grain_counter < total_grain_number; grain_counter++)
                        grains[grain_counter] = new ArrayList(0);
                    
                    // Distribution of cells on grains and determination of cells 
                    // located in grain/specimen boundary regions
                    if(specimen_is_built)
                    if(gbr_thickness > 0)
                    if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                    {
                      System.out.println("Determination of grain boundary region cells is in process...");
                        
                      // Distribution of cells on grains
                      for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                      {
                        state = frSpec.get(cell_counter);
                        grains[state-1].add(cell_counter);
                      }
                    
                      // Determination of cells located in grain/specimen boundary regions
                      for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                      {
                        // Index of grain containing current cell
                        state = frSpec.get(cell_counter);
                        
                        // Cell must be internal for grain (and for specimen)
                        if(!grain_boundary_cell[cell_counter])
                     //   if(location_types[cell_counter] == 0)
                        {
                          // Grain containing current cell
                          grain = grains[state-1];
                        
                          // Number of cells in grain containing current cell
                          grain_cell_number = grain.size();
                        
                          // Index of layer containing this grain
                          layer_index = calcLayerIndex(state, layer_grain_numbers);
                        
                          // Determination of grain boundary thickness
                          if(layer_index == 1) gbr_thickness = grain_bound_region_thickness_2;
                          if(layer_index == 2) gbr_thickness = grain_bound_region_thickness_3;
                          if(layer_index == 3) gbr_thickness = grain_bound_region_thickness_4;
                          if(layer_index == 4) gbr_thickness = grain_bound_region_thickness_5;
                          if(layer_index == 5) gbr_thickness = grain_bound_region_thickness;
                        
                          // Consideration of all cells in grain containing current cell 
                          for(int grain_cell_counter = 0; grain_cell_counter < grain_cell_number; grain_cell_counter++)
                          {
                            // Index of current cell from this grain
                            grain_cell_index = (int)grain.get(grain_cell_counter);
                            
                            // Location type = 1 if the cell is on grain or specimen boundary, else location type = 0.
                        //    if(location_types[grain_cell_index] == 1)
                            if(grain_boundary_cell[grain_cell_index])
                            {
                                if(calcDistance(cell_counter, grain_cell_index) <= gbr_thickness)
                                {
                                    grain_bound_region_cell[cell_counter] = true;
                                    grain_cell_index = grain_cell_number;
                                    grain_bound_region_cell_counter++;
                                }
                            }
                          }
                        }
                      }
                      
                      System.out.println("Determination of grain boundary region cells is finished!");
                    }
                    
                    System.out.println("Minimal distance:                                      "+calcDistance(0, 1));
                    System.out.println("Thickness of grain boundary region in layer #1:        "+grain_bound_region_thickness_2);
                    System.out.println("Thickness of grain boundary region in layer #2:        "+grain_bound_region_thickness_3);
                    System.out.println("Thickness of grain boundary region in layer #3:        "+grain_bound_region_thickness_4);
                    System.out.println("Thickness of grain boundary region in layer #4:        "+grain_bound_region_thickness_5);
                    System.out.println("Thickness of grain boundary region in layer #5:        "+grain_bound_region_thickness);
                    System.out.println("Number of cells in grain boundary regions: "+grain_bound_region_cell_counter+"\n");
                    
                    spec_bound_intergran_cell_number = 0;
                    intergran_cell_number = 0;
                    spec_bound_cell_number = 0;
                    intragran_cell_number = 0;
                    gr_bound_reg_cell_number = 0;
                    spec_bound_gr_bound_reg_cell_number = 0;
                    test_sum = 0;
                    
                    try
                    {
                        System.out.println("Size of specimen: "+frSpecSize);
                        System.out.println("Size of array of cell indices: "+cell_indices.length);
                        
                        if(specimen_is_built)
                        for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                        {
                            state = frSpec.get(cell_counter);
                            
                            // Determination of current cell indices and coordinates
                            indices = cell_indices[cell_counter];
                            
                            index1 = indices.getI();
                            index2 = indices.getJ();
                            index3 = indices.getK();

                      //      cell_coordinates = calcCoordinates(index1, index2, index3);
                            cell_coordinates = cell_coords[cell_counter];

                            coord_X = cell_coordinates.getX();
                            coord_Y = cell_coordinates.getY();
                            coord_Z = cell_coordinates.getZ();
                            
                         //   if(state != undamaged_cell)
                            if(show_grain_bounds != 0)
                            {
                                if(state == undamaged_cell)
                                    state = total_grain_number + 1;
                                        
                                 bw.write(state+"");
                                  
                                 if(totalSizeOfClusters != frSpecSize)
                                   bw1.write("1");
                                 else
                                   bw1.write(state+" "+coord_X+" "+coord_Y+" "+coord_Z);
                                 
                                 if(totalSizeOfClusters == frSpecSize)
                                 if(show_grain_bounds!=0)
                                 {                                     
                                     if(!grain_boundary_cell[cell_counter] & !grain_bound_region_cell[cell_counter] & !inner_boundary_cell[cell_counter])
                                     {
                                         bw.write (" "+Common.INTRAGRANULAR_CELL);
                                         bw1.write(" "+Common.INTRAGRANULAR_CELL);
                                         intragran_cell_number++;
                                     }
                                     
                                     if(!grain_boundary_cell[cell_counter] & !grain_bound_region_cell[cell_counter] & inner_boundary_cell[cell_counter])
                                     {
                                         bw.write (" "+Common.INNER_BOUNDARY_CELL);
                                         bw1.write(" "+Common.INNER_BOUNDARY_CELL);
                                         spec_bound_cell_number++;
                                     }
                                                                          
                                     if(grain_boundary_cell[cell_counter]  & !grain_bound_region_cell[cell_counter] & !inner_boundary_cell[cell_counter])
                                     {
                                         bw.write (" "+Common.INTERGRANULAR_CELL);
                                         bw1.write(" "+Common.INTERGRANULAR_CELL);
                                         intergran_cell_number++;
                                     }
                                     
                                     if(grain_boundary_cell[cell_counter]  & !grain_bound_region_cell[cell_counter] & inner_boundary_cell[cell_counter])
                                     {
                                         bw.write (" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);
                                         bw1.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);
                                         spec_bound_intergran_cell_number++;
                                     }
                                     
                                     if(!grain_boundary_cell[cell_counter] & grain_bound_region_cell[cell_counter] & !inner_boundary_cell[cell_counter])
                                     {
                                         bw.write (" "+Common.GRAIN_BOUNDARY_REGION_CELL);
                                         bw1.write(" "+Common.GRAIN_BOUNDARY_REGION_CELL);
                                         gr_bound_reg_cell_number++;
                                     }
                                     
                                     if(!grain_boundary_cell[cell_counter] & grain_bound_region_cell[cell_counter] & inner_boundary_cell[cell_counter])
                                     {
                                         bw.write (" "+Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL);
                                         bw1.write(" "+Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL);
                                         spec_bound_gr_bound_reg_cell_number++;
                                     }
                                 }
                                 
                                 if(totalSizeOfClusters != frSpecSize)
                                   bw1.write(" "+Common.INNER_BOUNDARY_CELL+" "+state+" "+coord_X+" "+coord_Y+" "+coord_Z+" 0 0 0 0 0 0 0 0 "+life_periods[cell_counter]);
                                 
                                 bw1.newLine();
                                 bw1.flush();
                                 bw.newLine();
                                 bw.flush();
                            }
                        }
                        
                        // TEST
                        test_sum = intragran_cell_number + spec_bound_cell_number + intergran_cell_number + 
                                   spec_bound_intergran_cell_number + gr_bound_reg_cell_number + spec_bound_gr_bound_reg_cell_number;
                                 
                        System.out.println("Number of intragranular cells in the interior of specimen:       "+intragran_cell_number);
                        System.out.println("Number of intragranular cells at specimen boundary:              "+spec_bound_cell_number);
                                 
                        System.out.println("Number of intergranular cells in the interior of specimen:       "+intergran_cell_number);
                        System.out.println("Number of intergranular cells at specimen boundary:              "+spec_bound_intergran_cell_number);
                                     
                        System.out.println("Number of cells near grain boundary in the interior of specimen: "+gr_bound_reg_cell_number);
                        System.out.println("Number of cells near grain boundary at specimen boundary:        "+spec_bound_gr_bound_reg_cell_number);
                                 
                        System.out.println("Total number of cells:                                           "+frSpecSize+" = "+test_sum);
                        
                        bw.close();
                        bw1.close();

                        System.out.println();
                        System.out.println("Name of created file: "+writeFileName);
                        System.out.println("Name of created file: "+writeFileName_2);
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }
                    
                    // TEST
                    thirdDate = new Date();
                    timePeriod = thirdDate.getTime() - secondDate.getTime();
                    fileCreationPeriod = fileCreationPeriod + timePeriod;
                }
                
                // TEST
                thirdDate = new Date();
                    
                // Prolongation of time cycle if dendritic growth is going on
                if(!specimen_is_built)
             //   if(damagedCellsCounter == 0)
                if(time_step_counter >= stepNumber-10)
                if(totalSizeOfClusters < frSpecSize)// & totalSizeOfClusters > frSpecSize*0.99 )
                {
                    empty_cells_number = frSpecSize - totalSizeOfClusters;
                    
                    stepCounter = stepNumber-2;
                    System.out.println(" Specimen is not built!!!");

                    // Determination of presence of "empty" cells
                 //   if(false)
                    for(int cell_counter = 0; cell_counter<frSpec.size(); cell_counter++)
                    {
                        grain_index = frSpec.get(cell_counter);

                        if(grain_index == GrainsHCPCommon.UNDAMAGED_CELL)
                        {
                            neighb_grain_indices = new ArrayList(0);
                            specimen_is_built = false;
                       //     System.out.println(" Cell # "+cell_counter+" is empty. ");

                            // Joining of this empty cell to one of adjacent clusters
                            // Array of indices of neighbours
                        //    neighbours3D[cell_counter] = calcNeighbours3D(cell_counter, cell_number_I, cell_number_J, cell_number_K);

                            // Creation of list of indices of adjacent grains
                            for(int neighb_counter = 0; neighb_counter<12; neighb_counter++)
                            if(neighbours3D[cell_counter][neighb_counter]>-1)
                            {
                                neighb_grain_index = frSpec.get(neighbours3D[cell_counter][neighb_counter]);

                           //     System.out.println("Grain of neighbour #"+neighb_counter+": "+neighb_grain_index);

                                if(neighb_grain_index != GrainsHCPCommon.UNDAMAGED_CELL)
                                    neighb_grain_indices.add(neighb_grain_index);
                            }

                        //    System.out.print("Adjacent grains: "+neighb_grain_indices.toString());

                            // Stochastic choice of grain index of current cell
                            if(neighb_grain_indices.size()>0)
                            {
                                gr_ind_counter = (int)Math.floor(neighb_grain_indices.size()*Math.random());

                                if(gr_ind_counter == neighb_grain_indices.size())
                                    gr_ind_counter--;

                                grain_index = (Integer)neighb_grain_indices.get(gr_ind_counter);
                                frSpec.set(cell_counter, grain_index);
                          //      System.out.println(" Cell # "+cell_counter+" belongs to grain # "+grain_index);
                                totalSizeOfClusters++;
                            }
                            else
                                System.out.println(" Cell # "+cell_counter+" is still empty.");
                        }
                    }
                    
                //    System.out.println("time_step_counter = "+time_step_counter+"; totalSizeOfClusters = "+totalSizeOfClusters+"; frSpecSize = "+frSpecSize);
                }
                
                // TEST
                finishDate = new Date();
                timePeriod = finishDate.getTime() - thirdDate.getTime();
                emptyCellsJoiningPeriod = emptyCellsJoiningPeriod + timePeriod;
                
                timePeriod = finishDate.getTime() - startDate.getTime();
                totalPeriod = totalPeriod + timePeriod;

                if(totalSizeOfClusters == frSpecSize)
                {   
                    System.out.println("time_step_counter = "+time_step_counter+"; totalSizeOfClusters = "+totalSizeOfClusters+"; frSpecSize = "+frSpecSize);
                    System.out.println("SPECIMEN is built!!!");
                }
                else
                {
                    System.out.println("time_step_counter = "+time_step_counter+". totalSizeOfClusters = "+totalSizeOfClusters+"; frSpecSize = "+frSpecSize);
                    System.out.println("Maximal length of grain: "+max_gr_vec_length);
                    System.out.println("=====================+++++++++++++++++++++=====================");
                }
                //----------- end of time step --------------------
            }

            
            System.out.println("Number of steps of specimen building: "+time_step_counter);
            
            // TEST
            System.out.println("Period of joining of "+(frSpecSize - empty_cells_number)+" cells to grains:           "+clusterNumberCalcPeriod+" ms.");
            System.out.println("Period of calculation of grain cluster sizes:   "+clusterSizeCalcPeriod+" ms.");
            System.out.println("Period of file creation:                        "+fileCreationPeriod+" ms.");
            System.out.println("Period of joining of "+empty_cells_number+" empty cells to grains:     "+emptyCellsJoiningPeriod+" ms.");
            System.out.println("Total period of specimen building:                "+totalPeriod+" ms.");
            System.out.println("Maximal length of grain: "+max_gr_vec_length);
            
            System.out.println("=====================+++++++++++++++++++++++=================");
            System.out.println("Periods in calcClusterNumber(...)");
            
            for(int counter = 0; counter < time_period.length; counter++)
                System.out.println("Period # "+counter+": "+time_period[counter]);
            
            // Initial number of colours of grains
            int colour_number = 16;//4;//3;//8;//0;//
            
            // TEST
            startDate = new Date();

            System.out.println("\nInitial number of colours: " + colour_number+"\n");
            
            // Minimal necessary number of colours
         //   int necessary_col_number = 0;

            // Current number of colours
        //    int current_col_number = colour_number;

          /*
            while(colour_number!=necessary_col_number)
            {
                colour_number = current_col_number;

                // The method distributes colours among clusters of cells in optimal way.
                necessary_col_number = setClusterColoursOpt(colour_number);
                System.out.println("\nThe necessary number of colours: " + necessary_col_number+"\n");
                current_col_number = necessary_col_number;
            }
          */
            
            // The method distributes colours among clusters of cells in optimal way.
            boolean error_of_det_colours = true; // setClusterColoursOpt(colour_number); // 

            System.out.println("total_grain_number = "+total_grain_number);
            
            if(total_grain_number == 0)// 1)//
              error_of_det_colours = false;
            
            while(error_of_det_colours)
            {
                colour_number++;
                System.out.println("\nCurrent number of colours: " + colour_number+"\n");
                error_of_det_colours = setClusterColoursOpt(colour_number);// false; //
                
                if(colour_number >= 16)
                    error_of_det_colours = false;
                    
                System.out.println("\nCurrent number of colours: " + colour_number+"\n");
            }
            
            System.out.println("\nNecessary number of colours: " + colour_number+"\n");
            
            // TEST
            finishDate = new Date();
            timePeriod = finishDate.getTime() - startDate.getTime();
            System.out.println("Period of determination of cluster colours: "+timePeriod+" ms.");
        }
        
        /** The method calculates the distance between two cells (in cell diameters)
         * @param index1 single index of 1st cell
         * @param index2 single index of 2nd cell
         * @return the distance between two cells
         */
        private double calcDistance(int index1, int index2)
        {
            // Triple index of 1st cell
            Three tripleIndex1 = calcTripleIndex(index1, cell_number_I, cell_number_J, cell_number_K);
            
            // Triple index of 2nd cell
            Three tripleIndex2 = calcTripleIndex(index2, cell_number_I, cell_number_J, cell_number_K);
            
            // Coordinates of 1st cell
            VectorR3 coordinates1 = calcCoordinates(tripleIndex1.getI(), tripleIndex1.getJ(), tripleIndex1.getK());
            
            // Coordinates of 2nd cell
            VectorR3 coordinates2 = calcCoordinates(tripleIndex2.getI(), tripleIndex2.getJ(), tripleIndex2.getK());
            
            // Residial vector for radius-vectors of two cells
            VectorR3 resid_vector = residial(coordinates1, coordinates2);
            
            // The length of residial vector
            double distance = resid_vector.getLength();
            
            return distance;
        }
        
        /** THe method calculates the index of layer containing grain with certain index.
         * @param grain_index index of grain from the interval (1; grain_number)
         * @param layer_grain_numbers array of numbers of grains in each layer
         * @return the index of layer containing grain with certain index
         */
        private int calcLayerIndex(int grain_index, int[] layer_grain_numbers)
        {
            int layer_index = -1;
            int sum = 0;
            
            for(int layer_counter = 0; layer_counter < layer_grain_numbers.length; layer_counter++)
              if(grain_index > sum & grain_index <= sum + layer_grain_numbers[layer_counter])
              {
                layer_index = layer_counter + 1;
                layer_counter = layer_grain_numbers.length;
              }
              else
                sum += layer_grain_numbers[layer_counter];
            
            return layer_index;
        }
        
        /** The constructor generates 3-layer specimen.
         * @param create_layers variable turning on creation of surface layers
         * @param layer2_type type of intermediate layer
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of intermediate layer
         * @param _completed_steps text area
         */
        public GrainsHCPTask(boolean create_layers, byte layer2_type, double layer1_thickness, double layer2_thickness,
                             double layer3_thickness, double layer2_elem_size, TextArea _completed_steps, boolean var)
        {

            /* Block 20 continue */

            // Reading of task parameters
            readTask();

            completed_steps = _completed_steps;

           // Creation of two specimens without embryos of cracks
            System.out.println(GrainsHCPCommon.CREATION_OF_SPECIMENS);
            frSpec   = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
        //  hotSpec = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

            // Specimen with cells, which must be joined to clusters at next time step
            frSpecFuture = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecSize   = frSpec.size();

            // Setting of coordinates of vectors from cell to its neighbours at 1st coordination sphere
            setNeighb3D1SVectors();

            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            neighbours3D = new int[frSpecSize][54];

            //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
            // of each cell of specimen
            for(int cell_counter = 0; cell_counter<frSpecSize; cell_counter++)
            {
                setNeighbours3D(cell_counter);
            }

            // Setting of triple indices of all cells contained in specimen
            setTripleIndices();

           // List of clusters containing cells in the state "1"
         // clusterList = new ArrayList(0);

           // Cluster containing all cells in the state "1"
//            largeCluster = new Cluster();

            // The number of time steps
            stepNumber = LoadProperties.STEP_NUMBER;

            // The probability of generation of embryo
            probGeneration = LoadProperties.GENERATION_PROBABILITY;

         // Stochastic distribution of crack embryos in specimen (time step #0)
            System.out.println();
            System.out.println(GrainsHCPCommon.GENERATION_OF_EMBRYOS);

            // Thickness of upper surface layer where embryos cannot be generated
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;

      //      embryo_distribution_type = LoadProperties.STOCH_EMBRYOS_IN_CUBES;

            // Thickness of 1st substrate layer
            double substrate_layer_1_thick = (specimen_size_Z - layer_thickness);//*0.5;//0;//

            // TEST
            System.out.println("Total thickness of layers: "+layer_thickness);
            System.out.println("frSpecSize = "+frSpecSize);
            System.out.println("specimen_size_Z = "+specimen_size_Z);

            embryos_number_1 = 0;

            if(embryo_distribution_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
                System.out.println();
                System.out.println("Method of embryos distribution: STOCHASTIC_EMBRYOS");
                // generateEmbryos(layer_thickness);
             //   generateEmbryos(layer_thickness, substrate_layer_1_thick);
                generateEmbryos(layer1_thickness, layer2_thickness, layer3_thickness, substrate_layer_1_thick);
            }

            if(embryo_distribution_type==LoadProperties.REGULAR_EMBRYOS)
            {
                System.out.println();
                System.out.println("Method of embryos distribution: REGULAR_EMBRYOS");
                // generateEmbryosRegularly(layer_thickness);
                generateEmbryosRegularly(layer_thickness, substrate_layer_1_thick);
            }

            if(embryo_distribution_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {
                System.out.println();
                System.out.println("Method of embryos distribution: STOCH_EMBRYOS_IN_CUBES");
                // generateEmbryosRandomly(layer_thickness);
                generateEmbryosRandomly(layer_thickness, substrate_layer_1_thick);
            }

            /* Block 21 */

            //   generateEmbryosOnFacets();

            // Fields for calculation of time periods of program execution
            Date startDate;
            Date firstDate;
            Date secondDate;
            Date thirdDate;
            Date finishDate;
            long timePeriod;
            long totalPeriod = 0;
            long clusterNumberCalcPeriod = 0;

     //     int clusterListSize;
            int damagedCellsCounter;
            int time_step_counter = 0;

            // Number of cluster of current cell at next time step
            int cell_cluster_number = undamaged_cell;
            
            boolean calc_anis_cosinus = true;

            // Propagation of cracks (dendrites)
            for (int stepCounter = 1; stepCounter < stepNumber+1; stepCounter++)
            {
                //----- start of time step -------
           /*
                if(stepCounter == 1)
                    writeDamagedNeighbours = true;
                else
                    writeDamagedNeighbours = false;
            */

                startDate = new Date();
                time_step_counter++;
                clusterNumberCalcPeriod = 0;

                // Cell coordinates
                VectorR3 coordinates;

               /** Calculation of number of damaged neighbours at each of 3 coordination spheres
                * of every undamaged cell and determination of the probability of damage of every undamaged cell
                */
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    firstDate = new Date();

                    // Calculation of triple index of cell
                    indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

                    // Calculation of coordinates of cell
                    coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());

                    /** If the cell is located in substrate (not in surface layers) and its state is "0"
                     * then the number of cluster that will contain the cell is calculated.
                     */
                //    if(coordinates.getZ()>=layer_thickness)
                    if(frSpec.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();

                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand > LoadProperties.GROWTH_PROBABILITY*min_value)&
                           (long_rand < LoadProperties.GROWTH_PROBABILITY*max_value))
                          clusterNumber = calcClusterNumber(frSpec, cell_counter, calc_anis_cosinus);
                        else
                          clusterNumber = undamaged_cell;
                    }

                    //   Calculation of time period of determination of numbers of clusters,
                    // which will contain cells at next time step
                    timePeriod = (new Date()).getTime() - firstDate.getTime();
                    clusterNumberCalcPeriod = clusterNumberCalcPeriod + timePeriod;

                    // If the the cell should be joined to one of clusters
                    // then the cell is joined to this cluster at the end of current time step.
                    // The number of the cell is kept in temporary massive responsible for indices of cells
                    // joined to corresponding cluster at current time step.
                    if (clusterNumber != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture.set(cell_counter, clusterNumber);
                }

                //   TEST
                firstDate = new Date();
                timePeriod = firstDate.getTime() - startDate.getTime();

            //    clusterListSize = clusterList.size();
                damagedCellsCounter = 0;

                // Change of states of cells
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cell_cluster_number = frSpecFuture.get(cell_counter);

                    if(cell_cluster_number != undamaged_cell)
    /* =true */     if(frSpec.get(cell_counter) == undamaged_cell)
                    {
                        frSpec.set(cell_counter, cell_cluster_number);
          //            hotSpec.set(cell_counter, time_step_counter);
                        damagedCellsCounter++;
                    }

                    frSpecFuture.set(cell_counter, undamaged_cell);
                }

           //   TEST
                secondDate = new Date();
                timePeriod = secondDate.getTime() - firstDate.getTime();

           //   TEST
                thirdDate = new Date();
                timePeriod = thirdDate.getTime() - secondDate.getTime();

                // Number of cells in current cluster
                int clusterSize = 0;

                // Total number of cells in all clusters
                int totalSizeOfClusters = 0;

           //     int largeClusterSize = largeCluster.size();

                // Array of cluster sizes
                // (i-th element of array equals number of cells in i-th cluster, i>0)
                int[] cluster_sizes = new int[embryos_number+1];

                // Index of cluster containing current cell
                int cluster_index = 0;

                // Calculation of cluster sizes
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cluster_index = frSpec.get(cell_counter);

               //   if(cluster_index != 0)
                    if(cluster_index >= 0)
                        if(cluster_index < embryos_number+1)
                            cluster_sizes[cluster_index]++;
                        else
                            System.out.println(GrainsHCPCommon.ERROR_1);
                }

                // Printing of values of cluster sizes
                for (cluster_index = 1; cluster_index <= embryos_number; cluster_index++)
                {
                    clusterSize = cluster_sizes[cluster_index];

                    totalSizeOfClusters = totalSizeOfClusters + clusterSize;
                }

                // End of time cycle if all cells are joined to clusters
                if(totalSizeOfClusters == frSpecSize)
                    stepCounter = stepNumber;

                System.out.println("<3> "+GrainsHCPCommon.TOTAL_NUMBER_OF_CELLS + totalSizeOfClusters+
                                   "; frSpecSize = "+frSpecSize+"; stepCounter = "+stepCounter+"; stepNumber = "+stepNumber);

          //      System.out.println(GrainsHCPCommon.NUMBER_OF_SWITCHED_CELLS+time_step_counter+": "+damagedCellsCounter);

                // Reduction of time cycle if there are no cells joined to clusters at current time step
                if(damagedCellsCounter == 0)//totalSizeOfClusters*0.001)
                    stepCounter = stepCounter + Math.max(1, (stepNumber - stepCounter)/2);

                // Printing of data about every z-plane of cells in the file
                double coord_X = -1; //
                double coord_Y = -1; // Coordinates of cell
                double coord_Z = -1; //

                VectorR3 cell_coordinates;  // Three coordinates of cell

                int state = -1;
                int damageCounter = 0;
            //    int totalDamageNumber = 0;

                if((time_step_counter%(stepNumber/GrainsHCPCommon.OUTPUT_FILES_NUMBER) == 0) |
                   (totalSizeOfClusters == frSpecSize) | (stepCounter > stepNumber))
           //     for (int z_plane = 0; z_plane < cell_number_K; z_plane++)
                {
                    try
                    {
                     //   bw = new BufferedWriter(new FileWriter(writeFileName+"_"+time_step_counter+".txt"));
                        bw = new BufferedWriter(new FileWriter(writeFileName));
                        bw.write("#  The file contains information about initial conditions:");
                        bw.newLine();
                        bw.write("# in 1st string - total number of inner cellular automata,");
                        bw.newLine();
                        bw.write("# in 2nd string - total number of grains and the number of grains in 1st substrate layer");
                        bw.newLine();
                        bw.write("# in each further string - grain index of corresponding inner CA");
                        bw.newLine();
                        bw.write("# and location type of corresponding inner CA in grain and in specimen:");
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
                        bw.write(frSpecSize+"\n");
                        bw.write(embryos_number+phase_number-1+" "+embryos_number_1+"\n");

                        bw1= new BufferedWriter(new FileWriter(writeFileName_2));
                        bw1.write("#  The file contains information about initial geometry:");
                        bw1.newLine();
                        bw1.write("# in 1st string - total number of inner cellular automata,");
                        bw1.newLine();
                        bw1.write("# specimen sizes along axes X, Y, Z expressed in radiuses of cell");
                        bw1.newLine();
                        bw1.write("# and number of grains;");
                        bw1.newLine();
                        bw1.write("# in each further string - grain index of corresponding inner CA, its coordinates");
                        bw1.newLine();
                        bw1.write("# and location type of corresponding inner CA in grain and in specimen:");
                        bw1.newLine();
                        bw1.write("# 2 - outer boundary CA,");
                        bw1.newLine();
                        bw1.write("# 3 - intragranular CA in the interior of specimen,");
                        bw1.newLine();
                        bw1.write("# 4 - intergranular CA in the interior of specimen,");
                        bw1.newLine();
                        bw1.write("# 5 - intragranular CA on specimen boundary,");
                        bw1.newLine();
                        bw1.write("# 6 - intergranular CA on specimen boundary.");
                        bw1.newLine();
                        bw1.write(frSpecSize+" "+
                                  LoadProperties.SPECIMEN_SIZE_X+" "+
                                  LoadProperties.SPECIMEN_SIZE_Y+" "+
                                  LoadProperties.SPECIMEN_SIZE_Z);

                        if(show_grain_bounds != 0)
                            bw1.write(" "+(embryos_number+phase_number-1));

                        bw1.newLine();
                    //    bw1.write(embryos_number+"");
                    //    bw1.newLine();
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }

                    // System.out.println("The number of cells changed its state at "+time_step_counter+" time step: "+damagedCellsCounter);

                    int[] neighb1S_indices = new int[12];
                    int neghb1S_state = -1;
                    boolean grain_boundary_cell = false;
                    boolean inner_boundary_cell = false;

                    if(create_layers)
                    {
                        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                        {
                            layer1_thickness = layer1_thickness*radius_SCP/radius_HCP;
                            layer2_thickness = layer2_thickness*radius_SCP/radius_HCP;
                            layer3_thickness = layer3_thickness*radius_SCP/radius_HCP;
                            layer2_elem_size = layer2_elem_size*radius_SCP/radius_HCP;
                            
                            System.out.println("specimen_size_X = "+specimen_size_X);
                            System.out.println("specimen_size_Y = "+specimen_size_Y);
                            System.out.println("specimen_size_Z = "+specimen_size_Z);
                            
                            System.out.println("layer1_thickness = "+layer1_thickness);
                            System.out.println("layer2_thickness = "+layer2_thickness);
                            System.out.println("layer3_thickness = "+layer3_thickness);
                            System.out.println("layer2_elem_size = "+layer2_elem_size);
                        }
                        
                        if(layer2_type == Common.SIMPLE_LAYER)
                        {
                         //   if(false)
                            createInitCondSimpleFile(layer1_thickness, layer2_thickness, layer3_thickness);

                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SIMPLE_LAYER");
                            System.out.println("Type of intermediate layer: SIMPLE_LAYER");
                        }

                        if(layer2_type == Common.TRIANGLE_GOFR)
                        {
                            createInitCondTriangleGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: TRIANGLE_GOFR");
                            System.out.println("Type of intermediate layer: TRIANGLE_GOFR");
                        }

                        if(layer2_type == Common.SQUARE_GOFR)
                        {
                            createInitCondSquareGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SQUARE_GOFR");
                            System.out.println("Type of intermediate layer: SQUARE_GOFR");
                        }

                        if(layer2_type == Common.PYRAMIDES)
                        {
                            createInitCondPyramidesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: PYRAMIDES");
                            System.out.println("Type of intermediate layer: PYRAMIDES");
                        }

                        if(layer2_type == Common.CHESS_CUBES)
                        {
                            createInitCondChessCubesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: CHESS_CUBES");
                            System.out.println("Type of intermediate layer: CHESS_CUBES");
                        }
                    }
                            
                    if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                    {
                        // Transition of specimen to simple cubic packing
                        showSpecimenInSCP();
                    }

                    if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                    for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                    {
                        state = frSpec.get(cell_counter);

                        // Determination of current cell indices
                        indices = cell_indices[cell_counter];

                        //System.out.println(cell_counter+" "+indices.getI() +" "+indices.getJ()+" "+indices.getK());

                        index1 = indices.getI();
                        index2 = indices.getJ();
                        index3 = indices.getK();

                        // Printing of coordinates and states of cells
                    //    if (index3 == z_plane)
                    //    {
                            cell_coordinates = calcCoordinates(index1, index2, index3);

                            coord_X = cell_coordinates.getX();
                            coord_Y = cell_coordinates.getY();
                            coord_Z = cell_coordinates.getZ();

                            grain_boundary_cell = false;
                            inner_boundary_cell = false;

                            if(state != undamaged_cell)
                            {
                                if(show_grain_bounds != 0)
                                {
                                    // Single indices of cells at 1st coordination sphere of current "central" cell
                                    neighb1S_indices = neighbours3D[cell_counter];

                                    // Checking of states of cells at 1st coordination sphere of current "central" cell
                                    for(int neihb1S_counter = 0; neihb1S_counter<12; neihb1S_counter++)
                                    {
                                        if(neighb1S_indices[neihb1S_counter]>-1)
                                        {
                                            // State of neighbour cell
                                            neghb1S_state = frSpec.get(neighb1S_indices[neihb1S_counter]);

                                            // If state of neighbour cell differs from state of current "central" cell
                                            //then this "central" cell is boundary cell
                                            if(neghb1S_state != state)
                                                grain_boundary_cell = true;
                                        }
                                        else
                                        {
                                            inner_boundary_cell = true;
                                        }
                                    }
                                }

                                try
                                {
                                    bw.write(/*"0.0 0.0 300.0 "*/state+"");
                                    bw1.write(cell_counter+" "+state+" "+coord_X+" "+coord_Y+" "+coord_Z);

                                    if(show_grain_bounds!=0)
                                    {
                                        if((grain_boundary_cell)&(inner_boundary_cell))
                                            bw1.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell)&(!inner_boundary_cell))
                                            bw1.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell)&(inner_boundary_cell))
                                            bw1.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell)&(!inner_boundary_cell))
                                            bw1.write(" "+Common.INTRAGRANULAR_CELL);

                                        if((grain_boundary_cell)&(inner_boundary_cell))
                                            bw.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell)&(!inner_boundary_cell))
                                            bw.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell)&(inner_boundary_cell))
                                            bw.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell)&(!inner_boundary_cell))
                                            bw.write(" "+Common.INTRAGRANULAR_CELL);
                                    }

                                    bw1.newLine();
                                    bw1.flush();
                                    bw.newLine();
                                    bw.flush();
                                }
                                catch(IOException io_exc)
                                {
                                    System.out.println("Error: " + io_exc);
                                }
                                
                                damageCounter++;
                            }
                    //    }
                    }

                    try
                    {
                        bw.close();
                        bw1.close();

                        System.out.println();
                        System.out.println("Name of created file: "+writeFileName);
                        System.out.println("Name of created file: "+writeFileName_2);
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }
                }

                // Prolongation of time cycle if dendritic growth is going on
                if(stepCounter > stepNumber-10)
                if(totalSizeOfClusters < frSpecSize)
                    stepCounter = stepCounter-2;

                //   TEST
                finishDate = new Date();
                timePeriod = finishDate.getTime() - thirdDate.getTime();

                totalPeriod = totalPeriod + timePeriod;
                //----------- end of time step --------------------
            }
        }

        /** The constructor generates 3-layer specimen.
         * @param create_layers variable turning on creation of surface layers
         * @param layer2_type type of intermediate layer
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of intermediate layer
         * @param grain_layer1_thick
         * grain_layer2_thick
         * @param _completed_steps text area
         */
        public GrainsHCPTask(boolean create_layers, byte layer2_type, double layer1_thickness,
                             double layer2_thickness, double layer3_thickness,
                             double grain_layer1_thick, double grain_layer2_thick,
                             double layer2_elem_size, TextArea _completed_steps)
        {

            /* Block 20 continue */

            // Reading of task parameters
            readTask();

            completed_steps = _completed_steps;

           // Creation of two specimens without embryos of cracks
            System.out.println(GrainsHCPCommon.CREATION_OF_SPECIMENS);
            frSpec   = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
        //  hotSpec = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

            // Specimen with cells, which must be joined to clusters at next time step
            frSpecFuture = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecSize   = frSpec.size();

            // Setting of coordinates of vectors from cell to its neighbours at 1st coordination sphere
            setNeighb3D1SVectors();

            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            neighbours3D = new int[frSpecSize][54];

            //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
            // of each cell of specimen
            for(int cell_counter = 0; cell_counter<frSpecSize; cell_counter++)
            {
                setNeighbours3D(cell_counter);
            }

            // Setting of triple indices of all cells contained in specimen
            setTripleIndices();

           // List of clusters containing cells in the state "1"
         // clusterList = new ArrayList(0);

           // Cluster containing all cells in the state "1"
//            largeCluster = new Cluster();

            // The number of time steps
            stepNumber = LoadProperties.STEP_NUMBER;

            // The probability of generation of embryo
            probGeneration = LoadProperties.GENERATION_PROBABILITY;

         // Stochastic distribution of crack embryos in specimen (time step #0)
            System.out.println();
            System.out.println(GrainsHCPCommon.GENERATION_OF_EMBRYOS);
            
            // Thickness of upper surface layer where embryos cannot be generated
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;
            
            // TEST
            //      embryo_distribution_type = LoadProperties.STOCH_EMBRYOS_IN_CUBES;
            
            // Thickness of 1st substrate layer
            double substrate_layer_1_thick = (specimen_size_Z - layer_thickness);//*0.5;//

            System.out.println("frSpecSize = "+frSpecSize);

            embryos_number_1 = 0;

            if(embryo_distribution_type==LoadProperties.STOCHASTIC_EMBRYOS)
            {
                System.out.println();
                System.out.println("Method of embryos distribution: STOCHASTIC_EMBRYOS");
                // generateEmbryos(layer_thickness);
                generateEmbryos(layer_thickness, substrate_layer_1_thick);
            }

            if(embryo_distribution_type==LoadProperties.REGULAR_EMBRYOS)
            {
                System.out.println();
                System.out.println("Method of embryos distribution: REGULAR_EMBRYOS");
                // generateEmbryosRegularly(layer_thickness);
                generateEmbryosRegularly(layer_thickness, substrate_layer_1_thick);
            }
            
            if(embryo_distribution_type==LoadProperties.STOCH_EMBRYOS_IN_CUBES)
            {
                System.out.println();
                System.out.println("Method of embryos distribution: STOCH_EMBRYOS_IN_CUBES");
                // generateEmbryosRandomly(layer_thickness);
                generateEmbryosRandomly(layer_thickness, substrate_layer_1_thick);
            }
            
            /* Block 21 */

            //   generateEmbryosOnFacets();

            // Fields for calculation of time periods of program execution
            Date startDate;
            Date firstDate;
            Date secondDate;
            Date thirdDate;
            Date finishDate;
            long timePeriod;
            long totalPeriod = 0;
            long clusterNumberCalcPeriod = 0;

     //     int clusterListSize;
            int damagedCellsCounter;
            int time_step_counter = 0;

            // Number of cluster of current cell at next time step
            int cell_cluster_number = undamaged_cell;
            
            boolean calc_anis_cosinus = true;

            // Propagation of cracks (dendrites)
            for (int stepCounter = 1; stepCounter < stepNumber+1; stepCounter++)
            {
                //----- start of time step -------
           /*
                if(stepCounter == 1)
                    writeDamagedNeighbours = true;
                else
                    writeDamagedNeighbours = false;
            */

                startDate = new Date();
                time_step_counter++;
                clusterNumberCalcPeriod = 0;

                // Cell coordinates
                VectorR3 coordinates;

               /** Calculation of number of damaged neighbours at each of 3 coordination spheres
                * of every undamaged cell and determination of the probability of damage of every undamaged cell
                */
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    firstDate = new Date();

                    // Calculation of triple index of cell
                    indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

                    // Calculation of coordinates of cell
                    coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());

                    /** If the cell is located in substrate (not in surface layers) and its state is "0"
                     * then the number of cluster that will contain the cell is calculated.
                     */
                    if(coordinates.getZ()>=layer_thickness)
                    if(frSpec.get(cell_counter) == undamaged_cell)
                    {
                        rand = new Random();
                        long_rand = rand.nextLong();

                        // If probabilistic condition of damage is fulfilled
                        // then the number of cluster that will contain the cell is calculated.
                        if((long_rand > LoadProperties.GROWTH_PROBABILITY*min_value)&
                           (long_rand < LoadProperties.GROWTH_PROBABILITY*max_value))
                          clusterNumber = calcClusterNumber(frSpec, cell_counter, calc_anis_cosinus);
                        else
                          clusterNumber = undamaged_cell;
                    }

                    //   Calculation of time period of determination of numbers of clusters,
                    // which will contain cells at next time step
                    timePeriod = (new Date()).getTime() - firstDate.getTime();
                    clusterNumberCalcPeriod = clusterNumberCalcPeriod + timePeriod;

                    // If the the cell should be joined to one of clusters
                    // then the cell is joined to this cluster at the end of current time step.
                    // The number of the cell is kept in temporary massive responsible for indices of cells
                    // joined to corresponding cluster at current time step.
                    if (clusterNumber != undamaged_cell)
                        // Setting of new states of cells switched at current time step
                        frSpecFuture.set(cell_counter, clusterNumber);
                }
           /*
                System.out.println();
                System.out.println(GrainsHCPCommon.CALCULATION_PERIOD_1 + clusterNumberCalcPeriod);
                System.out.println();
             */
                //   TEST
                firstDate = new Date();
                timePeriod = firstDate.getTime() - startDate.getTime();
          /*
                System.out.println();
                System.out.println(GrainsHCPCommon.CALCULATION_PERIOD_2 + time_step_counter + ": " + timePeriod);
                System.out.println();
           //   END OF TEST

                System.out.println(GrainsHCPCommon.DETERMINATION_OF_NEW_CELL_STATES);
            */
            //    clusterListSize = clusterList.size();
                damagedCellsCounter = 0;

                // Change of states of cells
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cell_cluster_number = frSpecFuture.get(cell_counter);

                    if(cell_cluster_number != undamaged_cell)
    /* =true */     if(frSpec.get(cell_counter) == undamaged_cell)
                    {
                        frSpec.set(cell_counter, cell_cluster_number);
          //            hotSpec.set(cell_counter, time_step_counter);
                        damagedCellsCounter++;
                    }

                    frSpecFuture.set(cell_counter, undamaged_cell);
                }

           //   TEST
                secondDate = new Date();
                timePeriod = secondDate.getTime() - firstDate.getTime();
           /*
                System.out.println();
                System.out.println(GrainsHCPCommon.CALCULATION_PERIOD_3 + time_step_counter + ": " + timePeriod);
                System.out.println();
            */
           //   END OF TEST

           /*
                try
                {
                    bw = new BufferedWriter(new FileWriter(writeFileName+"_"+stepCounter+".txt"));
                    bw.newLine();
                    bw.write("The number of cells changed its state: " + damagedCellsCounter);
                    bw.newLine();
                    bw.newLine();
                    System.out.println("The number of cells changed its state: " + damagedCellsCounter);
                }
                catch(IOException io_exc)
                {
                    System.out.println("Error: " + io_exc);
                }
           */

                // Addition of indices of cells damaged at current time step to clusters
        /*        for(int cluster_counter = 0; cluster_counter<clusterListSize; cluster_counter++)
                {
                    tempCluster = (Cluster)newClusters.get(cluster_counter);
                    cluster = (Cluster)clusterList.get(cluster_counter);

                    for (int cell_counter = 0; cell_counter<tempCluster.size(); cell_counter++)
                    {
                        cluster.add(tempCluster.get(cell_counter));
                    }
                }
         */
           //   TEST
                thirdDate = new Date();
                timePeriod = thirdDate.getTime() - secondDate.getTime();
              /*
                System.out.println();
                System.out.println(GrainsHCPCommon.CALCULATION_PERIOD_4 + time_step_counter + ": " + timePeriod);
                System.out.println();
               */
           //   END OF TEST

                // Number of cells in current cluster
                int clusterSize = 0;

                // Total number of cells in all clusters
                int totalSizeOfClusters = 0;

           //     int largeClusterSize = largeCluster.size();

                // Array of cluster sizes
                // (i-th element of array equals number of cells in i-th cluster, i>0)
                int[] cluster_sizes = new int[embryos_number+1];

                // Index of cluster containing current cell
                int cluster_index = 0;

                // Calculation of cluster sizes
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    cluster_index = frSpec.get(cell_counter);

                    // TEST
        //          if(cluster_index != 0)
        //              System.out.println("Cell # "+cell_counter+" belongs to cluster # "+cluster_index);
                    // END OF TEST

               //   if(cluster_index != 0)
                    if(cluster_index >= 0)
                        if(cluster_index < embryos_number+1)
                            cluster_sizes[cluster_index]++;
                        else
                            System.out.println(GrainsHCPCommon.ERROR_1);
                }

                // Printing of values of cluster sizes
                for (cluster_index = 1; cluster_index <= embryos_number; cluster_index++)
                {
                    clusterSize = cluster_sizes[cluster_index];

               //     System.out.println(GrainsHCPCommon.NUMBER_OF_CELLS + cluster_index + ": "+clusterSize);
                    totalSizeOfClusters = totalSizeOfClusters + clusterSize;
                }

                // End of time cycle if all cells are joined to clusters
                if(totalSizeOfClusters == frSpecSize)
                    stepCounter = stepNumber;

                System.out.println("<4> "+GrainsHCPCommon.TOTAL_NUMBER_OF_CELLS + totalSizeOfClusters+
                                   "; frSpecSize = "+frSpecSize+"; stepCounter = "+stepCounter+"; stepNumber = "+stepNumber);

          //      System.out.println(GrainsHCPCommon.NUMBER_OF_SWITCHED_CELLS+time_step_counter+": "+damagedCellsCounter);

                // Reduction of time cycle if there are no cells joined to clusters at current time step
                if(damagedCellsCounter == 0)//totalSizeOfClusters*0.001)
                    stepCounter = stepCounter + Math.max(1, (stepNumber - stepCounter)/2);

                // TEST
            //    System.out.println();
            //    System.out.println(GrainsHCPCommon.STEP_COUNTER_EQUALS+stepCounter);

                // Printing of data about every z-plane of cells in the file
                double coord_X = -1; //
                double coord_Y = -1; // Coordinates of cell
                double coord_Z = -1; //

                VectorR3 cell_coordinates;  // Three coordinates of cell

                int state = -1;
                int damageCounter = 0;
            //    int totalDamageNumber = 0;

                if(time_step_counter%(stepNumber/GrainsHCPCommon.OUTPUT_FILES_NUMBER) == 0 |
                   totalSizeOfClusters == frSpecSize | 
                   stepCounter > stepNumber)
           //     for (int z_plane = 0; z_plane < cell_number_K; z_plane++)
                {
                    try
                    {
                        if(totalSizeOfClusters != frSpecSize)
                          bw = new BufferedWriter(new FileWriter(GrainsHCPCommon.DEND_RESULT_DIRECTION+"/grains_"+time_step_counter+".txt"));
                        else
                          bw = new BufferedWriter(new FileWriter(writeFileName));
                        
                        
                        bw.write("#  The file contains information about initial conditions:");
                        bw.newLine();
                        bw.write("# in 1st string - total number of inner cellular automata,");
                        bw.newLine();
                        bw.write("# in 2nd string - total number of grains and the number of grains in 1st substrate layer");
                        bw.newLine();
                        bw.write("# in each further string - grain index of corresponding inner CA");
                        bw.newLine();
                        bw.write("# and location type of corresponding inner CA in grain and in specimen:");
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
                        
                        if(totalSizeOfClusters != frSpecSize)
                          bw.write(totalSizeOfClusters+"\n");
                        else
                          bw.write(frSpecSize+"\n");
                        
                        bw.write(embryos_number+phase_number-1+" "+embryos_number_1+"\n");

                        bw1= new BufferedWriter(new FileWriter(writeFileName_2));
                        bw1.write("#  The file contains information about initial geometry:");
                        bw1.newLine();
                        bw1.write("# in 1st string - total number of inner cellular automata,");
                        bw1.newLine();
                        bw1.write("# specimen sizes along axes X, Y, Z expressed in radiuses of cell");
                        bw1.newLine();
                        bw1.write("# and number of grains;");
                        bw1.newLine();
                        bw1.write("# in each further string - grain index of corresponding inner CA, its coordinates");
                        bw1.newLine();
                        bw1.write("# and location type of corresponding inner CA in grain and in specimen:");
                        bw1.newLine();
                        bw1.write("# 2 - outer boundary CA,");
                        bw1.newLine();
                        bw1.write("# 3 - intragranular CA in the interior of specimen,");
                        bw1.newLine();
                        bw1.write("# 4 - intergranular CA in the interior of specimen,");
                        bw1.newLine();
                        bw1.write("# 5 - intragranular CA on specimen boundary,");
                        bw1.newLine();
                        bw1.write("# 6 - intergranular CA on specimen boundary.");
                        bw1.newLine();
                        bw1.write(frSpecSize+" "+
                                  LoadProperties.SPECIMEN_SIZE_X+" "+
                                  LoadProperties.SPECIMEN_SIZE_Y+" "+
                                  LoadProperties.SPECIMEN_SIZE_Z);

                        if(show_grain_bounds != 0)
                            bw1.write(" "+(embryos_number+phase_number-1));

                        bw1.newLine();
                    //    bw1.write(embryos_number+"");
                    //    bw1.newLine();
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }

                    // System.out.println("The number of cells changed its state at "+time_step_counter+" time step: "+damagedCellsCounter);

                    int[] neighb1S_indices = new int[12];
                    int neghb1S_state = -1;
                    boolean grain_boundary_cell = false;
                    boolean inner_boundary_cell = false;

                    if(create_layers)
                    {
                        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                        {
                            layer1_thickness = layer1_thickness*radius_SCP/radius_HCP;
                            layer2_thickness = layer2_thickness*radius_SCP/radius_HCP;
                            layer3_thickness = layer3_thickness*radius_SCP/radius_HCP;
                            layer2_elem_size = layer2_elem_size*radius_SCP/radius_HCP;
                            
                            System.out.println("specimen_size_X = "+specimen_size_X);
                            System.out.println("specimen_size_Y = "+specimen_size_Y);
                            System.out.println("specimen_size_Z = "+specimen_size_Z);
                            
                            System.out.println("layer1_thickness = "+layer1_thickness);
                            System.out.println("layer2_thickness = "+layer2_thickness);
                            System.out.println("layer3_thickness = "+layer3_thickness);
                            System.out.println("layer2_elem_size = "+layer2_elem_size);
                        }
                        
                        if(layer2_type == Common.SIMPLE_LAYER)
                        {
                            createInitCondSimpleFile(layer1_thickness, layer2_thickness, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SIMPLE_LAYER");
                        }

                        if(layer2_type == Common.TRIANGLE_GOFR)
                        {
                            createInitCondTriangleGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: TRIANGLE_GOFR");
                        }

                        if(layer2_type == Common.SQUARE_GOFR)
                        {
                            createInitCondSquareGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SQUARE_GOFR");
                        }

                        if(layer2_type == Common.PYRAMIDES)
                        {
                            createInitCondPyramidesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: PYRAMIDES");
                        }

                        if(layer2_type == Common.CHESS_CUBES)
                        {
                            createInitCondChessCubesFile(layer1_thickness, layer2_thickness, layer2_elem_size, layer3_thickness);
                            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: CHESS_CUBES");
                        }
                    }

                    if(packing_type == Common.SIMPLE_CUBIC_PACKING)
                    {
                        // Transition of specimen to simple cubic packing
                        showSpecimenInSCP();
                    }

                    if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
                    for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                    {
                        state = frSpec.get(cell_counter);

                        // Determination of current cell indices
                        indices = cell_indices[cell_counter];

                        //System.out.println(cell_counter+" "+indices.getI() +" "+indices.getJ()+" "+indices.getK());

                        index1 = indices.getI();
                        index2 = indices.getJ();
                        index3 = indices.getK();

                        // Printing of coordinates and states of cells
                    //    if (index3 == z_plane)
                    //    {
                            cell_coordinates = calcCoordinates(index1, index2, index3);

                            coord_X = cell_coordinates.getX();
                            coord_Y = cell_coordinates.getY();
                            coord_Z = cell_coordinates.getZ();

                            grain_boundary_cell = false;
                            inner_boundary_cell = false;

                            if(state != undamaged_cell)
                            {
                                if(show_grain_bounds != 0)
                                {
                                    // Single indices of cells at 1st coordination sphere of current "central" cell
                                    neighb1S_indices = neighbours3D[cell_counter];

                                    // Checking of states of cells at 1st coordination sphere of current "central" cell
                                    for(int neihb1S_counter = 0; neihb1S_counter<12; neihb1S_counter++)
                                    {
                                        if(neighb1S_indices[neihb1S_counter]>-1)
                                        {
                                            // State of neighbour cell
                                            neghb1S_state = frSpec.get(neighb1S_indices[neihb1S_counter]);

                                            // If state of neighbour cell differs from state of current "central" cell
                                            //then this "central" cell is boundary cell
                                            if(neghb1S_state != state)
                                                grain_boundary_cell = true;
                                        }
                                        else
                                        {
                                            inner_boundary_cell = true;
                                        }
                                    }
                                }

                                try
                                {
                                    bw.write(/*"0.0 0.0 300.0 "*/state+"");
                                    bw1.write(state+" "+coord_X+" "+coord_Y+" "+coord_Z);

                                    if(show_grain_bounds!=0)
                                    {
                                        if((grain_boundary_cell)&(inner_boundary_cell))
                                            bw1.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell)&(!inner_boundary_cell))
                                            bw1.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell)&(inner_boundary_cell))
                                            bw1.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell)&(!inner_boundary_cell))
                                            bw1.write(" "+Common.INTRAGRANULAR_CELL);

                                        if((grain_boundary_cell)&(inner_boundary_cell))
                                            bw.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);

                                        if((grain_boundary_cell)&(!inner_boundary_cell))
                                            bw.write(" "+Common.INTERGRANULAR_CELL);

                                        if((!grain_boundary_cell)&(inner_boundary_cell))
                                            bw.write(" "+Common.INNER_BOUNDARY_CELL);

                                        if((!grain_boundary_cell)&(!inner_boundary_cell))
                                            bw.write(" "+Common.INTRAGRANULAR_CELL);
                                    }

                                    bw1.newLine();
                                    bw1.flush();
                                    bw.newLine();
                                    bw.flush();
                                }
                                catch(IOException io_exc)
                                {
                                    System.out.println("Error: " + io_exc);
                                }
                                damageCounter++;
                            }
                    //    }
                    }

                    try
                    {
                        bw.close();
                        bw1.close();

                        System.out.println();
                        System.out.println("Name of created file: "+writeFileName);
                        System.out.println("Name of created file: "+writeFileName_2);
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }

             //       totalDamageNumber = totalDamageNumber + damageCounter;
                }

                // Prolongation of time cycle if dendritic growth is going on
                if(stepCounter > stepNumber-10)
                if(totalSizeOfClusters < frSpecSize)
                    stepCounter = stepCounter - 2;
            /*
                System.out.println();
                System.out.println("Total number of damages = " + totalDamageNumber);
                System.out.println("Number of cells = " + frSpecSize);
                System.out.println();
            */
                //   TEST
                finishDate = new Date();
                timePeriod = finishDate.getTime() - thirdDate.getTime();
             /*
                System.out.println();
                System.out.println(GrainsHCPCommon.CALCULATION_PERIOD_5 + time_step_counter + ": " + timePeriod);
                System.out.println();

                timePeriod = finishDate.getTime() - startDate.getTime();

                System.out.println();
                System.out.println(GrainsHCPCommon.TOTAL_CALCULATION_PERIOD + time_step_counter + ": " + timePeriod);
                System.out.println();
              */
           //   END OF TEST

                totalPeriod = totalPeriod + timePeriod;

           //   TEST
        /*
                damagedCellsCounter = 0;
                if(time_step_counter <= stepNumber)
                {
                    System.out.println();
                    for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                    {
                        cell_cluster_number = frSpecFuture.get(cell_counter);

                        if(cell_cluster_number != undamaged_cell)
                        {
                          damagedCellsCounter++;
                          System.out.println("Cell # "+cell_counter+" belongs to cluster # "+cell_cluster_number);
                        }
                    }
                    System.out.println("Number of damaged cells in FRSPECFUTURE = "+damagedCellsCounter);
                    System.out.println();
                }
         */
           //   END OF TEST
              /*
                System.out.println();
                System.out.println(GrainsHCPCommon.END_OF_TIME_STEP+time_step_counter);
                System.out.println();
               */
                //----------- end of time step --------------------
            }
        /*
            System.out.println();
            System.out.println(GrainsHCPCommon.EXECUTION_PERIOD + totalPeriod);
            System.out.println();
         */
        }
        
        /** The constructor generates 3-layer specimen.
         * @param layer2_type type of intermediate layer
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of intermediate layer
         * @param layer2_elem_size size of geometric element of intermediate layer
         * @param _completed_steps text area
         */
        public GrainsHCPTask(byte layer2_type, double layer1_thickness,
                             double layer2_thickness, double layer2_elem_size, TextArea _completed_steps)
        {
            // Reading of task parameters
            readTask();

            // Extracting of lower intermediate layer
            phase_number--;

            embryos_number = 0;
            embryos_number_1 = 0;

            completed_steps = _completed_steps;
            
            // Total number of cells
            int cell_number = cell_number_I*cell_number_J*cell_number_K;
            
            try
            {
                bw = new BufferedWriter(new FileWriter(writeFileName));
                bw.write("#  The file contains information about initial conditions:");
                bw.newLine();
                bw.write("# in 1st string - total number of inner cellular automata,");
                bw.newLine();
                bw.write("# in 2nd string - total number of grains and the number of grains in 1st substrate layer");
                bw.newLine();
                bw.write("# in each further string - grain index of corresponding inner CA");
                bw.newLine();
                bw.write("# and type of automaton location in grain and in specimen:");
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
                bw.write(cell_number+"");
                bw.newLine();
                bw.write(phase_number+" "+embryos_number_1);
                bw.newLine();

                bw1= new BufferedWriter(new FileWriter(writeFileName_2));
                bw1.write("#  The file contains information about initial geometry:");
                bw1.newLine();
                bw1.write("# in 1st string - total number of inner cellular automata,");
                bw1.newLine();
                bw1.write("# specimen sizes along axes X, Y, Z expressed in radiuses of cell");
                bw1.newLine();
                bw1.write("# and number of grains;");
                bw1.newLine();
                bw1.write("# in each further string - grain index, 3 coordinates of corresponding inner CA");
                bw1.newLine();
                bw1.write("# and type of automaton location in grain and in specimen:");
                bw1.newLine();
                bw1.write("# 2 - outer boundary CA,");
                bw1.newLine();
                bw1.write("# 3 - intragranular CA in the interior of specimen,");
                bw1.newLine();
                bw1.write("# 4 - intergranular CA in the interior of specimen,");
                bw1.newLine();
                bw1.write("# 5 - intragranular CA on specimen boundary,");
                bw1.newLine();
                bw1.write("# 6 - intergranular CA on specimen boundary.");
                bw1.newLine();
                bw1.write(cell_number+" "+
                          LoadProperties.SPECIMEN_SIZE_X+" "+
                          LoadProperties.SPECIMEN_SIZE_Y+" "+
                          LoadProperties.SPECIMEN_SIZE_Z);

                if(show_grain_bounds != 0)
                    bw1.write(" "+(embryos_number+phase_number));

                bw1.newLine();
          /*
                bw1.write("layer1_thickness: "+layer1_thickness);bw1.newLine();
                bw1.write("layer2_thickness: "+layer2_thickness);bw1.newLine();
                bw1.write("layer2_elem_size: "+layer2_elem_size);bw1.newLine();
           */
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }

            completed_steps.setText(completed_steps.getText()+"\n"+GrainsHCPCommon.CREATION_OF_SPECIMENS);
            System.out.println(GrainsHCPCommon.CREATION_OF_SPECIMENS);
            frSpec   = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecSize   = frSpec.size();

            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            neighbours3D = new int[frSpecSize][54];

            //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
            // of each cell of specimen
            for(int cell_counter = 0; cell_counter<frSpecSize; cell_counter++)
            {
                setNeighbours3D(cell_counter);
                frSpec.set(cell_counter, embryos_number+phase_number);
            }
            
            // Setting of triple indices of all cells contained in specimen
            setTripleIndices();
            
            if(layer2_type == Common.SIMPLE_LAYER)
            {
                createInitCondSimpleFile(layer1_thickness, layer2_thickness, 0);
                completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SIMPLE_LAYER");
            }
            
            if(layer2_type == Common.TRIANGLE_GOFR)
            {
                createInitCondTriangleGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, 0);
                completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: TRIANGLE_GOFR");
            }
            
            if(layer2_type == Common.SQUARE_GOFR)
            {
                createInitCondSquareGofrFile(layer1_thickness, layer2_thickness, layer2_elem_size, 0);
                completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: SQUARE_GOFR");
            }
            
            if(layer2_type == Common.PYRAMIDES)
            {
                createInitCondPyramidesFile(layer1_thickness, layer2_thickness, layer2_elem_size, 0);
                completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: PYRAMIDES");
            }
            
            if(layer2_type == Common.CHESS_CUBES)
            {
                createInitCondChessCubesFile(layer1_thickness, layer2_thickness, layer2_elem_size, 0);
                completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: CHESS_CUBES");
            }
/*-----------------------------------------------------------------------------------------------------------------------*/
            int state;
            VectorR3 cell_coordinates;
            double coord_X, coord_Y, coord_Z;
            int[] neighb1S_indices = new int[54];
         //   int damageCounter = 0;
            int neghb1S_state = -1;
            boolean grain_boundary_cell = false;
            boolean inner_boundary_cell = false;
            
            // Type of cell location
            byte location_type = -1;

            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                state = frSpec.get(cell_counter);

                // Determination of current cell indices
                indices = cell_indices[cell_counter];

                //System.out.println(cell_counter+" "+indices.getI() +" "+indices.getJ()+" "+indices.getK());

                index1 = indices.getI();
                index2 = indices.getJ();
                index3 = indices.getK();

                // Printing of coordinates and states of cells

                cell_coordinates = calcCoordinates(index1, index2, index3);

                coord_X = cell_coordinates.getX();
                coord_Y = cell_coordinates.getY();
                coord_Z = cell_coordinates.getZ();

                grain_boundary_cell = false;
                inner_boundary_cell = false;
                
                if(state != undamaged_cell)
                {
                  //  System.out.println("cell_counter = "+cell_counter);
                    
                    if(show_grain_bounds != 0)
                    {
                        // Single indices of cells at 1st coordination sphere of current "central" cell
                        neighb1S_indices = neighbours3D[cell_counter];

                        // Checking of states of cells at 1st coordination sphere of current "central" cell
                        for(int neihb1S_counter = 0; neihb1S_counter<12; neihb1S_counter++)
                        {
                            if(neighb1S_indices[neihb1S_counter]>-1)
                            {
                                // State of neighbour cell
                                neghb1S_state = frSpec.get(neighb1S_indices[neihb1S_counter]);

                                // If state of neighbour cell differs from state of current "central" cell
                                //then this "central" cell is boundary cell
                                if(neghb1S_state != state)
                                    grain_boundary_cell = true;
                            }
                            else
                                inner_boundary_cell = true;
                        }
                    }

                    try
                    {
                        bw.write(state+"");
                        bw1.write(state+" "+coord_X+" "+coord_Y+" "+coord_Z);

                        if(show_grain_bounds!=0)
                        {
                            if((grain_boundary_cell)&(inner_boundary_cell))
                                location_type = Common.INNER_BOUNDARY_INTERGRANULAR_CELL;

                            if((grain_boundary_cell)&(!inner_boundary_cell))
                                location_type = Common.INTERGRANULAR_CELL;

                            if((!grain_boundary_cell)&(inner_boundary_cell))
                                location_type = Common.INNER_BOUNDARY_CELL;

                            if((!grain_boundary_cell)&(!inner_boundary_cell))
                                location_type = Common.INTRAGRANULAR_CELL;

                            bw.write(" "+location_type);
                            bw1.write(" "+location_type);
                        }
                        
                        bw1.newLine();
                        bw1.flush();
                        bw.newLine();
                        bw.flush();
                    }
                    catch(IOException io_exc)
                    {
                   //     System.out.println("Error: " + io_exc);
                        completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
                    }
                    
               //     damageCounter++;
                }
            }
            
            try
            {
                bw.close();
                bw1.close();

                completed_steps.setText(completed_steps.getText()+"\nName of created file: "+writeFileName);
                completed_steps.setText(completed_steps.getText()+"\nName of created file: "+writeFileName_2+"\n");

          /*
                System.out.println();
                System.out.println("Name of created file: "+writeFileName);
                System.out.println("Name of created file: "+writeFileName_2);
           */
            }
            catch(IOException io_exc)
            {
            //    System.out.println("Error: " + io_exc);
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
            }
        }

        /** The constructor generates 2-layer specimen.
         * @param section_coord number of axis perpendicular to interface of created layers
         * @param layer_portion portion of cells in layer
         */
        public GrainsHCPTask(byte section_coord, double layer_portion)
        {
            // Reading of task parameters
            readTask();

           // Creation of two specimens without embryos of cracks
            System.out.println(GrainsHCPCommon.CREATION_OF_SPECIMENS);
            frSpec   = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
        //  hotSpec = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);

            // Specimen with cells, which must be joined to clusters at next time step
            frSpecFuture = new GrainsHCPSpecimenR3(cell_number_I, cell_number_J, cell_number_K);
            frSpecSize   = frSpec.size();

            // Setting of coordinates of vectors from cell to its neighbours at 1st coordination sphere
            setNeighb3D1SVectors();

            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            neighbours3D = new int[frSpecSize][54];

            //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
            // of each cell of specimen
            for(int cell_counter = 0; cell_counter<frSpecSize; cell_counter++)
            {
                setNeighbours3D(cell_counter);
            }

            // Setting of triple indices of all cells contained in specimen
            setTripleIndices();

           // List of clusters containing cells in the state "1"
         // clusterList = new ArrayList(0);

           // Cluster containing all cells in the state "1"
//            largeCluster = new Cluster();

            // The number of time steps
            stepNumber = LoadProperties.STEP_NUMBER;

            // The probability of generation of embryo
            probGeneration = LoadProperties.GENERATION_PROBABILITY;

            embryos_number_1 = 0;

         // Stochastic distribution of crack embryos in specimen (time step #0)
            System.out.println();
            System.out.println(GrainsHCPCommon.GENERATION_OF_EMBRYOS);

   /*--------------------------------------------------------------------------------------*/
            int cells_in_layer_1 = 0;
            int cells_in_layer_2 = 0;

            // Triple index of embryo
            Three tripleIndexOfEmbryo = new Three(-1, -1, -1);

            // Generation of layers perpendicular axis OX
            if(section_coord == Common.CREATE_SECTION_X)
            {
                int layer_thickness = (int)Math.round(cell_number_I*layer_portion);

                // Generation of layers
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    tripleIndexOfEmbryo = cell_indices[cell_counter];

                    // Change of state of considered cell
                    if(tripleIndexOfEmbryo.getI()<layer_thickness)
                    {
                        frSpec.set(cell_counter, 1);
                        cells_in_layer_1++;
                    }
                    else
                    {
                        frSpec.set(cell_counter, 2);
                        cells_in_layer_2++;
                    }
                }
            }

            // Generation of layers perpendicular axis OY
            if(section_coord == Common.CREATE_SECTION_Y)
            {
                int layer_thickness = (int)Math.round(cell_number_J*layer_portion);

                // Generation of layers
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    tripleIndexOfEmbryo = cell_indices[cell_counter];

                    // Change of state of considered cell
                    if(tripleIndexOfEmbryo.getJ()<layer_thickness)
                    {
                        frSpec.set(cell_counter, 1);
                        cells_in_layer_1++;
                    }
                    else
                    {
                        frSpec.set(cell_counter, 2);
                        cells_in_layer_2++;
                    }
                }
            }

            // Generation of layers perpendicular axis OZ
            if(section_coord == Common.CREATE_SECTION_Z)
            {
                int layer_thickness = (int)Math.round(cell_number_K*layer_portion);

                // Generation of layers
                for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
                {
                    tripleIndexOfEmbryo = cell_indices[cell_counter];

                    // Change of state of considered cell
                    if(tripleIndexOfEmbryo.getK()<layer_thickness)
                    {
                        frSpec.set(cell_counter, 1);
                        cells_in_layer_1++;
                    }
                    else
                    {
                        frSpec.set(cell_counter, 2);
                        cells_in_layer_2++;
                    }
                }
            }

            System.out.println("Number of cells in 1st layer: " + cells_in_layer_1);
            System.out.println("Number of cells in 2nd layer: " + cells_in_layer_2);
    /*-----------------------------------------------------------------------------------------------------*/

            frSpecSize = frSpec.size();

            try
            {
                bw = new BufferedWriter(new FileWriter(writeFileName));
                bw.write(frSpecSize+"");
                bw.newLine();
                bw.write(2+" "+embryos_number_1);
                bw.newLine();

                bw1= new BufferedWriter(new FileWriter(writeFileName_2));
                bw1.write(frSpecSize+" "+
                        LoadProperties.SPECIMEN_SIZE_X+" "+
                        LoadProperties.SPECIMEN_SIZE_Y+" "+
                        LoadProperties.SPECIMEN_SIZE_Z);

                if(LoadProperties.SHOW_GRAIN_BOUNDS != 0)
                    bw1.write(" "+2);

                bw1.newLine();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
   /*-----------------------------------------------------------------------------------------------------*/
            int state = -1;
            VectorR3 cell_coordinates;
            double coord_X, coord_Y, coord_Z;
            boolean boundary_cell, spec_bound_cell;
            int[] neighb1S_indices = new int[12];
            int neghb1S_state;

            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                state = frSpec.get(cell_counter);

                // Determination of current cell indices
                indices = cell_indices[cell_counter];

                index1 = indices.getI();
                index2 = indices.getJ();
                index3 = indices.getK();

                cell_coordinates = calcCoordinates(index1, index2, index3);

                coord_X = cell_coordinates.getX();
                coord_Y = cell_coordinates.getY();
                coord_Z = cell_coordinates.getZ();

                boundary_cell = false;
                spec_bound_cell = false;

                if(state != undamaged_cell)
                {
                    if(show_grain_bounds == 1)
                    {
                        // Single indices of cells at 1st coordination sphere of current "central" cell
                        neighb1S_indices = neighbours3D[cell_counter];

                        // Checking of states of cells at 1st coordination sphere of current "central" cell
                        for(int neihb1S_counter = 0; neihb1S_counter<12; neihb1S_counter++)
                        {
                            if(neighb1S_indices[neihb1S_counter]>-1)
                            {
                                // State of neighbour cell
                                neghb1S_state = frSpec.get(neighb1S_indices[neihb1S_counter]);

                                // If state of neighbour cell differs from state of current "central" cell
                                //then this "central" cell is located on grain boundary.
                                if(neghb1S_state != state)
                                {
                                    boundary_cell = true;
                                    neihb1S_counter = 12;
                                }
                            }
                        }
                        
                        // Determination of inner cells located on specimen boundary
                        if((index1 == 0)|(index1 == cell_number_I-1)|
                           (index2 == 0)|(index2 == cell_number_J-1)|
                           (index3 == 0)|(index3 == cell_number_K-1))
                        {
                            spec_bound_cell = true;
                        }
                    }

                    try
                    {
                        bw.write(state+"");
                        bw1.write(state+" "+coord_X+" "+coord_Y+" "+coord_Z);

                        if(show_grain_bounds == 1)
                        {
                            if((boundary_cell) &(spec_bound_cell))
                            {
                                bw .write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);
                                bw1.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);
                            }
                            
                            if((boundary_cell) &(!spec_bound_cell))
                            {
                                bw .write(" "+Common.INTERGRANULAR_CELL);
                                bw1.write(" "+Common.INTERGRANULAR_CELL);
                            }                                
                                
                            if((!boundary_cell) &(spec_bound_cell))
                            {
                                bw .write(" "+Common.INNER_BOUNDARY_CELL);
                                bw1.write(" "+Common.INNER_BOUNDARY_CELL);
                            }   
                                
                            if((!boundary_cell)&(!spec_bound_cell))   
                            {
                                bw .write(" "+Common.INTRAGRANULAR_CELL);
                                bw1.write(" "+Common.INTRAGRANULAR_CELL);
                            }
                        }

                        bw.newLine();
                        bw.flush();
                        bw1.newLine();
                        bw1.flush();
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }
                }
            }
            
            try
            {
                bw.close();
                bw1.close();

                System.out.println();
                System.out.println("Name of created file: "+writeFileName);
                System.out.println("Name of created file: "+writeFileName_2);
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
        }

        /** The method reads values of fields of the class from the file.
         */
        private void readTask()
        {
            try
            {
                br = new BufferedReader(new FileReader(GrainsHCPCommon.TASK_PATH+
                                                       GrainsHCPCommon.TASK_NAME_FILE+
                                                       GrainsHCPCommon.TASK_EXTENSION));
                
                task_name = br.readLine();
                
                task_file = GrainsHCPCommon.TASK_PATH+task_name+GrainsHCPCommon.TASK_EXTENSION;
                
                System.out.println("task file = "+task_file);
            }
            catch(IOException io_exc)
            {
                System.out.println("Error:"+io_exc);
            }
            
            LoadProperties loadProps = new LoadProperties(task_file);
            
            writeFileName_2          = loadProps.INIT_GEOM_FILE_NAME;
            writeFileName            = loadProps.INIT_COND_FILE_NAME;
            
            // Creation of file with description of task parameters
            // GrainsHCPCommon.createDescriptionFile();
            damaged_cell             = GrainsHCPCommon.DAMAGED_CELL;
            undamaged_cell           = GrainsHCPCommon.UNDAMAGED_CELL;
            
            packing_type             = loadProps.PACKING_TYPE;
            cell_number_I            = loadProps.CELL_NUMBER_I;
            cell_number_J            = loadProps.CELL_NUMBER_J;
            cell_number_K            = loadProps.CELL_NUMBER_K;
            
            average_grain_size_2     = loadProps.AVERAGE_GRAIN_SIZE_2;
            average_grain_size_2_Y   = loadProps.AVERAGE_GRAIN_SIZE_2_Y;
            average_grain_size_2_Z   = loadProps.AVERAGE_GRAIN_SIZE_2_Z;
            
            average_grain_size_3     = loadProps.AVERAGE_GRAIN_SIZE_3;
            average_grain_size_3_Y   = loadProps.AVERAGE_GRAIN_SIZE_3_Y;
            average_grain_size_3_Z   = loadProps.AVERAGE_GRAIN_SIZE_3_Z;
            
            average_grain_size_4     = loadProps.AVERAGE_GRAIN_SIZE_4;
            average_grain_size_4_Y   = loadProps.AVERAGE_GRAIN_SIZE_4_Y;
            average_grain_size_4_Z   = loadProps.AVERAGE_GRAIN_SIZE_4_Z;
            
            average_grain_size_5     = loadProps.AVERAGE_GRAIN_SIZE_5;
            average_grain_size_5_Y   = loadProps.AVERAGE_GRAIN_SIZE_5_Y;
            average_grain_size_5_Z   = loadProps.AVERAGE_GRAIN_SIZE_5_Z;
            
            average_grain_size       = loadProps.AVERAGE_GRAIN_SIZE;
            average_grain_size_Y     = loadProps.AVERAGE_GRAIN_SIZE_Y;
            average_grain_size_Z     = loadProps.AVERAGE_GRAIN_SIZE_Z;
            
            grain_bound_region_thickness_2 = loadProps.GRAIN_BOUND_REGION_THICKNESS_2;
            grain_bound_region_thickness_3 = loadProps.GRAIN_BOUND_REGION_THICKNESS_3;
            grain_bound_region_thickness_4 = loadProps.GRAIN_BOUND_REGION_THICKNESS_4;
            grain_bound_region_thickness_5 = loadProps.GRAIN_BOUND_REGION_THICKNESS_5;
            grain_bound_region_thickness   = loadProps.GRAIN_BOUND_REGION_THICKNESS;
            
            embryo_distribution_type = loadProps.DISTRIBUTION_METHOD;
            
            min_value = Long.MIN_VALUE;
            max_value = Long.MAX_VALUE;
            
            show_grain_bounds  = loadProps.SHOW_GRAIN_BOUNDS;   
            
            specimen_size_X    = loadProps.SPECIMEN_SIZE_X;
            specimen_size_Y    = loadProps.SPECIMEN_SIZE_Y;
            specimen_size_Z    = loadProps.SPECIMEN_SIZE_Z;

     //       specimen_size_X   = specimen_size_X/2;
       //     specimen_size_Y   = specimen_size_Y/2;
         //   specimen_size_Z   = specimen_size_Z/2;
            
            // Specimen volume calculation
            specimen_volume      = specimen_size_X*specimen_size_Y*specimen_size_Z;
            
            cell_size = 2*LoadProperties.CELL_RADIUS;

            // Average grain volume calculation
            average_grain_volume = average_grain_size*average_grain_size_Y*average_grain_size_Z;
            
            // Coefficient of inner anisotropy vector
            coeff_inner_anisotropy    = loadProps.COEFF_INNER_ANISOTROPY;
            
            // Coordinates of inner anisotropy vector
            double inner_anys_coord_X = loadProps.INNER_ANISOTROPY_COORD_X;
            double inner_anys_coord_Y = loadProps.INNER_ANISOTROPY_COORD_Y;
            double inner_anys_coord_Z = loadProps.INNER_ANISOTROPY_COORD_Z;
            
            // Coefficient of outer anisotropy vector
            coeff_outer_anisotropy    = loadProps.COEFF_OUTER_ANISOTROPY;

            // Coordinates of outer anisotropy vector
            double outer_anys_coord_X = loadProps.OUTER_ANISOTROPY_COORD_X;
            double outer_anys_coord_Y = loadProps.OUTER_ANISOTROPY_COORD_Y;
            double outer_anys_coord_Z = loadProps.OUTER_ANISOTROPY_COORD_Z;
            
            System.out.println();
            System.out.println("================= GrainsHCPTask ====================");
            System.out.println("COEFF_INNER_ANISOTROPY   = "+coeff_inner_anisotropy+"\n");
            System.out.println("INNER_ANISOTROPY_COORD_X = "+inner_anys_coord_X+"\n");
            System.out.println("INNER_ANISOTROPY_COORD_Y = "+inner_anys_coord_Y+"\n");
            System.out.println("INNER_ANISOTROPY_COORD_Z = "+inner_anys_coord_Z+"\n");
            System.out.println();
            System.out.println("COEFF_OUTER_ANISOTROPY   = "+coeff_outer_anisotropy+"\n");
            System.out.println("OUTER_ANISOTROPY_COORD_X = "+outer_anys_coord_X+"\n");
            System.out.println("OUTER_ANISOTROPY_COORD_Y = "+outer_anys_coord_Y+"\n");
            System.out.println("OUTER_ANISOTROPY_COORD_Z = "+outer_anys_coord_Z+"\n");
            System.out.println("==========++++++++++++=============+++++++++++==============");
            System.out.println();
            
            // inner anisotropy vector
            inner_vector = new VectorR3(inner_anys_coord_X, inner_anys_coord_Y, inner_anys_coord_Z);
            
            // outer anisotropy vector
            outer_vector = new VectorR3(outer_anys_coord_X, outer_anys_coord_Y, outer_anys_coord_Z);            
        }
        
        /** The method generates embryos of cracks (dendrites).  
         * @param layer_thickness thickness of upper surface layer where embryos cannot be generated
         */
        private void generateEmbryos(double layer_thickness)
        {    
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryos(double layer_thickness)\n");
            
            // Total number of embryos
            embryos_number = 0;
            
            // Number of embryos at 1st, 2nd and 3rd coordination spheres.
            int neigbouring_embryos_number;
            
            // Coordinates of current cell
            VectorR3 coordinates;

            // Number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            int substrate_cell_layers_number = 0;
            
            // Calculation of number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            for(int cell_layers_counter = 0; cell_layers_counter<cell_number_K; cell_layers_counter++)
            {
                // Coordinates of 1st cell in current layer
                coordinates = calcCoordinates(0, 0, cell_layers_counter);
                
                if(coordinates.getZ()>=layer_thickness)
                {
                    substrate_cell_layers_number = cell_number_K-cell_layers_counter;

                    // End of the cycle
                    cell_layers_counter = cell_number_K;
                }
            }
            
            // Number of cells in substrate
            double substrate_cell_number = frSpecSize*substrate_cell_layers_number/(double)cell_number_K;

            // Volume of substrate where embryos can be generated
            double substrate_volume = specimen_volume*substrate_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos
            double expected_embryos_number = substrate_volume/average_grain_volume;
            
            // Counter of cells in substrate
            int substrate_cell_counter = 0;

            // Generation of embryos of cracks
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
              // Calculation of triple index of cell
              indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);
              
              // Calculation of coordinates of cell
              coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());
              
              if(coordinates.getZ()>=layer_thickness)
              {
                rand = new Random();
                long_rand = rand.nextLong();
                
                probGeneration = (expected_embryos_number - embryos_number)/
                                 (substrate_cell_number   - substrate_cell_counter);
                
                probGeneration = Math.min(1, probGeneration);
                
                substrate_cell_counter++;
/* 
                System.out.println("Probability of generation of embryo in cell " + cell_counter + 
                                   " equals " + probGeneration);
*/
                if((long_rand >= probGeneration*min_value)&
                   (long_rand <= probGeneration*max_value))
                {
                    neigbouring_embryos_number = 0;
                
                    // Determination of total number of embryos at 1st, 2nd and 3rd 
                    // coordination spheres of the considered cell
                    for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                    if(neighbours3D[cell_counter][neighb_counter]>-1)
                    if(frSpec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                    {
                       neigbouring_embryos_number++;
                       neighb_counter = 54;
                    }
                    
                    // If there are no embryos at 1st, 2nd and 3rd spheres then the state
                    // of the cell is changed and the new cluster containing this cell appears
                    if(neigbouring_embryos_number == 0)
                    {
                        // Calculation of current embryos number
                        embryos_number++;
                        
                        // Change of state of considered cell
                        frSpec.set(cell_counter, embryos_number);
                        
                        Three tripleIndexOfEmbryo = cell_indices[cell_counter];
                    /*
                        try
                        {
                            bw.write("Cell with single index " + cell_counter + " and triple index (" +
                                    tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                    tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number);
                            bw.newLine();
                            bw.flush();
                        }
                        catch(IOException io_exc)
                        {
                            System.out.println("Error: " + io_exc);
                        }
                  */        
                        System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                    tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                    tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number);
                      }
                }
              }
            }
      /*     
            try
            {
                bw.write("Number of embryos = " + embryos_number);
                bw.newLine();
                bw.flush();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
        */
            //TEST
            System.out.println();
            System.out.println("Number of cells in substrate: "+substrate_cell_number+" = "+substrate_cell_counter);
            System.out.println("Expected number of embryos:   "+expected_embryos_number);
            System.out.println("Number of embryos:            "+embryos_number);
            System.out.println();
       //     System.out.println("Number of embryos in large cluster = " + largeCluster.size());
                        
      /*-------------------------------------------------------------------------------------------*/
            
         //   calcNeighbIndices(3, 3, 3);
        }
        
        /** The method generates embryos of cracks (dendrites). 
         * @param layer_thickness thickness of upper surface layer where embryos cannot be generated
         * @param grain_layer_1_thick thickness of 1st grain layer
         */
        private void generateEmbryos(double layer_thickness, double grain_layer_1_thick)
        {     
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryos(double layer_thickness, double grain_layer_1_thick)\n");
            
            // Total number of embryos
            embryos_number = 0;

            embryos_number_1 = 0;
            embryos_number_2 = 0;

            // Average volume of grain in 2nd substrate layer
            average_grain_size_2 = average_grain_size*0.5;//1.0;//

            // TEST
            System.out.println("Thickness of 1st grain layer: "+grain_layer_1_thick);
            System.out.println("Average size of grain in 1st substrate layer: "+average_grain_size);
            System.out.println("Average size of grain in 2nd substrate layer: "+average_grain_size_2);

            // Average volume of grain in 1st substrate layer
            double average_grain_volume_1 = average_grain_size*average_grain_size*average_grain_size;

            // Average volume of grain in 2nd substrate layer
            double average_grain_volume_2 = average_grain_size_2*average_grain_size_2*average_grain_size_2;
            
            // Number of embryos at 1st, 2nd and 3rd coordination spheres.
            int neigbouring_embryos_number;
            
            // Coordinates of current cell
            VectorR3 coordinates;

            // Number of cell layers along axis Z in surface layer
            // (interface of substrate and surface layer is perpendicular to axis Z)
            int surface_cell_layers_number = 0;

            // Number of cell layers along axis Z in substrate
            int substrate_cell_layers_number = 0;

            // Number of cell layers along axis Z in 1st substrate layer
            int substrate_1_cell_layers_number = 0;
            
            // Number of cell layers along axis Z in 2nd substrate layer
            int substrate_2_cell_layers_number = 0;
            
            // Cell coordinate Z
            double coord_Z = 0;

            // Variable necessary in order to know whether sufrace layer thickness is known
            boolean surf_thick_is_known = false;

            // Calculation of number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            for(int cell_layers_counter = 0; cell_layers_counter<cell_number_K; cell_layers_counter++)
            {
                // Coordinates of 1st cell in current layer
                coordinates = calcCoordinates(0, 0, cell_layers_counter);

                coord_Z = coordinates.getZ();

                if((coord_Z >= layer_thickness)&(!surf_thick_is_known))
                {
                    surface_cell_layers_number = cell_layers_counter;
                    substrate_cell_layers_number = cell_number_K - surface_cell_layers_number;
                    surf_thick_is_known = true;
                }

                if(coord_Z >= layer_thickness + grain_layer_1_thick)
                {
                    substrate_1_cell_layers_number = cell_layers_counter - surface_cell_layers_number;
                    substrate_2_cell_layers_number = substrate_cell_layers_number - substrate_1_cell_layers_number;

                    // End of the cycle
                    cell_layers_counter = cell_number_K;
                }
            }

            // TEST
            System.out.println("surface_cell_layers_number     = "+surface_cell_layers_number);
            System.out.println("substrate_1_cell_layers_number = "+substrate_1_cell_layers_number);
            System.out.println("substrate_2_cell_layers_number = "+substrate_2_cell_layers_number);


            // Number of cells in 1st substrate layer
            int substrate_1_cell_number = (int)Math.round(frSpecSize*substrate_1_cell_layers_number/(double)cell_number_K);

            // Volume of 1st substrate layer where embryos can be generated
            double substrate_1_volume = specimen_volume*substrate_1_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 1st substrate layer
            double expected_embryos_number_1 = substrate_1_volume/average_grain_volume_1;
            
            // Counter of cells in 1st substrate layer
            int substrate_1_cell_counter = 0;            
            
            // Number of embryos in 1st substrate layer
            embryos_number_1 = 0;
            
            // Number of cells in 2nd substrate layer
            int substrate_2_cell_number = (int)Math.round(frSpecSize*substrate_2_cell_layers_number/(double)cell_number_K);

            // Volume of 2nd substrate layer where embryos can be generated
            double substrate_2_volume = specimen_volume*substrate_2_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 2nd substrate layer
            double expected_embryos_number_2 = substrate_2_volume/average_grain_volume_2;
            
            // Counter of cells in 2nd substrate layer
            int substrate_2_cell_counter = 0;        
            
            // Number of embryos in 2nd substrate layer
            embryos_number_2 = 0;

            // Generation of embryos of cracks
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
              // Calculation of triple index of cell
              indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);
              
              // Calculation of coordinates of cell
              coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());
              
              coord_Z = coordinates.getZ();
              
              if(coord_Z>=layer_thickness)
              {
                  if(coord_Z < layer_thickness+grain_layer_1_thick)
                  {                  
                      rand = new Random();
                      long_rand = rand.nextLong();
                
                      probGeneration = (expected_embryos_number_1 - embryos_number_1)/
                                       (substrate_1_cell_number   - substrate_1_cell_counter);
                
                      probGeneration = Math.min(1, probGeneration);
                
                      substrate_1_cell_counter++;
                      
                      if((long_rand >= probGeneration*min_value)&
                         (long_rand <= probGeneration*max_value))
                      {
                          neigbouring_embryos_number = 0;
                
                          // Determination of total number of embryos at 1st, 2nd and 3rd 
                          // coordination spheres of the considered cell
                          for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                          if(neighbours3D[cell_counter][neighb_counter]>-1)
                          if(frSpec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                          {
                              neigbouring_embryos_number++;
                              neighb_counter = 54;
                          }
                    
                          // If there are no embryos at 1st, 2nd and 3rd spheres then the state
                          // of the cell is changed and the new cluster containing this cell appears
                          if(neigbouring_embryos_number == 0)
                          {
                              // Calculation of current embryos number in 1st substrate layer
                              embryos_number_1++;
                              
                              // Calculation of current embryos number in substrate
                              embryos_number++;
                        
                              // Change of state of considered cell
                              frSpec.set(cell_counter, embryos_number);
                        
                              Three tripleIndexOfEmbryo = cell_indices[cell_counter];
                              
                              System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                                  tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                                  tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number);
                          }
                      }
                  }
                  else
                  {
                      rand = new Random();
                      long_rand = rand.nextLong();
                
                      probGeneration = (expected_embryos_number_2 - embryos_number_2)/
                                       (substrate_2_cell_number   - substrate_2_cell_counter);
                
                      probGeneration = Math.min(1, probGeneration);
                
                      substrate_2_cell_counter++;
                      
                      if((long_rand >= probGeneration*min_value)&
                         (long_rand <= probGeneration*max_value))
                      {
                          neigbouring_embryos_number = 0;
                
                          // Determination of total number of embryos at 1st, 2nd and 3rd 
                          // coordination spheres of the considered cell
                          for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                          if(neighbours3D[cell_counter][neighb_counter]>-1)
                          if(frSpec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                          {
                              neigbouring_embryos_number++;
                              neighb_counter = 54;
                          }
                    
                          // If there are no embryos at 1st, 2nd and 3rd spheres then the state
                          // of the cell is changed and the new cluster containing this cell appears
                          if(neigbouring_embryos_number == 0)
                          {
                              // Calculation of current embryos number in 2nd substrate layer
                              embryos_number_2++;
                              
                              // Calculation of current number of embryos in substrate
                              embryos_number++;
                        
                              // Change of state of considered cell
                              frSpec.set(cell_counter, embryos_number);
                        
                              Three tripleIndexOfEmbryo = cell_indices[cell_counter];
                              
                              System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                                  tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                                  tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number);
                          }
                      }
                  }
              }
            }
            
            // Number of cells in substrate
            int substrate_cell_number = substrate_1_cell_number + substrate_2_cell_number;            
            int substrate_cell_counter = substrate_1_cell_counter + substrate_2_cell_counter;
            
            // Expected number of embryos in substrate
            double expected_embryos_number = expected_embryos_number_1 +expected_embryos_number_2;

            //TEST
            System.out.println();
            System.out.println("Number of cells in 1st substrate layer: "+substrate_1_cell_number+" = "+substrate_1_cell_counter);
            System.out.println("Expected number of embryos in 1st substrate layer:   "+expected_embryos_number_1);
            System.out.println("Number of embryos in 1st substrate layer:            "+embryos_number_1);
            System.out.println();
            System.out.println("Number of cells in 2nd substrate layer: "+substrate_2_cell_number+" = "+substrate_2_cell_counter);
            System.out.println("Expected number of embryos in 2nd substrate layer:   "+expected_embryos_number_2);
            System.out.println("Number of embryos in 2nd substrate layer:            "+embryos_number_2);
            System.out.println();
            System.out.println("Number of cells in substrate: "+substrate_cell_number+" = "+substrate_cell_counter);
            System.out.println("Expected number of embryos in substrate:   "+expected_embryos_number);
            System.out.println("Number of embryos in substrate:            "+embryos_number);
            System.out.println();
        }

        /** The method generates embryos of cracks (dendrites).
         * @param layer_thickness thickness of upper surface layer where embryos cannot be generated
         * @param grain_layer_1_thick thickness of 1st grain layer
         */
        private void generateEmbryos(double layer1_thickness, double layer2_thickness, double layer3_thickness, double grain_layer_1_thick)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryos(double layer1_thickness, double layer2_thickness, double layer3_thickness, double grain_layer_1_thick)\n");
            
            // Total number of embryos
            embryos_number = 0;
            embryos_number_1 = 0;
            embryos_number_2 = 0;
            
            // Average volume of grain in 2nd substrate layer
            average_grain_size_2 = average_grain_size*0.3;//1.0;//

            // TEST
            System.out.println("Thickness of 1st grain layer: "+grain_layer_1_thick);
            System.out.println("Average size of grain in 1st substrate layer: "+average_grain_size);
            System.out.println("Average size of grain in 2nd substrate layer: "+average_grain_size_2);

            // Average volume of grain in 1st substrate layer
            double average_grain_volume_1 = average_grain_size*average_grain_size*average_grain_size;

            // Average volume of grain in 2nd substrate layer
            double average_grain_volume_2 = average_grain_size_2*average_grain_size_2*average_grain_size_2;

            // Number of embryos at 1st, 2nd and 3rd coordination spheres.
            int neigbouring_embryos_number;

            // Coordinates of current cell
            VectorR3 coordinates;

            // Number of cell layers along axis Z in surface layer
            // (interface of substrate and surface layer is perpendicular to axis Z)
            int surface_cell_layers_number = 0;

            // Number of cell layers along axis Z in substrate
            int substrate_cell_layers_number = 0;

            // Number of cell layers along axis Z in 1st substrate layer
            int substrate_1_cell_layers_number = 0;

            // Number of cell layers along axis Z in 2nd substrate layer
            int substrate_2_cell_layers_number = 0;

            // Cell coordinate Z
            double coord_Z = 0;

            // Variable necessary in order to know whether sufrace layer thickness is known
            boolean surf_thick_is_known = false;

            // Calculation of number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            for(int cell_layers_counter = 0; cell_layers_counter<cell_number_K; cell_layers_counter++)
            {
                // Coordinates of 1st cell in current layer
                coordinates = calcCoordinates(0, 0, cell_layers_counter);

                coord_Z = coordinates.getZ();

                if((coord_Z >= layer1_thickness)&(!surf_thick_is_known))
                {
                    surface_cell_layers_number = cell_layers_counter;
                    substrate_cell_layers_number = cell_number_K - surface_cell_layers_number;
                    surf_thick_is_known = true;
                }

                if(coord_Z >= layer1_thickness + grain_layer_1_thick)
                {
                    substrate_1_cell_layers_number = cell_layers_counter - surface_cell_layers_number;
                    substrate_2_cell_layers_number = substrate_cell_layers_number - substrate_1_cell_layers_number;

                    // End of the cycle
                    cell_layers_counter = cell_number_K;
                }
            }

            // TEST
            System.out.println("surface_cell_layers_number     = "+surface_cell_layers_number);
            System.out.println("substrate_1_cell_layers_number = "+substrate_1_cell_layers_number);
            System.out.println("substrate_2_cell_layers_number = "+substrate_2_cell_layers_number);


            // Number of cells in 1st substrate layer
            int substrate_1_cell_number = (int)Math.round(frSpecSize*substrate_1_cell_layers_number/(double)cell_number_K);

            // Volume of 1st substrate layer where embryos can be generated
            double substrate_1_volume = specimen_volume*substrate_1_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 1st substrate layer
            double expected_embryos_number_1 = substrate_1_volume/average_grain_volume_1;

            // Counter of cells in 1st substrate layer
            int substrate_1_cell_counter = 0;

            // Number of embryos in 1st substrate layer
            embryos_number_1 = 0;

            // Number of cells in 2nd substrate layer
            int substrate_2_cell_number = (int)Math.round(frSpecSize*substrate_2_cell_layers_number/(double)cell_number_K);

            // Volume of 2nd substrate layer where embryos can be generated
            double substrate_2_volume = specimen_volume*substrate_2_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 2nd substrate layer
            double expected_embryos_number_2 = substrate_2_volume/average_grain_volume_2;

            // Counter of cells in 2nd substrate layer
            int substrate_2_cell_counter = 0;

            // Number of embryos in 2nd substrate layer
            embryos_number_2 = 0;

            // Generation of embryos of cracks
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
              // Calculation of triple index of cell
              indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

              // Calculation of coordinates of cell
              coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());

              coord_Z = coordinates.getZ();

              if(coord_Z>=0)
              {
                  if(coord_Z < layer1_thickness + layer2_thickness +layer3_thickness)
                  {                  
                      rand = new Random();
                      long_rand = rand.nextLong();

                      probGeneration = (expected_embryos_number_1 - embryos_number_1)/
                                       (substrate_1_cell_number   - substrate_1_cell_counter);

                      probGeneration = Math.min(1, probGeneration);

                      substrate_1_cell_counter++;

                      if((long_rand >= probGeneration*min_value)&
                         (long_rand <= probGeneration*max_value))
                      {
                          neigbouring_embryos_number = 0;

                          // Determination of total number of embryos at 1st, 2nd and 3rd
                          // coordination spheres of the considered cell
                          for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                          if(neighbours3D[cell_counter][neighb_counter]>-1)
                          if(frSpec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                          {
                              neigbouring_embryos_number++;
                              neighb_counter = 54;
                          }

                          // If there are no embryos at 1st, 2nd and 3rd spheres then the state
                          // of the cell is changed and the new cluster containing this cell appears
                          if(neigbouring_embryos_number == 0)
                          {
                              // Calculation of current embryos number in 1st substrate layer
                              embryos_number_1++;

                              // Calculation of current embryos number in substrate
                              embryos_number++;

                              // Change of state of considered cell
                              frSpec.set(cell_counter, embryos_number);

                              Three tripleIndexOfEmbryo = cell_indices[cell_counter];

                              System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                                  tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                                  tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number);
                          }
                      }
                  }
                  else
                  {
                      rand = new Random();
                      long_rand = rand.nextLong();

                      probGeneration = (expected_embryos_number_2 - embryos_number_2)/
                                       (substrate_2_cell_number   - substrate_2_cell_counter);

                      probGeneration = Math.min(1, probGeneration);

                      substrate_2_cell_counter++;

                      if((long_rand >= probGeneration*min_value)&
                         (long_rand <= probGeneration*max_value))
                      {
                          neigbouring_embryos_number = 0;

                          // Determination of total number of embryos at 1st, 2nd and 3rd
                          // coordination spheres of the considered cell
                          for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                          if(neighbours3D[cell_counter][neighb_counter]>-1)
                          if(frSpec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                          {
                              neigbouring_embryos_number++;
                              neighb_counter = 54;
                          }

                          // If there are no embryos at 1st, 2nd and 3rd spheres then the state
                          // of the cell is changed and the new cluster containing this cell appears
                          if(neigbouring_embryos_number == 0)
                          {
                              // Calculation of current embryos number in 2nd substrate layer
                              embryos_number_2++;

                              // Calculation of current number of embryos in substrate
                              embryos_number++;

                              // Change of state of considered cell
                              frSpec.set(cell_counter, embryos_number);

                              Three tripleIndexOfEmbryo = cell_indices[cell_counter];

                              System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                                  tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                                  tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number);
                          }
                      }
                  }
              }
            }

            // Number of cells in substrate
            int substrate_cell_number = substrate_1_cell_number + substrate_2_cell_number;
            int substrate_cell_counter = substrate_1_cell_counter + substrate_2_cell_counter;

            // Expected number of embryos in substrate
            double expected_embryos_number = expected_embryos_number_1 +expected_embryos_number_2;

            //TEST
            System.out.println();
            System.out.println("Number of cells in 1st substrate layer: "+substrate_1_cell_number+" = "+substrate_1_cell_counter);
            System.out.println("Expected number of embryos in 1st substrate layer:   "+expected_embryos_number_1);
            System.out.println("Number of embryos in 1st substrate layer:            "+embryos_number_1);
            System.out.println();
            System.out.println("Number of cells in 2nd substrate layer: "+substrate_2_cell_number+" = "+substrate_2_cell_counter);
            System.out.println("Expected number of embryos in 2nd substrate layer:   "+expected_embryos_number_2);
            System.out.println("Number of embryos in 2nd substrate layer:            "+embryos_number_2);
            System.out.println();
            System.out.println("Number of cells in substrate: "+substrate_cell_number+" = "+substrate_cell_counter);
            System.out.println("Expected number of embryos in substrate:   "+expected_embryos_number);
            System.out.println("Number of embryos in substrate:            "+embryos_number);
            System.out.println();
        }

        /** The method generates embryos of cracks (dendrites) in specimen part between planes.
         * @param fr_spec considered specimen
         * @param average_grain_size average size of grain
         * @param min_coord_X coordinate X of left intersected plane
         * @param max_coord_X coordinate X of right intersected plane
         * @param min_coord_Y coordinate Y of front intersected plane
         * @param max_coord_Y coordinate Y of back intersected plane
         * @param min_coord_Z coordinate Z of upper intersected plane
         * @param max_coord_Z coordinate Z of lower intersected plane
         */
        private void generateEmbryos(GrainsHCPSpecimenR3 fr_spec, double average_grain_size, double min_coord_X, double max_coord_X, 
                                                     double min_coord_Y, double max_coord_Y, double min_coord_Z, double max_coord_Z)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryos(GrainsHCPSpecimenR3 fr_spec, double average_grain_size, double min_coord_X, double max_coord_X, \n" +
                               "                                                     double min_coord_Y, double max_coord_Y, double min_coord_Z, double max_coord_Z)\n");
            
            // Total number of embryos
            embryos_number_1 = 0;
            
            // Average volume of grain in specimen
            average_grain_volume = average_grain_size*average_grain_size*average_grain_size;
            
            // TEST
            System.out.println("Average size of grain:   "+average_grain_size);
            System.out.println("Average volume of grain: "+average_grain_volume);
            System.out.println("Specimen volume:         "+specimen_volume);
            
            // Expected number of embryos in specimen part
            double expected_embryos_number = (max_coord_X-min_coord_X)*(max_coord_Y-min_coord_Y)*(max_coord_Z-min_coord_Z)/average_grain_volume;
            
            // Current cell coordinates
            VectorR3 cell_coord = new VectorR3(0, 0, 0);
            double cell_coord_X = 0;
            double cell_coord_Y = 0;
            double cell_coord_Z = 0;            
            
            // Number of cells in considered part of specimen
            int spec_part_cell_number = 0;
            
            // Calculation of number of cell layers in considered part of specimen
            if(expected_embryos_number>0)
              for(int cell_index_I = 0; cell_index_I<cell_number_I; cell_index_I++)
              for(int cell_index_J = 0; cell_index_J<cell_number_J; cell_index_J++)
              for(int cell_index_K = 0; cell_index_K<cell_number_K; cell_index_K++)
              {
                // Cell coordinates
                cell_coord = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
                cell_coord_X = cell_coord.getX();
                cell_coord_Y = cell_coord.getY();
                cell_coord_Z = cell_coord.getZ();
                
                // Calculation of cells in considered part of specimen
                if(cell_coord_X>=min_coord_X & cell_coord_X<max_coord_X)
                if(cell_coord_Y>=min_coord_Y & cell_coord_Y<max_coord_Y)
                if(cell_coord_Z>=min_coord_Z & cell_coord_Z<max_coord_Z)
                    spec_part_cell_number++;
              }
            
            // Counter of cells in considered part of specimen
            int spec_part_cell_counter = 0;
            
            // Counter of embryos in specimen
            int neigbouring_embryos_number = 0;
            
            // Cell indices
            int cell_index_I = 0;
            int cell_index_J = 0;
            int cell_index_K = 0;
            
            Three tripleIndexOfEmbryo;

            // Generation of embryos of cracks
            if(spec_part_cell_number>0)
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
              // Calculation of cell indices
              indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);
              cell_index_I = indices.getI();
              cell_index_J = indices.getJ();
              cell_index_K = indices.getK();
              
              // Cell coordinates
              cell_coord = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
              cell_coord_X = cell_coord.getX();
              cell_coord_Y = cell_coord.getY();
              cell_coord_Z = cell_coord.getZ();
              
              if(cell_coord_X>=min_coord_X & cell_coord_X<max_coord_X)
              if(cell_coord_Y>=min_coord_Y & cell_coord_Y<max_coord_Y)
              if(cell_coord_Z>=min_coord_Z & cell_coord_Z<max_coord_Z)
              {
                rand      = new Random();
                long_rand = rand.nextLong();
                
                // Probability of embryo generation
                probGeneration = (expected_embryos_number - embryos_number_1)/
                                 (spec_part_cell_number   - spec_part_cell_counter);

                probGeneration = Math.min(1, probGeneration);
                spec_part_cell_counter++;
                
                // Stochastic generation of embryo with calculated probability
                if((long_rand >= probGeneration*min_value)&
                   (long_rand <  probGeneration*max_value))
                {
                  neigbouring_embryos_number = 0;

                  // Determination of total number of embryos at 1st, 2nd and 3rd
                  // coordination spheres of the considered cell
                  for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                  if(neighbours3D[cell_counter][neighb_counter]>-1)
                  if(fr_spec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                  {
                      neigbouring_embryos_number++;
                      neighb_counter = 54;
                  }

                  // If there are no embryos at 1st, 2nd and 3rd spheres then the state
                  // of the cell is changed and the new cluster containing this cell appears
                  if(neigbouring_embryos_number == 0)
                  {
                      // Calculation of current embryos number in substrate
                      embryos_number_1++;
                      embryos_number++;

                      // Change of state of considered cell
                      fr_spec.set(cell_counter, embryos_number);

                 //     tripleIndexOfEmbryo = cell_indices[cell_counter];

                      System.out.println("Cell with single index "+cell_counter+" and triple index (" +
                                          cell_index_I+", "+cell_index_J+", "+cell_index_K+") "+
                                         "contains embryo # " + embryos_number_1+" in layer, # "+embryos_number+" in specimen.");
                  }
                }
              }
            }

            int cell_counter = 0;

            // Creation of single embryo if there are no embryos in specimen part
            if(embryos_number_1 < 2 & spec_part_cell_number>0)
            {
                embryos_number_1++;
                embryos_number++;

                for (int index = 0; index<fr_spec.size(); index++)
                {
                    // Change of state of considered cell
                    fr_spec.set(index, embryos_number);

                    cell_counter++;
                }

                System.out.println("The specimen is homogeneous and contains single grain # "+embryos_number);
            }

            //TEST
            System.out.println();
            System.out.println("Number of cells in specimen part: "+fr_spec.size());
            System.out.println("Expected number of embryos:       "+expected_embryos_number);
            System.out.println("Total number of embryos:          "+embryos_number_1);
            System.out.println();
        }

        /** The method generates embryos of clusters (cracks, dendrites or grains) in specimen part.
         * @param fr_spec considered specimen
         * @param average_grain_size_X average size of grain along axis X
         * @param average_grain_size_Y average size of grain along axis Y
         * @param average_grain_size_Z average size of grain along axis Z
         * @param spec_part_index index of considered specimen part
         */
        private void generateEmbryos(GrainsHCPSpecimenR3 fr_spec, double average_grain_size_X, double average_grain_size_Y, double average_grain_size_Z, byte _spec_part_index)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryos(GrainsHCPSpecimenR3 fr_spec, double average_grain_size_X, double average_grain_size_Y, double average_grain_size_Z, byte _spec_part_index)\n");
            
            // Total number of embryos
            embryos_number_1 = 0;

            // Average volume of grain in specimen
            average_grain_volume = average_grain_size_X*average_grain_size_Y*average_grain_size_Z;

            // Number of cells in considered part of specimen
            int spec_part_cell_number = 0;

            // Index of specimen part containing current cell
            byte spec_part_index;

            // Calculation of number of cells in considered part of specimen
            for(int cell_counter = 0; cell_counter<fr_spec.size(); cell_counter++)
            {
                spec_part_index = layer_indices[cell_counter];

                if(spec_part_index == _spec_part_index)
                    spec_part_cell_number++;
            }

            // Layer volume
            double layer_volume = specimen_volume*spec_part_cell_number/fr_spec.size();

            // Expected number of embryos in 1st specimen
            double expected_embryos_number = layer_volume/average_grain_volume;

            // Counter of cells in considered part of specimen
            int spec_part_cell_counter = 0;

            // Counter of embryos in specimen
            int neigbouring_embryos_number = 0;

            // Triple index of embryo
            Three tripleIndexOfEmbryo;

            // Cell single index
            int cell_index;
            int cell_index_I, cell_index_J, cell_index_K;

            boolean three_embryos = false;

            if((expected_embryos_number>2.9)&(expected_embryos_number<3.1))
            {
                three_embryos = true;
                System.out.println("Expected number of embryos:  "+expected_embryos_number);
            }

            // Change of state of 3 cells
            if(three_embryos)
            {
                System.out.println("There are 3 grain embryos in substrate!!!");

                cell_index_I = cell_number_I/2;
                cell_index_J = cell_number_J/2;
                cell_index_K = cell_number_K/4;
                cell_index = calcSingleIndex(cell_index_I, cell_index_J, cell_index_K);
                embryos_number_1++;
                embryos_number++;
                fr_spec.set(cell_index, embryos_number);
                System.out.println("Cell # "+cell_index+" with indices ("+cell_index_I+", "+cell_index_J+", "+cell_index_K+") contains embryo # " + embryos_number_1+" ## "+embryos_number);

                cell_index_I =   cell_number_I/4;// cell_number_I/2;// 
                cell_index_J =   cell_number_J/2;// cell_number_J/4;// 
                cell_index_K = 3*cell_number_K/4;
                cell_index = calcSingleIndex(cell_index_I, cell_index_J, cell_index_K);
                embryos_number_1++;
                embryos_number++;
                fr_spec.set(cell_index, embryos_number);
                System.out.println("Cell # "+cell_index+" with indices ("+cell_index_I+", "+cell_index_J+", "+cell_index_K+") contains embryo # " + embryos_number_1+" ## "+embryos_number);

                cell_index_I = 3*cell_number_I/4;//   cell_number_I/2;// 
                cell_index_J =   cell_number_J/2;// 3*cell_number_J/4;// 
                cell_index_K = 3*cell_number_K/4;
                cell_index = calcSingleIndex(cell_index_I, cell_index_J, cell_index_K);
                embryos_number_1++;
                embryos_number++;
                fr_spec.set(cell_index, embryos_number);
                System.out.println("Cell # "+cell_index+" with indices ("+cell_index_I+", "+cell_index_J+", "+cell_index_K+") contains embryo # " + embryos_number_1+" ## "+embryos_number);
            }

            // Generation of embryos of cracks
            if(!three_embryos)
            if(spec_part_cell_number>0)
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
              spec_part_index = layer_indices[cell_counter];

              if(spec_part_index == _spec_part_index)
              {
                rand = new Random();
                long_rand = rand.nextLong();

                probGeneration = (expected_embryos_number - embryos_number_1)/
                                 (spec_part_cell_number   - spec_part_cell_counter);

                probGeneration = Math.min(1, probGeneration);

                spec_part_cell_counter++;

                if((long_rand >= probGeneration*min_value)&
                   (long_rand <  probGeneration*max_value))
                {
                  neigbouring_embryos_number = 0;

                  // Determination of total number of embryos at 1st, 2nd and 3rd
                  // coordination spheres of the considered cell
                  for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                  if(neighbours3D[cell_counter][neighb_counter]>-1)
                  if(fr_spec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                  {
                      neigbouring_embryos_number++;
                      neighb_counter = 54;
                  }

                  // If there are no embryos at 1st, 2nd and 3rd spheres then the state
                  // of the cell is changed and the new cluster containing this cell appears
                  if(neigbouring_embryos_number == 0)
                  {
                      // Calculation of current embryos number in substrate
                      embryos_number_1++;
                      embryos_number++;

                      // Change of state of considered cell
                      fr_spec.set(cell_counter, embryos_number);

                      tripleIndexOfEmbryo = cell_indices[cell_counter];

                      System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                         tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                         tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number_1+" ## "+embryos_number);
                  }
                }
              }
            }

          //  int cell_counter = 0;

            // Creation of single embryo if there are no embryos in specimen
       //     if(embryos_number_1 < 2 | expected_embryos_number<=1)
            if(embryos_number_1 < 2 & spec_part_cell_number>0)
            if(spec_part_cell_number>0)
            {
                embryos_number_1 = 1;
                embryos_number++;

                for (int index = 0; index<fr_spec.size(); index++)
                if(layer_indices[index] == _spec_part_index)
                {
                    // Change of state of considered cell
                    fr_spec.set(index, embryos_number);

                    // TEST
                //  fr_spec.set(index, layer_indices[index]);

                //    cell_counter++;
                }

                System.out.println("The specimen is homogeneous and contains single grain # "+embryos_number_1+" ## "+embryos_number);
            }

            //TEST
            System.out.println();
            // TEST
            System.out.println("Average grain sizes:  "+average_grain_size_X+" "+average_grain_size_Y+" "+average_grain_size_Z);
            System.out.println("Average grain volume: "+average_grain_volume);
            System.out.println("Layer #"+_spec_part_index+" volume:         "+layer_volume);
         //   System.out.println("Number of cells in specimen: "+fr_spec.size());
            System.out.println("Number of cells in layer #"+_spec_part_index+": "+spec_part_cell_number);//+" = "+cell_counter);
            System.out.println("Expected number of embryos:  "+expected_embryos_number);
            System.out.println("Total number of embryos:     "+embryos_number_1);
            System.out.println();
        }

        /** The method generates embryos of clusters (cracks, dendrites or grains)
         * in each automaton of chosen specimen part.
         * @param fr_spec considered specimen
         * @param spec_part_index index of chosen specimen part
         */
        private void generateEmbryosInEachCell(GrainsHCPSpecimenR3 fr_spec, byte _spec_part_index)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosInEachCell(GrainsHCPSpecimenR3 fr_spec, byte _spec_part_index)\n");
            
            // Number of cells in considered part of specimen
            int spec_part_cell_number = 0;

            // Index of specimen part containing current cell
            byte spec_part_index;
            
            Three tripleIndexOfEmbryo;

            // Calculation of number of cells in considered part of specimen
            for(int cell_counter = 0; cell_counter<fr_spec.size(); cell_counter++)
            {
                spec_part_index = layer_indices[cell_counter];

                if(spec_part_index == _spec_part_index)
                    spec_part_cell_number++;
            }

            // Counter of cells in considered part of specimen
            int spec_part_cell_counter = 0;

            // Generation of embryos of cracks
            if(spec_part_cell_number>0)
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
              spec_part_index = layer_indices[cell_counter];

              if(spec_part_index == _spec_part_index)
              {
                spec_part_cell_counter++;

                // Calculation of current embryos number in substrate
                embryos_number_1++;
                embryos_number++;

                // Change of state of considered cell
                fr_spec.set(cell_counter, embryos_number);

                tripleIndexOfEmbryo = cell_indices[cell_counter];

                System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                   tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                   tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number_1+" ## "+embryos_number);
              }
            }

            //TEST
            System.out.println();
            System.out.println("Number of cells in layer #"+_spec_part_index+": "+spec_part_cell_number);
            System.out.println("Total number of embryos:     "+embryos_number_1);
            System.out.println();
        }

        /** The method generates embryos of cracks (dendrites) arranged regularly.
         * Each embryo is generated in centre of specified volume.
         * @param layer_thickness thickness of upper surface layer where embryos cannot be generated
         */
        private void generateEmbryosRegularly(double layer_thickness)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRegularly(double layer_thickness)\n");
            
            // Triple index of embryo
        //    Three tripleIndexOfEmbryo = new Three(-1, -1, -1);

            // Total number of embryos
            embryos_number = 0;

            // Number of embryos at 1st, 2nd and 3rd coordination spheres.
            int neigbouring_embryos_number;

            // Coordinates of current cell
            VectorR3 coordinates;

            // Number of cell layers along axis Z in substrate
            int substrate_cell_layers_number = 0;

            // Number of cell layers along axis Z in surface layer
            int surface_cell_layers_number = cell_number_K;

            // Calculation of number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            for(int cell_layers_counter = 0; cell_layers_counter<cell_number_K; cell_layers_counter++)
            {
                // Coordinates of 1st cell in current layer
                coordinates = calcCoordinates(0, 0, cell_layers_counter);

                if(coordinates.getZ()>=layer_thickness)
                {
                    substrate_cell_layers_number = cell_number_K-cell_layers_counter;
                    surface_cell_layers_number   = cell_layers_counter;

                    // End of the cycle
                    cell_layers_counter = cell_number_K;
                }
            }

            // Number of cells in substrate
            double substrate_cell_number = frSpecSize*substrate_cell_layers_number/(double)cell_number_K;

            // Volume of substrate where embryos can be generated
            double substrate_volume = specimen_volume*substrate_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos
            double expected_embryos_number = substrate_volume/average_grain_volume;

            // Counter of cells in substrate
            int substrate_cell_counter = 0;
              
           // Generation of embryos of cracks
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {  
                // Calculation of triple index of cell
                indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

                // Calculation of coordinates of cell
                coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());

                if(coordinates.getZ()>=layer_thickness)
                {
                  substrate_cell_counter++;

                  coordinates.setZ(coordinates.getZ()-layer_thickness);
                  
             //     System.out.print  (Math.round(coordinates.getX())%Math.round(average_grain_size)+" "+Math.round(average_grain_size/2)+"    ");
               //   System.out.print  (Math.round(coordinates.getY())%Math.round(average_grain_size)+" "+Math.round(average_grain_size/2)+"    ");
                 // System.out.println(Math.round(coordinates.getZ())%Math.round(average_grain_size)+" "+Math.round(average_grain_size/2)+"    ");

                  if(Math.round(coordinates.getX()/1.732)%Math.round(average_grain_size/1.732)==Math.round(average_grain_size/3.464))
                  if(Math.round(coordinates.getY()/2.000)%Math.round(average_grain_size/2.000)==Math.round(average_grain_size/4.000))
                  if(Math.round(coordinates.getZ()/1.633)%Math.round(average_grain_size/1.633)==Math.round(average_grain_size/3.266))
                  {
                    embryos_number++;
                    
                    // Change of state of considered cell
                    frSpec.set(cell_counter, embryos_number);
              /*
                    try
                    {
                        bw.write("Cell with single index " + cell_counter + " and triple index (" +
                            tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                            tripleIndexOfEmbryo.getK() + ") " + " contains embryo # " + embryos_number);
                        bw.newLine();
                        bw.flush();
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }
               */
                    System.out.println("Cell with single index "+cell_counter+" and triple index ("+
                                       indices.getI()+", "+indices.getJ()+", "+indices.getK()+") "+
                                       " contains embryo # "+embryos_number);
                    
                    coordinates.setZ(coordinates.getZ()+layer_thickness);
                    
                    System.out.println("Cell coordinates: ("+
                                       coordinates.getX()+", "+coordinates.getY()+", "+coordinates.getZ()+"). "+
                                       " Average grain size: "+average_grain_size);


                  }
                }
            }
      /*
            try
            {
                bw.write("Number of embryos = " + embryos_number);
                bw.newLine();
                bw.flush();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
       */
            System.out.println("Number of embryos = " + embryos_number);
       //     System.out.println("Number of embryos in large cluster = " + largeCluster.size());
                        
      /*-------------------------------------------------------------------------------------------*/
            //TEST
            System.out.println();
            System.out.println("Number of cells in substrate: "+substrate_cell_number+" = "+substrate_cell_counter);
            System.out.println("Expected number of embryos:   "+expected_embryos_number);
            System.out.println("Number of embryos:            "+embryos_number);
            System.out.println();
         //   calcNeighbIndices(3, 3, 3);
        }
        
        /** The method generates embryos of cracks (dendrites) arranged regularly.
         * Each embryo is generated in centre of specified volume.
         * @param layer_thickness thickness of upper surface layer where embryos cannot be generated
         * @param grain_layer_1_thick thickness of 1st grain layer
         */
        private void generateEmbryosRegularly(double layer_thickness, double grain_layer_1_thick)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRegularly(double layer_thickness, double grain_layer_1_thick)\n");
            
            // Triple index of embryo
        //    Three tripleIndexOfEmbryo = new Three(-1, -1, -1);

            // Total number of embryos
            embryos_number = 0;
                        
            // Numbers of embryos in substrate layers
            embryos_number_1 = 0;
            embryos_number_2 = 0;

            // Number of embryos at 1st, 2nd and 3rd coordination spheres.
            int neigbouring_embryos_number;

            // Coordinates of current cell
            VectorR3 coordinates;

            // Number of cell layers along axis Z in substrate
            int substrate_cell_layers_number = 0;

            // Number of cell layers along axis Z in surface layer
            int surface_cell_layers_number = cell_number_K;

            double coord_Z = 0;

            double layer_1_aver_gr_size = average_grain_size;
            double layer_2_aver_gr_size = average_grain_size*0.5;

            double layer_1_aver_gr_volume = layer_1_aver_gr_size*layer_1_aver_gr_size*layer_1_aver_gr_size;
            double layer_2_aver_gr_volume = layer_2_aver_gr_size*layer_2_aver_gr_size*layer_2_aver_gr_size;

            double grain_size = 0;

            // Number of cell layers in 1st substrate layer
            int substrate_1_cell_layers_number=0;

            // Number of cell layers in 2nd substrate layer
            int substrate_2_cell_layers_number=0;

            // Variable necessary in order to know whether sufrace layer thickness is known
            boolean surf_thick_is_known = false;

            // Calculation of number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            for(int cell_layers_counter = 0; cell_layers_counter<cell_number_K; cell_layers_counter++)
            {
                // Coordinates of 1st cell in current layer
                coordinates = calcCoordinates(0, 0, cell_layers_counter);

                coord_Z = coordinates.getZ();

                if((coord_Z >= layer_thickness)&(!surf_thick_is_known))
                {
                    surface_cell_layers_number = cell_layers_counter;
                    substrate_cell_layers_number = cell_number_K-cell_layers_counter;
                    surf_thick_is_known = true;
                }

                if(coord_Z >= layer_thickness + grain_layer_1_thick)
                {
                    substrate_1_cell_layers_number = cell_layers_counter - surface_cell_layers_number;
                    substrate_2_cell_layers_number = substrate_cell_layers_number - substrate_1_cell_layers_number;

                    // End of the cycle
                    cell_layers_counter = cell_number_K;
                }
            }

            // TEST
            System.out.println("Layers: "+surface_cell_layers_number+" "+substrate_1_cell_layers_number+" "+substrate_2_cell_layers_number);

            // Number of cells in substrate
            double substrate_cell_number = frSpecSize*substrate_cell_layers_number/(double)cell_number_K;

            // Volume of substrate where embryos can be generated
            double substrate_volume = specimen_volume*substrate_cell_layers_number/(double)cell_number_K;

            // Number of cells in 1st substrate
            double substrate_1_cell_number = frSpecSize*substrate_1_cell_layers_number/(double)cell_number_K;

            // Volume of 1st substrate where embryos can be generated
            double substrate_1_volume = specimen_volume*substrate_1_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 1st substrate
            double expected_embryos_number_1 = substrate_1_volume/layer_1_aver_gr_volume;

            // Number of cells in 2nd substrate
            double substrate_2_cell_number = frSpecSize*substrate_2_cell_layers_number/(double)cell_number_K;

            // Volume of 2nd substrate where embryos can be generated
            double substrate_2_volume = specimen_volume*substrate_2_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 2nd substrate
            double expected_embryos_number_2 = substrate_2_volume/layer_2_aver_gr_volume;

            // Expected number of embryos
            double expected_embryos_number = expected_embryos_number_1 + expected_embryos_number_2;

            // Counter of cells in substrate
            int substrate_cell_counter = 0;

            // Variable responsible for conditions of embryo generation
            boolean create_embryo = false;
            
            // Variable responsible for embryo location
            boolean substrate_layer_1 = false;
            boolean substrate_layer_2 = false;

            // Generation of embryos of cracks
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                // Calculation of triple index of cell
                indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);
                
                // Calculation of coordinates of cell
                coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());
                
                coord_Z = coordinates.getZ();

                if(coord_Z>=layer_thickness)
                {
                  substrate_cell_counter++;

                  coordinates.setZ(coord_Z-layer_thickness);

                  if(coord_Z < grain_layer_1_thick)
                  {
                      create_embryo = (Math.round(coordinates.getX()/1.732)%Math.round(layer_1_aver_gr_size/1.732)==Math.round(layer_1_aver_gr_size/3.464))&
                                      (Math.round(coordinates.getY()/2.000)%Math.round(layer_1_aver_gr_size/2.000)==Math.round(layer_1_aver_gr_size/4.000))&
                                      (Math.round(coordinates.getZ()/1.633)%Math.round(layer_1_aver_gr_size/1.633)==Math.round(layer_1_aver_gr_size/3.266));

                      substrate_layer_1 = true;
                      substrate_layer_2 = false;

                      grain_size = layer_1_aver_gr_size;
                  }                      
                  else
                  {
                      create_embryo = (Math.round(coordinates.getX()/1.732)%Math.round(layer_2_aver_gr_size/1.732)==Math.round(layer_2_aver_gr_size/3.464))&
                                      (Math.round(coordinates.getY()/2.000)%Math.round(layer_2_aver_gr_size/2.000)==Math.round(layer_2_aver_gr_size/4.000))&
                                      (Math.round(coordinates.getZ()/1.633)%Math.round(layer_2_aver_gr_size/1.633)==Math.round(layer_2_aver_gr_size/3.266));

                      substrate_layer_1 = false;
                      substrate_layer_2 = true;

                      grain_size = layer_2_aver_gr_size;
                  }
                  
                  if(create_embryo)
                  {
                    embryos_number++;

                    if(substrate_layer_1)
                        embryos_number_1++;

                    if(substrate_layer_2)
                        embryos_number_2++;

                    // Change of state of considered cell
                    frSpec.set(cell_counter, embryos_number);

                    System.out.println("Cell with single index "+cell_counter+" and triple index ("+
                                       indices.getI()+", "+indices.getJ()+", "+indices.getK()+") "+
                                       " contains embryo # "+embryos_number);

                    coordinates.setZ(coordinates.getZ()+layer_thickness);

                    System.out.println("Cell coordinates: ("+
                                       coordinates.getX()+", "+coordinates.getY()+", "+coordinates.getZ()+"). "+
                                       " Average grain size: "+grain_size);
                  }
                }
            }

            System.out.println("Number of embryos = " + embryos_number);
            
            //TEST
            System.out.println();
            System.out.println("Number of cells in 1st substrate layer: "+substrate_1_cell_number);
            System.out.println("Expected number of embryos in 1st substrate layer:   "+expected_embryos_number_1);
            System.out.println("Number of embryos in 1st substrate layer:            "+embryos_number_1);
            System.out.println();
            System.out.println("Number of cells in 2nd substrate layer: "+substrate_2_cell_number);
            System.out.println("Expected number of embryos:   "+expected_embryos_number_2);
            System.out.println("Number of embryos:            "+embryos_number_2);
            System.out.println();
            System.out.println("Number of cells in substrate: "+substrate_cell_number+" = "+substrate_cell_counter);
            System.out.println("Expected number of embryos:   "+expected_embryos_number);
            System.out.println("Number of embryos:            "+embryos_number);
            System.out.println();
        }

        /** The method generates embryos of cracks (dendrites) arranged regularly in specimen part between planes;
         * each embryo is generated in the centre of specified volume.
         * @param fr_spec considered specimen
         * @param average_grain_size_X average size of grain along axis X
         * @param average_grain_size_Y average size of grain along axis Y
         * @param average_grain_size_Z average size of grain along axis Z
         * @param min_coord_X coordinate X of left intersected plane
         * @param max_coord_X coordinate X of right intersected plane
         * @param min_coord_Y coordinate Y of front intersected plane
         * @param max_coord_Y coordinate Y of back intersected plane
         * @param min_coord_Z coordinate Z of upper intersected plane
         * @param max_coord_Z coordinate Z of lower intersected plane
         */
        private void generateEmbryosRegularly(GrainsHCPSpecimenR3 fr_spec, double average_grain_size_X, double average_grain_size_Y, double average_grain_size_Z,
                                          double min_coord_X, double max_coord_X, double min_coord_Y, double max_coord_Y, double min_coord_Z, double max_coord_Z)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRegularly(GrainsHCPSpecimenR3 fr_spec, ..., double max_coord_Z)\n");
            
            // Average volume of grain
            average_grain_volume = average_grain_size_X*average_grain_size_Y*average_grain_size_Z;

            // Expected number of embryos in 1st specimen
            double expected_embryos_number = (max_coord_X-min_coord_X)*(max_coord_Y-min_coord_Y)*(max_coord_Z-min_coord_Z)/average_grain_volume;

            // Three coordinates of cell
            VectorR3 coordinates;

            // Cell coordinates X, Y and Z
            double cell_coord_X;
            double cell_coord_Y;
            double cell_coord_Z;
            
            // Cell indices
            int cell_index_I, cell_index_J, cell_index_K;
            
            // number of cells in considered part of specimen
            int specimen_part_cell_number = 0;
            
            System.out.println("Average grain sizes: "+average_grain_size_X+"; "+average_grain_size_Y+""+average_grain_size_Z);

            // Calculation of number of cells in considered part of specimen
            if(expected_embryos_number>0)
            for(cell_index_I = 0; cell_index_I<cell_number_I; cell_index_I++)
            for(cell_index_J = 0; cell_index_J<cell_number_J; cell_index_J++)
            for(cell_index_K = 0; cell_index_K<cell_number_K; cell_index_K++)
            {
                // Cell coordinates
                coordinates = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
                cell_coord_X = coordinates.getX();
                cell_coord_Y = coordinates.getY();
                cell_coord_Z = coordinates.getZ();

                if(cell_coord_X>=min_coord_X & cell_coord_X<max_coord_X)
                if(cell_coord_Y>=min_coord_Y & cell_coord_Y<max_coord_Y)
                if(cell_coord_Z>=min_coord_Z & cell_coord_Z<max_coord_Z)
                  specimen_part_cell_number++;
            }

            // Total number of embryos in considered part of specimen
            embryos_number_1 = 0;
            
            // Variable responsible for embryo creation
            boolean create_embryo;
            
            // Generation of embryos of cracks
            // if(expected_embryos_number>=1)
            if(specimen_part_cell_number>0)
            for(int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                // Calculation of triple index of cell
                indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);                
                cell_index_I = indices.getI();
                cell_index_J = indices.getJ();
                cell_index_K = indices.getK();
                
                // Calculation of coordinates of cell
                coordinates = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
                cell_coord_X = coordinates.getX();
                cell_coord_Y = coordinates.getY();
                cell_coord_Z = coordinates.getZ();
                
                // Determination whether this cell will be embryo
                create_embryo = (Math.round(cell_coord_X/1.732)%Math.round(average_grain_size_X/1.732)==Math.round(average_grain_size_X/3.464))&
                                (Math.round(cell_coord_Y/2.000)%Math.round(average_grain_size_Y/2.000)==Math.round(average_grain_size_Y/4.000))&
                                (Math.round(cell_coord_Z/1.633)%Math.round(average_grain_size_Z/1.633)==Math.round(average_grain_size_Z/3.266));
                
                if(create_embryo)
                if(cell_coord_X>=min_coord_X & cell_coord_X<max_coord_X &
                   cell_coord_Y>=min_coord_Y & cell_coord_Y<max_coord_Y &
                   cell_coord_Z>=min_coord_Z & cell_coord_Z<max_coord_Z)
                {
                    embryos_number_1++;
                    embryos_number++;
                    
                    // Change of state of considered cell
                    fr_spec.set(cell_counter, embryos_number);
                    
                    System.out.println("Cell with single index "+cell_counter+" and triple index ("+
                                        cell_index_I+", "+cell_index_J+", "+ cell_index_K+") "+
                                       "contains embryo # "+embryos_number_1+" in layer and # "+embryos_number+" in specimen.");
                }
            }
            
          //  int cell_counter = 0;
            
            // Creation of single grain if there are no embryos in specimen
            //     if(embryos_number_1 < 2 | expected_embryos_number<=1)
            if(embryos_number_1 < 2 & fr_spec.size()>0)
            {
                embryos_number_1++;
                embryos_number++;

                for (int index = 0; index<fr_spec.size(); index++)
                {
                    // Change of state of considered cell
                    fr_spec.set(index, embryos_number);

                 //   cell_counter++;
                }

                System.out.println("The specimen is homogeneous and contains single grain # "+embryos_number);
            }

            //TEST
            System.out.println();
            System.out.println("Number of cells in specimen: "+fr_spec.size());
            System.out.println("Expected number of embryos:   "+expected_embryos_number);
            System.out.println("Total number of embryos:      "+embryos_number_1);
            System.out.println();
        }
        
        /** The method generates embryos of clusters (cracks, dendrites or grains)
         * arranged regularly in specimen part.
         * Each embryo is generated in centre of specified volume.
         * @param fr_spec considered specimen
         * @param average_grain_size average size of grain
         * @param spec_part_index index of considered specimen part
         */
        private void generateEmbryosRegularly(GrainsHCPSpecimenR3 fr_spec, double average_grain_size_X, double average_grain_size_Y, 
                                              double average_grain_size_Z, byte _spec_part_index)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRegularly(GrainsHCPSpecimenR3 fr_spec, ..., byte _spec_part_index)\n");
            
            // Total number of embryos
            embryos_number_1 = 0;
            
            // Average volume of grain in specimen
            average_grain_volume = average_grain_size_X*average_grain_size_Y*average_grain_size_Z;

            // Number of cells in considered part of specimen
            int spec_part_cell_number = 0;

            // Index of specimen part containing current cell
            byte spec_part_index;

            // Calculation of number of cells in considered part of specimen
            for(int cell_counter = 0; cell_counter<fr_spec.size(); cell_counter++)
            {
                spec_part_index = layer_indices[cell_counter];

                if(spec_part_index == _spec_part_index)
                    spec_part_cell_number++;
            }

            // Layer volume
            double layer_volume = specimen_volume*spec_part_cell_number/fr_spec.size();

            // Expected number of embryos in 1st specimen
            double expected_embryos_number = layer_volume/average_grain_volume;

            System.out.println("Expected number of embryos: "+expected_embryos_number);
            System.out.println("Average volume of grain:    "+average_grain_volume);
        /*
            VectorR3 coordinates;
            boolean create_embryo;
            
            // Generation of embryos of grains
            if(spec_part_cell_number>0)
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
              spec_part_index = layer_indices[cell_counter];

              if(spec_part_index == _spec_part_index)
              {
                // Calculation of triple index of cell
                indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

                // Calculation of coordinates of cell
                coordinates = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());

                // Here occurs determination whether this cell will be embryo.
                create_embryo = (Math.round(coordinates.getX()/1.732)%Math.round(average_grain_size_X/1.732)==Math.round(average_grain_size_X/3.464))&
                                (Math.round(coordinates.getY()/2.000)%Math.round(average_grain_size_Y/2.000)==Math.round(average_grain_size_Y/4.000))&
                                (Math.round(coordinates.getZ()/1.633)%Math.round(average_grain_size_Z/1.633)==Math.round(average_grain_size_Z/3.266));
                
                if(embryos_number_1 <= 2)
                {
                  if(embryos_number_1 % 2 == 0)
                    create_embryo = 
                               //   Math.round(coordinates.getX()/1.732)%Math.round(average_grain_size_X/1.732)==0.0 &
                               //   Math.round(coordinates.getY()/2.000)%Math.round(average_grain_size_Y/2.000)==Math.round(3.8*average_grain_size_Y/4.000) &
                            (Math.round(coordinates.getX()/1.732)%Math.round(average_grain_size_X/1.732)==Math.round(average_grain_size_X/3.464))&
                             indices.getJ() == cell_number_J/2 &
                       //      indices.getJ() == cell_number_J/2 - 3 &
                       //   indices.getK() == 0;
                                  Math.round(coordinates.getZ()/1.633)%Math.round(average_grain_size_Z/1.633)==Math.round(average_grain_size_Z/3.266);
                  else
                    create_embryo =
                                //  Math.round(coordinates.getX()/1.732)%Math.round(average_grain_size_X/1.732)==0.0 &
                                //  Math.round(coordinates.getY()/2.000)%Math.round(average_grain_size_Y/2.000)==Math.round(0.2*average_grain_size_Y/4.000) &
                            (Math.round(coordinates.getX()/1.732)%Math.round(average_grain_size_X/1.732)==Math.round(average_grain_size_X/3.464))&
                             indices.getJ() == cell_number_J/2 &
                       //     indices.getJ() == cell_number_J/2 + 3 &
                       //   indices.getK() == 0;
                                 Math.round(coordinates.getZ()/1.633)%Math.round(average_grain_size_Z/1.633)==Math.round(average_grain_size_Z/3.266);
                }
                
                if(create_embryo)
                {
                    embryos_number_1++;
                    embryos_number++;

                    // Change of state of considered cell
                    fr_spec.set(cell_counter, embryos_number);

                    System.out.println("Cell with single index "+cell_counter+", triple index ("+indices.getI()+", "+indices.getJ()+", "+
                                       indices.getK()+") and coord-s ("+coordinates.getX()+", "+coordinates.getY()+", "+coordinates.getZ()+") contains embryo # "+embryos_number_1+" ## "+embryos_number);
                    
                    // Period of presence of current cell near one of clusters
                 //   life_periods[cell_counter] = 1;

               //     System.out.println("Cell coordinates: ("+coordinates.getX()+", "+coordinates.getY()+", "+coordinates.getZ()+"). "+
                 //                      "Average grain size: "+average_grain_size);
                }
              }
            }
        */
            
            // Number of embryos along axes X, Y, Z in the whole specimen
            int embryo_number_X = (int)Math.round(specimen_size_X/average_grain_size_X);
            int embryo_number_Y = (int)Math.round(specimen_size_Y/average_grain_size_Y);
            int embryo_number_Z = (int)Math.round(specimen_size_Z/average_grain_size_Z);
            
            // Coordinates of cell containing embryo
            double embryo_cell_coord_X, embryo_cell_coord_Y, embryo_cell_coord_Z;
            
            // Triple index of cell containing current embryo
            Three embryo_cell_indices;
            
            // 3 indices of cell containing current embryo
            int embryo_cell_index_I, embryo_cell_index_J, embryo_cell_index_K;
            
            // Single index of cell containing embryo
            int embryo_cell_index;
                    
            // Generation of embryos of grains
            if(spec_part_cell_number>0)
            for(int embryo_index_X = 0; embryo_index_X < embryo_number_X; embryo_index_X++)
            for(int embryo_index_Y = 0; embryo_index_Y < embryo_number_Y; embryo_index_Y++)
            for(int embryo_index_Z = 0; embryo_index_Z < embryo_number_Z; embryo_index_Z++)
            {
                // Coordinates of cell containing current embryo
                embryo_cell_coord_X = average_grain_size_X*embryo_index_X + average_grain_size_X/2.0;
                embryo_cell_coord_Y = average_grain_size_Y*embryo_index_Y + average_grain_size_Y/2.0;
                embryo_cell_coord_Z = average_grain_size_Z*embryo_index_Z + average_grain_size_Z/2.0;
                
                // Triple index of cell containing current embryo
                embryo_cell_indices = calcTripleIndex(embryo_cell_coord_X, embryo_cell_coord_Y, embryo_cell_coord_Z);
                
                embryo_cell_index_I = embryo_cell_indices.getI();
                embryo_cell_index_J = embryo_cell_indices.getJ();
                embryo_cell_index_K = embryo_cell_indices.getK();
                
                // Single index of cell containing embryo
                embryo_cell_index = calcSingleIndex(embryo_cell_index_I, embryo_cell_index_J, embryo_cell_index_K);
          
                // If the embryo is located in given layer
                if(layer_indices[embryo_cell_index] == _spec_part_index)
                {
                    embryos_number_1++;
                    embryos_number++;

                    // Change of state of considered cell
                    fr_spec.set(embryo_cell_index, embryos_number);
                    
                    System.out.println("Cell with single index "+embryo_cell_index+" and triple index ("+embryo_cell_index_I+", "+embryo_cell_index_J+", "+embryo_cell_index_K+
                          ") contains embryo # "+embryos_number_1+" ## "+embryos_number+" with indices ("+embryo_index_X+", "+embryo_index_Y+" "+embryo_index_Z+").");
                }
            }

          //  int cell_counter = 0;

            // Creation of single embryo if there are no embryos in specimen
        //    if(embryos_number_1 < 2 |expected_embryos_number<=1)
            if(embryos_number_1 < 2 & fr_spec.size()>0)
            if(spec_part_cell_number>0)
            {
                embryos_number_1 = 1;
                embryos_number++;

                for (int index = 0; index<fr_spec.size(); index++)
                if(layer_indices[index] == _spec_part_index)
                {
                    // Change of state of considered cell
                    fr_spec.set(index, embryos_number);

                    // TEST
                //  fr_spec.set(index, layer_indices[index]);

                //    cell_counter++;
                }

                System.out.println("The specimen is homogeneous and contains single grain # "+embryos_number_1+" ## "+embryos_number);
            }

            //TEST
            System.out.println();
            // TEST
            System.out.println("Average grain size:   "+average_grain_size_X+" "+average_grain_size_Y+" "+average_grain_size_Z);
            System.out.println("Average grain volume: "+average_grain_volume);
            System.out.println("Layer #"+_spec_part_index+" volume:         "+layer_volume);
         //   System.out.println("Number of cells in specimen: "+fr_spec.size());
            System.out.println("Number of cells in layer #"+_spec_part_index+": "+spec_part_cell_number);//+" = "+cell_counter);
            System.out.println("Expected number of embryos:  "+expected_embryos_number);
            System.out.println("Total number of embryos:     "+embryos_number_1);
            System.out.println();
        }

        /** The method generates embryos of cracks (dendrites) arranged randomly.
         * Each embryo is generated in specified volume.
         * @param layer_thickness thickness of upper surface layer where embryos cannot be generated
         */
        private void generateEmbryosRandomly(double layer_thickness)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRandomly(double layer_thickness)\n");
            
            // Total number of embryos
            embryos_number = 0;
            
            // Sizes of volume containing single embryo
            double volume_size_X = average_grain_size;
            double volume_size_Y = average_grain_size;
            double volume_size_Z = average_grain_size;
            
            // Embryo indices
            int embryo_index_I = -1;
            int embryo_index_J = -1;
            int embryo_index_K = -1;
            
            // Single index of embryo
            int embryo_index = -1;

            // Number of embryos at 1st, 2nd and 3rd coordination spheres.
            int neigbouring_embryos_number;

            // Coordinates of current cell
            VectorR3 coordinates;

            // Number of cell layers along axis Z in substrate
            int substrate_cell_layers_number = 0;

            // Number of cell layers along axis Z in surface layer
            int surface_cell_layers_number = cell_number_K;

            // Calculation of number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            for(int cell_layers_counter = 0; cell_layers_counter<cell_number_K; cell_layers_counter++)
            {
                // Coordinates of 1st cell in current layer
                coordinates = calcCoordinates(0, 0, cell_layers_counter);

                if(coordinates.getZ()>=layer_thickness)
                {
                    substrate_cell_layers_number = cell_number_K-cell_layers_counter;
                    surface_cell_layers_number   = cell_layers_counter;

                    // End of the cycle
                    cell_layers_counter = cell_number_K;
                }
            }
            
            // Number of cells in substrate
            double substrate_cell_number = frSpecSize*substrate_cell_layers_number/(double)cell_number_K;

            // Volume of substrate where embryos can be generated
            double substrate_volume = specimen_volume*substrate_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos
            double expected_embryos_number = substrate_volume/average_grain_volume;

            // Stochastic determination of indices of embryos
            for(int volume_counter_Z=0; volume_counter_Z<specimen_size_Z/average_grain_size; volume_counter_Z++)
            for(int volume_counter_Y=0; volume_counter_Y<specimen_size_Y/average_grain_size; volume_counter_Y++)
            for(int volume_counter_X=0; volume_counter_X<specimen_size_X/average_grain_size; volume_counter_X++)
            {
                // Stochastic choice of triple index of embryo
                embryo_index_I = (int)Math.floor(volume_size_X*volume_counter_X/1.732 + volume_size_X*Math.random()/1.732);
                embryo_index_J = (int)Math.floor(volume_size_Y*volume_counter_Y/2.000 + volume_size_Y*Math.random()/2.000);
                embryo_index_K = (int)Math.floor(volume_size_Z*volume_counter_Z/1.633 + volume_size_Z*Math.random()/1.633);

                embryo_index_I = Math.min(embryo_index_I, cell_number_I-1);
                embryo_index_J = Math.min(embryo_index_J, cell_number_J-1);
                embryo_index_K = Math.min(embryo_index_K, cell_number_K-1);

                System.out.println("Volume indices: "+volume_counter_X+" "+volume_counter_Y+" "+volume_counter_Z+"; ");
            //    System.out.println("Embryo indices: "+embryo_index_I+" "+embryo_index_J+" "+embryo_index_K);

                // Calculation of coordinates of cell
                coordinates = calcCoordinates(embryo_index_I, embryo_index_J, embryo_index_K);

            //    if(coordinates.getX() < specimen_size_X)
            //    if(coordinates.getY() < specimen_size_Y)
                if(coordinates.getZ() >=layer_thickness)
            //    if(coordinates.getZ() < specimen_size_Z)
                {
                    // Calculation of single index of embryo
                    embryo_index   = cell_number_I*cell_number_J*embryo_index_K+
                                     cell_number_I*embryo_index_J + embryo_index_I;

                    embryos_number++;

                    // Change of state of considered cell
                    frSpec.set(embryo_index, embryos_number);

                    // Printing of indices of embryos
             /*
                    try
                    {
                        bw.write("Cell with single index "+embryo_index+" and triple index ("+
                                  embryo_index_I+", "+embryo_index_J+", "+embryo_index_K+") "+
                                 "contains embryo # "+embryos_number);
                        bw.newLine();
                        bw.flush();
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }
              */

                    System.out.println("Cell with single index "+embryo_index+" and triple index ("+
                                        embryo_index_I+", "+embryo_index_J+", "+embryo_index_K+") "+
                                       "contains embryo # "+embryos_number);
                }
                else
                    System.out.println("The volume does not contain any embryo.");
            }
       /*
            try
            {
                bw.write("Number of embryos: " + embryos_number);
                bw.newLine();
                bw.flush();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
        */
            //TEST
            System.out.println();
            System.out.println("Number of cells in substrate: "+substrate_cell_number);
            System.out.println("Expected number of embryos:   "+expected_embryos_number);
            System.out.println("Number of embryos:            "+embryos_number);
            System.out.println();
       //     System.out.println("Number of embryos in large cluster = " + largeCluster.size());

      /*-------------------------------------------------------------------------------------------*/

         //   calcNeighbIndices(3, 3, 3);
        }

        /** The method generates embryos of cracks (dendrites) arranged randomly.
         * Each embryo is generated in specified volume.
         * @param layer_thickness thickness of upper surface layer where embryos cannot be generated
         * @param grain_layer_1_thick thickness of 1st grain layer
         */
        private void generateEmbryosRandomly(double layer_thickness, double grain_layer_1_thick)
        {            
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRandomly(double layer_thickness, double grain_layer_1_thick)\n");
            
            // Total number of embryos
            embryos_number = 0;

            // Number of embryos in 1st substrate layer
            embryos_number_1 = 0;

            // Number of embryos in 2nd substrate layer
            embryos_number_2 = 0;

            // Average size of grain in 2nd substrate layer
            average_grain_size_2 = average_grain_size;//*0.5;//

            // Average volume of grain in 1st substrate layer
            double average_grain_volume_1 = average_grain_size*average_grain_size*average_grain_size;

            // Average volume of grain in 2nd substrate layer
            double average_grain_volume_2 = average_grain_size_2*average_grain_size_2*average_grain_size_2;

            // Sizes of volume containing single embryo in 1st substrate
            double substrate_1_volume_size_X = average_grain_size;
            double substrate_1_volume_size_Y = average_grain_size;
            double substrate_1_volume_size_Z = average_grain_size;

            // Sizes of volume containing single embryo in 1st substrate
            double substrate_2_volume_size_X = average_grain_size_2;
            double substrate_2_volume_size_Y = average_grain_size_2;
            double substrate_2_volume_size_Z = average_grain_size_2;

            // Embryo indices
            int embryo_index_I = -1;
            int embryo_index_J = -1;
            int embryo_index_K = -1;

            // Single index of embryo
            int embryo_index = -1;

            // Number of embryos at 1st, 2nd and 3rd coordination spheres.
            int neigbouring_embryos_number;

            // Coordinates of current cell
            VectorR3 coordinates;

            // Number of cell layers along axis Z in substrate
            int substrate_cell_layers_number = 0;

            // Number of cell layers along axis Z in surface layer
            int surface_cell_layers_number = cell_number_K;

            // Cell coordinate Z
            double coord_Z = 0;

            // Number of cell layers in 1st substrate layer
            int substrate_1_cell_layers_number=0;

            // Number of cell layers in 2nd substrate layer
            int substrate_2_cell_layers_number=0;

            // Variable necessary in order to know whether sufrace layer thickness is known
            boolean surf_thick_is_known = false;

            // Calculation of number of cell layers along axis Z in substrate
            // (interface of substrate and surface layer is perpendicular to axis Z)
            for(int cell_layers_counter = 0; cell_layers_counter<cell_number_K; cell_layers_counter++)
            {
                // Coordinates of 1st cell in current layer
                coordinates = calcCoordinates(0, 0, cell_layers_counter);

                coord_Z = coordinates.getZ();

                if((coord_Z >= layer_thickness)&(!surf_thick_is_known))
                {
                    surface_cell_layers_number = cell_layers_counter;
                    substrate_cell_layers_number = cell_number_K-cell_layers_counter;
                    surf_thick_is_known = true;
                }

                if(coord_Z >= layer_thickness + grain_layer_1_thick)
                {
                    substrate_1_cell_layers_number = cell_layers_counter - surface_cell_layers_number;
                    substrate_2_cell_layers_number = substrate_cell_layers_number - substrate_1_cell_layers_number;

                    // End of the cycle
                    cell_layers_counter = cell_number_K;
                }
            }

            // TEST
            System.out.println("Layers: "+surface_cell_layers_number+" "+substrate_1_cell_layers_number+" "+substrate_2_cell_layers_number);

            // Number of cells in substrate
            double substrate_cell_number = frSpecSize*substrate_cell_layers_number/(double)cell_number_K;

            // Volume of substrate where embryos can be generated
            double substrate_volume = specimen_volume*substrate_cell_layers_number/(double)cell_number_K;

            // Number of cells in 1st substrate
            double substrate_1_cell_number = frSpecSize*substrate_1_cell_layers_number/(double)cell_number_K;

            // Volume of 1st substrate where embryos can be generated
            double substrate_1_volume = specimen_volume*substrate_1_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 1st substrate
            double expected_embryos_number_1 = substrate_1_volume/average_grain_volume_1;

            // Number of cells in 2nd substrate
            double substrate_2_cell_number = frSpecSize*substrate_2_cell_layers_number/(double)cell_number_K;

            // Volume of 2nd substrate where embryos can be generated
            double substrate_2_volume = specimen_volume*substrate_2_cell_layers_number/(double)cell_number_K;

            // Expected number of embryos in 2nd substrate
            double expected_embryos_number_2 = substrate_2_volume/average_grain_volume_2;

            // Expected number of embryos
            double expected_embryos_number = expected_embryos_number_1 + expected_embryos_number_2;

            // Stochastic determination of indices of embryos in 1st substrate layer
            for(int volume_counter_Z=0; volume_counter_Z<specimen_size_Z/average_grain_size; volume_counter_Z++)
            for(int volume_counter_Y=0; volume_counter_Y<specimen_size_Y/average_grain_size; volume_counter_Y++)
            for(int volume_counter_X=0; volume_counter_X<specimen_size_X/average_grain_size; volume_counter_X++)
            {
                // Stochastic choice of triple index of embryo
                embryo_index_I = (int)Math.floor(substrate_1_volume_size_X*volume_counter_X/1.732 + substrate_1_volume_size_X*Math.random()/1.732);
                embryo_index_J = (int)Math.floor(substrate_1_volume_size_Y*volume_counter_Y/2.000 + substrate_1_volume_size_Y*Math.random()/2.000);
                embryo_index_K = (int)Math.floor(substrate_1_volume_size_Z*volume_counter_Z/1.633 + substrate_1_volume_size_Z*Math.random()/1.633);

                embryo_index_I = Math.min(embryo_index_I, cell_number_I-1);
                embryo_index_J = Math.min(embryo_index_J, cell_number_J-1);
                embryo_index_K = Math.min(embryo_index_K, cell_number_K-1);

                System.out.println("Volume indices: "+volume_counter_X+" "+volume_counter_Y+" "+volume_counter_Z+"; ");
            //    System.out.println("Embryo indices: "+embryo_index_I+" "+embryo_index_J+" "+embryo_index_K);

                // Calculation of coordinates of cell
                coordinates = calcCoordinates(embryo_index_I, embryo_index_J, embryo_index_K);

                coord_Z = coordinates.getZ();

            //    if(coordinates.getX() < specimen_size_X)
            //    if(coordinates.getY() < specimen_size_Y)
                if((coord_Z >= layer_thickness)&(coord_Z < layer_thickness+grain_layer_1_thick))
            //    if(coordinates.getZ() < specimen_size_Z)
                {
                    // Calculation of single index of embryo
                    embryo_index   = cell_number_I*cell_number_J*embryo_index_K+
                                     cell_number_I*embryo_index_J + embryo_index_I;

                    embryos_number++;
                    embryos_number_1++;

                    // Change of state of considered cell
                    frSpec.set(embryo_index, embryos_number);

                    System.out.println("Cell with single index "+embryo_index+" and triple index ("+
                                        embryo_index_I+", "+embryo_index_J+", "+embryo_index_K+") "+
                                       "contains embryo # "+embryos_number);
                }
                else
                    System.out.println("The volume does not contain any embryo.");
            }

            // Stochastic determination of indices of embryos in 2nd substrate layer
            for(int volume_counter_Z=0; volume_counter_Z<specimen_size_Z/average_grain_size_2; volume_counter_Z++)
            for(int volume_counter_Y=0; volume_counter_Y<specimen_size_Y/average_grain_size_2; volume_counter_Y++)
            for(int volume_counter_X=0; volume_counter_X<specimen_size_X/average_grain_size_2; volume_counter_X++)
            {
                // Stochastic choice of triple index of embryo
                embryo_index_I = (int)Math.floor(substrate_2_volume_size_X*volume_counter_X/1.732 + substrate_2_volume_size_X*Math.random()/1.732);
                embryo_index_J = (int)Math.floor(substrate_2_volume_size_Y*volume_counter_Y/2.000 + substrate_2_volume_size_Y*Math.random()/2.000);
                embryo_index_K = (int)Math.floor(substrate_2_volume_size_Z*volume_counter_Z/1.633 + substrate_2_volume_size_Z*Math.random()/1.633);

                embryo_index_I = Math.min(embryo_index_I, cell_number_I-1);
                embryo_index_J = Math.min(embryo_index_J, cell_number_J-1);
                embryo_index_K = Math.min(embryo_index_K, cell_number_K-1);

                System.out.println("Volume indices: "+volume_counter_X+" "+volume_counter_Y+" "+volume_counter_Z+"; ");
            //    System.out.println("Embryo indices: "+embryo_index_I+" "+embryo_index_J+" "+embryo_index_K);

                // Calculation of coordinates of cell
                coordinates = calcCoordinates(embryo_index_I, embryo_index_J, embryo_index_K);

                coord_Z = coordinates.getZ();

            //    if(coordinates.getX() < specimen_size_X)
            //    if(coordinates.getY() < specimen_size_Y)
                if((coord_Z >= layer_thickness+grain_layer_1_thick))
            //    if(coordinates.getZ() < specimen_size_Z)
                {
                    // Calculation of single index of embryo
                    embryo_index   = cell_number_I*cell_number_J*embryo_index_K+
                                     cell_number_I*embryo_index_J + embryo_index_I;

                    embryos_number++;
                    embryos_number_2++;

                    // Change of state of considered cell
                    frSpec.set(embryo_index, embryos_number);

                    System.out.println("Cell with single index "+embryo_index+" and triple index ("+
                                        embryo_index_I+", "+embryo_index_J+", "+embryo_index_K+") "+
                                       "contains embryo # "+embryos_number);
                }
                else
                    System.out.println("The volume does not contain any embryo.");
            }

            //TEST
            System.out.println();
            System.out.println("Number of cells in 1st substrate layer: "+substrate_1_cell_number);
            System.out.println("Expected number of embryos in 1st substrate layer:   "+expected_embryos_number_1);
            System.out.println("Number of embryos in 1st substrate layer: "+embryos_number_1);
            System.out.println();
            System.out.println("Number of cells in 2nd substrate layer: "+substrate_2_cell_number);
            System.out.println("Expected number of embryos in 2nd substrate layer:   "+expected_embryos_number_2);
            System.out.println("Number of embryos in 2nd substrate layer: "+embryos_number_2);
            System.out.println();
            System.out.println("Number of cells in substrate: "+substrate_cell_number);
            System.out.println("Expected number of embryos:   "+expected_embryos_number);
            System.out.println("Total number of embryos:      "+embryos_number);
            System.out.println();
        }
        
        /** The method generates embryos of cracks (dendrites) arranged randomly in specimen part between planes;
         * each embryo is generated in specified volume.
         * @param fr_spec considered specimen
         * @param average_grain_size average size of grain
         * @param min_coord_X coordinate X of left intersected plane
         * @param max_coord_X coordinate X of right intersected plane
         * @param min_coord_Y coordinate Y of front intersected plane
         * @param max_coord_Y coordinate Y of back intersected plane
         * @param min_coord_Z coordinate Z of upper intersected plane
         * @param max_coord_Z coordinate Z of lower intersected plane
         */        
        private void generateEmbryosRandomly(GrainsHCPSpecimenR3 fr_spec, double average_grain_size, double min_coord_X, double max_coord_X, 
                                                             double min_coord_Y, double max_coord_Y, double min_coord_Z, double max_coord_Z)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRandomly(GrainsHCPSpecimenR3 fr_spec, double average_grain_size, double min_coord_X, double max_coord_X, \n" +
                               "                                                             double min_coord_Y, double max_coord_Y, double min_coord_Z, double max_coord_Z)\n");
            
            // Total number of embryos
            embryos_number_1 = 0;
            
            // Average volume of grain
            average_grain_volume = average_grain_size*average_grain_size*average_grain_size;
            
            // Embryo indices
            int embryo_index_I = -1;
            int embryo_index_J = -1;
            int embryo_index_K = -1;
            
            // Single index of embryo
            int embryo_index = -1;
            
            // Expected number of embryos in specimen part between intersected planes
            double expected_embryos_number = (max_coord_X-min_coord_X)*(max_coord_Y-min_coord_Y)*(max_coord_Z-min_coord_Z)/average_grain_volume;
            
            //TEST
            System.out.println("---------==========================-----------");
            System.out.println("min_coord_X = "+min_coord_X);
            System.out.println("max_coord_X = "+max_coord_X);
            System.out.println("min_coord_Y = "+min_coord_Y);
            System.out.println("max_coord_Y = "+max_coord_Y);
            System.out.println("min_coord_Z = "+min_coord_Z);
            System.out.println("max_coord_Z = "+max_coord_Z);
            System.out.println("-----------------------");
            System.out.println("average_grain_size      = "+average_grain_size);
            System.out.println("average_grain_volume    = "+average_grain_volume);
            System.out.println("expected_embryos_number = "+expected_embryos_number);
            System.out.println("---------==========================-----------");
            
            // Coordinates of embryo
            VectorR3 embryo_coord;
            
            // Number of cells in considered part of specimen
            int specimen_part_cell_number = 0;
            
            // Cell coordinates
            VectorR3 coordinates;            
            double coord_X;
            double coord_Y;
            double coord_Z;
            
            // Calculation of number of cell layers in considered part of specimen
            if(expected_embryos_number>0)
            for(int cell_index_I = 0; cell_index_I<cell_number_I; cell_index_I++)
            for(int cell_index_J = 0; cell_index_J<cell_number_J; cell_index_J++)
            for(int cell_index_K = 0; cell_index_K<cell_number_K; cell_index_K++)
            {
                coordinates = calcCoordinates(cell_index_I, cell_index_J, cell_index_K);
                coord_X     = coordinates.getX();
                coord_Y     = coordinates.getY();
                coord_Z     = coordinates.getZ();
                
                if(coord_X>=min_coord_X & coord_X<max_coord_X)
                if(coord_Y>=min_coord_Y & coord_Y<max_coord_Y)
                if(coord_Z>=min_coord_Z & coord_Z<max_coord_Z)
                    specimen_part_cell_number++;
            }
            
            // if(expected_embryos_number>=1)
            if(specimen_part_cell_number>0)
            // Stochastic determination of indices of embryos in substrate part
            for(int volume_counter_Z=0; volume_counter_Z<specimen_size_Z/average_grain_size; volume_counter_Z++)
            for(int volume_counter_Y=0; volume_counter_Y<specimen_size_Y/average_grain_size; volume_counter_Y++)
            for(int volume_counter_X=0; volume_counter_X<specimen_size_X/average_grain_size; volume_counter_X++)
            {
                // Stochastic choice of triple index of embryo
                embryo_index_I = (int)Math.floor(average_grain_size*volume_counter_X/1.732 + average_grain_size*Math.random()/1.732);
                embryo_index_J = (int)Math.floor(average_grain_size*volume_counter_Y/2.000 + average_grain_size*Math.random()/2.000);
                embryo_index_K = (int)Math.floor(average_grain_size*volume_counter_Z/1.633 + average_grain_size*Math.random()/1.633);

                embryo_index_I = Math.min(embryo_index_I, cell_number_I-1);
                embryo_index_J = Math.min(embryo_index_J, cell_number_J-1);
                embryo_index_K = Math.min(embryo_index_K, cell_number_K-1);

             //   System.out.println("Volume indices: "+volume_counter_X+" "+volume_counter_Y+" "+volume_counter_Z);

                embryo_coord = calcCoordinates(embryo_index_I, embryo_index_J, embryo_index_K);
                
                coord_X = embryo_coord.getX();
                coord_Y = embryo_coord.getY();
                coord_Z = embryo_coord.getZ();

                if(coord_X>=min_coord_X & coord_X<max_coord_X)
                if(coord_Y>=min_coord_Y & coord_Y<max_coord_Y)
                if(coord_Z>=min_coord_Z & coord_Z<max_coord_Z)
                {
                  // Calculation of single index of embryo
                  embryo_index   = cell_number_I*cell_number_J*embryo_index_K+
                                   cell_number_I*embryo_index_J + embryo_index_I;

                  embryos_number_1++;
                  embryos_number++;

                  // Change of state of considered cell
                  fr_spec.set(embryo_index, embryos_number);

                  System.out.println("Cell with single index "+embryo_index+" and triple index ("+
                                      embryo_index_I+", "+embryo_index_J+", "+embryo_index_K+") "+
                                     "contains embryo # "+embryos_number_1+" in layer, # "+embryos_number+" in specimen.");
                }
            }

          //  int cell_counter = 0;

            // Creation of single grain if there are no embryos in specimen
            if(embryos_number_1 == 0 & specimen_part_cell_number>0)
            {
                embryos_number_1++;
                embryos_number++;

                for (int index = 0; index<fr_spec.size(); index++)
                {
                    // Change of state of considered cell
                    fr_spec.set(index, embryos_number);

              //      cell_counter++;
                }

                System.out.println("The specimen is homogeneous and contains single grain # "+embryos_number);
            }

            //TEST
            System.out.println();
            System.out.println("Number of cells in specimen: "+fr_spec.size());
            System.out.println("Expected number of embryos:   "+expected_embryos_number);
            System.out.println("Total number of embryos:      "+embryos_number_1);
            System.out.println();
        }
                       
        /** The method generates embryos of cracks (dendrites) arranged randomly
         * in specimen part between two intersected planes.
         * Each embryo is generated in specified volume.
         * @param fr_spec considered specimen
         * @param average_grain_size average size of grain
         */
        private void generateEmbryosRandomly(GrainsHCPSpecimenR3 fr_spec, double average_grain_size_X, double average_grain_size_Y, double average_grain_size_Z, byte _spec_part_index)
        {
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosRandomly(GrainsHCPSpecimenR3 fr_spec, double average_grain_size_X, double average_grain_size_Y, double average_grain_size_Z, byte _spec_part_index)\n");
            
            // Total number of embryos
            embryos_number_1 = 0;

            // Average volume of grain in specimen
            average_grain_volume = average_grain_size_X*average_grain_size_Y*average_grain_size_Z;

            // Number of cells in considered part of specimen
            int spec_part_cell_number = 0;
            int specimen_cell_number = fr_spec.size();

            // Index of specimen part containing current cell
            byte spec_part_index;
            
            Three cell_indices;
            
            int cell_index_I, 
                cell_index_J, 
                cell_index_K;
            int max_cell_index_I = 0, 
                max_cell_index_J = 0, 
                max_cell_index_K = 0;
            int min_cell_index_I = cell_number_I-1, 
                min_cell_index_J = cell_number_J-1, 
                min_cell_index_K = cell_number_K-1;
            
        //    ArrayList spec_part_cell_indices = new ArrayList(0);

            // Calculation of number of cells in considered part of specimen
            for(int cell_counter = 0; cell_counter<fr_spec.size(); cell_counter++)
            {
                spec_part_index = layer_indices[cell_counter];

                if(spec_part_index == _spec_part_index)
                {
                    cell_indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);
                    
                    cell_index_I = cell_indices.getI();
                    cell_index_J = cell_indices.getJ();
                    cell_index_K = cell_indices.getK();
                    
                    if(cell_index_I < min_cell_index_I) min_cell_index_I = cell_index_I;
                    if(cell_index_J < min_cell_index_J) min_cell_index_J = cell_index_J;
                    if(cell_index_K < min_cell_index_K) min_cell_index_K = cell_index_K;
                    
                    if(cell_index_I > max_cell_index_I) max_cell_index_I = cell_index_I;
                    if(cell_index_J > max_cell_index_J) max_cell_index_J = cell_index_J;
                    if(cell_index_K > max_cell_index_K) max_cell_index_K = cell_index_K;
                    
                //    spec_part_cell_indices.add(cell_counter);
                    spec_part_cell_number++;
                }
            }
            
            // Sizes of specimen layer along axes expressed in cell layers
            int layer_cell_number_X = max_cell_index_I - min_cell_index_I + 1;
            int layer_cell_number_Y = max_cell_index_J - min_cell_index_J + 1;
            int layer_cell_number_Z = max_cell_index_K - min_cell_index_K + 1;

            // Layer volume
            double layer_volume = specimen_volume*spec_part_cell_number/specimen_cell_number;

            // Expected number of embryos in 1st specimen
            double expected_embryos_number = layer_volume/average_grain_volume;
            
            // Indices of embryo
            int embryo_index_I, embryo_index_J, embryo_index_K;
            
            // Single index of embryo
            int embryo_index;
            
            double vol_number_X = (specimen_size_X/average_grain_size_X)*layer_cell_number_X*1.0/cell_number_I;
            double vol_number_Y = (specimen_size_Y/average_grain_size_Y)*layer_cell_number_Y*1.0/cell_number_J;
            double vol_number_Z = (specimen_size_Z/average_grain_size_Z)*layer_cell_number_Z*1.0/cell_number_K;
            
            //TEST
            System.out.println("----------==========================----------");
            System.out.println("specimen_volume         = "+specimen_volume);
            System.out.println("specimen_cell_number    = "+specimen_cell_number);
            System.out.println("----------------------------");
            System.out.println("layer_volume            = "+layer_volume);
            System.out.println("spec_part_cell_number   = "+spec_part_cell_number);
            System.out.println("----------------------------");
            System.out.println("specimen_size_X         = "+specimen_size_X);
            System.out.println("specimen_size_Y         = "+specimen_size_Y);
            System.out.println("specimen_size_Z         = "+specimen_size_Z);
            System.out.println("----------------------------");
            System.out.println("layer_cell_number_X     = "+layer_cell_number_X);
            System.out.println("layer_cell_number_Y     = "+layer_cell_number_Y);
            System.out.println("layer_cell_number_Z     = "+layer_cell_number_Z);
            System.out.println("----------------------------");
            System.out.println("cell_number_I           = "+cell_number_I);
            System.out.println("cell_number_J           = "+cell_number_J);
            System.out.println("cell_number_K           = "+cell_number_K);
            System.out.println("----------------------------");
            System.out.println("average_grain_size_X    = "+average_grain_size_X);
            System.out.println("average_grain_size_Y    = "+average_grain_size_Y);
            System.out.println("average_grain_size_Z    = "+average_grain_size_Z);
            System.out.println("average_grain_volume    = "+average_grain_volume);
            System.out.println("expected_embryos_number = "+expected_embryos_number);
            System.out.println("----------------------------");
            System.out.println("vol_number_X            = "+vol_number_X);
            System.out.println("vol_number_Y            = "+vol_number_Y);
            System.out.println("vol_number_Z            = "+vol_number_Z);
            System.out.println("----------==========================----------");
            
            double vol_ratio_X=0, vol_ratio_Y=0, vol_ratio_Z=0;
            int min_index_I, max_index_I, min_index_J, max_index_J, min_index_K, max_index_K;
            
            // Stochastic determination of indices of embryos in 1st substrate layer
            if(spec_part_cell_number>0)
            for(int volume_counter_Z=0; volume_counter_Z<vol_number_Z; volume_counter_Z++)
            for(int volume_counter_Y=0; volume_counter_Y<vol_number_Y; volume_counter_Y++)
            for(int volume_counter_X=0; volume_counter_X<vol_number_X; volume_counter_X++)
            {
                vol_ratio_X = vol_number_X - volume_counter_X;
                vol_ratio_Y = vol_number_Y - volume_counter_Y;
                vol_ratio_Z = vol_number_Z - volume_counter_Z;                
                
                if(vol_ratio_X > 1) vol_ratio_X = 1;
                if(vol_ratio_Y > 1) vol_ratio_Y = 1;
                if(vol_ratio_Z > 1) vol_ratio_Z = 1;
                
                min_index_I = min_cell_index_I + (int)Math.floor(average_grain_size_X*volume_counter_X*Math.sqrt(1.0/3.0));
                max_index_I = min_cell_index_I + (int)Math.floor(average_grain_size_X*(volume_counter_X + vol_ratio_X)*Math.sqrt(1.0/3.0));
                
                min_index_J = min_cell_index_J + (int)Math.floor(average_grain_size_Y*volume_counter_Y*0.5);
                max_index_J = min_cell_index_J + (int)Math.floor(average_grain_size_Y*(volume_counter_Y + vol_ratio_Y)*0.5);
                
                min_index_K = min_cell_index_K + (int)Math.floor(average_grain_size_Z*volume_counter_Z*Math.sqrt(3.0/8.0));
                max_index_K = min_cell_index_K + (int)Math.floor(average_grain_size_Z*(volume_counter_Z + vol_ratio_Z)*Math.sqrt(3.0/8.0));
                
                min_index_I = Math.max(min_cell_index_I, min_index_I);
                max_index_I = Math.min(max_cell_index_I, max_index_I);
                max_index_I = Math.max(min_index_I, max_index_I);
                min_index_I = Math.min(min_index_I, max_index_I);
                
                min_index_J = Math.max(min_cell_index_J, min_index_J);
                max_index_J = Math.min(max_cell_index_J, max_index_J);
                max_index_J = Math.max(min_index_J, max_index_J);
                min_index_J = Math.min(min_index_J, max_index_J);
                
                min_index_K = Math.max(min_cell_index_K, min_index_K);
                max_index_K = Math.min(max_cell_index_K, max_index_K);
                max_index_K = Math.max(min_index_K, max_index_K);
                min_index_K = Math.min(min_index_K, max_index_K);
                
                System.out.println(" - - - - - - - - - - - - - - - - - -");
                System.out.println("Volume indices:         "+volume_counter_X+" "+volume_counter_Y+" "+volume_counter_Z);
                System.out.println("Volume ratios:          "+vol_ratio_X+" "+vol_ratio_Y+" "+vol_ratio_Z);
                System.out.println("Minimal embryo index I: "+min_index_I);
                System.out.println("Maximal embryo index I: "+max_index_I);
                System.out.println("Minimal embryo index J: "+min_index_J);
                System.out.println("Maximal embryo index J: "+max_index_J);
                System.out.println("Minimal embryo index K: "+min_index_K);
                System.out.println("Maximal embryo index K: "+max_index_K);
                        
             // Stochastic choice of triple index of embryo
             // embryo_index_I = (int)Math.floor(average_grain_size*volume_counter_X/1.732 + average_grain_size*Math.random()/1.732);
             // embryo_index_J = (int)Math.floor(average_grain_size*volume_counter_Y/2.000 + average_grain_size*Math.random()/2.000);
             // embryo_index_K = (int)Math.floor(average_grain_size*volume_counter_Z/1.633 + average_grain_size*Math.random()/1.633);                
                
                embryo_index_I = min_cell_index_I + (int)Math.floor(average_grain_size_X*(volume_counter_X + vol_ratio_X*Math.random())*Math.sqrt(1.0/3.0));
                embryo_index_J = min_cell_index_J + (int)Math.floor(average_grain_size_Y*(volume_counter_Y + vol_ratio_Y*Math.random())*0.5);
                embryo_index_K = min_cell_index_K + (int)Math.floor(average_grain_size_Z*(volume_counter_Z + vol_ratio_Z*Math.random())*Math.sqrt(3.0/8.0));

                embryo_index_I = Math.min(embryo_index_I, max_index_I);
                embryo_index_I = Math.max(embryo_index_I, min_index_I);
                embryo_index_J = Math.min(embryo_index_J, max_index_J);
                embryo_index_J = Math.max(embryo_index_J, min_index_J);
                embryo_index_K = Math.min(embryo_index_K, max_index_K);
                embryo_index_K = Math.max(embryo_index_K, min_index_K);
                
                // Calculation of single index of embryo
                embryo_index   = cell_number_I*cell_number_J*embryo_index_K + cell_number_I*embryo_index_J + embryo_index_I;
                
                spec_part_index = layer_indices[embryo_index];
                
                System.out.println("Cell with single index "+embryo_index+" and triple index ("+embryo_index_I+", "+
                                   embryo_index_J+", "+embryo_index_K+") is located in layer # "+spec_part_index+".");

                if(spec_part_index == _spec_part_index)
                {
                  embryos_number_1++;
                  embryos_number++;

                  // Change of state of considered cell
                  fr_spec.set(embryo_index, embryos_number);

                  System.out.println("Cell with single index "+embryo_index+" and triple index ("+embryo_index_I+", "+
                                     embryo_index_J+", "+embryo_index_K+") "+"contains embryo # "+embryos_number_1+" ## "+embryos_number);
                }
            }

          //  int cell_counter = 0;

            // Creation of single embryo if there are no embryos in specimen
      //    if(embryos_number_1 < 2 |expected_embryos_number<=1)
            if(embryos_number_1 < 1 & spec_part_cell_number > 0)
            {
                embryos_number_1 = 1;
                embryos_number++;

                for (int index = 0; index<fr_spec.size(); index++)
                if(layer_indices[index] == _spec_part_index)
                {
                    // Change of state of considered cell
                    fr_spec.set(index, embryos_number);

                    // TEST
                //  fr_spec.set(index, layer_indices[index]);

                //    cell_counter++;
                }

                System.out.println("The specimen is homogeneous and contains single grain # "+embryos_number_1+" ## "+embryos_number);
            }

            //TEST
            System.out.println();
            // TEST
            System.out.println("Average sizes of grain:   "+average_grain_size_X+" "+average_grain_size_Y+" "+average_grain_size_Z);
            System.out.println("Average volume of grain: "+average_grain_volume);
            System.out.println("Layer #"+_spec_part_index+" volume:         "+layer_volume);
         //   System.out.println("Number of cells in specimen: "+fr_spec.size());
            System.out.println("Number of cells in layer #"+_spec_part_index+": "+spec_part_cell_number);//+" = "+cell_counter);
            System.out.println("Expected number of embryos:  "+expected_embryos_number);
            System.out.println("Total number of embryos:     "+embryos_number_1);
            System.out.println();
        }
        
        /** The method generates embryos of cracks on both sides 
         * of internal boundary (weld joint).
         */
        public void generateEmbryosOnWeldJoint()
        {     
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosOnWeldJoint()\n");
            
            // Total number of embryos of clusters
            embryos_number = 10;
            
            // Width of joint weld
            int weld_width = 20;
            
            // Triple index of embryo
            Three tripleIndexOfEmbryo;
            
            // Embryo indices
            int indexI;
            int indexJ;
            int indexK;
            
            // Variable for counting of clusters
            int cluster_counter = 0;

           // Generation of embryos of cracks.
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                tripleIndexOfEmbryo = cell_indices[cell_counter]; 
                   
                indexI = tripleIndexOfEmbryo.getI();
                indexJ = tripleIndexOfEmbryo.getJ();
                indexK = tripleIndexOfEmbryo.getK();
                
                if((embryos_number/2)*(indexI+cell_number_I/embryos_number+1)%cell_number_I == 0)
                if(Math.abs(cell_number_J/2-0.5 - indexJ) == weld_width/2+0.5)
                {
                    cluster_counter++;
                    
                    // Change of state of considered cell
                    frSpec.set(cell_counter, cluster_counter);
                    
                    try
                    {
                        bw.write("Cell with single index " + cell_counter + " and triple index (" +
                                 tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                 tripleIndexOfEmbryo.getK() + ") " + " belongs to cluster # " + cluster_counter);
                        bw.newLine();
                        bw.flush();
                    }
                    catch(IOException io_exc)
                    {
                        System.out.println("Error: " + io_exc);
                    }
                    
                    System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                       tripleIndexOfEmbryo.getI() + ", " + tripleIndexOfEmbryo.getJ() + ", " +
                                       tripleIndexOfEmbryo.getK() + ") " + " belongs to the cluster " + cluster_counter);                    
                }
                
                // Generation of weld joint
                if(Math.abs(cell_number_J/2-0.5 - indexJ) <= weld_width/2-0.5)
                {
                    // Change of state of cell at weld joint
                    frSpec.set(cell_counter, (byte)(embryos_number+1));
                }
            }
            
            try
            {
                bw.write("Number of embryos = " + embryos_number);
                bw.newLine();
                bw.flush();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
            
            System.out.println("Number of embryos = " + embryos_number);
        //    System.out.println("Number of embryos in large cluster = " + largeCluster.size());
        }
        
        /** The method generates embryos of cracks (dendrites) on facets of specimen.         
         */
        public void generateEmbryosOnFacets()
        {    
            System.out.println("\nCURRENT METHOD: GrainsHCPTask.generateEmbryosOnFacets()\n");
            
            // Total number of embryos of clusters
            embryos_number = 0;
            
            // Number of embryos at 1st, 2nd and 3rd coordination spheres
            int neigbouring_embryos_number;
            
            // Expected number of embryos
            double expected_embryos_number = probGeneration * frSpecSize;
            
            // Triple index of cell
            Three triple_index;
            
            // Counter of cells on facets of specimen being considered during spatial cycle
            int facet_cells_counter = 0;
            
            // Number of cells on facets of specimen being considered during spatial cycle
            int facet_cells_number = cell_number_I*(cell_number_J-1)+
                                     cell_number_J*(cell_number_K-1)+
                                     cell_number_K*(cell_number_I-1)+1;
                
            // Generation of embryos of cracks
            for (int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)            
            {
              triple_index = cell_indices[cell_counter];
                            
              if((triple_index.getI()==0)|(triple_index.getJ()==0)|(triple_index.getK()==0))
              {
                // Calculation of embryo generation probability
                probGeneration = (expected_embryos_number-embryos_number)/(facet_cells_number-facet_cells_counter);
                probGeneration = Math.min(1, probGeneration);
              
                // Calculation of number of considered cells on specimen facets
                facet_cells_counter++;
                  
                // Generation of random "long" number
                rand = new Random();
                long_rand = rand.nextLong();

      //          System.out.println("Probability of generation of embryo in cell " + cell_counter + 
      //                             " equals " + probGeneration);

                if((long_rand >= probGeneration*min_value)&
                   (long_rand <= probGeneration*max_value))
                {
                    neigbouring_embryos_number = 0;
                
                    // Determination of total number of embryos at 1st, 2nd and 3rd 
                    // coordination spheres of the considered cell
                    for(int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
                    if(neighbours3D[cell_counter][neighb_counter]>-1)
                    if(frSpec.get(neighbours3D[cell_counter][neighb_counter]) != undamaged_cell)
                    {
                       neigbouring_embryos_number++;
                       neighb_counter = 54;
                    }
                                                                        
                    // If there is no embryos at 1st, 2nd and 3rd spheres then the state
                    // of the cell is changed and the new cluster containing this cell appears
                    if(neigbouring_embryos_number == 0)
                    {   
                        embryos_number++;
                        
                        // Change of state of considered cell
                        frSpec.set(cell_counter, embryos_number);
                        
                        try
                        {
                            bw.write("Cell with single index " + cell_counter + " and triple index (" +
                                    triple_index.getI() + ", " + triple_index.getJ() + ", " +
                                    triple_index.getK() + ") " + " belongs to the cluster " + embryos_number);
                            bw.newLine();
                            bw.flush();
                        }
                        catch(IOException io_exc)
                        {
                            System.out.println("Error: " + io_exc);
                        }
                           
                        System.out.println("Cell with single index " + cell_counter + " and triple index (" +
                                    triple_index.getI() + ", " + triple_index.getJ() + ", " +
                                    triple_index.getK() + ") " + " belongs to the cluster " + embryos_number);
                    }
                }
              }
            }
            
            try
            {
                bw.write("Number of embryos = " + embryos_number);
                bw.newLine();
                bw.flush();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
            
            System.out.println("Number of embryos = " + embryos_number);
       //     System.out.println("Number of embryos in large cluster = " + largeCluster.size());
                        
      //-------------------------------------------------------------------------------------------
            
         //   calcNeighbIndices(3, 3, 3);
        }
    
        /** The method calculates the number of damaged neighbours at 1st, 2nd and 3rd coordination spheres
         * of given cell and the number of cluster of the cell's damaged neighbour at 1st coordination sphere.
         * @param _frSpec specimen containing given cell
         * @param cellIndex index of given cell
         * @return the probability of damage of given cell and the number of cluster containing the cell
         */
        private int calcClusterNumber(GrainsHCPSpecimenR3 _frSpec, int cellIndex, boolean calc_anis_cosinus)
        {
            //  Cosinus of angle formed by vector from considered cell 
            // to damaged neighbour at 1st coordination sphere and vector of inner anisotropy
            double cosinus_inner = 0;
            
            //  Cosinus of angle formed by vector from considered cell 
            // to damaged neighbour at 1st coordination sphere and vector of outer anisotropy
            double cosinus_outer = 0;
            
            // Array of numbers of neighbour cells at 1st sphere in each grain
            int[] neihb1S_cells_in_grains = new int[embryos_number+1];
            
            // Array of numbers of neighbour cells at 2nd sphere in each grain
            int[] neihb2S_cells_in_grains = new int[embryos_number+1];
            
            // Array of numbers of neighbour cells at 3rd sphere in each grain
            int[] neihb3S_cells_in_grains = new int[embryos_number+1];
            
            // Maximum of numbers of neighbour cells in each grain
            int maximum = 0;
            
            // Single indices of damaged cells at 1st coordination sphere
            int[] damagedNeighb1SIndices = new int[12];
            
            for(int index_counter = 0; index_counter<12; index_counter++)
                damagedNeighb1SIndices[index_counter]  =-1;
            
           // Indices of considered cell
         // indices = cell_indices[cellIndex];
            
            // Numbers of damaged cells at 1st, 2nd and 3rd coordination spheres (from the same cluster)
            int damageNumber1 = 0;
            int damageNumber2 = 0;
            int damageNumber3 = 0;
            
       //   int _frSpecSize = _frSpec.size();
            
            clusterIndex = 0;
          //  index = -1;
            int neighbour1S_index = -1;
            int neighbour2S_index = -1;
            int neighbour3S_index = -1;
            int neighb_neighb1S_index = -1;
            
            int neighbour1S_state = undamaged_cell;
            int neighbour2S_state = undamaged_cell;
            int neighbour3S_state = undamaged_cell;
            
            // Number of cells, which came in the state "1" at current time step
            int numberOfCellsSwitchedAtCurrentStep = 0;
            
            // Vector from cell centre to centre of damaged neighbour at 1st coordination sphere
            VectorR3 neighb_vector;
            
            Three  cell_tr_index;
            VectorR3 cell_coords   = new VectorR3();
            VectorR3 growth_vector = new VectorR3();
            
            //TEST
       /*   
            for (int neighbours3D_counter = 0; neighbours3D_counter < 54; neighbours3D_counter++)
            {
                System.out.print("Cell # "+cellIndex+" "); 
                
                if(neighbours3D[cellIndex][neighbours3D_counter]>-1)            
                    System.out.print(neighbours3D[cellIndex][neighbours3D_counter]+" ");
                
                System.out.println();
            }
       */   
           Date date_0 = new Date();
           
           /* Calculation of number of cells, which came in the state "1" at current time step,
            * at 1st, 2nd and 3rd coordination spheres of the considered cell
            */
            for (int neighbours3D_counter = 0; neighbours3D_counter < 54; neighbours3D_counter++)
            if(neighbours3D[cellIndex][neighbours3D_counter]>-1)
      //    if(largeTempCluster.contains(neighbours3D[cellIndex][neighbours3D_counter]))
            if(frSpecFuture.get(neighbours3D[cellIndex][neighbours3D_counter]) != undamaged_cell)
            {
                numberOfCellsSwitchedAtCurrentStep++;
                neighbours3D_counter = 54;
            }
            
            Date date_1 = new Date();
            time_period[0] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 1st coordination 
            // sphere in each cluster
            for (int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                // Single index of neighbour at 1st coordination sphere
                neighbour1S_index = neighbours3D[cellIndex][neighb_counter];

                // Neighbour must be located in the same layer that considered cell
                if((neighbour1S_index>-1))
                if(layer_indices[neighbour1S_index] == layer_indices[cellIndex])
                {   
                    // State of neighbour at 1st coordination sphere
                    neighbour1S_state = _frSpec.get(neighbour1S_index);
                    
                    if (neighbour1S_state != undamaged_cell)
                        // Calculation of numbers of neighbour cells in each grain
                        neihb1S_cells_in_grains[neighbour1S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[1] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 2nd coordination 
            // sphere in each cluster
            for (int neighb2S_counter = 12; neighb2S_counter < 42; neighb2S_counter++)
            {
                // Single index of neighbour at 2nd coordination sphere
                neighbour2S_index = neighbours3D[cellIndex][neighb2S_counter];

                // Neighbour must be located in the same layer that considered cell
                if(neighbour2S_index > -1)
                if(layer_indices[neighbour2S_index] == layer_indices[cellIndex])
                {
                    // State of neighbour at 2nd coordination sphere
                    neighbour2S_state = _frSpec.get(neighbour2S_index);
                    
                    if (neighbour2S_state != undamaged_cell)                     
                        // Calculation of numbers of neighbour cells in each grain
                        neihb2S_cells_in_grains[neighbour2S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[2] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 3rd coordination
            // sphere in each cluster
            for (int neighb3S_counter = 42; neighb3S_counter < 54; neighb3S_counter++)
            {
                // Single index of neighbour at 3rd coordination sphere
                neighbour3S_index = neighbours3D[cellIndex][neighb3S_counter];

                // Neighbour must be located in the same layer that considered cell
                if((neighbour3S_index>-1))
                if(layer_indices[neighbour3S_index] == layer_indices[cellIndex])
                {
                    // State of neighbour at 3rd coordination sphere
                    neighbour3S_state = _frSpec.get(neighbour3S_index);
                    
                    if (neighbour3S_state != undamaged_cell)
                        // Calculation of numbers of neighbour cells in each grain
                        neihb3S_cells_in_grains[neighbour3S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[3] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
     /*       
            //TEST
            System.out.print("Cell # "+cellIndex+":");
            for(int grain_counter = 0; grain_counter<embryos_number+1; grain_counter++)
                System.out.print(" "+neihb_cells_in_grains[grain_counter]);
      */      
            // Seeking of maximum from numbers of neighbours1S in each grain
            for(int grain_counter = 1; grain_counter < embryos_number+1; grain_counter++)
            {
                if(neihb1S_cells_in_grains[grain_counter]>maximum)
                {
                    maximum = neihb1S_cells_in_grains[grain_counter];
                    clusterIndex = grain_counter;
                }
                
                if(neihb1S_cells_in_grains[grain_counter]==maximum)
                {
                    // Comparison of numbers of neighbours2S in each of two grains
                    if(neihb2S_cells_in_grains[grain_counter]>neihb2S_cells_in_grains[clusterIndex])
                        clusterIndex = grain_counter;
                    
                    if(neihb2S_cells_in_grains[grain_counter]==neihb2S_cells_in_grains[clusterIndex])
                        // Comparison of numbers of neighbours3S in each of two grains
                        if(neihb3S_cells_in_grains[grain_counter]>neihb3S_cells_in_grains[clusterIndex])
                            clusterIndex = grain_counter;
                }
            }
            
            date_1 = new Date();
            time_period[4] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
       /*     
            //TEST
            System.out.print(". "+maximum+" cells in grain # "+clusterIndex);
        */    
       
         //   life_periods[cellIndex] = 0;
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            // Calculation of number of neighbour cells in considered cluster
            for (int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                // Single index of neighbour at 1st coordination sphere
                neighbour1S_index = neighbours3D[cellIndex][neighb_counter];
                
                // Neighbour must be located in the same layer that considered cell
                if((neighbour1S_index>-1))
                if(layer_indices[neighbour1S_index] == layer_indices[cellIndex])
                {   
                    // State of neighbour at 1st coordination sphere
                    neighbour1S_state = _frSpec.get(neighbour1S_index);
                    
                    if(neighbour1S_state != undamaged_cell)
                    if(neighbour1S_state == clusterIndex)
                    {   
                        // Calculation of number of neighbour cells in considered cluster
                        damageNumber1++;
                        
                      //  System.out.println("cellIndex = "+cellIndex+"; damageNumber1 =  "+damageNumber1+"; clusterIndex = "+clusterIndex);
                        
                     // if(damageNumber1 == 1)
                        if(damageNumber1 >= 1)
                        {
                            // Obtaining of single index of neighbour cell in considered cluster
                            damagedNeighb1SIndices[damageNumber1-1] = neighbour1S_index;
                            
                            // Calculation of vector from considered cell to neighbour cells in considered cluster
                            neighb_vector = resid_vectors[neighb_counter];
                            
                            // Indices of current neighbour from adjacent grain
                            cell_tr_index = calcTripleIndex(cellIndex, cell_number_I, cell_number_J, cell_number_K);
                            
                            // Calculation of coordinates of cell
                            cell_coords = calcCoordinates(cell_tr_index.getI(), cell_tr_index.getJ(), cell_tr_index.getK());
                            
                            // Vector from grain embryo to current cell
                            growth_vector = residial(cell_coords, embryo_coords[neighbour1S_state - 1]);
                            
                            if(calc_anis_cosinus)
                            {
                              //   Calculation of cosinus of angle formed by vector from considered cell 
                              // to neighbour cell in considered cluster and vector of inner anisotropy
                           //   cosinus_inner = cosinus(neighb_vector, inner_vector);
                              cosinus_inner = cosinus(growth_vector, inner_vector);
                        
                              //   Calculation of cosinus of angle formed by vector from considered cell 
                              // to neighbour cell in considered cluster and vector of outer anisotropy
                          //    cosinus_outer = cosinus(neighb_vector, outer_vector);
                              cosinus_outer = cosinus(growth_vector, outer_vector);
                            }
                            else
                            {
                                cosinus_inner = 1;
                                cosinus_outer = 1;
                            }
                            
                       //     if(damageNumber1 > 1)
                              // Period of presence of current cell near one of clusters
                              life_periods[cellIndex] += life_periods[neighbour1S_index]/12;
                        }
                    }
                }
            }
            
            date_1 = new Date();
            time_period[5] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
      /*      
            //TEST
            System.out.println(". "+damageNumber1+"-"+maximum+"="+(damageNumber1-maximum));
            if(damageNumber1-maximum != 0)
                System.out.println("ERROR!!!ERROR!!!ERROR!!!ERROR!!!ERROR!!!ERROR!!!");
      */      
        /*
            int neighb_neighb_counter;
            
            // Exclusion of thickness for the case of large number (>2) of neighbour cells
            if(damageNumber1 > 2)
                damageNumber1 = 0;
            else
            // Exclusion of possibility of cluster closing
            if(damageNumber1 > 1)
            {                       
                for(int damage1S_counter = 0; damage1S_counter<damageNumber1-1; damage1S_counter++)
                {     
                    // Index of 1st neighbour
                    neighbour1S_index = damagedNeighb1SIndices[damage1S_counter];
                
                    // Search of 1st neighbour at 1st coordination sphere of 2nd neighbour
                    for(neighb_neighb_counter = 0; neighb_neighb_counter<12; neighb_neighb_counter++)
                    {
                        // Single index of cell at 1st coordination sphere of 2nd neighbour
                        neighb_neighb1S_index = neighbours3D[damagedNeighb1SIndices[damage1S_counter+1]][neighb_neighb_counter];
                                       
                        // Case of location of 1st neighbour at 1st coordination sphere of 2nd neighbour
                        if(neighb_neighb1S_index == neighbour1S_index)
                        {    
                            // End of current cycle
                            neighb_neighb_counter = 13;
                        }
                    }
                    
                    // Case of location of 1st neighbour out of 1st coordination sphere of 2nd neighbour
                    if(neighb_neighb_counter < 13)
                    {
                        // Exclusion of possibility of cluster closing
                        damageNumber1 = 0;
                        clusterIndex  = undamaged_cell;
                             
                        // Calculation of cosinuses of above-mentioned vectors
                        cosinus_inner = 0;
                        cosinus_outer = 0;
                    }                        
                }
            }   
      */
            
            //TEST
     //     System.out.print("Cell # "+cellIndex+" can go to cluster # " + clusterIndex);
     //       if(damageNumber1 == 2)
     //       System.out.println(". damageNumber1 = "+damageNumber1);
            //END OF TEST
            
            // Calculation of number of damaged neighbours at 2nd and 3rd coordination spheres, which are neighbours
            // at 1st coordination sphere of damaged neighbour at 1st coordination sphere of considered cell
            if (damageNumber1 == 1)
            {                
                damageNumber2 = calcNumberOfDamagedNeighbours2(_frSpec, cellIndex, damagedNeighb1SIndices[damageNumber1-1], clusterIndex);
                damageNumber3 = calcNumberOfDamagedNeighbours3(_frSpec, cellIndex, damagedNeighb1SIndices[damageNumber1-1], clusterIndex);    
                
            //    System.out.println("embryo coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+"; prob.: "+probDamage);
            }
            
            date_1 = new Date();
            time_period[6] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            //TEST
     //       if(writeDamagedNeighbours == true)
     //         System.out.println(cellIndex+" "+damageNumber1+" "+damageNumber2+" "+damageNumber3);
            
            probDamage = calcProbDamage(damageNumber1, damageNumber2, damageNumber3);
            
         //   probDamage = calcProbDamageForParalCalc(damageNumber1, damageNumber2, damageNumber3);
        /*    
            if(damageNumber1 > 0)
            {
            //  System.out.println("ERROR!!! Method 'calcProbDamageForParalCalc(damageNumber1, damageNumber2, damageNumber3)' is wrong!!!");
              System.out.println("damageNumber1 = "+damageNumber1+"; damageNumber2 = "+damageNumber2+"; damageNumber3 = "+damageNumber3);
              System.out.println("probDamage = "+probDamage+"; probParalCalc = "+probParalCalc);
            }
      
            if (damageNumber1 == 2)
                System.out.println(cellIndex+" 1st neighb: "+ damagedNeighb1SIndices[0]
                                            +" 2nd neighb: "+ damagedNeighb1SIndices[1]);
       */   
      
            // Period of presence of current cell near one of clusters
         //   if(damageNumber1 >= 1)
         //     life_periods[cellIndex]++;
            
            double abs_diff = 0;
            double gr_vec_length = growth_vector.getLength();
            double rand = Math.random();
            
            if(Math.abs(cosinus_inner) > 1)
                cosinus_inner = Math.signum(cosinus_inner);
            
            if(Math.abs(cosinus_outer) > 1)
                cosinus_outer = Math.signum(cosinus_outer);
            
       //     if(damageNumber1 > 0)
          //    System.out.println("cell coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+"; prob.: "+probDamage);
            
          //  if(damageNumber1 > 0)
          //    probDamage = 0.1;
          
        //  System.out.println("coeff_inner_anisotropy: "+coeff_inner_anisotropy+"; coeff_outer_anisotropy: "+coeff_outer_anisotropy);
          
            // period of growth of clusters in seconds
            growth_period = 0.0025;
        
            // Recalculation of switch probability according to anisotropy
            if(calc_anis_cosinus)
            if(coeff_inner_anisotropy > 0 | coeff_outer_anisotropy > 0)
            if(clusterIndex != undamaged_cell)
            if(Math.abs(cosinus_inner) < 1 | Math.abs(cosinus_outer) < 1)
            if(probDamage > 0)
            if(damageNumber1 > 0)
            {
           //   probDamage = probDamage*Math.pow(coeff_inner_anisotropy, cosinus_inner - 1);// + coeff_outer_anisotropy*cosinus_outer;
              
             if(rand > 0.475)
             {
           //    System.out.println("Grn# "+clusterIndex+"; cell coord-s: "+cell_coords.writeToString()+"; emb.coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+ ";\n  "+
           //    "gr.vec: "+growth_vector.writeToString()+"; in.vec: "+inner_vector.writeToString()+"; cos.in: "+cosinus_inner+"; init.prob: "+probDamage);
               
               if(damageNumber1 <= 2)// 3)// 
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 10*(cosinus_inner - 1));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, gr_vec_length*(cosinus_inner - 1));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, (cosinus_inner - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 4*(cosinus_inner - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 100*(cosinus_inner - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 20*(cosinus_inner - 1)/(1.0 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -40*Math.pow(-0.5*(cosinus_inner - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
               
               if(damageNumber1 >= 3)
             //  probDamage = probDamage/(gr_vec_length*gr_vec_length*gr_vec_length*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -12*gr_vec_length/(max_gr_vec_length+gr_vec_length));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -gr_vec_length/10);
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, - 0.07*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, (cosinus_inner - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 4*(cosinus_inner - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 100*(cosinus_inner - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -40*Math.pow(-0.5*(cosinus_inner - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
               
           //    System.out.println("; prob.: "+probDamage);
             }
             else
             {
          //     System.out.println("Grn# "+clusterIndex+"; cell coord-s: "+cell_coords.writeToString()+"; emb.coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+ ";\n  "+
          //     "gr.vec: "+growth_vector.writeToString()+"; out.vec: "+outer_vector.writeToString()+"; cos.out= "+cosinus_outer+"; init.prob: "+probDamage);
               
               if(damageNumber1 <= 2)// 3)// 
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 10*(cosinus_inner - 1));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, gr_vec_length*(cosinus_outer - 1));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, (cosinus_outer - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 4*(cosinus_outer - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 100*(cosinus_outer - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -40*Math.pow(-0.5*(cosinus_outer - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
                   
               if(damageNumber1 >= 3)
             //  probDamage = probDamage/(gr_vec_length*gr_vec_length*gr_vec_length*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -12*gr_vec_length/(max_gr_vec_length+gr_vec_length));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -gr_vec_length/10);
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, - 0.07*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, (cosinus_outer - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 4*(cosinus_outer - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 100*(cosinus_outer - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -40*Math.pow(-0.5*(cosinus_outer - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
               
       //        System.out.println("; prob.: "+probDamage);
             }
             
             // Period of presence of current cell near one of clusters
          //   life_periods[cellIndex] = 0;
             
          // if(damageNumber1 >= 6 & gr_vec_length != 0)
          //     probDamage = probDamage/(gr_vec_length*gr_vec_length*gr_vec_length);
             if(gr_vec_length > max_gr_vec_length)
                 max_gr_vec_length = gr_vec_length;
             
          //    abs_diff = 0.5*Math.abs(cosinus_inner*Math.abs(cosinus_inner) + cosinus_inner*cosinus_inner 
          //                          - cosinus_outer*Math.abs(cosinus_outer) - cosinus_outer*cosinus_outer);
            
          //    if(abs_diff < 1.0E-10) 
          //      abs_diff = 1.0E-10;
            
           //   probDamage *= Math.exp(1 - 1/abs_diff);
           //   probDamage *= abs_diff*abs_diff*abs_diff*abs_diff;
            }
            
            if(clusterIndex - 1 >= embryo_coords.length | clusterIndex < 1)
            {
                probDamage = 0;
            }
         //   else
           //     System.out.println("clusterIndex: "+clusterIndex+"; prob.: "+probDamage+"; damageNumber1 = "+damageNumber1);
            
            if(probDamage<0)
                probDamage = 0;
            
            if(probDamage>1)
                probDamage = 1;
            
            date_1 = new Date();
            time_period[7] += date_1.getTime() - date_0.getTime();
            
        /*
            // TEST
            if(probDamage>0)
            {
                System.out.println("Cell number " + cellIndex + ": ");
                System.out.println("Neighbours: "+ damageNumber1 + ", " + damageNumber2 + ", " + damageNumber3);
                System.out.println("The probability of damage: " + probDamage);
            }
            // END OF TEST
        */
            
            // Stochastic determination of cell damage
        /* 
            rand = new Random();
            long_rand = rand.nextLong();
        */ 
            int _clusterNumber = undamaged_cell;
                    
            if((long_rand > probDamage*min_value)&(long_rand < probDamage*max_value))
            {
                _clusterNumber = clusterIndex;                
            }
            
            return _clusterNumber;
        }
        
        
        /** The method calculates the number of damaged neighbours at 1st, 2nd and 3rd coordination spheres
         * without using of if-operators.
         * of given cell and the number of cluster of the cell's damaged neighbour at 1st coordination sphere.
         * @param _frSpec specimen containing given cell
         * @param cellIndex index of given cell
         * @return the probability of damage of given cell and the number of cluster containing the cell
         */
        private int calcClusterNumberForParalCalc(GrainsHCPSpecimenR3 _frSpec, int cellIndex, boolean calc_anis_cosinus)
        {
            //  Cosinus of angle formed by vector from considered cell 
            // to damaged neighbour at 1st coordination sphere and vector of inner anisotropy
            double cosinus_inner = 0;
            
            //  Cosinus of angle formed by vector from considered cell 
            // to damaged neighbour at 1st coordination sphere and vector of outer anisotropy
            double cosinus_outer = 0;
            
            // Array of numbers of neighbour cells at 1st sphere in each grain
            int[] neihb1S_cells_in_grains = new int[embryos_number+1];
            
            // Array of numbers of neighbour cells at 2nd sphere in each grain
            int[] neihb2S_cells_in_grains = new int[embryos_number+1];
            
            // Array of numbers of neighbour cells at 3rd sphere in each grain
            int[] neihb3S_cells_in_grains = new int[embryos_number+1];
            
            // Maximum of numbers of neighbour cells in each grain
            int maximum = 0;
            
            // Single indices of damaged cells at 1st coordination sphere
            int[] damagedNeighb1SIndices = new int[12];
            
            for(int index_counter = 0; index_counter<12; index_counter++)
                damagedNeighb1SIndices[index_counter]  =-1;
            
           // Indices of considered cell
         // indices = cell_indices[cellIndex];
            
            // Numbers of damaged cells at 1st, 2nd and 3rd coordination spheres (from the same cluster)
            int damageNumber1 = 0;
            int damageNumber2 = 0;
            int damageNumber3 = 0;
            
       //   int _frSpecSize = _frSpec.size();
            
            clusterIndex = 0;
          //  index = -1;
            int neighbour1S_index = -1;
            int neighbour2S_index = -1;
            int neighbour3S_index = -1;
            int neighb_neighb1S_index = -1;
            
            int neighbour1S_state = undamaged_cell;
            int neighbour2S_state = undamaged_cell;
            int neighbour3S_state = undamaged_cell;
            
            // Number of cells, which came in the state "1" at current time step
            int numberOfCellsSwitchedAtCurrentStep = 0;
            
            // Vector from cell centre to centre of damaged neighbour at 1st coordination sphere
            VectorR3 neighb_vector;
            
            Three  cell_tr_index;
            VectorR3 cell_coords   = new VectorR3();
            VectorR3 growth_vector = new VectorR3();
            
            // Layer index of current neighbour at 1st coordination sphere
            int neighb_layer_index;
            
            // Variable equals 1 if there is a neighbour cell from cluster.
            int damageNumber1_few = 0;
            
            // Variable equals 1 if current neighbour cell is located in cluster.
            int damaged_neighb_cell = 0;
            
            //TEST
       /*   
            for (int neighbours3D_counter = 0; neighbours3D_counter < 54; neighbours3D_counter++)
            {
                System.out.print("Cell # "+cellIndex+" "); 
                
                if(neighbours3D[cellIndex][neighbours3D_counter]>-1)            
                    System.out.print(neighbours3D[cellIndex][neighbours3D_counter]+" ");
                
                System.out.println();
            }
       */   
           Date date_0 = new Date();
           
           /* Calculation of number of cells, which came in the state "1" at current time step,
            * at 1st, 2nd and 3rd coordination spheres of the considered cell
            */
            for (int neighbours3D_counter = 0; neighbours3D_counter < 54; neighbours3D_counter++)
            if(neighbours3D[cellIndex][neighbours3D_counter]>-1)
      //    if(largeTempCluster.contains(neighbours3D[cellIndex][neighbours3D_counter]))
            if(frSpecFuture.get(neighbours3D[cellIndex][neighbours3D_counter]) != undamaged_cell)
            {
                numberOfCellsSwitchedAtCurrentStep++;
                neighbours3D_counter = 54;
            }
            
            Date date_1 = new Date();
            time_period[0] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 1st coordination 
            // sphere in each cluster
            for (int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                // Single index of neighbour at 1st coordination sphere
                neighbour1S_index = neighbours3D[cellIndex][neighb_counter];

                // Neighbour must be located in the same layer that considered cell
                if((neighbour1S_index>-1))
                if(layer_indices[neighbour1S_index] == layer_indices[cellIndex])
                {   
                    // State of neighbour at 1st coordination sphere
                    neighbour1S_state = _frSpec.get(neighbour1S_index);
                    
                    if (neighbour1S_state != undamaged_cell)
                        // Calculation of numbers of neighbour cells in each grain
                        neihb1S_cells_in_grains[neighbour1S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[1] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 2nd coordination 
            // sphere in each cluster
            for (int neighb2S_counter = 12; neighb2S_counter < 42; neighb2S_counter++)
            {
                // Single index of neighbour at 2nd coordination sphere
                neighbour2S_index = neighbours3D[cellIndex][neighb2S_counter];

                // Neighbour must be located in the same layer that considered cell
                if(neighbour2S_index > -1)
                if(layer_indices[neighbour2S_index] == layer_indices[cellIndex])
                {
                    // State of neighbour at 2nd coordination sphere
                    neighbour2S_state = _frSpec.get(neighbour2S_index);
                    
                    if (neighbour2S_state != undamaged_cell)                     
                        // Calculation of numbers of neighbour cells in each grain
                        neihb2S_cells_in_grains[neighbour2S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[2] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 3rd coordination
            // sphere in each cluster
            for (int neighb3S_counter = 42; neighb3S_counter < 54; neighb3S_counter++)
            {
                // Single index of neighbour at 3rd coordination sphere
                neighbour3S_index = neighbours3D[cellIndex][neighb3S_counter];

                // Neighbour must be located in the same layer that considered cell
                if((neighbour3S_index>-1))
                if(layer_indices[neighbour3S_index] == layer_indices[cellIndex])
                {
                    // State of neighbour at 3rd coordination sphere
                    neighbour3S_state = _frSpec.get(neighbour3S_index);
                    
                    if (neighbour3S_state != undamaged_cell)
                        // Calculation of numbers of neighbour cells in each grain
                        neihb3S_cells_in_grains[neighbour3S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[3] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
     /*       
            //TEST
            System.out.print("Cell # "+cellIndex+":");
            for(int grain_counter = 0; grain_counter<embryos_number+1; grain_counter++)
                System.out.print(" "+neihb_cells_in_grains[grain_counter]);
      */      
            // Seeking of maximum from numbers of neighbours1S in each grain
            for(int grain_counter = 1; grain_counter < embryos_number+1; grain_counter++)
            {
                if(neihb1S_cells_in_grains[grain_counter]>maximum)
                {
                    maximum = neihb1S_cells_in_grains[grain_counter];
                    clusterIndex = grain_counter;
                }
                
                if(neihb1S_cells_in_grains[grain_counter]==maximum)
                {
                    // Comparison of numbers of neighbours2S in each of two grains
                    if(neihb2S_cells_in_grains[grain_counter]>neihb2S_cells_in_grains[clusterIndex])
                        clusterIndex = grain_counter;
                    
                    if(neihb2S_cells_in_grains[grain_counter]==neihb2S_cells_in_grains[clusterIndex])
                        // Comparison of numbers of neighbours3S in each of two grains
                        if(neihb3S_cells_in_grains[grain_counter]>neihb3S_cells_in_grains[clusterIndex])
                            clusterIndex = grain_counter;
                }
            }
            
            date_1 = new Date();
            time_period[4] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
                        
            // Indices of current neighbour from adjacent grain
            cell_tr_index = calcTripleIndex(cellIndex, cell_number_I, cell_number_J, cell_number_K);
                            
            // Calculation of coordinates of cell
            cell_coords = calcCoordinates(cell_tr_index.getI(), cell_tr_index.getJ(), cell_tr_index.getK());
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            // Calculation of number of neighbour cells at 1st coordination sphere in considered cluster 
            for (int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                // Single index of neighbour at 1st coordination sphere
                neighbour1S_index = neighbours3D[cellIndex][neighb_counter];
                
                if(neighbour1S_index > -1)   
                {
                  // State of current neighbour at 1st coordination sphere
                  neighbour1S_state  = _frSpec.get(neighbour1S_index);
                  
                  // Layer index of current neighbour at 1st coordination sphere
                  neighb_layer_index = layer_indices[neighbour1S_index];
                  
                  // Vector from grain embryo to current cell
                  growth_vector = residial(cell_coords, embryo_coords[neighbour1S_state - 1]);
                  
                  life_periods[cellIndex] += life_periods[neighbour1S_index]/12;
                }
                else
                {
                  neighbour1S_state  = undamaged_cell;
                  neighb_layer_index = -1;
                  growth_vector = new VectorR3(1, 0, 0);
                }
                
                damaged_neighb_cell = ((1 + (int)Math.signum(neighbour1S_index + 0.5))/2)*((1 + (int)Math.signum(neighbour1S_state - 0.5))/2)*
                                      ((1 + (int)Math.signum(0.5 - Math.abs(neighb_layer_index - layer_indices[cellIndex])))/2)*
                                      ((1 + (int)Math.signum(0.5 - Math.abs(neighbour1S_state  - clusterIndex)))/2);
                
                // Neighbour must be located in the same layer that considered cell
                // Current neighbour cell must belong to grain with maximal number of neighbours
                damageNumber1+= damaged_neighb_cell;
                    
                if(damageNumber1 == 1)  
                  // Obtaining of single index of neighbour cell in considered cluster
                  damagedNeighb1SIndices[damageNumber1-1] = neighbour1S_index;
                   
               // damageNumber1_few = (1 + (int)Math.signum(damageNumber1 - 0.5))/2;  
                
                // Calculation of vector from considered cell to neighbour cells in considered cluster
                // neighb_vector = resid_vectors[neighb_counter];
                
                //   Calculation of cosinus of angle formed by vector from considered cell 
                // to neighbour cell in considered cluster and vector of inner anisotropy
                //   cosinus_inner = cosinus(neighb_vector, inner_vector);
                cosinus_inner += damaged_neighb_cell*cosinus(growth_vector, inner_vector);
                        
                //   Calculation of cosinus of angle formed by vector from considered cell 
                // to neighbour cell in considered cluster and vector of outer anisotropy
                //    cosinus_outer = cosinus(neighb_vector, outer_vector);
                cosinus_outer += damaged_neighb_cell*cosinus(growth_vector, outer_vector);
            }
            
            if(damageNumber1 >= 1)  
            {
              cosinus_inner = cosinus_inner/damageNumber1;
              cosinus_outer = cosinus_outer/damageNumber1;
            }
            
            date_1 = new Date();
            time_period[5] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            // Calculation of number of damaged neighbours at 2nd and 3rd coordination spheres, which are neighbours
            // at 1st coordination sphere of damaged neighbour at 1st coordination sphere of considered cell
            if(damageNumber1 == 1)
            {                
                damageNumber2 = calcNumberOfDamagedNeighbours2(_frSpec, cellIndex, damagedNeighb1SIndices[damageNumber1-1], clusterIndex);
                damageNumber3 = calcNumberOfDamagedNeighbours3(_frSpec, cellIndex, damagedNeighb1SIndices[damageNumber1-1], clusterIndex);    
                
            //    System.out.println("embryo coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+"; prob.: "+probDamage);
            }
            
            date_1 = new Date();
            time_period[6] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            //TEST
     //       if(writeDamagedNeighbours == true)
     //         System.out.println(cellIndex+" "+damageNumber1+" "+damageNumber2+" "+damageNumber3);
            
         //   probDamage = calcProbDamage(damageNumber1, damageNumber2, damageNumber3);
            
            probDamage = calcProbDamageForParalCalc(damageNumber1, damageNumber2, damageNumber3);
        /*    
            if(damageNumber1 > 0)
            {
            //  System.out.println("ERROR!!! Method 'calcProbDamageForParalCalc(damageNumber1, damageNumber2, damageNumber3)' is wrong!!!");
              System.out.println("damageNumber1 = "+damageNumber1+"; damageNumber2 = "+damageNumber2+"; damageNumber3 = "+damageNumber3);
              System.out.println("probDamage = "+probDamage+"; probParalCalc = "+probParalCalc);
            }
      
            if (damageNumber1 == 2)
                System.out.println(cellIndex+" 1st neighb: "+ damagedNeighb1SIndices[0]
                                            +" 2nd neighb: "+ damagedNeighb1SIndices[1]);
       */   
      
            // Period of presence of current cell near one of clusters
         //   if(damageNumber1 >= 1)
         //     life_periods[cellIndex]++;
            
            double abs_diff = 0;
            double gr_vec_length = growth_vector.getLength();
            double rand = Math.random();
            
            if(Math.abs(cosinus_inner) > 1)
                cosinus_inner = Math.signum(cosinus_inner);
            
            if(Math.abs(cosinus_outer) > 1)
                cosinus_outer = Math.signum(cosinus_outer);
            
       //     if(damageNumber1 > 0)
          //    System.out.println("cell coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+"; prob.: "+probDamage);
            
          //  if(damageNumber1 > 0)
          //    probDamage = 0.1;
          
        //  System.out.println("coeff_inner_anisotropy: "+coeff_inner_anisotropy+"; coeff_outer_anisotropy: "+coeff_outer_anisotropy);
          
            // period of growth of clusters in seconds
            growth_period = 0.0025;
        
            // Recalculation of switch probability according to anisotropy
            if(calc_anis_cosinus)
            if(coeff_inner_anisotropy > 0 | coeff_outer_anisotropy > 0)
            if(clusterIndex != undamaged_cell)
            if(Math.abs(cosinus_inner) < 1 | Math.abs(cosinus_outer) < 1)
            if(probDamage > 0)
            if(damageNumber1 > 0)
            {
           //   probDamage = probDamage*Math.pow(coeff_inner_anisotropy, cosinus_inner - 1);// + coeff_outer_anisotropy*cosinus_outer;
              
             if(rand > 0.475)
             {
           //    System.out.println("Grn# "+clusterIndex+"; cell coord-s: "+cell_coords.writeToString()+"; emb.coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+ ";\n  "+
           //    "gr.vec: "+growth_vector.writeToString()+"; in.vec: "+inner_vector.writeToString()+"; cos.in: "+cosinus_inner+"; init.prob: "+probDamage);
               
               if(damageNumber1 <= 2)// 3)// 
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 10*(cosinus_inner - 1));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, gr_vec_length*(cosinus_inner - 1));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, (cosinus_inner - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 4*(cosinus_inner - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 100*(cosinus_inner - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 20*(cosinus_inner - 1)/(1.0 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -40*Math.pow(-0.5*(cosinus_inner - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
               
               if(damageNumber1 >= 3)
             //  probDamage = probDamage/(gr_vec_length*gr_vec_length*gr_vec_length*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -12*gr_vec_length/(max_gr_vec_length+gr_vec_length));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -gr_vec_length/10);
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, - 0.07*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, (cosinus_inner - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 4*(cosinus_inner - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_inner_anisotropy, 100*(cosinus_inner - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_inner_anisotropy, -40*Math.pow(-0.5*(cosinus_inner - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
               
           //    System.out.println("; prob.: "+probDamage);
             }
             else
             {
          //     System.out.println("Grn# "+clusterIndex+"; cell coord-s: "+cell_coords.writeToString()+"; emb.coord-s: "+embryo_coords[clusterIndex - 1].writeToString()+ ";\n  "+
          //     "gr.vec: "+growth_vector.writeToString()+"; out.vec: "+outer_vector.writeToString()+"; cos.out= "+cosinus_outer+"; init.prob: "+probDamage);
               
               if(damageNumber1 <= 2)// 3)// 
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 10*(cosinus_inner - 1));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, gr_vec_length*(cosinus_outer - 1));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, (cosinus_outer - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 4*(cosinus_outer - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 100*(cosinus_outer - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -40*Math.pow(-0.5*(cosinus_outer - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
                   
               if(damageNumber1 >= 3)
             //  probDamage = probDamage/(gr_vec_length*gr_vec_length*gr_vec_length*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -12*gr_vec_length/(max_gr_vec_length+gr_vec_length));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -gr_vec_length/10);
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, - 0.07*gr_vec_length);
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, (cosinus_outer - 1)*10.0/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 4*(cosinus_outer - 1)*gr_vec_length/Math.log10(10 + life_periods[cellIndex]));
             //  probDamage = probDamage*Math.pow(coeff_outer_anisotropy, 100*(cosinus_outer - 1)/Math.log10(10 + life_periods[cellIndex]/growth_period));
                 probDamage = probDamage*Math.pow(coeff_outer_anisotropy, -40*Math.pow(-0.5*(cosinus_outer - 1), 1)/(1.0 + life_periods[cellIndex]/growth_period));
               
       //        System.out.println("; prob.: "+probDamage);
             }
             
             // Period of presence of current cell near one of clusters
          //   life_periods[cellIndex] = 0;
             
          // if(damageNumber1 >= 6 & gr_vec_length != 0)
          //     probDamage = probDamage/(gr_vec_length*gr_vec_length*gr_vec_length);
             if(gr_vec_length > max_gr_vec_length)
                 max_gr_vec_length = gr_vec_length;
             
          //    abs_diff = 0.5*Math.abs(cosinus_inner*Math.abs(cosinus_inner) + cosinus_inner*cosinus_inner 
          //                          - cosinus_outer*Math.abs(cosinus_outer) - cosinus_outer*cosinus_outer);
            
          //    if(abs_diff < 1.0E-10) 
          //      abs_diff = 1.0E-10;
            
           //   probDamage *= Math.exp(1 - 1/abs_diff);
           //   probDamage *= abs_diff*abs_diff*abs_diff*abs_diff;
            }
            
            if(clusterIndex - 1 >= embryo_coords.length | clusterIndex < 1)
            {
                probDamage = 0;
            }
         //   else
           //     System.out.println("clusterIndex: "+clusterIndex+"; prob.: "+probDamage+"; damageNumber1 = "+damageNumber1);
            
            if(probDamage<0)
                probDamage = 0;
            
            if(probDamage>1)
                probDamage = 1;
            
            date_1 = new Date();
            time_period[7] += date_1.getTime() - date_0.getTime();
            
        /*
            // TEST
            if(probDamage>0)
            {
                System.out.println("Cell number " + cellIndex + ": ");
                System.out.println("Neighbours: "+ damageNumber1 + ", " + damageNumber2 + ", " + damageNumber3);
                System.out.println("The probability of damage: " + probDamage);
            }
            // END OF TEST
        */
            
            // Stochastic determination of cell damage
        /* 
            rand = new Random();
            long_rand = rand.nextLong();
        */ 
            int _clusterNumber = undamaged_cell;
                    
            if((long_rand > probDamage*min_value)&(long_rand < probDamage*max_value))
            {
                _clusterNumber = clusterIndex;                
            }
            
            return _clusterNumber;
        }
        
        /** The method calculates the number of damaged neighbours at 1st, 2nd and 3rd coordination spheres
         * of given cell and the number of cluster of the cell's damaged neighbour at 1st coordination sphere.
         * @param _frSpec specimen containing given cell
         * @param cellIndex index of given cell
         * @return the probability of damage of given cell and the number of cluster containing the cell
         */
        private int calcClusterNumberForAcicularStructure(GrainsHCPSpecimenR3 _frSpec, int cellIndex, boolean calc_anis_cosinus)
        {
            //  Cosinus of angle formed by vector from considered cell 
            // to damaged neighbour at 1st coordination sphere and vector of inner anisotropy
            double cosinus_inner = 0;
            
            //  Cosinus of angle formed by vector from considered cell 
            // to damaged neighbour at 1st coordination sphere and vector of outer anisotropy
            double cosinus_outer = 0;
            
            // Array of numbers of neighbour cells at 1st sphere in each grain
            int[] neihb1S_cells_in_grains = new int[embryos_number+1];
            
            // Array of numbers of neighbour cells at 2nd sphere in each grain
            int[] neihb2S_cells_in_grains = new int[embryos_number+1];
            
            // Array of numbers of neighbour cells at 3rd sphere in each grain
            int[] neihb3S_cells_in_grains = new int[embryos_number+1];
            
            // Maximum of numbers of neighbour cells in each grain
            int maximum = 0;
            
            // Single indices of damaged cells at 1st coordination sphere
            int[] damagedNeighb1SIndices = new int[12];
            
            for(int index_counter = 0; index_counter<12; index_counter++)
                damagedNeighb1SIndices[index_counter]  =-1;
            
           // Indices of considered cell
         // indices = cell_indices[cellIndex];
            
            // Numbers of damaged cells at 1st, 2nd and 3rd coordination spheres (from the same cluster)
            int damageNumber1 = 0;
            int damageNumber2 = 0;
            int damageNumber3 = 0;
            
       //   int _frSpecSize = _frSpec.size();
            
            clusterIndex = 0;
          //  index = -1;
            int neighbour1S_index = -1;
            int neighbour2S_index = -1;
            int neighbour3S_index = -1;
            int neighb_neighb1S_index = -1;
            
            int neighbour1S_state = undamaged_cell;
            int neighbour2S_state = undamaged_cell;
            int neighbour3S_state = undamaged_cell;
            
            // Number of cells, which came in the state "1" at current time step
            int numberOfCellsSwitchedAtCurrentStep = 0;
            
            // Vector from cell centre to centre of damaged neighbour at 1st coordination sphere
            VectorR3 neighb_vector;
            
            //TEST
       /*   
            for (int neighbours3D_counter = 0; neighbours3D_counter < 54; neighbours3D_counter++)
            {
                System.out.print("Cell # "+cellIndex+" "); 
                
                if(neighbours3D[cellIndex][neighbours3D_counter]>-1)            
                    System.out.print(neighbours3D[cellIndex][neighbours3D_counter]+" ");
                
                System.out.println();
            }
       */   
           Date date_0 = new Date();
           
           /* Calculation of number of cells, which came in the state "1" at current time step,
            * at 1st, 2nd and 3rd coordination spheres of the considered cell
            */
            for (int neighbours3D_counter = 0; neighbours3D_counter < 54; neighbours3D_counter++)
            if(neighbours3D[cellIndex][neighbours3D_counter]>-1)
      //    if(largeTempCluster.contains(neighbours3D[cellIndex][neighbours3D_counter]))
            if(frSpecFuture.get(neighbours3D[cellIndex][neighbours3D_counter]) != undamaged_cell)
            {
                numberOfCellsSwitchedAtCurrentStep++;
                neighbours3D_counter = 54;
            }
            
            Date date_1 = new Date();
            time_period[0] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 1st coordination 
            // sphere in each cluster
            for (int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                // Single index of neighbour at 1st coordination sphere
                neighbour1S_index = neighbours3D[cellIndex][neighb_counter];

                // Neighbour must be located in the same layer that considered cell
                if((neighbour1S_index>-1))
                if(layer_indices[neighbour1S_index] == layer_indices[cellIndex])
                {   
                    // State of neighbour at 1st coordination sphere
                    neighbour1S_state = _frSpec.get(neighbour1S_index);
                    
                    if (neighbour1S_state != undamaged_cell)
                        // Calculation of numbers of neighbour cells in each grain
                        neihb1S_cells_in_grains[neighbour1S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[1] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 2nd coordination 
            // sphere in each cluster
            for (int neighb2S_counter = 12; neighb2S_counter < 42; neighb2S_counter++)
            {
                // Single index of neighbour at 2nd coordination sphere
                neighbour2S_index = neighbours3D[cellIndex][neighb2S_counter];

                // Neighbour must be located in the same layer that considered cell
                if(neighbour2S_index > -1)
                if(layer_indices[neighbour2S_index] == layer_indices[cellIndex])
                {
                    // State of neighbour at 2nd coordination sphere
                    neighbour2S_state = _frSpec.get(neighbour2S_index);
                    
                    if (neighbour2S_state != undamaged_cell)                     
                        // Calculation of numbers of neighbour cells in each grain
                        neihb2S_cells_in_grains[neighbour2S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[2] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            //  Calculation of numbers of neighbour cells at 3rd coordination
            // sphere in each cluster
            for (int neighb3S_counter = 42; neighb3S_counter < 54; neighb3S_counter++)
            {
                // Single index of neighbour at 3rd coordination sphere
                neighbour3S_index = neighbours3D[cellIndex][neighb3S_counter];

                // Neighbour must be located in the same layer that considered cell
                if((neighbour3S_index>-1))
                if(layer_indices[neighbour3S_index] == layer_indices[cellIndex])
                {
                    // State of neighbour at 3rd coordination sphere
                    neighbour3S_state = _frSpec.get(neighbour3S_index);
                    
                    if (neighbour3S_state != undamaged_cell)
                        // Calculation of numbers of neighbour cells in each grain
                        neihb3S_cells_in_grains[neighbour3S_state]++;
                }
            }
            
            date_1 = new Date();
            time_period[3] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
     /*       
            //TEST
            System.out.print("Cell # "+cellIndex+":");
            for(int grain_counter = 0; grain_counter<embryos_number+1; grain_counter++)
                System.out.print(" "+neihb_cells_in_grains[grain_counter]);
      */      
            // Seeking of maximum from numbers of neighbours1S in each grain
            for(int grain_counter = 1; grain_counter < embryos_number+1; grain_counter++)
            {
                if(neihb1S_cells_in_grains[grain_counter]>maximum)
                {
                    maximum = neihb1S_cells_in_grains[grain_counter];
                    clusterIndex = grain_counter;
                }
                
                if(neihb1S_cells_in_grains[grain_counter]==maximum)
                {
                    // Comparison of numbers of neighbours2S in each of two grains
                    if(neihb2S_cells_in_grains[grain_counter]>neihb2S_cells_in_grains[clusterIndex])
                        clusterIndex = grain_counter;
                    
                    if(neihb2S_cells_in_grains[grain_counter]==neihb2S_cells_in_grains[clusterIndex])
                        // Comparison of numbers of neighbours3S in each of two grains
                        if(neihb3S_cells_in_grains[grain_counter]>neihb3S_cells_in_grains[clusterIndex])
                            clusterIndex = grain_counter;
                }
            }
            
            date_1 = new Date();
            time_period[4] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
       /*     
            //TEST
            System.out.print(". "+maximum+" cells in grain # "+clusterIndex);
        */    
            if(numberOfCellsSwitchedAtCurrentStep == 0)
            // Calculation of number of neighbour cells in considered cluster
            for (int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                // Single index of neighbour at 1st coordination sphere
                neighbour1S_index = neighbours3D[cellIndex][neighb_counter];

                // Neighbour must be located in the same layer that considered cell
                if((neighbour1S_index>-1))
                if(layer_indices[neighbour1S_index] == layer_indices[cellIndex])
                {   
                    // State of neighbour at 1st coordination sphere
                    neighbour1S_state = _frSpec.get(neighbour1S_index);
                    
                    if(neighbour1S_state != undamaged_cell)
                    if(neighbour1S_state == clusterIndex)
                    {                        
                        // Calculation of number of neighbour cells in considered cluster
                        damageNumber1++;
                        
                        if(damageNumber1 == 1)
                        {
                            // Obtaining of single index of neighbour cell in considered cluster
                            damagedNeighb1SIndices[damageNumber1-1] = neighbour1S_index;
                        
                            // Calculation of vector from considered cell to neighbour cells in considered cluster
                            neighb_vector = resid_vectors[neighb_counter];
                            
                            if(calc_anis_cosinus)
                            {
                              //   Calculation of cosinus of angle formed by vector from considered cell 
                              // to neighbour cell in considered cluster and vector of inner anisotropy
                              cosinus_inner = cosinus(neighb_vector, inner_vector);
                        
                              //   Calculation of cosinus of angle formed by vector from considered cell 
                              // to neighbour cell in considered cluster and vector of outer anisotropy
                              cosinus_outer = cosinus(neighb_vector, outer_vector);
                            }
                            else
                            {
                                cosinus_inner = 1;
                                cosinus_outer = 1;
                            }
                        }
                    }
                }
            }
            
            date_1 = new Date();
            time_period[5] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
      /*      
            //TEST
            System.out.println(". "+damageNumber1+"-"+maximum+"="+(damageNumber1-maximum));
            if(damageNumber1-maximum != 0)
                System.out.println("ERROR!!!ERROR!!!ERROR!!!ERROR!!!ERROR!!!ERROR!!!");
      */      
        /*
            int neighb_neighb_counter;
            
            // Exclusion of thickness for the case of large number (>2) of neighbour cells
            if(damageNumber1 > 2)
                damageNumber1 = 0;
            else
            // Exclusion of possibility of cluster closing
            if(damageNumber1 > 1)
            {                       
                for(int damage1S_counter = 0; damage1S_counter<damageNumber1-1; damage1S_counter++)
                {     
                    // Index of 1st neighbour
                    neighbour1S_index = damagedNeighb1SIndices[damage1S_counter];
                
                    // Search of 1st neighbour at 1st coordination sphere of 2nd neighbour
                    for(neighb_neighb_counter = 0; neighb_neighb_counter<12; neighb_neighb_counter++)
                    {
                        // Single index of cell at 1st coordination sphere of 2nd neighbour
                        neighb_neighb1S_index = neighbours3D[damagedNeighb1SIndices[damage1S_counter+1]][neighb_neighb_counter];
                                       
                        // Case of location of 1st neighbour at 1st coordination sphere of 2nd neighbour
                        if(neighb_neighb1S_index == neighbour1S_index)
                        {    
                            // End of current cycle
                            neighb_neighb_counter = 13;
                        }
                    }
                    
                    // Case of location of 1st neighbour out of 1st coordination sphere of 2nd neighbour
                    if(neighb_neighb_counter < 13)
                    {
                        // Exclusion of possibility of cluster closing
                        damageNumber1 = 0;
                        clusterIndex  = undamaged_cell;
                             
                        // Calculation of cosinuses of above-mentioned vectors
                        cosinus_inner = 0;
                        cosinus_outer = 0;
                    }                        
                }
            }   
      */
            
            //TEST
     //     System.out.print("Cell # "+cellIndex+" can go to cluster # " + clusterIndex);
     //       if(damageNumber1 == 2)
     //       System.out.println(". damageNumber1 = "+damageNumber1);
            //END OF TEST
            
            // Calculation of number of damaged neighbours at 2nd and 3rd coordination spheres, which are neighbours
            // at 1st coordination sphere of damaged neighbour at 1st coordination sphere of considered cell
            if (damageNumber1 == 1)
            {                
                damageNumber2 = calcNumberOfDamagedNeighbours2(_frSpec, cellIndex, damagedNeighb1SIndices[damageNumber1-1], clusterIndex);
                damageNumber3 = calcNumberOfDamagedNeighbours3(_frSpec, cellIndex, damagedNeighb1SIndices[damageNumber1-1], clusterIndex);                
            }
            
            date_1 = new Date();
            time_period[6] += date_1.getTime() - date_0.getTime();
            
            date_0 = new Date();
            
            //TEST
     //       if(writeDamagedNeighbours == true)
     //         System.out.println(cellIndex+" "+damageNumber1+" "+damageNumber2+" "+damageNumber3);
            
            probDamage = calcProbDamage(damageNumber1, damageNumber2, damageNumber3);
      /*
            if (damageNumber1 == 2)
                System.out.println(cellIndex+" 1st neighb: "+ damagedNeighb1SIndices[0]
                                            +" 2nd neighb: "+ damagedNeighb1SIndices[1]);
       */   
            // Recalculation of switch probability according to anisotropy
            if(calc_anis_cosinus & coeff_inner_anisotropy > 0 & Math.abs(cosinus_inner) < 1)
                probDamage = probDamage*Math.pow(coeff_inner_anisotropy, Math.abs(cosinus_inner) - 1);// + coeff_outer_anisotropy*cosinus_outer;
            
            if(probDamage<0)
                probDamage = 0;
            
            if(probDamage>1)
                probDamage = 1;
            
            date_1 = new Date();
            time_period[7] += date_1.getTime() - date_0.getTime();
            
        /*
            // TEST
            if(probDamage>0)
            {
                System.out.println("Cell number " + cellIndex + ": ");
                System.out.println("Neighbours: "+ damageNumber1 + ", " + damageNumber2 + ", " + damageNumber3);
                System.out.println("The probability of damage: " + probDamage);
            }
            // END OF TEST
        */
            
            // Stochastic determination of cell damage
        /* 
            rand = new Random();
            long_rand = rand.nextLong();
        */ 
            int _clusterNumber = undamaged_cell;
                    
            if((long_rand > probDamage*min_value)&(long_rand < probDamage*max_value))
            {
                _clusterNumber = clusterIndex;                
            }
            
            return _clusterNumber;
        }
        
        /** The method calculates the number of damaged neighbours at 2nd coordination sphere of the given cell
         * those are neighbours at 1st coordination sphere of the given damaged neighbour at 1st coordination sphere
         * of the given cell.
         * @param _frSpec              the specimen containing the given cell
         * @param _cellIndex           the index of the given cell
         * @param _damagedNeighb1SIndex the single index of the given damaged neighbour at 1st coordination sphere
         *                             of the given cell
         * @param _clusterIndex        the number of cluster of the given damaged neighbour at 1st coordination sphere
         *                             of the given cell
         * @return the number of damaged neighbours at 2nd coordination sphere of the given cell those are neighbours 
         *         at 1st coordination sphere of the given damaged neighbour at 1st coordination sphere of the given cell
         */
        public int calcNumberOfDamagedNeighbours2(GrainsHCPSpecimenR3 _frSpec, int _cellIndex, int _damagedNeighb1SIndex, int _clusterIndex)
        {
            // Returnable value
            int damageNumber2 = 0;
            
            // Index of cell at 2nd coordination sphere of considered "central" cell
            int neighbour2SIndex;
            
            // Calculation of the number of damaged neighbours at 2nd coordination sphere
            for (int neighb2_counter = 12; neighb2_counter < 42; neighb2_counter++)
            {                
                neighbour2SIndex = neighbours3D[_cellIndex][neighb2_counter];
                
                if(neighbour2SIndex>-1)
                  if (_frSpec.get(neighbour2SIndex) != undamaged_cell)
                    // Analysis of cells at 1st coordination sphere of cell 
                    //at 2nd coordination sphere of considered "central" cell
                    for (int neighb1_counter = 0; neighb1_counter < 12; neighb1_counter++)
                      if (neighbours3D[neighbour2SIndex][neighb1_counter] == _damagedNeighb1SIndex)
                        damageNumber2++;
            }

            return damageNumber2;
        }

        /** The method calculates the number of damaged neighbours at 3rd coordination sphere of the given cell 
         * those are neighbours at 1st coordination sphere of the given damaged neighbour at 1st coordination sphere
         * of the given cell.
         * @param _frSpec              the specimen containing the given cell
         * @param _cellIndex           the single index of the given cell
         * @param _damagedNeighb1SIndex the single index of the given damaged neighbour at 1st coordination sphere
         *                             of the given cell
         * @param _clusterIndex        the number of cluster of the given damaged neighbour at 1st coordination sphere
         *                             of the given cell
         * @return the number of damaged neighbours at 3rd coordination sphere of the given cell those are neighbours 
         *         at 1st coordination sphere of the given damaged neighbour at 1st coordination sphere of the given cell
         */
        public int calcNumberOfDamagedNeighbours3(GrainsHCPSpecimenR3 _frSpec, int _cellIndex, int _damagedNeighb1SIndex, int _clusterIndex)
        {
            // Returnable value
            int damageNumber3 = 0;
            
            // Index of cell at 3rd coordination sphere of considered "central" cell
            int neighbour3SIndex;
            
            // Calculation of the number of damaged neighbours at 3rd coordination sphere
            for (int neighb2_counter = 42; neighb2_counter < 54; neighb2_counter++)
            {                
                neighbour3SIndex = neighbours3D[_cellIndex][neighb2_counter];
                
                if(neighbour3SIndex>-1)
                  if (_frSpec.get(neighbour3SIndex) != undamaged_cell)
                    // Analysis of cells at 1st coordination sphere of cell 
                    //at 3rd coordination sphere of considered "central" cell
                    for (int neighb1_counter = 0; neighb1_counter < 12; neighb1_counter++)
                      if (neighbours3D[neighbour3SIndex][neighb1_counter] == _damagedNeighb1SIndex)
                        damageNumber3++;
            }
            
            return damageNumber3;
        }

        /** The method calculates probability of cell switch.
         * @param damageNumber1 number of switched cells at 1st coordination sphere
         * @param damageNumber2 number of switched neighbours at 2nd coordination sphere of cell 
         *                      those are neighbours at 1st coordination sphere of considered switched neighbour
         *                      at 1st coordination sphere of cell
         * @param damageNumber3 number of switched neighbours at 3rd coordination sphere of cell 
         *                      those are neighbours at 1st coordination sphere of considered switched neighbour
         *                      at 1st coordination sphere of cell
         * @return probability of cell switch
         */
        public double calcProbDamage(int damageNumber1, int damageNumber2, int damageNumber3)
        {
            // Probability of cell damage
            probDamage = 0;
            
            // Zero vector
     //     VectorR3 zero_vector = new VectorR3(0, 0, 0);
            
            // Vector from considered cell to damaged cell at 1st coordination sphere
     //     VectorR3 damaged_neighb_shift = zero_vector;

            if(damageNumber1 == 1)
            {
                if(damageNumber2 == 0)
                {
                    if(damageNumber3 == 0){probDamage = 1.0;}//LoadProperties.GROWTH_PROBABILITY;}
                    if(damageNumber3 == 1){probDamage = LoadProperties.STRAIGHT_PROBABILITY;}                            
                }     
             // if (damageNumber2 == 1)   {probDamage = prob_turn;}
                if (damageNumber2 == 1)   {probDamage = LoadProperties.TURN_PROBABILITY;}
                if (damageNumber2 == 2)   {probDamage = LoadProperties.BRANCHING2_PROBABILITY;}
                if (damageNumber2 == 3)   {probDamage = LoadProperties.BRANCHING3_PROBABILITY;}  
                
                // Determination of vector from considered cell to damaged cell at 1st coordination sphere
          /*      for(int neighb_counter = 0; neighb_counter< neighb_shifts.length; neighb_counter++)
                if (!neighb_shifts[neighb_counter].equals(zero_vector))
                {
                    damaged_neighb_shift = new VectorR3(neighb_shifts[neighb_counter]);
                }
            */    
                // Calculation of cell switch probability basing on direction of damage growth
                //probDamage = probDamage + addProb*cosinus(outer_vector, damaged_neighb_shift);
            }
            
            if(damageNumber1 > 1)
                probDamage = LoadProperties.THICKENING_PROBABILITY;// prob_thickhess;
       /*
            if(damageNumber1 == 2)
                probDamage = LoadProperties.THICKENING_PROBABILITY/2;
        
            if(damageNumber1 == 3)
                probDamage = LoadProperties.THICKENING_PROBABILITY;
        */
            
            // Probability value must be in segment [0; 1]!
            if(probDamage<0)                probDamage = 0;
            if(probDamage>1)                probDamage = 1;

            return probDamage;
        }
        
        /** The method calculates probability of cell switch without using of if-operators.
         * @param damageNumber1 number of switched cells at 1st coordination sphere
         * @param damageNumber2 number of switched neighbours at 2nd coordination sphere of cell 
         *                      those are neighbours at 1st coordination sphere of considered switched neighbour
         *                      at 1st coordination sphere of cell
         * @param damageNumber3 number of switched neighbours at 3rd coordination sphere of cell 
         *                      those are neighbours at 1st coordination sphere of considered switched neighbour
         *                      at 1st coordination sphere of cell
         * @return probability of cell switch
         */
        public double calcProbDamageForParalCalc(int damageNumber1, int damageNumber2, int damageNumber3)
        {
            double prob_thick    = LoadProperties.THICKENING_PROBABILITY;
            double prob_growth   = LoadProperties.GROWTH_PROBABILITY;
            double prob_straight = LoadProperties.STRAIGHT_PROBABILITY;
            
            // Case of 1 cell at 1st coordination sphere
            int damageNumber1_1 = (1 + (int)Math.signum((1.5 - damageNumber1)*(damageNumber1 - 0.5)))/2;
            
            // Case of more than 1 cell at 1st coordination sphere
            int damageNumber1_few = (1 + (int)Math.signum(damageNumber1 - 1.5))/2;
            
            // Case of no cells at 2nd coordination sphere
            int damageNumber2_0 = (1 + (int)Math.signum(0.5 - damageNumber2))/2;
            
            // Case of 1 cell at 2nd coordination sphere
            int damageNumber2_1 = (1 + (int)Math.signum((1.5 - damageNumber2)*(damageNumber2 - 0.5)))/2;
            
            // Case of 2 cells at 2nd coordination sphere
            int damageNumber2_2 = (1 + (int)Math.signum((2.5 - damageNumber2)*(damageNumber2 - 1.5)))/2;
            
            // Case of 3 cells at 2nd coordination sphere
            int damageNumber2_3 = (1 + (int)Math.signum((3.5 - damageNumber2)*(damageNumber2 - 2.5)))/2;
            
            // Case of no cells at 3rd coordination sphere
            int damageNumber3_0 = (1 + (int)Math.signum(0.5 - damageNumber3))/2;
            
            // Case of 1 cell at 3rd coordination sphere
            int damageNumber3_1 = (1 + (int)Math.signum((1.5 - damageNumber3)*(damageNumber3 - 0.5)))/2;
            
            // Probability of cell damage
            probDamage = damageNumber1_1*(damageNumber2_0*(damageNumber3_0*prob_growth + damageNumber3_1*prob_straight) +
                                          damageNumber3_0*(damageNumber2_1*LoadProperties.TURN_PROBABILITY         +
                                                           damageNumber2_2*LoadProperties.BRANCHING2_PROBABILITY   +
                                                           damageNumber2_3*LoadProperties.BRANCHING3_PROBABILITY )) +
                         damageNumber1_few*prob_thick;
            
            return probDamage;
        }
        
        /** The method calculates three-dimensional index of the cell 
         * with the given one-dimensional index.
         * @param intIndex one-dimensional index
         * @param size_X the specimen size along X-axis
         * @param size_Y the specimen size along Y-axis
         * @param size_Z the specimen size along Z-axis
         * @return three-dimensional index
         */
        private static Three calcTripleIndex(int intIndex, int size_X, int size_Y, int size_Z)
        {
            // Returnable 3D index
            Three tripleIndex = new Three();
            
            if ((intIndex > -1) & (intIndex < size_X*size_Y*size_Z))
            {
                tripleIndex.setI((intIndex % (size_X*size_Y)) % size_X);                
                tripleIndex.setJ((intIndex % (size_X*size_Y)) / size_X);
                tripleIndex.setK( intIndex / (size_X*size_Y));
            }
            
            return tripleIndex;
        }
        
        
        /** The method sets array of triple indices of all cells of specimen
         * in case of simple cubic package (SCP) of cellular automata (CA).
         * @param frSpecSCP_size size of specimen with simple cubic package of CA
         * @param cube_number_I number of cubic cells along axis X
         * @param cube_number_J number of cubic cells along axis Y
         * @param cube_number_K number of cubic cells along axis Z
         */
        private void setTripleIndicesSCP(int frSpecSCP_size, int cube_number_I,
                                         int cube_number_J, int cube_number_K)
        {
            cell_indices = new Three[frSpecSCP_size];
            
            for(int cell_counter = 0; cell_counter < frSpecSCP_size; cell_counter++)
              cell_indices[cell_counter] = calcTripleIndex(cell_counter, 
                                   cube_number_I, cube_number_J, cube_number_K);
        }
        
        /** The method returns indices of cells at 1st, 2nd and 3rd coordination spheres of given cell.
         * @param _intIndex index of given cell
         * @see neighbours3D
         */
        private void setNeighbours3D(int _intIndex)
        {         
            // Triple index of cell
            Three _tripleIndex = calcTripleIndex(_intIndex,     cell_number_I,
                                                 cell_number_J, cell_number_K);
            
            // Indices of considered cell
            index1 = _tripleIndex.getI();
            index2 = _tripleIndex.getJ();
            index3 = _tripleIndex.getK();
            
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
                
                if((neighbour_indexI > -1)&(neighbour_indexI < cell_number_I)&
                   (neighbour_indexJ > -1)&(neighbour_indexJ < cell_number_J)&
                   (neighbour_indexK > -1)&(neighbour_indexK < cell_number_K))
                {
                    neighbours3D[_intIndex][neighb_counter] = neighbour_indexI + neighbour_indexJ*cell_number_I +
                                                              neighbour_indexK*cell_number_I*cell_number_J;
                }
                else
                    neighbours3D[_intIndex][neighb_counter] = GrainsHCPCommon.CELL_OUTSIDE_OF_SPECIMEN;//-3;
            }            
        }

        /** The method returns indices of cubic cells
         * at 1st and 2nd coordination spheres of given cubic cell.
         * @param _intIndex index of given cell
         * @param cube_number_I number of cubic cells along axis X
         * @param cube_number_J number of cubic cells along axis Y
         * @param cube_number_K number of cubic cells along axis Z
         * @see neighbours3D
         */
        public void setNeighbours3D_SCP(int _intIndex, int cube_number_I,
                                        int cube_number_J, int cube_number_K)
        {
            // Triple index of cell
            Three _tripleIndex = cell_indices[_intIndex];

            // Indices of considered cell
            index1 = _tripleIndex.getI();
            index2 = _tripleIndex.getJ();
            index3 = _tripleIndex.getK();

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

            // If each triple index of neighbour cell is within boundaries then
            // the single index of the cell is calculated, else
            // this cell is deleted from the array of neighbours (single index = -3).
            for (int neighb_counter = 0; neighb_counter < 26; neighb_counter++)
            {
                neighbour_indexI = neighbours[neighb_counter].getI();
                neighbour_indexJ = neighbours[neighb_counter].getJ();
                neighbour_indexK = neighbours[neighb_counter].getK();

                if((neighbour_indexI > -1)&(neighbour_indexI < cube_number_I)&
                   (neighbour_indexJ > -1)&(neighbour_indexJ < cube_number_J)&
                   (neighbour_indexK > -1)&(neighbour_indexK < cube_number_K))
                {
                    neighbours3D[_intIndex][neighb_counter] = neighbour_indexI + neighbour_indexJ*cube_number_I +
                                                              neighbour_indexK*cube_number_I*cube_number_J;
                }
                else
                    neighbours3D[_intIndex][neighb_counter] = GrainsHCPCommon.CELL_OUTSIDE_OF_SPECIMEN;//-3;
            }
        }
        
        /** The method calculates indices of cells at 1st, 2nd and 3rd coordination spheres
         * of cell with certain indices.
         * @param indexI 1st index of cell
         * @param indexJ 2nd index of cell
         * @param indexK 3rd index of cell
         */
        public void calcNeighbIndices(int indexI, int indexJ, int indexK)
        {
            int cell_index = indexI + 
                             indexJ*cell_number_I +
                             indexK*cell_number_I*cell_number_J;
            
            System.out.println("cell_number_I = "+cell_number_I);
            System.out.println("cell_number_J = "+cell_number_J);
            System.out.println("cell_number_K = "+cell_number_K);
            
            VectorR3 cell_coord  = calcCoordinates(indexI, indexJ, indexK);
            
            Three neighb_indices;
            
            int neighb_indexI;
            int neighb_indexJ;
            int neighb_indexK;
            
            int neighb_index;
            
            VectorR3 neighb_coord;
            VectorR3 resid_vector;
            
            ArrayList all_neighbours = new ArrayList(0);
            
            System.out.println("1st coord. sphere:");
            
            for(int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                all_neighbours.add(neighbours3D[neighb_counter]);
                
                // Triple index of cell at 1st coordination sphere
                neighb_indices = calcTripleIndex(neighbours3D[cell_index][neighb_counter], 
                                                 cell_number_I, cell_number_J, cell_number_K);
                                
                neighb_indexI = neighb_indices.getI();
                neighb_indexJ = neighb_indices.getJ();
                neighb_indexK = neighb_indices.getK();
                                
                System.out.println(neighb_indexI+" "+neighb_indexJ+" "+neighb_indexK);
            }
            
            System.out.println();
            
            for(int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
            {
                // Setting of indices of cells at 1st coordination sphere of current neighbour
                setNeighbours3D(((Integer)all_neighbours.get(neighb_counter)).intValue());
                
                //  Addition of indices of cells at 1st coordination sphere of current neighbour
                // to list of all cell neighbours
                for(int neighb_neighb_counter = 0; neighb_neighb_counter < 12; neighb_neighb_counter++)
                if(!all_neighbours.contains(neighbours3D[neighb_neighb_counter]))
                    all_neighbours.add(neighbours3D[neighb_neighb_counter]);
            }
            
            System.out.println("Number of cells at 1-3 spheres: "+all_neighbours.size());
            
            // Cycle over all neighbour cells
            for(int cell_counter = 0; cell_counter < all_neighbours.size(); cell_counter++)
            {
                neighb_index = ((Integer)all_neighbours.get(cell_counter)).intValue();
                
                // Triple index of neighbour cell
                neighb_indices = calcTripleIndex(neighb_index, cell_number_I, cell_number_J, cell_number_K);
                                
                neighb_indexI = neighb_indices.getI();
                neighb_indexJ = neighb_indices.getJ();
                neighb_indexK = neighb_indices.getK();
                
                neighb_coord  = calcCoordinates(neighb_indexI, neighb_indexJ, neighb_indexK);
                
                resid_vector = residial(neighb_coord, cell_coord);
                
                if(cell_counter<10)
                    System.out.print(" ");
                System.out.print  (cell_counter+" ");
                
                if(neighb_indexI-indexI >= 0)
                    System.out.print(" ");
                System.out.print  ((neighb_indexI-indexI)+" ");
                
                if(neighb_indexJ-indexJ >= 0)
                    System.out.print(" ");
                System.out.print  ((neighb_indexJ-indexJ)+" ");
                
                if(neighb_indexK-indexK >= 0)
                    System.out.print(" ");
                System.out.print  ((neighb_indexK-indexK)+"  ");
                
                System.out.println(Math.round(resid_vector.getLength()*resid_vector.getLength()));
                
           //   System.out.println(neighb_coord.getX()+" "+neighb_coord.getY()+" "+neighb_coord.getZ());
            }
        }

        /** The method builds specimen in simple cubic package.
         */
        private void showSpecimenInSCP()
        {
            System.out.println();
            System.out.println("Method showSpecimenInSCP() is started.");
            
            // Number of cubic cells along axes
            int cube_number_I = (int)(Math.floor(radius_HCP/radius_SCP*(0.5 + Math.sqrt(3.0/4.0)*(cell_number_I-1))) + 1);
            int cube_number_J = (int)(Math.floor(radius_HCP/radius_SCP*(cell_number_J-0.25)) + 1);
            int cube_number_K = (int)(Math.floor(radius_HCP/radius_SCP*(0.5 + Math.sqrt(2.0/3.0)*(cell_number_K-1))) + 1);
                 
            System.out.println("Numbers of HCP-elements: "+cell_number_I+" "+cell_number_J+" "+cell_number_K);
            System.out.println("Numbers of SCP-elements: "+cube_number_I+" "+cube_number_J+" "+cube_number_K);

            // Total number of cubic cells
            int cube_number = cube_number_I*cube_number_J*cube_number_K;
            
            // Specimen consisting of cubic cells
            GrainsHCPSpecimenR3 frSpecSCP = new GrainsHCPSpecimenR3(cube_number_I, cube_number_J, cube_number_K);
            
            // Size of specimen consisting of HCP-cells
            frSpecSize = frSpec.size();
            
            // State of HCP-cell
            int cell_state;

            // Number of cell states
            int state_number = 0;
            
            // Coordinates of HCP-cell
            VectorR3 cell_coords;
            
            // Indices of cubic cell
            int cube_index_I, cube_index_J, cube_index_K;
            
            // Single index of cubic cell
            int cube_index;
            
            // List of possible states of cubic cell
            ArrayList poss_states;
            
            // Array of lists of possible states of cubic cells
            ArrayList[] poss_states_array = new ArrayList[cube_number];
            
            for(int list_counter = 0; list_counter<cube_number; list_counter++)
            {
                poss_states_array[list_counter] = new ArrayList(0);
            }
            
            // Maximal values of cubic cell indices
            int max_cube_index_I = -1;
            int max_cube_index_J = -1;
            int max_cube_index_K = -1;

            // Counter of cubic cells
            int cube_counter = 0;
            
            // Cycle over net of HCP-cells
            for(int cell_counter=0; cell_counter<frSpecSize; cell_counter++)
            {
                // State of current HCP-cell
                cell_state   = frSpec.get(cell_counter);

                // Determination of number of cell states
                if(cell_state>state_number)
                    state_number = cell_state;
                
                // Indices of current HCP-cell
                indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);
                
                // Coordinates of current HCP-cell
                cell_coords  = calcCoordinates(indices.getI(), indices.getJ(), indices.getK());
             /*
                cube_number_I = (int)(Math.floor(radius_HCP/radius_SCP*(0.5 + Math.sqrt(3.0/4.0)*(cell_number_I-1))) + 1);
                cube_number_J = (int)(Math.floor(radius_HCP/radius_SCP*(cell_number_J-0.25)) + 1);
                cube_number_K = (int)(Math.floor(radius_HCP/radius_SCP*(0.5 + Math.sqrt(2.0/3.0)*(cell_number_I-1))) + 1);
              */
                // Indices of cubic cell containing centre of current HCP-cell
                cube_index_I = (int)Math.floor(0.5*(cell_coords.getX() - 0.5*radius_HCP)/radius_SCP);
                cube_index_J = (int)Math.floor(0.5*(cell_coords.getY() - 0.5*radius_HCP)/radius_SCP);
                cube_index_K = (int)Math.floor(0.5*cell_coords.getZ()/radius_SCP);

                // Determination of maximal values of cubic cell indices
                if(cube_index_I > max_cube_index_I)
                    max_cube_index_I = cube_index_I;

                if(cube_index_J > max_cube_index_J)
                    max_cube_index_J = cube_index_J;

                if(cube_index_K > max_cube_index_K)
                    max_cube_index_K = cube_index_K;

                if ((cube_index_I > -1)&(cube_index_I < cube_number_I)&
                    (cube_index_J > -1)&(cube_index_J < cube_number_J)&
                    (cube_index_K > -1)&(cube_index_K < cube_number_K))
                {
                    // Calculation of single index of cubic cell
                    cube_index = cube_index_I + cube_number_I*cube_index_J + 
                                 cube_number_I*cube_number_J*cube_index_K;

                    // Addition of state of current HCP-cell to the list of possible states
                    // of corresponding cubic cell
                    poss_states = poss_states_array[cube_index];//new ArrayList(poss_states_array[cube_index]);//
                    poss_states.add(cell_state);
                    poss_states_array[cube_index] = new ArrayList(poss_states);

                    if(poss_states.size()==1)
                        cube_counter++;
                }
                else
                {
                    System.out.println(" Wrong HCP-indices: "+indices.getI()+" "+indices.getJ()+" "+indices.getK());
                    System.out.println(" Wrong HCP-coord-s: "+cell_coords.getX()+" "+cell_coords.getY()+" "+cell_coords.getZ());
                    System.out.println(" Wrong SCP-indices: "+cube_index_I+" "+cube_index_J+" "+cube_index_K);
                }
            }

            // TEST
            System.out.println();
            System.out.println("cube_number_I = "+cube_number_I);
            System.out.println("cube_number_J = "+cube_number_J);
            System.out.println("cube_number_K = "+cube_number_K);
            // END OF TEST
            
            // TEST
            System.out.println();
            System.out.println("Maximal indices of CA: "+max_cube_index_I+" "+max_cube_index_J+" "+max_cube_index_K);
            System.out.println("Length of array of possible states of cubic cells: "+cube_counter+" <= "+poss_states_array.length);
            System.out.println();
            // END OF TEST

            // Number of possible states of current cubic cell
            int poss_states_number;

            // Counter of possible states of current cubic cell
            int poss_states_counter;

            // Array of numbers of various possible states of current cubic cell
            int[] poss_states_numbers = new int[state_number];

            // Counter of possible states of current cubic cell
            int state_counter;
            
            // Maximal value of numbers of possible states of current cubic cell
            int max_poss_state_number;

            // Number of HCP-cells in certain state located in current cubic cell
            int poss_state_number;

            // List of candidates to real state of current cubic cell
            // (all candidate states have equal probabilities to became a real state)
            ArrayList real_state_candidats;

            // Number of candidates to real state of current cubic cell
            int candidate_number;

            // Index of real state in the list of candidate states
            int real_state_index;

            // Real state of current cubic cell
            int real_state;

            // List of empty cubic cells
            ArrayList empty_cubic_cells = new ArrayList(0);

            // Number of empty cubic cells
            int empty_cubes_number=0;

            // Cycle over net of cubic cells
            for(cube_counter = 0; cube_counter<cube_number; cube_counter++)
            {
                for(state_counter=0; state_counter<state_number; state_counter++)
                    poss_states_numbers[state_counter]=0;

                // List of possible states of current cubic cell
                poss_states = poss_states_array[cube_counter];
             
                // Number of possible states of current cubic cell
                poss_states_number = poss_states.size();

                if(poss_states_number>0)
                {
                  // Calculation of numbers of various possible states of current cubic cell
                  for(poss_states_counter=0; poss_states_counter<poss_states_number; poss_states_counter++)
                      poss_states_numbers[(Integer)poss_states.get(poss_states_counter)-1]++;
                
                  // Maximal value of numbers of possible states of current cubic cell
                  max_poss_state_number = 0;

                  // List of candidates to  real state of current cubic cell
                  real_state_candidats  = new ArrayList(0);

                  // Cycle over all cell states
                  for(state_counter=0; state_counter<state_number; state_counter++)
                  {
                    // Number of HCP-cells in certain state located in current cubic cell
                    poss_state_number = poss_states_numbers[state_counter];

                    // Addition of state index to list of candidates to real state of current cubic cell
                    if((poss_state_number==max_poss_state_number)&(poss_state_number>0))
                        real_state_candidats.add(state_counter+1);


                    if(poss_state_number>max_poss_state_number)
                    {
                        max_poss_state_number = poss_state_number;

                        // Creation of new list of candidate states
                        real_state_candidats = new ArrayList(0);

                        // Addition of state index to list of candidates to real state of current cubic cell
                        real_state_candidats.add(state_counter+1);
                    }
                  }

                  // Number of candidates to real state of current cubic cell
                  candidate_number = real_state_candidats.size();

                  // Index of real state in the list of candidate states
                  real_state_index = (int)Math.floor(Math.random()*candidate_number);

                  // Extraction of real state of current cubic cell from list of candidate states
                  if(real_state_index<candidate_number)
                      real_state = (Integer)real_state_candidats.get(real_state_index);
                  else
                      real_state = (Integer)real_state_candidats.get(candidate_number-1);
                  
                  // Setting of state of cubic cell to the specimen (net of cubic cells)
                  frSpecSCP.set(cube_counter, real_state);
                  
                  //TEST
                 // System.out.println("Cube number: "+cube_counter+". Number of possible states: "+poss_states.size());
                }
                else
                {
                    frSpecSCP.set(cube_counter, -1);
                    empty_cubes_number++;

                    empty_cubic_cells.add(cube_counter);

                    //TEST
                    System.out.println("Cube number: "+cube_counter+". Number of possible states: "+poss_states.size());
                }
            }

            //TEST
            System.out.println();
            System.out.println("Number of empty cubic cells: "+empty_cubes_number+" = "+empty_cubic_cells.size());
            System.out.println();

            // Size of specimen with simple cubic package
            int frSpecSCP_size = frSpecSCP.size();
            
            // Index of empty cubic cell
            int empty_cube_index;
            
            // Neighbour cubic cells of empty cubic cell
            int[] empty_cube_neighbs = new int[26];

            // Counter of neighbour cubic cells of empty cubic cell
            int neighb_counter = 0;

            // State of neighbour cubic cell of empty cubic cell
            int neighb_state;

            // List of states of neighbour cubic cells of empty cubic cell
            ArrayList neighb_states = new ArrayList(0);

            // Index of current neighbour cubic cell of empty cubic cell
            int neighb_index;

            // Number of neighbour cubic cells of empty cubic cell
            int neighb_number;

            // Number of cycles necessary for filling of all cubic cells
            int cycle_number = 1;

            // Index of neighbour of empty cubic cell
            int empty_cube_neighb_index;

            // Setting of states of empty cubic cells
            for(int cycle_counter = 0; cycle_counter<cycle_number; cycle_counter++)
            {
              for(int empty_cubes_counter = 0; empty_cubes_counter<empty_cubic_cells.size(); empty_cubes_counter++)
              {
                // Clearing of list of states of neighbour cubic cells of current empty cubic cell
                neighb_states.clear();

                // Single index of current empty cubic cell
                empty_cube_index = (Integer)empty_cubic_cells.get(empty_cubes_counter);

                // Setting of neighbours of current empty cubic cell
                setNeighbours3D_SCP(empty_cube_index, cube_number_I, cube_number_J, cube_number_K);

                // Indices of neighbour cubic cells of current empty cubic cell
                empty_cube_neighbs = neighbours3D[empty_cube_index];

                for(neighb_counter=0; neighb_counter<6; neighb_counter++)
                {
                    // Index of neighbour of current empty cubic cell
                    empty_cube_neighb_index = empty_cube_neighbs[neighb_counter];

                    // State of current neighbour of current empty cubic cell
                    if(empty_cube_neighb_index>0)
                    {
                        neighb_state = frSpecSCP.get(empty_cube_neighb_index);

                        // Addition of the state to the list
                        if(neighb_state>0)
                            neighb_states.add(neighb_state);
                    }
                }

                // Number of inner neighbour cubic cells of empty cubic cell
                neighb_number = neighb_states.size();

                if(neighb_number>0)
                {
                    // Stochastic choice of one of inner neighbour cubic cells
                    // at 1st coordination sphere of empty cubic cell
                    neighb_index = (int)Math.floor(Math.random()*neighb_number);

                    if(neighb_index>=neighb_number)
                        neighb_index = neighb_number-1;

                    // Setting of state of current empty cell (filling of cell)
                    frSpecSCP.set(empty_cube_index, (Integer)neighb_states.get(neighb_index));

                    // Removing of index of former empty cell from the list
                    empty_cubic_cells.remove(empty_cubes_counter);
                }

                // for homogeneous specimen
                /*
                {
                    // Setting of state of current empty cell (filling of cell)
                    frSpecSCP.set(empty_cube_index, state_number);

                    // Removing of index of former empty cell from the list
                    empty_cubic_cells.remove(empty_cubes_counter);
                }
                */
              }

              empty_cubes_number = empty_cubic_cells.size();

              if(empty_cubes_number>0)
              {
                  cycle_number++;

                  // TEST
                  System.out.println("Number of empty cubic cells: "+empty_cubes_number);
              }
              else
                  // TEST
                  System.out.println("Number of empty cubic cells: "+empty_cubes_number);
            }

            //  Specimen consisting of "small" cubic cells with linear size 2 times smaller
            // than size of constructed cubic cells
        //    GrainsHCPSpecimenR3 frSpecSCP2 = new GrainsHCPSpecimenR3(
          //                (int)Math.round(cube_number_I*radius_SCP/radius_HCP),
            //              (int)Math.round(cube_number_J*radius_SCP/radius_HCP),
              //            (int)Math.round(cube_number_K*radius_SCP/radius_HCP));
            
            GrainsHCPSpecimenR3 frSpecSCP2 = new GrainsHCPSpecimenR3(cube_number_I, cube_number_J, cube_number_K);

            // Size of specimen consisting of small cubic cells
            int frSpecSCP2_size = frSpecSCP2.size();

            // State of constructed (large) cubic cell
            int cube_state;

            // Triple index of large cubic cell
            Three cube_indices; 
            
            // Triple index of small cubic cell
            Three small_cube_indices;

            // Indices of small cubic cells
            int small_cube_index_I, small_cube_index_J, small_cube_index_K;

            // Creation of specimen consisting of small cubic cells
            for(int small_cube_counter = 0; small_cube_counter<frSpecSCP2_size; small_cube_counter++)
            {
                // Triple index of small cubic cell
            //    small_cube_indices = calcTripleIndex(small_cube_counter, 
              //            (int)Math.round(cube_number_I*radius_SCP/radius_HCP),
                //          (int)Math.round(cube_number_J*radius_SCP/radius_HCP),
                  //        (int)Math.round(cube_number_K*radius_SCP/radius_HCP));
                
                small_cube_indices = calcTripleIndex(small_cube_counter, cube_number_I, cube_number_J, cube_number_K);

                // Indices of small cubic cell
                small_cube_index_I = small_cube_indices.getI();
                small_cube_index_J = small_cube_indices.getJ();
                small_cube_index_K = small_cube_indices.getK();

                // Indices of corresponding large cubic cell
                cube_index_I = small_cube_index_I;//(int)Math.floor(small_cube_index_I*radius_HCP/radius_SCP);
                cube_index_J = small_cube_index_J;//(int)Math.floor(small_cube_index_J*radius_HCP/radius_SCP);
                cube_index_K = small_cube_index_K;//(int)Math.floor(small_cube_index_K*radius_HCP/radius_SCP);

            //    cube_index_I = small_cube_index_I/5;
              //  cube_index_J = small_cube_index_J/5;
                //cube_index_K = small_cube_index_K/5;

                // Single index of large cubic cell
                cube_index = cube_index_I + cube_number_I*cube_index_J +
                               cube_number_I*cube_number_J*cube_index_K;

                // State of large cubic cell
                if((cube_index>-1)&(cube_index<frSpecSCP_size))
                    cube_state = frSpecSCP.get(cube_index);
                else
                {
                  //  System.out.println("Small cube indices: "+small_cube_index_I+" "+small_cube_index_J+" "+small_cube_index_K);
                    System.out.println("Large cube indices: "+cube_index_I+" "+cube_index_J+" "+cube_index_K+"; index: "+cube_index+"; size: "+frSpecSCP_size);
                    cube_state = -1;
                }

                // Setting of state of small cubic cell
                frSpecSCP2.set(small_cube_counter, cube_state);
            }
            
            frSpecSize = frSpecSCP2_size;
            
            // Redetermination of numbers of cubic cells along axes
            cube_number_I = cube_number_I;//(int)Math.round(cube_number_I*radius_SCP/radius_HCP);
            cube_number_J = cube_number_J;//(int)Math.round(cube_number_J*radius_SCP/radius_HCP);
            cube_number_K = cube_number_K;//(int)Math.round(cube_number_K*radius_SCP/radius_HCP);

            try
            {
                bw = new BufferedWriter(new FileWriter(writeFileName));
                bw.write("#  The file contains information about initial conditions:");
                bw.newLine();
                bw.write("# in 1st string - total number of inner cellular automata;");
                bw.newLine();
                bw.write("# in 2nd string - total number of grains, the numbers of grains in surface layer,");
                bw.newLine();
                bw.write("# upper interlayer, lower interlayer, upper substrate, lower substrate;");
                bw.newLine();
                bw.write("# in each further string - grain index of corresponding inner CA");
                bw.newLine();
                bw.write("# and location type of corresponding inner CA in grain and in specimen:");
                bw.newLine();
                bw.write("# 2 - outer boundary CA (it does not exist here),");
                bw.newLine();
                bw.write("# 3 - intragranular CA in the interior of specimen,");
                bw.newLine();
                bw.write("# 4 - intergranular CA in the interior of specimen,");
                bw.newLine();
                bw.write("# 5 - intragranular CA on specimen boundary,");
                bw.newLine();
                bw.write("# 6 - intergranular CA on specimen boundary.");
                bw.newLine();
                bw.write(frSpecSCP2_size+"\n");
                bw.write(total_grain_number+" "+layer_grain_numbers[0]+" "+layer_grain_numbers[1]+" "+
                             layer_grain_numbers[2]+" "+layer_grain_numbers[3]+" "+layer_grain_numbers[4]+"\n");
                
               // bw.write(embryos_number+" "+embryos_number_1+"\n");

                bw1= new BufferedWriter(new FileWriter(writeFileName_2));
                bw1.write("#  The file contains information about initial geometry:");
                bw1.newLine();
                bw1.write("# in 1st string - total number of inner cellular automata,");
                bw1.newLine();
                bw1.write("# specimen sizes along axes X, Y, Z expressed in radiuses of cell");
                bw1.newLine();
                bw1.write("# and number of grains;");
                bw1.newLine();
                bw1.write("# in each further string - grain index of corresponding inner CA, its coordinates");
                bw1.newLine();
                bw1.write("# and location type of corresponding inner CA in grain and in specimen:");
                bw1.newLine();
                bw1.write("# 2 - outer boundary CA,");
                bw1.newLine();
                bw1.write("# 3 - intragranular CA in the interior of specimen,");
                bw1.newLine();                
                bw1.write("# 4 - intergranular CA in the interior of specimen,");
                bw1.newLine();
                bw1.write("# 5 - intragranular CA on specimen boundary,");
                bw1.newLine();
                bw1.write("# 6 - intergranular CA on specimen boundary.");
                bw1.write(frSpecSCP2_size+" "+cube_number_I+" "+cube_number_J+" "+cube_number_K);
          //      bw1.write("Maximal indices of CA: "+max_cube_index_I+" "+max_cube_index_J+" "+max_cube_index_K);

                if(show_grain_bounds != 0)
                    bw1.write(" "+(embryos_number+phase_number-1));

                bw1.newLine();
                
                // State of cubic cell at 1st coordination sphere of current cell
                int neghb1S_state;
                
                // Cubic cell coordinates;
                VectorR3 cube_coords;
                
                // Coordinates of current cubic cell
                double cube_coord_X, cube_coord_Y, cube_coord_Z;
                
                boolean grain_boundary_cell = false;
                boolean inner_boundary_cell = false;
                
                // Indices of neighbour cubic cell at 1st coordination sphere 
                int[] neighb1S_indices;

                // Setting of triple indices for all cubic cells
                setTripleIndicesSCP(frSpecSCP2_size, cube_number_I, cube_number_J, cube_number_K);

                // Single indices of cubic cells at 1st, 2nd and 3rd coordinational spheres
                neighbours3D = new int[frSpecSCP2_size][26];

                //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
                // of each cubic cell of SCP-specimen
                for(int cell_counter = 0; cell_counter<frSpecSCP2_size; cell_counter++)
                    setNeighbours3D_SCP(cell_counter, cube_number_I, cube_number_J, cube_number_K);

                for (int cell_counter = 0; cell_counter < frSpecSCP2_size; cell_counter++)
                {
                    cube_state = frSpecSCP2.get(cell_counter);
                    
                 // Determination of current cell indices
                    indices = cell_indices[cell_counter];
                     
                    index1 = indices.getI();
                    index2 = indices.getJ();
                    index3 = indices.getK();
                    
                 // System.out.println(cell_counter+" "+index1+" "+index2+" "+index3);

                    // Coordinates of current cubic cell
                    cube_coords = calcCoordinatesSCP(index1, index2, index3);
                    
                    cube_coord_X = cube_coords.getX();
                    cube_coord_Y = cube_coords.getY();
                    cube_coord_Z = cube_coords.getZ();

                    grain_boundary_cell = false;
                    inner_boundary_cell = false;

                    if(cube_state != undamaged_cell)
                    {
                        if(show_grain_bounds != 0)
                        {
                            // Single indices of cells at 1st coordination sphere of current "central" cell
                            neighb1S_indices = neighbours3D[cell_counter];
                                    
                            // Checking of states of cells at 1st coordination sphere of current "central" cubic cell
                            for(int neihb1S_counter = 0; neihb1S_counter<6; neihb1S_counter++)
                            {
                                if(neighb1S_indices[neihb1S_counter]>-1)
                                {
                                    // State of neighbour cell
                                    neghb1S_state = frSpecSCP2.get(neighb1S_indices[neihb1S_counter]);
                                    
                                    // If state of neighbour cell differs from state of current "central" cell
                                    //then this "central" cell is boundary cell
                                    if(neghb1S_state != cube_state)
                                        grain_boundary_cell = true;
                                }
                                else
                                    inner_boundary_cell = true;
                            }
                        }
                        
                        try
                        {
                            bw.write(cube_state+"");
                            bw1.write(cube_state+" "+cube_coord_X+" "+cube_coord_Y+" "+cube_coord_Z);
                            
                            if(show_grain_bounds!=0)
                            {
                                if((grain_boundary_cell)&(inner_boundary_cell))
                                {
                                    bw .write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);
                                    bw1.write(" "+Common.INNER_BOUNDARY_INTERGRANULAR_CELL);
                                }
                                
                                if((grain_boundary_cell)&(!inner_boundary_cell))
                                {
                                    bw .write(" "+Common.INTERGRANULAR_CELL);
                                    bw1.write(" "+Common.INTERGRANULAR_CELL);
                                }
                                
                                if((!grain_boundary_cell)&(inner_boundary_cell))
                                {
                                    bw .write(" "+Common.INNER_BOUNDARY_CELL);
                                    bw1.write(" "+Common.INNER_BOUNDARY_CELL);
                                }
                                
                                if((!grain_boundary_cell)&(!inner_boundary_cell))
                                {
                                    bw .write(" "+Common.INTRAGRANULAR_CELL);
                                    bw1.write(" "+Common.INTRAGRANULAR_CELL);
                                }
                            }
                            
                            bw1.newLine();
                            bw1.flush();
                            bw.newLine();
                            bw.flush();
                        }
                        catch(IOException io_exc)
                        {
                            System.out.println("Error: " + io_exc);
                        }
                        
                        // damageCounter++;
                    }
                }
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
            
            // Redetermination of list of initial HCP-cells
         //  frSpec = frSpecSCP2;
        }
        
        /** The method sets array of triple indices of all cells of specimen.
         */
        private void setTripleIndices()
        {
            cell_indices = new Three[frSpecSize];
            
            for(int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
              cell_indices[cell_counter] = calcTripleIndex(cell_counter, 
                                   cell_number_I, cell_number_J, cell_number_K);
        }
        
        /** The method calculates and returns 3D coordinates of cell with certain 
         * triple index in case of hexagonal close packing.
         * @param index1 1st index of HCP-cell
         * @param index2 2nd index of HCP-cell
         * @param index3 3rd index of HCP-cell
         * @return 3D coordinates of HCP-cell
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
                    coord_X = radius_HCP*(1 + index1*Math.sqrt(3));
                    coord_Y = radius_HCP*(1 + index2*2);
                }
                else
                {
                    coord_X = radius_HCP*(1 + index1*Math.sqrt(3));
                    coord_Y = radius_HCP*(2 + index2*2);
                }
            }
            
            if(index3 % 3 == 1)
            {
                if(index1 % 2 == 0)
                {
                    coord_X = radius_HCP*(1 + 1/Math.sqrt(3) + index1*Math.sqrt(3));
                    coord_Y = radius_HCP*(2 + index2*2);
                }
                else
                {
                    coord_X = radius_HCP*(1 + 1/Math.sqrt(3)+ index1*Math.sqrt(3));
                    coord_Y = radius_HCP*(1 + index2*2);
                }
            }
            
            if(index3 % 3 == 2)
            {
                if(index1 % 2 == 0)
                {
                    coord_X = radius_HCP*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));
                    coord_Y = radius_HCP*(1 + index2*2);
                }
                else
                {
                    coord_X = radius_HCP*(1 + 2/Math.sqrt(3) + index1*Math.sqrt(3));
                    coord_Y = radius_HCP*(2 + index2*2);
                }
            }
            
            coord_Z = radius_HCP*(1 + 2*index3*Math.sqrt(2.0/3.0));
            
            return new VectorR3(coord_X, coord_Y, coord_Z);
        }
        
        /** The method calculates and returns single index of cell containing the point with certain coordinates.
         * @param coord_X coordinate X of the point
         * @param coord_Y coordinate Y of the point
         * @param coord_Z coordinate Z of the point
         * @return triple index of cell containing the point with certain coordinates
         */
        private Three calcTripleIndex(double coord_X, double coord_Y, double coord_Z)
        {
            // Triple index of cell
            int cell_index_I = -1, cell_index_J = -1, cell_index_K = -1;
            
            // 3rd index of cell is calculated
            cell_index_K = (int)Math.round((coord_Z/radius_HCP - 1.0)*Math.sqrt(3.0/8.0));
            
            if(cell_index_K % 3 == 0)
            {
              // 1st index of cell is calculated
              cell_index_I = (int)Math.round((coord_X/radius_HCP - 1.0)/Math.sqrt(3.0));
              
              // 2nd index of cell is calculated
              if(cell_index_I % 2 == 0)
                cell_index_J = (int)Math.round((coord_Y/radius_HCP - 1.0)/2.0);
              else
                cell_index_J = (int)Math.round((coord_Y/radius_HCP - 2.0)/2.0);
            }
            
            if(cell_index_K % 3 == 1)
            {
              // 1st index of cell is calculated
              cell_index_I = (int)Math.round((coord_X/radius_HCP - 1.0 - 1.0/Math.sqrt(3.0))/Math.sqrt(3.0));
              
              // 2nd index of cell is calculated
              if(cell_index_I % 2 == 0)
                cell_index_J = (int)Math.round((coord_Y/radius_HCP - 2.0)/2.0);
              else
                cell_index_J = (int)Math.round((coord_Y/radius_HCP - 1.0)/2.0);
            }
                       
            if(cell_index_K % 3 == 2)
            {
              // 1st index of cell is calculated
              cell_index_I = (int)Math.round((coord_X/radius_HCP - 1.0 - 2.0/Math.sqrt(3.0))/Math.sqrt(3.0));
              
              // 2nd index of cell is calculated
              if(cell_index_I % 2 == 0)
                cell_index_J = (int)Math.round((coord_Y/radius_HCP - 1.0)/2.0);
              else
                cell_index_J = (int)Math.round((coord_Y/radius_HCP - 2.0)/2.0);
            }
              
            return new Three(cell_index_I, cell_index_J, cell_index_K);
        }

        /** The method calculates and returns 3D coordinates of cell with certain
         * triple index in case of simple cubic package (SCP) of cells.
         * @param index1 1st index of SCP-cell
         * @param index2 2nd index of SCP-cell
         * @param index3 3rd index of SCP-cell
         * @return 3D coordinates of SCP-cell
         */
        private VectorR3 calcCoordinatesSCP(int index1, int index2, int index3)
        {
            // Coordinates of cell
            double coord_X=0, coord_Y=0, coord_Z=0;
            
            // Calculation of coordinates of cell
            coord_X = radius_HCP*(1 + 2*index1);
            coord_Y = radius_HCP*(1 + 2*index2);
            coord_Z = radius_HCP*(1 + 2*index3);

            return new VectorR3(coord_X, coord_Y, coord_Z);
        }

       /** The method returns cosinus of angle between two vectors.
        * @param vect_1 1st vector
        * @param vect_2 2nd vector
        * @return cosinus of angle between two vectors
        */
        public double cosinus(VectorR3 vect_1, VectorR3 vect_2)
        {            
            return scalarProduct(vect_1, vect_2)/
            Math.sqrt(scalarProduct(vect_1, vect_1)*scalarProduct(vect_2, vect_2));
        }
        
        /** The method returns scalar product of two vectors.
         * @param vect_1 1st vector
         * @param vect_2 2nd vector
         * @return scalar product of two vectors
         */
        public double scalarProduct(VectorR3 vect_1, VectorR3 vect_2)
        {            
            return vect_1.getX()*vect_2.getX() + 
                   vect_1.getY()*vect_2.getY() + 
                   vect_1.getZ()*vect_2.getZ(); 
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
        
        /** The method calculates coordinates of vectors from central cell 
         * to neighbours at 1st coordination sphere.
         */
        private void setNeighb3D1SVectors()
        { 
            // Coordinates of centre of cell with indices (1, 1, 1)
            VectorR3 coordinates = calcCoordinates(1, 1, 1);
            
            // Coordinates of centres of cells at 1st coordination sphere of cell (1, 1, 1)
            VectorR3[] neighb_vectors = new VectorR3[12];
               
            // Vectors from cell with odd index K to neighbours at 1st coordination sphere
            resid_vectors = new VectorR3[12];
            
            // Calculation of coordinates of neighbours at 1st coordination sphere            
            neighb_vectors[ 0] = calcCoordinates( 1, 0, 0);
            neighb_vectors[ 1] = calcCoordinates( 1, 1, 0);
            neighb_vectors[ 2] = calcCoordinates( 2, 1, 0);
            neighb_vectors[ 3] = calcCoordinates( 0, 0, 1);
            neighb_vectors[ 4] = calcCoordinates( 0, 1, 1);
            neighb_vectors[ 5] = calcCoordinates( 1, 0, 1);
            neighb_vectors[ 6] = calcCoordinates( 1, 2, 1);
            neighb_vectors[ 7] = calcCoordinates( 2, 0, 1);
            neighb_vectors[ 8] = calcCoordinates( 2, 1, 1);
            neighb_vectors[ 9] = calcCoordinates( 0, 1, 2);
            neighb_vectors[10] = calcCoordinates( 1, 0, 2);
            neighb_vectors[11] = calcCoordinates( 1, 1, 2);
            
            // Calculation of vectors from central cell to neighbours at 1st coordination sphere.
            for(int vect_counter = 0; vect_counter<neighb_vectors.length; vect_counter++)
            {
                resid_vectors[vect_counter] = residial(neighb_vectors[vect_counter], coordinates);
            }
        }

        /** The method creates file with information about distribution of cells on 3 layers:
         * 1. Surface layer (1st phase);
         * 2. Upper intermediate layer (triangle prisms of 2nd phase - with lateral face on surface layer
         *    and opposite edge on substrate - and 3rd phase out of the prisms);
         * 3. Lower intermediate layer (4th phase);
         * 4. Substrate (5th phase).
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of upper intermediate layer
         */
        private void createInitCondTriangleGofrFile(double layer1_thickness, double layer2_thickness,
                                                   double layer2_elem_size, double layer3_thickness)
        {
            // Current cell coordinates
            VectorR3 coords;

            double coord_X;
            double coord_Z;
            double floor_X;
            
            // Single index of current cell
            int index;

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates
                coords = calcCoordinates(i_index, j_index, k_index);

                coord_X = coords.getX();
                coord_Z = coords.getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);

                floor_X = layer2_elem_size*Math.floor(coord_X/layer2_elem_size);

                if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    frSpec.set(index, embryos_number+1);

                if((coord_Z >= layer1_thickness) & 
                   (coord_Z <  layer1_thickness+layer2_thickness))
                {
                    if(((coord_X - floor_X <=layer2_elem_size/2)&
                        (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X)))|
                       ((coord_X - floor_X > layer2_elem_size/2)&
                        (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X))))
                        frSpec.set(index, embryos_number+2);
                    else
                        frSpec.set(index, embryos_number+3);
                }

                if((coord_Z >= layer1_thickness+layer2_thickness) &
                   (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    frSpec.set(index, embryos_number+4);

         //       if(coord_Z >= layer1_thickness+layer2_thickness+layer3_thickness)
           //         frSpec.set(index, embryos_number);
            }
        }

        /** The method creates file with information about distribution of cells on 3 layers:
         * 1. Surface layer (1st phase);
         * 2. Upper intermediate layer (rectangles of 2nd phase - with one lateral face on surface layer
         *    and opposite lateral face on substrate - and 3rd phase out of the rectangles);
         * 3. Lower intermediate layer (4th phase);
         * 4. Substrate (5th phase).
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of upper intermediate layer
         */
        private void createInitCondSquareGofrFile(double layer1_thickness, double layer2_thickness,
                                                 double layer2_elem_size, double layer3_thickness)
        {
            // Current cell coordinates
            VectorR3 coords;

            double coord_X;
            double coord_Z;
            
            // Single index of current cell
            int index;

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates
                coords = calcCoordinates(i_index, j_index, k_index);

                coord_X = coords.getX();
                coord_Z = coords.getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);

                if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    frSpec.set(index, embryos_number+1);

                if((coord_Z>=layer1_thickness) &
                   (coord_Z< layer1_thickness+layer2_thickness))
                {
                    if(Math.floor(coord_X/layer2_elem_size)%2==0)
                        frSpec.set(index, embryos_number+2);
                    else
                        frSpec.set(index, embryos_number+3);
                }

                if((coord_Z>=layer1_thickness+layer2_thickness) &
                   (coord_Z< layer1_thickness+layer2_thickness+layer3_thickness))
                    frSpec.set(index, embryos_number+4);

              //  if(coord_Z >= layer1_thickness+layer2_thickness+layer3_thickness)
               //     frSpec.set(index, embryos_number);
            }
        }

         /** The method creates file with information about distribution of cells on 3 layers:
         * 1. Surface layer (1st phase);
         * 2. Upper intermediate layer (chessboard-like distribution of cubes with 2nd and 3rd phases -
         *                        with one facet on surface layer and opposite facet on substrate);
         * 3. Lower intermediate layer (4th phase);
         * 4. Substrate (5th phase).
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of upper intermediate layer
         */
        private void createInitCondChessCubesFile(double layer1_thickness, double layer2_thickness,
                                                 double layer2_elem_size, double layer3_thickness)
        {
            // Current cell coordinates
            VectorR3 coords;

            double coord_X;
            double coord_Y;
            double coord_Z;
            
            // Single index of current cell
            int index;
            
            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates
                coords = calcCoordinates(i_index, j_index, k_index);

                coord_X = coords.getX();
                coord_Y = coords.getY();
                coord_Z = coords.getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);

                if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    frSpec.set(index, embryos_number+1);

                if((coord_Z>=layer1_thickness) &
                   (coord_Z< layer1_thickness+layer2_thickness))
                {
                    if((Math.floor(coord_X/layer2_elem_size)+Math.floor(coord_Y/layer2_elem_size))%2==0)
                        frSpec.set(index, embryos_number+2);
                    else
                        frSpec.set(index, embryos_number+3);
                }

                if((coord_Z>=layer1_thickness+layer2_thickness) &
                   (coord_Z< layer1_thickness+layer2_thickness+layer3_thickness))
                    frSpec.set(index, embryos_number+4);

          //      if(coord_Z >= layer1_thickness+layer2_thickness+layer3_thickness)
           //         frSpec.set(index, embryos_number);
            }
        }

        /** The method creates file with information about distribution of cells on 3 layers:
         * 1. Surface layer (1st phase);
         * 2. Intermediate layer (2nd phase);
         * 3. Substrate (3rd phase).         *
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of intermediate layer
         * @param layer3_thickness thickness of 1st substrate layer
         */
        private void createInitCondSimpleFile(double layer1_thickness, double layer2_thickness,
                                              double layer3_thickness)
        {
            // Thickness of upper surface layer where embryos cannot be generated
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;

            System.out.println("Total thickness of layers: "+layer_thickness);

            // Current cell coordinate Z
            double coord_Z;
            
            // Single index of current cell
            int index;
            
            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates                
                coord_Z = calcCoordinates(i_index, j_index, k_index).getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);

                if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    frSpec.set(index, embryos_number+1);

                if((coord_Z >= layer1_thickness) &
                   (coord_Z < layer1_thickness+layer2_thickness/2))
                    frSpec.set(index, embryos_number+2);

                if((coord_Z >= layer1_thickness+layer2_thickness/2) &
                   (coord_Z < layer1_thickness+layer2_thickness))
                    frSpec.set(index, embryos_number+3);

                if((coord_Z >= layer1_thickness+layer2_thickness) &
                   (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    frSpec.set(index, embryos_number+4);

          //      if(coord_Z >= layer1_thickness+layer2_thickness+layer3_thickness)
            //        frSpec.set(index, embryos_number+5);
            }
        }

        /** The method creates file with information about distribution of cells on 3 layers:
         * 1. Surface layer (1st phase);
         * 2. Intermediate layer (2nd phase);
         * 3. Substrate (3rd phase).
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of intermediate layer
         * @param layer3_thickness thickness of 1st substrate layer
         * @param layer2_type type of intermediate layer
         * @param layer2_elem_size size of element in intermediate layer
         * @return array of numbers of grains in layers
         */
        private int[] createInitCondSimpleFileGrainsInLayers(double layer1_thickness, double layer2_thickness,
                                                             double layer3_thickness, byte layer2_type, double layer2_elem_size)
        {
            // Thickness of upper surface layer where embryos cannot be generated
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;

            System.out.println("Total thickness of layers: "+layer_thickness);

            VectorR3 cell_coord;

            // Cell coordinates
            double coord_X, coord_Y, coord_Z;

            // Single index of current cell
            int index;

            int layer1_cell_counter = 0;
            int layer2_cell_counter = 0;
            int layer3_cell_counter = 0;
            int layer4_cell_counter = 0;
            int layer5_cell_counter = 0;
       //     int specimen_cell_counter = 0;

            // Lists of grain indices in each layer
            ArrayList layer1_grain_indices = new ArrayList(0);
            ArrayList layer2_grain_indices = new ArrayList(0);
            ArrayList layer3_grain_indices = new ArrayList(0);
            ArrayList layer4_grain_indices = new ArrayList(0);
            ArrayList layer5_grain_indices = new ArrayList(0);

            // Numbers of grains in each layer
            int layer1_grain_number = 0;
            int layer2_grain_number = 0;
            int layer3_grain_number = 0;
            int layer4_grain_number = 0;
            int layer5_grain_number = 0;

            // Index of current grain
            int grain_index;

            double floor_X;
            double floor_Y;

            String inter_layer_type = new String();

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates
                cell_coord = calcCoordinates(i_index, j_index, k_index);

                coord_X = cell_coord.getX();
                coord_Y = cell_coord.getY();
                coord_Z = cell_coord.getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);

           //     specimen_cell_counter++;

                if(layer2_type == Common.SIMPLE_LAYER)
                {
                    inter_layer_type = "SIMPLE_LAYER";

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness) &
                       (coord_Z <  layer1_thickness+layer2_thickness/2))
                    {
                        grain_index = (Integer)frSpec3.get(index);
                        frSpec.set(index, grain_index);
                        layer2_cell_counter++;

                        if(!layer2_grain_indices.contains(grain_index))
                        {
                            layer2_grain_indices.add(grain_index);
                            layer2_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness/2) &
                       (coord_Z <  layer1_thickness+layer2_thickness))
                    {
                        grain_index = (Integer)frSpec4.get(index);
                        frSpec.set(index, grain_index);
                        layer3_cell_counter++;

                        if(!layer3_grain_indices.contains(grain_index))
                        {
                            layer3_grain_indices.add(grain_index);
                            layer3_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.TRIANGLE_GOFR)
                {
                    inter_layer_type = "TRIANGLE_GOFR";

                    // Single index of current cell
                    index = calcSingleIndex(i_index, j_index, k_index);

                    floor_X = layer2_elem_size*Math.floor(coord_X/layer2_elem_size);

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness) &
                       (coord_Z <  layer1_thickness+layer2_thickness))
                    {
                        if(((coord_X - floor_X <=layer2_elem_size/2)&
                            (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X)))|
                            ((coord_X - floor_X > layer2_elem_size/2)&
                            (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X))))
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.SQUARE_GOFR)
                {
                    inter_layer_type = "SQUARE_GOFR";

                    // Single index of current cell
                    index = calcSingleIndex(i_index, j_index, k_index);

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z>=layer1_thickness) &
                       (coord_Z< layer1_thickness+layer2_thickness))
                    {
                        if(Math.floor(coord_X/layer2_elem_size)%2==0)
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.PYRAMIDES)
                {
                    inter_layer_type = "PYRAMIDES";

                    floor_X = layer2_elem_size*Math.floor(coord_X/layer2_elem_size);
                    floor_Y = layer2_elem_size*Math.floor(coord_Y/layer2_elem_size);

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z>=layer1_thickness)&
                       (coord_Z< layer1_thickness+layer2_thickness))
                    {
                        if((((coord_X - floor_X <=layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X)))|
                            ((coord_X - floor_X > layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X))))&
                           (((coord_Y - floor_Y <=layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_Y - floor_Y)))|
                            ((coord_Y - floor_Y > layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_Y - floor_Y)))))
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.CHESS_CUBES)
                {
                    inter_layer_type = "CHESS_CUBES";

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z>=layer1_thickness) &
                       (coord_Z< layer1_thickness+layer2_thickness))
                    {
                        if((Math.floor(coord_X/layer2_elem_size)+Math.floor(coord_Y/layer2_elem_size))%2==0)
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(coord_Z >= layer1_thickness+layer2_thickness+layer3_thickness)
                {
                    grain_index = (Integer)frSpec.get(index);
                    layer5_cell_counter++;

                    if(!layer5_grain_indices.contains(grain_index))
                    {
                        layer5_grain_indices.add(grain_index);
                        layer5_grain_number++;
                    }
                }
            }

            int cell_number = layer1_cell_counter+layer2_cell_counter+layer3_cell_counter+
                              layer4_cell_counter+layer5_cell_counter;

            // Lists of indices of all grains in each layer (cannot be equal to each other)
            ArrayList layer1_new_grain_indices = new ArrayList(0);
            ArrayList layer2_new_grain_indices = new ArrayList(0);
            ArrayList layer3_new_grain_indices = new ArrayList(0);
            ArrayList layer4_new_grain_indices = new ArrayList(0);
            ArrayList layer5_new_grain_indices = new ArrayList(0);

            // Creation of 1st of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer1_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer1_grain_indices.get(grain_index_counter));//.intValue();

                if(!layer1_new_grain_indices.contains(grain_index))
                    layer1_new_grain_indices.add(grain_index);
                else
                    layer1_grain_number--;
            }

            // Creation of 2nd of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer2_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer2_grain_indices.get(grain_index_counter));//.intValue();

                if(!layer2_new_grain_indices.contains(grain_index))
                    layer2_new_grain_indices.add(grain_index);
                else
                    layer2_grain_number--;
            }

            // Creation of 3rd of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer3_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer3_grain_indices.get(grain_index_counter));//.intValue();

                if(!layer3_new_grain_indices.contains(grain_index))
                    layer3_new_grain_indices.add(grain_index);
                else
                    layer3_grain_number--;
            }

            // Creation of 4th of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer4_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer4_grain_indices.get(grain_index_counter));//.intValue();

                if(!layer4_new_grain_indices.contains(grain_index))
                    layer4_new_grain_indices.add(grain_index);
                else
                    layer4_grain_number--;
            }

            // Creation of 5th of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer5_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer5_grain_indices.get(grain_index_counter));//.intValue();

                if(!layer5_new_grain_indices.contains(grain_index))
                    layer5_new_grain_indices.add(grain_index);
                else
                    layer5_grain_number--;
            }

            int grain_number = layer1_grain_number+layer2_grain_number+layer3_grain_number+
                               layer4_grain_number+layer5_grain_number;

            // List of indices of all grains in specimen
         //   ArrayList sorted_grain_indices = new ArrayList(0);

            // List of indices of grains in all specimen layers
            ArrayList layer_grain_indices = new ArrayList(0);

            // Creation of this list
            for(int cell_counter = 0; cell_counter<layer1_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer1_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer2_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer2_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer3_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer3_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer4_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer4_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer5_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer5_new_grain_indices.get(cell_counter));

            // List of indices of all grains in specimen (cannot be equal to each other)
            ArrayList grain_indices = new ArrayList(0);

            // Addition of indices of all grains to the list
            for(int grain_counter = 0; grain_counter<layer_grain_indices.size(); grain_counter++)
            {
                grain_index = (Integer)layer_grain_indices.get(grain_counter);

                if(!grain_indices.contains(grain_index))
                    grain_indices.add(grain_index);
                /*
                // Sorting of grain indices by increasing (algorithm contains mistakes!!!)
                if(!sorted_grain_indices.contains(grain_index))
                {
                    if(sorted_grain_indices.isEmpty())
                        sorted_grain_indices.add(grain_index);
                    else
                      for(int grain_index_counter = 0; grain_index_counter<sorted_grain_indices.size(); grain_index_counter++)
                        if(grain_index<((Integer)sorted_grain_indices.get(grain_index_counter)).intValue())
                        {
                            sorted_grain_indices.add(grain_index);

                            for(int larger_index_counter = grain_index_counter; larger_index_counter<sorted_grain_indices.size()-1; larger_index_counter++)
                                sorted_grain_indices.set(larger_index_counter+1, sorted_grain_indices.get(larger_index_counter));

                            sorted_grain_indices.set(grain_index_counter, grain_index);
                            grain_index_counter = sorted_grain_indices.size();
                        }

                    if(!sorted_grain_indices.contains(grain_index))
                        sorted_grain_indices.add(grain_index);
                }
                 */
            }

            // TEST
            boolean error = false;

            for(int cell_counter = 0; cell_counter<frSpec.size(); cell_counter++)
            {
                grain_index = (Integer)frSpec.get(cell_counter);

                if(!grain_indices.contains(grain_index))
                {
                    error = true;
                    cell_counter=frSpec.size();
                }
            }

            System.out.println();
            if(error) System.out.println("ERROR of determination of grain indices!!!");
            else      System.out.println("There is NO ERROR of determination of grain indices!!!");
            System.out.println();
            // END OF TEST

            System.out.println("Total number of grains in specimen: "+grain_indices.size());

            //TEST
            for(int grain_index_counter = 0; grain_index_counter<grain_indices.size(); grain_index_counter++)
                System.out.println("Grain # "+(grain_index_counter+1)+" has index "+grain_indices.get(grain_index_counter));

            System.out.println();

         /*
          * // Printing of indices of grains in upper intermediate layer
            for(int grain_index_counter = 0; grain_index_counter<layer2_new_grain_indices.size(); grain_index_counter++)
            {
                grain_index = (Integer)layer2_new_grain_indices.get(grain_index_counter);

                System.out.print("1st interlayer grain # "+(grain_index_counter+1)+" has index "+grain_index);
                System.out.println();
            }
         */

            for(int cell_counter = 0; cell_counter<frSpec.size(); cell_counter++)
            {
                grain_index = (Integer)frSpec.get(cell_counter);

                if(grain_indices.contains(grain_index))
                    frSpec.set(cell_counter, 1+grain_indices.indexOf(grain_index));
                else
                    System.out.println("ERROR!!! One of grain indices is not taken into account!!!");
            }

            // TEST
            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: "+ inter_layer_type);
            System.out.println("Type of intermediate layer:         "+inter_layer_type);

            System.out.println("Number of cells in surface layer:   "+layer1_cell_counter+" from "+frSpec2.size());
            System.out.println("Number of cells in 1st interlayer:  "+layer2_cell_counter+" from "+frSpec3.size());
            System.out.println("Number of cells in 2nd interlayer:  "+layer3_cell_counter+" from "+frSpec4.size());
            System.out.println("Number of cells in 1st substrate:   "+layer4_cell_counter+" from "+frSpec5.size());
            System.out.println("Number of cells in 2nd substrate:   "+layer5_cell_counter+" from "+frSpec.size());
            System.out.println("Total number of cells in layers:    "+cell_number);

            System.out.println("Number of grains in surface layer:  "+layer1_grain_number+" = "+layer1_new_grain_indices.size());
            System.out.println("Number of grains in 1st interlayer: "+layer2_grain_number+" = "+layer2_new_grain_indices.size());
            System.out.println("Number of grains in 2nd interlayer: "+layer3_grain_number+" = "+layer3_new_grain_indices.size());
            System.out.println("Number of grains in 1st substrate:  "+layer4_grain_number+" = "+layer4_new_grain_indices.size());
            System.out.println("Number of grains in 2nd substrate:  "+layer5_grain_number+" = "+layer5_new_grain_indices.size());
            System.out.println("Total number of grains in layers:   "+grain_number);

            layer_grain_numbers = new int[5];

            layer_grain_numbers[0] = layer1_grain_number;
            layer_grain_numbers[1] = layer2_grain_number;
            layer_grain_numbers[2] = layer3_grain_number;
            layer_grain_numbers[3] = layer4_grain_number;
            layer_grain_numbers[4] = layer5_grain_number;

            return layer_grain_numbers;
        }

        /** The method creates file with information about distribution of cells on 3 layers:
         * 1. Surface layer (1st phase);
         * 2. Intermediate layer (2nd phase);
         * 3. Substrate (3rd phase).
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of intermediate layer
         * @param layer3_thickness thickness of 1st substrate layer
         * @param layer2_type type of intermediate layer
         * @param layer2_elem_size size of element in intermediate layer
         * @return array of numbers of grains in layers
         */
        private int[] createInitCondSimpleFileGrainsInLayers_2(double layer1_thickness, double layer2_thickness,
                                                             double layer3_thickness, byte layer2_type, double layer2_elem_size)
        {
            // Thickness of upper surface layer where embryos cannot be generated
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;

            System.out.println("Total thickness of layers: "+layer_thickness);

            VectorR3 cell_coord;

            // Cell coordinates
            double coord_X, coord_Y, coord_Z;

            // Single index of current cell
            int index;
            
            int layer1_cell_counter = 0;
            int layer2_cell_counter = 0;
            int layer3_cell_counter = 0;
            int layer4_cell_counter = 0;
            int layer5_cell_counter = 0;
       //     int specimen_cell_counter = 0;

            // Lists of grain indices in each layer
            ArrayList layer1_grain_indices = new ArrayList(0);
            ArrayList layer2_grain_indices = new ArrayList(0);
            ArrayList layer3_grain_indices = new ArrayList(0);
            ArrayList layer4_grain_indices = new ArrayList(0);
            ArrayList layer5_grain_indices = new ArrayList(0);

            // Numbers of grains in each layer
            int layer1_grain_number = 0;
            int layer2_grain_number = 0;
            int layer3_grain_number = 0;
            int layer4_grain_number = 0;
            int layer5_grain_number = 0;

            // Index of current grain
            int grain_index;

            double floor_X;
            double floor_Y;

            // Type if intermediate layer
            String inter_layer_type = new String();

            // Layer index
            byte layer_index;

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates
                cell_coord = calcCoordinates(i_index, j_index, k_index);

                coord_X = cell_coord.getX();
                coord_Y = cell_coord.getY();
                coord_Z = cell_coord.getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);
                
                // Grain index
                grain_index = (Integer)frSpec.get(index);

                // Layer index
                layer_index = layer_indices[index];

                // Surface layer
                if(layer_index == 1)
                {
                    layer1_cell_counter++;

                    if(!layer1_grain_indices.contains(grain_index))
                    {
                        layer1_grain_indices.add(grain_index);
                        layer1_grain_number++;
                    }
                }

                if(layer_index == 2)
                {
                    layer2_cell_counter++;

                    if(!layer2_grain_indices.contains(grain_index))
                    {
                        layer2_grain_indices.add(grain_index);
                        layer2_grain_number++;
                    }
                }

                if(layer_index == 3)
                {
                    layer3_cell_counter++;

                    if(!layer3_grain_indices.contains(grain_index))
                    {
                        layer3_grain_indices.add(grain_index);
                        layer3_grain_number++;
                    }
                }

                if(layer_index == 4)
                {
                    layer4_cell_counter++;

                    if(!layer4_grain_indices.contains(grain_index))
                    {
                        layer4_grain_indices.add(grain_index);
                        layer4_grain_number++;
                    }
                }

                if(layer_index == 5)
                {
                    layer5_cell_counter++;

                    if(!layer5_grain_indices.contains(grain_index))
                    {
                        layer5_grain_indices.add(grain_index);
                        layer5_grain_number++;
                    }
                }

                /*
                if(layer2_type == Common.SIMPLE_LAYER)
                {
                    inter_layer_type = "SIMPLE_LAYER";

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec.get(index);
                     //   frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness) &
                       (coord_Z <  layer1_thickness+layer2_thickness/2))
                    {
                        grain_index = (Integer)frSpec.get(index);
                   //     frSpec.set(index, grain_index);
                        layer2_cell_counter++;

                        if(!layer2_grain_indices.contains(grain_index))
                        {
                            layer2_grain_indices.add(grain_index);
                            layer2_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness/2) &
                       (coord_Z <  layer1_thickness+layer2_thickness))
                    {
                        grain_index = (Integer)frSpec.get(index);
                   //     frSpec.set(index, grain_index);
                        layer3_cell_counter++;

                        if(!layer3_grain_indices.contains(grain_index))
                        {
                            layer3_grain_indices.add(grain_index);
                            layer3_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec.get(index);
                //        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.TRIANGLE_GOFR)
                {
                    inter_layer_type = "TRIANGLE_GOFR";

                    // Single index of current cell
                    index = calcSingleIndex(i_index, j_index, k_index);

                    floor_X = layer2_elem_size*Math.floor(coord_X/layer2_elem_size);

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z >= layer1_thickness) &
                       (coord_Z <  layer1_thickness+layer2_thickness))
                    {
                        if(((coord_X - floor_X <=layer2_elem_size/2)&
                            (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X)))|
                            ((coord_X - floor_X > layer2_elem_size/2)&
                            (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X))))
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.SQUARE_GOFR)
                {
                    inter_layer_type = "SQUARE_GOFR";

                    // Single index of current cell
                    index = calcSingleIndex(i_index, j_index, k_index);

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z>=layer1_thickness) &
                       (coord_Z< layer1_thickness+layer2_thickness))
                    {
                        if(Math.floor(coord_X/layer2_elem_size)%2==0)
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }
                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.PYRAMIDES)
                {
                    inter_layer_type = "PYRAMIDES";

                    floor_X = layer2_elem_size*Math.floor(coord_X/layer2_elem_size);
                    floor_Y = layer2_elem_size*Math.floor(coord_Y/layer2_elem_size);

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z>=layer1_thickness)&
                       (coord_Z< layer1_thickness+layer2_thickness))
                    {
                        if((((coord_X - floor_X <=layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X)))|
                            ((coord_X - floor_X > layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X))))&
                           (((coord_Y - floor_Y <=layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_Y - floor_Y)))|
                            ((coord_Y - floor_Y > layer2_elem_size/2)&
                             (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_Y - floor_Y)))))
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(layer2_type == Common.CHESS_CUBES)
                {
                    inter_layer_type = "CHESS_CUBES";

                    if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    {
                        grain_index = (Integer)frSpec2.get(index);
                        frSpec.set(index, grain_index);
                        layer1_cell_counter++;

                        if(!layer1_grain_indices.contains(grain_index))
                        {
                            layer1_grain_indices.add(grain_index);
                            layer1_grain_number++;
                        }
                    }

                    if((coord_Z>=layer1_thickness) &
                       (coord_Z< layer1_thickness+layer2_thickness))
                    {
                        if((Math.floor(coord_X/layer2_elem_size)+Math.floor(coord_Y/layer2_elem_size))%2==0)
                        {
                            grain_index = (Integer)frSpec3.get(index);
                            frSpec.set(index, grain_index);
                            layer2_cell_counter++;

                            if(!layer2_grain_indices.contains(grain_index))
                            {
                                layer2_grain_indices.add(grain_index);
                                layer2_grain_number++;
                            }
                        }
                        else
                        {
                            grain_index = (Integer)frSpec4.get(index);
                            frSpec.set(index, grain_index);
                            layer3_cell_counter++;

                            if(!layer3_grain_indices.contains(grain_index))
                            {
                                layer3_grain_indices.add(grain_index);
                                layer3_grain_number++;
                            }
                        }
                    }

                    if((coord_Z >= layer1_thickness+layer2_thickness) &
                       (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        grain_index = (Integer)frSpec5.get(index);
                        frSpec.set(index, grain_index);
                        layer4_cell_counter++;

                        if(!layer4_grain_indices.contains(grain_index))
                        {
                            layer4_grain_indices.add(grain_index);
                            layer4_grain_number++;
                        }
                    }
                }

                if(coord_Z >= layer1_thickness+layer2_thickness+layer3_thickness)
                {
                    grain_index = (Integer)frSpec.get(index);
                    layer5_cell_counter++;
                    
                    if(!layer5_grain_indices.contains(grain_index))
                    {
                        layer5_grain_indices.add(grain_index);
                        layer5_grain_number++;
                    }
                }
                */
            }

            int cell_number = layer1_cell_counter+layer2_cell_counter+layer3_cell_counter+
                              layer4_cell_counter+layer5_cell_counter;

            // Lists of indices of all grains in each layer (cannot be equal to each other)
            ArrayList layer1_new_grain_indices = new ArrayList(0);
            ArrayList layer2_new_grain_indices = new ArrayList(0);
            ArrayList layer3_new_grain_indices = new ArrayList(0);
            ArrayList layer4_new_grain_indices = new ArrayList(0);
            ArrayList layer5_new_grain_indices = new ArrayList(0);

            // Creation of 1st of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer1_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer1_grain_indices.get(grain_index_counter));//.intValue();

                if((!layer1_new_grain_indices.contains(grain_index))&(grain_index!=0))
                    layer1_new_grain_indices.add(grain_index);
                else
                    layer1_grain_number--;
            }

            // Creation of 2nd of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer2_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer2_grain_indices.get(grain_index_counter));//.intValue();

                if((!layer2_new_grain_indices.contains(grain_index))&(grain_index!=0))
                    layer2_new_grain_indices.add(grain_index);
                else
                    layer2_grain_number--;
            }

            // Creation of 3rd of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer3_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer3_grain_indices.get(grain_index_counter));//.intValue();

                if((!layer3_new_grain_indices.contains(grain_index))&(grain_index!=0))
                    layer3_new_grain_indices.add(grain_index);
                else
                    layer3_grain_number--;
            }

            // Creation of 4th of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer4_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer4_grain_indices.get(grain_index_counter));//.intValue();

                if((!layer4_new_grain_indices.contains(grain_index))&(grain_index!=0))
                    layer4_new_grain_indices.add(grain_index);
                else
                    layer4_grain_number--;
            }

            // Creation of 5th of these lists
            for(int grain_index_counter = 0; grain_index_counter<layer5_grain_indices.size(); grain_index_counter++)
            {
                grain_index = ((Integer)layer5_grain_indices.get(grain_index_counter));//.intValue();

                if((!layer5_new_grain_indices.contains(grain_index))&(grain_index!=0))
                    layer5_new_grain_indices.add(grain_index);
                else
                    layer5_grain_number--;
            }

            int grain_number = layer1_grain_number+layer2_grain_number+layer3_grain_number+
                               layer4_grain_number+layer5_grain_number;

            // List of indices of all grains in specimen
         //   ArrayList sorted_grain_indices = new ArrayList(0);

            // List of indices of grains in all specimen layers
            ArrayList layer_grain_indices = new ArrayList(0);

            // Creation of this list
            for(int cell_counter = 0; cell_counter<layer1_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer1_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer2_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer2_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer3_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer3_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer4_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer4_new_grain_indices.get(cell_counter));

            for(int cell_counter = 0; cell_counter<layer5_new_grain_indices.size(); cell_counter++)
                layer_grain_indices.add(layer5_new_grain_indices.get(cell_counter));

            // List of indices of all grains in specimen (cannot be equal to each other)
            ArrayList grain_indices = new ArrayList(0);

            // Addition of indices of all grains to the list
            for(int grain_counter = 0; grain_counter<layer_grain_indices.size(); grain_counter++)
            {
                grain_index = (Integer)layer_grain_indices.get(grain_counter);
                
                if(!grain_indices.contains(grain_index))
                    grain_indices.add(grain_index);
            }

            // TEST
            boolean error = false;

            for(int cell_counter = 0; cell_counter<frSpec.size(); cell_counter++)
            {
                grain_index = (Integer)frSpec.get(cell_counter);

                if(grain_index > 0)
                if(!grain_indices.contains(grain_index))
                {
                    error = true;
                  //  cell_counter=frSpec.size();
                    System.out.println("ERROR of determination of grain indices!!! Grain with index "+grain_index+" is absent!!!");
                }
            }
            
            System.out.println();
            if(error) System.out.println("ERROR of determination of grain indices!!!");
            else      System.out.println("There is NO ERROR of determination of grain indices!!!");
            System.out.println();
            // END OF TEST

            System.out.println("Total number of grains in specimen: "+grain_indices.size());

            //TEST
            for(int grain_index_counter = 0; grain_index_counter<grain_indices.size(); grain_index_counter++)
                System.out.println("Grain # "+(grain_index_counter+1)+" has index "+grain_indices.get(grain_index_counter));

            System.out.println();

            for(int cell_counter = 0; cell_counter<frSpec.size(); cell_counter++)
            {
                grain_index = (Integer)frSpec.get(cell_counter);

                if(grain_indices.contains(grain_index))
                    frSpec.set(cell_counter, 1+grain_indices.indexOf(grain_index));
            //    else
              //      System.out.println("ERROR!!! One of grain indices is not taken into account!!!");
            }
            
            // TEST
         //   completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: "+ inter_layer_type);
         //   System.out.println("Type of intermediate layer:         "+inter_layer_type);

            System.out.println("Number of cells in surface layer:   "+layer1_cell_counter);//+" from "+frSpec2.size());//
            System.out.println("Number of cells in 1st interlayer:  "+layer2_cell_counter);//+" from "+frSpec3.size());//
            System.out.println("Number of cells in 2nd interlayer:  "+layer3_cell_counter);//+" from "+frSpec4.size());//
            System.out.println("Number of cells in 1st substrate:   "+layer4_cell_counter);//+" from "+frSpec5.size());//
            System.out.println("Number of cells in 2nd substrate:   "+layer5_cell_counter);//+" from "+frSpec.size());//
            System.out.println("Total number of cells in layers:    "+cell_number);

            System.out.println("Number of grains in surface layer:  "+layer1_grain_number+" = "+layer1_new_grain_indices.size());
            System.out.println("Number of grains in 1st interlayer: "+layer2_grain_number+" = "+layer2_new_grain_indices.size());
            System.out.println("Number of grains in 2nd interlayer: "+layer3_grain_number+" = "+layer3_new_grain_indices.size());
            System.out.println("Number of grains in 1st substrate:  "+layer4_grain_number+" = "+layer4_new_grain_indices.size());
            System.out.println("Number of grains in 2nd substrate:  "+layer5_grain_number+" = "+layer5_new_grain_indices.size());
            System.out.println("Total number of grains in layers:   "+grain_number);

            layer_grain_numbers = new int[5];

            layer_grain_numbers[0] = layer1_grain_number;
            layer_grain_numbers[1] = layer2_grain_number;
            layer_grain_numbers[2] = layer3_grain_number;
            layer_grain_numbers[3] = layer4_grain_number;
            layer_grain_numbers[4] = layer5_grain_number;

            return layer_grain_numbers;
        }        
        
        /** The method determines indices of layers where each cell is located.
         * There are 5 layers:
         * 1. Surface layer (1st phase);
         * 2. Upper intermediate layer (2nd phase);
         * 3. Lower intermediate layer (3rd phase);
         * 4. Upper substrate (4th phase);
         * 5. Lower substrate (5th phase).
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of intermediate layers
         * @param layer3_thickness thickness of upper substrate layer
         * @param layer2_type type of boundary between intermediate layers
         * @param layer2_elem_size size of boundary element in intermediate layers
         * @return array of layer indices of all cells
         */
        private void determineLayerIndices(double layer1_thickness, double layer2_thickness,
                                           double layer3_thickness, byte layer2_type, double layer2_elem_size)
        {
            // Total thickness of layers
            double layer_thickness = layer1_thickness+layer2_thickness+layer3_thickness;
            
            System.out.println("--------================-------------=========");
            System.out.println("Total thickness of layers: "+layer_thickness);
            System.out.println("layer1_thickness: "+layer1_thickness);
            System.out.println("layer2_thickness: "+layer2_thickness);
            System.out.println("layer3_thickness: "+layer3_thickness);
            System.out.println("--------================-------------=========");

            VectorR3 cell_coord;

            // Cell coordinates
            double coord_X, coord_Y, coord_Z;
            
            double chosen_coord = 0;
            double first_coord  = 0;
            double second_coord = 0;

            // Single index of current cell
            int index;
            
            int layer1_cell_counter = 0;
            int layer2_cell_counter = 0;
            int layer3_cell_counter = 0;
            int layer4_cell_counter = 0;
            int layer5_cell_counter = 0;

            double floor_1;
            double floor_2;

            String inter_layer_type = new String();
            
            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates
                cell_coord = calcCoordinates(i_index, j_index, k_index);

                coord_X = cell_coord.getX();
                coord_Y = cell_coord.getY();
                coord_Z = cell_coord.getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);

                if(layer2_type == Common.SIMPLE_LAYER)
                {
                  inter_layer_type = "SIMPLE_LAYER";
                  
                  if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
                      chosen_coord = coord_X;
                  
                  if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
                      chosen_coord = coord_Y;
                  
                  if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
                      chosen_coord = coord_Z;
                  
                  if((chosen_coord >= 0) & 
                     (chosen_coord < layer1_thickness))
                  { layer_indices[index] = 1; layer1_cell_counter++;}

                  if((chosen_coord >= layer1_thickness) &
                     (chosen_coord <  layer1_thickness+layer2_thickness/2))
                  { layer_indices[index] = 2; layer2_cell_counter++;}

                  if((chosen_coord >= layer1_thickness+layer2_thickness/2) &
                     (chosen_coord <  layer1_thickness+layer2_thickness))
                  { layer_indices[index] = 3; layer3_cell_counter++;}

                  if((chosen_coord >= layer1_thickness+layer2_thickness) &
                     (chosen_coord < layer1_thickness+layer2_thickness+layer3_thickness))
                  { layer_indices[index] = 4; layer4_cell_counter++;}
                }

                if(layer2_type == Common.TRIANGLE_GOFR)
                {
                    inter_layer_type = "TRIANGLE_GOFR";
                    
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
                    {
                      chosen_coord = coord_X;
                      first_coord  = coord_Y;
                      second_coord = coord_Z;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
                    {
                      chosen_coord = coord_Y;
                      first_coord  = coord_Z;
                      second_coord = coord_X;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
                    {
                      chosen_coord = coord_Z;
                      first_coord  = coord_X;
                      second_coord = coord_Y;
                    }

                    // Single index of current cell
                    index = calcSingleIndex(i_index, j_index, k_index);

                    floor_1 = layer2_elem_size*Math.floor(first_coord/layer2_elem_size);

                    if((chosen_coord >= 0) & (chosen_coord < layer1_thickness))
                    {                        
                        layer1_cell_counter++;
                        layer_indices[index] = 1;
                    }

                    if((chosen_coord >= layer1_thickness) &
                       (chosen_coord <  layer1_thickness+layer2_thickness))
                    {
                        if(((first_coord - floor_1 <=layer2_elem_size/2)&
                            (chosen_coord <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(first_coord - floor_1)))|
                           ((first_coord - floor_1 > layer2_elem_size/2)&
                            (chosen_coord <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(first_coord - floor_1))))
                        {
                            layer2_cell_counter++;
                            layer_indices[index] = 2;
                        }
                        else
                        {                            
                            layer3_cell_counter++;
                            layer_indices[index] = 3;
                        }
                    }

                    if((chosen_coord >= layer1_thickness+layer2_thickness) &
                       (chosen_coord <  layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        layer4_cell_counter++;
                        layer_indices[index] = 4;
                    }
                }

                if(layer2_type == Common.SQUARE_GOFR)
                {
                    inter_layer_type = "SQUARE_GOFR";

                    // Single index of current cell
                    index = calcSingleIndex(i_index, j_index, k_index);
                    
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
                    {
                      chosen_coord = coord_X;
                      first_coord  = coord_Y;
                      second_coord = coord_Z;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
                    {
                      chosen_coord = coord_Y;
                      first_coord  = coord_Z;
                      second_coord = coord_X;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
                    {
                      chosen_coord = coord_Z;
                      first_coord  = coord_X;
                      second_coord = coord_Y;
                    }

                    if((chosen_coord >= 0) & (chosen_coord < layer1_thickness))
                    {                        
                        layer1_cell_counter++;
                        layer_indices[index] = 1;
                    }

                    if((chosen_coord>=layer1_thickness) &
                       (chosen_coord< layer1_thickness+layer2_thickness))
                    {
                        if(Math.floor(first_coord/layer2_elem_size)%2==0)
                        {
                            layer2_cell_counter++;
                            layer_indices[index] = 2;
                        }
                        else
                        {
                            layer3_cell_counter++;
                            layer_indices[index] = 3;
                        }
                    }
                    if((chosen_coord >= layer1_thickness+layer2_thickness) &
                       (chosen_coord < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        layer4_cell_counter++;
                        layer_indices[index] = 4;
                    }
                }

                if(layer2_type == Common.PYRAMIDES)
                {
                    inter_layer_type = "PYRAMIDES";
                    
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
                    {
                      chosen_coord = coord_X;
                      first_coord  = coord_Y;
                      second_coord = coord_Z;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
                    {
                      chosen_coord = coord_Y;
                      first_coord  = coord_Z;
                      second_coord = coord_X;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
                    {
                      chosen_coord = coord_Z;
                      first_coord  = coord_X;
                      second_coord = coord_Y;
                    }

                    floor_1 = layer2_elem_size*Math.floor(first_coord/layer2_elem_size);
                    floor_2 = layer2_elem_size*Math.floor(second_coord/layer2_elem_size);

                    if((chosen_coord >= 0) & (chosen_coord < layer1_thickness))
                    {
                        layer1_cell_counter++;
                        layer_indices[index] = 1;
                    }

                    if((chosen_coord>=layer1_thickness)&
                       (chosen_coord< layer1_thickness+layer2_thickness))
                    {
                        if((((first_coord - floor_1 <=layer2_elem_size/2)&
                             (chosen_coord <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(first_coord - floor_1)))|
                            ((first_coord - floor_1 > layer2_elem_size/2)&
                             (chosen_coord <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(first_coord - floor_1))))&
                           (((second_coord - floor_2 <=layer2_elem_size/2)&
                             (chosen_coord <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(second_coord - floor_2)))|
                            ((second_coord - floor_2 > layer2_elem_size/2)&
                             (chosen_coord <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(second_coord - floor_2)))))
                        {
                            layer2_cell_counter++;
                            layer_indices[index] = 2;
                        }
                        else
                        {
                            layer3_cell_counter++;
                            layer_indices[index] = 3;
                        }
                    }

                    if((chosen_coord >= layer1_thickness+layer2_thickness) &
                       (chosen_coord < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        layer4_cell_counter++;
                        layer_indices[index] = 4;
                    }
                }

                if(layer2_type == Common.CHESS_CUBES)
                {
                    inter_layer_type = "CHESS_CUBES";
                    
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X)
                    {
                      chosen_coord = coord_X;
                      first_coord  = coord_Y;
                      second_coord = coord_Z;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y)
                    {
                      chosen_coord = coord_Y;
                      first_coord  = coord_Z;
                      second_coord = coord_X;
                    }
                  
                    if(layer_direction == UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z)
                    {
                      chosen_coord = coord_Z;
                      first_coord  = coord_X;
                      second_coord = coord_Y;
                    }

                    if((chosen_coord >= 0) & (chosen_coord < layer1_thickness))
                    {
                        layer1_cell_counter++;
                        layer_indices[index] = 1;
                    }

                    if((chosen_coord>=layer1_thickness) &
                       (chosen_coord< layer1_thickness+layer2_thickness))
                    {
                        if((Math.floor(first_coord/layer2_elem_size)+Math.floor(second_coord/layer2_elem_size))%2==0)
                        {
                            layer2_cell_counter++;
                            layer_indices[index] = 2;
                        }
                        else
                        {
                            layer3_cell_counter++;
                            layer_indices[index] = 3;
                        }
                    }

                    if((chosen_coord >= layer1_thickness+layer2_thickness) &
                       (chosen_coord < layer1_thickness+layer2_thickness+layer3_thickness))
                    {
                        layer4_cell_counter++;
                        layer_indices[index] = 4;
                    }
                }

                if(chosen_coord >= layer1_thickness+layer2_thickness+layer3_thickness)
                {
                    layer5_cell_counter++;
                    layer_indices[index] = 5;
                }
            }

            int cell_number = layer1_cell_counter+layer2_cell_counter+layer3_cell_counter+
                              layer4_cell_counter+layer5_cell_counter;

            // TEST
            completed_steps.setText(completed_steps.getText()+"\nType of intermediate layer: "+ inter_layer_type);
            System.out.println("Type of intermediate layer:         "+inter_layer_type);

            System.out.println("Number of cells in surface layer:   "+layer1_cell_counter);//+" from "+frSpec2.size());
            System.out.println("Number of cells in 1st interlayer:  "+layer2_cell_counter);//+" from "+frSpec3.size());
            System.out.println("Number of cells in 2nd interlayer:  "+layer3_cell_counter);//+" from "+frSpec4.size());
            System.out.println("Number of cells in 1st substrate:   "+layer4_cell_counter);//+" from "+frSpec5.size());
            System.out.println("Number of cells in 2nd substrate:   "+layer5_cell_counter);//+" from "+frSpec.size());
            System.out.println("Total number of cells in layers:    "+cell_number+" = "+frSpec.size());
            System.out.println("--------================-------------=========");
        }

        /** The method creates file with information about distribution of cells on 3 layers:
         * 1. Surface layer (1st phase);
         * 2. Upper intermediate layer (pyramides of 2nd phase - with base on surface layer
         *    and vertex on substrate - and 3rd phase out of the pyramides);
         * 3. Lower intermediate layer (4th phase);
         * 4. Substrate (5th phase).
         * @param layer1_thickness thickness of surface layer
         * @param layer2_thickness thickness of upper intermediate layer
         * @param layer3_thickness thickness of lower intermediate layer
         * @param layer2_elem_size size of geometric element of upper intermediate layer
         */
        private void createInitCondPyramidesFile(double layer1_thickness, double layer2_thickness,
                                                double layer2_elem_size, double layer3_thickness)
        {            
            // Current cell coordinates
            VectorR3 coords;
            
            double coord_X;
            double coord_Y;
            double coord_Z;
            double floor_X, floor_Y;
            
            // Single index of current cell
            int index;

            for (int k_index = 0; k_index < cell_number_K; k_index++)
            for (int j_index = 0; j_index < cell_number_J; j_index++)
            for (int i_index = 0; i_index < cell_number_I; i_index++)
            {
                // Current cell coordinates
                coords = calcCoordinates(i_index, j_index, k_index);

                coord_X = coords.getX();
                coord_Y = coords.getY();
                coord_Z = coords.getZ();

                // Single index of current cell
                index = calcSingleIndex(i_index, j_index, k_index);

                floor_X = layer2_elem_size*Math.floor(coord_X/layer2_elem_size);
                floor_Y = layer2_elem_size*Math.floor(coord_Y/layer2_elem_size);

                if((coord_Z >= 0) & (coord_Z < layer1_thickness))
                    frSpec.set(index, embryos_number+1);

                if((coord_Z>=layer1_thickness) &
                   (coord_Z< layer1_thickness+layer2_thickness))
                {
                    if((((coord_X - floor_X <=layer2_elem_size/2)&
                         (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X)))|
                        ((coord_X - floor_X > layer2_elem_size/2)&
                         (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_X - floor_X))))&
                       (((coord_Y - floor_Y <=layer2_elem_size/2)&
                         (coord_Z <=layer1_thickness + (2*layer2_thickness/layer2_elem_size)*(coord_Y - floor_Y)))|
                        ((coord_Y - floor_Y > layer2_elem_size/2)&
                         (coord_Z <=layer1_thickness + 2*layer2_thickness - (2*layer2_thickness/layer2_elem_size)*(coord_Y - floor_Y)))))
                        frSpec.set(index, embryos_number+2);
                    else
                        frSpec.set(index, embryos_number+3);
                }

                if((coord_Z >= layer1_thickness+layer2_thickness) &
                   (coord_Z < layer1_thickness+layer2_thickness+layer3_thickness))
                    frSpec.set(index, embryos_number+4);

          //      if(coord_Z >= layer1_thickness+layer2_thickness+layer3_thickness)
           //         frSpec.set(index, embryos_number);
            }
        }
        
        /** The method calculates single index of cell3D with certain triple index.
         * @param indexI 1st index of cell3D
         * @param indexJ 2nd index of cell3D
         * @param indexK 3rd index of cell3D
         * @return single index of cell3D
         */
        public int calcSingleIndex(int indexI, int indexJ, int indexK)
        {
            // Single index of cell3D
            int single_index = -1;
            
            // Calculation of single index of cell3D
            if ((indexI > -1)&(indexI < cell_number_I)&
                (indexJ > -1)&(indexJ < cell_number_J)&
                (indexK > -1)&(indexK < cell_number_K))
            {
                single_index = indexI + cell_number_I*indexJ + 
                               cell_number_I*cell_number_J*indexK;
            }
            
            return single_index;
        }

        /** The method distributes colours among clusters of cells in optimal way.
         * @param colour_number number of colours of cell clusters
         */
        private static boolean setClusterColoursOpt(int colour_number)
        {
            System.out.println("Determination of grain colours is started...");

            // Index of cluster (dendrite) containing current dendrite cell
            int cluster_index;

            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            int[][] neighbours3D = new int[frSpecSize][54];

            int max_colour_index = 0;
            
            // Single indices of cells at 1st, 2nd and 3rd coordinational spheres
            neighbours3D = new int[frSpecSize][54];

            //  Determination of indices of neighbours at 1st, 2nd and 3rd coordination spheres
            // of each cell of specimen
            for(int cell_counter = 0; cell_counter<frSpecSize; cell_counter++)
                neighbours3D[cell_counter] = calcNeighbours3D(cell_counter, cell_number_I, cell_number_J, cell_number_K);

            // Single index of neighbour at 1st coordination sphere of current cell
            int neighb_index;

            // Index of cluster of neighbour of current cell at 1st coordination sphere
            int neighb_cluster_index;

            // Array of colours of clusters
            int[] cluster_colours = new int[total_grain_number];

            // Assignment of colours of clusters
            for(int cluster_counter = 0; cluster_counter < total_grain_number; cluster_counter++)
              cluster_colours[cluster_counter] = colour_number;

            // Array of indices of adjacent clusters of all clusters
            ArrayList[] adj_cluster_indices = new ArrayList[total_grain_number];

            for(int cluster_counter = 0; cluster_counter<total_grain_number; cluster_counter++)
                adj_cluster_indices[cluster_counter] = new ArrayList(0);

            // Array of inhibit colours of all clusters
            ArrayList[] inhibit_colours = new ArrayList[total_grain_number];

            // Initialization of array of inhibit colours of all clusters
            for(int cluster_counter = 0; cluster_counter<total_grain_number; cluster_counter++)
                inhibit_colours[cluster_counter] = new ArrayList(0);

            // Array of possible colours of all clusters
            ArrayList[] possible_colours = new ArrayList[total_grain_number];

            // Initialization of array of possible colours of all clusters
            for(int cluster_counter = 0; cluster_counter<total_grain_number; cluster_counter++)
                possible_colours[cluster_counter] = new ArrayList(0);

            // Index of colour of current cluster
            int cluster_colour_index;

            // Variable responsible for the fact that colour is chosen
       //     boolean colour_is_chosen = false;

            // Colour of current adjacent cluster
            int adj_cluster_colour;

            //TEST
            System.out.println();
            System.out.println("The specimen contains "+frSpecSize+" cells");

            for(int cell_counter = 0; cell_counter < frSpecSize; cell_counter++)
            {
                // Index of cluster containing current cell
                cluster_index = (int)frSpec.get(cell_counter);

              //  System.out.print("Cell # "+cell_counter+" is in cluster # "+cluster_index+" and has neighbours ## ");

                // Analysis of cluster indices of all neighbours at 1st sphere of current cell
                for(int neighb_counter = 0; neighb_counter < 12; neighb_counter++)
                {
                    // Current neighbour index
                    neighb_index = neighbours3D[cell_counter][neighb_counter];

                    // This neighbour must be in specimen
                    if((neighb_index>-1)&(neighb_index<frSpec.size()))
                    {
                   //     System.out.print(neighb_index+", ");

                        // Index of cluster containing this neighbour
                        neighb_cluster_index = (int)frSpec.get(neighb_index);

                        // If neighbour cell is in adjacent grain then the colour of current grain
                        // must be different from the colour of this adjacent grain
                        if(cluster_index*neighb_cluster_index != 0)
                        if(cluster_index != neighb_cluster_index)
                        if(!adj_cluster_indices[cluster_index-1].contains(neighb_cluster_index))
                            adj_cluster_indices[cluster_index-1].add(neighb_cluster_index);
                    }
                }
             //   System.out.println();
            }

            //TEST
        /*
            System.out.println();
            System.out.println("TEST 'Adjacent clusters'");
            for(int cluster_counter = 0; cluster_counter<total_grain_number; cluster_counter++)
            {
                System.out.println("Cluster # "+(cluster_counter+1)+" has "+adj_cluster_indices[cluster_counter].size()+
                                   " adjacent clusters ## "+adj_cluster_indices[cluster_counter].toString());
            }
            System.out.println("END OF TEST 'Adjacent clusters'");
            System.out.println();
         */

            // Index of error
            int error_index = GrainsHCPCommon.NO_ERROR;

            // Array of variables responsible for colouring of clusters
            boolean[] coloured_clusters = new boolean[total_grain_number];

            for(int col_clusters_counter = 0; col_clusters_counter<total_grain_number; col_clusters_counter++)
                coloured_clusters[col_clusters_counter] = false;

            // The variable is true if adjacent cluster is coloured.
            boolean adj_cluster_is_coloured = false;

            // The variable is true if all clusters are coloured.
            boolean all_clusters_are_coloured = false;

            // Variable responsible for presence of error of determination of cluster colours
            boolean error_of_det_colours = false;

            // Counter of cycles
            int cycle_counter = 1;

            if(total_grain_number > 1)//0)
            {
                cluster_colours[total_grain_number-1] = 0;
                coloured_clusters[total_grain_number-1] = true;
            }

         //   System.out.println("Cluster # 1 has adjacent clusters: "+adj_cluster_indices[0].toString());

            // Choice of colours of all clusters
            while(!all_clusters_are_coloured)
            {
              //  if(total_grain_number <= 10000)
                // Cycle over all clusters beginning from the second one (embryos_number = the number of clusters)
                for(int cluster_counter = total_grain_number-1; cluster_counter>0; cluster_counter--)
                if(!coloured_clusters[cluster_counter-1])
                {
                  // Search of adjacent cluster coloured at one of previous time steps
                  for(int adj_cluster_counter = 0; adj_cluster_counter < adj_cluster_indices[cluster_counter-1].size(); adj_cluster_counter++)
                  {
                    adj_cluster_is_coloured = coloured_clusters[(Integer)adj_cluster_indices[cluster_counter-1].get(adj_cluster_counter)-1];

                    if(adj_cluster_is_coloured)
                        adj_cluster_counter = adj_cluster_indices[cluster_counter-1].size();
                  }

                  // Analysis of colours of all adjacent clusters for current cluster if one adjacent cluster was coloured at one of previous time steps
                  if(adj_cluster_is_coloured)
                  for(int adj_cluster_counter = 0; adj_cluster_counter < adj_cluster_indices[cluster_counter-1].size(); adj_cluster_counter++)
                  {
                    adj_cluster_colour = cluster_colours[(Integer)adj_cluster_indices[cluster_counter-1].get(adj_cluster_counter)-1];

                    if(adj_cluster_colour != colour_number)
                    if(!inhibit_colours[cluster_counter-1].contains(adj_cluster_colour))
                        inhibit_colours[cluster_counter-1].add(adj_cluster_colour);
                  }

                  // Clearing of list of possible colors of current cluster
                  possible_colours[cluster_counter-1].clear();

                  // Choice of set of possible colours of current cluster
                  if(inhibit_colours[cluster_counter-1].size()>0)
                  {
                    // Creation of set of possible colours of current cluster
                    for(int colour_counter=0; colour_counter<colour_number; colour_counter++)
                    if(!inhibit_colours[cluster_counter-1].contains(colour_counter))
                        possible_colours[cluster_counter-1].add(colour_counter);

                    if(possible_colours[cluster_counter-1].size()>0)
                    {
                        // Choice of cluster colour from the set of possible colours
                        cluster_colour_index = (int)Math.floor(Math.random()*possible_colours[cluster_counter-1].size())-1;

                        if(cluster_colour_index < 0)              
                            cluster_colour_index = 0;
                        
                        if(cluster_colour_index >= possible_colours[cluster_counter-1].size()) 
                            cluster_colour_index = possible_colours[cluster_counter-1].size() - 1;

                        cluster_colours[cluster_counter-1] = ((Integer)possible_colours[cluster_counter-1].get(cluster_colour_index)).intValue();
                    }
                    else
                        cluster_colours[cluster_counter-1] = colour_number - 1;
                  }
                }
               
                all_clusters_are_coloured = true;

            //    if(total_grain_number <= 10000)
                {
                  // Increase of the number of coloured clusters
                  for(int cluster_counter = 1; cluster_counter<=cluster_colours.length; cluster_counter++)
                  if(cluster_colours[cluster_counter-1]<colour_number)
                  {
                    if((!coloured_clusters[cluster_counter-1])|((cluster_counter == 1)&(cycle_counter == 1)))
                    {
                     //   System.out.println("Cluster # "+cluster_counter+" has adjacent clusters: "+adj_cluster_indices[cluster_counter-1].toString());
                        coloured_clusters[cluster_counter-1] = true;
                    }
                  }
                  else
                    all_clusters_are_coloured = false;
                }
                 
                if(total_grain_number == 1)
                    all_clusters_are_coloured = true;
                
                cycle_counter++;
                
            //    if(total_grain_number <= 10000)
                if(cycle_counter>total_grain_number)
                {
                    all_clusters_are_coloured = true;
                    error_index = GrainsHCPCommon.ERROR_NOT_SUFFICIENT_COLOUR_NUMBER;

                    // Variable responsible for presence of error of determination of cluster colours
                    error_of_det_colours = true;

                  //  if(total_grain_number > 1)
                    //    System.out.println("ERROR: the number of colours is too small!!!");
                }
            }
            
            max_colour_index = 0;

        //    if(total_grain_number <= 10000)
            // Determination of number of colours of all clusters
            for(int cluster_counter = 1; cluster_counter <= total_grain_number; cluster_counter++)
              if(cluster_colours[cluster_counter-1] > max_colour_index)
                max_colour_index = cluster_colours[cluster_counter-1];
            
            if(total_grain_number <= 15)
                max_colour_index = total_grain_number - 1;
            
            if(total_grain_number > 15)
                max_colour_index = 15;
            
            // Real number of colours
            int real_colour_number = max_colour_index+1;
            
            int resolution = 256;
            int red, green, blue;
            int colour_index = -1;
            
            int red_number   = 9;
            int green_number = 9;
            int blue_number  = 9;
            
            // Number of colours of initial grains
            int init_grain_col_number = (red_number/3)*(green_number/3)*(blue_number/3);
            
            // Number of colours of new grains
            int new_grain_col_number  = (red_number - red_number/3 - 1)*(green_number - green_number/3 - 1)*(blue_number - blue_number/3 - 1);
            
            // Total number of colours of initial and new grains
            int max_col_number = init_grain_col_number + new_grain_col_number;
            
            real_colour_number = max_col_number;

            String[] colours = new String[max_col_number];
            
            for(int red_counter   = 0; red_counter   < red_number/3;   red_counter++)
            for(int green_counter = 0; green_counter < green_number/3; green_counter++)
            for(int blue_counter  = 0; blue_counter  < blue_number/3;  blue_counter++)
            {
                colour_index++;
                colours[colour_index] = ((resolution - 1)*red_counter)  /(red_number   - 1)+" "+
                                        ((resolution - 1)*green_counter)/(green_number - 1)+" "+
                                        ((resolution - 1)*blue_counter) /(blue_number  - 1);
            }
            
            for(int red_counter   = 1 + red_number/3;   red_counter   < red_number;   red_counter  ++)
            for(int green_counter = 1 + green_number/3; green_counter < green_number; green_counter++)
            for(int blue_counter  = 1 + blue_number/3;  blue_counter  < blue_number;  blue_counter ++)
            {
                colour_index++;
                colours[colour_index] = ((resolution - 1)*red_counter)  /(red_number   - 1)+" "+
                                        ((resolution - 1)*green_counter)/(green_number - 1)+" "+
                                        ((resolution - 1)*blue_counter) /(blue_number  - 1);
            }
            
            /*
            // Gray
            colours[0] = " "+0+" "+100+" "+100+" "+100+"\n";
            
            // White
            colours[1] = " "+1+" "+255+" "+255+" "+255+"\n";
            
            // Violet
            colours[2] = " "+2+" "+255+"   "+0+" "+255+"\n";
            
            // Yellow
            colours[3] = " "+3+" "+255+" "+255+"   "+0+"\n";
            
            // Gray milk
            colours[4] = " "+4+"   "+0+" "+255+" "+255+"\n";
            
            // Red
            colours[5] = " "+5+" "+255+"   "+0+"   "+0+"\n";
            
            // Green 
            colours[6] = " "+6+"   "+0+" "+255+"   "+0+"\n";
            
            // Blue
            colours[7] = " "+7+"   "+0+"   "+0+" "+255+"\n";

            // Dark-green
            colours[8] = " "+8+"  "+77+" "+153+" "+153+"\n";

            // Dark-violet
            colours[9] = " "+9+" "+153+"  "+77+" "+102+"\n";

            // Brown
            colours[10]= ""+10+" "+153+"  "+77+"  "+51+"\n";

            // Dark-brown
            colours[11]= ""+11+"  "+26+"  "+51+"  "+77+"\n";

            // Dark-blue
            colours[12]= ""+12+" "+179+" "+179+" "+255+"\n";

            // Light-green
            colours[13]= ""+13+" "+230+" "+128+" "+179+"\n";

            // Capuccino
            colours[14]= ""+14+"  "+97+"  "+54+"  "+77+"\n";

            // Gray-brown
            colours[15]= ""+15+" "+179+" "+153+" "+153+"\n";
            */
            
            try
            {
                String colours_file_name = Common.INIT_COND_PATH+"/"+LoadProperties.SPECIMEN_NAME+Common.INIT_COND_NAME+"/grain_colours."+GrainsHCPCommon.DEND_COLOR_EXTENSION;

                bw_colours   = new BufferedWriter(new FileWriter(colours_file_name));

                bw_colours.write(real_colour_number+" "+(total_grain_number+12)+" "+(total_grain_number+12)+" "+init_grain_col_number+" "+new_grain_col_number+"\n");
                bw_colours.flush();

                // Printing of indices of colours and its "RGB" parameters
                for(int colour_counter = 0; colour_counter < Math.min(real_colour_number, max_col_number); colour_counter++)
                {
                    bw_colours.write(colour_counter+" "+colours[colour_counter]+"\n");
                    bw_colours.flush();
                }

                for(int colour_counter = max_col_number; colour_counter < real_colour_number; colour_counter++)
                {
                    red   = (int)Math.floor(resolution*Math.random()) - 1;
                    green = (int)Math.floor(resolution*Math.random()) - 1;
                    blue  = (int)Math.floor(resolution*Math.random()) - 1;

                    if(red   < 0) red   = 0;
                    if(green < 0) green = 0;
                    if(blue  < 0) blue  = 0;

                    bw_colours.write(colour_counter+" "+red+" "+green+" "+blue+"\n");
                    bw_colours.flush();
                }

                max_colour_index = 0;
                
                // Printing of indices of all clusters and indices of its colours
                for(int cluster_counter = 1; cluster_counter <= total_grain_number; cluster_counter++)
                {
                    colour_index = cluster_colours[cluster_counter-1];

               //   if(total_grain_number < 10000)
                    if(colour_index >= colour_number)
                    {
                        error_of_det_colours = true;
                        
                    //    if(total_grain_number > 1)
                       //     System.out.println("colour_index = "+colour_index+"; colour_number = "+colour_number+". Number of colours is too small!!! \n");
                        
                        colour_index = colour_number - 1;
                    }

                 //   if(total_grain_number >= 10000)
                    {
                    //    bw_colours.write("colour_index = "+colour_index+"; colour_number = "+colour_number+". Number of colours is too small!!! \n");                        
                 //       colour_index = (int)Math.floor(Math.random()*real_colour_number);
                    }
                        
                    bw_colours.write(cluster_counter+" "+colour_index+"\n");

                    if(colour_index > max_colour_index)
                      max_colour_index = colour_index;

                    bw_colours.flush();
                }
                
                // Printing of indices of facets and cells and indices of its colours
                for(int cluster_counter = total_grain_number+1; cluster_counter <= total_grain_number+12; cluster_counter++)
                {
                  bw_colours.write(cluster_counter+" 0\n");
                  bw_colours.flush();
                }

                bw_colours.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: "+io_exc);
            }

            ArrayList adj_clusters = new ArrayList(0);
            int adj_cluster_index;

            // Printing of colour indices of all clusters
            for(int cluster_counter = 1; cluster_counter <= total_grain_number; cluster_counter++)
            {
                System.out.print("Cluster # "+cluster_counter+" has colour # "+cluster_colours[cluster_counter-1]+". ");
                System.out.print("Colours of adjacent grains:");
                
                adj_clusters = adj_cluster_indices[cluster_counter-1];

                for(int adj_grain_counter = 0; adj_grain_counter< adj_clusters.size(); adj_grain_counter++)
                {
                    adj_cluster_index = ((Integer)adj_clusters.get(adj_grain_counter)).intValue();
                    System.out.print(" ["+adj_cluster_index+"-"+cluster_colours[adj_cluster_index-1]);

                    if(cluster_colours[cluster_counter-1] == cluster_colours[adj_cluster_index-1])
                    {
                        error_of_det_colours = true;
                        System.out.println("Error of determination of colours!!!");
                        cluster_counter = total_grain_number;
                    }
                    else
                        System.out.print("]");
                }

            //    for(int adj_grain_counter = 0; adj_grain_counter< inhibit_colours[cluster_counter-1].size(); adj_grain_counter++)
              //      System.out.print(inhibit_colours[cluster_counter-1].get(adj_grain_counter)+" ");

                System.out.println();
            }
            
            if(total_grain_number == 1)
            {
                error_of_det_colours = false;
                cluster_colours[0] = 0;
            }

            if(!error_of_det_colours)
                System.out.println("The determination of colours is correct!!!");
            else
                System.out.println("Error of determination of colours!!!");

            System.out.println("Number of colours: "+(max_colour_index+1));
            // END OF TEST

            return error_of_det_colours;
        }

        /** The method returns indices of cells at 1st, 2nd and 3rd coordination spheres of given cell.
         * @param _intIndex index of given cell
         * @see neighbours3D
         */
        public static int[] calcNeighbours3D(int _intIndex, int _cell_number_X, int _cell_number_Y, int _cell_number_Z)
        {
            // Triple index of cell
            Three _tripleIndex = calcTripleIndex(_intIndex, _cell_number_X, _cell_number_Y, _cell_number_Z);

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

            int[] neighbours3D = new int[54];

            // If each triple index of neighbour cell is within boundaries then
            // the single index of the cell is calculated, else
            // this cell is deleted from the array of neighbours (single index = -3).
            for (int neighb_counter = 0; neighb_counter < 54; neighb_counter++)
            {
                neighbour_indexI = neighbours[neighb_counter].getI();
                neighbour_indexJ = neighbours[neighb_counter].getJ();
                neighbour_indexK = neighbours[neighb_counter].getK();

                if((neighbour_indexI > -1)&(neighbour_indexI < _cell_number_X)&
                   (neighbour_indexJ > -1)&(neighbour_indexJ < _cell_number_Y)&
                   (neighbour_indexK > -1)&(neighbour_indexK < _cell_number_Z))
                {
                    neighbours3D[neighb_counter] = neighbour_indexI + neighbour_indexJ*_cell_number_X +
                                                               neighbour_indexK*_cell_number_X*_cell_number_Y;
                }
                else
                    neighbours3D[neighb_counter] = -3;

             //   System.out.print(neighbours3D[neighb_counter]+" ");
            }
         //   System.out.println();

            return neighbours3D;
        }
    }