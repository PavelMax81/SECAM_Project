/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

/**
 *
 * @author DmitryB
 */
public class UI_3DPictureParams {
    
    boolean is_location_type_expanded, 
            is_grain_types_expanded, 
            is_states_of_cell_expanded, 
            is_types_of_inner_cells_expanded, 
            is_types_of_outer_boundary_cells_expanded;
    
    boolean OUTER_CELL, 
            INTRAGRANULAR_CELL, 
            INTERGRANULAR_CELL, 
            INNER_BOUNDARY_CELL, 
            INNER_BOUNDARY_INTERGRANULAR_CELL,
            GRAIN_BOUNDARY_REGION_CELL, 
            INNER_BOUND_GRAIN_BOUND_REGION_CELL;
    
    boolean INITIAL_GRAIN, 
            NEW_GRAIN, 
            TWINNING_GRAIN;
    
    boolean ELASTIC_CELL,
            PLASTIC_CELL,
            DAMAGED_CELL;
    
    boolean LAYER_CELL,
            INNER_CELL;
    
    boolean ADIABATIC_TEMPERATURE_CELL,
            ADIABATIC_THERMAL_CELL,
            STRAIN_ADIABATIC_CELL,
            STRAIN_TEMPERATURE_CELL,
            STRAIN_THERMAL_CELL,
            STRESS_ADIABATIC_CELL,
            STRESS_TEMPERATURE_CELL,
            STRESS_THERMAL_CELL,
            ADIABATIC_ADIABATIC_CELL;
    
    public UI_3DPictureParams(){

        is_location_type_expanded = true;
        is_grain_types_expanded = false;
        is_states_of_cell_expanded = false;
        is_types_of_inner_cells_expanded = false;
        is_types_of_outer_boundary_cells_expanded = false;
        
        OUTER_CELL = false;
        INTRAGRANULAR_CELL = false;
        INTERGRANULAR_CELL = false;
        INNER_BOUNDARY_CELL = true;
        INNER_BOUNDARY_INTERGRANULAR_CELL = true;
        GRAIN_BOUNDARY_REGION_CELL = false;
        INNER_BOUND_GRAIN_BOUND_REGION_CELL = true;
        
        INITIAL_GRAIN = true;
        NEW_GRAIN = true;
        TWINNING_GRAIN = true;
        
        ELASTIC_CELL = true;
        PLASTIC_CELL = true;
        DAMAGED_CELL = true;
        
        LAYER_CELL = false;
        INNER_CELL = false;
        
        ADIABATIC_TEMPERATURE_CELL = true;
        ADIABATIC_THERMAL_CELL = true;
        STRAIN_ADIABATIC_CELL = true;
        STRAIN_TEMPERATURE_CELL = true;
        STRAIN_THERMAL_CELL = true;
        STRESS_ADIABATIC_CELL = true;
        STRESS_TEMPERATURE_CELL = true;
        STRESS_THERMAL_CELL = true;
        ADIABATIC_ADIABATIC_CELL = true;
    }    
    
    public UI_3DPictureParams(UI_3DPictureParams params){
        
    }
    
}
