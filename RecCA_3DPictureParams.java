/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author DmitryB
 */
public class RecCA_3DPictureParams extends Stage{
    
    Scene scene;
    
    GridPane center_layout, bottom_layout;
    ScrollPane scroll_pane;
    Button ok, close;
    
    BorderPane root;
    TitledPane location_type, grain_types;
    
    RadioButton location_type_1, location_type_2, location_type_3, location_type_4, location_type_5, location_type_6, location_type_7, 
            
                grain_type_1, grain_type_2, grain_type_3;
    
    RecCA_3DPictureCreator pict;
    
    public RecCA_3DPictureParams(RecCA_3DPictureCreator picture){
        
        initAllElements();
        inputParameters(new UI_3DPictureParams());
        
        pict = picture;
        
        root = new BorderPane();
        
        root.setCenter(scroll_pane);
        root.setBottom(bottom_layout);
        
        scene = new Scene(root);
        
        this.setTitle("3D Picture Parameters");
        this.setScene(scene);
        this.show();
    }
    
    private void initAllElements(){
        
        location_type = new TitledPane();
        location_type.setText("Cell's LOCATION TYPE");
        
        location_type_1 = new RadioButton("OUTER CELL");
        location_type_1.setTooltip(new Tooltip("Cell located in outer boundary of specimen"));
        
        location_type_2 = new RadioButton("INTRAGRANULAR CELL");
        location_type_2.setTooltip(new Tooltip("Cell located in the interior of grain"));
        
        location_type_3 = new RadioButton("INTERGRANULAR CELL");
        location_type_3.setTooltip(new Tooltip("Cell located at grain boundary in the interior of specimen"));
        
        location_type_4 = new RadioButton("INNER BOUNDARY CELL");
        location_type_4.setTooltip(new Tooltip("Cell located at specimen boundary"));
        
        location_type_5 = new RadioButton("INNER BOUNDARY INTERGRANULAR CELL");
        location_type_5.setTooltip(new Tooltip("Cell located at specimen boundary and at grain boundary"));
        
        location_type_6 = new RadioButton("GRAIN BOUNDARY REGION CELL");
        location_type_6.setTooltip(new Tooltip("Cell located in grain boundary region in the interior of specimen"));
        
        location_type_7 = new RadioButton("INNER BOUND GRAIN BOUND REGION CELL");
        location_type_7.setTooltip(new Tooltip("Cell located in grain boundary region at specimen boundary"));
        
        GridPane location_type_layout = new GridPane();
        location_type_layout.setVgap(3.0);
        location_type_layout.setAlignment(Pos.CENTER_LEFT);
        
        location_type_layout.add(location_type_1, 0, 0);
        location_type_layout.add(location_type_2, 0, 1);
        location_type_layout.add(location_type_3, 0, 2);
        location_type_layout.add(location_type_4, 0, 3);
        location_type_layout.add(location_type_5, 0, 4);
        location_type_layout.add(location_type_6, 0, 5);
        location_type_layout.add(location_type_7, 0, 6);
        
        location_type.setContent(location_type_layout);
        location_type.setExpanded(false);
        
        grain_types = new TitledPane();
        grain_types.setText("GRAIN TYPES");
        grain_types.setTooltip(new Tooltip("Types of grain according to its role in recrystallization process"));
        
        grain_type_1 = new RadioButton("INITIAL GRAIN");
        grain_type_2 = new RadioButton("NEW GRAIN");
        grain_type_3 = new RadioButton("TWINNING GRAIN");
        
        GridPane grain_types_layout = new GridPane();
        grain_types_layout.setVgap(3.0);
        grain_types_layout.setAlignment(Pos.CENTER_LEFT);
        
        grain_types_layout.add(grain_type_1, 0, 0);
        grain_types_layout.add(grain_type_2, 0, 1);
        grain_types_layout.add(grain_type_3, 0, 2);
        
        grain_types.setContent(grain_types_layout);
        grain_types.setExpanded(false);
        
        center_layout = new GridPane();
        center_layout.setAlignment(Pos.CENTER);
        center_layout.setPadding(new Insets(10, 10, 10, 10));
        center_layout.setVgap(2.0);
        
        center_layout.add(location_type, 0, 1);
        center_layout.add(grain_types, 0, 2);
        
        scroll_pane = new ScrollPane();
        scroll_pane.setContent(center_layout);
        
        ok = new Button("OK");
        close = new Button("CLOSE");
        
        bottom_layout = new GridPane();
        bottom_layout.setAlignment(Pos.BOTTOM_CENTER);
        bottom_layout.setPadding(new Insets(10, 10, 10, 10));
        bottom_layout.setHgap(10.0);
        
        bottom_layout.add(ok, 0, 0);
        bottom_layout.add(close, 1, 0);
        
    }    
    
    private void inputParameters(UI_3DPictureParams ui_params){
        
        location_type.setExpanded(ui_params.is_location_type_expanded);
        grain_types.setExpanded(ui_params.is_grain_types_expanded);
        
        location_type_1.setSelected(ui_params.OUTER_CELL);
        location_type_2.setSelected(ui_params.INTRAGRANULAR_CELL);
        location_type_3.setSelected(ui_params.INTERGRANULAR_CELL);
        location_type_4.setSelected(ui_params.INNER_BOUNDARY_CELL);
        location_type_5.setSelected(ui_params.INNER_BOUNDARY_INTERGRANULAR_CELL);
        location_type_6.setSelected(ui_params.GRAIN_BOUNDARY_REGION_CELL);
        location_type_7.setSelected(ui_params.INNER_BOUND_GRAIN_BOUND_REGION_CELL);
        
        grain_type_1.setSelected(ui_params.INITIAL_GRAIN);
        grain_type_2.setSelected(ui_params.NEW_GRAIN);
        grain_type_3.setSelected(ui_params.TWINNING_GRAIN);
        
    }
    
}
