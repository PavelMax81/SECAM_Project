/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author mdd
 */
public class RecCAListView extends Stage{
    
    BorderPane root;
    Scene scene;
    
    Label top_label;
    
    Button next, back;
    
    ListView<String> list_view;
    
    List<String> jocl_stresses, jocl_res;
    
    List<String> selected_parameters = new ArrayList<>();
    List<String> selected_time_steps = new ArrayList<>(); 
    
    public RecCAListView(){
        initAllElements();
        scene = new Scene(root);
        this.setTitle("Demo -- Boost Results Analization");
        this.setScene(scene);
        this.show();
    }
    
    private void initAllElements(){
        
        root = new BorderPane();
        
        top_label = new Label();
        
        HBox top_layout = new HBox();
        top_layout.setAlignment(Pos.CENTER);
        top_layout.setPadding(new Insets(15, 15, 15, 15));
        
        top_layout.getChildren().add(top_label);
        
        root.setTop(top_layout);
        
        list_view = new ListView();
        list_view.setPadding(new Insets(0, 15, 15, 15));
        list_view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        root.setCenter(list_view);
        
        next = new Button("Next");
        next.setPadding(new Insets(5, 20, 5, 20));
        
        back = new Button("Back");
        back.setPadding(new Insets(5, 20, 5, 20));
        
        HBox bottom_layout = new HBox();
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setPadding(new Insets(0, 5, 0, 5));
        
        bottom_layout.setAlignment(Pos.CENTER);
        bottom_layout.setPadding(new Insets(15, 15, 15, 15));
        
        bottom_layout.getChildren().addAll(back, sep, next);
        
        root.setBottom(bottom_layout);
        
        initParametersListView();
    }
    
    private void initParametersListView(){
        
        top_label.setText("Select Parameters");
        top_label.setStyle("-fx-font-size:25px");
        
        if(list_view.getItems().isEmpty())
            list_view.getItems().addAll(getParametersList("jocl"));
        else{
            list_view.getItems().clear();
            list_view.getItems().addAll(getParametersList("jocl"));
        }
        
        back.setDisable(true);
        
        next.setOnAction(e -> {
            if(list_view.getSelectionModel().isEmpty())
                new Alert(AlertType.ERROR, "You should select at least one parameter!").showAndWait();
            else{
                selected_parameters = list_view.getSelectionModel().getSelectedItems();
                selected_parameters.forEach(item -> System.out.println("Selected Parameter: " + item));
                System.out.println("Is One Type : " + isOneTypeOfFilesToRead());
                initTimeStepsListView();
                back.setDisable(false);
            }
        });
    }
    
    private List<String> getParametersList(String data_type){
        
        List<String> list = new ArrayList<>();
        
        switch(data_type){
            case "jocl":
                list.add("Temperature");
                list.add("Specific Elastic Energy");
                list.add("Value for Crack Generation");
                list.add("X Component of Specific Volume Force Moment Vector");
                list.add("Y Component of Specific Volume Force Moment Vector");
                list.add("Z Component of Specific Volume Force Moment Vector");
                list.add("Current Influx of Specific Dissipated Energy");
                list.add("Specific Dissipated Energy");
                list.add("Absolute Value of Stress Vector");
                list.add("Change of Density of Defects");
                list.add("X Component of Stress Vector");
                list.add("Y Component of Stress Vector");
                list.add("Z Component of Stress Vector");
                break;
        }
        
        return list;
    }
    
    private void initTimeStepsListView(){
        top_label.setText("Select Time Steps");
        
        list_view.getItems().clear();
        
        list_view.getItems().addAll(getTimeStepList());
        
        next.setOnAction(e -> {
            if(list_view.getSelectionModel().isEmpty())
                new Alert(AlertType.ERROR, "You should select at least one time step!").showAndWait();
            else{
                selected_time_steps = list_view.getSelectionModel().getSelectedItems();
                selected_time_steps.forEach(item -> System.out.println("Selected Time Step: " + item));
                initGeneralParametersLayout();
                top_label.setText("");
            }
        });
        
        back.setOnAction(e -> {
            initParametersListView();
        });
    }
    
    private List<String> getTimeStepList(){
        List<String> time_steps = new ArrayList<>();
        
        for(int i = 0 ; i < 100 ; i++){
            time_steps.add(String.valueOf(i * 100));
        }
        return time_steps;
    }
    
    ListView<String> selected_params_box;
    ComboBox selected_time_steps_box;
    TextField max_value_txt_fld, min_value_txt_fld;
    Button show;
    
    private void initGeneralParametersLayout(){
        
        selected_params_box = new ListView();
        selected_params_box.getItems().addAll(selected_parameters);
        
        selected_time_steps_box = new ComboBox();
        selected_time_steps_box.getItems().addAll(selected_time_steps);
        
        max_value_txt_fld = new TextField();
        max_value_txt_fld.setText("Write here recomended max value for selected in the combobox parameter");
        
        min_value_txt_fld = new TextField();
        min_value_txt_fld.setText("min value goes here");
        
        HBox layout = new HBox();
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setAlignment(Pos.CENTER);
        
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setPadding(new Insets(2, 7, 2, 7));
        
        GridPane right_layout = new GridPane();
        right_layout.setAlignment(Pos.CENTER);
        right_layout.setHgap(5.0);
        right_layout.setVgap(5.0);
        right_layout.setPadding(new Insets(10, 10, 10, 10));
        
        right_layout.add(new Label("Max Value : "), 0, 0);
        right_layout.add(max_value_txt_fld, 1, 0);
        right_layout.add(new Label("Min Value : "), 0, 1);
        right_layout.add(min_value_txt_fld, 1, 1);
        
        layout.getChildren().addAll(selected_params_box, sep, right_layout);
        
        root.setCenter(layout);
        
        back.setOnAction(e -> {
            initTimeStepsListView();
        });
        
        next.setOnAction(e -> {});
        
        selected_params_box.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            // Implement code for changing the value in the max_value and min_value text fields
            System.out.println("Selected Parameter : " + newValue);
        });
    }
    
    
    
    Map<String, Double> max_values = new HashMap<>();
    Map<String, Double> min_values = new HashMap<>();
    
    /**
     * Method for computing average value of max and min values
     * according to selected parameters and time steps
     * @param task_name
     */
    private void getValuesForScale(){
        List<String> files_to_read = new ArrayList<>();
    }
    
    private String test(){
        
        return "";
    }
    
    // NOT WORKING !!!!!!
    private boolean isOneTypeOfFilesToRead(){
        
        List<String> param1 = this.getJoclResParams();
        List<String> param2 = this.getJoclStressesParams();
        
        for(String item : selected_parameters){
            if(!param2.contains(item))
                return false;
            else
                return true;
        }
        return false;
    }
    
    private List<String> getJoclResFilesToRead(String task_name){
        List<String> list = new ArrayList<>();
        return list;
    }
    
    private List<String> getJoclStressesFilesToRead(String task_name){
        List<String> list = new ArrayList<>();
        
        return list;
    }
    
    private List<String> getJoclResParams(){
        List<String> list = new ArrayList<>();
        list.add("Temperature");
        list.add("Specific Elastic Energy");
        list.add("Value for Crack Generation");
        list.add("X Component of Specific Volume Force Moment Vector");
        list.add("Y Component of Specific Volume Force Moment Vector");
        list.add("Z Component of Specific Volume Force Moment Vector");
        list.add("Current Influx of Specific Dissipated Energy");
        list.add("Specific Dissipated Energy");
        return list;
    }
    
    private List<String> getJoclStressesParams(){
        List<String> list = new ArrayList<>();
        list.add("Absolute Value of Stress Vector");
        list.add("Change of Density of Defects");
        list.add("X Component of Stress Vector");
        list.add("Y Component of Stress Vector");
        list.add("Z Component of Stress Vector");
        return list;
    }
}
