/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

/**
 *
 * @author dmitryb
 */
public class RecCAScaleLayout extends GridPane{
    
    double scene_width = Screen.getPrimary().getVisualBounds().getWidth();
    double scene_height = Screen.getPrimary().getVisualBounds().getHeight();
    
    private final static double RED_HUE = Color.RED.getHue() ;
    private final static double PURPLE_HUE = Color.PURPLE.getHue();
    
    TextField max_txt_fld = new TextField();
    TextField min_txt_fld = new TextField();
    
    private double max, min;
    
    public double getMaxValue(){
        return max;
    }
    
    public double getMinValue(){
        return min;
    }
    
    Label l1, l2;
    
    GridPane title;
    
    Label tick_lbl1 = new Label();
    Label tick_lbl2 = new Label();
    Label tick_lbl3 = new Label();
    Label tick_lbl4 = new Label();
    Label tick_lbl5 = new Label();
    
    Button show_this, save;
    ComboBox scale_type;
    CheckBox reverse;
    ImageView scale_image;
    
    RadioButton new_tab;
    
    RecCAResultsParams res;
    
    public RecCAScale scale;
    
    int width, height;
    Color color;
    
    String param, measure, prefix;
    
    boolean is_reverse;
    
    public boolean isReverse(){
        return this.is_reverse;
    }
    public void setReverse(boolean value){
        this.is_reverse = value;
    }
    
    public RecCAScaleLayout(String smth){
        System.out.println("Create empty RecCAScaleLayout");
        this.add(new Label(smth), 0, 0);
    }
    
    public RecCAScaleLayout(RecCAResultsParams results, RecCAScale scale ,double max_value, double min_value, String param){
        
        this.setStyle("-fx-background-color: white");
        
        this.setPadding(new Insets(5,5,5,5));
        
        this.width = 50;
        this.height = 150;
        
        this.res = results;
        this.scale = scale;
        
        this.param = param;
        
        System.out.println("RecCAScaleLayout constructor start");
            
        l1 = new Label("Max Value:");
        l2 = new Label("Min Value:");
        title = new GridPane();
        
        this.max = max_value;
        this.min = min_value;
        
        String lbl_style = "{ -fx-font-size: 12pt; -fx-text-fill: black; }";
        
        tick_lbl1.setStyle(lbl_style);
        tick_lbl2.setStyle(lbl_style);
        tick_lbl3.setStyle(lbl_style);
        tick_lbl4.setStyle(lbl_style);
        tick_lbl5.setStyle(lbl_style);
        
        max_txt_fld.setMaxWidth(100.0d);
        String max_val_text = String.format("%1.2e", max);
        max_txt_fld.setText(max_val_text.replaceAll(",", "."));
        
        min_txt_fld.setMaxWidth(100.0d);
        String min_val_text = String.format("%1.2e", min);
        min_txt_fld.setText(min_val_text.replaceAll(",", "."));
        
        setTickLabel(max_value, min_value, "rainbow");
        setTitle();
        
        this.scale_type = new ComboBox();
        scale_type.getItems().addAll(
                "rainbow", "gray", "red/blue", "centered", "4colours"
        );
        scale_type.getSelectionModel().selectFirst();
        
        reverse = new CheckBox("Reverse");        
        
        show_this = new Button("Show This");
        show_this.setPadding(new Insets(5, 20, 5, 20));
        
        save = new Button("Save as Image");
        save.setPadding(new Insets(5, 20, 5, 20));
        
        new_tab = new RadioButton("New Tab");
        
        Separator sep = new Separator();
        Separator sep1 = new Separator();
        Separator sep2 = new Separator(Orientation.VERTICAL);
        Separator sep3 = new Separator(Orientation.HORIZONTAL);
        
        scale_image = new ImageView();
        
        this.createScaleImage(this.scale_type.getSelectionModel().getSelectedItem().toString());
        
        GridPane.setConstraints(l1, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(max_txt_fld, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(l2, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(min_txt_fld, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(sep, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(scale_type, 0, 3, 2, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setConstraints(reverse, 0, 4, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep1, 0, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(show_this, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(new_tab, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(sep3, 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(save, 0, 8, 2, 1, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(sep2, 2, 0, 1, 7, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(title, 3, 0, 2, 1, HPos.LEFT, VPos.CENTER);
        
        /**
         * Scale Image Layout
         */
        GridPane scale_image_layout = new GridPane();
        GridPane.setConstraints(scale_image, 0, 0, 1, 5, HPos.LEFT, VPos.CENTER);
        
        GridPane.setConstraints(tick_lbl5, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setMargin(tick_lbl5, new Insets(0, 0, 18, 7));
        
        GridPane.setConstraints(tick_lbl4, 1, 1, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setMargin(tick_lbl4, new Insets(0, 0, 18, 7));
        
        GridPane.setConstraints(tick_lbl3, 1, 2, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setMargin(tick_lbl3, new Insets(0, 0, 18, 7));
        
        GridPane.setConstraints(tick_lbl2, 1, 3, 1, 1, HPos.LEFT, VPos.CENTER);
        GridPane.setMargin(tick_lbl2, new Insets(0, 0, 18, 7));
        
        GridPane.setConstraints(tick_lbl1, 1, 4, 1, 1, HPos.LEFT, VPos.CENTER);
        
        scale_image_layout.getChildren().addAll(
                scale_image, tick_lbl5, tick_lbl4, tick_lbl3, tick_lbl2, tick_lbl1
        );
        
        GridPane.setConstraints(scale_image_layout, 3, 1, 1, 5, HPos.LEFT, VPos.CENTER);
        
        
        this.getChildren().addAll(
            l1, l2, max_txt_fld, min_txt_fld, sep, scale_type, reverse, sep1, show_this, title, sep2, save, scale_image_layout, new_tab, sep3
        );
        
        this.setAlignment(Pos.CENTER_LEFT);        
        this.setHgap(3.0d);
        this.setVgap(3.0d);
        
        save.setOnAction(e -> {
            System.out.println("RecCAScaleLayout button save is pushed");
            RecCASaveScale save_scale = new RecCASaveScale(this);
        });
    }
    
    public void createScaleImage(String scale_type){
        
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixel_writer= image.getPixelWriter();
        
        int y;        
        for(y = 0 ; y < 10 ; y++){
            for (int x=0; x<width; x++) {
                pixel_writer.setColor(x, y, Color.BLACK);
            }
        }
        for(y=10 ; y < (height-10) ; y++){
            double value = max - (max - min) * (y-10) / (height-10) ;
            Color color = getColor(scale_type, value);
            for (int x=0; x<width; x++) {
                pixel_writer.setColor(x, y, color);
            }
        }
        for(y = ((int) (height - 10)) ; y < height ; y++){
            for (int x=0; x<width; x++) {
                pixel_writer.setColor(x, y, Color.WHITE);
            }
        }
        this.scale_image.setImage(image);
    }
    
    public void setTickLabel(double max_val, double min_val, String type){
        
        double first = max_val,
               second = min_val + (max_val - min_val)*0.75,
               third = min_val + (max_val - min_val)*0.5,
               fourth = min_val + (max_val - min_val)*0.25,
               fifth = min_val;
        
        double MAX_VAL = Math.abs(max_val) >= Math.abs(min_val) ? Math.abs(max_val) : Math.abs(min_val);
        
        if(param.equals("temperature") || param.equals("relative_temperature")){
            if(type.equals("red/blue")){
                prefix = "";
                this.tick_lbl5.setText(String.format("%.3f",first));
                this.tick_lbl4.setText("");
                this.tick_lbl3.setText("0.0");
                this.tick_lbl2.setText("");
                this.tick_lbl1.setText(String.format("%.3f",fifth));
            }
            else{
                prefix = "";
                this.tick_lbl5.setText(String.format("%.3f",first));
                this.tick_lbl4.setText(String.format("%.3f",second));
                this.tick_lbl3.setText(String.format("%.3f",third));
                this.tick_lbl2.setText(String.format("%.3f",fourth));
                this.tick_lbl1.setText(String.format("%.3f",fifth));
            }
        }
        else{
            if(type.equals("red/blue")){
                if( MAX_VAL/ 1e+9 > 1 ){
                    prefix = "G";
                    tick_lbl5.setText(String.format("%.3f",first / 1e+9));
                    tick_lbl4.setText("");
                    tick_lbl3.setText("0.0");
                    tick_lbl2.setText("");
                    tick_lbl1.setText(String.format("%.3f",fifth / 1e+9));
                }
                else if(MAX_VAL/1e+6 > 1){
                    prefix = "M";
                    tick_lbl5.setText(String.format("%.3f",first / 1e+6));
                    tick_lbl4.setText("");
                    tick_lbl3.setText("0.0");
                    tick_lbl2.setText("");
                    tick_lbl1.setText(String.format("%.3f",fifth / 1e+6));
                }
                else if(MAX_VAL/1e+3 > 1){
                    prefix = "k";
                    tick_lbl5.setText(String.format("%.3f",first / 1e+3));
                    tick_lbl4.setText("");
                    tick_lbl3.setText("0.0");
                    tick_lbl2.setText("");
                    tick_lbl1.setText(String.format("%.3f",fifth / 1e+3));
                }
                else if(MAX_VAL/1e-9 < 1){
                    prefix = "n";
                    tick_lbl5.setText(String.format("%.3f",first / 1e-9));
                    tick_lbl4.setText("");
                    tick_lbl3.setText("0.0");
                    tick_lbl2.setText("");
                    tick_lbl1.setText(String.format("%.3f",fifth / 1e-9));
                }
                else if(MAX_VAL/1e-6 < 1){
                    prefix = "mc";
                    tick_lbl5.setText(String.format("%.3f",first / 1e-6));
                    tick_lbl4.setText("");
                    tick_lbl3.setText("0.0");
                    tick_lbl2.setText("");
                    tick_lbl1.setText(String.format("%.3f",fifth / 1e-6));
                }
                else if(MAX_VAL/1e-3 < 1){
                    prefix = "m";
                    tick_lbl5.setText(String.format("%.3f",first / 1e-3));
                    tick_lbl4.setText("");
                    tick_lbl3.setText("0.0");
                    tick_lbl2.setText("");
                    tick_lbl1.setText(String.format("%.3f",fifth / 1e-3));
                }
                else{
                    prefix = "";
                    tick_lbl5.setText(String.format("%.3f",first));
                    tick_lbl4.setText("");
                    tick_lbl3.setText("0.0");
                    tick_lbl2.setText("");
                    tick_lbl1.setText(String.format("%.3f",fifth));                
                }            
            }
            else{            
                if( MAX_VAL/ 1e+9 >= 1 ){
                    prefix = "G";
                    this.tick_lbl5.setText(String.format("%.3f", first / 1e+9));
                    this.tick_lbl4.setText(String.format("%.3f", second / 1e+9));
                    this.tick_lbl3.setText(String.format("%.3f", third / 1e+9));
                    this.tick_lbl2.setText(String.format("%.3f", fourth / 1e+9));
                    this.tick_lbl1.setText(String.format("%.3f", fifth / 1e+9));
                }
                else if(MAX_VAL/1e+6 >= 1){
                    prefix = "M";
                    this.tick_lbl5.setText(String.format("%.3f", first / 1e+6));
                    this.tick_lbl4.setText(String.format("%.3f", second / 1e+6));
                    this.tick_lbl3.setText(String.format("%.3f", third / 1e+6));
                    this.tick_lbl2.setText(String.format("%.3f", fourth / 1e+6));
                    this.tick_lbl1.setText(String.format("%.3f", fifth / 1e+6));
                }
                else if(MAX_VAL/1e+3 >= 1){
                    prefix = "k";
                    this.tick_lbl5.setText(String.format("%.3f", first / 1e+3));
                    this.tick_lbl4.setText(String.format("%.3f", second / 1e+3));
                    this.tick_lbl3.setText(String.format("%.3f", third / 1e+3));
                    this.tick_lbl2.setText(String.format("%.3f", fourth / 1e+3));
                    this.tick_lbl1.setText(String.format("%.3f", fifth / 1e+3));
                }
                else if(MAX_VAL/1e-9 <= 1){
                    prefix = "n";
                    this.tick_lbl5.setText(String.format("%.3f", first / 1e-9));
                    this.tick_lbl4.setText(String.format("%.3f", second / 1e-9));
                    this.tick_lbl3.setText(String.format("%.3f", third / 1e-9));
                    this.tick_lbl2.setText(String.format("%.3f", fourth / 1e-9));
                    this.tick_lbl1.setText(String.format("%.3f", fifth / 1e-9));
                }
                else if(MAX_VAL/1e-6 <= 1){
                    prefix = "mc";
                    this.tick_lbl5.setText(String.format("%.3f", first / 1e-6));
                    this.tick_lbl4.setText(String.format("%.3f", second / 1e-6));
                    this.tick_lbl3.setText(String.format("%.3f", third / 1e-6));
                    this.tick_lbl2.setText(String.format("%.3f", fourth / 1e-6));
                    this.tick_lbl1.setText(String.format("%.3f", fifth / 1e-6));
                }
                else if(MAX_VAL/1e-3 <= 1){
                    prefix = "m";
                    this.tick_lbl5.setText(String.format("%.3f", first / 1e-3));
                    this.tick_lbl4.setText(String.format("%.3f", second / 1e-3));
                    this.tick_lbl3.setText(String.format("%.3f", third / 1e-3));
                    this.tick_lbl2.setText(String.format("%.3f", fourth / 1e-3));
                    this.tick_lbl1.setText(String.format("%.3f", fifth / 1e-3));
                }
                else{
                    prefix = "";
                    this.tick_lbl5.setText(String.format("%.3f",first));
                    this.tick_lbl4.setText(String.format("%.3f",second));
                    this.tick_lbl3.setText(String.format("%.3f",third));
                    this.tick_lbl2.setText(String.format("%.3f",fourth));
                    this.tick_lbl1.setText(String.format("%.3f",fifth));
                }            
            }
        }
        
    }
    
    public double brightness;
    
    public Color getColor(String scale_type, double value){
        if(is_reverse){
            
        }
        else{
            switch(scale_type){
                case "rainbow":
                    if(value < min){
                        color = Color.WHITE;
                    }
                    else if(value > max){
                        color = Color.BLACK;
                    }
                    else{
                        double hue = PURPLE_HUE + (RED_HUE - PURPLE_HUE) * (value - min) / (max - min);
                        color = Color.hsb(hue, 1.0, 1.0);
                    }
                    break;
                case "gray":
                    if(value < min){
                        color = Color.WHITE;
                    }
                    else if(value > max){
                        color = Color.BLACK;
                    }
                    else{
                        double brightness = (value - min) / (max - min) ;
                        color = Color.hsb(120, 0 , 1 -brightness);
                    }
                    break;
                    
                case "4 colours":
                    if(value < min){
                        color = Color.WHITE;
                    }
                    else if(value > max){
                        color = Color.BLACK;
                    }
                    else{
                        if(value < (max - min)*0.25 && value > min){
                            brightness = 0.8;
                        }
                        else if(value < (max-min)*0.5 && value > (max - min)*0.25){
                            brightness = 0.6;
                        }
                        else if(value < (max-min)*0.75 && value > (max - min)*0.5){
                            brightness = 0.4;
                        }
                        else if(value < max && value > (max-min)*0.75){
                            brightness = 0.2;
                        }
                        color = Color.hsb(120, 0 , brightness);
                    }
                    break;
                    
                case "centered":
                    if(value < min){
                        color = Color.WHITE;
                    }
                    else if(value > max){
                        color = Color.BLACK;
                    }
                    else{
                        double center = (max - min)/2;
                        if(value < center){
                            brightness = 0.95 - 0.95 *(value - min) / (max - min) ;
                        }

                        if(value > center){
                            brightness = 0.95 *(value - min) / (max - min) ;
                        }
                        color = Color.hsb(120, 0, brightness);
                    }
                    break;
                    
                case "red/blue":
                    if(value > 0){
                        color = Color.RED;
                    }
                    else color = Color.BLUE;
                    break;
            }
        }
        return color;
    }
    
    Label lbl = new Label();
    
    public void setTitle(){
        
        switch(param){
            case "temperature":
                measure = "K";
                break;
            case "specific_elastic_energy":
                measure = "Pa";
                break;
            case "principal_stresses":
                measure = "Pa";
                break;
            case "x_component_of_specific_force_moment":
                measure = "Pa";
                break;
            case "y_component_of_specific_force_moment":
                measure = "Pa";
                break;
            case "z_component_of_specific_force_moment":
                measure = "Pa";
                break;
            case "current_influx_of_specific_dissipated_energy":
                measure = "Pa";
                break;
            case "specific_dissipated_energy":
                measure = "Pa";
                break;        
                
            case "abs_value_of_stress_vector":
                measure = "Pa";
                break;
            case "value_for_crack_generation":
                measure = "Pa";
                break;
            case "x_comp_of_stress_vector":
                measure = "Pa";
                break;
            case "y_comp_of_stress_vector":
                measure = "Pa";
                break;
            case "z_comp_of_stress_vector":
                measure = "Pa";
                break;
            case "current_influx_of_spec_dissipated_energy":
                measure = "Pa";
                break;
                
            case "relative_temperature":
                measure = "K";
                break;
            case "relative_specific_elastic_energy":
                measure = "%";
                break;
            case "total_specific_energy":
                measure = "Pa";
                break;
            case "abs_value_of_X_comp_of_inst_spec_force_moment":
                measure = "Pa";
                break;
            case "abs_value_of_Y_comp_of_inst_spec_force_moment":
                measure = "Pa";
                break;
            case "abs_value_of_Z_comp_of_inst_spec_force_moment":
                measure = "Pa";
                break;
            case "abs_value_of_inst_spec_force_moment":
                measure = "Pa";
                break;
            case "relative_specific_dissipated_energy":
                measure = "%";
                break;
                
            case "strain":
                measure = "Pa";
                break;
            case "total_specific_torsion_energy":
                measure = "Pa";
                break;
            case "x_component_of_inst_spec_force_moment":
                measure = "Pa";
                break;
            case "y_component_of_inst_spec_force_moment":
                measure = "Pa";
                break;
            case "z_component_of_inst_spec_force_moment":
                measure = "Pa";
                break;
            case "current_influx_of_torsion_energy":
                measure = "Pa";
                break;
            case "x_component_of_angle_velocity":
                measure = "rad/s";
                break;
            case "y_component_of_angle_velocity":
                measure = "rad/s";
                break;
            case "z_component_of_angle_velocity":
                measure = "rad/s";
                break;
            case "x_component_of_torsion_angle_vector":
                measure = "rad";
                break;
            case "y_component_of_torsion_angle_vector":
                measure = "rad";
                break;
            case "z_component_of_torsion_angle_vector":
                measure = "rad";
                break;
            case "relative_power_of_torsion":
                measure = "%";
                break;
            case "relative_torsion_energy":
                measure = "%";
                break;
            case "effective_stresses":
                measure = "Pa";
                break;
        }
        
        lbl.setText(", " + prefix + measure);
        lbl.setStyle("{ -fx-font-size: 20pt; -fx-text-fill: black; }");
        ImageView image = getTitleImage();
        GridPane.setConstraints(image, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        GridPane.setConstraints(lbl, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        this.title.getChildren().clear();
        this.title.getChildren().addAll(image, lbl);
    }
    
    public ImageView getTitleImage(){
        Image image = null;
        ImageView view = null;
        switch(param){
            case "temperature":
                image = new Image("./interf/scale_images/temperature.png");
                break;
            case "specific_elastic_energy":
                image = new Image("./interf/scale_images/spec_elastic_energy.png");
                break;
            case "principal_stresses":
                image = new Image("./interf/scale_images/principal_stresses.png");
                break;
            case "x_component_of_specific_force_moment":
                image = new Image("./interf/scale_images/spec_mom_x.png");
                break;
            case "y_component_of_specific_force_moment":
                image = new Image("./interf/scale_images/spec_mom_y.png");
                break;
            case "z_component_of_specific_force_moment":
                image = new Image("./interf/scale_images/spec_mom_z.png");
                break;
            case "current_influx_of_specific_dissipated_energy":
                image = new Image("./interf/scale_images/influx_of_spec_diss_energy.png");
                break;
            case "specific_dissipated_energy":
                image = new Image("./interf/scale_images/spec_diss_energy.png");
                break;        
            
            case "x_comp_of_stress_vector":
                image = new Image("./interf/scale_images/x_comp_of_stress_vector.png");
                break;
            case "y_comp_of_stress_vector":
                image = new Image("./interf/scale_images/y_comp_of_stress_vector.png");
                break;
            case "z_comp_of_stress_vector":
                image = new Image("./interf/scale_images/z_comp_of_stress_vector.png");
                break;
            case "abs_value_of_stress_vector":
                image = new Image("./interf/scale_images/abs_stress_vector.png");
                break;
            case "current_influx_of_spec_dissipated_energy":
                image = new Image("./interf/scale_images/influx_of_spec_torsion_energy.png");
                break;
                
            case "relative_temperature":
                image = new Image("./interf/scale_images/temperature.png");
                break;
            case "relative_specific_elastic_energy":
                image = new Image("./interf/scale_images/effective_stresses.png");
                break;
            case "total_specific_energy":
                image = new Image("./interf/scale_images/total_specific_energy.png");
                break;
            case "abs_value_of_X_comp_of_inst_spec_force_moment":
                image = new Image("./interf/scale_images/abs_spec_mom_x.png");
                break;
            case "abs_value_of_Y_comp_of_inst_spec_force_moment":
                image = new Image("./interf/scale_images/abs_spec_mom_y.png");
                break;
            case "abs_value_of_Z_comp_of_inst_spec_force_moment":
                image = new Image("./interf/scale_images/abs_spec_mom_z.png");
                break;
            case "abs_value_of_inst_spec_force_moment":
                image = new Image("./interf/scale_images/abs_spec_mom.png");
                break;
            case "relative_specific_dissipated_energy":
                image = new Image("./interf/scale_images/relative_torsion_energy.png");
                break;
                
            case "strain":
                image = new Image("./interf/scale_images/strain.png");
                break;
            case "total_specific_torsion_energy":
                image = new Image("./interf/scale_images/spec_diss_energy.png");
                break;
            case "x_component_of_inst_spec_force_moment":
                image = new Image("./interf/scale_images/abs_spec_mom_x.png");
                break;
            case "y_component_of_inst_spec_force_moment":
                image = new Image("./interf/scale_images/abs_spec_mom_y.png");
                break;
            case "z_component_of_inst_spec_force_moment":
                image = new Image("./interf/scale_images/abs_spec_mom_z.png");
                break;
            case "current_influx_of_torsion_energy":
                image = new Image("./interf/scale_images/influx_of_spec_torsion_energy.png");
                break;
            case "x_component_of_angle_velocity":
                image = new Image("./interf/scale_images/angle_velocity_x.png");
                view = new ImageView(image);
                break;
            case "y_component_of_angle_velocity":
                image = new Image("./interf/scale_images/angle_velocity_y.png");
                break;
            case "z_component_of_angle_velocity":
                image = new Image("./interf/scale_images/angle_velocity_z.png");
                break;
            case "x_component_of_torsion_angle_vector":
                image = new Image("./interf/scale_images/angle_vector_x.png");
                break;
            case "y_component_of_torsion_angle_vector":
                image = new Image("./interf/scale_images/angle_vector_y.png");
                break;
            case "z_component_of_torsion_angle_vector":
                image = new Image("./interf/scale_images/angle_vector_z.png");
                break;
            case "relative_power_of_torsion":
                image = new Image("./interf/scale_images/relative_power_of_torsion.png");
                break;
            case "relative_torsion_energy":
                image = new Image("./interf/scale_images/relative_torsion_energy.png");
                break;
            case "effective_stresses":
                image = new Image("./interf/scale_images/effective_stresses.png");                
                break;
        }
        view = new ImageView(image);
        return view;
    }
    
}
