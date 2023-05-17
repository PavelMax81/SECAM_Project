/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import interf.RecCASaveImage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author DmitryB
 */
public class RecCAChartFullScreenMode extends Stage{
    
    Node node;
    
    public RecCAChartFullScreenMode(Node node){
        
        this.node = node; 
        
        ImageView image = new ImageView(node.snapshot(new SnapshotParameters(), null));
        image.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight()*0.95);
        image.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth()*0.95);
        Scene scene = new Scene(new StackPane(image));
        setScene(scene);
        setFullScreen(true);
        show();
        
    }
    
    
}
