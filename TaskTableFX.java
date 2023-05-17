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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import util.Common;

/**
 *
 * @author DmitryB
 */
public class TaskTableFX extends GridPane{
    
    CheckBox heat_flows, recryst, mech_flows, crack, equilibrium_state,
             show_grain_bounds, heat_expansion, force_moments;
    
    Label dir, time_step, total_time, file_number, response_rate, bound_type, task_comments;
    
    UITask task;
    UIInterface ui;
    
    public TaskTableFX(UITask ui_task, UIInterface ui_interface) {
        task = ui_task;
        ui = ui_interface;
        
        initAllElements();
        addAllElements();
        
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5,5,5,5));
    }
    
    private void initAllElements(){
        
        dir = new Label("Task Directory:\n" + Common.TASK_PATH+"/"+ui.getTaskPath()+"."+Common.TASK_EXTENSION);
        
        heat_flows = new CheckBox("Simulate Heat Flows");
        heat_flows.setDisable(true);
        heat_flows.setStyle("-fx-opacity: 1");
        heat_flows.setSelected(task.getHeatTransfer());
        
        recryst = new CheckBox("Simulate Recrystallization");
        recryst.setDisable(true);
        recryst.setStyle("-fx-opacity: 1");
        recryst.setSelected(task.getRecrystallization());
        
        mech_flows = new CheckBox("Simulate Mechanical Flows");
        mech_flows.setDisable(true);
        mech_flows.setStyle("-fx-opacity: 1");
        mech_flows.setSelected(task.getMechanicalElating());
        
        crack = new CheckBox("Simulate Cracks");
        crack.setDisable(true);
        crack.setStyle("-fx-opacity: 1");
        crack.setSelected(task.getDefectsInfluence());
        
        equilibrium_state = new CheckBox("Equilibrium State");
        equilibrium_state.setDisable(true);
        equilibrium_state.setStyle("-fx-opacity: 1");
        equilibrium_state.setSelected(task.getEquilibriumState());
        
        show_grain_bounds = new CheckBox("Show Grain Boundaries");
        show_grain_bounds.setDisable(true);
        show_grain_bounds.setStyle("-fx-opacity: 1");
        show_grain_bounds.setSelected(task.getShowGrainBounds());
        
        heat_expansion = new CheckBox("Heat Expansion");
        heat_expansion.setDisable(true);
        heat_expansion.setStyle("-fx-opacity: 1");
        heat_expansion.setSelected(task.getCalcHeatExpansion());
        
        force_moments = new CheckBox("Force Moments");
        force_moments.setDisable(true);
        force_moments.setStyle("-fx-opacity: 1");
        force_moments.setSelected(task.getCalcForceMoments());
        
        time_step = new Label("Time Step = " + new Double(task.getTimeStep()).toString());
        total_time = new Label("Total Time = "+new Double(task.getTotalTime()).toString());
        file_number = new Label("Output File Number = " + new Long(task.getOutputFile()).toString());
        response_rate = new Label("Response Rate = "+new Double(task.getResponseRate()).toString());
        
        bound_type = new Label();
        
        if(task.getBoundaryType() == 0)
            bound_type.setText("Boundary Type: (0) homogeneous mechanical loading and homogeneous heating");
        else if(task.getBoundaryType() == 1)
            bound_type.setText("Boundary Type: (1) mechanical loading by indentor and homogeneous heating");
        else if(task.getBoundaryType() == 2)
            bound_type.setText("Boundary Type: (2) homogeneous mechanical loading of facets and heating of circle area");
        else if(task.getBoundaryType() == 3)
            bound_type.setText("Boundary Type: (3) mechanical bending and homogeneous heating");
        else if(task.getBoundaryType() == 4)
            bound_type.setText("Boundary Type: (4) mechanical shear and homogeneous heating");
        
        task_comments = new Label("Task Comments:\n" + task.getTaskComments().getText());
        
    }
    
    private void addAllElements(){
        this.setHgap(8.0d);
        this.setVgap(5.0d);
        
        Separator s1 = new Separator();
        Separator s2 = new Separator();
        Separator s3 = new Separator();
        
        GridPane.setConstraints(dir, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s1, 0, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(heat_flows, 0, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(recryst, 0, 3, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(mech_flows, 0, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(crack, 0, 5, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(equilibrium_state, 0, 6, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(show_grain_bounds, 0, 7, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(heat_expansion, 0, 8, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(force_moments, 0, 9, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s2, 0, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(time_step, 0, 11, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(total_time, 0, 12, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(file_number, 0, 13, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(s3, 0, 14, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(response_rate, 0, 15, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(bound_type, 0, 16, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(task_comments, 0, 17, 1, 1, HPos.LEFT, VPos.CENTER);
        
        this.getChildren().addAll(
                s1, s2, s3,
                dir, time_step, total_time, file_number, response_rate, bound_type, task_comments,
                heat_flows, recryst, mech_flows, crack, equilibrium_state,
                show_grain_bounds, heat_expansion, force_moments
        );
    }
    
}
