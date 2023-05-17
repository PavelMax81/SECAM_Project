/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.stage.Screen;

/**
 *
 * @author Ear
 */
public class RecCAScheme extends BorderPane{
    
    RecCA_3DPictureCreator pict;
    
    TransferredDataBank data_bank;
    
    SubScene sub_scene;
    
    ScrollPane scroll_pane;
    
    Label top_facet_temp, top_facet_mech, 
             left_facet_temp, left_facet_mech,
             right_facet_temp, right_facet_mech,
             bottom_facet_temp, bottom_facet_mech,
             front_facet_temp, front_facet_mech,
             back_facet_temp, back_facet_mech;
    
    /**
     * Right Tool Bar Elements
     */
    ToolBar tool_bar;
    
    TitledPane sliders_tp;
    Slider slider_x, slider_y, slider_z, zoom;
    
    double width = Screen.getPrimary().getVisualBounds().getWidth();
    double height = Screen.getPrimary().getVisualBounds().getHeight();
    
    public RecCAScheme(TransferredDataBank receive_data_bank) {
        System.out.println("RecCAScheme constructor: start");
        
        this.data_bank = receive_data_bank;
        
        this.pict = new RecCA_3DPictureCreator(data_bank);
        
        pict.camera.setRotate(90.0);
        
        this.sub_scene = new SubScene(pict.world, width*0.8, height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);
        
        tool_bar = new ToolBar();
        tool_bar.setOrientation(Orientation.VERTICAL);
        initToolBar();
        showScheme();
        initBoundCondLabels();
        
        setBoundCond(data_bank.getUIBoundaryCondition());
        setTanks(data_bank.getUITank());
        handle3DArea(sub_scene, pict);
        
        scroll_pane = new ScrollPane();
        scroll_pane.setContent(sub_scene);
        
        this.setRight(tool_bar);
        this.setCenter(scroll_pane);        
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
    
    public void initToolBar(){
        
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
        
        this.tool_bar.getItems().add(
                sliders_tp                
        );
    }
    
    public void showScheme(){
        System.out.println("ReccAScheme method showScheme(): start");
        
        Box cube = new Box(40,40,40);
        cube.setTranslateX(20.0d);
        cube.setTranslateY(20.0d);
        cube.setTranslateZ(20.0d);
        cube.setMaterial(new PhongMaterial(Color.GRAY));
        
        Arrow top_arrow_No1 = new Arrow();
        Arrow top_arrow_No2 = new Arrow();
        
        Arrow left_arrow_No1 = new Arrow();
        Arrow left_arrow_No2 = new Arrow();
        
        Arrow right_arrow_No1 = new Arrow();
        Arrow right_arrow_No2 = new Arrow();
        
        Arrow bottom_arrow_No1 = new Arrow();
        Arrow bottom_arrow_No2 = new Arrow();
        
        Arrow front_arrow_No1 = new Arrow();
        Arrow front_arrow_No2 = new Arrow();
        
        Arrow back_arrow_No1 = new Arrow();
        Arrow back_arrow_No2 = new Arrow();
        
        top_arrow_No1.layout.setTranslateX(25);
        top_arrow_No1.layout.setTranslateY(60);
        top_arrow_No1.layout.setTranslateZ(20);
        top_arrow_No1.layout.setRotateZ(-90.0d);

        top_arrow_No2.layout.setTranslateX(15);
        top_arrow_No2.layout.setTranslateY(60);
        top_arrow_No2.layout.setTranslateZ(20);
        top_arrow_No2.layout.setRotateZ(-90.0d);

        left_arrow_No1.layout.setTranslateX(60);
        left_arrow_No1.layout.setTranslateY(15);
        left_arrow_No1.layout.setTranslateZ(20);
        left_arrow_No1.layout.setRotateZ(180.0d);        
        
        left_arrow_No2.layout.setTranslateX(60);
        left_arrow_No2.layout.setTranslateY(25);
        left_arrow_No2.layout.setTranslateZ(20);
        left_arrow_No2.layout.setRotateZ(180.0d);
        
        right_arrow_No1.layout.setTranslateX(-20);
        right_arrow_No1.layout.setTranslateY(15);
        right_arrow_No1.layout.setTranslateZ(20);
        
        right_arrow_No2.layout.setTranslateX(-20);
        right_arrow_No2.layout.setTranslateY(25);
        right_arrow_No2.layout.setTranslateZ(20);
        
        bottom_arrow_No1.layout.setTranslateX(15);
        bottom_arrow_No1.layout.setTranslateY(-20);
        bottom_arrow_No1.layout.setTranslateZ(20);
        bottom_arrow_No1.layout.setRotateZ(90.0d);
        
        bottom_arrow_No2.layout.setTranslateX(25);
        bottom_arrow_No2.layout.setTranslateY(-20);
        bottom_arrow_No2.layout.setTranslateZ(20);
        bottom_arrow_No2.layout.setRotateZ(90.0d);
        
        front_arrow_No1.layout.setTranslateX(15);
        front_arrow_No1.layout.setTranslateY(20);
        front_arrow_No1.layout.setTranslateZ(60);
        front_arrow_No1.layout.setRotateY(90.0d);
        
        front_arrow_No2.layout.setTranslateX(25);
        front_arrow_No2.layout.setTranslateY(20);
        front_arrow_No2.layout.setTranslateZ(60);
        front_arrow_No2.layout.setRotateY(90.0d);
        
        back_arrow_No1.layout.setTranslateX(15);
        back_arrow_No1.layout.setTranslateY(20);
        back_arrow_No1.layout.setTranslateZ(-20);
        back_arrow_No1.layout.setRotateY(-90.0d);
        
        back_arrow_No2.layout.setTranslateX(25);
        back_arrow_No2.layout.setTranslateY(20);
        back_arrow_No2.layout.setTranslateZ(-20);
        back_arrow_No2.layout.setRotateY(-90.0d);
        
        pict.world.getChildren().addAll(cube, top_arrow_No1.layout,
                                                        top_arrow_No2.layout,
                                                        left_arrow_No1.layout,
                                                        left_arrow_No2.layout,
                                                        right_arrow_No1.layout,
                                                        right_arrow_No2.layout,
                                                        bottom_arrow_No1.layout,
                                                        bottom_arrow_No2.layout,
                                                        front_arrow_No1.layout,
                                                        front_arrow_No2.layout,
                                                        back_arrow_No1.layout,
                                                        back_arrow_No2.layout);
    }
    
    public void initBoundCondLabels(){
        top_facet_temp = new Label();
        top_facet_mech = new Label();
        
        top_facet_temp.setTranslateX(25);
        top_facet_temp.setTranslateY(60);
        top_facet_temp.setTranslateZ(20);
        top_facet_temp.setRotate(180.0);
        
        top_facet_mech.setTranslateX(25);
        top_facet_mech.setTranslateY(50);
        top_facet_mech.setTranslateZ(20);
        top_facet_mech.setRotate(180.0);
        
        left_facet_temp = new Label();
        left_facet_mech = new Label();
        
        left_facet_temp.setTranslateX(60);
        left_facet_temp.setTranslateY(15);
        left_facet_temp.setTranslateZ(20);
        left_facet_temp.setRotate(180.0);
        
        left_facet_mech.setTranslateX(60);
        left_facet_mech.setTranslateY(25);
        left_facet_mech.setTranslateZ(20);
        left_facet_mech.setRotate(180.0);
        
        right_facet_temp = new Label();
        right_facet_mech = new Label();
        
        right_facet_temp.setTranslateX(-20);
        right_facet_temp.setTranslateY(15);
        right_facet_temp.setTranslateZ(20);
        right_facet_temp.setRotate(180.0);
        
        right_facet_mech.setTranslateX(-20);
        right_facet_mech.setTranslateY(25);
        right_facet_mech.setTranslateZ(20);
        right_facet_mech.setRotate(180.0);
        
        bottom_facet_temp = new Label();
        bottom_facet_mech = new Label();
        
        bottom_facet_temp.setTranslateX(25);
        bottom_facet_temp.setTranslateY(-20);
        bottom_facet_temp.setTranslateZ(20);
        bottom_facet_temp.setRotate(180.0d);
        
        bottom_facet_mech.setTranslateX(25);
        bottom_facet_mech.setTranslateY(-30);
        bottom_facet_mech.setTranslateZ(20);
        bottom_facet_mech.setRotate(180.0d);
        
        front_facet_temp = new Label();
        front_facet_mech = new Label();
        
        front_facet_temp.setTranslateX(15);
        front_facet_temp.setTranslateY(20);
        front_facet_temp.setTranslateZ(60);
        front_facet_temp.setRotate(90.0d);
        
        front_facet_mech.setTranslateX(25);
        front_facet_mech.setTranslateY(20);
        front_facet_mech.setTranslateZ(60);
        front_facet_mech.setRotate(90.0d);
        
        back_facet_temp = new Label();
        back_facet_mech = new Label();
        
        back_facet_temp.setTranslateX(15);
        back_facet_temp.setTranslateY(20);
        back_facet_temp.setTranslateZ(-20);
        back_facet_temp.setRotate(-90.0d);
        
        back_facet_mech.setTranslateX(25);
        back_facet_mech.setTranslateY(20);
        back_facet_mech.setTranslateZ(-20);
        back_facet_mech.setRotate(-90.0d);
        
        pict.world.getChildren().addAll(
                top_facet_temp, top_facet_mech, 
                left_facet_temp, left_facet_mech,
                right_facet_temp, right_facet_mech,
                bottom_facet_temp, bottom_facet_mech,
                front_facet_temp, front_facet_mech,
                back_facet_temp, back_facet_mech
        );
    }
    
    public void setBoundCond(UIBoundaryCondition bound_cond){
        
        top_facet_temp.setText("T = " + String.valueOf(bound_cond.top_thermal_influx));
        top_facet_mech.setText("dE = " + String.valueOf(bound_cond.top_mechanical_loading));
        
        left_facet_temp.setText("T = " + String.valueOf(bound_cond.left_thermal_influx));
        left_facet_mech.setText("dE = " + String.valueOf(bound_cond.left_mechanical_loading));
        
        right_facet_temp.setText("T = " + String.valueOf(bound_cond.right_thermal_influx));
        right_facet_mech.setText("dE = " + String.valueOf(bound_cond.right_mechanical_loading));
        
        bottom_facet_temp.setText("T = " + String.valueOf(bound_cond.bottom_thermal_influx));
        bottom_facet_mech.setText("dE = " + String.valueOf(bound_cond.bottom_mechanical_loading));
        
        front_facet_temp.setText("T = " + String.valueOf(bound_cond.front_thermal_influx));
        front_facet_mech.setText("dE = " + String.valueOf(bound_cond.front_mechanical_loading));
        
        back_facet_temp.setText("T = " + String.valueOf(bound_cond.back_thermal_influx));
        back_facet_mech.setText("dE = " + String.valueOf(bound_cond.back_mechanical_loading));        
    }
    
    public void setTanks(UITank tank){
        
    }    
}
