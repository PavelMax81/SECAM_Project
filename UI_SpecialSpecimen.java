/*
 * @(#) UI_SpecialSpecimen.java version 1.0.0;       December 2 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of special specimen data
 *
 *=============================================================
 *  Last changes :
 *          25 December 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.1
 *          Edit: Add new field for inpur third layer thickness
 */

/** Class for creation bank of special specimen data
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - December 2009
 */

package interf;

import java.util.Properties;
import java.io.*;

public class UI_SpecialSpecimen
{
    /** This field is intended for remember special
     * specimen parameter values
     */
    protected double first_layer_thickness;
    protected double second_layer_thickness;
    protected double third_layer_thickness;
    
    protected double layer_elem_size;
    
    protected double first_layer_grain_size,        first_layer_grain_size_Y,        first_layer_grain_size_Z;
    protected double upper_second_layer_grain_size, upper_second_layer_grain_size_Y, upper_second_layer_grain_size_Z;
    protected double lower_second_layer_grain_size, lower_second_layer_grain_size_Y, lower_second_layer_grain_size_Z;
    protected double third_layer_grain_size,        third_layer_grain_size_Y,        third_layer_grain_size_Z;
    protected double substrate_grain_size,          substrate_grain_size_Y,          substrate_grain_size_Z;
    
    /** Values of thickness of grain boundary region (in cell diameters) 
     * for lower substrate, surface layer, 1st and 2nd intermediate layers 
     * and upper substrate respectively */
    protected double first_layer_grain_bound_region_thickness;
    protected double upper_second_layer_grain_bound_region_thickness;
    protected double lower_second_layer_grain_bound_region_thickness;
    protected double third_layer_grain_bound_region_thickness;
    protected double substrate_grain_bound_region_thickness;
    
    /** The value responsible for choice of geometrical type of intermediate layer 
     */
    protected byte layer_type;
    
    /** The value responsible for choice of the direction of vector normal to layer interfaces
     */
    public static byte layer_direction;
    
    /** Variables responsible for type of grain embryo distribution in layers*/
    protected byte surface_layer_embryo_distr_type;
    protected byte upper_inter_layer_embryo_distr_type;    
    protected byte lower_inter_layer_embryo_distr_type;
    protected byte upper_substrate_embryo_distr_type;
    protected byte lower_substrate_embryo_distr_type;       
    
    /** The constructor creates special specimen data bank with default parameters
     */
    public UI_SpecialSpecimen()
    {
        System.out.println("UI_SpecialSpecimen: constructor creation start");

        // Set default parameter values
        first_layer_thickness  = 0.0;
        second_layer_thickness = 0.0;
        third_layer_thickness  = 0.0;

        first_layer_grain_size        = 0; first_layer_grain_size_Y = 0;        first_layer_grain_size_Z = 0;
        upper_second_layer_grain_size = 0; upper_second_layer_grain_size_Y = 0; upper_second_layer_grain_size_Z = 0;
        lower_second_layer_grain_size = 0; lower_second_layer_grain_size_Y = 0; lower_second_layer_grain_size_Z = 0;
        third_layer_grain_size        = 0; third_layer_grain_size_Y = 0;        third_layer_grain_size_Z = 0;
        substrate_grain_size          = 0; substrate_grain_size_Y = 0;          substrate_grain_size_Z = 0;
        
        /** Values of thickness of grain boundary region (in cell diameters) 
         * for lower substrate, surface layer, 1st and 2nd intermediate layers 
         * and upper substrate respectively */
        first_layer_grain_bound_region_thickness        = 0;
        upper_second_layer_grain_bound_region_thickness = 0;
        lower_second_layer_grain_bound_region_thickness = 0;
        third_layer_grain_bound_region_thickness        = 0;
        substrate_grain_bound_region_thickness          = 0;

        layer_elem_size = 0.0;
        layer_type      = 0;
        layer_direction = 2;
        
        surface_layer_embryo_distr_type     = UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE;
        upper_inter_layer_embryo_distr_type = UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE;
        lower_inter_layer_embryo_distr_type = UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE;
        upper_substrate_embryo_distr_type   = UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE;
        lower_substrate_embryo_distr_type   = UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE;
    }

    /**
     * Create special specimen data bank with existed parameters
     * @param some_ui_special_specimen - existed special specimen data bank
     */

    public UI_SpecialSpecimen(UI_SpecialSpecimen some_ui_special_specimen)
    {
        System.out.println("UI_SpecialSpecimen: constructor(UI_SpecialSpecimen some_ui_special_specimen) creation start");

        // Assignment values of existing parameters
        first_layer_thickness  = some_ui_special_specimen.getFirstLayerThickness();
        second_layer_thickness = some_ui_special_specimen.getSecondLayerThickness();
        third_layer_thickness  = some_ui_special_specimen.getThirdLayerThickness();

        first_layer_grain_size          = some_ui_special_specimen.getFirstLayerGrainSize();
        first_layer_grain_size_Y        = some_ui_special_specimen.getFirstLayerGrainSizeY();
        first_layer_grain_size_Z        = some_ui_special_specimen.getFirstLayerGrainSizeZ();
        
        upper_second_layer_grain_size   = some_ui_special_specimen.getUpperSecondLayerGrainSize();
        upper_second_layer_grain_size_Y = some_ui_special_specimen.getUpperSecondLayerGrainSizeY();
        upper_second_layer_grain_size_Z = some_ui_special_specimen.getUpperSecondLayerGrainSizeZ();
        
        lower_second_layer_grain_size   = some_ui_special_specimen.getLowerSecondLayerGrainSize();
        lower_second_layer_grain_size_Y = some_ui_special_specimen.getLowerSecondLayerGrainSizeY();
        lower_second_layer_grain_size_Z = some_ui_special_specimen.getLowerSecondLayerGrainSizeZ();
        
        third_layer_grain_size          = some_ui_special_specimen.getThirdLayerGrainSize();
        third_layer_grain_size_Y        = some_ui_special_specimen.getThirdLayerGrainSizeY();
        third_layer_grain_size_Z        = some_ui_special_specimen.getThirdLayerGrainSizeZ();
        
        substrate_grain_size            = some_ui_special_specimen.getSubstrateGrainSize();
        substrate_grain_size_Y          = some_ui_special_specimen.getSubstrateGrainSizeY();
        substrate_grain_size_Z          = some_ui_special_specimen.getSubstrateGrainSizeZ();
        
        /** Values of thickness of grain boundary region (in cell diameters) 
         * for lower substrate, surface layer, 1st and 2nd intermediate layers 
         * and upper substrate respectively */
        first_layer_grain_bound_region_thickness        = some_ui_special_specimen.getFirstLayerGrainBoundThickness();
        upper_second_layer_grain_bound_region_thickness = some_ui_special_specimen.getUpperSecondLayerGrainBoundThickness();
        lower_second_layer_grain_bound_region_thickness = some_ui_special_specimen.getLowerSecondLayerGrainBoundThickness();
        third_layer_grain_bound_region_thickness        = some_ui_special_specimen.getThirdLayerGrainBoundThickness();
        substrate_grain_bound_region_thickness          = some_ui_special_specimen.getSubstrateGrainBoundThickness();

        layer_elem_size = some_ui_special_specimen.getLayerElemSize();
        layer_type      = some_ui_special_specimen.getLayerType();
        layer_direction = some_ui_special_specimen.getLayerDirection();
        
        surface_layer_embryo_distr_type     = some_ui_special_specimen.getSurfaceLayerEmbryoDistrType();
        upper_inter_layer_embryo_distr_type = some_ui_special_specimen.getUpperInterLayerEmbryoDistrType();
        lower_inter_layer_embryo_distr_type = some_ui_special_specimen.getLowerInterLayerEmbryoDistrType();
        upper_substrate_embryo_distr_type   = some_ui_special_specimen.getUpperSubstrateEmbryoDistrType();
        lower_substrate_embryo_distr_type   = some_ui_special_specimen.getLowerSubstrateEmbryoDistrType();
    }
    
    /**
     * Create special specimen data bank with data from file
     * @param special_specimen_file_name - file name with data
     */

    public UI_SpecialSpecimen(String special_specimen_file_name)
    {
        System.out.println("Constructor UI_SpecialSpecimen("+special_specimen_file_name+".lrs) is started...");

        // Read data file
        readSpecialSpecimenData(special_specimen_file_name+".lrs");

        //TEST
        System.out.println();
        System.out.println("first_layer_thickness = "+first_layer_thickness);
        System.out.println("second_layer_thickness = "+second_layer_thickness);
        System.out.println("third_layer_thickness = "+third_layer_thickness);
        System.out.println();

        System.out.println("first layer grain sizes:        "+first_layer_grain_size+" "+first_layer_grain_size_Y+" "+first_layer_grain_size_Z);
        System.out.println("upper second layer grain sizes: "+upper_second_layer_grain_size+" "+upper_second_layer_grain_size_Y+" "+upper_second_layer_grain_size_Z);
        System.out.println("lower second layer grain sizes: "+lower_second_layer_grain_size+" "+lower_second_layer_grain_size_Y+" "+lower_second_layer_grain_size_Z);
        System.out.println("third layer grain sizes:        "+third_layer_grain_size+" "+third_layer_grain_size_Y+" "+third_layer_grain_size_Z);
        System.out.println("substrate grain sizes:          "+substrate_grain_size+" "+substrate_grain_size_Y+" "+substrate_grain_size_Z);
        System.out.println();
        
        /** Values of thickness of grain boundary region (in cell diameters) 
         * for lower substrate, surface layer, 1st and 2nd intermediate layers 
         * and upper substrate respectively */
        System.out.println("first_layer_grain_bound_region_thickness = "+first_layer_grain_bound_region_thickness);
        System.out.println("upper_second_layer_grain_bound_region_thickness = "+upper_second_layer_grain_bound_region_thickness);
        System.out.println("lower_second_layer_grain_bound_region_thickness = "+lower_second_layer_grain_bound_region_thickness);
        System.out.println("third_layer_grain_bound_region_thickness = "+third_layer_grain_bound_region_thickness);
        System.out.println("substrate_grain_bound_region_thickness = "+substrate_grain_bound_region_thickness);
        System.out.println();
        
        System.out.println(UICommon.LAYER_ELEM_SIZE_PROPERTIES+" = "+layer_elem_size);
        System.out.println(UICommon.LAYER_TYPE_PROPERTIES+" = "+layer_type);
        System.out.println(UICommon.LAYER_DIRECTION_PROPERTIES+" = "+layer_direction);
        System.out.println();
        
        System.out.println("surface_layer_embryo_distr_type = "    + surface_layer_embryo_distr_type);
        System.out.println("upper_inter_layer_embryo_distr_type = "+ upper_inter_layer_embryo_distr_type);
        System.out.println("lower_inter_layer_embryo_distr_type = "+ lower_inter_layer_embryo_distr_type);
        System.out.println("upper_substrate_embryo_distr_type = "  + upper_substrate_embryo_distr_type);
        System.out.println("lower_substrate_embryo_distr_type = "  + lower_substrate_embryo_distr_type);
        System.out.println();
    }
    
    /** The reads and sets special specimen data with the help of data file.
     * @param file_name - data file name
     */
    public void readSpecialSpecimenData(String file_name)
    {
        // Read data from file
        try
        {
            Properties special_specimen_data_properties = new Properties();
            FileInputStream special_specimen_loader = new FileInputStream(file_name);
            special_specimen_data_properties.load(special_specimen_loader);
            special_specimen_loader.close();            

            // Set special specimen parameter values
            first_layer_thickness           = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.FIRST_LAYER_THICKNESS_PROPERTIES)).doubleValue();
            second_layer_thickness          = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.SECOND_LAYER_THICKNESS_PROPERTIES)).doubleValue();
            third_layer_thickness           = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.THIRD_LAYER_THICKNESS_PROPERTIES)).doubleValue();

            first_layer_grain_size          = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.FIRST_LAYER_GRAIN_SIZE_PROPERTIES)).doubleValue();
            first_layer_grain_size_Y        = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.FIRST_LAYER_GRAIN_SIZE_Y_PROPERTIES)).doubleValue();
            first_layer_grain_size_Z        = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.FIRST_LAYER_GRAIN_SIZE_Z_PROPERTIES)).doubleValue();
            
            upper_second_layer_grain_size   = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES)).doubleValue();
            upper_second_layer_grain_size_Y = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_Y_PROPERTIES)).doubleValue();
            upper_second_layer_grain_size_Z = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_Z_PROPERTIES)).doubleValue();
            
            lower_second_layer_grain_size   = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES)).doubleValue();
            lower_second_layer_grain_size_Y = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_Y_PROPERTIES)).doubleValue();
            lower_second_layer_grain_size_Z = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_Z_PROPERTIES)).doubleValue();
            
            third_layer_grain_size          = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.THIRD_LAYER_GRAIN_SIZE_PROPERTIES)).doubleValue();
            third_layer_grain_size_Y        = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.THIRD_LAYER_GRAIN_SIZE_Y_PROPERTIES)).doubleValue();
            third_layer_grain_size_Z        = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.THIRD_LAYER_GRAIN_SIZE_Z_PROPERTIES)).doubleValue();
            
            substrate_grain_size            = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.SUBSTRATE_GRAIN_SIZE_PROPERTIES)).doubleValue();
            substrate_grain_size_Y          = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.SUBSTRATE_GRAIN_SIZE_Y_PROPERTIES)).doubleValue();
            substrate_grain_size_Z          = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.SUBSTRATE_GRAIN_SIZE_Z_PROPERTIES)).doubleValue();
            
            /** Values of thickness of grain boundary region (in cell diameters) 
             * for lower substrate, surface layer, 1st and 2nd intermediate layers 
             * and upper substrate respectively */
            first_layer_grain_bound_region_thickness        = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.FIRST_LAYER_GRAIN_BOUND_THICK_PROPERTIES)).doubleValue();
            upper_second_layer_grain_bound_region_thickness = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.UPPER_SECOND_LAYER_GRAIN_BOUND_THICK_PROPERTIES)).doubleValue();
            lower_second_layer_grain_bound_region_thickness = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.LOWER_SECOND_LAYER_GRAIN_BOUND_THICK_PROPERTIES)).doubleValue();
            third_layer_grain_bound_region_thickness        = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.THIRD_LAYER_GRAIN_BOUND_THICK_PROPERTIES)).doubleValue();
            substrate_grain_bound_region_thickness          = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.SUBSTRATE_GRAIN_BOUND_THICK_PROPERTIES)).doubleValue();

            layer_elem_size               = Double.valueOf(special_specimen_data_properties.getProperty(UICommon.LAYER_ELEM_SIZE_PROPERTIES)).doubleValue();
            layer_type                    = Byte.valueOf(special_specimen_data_properties.getProperty(UICommon.LAYER_TYPE_PROPERTIES)).byteValue();
            layer_direction               = Byte.valueOf(special_specimen_data_properties.getProperty(UICommon.LAYER_DIRECTION_PROPERTIES)).byteValue();
            
            surface_layer_embryo_distr_type     = Byte.valueOf(special_specimen_data_properties.getProperty(UICommon.SURFACE_LAYER_EMBRYO_DISTRIBUTION_TYPE)).byteValue();
            upper_inter_layer_embryo_distr_type = Byte.valueOf(special_specimen_data_properties.getProperty(UICommon.UPPER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE)).byteValue();
            lower_inter_layer_embryo_distr_type = Byte.valueOf(special_specimen_data_properties.getProperty(UICommon.LOWER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE)).byteValue();
            upper_substrate_embryo_distr_type   = Byte.valueOf(special_specimen_data_properties.getProperty(UICommon.UPPER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE)).byteValue();
            lower_substrate_embryo_distr_type   = Byte.valueOf(special_specimen_data_properties.getProperty(UICommon.LOWER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE)).byteValue();
        }
        catch(IOException io_ex)
        {
            System.out.println(UICommon.CATCH_ERROR__NAME+io_ex);
        }
    }

    /*
     * Set value of first layer thickness parameter
     * @param value - value of first layer thickness parameter
     */
    public void setFirstLayerThickness(double value)
    {
        first_layer_thickness = value;
    }

    /*
     * Set value of second layer thickness parameter
     * @param value - value of second layer thickness parameter
     */
    public void setSecondLayerThickness(double value)
    {
        second_layer_thickness = value;
    }

    /*
     * Set value of third layer thickness parameter
     * @param value - value of third layer thickness parameter
     */
    public void setThirdLayerThickness(double value)
    {
        third_layer_thickness = value;
    }

    /** The method sets value of first layer grain size along axis X
     * @param value - value of first layer grain size along axis X
     */
    public void setFirstLayerGrainSize(double value)
    {
        first_layer_grain_size = value;
    }
    
    /** The method sets value of first layer grain size along axis Y
     * @param value - value of first layer grain size along axis Y
     */
    public void setFirstLayerGrainSizeY(double value)
    {
        first_layer_grain_size_Y = value;
    }
    
    /** The method sets value of first layer grain size along axis Z
     * @param value - value of first layer grain size along axis Z
     */
    public void setFirstLayerGrainSizeZ(double value)
    {
        first_layer_grain_size_Z = value;
    }

    /** The method sets value of upper second layer grain size along axis X
     * @param value - value of upper second layer grain size along axis X
     */
    public void setUpperSecondLayerGrainSize(double value)
    {
        upper_second_layer_grain_size = value;
    }
    
    /** The method sets value of upper second layer grain size along axis Y
     * @param value - value of upper second layer grain size along axis Y
     */
    public void setUpperSecondLayerGrainSizeY(double value)
    {
        upper_second_layer_grain_size_Y= value;
    }
    
    /** The method sets value of upper second layer grain size along axis Z
     * @param value - value of upper second layer grain size along axis Z
     */
    public void setUpperSecondLayerGrainSizeZ(double value)
    {
        upper_second_layer_grain_size_Z = value;
    }

    /** The method sets value of lower second layer grain size along axis X
     * @param value - value of lower second layer grain size along axis X
     */
    public void setLowerSecondLayerGrainSize(double value)
    {
        lower_second_layer_grain_size = value;
    }
    
    /** The method sets value of lower second layer grain size along axis Y
     * @param value - value of lower second layer grain size along axis Y
     */
    public void setLowerSecondLayerGrainSizeY(double value)
    {
        lower_second_layer_grain_size_Y = value;
    }
    
    /** The method sets value of lower second layer grain size along axis Z
     * @param value - value of lower second layer grain size along axis Z
     */
    public void setLowerSecondLayerGrainSizeZ(double value)
    {
        lower_second_layer_grain_size_Z = value;
    }

    /** The method sets value of third layer grain size along axis X
     * @param value - value of third layer grain size along axis X
     */
    public void setThirdLayerGrainSize(double value)
    {
        third_layer_grain_size = value;
    }
    
    /** The method sets value of third layer grain size along axis Y
     * @param value - value of third layer grain size along axis Y
     */
    public void setThirdLayerGrainSizeY(double value)
    {
        third_layer_grain_size_Y = value;
    }
    
    /** The method sets value of third layer grain size along axis Z
     * @param value - value of third layer grain size along axis Z
     */
    public void setThirdLayerGrainSizeZ(double value)
    {
        third_layer_grain_size_Z = value;
    }

    /** The method sets value of substrate grain size along axis X
     * @param value - value of substrate grain size along axis X
     */
    public void setSubstrateGrainSize(double value)
    {
        substrate_grain_size = value;
    }
    
    /** The method sets value of substrate grain size along axis Y
     * @param value - value of substrate grain size along axis Y
     */
    public void setSubstrateGrainSizeY(double value)
    {
        substrate_grain_size_Y = value;
    }
    
    /** The method sets value of substrate grain size along axis Z
     * @param value - value of substrate grain size along axis Z
     */
    public void setSubstrateGrainSizeZ(double value)
    {
        substrate_grain_size_Z = value;
    }

    /** The method sets intermediate layer element size
     * @param value intermediate layer element size
     */
    public void setLayerElemSize(double value)
    {
        layer_elem_size = value;
    }

    /** The method sets geometrical type of intermediate layer.
     * @param _layer_type geometrical type of intermediate layer
     */
    public void setLayerType(byte _layer_type)
    {
        layer_type = _layer_type;
    }
    
    /** The method sets type of direction of vector perpendicular to layer interface.
     * @param _layer_direction type of direction of vector perpendicular to layer interface
     */
    public void setLayerDirection(byte _layer_direction)
    {
        layer_direction = _layer_direction;
    }

    /** The method returns thickness of first layer.
     * @return - thickness of first layer
     */
    public double getFirstLayerThickness()
    {
        return first_layer_thickness;
    }

    /*
     * Give value of second layer thickness parameter
     * @return - value of second layer thickness parameter
     */

    public double getSecondLayerThickness()
    {
        return second_layer_thickness;
    }

    /*
     * Give value of third layer thickness parameter
     * @return - value of third layer thickness parameter
     */
    public double getThirdLayerThickness()
    {
        return third_layer_thickness;
    }

    /** The method returns value of first layer grain size along axis X
     * @return - value of first layer grain size along axis X
     */
    public double getFirstLayerGrainSize()
    {
        return first_layer_grain_size;
    }
            
    /** The method returns value of first layer grain size along axis Y
     * @return - value of first layer grain size
     */
    public double getFirstLayerGrainSizeY()
    {
        return first_layer_grain_size_Y;
    }
    
    /** The method returns value of first layer grain size along axis Z
     * @return - value of first layer grain size along axis Z
     */
    public double getFirstLayerGrainSizeZ()
    {
        return first_layer_grain_size_Z;
    }

    /** The method returns value of upper second layer grain size along axis X
     * @return - value of upper second layer grain size along axis X
     */
    public double getUpperSecondLayerGrainSize()
    {
        return upper_second_layer_grain_size;
    }
    
    /** The method returns value of upper second layer grain size along axis Y
     * @return - value of upper second layer grain size along axis Y
     */
    public double getUpperSecondLayerGrainSizeY()
    {
        return upper_second_layer_grain_size_Y;
    }
    
    /** The method returns value of upper second layer grain size along axis Z
     * @return - value of upper second layer grain size along axis Z
     */
    public double getUpperSecondLayerGrainSizeZ()
    {
        return upper_second_layer_grain_size_Z;
    }

    /** The method returns value of lower second layer grain size along axis X
     * @return - value of lower second layer grain size along axis X
     */
    public double getLowerSecondLayerGrainSize()
    {
        return lower_second_layer_grain_size;
    }
    
    /** The method returns value of lower second layer grain size along axis Y
     * @return - value of lower second layer grain size along axis Y
     */
    public double getLowerSecondLayerGrainSizeY()
    {
        return lower_second_layer_grain_size_Y;
    }
    
    /** The method returns value of lower second layer grain size along axis Z
     * @return - value of lower second layer grain size along axis Z
     */
    public double getLowerSecondLayerGrainSizeZ()
    {
        return lower_second_layer_grain_size_Z;
    }

    /** The method returns value of third layer grain size along axis X
     * @return - value of third layer grain size along axis X
     */
    public double getThirdLayerGrainSize()
    {
        return third_layer_grain_size;
    }
    
    /** The method returns value of third layer grain size along axis Y
     * @return - value of third layer grain size along axis Y
     */
    public double getThirdLayerGrainSizeY()
    {
        return third_layer_grain_size_Y;
    }
    
    /** The method returns value of third layer grain size along axis Z
     * @return - value of third layer grain size along axis Z
     */
    public double getThirdLayerGrainSizeZ()
    {
        return third_layer_grain_size_Z;
    }
    
    /** The method returns value of substrate grain size along axis X
     * @return - value of substrate grain size along axis X
     */
    public double getSubstrateGrainSize()
    {
        return substrate_grain_size;
    }
    
    /** The method returns value of substrate grain size along axis Y
     * @return - value of substrate grain size along axis Y
     */
    public double getSubstrateGrainSizeY()
    {
        return substrate_grain_size_Y;
    }
    
    /** The method returns value of substrate grain size along axis Z
     * @return - value of substrate grain size along axis Z
     */
    public double getSubstrateGrainSizeZ()
    {
        return substrate_grain_size_Z;
    }
    
    /** The method returns value of thickness of grain boundary region (in cell diameters)
     * for surface layer.
     * @return - value of thickness of grain boundary region (in cell diameters) for surface layer
     */
    public double getFirstLayerGrainBoundThickness()
    {
        return first_layer_grain_bound_region_thickness;
    }
    
    /** The method sets value of thickness of grain boundary region (in cell diameters)
     * for surface layer.
     * @param value - value of thickness of grain boundary region (in cell diameters) for surface layer
     */
    public void setFirstLayerGrainBoundThickness(double value)
    {
        first_layer_grain_bound_region_thickness = value;
    }
    
    /** The method returns value of thickness of grain boundary region (in cell diameters)
     * for upper intermediate layer.
     * @return - value of thickness of grain boundary region (in cell diameters) for upper intermediate layer
     */
    public double getUpperSecondLayerGrainBoundThickness()
    {
        return upper_second_layer_grain_bound_region_thickness;
    }    
    
    /** The method sets value of thickness of grain boundary region (in cell diameters)
     * for upper intermediate layer.
     * @param value - value of thickness of grain boundary region (in cell diameters) for upper intermediate layer
     */
    public void setUpperSecondLayerGrainBoundThickness(double value)
    {
        upper_second_layer_grain_bound_region_thickness = value;
    }
    
    /** The method returns value of thickness of grain boundary region (in cell diameters)
     * for lower intermediate layer.
     * @return - value of thickness of grain boundary region (in cell diameters) for lower intermediate layer
     */
    public double getLowerSecondLayerGrainBoundThickness()
    {
        return lower_second_layer_grain_bound_region_thickness;
    }
    
    /** The method sets value of thickness of grain boundary region (in cell diameters)
     * for lower intermediate layer.
     * @param value - value of thickness of grain boundary region (in cell diameters) for lower intermediate layer
     */
    public void setLowerSecondLayerGrainBoundThickness(double value)
    {
        lower_second_layer_grain_bound_region_thickness = value;
    }
    
    /** The method returns value of thickness of grain boundary region (in cell diameters)
     * for upper substrate.
     * @return - value of thickness of grain boundary region (in cell diameters) for upper substrate
     */
    public double getThirdLayerGrainBoundThickness()
    {
        return third_layer_grain_bound_region_thickness;
    }
    
    /** The method sets value of thickness of grain boundary region (in cell diameters)
     * for upper substrate.
     * @param value value of thickness of grain boundary region (in cell diameters) for upper substrate
     */
    public void setThirdLayerGrainBoundThickness(double value)
    {
        third_layer_grain_bound_region_thickness = value;
    }
    
    /** The method returns value of thickness of grain boundary region (in cell diameters)
     * for lower substrate.
     * @return - value of thickness of grain boundary region (in cell diameters) for lower substrate
     */
    public double getSubstrateGrainBoundThickness()
    {
        return substrate_grain_bound_region_thickness;
    }
    
    /** The method sets value of thickness of grain boundary region (in cell diameters)
     * for lower substrate.
     * @param value value of thickness of grain boundary region (in cell diameters) for lower substrate
     */
    public void setSubstrateGrainBoundThickness(double value)
    {
        substrate_grain_bound_region_thickness = value;
    }
    
    /** Give value of layer element size parameter
     * @return - value of layer element size parameter
     */
    public double getLayerElemSize()
    {
        return layer_elem_size;
    }
    
    /** The method returns geometrical type of intermediate layer.
     * @return geometrical type of intermediate layer
     */
    public byte getLayerType()
    {
        return layer_type;
    }
    
    /** The method returns type of direction of vector perpendicular to layer interface.
     * @return type of direction of vector perpendicular to layer interface
     */
    public byte getLayerDirection()
    {
        return layer_direction;
    }
    
    /** The method returns the value responsible for type of grain embryo distribution in surface layer.
     * @return the value responsible for type of grain embryo distribution in surface layer
     */
    public byte getSurfaceLayerEmbryoDistrType()
    {
        return surface_layer_embryo_distr_type;
    }
       
    /** The method returns the value responsible for type of grain embryo distribution in upper intermediate layer.
     * @return the value responsible for type of grain embryo distribution in upper intermediate layer
     */
    public byte getUpperInterLayerEmbryoDistrType()
    {
        return upper_inter_layer_embryo_distr_type;
    }
        
    /** The method returns the value responsible for type of grain embryo distribution in lower intermediate layer.
     * @return the value responsible for type of grain embryo distribution in lower intermediate layer
     */
    public byte getLowerInterLayerEmbryoDistrType()
    {
        return lower_inter_layer_embryo_distr_type;
    }      
       
    /** The method returns the value responsible for type of grain embryo distribution in upper substrate layer.
     * @return the value responsible for type of grain embryo distribution in upper substrate layer
     */
    public byte getUpperSubstrateEmbryoDistrType()
    {
        return upper_substrate_embryo_distr_type;
    }        
       
    /** The method returns the value responsible for type of grain embryo distribution in lower substrate layer.
     * @return the value responsible for type of grain embryo distribution in lower substrate layer
     */
    public byte getLowerSubstrateEmbryoDistrType()
    {
        return lower_substrate_embryo_distr_type;
    }
    
    /** The method sets the value responsible for type of grain embryo distribution in surface layer.
     * @param _surface_layer_embryo_distr_type the value responsible for type of grain embryo distribution in surface layer
     */
    public void setSurfaceLayerEmbryoDistrType(byte _surface_layer_embryo_distr_type)
    {
        surface_layer_embryo_distr_type = _surface_layer_embryo_distr_type;
    }
       
    /** The method sets the value responsible for type of grain embryo distribution in upper intermediate layer.
     * @param _upper_inter_layer_embryo_distr_type the value responsible for type of grain embryo distribution in upper intermediate layer
     */
    public void setUpperInterLayerEmbryoDistrType(byte _upper_inter_layer_embryo_distr_type)
    {
        upper_inter_layer_embryo_distr_type = _upper_inter_layer_embryo_distr_type;
    }
        
    /** The method sets the value responsible for type of grain embryo distribution in lower intermediate layer.
     * @param _lower_inter_layer_embryo_distr_type the value responsible for type of grain embryo distribution in lower intermediate layer
     */
    public void setLowerInterLayerEmbryoDistrType(byte _lower_inter_layer_embryo_distr_type)
    {
        lower_inter_layer_embryo_distr_type = _lower_inter_layer_embryo_distr_type;
    }      
       
    /** The method sets the value responsible for type of grain embryo distribution in upper substrate layer.
     * @param _upper_substrate_embryo_distr_type the value responsible for type of grain embryo distribution in upper substrate layer
     */
    public void setUpperSubstrateEmbryoDistrType(byte _upper_substrate_embryo_distr_type)
    {
        upper_substrate_embryo_distr_type = _upper_substrate_embryo_distr_type;
    }        
       
    /** The method sets the value responsible for type of grain embryo distribution in lower substrate layer.
     * @param _lower_substrate_embryo_distr_type the value responsible for type of grain embryo distribution in lower substrate layer
     */
    public void setLowerSubstrateEmbryoDistrType(byte _lower_substrate_embryo_distr_type)
    {
        lower_substrate_embryo_distr_type = _lower_substrate_embryo_distr_type;
    }   
}