/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.File;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCA_SpecialSpecimenStructureParams extends Stage{
    
    StructureGeneratorAndRecrystallizator generate_grain_structure;
    
    TextField first_layer_thickness_text_field,
            second_layer_thickness_text_field,
            third_layer_thickness_text_field,

            first_layer_grain_size_text_field,        first_layer_grain_size_Y_text_field,        first_layer_grain_size_Z_text_field,
            upper_second_layer_grain_size_text_field, upper_second_layer_grain_size_Y_text_field, upper_second_layer_grain_size_Z_text_field,
            lower_second_layer_grain_size_text_field, lower_second_layer_grain_size_Y_text_field, lower_second_layer_grain_size_Z_text_field,
            third_layer_grain_size_text_field,        third_layer_grain_size_Y_text_field,        third_layer_grain_size_Z_text_field,
            substrate_grain_size_text_field,          substrate_grain_size_Y_text_field,          substrate_grain_size_Z_text_field,
            layer_elem_size_text_field;
    
    /** Text fields for values of thickness of grain boundary region (in cell diameters) 
     * for lower substrate, surface layer, 1st and 2nd intermediate layers 
     * and upper substrate respectively */
    TextField first_layer_grain_bound_thick_text_field,
               upper_second_layer_grain_bound_thick_text_field, 
               lower_second_layer_grain_bound_thick_text_field,
               third_layer_grain_bound_thick_text_field,
               substrate_grain_bound_thick_text_field;

    /** This field is intended for create helps for each input parameter field
     */
    Label  first_layer_thickness_help_label,
            second_layer_thickness_help_label,
            third_layer_thickness_help_label,

            first_layer_grain_size_help_label, 
            second_layer_grain_size_help_label,
            third_layer_grain_size_help_label,

            layer_type_help_label,
            layer_elem_size_help_label;
    
    Label X_No1, Y_No1, Z_No1,
          X_No2, Y_No2, Z_No2,
          X_No3, Y_No3, Z_No3,
          X_No4, Y_No4, Z_No4,
          X_No5, Y_No5, Z_No5;
    
    Label embryo_distib_No1,
          embryo_distib_No2,
          embryo_distib_No3,
          embryo_distib_No4,
          embryo_distib_No5;
    
    Label  surface_layer_grain_bound_thick_help_label, 
            upper_inter_layer_grain_bound_thick_help_label,
            lower_inter_layer_grain_bound_thick_help_label,
            upper_substrate_layer_grain_bound_thick_help_label,
            lower_substrate_layer_grain_bound_thick_help_label;

    /** This field is intended for choose layer type.
     */
    RadioButton continuous_layer_type_radio_button;
    RadioButton triangle_lattice_layer_type_radio_button;
    RadioButton cube_lattice_layer_type_radio_button;
    RadioButton triangle_plate_lattice_layer_type_radio_button;
    RadioButton cube_plate_lattice_layer_type_radio_button;    
    
    /** Buttons responsible for choosing of the direction of vector normal to interface of layers
     */
    RadioButton layers_perpendicular_to_axis_X;
    RadioButton layers_perpendicular_to_axis_Y;
    RadioButton layers_perpendicular_to_axis_Z;
    
    /** Buttons responsible for choosing of type of distribution of grain embryos in layers
     */
    RadioButton surface_layer_embryo_distr_regular_type, surface_layer_embryo_distr_mixed_type, surface_layer_embryo_distr_stochastic_type;
    RadioButton upper_inter_layer_embryo_distr_regular_type, upper_inter_layer_embryo_distr_mixed_type, upper_inter_layer_embryo_distr_stochastic_type;
    RadioButton lower_inter_layer_embryo_distr_regular_type, lower_inter_layer_embryo_distr_mixed_type, lower_inter_layer_embryo_distr_stochastic_type;
    RadioButton upper_substrate_embryo_distr_regular_type, upper_substrate_embryo_distr_mixed_type, upper_substrate_embryo_distr_stochastic_type;
    RadioButton lower_substrate_embryo_distr_regular_type, lower_substrate_embryo_distr_mixed_type, lower_substrate_embryo_distr_stochastic_type;

    /** This field is intended for controll all radio buttons of
     * layer type as one system
     */
    ToggleGroup layer_type_button_group;
    
    /** Creation of button group for set of radio buttons responsible for 
     * choice of the direction of vector normal to layer interfaces
     */
    ToggleGroup layer_direction_button_group;
    
    /** Groups  of buttons responsible for choosing of type of distribution of grain embryos in layers
     */
    ToggleGroup surface_layer_embryo_distr_type_button_group;
    ToggleGroup upper_inter_layer_embryo_distr_type_button_group;
    ToggleGroup lower_inter_layer_embryo_distr_type_button_group;
    ToggleGroup upper_substrate_embryo_distr_type_button_group;
    ToggleGroup lower_substrate_embryo_distr_type_button_group;

    /** This field is intended for create buttons in this frame
     */
    Button ok_button, cancel_button;

    /** This field is intended for create data bank
     */
    UI_SpecialSpecimen ui_special_specimen;
    
    /** This field is intended for work with transfer data bank
     */
    TransferredDataBank transfer_data_bank;
    
    VBox main_layout;
    
    public RecCA_SpecialSpecimenStructureParams(TransferredDataBank receive_transfer_data_bank){
        // Create system comment
        System.out.println("RecCA_SpecialSpecimenStructureParams: constructor creation start");
        // Set transfer data bank
        transfer_data_bank = receive_transfer_data_bank;

        // Remember choosed specimen name
        String specimen_name = transfer_data_bank.getUIInterface().getSpecimenPath();
        
        // Check - exist or not special specimen data bank
        File file = new File(Common.SPEC_PATH+"/"+specimen_name+Common.INIT_COND_NAME+"/"+specimen_name+".lrs");
        if(file.exists())
            // Special specimen data bank exists - load it
            ui_special_specimen = new UI_SpecialSpecimen(Common.SPEC_PATH+"/"+specimen_name+Common.INIT_COND_NAME+"/"+specimen_name);
        else
            // Special specimen data bank not exists - create
            // default special specimen data bank
            ui_special_specimen = new UI_SpecialSpecimen();
        
        // Create buttons
        createButtons();

        // Create helps
        createLayerHelps();

        // Create types of layer
        createLayerTypeRadioButtons();

        // Create fields for input parameters
        createLayerParameterTextFields();
        
        main_layout = new VBox();
        
        addAllElements();        
        handleEvents();        
        this.setScene(new Scene(main_layout));
        this.setTitle("Special Specimen Parameters");
        this.show();
    }
    
    public void createLayerTypeRadioButtons()
    {
        System.out.println("RecCA_SpecialSpecimenStructureParams: method: createLayerTypeRadioButtons: start");

        // Button responsible for layer interfaces perpendicular to axis X
        layers_perpendicular_to_axis_X = new RadioButton("Layers are perpendicular to axis X");
        
        // Button responsible for layer interfaces perpendicular to axis Y
        layers_perpendicular_to_axis_Y = new RadioButton("Layers are perpendicular to axis Y");
        
        // Button responsible for layer interfaces perpendicular to axis Z
        layers_perpendicular_to_axis_Z = new RadioButton("Layers are perpendicular to axis Z");
        
        // Buttons responsible for choosing of type of distribution of grain embryos in layers
        surface_layer_embryo_distr_stochastic_type = new RadioButton("stochastic");
        
        surface_layer_embryo_distr_regular_type = new RadioButton("regular");
        
        surface_layer_embryo_distr_mixed_type = new RadioButton("mixed");
        
        upper_inter_layer_embryo_distr_stochastic_type = new RadioButton("stochastic");        
        
        upper_inter_layer_embryo_distr_regular_type = new RadioButton("regular");        
        
        upper_inter_layer_embryo_distr_mixed_type = new RadioButton("mixed");        
        
        lower_inter_layer_embryo_distr_stochastic_type = new RadioButton("stochastic");
        
        lower_inter_layer_embryo_distr_regular_type = new RadioButton("regular");
        
        lower_inter_layer_embryo_distr_mixed_type = new RadioButton("mixed");
        
        upper_substrate_embryo_distr_stochastic_type = new RadioButton("stochastic");
        
        upper_substrate_embryo_distr_regular_type = new RadioButton("regular");        
        
        upper_substrate_embryo_distr_mixed_type = new RadioButton("mixed");        
        
        lower_substrate_embryo_distr_stochastic_type = new RadioButton("stochastic");        
        
        lower_substrate_embryo_distr_regular_type = new RadioButton("regular");        
        
        lower_substrate_embryo_distr_mixed_type = new RadioButton("mixed");        
        
        // Create radio button for continuous layer type
        continuous_layer_type_radio_button = new RadioButton("two parts of intermediate layer are divided by horizontal flat interface");

        // Create radio button for triangle plate lattice layer type
        triangle_plate_lattice_layer_type_radio_button = new RadioButton("horizontal triangle prisms in intermediate layer");

        // Create radio button for cube plate lattice layer type
        cube_plate_lattice_layer_type_radio_button = new RadioButton("horizontal right parallelepipeds in intermediate layer");

        // Create radio button for triangle lattice layer type
        triangle_lattice_layer_type_radio_button = new RadioButton("pyramides with square bases located in interface with surface layer");

        // Create radio button for cube lattice layer type
        cube_lattice_layer_type_radio_button = new RadioButton("right parallelepipeds with square bases located in interface with surface layer");

        // Creation of button group for set of radio buttons responsible for 
        // choice of the direction of vector normal to layer interfaces
        layer_direction_button_group = new ToggleGroup();
        layer_direction_button_group.getToggles().addAll(layers_perpendicular_to_axis_X,
                                                         layers_perpendicular_to_axis_Y,
                                                         layers_perpendicular_to_axis_Z);
        
        // Creation of groups  of buttons responsible for choosing of type of distribution of grain embryos in layers
        surface_layer_embryo_distr_type_button_group = new ToggleGroup();
        surface_layer_embryo_distr_type_button_group.getToggles().addAll(surface_layer_embryo_distr_regular_type,
                                                                         surface_layer_embryo_distr_mixed_type,
                                                                         surface_layer_embryo_distr_stochastic_type);
        
        upper_inter_layer_embryo_distr_type_button_group = new ToggleGroup();
        upper_inter_layer_embryo_distr_type_button_group.getToggles().addAll(upper_inter_layer_embryo_distr_regular_type,
                                                                             upper_inter_layer_embryo_distr_mixed_type,
                                                                             upper_inter_layer_embryo_distr_stochastic_type);
        
        lower_inter_layer_embryo_distr_type_button_group = new ToggleGroup();
        lower_inter_layer_embryo_distr_type_button_group.getToggles().addAll(lower_inter_layer_embryo_distr_regular_type,
                                                                             lower_inter_layer_embryo_distr_mixed_type,
                                                                             lower_inter_layer_embryo_distr_stochastic_type);
        
        upper_substrate_embryo_distr_type_button_group = new ToggleGroup();
        upper_substrate_embryo_distr_type_button_group.getToggles().addAll(upper_substrate_embryo_distr_regular_type,
                                                                           upper_substrate_embryo_distr_mixed_type,
                                                                           upper_substrate_embryo_distr_stochastic_type);
        
        lower_substrate_embryo_distr_type_button_group = new ToggleGroup();
        lower_substrate_embryo_distr_type_button_group.getToggles().addAll(lower_substrate_embryo_distr_regular_type,
                                                                           lower_substrate_embryo_distr_mixed_type,
                                                                           lower_substrate_embryo_distr_stochastic_type);
        
        // Create button group for this set of radio buttons
        // for controll them as one system
        layer_type_button_group = new ToggleGroup();
        layer_type_button_group.getToggles().addAll(continuous_layer_type_radio_button,
                                                    triangle_plate_lattice_layer_type_radio_button,
                                                    cube_plate_lattice_layer_type_radio_button,
                                                    triangle_lattice_layer_type_radio_button,
                                                    cube_lattice_layer_type_radio_button);
        

        // Getting of the value responsible for the type of intermediate layer
        int layer_type = ui_special_specimen.getLayerType();
        
        if(layer_type==0) continuous_layer_type_radio_button.setSelected(true);
        if(layer_type==1) triangle_plate_lattice_layer_type_radio_button.setSelected(true);
        if(layer_type==2) cube_plate_lattice_layer_type_radio_button.setSelected(true);
        if(layer_type==3) triangle_lattice_layer_type_radio_button.setSelected(true);
        if(layer_type==4) cube_lattice_layer_type_radio_button.setSelected(true);
        
        // Getting of the value responsible for choice of the direction of vector normal to layer interfaces
        int layer_direction = ui_special_specimen.getLayerDirection();
        
        if(layer_direction==0) layers_perpendicular_to_axis_X.setSelected(true);
        if(layer_direction==1) layers_perpendicular_to_axis_Y.setSelected(true);
        if(layer_direction==2) layers_perpendicular_to_axis_Z.setSelected(true);
        
        
        // Getting of the value responsible for choice of type of grain embryo distribution in layers
        int surface_layer_embryo_distr_type = ui_special_specimen.getSurfaceLayerEmbryoDistrType();
        int upper_inter_layer_embryo_distr_type = ui_special_specimen.getUpperInterLayerEmbryoDistrType();
        int lower_inter_layer_embryo_distr_type = ui_special_specimen.getLowerInterLayerEmbryoDistrType();
        int upper_substrate_embryo_distr_type = ui_special_specimen.getUpperSubstrateEmbryoDistrType();
        int lower_substrate_embryo_distr_type = ui_special_specimen.getLowerSubstrateEmbryoDistrType();
        
        if(surface_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE) 
            surface_layer_embryo_distr_regular_type.setSelected(true);
        else                                   
            surface_layer_embryo_distr_regular_type.setSelected(false);
        
        if(surface_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE) 
            surface_layer_embryo_distr_mixed_type.setSelected(true);
        else                                   
            surface_layer_embryo_distr_mixed_type.setSelected(false);
        
        if(surface_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE)
            surface_layer_embryo_distr_stochastic_type.setSelected(true);
        else                                   
            surface_layer_embryo_distr_stochastic_type.setSelected(false);
        
        
        if(upper_inter_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE) 
            upper_inter_layer_embryo_distr_regular_type.setSelected(true);
        else                                   
            upper_inter_layer_embryo_distr_regular_type.setSelected(false);
        
        if(upper_inter_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE) 
            upper_inter_layer_embryo_distr_mixed_type.setSelected(true);
        else                                   
            upper_inter_layer_embryo_distr_mixed_type.setSelected(false);
        
        if(upper_inter_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE)
            upper_inter_layer_embryo_distr_stochastic_type.setSelected(true);
        else
            upper_inter_layer_embryo_distr_stochastic_type.setSelected(false);
    
        
        if(lower_inter_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE) 
            lower_inter_layer_embryo_distr_regular_type.setSelected(true);
        else                                   
            lower_inter_layer_embryo_distr_regular_type.setSelected(false);
        
        if(lower_inter_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE) 
            lower_inter_layer_embryo_distr_mixed_type.setSelected(true);
        else                                   
            lower_inter_layer_embryo_distr_mixed_type.setSelected(false);
        
        if(lower_inter_layer_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE)
            lower_inter_layer_embryo_distr_stochastic_type.setSelected(true);
        else                                   
            lower_inter_layer_embryo_distr_stochastic_type.setSelected(false);
        
        
        if(upper_substrate_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE) 
            upper_substrate_embryo_distr_regular_type.setSelected(true);
        else                                   
            upper_inter_layer_embryo_distr_regular_type.setSelected(false);
        
        if(upper_substrate_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE) 
            upper_substrate_embryo_distr_mixed_type.setSelected(true);
        else                                   
            upper_substrate_embryo_distr_mixed_type.setSelected(false);
        
        if(upper_substrate_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE)
            upper_substrate_embryo_distr_stochastic_type.setSelected(true);
        else
            upper_substrate_embryo_distr_stochastic_type.setSelected(false);
        
        
        if(lower_substrate_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE) 
            lower_substrate_embryo_distr_regular_type.setSelected(true);
        else                                   
            lower_inter_layer_embryo_distr_regular_type.setSelected(false);
        
        if(lower_substrate_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE) 
            lower_substrate_embryo_distr_mixed_type.setSelected(true);
        else                                   
            lower_substrate_embryo_distr_mixed_type.setSelected(false);
        
        if(lower_substrate_embryo_distr_type==UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE)
            lower_substrate_embryo_distr_stochastic_type.setSelected(true);
        else
            lower_substrate_embryo_distr_stochastic_type.setSelected(false);
    }
    
    public void createLayerHelps()
    {
        System.out.println("RecCA_SpecialSpecimenStructureParams: method: createLayerHelps: start");
        // Create help (label) for first layer thickness parameter field
        first_layer_thickness_help_label = new Label("Surface layer thickness (m)");

        // Create help (label) for second layer thickness parameter field
        second_layer_thickness_help_label = new Label("Intermediate layer thickness (m)");

        // Create help (label) for third layer thickness parameter field
        third_layer_thickness_help_label = new Label("Upper substrate layer thickness (m)");

        // Create help (label) for surface grain size parameter field
        first_layer_grain_size_help_label = new Label("Surface layer grain size (m)");

        // Create help (label) for upper intermediate layer grain size parameter field
        second_layer_grain_size_help_label = new Label("Upper intermediate layer grain size (m)");

        // Create help (label) for lower intermediate layer grain size parameter field
        second_layer_grain_size_help_label = new Label("Lower intermediate layer grain size (m)");

        // Create help (label) for upper substrate layer grain size parameter field
        third_layer_grain_size_help_label = new Label("Upper substrate layer grain size (m)");

        // Create help (label) for lower substrate layer grain size parameter field
        third_layer_grain_size_help_label = new Label("Lower substrate layer grain size (m)");

        // Create help (label) for layer type parameter field
        layer_type_help_label = new Label("Layer type");

        // Create help (label) for layer element size parameter field
        layer_elem_size_help_label = new Label("Layer element size (m)");
        
        // Create help (label) for surface grain boundary_thickness
        surface_layer_grain_bound_thick_help_label = new Label("Surface layer grain boundary thickness (m)");

        // Create help (label) for upper intermediate layer grain boundary_thickness
        upper_inter_layer_grain_bound_thick_help_label = new Label("Upper inter.layer grain boundary thickness (m)");

        // Create help (label) for lower intermediate layer grain boundary_thickness
        lower_inter_layer_grain_bound_thick_help_label = new Label("Lower inter.layer grain boundary thickness (m)");

        // Create help (label) for upper substrate layer grain size boundary_thickness
        upper_substrate_layer_grain_bound_thick_help_label = new Label("Upper substrate layer grain boundary thickness (m)");

        // Create help (label) for lower substrate layer grain size boundary_thickness
        lower_substrate_layer_grain_bound_thick_help_label = new Label("Lower substrate layer grain boundary thickness (m)");
        
        X_No1 = new Label("X:");
        X_No2 = new Label("X:");
        X_No3 = new Label("X:");
        X_No4 = new Label("X:");
        X_No5 = new Label("X:");
        
        Y_No1 = new Label("Y:");
        Y_No2 = new Label("Y:");
        Y_No3 = new Label("Y:");
        Y_No4 = new Label("Y:");
        Y_No5 = new Label("Y:");
        
        Z_No1 = new Label("Z:");
        Z_No2 = new Label("Z:");
        Z_No3 = new Label("Z:");
        Z_No4 = new Label("Z:");
        Z_No5 = new Label("Z:");
        
        embryo_distib_No1 = new Label("Embryo Distribution:");
        embryo_distib_No2 = new Label("Embryo Distribution:");
        embryo_distib_No3 = new Label("Embryo Distribution:");
        embryo_distib_No4 = new Label("Embryo Distribution:");
        embryo_distib_No5 = new Label("Embryo Distribution:");
    }
    
    /*
     * Create text field for each input special specimen parameter
     */

    public void createLayerParameterTextFields()
    {
        System.out.println("RecCA_SpecialSpecimenStructureParams: method: createLayerParameterTextFields: start");
        
        // Diameter of cell expressed in meters
        double cell_size = transfer_data_bank.getUISpecimen().getCellSize();
        
        //TEST
        System.out.println("cell_size = "+cell_size);
        System.out.println();
        
        // Power of cell size value
        int cell_size_power = (int)Math.floor(Math.log10(cell_size));
        
        // Number for rounding of values
        int round_number = (int)Math.round(Math.pow(10, -cell_size_power+3));
        
        // Values of thickness for each layer and substrate expressed in meters
        double layer1_thick              = (int)Math.round(round_number*cell_size*ui_special_specimen.getFirstLayerThickness()/2)*1.0/round_number;
        double layer2_thick              = (int)Math.round(round_number*cell_size*ui_special_specimen.getSecondLayerThickness()/2)*1.0/round_number;
        double layer3_thick              = (int)Math.round(round_number*cell_size*ui_special_specimen.getThirdLayerThickness()/2)*1.0/round_number;
        
        // Values of grain size for each layer and substrate expressed in meters
        double layer1_grain_size         = (int)Math.round(round_number*cell_size*ui_special_specimen.getFirstLayerGrainSize())*1.0/round_number;
        double layer1_grain_size_Y       = (int)Math.round(round_number*cell_size*ui_special_specimen.getFirstLayerGrainSizeY())*1.0/round_number;
        double layer1_grain_size_Z       = (int)Math.round(round_number*cell_size*ui_special_specimen.getFirstLayerGrainSizeZ())*1.0/round_number;
        
        double upper_layer2_grain_size   = (int)Math.round(round_number*cell_size*ui_special_specimen.getUpperSecondLayerGrainSize())*1.0/round_number;
        double upper_layer2_grain_size_Y = (int)Math.round(round_number*cell_size*ui_special_specimen.getUpperSecondLayerGrainSizeY())*1.0/round_number;
        double upper_layer2_grain_size_Z = (int)Math.round(round_number*cell_size*ui_special_specimen.getUpperSecondLayerGrainSizeZ())*1.0/round_number;
        
        double lower_layer2_grain_size   = (int)Math.round(round_number*cell_size*ui_special_specimen.getLowerSecondLayerGrainSize())*1.0/round_number;
        double lower_layer2_grain_size_Y = (int)Math.round(round_number*cell_size*ui_special_specimen.getLowerSecondLayerGrainSizeY())*1.0/round_number;
        double lower_layer2_grain_size_Z = (int)Math.round(round_number*cell_size*ui_special_specimen.getLowerSecondLayerGrainSizeZ())*1.0/round_number;
        
        double layer3_grain_size         = (int)Math.round(round_number*cell_size*ui_special_specimen.getThirdLayerGrainSize())*1.0/round_number;
        double layer3_grain_size_Y       = (int)Math.round(round_number*cell_size*ui_special_specimen.getThirdLayerGrainSizeY())*1.0/round_number;
        double layer3_grain_size_Z       = (int)Math.round(round_number*cell_size*ui_special_specimen.getThirdLayerGrainSizeZ())*1.0/round_number;
        
        double substrate_grain_size      = (int)Math.round(round_number*cell_size*ui_special_specimen.getSubstrateGrainSize())*1.0/round_number;
        double substrate_grain_size_Y    = (int)Math.round(round_number*cell_size*ui_special_specimen.getSubstrateGrainSizeY())*1.0/round_number;
        double substrate_grain_size_Z    = (int)Math.round(round_number*cell_size*ui_special_specimen.getSubstrateGrainSizeZ())*1.0/round_number;
        
        // Value of structure element size for 2nd layer expressed in meters
        double layer2_elem_size        = (int)Math.round(round_number*cell_size*ui_special_specimen.getLayerElemSize()/2)*1.0/round_number;
        
        /** Values of thickness of grain boundary region (in cell diameters) 
         * for lower substrate, surface layer, 1st and 2nd intermediate layers 
         * and upper substrate respectively */
        double grain_bound_region_thickness_2 = (int)Math.round(round_number*cell_size*ui_special_specimen.getFirstLayerGrainBoundThickness())*1.0/round_number;
        double grain_bound_region_thickness_3 = (int)Math.round(round_number*cell_size*ui_special_specimen.getUpperSecondLayerGrainBoundThickness())*1.0/round_number;
        double grain_bound_region_thickness_4 = (int)Math.round(round_number*cell_size*ui_special_specimen.getLowerSecondLayerGrainBoundThickness())*1.0/round_number;
        double grain_bound_region_thickness_5 = (int)Math.round(round_number*cell_size*ui_special_specimen.getThirdLayerGrainBoundThickness())*1.0/round_number;
        double grain_bound_region_thickness   = (int)Math.round(round_number*cell_size*ui_special_specimen.getSubstrateGrainBoundThickness())*1.0/round_number;
        
        // Create text field for input first layer thickness parameter
        first_layer_thickness_text_field = new TextField();
        first_layer_thickness_text_field.setText(layer1_thick+"");

        // Create text field for input second layer thickness parameter
        second_layer_thickness_text_field = new TextField();
        second_layer_thickness_text_field.setText(layer2_thick+"");

        // Create text field for input third layer thickness parameter
        third_layer_thickness_text_field = new TextField();
        third_layer_thickness_text_field.setText(layer3_thick+"");

        // Create text field for input first layer grain size parameter
        first_layer_grain_size_text_field = new TextField();
        first_layer_grain_size_text_field.setText(layer1_grain_size+"");
        
        first_layer_grain_size_Y_text_field = new TextField();
        first_layer_grain_size_Y_text_field.setText(layer1_grain_size_Y+"");
        
        first_layer_grain_size_Z_text_field = new TextField();
        first_layer_grain_size_Z_text_field.setText(layer1_grain_size_Z+"");

        // Create text field for input second layer grain size parameter
        upper_second_layer_grain_size_text_field = new TextField();
        upper_second_layer_grain_size_text_field.setText(upper_layer2_grain_size+"");
        
        upper_second_layer_grain_size_Y_text_field = new TextField();
        upper_second_layer_grain_size_Y_text_field.setText(upper_layer2_grain_size_Y+"");
        
        upper_second_layer_grain_size_Z_text_field = new TextField();
        upper_second_layer_grain_size_Z_text_field.setText(upper_layer2_grain_size_Z+"");

        // Create text field for input second layer grain size parameter
        lower_second_layer_grain_size_text_field = new TextField();
        lower_second_layer_grain_size_text_field.setText(lower_layer2_grain_size+"");
        
        lower_second_layer_grain_size_Y_text_field = new TextField();
        lower_second_layer_grain_size_Y_text_field.setText(lower_layer2_grain_size_Y+"");
        
        lower_second_layer_grain_size_Z_text_field = new TextField();
        lower_second_layer_grain_size_Z_text_field.setText(lower_layer2_grain_size_Z+"");

        // Create text field for input third layer grain size parameter
        third_layer_grain_size_text_field = new TextField();
        third_layer_grain_size_text_field.setText(layer3_grain_size+"");
        
        third_layer_grain_size_Y_text_field = new TextField();
        third_layer_grain_size_Y_text_field.setText(layer3_grain_size_Y+"");
        
        third_layer_grain_size_Z_text_field = new TextField();
        third_layer_grain_size_Z_text_field.setText(layer3_grain_size_Z+"");

        // Create text field for input substrate grain size parameter
        substrate_grain_size_text_field = new TextField();
        substrate_grain_size_text_field.setText(substrate_grain_size+"");
        
        substrate_grain_size_Y_text_field = new TextField();
        substrate_grain_size_Y_text_field.setText(substrate_grain_size_Y+"");
        
        substrate_grain_size_Z_text_field = new TextField();
        substrate_grain_size_Z_text_field.setText(substrate_grain_size_Z+"");
                
        /** Text fields for values of thickness of grain boundary region (in cell diameters) 
         * for lower substrate, surface layer, 1st and 2nd intermediate layers 
         * and upper substrate respectively */
        first_layer_grain_bound_thick_text_field = new TextField();
        first_layer_grain_bound_thick_text_field.setText(grain_bound_region_thickness_2+"");

        upper_second_layer_grain_bound_thick_text_field = new TextField();
        upper_second_layer_grain_bound_thick_text_field.setText(grain_bound_region_thickness_3+"");
        
        lower_second_layer_grain_bound_thick_text_field = new TextField();
        lower_second_layer_grain_bound_thick_text_field.setText(grain_bound_region_thickness_4+"");

        third_layer_grain_bound_thick_text_field = new TextField();
        third_layer_grain_bound_thick_text_field.setText(grain_bound_region_thickness_5+"");
        
        substrate_grain_bound_thick_text_field = new TextField();
        substrate_grain_bound_thick_text_field.setText(grain_bound_region_thickness+"");
        
        
        // Create text field for input layer element size parameter
        layer_elem_size_text_field = new TextField();
        layer_elem_size_text_field.setText(layer2_elem_size+"");
    }
    
    public void createButtons()
    {
        System.out.println("RecCA_SpecialSpecimenStructureParams: method: createButtons: start");
        // Create ok button
        ok_button = new Button(UICommon.BUTTON_OK_NAME);
        // Create cancel button
        cancel_button = new Button(UICommon.BUTTON_CANCEL_NAME);        
    }
    
    /*
     * Controll - write or wrong type have inputed special
     * specimen parameter values
     */

    public boolean isParametersCorrect()
    {
        System.out.println("RecCA_SpecialSpecimenStructureParams: method: isParametersCorrect: start");
        // Check - write or false type have inputed parameter values
        try
        {
            Double.parseDouble(first_layer_thickness_text_field.getText().toString());
            Double.parseDouble(second_layer_thickness_text_field.getText().toString());
            Double.parseDouble(third_layer_thickness_text_field.getText().toString());

            Double.parseDouble(first_layer_grain_size_text_field.getText().toString());
            Double.parseDouble(first_layer_grain_size_Y_text_field.getText().toString());
            Double.parseDouble(first_layer_grain_size_Z_text_field.getText().toString());
            
            Double.parseDouble(upper_second_layer_grain_size_text_field.getText());
            Double.parseDouble(upper_second_layer_grain_size_Y_text_field.getText());
            Double.parseDouble(upper_second_layer_grain_size_Z_text_field.getText());
            
            Double.parseDouble(lower_second_layer_grain_size_text_field.getText());
            Double.parseDouble(lower_second_layer_grain_size_Y_text_field.getText());
            Double.parseDouble(lower_second_layer_grain_size_Z_text_field.getText());
            
            Double.parseDouble(third_layer_grain_size_text_field.getText());
            Double.parseDouble(third_layer_grain_size_Y_text_field.getText());
            Double.parseDouble(third_layer_grain_size_Z_text_field.getText());
            
            Double.parseDouble(substrate_grain_size_text_field.getText());
            Double.parseDouble(substrate_grain_size_Y_text_field.getText());
            Double.parseDouble(substrate_grain_size_Z_text_field.getText());
            
            Double.parseDouble(layer_elem_size_text_field.getText());
            
            Double.parseDouble(first_layer_grain_bound_thick_text_field.getText());
            Double.parseDouble(upper_second_layer_grain_bound_thick_text_field.getText().toString());
            Double.parseDouble(lower_second_layer_grain_bound_thick_text_field.getText().toString());
            Double.parseDouble(third_layer_grain_bound_thick_text_field.getText().toString());
            Double.parseDouble(substrate_grain_bound_thick_text_field.getText().toString());

            // If layer type is not chosen then inform about it
            if(!continuous_layer_type_radio_button.isSelected()&
               !triangle_plate_lattice_layer_type_radio_button.isSelected()&
               !cube_plate_lattice_layer_type_radio_button.isSelected()&
               !triangle_lattice_layer_type_radio_button.isSelected()&
               !cube_lattice_layer_type_radio_button.isSelected())
            {   
                new Alert(AlertType.ERROR, UICommon.LAYER_TYPE_NO_CHOOSED).show();
                return false;
            }
            
            // If layer type is not chosen then inform about it
            if(!layers_perpendicular_to_axis_X.isSelected()&
               !layers_perpendicular_to_axis_Y.isSelected()&
               !layers_perpendicular_to_axis_Z.isSelected())
            {
                new Alert(AlertType.ERROR, UICommon.LAYER_TYPE_NO_CHOOSED).show();
                return false;
            }
            
            // If type of distribution of grain embryos in layers is not chosen then inform about it
            if(!surface_layer_embryo_distr_regular_type.isSelected()&
               !surface_layer_embryo_distr_mixed_type.isSelected()&
               !surface_layer_embryo_distr_stochastic_type.isSelected())
            {
                new Alert(AlertType.ERROR, UICommon.EMBRYO_DISTR_TYPE_IS_NOT_CHOSEN).show();
                return false;
            }
            
            if(!upper_inter_layer_embryo_distr_regular_type.isSelected()&
               !upper_inter_layer_embryo_distr_mixed_type.isSelected()&
               !upper_inter_layer_embryo_distr_stochastic_type.isSelected())
            {
                new Alert(AlertType.ERROR, UICommon.EMBRYO_DISTR_TYPE_IS_NOT_CHOSEN).show();
                return false;
            }
            
            if(!lower_inter_layer_embryo_distr_regular_type.isSelected()&
               !lower_inter_layer_embryo_distr_mixed_type.isSelected()&
               !lower_inter_layer_embryo_distr_stochastic_type.isSelected())
            {
                new Alert(AlertType.ERROR, UICommon.EMBRYO_DISTR_TYPE_IS_NOT_CHOSEN).show();
                return false;
            }
        
            if(!upper_substrate_embryo_distr_regular_type.isSelected()&
               !upper_substrate_embryo_distr_mixed_type.isSelected()&
               !upper_substrate_embryo_distr_stochastic_type.isSelected())
            {
                new Alert(AlertType.ERROR, UICommon.EMBRYO_DISTR_TYPE_IS_NOT_CHOSEN).show();
                return false;
            }
        
            
            if(!lower_substrate_embryo_distr_regular_type.isSelected()&
               !lower_substrate_embryo_distr_mixed_type.isSelected()&
               !lower_substrate_embryo_distr_stochastic_type.isSelected())
            {
                new Alert(AlertType.ERROR, UICommon.EMBRYO_DISTR_TYPE_IS_NOT_CHOSEN).show();
                return false;
            }            
            // If all is correct then all fine
            return true;
        }
        catch (NumberFormatException e)
        {
            new Alert(AlertType.ERROR, "Incorrect Data Input").showAndWait();
        }

        // Somthing false
        return false;
    }
    
    /** Remember inputed parameter values in data bank 
     */
    public void inputParams()
    {
        System.out.println("RecCA_SpecialSpecimenStructureParams: method: inputParams: start");

        // Remember inputed special specimen parameter values
        ui_special_specimen.setFirstLayerThickness(Double.valueOf(first_layer_thickness_text_field.getText()));
        ui_special_specimen.setSecondLayerThickness(Double.valueOf(second_layer_thickness_text_field.getText()));
        ui_special_specimen.setThirdLayerThickness(Double.valueOf(third_layer_thickness_text_field.getText()));

        ui_special_specimen.setFirstLayerGrainSize(Double.valueOf(first_layer_grain_size_text_field.getText()));
        ui_special_specimen.setFirstLayerGrainSizeY(Double.valueOf(first_layer_grain_size_Y_text_field.getText()));
        ui_special_specimen.setFirstLayerGrainSizeZ(Double.valueOf(first_layer_grain_size_Z_text_field.getText()));
        
        ui_special_specimen.setUpperSecondLayerGrainSize(Double.valueOf(upper_second_layer_grain_size_text_field.getText()));
        ui_special_specimen.setUpperSecondLayerGrainSizeY(Double.valueOf(upper_second_layer_grain_size_Y_text_field.getText()));
        ui_special_specimen.setUpperSecondLayerGrainSizeZ(Double.valueOf(upper_second_layer_grain_size_Z_text_field.getText()));
        
        ui_special_specimen.setLowerSecondLayerGrainSize(Double.valueOf(lower_second_layer_grain_size_text_field.getText()));
        ui_special_specimen.setLowerSecondLayerGrainSizeY(Double.valueOf(lower_second_layer_grain_size_Y_text_field.getText()));
        ui_special_specimen.setLowerSecondLayerGrainSizeZ(Double.valueOf(lower_second_layer_grain_size_Z_text_field.getText()));
        
        ui_special_specimen.setThirdLayerGrainSize(Double.valueOf(third_layer_grain_size_text_field.getText()));
        ui_special_specimen.setThirdLayerGrainSizeY(Double.valueOf(third_layer_grain_size_Y_text_field.getText()));
        ui_special_specimen.setThirdLayerGrainSizeZ(Double.valueOf(third_layer_grain_size_Z_text_field.getText()));
        
        ui_special_specimen.setSubstrateGrainSize(Double.valueOf(substrate_grain_size_text_field.getText()));
        ui_special_specimen.setSubstrateGrainSizeY(Double.valueOf(substrate_grain_size_Y_text_field.getText()));
        ui_special_specimen.setSubstrateGrainSizeZ(Double.valueOf(substrate_grain_size_Z_text_field.getText()));

        ui_special_specimen.setLayerElemSize(Double.valueOf(layer_elem_size_text_field.getText()));
        
        ui_special_specimen.setFirstLayerGrainBoundThickness(Double.valueOf(first_layer_grain_bound_thick_text_field.getText()));
        ui_special_specimen.setUpperSecondLayerGrainBoundThickness(Double.valueOf(upper_second_layer_grain_bound_thick_text_field.getText()));
        ui_special_specimen.setLowerSecondLayerGrainBoundThickness(Double.valueOf(lower_second_layer_grain_bound_thick_text_field.getText()));
        ui_special_specimen.setThirdLayerGrainBoundThickness(Double.valueOf(third_layer_grain_bound_thick_text_field.getText()));
        ui_special_specimen.setSubstrateGrainBoundThickness(Double.valueOf(substrate_grain_bound_thick_text_field.getText()));
         
        // Chosen layer type is remembered.
        if(continuous_layer_type_radio_button.isSelected())
            ui_special_specimen.setLayerType(UICommon.CONTINUOUS_LAYER);
        
        if(triangle_plate_lattice_layer_type_radio_button.isSelected())            
            ui_special_specimen.setLayerType(UICommon.HORIZONTAL_TRIANGLE_PRISMS);
        
        if(cube_plate_lattice_layer_type_radio_button.isSelected())
            ui_special_specimen.setLayerType(UICommon.HORIZONTAL_RIGHT_PARALLELEPIPEDS);
        
        if(triangle_lattice_layer_type_radio_button.isSelected())
            ui_special_specimen.setLayerType(UICommon.VERTICAL_SQUARE_PYRAMIDES);
        
        if(cube_lattice_layer_type_radio_button.isSelected())
            ui_special_specimen.setLayerType(UICommon.VERTICAL_RIGHT_PARALLELEPIPEDS);
        
        // Chosen type of layer direction is remembered.
        if(layers_perpendicular_to_axis_X.isSelected())
            ui_special_specimen.setLayerDirection(UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_X);
        
        if(layers_perpendicular_to_axis_Y.isSelected())
            ui_special_specimen.setLayerDirection(UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Y);
        
        if(layers_perpendicular_to_axis_Z.isSelected())
            ui_special_specimen.setLayerDirection(UICommon.LAYERS_ARE_PERPENDICULAR_TO_AXIS_Z);
        
        // Setting of type of embryo distribution in each layer
        // Surface layer
        if(surface_layer_embryo_distr_regular_type.isSelected())
            ui_special_specimen.setSurfaceLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE);
        
        if(surface_layer_embryo_distr_mixed_type.isSelected())
            ui_special_specimen.setSurfaceLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE);
        
        if(surface_layer_embryo_distr_stochastic_type.isSelected())
            ui_special_specimen.setSurfaceLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE);
        
        // Upper intermediate layer
        if(upper_inter_layer_embryo_distr_regular_type.isSelected())
            ui_special_specimen.setUpperInterLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE);
        
        if(upper_inter_layer_embryo_distr_mixed_type.isSelected())
            ui_special_specimen.setUpperInterLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE);
        
        if(upper_inter_layer_embryo_distr_stochastic_type.isSelected())
            ui_special_specimen.setUpperInterLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE);
        
        // Lower intermediate layer
        if(lower_inter_layer_embryo_distr_regular_type.isSelected())
            ui_special_specimen.setLowerInterLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE);
        
        if(lower_inter_layer_embryo_distr_mixed_type.isSelected())
            ui_special_specimen.setLowerInterLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE);
        
        if(lower_inter_layer_embryo_distr_stochastic_type.isSelected())
            ui_special_specimen.setLowerInterLayerEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE);
        
        // Upper substrate
        if(upper_substrate_embryo_distr_regular_type.isSelected())
            ui_special_specimen.setUpperSubstrateEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE);
        
        if(upper_substrate_embryo_distr_mixed_type.isSelected())
            ui_special_specimen.setUpperSubstrateEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE);
        
        if(upper_substrate_embryo_distr_stochastic_type.isSelected())
            ui_special_specimen.setUpperSubstrateEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE);
        
        // Lower substrate
        if(lower_substrate_embryo_distr_regular_type.isSelected())
            ui_special_specimen.setLowerSubstrateEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_REGULAR_TYPE);
        
        if(lower_substrate_embryo_distr_mixed_type.isSelected())
            ui_special_specimen.setLowerSubstrateEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_MIXED_TYPE);
        
        if(lower_substrate_embryo_distr_stochastic_type.isSelected())
            ui_special_specimen.setLowerSubstrateEmbryoDistrType(UICommon.EMBRYO_DISTRIBUTION_STOCHASTIC_TYPE);
    }
    
    
    public void addAllElements(){
        
        TitledPane tp1 = new TitledPane();
        tp1.setText("Layers thickness");
        TitledPane tp2 = new TitledPane();
        tp2.setText("Grain Sizes and Distribution Types");
        TitledPane tp3 = new TitledPane();
        tp3.setText("Grain Boundary Thickness");
        TitledPane tp4 = new TitledPane();
        tp4.setText("Layers Direction");
        TitledPane tp5 = new TitledPane();
        tp5.setText("Layer Type");

        GridPane tp1_content = new GridPane();
        tp1_content.setPadding(new Insets(10, 10, 10, 10));
        tp1_content.setAlignment(Pos.CENTER_LEFT);
        tp1_content.setHgap(10.0d);
        tp1_content.setVgap(10.0d);
        
        GridPane.setConstraints(first_layer_thickness_help_label, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(first_layer_thickness_text_field, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(second_layer_thickness_help_label, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(second_layer_thickness_text_field, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(third_layer_thickness_help_label, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(third_layer_thickness_text_field, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        
        tp1_content.getChildren().addAll(
                first_layer_thickness_help_label, first_layer_thickness_text_field,
                second_layer_thickness_help_label, second_layer_thickness_text_field,
                third_layer_thickness_help_label, third_layer_thickness_text_field
        );
        tp1.setContent(tp1_content);
        
        ScrollPane scroll_pane = new ScrollPane();
        
        GridPane tp2_content = new GridPane();
        tp2_content.setPadding(new Insets(10, 10, 10, 10));
        tp2_content.setAlignment(Pos.CENTER_LEFT);
        tp2_content.setHgap(7.0d);
        tp2_content.setVgap(7.0d);
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        Separator sep2 = new Separator(Orientation.VERTICAL);
        Separator sep3 = new Separator(Orientation.VERTICAL);
        Separator sep4 = new Separator(Orientation.VERTICAL);
        Separator sep5 = new Separator(Orientation.VERTICAL);
        
        Separator sep6 = new Separator(Orientation.HORIZONTAL);
        Separator sep7 = new Separator(Orientation.HORIZONTAL);
        Separator sep8 = new Separator(Orientation.HORIZONTAL);
        Separator sep9 = new Separator(Orientation.HORIZONTAL);
        Separator sep10 = new Separator(Orientation.HORIZONTAL);
        
        Label l1 = new Label("First Layer Grain Size: ");
        Label l2 = new Label("Upper Second Layer Grain Size:");
        Label l3 = new Label("Lower Second Layer Grain Size:");
        Label l4 = new Label("Third Layer Grain Size:");
        Label l5 = new Label("Substrate Layer Grains Size:");
        
        /**
         * first_layer_grain_size_text_field,        first_layer_grain_size_Y_text_field,        first_layer_grain_size_Z_text_field,
            upper_second_layer_grain_size_text_field, upper_second_layer_grain_size_Y_text_field, upper_second_layer_grain_size_Z_text_field,
            lower_second_layer_grain_size_text_field, lower_second_layer_grain_size_Y_text_field, lower_second_layer_grain_size_Z_text_field,
            third_layer_grain_size_text_field,        third_layer_grain_size_Y_text_field,        third_layer_grain_size_Z_text_field,
            substrate_grain_size_text_field,          substrate_grain_size_Y_text_field,          substrate_grain_size_Z_text_field,
         */
        
        GridPane.setConstraints(l1, 0, 0, 1, 3, HPos.RIGHT, VPos.CENTER);        
        
        GridPane.setConstraints(X_No1, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(first_layer_grain_size_text_field, 2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Y_No1, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(first_layer_grain_size_Y_text_field, 2, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Z_No1, 1, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(first_layer_grain_size_Z_text_field, 2, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 3, 0, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(embryo_distib_No1, 4, 0, 1, 3, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(surface_layer_embryo_distr_stochastic_type, 5, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(surface_layer_embryo_distr_regular_type, 5, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(surface_layer_embryo_distr_mixed_type, 5, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep6, 0, 3, 6, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l2, 0, 4, 1, 3, HPos.RIGHT, VPos.CENTER);        
        
        GridPane.setConstraints(X_No2, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(upper_second_layer_grain_size_text_field, 2, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Y_No2, 1, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(upper_second_layer_grain_size_Y_text_field, 2, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Z_No2, 1, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(upper_second_layer_grain_size_Z_text_field, 2, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 3, 4, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(embryo_distib_No2, 4, 4, 1, 3, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(upper_inter_layer_embryo_distr_stochastic_type, 5, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(upper_inter_layer_embryo_distr_regular_type, 5, 5, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(upper_inter_layer_embryo_distr_mixed_type, 5, 6, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep7, 0, 7, 6, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l3, 0, 8, 1, 3, HPos.RIGHT, VPos.CENTER);        
        
        GridPane.setConstraints(X_No3, 1, 8, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lower_second_layer_grain_size_text_field, 2, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Y_No3, 1, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lower_second_layer_grain_size_Y_text_field, 2, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Z_No3, 1, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lower_second_layer_grain_size_Z_text_field, 2, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 3, 8, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(embryo_distib_No3, 4, 8, 1, 3, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lower_inter_layer_embryo_distr_stochastic_type, 5, 8, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(lower_inter_layer_embryo_distr_regular_type, 5, 9, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(lower_inter_layer_embryo_distr_mixed_type, 5, 10, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep8, 0, 11, 6, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l4, 0, 12, 1, 3, HPos.RIGHT, VPos.CENTER);        
        
        GridPane.setConstraints(X_No4, 1, 12, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(third_layer_grain_size_text_field, 2, 12, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Y_No4, 1, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(third_layer_grain_size_Y_text_field, 2, 13, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Z_No4, 1, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(third_layer_grain_size_Z_text_field, 2, 14, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(embryo_distib_No4, 4, 12, 1, 3, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(upper_substrate_embryo_distr_stochastic_type, 5, 12, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(upper_substrate_embryo_distr_regular_type, 5, 13, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(upper_substrate_embryo_distr_mixed_type, 5, 14, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep9, 0, 15, 6, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l5, 0, 16, 1, 3, HPos.RIGHT, VPos.CENTER);        
        
        GridPane.setConstraints(X_No5, 1, 16, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(substrate_grain_size_text_field, 2, 16, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Y_No5, 1, 17, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(substrate_grain_size_Y_text_field, 2, 17, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(Z_No5, 1, 18, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(substrate_grain_size_Z_text_field, 2, 18, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(embryo_distib_No5, 4, 16, 1, 3, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(lower_substrate_embryo_distr_stochastic_type, 5, 16, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(lower_substrate_embryo_distr_regular_type, 5, 17, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(lower_substrate_embryo_distr_mixed_type, 5, 18, 1, 1, HPos.LEFT, VPos.CENTER);
        
        tp2_content.getChildren().addAll(
                l1, l2, l3, l4, l5,
                X_No1, Y_No1, Z_No1, X_No2, Y_No2, Z_No2, X_No3, Y_No3, Z_No3,
                X_No4, Y_No4, Z_No4, X_No5, Y_No5, Z_No5,
                first_layer_grain_size_text_field,
                first_layer_grain_size_Y_text_field,
                first_layer_grain_size_Z_text_field,
                upper_second_layer_grain_size_text_field,
                upper_second_layer_grain_size_Y_text_field,
                upper_second_layer_grain_size_Z_text_field,
                lower_second_layer_grain_size_text_field,
                lower_second_layer_grain_size_Y_text_field,
                lower_second_layer_grain_size_Z_text_field,
                third_layer_grain_size_text_field,
                third_layer_grain_size_Y_text_field,
                third_layer_grain_size_Z_text_field,
                substrate_grain_size_text_field,
                substrate_grain_size_Y_text_field,
                substrate_grain_size_Z_text_field,
                sep1, sep2, sep3, sep4, sep5, sep6, sep7, sep8, sep9,
                embryo_distib_No1, embryo_distib_No2, embryo_distib_No3,embryo_distib_No4,embryo_distib_No5,
                surface_layer_embryo_distr_stochastic_type,
                surface_layer_embryo_distr_regular_type,
                surface_layer_embryo_distr_mixed_type,
                upper_inter_layer_embryo_distr_stochastic_type,
                upper_inter_layer_embryo_distr_regular_type,
                upper_inter_layer_embryo_distr_mixed_type,
                lower_inter_layer_embryo_distr_stochastic_type,
                lower_inter_layer_embryo_distr_regular_type,
                lower_inter_layer_embryo_distr_mixed_type,
                upper_substrate_embryo_distr_stochastic_type,
                upper_substrate_embryo_distr_regular_type,
                upper_substrate_embryo_distr_mixed_type,
                lower_substrate_embryo_distr_stochastic_type,
                lower_substrate_embryo_distr_regular_type,
                lower_substrate_embryo_distr_mixed_type
        );
        scroll_pane.setContent(tp2_content);
        tp2.setContent(scroll_pane);
        
        GridPane tp3_content = new GridPane();
        tp3_content.setPadding(new Insets(10, 10, 10, 10));
        tp3_content.setAlignment(Pos.CENTER_LEFT);
        tp3_content.setHgap(7.0d);
        tp3_content.setVgap(7.0d);
        tp3_content.add(layers_perpendicular_to_axis_X, 0, 1);
        tp3_content.add(layers_perpendicular_to_axis_Y, 0, 2);
        tp3_content.add(layers_perpendicular_to_axis_Z, 0, 3);
        tp3.setContent(tp3_content);
        
        GridPane tp4_content = new GridPane();
        tp4_content.setPadding(new Insets(10, 10, 10, 10));
        tp4_content.setAlignment(Pos.CENTER_LEFT);
        tp4_content.setHgap(7.0d);
        tp4_content.setVgap(7.0d);
        tp4_content.add(continuous_layer_type_radio_button, 0, 0);
        tp4_content.add(triangle_lattice_layer_type_radio_button, 0, 1);
        tp4_content.add(cube_lattice_layer_type_radio_button, 0, 2);
        tp4_content.add(triangle_plate_lattice_layer_type_radio_button, 0, 3);
        tp4_content.add(cube_plate_lattice_layer_type_radio_button, 0, 4);
        tp4.setContent(tp4_content);
        
        ok_button.setPadding(new Insets(5, 30, 5, 30));
        cancel_button.setPadding(new Insets(5, 30, 5, 30));
        
        HBox bottom_layout = new HBox(ok_button, sep3, cancel_button);
        bottom_layout.setAlignment(Pos.CENTER);
        
        main_layout.setAlignment(Pos.TOP_CENTER);
        main_layout.getChildren().addAll(
                tp1, tp2,
                sep1,
                tp3,
                sep2,
                tp4,
                sep4,
                bottom_layout
        );
    }
    
    public void handleEvents(){
        ok_button.setOnAction(e -> {
            System.out.println("RecCA_SpecialSpecimenStructureParams: method: actionPerformed: button 'OK' is pushed");
            if(isParametersCorrect())
            {
                // Remember inputed params in data bank
                inputParams();

                // Remember in UI interface that we want to
                // calculate special specimen structure
                transfer_data_bank.getUIInterface().setSpecialSpecimenPath(transfer_data_bank.getUIInterface().getSpecimenPath());
                System.out.println("////Special specimen Params spec rows num = "+transfer_data_bank.getUISpecimen().materials.size());

                // Create default parameters for initial,
                // boundary conditions and tasks
                DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);

                // Save inputed params
                SaveParams data_saver = new SaveParams(ui_special_specimen, transfer_data_bank.getUIInterface().getSpecimenPath(), transfer_data_bank.getUISpecimen().getCellSize());
                
                this.close();
                generate_grain_structure = new StructureGeneratorAndRecrystallizator(transfer_data_bank, "generate_grain_structure");  
            }
        });
        
        cancel_button.setOnAction(e -> {
            System.out.println("RecCA_SpecialSpecimenStructureParams: method: actionPerformed: button 'Cancel' is pushed");
            this.close();
        });
    }
    
}
