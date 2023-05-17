/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author dmitryb
 */
public class RecCAScale {
    
    public double MIN;  //Min Value
    public double MAX;  //Max value
    
    private final static double RED_HUE = Color.RED.getHue() ;
    private final static double PURPLE_HUE = Color.PURPLE.getHue();
    
    public void setMinValueToScale(double minValue){
        this.MIN = minValue;
    }
    
    public double getMinValueForScale(){
        return MIN;
    }
    
    public void setMaxValueToScale(double maxValue){
        this.MAX = maxValue;
    }
    
    public double getMaxValueForScale(){
        return MAX;
    }
    
    
    public Color color;
    public double brightness;
    public boolean reverse;
     
    public void setReverse(boolean is_reverse){
        if(is_reverse) reverse = true;
        else reverse = false;
    }
    
    public Color getColorForScale(String scale_option, double value){
               
        if(!reverse){
            switch (scale_option){                
                case "rainbow":
                    if(value < MIN){
                        color = Color.WHITE;
                    }
                    else if(value > MAX){
                        color = Color.BLACK;
                    }
                    else{
                        double hue = PURPLE_HUE + (RED_HUE - PURPLE_HUE) * (value - MIN) / (MAX - MIN);
                        color = Color.hsb(hue, 1.0, 1.0);
                    }
                    break;
                
                case "gray":
                    if(value < MIN){
                        color = Color.WHITE;
                    }
                    else if(value > MAX){
                        color = Color.BLACK;
                    }
                    else{
                        double brightness = (value - MIN) / (MAX - MIN) ;
                        color = Color.hsb(120, 0 , 1 -brightness);
                    }
                    break;
                    
                case "4 colours":
                    if(value < MIN){
                        color = Color.WHITE;
                    }
                    else if(value > MAX){
                        color = Color.BLACK;
                    }
                    else{
                        if(value < (MAX - MIN)*0.25 && value > MIN){
                            brightness = 0.8;
                        }
                        else if(value < (MAX-MIN)*0.5 && value > (MAX - MIN)*0.25){
                            brightness = 0.6;
                        }
                        else if(value < (MAX-MIN)*0.75 && value > (MAX - MIN)*0.5){
                            brightness = 0.4;
                        }
                        else if(value < MAX && value > (MAX-MIN)*0.75){
                            brightness = 0.2;
                        }
                        color = Color.hsb(120, 0 , brightness);
                    }
                    break;
                    
                case "centered":
                    if(value < MIN){
                        color = Color.WHITE;
                    }
                    else if(value > MAX){
                        color = Color.BLACK;
                    }
                    else{
                        double center = (MAX - MIN)/2;
                        if(value < center){
                            brightness = 0.95 - 0.95 *(value - MIN) / (MAX - MIN) ;
                        }

                        if(value > center){
                            brightness = 0.95 *(value - MIN) / (MAX - MIN) ;
                        }
                        color = Color.hsb(120, 0, brightness);
                    }
                    break;
                    
                case "red/blue":
                    if(value > MAX)
                        color = Color.BLACK;
                    else if(value < MIN)
                        color = Color.WHITE;
                    else{
                        if(value > 0)
                            color = Color.RED;                        
                        else 
                            color = Color.BLUE;
                    }                    
                    break;    
            }
        }
        else{
            
            switch (scale_option){
                case "rainbow":
                    if(value < MIN){
                        color = Color.BLACK;
                    }
                    else if(value > MAX){
                        color = Color.WHITE;
                    }
                    else{
                        double hue = RED_HUE + (PURPLE_HUE - RED_HUE) * (value - MIN) / (MAX - MIN);
                        color = Color.hsb(hue, 1.0, 1.0);
                    }
                    break;
                    
                case "gray":
                    if(value < MIN){
                        color = Color.BLACK;
                    }
                    else if(value > MAX){
                        color = Color.WHITE;
                    }
                    break;
                case "4 colours":
                    if(value < MIN){
                        color = Color.BLACK;
                    }
                    else if(value > MAX){
                        color = Color.WHITE;
                    }
                    break;
                    
                case "centered":
                    if(value < MIN){
                        color = Color.BLACK;
                    }
                    else if(value > MAX){
                        color = Color.WHITE;
                    }
                    else{
                        double center = (MAX - MIN)/2;
                        if(value < center){
                            brightness = 0.95 - 0.95 *(value - MIN) / (MAX - MIN) ;
                        }

                        if(value > center){
                            brightness = 0.95 *(value - MIN) / (MAX - MIN) ;
                        }
                        color = Color.hsb(120, 0, brightness);
                    }
                    break;
                    
                case "red/blue":
                    if(value < 0){
                        color = Color.RED;
                    }
                    else color = Color.BLUE;
                    break;      
            }        
        }
        
        return color;
    }
    
    public int image_width;
    public int image_height;
    
    public void setImageWidth(int image_width){
        this.image_width = image_width;
    }
    
    public void setImageHeight(int image_height){
        this.image_height = image_height;
    }
    
    public WritableImage createScaleImage(String scale_option){
        WritableImage image = new WritableImage(image_width, image_height);
        double width = image.getWidth();
        double height = image.getHeight();
        PixelWriter pixelWriter = image.getPixelWriter();
        
        int y;        
        for(y = 0 ; y < 10 ; y++){
            for (int x=0; x<width; x++) {
                pixelWriter.setColor(x, y, Color.BLACK);
            }
        }
        for(y=10 ; y < (height-10) ; y++){
            double value = MAX - (MAX - MIN) * (y-10) / (height-10) ;
            Color color = getColorForScale(scale_option, value);
            for (int x=0; x<width; x++) {
                pixelWriter.setColor(x, y, color);
            }
        }
        for(y = ((int) (height - 10)) ; y < height ; y++){
            for (int x=0; x<width; x++) {
                pixelWriter.setColor(x, y, Color.WHITE);
            }
        }
        return image;
    }  
    
    public WritableImage createRedBlueScaleImage(){
        WritableImage image = new WritableImage(image_width, image_height);
        PixelWriter pixelWriter = image.getPixelWriter();
        int y;
        if(reverse){
            //Create Reverse Red/Blue Scale Image
            for(y=0 ; y < image_height/2 ; y++){
                for (int x=0; x<image_width; x++) {
                    pixelWriter.setColor(x, y, Color.RED);
                }
            }
            for(y=image_height/2 ; y < image_height ; y++){
                for (int x=0; x<image_width; x++) {
                    pixelWriter.setColor(x, y, Color.BLUE);
                }
            }
        }
        else {
            //Crete Red/Blue Scale Image
            for(y=0 ; y < image_height/2 ; y++){
                for (int x=0; x<image_width; x++) {
                    pixelWriter.setColor(x, y, Color.BLUE);
                }
            }
            for(y=image_height/2 ; y < image_height ; y++){
                for (int x=0; x<image_width; x++) {
                    pixelWriter.setColor(x, y, Color.RED);
                }
            }
        }
        return image;
    }
    
}