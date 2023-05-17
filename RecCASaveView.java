/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCASaveView extends Stage{
    
    TextField rotate_x, rotate_y, rotate_z, zoom, translate_x, translate_y;
    CheckBox show_axis;
    
    Button save, close;
    StringConverter conv;
    
    RecCA_3DPictureCreator picture;
    
    SavingDialog save_file;
    
    TextField file_name;
    
    SaveParams save_view_file;
    
    String results_folder;
    
    TransferredDataBank trans_data_bank;
    
    
    public RecCASaveView(TransferredDataBank receive_data_bank, RecCA_3DPictureCreator pict, String results){
        System.out.println("RecCASaveView constructor creation start");
        
        this.trans_data_bank = receive_data_bank;
        
        results_folder = results;
        
        this.picture = pict;
        
        file_name = new TextField();
        
        rotate_x = new TextField();
        rotate_y = new TextField();
        rotate_z = new TextField();
        zoom = new TextField();
        translate_x = new TextField();
        translate_y = new TextField();
        
        show_axis = new CheckBox("Show Axis");
        
        save = new Button("Save");
        close = new Button("Close");
        
        Label l1 = new Label("Rotate X:");
        Label l2 = new Label("Rotate Y:");
        Label l3 = new Label("Rotate Z:");
        Label l4 = new Label("Zoom:");
        Label l5 = new Label("Translate X:");
        Label l6 = new Label("Translate Y:");
        
        Separator sep = new Separator();
        
        GridPane layout = new GridPane();
        layout.setAlignment(Pos.CENTER);
        layout.setHgap(7.0d);
        layout.setVgap(7.0d);
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l6, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(rotate_x, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_y, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_z, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(zoom, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(translate_x, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(translate_y, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(show_axis, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(save, 0, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(close, 1, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        
        layout.getChildren().addAll(
                l1, l2, l3, l4, l5, l6,
                rotate_x, rotate_y, rotate_z,
                zoom, translate_x, translate_y,
                save, close, show_axis
        );
        
        this.setTitle("Save View");
        this.setScene(new Scene(layout));
        this.show();
        bindAllValues();
        handleEvents();        
    }
    
    public void bindAllValues(){
        System.out.println("RecCASaveView bindAllValues() method: start");
        conv = new DoubleStringConverter();
        rotate_x.textProperty().bindBidirectional(picture.cameraXform.rx.angleProperty(), conv);
        rotate_y.textProperty().bindBidirectional(picture.cameraXform.ry.angleProperty(), conv);
        rotate_z.textProperty().bindBidirectional(picture.cameraXform.rz.angleProperty(), conv);
        zoom.textProperty().bindBidirectional(picture.camera.translateZProperty(), conv);
        translate_x.textProperty().bindBidirectional(picture.camera.translateXProperty(), conv);
        translate_y.textProperty().bindBidirectional(picture.camera.translateYProperty(), conv);
        show_axis.selectedProperty().bindBidirectional(picture.axisGroup.visibleProperty());
    }
    
    public void handleEvents(){
        System.out.println("RecCASaveView handleEvents() method: start");
        save.setOnAction(e -> {
            System.out.println("Action Performed : button 'save' is pushed");
            save_file = new SavingDialog(file_name);
            save_file.save_btn.setOnAction(event -> {
                if(!file_name.getText().isEmpty()){
                    try {    
                        createViewFile(results_folder, file_name.getText());
                    } catch (IOException ex) {
                        System.out.println("Error: can't create file");
                    }
                    save_file.close();
                    new Alert(AlertType.INFORMATION, "File is Created!").showAndWait();
                }
                else{
                    new Alert(AlertType.ERROR, "Input file name!").show();
                }                
            });
        });
        close.setOnAction(e -> this.close());
    }
    
    public void createViewFile(String results_folder, String file_name) throws IOException{
        
        String file_path = Common.TASK_PATH + "/" + results_folder + "/" + file_name + ".view";
        
        File file = new File(file_path);
        if(!file.exists())
            file.createNewFile();
        System.out.println("Create File: " + file.getAbsolutePath());
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(                
                "rotate_x = " + rotate_x.getText() + "\n" + 
                "rotate_y = " + rotate_y.getText() + "\n" + 
                "rotate_z = " + rotate_z.getText() + "\n" + 
                "zoom = " + zoom.getText() + "\n" + 
                "translate_x = " + translate_x.getText() + "\n" + 
                "translate_y = " + translate_y.getText() + "\n" + 
                "show_axis = " + show_axis.isSelected()
        );
        bw.close();        
    }    
}
