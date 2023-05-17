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
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

/**
 *
 * @author DmitryB
 */
public class InitialCondTableFX extends GridPane{
    
    Label layer1, layer2, layer3, layer4, layer5,
          temp1, thermal_energy1, mech_energy1,
          temp2, thermal_energy2, mech_energy2,
          temp3, thermal_energy3, mech_energy3,
          temp4, thermal_energy4, mech_energy4,
          temp5, thermal_energy5, mech_energy5;
          
    UIInitialCondition cond;
    
    public InitialCondTableFX(UIInitialCondition ui_init_cond) {
        cond = ui_init_cond;
        
        initAllElements();
        addAllElements();
        
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5,5,5,5));        
    }
    
    private void initAllElements(){
        
        layer1 = new Label("#1 Layer");
        layer2 = new Label("#2 Layer");
        layer3 = new Label("#3 Layer");
        layer4 = new Label("#4 Layer");
        layer5 = new Label("#5 Layer");
        
        temp1 = new Label("Temperature = " + String.valueOf(cond.first_temperature));
        thermal_energy1 = new Label("Thermal Energy = " + String.valueOf(cond.first_thermal_energy));
        mech_energy1 = new Label("Mechanical Energy = " + String.valueOf(cond.first_mechanical_energy));
        
        temp2 = new Label("Temperature = " + String.valueOf(cond.second_temperature));
        thermal_energy2 = new Label("Thermal Energy = " + String.valueOf(cond.second_thermal_energy));
        mech_energy2 = new Label("Mechanical Energy = " + String.valueOf(cond.second_mechanical_energy));
        
        temp3 = new Label("Temperature = " + String.valueOf(cond.third_temperature));
        thermal_energy3 = new Label("Thermal Energy = " + String.valueOf(cond.third_temperature));
        mech_energy3 = new Label("Mechanical Energy = " + String.valueOf(cond.third_mechanical_energy));
        
        temp4 = new Label("Temperature = " + String.valueOf(cond.fourth_temperature));
        thermal_energy4 = new Label("Thermal Energy = " + String.valueOf(cond.fourth_thermal_energy));
        mech_energy4 = new Label("Mechanical Energy = " + String.valueOf(cond.fourth_mechanical_energy));
        
        temp5 = new Label("Temperature = " + String.valueOf(cond.fifth_temperature));
        thermal_energy5 = new Label("Thermal Energy = " + String.valueOf(cond.fifth_thermal_energy));
        mech_energy5 = new Label("Mechanical Energy = " + String.valueOf(cond.fifth_mechanical_energy));
                
    }
    
    private void addAllElements(){
        this.setHgap(8.0d);
        this.setVgap(5.0d);
        
        Separator s1 = new Separator();
        Separator s2 = new Separator();
        Separator s3 = new Separator();
        Separator s4 = new Separator();
        
        Separator hs1 = new Separator(Orientation.VERTICAL);
        Separator hs2 = new Separator(Orientation.VERTICAL);
        Separator hs3 = new Separator(Orientation.VERTICAL);
        Separator hs4 = new Separator(Orientation.VERTICAL);
        Separator hs5 = new Separator(Orientation.VERTICAL);
        
        GridPane.setConstraints(layer1, 0, 0, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(hs1, 1, 0, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(temp1, 2, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(thermal_energy1, 2, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(mech_energy1, 2, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s1, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);
        
        
        GridPane.setConstraints(layer2, 0, 4, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(hs2, 1, 4, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(temp2, 2, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(thermal_energy2, 2, 5, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(mech_energy2, 2, 6, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s2, 0, 7, 3, 1, HPos.CENTER, VPos.CENTER);
        
        
        GridPane.setConstraints(layer3, 0, 8, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(hs3, 1, 8, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(temp3, 2, 8, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(thermal_energy3, 2, 9, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(mech_energy3, 2, 10, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s3, 0, 11, 3, 1, HPos.CENTER, VPos.CENTER);
        
        
        GridPane.setConstraints(layer4, 0, 12, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(hs4, 1, 12, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(temp4, 2, 12, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(thermal_energy4, 2, 13, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(mech_energy4, 2, 14, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s4, 0, 15, 3, 1, HPos.CENTER, VPos.CENTER);
        
        
        GridPane.setConstraints(layer5, 0, 16, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(hs5, 1, 16, 1, 3, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(temp5, 2, 16, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(thermal_energy5, 2, 17, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(mech_energy5, 2, 18, 1, 1, HPos.LEFT, VPos.CENTER);
        
        
        this.getChildren().addAll(
                s1, s2, s3, s4, hs1, hs2, hs3, hs4, hs5,
                layer1, layer2, layer3, layer4, layer5,
                temp1, thermal_energy1, mech_energy1,
                temp2, thermal_energy2, mech_energy2,
                temp3, thermal_energy3, mech_energy3,
                temp4, thermal_energy4, mech_energy4,
                temp5, thermal_energy5, mech_energy5
        );
    }
}
