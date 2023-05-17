package cellcore;
import java.util.*;
import java.io.*;
import util.*;
import interf.*;

/*
 * @(#)Material.java version 1.0.3;       Nov 2008
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for information about material. 
 *
 *=============================================================
 * Last changes :
 *         14 November 2008 by Pavel V. Maksimov 
 *       (fields "thermal_conduct_bound" and "phonon_portion" 
 *        and methods "get-" and "set-" for them are added).
 *
 *            File version 1.0.3
 */

/** Class for info about specimen3D for modelling of deformation. 
 * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.0 - Apr 2008
 */

public class Material
{    
    Properties material_properties;
    
    /** Modulus of elasticity (MPa) */
    private double mod_elast;
    
    /** Density (kg/m3) */
    private double density;
    
    /** Coefficient of heat expansion (1/K) */
    private double heatExpansionCoeff;
    
    /** Yield strain */
    private double yieldStrain;
    
    /** Ultimate strain */
    private double ultimateStrain;
    
    /** Coefficient determining the portion of thermal energy in change of total energy */
    private double energy_coeff;
    
    /** Specific heat capacity */
    private double specific_heat_capacity;
    
    /** Thermal conductivity */
    private double thermal_conductivity;
    
    /** Thermal conductivity on grain boundary */
    private double thermal_conduct_bound;
    
    /** Portion of phonon thermal conductivity in total thermal conductivity */
    private double phonon_portion;
    
    /** Low temperature threshold value */
    private double lowTemperThreshold;
    
    /** High temperature threshold value */
    private double highTemperThreshold;
    
    /** High temperature threshold values for each material state */
    private double highTemperThrSCP;
    private double highTemperThrBCC;
    private double highTemperThrFCC;
    private double highTemperThrHCP;
    
    /** Activation energy */
    private double actEnergy;
    
    /** Specific energy of high angle grain boundary */
    private double energyHAGB;
    
    /** Maximal value of boundary mobility under thermal recrystallization */
    private double maxMobility;
    
    /** Maximal value of boundary mobility under cold recrystallization */
    private double dislMaxMobility;
    
    /** Maximal value of boundary mobility under mechanical loading */
    private double mechMaxMobility;
    
    /** Transition limit to high angle grain boundary regime */
    private double angleLimitHAGB;
    
    /** Shear modulus */
    private double mod_shear;
    
    /** Lattice parameter */    
    private double lattice_parameter;
    
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
    private double disl_distr_coeff;

    /** Coefficient determining switch of cell to plastic state */
    private double yield_state_coeff;

    /** Coefficient determining switch of cell to damaged state */
    private double ultimate_state_coeff;

    /** Volume fraction of particles */
    private double part_vol_fraction;

    /** Surface energy of particle */
    private double part_surf_energy;

    /** Radius of particle */
    private double part_radius;
    
    /** Molar mass of material */
    private double molar_mass;
    
    /** Threshold stress for switch of local state of material */
    private double threshold_stress;
    
    /** Coefficient for calculation of local torsion energy */
    private double torsion_energy_coeff;
    
    /** Coefficient for calculation of local torsion energy at grain boundary */
    private double torsion_energy_coeff_gr_bound;
    
    /** Coefficient for calculation of local torsion energy in a region near grain boundary */
    private double torsion_energy_coeff_gb_region;
    
    /** Temperature of start of twinning */
    private double min_twin_temperature;
    
    /** Temperature of finish of twinning */
    private double twinning_temperature;
    
    /** Maximal probability of recrystallization */
    private double max_prob_recryst;
    
    /** Maximal probability of twinning */
    private double max_prob_twinning;
    
    /** Minimal density of dislocations */
    private double min_disl_density;
    
    /** The constructor loads parameters of material. 
     * @param material_file file with parameters of material
     */
    public Material(String material_file)
    {
        material_properties = new Properties();
        
        try
        {
            FileInputStream fin = new FileInputStream(Common.MATERIALS_PATH+"/"+material_file+"."+Common.MATERIAL_EXTENSION);
            material_properties.load(fin);
            fin.close();
                                  
            // System.out.println("Name of read file with material properties: "+Common.MATERIALS_PATH+"/"+material_file+"."+Common.MATERIAL_EXTENSION);
        }
        catch(IOException io_ex) 
        {
            System.out.println("ERROR: "+io_ex);
        }
                 
        // Reading of data about materials.        
        // System.out.println("./task/"+material_file+".txt");
        // System.out.println("material = "+material_file+" state = "+material_state);
        // System.out.println("material_properties= "+material_properties);
        mod_elast               = (new Double(material_properties.getProperty(UICommon.MOD_ELAST_PROPERTIES))).doubleValue();
        density                 = (new Double(material_properties.getProperty(UICommon.DENSITY_PROPERTIES))).doubleValue();
        heatExpansionCoeff      = (new Double(material_properties.getProperty(UICommon.HEAT_EXPANSION_COEFF_PROPERTIES))).doubleValue();
        yieldStrain             = (new Double(material_properties.getProperty(UICommon.YIELD_STRAIN_PROPERTIES))).doubleValue();
        ultimateStrain          = (new Double(material_properties.getProperty(UICommon.ULTIMATE_STRAIN_PROPERTIES))).doubleValue();
        
        energy_coeff            = (new Double(material_properties.getProperty(UICommon.ENERGY_COEFF_PROPERTIES))).doubleValue();
        specific_heat_capacity  = (new Double(material_properties.getProperty(UICommon.SPECIFIC_HEAT_CAPACITY_PROPERTIES))).doubleValue();
        thermal_conductivity    = (new Double(material_properties.getProperty(UICommon.THERMAL_CONDUCTIVITY_PROPERTIES))).doubleValue();
        thermal_conduct_bound   = (new Double(material_properties.getProperty(UICommon.THERMAL_CONDUCT_BOUND_PROPERTIES))).doubleValue();
        phonon_portion          = (new Double(material_properties.getProperty(UICommon.PHONON_PORTION_PROPERTIES))).doubleValue();
        actEnergy               = (new Double(material_properties.getProperty(UICommon.ACT_ENERGY_PROPERTIES))).doubleValue();
        angleLimitHAGB          = (new Double(material_properties.getProperty(UICommon.ANGLE_LIMIT_HAGB_PROPERTIES))).doubleValue();
        energyHAGB              = (new Double(material_properties.getProperty(UICommon.ENERGY_HAGB_PROPERTIES))).doubleValue();
        maxMobility             = (new Double(material_properties.getProperty(UICommon.MAX_MOBILITY_PROPERTIES))).doubleValue();
        dislMaxMobility         = (new Double(material_properties.getProperty(UICommon.DISL_MAX_MOBILITY_PROPERTIES))).doubleValue();
        mechMaxMobility         = (new Double(material_properties.getProperty(UICommon.MECH_MAX_MOBILITY_PROPERTIES))).doubleValue();
        mod_shear               = (new Double(material_properties.getProperty(UICommon.MOD_SHEAR_PROPERTIES))).doubleValue();
        
        disl_distr_coeff        = (new Double(material_properties.getProperty(UICommon.DISL_DISTR_COEFF_PROPERTIES))).doubleValue();
        yield_state_coeff       = (new Double(material_properties.getProperty(UICommon.YIELD_STATE_COEFF_PROPERTIES))).doubleValue();
        ultimate_state_coeff    = (new Double(material_properties.getProperty(UICommon.ULTIMATE_STATE_COEFF_PROPERTIES))).doubleValue();

        part_vol_fraction       = (new Double(material_properties.getProperty(UICommon.PART_VOL_FRACTION_PROPERTIES))).doubleValue();
     //   part_surf_energy        = (new Double(material_properties.getProperty("part_surf_energy"))).doubleValue();
        part_radius             = (new Double(material_properties.getProperty(UICommon.PART_RADIUS_PROPERTIES))).doubleValue();
        
        molar_mass              = (new Double(material_properties.getProperty(UICommon.MOLAR_MASS_PROPERTIES))).doubleValue();
        threshold_stress        = (new Double(material_properties.getProperty(UICommon.THRESHOLD_STRESS_PROPERTIES))).doubleValue();
        
        torsion_energy_coeff    = (new Double(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_PROPERTIES))).doubleValue();
        torsion_energy_coeff_gr_bound  = (new Double(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_GB_1_PROPERTIES))).doubleValue();         
        torsion_energy_coeff_gb_region = (new Double(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_GB_2_PROPERTIES))).doubleValue();
        
        lowTemperThreshold      = (new Double(material_properties.getProperty(UICommon.LOW_TEMPER_THR_VALUE_PROPERTIES))).doubleValue();
        highTemperThreshold     = (new Double(material_properties.getProperty(UICommon.HIGH_TEMPER_THR_VALUE_PROPERTIES))).doubleValue();
        
        // Parameter of lattice (minimal distance between lattice nodes/atoms)
        lattice_parameter       = (new Double(material_properties.getProperty(UICommon.LATTICE_PARAMETER_PROPERTIES))).doubleValue();
        
        // Lengths of 3 vectors of lattice anisotropy
        lattice_vector_A_length = (new Double(material_properties.getProperty(UICommon.LATTICE_VECTOR_A_LENGTH_PROPERTIES))).doubleValue();
        lattice_vector_B_length = (new Double(material_properties.getProperty(UICommon.LATTICE_VECTOR_B_LENGTH_PROPERTIES))).doubleValue();
        lattice_vector_C_length = (new Double(material_properties.getProperty(UICommon.LATTICE_VECTOR_C_LENGTH_PROPERTIES))).doubleValue();
    
        // Angles between vectors of lattice anisotropy
        lattice_angle_vecA_vecB = (new Double(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_A_VEC_B_PROPERTIES))).doubleValue();
        lattice_angle_vecB_vecC = (new Double(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_B_VEC_C_PROPERTIES))).doubleValue();
        lattice_angle_vecC_vecA = (new Double(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_C_VEC_A_PROPERTIES))).doubleValue();
    
        // Coefficient of lattice anisotropy
        lattice_anis_coeff      = (new Double(material_properties.getProperty(UICommon.LATTICE_ANISOTROPY_COEFF_PROPERTIES))).doubleValue();
        
        // Temperature of start of twinning
        min_twin_temperature    = (new Double(material_properties.getProperty(UICommon.MIN_TWIN_TEMPERATURE_PROPERTIES))).doubleValue();
    
        // Temperature of finish of twinning
        twinning_temperature    = (new Double(material_properties.getProperty(UICommon.TWINNING_TEMPERATURE_PROPERTIES))).doubleValue();
        
        // Maximal probability of recrystallization
        max_prob_recryst        = (new Double(material_properties.getProperty(UICommon.MAXIMAL_PROB_RECRYST_PROPERTIES))).doubleValue();
        
        // Maximal probability of twinning
        max_prob_twinning       = (new Double(material_properties.getProperty(UICommon.MAXIMAL_PROB_TWINNING_PROPERTIES))).doubleValue();
        
        // Minimal density of dislocations
        min_disl_density        = (new Double(material_properties.getProperty(UICommon.MINIMAL_DISL_DENSITY_PROPERTIES))).doubleValue();
    }
        
    /** The constructor creates material basing on sample material.
     * @param _material sample material
     */
    public Material(Material _material)
    {
        // Loading of parameters of sample material
        mod_elast               = _material.get_mod_elast();
        density                 = _material.get_density();
        heatExpansionCoeff      = _material.get_heatExpCoeff();
        yieldStrain             = _material.get_yieldStrain();
        ultimateStrain          = _material.get_ultimateStrain();
        energy_coeff            = _material.get_energy_coeff();
        specific_heat_capacity  = _material.get_specific_heat_capacity();
        thermal_conductivity    = _material.get_thermal_conductivity();
        thermal_conduct_bound   = _material.get_thermal_conduct_bound();
        phonon_portion          = _material.get_phonon_portion();
        lowTemperThreshold      = _material.getLowTemperThreshold();
        highTemperThreshold     = _material.getHighTemperThreshold();
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

        part_vol_fraction       = _material.getParticleVolumeFraction();
     //   part_surf_energy        = _material.getParticleSurfaceEnergy();
        part_radius             = _material.getParticleRadius();
        
        molar_mass              = _material.getMolarMass();        
        threshold_stress        = _material.getThresholdStress();
        
        torsion_energy_coeff           = _material.getTorsionEnergyCoeff();
        torsion_energy_coeff_gr_bound  = _material.getTorsionEnergyCoeffForGrBound();
        torsion_energy_coeff_gb_region = _material.getTorsionEnergyCoeffForGrBoundRegion();
        
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
        
        // Temperature of start of twinning
        min_twin_temperature    = _material.getMinTwinTemperature();
    
        // Temperature of finish of twinning
        twinning_temperature    = _material.getTwinningTemperature();
        
        // Maximal probability of recrystallization
        max_prob_recryst        = _material.getMaxProbRecryst();
        
        // Maximal probability of twinning
        max_prob_twinning       = _material.getMaxProbTwinning();
        
        // Minimal density of dislocations
        min_disl_density        = _material.getMinDislDensity();
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
    
    /** The method returns temperature of start of twinning.
     * @return temperature of start of twinning
     */
    public double getMinTwinTemperature()
    {
        return min_twin_temperature;
    }
    
    /** The method returns temperature of finish of twinning.
     * @return temperature of finish of twinning
     */
    public double getTwinningTemperature()
    {
        return twinning_temperature;
    }
    
    /** The method returns maximal probability of recrystallization.
     * @return maximal probability of recrystallization
     */
    public double getMaxProbRecryst()
    {
        return max_prob_recryst;
    }
    
    /** The method returns maximal probability of twinning.
     * @return maximal probability of twinning
     */
    public double getMaxProbTwinning()
    {
        return max_prob_twinning;
    }
    
    /** The method returns minimal density of dislocations.
     * @return minimal density of dislocations
     */
    public double getMinDislDensity()
    {
        return min_disl_density;
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
    
    /** The method returns ultimate strain of material.
     * @return ultimate strain of material
     */
    public double get_ultimateStrain()
    {
        return ultimateStrain;
    }    
    
    /** The method returns coefficient determining the portion of thermal energy
     * in change of total energy.
     * @return coefficient determining the portion of thermal energy in change of total energy
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
    
    /** The method returns high temperature threshold value for SCP.
     * @return high temperature threshold value for SCP
     */
    public double getHighTemperThrSCP()
    {
        return highTemperThrSCP;
    }
    
    /** The method returns high temperature threshold value for BCC.
     * @return high temperature threshold value for BCC
     */
    public double getHighTemperThrBCC()
    {
        return highTemperThrBCC;
    }
    
    /** The method returns high temperature threshold value for FCC.
     * @return high temperature threshold value for FCC
     */
    public double getHighTemperThrFCC()
    {
        return highTemperThrFCC;
    }
    
    /** The method returns high temperature threshold value for HCP.
     * @return high temperature threshold value for HCP
     */
    public double getHighTemperThrHCP()
    {
        return highTemperThrHCP;
    }
        
    /** The method returns activation energy.
     * @return activation energy
     */
    public double get_activation_energy()
    {
        return actEnergy;
    }
    
    /** The method returns specific energy of high angle grain boundary.
     * @return specific energy of high angle grain boundary 
     */
    public double getEnergyHAGB()
    {
        return energyHAGB;
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
    
    /** The method returns transition limit to high angle grain boundary regime.
     * @return transition limit to high angle grain boundary regime
     */
    public double getAngleLimitHAGB()
    {
        return angleLimitHAGB;
    }

    /** The method returns volume fraction of particles.
     * @return volume fraction of particles
     */
    public double getParticleVolumeFraction()
    {
        return part_vol_fraction;
    }

    /** The method returns surface energy of particle.
     * @return surface energy of particle
     */
    public double getParticleSurfaceEnergy()
    {
        return part_surf_energy;
    }

    /** The method returns radius of particle.
     * @return radius of particle
     */
    public double getParticleRadius()
    {
        return part_radius;
    }

    /** The method returns ultimate strain of material.
     * @return ultimate strain of material
     */
    double getUltimateStrain()
    {
        return ultimateStrain;
    }

    /** The method sets volume fraction of particles.
     * @param _part_vol_fraction volume fraction of particles
     */
    public void setParticleVolumeFraction(double _part_vol_fraction)
    {
        part_vol_fraction = _part_vol_fraction;
    }

    /** The method sets surface energy of particle.
     * @param _part_surf_energy surface energy of particle
     */
    public void setParticleSurfaceEnergy(double _part_surf_energy)
    {
        part_surf_energy = _part_surf_energy;
    }

    /** The method sets radius of particle.
     * @param _part_radius radius of particle
     */
    public void setParticleRadius(double _part_radius)
    {
        part_radius = _part_radius;
    }

    /** The method sets specific energy of high angle grain boundary.
     * @param _energyHAGB specific energy of high angle grain boundary 
     */
    public void setEnergyHAGB(double _energyHAGB)
    {
        energyHAGB = _energyHAGB;
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
    
    /** The method sets transition limit to high angle grain boundary regime.
     * @param _angleLimitHAGB transition limit to high angle grain boundary regime
     */
    public void setAngleLimitHAGB(double _angleLimitHAGB)
    {
        angleLimitHAGB = _angleLimitHAGB;
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
    
    /** The method sets temperature of start of twinning.
     * @param value temperature of start of twinning
     */
    public void setMinTwinTemperature(double value)
    {
        min_twin_temperature = value;
    }
    
    /** The method sets temperature of finish of twinning.
     * @param value temperature of finish of twinning
     */
    public void setTwinningTemperature(double value)
    {
        twinning_temperature = value;
    }
    
    /** The method sets maximal probability of recrystallization.
     * @param value maximal probability of recrystallization
     */
    public void setMaxProbRecryst(double value)
    {
        max_prob_recryst = value;
    }
        
    /** The method sets maximal probability of twinning.
     * @param value maximal probability of twinning
     */
    public void setMaxProbTwinning(double value)
    {
        max_prob_twinning = value;
    }
    
    /** The method sets minimal density of dislocations.
     * @param minimal density of dislocations
     */
    public void setMinDislDensity(double value)
    {
        min_disl_density = value;
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

    /** The method sets ultimate strain of material.
     * @param _ultimateStrain ultimate strain of material
     */
    public void setUltimateStrain(double _ultimateStrain)
    {
        ultimateStrain = _ultimateStrain;
    }
    
    /** The method returns molar mass of material.
     * @return molar mass of material
     */
    public double getMolarMass()
    {
        return molar_mass;
    }
    
    /** The method sets molar mass of material.
     * @param _molar_mass molar mass of material
     */
    public void setMolarMass(double _molar_mass)
    {
        molar_mass = _molar_mass;
    }
    
    /** The method returns threshold stress for switch of local state of material.
     * @return threshold stress for switch of local state of material
     */
    public double getThresholdStress()
    {
        return threshold_stress;
    }
    
    /** The method sets threshold stress for switch of local state of material.
     * @param _threshold_stress threshold stress for switch of local state of material
     */
    public void setThresholdStress(double _threshold_stress)
    {
        threshold_stress = _threshold_stress;
    }    
    
    /** The method returns coefficient for calculation of local torsion energy.
     * @return coefficient for calculation of local torsion energy
     */
    public double getTorsionEnergyCoeff()
    {
        return torsion_energy_coeff;
    }
    
    /** The method sets coefficient for calculation of local torsion energy.
     * @param _torsion_energy_coeff coefficient for calculation of local torsion energy
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
}