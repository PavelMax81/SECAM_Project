/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author dmitryb
 */
public class RecCAChartPreferences extends VBox{
    
    TitledPane general;
    RecCAGeneralPreferences general_prefs;
    
    TitledPane line1_tp, line2_tp, line3_tp, line4_tp, line5_tp;
    RecCAChartLineLayout line1, line2, line3, line4, line5;
    
    boolean is_line2, is_line3, is_line4, is_line5;
    
    public RecCAChartPreferences(){
        setAlignment(Pos.CENTER_LEFT);
        initAllElements();
        getChildren().addAll(
                general,
                new Separator(),
                line1_tp
        );
    }
    
    public RecCAChartPreferences(RecCAGeneralPreferences prefs, RecCAChartLineLayout line1){
        setAlignment(Pos.CENTER_LEFT);
        
        general = new TitledPane();
        general.setText("General Preferences");
        general.setContent(prefs);
        general.setExpanded(false);
        
        line1_tp = new TitledPane();
        line1_tp.setText("Curve #1");
        line1_tp.setContent(line1);
        line1_tp.setExpanded(false);
        
        getChildren().addAll(
                general,
                new Separator(),
                line1_tp
        );
    }
    
    public RecCAChartPreferences(RecCAGeneralPreferences general, 
                                 RecCAChartLineLayout line1,
                                 RecCAChartLineLayout line2)
    {}
    
    public RecCAChartPreferences(RecCAGeneralPreferences general, 
                                 RecCAChartLineLayout line1,
                                 RecCAChartLineLayout line2,
                                 RecCAChartLineLayout line3)
    {}
    
    public RecCAChartPreferences(RecCAGeneralPreferences general, 
                                 RecCAChartLineLayout line1,
                                 RecCAChartLineLayout line2,
                                 RecCAChartLineLayout line3,
                                 RecCAChartLineLayout line4)
    {}
    
    public RecCAChartPreferences(RecCAGeneralPreferences general, 
                                 RecCAChartLineLayout line1,
                                 RecCAChartLineLayout line2,
                                 RecCAChartLineLayout line3,
                                 RecCAChartLineLayout line4,
                                 RecCAChartLineLayout line5)
    {}
    
    
    
    public void initAllElements(){
        general = new TitledPane();
        general.setText("General Preferences");
        general.setExpanded(false);
        
        general_prefs = new RecCAGeneralPreferences();
        
        general.setContent(general_prefs);
        
        line1_tp = new TitledPane();
        line1_tp.setText("Curve #1");
        line1_tp.setExpanded(false);
        
        line1 = new RecCAChartLineLayout();
        
        line1_tp.setContent(line1);        
    }
    
    public void addSecondCurve(){
        line2_tp = new TitledPane();
        line2_tp.setText("Curve #2");
        line2_tp.setExpanded(false);
        
        line2 = new RecCAChartLineLayout();
        
        line2_tp.setContent(line2);
        
        getChildren().add(line2_tp);
        
        is_line2 = true;
        setContextMenu(line2_tp);
    }
    
    private void setContextMenu(TitledPane tp){
        ContextMenu menu = new ContextMenu();
        MenuItem close = new MenuItem("Close");
        menu.getItems().add(close);
        
        tp.setContextMenu(menu);
        
        close.setOnAction(e -> {
            if(tp.equals(line2_tp)){
                is_line2 = false;
                getChildren().remove(line2_tp);
            }
            else if(tp.equals(line3_tp)){
                is_line3 = false;
                getChildren().remove(line3_tp);
            }
            else if(tp.equals(line4_tp)){
                is_line4 = false;
                getChildren().remove(line4_tp);
            }
            else if(tp.equals(line5_tp)){
                is_line5 = false;
                getChildren().remove(line5_tp);
            }
        });
    }
    
    public void addThirdCurve(){
        line3_tp = new TitledPane();
        line3_tp.setText("Curve #3");
        line3_tp.setExpanded(false);
        
        line3 = new RecCAChartLineLayout();
        
        line3_tp.setContent(line3);
        
        getChildren().add(line3_tp);
        
        is_line3 = true;
        setContextMenu(line3_tp);
    }
    
    public void addFourthCurve(){
        line4_tp = new TitledPane();
        line4_tp.setText("Curve #4");
        line4_tp.setExpanded(false);
        
        line4 = new RecCAChartLineLayout();
        
        line4_tp.setContent(line4);
        
        getChildren().add(line4_tp);
        
        is_line4 = true;
        setContextMenu(line4_tp);
    }
    
    public void addFifthCurve(){
        line5_tp = new TitledPane();
        line5_tp.setText("Curve #5");
        line5_tp.setExpanded(false);
        
        line5 = new RecCAChartLineLayout();
        
        line5_tp.setContent(line5);
        
        getChildren().add(line5_tp);
        
        is_line5 = true;
        setContextMenu(line5_tp);
    }
    
}
