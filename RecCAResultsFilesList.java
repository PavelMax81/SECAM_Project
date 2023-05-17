/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.File;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCAResultsFilesList extends Stage{
    
    Scene scene;
    VBox root;
    
    public ListView list;
    ListView results_list;
    
    /**
     * This field is intended for remembering in memory directory of files and their extension
     */

    TransferredDataBank trans_data_bank;
    
    public RecCAResultsFilesList(TransferredDataBank receive_data_bank){
        
        trans_data_bank = receive_data_bank;
        
        /*
         * Remembering directory of files and their extension in memory
         */
        
        double width = Screen.getPrimary().getVisualBounds().getWidth();
        double height = Screen.getPrimary().getVisualBounds().getHeight();
        
        list = new ListView();
        list.getItems().clear();
        list.getItems().addAll(loadFiles());
        list.setMinWidth(width * 0.3);
        list.setMinHeight(height * 0.5);
        
        results_list = new ListView();
        results_list.setMinWidth(0);
        results_list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        results_list.setMinWidth(width * 0.5);
        results_list.setMinHeight(height * 0.5);
        
        Separator sep = new Separator(Orientation.VERTICAL);        
        sep.setPadding(new Insets(0, 10, 0, 10));
                
        HBox layout = new HBox(list, sep, results_list);        
        layout.setAlignment(Pos.CENTER);
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(10, 0, 10, 0));
        
        root = new VBox();
        root.setPadding(new Insets(20,20,20,20));
        scene = new Scene(root);
        
        root.getChildren().addAll(
                layout,
                sep1,
                initButtons()
        );
        
        selectionEvent();
        
        root.setAlignment(Pos.CENTER);
        
        this.setScene(scene);
        this.show();
    }
    
    public List<String> loadFiles(){
        
        List<String> list = FXCollections.observableArrayList();
        
        String folder;
        File[] files;        
        
        String dir = Common.TASK_PATH;
        files = new File(dir).listFiles();
        for(File file : files){
            if(file.isDirectory()){
                list.add(file.getName());
            }
        }
        this.setTitle("Select Results");
        
        return list;
    }
    
    public Button ok, cancel;
        
    public HBox initButtons(){
        ok = new Button("OK");
        ok.setPadding(new Insets(5, 15, 5, 15));
        
        cancel = new Button("Cancel");
        cancel.setPadding(new Insets(5, 15, 5, 15));
        
        cancel.setOnAction(e -> this.close());
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setPadding(new Insets(0, 5, 0, 5));
        
        HBox layout = new HBox(ok, sep1, cancel);        
        
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
    
    public void selectionEvent(){
        
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
            
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
                if(!results_list.getItems().isEmpty())
                    results_list.getItems().clear();
                
                File[] files;

                files = new File(Common.TASK_PATH + "/" + newValue).listFiles();

                for(File file : files){
                    if(file.isFile()){
                        results_list.getItems().add(file.getName());
                    }
                }                
            }
        });
        
    }
    
    public void refreshList(){
        list.getItems().clear();
        list.getItems().addAll(loadFiles());
        list.refresh();
    }
    
    
    public void deleteFolder(String path){
        System.out.println("Deleting folder: " + path);
        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File f : files){
            if(f.isDirectory()){
                deleteFolder(f.getAbsolutePath());
                f.delete();
            }
            else if(f.isFile()){
                f.delete();
            }
        }
        folder.delete();
    }
    
    public void deleteFile(String path){
        System.out.println("Deleting file: " + path);
        File file = new File(path);
        file.delete();
    }
        
}
