/*
 * @(#) RecCATasksParams.java version 1.0.0;       April 21 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for creation a frame of corresponding task parameters
 * 
 *=============================================================
 *  Last changes :
 *          27 December 2010 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.2.5
 *          Edit: Add CheckBox for ignore or not ignore limit
 *          on time step value
 */

/** Class for creation a frame of corresponding task parameters
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - April 2009
 */

package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Common;

public class RecCATasksParams extends Stage
{
    Scene scene;
    VBox root;
    
    /**
     * This field is intended for creation helps for users what to input,
     * or if parameters loading from file - show what it is
     */
    
    Label help_task_type,help_task_parameters,help_time_step,help_total_time,
            help_output_file, help_response_rate, help_comments;
    
    /**
     * This field is intended for creation a textfields for inputing or showing
     * parameters of corresponding task
     */
    
    TextField time_step_text_field ,total_time_text_field ,
            output_file_text_field, response_rate;

    /**
     * This field is intended for input comments for each task.
     * Every time, when we want to remember conditions and
     * for what aims task was created we can simply watch commets
     */

    TextArea comments_text_area;

    /**
     * This field is intended for creating buttons "Save" and "Save as" for
     * saving option
     */
    
    Button button_save, button_ok, button_cancel;
    
    /** This field is intended for check types of task
     */    
    CheckBox heat_transfer_task_type, recrystallization_task_type,
            mechanical_loading_task_type, crack_formation_task_type,
            equilibrium_state_task_type, show_grain_bounds_task_type;
    
    /**
     * This field is intended for creation default bank of task's data,
     * or for creation bank of task's data by loading from
     * corresponding file
     */
    UITask ui_task;

    /**
     * This field is intended for input file name of saving params
     */

    TextField textfield_for_file_location;

    /**
     * This field is intended for control options of saving frame
     */

    int save_option;

    /**
     * This field is intended for save all params
     */

    SaveParams saving;

    /**
     * This field is intended for use fields of transferred data bank
     */

    TransferredDataBank transfer_data_bank;

    /**
     * This field is intended for remember choosed specimen
     */

    String specimen_name ;

    /**
     *  This field is intended for remember specimen data bank
     */

    UISpecimen ui_specimen;
    
    /**
     * This field is intended for remember number of rows in specimen table
     * for knowing number of used materials
     */

    int specimen_table_rows_num;

    /**
     * This fields is intended for remember all recomended values of
     * time step
     */

    double recomended_heat_transfer_time_step[];
    double recomended_mech_loading_time_step;
    double recomended_recrysatllization_time_step[];

    /**
     * This field is intended for remember material data bank which used
     * for recomended value of time step creation
     */

    UIMaterial ui_material;

    /**
     * This field is intended for remember boundary condition data bank
     * which used for recomended value of time step creation
     */

    UIBoundaryCondition ui_boundary;

    /**
     * This field is intended for remember initial condition data bank
     * which used for recomended value of time step creation
     */

    UIInitialCondition ui_initial;

    /**
     * This field is intended for remember min and max temperatures
     * which used for recomended value of time step creation
     */

    double min_temp, max_temp;
    
    /**
     * This field is intended for show to user maxumum values of
     * tome step for corresponding type of task
     */

    Label max_heat_time_step_help, max_recrystallization_time_step_help,
            max_mechanical_loading_time_step_help, max_time_step_help;
    
    /**
     * This field is intended for remember maximum values
     * for all task types (with it combinations) and
     * also general maximum value
     */

    double max_heat_time_step_value, max_recrystallization_time_step_value,
            max_mechanical_loading_time_step_value, max_time_step_value,
            max_heat_vs_mech_time_step_value, max_heat_vs_recryst_time_step_value,
            max_recryst_vs_mech_time_step_value, max_heat_vs_recryst_vs_mech_time_step_value;

    /**
     * This field is intended for remember all uses materials, which
     * was choosed for specimen and boundaries
     */

    String [] uses_materials;

    /** This field is intended for choosing calculation or not of
     * heat expansion and force moments
     */
    CheckBox calc_heat_expansion, calc_force_moments;
    
    CheckBox defects_influence_task_type;

    RadioButton first_boundary_type_radio_button, second_boundary_type_radio_button,
                third_boundary_type_radio_button, fourth_boundary_type_radio_button,
                fifth_boundary_type_radio_button;

    ToggleGroup boundary_type_button_group;
    
    /**
     * This field is intended for ignore limit on time step
     */

    CheckBox ignore_time_step_limit;

    /**
     * This constructor create frame with parameters of task
     * @param receive_transfer_data_bank - transferred data bank
     * @param list - task list
     */
    
    public RecCATasksParams(TransferredDataBank receive_transfer_data_bank)
    {
        System.out.println("RecCATaskParams: start constructor creation");
        
        root = new VBox();
        scene = new Scene(root);
        
        /*
         * Remember transferred data bank
         */

        transfer_data_bank = receive_transfer_data_bank;
        
        /*
         * Create bank of task's data
         */

        ui_task = transfer_data_bank.getUITask();

        /** Create helps for user */        
        createHelpsForUsersInParamsOfTasks();

        /** Create textfields for showing or inputing value of parameters  */                
        createTextFieldsForParamsOfTasks();

        /** Create buttons */        
        createButtonsForParamsOfTasks();

        /** Create types of task */        
        createTypesOfTask();

        // Create labels for show to user maximum values of time step
        // for corresponding task and also create label with
        // generall value of max time step
        createMaxTimeStepLabels();

        // Remember all different uses materials
        getUseMaterials();

        // Calculate max time step values for
        // each task type and it combinations
        getMaxTimeStepValues();
        
        // Check all choosed checkboxes (when we load existed task
        // it must be done)
        checkTaskTypes();

        createBoundaryTypeChoosing();

        /*
         * Add elements on frame
         */
        
        addElementsForParametersOfTasks();       
        handleEvent();
        this.button_save.setDisable(false);
        
        this.setTitle("Task Parameters");
        this.setScene(scene); 
        
        textfield_for_file_location = new TextField();
    }
    
    /*
     * This method is responsible for actions of each button.
     * @param w - pushed button
     */
    public void handleEvent(){
        button_save.setOnAction(e -> {
            System.out.println("RecCATaskParams: method: actionPerformed: button 'save' is pushed");
            if(isParametersInput())
            {
                System.out.println("Correct!!!");
                inputParams();
                savingDialofOfParamsForTasks();
            }
            checkTaskTypes();
        });
        button_ok.setOnAction(e -> {
            System.out.println("RecCATaskParams: method: actionPerformed: button 'ok' is pushed");
            
            if(isParametersInput())
            {
            inputParams();
            saving = new SaveParams(ui_task,transfer_data_bank.getUIInterface().getTaskPath(), transfer_data_bank.getUIInterface(), transfer_data_bank.getUISpecimen(),
                    transfer_data_bank.getUIBoundaryCondition(), transfer_data_bank.getUIInterface().getTankPath());
            }
            checkTaskTypes();
        });
    }
    
    /*
     * Create radio buttons for choosing boundary type,
     * it can be:
     * homogeneous mechanical loading and homogeneous heating,
     * mechanical loading by indentor and homogeneous heating,
     * homogeneous mechanical loading of facets and heating of circle area,
     * mechanical bending and homogeneous heating,
     * mechanical shear and homogeneous heating. 
     */

    public void createBoundaryTypeChoosing()
    {
        boundary_type_button_group = new ToggleGroup();

        first_boundary_type_radio_button = new RadioButton("homogeneous mechanical loading and homogeneous heating");
        second_boundary_type_radio_button = new RadioButton("mechanical loading by indentor and homogeneous heating");
        third_boundary_type_radio_button = new RadioButton("homogeneous mechanical loading of facets and heating of circle area");
        fourth_boundary_type_radio_button = new RadioButton("mechanical bending and homogeneous heating");
        fifth_boundary_type_radio_button = new RadioButton("mechanical shear and homogeneous heating");
        
        ToggleGroup group = new ToggleGroup();
        
        group.getToggles().addAll(
                first_boundary_type_radio_button,
                second_boundary_type_radio_button,
                third_boundary_type_radio_button,
                fourth_boundary_type_radio_button,
                fifth_boundary_type_radio_button
        );
        
        if(ui_task.getBoundaryType()==0)
            first_boundary_type_radio_button.setSelected(true);
        if(ui_task.getBoundaryType()==1)
            second_boundary_type_radio_button.setSelected(true);
        if(ui_task.getBoundaryType()==2)
            third_boundary_type_radio_button.setSelected(true);
        if(ui_task.getBoundaryType()==3)
            fourth_boundary_type_radio_button.setSelected(true);
        if(ui_task.getBoundaryType()==4)
            fifth_boundary_type_radio_button.setSelected(true);
    }
    
    /*
     * Check all task type check boxes and
     * if some check box is choosed then it show
     * corresponding label with max value of 
     * time step of corresponding task type 
     */

    public void checkTaskTypes()
    {
        System.out.println("RecCATaskParams: method: checkTaskTypes: start");

        // If "heat transfer" tas ktype was choosed
        if(heat_transfer_task_type.isSelected())
            // Set label with max value of time step with
            // this task type visible
            max_heat_time_step_help.setVisible(true);
        else
            // Set label with max value of time step with
            // this task type not visible
            max_heat_time_step_help.setVisible(false);

        // If "recrystallization" tas ktype was choosed
        if(recrystallization_task_type.isSelected())
            // Set label with max value of time step with
            // this task type visible
            max_recrystallization_time_step_help.setVisible(true);
        else
            // Set label with max value of time step with
            // this task type not visible
            max_recrystallization_time_step_help.setVisible(false);

        // If "mechanical loading" tas ktype was choosed
        if(mechanical_loading_task_type.isSelected())
            // Set label with max value of time step with
            // this task type visible
            max_mechanical_loading_time_step_help.setVisible(true);
        else
            // Set label with max value of time step with
            // this task type not visible
            max_mechanical_loading_time_step_help.setVisible(false);
        
        
        heat_transfer_task_type.setOnAction(e -> {
            // If only "heat transfer" task type is choosed
            if(heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                        !mechanical_loading_task_type.isSelected())
            max_time_step_value = max_heat_time_step_value;
            
            // If only "mechanical loading" task type is choosed
            else if(!heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_mechanical_loading_time_step_value;
            // There is no formula for this situation, so max time step may be each double value
                max_time_step_value = 1000000;
            
            // If only "recrystallization" task type is choosed
            if(!heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            !mechanical_loading_task_type.isSelected())
                max_time_step_value = max_recrystallization_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer" and "recrystallization"
            else if(heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            !mechanical_loading_task_type.isSelected())
                max_time_step_value = max_heat_vs_recryst_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer" and "mechanical loading"
            else if(heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_heat_vs_mech_time_step_value;
            // there is no formula for mech max time step
                    max_time_step_value = max_heat_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "recrystallization" and "mechanical loading"
            if(!heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_recryst_vs_mech_time_step_value;
            // there is no formula for mech max time step
                max_time_step_value = max_recrystallization_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer"
            // and "mechanical loading" and "recrystallization"
            if(heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_heat_vs_recryst_vs_mech_time_step_value;
            // there is no formula for mech max time step
                max_time_step_value = max_heat_vs_recryst_time_step_value;
            
            System.out.println("Max Time Step = "  + max_time_step_value);
            max_time_step_help.setText("max time step = " + max_time_step_value);
        });
        
        mechanical_loading_task_type.setOnAction(e -> {
            // If only "heat transfer" task type is choosed
            if(heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                        !mechanical_loading_task_type.isSelected())
            max_time_step_value = max_heat_time_step_value;
            
            // If only "mechanical loading" task type is choosed
            else if(!heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_mechanical_loading_time_step_value;
            // There is no formula for this situation, so max time step may be each double value
                max_time_step_value = 1000000;
            
            // If only "recrystallization" task type is choosed
            if(!heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            !mechanical_loading_task_type.isSelected())
                max_time_step_value = max_recrystallization_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer" and "recrystallization"
            else if(heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            !mechanical_loading_task_type.isSelected())
                max_time_step_value = max_heat_vs_recryst_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer" and "mechanical loading"
            else if(heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_heat_vs_mech_time_step_value;
            // there is no formula for mech max time step
                    max_time_step_value = max_heat_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "recrystallization" and "mechanical loading"
            if(!heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_recryst_vs_mech_time_step_value;
            // there is no formula for mech max time step
                max_time_step_value = max_recrystallization_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer"
            // and "mechanical loading" and "recrystallization"
            if(heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_heat_vs_recryst_vs_mech_time_step_value;
            // there is no formula for mech max time step
                max_time_step_value = max_heat_vs_recryst_time_step_value;
            
            System.out.println("Max Time Step = "  + max_time_step_value);
            max_time_step_help.setText("max time step = " + max_time_step_value);
        });
        
        recrystallization_task_type.setOnAction(e -> {
            // If only "heat transfer" task type is choosed
            if(heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                        !mechanical_loading_task_type.isSelected())
            max_time_step_value = max_heat_time_step_value;
            
            // If only "mechanical loading" task type is choosed
            else if(!heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_mechanical_loading_time_step_value;
            // There is no formula for this situation, so max time step may be each double value
                max_time_step_value = 1000000;
            
            // If only "recrystallization" task type is choosed
            if(!heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            !mechanical_loading_task_type.isSelected())
                max_time_step_value = max_recrystallization_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer" and "recrystallization"
            else if(heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            !mechanical_loading_task_type.isSelected())
                max_time_step_value = max_heat_vs_recryst_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer" and "mechanical loading"
            else if(heat_transfer_task_type.isSelected()&!recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_heat_vs_mech_time_step_value;
            // there is no formula for mech max time step
                    max_time_step_value = max_heat_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "recrystallization" and "mechanical loading"
            if(!heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_recryst_vs_mech_time_step_value;
            // there is no formula for mech max time step
                max_time_step_value = max_recrystallization_time_step_value;
            
            // Set corresponding value of general max time step value
            // for combination of task types: "heat transfer"
            // and "mechanical loading" and "recrystallization"
            if(heat_transfer_task_type.isSelected()&recrystallization_task_type.isSelected()&
                            mechanical_loading_task_type.isSelected())
            //            max_time_step_value = max_heat_vs_recryst_vs_mech_time_step_value;
            // there is no formula for mech max time step
                max_time_step_value = max_heat_vs_recryst_time_step_value;
            
            System.out.println("Max Time Step = "  + max_time_step_value);
            max_time_step_help.setText("max time step = " + max_time_step_value);
        });
    }
    
    /*
     * Create helps for users what to input
     * or if parameters are loading from file - show what it is
     */
    
    public void createHelpsForUsersInParamsOfTasks()
    {
        System.out.println("RecCATaskParams: method: createHelpsForUsersInParamsOfTasks: start");

        help_time_step = new Label(UICommon.TIME_STEP_NAME);
        help_total_time = new Label(UICommon.TOTAL_TIME_NAME);
        help_output_file = new Label(UICommon.OUTPUT_FILE_NAME);
        help_task_type = new Label(UICommon.TASK_TYPE_NAME);
        help_task_parameters = new Label(UICommon.TASK_PARAMETERS_NAME);
        help_response_rate = new Label(UICommon.TASK_RESPONSE_RATE_NAME);
        help_comments = new Label(UICommon.TASK_COMMENTS_NAME);        
    }
    
    /** Create textfields for input or showing parameters of corresponding help
     */    
    public void createTextFieldsForParamsOfTasks()
    {
        System.out.println("RecCATaskParams: method: createTextFieldsForParamsOfTasks: start");

        time_step_text_field = new TextField();
        time_step_text_field.setText((new Double(ui_task.getTimeStep())).toString());
        
        // CREATE "ignore" CHECKBOX
        ignore_time_step_limit = new CheckBox("Ignore time step limits");
        
        total_time_text_field = new TextField();
        total_time_text_field.setText((new Double(ui_task.getTotalTime())).toString());
        
        output_file_text_field = new TextField();
        output_file_text_field.setText((new Long(ui_task.getOutputFile())).toString());
        
        response_rate = new TextField();
        response_rate.setText((new Double(ui_task.getResponseRate())).toString());
        
        comments_text_area = new TextArea(ui_task.getTaskComments().getText());        
    }
    
    /**  Create buttons: "Save as" with "OK" or "Cancel"
     */    
    public void createButtonsForParamsOfTasks()
    {
        System.out.println("RecCATaskParams: method: createButtonsForParamsOfTasks: start");

        button_save = new Button(UICommon.BUTTON_SAVE_NAME);

        button_ok = new Button(UICommon.BUTTON_OK_NAME);
        
        button_cancel = new Button(UICommon.BUTTON_CANCEL_NAME);
        
    }
    
    /*
     * Add elements on frame
     */
    
    public void addElementsForParametersOfTasks()
    {
        System.out.println("RecCATaskParams: method: addElementsForParametersOfTasks: start");
        
        GridPane layout = new GridPane();
        layout.setHgap(7.0d);
        layout.setVgap(7.0d);
        layout.setAlignment(Pos.CENTER);

        Separator sep2 = new Separator();
        Separator sep3 = new Separator();

        GridPane.setConstraints(help_task_type, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);

        GridPane.setConstraints(heat_transfer_task_type, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(recrystallization_task_type, 0, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(mechanical_loading_task_type, 0, 3, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(crack_formation_task_type, 0, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(equilibrium_state_task_type, 0, 5, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(show_grain_bounds_task_type, 0, 6, 1, 1, HPos.LEFT, VPos.CENTER);

        GridPane.setConstraints(help_response_rate, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(response_rate, 2, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(help_comments, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(comments_text_area, 2, 1, 1, 6, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 0, 7, 3, 1);

        GridPane.setConstraints(help_task_parameters, 0, 8, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(help_time_step, 0, 9, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(help_total_time, 0, 10, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(help_output_file, 0, 11, 1, 1, HPos.LEFT, VPos.CENTER);

        GridPane.setConstraints(time_step_text_field, 1, 9, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(max_time_step_help, 2, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(total_time_text_field, 1, 10, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(output_file_text_field, 1, 11, 1, 1, HPos.LEFT, VPos.CENTER);

        GridPane.setConstraints(sep3, 0, 12, 3, 1);

        GridPane.setConstraints(calc_heat_expansion, 0, 13, 1, 1);
        GridPane.setConstraints(calc_force_moments, 1, 13, 1, 1);
        GridPane.setConstraints(defects_influence_task_type, 2, 13, 1, 1);

        GridPane.setConstraints(first_boundary_type_radio_button, 0, 14, 3, 1);
        GridPane.setConstraints(second_boundary_type_radio_button, 0, 15, 3, 1);
        GridPane.setConstraints(third_boundary_type_radio_button, 0, 16, 3, 1);
        GridPane.setConstraints(fourth_boundary_type_radio_button, 0, 17, 3, 1);
        GridPane.setConstraints(fifth_boundary_type_radio_button, 0, 18, 3, 1);
        
        layout.getChildren().addAll(
                max_time_step_help,
                help_task_type, heat_transfer_task_type, recrystallization_task_type,
                mechanical_loading_task_type, crack_formation_task_type, equilibrium_state_task_type,
                show_grain_bounds_task_type, help_response_rate, response_rate, help_comments,
                comments_text_area, sep2, help_task_parameters, help_time_step, help_total_time,
                help_output_file, time_step_text_field, total_time_text_field, output_file_text_field,
                sep3, calc_heat_expansion, calc_force_moments, defects_influence_task_type,
                first_boundary_type_radio_button, second_boundary_type_radio_button,
                third_boundary_type_radio_button, fourth_boundary_type_radio_button, 
                fifth_boundary_type_radio_button
        );
        
        Separator ver_sep_1 = new Separator(Orientation.VERTICAL);
        ver_sep_1.setPadding(new Insets(0, 5, 0, 5));
        Separator ver_sep_2 = new Separator(Orientation.VERTICAL);
        ver_sep_2.setPadding(new Insets(0, 5, 0, 5));
        HBox bottom_layout = new HBox(button_save, ver_sep_1, button_cancel);
        button_save.setPadding(new Insets(5, 30, 5, 30));
        button_cancel.setPadding(new Insets(5, 30, 5, 30));
        bottom_layout.setAlignment(Pos.CENTER);        
        root.getChildren().addAll(
                layout,
                bottom_layout
        );
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20,20,20,20));
    }
    
    /*
     *  Create dialog which used to choose name of saved file with all data
     */
    
    public void savingDialofOfParamsForTasks()
    {
        System.out.println("RecCATaskParams: method: savingDialofOfParamsForTasks: start");

        /*
         * Create JTextField for input location of saving data
         */
        this.close();
        SavingDialog save = new SavingDialog(textfield_for_file_location);
        
        save.save_btn.setOnAction(e -> {
            if(isNameCorrect(textfield_for_file_location))
            {
                try{
                    transfer_data_bank.getUIInterface().setTaskPath(textfield_for_file_location.getText());
                    saving = new SaveParams(ui_task,textfield_for_file_location.getText(),
                            transfer_data_bank.getUIInterface(), transfer_data_bank.getUISpecimen(),
                            transfer_data_bank.getUIBoundaryCondition(),
                            transfer_data_bank.getUIInterface().getTankPath());
                    new Alert(AlertType.INFORMATION, "File successfuly created").showAndWait();
                }
                catch(Exception ex){
                    new Alert(AlertType.ERROR, "Failed to create file").showAndWait();
                }
            }
            save.close();
        });
    }
    
    /*
     * Create types of task, which can be choosed
     */

    public void createTypesOfTask()
    {
        System.out.println("RecCATaskParams: method: createTypesOfTask: start");

        heat_transfer_task_type = new CheckBox(UICommon.HEAT_TRANSFER_NAME);
        heat_transfer_task_type.setSelected(ui_task.getHeatTransfer());
        recrystallization_task_type = new CheckBox(UICommon.RECRYSTALLIZATION_NAME);
        recrystallization_task_type.setSelected(ui_task.getRecrystallization());
        mechanical_loading_task_type = new CheckBox(UICommon.MECHANICAL_ELATING_NAME);
        mechanical_loading_task_type.setSelected(ui_task.getMechanicalElating());
        crack_formation_task_type = new CheckBox(UICommon.CRACK_FORMATION_NAME);
        crack_formation_task_type.setSelected(ui_task.getCrackFormation());
        equilibrium_state_task_type = new CheckBox(UICommon.EQUILIBRIUM_STATE_NAME);
        equilibrium_state_task_type.setSelected(ui_task.getEquilibriumState());
        show_grain_bounds_task_type = new CheckBox(UICommon.SHOW_GRAIN_BOUNDS_NAME);
        show_grain_bounds_task_type.setSelected(ui_task.getShowGrainBounds());
        calc_heat_expansion = new CheckBox(UICommon.CALC_HEAT_EXPANSION_NAME);
        calc_heat_expansion.setSelected(ui_task.getCalcHeatExpansion());
        calc_force_moments = new CheckBox(UICommon.CALC_FORCE_MOMENTS_NAME);
        calc_force_moments.setSelected(ui_task.getCalcForceMoments());
        defects_influence_task_type = new CheckBox(UICommon.DEFECTS_INFLUENCE_NAME);
        defects_influence_task_type.setSelected(ui_task.getDefectsInfluence());
        
        
    }

    /*
     * Remember inputed task parameters in UITask
     */

    public void inputParams()
    {
        System.out.println("RecCATaskParams: method: inputParams: start");

        /** set input task types
         */
        ui_task.setHeatTransfer(Boolean.valueOf(heat_transfer_task_type.isSelected()));
        ui_task.setRecrystallization(Boolean.valueOf(recrystallization_task_type.isSelected()));
        ui_task.setMechanicalElating(Boolean.valueOf(mechanical_loading_task_type.isSelected()));
        ui_task.setCrackFormation(Boolean.valueOf(crack_formation_task_type.isSelected()));
        ui_task.setEquilibriumSate(Boolean.valueOf(equilibrium_state_task_type.isSelected()));
        ui_task.setShowGrainBounds(Boolean.valueOf(show_grain_bounds_task_type.isSelected()));
        ui_task.setCalcForceMoments(Boolean.valueOf(calc_force_moments.isSelected()));
        ui_task.setCalcHeatExpansion(Boolean.valueOf(calc_heat_expansion.isSelected()));
        
        ui_task.setDefectsInfluence(Boolean.valueOf(defects_influence_task_type.isSelected()));

        /** set input task parameters
         */
        ui_task.setTimeStep(Double.valueOf(time_step_text_field.getText()));
        ui_task.setTotalTime(Double.valueOf(total_time_text_field.getText()));
        ui_task.setOutputFile((Double.valueOf(output_file_text_field.getText())).longValue());
        ui_task.setResponseRate(Double.valueOf(response_rate.getText()));
        ui_task.setTaskComments(comments_text_area);

        /*
         * Set inputed boundary type
         */

        if(first_boundary_type_radio_button.isSelected())
            ui_task.setBoundaryType(0);
        if(second_boundary_type_radio_button.isSelected())
            ui_task.setBoundaryType(1);
        if(third_boundary_type_radio_button.isSelected())
            ui_task.setBoundaryType(2);
        if(fourth_boundary_type_radio_button.isSelected())
            ui_task.setBoundaryType(3);
        if(fifth_boundary_type_radio_button.isSelected())
            ui_task.setBoundaryType(4);
    }

    /*
     * Controll - input parameters or not,
     * and if they input - right form they have or not
     * @return - right form they have or not 
     */

    public boolean isParametersInput()
    {
        System.out.println("RecCATaskParams: method: isParametersInput: start");

        try
        {
            Double.parseDouble(time_step_text_field.getText().toString());
            Double.parseDouble(total_time_text_field.getText().toString());
            Double.parseDouble(output_file_text_field.getText().toString());
            Double.parseDouble(response_rate.getText().toString());

            if(!ignore_time_step_limit.isSelected())
            {
                if(Double.valueOf(time_step_text_field.getText()).doubleValue()>max_time_step_value)
                {
                    new Alert(AlertType.ERROR, UICommon.ERROR_ANOTHER_VALUE_OF_TIME_STEP_EXCEPTION_NAME).show();                    
                    return false;
                }
            }
            if(Double.valueOf(time_step_text_field.getText()).doubleValue()>Double.valueOf(total_time_text_field.getText()).doubleValue())
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_TOTAL_TIME_SMALLER_TIME_STEP_EXCEPTION_NAME).show();
                return false;
            }

            if(!first_boundary_type_radio_button.isSelected()&!second_boundary_type_radio_button.isSelected()
                    &!third_boundary_type_radio_button.isSelected()&!fourth_boundary_type_radio_button.isSelected()
                    &!fifth_boundary_type_radio_button.isSelected())
            {
                new Alert(AlertType.ERROR, "You must choose some boundary type!").show();
                return false;
            }

            return true;
        }
         catch (NumberFormatException e)
        {
            new Alert(AlertType.ERROR, "Incorrect Data Input").showAndWait();
        }
        return false;
    }

    /*
     * Controll inputed name
     * @param users_file_name - what name was inputed
     * @return - can be created file with inputed name or not
     */

    public boolean isNameCorrect(TextField users_file_name)
    {
        System.out.println("RecCATaskParams: method: isNameCorrect: start");

        boolean choice = false;

        String file_name = users_file_name.getText();

        if(file_name.isEmpty())
        {
            new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }

        if(file_name.equals(UICommon.TASK_PARAM_NAME))
        {
            new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }

        if(file_name.length()>4)
            if(file_name.substring(file_name.length()-4,file_name.length()).equals(UICommon.END_REC_NAME))
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

        if(file_name.length()>5)
            if(file_name.substring(file_name.length()-5,file_name.length()).equals(UICommon.END_GEOM_NAME))
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

        if(file_name.length()>7)
            if(file_name.substring(file_name.length()-7,file_name.length()).equals(UICommon.END_GRAINS_NAME))
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

      return true;
    }

    /*
     * Calculate max values of time step for each
     * task type and it combinations
     */

    public void getMaxTimeStepValues()
    {
        System.out.println("RecCATaskParams: method: getMaxTimeStepValues: start");

        /*
         * Remember choosed specimen
         */

         specimen_name = transfer_data_bank.getUIInterface().getSpecimenPath();
            
        /*
         * Remember specimen data bank 
         */

         ui_specimen = new UISpecimen(Common.SPEC_PATH+"/"+specimen_name+"/"+specimen_name+".");

        /*
         * Remember specimen table rows number (number of used materials)
         */

         specimen_table_rows_num = transfer_data_bank.getUISpecimen().materials.size();
//System.out.println("////Task Params first spec rows num = "+specimen_table_rows_num);
        /*
         * Create fields for remember values of time step for all
         * types of task
         */

         recomended_heat_transfer_time_step = new double[specimen_table_rows_num];
//         recomended_mech_loading_time_step = 0;
         recomended_recrysatllization_time_step = new double[specimen_table_rows_num];

         // Calculate max time step value for only
         // "heat transfer" task type       

         // Calculate all possible values
         for(int i = 0; i < specimen_table_rows_num; i++)
         {
             ui_material = new UIMaterial(Common.MATERIALS_PATH+"/"+transfer_data_bank.getUISpecimen().materials.get(i)+"."+Common.MATERIAL_EXTENSION);

             recomended_heat_transfer_time_step[i] = 0.0533;
             recomended_heat_transfer_time_step[i] = recomended_heat_transfer_time_step[i]*ui_material.getSpecificHeatCapacity();
             recomended_heat_transfer_time_step[i] = recomended_heat_transfer_time_step[i]*ui_specimen.getCellSize();
             recomended_heat_transfer_time_step[i] = recomended_heat_transfer_time_step[i]*ui_specimen.getCellSize();
             recomended_heat_transfer_time_step[i] = recomended_heat_transfer_time_step[i]*ui_material.getDensity();
             recomended_heat_transfer_time_step[i] = recomended_heat_transfer_time_step[i] / ui_material.getThermalConductivity();
         }

         // Find minimum value among maximum values

         // This field is intended for find min value among
         // all values
         double min_max_heat_time_step = recomended_heat_transfer_time_step[0];

         for(int i = 1; i < recomended_heat_transfer_time_step.length; i++)
            if(min_max_heat_time_step > recomended_heat_transfer_time_step[i])
                min_max_heat_time_step = recomended_heat_transfer_time_step[i];
                
         // Set calculated value in corresponding label
         max_heat_time_step_help.setText("max time step = "+min_max_heat_time_step);

         // Remember value
         max_heat_time_step_value = min_max_heat_time_step;

         // Calculate max time step value for only
         // "mechanical loading" task type

//         recomended_mech_loading_time_step = ui_specimen.getCellSize() / 13;
//         recomended_mech_loading_time_step = recomended_mech_loading_time_step / getMaximalMechMaxMobility();
//         recomended_mech_loading_time_step = recomended_mech_loading_time_step / getMaximalModElast();

         // Remember calculated value
         double min_max_mechanical_loading_time_step = recomended_mech_loading_time_step;

         // Set calculated value in corresponding label
//         max_mechanical_loading_time_step_help.setText("max time step = "+min_max_mechanical_loading_time_step);
// There is no limit for mech max time step
         max_mechanical_loading_time_step_help.setText("max time step = infinity");

         // Remember value
         max_mechanical_loading_time_step_value = min_max_mechanical_loading_time_step;
         
         // Calculate max time step value for only
         // "recrystallization" task type

         // Remember boundary and initial conditions
         // it needs for choose minimal temperature
         ui_boundary = new UIBoundaryCondition(Common.BOUND_COND_PATH+"/"+specimen_name+Common.BOUND_COND_NAME+"/"+transfer_data_bank.getUIInterface().getBoundCondPath()+"."+Common.BOUND_COND_EXTENSION);
         ui_initial = new UIInitialCondition(Common.INIT_COND_PATH+"/"+specimen_name+Common.INIT_COND_NAME+"/"+transfer_data_bank.getUIInterface().getInitCondPath()+"."+Common.INIT_COND_EXTENSION);

         /*
          * Set max temperature
          */

         // In case of special specimen
         if(transfer_data_bank.getUIInterface().getSpecialSpecimenPath()!=null)
         {
             // Temporary temperature for finding maximal temperature value
             double temp = ui_initial.getFirstTemperature();
             
             if(temp < ui_initial.getSecondTemperature())
                 temp = ui_initial.getSecondTemperature();
             if(temp < ui_initial.getThirdTemperature())
                 temp = ui_initial.getThirdTemperature();
             if(temp < ui_initial.getFourthTemperature())
                 temp = ui_initial.getFourthTemperature();
             
             max_temp = temp;
         }
         else
             max_temp = ui_initial.getFirstTemperature();

         if(ui_boundary.getTopTemperature())
             if(ui_boundary.getTopThermalInflux()>max_temp)
                 max_temp = ui_boundary.getTopThermalInflux();
         if(ui_boundary.getBottomTemperature())
             if(ui_boundary.getBottomThermalInflux()>max_temp)
                 max_temp = ui_boundary.getBottomThermalInflux();
         if(ui_boundary.getLeftTemperature())
             if(ui_boundary.getLeftThermalInflux()>max_temp)
                 max_temp = ui_boundary.getLeftThermalInflux();
         if(ui_boundary.getRightTemperature())
             if(ui_boundary.getRightThermalInflux()>max_temp)
                 max_temp = ui_boundary.getRightThermalInflux();
         if(ui_boundary.getFrontTemperature())
             if(ui_boundary.getFrontThermalInflux()>max_temp)
                 max_temp = ui_boundary.getFrontThermalInflux();
         if(ui_boundary.getBackTemperature())
             if(ui_boundary.getBackThermalInflux()>max_temp)
                 max_temp = ui_boundary.getBackThermalInflux();

         /*
          * Set min temperature
          */

         // In case of special specimen
         if(transfer_data_bank.getUIInterface().getSpecialSpecimenPath()!=null)
         {
             // Temporary temperature for finding minimal temperature value
             double temp = ui_initial.getFirstTemperature();

             if(temp > ui_initial.getSecondTemperature())
                 temp = ui_initial.getSecondTemperature();
             if(temp > ui_initial.getThirdTemperature())
                 temp = ui_initial.getThirdTemperature();
             if(temp > ui_initial.getFourthTemperature())
                 temp = ui_initial.getFourthTemperature();

             max_temp = temp;
         }
         else
             min_temp = ui_initial.getFirstTemperature();

         if(ui_boundary.getTopTemperature())
             if(ui_boundary.getTopThermalInflux()<min_temp)
                 min_temp = ui_boundary.getTopThermalInflux();
         if(ui_boundary.getBottomTemperature())
             if(ui_boundary.getBottomThermalInflux()<min_temp)
                 min_temp = ui_boundary.getBottomThermalInflux();
         if(ui_boundary.getLeftTemperature())
             if(ui_boundary.getLeftThermalInflux()<min_temp)
                 min_temp = ui_boundary.getLeftThermalInflux();
         if(ui_boundary.getRightTemperature())
             if(ui_boundary.getRightThermalInflux()<min_temp)
                 min_temp = ui_boundary.getRightThermalInflux();
         if(ui_boundary.getFrontTemperature())
             if(ui_boundary.getFrontThermalInflux()<min_temp)
                 min_temp = ui_boundary.getFrontThermalInflux();
         if(ui_boundary.getBackTemperature())
             if(ui_boundary.getBackThermalInflux()<min_temp)
                 min_temp = ui_boundary.getBackThermalInflux();

         double max_density = 0;
         double max_deviation = 0;

         double max_max_mobility = 0;
         double max_spec_heat_capacity = 0;
         double max_disl_max_mobility = 0;
         double max_mod_shear = 0;
         double max_lattice_param = 0;
         double max_disl_distr_coeff = 0;

         double temp_value = 0;

         for(int i = 0; i < specimen_table_rows_num; i++)
         {
             ui_material = new UIMaterial(Common.MATERIALS_PATH+"/"+transfer_data_bank.getUISpecimen().materials.get(i)+"."+Common.MATERIAL_EXTENSION);

             temp_value = Double.valueOf(ui_specimen.disl_density.get(i));

             if(max_density < temp_value)
                 max_density = temp_value;

             temp_value = Double.valueOf(ui_specimen.max_deviation.get(i)).doubleValue();

             if(max_deviation < temp_value)
                 max_deviation = temp_value;

             temp_value = ui_material.getMaxMobility();

             if(max_max_mobility < temp_value)
                 max_max_mobility = temp_value;

             temp_value = ui_material.getSpecificHeatCapacity();

             if(max_spec_heat_capacity < temp_value)
                 max_spec_heat_capacity = temp_value;

             temp_value = ui_material.getDislMaxMobility();

             if(max_disl_max_mobility < temp_value)
                 max_disl_max_mobility = temp_value;

             temp_value = ui_material.getModShear();

             if(max_mod_shear < temp_value)
                 max_mod_shear = temp_value;

             temp_value = ui_material.getLatticeParameter();

             if(max_lattice_param < temp_value)
                 max_lattice_param = temp_value;

             temp_value = ui_material.getDislDistrCoeff();

             if(max_disl_distr_coeff < temp_value)
                 max_disl_distr_coeff = temp_value;
         }

         // Calculate max time step value
   //      for(int i = 0; i < specimen_table_rows_num; i++)
     //    {
       //      ui_material = new UIMaterial(Common.MATERIALS_PATH+"/"+transfer_data_bank.getUISpecimen().getTableData().getCell(i, 1)+"."+Common.MATERIAL_EXTENSION);

             max_recrystallization_time_step_value = ui_specimen.getCellSize() / (max_max_mobility*max_spec_heat_capacity*(max_temp-min_temp)+2*max_disl_max_mobility*max_mod_shear*max_lattice_param*max_lattice_param*max_disl_distr_coeff*max_density*max_deviation);
    //     }

         // Find minimum value among maximum values

         // This field is intended for find min value among
         // all values
//         double min_max_recrystallization_time_step = recomended_recrysatllization_time_step[0];

//         for(int i = 1; i < recomended_recrysatllization_time_step.length; i++)
  //           if(min_max_recrystallization_time_step < recomended_recrysatllization_time_step[i])
    //             min_max_recrystallization_time_step = recomended_recrysatllization_time_step[i];

         // Set calculated value in corresponding label
         max_recrystallization_time_step_help.setText("max time step = "+max_recrystallization_time_step_value);

         // Remember value
  //       max_recrystallization_time_step_value = min_max_recrystallization_time_step;

         // Calculate max time step for combination
         // of task types: "heat transfer" and "mechanical loading"
         if(max_heat_time_step_value < max_mechanical_loading_time_step_value)
             max_heat_vs_mech_time_step_value = max_heat_time_step_value;
         else
             max_heat_vs_mech_time_step_value = max_mechanical_loading_time_step_value;

         // Calculate max time step for combination
         // of task types: "recrystallization" and "mechanical loading"
         if(max_recrystallization_time_step_value < max_mechanical_loading_time_step_value)
             max_recryst_vs_mech_time_step_value = max_recrystallization_time_step_value;
         else
             max_recryst_vs_mech_time_step_value = max_mechanical_loading_time_step_value;

         // Calculate max time step for combination
         // of task types: "recrystallization" and "heat transfer"
         if(max_heat_time_step_value < max_recrystallization_time_step_value)
             max_heat_vs_recryst_time_step_value = max_heat_time_step_value;
         else
             max_heat_vs_recryst_time_step_value = max_recrystallization_time_step_value;

         // Calculate max time step for combination
         // of task types: "heat transfer" and "recrystallization"
         // and "mechanical loading"
         // This field is intended for find minimum value among them
         double min = max_heat_time_step_value;
         if(min > max_recrystallization_time_step_value)
             min = max_recrystallization_time_step_value;
         if(min > max_mechanical_loading_time_step_value)
             min = max_mechanical_loading_time_step_value;
         // Remember calculated value fot this combination
         max_heat_vs_recryst_vs_mech_time_step_value = min;
    }

    /*
     * Create labels with text which equals value
     * of maximum time step of corresponding task type
     */

    public void createMaxTimeStepLabels()
    {
        System.out.println("RecCATaskParams: method: createMaxTimeStepLabels: start");

        // Create label for "heat" task type
        max_heat_time_step_help = new Label("max time step = 0");
        // Set not visible
        max_heat_time_step_help.setVisible(false);

        // Create label for "recrystallization" task type
        max_recrystallization_time_step_help = new Label("max time step = 0");
        // Set not visible
        max_recrystallization_time_step_help.setVisible(false);
        
        // Create label for "mechanical loading" task type
        max_mechanical_loading_time_step_help = new Label("max time step = 0");
        // Set not visible
        max_mechanical_loading_time_step_help.setVisible(false);

        // Create general label for all task type
        max_time_step_help = new Label();
        
    }

    /*
     * Give maximal mod elast from all choosed materials
     * @return - maximal mod elast among all set of mod elasts
     */

    public double getMaximalModElast()
    {
        // This field is intended for find maximal mod elast
        double mod_elast = 0;

        // Read each differnet material from different material
        // array
        for(int i = 0; i < uses_materials.length; i++)
        {
            // Read material params
            ui_material = new UIMaterial(Common.MATERIALS_PATH+"/"+uses_materials[i]+"."+Common.MATERIAL_EXTENSION);
            // Check - new value of mod elast bigger or not then old
            if(mod_elast < ui_material.getModElast())
                // If new value of mod elast is bigger then remember it
                mod_elast = ui_material.getModElast();
        }

        // Give maximal value of mod elast
        return mod_elast;
    }

    /*
     * Give maximal mech max mobility from all choosed materials
     * @return - maximal mech max mobility among all set of mech max mobilities
     */

    public double getMaximalMechMaxMobility()
    {
        // This field is intended for find maximal mod elast
        double mech_max_mobility = 0;

        // Read each differnet material from different material
        // array
        for(int i = 0; i < uses_materials.length; i++)
         {
            // Read material params
            ui_material = new UIMaterial(Common.MATERIALS_PATH+"/"+uses_materials[i]+"."+Common.MATERIAL_EXTENSION);
            // Check - new value of mod elast bigger or not then old
            if(mech_max_mobility < ui_material.getMechMaxMobility())
                // If new value of mod elast is bigger then remember it
                mech_max_mobility = ui_material.getMechMaxMobility();
        }

        // Give maximal value of mech max mobility
        return mech_max_mobility;
    }

    /*
     * Give all uses materials (in specimen and boundaries)
     * @param materials - array for remember uses materials
     */

    public void getUseMaterials()
    {
        // Remember number of used materials on specimen
        specimen_table_rows_num = transfer_data_bank.getUISpecimen().materials.size();
        // This field is intended for remember all material names
        // (specimen_table_rows_num number of materials in specimen
        // and 6 number of materials in boundaries)
//System.out.println("////Task Params second spec rows num (first in method) = "+specimen_table_rows_num);
        String [] different_materials = new String[specimen_table_rows_num+6];
        // This field is intended for remember number
        // of different materials
        int different_materials_number = 0;
        // Read all specimen material names
//System.out.println("//// spec rows num = "+specimen_table_rows_num);
        for(int i = 0; i < specimen_table_rows_num; i++)
            // Remember specimen material name
        {
//System.out.println("//// i = "+i);
            different_materials[i] = new String(transfer_data_bank.getUISpecimen().materials.get(i));
//System.out.println("//// begin different_materials "+i+" = "+different_materials[i]);
        }

        // Remember TOP boundarie material name
        different_materials[specimen_table_rows_num] = new String(transfer_data_bank.getUIBoundaryCondition().getTopAdiabaticMaterial());
//System.out.println("//// begin different_materials "+specimen_table_rows_num + 0+" = "+different_materials[specimen_table_rows_num + 0]);
        // Remember BOTTOM boundarie material name
        different_materials[specimen_table_rows_num + 1] = new String(transfer_data_bank.getUIBoundaryCondition().getBottomAdiabaticMaterial());
//System.out.println("//// begin different_materials "+specimen_table_rows_num + 1+" = "+different_materials[specimen_table_rows_num + 1]);
        // Remember LEFT boundarie material name
        different_materials[specimen_table_rows_num + 2] = new String(transfer_data_bank.getUIBoundaryCondition().getLeftAdiabaticMaterial());
//System.out.println("//// begin different_materials "+specimen_table_rows_num + 2+" = "+different_materials[specimen_table_rows_num + 2]);
        // Remember RIGHT boundarie material name
        different_materials[specimen_table_rows_num + 3] = new String(transfer_data_bank.getUIBoundaryCondition().getRightAdiabaticMaterial());
//System.out.println("//// begin different_materials "+specimen_table_rows_num + 3+" = "+different_materials[specimen_table_rows_num + 3]);
        // Remember FRONT boundarie material name
        different_materials[specimen_table_rows_num + 4] = new String(transfer_data_bank.getUIBoundaryCondition().getFrontAdiabaticMaterial());
//System.out.println("//// begin different_materials "+specimen_table_rows_num + 4+" = "+different_materials[specimen_table_rows_num + 4]);
        // Remember BACK boundarie material name
        different_materials[specimen_table_rows_num + 5] = new String(transfer_data_bank.getUIBoundaryCondition().getBackAdiabaticMaterial());
//System.out.println("//// begin different_materials "+specimen_table_rows_num + 5+" = "+different_materials[specimen_table_rows_num + 5]);

        // Calculate number of different material names

        // This field is intended for controll each follow
        // material name is different name or not
        boolean different_name = true;
//System.out.println("//// begin different_materials length = "+different_materials.length);
        // Check all names
        for(int i = 0; i < different_materials.length; i++)
        {
//System.out.println("//// check different_materials length "+i+" = "+different_materials[i]);
            // Set that name is different (in default)
            different_name = true;
            // If checked materials exists then checked with
            // another names
            if(!different_materials[i].equals("")&!different_materials[i].equals(UICommon.FIRST_CHOOSE_NAME))
            {
                // Check with other names
                for(int j = i + 1; j < different_materials.length; j++)
                    // If other name is similar as first, then it must be
                    // not remember
                    if(different_materials[i].equals(different_materials[j])
                    &!different_materials[j].equals(""))
                    {
                        // This name is not different
//                        different_name = false;
                        // Remove similar name
                        different_materials[j] = "";
                    }
            }
            // If name is not exists
            else
            {
                // Remove this name
                different_materials[i] = "";
                
                // This name is not different
                different_name = false;
            }

            // If after check choosed material name is another
            // then this name add in list
            if(different_name == true)
                different_materials_number = different_materials_number + 1;
        }

        // Create array of uses materials
        uses_materials = new String[different_materials_number];
        // This field is intended for fill each cell in array
        int k = 0;
        // Fill array
        for(int i = 0; i < different_materials.length; i++)
            // If material name exists than add it
            if(!different_materials[i].equals(""))
            {
                System.out.println("/// uses material = "+different_materials[i]);
                uses_materials[k] = different_materials[i];
                k = k + 1;
            }
    }
}
