/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.help;

import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 *
 * @author Ear
 */
public class RecCAHelp extends Stage{
    
    Scene scene;
    
    WebView web_view;
    WebEngine engine;
    
    BorderPane root;
    ToolBar tool_bar;
    TreeView tree;
    TreeItem specimen, conditions, initial_cond, bound_cond, tank_cond, task, results, run_program, charts, computer;
    
    public RecCAHelp(){
        
        tree = new TreeView();
        
        TreeItem tree_root = new TreeItem();
        
        specimen = new TreeItem("Specimen");
        initial_cond = new TreeItem("Initial Conditions");
        bound_cond = new TreeItem("Boundary Conditions");
        tank_cond = new TreeItem("Tanks");
        task = new TreeItem("Task");
        results = new TreeItem("Results");
        run_program = new TreeItem("Run Program");
        charts = new TreeItem("Charts");
        computer = new TreeItem("Installation");
        conditions = new TreeItem("Conditions");
        conditions.getChildren().addAll(initial_cond, bound_cond, tank_cond);
        
        tree_root.getChildren().addAll(
                specimen, conditions, task, results, run_program, charts, computer
        );
        
        tree.setRoot(tree_root);
        tree.setShowRoot(false);
        tree.setPadding(new Insets(5, 5, 5, 5));
        
        tool_bar = new ToolBar();
        tool_bar.setOrientation(Orientation.VERTICAL);
        tool_bar.getItems().add(tree);
        
        web_view = new WebView();
        
        engine = web_view.getEngine();
        
        URL url = getClass().getResource("help.html");
        
        engine.load(url.toExternalForm());
        
        root = new BorderPane();
        root.setCenter(web_view);
        root.setLeft(tool_bar);
        
        scene = new Scene(root);
        
        this.setTitle("RecCA - Help");
        this.setScene(scene);
        this.show();
        
        handleEvents();
    }
    
    public void handleEvents(){
        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>(){
            @Override
            public void changed(ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) {
                URL url;
                /*specimen, initial_cond, bound_cond, tank_cond, task, results, run_program, charts;*/
                if(newValue.equals(specimen)){
                    url = getClass().getResource("specimen.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(conditions)){
                    url = getClass().getResource("conditions.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(initial_cond)){
                    url = getClass().getResource("init_cond.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(bound_cond)){
                    url = getClass().getResource("bound_cond.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(tank_cond)){
                    url = getClass().getResource("tanks.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(task)){
                    url = getClass().getResource("task.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(results)){
                    url = getClass().getResource("results.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(run_program)){
                    url = getClass().getResource("run_program.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(charts)){
                    url = getClass().getResource("charts.html");
                    engine.load(url.toExternalForm());
                }
                else if(newValue.equals(computer)){
                    url = getClass().getResource("installation.html");
                    engine.load(url.toExternalForm());
                }
            }
        });
    }
    
}
