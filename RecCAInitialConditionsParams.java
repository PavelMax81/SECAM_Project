/*
 * @(#) RecCAInitialConditionsParams.java version 1.0.0;       April 21 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for creation a frame for input parameters of initial conditions
 * 
 *=============================================================
 *  Last changes :
 *          25 December 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.1.0
 *          Edit: When lrs (special specimen) was choosed, then we must create
 *          5 lines in corresponding file (init cond file), else 5 equal lines
 */

/** Class for creation a frame for input parameters of initial conditions
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.*;

public class RecCAInitialConditionsParams extends Stage
{
    /**
     * This field is intended for creation helps for users what to input
     */
    
    Label help_first_thermal_energy,help_first_mechanical_energy,
            help_first_temperature, help_second_thermal_energy,
            help_second_mechanical_energy, help_second_temperature,
            help_third_thermal_energy,help_third_mechanical_energy,
            help_third_temperature, help_fourth_thermal_energy,
            help_fourth_mechanical_energy, help_fourth_temperature,
            help_fifth_thermal_energy, help_fifth_mechanical_energy,
            help_fifth_temperature;

    /**
     * This field is intended for write for what material we choose
     * initial conditions if special specimen was chosoed
     */

    Label first_material_help, second_material_help,
            third_material_help, fourth_material_help,
            fifth_material_help;
    
    /**
     * This field is intended for creation a textfields for inputing or showing
     * parameters of corresponding helps
     */
    
    TextField first_thermal_energy, first_mechanical_energy,
            first_temperature, second_thermal_energy,
            second_mechanical_energy, second_temperature,
            third_thermal_energy, third_mechanical_energy,
            third_temperature, fourth_thermal_energy,
            fourth_mechanical_energy, fourth_temperature,
            fifth_thermal_energy, fifth_mechanical_energy,
            fifth_temperature;

    /**
     * This field is intended controll the button of saving params
     */

    int save_option;
    
    /**
     * This field is intended for saving params
     */

    SaveParams saving;
    
    /**
     * This field is intended for creating buttons: "Save as" and "Cancel" or "OK"
     */
    
    Button button_save,button_ok, button_cancel;

    /**
     * This field is intended for remember all initial conditions in their databank
     */

    UIInitialCondition global_ui_init_cond;
    
    /**
     * This field is intended for input file name for writing params
     */
    
    TextField textfield_for_file_location;

    
    /**
     * This field is intended for work with transferred data bank
     */

    TransferredDataBank transfer_data_bank;
    
    Scene scene;
    VBox root;
    
    /**
     * Create frame for input parameters of initial condition
     * @param receive_transfer_data_bank - transferred data bank
     */
    
    public RecCAInitialConditionsParams(TransferredDataBank receive_transfer_data_bank)
    {
        /*
         * Remember transferred data bank
         */

        transfer_data_bank = receive_transfer_data_bank;
        
        System.out.println("RecCAInitialConditionsParams: constructor creation start");

        /*
         * Remember UIInitialCondition
         */

        global_ui_init_cond = transfer_data_bank.getUIInitialCondition();
        
        createHelpsForUsersInParamsOfInitialConditions();
        createTextFieldsForParamsOfInitialConditions();
        createButtonsForParamsOfInitialConditions();
        addElementsForParametersOfInitialConditions();
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(15, 2, 15, 2));
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setPadding(new Insets(0, 5, 0, 5));
        button_save.setPadding(new Insets(5, 30, 5, 30));
        button_cancel.setPadding(new Insets(5, 30, 5, 30));
        
        HBox bottom_layout = new HBox(button_save, sep1, button_cancel);
        bottom_layout.setAlignment(Pos.CENTER);
        bottom_layout.setPadding(new Insets(10,30,30,30));
        
        root = new VBox();
        
        root.getChildren().addAll(
                tab_pane,
                sep,
                bottom_layout
        );
        
        root.setAlignment(Pos.TOP_CENTER);
        
        scene = new Scene(root);
        
        this.setScene(scene);
        this.setTitle("Initial Conditions");
        
        handleEvents();
        
        textfield_for_file_location = new TextField();
    }

    /*
     * Controll buttons
     * @param w - event
     */
    
    public void handleEvents() 
    {
        button_save.setOnAction
        ( 
          e -> 
          {
            if(isParametersInput())
            {
                System.out.println("RecCAInitialConditionsParams: method: actionPerformed: button 'save' is pushed");
                inputParams();
                savingDialofOfParamsForInitialConditions();
            }
          }
        );
    }
    
    /*
     * Create parameter helps
     */
    
    private void createHelpsForUsersInParamsOfInitialConditions()
    {
        System.out.println("RecCAInitialConditionsParams: method: createHelpsForUsersInParamsOfInitialConditions: start");

        help_first_thermal_energy = new Label(UICommon.HEAT_ENERGY_NAME);
        help_first_mechanical_energy = new Label(UICommon.MECHANICAL_ENERGY_NAME);
        help_first_temperature = new Label(UICommon.TEMPERATURE_NAME);
        
        // In case of special specimen all helps must be created,
        // it is: material helps and parameter helps
        
        // Create helps for each material
        first_material_help = new Label("1st PHASE ("+transfer_data_bank.getUISpecimen().materials.get(0)+")");
        second_material_help = new Label("2nd PHASE ("+transfer_data_bank.getUISpecimen().materials.get(1)+")");
        third_material_help = new Label("3rd PHASE ("+transfer_data_bank.getUISpecimen().materials.get(2)+")");
        fourth_material_help = new Label("4th PHASE ("+transfer_data_bank.getUISpecimen().materials.get(3)+")");
        fifth_material_help = new Label("5th PHASE ("+transfer_data_bank.getUISpecimen().materials.get(4)+")");
            
        // Create helps for parameter fields

        help_second_thermal_energy = new Label(UICommon.HEAT_ENERGY_NAME);
        help_second_mechanical_energy = new Label(UICommon.MECHANICAL_ENERGY_NAME);
        help_second_temperature = new Label(UICommon.TEMPERATURE_NAME);
        help_third_thermal_energy = new Label(UICommon.HEAT_ENERGY_NAME);
        help_third_mechanical_energy = new Label(UICommon.MECHANICAL_ENERGY_NAME);
        help_third_temperature = new Label(UICommon.TEMPERATURE_NAME);
        help_fourth_thermal_energy = new Label(UICommon.HEAT_ENERGY_NAME);
        help_fourth_mechanical_energy = new Label(UICommon.MECHANICAL_ENERGY_NAME);
        help_fourth_temperature = new Label(UICommon.TEMPERATURE_NAME);
        help_fifth_thermal_energy = new Label(UICommon.HEAT_ENERGY_NAME);
        help_fifth_mechanical_energy = new Label(UICommon.MECHANICAL_ENERGY_NAME);
        help_fifth_temperature = new Label(UICommon.TEMPERATURE_NAME);
        
    }
    
    /*
     * Create textfields for input parameters
     */
    
    private void createTextFieldsForParamsOfInitialConditions()
    {
        System.out.println("RecCAInitialConditionsParams: method: createTextFieldsForParamsOfInitialConditions: start");

        first_thermal_energy = new TextField();
        first_thermal_energy.setText((new Double(global_ui_init_cond.getFirstThermalEnergy())).toString());
        first_mechanical_energy = new TextField();
        first_mechanical_energy.setText((new Double(global_ui_init_cond.getFirstMechanicalEnergy())).toString());
        first_temperature = new TextField();
        first_temperature.setText((new Double(global_ui_init_cond.getFirstTemperature())).toString());
        // If special specimen was choosed then we must create
        // text field for each material for input initial conditions
        // for each of them
        second_thermal_energy = new TextField();
        second_thermal_energy.setText((new Double(global_ui_init_cond.getSecondThermalEnergy())).toString());
        second_mechanical_energy = new TextField();
        second_mechanical_energy.setText((new Double(global_ui_init_cond.getSecondMechanicalEnergy())).toString());
        second_temperature = new TextField();
        second_temperature.setText((new Double(global_ui_init_cond.getSecondTemperature())).toString());
        third_thermal_energy = new TextField();
        third_thermal_energy.setText((new Double(global_ui_init_cond.getThirdThermalEnergy())).toString());
        third_mechanical_energy = new TextField();
        third_mechanical_energy.setText((new Double(global_ui_init_cond.getThirdMechanicalEnergy())).toString());
        third_temperature = new TextField();
        third_temperature.setText((new Double(global_ui_init_cond.getThirdTemperature())).toString());
        fourth_thermal_energy = new TextField();
        fourth_thermal_energy.setText((new Double(global_ui_init_cond.getFourthThermalEnergy())).toString());
        fourth_mechanical_energy = new TextField();
        fourth_mechanical_energy.setText((new Double(global_ui_init_cond.getFourthMechanicalEnergy())).toString());
        fourth_temperature = new TextField();
        fourth_temperature.setText((new Double(global_ui_init_cond.getFourthTemperature())).toString());
        fifth_thermal_energy = new TextField();
        fifth_thermal_energy.setText((new Double(global_ui_init_cond.getFifthThermalEnergy())).toString());
        fifth_mechanical_energy = new TextField();
        fifth_mechanical_energy.setText((new Double(global_ui_init_cond.getFifthMechanicalEnergy())).toString());
        fifth_temperature = new TextField();
        fifth_temperature.setText((new Double(global_ui_init_cond.getFifthTemperature())).toString());                    
    }
    
    /*
     * Create buttons "Save as" and "OK" or "Cancel"
     * @param name - name of second button
     */
    
    public void createButtonsForParamsOfInitialConditions()
    {
        System.out.println("RecCAInitialConditionsParams: method: createButtonsForParamsOfInitialConditions: start");

        button_save = new Button(UICommon.BUTTON_SAVE_NAME);
        button_ok = new Button(UICommon.BUTTON_OK_NAME);            
        button_cancel = new Button(UICommon.BUTTON_CANCEL_NAME);                
    }
    
    /*
     * Add elements on frame
     */
    
    TabPane tab_pane;
    Tab first_tab;
    Tab second_tab;
    Tab third_tab;
    Tab fourth_tab;
    Tab fifth_tab;
    
    public void addElementsForParametersOfInitialConditions()
    {
        System.out.println("RecCAInitialConditionsParams: method: addElementsForParametersOfInitialConditions: start");
        
        tab_pane = new TabPane();
        
        first_tab = new Tab("1st Phase");
        first_tab.setClosable(false);
        
        second_tab = new Tab("2nd Phase");
        second_tab.setClosable(false);
        
        third_tab = new Tab("3rd Phase");
        third_tab.setClosable(false);
        
        fourth_tab = new Tab("4th Phase");
        fourth_tab.setClosable(false);
        
        fifth_tab = new Tab("5th Phase");
        fifth_tab.setClosable(false);
        
        Separator sep1 = new Separator();
        
        GridPane first_tab_layout = new GridPane();
        GridPane.setConstraints(first_material_help, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(sep1, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_first_thermal_energy, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_first_mechanical_energy, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_first_temperature, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(first_thermal_energy, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(first_mechanical_energy, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(first_temperature, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        first_tab_layout.setHgap(7.0d);
        first_tab_layout.setVgap(7.0d);
        first_tab_layout.setAlignment(Pos.CENTER);
        first_tab_layout.setPadding(new Insets(30,30,30,30));
        first_tab_layout.getChildren().addAll(
                sep1,
                first_material_help,
                help_first_thermal_energy,
                help_first_mechanical_energy,
                help_first_temperature,
                first_thermal_energy, 
                first_mechanical_energy,
                first_temperature
        );
        first_tab.setContent(first_tab_layout);
        
        Separator sep2 = new Separator();
        GridPane second_tab_layout = new GridPane();
        GridPane.setConstraints(second_material_help, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(sep2, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_second_thermal_energy, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_second_mechanical_energy, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_second_temperature, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(second_thermal_energy, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(second_mechanical_energy, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(second_temperature, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        second_tab_layout.setHgap(7.0d);
        second_tab_layout.setVgap(7.0d);
        second_tab_layout.setAlignment(Pos.CENTER);
        second_tab_layout.setPadding(new Insets(30,30,30,30));
        second_tab_layout.getChildren().addAll(
                sep2,
                second_material_help,
                help_second_thermal_energy,
                help_second_mechanical_energy,
                help_second_temperature,
                second_thermal_energy, 
                second_mechanical_energy,
                second_temperature
        );
        second_tab.setContent(second_tab_layout);
        
        Separator sep3 = new Separator();
        GridPane third_tab_layout = new GridPane();
        GridPane.setConstraints(third_material_help, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(sep3, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_third_thermal_energy, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_third_mechanical_energy, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_third_temperature, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(third_thermal_energy, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(third_mechanical_energy, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(third_temperature, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        third_tab_layout.setHgap(7.0d);
        third_tab_layout.setVgap(7.0d);
        third_tab_layout.setAlignment(Pos.CENTER);
        third_tab_layout.setPadding(new Insets(30,30,30,30));
        third_tab_layout.getChildren().addAll(
                sep3,
                third_material_help,
                help_third_thermal_energy,
                help_third_mechanical_energy,
                help_third_temperature,
                third_thermal_energy, 
                third_mechanical_energy,
                third_temperature
        );
        third_tab.setContent(third_tab_layout);
        
        Separator sep4 = new Separator();
        GridPane fourth_tab_layout = new GridPane();
        GridPane.setConstraints(fourth_material_help, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(sep4, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_fourth_thermal_energy, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_fourth_mechanical_energy, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_fourth_temperature, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(fourth_thermal_energy, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(fourth_mechanical_energy, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(fourth_temperature, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        fourth_tab_layout.setHgap(7.0d);
        fourth_tab_layout.setVgap(7.0d);
        fourth_tab_layout.setAlignment(Pos.CENTER);
        fourth_tab_layout.setPadding(new Insets(30,30,30,30));
        fourth_tab_layout.getChildren().addAll(
                sep4,
                fourth_material_help,
                help_fourth_thermal_energy,
                help_fourth_mechanical_energy,
                help_fourth_temperature,
                fourth_thermal_energy, 
                fourth_mechanical_energy,
                fourth_temperature
        );
        fourth_tab.setContent(fourth_tab_layout);
        
        Separator sep5 = new Separator();
        GridPane fifth_tab_layout = new GridPane();
        GridPane.setConstraints(fifth_material_help, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(sep5, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_fifth_thermal_energy, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_fifth_mechanical_energy, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_fifth_temperature, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(fifth_thermal_energy, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(fifth_mechanical_energy, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(fifth_temperature, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        fifth_tab_layout.setHgap(7.0d);
        fifth_tab_layout.setVgap(7.0d);
        fifth_tab_layout.setAlignment(Pos.CENTER);
        fifth_tab_layout.setPadding(new Insets(30,30,30,30));
        fifth_tab_layout.getChildren().addAll(
                sep5,
                fifth_material_help,
                help_fifth_thermal_energy,
                help_fifth_mechanical_energy,
                help_fifth_temperature,
                fifth_thermal_energy, 
                fifth_mechanical_energy,
                fifth_temperature
        );
        fifth_tab.setContent(fifth_tab_layout);
        
        tab_pane.getTabs().addAll(first_tab, second_tab, third_tab, fourth_tab, fifth_tab);
    }
    
    /*
     * Creaet dialog which used to choose path of saving
     */
    
    public void savingDialofOfParamsForInitialConditions()
    {
        System.out.println("RecCAInitialConditionsParams: method: savingDialofOfParamsForInitialConditions: start");

        /*
         * Create JTextField for input location of saving data
         */
        this.close();
        
        SavingDialog save = new SavingDialog(textfield_for_file_location);
        
        save.save_btn.setOnAction(e -> {
            if(isNameCorrect(textfield_for_file_location))
            {
                try{
                    /*
                     * Save data in file
                    */
                    saving = new SaveParams(global_ui_init_cond,transfer_data_bank.getUIInterface().getSpecimenPath(),textfield_for_file_location.getText());
                    transfer_data_bank.getUIInterface().setInitCondPath(textfield_for_file_location.getText());
                    new Alert(AlertType.INFORMATION, "File successfuly created").showAndWait();
                }
                catch(Exception ex){
                    new Alert(AlertType.ERROR, "Failed to create file").showAndWait();
                }
            }
            save.close();
        });
        
    }

    /** Remember inputed parameters in UIInitialCondition
     */
    public void inputParams()
    {
        System.out.println("RecCAInitialConditionsParams.inputParams() is started.");

        // Set input parameters in data bank
        global_ui_init_cond.setFirstThermalEnergy(Double.valueOf(first_thermal_energy.getText()));
        global_ui_init_cond.setFirstMechanicalEnergy(Double.valueOf(first_mechanical_energy.getText()));
        global_ui_init_cond.setFirstTemperature(Double.valueOf(first_temperature.getText()));
        
        global_ui_init_cond.setSecondThermalEnergy(Double.valueOf(second_thermal_energy.getText()));
        global_ui_init_cond.setSecondMechanicalEnergy(Double.valueOf(second_mechanical_energy.getText()));
        global_ui_init_cond.setSecondTemperature(Double.valueOf(second_temperature.getText()));
        
        global_ui_init_cond.setThirdThermalEnergy(Double.valueOf(third_thermal_energy.getText()));
        global_ui_init_cond.setThirdMechanicalEnergy(Double.valueOf(third_mechanical_energy.getText()));
        global_ui_init_cond.setThirdTemperature(Double.valueOf(third_temperature.getText()));
        
        global_ui_init_cond.setFourthThermalEnergy(Double.valueOf(fourth_thermal_energy.getText()));
        global_ui_init_cond.setFourthMechanicalEnergy(Double.valueOf(fourth_mechanical_energy.getText()));
        global_ui_init_cond.setFourthTemperature(Double.valueOf(fourth_temperature.getText()));
        
        global_ui_init_cond.setFifthThermalEnergy(Double.valueOf(fifth_thermal_energy.getText()));
        global_ui_init_cond.setFifthMechanicalEnergy(Double.valueOf(fifth_mechanical_energy.getText()));
        global_ui_init_cond.setFifthTemperature(Double.valueOf(fifth_temperature.getText()));
    }

    /** Controll that right form of initial condition parameters was inputed
     * @return - right form of value or not
     */
    public boolean isParametersInput()
    {
        System.out.println("RecCAInitialConditionsParams: method: isParametersInput: start");

        try
        {
            Double.parseDouble(first_thermal_energy.getText());
            Double.parseDouble(first_mechanical_energy.getText());
            Double.parseDouble(first_temperature.getText());

            // If we set init cond for 4 materials (in case of special specimen)
            // we need to check all inputed params
            if(transfer_data_bank.getUIInterface().getSpecialSpecimenPath()!=null)
            {
                Double.parseDouble(second_thermal_energy.getText());
                Double.parseDouble(second_mechanical_energy.getText());
                Double.parseDouble(second_temperature.getText());

                Double.parseDouble(third_thermal_energy.getText());
                Double.parseDouble(third_mechanical_energy.getText());
                Double.parseDouble(third_temperature.getText());

                Double.parseDouble(fourth_thermal_energy.getText());
                Double.parseDouble(fourth_mechanical_energy.getText());
                Double.parseDouble(fourth_temperature.getText());

                Double.parseDouble(fifth_thermal_energy.getText());
                Double.parseDouble(fifth_mechanical_energy.getText());
                Double.parseDouble(fifth_temperature.getText());
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
        System.out.println("RecCAInitialConditionsParams: method: isNameCorrect: start");

        boolean choice = false;

        String file_name = users_file_name.getText();

        if(file_name.isEmpty())
        {   
            new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }
        
      return true;
    }
}
