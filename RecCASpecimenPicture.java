/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author dmitryb
 */
public class RecCASpecimenPicture extends VBox{
    
    Button show_scheme, show_structure, save;
    
    ToolBar tool_bar;
    
    TextArea text_area;
    
    UISpecimen ui_specimen;
    
    TransferredDataBank transfer_data_bank;
    
    Label l1 , l11 ,l12, l13, l14, l15;
    Label l2 , l21 ,l22, l23, l24, l25;
    Label l3 , l31 ,l32, l33, l34, l35;
    Label l4 , l41 ,l42, l43, l44, l45;
    Label l5 , l51 ,l52, l53, l54, l55;
    
    BorderPane content;
    
    RecCAScheme scheme;
    RecCAInitialStructure structure;
    
    public RecCASpecimenPicture(UISpecimen temp_ui_specimen, TransferredDataBank receive_transfer_data_bank){
        
        System.out.println("RecCASpecimenPicture: constructor: SaveParams(UISpecimen temp_ui_specimen, UIInterface ui, TransferredDataBank receive_transfer_data_bank): creation start");

        // Remember text area in interface main window
        // for write specimen infi in it
        
        text_area = new TextArea();

        // Remember specimen data bank

        ui_specimen = temp_ui_specimen;

        // Remember transferred data bank

        transfer_data_bank = receive_transfer_data_bank;

        // Set specimen data bank in transferred data bank for future
        // actions with it's data

        transfer_data_bank.setUISpecimen(ui_specimen);
        
        /*
         * write specimen params data
         */
        text_area.setEditable(false);
        text_area.setText("Specimen parameters:\n");
        text_area.setText(text_area.getText()+"\n"+UICommon.CELL_SIZE_PROPERTIES+" = "+new Double(ui_specimen.getCellSize()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.ELEMENT_NUMBER_X_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberX()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.ELEMENT_NUMBER_Y_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberY()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.ELEMENT_NUMBER_Z_PROPERTIES+" = "+new Integer(ui_specimen.getElementNumberZ()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.SURF_THICKNESS_PROPERTIES+" = "+new Double(ui_specimen.getSurfThickness()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.MIN_NEIGHBOURS_NUMBER_PROPERTIES+" = "+new Double(ui_specimen.getMinNeighboursNumber()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.SPECIMEN_SIZE_X_PROPERTIES+" = "+new Double(ui_specimen.getSpecimenSizeX()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.SPECIMEN_SIZE_Y_PROPERTIES+" = "+new Double(ui_specimen.getSpecimenSizeY()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.SPECIMEN_SIZE_Z_PROPERTIES+" = "+new Double(ui_specimen.getSpecimenSizeZ()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.COORDINATE_X_PROPERTIES+" = "+new Double(ui_specimen.getCoordinateX()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.COORDINATE_Y_PROPERTIES+" = "+new Double(ui_specimen.getCoordinateY()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.COORDINATE_Z_PROPERTIES+" = "+new Double(ui_specimen.getCoordinateZ()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.ANISOTROPY_COEFF_PROPERTIES+" = "+new Double(ui_specimen.getAnisotropyCoeff()).toString());

        text_area.setText(text_area.getText()+"\n"+UICommon.NUMBER_OF_PHASES_PROPERTIES+" = "+new Integer(ui_specimen.getNumberOfPhases()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.PARTICLE_VOLUME_FRACTION_PROPERTIES+" = "+new Double(ui_specimen.getParticleVolumeFraction()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.PARTICLE_RADIUS_PROPERTIES+" = "+new Double(ui_specimen.getParticleRadius()).toString());
        
        text_area.setText(text_area.getText()+"\n"+UICommon.ANIS_VECTOR_X_NAME+" = "+new Double(ui_specimen.getAnisVectorX()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.ANIS_VECTOR_Y_NAME+" = "+new Double(ui_specimen.getAnisVectorY()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.ANIS_VECTOR_Z_NAME+" = "+new Double(ui_specimen.getAnisVectorZ()).toString());
        text_area.setText(text_area.getText()+"\n"+UICommon.SPEC_ANIS_COEFF_NAME+" = "+new Double(ui_specimen.getSpecimenAnisotropyCoeff()).toString());

        // Write packing type of specimen
        if(ui_specimen.getPackingType()==0)
            text_area.setText(text_area.getText()+"\n"+UICommon.PACKING_TYPE_NAME+" HCP");
        if(ui_specimen.getPackingType()==1)
            text_area.setText(text_area.getText()+"\n"+UICommon.PACKING_TYPE_NAME+" SCP");

        text_area.setText(text_area.getText()+"\n"+"show grain bounds = "+ui_specimen.getGrainCellularAutomata());

        /*
         * write calculation method data
         */

        if(ui_specimen.getStochasticMethod()==true)
            text_area.setText(text_area.getText()+"\n"+UICommon.METHOD_NAME+" = "+UICommon.METHOD_STOCHASTIC_PROPERTIES);
        if(ui_specimen.getRegularMethod()==true)
            text_area.setText(text_area.getText()+"\n"+UICommon.METHOD_NAME+" = "+UICommon.METHOD_REGULAR_PROPERTIES);
        if(ui_specimen.getMixedMethod()==true)
            text_area.setText(text_area.getText()+"\n"+UICommon.METHOD_NAME+" = "+UICommon.METHOD_MIXED_PROPERTIES);            
        
        l1 = new Label("Materials");
        l11 = new Label(ui_specimen.materials.get(0));
        l12 = new Label(ui_specimen.materials.get(1));
        l13 = new Label(ui_specimen.materials.get(2));
        l14 = new Label(ui_specimen.materials.get(3));
        l15 = new Label(ui_specimen.materials.get(4));
        
        
        l2 = new Label("Volume Fraction");
        l21 = new Label(String.valueOf(ui_specimen.volume_fraction.get(0)));
        l22 = new Label(String.valueOf(ui_specimen.volume_fraction.get(1)));
        l23 = new Label(String.valueOf(ui_specimen.volume_fraction.get(2)));
        l24 = new Label(String.valueOf(ui_specimen.volume_fraction.get(3)));
        l25 = new Label(String.valueOf(ui_specimen.volume_fraction.get(4)));
        
        l3 = new Label("Angle Range");
        l31 = new Label(String.valueOf(ui_specimen.angle_range.get(0)));
        l32 = new Label(String.valueOf(ui_specimen.angle_range.get(1)));
        l33 = new Label(String.valueOf(ui_specimen.angle_range.get(2)));
        l34 = new Label(String.valueOf(ui_specimen.angle_range.get(3)));
        l35 = new Label(String.valueOf(ui_specimen.angle_range.get(4)));
        
        l4 = new Label("Disl. Density");
        l41 = new Label(String.valueOf(ui_specimen.disl_density.get(0)));
        l42 = new Label(String.valueOf(ui_specimen.disl_density.get(1)));
        l43 = new Label(String.valueOf(ui_specimen.disl_density.get(2)));
        l44 = new Label(String.valueOf(ui_specimen.disl_density.get(3)));
        l45 = new Label(String.valueOf(ui_specimen.disl_density.get(4)));
        
        l5 = new Label("Max. Deviation");
        l51 = new Label(String.valueOf(ui_specimen.max_deviation.get(0)));
        l52 = new Label(String.valueOf(ui_specimen.max_deviation.get(1)));
        l53 = new Label(String.valueOf(ui_specimen.max_deviation.get(2)));
        l54 = new Label(String.valueOf(ui_specimen.max_deviation.get(3)));
        l55 = new Label(String.valueOf(ui_specimen.max_deviation.get(4)));
        
        GridPane mat_tab = new GridPane();
        mat_tab.setAlignment(Pos.CENTER);
        mat_tab.setHgap(3.0d);
        mat_tab.setVgap(3.0d);
        mat_tab.setPadding(new Insets(10, 10, 10, 10));
        
        mat_tab.add(l1, 0, 0);
        mat_tab.add(l11, 0, 1);
        mat_tab.add(l12, 0, 2);
        mat_tab.add(l13, 0, 3);
        mat_tab.add(l14, 0, 4);
        mat_tab.add(l15, 0, 5);
        
        mat_tab.add(l2, 1, 0);
        mat_tab.add(l21, 1, 1);
        mat_tab.add(l22, 1, 2);
        mat_tab.add(l23, 1, 3);
        mat_tab.add(l24, 1, 4);
        mat_tab.add(l25, 1, 5);
        
        mat_tab.add(l3, 2, 0);
        mat_tab.add(l31, 2, 1);
        mat_tab.add(l32, 2, 2);
        mat_tab.add(l33, 2, 3);
        mat_tab.add(l34, 2, 4);
        mat_tab.add(l35, 2, 5);
        
        mat_tab.add(l4, 3, 0);
        mat_tab.add(l41, 3, 1);
        mat_tab.add(l42, 3, 2);
        mat_tab.add(l43, 3, 3);
        mat_tab.add(l44, 3, 4);
        mat_tab.add(l45, 3, 5);
        
        mat_tab.add(l5, 4, 0);
        mat_tab.add(l51, 4, 1);
        mat_tab.add(l52, 4, 2);
        mat_tab.add(l53, 4, 3);
        mat_tab.add(l54, 4, 4);
        mat_tab.add(l55, 4, 5);
        
        tool_bar = new ToolBar();
        
        show_scheme = new Button("Show Scheme");
        show_structure = new Button("Show Structure");
        save = new Button("Save");
        
        tool_bar.getItems().addAll(show_scheme, show_structure, save);
        
        content = new BorderPane();
        
        SplitPane split_pane = new SplitPane();
        split_pane.setPadding(new Insets(5, 5, 5, 5));
        
        split_pane.getItems().add(new VBox(tool_bar,content));
        split_pane.getItems().add(text_area);
        
        this.getChildren().addAll(
                split_pane,
                new Separator(),
                mat_tab
        );
        handleEvents();
        
        scheme = new RecCAScheme(transfer_data_bank);
        structure = new RecCAInitialStructure(transfer_data_bank);
    }    
    
    public void handleEvents(){
        
        show_scheme.setOnAction(e -> {
            System.out.println("Show Scheme");
            content.setCenter(scheme);;
        });
        
        show_structure.setOnAction(e -> {
            System.out.println("Show Structure");
            content.setCenter(structure);;
        });
        
        save.setOnAction(e -> {
            System.out.println("Save Image");            
        });
    }    
}
