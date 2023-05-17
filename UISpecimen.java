/*
 * @(#) UISpecimen.java version 1.0.0;       May 22 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of specimen data, wich contain default value of
 * specimen parameters and default checked calculation method (regular or stochastic).
 * This class can load data from file in specific directory.
 * This class can set and get value of parameters and checked calculation method.
 *
 *=============================================================
 *  Last changes :
 *          15 Febrary 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.1.3
 *          Edit: field for remember packiging type:
 *          hexagonal close packing (HCP) or simple cubic packing (SCP)
 */

/** Class for creation bank of specimen data, wich contain default value of
 *  specimen parameters and default checked calculation method (regular or stochastic).
 *  This class can load data from file in specific directory.
 *  This class can set and get value of parameters and checked calculation method.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - May 2009
 */

package interf;

import java.util.Properties;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import util.*;

public class UISpecimen
{
    /**
     * This field is intended for creation a double value for remembering
     * in memory value of specimen parameters
     */

    protected double cell_size;
    protected double specimen_size_x,specimen_size_y,specimen_size_z;
    
    protected double coordinate_x, coordinate_y, coordinate_z, anisotropy_coeff;
    
    protected double average_grain_length, average_grain_length_2,
                     average_grain_length_3, average_grain_length_4, average_grain_length_5;
    protected double particle_volume_fraction,particle_radius;
    protected int element_number_x, element_number_y, element_number_z;
    protected double surf_thickness;
    protected int min_neighbours_number;
    
    protected double anis_vector_x, anis_vector_y, anis_vector_z, specimen_anisotropy_coeff;

    /**
     * This field is intended for remember packing type of specimen
     */

    protected int packing_type;

    protected int number_of_phases;

    /**
     * This field is intended for creating boolean value for remembering
     * in memory checked calculation method (regular or stochastic)
     */

    protected boolean stochastic_method,regular_method, mixed_method;

    /**
     * This field is intended for remembering in memory directory of choosed specimen
     */

    protected String specimen_path;

    /**
     * This field is intended for creating table of materials
     */

    public List<String> materials = new ArrayList<>();
    public List<Double> angle_range = new ArrayList<>();
    public List<Double> volume_fraction = new ArrayList<>();
    public List<Double> max_deviation = new ArrayList<>();
    public List<Double> disl_density = new ArrayList<>();

    /**
     * This field is intended for choose by user - write
     * grain cells or not
     */

    protected boolean grain_cellular_automata;

    /**
     * Create default bank of data UISpecimen
     */

    public UISpecimen()
    {
        System.out.println("UISpecimen: constructor creation start");

        /*
         * input default parameters value
         */

        cell_size = 0.0;

        element_number_x = 1;
        element_number_y = 1;
        element_number_z = 1;

        surf_thickness = 0.0;

        min_neighbours_number = 4;

        specimen_size_x = 0.0;
        specimen_size_y = 0.0;
        specimen_size_z = 0.0;

        coordinate_x = 1.0;
        coordinate_y = 0.0;
        coordinate_z = 0.0;

        anisotropy_coeff = 1.0;
                
        anis_vector_x = 1.0;
        anis_vector_y = 0.0;
        anis_vector_z = 0.0;
        specimen_anisotropy_coeff = 1.0;

        average_grain_length   = 0.0;
        average_grain_length_2 = 0.0;
        average_grain_length_3 = 0.0;
        average_grain_length_4 = 0.0;
        average_grain_length_5 = 0.0;

        number_of_phases = 5;

        particle_volume_fraction = 0.0;
        particle_radius = 0.0;

        /*
         * input default checked calculation method
         */

        stochastic_method = false;
        regular_method = false;
        mixed_method = true; // false; // 

        /*
         * do not write grain cells
         */

        grain_cellular_automata = true; // false; // 

        // Default paching type equals
        // hexagonal close packing (HCP)
        packing_type = 0;
        
        for(int i=0; i < number_of_phases; i++){
            materials.add("select_material...");
            angle_range.add(0.0d);
            volume_fraction.add(0.0d);
            max_deviation.add(0.0d);
            disl_density.add(0.0d);

        }
        
    }

    /**
     * This constructor is intended for creation UISpecimen by
     * assignment values of parameter UISpecimen from existing UISpecimen parameters
     * and checked calculation method
     * @param some_uispecimen - copy of UISpecimen
     */

    public UISpecimen(UISpecimen some_uispecimen)
    {
        System.out.println("UISpecimen: constructor(UISpecimen some_uispecimen) creation start");

        /*
         * assignment double values of existing parameters
         */
        
        cell_size = some_uispecimen.getCellSize();
        
        element_number_x = some_uispecimen.getElementNumberX();
        element_number_x = some_uispecimen.getElementNumberY();
        element_number_x = some_uispecimen.getElementNumberZ();

        surf_thickness = some_uispecimen.getSurfThickness();

        min_neighbours_number = some_uispecimen.getMinNeighboursNumber();

        specimen_size_x = some_uispecimen.getSpecimenSizeX();
        specimen_size_y = some_uispecimen.getSpecimenSizeY();
        specimen_size_z = some_uispecimen.getSpecimenSizeZ();

        coordinate_x = some_uispecimen.getCoordinateX();
        coordinate_y = some_uispecimen.getCoordinateY();
        coordinate_z = some_uispecimen.getCoordinateZ();

        anisotropy_coeff = some_uispecimen.getAnisotropyCoeff();
        
        average_grain_length = some_uispecimen.getAverageGrainLength();
        average_grain_length_2 = some_uispecimen.getAverageGrainLength_2();
        average_grain_length_3 = some_uispecimen.getAverageGrainLength_3();
        average_grain_length_4 = some_uispecimen.getAverageGrainLength_4();
        average_grain_length_5 = some_uispecimen.getAverageGrainLength_5();
        number_of_phases = some_uispecimen.getNumberOfPhases();

        particle_volume_fraction = some_uispecimen.getParticleVolumeFraction();
        particle_radius = some_uispecimen.getParticleRadius();

        grain_cellular_automata = some_uispecimen.getGrainCellularAutomata();

        // Assignment packing type
        packing_type = some_uispecimen.getPackingType();
        
        anis_vector_x = some_uispecimen.getAnisVectorX();
        anis_vector_y = some_uispecimen.getAnisVectorY();
        anis_vector_z = some_uispecimen.getAnisVectorZ();
        specimen_anisotropy_coeff = some_uispecimen.getSpecimenAnisotropyCoeff();

        /*
         * assignment existing checked position
         */
        stochastic_method = some_uispecimen.getStochasticMethod();
        regular_method = some_uispecimen.getRegularMethod();
        mixed_method = some_uispecimen.getMixedMethod();

        materials = some_uispecimen.materials;
        angle_range = some_uispecimen.angle_range;
        max_deviation = some_uispecimen.max_deviation;
        disl_density = some_uispecimen.disl_density;
        volume_fraction = some_uispecimen.volume_fraction;
    }

    /**
     * This constructor is intended for creating UISpecimen data bank witch
     * loading value of parameters and checked calculation method from file in
     * specific directory "specimen_file_name"
     * @param specimen_file_name - directory and name of read file
     */
    public UISpecimen(String specimen_file_name)
    {
        System.out.println("UISpecimen: constructor(String specimen_file_name) creation start");
        System.out.println("specimen_file_name: "+specimen_file_name+Common.SPECIMEN_EXTENSION);

        /*
         * Read file in specific directory "specimen_file_name"
         */
        read(specimen_file_name+Common.SPECIMEN_EXTENSION);
        
        read_table(specimen_file_name);
        
        /*
         * remembering directory of choosed specimen
         */
         specimen_path = new String(specimen_file_name);
    }

    /*
     * This method is intended for set new automata packing type
     * @param value - new automata packing type
     */

    public void setPackingType(int value)
    {
        packing_type = value;
    }

    /*
     * This method is intended for set new value of parameter "cell size x"
     * @param value - new value of cell size x
     */

    public void setCellSize(double value)
    {
        cell_size = value;
    }

    /*
     * This method is intended for set new value of parameter "specimen size x"
     * @param value - new value of specimen size x
     */

    public void setSpecimenSizeX(double value)
    {
        specimen_size_x = value;
    }

    /*
     * This method is intended for set new value of parameter "specimen size y"
     * @param value - new value of specimen size y
     */

    public void setSpecimenSizeY(double value)
    {
        specimen_size_y = value;
    }

    /*
     * This method is intended for set new value of parameter "specimen size z"
     * @param value - new value of specimen size z
     */

    public void setSpecimenSizeZ(double value)
    {
        specimen_size_z = value;
    }

    /*
     * This method is intended for set new value of parameter "coordinate x"
     * @param value - new value of coordinate x
     */

    public void setCoordinateX(double value)
    {
        coordinate_x = value;
    }

    /*
     * This method is intended for set new value of parameter "coordinate y"
     * @param value - new value of coordinate y
     */

    public void setCoordinateY(double value)
    {
        coordinate_y = value;
    }

    /*
     * This method is intended for set new value of parameter "coordinate z"
     * @param value - new value of coordinate z
     */

    public void setCoordinateZ(double value)
    {
        coordinate_z = value;
    }

    /*
     * This method is intended for set new value of parameter "anisotropy coeff"
     * @param value - new value of anisotropy coeff
     */
    public void setAnisotropyCoeff(double value)
    {
        anisotropy_coeff = value;
    }    
    
    /** This method sets the value of the coordinate X of the specimen anisotropy vector.
     * @param value - the value of the coordinate X of the specimen anisotropy vector
     */
    public void setAnisVectorX(double value)
    {
        anis_vector_x = value;
    }
    
    /** This method sets the value of the coordinate Y of the specimen anisotropy vector.
     * @param value - the value of the coordinate Y of the specimen anisotropy vector
     */
    public void setAnisVectorY(double value)
    {
        anis_vector_y = value;
    }

    /** This method sets the value of the coordinate Z of the specimen anisotropy vector.
     * @param value - the value of the coordinate Z of the specimen anisotropy vector
     */
    public void setAnisVectorZ(double value)
    {
        anis_vector_z = value;
    }

    /** The method returns the value of the specimen anisotropy coefficient.
     * @return the value of the specimen anisotropy coefficient
     */
    public void setSpecimenAnisotropyCoeff(double value)
    {
        specimen_anisotropy_coeff = value;
    }

    /*
     * This method is intended for set new value of parameter "average grain length"
     * @param value - new value of average grain length
     */

    public void setAverageGrainLength(double value, double value_2, double value_3, double value_4, double value_5)
    {
        average_grain_length = value;
        average_grain_length_2 = value_2;
        average_grain_length_3 = value_3;
        average_grain_length_4 = value_4;
        average_grain_length_5 = value_5;
    }

    /*
     * This method is intended for set new value of parameter "number of phases"
     * @param value - new value of number of phases
     */

    public void setNumberOfPhases(int value)
    {
        number_of_phases = value;
    }

    /*
     * This method is intended for set new value of parameter "particle volume fraction"
     * @param value - new value of particle volume fraction
     */

    public void setParticleVolumeFraction(double value)
    {
        particle_volume_fraction = value;
    }

    /*
     * This method is intended for set new value of parameter "particle radius"
     * @param value - new value of particle radius
     */

    public void setParticleRadius(double value)
    {
        particle_radius = value;
    }

    /*
     * This method is intended for set checked or not calculation method - "regular"
     * @param value - value which responsible for checked or not calculation method - "regular"
     */

    public void setRegularMethod(boolean value)
    {
        regular_method = value;
    }

    /*
     * This method is intended for set checked or not calculation method - "mixed"
     * @param value - value which responsible for checked or not calculation method - "mixed"
     */

    public void setMixedMethod(boolean value)
    {
        mixed_method = value;
    }

    /*
     * This method is intended for set checked or not calculation method - "stochastic"
     * @param value - value which responsible for checked or not calculation method - "stochastic"
     */

    public void setStochasticMethod(boolean value)
    {
        stochastic_method = value;
    }

    public void setElementNumberX(int value)
    {
        element_number_x = value;
    }

    public void setElementNumberY(int value)
    {
        element_number_y = value;
    }

    public void setElementNumberZ(int value)
    {
        element_number_z = value;
    }

    public void setSurfThickness(double value)
    {
        surf_thickness = value;
    }

    public void setMinNeighboursNumber(int value)
    {
        min_neighbours_number = value;
    }

    /*
     * set information about - write grain cells or not
     * @param value - write grain cells or not
     */

    public void setGrainCellularAutomata(boolean value)
    {
        grain_cellular_automata = value;
    }

    /*
     * This method is intended for get value automata packing type
     */

    public int getPackingType()
    {
        return packing_type;
    }

    /*
     * This method is intended for get value of parameter "cell size"
     */

    public double getCellSize()
    {
        return cell_size;
    }

    /*
     * This method is intended for get value of parameter "specimen size x"
     */

    public double getSpecimenSizeX()
    {
        return specimen_size_x;
    }

    /*
     * This method is intended for get value of parameter "specimen size y"
     */

    public double getSpecimenSizeY()
    {
        return specimen_size_y;
    }

    /*
     * This method is intended for get value of parameter "specimen size z"
     */

    public double getSpecimenSizeZ()
    {
        return specimen_size_z;
    }

    /** This method is intended for get value of parameter "coordinate x".
     */
    public double getCoordinateX()
    {
        return coordinate_x;
    }

    /** This method is intended for get value of parameter "coordinate y".
     */
    public double getCoordinateY()
    {
        return coordinate_y;
    }

    /** This method is intended for get value of parameter "coordinate z".
     */
    public double getCoordinateZ()
    {
        return coordinate_z;
    }

    /** This method is intended for get value of parameter "anisotropy coeff".
     */
    public double getAnisotropyCoeff()
    {
        return anisotropy_coeff;
    }
    
    /** The method returns the value of the coordinate X of the specimen anisotropy vector.
     * @return the value of the coordinate X of the specimen anisotropy vector
     */
    public double getAnisVectorX()
    {
        return anis_vector_x;
    }
    
    /** The method returns the value of the coordinate Y of the specimen anisotropy vector.
     * @return the value of the coordinate Y of the specimen anisotropy vector
     */
    public double getAnisVectorY()
    {
        return anis_vector_y;
    }
    
    /** The method returns the value of the coordinate Z of the specimen anisotropy vector.
     * @return the value of the coordinate Z of the specimen anisotropy vector
     */
    public double getAnisVectorZ()
    {
        return anis_vector_z;
    }
    
    /** The method returns the value of the specimen anisotropy coefficient.
     * @return the value of the specimen anisotropy coefficient
     */
    public double getSpecimenAnisotropyCoeff()
    {
        return specimen_anisotropy_coeff;
    }

    /** This method is intended for get value of parameter "average_grain-length".
     */
    public double getAverageGrainLength()
    {
        return average_grain_length;
    }
    
    /** This method is intended for get value of parameter "average_grain_length_2".
     */
    public double getAverageGrainLength_2()
    {
        return average_grain_length_2;
    }
    /** This method is intended for get value of parameter "average_grain_length_3".
     */
    public double getAverageGrainLength_3()
    {
        return average_grain_length_3;
    }

        /*
     * This method is intended for get value of parameter "average_grain_length_4"
     */

    public double getAverageGrainLength_4()
    {
        return average_grain_length_4;
    }


        /*
     * This method is intended for get value of parameter "average_grain_length_5"
     */

    public double getAverageGrainLength_5()
    {
        return average_grain_length_5;
    }

    /*
     * This method is intended for get value of parameter "number of phases"
     */

    public int getNumberOfPhases()
    {
        return number_of_phases;
    }

    /*
     * This method is intended for get value of parameter "particle volume fraction"
     */

    public double getParticleVolumeFraction()
    {
        return particle_volume_fraction;
    }

    /*
     * This method is intended for get value of parameter "particle radius"
     */

    public double getParticleRadius()
    {
        return particle_radius;
    }

    public int getElementNumberX()
    {
        return element_number_x;
    }

    public int getElementNumberY()
    {
        return element_number_y;
    }

    public int getElementNumberZ()
    {
        return element_number_z;
    }

    public double getSurfThickness()
    {
        return surf_thickness;
    }

    public int getMinNeighboursNumber()
    {
        return min_neighbours_number;
    }

    /*
     * This method is intended for get information - checked or not calculation method - "regular"
     */

    public boolean getRegularMethod()
    {
        return regular_method;
    }

    /*
     * This method is intended for get information - checked or not calculation method - "mixed"
     */

    public boolean getMixedMethod()
    {
        return mixed_method;
    }

    /*
     * This method is intended for get information - checked or not calculation method - "stochastic"
     */

    public boolean getStochasticMethod()
    {
        return stochastic_method;
    }

    /*
     * Get information about - is writed grain cells or not
     * @return - show or nor grain cells
     */

    public boolean getGrainCellularAutomata()
    {
        return grain_cellular_automata;
    }

    /*
     * This method is intended for loading value of parameters
     * and checked calculation method from file in specific directory with
     * specific name "specimen_file_name"
     * @param specimen_file_name - directory and name of readed file
     */

    private void read(String specimen_file_name)
    {
        /*
         * read data from file
         */

        Properties loading_properties = new Properties();
        try
        {
            FileInputStream loader = new FileInputStream(specimen_file_name);
            loading_properties.load(loader);
            loader.close();
        }
        catch(IOException io_ex)
        {
            System.out.println(UICommon.CATCH_ERROR__NAME+io_ex);
        }

        /*
         * read value of parameters from file in directory "task_file_name"
         */

        cell_size = Double.valueOf(loading_properties.getProperty(UICommon.CELL_SIZE_PROPERTIES)).doubleValue();
        
        element_number_x = Integer.valueOf(loading_properties.getProperty(UICommon.ELEMENT_NUMBER_X_PROPERTIES)).intValue();
        element_number_y = Integer.valueOf(loading_properties.getProperty(UICommon.ELEMENT_NUMBER_Y_PROPERTIES)).intValue();
        element_number_z = Integer.valueOf(loading_properties.getProperty(UICommon.ELEMENT_NUMBER_Z_PROPERTIES)).intValue();

        surf_thickness = Double.valueOf(loading_properties.getProperty(UICommon.SURF_THICKNESS_PROPERTIES)).doubleValue();

        min_neighbours_number = Integer.valueOf(loading_properties.getProperty(UICommon.MIN_NEIGHBOURS_NUMBER_PROPERTIES)).intValue();

        specimen_size_x = Double.valueOf(loading_properties.getProperty(UICommon.SPECIMEN_SIZE_X_PROPERTIES)).doubleValue()*cell_size/2;
        specimen_size_y = Double.valueOf(loading_properties.getProperty(UICommon.SPECIMEN_SIZE_Y_PROPERTIES)).doubleValue()*cell_size/2;
        specimen_size_z = Double.valueOf(loading_properties.getProperty(UICommon.SPECIMEN_SIZE_Z_PROPERTIES)).doubleValue()*cell_size/2;

        coordinate_x = Double.valueOf(loading_properties.getProperty(UICommon.COORDINATE_X_PROPERTIES)).doubleValue();
        coordinate_y = Double.valueOf(loading_properties.getProperty(UICommon.COORDINATE_Y_PROPERTIES)).doubleValue();
        coordinate_z = Double.valueOf(loading_properties.getProperty(UICommon.COORDINATE_Z_PROPERTIES)).doubleValue();

        anisotropy_coeff = Double.valueOf(loading_properties.getProperty(UICommon.ANISOTROPY_COEFF_PROPERTIES)).doubleValue();
        
        anis_vector_x = Double.valueOf(loading_properties.getProperty(UICommon.ANIS_VECTOR_X_NAME)).doubleValue();
        anis_vector_y = Double.valueOf(loading_properties.getProperty(UICommon.ANIS_VECTOR_Y_NAME)).doubleValue();
        anis_vector_z = Double.valueOf(loading_properties.getProperty(UICommon.ANIS_VECTOR_Z_NAME)).doubleValue();

        specimen_anisotropy_coeff = Double.valueOf(loading_properties.getProperty(UICommon.SPEC_ANIS_COEFF_NAME)).doubleValue();

        /*
        average_grain_length = Double.valueOf(loading_properties.getProperty(UICommon.AVERAGE_GRAIN_LENGTH_PROPERTIES)).doubleValue()*cell_size;
        average_grain_length_2 = Double.valueOf(loading_properties.getProperty(UICommon.AVERAGE_GRAIN_LENGTH_2_PROPERTIES)).doubleValue()*cell_size;
        average_grain_length_3 = Double.valueOf(loading_properties.getProperty(UICommon.AVERAGE_GRAIN_LENGTH_3_PROPERTIES)).doubleValue()*cell_size;
        average_grain_length_4 = Double.valueOf(loading_properties.getProperty(UICommon.AVERAGE_GRAIN_LENGTH_4_PROPERTIES)).doubleValue()*cell_size;
        average_grain_length_5 = Double.valueOf(loading_properties.getProperty(UICommon.AVERAGE_GRAIN_LENGTH_5_PROPERTIES)).doubleValue()*cell_size;
        *
        */

        number_of_phases = Integer.valueOf(loading_properties.getProperty(UICommon.NUMBER_OF_PHASES_PROPERTIES)).intValue();

        particle_volume_fraction = Double.valueOf(loading_properties.getProperty(UICommon.PARTICLE_VOLUME_FRACTION_PROPERTIES)).doubleValue();
        particle_radius = Double.valueOf(loading_properties.getProperty(UICommon.PARTICLE_RADIUS_PROPERTIES)).doubleValue();

        packing_type = Integer.valueOf(loading_properties.getProperty(UICommon.PACKING_TYPE_PROPERTIES)).intValue();
        
        if(loading_properties.getProperty("show_grain_bounds").equals("0"))
            grain_cellular_automata = false;
        if(loading_properties.getProperty("show_grain_bounds").equals("1"))
            grain_cellular_automata = true;

        /*
         * read checked positions
         */

        if (loading_properties.getProperty(UICommon.METHOD_NAME).equals(UICommon.METHOD_STOCHASTIC_PROPERTIES))
        {
            regular_method = false;
            stochastic_method = true;
            mixed_method = false;
        }
        if (loading_properties.getProperty(UICommon.METHOD_NAME).equals(UICommon.METHOD_REGULAR_PROPERTIES))
        {
            stochastic_method = false;
            regular_method = true;
            mixed_method = false;
        }
        if (loading_properties.getProperty(UICommon.METHOD_NAME).equals(UICommon.METHOD_MIXED_PROPERTIES))
        {
            stochastic_method = false;
            regular_method = false;
            mixed_method = true;
        }

    }
    
    public void read_table(String specimen_name){
        File file;
        file = new File(specimen_name+Common.SPECIMEN_MATERIAL_EXTENSION);
        
        if(file.exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                
                while((line = br.readLine())!=null){
                    String[] tokens = line.split(" ");
                    volume_fraction.add(Double.parseDouble(tokens[0]));
                    materials.add(tokens[1]);
                    angle_range.add(Double.parseDouble(tokens[2]));
                    disl_density.add(Double.parseDouble(tokens[3]));
                    max_deviation.add(Double.parseDouble(tokens[4]));
                }
                
            } catch (FileNotFoundException ex) {
                System.out.println("File is not exist exception!!!");
            } catch (IOException ex) {
                System.out.println("Error: can't read " + file.getName());
                System.out.println("Exception: " + ex);
            }
        }
    }

    
}
