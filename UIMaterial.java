/*
 * @(#) UIMaterial.java version 1.0.0;       May 12 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of material's data, wich contain default value of
 * material's parameters. This class can load data from file in specific directory.
 * This class can set and get value of parameters.
 *
 *=============================================================
 *  Last changes :
 *          8 December 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.4
 *          Edit: Add new parameters - yield_state_coeff and
 *          ultimate_state_coeff
 */

/** Class for creation bank of material's data, wich contain default value of
 *  material's parameters. This class can load data from file in specific directory.
 *  This class can set and get value of parameters.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - May 2009
 */

package interf;

import java.util.Properties;
import java.io.*;

public class UIMaterial
{
    /** This field is intended for creation a double value for remembering
     * in memory value of material's parameters
     */
    protected double mod_elast,density,heatExpansionCoeff,yieldStrain,ultimateStrain,energy_coeff;
    protected double specific_heat_capacity,thermal_conductivity,thermal_conduct_bound,phonon_portion;
    protected double lowTemperThrValue,highTemperThrValue,actEnergy,angleLimitHAGB,energyHAGB;
    protected double maxMobility,mod_shear,disl_distr_coeff;
    protected double dislMaxMobility, mechMaxMobility, yield_state_coeff, ultimate_state_coeff;
    protected double part_vol_fraction, part_radius;
    protected double molar_mass, threshold_stress;
    protected double torsion_energy_coeff, torsion_energy_coeff_gr_bound, torsion_energy_coeff_gb_region;
  //  protected double low_tempr_recryst, high_tempr_recryst;
    protected double lattice_parameter;
    protected double lattice_vector_A_length, lattice_vector_B_length, lattice_vector_C_length;
    protected double lattice_angle_vecA_vecB, lattice_angle_vecB_vecC, lattice_angle_vecC_vecA;
    protected double lattice_anis_coeff;
    protected double min_twin_temperature, twinning_temperature;
    double max_prob_recryst, max_prob_twinning, min_disl_density;
    
    /** Create default bank of data UIMaterial
     */
    public UIMaterial()
    {
        System.out.println("UIMaterial: constructor creation start");

        //input default parameters value
        mod_elast = 0.0;
        density = 0.0;
        heatExpansionCoeff = 0.0;
        yieldStrain = 0.0;
        ultimateStrain = 0.0;
        energy_coeff = 0.0;
        specific_heat_capacity = 0.0;
        thermal_conductivity = 0.0;
        thermal_conduct_bound = 0.0;
        phonon_portion = 0.0;
        lowTemperThrValue = 0.0;
        highTemperThrValue = 0.0;
        actEnergy = 0.0;
        angleLimitHAGB = 0.0;
        energyHAGB = 0.0;
        maxMobility = 0.0;
        mod_shear = 0.0;
        
        lattice_parameter = 0.0;
        lattice_vector_A_length =  1.0;
        lattice_vector_B_length =  1.0;
        lattice_vector_C_length =  1.0;
        lattice_angle_vecA_vecB = 90.0;
        lattice_angle_vecB_vecC = 90.0;
        lattice_angle_vecC_vecA = 90.0;
        lattice_anis_coeff      =  1.0;
    
        disl_distr_coeff = 0.0;
        dislMaxMobility = 0.0;
        mechMaxMobility = 0.0;
        yield_state_coeff = 0.0;
        ultimate_state_coeff = 0.0;
        part_vol_fraction = 0;
        part_radius = 0;
        
        molar_mass = 0;
        threshold_stress = 0;
        
        torsion_energy_coeff = 0;
        torsion_energy_coeff_gr_bound = 0;
        torsion_energy_coeff_gb_region = 0;
                
        min_twin_temperature = 0.0;
        twinning_temperature = 0;
        
        max_prob_twinning    = 0.0;
        max_prob_recryst = 0;
        min_disl_density = 0;
        
      //  low_tempr_recryst  = 0;
      //  high_tempr_recryst = 0;
    }

    /** This constructor is intended for creation UIMaterial by
     * assignment values of parameter UIMateriatopl from existing UIMaterial parameters
     * @param some_uimaterial - copy of UIMaterial
     */
    public UIMaterial(UIMaterial some_uimaterial)
    {
        System.out.println("UIMaterial: constructor(UIMaterial some_uimaterial) creation start");

        // assignment double values of existing parameters
        mod_elast              = some_uimaterial.getModElast();
        density                = some_uimaterial.getDensity();
        heatExpansionCoeff     = some_uimaterial.getHeatExpansionCoeff();
        yieldStrain            = some_uimaterial.getYieldStrain();
        ultimateStrain         = some_uimaterial.getUltimateStrain();
        energy_coeff           = some_uimaterial.getEnergyCoeff();
        specific_heat_capacity = some_uimaterial.getSpecificHeatCapacity();
        thermal_conductivity   = some_uimaterial.getThermalConductivity();
        thermal_conduct_bound  = some_uimaterial.getThermalConductBound();
        
        phonon_portion       = some_uimaterial.getPhononPortion();
        lowTemperThrValue    = some_uimaterial.getLowTemperThrValue();
        highTemperThrValue   = some_uimaterial.getHighTemperThrValue();
        actEnergy            = some_uimaterial.getActEnergy();
        angleLimitHAGB       = some_uimaterial.getAngleLimitHAGB();
        energyHAGB           = some_uimaterial.getEnergyHAGB();
        maxMobility          = some_uimaterial.getMaxMobility();
        mod_shear            = some_uimaterial.getModShear();
        
        disl_distr_coeff     = some_uimaterial.getDislDistrCoeff();
        dislMaxMobility      = some_uimaterial.getDislMaxMobility();
        mechMaxMobility      = some_uimaterial.getMechMaxMobility();
        yield_state_coeff    = some_uimaterial.getYieldStateCoeff();
        ultimate_state_coeff = some_uimaterial.getUltimateStateCoeff();
        part_vol_fraction    = some_uimaterial.getPartVolFraction();
        part_radius          = some_uimaterial.getPartRadius();
        
        molar_mass           = some_uimaterial.getMolarMass();
        threshold_stress     = some_uimaterial.getThresholdStress();
        
        torsion_energy_coeff           = some_uimaterial.getTorsionEnergyCoeff();
        torsion_energy_coeff_gr_bound  = some_uimaterial.getTorsionEnergyCoeffForGrBoundary();
        torsion_energy_coeff_gb_region = some_uimaterial.getTorsionEnergyCoeffForGrBoundRegion();
        
        lattice_parameter       = some_uimaterial.getLatticeParameter();
        
        lattice_vector_A_length = some_uimaterial.getLatticeVector_A_Length();
        lattice_vector_B_length = some_uimaterial.getLatticeVector_B_Length();
        lattice_vector_C_length = some_uimaterial.getLatticeVector_C_Length();
        
        lattice_angle_vecA_vecB = some_uimaterial.getLatticeAngle_vecA_vecB();
        lattice_angle_vecB_vecC = some_uimaterial.getLatticeAngle_vecB_vecC();
        lattice_angle_vecC_vecA = some_uimaterial.getLatticeAngle_vecC_vecA();
        
        lattice_anis_coeff      = some_uimaterial.getLatticeAnisCoeff();
        
        min_twin_temperature = some_uimaterial.getMinTwinTemperature();
        twinning_temperature = some_uimaterial.getTwinningTemperature();
        
        max_prob_recryst     = some_uimaterial.getMaxProbRecryst();
        max_prob_twinning    = some_uimaterial.getMaxProbTwinning();
        min_disl_density     = some_uimaterial.getMinDislDensity();
        
      //  low_tempr_recryst  = some_uimaterial.getLowTemprRecryst();
      //  high_tempr_recryst = some_uimaterial.getHighTemprRecryst();
    }

    /** This constructor is intended for creating UIMaterial data bank with
     * loading value of parameters from file in specific
     * directory "material_file_name"
     * @param material_file_name - directory and name of readed file
     */
    public UIMaterial(String material_file_name)
    {
        System.out.println("UIMaterial: constructor(String material_file_name) creation start");

        /** Read file in specific directory "material_file_name"
         */
        read(material_file_name);
    }
    
    /** This method sets the value of torsion energy coefficient
     * @return the value of torsion energy coefficient
     */
    public double getTorsionEnergyCoeff()
    {
        return torsion_energy_coeff;
    }
    
    /** This method sets the value of torsion energy coefficient
     * @param value - the value of torsion energy coefficient
     */
    public void setTorsionEnergyCoeff(double value)
    {
        torsion_energy_coeff = value;
    }
            
    /** This method sets the value of torsion energy coefficient for grain boundary
     * @return the value of torsion energy coefficient for grain boundary
     */
    public double getTorsionEnergyCoeffForGrBoundary()
    {
        return torsion_energy_coeff_gr_bound;
    }
    
    /** This method sets the value of torsion energy coefficient for grain boundary
     * @param value the value of torsion energy coefficient for grain boundary
     */
    public void setTorsionEnergyCoeffForGrBoundary(double value)
    {
        torsion_energy_coeff_gr_bound = value;
    }
    
    /** This method sets the value of torsion energy coefficient for grain boundary region
     * @param value - the value of torsion energy coefficient for grain boundary region
     */
    public double getTorsionEnergyCoeffForGrBoundRegion()
    {
        return torsion_energy_coeff_gb_region;
    }
    
    /** This method sets the value of torsion energy coefficient for grain boundary region
     * @param value the value of torsion energy coefficient for grain boundary region
     */
    public void setTorsionEnergyCoeffForGrBoundRegion(double value)
    {
        torsion_energy_coeff_gb_region = value;
    }
    
    /** This method sets the value of molar mass
     * @return the value of molar mass
     */
    public double getMolarMass()
    {
        return molar_mass;
    }
    
    /** This method sets the value of molar mass
     * @param value - the value of molar mass
     */
    public void setMolarMass(double value)
    {
        molar_mass = value;
    }
    
     /** This method sets the value of threshold stress
     * @return the value of threshold stress
     */
    public double getThresholdStress()
    {
        return threshold_stress;
    }
    
    /** This method sets the value of threshold stress
     * @param value - the value of threshold stress
     */
    public void setThresholdStress(double value)
    {
        threshold_stress = value;
    }
    
    
    /** This method sets the value of modulus of elasticity
     * @return the value of modulus of elasticity
     */
    public double getModElast()
    {
        return mod_elast;
    }
    
    /** This method is intended for set new value of parameter "mod elast"
     * @param value - new value of mod elast
     */
    public void setModElast(double value)
    {
        mod_elast = value;
    }

    /** This method is intended for set new value of parameter "yield_state_coeff"
     * @param value - new value of yield_state_coeff
     */
    public void setYieldStateCoeff(double value)
    {
        yield_state_coeff = value;
    }

    /** This method sets the value of the ultimate state coefficient.
     * @param value the ultimate state coefficient
     */
    public void setUltimateStateCoeff(double value)
    {
        ultimate_state_coeff = value;
    }
        
    /** This method sets the value of the particle volume fraction.
     * @param value the particle volume fraction
     */
    public void setPartVolFraction(double value)
    {
        part_vol_fraction = value;
    }    
       
    /** This method sets the value of the particle radius.
     * @param value the particle radius
     */
    public void setPartRadius(double value)
    {
        part_radius = value;
    }

    /** This method is intended for set new value of parameter "density"
     * @param value - new value of density
     */
    public void setDensity(double value)
    {
        density = value;
    }

    /** This method is intended for set new value of parameter "heatExpansionCoeff"
     * @param value - new value of heatExpansionCoeff
     */
    public void setHeatExpansionCoeff(double value)
    {
        heatExpansionCoeff = value;
    }

    /** This method is intended for set new value of parameter "yieldStrain"
     * @param value - new value of yieldStrain
     */
    public void setYieldStrain(double value)
    {
        yieldStrain = value;
    }

    /** This method is intended for set new value of parameter "ultimateStrain"
     * @param value - new value of ultimateStrain
     */
    public void setUltimateStrain(double value)
    {
        ultimateStrain = value;
    }

    /** This method is intended for set new value of parameter "energy coeff"
     * @param value - new value of energy coeff
     */
    public void setEnergyCoeff(double value)
    {
        energy_coeff = value;
    }

    /** This method is intended for set new value of parameter "specific heat capacity"
     * @param value - new value of specific heat capacity
     */
    public void setSpecificHeatCapacity(double value)
    {
        specific_heat_capacity = value;
    }

    /** This method is intended for set new value of parameter "thermal conductivity"
     * @param value - new value of thermal conductivity
     */
    public void setThermalConductivity(double value)
    {
        thermal_conductivity = value;
    }

    /** This method is intended for set new value of parameter "thermal conduct bound"
     * @param value - new value of thermal conduct bound
     */
    public void setThermalConductBound(double value)
    {
        thermal_conduct_bound = value;
    }

    /** This method is intended for set new value of parameter "phonon portion"
     * @param value - new value of phonon portion
     */
    public void setPhononPortion(double value)
    {
        phonon_portion = value;
    }

    /** This method is intended for set new value of parameter "lowTemperThrValue"
     * @param value - new value of lowTemperThrValue
     */
    public void setLowTemperThrValue(double value)
    {
        lowTemperThrValue = value;
    }

    /** This method is intended for set new value of parameter "highTemperThrValue"
     * @param value - new value ofhighTemperThrValue
     */
    public void setHighTemperThrValue(double value)
    {
        highTemperThrValue = value;
    }

    /** This method is intended for set new value of parameter "actEnergy"
     * @param value - new value of actEnergy
     */
    public void setActEnergy(double value)
    {
        actEnergy = value;
    }

    /** This method is intended for set new value of parameter "angleLimitHAGB"
     * @param value - new value of angleLimitHAGB
     */
    public void setAngleLimitHAGB(double value)
    {
        angleLimitHAGB = value;
    }

    /** This method is intended for set new value of parameter "energyHAGB"
     * @param value - new value of energyHAGB
     */
    public void setEnergyHAGB(double value)
    {
        energyHAGB = value;
    }

    /** This method is intended for set new value of parameter "maxMobility"
     * @param value - new value of maxMobility
     */
    public void setMaxMobility(double value)
    {
        maxMobility = value;
    }

    /** This method is intended for set new value of parameter "mod shear"
     * @param value - new value of mod shear
     */
    public void setModShear(double value)
    {
        mod_shear = value;
    }

    /** This method is intended for set new value of parameter "lattice parameter"
     * @param value - new value of lattice parameter
     */
    public void setLatticeParameter(double value)
    {
        lattice_parameter = value;
    }
    
    public void setLatticeVector_A_Length(double value)
    {
        lattice_vector_A_length = value;
    }
    
    public void setLatticeVector_B_Length(double value)
    {
        lattice_vector_B_length = value;
    }
    
    public void setLatticeVector_C_Length(double value)
    {
        lattice_vector_C_length = value;
    }
    
    public void setLatticeAngle_vecA_vecB(double value)
    {
        lattice_angle_vecA_vecB = value;
    }
    
    public void setLatticeAngle_vecB_vecC(double value)
    {
        lattice_angle_vecB_vecC = value;
    }
    
    public void setLatticeAngle_vecC_vecA(double value)
    {
        lattice_angle_vecC_vecA = value;
    }
    
    public void setLatticeAnisCoeff(double value)
    {  
        lattice_anis_coeff = value;
    }
      
    public void setMinTwinTemperature(double value)
    {
        min_twin_temperature = value;
    }
        
    public void setTwinningTemperature(double value)
    {
        twinning_temperature = value;
    }
    
    public void setMaxProbTwinning(double value)
    {
        max_prob_twinning = value;
    }
    
    public void setMaxProbRecryst(double value)
    {
        max_prob_recryst = value;
    }
    
    public void setMinDislDensity(double value)
    {
        min_disl_density = value;
    }

    /** This method is intended for set new value of parameter "disl distr coeff"
     * @param value - new value of disl distr coeff
     */
    public void setDislDistrCoeff(double value)
    {
        disl_distr_coeff = value;
    }

    /** Set new value of parameter "disl max mobility"
     * @param value - new value of disl max mobility
     */
    public void setDislMaxMobility(double value)
    {
        dislMaxMobility = value;
    }

    /** Set new value of parameter "mech max mobility"
     * @param value - new value of mech max mobility
     */
    public void setMechMaxMobility(double value)
    {
        mechMaxMobility = value;
    }

    /** This method is intended for get value of parameter "yield_state_coeff"
     */
    public double getYieldStateCoeff()
    {
        return yield_state_coeff;
    }

    /** This method is intended for get value of parameter "ultimate_state_coeff"
     */
    public double getUltimateStateCoeff()
    {
        return ultimate_state_coeff;
    }

    /** This method returns the value of the particle volume fraction.
     * @return the particle volume fraction
     */
    public double getPartVolFraction()
    {
        return part_vol_fraction;
    }

    /** This method returns the value of the particle radius.
     * @return the particle radius
     */
    public double getPartRadius()
    {
        return part_radius;
    }

    /** This method is intended for get value of parameter "density"
     */
    public double getDensity()
    {
        return density;
    }

    /** This method is intended for get value of parameter "heatExpansionCoeff"
     */
    public double getHeatExpansionCoeff()
    {
        return heatExpansionCoeff;
    }

    /*
     * This method is intended for get value of parameter "yieldStrain"
     */

    public double getYieldStrain()
    {
        return yieldStrain;
    }

    /*
     * This method is intended for get value of parameter "ultimateStrain"
     */

    public double getUltimateStrain()
    {
        return ultimateStrain;
    }

    /*
     * This method is intended for get value of parameter "energy coeff"
     */

    public double getEnergyCoeff()
    {
        return energy_coeff;
    }

    /*
     * This method is intended for get value of parameter "specific heat capacity"
     */

    public double getSpecificHeatCapacity()
    {
        return specific_heat_capacity;
    }

    /*
     * This method is intended for get value of parameter "thermal conductivity"
     */

    public double getThermalConductivity()
    {
        return thermal_conductivity;
    }

    /*
     * This method is intended for get value of parameter "thermal conduct bound"
     */

    public double getThermalConductBound()
    {
        return thermal_conduct_bound;
    }

    /*
     * This method is intended for get value of parameter "phonon portion"
     */

    public double getPhononPortion()
    {
        return phonon_portion;
    }

    /*
     * This method is intended for get value of parameter "lowTemperThrValue"
     */

    public double getLowTemperThrValue()
    {
        return lowTemperThrValue;
    }

    /*
     * This method is intended for get value of parameter "highTemperThrValue"
     */

    public double getHighTemperThrValue()
    {
        return highTemperThrValue;
    }

    /*
     * This method is intended for get value of parameter "actEnergy"
     */

    public double getActEnergy()
    {
        return actEnergy;
    }

    /*
     * This method is intended for get value of parameter "angleLimitHAGB"
     */

    public double getAngleLimitHAGB()
    {
        return angleLimitHAGB;
    }

    /*
     * This method is intended for get value of parameter "energyHAGB"
     */

    public double getEnergyHAGB()
    {
        return energyHAGB;
    }

    /*
     * This method is intended for get value of parameter "maxMobility"
     */

    public double getMaxMobility()
    {
        return maxMobility;
    }

    /*
     * This method is intended for get value of parameter "mod shear"
     */

    public double getModShear()
    {
        return mod_shear;
    }

    /** This method is intended for get value of parameter "disl distr coeff"
     */
    public double getDislDistrCoeff()
    {
        return disl_distr_coeff;
    }

    /** Get value of dislMaxMobility
     * @return - dislMaxMobility
     */
    public double getDislMaxMobility()
    {
        return dislMaxMobility;
    }

    /** Get value of mechMaxMobility
     * @return - value of mechMaxMobility
     */
    public double getMechMaxMobility()
    {
        return mechMaxMobility;
    }
    
    /** This method is intended for get value of parameter "lattice parameter"
     */
    public double getLatticeParameter()
    {
        return lattice_parameter;
    }
    
    public double getLatticeVector_A_Length()
    {
        return lattice_vector_A_length;
    }
    
    public double getLatticeVector_B_Length()
    {
        return lattice_vector_B_length;
    }
    
    public double getLatticeVector_C_Length()
    {
        return lattice_vector_C_length;
    }
    
    public double getLatticeAngle_vecA_vecB()
    {
        return lattice_angle_vecA_vecB;
    }
    
    public double getLatticeAngle_vecB_vecC()
    {
        return lattice_angle_vecB_vecC;
    }
    
    public double getLatticeAngle_vecC_vecA()
    {
        return lattice_angle_vecC_vecA;
    }
    
    public double getLatticeAnisCoeff()
    {  
        return lattice_anis_coeff;
    }
    
    public double getMinTwinTemperature()
    {
        return min_twin_temperature;
    }
    
    public double getTwinningTemperature()
    {
        return twinning_temperature;
    }
    
    public double getMaxProbRecryst()
    {
        return max_prob_recryst;
    }
    
    public double getMaxProbTwinning()
    {
        return max_prob_twinning;
    }
    
    public double getMinDislDensity()
    {
        return min_disl_density;
    }

    /** This method is intended for loading value of parameters
     * from file in specific directory with
     * specific name "material_file_name"
     * @param material_file_name - directory and name of readed file
     */
    public void read(String material_file_name)
    {
        // read data from file
        
        Properties material_properties = new Properties();
        try
        {        
            FileInputStream load_material = new FileInputStream(material_file_name);
            material_properties.load(load_material);
            load_material.close();
        }
        catch(IOException io_ex)
        {
            System.out.println(UICommon.CATCH_ERROR__NAME+io_ex);
        }

        /*
         * read value of parameters from file in directory "file_directory"
         */

        mod_elast = Double.valueOf(material_properties.getProperty(UICommon.MOD_ELAST_PROPERTIES)).doubleValue();
        density = Double.valueOf(material_properties.getProperty(UICommon.DENSITY_PROPERTIES)).doubleValue();
        heatExpansionCoeff = Double.valueOf(material_properties.getProperty(UICommon.HEAT_EXPANSION_COEFF_PROPERTIES)).doubleValue();
        yieldStrain = Double.valueOf(material_properties.getProperty(UICommon.YIELD_STRAIN_PROPERTIES)).doubleValue();
        ultimateStrain = Double.valueOf(material_properties.getProperty(UICommon.ULTIMATE_STRAIN_PROPERTIES)).doubleValue();
        energy_coeff = Double.valueOf(material_properties.getProperty(UICommon.ENERGY_COEFF_PROPERTIES)).doubleValue();
        specific_heat_capacity = Double.valueOf(material_properties.getProperty(UICommon.SPECIFIC_HEAT_CAPACITY_PROPERTIES)).doubleValue();
        thermal_conductivity = Double.valueOf(material_properties.getProperty(UICommon.THERMAL_CONDUCTIVITY_PROPERTIES)).doubleValue();
        thermal_conduct_bound = Double.valueOf(material_properties.getProperty(UICommon.THERMAL_CONDUCT_BOUND_PROPERTIES)).doubleValue();
        phonon_portion = Double.valueOf(material_properties.getProperty(UICommon.PHONON_PORTION_PROPERTIES)).doubleValue();
        lowTemperThrValue = Double.valueOf(material_properties.getProperty(UICommon.LOW_TEMPER_THR_VALUE_PROPERTIES)).doubleValue();
        highTemperThrValue = Double.valueOf(material_properties.getProperty(UICommon.HIGH_TEMPER_THR_VALUE_PROPERTIES)).doubleValue();
        actEnergy = Double.valueOf(material_properties.getProperty(UICommon.ACT_ENERGY_PROPERTIES)).doubleValue();
        angleLimitHAGB = Double.valueOf(material_properties.getProperty(UICommon.ANGLE_LIMIT_HAGB_PROPERTIES)).doubleValue();
        energyHAGB = Double.valueOf(material_properties.getProperty(UICommon.ENERGY_HAGB_PROPERTIES)).doubleValue();
        maxMobility = Double.valueOf(material_properties.getProperty(UICommon.MAX_MOBILITY_PROPERTIES)).doubleValue();
        mod_shear = Double.valueOf(material_properties.getProperty(UICommon.MOD_SHEAR_PROPERTIES)).doubleValue();
        
        disl_distr_coeff = Double.valueOf(material_properties.getProperty(UICommon.DISL_DISTR_COEFF_PROPERTIES)).doubleValue();
        dislMaxMobility = Double.valueOf(material_properties.getProperty(UICommon.DISL_MAX_MOBILITY_PROPERTIES)).doubleValue();
        mechMaxMobility = Double.valueOf(material_properties.getProperty(UICommon.MECH_MAX_MOBILITY_PROPERTIES)).doubleValue();
        yield_state_coeff = Double.valueOf(material_properties.getProperty(UICommon.YIELD_STATE_COEFF_PROPERTIES)).doubleValue();
        ultimate_state_coeff = Double.valueOf(material_properties.getProperty(UICommon.ULTIMATE_STATE_COEFF_PROPERTIES)).doubleValue();
        part_vol_fraction = Double.valueOf(material_properties.getProperty(UICommon.PART_VOL_FRACTION_PROPERTIES)).doubleValue();
        part_radius = Double.valueOf(material_properties.getProperty(UICommon.PART_RADIUS_PROPERTIES)).doubleValue();
        
        molar_mass = Double.valueOf(material_properties.getProperty(UICommon.MOLAR_MASS_PROPERTIES)).doubleValue();
        threshold_stress = Double.valueOf(material_properties.getProperty(UICommon.THRESHOLD_STRESS_PROPERTIES)).doubleValue();
        
        torsion_energy_coeff = Double.valueOf(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_PROPERTIES)).doubleValue();
        torsion_energy_coeff_gr_bound = Double.valueOf(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_GB_1_PROPERTIES)).doubleValue();
        torsion_energy_coeff_gb_region = Double.valueOf(material_properties.getProperty(UICommon.TORSION_ENERGY_COEFF_GB_2_PROPERTIES)).doubleValue();
        
        lattice_parameter       = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_PARAMETER_PROPERTIES)).doubleValue();
        
        lattice_vector_A_length = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_VECTOR_A_LENGTH_PROPERTIES)).doubleValue();
        lattice_vector_B_length = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_VECTOR_B_LENGTH_PROPERTIES)).doubleValue();
        lattice_vector_C_length = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_VECTOR_C_LENGTH_PROPERTIES)).doubleValue();
         
        lattice_angle_vecA_vecB = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_A_VEC_B_PROPERTIES)).doubleValue();
        lattice_angle_vecB_vecC = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_B_VEC_C_PROPERTIES)).doubleValue();
        lattice_angle_vecC_vecA = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_ANGLE_VEC_C_VEC_A_PROPERTIES)).doubleValue();
        
        lattice_anis_coeff      = Double.valueOf(material_properties.getProperty(UICommon.LATTICE_ANISOTROPY_COEFF_PROPERTIES)).doubleValue();
        
        min_twin_temperature    = Double.valueOf(material_properties.getProperty(UICommon.MIN_TWIN_TEMPERATURE_PROPERTIES)).doubleValue();
        twinning_temperature    = Double.valueOf(material_properties.getProperty(UICommon.TWINNING_TEMPERATURE_PROPERTIES)).doubleValue();
        
        max_prob_recryst        = Double.valueOf(material_properties.getProperty(UICommon.MAXIMAL_PROB_RECRYST_PROPERTIES)).doubleValue();
        max_prob_twinning       = Double.valueOf(material_properties.getProperty(UICommon.MAXIMAL_PROB_TWINNING_PROPERTIES)).doubleValue();
        min_disl_density        = Double.valueOf(material_properties.getProperty(UICommon.MINIMAL_DISL_DENSITY_PROPERTIES)).doubleValue();
    }
}