/*
 * To change this licenses header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import interf.charts.RecCAChart;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCAResultsParams extends Stage{
    
    Scene scene;
    BorderPane root;
    
    double scene_width = Screen.getPrimary().getVisualBounds().getWidth();
    double scene_height = Screen.getPrimary().getVisualBounds().getHeight();
    
    /**
     * Menu Bar Components
     */
    MenuBar menu_bar;
    Menu file, save_image, view, chart, set_camera;
    MenuItem save_sample_as_image, save_scale_as_image,
             save_all_as_image, exit, set_camera_XY, set_camera_YZ, set_camera_XZ,
             build_chart, create_files;
    RadioMenuItem show_axis, slider_view, mouse_view;
    
    /**
     * Top ToolBar Elements
     */
    ToolBar top_tool_bar;
    ComboBox time_steps, views;
    
    ComboBox results;
    
    Button add_results;
    Button demo;
    
    ToggleButton time_steps_tb, results_tb, view_tb;
    
    List<String> time_steps_values = new ArrayList<>();
    Button next, previous, save_view, set_view;
    RadioButton show_all, show_plane;
    ToggleGroup show_group;
    
    /**
     * Right ToolBar Elements
     */
    ToolBar right_tool_bar;
    TitledPane camera_control, params, scale, materials, plane;
    
    RadioButton param_No1, param_No2, param_No3, param_No4, param_No5, 
                param_No6, param_No7, param_No8, param_No9, structure;
    ComboBox structure_type;
    ToggleGroup params_group;
    RadioButton new_tab;
    Button show;
    
    ToggleButton simple_plane;
    ComboBox plane_type;
    TextField layer_number;
    Label max_layer_number;
    
    TabPane tab_pane;
    
    String task_name;
    
    TransferredDataBank trans_data_bank;
    
    RecCASaveView save_view_stage;
    
    RecCAReadFiles data;
    
    
    public RecCAResultsParams(TransferredDataBank receive_data_bank){
        System.out.println("RecCAResultsParams constructor: start");
        
        root = new BorderPane();
        double width = Screen.getPrimary().getVisualBounds().getWidth()*0.9;
        double height = Screen.getPrimary().getVisualBounds().getHeight()*0.9;
        scene = new Scene(root, width, height);
        
        this.trans_data_bank = receive_data_bank;
        
        System.out.println("RecCAResultsParams constructor start");
        initMenuBar();
        initTopToolBar();
        initRightToolBar();
        handleEvents();
        handleTopToolBar();
        
        views.getItems().addAll(getAllViewFiles());
        
        tab_pane = new TabPane();
        
        Tab tab = new Tab("Initial Tab");
        setContextMenu(tab);
        tab_pane.getTabs().add(tab);
        
        root.setCenter(tab_pane);
        
        root.setTop(new VBox(menu_bar, top_tool_bar));
        root.setRight(right_tool_bar);
        
        this.setTitle("RecCA - Results");
        this.setScene(scene);
        this.show();
    }
    
    public void initMenuBar(){
        System.out.println("RecCAResults params method initMenuBar(): start");
        menu_bar = new MenuBar();
        
        file = new Menu("File");
        view = new Menu("View");
        chart = new Menu("Chart");
        
        create_files = new MenuItem("Create Files");
        
        save_image = new Menu("Save As Image...");
        
        save_sample_as_image = new MenuItem("Sample");
        save_scale_as_image = new MenuItem("Scale");
        save_all_as_image = new MenuItem("Both");
        
        exit = new MenuItem("Exit.");
        exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        
        save_image.getItems().addAll(
                save_sample_as_image,
                save_scale_as_image,
                save_all_as_image
        );
        
        file.getItems().addAll(
                create_files, save_image, 
                new SeparatorMenuItem(), exit
        );
        
        slider_view = new RadioMenuItem("Slider View");
        mouse_view = new RadioMenuItem("Mouse View");
        
        set_camera = new Menu("Set Camera");
        set_camera_XY = new MenuItem("XY");
        set_camera_YZ = new MenuItem("YZ");
        set_camera_XZ = new MenuItem("XZ");
        set_camera.getItems().addAll(
                set_camera_XY,
                set_camera_YZ,
                set_camera_XZ
        );
        
        show_axis = new RadioMenuItem("Show Axis");
        show_axis.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        
        view.getItems().addAll(
                slider_view,
                mouse_view,
                new SeparatorMenuItem(),
                set_camera,
                new SeparatorMenuItem(),
                show_axis
        );
        
        chart = new Menu("Chart");
        build_chart = new MenuItem("Build Chart");
        
        chart.getItems().add(build_chart);
        
        menu_bar.getMenus().addAll(file, view, chart);
        handleMenuBar();
    }
    
    public void handleMenuBar(){
        exit.setOnAction(e -> Platform.exit());
        
        build_chart.setOnAction(e -> {
            RecCAChart recca_chart = new RecCAChart(trans_data_bank);
        });
        
        create_files.setOnAction(e -> {
            RecCAResultsFilesList res = new RecCAResultsFilesList(trans_data_bank);
            
            res.ok.setOnAction(event -> {
                if(res.results_list.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Files!");
                else{
                    RecCACreateFiles files = new RecCACreateFiles(res.results_list.getSelectionModel().getSelectedItems());
                    res.close();
                }
            });
        });
    }
    
    public void initTopToolBar(){
        System.out.println("RecCAResultsParams method initTopToolBar: start");
        
        top_tool_bar = new ToolBar();
        
        demo = new Button("DEMO");
        
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setPadding(new Insets(0,3,0,3));
        
        results_tb = new ToggleButton("Results");
        time_steps_tb = new ToggleButton("Time Steps");
        view_tb = new ToggleButton("View");
        
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(results_tb, time_steps_tb, view_tb);
        
        results = new ComboBox();
        results.setMaxWidth(250.0d);
        results.setMinWidth(250.0d);
        add_results = new Button();
        ImageView add_img = new ImageView(new Image("./interf/icons/add.png"));
        add_img.setFitHeight(15.0d);
        add_img.setFitWidth(15.0d);
        add_results.setGraphic(add_img);
        
        time_steps = new ComboBox();
        time_steps.setMinWidth(500.0d);
        time_steps.setMaxWidth(500.0d);
        
        views = new ComboBox();
        show_all = new RadioButton("All");
        show_all.setSelected(true);
        show_plane = new RadioButton("Plane");
        show_group = new ToggleGroup();
        show_group.getToggles().addAll(show_all, show_plane);
        
        ImageView next_icon = new ImageView(new Image("./interf/icons/arrow.png"));
        next_icon.setFitWidth(15.0d);
        next_icon.setFitHeight(15.0d);
        ImageView prev_icon = new ImageView(new Image("./interf/icons/arrow.png"));
        prev_icon.setFitWidth(15.0d);
        prev_icon.setFitHeight(15.0d);
        prev_icon.setRotate(180);
        
        next = new Button();
        next.setGraphic(next_icon);
        previous = new Button();
        previous.setGraphic(prev_icon);
        
        save_view = new Button("Save View");
        set_view = new Button("Set");
        
        Separator sep1 = new Separator();        
        sep1.setPadding(new Insets(5,5,5,5));
        sep1.setOrientation(Orientation.VERTICAL);
        
        Separator sep2 = new Separator();        
        sep2.setPadding(new Insets(5,5,5,5));
        sep2.setOrientation(Orientation.VERTICAL);
        
        Separator sep3 = new Separator();        
        sep3.setPadding(new Insets(5,5,5,5));
        sep3.setOrientation(Orientation.VERTICAL);
        
        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER_LEFT);
        
        results_tb.setOnAction(e -> {
            layout.getChildren().clear();
            layout.getChildren().addAll(new Label("Select Results: "), results, add_results);
        });
        
        time_steps_tb.setOnAction(e -> {
            if(time_steps_tb.isSelected()){
                layout.getChildren().clear();
                layout.getChildren().addAll(new Label("Show: "),show_all, show_plane, sep1, time_steps, previous, next);
            }
        });
        
        view_tb.setOnAction(e -> {
            if(view_tb.isSelected()){
                layout.getChildren().clear();
                layout.getChildren().addAll(new Label("Select View: "), views, set_view, sep3, save_view);
            }                
        });
        
        top_tool_bar.getItems().addAll(demo, sep, new Label("Select: "), results_tb, time_steps_tb, view_tb, sep2, layout);        
    }
    
    public void handleTopToolBar()
    {   
        demo.setOnAction(e -> {
            RecCAListView demo = new RecCAListView();
        });
        
        add_results.setOnAction(e -> {
            RecCAResultsFilesList res = new RecCAResultsFilesList(trans_data_bank);
            
            res.ok.setOnAction(event -> {
                String selected = res.list.getSelectionModel().getSelectedItem().toString();
                results.getItems().add(selected);
                System.out.println("ADD RESULTS: " + selected);
                res.close();
            });
            
            res.list.setOnMouseClicked(me -> {
                if(me.getClickCount() == 2){
                    String selected = res.list.getSelectionModel().getSelectedItem().toString();
                    results.getItems().add(selected);
                    System.out.println("ADD RESULTS: " + selected);
                    res.close();
                }
            });
        });
        
        results.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            String selected = newValue.toString();
            
            if(!time_steps_values.isEmpty())
                time_steps_values.clear();
            
            time_steps_values = loadFiles(Common.TASK_PATH + "/" + selected + "/", "time_steps");
            
            if(!time_steps.getItems().isEmpty())
                time_steps.getItems().clear();
                
            time_steps.getItems().addAll(loadFiles(Common.TASK_PATH + "/" + selected, "res"));
            
            System.out.println("=========================================");
            System.out.println("RecCAResultsParams.handleTopToolBar(): Number of time steps for representation of results: "+time_steps_values.size());
            System.out.println("=========================================");
            
            task_name = selected;
        });
        
       
        next.setOnAction(e -> time_steps.getSelectionModel().selectNext());        
        previous.setOnAction(e -> time_steps.getSelectionModel().selectPrevious());
        
        time_steps.getSelectionModel().selectedItemProperty().addListener((ObservableValue ov, Object oldValue, Object newValue) -> {
            
            String selected_item_name = newValue.toString();
            
            try {
                data = new RecCAReadFiles(Common.TASK_PATH + "/" + task_name + "/" + selected_item_name);
            } catch (IOException ex) {
                System.out.println("Error: can't read file " + selected_item_name);
            }
            
            params.setExpanded(true);
            
            if(selected_item_name.endsWith("relValues.res")){
                param_No1.setText("Temperature");
                param_No2.setText("Relative Spec. Elastic Energy");
                param_No3.setText("Total Specfic Energy");
                param_No4.setText("Abs Value of X comp. of Spec. Moment");
                param_No5.setText("Abs Value of Y comp. of Spec. Moment");
                param_No6.setText("Abs Value of Z comp. of Spec. Moment");
                param_No7.setText("Abs Value of Spec. Force Mom.");
                param_No8.setText("Relative Spec. Diss. Energy"); 
                param_No9.setVisible(false);
            }
            else if(selected_item_name.endsWith("_jocl_torsion.res")){
                param_No1.setText("X Comp. of Angle Velocity");
                param_No2.setText("Y Comp. of Angle Velocity");
                param_No3.setText("Z Comp. of Angle Velocity");
                param_No4.setText("X Comp. of Torsion Angle");
                param_No5.setText("Y Comp. of Torsion Angle");
                param_No6.setText("Z Comp. of Torsion Angle");
                param_No7.setText("Abs. Angle Velosity of Torsion");
                param_No8.setText("Abs. Torsion Angle"); 
                param_No9.setVisible(false);
            }
            else if(selected_item_name.contains("inst_mom.res")){
                param_No1.setText("Abs. Value of X Inst. Spec. Moment");
                param_No2.setText("Abs. Value of Y Inst. Spec. Moment");
                param_No3.setText("Abs. Value of Z Inst. Spec. Moment");
                param_No4.setText("X Comp. of Inst. Spec. Moment");
                param_No5.setText("Y Comp. of Inst. Spec. Moment");
                param_No6.setText("Z Comp. of Inst. Spec. Moment");
                param_No7.setText("Abs. Value of Inst. Moment");
                param_No8.setText("Current Influx Of Torsion Energy");
                param_No9.setVisible(false);
            }   
            else if(selected_item_name.contains("torsion.res")){
                param_No1.setText("X Comp. of Angle Velocity");
                param_No2.setText("Y Comp. of Angle Velocity");
                param_No3.setText("Z Comp. of Angle Velocity");
                param_No4.setText("X Comp. of Torsion Angle Vector");
                param_No5.setText("Y Comp. of Torsion Angle Vector");
                param_No6.setText("Z Comp. of Torsion Angle Vector");
                param_No7.setText("Relative Power Of Torsion");
                param_No8.setText("Relative Torsion Energy");
                param_No9.setText("Total Specific Energy");
                param_No9.setVisible(true);
            }
            else if(selected_item_name.contains("jocl_stresses.res")){
                param_No1.setText("Defect Density");
                param_No2.setText("Abs. Value of Stress Vector");
                param_No3.setText("Change of Density Defects");
                param_No4.setText("X Comp. of Stress Vector");
                param_No5.setText("Y Comp. of Stress Vector");
                param_No6.setText("Z Comp. of Stress Vector");
                param_No7.setText("Influx of Spec. Diss. Energy");
                param_No8.setText("Spec. Diss. Energy");
                param_No9.setVisible(false);
            }
            else if(selected_item_name.contains("jocl.res")){
                param_No1.setText("Temperature");
                param_No2.setText("Specific Elastic Energy");
                param_No3.setText("Principal Stresses");
                param_No4.setText("X Comp. of Spec. Force Moment");
                param_No5.setText("Y Comp. of Spec. Force Moment");
                param_No6.setText("Z Comp. of Spec. Force Moment");
                param_No7.setText("Influx of Spec. Diss. Energy");
                param_No8.setText("Spec. Diss. Energy");
                param_No9.setVisible(false);
            }                         
            
            else if(selected_item_name.contains(".res")){
                param_No1.setText("Temperature");
                param_No2.setText("Effective Stresses");
                param_No3.setText("Principal Stresses");
                param_No4.setText("X Comp. of Spec. Moment");
                param_No5.setText("Y Comp. of Spec. Moment");
                param_No6.setText("Z Comp. of Spec. Moment");
                param_No7.setText("Strain");
                param_No8.setText("Total Spec.Torsion Energy");
                param_No9.setVisible(false);
            }
            
        });
        
        show_plane.setOnAction(e -> {
            if(show_plane.isSelected()) {
                plane.setDisable(false);
                plane.setExpanded(true);
            }
            else {
                plane.setDisable(true);
                plane.setExpanded(false);
            } 
        });
        show_all.setOnAction(e -> {
            if(show_all.isSelected()){
                plane.setDisable(true);
                plane.setExpanded(false);
            }
        });
    }
    
    public void initRightToolBar(){
        right_tool_bar = new ToolBar();
        right_tool_bar.setOrientation(Orientation.VERTICAL);
        
        right_tool_bar.setMinWidth(400.0d);
        right_tool_bar.setMaxWidth(400.0d);
        right_tool_bar.setManaged(true);
        
        
        camera_control = new TitledPane();
        camera_control.setText("Camera Control");
        
        
        /**
         * Create Plane TitledPane
         */
        simple_plane = new ToggleButton("Simple Plane");
        plane_type = new ComboBox();
        plane_type.getItems().addAll("X", "Y", "Z");
        layer_number = new TextField();
        max_layer_number = new Label();
        Label layer_type_lbl = new Label("Layer Number:");
        
        GridPane plane_layout = new GridPane();
        plane_layout.setAlignment(Pos.CENTER_LEFT);
        plane_layout.setHgap(7.0d);
        plane_layout.setVgap(7.0d);
        
        plane_layout.add(simple_plane, 0, 0);
        plane_layout.add(plane_type, 0, 1);
        plane_layout.add(layer_type_lbl, 0, 2);
        plane_layout.add(layer_number, 1, 2);
        plane_layout.add(max_layer_number, 1, 3);
        
        plane = new TitledPane();
        plane.setText("Plane");
        plane.setContent(plane_layout);
        plane.setExpanded(false);
        plane.setDisable(true);
        
        /**
         * Create Parameters TitledPane
         */
        param_No1 = new RadioButton("Parameter #1");
        param_No2 = new RadioButton("Parameter #2");
        param_No3 = new RadioButton("Parameter #3");
        param_No4 = new RadioButton("Parameter #4");
        param_No5 = new RadioButton("Parameter #5");
        param_No6 = new RadioButton("Parameter #6");
        param_No7 = new RadioButton("Parameter #7");
        param_No8 = new RadioButton("Parameter #8");
        param_No9 = new RadioButton("Parameter #9");
        structure = new RadioButton("Structure");
        
        params_group = new ToggleGroup();
        params_group.getToggles().addAll(
                param_No1, param_No2, param_No3,
                param_No4, param_No5, param_No6,
                param_No7, param_No8, param_No9, 
                structure
        );
        
        new_tab = new RadioButton("New Tab");
        new_tab.setSelected(true);
        
        show = new Button("Show");
        show.setPadding(new Insets(5, 30, 5, 30));
        
        Separator sep = new Separator();
        Separator sep1 = new Separator();
        
        structure_type = new ComboBox();
        structure_type.getItems().addAll(
                "with boundaries",
                "without boundaries",
                "boundary automata view", 
                "materials"
        );
        structure_type.getSelectionModel().selectFirst();
        
        GridPane params_layout = new GridPane();
        params_layout.setAlignment(Pos.CENTER);
        params_layout.setHgap(5.0d);
        params_layout.setVgap(5.0d);
        
        params_layout.add(param_No1, 0, 0, 2, 1);
        params_layout.add(param_No2, 0, 1, 2, 1);
        params_layout.add(param_No3, 0, 2, 2, 1);
        params_layout.add(param_No4, 0, 3, 2, 1);
        params_layout.add(param_No5, 0, 4, 2, 1);
        params_layout.add(param_No6, 0, 5, 2, 1);
        params_layout.add(param_No7, 0, 6, 2, 1);
        params_layout.add(param_No8, 0, 7, 2, 1);
        params_layout.add(param_No9, 0, 8, 2, 1);
        params_layout.add(sep, 0, 9, 2, 1);
        params_layout.add(structure, 0, 10, 1, 1);
        params_layout.add(structure_type, 1, 10, 1, 1);
        params_layout.add(sep1, 0, 11, 2, 1);
        params_layout.add(plane, 0, 12, 2, 1);
        params_layout.add(show, 0, 13, 1, 1);
        params_layout.add(new_tab, 1, 13, 1, 1);
        
        params = new TitledPane();
        params.setText("Parameters");
        params.setContent(params_layout);
        params.setExpanded(false);
        
        scale = new TitledPane();
        scale.setText("Scale");
        scale.setExpanded(false);
        
        materials = new TitledPane();
        materials.setText("Materials");
        materials.setVisible(false);
        
        VBox layout = new VBox(camera_control, params, scale, materials);
        right_tool_bar.getItems().add(layout);
    }
    
    public List<String> loadFiles(String folder, String type)
    {
        List<String> list = FXCollections.observableArrayList();
        File[] files = new File(folder).listFiles();
        
        int file_counter = 0;
        
        switch(type){
            case "res":
                for(File file : files)
                {
                  if(file.isFile() && file.getName().endsWith(".res"))
                  {
                    file_counter++;
                    list.add(file.getName());
                  }
                }
                break;
            case "view":
                for(File file : files)
                {
                  if(file.isFile() && file.getName().endsWith(".view"))
                  {
                    file_counter++;
                    list.add(file.getName());
                  }
                }
                break;
            case "time_steps":
                for(File file : files)
                {
                  if(file.isFile() && file.getName().endsWith("relValues.res"))
                    {
                        file_counter++;

                        System.out.print("File # "+file_counter+". type: relValues; ");

                        String str = file.getName().replaceAll("_relValues.res", "");

                        System.out.print("name: "+str);

                        String time_step = str.substring( str.length()-6, str.length());

                        System.out.println("; time_step = "+time_step);

                        list.add(time_step);
                    }
                  else if(file.isFile() && file.getName().endsWith("jocl_stresses.res"))
                    {
                        file_counter++;

                        System.out.print("File # "+file_counter+". type: jocl_stresses; ");

                        String str = file.getName().replaceAll("_jocl_stresses.res", "");

                        System.out.println("name: "+str);

                        String time_step = str.substring(str.length()-6, str.length());

                        System.out.println("; time_step = "+time_step);

                        list.add(time_step);
                    }

                  else if(file.isFile() && file.getName().endsWith("torsion.res"))
                  {
                      String str = file.getName().replaceAll("_torsion.res", "");
                      String time_step = str.substring( str.length()-6, str.length());
                      list.add(time_step);
                  }

                }
                break;
        }
        
        Collections.sort(list);
        
        System.out.println("----------------------------------------");
        System.out.println("type = "+type);
        System.out.println("RecCAResultsParams.loadFiles(): Number of files in directory with results: "+list.size());
        System.out.println("----------------------------------------");
        
        return list;
    }
    
    public void handleEvents()
    {   
        structure_type.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if(newValue.equals("materials"))
                materials.setVisible(true);
            else
                materials.setVisible(false);
        });
        
        plane_type.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                
                if(time_steps.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select the file for representation").showAndWait();
                else{
                    try {
                        data.readSecaFile(data.getSecaFilePath(Common.TASK_PATH + "/" + task_name + "/"));
                    } catch (IOException ex) {
                        System.out.println("Error: can't read or find seca file");
                    }
                }
                
                switch( (int) newValue){
                    case 0:
                        max_layer_number.setText("(max layer number = "+data.cell_number_x+")");
                        break;
                    case 1:
                        max_layer_number.setText("(max layer number = "+data.cell_number_y+")");
                        break;
                    case 2:
                        max_layer_number.setText("(max layer number = "+data.cell_number_z+")");
                        break;
                }
            }
        });
        
        show.setOnAction(e -> 
        {
            
            if(this.results.getSelectionModel().isEmpty())
                new Alert(AlertType.ERROR, "Select the Results!!!").showAndWait();
            if(this.time_steps.getSelectionModel().isEmpty())
                new Alert(AlertType.ERROR, "Select the File for Representation").showAndWait();
            
            String file_name = time_steps.getSelectionModel().getSelectedItem().toString();
            String file_path = Common.TASK_PATH + "/" + task_name + "/" + file_name;
            
            int index = time_steps.getSelectionModel().getSelectedIndex() / 3;
            
            System.out.println("Number of time steps for representation of results: "+time_steps_values.size());
            System.out.println("Name of file with results: "+file_path);
            System.out.println("Time Step Index: "+index);
                
            RecCAScale scale = new RecCAScale();
            
            if(show_all.isSelected()){
                if(file_name.endsWith("_relValues.res"))
                {
                         if(param_No1.isSelected()) showAll(file_name, param_No1, "relative_temperature", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No2.isSelected()) showAll(file_name, param_No2, "relative_specific_elastic_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No3.isSelected()) showAll(file_name, param_No3, "total_specific_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No4.isSelected()) showAll(file_name, param_No4, "abs_value_of_X_comp_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No5.isSelected()) showAll(file_name, param_No5, "abs_value_of_Y_comp_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No6.isSelected()) showAll(file_name, param_No6, "abs_value_of_Z_comp_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No7.isSelected()) showAll(file_name, param_No7, "abs_value_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                                                        
                    else if(param_No8.isSelected()) showAll(file_name, param_No8, "relative_specific_dissipated_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                }
                else 
                if(file_name.endsWith("_jocl_torsion.res")){
                         if(param_No1.isSelected()) showAll(file_name, param_No1, "x_component_of_angle_velocity", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No2.isSelected()) showAll(file_name, param_No2, "y_component_of_angle_velocity", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No3.isSelected()) showAll(file_name, param_No3, "z_component_of_angle_velocity", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No4.isSelected()) showAll(file_name, param_No4, "x_component_of_torsion_angle_vector", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No5.isSelected()) showAll(file_name, param_No5, "y_component_of_torsion_angle_vector", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No6.isSelected()) showAll(file_name, param_No6, "z_component_of_torsion_angle_vector", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No7.isSelected()) showAll(file_name, param_No7, "abs_value_of_angle_velocity", time_steps_values.get(index), scale, new_tab.isSelected());                                                        
                    else if(param_No8.isSelected()) showAll(file_name, param_No8, "abs_value_of_torsion_angle", time_steps_values.get(index), scale, new_tab.isSelected());
                }
                else 
                if(file_name.endsWith("jocl.res"))
                {
                         if(param_No1.isSelected()) showAll(file_name, param_No1, "temperature", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No2.isSelected()) showAll(file_name, param_No2, "specific_elastic_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No3.isSelected()) showAll(file_name, param_No3, "principal_stresses", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No4.isSelected()) showAll(file_name, param_No4, "x_component_of_specific_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No5.isSelected()) showAll(file_name, param_No5, "y_component_of_specific_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No6.isSelected()) showAll(file_name, param_No6, "z_component_of_specific_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No7.isSelected()) showAll(file_name, param_No7, "current_influx_of_specific_dissipated_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No8.isSelected()) showAll(file_name, param_No8, "specific_dissipated_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                }                    
                
                else if(file_name.endsWith("_torsion.res"))
                {
                         if(param_No1.isSelected()) showAll(file_name, param_No1, "x_component_of_angle_velocity", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No2.isSelected()) showAll(file_name, param_No2, "y_component_of_angle_velocity", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No3.isSelected()) showAll(file_name, param_No3, "z_component_of_angle_velocity", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No4.isSelected()) showAll(file_name, param_No4, "x_component_of_torsion_angle_vector", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No5.isSelected()) showAll(file_name, param_No5, "y_component_of_torsion_angle_vector", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No6.isSelected()) showAll(file_name, param_No6, "z_component_of_torsion_angle_vector", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No7.isSelected()) showAll(file_name, param_No7, "relative_power_of_torsion", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No8.isSelected()) showAll(file_name, param_No8, "relative_torsion_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No9.isSelected()) showAll(file_name, param_No9, "total_specific_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                }
                else 
                if(file_name.endsWith("_inst_mom.res"))
                {
                         if(param_No1.isSelected()) showAll(file_name, param_No1, "abs_value_of_X_comp_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No2.isSelected()) showAll(file_name, param_No2, "abs_value_of_Y_comp_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No3.isSelected()) showAll(file_name, param_No3, "abs_value_of_Z_comp_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No4.isSelected()) showAll(file_name, param_No4, "x_component_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No5.isSelected()) showAll(file_name, param_No5, "y_component_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No6.isSelected()) showAll(file_name, param_No6, "z_component_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No7.isSelected()) showAll(file_name, param_No7, "abs_value_of_inst_spec_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No8.isSelected()) showAll(file_name, param_No8, "current_influx_of_torsion_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                }
                else if(file_name.endsWith("jocl_stresses.res")){
                         if(param_No1.isSelected()) showAll(file_name, param_No1, "temperature", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No2.isSelected()) showAll(file_name, param_No2, "abs_value_of_stress_vector", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No3.isSelected()) showAll(file_name, param_No3, "value_for_crack_generation", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No4.isSelected()) showAll(file_name, param_No4, "x_comp_of_stress_vector", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No5.isSelected()) showAll(file_name, param_No5, "y_comp_of_stress_vector", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No6.isSelected()) showAll(file_name, param_No6, "z_comp_of_stress_vector", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No7.isSelected()) showAll(file_name, param_No7, "current_influx_of_spec_dissipated_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No8.isSelected()) showAll(file_name, param_No8, "specific_dissipated_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                }
                else 
                if(file_name.endsWith(".res"))
                {
                         if(param_No1.isSelected()) showAll(file_name, param_No1, "temperature", time_steps_values.get(index), scale, new_tab.isSelected());                                                        
                    else if(param_No2.isSelected()) showAll(file_name, param_No2, "effective_stresses", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No3.isSelected()) showAll(file_name, param_No3, "principal_stresses", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No4.isSelected()) showAll(file_name, param_No4, "x_component_of_specific_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());                            
                    else if(param_No5.isSelected()) showAll(file_name, param_No5, "y_component_of_specific_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No6.isSelected()) showAll(file_name, param_No6, "z_component_of_specific_force_moment", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No7.isSelected()) showAll(file_name, param_No7, "strain", time_steps_values.get(index), scale, new_tab.isSelected());
                    else if(param_No8.isSelected()) showAll(file_name, param_No8, "total_specific_torsion_energy", time_steps_values.get(index), scale, new_tab.isSelected());                            
                }
                
                if(structure.isSelected())
                {
                    String type = structure_type.getSelectionModel().getSelectedItem().toString();
                    showAllStructure(file_name, type, index ,new_tab.isSelected());                    
                }
                
            }
            else 
            if(show_plane.isSelected())
            {
                if(this.plane_type.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select the Plane (X, Y or Z)").showAndWait();
                
                if(this.layer_number.getText().isEmpty())
                    new Alert(AlertType.ERROR, "Please input layer number").showAndWait();
                
                int layer_number = Integer.parseInt(this.layer_number.getText());
                int plane_index = this.plane_type.getSelectionModel().getSelectedIndex();
                
                char plane = 'X';
                
                switch (plane_index)
                {
                    case 0:
                        plane = 'X';
                        if(layer_number <= 0 || layer_number > data.cell_number_x)
                            new Alert(AlertType.ERROR, "Incorrect layer number!!!").showAndWait();
                        break;
                    case 1:
                        plane = 'Y';
                        if(layer_number <= 0 || layer_number > data.cell_number_y)
                            new Alert(AlertType.ERROR, "Incorrect layer number!!!").showAndWait();
                        break;
                    case 2:
                        plane = 'Z';
                        if(layer_number <= 0 || layer_number > data.cell_number_z)
                            new Alert(AlertType.ERROR, "Incorrect layer number!!!").showAndWait();
                        break;                        
                }      
                if(structure.isSelected())
                {
                    String type = structure_type.getSelectionModel().getSelectedItem().toString();
                    showPlaneStructure(file_name, structure, type, plane, layer_number, index, new_tab.isSelected());
                }
                
                if(file_name.endsWith("_relValues.res"))
                {
                         if(param_No1.isSelected()) showPlane(file_name, param_No1, "relative_temperature", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());                            
                    else if(param_No2.isSelected()) showPlane(file_name, param_No2, "relative_specific_elastic_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());                            
                    else if(param_No3.isSelected()) showPlane(file_name, param_No3, "total_specific_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());                                                        
                    else if(param_No4.isSelected()) showPlane(file_name, param_No4, "abs_value_of_X_comp_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());                            
                    else if(param_No5.isSelected()) showPlane(file_name, param_No5, "abs_value_of_Y_comp_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No6.isSelected()) showPlane(file_name, param_No6, "abs_value_of_Z_comp_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No7, "abs_value_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());                            
                    else if(param_No8.isSelected()) showPlane(file_name, param_No8, "relative_specific_dissipated_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());                            
                }
                else 
                if(file_name.endsWith("jocl.res"))
                {
                         if(param_No1.isSelected()) showPlane(file_name, param_No1, "temperature", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No2.isSelected()) showPlane(file_name, param_No2, "specific_elastic_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No3.isSelected()) showPlane(file_name, param_No3, "principal_stresses", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No4.isSelected()) showPlane(file_name, param_No4, "x_component_of_specific_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No5.isSelected()) showPlane(file_name, param_No5, "y_component_of_specific_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No6.isSelected()) showPlane(file_name, param_No6, "z_component_of_specific_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No7, "current_influx_of_specific_dissipated_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No8.isSelected()) showPlane(file_name, param_No8, "specific_dissipated_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                }                    
                
                else if(file_name.endsWith("_torsion.res"))
                {
                         if(param_No1.isSelected()) showPlane(file_name, param_No1, "x_component_of_angle_velocity", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No2.isSelected()) showPlane(file_name, param_No2, "y_component_of_angle_velocity", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No3.isSelected()) showPlane(file_name, param_No3, "z_component_of_angle_velocity", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No4.isSelected()) showPlane(file_name, param_No4, "x_component_of_torsion_angle_vector", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No5.isSelected()) showPlane(file_name, param_No5, "y_component_of_torsion_angle_vector", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No6.isSelected()) showPlane(file_name, param_No6, "z_component_of_torsion_angle_vector", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No7, "relative_power_of_torsion", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No8.isSelected()) showPlane(file_name, param_No8, "relative_torsion_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No9.isSelected()) showPlane(file_name, param_No9, "total_specific_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                }
                else                         
                    if(file_name.endsWith("_inst_mom.res"))
                    {
                         if(param_No1.isSelected()) showPlane(file_name, param_No1, "abs_value_of_X_comp_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No2.isSelected()) showPlane(file_name, param_No2, "abs_value_of_Y_comp_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No3.isSelected()) showPlane(file_name, param_No3, "abs_value_of_Z_comp_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No4.isSelected()) showPlane(file_name, param_No4, "x_component_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No5.isSelected()) showPlane(file_name, param_No5, "y_component_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No6.isSelected()) showPlane(file_name, param_No6, "z_component_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No7, "abs_value_of_inst_spec_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No8, "current_influx_of_torsion_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                }
                else if(file_name.endsWith("jocl_stresses.res")){
                         if(param_No1.isSelected()) showPlane(file_name, param_No1, "temperature", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No2.isSelected()) showPlane(file_name, param_No2, "abs_value_of_stress_vector", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No3.isSelected()) showPlane(file_name, param_No3, "value_for_crack_generation", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No4.isSelected()) showPlane(file_name, param_No4, "x_comp_of_stress_vector", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No5.isSelected()) showPlane(file_name, param_No5, "y_comp_of_stress_vector", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No6.isSelected()) showPlane(file_name, param_No6, "z_comp_of_stress_vector", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No7, "current_influx_of_spec_dissipated_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No8, "specific_dissipated_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                }
                else 
                if(file_name.endsWith(".res"))
                {
                         if(param_No1.isSelected()) showPlane(file_name, param_No1, "temperature", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No2.isSelected()) showPlane(file_name, param_No2, "effective_stresses", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No3.isSelected()) showPlane(file_name, param_No3, "principal_stresses", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No4.isSelected()) showPlane(file_name, param_No4, "x_component_of_specific_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No5.isSelected()) showPlane(file_name, param_No5, "y_component_of_specific_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No6.isSelected()) showPlane(file_name, param_No5, "z_component_of_specific_force_moment", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No6, "strain", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                    else if(param_No7.isSelected()) showPlane(file_name, param_No7, "total_specific_torsion_energy", plane, layer_number, time_steps_values.get(index), scale,  new_tab.isSelected());
                }               
            }
        });
    }
    
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    
    private void handle3DArea(SubScene sub_scene, RecCA_3DPictureCreator picture){
        
        sub_scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
            
            if(me.isSecondaryButtonDown()){
                showContextMenu(sub_scene, picture, me.getScreenX(), me.getScreenY());
            }
        });
        
        sub_scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();

            mouseDeltaX = (mousePosX - mouseOldX); 
            mouseDeltaY = (mousePosY - mouseOldY); 
            
            if(me.isPrimaryButtonDown()){
                picture.cameraXform.ry.setAngle(picture.cameraXform.ry.getAngle() - mouseDeltaX*UICommon.MOUSE_SPEED*UICommon.ROTATION_SPEED);  
                picture.cameraXform.rx.setAngle(picture.cameraXform.rx.getAngle() + mouseDeltaY*UICommon.MOUSE_SPEED*UICommon.ROTATION_SPEED);
            }
            
            if(me.isMiddleButtonDown()){
                picture.camera.setTranslateX(picture.camera.getTranslateX() - mouseDeltaX*UICommon.MOUSE_SPEED*UICommon.TRACK_SPEED);
                picture.camera.setTranslateY(picture.camera.getTranslateY() - mouseDeltaY*UICommon.MOUSE_SPEED*UICommon.TRACK_SPEED);
            }
        });
        
        sub_scene.setOnScroll(se -> {
            double z = picture.camera.getTranslateZ();
            double newZ = z + se.getDeltaY()/2;
            picture.camera.setTranslateZ(newZ);      
        });                   
    }
    
    private String initial_directory = "";
    private String initial_name = "";
    
    private void showContextMenu(SubScene sub_scene, RecCA_3DPictureCreator pict, double pos_x, double pos_y){
        ContextMenu menu = new ContextMenu();
        
        MenuItem show_axis = new MenuItem("Show Axis");
        MenuItem save_as_image = new MenuItem("Save");
        
        Menu set_camera = new Menu("Set Camera");
        MenuItem xy = new MenuItem("XY");
        MenuItem xz = new MenuItem("XZ");
        MenuItem yz = new MenuItem("YZ");
        set_camera.getItems().addAll(xy, xz, yz);
        
        MenuItem animation = new Menu("animation!!!!");
        
        menu.getItems().addAll(show_axis, set_camera, new SeparatorMenuItem(), save_as_image, animation);
        menu.show(this, pos_x, pos_y);
        
        show_axis.setOnAction(e -> {
            if(pict.axisGroup.isVisible())
                pict.axisGroup.setVisible(false);
            else 
                pict.axisGroup.setVisible(true);
        });
        
        xy.setOnAction(e -> {
            pict.cameraXform.rx.setAngle(0.0);
            pict.cameraXform.ry.setAngle(0.0);
            pict.cameraXform.rz.setAngle(180.0);
        });
        
        xz.setOnAction(e -> {
            pict.cameraXform.rx.setAngle(0.0);
            pict.cameraXform.ry.setAngle(180.0);
            pict.cameraXform.rz.setAngle(0.0);
        });
        
        yz.setOnAction(e -> {
            pict.cameraXform.rx.setAngle(180.0);
            pict.cameraXform.ry.setAngle(0.0);
            pict.cameraXform.rz.setAngle(0.0);
        });
        
        animation.setOnAction(e -> new RecCAAnimationDemo(pict));
        
        save_as_image.setOnAction(e -> {
            RecCASaveImage save_image = new RecCASaveImage(sub_scene, initial_directory, initial_name);
            
            save_image.save.setOnAction(event -> {
                System.out.println("RecCASaveImage action performed save button is pushed");

                String dir = save_image.directory.getText();
                String ext = save_image.extension.getSelectionModel().getSelectedItem().toString();

                WritableImage image = save_image.node.snapshot(new SnapshotParameters(), null);
                
                try{
                    
                    if(save_image.directory.getText().isEmpty()){
                        new Alert(AlertType.ERROR, "Please choose directory for image").showAndWait();
                    }
                    else if(save_image.name.getText().isEmpty()){
                        new Alert(AlertType.ERROR, "Please input the file name").showAndWait();
                    }
                    else{
                        File file = new File(dir + "/" + save_image.name.getText() + "." + ext);
                        try {
                            ImageIO.write(SwingFXUtils.fromFXImage(image, null), ext, file);
                        } catch (IOException ex) {
                            System.out.println("Error: RecCASaveImage save image event");
                        }
                        
                        initial_directory = save_image.directory.getText();
                        initial_name = save_image.name.getText();

                        new Alert(AlertType.INFORMATION, "Image successfully saved!").showAndWait();
                        save_image.close();
                    }
                }
                catch(Exception exc){
                    new Alert(AlertType.ERROR, "Failed to save the image!");
                }
                
            });
        });
    }
    
    private void handleTabSelection(String cb_item, RadioButton rb, Tab tab, RecCA_3DPictureCreator pict,  RecCAScaleLayout layout, RecCACameraControlLayout camera_cont, boolean isShowAll, char plane, int layer_number){
        
        layout.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2){
                RecCAChangeScale change_scale = new RecCAChangeScale(layout);
            }
        });
        
        if(!views.getSelectionModel().isEmpty()){
            String file_name = views.getSelectionModel().getSelectedItem().toString();
            System.out.println("Selected View File: " + file_name);
            try {
                setCamera(pict, file_name);
            } catch (IOException ex) {
                System.out.println("Error: can't read view file");
            }
        }
        
        tab.setOnSelectionChanged(e -> {
            
            if(isShowAll){
                show_all.setSelected(true);
                this.plane.setExpanded(false);
                this.plane.setDisable(true);
            }
            else{
                show_plane.setSelected(true);
                this.plane.setExpanded(true);
                this.plane.setDisable(false);
                
                if(!this.plane_type.getSelectionModel().getSelectedItem().equals(plane))
                    this.plane_type.getSelectionModel().select(plane);
                
                if(!this.layer_number.getText().equals(String.valueOf(layer_number)))
                    this.layer_number.setText(String.valueOf(layer_number));
                
            }
            
            set_view.setOnAction(event -> {
                String file_name = views.getSelectionModel().getSelectedItem().toString();
                System.out.println("Selected View File: " + file_name);
                try {
                    setCamera(pict, file_name);
                } catch (IOException ex) {
                    System.out.println("Error: can't read view file");
                }
            });
            
            save_view.setOnAction(event -> {
                RecCASaveView save = new RecCASaveView(trans_data_bank, pict, task_name);
                save.close.setOnAction(ev -> {
                    save.close();
                    refreshViewFiles();
                });
            });
            
            if(!rb.isSelected())
                rb.setSelected(true);
        
            if(!time_steps.getSelectionModel().getSelectedItem().equals(cb_item))
                this.time_steps.getSelectionModel().select(cb_item);
            
            if(!layout.equals(null))
                scale.setContent(layout);
            
            camera_control.setContent(camera_cont);                        
        });                        
    }
    
    private void handleShowThis(RecCA_3DPictureCreator prev_pict, RecCAScaleLayout layout, RecCAScale scale){
        
        layout.show_this.setOnAction(e -> {
            
            Tab tab;
            
            if(layout.new_tab.isSelected()){
                tab = new Tab();
                tab.setText("New Tab");
                tab_pane.getTabs().add(tab);
                tab_pane.getSelectionModel().select(tab);
            }
            else{
                tab = tab_pane.getSelectionModel().getSelectedItem();
            }
            
            String file_name = time_steps.getSelectionModel().getSelectedItem().toString();
            
            String file_path = Common.TASK_PATH + "/" + task_name + "/" + file_name;
            
            int index = 0;
            
            if(!file_path.endsWith("relValues.res") || !file_path.endsWith("jocl_stresses.res") || !file_name.endsWith("jocl.res"))
                index = time_steps.getSelectionModel().getSelectedIndex() / 3;
            else 
                index = time_steps.getSelectionModel().getSelectedIndex() / 2;
            
            double max_val = 0.0;
            double min_val = 0.0;
            
            try{
                max_val = Double.parseDouble(layout.max_txt_fld.getText());
                min_val = Double.parseDouble(layout.min_txt_fld.getText());
            }
            catch(NumberFormatException exc){
                new Alert(AlertType.ERROR, "Incorrect data input").showAndWait();
            }
            
            System.out.println("Min Value: " + min_val);
            System.out.println("Max Value: " + max_val);
            
            String type = layout.scale_type.getSelectionModel().getSelectedItem().toString();
            System.out.println("Selected Scale Type: " + type);
            
            scale.setMaxValueToScale(max_val);
            scale.setMinValueToScale(min_val);
            
            scale.setReverse(layout.reverse.isSelected());
            
            System.out.println("Scale is reverse: " + layout.reverse.isSelected());
            
            if(show_all.isSelected()){
                if(file_name.endsWith("_relValues.res")){
                    if(param_No1.isSelected())
                        showAllWithScale(file_name, param_No1, tab, "relative_temperature", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No2.isSelected())
                        showAllWithScale(file_name, param_No2, tab, "relative_specific_elastic_energy", prev_pict, layout, scale, max_val, min_val, type);                                                
                    else if(param_No3.isSelected())
                        showAllWithScale(file_name, param_No3, tab, "relative_specific_elastic_energy", prev_pict, layout, scale, max_val, min_val, type);                                                        
                    else if(param_No4.isSelected())
                        showAllWithScale(file_name, param_No4, tab, "abs_value_of_X_comp_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No5.isSelected())
                        showAllWithScale(file_name, param_No5, tab, "abs_value_of_Y_comp_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No6.isSelected())
                        showAllWithScale(file_name, param_No6, tab, "abs_value_of_Z_comp_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No7.isSelected())
                        showAllWithScale(file_name, param_No7, tab, "abs_value_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No8.isSelected())
                        showAllWithScale(file_name, param_No8, tab, "relative_specific_dissipated_energy", prev_pict, layout, scale, max_val, min_val, type);                            
                }
                else if(file_name.endsWith("jocl_stresses.res")){
                    if(param_No1.isSelected())
                        showAllWithScale(file_name, param_No1, tab, "temperature", prev_pict, layout, scale, max_val, min_val, type);                    
                    else if(param_No2.isSelected())
                        showAllWithScale(file_name, param_No2, tab, "abs_value_of_stress_vector", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showAllWithScale(file_name, param_No3, tab, "value_for_crack_generation", prev_pict, layout, scale, max_val, min_val, type);  
                    else if(param_No4.isSelected())
                        showAllWithScale(file_name, param_No4, tab, "x_comp_of_stress_vector", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No5.isSelected())
                        showAllWithScale(file_name, param_No5, tab, "y_comp_of_stress_vector", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showAllWithScale(file_name, param_No6, tab, "z_comp_of_stress_vector", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showAllWithScale(file_name, param_No7, tab, "current_influx_of_spec_dissipated_energy", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No8.isSelected())
                        showAllWithScale(file_name, param_No8, tab, "specific_dissipated_energy", prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_name.endsWith("jocl.res")){
                    if(param_No1.isSelected())
                        showAllWithScale(file_name, param_No1, tab, "temperature", prev_pict, layout, scale, max_val, min_val, type);                                                        
                    else if(param_No2.isSelected())
                        showAllWithScale(file_name, param_No2, tab, "specific_elastic_energy", prev_pict, layout, scale, max_val, min_val, type);                                                                                    
                    else if(param_No3.isSelected())
                        showAllWithScale(file_name, param_No3, tab, "principal_stresses", prev_pict, layout, scale, max_val, min_val, type);                                                                                                                
                    else if(param_No4.isSelected())
                        showAllWithScale(file_name, param_No4, tab, "x_component_of_specific_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No5.isSelected())
                        showAllWithScale(file_name, param_No5, tab, "y_component_of_specific_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showAllWithScale(file_name, param_No6, tab, "z_component_of_specific_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showAllWithScale(file_name, param_No7, tab, "current_influx_of_specific_dissipated_energy", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No8.isSelected())
                        showAllWithScale(file_name, param_No8, tab, "specific_dissipated_energy", prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_name.endsWith("torsion.res")){
                    if(param_No1.isSelected())
                        showAllWithScale(file_name, param_No1, tab, "x_component_of_angle_velocity", prev_pict, layout, scale, max_val, min_val, type);                             
                    else if(param_No2.isSelected())
                        showAllWithScale(file_name, param_No2, tab, "y_component_of_angle_velocity", prev_pict, layout, scale, max_val, min_val, type); 
                    else if(param_No3.isSelected())
                        showAllWithScale(file_name, param_No3, tab, "z_component_of_angle_velocity", prev_pict, layout, scale, max_val, min_val, type); 
                    else if(param_No4.isSelected())
                        showAllWithScale(file_name, param_No4, tab, "x_component_of_torsion_angle_vector", prev_pict, layout, scale, max_val, min_val, type); 
                    else if(param_No5.isSelected())
                        showAllWithScale(file_name, param_No5, tab, "y_component_of_torsion_angle_vector", prev_pict, layout, scale, max_val, min_val, type); 
                    else if(param_No6.isSelected())
                        showAllWithScale(file_name, param_No6, tab, "z_component_of_torsion_angle_vector", prev_pict, layout, scale, max_val, min_val, type); 
                    else if(param_No7.isSelected())
                        showAllWithScale(file_name, param_No7, tab, "relative_power_of_torsion", prev_pict, layout, scale, max_val, min_val, type);                             
                    else if(param_No8.isSelected())
                        showAllWithScale(file_name, param_No8, tab, "relative_torsion_energy", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No9.isSelected())
                        showAllWithScale(file_name, param_No9, tab, "total_specific_energy", prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_name.endsWith("inst_mom.res")){
                    if(param_No1.isSelected())
                        showAllWithScale(file_name, param_No1, tab, "abs_value_of_X_comp_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No2.isSelected())
                        showAllWithScale(file_name, param_No2, tab, "abs_value_of_Y_comp_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showAllWithScale(file_name, param_No3, tab, "abs_value_of_Z_comp_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No4.isSelected())
                        showAllWithScale(file_name, param_No4, tab, "x_component_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No5.isSelected())
                        showAllWithScale(file_name, param_No5, tab, "y_component_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showAllWithScale(file_name, param_No6, tab, "z_component_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showAllWithScale(file_name, param_No7, tab, "abs_value_of_inst_spec_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No8.isSelected())
                        showAllWithScale(file_name, param_No8, tab, "current_influx_of_torsion_energy", prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_name.endsWith(".res")){
                    if(param_No1.isSelected())
                        showAllWithScale(file_name, param_No1, tab, "temperature", prev_pict, layout, scale, max_val, min_val, type);                    
                    else if(param_No2.isSelected())
                        showAllWithScale(file_name, param_No2, tab, "effective_stresses", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showAllWithScale(file_name, param_No3, tab, "principal_stresses", prev_pict, layout, scale, max_val, min_val, type);  
                    else if(param_No4.isSelected())
                        showAllWithScale(file_name, param_No4, tab, "x_component_of_specific_force_moment", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No5.isSelected())
                        showAllWithScale(file_name, param_No5, tab, "y_component_of_specific_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showAllWithScale(file_name, param_No6, tab, "z_component_of_specific_force_moment", prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showAllWithScale(file_name, param_No7, tab, "strain", prev_pict, layout, scale, max_val, min_val, type);                            
                    else if(param_No8.isSelected())
                        showAllWithScale(file_name, param_No8, tab, "total_specific_torsion_energy", prev_pict, layout, scale, max_val, min_val, type);
                }
            }
            
            else if(show_plane.isSelected()){
                
                int layer_number = Integer.parseInt(this.layer_number.getText());
                int plane_index = this.plane_type.getSelectionModel().getSelectedIndex();
                char plane = 'X';
                
                switch (plane_index)
                {
                    case 0:
                        plane = 'X';
                        break;
                    case 1:
                        plane = 'Y';
                        break;
                    case 2:
                        plane = 'Z';
                        break;                        
                }
                
                if(file_path.endsWith("relValues.res")){
                    if(param_No1.isSelected())
                        showPlaneWithScale(file_name, param_No1, tab, "relative_temperature", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No2.isSelected())
                        showPlaneWithScale(file_name, param_No2, tab, "relative_specific_elastic_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showPlaneWithScale(file_name, param_No3, tab, "total_specific_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No4.isSelected())
                        showPlaneWithScale(file_name, param_No4, tab, "abs_value_of_X_comp_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No5.isSelected())
                        showPlaneWithScale(file_name, param_No5, tab, "abs_value_of_Y_comp_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showPlaneWithScale(file_name, param_No6, tab, "abs_value_of_Z_comp_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showPlaneWithScale(file_name, param_No7, tab, "abs_value_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No8.isSelected())
                        showPlaneWithScale(file_name, param_No8, tab, "relative_specific_dissipated_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_path.endsWith("jocl_stresses.res")){
                    if(param_No1.isSelected())
                        showPlaneWithScale(file_name, param_No1, tab, "temperature", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No2.isSelected())
                        showPlaneWithScale(file_name, param_No2, tab, "abs_value_of_stress_vector", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showPlaneWithScale(file_name, param_No3, tab, "value_for_crack_generation", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No4.isSelected())
                        showPlaneWithScale(file_name, param_No4, tab, "x_comp_of_stress_vector", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No5.isSelected())
                        showPlaneWithScale(file_name, param_No5, tab, "y_comp_of_stress_vector", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showPlaneWithScale(file_name, param_No6, tab, "z_comp_of_stress_vector", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showPlaneWithScale(file_name, param_No7, tab, "current_influx_of_spec_dissipated_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No8.isSelected())
                        showPlaneWithScale(file_name, param_No8, tab, "specific_dissipated_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_path.endsWith("jocl.res")){
                    if(param_No1.isSelected())
                        showPlaneWithScale(file_name, param_No1, tab, "temperature", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No2.isSelected())
                        showPlaneWithScale(file_name, param_No2, tab, "specific_elastic_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showPlaneWithScale(file_name, param_No3, tab, "principal_stresses", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No4.isSelected())
                        showPlaneWithScale(file_name, param_No4, tab, "x_component_of_specific_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No5.isSelected())
                        showPlaneWithScale(file_name, param_No5, tab, "y_component_of_specific_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showPlaneWithScale(file_name, param_No6, tab, "z_component_of_specific_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showPlaneWithScale(file_name, param_No7, tab, "current_influx_of_specific_dissipated_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No8.isSelected())
                        showPlaneWithScale(file_name, param_No8, tab, "specific_dissipated_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_path.endsWith("inst_mom.res")){
                    if(param_No1.isSelected())
                        showPlaneWithScale(file_name, param_No1, tab, "abs_value_of_X_comp_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No2.isSelected())
                        showPlaneWithScale(file_name, param_No2, tab, "abs_value_of_Y_comp_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showPlaneWithScale(file_name, param_No3, tab, "abs_value_of_Z_comp_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No4.isSelected())
                        showPlaneWithScale(file_name, param_No4, tab, "x_component_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No5.isSelected())
                        showPlaneWithScale(file_name, param_No5, tab, "y_component_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showPlaneWithScale(file_name, param_No6, tab, "z_component_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showPlaneWithScale(file_name, param_No7, tab, "abs_value_of_inst_spec_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No8.isSelected())
                        showPlaneWithScale(file_name, param_No8, tab, "current_influx_of_torsion_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_path.endsWith("torsion.res")){
                    if(param_No1.isSelected())
                        showPlaneWithScale(file_name, param_No1, tab, "x_component_of_angle_velocity", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No2.isSelected())
                        showPlaneWithScale(file_name, param_No2, tab, "y_component_of_angle_velocity", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showPlaneWithScale(file_name, param_No3, tab, "z_component_of_angle_velocity", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No4.isSelected())
                        showPlaneWithScale(file_name, param_No4, tab, "x_component_of_torsion_angle_vector", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No5.isSelected())
                        showPlaneWithScale(file_name, param_No5, tab, "y_component_of_torsion_angle_vector", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showPlaneWithScale(file_name, param_No6, tab, "z_component_of_torsion_angle_vector", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showPlaneWithScale(file_name, param_No7, tab, "relative_power_of_torsion", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No8.isSelected())
                        showPlaneWithScale(file_name, param_No8, tab, "relative_torsion_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No9.isSelected())
                        showPlaneWithScale(file_name, param_No9, tab, "total_specific_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                }
                else if(file_path.endsWith(".res")){
                    if(param_No1.isSelected())
                        showPlaneWithScale(file_name, param_No1, tab, "temperature", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No2.isSelected())
                        showPlaneWithScale(file_name, param_No2, tab, "effective_stresses", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No3.isSelected())
                        showPlaneWithScale(file_name, param_No3, tab, "principal_stresses", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No4.isSelected())
                        showPlaneWithScale(file_name, param_No4, tab, "x_component_of_specific_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No5.isSelected())
                        showPlaneWithScale(file_name, param_No5, tab, "y_component_of_specific_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No6.isSelected())
                        showPlaneWithScale(file_name, param_No6, tab, "z_component_of_specific_force_moment", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No7.isSelected())
                        showPlaneWithScale(file_name, param_No7, tab, "strain", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                    else if(param_No8.isSelected())
                        showPlaneWithScale(file_name, param_No8, tab, "total_specific_torsion_energy", plane, layer_number, index, prev_pict, layout, scale, max_val, min_val, type);
                }
            }            
        });
    }
    
    private void setCamera(RecCA_3DPictureCreator picture, String file_name) throws FileNotFoundException, IOException{
        System.out.println("RecCAReesultsParams setCamera() method: start");
        
        String file_path = "user/task_db/" + file_name;
        
        File file = new File(file_path);
        
        System.out.println("Read: " + file.getAbsolutePath());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null){
            if(line.startsWith("rotate_x = "))
                picture.cameraXform.setRotateX(Double.parseDouble(line.replaceAll("rotate_x = ", "")));
            else if(line.startsWith("rotate_y = "))
                picture.cameraXform.setRotateY(Double.parseDouble(line.replaceAll("rotate_y = ", "")));
            else if(line.startsWith("rotate_z = "))
                picture.cameraXform.setRotateZ(Double.parseDouble(line.replaceAll("rotate_z = ", "")));
            else if(line.startsWith("zoom = "))
                picture.camera.setTranslateZ(Double.parseDouble(line.replaceAll("zoom = ", "")));
            else if(line.startsWith("translate_x = "))
                picture.camera.setTranslateX(Double.parseDouble(line.replaceAll("translate_x = ", "")));
            else if(line.startsWith("translate_y = "))
                picture.camera.setTranslateY(Double.parseDouble(line.replaceAll("translate_y = ", "")));
            else if(line.startsWith("show_axis = ")){
                picture.axisGroup.setVisible(Boolean.parseBoolean(line.replaceAll("show_axis = ", "")));
            }
        }
    }
    
    private void showAll(String cb_item, RadioButton rb, String param, String index, RecCAScale scale, boolean is_new_tab){
        System.out.println("Show: " + param);
        String title = getTabTitle(param);
        Tab tab;
        RecCA_3DPictureCreator pict = new RecCA_3DPictureCreator(trans_data_bank);
        RecCACameraControlLayout camera = new RecCACameraControlLayout(pict);
                        
        if(is_new_tab){
            tab = new Tab();
            tab_pane.getTabs().addAll(tab);
        }
        else{
            tab = tab_pane.getSelectionModel().getSelectedItem();
        }
        
        tab.setText(title + " #" + index);
        setContextMenu(tab);
        pict.showAll(data, param , scale, "rainbow");
                
        SubScene sub_scene = new SubScene(pict.world, scene_width, scene_height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);

        RecCAScaleLayout scale_layout = new RecCAScaleLayout(this, scale ,pict.max_parameter_value, pict.min_parameter_value, param);
        
        if(!is_new_tab)
            this.scale.setContent(scale_layout);
        
        sub_scene.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2){
                RecCA_3DPictureParams params = new RecCA_3DPictureParams(pict);
                params.ok.setOnAction(e -> {
                    pict.showAll(data, param, scale, "rainbow", params);
                });
            }
        });
        
        this.camera_control.setContent(camera);
        this.scale.setContent(scale_layout);
        
        handle3DArea(sub_scene, pict);                            
        handleTabSelection(cb_item, rb, tab, pict, scale_layout, camera, true, '0', 0);
        handleShowThis(pict, scale_layout, scale);

        tab.setContent(sub_scene);
        
        tab_pane.getSelectionModel().selectLast();
        this.scale.setExpanded(true);
        this.scale.setDisable(false);
    }
    
    private void showAllStructure(String cb_item, String type, int index, boolean is_new_tab){
        
        Tab tab;
        String tab_title = null;
        
        switch(type){
            case "with boundaries":
                tab_title = "Structure With Boundaries #" + index;
                struct_type = "structure";
                break;
            case "without boundaries":
                tab_title = "Structure Without Boundaries #" + index;
                struct_type = "structure (without boundaries)";
                break;
            case "boundary automata view":
                tab_title = "Boundary Automata View #" + index;
                struct_type = "structure (boundary automata view)";
                break;
            case "materials":
                tab_title = "Materials #" + index;
                struct_type = "materials";
                break;
        }
        
        if(is_new_tab){
            tab = new Tab();
            tab_pane.getTabs().addAll(tab);
        }
        else{
            tab = tab_pane.getSelectionModel().getSelectedItem();
        }
        tab.setText(tab_title);
        setContextMenu(tab);
        System.out.println("Show Structure " + type);
        RecCA_3DPictureCreator pict = new RecCA_3DPictureCreator(trans_data_bank);
        RecCACameraControlLayout camera = new RecCACameraControlLayout(pict);
        
        try {
            if(structure_type.equals("materials")){
                pict.showAllMaterials(task_name, data);
                createMaterialsTable();
            }
            else
                pict.showAllStructure(task_name, data, struct_type);
        } catch (IOException ex) {
            System.out.println("Error: showAllStructure() method");
        }
        
        SubScene sub_scene = new SubScene(pict.world, scene_width, scene_height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);
        handle3DArea(sub_scene, pict);
        
        sub_scene.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2){
                RecCA_3DPictureParams params = new RecCA_3DPictureParams(pict);
                params.ok.setOnAction(e -> {
                    try {
                        pict.showAllStructure(task_name, data, struct_type, params);
                    } catch (IOException ex) {
                        System.out.println("Error : showAllStructure()");
                    }
                });
            }
        });
        
        RecCAScaleLayout scale_layout = new RecCAScaleLayout( index + " / " + struct_type);
        
        this.camera_control.setContent(camera);
        this.scale.setContent(scale_layout);
        
        handleTabSelection(cb_item, structure, tab, pict, scale_layout, camera, is_new_tab,'0', 0);
        
        tab.setContent(sub_scene);
        tab_pane.getSelectionModel().selectLast();
        this.scale.setExpanded(false);        
    }
    
    private void showAllWithScale(String cb_item, RadioButton rb, Tab selected_tab, String param, RecCA_3DPictureCreator prev_pict, RecCAScaleLayout layout, RecCAScale scale, double max_val, double min_val, String type){
        System.out.println("Show Parameter: " + param);
        RecCA_3DPictureCreator pict = new RecCA_3DPictureCreator(trans_data_bank);
        RecCACameraControlLayout camera = new RecCACameraControlLayout(pict);
        layout.setTickLabel(max_val, min_val, type);
        layout.createScaleImage(type);
        layout.setTitle();
        
        pict.showAllWithScale(data, param, scale, type);
        pict.cameraXform.setRotateX(prev_pict.cameraXform.rx.getAngle());
        pict.cameraXform.setRotateY(prev_pict.cameraXform.ry.getAngle());
        pict.cameraXform.setRotateZ(prev_pict.cameraXform.rz.getAngle());
        pict.camera.setTranslateX(prev_pict.camera.getTranslateX());
        pict.camera.setTranslateY(prev_pict.camera.getTranslateY());
        pict.camera.setTranslateZ(prev_pict.camera.getTranslateZ());
        
        SubScene sub_scene = new SubScene(pict.world, scene_width, scene_height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);
        sub_scene.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2){
                RecCA_3DPictureParams params = new RecCA_3DPictureParams(pict);
                params.ok.setOnAction(e -> {
                    pict.showAllWithScale(data, param, scale, type, params);
                });
            }
        });
        
        this.camera_control.setContent(camera);
        handleTabSelection(cb_item, rb, selected_tab, pict, layout, camera, true, '0', 0);
        handle3DArea(sub_scene, pict);
        handleShowThis(pict, layout, scale);
        selected_tab.setContent(sub_scene);
    }
    
    private void showPlane(String cb_item, RadioButton rb, String param, char plane, int layer_number, String index, RecCAScale scale, boolean is_new_tab){
        System.out.println("Show Plane:\nplane type: " + plane+"\nlayer number: " + layer_number + "\nparameter: " + param);
        
        String title = getTabTitle(param);
        
        Tab tab;
        
        if(is_new_tab){
            tab = new Tab();
            tab_pane.getTabs().add(tab);
            tab_pane.getSelectionModel().selectLast();
        }
        else{
            tab = tab_pane.getSelectionModel().getSelectedItem();
        }
        
        tab.setText(title + " #" + index);
        setContextMenu(tab);
        RecCA_3DPictureCreator pict = new RecCA_3DPictureCreator(trans_data_bank);
        RecCACameraControlLayout camera = new RecCACameraControlLayout(pict);

        try {
            pict.showPlane(task_name, data, plane, layer_number, param, scale, "rainbow");
        } catch (IOException ex) {
            System.out.println("Error: showPlane() relative_temperature");
        }
        
        SubScene sub_scene = new SubScene(pict.world, scene_width, scene_height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);
        
        RecCAScaleLayout scale_layout = new RecCAScaleLayout(this, scale ,pict.max_parameter_value, pict.min_parameter_value, param);
        
        sub_scene.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2){
                RecCA_3DPictureParams params = new RecCA_3DPictureParams(pict);
                params.ok.setOnAction(e -> {
                    try {
                        pict.showPlane(task_name, data, plane, layer_number, param, scale, "rainbow", params);
                    } catch (IOException ex) {
                        System.out.println("Error : showPlane()");
                    }
                });
            }
        });
        
        this.camera_control.setContent(camera);
        this.scale.setContent(scale_layout);
        
        handle3DArea(sub_scene, pict);                            
        handleTabSelection(cb_item, rb, tab, pict, scale_layout, camera, false, plane, layer_number);
        handleShowThis(pict, scale_layout, scale);

        tab.setContent(sub_scene);
        this.scale.setExpanded(true);
        this.scale.setDisable(false);
    }
    
    private String struct_type;
    
    private void showPlaneStructure(String cb_item, RadioButton rb, String type, char plane, int layer_number, int index, boolean is_new_tab){
        
        Tab tab;
        String tab_title = null;
        
        switch(type){
            case "with boundaries":
                tab_title = "Structure With Boundaries #" + index;
                struct_type = "structure";
                break;
            case "without boundaries":
                tab_title = "Structure Without Boundaries #" + index;
                struct_type = "structure (without boundaries)";
                break;
            case "boundary automata view":
                tab_title = "Boundary Automata View #" + index;
                struct_type = "structure (boundary automata view)";
                break;
            case "materials":
                tab_title = "Materials #" + index;
                break;
        }
        
        if(is_new_tab){
            tab = new Tab();
            tab_pane.getTabs().add(tab);
            tab_pane.getSelectionModel().selectLast();
        }
        else{
            tab = tab_pane.getSelectionModel().getSelectedItem();
        }
        tab.setText(tab_title);
        setContextMenu(tab);
        System.out.println("Show Structure " + type);
        RecCA_3DPictureCreator pict = new RecCA_3DPictureCreator(trans_data_bank);
        RecCACameraControlLayout camera = new RecCACameraControlLayout(pict);
        
        try 
        {
            pict.showPlaneStructure(task_name, data, plane, layer_number, struct_type);
        } 
        catch (IOException ex) 
        {
            System.out.println("Error: showPlaneStructure()");
        }
        
        SubScene sub_scene = new SubScene(pict.world, scene_width, scene_height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);
        
        sub_scene.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2){
                RecCA_3DPictureParams params = new RecCA_3DPictureParams(pict);
                params.ok.setOnAction(e -> {
                    try {
                        pict.showPlaneStructure(task_name, data, plane, layer_number, struct_type, params);
                    } catch (IOException ex) {
                        System.out.println("Error : showPlaneStructure()");
                    }
                });
            }
        });
        
        handle3DArea(sub_scene, pict);
        RecCAScaleLayout scale_layout = new RecCAScaleLayout( index + " / " + struct_type);
        
        this.camera_control.setContent(camera);
        this.scale.setContent(scale_layout);
        
        handleTabSelection(cb_item, rb, tab, pict, scale_layout, camera, false, plane, layer_number);
        tab.setContent(sub_scene);        
        this.scale.setExpanded(false);
        this.scale.setDisable(true);       
    }
    
    private void showPlaneWithScale(String cb_item, RadioButton rb, Tab selected_tab, String param, char plane, int layer_number, int index, RecCA_3DPictureCreator prev_pict, RecCAScaleLayout layout, RecCAScale scale, double max_val, double min_val, String type){
        System.out.println("Show Parameter: " + param);
        RecCA_3DPictureCreator pict = new RecCA_3DPictureCreator(trans_data_bank);
        RecCACameraControlLayout camera = new RecCACameraControlLayout(pict);
        layout.setTickLabel(max_val, min_val, type);
        layout.createScaleImage(type);        
        layout.setTitle();
        
        try {
            pict.showPlaneWithScale(task_name, data, param, plane, layer_number, scale, type);
        } catch (IOException ex) {
            System.out.println("Error: showAllWithScale()");
        }
        
        pict.cameraXform.setRotateX(prev_pict.cameraXform.rx.getAngle());
        pict.cameraXform.setRotateY(prev_pict.cameraXform.ry.getAngle());
        pict.cameraXform.setRotateZ(prev_pict.cameraXform.rz.getAngle());
        pict.camera.setTranslateX(prev_pict.camera.getTranslateX());
        pict.camera.setTranslateY(prev_pict.camera.getTranslateY());
        pict.camera.setTranslateZ(prev_pict.camera.getTranslateZ());
        
        SubScene sub_scene = new SubScene(pict.world, scene_width, scene_height, true, SceneAntialiasing.BALANCED);
        sub_scene.setCamera(pict.camera);
        
        sub_scene.setOnMouseClicked(me -> {
            if(me.getClickCount() == 2){
                RecCA_3DPictureParams params = new RecCA_3DPictureParams(pict);
                params.ok.setOnAction(e -> {
                    try {
                        pict.showPlaneWithScale(task_name, data, param, plane, layer_number, scale, type, params);
                    } catch (IOException ex) {
                        System.out.println("Error : showPlaneStructure()");
                    }
                });
            }
        });
        
        this.camera_control.setContent(camera);
        
        handleTabSelection(cb_item, rb, selected_tab, pict, layout, camera, false, plane, layer_number);
        handle3DArea(sub_scene, pict);
        handleShowThis(pict, layout, scale);
        selected_tab.setContent(sub_scene);
    }
    
    private void refreshViewFiles(){           
        
        if(views.getItems().isEmpty())
            views.getItems().addAll(getAllViewFiles());
        
        else{
            views.getItems().clear();
            views.getItems().addAll(getAllViewFiles());
        }
    }
    
    private List<String> getAllViewFiles(){
        
        List<String> list = new ArrayList<>();
        List<String> res_list = new ArrayList<>();
        
        for(File f : new File("./user/task_db/").listFiles()){
            if(f.isDirectory())
                res_list.add(f.getName());
        }
        
        for(String res : res_list){
            for(File f : new File("./user/task_db/" + res + "/").listFiles()){
                if(f.getName().endsWith(".view"))
                    list.add(res + "/" + f.getName());
            }
        }
        System.out.println("ALL VIEW FILES: " + list);
        return list;
    } 
        
    private void createMaterialsTable() throws FileNotFoundException, IOException{
        List<String> list = new ArrayList<>();
        
        String res = results.getSelectionModel().getSelectedItem().toString();
        String seca_file_path = "./task_db/" + res + ".seca";
        
        Properties props = new Properties();
        FileInputStream stream = new FileInputStream(seca_file_path);
        props.load(stream);
        stream.close();
        
        String bound_grain_path = props.getProperty("bound_grains_file_name");
        String tank_file = props.getProperty("tanks_file");
        String spm_file = props.getProperty("phases_file_name");
        
        System.out.println("READ : " + spm_file);
        
        List<String> phases_materials = new ArrayList<>();
        
        for(String item : Files.readAllLines(Paths.get(spm_file))){
            String[] tokens = item.split(" ");
            phases_materials.add(tokens[1]);
        }
        
        System.out.println("PHASES MATERIALS : " + phases_materials);
        boolean is_phases_homogeneus = isHomogeneus(phases_materials);
        System.out.println("PHASES MATERIALS IS HOMOGENEUS: " + is_phases_homogeneus);
        
        System.out.println("READ : " + bound_grain_path);
        
        List<String> bound_materials = new ArrayList<>();
        
        for(String item : Files.readAllLines(Paths.get(bound_grain_path))){
            if(!item.startsWith("#") && !item.equals(null) && !item.equals("")){
                String[] tokens = item.split(" ");
                bound_materials.add(tokens[0]);
            }
        }
        
        System.out.println("BOUNDARY MATERIALS : " + bound_materials);
        boolean is_boundaries_homogeneus = isHomogeneus(bound_materials);
        System.out.println("BOUNDARY MATERIALS IS HOMOGENEUS: " + is_boundaries_homogeneus);
        
        System.out.println("READ : " + tank_file);
        
        List<String> tank_materials = new ArrayList<>();
        
        for(String item : Files.readAllLines(Paths.get(tank_file))){
            if(!item.startsWith("#") && !item.startsWith("tank_geom_type") && !item.equals(null) && !item.equals("")){
                String[] tokens = item.split(" ");
                tank_materials.add(tokens[0]);
            }
        }
        
        System.out.println("TANKS MATERIALS : " + tank_materials);
        boolean is_tanks_homogeneus = isHomogeneus(tank_materials);
        System.out.println("TANK MATERIALS IS HOMOGENEUS: " + is_tanks_homogeneus);
        
        GridPane layout = new GridPane();
        
        Label l1 = new Label("LAYERS MATERIALS");
        Label l2 = new Label("BOUNDARIES MATERIALS");
        Label l3 = new Label("TANKS MATERIALS");
        
        layout.add(l1, 0, 0, 2, 1);
        
        Rectangle rec1 = new Rectangle();
        rec1.setFill(getColorForMaterial(phases_materials.get(0)));
        
        Rectangle rec2 = new Rectangle();
        rec2.setFill(getColorForMaterial(bound_materials.get(0)));
        
        Rectangle rec3 = new Rectangle();
        rec3.setFill(getColorForMaterial(tank_materials.get(0)));
        
        if(is_phases_homogeneus){
            layout.add(new Label(phases_materials.get(0)), 0, 1);
            layout.add(rec1, 1, 1);
        }
        else{
            
        }
        
        layout.add(l2, 0, 2, 2, 1);
        
        if(is_boundaries_homogeneus){
            layout.add(new Label(bound_materials.get(0)), 0, 3);
            layout.add(rec2, 1, 3);
        }
        else{
            
        }
        
        layout.add(l3, 0, 4, 2, 1);
        
        if(is_tanks_homogeneus){
            layout.add(new Label(tank_materials.get(0)), 0, 5);
            layout.add(rec3, 1, 5);
        }
        else{
            
        }
        
        materials.setContent(layout);
    }
    
    private boolean isHomogeneus(List<String> materials){
        
        for(int i = 1 ; i < materials.size() ; i++){
            if(materials.get(i-1) != materials.get(i))
                return false;
        }
        
        return true;
    }
    
    public Color getColorForMaterial(String material){
        Color color = null;
        
        switch(material){
            case "aluminium":
                color = Color.ANTIQUEWHITE;
                break;
            case "aluminium_1":
                color = Color.AQUA;
                break;
            case "aluminium_2":
                color = Color.BLANCHEDALMOND;
                break;
            case "aluminium_3":
                color = Color.BLUE;
                break;
            case "ceramics":
                color = Color.BLUEVIOLET;
                break;
            case "copper":
                color = Color.ORANGE;
                break;
            case "copper_2":
                color = Color.BROWN;
                break;
            case "default":
                color = Color.ALICEBLUE;
                break;
            case "empty":
                color = Color.AZURE;
                break;
            case "iron":
                color = Color.SILVER;
                break;
            case "molibden":
                color = Color.BURLYWOOD;
                break;
            case "rubber":
                color = Color.CADETBLUE;
                break;
            case "steel":
                color = Color.DARKGREY;
                break;
            case "steel_08X18H10T":
                color = Color.CHARTREUSE;
                break;
            case "steel_12X18H09T":
                color = Color.CHOCOLATE;
                break;
            case "steel2":
                color = Color.CORAL;
                break;
            case "steel12GBA":
                color = Color.CRIMSON;
                break;
            case "steel17G1S":
                color = Color.CYAN;
                break;
            case "steel17G1S_213K":
                color = Color.DARKGOLDENROD;
                break;
            case "steel17G1S_233K":
                color = Color.DARKGREEN;
                break;
            case "steel17G1S_253K":
                color = Color.DARKKHAKI;
                break;
            case "steel17G1S_273K":
                color = Color.DARKMAGENTA;
                break;
            case "steel25X1MF":
                color = Color.DARKORANGE;
                break;
            case "steel40X13":
                color = Color.DARKRED;
                break;
            case "steel40X13_2":
                color = Color.DARKSALMON;
                break;
            case "TiAlC":
                color = Color.DARKSEAGREEN;
                break;
            case "TiAlC_a":
                color = Color.DARKSLATEBLUE;
                break;
            case "TiAlN":
                color = Color.DODGERBLUE;
                break;
            case "titanium":
                color = Color.LIGHTSTEELBLUE;
                break;
            case "titanium_iron":                
                color = Color.LIGHTGREY;
                break;
            case "titanium_iron_a":
                color = Color.MAROON;
                break;
            case "zirconium_dioxide":
                color = Color.OLIVEDRAB;
                break;            
        }
        
        return color;
    }
    
    private String getTabTitle(String param){
        String title = null;
        switch(param){
            case "temperature":
                title = "Temperature";
                break;
            case "specific_elastic_energy":
                title = "Spec. Elastic Energy";
                break;
            case "principal_stresses":
                title = "Principal Stress";
                break;
            case "x_component_of_specific_force_moment":
                title = "X Spec. Moment";
                break;
            case "y_component_of_specific_force_moment":
                title = "Y Spec. Moment";
                break;
            case "z_component_of_specific_force_moment":
                title = "Z Spec. Moment";
                break;
            case "current_influx_of_specific_dissipated_energy":
                title = "Influx of Spec. Diss. Energy";
                break;
            case "specific_dissipated_energy":
                title = "Spec. Diss. Energy";
                break;
                
            case "abs_value_of_stress_vector":
                title = "Abs. Stress Vector";
                break;
            case "value_for_crack_generation":
                title = "Value for Crack Generation";
                break;
            case "x_comp_of_stress_vector":
                title = "X Stress Vector";
                break;
            case "y_comp_of_stress_vector":
                title = "Y Stress Vector";
                break;
            case "z_comp_of_stress_vector":
                title = "Z Stress Vector";
                break;
            case "current_influx_of_spec_dissipated_energy":
                title = "Influx of Spec. Diss. Energy";
                break;
                
            case "relative_temperature":
                title = "Temperature";
                break;
            case "relative_specific_elastic_energy":
                title = "Rel. Spec. Elast. Energy";
                break;
            case "total_specific_energy":
                title = "Total Spec. Energy";
                break;
            case "abs_value_of_X_comp_of_inst_spec_force_moment":
                title = "Abs X Inst. Spec. Moment";
                break;
            case "abs_value_of_Y_comp_of_inst_spec_force_moment":
                title = "Abs Y Inst. Spec. Moment";
                break;
            case "abs_value_of_Z_comp_of_inst_spec_force_moment":
                title = "Abs Z Inst. Spec. Moment";
                break;
            case "abs_value_of_inst_spec_force_moment":
                title = "Abs. Inst. Spec. Moment";
                break;
            case "relative_specific_dissipated_energy":
                title = "Rel. Spec. Diss. Energy";
                break;
                
            case "strain":
                title = "Strain";
                break;
            case "total_specific_torsion_energy":
                title = "Total Spec. Torsion Energy";
                break;
            case "x_component_of_inst_spec_force_moment":
                title = "X Inst. Spec. Moment";
                break;
            case "y_component_of_inst_spec_force_moment":
                title = "Y Inst. Spec. Moment";
                break;
            case "z_component_of_inst_spec_force_moment":
                title = "Z Inst. Spec. Moment";
                break;
            case "current_influx_of_torsion_energy":
                title = "Influx of Tors. Energy";
                break;
            case "x_component_of_angle_velocity":
                title = "X Angle Velocity";
                break;
            case "y_component_of_angle_velocity":
                title = "Y Angle Velocity";
                break;
            case "z_component_of_angle_velocity":
                title = "Z Angle Velocity";
                break;
            case "x_component_of_torsion_angle_vector":
                title = "X Tors. Angle Vect.";
                break;
            case "y_component_of_torsion_angle_vector":
                title = "Y Tors. Angle Vect.";
                break;
            case "z_component_of_torsion_angle_vector":
                title = "Z Tors. Angle Vect.";
                break;
            case "relative_power_of_torsion":
                title = "Rel. Power of Tors.";
                break;
            case "relative_torsion_energy":
                title = "Rel. Tors. Energy";
                break;
            
            case "effective_stresses":
                title = "Eff. Stresses";
                break;
        }
        return title;
    }
    
    private void setContextMenu(Tab tab){
        ContextMenu context_menu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename");
        context_menu.getItems().add(rename);
        tab.setContextMenu(context_menu);
        
        rename.setOnAction(e -> showRenameTabWindow(tab));
    }
    
    private void showRenameTabWindow(Tab tab){
        TextField name = new TextField(tab.getText());
        
        Button ok = new Button("OK");
        Button cancel = new Button("Cancel");
        
        ok.setPadding(new Insets(5, 20, 5, 20));
        cancel.setPadding(new Insets(5, 20, 5, 20));
        
        Separator sep = new Separator();
        sep.setPadding(new Insets(10, 2, 10, 2));
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setPadding(new Insets(0, 10, 0, 10));
        
        HBox bottom = new HBox(ok, sep1, cancel);
        bottom.setAlignment(Pos.CENTER);
        
        VBox layout = new VBox(name, sep, bottom);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        
        Stage stage = new Stage();
        stage.setScene(new Scene(layout));
        stage.show();
        
        ok.setOnAction(e -> {
            tab.setText(name.getText());
            stage.close();
        });
        
        cancel.setOnAction(e -> stage.close());
    }
}
