/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author dmitryb
 */
public class RecCASaveImage extends Stage{
    
    public ComboBox extension;
    
    public Button save, close, choose_dir;
    
    public TextField name, directory;
    
    Scene scene;
    BorderPane root;
    
    public Node node;
    
    public ImageView image;
    
    public RecCASaveImage(Node node, String init_dir, String init_name){
        System.out.println("RecCASaveImage constructor: start");
        
        this.node = node;
        
        root = new BorderPane();
        
        image = new ImageView(node.snapshot(new SnapshotParameters(), null));
        image.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.4);
        image.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth()* 0.4);
        
        root.setCenter(image);
        
        scene = new Scene(root);
        
        this.initAllElements();
        this.addAllElements();
        this.handleEvents();
        
        this.directory.setText(init_dir);
        this.name.setText(init_name);
        
        this.setTitle("Save Image");
        this.setScene(scene);
        this.show();
    }
    
    public void initAllElements(){
        System.out.println("RecCASaveImage initAllElements() method: start");
        
        extension = new ComboBox();
        extension.getItems().add("png");
        extension.getSelectionModel().selectFirst();
        
        save = new Button("Save");
        close = new Button("Close");
        choose_dir = new Button("Choose Directory");
        
        name = new TextField();
        directory = new TextField();
        directory.setText(System.getProperty("user.dir"));        
        
    }
    
    public void addAllElements(){
        System.out.println("RecCASaveImage addAllElements() method: start");
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 5, 5, 5));
        
        Label l1 = new Label("File Name:");
        Label l2 = new Label("File Directory:");
        
        GridPane center_layout = new GridPane();
        center_layout.setHgap(7.0d);
        center_layout.setVgap(7.0d);
        center_layout.setAlignment(Pos.CENTER);
        center_layout.setPadding(new Insets(5, 5, 5, 5));
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(name, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(extension, 2, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(l2, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(directory, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(choose_dir, 2, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        
        center_layout.getChildren().addAll(
                l1, l2, name, directory,
                extension, choose_dir
        );
        
        HBox bottom_layout = new HBox();
        bottom_layout.setAlignment(Pos.CENTER);
        bottom_layout.setPadding(new Insets(5, 5, 5, 5));
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setPadding(new Insets(0, 5, 0, 5));
        
        bottom_layout.getChildren().addAll(
                save, sep1, close
        );
        
        root.setBottom(new VBox(center_layout, sep2, bottom_layout));
    }
    
    public void handleEvents(){
        System.out.println("RecCASaveImage handleEvents() method: start");
        
        close.setOnAction(e -> this.close());
        
        
        
        choose_dir.setOnAction(e -> {
            System.out.println("RecCASaveImage action performed choose directory button is pushed");
            DirectoryChooser dir = new DirectoryChooser();
            File file = dir.showDialog(this);
            this.directory.setText(file.getAbsolutePath());
        });
        
    }
    
}
