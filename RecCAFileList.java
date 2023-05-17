/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCAFileList extends Stage{
    
    Scene scene;
    VBox root;
    
    ListView list;
    
    TextArea parameters;
    
    /**
     * This field is intended for remembering in memory directory of files and their extension
     */

    String directory,extension;
    
    TransferredDataBank trans_data_bank;
    
    public RecCAFileList(TransferredDataBank receive_data_bank, String directory_of_files, String extension_of_files){
        
        trans_data_bank = receive_data_bank;
        
        /*
         * Remembering directory of files and their extension in memory
         */
        
        directory = new String(directory_of_files);
        extension = new String(extension_of_files);
        
        list = new ListView();
        list.getItems().clear();
        list.getItems().addAll(loadFiles());
        
        parameters = new TextArea();
        
        Separator sep = new Separator(Orientation.VERTICAL);        
        sep.setPadding(new Insets(0, 10, 0, 10));
        Separator sep2 = new Separator(Orientation.VERTICAL);        
        sep2.setPadding(new Insets(0, 10, 0, 10));
        
        refresh = new Button();
        ImageView img = new ImageView(new Image("./interf/icons/refresh.png"));
        img.setFitHeight(12.0);
        img.setFitWidth(12.0);
        refresh.setGraphic(img);
        refresh.setPadding(new Insets(13,13,13,13));
        
        refresh.setOnAction(e -> {
            System.out.println("Action Performed: refresh");
            this.refreshList();
        });
        
        HBox layout = new HBox(list, sep, refresh, sep2,parameters);        
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
        initContextMenu();
        selectionEvent();
        root.setAlignment(Pos.CENTER);
        
        this.setScene(scene);
        this.show();
    }
    
    public List<String> loadFiles(){
        
        List<String> list = FXCollections.observableArrayList();
        
        String folder;
        File[] files;
        
        if(directory.equals(Common.SPEC_PATH)){
            files = new File(directory).listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    list.add(file.getName());
                }
            }
            this.setTitle("Select Specimen");
        }
        
        else if(directory.equals(Common.MATERIALS_PATH)){
            files = new File(directory).listFiles();
            for(File file : files){
                if(file.isFile()){
                    list.add(file.getName().replaceAll("." + Common.MATERIAL_EXTENSION, ""));
                }
            }
            this.setTitle("Select Material");
        }        
        else if(directory.equals("init_cond")){
            String spec_name = trans_data_bank.getUIInterface().getSpecimenPath();
            String dir = Common.SPEC_PATH + "/" + spec_name + Common.INIT_COND_NAME;
            files = new File(dir).listFiles();
            for(File file : files){
                if(file.isFile() && file.getName().endsWith(".txt") && !file.getName().contains("init_cond_rec")){
                    list.add(file.getName().replace(".txt", ""));
                }
            }
            this.setTitle("Select Initial Conditions");
        }
        else if(directory.equals("bound_cond")){
            String spec_name = trans_data_bank.getUIInterface().getSpecimenPath();
            String dir = Common.SPEC_PATH + "/" + spec_name + Common.BOUND_COND_NAME;
            files = new File(dir).listFiles();
            for(File file : files){
                if(file.isFile() && 
                   file.getName().endsWith(".txt") && 
                   !file.getName().endsWith("tanks.txt") &&
                   !file.getName().endsWith("_grains.txt")&&
                   !file.getName().endsWith(".bon")&&
                   !file.getName().endsWith("_bound_interaction_type.txt") &&
                   !file.getName().endsWith("bound_geom.txt"))
                {
                    list.add(file.getName().replace(".txt", ""));
                }
            }
            this.setTitle("Select Boundary Conditions");
        }
        else if(directory.equals("tank")){
            String spec_name = trans_data_bank.getUIInterface().getSpecimenPath();
            String dir = Common.SPEC_PATH + "/" + spec_name + Common.BOUND_COND_NAME;
            files = new File(dir).listFiles();
            for(File file : files){
                if(file.isFile() && file.getName().endsWith("_tanks.txt")){
                    list.add(file.getName().replace("_tanks.txt", ""));
                }
            }
            this.setTitle("Select Tanks");
        }
        else if(directory.equals("task")){
            String dir = Common.TASK_PATH;
            files = new File(dir).listFiles();
            for(File file : files){
                if(file.isFile() && file.getName().endsWith(".seca")){
                    list.add(file.getName().replace(".seca", ""));
                }
            }
            this.setTitle("Select Task");
        }
        
        else if(directory.equals("results")){
            String dir = Common.TASK_PATH;
            files = new File(dir).listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    list.add(file.getName());
                }
            }
            this.setTitle("Select Results");
        }
        
        return list;
    }
    
    Button ok, change, delete;
    Button create_new, refresh;
    
    public HBox initButtons(){
        ok = new Button("OK");
        change = new Button("Change");
        delete = new Button("Delete");
        
        ok.setPadding(new Insets(5, 15, 5, 15));
        change.setPadding(new Insets(5, 15, 5, 15));
        delete.setPadding(new Insets(5, 15, 5, 15));
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setPadding(new Insets(0, 5, 0, 5));
        Separator sep2 = new Separator(Orientation.VERTICAL);
        sep2.setPadding(new Insets(0, 5, 0, 5));
        
        HBox layout = new HBox(ok, sep1, change, sep2, delete);        
        
        create_new = new Button("Create New");
        create_new.setPadding(new Insets(5, 15, 5, 15));
        Separator sep3 = new Separator(Orientation.VERTICAL);
        sep3.setPadding(new Insets(0, 5, 0, 5));
        layout.getChildren().addAll(sep3, create_new);
        
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
    
    String info;
    
    public void selectionEvent(){
        
        if(directory.equals(Common.MATERIALS_PATH)){
            list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    info = "Material Paremeters:\n\n";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(new File(Common.MATERIALS_PATH+"/" + newValue + "." + Common.MATERIAL_EXTENSION)));
                        String line;
                        while( (line = br.readLine()) != null){
                            info += line + "\n";
                        }                        
                    } catch (FileNotFoundException ex) {
                        System.out.println("Error: can't find file " + newValue);
                    } catch (IOException ex) {
                        System.out.println("IOException: " + ex);
                    }
                parameters.setText(info);                    
                }                         
            });
        }
        
        else if(directory.equals(Common.SPEC_PATH)){
            list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    info = "Specimen Parameters: \n\n";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(new File(Common.SPEC_PATH+"/" + newValue + "/" + newValue + ".txt")));
                        String line;
                        while( (line = br.readLine()) != null){
                            info += line + "\n";
                        }                        
                    } catch (FileNotFoundException ex) {
                        System.out.println("Error: can't find file " + newValue);
                    } catch (IOException ex) {
                        System.out.println("IOException: " + ex);
                    }
                parameters.setText(info);                    
                }                         
            });            
        }
        
        else if(directory.equals("init_cond")){
            list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    info = "Initial Conditions Parameters: \n\n";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(new File(Common.SPEC_PATH + "/" + trans_data_bank.getUIInterface().getSpecimenPath() + Common.INIT_COND_NAME +"/" + newValue + ".txt")));
                        String line;
                        while( (line = br.readLine()) != null){
                            info += line + "\n";
                        }                        
                    } catch (FileNotFoundException ex) {
                        System.out.println("Error: can't find file " + newValue);
                    } catch (IOException ex) {
                        System.out.println("IOException: " + ex);
                    }
                parameters.setText(info);                    
                }                         
            });
        }
        
        else if(directory.equals("bound_cond")){
            list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    info = "Boundary Conditions Parameters: \n\n";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(new File(Common.SPEC_PATH + "/" + trans_data_bank.getUIInterface().getSpecimenPath() + Common.BOUND_COND_NAME + "/" + newValue + ".txt")));
                        String line;
                        while( (line = br.readLine()) != null){
                            info += line + "\n";
                        }                        
                    } catch (FileNotFoundException ex) {
                        System.out.println("Error: can't find file " + newValue);
                    } catch (IOException ex) {
                        System.out.println("IOException: " + ex);
                    }
                parameters.setText(info);                    
                }                         
            });
        }
        
        else if(directory.equals("tank")){
            list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    info = "Tank Parameters: \n\n";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(new File(Common.SPEC_PATH + "/" + trans_data_bank.getUIInterface().getSpecimenPath() + Common.BOUND_COND_NAME + "/" + newValue + "_tanks.txt")));
                        String line;
                        while( (line = br.readLine()) != null){
                            info += line + "\n";
                        }                        
                    } catch (FileNotFoundException ex) {
                        System.out.println("Error: can't find file " + newValue);
                    } catch (IOException ex) {
                        System.out.println("IOException: " + ex);
                    }
                parameters.setText(info);                    
                }                         
            });
        }
        
        else if(directory.equals("task")){
            list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    info = "Task Parameters: \n\n";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(new File(Common.TASK_PATH + "/" + newValue + ".seca")));
                        String line;
                        while( (line = br.readLine()) != null){
                            info += line + "\n";
                        }                        
                    } catch (FileNotFoundException ex) {
                        System.out.println("Error: can't find file " + newValue);
                    } catch (IOException ex) {
                        System.out.println("IOException: " + ex);
                    }
                parameters.setText(info);                    
                }                         
            });
        }
        
        else if(directory.equals("results")){
            list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    info = "Results Files:\n\n";
                    
                    File[] files;
                    
                    if(trans_data_bank.isWindows){
                         files = new File(System.getProperty("user.dir") + "\\user\\task_db\\" + newValue).listFiles();
                    }
                    else{
                         files = new File(Common.TASK_PATH + "/" + newValue).listFiles();
                    }
                    
                    if(files.length == 0)
                        info += "This Folder is Empty";
                    
                    for(File file : files){
                        if(file.isFile()){
                            info += file.getName() + "\n";
                        }
                    }
                    parameters.setText(info);
                }
            });
        }
    }
    
    public void refreshList(){
        list.getItems().clear();
        list.getItems().addAll(loadFiles());
        list.refresh();
    }
    
    ContextMenu context_menu;
    MenuItem change_menu_item, delete_menu_item, select_menu_item, open_containing_folder, refresh_list, cancel;
    
    public void initContextMenu(){
        System.out.println("RecCAFileList method initContextMenu(): start");
        
        context_menu = new ContextMenu();
        
        open_containing_folder = new MenuItem("Open containing folder ...");
        
        change_menu_item = new MenuItem("Change");
        delete_menu_item = new MenuItem("Delete");
        select_menu_item = new MenuItem("Select");
        refresh_list = new MenuItem("Refresh List");
        refresh_list.setAccelerator(KeyCombination.keyCombination("F5"));
        
        refresh_list.setOnAction(e -> {
            System.out.println("Action Performed: refresh list");
            this.refreshList();
        });
        
        cancel = new MenuItem("Cancel");
        
        context_menu.getItems().addAll(open_containing_folder, new SeparatorMenuItem(), refresh_list ,new SeparatorMenuItem(), select_menu_item, change_menu_item, delete_menu_item, new SeparatorMenuItem(), cancel);
        
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
