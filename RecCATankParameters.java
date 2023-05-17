/**
 * @(#) RecCATankParameters.java version 1.0.0;       April 1 2011
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation a frame for inputing or showing parameters of
 * created or choosed tank
 *
 *=============================================================
 *  Last changes :
 *          1 April 2011 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.0
 *          Edit: Class creation
 */

/** Class for creation of frame to input or show parameters of created or chosen tank
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - April 2011
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


public class RecCATankParameters extends Stage{
    Scene scene;
    VBox root;
    
    /** This field is intended for creation a textfields for inputing or showing
     * parameters ("Mechanical loading" : stress or strain,
     * "Thermal influx" : temperature or energy) of
     * corresponding side of boundary ("Top", "Bottom", "Left",
     * "Right", "Front" or "Back")
     */
    TextField top_mechanical_loading,    top_thermal_influx;
    TextField bottom_mechanical_loading, bottom_thermal_influx;
    TextField left_mechanical_loading,   left_thermal_influx;
    TextField right_mechanical_loading,  right_thermal_influx;
    TextField front_mechanical_loading,  front_thermal_influx;
    TextField back_mechanical_loading,   back_thermal_influx;
    
    /** Minimal values of parameters of thermal and mechanical loading under cycling loading
     */
    TextField top_min_mechanical_loading,    top_min_thermal_influx;
    TextField bottom_min_mechanical_loading, bottom_min_thermal_influx;
    TextField left_min_mechanical_loading,   left_min_thermal_influx;
    TextField right_min_mechanical_loading,  right_min_thermal_influx;
    TextField front_min_mechanical_loading,  front_min_thermal_influx;
    TextField back_min_mechanical_loading,   back_min_thermal_influx;
    
    /** Labels for minimal values of parameters of thermal and mechanical loading under cycling loading */
    Label top_min_mechanical_loading_label,    top_min_thermal_influx_label;
    Label bottom_min_mechanical_loading_label, bottom_min_thermal_influx_label;
    Label left_min_mechanical_loading_label,   left_min_thermal_influx_label;
    Label right_min_mechanical_loading_label,  right_min_thermal_influx_label;
    Label front_min_mechanical_loading_label,  front_min_thermal_influx_label;
    Label back_min_mechanical_loading_label,   back_min_thermal_influx_label;
    
    /** Labels for maximal values of parameters of thermal and mechanical loading under cycling loading */
    Label top_max_mechanical_loading_label,    top_max_thermal_influx_label;
    Label bottom_max_mechanical_loading_label, bottom_max_thermal_influx_label;
    Label left_max_mechanical_loading_label,   left_max_thermal_influx_label;
    Label right_max_mechanical_loading_label,  right_max_thermal_influx_label;
    Label front_max_mechanical_loading_label,  front_max_thermal_influx_label;
    Label back_max_mechanical_loading_label,   back_max_thermal_influx_label;
    
    /** Fields for types of time functions for loading of tanks */
    TextField top_time_function_type,   bottom_time_function_type, left_time_function_type,
              right_time_function_type, front_time_function_type,  back_time_function_type;
    
    /** Fields for time portions of loading for all tanks */
    TextField top_loading_time_portion,   bottom_loading_time_portion, left_loading_time_portion,
              right_loading_time_portion, front_loading_time_portion,  back_loading_time_portion;
    
    /** Fields for time portions of relaxation for all tanks */
    TextField top_relaxation_time_portion,   bottom_relaxation_time_portion, left_relaxation_time_portion,
              right_relaxation_time_portion, front_relaxation_time_portion,  back_relaxation_time_portion;
    
    /** This field is intended for creating a set of check boxes for edit
     * only needed parameters ("Mechanical loading", "Thermal influx")
     * on corresponding side of boundary ("Top", "Bottom", "Left",
     * "Right", "Front" or "Back")
     */
    CheckBox top_check_mechanical_loading,top_check_thermal_influx;
    CheckBox bottom_check_mechanical_loading,bottom_check_thermal_influx;
    CheckBox left_check_mechanical_loading,left_check_thermal_influx;
    CheckBox right_check_mechanical_loading,right_check_thermal_influx;
    CheckBox front_check_mechanical_loading,front_check_thermal_influx;
    CheckBox back_check_mechanical_loading,back_check_thermal_influx;

    /** This field is intended for choose stress or strain for mechanical
     * loading and temperature or energy for thermal influx
     */
    RadioButton top_stress, top_strain, top_temperature, top_energy;
    RadioButton bottom_stress, bottom_strain, bottom_temperature, bottom_energy;
    RadioButton left_stress, left_strain, left_temperature, left_energy;
    RadioButton right_stress, right_strain, right_temperature, right_energy;
    RadioButton front_stress, front_strain, front_temperature, front_energy;
    RadioButton back_stress, back_strain, back_temperature, back_energy;
    
    /** Buttons for choice of geometrical type of tank */
    RadioButton parallelepiped_tank, vertical_ellyptic_tank, horizon_triangle_tank, horizon_circle_tank;

    /** This field is intended for choose onle one type: stress or strain,
     * and temperature or energy
     */
    ToggleGroup top_mechanical_loading_group, top_thermal_influx_group;
    ToggleGroup bottom_mechanical_loading_group, bottom_thermal_influx_group;
    ToggleGroup left_mechanical_loading_group, left_thermal_influx_group;
    ToggleGroup right_mechanical_loading_group, right_thermal_influx_group;
    ToggleGroup front_mechanical_loading_group, front_thermal_influx_group;
    ToggleGroup back_mechanical_loading_group, back_thermal_influx_group;
    
    /** Group of buttons for choice of geometrical type of tank */
    ToggleGroup tank_geometrical_types_group;

    /** This field is intended for creating buttons "Save as" and "Cancel" or "OK"
     */
    Button button_save, button_ok;

    /** This field is intended for created inscription for
     * inform user where which boundary
     */
    TabPane tab_pane;
    Tab top, bottom, left, right, front, back;

    /** This field is intended for input file name of saving bound cond
     */
    TextField textfield_for_file_location;

    /** This field is intended for controll what button in saving dialog was pushed
     */
    int save_option;

    /** This field is intended for save bound cond data
     */
    SaveParams saving;

    /** This field is intended for remember params in tank data bank
     */
    UITank some_global;

    
    /** This field is intended for controll all transferred data bank
     */
    TransferredDataBank transfer_data_bank;

    /** This field is intended to inform user adiobatic cell or not
     */
    Label top_adiobatic_or_not, bottom_adiobatic_or_not;
    Label left_adiobatic_or_not, right_adiobatic_or_not;
    Label front_adiobatic_or_not, back_adiobatic_or_not;

    /** This field is intended to choose material
     * in case when cell is not adiobatic
     */
    Button top_adiobatic_material, bottom_adiobatic_material;
    Button left_adiobatic_material, right_adiobatic_material;
    Button front_adiobatic_material, back_adiobatic_material;

    /** This field is intended for choose material for not adiabatic
     * boundary
     */
    RecCAFileList materials;

    /** This fields is intended for choose boundary type:
     * 0 - boundary cell does not change mechanical energy because of interaction with neighbours;
     * 1 - boundary cell changes mechanical energy because of interaction with inner neighbour cells only;
     * 2 - boundary cell changes mechanical energy because of interaction with each neighbour cell.
     */
    CheckBox first_boundary_type, second_boundary_type, third_boundary_type;

    /** This field is intended for controll boundary type check boxes as one system
     */
    ToggleGroup boundary_type_group;
    
    /** Labels for coordinates of all tanks */
    Label top_x_1, top_x_2, top_y_1, top_y_2, top_z_1, top_z_2;
    Label bottom_x_1, bottom_x_2, bottom_y_1, bottom_y_2, bottom_z_1, bottom_z_2;
    Label left_x_1, left_x_2, left_y_1, left_y_2, left_z_1, left_z_2;
    Label right_x_1, right_x_2, right_y_1, right_y_2, right_z_1, right_z_2;
    Label front_x_1, front_x_2, front_y_1, front_y_2, front_z_1, front_z_2;
    Label back_x_1, back_x_2, back_y_1, back_y_2, back_z_1, back_z_2;
    
    /** Labels for types of time functions for all tanks */
    Label top_time_function_type_label,    top_loading_time_portion_label,    top_relaxation_time_portion_label;
    Label bottom_time_function_type_label, bottom_loading_time_portion_label, bottom_relaxation_time_portion_label;
    Label left_time_function_type_label,   left_loading_time_portion_label,   left_relaxation_time_portion_label;
    Label right_time_function_type_label,  right_loading_time_portion_label,  right_relaxation_time_portion_label;
    Label front_time_function_type_label,  front_loading_time_portion_label,  front_relaxation_time_portion_label;
    Label back_time_function_type_label,   back_loading_time_portion_label,   back_relaxation_time_portion_label;
    
    /** text fields for tank coordinates */
    TextField top_coordinate_x_1,    top_coordinate_x_2,    top_coordinate_y_1,    top_coordinate_y_2,    top_coordinate_z_1,    top_coordinate_z_2, 
              bottom_coordinate_x_1, bottom_coordinate_x_2, bottom_coordinate_y_1, bottom_coordinate_y_2, bottom_coordinate_z_1, bottom_coordinate_z_2, 
              left_coordinate_x_1,   left_coordinate_x_2,   left_coordinate_y_1,   left_coordinate_y_2,   left_coordinate_z_1,   left_coordinate_z_2, 
              right_coordinate_x_1,  right_coordinate_x_2,  right_coordinate_y_1,  right_coordinate_y_2,  right_coordinate_z_1,  right_coordinate_z_2,
              front_coordinate_x_1,  front_coordinate_x_2,  front_coordinate_y_1,  front_coordinate_y_2,  front_coordinate_z_1,  front_coordinate_z_2, 
              back_coordinate_x_1,   back_coordinate_x_2,   back_coordinate_y_1,   back_coordinate_y_2,   back_coordinate_z_1,   back_coordinate_z_2;
    
    /** Create intnal frame for input boundary conditions
     * @param list - boundary conditions list
     * @param receive_transfer_data_bank - transferred data bank
     */
    public RecCATankParameters(TransferredDataBank receive_transfer_data_bank)
    {
        System.out.println("RecCATankParameters constructor: start");
        
        root = new VBox();
        scene = new Scene(root);
        
        /** Remember transferred data bank
         */
        transfer_data_bank = receive_transfer_data_bank;

        /** Remember boundary conditions data bank
         */
        some_global = transfer_data_bank.getUITank();        
        System.out.println("value = "+some_global.getTopMechValue());
        
        /** Create textfields for inputing or showing
         * values of corresponding parameters
         */
        createTextFieldsForParamsOfBoundaryConditions();

        /** Create buttons: "Save as" and "Cancel" or "OK"
         */
        createButtonsForParamsOfBoundaryConditions();

        /** Create checkboxes to each parameter for choosing
         * which parameters input and wich of them is not needed
         */
        createCheckBoxesForBoundary();

        /** Create radio buttons for choose task types:
         * for mechanical loading: stress or strain,
         * for thermal influx: temperature or energy
         */
        createTypeTaskChoose();
        createCoordinateTextFields();
        createCoordinateHelps();

        /** Create adiabatic helps
         */
        createAdiobaticInfo();

        /** Create boxes for choose material for not adiabatic boundary
         */
        createNotAdiobaticMaterialBox();

        /** Add elements on frame
         */
        addElementsForParametersOfBoundaryConditions();

        /** Check selected checkboxes - if check box is selected
         * then value of corresponding parameter can be changed
         * and vice versa
         */
        setCheckedPositions();

        /** Add needed action listeners to corresponding elements of internal frame
         */
        handleEvents();
        
        this.setTitle("Tank Parameters");
        this.setScene(scene);
        
        textfield_for_file_location = new TextField();
    }

    /** Create textfields for inputing or showing
     * parameters ("Mechanical loading", "Thermal energy") of
     * corresponding side of boundary
     * Create helps (names of each boundary)
     */
    public void createTextFieldsForParamsOfBoundaryConditions()
    {
        System.out.println("RecCATankParameters: method: createTextFieldsForParamsOfBoundaryConditions: start");

        // Top parameter values (textfields)
        top_min_mechanical_loading = new TextField();
        top_min_mechanical_loading.setText((new Double(some_global.getTopMinMechValue())).toString());
        top_mechanical_loading = new TextField();
        top_mechanical_loading.setText((new Double(some_global.getTopMechValue())).toString());
        top_min_thermal_influx = new TextField();
        top_min_thermal_influx.setText((new Double(some_global.getTopMinThermValue())).toString());
        top_thermal_influx = new TextField();
        top_thermal_influx.setText((new Double(some_global.getTopThermValue())).toString());
        top_time_function_type = new TextField();
        top_time_function_type.setText((new Byte(some_global.getTopTimeFunctionType())).toString());
        top_loading_time_portion = new TextField();
        top_loading_time_portion.setText((new Double(some_global.getTopLoadingTimePortion())).toString());
        top_relaxation_time_portion = new TextField();
        top_relaxation_time_portion.setText((new Double(some_global.getTopRelaxationTimePortion())).toString());
        
        top_min_mechanical_loading_label = new Label("min");
        top_max_mechanical_loading_label = new Label("max");
        top_min_thermal_influx_label = new Label("min");
        top_max_thermal_influx_label = new Label("max");
        // Bottom parameter values (textfields)
        bottom_min_mechanical_loading = new TextField();
        bottom_min_mechanical_loading.setText((new Double(some_global.getBottomMinMechValue())).toString());
        bottom_mechanical_loading = new TextField();
        bottom_mechanical_loading.setText((new Double(some_global.getBottomMechValue())).toString());
        bottom_min_thermal_influx = new TextField();
        bottom_min_thermal_influx.setText((new Double(some_global.getBottomMinThermValue())).toString());
        bottom_thermal_influx = new TextField();
        bottom_thermal_influx.setText((new Double(some_global.getBottomThermValue())).toString());
        bottom_time_function_type = new TextField();
        bottom_time_function_type.setText((new Byte(some_global.getBottomTimeFunctionType())).toString());
        bottom_loading_time_portion = new TextField();
        bottom_loading_time_portion.setText((new Double(some_global.getBottomLoadingTimePortion())).toString());
        bottom_relaxation_time_portion = new TextField();
        bottom_relaxation_time_portion.setText((new Double(some_global.getBottomRelaxationTimePortion())).toString());
        bottom_min_mechanical_loading_label = new Label("min");
        bottom_max_mechanical_loading_label = new Label("max");
        bottom_min_thermal_influx_label = new Label("min");
        bottom_max_thermal_influx_label = new Label("max");
        
        // Left parameter values (textfields)
        left_min_mechanical_loading = new TextField();
        left_min_mechanical_loading.setText((new Double(some_global.getLeftMinMechValue())).toString());
        left_mechanical_loading = new TextField();
        left_mechanical_loading.setText((new Double(some_global.getLeftMechValue())).toString());
        left_min_thermal_influx = new TextField();
        left_min_thermal_influx.setText((new Double(some_global.getLeftMinThermValue())).toString());
        left_thermal_influx = new TextField();
        left_thermal_influx.setText((new Double(some_global.getLeftThermValue())).toString());
        left_time_function_type = new TextField();
        left_time_function_type.setText((new Byte(some_global.getLeftTimeFunctionType())).toString());
        left_loading_time_portion = new TextField();
        left_loading_time_portion.setText((new Double(some_global.getLeftLoadingTimePortion())).toString());
        left_relaxation_time_portion = new TextField();
        left_relaxation_time_portion.setText((new Double(some_global.getLeftRelaxationTimePortion())).toString());
        left_min_mechanical_loading_label = new Label("min");
        left_max_mechanical_loading_label = new Label("max");
        left_min_thermal_influx_label = new Label("min");
        left_max_thermal_influx_label = new Label("max");
        // Right parameter values (textfields)
        right_min_mechanical_loading = new TextField();
        right_min_mechanical_loading.setText((new Double(some_global.getRightMinMechValue())).toString());
        right_mechanical_loading = new TextField();
        right_mechanical_loading.setText((new Double(some_global.getRightMechValue())).toString());
        right_min_thermal_influx = new TextField();
        right_min_thermal_influx.setText((new Double(some_global.getRightMinThermValue())).toString());
        right_thermal_influx = new TextField();
        right_thermal_influx.setText((new Double(some_global.getRightThermValue())).toString());
        right_time_function_type = new TextField();
        right_time_function_type.setText((new Byte(some_global.getRightTimeFunctionType())).toString());
        right_loading_time_portion = new TextField();
        right_loading_time_portion.setText((new Double(some_global.getRightLoadingTimePortion())).toString());
        right_relaxation_time_portion = new TextField();
        right_relaxation_time_portion.setText((new Double(some_global.getRightRelaxationTimePortion())).toString());
        right_min_mechanical_loading_label = new Label("min");
        right_max_mechanical_loading_label = new Label("max");
        right_min_thermal_influx_label = new Label("min");
        right_max_thermal_influx_label = new Label("max");
        // Front parameter values (textfields)
        front_min_mechanical_loading = new TextField();
        front_min_mechanical_loading.setText((new Double(some_global.getFrontMinMechValue())).toString());
        front_mechanical_loading = new TextField();
        front_mechanical_loading.setText((new Double(some_global.getFrontMechValue())).toString());
        front_min_thermal_influx = new TextField();
        front_min_thermal_influx.setText((new Double(some_global.getFrontMinThermValue())).toString());
        front_thermal_influx = new TextField();
        front_thermal_influx.setText((new Double(some_global.getFrontThermValue())).toString());
        front_time_function_type = new TextField();
        front_time_function_type.setText((new Byte(some_global.getFrontTimeFunctionType())).toString());
        front_loading_time_portion = new TextField();
        front_loading_time_portion.setText((new Double(some_global.getFrontLoadingTimePortion())).toString());
        front_relaxation_time_portion = new TextField();
        front_relaxation_time_portion.setText((new Double(some_global.getFrontRelaxationTimePortion())).toString());
        front_min_mechanical_loading_label = new Label("min");
        front_max_mechanical_loading_label = new Label("max");
        front_min_thermal_influx_label = new Label("min");
        front_max_thermal_influx_label = new Label("max");
        // Back parameter values (textfields)
        back_min_mechanical_loading = new TextField();
        back_min_mechanical_loading.setText((new Double(some_global.getBackMinMechValue())).toString());
        back_mechanical_loading = new TextField();
        back_mechanical_loading.setText((new Double(some_global.getBackMechValue())).toString());
        back_min_thermal_influx = new TextField();
        back_min_thermal_influx.setText((new Double(some_global.getBackMinThermValue())).toString());
        back_thermal_influx = new TextField();
        back_thermal_influx.setText((new Double(some_global.getBackThermValue())).toString());
        back_time_function_type = new TextField();
        back_time_function_type.setText((new Byte(some_global.getBackTimeFunctionType())).toString());
        back_loading_time_portion = new TextField();
        back_loading_time_portion.setText((new Double(some_global.getBackLoadingTimePortion())).toString());
        back_relaxation_time_portion = new TextField();
        back_relaxation_time_portion.setText((new Double(some_global.getBackRelaxationTimePortion())).toString());
        back_min_mechanical_loading_label = new Label("min");
        back_max_mechanical_loading_label = new Label("max");
        back_min_thermal_influx_label = new Label("min");
        back_max_thermal_influx_label = new Label("max");
        /*
         * Create helps (boundary names)
         */
        top = new Tab(UICommon.TOP_NAME);
        top.setClosable(false);
        
        bottom = new Tab(UICommon.BOTTOM_NAME);
        bottom.setClosable(false);
        
        left = new Tab(UICommon.LEFT_NAME);
        left.setClosable(false);
        
        right = new Tab(UICommon.RIGHT_NAME);
        right.setClosable(false);
        
        front = new Tab(UICommon.FRONT_NAME);
        front.setClosable(false);
        
        back = new Tab(UICommon.BACK_NAME);
        back.setClosable(false);
    }

    public void createCoordinateTextFields()
    {
        // Create textfields for input coordinayes for top side
        top_coordinate_x_1 = new TextField();
        top_coordinate_x_1.setText((new Double(some_global.getTopCoordinateX1())).toString());
        
        top_coordinate_x_2 = new TextField();
        top_coordinate_x_2.setText((new Double(some_global.getTopCoordinateX2())).toString());
        
        top_coordinate_y_1 = new TextField();
        top_coordinate_y_1.setText((new Double(some_global.getTopCoordinateY1())).toString());
        
        top_coordinate_y_2 = new TextField();
        top_coordinate_y_2.setText((new Double(some_global.getTopCoordinateY2())).toString());
        
        top_coordinate_z_1 = new TextField();
        top_coordinate_z_1.setText((new Double(some_global.getTopCoordinateZ1())).toString());
        
        top_coordinate_z_2 = new TextField();
        top_coordinate_z_2.setText((new Double(some_global.getTopCoordinateZ2())).toString());
        
        // Create textfields for input coordinayes for bottom side
        bottom_coordinate_x_1 = new TextField();
        bottom_coordinate_x_1.setText((new Double(some_global.getBottomCoordinateX1())).toString());
        bottom_coordinate_x_2 = new TextField();
        bottom_coordinate_x_2.setText((new Double(some_global.getBottomCoordinateX2())).toString());
        
        bottom_coordinate_y_1 = new TextField();
        bottom_coordinate_y_1.setText((new Double(some_global.getBottomCoordinateY1())).toString());        
        bottom_coordinate_y_2 = new TextField();
        bottom_coordinate_y_2.setText((new Double(some_global.getBottomCoordinateY2())).toString());

        bottom_coordinate_z_1 = new TextField();
        bottom_coordinate_z_1.setText((new Double(some_global.getBottomCoordinateZ1())).toString());
        bottom_coordinate_z_2 = new TextField();
        bottom_coordinate_z_2.setText((new Double(some_global.getBottomCoordinateZ2())).toString());

        // Create textfields for input coordinayes for left side
        left_coordinate_x_1 = new TextField();
        left_coordinate_x_1.setText((new Double(some_global.getLeftCoordinateX1())).toString());
        left_coordinate_x_2 = new TextField();
        left_coordinate_x_2.setText((new Double(some_global.getLeftCoordinateX2())).toString());
        
        left_coordinate_y_1 = new TextField();
        left_coordinate_y_1.setText((new Double(some_global.getLeftCoordinateY1())).toString());
        left_coordinate_y_2 = new TextField();
        left_coordinate_y_2.setText((new Double(some_global.getLeftCoordinateY2())).toString());

        
        left_coordinate_z_1 = new TextField();
        left_coordinate_z_1.setText((new Double(some_global.getLeftCoordinateZ1())).toString());
        left_coordinate_z_2 = new TextField();
        left_coordinate_z_2.setText((new Double(some_global.getLeftCoordinateZ2())).toString());
        
        // Create textfields for input coordinayes for right side
        right_coordinate_x_1 = new TextField();
        right_coordinate_x_1.setText((new Double(some_global.getRightCoordinateX1())).toString());
        right_coordinate_x_2 = new TextField();
        right_coordinate_x_2.setText((new Double(some_global.getRightCoordinateX2())).toString());        
        
        right_coordinate_y_1 = new TextField();
        right_coordinate_y_1.setText((new Double(some_global.getRightCoordinateY1())).toString());        
        right_coordinate_y_2 = new TextField();
        right_coordinate_y_2.setText((new Double(some_global.getRightCoordinateY2())).toString());
        
        right_coordinate_z_1 = new TextField();
        right_coordinate_z_1.setText((new Double(some_global.getRightCoordinateZ1())).toString());
        right_coordinate_z_2 = new TextField();
        right_coordinate_z_2.setText((new Double(some_global.getRightCoordinateZ2())).toString());

        // Create textfields for input coordinayes for front side
        front_coordinate_x_1 = new TextField();
        front_coordinate_x_1.setText((new Double(some_global.getFrontCoordinateX1())).toString());
        front_coordinate_x_2 = new TextField();
        front_coordinate_x_2.setText((new Double(some_global.getFrontCoordinateX2())).toString());
        
        front_coordinate_y_1 = new TextField();
        front_coordinate_y_1.setText((new Double(some_global.getFrontCoordinateY1())).toString());
        front_coordinate_y_2 = new TextField();
        front_coordinate_y_2.setText((new Double(some_global.getFrontCoordinateY2())).toString());
        
        front_coordinate_z_1 = new TextField();
        front_coordinate_z_1.setText((new Double(some_global.getFrontCoordinateZ1())).toString());
        front_coordinate_z_2 = new TextField();
        front_coordinate_z_2.setText((new Double(some_global.getFrontCoordinateZ2())).toString());
        
        // Create textfields for input coordinayes for back side
        back_coordinate_x_1 = new TextField();
        back_coordinate_x_1.setText((new Double(some_global.getBackCoordinateX1())).toString());
        back_coordinate_x_2 = new TextField();
        back_coordinate_x_2.setText((new Double(some_global.getBackCoordinateX2())).toString());
        
        back_coordinate_y_1 = new TextField();
        back_coordinate_y_1.setText((new Double(some_global.getBackCoordinateY1())).toString());
        back_coordinate_y_2 = new TextField();
        back_coordinate_y_2.setText((new Double(some_global.getBackCoordinateY2())).toString());
        
        back_coordinate_z_1 = new TextField();
        back_coordinate_z_1.setText((new Double(some_global.getBackCoordinateZ1())).toString());
        back_coordinate_z_2 = new TextField();
        back_coordinate_z_2.setText((new Double(some_global.getBackCoordinateZ2())).toString());        
    }

    public void createCoordinateHelps()
    {
        // TOP
        top_x_1 = new Label("X min");
        top_x_2 = new Label("X max");
        top_y_1 = new Label("Y min");
        top_y_2 = new Label("Y max");
        top_z_1 = new Label("Z min");
        top_z_2 = new Label("Z max");
        
        top_time_function_type_label = new Label("time function type");
        top_loading_time_portion_label = new Label("loading time portion");
        top_relaxation_time_portion_label = new Label("relaxation time portion");
        
        // BOTTOM
        bottom_x_1 = new Label("X min");
        bottom_x_2 = new Label("X max");
        bottom_y_1 = new Label("Y min");
        bottom_y_2 = new Label("Y max");
        bottom_z_1 = new Label("Z min");
        bottom_z_2 = new Label("Z max");
        bottom_time_function_type_label = new Label("time function type");
        bottom_loading_time_portion_label = new Label("loading time portion");
        bottom_relaxation_time_portion_label = new Label("relaxation time portion");
        
        // LEFT
        left_x_1 = new Label("X min");
        left_x_2 = new Label("X max");
        left_y_1 = new Label("Y min");
        left_y_2 = new Label("Y max");
        left_z_1 = new Label("Z min");
        left_z_2 = new Label("Z max");
        left_time_function_type_label = new Label("time function type");
        left_loading_time_portion_label = new Label("loading time portion");
        left_relaxation_time_portion_label = new Label("relaxation time portion");
        
        // RIGHT
        right_x_1 = new Label("X min");
        right_x_2 = new Label("X max");
        right_y_1 = new Label("Y min");
        right_y_2 = new Label("Y max");
        right_z_1 = new Label("Z min");
        right_z_2 = new Label("Z max");
        right_time_function_type_label = new Label("time function type");
        right_loading_time_portion_label = new Label("loading time portion");
        right_relaxation_time_portion_label = new Label("relaxation time portion");
        
        // FRONT
        front_x_1 = new Label("X min");
        front_x_2 = new Label("X max");
        front_y_1 = new Label("Y min");
        front_y_2 = new Label("Y max");
        front_z_1 = new Label("Z min");
        front_z_2 = new Label("Z max");
        
        right_time_function_type_label = new Label("time function type");
        right_loading_time_portion_label = new Label("loading time portion");
        right_relaxation_time_portion_label = new Label("relaxation time portion");
        // BACK
        back_x_1 = new Label("X min");
        back_x_2 = new Label("X max");
        back_y_1 = new Label("Y min");
        back_y_2 = new Label("Y max");
        back_z_1 = new Label("Z min");
        back_z_2 = new Label("Z max");
        right_time_function_type_label = new Label("time function type");
        right_loading_time_portion_label = new Label("loading time portion");
        right_relaxation_time_portion_label = new Label("relaxation time portion");        
    }        

    /** Create helps about adiobatic boundary or not
     */
    public void createAdiobaticInfo()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createAdiobaticInfo: start");

        top_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        bottom_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        left_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        right_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        front_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);
        back_adiobatic_or_not = new Label(UICommon.ADIOBATIC_PROPERTIES);        
    }

    /** Create box for choose material for not adiobatic boundary
     */
    public void createNotAdiobaticMaterialBox()
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

    /*
     * Create buttons: "Save as" and "OK" or "Cancel"
     * @param name - name of second button
     */

    public void createButtonsForParamsOfBoundaryConditions()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createButtonsForParamsOfBoundaryConditions: start");

        // Create button "Save"
        button_save = new Button(UICommon.BUTTON_SAVE_NAME);
        button_ok = new Button(UICommon.BUTTON_OK_NAME);
    }

    /*
     * Add elements on frame
     */

    public void addElementsForParametersOfBoundaryConditions()
    {
        System.out.println("RecCABoundaryConditionsParams: method: addElementsForParametersOfBoundaryConditions: start");        
        
        tab_pane = new TabPane();
        
        top = new Tab("Top Facet");
        top.setClosable(false);
        top.setContent(getTopTabLayout());
        
        left = new Tab("Left Facet");
        left.setClosable(false);
        left.setContent(getLeftTabLayout());
        
        right = new Tab("Right Facet");
        right.setClosable(false);
        right.setContent(getRightTabLayout());
        
        bottom = new Tab("Bottom Facet");
        bottom.setClosable(false);
        bottom.setContent(getBottomTabLayout());
        
        front = new Tab("Front Facet");
        front.setClosable(false);
        front.setContent(getFrontTabLayout());
        
        back = new Tab("Back Facet");
        back.setClosable(false);
        back.setContent(getBackTabLayout());
        
        tab_pane.getTabs().addAll(top, left, right, bottom, front, back);
        
        VBox geom_type = new VBox(parallelepiped_tank, vertical_ellyptic_tank, horizon_triangle_tank, horizon_circle_tank);
        geom_type.setPadding(new Insets(30, 10, 30, 10));
        geom_type.setAlignment(Pos.CENTER_LEFT);
        
        button_save.setPadding(new Insets(5, 30, 5, 30));
        HBox bottom_layout = new HBox(button_save);
        bottom_layout.setPadding(new Insets(30, 10, 30, 10));
        bottom_layout.setAlignment(Pos.CENTER);
        
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(
                tab_pane,
                new Separator(),
                geom_type,
                new Separator(),
                bottom_layout                
        );        
    }
    
    public GridPane getTopTabLayout(){
        
        top_coordinate_x_1.setMaxWidth(60.0d);
        top_coordinate_x_2.setMaxWidth(60.0d);
        top_coordinate_y_1.setMaxWidth(60.0d);
        top_coordinate_y_2.setMaxWidth(60.0d);
        top_coordinate_z_1.setMaxWidth(60.0d);
        top_coordinate_z_2.setMaxWidth(60.0d);

        top_loading_time_portion.setMaxWidth(50.0d);
        top_relaxation_time_portion.setMaxWidth(50.0d);
        top_time_function_type.setMaxWidth(50.0d);

        top_mechanical_loading.setMaxWidth(50.0d);
        top_min_mechanical_loading.setMaxWidth(50.0d);
        top_thermal_influx.setMaxWidth(50.0d);
        top_min_thermal_influx.setMaxWidth(50.0d);

        top_adiobatic_material.setPadding(new Insets(8, 30, 8, 30));

        Label max_lbl1 = new Label("max:");
        Label min_lbl1 = new Label("min:");

        Label max_lbl2 = new Label("max:");
        Label min_lbl2 = new Label("min:");

        Label l1 = new Label("max X:");
        Label l2 = new Label("min X:");
        Label l3 = new Label("max Y:");
        Label l4 = new Label("min Y:");
        Label l5 = new Label("max Z:");
        Label l6 = new Label("min Z:");

        Label l7 = new Label("Time Function Type:");
        Label l8 = new Label("Loading Time Portion:");
        Label l9 = new Label("Relaxation Time Portion:");

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(5, 0, 5, 0));
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 5, 5, 5));
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 5, 5, 5));
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(10, 10, 10, 10));
        sep3.setOrientation(Orientation.VERTICAL);
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 5, 5, 5));

        GridPane.setConstraints(top_check_mechanical_loading, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl1, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(top_mechanical_loading, 2, 0, 1, 1);
        GridPane.setConstraints(top_min_mechanical_loading, 2, 1, 1, 1);
        GridPane.setConstraints(top_stress, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_strain, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep1, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(top_check_thermal_influx, 0, 4, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl2, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 1, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(top_thermal_influx, 2, 4, 1, 1);
        GridPane.setConstraints(top_min_thermal_influx, 2, 5, 1, 1);
        GridPane.setConstraints(top_temperature, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_energy, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep3, 3, 0, 1, 6, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(l1, 4, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 4, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 4, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 4, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 4, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 4, 5, 1, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(top_coordinate_x_1, 5, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_coordinate_x_2, 5, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_coordinate_y_1, 5, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_coordinate_y_2, 5, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_coordinate_z_1, 5, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_coordinate_z_2, 5, 5, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 0, 7, 6, 1);

        GridPane.setConstraints(l7, 0, 8, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l8, 0, 9, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l9, 0, 10, 2, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(top_time_function_type, 2, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_loading_time_portion, 2, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_relaxation_time_portion, 2, 10, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep4, 0, 11, 6, 1);
        GridPane.setConstraints(top_adiobatic_or_not, 0, 12, 5, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_adiobatic_material, 0, 13, 5, 1, HPos.CENTER, VPos.CENTER);

        layout.getChildren().addAll(top_check_mechanical_loading,
                top_mechanical_loading,
                top_min_mechanical_loading,
                max_lbl1, min_lbl1, max_lbl2, min_lbl2,
                top_stress,top_strain,
                sep1, sep3, sep2, sep4,
                top_check_thermal_influx,
                top_thermal_influx,
                top_min_thermal_influx,
                top_temperature,top_energy,
                l1, l2, l3, l4, l5, l6, l7, l8, l9, top_adiobatic_or_not,
                top_coordinate_x_1,
                top_coordinate_x_2,
                top_coordinate_y_1,
                top_coordinate_y_2,
                top_coordinate_z_1,
                top_coordinate_z_2,
                top_time_function_type,
                top_loading_time_portion,
                top_relaxation_time_portion,
                top_adiobatic_material);

        layout.setVgap(5.0d);
        layout.setHgap(5.0d);
        layout.setAlignment(Pos.CENTER);
        
        return layout;
    }
    
    public GridPane getLeftTabLayout(){
        
        left_coordinate_x_1.setMaxWidth(60.0d);
        left_coordinate_x_2.setMaxWidth(60.0d);
        left_coordinate_y_1.setMaxWidth(60.0d);
        left_coordinate_y_2.setMaxWidth(60.0d);
        left_coordinate_z_1.setMaxWidth(60.0d);
        left_coordinate_z_2.setMaxWidth(60.0d);

        left_loading_time_portion.setMaxWidth(50.0d);
        left_relaxation_time_portion.setMaxWidth(50.0d);
        left_time_function_type.setMaxWidth(50.0d);

        left_mechanical_loading.setMaxWidth(50.0d);
        left_min_mechanical_loading.setMaxWidth(50.0d);
        left_thermal_influx.setMaxWidth(50.0d);
        left_min_thermal_influx.setMaxWidth(50.0d);

        left_adiobatic_material.setPadding(new Insets(8, 30, 8, 30));

        Label max_lbl1 = new Label("max:");
        Label min_lbl1 = new Label("min:");

        Label max_lbl2 = new Label("max:");
        Label min_lbl2 = new Label("min:");

        Label l1 = new Label("max X:");
        Label l2 = new Label("min X:");
        Label l3 = new Label("max Y:");
        Label l4 = new Label("min Y:");
        Label l5 = new Label("max Z:");
        Label l6 = new Label("min Z:");

        Label l7 = new Label("Time Function Type:");
        Label l8 = new Label("Loading Time Portion:");
        Label l9 = new Label("Relaxation Time Portion:");

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(5, 0, 5, 0));
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 5, 5, 5));
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 5, 5, 5));
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(10, 10, 10, 10));
        sep3.setOrientation(Orientation.VERTICAL);
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 5, 5, 5));

        GridPane.setConstraints(left_check_mechanical_loading, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl1, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(left_mechanical_loading, 2, 0, 1, 1);
        GridPane.setConstraints(left_min_mechanical_loading, 2, 1, 1, 1);
        GridPane.setConstraints(left_stress, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_strain, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep1, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(left_check_thermal_influx, 0, 4, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl2, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 1, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(left_thermal_influx, 2, 4, 1, 1);
        GridPane.setConstraints(left_min_thermal_influx, 2, 5, 1, 1);
        GridPane.setConstraints(left_temperature, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_energy, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep3, 3, 0, 1, 6, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(l1, 4, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 4, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 4, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 4, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 4, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 4, 5, 1, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(left_coordinate_x_1, 5, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_coordinate_x_2, 5, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_coordinate_y_1, 5, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_coordinate_y_2, 5, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_coordinate_z_1, 5, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_coordinate_z_2, 5, 5, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 0, 7, 6, 1);

        GridPane.setConstraints(l7, 0, 8, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l8, 0, 9, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l9, 0, 10, 2, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(left_time_function_type, 2, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_loading_time_portion, 2, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_relaxation_time_portion, 2, 10, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep4, 0, 11, 6, 1);
        GridPane.setConstraints(left_adiobatic_or_not, 0, 12, 5, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_adiobatic_material, 0, 13, 5, 1, HPos.CENTER, VPos.CENTER);

        layout.getChildren().addAll(left_check_mechanical_loading,
                left_mechanical_loading,
                left_min_mechanical_loading,
                max_lbl1, min_lbl1, max_lbl2, min_lbl2,
                left_stress,left_strain,
                sep1, sep3, sep2, sep4,
                left_check_thermal_influx,
                left_thermal_influx,
                left_min_thermal_influx,
                left_temperature,left_energy,
                l1, l2, l3, l4, l5, l6, l7, l8, l9, left_adiobatic_or_not,
                left_coordinate_x_1,
                left_coordinate_x_2,
                left_coordinate_y_1,
                left_coordinate_y_2,
                left_coordinate_z_1,
                left_coordinate_z_2,
                left_time_function_type,
                left_loading_time_portion,
                left_relaxation_time_portion,
                left_adiobatic_material);

        layout.setVgap(5.0d);
        layout.setHgap(5.0d);
        layout.setAlignment(Pos.CENTER);
        
        return layout;
    }
    
    public GridPane getRightTabLayout(){
        
        right_coordinate_x_1.setMaxWidth(60.0d);
        right_coordinate_x_2.setMaxWidth(60.0d);
        right_coordinate_y_1.setMaxWidth(60.0d);
        right_coordinate_y_2.setMaxWidth(60.0d);
        right_coordinate_z_1.setMaxWidth(60.0d);
        right_coordinate_z_2.setMaxWidth(60.0d);

        right_loading_time_portion.setMaxWidth(50.0d);
        right_relaxation_time_portion.setMaxWidth(50.0d);
        right_time_function_type.setMaxWidth(50.0d);

        right_mechanical_loading.setMaxWidth(50.0d);
        right_min_mechanical_loading.setMaxWidth(50.0d);
        right_thermal_influx.setMaxWidth(50.0d);
        right_min_thermal_influx.setMaxWidth(50.0d);

        right_adiobatic_material.setPadding(new Insets(8, 30, 8, 30));

        Label max_lbl1 = new Label("max:");
        Label min_lbl1 = new Label("min:");

        Label max_lbl2 = new Label("max:");
        Label min_lbl2 = new Label("min:");

        Label l1 = new Label("max X:");
        Label l2 = new Label("min X:");
        Label l3 = new Label("max Y:");
        Label l4 = new Label("min Y:");
        Label l5 = new Label("max Z:");
        Label l6 = new Label("min Z:");

        Label l7 = new Label("Time Function Type:");
        Label l8 = new Label("Loading Time Portion:");
        Label l9 = new Label("Relaxation Time Portion:");

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(5, 0, 5, 0));
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 5, 5, 5));
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 5, 5, 5));
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(10, 10, 10, 10));
        sep3.setOrientation(Orientation.VERTICAL);
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 5, 5, 5));

        GridPane.setConstraints(right_check_mechanical_loading, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl1, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(right_mechanical_loading, 2, 0, 1, 1);
        GridPane.setConstraints(right_min_mechanical_loading, 2, 1, 1, 1);
        GridPane.setConstraints(right_stress, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_strain, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep1, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(right_check_thermal_influx, 0, 4, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl2, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 1, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(right_thermal_influx, 2, 4, 1, 1);
        GridPane.setConstraints(right_min_thermal_influx, 2, 5, 1, 1);
        GridPane.setConstraints(right_temperature, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_energy, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep3, 3, 0, 1, 6, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(l1, 4, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 4, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 4, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 4, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 4, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 4, 5, 1, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(right_coordinate_x_1, 5, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_coordinate_x_2, 5, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_coordinate_y_1, 5, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_coordinate_y_2, 5, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_coordinate_z_1, 5, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_coordinate_z_2, 5, 5, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 0, 7, 6, 1);

        GridPane.setConstraints(l7, 0, 8, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l8, 0, 9, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l9, 0, 10, 2, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(right_time_function_type, 2, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_loading_time_portion, 2, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_relaxation_time_portion, 2, 10, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep4, 0, 11, 6, 1);
        GridPane.setConstraints(right_adiobatic_or_not, 0, 12, 5, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_adiobatic_material, 0, 13, 5, 1, HPos.CENTER, VPos.CENTER);

        layout.getChildren().addAll(right_check_mechanical_loading,
                right_mechanical_loading,
                right_min_mechanical_loading,
                max_lbl1, min_lbl1, max_lbl2, min_lbl2,
                right_stress,right_strain,
                sep1, sep3, sep2, sep4,
                right_check_thermal_influx,
                right_thermal_influx,
                right_min_thermal_influx,
                right_temperature,right_energy,
                l1, l2, l3, l4, l5, l6, l7, l8, l9, right_adiobatic_or_not,
                right_coordinate_x_1,
                right_coordinate_x_2,
                right_coordinate_y_1,
                right_coordinate_y_2,
                right_coordinate_z_1,
                right_coordinate_z_2,
                right_time_function_type,
                right_loading_time_portion,
                right_relaxation_time_portion,
                right_adiobatic_material);

        layout.setVgap(5.0d);
        layout.setHgap(5.0d);
        layout.setAlignment(Pos.CENTER);
        
        return layout;
    }
    
    public GridPane getBottomTabLayout(){
        
        bottom_coordinate_x_1.setMaxWidth(60.0d);
        bottom_coordinate_x_2.setMaxWidth(60.0d);
        bottom_coordinate_y_1.setMaxWidth(60.0d);
        bottom_coordinate_y_2.setMaxWidth(60.0d);
        bottom_coordinate_z_1.setMaxWidth(60.0d);
        bottom_coordinate_z_2.setMaxWidth(60.0d);

        bottom_loading_time_portion.setMaxWidth(50.0d);
        bottom_relaxation_time_portion.setMaxWidth(50.0d);
        bottom_time_function_type.setMaxWidth(50.0d);

        bottom_mechanical_loading.setMaxWidth(50.0d);
        bottom_min_mechanical_loading.setMaxWidth(50.0d);
        bottom_thermal_influx.setMaxWidth(50.0d);
        bottom_min_thermal_influx.setMaxWidth(50.0d);

        bottom_adiobatic_material.setPadding(new Insets(8, 30, 8, 30));

        Label max_lbl1 = new Label("max:");
        Label min_lbl1 = new Label("min:");

        Label max_lbl2 = new Label("max:");
        Label min_lbl2 = new Label("min:");

        Label l1 = new Label("max X:");
        Label l2 = new Label("min X:");
        Label l3 = new Label("max Y:");
        Label l4 = new Label("min Y:");
        Label l5 = new Label("max Z:");
        Label l6 = new Label("min Z:");

        Label l7 = new Label("Time Function Type:");
        Label l8 = new Label("Loading Time Portion:");
        Label l9 = new Label("Relaxation Time Portion:");

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(5, 0, 5, 0));
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 5, 5, 5));
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 5, 5, 5));
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(10, 10, 10, 10));
        sep3.setOrientation(Orientation.VERTICAL);
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 5, 5, 5));

        GridPane.setConstraints(bottom_check_mechanical_loading, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl1, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(bottom_mechanical_loading, 2, 0, 1, 1);
        GridPane.setConstraints(bottom_min_mechanical_loading, 2, 1, 1, 1);
        GridPane.setConstraints(bottom_stress, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_strain, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep1, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(bottom_check_thermal_influx, 0, 4, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl2, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 1, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(bottom_thermal_influx, 2, 4, 1, 1);
        GridPane.setConstraints(bottom_min_thermal_influx, 2, 5, 1, 1);
        GridPane.setConstraints(bottom_temperature, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_energy, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep3, 3, 0, 1, 6, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(l1, 4, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 4, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 4, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 4, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 4, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 4, 5, 1, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(bottom_coordinate_x_1, 5, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_coordinate_x_2, 5, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_coordinate_y_1, 5, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_coordinate_y_2, 5, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_coordinate_z_1, 5, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_coordinate_z_2, 5, 5, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 0, 7, 6, 1);

        GridPane.setConstraints(l7, 0, 8, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l8, 0, 9, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l9, 0, 10, 2, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(bottom_time_function_type, 2, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_loading_time_portion, 2, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_relaxation_time_portion, 2, 10, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep4, 0, 11, 6, 1);
        GridPane.setConstraints(bottom_adiobatic_or_not, 0, 12, 5, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_adiobatic_material, 0, 13, 5, 1, HPos.CENTER, VPos.CENTER);

        layout.getChildren().addAll(bottom_check_mechanical_loading,
                bottom_mechanical_loading,
                bottom_min_mechanical_loading,
                max_lbl1, min_lbl1, max_lbl2, min_lbl2,
                bottom_stress,bottom_strain,
                sep1, sep3, sep2, sep4,
                bottom_check_thermal_influx,
                bottom_thermal_influx,
                bottom_min_thermal_influx,
                bottom_temperature,bottom_energy,
                l1, l2, l3, l4, l5, l6, l7, l8, l9, bottom_adiobatic_or_not,
                bottom_coordinate_x_1,
                bottom_coordinate_x_2,
                bottom_coordinate_y_1,
                bottom_coordinate_y_2,
                bottom_coordinate_z_1,
                bottom_coordinate_z_2,
                bottom_time_function_type,
                bottom_loading_time_portion,
                bottom_relaxation_time_portion,
                bottom_adiobatic_material);

        layout.setVgap(5.0d);
        layout.setHgap(5.0d);
        layout.setAlignment(Pos.CENTER);
        
        return layout;
    }
    
    public GridPane getFrontTabLayout(){
        
        front_coordinate_x_1.setMaxWidth(60.0d);
        front_coordinate_x_2.setMaxWidth(60.0d);
        front_coordinate_y_1.setMaxWidth(60.0d);
        front_coordinate_y_2.setMaxWidth(60.0d);
        front_coordinate_z_1.setMaxWidth(60.0d);
        front_coordinate_z_2.setMaxWidth(60.0d);

        front_loading_time_portion.setMaxWidth(50.0d);
        front_relaxation_time_portion.setMaxWidth(50.0d);
        front_time_function_type.setMaxWidth(50.0d);

        front_mechanical_loading.setMaxWidth(50.0d);
        front_min_mechanical_loading.setMaxWidth(50.0d);
        front_thermal_influx.setMaxWidth(50.0d);
        front_min_thermal_influx.setMaxWidth(50.0d);

        front_adiobatic_material.setPadding(new Insets(8, 30, 8, 30));

        Label max_lbl1 = new Label("max:");
        Label min_lbl1 = new Label("min:");

        Label max_lbl2 = new Label("max:");
        Label min_lbl2 = new Label("min:");

        Label l1 = new Label("max X:");
        Label l2 = new Label("min X:");
        Label l3 = new Label("max Y:");
        Label l4 = new Label("min Y:");
        Label l5 = new Label("max Z:");
        Label l6 = new Label("min Z:");

        Label l7 = new Label("Time Function Type:");
        Label l8 = new Label("Loading Time Portion:");
        Label l9 = new Label("Relaxation Time Portion:");

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(5, 0, 5, 0));
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 5, 5, 5));
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 5, 5, 5));
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(10, 10, 10, 10));
        sep3.setOrientation(Orientation.VERTICAL);
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 5, 5, 5));

        GridPane.setConstraints(front_check_mechanical_loading, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl1, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(front_mechanical_loading, 2, 0, 1, 1);
        GridPane.setConstraints(front_min_mechanical_loading, 2, 1, 1, 1);
        GridPane.setConstraints(front_stress, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_strain, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep1, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(front_check_thermal_influx, 0, 4, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl2, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 1, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(front_thermal_influx, 2, 4, 1, 1);
        GridPane.setConstraints(front_min_thermal_influx, 2, 5, 1, 1);
        GridPane.setConstraints(front_temperature, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_energy, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep3, 3, 0, 1, 6, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(l1, 4, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 4, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 4, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 4, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 4, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 4, 5, 1, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(front_coordinate_x_1, 5, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_coordinate_x_2, 5, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_coordinate_y_1, 5, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_coordinate_y_2, 5, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_coordinate_z_1, 5, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_coordinate_z_2, 5, 5, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 0, 7, 6, 1);

        GridPane.setConstraints(l7, 0, 8, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l8, 0, 9, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l9, 0, 10, 2, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(front_time_function_type, 2, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_loading_time_portion, 2, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_relaxation_time_portion, 2, 10, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep4, 0, 11, 6, 1);
        GridPane.setConstraints(front_adiobatic_or_not, 0, 12, 5, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_adiobatic_material, 0, 13, 5, 1, HPos.CENTER, VPos.CENTER);

        layout.getChildren().addAll(front_check_mechanical_loading,
                front_mechanical_loading,
                front_min_mechanical_loading,
                max_lbl1, min_lbl1, max_lbl2, min_lbl2,
                front_stress,front_strain,
                sep1, sep3, sep2, sep4,
                front_check_thermal_influx,
                front_thermal_influx,
                front_min_thermal_influx,
                front_temperature,front_energy,
                l1, l2, l3, l4, l5, l6, l7, l8, l9, front_adiobatic_or_not,
                front_coordinate_x_1,
                front_coordinate_x_2,
                front_coordinate_y_1,
                front_coordinate_y_2,
                front_coordinate_z_1,
                front_coordinate_z_2,
                front_time_function_type,
                front_loading_time_portion,
                front_relaxation_time_portion,
                front_adiobatic_material);

        layout.setVgap(5.0d);
        layout.setHgap(5.0d);
        layout.setAlignment(Pos.CENTER);
        
        return layout;
    }
    
    public GridPane getBackTabLayout(){
        
        back_coordinate_x_1.setMaxWidth(60.0d);
        back_coordinate_x_2.setMaxWidth(60.0d);
        back_coordinate_y_1.setMaxWidth(60.0d);
        back_coordinate_y_2.setMaxWidth(60.0d);
        back_coordinate_z_1.setMaxWidth(60.0d);
        back_coordinate_z_2.setMaxWidth(60.0d);

        back_loading_time_portion.setMaxWidth(50.0d);
        back_relaxation_time_portion.setMaxWidth(50.0d);
        back_time_function_type.setMaxWidth(50.0d);

        back_mechanical_loading.setMaxWidth(50.0d);
        back_min_mechanical_loading.setMaxWidth(50.0d);
        back_thermal_influx.setMaxWidth(50.0d);
        back_min_thermal_influx.setMaxWidth(50.0d);

        back_adiobatic_material.setPadding(new Insets(8, 30, 8, 30));

        Label max_lbl1 = new Label("max:");
        Label min_lbl1 = new Label("min:");

        Label max_lbl2 = new Label("max:");
        Label min_lbl2 = new Label("min:");

        Label l1 = new Label("max X:");
        Label l2 = new Label("min X:");
        Label l3 = new Label("max Y:");
        Label l4 = new Label("min Y:");
        Label l5 = new Label("max Z:");
        Label l6 = new Label("min Z:");

        Label l7 = new Label("Time Function Type:");
        Label l8 = new Label("Loading Time Portion:");
        Label l9 = new Label("Relaxation Time Portion:");

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(5, 0, 5, 0));
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 5, 5, 5));
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 5, 5, 5));
        Separator sep3 = new Separator();
        sep3.setPadding(new Insets(10, 10, 10, 10));
        sep3.setOrientation(Orientation.VERTICAL);
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(5, 5, 5, 5));

        GridPane.setConstraints(back_check_mechanical_loading, 0, 0, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl1, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl1, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(back_mechanical_loading, 2, 0, 1, 1);
        GridPane.setConstraints(back_min_mechanical_loading, 2, 1, 1, 1);
        GridPane.setConstraints(back_stress, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_strain, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep1, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(back_check_thermal_influx, 0, 4, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_lbl2, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_lbl2, 1, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(back_thermal_influx, 2, 4, 1, 1);
        GridPane.setConstraints(back_min_thermal_influx, 2, 5, 1, 1);
        GridPane.setConstraints(back_temperature, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_energy, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep3, 3, 0, 1, 6, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(l1, 4, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 4, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 4, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 4, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 4, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 4, 5, 1, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(back_coordinate_x_1, 5, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_coordinate_x_2, 5, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_coordinate_y_1, 5, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_coordinate_y_2, 5, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_coordinate_z_1, 5, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_coordinate_z_2, 5, 5, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 0, 7, 6, 1);

        GridPane.setConstraints(l7, 0, 8, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l8, 0, 9, 2, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l9, 0, 10, 2, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(back_time_function_type, 2, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_loading_time_portion, 2, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_relaxation_time_portion, 2, 10, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep4, 0, 11, 6, 1);
        GridPane.setConstraints(back_adiobatic_or_not, 0, 12, 5, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_adiobatic_material, 0, 13, 5, 1, HPos.CENTER, VPos.CENTER);

        layout.getChildren().addAll(back_check_mechanical_loading,
                back_mechanical_loading,
                back_min_mechanical_loading,
                max_lbl1, min_lbl1, max_lbl2, min_lbl2,
                back_stress,back_strain,
                sep1, sep3, sep2, sep4,
                back_check_thermal_influx,
                back_thermal_influx,
                back_min_thermal_influx,
                back_temperature,back_energy,
                l1, l2, l3, l4, l5, l6, l7, l8, l9, back_adiobatic_or_not,
                back_coordinate_x_1,
                back_coordinate_x_2,
                back_coordinate_y_1,
                back_coordinate_y_2,
                back_coordinate_z_1,
                back_coordinate_z_2,
                back_time_function_type,
                back_loading_time_portion,
                back_relaxation_time_portion,
                back_adiobatic_material);

        layout.setVgap(5.0d);
        layout.setHgap(5.0d);
        layout.setAlignment(Pos.CENTER);
        
        return layout;
    }
    
    /** Create saving dialog window for saving boundary parameters
     */
    public void savingDialofOfParamsForBoundaryConditions()
    {
        System.out.println("RecCATankParameters: method: savingDialofOfParamsForBoundaryConditions: start");
        this.close();
        SavingDialog save = new SavingDialog(textfield_for_file_location);
        save.save_btn.setOnAction(e -> {
            if(isNameCorrect(textfield_for_file_location))
            {
                try{
                    /** Remember path of choosed boundary conditions
                    */
                   transfer_data_bank.getUIInterface().setTankPath(textfield_for_file_location.getText());

                   /** Save inputed parameters in corresponding file
                    */
                   saving = new SaveParams(some_global, transfer_data_bank);
                   save.close();
                    new Alert(AlertType.INFORMATION, "File successfuly created").showAndWait();
                }
                catch(Exception ex){
                    new Alert(AlertType.ERROR, "Failed to create file").showAndWait();
                }
            }
        });        
    }
    

    /** Create checkboxes for choosing what parameters ("Mechanical loading",
     * "Thermal influx") input in corresponding side of boundary
     * ("Top", "Bottom", "Left", "Right", "Front" or "Back")
     */
    public void createCheckBoxesForBoundary()
    {
        System.out.println("RecCABoundaryConditionsParams: method: createCheckBoxesForBoundary: start");

        top_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        top_check_mechanical_loading.setSelected(some_global.getTopCheckMechanicalLoading());
        System.out.println("Top Checked or not = "+some_global.getTopCheckMechanicalLoading());

        top_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        top_check_thermal_influx.setSelected(some_global.getTopCheckThermalInflux());

        bottom_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        bottom_check_mechanical_loading.setSelected(some_global.getBottomCheckMechanicalLoading());

        bottom_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        bottom_check_thermal_influx.setSelected(some_global.getBottomCheckThermalInflux());

        left_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        left_check_mechanical_loading.setSelected(some_global.getLeftCheckMechanicalLoading());

        left_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        left_check_thermal_influx.setSelected(some_global.getLeftCheckThermalInflux());

        right_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        right_check_mechanical_loading.setSelected(some_global.getRightCheckMechanicalLoading());

        right_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        right_check_thermal_influx.setSelected(some_global.getRightCheckThermalInflux());

        front_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        front_check_mechanical_loading.setSelected(some_global.getFrontCheckMechanicalLoading());

        front_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        front_check_thermal_influx.setSelected(some_global.getFrontCheckThermalInflux());

        back_check_mechanical_loading = new CheckBox(UICommon.MECHANICAL_LOADING_NAME);
        back_check_mechanical_loading.setSelected(some_global.getBackCheckMechanicalLoading());

        back_check_thermal_influx = new CheckBox(UICommon.THERMAL_ENERGY_NAME);
        back_check_thermal_influx.setSelected(some_global.getBackCheckThermalInflux());
        
        System.out.println();
        System.out.println(UICommon.THERMAL_ENERGY_NAME +" = "+ some_global.getBackCheckThermalInflux());
        System.out.println();
    }

    /** Create radio buttons for choose in mechanical loading: stress
     * or strain, and in thermal influx: temperature or energy
     */
    public void createTypeTaskChoose()
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
        
        /** Buttons for choice of tank geometrical type */
        parallelepiped_tank    = new RadioButton("Parallelepiped Tanks");
        vertical_ellyptic_tank = new RadioButton("Vertical Ellyptic Tanks");
        horizon_triangle_tank  = new RadioButton("Horizontal Triangle Tanks");
        horizon_circle_tank    = new RadioButton("Horizontal Circle Tanks");
        
        /** Group of buttons for choice of geometrical type of tank */
        tank_geometrical_types_group = new ToggleGroup();
        tank_geometrical_types_group.getToggles().addAll(
                parallelepiped_tank,
                vertical_ellyptic_tank,
                horizon_triangle_tank,
                horizon_circle_tank
        );
        
        // Create corresponding button groups
        top_mechanical_loading_group = new ToggleGroup();
        top_mechanical_loading_group.getToggles().addAll(top_stress, top_strain);
        
        top_thermal_influx_group = new ToggleGroup();
        top_thermal_influx_group.getToggles().addAll(top_temperature, top_energy);
        
        bottom_mechanical_loading_group = new ToggleGroup();
        bottom_mechanical_loading_group.getToggles().addAll(bottom_stress, bottom_strain);
        bottom_thermal_influx_group = new ToggleGroup();
        bottom_thermal_influx_group.getToggles().addAll(bottom_temperature, bottom_energy);
        
        left_mechanical_loading_group = new ToggleGroup();
        left_mechanical_loading_group.getToggles().addAll(left_stress, left_strain);
        left_thermal_influx_group = new ToggleGroup();
        left_thermal_influx_group.getToggles().addAll(left_temperature, left_energy);

        right_mechanical_loading_group = new ToggleGroup();
        right_mechanical_loading_group.getToggles().addAll(right_stress, right_strain);
        right_thermal_influx_group = new ToggleGroup();
        right_thermal_influx_group.getToggles().addAll(right_temperature, right_energy);

        front_mechanical_loading_group = new ToggleGroup();
        front_mechanical_loading_group.getToggles().addAll(front_stress, front_strain);
        front_thermal_influx_group = new ToggleGroup();
        front_thermal_influx_group.getToggles().addAll(front_temperature, front_energy);

        back_mechanical_loading_group = new ToggleGroup();
        back_mechanical_loading_group.getToggles().addAll(back_stress, back_strain);
        back_thermal_influx_group = new ToggleGroup();
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
        
        // Buttons for choice of tank type
        parallelepiped_tank.setSelected(some_global.getCheckParallelepipedTankType());
        vertical_ellyptic_tank.setSelected(some_global.getCheckVerticalEllypticTankType());
        horizon_triangle_tank.setSelected(some_global.getCheckHorizonTriangleTankType());
        horizon_circle_tank.setSelected(some_global.getCheckHorizonCircleTankType());
        
    }

    /** Check selected checkboxes,
     * if check box is selected, then corresponding
     * parameter value and task type can be changed and vice versa
     */
    public void setCheckedPositions()
    {
        System.out.println("RecCATankConditionsParams: method: setCheckedPositions: start");
        
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
        
        top_time_function_type.disableProperty().bind(top_check_mechanical_loading.selectedProperty().not().and(top_check_thermal_influx.selectedProperty().not()));
        top_loading_time_portion.disableProperty().bind(top_check_mechanical_loading.selectedProperty().not().and(top_check_thermal_influx.selectedProperty().not()));
        top_relaxation_time_portion.disableProperty().bind(top_check_mechanical_loading.selectedProperty().not().and(top_check_thermal_influx.selectedProperty().not()));        
        top_adiobatic_material.visibleProperty().bind(top_check_mechanical_loading.selectedProperty().or(top_check_thermal_influx.selectedProperty()));
        
        top_check_mechanical_loading.setOnAction(e -> {
            if(top_check_mechanical_loading.isSelected() | top_check_thermal_influx.isSelected())        
                top_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                    
            else top_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);
        });
        
        top_check_thermal_influx.setOnAction(e -> {
            if(top_check_mechanical_loading.isSelected() | top_check_thermal_influx.isSelected())        
                top_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);                    
            else top_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);
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
        bottom_time_function_type.disableProperty().bind(bottom_check_mechanical_loading.selectedProperty().not().and(bottom_check_thermal_influx.selectedProperty().not()));        
        bottom_loading_time_portion.disableProperty().bind(bottom_check_mechanical_loading.selectedProperty().not().and(bottom_check_thermal_influx.selectedProperty().not()));        
        bottom_relaxation_time_portion.disableProperty().bind(bottom_check_mechanical_loading.selectedProperty().not().and(bottom_check_thermal_influx.selectedProperty().not()));        
        bottom_adiobatic_material.visibleProperty().bind(bottom_check_mechanical_loading.selectedProperty().and(bottom_check_thermal_influx.selectedProperty()));
        
        if(bottom_check_mechanical_loading.isSelected()|bottom_check_thermal_influx.isSelected())
            bottom_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);            
        else bottom_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);
            

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
        left_time_function_type.disableProperty().bind(left_check_mechanical_loading.selectedProperty().not().and(left_check_thermal_influx.selectedProperty().not()));        
        left_loading_time_portion.disableProperty().bind(left_check_mechanical_loading.selectedProperty().not().and(left_check_thermal_influx.selectedProperty().not()));        
        left_relaxation_time_portion.disableProperty().bind(left_check_mechanical_loading.selectedProperty().not().and(left_check_thermal_influx.selectedProperty().not()));        
        left_adiobatic_material.visibleProperty().bind(left_check_mechanical_loading.selectedProperty().and(left_check_thermal_influx.selectedProperty()));
        
        if(left_check_mechanical_loading.isSelected()|left_check_thermal_influx.isSelected())
            left_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else left_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);
            
        
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
        right_time_function_type.disableProperty().bind(right_check_mechanical_loading.selectedProperty().not().and(right_check_thermal_influx.selectedProperty().not()));        
        right_loading_time_portion.disableProperty().bind(right_check_mechanical_loading.selectedProperty().not().and(right_check_thermal_influx.selectedProperty().not()));        
        right_relaxation_time_portion.disableProperty().bind(right_check_mechanical_loading.selectedProperty().not().and(right_check_thermal_influx.selectedProperty().not()));        
        right_adiobatic_material.visibleProperty().bind(right_check_mechanical_loading.selectedProperty().and(right_check_thermal_influx.selectedProperty()));
        
        if(right_check_mechanical_loading.isSelected()|right_check_thermal_influx.isSelected())
            right_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);            
        else right_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);
            
        
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
        front_time_function_type.disableProperty().bind(front_check_mechanical_loading.selectedProperty().not().and(front_check_thermal_influx.selectedProperty().not()));        
        front_loading_time_portion.disableProperty().bind(front_check_mechanical_loading.selectedProperty().not().and(front_check_thermal_influx.selectedProperty().not()));        
        front_relaxation_time_portion.disableProperty().bind(front_check_mechanical_loading.selectedProperty().not().and(front_check_thermal_influx.selectedProperty().not()));        
        front_adiobatic_material.visibleProperty().bind(front_check_mechanical_loading.selectedProperty().and(front_check_thermal_influx.selectedProperty()));
        
        if(front_check_mechanical_loading.isSelected()|front_check_thermal_influx.isSelected())
            front_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);            
        else front_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);
            

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
        
        back_time_function_type.disableProperty().bind(back_check_mechanical_loading.selectedProperty().not().or(back_check_thermal_influx.selectedProperty().not()));        
        back_loading_time_portion.disableProperty().bind(back_check_mechanical_loading.selectedProperty().not().or(back_check_thermal_influx.selectedProperty().not()));        
        back_relaxation_time_portion.disableProperty().bind(back_check_mechanical_loading.selectedProperty().not().or(back_check_thermal_influx.selectedProperty().not()));        
        back_adiobatic_material.visibleProperty().bind(back_check_mechanical_loading.selectedProperty().or(back_check_thermal_influx.selectedProperty()));
        
        if(back_check_mechanical_loading.isSelected()|back_check_thermal_influx.isSelected())
            back_adiobatic_or_not.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);            
        else back_adiobatic_or_not.setText(UICommon.ADIOBATIC_PROPERTIES);
            
    }

    /** Remember inputed parameters in boundary conditions data bank
     */
    public void inputParams()
    {
        System.out.println("RecCABoundaryConditionsParams: method: inputParams: start");

        /** set input checkboxes
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
        
        // Buttons for choice of tank type        
        some_global.setParallelepipedTankType(parallelepiped_tank.isSelected());
        some_global.setVerticalEllypticTankType(vertical_ellyptic_tank.isSelected());
        some_global.setHorizonTriangleTankType(horizon_triangle_tank.isSelected());
        some_global.setHorizonCircleTankType(horizon_circle_tank.isSelected());        

        /** Set input parameters in data bank
         */
        some_global.setTopMinMechValue(Double.valueOf(top_min_mechanical_loading.getText()));
        some_global.setTopMechValue(Double.valueOf(top_mechanical_loading.getText()));
        some_global.setTopMinThermValue(Double.valueOf(top_min_thermal_influx.getText()));
        some_global.setTopThermValue(Double.valueOf(top_thermal_influx.getText()));
        some_global.setTopTimeFunctionType(Byte.valueOf(top_time_function_type.getText()));                
        some_global.setTopLoadingTimePortion(Double.valueOf(top_loading_time_portion.getText()));
        some_global.setTopRelaxationTimePortion(Double.valueOf(top_relaxation_time_portion.getText()));

        some_global.setBottomMinMechValue(Double.valueOf(bottom_min_mechanical_loading.getText()));
        some_global.setBottomMechValue(Double.valueOf(bottom_mechanical_loading.getText()));
        some_global.setBottomMinThermValue(Double.valueOf(bottom_min_thermal_influx.getText()));
        some_global.setBottomThermValue(Double.valueOf(bottom_thermal_influx.getText()));
        some_global.setBottomTimeFunctionType(Byte.valueOf(bottom_time_function_type.getText()));                
        some_global.setBottomLoadingTimePortion(Double.valueOf(bottom_loading_time_portion.getText()));
        some_global.setBottomRelaxationTimePortion(Double.valueOf(bottom_relaxation_time_portion.getText()));

        some_global.setLeftMinMechValue(Double.valueOf(left_min_mechanical_loading.getText()));
        some_global.setLeftMechValue(Double.valueOf(left_mechanical_loading.getText()));
        some_global.setLeftMinThermValue(Double.valueOf(left_min_thermal_influx.getText()));
        some_global.setLeftThermValue(Double.valueOf(left_thermal_influx.getText()));
        some_global.setLeftTimeFunctionType(Byte.valueOf(left_time_function_type.getText()));                
        some_global.setLeftLoadingTimePortion(Double.valueOf(left_loading_time_portion.getText()));
        some_global.setLeftRelaxationTimePortion(Double.valueOf(left_relaxation_time_portion.getText()));

        some_global.setRightMinMechValue(Double.valueOf(right_min_mechanical_loading.getText()));
        some_global.setRightMechValue(Double.valueOf(right_mechanical_loading.getText()));
        some_global.setRightMinThermValue(Double.valueOf(right_min_thermal_influx.getText()));
        some_global.setRightThermValue(Double.valueOf(right_thermal_influx.getText()));
        some_global.setRightTimeFunctionType(Byte.valueOf(right_time_function_type.getText()));                
        some_global.setRightLoadingTimePortion(Double.valueOf(right_loading_time_portion.getText()));
        some_global.setRightRelaxationTimePortion(Double.valueOf(right_relaxation_time_portion.getText()));

        some_global.setFrontMinMechValue(Double.valueOf(front_min_mechanical_loading.getText()));
        some_global.setFrontMechValue(Double.valueOf(front_mechanical_loading.getText()));
        some_global.setFrontMinThermValue(Double.valueOf(front_min_thermal_influx.getText()));
        some_global.setFrontThermValue(Double.valueOf(front_thermal_influx.getText()));
        some_global.setFrontTimeFunctionType(Byte.valueOf(front_time_function_type.getText()));                
        some_global.setFrontLoadingTimePortion(Double.valueOf(front_loading_time_portion.getText()));
        some_global.setFrontRelaxationTimePortion(Double.valueOf(front_relaxation_time_portion.getText()));

        some_global.setBackMinMechValue(Double.valueOf(back_min_mechanical_loading.getText()));
        some_global.setBackMechValue(Double.valueOf(back_mechanical_loading.getText()));
        some_global.setBackMinThermValue(Double.valueOf(back_min_thermal_influx.getText()));
        some_global.setBackThermValue(Double.valueOf(back_thermal_influx.getText()));
        some_global.setBackTimeFunctionType(Byte.valueOf(back_time_function_type.getText()));                
        some_global.setBackLoadingTimePortion(Double.valueOf(back_loading_time_portion.getText()));
        some_global.setBackRelaxationTimePortion(Double.valueOf(back_relaxation_time_portion.getText()));

        /** Remember data about non-adiabatic boundaries
         */
        if(!top_check_mechanical_loading.isSelected()&!top_check_thermal_influx.isSelected())
            some_global.setTopAdiabaticMaterial(this.transfer_data_bank.getUISpecimen().materials.get(0));
        else
            some_global.setTopAdiabaticMaterial(top_adiobatic_material.getText().toString());
        
        if(!this.bottom_check_mechanical_loading.isSelected()&!bottom_check_thermal_influx.isSelected())
        {
            some_global.setBottomAdiabaticMaterial(this.transfer_data_bank.getUISpecimen().materials.get(0));
        }
        else
            some_global.setBottomAdiabaticMaterial(bottom_adiobatic_material.getText().toString());
        if(!left_check_mechanical_loading.isSelected()&!left_check_thermal_influx.isSelected())
            some_global.setLeftAdiabaticMaterial(this.transfer_data_bank.getUISpecimen().materials.get(0));
        else
            some_global.setLeftAdiabaticMaterial(left_adiobatic_material.getText().toString());
        if(!right_check_mechanical_loading.isSelected()&!right_check_thermal_influx.isSelected())
            some_global.setRightAdiabaticMaterial(this.transfer_data_bank.getUISpecimen().materials.get(0));
        else
            some_global.setRightAdiabaticMaterial(right_adiobatic_material.getText().toString());
        if(!front_check_mechanical_loading.isSelected()&!front_check_thermal_influx.isSelected())
            some_global.setFrontAdiabaticMaterial(this.transfer_data_bank.getUISpecimen().materials.get(0));
        else
            some_global.setFrontAdiabaticMaterial(front_adiobatic_material.getText().toString());
        if(!back_check_mechanical_loading.isSelected()&!back_check_thermal_influx.isSelected())
            some_global.setBackAdiabaticMaterial(this.transfer_data_bank.getUISpecimen().materials.get(0));
        else
            some_global.setBackAdiabaticMaterial(back_adiobatic_material.getText().toString());

            some_global.setTopCoordinateX1(Double.valueOf(top_coordinate_x_1.getText()));
            some_global.setTopCoordinateX2(Double.valueOf(top_coordinate_x_2.getText()));
            some_global.setTopCoordinateY1(Double.valueOf(top_coordinate_y_1.getText()));
            some_global.setTopCoordinateY2(Double.valueOf(top_coordinate_y_2.getText()));
            some_global.setTopCoordinateZ1(Double.valueOf(top_coordinate_z_1.getText()));
            some_global.setTopCoordinateZ2(Double.valueOf(top_coordinate_z_2.getText()));

            some_global.setBottomCoordinateX1(Double.valueOf(bottom_coordinate_x_1.getText()));
            some_global.setBottomCoordinateX2(Double.valueOf(bottom_coordinate_x_2.getText()));
            some_global.setBottomCoordinateY1(Double.valueOf(bottom_coordinate_y_1.getText()));
            some_global.setBottomCoordinateY2(Double.valueOf(bottom_coordinate_y_2.getText()));
            some_global.setBottomCoordinateZ1(Double.valueOf(bottom_coordinate_z_1.getText()));
            some_global.setBottomCoordinateZ2(Double.valueOf(bottom_coordinate_z_2.getText()));

            some_global.setLeftCoordinateX1(Double.valueOf(left_coordinate_x_1.getText()));
            some_global.setLeftCoordinateX2(Double.valueOf(left_coordinate_x_2.getText()));
            some_global.setLeftCoordinateY1(Double.valueOf(left_coordinate_y_1.getText()));
            some_global.setLeftCoordinateY2(Double.valueOf(left_coordinate_y_2.getText()));
            some_global.setLeftCoordinateZ1(Double.valueOf(left_coordinate_z_1.getText()));
            some_global.setLeftCoordinateZ2(Double.valueOf(left_coordinate_z_2.getText()));

            some_global.setRightCoordinateX1(Double.valueOf(right_coordinate_x_1.getText()));
            some_global.setRightCoordinateX2(Double.valueOf(right_coordinate_x_2.getText()));
            some_global.setRightCoordinateY1(Double.valueOf(right_coordinate_y_1.getText()));
            some_global.setRightCoordinateY2(Double.valueOf(right_coordinate_y_2.getText()));
            some_global.setRightCoordinateZ1(Double.valueOf(right_coordinate_z_1.getText()));
            some_global.setRightCoordinateZ2(Double.valueOf(right_coordinate_z_2.getText()));

            some_global.setFrontCoordinateX1(Double.valueOf(front_coordinate_x_1.getText()));
            some_global.setFrontCoordinateX2(Double.valueOf(front_coordinate_x_2.getText()));
            some_global.setFrontCoordinateY1(Double.valueOf(front_coordinate_y_1.getText()));
            some_global.setFrontCoordinateY2(Double.valueOf(front_coordinate_y_2.getText()));
            some_global.setFrontCoordinateZ1(Double.valueOf(front_coordinate_z_1.getText()));
            some_global.setFrontCoordinateZ2(Double.valueOf(front_coordinate_z_2.getText()));

            some_global.setBackCoordinateX1(Double.valueOf(back_coordinate_x_1.getText()));
            some_global.setBackCoordinateX2(Double.valueOf(back_coordinate_x_2.getText()));
            some_global.setBackCoordinateY1(Double.valueOf(back_coordinate_y_1.getText()));
            some_global.setBackCoordinateY2(Double.valueOf(back_coordinate_y_2.getText()));
            some_global.setBackCoordinateZ1(Double.valueOf(back_coordinate_z_1.getText()));
            some_global.setBackCoordinateZ2(Double.valueOf(back_coordinate_z_2.getText()));
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
            
            // Checking of parameters of time functions for all tanks
            if(top_check_thermal_influx.isSelected() | top_check_mechanical_loading.isSelected())
            {
                Byte.parseByte(top_time_function_type.getText().toString());
                Double.parseDouble(top_loading_time_portion.getText().toString());
                Double.parseDouble(top_relaxation_time_portion.getText().toString());
            }
            
            if(bottom_check_thermal_influx.isSelected() | bottom_check_mechanical_loading.isSelected())
            {
                Byte.parseByte(bottom_time_function_type.getText().toString());
                Double.parseDouble(bottom_loading_time_portion.getText().toString());
                Double.parseDouble(bottom_relaxation_time_portion.getText().toString());
            }
            
            if(left_check_thermal_influx.isSelected() | left_check_mechanical_loading.isSelected())
            {
                Byte.parseByte(left_time_function_type.getText().toString());
                Double.parseDouble(left_loading_time_portion.getText().toString());
                Double.parseDouble(left_relaxation_time_portion.getText().toString());
            }            
            
            if(right_check_thermal_influx.isSelected() | right_check_mechanical_loading.isSelected())
            {
                Byte.parseByte(right_time_function_type.getText().toString());
                Double.parseDouble(right_loading_time_portion.getText().toString());
                Double.parseDouble(right_relaxation_time_portion.getText().toString());
            }
            
            if(front_check_thermal_influx.isSelected() | front_check_mechanical_loading.isSelected())
            {
                Byte.parseByte(front_time_function_type.getText().toString());
                Double.parseDouble(front_loading_time_portion.getText().toString());
                Double.parseDouble(front_relaxation_time_portion.getText().toString());
            }
            
            if(back_check_thermal_influx.isSelected() | back_check_mechanical_loading.isSelected())
            {
                Byte.parseByte(back_time_function_type.getText().toString());
                Double.parseDouble(back_loading_time_portion.getText().toString());
                Double.parseDouble(back_relaxation_time_portion.getText().toString());
            }
            
            // Checking of parameters of thermal influxes for all tanks            
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
            
            if(!parallelepiped_tank.isSelected() & !vertical_ellyptic_tank.isSelected()& !horizon_triangle_tank.isSelected() & !horizon_circle_tank.isSelected())
            {
                new Alert(AlertType.ERROR, "Please, set the geometrical type of tank!").show();
                return false;
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
        button_save.setOnAction(e ->{
            System.out.println("RecCATankParameters: method: actionPerformed: button 'save' pushed");
            if(isParametersInput()){
                 /*
                  * Remember inputed parameters in boundary condition data bank
                  */

                 inputParams();

                 /*
                  * Remember this boundary condition data bank
                  */

                 transfer_data_bank.setUITank(some_global);

                 /*
                  * Save data in choosed by user directory in dialog saving window
                  */

                 savingDialofOfParamsForBoundaryConditions();
                 }
        });
        
        top_adiobatic_material.setOnAction(e -> {
                System.out.println("RecCATankParameters: method: actionPerformed: top boundary not adiabatic");
                materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
                materials.ok.setOnAction(event -> {
                    String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                    System.out.println("RecCATankParameters: top_adiobatic_material selected material: " + selected_mat);
                    top_adiobatic_material.setText(selected_mat);
                    materials.close();
                });
                setCheckedPositions();
            });

            bottom_adiobatic_material.setOnAction(e -> {
                System.out.println("RecCATankParameters: method: actionPerformed: bottom boundary not adiabatic");
                materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);

                materials.ok.setOnAction(event -> {
                    String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                    System.out.println("RecCATankParameters: bottom_adiobatic_material selected material: " + selected_mat);
                    bottom_adiobatic_material.setText(selected_mat);
                    materials.close();
                });
                setCheckedPositions();
            });

            left_adiobatic_material.setOnAction(e -> {
                System.out.println("RecCATankParameters: method: actionPerformed: left boundary not adiabatic");
                materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);

                materials.ok.setOnAction(event -> {
                    String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                    System.out.println("RecCATankParameters: left adiobatic material selected material: " + selected_mat);
                    left_adiobatic_material.setText(selected_mat);
                    materials.close();
                });
                setCheckedPositions();
            });

            right_adiobatic_material.setOnAction(e -> {
                System.out.println("RecCATankParameters: method: actionPerformed: right boundary not adiabatic");
                materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);

                materials.ok.setOnAction(event -> {
                    String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                    System.out.println("RecCATankParameters: right adiobatic material selected material: " + selected_mat);
                    right_adiobatic_material.setText(selected_mat);
                    materials.close();
                });
                setCheckedPositions();
            });

            front_adiobatic_material.setOnAction(e -> {
                System.out.println("RecCATankParameters: method: actionPerformed: front boundary not adiabatic");
                materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);

                materials.ok.setOnAction(event -> {
                    String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                    System.out.println("RecCATankParameters: front adiobatic material selected material: " + selected_mat);
                    front_adiobatic_material.setText(selected_mat);
                    materials.close();
                });
                setCheckedPositions();
            });

            back_adiobatic_material.setOnAction(e -> {
                System.out.println("RecCATankParameters: method: actionPerformed: back boundary not adiabatic");
                materials = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);

                materials.ok.setOnAction(event -> {
                    String selected_mat = materials.list.getSelectionModel().getSelectedItem().toString();
                    System.out.println("RecCATankParameters: back adiobatic material selected material: " + selected_mat);
                    back_adiobatic_material.setText(selected_mat);
                    materials.close();
                });
                setCheckedPositions();
            });
        
    }
    
    /*
     * Controll inputed name for created file with boundary conditions
     * @param users_file_name - what name was inputed
     * @return - can be created file with inputed name or not
     */

    public boolean isNameCorrect(TextField users_file_name)
    {
        System.out.println("RecCATankConditionsParams: method: isNameCorrect: start");

        // Remember inputed file name

        String file_name = users_file_name.getText();

        // If file name empty

        if(file_name.isEmpty())
        {
            new Alert(Alert.AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }

        // If file name equals with name of task file

        if(file_name.equals(UICommon.TASK_PARAM_NAME))
        {
            new Alert(Alert.AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }

        // If file name ending on initial conditions file

        if(file_name.length()>4)
            if(file_name.substring(file_name.length()-4,file_name.length()).equals(UICommon.END_REC_NAME))
            {
                new Alert(Alert.AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

        // If file name ending on geometry file

        if(file_name.length()>5)
            if(file_name.substring(file_name.length()-5,file_name.length()).equals(UICommon.END_GEOM_NAME))
            {
                new Alert(Alert.AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

        // If file name ending on grains file

        if(file_name.length()>7)
            if(file_name.substring(file_name.length()-7,file_name.length()).equals(UICommon.END_GRAINS_NAME))
            {
                new Alert(Alert.AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
                return false;
            }

      return true;
    }    
}
