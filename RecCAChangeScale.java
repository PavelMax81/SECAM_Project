/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author DmitryB
 */
public class RecCAChangeScale extends Stage{
    
    Scene scene;
    BorderPane layout;
    
    RecCAScaleLayout scale;
    
    public RecCAChangeScale(RecCAScaleLayout scale_layout){
        
        scale = scale_layout;
        
        initAllElements();
        
        scene = new Scene(layout);
        centerOnScreen();
        setScene(scene);
        setTitle("RecCA - 'Change Scale' ");
        show();
        
        handleEvents();
    }
    
    ToolBar tool_bar;
    Button save, close;
    
    TextField first_tick_mark, second_tick_mark, third_tick_mark, fourth_tick_mark, fifth_tick_mark;
    TextField scale_title;
    
    private void initAllElements(){
        
        layout = new BorderPane();
        
        save = new Button("Save");
        close = new Button("Close");
        
        tool_bar = new ToolBar();
        tool_bar.setOrientation(Orientation.VERTICAL);
        
        scale_title = new TextField();
        
        first_tick_mark = new TextField();
        second_tick_mark = new TextField();
        third_tick_mark = new TextField();
        fourth_tick_mark = new TextField();
        fifth_tick_mark = new TextField();
        
        setInitialValues();
        
        Label l1 = new Label("Scale Titles:");
        
        Label l2 = new Label("First Tick Mark:");
        Label l3 = new Label("Second Tick Mark:");
        Label l4 = new Label("Third Tick Mark:");
        Label l5 = new Label("Fourth Tick Mark:");
        Label l6 = new Label("Fifth Tick Mark:");
        
        GridPane tool_bar_layout = new GridPane();
        tool_bar_layout.setAlignment(Pos.CENTER_LEFT);
        tool_bar_layout.setVgap(5.0);
        tool_bar_layout.setHgap(5.0);
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(scale_title, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        
        Separator sep1 = new Separator();
        
        GridPane.setConstraints(sep1, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l2, 0, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(first_tick_mark, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l3, 0, 3, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(second_tick_mark, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l4, 0, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(third_tick_mark, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l5, 0, 5, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(fourth_tick_mark, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(l6, 0, 6, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(fifth_tick_mark, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        
        tool_bar_layout.getChildren().addAll(
                l1, l2, l3, l4, l5, l6, sep1,
                scale_title, first_tick_mark, second_tick_mark, 
                third_tick_mark, fourth_tick_mark, fifth_tick_mark
        );
        
        tool_bar.getItems().add(tool_bar_layout);
        
        HBox buttons_layout = new HBox();
        buttons_layout.setAlignment(Pos.CENTER);
        
        Separator hsep1 = new Separator(Orientation.VERTICAL);
        hsep1.setPadding(new Insets(0, 10, 0, 10));
        
        buttons_layout.getChildren().addAll(save, hsep1, close);
        
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(10, 2, 10, 2));
        
        tool_bar.getItems().addAll(sep2, buttons_layout);
        
        layout.setRight(tool_bar);
    }
    
    private void setInitialValues(){
        first_tick_mark.setText(scale.tick_lbl5.getText());
        second_tick_mark.setText(scale.tick_lbl4.getText());
        third_tick_mark.setText(scale.tick_lbl3.getText());
        fourth_tick_mark.setText(scale.tick_lbl2.getText());
        fifth_tick_mark.setText(scale.tick_lbl1.getText());
    }
    
    private void setInputValues(){
        scale.tick_lbl1.setText(fifth_tick_mark.getText());
        scale.tick_lbl2.setText(fourth_tick_mark.getText());
        scale.tick_lbl3.setText(third_tick_mark.getText());
        scale.tick_lbl4.setText(second_tick_mark.getText());
        scale.tick_lbl5.setText(first_tick_mark.getText());
    }
    
    private void handleEvents(){
        close.setOnAction(e -> close());
        
        save.setOnAction(e -> {
            setInputValues();
            close();
        });
    }
    
}
