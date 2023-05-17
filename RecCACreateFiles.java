/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author DmitryB
 */
public class RecCACreateFiles extends Stage{
    
    Scene scene;
    BorderPane root;
    ScrollPane scroll_pane;
    
    List<String> files;
    
    ListView list_view;
    
    Button next, back;
    GridPane bottom_layout;
    
    Label top_label;
    
    public RecCACreateFiles(List<String> selected_files){
        
        files = selected_files;
        
        root = new BorderPane();
        
        top_label = new Label("SELECTED RESULTS");
        top_label.setPadding(new Insets(10, 10, 10, 10));
        top_label.setAlignment(Pos.TOP_CENTER);
        top_label.setStyle("-fx-font-weight: bold");
        
        VBox top_layout = new VBox(top_label);
        top_layout.setAlignment(Pos.TOP_CENTER);
        top_layout.setPadding(new Insets(10, 10, 10, 10));
        
        root.setTop(top_layout);
        
        scroll_pane = new ScrollPane();
        scroll_pane.setPadding(new Insets(20, 20, 20, 20));
        root.setCenter(scroll_pane);
        
        initAllElements();
        
        root.setBottom(bottom_layout);
        
        scene = new Scene(root);
        
        setScene(scene);
        setTitle("Create Files (For Surfer)");
        show();
    }
    
    private void initAllElements(){
        
        list_view = new ListView();
        list_view.getItems().addAll(files);
        list_view.setMinWidth(500.0);
        
        initContextMenu();
        
        list_view.setContextMenu(context_menu);
        
        GridPane center_layout = new GridPane();
        center_layout.setHgap(5.0);
        center_layout.setVgap(5.0);
        center_layout.setAlignment(Pos.CENTER);
        
        center_layout.add(list_view, 0, 0);
        
        scroll_pane.setContent(center_layout);
        
        bottom_layout = new GridPane();
        bottom_layout.setPadding(new Insets(5, 20, 20, 20));
        bottom_layout.setAlignment(Pos.BOTTOM_CENTER);
        bottom_layout.setHgap(20.0);
        
        next = new Button("Next");
        next.setPadding(new Insets(5, 20, 5, 20));
        
        back = new Button("Back");
        back.setPadding(new Insets(5, 20, 5, 20));
        
        bottom_layout.add(back, 0, 0);
        bottom_layout.add(next, 1, 0);
    }
    
    ContextMenu context_menu;
    MenuItem add_item, delete_item;
    
    private void initContextMenu(){
        context_menu = new ContextMenu();
        
        add_item = new MenuItem("Add File");
        delete_item = new MenuItem("Delete");
        
        context_menu.getItems().addAll(add_item, delete_item);
        
        delete_item.setOnAction(e -> list_view.getItems().remove(list_view.getSelectionModel().getSelectedItem()));
        
        add_item.setOnAction(e -> {
            System.out.println("Action Performed: add file");
        });
    }
    
    boolean is_relValues, is_jocl_stresses, is_jocl, is_inst_mom, is_torsion, is_res;
    
    private void processFiles(){        
        
        List<String> list_of_files = list_view.getItems();
        
        list_of_files.forEach((item) -> {
            if(item.endsWith("relValues.res"))
                is_relValues = true;
            else if(item.endsWith("jocl_stresses.res"))
                is_jocl_stresses = true;
            else if(item.endsWith("jocl.res"))
                is_jocl = true;
            else if(item.endsWith("inst_mom.res"))
                is_inst_mom = true;
            else if(item.endsWith("torsion.res"))
                is_torsion = true;
            else if(item.endsWith(".res"))
                is_res = true;
        });
    }
    
    private void showSecondLayout(){
        
    }
    
}
