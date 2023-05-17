/*
 * @(#) UIBoundaryCondition.java version 1.0.0;       May 15 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of boundary condition data, wich contain default value of
 * boundary condition parameters of default side's checked positions.
 * This class can load data from file in specific directory.
 * This class can set and get value of parameters of side's checked positions.
 *
 *=============================================================
 *  Last changes :
 *          4 March 2010 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.1.1
 *          Edit: 3 new fields corresponding for type of interaction of
 *          boundary cells with neighbours:
 *          # 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
 *          # 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
 *          # 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
 */

/** Class for creation bank of boundary condition data, wich contain default value of
 *  boundary condition parameters of default side's checked positions.
 *  This class can load data from file in specific directory.
 *  This class can set and get value of parameters of side's checked positions.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - May 2009
 */

package interf;

import java.io.*;
import java.util.Properties;
import util.*;
// import javax.swing.*;

public class UIBoundaryCondition
{
    /** These fields are intended for creation of double values
     * of boundary condition parameters 
     * ("Mechanical loading", "Thermal influx" and periods of loading and relaxation) 
     * for corresponding boundary facet ("Top", "Bottom","Left","Right","Front","Back").
     */
    protected double top_mechanical_loading,    top_thermal_influx,    top_bound_load_time_portion,    top_bound_relax_time_portion;
    protected double bottom_mechanical_loading, bottom_thermal_influx, bottom_bound_load_time_portion, bottom_bound_relax_time_portion;
    protected double left_mechanical_loading,   left_thermal_influx,   left_bound_load_time_portion,   left_bound_relax_time_portion;
    protected double right_mechanical_loading,  right_thermal_influx,  right_bound_load_time_portion,  right_bound_relax_time_portion;
    protected double front_mechanical_loading,  front_thermal_influx,  front_bound_load_time_portion,  front_bound_relax_time_portion;
    protected double back_mechanical_loading,   back_thermal_influx,   back_bound_load_time_portion,   back_bound_relax_time_portion;
    
    protected double top_min_mechanical_loading,    top_min_thermal_influx;
    protected double bottom_min_mechanical_loading, bottom_min_thermal_influx;
    protected double left_min_mechanical_loading,   left_min_thermal_influx;
    protected double right_min_mechanical_loading,  right_min_thermal_influx;
    protected double front_min_mechanical_loading,  front_min_thermal_influx;
    protected double back_min_mechanical_loading,   back_min_thermal_influx;
    
    /** Types of time functions for all boundary facets
     */
    protected byte   top_bound_time_function_type,   bottom_bound_time_function_type, left_bound_time_function_type,
                     right_bound_time_function_type, front_bound_time_function_type,  back_bound_time_function_type;

    /** This field is intended for creating of boolean value to remember checked position 
     * ("Mechanical loading", "Thermal influx" and periods of loading and relaxation) 
     * at corresponding boundary facet ("Top","Bottom","Left","Right","Front","Back").
     */
    protected boolean top_check_mechanical_loading,    top_check_thermal_influx;
    protected boolean bottom_check_mechanical_loading, bottom_check_thermal_influx;
    protected boolean left_check_mechanical_loading,   left_check_thermal_influx;
    protected boolean right_check_mechanical_loading,  right_check_thermal_influx;
    protected boolean front_check_mechanical_loading,  front_check_thermal_influx;
    protected boolean back_check_mechanical_loading,   back_check_thermal_influx;

    /** This field is intended to remember chosen task types: stress or strain and temperature or energy.
     */
    protected boolean top_stress,    top_strain,    top_temperature,    top_energy;
    protected boolean bottom_stress, bottom_strain, bottom_temperature, bottom_energy;
    protected boolean left_stress,   left_strain,   left_temperature,   left_energy;
    protected boolean right_stress,  right_strain,  right_temperature,  right_energy;
    protected boolean front_stress,  front_strain,  front_temperature,  front_energy;
    protected boolean back_stress,   back_strain,   back_temperature,   back_energy;

    /** This field is intended to remember chosen material for non-adiabatic cells.
     */
    protected String top_adiabatic_material,   bottom_adiabatic_material, left_adiabatic_material;
    protected String right_adiabatic_material, front_adiabatic_material,  back_adiabatic_material;

    /** This field is intended to choose type of interaction of boundary cells with neighbours:
     * 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
     * 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
     * 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
     */
    protected int boundary_type;
    
    /** The type of boundary parameter function of cell coordinates.
     */
    protected int boundary_function_type;
    
    /** Types of tanks
     */
    protected boolean parallelepiped_tank, vertical_ellyptic_tank, horizon_triangle_tank, horizon_circle_tank;
            
    /** The constructor creates default bank of data UIBoundCondition.
     */
    public UIBoundaryCondition()
    {
        System.out.println("UIBoundaryCondition: constructor creation start");
        
        // Types of time functions for all boundary facets
        top_bound_time_function_type    = 0;
        bottom_bound_time_function_type = 0;
        left_bound_time_function_type   = 0;
        right_bound_time_function_type  = 0;
        front_bound_time_function_type  = 0;
        back_bound_time_function_type   = 0;

        // input default parameters value
        top_min_mechanical_loading      = 0.0;
        top_mechanical_loading          = 0.0;
        top_min_thermal_influx          = 0.0;
        top_thermal_influx              = 0.0;
        top_bound_load_time_portion     = 0.0;
        top_bound_relax_time_portion    = 0.0;

        bottom_min_mechanical_loading   = 0.0;
        bottom_mechanical_loading       = 0.0;
        bottom_min_thermal_influx       = 0.0;
        bottom_thermal_influx           = 0.0;
        bottom_bound_load_time_portion  = 0.0;
        bottom_bound_relax_time_portion = 0.0;

        left_min_mechanical_loading     = 0.0;
        left_mechanical_loading         = 0.0;
        left_min_thermal_influx         = 0.0;
        left_thermal_influx             = 0.0;
        left_bound_load_time_portion    = 0.0;
        left_bound_relax_time_portion   = 0.0;

        right_min_mechanical_loading    = 0.0;
        right_mechanical_loading        = 0.0;
        right_min_thermal_influx        = 0.0;
        right_thermal_influx            = 0.0;
        right_bound_load_time_portion   = 0.0;
        right_bound_relax_time_portion  = 0.0;

        front_min_mechanical_loading    = 0.0;
        front_mechanical_loading        = 0.0;
        front_min_thermal_influx        = 0.0;
        front_thermal_influx            = 0.0;
        front_bound_load_time_portion   = 0.0;
        front_bound_relax_time_portion  = 0.0;

        back_min_mechanical_loading     = 0.0;
        back_mechanical_loading         = 0.0;
        back_min_thermal_influx         = 0.0;
        back_thermal_influx             = 0.0;
        back_bound_load_time_portion    = 0.0;
        back_bound_relax_time_portion   = 0.0;

        // input default checked position for corresponding facet
        top_check_mechanical_loading          = false;
        top_check_thermal_influx              = false;

        bottom_check_mechanical_loading       = false;
        bottom_check_thermal_influx           = false;

        left_check_mechanical_loading         = false;
        left_check_thermal_influx             = false;

        right_check_mechanical_loading        = false;
        right_check_thermal_influx            = false;

        front_check_mechanical_loading        = false;
        front_check_thermal_influx            = false;

        back_check_mechanical_loading         = false;
        back_check_thermal_influx             = false;

        // Set default values for task type
        top_stress = true;
        top_strain = false;
        top_temperature = true;
        top_energy = false;

        bottom_stress = true;
        bottom_strain = false;
        bottom_temperature = true;
        bottom_energy = false;

        left_stress = true;
        left_strain = false;
        left_temperature = true;
        left_energy = false;

        right_stress = true;
        right_strain = false;
        right_temperature = true;
        right_energy = false;

        front_stress = true;
        front_strain = false;
        front_temperature = true;
        front_energy = false;

        back_stress = true;
        back_strain = false;
        back_temperature = true;
        back_energy = false;

        // Set default value defining whether a facet is adiabatic
        top_adiabatic_material    = new String();
        bottom_adiabatic_material = new String();
        left_adiabatic_material   = new String();
        right_adiabatic_material  = new String();
        front_adiabatic_material  = new String();
        back_adiabatic_material   = new String();

        /** Set default boundary type:
         * 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
         * 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
         * 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
         */
        boundary_type          = 2;
        
        // The type of boundary parameter function of cell coordinates
        boundary_function_type = 0;
        
        parallelepiped_tank = false; 
        vertical_ellyptic_tank = false; 
        horizon_triangle_tank = false; 
        horizon_circle_tank = false;
    }

    /*
     * This constructor is intended for creation UIBoundCondition by
     * assignment values of parameter UIBoundCondition from existing UIBoundCondition parameters
     * @param some_uiboundarycondition - existed boundary condition data bank
     */

    public UIBoundaryCondition(UIBoundaryCondition some_uiboundarycondition)
    {
        System.out.println("UIBoundaryCondition: constructor(UIBoundaryCondition some_uiboundarycondition) creation start");

        // Types of time functions for all boundary facets
        top_bound_time_function_type    = some_uiboundarycondition.getTopBoundTimeFunctionType();
        bottom_bound_time_function_type = some_uiboundarycondition.getBottomBoundTimeFunctionType();
        left_bound_time_function_type   = some_uiboundarycondition.getLeftBoundTimeFunctionType();
        right_bound_time_function_type  = some_uiboundarycondition.getRightBoundTimeFunctionType();
        front_bound_time_function_type  = some_uiboundarycondition.getFrontBoundTimeFunctionType();
        back_bound_time_function_type   = some_uiboundarycondition.getBackBoundTimeFunctionType();
        
        /** assignment of double values of existing parameters
         */
        top_min_mechanical_loading      = some_uiboundarycondition.getTopMinMechanicalLoading();
        top_mechanical_loading          = some_uiboundarycondition.getTopMechanicalLoading();
        top_min_thermal_influx          = some_uiboundarycondition.getTopMinThermalInflux();
        top_thermal_influx              = some_uiboundarycondition.getTopThermalInflux();
        top_bound_load_time_portion     = some_uiboundarycondition.getTopBoundLoadTimePortion();
        top_bound_relax_time_portion    = some_uiboundarycondition.getTopBoundRelaxTimePortion();

        bottom_min_mechanical_loading   = some_uiboundarycondition.getBottomMinMechanicalLoading();
        bottom_mechanical_loading       = some_uiboundarycondition.getBottomMechanicalLoading();
        bottom_min_thermal_influx       = some_uiboundarycondition.getBottomMinThermalInflux();
        bottom_thermal_influx           = some_uiboundarycondition.getBottomThermalInflux();
        bottom_bound_load_time_portion  = some_uiboundarycondition.getBottomBoundLoadTimePortion();
        bottom_bound_relax_time_portion = some_uiboundarycondition.getBottomBoundRelaxTimePortion();

        left_min_mechanical_loading     = some_uiboundarycondition.getLeftMinMechanicalLoading();
        left_mechanical_loading         = some_uiboundarycondition.getLeftMechanicalLoading();
        left_min_thermal_influx         = some_uiboundarycondition.getLeftMinThermalInflux();
        left_thermal_influx             = some_uiboundarycondition.getLeftThermalInflux();
        left_bound_load_time_portion    = some_uiboundarycondition.getLeftBoundLoadTimePortion();
        left_bound_relax_time_portion   = some_uiboundarycondition.getLeftBoundRelaxTimePortion();

        right_min_mechanical_loading    = some_uiboundarycondition.getRightMinMechanicalLoading();
        right_mechanical_loading        = some_uiboundarycondition.getRightMechanicalLoading();
        right_min_thermal_influx        = some_uiboundarycondition.getRightMinThermalInflux();
        right_thermal_influx            = some_uiboundarycondition.getRightThermalInflux();
        right_bound_load_time_portion   = some_uiboundarycondition.getRightBoundLoadTimePortion();
        right_bound_relax_time_portion  = some_uiboundarycondition.getRightBoundRelaxTimePortion();

        front_min_mechanical_loading    = some_uiboundarycondition.getFrontMinMechanicalLoading();
        front_mechanical_loading        = some_uiboundarycondition.getFrontMechanicalLoading();
        front_min_thermal_influx        = some_uiboundarycondition.getFrontMinThermalInflux();
        front_thermal_influx            = some_uiboundarycondition.getFrontThermalInflux();
        front_bound_load_time_portion   = some_uiboundarycondition.getFrontBoundLoadTimePortion();
        front_bound_relax_time_portion  = some_uiboundarycondition.getFrontBoundRelaxTimePortion();

        back_min_mechanical_loading     = some_uiboundarycondition.getBackMinMechanicalLoading();
        back_mechanical_loading         = some_uiboundarycondition.getBackMechanicalLoading();
        back_min_thermal_influx         = some_uiboundarycondition.getBackMinThermalInflux();
        back_thermal_influx             = some_uiboundarycondition.getBackThermalInflux();
        back_bound_load_time_portion    = some_uiboundarycondition.getBackBoundLoadTimePortion();
        back_bound_relax_time_portion   = some_uiboundarycondition.getBackBoundRelaxTimePortion();
        
        /** assignment of existing checked position
         */
        top_check_mechanical_loading          = some_uiboundarycondition.getTopCheckMechanicalLoading();
        top_check_thermal_influx              = some_uiboundarycondition.getTopCheckThermalInflux();

        bottom_check_mechanical_loading       = some_uiboundarycondition.getBottomCheckMechanicalLoading();
        bottom_check_thermal_influx           = some_uiboundarycondition.getBottomCheckThermalInflux();

        left_check_mechanical_loading         = some_uiboundarycondition.getLeftCheckMechanicalLoading();
        left_check_thermal_influx             = some_uiboundarycondition.getLeftCheckThermalInflux();

        right_check_mechanical_loading        = some_uiboundarycondition.getRightCheckMechanicalLoading();
        right_check_thermal_influx            = some_uiboundarycondition.getRightCheckThermalInflux();

        front_check_mechanical_loading        = some_uiboundarycondition.getFrontCheckMechanicalLoading();
        front_check_thermal_influx            = some_uiboundarycondition.getFrontCheckThermalInflux();

        back_check_mechanical_loading         = some_uiboundarycondition.getBackCheckMechanicalLoading();
        back_check_thermal_influx             = some_uiboundarycondition.getBackCheckThermalInflux();

        // assignment of values for task type
        top_stress         = some_uiboundarycondition.getTopStress();
        top_strain         = some_uiboundarycondition.getTopStrain();
        top_temperature    = some_uiboundarycondition.getTopTemperature();
        top_energy         = some_uiboundarycondition.getTopEnergy();

        bottom_stress      = some_uiboundarycondition.getBottomStress();
        bottom_strain      = some_uiboundarycondition.getBottomStrain();
        bottom_temperature = some_uiboundarycondition.getBottomTemperature();
        bottom_energy      = some_uiboundarycondition.getBottomEnergy();

        left_stress        = some_uiboundarycondition.getLeftStress();
        left_strain        = some_uiboundarycondition.getLeftStrain();
        left_temperature   = some_uiboundarycondition.getLeftTemperature();
        left_energy        = some_uiboundarycondition.getLeftEnergy();

        right_stress       = some_uiboundarycondition.getRightStress();
        right_strain       = some_uiboundarycondition.getRightStrain();
        right_temperature  = some_uiboundarycondition.getRightTemperature();
        right_energy       = some_uiboundarycondition.getRightEnergy();

        front_stress       = some_uiboundarycondition.getFrontStress();
        front_strain       = some_uiboundarycondition.getFrontStrain();
        front_temperature  = some_uiboundarycondition.getFrontTemperature();
        front_energy       = some_uiboundarycondition.getFrontEnergy();

        back_stress        = some_uiboundarycondition.getBackStress();
        back_strain        = some_uiboundarycondition.getBackStrain();
        back_temperature   = some_uiboundarycondition.getBackTemperature();
        back_energy        = some_uiboundarycondition.getBackEnergy();
        
        // Types of tanks
        parallelepiped_tank    = some_uiboundarycondition.getParallelepipedTank();
        vertical_ellyptic_tank = some_uiboundarycondition.getVerticalEllypticTank();
        horizon_triangle_tank  = some_uiboundarycondition.getHorizonTriangleTank();
        horizon_circle_tank    = some_uiboundarycondition.getHorizonCircleTank();

        /** assignment of chosen materials for non-adiabatic boundaries
         */
        top_adiabatic_material    = some_uiboundarycondition.getTopAdiabaticMaterial();
        bottom_adiabatic_material = some_uiboundarycondition.getBottomAdiabaticMaterial();
        left_adiabatic_material   = some_uiboundarycondition.getLeftAdiabaticMaterial();
        right_adiabatic_material  = some_uiboundarycondition.getRightAdiabaticMaterial();
        front_adiabatic_material  = some_uiboundarycondition.getFrontAdiabaticMaterial();
        back_adiabatic_material   = some_uiboundarycondition.getBackAdiabaticMaterial();

        // assignment of boundary type
        boundary_type             = some_uiboundarycondition.getBoundaryType();
        
        // assignment of type of boundary parameter function of cell coordinates
        boundary_function_type    = some_uiboundarycondition.getBoundaryFunctionType();
    }

    /**
     * This constructor create boundary conditions data bank by
     * loading data from file
     * @param bound_cond_file_name - directory of file
     */

    public UIBoundaryCondition(String bound_cond_file_name)
    {
        System.out.println("UIBoundaryCondition: constructor(String bound_cond_file_name) creation start");

        /*
         * Read file in specific directory "bound_cond_file_name"
         */

        read(bound_cond_file_name);
    }

    /**
     * This constructor create boundary conditions data bank by
     * loading data from file
     * @param ui_int - choosed parameters data bank (there is name of file)
     */

    public UIBoundaryCondition(UIInterface ui_int)
    {
        System.out.println("UIBoundaryCondition: constructor(UIInterface ui_int) creation start");

        /*
         * Load common boundary parameters
         */

        read(Common.BOUND_COND_PATH+"/"+ui_int.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_int.getBoundCondPath()+"."+Common.BOUND_COND_EXTENSION);

        /*
         * Load data about adiabatic cell or not
         */

        read2(ui_int);
    }

    /*
     * Set boundary type
     * @param value - boundary type:
     * # 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
     * # 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
     * # 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
     */

    public void setBoundaryType(int value)
    {
        boundary_type = value;
    }    
    
    /** The method sets boundary function type.
     * @param value - boundary function type
     */
    public void setBoundaryFunctionType(int value)
    {
        boundary_function_type = value;
    }

    /*
     * Set top "stress" radio button selected or not
     * @param value - selected or not
     */

    public void setTopStress(boolean value)
    {
        top_stress = value;
    }

    // Some new + some new

    /*
     * Set top "strain" radio button selected or not
     * @param value - selected or not
     */

    public void setTopStrain(boolean value)
    {
        top_strain = value;
    }

    /*
     * Set top "temperature" radio button selected or not
     * @param value - selected or not
     */

    public void setTopTemperature(boolean value)
    {
        top_temperature = value;
    }

    /*
     * Set top "energy" radio button selected or not
     * @param value - selected or not
     */

    public void setTopEnergy(boolean value)
    {
        top_energy = value;
    }

    /*
     * Set bottom "stress" radio button selected or not
     * @param value - selected or not
     */

    public void setBottomStress(boolean value)
    {
        bottom_stress = value;
    }

    /*
     * Set bottom "strain" radio button selected or not
     * @param value - selected or not
     */

    public void setBottomStrain(boolean value)
    {
        bottom_strain = value;
    }

    /*
     * Set bottom "temperature" radio button selected or not
     * @param value - selected or not
     */

    public void setBottomTemperature(boolean value)
    {
        bottom_temperature = value;
    }

    /*
     * Set bottom "energy" radio button selected or not
     * @param value - selected or not
     */

    public void setBottomEnergy(boolean value)
    {
        bottom_energy = value;
    }

    /*
     * Set left "stress" radio button selected or not
     * @param value - selected or not
     */

    public void setLeftStress(boolean value)
    {
        left_stress = value;
    }

    /*
     * Set left "strain" radio button selected or not
     * @param value - selected or not
     */

    public void setLeftStrain(boolean value)
    {
        left_strain = value;
    }

    /*
     * Set left "temperature" radio button selected or not
     * @param value - selected or not
     */

    public void setLeftTemperature(boolean value)
    {
        left_temperature = value;
    }

    /*
     * Set left "energy" radio button selected or not
     * @param value - selected or not
     */

    public void setLeftEnergy(boolean value)
    {
        left_energy = value;
    }

    /*
     * Set right "stress" radio button selected or not
     * @param value - selected or not
     */

    public void setRightStress(boolean value)
    {
        right_stress = value;
    }

    /*
     * Set right "strain" radio button selected or not
     * @param value - selected or not
     */

    public void setRightStrain(boolean value)
    {
        right_strain = value;
    }

    /*
     * Set right "temperature" radio button selected or not
     * @param value - selected or not
     */

    public void setRightTemperature(boolean value)
    {
        right_temperature = value;
    }

    /*
     * Set right "energy" radio button selected or not
     * @param value - selected or not
     */

    public void setRightEnergy(boolean value)
    {
        right_energy = value;
    }

    /*
     * Set front "stress" radio button selected or not
     * @param value - selected or not
     */

    public void setFrontStress(boolean value)
    {
        front_stress = value;
    }

    /*
     * Set front "strain" radio button selected or not
     * @param value - selected or not
     */

    public void setFrontStrain(boolean value)
    {
        front_strain = value;
    }

    /*
     * Set front "temperature" radio button selected or not
     * @param value - selected or not
     */

    public void setFrontTemperature(boolean value)
    {
        front_temperature = value;
    }

    /*
     * Set front "energy" radio button selected or not
     * @param value - selected or not
     */

    public void setFrontEnergy(boolean value)
    {
        front_energy = value;
    }

    /*
     * Set back "stress" radio button selected or not
     * @param value - selected or not
     */

    public void setBackStress(boolean value)
    {
        back_stress = value;
    }

    /*
     * Set back "strain" radio button selected or not
     * @param value - selected or not
     */

    public void setBackStrain(boolean value)
    {
        back_strain = value;
    }

    /*
     * Set back "temperature" radio button selected or not
     * @param value - selected or not
     */

    public void setBackTemperature(boolean value)
    {
        back_temperature = value;
    }

    /*
     * Set back "energy" radio button selected or not
     * @param value - selected or not
     */

    public void setBackEnergy(boolean value)
    {
        back_energy = value;
    }
    
    public void setParallelepipedTank(boolean value)
    {
        parallelepiped_tank = value;
    }
    
    public void setVerticalEllypticTank(boolean value)
    {
        vertical_ellyptic_tank = value;
    }
    
    public void setHorizonTriangleTank(boolean value)
    {
        horizon_triangle_tank = value;
    }
    
    public void setHorizonCircleTank(boolean value)
    {
        horizon_circle_tank = value;
    }
    
    /*
     * Give boundary type.
     * @return - boundary type:
     * # 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
     * # 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
     * # 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
     */

    public int getBoundaryType()
    {
        return boundary_type;
    }
    
    /** The method returns boundary function type.
     * @param value - boundary function type
     */
    public int getBoundaryFunctionType()
    {
        return boundary_function_type;
    }

    /*
     * Get information about top "stress" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getTopStress()
    {
        return top_stress;
    }

    /*
     * Get information about top "strain" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getTopStrain()
    {
        return top_strain;
    }

    /*
     * Get information about top "temperature" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getTopTemperature()
    {
        return top_temperature;
    }

    /*
     * Get information about top "energy" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getTopEnergy()
    {
        return top_energy;
    }

    /*
     * Get information about bottom "stress" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBottomStress()
    {
        return bottom_stress;
    }

    /*
     * Get information about bottom "strain" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBottomStrain()
    {
        return bottom_strain;
    }

    /*
     * Get information about bottom "temperature" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBottomTemperature()
    {
        return bottom_temperature;
    }

    /*
     * Get information about bottom "energy" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBottomEnergy()
    {
        return bottom_energy;
    }

    /*
     * Get information about left "stress" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getLeftStress()
    {
        return left_stress;
    }

    /*
     * Get information about left "strain" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getLeftStrain()
    {
        return left_strain;
    }

    /*
     * Get information about left "temperature" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getLeftTemperature()
    {
        return left_temperature;
    }

    /*
     * Get information about left "energy" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getLeftEnergy()
    {
        return left_energy;
    }

    /*
     * Get information about right "stress" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getRightStress()
    {
        return right_stress;
    }

    /*
     * Get information about right "strain" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getRightStrain()
    {
        return right_strain;
    }

    /*
     * Get information about right "temperature" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getRightTemperature()
    {
        return right_temperature;
    }

    /*
     * Get information about right "energy" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getRightEnergy()
    {
        return right_energy;
    }

    /*
     * Get information about front "stress" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getFrontStress()
    {
        return front_stress;
    }

    /*
     * Get information about front "strain" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getFrontStrain()
    {
        return front_strain;
    }

    /*
     * Get information about front "temperature" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getFrontTemperature()
    {
        return front_temperature;
    }

    /*
     * Get information about front "energy" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getFrontEnergy()
    {
        return front_energy;
    }

    /*
     * Get information about back "stress" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBackStress()
    {
        return back_stress;
    }

    /*
     * Get information about back "strain" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBackStrain()
    {
        return back_strain;
    }

    /*
     * Get information about back "temperature" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBackTemperature()
    {
        return back_temperature;
    }

    /*
     * Get information about back "energy" radio button - selected
     * it or not
     * @return - selected or not
     */

    public boolean getBackEnergy()
    {
        return back_energy;
    }

    public boolean getParallelepipedTank()
    {
        return parallelepiped_tank;
    }
    
    public boolean getVerticalEllypticTank()
    {
        return vertical_ellyptic_tank;
    }
    
    public boolean getHorizonTriangleTank()
    {
        return horizon_triangle_tank;
    }
    
    public boolean getHorizonCircleTank()
    {
        return horizon_circle_tank;
    }
    
    /** Set new value of mechanical loading at top facet
     * @param value - new value of mechanical loading
     */
    public void setTopMechanicalLoading(double value)
    {
        top_mechanical_loading = value;
    }
    
    /** Set new value of thermal influx at top facet
     * @param value - new value of thermal influx
     */
    public void setTopThermalInflux(double value)
    {
        top_thermal_influx = value;
    }    
    
    /** Set new value of minimal mechanical loading at top facet
     * @param value - new value of minimal mechanical loading
     */
    public void setTopMinMechanicalLoading(double value)
    {
        top_min_mechanical_loading = value;
    }
    
    /** Set new value of minimal thermal influx at top facet
     * @param value - new value of minimal thermal influx
     */
    public void setTopMinThermalInflux(double value)
    {
        top_min_thermal_influx = value;
    }
    
    public void setTopBoundTimeFunctionType(byte value)
    {
        top_bound_time_function_type = value;
    }
    
    public void setTopBoundLoadTimePortion(double value)
    {
        top_bound_load_time_portion = value;
    }
    
    public void setTopBoundRelaxTimePortion(double value)
    {
        top_bound_relax_time_portion = value;
    }

    /** Set new value of mechanical loading at bottom facet
     * @param value - new value of mechanical loading
     */
    public void setBottomMechanicalLoading(double value)
    {
        bottom_mechanical_loading = value;
    }

    /** Set new value of thermal influx at bottom facet
     * @param value - new value of thermal influx
     */
    public void setBottomThermalInflux(double value)
    {
        bottom_thermal_influx = value;
    }    
    
    /** Set new value of minimal mechanical loading at bottom facet
     * @param value - new value of minimal mechanical loading
     */
    public void setBottomMinMechanicalLoading(double value)
    {
        bottom_min_mechanical_loading = value;
    }

    /** Set new value of minimal thermal influx at bottom facet
     * @param value - new value of minimal thermal influx
     */
    public void setBottomMinThermalInflux(double value)
    {
        bottom_min_thermal_influx = value;
    }
    
    public void setBottomBoundTimeFunctionType(byte value)
    {
        bottom_bound_time_function_type = value;
    }
    
    public void setBottomBoundLoadTimePortion(double value)
    {
        bottom_bound_load_time_portion = value;
    }
    
    public void setBottomBoundRelaxTimePortion(double value)
    {
        bottom_bound_relax_time_portion = value;
    }

    /** Set new value of mechanical loading at left facet
     * @param value - new value of mechanical loading
     */
    public void setLeftMechanicalLoading(double value)
    {
        left_mechanical_loading = value;
    }
    
    /** Set new value of thermal influx at left facet
     * @param value - new value of thermal influx
     */
    public void setLeftThermalInflux(double value)
    {
        left_thermal_influx = value;
    }    
    
    /** Set new value of minimal mechanical loading at left facet
     * @param value - new value of minimal mechanical loading
     */
    public void setLeftMinMechanicalLoading(double value)
    {
        left_min_mechanical_loading = value;
    }
    
    /** Set new value of minimal thermal influx at left facet
     * @param value - new value of minimal thermal influx
     */
    public void setLeftMinThermalInflux(double value)
    {
        left_min_thermal_influx = value;
    }
    
    public void setLeftBoundTimeFunctionType(byte value)
    {
        left_bound_time_function_type = value;
    }
    
    public void setLeftBoundLoadTimePortion(double value)
    {
        left_bound_load_time_portion = value;
    }
    
    public void setLeftBoundRelaxTimePortion(double value)
    {
        left_bound_relax_time_portion = value;
    }

    /** Set new value of mechanical loading at right facet
     * @param value - new value of mechanical loading
     */
    public void setRightMechanicalLoading(double value)
    {
        right_mechanical_loading = value;
    }

    /** Set new value of thermal influx at right facet
     * @param value - new value of thermal influx
     */
    public void setRightThermalInflux(double value)
    {
        right_thermal_influx = value;
    }    
    
    /** Set new value of minimal mechanical loading at right facet
     * @param value - new value of minimal mechanical loading
     */
    public void setRightMinMechanicalLoading(double value)
    {
        right_min_mechanical_loading = value;
    }

    /** Set new value of minimal thermal influx at right facet
     * @param value - new value of minimal thermal influx
     */
    public void setRightMinThermalInflux(double value)
    {
        right_min_thermal_influx = value;
    }
    
    public void setRightBoundTimeFunctionType(byte value)
    {
        right_bound_time_function_type = value;
    }
    
    public void setRightBoundLoadTimePortion(double value)
    {
        right_bound_load_time_portion = value;
    }
    
    public void setRightBoundRelaxTimePortion(double value)
    {
        right_bound_relax_time_portion = value;
    }

    /** Set new value of mechanical loading at front facet
     * @param value - new value of thermal influx
     */
    public void setFrontMechanicalLoading(double value)
    {
        front_mechanical_loading = value;
    }
    
    /** Set new value of thermal influx at front facet
     * @param value - new value of thermal influx
     */
    public void setFrontThermalInflux(double value)
    {
        front_thermal_influx = value;
    }    
    
    /** Set new value of minimal mechanical loading at front facet
     * @param value - new value of minimal mechanical loading
     */
    public void setFrontMinMechanicalLoading(double value)
    {
        front_min_mechanical_loading = value;
    }
    
    /** Set new value of minimal thermal influx at front facet
     * @param value - new value of minimal thermal influx
     */
    public void setFrontMinThermalInflux(double value)
    {
        front_min_thermal_influx = value;
    }
    
    public void setFrontBoundTimeFunctionType(byte value)
    {
        front_bound_time_function_type = value;
    }
    
    public void setFrontBoundLoadTimePortion(double value)
    {
        front_bound_load_time_portion = value;
    }
    
    public void setFrontBoundRelaxTimePortion(double value)
    {
        front_bound_relax_time_portion = value;
    }

    /** Set new value of mechanical loading at back facet
     * @param value - new value of thermal influx
     */
    public void setBackMechanicalLoading(double value)
    {
        back_mechanical_loading = value;
    }

    /** Set new value of thermal influx at back facet
     * @param value - new value of thermal influx
     */
    public void setBackThermalInflux(double value)
    {
        back_thermal_influx = value;
    }
    
    /** Set new value of minimal mechanical loading at back facet
     * @param value - new value of minimal mechanical loading
     */
    public void setBackMinMechanicalLoading(double value)
    {
        back_min_mechanical_loading = value;
    }

    /** Set new value of minimal thermal influx at back facet
     * @param value - new value of minimal thermal influx
     */
    public void setBackMinThermalInflux(double value)
    {
        back_min_thermal_influx = value;
    }
    
    public void setBackBoundTimeFunctionType(byte value)
    {
        back_bound_time_function_type = value;
    }
    
    public void setBackBoundLoadTimePortion(double value)
    {
        back_bound_load_time_portion = value;
    }
    
    public void setBackBoundRelaxTimePortion(double value)
    {
        back_bound_relax_time_portion = value;
    }

    /** Set checked or not position - "mechanical loading"
     *  in side of boundary "top"
     * @param value - value which responsible for 
     * checked or not position - "mechanical loading"
     */
    public void setTopCheckMechanicalLoading(boolean value)
    {
        top_check_mechanical_loading = value;
    }

    /** Set checked or not position - "thermal influx"
     *  in side of boundary "top"
     * @param value - value which responsible for checked 
     * or not position - "thermal influx"
     */
    public void setTopCheckThermalInflux(boolean value)
    {
        top_check_thermal_influx = value;
    }

    /** Set checked or not position - "mechanical loading"
     *  in side of boundary "bottom"
     * @param value - value which responsible for checked 
     * or not position - "mechanical loading"
     */
    public void setBottomCheckMechanicalLoading(boolean value)
    {
        bottom_check_mechanical_loading = value;
    }

    /** Set checked or not position - "thermal influx"
     *  in side of boundary "bottom"
     * @param value - value which responsible for checked 
     * or not position - "thermal influx"
     */
    public void setBottomCheckThermalInflux(boolean value)
    {
        bottom_check_thermal_influx = value;
    }

    /** Set checked or not position - "mechanical loading"
     *  in side of boundary "left"
     * @param value - value which responsible for checked 
     * or not position - "mechanical loading"
     */
    public void setLeftCheckMechanicalLoading(boolean value)
    {
        left_check_mechanical_loading = value;
    }

    /** Set checked or not position - "thermal influx"
     *  in side of boundary "left"
     * @param value - value which responsible for checked 
     * or not position - "thermal infux"
     */
    public void setLeftCheckThermalInflux(boolean value)
    {
        left_check_thermal_influx = value;
    }

    /** Set checked or not position - "mechanical loading"
     *  in side of boundary "right"
     * @param value - value which responsible for checked 
     * or not position - "mechanical loading"
     */
    public void setRightCheckMechanicalLoading(boolean value)
    {
        right_check_mechanical_loading = value;
    }

    /** Set checked or not position - "thermal influx"
     *  in side of boundary "right"
     * @param value - value which responsible for checked 
     * or not position - "thermal influx"
     */
    public void setRightCheckThermalInflux(boolean value)
    {
        right_check_thermal_influx = value;
    }

    /** Set checked or not position - "mechanical loading"
     *  in side of boundary "front"
     * @param value - value which responsible for checked 
     * or not position - "mechanical loading"
     */
    public void setFrontCheckMechanicalLoading(boolean value)
    {
        front_check_mechanical_loading = value;
    }

    /** Set checked or not position - "thermal influx"
     *  in side of boundary "front"
     * @param value - value which responsible for checked 
     * or not position - "thermal influx"
     */
    public void setFrontCheckThermalInflux(boolean value)
    {
        front_check_thermal_influx = value;
    }

    /** Set checked or not position - "mechanical loading"
     *  in side of boundary "back"
     * @param value - value which responsible for checked 
     * or not position - "mechanical loading"
     */
    public void setBackCheckMechanicalLoading(boolean value)
    {
        back_check_mechanical_loading = value;
    }

    /** Set checked or not position - "thermal influx"
     *  in side of boundary "back"
     * @param value - value which responsible for checked 
     * or not position - "thermal influx"
     */
    public void setBackCheckThermalInflux(boolean value)
    {
        back_check_thermal_influx = value;
    }

    /** Set info about adiabatic top boundary or not
     * @param value - adiabatic or not
     */    
    public void setTopAdiabaticMaterial(String value)
    {
        top_adiabatic_material = value;
    }

    /** Set info about adiabatic bottom boundary or not
     * @param value - adiabatic or not
     */
    public void setBottomAdiabaticMaterial(String value)
    {
        bottom_adiabatic_material = value;
    }

    /** Set info about adiabatic left boundary or not
     * @param value - adiabatic or not
     */
    public void setLeftAdiabaticMaterial(String value)
    {
        left_adiabatic_material = value;
    }

    /** Set info about adiabatic right boundary or not
     * @param value - adiabatic or not
     */
    public void setRightAdiabaticMaterial(String value)
    {
        right_adiabatic_material = value;
    }

    /** Set info about adiabatic front boundary or not
     * @param value - adiabatic or not
     */
    public void setFrontAdiabaticMaterial(String value)
    {
        front_adiabatic_material = value;
    }

    /** Set info about adiabatic back boundary or not
     * @param value - adiabatic or not
     */
    public void setBackAdiabaticMaterial(String value)
    {
        back_adiabatic_material = value;
    }

    /** Get value of parameter "mechanical loading" in side
     * of boundary "top"
     */
    public double getTopMechanicalLoading()
    {
        return top_mechanical_loading;
    }

    /** Get value of parameter "thermal influx" in side
     * of boundary "top"
     */
    public double getTopThermalInflux()
    {
        return top_thermal_influx;
    }    
    
    /** Get value of parameter "minimal mechanical loading" in side
     * of boundary "top"
     */
    public double getTopMinMechanicalLoading()
    {
        return top_min_mechanical_loading;
    }

    /** Get value of parameter "minimal thermal influx" in side
     * of boundary "top"
     */
    public double getTopMinThermalInflux()
    {
        return top_min_thermal_influx;
    }
    
    public byte getTopBoundTimeFunctionType()
    {
        return top_bound_time_function_type;
    }
    
    public double getTopBoundLoadTimePortion()
    {
        return top_bound_load_time_portion;
    }
    
    public double getTopBoundRelaxTimePortion()
    {
        return top_bound_relax_time_portion;
    }
    
    /** Get value of parameter "mechanical loading" in side
     * of boundary "bottom"
     */
    public double getBottomMechanicalLoading()
    {
        return bottom_mechanical_loading;
    }

    /** Get value of parameter "thermal influx" in side
     * of boundary "bottom"
     */
    public double getBottomThermalInflux()
    {
        return bottom_thermal_influx;
    }
    
    /** Get value of parameter "minimal mechanical loading" in side
     * of boundary "bottom"
     */
    public double getBottomMinMechanicalLoading()
    {
        return bottom_min_mechanical_loading;
    }

    /** Get value of parameter "minimal thermal influx" in side
     * of boundary "bottom"
     */
    public double getBottomMinThermalInflux()
    {
        return bottom_min_thermal_influx;
    }
    
    public byte getBottomBoundTimeFunctionType()
    {
        return bottom_bound_time_function_type;
    }
    
    public double getBottomBoundLoadTimePortion()
    {
        return bottom_bound_load_time_portion;
    }
    
    public double getBottomBoundRelaxTimePortion()
    {
        return bottom_bound_relax_time_portion;
    }
       
    /** Get value of parameter "mechanical loading" in side
     * of boundary "left"
     */
    public double getLeftMechanicalLoading()
    {
        return left_mechanical_loading;
    }

    /** Get value of parameter "thermal influx" in side
     * of boundary "left"
     */
    public double getLeftThermalInflux()
    {
        return left_thermal_influx;
    }    
     
    /** Get value of parameter "minimal mechanical loading" in side
     * of boundary "left"
     */
    public double getLeftMinMechanicalLoading()
    {
        return left_min_mechanical_loading;
    }

    /** Get value of parameter "minimal thermal influx" in side
     * of boundary "left"
     */
    public double getLeftMinThermalInflux()
    {
        return left_min_thermal_influx;
    }
    
    public byte getLeftBoundTimeFunctionType()
    {
        return left_bound_time_function_type;
    }
    
    public double getLeftBoundLoadTimePortion()
    {
        return left_bound_load_time_portion;
    }
    
    public double getLeftBoundRelaxTimePortion()
    {
        return left_bound_relax_time_portion;
    }
      
    /** Get value of parameter "mechanical loading" in side
     * of boundary "right"
     */
    public double getRightMechanicalLoading()
    {
        return right_mechanical_loading;
    }
    
    /** Get value of parameter "thermal influx" in side
     * of boundary "right"
     */
    public double getRightThermalInflux()
    {
        return right_thermal_influx;
    }
    
     
    /** Get value of parameter "minimal mechanical loading" in side
     * of boundary "right"
     */
    public double getRightMinMechanicalLoading()
    {
        return right_min_mechanical_loading;
    }
    
    /** Get value of parameter "minimal thermal influx" in side
     * of boundary "right"
     */
    public double getRightMinThermalInflux()
    {
        return right_min_thermal_influx;
    }
    
    public byte getRightBoundTimeFunctionType()
    {
        return right_bound_time_function_type;
    }
    
    public double getRightBoundLoadTimePortion()
    {
        return right_bound_load_time_portion;
    }
    
    public double getRightBoundRelaxTimePortion()
    {
        return right_bound_relax_time_portion;
    }
       
    /** Get value of parameter "mechanical loading" in side
     * of boundary "front"
     */
    public double getFrontMechanicalLoading()
    {
        return front_mechanical_loading;
    }

    /** Get value of parameter "thermal influx" in side
     * of boundary "front"
     */
    public double getFrontThermalInflux()
    {
        return front_thermal_influx;
    }    
    
    /** Get value of parameter "minimal mechanical loading" in side
     * of boundary "front"
     */
    public double getFrontMinMechanicalLoading()
    {
        return front_min_mechanical_loading;
    }

    /** Get value of parameter "minimal thermal influx" in side
     * of boundary "front"
     */
    public double getFrontMinThermalInflux()
    {
        return front_min_thermal_influx;
    }

    public byte getFrontBoundTimeFunctionType()
    {
        return front_bound_time_function_type;
    }
    
    public double getFrontBoundLoadTimePortion()
    {
        return front_bound_load_time_portion;
    }
    
    public double getFrontBoundRelaxTimePortion()
    {
        return front_bound_relax_time_portion;
    }
       
    /** Get value of parameter "mechanical loading" in side
     * of boundary "back"
     */
    public double getBackMechanicalLoading()
    {
        return back_mechanical_loading;
    }

    /** Get value of parameter "thermal influx" in side
     * of boundary "back"
     */
    public double getBackThermalInflux()
    {
        return back_thermal_influx;
    }    
    
    /** Get value of parameter "minimal mechanical loading" in side
     * of boundary "back"
     */
    public double getBackMinMechanicalLoading()
    {
        return back_min_mechanical_loading;
    }

    /** Get value of parameter "minimal thermal influx" in side
     * of boundary "back"
     */
    public double getBackMinThermalInflux()
    {
        return back_min_thermal_influx;
    }
    
    public byte getBackBoundTimeFunctionType()
    {
        return back_bound_time_function_type;
    }
    
    public double getBackBoundLoadTimePortion()
    {
        return back_bound_load_time_portion;
    }
    
    public double getBackBoundRelaxTimePortion()
    {
        return back_bound_relax_time_portion;
    }
          
    /** Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "top"
     */
    public boolean getTopCheckMechanicalLoading()
    {
        return top_check_mechanical_loading;
    }

    /** Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "top"
     */
    public boolean getTopCheckThermalInflux()
    {
        return top_check_thermal_influx;
    }

    /** Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "bottom"
     */
    public boolean getBottomCheckMechanicalLoading()
    {
        return bottom_check_mechanical_loading;
    }

    /** Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "bottom"
     */
    public boolean getBottomCheckThermalInflux()
    {
        return bottom_check_thermal_influx;
    }

    /** Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "left"
     */
    public boolean getLeftCheckMechanicalLoading()
    {
        return left_check_mechanical_loading;
    }

    /** Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "left"
     */
    public boolean getLeftCheckThermalInflux()
    {
        return left_check_thermal_influx;
    }

    /** Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "right"
     */
    public boolean getRightCheckMechanicalLoading()
    {
        return right_check_mechanical_loading;
    }

    /** Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "right"
     */
    public boolean getRightCheckThermalInflux()
    {
        return right_check_thermal_influx;
    }

    /*
     * Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "front"
     */

    public boolean getFrontCheckMechanicalLoading()
    {
        return front_check_mechanical_loading;
    }

    /*
     * Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "front"
     */

    public boolean getFrontCheckThermalInflux()
    {
        return front_check_thermal_influx;
    }

    /*
     * Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "back"
     */

    public boolean getBackCheckMechanicalLoading()
    {
        return back_check_mechanical_loading;
    }

    /*
     * Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "back"
     */

    public boolean getBackCheckThermalInflux()
    {
        return back_check_thermal_influx;
    }

    /*
     * Get info about top boundary - adiabatic it or not
     * @return - adiabatic or not
     */

    public String getTopAdiabaticMaterial()
    {
        return top_adiabatic_material;
    }

    /*
     * Get info about bottom boundary - adiabatic it or not
     * @return - adiabatic or not
     */

    public String getBottomAdiabaticMaterial()
    {
        return bottom_adiabatic_material;
    }

    /*
     * Get info about left boundary - adiabatic it or not
     * @return - adiabatic or not
     */

    public String getLeftAdiabaticMaterial()
    {
        return left_adiabatic_material;
    }

    /*
     * Get info about right boundary - adiabatic it or not
     * @return - adiabatic or not
     */

    public String getRightAdiabaticMaterial()
    {
        return right_adiabatic_material;
    }

    // Some another new string for mercurial

    /*
     * Get info about front boundary - adiabatic it or not
     * @return - adiabatic or not
     */

    public String getFrontAdiabaticMaterial()
    {
        return front_adiabatic_material;
    }

    /*
     * Get info about back boundary - adiabatic it or not
     * @return - adiabatic or not
     */

    public String getBackAdiabaticMaterial()
    {
        return back_adiabatic_material;
    }

    /*
     * Load value of parameters
     * @param bound_cond_file_name - directory of data file
     */

    public void read(String bound_cond_file_name)
    {
        /**
         * read data from file
         */

        StringVector data = null;
        
        try
        {
            TextTableFileReader data_reader = new TextTableFileReader(bound_cond_file_name);
            data = data_reader.convertToTable();
            
            left_min_mechanical_loading     = Double.valueOf(data.getCell(0, 0)).doubleValue();
            left_mechanical_loading         = Double.valueOf(data.getCell(0, 1)).doubleValue();
            left_min_thermal_influx         = Double.valueOf(data.getCell(0, 2)).doubleValue();
            left_thermal_influx             = Double.valueOf(data.getCell(0, 3)).doubleValue();
            left_bound_time_function_type   = Byte.valueOf  (data.getCell(0, 4)).byteValue();
            left_bound_load_time_portion    = Double.valueOf(data.getCell(0, 5)).doubleValue();
            left_bound_relax_time_portion   = Double.valueOf(data.getCell(0, 6)).doubleValue();
            
            front_min_mechanical_loading    = Double.valueOf(data.getCell(1, 0)).doubleValue();
            front_mechanical_loading        = Double.valueOf(data.getCell(1, 1)).doubleValue();
            front_min_thermal_influx        = Double.valueOf(data.getCell(1, 2)).doubleValue();
            front_thermal_influx            = Double.valueOf(data.getCell(1, 3)).doubleValue();
            front_bound_time_function_type  = Byte.valueOf  (data.getCell(1, 4)).byteValue();
            front_bound_load_time_portion   = Double.valueOf(data.getCell(1, 5)).doubleValue();
            front_bound_relax_time_portion  = Double.valueOf(data.getCell(1, 6)).doubleValue();

            top_min_mechanical_loading      = Double.valueOf(data.getCell(2, 0)).doubleValue();
            top_mechanical_loading          = Double.valueOf(data.getCell(2, 1)).doubleValue();
            top_min_thermal_influx          = Double.valueOf(data.getCell(2, 2)).doubleValue();
            top_thermal_influx              = Double.valueOf(data.getCell(2, 3)).doubleValue();
            top_bound_time_function_type    = Byte.valueOf  (data.getCell(2, 4)).byteValue();
            top_bound_load_time_portion     = Double.valueOf(data.getCell(2, 5)).doubleValue();
            top_bound_relax_time_portion    = Double.valueOf(data.getCell(2, 6)).doubleValue();

            bottom_min_mechanical_loading   = Double.valueOf(data.getCell(3, 0)).doubleValue();
            bottom_mechanical_loading       = Double.valueOf(data.getCell(3, 1)).doubleValue();
            bottom_min_thermal_influx       = Double.valueOf(data.getCell(3, 2)).doubleValue();
            bottom_thermal_influx           = Double.valueOf(data.getCell(3, 3)).doubleValue();
            bottom_bound_time_function_type = Byte.valueOf  (data.getCell(3, 4)).byteValue();
            bottom_bound_load_time_portion  = Double.valueOf(data.getCell(3, 5)).doubleValue();
            bottom_bound_relax_time_portion = Double.valueOf(data.getCell(3, 6)).doubleValue();
            
            back_min_mechanical_loading     = Double.valueOf(data.getCell(4, 0)).doubleValue();
            back_mechanical_loading         = Double.valueOf(data.getCell(4, 1)).doubleValue();
            back_min_thermal_influx         = Double.valueOf(data.getCell(4, 2)).doubleValue();
            back_thermal_influx             = Double.valueOf(data.getCell(4, 3)).doubleValue();
            back_bound_time_function_type   = Byte.valueOf  (data.getCell(4, 4)).byteValue();
            back_bound_load_time_portion    = Double.valueOf(data.getCell(4, 5)).doubleValue();
            back_bound_relax_time_portion   = Double.valueOf(data.getCell(4, 6)).doubleValue();

            right_min_mechanical_loading    = Double.valueOf(data.getCell(5, 0)).doubleValue();
            right_mechanical_loading        = Double.valueOf(data.getCell(5, 1)).doubleValue();
            right_min_thermal_influx        = Double.valueOf(data.getCell(5, 2)).doubleValue();
            right_thermal_influx            = Double.valueOf(data.getCell(5, 3)).doubleValue();
            right_bound_time_function_type  = Byte.valueOf  (data.getCell(5, 4)).byteValue();
            right_bound_load_time_portion   = Double.valueOf(data.getCell(5, 5)).doubleValue();
            right_bound_relax_time_portion  = Double.valueOf(data.getCell(5, 6)).doubleValue();
        }
        catch(FileNotFoundException w)
        {}
    }

    /*
     * Load data about adiabatic boundary or not and
     * what task types is choosed
     * @param ui_int - interface data bank, which cantain data file name
     */

    public void read2(UIInterface ui_int)
    {
        /*
         * read data from file
         */
        try
        {
            StringVector materials = null;
            TextTableFileReader materials_reader = new TextTableFileReader(Common.BOUND_COND_PATH+"/"+ui_int.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_int.getBoundCondPath()+"_grains."+Common.BOUND_COND_EXTENSION);
            materials = materials_reader.convertToTable();

            // Load materials of all boundaries
            left_adiabatic_material   = materials.getCell(0, 0);
            front_adiabatic_material  = materials.getCell(1, 0);
            top_adiabatic_material    = materials.getCell(2, 0);
            bottom_adiabatic_material = materials.getCell(3, 0);
            back_adiabatic_material   = materials.getCell(4, 0);
            right_adiabatic_material  = materials.getCell(5, 0);
            
            // Load info about top boundary - was choosed
            // mechanical loading or not
            // if yes then stress or strain must be selected
            if(!materials.getCell(2, 1).equals(UICommon.N_A_R_B_NAME))
            {
                top_check_mechanical_loading = true;
                
                if(materials.getCell(2, 1).equals(UICommon.STRESS_R_B_NAME)) top_stress = true;
                if(materials.getCell(2, 1).equals(UICommon.STRAIN_R_B_NAME)) top_strain = true;
            }
            // If mechanical loading was not choosed then
            // it must be not selected field
            else
                top_check_mechanical_loading = false;

            // Load info about top boundary - was choosed
            // thermal influx or not
            // if yes then temperature or energy must be selected
            if(!materials.getCell(2, 2).equals(UICommon.N_A_R_B_NAME))
            {
                top_check_thermal_influx = true;
                
                if(materials.getCell(2, 2).equals(UICommon.TEMPERATURE_R_B_NAME)) top_temperature = true;
                if(materials.getCell(2, 2).equals(UICommon.ENERGY_R_B_NAME))      top_energy = true;
            }
            // If thrmal influx was not choosed then
            // it must be not selected field
            else
                top_check_thermal_influx = false;

            // Load info about bottom boundary - was choosed
            // mechanical loading or not
            // if yes then stress or strain must be selected
            if(!materials.getCell(3, 1).equals(UICommon.N_A_R_B_NAME))
            {
                bottom_check_mechanical_loading = true;
                if(materials.getCell(3, 1).equals(UICommon.STRESS_R_B_NAME)) bottom_stress = true;
                if(materials.getCell(3, 1).equals(UICommon.STRAIN_R_B_NAME)) bottom_strain = true;
            }

            // If mechanical loading was not choosed then
            // it must be not selected field
            else
                bottom_check_mechanical_loading = false;

            // Load info about bottom boundary - was choosed
            // thermal influx or not
            // if yes then temperature or energy must be selected
            if(!materials.getCell(3, 2).equals(UICommon.N_A_R_B_NAME))
            {
                bottom_check_thermal_influx = true;
                if(materials.getCell(3, 2).equals(UICommon.TEMPERATURE_R_B_NAME)) bottom_temperature = true;
                if(materials.getCell(3, 2).equals(UICommon.ENERGY_R_B_NAME))      bottom_energy = true;
            }
            // If thrmal influx was not choosed then
            // it must be not selected field
            else
                bottom_check_thermal_influx = false;

            // Load info about left boundary - was choosed
            // mechanical loading or not
            // if yes then stress or strain must be selected
            if(!materials.getCell(0, 1).equals(UICommon.N_A_R_B_NAME))
            {
                left_check_mechanical_loading = true;
                
                if(materials.getCell(0, 1).equals(UICommon.STRESS_R_B_NAME)) left_stress = true;
                if(materials.getCell(0, 1).equals(UICommon.STRAIN_R_B_NAME)) left_strain = true;
            }
            // If mechanical loading was not choosed then
            // it must be not selected field
            else
                left_check_mechanical_loading = false;

            // Load info about left boundary - was choosed
            // thermal influx or not
            // if yes then temperature or energy must be selected
            if(!materials.getCell(0, 2).equals(UICommon.N_A_R_B_NAME))
            {
                left_check_thermal_influx = true;
                
                if(materials.getCell(0, 2).equals(UICommon.TEMPERATURE_R_B_NAME)) left_temperature = true;
                if(materials.getCell(0, 2).equals(UICommon.ENERGY_R_B_NAME))      left_energy = true;
            }
            // If thrmal influx was not choosed then
            // it must be not selected field
            else
                left_check_thermal_influx = false;

            // Load info about right boundary - was choosed
            // mechanical loading or not
            // if yes then stress or strain must be selected
            if(!materials.getCell(5, 1).equals(UICommon.N_A_R_B_NAME))
            {
                right_check_mechanical_loading = true;
                
                if(materials.getCell(5, 1).equals(UICommon.STRESS_R_B_NAME)) right_stress = true;
                if(materials.getCell(5, 1).equals(UICommon.STRAIN_R_B_NAME)) right_strain = true;
            }
            // If mechanical loading was not choosed then
            // it must be not selected field
            else
                right_check_mechanical_loading = false;

            // Load info about right boundary - was choosed
            // thermal influx or not
            // if yes then temperature or energy must be selected
            if(!materials.getCell(5, 2).equals(UICommon.N_A_R_B_NAME))
            {
                right_check_thermal_influx = true;
                
                if(materials.getCell(5, 2).equals(UICommon.TEMPERATURE_R_B_NAME)) right_temperature = true;
                if(materials.getCell(5, 2).equals(UICommon.ENERGY_R_B_NAME))      right_energy = true;
            }
            // If thrmal influx was not choosed then
            // it must be not selected field
            else
                right_check_thermal_influx = false;

            // Load info about front boundary - was choosed
            // mechanical loading or not
            // if yes then stress or strain must be selected
            if(!materials.getCell(1, 1).equals(UICommon.N_A_R_B_NAME))
            {
                front_check_mechanical_loading = true;
                
                if(materials.getCell(1, 1).equals(UICommon.STRESS_R_B_NAME)) front_stress = true;
                if(materials.getCell(1, 1).equals(UICommon.STRAIN_R_B_NAME)) front_strain = true;
            }
            // If mechanical loading was not choosed then
            // it must be not selected field
            else
                front_check_mechanical_loading = false;

            // Load info about front boundary - was choosed
            // thermal influx or not
            // if yes then temperature or energy must be selected
            if(!materials.getCell(1, 2).equals(UICommon.N_A_R_B_NAME))
            {
                front_check_thermal_influx = true;
                
                if(materials.getCell(1, 2).equals(UICommon.TEMPERATURE_R_B_NAME)) front_temperature = true;
                if(materials.getCell(1, 2).equals(UICommon.ENERGY_R_B_NAME))      front_energy = true;
            }
            // If thrmal influx was not choosed then
            // it must be not selected field
            else
                front_check_thermal_influx = false;

            // Load info about back boundary - was choosed
            // mechanical loading or not
            // if yes then stress or strain must be selected
            if(!materials.getCell(4, 1).equals(UICommon.N_A_R_B_NAME))
            {
                back_check_mechanical_loading = true;
                
                if(materials.getCell(4, 1).equals(UICommon.STRESS_R_B_NAME)) back_stress = true;
                if(materials.getCell(4, 1).equals(UICommon.STRAIN_R_B_NAME)) back_strain = true;
            }
            // If mechanical loading was not choosed then
            // it must be not selected field
            else
                back_check_mechanical_loading = false;

            // Load info about back boundary - was choosed
            // thermal influx or not
            // if yes then temperature or energy must be selected
            if(!materials.getCell(4, 2).equals(UICommon.N_A_R_B_NAME))
            {
                back_check_thermal_influx = true;
                
                if(materials.getCell(4, 2).equals(UICommon.TEMPERATURE_R_B_NAME)) back_temperature = true;
                if(materials.getCell(4, 2).equals(UICommon.ENERGY_R_B_NAME))      back_energy = true;
            }
            // If thrmal influx was not choosed then
            // it must be not selected field            
            else
                back_check_thermal_influx = false;
        }
        catch(FileNotFoundException o)
        {}

        // Load information about boundary type:
        // # 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
        // # 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
        // # 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
        Properties loading_properties = new Properties();

        try
        {
            FileInputStream loader = new FileInputStream(Common.BOUND_COND_PATH+"/"+ui_int.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_int.getBoundCondPath()+"_"+UICommon.BOUNDARY_TYPE_PROPERTIES+"."+Common.BOUND_COND_EXTENSION);
            loading_properties.load(loader);
            loader.close();
        }
        catch(IOException io_ex)
        {
            System.out.println(UICommon.CATCH_ERROR__NAME+io_ex);
        }

        // Set boundary type
        boundary_type = Integer.valueOf(loading_properties.getProperty(UICommon.BOUNDARY_TYPE_PROPERTIES)).intValue();
        
        // Set boundary type
        boundary_function_type = Integer.valueOf(loading_properties.getProperty(UICommon.BOUNDARY_FUNCTION_TYPE_PROPERTIES)).intValue();
    }
}