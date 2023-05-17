/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 *
 * @author DmitryB
 */
public class SpecimenTableFX extends GridPane{
    
    Label cell_cize, element_number_X, element_number_Y, element_number_Z,
          surf_thickness, min_num_of_neighbors, spec_size_X, spec_size_Y, spec_size_Z,
          coordinate_X, coordinate_Y, coordinate_Z, anist_coeff, number_of_phases,
          particle_volume_fraction, particle_radius, 
          anis_vector_X, anis_vector_Y, anis_vector_Z, spec_anis_coeff,
          packing_type, show_grain_structure, method;
    
    Label cell_cize_val, element_number_X_val, element_number_Y_val, element_number_Z_val,
          surf_thickness_val, min_num_of_neighbors_val, spec_size_X_val, spec_size_Y_val, spec_size_Z_val,
          coordinate_X_val, coordinate_Y_val, coordinate_Z_val, anist_coeff_val, number_of_phases_val,
          particle_volume_fraction_val, particle_radius_val, 
          anis_vector_X_val, anis_vector_Y_val, anis_vector_Z_val, spec_anis_coeff_val,
          packing_type_val, show_grain_structure_val, method_val;
    
    UISpecimen spec;
    
    public SpecimenTableFX(UISpecimen ui_spec){
        this.spec = ui_spec;
        
        this.setGridLinesVisible(true);
        
        initAllElements();
        addAllElements();
        
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5,5,5,5));
    }
    
    public void initAllElements(){
        
        cell_cize = new Label("Cell Size:");
        cell_cize.setPadding(new Insets(2,2,2,2));
        
        element_number_X = new Label("Element Number X:");
        element_number_X.setPadding(new Insets(2,2,2,2));
        
        element_number_Y = new Label("Element Number Y:");
        element_number_Y.setPadding(new Insets(2,2,2,2));
        
        element_number_Z = new Label("Element Number Z:");
        element_number_Z.setPadding(new Insets(2,2,2,2));
        
        surf_thickness = new Label("Surface Thickness:");
        surf_thickness.setPadding(new Insets(2,2,2,2));
        
        min_num_of_neighbors = new Label("Min. Number of Neighbors:");
        min_num_of_neighbors.setPadding(new Insets(2,2,2,2));
        
        spec_size_X = new Label("Specimen Size X:");
        spec_size_X.setPadding(new Insets(2,2,2,2));
        
        spec_size_Y = new Label("Specimen Size Y");
        spec_size_Y.setPadding(new Insets(2,2,2,2));
        
        spec_size_Z = new Label("Specimen Size Z");
        spec_size_Z.setPadding(new Insets(2,2,2,2));
        
        coordinate_X = new Label("Coordinate X:");
        coordinate_X.setPadding(new Insets(2,2,2,2));
        
        coordinate_Y = new Label("Coordinate Y:");
        coordinate_Y.setPadding(new Insets(2,2,2,2));
        
        coordinate_Z = new Label("Coordinate Z:");
        coordinate_Z.setPadding(new Insets(2,2,2,2));
        
        anist_coeff = new Label("Anis. Coefficient:");
        anist_coeff.setPadding(new Insets(2,2,2,2));
        
        number_of_phases = new Label("Number of Phases:");
        number_of_phases.setPadding(new Insets(2,2,2,2));
        
        particle_volume_fraction = new Label("Particle Volume Fraction:");
        particle_volume_fraction.setPadding(new Insets(2,2,2,2));
        
        particle_radius = new Label("Particle Radius:");
        particle_radius.setPadding(new Insets(2,2,2,2));
        
        anis_vector_X = new Label("Anis. Vector X:");
        anis_vector_X.setPadding(new Insets(2,2,2,2));
        
        anis_vector_Y = new Label("Anis. Vector Y:");
        anis_vector_Y.setPadding(new Insets(2,2,2,2));
        
        anis_vector_Z = new Label("Anis. Vector Z:");
        anis_vector_Z.setPadding(new Insets(2,2,2,2));
        
        spec_anis_coeff = new Label("Spec. Anis. Coefficient:");
        spec_anis_coeff.setPadding(new Insets(2,2,2,2));
        
        packing_type = new Label("Packing Type:");
        packing_type.setPadding(new Insets(2,2,2,2));
        
        show_grain_structure = new Label("Show Grain Structure:");
        show_grain_structure.setPadding(new Insets(2,2,2,2));
        
        method = new Label("Method:");
        method.setPadding(new Insets(2,2,2,2));
        
        cell_cize_val = new Label(String.valueOf(spec.getCellSize()) + " (m)");
        element_number_X_val = new Label(String.valueOf(spec.getElementNumberX()));
        element_number_Y_val = new Label(String.valueOf(spec.getElementNumberY()));
        element_number_Z_val = new Label(String.valueOf(spec.getElementNumberZ()));
        surf_thickness_val = new Label(String.valueOf(spec.getSurfThickness()));
        min_num_of_neighbors_val = new Label(String.valueOf(spec.getMinNeighboursNumber()));
        spec_size_X_val = new Label(String.valueOf(spec.getSpecimenSizeX()) + "(m)  /  " + String.valueOf(spec.getSpecimenSizeX()/(spec.getCellSize()/2)) + "(rad)");
        spec_size_Y_val = new Label(String.valueOf(spec.getSpecimenSizeY()) + "(m)  /  " + String.valueOf(spec.getSpecimenSizeY()/(spec.getCellSize()/2)) + "(rad)");
        spec_size_Z_val = new Label(String.valueOf(spec.getSpecimenSizeZ()) + "(m)  /  " + String.valueOf(spec.getSpecimenSizeZ()/(spec.getCellSize()/2)) + "(rad)");
        coordinate_X_val = new Label(String.valueOf(spec.getCoordinateX()));
        coordinate_Y_val = new Label(String.valueOf(spec.getCoordinateY()));
        coordinate_Z_val = new Label(String.valueOf(spec.getCoordinateZ()));
        anist_coeff_val = new Label(String.valueOf(spec.getAnisotropyCoeff()));
        number_of_phases_val = new Label(String.valueOf(spec.getNumberOfPhases()));
        particle_volume_fraction_val = new Label(String.valueOf(spec.getParticleVolumeFraction()));
        particle_radius_val = new Label(String.valueOf(spec.getParticleRadius()));
        anis_vector_X_val = new Label(String.valueOf(spec.getAnisVectorX()));
        anis_vector_Y_val = new Label(String.valueOf(spec.getAnisVectorY()));
        anis_vector_Z_val = new Label(String.valueOf(spec.getAnisVectorZ()));
        spec_anis_coeff_val = new Label(String.valueOf(spec.getSpecimenAnisotropyCoeff()));
        
        packing_type_val = new Label();
        if(spec.getPackingType() == 0)
            packing_type_val.setText("HCP");
        else if(spec.getPackingType() == 1)
            packing_type_val.setText("SCP");
        
        show_grain_structure_val = new Label(String.valueOf(spec.getGrainCellularAutomata()));
        
        method_val = new Label();
        
        if(spec.getStochasticMethod())
            method_val.setText("stochastic");
        else if(spec.getMixedMethod())
            method_val.setText("mixed");
        else if(spec.getRegularMethod())
            method_val.setText("regular");
        
    }
    
    public void addAllElements(){
        
        GridPane.setConstraints(cell_cize, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(cell_cize_val, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(element_number_X, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(element_number_X_val, 1, 1, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(element_number_Y, 0, 2, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(element_number_Y_val, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(element_number_Z, 0, 3, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(element_number_Z_val, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(surf_thickness, 0, 4, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(surf_thickness_val, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(min_num_of_neighbors, 0, 5, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(min_num_of_neighbors_val, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(spec_size_X, 0, 6, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(spec_size_X_val, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(spec_size_Y, 0, 7, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(spec_size_Y_val, 1, 7, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(spec_size_Z, 0, 8, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(spec_size_Z_val, 1, 8, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(coordinate_X, 0, 9, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(coordinate_X_val, 1, 9, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(coordinate_Y, 0, 10, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(coordinate_Y_val, 1, 10, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(coordinate_Z, 0, 11, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(coordinate_Z_val, 1, 11, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(anist_coeff, 0, 12, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anist_coeff_val, 1, 12, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(number_of_phases, 0, 13, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(number_of_phases_val, 1, 13, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(particle_volume_fraction, 0, 14, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(particle_volume_fraction_val, 1, 14, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(particle_radius, 0, 15, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(particle_radius_val, 1, 15, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(anis_vector_X, 0, 16, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anis_vector_X_val, 1, 16, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(anis_vector_Y, 0, 17, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anis_vector_Y_val, 1, 17, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(anis_vector_Z, 0, 18, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(anis_vector_Z_val, 1, 18, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(spec_anis_coeff, 0, 19, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(spec_anis_coeff_val, 1, 19, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(packing_type, 0, 20, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(packing_type_val, 1, 20, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(show_grain_structure, 0, 21, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(show_grain_structure_val, 1, 21, 1, 1, HPos.CENTER, VPos.CENTER);
        
        GridPane.setConstraints(method, 0, 22, 1, 1, HPos.RIGHT, VPos.CENTER);
        GridPane.setConstraints(method_val, 1, 22, 1, 1, HPos.CENTER, VPos.CENTER);
        
        this.getChildren().addAll(
                cell_cize, element_number_X, element_number_Y, element_number_Z,
                surf_thickness, min_num_of_neighbors, spec_size_X, spec_size_Y, spec_size_Z,
                coordinate_X, coordinate_Y, coordinate_Z, anist_coeff, number_of_phases,
                particle_volume_fraction, particle_radius, 
                anis_vector_X, anis_vector_Y, anis_vector_Z, spec_anis_coeff,
                packing_type, show_grain_structure, method,
                cell_cize_val, element_number_X_val, element_number_Y_val, element_number_Z_val,
                surf_thickness_val, min_num_of_neighbors_val, spec_size_X_val, spec_size_Y_val, spec_size_Z_val,
                coordinate_X_val, coordinate_Y_val, coordinate_Z_val, anist_coeff_val, number_of_phases_val,
                particle_volume_fraction_val, particle_radius_val, 
                anis_vector_X_val, anis_vector_Y_val, anis_vector_Z_val, spec_anis_coeff_val,
                packing_type_val, show_grain_structure_val, method_val
        );
    }
    
}
