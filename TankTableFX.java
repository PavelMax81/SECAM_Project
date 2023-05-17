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
import util.Common;

/**
 *
 * @author DmitryB
 */
public class TankTableFX extends GridPane{
    
    UITank tank;
    UIInterface ui;
    
    Label file_dir, top, top_mat, top_x1, top_x2, top_y1, top_y2, top_z1, top_z2,
          left, left_mat, left_x1, left_x2, left_y1, left_y2, left_z1, left_z2,
          right, right_mat, right_x1, right_x2, right_y1, right_y2, right_z1, right_z2,
          bottom, bottom_mat, bottom_x1, bottom_x2, bottom_y1, bottom_y2, bottom_z1, bottom_z2,
          front, front_mat, front_x1, front_x2, front_y1, front_y2, front_z1, front_z2,
          back, back_mat, back_x1, back_x2, back_y1, back_y2, back_z1, back_z2,
          top_mech_load, top_therm_influx, top_is_adiabatic,
          left_mech_load, left_therm_influx, left_is_adiabatic,
          right_mech_load, right_therm_influx, right_is_adiabatic,
          bottom_mech_load, bottom_therm_influx,  bottom_is_adiabatic,
          front_mech_load, front_therm_influx, front_is_adiabatic,
          back_mech_load, back_therm_influx, back_is_adiabatic;
    
    public TankTableFX(UITank ui_tank, UIInterface ui_interface) {
        tank = ui_tank;
        ui = ui_interface;
        
        initAllElements();
        addAllElements();
        
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5, 5, 5, 5));
//        this.setStyle("-fx-font-size: 16px");
    }
    
    private void initAllElements(){
        
        file_dir = new Label("File: " + Common.BOUND_COND_PATH+"/"+ui.getSpecimenPath()+Common.BOUND_COND_NAME+"/"+ui.getTankPath()+"."+Common.TANK_EXTENSION);
        
        top = new Label("TOP");
        
        top_mat = new Label("Material: " + tank.getTopAdiabaticMaterial());
        
        top_x1 = new Label("x1 : " + String.valueOf(tank.getTopCoordinateX1()));
        top_x2 = new Label("x2 : " + String.valueOf(tank.getTopCoordinateX2()));
        top_y1 = new Label("y1 : " + String.valueOf(tank.getTopCoordinateY1()));
        top_y2 = new Label("y2 : " + String.valueOf(tank.getTopCoordinateY2()));
        top_z1 = new Label("z1 : " + String.valueOf(tank.getTopCoordinateZ1()));
        top_z2 = new Label("z2 : " + String.valueOf(tank.getTopCoordinateZ2()));
        
        
        top_mech_load = new Label();
        
        if(tank.getTopCheckMechanicalLoading()){
            if(tank.getTopStress())
                top_mech_load.setText("Mechanical Loading " + "(stress) = " + new Double(tank.getTopMechValue()).toString()+" ");
            else if(tank.getTopStrain())
                top_mech_load.setText("Mechanical Loading " + " (strain) = " + new Double(tank.getTopMechValue()).toString()+" ");
        }
        else
            top_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        top_therm_influx = new Label();
        
        if(tank.getTopCheckThermalInflux()){
            if(tank.getTopTemperature())
                top_therm_influx.setText("Thermal Influx " + " (temperature) = " + new Double(tank.getTopThermValue()).toString()+" ");
            else if(tank.getTopEnergy())
                top_therm_influx.setText("Thermal Influx " + " (energy) = " + new Double(tank.getTopThermValue()).toString()+" ");
        }
        else
            top_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        top_is_adiabatic = new Label();
        
        if(tank.getTopCheckMechanicalLoading() | tank.getTopCheckThermalInflux())
            top_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else 
            top_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        left = new Label("LEFT");
        
        left_mat = new Label("Material: " + tank.getLeftAdiabaticMaterial());
        
        left_x1 = new Label("x1 : " + String.valueOf(tank.getLeftCoordinateX1()));
        left_x2 = new Label("x2 : " + String.valueOf(tank.getLeftCoordinateX2()));
        left_y1 = new Label("y1 : " + String.valueOf(tank.getLeftCoordinateY1()));
        left_y2 = new Label("y2 : " + String.valueOf(tank.getLeftCoordinateY2()));
        left_z1 = new Label("z1 : " + String.valueOf(tank.getLeftCoordinateZ1()));
        left_z2 = new Label("z2 : " + String.valueOf(tank.getLeftCoordinateZ2()));
        
        
        left_mech_load = new Label();
        
        if(tank.getLeftCheckMechanicalLoading()){
            if(tank.getLeftStress())
                left_mech_load.setText("Mechanical Loading " + "(stress) = " + new Double(tank.getLeftMechValue()).toString()+" ");
            else if(tank.getLeftStrain())
                left_mech_load.setText("Mechanical Loading " + " (strain) = " + new Double(tank.getLeftMechValue()).toString()+" ");
        }
        else
            left_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        left_therm_influx = new Label();
        
        if(tank.getLeftCheckThermalInflux()){
            if(tank.getLeftTemperature())
                left_therm_influx.setText("Thermal Influx " + " (temperature) = " + new Double(tank.getLeftThermValue()).toString()+" ");
            else if(tank.getLeftEnergy())
                left_therm_influx.setText("Thermal Influx " + " (energy) = " + new Double(tank.getLeftThermValue()).toString()+" ");
        }
        else
            left_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        left_is_adiabatic = new Label();
        
        if(tank.getLeftCheckMechanicalLoading() | tank.getLeftCheckThermalInflux())
            left_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else 
            left_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        right = new Label("RIGHT");
        
        right_mat = new Label("Material: " + tank.getRightAdiabaticMaterial());
        
        right_x1 = new Label("x1 : " + tank.getRightCoordinateX1());
        right_x2 = new Label("x2 : " + tank.getRightCoordinateX2());
        right_y1 = new Label("y1 : " + tank.getRightCoordinateY1());
        right_y2 = new Label("y2 : " + tank.getRightCoordinateY2());
        right_z1 = new Label("z1 : " + tank.getRightCoordinateZ1());
        right_z2 = new Label("z2 : " + tank.getRightCoordinateZ2());
        
        right_mech_load = new Label();
        
        if(tank.getRightCheckMechanicalLoading()){
            if(tank.getRightStress())
                right_mech_load.setText("Mechanical Loading " + "(stress) = " + new Double(tank.getRightMechValue()).toString()+" ");
            else if(tank.getRightStrain())
                right_mech_load.setText("Mechanical Loading " + " (strain) = " + new Double(tank.getRightMechValue()).toString()+" ");
        }
        else
            right_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        right_therm_influx = new Label();
        
        if(tank.getRightCheckThermalInflux()){
            if(tank.getRightTemperature())
                right_therm_influx.setText("Thermal Influx " + " (temperature) = " + new Double(tank.getRightThermValue()).toString()+" ");
            else if(tank.getRightEnergy())
                right_therm_influx.setText("Thermal Influx " + " (energy) = " + new Double(tank.getRightThermValue()).toString()+" ");
        }
        else
            right_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        right_is_adiabatic = new Label();
        
        if(tank.getRightCheckMechanicalLoading() | tank.getRightCheckThermalInflux())
            right_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else 
            right_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        bottom = new Label("BOTTOM");
        
        bottom_mat = new Label("Material:" + tank.getBottomAdiabaticMaterial());
        
        bottom_x1 = new Label("x1 : " + String.valueOf(tank.getBottomCoordinateX1()));
        bottom_x2 = new Label("x2 : " + String.valueOf(tank.getBottomCoordinateX2()));
        bottom_y1 = new Label("y1 : " + String.valueOf(tank.getBottomCoordinateY1()));
        bottom_y2 = new Label("y2 : " + String.valueOf(tank.getBottomCoordinateY2()));
        bottom_z1 = new Label("z1 : " + String.valueOf(tank.getBottomCoordinateZ1()));
        bottom_z2 = new Label("z2 : " + String.valueOf(tank.getBottomCoordinateZ2()));
        
        bottom_mech_load = new Label();
        
        if(tank.getBottomCheckMechanicalLoading()){
            if(tank.getBottomStress())
                bottom_mech_load.setText("Mechanical Loading " + "(stress) = " + new Double(tank.getBottomMechValue()).toString()+" ");
            else if(tank.getBottomStrain())
                bottom_mech_load.setText("Mechanical Loading " + " (strain) = " + new Double(tank.getBottomMechValue()).toString()+" ");
        }
        else
            bottom_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        bottom_therm_influx = new Label();
        
        if(tank.getBottomCheckThermalInflux()){
            if(tank.getBottomTemperature())
                bottom_therm_influx.setText("Thermal Influx " + " (temperature) = " + new Double(tank.getBottomThermValue()).toString()+" ");
            else if(tank.getBottomEnergy())
                bottom_therm_influx.setText("Thermal Influx " + " (energy) = " + new Double(tank.getBottomThermValue()).toString()+" ");
        }
        else
            bottom_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        bottom_is_adiabatic = new Label();
        
        if(tank.getBottomCheckMechanicalLoading() | tank.getBottomCheckThermalInflux())
            bottom_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else 
            bottom_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        front = new Label("FRONT");
        
        front_mat = new Label("Material: " + tank.getFrontAdiabaticMaterial());
        
        front_x1 = new Label("x1 : " + String.valueOf(tank.getFrontCoordinateX1()));
        front_x2 = new Label("x2 : " + String.valueOf(tank.getFrontCoordinateX2()));
        front_y1 = new Label("y1 : " + String.valueOf(tank.getFrontCoordinateY1()));
        front_y2 = new Label("y2 : " + String.valueOf(tank.getFrontCoordinateY2()));
        front_z1 = new Label("z1 : " + String.valueOf(tank.getFrontCoordinateZ1()));
        front_z2 = new Label("z2 : " + String.valueOf(tank.getFrontCoordinateZ2()));
        
        front_mech_load = new Label();
        
        if(tank.getFrontCheckMechanicalLoading()){
            if(tank.getFrontStress())
                front_mech_load.setText("Mechanical Loading " + "(stress) = " + new Double(tank.getFrontMechValue()).toString()+" ");
            else if(tank.getFrontStrain())
                front_mech_load.setText("Mechanical Loading " + " (strain) = " + new Double(tank.getFrontMechValue()).toString()+" ");
        }
        else
            front_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        front_therm_influx = new Label();
        
        if(tank.getFrontCheckThermalInflux()){
            if(tank.getFrontTemperature())
                front_therm_influx.setText("Thermal Influx " + " (temperature) = " + new Double(tank.getFrontThermValue()).toString()+" ");
            else if(tank.getFrontEnergy())
                front_therm_influx.setText("Thermal Influx " + " (energy) = " + new Double(tank.getFrontThermValue()).toString()+" ");
        }
        else
            front_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        front_is_adiabatic = new Label();
        
        if(tank.getFrontCheckMechanicalLoading() | tank.getFrontCheckThermalInflux())
            front_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else 
            front_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
        
        
        back = new Label("BACK");
        
        back_mat = new Label("Material: " + tank.getBackAdiabaticMaterial());
        
        back_x1 = new Label("x1 : " + String.valueOf(tank.getBackCoordinateX1()));
        back_x2 = new Label("x2 : " + String.valueOf(tank.getBackCoordinateX2()));
        back_y1 = new Label("y1 : " + String.valueOf(tank.getBackCoordinateY1()));
        back_y2 = new Label("y2 : " + String.valueOf(tank.getBackCoordinateY2()));
        back_z1 = new Label("z1 : " + String.valueOf(tank.getBackCoordinateZ1()));
        back_z2 = new Label("z2 : " + String.valueOf(tank.getBackCoordinateZ2()));
        
        back_mech_load = new Label();
        
        if(tank.getBackCheckMechanicalLoading()){
            if(tank.getBackStress())
                back_mech_load.setText("Mechanical Loading " + "(stress) = " + new Double(tank.getBackMechValue()).toString()+" ");
            else if(tank.getBackStrain())
                back_mech_load.setText("Mechanical Loading " + " (strain) = " + new Double(tank.getBackMechValue()).toString()+" ");
        }
        else
            back_mech_load.setText("Mechanical Loading = " + new Double("0.0").toString()+" ");
        
        back_therm_influx = new Label();
        
        if(tank.getBackCheckThermalInflux()){
            if(tank.getBackTemperature())
                back_therm_influx.setText("Thermal Influx " + " (temperature) = " + new Double(tank.getBackThermValue()).toString()+" ");
            else if(tank.getBackEnergy())
                back_therm_influx.setText("Thermal Influx " + " (energy) = " + new Double(tank.getBackThermValue()).toString()+" ");
        }
        else
            back_therm_influx.setText("Thermal Influx = " + new Double("0.0").toString()+" ");
        
        back_is_adiabatic = new Label();
        
        if(tank.getBackCheckMechanicalLoading() | tank.getBackCheckThermalInflux())
            back_is_adiabatic.setText(UICommon.ADIOBATIC_NOR_PROPERTIES);
        else 
            back_is_adiabatic.setText(UICommon.ADIOBATIC_PROPERTIES);
    }
    
    private void addAllElements(){
        this.setHgap(8.0d);
        this.setVgap(3.0d);
        
        Separator s1 = new Separator();
        s1.setPadding(new Insets(5, 0, 5, 0));
        Separator s2 = new Separator();
        Separator s3 = new Separator();
        Separator s4 = new Separator();
        Separator s5 = new Separator();
        Separator s6 = new Separator();
        Separator s7 = new Separator();
        Separator s8 = new Separator();
        Separator s9 = new Separator();
        Separator s10 = new Separator();
        Separator s11 = new Separator();
        Separator s12 = new Separator();
        
        Separator hs1 = new Separator(Orientation.VERTICAL);
        Separator hs2 = new Separator(Orientation.VERTICAL);
        Separator hs3 = new Separator(Orientation.VERTICAL);
        Separator hs4 = new Separator(Orientation.VERTICAL);
        Separator hs5 = new Separator(Orientation.VERTICAL);
        Separator hs6 = new Separator(Orientation.VERTICAL);
        Separator hs7 = new Separator(Orientation.VERTICAL);
        Separator hs8 = new Separator(Orientation.VERTICAL);
        Separator hs9 = new Separator(Orientation.VERTICAL);
        Separator hs10 = new Separator(Orientation.VERTICAL);
        Separator hs11 = new Separator(Orientation.VERTICAL);
        Separator hs12 = new Separator(Orientation.VERTICAL);
        
        GridPane.setConstraints(file_dir, 0, 0, 10, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s1, 0, 1, 6, 1, HPos.CENTER, VPos.CENTER);        
        
        GridPane.setConstraints(top, 0, 2, 1, 7, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(top_x1, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_x2, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs1, 2, 2, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_y1, 3, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_y2, 3, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs2, 4, 2, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_z1, 5, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(top_z2, 5, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(s2, 1, 4, 5, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(top_mat, 1, 5, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(top_mech_load, 1, 6, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(top_therm_influx, 1, 7, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(top_is_adiabatic, 1, 8, 5, 1, HPos.LEFT, VPos.CENTER);
        
        //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
        
        GridPane.setConstraints(s3, 0, 9, 6, 1, HPos.CENTER, VPos.CENTER);        
        
        GridPane.setConstraints(left, 0, 10, 1, 7, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(left_x1, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_x2, 1, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs3, 2, 10, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_y1, 3, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_y2, 3, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs4, 4, 10, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_z1, 5, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(left_z2, 5, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(s4, 1, 12, 5, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(left_mat, 1, 13, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(left_mech_load, 1, 14, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(left_therm_influx, 1, 15, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(left_is_adiabatic, 1, 16, 5, 1, HPos.LEFT, VPos.CENTER);
        
        //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
        
        GridPane.setConstraints(s5, 0, 17, 6, 1, HPos.CENTER, VPos.CENTER);        
        
        GridPane.setConstraints(right, 0, 18, 1, 7, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(right_x1, 1, 18, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_x2, 1, 19, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs5, 2, 18, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_y1, 3, 18, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_y2, 3, 19, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs6, 4, 18, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_z1, 5, 18, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(right_z2, 5, 19, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(s6, 1, 20, 5, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(right_mat, 1, 21, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(right_mech_load, 1, 22, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(right_therm_influx, 1, 23, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(right_is_adiabatic, 1, 24, 5, 1, HPos.LEFT, VPos.CENTER);
        
        //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
        
        GridPane.setConstraints(s7, 0, 25, 6, 1, HPos.CENTER, VPos.CENTER);        
        
        GridPane.setConstraints(bottom, 0, 26, 1, 7, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bottom_x1, 1, 26, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_x2, 1, 27, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs7, 2, 26, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_y1, 3, 26, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_y2, 3, 27, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs8, 4, 26, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_z1, 5, 26, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(bottom_z2, 5, 27, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(s8, 1, 28, 5, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(bottom_mat, 1, 29, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(bottom_mech_load, 1, 30, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(bottom_therm_influx, 1, 31, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(bottom_is_adiabatic, 1, 32, 5, 1, HPos.LEFT, VPos.CENTER);
        
        //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
        
        GridPane.setConstraints(s9, 0, 33, 6, 1, HPos.CENTER, VPos.CENTER);        
        
        GridPane.setConstraints(front, 0, 34, 1, 7, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(front_x1, 1, 34, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_x2, 1, 35, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs9, 2, 34, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_y1, 3, 34, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_y2, 3, 35, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs10, 4, 34, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_z1, 5, 34, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(front_z2, 5, 35, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(s10, 1, 36, 5, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(front_mat, 1, 37, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(front_mech_load, 1, 38, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(front_therm_influx, 1, 39, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(front_is_adiabatic, 1, 40, 5, 1, HPos.LEFT, VPos.CENTER);
        
        //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
        
        GridPane.setConstraints(s11, 0, 41, 6, 1, HPos.CENTER, VPos.CENTER);        
        
        GridPane.setConstraints(back, 0, 42, 1, 7, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(back_x1, 1, 42, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_x2, 1, 43, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs11, 2, 42, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_y1, 3, 42, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_y2, 3, 43, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(hs12, 4, 42, 1, 2, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_z1, 5, 42, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(back_z2, 5, 43, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(s12, 1, 44, 5, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(back_mat, 1, 45, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(back_mech_load, 1, 46, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(back_therm_influx, 1, 47, 5, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(back_is_adiabatic, 1, 48, 5, 1, HPos.LEFT, VPos.CENTER);
        
        this.getChildren().addAll(
                    s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12,
                    hs1, hs2, hs3, hs4, hs5, hs6, hs7, hs8, hs9, hs10, hs11, hs12,
                    file_dir, top, top_mat, top_x1, top_x2, top_y1, top_y2, top_z1, top_z2,
                    left, left_mat, left_x1, left_x2, left_y1, left_y2, left_z1, left_z2,
                    right, right_mat, right_x1, right_x2, right_y1, right_y2, right_z1, right_z2,
                    bottom, bottom_mat, bottom_x1, bottom_x2, bottom_y1, bottom_y2, bottom_z1, bottom_z2,
                    front, front_mat, front_x1, front_x2, front_y1, front_y2, front_z1, front_z2,
                    back, back_mat, back_x1, back_x2, back_y1, back_y2, back_z1, back_z2,
                    top_mech_load, top_therm_influx, top_is_adiabatic,
                    left_mech_load, left_therm_influx, left_is_adiabatic,
                    right_mech_load, right_therm_influx, right_is_adiabatic,
                    bottom_mech_load, bottom_therm_influx,  bottom_is_adiabatic,
                    front_mech_load, front_therm_influx, front_is_adiabatic,
                    back_mech_load, back_therm_influx, back_is_adiabatic
        );
    }
    
}
