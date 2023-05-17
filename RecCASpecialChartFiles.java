/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.charts;

import interf.RecCAReadFiles;
import interf.UICommon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import util.Common;

/**
 *
 * @author Ear
 */
public class RecCASpecialChartFiles 
{
    public List<Double> time_steps;
    
    //relValues
    public List<Double> relative_temperature = new ArrayList<>();
    public List<Double> relative_specific_elastic_energy = new ArrayList<>();
    public List<Double> total_specific_energy = new ArrayList<>();
    public List<Double> abs_value_of_X_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_Y_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_Z_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> relative_specific_dissipated_energy = new ArrayList<>();
    
    //jocl_stresses.res
    public List<Double> abs_value_of_stress_vector = new ArrayList<>();
    public List<Double> value_for_crack_generation = new ArrayList<>();
    public List<Double> x_comp_of_stress_vector = new ArrayList<>();
    public List<Double> y_comp_of_stress_vector = new ArrayList<>();
    public List<Double> z_comp_of_stress_vector = new ArrayList<>();
    
    //jocl res
    public List<Double> temperature = new ArrayList<>();
    public List<Double> specific_elastic_energy = new ArrayList<>();
    public List<Double> principal_stresses = new ArrayList<>();
    public List<Double> x_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> y_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> z_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> current_influx_of_specific_dissipated_energy = new ArrayList<>();
    public List<Double> specific_dissipated_energy = new ArrayList<>();

    //Not Jocl Files
    //.res file
    public List<Double> effective_stresses = new ArrayList<>();
    public List<Double> strain = new ArrayList<>();
    public List<Double> total_specific_torsion_energy = new ArrayList<>();

    //inst_mom.res file
    public List<Double> x_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> y_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> z_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> current_influx_of_torsion_energy = new ArrayList<>();

    //torsion.res file
    public List<Double> x_component_of_angle_velocity = new ArrayList<>();
    public List<Double> y_component_of_angle_velocity = new ArrayList<>();
    public List<Double> z_component_of_angle_velocity = new ArrayList<>();
    public List<Double> x_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> y_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> z_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> relative_power_of_torsion = new ArrayList<>();
    public List<Double> relative_torsion_energy = new ArrayList<>();
    
    double x_init, y_init, z_init, x_max, y_max, z_max;
    
    boolean is_relValues, is_jocl_res, is_jocl_stresses, is_inst_mom, is_torsion, is_res;
    
    String task_name;
    
    public RecCASpecialChartFiles(String task_name, String file_name, double x, double y, double z,
                                  double x_length, double y_length, double z_length) throws IOException
    {
        System.out.println("RecCASpecialChartFiles");
        
        this.task_name = task_name;
        
        time_steps = getTimeSteps();
        
        this.x_init = x;
        this.y_init = y;
        this.z_init = z;
        
        System.out.println("RecCASpecialChartFiles: x(0) = " + x_init);
        System.out.println("RecCASpecialChartFiles: y(0) = " + y_init);
        System.out.println("RecCASpecialChartFiles: z(0) = " + z_init);
        
        this.x_max = x_init + x_length;
        this.y_max = x_init + y_length;
        this.z_max = x_init + z_length;
        
        System.out.println("RecCASpecialChartFiles: x(max) = " + x_max);
        System.out.println("RecCASpecialChartFiles: y(max) = " + y_max);
        System.out.println("RecCASpecialChartFiles: z(max) = " + z_max);
        
        List<File> files = java.nio.file.Files.walk(Paths.get(System.getProperty("user.dir") + "/user/task_db/" + task_name + "/"))
                                .filter(java.nio.file.Files::isRegularFile)
                                .map(Path::toFile)
                                .collect(Collectors.toList());
        Collections.sort(files);
        
        int file_type_relValues = 0;
        int file_type_jocl_stresses = 0;
        int file_type_jocl_res = 0;
        int file_type_torsion = 0;
        int file_type_inst_mom_res = 0;
        int file_type_res = 0;
        
        for(File f : files)
        {            
            if(f.getName().endsWith("_relValues.res"))
            {
                file_type_relValues = 1;
                is_relValues = true;
                
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), x_init, y_init, z_init, x_length, y_length, z_length);
                
                this.relative_temperature.add(read.getAverageValue(read.relative_temperature));
                this.relative_specific_elastic_energy.add(read.getAverageValue(read.relative_specific_elastic_energy));
                this.relative_specific_dissipated_energy.add(read.getAverageValue(read.relative_spec_dissipated_energy));
                this.abs_value_of_X_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_X_comp_of_inst_spec_force_moment));
                this.abs_value_of_Y_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Y_comp_of_inst_spec_force_moment));
                this.abs_value_of_Z_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Z_comp_of_inst_spec_force_moment));
                this.abs_value_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_inst_spec_force_moment));
                this.total_specific_energy.add(read.getAverageValue(read.total_specific_energy));
            }
            else
            if(f.getName().endsWith("jocl_stresses.res"))
            {
                file_type_jocl_stresses = 1;
                is_jocl_stresses = true;
                
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), x_init, y_init, z_init, x_length, y_length, z_length);
                
                abs_value_of_stress_vector.add(read.getAverageValue(read.abs_value_of_stress_vector));
                value_for_crack_generation.add(read.getAverageValue(read.value_for_crack_generation));
                x_comp_of_stress_vector.add(read.getAverageValue(read.x_comp_of_stress_vector));
                y_comp_of_stress_vector.add(read.getAverageValue(read.y_comp_of_stress_vector));
                z_comp_of_stress_vector.add(read.getAverageValue(read.z_comp_of_stress_vector));                
            }
            else 
            if(f.getName().endsWith("jocl.res"))
            {
                file_type_jocl_res = 1;
                is_jocl_res = true;
                
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), x_init, y_init, z_init, x_length, y_length, z_length);
                
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
                file_type_torsion = 1;
                is_torsion = true;
                
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), x_init, y_init, z_init, x_length, y_length, z_length);
                
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
                file_type_inst_mom_res = 1;
                is_inst_mom = true;
                
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), x_init, y_init, z_init, x_length, y_length, z_length);
                
                this.abs_value_of_X_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_X_comp_of_inst_spec_force_moment));
                this.abs_value_of_Y_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Y_comp_of_inst_spec_force_moment));
                this.abs_value_of_Z_comp_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_Z_comp_of_inst_spec_force_moment));
                this.x_component_of_inst_spec_force_moment.add(read.getAverageValue(read.x_component_of_inst_spec_force_moment));
                this.y_component_of_inst_spec_force_moment.add(read.getAverageValue(read.y_component_of_inst_spec_force_moment));
                this.z_component_of_inst_spec_force_moment.add(read.getAverageValue(read.z_component_of_inst_spec_force_moment));
                this.abs_value_of_inst_spec_force_moment.add(read.getAverageValue(read.abs_value_of_inst_spec_force_moment));
                this.current_influx_of_torsion_energy.add(read.getAverageValue(read.current_influx_of_torsion_energy));
            }
            else 
            if(f.getName().endsWith(".res"))
            {
                file_type_res = 1;
                is_res = true;
                
                RecCAReadFiles read = new RecCAReadFiles(f.getAbsolutePath(), x_init, y_init, z_init, x_length, y_length, z_length);
                
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
                
        int file_type_number = 
        file_type_relValues+ file_type_jocl_stresses+ file_type_jocl_res+ file_type_torsion+ file_type_inst_mom_res+ file_type_res;
        
        if(is_relValues)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + file_name + "_relValues.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.REL_VALUES_CHART_FILE_STRING);
            bw.newLine();
            
          /*
            bw.write("# X initial = " + x_init + "\n");
            bw.write("# X max = " + x_max + "\n");
            bw.write("# Y initial = " + y_init + "\n");
            bw.write("# Y max = " + y_max + "\n");
            bw.write("# Z initial = " + z_init + "\n");
            bw.write("# Z max = " + z_max + "\n");
          */  
            for (int i = 0 ; i < this.time_steps.size()/file_type_number ; i++)
            {
                bw.write(
                        time_steps.get(file_type_number*i) + " " +
                        relative_temperature.get(i) + " " + 
                        relative_specific_elastic_energy.get(i) + " " + 
                        total_specific_energy.get(i) + " " + 
                        abs_value_of_X_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Y_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Z_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_inst_spec_force_moment.get(i) + " " + 
                        relative_specific_dissipated_energy.get(i)
                );
                bw.newLine();
            }
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");        
        }
        
        if(is_jocl_stresses)
        {
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + file_name + "_jocl_stresses.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists())
            {
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.JOCL_STRESSES_CHART_FILE_STRING);
            bw.newLine();
            
          /*
            bw.write("# X initial = " + x_init + "\n");
            bw.write("# X max = " + x_max + "\n");
            bw.write("# Y initial = " + y_init + "\n");
            bw.write("# Y max = " + y_max + "\n");
            bw.write("# Z initial = " + z_init + "\n");
            bw.write("# Z max = " + z_max + "\n");
          */
            
            for(int i = 0 ; i < time_steps.size()/file_type_number; i ++)
            {
                bw.write(
                        time_steps.get(file_type_number*i) + " " +
                        temperature.get(i) + " " + 
                        abs_value_of_stress_vector.get(i) + " " + 
                        value_for_crack_generation.get(i) + " " + 
                        x_comp_of_stress_vector.get(i) + " " + 
                        y_comp_of_stress_vector.get(i) + " " + 
                        z_comp_of_stress_vector.get(i) + " " + 
                        current_influx_of_specific_dissipated_energy.get(i) + " " + 
                        specific_dissipated_energy.get(i)
                );
                
            //    bw.write(" " +temperature.size() +" "+time_steps.size());
                bw.newLine();
            }
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");
        }
        
        if(is_jocl_res){
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + file_name + "_jocl.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.JOCL_CHART_FILE_STRING);
            bw.newLine();
         /*
            bw.write("# X initial = " + x_init + "\n");
            bw.write("# X max = " + x_max + "\n");
            bw.write("# Y initial = " + y_init + "\n");
            bw.write("# Y max = " + y_max + "\n");
            bw.write("# Z initial = " + z_init + "\n");
            bw.write("# Z max = " + z_max + "\n");
          */
            for (int i = 0 ; i < this.time_steps.size()/file_type_number; i++){
                bw.write(
                        time_steps.get(file_type_number*i) + " " +
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
        
        if(is_inst_mom){
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + file_name + "_inst_mom.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.INST_MOM_CHART_FILE_STRING);
            bw.newLine();
          /*
            bw.write("# X initial = " + x_init + "\n");
            bw.write("# X max = " + x_max + "\n");
            bw.write("# Y initial = " + y_init + "\n");
            bw.write("# Y max = " + y_max + "\n");
            bw.write("# Z initial = " + z_init + "\n");
            bw.write("# Z max = " + z_max + "\n");
          */
            for (int i = 0 ; i < this.time_steps.size()/file_type_number ; i++){
                bw.write(
                        time_steps.get(file_type_number*i) + " " +
                        abs_value_of_X_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Y_comp_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_Z_comp_of_inst_spec_force_moment.get(i) + " " + 
                        x_component_of_inst_spec_force_moment.get(i) + " " + 
                        y_component_of_inst_spec_force_moment.get(i) + " " + 
                        z_component_of_inst_spec_force_moment.get(i) + " " + 
                        abs_value_of_inst_spec_force_moment.get(i) + " " + 
                        current_influx_of_torsion_energy.get(i)
                );
                bw.newLine();
            }        
            bw.close();
            
            System.out.println("!!!!--- FILE ("+ file.getName() +") IS CREATED ---!!!!");
        }
        
        if(is_torsion){
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + file_name + "_torsion.chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.TORSION_CHART_FILE_STRING);
            bw.newLine();
            
          /*
            bw.write("# X initial = " + x_init + "\n");
            bw.write("# X max = " + x_max + "\n");
            bw.write("# Y initial = " + y_init + "\n");
            bw.write("# Y max = " + y_max + "\n");
            bw.write("# Z initial = " + z_init + "\n");
            bw.write("# Z max = " + z_max + "\n");
          */
            for (int i = 0 ; i < this.time_steps.size()/file_type_number; i++){
                bw.write(
                        time_steps.get(file_type_number*i) + " " +
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
        
        if(is_res){
            File file = new File(Common.TASK_PATH + "/" + task_name + "/" + file_name + ".chart");
            
            System.out.println("!!!!--- CREATE FILE : " + file.getName() + " ---!!!!");
            
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            else file.createNewFile();
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            bw.write(UICommon.RES_CHART_FILE_STRING);
            bw.newLine();
            
          /*
            bw.write("# X initial = " + x_init + "\n");
            bw.write("# X max = " + x_max + "\n");
            bw.write("# Y initial = " + y_init + "\n");
            bw.write("# Y max = " + y_max + "\n");
            bw.write("# Z initial = " + z_init + "\n");
            bw.write("# Z max = " + z_max + "\n");
          */
            for (int i = 0 ; i < this.time_steps.size()/file_type_number ; i++)
            {
                bw.write(
                        time_steps.get(file_type_number*i) + " " +
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
        
    }
    
    private double getTimeStepValue() throws FileNotFoundException, IOException{
        String path = "./user/task_db/" + task_name + ".seca";
        
        Properties props = new Properties();
        
        FileInputStream stream = new FileInputStream(path);
        props.load(stream);
        stream.close();
        
        double value = Double.valueOf(props.getProperty("time_step")).doubleValue();
        
        return value;
    }
    
    private List<Double> getTimeSteps() throws IOException
    {
        double time_step = getTimeStepValue();
        
        List<Double> list = new ArrayList<>();
      /* 
        List<Double> list_res           = new ArrayList<>();
        List<Double> list_relValues_res = new ArrayList<>();
        List<Double> list_torsion_res   = new ArrayList<>();
        
        List<Double> list_jocl_res          = new ArrayList<>();
        List<Double> list_jocl_stresses_res = new ArrayList<>();
        List<Double> list_jocl_torsion_res  = new ArrayList<>();
      */
        String task_path = "./user/task_db/" + task_name + "/";
        
        List<File> files = java.nio.file.Files.walk(Paths.get(task_path))
                                .filter(java.nio.file.Files::isRegularFile)
                                .map(Path::toFile)
                                .collect(Collectors.toList());
        Collections.sort(files);
        
        for(File f : files)
        {            
            int value = 0;
            String file_name = f.getName();
            
            if(!file_name.contains("_jocl"))
            {
              if(file_name.contains(".res") & !file_name.contains("relValues.res") & !file_name.contains("torsion.res") & !file_name.contains("inst_mom.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll(".res", "").replaceAll("_", ""));
                list.add( value * time_step );
              }      
                
              if(file_name.contains("relValues.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_relValues.res", "").replaceAll("_", ""));
                list.add( value * time_step );
              }         
             
              if(file_name.contains("torsion.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_torsion.res", "").replaceAll("_", ""));
                list.add( value * time_step );
              }
              
              if(file_name.contains("inst_mom.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_inst_mom.res", "").replaceAll("_", ""));
                list.add( value * time_step );
              }
            }
            
            if(file_name.contains("_jocl"))
            {   
              if(file_name.contains("_jocl.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_jocl.res", "").replaceAll("_", ""));
                list.add( value * time_step );
              }
            
              if(file_name.contains("_jocl_stresses.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_jocl_stresses.res", "").replaceAll("_", ""));
                list.add( value * time_step );
              }   
            
              if(file_name.contains("_jocl_torsion.res"))
              {
                value = Integer.parseInt(f.getName().replaceAll(task_name, "").replaceAll("_jocl_torsion.res", "").replaceAll("_", ""));
                list.add( value * time_step );
              }
            }
        }
        
        System.out.println("RecCASpecialChartFiles.getTimeSteps(): TIME STEPS: " + list);
        
        return list;
    }    
}
