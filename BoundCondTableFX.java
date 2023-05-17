/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import static javafx.geometry.Pos.CENTER_LEFT;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import util.Common;

/**
 *
 * @author DmitryB
 */
public class BoundCondTableFX extends GridPane{
    
    UIBoundaryCondition cond;
    UIInterface ui;
    
    Label bound_cond_dir, first_dir, second_dir,
          top, left, right, back, front, bottom,
          top_mech_load, top_therm_influx, top_is_adiabatic,
          left_mech_load, left_therm_influx, left_is_adiabatic,
          right_mech_load, right_therm_influx, right_is_adiabatic,
          bottom_mech_load, bottom_therm_influx, bottom_is_adiabatic,
          front_mech_load, front_therm_influx, front_is_adiabatic,
          back_mech_load, back_therm_influx, back_is_adiabatic,
          bound_type, boun_func_type;
    
    public BoundCondTableFX(UIBoundaryCondition ui_bound_cond, UIInterface ui_interface) {
        cond = ui_bound_cond;
        ui = ui_interface;
        
        initAllElements();
        addAllElements();
        
        this.setPadding(new Insets(5,5,5,5));
        this.setAlignment(CENTER_LEFT);
        this.setHgap(3.0d);
        this.setVgap(3.0d);
    }
    
    private void initAllElements(){
        
        bound_cond_dir = new Label("Boundary Conditions Directory");
        
        first_dir = new Label("First File: " + Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"."+Common.BOUND_COND_EXTENSION);
        second_dir = new Label("Second File: " + Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getBoundCondPath()+"_grains."+Common.BOUND_COND_EXTENSION);
        
        top = new Label("TOP");
        left = new Label("LEFT");
        right = new Label("RIGHT");
        bottom = new Label("BOTTOM");
        front = new Label("FRONT");
        back = new Label("BACK");
        
        
        top_mech_load = new Label();
        
        if(cond.getTopCheckMechanicalLoading()){
            if(cond.getTopStress())                
                top_mech_load.setText("Mechanical Loading " + " (stress) = "+new Double(cond.getTopMechanicalLoading()).toString()+" ");
            else if(cond.getTopStrain())
                top_mech_load.setText("Mechanical Loading " + " (strain) = "+new Double(cond.getTopMechanicalLoading()).toString()+" ");
        }
        else top_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        top_therm_influx = new Label();
        
        if(cond.getTopCheckThermalInflux()){
            if(cond.getTopTemperature())
                top_therm_influx.setText("Thermal Influx " + " (temperature) = "+new Double(cond.getTopThermalInflux()).toString()+" ");
            else if(cond.getTopEnergy())
                top_therm_influx.setText("Thermal Influx " + " (energy) = "+new Double(cond.getTopThermalInflux()).toString()+" ");
        }
        else top_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        top_is_adiabatic = new Label();
        
        if(cond.getTopCheckMechanicalLoading()|cond.getTopCheckThermalInflux())
            top_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else
            top_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        left_mech_load = new Label();
        
        if(cond.getLeftCheckMechanicalLoading()){
            if(cond.getLeftStress())                
                left_mech_load.setText("Mechanical Loading " + " (stress) = "+new Double(cond.getLeftMechanicalLoading()).toString()+" ");
            else if(cond.getLeftStrain())
                left_mech_load.setText("Mechanical Loading " + " (strain) = "+new Double(cond.getLeftMechanicalLoading()).toString()+" ");
        }
        else left_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        left_therm_influx = new Label();
        
        if(cond.getLeftCheckThermalInflux()){
            if(cond.getLeftTemperature())
                left_therm_influx.setText("Thermal Influx " + " (temperature) = "+new Double(cond.getLeftThermalInflux()).toString()+" ");
            else if(cond.getLeftEnergy())
                left_therm_influx.setText("Thermal Influx " + " (energy) = "+new Double(cond.getLeftThermalInflux()).toString()+" ");
        }
        else left_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        left_is_adiabatic = new Label();
        
        if(cond.getLeftCheckMechanicalLoading()|cond.getLeftCheckThermalInflux())
            left_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else
            left_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        right_mech_load = new Label();
        
        if(cond.getRightCheckMechanicalLoading()){
            if(cond.getRightStress())                
                right_mech_load.setText("Mechanical Loading " + " (stress) = "+new Double(cond.getRightMechanicalLoading()).toString()+" ");
            else if(cond.getRightStrain())
                right_mech_load.setText("Mechanical Loading " + " (strain) = "+new Double(cond.getRightMechanicalLoading()).toString()+" ");
        }
        else right_mech_load.setText("Mechanical Loading " + new Double("0.0").toString()+" ");
        
        right_therm_influx = new Label();
        
        if(cond.getRightCheckThermalInflux()){
            if(cond.getRightTemperature())
                right_therm_influx.setText("Thermal Influx " + " (temperature) = "+new Double(cond.getRightThermalInflux()).toString()+" ");
            else if(cond.getRightEnergy())
                right_therm_influx.setText("Thermal Influx " + " (energy) = "+new Double(cond.getRightThermalInflux()).toString()+" ");
        }
        else right_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        right_is_adiabatic = new Label();
        
        if(cond.getRightCheckMechanicalLoading()|cond.getRightCheckThermalInflux())
            right_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else
            right_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        bottom_mech_load = new Label();
        
        if(cond.getBottomCheckMechanicalLoading()){
            if(cond.getBottomStress())                
                bottom_mech_load.setText("Mechanical Loading " + " (stress) = "+new Double(cond.getBottomMechanicalLoading()).toString()+" ");
            else if(cond.getBottomStrain())
                bottom_mech_load.setText("Mechanical Loading " + " (strain) = "+new Double(cond.getBottomMechanicalLoading()).toString()+" ");
        }
        else bottom_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        bottom_therm_influx = new Label();
        
        if(cond.getBottomCheckThermalInflux()){
            if(cond.getBottomTemperature())
                bottom_therm_influx.setText("Thermal Influx " + " (temperature) = "+new Double(cond.getBottomThermalInflux()).toString()+" ");
            else if(cond.getBottomEnergy())
                bottom_therm_influx.setText("Thermal Influx " + " (energy) = "+new Double(cond.getBottomThermalInflux()).toString()+" ");
        }
        else bottom_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        bottom_is_adiabatic = new Label();
        
        if(cond.getBottomCheckMechanicalLoading()|cond.getBottomCheckThermalInflux())
            bottom_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else
            bottom_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        front_mech_load = new Label();
        
        if(cond.getFrontCheckMechanicalLoading()){
            if(cond.getFrontStress())                
                front_mech_load.setText("Mechanical Loading " + " (stress) = "+new Double(cond.getFrontMechanicalLoading()).toString()+" ");
            else if(cond.getFrontStrain())
                front_mech_load.setText("Mechanical Loading " + " (strain) = "+new Double(cond.getFrontMechanicalLoading()).toString()+" ");
        }
        else front_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        front_therm_influx = new Label();
        
        if(cond.getFrontCheckThermalInflux()){
            if(cond.getFrontTemperature())
                front_therm_influx.setText("Thermal Influx " + " (temperature) = "+new Double(cond.getFrontThermalInflux()).toString()+" ");
            else if(cond.getFrontEnergy())
                front_therm_influx.setText("Thermal Influx " + " (energy) = "+new Double(cond.getFrontThermalInflux()).toString()+" ");
        }
        else front_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        front_is_adiabatic = new Label();
        
        if(cond.getFrontCheckMechanicalLoading()|cond.getFrontCheckThermalInflux())
            front_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else
            front_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        back_mech_load = new Label();
        
        if(cond.getBackCheckMechanicalLoading()){
            if(cond.getBackStress())                
                back_mech_load.setText("Mechanical Loading " + " (stress) = "+new Double(cond.getBackMechanicalLoading()).toString()+" ");
            else if(cond.getBackStrain())
                back_mech_load.setText("Mechanical Loading " + " (strain) = "+new Double(cond.getBackMechanicalLoading()).toString()+" ");
        }
        else back_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        back_therm_influx = new Label();
        
        if(cond.getBackCheckThermalInflux()){
            if(cond.getBackTemperature())
                back_therm_influx.setText("Thermal Influx " + " (temperature) = "+new Double(cond.getBackThermalInflux()).toString()+" ");
            else if(cond.getBackEnergy())
                back_therm_influx.setText("Thermal Influx " + " (energy) = "+new Double(cond.getBackThermalInflux()).toString()+" ");
        }
        else back_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        back_is_adiabatic = new Label();
        
        if(cond.getBackCheckMechanicalLoading()|cond.getBackCheckThermalInflux())
            back_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else
            back_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        bound_type = new Label();
        
        if(cond.getBoundaryType() == 0) 
            bound_type.setText("Boundary Type: 0 - boundary cell does not change mechanical energy\n because of interaction with neighbours.");
        else if(cond.getBoundaryType() == 1) 
            bound_type.setText("Boundary Type: 1 - boundary cell changes mechanical energy\n because of interaction with inner neighbour cells only.");
        else if(cond.getBoundaryType() == 2) 
            bound_type.setText("Boundary Type: 2 - boundary cell changes mechanical energy\n because of interaction with each neighbour cell.");
        
        
        boun_func_type = new Label("Boundary Function Type = " + String.valueOf(cond.getBoundaryFunctionType()));
                
    }
    
    private void addAllElements(){
        Separator sep = new Separator();
        sep.setPadding(new Insets(5, 0, 5, 0));

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();
        Separator sep3 = new Separator();
        Separator sep4 = new Separator();
        Separator sep5 = new Separator();
        
        Separator sep6 = new Separator();
        sep6.setPadding(new Insets(5, 0, 5, 0));
        
        GridPane.setConstraints(bound_cond_dir, 0, 0, 3, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(first_dir, 0, 1, 4, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(second_dir, 0, 2, 4, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep, 0, 3, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(top, 0, 4, 1, 3, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_mech_load, 1, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(top_therm_influx, 1, 5, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(top_is_adiabatic, 1, 6, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 7, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(left, 0, 8, 1, 3, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_mech_load, 1, 8, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(left_therm_influx, 1, 9, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(left_is_adiabatic, 1, 10, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 0, 11, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(right, 0, 12, 1, 3, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_mech_load, 1, 12, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(right_therm_influx, 1, 13, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(right_is_adiabatic, 1, 14, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep3, 0, 15, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bottom, 0, 16, 1, 3, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_mech_load, 1, 16, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(bottom_therm_influx, 1, 17, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(bottom_is_adiabatic, 1, 18, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep4, 0, 19, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(front, 0, 20, 1, 3, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_mech_load, 1, 20, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(front_therm_influx, 1, 21, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(front_is_adiabatic, 1, 22, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep5, 0, 23, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(back, 0, 24, 1, 3, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_mech_load, 1, 24, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(back_therm_influx, 1, 25, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(back_is_adiabatic, 1, 26, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep6, 0, 27, 3, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bound_type, 0, 28, 4, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(boun_func_type, 0, 29, 4, 1, HPos.LEFT, VPos.CENTER);
        
        this.getChildren().addAll(
                sep, sep1, sep2, sep3, sep4, sep5, sep6,
                bound_cond_dir, first_dir, second_dir,
                top, left, right, back, front, bottom,
                top_mech_load, top_therm_influx, top_is_adiabatic,
                left_mech_load, left_therm_influx, left_is_adiabatic,
                right_mech_load, right_therm_influx, right_is_adiabatic,
                bottom_mech_load, bottom_therm_influx, bottom_is_adiabatic,
                front_mech_load, front_therm_influx, front_is_adiabatic,
                back_mech_load, back_therm_influx, back_is_adiabatic,
                bound_type, boun_func_type
        );
    }
    
}
