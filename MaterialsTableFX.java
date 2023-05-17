/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author DmitryB
 */
public class MaterialsTableFX extends GridPane{
    
    Label materials, volume_fraction, angle_range, disl_density, max_deviation;
    Label mat_1, mat_2, mat_3, mat_4, mat_5,
          vol_frac_1, vol_frac_2, vol_frac_3, vol_frac_4, vol_frac_5,
          ang_range_1, ang_range_2, ang_range_3, ang_range_4, ang_range_5,
          disl_dens_1, disl_dens_2, disl_dens_3, disl_dens_4, disl_dens_5,
          max_dev_1, max_dev_2, max_dev_3, max_dev_4, max_dev_5;
    
    UISpecimen spec;
    
    public MaterialsTableFX(UISpecimen ui_spec){
        
        System.out.println("MaterialsTableFX constructor: start");
        
        this.spec = ui_spec;
        
        initAllElements();
        addAllElements();
        
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5,5,5,5));        
    }
    
    private void initAllElements(){
        
        materials = new Label("Materials");
        materials.setPadding(new Insets(2,2,2,2));
        
        volume_fraction = new Label("Volume Fraction");
        volume_fraction.setPadding(new Insets(2,2,2,2));
        
        angle_range = new Label("Angle Range");
        angle_range.setPadding(new Insets(2,2,2,2));
        
        disl_density = new Label("Dislocation Density");
        disl_density.setPadding(new Insets(2,2,2,2));
        
        max_deviation = new Label("Maximal Deviation");
        max_deviation.setPadding(new Insets(2,2,2,2));
        
        mat_1 = new Label(spec.materials.get(0));
        mat_1.setPadding(new Insets(2,2,2,2));
        
        mat_2 = new Label(spec.materials.get(1));
        mat_2.setPadding(new Insets(2,2,2,2));
        
        mat_3 = new Label(spec.materials.get(2));
        mat_3.setPadding(new Insets(2,2,2,2));
        
        mat_4 = new Label(spec.materials.get(3));
        mat_4.setPadding(new Insets(2,2,2,2));
        
        mat_5 = new Label(spec.materials.get(4));
        mat_5.setPadding(new Insets(2,2,2,2));
        
        vol_frac_1 = new Label(String.valueOf(spec.volume_fraction.get(0)));
        vol_frac_1.setPadding(new Insets(2,2,2,2));
        
        vol_frac_2 = new Label(String.valueOf(spec.volume_fraction.get(1)));
        vol_frac_2.setPadding(new Insets(2,2,2,2));
        
        vol_frac_3 = new Label(String.valueOf(spec.volume_fraction.get(2)));
        vol_frac_3.setPadding(new Insets(2,2,2,2));
        
        vol_frac_4 = new Label(String.valueOf(spec.volume_fraction.get(3)));
        vol_frac_4.setPadding(new Insets(2,2,2,2));
        
        vol_frac_5 = new Label(String.valueOf(spec.volume_fraction.get(4)));
        vol_frac_5.setPadding(new Insets(2,2,2,2));
        
        
        ang_range_1 = new Label(String.valueOf(spec.angle_range.get(0)));
        ang_range_1.setPadding(new Insets(2,2,2,2));
        
        ang_range_2 = new Label(String.valueOf(spec.angle_range.get(1)));
        ang_range_2.setPadding(new Insets(2,2,2,2));
        
        ang_range_3 = new Label(String.valueOf(spec.angle_range.get(2)));
        ang_range_3.setPadding(new Insets(2,2,2,2));
        
        ang_range_4 = new Label(String.valueOf(spec.angle_range.get(3)));
        ang_range_4.setPadding(new Insets(2,2,2,2));
        
        ang_range_5 = new Label(String.valueOf(spec.angle_range.get(4)));
        ang_range_5.setPadding(new Insets(2,2,2,2));
        
        
        disl_dens_1 = new Label(String.valueOf(spec.disl_density.get(0)));
        disl_dens_1.setPadding(new Insets(2,2,2,2));
        
        disl_dens_2 = new Label(String.valueOf(spec.disl_density.get(1)));
        disl_dens_2.setPadding(new Insets(2,2,2,2));
        
        disl_dens_3 = new Label(String.valueOf(spec.disl_density.get(2)));
        disl_dens_3.setPadding(new Insets(2,2,2,2));
        
        disl_dens_4 = new Label(String.valueOf(spec.disl_density.get(3)));
        disl_dens_4.setPadding(new Insets(2,2,2,2));
        
        disl_dens_5 = new Label(String.valueOf(spec.disl_density.get(4)));
        disl_dens_5.setPadding(new Insets(2,2,2,2));
        
        
        max_dev_1 = new Label(String.valueOf(spec.max_deviation.get(0)));
        max_dev_1.setPadding(new Insets(2,2,2,2));
        
        max_dev_2 = new Label(String.valueOf(spec.max_deviation.get(1)));
        max_dev_2.setPadding(new Insets(2,2,2,2));
        
        max_dev_3 = new Label(String.valueOf(spec.max_deviation.get(2)));
        max_dev_3.setPadding(new Insets(2,2,2,2));
        
        max_dev_4 = new Label(String.valueOf(spec.max_deviation.get(3)));
        max_dev_4.setPadding(new Insets(2,2,2,2));
        
        max_dev_5 = new Label(String.valueOf(spec.max_deviation.get(4)));        
        max_dev_5.setPadding(new Insets(2,2,2,2));
    }
    
    private void addAllElements(){
        
        this.setGridLinesVisible(true);
        this.setAlignment(Pos.CENTER);
        
        GridPane.setConstraints(materials, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(volume_fraction, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(angle_range, 2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(disl_density, 3, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_deviation, 4, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(mat_1, 0, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(mat_2, 0, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(mat_3, 0, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(mat_4, 0, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(mat_5, 0, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(vol_frac_1, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(vol_frac_2, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(vol_frac_3, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(vol_frac_4, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(vol_frac_5, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(ang_range_1, 2, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(ang_range_2, 2, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(ang_range_3, 2, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(ang_range_4, 2, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(ang_range_5, 2, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(disl_dens_1, 3, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(disl_dens_2, 3, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(disl_dens_3, 3, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(disl_dens_4, 3, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(disl_dens_5, 3, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(max_dev_1, 4, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_dev_2, 4, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_dev_3, 4, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_dev_4, 4, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(max_dev_5, 4, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        this.getChildren().addAll(
                materials, volume_fraction, angle_range, disl_density, max_deviation,
                mat_1, mat_2, mat_3, mat_4, mat_5,
                vol_frac_1, vol_frac_2, vol_frac_3, vol_frac_4, vol_frac_5,
                ang_range_1, ang_range_2, ang_range_3, ang_range_4, ang_range_5,
                disl_dens_1, disl_dens_2, disl_dens_3, disl_dens_4, disl_dens_5,
                max_dev_1, max_dev_2, max_dev_3, max_dev_4, max_dev_5
        );        
    }
    
}


