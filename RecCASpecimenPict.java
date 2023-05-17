/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import util.Common;

/**
 *
 * @author Ear
 */
public class RecCASpecimenPict extends VBox{
    
    SplitPane root;
    
    UISpecimen ui_specimen;
    
    TransferredDataBank transfer_data_bank;
    
    RecCAScheme scheme;
    RecCAInitialStructure structure;
    
    public RecCASpecimenPict(UISpecimen temp_ui_specimen, TransferredDataBank receive_transfer_data_bank){
        System.out.println("RecCASpecimenPict constructor: start");
        
        transfer_data_bank = receive_transfer_data_bank;
        ui_specimen = temp_ui_specimen;
        
        this.initLeftContent();
        this.initRightContent();
        
        root = new SplitPane();
        root.setMinHeight(Screen.getPrimary().getVisualBounds().getHeight());
        root.setOrientation(Orientation.HORIZONTAL);
        root.getItems().addAll(left_content, scroll_pane);
        
        this.setSpecimenParams(ui_specimen);
        
        this.setMaterials(ui_specimen);
        
        this.setInitCond(transfer_data_bank.getUIInitialCondition());
        this.setBoundCond(transfer_data_bank.getUIBoundaryCondition(), transfer_data_bank.getUIInterface());
        this.setTank(transfer_data_bank.getUITank(), transfer_data_bank.getUIInterface());
        
        this.setTask(transfer_data_bank.getUITask(), transfer_data_bank.getUIInterface());
        
        this.getChildren().add(root);
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(5,5,5,5));
        
        scheme = new RecCAScheme(transfer_data_bank);
        structure = new RecCAInitialStructure(transfer_data_bank);
        
        handleEvents();
    }
    
    BorderPane left_content;
    ToolBar tool_bar;
    Button show_scheme, show_structure;
    
    public void initLeftContent(){
        System.out.println("RecCASpecimenPict method initLeftContent: start");
        left_content = new BorderPane();
        
        tool_bar = new ToolBar();
        
        show_scheme = new Button("Show Scheme");
        show_structure = new Button("Show Structure");
        
        tool_bar.getItems().addAll(
                show_scheme,
                show_structure
        );
        
        left_content.setTop(tool_bar);        
    }
    
    VBox right_content;
    ScrollPane scroll_pane;
    
    TitledPane specimen,specimen_param, materials,
               conditions, init_cond, bound_cond, tank, task;
    
    private void initRightContent(){        
        System.out.println("RecCASpecimenPict method initRightContent(): start");
        
        /*
        specimen_param_txt_area,
        materials_txt_area,
        init_cond_txt_area,
        bound_cond_txt_area,
        tank_txt_area,
        task_txt_area;
        */
        
        double width = Screen.getPrimary().getVisualBounds().getWidth();
        double height = Screen.getPrimary().getVisualBounds().getHeight();
                
        right_content = new VBox();
        right_content.setAlignment(Pos.CENTER);
        right_content.setPadding(new Insets(5,5,5,5));
        
        specimen = new TitledPane();
        specimen.setText("Specimen");
        
        specimen_param = new TitledPane();
        specimen_param.setText("Parameters");
        
        
        materials = new TitledPane();
        materials.setText("Materials");
//        materials.setContent(materials_txt_area);
        
        VBox spec_layout = new VBox(specimen_param,
                                    materials);
        specimen_param.setExpanded(false);
        specimen_param.setExpanded(false);
        spec_layout.setAlignment(Pos.CENTER);
        spec_layout.setPadding(new Insets(5,5,5,5));
        
        specimen.setContent(spec_layout);
        
        conditions = new TitledPane();
        conditions.setText("Conditions");
        
        init_cond = new TitledPane();
        init_cond.setText("Initial Conditions");
        
        bound_cond = new TitledPane();
        bound_cond.setText("Boundary Conditions");
        
        
        tank = new TitledPane();
        tank.setText("Tank");
        
        VBox cond_layout = new VBox(init_cond, 
                                    bound_cond, 
                                    tank);
        
        init_cond.setExpanded(false);
        bound_cond.setExpanded(false);
        tank.setExpanded(false);
        
        cond_layout.setAlignment(Pos.CENTER);
        cond_layout.setPadding(new Insets(5,5,5,5));
        
        conditions.setContent(cond_layout);
        
        task = new TitledPane();
        task.setText("Task");
        
        right_content.getChildren().addAll(
                specimen,
                new Separator(),
                conditions,
                new Separator(),
                task
        );
        
        specimen.setExpanded(true);
        conditions.setExpanded(true);
        task.setExpanded(false);
        
        scroll_pane = new ScrollPane();
        scroll_pane.setContent(right_content);
    }
    
    SpecimenTableFX spec_params;
    
    public void setSpecimenParams(UISpecimen spec){
        spec_params = new SpecimenTableFX(spec);
        specimen_param.setContent(spec_params);
    }
    
    MaterialsTableFX table;
    
    public void setMaterials(UISpecimen spec){
        table = new MaterialsTableFX(spec);        
        materials.setContent(table);
    }
    
    InitialCondTableFX init_conditions;
    
    public void setInitCond(UIInitialCondition init_cond){
        init_conditions = new InitialCondTableFX(init_cond);
        this.init_cond.setContent(init_conditions);
    }
    
    BoundCondTableFX bound_cond_params;
    
    public void setBoundCond(UIBoundaryCondition ui_bound_cond, UIInterface ui_interface){
        bound_cond_params = new BoundCondTableFX(ui_bound_cond, ui_interface);
        bound_cond.setContent(bound_cond_params);
    }
    
    TankTableFX tank_params;
    
    public void setTank(UITank ui_tank, UIInterface ui_interface){
        tank_params = new TankTableFX(ui_tank, ui_interface);
        tank.setContent(tank_params);
    }
    
    TaskTableFX task_params;
    
    public void setTask(UITask ui_task, UIInterface ui_interface){
        task_params = new TaskTableFX(ui_task, ui_interface);
        task.setContent(task_params);
    }
    
    private void handleEvents(){
        show_scheme.setOnAction(e -> {
            System.out.println("RecCASpecimenPict show_scheme button is pushed");
            left_content.setCenter(scheme);
        });
        
        show_structure.setOnAction(e -> {
            System.out.println("RecCASpecimenPict show_scheme button is pushed");
            left_content.setCenter(structure);;
        });
    }
}
