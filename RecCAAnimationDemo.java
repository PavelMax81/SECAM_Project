/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;
/**
 *
 * @author DmitryB
 */
public class RecCAAnimationDemo{
    
    public RecCAAnimationDemo(RecCA_3DPictureCreator pict){
        
        pict.elementsGroup.getChildren().clear();
        
        Sphere cell = new Sphere(40.0);
        pict.elementsGroup.getChildren().add(cell);
        
        
        cell.setMaterial(new PhongMaterial(Color.RED));
        
        List<PhongMaterial> materials = new ArrayList<>();
        materials.add(new PhongMaterial(Color.BLUE));
        materials.add(new PhongMaterial(Color.BLACK));
        materials.add(new PhongMaterial(Color.WHITE));
        materials.add(new PhongMaterial(Color.GREEN));
        
        AnimationTimer timer = new AnimationTimer(){
            @Override
            public void handle(long l) {
               materials.forEach(mat -> cell.setMaterial(mat));
            }
        };
        timer.start();
        
    }
    
}
