package cellcore;
import util.*;
import recGeometry.*;
import JSci.maths.matrices.*;

/* @(#) RecCellR3.java version 1.0.7;       Nov 2008
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about cell3D for simulation of processes in solid
 *
 *=============================================================
 *  Last changes :
 *        14 November 2008 by Pavel V. Maksimov 
 *       (fields "thermal_conduct_bound" and "phonon_portion" 
 *        and methods "get-" and "set-" for them are added).
 *        File version 1.0.7
 */

/** Class for info about cell3D for simulation of processes in solid
* @author Dmitrii D. Moiseenko & Pavel V. Maksimov
* @version 1.0 - Nov 2005
*/
public class RecCellR3
{
    /** The indices (coordinates) of cell3D for simulation of processes in solid
     */
    Three indices;
    
    /** Cell3D size in direction of axis X */
    double cellSizeX;
    
    /** Cell3D size in direction of axis Y */
    double cellSizeY;
  
    /** Cell3D size in direction of axis Z */
    double cellSizeZ;

    /** Type of packing of cells in specimen(SCP or HCP) */
    byte packing_type;
    
    /** Volume of cell3D for simulation of processes in solid
     */
    double volume;
     
    /** Type of material */
//    Material material;
    
    /** Type of crystal lattice packing of cell3D for simulation of processes in solid: 
     *                    "SCP" - simple cubic packing,
     *                    "BCC" - body-centered cubic lattice,
     *                    "FCC" - face-centered cubic lattice,
     *                    "HCP" - hexagonal close packing.
     */
    byte state;
    
    /** Name of file with information about material */
    String material_file;
       
    /** Modulus of elasticity of cell3D (MPa) */
    double mod_elast;
    
    /** Density of cell3D (kg/m3) */
    double density;
    
    /** Coefficient of heat expansion of cell3D (1/K) */
    double heatExpansionCoeff;
    
    /** Yield strain of cell3D */
    double yieldStrain;
    
    /** Ultimate strain of cell3D */
    double ultimateStrain;

    /** Coefficient determining switch of cell to plastic state */
    double yield_state_coeff;

    /** Coefficient determining switch of cell to damaged state */
    double ultimate_state_coeff;
    
    /** Coefficient of division of change of thermal energy of cell3D */
    double energy_coeff;
    
    /** Specific heat capacity of cell3D */
    double specific_heat_capacity;
    
    /** Thermal conductivity of cell3D */
    double thermal_conductivity;
    
    /** Thermal conductivity of cell3D on grain boundary */
    double thermal_conduct_bound;
    
    /** Portion of phonon thermal conductivity 
     * in total thermal conductivity of cell3D 
     */
    double phonon_portion;
    
    /** Activation energy of cell3D */
    double actEnergy;
    
    /** Maximal value of boundary mobility of cell3D under heat recrystallization */
    double maxMobility;
    
    /** Maximal value of boundary mobility of cell3D under cold recrystallization */
    double dislMaxMobility;
    
    /** Maximal value of boundary mobility under mechanical loading */
    double mechMaxMobility;
    
    /** Transition limit to high angle grain boundary regime of cell3D material */
    double angleLimitHAGB;
    
    /** Specific energy of high angle grain boundary of cell3D material */
    double energyHAGB;
    
    /** Volume fraction of foreign particles of cell3D material */
    double part_volume_fraction;
    
    /** Radius of foreign particle of cell3D material */
    double particle_radius;
    
    /** Total energy of cell3D */
    double total_energy;
  
    /** Mechanical energy of cell3D */
    double mech_energy;

    /** Thermal energy of cell3D */
    double thermal_energy;

    /** Torsion energy of cell3D */
    double torsion_energy;
  
    /** Entropy of cell3D */
    double entropy;
    
    /** Current temperature of cell3D */    
    double temperature;
    
    /** Initial temperature of cell3D */    
    double initialTemperature;
    
    /** Stress in cell */
    double stress;
    
    // Previous stress of cell
    double old_stress;

    /** Strain of cell */
    double strain;
    
    // Total heat strain
    double heat_strain;
    
    // Change of heat strain
    double heat_strain_change;
    
    /** Euler angles of cell3D */
    double[] eulerAngles;
    
    /** Index of grain containing cell3D */
    int grainIndex;
    
    /** Array of single indices of neighbour cells at 1st coordination sphere
     * in case of hexagonal close packing of cells */
  //  int[] neighbour_indices_HCP;
    
    /** Type of cell location on grain boundary:
     * true - if cell is located on grain boundary, 
     * false - if cell is not located on grain boundary
     */  
    boolean locationOnBoundary;

    /** Type of cell location on specimen boundary:
     * true - if cell is located on specimen boundary,
     * false - if cell is not located on specimen boundary
     */
    boolean locationOnSpecimenBoundary;
     
    /** Shear modulus */
    double mod_shear;
    
    /** Lattice parameter */
    double lattice_parameter;    
    
    /** Lengths of 3 vectors of lattice anisotropy*/
    private double lattice_vector_A_length;        
    private double lattice_vector_B_length;
    private double lattice_vector_C_length;
    
    /** Angles between vectors of lattice anisotropy*/
    private double lattice_angle_vecA_vecB;
    private double lattice_angle_vecB_vecC;
    private double lattice_angle_vecC_vecA;
    
    /** Coefficient of lattice anisotropy*/
    private double lattice_anis_coeff;
    
    /** Coefficient concerning character of distribution of dislocations */
    double disl_distr_coeff;
    
    /** Type of cell according to its location in grain and in specimen
     */
    byte type;

    /** Type of grain containing cell according to its energy interaction
     * with neighbour grains
     */
    byte grain_type;

    /** Density of dislocations of cell */
    double disl_density;
    
    /** Coordinates of cell */
    VectorR3 coordinates;

    /** Force moment of cell */
    VectorR3 force_moment;
    
    /** Angle velocity of cell */
    VectorR3 angle_velocity;

    /** Displacement vector of the cell */
    VectorR3 displ_vector;

    /** Strain tensor of cell*/
    DoubleMatrix strain_tensor;

    /** Stress tensor of cell*/
    DoubleMatrix stress_tensor;

    /** Average stress */
    double average_stress;

    /** Component X of force moment calculated starting from stress tensor */
    double moment_X;

    /** Component Y of force moment calculated starting from stress tensor */
    double moment_Y;

    /** Component Z of force moment calculated starting from stress tensor */
    double moment_Z;
    
    /** Array of centres of cell boundaries */
    PointR3[] bound_centres;
    
    /** Array of centres of normal vectors of cell boundaries with neighbours */
    VectorR3[] bound_norm_vectors;
    
    /** Array of stresses at cell boundaries */
    double[] bound_stresses;
    
    /** Array of strain velocities at cell boundaries */
    double[] bound_velocities;
    
    /** Component X of torsion angle */
    double torsion_angle_X;
    
    /** Component Y of torsion angle */
    double torsion_angle_Y;
    
    /** Component Z of torsion angle */
    double torsion_angle_Z;
    
    /** the value of cell torsion energy change */
    private double torsion_energy_change;
    
    /** the coefficient for calculation of cell torsion energy change */
    private double torsion_energy_coeff;
    
    /** Coefficient for calculation of local torsion energy at grain boundary */
    double torsion_energy_coeff_gr_bound;
    
    /** Coefficient for calculation of local torsion energy in a region near grain boundary */
    double torsion_energy_coeff_gb_region;
    
    /** current coefficient for calculation of cell torsion energy change */
    private double current_torsion_energy_coeff;
    
    /** Low temperature threshold value for recrystallization */
    private double lowTemperThreshold;
    
    /** High temperature threshold value for recrystallization */
    private double highTemperThreshold;
    
    /** Temperature of start of twinning */
    private double min_twin_temperature;
    
    /** Temperature of finish of twinning */
    private double twinning_temperature;
    
    /** threshold stress for phase transition of cell material */
    private double threshold_stress;
    
    /** the value of change of cell mechanical energy */
    public double  mech_energy_change;
    
    /** Instant specific force moment of cell */
    private VectorR3 instant_force_moment;
    
    /** Total number of 1D and 2D defects in material contained in a cell */
    private long defect_number;
    
    /** Total number of atoms or molecules in material contained in a cell */
    private long atom_number;
    
    /** Molar mass of material contained in a cell */
    private double molar_mass;
    
    /** Change of torsion angle */
  //  double torsion_angle_change;
    
    /** Heat influx to cell */
    private double heat_influx;
    
    /** volume portion of particles */
    private double part_vol_fraction;
    
    // stress vector of cell
    private VectorR3 stress_vector;
    
    /** The constructor creates cell3D: indices, sizes, volume, state and physical properties.
     * @param cell_size_X cell3D size in direction of axis X
     * @param cell_size_Y cell3D size in direction of axis Y
     * @param cell_size_Z cell3D size in direction of axis Z
     * @param _indices triple index of cell3D
     * @param _material name of file with information about material
     * @param _packing_type type of packing of cells in specimen(SCP or HCP)
     */
    public RecCellR3(double cell_size_X, double cell_size_Y, double cell_size_Z,
                     Three _indices,     String _material,   byte _packing_type)
    {
        indices            = new Three(_indices);
        cellSizeX          = cell_size_X;
        cellSizeY          = cell_size_Y;
        cellSizeZ          = cell_size_Z;
        packing_type       =_packing_type;
        calcVolume(cellSizeX, cellSizeY, cellSizeZ);
        
        material_file      = new String(_material);
        Material material  = new Material(material_file);
        
        // Loading of parameters of cell3D material
        setMaterial(material);        

        old_stress         = 0;
        stress             = 0;
        strain             = 0;
        heat_strain        = 0; 
        state              = Common.ELASTIC_CELL;
        mech_energy        = 0;
        thermal_energy     = 0;
        total_energy       = 0;
        entropy            = 0;
        temperature        = 0;
        initialTemperature = 0;
        grainIndex         =-1;
        locationOnBoundary = false;
        locationOnSpecimenBoundary = false;
        grain_type         = Common.INNER_CELL;
        type               = Common.INTRAGRANULAR_CELL;
        disl_density       = 0;
        
        force_moment       = new VectorR3(0, 0, 0);
        angle_velocity     = new VectorR3(0, 0, 0);
        torsion_angle_X    = 0;
        torsion_angle_Y    = 0;
        torsion_angle_Z    = 0;
        instant_force_moment = new VectorR3(0, 0, 0);

        coordinates        = new VectorR3(0, 0, 0);
        displ_vector       = new VectorR3(0, 0, 0);
        strain_tensor      = new DoubleMatrix(3, 3);
        stress_tensor      = new DoubleMatrix(3, 3);
        //System.out.println("_material = "+_material+", "+_state);
        stress_vector      = new VectorR3(0, 0, 0);

        average_stress      = 0;
        moment_X            = 0;
        moment_Y            = 0;
        moment_Z            = 0;
        
        torsion_energy      = 0;
        ultimateStrain      = 0;
        energyHAGB          = 0;
        
        // Number of neighbours at 1st coordination sphere
        int neighb1S_number = 0;
        
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            neighb1S_number = 12;

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            neighb1S_number = 6;
        
        // Array of centres of cell boundaries
        bound_centres                = new PointR3[neighb1S_number];
   
        // Array of centres of normal vectors of cell boundaries with neighbours
        bound_norm_vectors           = new VectorR3[neighb1S_number];
    
        // Array of stresses at cell boundaries
        bound_stresses               = new double[neighb1S_number];
    
        // Array of strain velocities at cell boundaries
        bound_velocities             = new double[neighb1S_number];
        
        // the value of cell torsion energy change
        torsion_energy_change        = 0;
        
        // the coefficient for calculation of cell torsion energy change
        torsion_energy_coeff         = material.getTorsionEnergyCoeff();
        
        // Coefficient for calculation of local torsion energy at grain boundary
        torsion_energy_coeff_gr_bound = material.getTorsionEnergyCoeffForGrBound();
    
        // Coefficient for calculation of local torsion energy in a region near grain boundary
        torsion_energy_coeff_gb_region = material.getTorsionEnergyCoeffForGrBoundRegion();
        
        // current coefficient for calculation of cell torsion energy change
        current_torsion_energy_coeff = material.getTorsionEnergyCoeff();
        
    //    System.out.println("material_file = "+material_file+"; current_torsion_energy_coeff = "+current_torsion_energy_coeff);
    
        // Low temperature threshold value for recrystallization
        lowTemperThreshold      = material.getLowTemperThreshold();
        
        // High temperature threshold value for recrystallization
        highTemperThreshold     = material.getHighTemperThreshold();
        
        // Temperature of start of twinning
        min_twin_temperature    = material.getMinTwinTemperature();
    
        // Temperature of finish of twinning
        twinning_temperature    = material.getTwinningTemperature();
        
        // Threshold stress for phase transition of cell material
        threshold_stress   = material.getThresholdStress();
    
        // the value of change of cell mechanical energy
        mech_energy_change = 0;
        
        // total number of atoms or molecules in material contained in a cell
        atom_number        = 0;
        
        // total number of 1D and 2D defects in material contained in a cell
        defect_number      = 0;
        
        // Molar mass of material contained in a cell
        molar_mass         = 0;
        
        // Heat influx to cell
        heat_influx        = 0;
        
        lattice_parameter       = 0;
        
        // Lengths of 3 vectors of lattice anisotropy
        lattice_vector_A_length = 0;
        lattice_vector_B_length = 0;
        lattice_vector_C_length = 0;
    
        // Angles between vectors of lattice anisotropy
        lattice_angle_vecA_vecB = 0;
        lattice_angle_vecB_vecC = 0;
        lattice_angle_vecC_vecA = 0;
    
        // Coefficient of lattice anisotropy
        lattice_anis_coeff      = 0;
        
        // volume portion of particles
        part_vol_fraction = 0;
    }
    
    /** The constructor creates cell3D for simulation of deformation:
     * indices, sizes, volume and physical properties of the cell3D.
     * @param defCellR3 sample cell3D
     */
    public RecCellR3(RecCellR3 defCellR3)
    {
        // Setting of indices of neighbour cells
    //    setNeighbourIndicesHCP(defCellR3.getNeighbourIndicesHCP());
         
        indices         = new Three(defCellR3.getIndices());
        cellSizeX       = defCellR3.getCellSizeX();
        cellSizeY       = defCellR3.getCellSizeY();
        cellSizeZ       = defCellR3.getCellSizeZ();

        packing_type    = defCellR3.getPackingType();
        volume          = defCellR3.getVolume();
        
        //Setting of physical properties of the cell3D.
        material_file   = new String(defCellR3.getMaterialFile());
                      
        // Loading of parameters of cell3D material
        mod_elast               = defCellR3.get_mod_elast();
        density                 = defCellR3.get_density();
        heatExpansionCoeff      = defCellR3.get_heatExpCoeff();
        yieldStrain             = defCellR3.get_yieldStrain();
        ultimateStrain          = defCellR3.getUltimateStrain();
        yield_state_coeff       = defCellR3.getYieldStateCoeff();
        ultimate_state_coeff    = defCellR3.getUltimateStateCoeff();
        energy_coeff            = defCellR3.get_energy_coeff();
        specific_heat_capacity  = defCellR3.get_specific_heat_capacity();
        thermal_conductivity    = defCellR3.get_thermal_conductivity();
        thermal_conduct_bound   = defCellR3.get_thermal_conduct_bound();
        phonon_portion          = defCellR3.get_phonon_portion();
        actEnergy               = defCellR3.get_activation_energy();
        angleLimitHAGB          = defCellR3.getAngleLimitHAGB();
        energyHAGB              = defCellR3.getEnergyHAGB();
        maxMobility             = defCellR3.getMaxMobility();
        dislMaxMobility         = defCellR3.getDislMaxMobility();
        mechMaxMobility         = defCellR3.getMechMaxMobility();
        part_volume_fraction    = defCellR3.get_partVolumeFraction();
        particle_radius         = defCellR3.get_particleRadius();
        eulerAngles             = defCellR3.getEulerAngles();  

        old_stress              = defCellR3.getPrevMechStress();
        stress                  = defCellR3.getMechStress();
        strain                  = defCellR3.getStrain();
        heat_strain             = defCellR3.getHeatStrain();
        state                   = defCellR3.getState();
        mech_energy             = defCellR3.getMechEnergy();        
        thermal_energy          = defCellR3.getThermalEnergy();
        total_energy            = defCellR3.getTotalEnergy();
        entropy                 = defCellR3.getEntropy();
        temperature             = defCellR3.getTemperature();
        initialTemperature      = defCellR3.getInitialTemperature();
        grainIndex              = defCellR3.getGrainIndex();
        locationOnBoundary      = defCellR3.getLocationOnBoundary();
        locationOnSpecimenBoundary= defCellR3.getLocationOnSpecBoundary();
        
        grain_type              = defCellR3.getGrainType();
        disl_density            = defCellR3.getDislDensity();
        
        force_moment            = defCellR3.getForceMoment();
        angle_velocity          = defCellR3.getAngleVelocity();
        torsion_angle_X         = defCellR3.getTorsionAngleX();
        torsion_angle_Y         = defCellR3.getTorsionAngleY();
        torsion_angle_Z         = defCellR3.getTorsionAngleZ();
        instant_force_moment    = defCellR3.getInstantForceMoment();
        
        mod_shear               = defCellR3.getModShear();
        
        lattice_parameter       = defCellR3.getLatticeParameter();
        
        // Lengths of 3 vectors of lattice anisotropy
        lattice_vector_A_length = defCellR3.getLatticeVector_A_Length();
        lattice_vector_B_length = defCellR3.getLatticeVector_B_Length();
        lattice_vector_C_length = defCellR3.getLatticeVector_C_Length();
    
        // Angles between vectors of lattice anisotropy
        lattice_angle_vecA_vecB = defCellR3.getLatticeAngle_vecA_vecB();
        lattice_angle_vecB_vecC = defCellR3.getLatticeAngle_vecB_vecC();
        lattice_angle_vecC_vecA = defCellR3.getLatticeAngle_vecC_vecA();
    
        // Coefficient of lattice anisotropy
        lattice_anis_coeff      = defCellR3.getLatticeAnisCoeff();
        
        disl_distr_coeff        = defCellR3.getDislDistrCoeff();
        
        coordinates             = defCellR3.getCoordinates();
        displ_vector            = defCellR3.getDisplVector();
        strain_tensor           = defCellR3.getStrainTensor();
        stress_tensor           = defCellR3.getStressTensor();
        stress_vector           = defCellR3.getStressVector();

        average_stress          = defCellR3.getAverageStress();
        
        moment_X                = defCellR3.getMomentX();
        moment_Y                = defCellR3.getMomentY();        
        moment_Z                = defCellR3.getMomentZ();
        
        torsion_energy          = defCellR3.getTorsionEnergy();
        
        // Array of centres of cell boundaries
        bound_centres           = defCellR3.getBoundCentres();
   
        // Array of centres of normal vectors of cell boundaries with neighbours
        bound_norm_vectors      = defCellR3.getBoundNormVectors();
    
        // Array of stresses at cell boundaries
        bound_stresses          = defCellR3.getBoundStresses();
    
        // Array of strain velocities at cell boundaries
        bound_velocities        = defCellR3.getBoundVelocities();
        
        // the value of cell torsion energy change
        torsion_energy_change   = defCellR3.getTorsionEnergyChange();
        
        // the coefficient for calculation of cell torsion energy change
        torsion_energy_coeff    = defCellR3.getTorsionEnergyCoeff();
        
        // Coefficient for calculation of local torsion energy at grain boundary
        torsion_energy_coeff_gr_bound = defCellR3.getTorsionEnergyCoeffForGrBound();
    
        // Coefficient for calculation of local torsion energy in a region near grain boundary
        torsion_energy_coeff_gb_region = defCellR3.getTorsionEnergyCoeffForGrBoundRegion();
        
        // current coefficient for calculation of cell torsion energy change
        current_torsion_energy_coeff = defCellR3.getCurrentTorsionEnergyCoeff();
        
        // Low temperature threshold value for recrystallization
        lowTemperThreshold      = defCellR3.getLowTemperThreshold();
        
        // High temperature threshold value for recrystallization
        highTemperThreshold     = defCellR3.getHighTemperThreshold();
        
        // Temperature of start of twinning
        min_twin_temperature    = defCellR3.getMinTwinTemperature();
        
        // Temperature of finish of twinning
        twinning_temperature    = defCellR3.getTwinningTemperature();
        
        // Threshold stress for phase transition of cell material
        threshold_stress        = defCellR3.getThresholdStress();
    
        // the value of change of cell mechanical energy
        mech_energy_change      = defCellR3.getMechEnergyChange();
        
        // total number of atoms or molecules in material contained in a cell
        atom_number             = defCellR3.getAtomNumber();
        
        // total number of 1D and 2D defects in material contained in a cell
        defect_number           = defCellR3.getDefectNumber();
        
        // Molar mass of material contained in a cell
        molar_mass              = defCellR3.getMolarMass();
        
        // Heat influx to cell
        heat_influx             = defCellR3.getHeatInflux();
        
        // volume portion of particles
        part_vol_fraction       = defCellR3.getParticleVolumeFraction();
        
        setType(defCellR3.getType());
    }
    
    /** The constructor creates new cell3D for simulation of deformation.
     */
    public RecCellR3()
    {
        indices         = new Three();
        cellSizeX       = 0;
        cellSizeY       = 0;
        cellSizeZ       = 0;
        calcVolume();

        packing_type    = Common.HEXAGONAL_CLOSE_PACKING;
        volume          = 0;
        
        //Setting of physical properties of the cell3D.
        material_file   = new String();
                      
        // Loading of parameters of cell3D material
        mod_elast               = 0;
        mod_shear               = 0;
        density                 = 0;
        heatExpansionCoeff      = 0;
        yieldStrain             = 0;
        ultimateStrain          = 0;
        yield_state_coeff       = 0;
        ultimate_state_coeff    = 0;
        energy_coeff            = 0;
        specific_heat_capacity  = 0;
        thermal_conductivity    = 0;
        thermal_conduct_bound   = 0;
        phonon_portion          = 0;
        actEnergy               = 0;
        angleLimitHAGB          = 0;
        energyHAGB              = 0;
        maxMobility             = 0;
        dislMaxMobility         = 0;
        mechMaxMobility         = 0;
        part_volume_fraction    = 0;
        particle_radius         = 0;
        eulerAngles             = new double[3];

        old_stress              = 0;
        stress                  = 0;
        strain                  = 0;
        heat_strain             = 0;
        state                   = Common.ELASTIC_CELL;
        mech_energy             = 0;
        thermal_energy          = 0;
        total_energy            = 0;
        entropy                 = 0;
        temperature             = 0;
        initialTemperature      = 0;
        grainIndex              = 0;
        locationOnBoundary        = false;
        locationOnSpecimenBoundary= false;
        type                    = 0;
        grain_type              = 0;
        disl_density            = 0;
        
        force_moment            = new VectorR3();
        angle_velocity          = new VectorR3();
        torsion_angle_X         = 0;
        torsion_angle_Y         = 0;
        torsion_angle_Z         = 0;
        instant_force_moment    = new VectorR3();
        
        lattice_parameter       = 0;
        
        // Lengths of 3 vectors of lattice anisotropy
        lattice_vector_A_length = 0;
        lattice_vector_B_length = 0;
        lattice_vector_C_length = 0;
    
        // Angles between vectors of lattice anisotropy
        lattice_angle_vecA_vecB = 0;
        lattice_angle_vecB_vecC = 0;
        lattice_angle_vecC_vecA = 0;
    
        // Coefficient of lattice anisotropy
        lattice_anis_coeff      = 0;
        
        disl_distr_coeff        = 0;
        
        coordinates             = new VectorR3();
        displ_vector            = new VectorR3();
        strain_tensor           = new DoubleMatrix(3, 3);
        stress_tensor           = new DoubleMatrix(3, 3);
        stress_vector           = new VectorR3(0, 0, 0);

        average_stress          = 0;
        
        moment_X = 0;
        moment_Y = 0;   
        moment_Z = 0;
        
        torsion_energy = 0;
        
        // Number of neighbours at 1st coordination sphere
        int neighb1S_number = 0;
        
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            neighb1S_number = 12;

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            neighb1S_number = 6;
        
        // Array of centres of cell boundaries
        bound_centres = new PointR3[neighb1S_number];
   
        // Array of centres of normal vectors of cell boundaries with neighbours
        bound_norm_vectors = new VectorR3[neighb1S_number];
    
        // Array of stresses at cell boundaries
        bound_stresses = new double[neighb1S_number];
    
        // Array of strain velocities at cell boundaries        
        bound_velocities = new double[neighb1S_number];
        
        // the value of cell torsion energy change
        torsion_energy_change = 0;
        
        // the coefficient for calculation of cell torsion energy change
        torsion_energy_coeff  = 0;
        
        // Coefficient for calculation of local torsion energy at grain boundary
        torsion_energy_coeff_gr_bound = 0;
    
        // Coefficient for calculation of local torsion energy in a region near grain boundary
        torsion_energy_coeff_gb_region = 0;
        
        // current coefficient for calculation of cell torsion energy change
        current_torsion_energy_coeff = 0;
        
        // Low temperature threshold value for recrystallization 
        lowTemperThreshold      = 0;
        
        // High temperature threshold value for recrystallization
        highTemperThreshold     = 0;
        
        // Temperature of start of twinning
        min_twin_temperature    = 0;
                
        // Temperature of finish of twinning
        twinning_temperature    = 0;
        
        // threshold stress for phase transition of cell material
        threshold_stress   = 0;
    
        // the value of change of cell mechanical energy
        mech_energy_change = 0;
        
        // total number of atoms or molecules in material contained in a cell
        atom_number        = 0;
        
        // total number of 1D and 2D defects in material contained in a cell
        defect_number      = 0;
        
        // Molar mass of material contained in a cell
        molar_mass         = 0;
        
        // Heat influx to cell
        heat_influx        = 0;
        
        // volume portion of particles
        part_vol_fraction  = 0;
    }
    
    /** The method returns torsion energy of cell3D.
     * @return torsion energy of cell3D
     */
    public double getTorsionEnergy()
    {
        return torsion_energy;
    }

    /** The method returns type of packing of cells in specimen (SCP or HCP).
     * @return type of packing of cells in specimen (SCP or HCP)
     */
    public byte getPackingType()
    {
        return packing_type;
    }

    /** The method sets type of packing of cells in specimen (SCP or HCP).
     * @param _packing_type type of packing of cells in specimen (SCP or HCP)
     */
    public void setPackingType(byte _packing_type)
    {
        packing_type = _packing_type;
    }

    /** The method calculates volume of cell3D for modelling of deformation.
     * @param size_X size of the cell3D in direction of axis X
     * @param size_Y size of the cell3D in direction of axis Y
     * @param size_Z size of the cell3D in direction of axis Z
     */
    public void calcVolume(double size_X, double size_Y, double size_Z)
    {
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            volume = 0.6938*size_X*size_Y*size_Z;

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            volume = size_X*size_Y*size_Z;
    }
    
    /** The method calculates volume of cell3D.
     */
    public double calcVolume()
    {
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
            volume = 0.6938*cellSizeX*cellSizeY*cellSizeZ;

        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
            volume = cellSizeX*cellSizeY*cellSizeZ;

        return volume;
    }    
    
    /** The method sets volume of cell3D for modelling of deformation.
     * @param _volume volume of cell3D.
     */
    public void setVolume (double _volume)
    {
        volume = _volume;
    }
    
    /** The method returns volume of cell3D for modelling of deformation.
     * @return volume of cell3D.
     */
    public double getVolume()
    {
        return volume;
    }
    
    /** The method returns thermal energy of cell3D for modelling of deformation.
     * @return thermal energy of cell3D.
     */
    public double getThermalEnergy()
    {
        return thermal_energy;
    }
    
    /** The method sets thermal energy influx to cell3D for modelling of deformation.
     * @param _thermal_energy thermal energy influx to cell3D.
     */
    public void setThermalEnergy(double _thermal_energy)
    {
        thermal_energy = _thermal_energy;
    }
    
    /** The method returns mechanical energy of cell3D for modelling of deformation.
     * @return mechanical energy of cell3D.
     */
    public double getMechEnergy()
    {
        return mech_energy;
    }
    
    /** The method sets mechanical energy of cell3D for modelling of deformation.
     * @param _mech_energy mechanical energy of cell3D.
     */
    public void setMechEnergy(double _mech_energy)
    {
        mech_energy = _mech_energy;
    }

    /** The method changes mechanical energy of cell3D for modelling of deformation.
     * @param _mech_energy additional mechanical energy of cell3D.
     */
    public void addMechEnergy(double _mech_energy)
    { 
        mech_energy+= _mech_energy;
    }
    
    /** The method returns triple index of cell3D for modelling of deformation.
     * @return triple index of cell3D.
     */
    public Three getIndices()
    {
        return indices;
    }
    
    /** The method sets triple index of cell3D for modelling of deformation.
     * @param _indices triple index of cell3D.
     */
    public void setIndices (Three _indices)
    {
        indices = new Three (_indices);
    }

    /** The method returns coordinates of cell3D.
     * @return coordinates of cell3D
     */
    public VectorR3 getCoordinates()
    {
        return coordinates;
    }

    /** The method sets coordinates of cell3D.
     * @param _coordinates new coordinates of cell3D
     */
    public void setCoordinates(VectorR3 _coordinates)
    {
        coordinates = new VectorR3(_coordinates);
    }
    
    /** The method returns force moment of cell3D.
     * @return force moment of cell3D
     */
    public VectorR3 getForceMoment()
    {
        return force_moment;
    }
    
    /** The method returns component X of force moment of cell3D.
     * @return component X of force moment of cell3D
     */
    public double getForceMomentX()
    {
        return force_moment.getX();
    }
    
    /** The method returns component Y of force moment of cell3D.
     * @return component Y of force moment of cell3D
     */
    public double getForceMomentY()
    {
        return force_moment.getY();
    }
    
    /** The method returns component Z of force moment of cell3D.
     * @return component Z of force moment of cell3D
     */
    public double getForceMomentZ()
    {
        return force_moment.getZ();
    }
    
    /** The method sets force moment of cell3D.
     * @param _force_moment force moment of cell3D
     */
    public void setForceMoment(VectorR3 _force_moment)
    {
        force_moment = new VectorR3(_force_moment);
    }
    
    /** The method returns angle velocity of cell3D.
     * @return angle velocity of cell3D
     */
    public VectorR3 getAngleVelocity()
    {
        return angle_velocity;
    }
    
    /** The method returns component X of angle velocity of cell3D.
     * @return component X of angle velocity of cell3D
     */
    public double getAngleVelocityX()
    {
        return angle_velocity.getX();
    }
    
    /** The method returns component Y of angle velocity of cell3D.
     * @return component Y of angle velocity of cell3D
     */
    public double getAngleVelocityY()
    {
        return angle_velocity.getY();
    }
    
    /** The method returns component Z of angle velocity of cell3D.
     * @return component Z of angle velocity of cell3D
     */
    public double getAngleVelocityZ()
    {
        return angle_velocity.getZ();
    }
    
    /** The method sets angle velocity of cell3D.
     * @param _angle_velocity new angle velocity of cell3D
     */
    public void setAngleVelocity(VectorR3 _angle_velocity)
    {
        angle_velocity = new VectorR3(_angle_velocity);
    }
    
    /** The method calculates absolute value of force moment of cell3D.
     * @return absolute value of force moment of cell3D
     */
    public double calcAbsForceMoment()
    {
       return force_moment.getLength();
    }

    /** The method calculates torsion energy of cell3D and adds it to mechanical energy.
     * @param moment_module absolute value of force moment of cell3D
     */
    public void addTorsionEnergy(double moment_module)
    {
        torsion_energy = 16*Math.abs(moment_module)*moment_module/
                           (Math.PI*mod_shear*cellSizeX*cellSizeY*cellSizeZ);
        
        mech_energy+= torsion_energy*1.0E-12;
        
        calcMechStressAndStrain();
    }
    
    /** The method returns temperature of cell3D for modelling of deformation.
     * @return temperature of cell3D.
     */
    public double getTemperature()
    {
        return temperature;
    }
    
    /** The method returns initial temperature of cell3D for modelling of deformation.
     * @return initial temperature of cell3D.
     */
    public double getInitialTemperature()
    {
        return initialTemperature;
    }
    
    /** The method sets temperature of cell3D for modelling of deformation.
     * @param _temperature temperature of cell3D.
     */
    public void setTemperature(double _temperature)
    {
        temperature = _temperature;
    }
        
    /** The method sets initial temperature of cell3D for modelling of deformation.
     * @param _initialTemperature initial temperature of cell3D.
     */
    public void setInitialTemperature(double _initialTemperature)
    {
        initialTemperature = _initialTemperature;
    }
    
    /** The method returns type of crystal lattice packing of cell3D for modelling of deformation.
     * @return type of crystal lattice packing of cell3D.
     */
    public byte getState()
    {
        return state;
    }
    
    /** The method sets type of crystal lattice packing of cell3D for modelling of deformation.
     * @param _state type of crystal lattice packing of cell3D.
     */
    public void setState(byte _state)
    {
        state = _state;
    }
    
    /** The method calculates entropy of cell3D for modelling of deformation 
     * in case of isothermal process.
     */
    public void calcEntropy()
    {
        entropy = thermal_energy/temperature;
    }
    
    /** The method returns total energy of cell3D for modelling of deformation.
     * @return total energy of cell3D.
     */
    public double getTotalEnergy()
    {
        return total_energy;
    }
  
    /** The method returns entropy of cell3D for modelling of deformation.
     * @return entropy of cell3D.
     */
    public double getEntropy()
    {
        return entropy;
    }
 
    /** The method sets total energy of cell3D for modelling of deformation.
     * @param _total_energy total energy of cell3D.
     */
    public void setTotalEnergy(double _total_energy)
    {
        total_energy = _total_energy;
    }     
    
    /** The method sets torsion energy of cell3D for modelling of deformation.
     * @param _torsion_energy torsion energy of cell3D.
     */
    public void setTorsionEnergy(double _torsion_energy)
    {
        torsion_energy = _torsion_energy;
    } 
  
    /** The method sets entropy of cell3D for modelling of deformation.
     * @param _entropy entropy of cell3D.
     */
    public void setEntropy(double _entropy)
    {
        entropy = _entropy;
    }
    
    /** The method returns cell3D size in direction of axis X.
     * @return cell3D size in direction of axis X
     */
    public double getCellSizeX()
    {
        return cellSizeX;
    }
    
    /** The method sets cell3D size in direction of axis X.
     * @param cell_size_X cell3D size in direction of axis X
     */
    public void setCellSizeX(double cell_size_X)
    {
        cellSizeX = cell_size_X;
    }
    
    /** The method returns cell3D size in direction of axis Y.
     * @return cell3D size in direction of axis Y
     */
    public double getCellSizeY()
    {
        return cellSizeY;
    }
    
    /** The method sets cell3D size in direction of axis Y.
     * @param cell_size_Y cell3D size in direction of axis Y
     */
    public void setCellSizeY(double cell_size_Y)
    {
        cellSizeY = cell_size_Y;
    }
    
    /** The method returns cell3D size in direction of axis Z.
     * @return cell3D size in direction of axis Z
     */
    public double getCellSizeZ()
    {
        return cellSizeZ;
    }
    
    /** The method sets cell3D size in direction of axis Z.
     * @param cell_size_Z cell3D size in direction of axis Z
     */
    public void setCellSizeZ(double cell_size_Z)
    {
        cellSizeZ = cell_size_Z;
    }

    /** The method returns three Euler angles.
     * @return three Euler angles
     */
    public double[] getEulerAngles()
    {
        return eulerAngles;
    }
    
    /** The method sets three Euler angles.
     * @param _eulerAngles three Euler angles
     */
    public void setEulerAngles(double[] _eulerAngles)
    {
        eulerAngles = new double[3];

        eulerAngles[0] = _eulerAngles[0];
        eulerAngles[1] = _eulerAngles[1];
        eulerAngles[2] = _eulerAngles[2];
    }
    
    /** The method returns name of file with information about material 
     * @return material_file name of file with information about material
     */
    public String getMaterialFile()
    {
        return material_file;
    }
    
    /** The method sets name of file with information about material 
     * @param _material_file name of file with information about material
     */
    public void setMaterialFile(String _material_file)
    {
        material_file = new String(_material_file);
    }
        
    /** The method sets material of cell
     * @param _material material material of cell
     */
    public void setMaterial(Material _material)
    {
        //Material material = new Material(_material);
        
        // Loading of parameters of cell3D material
        mod_elast               = _material.get_mod_elast();
        density                 = _material.get_density();
        heatExpansionCoeff      = _material.get_heatExpCoeff();
        yieldStrain             = _material.get_yieldStrain();
        ultimateStrain          = _material.getUltimateStrain();
        energy_coeff            = _material.get_energy_coeff();
        specific_heat_capacity  = _material.get_specific_heat_capacity();
        thermal_conductivity    = _material.get_thermal_conductivity();
        thermal_conduct_bound   = _material.get_thermal_conduct_bound();
        phonon_portion          = _material.get_phonon_portion();
        actEnergy               = _material.get_activation_energy();
        angleLimitHAGB          = _material.getAngleLimitHAGB();
        energyHAGB              = _material.getEnergyHAGB();
        maxMobility             = _material.getMaxMobility();
        dislMaxMobility         = _material.getDislMaxMobility();
        mechMaxMobility         = _material.getMechMaxMobility();
        mod_shear               = _material.getModShear();
        
        disl_distr_coeff        = _material.getDislDistrCoeff();
        yield_state_coeff       = _material.getYieldStateCoeff();
        ultimate_state_coeff    = _material.getUltimateStateCoeff();
        molar_mass              = _material.getMolarMass();
        
        // the coefficient for calculation of cell torsion energy change
        torsion_energy_coeff           = _material.getTorsionEnergyCoeff();
        
        // Coefficient for calculation of local torsion energy at grain boundary
        torsion_energy_coeff_gr_bound  = _material.getTorsionEnergyCoeffForGrBound();
    
        // Coefficient for calculation of local torsion energy in a region near grain boundary
        torsion_energy_coeff_gb_region = _material.getTorsionEnergyCoeffForGrBoundRegion();
        
        // current coefficient for calculation of cell torsion energy change
        current_torsion_energy_coeff   = _material.getTorsionEnergyCoeff();
        
        // Low temperature threshold value for recrystallization
        lowTemperThreshold      = _material.getLowTemperThreshold();
        
        // High temperature threshold value for recrystallization
        highTemperThreshold     = _material.getHighTemperThreshold();
   //     System.out.println("material_file = "+material_file+"; current_torsion_energy_coeff = "+current_torsion_energy_coeff);
   
        min_twin_temperature    = _material.getMinTwinTemperature();
        twinning_temperature    = _material.getTwinningTemperature();
   
        lattice_parameter       = _material.getLatticeParameter();
   
        // Lengths of 3 vectors of lattice anisotropy
        lattice_vector_A_length = _material.getLatticeVector_A_Length();
        lattice_vector_B_length = _material.getLatticeVector_B_Length();
        lattice_vector_C_length = _material.getLatticeVector_C_Length();
    
        // Angles between vectors of lattice anisotropy
        lattice_angle_vecA_vecB = _material.getLatticeAngle_vecA_vecB();
        lattice_angle_vecB_vecC = _material.getLatticeAngle_vecB_vecC();
        lattice_angle_vecC_vecA = _material.getLatticeAngle_vecC_vecA();
    
        // Coefficient of lattice anisotropy
        lattice_anis_coeff      = _material.getLatticeAnisCoeff();
        
        part_vol_fraction       = 2*Math.random()*_material.getParticleVolumeFraction();
        
      //  calcAtomNumber();
    }
    
    /** The method calculates the number of atoms in a cell.
     */
    public void calcAtomNumber()
    {
        atom_number = (long)Math.round(density*volume*Common.AVOGADRO_NUMBER/molar_mass);
    }
        
    /** The method returns strain of cell.
     * @return strain of cell
     */
    public double getStrain()
    {
        return strain;
    }

    /** The method sets strain of cell.
     * @param _strain strain of cell
     */
    public void setStrain(double _strain)
    {
    //    double old_strain = strain;
        strain = _strain;

    //    stress+= (strain - old_strain)*mod_elast;
      //  mech_energy+= (strain-old_strain)*mod_elast*getVolume();
    }
    
    /** The method returns heat strain of cell.
     * @return heat strain of cell
     */
    public double getHeatStrain()
    {
        return heat_strain;
    }

    /** The method sets heat strain of cell.
     * @param _strain heat strain of cell
     */
    public void setHeatStrain(double _strain)
    {
    //    double old_strain = strain;
        heat_strain = _strain;

    //    stress+= (strain - old_strain)*mod_elast;
      //  mech_energy+= (strain-old_strain)*mod_elast*getVolume();
    }

    /** The method changes strain of cell.
     * @param _strain additional strain of cell
     */
    public void addStrain(double _strain)
    {
        strain+= _strain;
    }


    /** The method sets new mechanical stress of cell
     * and calculates new values of strain and elastic energy of cell.
     * @param _stress new mechanical stress of cell
     */
    public void setMechStress(double _stress)
    {
   //     double old_stress = stress;
        stress = _stress;

   //     strain+= (stress-old_stress)/mod_elast;
    //    mech_energy+= (stress-old_stress)*getVolume();
    }
    
    /** Determination of new cell state depending on cell strain
     * @param crack_prob probability of microcrack formation
     */
    public void determineState(double crack_prob)
    {
        byte new_state = Common.ELASTIC_CELL;
        boolean state_is_determined = false;
        
        if(!state_is_determined)
        if(state == Common.ELASTIC_CELL)
        {
            if(strain>yieldStrain)
            {
                new_state = Common.PLASTIC_CELL;
            }
            else
            {
                new_state = state;
            }
        }
        
        if(!state_is_determined)
        if(state == Common.ELASTIC_CELL | state == Common.PLASTIC_CELL)
        {
           // if(strain > ultimateStrain)
            if(false)
            {
                if(Math.random() < crack_prob)
                    new_state = Common.DAMAGED_CELL;
                else
                    new_state = state;
            }
            else
                new_state = state;
            
            state_is_determined = true;
        }
        
        if(false)
        if(!state_is_determined)
        if(state == Common.DAMAGED_CELL)
            new_state = Common.DAMAGED_CELL;
        
        state = new_state;
    }
    
    /** Determination of new cell state depending on cell stress and
     * ratio of torsion energy to elastic energy
     * @param _threshold_stress threshold value for cell switching
     * @param _tors_energy_ratio ratio of torsion energy to elastic energy
     */
    public void determineState(double _threshold_stress, double _tors_energy_ratio)
    {
        // New state of cell
        byte new_state = state;
        
        // Threshold value of energy transitted from torsion energy to mechanical energy
        // when torsion energy becomes larger than this threshold energy
        double threshold_energy_1 = (torsion_energy + mech_energy)*_tors_energy_ratio;
      //  double threshold_energy_2 = threshold_energy_1;
        
        double threshold_energy_2 = 0.5*(torsion_energy + mech_energy)*_tors_energy_ratio;
        
        if(state == Common.ELASTIC_CELL)
        {
            if(stress >= _threshold_stress & torsion_energy >= threshold_energy_1)
            {
                new_state = Common.PLASTIC_CELL;
                current_torsion_energy_coeff = 2*torsion_energy_coeff;
                
                //1.
           //     torsion_energy = 0;
             //   mech_energy   += torsion_energy;
               // stress         = mech_energy/getVolume();
                
                //2.
                torsion_energy -= threshold_energy_1;
            }
            else
            {
                new_state = Common.ELASTIC_CELL;
                current_torsion_energy_coeff = torsion_energy_coeff;
            }
        }
        
        if(state == Common.PLASTIC_CELL)
        {
            if(stress <= _threshold_stress & torsion_energy >= threshold_energy_2)
            {
                new_state = Common.ELASTIC_CELL;
                current_torsion_energy_coeff = torsion_energy_coeff;
                
                //1.
             //   torsion_energy = 0;
               // mech_energy   += torsion_energy;
              //  stress         = mech_energy/getVolume();
                
                //2.
                torsion_energy -= threshold_energy_2;
            }
            else
            {                
                new_state = Common.PLASTIC_CELL;
                current_torsion_energy_coeff = 2*torsion_energy_coeff;
            }
        }
        
        state = new_state;
    }

    /** The method returns coefficient determining switch of cell to plastic state.
     * @return coefficient determining switch of cell to plastic state
     */
    public double getYieldStateCoeff()
    {
        return yield_state_coeff;
    }

    /** The method returns coefficient determining switch of cell to damaged state.
     * @return coefficient determining switch of cell to plastic state
     */
    public double getUltimateStateCoeff()
    {
        return ultimate_state_coeff;
    }


    /** The method sets coefficient determining switch of cell to plastic state.
     * @param _yield_state_coeff coefficient determining switch of cell to plastic state
     */
    public void setYieldStateCoeff(double _yield_state_coeff)
    {
        yield_state_coeff = _yield_state_coeff;
    }

    /** The method sets coefficient determining switch of cell to damaged state.
     * @param _ultimate_state_coeff coefficient determining switch of cell to damaged state
     */
    public  void setUltimateStateCoeff(double _ultimate_state_coeff)
    {
        ultimate_state_coeff = _ultimate_state_coeff;
    }

    /** Determination of new cell state depending on cell strain
     */
    public void determineState()
    {
        // Strain of cell
     //   strain = stress/get_mod_elast();

        // New state of cell
        byte new_state = state;

        // Cell switch probability
        double prob_switch = 0;

        // Determination of new cell state according to threshold values of strain
        // (for cell in elastic state)
        if(state == Common.ELASTIC_CELL)
        {
            if(strain>=yieldStrain)// & strain<ultimateStrain)
            {
                prob_switch = calcProbSwitch(yield_state_coeff, yieldStrain);//1;//
                
                if(Math.random() < prob_switch)
                    new_state = Common.PLASTIC_CELL;
            }

            if(false)
            if(strain>=ultimateStrain)
            {
                prob_switch = calcProbSwitch(ultimate_state_coeff, ultimateStrain);//1;//

                if(Math.random() < prob_switch)
                    new_state = Common.DAMAGED_CELL;
            }
        }

        // Determination of new cell state according to threshold value of strain
        // (for cell in plastic state)
        if(false)
        if(state == Common.PLASTIC_CELL)
        {
            if(strain>=ultimateStrain)
            {
                prob_switch = calcProbSwitch(ultimate_state_coeff, ultimateStrain);//1;//

                if(Math.random() < prob_switch)
                    new_state = Common.DAMAGED_CELL;
            }
        }

        // TEST
        if(false)
        if(state == Common.PLASTIC_CELL)
        if(new_state == Common.ELASTIC_CELL)
        {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("State was plastic and became elastic!!!");
            System.out.println("material: "+getMaterialFile());
            System.out.println("probability: "+prob_switch);
            System.out.println("strain: "+strain);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        // END OF TEST

        state = new_state;
    }

    /** The method calculates probability of cell switch to plastic state.
     * @param switch_coeff coefficient determining switch of cell to plastic or damaged state
     * @param threshold_strain threshold strain of cell (for switch to plastic or damaged state)
     * @return probability of cell switch to plastic state
     */
    public double calcProbSwitch(double switch_coeff, double threshold_strain)
    {
        return Math.exp(-switch_coeff*threshold_strain/strain);
    }
       
    /** The method returns mechanical stress in cell.
     * @return mechanical stress in cell
     */
    public double getMechStress()
    {
        return stress;
    }
    
    /** The method returns mechanical stress in cell at previous time step.
     * @return mechanical stress in cell at previous time step
     */
    public double getPrevMechStress()
    {
        return old_stress;
    }
    
    /** The method changes state of cell and sets new parameters of cell material:
     * temperature, thermal and mechanical parts of energy.
     */
    public void changeState(double[] prob_switch)
    {
        // Determination of cell state.
        double rand = Math.random();
        
        byte oldState = state;
        
        if (rand < prob_switch[0])
        {
            setState((byte)0);
        }
        
        if((rand >= prob_switch[0])&(rand < prob_switch[0]+prob_switch[1]))
        {
            setState((byte)1);
        }  
        
        if((rand >= prob_switch[0]+prob_switch[1])&(rand < 1-prob_switch[3]))
        {
            setState((byte)2);
        }
        
        if(rand >= 1-prob_switch[3])
        {
            setState((byte)3);
        }
        
        //  System.out.println(". New cell state= "+state);
        //  System.out.println("material_file = "+material_file);        
        if(state != oldState)
        {         
            //TEST  
      /*
            System.out.println();
            System.out.print  ("        actEnergy = "+actEnergy);
       */
            //END OF TEST
            // Change of material
            Material material = new Material(material_file);
        
            // Calculation of change of activation energy after cell switching
            double newActEnergy = material.get_activation_energy();
            double energyDifference = newActEnergy - actEnergy;
      /*
            System.out.print  ("  newActEnergy = "+newActEnergy);
            System.out.println("  energyDifference = "+energyDifference);
            System.out.print  ("        mech_energy = "+mech_energy);
       */
            //  Calculation of new parameters of cell
            if(energyDifference > 0)
            {
                mech_energy = mech_energy - energy_coeff*energyDifference;
                thermal_energy = thermal_energy - (1-energy_coeff)*energyDifference;
            }
            else        
            {
                mech_energy = mech_energy + energy_coeff*energyDifference;
                thermal_energy = thermal_energy + (1-energy_coeff)*energyDifference;
            }
     /*
            System.out.print  ("  energy_coeff = "+material.get_energy_coeff());
            System.out.print  ("  new mech_energy = "+mech_energy);
      */
        }        
    }
    
    /** The method returns strain of cell.
     * @return strain of cell
     */
    public double calcStrainT()
    {
        double old_heat_strain = heat_strain;
        
        heat_strain_change = heatExpansionCoeff*(temperature - initialTemperature);
        
       // if(temperature > initialTemperature)
        heat_strain += heat_strain_change;
        strain      += heat_strain_change;
        
        Three _indices = new Three(1, 1, 1);
        
        if(false)
        if(indices.equals(_indices))
        {
            System.out.println();
            System.out.println("initialTemperature = "+initialTemperature);
            System.out.println("temperature        = "+temperature);
        }

        double old_mech_energy = getMechEnergy();
        double new_mech_energy = 0;
        double old_strain = strain;
        boolean change_mod_elast = true;// false;// 

      //  if(heat_strain > old_heat_strain)
        {
            // Total strain is increased due to heat expansion.
         //   strain += heat_strain_change;
            
            // Young modulus is changed under heat expansion.
      //      if(change_mod_elast & strain != 0 & old_strain != 0)
      //          mod_elast = mod_elast*(old_strain/strain);
            
            // The value of elastic energy is changed under heat expansion.
        //    calcElasticEnergy(heat_strain_change);
        //    calcThermalEnergy();
        
            double old_thermal_energy = thermal_energy;
            double tempr_change;
        
            if(false)
                tempr_change = (old_mech_energy - mech_energy)/(get_density()*getVolume()*get_specific_heat_capacity());
        
            if(false)
       //     if(tempr_change < 0)
            if(Math.abs(tempr_change) > Math.abs(temperature - initialTemperature))
            {   
                // System.out.println("ERROR!!! Heat expansion is too large! initialTemperature = "+initialTemperature+"; temperature = "+temperature);
            
                tempr_change   = (old_thermal_energy - thermal_energy)/(get_density()*getVolume()*get_specific_heat_capacity());
                temperature    = temperature + tempr_change;
                thermal_energy = old_thermal_energy;   
            
                heat_strain    = old_heat_strain - heatExpansionCoeff*tempr_change;
                strain         = old_strain;
                mech_energy    = old_mech_energy; 
                calcElasticEnergy(heat_strain - old_heat_strain);
            }
            
        //    if(tempr_change < 0)
            if(false)
            if(Math.abs(tempr_change) <= Math.abs(temperature - initialTemperature))
            {
                temperature  = temperature + tempr_change;
                calcThermalEnergy();
            }
        
            if(false)
            if(indices.equals(_indices))
            {
                System.out.println("temperature change = "+tempr_change);
                System.out.println("new temperature    = "+temperature);
                System.out.println("old_heat_strain    = "+old_heat_strain);
                System.out.println("heat_strain        = "+heat_strain);
                System.out.println("total strain       = "+strain);
                System.out.println();
                
                System.out.print("old_thermal_energy = "+old_thermal_energy);
                System.out.print("; thermal_energy = "+thermal_energy);
                System.out.println("; thermal energy change = "+(thermal_energy-old_thermal_energy));
                System.out.print("oldMechEnergy = "+old_mech_energy);
                System.out.print("; mech_energy = "+mech_energy);
                System.out.println("; mech. energy change = "+(mech_energy-old_mech_energy));
                System.out.println();
            }
            
            initialTemperature = temperature;
        }
        
        return heat_strain - old_heat_strain;
    }

    /** The method calculates mechanical stress in cell.
     */
    public void calcMechStressAndStrain()
    {
        old_stress = stress;
     /* 
        stress = Math.sqrt(2*Math.abs(mech_energy)*mod_elast/getVolume());
        
        if(mech_energy<0)
            stress = -stress;
      */
      /*
        stress = mech_energy/getVolume();

        if(stress != oldStress)
            strain+= (stress - oldStress)/mod_elast;
       */
        
        stress = mech_energy/getVolume();
        strain += (stress - old_stress)/mod_elast;
        
    //    Three cell_indices = getIndices();

    //    if(mech_energy!=0)
      //      System.out.println("Cell with indices ("+cell_indices.getI()+", "+cell_indices.getJ()+", "+cell_indices.getK()+") has stress "+stress+" Pa.");
    }

    /** The method calculates elastic energy of cell.
     * @param strain strain of cell
     */
    public void calcElasticEnergy(double strain_change)
    {
        // Portion of mechanical energy change spent on heating
        double heat_portion = 0;// 0.3;// 
        
        old_stress = stress;
        
        boolean change_mod_elast = false;// true;// 
        
        // Young modulus is changed under heat expansion.
        if(change_mod_elast & strain_change != 0 & (strain_change + heat_strain_change) != 0)
           mod_elast = mod_elast*strain_change/(strain_change + heat_strain_change);
        
        // Stress change
        double stress_change = (1 - heat_portion)*(strain_change + heat_strain_change)*mod_elast;
        
        // Stress change
        stress+= stress_change;
        
        // Mechanical energy change
        mech_energy_change = stress_change*getVolume();
        
     //   mech_energy+= Math.abs(strain_change)*strain_change*mod_elast*getVolume()/2;
     //   mech_energy+= strain_change*mod_elast*getVolume();
     //   mech_energy = stress*getVolume();
        mech_energy += mech_energy_change;
        
        // dissipation of energy under torsion
    //    mech_energy = mech_energy - torsion_energy;
    
        if(heat_portion > 0)
        {
            thermal_energy += heat_portion*Math.abs(mech_energy_change);
            temperature    += heat_portion*Math.abs(mech_energy_change)/(specific_heat_capacity*density*volume);
        } 
    }
    
    /** The method calculates influx of thermal energy to cell.
     */
    public void calcThermalEnergy()
    {
        // thermal_energy = specific_heat_capacity*(temperature-initialTemperature)*density*volume;
        
        thermal_energy = specific_heat_capacity*temperature*density*volume;
    }
    
    /** The method calculates temperature of cell.
     * @return temperature of cell
     */
    public void calcTemperature()
    {
     //   temperature = initialTemperature + thermal_energy/(specific_heat_capacity*density*volume);
        
        temperature = thermal_energy/(specific_heat_capacity*density*volume);
    }
    
    /** The method changes state of cell and sets new parameters of cell material:
     * temperature, thermal and mechanical parts of energy.
     */
    public void changeState(double prob_switch)
    {
        // Determination of cell state.
        double rand = Math.random();
        
        byte oldState = state;
        
        if (rand < prob_switch)
        {
            setState((byte)1);
        }
        
        //  System.out.println(". New cell state= "+state);
        //  System.out.println("material_file = "+material_file);        
        if(state != oldState)
        {
            //TEST  
      /*
            System.out.println();
            System.out.print  ("        actEnergy = "+actEnergy);
       */
            //END OF TEST
            // Change of material
            Material material = new Material(material_file);
        
            // Calculation of change of activation energy after cell switching
            double newActEnergy = material.get_activation_energy();
            double energyDifference = newActEnergy - actEnergy;
      /*
            System.out.print  ("  newActEnergy = "+newActEnergy);
            System.out.println("  energyDifference = "+energyDifference);
            System.out.print  ("        mech_energy = "+mech_energy);
       */
            //  Calculation of new parameters of cell
            if(energyDifference > 0)
            {
                mech_energy-= energy_coeff*energyDifference;
                thermal_energy-= (1-energy_coeff)*energyDifference;
            }
            else        
            {
                mech_energy+= energy_coeff*energyDifference;
                thermal_energy+= (1-energy_coeff)*energyDifference;
            }
     /*
            System.out.print  ("  energy_coeff = "+material.get_energy_coeff());
            System.out.print  ("  new mech_energy = "+mech_energy);
      */
        }        
    }
    
    /** The method returns modulus of elasticity.
     * @return modulus of elasticity
     */
    public double get_mod_elast()
    {
        return mod_elast;
    }
    
    /** The method returns density of material.
     * @return density of material
     */
    public double get_density()
    {
        return density;
    }
    
    /** The method returns coefficient of heat expansion of material.
     * @return coefficient of heat expansion of material
     */
    public double get_heatExpCoeff()
    {
        return heatExpansionCoeff;
    }
    
    /** The method returns yield strain of material.
     * @return yield strain of material
     */
    public double get_yieldStrain()
    {
        return yieldStrain;
    }
    
    /** The method returns ultimate strain of material.
     * @return ultimate strain of material
     */
    public double getUltimateStrain()
    {
        return ultimateStrain;
    }    
    
    /** The method returns coefficient of thermal expansion.
     * @return coefficient of thermal expansion
     */
    public double get_energy_coeff()
    {
        return energy_coeff;
    }
    
    /** The method returns specific heat capacity.
     * @return specific heat capacity
     */
    public double get_specific_heat_capacity()
    {
        return specific_heat_capacity;
    }

    /** The method returns thermal conductivity.
     * @return thermal conductivity
     */
    public double get_thermal_conductivity()
    {
        return thermal_conductivity;
    }

    /** The method returns thermal conductivity on grain boundary.
     * @return thermal conductivity on grain boundary
     */
    public double get_thermal_conduct_bound()
    {
        return thermal_conduct_bound;
    }
    
    /** The method returns portion of phonon thermal conductivity
     * in total thermal conductivity.
     * @return portion of phonon thermal conductivity in total thermal conductivity
     */
    public double get_phonon_portion()
    {
        return phonon_portion;
    }
    
    /** The method returns activation energy.
     * @return activation energy
     */
    public double get_activation_energy()
    {
        return actEnergy;
    }
    
    /** The method returns transition limit to high angle grain boundary regime.
     * @return transition limit to high angle grain boundary regime
     */
    public double getAngleLimitHAGB()
    {
        return angleLimitHAGB;
    }
    
    /** The method returns specific energy of high angle grain boundary.
     * @return specific energy of high angle grain boundary 
     */
    public double getEnergyHAGB()
    {
        return energyHAGB;
    }
    
    /** The method sets specific energy of high angle grain boundary.
     * @param _energyHAGB specific energy of high angle grain boundary 
     */
    public void setEnergyHAGB(double _energyHAGB)
    {
        energyHAGB = _energyHAGB;
    }
    
    /** The method returns maximal value of boundary mobility
     * under heat recrystallization.
     * @return maximal value of boundary mobility 
     *         under heat recrystallization
     */
    public double getMaxMobility()
    {
        return maxMobility;
    }
    
    /** The method returns maximal value of boundary mobility 
     * under cold recrystallization.
     * @return maximal value of boundary mobility 
     *         under cold recrystallization
     */
    public double getDislMaxMobility()
    {
        return dislMaxMobility;
    }
    
    /** The method returns maximal value of boundary mobility 
     * under mechanical loading.
     * @return maximal value of boundary mobility 
     *         under mechanical loading
     */
    public double getMechMaxMobility()
    {
        return mechMaxMobility;
    }
    
    /** The method returns volume fraction of foreign particles.
     * @return volume fraction of foreign particles
     */
    public double get_partVolumeFraction()
    {
        return part_volume_fraction;
    }
    
    /** The method returns radius of foreign particle
     * @return radius of foreign particle
     */
    public double get_particleRadius()
    {
        return particle_radius;
    }

    /** The method returns index of grain containing cell.
     * @return index of grain containing cell
     */
    public int getGrainIndex()
    {
        return grainIndex;
    }
            
    /** The method returns type of cell.
     * @return type of cell
     */
    public byte getType()
    {
        return type;
    }
    
    /** The method sets type of cell.
     * @param _type type of cell
     */
    public void setType(byte _type)
    {
        type = _type;
        
        if(type == Common.INTERGRANULAR_CELL | type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
        {
            torsion_energy_coeff         = torsion_energy_coeff_gr_bound;
            current_torsion_energy_coeff = torsion_energy_coeff_gr_bound;
        }
        
        if(type == Common.GRAIN_BOUNDARY_REGION_CELL | type == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL)
        {
            torsion_energy_coeff         = torsion_energy_coeff_gb_region;
            current_torsion_energy_coeff = torsion_energy_coeff_gb_region;
        }
    }

    /** The method returns type of grain of cell.
     * @return type of cell
     */
    public byte getGrainType()
    {
        return grain_type;
    }

    /** The method sets type of grain of cell.
     * @param _type type of cell
     */
    public void setGrainType(byte _grain_type)
    {
        grain_type = _grain_type;
    }

    /** The method returns density of dislocations of cell.
     * @return density of dislocations of cell
     */
    public double getDislDensity()
    {
        return disl_density;
    }

    /** The method sets density of dislocations of cell.
     * @param _disl_density density of dislocations of cell
     */
    public void setDislDensity(double _disl_density)
    {
        disl_density = _disl_density;
    }
    
    /** The method calculates the number of defects in a cell.
     * @param torsion_angle_change change of angle of torsion of material in a cell
     */
    public void calcDefectNumber()
    { 
        // Calculation of initial number of defects
        defect_number         = (long)Math.round(volume*disl_density/lattice_parameter);
        
        // The number of defects cannot be larger then the total number of atoms in a cell
        defect_number        = Math.min(atom_number, defect_number);
        
        // The total number of atoms is decreased due to presence of defects
        atom_number   -= defect_number;
    }
    
    /** The method returns type of cell location on grain boundary:
     * true - if cell is located on grain boundary, 
     * false - if cell is not located on grain boundary
     * @return type of cell location on grain boundary:
     * true - if cell is located on grain boundary, 
     * false - if cell is not located on grain boundary
     */
    public boolean getLocationOnBoundary()
    {
        return locationOnBoundary;
    }

    /** The method returns type of cell location on specimen boundary:
     * true - if cell is located on specimen boundary,
     * false - if cell is not located on specimen boundary
     * @return type of cell location on specimen boundary:
     * true - if cell is located on specimen boundary,
     * false - if cell is not located on specimen boundary
     */
    public boolean getLocationOnSpecBoundary()
    {
        return locationOnSpecimenBoundary;
    }
    
    /** The method returns shear modulus of material.
     * @return shear modulus of material
     */
    public double getModShear()
    {
        return mod_shear;
    }
    
    /** The method returns lattice parameter of material.
     * @return lattice parameter of material
     */
    public double getLatticeParameter()
    {
        return lattice_parameter;
    }
    
    /** The method returns length of vector A of lattice anisotropy.
     * @return length of vector A of lattice anisotropy
     */
    public double getLatticeVector_A_Length()
    {
        return lattice_vector_A_length;
    }
    
    /** The method returns length of vector B of lattice anisotropy.
     * @return length of vector B of lattice anisotropy
     */
    public double getLatticeVector_B_Length()
    {
        return lattice_vector_B_length;
    }
    
    /** The method returns length of vector C of lattice anisotropy.
     * @return length of vector C of lattice anisotropy
     */
    public double getLatticeVector_C_Length()
    {
        return lattice_vector_C_length;
    }
    
    /** The method returns angle between lattice anisotropy vectors A and B.
     * @return angle between lattice anisotropy vectors A and B
     */
    public double getLatticeAngle_vecA_vecB()
    {
        return lattice_angle_vecA_vecB;
    }
    
    /** The method returns angle between lattice anisotropy vectors B and C.
     * @return angle between lattice anisotropy vectors B and C
     */
    public double getLatticeAngle_vecB_vecC()
    {
        return lattice_angle_vecB_vecC;
    }
    
    /** The method returns angle between lattice anisotropy vectors C and A.
     * @return angle between lattice anisotropy vectors C and A
     */
    public double getLatticeAngle_vecC_vecA()
    {
        return lattice_angle_vecC_vecA;
    }
    
    /** The method returns coefficient of lattice anisotropy.
     * @return coefficient of lattice anisotropy
     */
    public double getLatticeAnisCoeff()
    {
        return lattice_anis_coeff;
    }
    
    /** The method returns volume portion of particles.
     * @return volume portion of particles
     */
    public double getParticleVolumeFraction()
    {
      return part_vol_fraction;
    }
    
    /** The method sets volume portion of particles.
     * @param _part_vol_fraction volume portion of particles
     */
    public void setParticleVolumeFraction(double _part_vol_fraction)
    {
      part_vol_fraction = _part_vol_fraction;
    }
    
    /** The method returns coefficient concerning character 
     * of distribution of dislocations in material.
     * @return coefficient concerning character 
     *         of distribution of dislocations in material
     */
    public double getDislDistrCoeff()
    {
        return disl_distr_coeff;
    }
    
    /** The method sets maximal value of boundary mobility
     * under heat recrystallization.
     * @param _maxMobility maximal value of boundary mobility 
     *                     under heat recrystallization
     */
    public void setMaxMobility(double _maxMobility)
    {
        maxMobility = _maxMobility;
    }   
    
    /** The method sets maximal value of boundary mobility
     * under cold recrystallization.
     * @param _maxMobility maximal value of boundary mobility 
     *                     under cold recrystallization
     */
    public void setDislMaxMobility(double _dislMaxMobility)
    {
        dislMaxMobility = _dislMaxMobility;
    }
    
    /** The method sets maximal value of boundary mobility
     * under mechanical loading.
     * @param _mechMaxMobility maximal value of boundary mobility 
     *                         under mechanical loading
     */
    public void setMechMaxMobility(double _mechMaxMobility)
    {
        mechMaxMobility = _mechMaxMobility;
    }
    
    /** The method sets density of material.
     * @param _density density of material
     */
    public void set_density(double _density)
    {
        density = _density;
    }
    
    /** The method sets coefficient of heat expansion of material.
     * @param _heatExpCoeff coefficient of heat expansion of material
     */
    public void set_heatExpCoeff(double _heatExpCoeff)
    {
        heatExpansionCoeff = _heatExpCoeff;
    }
    
    /** The method sets yield strain of material.
     * @param _yieldStrain yield strain of material
     */
    public void set_yieldStrain(double _yieldStrain)
    {
        yieldStrain = _yieldStrain;
    }
    
    /** The method sets specific heat capacity.
     * @param _specific_heat_capacity _specific_heat_capacity specific heat capacity
     */
    public void set_specific_heat_capacity(double _specific_heat_capacity)
    {
        specific_heat_capacity = _specific_heat_capacity;
    }

    /** The method sets thermal conductivity.
     * @param _thermal_conductivity thermal conductivity
     */
    public void set_thermal_conductivity(double _thermal_conductivity)
    {
        thermal_conductivity = _thermal_conductivity;
    }
    
    /** The method sets thermal conductivity on grain boundary.
     * @param _thermal_conduct_bound thermal conductivity on grain boundary
     */
    public void set_thermal_conduct_bound(double _thermal_conduct_bound)
    {
        thermal_conduct_bound = _thermal_conduct_bound;
    }
    
    /** The method sets portion of phonon thermal conductivity
     * in total thermal conductivity.
     * @param _phonon_portion portion of phonon thermal conductivity in total thermal conductivity
     */
    public void set_phonon_portion(double _phonon_portion)
    {
        phonon_portion = _phonon_portion;
    }
    
    /** The method sets index of grain containing cell.
     * @param _grainIndex index of grain containing cell
     */
    public void setGrainIndex(int _grainIndex)
    {
        grainIndex = _grainIndex;
    }
    
    /** The method sets type of cell location on grain boundary:
     * true - if cell is located on grain boundary, 
     * false - if cell is not located on grain boundary
     * @param _locationOnBoundary type of cell location on grain boundary
     */
    public void setLocationOnBoundary(boolean _locationOnBoundary)
    {
        locationOnBoundary = _locationOnBoundary;
    }

    /** The method sets type of cell location on specimen boundary:
     * true - if cell is located on specimen boundary,
     * false - if cell is not located on specimen boundary
     * @param _locationOnBoundary type of cell location on specimen boundary
     */
    public void setLocationOnSpecBoundary(boolean _locationOnSpecimenBoundary)
    {
        locationOnSpecimenBoundary = _locationOnSpecimenBoundary;
    }

    /** The method sets shear modulus of material.
     * @param _mod_shear shear modulus of material
     */
    public void setModShear(double _mod_shear)
    {
        mod_shear = _mod_shear;
    }
    
    /** The method sets lattice parameter of material.
     * @param _lattice_parameter lattice parameter of material
     */
    public void setLatticeParameter(double _lattice_parameter)
    {
        lattice_parameter = _lattice_parameter;
    }    
    
    /** The method sets length of vector A of lattice anisotropy.
     * @param value length of vector A of lattice anisotropy
     */
    public void setLatticeVector_A_Length(double value)
    {
        lattice_vector_A_length = value;
    }
    
    /** The method sets length of vector B of lattice anisotropy.
     * @param value length of vector B of lattice anisotropy
     */
    public void setLatticeVector_B_Length(double value)
    {
        lattice_vector_B_length = value;
    }
    
    /** The method sets length of vector C of lattice anisotropy.
     * @param value length of vector C of lattice anisotropy
     */
    public void setLatticeVector_C_Length(double value)
    {
        lattice_vector_C_length = value;
    }
    
    /** The method sets angle between lattice anisotropy vectors A and B.
     * @param value angle between lattice anisotropy vectors A and B
     */
    public void setLatticeAngle_vecA_vecB(double value)
    {
        lattice_angle_vecA_vecB = value;
    }
    
    /** The method sets angle between lattice anisotropy vectors B and C.
     * @param value angle between lattice anisotropy vectors B and C
     */
    public void setLatticeAngle_vecB_vecC(double value)
    {
        lattice_angle_vecB_vecC = value;
    }
    
    /** The method sets angle between lattice anisotropy vectors C and A.
     * @param value angle between lattice anisotropy vectors C and A
     */
    public void setLatticeAngle_vecC_vecA(double value)
    {
        lattice_angle_vecC_vecA = value;
    }
    
    /** The method sets coefficient of lattice anisotropy.
     * @param value coefficient of lattice anisotropy
     */
    public void setLatticeAnisCoeff(double value)
    {
        lattice_anis_coeff = value;
    }
    
    /** The method sets temperature of start of grain twinning.
     * @param value temperature of start of grain twinning
     */
    public void setMinTwinTemperature(double value)
    {
        min_twin_temperature = value;
    }
    
    /** The method sets temperature of finish of grain twinning.
     * @param value temperature of finish of grain twinning
     */
    public void setTwinningTemperature(double value)
    {
        twinning_temperature = value;
    }
    
    /** The method sets coefficient concerning character 
     * of distribution of dislocations in material.
     * @param _disl_distr_coeff coefficient concerning character 
     *         of distribution of dislocations in material
     */
    public void setDislDistrCoeff(double _disl_distr_coeff)
    {
        disl_distr_coeff = _disl_distr_coeff;
    }

    /** The method sets displacement vector of the cell.
     * @param _displ_vect
     */
    public void setDisplVector(VectorR3 _displ_vect)
    {
        displ_vector = new VectorR3(_displ_vect);
    }

    /** The method returns displacement vector of the cell.
     * @return displacement vector of the cell
     */
    public VectorR3 getDisplVector()
    {
        return displ_vector;
    }

    /** The method returns strain tensor of cell
     * @return strain tensor of cell
     */
    public DoubleMatrix getStrainTensor()
    {
        return strain_tensor;
    }

    /** The method sets strain tensor of cell
     * @param _strain_tensor strain tensor of cell
     */
    public void setStrainTensor(DoubleMatrix _strain_tensor)
    {
        int col_number = _strain_tensor.columns();
        int row_number = _strain_tensor.rows();
        
        strain_tensor = new DoubleMatrix(row_number, col_number);

        for(int row_counter = 0; row_counter<row_number; row_counter++)
        for(int col_counter = 0; col_counter<col_number; col_counter++)
            strain_tensor.setElement(row_counter, col_counter, _strain_tensor.getElement(row_counter, col_counter));
    }

    /** The method returns stress tensor of cell
     * @return stress tensor of cell
     */
    public DoubleMatrix getStressTensor()
    {
        return stress_tensor;
    }

    /** The method sets stress tensor of cell
     * @param _stress_tensor stress tensor of cell
     */
    public void setStressTensor(DoubleMatrix _stress_tensor)
    {
        int col_number = _stress_tensor.columns();
        int row_number = _stress_tensor.rows();

       // stress_tensor = new DoubleMatrix(row_number, col_number);

        for(int row_counter = 0; row_counter<row_number; row_counter++)
        for(int col_counter = 0; col_counter<col_number; col_counter++)
            stress_tensor.setElement(row_counter, col_counter, _stress_tensor.getElement(row_counter, col_counter));
    }
         
    /** The method calculates stress vector of cell according to the values
     * of the stresses at boundaries with neighbour cells.
     * @param _bound_stresses array of stresses at boundaries with neighbour cells
     * @param cell1S_vectors vectors from "cell1S" to "central" cell
     */
    public void calcStressVector(double[] _bound_stresses, VectorR3[] cell1S_vectors)
    {
      double stress_X = 0, stress_Y = 0, stress_Z = 0;
      
      // Number of vectors to neighbours
      int vector_number = cell1S_vectors.length;// 12;//
      
      if(_bound_stresses.length != vector_number)
        System.out.println("Error!!! Number of neighbours is not correct!");
        
      // Calculation of stress vector as a sum of vectors at boundaries with neighbours
      for(int bound_counter = 0; bound_counter < vector_number; bound_counter++)
      {
        cell1S_vectors[bound_counter].normalize();
        
        stress_X += _bound_stresses[bound_counter]*cell1S_vectors[bound_counter].getX();
        stress_Y += _bound_stresses[bound_counter]*cell1S_vectors[bound_counter].getY();
        stress_Z += _bound_stresses[bound_counter]*cell1S_vectors[bound_counter].getZ();
      }
      
      stress_vector.setX(stress_X);
      stress_vector.setY(stress_Y);
      stress_vector.setZ(stress_Z);
    }
    
    /** The method returns stress vector of cell.
     * @return stress vector of cell
     */
    public VectorR3 getStressVector()
    {
        return stress_vector;
    }
    
    /** The method sets stress vector of cell.
     * @param _stress_vector stress vector of cell
     */
    public void setStressVector(VectorR3 _stress_vector)
    {
        stress_vector = new VectorR3(_stress_vector);
    }
    
    /** The method calculates stress tensor of cell according to the values
     * of the stresses at boundaries with neighbour cells.
     * @param _bound_stresses array of stresses at boundaries with neighbour cells
     * @param cell1S_vectors vectors from "cell1S" to "central" cell
     */
    public void calcStressTensor(double[] _bound_stresses, VectorR3[] cell1S_vectors)
    {
        // Number of vectors to neighbours
        int vector_number = cell1S_vectors.length;// 12;//

        if(_bound_stresses.length != vector_number)
            System.out.println("Error!!! Number of neighbours is not correct!");
        
        VectorR3[] stress_vectors = new VectorR3[vector_number];

        // Calculation of vectors at cell boundaries
        for(int bound_counter = 0; bound_counter<vector_number; bound_counter++)
        {
            stress_vectors[bound_counter] = new VectorR3(0, 0, 0);
          //  System.out.println("Vector #"+bound_counter+": "+cell1S_vectors[bound_counter].getX()+" "+cell1S_vectors[bound_counter].getY()+" "+cell1S_vectors[bound_counter].getZ());

            stress_vectors[bound_counter].setX(_bound_stresses[bound_counter]*cell1S_vectors[bound_counter].getX());
            stress_vectors[bound_counter].setY(_bound_stresses[bound_counter]*cell1S_vectors[bound_counter].getY());
            stress_vectors[bound_counter].setZ(_bound_stresses[bound_counter]*cell1S_vectors[bound_counter].getZ());
        }

        // Elements of stress tensor
        double s_xx=0, s_xy=0, s_xz=0;
        double s_yx=0, s_yy=0, s_yz=0;
        double s_zx=0, s_zy=0, s_zz=0;

        // Calculation of stress tensor elements
    //    s_xx = -stress_vectors[ 0].getX()-stress_vectors[ 1].getX()+stress_vectors[ 2].getX()-stress_vectors[ 3].getX()
      //         -stress_vectors[ 4].getX()-stress_vectors[ 5].getX()+stress_vectors[ 6].getX()+stress_vectors[ 7].getX()
        //       +stress_vectors[ 8].getX()-stress_vectors[ 9].getX()+stress_vectors[10].getX()+stress_vectors[11].getX();

        byte sign_X, sign_Y, sign_Z;
        byte sign;

        // Calculation of stress vector at X-plane
        for(int vector_counter = 0; vector_counter<vector_number; vector_counter++)
        {
            sign_X = sign(cell1S_vectors[vector_counter].getX());
            sign_Y = sign(cell1S_vectors[vector_counter].getY());
            sign_Z = sign(cell1S_vectors[vector_counter].getZ());
            
            if(sign_X != 0)     sign=sign_X;
            else
            {
                if(sign_Y != 0) sign=sign_Y;
                else            sign=sign_Z;
            }

            s_xx += sign*stress_vectors[vector_counter].getX();
            s_xy += sign*stress_vectors[vector_counter].getY();
            s_xz += sign*stress_vectors[vector_counter].getZ();
        }

        // Calculation of stress vector at Y-plane
        for(int vector_counter = 0; vector_counter<vector_number; vector_counter++)
        {
            sign_X = sign(cell1S_vectors[vector_counter].getX());
            sign_Y = sign(cell1S_vectors[vector_counter].getY());
            sign_Z = sign(cell1S_vectors[vector_counter].getZ());

            if(sign_Y != 0)     sign=sign_Y;
            else
            {
                if(sign_Z != 0) sign=sign_Z;
                else            sign=sign_X;
            }

            s_yx += sign*stress_vectors[vector_counter].getX();
            s_yy += sign*stress_vectors[vector_counter].getY();
            s_yz += sign*stress_vectors[vector_counter].getZ();
        }

        // Calculation of stress vector at Z-plane
        for(int vector_counter = 0; vector_counter<vector_number; vector_counter++)
        {
            sign_X = sign(cell1S_vectors[vector_counter].getX());
            sign_Y = sign(cell1S_vectors[vector_counter].getY());
            sign_Z = sign(cell1S_vectors[vector_counter].getZ());

            if(sign_Z != 0)     sign=sign_Z;
            else
            {
                if(sign_X != 0) sign=sign_X;
                else            sign=sign_Y;
            }

            s_zx += sign*stress_vectors[vector_counter].getX();
            s_zy += sign*stress_vectors[vector_counter].getY();
            s_zz += sign*stress_vectors[vector_counter].getZ();
        }

        // Elements of vector at plane perpendicular to axis X
        stress_tensor.setElement(0, 0, s_xx);
        stress_tensor.setElement(0, 1, s_xy);
        stress_tensor.setElement(0, 2, s_xz);

        // Elements of vector at plane perpendicular to axis Y
        stress_tensor.setElement(1, 0, s_yx);
        stress_tensor.setElement(1, 1, s_yy);
        stress_tensor.setElement(1, 2, s_yz);

        // Elements of vector at plane perpendicular to axis Z
        stress_tensor.setElement(2, 0, s_zx);
        stress_tensor.setElement(2, 1, s_zy);
        stress_tensor.setElement(2, 2, s_zz);

        // Calculation of average stress
        average_stress = (s_xx + s_yy + s_zz)/3;

        // Calculation of components of force moment
        moment_X = s_zy - s_yz;
        moment_Y = s_xz - s_zx;
        moment_Z = s_yx - s_xy;
    }

    /** The method returns the value of the average stress
     * @return the value of the average stress
     */
    public double getAverageStress()
    {
        return average_stress;
    }

    /** The method returns component X of force moment calculated starting from stress tensor.
     * @return component X of force moment calculated starting from stress tensor
     */
    public double getMomentX()
    {
        return moment_X;
    }

    /** The method returns component Y of force moment calculated starting from stress tensor.
     * @return component Y of force moment calculated starting from stress tensor
     */
    public double getMomentY()
    {
        return moment_Y;
    }

    /** The method returns component Z of force moment calculated starting from stress tensor.
     * @return component Z of force moment calculated starting from stress tensor
     */
    public double getMomentZ()
    {
        return moment_Z;
    }

    /** The method sets ultimate strain of material.
     * @param _ultimateStrain ultimate strain of material
     */
    public void setUltimateStrain(double _ultimateStrain)
    {
        ultimateStrain = _ultimateStrain;
    }
    
    /** The method returns sign of the number analyzed.
     * @param x the number analyzed
     * @return sign of the number analyzed
     */
    public byte sign(double x)
    {
        byte sign = 0;
        if(x>0) sign = 1;
        if(x<0) sign =-1;
        
        return sign;
    }
    
    /** The method returns array of centres of cell boundaries.
     * @return array of centres of cell boundaries
     */
    public PointR3[] getBoundCentres()
    {
        return bound_centres;
    }
    
    /** The method returns array of centres of normal vectors of cell boundaries with neighbours.
     * @return array of centres of normal vectors of cell boundaries with neighbours
     */
    public VectorR3[] getBoundNormVectors()
    {
        return bound_norm_vectors;
    }
    
    /** The method returns array of stress vectors at cell boundaries.
     * @return array of stress vectors at cell boundaries
     */
    public double[] getBoundStresses()
    {
        return bound_stresses;
    }
    
    /** The method returns array of strain velocity vectors at cell boundaries.
     * @return array of stress vectors at cell boundaries
     */
    public double[] getBoundVelocities()
    {
        return bound_velocities;
    }
    
    /** The method sets array of centres of cell boundaries.
     * @param _bound_centres array of centres of cell boundaries
     */
    public void setBoundCentres(PointR3[] _bound_centres)
    {
        for(int bound_counter = 0; bound_counter<_bound_centres.length; bound_counter++)
            bound_centres[bound_counter] = _bound_centres[bound_counter];
    }
    
    /** The method sets array of centres of normal vectors of cell boundaries with neighbours.
     * @param _bound_norm_vectors array of centres of normal vectors of cell boundaries with neighbours
     */
    public void setBoundNormVectors(VectorR3[] _bound_norm_vectors)
    {
        for(int bound_counter = 0; bound_counter<_bound_norm_vectors.length; bound_counter++)
            bound_norm_vectors[bound_counter] = _bound_norm_vectors[bound_counter];
    }
    
    /** The method sets array of stress vectors at cell boundaries.
     * @param _bound_stresses array of stress vectors at cell boundaries
     */
    public void setBoundStresses(double[] _bound_stresses)
    {
        for(int bound_counter = 0; bound_counter<_bound_stresses.length; bound_counter++)
            bound_stresses[bound_counter] = _bound_stresses[bound_counter];
    }
    
    /** The method sets stress at cell facet.
     * @param bound_counter index of facet
     * @param bound_stress stress at cell facet
     */
    public void setBoundStress(int bound_counter, double bound_stress)
    {
        bound_stresses[bound_counter] = bound_stress;
    }
    
    /** The method sets array of strain velocity vectors at cell boundaries.
     * @param _bound_velocities array of stress vectors at cell boundaries
     */
    public void setBoundVelocities(double[] _bound_velocities)
    {
        for(int bound_counter = 0; bound_counter<_bound_velocities.length; bound_counter++)
            bound_velocities[bound_counter] = _bound_velocities[bound_counter];
    }
    
    /** The method sets strain velocity at cell facet.
     * @param bound_counter index of facet
     * @param bound_velocity strain velocity
     */
    public void setBoundVelocity(int bound_counter, double bound_velocity)
    {
        bound_velocities[bound_counter] = bound_velocity;
    }
    
    /** The method calculates and returns the value of torsion energy of cell.
     * @return the value of torsion energy of cell
     */
    public double calcTorsionEnergy()
    {
        // Square of the absolute value of cell force moment 
     //   double sqr_moment_module = moment_X*moment_X + moment_Y*moment_Y + moment_Z*moment_Z;
        
        // The absolute value of cell force moment 
        double moment_module = calcAbsForceMoment();
        
        // The value of torsion energy of cell
        // double tors_energy = 16*sqr_moment_module/(Math.PI*mod_shear*cellSizeX*cellSizeY*cellSizeZ);
        double tors_energy = 16*moment_module*moment_module/(Math.PI*mod_shear*cellSizeX*cellSizeY*cellSizeZ);
        
        return tors_energy;
    }   
    
    /** The method calculates and returns the value of cell torsion energy change.
     * @param torsion_angle_change the value of cell torsion angle change
     */
    public void calcTorsionEnergyChange(VectorR3 torsion_angle_change)
    {
        boolean determineState = false;// true;// 
        
        //   calcNewTorsionEnergyCoeff(old_stress, stress);
        
        // Threshold value of ratio of torsion energy to mechanical energy
        double tors_energy_ratio = 0.03;// 1.0;// 
        
        // Determination of current state of cell
        if(determineState)
            determineState(threshold_stress, tors_energy_ratio);
        else
            state = Common.ELASTIC_CELL;
        
        boolean error = false;
        
        if(state != Common.ELASTIC_CELL & state != Common.PLASTIC_CELL)
        {
            System.out.println("ERROR!!! Cell state is not determined!!! state = "+state);
            error = true;
        }
            
        double abs_mech_energy = Math.abs(mech_energy);
        double abs_mech_energy_change = Math.abs(mech_energy_change);
        
        double new_tors_energy = 0;
        
        // the absolute value of cell torsion angle change
        double torsion_angle_abs_value = torsion_angle_change.getLength();
        
     // double old_mech_energy = mech_energy;
        
        // Case of elastic state
        if(state == Common.ELASTIC_CELL)
          current_torsion_energy_coeff = torsion_energy_coeff;
        
        // Case of plastic state
        if(state == Common.PLASTIC_CELL | error)
          current_torsion_energy_coeff = 2*torsion_energy_coeff;
        
    //  System.out.println("current_torsion_energy_coeff = "+current_torsion_energy_coeff);
        
        if(true) // if(Math.abs(stress) >= Math.abs(old_stress)) // 
        {
          // calculation of the value of cell torsion energy change
          torsion_energy_change = current_torsion_energy_coeff*mod_shear*Math.PI*
                                  torsion_angle_abs_value*torsion_angle_abs_value*volume/16;
            
          if(-torsion_energy_change > torsion_energy)
              torsion_energy_change = -torsion_energy;
            
          // The value of torsion energy change cannot be larger than change of mechanical energy or 
          // total mechanical energy.
          if(torsion_energy_change > Math.min(abs_mech_energy_change, abs_mech_energy))
              torsion_energy_change = Math.min(abs_mech_energy_change, abs_mech_energy);
        }
        else
        {
          // calculation of the value of cell torsion energy change
          torsion_energy_change = - current_torsion_energy_coeff*mod_shear*Math.PI*
                                  torsion_angle_abs_value*torsion_angle_abs_value*volume/16;
          
          if(-torsion_energy_change > torsion_energy)
              torsion_energy_change = -torsion_energy;
          
          // The value of torsion energy change cannot be larger than change of mechanical energy or 
          // total mechanical energy.
          if(-torsion_energy_change > Math.min(abs_mech_energy_change, abs_mech_energy))
              torsion_energy_change = -Math.min(abs_mech_energy_change, abs_mech_energy);
        }
        
      //  System.out.print("Cell with indices ("+getIndices().writeToString()+"): mech.energy = "+mech_energy+" ");
      
        // Variable is responsible for chanfge of mechanical energy due to change of torsion energy of cell
        boolean change_mech_energy = true; // false; // 
        
        if(change_mech_energy)
        {
          // Mechanical energy change and total value of mechanical energy cannot change a sign
          if(mech_energy_change>0)
          {
            mech_energy_change = mech_energy_change - torsion_energy_change;
            mech_energy        = mech_energy - torsion_energy_change;
          }
          else 
          {
            mech_energy_change = mech_energy_change + torsion_energy_change;
            mech_energy        = mech_energy + torsion_energy_change;
          }
        }
        
      //  System.out.println("; changed mech.energy = "+mech_energy);
        
        // Change of torsion energy
        torsion_energy = torsion_energy + torsion_energy_change;
          
        // Change of stress
        stress = mech_energy/getVolume();
          
        old_stress = stress;
    }
    
    /** Change of torsion energy coefficient
     * @param old_stress old value of cell stress
     * @param new_stress new value of cell stress
     */
    public void calcNewTorsionEnergyCoeff(double old_stress, double new_stress)
    {
        double factor = 0.9;
        
        if(state == Common.ELASTIC_CELL)
           current_torsion_energy_coeff = torsion_energy_coeff;
        
        if(threshold_stress <= 0)
        {
            System.out.println("ERROR!!! threshold_stress must be positive !!! threshold_stress = "+threshold_stress);
        }
        
        if(Math.signum(old_stress) == Math.signum(new_stress) & new_stress >= 0)
        {
          if(Math.abs(old_stress) <= Math.abs(new_stress))
          {
            if(Math.abs(new_stress) <=  threshold_stress)
            {
                current_torsion_energy_coeff = torsion_energy_coeff;
                state = Common.ELASTIC_CELL;
            }
            else
            {
                current_torsion_energy_coeff = torsion_energy_coeff*Math.abs(new_stress)/threshold_stress;
                state = Common.PLASTIC_CELL;
            }
          }
          
          if(Math.abs(new_stress) <= Math.abs(old_stress) & state == Common.PLASTIC_CELL)
          {
         //   if(current_torsion_energy_coeff != torsion_energy_coeff)
            if(new_stress < factor*threshold_stress)
            {
                if(new_stress > 0)
                    current_torsion_energy_coeff = torsion_energy_coeff*Math.abs(new_stress)/(factor*threshold_stress);
                else
                    current_torsion_energy_coeff = torsion_energy_coeff;
                
                state = Common.ELASTIC_CELL;
            }
          }
        }
        
        if(Math.signum(old_stress) != Math.signum(new_stress))
        {
            if(old_stress <= 0 & new_stress >= 0)
            {
                if(new_stress <=  threshold_stress)
                {
                    current_torsion_energy_coeff = torsion_energy_coeff;
                    state = Common.ELASTIC_CELL;
                }
                else
                {
                    current_torsion_energy_coeff = torsion_energy_coeff*new_stress/threshold_stress;
                    state = Common.PLASTIC_CELL;
                }
            }
            
            if(old_stress >= 0 & new_stress <= 0 & state == Common.PLASTIC_CELL)
            {
                if(state == Common.PLASTIC_CELL)
                if(current_torsion_energy_coeff != torsion_energy_coeff)
                {
                    current_torsion_energy_coeff = torsion_energy_coeff;
                    state = Common.ELASTIC_CELL;
                }
                
                if(state == Common.ELASTIC_CELL)
                {
                    current_torsion_energy_coeff = torsion_energy_coeff;
                }
            }
        }
        
        if(new_stress < 0)
        {
            current_torsion_energy_coeff = torsion_energy_coeff;
        }
        
        if(current_torsion_energy_coeff <=0)
        {
            System.out.println("ERROR!!! current_torsion_energy_coeff cannot be negative !!! "
                    + "current_torsion_energy_coeff = "+current_torsion_energy_coeff+" state = "+state);
        }
    }
    
    /** The method calculates the new number of defects for cell
     * @param torsion_angle_change change of angle of torsion of material in a cell
     * @param def_portion portion of defects generated due to local torsion of crystal lattice 
     */
    public void addDefectNumber(double def_portion, double torsion_angle_change)
    {
     //   long def_num_change_1, def_num_change_2; 
        
        // Calculation of change of defect number according to the 1st formula
   //     def_num_change_1 = (long)Math.round(volume*disl_density/lattice_parameter) - defect_number;
        
        // Calculation of change of defect number according to the 2nd formula
     //   def_num_change_2 = (long)Math.round(current_torsion_energy_coeff*torsion_angle_change/12.0);
        
        // Two values of defect number change cannot be negative.
   //     if(def_num_change_1 < 0)    def_num_change_1 = 0;
   //     if(def_num_change_2 < 0)    def_num_change_2 = 0;
        
        // Average value for two values of defect number change is calculated.
        long def_num_change;//   = def_num_change_2;// (def_num_change_1 + def_num_change_2)/2;
        
     //   def_num_change = (long)Math.round(volume*disl_density_change/lattice_parameter);
     
        def_num_change = (long)Math.round(def_portion*torsion_angle_change*cellSizeX*cellSizeY*cellSizeZ/
                                         (Math.PI*lattice_parameter*lattice_parameter*lattice_parameter));
        
        // Change of defect number cannot be larger than the total number of atoms.
        def_num_change   = Math.min(def_num_change, atom_number);
        
        // Negative change of defect number cannot be larger than the total number of defects.
        def_num_change   = Math.max(def_num_change, -defect_number);
        
        // The total number of defects is determined.
        defect_number   += def_num_change;
        
        // The total number of atoms is determined.
        atom_number     -= def_num_change;
        
        // Change of dislocation density for material contained in a cell
        double disl_density_change = def_num_change*lattice_parameter/volume;
        
        disl_density_change = Math.max(-disl_density, disl_density_change);
        
        disl_density+= disl_density_change;
    }            
    
    /** The method returns the value of cell torsion energy change.
     * @return the value of cell torsion energy change
     */
    public double getTorsionEnergyChange()
    {
        return torsion_energy_change;
    }
    
    /** The method sets the value of cell torsion energy change.
     * @param _torsion_energy_change the value of cell torsion energy change
     */
    public void setTorsionEnergyChange(double _torsion_energy_change)
    {
        torsion_energy_change = _torsion_energy_change;
    }
    
    /** The method returns the coefficient for calculation of cell torsion energy change.
     * @return the coefficient for calculation of cell torsion energy change
     */
    public double getTorsionEnergyCoeff()
    {
        return torsion_energy_coeff;
    }
    
    /** The method sets the coefficient for calculation of cell torsion energy change.
     * @param _torsion_energy_coeff the coefficient for calculation of cell torsion energy change
     */
    public void setTorsionEnergyCoeff(double _torsion_energy_coeff)
    {
        torsion_energy_coeff = _torsion_energy_coeff;
    }
    
    /** The method returns coefficient for calculation of local torsion energy at grain boundary.
     * @return coefficient for calculation of local torsion energy at grain boundary
     */
    public double getTorsionEnergyCoeffForGrBound()
    {
        return torsion_energy_coeff_gr_bound;
    }
    
    /** The method sets coefficient for calculation of local torsion energy at grain boundary.
     * @param torsion_energy_coeff_gr_bound coefficient for calculation of local torsion energy at grain boundary
     */
    public void setTorsionEnergyCoeffForGrBound(double _torsion_energy_coeff_gr_bound)
    {
        torsion_energy_coeff_gr_bound = _torsion_energy_coeff_gr_bound;
    }
    
    /** The method returns coefficient for calculation of local torsion energy in a region near grain boundary.
     * @return coefficient for calculation of local torsion energy in a region near grain boundary
     */
    public double getTorsionEnergyCoeffForGrBoundRegion()
    {
        return torsion_energy_coeff_gb_region;
    }
    
    /** The method sets coefficient for calculation of local torsion energy in a region near grain boundary.
     * @param torsion_energy_coeff_gb_region coefficient for calculation of local torsion energy in a region near grain boundary
     */
    public void setTorsionEnergyCoeffForGrBoundRegion(double _torsion_energy_coeff_gb_region)
    {
        torsion_energy_coeff_gb_region = _torsion_energy_coeff_gb_region;
    }
    
    /** The method returns current coefficient for calculation of cell torsion energy change.
     * @return current coefficient for calculation of cell torsion energy change
     */
    public double getCurrentTorsionEnergyCoeff()
    {
        return current_torsion_energy_coeff;
    }
    
    /** The method returns current coefficient for calculation of cell torsion energy change.
     * @param current_torsion_energy_coeff current coefficient for calculation of cell torsion energy change
     */
    public void setCurrentTorsionEnergyCoeff(double _current_torsion_energy_coeff)
    {
        current_torsion_energy_coeff = _current_torsion_energy_coeff;
    }
    
    /** The method returns low temperature threshold value.
     * @return low temperature threshold value
     */
    public double getLowTemperThreshold()
    {
        return lowTemperThreshold;
    }
    
    /** The method returns high temperature threshold value
     * @return high temperature threshold value.
     */
    public double getHighTemperThreshold()
    {
        return highTemperThreshold;
    }
    
    /** The method returns temperature of start of twinning.
     * @return temperature of start of twinning
     */
    public double getMinTwinTemperature()
    {
        return min_twin_temperature;
    }
    
    /** The method returns temperature of finish of grain twinning.
     * @return temperature of finish of grain twinning
     */
    public double getTwinningTemperature()
    {
        return twinning_temperature;
    }
    
    /** The method returns the threshold stress for phase transition of cell material.
     * @return the threshold stress for phase transition of cell material
     */
    public double getThresholdStress()
    {
        return threshold_stress;
    }
    
    /** The method sets the threshold stress for phase transition of cell material.
     * @param _threshold_stress the threshold stress for phase transition of cell material
     */
    public void setThresholdStress(double _threshold_stress)
    {
        threshold_stress = _threshold_stress;
    }
    
    /** The method returns the value of change of cell mechanical energy.
     * @return the value of change of cell mechanical energy
     */
    public double getMechEnergyChange()
    {
        return mech_energy_change;
    }    
    
    /** The method adds the certain value to the value of torsion energy.
     * @param tors_energy_change the value added to the value of torsion energy
     */
    public void changeTorsionEnergy(double tors_energy_change)
    {
        torsion_energy = torsion_energy + tors_energy_change;
        
        if(torsion_energy < 0)
        {
          //  System.out.println("ERROR!!! torsion_energy cannot be negative!!! torsion_energy = "+torsion_energy);
            
            if(mech_energy >= 0) mech_energy += torsion_energy;
            else                 mech_energy -= torsion_energy;
            
            torsion_energy = 0;
        }
    }
    
    /** The method returns component X of torsion angle.
     * @return component X of torsion angle
     */    
    public double getTorsionAngleX()
    {
        return torsion_angle_X;
    }
    
    /** The method returns component Y of torsion angle.
     * @return component Y of torsion angle
     */
    public double getTorsionAngleY()
    {
        return torsion_angle_Y;
    }
    
    /** The method returns component Z of torsion angle.
     * @return component Z of torsion angle
     */
    public double getTorsionAngleZ()
    {
        return torsion_angle_Z;
    }
    
    /** The method returns torsion angle of cell3D.
     * @return torsion angle of cell3D
     */
    public VectorR3 getTorsionAngle()
    {
        return new VectorR3(torsion_angle_X, torsion_angle_Y, torsion_angle_Z);
    }
    
    /** The method sets component X of torsion angle of cell.
     * @param _torsion_angle_X component X of torsion angle of cell
     */    
    public void setTorsionAngleX(double _torsion_angle_X)
    {
        torsion_angle_X = _torsion_angle_X;
    }
    
    /** The method sets component Y of torsion angle of cell.
     * @param _torsion_angle_Y component Y of torsion angle of cell
     */    
    public void setTorsionAngleY(double _torsion_angle_Y)
    {
        torsion_angle_Y = _torsion_angle_Y;
    }
    
    /** The method sets component Z of torsion angle of cell.
     * @param _torsion_angle_Z component Z of torsion angle of cell
     */    
    public void setTorsionAngleZ(double _torsion_angle_Z)
    {
        torsion_angle_Z = _torsion_angle_Z;
    }    
    
    /** The method sets torsion angle of cell3D.
     * @param _torsion_angle new torsion angle of cell3D
     */
    public void setTorsionAngle(VectorR3 _torsion_angle)
    {
        torsion_angle_X = _torsion_angle.getX();
        torsion_angle_Y = _torsion_angle.getY();
        torsion_angle_Z = _torsion_angle.getZ();
    }
    
    /** The method increases component X of torsion angle of cell by certain value.
     * @param _torsion_angle_X additional value of component X of torsion angle of cell
     */    
    public void addTorsionAngleX(double _torsion_angle_X)
    {
        torsion_angle_X += _torsion_angle_X;
    }
    
    /** The method increases component Y of torsion angle of cell by certain value.
     * @param _torsion_angle_Y additional value of component Y of torsion angle of cell
     */    
    public void addTorsionAngleY(double _torsion_angle_Y)
    {
        torsion_angle_Y += _torsion_angle_Y;
    }  
    
    /** The method increases component Z of torsion angle of cell by certain value.
     * @param _torsion_angle_Z additional value of component Z of torsion angle of cell
     */    
    public void addTorsionAngleZ(double _torsion_angle_Z)
    {
        torsion_angle_Z += _torsion_angle_Z;
    }    
    
    /** The method adds certain vector angle to torsion angle of cell.
     * @param _torsion_angle additional vector angle
     */
    public void addTorsionAngle(VectorR3 _torsion_angle)
    {      
        torsion_angle_X += _torsion_angle.getX();
        torsion_angle_Y += _torsion_angle.getY();
        torsion_angle_Z += _torsion_angle.getZ();
        
      //  torsion_angle_change = _torsion_angle.getLength();
    }
    
    /** The method returns instant specific force moment of cell.
     * @return _instant_force_moment instant specific force moment
     */
    public VectorR3 getInstantForceMoment()
    {
        return instant_force_moment;
    }
    
    /** The method returns component X of instant specific force moment of cell.
     * @return _instant_force_moment component X of instant specific force moment
     */
    public double getInstantForceMomentX()
    {
        return instant_force_moment.getX();
    }
    
    /** The method returns component Y of instant specific force moment of cell.
     * @return _instant_force_moment component Y of instant specific force moment
     */
    public double getInstantForceMomentY()
    {
        return instant_force_moment.getY();
    }
    
    /** The method returns component Z of instant specific force moment of cell.
     * @return _instant_force_moment component Z of instant specific force moment
     */
    public double getInstantForceMomentZ()
    {
        return instant_force_moment.getZ();
    }
    
    /** The method sets instant specific force moment of cell.
     * @param _instant_force_moment instant specific force moment
     */
    public void setInstantForceMoment(VectorR3 _instant_force_moment)
    {
        instant_force_moment = new VectorR3(_instant_force_moment);
    }    
    
    /** The method returns total number of 1D and 2D defects in material contained in a cell.
     * @return total number of 1D and 2D defects in material contained in a cell
     */
    public long getDefectNumber()
    {
        return defect_number;
    }
    
    /** The method returns total number of atoms in material contained in a cell.
     * @return total number of atoms in material contained in a cell
     */
    public long getAtomNumber()
    {
        return atom_number;
    }
    
    /** The method returns molar mass of material.
     * @return molar mass of material
     */
    public double getMolarMass()
    {
        return molar_mass;
    }
    
    /** The method sets total number of 1D and 2D defects in material contained in a cell.
     * @param _defect_number total number of 1D and 2D defects in material contained in a cell
     */
    public void setDefectNumber(int _defect_number)
    {
        defect_number = _defect_number;
    }
    
    /** The method sets total number of atoms in material contained in a cell.
     * @param _atom_number total number of atoms in material contained in a cell
     */
    public void setAtomNumber(int _atom_number)
    {
        atom_number = _atom_number;
    }
    
    /** The method sets molar mass of material.
     * @param _molar_mass molar mass of material
     */
    public void setMolarMass(double _molar_mass)
    {
        molar_mass = _molar_mass;
    }
    
    /** The method adds current heat influx to cell heat influx.
     * @param current_heat_influx current heat influx
     * 
     */
    public void addHeatInflux(double current_heat_influx)
    {
        heat_influx += current_heat_influx;
    }
    
    /** The method calculates current heat influx.
     * 
     */
    public void addHeatInflux()
    {
        heat_influx += (temperature - initialTemperature)*density*volume*specific_heat_capacity;
    }
    
    /** The method returns cell heat influx.
     * @return cell heat influx
     */
    public double getHeatInflux()
    {
        return heat_influx;
    }
    
    /** The method sets current heat influx to cell.
     * @param _heat_influx current heat influx to cell
     * 
     */
    public void setHeatInflux(double _heat_influx)
    {
        heat_influx = _heat_influx;
    }
    
    /** The method returns array of single indices of cell3D neighbours in case of HCP lattice of cells.
     * @return _neighbour_indices_HCP array of single indices of cell3D neighbours in case of HCP lattice of cells
     */
/*
    public int[] getNeighbourIndicesHCP()
    {
        return neighbour_indices_HCP;
    }
 */
    /** The method sets array of single indices of cell3D neighbours in case of HCP lattice of cells.
     * @param _neighbour_indices_HCP array of single indices of cell3D neighbours in case of HCP lattice of cells
     */
    /*
    public void setNeighbourIndicesHCP(int[] _neighbour_indices_HCP)
    {
        neighbour_indices_HCP = _neighbour_indices_HCP;
    }
     */

    /** The method returns material of the cell.
     * @return material of the cell
     */
 /*   public Material getMaterial()
    {
        return material;
    }
 */
    /** The method sets material of cell
     * @param _material type of material
     * @param _state state of material
     */
/*    public void setMaterial(String _material, byte _state)
    {
        material_file   = new String(_material);
        state           = _state;
        material        = new Material(_material, _state);
    }
*/
    /** The method sets type of cell material
     * @param _material type of cell material
     */
/*    public void setMaterial(Material _material)
    {
        material = new Material(_material);
    }
 */
}