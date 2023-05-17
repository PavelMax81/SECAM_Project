/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import interf.RecCASaveImage;
import interf.TransferredDataBank;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author dmitryb
 */
public class RecCAChart extends Stage{
    
    BorderPane root;
    Scene scene;
    
    /**
     * Tool Bar Components
     */
    ToolBar tool_bar;
    Button save_img, close;
    ComboBox params;
    TitledPane chart_tools;
    
    TabPane tab_pane;
    
    TransferredDataBank trans_data_bank;
    
    String task_name;
    List<String> seca_files = new ArrayList<>();
    
    RecCAChartFiles chart_files;
    
    String seca_file_path;
    
    private String initial_directory = "";
    private String initial_name = "";
    
    public RecCAChart(TransferredDataBank receive_data_bank){
        System.out.println("RecCAChart constructor: start");
        
        this.trans_data_bank = receive_data_bank;
        
        root = new BorderPane();
        
        initTopToolBar();
        root.setTop(top_tool_bar);
        
        tab_pane = new TabPane();
        
        Tab tab = createNewTab();
        tab_pane.getTabs().add(tab);
        
        root.setCenter(tab_pane);
        
        initRightToolBar();
        
        RecCAChartPreferences prefs = new RecCAChartPreferences();
        handleAddLineButton(prefs);
        handleShowButton(tab, prefs);
        right_tool_bar.getItems().add(prefs);
        handleTabSelection(tab, prefs);
        
        root.setRight(right_tool_bar);
        
        double width = Screen.getPrimary().getVisualBounds().getWidth() * 0.9;
        double height = Screen.getPrimary().getVisualBounds().getHeight() * 0.9;
        
        scene = new Scene(root, width, height);
        
        this.setTitle("RecCA - Chart App");
        this.setScene(scene);
        this.show();
    }
    
    private void handleTabSelection(Tab tab, RecCAChartPreferences pref){
        tab.setOnSelectionChanged(e -> {
            right_tool_bar.getItems().remove(1);
            this.right_tool_bar.getItems().add(pref);
            
            handleAddLineButton(pref);
            handleShowButton(tab, pref);
        });
    }
    
    private void handleAddLineButton(RecCAChartPreferences pref){
        add_line.setOnAction(e -> {
            if(!pref.is_line2){
                pref.addSecondCurve();
            }
            else if(!pref.is_line3){
                pref.addThirdCurve();
            }
            else if(!pref.is_line4){
                pref.addFourthCurve();
            }
            else if(!pref.is_line5){
                pref.addFifthCurve();
            }            
        });
    }
    
    private void handleShowButton(Tab tab, RecCAChartPreferences pref){
        show.setOnAction(e -> {
            
            if(!pref.is_line2){
                
                if(pref.line1.results.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Results").showAndWait();
                else if(pref.line1.files.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select File").showAndWait();
                else if(pref.line1.params.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Parameter!").showAndWait();
                
                String param = pref.line1.params.getSelectionModel().getSelectedItem().toString();                
                RecCAChartCreator chart = new RecCAChartCreator(pref.line1.data.time_steps, getArrayList(param, pref.line1.data));
                bindGeneralValues(chart, pref);
                bindAllValues(chart, pref.line1);
                tab.setContent(chart.line_chart);
            }
            
            else if(!pref.is_line3){
                
                if(pref.line1.results.getSelectionModel().isEmpty() || pref.line2.results.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Results").showAndWait();
                else if(pref.line1.files.getSelectionModel().isEmpty() || pref.line2.files.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select File").showAndWait();
                else if(pref.line1.params.getSelectionModel().isEmpty() || pref.line2.params.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Parameter!").showAndWait();
                
                String param = pref.line1.params.getSelectionModel().getSelectedItem().toString();
                String param2 = pref.line2.params.getSelectionModel().getSelectedItem().toString();
                
                RecCAChartCreator chart = new RecCAChartCreator(pref.line1.data.time_steps, 
                                                                pref.line2.data.time_steps, 
                                                                getArrayList(param, pref.line1.data),
                                                                getArrayList(param2, pref.line2.data));
                
                bindGeneralValues(chart, pref);
                bindAllValues(chart, pref.line1, pref.line2);
                tab.setContent(chart.line_chart);
            }
            else if(!pref.is_line4){
                
                if(pref.line1.results.getSelectionModel().isEmpty() || 
                   pref.line2.results.getSelectionModel().isEmpty() ||
                   pref.line3.results.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Results").showAndWait();
                else if(pref.line1.files.getSelectionModel().isEmpty() || 
                        pref.line2.files.getSelectionModel().isEmpty() ||
                        pref.line3.files.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select File").showAndWait();
                else if(pref.line1.params.getSelectionModel().isEmpty() || 
                        pref.line2.params.getSelectionModel().isEmpty() ||
                        pref.line3.params.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Parameter!").showAndWait();
                
                String param = pref.line1.params.getSelectionModel().getSelectedItem().toString();
                String param2 = pref.line2.params.getSelectionModel().getSelectedItem().toString();
                String param3 = pref.line3.params.getSelectionModel().getSelectedItem().toString();
                
                RecCAChartCreator chart = new RecCAChartCreator(pref.line1.data.time_steps, 
                                                                pref.line2.data.time_steps, 
                                                                pref.line3.data.time_steps, 
                                                                getArrayList(param, pref.line1.data),
                                                                getArrayList(param2, pref.line2.data),
                                                                getArrayList(param3, pref.line3.data));
                
                bindGeneralValues(chart, pref);
                bindAllValues(chart, pref.line1, pref.line2, pref.line3);
                tab.setContent(chart.line_chart);
            }
            else if(!pref.is_line5){
                
                if(pref.line1.results.getSelectionModel().isEmpty() || 
                   pref.line2.results.getSelectionModel().isEmpty() ||
                   pref.line3.results.getSelectionModel().isEmpty() ||
                   pref.line4.results.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Results").showAndWait();
                else if(pref.line1.files.getSelectionModel().isEmpty() || 
                        pref.line2.files.getSelectionModel().isEmpty() ||
                        pref.line3.files.getSelectionModel().isEmpty() ||
                        pref.line4.files.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select File").showAndWait();
                else if(pref.line1.params.getSelectionModel().isEmpty() || 
                        pref.line2.params.getSelectionModel().isEmpty() ||
                        pref.line3.params.getSelectionModel().isEmpty() ||
                        pref.line4.params.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Parameter!").showAndWait();
                
                String param = pref.line1.params.getSelectionModel().getSelectedItem().toString();
                String param2 = pref.line2.params.getSelectionModel().getSelectedItem().toString();
                String param3 = pref.line3.params.getSelectionModel().getSelectedItem().toString();
                String param4 = pref.line4.params.getSelectionModel().getSelectedItem().toString();
                
                RecCAChartCreator chart = new RecCAChartCreator(pref.line1.data.time_steps, 
                                                                pref.line2.data.time_steps, 
                                                                pref.line3.data.time_steps, 
                                                                pref.line4.data.time_steps,
                                                                getArrayList(param, pref.line1.data),
                                                                getArrayList(param2, pref.line2.data),
                                                                getArrayList(param3, pref.line3.data),
                                                                getArrayList(param4, pref.line4.data));
                bindGeneralValues(chart, pref);
                bindAllValues(chart, pref.line1, pref.line2, pref.line3, pref.line4);
                tab.setContent(chart.line_chart);
            }
            else if(pref.is_line5){
                
                if(pref.line1.results.getSelectionModel().isEmpty() || 
                   pref.line2.results.getSelectionModel().isEmpty() ||
                   pref.line3.results.getSelectionModel().isEmpty() ||
                   pref.line4.results.getSelectionModel().isEmpty() ||
                   pref.line5.results.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Results").showAndWait();
                else if(pref.line1.files.getSelectionModel().isEmpty() || 
                        pref.line2.files.getSelectionModel().isEmpty() ||
                        pref.line3.files.getSelectionModel().isEmpty() ||
                        pref.line4.files.getSelectionModel().isEmpty() ||
                        pref.line5.files.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select File").showAndWait();
                else if(pref.line1.params.getSelectionModel().isEmpty() || 
                        pref.line2.params.getSelectionModel().isEmpty() ||
                        pref.line3.params.getSelectionModel().isEmpty() ||
                        pref.line4.params.getSelectionModel().isEmpty() ||
                        pref.line5.params.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Select Parameter!").showAndWait();
                
                String param = pref.line1.params.getSelectionModel().getSelectedItem().toString();
                String param2 = pref.line2.params.getSelectionModel().getSelectedItem().toString();
                String param3 = pref.line3.params.getSelectionModel().getSelectedItem().toString();
                String param4 = pref.line4.params.getSelectionModel().getSelectedItem().toString();
                String param5 = pref.line5.params.getSelectionModel().getSelectedItem().toString();
                
                RecCAChartCreator chart = new RecCAChartCreator(pref.line1.data.time_steps, 
                                                                pref.line2.data.time_steps, 
                                                                pref.line3.data.time_steps, 
                                                                pref.line4.data.time_steps,
                                                                pref.line5.data.time_steps,
                                                                getArrayList(param, pref.line1.data),
                                                                getArrayList(param2, pref.line2.data),
                                                                getArrayList(param3, pref.line3.data),
                                                                getArrayList(param4, pref.line4.data),
                                                                getArrayList(param5, pref.line5.data));
                
                bindGeneralValues(chart, pref);
                bindAllValues(chart, pref.line1, pref.line2, pref.line3, pref.line4, pref.line5);
                tab.setContent(chart.line_chart);
            }            
        });        
    }
    
    ToolBar top_tool_bar;
    Button new_tab, create_file, save_as_img, full_screen;
    
    public void initTopToolBar(){
        top_tool_bar = new ToolBar();
        
        new_tab = new Button("New Tab");
        new_tab.setTooltip(new Tooltip("create a new tab for chart"));
        
        create_file = new Button("Create File");
        create_file.setTooltip(new Tooltip("create file with chart data for special area of specimen"));
        
        save_as_img = new Button();
        
        save_as_img.setTooltip(new Tooltip("save chart as png file"));
        
        ImageView save_icon = new ImageView(new Image("./interf/icons/save.png"));
        save_icon.setFitWidth(17.0);
        save_icon.setFitHeight(17.0);
        
        save_as_img.setGraphic(save_icon);
        
        full_screen = new Button();
        full_screen.setTooltip(new Tooltip("show chart in the new window in full screen mode"));
        
        ImageView full_screen_icon = new ImageView(new Image("./interf/icons/full_screen.png"));
        full_screen_icon.setFitWidth(17.0);
        full_screen_icon.setFitHeight(17.0);
        
        full_screen.setGraphic(full_screen_icon);
        
        Separator sep = new Separator(Orientation.VERTICAL);
        sep.setPadding(new Insets(0, 5, 0, 5));
        
        top_tool_bar.getItems().addAll(new_tab, create_file, save_as_img, full_screen);
        
        handleTopToolBar();
    }
    
    private Tab createNewTab(){
        Tab new_tab = new Tab();
        new_tab.setText("New Tab");

        ContextMenu menu = new ContextMenu();

        MenuItem rename = new MenuItem("Rename");
        menu.getItems().add(rename);

        rename.setOnAction(event -> showRenameTabWindow(new_tab));

        new_tab.setContextMenu(menu);
        return new_tab;
    }
    
    public void handleTopToolBar(){
        
        new_tab.setOnAction(e -> {
            
            System.out.println("New Tab is Created");
            
            Tab new_tab = createNewTab();
            
            tab_pane.getTabs().add(new_tab);
            
            RecCAChartPreferences prefs = new RecCAChartPreferences();
            handleTabSelection(new_tab, prefs);
        });        
        
        create_file.setOnAction(e -> {
            RecCACreateChartFile create = new RecCACreateChartFile();            
        });
        
        full_screen.setOnAction(e -> {
            System.out.println("Action Performed : Full Screen");
            ImageView img = new ImageView(tab_pane.getSelectionModel().getSelectedItem().getContent().snapshot(new SnapshotParameters(), null));
            Node content = tab_pane.getSelectionModel().getSelectedItem().getContent();
            RecCAChartFullScreenMode full_screen = new RecCAChartFullScreenMode(content);
        });
        
        save_as_img.setOnAction(e -> {
            System.out.println("Actioin Performed: Save As Image");
            
            RecCASaveImage save_img = new RecCASaveImage(tab_pane.getSelectionModel().getSelectedItem().getContent(), initial_directory, initial_name);
            
            save_img.save.setOnAction(event -> {
            
                System.out.println("RecCASaveImage action performed save button is pushed");

                String dir = save_img.directory.getText();
                String ext = save_img.extension.getSelectionModel().getSelectedItem().toString();

                WritableImage image = save_img.node.snapshot(new SnapshotParameters(), null);

                try{
                    if(save_img.directory.getText().isEmpty()){
                        new Alert(AlertType.ERROR, "Please choose directory for image").showAndWait();
                    }
                    else if(save_img.name.getText().isEmpty()){
                        new Alert(AlertType.ERROR, "Please input the file name").showAndWait();
                    }
                    else{
                        File file = new File(dir + "/" + save_img.name.getText() + "." + ext);
                        try {
                            ImageIO.write(SwingFXUtils.fromFXImage(image, null), ext, file);
                        } catch (IOException ex) {
                            System.out.println("Error: RecCASaveImage save image event");
                        }

                        initial_directory = save_img.directory.getText();
                        initial_name = save_img.name.getText();

                        new Alert(AlertType.INFORMATION, "Image successfully saved!").showAndWait();
                        save_img.close();
                    }
                }
                catch(Exception exc){
                    new Alert(AlertType.ERROR, "Failed to save the image!");
                }
                
            });
        });
    }
    
    public void showRenameTabWindow(Tab tab){
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
    
    ToolBar right_tool_bar;
    Button add_line;
    Button show;
    
    public void initRightToolBar(){
        right_tool_bar = new ToolBar();
        
        add_line = new Button("Add Line");
        
        show = new Button("Show");
        show.setPadding(new Insets(5, 20, 5, 20));
        show.setStyle("-fx-font-weight: bold");
        
        VBox top = new VBox(show,
                            new Separator(),
                            add_line);
        top.setAlignment(Pos.CENTER);
        
        right_tool_bar.setOrientation(Orientation.VERTICAL);
        right_tool_bar.getItems().add(top);
    }
            
    public void bindGeneralValues(RecCAChartCreator chart, RecCAChartPreferences pref){
        
        chart.line_chart.verticalGridLinesVisibleProperty().bindBidirectional(pref.general_prefs.show_grid.selectedProperty());
        chart.line_chart.horizontalGridLinesVisibleProperty().bindBidirectional(pref.general_prefs.show_grid.selectedProperty());
        
        chart.value_axis.labelProperty().bindBidirectional(pref.general_prefs.value_axis_title.textProperty());
        chart.time_axis.labelProperty().bindBidirectional(pref.general_prefs.time_axis_title.textProperty());
        
        chart.line_chart.legendVisibleProperty().bindBidirectional(pref.general_prefs.show_legend.selectedProperty());
        
        pref.general_prefs.time_axis_prefix.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            
            String value_prefix = pref.general_prefs.value_axis_prefix.getSelectionModel().getSelectedItem().toString();
            
            double value_prefix_value = 0;
            double time_prefix_value = 0;
            
            switch(value_prefix){
                case "none":
                    value_prefix_value = 1;
                    break;
                case "n":
                    value_prefix_value = 1e-9;                    
                    break;
                case "mc":
                    value_prefix_value = 1e-6;                    
                    break;
                case "m":
                    value_prefix_value = 1e-3;                    
                    break;
                case "k":
                    value_prefix_value = 1e+3;                    
                    break;
                case "M":
                    value_prefix_value = 1e+6;                    
                    break;
                case "G":
                    value_prefix_value = 1e+9;
                    break;
            }
            
            switch(newValue.toString()){
                case "none":
                    time_prefix_value = 1;
                    break;
                case "n":
                    time_prefix_value = 1e-9;                    
                    break;
                case "mc":
                    time_prefix_value = 1e-6;                    
                    break;
                case "m":
                    time_prefix_value = 1e-3;                    
                    break;
                case "k":
                    time_prefix_value = 1e+3;                    
                    break;
                case "M":
                    time_prefix_value = 1e+6;                    
                    break;
                case "G":
                    time_prefix_value = 1e+9;
                    break;
            }
            
            if(pref.is_line5){
                chart.setPrefixValue(5, value_prefix_value, time_prefix_value);
            }
            else if(pref.is_line4){
                chart.setPrefixValue(4, value_prefix_value, time_prefix_value);
            }
            else if(pref.is_line3){
                chart.setPrefixValue(3, value_prefix_value, time_prefix_value);
            }
            else if(pref.is_line2){
                chart.setPrefixValue(2, value_prefix_value, time_prefix_value);
            }
            else{
                chart.setPrefixValue(1, value_prefix_value, time_prefix_value);       
            }
        });
        
        pref.general_prefs.value_axis_prefix.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            String time_prefix = pref.general_prefs.time_axis_prefix.getSelectionModel().getSelectedItem().toString();
            
            double value_prefix_value = 0;
            double time_prefix_value = 0;
            
            switch(time_prefix){
                case "none":
                    time_prefix_value = 1;
                    break;
                case "n":
                    time_prefix_value = 1e-9;                    
                    break;
                case "mc":
                    time_prefix_value = 1e-6;                    
                    break;
                case "m":
                    time_prefix_value = 1e-3;                    
                    break;
                case "k":
                    time_prefix_value = 1e+3;                    
                    break;
                case "M":
                    time_prefix_value = 1e+6;                    
                    break;
                case "G":
                    time_prefix_value = 1e+9;
                    break;
            }
            
            switch(newValue.toString()){
                case "none":
                    value_prefix_value = 1;
                    break;
                case "n":
                    value_prefix_value = 1e-9;                    
                    break;
                case "mc":
                    value_prefix_value = 1e-6;                    
                    break;
                case "m":
                    value_prefix_value = 1e-3;                    
                    break;
                case "k":
                    value_prefix_value = 1e+3;                    
                    break;
                case "M":
                    value_prefix_value = 1e+6;                    
                    break;
                case "G":
                    value_prefix_value = 1e+9;
                    break;
            }
            
            if(pref.is_line2){
                chart.setPrefixValue(2, value_prefix_value, time_prefix_value);
            }
            else if(pref.is_line3){
                chart.setPrefixValue(3, value_prefix_value, time_prefix_value);
            }
            else if(pref.is_line4){
                chart.setPrefixValue(4, value_prefix_value, time_prefix_value);
            }
            else if(pref.is_line5){
                chart.setPrefixValue(5, value_prefix_value, time_prefix_value);
            }
            else{
                chart.setPrefixValue(1, value_prefix_value, time_prefix_value);       
            }
        });
        
        pref.general_prefs.time_axis_check.selectedProperty().bindBidirectional(chart.time_axis.autoRangingProperty());
        pref.general_prefs.value_axis_check.selectedProperty().bindBidirectional(chart.value_axis.autoRangingProperty());
        
        pref.general_prefs.time_axis_check.setOnAction(e -> {
            if(!pref.general_prefs.time_axis_check.isSelected()){
                pref.general_prefs.time_axis_tick_unit.setText(String.valueOf(chart.time_axis.getTickUnit()));
                pref.general_prefs.time_axis_lower_bound.setText(String.valueOf(chart.time_axis.getLowerBound()));
                pref.general_prefs.time_axis_upper_bound.setText(String.valueOf(chart.time_axis.getUpperBound()));
            }
        });
        
        pref.general_prefs.value_axis_check.setOnAction(e -> {
            if(!pref.general_prefs.value_axis_check.isSelected()){
                pref.general_prefs.value_axis_tick_unit.setText(String.valueOf(chart.value_axis.getTickUnit()));
                pref.general_prefs.value_axis_lower_bound.setText(String.valueOf(chart.value_axis.getLowerBound()));
                pref.general_prefs.value_axis_upper_bound.setText(String.valueOf(chart.value_axis.getUpperBound()));
            }
        });
        
        pref.general_prefs.time_axis_tick_unit.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ENTER){
                double tick_unit = Double.parseDouble(pref.general_prefs.time_axis_tick_unit.getText());
                chart.time_axis.setTickUnit(tick_unit);
            }
        });
        
        pref.general_prefs.time_axis_lower_bound.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ENTER){
                double lower_bound = Double.parseDouble(pref.general_prefs.time_axis_lower_bound.getText());
                chart.time_axis.setLowerBound(lower_bound);
            }
        });
        
        pref.general_prefs.time_axis_upper_bound.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ENTER){
                double upper_bound = Double.parseDouble(pref.general_prefs.time_axis_upper_bound.getText());
                chart.time_axis.setUpperBound(upper_bound);
            }
        });
        
        pref.general_prefs.value_axis_tick_unit.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ENTER){
                double tick_unit = Double.parseDouble(pref.general_prefs.value_axis_tick_unit.getText());
                chart.value_axis.setTickUnit(tick_unit);
            }
        });
        
        pref.general_prefs.value_axis_lower_bound.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ENTER){
                double lower_bound = Double.parseDouble(pref.general_prefs.value_axis_lower_bound.getText());
                chart.value_axis.setLowerBound(lower_bound);
            }
        });
        
        pref.general_prefs.value_axis_upper_bound.setOnKeyPressed(ke -> {
            if(ke.getCode() == KeyCode.ENTER){
                double upper_bound = Double.parseDouble(pref.general_prefs.value_axis_upper_bound.getText());
                chart.value_axis.setUpperBound(upper_bound);
            }
        });
    }
    
    public void bindAllValues(RecCAChartCreator chart, RecCAChartLineLayout line){
        
        line.tools.name.textProperty().bindBidirectional(chart.series1_name);
        
        line.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 1);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line1_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line1_color.css");
        });
        
    }
    
    public void bindAllValues(RecCAChartCreator chart, 
                              RecCAChartLineLayout line, 
                              RecCAChartLineLayout line2)
    {
        line.tools.name.textProperty().bindBidirectional(chart.series1_name);
        line2.tools.name.textProperty().bindBidirectional(chart.series2_name);
        
        line.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 1);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line1_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line1_color.css");
        });
        
        line2.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line2.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 2);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line2_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line2_color.css");
        });
    }
    
    public void bindAllValues(RecCAChartCreator chart, 
                              RecCAChartLineLayout line, 
                              RecCAChartLineLayout line2,
                              RecCAChartLineLayout line3)
    {
        line.tools.name.textProperty().bindBidirectional(chart.series1_name);
        line2.tools.name.textProperty().bindBidirectional(chart.series2_name);
        line3.tools.name.textProperty().bindBidirectional(chart.series3_name);
        
        line.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 1);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line1_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line1_color.css");
        });
        
        line2.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line2.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 2);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line2_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line2_color.css");
        });
        
        line3.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line3.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 3);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line3_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line3_color.css");
        });
    }
    
    public void bindAllValues(RecCAChartCreator chart, 
                              RecCAChartLineLayout line, 
                              RecCAChartLineLayout line2,
                              RecCAChartLineLayout line3,
                              RecCAChartLineLayout line4)
    {
        line.tools.name.textProperty().bindBidirectional(chart.series1_name);
        line2.tools.name.textProperty().bindBidirectional(chart.series2_name);
        line3.tools.name.textProperty().bindBidirectional(chart.series3_name);
        line4.tools.name.textProperty().bindBidirectional(chart.series4_name);
        
        line.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 1);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line1_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line1_color.css");
        });
        
        line2.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line2.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 2);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line2_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line2_color.css");
        });
        
        line3.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line3.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 3);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line3_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line3_color.css");
        });
        
        line4.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line4.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 4);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line4_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line4_color.css");
        });
    }
    
    public void bindAllValues(RecCAChartCreator chart, 
                              RecCAChartLineLayout line, 
                              RecCAChartLineLayout line2,
                              RecCAChartLineLayout line3,
                              RecCAChartLineLayout line4,
                              RecCAChartLineLayout line5)
    {
        line.tools.name.textProperty().bindBidirectional(chart.series1_name);
        line2.tools.name.textProperty().bindBidirectional(chart.series2_name);
        line3.tools.name.textProperty().bindBidirectional(chart.series3_name);
        line4.tools.name.textProperty().bindBidirectional(chart.series4_name);
        line5.tools.name.textProperty().bindBidirectional(chart.series5_name);
        
        line.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 1);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line1_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line1_color.css");
        });
        
        line2.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line2.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 2);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line2_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line2_color.css");
        });
        
        line3.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line3.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 3);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line3_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line3_color.css");
        });
        
        line4.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line4.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 4);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line4_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line4_color.css");
        });
        
        line5.tools.color_picker.setOnAction(e -> {
            String hex = "#" + Integer.toHexString(line5.tools.color_picker.getValue().hashCode());
            try {
                line.writeNewColorToCss(hex, 5);
            } catch (IOException ex) {
                System.out.println("Can't create new css file");
            }
            chart.line_chart.getStylesheets().remove("./interf/charts/css/line5_color.css");
            chart.line_chart.getStylesheets().add("./interf/charts/css/line5_color.css");
        });
    }
    
    public List<Double> getArrayList(String param, RecCAChartFiles data){
        List<Double> array = null;
        switch(param){
            
            case "Temperature":
                array = data.temperature;
                break;
            case "Specific Elastic Energy":
                array = data.specific_elastic_energy;
                break;
            case "Specific Dissipated Energy":
                array = data.specific_dissipated_energy;
                break;
            case "Abs. Value of X Inst. Spec. Moment":
                array = data.abs_value_of_X_comp_of_inst_spec_force_moment;
                break;
            case "Abs. Value of Y Inst. Spec. Moment":
                array = data.abs_value_of_Y_comp_of_inst_spec_force_moment;
                break;
            case "Abs. Value of Z Inst. Spec. Moment":
                array = data.abs_value_of_Z_comp_of_inst_spec_force_moment;
                break;
            case "Abs. Value of Inst. Moment":
                array = data.abs_value_of_inst_spec_force_moment;
                break;
            case "Total Specific Energy":
                array = data.total_specific_energy;
                break;
            case "Abs. Value of Stress Vector":
                array = data.abs_value_of_stress_vector;
                break;
            case "Value for Crack Generation":
                array = data.value_for_crack_generation;
                break;
            case "X Comp. of Stress Vector":
                array = data.x_comp_of_stress_vector;
                break;
            case "Y Comp. of Stress Vector":
                array = data.y_comp_of_stress_vector;
                break;
            case "Z Comp. of Stress Vector":
                array = data.z_comp_of_stress_vector;
                break;
            case "Current Influx Of Torsion Energy":
                array = data.current_influx_of_specific_dissipated_energy;
                break;
            
            case "Abs. Angle Velocity of Torsion":
                array = data.abs_value_of_angle_velocity;
                break;
            case "Abs. Torsion Angle":
                array = data.abs_value_of_torsion_angle;
                break;
                
            case "Principal Stresses":
                array = data.principal_stresses;
                break;
            case "X Comp. of Spec. Force Moment":
                array = data.x_component_of_specific_force_moment;
                break;
            case "Y Comp. of Spec. Force Moment":
                array = data.y_component_of_specific_force_moment;
                break;
            case "Z Comp. of Spec. Force Moment":
                array = data.z_component_of_specific_force_moment;
                break;
            case "X Comp. of Inst. Spec. Moment":
                array = data.x_component_of_inst_spec_force_moment;
                break;
            case "Y Comp. of Inst. Spec. Moment":
                array = data.y_component_of_inst_spec_force_moment;
                break;
            case "Z Comp. of Inst. Spec. Moment":
                array = data.z_component_of_inst_spec_force_moment;
                break;
            case "X Comp. of Angle Velocity":
                array = data.x_component_of_angle_velocity;
                break;
            case "Y Comp. of Angle Velocity":
                array = data.y_component_of_angle_velocity;
                break;
            case "Z Comp. of Angle Velocity":
                array = data.z_component_of_angle_velocity;
                break;
            case "X Comp. of Torsion Angle Vector":
                array = data.x_component_of_torsion_angle_vector;
                break;
            case "Y Comp. of Torsion Angle Vector":
                array = data.y_component_of_torsion_angle_vector;
                break;
            case "Z Comp. of Torsion Angle Vector":
                array = data.z_component_of_torsion_angle_vector;
                break;
            case "Relative Power Of Torsion":
                array = data.relative_power_of_torsion;
                break;
            case "Relative Torsion Energy":
                array = data.relative_torsion_energy;
                break;
            case "Effective Stresses":
                array = data.effective_stresses;
                break;
            case "Strain":
                array = data.strain;
                break;
            case "Total Spec.Torsion Energy":
                array = data.total_specific_torsion_energy;
                break;            
        }
        
        return array;
    }
    
}
