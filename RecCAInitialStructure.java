/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Screen;

/**
 *
 * @author Ear
 */
public class RecCAInitialStructure extends BorderPane{
    
    TransferredDataBank data_bank;
    RecCA_3DPictureCreator pict;
    SubScene sub_scene;
    ScrollPane scroll_pane;
    
    /**
     * ToolBar Elements
     */
    ToolBar tool_bar;
    Slider slider_x, slider_y, slider_z, zoom;
    TitledPane sliders_tp, info_tp;
    double number_of_inner_cells, size_x, size_y, size_z, number_of_grains;
    
    double width = Screen.getPrimary().getVisualBounds().getWidth();
    double height = Screen.getPrimary().getVisualBounds().getHeight();
    
    public RecCAInitialStructure(TransferredDataBank receive_data_bank) {
        System.out.println("RecCAInitialStructure constructor: start");
        this.data_bank = receive_data_bank;
        
        this.pict = new RecCA_3DPictureCreator(data_bank);
        this.sub_scene = new SubScene(pict.world, width * 0.8 , height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);
        
        handle3DArea(sub_scene, pict);
        
        try {
            readFiles();
        } catch (IOException ex) {
            System.out.println("Error: RecCAInitialStructure readFiles() method");
        }        
        showInitialStructure();
        
        scroll_pane = new ScrollPane();
        scroll_pane.setContent(sub_scene);
        this.setCenter(scroll_pane);
        
        /**
         * Initialize All ToolBar Elements
         */
        tool_bar = new ToolBar();
        tool_bar.setOrientation(Orientation.VERTICAL);
        
        sliders_tp = new TitledPane();
        sliders_tp.setText("Sliders");
        sliders_tp.setAlignment(Pos.CENTER);
        sliders_tp.setPadding(new Insets(5, 5, 5, 5));
        
        Label x_lbl = new Label("Rotate X:");
        Label y_lbl = new Label("Rotate Y:");
        Label z_lbl = new Label("Rotate Z:");
        Label zoom_lbl = new Label("Zoom:");
        
        slider_x = new Slider(-180.0d, 180.0d, 0.0d);
        slider_x.setTooltip(new Tooltip("drag to rotate the specimen around X Axis"));
        
        slider_y = new Slider(-180.0d, 180.0d, 0.0d);
        slider_y.setTooltip(new Tooltip("drag to rotate the specimen around Y Axis"));
        
        slider_z = new Slider(-180.0d, 180.0d, 0.0d);
        slider_z.setTooltip(new Tooltip("drag to rotate the specimen around Z Axis"));
        
        zoom = new Slider(-500.0d, 500.0d, 0.0d);
        zoom.setTooltip(new Tooltip("drag to change the distance relatively to the specimen"));
        
        GridPane sliders = new GridPane();
        sliders.setAlignment(Pos.CENTER_LEFT);
        sliders.setPadding(new Insets(5, 5, 5, 5));
        sliders.setHgap(7.0d);
        sliders.setVgap(5.0d);
        
        GridPane.setConstraints(x_lbl, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(y_lbl, 0, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(z_lbl, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(zoom_lbl, 0, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(slider_x, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(slider_y, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(slider_z, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(zoom, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        
        sliders.getChildren().addAll(
                x_lbl, y_lbl, z_lbl, zoom_lbl,
                slider_x,
                slider_y,
                slider_z,
                zoom
        );
        
        slider_x.valueProperty().bindBidirectional(pict.cameraXform.rx.angleProperty());
        slider_y.valueProperty().bindBidirectional(pict.cameraXform.ry.angleProperty());
        slider_z.valueProperty().bindBidirectional(pict.cameraXform.rz.angleProperty());
        zoom.valueProperty().bindBidirectional(pict.camera.translateZProperty());
        
        sliders_tp.setContent(sliders);
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(7, 2, 7, 2));
        
        info_tp = new TitledPane();
        info_tp.setText("Initial Structure Info");
        info_tp.setAlignment(Pos.CENTER);
        info_tp.setPadding(new Insets(5, 5, 5, 5));
        
        GridPane info_layout = new GridPane();
        info_layout.setAlignment(Pos.CENTER_LEFT);
        info_layout.setPadding(new Insets(5, 5, 5, 5));
        info_layout.setHgap(7.0d);
        info_layout.setVgap(5.0d);
        
        Label l1 = new Label("Total Number of Inner Cells");
        Label l2 = new Label("Specimen Size Along X");
        Label l3 = new Label("Specimen Size Along Y");
        Label l4 = new Label("Specimen Size Along Z");
        Label l5 = new Label("Number of Grains");
        
        Label val1 = new Label(String.valueOf(number_of_inner_cells));
        Label val2 = new Label(String.valueOf(size_x));
        Label val3= new Label(String.valueOf(size_y));
        Label val4 = new Label(String.valueOf(size_z));
        Label val5 = new Label(String.valueOf(number_of_grains));
        
        GridPane.setConstraints(l1, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l2, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l3, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l4, 0, 3, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l5, 0, 4, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(val1, 2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(val2, 2, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(val3, 2, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(val4, 2, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(val5, 2, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        info_layout.getChildren().addAll(
                l1, l2, l3, l4, l5,
                val1, val2, val3, val4, val5
        );
        
        info_tp.setContent(info_layout);
        
        tool_bar.getItems().addAll(
                sliders_tp,
                sep,
                info_tp
        );
        
        this.setRight(tool_bar);
    }
    
    public void showInitialStructure(){
        
        for(int i=0 ; i < x_coordinate.size(); i ++){
            
            Sphere cell = new Sphere(1);
            
            cell.setTranslateX(x_coordinate.get(i));
            cell.setTranslateY(y_coordinate.get(i));
            cell.setTranslateZ(z_coordinate.get(i));
            
            cell.setCache(true);
            cell.setCacheHint(CacheHint.DEFAULT);
            
            for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                if(Objects.equals(grain_index.get(i), grain_index_clrs.get(j))){
                    setColorToCell(cell, r_color.get(color_number.get(j)),
                                        g_color.get(color_number.get(j)),
                                        b_color.get(color_number.get(j)));
                    break;
                }
            }
            
            pict.elementsGroup.getChildren().addAll(cell);
        }        
        pict.world.getChildren().add(pict.elementsGroup);
    }
    
    public List<Integer> grain_index = new ArrayList<>();
    public List<Double> x_coordinate = new ArrayList<>();
    public List<Double> y_coordinate = new ArrayList<>();
    public List<Double> z_coordinate = new ArrayList<>();
    
    public List<Integer> r_color = new ArrayList<>();
    public List<Integer> g_color = new ArrayList<>();
    public List<Integer> b_color = new ArrayList<>();
    public List<Integer> color_number = new ArrayList<>();
    public List<Integer> grain_index_clrs = new ArrayList<>();
    
    public void readFiles() throws FileNotFoundException, IOException{
        String spec = data_bank.getUIInterface().getSpecimenPath();
        
        String clrs_path = "./user/spec_db/" + spec + "/init_cond_db/grain_colours.clrs";
        String str_path = "./user/spec_db/" + spec + "/init_cond_db/" + spec + ".str";
        
        BufferedReader br1 = new BufferedReader(new FileReader(clrs_path));
        System.out.println("Read: " + clrs_path);
        BufferedReader br2 = new BufferedReader(new FileReader(str_path));
        System.out.println("Read: " + str_path);
        
        List<String> clrs_lines = br1.lines().collect(Collectors.toList());        
        List<String> str_lines = br2.lines().filter(line -> !line.startsWith("#")).collect(Collectors.toList());
        
        String[] data = str_lines.get(0).split(" ");
        
        number_of_inner_cells = Double.parseDouble(data[0]);
        size_x = Double.parseDouble(data[1]);
        size_y = Double.parseDouble(data[2]);
        size_z = Double.parseDouble(data[3]);
        number_of_grains = Double.parseDouble(data[4]);
        
        for(int i = 1 ; i < clrs_lines.size() ; i ++){
            String[] tokens = clrs_lines.get(i).split(" ");                
            if(tokens.length == 2){
                grain_index_clrs.add(Integer.parseInt(tokens[0]));
                color_number.add(Integer.parseInt(tokens[1]));
            }
            else if(tokens.length == 4){                    
                r_color.add(Integer.parseInt(tokens[1]));
                g_color.add(Integer.parseInt(tokens[2]));
                b_color.add(Integer.parseInt(tokens[3]));
            }
        }
        
        for(int i = 1 ; i < str_lines.size() ; i ++){
            String[] tokens = str_lines.get(i).split(" ");            
            grain_index.add(Integer.parseInt(tokens[0]));
            x_coordinate.add(Double.parseDouble(tokens[1]));
            y_coordinate.add(Double.parseDouble(tokens[2]));
            z_coordinate.add(Double.parseDouble(tokens[3]));
        }
        
        br1.close();
        br2.close();
        System.out.println("Files Read");
    }
    
    public void setColorToCell(Sphere cell , int rColor , int gColor, int bColor){
        cell.setMaterial(new PhongMaterial(Color.rgb(rColor, gColor, bColor)));
    }
    
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    
    public void handle3DArea(SubScene sub_scene, RecCA_3DPictureCreator picture){
        sub_scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        
        sub_scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();

            mouseDeltaX = (mousePosX - mouseOldX); 
            mouseDeltaY = (mousePosY - mouseOldY); 
            
            if(me.isPrimaryButtonDown()){
                picture.cameraXform.ry.setAngle(picture.cameraXform.ry.getAngle() - mouseDeltaX*UICommon.MOUSE_SPEED*UICommon.ROTATION_SPEED);  
                picture.cameraXform.rx.setAngle(picture.cameraXform.rx.getAngle() + mouseDeltaY*UICommon.MOUSE_SPEED*UICommon.ROTATION_SPEED);
            }
            
            if(me.isSecondaryButtonDown()){                
                picture.camera.setTranslateX(picture.camera.getTranslateX() - mouseDeltaX*UICommon.MOUSE_SPEED*UICommon.TRACK_SPEED);
                picture.camera.setTranslateY(picture.camera.getTranslateY() - mouseDeltaY*UICommon.MOUSE_SPEED*UICommon.TRACK_SPEED);
            }            
        });
        
        sub_scene.setOnScroll(se -> {
            double z = picture.camera.getTranslateZ();
            double newZ = z + se.getDeltaY()/2;
            picture.camera.setTranslateZ(newZ);      
        });
    }
    
}
