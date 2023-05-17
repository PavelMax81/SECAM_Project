/**
 * @(#) RecrystallizationSimulation.java version 1.0.0;       September 7 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for recrystallization simulation
 *
 *=============================================================
 *  Last changes :
 *          26 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.4
 *          Edit: If in frame with task list we choose check box "show
 *          grain bounds" then GrainsHCPMain is not activated
 *          because this checkbox is not fir it
 */

/** Class for recrystallization simulation
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - September 2009
 */

package interf;

import corecalc.*;
import java.util.*;
import javafx.scene.control.TextArea;
import paral_calc.*;

public class RecrystallizationSimulation extends Thread
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
    
    private boolean paral_calc;

    /**
     * Recrystallization
     * @param receive_transfer_data_bank - transferred data bank
     * @param completed_text_area - field for write completed steps of calculations
     * @param stat - progress status frame
     */

 public RecrystallizationSimulation(TransferredDataBank receive_transfer_data_bank, TextArea completed_text_area, 
                                    StructureGeneratorAndRecrystallizator stat, boolean _paral_calc)
 {
     System.out.println("RecrystallizationSimulation: constructor creation start");

     /*
      * remember transferred data bank
      */

     transfer_data_bank = receive_transfer_data_bank;

     /*
      * Remember frame of progress status
      */

     status = stat;

     /*
      * Remember  field in what comleted steps will be written
      */

     completed_steps = completed_text_area;
     
     paral_calc = _paral_calc;
 }

    @Override
    /*
     * What this thread must do
     */

 public void run()
 {
        System.out.println("RecrystallizationSimulation: method: run: start");

 /*    if(transfer_data_bank.getUITask().getShowGrainBounds())
     {
        // Creation of grain structure

        completed_steps.setText(completed_steps.getText()+"CREATION OF GRAIN STRUCTURE IS STARTED!!!");
        completed_steps.setText(completed_steps.getText()+"\n");

        GrainsHCPMain grMain = new GrainsHCPMain(completed_steps);

        completed_steps.setText(completed_steps.getText()+"\nCREATION OF GRAIN STRUCTURE IS FINISHED!!!\n");

        // Creation of files necessary for simulation of recrystallization
        completed_steps.setText(completed_steps.getText()+"\nCREATION OF FILES IS STARTED!!!");
     }*/
     
     // Simulation of recrystallization
     completed_steps.setText(completed_steps.getText()+"\nRECRYSTALLIZATION SIMULATION IS STARTED!!!");
     
     Date start_date = new Date();
     
     RecrystallizationCA defCA;
     Paral_Calc_SECAM paral_calc_SECAM;
     
     if(!paral_calc)
       defCA = new RecrystallizationCA(completed_steps);
     else
     {
       String[] task_name = new String[1];
       task_name[0] = transfer_data_bank.getUIInterface().getTaskPath();
       
       System.out.println("Task '"+task_name[0]+"' is started in parallel code!!!");
       
       paral_calc_SECAM = new Paral_Calc_SECAM(task_name);
     }
     
     Date finish_date = new Date();

     long time_period = finish_date.getTime() - start_date.getTime();
     completed_steps.setText(completed_steps.getText()+"\nThe period of execution of the program: " + time_period + " ms.");

     completed_steps.setText(completed_steps.getText()+"\nRECRYSTALLIZATION SIMULATION IS FINISHED!!!");
     
     try
     {
        sleep(3000);
     }
     catch(InterruptedException e)
     {}

     status.wait_label.setVisible(false);
     status.stop_button.setVisible(false);
     status.close_button.setVisible(true);
     status.close_button.setDisable(false);
     status.create_grain_structure = null;
 }
}
