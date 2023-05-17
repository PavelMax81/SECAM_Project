/*
 * @(#) SaveParams.java version 1.0.0;       July 1 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for write params of interface in corresponding file.
 *
 *=============================================================
 *  Last changes :
 *          4 April 2011 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.3.7
 *          Edit: Add path of tank file to default task file
 */

/** Class for write params of interface in corresponding file.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - July 2009
 */

package interf;

import JSci.maths.vectors.DoubleVector;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import javafx.scene.control.TextArea;
import util.*;

public class SaveParams
{
    /**
     * Specimen data bank
     */

    UISpecimen ui_specimen;

    /**
     * Specimen data bank
     */

    UI_SpecialSpecimen ui_special_specimen;

    /**
     * Material data bank
     */

    UIMaterial ui_material;

    /**
     * Initial condition data bank
     */

    UIInitialCondition ui_init_cond;

    /**
     * Boundary condition data bank
     */

    UIBoundaryCondition ui_bound_cond;

    UITank ui_tank;

    /**
     * Task data bank
     */

    UITask ui_task;

    /**
     * File for write
     */

    File file;

    /**
     * Choosed params data bank
     */

    UIInterface ui_interface;

    /**
     * This field is intended for create file for write choosed task name
     */

    File choosed_task_file;

    TextArea text_area;
    
    /**
     * This field is intended for write in corresponding files
     * needed transferred data bank elements
     */

    TransferredDataBank transfer_data_bank;

    /** Write specimen params in coresponding file
     * @param temp_specimen - data bank for writing
     * @param file_name - location of file for writing
     */
    public SaveParams(UISpecimen temp_specimen, String file_name)
    {
        System.out.println("SaveParams: constructor SaveParams(UISpecimen temp_specimen,"+ file_name+") is started");

        // Remember specimen data bank

        ui_specimen = temp_specimen;

        /*
         * Create folder with all data of new specimen
         */

        File temp_file = new File(Common.SPEC_PATH_FOR_WRITING+file_name);
        if(!temp_file.isDirectory())
            new File(Common.SPEC_PATH_FOR_WRITING+file_name).mkdir();

        // If file with values of parameters of specimen exists
        // then it removed for rewriting

        file = new File(Common.SPEC_PATH_FOR_WRITING+file_name+"/"+file_name+"."+Common.SPECIMEN_EXTENSION);
        if(file.exists())
            file.delete();
        
        /*
         * Write params
         */

        try
        {
            file = new File(Common.SPEC_PATH_FOR_WRITING+file_name+"/"+file_name+"."+Common.SPECIMEN_EXTENSION);
            
            System.out.println("NAME OF FILE WITH SPECIMEN PARAMETERS: "+Common.SPEC_PATH_FOR_WRITING+file_name+"/"+file_name+"."+Common.SPECIMEN_EXTENSION);

            // Create file

            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            // Writing of parameters

            buf_writer.write(UICommon.CELL_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getCellSize()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ELEMENT_NUMBER_X_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberX()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ELEMENT_NUMBER_Y_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberY()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ELEMENT_NUMBER_Z_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberZ()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.SURF_THICKNESS_PROPERTIES+" = "+new Double(ui_specimen.getSurfThickness()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.MIN_NEIGHBOURS_NUMBER_PROPERTIES+" = "+new Integer(ui_specimen.getMinNeighboursNumber()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.SPECIMEN_SIZE_X_PROPERTIES+" = "+new Double(ui_specimen.getSpecimenSizeX()*2/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.SPECIMEN_SIZE_Y_PROPERTIES+" = "+new Double(ui_specimen.getSpecimenSizeY()*2/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.SPECIMEN_SIZE_Z_PROPERTIES+" = "+new Double(ui_specimen.getSpecimenSizeZ()*2/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.COORDINATE_X_PROPERTIES+" = "+new Double(ui_specimen.getCoordinateX()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.COORDINATE_Y_PROPERTIES+" = "+new Double(ui_specimen.getCoordinateY()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.COORDINATE_Z_PROPERTIES+" = "+new Double(ui_specimen.getCoordinateZ()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.ANISOTROPY_COEFF_PROPERTIES+" = "+new Double(ui_specimen.getAnisotropyCoeff()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.FIRST_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getAverageGrainLength_2()/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getAverageGrainLength_3()/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getAverageGrainLength_4()/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.THIRD_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getAverageGrainLength_5()/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.SUBSTRATE_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getAverageGrainLength()/ui_specimen.getCellSize()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.NUMBER_OF_PHASES_PROPERTIES+" = "+new Integer(ui_specimen.getNumberOfPhases()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.PARTICLE_VOLUME_FRACTION_PROPERTIES+" = "+new Double(ui_specimen.getParticleVolumeFraction()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.PARTICLE_RADIUS_PROPERTIES+" = "+new Double(ui_specimen.getParticleRadius()).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.PACKING_TYPE_PROPERTIES+" = "+new Integer(ui_specimen.getPackingType()).toString());
            buf_writer.newLine();

            // Write info about write grains info duiring
            // grain structure creation

            // If corresponding field was choosed, then grain structure
            // creation also write info about grains

            if(ui_specimen.getGrainCellularAutomata())
                buf_writer.write(UICommon.SHOW_GRAIN_STRUCTURE_PROPERTIES+UICommon.SHOW_GRAIN_STRUCTURE_VALUE);

            // If corresponding field was not choosed, then grain structure
            // creation not write info about grains
            else
                buf_writer.write(UICommon.SHOW_GRAIN_STRUCTURE_PROPERTIES+UICommon.NOT_SHOW_GRAIN_STRUCTURE_VALUE);
            buf_writer.newLine();
            
            // Write method of calculation
            if(ui_specimen.getStochasticMethod()==true)
            buf_writer.write(UICommon.METHOD_NAME+" = "+UICommon.METHOD_STOCHASTIC_PROPERTIES);
            if(ui_specimen.getRegularMethod()==true)
            buf_writer.write(UICommon.METHOD_NAME+" = "+UICommon.METHOD_REGULAR_PROPERTIES);
            if(ui_specimen.getMixedMethod()==true)
            buf_writer.write(UICommon.METHOD_NAME+" = "+UICommon.METHOD_MIXED_PROPERTIES);            
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANIS_VECTOR_X_NAME+" = "+new Double(ui_specimen.getAnisVectorX()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANIS_VECTOR_Y_NAME+" = "+new Double(ui_specimen.getAnisVectorY()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANIS_VECTOR_Z_NAME+" = "+new Double(ui_specimen.getAnisVectorZ()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.SPEC_ANIS_COEFF_NAME+" = "+new Double(ui_specimen.getSpecimenAnisotropyCoeff()).toString());
            buf_writer.newLine();
            
            buf_writer.close();
        }
        catch(IOException e)
        {
        }
        
        // Write table in second file
        // If file exists then it removed for rewriting
        file = new File(Common.SPEC_PATH_FOR_WRITING+file_name+"/"+file_name+"."+Common.SPECIMEN_MATERIAL_EXTENSION);
        if(file.exists())
            file.delete();

        try
        {
            file = new File(Common.SPEC_PATH_FOR_WRITING+file_name+"/"+file_name+"."+Common.SPECIMEN_MATERIAL_EXTENSION);

            // Create file
            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer2 = new BufferedWriter(new FileWriter(file));
            
            for(int i = 0 ; i< ui_specimen.getNumberOfPhases(); i++){
                buf_writer2.write(
                        ui_specimen.volume_fraction.get(i) + " " +
                        ui_specimen.materials.get(i) + " " +
                        ui_specimen.angle_range.get(i) + " " + 
                        ui_specimen.disl_density.get(i) + " " + 
                        ui_specimen.max_deviation.get(i)
                );
                buf_writer2.newLine();
            }
            
            buf_writer2.close();
        }
        catch(IOException e)
        {
        }
    }
    
    /** Write material params in coresponding file
     * @param temp_material - data bank for writing
     * @param file_name - location of file for writing
     */
    public SaveParams(UIMaterial temp_material, String file_name)
    {
        System.out.println("SaveParams: constructor: SaveParams(UIMaterial temp_material, String file_name): creation start");

        // Remember material data bank
        
        ui_material = new UIMaterial(temp_material);
        
        /*
         * If file exist, then he removed for rewrite
         */

        file = new File(Common.MATERIALS_PATH+"/"+file_name+"."+Common.MATERIAL_EXTENSION);
        if(file.exists())
            file.delete();
 
        /*
         * Write params
         */

        try
        {
            file = new File(Common.MATERIALS_PATH+"/"+file_name+"."+Common.MATERIAL_EXTENSION);
            
            System.out.println("+_+_+_+_+_+_+_+_\nMaterial file: "+Common.MATERIALS_PATH+"/"+file_name+"."+Common.MATERIAL_EXTENSION);

            // Create file

            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));
            
            // Write params
            buf_writer.write(UICommon.MOD_ELAST_PROPERTIES+"               = "+new Double(ui_material.getModElast()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.MOD_SHEAR_PROPERTIES+"               = "+new Double(ui_material.getModShear()).toString());
            buf_writer.newLine();            
            buf_writer.write(UICommon.DENSITY_PROPERTIES+"                 = "+new Double(ui_material.getDensity()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.HEAT_EXPANSION_COEFF_PROPERTIES+"      = "+new Double(ui_material.getHeatExpansionCoeff()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.SPECIFIC_HEAT_CAPACITY_PROPERTIES+"  = "+new Double(ui_material.getSpecificHeatCapacity()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.THERMAL_CONDUCTIVITY_PROPERTIES+"    = "+new Double(ui_material.getThermalConductivity()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.MOLAR_MASS_PROPERTIES+"              = "+new Double(ui_material.getMolarMass()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANGLE_LIMIT_HAGB_PROPERTIES+"          = "+new Double(ui_material.getAngleLimitHAGB()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ENERGY_HAGB_PROPERTIES+"              = "+new Double(ui_material.getEnergyHAGB()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.MAX_MOBILITY_PROPERTIES+"             = "+new Double(ui_material.getMaxMobility()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.DISL_MAX_MOBILITY_PROPERTIES+"         = "+new Double(ui_material.getDislMaxMobility()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.MECH_MAX_MOBILITY_PROPERTIES+"         = "+new Double(ui_material.getMechMaxMobility()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.YIELD_STRAIN_PROPERTIES+"             = "+new Double(ui_material.getYieldStrain()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ULTIMATE_STRAIN_PROPERTIES+"          = "+new Double(ui_material.getUltimateStrain()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ENERGY_COEFF_PROPERTIES+"            = "+new Double(ui_material.getEnergyCoeff()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.THERMAL_CONDUCT_BOUND_PROPERTIES+"   = "+new Double(ui_material.getThermalConductBound()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.PHONON_PORTION_PROPERTIES+"          = "+new Double(ui_material.getPhononPortion()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ACT_ENERGY_PROPERTIES+"               = "+new Double(ui_material.getActEnergy()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.DISL_DISTR_COEFF_PROPERTIES+"        = "+new Double(ui_material.getDislDistrCoeff()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.YIELD_STATE_COEFF_PROPERTIES+"       = "+new Double(ui_material.getYieldStateCoeff()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.ULTIMATE_STATE_COEFF_PROPERTIES+"    = "+new Double(ui_material.getUltimateStateCoeff()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.PART_VOL_FRACTION_PROPERTIES+"       = "+new Double(ui_material.getPartVolFraction()).toString());
            buf_writer.newLine();
            buf_writer.write(UICommon.PART_RADIUS_PROPERTIES+"             = "+new Double(ui_material.getPartRadius()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.THRESHOLD_STRESS_PROPERTIES+"        = "+new Double(ui_material.getThresholdStress()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.TORSION_ENERGY_COEFF_PROPERTIES+"    = "+new Double(ui_material.getTorsionEnergyCoeff()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.TORSION_ENERGY_COEFF_GB_1_PROPERTIES+"    = "+new Double(ui_material.getTorsionEnergyCoeffForGrBoundary()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.TORSION_ENERGY_COEFF_GB_2_PROPERTIES+"    = "+new Double(ui_material.getTorsionEnergyCoeffForGrBoundRegion()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LOW_TEMPER_THR_VALUE_PROPERTIES+"       = "+new Double(ui_material.getLowTemperThrValue()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.HIGH_TEMPER_THR_VALUE_PROPERTIES+"      = "+new Double(ui_material.getHighTemperThrValue()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.MIN_TWIN_TEMPERATURE_PROPERTIES+"    = "+new Double(ui_material.getMinTwinTemperature()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.TWINNING_TEMPERATURE_PROPERTIES+"    = "+new Double(ui_material.getTwinningTemperature()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_PARAMETER_PROPERTIES+"       = "+new Double(ui_material.getLatticeParameter()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_VECTOR_A_LENGTH_PROPERTIES+" = "+new Double(ui_material.getLatticeVector_A_Length()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_VECTOR_B_LENGTH_PROPERTIES+" = "+new Double(ui_material.getLatticeVector_B_Length()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_VECTOR_C_LENGTH_PROPERTIES+" = "+new Double(ui_material.getLatticeVector_C_Length()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_ANGLE_VEC_A_VEC_B_PROPERTIES+" = "+new Double(ui_material.getLatticeAngle_vecA_vecB()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_ANGLE_VEC_B_VEC_C_PROPERTIES+" = "+new Double(ui_material.getLatticeAngle_vecB_vecC()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_ANGLE_VEC_C_VEC_A_PROPERTIES+" = "+new Double(ui_material.getLatticeAngle_vecC_vecA()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.LATTICE_ANISOTROPY_COEFF_PROPERTIES+"      = "+new Double(ui_material.getLatticeAnisCoeff()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.MAXIMAL_PROB_RECRYST_PROPERTIES+"        = "+new Double(ui_material.getMaxProbRecryst()).toString());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.MAXIMAL_PROB_TWINNING_PROPERTIES+"       = "+new Double(ui_material.getMaxProbTwinning()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.MINIMAL_DISL_DENSITY_PROPERTIES+"        = "+new Double(ui_material.getMinDislDensity()).toString());
            buf_writer.newLine();
            
            buf_writer.close();
        }
        catch(IOException e)
        {
            System.out.println("SaveParams(UIMaterial temp_material, String file_name): IOException: "+e);
        }
    }
    
    /** Write initial condition params in coresponding file
     * @param temp_init_cond - data bank for writing
     * @param choosed_specimen - choosed specimen
     * @param file_name - location of file for writing
     */
     public SaveParams(UIInitialCondition temp_init_cond, String choosed_specimen, String file_name)
    {
         System.out.println("SaveParams: constructor: SaveParams(UIInitialCondition temp_init_cond, String choosed_specimen, String file_name): creation start");

         // Remember initial condition data bank
         ui_init_cond = new UIInitialCondition(temp_init_cond);

        // Creation of folder for initial conditions for current specimen
        System.out.println("File "+Common.INIT_COND_PATH+"/"+choosed_specimen+Common.INIT_COND_NAME+"/"+file_name+"."+Common.INIT_COND_EXTENSION+" is created.");
        
        File temp_file = new File(Common.INIT_COND_PATH+"/"+choosed_specimen+Common.INIT_COND_NAME);
        
        if(!temp_file.isDirectory())
        {
            new File(Common.INIT_COND_PATH+"/"+choosed_specimen+Common.INIT_COND_NAME).mkdir();
        }

        // If file exists then it is removed for rewriting
        file = new File(Common.INIT_COND_PATH+"/"+choosed_specimen+Common.INIT_COND_NAME+"/"+file_name+"."+Common.INIT_COND_EXTENSION);
        
        if(file.exists())
            file.delete();

        /* Write params
         */
        try
        {
            file = new File(Common.INIT_COND_PATH+"/"+choosed_specimen+Common.INIT_COND_NAME+"/"+file_name+"."+Common.INIT_COND_EXTENSION);

            // Create file

            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            // Write params

            buf_writer.write(ui_init_cond.getFirstThermalEnergy()+" "+ui_init_cond.getFirstMechanicalEnergy()+" "+ui_init_cond.getFirstTemperature());
            buf_writer.newLine();
            buf_writer.write(ui_init_cond.getSecondThermalEnergy()+" "+ui_init_cond.getSecondMechanicalEnergy()+" "+ui_init_cond.getSecondTemperature());
            buf_writer.newLine();
            buf_writer.write(ui_init_cond.getThirdThermalEnergy()+" "+ui_init_cond.getThirdMechanicalEnergy()+" "+ui_init_cond.getThirdTemperature());
            buf_writer.newLine();
            buf_writer.write(ui_init_cond.getFourthThermalEnergy()+" "+ui_init_cond.getFourthMechanicalEnergy()+" "+ui_init_cond.getFourthTemperature());
            buf_writer.newLine();
            buf_writer.write(ui_init_cond.getFifthThermalEnergy()+" "+ui_init_cond.getFifthMechanicalEnergy()+" "+ui_init_cond.getFifthTemperature());
            buf_writer.newLine();

            buf_writer.close();
            
            System.out.println();
            System.out.println("Temperature of 1st layer: "+ui_init_cond.getFirstTemperature());
            System.out.println("Temperature of 2nd layer: "+ui_init_cond.getSecondTemperature());
            System.out.println("Temperature of 3rd layer: "+ui_init_cond.getThirdTemperature());
            System.out.println("Temperature of 4th layer: "+ui_init_cond.getFourthTemperature());
            System.out.println("Temperature of 5th layer: "+ui_init_cond.getFifthTemperature());
            System.out.println();
        }
        catch(IOException e)
        {
        }
    }
     
     /** Write boundary condition params in coresponding file
      * @param temp_bound_cond - data bank for writing
      * @param choosed_specimen - choosed specimen
      * @param file_name - location of file for writing
      */
     public SaveParams(TransferredDataBank trans_data_bank)
     {
         System.out.println("SaveParams: constructor: SaveParams(TransferredDataBank trans_data_bank): creation start");

         // Remember boundary condition data bank

        ui_bound_cond = new UIBoundaryCondition(trans_data_bank.getUIBoundaryCondition());

        // Remember interface data bank

        UIInterface ui = trans_data_bank.getUIInterface();

        // Remember specimen data bank

        UISpecimen spec = trans_data_bank.getUISpecimen();

        // Create folder for bound cond of specimen
        File temp_file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME);
        
        if(!temp_file.isDirectory())
            temp_file.mkdir();
        
        // If file exists, then it must be removed.
        file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"."+Common.BOUND_COND_EXTENSION);

        if(file.exists())
            file.delete();
            
        // Write params
        try
        {
            file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"."+Common.BOUND_COND_EXTENSION);
            
            System.out.println("----------------====================----------------");
            System.out.println("File with boundary conditions: "+file);
            System.out.println("----------------====================----------------");

            // Create file
            if(!file.exists())                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));
            
            buf_writer.write("#   Each string corresponds to certain facet: \n" +
                             "# 1st - left, 2nd - front, 3rd - top, 4th - bottom, 5th - back, 6th - right.\n" +
                             "#   Each column contains the following values for corresponding facet:\n" +
                             "# 0. minimal value of chosen mechanical energy source;\n" +
                             "# 1. maximal value of chosen mechanical energy source;\n" +
                             "# 2. minimal value of chosen thermal energy source;\n" +
                             "# 3. maximal value of chosen thermal energy source;\n" +
                             "# 4. time function type (0 - uniform loading, 1 - cycle loading, 2 - loading according to periodic function);\n" +
                             "# 5. loading time portion;\n" +
                             "# 6. relaxation time portion.\n");
            
            // Write params

            // If corresponding checkbox (such as "mechanical loading"
            // or "thermal influx") is selected, then param value
            // write else write "0.0"
            
            // left
            if(ui_bound_cond.getLeftCheckMechanicalLoading())
            {
              buf_writer.write(new Double(ui_bound_cond.getLeftMinMechanicalLoading()).toString()+" ");
              buf_writer.write(new Double(ui_bound_cond.getLeftMechanicalLoading()).toString()+" ");
            }
            else  
              buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getLeftCheckThermalInflux())
            {
              buf_writer.write(new Double(ui_bound_cond.getLeftMinThermalInflux()).toString()+" ");
              buf_writer.write(new Double(ui_bound_cond.getLeftThermalInflux()).toString()+" ");
            }
            else  
              buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getLeftCheckMechanicalLoading() | ui_bound_cond.getLeftCheckThermalInflux())
            {
                buf_writer.write(new Byte(ui_bound_cond.getLeftBoundTimeFunctionType()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getLeftBoundLoadTimePortion()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getLeftBoundRelaxTimePortion()).toString()+"\n");
            }
            else
                buf_writer.write("0 0.0 0.0 \n");
            
            // front
            if(ui_bound_cond.getFrontCheckMechanicalLoading())
            {
                buf_writer.write(new Double(ui_bound_cond.getFrontMinMechanicalLoading()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getFrontMechanicalLoading()).toString()+" ");
            }
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getFrontCheckThermalInflux())
            {
                buf_writer.write(new Double(ui_bound_cond.getFrontMinThermalInflux()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getFrontThermalInflux()).toString()+" ");
            }                
            else
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getFrontCheckMechanicalLoading() | ui_bound_cond.getFrontCheckThermalInflux())
            {            
                buf_writer.write(new Byte(ui_bound_cond.getFrontBoundTimeFunctionType()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getFrontBoundLoadTimePortion()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getFrontBoundRelaxTimePortion()).toString());
                buf_writer.newLine();
            }
            else
                buf_writer.write("0 0.0 0.0 \n");
            
            // top
            if(ui_bound_cond.getTopCheckMechanicalLoading())
            {
                buf_writer.write(new Double(ui_bound_cond.getTopMinMechanicalLoading()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getTopMechanicalLoading()).toString()+" ");
            }
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getTopCheckThermalInflux())
            {
                buf_writer.write(new Double(ui_bound_cond.getTopMinThermalInflux()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getTopThermalInflux()).toString()+" ");
            }
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getTopCheckMechanicalLoading() | ui_bound_cond.getTopCheckThermalInflux())
            {
                buf_writer.write(new Byte(ui_bound_cond.getTopBoundTimeFunctionType()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getTopBoundLoadTimePortion()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getTopBoundRelaxTimePortion()).toString());
                buf_writer.newLine();
            }
            else  
                buf_writer.write("0 0.0 0.0 \n");
            
            // bottom
            if(ui_bound_cond.getBottomCheckMechanicalLoading())
            {
                buf_writer.write(new Double(ui_bound_cond.getBottomMinMechanicalLoading()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBottomMechanicalLoading()).toString()+" ");
            }
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getBottomCheckThermalInflux())
            {
                buf_writer.write(new Double(ui_bound_cond.getBottomMinThermalInflux()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBottomThermalInflux()).toString()+" ");
            }
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getBottomCheckMechanicalLoading() | ui_bound_cond.getBottomCheckThermalInflux())
            {
                buf_writer.write(new Byte(ui_bound_cond.getBottomBoundTimeFunctionType()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBottomBoundLoadTimePortion()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBottomBoundRelaxTimePortion()).toString());
                buf_writer.newLine();
            }
            else
                buf_writer.write("0 0.0 0.0 \n");
            
            // back
            if(ui_bound_cond.getBackCheckMechanicalLoading())
            {
                buf_writer.write(new Double(ui_bound_cond.getBackMinMechanicalLoading()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBackMechanicalLoading()).toString()+" ");
            }
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getBackCheckThermalInflux())
            {
                buf_writer.write(new Double(ui_bound_cond.getBackMinThermalInflux()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBackThermalInflux()).toString()+" ");
            }
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getBackCheckMechanicalLoading() |ui_bound_cond.getBackCheckThermalInflux())
            {
                buf_writer.write(new Byte(ui_bound_cond.getBackBoundTimeFunctionType()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBackBoundLoadTimePortion()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getBackBoundRelaxTimePortion()).toString());
                buf_writer.newLine();
            }
            else
                buf_writer.write("0 0.0 0.0 \n");
            
            // right
            if(ui_bound_cond.getRightCheckMechanicalLoading())
            {
                buf_writer.write(new Double(ui_bound_cond.getRightMinMechanicalLoading()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getRightMechanicalLoading()).toString()+" ");
            }   
            else  
                buf_writer.write("0.0 0.0 ");
            
            if(ui_bound_cond.getRightCheckThermalInflux())
            {
                buf_writer.write(new Double(ui_bound_cond.getRightMinThermalInflux()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getRightThermalInflux()).toString()+" ");
            }
            else 
                buf_writer.write("0.0 0.0 ");
            
            
            if(ui_bound_cond.getRightCheckMechanicalLoading() | ui_bound_cond.getRightCheckThermalInflux())
            {
                buf_writer.write(new Byte(ui_bound_cond.getRightBoundTimeFunctionType()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getRightBoundLoadTimePortion()).toString()+" ");
                buf_writer.write(new Double(ui_bound_cond.getRightBoundRelaxTimePortion()).toString());
                buf_writer.newLine();            
            }
            else
                buf_writer.write("0 0.0 0.0 \n");
            
        /*
            if(ui_bound_cond.getParallelepipedTank())
                buf_writer.write("tanks_type = "+UICommon.parallelepiped_tank);
            
            if(ui_bound_cond.getVerticalEllypticTank())
                buf_writer.write("tanks_type = "+UICommon.vertical_ellyptic_tank);
            
            if(ui_bound_cond.getHorizonTriangleTank())
                buf_writer.write("tanks_type = "+UICommon.horizon_triangle_tank);
            
            if(ui_bound_cond.getHorizonCircleTank())
                buf_writer.write("tanks_type = "+UICommon.horizon_circle_tank);
        */
            
            buf_writer.close();
        }
        catch(IOException e)
        {
            System.out.println("IOException in SaveParams(TransferredDataBank trans_data_bank): "+e);
        }

        /** Write second file (with info about adiabatic boundary or not,
         * with info about boundary materials, and info about
         * chosen task types: stress or strain and temperature or energy)
         */

        /** If file exists then it is removed.
         */
        file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"_grains."+Common.BOUND_COND_EXTENSION);
        
        if(file.exists())            file.delete();

        try
        {
            file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"_grains."+Common.BOUND_COND_EXTENSION);

            // Create file

            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            // If some task type is choosed in mechanical loading
            // then in file after name of boundary material
            // write stress or strain
            System.out.println("SAVE PARAMS LEFT MATERIAL = "+ui_bound_cond.getLeftAdiabaticMaterial());
            
            buf_writer.write("# Each string corresponds to certain facet: \n" +
                             "# 1st - left, 2nd - front, 3rd - top, 4th - bottom, 5th - back, 6th - right.\n" +
                             "# Each column contains the following values for corresponding facet:\n"+
                             "# 1. name of boundary material;\n"+
                             "# 2. name of chosen mechanical energy source;\n"+
                             "# 3. name of chosen thermal energy source.\n");
            
            if(ui_bound_cond.getLeftCheckMechanicalLoading()|ui_bound_cond.getLeftCheckThermalInflux())
            {
                // Write boundary material
                buf_writer.write(ui_bound_cond.getLeftAdiabaticMaterial());

                // Write stress or strain
                if(ui_bound_cond.getLeftCheckMechanicalLoading())
                {
                    if(ui_bound_cond.getLeftStress())
                        buf_writer.write(" "+UICommon.STRESS_R_B_NAME);
                    else
                        if(ui_bound_cond.getLeftStrain())
                            buf_writer.write(" "+UICommon.STRAIN_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)
                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);

                // If some task type is choosed in thermal influx
                // then in file after name of boundary material
                // write temperature or energy

                if(ui_bound_cond.getLeftCheckThermalInflux())
                {
                    if(ui_bound_cond.getLeftTemperature())
                        buf_writer.write(" "+UICommon.TEMPERATURE_R_B_NAME);
                    else
                        if(ui_bound_cond.getLeftEnergy())
                            buf_writer.write(" "+UICommon.ENERGY_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)

                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }

            // if no task type was choosed in machanical loading
            // and in thermal influx then
            // write info about it (create name N/A - not avalaible)
            // boundary material became default

            else
            {
                buf_writer.write(spec.materials.get(1) +" "+UICommon.N_A_R_B_NAME+" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }
            
            // If some task type is choosed in mechanical loading
            // then in file after name of boundary material
            // write stress or strain
            if(ui_bound_cond.getFrontCheckMechanicalLoading()|ui_bound_cond.getFrontCheckThermalInflux())
            {
                // Write boundary material
                buf_writer.write(ui_bound_cond.getFrontAdiabaticMaterial());

                // Write stress or strain
                if(ui_bound_cond.getFrontCheckMechanicalLoading())
                {
                    if(ui_bound_cond.getFrontStress())
                        buf_writer.write(" "+UICommon.STRESS_R_B_NAME);
                    else
                        if(ui_bound_cond.getFrontStrain())
                            buf_writer.write(" "+UICommon.STRAIN_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)

                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);

                // If some task type is choosed in thermal influx
                // then in file after name of boundary material
                // write temperature or energy

                if(ui_bound_cond.getFrontCheckThermalInflux())
                {
                    if(ui_bound_cond.getFrontTemperature())
                        buf_writer.write(" "+UICommon.TEMPERATURE_R_B_NAME);
                    else
                        if(ui_bound_cond.getFrontEnergy())
                            buf_writer.write(" "+UICommon.ENERGY_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)

                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }

            // if no task type was choosed in machanical loading
            // and in thermal influx then
            // write info about it (create name N/A - not avalaible)
            // boundary material became default

            else
            {
                buf_writer.write(spec.materials.get(1) +" "+UICommon.N_A_R_B_NAME+" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }

            // If some task type is choosed in mechanical loading
            // then in file after name of boundary material
            // write stress or strain
            if(ui_bound_cond.getTopCheckMechanicalLoading()|ui_bound_cond.getTopCheckThermalInflux())
            {
                // Write boundary material
                buf_writer.write(ui_bound_cond.getTopAdiabaticMaterial());

                // Write stress or strain
                if(ui_bound_cond.getTopCheckMechanicalLoading())
                {
                    if(ui_bound_cond.getTopStress())
                        buf_writer.write(" "+UICommon.STRESS_R_B_NAME);
                    else
                        if(ui_bound_cond.getTopStrain())
                            buf_writer.write(" "+UICommon.STRAIN_R_B_NAME);
                }

                // if no task type was chosen then
                // write info about it (create name N/A - not avalaible)
                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);

                // If some task type is choosed in thermal influx
                // then in file after name of boundary material
                // write temperature or energy
                if(ui_bound_cond.getTopCheckThermalInflux())
                {
                    if(ui_bound_cond.getTopTemperature())
                        buf_writer.write(" "+UICommon.TEMPERATURE_R_B_NAME);
                    else
                        if(ui_bound_cond.getTopEnergy())
                            buf_writer.write(" "+UICommon.ENERGY_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)
                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }

            // if no task type was choosed in machanical loading
            // and in thermal influx then
            // write info about it (create name N/A - not avalaible)
            // boundary material became default

            else
            {
                buf_writer.write(spec.materials.get(1) +" "+UICommon.N_A_R_B_NAME+" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }
            
            // If some task type is choosed in mechanical loading
            // then in file after name of boundary material
            // write stress or strain
            if(ui_bound_cond.getBottomCheckMechanicalLoading()|ui_bound_cond.getBottomCheckThermalInflux())
            {
                // Write boundary material

                buf_writer.write(ui_bound_cond.getBottomAdiabaticMaterial());

                // Write stress or strain

                if(ui_bound_cond.getBottomCheckMechanicalLoading())
                {
                    if(ui_bound_cond.getBottomStress())
                        buf_writer.write(" "+UICommon.STRESS_R_B_NAME);
                    else
                        if(ui_bound_cond.getBottomStrain())
                            buf_writer.write(" "+UICommon.STRAIN_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)

                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);

                // If some task type is choosed in thermal influx
                // then in file after name of boundary material
                // write temperature or energy

                if(ui_bound_cond.getBottomCheckThermalInflux())
                {
                    if(ui_bound_cond.getBottomTemperature())
                        buf_writer.write(" "+UICommon.TEMPERATURE_R_B_NAME);
                    else
                        if(ui_bound_cond.getBottomEnergy())
                            buf_writer.write(" "+UICommon.ENERGY_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)

                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }
            // if no task type was choosed in machanical loading
            // and in thermal influx then
            // write info about it (create name N/A - not avalaible)
            // boundary material became default
            else
            {
                buf_writer.write(spec.materials.get(1) +" "+UICommon.N_A_R_B_NAME+" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }
            
            // If some task type is choosed in mechanical loading
            // then in file after name of boundary material
            // write stress or strain
            if(ui_bound_cond.getBackCheckMechanicalLoading()|ui_bound_cond.getBackCheckThermalInflux())
            {
                // Write boundary material
                buf_writer.write(ui_bound_cond.getBackAdiabaticMaterial());

                // Write stress or strain
                if(ui_bound_cond.getBackCheckMechanicalLoading())
                {
                    if(ui_bound_cond.getBackStress())
                        buf_writer.write(" "+UICommon.STRESS_R_B_NAME);
                    else
                        if(ui_bound_cond.getBackStrain())
                            buf_writer.write(" "+UICommon.STRAIN_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)
                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);

                // If some task type is choosed in thermal influx
                // then in file after name of boundary material
                // write temperature or energy
                if(ui_bound_cond.getBackCheckThermalInflux())
                {
                    if(ui_bound_cond.getBackTemperature())
                        buf_writer.write(" "+UICommon.TEMPERATURE_R_B_NAME);
                    else
                        if(ui_bound_cond.getBackEnergy())
                            buf_writer.write(" "+UICommon.ENERGY_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)
                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }

            // if no task type was choosed in machanical loading
            // and in thermal influx then
            // write info about it (create name N/A - not avalaible)
            // boundary material became default
            else
            {
                buf_writer.write(spec.materials.get(1) +" "+UICommon.N_A_R_B_NAME+" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }
            
            // If some task type is choosed in mechanical loading
            // then in file after name of boundary material
            // write stress or strain
            if(ui_bound_cond.getRightCheckMechanicalLoading()|ui_bound_cond.getRightCheckThermalInflux())
            {
                // Write boundary material
                buf_writer.write(ui_bound_cond.getRightAdiabaticMaterial());

                // Write stress or strain
                if(ui_bound_cond.getRightCheckMechanicalLoading())
                {
                    if(ui_bound_cond.getRightStress())
                        buf_writer.write(" "+UICommon.STRESS_R_B_NAME);
                    else
                        if(ui_bound_cond.getRightStrain())
                            buf_writer.write(" "+UICommon.STRAIN_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)
                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);

                // If some task type is choosed in thermal influx
                // then in file after name of boundary material
                // write temperature or energy
                if(ui_bound_cond.getRightCheckThermalInflux())
                {
                    if(ui_bound_cond.getRightTemperature())
                        buf_writer.write(" "+UICommon.TEMPERATURE_R_B_NAME);
                    else
                        if(ui_bound_cond.getRightEnergy())
                            buf_writer.write(" "+UICommon.ENERGY_R_B_NAME);
                }

                // if no task type was choosed then
                // write info about it (create name N/A - not avalaible)
                else
                    buf_writer.write(" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }

            // if no task type was choosed in machanical loading
            // and in thermal influx then
            // write info about it (create name N/A - not avalaible)
            // boundary material became default
            else
            {
                buf_writer.write(spec.materials.get(1) +" "+UICommon.N_A_R_B_NAME+" "+UICommon.N_A_R_B_NAME);
                buf_writer.newLine();
            }

            buf_writer.close();
        }
        catch(IOException w)
        {}

        /*
         * Write third file with info about boundary type
         */

        /*
         * If file exists, then he removed for rewrite
         */

        file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"_"+UICommon.BOUNDARY_TYPE_PROPERTIES+"."+Common.BOUND_COND_EXTENSION);
        if(file.exists())
            file.delete();

        try
        {
            file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"_"+UICommon.BOUNDARY_TYPE_PROPERTIES+"."+Common.BOUND_COND_EXTENSION);

            // Create file
            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            // Write boundary type comments
            buf_writer.write(UICommon.BOUNDARY_TYPE_COMMENT);
            
            // Write boundary type info
            buf_writer.write(UICommon.BOUNDARY_TYPE_PROPERTIES+" = "+ui_bound_cond.getBoundaryType());
            buf_writer.newLine();
            
            buf_writer.newLine();     
            buf_writer.write(UICommon.BOUNDARY_FUNCTION_TYPE_COMMENT);
            buf_writer.newLine();  
            buf_writer.write(UICommon.BOUNDARY_FUNCTION_TYPE_PROPERTIES+" = "+ui_bound_cond.getBoundaryFunctionType());
            buf_writer.newLine();

            buf_writer.close();
        }
        catch(IOException w)
        {
          System.out.println("IOException: "+w);
        }
        
        /** Creation of file with properties of tanks */
        try
        {
          String tanks_name = ui.getTankPath();
          
          System.out.println("Name of file with tank properties: "+tanks_name);
          
          file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+tanks_name+"_"+
                          UICommon.TANKS_PROPERTIES+"."+Common.TANK_EXTENSION);
              
          if(file.exists())
            file.delete();
          else
            file.createNewFile();
          
          BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));
          
          buf_writer.write("# Each string corresponds to certain facet: \n" +
                           "# 1st - left, 2nd - front, 3rd - top, 4th - bottom, 5th - back, 6th - right.\n" +
                           "# Each column contains the following values for corresponding facet:\n" +
                           "#  1. name of boundary material;\n" +
                           "#  2. first coordinate of tank along axis OX;\n" +
                           "#  3. second coordinate of tank along axis OX;\n" +
                           "#  4. first coordinate of tank along axis OY;\n" +
                           "#  5. second coordinate of tank along axis OY;\n" +
                           "#  6. first coordinate of tank along axis OZ;\n" +
                           "#  7. second coordinate of tank along axis OZ;\n" +
                           "#  8. name of chosen mechanical energy source;\n" +
                           "#  9. name of chosen thermal energy source;\n" +
                  "# 10. minimal value of chosen mechanical energy source;\n" +
                  "# 11. maximal value of chosen mechanical energy source;\n" +
                  "# 12. minimal value of chosen thermal energy source;\n" +
                  "# 13. maximal value of chosen thermal energy source;\n" +
                  "# 14. time function type (0 - uniform loading, 1 - cycle loading, 2 - loading according to periodic function);\n" +
                  "# 15. loading time portion;\n" +
                  "# 16. relaxation time portion.\n");
          
          if(tanks_name.equals(UICommon.DEFAULT_BOUND_COND_FILE_NAME))
          {
            buf_writer.write("titanium 0.0 0.0 0.0 0.0 0.0 0.0 N/A N/A 0.0 0.0 0.0 0.0 0 0.0 0.0\n");
            buf_writer.write("titanium 0.0 0.0 0.0 0.0 0.0 0.0 N/A N/A 0.0 0.0 0.0 0.0 0 0.0 0.0\n");
            buf_writer.write("titanium 0.0 0.0 0.0 0.0 0.0 0.0 N/A N/A 0.0 0.0 0.0 0.0 0 0.0 0.0\n");
            buf_writer.write("titanium 0.0 0.0 0.0 0.0 0.0 0.0 N/A N/A 0.0 0.0 0.0 0.0 0 0.0 0.0\n");
            buf_writer.write("titanium 0.0 0.0 0.0 0.0 0.0 0.0 N/A N/A 0.0 0.0 0.0 0.0 0 0.0 0.0\n");
            buf_writer.write("titanium 0.0 0.0 0.0 0.0 0.0 0.0 N/A N/A 0.0 0.0 0.0 0.0 0 0.0 0.0\n");
          
            buf_writer.newLine();
          
            buf_writer.write("# Geometrical type of tanks:\n" +
                             "# 0. parallelepiped tanks;\n" +
                             "# 1. vertical ellyptic tanks;\n" +
                             "# 2. horizontal triangle tanks;\n" +
                             "# 3. horizontal circle tanks.\n");
            buf_writer.write(UICommon.GEOMETRICAL_TYPE_OF_TANKS+" = "+UICommon.parallelepiped_tank);
          }
          
          buf_writer.close();
        }
        catch(IOException w)
        {
          System.out.println("IOException: "+w);
        }
     }

     public SaveParams(UITank received_ui_tank, TransferredDataBank trans_data_bank)
     {
         System.out.println("SaveParams: constructor: SaveParams(UITank received_ui_tank, TransferredDataBank trans_data_bank)");

         // Remember boundary condition data bank

        ui_tank = received_ui_tank;

        // Remember interface data bank

        UIInterface ui = trans_data_bank.getUIInterface();

        // Remember specimen data bank

        UISpecimen spec = trans_data_bank.getUISpecimen();

        /*
         * Create folder for bound cond of specimen
         */

        File temp_file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME);

        if(!temp_file.isDirectory())
            temp_file.mkdir();

        /*
         * If file exist, then he removed for rewrite
         */
        file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getTankPath()+"_tanks."+Common.TANK_EXTENSION);
        if(file.exists())
            file.delete();

        /*
         * Write params
         */

        try
        {
            file = new File(Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getTankPath()+"_tanks."+Common.TANK_EXTENSION);

            // Create file

            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            // WRITE COMMENTS
            buf_writer.write("# Each string corresponds to certain facet: "
                         + "\n# 1st - left, 2nd - front, 3rd - top, 4th - bottom, 5th - back, 6th - right."
                         + "\n# Each column contains the following values for corresponding facet:"
                         + "\n#  1. name of boundary material;"
                         + "\n#  2. first coordinate of tank along axis OX;"
                         + "\n#  3. second coordinate of tank along axis OX;"
                         + "\n#  4. first coordinate of tank along axis OY;"
                         + "\n#  5. second coordinate of tank along axis OY;"
                         + "\n#  6. first coordinate of tank along axis OZ;"
                         + "\n#  7. second coordinate of tank along axis OZ;"
                         + "\n#  8. name of chosen mechanical energy source;"
                         + "\n#  9. name of chosen thermal energy source;"
                         + "\n# 10. minimal value of chosen mechanical energy source;"
                         + "\n# 11. maximal value of chosen mechanical energy source;"
                         + "\n# 12. minimal value of chosen thermal energy source;"
                         + "\n# 13. maximal value of chosen thermal energy source;"
                         + "\n# 14. time function type (0 - uniform loading, 1 - cycle loading, 2 - loading according to periodic function);"
                         + "\n# 15. loading time portion;"
                         + "\n# 16. relaxation time portion.\n");
            
            // LEFT
            buf_writer.write(ui_tank.getLeftAdiabaticMaterial()+" ");
            buf_writer.write(ui_tank.getLeftCoordinateX1()+" ");
            buf_writer.write(ui_tank.getLeftCoordinateX2()+" ");
            buf_writer.write(ui_tank.getLeftCoordinateY1()+" ");
            buf_writer.write(ui_tank.getLeftCoordinateY2()+" ");
            buf_writer.write(ui_tank.getLeftCoordinateZ1()+" ");
            buf_writer.write(ui_tank.getLeftCoordinateZ2()+" ");
            
            if(ui_tank.getLeftCheckMechanicalLoading())
            {
                if(ui_tank.getLeftStress())
                    buf_writer.write(UICommon.STRESS_R_B_NAME+" ");
                else
                    if(ui_tank.getLeftStrain())
                        buf_writer.write(UICommon.STRAIN_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            if(ui_tank.getLeftCheckThermalInflux())
            {
                if(ui_tank.getLeftTemperature())
                    buf_writer.write(UICommon.TEMPERATURE_R_B_NAME+" ");
                else
                    if(ui_tank.getLeftEnergy())
                        buf_writer.write(UICommon.ENERGY_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            buf_writer.write(ui_tank.getLeftMinMechValue()+" "+ui_tank.getLeftMechValue()+" ");
            buf_writer.write(ui_tank.getLeftMinThermValue()+" "+ui_tank.getLeftThermValue()+" ");
            
            buf_writer.write(ui_tank.getLeftTimeFunctionType()+" ");
            buf_writer.write(ui_tank.getLeftLoadingTimePortion()+" ");
            buf_writer.write(ui_tank.getLeftRelaxationTimePortion()+" ");
            
            buf_writer.newLine();

            // FRONT
            buf_writer.write(ui_tank.getFrontAdiabaticMaterial()+" ");
            buf_writer.write(ui_tank.getFrontCoordinateX1()+" ");
            buf_writer.write(ui_tank.getFrontCoordinateX2()+" ");
            buf_writer.write(ui_tank.getFrontCoordinateY1()+" ");
            buf_writer.write(ui_tank.getFrontCoordinateY2()+" ");
            buf_writer.write(ui_tank.getFrontCoordinateZ1()+" ");
            buf_writer.write(ui_tank.getFrontCoordinateZ2()+" ");
            
            if(ui_tank.getFrontCheckMechanicalLoading())
            {
                if(ui_tank.getFrontStress())
                    buf_writer.write(UICommon.STRESS_R_B_NAME+" ");
                else
                    if(ui_tank.getFrontStrain())
                        buf_writer.write(UICommon.STRAIN_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            if(ui_tank.getFrontCheckThermalInflux())
            {
                if(ui_tank.getFrontTemperature())
                    buf_writer.write(UICommon.TEMPERATURE_R_B_NAME+" ");
                else
                    if(ui_tank.getFrontEnergy())
                        buf_writer.write(UICommon.ENERGY_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            buf_writer.write(ui_tank.getFrontMinMechValue()+" "+ui_tank.getFrontMechValue()+" ");
            buf_writer.write(ui_tank.getFrontMinThermValue()+" "+ui_tank.getFrontThermValue()+" ");
            
            buf_writer.write(ui_tank.getFrontTimeFunctionType()+" ");
            buf_writer.write(ui_tank.getFrontLoadingTimePortion()+" ");
            buf_writer.write(ui_tank.getFrontRelaxationTimePortion()+" ");
            
            buf_writer.newLine();

            // TOP
            buf_writer.write(ui_tank.getTopAdiabaticMaterial()+" ");
            buf_writer.write(ui_tank.getTopCoordinateX1()+" ");
            buf_writer.write(ui_tank.getTopCoordinateX2()+" ");
            buf_writer.write(ui_tank.getTopCoordinateY1()+" ");
            buf_writer.write(ui_tank.getTopCoordinateY2()+" ");
            buf_writer.write(ui_tank.getTopCoordinateZ1()+" ");
            buf_writer.write(ui_tank.getTopCoordinateZ2()+" ");
            
            if(ui_tank.getTopCheckMechanicalLoading())
            {
                if(ui_tank.getTopStress())
                    buf_writer.write(UICommon.STRESS_R_B_NAME+" ");
                else
                    if(ui_tank.getTopStrain())
                        buf_writer.write(UICommon.STRAIN_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            if(ui_tank.getTopCheckThermalInflux())
            {
                if(ui_tank.getTopTemperature())
                    buf_writer.write(UICommon.TEMPERATURE_R_B_NAME+" ");
                else
                    if(ui_tank.getTopEnergy())
                        buf_writer.write(UICommon.ENERGY_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            buf_writer.write(ui_tank.getTopMinMechValue()+" "+ui_tank.getTopMechValue()+" ");
            buf_writer.write(ui_tank.getTopMinThermValue()+" "+ui_tank.getTopThermValue()+" ");
            
            buf_writer.write(ui_tank.getTopTimeFunctionType()+" ");
            buf_writer.write(ui_tank.getTopLoadingTimePortion()+" ");
            buf_writer.write(ui_tank.getTopRelaxationTimePortion()+" ");
            
            buf_writer.newLine();

            // BOTTOM
            buf_writer.write(ui_tank.getBottomAdiabaticMaterial()+" ");
            buf_writer.write(ui_tank.getBottomCoordinateX1()+" ");
            buf_writer.write(ui_tank.getBottomCoordinateX2()+" ");
            buf_writer.write(ui_tank.getBottomCoordinateY1()+" ");
            buf_writer.write(ui_tank.getBottomCoordinateY2()+" ");
            buf_writer.write(ui_tank.getBottomCoordinateZ1()+" ");
            buf_writer.write(ui_tank.getBottomCoordinateZ2()+" ");
            
            if(ui_tank.getBottomCheckMechanicalLoading())
            {
                if(ui_tank.getBottomStress())
                    buf_writer.write(UICommon.STRESS_R_B_NAME+" ");
                else
                    if(ui_tank.getBottomStrain())
                        buf_writer.write(UICommon.STRAIN_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            if(ui_tank.getBottomCheckThermalInflux())
            {
                if(ui_tank.getBottomTemperature())
                    buf_writer.write(UICommon.TEMPERATURE_R_B_NAME+" ");
                else
                    if(ui_tank.getBottomEnergy())
                        buf_writer.write(UICommon.ENERGY_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            buf_writer.write(ui_tank.getBottomMinMechValue()+" "+ui_tank.getBottomMechValue()+" ");
            buf_writer.write(ui_tank.getBottomMinThermValue()+" "+ui_tank.getBottomThermValue()+" ");
            
            buf_writer.write(ui_tank.getBottomTimeFunctionType()+" ");
            buf_writer.write(ui_tank.getBottomLoadingTimePortion()+" ");
            buf_writer.write(ui_tank.getBottomRelaxationTimePortion()+" ");
            
            buf_writer.newLine();

            // BACK
            buf_writer.write(ui_tank.getBackAdiabaticMaterial()+" ");
            buf_writer.write(ui_tank.getBackCoordinateX1()+" ");
            buf_writer.write(ui_tank.getBackCoordinateX2()+" ");
            buf_writer.write(ui_tank.getBackCoordinateY1()+" ");
            buf_writer.write(ui_tank.getBackCoordinateY2()+" ");
            buf_writer.write(ui_tank.getBackCoordinateZ1()+" ");
            buf_writer.write(ui_tank.getBackCoordinateZ2()+" ");
            
            if(ui_tank.getBackCheckMechanicalLoading())               
            {
                if(ui_tank.getBackStress())
                    buf_writer.write(UICommon.STRESS_R_B_NAME+" ");
                else
                    if(ui_tank.getBackStrain())
                        buf_writer.write(UICommon.STRAIN_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            if(ui_tank.getBackCheckThermalInflux())
            {
                if(ui_tank.getBackTemperature())
                    buf_writer.write(UICommon.TEMPERATURE_R_B_NAME+" ");
                else
                    if(ui_tank.getBackEnergy())
                        buf_writer.write(UICommon.ENERGY_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            buf_writer.write(ui_tank.getBackMinMechValue()+" "+ui_tank.getBackMechValue()+" ");
            buf_writer.write(ui_tank.getBackMinThermValue()+" "+ui_tank.getBackThermValue()+" ");
            
            buf_writer.write(ui_tank.getBackTimeFunctionType()+" ");
            buf_writer.write(ui_tank.getBackLoadingTimePortion()+" ");
            buf_writer.write(ui_tank.getBackRelaxationTimePortion()+" ");
            
            buf_writer.newLine();

            // RIGHT
            buf_writer.write(ui_tank.getRightAdiabaticMaterial()+" ");
            buf_writer.write(ui_tank.getRightCoordinateX1()+" ");
            buf_writer.write(ui_tank.getRightCoordinateX2()+" ");
            buf_writer.write(ui_tank.getRightCoordinateY1()+" ");
            buf_writer.write(ui_tank.getRightCoordinateY2()+" ");
            buf_writer.write(ui_tank.getRightCoordinateZ1()+" ");
            buf_writer.write(ui_tank.getRightCoordinateZ2()+" ");
            
            if(ui_tank.getRightCheckMechanicalLoading())
            {
                if(ui_tank.getRightStress())
                    buf_writer.write(UICommon.STRESS_R_B_NAME+" ");
                else
                    if(ui_tank.getRightStrain())
                        buf_writer.write(UICommon.STRAIN_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            if(ui_tank.getRightCheckThermalInflux())
            {
                if(ui_tank.getRightTemperature())
                    buf_writer.write(UICommon.TEMPERATURE_R_B_NAME+" ");
                else
                    if(ui_tank.getRightEnergy())
                        buf_writer.write(UICommon.ENERGY_R_B_NAME+" ");
            }
            else
                buf_writer.write(UICommon.N_A_R_B_NAME+" ");
            
            buf_writer.write(ui_tank.getRightMinMechValue()+" "+ui_tank.getRightMechValue()+" ");
            buf_writer.write(ui_tank.getRightMinThermValue()+" "+ui_tank.getRightThermValue()+" ");
            
            buf_writer.write(ui_tank.getRightTimeFunctionType()+" ");
            buf_writer.write(ui_tank.getRightLoadingTimePortion()+" ");
            buf_writer.write(ui_tank.getRightRelaxationTimePortion()+" ");
            
            buf_writer.newLine();
            buf_writer.newLine();
            
            // Geometrical type of tanks
            buf_writer.write("# Geometrical type of tanks:\n"
                           + "# 0. parallelepiped tanks;\n"
                           + "# 1. vertical ellyptic tanks;\n"
                           + "# 2. horizontal triangle tanks;\n"
                           + "# 3. horizontal circle tanks.\n");
            
            buf_writer.write(UICommon.GEOMETRICAL_TYPE_OF_TANKS+" = ");            
            
            if(ui_tank.getCheckParallelepipedTankType())   buf_writer.write(UICommon.parallelepiped_tank);
            if(ui_tank.getCheckVerticalEllypticTankType()) buf_writer.write(UICommon.vertical_ellyptic_tank);
            if(ui_tank.getCheckHorizonTriangleTankType())  buf_writer.write(UICommon.horizon_triangle_tank);
            if(ui_tank.getCheckHorizonCircleTankType())    buf_writer.write(UICommon.horizon_circle_tank);
            
            if(!ui_tank.getCheckParallelepipedTankType()&
               !ui_tank.getCheckVerticalEllypticTankType()&
               !ui_tank.getCheckHorizonTriangleTankType()&
               !ui_tank.getCheckHorizonCircleTankType())
            {
                buf_writer.write("no_tank_type");
                System.out.println("ERROR!!! Geometrical type of tanks is not determined.");
            }
            
            buf_writer.newLine();
            buf_writer.close();
        }
        catch(IOException w)
        {
            System.out.println("ERROR!!! "+w);
        }
     }

     /** Write task params in coresponding file
      * @param temp_task - data bank for writing
      * @param file_name - location of file for writing
      * @param ui_interf - location of choosed parameters (specimen, init cond and bound cond)
      * @param boundary_type - boundary type:
      * # 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
      * # 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
      * # 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
      */
     public SaveParams(UITask temp_task, String file_name, UIInterface ui_interf, UISpecimen ui_spec, UIBoundaryCondition ui_bound_cond, String tank_name)
     {
         System.out.println("SaveParams: constructor: SaveParams(UITask temp_task, String file_name, UIInterface ui_interf, UISpecimen ui_spec): creation start");

         // Remember interface data bank

        ui_interface = ui_interf;

        // Remember specimen data bank

        ui_specimen = ui_spec;

        // Remember task data bank

        ui_task = new UITask(temp_task);

        /*
         * Create file with name "task" in which write choosed task
         */

        choosed_task_file = new File(Common.TASK_PATH+"/"+Common.CHOOSED_TASK_FILE_NAME+"."+Common.TASK_EXTENSION);

        /*
         * If file exist, then he removed for rewrite
         */

        choosed_task_file = new File(Common.TASK_PATH+"/"+Common.CHOOSED_TASK_FILE_NAME+"."+Common.TASK_EXTENSION);
        if(choosed_task_file.exists())
            choosed_task_file.delete();

        /*
         * Write choosed task
         */

        try
        {
            choosed_task_file = new File(Common.TASK_PATH+"/"+Common.CHOOSED_TASK_FILE_NAME+"."+Common.TASK_EXTENSION);

            // Create file

            if(!choosed_task_file.exists())
                choosed_task_file.createNewFile();
            
            BufferedWriter choosed_task_writer = new BufferedWriter(new FileWriter(choosed_task_file));

            choosed_task_writer.write(file_name.toString());

            choosed_task_writer.close();
        }
        catch(IOException w)
        {
        }

        /*
         * Write file with task comments
         */

        try
        {
            File choosed_task_comments_file = new File(Common.TASK_PATH+"/"+file_name+"."+Common.TASK_COMMENTS_EXTENSION);

            // Create file

            if(!choosed_task_comments_file.exists())
                choosed_task_comments_file.createNewFile();

            String str = ui_task.getTaskComments().getText();

            BufferedWriter buf_comments_writer = new BufferedWriter(new FileWriter(choosed_task_comments_file));
            buf_comments_writer.write(str);
            buf_comments_writer.newLine();
            buf_comments_writer.close();
            
        }
        catch(IOException w)
        {
        }


        /*
         * Create file with all parameters, if file exist
         * then he removed for rewrite
         */

        file = new File(Common.TASK_PATH+"/"+file_name+"."+Common.TASK_EXTENSION);
        if(file.exists())
            file.delete();

        /*
         * Write params
         */

        try
        {
            file = new File(Common.TASK_PATH+"/"+file_name+"."+Common.TASK_EXTENSION);

            // Create file

            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            // Write params
            buf_writer.write("# Specimen name");
            buf_writer.newLine();
            buf_writer.write("specimen_name = "+ui_interface.getSpecimenPath());
            buf_writer.newLine();
            buf_writer.newLine();
            buf_writer.write(Common.FIRST_COMMENT_IN_TASK_PARAMETERS);
            buf_writer.newLine();            
            buf_writer.write(Common.INIT_PARAMS_FILE+Common.INIT_COND_PATH+"/"+ui_interface.getSpecimenPath()+Common.INIT_COND_NAME+"/"+ui_interface.getInitCondPath()+"."+Common.INIT_COND_EXTENSION);
         //   buf_writer.newLine();
            buf_writer.newLine();
            if(ui_interface.getSpecialSpecimenPath()==null)
                buf_writer.write(Common.LAYERS_FILE_NAME+"N/A");
            else
                buf_writer.write(Common.LAYERS_FILE_NAME+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+Common.INIT_COND_NAME+"/"+ui_interface.getSpecialSpecimenPath()+".lrs");

        //    buf_writer.newLine();
            buf_writer.newLine();

            if(tank_name==null)
                buf_writer.write(Common.TANKS_FILE_NAME+"N/A");
            else
                buf_writer.write(Common.TANKS_FILE_NAME + Common.SPEC_PATH + "/" + ui_interface.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+tank_name+"_tanks."+Common.TANK_EXTENSION);
            
         //   buf_writer.newLine();
            buf_writer.newLine();
            buf_writer.write(Common.BOUND_PARAMS_FILE+Common.BOUND_COND_PATH+"/"+ui_interface.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_interface.getBoundCondPath()+"."+Common.BOUND_COND_EXTENSION);
        //    buf_writer.newLine();
            buf_writer.newLine();
            buf_writer.write(Common.BOUND_GRAINS_FILE_NAME+Common.BOUND_COND_PATH+"/"+ui_interface.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_interface.getBoundCondPath()+"_grains."+Common.BOUND_COND_EXTENSION);
         //   buf_writer.newLine();
            buf_writer.newLine();
            buf_writer.write(Common.PHASES_FILE_NAME+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+"/"+ui_interface.getSpecimenPath()+"."+Common.SPECIMEN_MATERIAL_EXTENSION);
            buf_writer.newLine();
            buf_writer.newLine();
            buf_writer.write(Common.SECOND_COMMENT_IN_TASK_PARAMETERS);
            buf_writer.newLine();
            buf_writer.write(Common.INIT_COND_FILE_GEOMETRY+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+Common.INIT_COND_NAME+"/"+ui_interface.getSpecimenPath()+"."+Common.INIT_COND_FILE_GEOMETRY_EXTENSION);
            buf_writer.newLine();
            buf_writer.write(Common.INIT_COND_INPUT_FILE+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+Common.INIT_COND_NAME+"/"+"init_cond_rec."+Common.INIT_COND_INPUT_FILE_EXTENSION);
            buf_writer.newLine();
            buf_writer.write(Common.SPECIMEN_FILE+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+"/"+ui_interface.getSpecimenPath()+"."+Common.SPECIMEN_INTERFACE_EXTENSION);
            buf_writer.newLine();
            buf_writer.write(Common.INIT_COND_FILE+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+Common.INIT_COND_NAME+"/"+ui_interface.getInitCondPath()+"."+Common.INIT_COND_FILE_EXTENSION);
            buf_writer.newLine();
            buf_writer.write(Common.BOUND_COND_FILE+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_interface.getBoundCondPath()+"."+Common.BOUND_COND_FILE_EXTENSION);
            buf_writer.newLine();
            buf_writer.write(Common.BOUND_COND_FILE_GEOMETRY+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui_interface.getBoundCondPath()+"_bound_geom."+Common.BOUND_COND_FILE_GEOMETRY_EXTENSION);
            buf_writer.newLine();
            buf_writer.write(Common.GRAINS_FILE+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+"/"+ui_interface.getBoundCondPath()+"."+Common.GRAINS_FILE_EXTENSION);
            buf_writer.newLine();
            buf_writer.write(Common.GRAINS_FILE_2+Common.TASK_PATH+"/"+ui_interface.getTaskPath()+"/"+ui_interface.getTaskPath()+"."+Common.GRAINS_FILE_EXTENSION);
            buf_writer.newLine();
            buf_writer.write("grain_colours_file = "+Common.SPEC_PATH+"/"+ui_interface.getSpecimenPath()+"/init_cond_db/grain_colours.clrs");
            buf_writer.newLine();
            buf_writer.write("grain_colours_file_2 = "+Common.TASK_PATH+"/"+ui_interface.getTaskPath()+"/"+ui_interface.getTaskPath()+".clrs");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Value of time step");
            buf_writer.newLine();
            buf_writer.write(UICommon.TIME_STEP_PROPERTIES+" = "+new Double(ui_task.getTimeStep()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write("# Total time of numerical experiment");
            buf_writer.newLine();
            buf_writer.write(UICommon.TOTAL_TIME_PROPERTIES+" = "+new Double(ui_task.getTotalTime()).toString());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write("# Number of output files");
            buf_writer.newLine();
            buf_writer.write(UICommon.OUTPUT_FILE_PROPERTIES+" = "+new Long(ui_task.getOutputFile()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Variables responsible for simulation of corresponding processes");
            buf_writer.newLine();
            if(ui_task.getHeatTransfer())
                buf_writer.write(UICommon.HEAT_TRANSFER_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.HEAT_TRANSFER_PROPERTIES+" = 0");
            buf_writer.newLine();
            if(ui_task.getMechanicalElating())
                buf_writer.write(UICommon.MECHANICAL_ELATING_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.MECHANICAL_ELATING_PROPERTIES+" = 0");
            buf_writer.newLine();
            if(ui_task.getRecrystallization())
                buf_writer.write(UICommon.RECRYSTALLIZATION_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.RECRYSTALLIZATION_PROPERTIES+" = 0");
            buf_writer.newLine();
            if(ui_task.getCrackFormation())
                buf_writer.write(UICommon.CRACK_FORMATION_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.CRACK_FORMATION_PROPERTIES+" = 0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# This variable is responsible for state of specimen (equilibrium or nonequilibrium):");
            buf_writer.newLine();
            buf_writer.write("#if state=0 then recrystallization proceeds under initial temperature,");
            buf_writer.newLine();
            buf_writer.write("#if state=1 then recrystallization does not proceed under initial temperature.");
            buf_writer.newLine();
            if(ui_task.getEquilibriumState())
                buf_writer.write(UICommon.EQUILIBRIUM_STATE_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.EQUILIBRIUM_STATE_PROPERTIES+" = 0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Variable responsible for showing of cells on grain boundaries as atoms 'H' in 'RasMol'");
            buf_writer.newLine();
      //      if(ui_task.getShowGrainBounds())
      //          buf_writer.write(UICommon.SHOW_GRAIN_BOUNDS_PROPERTIES+" = 1");
      //      else
      //          buf_writer.write(UICommon.SHOW_GRAIN_BOUNDS_PROPERTIES+" = 0");
      //      buf_writer.newLine();

            if(ui_specimen.getGrainCellularAutomata())
                buf_writer.write(UICommon.SHOW_GRAIN_BOUNDS_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.SHOW_GRAIN_BOUNDS_PROPERTIES+" = 0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write(UICommon.PACKING_TYPE_COMMENT);
            buf_writer.newLine();
            buf_writer.write(UICommon.PACKING_TYPE_PROPERTIES+" = "+ui_specimen.getPackingType());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write(UICommon.TASK_PARAM_CALC_HEAT_EXPANSION_COMMENT);
            buf_writer.newLine();
            if(ui_task.getCalcHeatExpansion())
                buf_writer.write(UICommon.CALC_HEAT_EXPANSION_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.CALC_HEAT_EXPANSION_PROPERTIES+" = 0");
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.DEFECTS_INFLUENCE_COMMENT);
            buf_writer.newLine();
            if(ui_task.getDefectsInfluence())
                buf_writer.write(UICommon.DEFECTS_INFLUENCE_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.DEFECTS_INFLUENCE_PROPERTIES+" = 0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write(UICommon.TASK_PARAM_CALC_FORCE_MOMENTS_COMMENT);
            buf_writer.newLine();
            if(ui_task.getCalcForceMoments())
                buf_writer.write(UICommon.CALC_FORCE_MOMENTS_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.CALC_FORCE_MOMENTS_PROPERTIES+" = 0");
            buf_writer.newLine();

            buf_writer.write("#-------------------------------------------------------------------------------------------------------");
            buf_writer.newLine();
            
            buf_writer.write("# Size of specimen along axis X (in cell radiuses). In meters: "+ui_specimen.getSpecimenSizeX());
            double spec_size_X = 0.001*Math.round(1000*ui_specimen.getSpecimenSizeX()*2/ui_specimen.getCellSize());
            
            buf_writer.newLine();
            buf_writer.write(UICommon.SPECIMEN_SIZE_X_PROPERTIES+" = "+spec_size_X);
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Size of specimen along axis Y (in cell radiuses). In meters: "+ui_specimen.getSpecimenSizeY());
            double spec_size_Y = 0.001*Math.round(1000*ui_specimen.getSpecimenSizeY()*2/ui_specimen.getCellSize());
            
            buf_writer.newLine();
            buf_writer.write(UICommon.SPECIMEN_SIZE_Y_PROPERTIES+" = "+spec_size_Y);
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Size of specimen along axis Z (in cell radiuses). In meters: "+ui_specimen.getSpecimenSizeZ());
            double spec_size_Z = 0.001*Math.round(1000*ui_specimen.getSpecimenSizeZ()*2/ui_specimen.getCellSize());
            
            buf_writer.newLine();
            buf_writer.write(UICommon.SPECIMEN_SIZE_Z_PROPERTIES+" = "+spec_size_Z);
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Thickness of surface layer perpendicular to axis Z (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.SURF_THICKNESS_PROPERTIES+" = "+new Double(ui_specimen.getSurfThickness()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Size of cell");
            buf_writer.newLine();
            buf_writer.write(UICommon.CELL_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getCellSize()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Name of bulk material of specimen");
            buf_writer.newLine();
            buf_writer.write(UICommon.BULK_SPECIMEN_MATERIAL_PROPERTIES+" = aluminium");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Number of finite elements in direction of X-axis");
            buf_writer.newLine();
            buf_writer.write(UICommon.ELEMENT_NUMBER_X_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberX()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Number of finite elements in direction of Y-axis");
            buf_writer.newLine();
            buf_writer.write(UICommon.ELEMENT_NUMBER_Y_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberY()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Number of finite elements in direction of Z-axis");
            buf_writer.newLine();
            buf_writer.write(UICommon.ELEMENT_NUMBER_Z_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberZ()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Volume fraction of particles");
            buf_writer.newLine();
            buf_writer.write(UICommon.PARTICLE_VOLUME_FRACTION_PROPERTIES+" = "+new Double(ui_specimen.getParticleVolumeFraction()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Radius of particle");
            buf_writer.newLine();
            buf_writer.write(UICommon.PARTICLE_RADIUS_PROPERTIES+" = "+new Double(ui_specimen.getParticleRadius()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Minimal number of neighbour cells in adjacent grain necessary");
            buf_writer.newLine();
            buf_writer.write(UICommon.MIN_NEIGHBOURS_NUMBER_PROPERTIES+" = "+new Integer(ui_specimen.getMinNeighboursNumber()).toString());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write(UICommon.BOUNDARY_TYPE_COMMENT);
            buf_writer.write(UICommon.BOUNDARY_TYPE_PROPERTIES+" = "+ui_bound_cond.getBoundaryType());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write(UICommon.TASK_PARAM_GLOBAL_BOUNDARY_TYPE_COMMENT);
            buf_writer.write(UICommon.GLOBAL_BOUNDARY_TYPE_PROPERTIES+" = "+ui_task.getBoundaryType());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.BOUNDARY_FUNCTION_TYPE_COMMENT);
            buf_writer.newLine();
            buf_writer.write(UICommon.BOUNDARY_FUNCTION_TYPE_PROPERTIES+" = "+ui_bound_cond.getBoundaryFunctionType());
            buf_writer.newLine();
            buf_writer.newLine();
         /*
            buf_writer.write("# Type of functional dependence of boundary cell parameter on time:\n");
            buf_writer.write("# 0 - constant function;\n");
            buf_writer.write("# 1 - cycle loading;\n");
            buf_writer.write("# 2 - periodic function.\n");
            buf_writer.write("bound_time_function_type = 0\n\n");
            
            buf_writer.write("# Portion of total time period when boundary cells are loaded");
            buf_writer.newLine();
            buf_writer.write("bound_load_time_portion = 1.0");
            buf_writer.newLine();
            buf_writer.newLine();
         */

            buf_writer.write(UICommon.TASK_RESPONSE_RATE_COMMENT);
            buf_writer.write(UICommon.TASK_RESPONSE_RATE_PROPERTIES+" = "+ui_task.getResponseRate());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.TORSION_ENERGY_COEFF_COMMENT_PROPERTIES);
            buf_writer.newLine();
            buf_writer.write(UICommon.TORSION_ENERGY_COEFF_PROPERTIES+" = 1.0E4");
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write("# Field responsible for stochastic distribution of embryos");
            buf_writer.newLine();
            buf_writer.write("STOCHASTIC_EMBRYOS       =       0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Field responsible for regular distribution of embryos in centres of cubes");
            buf_writer.newLine();
            buf_writer.write("REGULAR_EMBRYOS          =       1");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Field responsible for stochastic distribution of embryos in centres of cubes");
            buf_writer.newLine();
            buf_writer.write("STOCH_EMBRYOS_IN_CUBES   =       2");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Method of distribution of embryos");
            buf_writer.newLine();
            
            if(ui_specimen.getStochasticMethod())
                buf_writer.write("DISTRIBUTION_METHOD      =       0");
            
            if(ui_specimen.getRegularMethod())
                buf_writer.write("DISTRIBUTION_METHOD      =       1");
            
            if(ui_specimen.getMixedMethod())
                buf_writer.write("DISTRIBUTION_METHOD      =       2");
            
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANISOTROPY_PRESENCE_COMMENT);
            buf_writer.newLine();
            
        //    if(ui_task.getAnisPresence())
            if(ui_specimen.getSpecimenAnisotropyCoeff() != 1.0 | ui_specimen.getAnisotropyCoeff() != 1.0)
                buf_writer.write(UICommon.ANISOTROPY_PRESENCE_PROPERTIES+" = 1");
            else
                buf_writer.write(UICommon.ANISOTROPY_PRESENCE_PROPERTIES+" = 0");
            
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.SPECIMEN_ANIS_COEFF_COMMENT);
            buf_writer.newLine();
            buf_writer.write(UICommon.SPECIMEN_ANIS_COEFF_PROPERTIES+" = "+ui_specimen.getSpecimenAnisotropyCoeff());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANISOTROPY_VECTOR_COMMENT);
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANISOTROPY_VECTOR_X_PROPERTIES+" = "+ui_specimen.getAnisVectorX());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANISOTROPY_VECTOR_Y_PROPERTIES+" = "+ui_specimen.getAnisVectorY());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.ANISOTROPY_VECTOR_Z_PROPERTIES+" = "+ui_specimen.getAnisVectorZ());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.OUTER_ANISOTROPY_COEFF_COMMENT);
            buf_writer.newLine();
            buf_writer.write(UICommon.OUTER_ANIS_COEFF_PROPERTIES+" = "+ui_specimen.getAnisotropyCoeff());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write(UICommon.OUTER_ANISOTROPY_VECTOR_COMMENT);
            buf_writer.newLine();
            buf_writer.write(UICommon.OUTER_ANIS_VECTOR_X_PROPERTIES+" = "+ui_specimen.getCoordinateX());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.OUTER_ANIS_VECTOR_Y_PROPERTIES+" = "+ui_specimen.getCoordinateY());
            buf_writer.newLine();
            
            buf_writer.write(UICommon.OUTER_ANIS_VECTOR_Z_PROPERTIES+" = "+ui_specimen.getCoordinateZ());
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.write("#-------------------------------------------------------------------------------------");
            buf_writer.newLine();

            buf_writer.write("# The probability of generation of dendrite");
            buf_writer.newLine();
            buf_writer.write("GENERATION_PROBABILITY   =       0.00026666666667");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# The probability of growth of dendrite");
            buf_writer.newLine();
            buf_writer.write("GROWTH_PROBABILITY       =       0.99999");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# The probability of turn of dendrite");
            buf_writer.newLine();
     //     buf_writer.write("TURN_PROBABILITY         =       0.3");
            buf_writer.write("TURN_PROBABILITY         =       0.0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# The probability of straight growth of dendrite");
            buf_writer.newLine();
     //     buf_writer.write("STRAIGHT_PROBABILITY     =       0.3");
            buf_writer.write("STRAIGHT_PROBABILITY     =       0.0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# The probability of appearance of 2nd dendrite branch");
            buf_writer.newLine();
            buf_writer.write("BRANCHING2_PROBABILITY   =       0.0");
     //     buf_writer.write("BRANCHING2_PROBABILITY   =       0.001");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# The probability of appearance of 3rd dendrite branch");
            buf_writer.newLine();
       //   buf_writer.write("BRANCHING3_PROBABILITY   =       0.0001");
            buf_writer.write("BRANCHING3_PROBABILITY   =       0.0");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# The probability of thickening of dendrite");
            buf_writer.newLine();
      //    buf_writer.write("THICKENING_PROBABILITY   =       0.9");
            buf_writer.write("THICKENING_PROBABILITY   =       0.99999");
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Number of time steps");
            buf_writer.newLine();
            buf_writer.write("STEP_NUMBER              =    1000");
            buf_writer.newLine();
            buf_writer.newLine();

        /* 
            buf_writer.write("# Coefficient of inner anisotropy");
            buf_writer.newLine();
      //    buf_writer.write("COEFF_INNER_ANISOTROPY   =       1.0E7");
      //    buf_writer.write("COEFF_INNER_ANISOTROPY   =       1.0E1");
            buf_writer.write("COEFF_INNER_ANISOTROPY   =       "+ui_specimen.getSpecimenAnisotropyCoeff());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Coordinates of inner anisotropy vector");
            buf_writer.newLine();
        //  buf_writer.write("INNER_ANISOTROPY_COORD_X =      -1.0");
            buf_writer.write("INNER_ANISOTROPY_COORD_X =      "+ui_specimen.getAnisVectorX());
            buf_writer.newLine();
        //  buf_writer.write("INNER_ANISOTROPY_COORD_Y =      -1.0");
            buf_writer.write("INNER_ANISOTROPY_COORD_Y =      "+ui_specimen.getAnisVectorY());
            buf_writer.newLine();
        //  buf_writer.write("INNER_ANISOTROPY_COORD_Z =       0.0");
            buf_writer.write("INNER_ANISOTROPY_COORD_Z =      "+ui_specimen.getAnisVectorZ());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Coefficient of outer anisotropy");
            buf_writer.newLine();
       //   buf_writer.write("COEFF_OUTER_ANISOTROPY   =       1.0E7");
       //   buf_writer.write("COEFF_OUTER_ANISOTROPY   =       1.0E1");
            buf_writer.write("COEFF_OUTER_ANISOTROPY   =       "+ui_specimen.getAnisotropyCoeff());
            buf_writer.newLine();
            buf_writer.newLine();

            buf_writer.write("# Coordinates of outer anisotropy vector");
            buf_writer.newLine();
       //   buf_writer.write("OUTER_ANISOTROPY_COORD_X =      -1.0");
            buf_writer.write("OUTER_ANISOTROPY_COORD_X =      "+ui_specimen.getCoordinateX());
            buf_writer.newLine();
       //   buf_writer.write("OUTER_ANISOTROPY_COORD_Y =       1.0");
            buf_writer.write("OUTER_ANISOTROPY_COORD_Y =      "+ui_specimen.getCoordinateY());
            buf_writer.newLine();
       //   buf_writer.write("OUTER_ANISOTROPY_COORD_Z =       0.0");
            buf_writer.write("OUTER_ANISOTROPY_COORD_Z =      "+ui_specimen.getCoordinateZ());
            buf_writer.newLine();
            buf_writer.newLine();
        */

        /*
            buf_writer.newLine();
            buf_writer.write("# Average size (in cell diameters) of grain in surface layer");
            buf_writer.newLine();
            buf_writer.write("AVERAGE_GRAIN_SIZE_2     =      "+ui_specimen.getAverageGrainLength_2()/ui_specimen.getCellSize());
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Average size (in cell diameters) of grain in upper intermediate layer");
            buf_writer.newLine();
            buf_writer.write("AVERAGE_GRAIN_SIZE_3     =      "+ui_specimen.getAverageGrainLength_3()/ui_specimen.getCellSize());
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Average size (in cell diameters) of grain in lower intermediate layer");
            buf_writer.newLine();
            buf_writer.write("AVERAGE_GRAIN_SIZE_4     =      "+ui_specimen.getAverageGrainLength_4()/ui_specimen.getCellSize());
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Average size (in cell diameters) of grain in higher substrate");
            buf_writer.newLine();
            buf_writer.write("AVERAGE_GRAIN_SIZE_5     =      "+ui_specimen.getAverageGrainLength_5()/ui_specimen.getCellSize());
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Average size (in cell diameters) of grain in lower substrate");
            buf_writer.newLine();
            buf_writer.write("AVERAGE_GRAIN_SIZE       =      "+ui_specimen.getAverageGrainLength()/ui_specimen.getCellSize());
            buf_writer.newLine();
        */
            buf_writer.close();

        //    saveParams(file_name, ui_specimen.getCellSize());
        }
        catch(IOException e)
        {
            System.out.println("IOException: "+e);
        }

        /*
         * Create folder with name of choosed task for input there results files
         */

        // If folder not exists then it created
        File folder = new File(Common.TASK_PATH+"/"+file_name);
        if(!folder.exists())
            folder.mkdir();

        // If folder exists then it removed for create it again
        else
        if(folder.exists())
        {
            folder.delete();
            File folder2 = new File(Common.TASK_PATH+"/"+file_name);
            folder2.mkdir();
        }
     }

     /** The method saves coordinates and parameters of cells from chosen plane to file (for Surfer)
      * @param file_directory directory of file
      * @param folder_name name of created folder
      * @param point2d coordinates of cells from plane
      * @param values parameters of cells from plane
      * @param what_to_show chosen parameter of cells, which distribution in plane must be written to file
      */
     public SaveParams(String file_directory, String folder_name, DoubleVector[] point2d, Double[] values, String what_to_show)
     {
         System.out.println("SaveParams: constructor: SaveParams(String file_directory, String file_name, DoubleVector [] point2d, Double [] values): creation start");

        /** Create folder with inputed name for
         */
        // If folder not exists then it created
        File folder = new File(file_directory+"/"+folder_name);
        
        System.out.println("SaveParams: directory of created file: "+file_directory+"/"+folder_name);
        
        if(!folder.exists())
            folder.mkdir();
        // If folder exists then it removed for create it again
//        else
//        {
//            FolderRemover folder_remover = new FolderRemover(file_directory+"/"+file_name);
//            folder.mkdir();
//        }

        // Create 6 files with coordinate values and corresponding
        // characteristic, it can be: temperature, effective stress,
        // principal stress,X moments, Y moments, Z moments, grain number and
        // automata state

        try
        {
            // Create folder with title "Surfer" - it will consists
            // file with structure for Surfer
            // If folder not exists then it created
            File surfer_folder = new File(file_directory+"/"+folder_name);//+"/Surfer");
            
            if(!surfer_folder.exists())
                surfer_folder.mkdir();
            // If folder exists then it removed for create it again
//            else
//            {
//                FolderRemover surfer_folder_remover = new FolderRemover(file_directory+"/"+file_name+"/Surfer");
//                surfer_folder.mkdir();
//            }

            File file = new File("some");
            
            System.out.println("SaveParams: chosen parameter: "+what_to_show);

            // Case of file "*.res".
            if(what_to_show.equals((UICommon.SHOW_TEMPERATURE)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TEMPERATURE+".txt");
            
            if(what_to_show.equals(UICommon.SHOW_EFFECTIVE_STRESS))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_EFFECTIVE_STRESS+".txt");
            
            if(what_to_show.equals(UICommon.SHOW_PRINCIPAL_STRESS))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_PRINCIPAL_STRESS+".txt");
            
            if(what_to_show.equals(UICommon.SHOW_STRAIN))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_STRAIN+".txt");
            
            if(what_to_show.equals(UICommon.SHOW_MOMENTS_X))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_MOMENTS_X+".txt");
            
            if(what_to_show.equals(UICommon.SHOW_MOMENTS_Y))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_MOMENTS_Y+".txt");
            
            if(what_to_show.equals(UICommon.SHOW_MOMENTS_Z))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_MOMENTS_Z+".txt");
            
            if(what_to_show.equals(UICommon.SHOW_DISLOCATION_DENSITIES))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_DISLOCATION_DENSITIES+".txt");
            
            // Case of file "*inst_mom.res".
            if(what_to_show.equals((UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_X)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_X+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_Y)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_Y+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_Z)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_Z+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_INSTANT_FORCE_MOMENT_X)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_X+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_INSTANT_FORCE_MOMENT_Y)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_Y+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_INSTANT_FORCE_MOMENT_Z)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_Z+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_ABS_INSTANT_FORCE_MOMENT)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ABS_INSTANT_FORCE_MOMENT+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_DISS_ENERGY_INFLUX)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_DISS_ENERGY_INFLUX+".txt");
            
            // Case of file "*torsion.res".
            if(what_to_show.equals((UICommon.SHOW_ANGLE_VELOCITY_X)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ANGLE_VELOCITY_X+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_ANGLE_VELOCITY_Y)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ANGLE_VELOCITY_Y+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_ANGLE_VELOCITY_Z)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ANGLE_VELOCITY_Z+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_TORSION_ANGLE_X)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TORSION_ANGLE_X+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_TORSION_ANGLE_Y)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TORSION_ANGLE_Y+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_TORSION_ANGLE_Z)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TORSION_ANGLE_Z+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_DISS_ENERGY_RELATIVE_INFLUX)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_DISS_ENERGY_RELATIVE_INFLUX+".txt");
            
            if(what_to_show.equals((UICommon.SHOW_RELATIVE_DISS_ENERGY)))
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_RELATIVE_DISS_ENERGY+".txt");
            
            // Show of specimen structure
            if(what_to_show.equals(UICommon.SHOW_SPEC_BOUND_STRUCTURE))
            {
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_spec_bound_structure.txt");
            }
            
            if(what_to_show.equals(UICommon.SHOW_ALL_SPECIMEN_STRUCTURE))
            {
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_spec_materials.txt");
            }
                        
            if(what_to_show.equals(UICommon.SHOW_SPECIMEN_GRAIN_STRUCTURE))
            {
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_spec_grains.txt");
            }
            
            if(what_to_show.equals(UICommon.SHOW_SPECIMEN_BOUNDARIES))
            {
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_spec_boundaries.txt");
            }
            
            if(what_to_show.equals(UICommon.SHOW_STATES))
            {
                file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_spec_states.txt");
            }

            // If some file exists then it must be removed for rewiting
            if(file.exists())
                file.delete();

            // Create files
            if(!file.exists())
                file.createNewFile();
            
            System.out.println("SaveParams: file with data about values in simple plane: "+file.getName());

            // Create buffered writer for write all data in 8 files
            BufferedWriter file_buf_writer = new BufferedWriter(new FileWriter(file));

            // Temporary remembering automata number
            int array_size = values.length;
            
            // Variable responsible for change of places of coordinates X and Y in text file
            boolean replace_coordinates = true;// false;// 

            // Write params
            for(int i = 0; i < array_size; i++)
            {
                if(!replace_coordinates)
                    file_buf_writer.write(point2d[i].getComponent(0)+" "+point2d[i].getComponent(1)+" "+values[i]);
                else
                    file_buf_writer.write(point2d[i].getComponent(1)+" "+point2d[i].getComponent(0)+" "+values[i]);
                
                file_buf_writer.newLine();
            }

            // Close all buffered writers
            file_buf_writer.close();
        }
        catch(IOException w)
        {
            System.out.println("Error: "+w);
        }
     }

     /** The method saves coordinates and parameters of cells from chosen plane to file (for Surfer)
      * @param file_directory directory of file
      * @param folder_name name of created folder
      * @param plane_cells array of cells from plane
      * @param point2d coordinates of cells from plane
      * @param file_name name of file with data
      * @param chosen_param chosen parameter of cells, which distribution in plane must be written to file
      */
     public SaveParams(String file_directory, String folder_name, ArrayList[] plane_cells, DoubleVector[] point2d, String file_name, String chosen_param)
     {
         System.out.println("SaveParams: constructor: SaveParams(String file_directory, String file_name, DoubleVector [] point2d): creation start");
         System.out.println("SaveParams: file_name = "+file_name);
         
         // Creation of file with coordinates and chosen parameters of each cell from chosen plane
         try
         {
            // Creation of folder 
            File surfer_folder = new File(file_directory+"/"+folder_name);
            
            if(!surfer_folder.exists())
                surfer_folder.mkdir();

            File temperature_file;
            File effective_stresses_file;
            File principle_stresses_file;
            File x_moments_file;
            File y_moments_file;
            File z_moments_file;
            File strain_file;
            File spec_diss_energy_file;            
            File specimen_structure_file;
            File specimen_structure_state_file;
            
            if(file_name.contains("_inst_mom.res") | file_name.contains("_torsion.res"))
            {
              if(file_name.contains("_inst_mom.res"))
              {
                temperature_file        = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_X);
                effective_stresses_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_Y);
                principle_stresses_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_ABS_Z);
                x_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_X);
                y_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_Y);
                z_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_INSTANT_FORCE_MOMENT_Z);                
                strain_file             = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ABS_INSTANT_FORCE_MOMENT);
                spec_diss_energy_file   = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_DISS_ENERGY_INFLUX);
              }
              else
              {
                temperature_file        = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ANGLE_VELOCITY_X);
                effective_stresses_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ANGLE_VELOCITY_Y);
                principle_stresses_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_ANGLE_VELOCITY_Z);
                x_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TORSION_ANGLE_X);
                y_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TORSION_ANGLE_Y);
                z_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TORSION_ANGLE_Z);                
                strain_file             = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_DISS_ENERGY_RELATIVE_INFLUX);
                spec_diss_energy_file   = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_RELATIVE_DISS_ENERGY);
              }
            }
            else  
            {
                temperature_file        = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_TEMPERATURE);
                effective_stresses_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_EFFECTIVE_STRESS);
                principle_stresses_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_PRINCIPAL_STRESS);
                x_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_MOMENTS_X);
                y_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_MOMENTS_Y);
                z_moments_file          = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_MOMENTS_Z);                
                strain_file             = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_STRAIN);
                spec_diss_energy_file   = new File(file_directory+"/"+folder_name+"/"+folder_name+"_"+UICommon.SHOW_DISLOCATION_DENSITIES);
            }
            
            specimen_structure_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_struct.txt");
            specimen_structure_state_file = new File(file_directory+"/"+folder_name+"/"+folder_name+"_struct_state.txt");
         
            // If some file exists then it must be removed for rewritten
            if(temperature_file.exists())              temperature_file.delete();
            if(effective_stresses_file.exists())       effective_stresses_file.delete();
            if(principle_stresses_file.exists())       principle_stresses_file.delete();
            if(x_moments_file.exists())                x_moments_file.delete();
            if(y_moments_file.exists())                y_moments_file.delete();
            if(z_moments_file.exists())                z_moments_file.delete();
            if(specimen_structure_file.exists())       specimen_structure_file.delete();
            if(specimen_structure_state_file.exists()) specimen_structure_state_file.delete();            
            if(strain_file.exists())                   strain_file.delete();
            if(spec_diss_energy_file.exists())         spec_diss_energy_file.delete();

            // Create files
            if(!temperature_file.exists())             temperature_file.createNewFile();
            if(!effective_stresses_file.exists())      effective_stresses_file.createNewFile();
            if(!principle_stresses_file.exists())      principle_stresses_file.createNewFile();
            if(!x_moments_file.exists())               x_moments_file.createNewFile();
            if(!y_moments_file.exists())               y_moments_file.createNewFile();
            if(!z_moments_file.exists())               z_moments_file.createNewFile();
            if(!specimen_structure_file.exists())      specimen_structure_file.createNewFile();
            if(!specimen_structure_state_file.exists())specimen_structure_state_file.createNewFile();
            if(!strain_file.exists())                  strain_file.createNewFile();
            if(!spec_diss_energy_file.exists())        spec_diss_energy_file.createNewFile();

            // Create buffered writer for write all data in 8 files
            BufferedWriter temperature_buf_writer        = new BufferedWriter(new FileWriter(temperature_file));
            BufferedWriter effective_stresses_buf_writer = new BufferedWriter(new FileWriter(effective_stresses_file));
            BufferedWriter principle_stresses_buf_writer = new BufferedWriter(new FileWriter(principle_stresses_file));
            BufferedWriter x_moments_buf_writer          = new BufferedWriter(new FileWriter(x_moments_file));
            BufferedWriter y_moments_buf_writer          = new BufferedWriter(new FileWriter(y_moments_file));
            BufferedWriter z_moments_buf_writer          = new BufferedWriter(new FileWriter(z_moments_file));
            BufferedWriter spec_str_buf_writer           = new BufferedWriter(new FileWriter(specimen_structure_file));
            BufferedWriter spec_str_state_buf_writer     = new BufferedWriter(new FileWriter(specimen_structure_state_file));            
            BufferedWriter strain_buf_writer             = new BufferedWriter(new FileWriter(strain_file));
            BufferedWriter spec_diss_energy_buf_writer   = new BufferedWriter(new FileWriter(spec_diss_energy_file));

            // Number of cells in first plane
            int first_plane_cell_number = plane_cells[0].size();
            
            // Number of cells in second plane
            int second_plane_cell_number = plane_cells[1].size();
            
            double coord_X;
            double coord_Y;
            VisualCellR3 cell;

            // Write params
            for(int index = 0; index < first_plane_cell_number + second_plane_cell_number; index++)
            {
                coord_X = point2d[index].getComponent(1);
                coord_Y = point2d[index].getComponent(0);
                
                if(index < first_plane_cell_number)
                  cell  = (VisualCellR3)plane_cells[0].get(index);
                else
                  cell  = (VisualCellR3)plane_cells[1].get(index - first_plane_cell_number);
                
                temperature_buf_writer.write       (coord_X+" "+coord_Y+" "+cell.getTemperature());
                temperature_buf_writer.newLine();
                
                effective_stresses_buf_writer.write(coord_X+" "+coord_Y+" "+cell.getEffStress());
                effective_stresses_buf_writer.newLine();
                
                principle_stresses_buf_writer.write(coord_X+" "+coord_Y+" "+cell.getPrinStress());
                principle_stresses_buf_writer.newLine();
                
                x_moments_buf_writer.write         (coord_X+" "+coord_Y+" "+cell.getForceMomentX());
                x_moments_buf_writer.newLine();
                
                y_moments_buf_writer.write         (coord_X+" "+coord_Y+" "+cell.getForceMomentY());
                y_moments_buf_writer.newLine();
                
                z_moments_buf_writer.write         (coord_X+" "+coord_Y+" "+cell.getForceMomentZ());
                z_moments_buf_writer.newLine();
                
                strain_buf_writer.write            (coord_X+" "+coord_Y+" "+cell.getStrain());
                strain_buf_writer.newLine();
                
                spec_diss_energy_buf_writer.write  (coord_X+" "+coord_Y+" "+cell.getDisclDensity());
                spec_diss_energy_buf_writer.newLine();
                
                spec_str_buf_writer.write          (coord_X+" "+coord_Y+" "+cell.getGrainIndex());
                spec_str_buf_writer.newLine();
                
                spec_str_state_buf_writer.write    (coord_X+" "+coord_Y+" "+cell.getState());
                spec_str_state_buf_writer.newLine();
                
                temperature_buf_writer.flush();
                effective_stresses_buf_writer.flush();
                principle_stresses_buf_writer.flush();
                x_moments_buf_writer.flush();
                y_moments_buf_writer.flush();
                z_moments_buf_writer.flush();
                spec_str_buf_writer.flush();
                spec_str_state_buf_writer.flush();                
                strain_buf_writer.flush();
                spec_diss_energy_buf_writer.flush();
            }
            
            // Close all buffered writers
            temperature_buf_writer.close();
            effective_stresses_buf_writer.close();
            principle_stresses_buf_writer.close();
            x_moments_buf_writer.close();
            y_moments_buf_writer.close();
            z_moments_buf_writer.close();
            spec_str_buf_writer.close();
            spec_str_state_buf_writer.close();
            strain_buf_writer.close();
            spec_diss_energy_buf_writer.close();
        }
        catch(IOException w)
        {
           System.out.println("IOException: "+w);
        }
     }

    /* Write result picture frame params into files for RasMol
     * @param automata_data - array of VisualCellR3 typed elements
     * @param step_number - number of time step corresponding to read file
     * @param read_file_name - name of read file with results
     * @param write_file_name - name of written file (with extension ".pdb")
     */
     public SaveParams(ArrayList automata_data, String file_directory, String file_name)
     {
            // Coefficient of magnification in "RasMol"
            double magnif_coeff = 2.25;

            //Index of grain containing cell
            int grain_index = 0;

            // Coordinates of cell
            double coordinate_X = 0;
            double coordinate_Y = 0;
            double coordinate_Z = 0;

            // Cell temperature
            int temperature = 0;
            int eff_stresses = 0;
            int prin_stresses = 0;
            int x_mom_stresses = 0;
            int y_mom_stresses = 0;
            int z_mom_stresses = 0;
            int state_of_cell = 0;

            // Type of cell location in grain: internal or boundary
            byte location_in_grain = 0;

            // Name of cell state
            String cell_state = "N  ";

            // Coordinates of cell multiplied by 10000 and rounded to integer
            int coordinate_X_round;
            int coordinate_Y_round;
            int coordinate_Z_round;

            // Integer parts of cell coordinates
            int coordinate_X_int;
            int coordinate_Y_int;
            int coordinate_Z_int;

            // Fractional parts of cell coordinates
            int coordinate_X_frac;
            int coordinate_Y_frac;
            int coordinate_Z_frac;

            // Number exponents of cell coordinates
            byte num_exp_X;
            byte num_exp_Y;
            byte num_exp_Z;

            try
            {

                // Create folder with inputed name for saving all data files
                // If folder not exists then it created
                File folder = new File(file_directory+"/"+file_name);
                if(!folder.exists())
                    folder.mkdir();
                // If folder exists then it removed for create it again
                else
                {
                    FolderRemover folder_remover = new FolderRemover(file_directory+"/"+file_name);
                    folder.mkdir();
                }

            // Create folder with title "RasMol" - it will consists
            // file with structure for RasMol
            // If folder not exists then it created
            File ras_mol__folder = new File(file_directory+"/"+file_name+"/RasMol");
            if(!ras_mol__folder.exists())
                ras_mol__folder.mkdir();
            // If folder exists then it removed for create it again
            else
            {
                FolderRemover ras_mol_folder_remover = new FolderRemover(file_directory+"/"+file_name+"/RasMol");
                ras_mol__folder.mkdir();
            }

            File ras_mol_temperature_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_tempr.pdb");
            File ras_mol_effective_stresses_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_eff_str_.pdb");
            File ras_mol_principle_stresses_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_pr_str.pdb");
            File ras_mol_x_moments_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_mom_X.pdb");
            File ras_mol_y_moments_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_mom_Y.pdb");
            File ras_mol_z_moments_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_mom_Z.pdb");
            File ras_mol_specimen_structure_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_struct.pdb");
            File ras_mol_specimen_structure_state_file = new File(file_directory+"/"+file_name+"/RasMol/"+file_name+"_struct_state.pdb");

            // If some file exists then it must be removed for rewiting
            if(ras_mol_temperature_file.exists())
                ras_mol_temperature_file.delete();
            if(ras_mol_effective_stresses_file.exists())
                ras_mol_effective_stresses_file.delete();
            if(ras_mol_principle_stresses_file.exists())
                ras_mol_principle_stresses_file.delete();
            if(ras_mol_x_moments_file.exists())
                ras_mol_x_moments_file.delete();
            if(ras_mol_y_moments_file.exists())
                ras_mol_y_moments_file.delete();
            if(ras_mol_z_moments_file.exists())
                ras_mol_z_moments_file.delete();
            if(ras_mol_specimen_structure_file.exists())
                ras_mol_specimen_structure_file.delete();
            if(ras_mol_specimen_structure_state_file.exists())
                ras_mol_specimen_structure_state_file.delete();

            // Create files
            if(!ras_mol_temperature_file.exists())
                ras_mol_temperature_file.createNewFile();
            if(!ras_mol_effective_stresses_file.exists())
                ras_mol_effective_stresses_file.createNewFile();
            if(!ras_mol_principle_stresses_file.exists())
                ras_mol_principle_stresses_file.createNewFile();
            if(!ras_mol_x_moments_file.exists())
                ras_mol_x_moments_file.createNewFile();
            if(!ras_mol_y_moments_file.exists())
                ras_mol_y_moments_file.createNewFile();
            if(!ras_mol_z_moments_file.exists())
                ras_mol_z_moments_file.createNewFile();
            if(!ras_mol_specimen_structure_file.exists())
                ras_mol_specimen_structure_file.createNewFile();
            if(!ras_mol_specimen_structure_state_file.exists())
                ras_mol_specimen_structure_state_file.createNewFile();

            // Create buffered writer for write all data in 8 files
            BufferedWriter ras_mol_temperature_buf_writer = new BufferedWriter(new FileWriter(ras_mol_temperature_file));
            BufferedWriter ras_mol_effective_stresses_buf_writer = new BufferedWriter(new FileWriter(ras_mol_effective_stresses_file));
            BufferedWriter ras_mol_principle_stresses_buf_writer = new BufferedWriter(new FileWriter(ras_mol_principle_stresses_file));
            BufferedWriter ras_mol_x_moments_buf_writer = new BufferedWriter(new FileWriter(ras_mol_x_moments_file));
            BufferedWriter ras_mol_y_moments_buf_writer = new BufferedWriter(new FileWriter(ras_mol_y_moments_file));
            BufferedWriter ras_mol_z_moments_buf_writer = new BufferedWriter(new FileWriter(ras_mol_z_moments_file));
            BufferedWriter ras_mol_spec_str_buf_writer = new BufferedWriter(new FileWriter(ras_mol_specimen_structure_file));
            BufferedWriter ras_mol_spec_str_state_buf_writer = new BufferedWriter(new FileWriter(ras_mol_specimen_structure_state_file));

            // For (int stringCounter = 0; stringCounter < string_number; stringCounter++)
            for(int i = 0; i < automata_data.size(); i++)
            {
                // Reading of coordinates and physical parameters of current cell

                location_in_grain = ((VisualCellR3) automata_data.get(i)).getLocationType();
                grain_index  = ((VisualCellR3) automata_data.get(i)).getGrainIndex();

                coordinate_X = ((VisualCellR3) automata_data.get(i)).getCoordX();
                coordinate_Y = ((VisualCellR3) automata_data.get(i)).getCoordY();
                coordinate_Z = ((VisualCellR3) automata_data.get(i)).getCoordZ();

                temperature  = (int)((VisualCellR3) automata_data.get(i)).getTemperature();
                eff_stresses  = (int)((VisualCellR3) automata_data.get(i)).getEffStress();

                eff_stresses  = (int)((VisualCellR3) automata_data.get(i)).getEffStress();
                prin_stresses  = (int)((VisualCellR3) automata_data.get(i)).getPrinStress();
                x_mom_stresses  = (int)((VisualCellR3) automata_data.get(i)).getForceMomentX();
                y_mom_stresses  = (int)((VisualCellR3) automata_data.get(i)).getForceMomentY();
                z_mom_stresses  = (int)((VisualCellR3) automata_data.get(i)).getForceMomentZ();
                state_of_cell  = (int)((VisualCellR3) automata_data.get(i)).getState();

                    

                // Redetermination of cell coordinates
                coordinate_X = magnif_coeff*coordinate_X;
                coordinate_Y = magnif_coeff*coordinate_Y;
                coordinate_Z = magnif_coeff*coordinate_Z;

                    
                if (grain_index%22 ==  0) cell_state = "N  ";
                if (grain_index%22 ==  1) cell_state = "Cu ";
                if (grain_index%22 ==  2) cell_state = "O  ";
                if (grain_index%22 ==  3) cell_state = "Si ";
                if (grain_index%22 ==  4) cell_state = "Fe ";
                if (grain_index%22 ==  5) cell_state = "B  ";
                if (grain_index%22 ==  6) cell_state = "Mg ";
                if (grain_index%22 ==  7) cell_state = "It ";
                if (grain_index%22 ==  8) cell_state = "Cd ";
                if (grain_index%22 ==  9) cell_state = "Na ";
                if (grain_index%22 == 10) cell_state = "Au ";
                if (grain_index%22 == 11) cell_state = "Al ";
                if (grain_index%22 == 12) cell_state = "I  ";
                if (grain_index%22 == 13) cell_state = "Cl ";
                if (grain_index%22 == 14) cell_state = "Br ";
                if (grain_index%22 == 15) cell_state = "Co ";
                if (grain_index%22 == 16) cell_state = "S  ";
                if (grain_index%22 == 17) cell_state = "Na ";
                if (grain_index%22 == 18) cell_state = "Pl ";
                if (grain_index%22 == 19) cell_state = "Mn ";
                if (grain_index%22 == 20) cell_state = "Li ";
                if (grain_index%22 == 21) cell_state = "I  ";

                ras_mol_temperature_buf_writer.write("ATOM         ");
                ras_mol_temperature_buf_writer.write(cell_state);
                ras_mol_temperature_buf_writer.write("              ");

                ras_mol_effective_stresses_buf_writer.write("ATOM         ");
                ras_mol_effective_stresses_buf_writer.write(cell_state);
                ras_mol_effective_stresses_buf_writer.write("              ");

                ras_mol_principle_stresses_buf_writer.write("ATOM         ");
                ras_mol_principle_stresses_buf_writer.write(cell_state);
                ras_mol_principle_stresses_buf_writer.write("              ");

                ras_mol_x_moments_buf_writer.write("ATOM         ");
                ras_mol_x_moments_buf_writer.write(cell_state);
                ras_mol_x_moments_buf_writer.write("              ");

                ras_mol_y_moments_buf_writer.write("ATOM         ");
                ras_mol_y_moments_buf_writer.write(cell_state);
                ras_mol_y_moments_buf_writer.write("              ");

                ras_mol_z_moments_buf_writer.write("ATOM         ");
                ras_mol_z_moments_buf_writer.write(cell_state);
                ras_mol_z_moments_buf_writer.write("              ");

                ras_mol_spec_str_buf_writer.write("ATOM         ");
                ras_mol_spec_str_buf_writer.write(cell_state);
                ras_mol_spec_str_buf_writer.write("              ");

                ras_mol_spec_str_state_buf_writer.write("ATOM         ");
                ras_mol_spec_str_state_buf_writer.write(cell_state);
                ras_mol_spec_str_state_buf_writer.write("              ");

                coordinate_X_round = (int)Math.round(10000*coordinate_X);
                coordinate_Y_round = (int)Math.round(10000*coordinate_Y);
                coordinate_Z_round = (int)Math.round(10000*coordinate_Z);

                // Obtaining of integer parts of cell coordinates
                coordinate_X_int   = coordinate_X_round/10000;
                coordinate_Y_int   = coordinate_Y_round/10000;
                coordinate_Z_int   = coordinate_Z_round/10000;

                // Obtaining of number exponents of cell coordinates
                num_exp_X = 3;
                if(coordinate_X_int/100 == 0) num_exp_X = (byte)(num_exp_X - 1);
                if(coordinate_X_int/ 10 == 0) num_exp_X = (byte)(num_exp_X - 1);

                num_exp_Y = 3;
                if(coordinate_Y_int/100 == 0) num_exp_Y = (byte)(num_exp_Y - 1);
                if(coordinate_Y_int/ 10 == 0) num_exp_Y = (byte)(num_exp_Y - 1);

                num_exp_Z = 3;
                if(coordinate_Z_int/100 == 0) num_exp_Z = (byte)(num_exp_Z - 1);
                if(coordinate_Z_int/ 10 == 0) num_exp_Z = (byte)(num_exp_Z - 1);

                // Obtaining of fractional parts of cell coordinates
                coordinate_X_frac  = coordinate_X_round%10000;
                coordinate_Y_frac  = coordinate_Y_round%10000;
                coordinate_Z_frac  = coordinate_Z_round%10000;

                // Writing of integer part of coordinate Y
                for(int space_counter = 0; space_counter < 3 - num_exp_Y; space_counter++)
                {
                    ras_mol_temperature_buf_writer.write(" ");
                    ras_mol_effective_stresses_buf_writer.write(" ");
                    ras_mol_principle_stresses_buf_writer.write(" ");
                    ras_mol_x_moments_buf_writer.write(" ");
                    ras_mol_y_moments_buf_writer.write(" ");
                    ras_mol_z_moments_buf_writer.write(" ");
                    ras_mol_spec_str_buf_writer.write(" ");
                    ras_mol_spec_str_state_buf_writer.write(" ");
                }

                ras_mol_temperature_buf_writer.write(coordinate_Y_int+".");
                ras_mol_effective_stresses_buf_writer.write(coordinate_Y_int+".");
                ras_mol_principle_stresses_buf_writer.write(coordinate_Y_int+".");
                ras_mol_x_moments_buf_writer.write(coordinate_Y_int+".");
                ras_mol_y_moments_buf_writer.write(coordinate_Y_int+".");
                ras_mol_z_moments_buf_writer.write(coordinate_Y_int+".");
                ras_mol_spec_str_buf_writer.write(coordinate_Y_int+".");
                ras_mol_spec_str_state_buf_writer.write(coordinate_Y_int+".");

                // Obtaining of number exponent of fractional part of coordinate Y
                if(coordinate_Y_frac/10 == 0)
                    num_exp_Y = 1;
                else
                    if(coordinate_Y_frac/100 == 0)
                        num_exp_Y = 2;
                    else
                        if(coordinate_Y_frac/1000 == 0)
                            num_exp_Y = 3;
                        else
                            if(coordinate_Y_frac/10000 == 0)
                                num_exp_Y = 4;

                // Writing of fractional part of coordinate Y
                for(int zero_counter = 0; zero_counter < 4 - num_exp_Y; zero_counter++)
                {
                    ras_mol_temperature_buf_writer.write("0");
                    ras_mol_effective_stresses_buf_writer.write("0");
                    ras_mol_principle_stresses_buf_writer.write("0");
                    ras_mol_x_moments_buf_writer.write("0");
                    ras_mol_y_moments_buf_writer.write("0");
                    ras_mol_z_moments_buf_writer.write("0");
                    ras_mol_spec_str_buf_writer.write("0");
                    ras_mol_spec_str_state_buf_writer.write("0");
                }

                ras_mol_temperature_buf_writer.write(coordinate_Y_frac+"");
                ras_mol_effective_stresses_buf_writer.write(coordinate_Y_frac+"");
                ras_mol_principle_stresses_buf_writer.write(coordinate_Y_frac+"");
                ras_mol_x_moments_buf_writer.write(coordinate_Y_frac+"");
                ras_mol_y_moments_buf_writer.write(coordinate_Y_frac+"");
                ras_mol_z_moments_buf_writer.write(coordinate_Y_frac+"");
                ras_mol_spec_str_buf_writer.write(coordinate_Y_frac+"");
                ras_mol_spec_str_state_buf_writer.write(coordinate_Y_frac+"");

                // Writing of integer part of coordinate X
                for(int space_counter = 0; space_counter < 3 - num_exp_X; space_counter++)
                {
                    ras_mol_temperature_buf_writer.write(" ");
                    ras_mol_effective_stresses_buf_writer.write(" ");
                    ras_mol_principle_stresses_buf_writer.write(" ");
                    ras_mol_x_moments_buf_writer.write(" ");
                    ras_mol_y_moments_buf_writer.write(" ");
                    ras_mol_z_moments_buf_writer.write(" ");
                    ras_mol_spec_str_buf_writer.write(" ");
                    ras_mol_spec_str_state_buf_writer.write(" ");
                }

                ras_mol_temperature_buf_writer.write(coordinate_X_int+".");
                ras_mol_effective_stresses_buf_writer.write(coordinate_X_int+".");
                ras_mol_principle_stresses_buf_writer.write(coordinate_X_int+".");
                ras_mol_x_moments_buf_writer.write(coordinate_X_int+".");
                ras_mol_y_moments_buf_writer.write(coordinate_X_int+".");
                ras_mol_z_moments_buf_writer.write(coordinate_X_int+".");
                ras_mol_spec_str_buf_writer.write(coordinate_X_int+".");
                ras_mol_spec_str_state_buf_writer.write(coordinate_X_int+".");

                // Obtaining of number exponent of fractional part of coordinate X
                if(coordinate_X_frac/10 == 0)
                    num_exp_X = 1;
                else
                    if(coordinate_X_frac/100 == 0)
                        num_exp_X = 2;
                    else
                        if(coordinate_X_frac/1000 == 0)
                            num_exp_X = 3;
                        else
                            if(coordinate_X_frac/10000 == 0)
                                num_exp_X = 4;

                // Writing of fractional part of coordinate X
                for(int zero_counter = 0; zero_counter < 4 - num_exp_X; zero_counter++)
                {
                    ras_mol_temperature_buf_writer.write("0");
                    ras_mol_effective_stresses_buf_writer.write("0");
                    ras_mol_principle_stresses_buf_writer.write("0");
                    ras_mol_x_moments_buf_writer.write("0");
                    ras_mol_y_moments_buf_writer.write("0");
                    ras_mol_z_moments_buf_writer.write("0");
                    ras_mol_spec_str_buf_writer.write("0");
                    ras_mol_spec_str_state_buf_writer.write("0");
                }

                ras_mol_temperature_buf_writer.write(coordinate_X_frac+"");
                ras_mol_effective_stresses_buf_writer.write(coordinate_X_frac+"");
                ras_mol_principle_stresses_buf_writer.write(coordinate_X_frac+"");
                ras_mol_x_moments_buf_writer.write(coordinate_X_frac+"");
                ras_mol_y_moments_buf_writer.write(coordinate_X_frac+"");
                ras_mol_z_moments_buf_writer.write(coordinate_X_frac+"");
                ras_mol_spec_str_buf_writer.write(coordinate_X_frac+"");
                ras_mol_spec_str_state_buf_writer.write(coordinate_X_frac+"");

                // Writing of integer part of coordinate Z
                for(int space_counter = 0; space_counter < 3 - num_exp_Z; space_counter++)
                {
                    ras_mol_temperature_buf_writer.write(" ");
                    ras_mol_effective_stresses_buf_writer.write(" ");
                    ras_mol_principle_stresses_buf_writer.write(" ");
                    ras_mol_x_moments_buf_writer.write(" ");
                    ras_mol_y_moments_buf_writer.write(" ");
                    ras_mol_z_moments_buf_writer.write(" ");
                    ras_mol_spec_str_buf_writer.write(" ");
                    ras_mol_spec_str_state_buf_writer.write(" ");
                }

                ras_mol_temperature_buf_writer.write(coordinate_Z_int+".");
                ras_mol_effective_stresses_buf_writer.write(coordinate_Z_int+".");
                ras_mol_principle_stresses_buf_writer.write(coordinate_Z_int+".");
                ras_mol_x_moments_buf_writer.write(coordinate_Z_int+".");
                ras_mol_y_moments_buf_writer.write(coordinate_Z_int+".");
                ras_mol_z_moments_buf_writer.write(coordinate_Z_int+".");
                ras_mol_spec_str_buf_writer.write(coordinate_Z_int+".");
                ras_mol_spec_str_state_buf_writer.write(coordinate_Z_int+".");

                // Obtaining of number exponent of fractional part of coordinate Z
                if(coordinate_Z_frac/10 == 0)
                    num_exp_Z = 1;
                else
                    if(coordinate_Z_frac/100 == 0)
                        num_exp_Z = 2;
                    else
                        if(coordinate_Z_frac/1000 == 0)
                            num_exp_Z = 3;
                        else
                            if(coordinate_Z_frac/10000 == 0)
                                num_exp_Z = 4;

                // Writing of fractional part of coordinate Z
                for(int zero_counter = 0; zero_counter < 4 - num_exp_Z; zero_counter++)
                {
                    ras_mol_temperature_buf_writer.write("0");
                    ras_mol_effective_stresses_buf_writer.write("0");
                    ras_mol_principle_stresses_buf_writer.write("0");
                    ras_mol_x_moments_buf_writer.write("0");
                    ras_mol_y_moments_buf_writer.write("0");
                    ras_mol_z_moments_buf_writer.write("0");
                    ras_mol_spec_str_buf_writer.write("0");
                    ras_mol_spec_str_state_buf_writer.write("0");
                }

                ras_mol_temperature_buf_writer.write(coordinate_Z_frac+"");
                ras_mol_effective_stresses_buf_writer.write(coordinate_Z_frac+"");
                ras_mol_principle_stresses_buf_writer.write(coordinate_Z_frac+"");
                ras_mol_x_moments_buf_writer.write(coordinate_Z_frac+"");
                ras_mol_y_moments_buf_writer.write(coordinate_Z_frac+"");
                ras_mol_z_moments_buf_writer.write(coordinate_Z_frac+"");
                ras_mol_spec_str_buf_writer.write(coordinate_Z_frac+"");
                ras_mol_spec_str_state_buf_writer.write(coordinate_Z_frac+"");

                // Writing of the value from the file
                ras_mol_temperature_buf_writer.write("      "+temperature);
                ras_mol_effective_stresses_buf_writer.write("      "+eff_stresses);
                ras_mol_principle_stresses_buf_writer.write("      "+prin_stresses);
                ras_mol_x_moments_buf_writer.write("      "+x_mom_stresses);
                ras_mol_y_moments_buf_writer.write("      "+y_mom_stresses);
                ras_mol_z_moments_buf_writer.write("      "+z_mom_stresses);
                ras_mol_spec_str_buf_writer.write("      "+grain_index);
                ras_mol_spec_str_state_buf_writer.write("      "+state_of_cell);

                ras_mol_temperature_buf_writer.newLine();
                ras_mol_effective_stresses_buf_writer.newLine();
                ras_mol_principle_stresses_buf_writer.newLine();
                ras_mol_x_moments_buf_writer.newLine();
                ras_mol_y_moments_buf_writer.newLine();
                ras_mol_z_moments_buf_writer.newLine();
                ras_mol_spec_str_buf_writer.newLine();
                ras_mol_spec_str_state_buf_writer.newLine();

                ras_mol_temperature_buf_writer.flush();
                ras_mol_effective_stresses_buf_writer.flush();
                ras_mol_principle_stresses_buf_writer.flush();
                ras_mol_x_moments_buf_writer.flush();
                ras_mol_y_moments_buf_writer.flush();
                ras_mol_z_moments_buf_writer.flush();
                ras_mol_spec_str_buf_writer.flush();
                ras_mol_spec_str_state_buf_writer.flush();
            }
                ras_mol_temperature_buf_writer.close();
                ras_mol_effective_stresses_buf_writer.close();
                ras_mol_principle_stresses_buf_writer.close();
                ras_mol_x_moments_buf_writer.close();
                ras_mol_y_moments_buf_writer.close();
                ras_mol_z_moments_buf_writer.close();
                ras_mol_spec_str_buf_writer.close();
                ras_mol_spec_str_state_buf_writer.close();
        }
        catch(FileNotFoundException w)
        {
            System.out.println("Error: " + w);
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }

     /*
      * Write special specimen data bank in current file
      * @param temp_ui_special_specimen - special specimen data bank
      * @param file_name - file for writing
      * @param cell_size - value of cell size, it's for translate
      * real units in cell radius and diameters
      */
     public SaveParams(UI_SpecialSpecimen temp_ui_special_specimen, String file_name, double cell_size)
     {
        System.out.println("SaveParams: constructor: SaveParams(UI_SpecialSpecimen temp_UI_special_specimen, String file_name): creation start");

        // Remember special specimen data bank

        ui_special_specimen = temp_ui_special_specimen;

        // If file with values of parameters of special specimen exists
        // then it removed for rewriting

        file = new File(Common.SPEC_PATH+"/"+file_name+Common.INIT_COND_NAME+"/"+file_name+".lrs");
        if(file.exists())
            file.delete();

        /*
         * Write params
         */
        try
        {
            file = new File(Common.SPEC_PATH+"/"+file_name+Common.INIT_COND_NAME+"/"+file_name+".lrs");

            // Create file
            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            double surf_thick_in_cell_radiuses                    = 0.001*Math.round(1000*ui_special_specimen.getFirstLayerThickness()*2/cell_size);
            double inter_layer_thick_in_cell_radiuses             = 0.001*Math.round(1000*ui_special_specimen.getSecondLayerThickness()*2/cell_size);
            double upper_substrate_thick_in_cell_radiuses         = 0.001*Math.round(1000*ui_special_specimen.getThirdLayerThickness()*2/cell_size);

            double layer_elem_size_in_cell_radiuses               = 0.001*Math.round(1000*ui_special_specimen.getLayerElemSize()*2/cell_size);
            byte   layer_type                                     = ui_special_specimen.getLayerType();
            byte   layer_direction                                = ui_special_specimen.getLayerDirection();

            double surf_grain_size_in_cell_diameters                = 0.001*Math.round(1000*ui_special_specimen.getFirstLayerGrainSize()/cell_size);
            double surf_grain_size_Y_in_cell_diameters              = 0.001*Math.round(1000*ui_special_specimen.getFirstLayerGrainSizeY()/cell_size);
            double surf_grain_size_Z_in_cell_diameters              = 0.001*Math.round(1000*ui_special_specimen.getFirstLayerGrainSizeZ()/cell_size);
            
            double upper_inter_layer_grain_size_in_cell_diameters   = 0.001*Math.round(1000*ui_special_specimen.getUpperSecondLayerGrainSize()/cell_size);
            double upper_inter_layer_grain_size_Y_in_cell_diameters = 0.001*Math.round(1000*ui_special_specimen.getUpperSecondLayerGrainSizeY()/cell_size);
            double upper_inter_layer_grain_size_Z_in_cell_diameters = 0.001*Math.round(1000*ui_special_specimen.getUpperSecondLayerGrainSizeZ()/cell_size);
            
            double lower_inter_layer_grain_size_in_cell_diameters   = 0.001*Math.round(1000*ui_special_specimen.getLowerSecondLayerGrainSize()/cell_size);
            double lower_inter_layer_grain_size_Y_in_cell_diameters = 0.001*Math.round(1000*ui_special_specimen.getLowerSecondLayerGrainSizeY()/cell_size);
            double lower_inter_layer_grain_size_Z_in_cell_diameters = 0.001*Math.round(1000*ui_special_specimen.getLowerSecondLayerGrainSizeZ()/cell_size);
            
            double upper_substrate_grain_size_in_cell_diameters     = 0.001*Math.round(1000*ui_special_specimen.getThirdLayerGrainSize()/cell_size);
            double upper_substrate_grain_size_Y_in_cell_diameters   = 0.001*Math.round(1000*ui_special_specimen.getThirdLayerGrainSizeY()/cell_size);
            double upper_substrate_grain_size_Z_in_cell_diameters   = 0.001*Math.round(1000*ui_special_specimen.getThirdLayerGrainSizeZ()/cell_size);
            
            double lower_substrate_grain_size_in_cell_diameters     = 0.001*Math.round(1000*ui_special_specimen.getSubstrateGrainSize()/cell_size);
            double lower_substrate_grain_size_Y_in_cell_diameters   = 0.001*Math.round(1000*ui_special_specimen.getSubstrateGrainSizeY()/cell_size);
            double lower_substrate_grain_size_Z_in_cell_diameters   = 0.001*Math.round(1000*ui_special_specimen.getSubstrateGrainSizeZ()/cell_size);

            // Writing of parameters
            buf_writer.write("# Surface thickness (in cell radiuses)");
            buf_writer.newLine();
            buf_writer.write(UICommon.FIRST_LAYER_THICKNESS_PROPERTIES+" = "+surf_thick_in_cell_radiuses);
            buf_writer.newLine();
            
            buf_writer.newLine();
            buf_writer.write("# Intermediate layer thickness (in cell radiuses)");
            buf_writer.newLine();
            buf_writer.write(UICommon.SECOND_LAYER_THICKNESS_PROPERTIES+" = "+inter_layer_thick_in_cell_radiuses);
            buf_writer.newLine();            
            
            buf_writer.newLine();
            buf_writer.write("# Upper substrate thickness (in cell radiuses)");
            buf_writer.newLine();
            buf_writer.write(UICommon.THIRD_LAYER_THICKNESS_PROPERTIES+" = "+upper_substrate_thick_in_cell_radiuses);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Intermediate layer structure element size (in cell radiuses)");
            buf_writer.newLine();
            buf_writer.write(UICommon.LAYER_ELEM_SIZE_PROPERTIES+" = "+layer_elem_size_in_cell_radiuses);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Intermediate layer structure type:\n"
                           + "# 0 - CONTINUOUS_LAYER, \n"
                           + "# 1 - HORIZONTAL_TRIANGLE_PRISMS, \n"
                           + "# 2 - HORIZONTAL_RIGHT_PARALLELEPIPEDS, \n"
                           + "# 3 - VERTICAL_SQUARE_PYRAMIDES, \n"
                           + "# 4 - VERTICAL_RIGHT_PARALLELEPIPEDS. ");            
            buf_writer.newLine();
            buf_writer.write(UICommon.LAYER_TYPE_PROPERTIES+" = "+layer_type);
            buf_writer.newLine();
            
            buf_writer.newLine();
            buf_writer.write("# The value responsible for choice of the direction of vector normal to layer interfaces:\n"
                           + "# 0 - along axis X, 1 - along axis Y, 2 - along axis Z. ");
            buf_writer.newLine();
            buf_writer.write(UICommon.LAYER_DIRECTION_PROPERTIES+" = "+layer_direction);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Surface grain sizes (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.FIRST_LAYER_GRAIN_SIZE_PROPERTIES+" = "+surf_grain_size_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.FIRST_LAYER_GRAIN_SIZE_Y_PROPERTIES+" = "+surf_grain_size_Y_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.FIRST_LAYER_GRAIN_SIZE_Z_PROPERTIES+" = "+surf_grain_size_Z_in_cell_diameters);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Upper intermediate layer grain sizes (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES+" = "+upper_inter_layer_grain_size_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_Y_PROPERTIES+" = "+upper_inter_layer_grain_size_Y_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_Z_PROPERTIES+" = "+upper_inter_layer_grain_size_Z_in_cell_diameters);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Lower intermediate layer grain sizes (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES+" = "+lower_inter_layer_grain_size_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_Y_PROPERTIES+" = "+lower_inter_layer_grain_size_Y_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_Z_PROPERTIES+" = "+lower_inter_layer_grain_size_Z_in_cell_diameters);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Upper substrate layer grain sizes (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.THIRD_LAYER_GRAIN_SIZE_PROPERTIES+" = "+upper_substrate_grain_size_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.THIRD_LAYER_GRAIN_SIZE_Y_PROPERTIES+" = "+upper_substrate_grain_size_Y_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.THIRD_LAYER_GRAIN_SIZE_Z_PROPERTIES+" = "+upper_substrate_grain_size_Z_in_cell_diameters);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Lower substrate layer grain sizes (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.SUBSTRATE_GRAIN_SIZE_PROPERTIES+" = "+lower_substrate_grain_size_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.SUBSTRATE_GRAIN_SIZE_Y_PROPERTIES+" = "+lower_substrate_grain_size_Y_in_cell_diameters);
            buf_writer.newLine();
            buf_writer.write(UICommon.SUBSTRATE_GRAIN_SIZE_Z_PROPERTIES+" = "+lower_substrate_grain_size_Z_in_cell_diameters);
            buf_writer.newLine();
            
            double surf_grain_bound_thickness              = 0.001*Math.round(1000*ui_special_specimen.getFirstLayerGrainBoundThickness()/cell_size);
            double upper_inter_layer_grain_bound_thickness = 0.001*Math.round(1000*ui_special_specimen.getUpperSecondLayerGrainBoundThickness()/cell_size);
            double lower_inter_layer_grain_bound_thickness = 0.001*Math.round(1000*ui_special_specimen.getLowerSecondLayerGrainBoundThickness()/cell_size);
            double upper_substrate_grain_bound_thickness   = 0.001*Math.round(1000*ui_special_specimen.getThirdLayerGrainBoundThickness()/cell_size);
            double lower_substrate_grain_bound_thickness   = 0.001*Math.round(1000*ui_special_specimen.getSubstrateGrainBoundThickness()/cell_size);
            
            buf_writer.newLine();
            buf_writer.write("# Surface grain boundary thickness (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.FIRST_LAYER_GRAIN_BOUND_THICK_PROPERTIES+" = "+surf_grain_bound_thickness);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Upper intermediate layer grain boundary thickness (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.UPPER_SECOND_LAYER_GRAIN_BOUND_THICK_PROPERTIES+" = "+upper_inter_layer_grain_bound_thickness);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Lower intermediate layer grain boundary thickness (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.LOWER_SECOND_LAYER_GRAIN_BOUND_THICK_PROPERTIES+" = "+lower_inter_layer_grain_bound_thickness);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Upper substrate layer grain boundary thickness (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.THIRD_LAYER_GRAIN_BOUND_THICK_PROPERTIES+" = "+upper_substrate_grain_bound_thickness);
            buf_writer.newLine();

            buf_writer.newLine();
            buf_writer.write("# Lower substrate layer grain boundary thickness (in cell diameters)");
            buf_writer.newLine();
            buf_writer.write(UICommon.SUBSTRATE_GRAIN_BOUND_THICK_PROPERTIES+" = "+lower_substrate_grain_bound_thickness);
            buf_writer.newLine();
            
            byte surface_layer_embryo_distr_type     = ui_special_specimen.getSurfaceLayerEmbryoDistrType();
            byte upper_inter_layer_embryo_distr_type = ui_special_specimen.getUpperInterLayerEmbryoDistrType();
            byte lower_inter_layer_embryo_distr_type = ui_special_specimen.getLowerInterLayerEmbryoDistrType();
            byte upper_substrate_embryo_distr_type   = ui_special_specimen.getUpperSubstrateEmbryoDistrType();
            byte lower_substrate_embryo_distr_type   = ui_special_specimen.getLowerSubstrateEmbryoDistrType();
            
            buf_writer.newLine();
            buf_writer.write("# Type of distribution of grain embryos in surface layer:\n");
            buf_writer.write("# 0 - stochastic distribution of embryos;\n");
            buf_writer.write("# 1 - regular distribution of embryos in centres of cubes;\n");
            buf_writer.write("# 2 - stochastic distribution of embryos in centres of cubes.\n");
            buf_writer.write(UICommon.SURFACE_LAYER_EMBRYO_DISTRIBUTION_TYPE+" = "+surface_layer_embryo_distr_type);
            buf_writer.newLine();
            
            buf_writer.newLine();
            buf_writer.write("# Type of distribution of grain embryos in upper intermediate layer\n");
            buf_writer.write(UICommon.UPPER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE+" = "+upper_inter_layer_embryo_distr_type);
            buf_writer.newLine();
            
            buf_writer.newLine();
            buf_writer.write("# Type of distribution of grain embryos in lower intermediate layer\n");
            buf_writer.write(UICommon.LOWER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE+" = "+lower_inter_layer_embryo_distr_type);
            buf_writer.newLine();
            
            buf_writer.newLine();
            buf_writer.write("# Type of distribution of grain embryos in upper substrate\n");
            buf_writer.write(UICommon.UPPER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE+" = "+upper_substrate_embryo_distr_type);
            buf_writer.newLine();
            
            buf_writer.newLine();
            buf_writer.write("# Type of distribution of grain embryos in lower substrate\n");
            buf_writer.write(UICommon.LOWER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE+" = "+lower_substrate_embryo_distr_type);
            buf_writer.newLine();
            buf_writer.newLine();
            
            buf_writer.close();
        }
        catch(IOException e)
        {
            System.out.println("IOException: "+e);
        }
    }

     /*
      * Write special specimen data bank in current file
      * @param temp_ui_special_specimen - special specimen data bank
      * @param file_name - file for writing
      * @param cell_size - value of cell size, it's for translate
      * real units in cell radius and diameters
      */
     private void saveParams(String file_name, double cell_size)
     {
        System.out.println("SaveParams: method: saveParams(UI_SpecialSpecimen temp_UI_special_specimen, String file_name): creation start");

        // Remember special specimen data bank
        // If file with values of parameters of special specimen exists
        // then it removed for rewriting

        file = new File(Common.SPEC_PATH+"/"+file_name+Common.INIT_COND_NAME+"/"+file_name+".lrs");
        if(file.exists())
            file.delete();

        /*
         * Write params
         */
        try
        {
            file = new File(Common.SPEC_PATH+"/"+file_name+Common.INIT_COND_NAME+"/"+file_name+".lrs");

            // Create file
            if(!file.exists())
                file.createNewFile();

            BufferedWriter buf_writer = new BufferedWriter(new FileWriter(file));

            // Wrie parameters
            buf_writer.write(UICommon.FIRST_LAYER_THICKNESS_PROPERTIES+" = "+new Double(ui_special_specimen.getFirstLayerThickness()*2/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.SECOND_LAYER_THICKNESS_PROPERTIES+" = "+new Double(ui_special_specimen.getSecondLayerThickness()*2/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.THIRD_LAYER_THICKNESS_PROPERTIES+" = "+new Double(ui_special_specimen.getThirdLayerThickness()*2/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write("\n"+UICommon.LAYER_ELEM_SIZE_PROPERTIES+" = "+new Double(ui_special_specimen.getLayerElemSize()*2/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.LAYER_TYPE_PROPERTIES+" = "+new Byte(ui_special_specimen.getLayerType()).toString());
            buf_writer.newLine();
                        
            buf_writer.write(UICommon.LAYER_DIRECTION_PROPERTIES+" = "+new Byte(ui_special_specimen.getLayerDirection()).toString());
            buf_writer.newLine();

            buf_writer.write("\n"+UICommon.FIRST_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_special_specimen.getFirstLayerGrainSize()/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.UPPER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_special_specimen.getUpperSecondLayerGrainSize()/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.LOWER_SECOND_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_special_specimen.getLowerSecondLayerGrainSize()/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.THIRD_LAYER_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_special_specimen.getThirdLayerGrainSize()/cell_size).toString());
            buf_writer.newLine();

            buf_writer.write(UICommon.SUBSTRATE_GRAIN_SIZE_PROPERTIES+" = "+new Double(ui_special_specimen.getSubstrateGrainSize()/cell_size).toString());
            buf_writer.newLine();

            buf_writer.close();
        }
        catch(IOException e)
        {
            System.out.println("IOException: "+e);
        }
    }     
        
}