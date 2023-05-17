/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Common;

/**
 *
 * @author Ear
 */
public class RecCACreateChartFile extends Stage{
    
    ComboBox results;
    
    BorderPane root;
    Scene scene;
    
    /**
     * Stage Elements
     */
    Button create, close;
        
    Label x_max, y_max, z_max;
    TextField x_initial, y_initial, z_initial,
                  x_length, y_length, z_length,
                  file_name;
    
    RadioButton meters, diameters;
    
    public RecCACreateChartFile(){
        System.out.println("RecCACreateChartFile constructor: start");
        
        root = new BorderPane();
        initAllElements();
        addAllElements();
        handleEvents();
        scene = new Scene(root);
        centerOnScreen();
        setTitle("Create Chart Files");
        setScene(scene);
        show();
    }
    
    public void initAllElements(){
        
        System.out.println("RecCACreateChartFile method initAllElements(): start");
        
        results = new ComboBox();
        results.getItems().addAll(loadResults());
        
        x_initial = new TextField();
        y_initial = new TextField();
        z_initial = new TextField();
        x_length = new TextField();
        y_length = new TextField();
        z_length = new TextField();
        
        meters = new RadioButton("m.");
        diameters = new RadioButton("diam.");
        
        x_max = new Label();
        y_max = new Label();
        z_max = new Label();
        
        x_max.setStyle("-fx-font-weight: bold");
        y_max.setStyle("-fx-font-weight: bold");
        z_max.setStyle("-fx-font-weight: bold");
        
        results.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            
            try {
                getMaxCoordinateValues(newValue.toString());
                getCellSize(newValue.toString());
            } catch (IOException ex) {
                System.out.println("Error: getMaxCoordinateValues()");
            }
            
            x_max.setText("X(max) = " + x_max_val);
            y_max.setText("Y(max) = " + y_max_val);
            z_max.setText("Z(max) = " + z_max_val);
            
        });
        
        diameters.setSelected(true);
        
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(meters, diameters);
        
        file_name = new TextField();
        file_name.setPromptText("Type File Name");
        
        create = new Button("Create File");
        close = new Button("Close");
                
    }
    
    public void addAllElements(){
        System.out.println("RecCACreateChartFile method addAllElements(): start");
        
        Label l1 =new Label("X Initial:");
        Label l12 = new Label("X Length:");
        Label l2 =new Label("Y Initial:");
        Label l22 = new Label("Y Length:");
        Label l3 =new Label("Z Initial:");
        Label l32 = new Label("Z Length:");
        
        Label sel_measure = new Label("Select unit:");
        
        Label l4 = new Label("File Name:");
        
        Separator sep1 = new Separator();
        Separator sep2 = new Separator();
        Separator sep12 = new Separator();
        
        GridPane layout = new GridPane();
        layout.setHgap(7.0d);
        layout.setVgap(7.0d);
        layout.setAlignment(Pos.CENTER);
        
        HBox radio_buttons = new HBox(meters, diameters);
        
        GridPane.setConstraints(sel_measure, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(radio_buttons, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(l1, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(x_initial, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l12, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(x_length, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(x_max, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 4, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l2, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(y_initial, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l22, 0, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(y_length, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(y_max, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l3, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(z_initial, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l32, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(z_length, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(z_max, 0, 11, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep12, 0, 12, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l4, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(file_name, 1, 13, 1, 1, HPos.CENTER, VPos.CENTER);
                
        layout.getChildren().addAll(
                sep1, sep2, l1, l2, l3, l12, l22, l32, 
                sel_measure, radio_buttons,
                x_initial, x_length,
                y_initial, y_length,
                z_initial, z_length,
                sep12, l4, file_name,
                x_max, y_max, z_max
        );
        
        HBox buttons_layout = new HBox();
        buttons_layout.setPadding(new Insets(5, 5, 5, 5));
        buttons_layout.setAlignment(Pos.CENTER);
        
        close.setPadding(new Insets(5,30,5,30));
        create.setPadding(new Insets(5,30,5,30));
        
        Separator sep3 = new Separator(Orientation.VERTICAL);
        sep3.setPadding(new Insets(0, 5, 0, 5));
        
        buttons_layout.getChildren().addAll(create, sep3, close);
                
        Separator sep4 = new Separator();
        sep4.setPadding(new Insets(10, 5, 10, 5));
        
        root.setCenter(new VBox(new HBox(new Label("Select Results: "),results), layout, sep4, buttons_layout));        
    }
    
    public void handleEvents(){
        System.out.println("RecCACreateChartFile method handleEvents(): start");
        
        close.setOnAction(e -> close());
        
        create.setOnAction(e -> {
            
            if(x_initial.getText().isEmpty() || 
               x_length.getText().isEmpty() || 
               y_initial.getText().isEmpty() || 
               y_length.getText().isEmpty() || 
               z_initial.getText().isEmpty() || 
               z_length.getText().isEmpty() || 
               file_name.getText().isEmpty())
            {
                new Alert(AlertType.ERROR, "Please, input all values!").showAndWait();
            }
            else{
                
                double x_init = Double.parseDouble(x_initial.getText());
                double x_max = Double.parseDouble(x_length.getText());
                double y_init = Double.parseDouble(y_initial.getText());
                double y_max = Double.parseDouble(y_length.getText());
                double z_init = Double.parseDouble(z_initial.getText());
                double z_max = Double.parseDouble(z_length.getText());
                
                if(meters.isSelected()){
                    x_init = x_init / cell_size;
                    x_max = x_max / cell_size;
                    y_init = y_init / cell_size;
                    y_max = y_max / cell_size;
                    z_init = z_init / cell_size;
                    z_max = z_max / cell_size;
                }
                
                String task = null;
                
                if(!results.getSelectionModel().isEmpty())
                    task = results.getSelectionModel().getSelectedItem().toString();
                else
                    new Alert(AlertType.ERROR, "Select Results!").showAndWait();
                
                String name = file_name.getText();
                
                try {
                    RecCASpecialChartFiles files = new RecCASpecialChartFiles(task, name, x_init, y_init, z_init, x_max, y_max, z_max);
                } catch (IOException ex) {
                    System.out.println("Error: can't create special chart file");
                    new Alert(AlertType.ERROR, "Failed to create files").showAndWait();
                }
                new Alert(AlertType.INFORMATION, "Files are Created").showAndWait();
            }
        });
        
        meters.setOnAction(e -> {
            if(meters.isSelected()){
                x_max.setText("X(max) = " + x_max_val * cell_size);
                y_max.setText("Y(max) = " + y_max_val * cell_size);
                z_max.setText("Z(max) = " + z_max_val * cell_size);
            }            
        });
        
        diameters.setOnAction(e -> {
            if(diameters.isSelected()){
                x_max.setText("X(max) = " + x_max_val);
                y_max.setText("Y(max) = " + y_max_val);
                z_max.setText("Z(max) = " + z_max_val);
            }            
        });
    }
    
    double x_max_val, y_max_val, z_max_val;
    
    public void getMaxCoordinateValues(String task) throws IOException{
        Path dir = Paths.get(getSomeResFile(task));
        
        List<Double> x_coord = new ArrayList<>();
        List<Double> y_coord = new ArrayList<>();
        List<Double> z_coord = new ArrayList<>();
        
        for(String line : Files.readAllLines(dir)){
            if(!line.startsWith("#") && !line.equals(null)){
                String[] items = line.split(" ");
                x_coord.add(Double.parseDouble(items[3]));
                y_coord.add(Double.parseDouble(items[4]));
                z_coord.add(Double.parseDouble(items[5]));
            }
        }
        
        x_max_val = Collections.max(x_coord);
        y_max_val = Collections.max(y_coord);
        z_max_val = Collections.max(z_coord);
    }
        
    public String getSomeResFile(String task){
        String res = null;        
        for(File f : new File(Common.TASK_PATH + "/" + task + "/").listFiles()){
            if(f.getAbsolutePath().endsWith("res")){
                res = f.getAbsolutePath();
                break;
            }                
        }        
        return res;
    }
    
    double cell_size;
    
    public void getCellSize(String task) throws IOException{
        
        String seca_path = null;
        
        for(File f : new File(Common.TASK_PATH + "/" + task + "/").listFiles()){
            if(f.getName().endsWith(".seca")){
                seca_path = f.getAbsolutePath();
                break;
            }
        }
        
        System.out.println("SECA PATH : " + seca_path);
        
        Properties properties = new Properties();
        FileInputStream stream = new FileInputStream(seca_path);
        properties.load(stream);
        stream.close();
        
        cell_size = Double.valueOf(properties.getProperty("cell_size")).doubleValue();
        
        System.out.println("CELL SIZE : " + cell_size);
    }
    
    private List<String> loadResults(){
        List<String> list = new ArrayList<>();
        for(File f : new File("./user/task_db/").listFiles()){
            if(f.isDirectory() && !f.getName().equals("default_task"))
                list.add(f.getName());
        }
        return list;
    }
}
