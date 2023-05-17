/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;
import util.Common;

/**
 *
 * @author dmitryb
 */
public class RecCAReadFiles {
    
    // .res Files 
    /*
        # Each string contains parameters of corresponding cell: 
        # 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 
        # 6. temperature; 7. specific elastic energy; 8. principal stress; 
        # 9-11. 3 components of specific volume force moment vector calculated using neighbour cells at 1st sphere only; 
        # 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 
        # 13. current influx of specific dissipated energy; 14. specific dissipated energy.
    */
    public List<Integer> energy_type = new ArrayList<>();
    public List<Integer> location_type = new ArrayList<>(); 
    public List<Integer> grain_index = new ArrayList<>();    
    public List<Double> x_coordinate = new ArrayList<>();
    public List<Double> y_coordinate = new ArrayList<>();
    public List<Double> z_coordinate = new ArrayList<>();    
    public List<Double> temperature = new ArrayList<>();
    public List<Double> specific_elastic_energy = new ArrayList<>();
    public List<Double> principal_stresses = new ArrayList<>();
    public List<Double> x_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> y_component_of_specific_force_moment = new ArrayList<>();
    public List<Double> z_component_of_specific_force_moment = new ArrayList<>();    
    public List<Integer> state_of_material = new ArrayList<>();        
    public List<Double> current_influx_of_spec_dissipated_energy = new ArrayList<>();
    public List<Double> specific_dissipated_energy = new ArrayList<>();
    
    // relValues.res Files
    /*
        # Each string contains parameters of corresponding cell: 
        # 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 
        # 6. temperature; 7. relative specific elastic energy; 8. total specific energy; 
        # 9-11. absolute values of 3 components of specific force moment calculated for the 1st coordination sphere; 
        # 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 
        # 13. absolute value of specific force moment; 14. relative specific dissipated energy. 
    */
    public List<Double> relative_temperature = new ArrayList<>();
    public List<Double> relative_specific_elastic_energy = new ArrayList<>();
    public List<Double > total_specific_energy = new ArrayList<>();
    
    public List<Double> abs_value_of_X_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_Y_comp_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> abs_value_of_Z_comp_of_inst_spec_force_moment = new ArrayList<>();
        
    public List<Double> abs_value_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> relative_spec_dissipated_energy = new ArrayList<>(); 
    
    //jocl_stresses.res files
    /*
    # Each string contains parameters of corresponding cell: 
    # 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 
    # 6. temperature; 7. absolute value of stress vector; 8. value for crack generation; 
    # 9-11. 3 components of stress vector; 
    # 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 
    # 13. current influx of specific dissipated energy; 14. specific dissipated energy.
    */
    public List<Double> abs_value_of_stress_vector = new ArrayList<>();
    public List<Double> value_for_crack_generation = new ArrayList<>();
    public List<Double> x_comp_of_stress_vector = new ArrayList<>();
    public List<Double> y_comp_of_stress_vector = new ArrayList<>();
    public List<Double> z_comp_of_stress_vector = new ArrayList<>();
    
    // jocl_torsion.res files
    public List<Double> abs_value_of_angle_velocity = new ArrayList<>();
    public List<Double> abs_value_of_torsion_angle = new ArrayList<>();
    
    //.seca
    public int cell_number_x;
    public int cell_number_y;
    public int cell_number_z;
    
    
    // read inst_mom, torsion. .res files
    /**
     *# Each string contains parameters of corresponding cell: 
    # 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 6. temperature; 
    # 7. effective stress; 8. principal stress; 
    # 9-11. 3 components of specific volume force moment vector calculated using neighbour cells at 1st sphere only; 
    # 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 
    # 13. strain; 14. total specific torsion energy; |
    # 15. density of defects; 16. atom number; 17. defect number; 18. portion of defects.
    # Total number of grains: 108. 
     */
    public List<Double> effective_stresses = new ArrayList<>();
    public List<Double> strain = new ArrayList<>();
    public List<Double> total_specific_torsion_energy = new ArrayList<>();
//    public List<Double> density_of_defects = new ArrayList<>();
    
    /*
    # 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 
    # 6-8. 3 absolute values of components of instant specific force moment calculated using neighbour cells at 1st-3rd spheres; 
    # 9-11. 3 components of instant specific force moment; 
    # 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 
    # 13. absolute value of instant specific force moment; 
    # 14. current influx of torsion energy; 
    # 15. current coefficient for calculation of torsion energy change. 
    */
    public List<Double> x_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> y_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> z_component_of_inst_spec_force_moment = new ArrayList<>();
    public List<Double> current_influx_of_torsion_energy = new ArrayList<>();
    /*
    # 0. energy type; 1. location type; 2. grain index; 3-5. 3 coordinates; 
    # 6-8. 3 components of angle velocity; 
    # 9-11. 3 components of torsion angle vector; 
    # 12. state of material: 0 - elastic, 1 - plastic, 2 - damaged; 
    # 13. relative power of torsion (current influx of torsion energy divided by current influx of elastic energy); 
    # 14. relative torsion energy (total torsion energy divided by total elastic energy); 
    # 15. total specific energy (sum of thermal and elastic energy divided by cell volume); 
    */
    public List<Double> x_component_of_angle_velocity = new ArrayList<>();
    public List<Double> y_component_of_angle_velocity = new ArrayList<>();
    public List<Double> z_component_of_angle_velocity = new ArrayList<>();
    public List<Double> x_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> y_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> z_component_of_torsion_angle_vector = new ArrayList<>();
    public List<Double> relative_power_of_torsion = new ArrayList<>();
    public List<Double> relative_torsion_energy = new ArrayList<>();
    
    /** Variable for cell located at outer boundary */
    private final int OUTER_CELL = 2;
    
    public RecCAReadFiles(String file_path) throws FileNotFoundException, IOException{
        
        System.out.println("--------------------------------READ--------------------------------");
        System.out.println("FILE PATH : " + file_path);
        System.out.println("--------------------------------------------------------------------");
        
        Path path = Paths.get(file_path);
        
        if(file_path.endsWith("_relValues.res"))
        {
          for(String line : Files.readAllLines(path))
          {
            if(!line.startsWith("#") && !line.equals(null))
            {
                    String[] items = line.split(" ");
                    energy_type.add(Integer.parseInt(items[0]));
                    location_type.add(Integer.parseInt(items[1]));
                    grain_index.add(Integer.parseInt(items[2]));
                    x_coordinate.add(Double.parseDouble(items[3]));
                    y_coordinate.add(Double.parseDouble(items[4]));
                    z_coordinate.add(Double.parseDouble(items[5]));
                    relative_temperature.add(Double.parseDouble(items[6]));
                    relative_specific_elastic_energy.add(Double.parseDouble(items[7]));
                    total_specific_energy.add(Double.parseDouble(items[8]));        
                    
                    abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[9]));
                    abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[10]));
                    abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[11]));
                    state_of_material.add(Integer.parseInt(items[12]));
                    abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[13]));
                    relative_spec_dissipated_energy.add(Double.parseDouble(items[14]));
            }
          }
        }
        else 
          if(file_path.endsWith("_jocl_stresses.res"))
          {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    energy_type.add(Integer.parseInt(items[0]));
                    location_type.add(Integer.parseInt(items[1]));
                    grain_index.add(Integer.parseInt(items[2]));
                    x_coordinate.add(Double.parseDouble(items[3]));
                    y_coordinate.add(Double.parseDouble(items[4]));
                    z_coordinate.add(Double.parseDouble(items[5]));
                    
                    temperature.add(Double.parseDouble(items[6]));
                    abs_value_of_stress_vector.add(Double.parseDouble(items[7]));
                    value_for_crack_generation.add(Double.parseDouble(items[8]));        
                    
                    x_comp_of_stress_vector.add(Double.parseDouble(items[9]));
                    y_comp_of_stress_vector.add(Double.parseDouble(items[10]));
                    z_comp_of_stress_vector.add(Double.parseDouble(items[11]));
                    state_of_material.add(Integer.parseInt(items[12]));
                    current_influx_of_spec_dissipated_energy.add(Double.parseDouble(items[13]));
                    specific_dissipated_energy.add(Double.parseDouble(items[14]));
                }
            }
        }
        else 
          if(file_path.endsWith("_jocl_torsion.res"))
          {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    energy_type.add(Integer.parseInt(items[0]));
                    location_type.add(Integer.parseInt(items[1]));
                    grain_index.add(Integer.parseInt(items[2]));

                    x_coordinate.add(Double.parseDouble(items[3]));
                    y_coordinate.add(Double.parseDouble(items[4]));
                    z_coordinate.add(Double.parseDouble(items[5]));

                    x_component_of_angle_velocity.add(Math.abs(Double.parseDouble(items[6])));
                    y_component_of_angle_velocity.add(Math.abs(Double.parseDouble(items[7])));
                    z_component_of_angle_velocity.add(Math.abs(Double.parseDouble(items[8])));
                    
                    x_component_of_torsion_angle_vector.add(Math.abs(Double.parseDouble(items[ 9])));
                    y_component_of_torsion_angle_vector.add(Math.abs(Double.parseDouble(items[10])));
                    z_component_of_torsion_angle_vector.add(Math.abs(Double.parseDouble(items[11])));

                    state_of_material.add(Integer.parseInt(items[12]));
                    
                    abs_value_of_angle_velocity.add(Double.parseDouble(items[13]));
                    abs_value_of_torsion_angle.add(Double.parseDouble(items[14])); 
                }
            }
        }
        else 
        if(file_path.endsWith("_inst_mom.res"))
        {
          for(String line : Files.readAllLines(path))
          {
            if(!line.startsWith("#") && !line.equals(null))
            {
                    String[] items = line.split(" ");
                    energy_type.add(Integer.parseInt(items[0]));
                    location_type.add(Integer.parseInt(items[1]));
                    grain_index.add(Integer.parseInt(items[2]));

                    x_coordinate.add(Double.parseDouble(items[3]));
                    y_coordinate.add(Double.parseDouble(items[4]));
                    z_coordinate.add(Double.parseDouble(items[5]));

                    abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[6]));
                    abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[7]));
                    abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[8]));
                    
                    x_component_of_inst_spec_force_moment.add(Double.parseDouble(items[9]));
                    y_component_of_inst_spec_force_moment.add(Double.parseDouble(items[10]));
                    z_component_of_inst_spec_force_moment.add(Double.parseDouble(items[11]));

                    state_of_material.add(Integer.parseInt(items[12]));
                    abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[13]));
                    current_influx_of_torsion_energy.add(Double.parseDouble(items[14])); 
            }
          }
        }
        else 
          if(file_path.endsWith("_torsion.res"))
          {
            for(String line : Files.readAllLines(path))
            {
              if(!line.startsWith("#") && !line.equals(null))
              {
                    String[] items = line.split(" ");
                    energy_type.add(Integer.parseInt(items[0]));
                    location_type.add(Integer.parseInt(items[1]));
                    grain_index.add(Integer.parseInt(items[2]));
                    x_coordinate.add(Double.parseDouble(items[3]));
                    y_coordinate.add(Double.parseDouble(items[4]));
                    z_coordinate.add(Double.parseDouble(items[5]));
                    x_component_of_angle_velocity.add(Math.abs(Double.parseDouble(items[6])));
                    y_component_of_angle_velocity.add(Math.abs(Double.parseDouble(items[7])));
                    z_component_of_angle_velocity.add(Math.abs(Double.parseDouble(items[8])));
                    x_component_of_torsion_angle_vector.add(Math.abs(Double.parseDouble(items[ 9])));
                    y_component_of_torsion_angle_vector.add(Math.abs(Double.parseDouble(items[10])));
                    z_component_of_torsion_angle_vector.add(Math.abs(Double.parseDouble(items[11])));
                    state_of_material.add(Integer.parseInt(items[12]));
                    relative_power_of_torsion.add(Double.parseDouble(items[13]));
                    relative_torsion_energy.add(Double.parseDouble(items[14]));
                    total_specific_energy.add(Double.parseDouble(items[15]));
              }
            }
          }
        else 
          if(file_path.endsWith("jocl.res"))
          {
            for(String line : Files.readAllLines(path))
            {
              if(!line.startsWith("#") && !line.equals(null))
              {
                    String[] items = line.split(" ");
                    energy_type.add(Integer.parseInt(items[0]));
                    location_type.add(Integer.parseInt(items[1]));
                    grain_index.add(Integer.parseInt(items[2]));

                    x_coordinate.add(Double.parseDouble(items[3]));
                    y_coordinate.add(Double.parseDouble(items[4]));
                    z_coordinate.add(Double.parseDouble(items[5]));

                    temperature.add(Double.parseDouble(items[6]));
                    specific_elastic_energy.add(Double.parseDouble(items[7]));
                    principal_stresses.add(Double.parseDouble(items[8]));

                    x_component_of_specific_force_moment.add(Math.abs(Double.parseDouble(items[ 9])));
                    y_component_of_specific_force_moment.add(Math.abs(Double.parseDouble(items[10])));
                    z_component_of_specific_force_moment.add(Math.abs(Double.parseDouble(items[11])));

                    state_of_material.add(Integer.parseInt(items[12]));
                    current_influx_of_spec_dissipated_energy.add(Double.parseDouble(items[13]));
                    specific_dissipated_energy.add(Double.parseDouble(items[14]));
              }
            }
          }
        else 
          if(file_path.endsWith(".res"))
          {
            for(String line : Files.readAllLines(path))
            {
              if(!line.startsWith("#") && !line.equals(null))
              {
                    String[] items = line.split(" ");
                    energy_type.add(Integer.parseInt(items[0]));
                    location_type.add(Integer.parseInt(items[1]));
                    grain_index.add(Integer.parseInt(items[2]));

                    x_coordinate.add(Double.parseDouble(items[3]));
                    y_coordinate.add(Double.parseDouble(items[4]));
                    z_coordinate.add(Double.parseDouble(items[5]));

                    temperature.add(Double.parseDouble(items[6]));
                    effective_stresses.add(Double.parseDouble(items[7]));
                    principal_stresses.add(Double.parseDouble(items[8]));

                    x_component_of_specific_force_moment.add(Math.abs(Double.parseDouble(items[ 9])));
                    y_component_of_specific_force_moment.add(Math.abs(Double.parseDouble(items[10])));
                    z_component_of_specific_force_moment.add(Math.abs(Double.parseDouble(items[11])));

                    state_of_material.add(Integer.parseInt(items[12]));
                    strain.add(Double.parseDouble(items[13]));
                    total_specific_torsion_energy.add(Double.parseDouble(items[14]));
              }
            }
          }        
        else 
            System.out.println("Error: file is not choosen, or name of the file is corrupt");
        
        System.out.println("--------------------------------DONE--------------------------------");
                
    }
    
    public RecCAReadFiles(String file_path, boolean is_for_chart) throws IOException{

        System.out.println("--------------------------------READ--------------------------------");
        System.out.println("FILE PATH : " + file_path);

        Path path = Paths.get(file_path);
        
        if(file_path.endsWith("_relValues.res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    if(loc_type != OUTER_CELL)
                    {
                        energy_type.add(Integer.parseInt(items[0]));
                        location_type.add(Integer.parseInt(items[1]));
                        grain_index.add(Integer.parseInt(items[2]));
                        x_coordinate.add(Double.parseDouble(items[3]));
                        y_coordinate.add(Double.parseDouble(items[4]));
                        z_coordinate.add(Double.parseDouble(items[5]));
                        relative_temperature.add(Double.parseDouble(items[6]));
                        relative_specific_elastic_energy.add(Double.parseDouble(items[7]));
                        total_specific_energy.add(Double.parseDouble(items[8]));        

                        abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[9]));
                        abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[10]));
                        abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[11]));
                        state_of_material.add(Integer.parseInt(items[12]));
                        abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[13]));
                        relative_spec_dissipated_energy.add(Double.parseDouble(items[14]));
                    }
                }
            }
        }
        else 
        if(file_path.endsWith("_jocl_stresses.res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    if(loc_type != OUTER_CELL)
                    {
                        energy_type.add(Integer.parseInt(items[0]));
                        location_type.add(Integer.parseInt(items[1]));
                        grain_index.add(Integer.parseInt(items[2]));
                        x_coordinate.add(Double.parseDouble(items[3]));
                        y_coordinate.add(Double.parseDouble(items[4]));
                        z_coordinate.add(Double.parseDouble(items[5]));

                        temperature.add(Double.parseDouble(items[6]));
                        abs_value_of_stress_vector.add(Double.parseDouble(items[7]));
                        value_for_crack_generation.add(Double.parseDouble(items[8]));        

                        x_comp_of_stress_vector.add(Double.parseDouble(items[9]));
                        y_comp_of_stress_vector.add(Double.parseDouble(items[10]));
                        z_comp_of_stress_vector.add(Double.parseDouble(items[11]));
                        state_of_material.add(Integer.parseInt(items[12]));
                        current_influx_of_spec_dissipated_energy.add(Double.parseDouble(items[13]));
                        specific_dissipated_energy.add(Double.parseDouble(items[14]));
                    }
                }
            }
        }
        else 
        if(file_path.endsWith("_jocl_torsion.res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    if(loc_type != OUTER_CELL)
                    {
                        energy_type.add(Integer.parseInt(items[0]));
                        location_type.add(Integer.parseInt(items[1]));
                        grain_index.add(Integer.parseInt(items[2]));

                        x_coordinate.add(Double.parseDouble(items[3]));
                        y_coordinate.add(Double.parseDouble(items[4]));
                        z_coordinate.add(Double.parseDouble(items[5]));

                        x_component_of_angle_velocity.add(Double.parseDouble(items[6]));
                        y_component_of_angle_velocity.add(Double.parseDouble(items[7]));
                        z_component_of_angle_velocity.add(Double.parseDouble(items[8]));

                        x_component_of_torsion_angle_vector.add(Double.parseDouble(items[9]));
                        y_component_of_torsion_angle_vector.add(Double.parseDouble(items[10]));
                        z_component_of_torsion_angle_vector.add(Double.parseDouble(items[11]));

                        state_of_material.add(Integer.parseInt(items[12]));

                        abs_value_of_angle_velocity.add(Double.parseDouble(items[13]));
                        abs_value_of_torsion_angle.add(Double.parseDouble(items[14])); 
                    }
                }
            }
        }
        else 
        if(file_path.endsWith("_inst_mom.res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    if(loc_type != OUTER_CELL)
                    {
                        energy_type.add(Integer.parseInt(items[0]));
                        location_type.add(Integer.parseInt(items[1]));
                        grain_index.add(Integer.parseInt(items[2]));

                        x_coordinate.add(Double.parseDouble(items[3]));
                        y_coordinate.add(Double.parseDouble(items[4]));
                        z_coordinate.add(Double.parseDouble(items[5]));

                        abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[6]));
                        abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[7]));
                        abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[8]));

                        x_component_of_inst_spec_force_moment.add(Double.parseDouble(items[9]));
                        y_component_of_inst_spec_force_moment.add(Double.parseDouble(items[10]));
                        z_component_of_inst_spec_force_moment.add(Double.parseDouble(items[11]));

                        state_of_material.add(Integer.parseInt(items[12]));
                        abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[13]));
                        current_influx_of_torsion_energy.add(Double.parseDouble(items[14])); 
                    }
                }
            }
        }
        else 
        if(file_path.endsWith("_torsion.res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    if(loc_type != OUTER_CELL)
                    {
                        energy_type.add(Integer.parseInt(items[0]));
                        location_type.add(Integer.parseInt(items[1]));
                        grain_index.add(Integer.parseInt(items[2]));
                        x_coordinate.add(Double.parseDouble(items[3]));
                        y_coordinate.add(Double.parseDouble(items[4]));
                        z_coordinate.add(Double.parseDouble(items[5]));
                        x_component_of_angle_velocity.add(Double.parseDouble(items[6]));
                        y_component_of_angle_velocity.add(Double.parseDouble(items[7]));
                        z_component_of_angle_velocity.add(Double.parseDouble(items[8]));
                        x_component_of_torsion_angle_vector.add(Double.parseDouble(items[9]));
                        y_component_of_torsion_angle_vector.add(Double.parseDouble(items[10]));
                        z_component_of_torsion_angle_vector.add(Double.parseDouble(items[11]));
                        state_of_material.add(Integer.parseInt(items[12]));
                        relative_power_of_torsion.add(Double.parseDouble(items[13]));
                        relative_torsion_energy.add(Double.parseDouble(items[14]));
                        total_specific_energy.add(Double.parseDouble(items[15]));
                    }                    
                }
            }
        }
        else
        if(file_path.endsWith("jocl.res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    if(loc_type != OUTER_CELL)
                    {
                        energy_type.add(Integer.parseInt(items[0]));
                        location_type.add(Integer.parseInt(items[1]));
                        grain_index.add(Integer.parseInt(items[2]));

                        x_coordinate.add(Double.parseDouble(items[3]));
                        y_coordinate.add(Double.parseDouble(items[4]));
                        z_coordinate.add(Double.parseDouble(items[5]));

                        temperature.add(Double.parseDouble(items[6]));
                        specific_elastic_energy.add(Double.parseDouble(items[7]));
                        principal_stresses.add(Double.parseDouble(items[8]));

                        x_component_of_specific_force_moment.add(Double.parseDouble(items[9]));
                        y_component_of_specific_force_moment.add(Double.parseDouble(items[10]));
                        z_component_of_specific_force_moment.add(Double.parseDouble(items[11]));

                        state_of_material.add(Integer.parseInt(items[12]));
                        current_influx_of_spec_dissipated_energy.add(Double.parseDouble(items[13]));
                        specific_dissipated_energy.add(Double.parseDouble(items[14]));
                    }
                    
                }
            }
        }
        else 
        if(file_path.endsWith(".res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    if(loc_type != OUTER_CELL)
                    {
                        energy_type.add(Integer.parseInt(items[0]));
                        location_type.add(Integer.parseInt(items[1]));
                        grain_index.add(Integer.parseInt(items[2]));

                        x_coordinate.add(Double.parseDouble(items[3]));
                        y_coordinate.add(Double.parseDouble(items[4]));
                        z_coordinate.add(Double.parseDouble(items[5]));

                        temperature.add(Double.parseDouble(items[6]));
                        effective_stresses.add(Double.parseDouble(items[7]));
                        principal_stresses.add(Double.parseDouble(items[8]));

                        x_component_of_specific_force_moment.add(Double.parseDouble(items[9]));
                        y_component_of_specific_force_moment.add(Double.parseDouble(items[10]));
                        z_component_of_specific_force_moment.add(Double.parseDouble(items[11]));

                        state_of_material.add(Integer.parseInt(items[12]));
                        strain.add(Double.parseDouble(items[13]));
                        total_specific_torsion_energy.add(Double.parseDouble(items[14]));
                    }                    
                }
            }
        }        
        else 
            System.out.println("Error: file is not choosen, or name of the file is corrupt");
        
        System.out.println("--------------------------------DONE--------------------------------");
    }
    
    double x_init, y_init, z_init, x_max, y_max, z_max;
    
    public RecCAReadFiles(String file_path, double x, double y, double z,
                          double x_length, double y_length, double z_length) throws IOException{
        
        x_init = x;
        y_init = y;
        z_init = z;
        
        System.out.println("RecCAReadFiles: x_init = " + x_init);
        System.out.println("RecCAReadFiles: y_init = " + y_init);
        System.out.println("RecCAReadFiles: z_init = " + z_init);
        
        x_max = x+x_length;
        y_max = y+y_length;
        z_max = z+z_length;
        
        System.out.println("RecCAReadFiles: x_length = " + x_length);
        System.out.println("RecCAReadFiles: y_length = " + y_length);
        System.out.println("RecCAReadFiles: z_length = " + z_length);
        
        System.out.println("RecCAReadFiles: x_max = " + x_max);
        System.out.println("RecCAReadFiles: y_max = " + y_max);
        System.out.println("RecCAReadFiles: z_max = " + z_max);
        
        System.out.println("------------------------------READ------------------------------");
        System.out.println("File: " + file_path);
        
        Path path = Paths.get(file_path);
        
        if(file_path.endsWith("relValues.res"))
        {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    double x_coord = Double.parseDouble(items[3]);
                    double y_coord = Double.parseDouble(items[4]);
                    double z_coord = Double.parseDouble(items[5]);
                    
                    if(loc_type != OUTER_CELL)
                    if( (x_coord >= x) && (x_coord <= x_max) &&
                        (y_coord >= y) && (y_coord <= y_max) &&
                        (z_coord >= z) && (z_coord <= z_max))                        
                    {
                        relative_temperature.add(Double.parseDouble(items[6]));
                        relative_specific_elastic_energy.add(Math.abs(Double.parseDouble(items[7])));
                        total_specific_energy.add(Double.parseDouble(items[8]));

                        abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[9]));
                        abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[10]));
                        abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[11]));

                        abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[13]));
                        relative_spec_dissipated_energy.add(Double.parseDouble(items[14]));                         
                    }
                }
            }
        }
        else 
          if(file_path.endsWith("jocl_stresses.res"))
          {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    double x_coord = Double.parseDouble(items[3]);
                    double y_coord = Double.parseDouble(items[4]);
                    double z_coord = Double.parseDouble(items[5]);
                    
                    if(loc_type != OUTER_CELL)
                    if( (x_coord >= x) && (x_coord <= x_max) &&
                        (y_coord >= y) && (y_coord <= y_max) &&
                        (z_coord >= z) && (z_coord <= z_max))                        
                    {
                        temperature.add(Double.parseDouble(items[6]));
                        abs_value_of_stress_vector.add(Double.parseDouble(items[7]));
                        value_for_crack_generation.add(Double.parseDouble(items[8]));
                        x_comp_of_stress_vector.add(Double.parseDouble(items[9]));
                        y_comp_of_stress_vector.add(Double.parseDouble(items[10]));
                        z_comp_of_stress_vector.add(Double.parseDouble(items[11]));
                        current_influx_of_spec_dissipated_energy.add(Double.parseDouble(items[13]));
                        specific_dissipated_energy.add(Double.parseDouble(items[14]));
                    }
                }
            }
        }
        else 
          if(file_path.endsWith("_jocl_torsion.res"))
          {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    double x_coord = Double.parseDouble(items[3]);
                    double y_coord = Double.parseDouble(items[4]);
                    double z_coord = Double.parseDouble(items[5]);
                    
                    if(loc_type != OUTER_CELL)
                    if( (x_coord >= x) && (x_coord <= x_max) &&
                        (y_coord >= y) && (y_coord <= y_max) &&
                        (z_coord >= z) && (z_coord <= z_max))                        
                    {
                        x_component_of_angle_velocity.add(Double.parseDouble(items[6]));
                        y_component_of_angle_velocity.add(Double.parseDouble(items[7]));
                        z_component_of_angle_velocity.add(Double.parseDouble(items[8]));

                        x_component_of_torsion_angle_vector.add(Double.parseDouble(items[9]));
                        y_component_of_torsion_angle_vector.add(Double.parseDouble(items[10]));
                        z_component_of_torsion_angle_vector.add(Double.parseDouble(items[11]));
                        
                        abs_value_of_angle_velocity.add(Double.parseDouble(items[13]));
                        abs_value_of_torsion_angle.add(Double.parseDouble(items[14])); 
                    }
                }
            }
        }
        else 
          if(file_path.endsWith("jocl.res"))
          {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    double x_coord = Double.parseDouble(items[3]);
                    double y_coord = Double.parseDouble(items[4]);
                    double z_coord = Double.parseDouble(items[5]);
                    
                    if(loc_type != OUTER_CELL)
                    if( (x_coord >= x) && (x_coord <= x_max) &&
                        (y_coord >= y) && (y_coord <= y_max) &&
                        (z_coord >= z) && (z_coord <= z_max))                        
                    {
                        temperature.add(Double.parseDouble(items[6]));
                        specific_elastic_energy.add(Double.parseDouble(items[7]));
                        principal_stresses.add(Double.parseDouble(items[8]));

                        x_component_of_specific_force_moment.add(Double.parseDouble(items[9]));
                        y_component_of_specific_force_moment.add(Double.parseDouble(items[10]));
                        z_component_of_specific_force_moment.add(Double.parseDouble(items[11]));

                        current_influx_of_spec_dissipated_energy.add(Double.parseDouble(items[13]));
                        specific_dissipated_energy.add(Double.parseDouble(items[14]));                        
                    }
                }
            }
        }
        else 
          if(file_path.endsWith("torsion.res"))
          {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    double x_coord = Double.parseDouble(items[3]);
                    double y_coord = Double.parseDouble(items[4]);
                    double z_coord = Double.parseDouble(items[5]);
                    
                    if(loc_type != OUTER_CELL)
                    if( (x_coord >= x) && (x_coord <= x_max) &&
                        (y_coord >= y) && (y_coord <= y_max) &&
                        (z_coord >= z) && (z_coord <= z_max))                        
                    {
                        x_component_of_angle_velocity.add(Double.parseDouble(items[6]));
                        y_component_of_angle_velocity.add(Double.parseDouble(items[7]));
                        z_component_of_angle_velocity.add(Double.parseDouble(items[8]));
                        
                        x_component_of_torsion_angle_vector.add(Double.parseDouble(items[9]));
                        y_component_of_torsion_angle_vector.add(Double.parseDouble(items[10]));
                        z_component_of_torsion_angle_vector.add(Double.parseDouble(items[11]));
                        
                        relative_power_of_torsion.add(Double.parseDouble(items[13]));
                        relative_torsion_energy.add(Double.parseDouble(items[14]));
                        total_specific_energy.add(Double.parseDouble(items[15]));                    }
                }
            }
        }
        else 
          if(file_path.endsWith("inst_mom.res"))
          {
            for(String line : Files.readAllLines(path))
            {
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    double x_coord = Double.parseDouble(items[3]);
                    double y_coord = Double.parseDouble(items[4]);
                    double z_coord = Double.parseDouble(items[5]);
                    
                    if(loc_type != OUTER_CELL)
                    if( (x_coord >= x) && (x_coord <= x_max) &&
                        (y_coord >= y) && (y_coord <= y_max) &&
                        (z_coord >= z) && (z_coord <= z_max))                        
                    {
                        abs_value_of_X_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[6]));
                        abs_value_of_Y_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[7]));
                        abs_value_of_Z_comp_of_inst_spec_force_moment.add(Double.parseDouble(items[8]));

                        x_component_of_inst_spec_force_moment.add(Double.parseDouble(items[9]));
                        y_component_of_inst_spec_force_moment.add(Double.parseDouble(items[10]));
                        z_component_of_inst_spec_force_moment.add(Double.parseDouble(items[11]));

                        abs_value_of_inst_spec_force_moment.add(Double.parseDouble(items[13]));
                        current_influx_of_torsion_energy.add(Double.parseDouble(items[14])); 
                    }
                }
            }
        }
        else 
        if(file_path.endsWith(".res"))
        {
            for(String line : Files.readAllLines(path)){
                if(!line.startsWith("#") && !line.equals(null))
                {
                    String[] items = line.split(" ");
                    
                    int loc_type = Integer.parseInt(items[1]);
                    
                    double x_coord = Double.parseDouble(items[3]);
                    double y_coord = Double.parseDouble(items[4]);
                    double z_coord = Double.parseDouble(items[5]);
                    
                    if(loc_type != OUTER_CELL)
                    if( (x_coord >= x) && (x_coord <= x_max) &&
                        (y_coord >= y) && (y_coord <= y_max) &&
                        (z_coord >= z) && (z_coord <= z_max))
                    {
                        temperature.add(Double.parseDouble(items[6]));
                        effective_stresses.add(Double.parseDouble(items[7]));
                        principal_stresses.add(Double.parseDouble(items[8]));

                        x_component_of_specific_force_moment.add(Double.parseDouble(items[9]));
                        y_component_of_specific_force_moment.add(Double.parseDouble(items[10]));
                        z_component_of_specific_force_moment.add(Double.parseDouble(items[11]));

                        strain.add(Double.parseDouble(items[13]));
                        total_specific_torsion_energy.add(Double.parseDouble(items[14]));                        
                    }
                }
            }
        }
        
        int tempr_size   = temperature.size();
        int eff_str_size = effective_stresses.size();
        int pr_str_size  = principal_stresses.size();
        int mom_X_size   = x_component_of_specific_force_moment.size();
        int mom_Y_size   = y_component_of_specific_force_moment.size();
        int mom_Z_size   = z_component_of_specific_force_moment.size();
        int strain_size  = strain.size();
        int total_spec_tors_en_size = total_specific_torsion_energy.size();
        
        if(tempr_size   == eff_str_size & 
           eff_str_size == pr_str_size & 
           pr_str_size  == mom_X_size & 
           mom_X_size   == mom_Y_size &
           mom_Y_size   == mom_Z_size & 
           mom_Z_size   == strain_size & 
           strain_size  == total_spec_tors_en_size)
        {
            System.out.println("Number of cells: "+tempr_size);
        }
        else
            System.out.println("ERROR!!! Sizes of arrays with data are different!!!");
                
        System.out.println("------------------------------DONE------------------------------");
        
    }
    
    public double getMaxValue(List<Double> list){
        return Collections.max(list);
    }
    
    public double getMinValue(List<Double> list){
        return Collections.min(list);
    }
    
    //.clrs
    public List<Integer> r_color = new ArrayList<>();
    public List<Integer> g_color = new ArrayList<>();
    public List<Integer> b_color = new ArrayList<>();
    public List<Integer> color_number = new ArrayList<>();
    public List<Integer> grain_index_clrs = new ArrayList<>();
    
    public void readClrsFile(String file_path) throws IOException{
        System.out.println("Read: " + file_path);
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(file_path));
        List<String> lines = reader.lines().collect(Collectors.toList());
        
        for (int i=1; i < lines.size(); i++){
            String[] tokens = lines.get(i).split(" ");                
            if(tokens.length == 2){
                grain_index_clrs.add(Integer.parseInt(tokens[0]));
                color_number.add(Integer.parseInt(tokens[1]));
            }
            else if(tokens.length == 4){                    
                r_color.add(Integer.parseInt(tokens[1]));
                g_color.add(Integer.parseInt(tokens[2]));
                b_color.add(Integer.parseInt(tokens[3]));
            }
        }             
    }
    
    public String getClrsFilePath(String project_dir) throws IOException{
        String grn_file_path = null;
        for (File file : new File(project_dir).listFiles()){
            if(file.getName().endsWith(".clrs")){
                grn_file_path = file.getAbsolutePath();
            }
        }
        return grn_file_path;
    }
    
    public String getSecaFilePath(String project_dir) throws IOException{
        String seca_file_path = null;
        File[] list_of_files = new File(project_dir).listFiles();
        for (File file : list_of_files){
            if(file.getName().endsWith(".seca")){
                seca_file_path = file.getAbsolutePath();
            }
        }        
        return seca_file_path;
    }
    
    public void readSecaFile(String file_path) throws IOException{
        System.out.println("Read: " + file_path);
        
        Properties props = new Properties();
        
        FileInputStream stream = new FileInputStream(file_path);
        props.load(stream);
        stream.close();
        
        cell_number_x = Integer.valueOf(props.getProperty("cell_number_X")).intValue();
        cell_number_y = Integer.valueOf(props.getProperty("cell_number_Y")).intValue();
        cell_number_z = Integer.valueOf(props.getProperty("cell_number_Z")).intValue();
                
    }
    
    public double getAverageValue(List<Double> list){
        double sum = 0;
        for(int i=0 ; i < list.size() ; i++){
            sum = sum + list.get(i);
        }
        double value = sum/list.size();
        
        DoubleSummaryStatistics stats = list.stream().mapToDouble(x -> x).summaryStatistics();
        return stats.getAverage();
    }
    
    public List<String> materials = new ArrayList<>();
    public List<Integer> grn_index = new ArrayList<>();
    
    public void readGrnFile(String task_name) throws IOException{
        
        String path = null;
        
        for (File file : new File("./user/task_db/" + task_name + "/").listFiles()){
            if(file.getName().endsWith(".grn"))
                path = file.getAbsolutePath();            
        }
        
        for(String line : Files.readAllLines(Paths.get(path))){
            if(!line.startsWith("#") && !line.equals(null) && !line.equals("")){
                String[] items = line.split(" ");
                grn_index.add(Integer.parseInt(items[0]));
                materials.add(items[1]);
            }
        }
        
    }
    
}
