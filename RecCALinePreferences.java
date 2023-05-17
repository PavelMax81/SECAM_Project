/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author DmitryB
 */
public class RecCALinePreferences extends TitledPane{
    
    GridPane layout;
    
    public RecCALinePreferences(){
        initAllElements();
        
        this.setText("Preferences");
        this.setContent(layout);
    }
    
    TextField name;
    ColorPicker color_picker;
    
    public void initAllElements(){
        
        layout = new GridPane();
        
        name = new TextField();
        
        color_picker = new ColorPicker();
        
        Label l2 = new Label("Legend:");
        Label l3 = new Label("Color:");
        
        GridPane.setConstraints(l2, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(name, 1, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(l3, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(color_picker, 1, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        
        layout.setHgap(5.0);
        layout.setVgap(5.0);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.getChildren().addAll(
                l2, l3, 
                color_picker, name
        );
    }
    
}
