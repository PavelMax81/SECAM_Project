/**
 * @(#) CreateGrainStructure.java version 1.0.0;       September 7 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation grain strucutre with help of threads
 *
 *=============================================================
 *  Last changes :
 *          19 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.3
 *          Edit: System comments
 */

/** Class for creation grain strucutre with help of threads
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - September 2009
 */

package interf;

import corecalc.*;
import javafx.scene.control.TextArea;
import util.*;

public class CreateGrainStructure extends Thread
{
    /**
     * This field is intended for controll all choosed pathes and parameters
     */

    TransferredDataBank transfer_data_bank;
    
    /**
     * This field is intended for write all completed steps in progress frame
     */
    
    TextArea completed_steps;

    /**
     * This field is intended for dispose progress frame after finish
     * all calculations
     */

    StructureGeneratorAndRecrystallizator status;
    
    /**
     * This field is intended for create controll grain structure file
     */

    ControllGrainStructureCreation controll_file_creator;
        
    /**
     * Create Grain Structure
     * @param receive_transfer_data_bank - transferred data bank
     * @param completed_text_area - field for write completed steps of calculations
     * @param stat - progress status frame
     */
    
    public CreateGrainStructure(TransferredDataBank receive_transfer_data_bank, TextArea completed_text_area, StructureGeneratorAndRecrystallizator stat)
    {
        System.out.println("CreateGrainStructure: constructor creation start");

        /*
         * remember transferred data bank
         */

        transfer_data_bank = receive_transfer_data_bank;

        /*
         * Remember frame of progress status
         */

        status = stat;

        /*
         * Remember field in what comleted steps will be written
         */

        completed_steps = completed_text_area;

        /*
         * Set priority of grain structure creation
         * Чем меньше значение приоритета, тем больше оперативки
         * отводится для действий в интерфейсе
         */

        setPriority(4);
    }

    @Override    
    /*
     * What this thread must do
     */
    public void run()
    {
        System.out.println("CreateGrainStructure: method: run: start");

        /*
         * Set default parameter values of initial, boundary conditions and also tasks
         */

         DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);

         // Creation of grain structure

         completed_steps.setText(completed_steps.getText()+UICommon.GRAIN_STRUCTURE_CREATION_START_NAME);
         completed_steps.setText(completed_steps.getText()+"\n");

         GrainsHCPMain grMain = new GrainsHCPMain(completed_steps);
         System.out.println("New grain structure is created!!!");

         completed_steps.setText(completed_steps.getText()+"\n"+UICommon.GRAIN_STRUCTURE_CREATION_FINISH_NAME+"\n");
         try
         {
            sleep(1500);

            controll_file_creator = new ControllGrainStructureCreation(transfer_data_bank.ui_interface.getSpecimenPath());
            sleep(1500);
         }
         catch(InterruptedException e)
         {}

         status.wait_label.setVisible(false);
         status.close_button.setVisible(true);
         status.close_button.setDisable(false);
         status.create_grain_structure = null;
         
         transfer_data_bank.set_determine_grain_angles(true);
    }

    public void do_not_run()
    {
        System.out.println("WARNING!!! Grain structure was not created!!! Old structure is used!!!");
        completed_steps.setText(completed_steps.getText()+"WARNING!!! Grain structure was not created!!! Old structure is used!!!");

        // Set default parameter values of initial, boundary conditions and also tasks
        DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);

         try
         {
            sleep(1500);
            controll_file_creator = new ControllGrainStructureCreation(transfer_data_bank.ui_interface.getSpecimenPath());
            sleep(1500);
         }
         catch(InterruptedException e)
         {}

         status.wait_label.setVisible(false);
         status.close_button.setVisible(true);
         status.close_button.setDisable(false);
         status.create_grain_structure = null;
         
         transfer_data_bank.set_determine_grain_angles(false);
    }
}
