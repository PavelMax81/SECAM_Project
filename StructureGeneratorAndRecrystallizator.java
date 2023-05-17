/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class StructureGeneratorAndRecrystallizator extends Stage{
    
    Scene scene;
    
    /**
     * This field is intended for show what programm has done
     */

    TextArea done_status;

    /**
     * This field is intended for show user what is writed in textarea
     */

    Label completed, wait_label;

    /**
     * This field is intended for explain to user what's happen in right moment
     */

    Label help_for_button_start;

    /**
     * This field is intended for controll all choosed pathes and parameters
     */

    TransferredDataBank transfer_data_bank;

    /**
     * This field is intended for create grain structure
     */

    CreateGrainStructure create_grain_structure;

    /**
     * This field is intended for recrystallization
     */

    RecrystallizationSimulation recrystallization;
    
    /**
     * This field is intended for create thread which 
     * create recrystallization files
     */

    CreateRecrystallizationFiles create_recryst_files;
    
    /**
     * This field is intended to know what type of actions must be done
     */

    String act;

    /**
     * This field is intended for start actions and close frame after it
     */

    Button start_button, close_button, stop_button, create_files_button,
           paral_start_button;
    
    /** Variable responsible for stochastic determination of new angles of grains */
    boolean determine_grain_angles;
    
    public StructureGeneratorAndRecrystallizator(TransferredDataBank receive_transfer_data_bank, String what_must_do_progress_bar)
    {
        System.out.println("StructureGeneratorAndRecrystallizator: constructor creation start");
        /* remember transferred data bank */
        transfer_data_bank = receive_transfer_data_bank;

        /* Remember type of actions */
        act = what_must_do_progress_bar;

        if(act.equals(UICommon.GRAIN_STRUCTURE_GENERATE_NAME))
            this.setTitle(UICommon.SPECIMEN_GENERATION_FRAME_NAME);
        else
        if(act.equals(UICommon.RECRYSTALLIZATION_CREATION_NAME))
            this.setTitle(UICommon.RECRYSTALLIZATION_FRAME_NAME);
        
        /* create JLabel "Completed:" */
        completed = new Label(UICommon.COMPLETED_HELP_NAME);
        /* create panel with scroll for done status */
        done_status = new TextArea();
        /* create buttons */        
        start_button = new Button(UICommon.BUTTON_START_NAME);
        
        if(act.equals(UICommon.GRAIN_STRUCTURE_GENERATE_NAME))
        {
            System.out.println("ACTION: "+UICommon.GRAIN_STRUCTURE_GENERATE_NAME);           
            done_status.setText("COMMENTS:\n" +
                "Button 'Start': creation of new grain structure;\n"+
                "Button 'Special': assignment of properties of layers;\n"+
                "Button 'Close': choice of existing specimen.\n\n");
        }
        
        close_button = new Button(UICommon.BUTTON_CLOSE_NAME);
        paral_start_button = new Button(UICommon.BUTTON_PARAL_START_NAME);
        create_files_button = new Button(UICommon.BUTTON_CREATE_FILES_NAME);
        
        wait_label = new Label(UICommon.WAIT_HELP_NAME);
        wait_label.setVisible(false);
        
        stop_button = new Button(UICommon.BUTTON_STOP_NAME);
        
        /* If recrystallization frame is creating then
         * button "Create files" must be exist
         */
        if(act.equals(UICommon.RECRYSTALLIZATION_CREATION_NAME))
        {
            /* create buttons */  
            done_status.setText("COMMENTS:\n" +
                "Button 'Paral.Start': saving of existing text files and start of parallel program execution;\n"+
                "Button 'Start': saving of existing text files and start of program execution;\n"+
                "Button 'Create files': creation of text files for program execution;\n"+
                "Button 'Close': saving of existing text files.\n\n");
        }
        
        VBox main_layout = new VBox();
        
        HBox bottom_layout = new HBox();
        bottom_layout.setPadding(new Insets(5, 5, 5, 5));
        bottom_layout.setAlignment(Pos.CENTER);
        
        Separator ver_sep1 = new Separator(Orientation.VERTICAL);
        ver_sep1.setPadding(new Insets(0, 5, 0, 5));
        Separator ver_sep2 = new Separator(Orientation.VERTICAL);
        ver_sep2.setPadding(new Insets(0, 5, 0, 5));
        Separator ver_sep3 = new Separator(Orientation.VERTICAL);
        ver_sep3.setPadding(new Insets(0, 5, 0, 5));
        Separator ver_sep4 = new Separator(Orientation.VERTICAL);
        ver_sep4.setPadding(new Insets(0, 5, 0, 5));
        
        if(act.equals(UICommon.GRAIN_STRUCTURE_GENERATE_NAME)){
            bottom_layout.getChildren().addAll(start_button, ver_sep1, wait_label ,ver_sep2, close_button);
        }
        
        if(act.equals(UICommon.RECRYSTALLIZATION_CREATION_NAME)){
            bottom_layout.getChildren().addAll(start_button, create_files_button, paral_start_button);
        }
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(10, 2, 10, 2));
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(10, 2, 10, 2));
        
        handleEvents();
        done_status.setPadding(new Insets(5, 5, 5, 5));
        done_status.setEditable(false);
        
        main_layout.getChildren().addAll(
                done_status,
                sep2,
                bottom_layout
        );
        
        main_layout.setAlignment(Pos.CENTER);
        
        this.setScene(new Scene(main_layout));
        this.show();
    }
    
    public void handleEvents(){
        start_button.setOnAction(e -> {
            if(act.equals(UICommon.GRAIN_STRUCTURE_GENERATE_NAME)){
                
                System.out.println("StructureGeneratorAndRecrystallizator: method: actionPerformed: button 'start' of grain structure generation frame is pushed");
                
                start_button.setVisible(false);
                close_button.setVisible(false);
                
                transfer_data_bank.set_determine_grain_angles(true);
                
                DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);
                
                create_grain_structure = new CreateGrainStructure(transfer_data_bank, done_status, this);
                wait_label.setVisible(true);
                create_grain_structure.start();
                if(transfer_data_bank.determine_grain_angles())
                    System.out.println("New angles of grains must be determined stochastically!!!");
                else
                    System.out.println("Angles of grains must not be changed!!!");

                act = "";                
            }
            
            if(act.equals(UICommon.RECRYSTALLIZATION_CREATION_NAME)){
                System.out.println("StructureGeneratorAndRecrystallizator: method: actionPerformed: button 'start' of recrystallization frame is pushed");

                start_button.setVisible(false);

                paral_start_button.setVisible(false);
                
                close_button.setVisible(false);

                boolean paral_calc = false;

                recrystallization = new RecrystallizationSimulation(transfer_data_bank, done_status, this, paral_calc);

                wait_label.setVisible(true);
                stop_button.setVisible(true);
                recrystallization.start();
                create_files_button.setVisible(false);
            }
        });
        
        close_button.setOnAction(e -> {
            if(act.equals(UICommon.GRAIN_STRUCTURE_GENERATE_NAME)){
                System.out.println("StructureGeneratorAndRecrystallizator: method: actionPerformed: button 'close' is pushed");
                System.out.println("WARNING!!! Grain structure was not created!!! Old structure is used!!!");
                
                start_button.setVisible(true);
              //  paral_start_button.setVisible(true);

                transfer_data_bank.set_determine_grain_angles(false);

                // Create default parameters for initial,
                // boundary conditions and tasks
                DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);

                create_grain_structure = new CreateGrainStructure(transfer_data_bank, done_status, this);
                close_button.setVisible(false);
                wait_label.setVisible(true);
                create_grain_structure.do_not_run();   
             // dispose(); 

                if(transfer_data_bank.determine_grain_angles())
                  System.out.println("New angles of grains must be determined stochastically!!!");
                else
                  System.out.println("Angles of grains must not be changed!!!");
            }
            System.out.println("StructureGeneratorAndRecrystallizator: method: actionPerformed: button 'close' is pushed");
            act = UICommon.GRAIN_STRUCTURE_GENERATE_NAME;
            this.close();
        });
        
        create_files_button.setOnAction(e -> {
            try{
                System.out.println("StructureGeneratorAndRecrystallizator: method: actionPerformed: button 'Create Files' is pushed");
                determine_grain_angles = transfer_data_bank.determine_grain_angles(); // true;// false;// 

                if(determine_grain_angles)
                  System.out.println("New angles of grains will be determined stochastically!!!");
                else
                  System.out.println("Angles of grains will not be changed!!!");

                // Remove old data ("res" files and another)
                FolderRemover old_files_remover = new FolderRemover(Common.TASK_PATH+"/"+transfer_data_bank.getUIInterface().getTaskPath());

                // Create this folder again
                File folder = new File(Common.TASK_PATH+"/"+transfer_data_bank.getUIInterface().getTaskPath());

                if(!folder.exists())
                    folder.mkdir();

                create_files_button.setVisible(false);
                close_button.setVisible(false);
                create_recryst_files = new CreateRecrystallizationFiles(transfer_data_bank, done_status, this);
                wait_label.setVisible(true);
                stop_button.setVisible(true);
                create_recryst_files.start();
                new Alert(AlertType.ERROR, "Files successfuly created!").showAndWait();
            }
            catch(Exception ex){
                new Alert(AlertType.ERROR, "Failed to create files").showAndWait();
            }
            
        });
        
        paral_start_button.setOnAction(e -> {
            System.out.println("StructureGeneratorAndRecrystallizator: method: actionPerformed: button 'paral.start' of recrystallization frame is pushed");

            start_button.setDisable(true);
            start_button.setVisible(false);
            
            paral_start_button.setVisible(false);
            paral_start_button.setDisable(true);
            
            close_button.setDisable(true);
            close_button.setVisible(false);
            
            boolean paral_calc = true;
            
            recrystallization = new RecrystallizationSimulation(transfer_data_bank, done_status, this, paral_calc);
            
            wait_label.setVisible(true);
            stop_button.setVisible(true);
            recrystallization.start();
            create_files_button.setVisible(false);
        });
    }
    
}
