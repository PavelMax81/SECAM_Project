/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author dmitryb
 */
public class Arrow extends Xform{
    
    public Xform layout = new Xform();
    
    public Arrow(){
        Box body = new Box(40, 1, 1);
        body.setMaterial(new PhongMaterial(Color.YELLOW));
        
        Box left_tip = new Box(5, 1, 1);
        left_tip.setRotate(-40.0d);
        left_tip.setTranslateX(17.5);
        left_tip.setTranslateY(1.5);
        left_tip.setMaterial(new PhongMaterial(Color.YELLOW));
        
        Box right_tip = new Box(5, 1, 1);
        right_tip.setTranslateX(17.5);
        right_tip.setTranslateY(-1.5);
        right_tip.setRotate(40.0d);
        right_tip.setMaterial(new PhongMaterial(Color.YELLOW));
        
        layout.getChildren().addAll(body, left_tip, right_tip);
    }

}
