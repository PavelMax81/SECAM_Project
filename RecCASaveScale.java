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
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Ear
 */
public class RecCASaveScale extends Stage{
    
    ToolBar tool_bar, bottom_tool_bar;
    
    TitledPane tick_marks;
    TextField first_value, second_value, third_value, fourth_value, fifth_value,
                  file_name_txt_fld, directory;
    ComboBox file_extension;
    
    Button close, save, choose_dir;
    
    Scene scene;
    BorderPane root;
    
    RecCAScaleLayout scale;
    
    /**
     * Scale Layout Elements
     */
    GridPane layout;
    Label tick_lbl1, tick_lbl2, tick_lbl3, tick_lbl4, tick_lbl5, title_lbl;
    ImageView scale_image, title_image;
    String measure, prefix;
    
    public RecCASaveScale(RecCAScaleLayout scale_layout){
        System.out.println("RecCASaveScale constructor: start");
        
        this.scale = scale_layout;
        
        root = new BorderPane();
        scene = new Scene(root);
        
        initAllElements();
        addAllElements();
        handleEvents();
        
        this.setTitle("Save Scale");
        this.setScene(scene);
        this.show();
    }
    
    public void initAllElements(){
        System.out.println("RecCASaveScale method initAllElements(): start");
        
        layout = new GridPane();
        
        this.tick_lbl1 = new Label();
        this.tick_lbl2 = new Label();
        this.tick_lbl3 = new Label();
        this.tick_lbl4 = new Label();
        this.tick_lbl5 = new Label();
        
        tick_lbl1.setText(scale.tick_lbl1.getText());
        tick_lbl2.setText(scale.tick_lbl2.getText());
        tick_lbl3.setText(scale.tick_lbl3.getText());
        tick_lbl4.setText(scale.tick_lbl4.getText());
        tick_lbl5.setText(scale.tick_lbl5.getText());
        
        this.scale_image = new ImageView();
        scale_image.setImage(scale.scale_image.getImage());
        
        this.title_image = scale.getTitleImage();
        
        this.measure = scale.measure;
        this.prefix = scale.prefix;
        
        this.title_lbl = new Label();
        
        tool_bar = new ToolBar();
        tool_bar.setOrientation(Orientation.VERTICAL);
        bottom_tool_bar = new ToolBar();
        
        close = new Button("Close");
        close.setPadding(new Insets(5, 30, 5, 30));
        
        save = new Button("Save");
        save.setPadding(new Insets(5, 30, 5, 30));
        
        file_name_txt_fld = new TextField();
        file_name_txt_fld.setPromptText("Type File Name");
        
        directory = new TextField();
        directory.setPromptText("File Directory");
        
        choose_dir = new Button("...");
        
        file_extension = new ComboBox();
        file_extension.getItems().addAll("png", "jpg");
        file_extension.getSelectionModel().selectFirst();
        
        first_value = new TextField();
        first_value.setText(scale.tick_lbl5.getText());
        first_value.setMaxWidth(100.0d);
        second_value = new TextField();
        second_value.setText(scale.tick_lbl4.getText());
        second_value.setMaxWidth(100.0d);
        third_value = new TextField();
        third_value.setText(scale.tick_lbl3.getText());
        third_value.setMaxWidth(100.0d);
        fourth_value = new TextField();
        fourth_value.setText(scale.tick_lbl2.getText());
        fourth_value.setMaxWidth(100.0d);
        fifth_value = new TextField();
        fifth_value.setText(scale.tick_lbl1.getText());
        fifth_value.setMaxWidth(100.0d);
        
        tick_marks = new TitledPane();
        tick_marks.setText("Tick Marks");
        tick_marks.setExpanded(false);
    }
    
    public void addAllElements(){
        System.out.println("RecCASaveScale method addAllElements(): start");
        
        Label l1 = new Label("1st value:");
        Label l2 = new Label("2st value:");
        Label l3 = new Label("3st value:");
        Label l4 = new Label("4st value:");
        Label l5 = new Label("5st value:");
        
        GridPane tick_marks_layout = new GridPane();
        tick_marks_layout.setAlignment(Pos.CENTER);
        tick_marks_layout.setHgap(5.0d);
        tick_marks_layout.setVgap(5.0d);
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l2, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l3, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l4, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(l5, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(first_value, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(second_value, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(third_value, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(fourth_value, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(fifth_value, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        tick_marks_layout.getChildren().addAll(
                l1, l2, l3, l4, l5,
                first_value,
                second_value,
                third_value,
                fourth_value,
                fifth_value
        );
        tick_marks.setContent(tick_marks_layout);
        
        VBox tool_bar_layout = new VBox();
        tool_bar_layout.setPadding(new Insets(2, 2, 2, 2));
        tool_bar_layout.setAlignment(Pos.CENTER);
        
        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(5, 2, 5, 2));
        
        tool_bar_layout.getChildren().add(tick_marks);
        
        tool_bar.getItems().add(tool_bar_layout);
        
        HBox buttons_layout = new HBox();
        buttons_layout.setAlignment(Pos.CENTER);
        
        Separator ver_sep1 = new Separator();
        ver_sep1.setPadding(new Insets(0, 7, 0, 7));
        
        buttons_layout.getChildren().addAll(
                save, ver_sep1, close
        );
        
        Label file_name_lbl = new Label("File Name");
        Label dir_lbl = new Label("Directory:");
        
        GridPane bottom_layout = new GridPane();
        bottom_layout.setAlignment(Pos.CENTER);
        bottom_layout.setHgap(7.0d);
        bottom_layout.setVgap(7.0d);
        
        GridPane.setConstraints(file_name_lbl, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(dir_lbl, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        
        GridPane.setConstraints(file_name_txt_fld, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(directory, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(file_extension, 2, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(choose_dir, 2, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        bottom_layout.getChildren().addAll(
                file_name_lbl, dir_lbl, 
                file_name_txt_fld, directory,
                file_extension, choose_dir
        );
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(5, 2, 5, 2));
        
        VBox bottom_tool_bar_layout = new VBox();
        bottom_tool_bar_layout.setPadding(new Insets(5, 5, 5, 5));
        bottom_tool_bar_layout.setAlignment(Pos.CENTER);
        bottom_tool_bar_layout.getChildren().addAll(
                bottom_layout,
                sep2,
                buttons_layout
        );
        
        bottom_tool_bar.getItems().add(bottom_tool_bar_layout);
        root.setBottom(bottom_tool_bar);
        root.setRight(tool_bar);
        
        title_lbl.setText(", " + prefix + measure);
        
        HBox title = new HBox();
        title.setAlignment(Pos.CENTER_LEFT);
        title.getChildren().addAll(title_image, title_lbl);
        
        GridPane.setConstraints(title, 0, 0, 2, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(scale_image, 0, 1, 1, 5, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(tick_lbl5, 1, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        tick_lbl5.setPadding(new Insets(1, 4, 8, 2));
        
        GridPane.setConstraints(tick_lbl4, 1, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        tick_lbl4.setPadding(new Insets(1, 4, 8, 2));
        
        GridPane.setConstraints(tick_lbl3, 1, 3, 1, 1, HPos.LEFT, VPos.CENTER);
        tick_lbl3.setPadding(new Insets(1, 4, 8, 2));
        
        GridPane.setConstraints(tick_lbl2, 1, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        tick_lbl2.setPadding(new Insets(1, 4, 8, 2));
        
        GridPane.setConstraints(tick_lbl1, 1, 5, 1, 1, HPos.LEFT, VPos.CENTER);
        
        layout.setHgap(7.0d);
        layout.setVgap(7.0d);
        layout.getChildren().addAll(
                title, scale_image, tick_lbl1, tick_lbl2, tick_lbl3, tick_lbl4, tick_lbl5
        );
        
        layout.setStyle("-fx-background-color: white; -fx-font-size: 14px; -fx-text-fill: black");
        
        root.setCenter(layout);
    }
    
    public void handleEvents(){
        System.out.println("RecCASaveScale method handleEvents(): start");
        
        close.setOnAction(e -> this.close());
        
        save.setOnAction(e -> {
            
            System.out.println("RecCASaveScale button save is pushed");
            if(file_name_txt_fld.getText().isEmpty() || directory.getText().isEmpty()){
                new Alert(AlertType.ERROR, "Type File Name and File Directory").show();
            }
            else{
                
                try{
                    saveFile(directory.getText(), file_name_txt_fld.getText());
                    new Alert(AlertType.INFORMATION, "Image successfully saved").showAndWait();
                    this.close();
                }
                catch(Exception ex){
                    new Alert(AlertType.ERROR, "Failed to save the image").showAndWait();
                }
            }
        });
        
        first_value.textProperty().bindBidirectional(tick_lbl1.textProperty());
        second_value.textProperty().bindBidirectional(tick_lbl2.textProperty());
        third_value.textProperty().bindBidirectional(tick_lbl3.textProperty());
        fourth_value.textProperty().bindBidirectional(tick_lbl4.textProperty());
        fifth_value.textProperty().bindBidirectional(tick_lbl5.textProperty());
        
        choose_dir.setOnAction(e -> {
            directory.setText(new DirectoryChooser().showDialog(this).getAbsolutePath());
        });
    }
    
    public void saveFile(String dir, String name){
        System.out.println("RecCASaveScale method saveFile(): start");
        String ext = file_extension.getSelectionModel().getSelectedItem().toString();
        WritableImage image = layout.snapshot(new SnapshotParameters(), null);
        File file = new File(dir + "/" + name + "." + ext);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), ext, file);
        } catch (IOException ex) {
            System.out.println("Error: RecCASaveImage save image event");
        }        
    }
    
}
