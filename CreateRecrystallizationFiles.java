/**
 * @(#) CreateRecrystallizationFiles.java version 1.0.0;       September 30 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation recrystallization files
 *
 *=============================================================
 *  Last changes :
 *          19 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.1
 *          Edit: System comments
 */

/** Class for creation recrystallization files
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - September 2009
 */

package interf;

import corecalc.*;
import javafx.scene.control.TextArea;

public class CreateRecrystallizationFiles extends Thread
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
    
    /** Variable that determines whether angles of grains will be determined stochastically */
    boolean determine_grain_angles;

    /**
     * Create Recrystallization files
     * @param receive_transfer_data_bank - transferred data bank
     * @param completed_text_area - field for write completed steps of calculations
     * @param stat - progress status frame
     * @param _determine_grain_angles variable that determines whether angles of grains will be determined stochastically
     */
    public CreateRecrystallizationFiles(TransferredDataBank receive_transfer_data_bank, TextArea completed_text_area, 
                                        StructureGeneratorAndRecrystallizator stat)
    {
        System.out.println("CreateRecrystallizationFiles: constructor creation start");
        
      //  determine_grain_angles = _determine_grain_angles;
        
        // remember transferred data bank
        transfer_data_bank = receive_transfer_data_bank;

        determine_grain_angles = transfer_data_bank.determine_grain_angles();

        // Remember frame of progress status
        status = stat;

        // Remember  field in what comleted steps will be written
        completed_steps = completed_text_area;
    }

    @Override
    /* This thread creates necessary files.
     */
    public void run()
    {
        System.out.println("CreateRecrystallizationFiles: method: run: start");
        
        System.out.println("CreateRecrystallizationFiles: determine_grain_angles = "+determine_grain_angles);
        
        FileCreationHCP fileCreation = new FileCreationHCP(completed_steps, determine_grain_angles);
        
        completed_steps.setText(completed_steps.getText()+"\nCREATION OF FILES IS FINISHED!!!");
        
        try
        {
            sleep(3000);
        }
        catch(InterruptedException e)
        {
            System.out.println("InterruptedException: "+e);
        }
        
        status.wait_label.setVisible(false);
        status.stop_button.setVisible(false);
        status.close_button.setVisible(true);
        status.close_button.setDisable(false);
        status.start_button.setVisible(true);
        status.create_grain_structure = null;
    }
}