/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import interf.RecCAReadFiles;
import interf.TransferredDataBank;
import interf.UICommon;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import util.Common;
import util.Common_Win;

/**
 *
 * @author dmitryb
 */
public class RecCAChartFiles {
    
    TransferredDataBank trans_data_bank;
    String task_path, task_name;
    
    /**
     * Chart Files Data     
     */
    // Jocl Files
    //.res Files
    public List<Double> time_steps = new ArrayList<>();

    public List<Double> temperature = new ArrayList<>();
    public List<Double> specific_elastic_energy = new ArrayList<>();
    public List<Double> principal_stresses = new ArrayList<>();
    public List<Double> x_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> y_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> z_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> current_influx_of_specific_dissipated_energy = new ArrayList<>();
    public List<Double> specific_dissipated_energy = new ArrayList<>();

    //.relValues.res
    public List<Double> total_specific_energy = new ArrayList<>();
    public List<Double> abs_value_of_X_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_Y_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_Z_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_inst_spec_force_moment = new ArrayList<>();
    
    //jocl_torsion.res
    public List<Double> abs_value_of_angle_velocity = new ArrayList<>();
    public List<Double> abs_value_of_torsion_angle = new ArrayList<>();
    
    //jocl_stresses files
    public List<Double> abs_value_of_stress_vector = new ArrayList<>();
    public List<Double> value_for_crack_generation = new ArrayList<>();
    public List<Double> x_comp_of_stress_vector = new ArrayList<>();
    public List<Double> y_comp_of_stress_vector = new ArrayList<>();
    public List<Double> z_comp_of_stress_vector = new ArrayList<>();
    
    //Not Jocl Files
    //.res file
    public List<Double> effective_stresses = new ArrayList<>();
    public List<Double> strain = new ArrayList<>();
    public List<Double> total_specific_torsion_energy = new ArrayList<>();

    //inst_mom.res file
    public List<Double> x_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> y_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> z_component_of_inst_spec_force_moment = new ArrayList<>();

    //torsion.res file
    public List<Double> x_component_of_angle_velocity = new ArrayList<>();
    public List<Double> y_component_of_angle_velocity = new ArrayList<>();
    public List<Double> z_component_of_angle_velocity = new ArrayList<>();
    public List<Double> x_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> y_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> z_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> relative_power_of_torsion = new ArrayList<>();
    public List<Double> relative_torsion_energy = new ArrayList<>();
    
    public double time_step_value;
    
    String seca_path;
    
    public RecCAChartFiles(String task_name, File seca_path){
        System.out.println("RecCAChartFiles constructor: start");
        
        this.task_name = task_name;
        this.seca_path = seca_path.getAbsolutePath();
        
        System.out.println("Task Name: " + task_name);
        
        task_path = Common.TASK_PATH + "/" + task_name + "/";
        
        if(!isFilesExist()){
            try {
                createFiles();
            } catch (IOException ex) {
                System.out.println("Error: CAN NOT CREATE FILES FOR CHARTS");
            }
        }
    }
    
    public RecCAChartFiles(String task_name, String file_name){
        System.out.println("RecCAChartFiles constructor: start");
        this.task_name = task_name;
        
        try {
            readFile(file_name);
        } catch (IOException ex) {
            System.out.println("Error: can't read chart data file " + file_name);
        }
    }
    
    public boolean isFilesExist(){
        
        int count = 0;
        
        boolean relVal = false;
        boolean stress = false;
        boolean jocl = false;
        boolean inst_mom = false;
        boolean torsion = false;
        boolean chart = false;
        
        for(File file : new File(task_path).listFiles()){
            if(file.getName().endsWith("relValues.chart"))
                relVal = true;
            else if(file.getName().endsWith("jocl_stresses.chart"))
                stress = true;
            else if(file.getName().endsWith("jocl.chart"))
                jocl = true;
            else if(file.getName().endsWith("inst_mom.chart"))
                inst_mom = true;
            else if(file.getName().endsWith("torsion.chart"))
                torsion = true;
            else if(file.getName().endsWith(".chart"))
                chart = true;                
        }
        
        if(relVal && jocl)
            return true;
        
        if(jocl && stress)
            return true;
        
        if(chart && inst_mom && torsion)
            return true;
        
        return false;
    }
    
    boolean is_relValues, is_jocl_res, is_jocl_stresses, is_jocl_torsion, is_inst_mom, is_torsion, is_res;
    
    public void createFiles() throws IOException
    {
        time_step_value = getTimeStepValue(seca_path);
        
        System.out.println("TIME STEP VALUE = " + time_step_value);
        
        List<File> files = java.nio.file.Files.walk(Paths.get(task_path))
                                .filter(java.nio.file.Files::isRegularFile)
                                .map(Path::toFile)
                                .collect(Collectors.toList());
        Collections.sort(files);
        
        List<Double> time_steps_res           = new ArrayList<>();
        List<Double> time_steps_inst_mom_res  = new ArrayList<>();
        List<Double> time_steps_torsion_res   = new ArrayList<>();
        List<Double> time_steps_relValues_res = new ArrayList<>();
        
        List<Double> time_steps_jocl_res           = new ArrayList<>();
        List<Double> time_steps_jocl_stresses_res  = new ArrayList<>();
        List<Double> time_steps_jocl_torsion_res   = new ArrayList<>();
        
        files.forEach((f) -> 
        {   
            int value = 0;
            String file_name = f.getName();
            
            System.out.println("file_name: "+file_name);
            
            if(!file_name.contains("_jocl"))
            {
              if(file_name.endsWith(".res") & !file_name.endsWith("relValues.res") & !file_name.endsWith("torsion.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll(".res", "").replaceAll("_", ""));
                
                time_steps_res.add( value * time_step_value );
                time_steps.add( value * time_step_value );
              }      
              
              if(file_name.endsWith("torsion.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_torsion.res", "").replaceAll("_", ""));
                
                time_steps_torsion_res.add( value * time_step_value );
                time_steps.add( value * time_step_value );
              }
              
              if(file_name.endsWith("inst_mom.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_inst_mom.res", "").replaceAll("_", ""));
                
                time_steps_inst_mom_res.add( value * time_step_value );
                time_steps.add( value * time_step_value );
              }    
                
              if(file_name.endsWith("relValues.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_relValues.res", "").replaceAll("_", ""));
                
                time_steps_relValues_res.add( value * time_step_value );
                time_steps.add( value * time_step_value );
              } 
            }
            
            if(file_name.contains("_jocl"))
            {   
              if(file_name.endsWith("_jocl.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_jocl.res", "").replaceAll("_", ""));
                
                time_steps_jocl_res.add( value * time_step_value );
                time_steps.add( value * time_step_value );
              }
              
              if(file_name.endsWith("_jocl_stresses.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_jocl_stresses.res", "").replaceAll("_", ""));
                
                time_steps_jocl_stresses_res.add( value * time_step_value );
                time_steps.add( value * time_step_value );
              }   
              
              if(file_name.endsWith("_jocl_torsion.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_jocl_torsion.res", "").replaceAll("_", ""));
                
                time_steps_jocl_torsion_res.add( value * time_step_value );
                time_steps.add( value * time_step_value );
              }
            }
            
            System.out.println("time step: "+value);
                    
           /* 
            if(f.getName().endsWith("relValues.res"))
            {
                time_steps.add(time_step_value * Double.parseDouble(f.getName().substring(f.getName().length() - 19, f.getName().length() - 14)));
            }
                        
            if(f.getName().endsWith("inst_mom.res"))
            {
                time_steps.add(time_step_value * Double.parseDouble(f.getName().substring(f.getName().length() - 17, f.getName().length() - 12)));
            }
            
            
            if(f.getName().endsWith("jocl_stresses.res"))
            {                
                time_steps.add(time_step_value * Double.parseDouble(f.getName().substring(f.getName().length() - 23, f.getName().length() - 18)));
            }
            */
        });
        
        System.out.println("TIME STEPS : " + time_steps);
        
        for(File f : files)
        {            
            if(f.getName().endsWith("_relValues.res"))
            {
                is_relValues = true;
                
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), true);
                
                this.temperature.add(read.getAverageValue(read.relative_temperature));
                this.specific_elastic_energy.add(read.getAverageValue(read.relative_specific_elastic_energy));
                this.specific_dissipated_energy.add(read.getAverageValue(read.relative_spec_dissipated_energy));
                this.abs_value_of_X_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_X_comp_of_inst_spec_force_moment));
                this.abs_value_of_Y_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Y_comp_of_inst_spec_force_moment));
                this.abs_value_of_Z_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Z_comp_of_inst_spec_force_moment));
                this.abs_value_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_inst_spec_force_moment));
                this.total_specific_energy.add(read.getAverageValue(read.total_specific_energy));
            }
            else 
            if(f.getName().endsWith("jocl_stresses.res"))
            {
                is_jocl_stresses = true;
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), true);
                abs_value_of_stress_vector.add(read.getAverageValue(read.abs_value_of_stress_vector));
                value_for_crack_generation.add(read.getAverageValue(read.value_for_crack_generation));
                x_comp_of_stress_vector.add(read.getAverageValue(read.x_comp_of_stress_vector));
                y_comp_of_stress_vector.add(read.getAverageValue(read.y_comp_of_stress_vector));
                z_comp_of_stress_vector.add(read.getAverageValue(read.z_comp_of_stress_vector));
            }
            else 
            if (f.getName().endsWith("jocl_torsion.res"))
            {
                is_jocl_torsion = true;
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), true);
                x_component_of_angle_velocity.add(read.getAverageValue(read.x_component_of_angle_velocity));
                y_component_of_angle_velocity.add(read.getAverageValue(read.y_component_of_angle_velocity));
                z_component_of_angle_velocity.add(read.getAverageValue(read.z_component_of_angle_velocity));
                x_component_of_torsion_angle_vector.add(read.getAverageValue(read.x_component_of_torsion_angle_vector));
                y_component_of_torsion_angle_vector.add(read.getAverageValue(read.y_component_of_torsion_angle_vector));
                z_component_of_torsion_angle_vector.add(read.getAverageValue(read.z_component_of_torsion_angle_vector));
                abs_value_of_angle_velocity.add(read.getAverageValue(read.abs_value_of_angle_velocity));
                abs_value_of_torsion_angle.add(read.getAverageValue(read.abs_value_of_torsion_angle));
            }
            else 
            if(f.getName().endsWith("jocl.res"))
            {
                is_jocl_res = true;
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), true);
                this.temperature.add(read.getAverageValue(read.temperature));
                this.specific_elastic_energy.add(read.getAverageValue(read.specific_elastic_energy));
                this.principal_stresses.add(read.getAverageValue(read.principal_stresses));
                this.x_component_of_specific_force_moment.add(read.getAverageValue(read.x_component_of_specific_force_moment));
                this.y_component_of_specific_force_moment.add(read.getAverageValue(read.y_component_of_specific_force_moment));
                this.z_component_of_specific_force_moment.add(read.getAverageValue(read.z_component_of_specific_force_moment));
                this.current_influx_of_specific_dissipated_energy.add(read.getAverageValue(read.current_influx_of_spec_dissipated_energy));
                this.specific_dissipated_energy.add(read.getAverageValue(read.specific_dissipated_energy));
            }
            else 
            if(f.getName().endsWith("torsion.res"))
            {
                is_torsion = true;
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), true);
                this.x_component_of_angle_velocity.add(read.getAverageValue(read.x_component_of_angle_velocity));
                this.y_component_of_angle_velocity.add(read.getAverageValue(read.y_component_of_angle_velocity));
                this.z_component_of_angle_velocity.add(read.getAverageValue(read.z_component_of_angle_velocity));
                this.x_component_of_torsion_angle_vector.add(read.getAverageValue(read.x_component_of_torsion_angle_vector));
                this.y_component_of_torsion_angle_vector.add(read.getAverageValue(read.y_component_of_torsion_angle_vector));
                this.z_component_of_torsion_angle_vector.add(read.getAverageValue(read.z_component_of_torsion_angle_vector));
                this.relative_power_of_torsion.add(read.getAverageValue(read.relative_power_of_torsion));
                this.relative_torsion_energy.add(read.getAverageValue(read.relative_torsion_energy));
                this.total_specific_energy.add(read.getAverageValue(read.total_specific_energy));
            }
            else 
            if(f.getName().endsWith("inst_mom.res"))
            {
                is_inst_mom = true;
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), true);
                this.abs_value_of_X_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_X_comp_of_inst_spec_force_moment));
                this.abs_value_of_Y_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Y_comp_of_inst_spec_force_moment));
                this.abs_value_of_Z_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Z_comp_of_inst_spec_force_moment));
                this.x_component_of_inst_spec_force_moment.add(read.getAverageValue(read.x_component_of_inst_spec_force_moment));
                this.y_component_of_inst_spec_force_moment.add(read.getAverageValue(read.y_component_of_inst_spec_force_moment));
                this.z_component_of_inst_spec_force_moment.add(read.getAverageValue(read.z_component_of_inst_spec_force_moment));
                this.abs_value_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_inst_spec_force_moment));
                this.current_influx_of_specific_dissipated_energy.add(read.getAverageValue(read.current_influx_of_torsion_energy));
            }
            else 
            if(f.getName().endsWith(".res"))
            {
                is_res = true;
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), true);
                this.temperature.add(read.getAverageValue(read.temperature));
                this.effective_stresses.add(read.getAverageValue(read.effective_stresses));
                this.principal_stresses.add(read.getAverageValue(read.principal_stresses));
                this.x_component_of_specific_force_moment.add(read.getAverageValue(read.x_component_of_specific_force_moment));
                this.y_component_of_specific_force_moment.add(read.getAverageValue(read.y_component_of_specific_force_moment));
                this.z_component_of_specific_force_moment.add(read.getAverageValue(read.z_component_of_specific_force_moment));
                this.strain.add(read.getAverageValue(read.strain));
                this.total_specific_torsion_energy.add(read.getAverageValue(read.total_specific_torsion_energy));
            }
        }
        
        if(is_relValues)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + task_name + "_relValues.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
         //   time_steps = time_steps_relValues_res;
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.REL_VALUES_CHART_FILE_STRING);
            bw.newLine();
            
            for (int i = 0 ; i < this.temperature.size() ; i++)
            {
                bw.write(
                        time_steps.get(i) + " " + 
                        temperature.get(i) + " " + 
                        specific_elastic_energy.get(i) + " " + 
                        total_specific_energy.get(i) + " " + 
                        abs_value_of_X_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Y_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Z_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_inst_spec_force_moment.get(i) + " " + 
                        specific_dissipated_energy.get(i)
                );
                bw.newLine();
            }
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");        
        }
        
        if(is_jocl_stresses)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + task_name + "_jocl_stresses.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
          //  time_steps = time_steps_jocl_stresses_res;
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.JOCL_STRESSES_CHART_FILE_STRING);
            bw.newLine();
            
            System.out.println("Temp size = " + temperature.size());
            System.out.println("time_steps size = " + time_steps.size());
            
            for(int i = 0 ; i < temperature.size(); i ++)
            {
                bw.write(
                        time_steps.get(i) + " " + 
                        temperature.get(i) + " " + 
                        abs_value_of_stress_vector.get(i) + " " + 
                        value_for_crack_generation.get(i) + " " + 
                        x_comp_of_stress_vector.get(i) + " " + 
                        y_comp_of_stress_vector.get(i) + " " + 
                        z_comp_of_stress_vector.get(i) + " " + 
                        current_influx_of_specific_dissipated_energy.get(i) + " " + 
                        specific_dissipated_energy.get(i)
                );
                bw.newLine();
            }
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");
        }
        
        if(is_jocl_torsion)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + task_name + "_jocl_torsion.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
          //  time_steps = time_steps_jocl_torsion_res;
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.JOCL_TORSION_CHART_FILE_STRING);
            bw.newLine();
            
            for (int i = 0 ; i < this.temperature.size() ; i++){
                bw.write(
                        time_steps.get(i) + " " + 
                        x_component_of_angle_velocity.get(i) + " " + 
                        y_component_of_angle_velocity.get(i) + " " + 
                        z_component_of_angle_velocity.get(i) + " " + 
                        x_component_of_torsion_angle_vector.get(i) + " " + 
                        y_component_of_torsion_angle_vector.get(i) + " " + 
                        z_component_of_torsion_angle_vector.get(i) + " " + 
                        abs_value_of_angle_velocity.get(i) + " " + 
                        abs_value_of_torsion_angle.get(i)
                );
                bw.newLine();
            }       
            
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");
        }
        
        if(is_jocl_res)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + task_name + "_jocl.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
          //  time_steps = time_steps_jocl_res;
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.JOCL_CHART_FILE_STRING);
            bw.newLine();
            
            for (int i = 0 ; i < this.temperature.size() ; i++)
            {
                bw.write(
                        time_steps.get(i) + " " + 
                        temperature.get(i) + " " + 
                        specific_elastic_energy.get(i) + " " + 
                        principal_stresses.get(i) + " " + 
                        x_component_of_specific_force_moment.get(i) + " " + 
                        y_component_of_specific_force_moment.get(i) + " " + 
                        z_component_of_specific_force_moment.get(i) + " " + 
                        current_influx_of_specific_dissipated_energy.get(i) + " " + 
                        specific_dissipated_energy.get(i)
                );
                bw.newLine();
            }       
            
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");
        }
        
        if(is_inst_mom)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + task_name + "_inst_mom.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
         //   time_steps = time_steps_inst_mom_res;
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.INST_MOM_CHART_FILE_STRING);
            bw.newLine();
            
            for (int i = 0 ; i < this.abs_value_of_X_comp_of_inst_spec_force_moment.size() ; i++)
            {
                bw.write(
                        time_steps.get(i) + " " + 
                        abs_value_of_X_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Y_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Z_comp_of_inst_spec_force_moment.get(i) + " " + 
                        x_component_of_inst_spec_force_moment.get(i) + " " + 
                        y_component_of_inst_spec_force_moment.get(i) + " " + 
                        z_component_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_inst_spec_force_moment.get(i) + " " + 
                        current_influx_of_specific_dissipated_energy.get(i)
                );
                bw.newLine();
            }        
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");
        }
        
        if(is_torsion)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + task_name + "_torsion.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
         //   time_steps = time_steps_torsion_res;
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.TORSION_CHART_FILE_STRING);
            bw.newLine();
            
            for (int i = 0 ; i < this.x_component_of_angle_velocity.size() ; i++)
            {
                bw.write(
                        time_steps.get(i) + " " + 
                        x_component_of_angle_velocity.get(i) + " " + 
                        y_component_of_angle_velocity.get(i) + " " + 
                        z_component_of_angle_velocity.get(i) + " " + 
                        x_component_of_torsion_angle_vector.get(i) + " " + 
                        y_component_of_torsion_angle_vector.get(i) + " " + 
                        z_component_of_torsion_angle_vector.get(i) + " " + 
                        relative_power_of_torsion.get(i) + " " + 
                        relative_torsion_energy.get(i) + " " +
                        total_specific_energy.get(i)
                );
                bw.newLine();
            }        
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");            
        }
        
        if(is_res)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + task_name + ".chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
          //  time_steps = time_steps_res;
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.RES_CHART_FILE_STRING);
            bw.newLine();
            
            for (int i = 0 ; i < this.temperature.size() ; i++)
            {
                bw.write(
                        time_steps.get(i) + " " + 
                        temperature.get(i) + " " + 
                        effective_stresses.get(i) + " " + 
                        principal_stresses.get(i) + " " + 
                        x_component_of_specific_force_moment.get(i) + " " + 
                        y_component_of_specific_force_moment.get(i) + " " + 
                        z_component_of_specific_force_moment.get(i) + " " + 
                        strain.get(i) + " " + 
                        total_specific_torsion_energy.get(i)
                );
                bw.newLine();
            }        
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");            
        }
        new Alert(AlertType.INFORMATION, "Files are created!!!").showAndWait();
    }
    
    public double getTimeStepValue(String seca_path) throws IOException
    {
        Path path = Paths.get(seca_path);
        
        for(String line : Files.readAllLines(path))
        {
            if(line.startsWith("time_step"))
                return Double.parseDouble(line.replaceAll("time_step = ", ""));
        }
        
        return 0.0;
    }
        
    public void clearAllLists()
    {
        time_steps.clear();
        temperature.clear();
        specific_elastic_energy.clear();
        principal_stresses.clear();
        x_component_of_specific_force_moment.clear();
        y_component_of_specific_force_moment.clear();
        z_component_of_specific_force_moment.clear();
        current_influx_of_specific_dissipated_energy.clear();
        specific_dissipated_energy.clear();
        temperature.clear();
        specific_elastic_energy.clear();
        abs_value_of_X_comp_of_inst_spec_force_moment.clear();
        abs_value_of_Y_comp_of_inst_spec_force_moment.clear();
        abs_value_of_Z_comp_of_inst_spec_force_moment.clear();
        abs_value_of_inst_spec_force_moment.clear();
        specific_dissipated_energy.clear();
        effective_stresses.clear();
        strain.clear();
        total_specific_torsion_energy.clear();
        x_component_of_inst_spec_force_moment.clear();
        y_component_of_inst_spec_force_moment.clear();
        z_component_of_inst_spec_force_moment.clear();
        current_influx_of_specific_dissipated_energy.clear();
        x_component_of_angle_velocity.clear();
        y_component_of_angle_velocity.clear();
        z_component_of_angle_velocity.clear();
        x_component_of_torsion_angle_vector.clear();
        y_component_of_torsion_angle_vector.clear();
        z_component_of_torsion_angle_vector.clear();
        relative_power_of_torsion.clear();
        relative_torsion_energy.clear();
        abs_value_of_stress_vector.clear();
        value_for_crack_generation.clear();
        x_comp_of_stress_vector.clear();
        y_comp_of_stress_vector.clear();
        z_comp_of_stress_vector.clear();
        abs_value_of_angle_velocity.clear();
        abs_value_of_torsion_angle.clear();
    }
    
    public void readFile(String file) throws IOException
    {
        System.out.println("!!!---- READ : " + file + " -----!!!");
        
        clearAllLists();
        
        Path path = Paths.get(Common.TASK_PATH + "/" + task_name + "/" + file);
        
        if(file.endsWith("relValues.chart"))
        {
            for(String line : Files.readAllLines(path)){
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    time_steps.add(Double.parseDouble(items[0]));
                    temperature.add(Double.parseDouble(items[1]));
                    specific_elastic_energy.add(Double.parseDouble(items[2]));
                    total_specific_energy.add(Double.parseDouble(items[3]));
                    abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[4]));
                    abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[5]));
                    abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[6]));
                    abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[7]));
                    specific_dissipated_energy.add(Double.parseDouble(items[8]));
                }
            }
        }
        else 
        if(file.endsWith("jocl_stresses.chart"))
        {
            for(String line : Files.readAllLines(path)){
                if(!line.startsWith("#") && !line.equals(null)){
                    String[] items = line.split(" ");
                    time_steps.add(Double.parseDouble(items[0]));
                    temperature.add(Double.parseDouble(items[1]));
                    abs_value_of_stress_vector.add(Double.parseDouble(items[2]));
                    value_for_crack_generation.add(Double.parseDouble(items[3]));
                    x_comp_of_stress_vector.add(Double.parseDouble(items[4]));
                    y_comp_of_stress_vector.add(Double.parseDouble(items[5]));
                    z_comp_of_stress_vector.add(Double.parseDouble(items[6]));
                    current_influx_of_specific_dissipated_energy.add(Double.parseDouble(items[7]));
                    specific_dissipated_energy.add(Double.parseDouble(items[8]));
                }
            }
        }
        else 
        if(file.endsWith("jocl_torsion.chart"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    time_steps.add(Double.parseDouble(items[0]));
                    x_component_of_angle_velocity.add(Double.parseDouble(items[1]));
                    y_component_of_angle_velocity.add(Double.parseDouble(items[2]));
                    z_component_of_angle_velocity.add(Double.parseDouble(items[3]));
                    x_component_of_torsion_angle_vector.add(Double.parseDouble(items[4]));
                    y_component_of_torsion_angle_vector.add(Double.parseDouble(items[5]));
                    z_component_of_torsion_angle_vector.add(Double.parseDouble(items[6]));
                    abs_value_of_angle_velocity.add(Double.parseDouble(items[7]));
                    abs_value_of_torsion_angle.add(Double.parseDouble(items[8]));
                }
            }
        }
        else 
        if(file.endsWith("jocl.chart"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    time_steps.add(Double.parseDouble(items[0]));
                    temperature.add(Double.parseDouble(items[1]));
                    specific_elastic_energy.add(Double.parseDouble(items[2]));
                    principal_stresses.add(Double.parseDouble(items[3]));
                    x_component_of_specific_force_moment.add(Double.parseDouble(items[4]));
                    y_component_of_specific_force_moment.add(Double.parseDouble(items[5]));
                    z_component_of_specific_force_moment.add(Double.parseDouble(items[6]));
                    current_influx_of_specific_dissipated_energy.add(Double.parseDouble(items[7]));
                    specific_dissipated_energy.add(Double.parseDouble(items[8]));
                }
            }
        }
        else 
        if(file.endsWith("inst_mom.chart"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    time_steps.add(Double.parseDouble(items[0]));
                    abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[1]));
                    abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[2]));
                    abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[3]));
                    x_component_of_inst_spec_force_moment.add(Double.parseDouble(items[4]));
                    y_component_of_inst_spec_force_moment.add(Double.parseDouble(items[5]));
                    z_component_of_inst_spec_force_moment.add(Double.parseDouble(items[6]));
                    abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[7]));
                    current_influx_of_specific_dissipated_energy.add(Double.parseDouble(items[8]));
                }
            }
        }
        else 
        if(!file.contains("jocl") && file.endsWith("torsion.chart"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    time_steps.add(Double.parseDouble(items[0]));
                    x_component_of_angle_velocity.add(Double.parseDouble(items[1]));
                    y_component_of_angle_velocity.add(Double.parseDouble(items[2]));
                    z_component_of_angle_velocity.add(Double.parseDouble(items[3]));
                    x_component_of_torsion_angle_vector.add(Double.parseDouble(items[4]));
                    y_component_of_torsion_angle_vector.add(Double.parseDouble(items[5]));
                    z_component_of_torsion_angle_vector.add(Double.parseDouble(items[6]));
                    relative_power_of_torsion.add(Double.parseDouble(items[7]));
                    relative_torsion_energy.add(Double.parseDouble(items[8]));
                    total_specific_energy.add(Double.parseDouble(items[9]));
                }
            }
        }
        else if(file.endsWith("chart"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    time_steps.add(Double.parseDouble(items[0]));
                    temperature.add(Double.parseDouble(items[1]));
                    effective_stresses.add(Double.parseDouble(items[2]));
                    principal_stresses.add(Double.parseDouble(items[3]));
                    x_component_of_specific_force_moment.add(Double.parseDouble(items[4]));
                    y_component_of_specific_force_moment.add(Double.parseDouble(items[5]));
                    z_component_of_specific_force_moment.add(Double.parseDouble(items[6]));
                    strain.add(Double.parseDouble(items[7]));
                    total_specific_torsion_energy.add(Double.parseDouble(items[8]));
                }
            }
        }
        
        System.out.println("!!!----  DONE ----!!!");
    }        
}
