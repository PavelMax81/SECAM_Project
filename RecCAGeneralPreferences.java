/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author DmitryB
 */
public class RecCAGeneralPreferences extends VBox{
    
    public RecCAGeneralPreferences(){
        initAllElements();
        bindAllValues();
    }
    
    CheckBox show_grid, show_legend;
    
    TitledPane value_axis, time_axis, axis_titles;
    
    CheckBox time_axis_check, value_axis_check;
    
    TextField time_axis_tick_unit, time_axis_lower_bound, time_axis_upper_bound,
              value_axis_tick_unit, value_axis_lower_bound, value_axis_upper_bound;
    
    TextField time_axis_title, value_axis_title;
    ComboBox time_axis_prefix, value_axis_prefix;
    
    public void initAllElements(){
        
        value_axis = new TitledPane();
        value_axis.setText("Value Axis");
        
        time_axis = new TitledPane();
        time_axis.setText("Time Axis");
        
        axis_titles = new TitledPane();
        axis_titles.setText("Axis Titles");
        axis_titles.setExpanded(false);
        
        time_axis_check = new CheckBox("Time Axis");
        value_axis_check = new CheckBox("Value Axis");
        time_axis_check.setSelected(true);
        value_axis_check.setSelected(true);
        
        time_axis_tick_unit = new TextField();
        time_axis_lower_bound = new TextField();
        time_axis_upper_bound = new TextField();
        
        value_axis_tick_unit = new TextField();
        value_axis_lower_bound = new TextField();
        value_axis_upper_bound = new TextField();
        
        time_axis_title = new TextField();
        time_axis_prefix = new ComboBox();
        time_axis_prefix.getItems().addAll("none", "n", "mc", "m", "k", "M", "G");
        time_axis_prefix.getSelectionModel().selectFirst();
        
        value_axis_title = new TextField();
        value_axis_prefix = new ComboBox();
        value_axis_prefix.getItems().addAll("none", "n", "mc", "m", "k", "M", "G");
        value_axis_prefix.getSelectionModel().selectFirst();
        
        show_grid = new CheckBox("Show Grid");
        show_grid.setSelected(true);
        show_legend = new CheckBox("Show Legend");
        show_legend.setSelected(true);
        
        Label l1 = new Label("Tick Unit");
        Label l2 = new Label("Lower Bound");
        Label l21 = new Label("Upper Bound");
        
        Label l3 = new Label("Tick Unit");
        Label l4 = new Label("Lower Bound");
        Label l41 = new Label("Upper Bound");
        
        Label l5 = new Label("Time Axis");
        Label l6 = new Label("Value Axis");
        
        Label l9 = new Label("Autoranging:");
        
        GridPane layout1 = new GridPane();
        
        GridPane.setConstraints(l9, 0, 0, 1, 2, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(time_axis_check, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(value_axis_check, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        layout1.setHgap(5.0d);
        layout1.setVgap(5.0d);
        layout1.setAlignment(Pos.CENTER_LEFT);
        layout1.getChildren().addAll(
                l9, time_axis_check, value_axis_check
        );
        
        this.getChildren().addAll(
                layout1,
                new Separator()
        );
        
        GridPane layout2 = new GridPane();
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l21, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(time_axis_tick_unit, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(time_axis_lower_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(time_axis_upper_bound, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        
        
        layout2.setHgap(5.0d);
        layout2.setVgap(5.0d);
        layout2.setAlignment(Pos.CENTER_LEFT);
        layout2.getChildren().addAll(
                l1, l2, l21, time_axis_tick_unit, time_axis_lower_bound, time_axis_upper_bound
        );
        time_axis.setContent(layout2);
        
        this.getChildren().addAll(
                time_axis,
                new Separator()
        );
        
        GridPane layout3 = new GridPane();
        
        GridPane.setConstraints(l3, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l41, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(value_axis_tick_unit, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(value_axis_lower_bound, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(value_axis_upper_bound, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        
        layout3.setHgap(5.0d);
        layout3.setVgap(5.0d);
        layout3.setAlignment(Pos.CENTER_LEFT);
        layout3.getChildren().addAll(
                l3, l4, l41, value_axis_tick_unit, value_axis_lower_bound, value_axis_upper_bound
        );
        value_axis.setContent(layout3);
        
        this.getChildren().addAll(
                value_axis,
                new Separator()
        );
        
        GridPane layout4 = new GridPane();
        
        GridPane.setConstraints(l5, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(time_axis_title, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(time_axis_prefix, 2, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(value_axis_title, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(value_axis_prefix, 2, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        
        layout4.setHgap(5.0d);
        layout4.setVgap(5.0d);
        layout4.setAlignment(Pos.CENTER_LEFT);
        layout4.getChildren().addAll(
                l5, l6, 
                time_axis_title, time_axis_prefix,
                value_axis_title, value_axis_prefix
        );
        axis_titles.setContent(layout4);
        
        this.getChildren().addAll(
                axis_titles,
                new Separator()
        );
        
        GridPane layout6 = new GridPane();
        GridPane.setConstraints(show_grid, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(show_legend, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        layout6.setHgap(5.0d);
        layout6.setVgap(5.0d);
        layout6.setAlignment(Pos.CENTER_LEFT);
        layout6.getChildren().addAll(
                show_grid, show_legend
        );
        
        this.getChildren().add(layout6);
        
    }
    
    private void bindAllValues(){
        value_axis.disableProperty().bind(value_axis_check.selectedProperty());
        value_axis.expandedProperty().bind(value_axis_check.selectedProperty().not());
        
        time_axis.disableProperty().bind(time_axis_check.selectedProperty());
        time_axis.expandedProperty().bind(time_axis_check.selectedProperty().not());
    }
}
