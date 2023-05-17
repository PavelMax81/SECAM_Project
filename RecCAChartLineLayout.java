/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author DmitryB
 */
public class RecCAChartLineLayout extends GridPane{
    
    ComboBox results, files, params;
    RecCALinePreferences tools;
    
    public RecCAChartLineLayout(){
        initAllElements();
        loadResults();
        handleEvents();        
        
        this.setPadding(new Insets(5, 5, 5, 5));
    }
    
    public void initAllElements(){
        
        results = new ComboBox();
        files = new ComboBox();
        params = new ComboBox();
                
        Label l1 = new Label("Results:");
        Label l2 = new Label("Files:");
        Label l3 = new Label("Parameters");
        
        Separator hs1 = new Separator();
        Separator hs2 = new Separator();
        Separator hs3 = new Separator();
        
        tools = new RecCALinePreferences();
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(results, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(hs1, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l2, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(files, 1, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(hs2, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l3, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(params, 1, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(hs3, 0, 5, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(tools, 0, 6, 2, 1, HPos.LEFT, VPos.CENTER);
        
        setAlignment(Pos.CENTER_LEFT);
        setHgap(5.0);
        setVgap(5.0);
        getChildren().addAll(
                l1, l2, l3, hs1, hs2, hs3,
                results, files, params,
                tools
        );
    }
    
    public void loadResults(){
        List<String> list = new ArrayList<>();
        for (File f : new File("./user/task_db/").listFiles()){
            if(f.isDirectory() && !f.getName().equals("default_task")){
                list.add(f.getName());
            }
        }
        results.getItems().addAll(list);
    }
    
    RecCAChartFiles data;
    
    Stage stage;
    
    public void setStage(Stage stage){
        this.stage = stage;
    }
    
    public void handleEvents(){
        results.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            String res = newValue.toString();
            
            if(!files.getItems().isEmpty())
                files.getItems().clear();
            
            for(File f : new File("./user/task_db/" + res + "/").listFiles()){
                if(f.isFile() && f.getName().endsWith(".chart"))
                    files.getItems().add(f.getName());
            }
            
            if(files.getItems().isEmpty()){
                Alert alert = new Alert(AlertType.CONFIRMATION, "There are no chart files \nCreate Default Files for Chart?");
                alert.showAndWait();
                if(alert.getResult().equals(ButtonType.OK)){
                    File file = new File("./user/task_db/" + res + ".seca");
                    if(file.exists()){
                        data = new RecCAChartFiles(res, file);
                    }
                    else
                        new Alert(AlertType.ERROR, "Seca File Doesn't Exists!").showAndWait();
                }
                else
                    System.out.println("DON'T CREATE FILES");
            }
        });
        
        files.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            String res = results.getSelectionModel().getSelectedItem().toString();
            String file = newValue.toString();
            data = new RecCAChartFiles(res, file);
            
            if(!params.getItems().isEmpty())
                params.getItems().clear();
            
            if(file.endsWith("relValues.chart")){
                params.getItems().addAll(
                        "Temperature",
                        "Specific Elastic Energy",
                        "Specific Dissipated Energy",
                        "Abs. Value of X Inst. Spec. Moment",
                        "Abs. Value of Y Inst. Spec. Moment",
                        "Abs. Value of Z Inst. Spec. Moment",
                        "Abs. Value of Inst. Moment",
                        "Total Specific Energy"
                );
            }
            else if(file.endsWith("jocl_stresses.chart")){
                params.getItems().addAll(
                        "Temperature",
                        "Abs. Value of Stress Vector",
                        "Value for Crack Generation",
                        "X Comp. of Stress Vector",
                        "Y Comp. of Stress Vector",
                        "Z Comp. of Stress Vector",
                        "Current Influx Of Torsion Energy",
                        "Specific Dissipated Energy"
                );
            }
            else if(file.endsWith("jocl_torsion.chart")){
                params.getItems().addAll(
                        "X Comp. of Angle Velocity",
                        "Y Comp. of Angle Velocity",
                        "Z Comp. of Angle Velocity",
                        "X Comp. of Torsion Angle Vector",
                        "Y Comp. of Torsion Angle Vector",
                        "Z Comp. of Torsion Angle Vector",
                        "Abs. Angle Velocity of Torsion",
                        "Abs. Torsion Angle"
                );
            }
            else if(file.endsWith("jocl.chart")){
                params.getItems().addAll(
                        "Temperature",
                        "Specific Elastic Energy",
                        "Principal Stresses",
                        "X Comp. of Spec. Force Moment",
                        "Y Comp. of Spec. Force Moment",
                        "Z Comp. of Spec. Force Moment",
                        "Current Influx Of Torsion Energy",
                        "Specific Dissipated Energy"
                );
            }
            else if(file.endsWith("inst_mom.chart")){
                params.getItems().addAll(
                        "Abs. Value of X Inst. Spec. Moment",
                        "Abs. Value of Y Inst. Spec. Moment",
                        "Abs. Value of Z Inst. Spec. Moment",
                        "X Comp. of Inst. Spec. Moment",
                        "Y Comp. of Inst. Spec. Moment",
                        "Z Comp. of Inst. Spec. Moment",
                        "Abs. Value of Inst. Moment",
                        "Current Influx Of Torsion Energy"
                );
            }
            else if(file.endsWith("torsion.chart")){
                params.getItems().addAll(
                        "X Comp. of Angle Velocity",
                        "Y Comp. of Angle Velocity",
                        "Z Comp. of Angle Velocity",
                        "X Comp. of Torsion Angle Vector",
                        "Y Comp. of Torsion Angle Vector",
                        "Z Comp. of Torsion Angle Vector",
                        "Relative Power Of Torsion",
                        "Relative Torsion Energy",
                        "Total Specific Energy"
                );
            }
            else if(file.endsWith(".chart")){
                params.getItems().addAll(
                        "Temperature",
                        "Effective Stresses",
                        "Principal Stresses",
                        "X Comp. of Spec. Force Moment",
                        "Y Comp. of Spec. Force Moment",
                        "Z Comp. of Spec. Force Moment",
                        "Strain",
                        "Total Spec.Torsion Energy"
                );
            }
            else{
                System.out.println("ERROR: incorrect file_name");
            }
                
        });
    }
    
    public void writeNewColorToCss(String hex, int line_number) throws IOException{
        
        String path = null;
        String default_color = null;
        
        switch(line_number){
            case 1:
                path = "./interf/charts/css/line1_color.css";
                default_color = ".default-color0";
                break;
            case 2:
                path = "./interf/charts/css/line2_color.css";
                default_color = ".default-color1";
                break;
            case 3:
                path = "./interf/charts/css/line3_color.css";
                default_color = ".default-color2";
                break;
            case 4:
                path = "./interf/charts/css/line4_color.css";
                default_color = ".default-color3";
                break;
            case 5:
                path = "./interf/charts/css/line5_color.css";
                default_color = ".default-color4";
                break;                
        }
        
        File file = new File(path);
        if(file.exists()){
            file.delete();
            file.createNewFile();
        }
        else{
            file.createNewFile();
        }
            
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        
        /*
        
        */
        
        bw.write(
                default_color + ".chart-series-line { \n" +
                "    -fx-stroke: "+ hex +"; \n" +
                "}"
        );
        bw.newLine();
        bw.write(
                default_color + ".chart-line-symbol {\n" +
                "    -fx-background-color: "+ hex+ ", white; \n" +
                "}"
        );
        
        bw.close();
    }
}
