/*
 * @(#) UITank.java version 1.0.0;       April 1 2011
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of tank data, wich contain default value of
 * tank parameters of default side's checked positions.
 * This class can load data from file in specific directory.
 * This class can set and get value of parameters of side's checked positions.
 *
 *=============================================================
 *  Last changes :
 *          1 April 2011 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.0
 *          Edit: Class creation
 */

/** Class for creation bank of tank data, wich contain default value of
 *  tank parameters of default side's checked positions.
 *  This class can load data from file in specific directory.
 *  This class can set and get value of parameters of side's checked positions.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - April 2011
 */

package interf;

import java.io.FileNotFoundException;
import java.io.IOException;
import util.Common;
import util.StringVector;
import util.TextTableFileReader;

public class UITank
{
    protected boolean top_check_mechanical_loading,    top_check_thermal_influx;
    protected boolean bottom_check_mechanical_loading, bottom_check_thermal_influx;
    protected boolean left_check_mechanical_loading,   left_check_thermal_influx;
    protected boolean right_check_mechanical_loading,  right_check_thermal_influx;
    protected boolean front_check_mechanical_loading,  front_check_thermal_influx;
    protected boolean back_check_mechanical_loading,   back_check_thermal_influx;    
    
    /** Types of time functions for tanks */
    byte top_time_function_type,   bottom_time_function_type, left_time_function_type,
         right_time_function_type, front_time_function_type,  back_time_function_type;
    
    /** Time portions of loading for tanks */
    double top_loading_time_portion,   bottom_loading_time_portion, left_loading_time_portion,
           right_loading_time_portion, front_loading_time_portion,  back_loading_time_portion;
    
    /** Time portions of relaxation for tanks */
    double top_relaxation_time_portion,   bottom_relaxation_time_portion, left_relaxation_time_portion,
           right_relaxation_time_portion, front_relaxation_time_portion,  back_relaxation_time_portion;    

    // Buttons for choice of tank type
    protected boolean parallelepiped_tank, vertical_ellyptic_tank, horizon_triangle_tank, horizon_circle_tank;
        
    /** Remember sizes of each side of tank
     */
    protected double top_coordinate_x_1, top_coordinate_x_2;
    protected double top_coordinate_y_1, top_coordinate_y_2;
    protected double top_coordinate_z_1, top_coordinate_z_2;
    
    protected double bottom_coordinate_x_1, bottom_coordinate_x_2;
    protected double bottom_coordinate_y_1, bottom_coordinate_y_2;
    protected double bottom_coordinate_z_1, bottom_coordinate_z_2;
    
    protected double left_coordinate_x_1, left_coordinate_x_2;
    protected double left_coordinate_y_1, left_coordinate_y_2;
    protected double left_coordinate_z_1, left_coordinate_z_2;
    
    protected double right_coordinate_x_1, right_coordinate_x_2;
    protected double right_coordinate_y_1, right_coordinate_y_2;
    protected double right_coordinate_z_1, right_coordinate_z_2;
    
    protected double front_coordinate_x_1, front_coordinate_x_2;
    protected double front_coordinate_y_1, front_coordinate_y_2;
    protected double front_coordinate_z_1, front_coordinate_z_2;
    
    protected double back_coordinate_x_1, back_coordinate_x_2;
    protected double back_coordinate_y_1, back_coordinate_y_2;
    protected double back_coordinate_z_1, back_coordinate_z_2;

    /** Remember mechanical energy type if it exists
     */
    protected boolean top_stress, top_strain, top_temperature, top_energy;
    protected boolean bottom_stress, bottom_strain, bottom_temperature, bottom_energy;
    protected boolean left_stress, left_strain, left_temperature, left_energy;
    protected boolean right_stress, right_strain, right_temperature, right_energy;
    protected boolean front_stress, front_strain, front_temperature, front_energy;
    protected boolean back_stress, back_strain, back_temperature, back_energy;

    /** This field is intended for remember choosed material for not adiabatic cells
     */
    protected String top_adiabatic_material, bottom_adiabatic_material, left_adiabatic_material;
    protected String right_adiabatic_material, front_adiabatic_material, back_adiabatic_material;

    /** Values of mechanical and thermal enegy
     */
    protected double top_mech_value,    top_therm_value;
    protected double bottom_mech_value, bottom_therm_value;
    protected double left_mech_value,   left_therm_value;
    protected double right_mech_value,  right_therm_value;
    protected double front_mech_value,  front_therm_value;
    protected double back_mech_value,   back_therm_value;
    
    /** minimal values of mechanical and thermal enegy under cycling loading
     */
    protected double min_top_mech_value,    min_top_therm_value;
    protected double min_bottom_mech_value, min_bottom_therm_value;
    protected double min_left_mech_value,   min_left_therm_value;
    protected double min_right_mech_value,  min_right_therm_value;
    protected double min_front_mech_value,  min_front_therm_value;
    protected double min_back_mech_value,   min_back_therm_value;
    
    /** Create default bank of data UIBoundCondition
     */
    public UITank()
    {
        System.out.println("UITank: constructor creation start");

        /** input default parameters value
         */
        // Minimal values
        min_top_mech_value = 0.0;
        min_top_therm_value = 0.0;
        
        min_bottom_mech_value = 0.0;
        min_bottom_therm_value = 0.0;
        
        min_left_mech_value = 0.0;
        min_left_therm_value = 0.0;
        
        min_right_mech_value = 0.0;
        min_right_therm_value = 0.0;
        
        min_front_mech_value = 0.0;
        min_front_therm_value = 0.0;
        
        min_back_mech_value = 0.0;
        min_back_therm_value = 0.0;
        
        // Maximal values
        top_mech_value = 0.0;
        top_therm_value = 0.0;
        
        bottom_mech_value = 0.0;
        bottom_therm_value = 0.0;
        
        left_mech_value = 0.0;
        left_therm_value = 0.0;
        
        right_mech_value = 0.0;
        right_therm_value = 0.0;
        
        front_mech_value = 0.0;
        front_therm_value = 0.0;
        
        back_mech_value = 0.0;
        back_therm_value = 0.0;
        
        // Set default coordinates
        top_coordinate_x_1 = 0.0;
        top_coordinate_x_2 = 0.0;
        top_coordinate_y_1 = 0.0;
        top_coordinate_y_2 = 0.0;

        bottom_coordinate_x_1 = 0.0;
        bottom_coordinate_x_2 = 0.0;
        bottom_coordinate_y_1 = 0.0;
        bottom_coordinate_y_2 = 0.0;

        left_coordinate_x_1 = 0.0;
        left_coordinate_x_2 = 0.0;
        left_coordinate_y_1 = 0.0;
        left_coordinate_y_2 = 0.0;

        right_coordinate_x_1 = 0.0;
        right_coordinate_x_2 = 0.0;
        right_coordinate_y_1 = 0.0;
        right_coordinate_y_2 = 0.0;

        front_coordinate_x_1 = 0.0;
        front_coordinate_x_2 = 0.0;
        front_coordinate_y_1 = 0.0;
        front_coordinate_y_2 = 0.0;

        back_coordinate_x_1 = 0.0;
        back_coordinate_x_2 = 0.0;
        back_coordinate_y_1 = 0.0;
        back_coordinate_y_2 = 0.0;

        /*
         * input default checked position of corrisponding side of boundary condition
         */
        top_check_mechanical_loading = false;
        top_check_thermal_influx = false;

        bottom_check_mechanical_loading = false;
        bottom_check_thermal_influx = false;

        left_check_mechanical_loading = false;
        left_check_thermal_influx = false;

        right_check_mechanical_loading = false;
        right_check_thermal_influx = false;

        front_check_mechanical_loading = false;
        front_check_thermal_influx = false;

        back_check_mechanical_loading = false;
        back_check_thermal_influx = false;
        
        // Types of tanks
        parallelepiped_tank = false; 
        vertical_ellyptic_tank = false; 
        horizon_triangle_tank = false; 
        horizon_circle_tank = false;

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

        /** Set default value about adiabatic boundary or not
         */
        top_adiabatic_material = new String("Copper");
        bottom_adiabatic_material = new String("Copper");
        left_adiabatic_material = new String("Copper");
        right_adiabatic_material = new String("Copper");
        front_adiabatic_material = new String("Copper");
        back_adiabatic_material = new String("Copper");
    }

    /** This constructor is intended for creation UIBoundCondition by
     * assignment values of parameter UIBoundCondition from existing UIBoundCondition parameters
     * @param some_uiboundarycondition - existed boundary condition data bank
     */
    public UITank(UITank some_uitank)
    {
        System.out.println("UITank: constructor(UITank some_uitank) creation start");

        /** assignment double values of existing parameters
         */
        // Minimal values
        min_top_mech_value = some_uitank.getTopMinMechValue();
        min_top_therm_value = some_uitank.getTopMinThermValue();

        min_bottom_mech_value = some_uitank.getBottomMinMechValue();
        min_bottom_therm_value = some_uitank.getBottomMinThermValue();

        min_left_mech_value = some_uitank.getLeftMinMechValue();
        min_left_therm_value = some_uitank.getLeftMinThermValue();

        min_right_mech_value = some_uitank.getRightMinMechValue();
        min_right_therm_value = some_uitank.getRightMinThermValue();

        min_front_mech_value = some_uitank.getFrontMinMechValue();
        min_front_therm_value = some_uitank.getFrontMinThermValue();

        min_back_mech_value = some_uitank.getBackMinMechValue();
        min_back_therm_value = some_uitank.getBackMinThermValue();
        
        // Maximal values
        top_mech_value = some_uitank.getTopMechValue();
        top_therm_value = some_uitank.getTopThermValue();

        bottom_mech_value = some_uitank.getBottomMechValue();
        bottom_therm_value = some_uitank.getBottomThermValue();

        left_mech_value = some_uitank.getLeftMechValue();
        left_therm_value = some_uitank.getLeftThermValue();

        right_mech_value = some_uitank.getRightMechValue();
        right_therm_value = some_uitank.getRightThermValue();

        front_mech_value = some_uitank.getFrontMechValue();
        front_therm_value = some_uitank.getFrontThermValue();

        back_mech_value = some_uitank.getBackMechValue();
        back_therm_value = some_uitank.getBackThermValue();

        // Set coordinates
        top_coordinate_x_1 = some_uitank.getTopCoordinateX1();
        top_coordinate_x_2 = some_uitank.getTopCoordinateX2();
        top_coordinate_y_1 = some_uitank.getTopCoordinateY1();
        top_coordinate_y_2 = some_uitank.getTopCoordinateY2();
        top_coordinate_z_1 = some_uitank.getTopCoordinateZ1();
        top_coordinate_z_2 = some_uitank.getTopCoordinateZ2();

        bottom_coordinate_x_1 = some_uitank.getBottomCoordinateX1();
        bottom_coordinate_x_2 = some_uitank.getBottomCoordinateX2();
        bottom_coordinate_y_1 = some_uitank.getBottomCoordinateY1();
        bottom_coordinate_y_2 = some_uitank.getBottomCoordinateY2();
        bottom_coordinate_z_1 = some_uitank.getBottomCoordinateZ1();
        bottom_coordinate_z_2 = some_uitank.getBottomCoordinateZ2();

        left_coordinate_x_1 = some_uitank.getLeftCoordinateX1();
        left_coordinate_x_2 = some_uitank.getLeftCoordinateX2();
        left_coordinate_y_1 = some_uitank.getLeftCoordinateY1();
        left_coordinate_y_2 = some_uitank.getLeftCoordinateY2();
        left_coordinate_z_1 = some_uitank.getLeftCoordinateZ1();
        left_coordinate_z_2 = some_uitank.getLeftCoordinateZ2();

        right_coordinate_x_1 = some_uitank.getRightCoordinateX1();
        right_coordinate_x_2 = some_uitank.getRightCoordinateX2();
        right_coordinate_y_1 = some_uitank.getRightCoordinateY1();
        right_coordinate_y_2 = some_uitank.getRightCoordinateY2();
        right_coordinate_z_1 = some_uitank.getRightCoordinateZ1();
        right_coordinate_z_2 = some_uitank.getRightCoordinateZ2();

        front_coordinate_x_1 = some_uitank.getFrontCoordinateX1();
        front_coordinate_x_2 = some_uitank.getFrontCoordinateX2();
        front_coordinate_y_1 = some_uitank.getFrontCoordinateY1();
        front_coordinate_y_2 = some_uitank.getFrontCoordinateY2();
        front_coordinate_z_1 = some_uitank.getFrontCoordinateZ1();
        front_coordinate_z_2 = some_uitank.getFrontCoordinateZ2();

        back_coordinate_x_1 = some_uitank.getBackCoordinateX1();
        back_coordinate_x_2 = some_uitank.getBackCoordinateX2();
        back_coordinate_y_1 = some_uitank.getBackCoordinateY1();
        back_coordinate_y_2 = some_uitank.getBackCoordinateY2();
        back_coordinate_z_1 = some_uitank.getBackCoordinateZ1();
        back_coordinate_z_2 = some_uitank.getBackCoordinateZ2();

        /** assignment existing checked position
         */
        top_check_mechanical_loading = some_uitank.getTopCheckMechanicalLoading();
        top_check_thermal_influx = some_uitank.getTopCheckThermalInflux();

        bottom_check_mechanical_loading = some_uitank.getBottomCheckMechanicalLoading();
        bottom_check_thermal_influx = some_uitank.getBottomCheckThermalInflux();

        left_check_mechanical_loading = some_uitank.getLeftCheckMechanicalLoading();
        left_check_thermal_influx = some_uitank.getLeftCheckThermalInflux();

        right_check_mechanical_loading = some_uitank.getRightCheckMechanicalLoading();
        right_check_thermal_influx = some_uitank.getRightCheckThermalInflux();

        front_check_mechanical_loading = some_uitank.getFrontCheckMechanicalLoading();
        front_check_thermal_influx = some_uitank.getFrontCheckThermalInflux();

        back_check_mechanical_loading = some_uitank.getBackCheckMechanicalLoading();
        back_check_thermal_influx = some_uitank.getBackCheckThermalInflux();
        
        // Types of tanks
        parallelepiped_tank    = some_uitank.getCheckParallelepipedTankType();
        vertical_ellyptic_tank = some_uitank.getCheckVerticalEllypticTankType();
        horizon_triangle_tank  = some_uitank.getCheckHorizonTriangleTankType();
        horizon_circle_tank    = some_uitank.getCheckHorizonCircleTankType();

        // assignment values for task type
        top_stress = some_uitank.getTopStress();
        top_strain = some_uitank.getTopStrain();
        top_temperature = some_uitank.getTopTemperature();
        top_energy = some_uitank.getTopEnergy();

        bottom_stress = some_uitank.getBottomStress();
        bottom_strain = some_uitank.getBottomStrain();
        bottom_temperature = some_uitank.getBottomTemperature();
        bottom_energy = some_uitank.getBottomEnergy();

        left_stress = some_uitank.getLeftStress();
        left_strain = some_uitank.getLeftStrain();
        left_temperature = some_uitank.getLeftTemperature();
        left_energy = some_uitank.getLeftEnergy();

        right_stress = some_uitank.getRightStress();
        right_strain = some_uitank.getRightStrain();
        right_temperature = some_uitank.getRightTemperature();
        right_energy = some_uitank.getRightEnergy();

        front_stress = some_uitank.getFrontStress();
        front_strain = some_uitank.getFrontStrain();
        front_temperature = some_uitank.getFrontTemperature();
        front_energy = some_uitank.getFrontEnergy();

        back_stress = some_uitank.getBackStress();
        back_strain = some_uitank.getBackStrain();
        back_temperature = some_uitank.getBackTemperature();
        back_energy = some_uitank.getBackEnergy();

        /** assignment choosed materials for not adiabatic boundarys of existing parameters
         */
        top_adiabatic_material = some_uitank.getTopAdiabaticMaterial();
        bottom_adiabatic_material = some_uitank.getBottomAdiabaticMaterial();
        left_adiabatic_material = some_uitank.getLeftAdiabaticMaterial();
        right_adiabatic_material = some_uitank.getRightAdiabaticMaterial();
        front_adiabatic_material = some_uitank.getFrontAdiabaticMaterial();
        back_adiabatic_material = some_uitank.getBackAdiabaticMaterial();
    }

    /** This constructor create boundary conditions data bank by
     * loading data from file
     * @param bound_cond_file_name - directory of file
     */
    public UITank(String file_name)
    {
        read(file_name);
    }

    /** This constructor create boundary conditions data bank by
     * loading data from file
     * @param ui_int - choosed parameters data bank (there is name of file)
     */
    public UITank(UIInterface ui_int)
    {
        read(Common.BOUND_COND_PATH+"/"+ui_int.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_int.getBoundCondPath()+"."+Common.TANK_EXTENSION);

//        read2(ui_int);
    }

    public void setTopCoordinateX1(double value)
    {
        top_coordinate_x_1 = value;
    }
    
    public void setTopCoordinateX2(double value)
    {
        top_coordinate_x_2 = value;
    }
    
    public void setTopCoordinateY1(double value)
    {
        top_coordinate_y_1 = value;
    }
    
    public void setTopCoordinateY2(double value)
    {
        top_coordinate_y_2 = value;
    }
    
    public void setTopCoordinateZ1(double value)
    {
        top_coordinate_z_1 = value;
    }
    
    public void setTopCoordinateZ2(double value)
    {
        top_coordinate_z_2 = value;
    }
    
    public void setBottomCoordinateX1(double value)
    {
        bottom_coordinate_x_1 = value;
    }
    
    public void setBottomCoordinateX2(double value)
    {
        bottom_coordinate_x_2 = value;
    }
    
    public void setBottomCoordinateY1(double value)
    {
        bottom_coordinate_y_1 = value;
    }
    
    public void setBottomCoordinateY2(double value)
    {
        bottom_coordinate_y_2 = value;
    }
    
    public void setBottomCoordinateZ1(double value)
    {
        bottom_coordinate_z_1 = value;
    }
    
    public void setBottomCoordinateZ2(double value)
    {
        bottom_coordinate_z_2 = value;
    }
    
    public void setLeftCoordinateX1(double value)
    {
        left_coordinate_x_1 = value;
    }
    
    public void setLeftCoordinateX2(double value)
    {
        left_coordinate_x_2 = value;
    }
    
    public void setLeftCoordinateY1(double value)
    {
        left_coordinate_y_1 = value;
    }
    
    public void setLeftCoordinateY2(double value)
    {
        left_coordinate_y_2 = value;
    }
    
    public void setLeftCoordinateZ1(double value)
    {
        left_coordinate_z_1 = value;
    }
    
    public void setLeftCoordinateZ2(double value)
    {
        left_coordinate_z_2 = value;
    }
    
    public void setRightCoordinateX1(double value)
    {
        right_coordinate_x_1 = value;
    }
    
    public void setRightCoordinateX2(double value)
    {
        right_coordinate_x_2 = value;
    }
    
    public void setRightCoordinateY1(double value)
    {
        right_coordinate_y_1 = value;
    }
    
    public void setRightCoordinateY2(double value)
    {
        right_coordinate_y_2 = value;
    }
    
    public void setRightCoordinateZ1(double value)
    {
        right_coordinate_z_1 = value;
    }
    
    public void setRightCoordinateZ2(double value)
    {
        right_coordinate_z_2 = value;
    }
    
    public void setFrontCoordinateX1(double value)
    {
        front_coordinate_x_1 = value;
    }
    
    public void setFrontCoordinateX2(double value)
    {
        front_coordinate_x_2 = value;
    }
    
    public void setFrontCoordinateY1(double value)
    {
        front_coordinate_y_1 = value;
    }
    
    public void setFrontCoordinateY2(double value)
    {
        front_coordinate_y_2 = value;
    }
    
    public void setFrontCoordinateZ1(double value)
    {
        front_coordinate_z_1 = value;
    }
    
    public void setFrontCoordinateZ2(double value)
    {
        front_coordinate_z_2 = value;
    }
    
    public void setBackCoordinateX1(double value)
    {
        back_coordinate_x_1 = value;
    }
    
    public void setBackCoordinateX2(double value)
    {
        back_coordinate_x_2 = value;
    }
    
    public void setBackCoordinateY1(double value)
    {
        back_coordinate_y_1 = value;
    }
    
    public void setBackCoordinateY2(double value)
    {
        back_coordinate_y_2 = value;
    }
    
    public void setBackCoordinateZ1(double value)
    {
        back_coordinate_z_1 = value;
    }
    
    public void setBackCoordinateZ2(double value)
    {
        back_coordinate_z_2 = value;
    }

    /** Set top "stress" radio button selected or not
     * @param value - selected or not
     */
    

    public void setTopStress(boolean value)
    {
        top_stress = value;
    }

    // Some new + some new

    /** Set top "strain" radio button selected or not
     * @param value - selected or not
     */
    public void setTopStrain(boolean value)
    {
        top_strain = value;
    }

    /** Set top "temperature" radio button selected or not
     * @param value - selected or not
     */
    public void setTopTemperature(boolean value)
    {
        top_temperature = value;
    }

    /** Set top "energy" radio button selected or not
     * @param value - selected or not
     */
    public void setTopEnergy(boolean value)
    {
        top_energy = value;
    }

    /** Set bottom "stress" radio button selected or not
     * @param value - selected or not
     */
    public void setBottomStress(boolean value)
    {
        bottom_stress = value;
    }

    /** Set bottom "strain" radio button selected or not
     * @param value - selected or not
     */
    public void setBottomStrain(boolean value)
    {
        bottom_strain = value;
    }

    /** Set bottom "temperature" radio button selected or not
     * @param value - selected or not
     */
    public void setBottomTemperature(boolean value)
    {
        bottom_temperature = value;
    }

    /** Set bottom "energy" radio button selected or not
     * @param value - selected or not
     */
    public void setBottomEnergy(boolean value)
    {
        bottom_energy = value;
    }

    /** Set left "stress" radio button selected or not
     * @param value - selected or not
     */
    public void setLeftStress(boolean value)
    {
        left_stress = value;
    }

    /** Set left "strain" radio button selected or not
     * @param value - selected or not
     */
    public void setLeftStrain(boolean value)
    {
        left_strain = value;
    }

    /** Set left "temperature" radio button selected or not
     * @param value - selected or not
     */
    public void setLeftTemperature(boolean value)
    {
        left_temperature = value;
    }

    /** Set left "energy" radio button selected or not
     * @param value - selected or not
     */
    public void setLeftEnergy(boolean value)
    {
        left_energy = value;
    }

    /** Set right "stress" radio button selected or not
     * @param value - selected or not
     */
    public void setRightStress(boolean value)
    {
        right_stress = value;
    }

    /** Set right "strain" radio button selected or not
     * @param value - selected or not
     */
    public void setRightStrain(boolean value)
    {
        right_strain = value;
    }

    /** Set right "temperature" radio button selected or not
     * @param value - selected or not
     */
    public void setRightTemperature(boolean value)
    {
        right_temperature = value;
    }

    /** Set right "energy" radio button selected or not
     * @param value - selected or not
     */
    public void setRightEnergy(boolean value)
    {
        right_energy = value;
    }

    /** Set front "stress" radio button selected or not
     * @param value - selected or not
     */
    public void setFrontStress(boolean value)
    {
        front_stress = value;
    }

    /** Set front "strain" radio button selected or not
     * @param value - selected or not
     */
    public void setFrontStrain(boolean value)
    {
        front_strain = value;
    }

    /** Set front "temperature" radio button selected or not
     * @param value - selected or not
     */
    public void setFrontTemperature(boolean value)
    {
        front_temperature = value;
    }

    /** Set front "energy" radio button selected or not
     * @param value - selected or not
     */
    public void setFrontEnergy(boolean value)
    {
        front_energy = value;
    }

    /** Set back "stress" radio button selected or not
     * @param value - selected or not
     */
    public void setBackStress(boolean value)
    {
        back_stress = value;
    }

    /** Set back "strain" radio button selected or not
     * @param value - selected or not
     */
    public void setBackStrain(boolean value)
    {
        back_strain = value;
    }

    /** Set back "temperature" radio button selected or not
     * @param value - selected or not
     */
    public void setBackTemperature(boolean value)
    {
        back_temperature = value;
    }

    /** Set back "energy" radio button selected or not
     * @param value - selected or not
     */
    public void setBackEnergy(boolean value)
    {
        back_energy = value;
    }

    /** Get information about top "stress" radio button - selected
     * it or not
     * @return - selected or not
     */
    public boolean getTopStress()
    {
        return top_stress;
    }

    /** Get information about top "strain" radio button - selected
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

    /*
     * Set new value of parameter "mechanical loading"
     * in side of boundary "top"
     * @param value - new value of mechanical loading
     */
    public void setTopMechValue(double value)
    {
        top_mech_value = value;
    }
    
    /** The method sets minimal value of cycling mechanical loading of top tank.
     * @param value minimal value of cycling loading of top tank
     */
    public void setTopMinMechValue(double value)
    {
        min_top_mech_value = value;
    }

    /** Set new value of parameter "thermal influx"
     * in side of boundary "top"
     * @param value - new value of thermal influx
     */
    public void setTopThermValue(double value)
    {
        top_therm_value = value;
    }
    
    /** The method sets minimal value of cycling thermal loading of top tank.
     * @param value minimal value of cycling loading of top tank
     */
    public void setTopMinThermValue(double value)
    {
        min_top_therm_value = value;
    }
    
    /** The method sets type of time function for top tank
     * @param value type of time function for top tank
     */
    public void setTopTimeFunctionType(byte value)
    {
        top_time_function_type = value;
    }
    
    /** The method sets time portion of loading for top tank
     * @param value time portion of loading for top tank
     */
    public void setTopLoadingTimePortion(double value)
    {
        top_loading_time_portion = value;
    }    
    
    /** The method sets time portion of relaxation for top tank
     * @param value time portion of relaxation for top tank
     */
    public void setTopRelaxationTimePortion(double value)
    {
        top_relaxation_time_portion = value;
    }

    /** Set new value of parameter "mechanical loading"
     * in side of boundary "bottom"
     * @param value - new value of mechanical loading
     */
    public void setBottomMechValue(double value)
    {
        bottom_mech_value = value;
    }

    /** Set new value of parameter "thermal influx"
     * in side of boundary "bottom"
     * @param value - new value of thermal influx
     */
    public void setBottomThermValue(double value)
    {
        bottom_therm_value = value;
    }    
    
    /** The method sets minimal value of cycling mechanical loading of bottom tank.
     * @param value minimal value of cycling loading of bottom tank
     */
    public void setBottomMinMechValue(double value)
    {
        min_bottom_mech_value = value;
    }
    
    /** The method sets minimal value of cycling thermal loading of bottom tank.
     * @param value minimal value of cycling loading of bottom tank
     */
    public void setBottomMinThermValue(double value)
    {
        min_bottom_therm_value = value;
    }
    
    /** The method sets type of time function for bottom tank
     * @param value type of time function for bottom tank
     */
    public void setBottomTimeFunctionType(byte value)
    {
        bottom_time_function_type = value;
    }
    
    /** The method sets time portion of loading for bottom tank
     * @param value time portion of loading for bottom tank
     */
    public void setBottomLoadingTimePortion(double value)
    {
        bottom_loading_time_portion = value;
    }
    
    /** The method sets time portion of relaxation for bottom tank
     * @param value time portion of relaxation for bottom tank
     */
    public void setBottomRelaxationTimePortion(double value)
    {
        bottom_relaxation_time_portion = value;
    }

    /** Set new value of parameter "mechanical loading"
     * in side of boundary "left"
     * @param value - new value of mechanical loading
     */
    public void setLeftMechValue(double value)
    {
        left_mech_value = value;
    }

    /** Set new value of parameter "thermal influx"
     * in side of boundary "left"
     * @param value - new value of thermal influx
     */
    public void setLeftThermValue(double value)
    {
        left_therm_value = value;
    }    
    
    /** The method sets minimal value of cycling mechanical loading of left tank.
     * @param value minimal value of cycling loading of left tank
     */
    public void setLeftMinMechValue(double value)
    {
        min_left_mech_value = value;
    }
    
    /** The method sets minimal value of cycling thermal loading of left tank.
     * @param value minimal value of cycling loading of left tank
     */
    public void setLeftMinThermValue(double value)
    {
        min_left_therm_value = value;
    }    
    
    /** The method sets type of time function for left tank
     * @param value type of time function for left tank
     */
    public void setLeftTimeFunctionType(byte value)
    {
        left_time_function_type = value;
    }
    
    /** The method sets time portion of loading for left tank
     * @param value time portion of loading for left tank
     */
    public void setLeftLoadingTimePortion(double value)
    {
        left_loading_time_portion = value;
    }
    
    /** The method sets time portion of relaxation for left tank
     * @param value time portion of relaxation for left tank
     */
    public void setLeftRelaxationTimePortion(double value)
    {
        left_relaxation_time_portion = value;
    }

    /** Set new value of parameter "mechanical loading"
     * in side of boundary "right"
     * @param value - new value of mechanical loading
     */
    public void setRightMechValue(double value)
    {
        right_mech_value = value;
    }

    /** Set new value of parameter "thermal influx"
     * in side of boundary "right"
     * @param value - new value of thermal influx
     */
    public void setRightThermValue(double value)
    {
        right_therm_value = value;
    }    
    
    /** The method sets minimal value of cycling mechanical loading of right tank.
     * @param value minimal value of cycling loading of right tank
     */
    public void setRightMinMechValue(double value)
    {
        min_right_mech_value = value;
    }
    
    /** The method sets minimal value of cycling thermal loading of right tank.
     * @param value minimal value of cycling loading of right tank
     */
    public void setRightMinThermValue(double value)
    {
        min_right_therm_value = value;
    }    
        
    /** The method sets type of time function for right tank
     * @param value type of time function for right tank
     */
    public void setRightTimeFunctionType(byte value)
    {
        right_time_function_type = value;
    }
    
    /** The method sets time portion of loading for right tank
     * @param value time portion of loading for right tank
     */
    public void setRightLoadingTimePortion(double value)
    {
        right_loading_time_portion = value;
    }
    
    /** The method sets time portion of relaxation for right tank
     * @param value time portion of relaxation for right tank
     */
    public void setRightRelaxationTimePortion(double value)
    {
        right_relaxation_time_portion = value;
    }

    /** Set new value of parameter "mechanical loading"
     * in side of boundary "front"
     * @param value - new value of mechanical loading
     */
    public void setFrontMechValue(double value)
    {
        front_mech_value = value;
    }

    /** Set new value of parameter "thermal influx"
     * in side of boundary "front"
     * @param value - new value of thermal influx
     */
    public void setFrontThermValue(double value)
    {
        front_therm_value = value;
    }    
    
    /** The method sets minimal value of cycling mechanical loading of front tank.
     * @param value minimal value of cycling loading of front tank
     */
    public void setFrontMinMechValue(double value)
    {
        min_front_mech_value = value;
    }
    
    /** The method sets minimal value of cycling thermal loading of front tank.
     * @param value minimal value of cycling loading of front tank
     */
    public void setFrontMinThermValue(double value)
    {
        min_front_therm_value = value;
    }
    
    /** The method sets type of time function for front tank
     * @param value type of time function for front tank
     */
    public void setFrontTimeFunctionType(byte value)
    {
        front_time_function_type = value;
    }
    
    /** The method sets time portion of loading for front tank
     * @param value time portion of loading for front tank
     */
    public void setFrontLoadingTimePortion(double value)
    {
        front_loading_time_portion = value;
    }
    
    /** The method sets time portion of relaxation for front tank
     * @param value time portion of relaxation for front tank
     */
    public void setFrontRelaxationTimePortion(double value)
    {
        front_relaxation_time_portion = value;
    }

    /** Set new value of parameter "mechanical loading"
     * in side of boundary "back"
     * @param value - new value of mechanical loading
     */
    public void setBackMechValue(double value)
    {
        back_mech_value = value;
    }

    /** Set new value of parameter "thermal influx"
     * in side of boundary "back"
     * @param value - new value of thermal influx
     */
    public void setBackThermValue(double value)
    {
        back_therm_value = value;
    }
    
    /** The method sets minimal value of cycling mechanical loading of back tank.
     * @param value minimal value of cycling loading of back tank
     */
    public void setBackMinMechValue(double value)
    {
        min_back_mech_value = value;
    }
    
    /** The method sets minimal value of cycling thermal loading of back tank.
     * @param value minimal value of cycling loading of back tank
     */
    public void setBackMinThermValue(double value)
    {
        min_back_therm_value = value;
    }
    
    /** The method sets type of time function for back tank
     * @param value type of time function for back tank
     */
    public void setBackTimeFunctionType(byte value)
    {
        back_time_function_type = value;
    }
    
    /** The method sets time portion of loading for back tank
     * @param value time portion of loading for back tank
     */
    public void setBackLoadingTimePortion(double value)
    {
        back_loading_time_portion = value;
    }
    
    /** The method sets time portion of relaxation for back tank
     * @param value time portion of relaxation for back tank
     */
    public void setBackRelaxationTimePortion(double value)
    {
        back_relaxation_time_portion = value;
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
    
    /** Setting of type of tanks "parallelepiped_tank"
     * @param value 
     */
    public void setParallelepipedTankType(boolean value)
    {
        parallelepiped_tank = value;
    }
    
    /** Setting of type of tanks "vertical_ellyptic_tank"
     * @param value 
     */
    public void setVerticalEllypticTankType(boolean value)
    {
        vertical_ellyptic_tank = value;
    }
    
    /** Setting of type of tanks "horizon_triangle_tank"
     * @param value 
     */
    public void setHorizonTriangleTankType(boolean value)
    {
        horizon_triangle_tank = value;
    }
    
    /** Setting of type of tanks "horizon_circle_tank"
     * @param value 
     */
    public void setHorizonCircleTankType(boolean value)
    {
        horizon_circle_tank = value;
    }

    /*
     * Set info about adiabatic top boundary or not
     * @param value - adiabatic or not
     */

    public void setTopAdiabaticMaterial(String value)
    {
        top_adiabatic_material = value;
    }

    /*
     * Set info about adiabatic bottom boundary or not
     * @param value - adiabatic or not
     */

    public void setBottomAdiabaticMaterial(String value)
    {
        bottom_adiabatic_material = value;
    }

    /*
     * Set info about adiabatic left boundary or not
     * @param value - adiabatic or not
     */

    public void setLeftAdiabaticMaterial(String value)
    {
        left_adiabatic_material = value;
    }

    /*
     * Set info about adiabatic right boundary or not
     * @param value - adiabatic or not
     */

    public void setRightAdiabaticMaterial(String value)
    {
        right_adiabatic_material = value;
    }

    /*
     * Set info about adiabatic front boundary or not
     * @param value - adiabatic or not
     */

    public void setFrontAdiabaticMaterial(String value)
    {
        front_adiabatic_material = value;
    }

    /*
     * Set info about adiabatic back boundary or not
     * @param value - adiabatic or not
     */

    public void setBackAdiabaticMaterial(String value)
    {
        back_adiabatic_material = value;
    }



    public double getTopCoordinateX1()
    {
        return top_coordinate_x_1;
    }

    public double getTopCoordinateX2()
    {
        return top_coordinate_x_2;
    }

    public double getTopCoordinateY1()
    {
        return top_coordinate_y_1;
    }

    public double getTopCoordinateY2()
    {
        return top_coordinate_y_2;
    }

    public double getTopCoordinateZ1()
    {
        return top_coordinate_z_1;
    }

    public double getTopCoordinateZ2()
    {
        return top_coordinate_z_2;
    }






    public double getBottomCoordinateX1()
    {
        return bottom_coordinate_x_1;
    }

    public double getBottomCoordinateX2()
    {
        return bottom_coordinate_x_2;
    }

    public double getBottomCoordinateY1()
    {
        return bottom_coordinate_y_1;
    }

    public double getBottomCoordinateY2()
    {
        return bottom_coordinate_y_2;
    }

    public double getBottomCoordinateZ1()
    {
        return bottom_coordinate_z_1;
    }

    public double getBottomCoordinateZ2()
    {
        return bottom_coordinate_z_2;
    }




    public double getLeftCoordinateX1()
    {
        return left_coordinate_x_1;
    }

    public double getLeftCoordinateX2()
    {
        return left_coordinate_x_2;
    }
    
    public double getLeftCoordinateY1()
    {
        return left_coordinate_y_1;
    }

    public double getLeftCoordinateY2()
    {
        return left_coordinate_y_2;
    }
    
    public double getLeftCoordinateZ1()
    {
        return left_coordinate_z_1;
    }

    public double getLeftCoordinateZ2()
    {
        return left_coordinate_z_2;
    }

    public double getRightCoordinateX1()
    {
        return right_coordinate_x_1;
    }

    public double getRightCoordinateX2()
    {
        return right_coordinate_x_2;
    }
    
    public double getRightCoordinateY1()
    {
        return right_coordinate_y_1;
    }

    public double getRightCoordinateY2()
    {
        return right_coordinate_y_2;
    }
    
    public double getRightCoordinateZ1()
    {
        return right_coordinate_z_1;
    }

    public double getRightCoordinateZ2()
    {
        return right_coordinate_z_2;
    }

    public double getFrontCoordinateX1()
    {
        return front_coordinate_x_1;
    }

    public double getFrontCoordinateX2()
    {
        return front_coordinate_x_2;
    }
    
    public double getFrontCoordinateY1()
    {
        return front_coordinate_y_1;
    }

    public double getFrontCoordinateY2()
    {
        return front_coordinate_y_2;
    }
    
    public double getFrontCoordinateZ1()
    {
        return front_coordinate_z_1;
    }

    public double getFrontCoordinateZ2()
    {
        return front_coordinate_z_2;
    }

    public double getBackCoordinateX1()
    {
        return back_coordinate_x_1;
    }

    public double getBackCoordinateX2()
    {
        return back_coordinate_x_2;
    }

    public double getBackCoordinateY1()
    {
        return back_coordinate_y_1;
    }

    public double getBackCoordinateY2()
    {
        return back_coordinate_y_2;
    }

    public double getBackCoordinateZ1()
    {
        return back_coordinate_z_1;
    }

    public double getBackCoordinateZ2()
    {
        return back_coordinate_z_2;
    }

    /** The method returns maximal parameter of mechanical cycling loading of top tank.
     * @return maximal parameter of mechanical cycling loading of top tank
     */
    public double getTopMechValue()
    {
        return top_mech_value;
    }

    /** The method returns maximal parameter of thermal cycling loading of top tank.
     * @return maximal parameter of thermal cycling loading of top tank
     */
    public double getTopThermValue()
    {
        return top_therm_value;
    }
    
    /** The method returns minimal parameter of mechanical cycling loading of top tank.
     * @return minimal parameter of mechanical cycling loading of top tank
     */
    public double getTopMinMechValue()
    {
        return min_top_mech_value;
    }

    /** The method returns minimal parameter of thermal cycling loading of top tank.
     * @return minimal parameter of thermal cycling loading of top tank
     */
    public double getTopMinThermValue()
    {
        return min_top_therm_value;
    }
    
    /** The method returns type of time function for top tank
     * @return type of time function for top tank
     */
    public byte getTopTimeFunctionType()
    {
        return top_time_function_type;
    }
    
    /** The method returns type of time function for bottom tank
     * @return type of time function for bottom tank
     */
    public byte getBottomTimeFunctionType()
    {
        return bottom_time_function_type;
    }
    
    /** The method returns type of time function for left tank
     * @return type of time function for left tank
     */
    public byte getLeftTimeFunctionType()
    {
        return left_time_function_type;
    }    
    
    /** The method returns type of time function for right tank
     * @return type of time function for right tank
     */
    public byte getRightTimeFunctionType()
    {
        return right_time_function_type;
    }    
    
    /** The method returns type of time function for front tank
     * @return type of time function for front tank
     */
    public byte getFrontTimeFunctionType()
    {
        return front_time_function_type;
    }
        
    /** The method returns type of time function for back tank
     * @return type of time function for back tank
     */
    public byte getBackTimeFunctionType()
    {
        return back_time_function_type;
    }
    
    /** The method returns time portion of loading for top tank
     * @return time portion of loading for top tank
     */
    public double getTopLoadingTimePortion()
    {
        return top_loading_time_portion;
    }
    
    /** The method returns time portion of loading for bottom tank
     * @return time portion of loading for bottom tank
     */
    public double getBottomLoadingTimePortion()
    {
        return bottom_loading_time_portion;
    }    
    
    /** The method returns time portion of loading for left tank
     * @return time portion of loading for left tank
     */
    public double getLeftLoadingTimePortion()
    {
        return left_loading_time_portion;
    }
    
    /** The method returns time portion of loading for right tank
     * @return time portion of loading for right tank
     */
    public double getRightLoadingTimePortion()
    {
        return right_loading_time_portion;
    }
    
    /** The method returns time portion of loading for front tank
     * @return time portion of loading for front tank
     */
    public double getFrontLoadingTimePortion()
    {
        return front_loading_time_portion;
    }
    
    /** The method returns time portion of loading for back tank
     * @return time portion of loading for back tank
     */
    public double getBackLoadingTimePortion()
    {
        return back_loading_time_portion;
    }
    
    /** The method returns time portion of relaxation for top tank
     * @return time portion of relaxation for top tank
     */
    public double getTopRelaxationTimePortion()
    {
        return top_relaxation_time_portion;
    }
    
    /** The method returns time portion of relaxation for bottom tank
     * @return time portion of relaxation for bottom tank
     */
    public double getBottomRelaxationTimePortion()
    {
        return bottom_relaxation_time_portion;
    }
    
    /** The method returns time portion of relaxation for left tank
     * @return time portion of relaxation for left tank
     */
    public double getLeftRelaxationTimePortion()
    {
        return left_relaxation_time_portion;
    }
    
    /** The method returns time portion of relaxation for right tank
     * @return time portion of relaxation for right tank
     */
    public double getRightRelaxationTimePortion()
    {
        return right_relaxation_time_portion;
    }
    
    /** The method returns time portion of relaxation for front tank
     * @return time portion of relaxation for front tank
     */
    public double getFrontRelaxationTimePortion()
    {
        return front_relaxation_time_portion;
    }
    
    /** The method returns time portion of relaxation for back tank
     * @return time portion of relaxation for back tank
     */
    public double getBackRelaxationTimePortion()
    {
        return back_relaxation_time_portion;
    }    
    
    /** The method returns maximal parameter of mechanical cycling loading of bottom tank.
     * @return maximal parameter of mechanical cycling loading of bottom tank
     */
    public double getBottomMechValue()
    {
        return bottom_mech_value;
    }
    
    /** The method returns maximal parameter of thermal cycling loading of bottom tank.
     * @return maximal parameter of thermal cycling loading of bottom tank
     */
    public double getBottomThermValue()
    {
        return bottom_therm_value;
    }
    
    /** The method returns minimal parameter of mechanical cycling loading of bottom tank.
     * @return minimal parameter of mechanical cycling loading of bottom tank
     */
    public double getBottomMinMechValue()
    {
        return min_bottom_mech_value;
    }

    /** The method returns minimal parameter of thermal cycling loading of bottom tank.
     * @return minimal parameter of thermal cycling loading of bottom tank
     */
    public double getBottomMinThermValue()
    {
        return min_bottom_therm_value;
    }
    
    /** The method returns maximal parameter of mechanical cycling loading of left tank.
     * @return maximal parameter of mechanical cycling loading of left tank
     */
    public double getLeftMechValue()
    {
        return left_mech_value;
    }
    
    /** The method returns maximal parameter of thermal cycling loading of left tank.
     * @return maximal parameter of thermal cycling loading of left tank
     */
    public double getLeftThermValue()
    {
        return left_therm_value;
    }
    
    /** The method returns minimal parameter of mechanical cycling loading of left tank.
     * @return minimal parameter of mechanical cycling loading of left tank
     */
    public double getLeftMinMechValue()
    {
        return min_left_mech_value;
    }

    /** The method returns minimal parameter of thermal cycling loading of left tank.
     * @return minimal parameter of thermal cycling loading of left tank
     */
    public double getLeftMinThermValue()
    {
        return min_left_therm_value;
    }
    
    /** The method returns maximal parameter of mechanical cycling loading of right tank.
     * @return maximal parameter of mechanical cycling loading of right tank
     */
    public double getRightMechValue()
    {
        return right_mech_value;
    }
    
    /** The method returns maximal parameter of thermal cycling loading of right tank.
     * @return maximal parameter of thermal cycling loading of right tank
     */
    public double getRightThermValue()
    {
        return right_therm_value;
    }
    
    /** The method returns minimal parameter of mechanical cycling loading of right tank.
     * @return minimal parameter of mechanical cycling loading of right tank
     */
    public double getRightMinMechValue()
    {
        return min_right_mech_value;
    }

    /** The method returns minimal parameter of thermal cycling loading of right tank.
     * @return minimal parameter of thermal cycling loading of right tank
     */
    public double getRightMinThermValue()
    {
        return min_right_therm_value;
    }
    
    /** The method returns maximal parameter of mechanical cycling loading of front tank.
     * @return maximal parameter of mechanical cycling loading of front tank
     */
    public double getFrontMechValue()
    {
        return front_mech_value;
    }
    
    /** The method returns maximal parameter of thermal cycling loading of front tank.
     * @return maximal parameter of thermal cycling loading of front tank
     */
    public double getFrontThermValue()
    {
        return front_therm_value;
    }
    
    /** The method returns minimal parameter of mechanical cycling loading of front tank.
     * @return minimal parameter of mechanical cycling loading of front tank
     */
    public double getFrontMinMechValue()
    {
        return min_front_mech_value;
    }
    
    /** The method returns minimal parameter of thermal cycling loading of front tank.
     * @return minimal parameter of thermal cycling loading of front tank
     */
    public double getFrontMinThermValue()
    {
        return min_front_therm_value;
    }
    
    /** The method returns maximal parameter of mechanical cycling loading of back tank.
     * @return maximal parameter of mechanical cycling loading of back tank
     */
    public double getBackMechValue()
    {
        return back_mech_value;
    }
    
    /** The method returns maximal parameter of thermal cycling loading of back tank.
     * @return maximal parameter of thermal cycling loading of back tank
     */
    public double getBackThermValue()
    {
        return back_therm_value;
    }
    
    /** The method returns minimal parameter of mechanical cycling loading of back tank.
     * @return minimal parameter of mechanical cycling loading of back tank
     */
    public double getBackMinMechValue()
    {
        return min_back_mech_value;
    }
    
    /** The method returns minimal parameter of thermal cycling loading of back tank.
     * @return minimal parameter of thermal cycling loading of back tank
     */
    public double getBackMinThermValue()
    {
        return min_back_therm_value;
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

    /** Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "front"
     */
    public boolean getFrontCheckMechanicalLoading()
    {
        return front_check_mechanical_loading;
    }

    /** Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "front"
     */
    public boolean getFrontCheckThermalInflux()
    {
        return front_check_thermal_influx;
    }

    /** Get information - checked or
     * not position - "mechanical loading" in side
     * of boundary "back"
     */
    public boolean getBackCheckMechanicalLoading()
    {
        return back_check_mechanical_loading;
    }

    /** Get information - checked or
     * not position - "thermal influx" in side
     * of boundary "back"
     */
    public boolean getBackCheckThermalInflux()
    {
        return back_check_thermal_influx;
    }

    /** The method returns presence of type of tanks "parallelepiped_tank"
    * @return presence of type of tanks "parallelepiped_tank" 
     */
    public boolean getCheckParallelepipedTankType()
    {
        return parallelepiped_tank;
    }
    
    /** The method returns presence of type of tanks "vertical_ellyptic_tank"
    * @return presence of type of tanks "parallelepiped_tank" 
     */
    public boolean getCheckVerticalEllypticTankType()
    {
        return vertical_ellyptic_tank;
    }
    
    /** The method returns presence of type of tanks "horizon_triangle_tank"
    * @return presence of type of tanks "parallelepiped_tank" 
     */
    public boolean getCheckHorizonTriangleTankType()
    {
        return horizon_triangle_tank;
    }
    
    /** The method returns presence of type of tanks "horizon_circle_tank"
     * @return presence of type of tanks "parallelepiped_tank" 
     */
    public boolean getCheckHorizonCircleTankType()
    {
        return horizon_circle_tank;
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

    public void read(String file_name)
    {
        /**
         * read data from file
         */
        StringVector data = null;

        try
        {
            System.out.println("READING FROM UI TANK is processed...");
            
            TextTableFileReader data_reader = new TextTableFileReader(file_name);
            data = data_reader.convertToTable();
            
            left_adiabatic_material   = data.getCell(0,0);
            front_adiabatic_material  = data.getCell(1,0);
            top_adiabatic_material    = data.getCell(2,0);
            bottom_adiabatic_material = data.getCell(3,0);
            back_adiabatic_material   = data.getCell(4,0);
            right_adiabatic_material  = data.getCell(5,0);
            
            left_coordinate_x_1 = Double.valueOf(data.getCell(0,1)).doubleValue();
            left_coordinate_x_2 = Double.valueOf(data.getCell(0,2)).doubleValue();
            left_coordinate_y_1 = Double.valueOf(data.getCell(0,3)).doubleValue();
            left_coordinate_y_2 = Double.valueOf(data.getCell(0,4)).doubleValue();
            left_coordinate_z_1 = Double.valueOf(data.getCell(0,5)).doubleValue();
            left_coordinate_z_2 = Double.valueOf(data.getCell(0,6)).doubleValue();
            
            front_coordinate_x_1 = Double.valueOf(data.getCell(1,1)).doubleValue();
            front_coordinate_x_2 = Double.valueOf(data.getCell(1,2)).doubleValue();
            front_coordinate_y_1 = Double.valueOf(data.getCell(1,3)).doubleValue();
            front_coordinate_y_2 = Double.valueOf(data.getCell(1,4)).doubleValue();
            front_coordinate_z_1 = Double.valueOf(data.getCell(1,5)).doubleValue();
            front_coordinate_z_2 = Double.valueOf(data.getCell(1,6)).doubleValue();
            
            top_coordinate_x_1 = Double.valueOf(data.getCell(2,1)).doubleValue();
            top_coordinate_x_2 = Double.valueOf(data.getCell(2,2)).doubleValue();
            top_coordinate_y_1 = Double.valueOf(data.getCell(2,3)).doubleValue();
            top_coordinate_y_2 = Double.valueOf(data.getCell(2,4)).doubleValue();
            top_coordinate_z_1 = Double.valueOf(data.getCell(2,5)).doubleValue();
            top_coordinate_z_2 = Double.valueOf(data.getCell(2,6)).doubleValue();

            bottom_coordinate_x_1 = Double.valueOf(data.getCell(3,1)).doubleValue();
            bottom_coordinate_x_2 = Double.valueOf(data.getCell(3,2)).doubleValue();
            bottom_coordinate_y_1 = Double.valueOf(data.getCell(3,3)).doubleValue();
            bottom_coordinate_y_2 = Double.valueOf(data.getCell(3,4)).doubleValue();
            bottom_coordinate_z_1 = Double.valueOf(data.getCell(3,5)).doubleValue();
            bottom_coordinate_z_2 = Double.valueOf(data.getCell(3,6)).doubleValue();
            
            back_coordinate_x_1 = Double.valueOf(data.getCell(4,1)).doubleValue();
            back_coordinate_x_2 = Double.valueOf(data.getCell(4,2)).doubleValue();
            back_coordinate_y_1 = Double.valueOf(data.getCell(4,3)).doubleValue();
            back_coordinate_y_2 = Double.valueOf(data.getCell(4,4)).doubleValue();
            back_coordinate_z_1 = Double.valueOf(data.getCell(4,5)).doubleValue();
            back_coordinate_z_2 = Double.valueOf(data.getCell(4,6)).doubleValue();
            
            right_coordinate_x_1 = Double.valueOf(data.getCell(5,1)).doubleValue();
            right_coordinate_x_2 = Double.valueOf(data.getCell(5,2)).doubleValue();
            right_coordinate_y_1 = Double.valueOf(data.getCell(5,3)).doubleValue();
            right_coordinate_y_2 = Double.valueOf(data.getCell(5,4)).doubleValue();
            right_coordinate_z_1 = Double.valueOf(data.getCell(5,5)).doubleValue();
            right_coordinate_z_2 = Double.valueOf(data.getCell(5,6)).doubleValue();            
            
            // Reading of minimal and maximal values of parameters of thermal and mechanical cycling loading
            min_left_mech_value    = Double.valueOf(data.getCell(0, 9)).doubleValue();
            left_mech_value        = Double.valueOf(data.getCell(0,10)).doubleValue();
            min_left_therm_value   = Double.valueOf(data.getCell(0,11)).doubleValue();
            left_therm_value       = Double.valueOf(data.getCell(0,12)).doubleValue();
            
            min_front_mech_value   = Double.valueOf(data.getCell(1, 9)).doubleValue();
            front_mech_value       = Double.valueOf(data.getCell(1,10)).doubleValue();
            min_front_therm_value  = Double.valueOf(data.getCell(1,11)).doubleValue();
            front_therm_value      = Double.valueOf(data.getCell(1,12)).doubleValue();
            
            min_top_mech_value     = Double.valueOf(data.getCell(2, 9)).doubleValue();
            top_mech_value         = Double.valueOf(data.getCell(2,10)).doubleValue();
            min_top_therm_value    = Double.valueOf(data.getCell(2,11)).doubleValue();
            top_therm_value        = Double.valueOf(data.getCell(2,12)).doubleValue();
            
            min_bottom_mech_value  = Double.valueOf(data.getCell(3, 9)).doubleValue();
            bottom_mech_value      = Double.valueOf(data.getCell(3,10)).doubleValue();
            min_bottom_therm_value = Double.valueOf(data.getCell(3,11)).doubleValue();
            bottom_therm_value     = Double.valueOf(data.getCell(3,12)).doubleValue();

            min_back_mech_value    = Double.valueOf(data.getCell(4, 9)).doubleValue();
            back_mech_value        = Double.valueOf(data.getCell(4,10)).doubleValue();
            min_back_therm_value   = Double.valueOf(data.getCell(4,11)).doubleValue();
            back_therm_value       = Double.valueOf(data.getCell(4,12)).doubleValue();
            
            min_right_mech_value   = Double.valueOf(data.getCell(5, 9)).doubleValue();
            right_mech_value       = Double.valueOf(data.getCell(5,10)).doubleValue();
            min_right_therm_value  = Double.valueOf(data.getCell(5,11)).doubleValue();
            right_therm_value      = Double.valueOf(data.getCell(5,12)).doubleValue();
            
            // Reading of parameters of time functions for all tanks
            left_time_function_type        = Byte.valueOf  (data.getCell(0,13)).byteValue();
            left_loading_time_portion      = Double.valueOf(data.getCell(0,14)).doubleValue();
            left_relaxation_time_portion   = Double.valueOf(data.getCell(0,15)).doubleValue();
            
            front_time_function_type       = Byte.valueOf  (data.getCell(1,13)).byteValue();
            front_loading_time_portion     = Double.valueOf(data.getCell(1,14)).doubleValue();
            front_relaxation_time_portion  = Double.valueOf(data.getCell(1,15)).doubleValue();
            
            top_time_function_type         = Byte.valueOf  (data.getCell(2,13)).byteValue();
            top_loading_time_portion       = Double.valueOf(data.getCell(2,14)).doubleValue();
            top_relaxation_time_portion    = Double.valueOf(data.getCell(2,15)).doubleValue();
            
            bottom_time_function_type      = Byte.valueOf  (data.getCell(3,13)).byteValue();
            bottom_loading_time_portion    = Double.valueOf(data.getCell(3,14)).doubleValue();
            bottom_relaxation_time_portion = Double.valueOf(data.getCell(3,15)).doubleValue();
            
            back_time_function_type        = Byte.valueOf  (data.getCell(4,13)).byteValue();
            back_loading_time_portion      = Double.valueOf(data.getCell(4,14)).doubleValue();
            back_relaxation_time_portion   = Double.valueOf(data.getCell(4,15)).doubleValue();
            
            right_time_function_type       = Byte.valueOf  (data.getCell(5,13)).byteValue();
            right_loading_time_portion     = Double.valueOf(data.getCell(5,14)).doubleValue();
            right_relaxation_time_portion  = Double.valueOf(data.getCell(5,15)).doubleValue();
            
            System.out.println("READING of parameters of tanks FROM UI TANK is finished!!!");
            
            // TOP
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!data.getCell(2, 7).equals(N/A) =");
            
            if(!data.getCell(2, 7).equals("N/A"))
            {
                System.out.println("WE READ FROM FILE THAT TOP THERM ENERGY CHECK BOX IS SELECTED ");
                
                top_check_mechanical_loading = true;
                if(data.getCell(2, 7).equals("stress"))
                    top_stress = true;
                if(data.getCell(2, 7).equals("strain"))
                    top_strain = true;
            }
            else
                top_check_mechanical_loading = false;
            
            if(!data.getCell(2, 8).equals("N/A"))
            {
                top_check_thermal_influx = true;
                if(data.getCell(2, 8).equals("temperature"))
                    top_temperature = true;
                if(data.getCell(2, 8).equals("energy"))
                    top_energy = true;
            }
            else
                top_check_thermal_influx = false;

            // BOTTOM
            if(!data.getCell(3, 7).equals("N/A"))
            {
                bottom_check_mechanical_loading = true;
                if(data.getCell(3, 7).equals("stress"))
                    bottom_stress = true;
                if(data.getCell(3, 7).equals("strain"))
                    bottom_strain = true;
            }
            else
                bottom_check_mechanical_loading = false;
            if(!data.getCell(3, 8).equals("N/A"))
            {
                bottom_check_thermal_influx = true;
                if(data.getCell(3, 8).equals("temperature"))
                    bottom_temperature = true;
                if(data.getCell(3, 8).equals("energy"))
                    bottom_energy = true;
            }
            else
                bottom_check_thermal_influx = false;

            // LEFT
            if(!data.getCell(0, 7).equals("N/A"))
            {
                left_check_mechanical_loading = true;
                if(data.getCell(0, 7).equals("stress"))
                    left_stress = true;
                if(data.getCell(0, 7).equals("strain"))
                    left_strain = true;
            }
            else
                left_check_mechanical_loading = false;
            if(!data.getCell(0, 8).equals("N/A"))
            {
                left_check_thermal_influx = true;
                if(data.getCell(0, 8).equals("temperature"))
                    left_temperature = true;
                if(data.getCell(0, 8).equals("energy"))
                    left_energy = true;
            }
            else
                left_check_thermal_influx = false;

            // RIGHT
            if(!data.getCell(5, 7).equals("N/A"))
            {
                right_check_mechanical_loading = true;
                if(data.getCell(5, 7).equals("stress"))
                    right_stress = true;
                if(data.getCell(5, 7).equals("strain"))
                    right_strain = true;
            }
            else
                right_check_mechanical_loading = false;
            if(!data.getCell(5, 8).equals("N/A"))
            {
                right_check_thermal_influx = true;
                if(data.getCell(5, 8).equals("temperature"))
                    right_temperature = true;
                if(data.getCell(5, 8).equals("energy"))
                    right_energy = true;
            }
            else
                right_check_thermal_influx = false;

            // FRONT
            if(!data.getCell(1, 7).equals("N/A"))
            {
                front_check_mechanical_loading = true;
                if(data.getCell(1, 7).equals("stress"))
                    front_stress = true;
                if(data.getCell(1, 7).equals("strain"))
                    front_strain = true;
            }
            else
                front_check_mechanical_loading = false;
            if(!data.getCell(1, 8).equals("N/A"))
            {
                front_check_thermal_influx = true;
                if(data.getCell(1, 8).equals("temperature"))
                    front_temperature = true;
                if(data.getCell(1, 8).equals("energy"))
                    front_energy = true;
            }
            else
                front_check_thermal_influx = false;

           // BACK
            if(!data.getCell(4, 7).equals("N/A"))
            {
                back_check_mechanical_loading = true;
                if(data.getCell(4, 7).equals("stress"))
                    back_stress = true;
                if(data.getCell(4, 7).equals("strain"))
                    back_strain = true;
            }
            else
                back_check_mechanical_loading = false;
            
            if(!data.getCell(4, 8).equals("N/A"))
            {
                back_check_thermal_influx = true;
                
                if(data.getCell(4, 8).equals("temperature"))
                    back_temperature = true;
                
                if(data.getCell(4, 8).equals("energy"))
                    back_energy = true;
            }
            else
                back_check_thermal_influx = false;
            
            String tank_geom_type = data.getCell(6, 2);
            
            if(tank_geom_type.equals(UICommon.parallelepiped_tank))    parallelepiped_tank    = true;
            else                                                       parallelepiped_tank    = false;
            
            if(tank_geom_type.equals(UICommon.vertical_ellyptic_tank)) vertical_ellyptic_tank = true;
            else                                                       vertical_ellyptic_tank = false;
            
            if(tank_geom_type.equals(UICommon.horizon_triangle_tank))  horizon_triangle_tank  = true;
            else                                                       horizon_triangle_tank  = false;
            
            if(tank_geom_type.equals(UICommon.horizon_circle_tank))    horizon_circle_tank    = true;
            else                                                       horizon_circle_tank    = false;
            
            data_reader.close();
        }
        catch(FileNotFoundException w)
        {}
        catch(IOException q)
        {}
    }
}
