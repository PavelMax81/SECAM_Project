/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import paral_calc.UICommon;

/**
 *
 * @author dmitryb
 */
public class SavingDialog extends Stage{
    
    Button save_btn, cancel_btn;
    Scene scene;
    VBox layout;
    
    public SavingDialog(TextField file_name){
        
        save_btn = new Button(UICommon.BUTTON_SAVE_NAME);
        cancel_btn = new Button(UICommon.BUTTON_CANCEL_NAME);
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 2, 10, 2));
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setPadding(new Insets(0, 5, 0, 5));
        
        HBox bottom_layout = new HBox(save_btn, sep1, cancel_btn);
        bottom_layout.setAlignment(Pos.CENTER);
        
        layout = new VBox(file_name, sep, bottom_layout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(7, 7, 7, 7));
        
        scene = new Scene(layout);
        this.setTitle("Save File");
        this.setScene(scene);
        this.show();
        
        cancel_btn.setOnAction(e -> this.close());
    }
    
}
