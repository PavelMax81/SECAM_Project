/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 *
 * @author dmitryb
 */
public class RecCAChartCreator{
    
    LineChart line_chart;
    NumberAxis time_axis;
    NumberAxis value_axis;
    
    XYChart.Series series, 
                   series_No2,  
                   series_No3,
                   series_No4, 
                   series_No5;
    
    StringProperty series1_name = new SimpleStringProperty("line #1");
    StringProperty series2_name = new SimpleStringProperty("line #2");
    StringProperty series3_name = new SimpleStringProperty("line #3");
    StringProperty series4_name = new SimpleStringProperty("line #4");
    StringProperty series5_name = new SimpleStringProperty("line #5");
    
    List<Double> time, parameters, 
                       parameters_No2, 
                       parameters_No3, 
                       parameters_No4,  
                       parameters_No5;
    
    double prefix_val;
    String prefix;
    
    double time_prefix_val;
    String time_prefix;
    
    /**
     * Constructor for creation single line chart
     * @param time_steps array of time steps
     * @param param array of parameter values
     * @param param_name the name of parameter
     */    
    public RecCAChartCreator(List<Double> time_steps, List<Double> param){
        
        this.time = time_steps;
        this.parameters = param;
        
        time_axis = new NumberAxis();
        value_axis = new NumberAxis();
        line_chart = new LineChart(time_axis, value_axis);
        time_axis.setMinHeight(5.0);
        line_chart.getStylesheets().add("./interf/charts/css/main_stylesheet.css");
        
        series = new XYChart.Series();
        
        series.nameProperty().bind(series1_name);
        
        for(int i = 0 ; i < param.size() ; i++){
            series.getData().add(new XYChart.Data<>(time.get(i),
                    parameters.get(i)));
        }
        line_chart.getData().add(series);
        line_chart.setCreateSymbols(false);
    }
    
    public RecCAChartCreator(List<Double> time_steps1, List<Double> time_steps2, 
                             List<Double> param_No1, List<Double> param_No2){
        
        this.time = time_steps1;
        
        if(time.size() > time_steps2.size()){
            time = time.subList(0, time_steps2.size());
            parameters = param_No1.subList(0, time_steps2.size());
            parameters_No2 = param_No2;
        }
        else if(time.size() < time_steps2.size()){
            parameters_No2 = param_No2.subList(0, time.size());
        }
        else if(time.size() == time_steps2.size()){
            this.parameters = param_No1;
            this.parameters_No2 = param_No2;
        }
        
        time_axis = new NumberAxis();
        value_axis = new NumberAxis();
        line_chart = new LineChart(time_axis, value_axis);
        
        line_chart.getStylesheets().add("./interf/charts/css/main_stylesheet.css");
        
        series = new XYChart.Series();
        series_No2 = new XYChart.Series();
        
        series.nameProperty().bind(series1_name);
        series_No2.nameProperty().bind(series2_name);
        
        for(int i = 0 ; i < parameters.size() ; i++){
            series.getData().add(new XYChart.Data<>(time.get(i),
                    parameters.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No2.size() ; i++){
            series_No2.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No2.get(i)));
        }
        
        line_chart.getData().addAll(
                series,
                series_No2
        );
        line_chart.setCreateSymbols(false);
    }
    
    public RecCAChartCreator(List<Double> time_steps1, List<Double> time_steps2, List<Double> time_steps3, 
                             List<Double> param_No1, List<Double> param_No2, List<Double> param_No3){
        
        this.time = time_steps1;
        
        this.parameters     = param_No1;
        this.parameters_No2 = param_No2;
        this.parameters_No3 = param_No3;
        
        time_axis  = new NumberAxis();
        value_axis = new NumberAxis();
        line_chart = new LineChart(time_axis, value_axis);
        
        line_chart.getStylesheets().add("./interf/charts/css/main_stylesheet.css");
        
        series = new XYChart.Series();
        series_No2 = new XYChart.Series();
        series_No3 = new XYChart.Series();

        series.nameProperty().bind(series1_name);
        series_No2.nameProperty().bind(series2_name);
        series_No3.nameProperty().bind(series3_name);
        
        for(int i = 0; i < parameters.size(); i++)
        {
          series.getData().add(new XYChart.Data<>(time.get(i), parameters.get(i)));
        }
        
        for(int i = 0; i < parameters_No2.size(); i++)
        {
          series_No2.getData().add(new XYChart.Data<>(time.get(i), parameters_No2.get(i)));
        }
        
        for(int i = 0; i < parameters_No3.size(); i++)
        {
          series_No3.getData().add(new XYChart.Data<>(time.get(i), parameters_No3.get(i)));
        }
        
        line_chart.getData().addAll(series, series_No2, series_No3);
        line_chart.setCreateSymbols(false);
    }
    
    public RecCAChartCreator(List<Double> time_steps1, List<Double> time_steps2, List<Double> time_steps3, List<Double> time_steps4, 
                             List<Double> param_No1, List<Double> param_No2, List<Double> param_No3, List<Double> param_No4){
        
        this.time = time_steps1;
        
        this.parameters = param_No1;
        this.parameters_No2 = param_No2;
        this.parameters_No3 = param_No3;
        this.parameters_No4 = param_No4;
        
        time_axis = new NumberAxis();
        value_axis = new NumberAxis();
        line_chart = new LineChart(time_axis, value_axis);
        
        line_chart.getStylesheets().add("./interf/charts/css/main_stylesheet.css");
        
        series = new XYChart.Series();
        series_No2 = new XYChart.Series();
        series_No3 = new XYChart.Series();
        series_No4 = new XYChart.Series();
        
        series.nameProperty().bind(series1_name);
        series_No2.nameProperty().bind(series2_name);
        series_No3.nameProperty().bind(series3_name);
        series_No4.nameProperty().bind(series4_name);        
        
        for(int i = 0 ; i < parameters.size() ; i++){
            series.getData().add(new XYChart.Data<>(time.get(i),
                    parameters.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No2.size() ; i++){
            series_No2.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No2.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No3.size() ; i++){
            series_No3.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No3.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No4.size() ; i++){
            series_No4.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No4.get(i)));
        }
        
        line_chart.getData().addAll(
                series,
                series_No2,
                series_No3,
                series_No4
        );
        line_chart.setCreateSymbols(false);
    }
    
    public RecCAChartCreator(List<Double> time_steps1, List<Double> time_steps2, List<Double> time_steps3, List<Double> time_steps4, List<Double> time_steps5,
                             List<Double> param_No1, List<Double> param_No2, List<Double> param_No3, List<Double> param_No4, List<Double> param_No5)
    {
        
        this.time = time_steps1;
        
        this.parameters = param_No1;
        this.parameters_No2 = param_No2;
        this.parameters_No3 = param_No3;
        this.parameters_No4 = param_No4;
        this.parameters_No5 = param_No5;
        
        time_axis = new NumberAxis();
        value_axis = new NumberAxis();
        line_chart = new LineChart(time_axis, value_axis);
        
        line_chart.getStylesheets().add("./interf/charts/css/main_stylesheet.css");
        
        series = new XYChart.Series();
        series_No2 = new XYChart.Series();
        series_No3 = new XYChart.Series();
        series_No4 = new XYChart.Series();
        series_No5 = new XYChart.Series();
        
        series.nameProperty().bind(series1_name);
        series_No2.nameProperty().bind(series2_name);
        series_No3.nameProperty().bind(series3_name);
        series_No4.nameProperty().bind(series4_name);
        series_No5.nameProperty().bind(series5_name);
        
        for(int i = 0 ; i < parameters.size() ; i++){
            series.getData().add(new XYChart.Data<>(time.get(i),
                    parameters.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No2.size() ; i++){
            series_No2.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No2.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No3.size() ; i++){
            series_No3.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No3.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No4.size() ; i++){
            series_No4.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No4.get(i)));
        }
        
        for(int i = 0 ; i < parameters_No5.size() ; i++){
            series_No5.getData().add(new XYChart.Data<>(time.get(i),
                    parameters_No5.get(i)));
        }
        
        line_chart.getData().addAll(
                series,
                series_No2,
                series_No3,
                series_No4,
                series_No5
        );
        line_chart.setCreateSymbols(false);
    }
    
    public void setPrefixValue(int line_number, double value_prefix, double time_prefix){
        line_chart.getData().clear();
        
        switch(line_number){
            
            case 1:
                
                XYChart.Series data = new XYChart.Series<>();
                data.nameProperty().bind(series1_name);
                for(int i = 0 ; i < parameters.size() ; i++){
                    data.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters.get(i)/value_prefix));
                }        
                
                line_chart.getData().add(data);
                
                break;
                
            case 2:
                
                XYChart.Series data21 = new XYChart.Series<>();
                XYChart.Series data22 = new XYChart.Series<>();
                
                data21.nameProperty().bind(series1_name);
                data22.nameProperty().bind(series2_name);
                
                for(int i = 0 ; i < parameters.size() ; i++){
                    data21.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters.get(i)/value_prefix));
                }        
                
                for(int i = 0 ; i < parameters_No2.size() ; i++){
                    data22.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No2.get(i)/value_prefix));
                } 
                
                line_chart.getData().addAll(data21, data22);
                
                break;
                
            case 3:
                
                XYChart.Series data31 = new XYChart.Series<>();
                XYChart.Series data32 = new XYChart.Series<>();
                XYChart.Series data33 = new XYChart.Series<>();
                
                data31.nameProperty().bind(series1_name);
                data32.nameProperty().bind(series2_name);
                data33.nameProperty().bind(series3_name);
                
                for(int i = 0 ; i < parameters.size() ; i++){
                    data31.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters.get(i)/value_prefix));
                }        
                
                for(int i = 0 ; i < parameters_No2.size() ; i++){
                    data32.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No2.get(i)/value_prefix));
                } 
                
                for(int i = 0 ; i < parameters_No3.size() ; i++){
                    data33.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No3.get(i)/value_prefix));
                }
                
                line_chart.getData().addAll(data31, data32, data33);
                
                break;
                
            case 4:
                
                XYChart.Series data41 = new XYChart.Series<>();
                XYChart.Series data42 = new XYChart.Series<>();
                XYChart.Series data43 = new XYChart.Series<>();
                XYChart.Series data44 = new XYChart.Series<>();
                
                data41.nameProperty().bind(series1_name);
                data42.nameProperty().bind(series2_name);
                data43.nameProperty().bind(series3_name);
                data44.nameProperty().bind(series4_name);
                
                for(int i = 0 ; i < parameters.size() ; i++){
                    data41.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters.get(i)/value_prefix));
                }        
                
                for(int i = 0 ; i < parameters_No2.size() ; i++){
                    data42.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No2.get(i)/value_prefix));
                } 
                
                for(int i = 0 ; i < parameters_No3.size() ; i++){
                    data43.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No3.get(i)/value_prefix));
                }
                
                for(int i = 0 ; i < parameters_No4.size() ; i++){
                    data44.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No4.get(i)/value_prefix));
                }
                
                line_chart.getData().addAll(data41, data42, data43, data44);
                
                break;
                
            case 5:
                
                XYChart.Series data51 = new XYChart.Series<>();
                XYChart.Series data52 = new XYChart.Series<>();
                XYChart.Series data53 = new XYChart.Series<>();
                XYChart.Series data54 = new XYChart.Series<>();
                XYChart.Series data55 = new XYChart.Series<>();
                
                data51.nameProperty().bind(series1_name);
                data52.nameProperty().bind(series2_name);
                data53.nameProperty().bind(series3_name);
                data54.nameProperty().bind(series4_name);
                data55.nameProperty().bind(series5_name);
                
                for(int i = 0 ; i < parameters.size() ; i++){
                    data51.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters.get(i)/value_prefix));
                }        
                
                for(int i = 0 ; i < parameters_No2.size() ; i++){
                    data52.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No2.get(i)/value_prefix));
                } 
                
                for(int i = 0 ; i < parameters_No3.size() ; i++){
                    data53.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No3.get(i)/value_prefix));
                }
                
                for(int i = 0 ; i < parameters_No4.size() ; i++){
                    data54.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No4.get(i)/value_prefix));
                }
                
                for(int i = 0 ; i < parameters_No5.size() ; i++){
                    data55.getData().add(new XYChart.Data<>(time.get(i)/time_prefix,
                            parameters_No5.get(i)/value_prefix));
                }
                
                line_chart.getData().addAll(data51, data52, data53, data54, data55);
                
                break;
        }
    }
    
}
