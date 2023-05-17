/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author dmitryb
 */
public class RecCACameraControlLayout extends VBox{
    
    Slider rotate_x, rotate_y, rotate_z, zoom, translate_x, translate_y;
    TitledPane rotate, translate;
    
    TextField rotate_x_txt_fld, rotate_y_txt_fld, rotate_z_txt_fld, zoom_txt_fld, translate_x_txt_fld, translate_y_txt_fld;
    Label rotate_x_lbl, rotate_y_lbl, rotate_z_lbl, zoom_lbl, translate_x_lbl, translate_y_lbl;
    
    RecCA_3DPictureCreator picture;
    
    public RecCACameraControlLayout(RecCA_3DPictureCreator pict){
        
        System.out.println("RecCACameraControlLayout constructor start");
        
        this.picture = pict;
        
        this.rotate = new TitledPane();
        rotate.setText("Rotate");
        
        this.translate = new TitledPane();
        translate.setText("Translate");
        
        this.rotate_x = new Slider(-180.0d, 180.0d, 0.0d);
        this.rotate_y = new Slider(-180.0d, 180.0d, 0.0d);
        this.rotate_z = new Slider(-180.0d, 180.0d, 0.0d);
        this.zoom = new Slider(-1000.0d, 0.0d, -500.d);
        this.translate_x = new Slider(-1000.0d, 1000.0d, 0.0d);
        this.translate_y = new Slider(-1000.0d, 1000.0d, 0.0d);
        
        rotate_x.setMaxWidth(250.0d);
        rotate_y.setMaxWidth(250.0d);
        rotate_z.setMaxWidth(250.0d);
        zoom.setMaxWidth(250.0d);
        translate_x.setMaxWidth(250.0d);
        translate_y.setMaxWidth(250.0d);
        
        this.rotate_x_txt_fld = new TextField();
        rotate_x_txt_fld.setMaxWidth(50.0d);
        this.rotate_y_txt_fld = new TextField();
        rotate_y_txt_fld.setMaxWidth(50.0d);
        this.rotate_z_txt_fld = new TextField();
        rotate_z_txt_fld.setMaxWidth(50.0d);
        this.zoom_txt_fld = new TextField();
        zoom_txt_fld.setMaxWidth(50.0d);
        this.translate_x_txt_fld = new TextField();
        translate_x_txt_fld.setMaxWidth(50.0d);
        this.translate_y_txt_fld = new TextField();
        translate_y_txt_fld.setMaxWidth(50.0d);
        
        this.rotate_x_lbl = new Label();
        this.rotate_y_lbl = new Label();
        this.rotate_z_lbl = new Label();
        this.zoom_lbl = new Label();
        this.translate_x_lbl = new Label();
        this.translate_y_lbl = new Label();
        
        GridPane rotate_layout = new GridPane();
        rotate_layout.setAlignment(Pos.CENTER);
        rotate_layout.setVgap(5.0d);
        rotate_layout.setHgap(5.0d);
        
        Label l1 = new Label("X:");
        Label l2 = new Label("Y:");
        Label l3 = new Label("Z:");
        Label l4 = new Label("Zoom:");
        Separator sep = new Separator();
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(rotate_x, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_x_txt_fld, 3, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_x_lbl, 1, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l2, 0, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(rotate_y, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_y_txt_fld, 3, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_y_lbl, 1, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l3, 0, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(rotate_z, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_z_txt_fld, 3, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(rotate_z_lbl, 1, 5, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep, 0, 6, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l4, 0, 7, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(zoom, 1, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(zoom_txt_fld, 3, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(zoom_lbl, 1, 8, 2, 1, HPos.CENTER, VPos.CENTER);
        
        rotate_layout.getChildren().addAll(
                l1, l2, l3, l4, sep,
                rotate_x, rotate_y, rotate_z, zoom,
                rotate_x_txt_fld, rotate_y_txt_fld, rotate_z_txt_fld, zoom_txt_fld,
                rotate_x_lbl, rotate_y_lbl, rotate_z_lbl, zoom_lbl
        );
        
        rotate.setContent(rotate_layout);
        
        
        GridPane translate_layout = new GridPane();
        translate_layout.setAlignment(Pos.CENTER);
        translate_layout.setVgap(5.0d);
        translate_layout.setHgap(5.0d);
        
        Label l5 = new Label("X:");
        Label l6 = new Label("Y:");
        
        GridPane.setConstraints(l5, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(translate_x, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(translate_x_txt_fld, 3, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(translate_x_lbl, 1, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l6, 0, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(translate_y, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(translate_y_txt_fld, 3, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(translate_y_lbl, 1, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        
        translate_layout.getChildren().addAll(
                l5, l6, translate_x, translate_y, 
                translate_x_txt_fld, translate_y_txt_fld,
                translate_x_lbl, translate_y_lbl
        );
        translate.setContent(translate_layout);
        translate.setExpanded(false);
        
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(
                rotate,
                translate                
        );
        this.setPadding(new Insets(5, 5, 5, 5));
        handleEvents();
    }
        
    
    public void handleEvents(){
        
        rotate_x.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> rotate_x_lbl.setText(String.format("%.2f", newValue)));
        rotate_x.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> rotate_x_txt_fld.setPromptText(String.format("%.2f", newValue)));
        
        rotate_y.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> rotate_y_lbl.setText(String.format("%.2f", newValue)));
        rotate_y.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> rotate_y_txt_fld.setPromptText(String.format("%.2f", newValue)));
        
        rotate_z.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> rotate_z_lbl.setText(String.format("%.2f", newValue)));
        rotate_z.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> rotate_z_txt_fld.setPromptText(String.format("%.2f", newValue)));
        
        zoom.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> zoom_lbl.setText(String.format("%.2f", newValue)));
        zoom.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> zoom_txt_fld.setPromptText(String.format("%.2f", newValue)));
        
        translate_x.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> translate_x_lbl.setText(String.format("%.2f", newValue)));
        translate_x.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> translate_x_txt_fld.setPromptText(String.format("%.2f", newValue)));
        
        translate_y.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> translate_y_lbl.setText(String.format("%.2f", newValue)));
        translate_y.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> translate_y_txt_fld.setPromptText(String.format("%.2f", newValue)));
        
        rotate_x.valueProperty().bindBidirectional(picture.cameraXform.rx.angleProperty());
        rotate_y.valueProperty().bindBidirectional(picture.cameraXform.ry.angleProperty());
        rotate_z.valueProperty().bindBidirectional(picture.cameraXform.rz.angleProperty());
        zoom.valueProperty().bindBidirectional(picture.camera.translateZProperty());
        translate_x.valueProperty().bindBidirectional(picture.camera.translateXProperty());
        translate_y.valueProperty().bindBidirectional(picture.camera.translateYProperty());
        
        rotate_x_txt_fld.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== ENTER){
                    try{
                        picture.cameraXform.rx.setAngle(Double.parseDouble(rotate_x_txt_fld.getText()));
                    }
                    catch(NumberFormatException ex){
                        new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
                    }
                }
            }
        });
        
        rotate_y_txt_fld.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== ENTER){
                    try{
                        picture.cameraXform.ry.setAngle(Double.parseDouble(rotate_y_txt_fld.getText()));
                    }
                    catch(NumberFormatException ex){
                        new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
                    }
                }
            }
        });
        
        rotate_z_txt_fld.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== ENTER){
                    try{
                        picture.cameraXform.rz.setAngle(Double.parseDouble(rotate_z_txt_fld.getText()));
                    }
                    catch(NumberFormatException ex){
                        new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
                    }
                }
            }
        });
        
        zoom_txt_fld.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== ENTER){
                    try{
                        picture.camera.setTranslateZ(Double.parseDouble(zoom_txt_fld.getText()));
                    }
                    catch(NumberFormatException ex){
                        new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
                    }
                }
            }
        });
        
        translate_x_txt_fld.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== ENTER){
                    try{
                        picture.camera.setTranslateX(Double.parseDouble(translate_x_txt_fld.getText()));
                    }
                    catch(NumberFormatException ex){
                        new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
                    }
                }
            }
        });
        
        translate_y_txt_fld.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== ENTER){
                    try{
                        picture.camera.setTranslateY(Double.parseDouble(translate_y_txt_fld.getText()));
                    }
                    catch(NumberFormatException ex){
                        new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
                    }
                }
            }
        });                
    }
}
