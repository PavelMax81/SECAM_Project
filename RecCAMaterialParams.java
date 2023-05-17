/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author dmitryb
 */
public class RecCAMaterialParams extends Stage{
    
    /** This field is intended for creation helps for users what to input when
     * new material is creating or if some material was chosen,
     * then these fields for show what it's the parameters
     */    
    Label help_mod_elast,help_density, help_heatExpansionCoeff, help_yieldStrain, help_ultimateStrain;
    Label help_energy_coeff, help_specific_heat_capacity, help_thermal_conductivity;
    Label help_thermal_conduct_bound, help_phonon_portion;
    Label help_actEnergy, help_angleLimitHAGB, help_energyHAGB, help_maxMobility;
    Label help_mod_shear, help_lattice_parameter, help_disl_distr_coeff;
    Label help_disl_Max_Mobility, help_mech_Max_Mobility, help_yield_state_coeff, help_ultimate_state_coeff;
    Label help_part_vol_fraction, help_part_radius;
    Label help_molar_mass, help_threshold_stress;    
    Label help_torsion_energy_coeff, help_torsion_energy_coeff_gr_bound, help_torsion_energy_coeff_gb_region;
    Label help_lowTemperThrValue, help_highTemperThrValue;
    
    Label help_lattice_vector_A_length_name, help_lattice_vector_B_length_name, help_lattice_vector_C_length_name;
    Label help_lattice_angle_vecA_vecB_name, help_lattice_angle_vecB_vecC_name, help_lattice_angle_vecC_vecA_name;
    Label help_lattice_anis_coeff_name;
    
    Label help_min_twin_temperature_name, help_twinning_temperature_name;
    Label help_max_prob_recryst_name, help_max_prob_twinning_name, help_min_disl_density_name;
    
    /** This field is intended for creation of text fields for inputing or showing of
     * the values of the parameters of corresponding material
     */    
    TextField mod_elast,density, heatExpansionCoeff, yieldStrain, ultimateStrain, energy_coeff;
    TextField specific_heat_capacity, thermal_conductivity, thermal_conduct_bound, phonon_portion;
    TextField lowTemperThrValue, highTemperThrValue, actEnergy, angleLimitHAGB, energyHAGB;
    TextField maxMobility, mod_shear, disl_distr_coeff;
    TextField disl_Max_Mobility, mech_Max_Mobility, yield_state_coeff, ultimate_state_coeff;
    TextField part_vol_fraction, part_radius;
    TextField molar_mass, threshold_stress;
    TextField torsion_energy_coeff, torsion_energy_coeff_gr_bound, torsion_energy_coeff_gb_region;
    
    TextField lattice_parameter;
    TextField lattice_vector_A_length, lattice_vector_B_length, lattice_vector_C_length;
    TextField lattice_angle_vecA_vecB, lattice_angle_vecB_vecC, lattice_angle_vecC_vecA;
    TextField lattice_anis_coeff;
    TextField min_twin_temperature, twinning_temperature;
    TextField max_prob_recryst, max_prob_twinning, min_disl_density;
    
    /** This field is intended for creating buttons: "Save as" and "OK" or "Cancel"
     */    
    Button button_save, button_ok, button_cancel;
    
    /** This field is intended for creation default bank of material's data,
     * or for creation bank of material's data by loading from
     * corresponding file when some material was chosen
     */
    UIMaterial some_global_material_data_bank;

    /** This Field is intended  for save params of new or changed material
     */
    SaveParams save_material;

    /** This field is intended for input file name for saving params
     */
    TextField textfield_for_file_location;
    
    /** This field is intended for controll all transferred data bank
     */
    TransferredDataBank transfer_data_bank;
    
    RecCAFileList list_of_materials;
    
    VBox root;
    Scene scene;
    
    public RecCAMaterialParams(UIMaterial some_material, String button_name, TransferredDataBank receive_data_bank, RecCAFileList list){
        System.out.println("RecCAMaterialParams: constructor creation start");
        this.transfer_data_bank = receive_data_bank;
        this.list_of_materials = list;
        some_global_material_data_bank = new UIMaterial(some_material);
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(10, 2, 10, 2));
        
        root = new VBox();
        scene = new Scene(root);
        
        root.getChildren().addAll(
                initAllElements(),
                sep1        
        );
        setParameters();
        createButtonsForParamsOfMaterials(button_name);
        handleEvents();
        
        this.setScene(scene);
        this.setTitle("Material Parameters");
        root.setAlignment(Pos.CENTER);
    }
    
    /**
     * button_save = new Button(UICommon.BUTTON_SAVE_NAME);
        button_save.setPadding(new Insets(5, 20, 5, 20));
        button_ok = new Button(UICommon.BUTTON_OK_NAME);
        button_ok.setPadding(new Insets(5, 20, 5, 20));
     * @return 
     */
    
    public void createButtonsForParamsOfMaterials(String name){
        System.out.println("RecCAMaterialParams: method: createButtonsForParamsOfMaterials: started");
        
        Separator sep2 = new Separator(Orientation.VERTICAL);
        sep2.setPadding(new Insets(0, 5, 0, 5));
        
        HBox bottom_layout = new HBox();
        
        button_cancel = new Button(UICommon.BUTTON_CANCEL_NAME);
        button_cancel.setPadding(new Insets(5, 20, 5, 20));
        button_save = new Button(UICommon.BUTTON_SAVE_NAME);
        button_save.setPadding(new Insets(5, 20, 5, 20));
        button_ok = new Button(UICommon.BUTTON_OK_NAME);
        button_ok.setPadding(new Insets(5, 20, 5, 20));
            
        if(name.equals(UICommon.BUTTON_SAVE_NAME)){            
            bottom_layout.getChildren().addAll(button_save, sep2, button_cancel);
        }        
        else{
            bottom_layout.getChildren().addAll(button_ok, sep2, button_cancel);
        }
        bottom_layout.setAlignment(Pos.CENTER);
        root.getChildren().add(bottom_layout);
    }
    
    public Node initAllElements(){
        
        help_mod_elast               = new Label(UICommon.MOD_ELAST_NAME);
        help_density                 = new Label(UICommon.DENSITY_NAME);
        help_heatExpansionCoeff      = new Label(UICommon.HEAT_EXPANSION_COEFF_NAME);
        help_yieldStrain             = new Label(UICommon.YIELD_STRAIN_NAME);
        help_ultimateStrain          = new Label(UICommon.ULTIMATE_STRAIN_NAME);
        help_energy_coeff            = new Label(UICommon.ENERGY_COEFF_NAME);
        help_specific_heat_capacity  = new Label(UICommon.SPECIFIC_HEAT_CAPACITY_NAME);
        help_thermal_conductivity    = new Label(UICommon.THERMAL_CONDUCTIVITY_NAME);
        help_thermal_conduct_bound   = new Label(UICommon.THERMAL_CONDUCT_BOUND_NAME);
        help_phonon_portion          = new Label(UICommon.PHONON_PORTION_NAME);
        help_yield_state_coeff       = new Label(UICommon.YIELD_STATE_COEFF_NAME);
        help_ultimate_state_coeff    = new Label(UICommon.ULTIMATE_STATE_COEFF_NAME);
        help_molar_mass              = new Label(UICommon.MOLAR_MASS_NAME);
        help_threshold_stress        = new Label(UICommon.THRESHOLD_STRESS_NAME);
        help_lowTemperThrValue       = new Label(UICommon.LOW_TEMPER_THR_VALUE_NAME);
        help_highTemperThrValue      = new Label(UICommon.HIGH_TEMPER_THR_VALUE_NAME);
        help_actEnergy               = new Label(UICommon.ACT_ENERGY_NAME);
        help_angleLimitHAGB          = new Label(UICommon.ANGLE_LIMIT_HAGB_NAME);
        help_energyHAGB              = new Label(UICommon.ENERGY_HAGB_NAME);
        help_maxMobility             = new Label(UICommon.MAX_MOBILITY_NAME);
        help_mod_shear               = new Label(UICommon.MOD_SHEAR_NAME);
        help_lattice_parameter       = new Label(UICommon.LATTICE_PARAMETER_NAME);
        help_disl_distr_coeff        = new Label(UICommon.DISL_DISTR_COEFF_NAME);
        help_disl_Max_Mobility       = new Label(UICommon.DISL_MAX_MOBILITY_NAME);
        help_mech_Max_Mobility       = new Label(UICommon.MECH_MAX_MOBILITY_NAME);
        help_part_vol_fraction       = new Label(UICommon.PART_VOL_FRACTION_NAME);
        help_part_radius             = new Label(UICommon.PART_RADIUS_NAME);
        help_torsion_energy_coeff    = new Label(UICommon.TORSION_ENERGY_COEFF_NAME);
        help_torsion_energy_coeff_gr_bound  = new Label(UICommon.TORSION_ENERGY_COEFF_GB_1_NAME);
        help_torsion_energy_coeff_gb_region = new Label(UICommon.TORSION_ENERGY_COEFF_GB_2_NAME);
        help_lattice_vector_A_length_name = new Label(UICommon.LATTICE_VECTOR_A_LENGTH);
        help_lattice_vector_B_length_name = new Label(UICommon.LATTICE_VECTOR_B_LENGTH);
        help_lattice_vector_C_length_name = new Label(UICommon.LATTICE_VECTOR_C_LENGTH);
        help_lattice_angle_vecA_vecB_name = new Label(UICommon.LATTICE_ANGLE_VEC_A_VEC_B);
        help_lattice_angle_vecB_vecC_name = new Label(UICommon.LATTICE_ANGLE_VEC_B_VEC_C);
        help_lattice_angle_vecC_vecA_name = new Label(UICommon.LATTICE_ANGLE_VEC_C_VEC_A);
        help_lattice_anis_coeff_name      = new Label(UICommon.LATTICE_ANISOTROPY_COEFF);
        help_min_twin_temperature_name    = new Label(UICommon.MIN_TWIN_TEMPERATURE);
        help_twinning_temperature_name    = new Label(UICommon.TWINNING_TEMPERATURE);
        help_max_prob_recryst_name        = new Label(UICommon.MAX_PROB_RECRYST);
        help_max_prob_twinning_name       = new Label(UICommon.MAX_PROB_TWINNING);
        help_min_disl_density_name        = new Label(UICommon.MINIMAL_DISL_DENSITY);
        
        mod_elast = new TextField();
        mod_elast.setMaxWidth(50.0d);
        density = new TextField();
        density.setMaxWidth(50.0d);
        heatExpansionCoeff = new TextField();
        heatExpansionCoeff.setMaxWidth(50.0d);
        yieldStrain = new TextField();
        yieldStrain.setMaxWidth(50.0d);
        ultimateStrain = new TextField();
        ultimateStrain.setMaxWidth(50.0d);
        energy_coeff = new TextField();
        energy_coeff.setMaxWidth(50.0d);
        specific_heat_capacity = new TextField();
        specific_heat_capacity.setMaxWidth(50.0d);
        thermal_conductivity = new TextField();
        thermal_conductivity.setMaxWidth(50.0d);
        thermal_conduct_bound = new TextField();
        thermal_conduct_bound.setMaxWidth(50.0d);
        phonon_portion = new TextField();
        phonon_portion.setMaxWidth(50.0d);
        lowTemperThrValue = new TextField();
        lowTemperThrValue.setMaxWidth(50.0d);
        highTemperThrValue = new TextField();
        highTemperThrValue.setMaxWidth(50.0d);
        actEnergy = new TextField();
        actEnergy.setMaxWidth(50.0d);
        angleLimitHAGB = new TextField();
        angleLimitHAGB.setMaxWidth(50.0d);
        energyHAGB = new TextField();
        energyHAGB.setMaxWidth(50.0d);
        maxMobility = new TextField();
        maxMobility.setMaxWidth(50.0d);
        mod_shear = new TextField();
        mod_shear.setMaxWidth(50.0d);
        disl_distr_coeff = new TextField();
        disl_distr_coeff.setMaxWidth(50.0d);
        disl_Max_Mobility = new TextField();
        disl_Max_Mobility.setMaxWidth(50.0d);
        mech_Max_Mobility = new TextField(); 
        mech_Max_Mobility.setMaxWidth(50.0d);
        yield_state_coeff = new TextField();
        yield_state_coeff.setMaxWidth(50.0d);
        ultimate_state_coeff = new TextField();
        ultimate_state_coeff.setMaxWidth(50.0d);
        part_vol_fraction = new TextField();
        part_vol_fraction.setMaxWidth(50.0d);
        part_radius = new TextField();
        part_radius.setMaxWidth(50.0d);
        molar_mass = new TextField(); 
        molar_mass.setMaxWidth(50.0d);
        threshold_stress = new TextField();
        threshold_stress.setMaxWidth(50.0d);
        torsion_energy_coeff = new TextField();
        torsion_energy_coeff.setMaxWidth(50.0d);
        torsion_energy_coeff_gr_bound = new TextField(); 
        torsion_energy_coeff_gr_bound.setMaxWidth(50.0d);
        torsion_energy_coeff_gb_region = new TextField();
        torsion_energy_coeff_gb_region.setMaxWidth(50.0d);

        lattice_parameter = new TextField();
        lattice_parameter.setMaxWidth(50.0d);
        lattice_vector_A_length = new TextField();
        lattice_vector_A_length.setMaxWidth(50.0d);
        lattice_vector_B_length = new TextField(); 
        lattice_vector_B_length.setMaxWidth(50.0d);
        lattice_vector_C_length = new TextField();
        lattice_vector_C_length.setMaxWidth(50.0d);
        lattice_angle_vecA_vecB = new TextField();
        lattice_angle_vecA_vecB.setMaxWidth(50.0d);
        lattice_angle_vecB_vecC = new TextField(); 
        lattice_angle_vecB_vecC.setMaxWidth(50.0d);
        lattice_angle_vecC_vecA = new TextField();
        lattice_angle_vecC_vecA.setMaxWidth(50.0d);
        lattice_anis_coeff = new TextField();
        lattice_anis_coeff.setMaxWidth(50.0d);
        min_twin_temperature = new TextField();
        min_twin_temperature.setMaxWidth(50.0d);
        twinning_temperature = new TextField();
        twinning_temperature.setMaxWidth(50.0d);
        max_prob_recryst = new TextField();
        max_prob_recryst.setMaxWidth(50.0d);
        max_prob_twinning = new TextField();
        max_prob_twinning.setMaxWidth(50.0d);
        min_disl_density = new TextField();
        min_disl_density.setMaxWidth(50.0d);
        
        Separator sep1 = new Separator();
        sep1.setOrientation(Orientation.VERTICAL);
        sep1.setPadding(new Insets(2, 10, 2, 10));
        Separator sep2 = new Separator();
        sep2.setOrientation(Orientation.VERTICAL);
        sep2.setPadding(new Insets(2, 10, 2, 10));
        
        GridPane layout = new GridPane();
        layout.setVgap(3.0d);
        layout.setHgap(3.0d);
        layout.setAlignment(Pos.CENTER);
        
        GridPane.setConstraints(help_mod_elast, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_density, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_heatExpansionCoeff, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_yieldStrain, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_ultimateStrain, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_energy_coeff, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_specific_heat_capacity, 0, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_thermal_conductivity, 0, 7, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_thermal_conduct_bound, 0, 8, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_phonon_portion, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_yield_state_coeff, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_ultimate_state_coeff, 0, 11, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_molar_mass, 0, 12, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_threshold_stress, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(mod_elast, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(density, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(heatExpansionCoeff, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(yieldStrain, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(ultimateStrain, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(energy_coeff, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(specific_heat_capacity, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(thermal_conductivity, 1, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(thermal_conduct_bound, 1, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(phonon_portion, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(yield_state_coeff, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(ultimate_state_coeff, 1, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(molar_mass, 1, 12, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(threshold_stress, 1, 13, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 2, 0, 1, 14, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(help_lowTemperThrValue, 3, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_highTemperThrValue, 3, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_actEnergy, 3, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_angleLimitHAGB, 3, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_energyHAGB, 3, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_maxMobility, 3, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_mod_shear, 3, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_lattice_parameter, 3, 7, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_disl_distr_coeff, 3, 8, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_disl_Max_Mobility, 3, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_mech_Max_Mobility, 3, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_part_vol_fraction, 3, 11, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_part_radius, 3, 12, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_torsion_energy_coeff, 3, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_torsion_energy_coeff_gr_bound, 3, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_torsion_energy_coeff_gb_region, 3, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(lowTemperThrValue, 4, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(highTemperThrValue, 4, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(actEnergy, 4, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(angleLimitHAGB, 4, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(energyHAGB, 4, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(maxMobility, 4, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(mod_shear, 4, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lattice_parameter, 4, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(disl_distr_coeff, 4, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(disl_Max_Mobility, 4, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(mech_Max_Mobility, 4, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(part_vol_fraction, 4, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(part_radius, 4, 12, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(torsion_energy_coeff, 4, 13, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(torsion_energy_coeff_gr_bound, 4, 14, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(torsion_energy_coeff_gb_region, 4, 15, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 5, 0, 1, 16, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(help_lattice_vector_A_length_name, 6, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_lattice_vector_B_length_name, 6, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_lattice_vector_C_length_name, 6, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_lattice_angle_vecA_vecB_name, 6, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_lattice_angle_vecB_vecC_name, 6, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_lattice_angle_vecC_vecA_name, 6, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_lattice_anis_coeff_name, 6, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_min_twin_temperature_name, 6, 7, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_twinning_temperature_name, 6, 8, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_max_prob_recryst_name, 6, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_max_prob_twinning_name, 6, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(help_min_disl_density_name, 6, 11, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(lattice_vector_A_length, 7, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lattice_vector_B_length, 7, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lattice_vector_C_length, 7, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lattice_angle_vecA_vecB, 7, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lattice_angle_vecB_vecC, 7, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lattice_angle_vecC_vecA, 7, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lattice_anis_coeff, 7, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(min_twin_temperature, 7, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(twinning_temperature, 7, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_prob_recryst, 7, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_prob_twinning, 7, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(min_disl_density, 7, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        
        layout.getChildren().addAll(
                 sep1, sep2,
                 help_mod_elast,help_density, help_heatExpansionCoeff, help_yieldStrain, help_ultimateStrain,
                 help_energy_coeff, help_specific_heat_capacity, help_thermal_conductivity,
                 help_thermal_conduct_bound, help_phonon_portion,
                 help_actEnergy, help_angleLimitHAGB, help_energyHAGB, help_maxMobility,
                 help_mod_shear, help_lattice_parameter, help_disl_distr_coeff,
                 help_disl_Max_Mobility, help_mech_Max_Mobility, help_yield_state_coeff, help_ultimate_state_coeff,
                 help_part_vol_fraction, help_part_radius,
                 help_molar_mass, help_threshold_stress,  
                 help_torsion_energy_coeff, help_torsion_energy_coeff_gr_bound, help_torsion_energy_coeff_gb_region,
                 help_lowTemperThrValue, help_highTemperThrValue,
                 help_lattice_vector_A_length_name, help_lattice_vector_B_length_name, help_lattice_vector_C_length_name,
                 help_lattice_angle_vecA_vecB_name, help_lattice_angle_vecB_vecC_name, help_lattice_angle_vecC_vecA_name,
                 help_lattice_anis_coeff_name,
                 help_min_twin_temperature_name, help_twinning_temperature_name,
                 help_max_prob_recryst_name, help_max_prob_twinning_name, help_min_disl_density_name,
                 mod_elast,density, heatExpansionCoeff, yieldStrain, ultimateStrain, energy_coeff,
                 specific_heat_capacity, thermal_conductivity, thermal_conduct_bound, phonon_portion,
                 lowTemperThrValue, highTemperThrValue, actEnergy, angleLimitHAGB, energyHAGB,
                 maxMobility, mod_shear, disl_distr_coeff,
                 disl_Max_Mobility, mech_Max_Mobility, yield_state_coeff, ultimate_state_coeff,
                 part_vol_fraction, part_radius,
                 molar_mass, threshold_stress,
                 torsion_energy_coeff, torsion_energy_coeff_gr_bound, torsion_energy_coeff_gb_region,
                 lattice_parameter,
                 lattice_vector_A_length, lattice_vector_B_length, lattice_vector_C_length,
                 lattice_angle_vecA_vecB, lattice_angle_vecB_vecC, lattice_angle_vecC_vecA,
                 lattice_anis_coeff,
                 min_twin_temperature, twinning_temperature,
                 max_prob_recryst, max_prob_twinning, min_disl_density
        );        
        return layout;
    }
    
    public void setParameters()
    {
        System.out.println("RecCAMaterialParams: method: createTextFieldsForParamsOfMaterials: started");

        mod_elast.setText((new Double(some_global_material_data_bank.getModElast())).toString());
        density.setText((new Double(some_global_material_data_bank.getDensity())).toString());
        heatExpansionCoeff.setText((new Double(some_global_material_data_bank.getHeatExpansionCoeff())).toString());
        yieldStrain.setText((new Double(some_global_material_data_bank.getYieldStrain())).toString());
        ultimateStrain.setText((new Double(some_global_material_data_bank.getUltimateStrain())).toString());
        energy_coeff.setText((new Double(some_global_material_data_bank.getEnergyCoeff())).toString());
        specific_heat_capacity.setText((new Double(some_global_material_data_bank.getSpecificHeatCapacity())).toString());
        thermal_conductivity.setText((new Double(some_global_material_data_bank.getThermalConductivity())).toString());
        thermal_conduct_bound.setText((new Double(some_global_material_data_bank.getThermalConductBound())).toString());
        phonon_portion.setText((new Double(some_global_material_data_bank.getPhononPortion())).toString());
        yield_state_coeff.setText((new Double(some_global_material_data_bank.getYieldStateCoeff())).toString());
        ultimate_state_coeff.setText((new Double(some_global_material_data_bank.getUltimateStateCoeff())).toString());
        molar_mass.setText((new Double(some_global_material_data_bank.getMolarMass())).toString());
        threshold_stress.setText((new Double(some_global_material_data_bank.getThresholdStress())).toString());
        lowTemperThrValue.setText((new Double(some_global_material_data_bank.getLowTemperThrValue())).toString());
        highTemperThrValue.setText((new Double(some_global_material_data_bank.getHighTemperThrValue())).toString());
        actEnergy.setText((new Double(some_global_material_data_bank.getActEnergy())).toString());
        angleLimitHAGB.setText((new Double(some_global_material_data_bank.getAngleLimitHAGB())).toString());
        energyHAGB.setText((new Double(some_global_material_data_bank.getEnergyHAGB())).toString());
        maxMobility.setText((new Double(some_global_material_data_bank.getMaxMobility())).toString());
        mod_shear.setText((new Double(some_global_material_data_bank.getModShear())).toString());
        lattice_parameter.setText((new Double(some_global_material_data_bank.getLatticeParameter())).toString());
        disl_distr_coeff.setText((new Double(some_global_material_data_bank.getDislDistrCoeff())).toString());
        disl_Max_Mobility.setText((new Double(some_global_material_data_bank.getDislMaxMobility())).toString());
        mech_Max_Mobility.setText((new Double(some_global_material_data_bank.getMechMaxMobility())).toString());
        part_vol_fraction.setText((new Double(some_global_material_data_bank.getPartVolFraction())).toString());
        part_radius.setText((new Double(some_global_material_data_bank.getPartRadius())).toString());
        torsion_energy_coeff.setText((new Double(some_global_material_data_bank.getTorsionEnergyCoeff())).toString());
        torsion_energy_coeff_gr_bound.setText((new Double(some_global_material_data_bank.getTorsionEnergyCoeffForGrBoundary())).toString());
        torsion_energy_coeff_gb_region.setText((new Double(some_global_material_data_bank.getTorsionEnergyCoeffForGrBoundRegion())).toString());
        lattice_vector_A_length.setText((new Double(some_global_material_data_bank.getLatticeVector_A_Length())).toString());
        lattice_vector_B_length.setText((new Double(some_global_material_data_bank.getLatticeVector_B_Length())).toString());
        lattice_vector_C_length.setText((new Double(some_global_material_data_bank.getLatticeVector_C_Length())).toString());
        lattice_angle_vecA_vecB.setText((new Double(some_global_material_data_bank.getLatticeAngle_vecA_vecB())).toString());
        lattice_angle_vecB_vecC.setText((new Double(some_global_material_data_bank.getLatticeAngle_vecB_vecC())).toString());
        lattice_angle_vecC_vecA.setText((new Double(some_global_material_data_bank.getLatticeAngle_vecC_vecA())).toString());
        lattice_anis_coeff.setText((new Double(some_global_material_data_bank.getLatticeAnisCoeff())).toString());
        min_twin_temperature.setText((new Double(some_global_material_data_bank.getMinTwinTemperature())).toString());
        twinning_temperature.setText((new Double(some_global_material_data_bank.getTwinningTemperature())).toString());
        max_prob_recryst.setText((new Double(some_global_material_data_bank.getMaxProbRecryst())).toString());
        max_prob_twinning.setText((new Double(some_global_material_data_bank.getMaxProbTwinning())).toString());
        min_disl_density.setText((new Double(some_global_material_data_bank.getMinDislDensity())).toString());        
    }
    
    /** Save changed parameters in material data bank
     */
    public void inputParams()
    {
        System.out.println("RecCAMaterialParams: method: inputParams: started");

        some_global_material_data_bank.setModElast(new Double(mod_elast.getText()));

        some_global_material_data_bank.setDensity(new Double(density.getText()));

        some_global_material_data_bank.setHeatExpansionCoeff(new Double(heatExpansionCoeff.getText()));

        some_global_material_data_bank.setYieldStrain(new Double(yieldStrain.getText()));

        some_global_material_data_bank.setUltimateStrain(new Double(ultimateStrain.getText()));

        some_global_material_data_bank.setEnergyCoeff(new Double(energy_coeff.getText()));

        some_global_material_data_bank.setSpecificHeatCapacity(new Double(specific_heat_capacity.getText()));

        some_global_material_data_bank.setThermalConductivity(new Double(thermal_conductivity.getText()));

        some_global_material_data_bank.setThermalConductBound(new Double(thermal_conduct_bound.getText()));

        some_global_material_data_bank.setPhononPortion(new Double(phonon_portion.getText()));

        some_global_material_data_bank.setLowTemperThrValue(new Double(lowTemperThrValue.getText()));

        some_global_material_data_bank.setHighTemperThrValue(new Double(highTemperThrValue.getText()));

        some_global_material_data_bank.setActEnergy(new Double(actEnergy.getText()));

        some_global_material_data_bank.setAngleLimitHAGB(new Double(angleLimitHAGB.getText()));

        some_global_material_data_bank.setEnergyHAGB(new Double(energyHAGB.getText()));

        some_global_material_data_bank.setMaxMobility(new Double(maxMobility.getText()));

        some_global_material_data_bank.setModShear(new Double(mod_shear.getText()));

        some_global_material_data_bank.setLatticeParameter(new Double(lattice_parameter.getText()));
        
        some_global_material_data_bank.setDislDistrCoeff(new Double(disl_distr_coeff.getText()));

        some_global_material_data_bank.setDislMaxMobility(new Double(disl_Max_Mobility.getText()));

        some_global_material_data_bank.setMechMaxMobility(new Double(mech_Max_Mobility.getText()));

        some_global_material_data_bank.setYieldStateCoeff(new Double(yield_state_coeff.getText()));

        some_global_material_data_bank.setUltimateStateCoeff(new Double(ultimate_state_coeff.getText()));

        some_global_material_data_bank.setPartVolFraction(new Double(part_vol_fraction.getText()));

        some_global_material_data_bank.setPartRadius(new Double(part_radius.getText()));
        
        some_global_material_data_bank.setMolarMass(new Double(molar_mass.getText()));
        
        some_global_material_data_bank.setThresholdStress(new Double(threshold_stress.getText()));
        
        some_global_material_data_bank.setTorsionEnergyCoeff(new Double(torsion_energy_coeff.getText()));
        
        some_global_material_data_bank.setTorsionEnergyCoeffForGrBoundary(new Double(torsion_energy_coeff_gr_bound.getText()));
        
        some_global_material_data_bank.setTorsionEnergyCoeffForGrBoundRegion(new Double(torsion_energy_coeff_gb_region.getText()));
        
        some_global_material_data_bank.setLatticeVector_A_Length(new Double(lattice_vector_A_length.getText()));
        some_global_material_data_bank.setLatticeVector_B_Length(new Double(lattice_vector_B_length.getText()));
        some_global_material_data_bank.setLatticeVector_C_Length(new Double(lattice_vector_C_length.getText()));
        
        some_global_material_data_bank.setLatticeAngle_vecA_vecB(new Double(lattice_angle_vecA_vecB.getText()));
        some_global_material_data_bank.setLatticeAngle_vecB_vecC(new Double(lattice_angle_vecB_vecC.getText()));
        some_global_material_data_bank.setLatticeAngle_vecC_vecA(new Double(lattice_angle_vecC_vecA.getText()));
        
        some_global_material_data_bank.setLatticeAnisCoeff(new Double(lattice_anis_coeff.getText()));
        
        some_global_material_data_bank.setMinTwinTemperature (new Double(min_twin_temperature.getText()));
        some_global_material_data_bank.setTwinningTemperature(new Double(twinning_temperature.getText()));
        some_global_material_data_bank.setMaxProbRecryst     (new Double(max_prob_recryst.getText()));
        some_global_material_data_bank.setMaxProbTwinning    (new Double(max_prob_twinning.getText()));
        some_global_material_data_bank.setMinDislDensity     (new Double(min_disl_density.getText()));
        
        System.out.println("Torsion energy coeff-s: "+torsion_energy_coeff.getText()+" "+
                           torsion_energy_coeff_gr_bound.getText()+" "+torsion_energy_coeff_gb_region.getText());
    }
    
    /** Contrioll - input parameters or not, and if
     * they input - satisfied for standart or not
     * @return - satisfied for standart or not
     */
    public boolean isParametersInput()
    {
        System.out.println("RecCAMaterialParams: method: isParametersInput: started");

        try
        {
            Double.parseDouble(mod_elast.getText().toString());
            Double.parseDouble(density.getText().toString());
            Double.parseDouble(heatExpansionCoeff.getText().toString());
            Double.parseDouble(yieldStrain.getText().toString());
            Double.parseDouble(ultimateStrain.getText().toString());
            Double.parseDouble(energy_coeff.getText().toString());
            Double.parseDouble(specific_heat_capacity.getText().toString());
            Double.parseDouble(thermal_conductivity.getText().toString());
            Double.parseDouble(thermal_conduct_bound.getText().toString());
            Double.parseDouble(phonon_portion.getText().toString());
            Double.parseDouble(lowTemperThrValue.getText().toString());
            Double.parseDouble(highTemperThrValue.getText().toString());
            Double.parseDouble(actEnergy.getText().toString());
            Double.parseDouble(angleLimitHAGB.getText().toString());
            Double.parseDouble(energyHAGB.getText().toString());
            Double.parseDouble(maxMobility.getText().toString());
            Double.parseDouble(mod_shear.getText().toString());
            Double.parseDouble(lattice_parameter.getText().toString());
            Double.parseDouble(disl_distr_coeff.getText().toString());
            Double.parseDouble(disl_Max_Mobility.getText().toString());
            Double.parseDouble(mech_Max_Mobility.getText().toString());
            Double.parseDouble(yield_state_coeff.getText().toString());
            Double.parseDouble(ultimate_state_coeff.getText().toString());
            Double.parseDouble(part_vol_fraction.getText().toString());
            Double.parseDouble(part_radius.getText().toString());
            
            Double.parseDouble(molar_mass.getText().toString());
            Double.parseDouble(threshold_stress.getText().toString());
            Double.parseDouble(torsion_energy_coeff.getText().toString());
            Double.parseDouble(torsion_energy_coeff_gr_bound.getText().toString());
            Double.parseDouble(torsion_energy_coeff_gb_region.getText().toString());

            return true;
        }
        catch (NumberFormatException e)
        {
        }
        return false;
    }
    
    
    
    public void savingDialofOfParamsForMaterials()
    {
        System.out.println("RecCAMaterialParams: method: savingDialofOfParamsForMaterials: started");
        
        VBox layout = new VBox();
        
        textfield_for_file_location = new TextField();
        
        Button ok_btn = new Button("Save");
        ok_btn.setPadding(new Insets(5, 20, 5, 20));
        Button cancel_btn = new Button("Close");
        cancel_btn.setPadding(new Insets(5, 20, 5, 20));
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(10, 2, 10, 2));
        Separator sep2 = new Separator(Orientation.VERTICAL);
        sep2.setPadding(new Insets(0, 7, 0 ,7));
        
        HBox bottom_layout = new HBox(ok_btn, sep2 , cancel_btn);
        bottom_layout.setAlignment(Pos.CENTER);
        
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(textfield_for_file_location, sep1, bottom_layout);
        
        Stage stage = new Stage();
        stage.setTitle("Save");
        stage.setScene(new Scene(layout));
        stage.show();
        
        ok_btn.setOnAction(e -> {
            if(textfield_for_file_location.getText() != null){
                /*
                 * Save changed parameters in material data bank
                 */

                inputParams();
                save_material = new SaveParams(some_global_material_data_bank, textfield_for_file_location.getText());
                
            }
            else{
                new Alert(AlertType.ERROR, "Please Input Material Name");
            }
//            transfer_data_bank.getRecCAInterface().main_layout.setCenter(list_of_materials);
        });
        
        cancel_btn.setOnAction(e -> stage.close());
        
    }
    
    public void handleEvents(){
        button_save.setOnAction(e -> {
            if(isParametersInput()){
                System.out.println("RecCAMaterialParams: method: actionPerformed: button 'save' is pushed");

                /*
                 * create saving dialog window 
                 */

                savingDialofOfParamsForMaterials();
            }
            else{
                new Alert(AlertType.ERROR, "Please Input All Paramets").show();
            }
        });
    }
}
