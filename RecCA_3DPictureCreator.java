/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.scene.CacheHint;
import javafx.scene.PerspectiveCamera;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCA_3DPictureCreator{
    
    final BorderPane root = new BorderPane();
    final Xform world = new Xform();    
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    final Xform axisGroup = new Xform();
    final Xform elementsGroup = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);    
    
    TransferredDataBank trans_data_bank;
    
    public RecCA_3DPictureCreator(TransferredDataBank receive_data_bank){
        System.out.println("RecCA_3DPictureCreator constructor start");
        
        this.trans_data_bank = receive_data_bank;
        
        buildCamera();
        buildAxes();        
    }
    
    public void buildCamera(){
        System.out.println("RecCA_3DPictureCreator method buildCamera(): start");
        
        root.getChildren().add(cameraXform);
        
        cameraXform.getChildren().add(cameraXform2);
        
        cameraXform.getChildren().add(cameraXform3);
        
        cameraXform3.getChildren().add(camera);
        
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(UICommon.CAMERA_NEAR_CLIP);
        camera.setFarClip(UICommon.CAMERA_FAR_CLIP);
        camera.setTranslateZ(UICommon.CAMERA_INITIAL_DISTANCE);
        camera.setTranslateX(50);
        camera.setTranslateY(-15);
        
        cameraXform.ry.setAngle(UICommon.CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(UICommon.CAMERA_INITIAL_X_ANGLE);
        cameraXform.rz.setAngle(0.0);
    }
    
    public void buildAxes(){
        System.out.println("RecCA_3DPictureCreator method buildAxes(): start");
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        
        /**
        *Create an Axis
        */ 
        final Box xAxis = new Box(UICommon.AXIS_LENGTH , 1, 1);
        final Box yAxis = new Box(1, UICommon.AXIS_LENGTH , 1);
        final Box zAxis = new Box(1, 1, UICommon.AXIS_LENGTH );
        
        /**
        *set the Text to every axis
        */
        
        final Box x_axis_lbl_1 = new Box(11, 1, 1);
        final Box x_axis_lbl_2 = new Box(11, 1, 1);
        x_axis_lbl_1.setRotate(50);
        x_axis_lbl_1.setMaterial(new PhongMaterial(Color.BLACK));
        x_axis_lbl_2.setRotate(-50);
        x_axis_lbl_2.setMaterial(new PhongMaterial(Color.BLACK));
        Xform x_axis_lbl = new Xform();
        x_axis_lbl.getChildren().addAll(x_axis_lbl_1,x_axis_lbl_2);
        x_axis_lbl.setTx(UICommon.AXIS_LENGTH/1.9d);
        x_axis_lbl.setTy(-5.0d);
                
        final Box y_axis_box_1 = new Box(6, 1, 1);
        final Box y_axis_box_2 = new Box(4, 1, 1);
        final Box y_axis_box_3 = new Box(4, 1, 1);
        y_axis_box_1.setMaterial(new PhongMaterial(Color.BLACK));
        y_axis_box_1.setRotate(90);
        y_axis_box_1.setTranslateY(-2.0d);
        y_axis_box_2.setMaterial(new PhongMaterial(Color.BLACK));
        y_axis_box_2.setRotate(50);
        y_axis_box_2.setTranslateX(1.9d);
        y_axis_box_2.setTranslateY(2.0d);
        y_axis_box_3.setMaterial(new PhongMaterial(Color.BLACK));
        y_axis_box_3.setRotate(-50);
        y_axis_box_3.setTranslateX(-1.9d);
        y_axis_box_3.setTranslateY(2.0d);
        Xform y_axis_lbl = new Xform();
        y_axis_lbl.getChildren().addAll(y_axis_box_1,
                                        y_axis_box_2,
                                        y_axis_box_3);
        y_axis_lbl.setTy(UICommon.AXIS_LENGTH/1.9d);
        y_axis_lbl.setTx(5.0d);
        
        final Box z_axis_box_1 = new Box(10, 1, 1);
        final Box z_axis_box_2 = new Box(8, 1, 1);
        final Box z_axis_box_3 = new Box(8, 1, 1);
        z_axis_box_1.setMaterial(new PhongMaterial(Color.BLACK));
        z_axis_box_2.setMaterial(new PhongMaterial(Color.BLACK));
        z_axis_box_3.setMaterial(new PhongMaterial(Color.BLACK));
        
        z_axis_box_1.setRotate(45.0d);
        z_axis_box_2.setTranslateY(4.0d);
        z_axis_box_3.setTranslateY(-4.0d);
        
        Xform z_axis_lbl = new Xform();
        z_axis_lbl.getChildren().addAll(z_axis_box_1,
                                        z_axis_box_2,
                                        z_axis_box_3);
        z_axis_lbl.setTz(UICommon.AXIS_LENGTH/1.9d);
        z_axis_lbl.setTx(5.0d);
        /**
        *set the color to every axis
        */

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis,x_axis_lbl, yAxis,y_axis_lbl, zAxis, z_axis_lbl);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }
    
    public double max_parameter_value;
    public double min_parameter_value;
    
    public void showAll(RecCAReadFiles data, String value, RecCAScale scale, String scale_option){
        
        System.out.println("RecCA_3DPictureCreator method showAll(): start");
        
        getMaxAndMinParameterValue(data, value);
        
        System.out.println("MAX VALUE: " + max_parameter_value);
        System.out.println("MIN VALUE: " + min_parameter_value);
        scale.setMaxValueToScale(max_parameter_value);
        scale.setMinValueToScale(min_parameter_value);
        
        int array_size = data.z_coordinate.size();
        
        for(int i=0; i< array_size ;i++ ){
            if(data.location_type.get(i) == Common.INNER_BOUNDARY_CELL 
             ||data.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL 
             ||data.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){
                    
                Sphere cell = new Sphere(1);
                
                cell.setTranslateX(data.x_coordinate.get(i));
                cell.setTranslateY(data.y_coordinate.get(i));
                cell.setTranslateZ(data.z_coordinate.get(i));
                
                cell.setCache(true);
                cell.setCacheHint(CacheHint.DEFAULT);
                
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                
                elementsGroup.getChildren().addAll(cell);
            }
        }
        world.getChildren().add(elementsGroup);     
        
    }
    
    int total_grains_number;
    
    public void showAllStructure(String task_name, RecCAReadFiles file, String type) throws IOException{
        System.out.println("RecCA_3DPictureCreator method showAllStructure(): start");
        
        total_grains_number = Collections.max(file.grain_index);
        
        file.readClrsFile(file.getClrsFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        List<Integer> grain_index_clrs = file.grain_index_clrs;
        System.out.println("Grain Index:" + grain_index_clrs);
        List<Integer> color_number = file.color_number;
        System.out.println("Color Number: " + color_number);
        List<Integer> r_color = file.r_color;
        List<Integer> g_color = file.g_color;
        List<Integer> b_color = file.b_color;
        System.out.println("R Color: " + file.r_color);
        System.out.println("G Color: " + file.g_color);
        System.out.println("B Color: " + file.b_color);
        
        List<Integer> location_type = file.location_type;
        List<Integer> grain_index = file.grain_index;
        int array_size = file.x_coordinate.size();
        
        List<Double> x_coordinate = file.x_coordinate;
        List<Double> y_coordinate = file.y_coordinate;
        List<Double> z_coordinate = file.z_coordinate;
        
        switch (type){
            case "structure":
                for(int i=0; i < array_size; i++){
                    if(location_type.get(i) == Common.INNER_BOUNDARY_CELL ||
                       location_type.get(i) == Common.GRAIN_BOUNDARY_REGION_CELL ||
                       location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL ||                   
                       location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){
                           
                        Sphere cell = new Sphere(1);                
                        cell.setTranslateX(x_coordinate.get(i));
                        cell.setTranslateY(y_coordinate.get(i));
                        cell.setTranslateZ(z_coordinate.get(i));
                        
                        for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                            if(Objects.equals(grain_index.get(i), grain_index_clrs.get(j))){
                                setColorToCell(cell, r_color.get(color_number.get(j)),
                                                    g_color.get(color_number.get(j)),
                                                    b_color.get(color_number.get(j)));
                                break;
                            }
                        }                            
                        elementsGroup.getChildren().addAll(cell);
                    }
                }
                world.getChildren().add(elementsGroup);
                break;
            case "structure (without boundaries)":
                for(int i=0; i < file.x_coordinate.size(); i++){
                    if(file.location_type.get(i) == Common.INNER_BOUNDARY_CELL ||
                       file.location_type.get(i) == Common.GRAIN_BOUNDARY_REGION_CELL ||                                              
                       file.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){
                            
                        Sphere cell = new Sphere(1);                
                        cell.setTranslateX(file.x_coordinate.get(i));
                        cell.setTranslateY(file.y_coordinate.get(i));
                        cell.setTranslateZ(file.z_coordinate.get(i));
                            
                        for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                            if(Objects.equals(file.grain_index.get(i), grain_index_clrs.get(j))){
                                setColorToCell(cell, file.r_color.get(color_number.get(j)),
                                                    file.g_color.get(color_number.get(j)),
                                                    file.b_color.get(color_number.get(j)));
                                break;
                            }
                        }                            
                        elementsGroup.getChildren().addAll(cell);
                    }
                }
                world.getChildren().add(elementsGroup);
                break;
            case "structure (boundary automata view)":
                for(int i=0; i < file.x_coordinate.size(); i++){
                    if(file.location_type.get(i) == Common.INNER_BOUNDARY_CELL ||
                       file.location_type.get(i) == Common.GRAIN_BOUNDARY_REGION_CELL ||
                       file.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL ||                   
                       file.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){
                           
                       Sphere cell = new Sphere(1);                
                       cell.setTranslateX(file.x_coordinate.get(i));
                       cell.setTranslateY(file.y_coordinate.get(i));
                       cell.setTranslateZ(file.z_coordinate.get(i));
                           
                       for(int j = 0 ; j < grain_index_clrs.size() ; j++){                                
                           if(Objects.equals(file.grain_index.get(i), grain_index_clrs.get(j))){                                    
                                setColorToCell(cell, file.r_color.get(color_number.get(j)),
                                                    file.g_color.get(color_number.get(j)),
                                                    file.b_color.get(color_number.get(j)));
                                break;
                           }
                       }
                       if(file.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL) cell.setMaterial(new PhongMaterial(Color.BLACK));
                       cell.setCache(true);
                       cell.setCacheHint(CacheHint.DEFAULT);
                       elementsGroup.getChildren().addAll(cell);
                    }
                }
                world.getChildren().add(elementsGroup);
                break;
        }
    }
    
    public void showAllMaterials(String task_name, RecCAReadFiles file) throws IOException{
        System.out.println("!!!----------------SHOW MATERIALS----------------!!!");
        
        file.readGrnFile(task_name);
        
        int array_size = file.z_coordinate.size();
        
        for(int i=0; i< array_size ;i++ ){
            if(file.location_type.get(i) == Common.INNER_BOUNDARY_CELL 
             ||file.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL
             ||file.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){
                    
                Sphere cell = new Sphere(1);
                
                cell.setTranslateX(file.x_coordinate.get(i));
                cell.setTranslateY(file.y_coordinate.get(i));
                cell.setTranslateZ(file.z_coordinate.get(i));
                
                cell.setCache(true);
                cell.setCacheHint(CacheHint.DEFAULT);
                
                for(int j = 0 ; j < file.grn_index.size() ; j ++){
                    if(Objects.equals(file.grain_index.get(i), file.grn_index.get(j)))
                        cell.setMaterial(new PhongMaterial(getColorForMaterial(file.materials.get(j))));
                }                
                elementsGroup.getChildren().addAll(cell);
            }
        }
        world.getChildren().add(elementsGroup);
    }
    
    public Color getColorForMaterial(String material){
        Color color = null;
        
        switch(material){
            case "aluminium":
                color = Color.ANTIQUEWHITE;
                break;
            case "aluminium_1":
                color = Color.AQUA;
                break;
            case "aluminium_2":
                color = Color.BLANCHEDALMOND;
                break;
            case "aluminium_3":
                color = Color.BLUE;
                break;
            case "ceramics":
                color = Color.BLUEVIOLET;
                break;
            case "copper":
                color = Color.ORANGE;
                break;
            case "copper_2":
                color = Color.BROWN;
                break;
            case "default":
                color = Color.ALICEBLUE;
                break;
            case "empty":
                color = Color.AZURE;
                break;
            case "iron":
                color = Color.SILVER;
                break;
            case "molibden":
                color = Color.BURLYWOOD;
                break;
            case "rubber":
                color = Color.CADETBLUE;
                break;
            case "steel":
                color = Color.DARKGREY;
                break;
            case "steel_08X18H10T":
                color = Color.CHARTREUSE;
                break;
            case "steel_12X18H09T":
                color = Color.CHOCOLATE;
                break;
            case "steel2":
                color = Color.CORAL;
                break;
            case "steel12GBA":
                color = Color.CRIMSON;
                break;
            case "steel17G1S":
                color = Color.CYAN;
                break;
            case "steel17G1S_213K":
                color = Color.DARKGOLDENROD;
                break;
            case "steel17G1S_233K":
                color = Color.DARKGREEN;
                break;
            case "steel17G1S_253K":
                color = Color.DARKKHAKI;
                break;
            case "steel17G1S_273K":
                color = Color.DARKMAGENTA;
                break;
            case "steel25X1MF":
                color = Color.DARKORANGE;
                break;
            case "steel40X13":
                color = Color.DARKRED;
                break;
            case "steel40X13_2":
                color = Color.DARKSALMON;
                break;
            case "TiAlC":
                color = Color.DARKSEAGREEN;
                break;
            case "TiAlC_a":
                color = Color.DARKSLATEBLUE;
                break;
            case "TiAlN":
                color = Color.DODGERBLUE;
                break;
            case "titanium":
                color = Color.LIGHTSTEELBLUE;
                break;
            case "titanium_iron":                
                color = Color.LIGHTGREY;
                break;
            case "titanium_iron_a":
                color = Color.MAROON;
                break;
            case "zirconium_dioxide":
                color = Color.OLIVEDRAB;
                break;            
        }
        
        return color;
    }
    
    public void showAllWithScale(RecCAReadFiles data, String value, RecCAScale scale, String scale_option){
        System.out.println("RecCA_3DPictureCreator method showAllWithScale(): start");
        
        int array_size = data.z_coordinate.size();
        
        for(int i=0; i< array_size ;i++){
            if(data.location_type.get(i) == Common.INNER_BOUNDARY_CELL 
           ||data.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL 
           ||data.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){

                Sphere cell = new Sphere(1);        
                cell.setTranslateX(data.x_coordinate.get(i));
                cell.setTranslateY(data.y_coordinate.get(i));
                cell.setTranslateZ(data.z_coordinate.get(i));
                cell.setCache(true);
                cell.setCacheHint(CacheHint.DEFAULT);
                
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                
                elementsGroup.getChildren().addAll(cell);
            }
        }
        world.getChildren().add(elementsGroup);  
    }
    
    public void showPlane(String task_name, RecCAReadFiles data, char plane, int layer_number, String value, RecCAScale scale, String scale_option) throws IOException{
        System.out.println("RecCA_3DPictureCreator method showPlane(): start");
        
        data.readSecaFile(data.getSecaFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        double max_X_coordinate = data.getMaxValue(data.x_coordinate);
        double min_X_coordinate = data.getMinValue(data.x_coordinate);
        double delta_X = (max_X_coordinate - min_X_coordinate)/(data.cell_number_x - 1);
        
        double max_Y_coordinate = data.getMaxValue(data.y_coordinate);
        double min_Y_coordinate = data.getMinValue(data.y_coordinate);
        double delta_Y = (max_Y_coordinate - min_Y_coordinate)/(data.cell_number_y - 1);
        
        double max_Z_coordinate = data.getMaxValue(data.z_coordinate);
        double min_Z_coordinate = data.getMinValue(data.z_coordinate);
        double delta_Z = (max_Z_coordinate - min_Z_coordinate)/(data.cell_number_z - 1);     
        
        getMaxAndMinParameterValue(data, value);
        
        System.out.println("MAX VALUE: " + max_parameter_value);
        System.out.println("MIN VALUE: " + min_parameter_value);
        scale.setMaxValueToScale(max_parameter_value);
        scale.setMinValueToScale(min_parameter_value);
        
        for(int i=0; i< data.x_coordinate.size() ;i++){
            if(data.location_type.get(i) != Common.OUTER_CELL){
                Sphere cell = new Sphere(1); 
                switch (plane){
                    case 'X':                                          
                        if(data.x_coordinate.get(i) < (layer_number + 0.5)*delta_X ){
                            if(data.x_coordinate.get(i) > (layer_number - 0.5)*delta_X ){
                                cell.setTranslateX(data.x_coordinate.get(i));
                                cell.setTranslateY(data.y_coordinate.get(i));
                                cell.setTranslateZ(data.z_coordinate.get(i));
                            }         
                        }
                        break;
                    case 'Y':
                         if(data.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y ){
                            if(data.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y ){
                                cell.setTranslateX(data.x_coordinate.get(i));
                                cell.setTranslateY(data.y_coordinate.get(i));
                                cell.setTranslateZ(data.z_coordinate.get(i));
                            }         
                        }
                        break;
                    case 'Z':
                         if(data.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z ){
                            if(data.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z ){
                                cell.setTranslateX(data.x_coordinate.get(i));
                                cell.setTranslateY(data.y_coordinate.get(i));
                                cell.setTranslateZ(data.z_coordinate.get(i));
                            }         
                        }
                        break;
                }
                
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                
                elementsGroup.getChildren().addAll(cell);
            }
        }
        world.getChildren().add(elementsGroup);
    }
    
    public void showPlaneStructure(String task_name, RecCAReadFiles read_file, char plane, int layer_number, String type) throws IOException{
        System.out.println("RecCA_3DPictureCreator method showPlaneStructure(): start");
        
        read_file.readSecaFile(read_file.getSecaFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        read_file.readClrsFile(read_file.getClrsFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        List<Integer> grain_index_clrs = read_file.grain_index_clrs;
        grain_index_clrs.remove(0);
        List<Integer> color_number = read_file.color_number;
        color_number.remove(0);
        
        double max_X_coordinate = read_file.getMaxValue(read_file.x_coordinate);
        double min_X_coordinate = read_file.getMinValue(read_file.x_coordinate);
        double delta_X = (max_X_coordinate - min_X_coordinate)/(read_file.cell_number_x - 1);
        
        double max_Y_coordinate = read_file.getMaxValue(read_file.y_coordinate);
        double min_Y_coordinate = read_file.getMinValue(read_file.y_coordinate);
        double delta_Y = (max_Y_coordinate - min_Y_coordinate)/(read_file.cell_number_y - 1);
        
        double max_Z_coordinate = read_file.getMaxValue(read_file.z_coordinate);
        double min_Z_coordinate = read_file.getMinValue(read_file.z_coordinate);
        double delta_Z = (max_Z_coordinate - min_Z_coordinate)/(read_file.cell_number_z - 1);
                       
        switch (type){
            case "structure":
                System.out.println("DeltaX: " + delta_X);
                System.out.println("DeltaY: " + delta_Y);
                System.out.println("DeltaZ: " + delta_Z);
                for(int i=0; i < read_file.x_coordinate.size(); i++){
                        if(read_file.location_type.get(i) == Common.INNER_BOUNDARY_CELL ||
                           read_file.location_type.get(i) == Common.GRAIN_BOUNDARY_REGION_CELL ||
                           read_file.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL ||                   
                           read_file.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){
                            
                            Sphere cell = new Sphere(1);                
                            
                            switch (plane)
                            {
                                case 'X':
                         //           if(read_file.x_coordinate.get(i) < (layer_number + 0.5)*delta_X )
                                {
                         //               if(read_file.x_coordinate.get(i) > (layer_number - 0.5)*delta_X )
                                {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));
                                }
                                }
                                    break;
                                case 'Y':
                             //       if(read_file.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y )
                                    {
                            //            if(read_file.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Z':
                             //       if(read_file.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z )
                                    {
                             //           if(read_file.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                            }
                            
                            for(int j = 0 ; j < grain_index_clrs.size() ; j++)
                            {
                                if(Objects.equals(read_file.grain_index.get(i), grain_index_clrs.get(j)))
                                {
                                    setColorToCell(cell, read_file.r_color.get(color_number.get(j)),
                                                         read_file.g_color.get(color_number.get(j)),
                                                         read_file.b_color.get(color_number.get(j)));
                                    break;
                                                           
                                }
                            }                            
                            elementsGroup.getChildren().addAll(cell);
                        }
                    }
                    world.getChildren().add(elementsGroup);
                break;
            case "structure (without boundaries)":
                for(int i=0; i < read_file.x_coordinate.size(); i++){
                        if(read_file.location_type.get(i) == Common.INNER_BOUNDARY_CELL ||
                           read_file.location_type.get(i) == Common.GRAIN_BOUNDARY_REGION_CELL ||
                           read_file.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL ||                   
                           read_file.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL){                            
                            Sphere cell = new Sphere(1); 
                            
                            switch (plane)
                            {
                                case 'X':
                            //        if(read_file.x_coordinate.get(i) < (layer_number + 0.5)*delta_X )
                                    {
                            //            if(read_file.x_coordinate.get(i) > (layer_number - 0.5)*delta_X )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Y':
                            //        if(read_file.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y )
                                    {
                            //            if(read_file.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Z':
                            //        if(read_file.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z )
                                    {
                            //            if(read_file.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                            }
                            
                            for(int j = 0 ; j < grain_index_clrs.size() ; j++)
                            {
                                if(Objects.equals(read_file.grain_index.get(i), grain_index_clrs.get(j)))
                                {
                                    setColorToCell(cell, read_file.r_color.get(color_number.get(j)),
                                                         read_file.g_color.get(color_number.get(j)),
                                                         read_file.b_color.get(color_number.get(j)));                                
                                }
                            }                            
                            elementsGroup.getChildren().addAll(cell);
                        }
                    }
                    world.getChildren().add(elementsGroup);
                break;

            case "structure (boundary automata view)":
                for(int i=0; i < read_file.x_coordinate.size(); i++)
                {
                        if(read_file.location_type.get(i) == Common.INNER_BOUNDARY_CELL ||
                           read_file.location_type.get(i) == Common.GRAIN_BOUNDARY_REGION_CELL ||
                           read_file.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL ||                   
                           read_file.location_type.get(i) == Common.INNER_BOUND_GRAIN_BOUND_REGION_CELL)
                        {
                            
                            Sphere cell = new Sphere(1);                
                            
                            switch (plane)
                            {
                                case 'X':
                               //     if(read_file.x_coordinate.get(i) < (layer_number + 0.5)*delta_X )
                                    {
                               //         if(read_file.x_coordinate.get(i) > (layer_number - 0.5)*delta_X )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));                                            
                                        }         
                                    }
                                    break;
                                    
                                case 'Y':
                            //        if(read_file.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y )
                                    {
                            //            if(read_file.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                    
                                case 'Z':
                            //        if(read_file.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z )
                                    {
                            //            if(read_file.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z )
                                        {
                                            cell.setTranslateX(read_file.x_coordinate.get(i));
                                            cell.setTranslateY(read_file.y_coordinate.get(i));
                                            cell.setTranslateZ(read_file.z_coordinate.get(i));                                            
                                        }         
                                    }
                                    break;
                            }
                            
                            for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                                if(Objects.equals(read_file.grain_index.get(i), grain_index_clrs.get(j)))
                                {
                                    setColorToCell(cell, read_file.r_color.get(color_number.get(j)),
                                                         read_file.g_color.get(color_number.get(j)),
                                                         read_file.b_color.get(color_number.get(j)));
                                    break;
                                }
                            }
                            elementsGroup.getChildren().addAll(cell);
                        }
                    }
                    world.getChildren().add(elementsGroup);
                break;
        }
    }
    
    public void showPlaneWithScale(String task_name, RecCAReadFiles data, String value, char plane, int layer_number, RecCAScale scale, String scale_option) throws IOException
    {
        System.out.println("RecCA_3DPictureCreator method showPlaneWithScale(): start");
        
        data.readSecaFile(data.getSecaFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        double max_X_coordinate = data.getMaxValue(data.x_coordinate);
        double min_X_coordinate = data.getMinValue(data.x_coordinate);
        double delta_X = (max_X_coordinate - min_X_coordinate)/(data.cell_number_x - 1);
        
        double max_Y_coordinate = data.getMaxValue(data.y_coordinate);
        double min_Y_coordinate = data.getMinValue(data.y_coordinate);
        double delta_Y = (max_Y_coordinate - min_Y_coordinate)/(data.cell_number_y - 1);
        
        double max_Z_coordinate = data.getMaxValue(data.z_coordinate);
        double min_Z_coordinate = data.getMinValue(data.z_coordinate);
        double delta_Z = (max_Z_coordinate - min_Z_coordinate)/(data.cell_number_z - 1);
        
        for(int i=0; i< data.x_coordinate.size() ;i++ )
        {
             if(data.location_type.get(i) != Common.OUTER_CELL)
             {
                 
                 Sphere cell = new Sphere(1);
                 
                 switch (plane)
                 {
                     case 'X':
                    //     if(data.x_coordinate.get(i) < (layer_number + 0.5)*delta_X )
                         {
                    //         if(data.x_coordinate.get(i) > (layer_number - 0.5)*delta_X )
                             {
                                 cell.setTranslateX(data.x_coordinate.get(i));
                                 cell.setTranslateY(data.y_coordinate.get(i));
                                 cell.setTranslateZ(data.z_coordinate.get(i));
                             }         
                         }
                         break;
                         
                     case 'Y':
                    //     if(data.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y )
                         {
                    //         if(data.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y )
                             {
                                 cell.setTranslateX(data.x_coordinate.get(i));
                                 cell.setTranslateY(data.y_coordinate.get(i));
                                 cell.setTranslateZ(data.z_coordinate.get(i));
                             }         
                         }                         
                         break;
                         
                     case 'Z':
                     //    if(data.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z )
                         {
                     //        if(data.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z )
                             {
                                 cell.setTranslateX(data.x_coordinate.get(i));
                                 cell.setTranslateY(data.y_coordinate.get(i));
                                 cell.setTranslateZ(data.z_coordinate.get(i));
                             }         
                         }
                         break;                
                 }
                 
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                                  
                elementsGroup.getChildren().addAll(cell);
             }
        }
        world.getChildren().add(elementsGroup);
    }
    
    public void setColorToCell(Sphere cell , int rColor , int gColor, int bColor)
    {
        cell.setMaterial(new PhongMaterial(Color.rgb(rColor, gColor, bColor)));
    }
    
    int first_cond, second_cond, third_cond, fourth_cond, fifth_cond, sixth_cond, seventh_cond, eight_cond, ninth_cond;
    
    private void getConditions(RecCA_3DPictureParams params)
    {
        if(params.location_type.isExpanded()){
            if(params.location_type_1.isSelected())
                first_cond = 2;
            else 
                first_cond = 100;

            if(params.location_type_2.isSelected())
                second_cond = 3;
            else 
                second_cond = 100;

            if(params.location_type_3.isSelected())
                third_cond = 4;
            else 
                third_cond = 100;

            if(params.location_type_4.isSelected())
                fourth_cond = 5;
            else 
                fourth_cond = 100;

            if(params.location_type_5.isSelected())
                fifth_cond = 6;
            else 
                fifth_cond = 100;

            if(params.location_type_6.isSelected())
                sixth_cond = 7;
            else 
                sixth_cond = 100;

            if(params.location_type_7.isSelected())
                seventh_cond = 8;
            else 
                seventh_cond = 100;
        }
    }
    
    public void showAll(RecCAReadFiles data, String value, RecCAScale scale, String scale_option, RecCA_3DPictureParams params){
        
        elementsGroup.getChildren().clear();
        world.getChildren().remove(elementsGroup);
        
        System.out.println("RecCA_3DPictureCreator method showAll(): start");
        
        getMaxAndMinParameterValue(data, value);
        
        System.out.println("MAX VALUE: " + max_parameter_value);
        System.out.println("MIN VALUE: " + min_parameter_value);
        
        scale.setMaxValueToScale(max_parameter_value);
        scale.setMinValueToScale(min_parameter_value);
        
        int array_size = data.z_coordinate.size();
        
        getConditions(params);
        
        for(int i=0; i< array_size ;i++ ){
            
            if(data.location_type.get(i) == first_cond
             ||data.location_type.get(i) == second_cond
             ||data.location_type.get(i) == third_cond
             ||data.location_type.get(i) == fourth_cond
             ||data.location_type.get(i) == fifth_cond
             ||data.location_type.get(i) == sixth_cond
             ||data.location_type.get(i) == seventh_cond
             ||data.location_type.get(i) == eight_cond
             ||data.location_type.get(i) == ninth_cond){
                
                    
                Sphere cell = new Sphere(1);
                
                cell.setTranslateX(data.x_coordinate.get(i));
                cell.setTranslateY(data.y_coordinate.get(i));
                cell.setTranslateZ(data.z_coordinate.get(i));
                
                cell.setCache(true);
                cell.setCacheHint(CacheHint.DEFAULT);
                
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                
                elementsGroup.getChildren().addAll(cell);
            }
        }
    }
    
    public void showAllStructure(String task_name, RecCAReadFiles file, String type, RecCA_3DPictureParams params) throws IOException{
        
        elementsGroup.getChildren().clear();
        world.getChildren().remove(elementsGroup);
        
        System.out.println("RecCA_3DPictureCreator method showAllStructure(): start");
        
        total_grains_number = Collections.max(file.grain_index);
        
        file.readClrsFile(file.getClrsFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        List<Integer> grain_index_clrs = file.grain_index_clrs;
        System.out.println("Grain Index:" + grain_index_clrs);
        List<Integer> color_number = file.color_number;
        System.out.println("Color Number: " + color_number);
        List<Integer> r_color = file.r_color;
        List<Integer> g_color = file.g_color;
        List<Integer> b_color = file.b_color;
        System.out.println("R Color: " + file.r_color);
        System.out.println("G Color: " + file.g_color);
        System.out.println("B Color: " + file.b_color);
        
        List<Integer> location_type = file.location_type;
        List<Integer> grain_index = file.grain_index;
        int array_size = file.x_coordinate.size();
        
        List<Double> x_coordinate = file.x_coordinate;
        List<Double> y_coordinate = file.y_coordinate;
        List<Double> z_coordinate = file.z_coordinate;
        
        getConditions(params);
        
        switch (type){
            case "structure":
                for(int i=0; i < array_size; i++){
                    if(file.location_type.get(i) == first_cond
                       ||file.location_type.get(i) == second_cond
                       ||file.location_type.get(i) == third_cond
                       ||file.location_type.get(i) == fourth_cond
                       ||file.location_type.get(i) == fifth_cond
                       ||file.location_type.get(i) == sixth_cond
                       ||file.location_type.get(i) == seventh_cond
                       ||file.location_type.get(i) == eight_cond
                       ||file.location_type.get(i) == ninth_cond){
                           
                        Sphere cell = new Sphere(1);                
                        cell.setTranslateX(x_coordinate.get(i));
                        cell.setTranslateY(y_coordinate.get(i));
                        cell.setTranslateZ(z_coordinate.get(i));
                        
                        for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                            if(Objects.equals(grain_index.get(i), grain_index_clrs.get(j))){
                                setColorToCell(cell, r_color.get(color_number.get(j)),
                                                    g_color.get(color_number.get(j)),
                                                    b_color.get(color_number.get(j)));
                                break;
                            }
                        }                            
                        elementsGroup.getChildren().addAll(cell);
                    }
                }
                world.getChildren().add(elementsGroup);
                break;
            case "structure (without boundaries)":
                for(int i=0; i < file.x_coordinate.size(); i++){
                    if(file.location_type.get(i) == first_cond
                       ||file.location_type.get(i) == second_cond
                       ||file.location_type.get(i) == third_cond
                       ||file.location_type.get(i) == fourth_cond
                       ||file.location_type.get(i) == fifth_cond
                       ||file.location_type.get(i) == sixth_cond
                       ||file.location_type.get(i) == seventh_cond
                       ||file.location_type.get(i) == eight_cond
                       ||file.location_type.get(i) == ninth_cond){
                            
                        Sphere cell = new Sphere(1);                
                        cell.setTranslateX(file.x_coordinate.get(i));
                        cell.setTranslateY(file.y_coordinate.get(i));
                        cell.setTranslateZ(file.z_coordinate.get(i));
                            
                        for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                            if(Objects.equals(file.grain_index.get(i), grain_index_clrs.get(j))){
                                setColorToCell(cell, file.r_color.get(color_number.get(j)),
                                                    file.g_color.get(color_number.get(j)),
                                                    file.b_color.get(color_number.get(j)));
                                break;
                            }
                        }                            
                        elementsGroup.getChildren().addAll(cell);
                    }
                }
                world.getChildren().add(elementsGroup);
                break;
            case "structure (boundary automata view)":
                for(int i=0; i < file.x_coordinate.size(); i++){
                    if(file.location_type.get(i) == first_cond
                       ||file.location_type.get(i) == second_cond
                       ||file.location_type.get(i) == third_cond
                       ||file.location_type.get(i) == fourth_cond
                       ||file.location_type.get(i) == fifth_cond
                       ||file.location_type.get(i) == sixth_cond
                       ||file.location_type.get(i) == seventh_cond
                       ||file.location_type.get(i) == eight_cond
                       ||file.location_type.get(i) == ninth_cond){
                           
                       Sphere cell = new Sphere(1);                
                       cell.setTranslateX(file.x_coordinate.get(i));
                       cell.setTranslateY(file.y_coordinate.get(i));
                       cell.setTranslateZ(file.z_coordinate.get(i));
                           
                       for(int j = 0 ; j < grain_index_clrs.size() ; j++){                                
                           if(Objects.equals(file.grain_index.get(i), grain_index_clrs.get(j))){                                    
                                setColorToCell(cell, file.r_color.get(color_number.get(j)),
                                                    file.g_color.get(color_number.get(j)),
                                                    file.b_color.get(color_number.get(j)));
                                break;
                           }
                       }
                       if(file.location_type.get(i) == Common.INNER_BOUNDARY_INTERGRANULAR_CELL) cell.setMaterial(new PhongMaterial(Color.BLACK));
                       cell.setCache(true);
                       cell.setCacheHint(CacheHint.DEFAULT);
                       elementsGroup.getChildren().addAll(cell);
                    }
                }
                world.getChildren().add(elementsGroup);
                break;
        }
    }
    
    public void showAllWithScale(RecCAReadFiles data, String value, RecCAScale scale, String scale_option, RecCA_3DPictureParams params){
        
        elementsGroup.getChildren().clear();
        world.getChildren().remove(elementsGroup);
        
        int array_size = data.z_coordinate.size();
        
        getConditions(params);
        
        for(int i=0; i< array_size ;i++){
            if(data.location_type.get(i) == first_cond
             ||data.location_type.get(i) == second_cond
             ||data.location_type.get(i) == third_cond
             ||data.location_type.get(i) == fourth_cond
             ||data.location_type.get(i) == fifth_cond
             ||data.location_type.get(i) == sixth_cond
             ||data.location_type.get(i) == seventh_cond
             ||data.location_type.get(i) == eight_cond
             ||data.location_type.get(i) == ninth_cond){

                Sphere cell = new Sphere(1);        
                cell.setTranslateX(data.x_coordinate.get(i));
                cell.setTranslateY(data.y_coordinate.get(i));
                cell.setTranslateZ(data.z_coordinate.get(i));
                cell.setCache(true);
                cell.setCacheHint(CacheHint.DEFAULT);
                
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                
                elementsGroup.getChildren().addAll(cell);
            }
        }
        world.getChildren().add(elementsGroup);
    }
    
    public void showPlane(String task_name, RecCAReadFiles data, char plane, int layer_number, String value, RecCAScale scale, String scale_option, RecCA_3DPictureParams params) throws IOException{
        
        elementsGroup.getChildren().clear();
        world.getChildren().remove(elementsGroup);
        
        data.readSecaFile(data.getSecaFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        double max_X_coordinate = data.getMaxValue(data.x_coordinate);
        double min_X_coordinate = data.getMinValue(data.x_coordinate);
        double delta_X = (max_X_coordinate - min_X_coordinate)/(data.cell_number_x - 1);
        
        double max_Y_coordinate = data.getMaxValue(data.y_coordinate);
        double min_Y_coordinate = data.getMinValue(data.y_coordinate);
        double delta_Y = (max_Y_coordinate - min_Y_coordinate)/(data.cell_number_y - 1);
        
        double max_Z_coordinate = data.getMaxValue(data.z_coordinate);
        double min_Z_coordinate = data.getMinValue(data.z_coordinate);
        double delta_Z = (max_Z_coordinate - min_Z_coordinate)/(data.cell_number_z - 1);     
        
        getMaxAndMinParameterValue(data, value);
        
        System.out.println("MAX VALUE: " + max_parameter_value);
        System.out.println("MIN VALUE: " + min_parameter_value);
        scale.setMaxValueToScale(max_parameter_value);
        scale.setMinValueToScale(min_parameter_value);
        
        getConditions(params);
        
        for(int i=0; i< data.x_coordinate.size() ;i++){
            if(data.location_type.get(i) == first_cond
             ||data.location_type.get(i) == second_cond
             ||data.location_type.get(i) == third_cond
             ||data.location_type.get(i) == fourth_cond
             ||data.location_type.get(i) == fifth_cond
             ||data.location_type.get(i) == sixth_cond
             ||data.location_type.get(i) == seventh_cond
             ||data.location_type.get(i) == eight_cond
             ||data.location_type.get(i) == ninth_cond){
                Sphere cell = new Sphere(1); 
                cell.setCache(true);
                cell.setCacheHint(CacheHint.DEFAULT);
                switch (plane){
                    case 'X':                                          
                        if(data.x_coordinate.get(i) < (layer_number + 0.5)*delta_X ){
                            if(data.x_coordinate.get(i) > (layer_number - 0.5)*delta_X ){
                                cell.setTranslateX(data.x_coordinate.get(i));
                                cell.setTranslateY(data.y_coordinate.get(i));
                                cell.setTranslateZ(data.z_coordinate.get(i));
                            }         
                        }
                        break;
                    case 'Y':
                         if(data.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y ){
                            if(data.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y ){
                                cell.setTranslateX(data.x_coordinate.get(i));
                                cell.setTranslateY(data.y_coordinate.get(i));
                                cell.setTranslateZ(data.z_coordinate.get(i));
                            }         
                        }
                        break;
                    case 'Z':
                         if(data.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z ){
                            if(data.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z ){
                                cell.setTranslateX(data.x_coordinate.get(i));
                                cell.setTranslateY(data.y_coordinate.get(i));
                                cell.setTranslateZ(data.z_coordinate.get(i));
                            }         
                        }
                        break;
                }
                
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                
                elementsGroup.getChildren().addAll(cell);
            }
        }
        world.getChildren().add(elementsGroup);
    }
    
    public void showPlaneStructure(String task_name, RecCAReadFiles data, char plane, int layer_number, String type, RecCA_3DPictureParams params) throws IOException{
        
        elementsGroup.getChildren().clear();
        world.getChildren().remove(elementsGroup);
        
        data.readSecaFile(data.getSecaFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        data.readClrsFile(data.getClrsFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        List<Integer> grain_index_clrs = data.grain_index_clrs;
//        grain_index_clrs.remove(0);
        List<Integer> color_number = data.color_number;
//        color_number.remove(0);
        
        double max_X_coordinate = data.getMaxValue(data.x_coordinate);
        double min_X_coordinate = data.getMinValue(data.x_coordinate);
        double delta_X = (max_X_coordinate - min_X_coordinate)/(data.cell_number_x - 1);
        
        double max_Y_coordinate = data.getMaxValue(data.y_coordinate);
        double min_Y_coordinate = data.getMinValue(data.y_coordinate);
        double delta_Y = (max_Y_coordinate - min_Y_coordinate)/(data.cell_number_y - 1);
        
        double max_Z_coordinate = data.getMaxValue(data.z_coordinate);
        double min_Z_coordinate = data.getMinValue(data.z_coordinate);
        double delta_Z = (max_Z_coordinate - min_Z_coordinate)/(data.cell_number_z - 1);
        
        getConditions(params);
        
        switch (type){
            case "structure":
                System.out.println("DeltaX: " + delta_X);
                System.out.println("DeltaY: " + delta_Y);
                System.out.println("DeltaZ: " + delta_Z);
                for(int i=0; i < data.x_coordinate.size(); i++){
                        if(data.location_type.get(i) == first_cond
                           ||data.location_type.get(i) == second_cond
                           ||data.location_type.get(i) == third_cond
                           ||data.location_type.get(i) == fourth_cond
                           ||data.location_type.get(i) == fifth_cond
                           ||data.location_type.get(i) == sixth_cond
                           ||data.location_type.get(i) == seventh_cond
                           ||data.location_type.get(i) == eight_cond
                           ||data.location_type.get(i) == ninth_cond){
                            
                            Sphere cell = new Sphere(1);                
                            
                            switch (plane){
                                case 'X':
                                    if(data.x_coordinate.get(i) < (layer_number + 0.5)*delta_X ){
                                        if(data.x_coordinate.get(i) > (layer_number - 0.5)*delta_X ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Y':
                                    if(data.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y ){
                                        if(data.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Z':
                                    if(data.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z ){
                                        if(data.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                            }
                            for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                                if(Objects.equals(data.grain_index.get(i), grain_index_clrs.get(j))){
                                    setColorToCell(cell, data.r_color.get(color_number.get(j)),
                                                         data.g_color.get(color_number.get(j)),
                                                         data.b_color.get(color_number.get(j)));
                                    break;
                                                           
                                }
                            }                            
                            elementsGroup.getChildren().addAll(cell);
                        }
                    }
                    world.getChildren().add(elementsGroup);
                break;
            case "structure (without boundaries)":
                for(int i=0; i < data.x_coordinate.size(); i++){
                        if(data.location_type.get(i) == first_cond
                           ||data.location_type.get(i) == second_cond
                           ||data.location_type.get(i) == third_cond
                           ||data.location_type.get(i) == fourth_cond
                           ||data.location_type.get(i) == fifth_cond
                           ||data.location_type.get(i) == sixth_cond
                           ||data.location_type.get(i) == seventh_cond
                           ||data.location_type.get(i) == eight_cond
                           ||data.location_type.get(i) == ninth_cond){                            
                            Sphere cell = new Sphere(1);                            
                            switch (plane){
                                case 'X':
                                    if(data.x_coordinate.get(i) < (layer_number + 0.5)*delta_X ){
                                        if(data.x_coordinate.get(i) > (layer_number - 0.5)*delta_X ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Y':
                                    if(data.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y ){
                                        if(data.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Z':
                                    if(data.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z ){
                                        if(data.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                            }
                            for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                                if(Objects.equals(data.grain_index.get(i), grain_index_clrs.get(j))){
                                    setColorToCell(cell, data.r_color.get(color_number.get(j)),
                                                         data.g_color.get(color_number.get(j)),
                                                         data.b_color.get(color_number.get(j)));                                
                                }
                            }                            
                            elementsGroup.getChildren().addAll(cell);
                        }
                    }
                    world.getChildren().add(elementsGroup);
                break;
            case "structure (boundary automata view)":
                for(int i=0; i < data.x_coordinate.size(); i++){
                        if(data.location_type.get(i) == first_cond
                           ||data.location_type.get(i) == second_cond
                           ||data.location_type.get(i) == third_cond
                           ||data.location_type.get(i) == fourth_cond
                           ||data.location_type.get(i) == fifth_cond
                           ||data.location_type.get(i) == sixth_cond
                           ||data.location_type.get(i) == seventh_cond
                           ||data.location_type.get(i) == eight_cond
                           ||data.location_type.get(i) == ninth_cond){
                            
                            Sphere cell = new Sphere(1);                
                            
                            switch (plane){
                                case 'X':
                                    if(data.x_coordinate.get(i) < (layer_number + 0.5)*delta_X ){
                                        if(data.x_coordinate.get(i) > (layer_number - 0.5)*delta_X ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));                                            
                                        }         
                                    }
                                    break;
                                case 'Y':
                                    if(data.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y ){
                                        if(data.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));
                                        }         
                                    }
                                    break;
                                case 'Z':
                                    if(data.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z ){
                                        if(data.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z ){
                                            cell.setTranslateX(data.x_coordinate.get(i));
                                            cell.setTranslateY(data.y_coordinate.get(i));
                                            cell.setTranslateZ(data.z_coordinate.get(i));                                            
                                        }         
                                    }
                                    break;
                            }
                            for(int j = 0 ; j < grain_index_clrs.size() ; j++){
                                if(Objects.equals(data.grain_index.get(i), grain_index_clrs.get(j))){
                                    setColorToCell(cell, data.r_color.get(color_number.get(j)),
                                                         data.g_color.get(color_number.get(j)),
                                                         data.b_color.get(color_number.get(j)));
                                    break;
                                }
                            }
                            elementsGroup.getChildren().addAll(cell);
                        }
                    }
                    world.getChildren().add(elementsGroup);
                break;
        }
    }
    
    public void showPlaneWithScale(String task_name, RecCAReadFiles data, String value, char plane, int layer_number, RecCAScale scale, String scale_option, RecCA_3DPictureParams params) throws IOException{
        
        elementsGroup.getChildren().clear();
        world.getChildren().remove(elementsGroup);
        
        data.readSecaFile(data.getSecaFilePath(Common.TASK_PATH + "/" + task_name + "/"));
        
        double max_X_coordinate = data.getMaxValue(data.x_coordinate);
        double min_X_coordinate = data.getMinValue(data.x_coordinate);
        double delta_X = (max_X_coordinate - min_X_coordinate)/(data.cell_number_x - 1);
        
        double max_Y_coordinate = data.getMaxValue(data.y_coordinate);
        double min_Y_coordinate = data.getMinValue(data.y_coordinate);
        double delta_Y = (max_Y_coordinate - min_Y_coordinate)/(data.cell_number_y - 1);
        
        double max_Z_coordinate = data.getMaxValue(data.z_coordinate);
        double min_Z_coordinate = data.getMinValue(data.z_coordinate);
        double delta_Z = (max_Z_coordinate - min_Z_coordinate)/(data.cell_number_z - 1);
        
        getConditions(params);
        
        for(int i=0; i< data.x_coordinate.size() ;i++ ){
             if(data.location_type.get(i) == first_cond
                ||data.location_type.get(i) == second_cond
                ||data.location_type.get(i) == third_cond
                ||data.location_type.get(i) == fourth_cond
                ||data.location_type.get(i) == fifth_cond
                ||data.location_type.get(i) == sixth_cond
                ||data.location_type.get(i) == seventh_cond
                ||data.location_type.get(i) == eight_cond
                ||data.location_type.get(i) == ninth_cond){
                 
                 Sphere cell = new Sphere(1);
                 switch (plane){
                     case 'X':
                         if(data.x_coordinate.get(i) < (layer_number + 0.5)*delta_X ){
                             if(data.x_coordinate.get(i) > (layer_number - 0.5)*delta_X ){
                                 cell.setTranslateX(data.x_coordinate.get(i));
                                 cell.setTranslateY(data.y_coordinate.get(i));
                                 cell.setTranslateZ(data.z_coordinate.get(i));
                             }         
                         }
                         break;
                     case 'Y':
                         if(data.y_coordinate.get(i) < (layer_number + 0.5)*delta_Y ){
                             if(data.y_coordinate.get(i) > (layer_number - 0.5)*delta_Y ){
                                 cell.setTranslateX(data.x_coordinate.get(i));
                                 cell.setTranslateY(data.y_coordinate.get(i));
                                 cell.setTranslateZ(data.z_coordinate.get(i));
                             }         
                         }
                         break;
                     case 'Z':
                         if(data.z_coordinate.get(i) < (layer_number + 0.5)*delta_Z ){
                             if(data.z_coordinate.get(i) > (layer_number - 0.5)*delta_Z ){
                                 cell.setTranslateX(data.x_coordinate.get(i));
                                 cell.setTranslateY(data.y_coordinate.get(i));
                                 cell.setTranslateZ(data.z_coordinate.get(i));
                             }         
                         }
                         break;                
                 }
                 
                setMaterialToCell(data, value, i, cell, scale, scale_option);
                                  
                elementsGroup.getChildren().addAll(cell);
             }
        }
        world.getChildren().add(elementsGroup);
    }
    
    private void getMaxAndMinParameterValue(RecCAReadFiles data, String value){
        switch (value){            
            /**
            * relValues.res
            */
            case "relative_temperature":
                max_parameter_value = data.getMaxValue(data.relative_temperature);
                min_parameter_value = data.getMinValue(data.relative_temperature);
                break;
            case "relative_specific_elastic_energy":
                max_parameter_value = data.getMaxValue(data.relative_specific_elastic_energy);
                min_parameter_value = data.getMinValue(data.relative_specific_elastic_energy);
                break;
            case "total_specific_energy":
                max_parameter_value = data.getMaxValue(data.total_specific_energy);
                min_parameter_value = data.getMinValue(data.total_specific_energy);
                break;
            case "abs_value_of_X_comp_of_inst_spec_force_moment":
                max_parameter_value = data.getMaxValue(data.abs_value_of_X_comp_of_inst_spec_force_moment);
                min_parameter_value = data.getMinValue(data.abs_value_of_X_comp_of_inst_spec_force_moment);
                break;
            case "abs_value_of_Y_comp_of_inst_spec_force_moment":
                max_parameter_value = data.getMaxValue(data.abs_value_of_Y_comp_of_inst_spec_force_moment);
                min_parameter_value = data.getMinValue(data.abs_value_of_Y_comp_of_inst_spec_force_moment);
                break;
            case "abs_value_of_Z_comp_of_inst_spec_force_moment":
                max_parameter_value = data.getMaxValue(data.abs_value_of_Z_comp_of_inst_spec_force_moment);
                min_parameter_value = data.getMinValue(data.abs_value_of_Z_comp_of_inst_spec_force_moment);
                break;
            case "abs_value_of_inst_spec_force_moment":
                max_parameter_value = data.getMaxValue(data.abs_value_of_inst_spec_force_moment);
                min_parameter_value = data.getMinValue(data.abs_value_of_inst_spec_force_moment);
                break;
            case "relative_specific_dissipated_energy":
                max_parameter_value = data.getMaxValue(data.relative_spec_dissipated_energy);
                min_parameter_value = data.getMinValue(data.relative_spec_dissipated_energy);
                break;

            /**
            * jocl_stresses.res
            */
            case "temperature":
                max_parameter_value = data.getMaxValue(data.temperature);
                min_parameter_value = data.getMinValue(data.temperature);
                break;
            case "abs_value_of_stress_vector":
                max_parameter_value = data.getMaxValue(data.abs_value_of_stress_vector);
                min_parameter_value = data.getMinValue(data.abs_value_of_stress_vector);
                break;
            case "value_for_crack_generation":
                max_parameter_value = data.getMaxValue(data.value_for_crack_generation);
                min_parameter_value = data.getMinValue(data.value_for_crack_generation);
                break;
            case "x_comp_of_stress_vector":
                max_parameter_value = data.getMaxValue(data.x_comp_of_stress_vector);
                min_parameter_value = data.getMinValue(data.x_comp_of_stress_vector);
                break;
            case "y_comp_of_stress_vector":
                max_parameter_value = data.getMaxValue(data.y_comp_of_stress_vector);
                min_parameter_value = data.getMinValue(data.y_comp_of_stress_vector);
                break;
            case "z_comp_of_stress_vector":
                max_parameter_value = data.getMaxValue(data.z_comp_of_stress_vector);
                min_parameter_value = data.getMinValue(data.z_comp_of_stress_vector);
                break;
            case "current_influx_of_spec_dissipated_energy":
                max_parameter_value = data.getMaxValue(data.current_influx_of_spec_dissipated_energy);
                min_parameter_value = data.getMinValue(data.current_influx_of_spec_dissipated_energy);
                break;
            case "specific_dissipated_energy":
                max_parameter_value = data.getMaxValue(data.specific_dissipated_energy);
                min_parameter_value = data.getMinValue(data.specific_dissipated_energy);
                break;
           
            /**
             * _jocl_torsion
             */
            case "abs_value_of_angle_velocity":
                max_parameter_value = data.getMaxValue(data.abs_value_of_angle_velocity);
                min_parameter_value = data.getMinValue(data.abs_value_of_angle_velocity);
                break;
            case "abs_value_of_torsion_angle":
                max_parameter_value = data.getMaxValue(data.abs_value_of_torsion_angle);
                min_parameter_value = data.getMinValue(data.abs_value_of_torsion_angle);
                break;
                
           /**
            * _inst_mom.res
            */
            case "x_component_of_inst_spec_force_moment":
                max_parameter_value = data.getMaxValue(data.x_component_of_inst_spec_force_moment);
                min_parameter_value = data.getMinValue(data.x_component_of_inst_spec_force_moment);
                break;
            case "y_component_of_inst_spec_force_moment":
                max_parameter_value = data.getMaxValue(data.y_component_of_inst_spec_force_moment);
                min_parameter_value = data.getMinValue(data.y_component_of_inst_spec_force_moment);
                break;
            case "z_component_of_inst_spec_force_moment":
                max_parameter_value = data.getMaxValue(data.z_component_of_inst_spec_force_moment);
                min_parameter_value = data.getMinValue(data.z_component_of_inst_spec_force_moment);
                break;
            case "current_influx_of_torsion_energy":
                max_parameter_value = data.getMaxValue(data.current_influx_of_torsion_energy);
                min_parameter_value = data.getMinValue(data.current_influx_of_torsion_energy);
                break;

           /**
            * torsion.res
            */
            case "x_component_of_angle_velocity":
                max_parameter_value = data.getMaxValue(data.x_component_of_angle_velocity);
                min_parameter_value = data.getMinValue(data.x_component_of_angle_velocity);
                break;
            case "y_component_of_angle_velocity":
                max_parameter_value = data.getMaxValue(data.y_component_of_angle_velocity);
                min_parameter_value = data.getMinValue(data.y_component_of_angle_velocity);
                break;
            case "z_component_of_angle_velocity":
                max_parameter_value = data.getMaxValue(data.z_component_of_angle_velocity);
                min_parameter_value = data.getMinValue(data.z_component_of_angle_velocity);
                break;
            case "x_component_of_torsion_angle_vector":
                max_parameter_value = data.getMaxValue(data.x_component_of_torsion_angle_vector);
                min_parameter_value = data.getMinValue(data.x_component_of_torsion_angle_vector);
                break;
            case "y_component_of_torsion_angle_vector":
                max_parameter_value = data.getMaxValue(data.y_component_of_torsion_angle_vector);
                min_parameter_value = data.getMinValue(data.y_component_of_torsion_angle_vector);
                break;
            case "z_component_of_torsion_angle_vector":
                max_parameter_value = data.getMaxValue(data.z_component_of_torsion_angle_vector);
                min_parameter_value = data.getMinValue(data.z_component_of_torsion_angle_vector);
                break;
            case "relative_power_of_torsion":
                max_parameter_value = data.getMaxValue(data.relative_power_of_torsion);
                min_parameter_value = data.getMinValue(data.relative_power_of_torsion);
                break;
            case "relative_torsion_energy":
                max_parameter_value = data.getMaxValue(data.relative_torsion_energy);
                min_parameter_value = data.getMinValue(data.relative_torsion_energy);
                break;            
           
            /**
            * jocl.res
            */            
            case "specific_elastic_energy":
                max_parameter_value = data.getMaxValue(data.specific_elastic_energy);
                min_parameter_value = data.getMinValue(data.specific_elastic_energy);
                break;
            case "principal_stresses":
                max_parameter_value = data.getMaxValue(data.principal_stresses);
                min_parameter_value = data.getMinValue(data.principal_stresses);
                break;
            case "x_component_of_specific_force_moment":
                max_parameter_value = data.getMaxValue(data.x_component_of_specific_force_moment);
                min_parameter_value = data.getMinValue(data.x_component_of_specific_force_moment);
                break;
            case "y_component_of_specific_force_moment":
                max_parameter_value = data.getMaxValue(data.y_component_of_specific_force_moment);
                min_parameter_value = data.getMinValue(data.y_component_of_specific_force_moment);
                break;
            case "z_component_of_specific_force_moment":
                max_parameter_value = data.getMaxValue(data.z_component_of_specific_force_moment);
                min_parameter_value = data.getMinValue(data.z_component_of_specific_force_moment);
                break;
            case "current_influx_of_specific_dissipated_energy":
                max_parameter_value = data.getMaxValue(data.current_influx_of_spec_dissipated_energy);
                min_parameter_value = data.getMinValue(data.current_influx_of_spec_dissipated_energy);
                break;            
                
           /**
            * .res
            */
            case "effective_stresses":
                max_parameter_value = data.getMaxValue(data.effective_stresses);
                min_parameter_value = data.getMinValue(data.effective_stresses);
                break;
            case "strain":
                max_parameter_value = data.getMaxValue(data.strain);
                min_parameter_value = data.getMinValue(data.strain);
                break;
            case "total_specific_torsion_energy":
                max_parameter_value = data.getMaxValue(data.total_specific_torsion_energy);
                min_parameter_value = data.getMinValue(data.total_specific_torsion_energy);
                break;
        }
    }
    
    private void setMaterialToCell(RecCAReadFiles data, String value, int i, Sphere cell, RecCAScale scale, String scale_option){
        switch (value){                    
            /**
             * relValues.res
             */
            case "relative_temperature":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.relative_temperature.get(i))));
                break;
            case "relative_specific_elastic_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.relative_specific_elastic_energy.get(i))));
                break;

            case "total_specific_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.total_specific_energy.get(i))));
                break;

            case "abs_value_of_X_comp_of_inst_spec_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.abs_value_of_X_comp_of_inst_spec_force_moment.get(i))));
                break;
            case "abs_value_of_Y_comp_of_inst_spec_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.abs_value_of_Y_comp_of_inst_spec_force_moment.get(i))));
                break;
            case "abs_value_of_Z_comp_of_inst_spec_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.abs_value_of_Z_comp_of_inst_spec_force_moment.get(i))));
                break;

            case "abs_value_of_inst_spec_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.abs_value_of_inst_spec_force_moment.get(i))));
                break;
            case "relative_specific_dissipated_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.relative_spec_dissipated_energy.get(i))));
                break; 

            /**
             * jocl_stresses.res
             */
            case "temperature":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.temperature.get(i))));
                break;
            case "abs_value_of_stress_vector":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.abs_value_of_stress_vector.get(i))));
                break;
            case "value_for_crack_generation":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.value_for_crack_generation.get(i))));
                break;
            case "x_comp_of_stress_vector":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.x_comp_of_stress_vector.get(i))));
                break;
            case "y_comp_of_stress_vector":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.y_comp_of_stress_vector.get(i))));
                break;
            case "z_comp_of_stress_vector":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.z_comp_of_stress_vector.get(i))));
                break;
            case "current_influx_of_spec_dissipated_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.current_influx_of_spec_dissipated_energy.get(i))));
                break;
            case "specific_dissipated_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.specific_dissipated_energy.get(i))));
                break;

            /**
             * _inst_mom.res
             */
            case "x_component_of_inst_spec_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.x_component_of_inst_spec_force_moment.get(i))));
                break;
            case "y_component_of_inst_spec_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.y_component_of_inst_spec_force_moment.get(i))));
                break;
            case "z_component_of_inst_spec_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.z_component_of_inst_spec_force_moment.get(i))));
                break;
            case "current_influx_of_torsion_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.current_influx_of_torsion_energy.get(i))));
                break;

            /**
             * torsion.res
             */                    
            case "x_component_of_angle_velocity":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.x_component_of_angle_velocity.get(i))));
                break;
            case "y_component_of_angle_velocity":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.y_component_of_angle_velocity.get(i))));
                break;
            case "z_component_of_angle_velocity":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.z_component_of_angle_velocity.get(i))));
                break;
            case "x_component_of_torsion_angle_vector":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.x_component_of_torsion_angle_vector.get(i))));
                break;
            case "y_component_of_torsion_angle_vector":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.y_component_of_torsion_angle_vector.get(i))));
                break;
            case "z_component_of_torsion_angle_vector":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.z_component_of_torsion_angle_vector.get(i))));
                break;
            case "relative_power_of_torsion":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.relative_power_of_torsion.get(i))));
                break;
            case "relative_torsion_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.relative_torsion_energy.get(i))));
                break;

            /**
             * jocl.res
             */
            case "specific_elastic_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.specific_elastic_energy.get(i))));
                break;
            case "principal_stresses":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.principal_stresses.get(i))));
                break;
            case "x_component_of_specific_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.x_component_of_specific_force_moment.get(i))));
                break;
            case "y_component_of_specific_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.y_component_of_specific_force_moment.get(i))));
                break;
            case "z_component_of_specific_force_moment":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.z_component_of_specific_force_moment.get(i))));
                break;
            case "current_influx_of_specific_dissipated_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.current_influx_of_spec_dissipated_energy.get(i))));
                break;                    

            /**
             * .res
             */
            case "effective_stresses":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.effective_stresses.get(i))));
                break;
            case "strain":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.strain.get(i))));
                break;
            case "total_specific_torsion_energy":
                cell.setMaterial(new PhongMaterial(scale.getColorForScale(scale_option, data.total_specific_torsion_energy.get(i))));
                break;                                                
        }
    }
}
