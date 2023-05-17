/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.event.EventHandler;
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
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCASpecimenParams extends Stage{
    
    Scene scene;
    VBox root;
    
    Label specimen_parameters,grain_structure,anisotropy_vector;
    Label help_cell_size, help_surf_thickness, help_element_number_x;
    Label help_element_number_y, help_element_number_z, help_min_neighbours_number;
    Label help_specimen_size_x,help_specimen_size_y,help_specimen_size_z;
    Label help_coordinate_x,help_coordinate_y,help_coordinate_z;
    Label help_anisotropy_coeff, help_number_of_phases; //,help_average_grain_length;//
    Label help_part_volume_fraction,help_part_radius;
    
    /**
     * This field is intended for create help of specimen packing type
     */
    Label packing_type_help;
    
    /** Interface labels for the parameters of specimen anisotropy 
     * (anisotropy vector coordinates, anisotropy coefficient)
     */
    Label help_anis_vector_x;
    Label help_anis_vector_y;
    Label help_anis_vector_z;
    Label help_spec_anis_coeff;
    
    TextField specimen_size_x,specimen_size_y,specimen_size_z;
    TextField coordinate_x,coordinate_y,coordinate_z,anisotropy_coeff,number_of_phases;
    TextField particle_volume_fraction,particle_radius;
    TextField cell_size, surf_thickness, min_neighbours_number, element_number_x;
    TextField element_number_y, element_number_z;    
    TextField anis_vector_x, anis_vector_y, anis_vector_z, specimen_anisotropy_coeff;
    
    /**
     * This foelds is intended for create 2 check boxes for choose
     * specimen packing type
     */

    RadioButton HCP_packing_type, SCP_packing_type;
    
    /**
     * This field is intended for choose by user - write grains cellular
     * in specimen structure file or not
     */
    CheckBox grain_cellular_automata;
    
    /**
     * This field is intended for creation a choice between methods
     * which will be used in calculations
     */    
    RadioButton choice_of_method_stochastic,choice_of_method_regular,
            choice_of_method_mixed;
    
    ToggleGroup group_of_choice, packing_type_group;
    
    Button button_generate;
    
    /**
     * Material Table
     */
    Label material_help, angle_range_help, volume_fraction_help, max_deviation_help, disl_density_help;
    Button material_No1, material_No2, material_No3, material_No4, material_No5;
    TextField angle_range_No1, angle_range_No2, angle_range_No3, angle_range_No4, angle_range_No5;
    TextField volume_fraction_No1, volume_fraction_No2, volume_fraction_No3, volume_fraction_No4, volume_fraction_No5;
    TextField max_deviation_No1, max_deviation_No2, max_deviation_No3, max_deviation_No4, max_deviation_No5;
    TextField disl_density_No1, disl_density_No2, disl_density_No3, disl_density_No4, disl_density_No5;
    
    TextField textfield_for_file_location;
    
    /**
     * This field is intended for save params in file
     */

    SaveParams saving;
    
    UISpecimen global_ui_specimen;
    
    StructureGeneratorAndRecrystallizator generate_grain_structure;
    
    /**
     * This field is intended for work with transfer data bank
     */

    TransferredDataBank transfer_data_bank;
    
    RecCAFileList list_of_files;
    
    public RecCASpecimenParams(TransferredDataBank receive_data_bank){
        System.out.println("RecCASpecimenParams: constructor creation start");

        /*
         * Set transfer data bank
         */

        transfer_data_bank = receive_data_bank;

        /*
         * Remember data bank of specimen params
         */

        global_ui_specimen = transfer_data_bank.getUISpecimen();
        
        root = new VBox();
        root.setPadding(new Insets(20,20,20,20));
        scene = new Scene(root);
        
        createHelpsForUsers();
        createSpecimenPackingTypeCheckBoxes();
        createTextFieldsForParams();
        createMethodChoice();
        createButtons();
        addAllElements();
        handleEvents();
        
        textfield_for_file_location = new TextField();
        
        this.setScene(scene);
        this.setTitle("Specimen Parameters");        
    }
    
    public void createHelpsForUsers()
    {
        System.out.println("RecCASpecimenParams: method: createHelpsForUsers: start");
        specimen_parameters = new Label(UICommon.SPECIMEN_PARAMETERS_NAME);
        grain_structure = new Label(UICommon.GRAIN_STRUCTURE_NAME);
        anisotropy_vector = new Label(UICommon.ANISOTROPY_VECTOR_NAME);
        
        help_specimen_size_x = new Label(UICommon.SPECIMEN_SIZE_X_NAME);
        help_specimen_size_y = new Label(UICommon.SPECIMEN_SIZE_Y_NAME);
        help_specimen_size_z = new Label(UICommon.SPECIMEN_SIZE_Z_NAME);
        help_element_number_x = new Label(UICommon.ELEMENT_NUMBER_X_NAME);
        help_element_number_y = new Label(UICommon.ELEMENT_NUMBER_Y_NAME);
        help_element_number_z = new Label(UICommon.ELEMENT_NUMBER_Z_NAME);
        
        help_specimen_size_x.setTooltip(new Tooltip(UICommon.SPECIMEN_SIZE_X_TOOL_TIP));
        help_specimen_size_y.setTooltip(new Tooltip(UICommon.SPECIMEN_SIZE_X_TOOL_TIP));
        help_specimen_size_z.setTooltip(new Tooltip(UICommon.SPECIMEN_SIZE_X_TOOL_TIP));
        help_element_number_x.setTooltip(new Tooltip(UICommon.FE_NUMBER_X_TOOL_TIP));
        help_element_number_y.setTooltip(new Tooltip(UICommon.FE_NUMBER_Y_TOOL_TIP));
        help_element_number_z.setTooltip(new Tooltip(UICommon.FE_NUMBER_Z_TOOL_TIP));
        
        help_cell_size = new Label(UICommon.CELL_SIZE_NAME);
        help_surf_thickness = new Label(UICommon.SURF_THICKNESS_NAME);
        help_min_neighbours_number = new Label(UICommon.MIN_NEIGHBOURS_NUMBER_NAME);
        
        help_cell_size.setTooltip(new Tooltip(UICommon.CELL_DIAMETER_TOOL_TIP));
        help_surf_thickness.setTooltip(new Tooltip(UICommon.SURF_THICKNESS_TOOL_TIP));
        help_min_neighbours_number.setTooltip(new Tooltip(UICommon.MIN_NEIGHBOURS_NUMBER_TOOL_TIP));
        
        help_coordinate_x = new Label(UICommon.COORDINATE_X_NAME);
        help_coordinate_y = new Label(UICommon.COORDINATE_Y_NAME);
        help_coordinate_z = new Label(UICommon.COORDINATE_Z_NAME);
        help_anisotropy_coeff = new Label(UICommon.ANISOTROPY_COEFF_NAME);
        help_number_of_phases = new Label(UICommon.NUMBER_OF_PHASES_NAME);

        packing_type_help = new Label(UICommon.PACKING_TYPE_NAME);
        
        help_anis_vector_x = new Label(UICommon.ANIS_VECTOR_X_NAME);
        help_anis_vector_y = new Label(UICommon.ANIS_VECTOR_Y_NAME);
        help_anis_vector_z = new Label(UICommon.ANIS_VECTOR_Z_NAME);
        help_spec_anis_coeff = new Label(UICommon.SPEC_ANIS_COEFF_NAME);

        help_coordinate_x.setTooltip(new Tooltip(UICommon.COORDINATE_X_TOOL_TIP));
        help_coordinate_y.setTooltip(new Tooltip(UICommon.COORDINATE_Y_TOOL_TIP));
        help_coordinate_z.setTooltip(new Tooltip(UICommon.COORDINATE_Z_TOOL_TIP));
        help_anisotropy_coeff.setTooltip(new Tooltip(UICommon.ANISOTROPY_COEFF_TOOL_TIP));
        help_number_of_phases.setTooltip(new Tooltip(UICommon.NUMBER_OF_PHASES_TOOL_TIP));
        
        help_part_volume_fraction = new Label(UICommon.PARTICLE_VOLUME_FRACTION_NAME);
        help_part_radius = new Label(UICommon.PARTICLE_RADIUS_NAME);

        help_part_volume_fraction.setTooltip(new Tooltip(UICommon.PARTICLE_VOLUME_FRACTION_TOOL_TIP));
        help_part_radius.setTooltip(new Tooltip(UICommon.PARTICLE_RADIUS_TOOL_TIP));
    }
    
    public void createSpecimenPackingTypeCheckBoxes(){
        packing_type_group = new ToggleGroup();
        HCP_packing_type = new RadioButton("HCP");
        SCP_packing_type = new RadioButton("SCP");
        packing_type_group.getToggles().addAll(HCP_packing_type,
                                               SCP_packing_type);
        
        if(global_ui_specimen.getPackingType() == 0)
        {
            SCP_packing_type.setSelected(false);
            HCP_packing_type.setSelected(true);
        }

        if(global_ui_specimen.getPackingType() == 1)
        {
            HCP_packing_type.setSelected(false);
            SCP_packing_type.setSelected(true);
        }        
    }
    
    public void createTextFieldsForParams(){
        System.out.println("RecCASpecimenParams: method: createTextFieldsForParams: start");
        
        grain_cellular_automata = new CheckBox(UICommon.SHOW_GRAIN_STRUCTURE_NAME);
        grain_cellular_automata.setSelected(global_ui_specimen.getGrainCellularAutomata());
        
        cell_size = new TextField();
        cell_size.setMaxWidth(100.0d);
        cell_size.setText((new Double(global_ui_specimen.getCellSize())).toString());

        surf_thickness = new TextField();
        surf_thickness.setMaxWidth(100.0d);
        surf_thickness.setText((new Double(global_ui_specimen.getSurfThickness())).toString());
        surf_thickness.setEditable(false);

        element_number_x = new TextField();
        element_number_x.setMaxWidth(100.0d);
        element_number_x.setText((new Integer(global_ui_specimen.getElementNumberX())).toString());
        element_number_x.setEditable(false);

        element_number_y = new TextField();
        element_number_y.setMaxWidth(100.0d);
        element_number_y.setText((new Integer(global_ui_specimen.getElementNumberY())).toString());
        element_number_y.setEditable(false);

        element_number_z = new TextField();
        element_number_z.setMaxWidth(100.0d);
        element_number_z.setText((new Integer(global_ui_specimen.getElementNumberZ())).toString());
        element_number_z.setEditable(false);

        min_neighbours_number = new TextField();
        min_neighbours_number.setMaxWidth(100.0d);
        min_neighbours_number.setText((new Integer(global_ui_specimen.getMinNeighboursNumber())).toString());

        specimen_size_x = new TextField();
        specimen_size_x.setMaxWidth(100.0d);
        specimen_size_x.setText((new Double(global_ui_specimen.getSpecimenSizeX())).toString());
        
        specimen_size_y = new TextField();
        specimen_size_y.setMaxWidth(100.0d);
        specimen_size_y.setText((new Double(global_ui_specimen.getSpecimenSizeY())).toString());
        
        specimen_size_z = new TextField();
        specimen_size_z.setMaxWidth(100.0d);
        specimen_size_z.setText((new Double(global_ui_specimen.getSpecimenSizeZ())).toString());
        
        coordinate_x = new TextField();
        coordinate_x.setMaxWidth(100.0d);
        coordinate_x.setText((new Double(global_ui_specimen.getCoordinateX())).toString());
        
        coordinate_y = new TextField();
        coordinate_y.setMaxWidth(100.0d);
        coordinate_y.setText((new Double(global_ui_specimen.getCoordinateY())).toString());
        
        coordinate_z = new TextField();
        coordinate_z.setMaxWidth(100.0d);
        coordinate_z.setText((new Double(global_ui_specimen.getCoordinateZ())).toString());

        anisotropy_coeff = new TextField();
        anisotropy_coeff.setMaxWidth(100.0d);
        anisotropy_coeff.setText((new Double(global_ui_specimen.getAnisotropyCoeff())).toString());
        
        anis_vector_x = new TextField();
        anis_vector_x.setMaxWidth(100.0d);
        anis_vector_x.setText((new Double(global_ui_specimen.getAnisVectorX())).toString());
        
        anis_vector_y = new TextField();
        anis_vector_y.setMaxWidth(100.0d);
        anis_vector_y.setText((new Double(global_ui_specimen.getAnisVectorY())).toString());
        
        anis_vector_z = new TextField();
        anis_vector_z.setMaxWidth(100.0d);
        anis_vector_z.setText((new Double(global_ui_specimen.getAnisVectorZ())).toString());
        
        specimen_anisotropy_coeff = new TextField();
        specimen_anisotropy_coeff.setMaxWidth(100.0d);
        specimen_anisotropy_coeff.setText((new Double(global_ui_specimen.getSpecimenAnisotropyCoeff())).toString());
        
        number_of_phases = new TextField();
        number_of_phases.setMaxWidth(100.0d);
        number_of_phases.setText((new Integer(global_ui_specimen.getNumberOfPhases())).toString());
        number_of_phases.setEditable(false);
        
        particle_volume_fraction = new TextField();
        particle_volume_fraction.setMaxWidth(100.0d);
        particle_volume_fraction.setText((new Double(global_ui_specimen.getParticleVolumeFraction())).toString());
        
        particle_radius = new TextField();
        particle_radius.setMaxWidth(100.0d);
        particle_radius.setText((new Double(global_ui_specimen.getParticleRadius())).toString());        
    }
    
    public void createMethodChoice(){
        System.out.println("RecCASpecimenParams: method: createMethodChoice: start");
        
        group_of_choice = new ToggleGroup();
        
        choice_of_method_stochastic = new RadioButton(UICommon.METHOD_STOCHASTIC_NAME);
        choice_of_method_regular = new RadioButton(UICommon.METHOD_REGULAR_NAME);
        choice_of_method_mixed = new RadioButton(UICommon.METHOD_MIXED_NAME);
        
        choice_of_method_stochastic.setTooltip(new Tooltip(UICommon.STOCHASTIC_METHOD_TOOL_TIP));
        choice_of_method_regular.setTooltip(new Tooltip(UICommon.REGULAR_METHOD_TOOL_TIP));
        
        choice_of_method_stochastic.setSelected(global_ui_specimen.getStochasticMethod());
        choice_of_method_regular.setSelected(global_ui_specimen.getRegularMethod());
        choice_of_method_mixed.setSelected(global_ui_specimen.getMixedMethod());
        
        group_of_choice.getToggles().addAll(choice_of_method_stochastic,
                                            choice_of_method_regular,
                                            choice_of_method_mixed);
    }
    
    public void createButtons(){
        System.out.println("RecCASpecimenParams: method: createButtons: start");
        button_generate = new Button(UICommon.BUTTON_GENERATE_NAME);
        button_generate.setPadding(new Insets(5, 30, 5, 30));
    }
    
    public GridPane createMaterialTable(){
        
        material_help = new Label("Materials:");
        angle_range_help = new Label("Angle Range:");
        volume_fraction_help = new Label("Volume Fraction:");
        max_deviation_help = new Label("Maximal Deviation:");
        disl_density_help = new Label("Dislocation Density:");
        
        material_No1 = new Button();
        material_No1.setPadding(new Insets(5, 30, 5, 30));
        material_No2 = new Button();
        material_No2.setPadding(new Insets(5, 30, 5, 30));
        material_No3 = new Button();
        material_No3.setPadding(new Insets(5, 30, 5, 30));
        material_No4 = new Button();
        material_No4.setPadding(new Insets(5, 30, 5, 30));
        material_No5 = new Button();
        material_No5.setPadding(new Insets(5, 30, 5, 30));
        
        angle_range_No1 = new TextField();
        angle_range_No2 = new TextField();
        angle_range_No3 = new TextField();
        angle_range_No4 = new TextField();
        angle_range_No5 = new TextField();
        
        volume_fraction_No1 = new TextField();
        volume_fraction_No2 = new TextField();
        volume_fraction_No3 = new TextField();
        volume_fraction_No4 = new TextField();
        volume_fraction_No5 = new TextField();
        
        max_deviation_No1 = new TextField();
        max_deviation_No2 = new TextField();
        max_deviation_No3 = new TextField();
        max_deviation_No4 = new TextField();
        max_deviation_No5 = new TextField();
        
        disl_density_No1 = new TextField();
        disl_density_No2 = new TextField();
        disl_density_No3 = new TextField();
        disl_density_No4 = new TextField();
        disl_density_No5 = new TextField();
        
        material_No1.setText(global_ui_specimen.materials.get(0));
        material_No2.setText(global_ui_specimen.materials.get(1));
        material_No3.setText(global_ui_specimen.materials.get(2));
        material_No4.setText(global_ui_specimen.materials.get(3));
        material_No5.setText(global_ui_specimen.materials.get(4));
        
        
        angle_range_No1.setText(String.valueOf(global_ui_specimen.angle_range.get(0)));
        angle_range_No2.setText(String.valueOf(global_ui_specimen.angle_range.get(1)));
        angle_range_No3.setText(String.valueOf(global_ui_specimen.angle_range.get(2)));
        angle_range_No4.setText(String.valueOf(global_ui_specimen.angle_range.get(3)));
        angle_range_No5.setText(String.valueOf(global_ui_specimen.angle_range.get(4)));
        
        volume_fraction_No1.setText(String.valueOf(global_ui_specimen.volume_fraction.get(0)));
        volume_fraction_No2.setText(String.valueOf(global_ui_specimen.volume_fraction.get(1)));
        volume_fraction_No3.setText(String.valueOf(global_ui_specimen.volume_fraction.get(2)));
        volume_fraction_No4.setText(String.valueOf(global_ui_specimen.volume_fraction.get(3)));
        volume_fraction_No5.setText(String.valueOf(global_ui_specimen.volume_fraction.get(4)));
        
        disl_density_No1.setText(String.valueOf(global_ui_specimen.disl_density.get(0)));
        disl_density_No2.setText(String.valueOf(global_ui_specimen.disl_density.get(1)));
        disl_density_No3.setText(String.valueOf(global_ui_specimen.disl_density.get(2)));
        disl_density_No4.setText(String.valueOf(global_ui_specimen.disl_density.get(3)));
        disl_density_No5.setText(String.valueOf(global_ui_specimen.disl_density.get(4)));
        
        max_deviation_No1.setText(String.valueOf(global_ui_specimen.max_deviation.get(0)));
        max_deviation_No2.setText(String.valueOf(global_ui_specimen.max_deviation.get(1)));
        max_deviation_No3.setText(String.valueOf(global_ui_specimen.max_deviation.get(2)));
        max_deviation_No4.setText(String.valueOf(global_ui_specimen.max_deviation.get(3)));
        max_deviation_No5.setText(String.valueOf(global_ui_specimen.max_deviation.get(4)));
        
        GridPane mat_tab = new GridPane();
        mat_tab.setGridLinesVisible(true);
        
        mat_tab.add(material_help, 0, 0);
        mat_tab.add(material_No1, 0, 1);
        mat_tab.add(material_No2, 0, 2);
        mat_tab.add(material_No3, 0, 3);
        mat_tab.add(material_No4, 0, 4);
        mat_tab.add(material_No5, 0, 5);
        
        mat_tab.add(volume_fraction_help, 1, 0);
        mat_tab.add(volume_fraction_No1, 1, 1);
        mat_tab.add(volume_fraction_No2, 1, 2);
        mat_tab.add(volume_fraction_No3, 1, 3);
        mat_tab.add(volume_fraction_No4, 1, 4);
        mat_tab.add(volume_fraction_No5, 1, 5);
        
        mat_tab.add(angle_range_help, 2, 0);
        mat_tab.add(angle_range_No1, 2, 1);
        mat_tab.add(angle_range_No2, 2, 2);
        mat_tab.add(angle_range_No3, 2, 3);
        mat_tab.add(angle_range_No4, 2, 4);
        mat_tab.add(angle_range_No5, 2, 5);
        
        mat_tab.add(disl_density_help, 3, 0);
        mat_tab.add(disl_density_No1, 3, 1);
        mat_tab.add(disl_density_No2, 3, 2);
        mat_tab.add(disl_density_No3, 3, 3);
        mat_tab.add(disl_density_No4, 3, 4);
        mat_tab.add(disl_density_No5, 3, 5);
        
        mat_tab.add(max_deviation_help, 4, 0);
        mat_tab.add(max_deviation_No1, 4, 1);
        mat_tab.add(max_deviation_No2, 4, 2);
        mat_tab.add(max_deviation_No3, 4, 3);
        mat_tab.add(max_deviation_No4, 4, 4);
        mat_tab.add(max_deviation_No5, 4, 5);
        mat_tab.setAlignment(Pos.CENTER);        
        return mat_tab;
    }
    
    public void addAllElements(){
        System.out.println("RecCASpecimenParams: method: addAllElements: start");
        
        HBox packing_type_layout = new HBox(packing_type_help, HCP_packing_type, SCP_packing_type);
        
        GridPane grid_pane = new GridPane();
        grid_pane.setVgap(5.0d);
        grid_pane.setHgap(5.0d);

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();
        sep2.setOrientation(Orientation.VERTICAL);
        Separator sep3 = new Separator();
        Separator sep4 = new Separator();
        sep4.setOrientation(Orientation.VERTICAL);
        Separator sep5 = new Separator();
        Separator sep9 = new Separator();
        Separator sep10 = new Separator();
        sep10.setOrientation(Orientation.VERTICAL);
        
        GridPane.setConstraints(specimen_parameters, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_cell_size, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(cell_size, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_specimen_size_x, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(specimen_size_x, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_specimen_size_y, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(specimen_size_y, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_specimen_size_z, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(specimen_size_z, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep1, 0, 5, 2, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(help_element_number_x, 0, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(element_number_x, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_element_number_y, 0, 7, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(element_number_y, 1, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_element_number_z, 0, 8, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(element_number_z, 1, 8, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep2, 2, 0, 1, 8, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(grain_structure, 3, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(anisotropy_vector, 3, 1, 2, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(help_coordinate_x, 3, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(coordinate_x, 4, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_coordinate_y, 3, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(coordinate_y, 4, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_coordinate_z, 3, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(coordinate_z, 4, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_anisotropy_coeff, 3, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anisotropy_coeff, 4, 5, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep3, 3, 6, 2, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(help_number_of_phases, 3, 7, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(number_of_phases, 4, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_surf_thickness, 3, 8, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(surf_thickness, 4, 8, 1, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep4, 5, 0, 1, 8, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(choice_of_method_stochastic, 6, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(choice_of_method_regular, 6, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(choice_of_method_mixed, 6, 2, 1, 1, HPos.LEFT, VPos.CENTER);

        GridPane.setConstraints(sep5, 6, 3, 2, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(help_part_volume_fraction, 6, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(particle_volume_fraction, 7, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_part_radius, 6, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(particle_radius, 7, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_min_neighbours_number, 6, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_neighbours_number, 7, 6, 1, 1, HPos.RIGHT, VPos.CENTER);

        GridPane.setConstraints(sep9, 6, 7, 2, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(grain_cellular_automata, 6, 8, 2, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(packing_type_layout, 6, 9, 2, 1, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(sep10, 8, 0, 1, 9, HPos.CENTER, VPos.CENTER);

        GridPane.setConstraints(help_anis_vector_x, 9, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anis_vector_x, 10, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_anis_vector_y, 9, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anis_vector_y, 10, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_anis_vector_z, 9, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anis_vector_z, 10, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(help_spec_anis_coeff, 9, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(specimen_anisotropy_coeff, 10, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        
        grid_pane.getChildren().addAll(
                sep1, sep2, sep3, sep4, sep5, sep9, sep10,
                specimen_parameters,grain_structure,anisotropy_vector,
                help_cell_size, help_surf_thickness, help_element_number_x,
                help_element_number_y, help_element_number_z, help_min_neighbours_number,
                help_specimen_size_x,help_specimen_size_y,help_specimen_size_z,
                help_coordinate_x,help_coordinate_y,help_coordinate_z,
                help_anisotropy_coeff, help_number_of_phases,
                help_part_volume_fraction,help_part_radius,
                packing_type_layout,
                help_anis_vector_x,help_anis_vector_y,help_anis_vector_z,help_spec_anis_coeff,
                specimen_size_x,specimen_size_y,specimen_size_z,
                coordinate_x,coordinate_y,coordinate_z,anisotropy_coeff,number_of_phases,
                particle_volume_fraction,particle_radius,
                cell_size, surf_thickness, min_neighbours_number, element_number_x,
                element_number_y, element_number_z,
                anis_vector_x, anis_vector_y, anis_vector_z, specimen_anisotropy_coeff,
                grain_cellular_automata,
                choice_of_method_stochastic,
                choice_of_method_regular,
                choice_of_method_mixed
        );
        
        grid_pane.setAlignment(Pos.CENTER);
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 2, 10, 2));
        Separator sep6 = new Separator();
        sep6.setPadding(new Insets(10, 2, 10, 2));
        
        root.getChildren().addAll(grid_pane, 
                                  sep,
                                  createMaterialTable(),
                                  sep6,
                                  button_generate);
    }
    
    Button save_button = new Button("Save");
    Button cancel_button = new Button("Cancel");
    
    public void chooseFileLocation(){
        System.out.println("RecCASpecimenParams: method: chooseFileLocation: start");
        
        SavingDialog save = new SavingDialog(textfield_for_file_location);
        
        this.close();
        
        save.save_btn.setOnAction(e -> {
            if(isNameCorrect(textfield_for_file_location)){                
                saving = new SaveParams(global_ui_specimen,textfield_for_file_location.getText());
                transfer_data_bank.getUIInterface().setSpecimenPath(textfield_for_file_location.getText()); 
                
                // Clear information abou lrs (special specimen structure) path
                transfer_data_bank.getUIInterface().setSpecialSpecimenPath(null);
                System.out.println("StructureGeneratorAndRecrystallizator: method: actionPerformed: button 'Special' is pushed");
                // Create frame for input special specimen parameters
                RecCA_SpecialSpecimenStructureParams special_specimen = new RecCA_SpecialSpecimenStructureParams(transfer_data_bank);
                
                              
            }
            save.close();
        });
    }
    
    public void handleEvents(){
        button_generate.setOnAction(e -> {
            if(isParametersInput()){
                setInputParams();
                chooseFileLocation();
            }            
        });
        
        material_No1.setOnAction(e -> {
            
            list_of_files = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            
            list_of_files.ok.setOnAction(event -> {
                material_No1.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                list_of_files.close();
            });
            
            list_of_files.create_new.setOnAction(event -> {
                RecCAMaterialParams mat_params = new RecCAMaterialParams(transfer_data_bank.getUIMaterial(), UICommon.BUTTON_SAVE_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.change.setOnAction(event -> {
                String mat = list_of_files.list.getSelectionModel().getSelectedItem().toString() + "." + Common.MATERIAL_EXTENSION;
                RecCAMaterialParams mat_params = new RecCAMaterialParams(new UIMaterial(Common.MATERIALS_PATH + "/" + mat), UICommon.BUTTON_OK_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.getClickCount() == 2){
                        material_No1.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                        list_of_files.close();
                    }
                }
            });            
        });
        
        material_No2.setOnAction(e -> {
            list_of_files = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            
            list_of_files.ok.setOnAction(event -> {
                material_No2.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                list_of_files.close();
            });
            
            list_of_files.create_new.setOnAction(event -> {
                RecCAMaterialParams mat_params = new RecCAMaterialParams(transfer_data_bank.getUIMaterial(), UICommon.BUTTON_SAVE_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.change.setOnAction(event -> {
                String mat = list_of_files.list.getSelectionModel().getSelectedItem().toString() + "." + Common.MATERIAL_EXTENSION;
                RecCAMaterialParams mat_params = new RecCAMaterialParams(new UIMaterial(Common.MATERIALS_PATH + "/" + mat), UICommon.BUTTON_OK_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.getClickCount() == 2){
                        material_No2.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                        list_of_files.close();
                    }
                }
            });      
        });
        
        material_No3.setOnAction(e -> {
            list_of_files = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
                        
            list_of_files.ok.setOnAction(event -> {
                material_No3.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                list_of_files.close();
            });
            
            list_of_files.create_new.setOnAction(event -> {
                RecCAMaterialParams mat_params = new RecCAMaterialParams(transfer_data_bank.getUIMaterial(), UICommon.BUTTON_SAVE_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.change.setOnAction(event -> {
                String mat = list_of_files.list.getSelectionModel().getSelectedItem().toString() + "." + Common.MATERIAL_EXTENSION;
                RecCAMaterialParams mat_params = new RecCAMaterialParams(new UIMaterial(Common.MATERIALS_PATH + "/" + mat), UICommon.BUTTON_OK_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.getClickCount() == 2){
                        material_No3.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                        list_of_files.close();
                    }
                }
            });
        });
        
        material_No4.setOnAction(e -> {
            list_of_files = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
                        
            list_of_files.ok.setOnAction(event -> {
                material_No4.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                list_of_files.close();
            });            
            
            list_of_files.create_new.setOnAction(event -> {
                RecCAMaterialParams mat_params = new RecCAMaterialParams(transfer_data_bank.getUIMaterial(), UICommon.BUTTON_SAVE_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.change.setOnAction(event -> {
                String mat = list_of_files.list.getSelectionModel().getSelectedItem().toString() + "." + Common.MATERIAL_EXTENSION;
                RecCAMaterialParams mat_params = new RecCAMaterialParams(new UIMaterial(Common.MATERIALS_PATH + "/" + mat), UICommon.BUTTON_OK_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.getClickCount() == 2){
                        material_No4.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                        list_of_files.close();
                    }
                }
            });
        });
        
        material_No5.setOnAction(e -> {
            list_of_files = new RecCAFileList(transfer_data_bank, Common.MATERIALS_PATH, Common.MATERIAL_EXTENSION);
            
            list_of_files.ok.setOnAction(event -> {
                material_No5.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                list_of_files.close();
            });            
            
            list_of_files.create_new.setOnAction(event -> {
                RecCAMaterialParams mat_params = new RecCAMaterialParams(transfer_data_bank.getUIMaterial(), UICommon.BUTTON_SAVE_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.change.setOnAction(event -> {
                String mat = list_of_files.list.getSelectionModel().getSelectedItem().toString() + "." + Common.MATERIAL_EXTENSION;
                RecCAMaterialParams mat_params = new RecCAMaterialParams(new UIMaterial(Common.MATERIALS_PATH + "/" + mat), UICommon.BUTTON_OK_NAME, transfer_data_bank, list_of_files);
                mat_params.show();
            });
            
            list_of_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.getClickCount() == 2){
                        material_No5.setText(list_of_files.list.getSelectionModel().getSelectedItem().toString());
                        list_of_files.close();
                    }
                }
            });
        });
        
    }
    
    public void setInputParams(){
        System.out.println("RecCASpecimenParams: method: setInputParams: start");
        /*
         * Set inputed params in data bank
         */

        global_ui_specimen.setCellSize(Double.valueOf(cell_size.getText()));

        global_ui_specimen.setElementNumberX(Integer.valueOf(element_number_x.getText()));
        global_ui_specimen.setElementNumberY(Integer.valueOf(element_number_y.getText()));
        global_ui_specimen.setElementNumberZ(Integer.valueOf(element_number_z.getText()));

        global_ui_specimen.setSurfThickness(Double.valueOf(surf_thickness.getText()));

        global_ui_specimen.setMinNeighboursNumber(Integer.valueOf(min_neighbours_number.getText()));
        
        global_ui_specimen.setSpecimenSizeX(Double.valueOf(specimen_size_x.getText()));
        global_ui_specimen.setSpecimenSizeY(Double.valueOf(specimen_size_y.getText()));
        global_ui_specimen.setSpecimenSizeZ(Double.valueOf(specimen_size_z.getText()));
        
        System.out.println("SPECIMEN SIZE X: "+specimen_size_x.getText());
        System.out.println("SPECIMEN SIZE Y: "+specimen_size_y.getText());
        System.out.println("SPECIMEN SIZE Z: "+specimen_size_z.getText());

        global_ui_specimen.setCoordinateX(Double.valueOf(coordinate_x.getText()));
        global_ui_specimen.setCoordinateY(Double.valueOf(coordinate_y.getText()));
        global_ui_specimen.setCoordinateZ(Double.valueOf(coordinate_z.getText()));

        global_ui_specimen.setAnisotropyCoeff(Double.valueOf(anisotropy_coeff.getText()));
        
        global_ui_specimen.setSpecimenAnisotropyCoeff(Double.valueOf(specimen_anisotropy_coeff.getText()));
        
        global_ui_specimen.setAnisVectorX(Double.valueOf(anis_vector_x.getText()));
        global_ui_specimen.setAnisVectorY(Double.valueOf(anis_vector_y.getText()));
        global_ui_specimen.setAnisVectorZ(Double.valueOf(anis_vector_z.getText()));    
        
        System.out.println("ANIS_VECTOR_x: "+Double.valueOf(anis_vector_x.getText()));
        System.out.println("ANIS_VECTOR_y: "+Double.valueOf(anis_vector_y.getText()));
        System.out.println("ANIS_VECTOR_Z: "+Double.valueOf(anis_vector_z.getText()));
        
        global_ui_specimen.setNumberOfPhases(Integer.valueOf(number_of_phases.getText()));

        global_ui_specimen.setParticleVolumeFraction(Double.valueOf(particle_volume_fraction.getText()));
        global_ui_specimen.setParticleRadius(Double.valueOf(particle_radius.getText()));

        global_ui_specimen.setStochasticMethod(choice_of_method_stochastic.isSelected());
        global_ui_specimen.setRegularMethod(choice_of_method_regular.isSelected());
        global_ui_specimen.setMixedMethod(choice_of_method_mixed.isSelected());

        global_ui_specimen.setGrainCellularAutomata(grain_cellular_automata.isSelected());

        if(HCP_packing_type.isSelected())
            global_ui_specimen.setPackingType(0);
        if(SCP_packing_type.isSelected())
            global_ui_specimen.setPackingType(1);
        
        global_ui_specimen.angle_range.set(0, Double.parseDouble(angle_range_No1.getText()));
        global_ui_specimen.angle_range.set(1, Double.parseDouble(angle_range_No2.getText()));
        global_ui_specimen.angle_range.set(2, Double.parseDouble(angle_range_No3.getText()));
        global_ui_specimen.angle_range.set(3, Double.parseDouble(angle_range_No4.getText()));
        global_ui_specimen.angle_range.set(4, Double.parseDouble(angle_range_No5.getText()));
        
        global_ui_specimen.volume_fraction.set(0, Double.parseDouble(volume_fraction_No1.getText()));
        global_ui_specimen.volume_fraction.set(1, Double.parseDouble(volume_fraction_No2.getText()));
        global_ui_specimen.volume_fraction.set(2, Double.parseDouble(volume_fraction_No3.getText()));
        global_ui_specimen.volume_fraction.set(3, Double.parseDouble(volume_fraction_No4.getText()));
        global_ui_specimen.volume_fraction.set(4, Double.parseDouble(volume_fraction_No5.getText()));
        
        global_ui_specimen.materials.set(0, material_No1.getText());
        global_ui_specimen.materials.set(1, material_No2.getText());
        global_ui_specimen.materials.set(2, material_No3.getText());
        global_ui_specimen.materials.set(3, material_No4.getText());
        global_ui_specimen.materials.set(4, material_No5.getText());
        
        global_ui_specimen.disl_density.set(0, Double.parseDouble(disl_density_No1.getText()));
        global_ui_specimen.disl_density.set(1, Double.parseDouble(disl_density_No2.getText()));
        global_ui_specimen.disl_density.set(2, Double.parseDouble(disl_density_No3.getText()));
        global_ui_specimen.disl_density.set(3, Double.parseDouble(disl_density_No4.getText()));
        global_ui_specimen.disl_density.set(4, Double.parseDouble(disl_density_No5.getText()));
        
        global_ui_specimen.max_deviation.set(0, Double.parseDouble(max_deviation_No1.getText()));
        global_ui_specimen.max_deviation.set(1, Double.parseDouble(max_deviation_No2.getText()));
        global_ui_specimen.max_deviation.set(2, Double.parseDouble(max_deviation_No3.getText()));
        global_ui_specimen.max_deviation.set(3, Double.parseDouble(max_deviation_No4.getText()));
        global_ui_specimen.max_deviation.set(4, Double.parseDouble(max_deviation_No5.getText()));        
    }
    
    public boolean isParametersInput(){
        System.out.println("RecCASpecimenParams: method: isParametersInput: start");
        
        try
        {
            Double.parseDouble(cell_size.getText().toString());
            Integer.parseInt(element_number_x.getText().toString());
            Integer.parseInt(element_number_y.getText().toString());
            Integer.parseInt(element_number_z.getText().toString());
            Double.parseDouble(surf_thickness.getText().toString());
            Double.parseDouble(min_neighbours_number.getText().toString());
            Double.parseDouble(specimen_size_x.getText().toString());
            Double.parseDouble(specimen_size_y.getText().toString());
            Double.parseDouble(specimen_size_z.getText().toString());
            
            Double.parseDouble(coordinate_x.getText().toString());
            Double.parseDouble(coordinate_y.getText().toString());
            Double.parseDouble(coordinate_z.getText().toString());
            Double.parseDouble(anisotropy_coeff.getText().toString());            
            
            Double.parseDouble(anis_vector_x.getText().toString());
            Double.parseDouble(anis_vector_y.getText().toString());
            Double.parseDouble(anis_vector_z.getText().toString());
            Double.parseDouble(specimen_anisotropy_coeff.getText().toString());

            Integer.parseInt(number_of_phases.getText().toString());
            Double.parseDouble(particle_volume_fraction.getText().toString());
            Double.parseDouble(particle_radius.getText().toString());

            if(Double.parseDouble(number_of_phases.getText().toString()) > 5)
            {   
                new Alert(AlertType.ERROR, "The number of phases is very large!\nIt must be >= 5!\nInput another number of phases!").showAndWait();
                return false;
            }

            if(Double.parseDouble(cell_size.getText().toString())==0.0)
            {   
                new Alert(AlertType.ERROR, "The cell diameter equals 0.0!\nPlease, input another value of cell diameter!").showAndWait();
                return false;
            }

            if(!HCP_packing_type.isSelected() & !SCP_packing_type.isSelected())
            {   
                new Alert(AlertType.ERROR, "The specimen packing type in not chosen!").showAndWait();
                return false;
            }

            if(Double.parseDouble(specimen_size_x.getText().toString())==0.0)
            {   
                new Alert(AlertType.ERROR, UICommon.ERROR_SPECIMEN_SIZE_X_IS_NULL_EXCEPTION_NAME).showAndWait();
                return false;
            }
            if(Double.parseDouble(specimen_size_y.getText().toString())==0.0)
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_SPECIMEN_SIZE_Y_IS_NULL_EXCEPTION_NAME).showAndWait();
                return false;
            }
            if(Double.parseDouble(specimen_size_z.getText().toString())==0.0)
            {
                new Alert(AlertType.ERROR, UICommon.ERROR_SPECIMEN_SIZE_Z_IS_NULL_EXCEPTION_NAME).showAndWait();
                return false;
            }
            
            if(material_No1.getText().equals("select_material...") || 
               material_No2.getText().equals("select_material...") ||
               material_No3.getText().equals("select_material...") ||
               material_No4.getText().equals("select_material...") ||
               material_No5.getText().equals("select_material..."))
            {
                new Alert(AlertType.ERROR, "Select All Materials").showAndWait();
                return false;
            }

            return true;
        }
         catch (NumberFormatException e) 
        {
            new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
        }
        
        return false;
    }
    
    /*
     * Controll inputed name
     * @param users_file_name - what name was inputed
     * @return - can be created file with inputed name or not
     */
    public boolean isNameCorrect(TextField users_file_name){
        System.out.println("RecCASpecimenParams: method: isNameCorrect: start");
        
        String file_name = users_file_name.getText();
        
        if(file_name.isEmpty())
        {   
            new Alert(AlertType.ERROR, UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME).show();
            return false;
        }

//        if(file_name.equals(UICommon.TASK_PARAM_NAME))
//        {
//            JOptionPane.showMessageDialog(null,UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME,UICommon.ERROR_DIALOG_NAME, JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//
//        if(file_name.length()>4)
//            if(file_name.substring(file_name.length()-4,file_name.length()).equals(UICommon.END_REC_NAME))
//            {
//                JOptionPane.showMessageDialog(null,UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME,UICommon.ERROR_DIALOG_NAME, JOptionPane.ERROR_MESSAGE);
//                return false;
//            }
//
//        if(file_name.length()>5)
//            if(file_name.substring(file_name.length()-5,file_name.length()).equals(UICommon.END_GEOM_NAME))
//            {
//                JOptionPane.showMessageDialog(null,UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME,UICommon.ERROR_DIALOG_NAME, JOptionPane.ERROR_MESSAGE);
//                return false;
//            }
//
//        if(file_name.length()>7)
//            if(file_name.substring(file_name.length()-7,file_name.length()).equals(UICommon.END_GRAINS_NAME))
//            {
//                JOptionPane.showMessageDialog(null,UICommon.ERROR_INPUT_ANOTHER_FILE_NAME_NAME,UICommon.ERROR_DIALOG_NAME, JOptionPane.ERROR_MESSAGE);
//                return false;
//            }
        
        return true;
    }
}
