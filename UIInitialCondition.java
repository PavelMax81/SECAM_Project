/*
 * @(#) UIInitialCondition.java version 1.0.0;       May 22 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of initial condition data, wich contain default
 * value of initial condition parameters.
 * This class can load data from file in specific directory.
 * This class can set and get value of parameters.
 *
 *=============================================================
 *  Last changes :
 *          25 December 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.5
 *          Edit: Add fields for case special specimen - fifth
 *          line in init cond file
 */

/** Class for creation bank of initial condition data, wich contain default
 *  value of initial condition parameters.
 *  This class can load data from file in specific directory.
 *  This class can set and get value of parameters.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - May 2009
 */

package interf;

import java.io.*;
import util.*;

public class UIInitialCondition
{
    /**
     * This field is intended for creation a double value for remembering
     * in memory value of initial condition parameters
     */

    protected double first_thermal_energy, first_mechanical_energy,
            first_temperature, second_thermal_energy,
            second_mechanical_energy, second_temperature,
            third_thermal_energy, third_mechanical_energy,
            third_temperature, fourth_thermal_energy,
            fourth_mechanical_energy, fourth_temperature,
            fifth_thermal_energy, fifth_mechanical_energy,
            fifth_temperature;

    /**
     * Create default bank of data UIInitialCondition
     */

    public UIInitialCondition()
    {
        System.out.println("UIInitialCondition: constructor creation start");

        /*
         * input default parameters value
         */

        first_thermal_energy     =   0.0;
        first_mechanical_energy  =   0.0;
        first_temperature        = 300.0;

        second_thermal_energy    =   0.0;
        second_mechanical_energy =   0.0;
        second_temperature       = 300.0;

        third_thermal_energy     =   0.0;
        third_mechanical_energy  =   0.0;
        third_temperature        = 300.0;

        fourth_thermal_energy    =   0.0;
        fourth_mechanical_energy =   0.0;
        fourth_temperature       = 300.0;

        fifth_thermal_energy     =   0.0;
        fifth_mechanical_energy  =   0.0;
        fifth_temperature        = 300.0;
    }

    /** This constructor is intended for creation UIInitialCondition by
     * assignment values of parameter UIInitialCondition from existing
     * UIInitialCondition parameters
     * @param some_uiinitcond - copy of UIInitialCondition
     */
    public UIInitialCondition(UIInitialCondition some_uiinitcond)
    {
        System.out.println("UIInitialCondition: constructor(UIInitialCondition some_uiinitcond) creation start");

        // assignment of double values of existing parameters
        first_thermal_energy     = some_uiinitcond.getFirstThermalEnergy();
        first_mechanical_energy  = some_uiinitcond.getFirstMechanicalEnergy();
        first_temperature        = some_uiinitcond.getFirstTemperature();

        second_thermal_energy    = some_uiinitcond.getSecondThermalEnergy();
        second_mechanical_energy = some_uiinitcond.getSecondMechanicalEnergy();
        second_temperature       = some_uiinitcond.getSecondTemperature();

        third_thermal_energy     = some_uiinitcond.getThirdThermalEnergy();
        third_mechanical_energy  = some_uiinitcond.getThirdMechanicalEnergy();
        third_temperature        = some_uiinitcond.getThirdTemperature();

        fourth_thermal_energy    = some_uiinitcond.getFourthThermalEnergy();
        fourth_mechanical_energy = some_uiinitcond.getFourthMechanicalEnergy();
        fourth_temperature       = some_uiinitcond.getFourthTemperature();

        fifth_thermal_energy     = some_uiinitcond.getFifthThermalEnergy();
        fifth_mechanical_energy  = some_uiinitcond.getFifthMechanicalEnergy();
        fifth_temperature        = some_uiinitcond.getFifthTemperature();
    }

    /** This constructor is intended for creating UIInitialCondition data bank with
     * loading value of parameters from file in
     * specific directory "init_cond_file_name"
     * @param init_cond_file_name - directory and name of readed file
     */
    public UIInitialCondition(String init_cond_file_name)
    {
        System.out.println("UIInitialCondition: constructor(String init_cond_file_name) creation start");

        System.out.println("init_cond_file_name: "+init_cond_file_name);
        /*
         * Read file in specific directory "init_cond_file_name"
         */

        read(init_cond_file_name);

    }

    /*
     * This method is intended for set new value of parameter "thermal energy"
     * @param value - new value of thermal energy
     */

    public void setFirstThermalEnergy(double value)
    {
        first_thermal_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "mechanical energy"
     * @param value - new value of mechanical energy
     */

    public void setFirstMechanicalEnergy(double value)
    {
        first_mechanical_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "temperature"
     * @param value - new value of temperature
     */

    public void setFirstTemperature(double value)
    {
        first_temperature = value;
    }

    /*
     * This method is intended for set new value of parameter "thermal energy"
     * @param value - new value of thermal energy
     */

    public void setSecondThermalEnergy(double value)
    {
        second_thermal_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "mechanical energy"
     * @param value - new value of mechanical energy
     */

    public void setSecondMechanicalEnergy(double value)
    {
        second_mechanical_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "temperature"
     * @param value - new value of temperature
     */

    public void setSecondTemperature(double value)
    {
        second_temperature = value;
    }

    /*
     * This method is intended for set new value of parameter "thermal energy"
     * @param value - new value of thermal energy
     */

    public void setThirdThermalEnergy(double value)
    {
        third_thermal_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "mechanical energy"
     * @param value - new value of mechanical energy
     */

    public void setThirdMechanicalEnergy(double value)
    {
        third_mechanical_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "temperature"
     * @param value - new value of temperature
     */

    public void setThirdTemperature(double value)
    {
        third_temperature = value;
    }

    /*
     * This method is intended for set new value of parameter "thermal energy"
     * @param value - new value of thermal energy
     */

    public void setFourthThermalEnergy(double value)
    {
        fourth_thermal_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "mechanical energy"
     * @param value - new value of mechanical energy
     */

    public void setFourthMechanicalEnergy(double value)
    {
        fourth_mechanical_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "temperature"
     * @param value - new value of temperature
     */

    public void setFourthTemperature(double value)
    {
        fourth_temperature = value;
    }

    /*
     * This method is intended for set new value of parameter "thermal energy"
     * @param value - new value of thermal energy
     */

    public void setFifthThermalEnergy(double value)
    {
        fifth_thermal_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "mechanical energy"
     * @param value - new value of mechanical energy
     */

    public void setFifthMechanicalEnergy(double value)
    {
        fifth_mechanical_energy = value;
    }

    /*
     * This method is intended for set new value of parameter "temperature"
     * @param value - new value of temperature
     */

    public void setFifthTemperature(double value)
    {
        fifth_temperature = value;
    }

    /*
     * This method is intended for get value of parameter "thermal energy"
     */

    public double getFirstThermalEnergy()
    {
        return first_thermal_energy;
    }

    /*
     * This method is intended for get value of parameter "mechanical energy"
     */

    public double getFirstMechanicalEnergy()
    {
        return first_mechanical_energy;
    }

    /*
     * This method is intended for get value of parameter "temperature"
     */

    public double getFirstTemperature()
    {
        return first_temperature;
    }

    /*
     * This method is intended for get value of parameter "thermal energy"
     */

    public double getSecondThermalEnergy()
    {
        return second_thermal_energy;
    }

    /*
     * This method is intended for get value of parameter "mechanical energy"
     */

    public double getSecondMechanicalEnergy()
    {
        return second_mechanical_energy;
    }

    /*
     * This method is intended for get value of parameter "temperature"
     */

    public double getSecondTemperature()
    {
        return second_temperature;
    }

    /*
     * This method is intended for get value of parameter "thermal energy"
     */

    public double getThirdThermalEnergy()
    {
        return third_thermal_energy;
    }

    /*
     * This method is intended for get value of parameter "mechanical energy"
     */

    public double getThirdMechanicalEnergy()
    {
        return third_mechanical_energy;
    }

    /*
     * This method is intended for get value of parameter "temperature"
     */

    public double getThirdTemperature()
    {
        return third_temperature;
    }

    /*
     * This method is intended for get value of parameter "thermal energy"
     */

    public double getFourthThermalEnergy()
    {
        return fourth_thermal_energy;
    }

    /*
     * This method is intended for get value of parameter "mechanical energy"
     */

    public double getFourthMechanicalEnergy()
    {
        return fourth_mechanical_energy;
    }

    /*
     * This method is intended for get value of parameter "temperature"
     */

    public double getFourthTemperature()
    {
        return fourth_temperature;
    }

    /*
     * This method is intended for get value of parameter "thermal energy"
     */

    public double getFifthThermalEnergy()
    {
        return fifth_thermal_energy;
    }

    /*
     * This method is intended for get value of parameter "mechanical energy"
     */

    public double getFifthMechanicalEnergy()
    {
        return fifth_mechanical_energy;
    }

    /*
     * This method is intended for get value of parameter "temperature"
     */

    public double getFifthTemperature()
    {
        return fifth_temperature;
    }

    /*
     * This method is intended for loading value of parameters
     * from file in specific directory with
     * specific name "init_cond_file_name"
     * @param init_cond_file_name - directory and name of readed file
     */

    public void read(String init_cond_file_name)
    {
        /**
         * read data from file
         */

        StringVector data = null;
        
        try
        {
            TextTableFileReader data_reader = new TextTableFileReader(init_cond_file_name);
            data = data_reader.convertToTable();
        }
        catch(FileNotFoundException w)
        {
            System.out.println("Error: "+w);
        }
        
        // Number of strings in file of initial conditions
//        int rows_num = data.getRowsNum();
        
        // Reading from 1st string
        first_thermal_energy         = Double.valueOf(data.getCell(0, 0)).doubleValue();
        first_mechanical_energy      = Double.valueOf(data.getCell(0, 1)).doubleValue();
        first_temperature            = Double.valueOf(data.getCell(0, 2)).doubleValue();
        
        // Reading from following strings
//        if(rows_num>1)
//        {
            second_thermal_energy    = Double.valueOf(data.getCell(1, 0)).doubleValue();
            second_mechanical_energy = Double.valueOf(data.getCell(1, 1)).doubleValue();
            second_temperature       = Double.valueOf(data.getCell(1, 2)).doubleValue();

            third_thermal_energy     = Double.valueOf(data.getCell(2, 0)).doubleValue();
            third_mechanical_energy  = Double.valueOf(data.getCell(2, 1)).doubleValue();
            third_temperature        = Double.valueOf(data.getCell(2, 2)).doubleValue();

            fourth_thermal_energy    = Double.valueOf(data.getCell(3, 0)).doubleValue();
            fourth_mechanical_energy = Double.valueOf(data.getCell(3, 1)).doubleValue();
            fourth_temperature       = Double.valueOf(data.getCell(3, 2)).doubleValue();

            fifth_thermal_energy    = Double.valueOf(data.getCell(4, 0)).doubleValue();
            fifth_mechanical_energy = Double.valueOf(data.getCell(4, 1)).doubleValue();
            fifth_temperature       = Double.valueOf(data.getCell(4, 2)).doubleValue();
//        }
//	  else
//	  {
/*		second_thermal_energy    = first_thermal_energy;
                second_mechanical_energy = first_mechanical_energy;
                second_temperature       = first_temperature;

                third_thermal_energy     = first_thermal_energy;
                third_mechanical_energy  = first_mechanical_energy;
                third_temperature        = first_temperature;

                fourth_thermal_energy    = first_thermal_energy;
                fourth_mechanical_energy = first_mechanical_energy;
                fourth_temperature       = first_temperature;

                fifth_thermal_energy    = first_thermal_energy;
                fifth_mechanical_energy = first_mechanical_energy;
                fifth_temperature       = first_temperature;
*/
//        }
    }
}
