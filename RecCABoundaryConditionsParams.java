/*
 * @(#) RecCABoundaryConditionsParams.java version 1.0.0;       April 21 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for creation a frame for inputing or showing parameters of
 * created or choosed boundary conditions
 * 
 *=============================================================
 *  Last changes :
 *          23 June 2010 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.2.1
 *          Edit: create controll for choosing stress or strain for mechanical
 *          loading and tempearture or energy for thermal influx on each boundary
 */

/** Class for creation a frame for inputing or showing parameters of
 *  created or chosen boundary conditions
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - April 2009
 */
package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Common;


public class RecCABoundaryConditionsParams extends Stage
{   
    Scene scene;
    VBox root;
    
    /** These fields are intended for creation text fields for inputing or showing
     * parameters ("Mechanical loading": stress or strain,
     * "Thermal influx": temperature or energy) at corresponding boundary facet 
     * ("Top", "Bottom", "Left", "Right", "Front" or "Back").
     */    
    TextField top_mechanical_loading,    top_thermal_influx,    top_bound_time_function_type,    top_bound_load_time_portion,    top_bound_relax_time_portion;
    TextField bottom_mechanical_loading, bottom_thermal_influx, bottom_bound_time_function_type, bottom_bound_load_time_portion, bottom_bound_relax_time_portion;
    TextField left_mechanical_loading,   left_thermal_influx,   left_bound_time_function_type,   left_bound_load_time_portion,   left_bound_relax_time_portion;
    TextField right_mechanical_loading,  right_thermal_influx,  right_bound_time_function_type,  right_bound_load_time_portion,  right_bound_relax_time_portion;
    TextField front_mechanical_loading,  front_thermal_influx,  front_bound_time_function_type,  front_bound_load_time_portion,  front_bound_relax_time_portion;
    TextField back_mechanical_loading,   back_thermal_influx,   back_bound_time_function_type,   back_bound_load_time_portion,   back_bound_relax_time_portion;
    
    TextField top_min_mechanical_loading,    top_min_thermal_influx;
    TextField bottom_min_mechanical_loading, bottom_min_thermal_influx;
    TextField left_min_mechanical_loading,   left_min_thermal_influx;
    TextField right_min_mechanical_loading,  right_min_thermal_influx;
    TextField front_min_mechanical_loading,  front_min_thermal_influx;
    TextField back_min_mechanical_loading,   back_min_thermal_influx;
    
    /** These fields are intended for creating a set of check boxes for edit
     * necessary parameters only ("Mechanical loading", "Thermal influx")
     * at corresponding boundary facet 
     * ("Top", "Bottom", "Left", "Right", "Front" or "Back").
     */
    CheckBox top_check_mechanical_loading,    top_check_thermal_influx;
    CheckBox bottom_check_mechanical_loading, bottom_check_thermal_influx;
    CheckBox left_check_mechanical_loading,   left_check_thermal_influx;
    CheckBox right_check_mechanical_loading,  right_check_thermal_influx;
    CheckBox front_check_mechanical_loading,  front_check_thermal_influx;
    CheckBox back_check_mechanical_loading,   back_check_thermal_influx;
    
    /** These fields are intended to choose stress or strain for mechanical
     * loading and temperature or thermal energy for thermal influx.
     */
    RadioButton top_stress,    top_strain,    top_temperature,    top_energy;
    RadioButton bottom_stress, bottom_strain, bottom_temperature, bottom_energy;
    RadioButton left_stress,   left_strain,   left_temperature,   left_energy;
    RadioButton right_stress,  right_strain,  right_temperature,  right_energy;
    RadioButton front_stress,  front_strain,  front_temperature,  front_energy;
    RadioButton back_stress,   back_strain,   back_temperature,   back_energy;

    /** These fields are intended to choose the type of boundary conditions: 
     * stress or strain, and temperature or thermal energy.
     */
    ToggleGroup top_mechanical_loading_group,    top_thermal_influx_group;
    ToggleGroup bottom_mechanical_loading_group, bottom_thermal_influx_group;
    ToggleGroup left_mechanical_loading_group,   left_thermal_influx_group;
    ToggleGroup right_mechanical_loading_group,  right_thermal_influx_group;
    ToggleGroup front_mechanical_loading_group,  front_thermal_influx_group;
    ToggleGroup back_mechanical_loading_group,   back_thermal_influx_group;
    
    /** These fields are intended for creating buttons "Save as" and "Cancel" or "OK".
     */    
    Button button_save, button_ok;
    
    /** These fields are intended to create description of facets for user.
     */
    TabPane tab_pane;
    Tab top, bottom, left, right, front, back;

    /** This field is intended to input file name of saved boundary conditions.
     */
    TextField textfield_for_file_location;

    /** This field is intended to control what button in saving dialog was pushed.
     */
    int save_option;

    /** This field is intended to save data of boundary conditions.
     */
    SaveParams saving;

    /** This field is intended for remember parameters in boundary condition data bank.
     */
    UIBoundaryCondition some_global;

    /** This field is intended to control all transferred data bank.
     */
    TransferredDataBank transfer_data_bank;

    /** These fields are intended to inform user whether a cell is adiabatic.
     */
    Label top_adiobatic_or_not,   bottom_adiobatic_or_not;
    Label left_adiobatic_or_not,  right_adiobatic_or_not;
    Label front_adiobatic_or_not, back_adiobatic_or_not;
    
    /** Labels for fields responsible for type of time function, portion of loading type 
     * and portion of relaxation time for all boundary facets
     */
    Label top_time_function_type_name,    top_load_time_portion_name,    top_relax_time_portion_name;
    Label bottom_time_function_type_name, bottom_load_time_portion_name, bottom_relax_time_portion_name;
    Label left_time_function_type_name,   left_load_time_portion_name,   left_relax_time_portion_name;
    Label right_time_function_type_name,  right_load_time_portion_name,  right_relax_time_portion_name;
    Label front_time_function_type_name,  front_load_time_portion_name,  front_relax_time_portion_name;
    Label back_time_function_type_name,   back_load_time_portion_name,   back_relax_time_portion_name;

    /** These fields are intended to choose material in case when cell is not adiabatic.
     */
    Button top_adiobatic_material, bottom_adiobatic_material;
    Button left_adiobatic_material, right_adiobatic_material;
    Button front_adiobatic_material, back_adiobatic_material;
    
    /** These fields are intended for choose material for not adiabatic boundary.
     */
    RecCAFileList materials;

    /** These fields are intended for choose boundary type:
     * 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
     * 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
     * 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
     */
    RadioButton first_boundary_type, second_boundary_type, third_boundary_type;

    /** These fields are intended to control boundary type check boxes as one system.
     */
    ToggleGroup boundary_type_group;

    /** These fields are intended to create "help" for boundary type chosen.
     */
    Label boundary_type_help;
    
    /** The type of boundary parameter function of cell coordinates
     */
    int function_type;
    
    RadioButton top_inner_bound, top_outer_bound;
    RadioButton bottom_inner_bound, bottom_outer_bound;
    RadioButton left_inner_bound, left_outer_bound;
    RadioButton right_inner_bound, right_outer_bound;
    RadioButton front_inner_bound, front_outer_bound;
    RadioButton back_inner_bound, back_outer_bound;
    ToggleGroup top_group, bottom_group, left_group, right_group, front_group, back_group;

    /** Internal frame for input boundary conditions is created.
     * @param list - boundary conditions list
     * @param receive_transfer_data_bank - transferred data bank
     */
    public RecCABoundaryConditionsParams(TransferredDataBank receive_transfer_data_bank)
    {
        System.out.println("RecCABoundaryConditionsParams: start");

        // Remember transferred data bank
        transfer_data_bank = receive_transfer_data_bank;

        // Remember boundary conditions data bank
        some_global = transfer_data_bank.getUIBoundaryCondition();
        
        root = new VBox();
        scene = new Scene(root);
        
        // Create textfields for inputing or showing values of corresponding parameters
        createTextFieldsForParamsOfBoundaryConditions();

        // Create buttons: "Save as" and "Cancel" or "OK"
        createButtonsForParamsOfBoundaryConditions();

        /* Create checkboxes to each parameter for choosing
         * which parameters input and wich of them is not needed
         */        
        createCheckBoxesForBoundary();

        /* Create radio buttons for choose task types:
         * for mechanical loading: stress or strain,
         * for thermal influx: temperature or energy
         */
        createTypeTaskChoose();

        /* Create adiabatic helps
         */
        createAdiabaticInfo();

        /* Create boxes for choose material for not adiabatic boundary
         */
        createNotAdiobaticMaterialBox();

        /* Add elements on frame
         */      
        addElementsForParametersOfBoundaryConditions();

        /* Check selected checkboxes - if check box is selected
         * then value of corresponding parameter can be changed
         * and vice versa
         */
        setCheckedPositions();

        // Create boundary type help
        createBoundaryTypeHelp();

        // CReate check boxes for choose boundary type
        createBoundaryTypeCheckBoxes();
        
        root.setAlignment(Pos.TOP_CENTER);
        
        button_save.setPadding(new Insets(5, 30, 5, 30));
        HBox bottom_layout = new HBox(button_save);
        bottom_layout.setAlignment(Pos.CENTER);
        bottom_layout.setPadding(new Insets(0,30,30,30));
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 2, 10, 2));
        
        root.getChildren().addAll(
                tab_pane,
                sep,
                bottom_layout
        );
        handleEvents();
        
        this.setTitle("Boundary Conditions");
        this.setScene(scene);
        
        textfield_for_file_location = new TextField();
    }

    /** Create check boxes for choose boundary type
     */
    private void createBoundaryTypeCheckBoxes()
    {
        // Create button group
        boundary_type_group = new ToggleGroup();

        // Create check boxes
        first_boundary_type = new RadioButton(UICommon.BOUNDARY_TYPE_FIRST_NAME);
        second_boundary_type = new RadioButton(UICommon.BOUNDARY_TYPE_SECOND_NAME);
        third_boundary_type = new RadioButton(UICommon.BOUNDARY_TYPE_THIRD_NAME);
        
        boundary_type_group.getToggles().addAll(first_boundary_type, second_boundary_type, third_boundary_type);

        // Load what boundary type was in default or in
        // existed boundary conditions parameters
        if(some_global.getBoundaryType()==0)
        {
            first_boundary_type.setSelected(true);
            second_boundary_type.setSelected(false);
            third_boundary_type.setSelected(false);
        }

        if(some_global.getBoundaryType()==1)
        {
            first_boundary_type.setSelected(false);
            second_boundary_type.setSelected(true);
            third_boundary_type.setSelected(false);
        }

        if(some_global.getBoundaryType()==2)
        {
            first_boundary_type.setSelected(false);
            second_boundary_type.setSelected(false);
            third_boundary_type.setSelected(true);
        }
    }

    /**
     * Create help for choose boundary type
     */
    private void createBoundaryTypeHelp()
    {
        boundary_type_help = new Label(UICommon.BOUNDARY_TYPE_NAME);        
    }
   
    /*
     * Create textfields for inputing or showing
     * parameters ("Mechanical loading", "Thermal energy") of
     * corresponding side of boundary
     * Create helps (names of each boundary)
     */
    
    private void createTextFieldsForParamsOfBoundaryConditions()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createTextFieldsForParamsOfBoundaryConditions: start");

        // Top parameter values (textfields)
        top_min_mechanical_loading = new TextField();
        top_min_mechanical_loading.setText((new Double(some_global.getTopMinMechanicalLoading())).toString());
        top_mechanical_loading = new TextField();
        top_mechanical_loading.setText((new Double(some_global.getTopMechanicalLoading())).toString());
        top_min_thermal_influx = new TextField();
        top_min_thermal_influx.setText((new Double(some_global.getTopMinThermalInflux())).toString());
        top_thermal_influx = new TextField();
        top_thermal_influx.setText((new Double(some_global.getTopThermalInflux())).toString());
        top_bound_time_function_type = new TextField();
        top_bound_time_function_type.setText((new Byte(some_global.getTopBoundTimeFunctionType())).toString());
        top_bound_load_time_portion  = new TextField();
        top_bound_load_time_portion.setText((new Double(some_global.getTopBoundLoadTimePortion())).toString());
        top_bound_relax_time_portion  = new TextField();
        top_bound_relax_time_portion.setText((new Double(some_global.getTopBoundRelaxTimePortion())).toString());
        // Bottom parameter values (textfields)
        bottom_min_mechanical_loading = new TextField();
        bottom_min_mechanical_loading.setText((new Double(some_global.getBottomMinMechanicalLoading())).toString());
        bottom_mechanical_loading = new TextField();
        bottom_mechanical_loading.setText((new Double(some_global.getBottomMechanicalLoading())).toString());
        bottom_min_thermal_influx = new TextField();
        bottom_min_thermal_influx.setText((new Double(some_global.getBottomMinThermalInflux())).toString());
        bottom_thermal_influx = new TextField();
        bottom_thermal_influx.setText((new Double(some_global.getBottomThermalInflux())).toString());
        bottom_bound_time_function_type = new TextField();
        bottom_bound_time_function_type.setText((new Byte(some_global.getBottomBoundTimeFunctionType())).toString());
        bottom_bound_load_time_portion  = new TextField();
        bottom_bound_load_time_portion.setText((new Double(some_global.getBottomBoundLoadTimePortion())).toString());
        bottom_bound_relax_time_portion  = new TextField();
        bottom_bound_relax_time_portion.setText((new Double(some_global.getBottomBoundRelaxTimePortion())).toString());
        // Left parameter values (textfields)
        left_min_mechanical_loading = new TextField();
        left_min_mechanical_loading.setText((new Double(some_global.getLeftMinMechanicalLoading())).toString());
        left_mechanical_loading = new TextField();
        left_mechanical_loading.setText((new Double(some_global.getLeftMechanicalLoading())).toString());
        left_min_thermal_influx = new TextField();
        left_min_thermal_influx.setText((new Double(some_global.getLeftMinThermalInflux())).toString());
        left_thermal_influx = new TextField();
        left_thermal_influx.setText((new Double(some_global.getLeftThermalInflux())).toString());
        left_bound_time_function_type = new TextField();
        left_bound_time_function_type.setText((new Byte(some_global.getLeftBoundTimeFunctionType())).toString());
        left_bound_load_time_portion  = new TextField();
        left_bound_load_time_portion.setText((new Double(some_global.getLeftBoundLoadTimePortion())).toString());
        left_bound_relax_time_portion  = new TextField();
        left_bound_relax_time_portion.setText((new Double(some_global.getLeftBoundRelaxTimePortion())).toString());
        // Right parameter values (textfields)
        right_min_mechanical_loading = new TextField();
        right_min_mechanical_loading.setText((new Double(some_global.getRightMinMechanicalLoading())).toString());
        right_mechanical_loading = new TextField();
        right_mechanical_loading.setText((new Double(some_global.getRightMechanicalLoading())).toString());
        right_min_thermal_influx = new TextField();
        right_min_thermal_influx.setText((new Double(some_global.getRightMinThermalInflux())).toString());
        right_thermal_influx = new TextField();
        right_thermal_influx.setText((new Double(some_global.getRightThermalInflux())).toString());
        right_bound_time_function_type = new TextField();
        right_bound_time_function_type.setText((new Byte(some_global.getRightBoundTimeFunctionType())).toString());
        right_bound_load_time_portion  = new TextField();
        right_bound_load_time_portion.setText((new Double(some_global.getRightBoundLoadTimePortion())).toString());
        right_bound_relax_time_portion  = new TextField();
        right_bound_relax_time_portion.setText((new Double(some_global.getRightBoundRelaxTimePortion())).toString());
        // Front parameter values (textfields)
        front_min_mechanical_loading = new TextField();
        front_min_mechanical_loading.setText((new Double(some_global.getFrontMinMechanicalLoading())).toString());
        front_mechanical_loading = new TextField();
        front_mechanical_loading.setText((new Double(some_global.getFrontMechanicalLoading())).toString());
        front_min_thermal_influx = new TextField();
        front_min_thermal_influx.setText((new Double(some_global.getFrontMinThermalInflux())).toString());
        front_thermal_influx = new TextField();
        front_thermal_influx.setText((new Double(some_global.getFrontThermalInflux())).toString());
        front_bound_time_function_type = new TextField();
        front_bound_time_function_type.setText((new Byte(some_global.getFrontBoundTimeFunctionType())).toString());
        front_bound_load_time_portion  = new TextField();
        front_bound_load_time_portion.setText((new Double(some_global.getFrontBoundLoadTimePortion())).toString());
        front_bound_relax_time_portion  = new TextField();
        front_bound_relax_time_portion.setText((new Double(some_global.getFrontBoundRelaxTimePortion())).toString());
        // Back parameter values (textfields)
        back_min_mechanical_loading = new TextField();
        back_min_mechanical_loading.setText((new Double(some_global.getBackMinMechanicalLoading())).toString());
        back_mechanical_loading = new TextField();
        back_mechanical_loading.setText((new Double(some_global.getBackMechanicalLoading())).toString());
        back_min_thermal_influx = new TextField();
        back_min_thermal_influx.setText((new Double(some_global.getBackMinThermalInflux())).toString());
        back_thermal_influx = new TextField();
        back_thermal_influx.setText((new Double(some_global.getBackThermalInflux())).toString());
        back_bound_time_function_type = new TextField();
        back_bound_time_function_type.setText((new Byte(some_global.getBackBoundTimeFunctionType())).toString());
        back_bound_load_time_portion  = new TextField();
        back_bound_load_time_portion.setText((new Double(some_global.getBackBoundLoadTimePortion())).toString());
        back_bound_relax_time_portion  = new TextField();
        back_bound_relax_time_portion.setText((new Double(some_global.getBackBoundRelaxTimePortion())).toString());
        
        /** Create helps (boundary names)
         */
        top = new Tab(UICommon.TOP_NAME);
        bottom = new Tab(UICommon.BOTTOM_NAME);
        left = new Tab(UICommon.LEFT_NAME);
        right = new Tab(UICommon.RIGHT_NAME);
        front = new Tab(UICommon.FRONT_NAME);
        back = new Tab(UICommon.BACK_NAME);
    }
    
    /** Create helps about adiabatic boundary or not
     */
    private void createAdiabaticInfo()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createAdiobaticInfo: start");
                
        // top
        top_time_function_type_name = new Label(UICommon.BOUND_TIME_FUNCTION_TYPE_NAME);
        top_load_time_portion_name = new Label(UICommon.BOUND_LOAD_TIME_PORTION_NAME);
        top_relax_time_portion_name = new Label(UICommon.BOUND_RELAX_TIME_PORTION_NAME);
        top_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        // bottom
        bottom_time_function_type_name = new Label(UICommon.BOUND_TIME_FUNCTION_TYPE_NAME);
        bottom_load_time_portion_name = new Label(UICommon.BOUND_LOAD_TIME_PORTION_NAME);
        bottom_relax_time_portion_name = new Label(UICommon.BOUND_RELAX_TIME_PORTION_NAME);
        bottom_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        // left
        left_time_function_type_name = new Label(UICommon.BOUND_TIME_FUNCTION_TYPE_NAME);
        left_load_time_portion_name = new Label(UICommon.BOUND_LOAD_TIME_PORTION_NAME);
        left_relax_time_portion_name = new Label(UICommon.BOUND_RELAX_TIME_PORTION_NAME);
        left_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        // right
        right_time_function_type_name = new Label(UICommon.BOUND_TIME_FUNCTION_TYPE_NAME);
        right_load_time_portion_name = new Label(UICommon.BOUND_LOAD_TIME_PORTION_NAME);
        right_relax_time_portion_name = new Label(UICommon.BOUND_RELAX_TIME_PORTION_NAME);
        right_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        // front
        front_time_function_type_name = new Label(UICommon.BOUND_TIME_FUNCTION_TYPE_NAME);
        front_load_time_portion_name = new Label(UICommon.BOUND_LOAD_TIME_PORTION_NAME);
        front_relax_time_portion_name = new Label(UICommon.BOUND_RELAX_TIME_PORTION_NAME);
        front_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        // back
        back_time_function_type_name = new Label(UICommon.BOUND_TIME_FUNCTION_TYPE_NAME);
        back_load_time_portion_name = new Label(UICommon.BOUND_LOAD_TIME_PORTION_NAME);
        back_relax_time_portion_name = new Label(UICommon.BOUND_RELAX_TIME_PORTION_NAME);
        back_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);        
    }

    /** Create box to choose material for non-adiabatic boundary
     */
    private void createNotAdiobaticMaterialBox()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createNotAdiobaticMaterialBox: start");

        // Create box for top boundary
        top_adiobatic_material = new Button();
        top_adiobatic_material.setText(UICommon.FIRST_CHOOSE_NAME);
        // If corresponding checkboxes is selected (mechanical loading
        // or thermal energy), then boundary became not adiabatic and
        // data about material must be loaded

        if(top_check_thermal_influx.isSelected() | this.top_check_mechanical_loading.isSelected())
        {
            // Load adiabatic material
            top_adiobatic_material.setText(some_global.getTopAdiabaticMaterial());
        }

        // Create box for bottom boundary
        bottom_adiobatic_material = new Button();
        bottom_adiobatic_material.setText(UICommon.FIRST_CHOOSE_NAME);
        
        // If corresponding checkboxes is selected (mechanical loading
        // or thermal energy), then boundary became not adiabatic and
        // data about material must be loaded
        if(bottom_check_thermal_influx.isSelected() | bottom_check_mechanical_loading.isSelected())
        {
            // Load adiabatic material
            bottom_adiobatic_material.setText(some_global.getBottomAdiabaticMaterial());
        }

        // Create box for left boundary
        left_adiobatic_material = new Button();
        left_adiobatic_material.setText(UICommon.FIRST_CHOOSE_NAME);
        
        // If corresponding checkboxes is selected (mechanical loading
        // or thermal energy), then boundary became not adiabatic and
        // data about material must be loaded
        if(left_check_thermal_influx.isSelected() | left_check_mechanical_loading.isSelected())
        {
            // Load adiabatic material
            left_adiobatic_material.setText(some_global.getLeftAdiabaticMaterial());
        }

        // Create box for right boundary
        right_adiobatic_material = new Button();
        right_adiobatic_material.setText(UICommon.FIRST_CHOOSE_NAME);

        // If corresponding checkboxes is selected (mechanical loading
        // or thermal energy), then boundary became not adiabatic and
        // data about material must be loaded
        if(right_check_thermal_influx.isSelected() | right_check_mechanical_loading.isSelected())
        {
            // Load adiabatic material
            right_adiobatic_material.setText(some_global.getRightAdiabaticMaterial());
        }

        // Create box for front boundary
        front_adiobatic_material = new Button();
        front_adiobatic_material.setText(UICommon.FIRST_CHOOSE_NAME);
        
        // If corresponding checkboxes is selected (mechanical loading
        // or thermal energy), then boundary became not adiabatic and
        // data about material must be loaded
        if(front_check_thermal_influx.isSelected() | front_check_mechanical_loading.isSelected())
        {
            // Load adiabatic material
            front_adiobatic_material.setText(some_global.getFrontAdiabaticMaterial());            
        }

        // Create box for back boundary
        back_adiobatic_material = new Button();
        back_adiobatic_material.setText(UICommon.FIRST_CHOOSE_NAME);
        
        // If corresponding checkboxes is selected (mechanical loading
        // or thermal energy), then boundary became not adiabatic and
        // data about material must be loaded
        if(back_check_thermal_influx.isSelected() | back_check_mechanical_loading.isSelected())
        {
            // Load adiabatic material            
            back_adiobatic_material.setText(some_global.getBackAdiabaticMaterial());         
        }
    }
    
    /** Create buttons: "Save as" and "OK" or "Cancel"
     * @param name - name of second button
     */    
    private void createButtonsForParamsOfBoundaryConditions()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createButtonsForParamsOfBoundaryConditions: start");

        
        button_save = new Button(UICommon.BUTTON_SAVE_NAME);
        button_ok = new Button(UICommon.BUTTON_OK_NAME);        
        
    }
    
    /** Add elements on frame
     */    
    private void addElementsForParametersOfBoundaryConditions()
    {
        System.out.println("RecCABoundaryConditionsParams: method: addElementsForParametersOfBoundaryConditions: start");
        
        tab_pane = new TabPane();
        top = new Tab("Top");
        left = new Tab("Left");
        right = new Tab("Right");
        bottom = new Tab("Bottom");
        back = new Tab("Back");
        front = new Tab("Front");
        
        top.setClosable(false);
        top.setContent(getTopTabLayout());
        
        left.setClosable(false);
        left.setContent(getLeftTabLayout());
        
        right.setClosable(false);
        right.setContent(getRightTabLayout());
        
        bottom.setClosable(false);
        bottom.setContent(getBottomTabLayout());
        
        back.setClosable(false);
        back.setContent(getBackTabLayout());
        
        front.setClosable(false);
        front.setContent(getFrontTabLayout());        
        
        tab_pane.getTabs().addAll(top, left, right, bottom, front, back);        
    }
    
    public GridPane getTopTabLayout(){
        
        Label lbl = new Label("Boundary Type:");
        
        Label max_lbl1 = new Label("Maximal Value:");
        Label min_lbl1 = new Label("Minimal Value:");
        
        Label max_lbl2 = new Label("Maximal Value:");
        Label min_lbl2 = new Label("Minimal Value:");
        
        Label lbl1 = new Label("Type of Time Function:");
        Label lbl2 = new Label("Loading Time Portion:");
        Label lbl3 = new Label("Relaxation Time Portion:");
        Label lbl4 = new Label("This Boundary is not adiabatic");
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 2, 5, 2));
        
        GridPane layout = new GridPane();
        
        GridPane.setConstraints(lbl, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_inner_bound, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_outer_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(top_check_mechanical_loading, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl1, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(top_mechanical_loading, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_min_mechanical_loading, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(top_stress, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_strain, 1, 6, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(top_check_thermal_influx, 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl2, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(top_thermal_influx, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_min_thermal_influx, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(top_temperature, 0, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_energy, 1, 11, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 0, 12, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl1, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl2, 0, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl3, 0, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(top_bound_time_function_type, 1, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(top_bound_load_time_portion, 1, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(top_bound_relax_time_portion, 1, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(sep4, 0, 16, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl4, 0, 17, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(top_adiobatic_material, 0, 18, 2, 1, HPos.RIGHT, VPos.CENTER);
        
        layout.getChildren().addAll(
                lbl,
                top_inner_bound, top_outer_bound,
                sep1, 
                top_check_mechanical_loading,
                max_lbl1, min_lbl1,
                top_mechanical_loading, top_min_mechanical_loading,
                top_stress, top_strain,
                sep2,
                top_check_thermal_influx,
                max_lbl2, min_lbl2,
                top_thermal_influx, top_min_thermal_influx,
                top_temperature, top_energy,
                sep3,
                lbl1, lbl2, lbl3,
                top_bound_time_function_type, 
                top_bound_load_time_portion,
                top_bound_relax_time_portion,
                sep4,
                top_adiobatic_material
        );
        
        layout.setPadding(new Insets(30, 30, 30, 30));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(8.0d);
        layout.setVgap(8.0d);
        
        return layout;
    }
    
    public GridPane getBottomTabLayout(){
        
        Label lbl = new Label("Boundary Type:");
        
        Label max_lbl1 = new Label("Maximal Value:");
        Label min_lbl1 = new Label("Minimal Value:");
        
        Label max_lbl2 = new Label("Maximal Value:");
        Label min_lbl2 = new Label("Minimal Value:");
        
        Label lbl1 = new Label("Type of Time Function:");
        Label lbl2 = new Label("Loading Time Portion:");
        Label lbl3 = new Label("Relaxation Time Portion:");
        Label lbl4 = new Label("This Boundary is not adiabatic");
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 2, 5, 2));
        
        GridPane layout = new GridPane();
        
        GridPane.setConstraints(lbl, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_inner_bound, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_outer_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bottom_check_mechanical_loading, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl1, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(bottom_mechanical_loading, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_min_mechanical_loading, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bottom_stress, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_strain, 1, 6, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bottom_check_thermal_influx, 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl2, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(bottom_thermal_influx, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_min_thermal_influx, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bottom_temperature, 0, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_energy, 1, 11, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 0, 12, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl1, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl2, 0, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl3, 0, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(bottom_bound_time_function_type, 1, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(bottom_bound_load_time_portion, 1, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(bottom_bound_relax_time_portion, 1, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(sep4, 0, 16, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl4, 0, 17, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(bottom_adiobatic_material, 0, 18, 2, 1, HPos.RIGHT, VPos.CENTER);
        
        layout.getChildren().addAll(
                lbl,
                bottom_inner_bound, bottom_outer_bound,
                sep1, 
                bottom_check_mechanical_loading,
                max_lbl1, min_lbl1,
                bottom_mechanical_loading, bottom_min_mechanical_loading,
                bottom_stress, bottom_strain,
                sep2,
                bottom_check_thermal_influx,
                max_lbl2, min_lbl2,
                bottom_thermal_influx, bottom_min_thermal_influx,
                bottom_temperature, bottom_energy,
                sep3,
                lbl1, lbl2, lbl3,
                bottom_bound_time_function_type, 
                bottom_bound_load_time_portion,
                bottom_bound_relax_time_portion,
                sep4,
                bottom_adiobatic_material
        );
        
        layout.setPadding(new Insets(30, 30, 30, 30));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(8.0d);
        layout.setVgap(8.0d);
        
        return layout;
    }
    
    public GridPane getLeftTabLayout(){
        Label lbl = new Label("Boundary Type:");
        
        Label max_lbl1 = new Label("Maximal Value:");
        Label min_lbl1 = new Label("Minimal Value:");
        
        Label max_lbl2 = new Label("Maximal Value:");
        Label min_lbl2 = new Label("Minimal Value:");
        
        Label lbl1 = new Label("Type of Time Function:");
        Label lbl2 = new Label("Loading Time Portion:");
        Label lbl3 = new Label("Relaxation Time Portion:");
        Label lbl4 = new Label("This Boundary is not adiabatic");
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 2, 5, 2));
        
        GridPane layout = new GridPane();
        
        GridPane.setConstraints(lbl, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_inner_bound, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_outer_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(left_check_mechanical_loading, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl1, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(left_mechanical_loading, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_min_mechanical_loading, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(left_stress, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_strain, 1, 6, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(left_check_thermal_influx, 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl2, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(left_thermal_influx, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_min_thermal_influx, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(left_temperature, 0, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_energy, 1, 11, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 0, 12, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl1, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl2, 0, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl3, 0, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(left_bound_time_function_type, 1, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(left_bound_load_time_portion, 1, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(left_bound_relax_time_portion, 1, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(sep4, 0, 16, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl4, 0, 17, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(left_adiobatic_material, 0, 18, 2, 1, HPos.RIGHT, VPos.CENTER);
        
        layout.getChildren().addAll(
                lbl,
                left_inner_bound, left_outer_bound,
                sep1, 
                left_check_mechanical_loading,
                max_lbl1, min_lbl1,
                left_mechanical_loading, left_min_mechanical_loading,
                left_stress, left_strain,
                sep2,
                left_check_thermal_influx,
                max_lbl2, min_lbl2,
                left_thermal_influx, left_min_thermal_influx,
                left_temperature, left_energy,
                sep3,
                lbl1, lbl2, lbl3,
                left_bound_time_function_type, 
                left_bound_load_time_portion,
                left_bound_relax_time_portion,
                sep4,
                left_adiobatic_material
        );
        
        layout.setPadding(new Insets(30, 30, 30, 30));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(8.0d);
        layout.setVgap(8.0d);
        
        return layout;
    }
    
    public GridPane getRightTabLayout(){
        Label lbl = new Label("Boundary Type:");
        
        Label max_lbl1 = new Label("Maximal Value:");
        Label min_lbl1 = new Label("Minimal Value:");
        
        Label max_lbl2 = new Label("Maximal Value:");
        Label min_lbl2 = new Label("Minimal Value:");
        
        Label lbl1 = new Label("Type of Time Function:");
        Label lbl2 = new Label("Loading Time Portion:");
        Label lbl3 = new Label("Relaxation Time Portion:");
        Label lbl4 = new Label("This Boundary is not adiabatic");
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 2, 5, 2));
        
        GridPane layout = new GridPane();
        
        GridPane.setConstraints(lbl, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_inner_bound, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_outer_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(right_check_mechanical_loading, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl1, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(right_mechanical_loading, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_min_mechanical_loading, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(right_stress, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_strain, 1, 6, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(right_check_thermal_influx, 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl2, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(right_thermal_influx, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_min_thermal_influx, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(right_temperature, 0, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_energy, 1, 11, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 0, 12, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl1, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl2, 0, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl3, 0, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(right_bound_time_function_type, 1, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(right_bound_load_time_portion, 1, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(right_bound_relax_time_portion, 1, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(sep4, 0, 16, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl4, 0, 17, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(right_adiobatic_material, 0, 18, 2, 1, HPos.RIGHT, VPos.CENTER);
        
        layout.getChildren().addAll(
                lbl,
                right_inner_bound, right_outer_bound,
                sep1, 
                right_check_mechanical_loading,
                max_lbl1, min_lbl1,
                right_mechanical_loading, right_min_mechanical_loading,
                right_stress, right_strain,
                sep2,
                right_check_thermal_influx,
                max_lbl2, min_lbl2,
                right_thermal_influx, right_min_thermal_influx,
                right_temperature, right_energy,
                sep3,
                lbl1, lbl2, lbl3,
                right_bound_time_function_type, 
                right_bound_load_time_portion,
                right_bound_relax_time_portion,
                sep4,
                right_adiobatic_material
        );
        
        layout.setPadding(new Insets(30, 30, 30, 30));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(8.0d);
        layout.setVgap(8.0d);
        
        return layout;
    }
    
    public GridPane getFrontTabLayout(){
        Label lbl = new Label("Boundary Type:");
        
        Label max_lbl1 = new Label("Maximal Value:");
        Label min_lbl1 = new Label("Minimal Value:");
        
        Label max_lbl2 = new Label("Maximal Value:");
        Label min_lbl2 = new Label("Minimal Value:");
        
        Label lbl1 = new Label("Type of Time Function:");
        Label lbl2 = new Label("Loading Time Portion:");
        Label lbl3 = new Label("Relaxation Time Portion:");
        Label lbl4 = new Label("This Boundary is not adiabatic");
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 2, 5, 2));
        
        GridPane layout = new GridPane();
        
        GridPane.setConstraints(lbl, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_inner_bound, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_outer_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(front_check_mechanical_loading, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl1, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(front_mechanical_loading, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_min_mechanical_loading, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(front_stress, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_strain, 1, 6, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(front_check_thermal_influx, 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl2, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(front_thermal_influx, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_min_thermal_influx, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(front_temperature, 0, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_energy, 1, 11, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 0, 12, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl1, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl2, 0, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl3, 0, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(front_bound_time_function_type, 1, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(front_bound_load_time_portion, 1, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(front_bound_relax_time_portion, 1, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(sep4, 0, 16, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl4, 0, 17, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(front_adiobatic_material, 0, 18, 2, 1, HPos.RIGHT, VPos.CENTER);
        
        layout.getChildren().addAll(
                lbl,
                front_inner_bound, front_outer_bound,
                sep1, 
                front_check_mechanical_loading,
                max_lbl1, min_lbl1,
                front_mechanical_loading, front_min_mechanical_loading,
                front_stress, front_strain,
                sep2,
                front_check_thermal_influx,
                max_lbl2, min_lbl2,
                front_thermal_influx, front_min_thermal_influx,
                front_temperature, front_energy,
                sep3,
                lbl1, lbl2, lbl3,
                front_bound_time_function_type, 
                front_bound_load_time_portion,
                front_bound_relax_time_portion,
                sep4,
                front_adiobatic_material
        );
        
        layout.setPadding(new Insets(30, 30, 30, 30));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(8.0d);
        layout.setVgap(8.0d);
        
        return layout;
    }
    
    public GridPane getBackTabLayout(){
        Label lbl = new Label("Boundary Type:");
        
        Label max_lbl1 = new Label("Maximal Value:");
        Label min_lbl1 = new Label("Minimal Value:");
        
        Label max_lbl2 = new Label("Maximal Value:");
        Label min_lbl2 = new Label("Minimal Value:");
        
        Label lbl1 = new Label("Type of Time Function:");
        Label lbl2 = new Label("Loading Time Portion:");
        Label lbl3 = new Label("Relaxation Time Portion:");
        Label lbl4 = new Label("This Boundary is not adiabatic");
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(5, 2, 5, 2));
        
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 2, 5, 2));
        
        GridPane layout = new GridPane();
        
        GridPane.setConstraints(lbl, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_inner_bound, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_outer_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(back_check_mechanical_loading, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl1, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(back_mechanical_loading, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_min_mechanical_loading, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(back_stress, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_strain, 1, 6, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(back_check_thermal_influx, 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_lbl2, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(back_thermal_influx, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_min_thermal_influx, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(back_temperature, 0, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_energy, 1, 11, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 0, 12, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl1, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl2, 0, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lbl3, 0, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(back_bound_time_function_type, 1, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(back_bound_load_time_portion, 1, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(back_bound_relax_time_portion, 1, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(sep4, 0, 16, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(lbl4, 0, 17, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(back_adiobatic_material, 0, 18, 2, 1, HPos.RIGHT, VPos.CENTER);
        
        layout.getChildren().addAll(
                lbl,
                back_inner_bound, back_outer_bound,
                sep1, 
                back_check_mechanical_loading,
                max_lbl1, min_lbl1,
                back_mechanical_loading, back_min_mechanical_loading,
                back_stress, back_strain,
                sep2,
                back_check_thermal_influx,
                max_lbl2, min_lbl2,
                back_thermal_influx, back_min_thermal_influx,
                back_temperature, back_energy,
                sep3,
                lbl1, lbl2, lbl3,
                back_bound_time_function_type, 
                back_bound_load_time_portion,
                back_bound_relax_time_portion,
                sep4,
                back_adiobatic_material
        );
        
        layout.setPadding(new Insets(30, 30, 30, 30));
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(8.0d);
        layout.setVgap(8.0d);
        
        return layout;
    }
    
    /** Create saving dialog window for saving boundary parameters.
     */    
    private void savingDialofOfParamsForBoundaryConditions()
    {
        System.out.println("RecCABoundaryConditionsParams: method: savingDialofOfParamsForBoundaryConditions: start");
        
        this.close();
        SavingDialog save = new SavingDialog(textfield_for_file_location);
        save.save_btn.setOnAction(e -> {
            if(isNameCorrect(textfield_for_file_location))
            {
                try{
                    new Alert(AlertType.INFORMATION, "File successfuly created").showAndWait();
                    /** Remember path of chosen boundary conditions
                    */
                   transfer_data_bank.getUIInterface().setBoundCondPath(textfield_for_file_location.getText());

                   /** Save input parameters in corresponding file 
                    */
                   saving = new SaveParams(transfer_data_bank);
                   save.close();
                }
                catch(Exception ex){
                    new Alert(AlertType.ERROR, "Failed to create file").showAndWait();
                }
            }
        });
        
    }
    
    
//    /** The method creates dialog window for choice of type of function 
//     * determining dependence of boundary parameter of cell on its coordinates.
//     */    
//    private int savingDialofOfParamsForTypeOfBoundaryFunction()
//    {
//        System.out.println("RecCABoundaryConditionsParams: method: savingDialofOfParamsForTypeOfBoundaryFunction: start");
//        
//        // Creation of dialog window for choice of type of function 
//        // determining dependence of boundary parameter of cell on its coordinates
//        RecCA_BoundaryFunctionTypes bound_function_types = new RecCA_BoundaryFunctionTypes(transfer_data_bank);
//        
//        return transfer_data_bank.getUIBoundaryCondition().getBoundaryFunctionType();//bound_function_types.getFunctionType();
//    }
//    
    /*
     * Create checkboxes for choosing what parameters ("Mechanical loading",
     * "Thermal influx") input in corresponding side of boundary
     * ("Top", "Bottom", "Left", "Right", "Front" or "Back")
     */
    
    private void createCheckBoxesForBoundary()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createCheckBoxesForBoundary: start");

        // top
        top_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        top_check_mechanical_loading.setSelected(some_global.getTopCheckMechanicalLoading());

        top_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        top_check_thermal_influx.setSelected(some_global.getTopCheckThermalInflux());

        // bottom
        bottom_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        bottom_check_mechanical_loading.setSelected(some_global.getBottomCheckMechanicalLoading());
        bottom_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        bottom_check_thermal_influx.setSelected(some_global.getBottomCheckThermalInflux());

        // left
        left_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        left_check_mechanical_loading.setSelected(some_global.getLeftCheckMechanicalLoading());
        left_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        left_check_thermal_influx.setSelected(some_global.getLeftCheckThermalInflux());

        // right
        right_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        right_check_mechanical_loading.setSelected(some_global.getRightCheckMechanicalLoading());
        right_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        right_check_thermal_influx.setSelected(some_global.getRightCheckThermalInflux());

        // front
        front_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        front_check_mechanical_loading.setSelected(some_global.getFrontCheckMechanicalLoading());

        front_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        front_check_thermal_influx.setSelected(some_global.getFrontCheckThermalInflux());

        // back
        back_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        back_check_mechanical_loading.setSelected(some_global.getBackCheckMechanicalLoading());

        back_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        back_check_thermal_influx.setSelected(some_global.getBackCheckThermalInflux());
        

    }

    /** Create radio buttons for choose in mechanical loading: stress
     * or strain, and in thermal influx: temperature or energy
     */
    private void createTypeTaskChoose()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createTypeTaskChoose: start");

        // Create radio buttons for choose task type
        top_stress = new RadioButton(UICommon.STRESS_R_B_NAME);
        top_strain = new RadioButton(UICommon.STRAIN_R_B_NAME);
        top_temperature = new RadioButton(UICommon.TEMPERATURE_R_B_NAME);
        top_energy = new RadioButton(UICommon.ENERGY_R_B_NAME);

        bottom_stress = new RadioButton(UICommon.STRESS_R_B_NAME);
        bottom_strain = new RadioButton(UICommon.STRAIN_R_B_NAME);
        bottom_temperature = new RadioButton(UICommon.TEMPERATURE_R_B_NAME);
        bottom_energy = new RadioButton(UICommon.ENERGY_R_B_NAME);

        left_stress = new RadioButton(UICommon.STRESS_R_B_NAME);
        left_strain = new RadioButton(UICommon.STRAIN_R_B_NAME);
        left_temperature = new RadioButton(UICommon.TEMPERATURE_R_B_NAME);
        left_energy = new RadioButton(UICommon.ENERGY_R_B_NAME);

        right_stress = new RadioButton(UICommon.STRESS_R_B_NAME);
        right_strain = new RadioButton(UICommon.STRAIN_R_B_NAME);
        right_temperature = new RadioButton(UICommon.TEMPERATURE_R_B_NAME);
        right_energy = new RadioButton(UICommon.ENERGY_R_B_NAME);

        front_stress = new RadioButton(UICommon.STRESS_R_B_NAME);
        front_strain = new RadioButton(UICommon.STRAIN_R_B_NAME);
        front_temperature = new RadioButton(UICommon.TEMPERATURE_R_B_NAME);
        front_energy = new RadioButton(UICommon.ENERGY_R_B_NAME);

        back_stress = new RadioButton(UICommon.STRESS_R_B_NAME);
        back_strain = new RadioButton(UICommon.STRAIN_R_B_NAME);
        back_temperature = new RadioButton(UICommon.TEMPERATURE_R_B_NAME);
        back_energy = new RadioButton(UICommon.ENERGY_R_B_NAME);

        // Create corresponding button groups
        top_mechanical_loading_group = new ToggleGroup();
        top_thermal_influx_group = new ToggleGroup();
        
        top_mechanical_loading_group.getToggles().addAll(top_stress, top_strain);
        top_thermal_influx_group.getToggles().addAll(top_temperature, top_energy);
                
        bottom_mechanical_loading_group = new ToggleGroup();
        bottom_thermal_influx_group = new ToggleGroup();

        bottom_mechanical_loading_group.getToggles().addAll(bottom_stress, bottom_strain);
        bottom_thermal_influx_group.getToggles().addAll(bottom_temperature, bottom_energy);
        
        left_mechanical_loading_group = new ToggleGroup();
        left_thermal_influx_group = new ToggleGroup();
        
        left_mechanical_loading_group.getToggles().addAll(left_stress, left_strain);
        left_thermal_influx_group.getToggles().addAll(left_temperature, left_energy);

        right_mechanical_loading_group = new ToggleGroup();
        right_thermal_influx_group = new ToggleGroup();
        
        right_mechanical_loading_group.getToggles().addAll(right_stress, right_strain);
        right_thermal_influx_group.getToggles().addAll(right_temperature, right_energy);
        
        front_mechanical_loading_group = new ToggleGroup();
        front_thermal_influx_group = new ToggleGroup();
        
        front_mechanical_loading_group.getToggles().addAll(front_stress, front_strain);
        front_thermal_influx_group.getToggles().addAll(front_temperature, front_energy);
        
        back_mechanical_loading_group = new ToggleGroup();
        back_thermal_influx_group = new ToggleGroup();
        
        back_mechanical_loading_group.getToggles().addAll(back_stress, back_strain);
        back_thermal_influx_group.getToggles().addAll(back_temperature, back_energy);
        
        // Load selected values from boundary condition data bank
        top_stress.setSelected(some_global.getTopStress());
        top_strain.setSelected(some_global.getTopStrain());
        top_temperature.setSelected(some_global.getTopTemperature());
        top_energy.setSelected(some_global.getTopEnergy());

        bottom_stress.setSelected(some_global.getBottomStress());
        bottom_strain.setSelected(some_global.getBottomStrain());
        bottom_temperature.setSelected(some_global.getBottomTemperature());
        bottom_energy.setSelected(some_global.getBottomEnergy());

        left_stress.setSelected(some_global.getLeftStress());
        left_strain.setSelected(some_global.getLeftStrain());
        left_temperature.setSelected(some_global.getLeftTemperature());
        left_energy.setSelected(some_global.getLeftEnergy());

        right_stress.setSelected(some_global.getRightStress());
        right_strain.setSelected(some_global.getRightStrain());
        right_temperature.setSelected(some_global.getRightTemperature());
        right_energy.setSelected(some_global.getRightEnergy());

        front_stress.setSelected(some_global.getFrontStress());
        front_strain.setSelected(some_global.getFrontStrain());
        front_temperature.setSelected(some_global.getFrontTemperature());
        front_energy.setSelected(some_global.getFrontEnergy());

        back_stress.setSelected(some_global.getBackStress());
        back_strain.setSelected(some_global.getBackStrain());
        back_temperature.setSelected(some_global.getBackTemperature());
        back_energy.setSelected(some_global.getBackEnergy());
        
        top_inner_bound = new RadioButton("Inner Boundary");
        top_outer_bound = new RadioButton("Outer Boundary");
        top_group = new ToggleGroup();
        top_group.getToggles().addAll(top_inner_bound, top_outer_bound);
        
        bottom_inner_bound = new RadioButton("Inner Boundary");
        bottom_outer_bound = new RadioButton("Outer Boundary");
        bottom_group = new ToggleGroup();
        bottom_group.getToggles().addAll(bottom_inner_bound, bottom_outer_bound);
        
        left_inner_bound = new RadioButton("Inner Boundary");
        left_outer_bound = new RadioButton("Outer Boundary");
        left_group = new ToggleGroup();
        left_group.getToggles().addAll(left_inner_bound, left_outer_bound);
        
        right_inner_bound = new RadioButton("Inner Boundary");
        right_outer_bound = new RadioButton("Outer Boundary");
        right_group = new ToggleGroup();
        right_group.getToggles().addAll(right_inner_bound, right_outer_bound);
        
        front_inner_bound = new RadioButton("Inner Boundary");
        front_outer_bound = new RadioButton("Outer Boundary");
        front_group = new ToggleGroup();
        front_group.getToggles().addAll(front_inner_bound, front_outer_bound);
        
        back_inner_bound = new RadioButton("Inner Boundary");
        back_outer_bound = new RadioButton("Outer Boundary");
        back_group = new ToggleGroup();
        back_group.getToggles().addAll(back_inner_bound, back_outer_bound);
    }

    /** Check selected checkboxes,
     * if check box is selected, then corresponding
     * parameter value and task type can be changed and vice versa
     */
    private void setCheckedPositions()
    {
        System.out.println("RecCABoundaryConditionsParams: method: setCheckedPositions: start");

        // If top mechanical loading is selected, then task types:
        // stress and strain became editable and vice versa
        
        top_min_mechanical_loading.disableProperty().bind(top_check_mechanical_loading.selectedProperty().not());
        top_mechanical_loading.disableProperty().bind(top_check_mechanical_loading.selectedProperty().not());
        top_stress.disableProperty().bind(top_check_mechanical_loading.selectedProperty().not());
        top_strain.disableProperty().bind(top_check_mechanical_loading.selectedProperty().not());
        
        // If top thermal influx is selected, then task types:
        // temperature and energy became editable and vice versa
        top_min_thermal_influx.disableProperty().bind(top_check_thermal_influx.selectedProperty().not());
        top_thermal_influx.disableProperty().bind(top_check_thermal_influx.selectedProperty().not());
        top_temperature.disableProperty().bind(top_check_thermal_influx.selectedProperty().not());
        top_energy.disableProperty().bind(top_check_thermal_influx.selectedProperty().not());
        
        // If top mechanical loading or top thermal influx is selected,
        // then corresponding help about adiabatic boundary
        // or not must be changed and box for choose material
        // must became visible or not
        
        top_adiobatic_material.visibleProperty().bind(top_check_mechanical_loading.selectedProperty().or(top_check_thermal_influx.selectedProperty()));
        top_bound_time_function_type.editableProperty().bind(top_check_mechanical_loading.selectedProperty().or(top_check_thermal_influx.selectedProperty()));
        top_bound_load_time_portion.editableProperty().bind(top_check_mechanical_loading.selectedProperty().or(top_check_thermal_influx.selectedProperty()));
        top_bound_relax_time_portion.editableProperty().bind(top_check_mechanical_loading.selectedProperty().or(top_check_thermal_influx.selectedProperty()));
        
        top_check_mechanical_loading.setOnAction(e -> {
            if(top_check_mechanical_loading.isSelected()){
                top_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                top_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        top_check_thermal_influx.setOnAction(e -> {
            if(top_check_thermal_influx.isSelected()){
                top_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                top_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        // If bottom mechanical loading is selected, then task types:
        // stress and strain became editable and vice versa
        bottom_min_mechanical_loading.disableProperty().bind(bottom_check_mechanical_loading.selectedProperty().not());
        bottom_mechanical_loading.disableProperty().bind(bottom_check_mechanical_loading.selectedProperty().not());
        bottom_stress.disableProperty().bind(bottom_check_mechanical_loading.selectedProperty().not());
        bottom_strain.disableProperty().bind(bottom_check_mechanical_loading.selectedProperty().not());
        
        // If bottom thermal influx is selected, then task types:
        // temperature and energy became editable and vice versa
        bottom_min_thermal_influx.disableProperty().bind(bottom_check_thermal_influx.selectedProperty().not());
        bottom_thermal_influx.disableProperty().bind(bottom_check_thermal_influx.selectedProperty().not());
        bottom_temperature.disableProperty().bind(bottom_check_thermal_influx.selectedProperty().not());
        bottom_energy.disableProperty().bind(bottom_check_thermal_influx.selectedProperty().not());
        
        // If bottom mechanical loading or bottom thermal influx is selected,
        // then corresponding help about adiabatic boundary
        // or not must be changed and box for choose material
        // must became visible or not
        bottom_adiobatic_material.visibleProperty().bind(bottom_check_mechanical_loading.selectedProperty().or(bottom_check_thermal_influx.selectedProperty()));
        bottom_bound_time_function_type.editableProperty().bind(bottom_check_mechanical_loading.selectedProperty().or(bottom_check_thermal_influx.selectedProperty()));
        bottom_bound_load_time_portion.editableProperty().bind(bottom_check_mechanical_loading.selectedProperty().or(bottom_check_thermal_influx.selectedProperty()));
        bottom_bound_relax_time_portion.editableProperty().bind(bottom_check_mechanical_loading.selectedProperty().or(bottom_check_thermal_influx.selectedProperty()));
        
        bottom_check_mechanical_loading.setOnAction(e -> {
            if(bottom_check_mechanical_loading.isSelected()){
                bottom_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                bottom_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        bottom_check_thermal_influx.setOnAction(e -> {
            if(bottom_check_thermal_influx.isSelected()){
                bottom_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                bottom_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        // If left mechanical loading is selected, then task types:
        // stress and strain became editable and vice versa
        left_min_mechanical_loading.disableProperty().bind(left_check_mechanical_loading.selectedProperty().not());
        left_mechanical_loading.disableProperty().bind(left_check_mechanical_loading.selectedProperty().not());
        left_stress.disableProperty().bind(left_check_mechanical_loading.selectedProperty().not());
        left_strain.disableProperty().bind(left_check_mechanical_loading.selectedProperty().not());
        
        // If left thermal influx is selected, then task types:
        // temperature and energy became editable and vice versa
        left_min_thermal_influx.disableProperty().bind(left_check_thermal_influx.selectedProperty().not());
        left_thermal_influx.disableProperty().bind(left_check_thermal_influx.selectedProperty().not());
        left_temperature.disableProperty().bind(left_check_thermal_influx.selectedProperty().not());
        left_energy.disableProperty().bind(left_check_thermal_influx.selectedProperty().not());
        
        // If left mechanical loading or left thermal influx is selected,
        // then corresponding help about adiabatic boundary
        // or not must be changed and box for choose material
        // must became visible or not
        
        left_adiobatic_material.visibleProperty().bind(left_check_mechanical_loading.selectedProperty().or(left_check_thermal_influx.selectedProperty()));
        left_bound_time_function_type.editableProperty().bind(left_check_mechanical_loading.selectedProperty().or(left_check_thermal_influx.selectedProperty()));
        left_bound_load_time_portion.editableProperty().bind(left_check_mechanical_loading.selectedProperty().or(left_check_thermal_influx.selectedProperty()));
        left_bound_relax_time_portion.editableProperty().bind(left_check_mechanical_loading.selectedProperty().or(left_check_thermal_influx.selectedProperty()));
        
        left_check_mechanical_loading.setOnAction(e -> {
            if(left_check_mechanical_loading.isSelected()){
                left_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                left_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        left_check_thermal_influx.setOnAction(e -> {
            if(left_check_thermal_influx.isSelected()){
                left_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                left_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        // If right mechanical loading is selected, then task types:
        // stress and strain became editable and vice versa
        right_min_mechanical_loading.disableProperty().bind(right_check_mechanical_loading.selectedProperty().not());
        right_mechanical_loading.disableProperty().bind(right_check_mechanical_loading.selectedProperty().not());
        right_stress.disableProperty().bind(right_check_mechanical_loading.selectedProperty().not());
        right_strain.disableProperty().bind(right_check_mechanical_loading.selectedProperty().not());
        
        // If right thermal influx is selected, then task types:
        // temperature and energy became editable and vice versa
        right_min_thermal_influx.disableProperty().bind(right_check_thermal_influx.selectedProperty().not());
        right_thermal_influx.disableProperty().bind(right_check_thermal_influx.selectedProperty().not());
        right_temperature.disableProperty().bind(right_check_thermal_influx.selectedProperty().not());
        right_energy.disableProperty().bind(right_check_thermal_influx.selectedProperty().not());
        
        // If right mechanical loading or right thermal influx is selected,
        // then corresponding help about adiabatic boundary
        // or not must be changed and box for choose material
        // must became visible or not
        right_adiobatic_material.visibleProperty().bind(right_check_mechanical_loading.selectedProperty().or(right_check_thermal_influx.selectedProperty()));
        right_bound_time_function_type.editableProperty().bind(right_check_mechanical_loading.selectedProperty().or(right_check_thermal_influx.selectedProperty()));
        right_bound_load_time_portion.editableProperty().bind(right_check_mechanical_loading.selectedProperty().or(right_check_thermal_influx.selectedProperty()));
        right_bound_relax_time_portion.editableProperty().bind(right_check_mechanical_loading.selectedProperty().or(right_check_thermal_influx.selectedProperty()));
        
        right_check_mechanical_loading.setOnAction(e -> {
            if(right_check_mechanical_loading.isSelected()){
                right_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                right_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        right_check_thermal_influx.setOnAction(e -> {
            if(right_check_thermal_influx.isSelected()){
                right_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                right_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });

        // If front mechanical loading is selected, then task types:
        // stress and strain became editable and vice versa
        front_min_mechanical_loading.disableProperty().bind(front_check_mechanical_loading.selectedProperty().not());
        front_mechanical_loading.disableProperty().bind(front_check_mechanical_loading.selectedProperty().not());
        front_stress.disableProperty().bind(front_check_mechanical_loading.selectedProperty().not());
        front_strain.disableProperty().bind(front_check_mechanical_loading.selectedProperty().not());
        
        // If front thermal influx is selected, then task types:
        // temperature and energy became editable and vice versa
        front_min_thermal_influx.disableProperty().bind(front_check_thermal_influx.selectedProperty().not());
        front_thermal_influx.disableProperty().bind(front_check_thermal_influx.selectedProperty().not());
        front_temperature.disableProperty().bind(front_check_thermal_influx.selectedProperty().not());
        front_energy.disableProperty().bind(front_check_thermal_influx.selectedProperty().not());
        
        // If front mechanical loading or front thermal influx is selected,
        // then corresponding help about adiabatic boundary
        // or not must be changed and box for choose material
        // must became visible or not
        
        front_adiobatic_material.visibleProperty().bind(front_check_mechanical_loading.selectedProperty().or(front_check_thermal_influx.selectedProperty()));
        front_bound_time_function_type.editableProperty().bind(front_check_mechanical_loading.selectedProperty().or(front_check_thermal_influx.selectedProperty()));
        front_bound_load_time_portion.editableProperty().bind(front_check_mechanical_loading.selectedProperty().or(front_check_thermal_influx.selectedProperty()));
        front_bound_relax_time_portion.editableProperty().bind(front_check_mechanical_loading.selectedProperty().or(front_check_thermal_influx.selectedProperty()));
        
        front_check_mechanical_loading.setOnAction(e -> {
            if(front_check_mechanical_loading.isSelected()){
                front_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                front_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        front_check_thermal_influx.setOnAction(e -> {
            if(front_check_thermal_influx.isSelected()){
                front_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                front_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        
        // If back mechanical loading is selected, then task types:
        // stress and strain became editable and vice versa
        back_min_mechanical_loading.disableProperty().bind(back_check_mechanical_loading.selectedProperty().not());
        back_mechanical_loading.disableProperty().bind(back_check_mechanical_loading.selectedProperty().not());
        back_stress.disableProperty().bind(back_check_mechanical_loading.selectedProperty().not());
        back_strain.disableProperty().bind(back_check_mechanical_loading.selectedProperty().not());
        
        // If back thermal influx is selected, then task types:
        // temperature and energy became editable and vice versa
        back_min_thermal_influx.disableProperty().bind(back_check_thermal_influx.selectedProperty().not());
        back_thermal_influx.disableProperty().bind(back_check_thermal_influx.selectedProperty().not());
        back_temperature.disableProperty().bind(back_check_thermal_influx.selectedProperty().not());
        back_energy.disableProperty().bind(back_check_thermal_influx.selectedProperty().not());
        
        // If back mechanical loading or back thermal influx is selected,
        // then corresponding help about adiabatic boundary
        // or not must be changed and box for choose material
        // must became visible or not
        
        back_adiobatic_material.visibleProperty().bind(back_check_mechanical_loading.selectedProperty().or(back_check_thermal_influx.selectedProperty()));
        back_bound_time_function_type.editableProperty().bind(back_check_mechanical_loading.selectedProperty().or(back_check_thermal_influx.selectedProperty()));
        back_bound_load_time_portion.editableProperty().bind(back_check_mechanical_loading.selectedProperty().or(back_check_thermal_influx.selectedProperty()));
        back_bound_relax_time_portion.editableProperty().bind(back_check_mechanical_loading.selectedProperty().or(back_check_thermal_influx.selectedProperty()));
        
        back_check_mechanical_loading.setOnAction(e -> {
            if(back_check_mechanical_loading.isSelected()){
                back_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                back_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });
        back_check_thermal_influx.setOnAction(e -> {
            if(back_check_thermal_influx.isSelected()){
                back_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                
            }
            else{
                back_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);                
            }
        });        
    }

    /** Remember inputed parameters in boundary conditions data bank
     */
    public void inputParams()
    {
        System.out.println("RecCABoundaryConditionsParams: method: inputParams: start");

        /** set inputed check boxes
         */
        some_global.setTopCheckMechanicalLoading(Boolean.valueOf(top_check_mechanical_loading.isSelected()));
        some_global.setTopCheckThermalInflux(Boolean.valueOf(top_check_thermal_influx.isSelected()));

        some_global.setBottomCheckMechanicalLoading(Boolean.valueOf(bottom_check_mechanical_loading.isSelected()));
        some_global.setBottomCheckThermalInflux(Boolean.valueOf(bottom_check_thermal_influx.isSelected()));

        some_global.setLeftCheckMechanicalLoading(Boolean.valueOf(left_check_mechanical_loading.isSelected()));
        some_global.setLeftCheckThermalInflux(Boolean.valueOf(left_check_thermal_influx.isSelected()));

        some_global.setRightCheckMechanicalLoading(Boolean.valueOf(right_check_mechanical_loading.isSelected()));
        some_global.setRightCheckThermalInflux(Boolean.valueOf(right_check_thermal_influx.isSelected()));

        some_global.setFrontCheckMechanicalLoading(Boolean.valueOf(front_check_mechanical_loading.isSelected()));
        some_global.setFrontCheckThermalInflux(Boolean.valueOf(front_check_thermal_influx.isSelected()));

        some_global.setBackCheckMechanicalLoading(Boolean.valueOf(back_check_mechanical_loading.isSelected()));
        some_global.setBackCheckThermalInflux(Boolean.valueOf(back_check_thermal_influx.isSelected()));

        // Remember task types
        some_global.setTopStress(top_stress.isSelected());
        some_global.setTopStrain(top_strain.isSelected());
        some_global.setTopTemperature(top_temperature.isSelected());
        some_global.setTopEnergy(top_energy.isSelected());

        some_global.setBottomStress(bottom_stress.isSelected());
        some_global.setBottomStrain(bottom_strain.isSelected());
        some_global.setBottomTemperature(bottom_temperature.isSelected());
        some_global.setBottomEnergy(bottom_energy.isSelected());

        some_global.setLeftStress(left_stress.isSelected());
        some_global.setLeftStrain(left_strain.isSelected());
        some_global.setLeftTemperature(left_temperature.isSelected());
        some_global.setLeftEnergy(left_energy.isSelected());

        some_global.setRightStress(right_stress.isSelected());
        some_global.setRightStrain(right_strain.isSelected());
        some_global.setRightTemperature(right_temperature.isSelected());
        some_global.setRightEnergy(right_energy.isSelected());

        some_global.setFrontStress(front_stress.isSelected());
        some_global.setFrontStrain(front_strain.isSelected());
        some_global.setFrontTemperature(front_temperature.isSelected());
        some_global.setFrontEnergy(front_energy.isSelected());

        some_global.setBackStress(back_stress.isSelected());
        some_global.setBackStrain(back_strain.isSelected());
        some_global.setBackTemperature(back_temperature.isSelected());
        some_global.setBackEnergy(back_energy.isSelected());
        
        /** Set inputed parameters in data bank
         */
        some_global.setTopMinMechanicalLoading(Double.valueOf(top_min_mechanical_loading.getText()));
        some_global.setTopMechanicalLoading(Double.valueOf(top_mechanical_loading.getText()));
        some_global.setTopMinThermalInflux(Double.valueOf(top_min_thermal_influx.getText()));  
        some_global.setTopThermalInflux(Double.valueOf(top_thermal_influx.getText()));        
        some_global.setTopBoundTimeFunctionType(Byte.valueOf(top_bound_time_function_type.getText()));
        some_global.setTopBoundLoadTimePortion(Double.valueOf(top_bound_load_time_portion.getText()));
        some_global.setTopBoundRelaxTimePortion(Double.valueOf(top_bound_relax_time_portion.getText()));

        some_global.setBottomMinMechanicalLoading(Double.valueOf(bottom_min_mechanical_loading.getText()));
        some_global.setBottomMechanicalLoading(Double.valueOf(bottom_mechanical_loading.getText()));
        some_global.setBottomMinThermalInflux(Double.valueOf(bottom_min_thermal_influx.getText()));
        some_global.setBottomThermalInflux(Double.valueOf(bottom_thermal_influx.getText()));
        some_global.setBottomBoundTimeFunctionType(Byte.valueOf(bottom_bound_time_function_type.getText()));
        some_global.setBottomBoundLoadTimePortion(Double.valueOf(bottom_bound_load_time_portion.getText()));
        some_global.setBottomBoundRelaxTimePortion(Double.valueOf(bottom_bound_relax_time_portion.getText()));

        some_global.setLeftMinMechanicalLoading(Double.valueOf(left_min_mechanical_loading.getText()));
        some_global.setLeftMechanicalLoading(Double.valueOf(left_mechanical_loading.getText()));
        some_global.setLeftMinThermalInflux(Double.valueOf(left_min_thermal_influx.getText()));
        some_global.setLeftThermalInflux(Double.valueOf(left_thermal_influx.getText()));
        some_global.setLeftBoundTimeFunctionType(Byte.valueOf(left_bound_time_function_type.getText()));
        some_global.setLeftBoundLoadTimePortion(Double.valueOf(left_bound_load_time_portion.getText()));
        some_global.setLeftBoundRelaxTimePortion(Double.valueOf(left_bound_relax_time_portion.getText()));

        some_global.setRightMinMechanicalLoading(Double.valueOf(right_min_mechanical_loading.getText()));
        some_global.setRightMechanicalLoading(Double.valueOf(right_mechanical_loading.getText()));
        some_global.setRightMinThermalInflux(Double.valueOf(right_min_thermal_influx.getText()));
        some_global.setRightThermalInflux(Double.valueOf(right_thermal_influx.getText()));
        some_global.setRightBoundTimeFunctionType(Byte.valueOf(right_bound_time_function_type.getText()));
        some_global.setRightBoundLoadTimePortion(Double.valueOf(right_bound_load_time_portion.getText()));
        some_global.setRightBoundRelaxTimePortion(Double.valueOf(right_bound_relax_time_portion.getText()));

        some_global.setFrontMinMechanicalLoading(Double.valueOf(front_min_mechanical_loading.getText()));
        some_global.setFrontMechanicalLoading(Double.valueOf(front_mechanical_loading.getText()));
        some_global.setFrontMinThermalInflux(Double.valueOf(front_min_thermal_influx.getText()));
        some_global.setFrontThermalInflux(Double.valueOf(front_thermal_influx.getText()));
        some_global.setFrontBoundTimeFunctionType(Byte.valueOf(front_bound_time_function_type.getText()));
        some_global.setFrontBoundLoadTimePortion(Double.valueOf(front_bound_load_time_portion.getText()));
        some_global.setFrontBoundRelaxTimePortion(Double.valueOf(front_bound_relax_time_portion.getText()));

        some_global.setBackMinMechanicalLoading(Double.valueOf(back_min_mechanical_loading.getText()));
        some_global.setBackMechanicalLoading(Double.valueOf(back_mechanical_loading.getText()));
        some_global.setBackMinThermalInflux(Double.valueOf(back_min_thermal_influx.getText()));
        some_global.setBackThermalInflux(Double.valueOf(back_thermal_influx.getText()));
        some_global.setBackBoundTimeFunctionType(Byte.valueOf(back_bound_time_function_type.getText()));
        some_global.setBackBoundLoadTimePortion(Double.valueOf(back_bound_load_time_portion.getText()));
        some_global.setBackBoundRelaxTimePortion(Double.valueOf(back_bound_relax_time_portion.getText()));

        /** Remember data about not adiabatic boundaries
         */        
        some_global.setTopAdiabaticMaterial(top_adiobatic_material.getText().toString());
        some_global.setBottomAdiabaticMaterial(bottom_adiobatic_material.getText().toString());
        some_global.setLeftAdiabaticMaterial(left_adiobatic_material.getText().toString());
        some_global.setRightAdiabaticMaterial(right_adiobatic_material.getText().toString());
        some_global.setFrontAdiabaticMaterial(front_adiobatic_material.getText().toString());
        some_global.setBackAdiabaticMaterial(back_adiobatic_material.getText().toString());
        
        System.out.println("LEFT MATERIAL in boundary = "+left_adiobatic_material.getText());
        
        // Remember boundary type
        if(first_boundary_type.isSelected())           some_global.setBoundaryType(0);
        if(second_boundary_type.isSelected())          some_global.setBoundaryType(1);
        if(third_boundary_type.isSelected())           some_global.setBoundaryType(2);
    }

    /*
     * Controll that all inputed values of parameters satisfied to standart
     * (it must be double typed)
     * @return - satisfy or not inputed value of parameter to standart
     */

    public boolean isParametersInput()
    {
        System.out.println("RecCABoundaryConditionsParams: method: isParametersInput: start");

        try
        {
            if(top_check_mechanical_loading.isSelected())
            {
                Double.parseDouble(top_mechanical_loading.getText().toString());
 //               Double.parseDouble(top_thermal_influx.getText().toString());

                if(!top_stress.isSelected()&!top_strain.isSelected())
                {   
                    new Alert(AlertType.ERROR, "Choose, please stress ot strain in top mechanical loading").show();
                    return false;
                }
            }
            if(bottom_check_mechanical_loading.isSelected())
            {
                Double.parseDouble(bottom_mechanical_loading.getText().toString());
  //              Double.parseDouble(bottom_thermal_influx.getText().toString());

                if(!bottom_stress.isSelected()&!bottom_strain.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please stress ot strain in bottom mechanical loading").show();
                    return false;
                }
            }
            if(right_check_mechanical_loading.isSelected())
            {
                Double.parseDouble(right_mechanical_loading.getText().toString());
  //              Double.parseDouble(right_thermal_influx.getText().toString());

                if(!right_stress.isSelected()&!right_strain.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please stress ot strain in right mechanical loading").show();
                    return false;
                }
            }
            if(left_check_mechanical_loading.isSelected())
            {
                Double.parseDouble(left_mechanical_loading.getText().toString());
  //              Double.parseDouble(left_thermal_influx.getText().toString());

                if(!left_stress.isSelected()&!left_strain.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please stress ot strain in left mechanical loading").show();
                    return false;
                }
            }
            if(front_check_mechanical_loading.isSelected())
            {
                Double.parseDouble(front_mechanical_loading.getText().toString());
  //              Double.parseDouble(front_thermal_influx.getText().toString());

                if(!front_stress.isSelected()&!front_strain.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please stress ot strain in front mechanical loading").show();
                    return false;
                }
            }
            if(back_check_mechanical_loading.isSelected())
            {
                Double.parseDouble(back_mechanical_loading.getText().toString());
   //             Double.parseDouble(back_thermal_influx.getText().toString());

                if(!back_stress.isSelected()&!back_strain.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please stress ot strain in back mechanical loading").show();
                    return false;
                }
            }

            if(top_check_thermal_influx.isSelected())
            {
//                Double.parseDouble(top_mechanical_loading.getText().toString());
                Double.parseDouble(top_thermal_influx.getText().toString());

                if(!top_temperature.isSelected()&!top_energy.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please temperature ot energy in top thermal influx").show();
                    return false;
                }
            }
            if(bottom_check_thermal_influx.isSelected())
            {
   //             Double.parseDouble(bottom_mechanical_loading.getText().toString());
                Double.parseDouble(bottom_thermal_influx.getText().toString());

                if(!bottom_temperature.isSelected()&!bottom_energy.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please temperature ot energy in bottom thermal influx").show();
                    return false;
                }
            }
            if(right_check_thermal_influx.isSelected())
            {
   //             Double.parseDouble(right_mechanical_loading.getText().toString());
                Double.parseDouble(right_thermal_influx.getText().toString());

                if(!right_temperature.isSelected()&!right_energy.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please temperature ot energy in right thermal influx").show();
                    return false;
                }
            }
            if(left_check_thermal_influx.isSelected())
            {
  //              Double.parseDouble(left_mechanical_loading.getText().toString());
                Double.parseDouble(left_thermal_influx.getText().toString());

                if(!left_temperature.isSelected()&!left_energy.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please temperature ot energy in left thermal influx").show();
                    return false;
                }
            }
            if(front_check_thermal_influx.isSelected())
            {
   //             Double.parseDouble(front_mechanical_loading.getText().toString());
                Double.parseDouble(front_thermal_influx.getText().toString());

                if(!front_temperature.isSelected()&!front_energy.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please temperature ot energy in front thermal influx").show();
                    return false;
                }
            }
            if(back_check_thermal_influx.isSelected())
            {
   //             Double.parseDouble(back_mechanical_loading.getText().toString());
                Double.parseDouble(back_thermal_influx.getText().toString());

                if(!back_temperature.isSelected()&!back_energy.isSelected())
                {
                    new Alert(AlertType.ERROR, "Choose, please temperature ot energy in back thermal influx").show();
                    return false;
                }
            }

            if(top_adiobatic_material.isVisible())
                if(top_adiobatic_material.getText().equals(UICommon.FIRST_CHOOSE_NAME))
                {
                    new Alert(AlertType.ERROR, UICommon.ERROR_TOP_NOT_ADIABATIC_EXCEPTION_NAME).show();
                    return false;
                }
            if(bottom_adiobatic_material.isVisible())
                if(bottom_adiobatic_material.getText().equals(UICommon.FIRST_CHOOSE_NAME))
                {
                    new Alert(AlertType.ERROR, UICommon.ERROR_BOTTOM_NOT_ADIABATIC_EXCEPTION_NAME).show();
                    return false;
                }
            if(left_adiobatic_material.isVisible())
                if(left_adiobatic_material.getText().equals(UICommon.FIRST_CHOOSE_NAME))
                {
                    new Alert(AlertType.ERROR, UICommon.ERROR_LEFT_NOT_ADIABATIC_EXCEPTION_NAME).show();
                    return false;
                }
            if(right_adiobatic_material.isVisible())
                if(right_adiobatic_material.getText().equals(UICommon.FIRST_CHOOSE_NAME))
                {
                    new Alert(AlertType.ERROR, UICommon.ERROR_RIGHT_NOT_ADIABATIC_EXCEPTION_NAME).show();
                    return false;
                }
            if(front_adiobatic_material.isVisible())
                if(front_adiobatic_material.getText().equals(UICommon.FIRST_CHOOSE_NAME))
                {
                    new Alert(AlertType.ERROR, UICommon.ERROR_FRONT_NOT_ADIABATIC_EXCEPTION_NAME).show();
                    return false;
                }
            if(back_adiobatic_material.isVisible())
                if(back_adiobatic_material.getText().equals(UICommon.FIRST_CHOOSE_NAME))
                {
                    new Alert(AlertType.ERROR, UICommon.ERROR_BACK_NOT_ADIABATIC_EXCEPTION_NAME).show();
                    return false;
                }

            // Check boundary type
            if(!first_boundary_type.isSelected()&!second_boundary_type.isSelected()&
                    !third_boundary_type.isSelected())
            {
                new Alert(AlertType.ERROR, "There is no boundary type choosed!").show();
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
     * Set all this frame (all his elements) editable or not
     * @param value - editable or not
     */

    public void setEditable(boolean value)
    {
        System.out.println("RecCABoundaryConditionsParams: method: setEditable: start");

        top_check_mechanical_loading.setDisable(!value);
        top_check_thermal_influx.setDisable(!value);
        top_min_mechanical_loading.setDisable(!value);
        top_mechanical_loading.setDisable(!value);
        top_min_thermal_influx.setDisable(!value);
        top_thermal_influx.setDisable(!value);
        top_adiobatic_material.setDisable(!value);

        bottom_check_mechanical_loading.setDisable(!value);
        bottom_check_thermal_influx.setDisable(!value);
        bottom_min_mechanical_loading.setDisable(!value);
        bottom_mechanical_loading.setDisable(!value);
        bottom_min_thermal_influx.setDisable(!value);
        bottom_thermal_influx.setDisable(!value);
        bottom_adiobatic_material.setDisable(!value);

        left_check_mechanical_loading.setDisable(!value);
        left_check_thermal_influx.setDisable(!value);
        left_min_mechanical_loading.setDisable(!value);
        left_mechanical_loading.setDisable(!value);
        left_min_thermal_influx.setDisable(!value);
        left_thermal_influx.setDisable(!value);
        left_adiobatic_material.setDisable(!value);

        right_check_mechanical_loading.setDisable(!value);
        right_check_thermal_influx.setDisable(!value);
        right_min_mechanical_loading.setDisable(!value);
        right_mechanical_loading.setDisable(!value);
        right_min_thermal_influx.setDisable(!value);
        right_thermal_influx.setDisable(!value);
        right_adiobatic_material.setDisable(!value);

        front_check_mechanical_loading.setDisable(!value);
        front_check_thermal_influx.setDisable(!value);
        front_min_mechanical_loading.setDisable(!value);
        front_mechanical_loading.setDisable(!value);
        front_min_thermal_influx.setDisable(!value);
        front_thermal_influx.setDisable(!value);
        front_adiobatic_material.setDisable(!value);

        back_check_mechanical_loading.setDisable(!value);
        back_check_thermal_influx.setDisable(!value);
        back_min_mechanical_loading.setDisable(!value);
        back_mechanical_loading.setDisable(!value);
        back_min_thermal_influx.setDisable(!value);
        back_thermal_influx.setDisable(!value);
        back_adiobatic_material.setDisable(!value);

        button_save.setDisable(!value);
        button_ok.setDisable(!value);
    }

    /*
     * Controll actions of buttons "Save as", "OK" and "Cancel"
     * Also controll what checkboxes is selected to allow inputing
     * parameters to corresponding textfields
     */
    
    public void handleEvents(){
        button_save.setOnAction(e -> {
            System.out.println("RecCABoundaryConditionsParams: method: actionPerformed: button 'Save as' pushed");
            
            // Controll - satisfied inputed values of parameters
            // to existed standart or not
            if(isParametersInput())
            {
                // Remember inputed parameters in boundary condition data bank
                inputParams();
                
                // Remember this boundary condition data bank
                transfer_data_bank.setUIBoundaryCondition(some_global);
                
                // Save data in choosed by user directory in dialog saving window
                savingDialofOfParamsForBoundaryConditions();
            }
        });
        
        top_adiobatic_material.setOnAction(e -> {
            System.out.println("RecCABoundaryConditionsParams: method: actionPerformed: top boundary not adiabatic");
            materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            
            materials.ok.setOnAction(event -> {
                String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                System.out.println("RecCABoundaryConditionsParams: top_adiobatic_material selected material: " + selected_mat);
                top_adiobatic_material.setText(selected_mat);
                materials.close();
            });
        });
             
        bottom_adiobatic_material.setOnAction(e -> {
            System.out.println("RecCABoundaryConditionsParams: method: actionPerformed: bottom boundary not adiabatic");
            materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            
            materials.ok.setOnAction(event -> {
                String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                System.out.println("RecCABoundaryConditionsParams: bottom_adiobatic_material selected material: " + selected_mat);
                bottom_adiobatic_material.setText(selected_mat);
                materials.close();
            });
        });
             
        left_adiobatic_material.setOnAction(e -> {
            System.out.println("RecCABoundaryConditionsParams: method: actionPerformed: left boundary not adiabatic");
            materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            
            materials.ok.setOnAction(event -> {
                String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                System.out.println("RecCABoundaryConditionsParams: left adiobatic material selected material: " + selected_mat);
                left_adiobatic_material.setText(selected_mat);
                materials.close();
            });
        });

        right_adiobatic_material.setOnAction(e -> {
            System.out.println("RecCABoundaryConditionsParams: method: actionPerformed: right boundary not adiabatic");
            materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);

            materials.ok.setOnAction(event -> {
                String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                System.out.println("RecCABoundaryConditionsParams: right adiobatic material selected material: " + selected_mat);
                right_adiobatic_material.setText(selected_mat);
                materials.close();
            });
        });

        front_adiobatic_material.setOnAction(e -> {
            System.out.println("RecCABoundaryConditionsParams: method: actionPerformed: front boundary not adiabatic");
            materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            materials.ok.setOnAction(event -> {
                String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                System.out.println("RecCABoundaryConditionsParams: front adiobatic material selected material: " + selected_mat);
                front_adiobatic_material.setText(selected_mat);
                materials.close();
            });
        });

        back_adiobatic_material.setOnAction(e -> {
            System.out.println("RecCABoundaryConditionsParams: method: actionPerformed: back boundary not adiabatic");
            materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            materials.ok.setOnAction(event -> {
                String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                System.out.println("RecCABoundaryConditionsParams: back adiobatic material selected material: " + selected_mat);
                back_adiobatic_material.setText(selected_mat);
                materials.close();
            });
        });
        setCheckedPositions(); 
    }

    /** Add action listeners for all needed elements
     */
    
    public int getFunctionType()
    {
        return function_type;
    }
    
    /*
     * Controll inputed name for created file with boundary conditions
     * @param users_file_name - what name was inputed
     * @return - can be created file with inputed name or not
     */

    public boolean isNameCorrect(TextField users_file_name)
    {
        System.out.println("RecCABoundaryConditionsParams: method: isNameCorrect: start");

        // Remember inputed file name

        String file_name = users_file_name.getText();

        // If file name empty

        if(file_name.isEmpty())
        {
            new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }

        // If file name equals with name of task file

        if(file_name.equals(UICommon.TASK_PARAM_NAME))
        {
            new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }

        // If file name ending on initial conditions file

        if(file_name.length()>4)
            if(file_name.substring(file_name.length()-4,file_name.length()).equals(UICommon.END_REC_NAME))
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

        // If file name ending on geometry file

        if(file_name.length()>5)
            if(file_name.substring(file_name.length()-5,file_name.length()).equals(UICommon.END_GEOM_NAME))
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

        // If file name ending on grains file

        if(file_name.length()>7)
            if(file_name.substring(file_name.length()-7,file_name.length()).equals(UICommon.END_GRAINS_NAME))
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

      return true;
    }
}
