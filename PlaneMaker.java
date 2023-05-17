package util;

import java.io.*;
import java.util.*;
import recGeometry.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.*;
import cellcore.*;

/*
 * @(#) PlaneMaker.java version 1.0.0;       October 28 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for extracting of 3D-cells located on certain distance from plane.
 *
 *=============================================================
 *  Last changes :
 *          28 October 2009 by Pavel V. Maksimov (creation of the class)
 *          File version 1.0.0
 */

/** Class for extracting of 3D-cells located on certain distance from plane.
 *  @author Dmitry D. Moiseenko, Pavel V. Maksimov, Gregory S. Bikineev
 *  @version 1.0.0 - October 2009
 */
public class PlaneMaker
{
    /** Name of file with specimen geometric parameters */
    protected String specimen_file;

    /** Name of file with parameters of grains */
    protected String grains_file;

    /** Name of file with minimal and maximal values of temperature and stress
     * for initial specimen including outer boundary cells
     */
    protected String extreme_values_file;

    /** Name of file with parameters of specimen layers */
    protected String layers_file_name;

    /** Cell size */
    protected double cell_size;

    /** Type of automata packing */
    protected byte packing_type;

    /** Set of all 3D-cells contained in certain specimen */
    protected StringVector specimenR3;

    /** Table of materials of all grains contained in certain specimen */
    protected StringVector grains_table;

    /** Cells containing minimal coordinates and parameters of cells in plane */
    protected VisualCellR3 min_cellR3;

    /** Cells containing maximal coordinates and parameters of cells in plane */
    protected VisualCellR3 max_cellR3;

    /** Array of coefficients of plane equation */
    protected double[] plane_coeffs;

    /** Variable responsible for showing of grain boundaries */
    protected byte show_grain_bounds;

    /** Numbers of cells along axes */
    protected int cell_number_I;
    protected int cell_number_J;
    protected int cell_number_K;

    /** Number of shown layers of cells */
    protected final int layer_number = 1;

    /** List of materials: element index corresponds to grain index,
     * element value corresponds to grain material. */
    protected ArrayList grain_materials;

    /** List of materials consisting specimen */
    protected ArrayList materials;

    /** Initial minimal and maximal values of temperature, strain and "effective" stress */
    protected double init_min_temperature;
    protected double init_max_temperature;
    protected double init_min_eff_stress;
    protected double init_max_eff_stress;
    protected double init_min_strain;
    protected double init_max_strain;

    /** Thickness of 1st specimen layer */
    protected double layer1_thickness;

    /** Thickness of 2nd specimen layer */
    protected double layer2_thickness;

    /** Thickness of 3rd specimen layer */
    protected double layer3_thickness;

    /** Size of element in 2nd specimen layer */
    protected double layer2_elem_size;

    /** Type of 2nd specimen layer */
    protected byte   layer2_type;

    /** Variable responsible for drawing of all cells:
     * if it equals false then boundary cells are not shown,
     * if it equals true then all cells are shown.
     */
 //   protected boolean show_all_cells;// = true;//false

    /** The constructor creates representative of class for extracting of
     * 3D-cells located on certain distance from plane.
     * @param input_file_name file with information about parameters of 3D-cells
     */
    public PlaneMaker(String input_file_name)
    {         
        try
        {
            // Reading of information about parameters of cells from file
            TextTableFileReader reader = new TextTableFileReader(input_file_name);
            
            System.out.println("File with information about 3D-cells: "+input_file_name);
            
            specimenR3 = reader.convertToTable();

            // Reading of task name from file
            BufferedReader br = new BufferedReader(new FileReader(Common.TASK_PATH+"/"+
                                        Common.TASK_NAME_FILE+"."+Common.TASK_EXTENSION));

            //  Creation of list of names of materials of grains: index of element
            // corresponds to grain index, element value - to grain material.
            grain_materials = new ArrayList(0);

            //  Creation of list of names of all materials
            materials = new ArrayList(0);

            // Reading of parameters of task from file
            String task_file_name = Common.TASK_PATH+"/"+br.readLine()+"."+Common.TASK_EXTENSION;

            readTask(task_file_name);
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }
    }

    /** The constructor creates representative of class
     * for extracting of certain 3D-cells from specimen.
     * @param input_file_name file with information about parameters of 3D-cells
     * @param task_name name of task
     */
    public PlaneMaker(String input_file_name, String task_name)
    {
        Common.setTaskName(task_name);

        try
        {
            // Reading of information about parameters of cells from file
            TextTableFileReader reader = new TextTableFileReader(input_file_name);
            
            System.out.println("File with information about 3D cells: "+input_file_name);
            
            specimenR3 = reader.convertToTable();

            //  Creation of list of names of materials of grains: index of element
            // corresponds to grain index, element value - to grain material.
            grain_materials = new ArrayList(0);

            //  Creation of list of names of all materials
            materials = new ArrayList(0);

            // File with short information about task
            String task_info_file = Common.TASK_PATH+"/"+task_name+"/"+task_name+"."+Common.TASK_EXTENSION;

            // Reading of values of parameters from file with short information about task
            readTask(task_info_file);
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: "+io_exc);
        }
    }

    /** The method reads values of fields of the class from the file.
     * @param file_name name of file containing values of fields of the class
     */
    public void readTask(String file_name)
    {
        Properties task_properties = new Properties();

        // Material of cell
        String material;

        try
        {
            FileInputStream fin = new FileInputStream(file_name);
            task_properties.load(fin);
            fin.close();

            System.out.println("Name of read file:    "+file_name);

            // Reading of name of file with information about grain parameters
            grains_file         = task_properties.getProperty("grains_file_2");

            // Reading of name of file with minimal and maximal values of temperature and stress
            // for initial specimen including outer boundary cells
            extreme_values_file = task_properties.getProperty("extreme_values_file");

            // Reading of name of file with parameters of specimen layers
            layers_file_name = task_properties.getProperty("layers_file_name");

            // Reading of numbers of cells along axes
            cell_number_I       = new Integer(task_properties.getProperty("cell_number_X")).intValue();
            cell_number_J       = new Integer(task_properties.getProperty("cell_number_Y")).intValue();
            cell_number_K       = new Integer(task_properties.getProperty("cell_number_Z")).intValue();

            //  Reading of variable responsible for showing of inter granular cells
            // and cells on specimen boundaries
            show_grain_bounds   = (new Byte(task_properties.getProperty("show_grain_bounds"))).byteValue();

            // Reading of cell size
            cell_size           = new Double(task_properties.getProperty("cell_size")).doubleValue();

            // Reading of type of automata packing
            packing_type        = (new Byte(task_properties.getProperty("packing_type"))).byteValue();
      /*-------------------------------------------------------------------------------*/
            // Reading of information about grain parameters from file
            TextTableFileReader reader = new TextTableFileReader(grains_file);
            grains_table = reader.convertToTable();

            // Number of grains
            int grain_number = grains_table.getRowsNum();

            // Addition of material names to the list
            for(int grain_counter = 0; grain_counter<grain_number; grain_counter++)
            {
                material = grains_table.getCell(grain_counter, 1);

                grain_materials.add(material);

                if(!materials.contains(material))
                    materials.add(material);

                //TEST
            //    System.out.println(materials.get(grain_counter));
            }

            //TEST
       //     System.out.println("Number of grains: "+materials.size());

     /*-------------------------------------------------------------------------------*/
            fin = new FileInputStream(extreme_values_file);
            task_properties.load(fin);
            fin.close();

            System.out.println("Name of read file:    "+extreme_values_file);

            init_min_temperature = new Double(task_properties.getProperty("min_temperature")).doubleValue();
            init_max_temperature = new Double(task_properties.getProperty("max_temperature")).doubleValue();
            init_min_eff_stress  = new Double(task_properties.getProperty("min_mech_stress")).doubleValue();
            init_max_eff_stress  = new Double(task_properties.getProperty("max_mech_stress")).doubleValue();
            init_min_strain      = new Double(task_properties.getProperty("min_strain")).doubleValue();
            init_max_strain      = new Double(task_properties.getProperty("max_strain")).doubleValue();
     /*-------------------------------------------------------------------------------*/
            fin = new FileInputStream(layers_file_name);
            task_properties.load(fin);
            fin.close();

            System.out.println("Name of read file:    "+layers_file_name);

            // Thickness of 1st layer expressed in cell radiuses
            layer1_thickness = new Double(task_properties.getProperty("layer1_thickness")).doubleValue();

            // Thickness of 2nd layer expressed in cell radiuses
            layer2_thickness = new Double(task_properties.getProperty("layer2_thickness")).doubleValue();

            // Thickness of 3rd layer expressed in cell radiuses
            layer3_thickness = new Double(task_properties.getProperty("layer3_thickness")).doubleValue();

            // Size of element in 2nd layer expressed in cell radiuses
            layer2_elem_size = new Double(task_properties.getProperty("layer2_elem_size")).doubleValue();

            // Type of 2nd layer
            layer2_type = new Byte(task_properties.getProperty("layer2_type")).byteValue();

            // Transition of geometric parameters of layers to absolute units
            layer1_thickness = layer1_thickness*cell_size/2;
            layer2_thickness = layer2_thickness*cell_size/2;
            layer3_thickness = layer3_thickness*cell_size/2;
            layer2_elem_size = layer2_elem_size*cell_size/2;
        }
        catch(IOException io_ex)
        {
            System.out.println("ERROR: "+io_ex);
        }
    }

    /** Number of cells along axis X
     * @return number of cells along axis X
     */
    public int getCellNumberI()
    {
        return cell_number_I;
    }

    /** Number of cells along axis Y
     * @return number of cells along axis Y
     */
    public int getCellNumberJ()
    {
        return cell_number_J;
    }

    /** Number of cells along axis Z
     * @return number of cells along axis Z
     */
    public int getCellNumberK()
    {
        return cell_number_K;
    }

    /** The method returns information about specimen at current time step.
     * @return information about specimen at current time step
     */
    public StringVector getSpecimenR3()
    {
        return specimenR3;
    }

    /** The method returns list of names of all materials.
     * @return list of names of all materials
     */
    public ArrayList getMaterials()
    {
        return materials;
    }

    /** The method returns number of materials in specimen.
     * @return number of materials in specimen
     */
    public int getMaterialNumber()
    {
        return materials.size();
    }
    
    /** The method returns thickness of 1st specimen layer.
     * @return thickness of 1st specimen layer
     */
    public double getLayer1Thickness()
    {
        return layer1_thickness;
    }

    /** The method returns thickness of 2nd specimen layer.
     * @return thickness of 2nd specimen layer
     */
    public double getLayer2Thickness()
    {
        return layer2_thickness;
    }

    /** The method returns thickness of 3rd specimen layer.
     * @return thickness of 3rd specimen layer
     */
    public double getLayer3Thickness()
    {
        return layer3_thickness;
    }

    /** The method returns size of element in 2nd specimen layer.
     * @return size of element in 2nd specimen layer
     */
    public double getLayer2ElementSize()
    {
        return layer2_elem_size;
    }

    /** The method returns type of 2nd specimen layer.
     * @return type of 2nd specimen layer
     */
    public byte getLayer2Type()
    {
        return layer2_type;
    }

    /** The method returns type of automata packing.
     * @return type of automata packing
     */
    public byte getPackingType()
    {
        return packing_type;
    }

    /** The method returns two factors of cell size D: first one equals X,
     * second one equals Y (Y equals 10 in the power of Z, Z is whole number),
     * such that D = X*Y.
     * @return two factors of cell size D: first one equals X,
     * second one equals Y (Y equals 10 in the power of Z, Z is whole number),
     * such that D = X*Y
     */
    public double[] getCellSizeFactors()
    {
        // Whole exponent of power of 10
        double exponent = Math.floor(Math.log10(cell_size));

        // Mantissa of cell size
        double mantissa = cell_size*Math.pow(10, -exponent);

        // Array of two returned numbers
        double[] cell_size_factors = new double[2];

        cell_size_factors[0] = Math.pow(10, exponent);
        cell_size_factors[1] = mantissa;

        return cell_size_factors;
    }

    /** The method creates representative of the class.
     * @param args string of parameters
     */
    public static void main(String[] args)
    {
        // Name of task
        String task_name;

   //   task_name = "tension_X";
        task_name = "heat_all";

        // Name of file with parameters of cells at certain time step
        String file_name = "./user/task_db/"+task_name+"/"+task_name+"_10.res";

        // Cell with maximal parameters of plane
        VisualCellR3 max_cell;

        // Cell with minimal parameters of plane
        VisualCellR3 min_cell;
        
        // Variable responsible for drawing of all cells:
        // if it equals false then boundary cells are not shown,
        // if it equals true then all cells are shown.
        boolean _show_all_cells = true;
        
        // Representative of class for extracting of certain 3D-cells from specimen
        PlaneMaker plane_maker = new PlaneMaker(file_name, task_name);
/* --------------------------------------------------------------------------------- */
        //TEST #1
        System.out.println();
        System.out.println("TEST #1");
        System.out.println();

        // Three points located in plane "X = 10*cell_size"
        PointR3 point_1 = new PointR3(10, 40, 2);
        PointR3 point_2 = new PointR3(10, 20, 3);
        PointR3 point_3 = new PointR3(10, 30, 4);

        // Two sets (planes) of automata
        ArrayList[] planes = new ArrayList[2];

        // Maximal distance from extracted cell to given plane
        double max_distance = 0.5;

        // Extraction of set of automata (located at distance less than maximal distance
        // from plane) from specimen
        planes = plane_maker.extractPlane(point_1, point_2, point_3, max_distance, _show_all_cells);

     /* int plane_number=0;
        String axis="X";
        planes[0] = plane_maker.extractPlane(plane_number, axis, _show_all_cells);
     */

        // Set of cells in upper plane
        int cell_number_0 = planes[0].size();

        // Set of cells in lower plane
        int cell_number_1 = planes[1].size();

        System.out.println("Number of cells in upper plane: "+cell_number_0);
        System.out.println("Number of cells in lower plane: "+cell_number_1);

        // Cell with maximal parameters of plane
        max_cell = plane_maker.getMaxCellR3();

        // Cell with minimal parameters of plane
        min_cell = plane_maker.getMinCellR3();

        System.out.println("Min. coordinates:    "+min_cell.getCoordX()+" "+min_cell.getCoordY()+" "+min_cell.getCoordZ());
        System.out.println("Min. temperature:    "+min_cell.getTemperature());
        System.out.println("Min. eff. stress:    "+min_cell.getEffStress());
        System.out.println("Min. prin.stress:    "+min_cell.getPrinStress());
        System.out.println("Min. force moment X: "+min_cell.getForceMomentX());
        System.out.println("Min. force moment Y: "+min_cell.getForceMomentY());
        System.out.println("Min. force moment Z: "+min_cell.getForceMomentZ());
        System.out.println("Min. strain:         "+min_cell.getStrain());

        System.out.println("Max. coordinates:    "+max_cell.getCoordX()+" "+max_cell.getCoordY()+" "+max_cell.getCoordZ());
        System.out.println("Max. temperature:    "+max_cell.getTemperature());
        System.out.println("Max. eff. stress:    "+max_cell.getEffStress());
        System.out.println("Max. prin.stress:    "+max_cell.getPrinStress());
        System.out.println("Max. force moment X: "+max_cell.getForceMomentX());
        System.out.println("Max. force moment Y: "+max_cell.getForceMomentY());
        System.out.println("Max. force moment Z: "+max_cell.getForceMomentZ());
        System.out.println("Max. strain:         "+max_cell.getStrain());

        // Obtaining of cell from plane
        VisualCellR3 cell_in_plane = (VisualCellR3)planes[0].get(planes[0].size()/2);

        //Old coordinates of this cell
        double old_coord_X = cell_in_plane.getCoordX();
        double old_coord_Y = cell_in_plane.getCoordY();
        double old_coord_Z = cell_in_plane.getCoordZ();

        System.out.println("Old 3D coordinates: "+old_coord_X+" "+old_coord_Y+" "+old_coord_Z);

        // Calculation of 2D coordinates of this point in new basis located in the plane
        DoubleVector pointR2 = plane_maker.calcCoordinatesR2(old_coord_X, old_coord_Y, old_coord_Z,
                                                             plane_maker.getPlaneEquationCoeffs());

        System.out.println("New 2D coordinates: "+pointR2.getComponent(0)+" "+pointR2.getComponent(1));

        //END OF TEST #1
/* ---------------------------------------------------------------------------------*/
        //TEST #2
        System.out.println();
        System.out.println("TEST #2");
        System.out.println();

        // Obtaining of 3D-cells located in 3 first layers from specimen boundary
        ArrayList outer_layers = plane_maker.getOuterVisualCellsR3(_show_all_cells);

        System.out.println("Number of cells in outer layers: "+outer_layers.size());

        // Cell with maximal parameters of plane
        max_cell = plane_maker.getMaxCellR3();

        // Cell with minimal parameters of plane
        min_cell = plane_maker.getMinCellR3();

        System.out.println("Min. coordinates:    "+min_cell.getCoordX()+" "+min_cell.getCoordY()+" "+min_cell.getCoordZ());
        System.out.println("Min. temperature:    "+min_cell.getTemperature());
        System.out.println("Min. eff. stress:    "+min_cell.getEffStress());
        System.out.println("Min. prin.stress:    "+min_cell.getPrinStress());
        System.out.println("Min. force moment X: "+min_cell.getForceMomentX());
        System.out.println("Min. force moment Y: "+min_cell.getForceMomentY());
        System.out.println("Min. force moment Z: "+min_cell.getForceMomentZ());
        System.out.println("Min. strain:         "+min_cell.getStrain());
        
        System.out.println("Max. coordinates:    "+max_cell.getCoordX()+" "+max_cell.getCoordY()+" "+max_cell.getCoordZ());
        System.out.println("Max. temperature:    "+max_cell.getTemperature());
        System.out.println("Max. eff. stress:    "+max_cell.getEffStress());
        System.out.println("Max. prin.stress:    "+max_cell.getPrinStress());
        System.out.println("Max. force moment X: "+max_cell.getForceMomentX());
        System.out.println("Max. force moment Y: "+max_cell.getForceMomentY());
        System.out.println("Max. force moment Z: "+max_cell.getForceMomentZ());
        System.out.println("Max. strain:         "+max_cell.getStrain());

        double coord_X;
        double coord_Y;
        double coord_Z;

        // Obtaining of cells from plane
        // for(int cell_index = 0; cell_index<20; cell_index++)
        {
            int cell_index = outer_layers.size()/2;

            VisualCellR3 vis_cell = (VisualCellR3)outer_layers.get(cell_index);

            coord_X = vis_cell.getCoordX();
            coord_Y = vis_cell.getCoordY();
            coord_Z = vis_cell.getCoordZ();

            System.out.print("Cell index: "+cell_index+"; cell coordinates: "+coord_X+" "+coord_Y+" "+coord_Z);
            System.out.println("; grain index: "+vis_cell.getGrainIndex()+"; grain material: "+vis_cell.getMaterial());
        }
        //END OF TEST #2
  /* ---------------------------------------------------------------------------------*/
        //TEST #3
        System.out.println();
        System.out.println("TEST #3");
        System.out.println();

        // Factors consisting cell size
        double[] factors = plane_maker.getCellSizeFactors();

        System.out.println("Cell size: "+factors[1]+"E"+(int)Math.log10(factors[0]));
        //END OF TEST #3
  /* ---------------------------------------------------------------------------------*/
        //TEST #4
        System.out.println();
        System.out.println("TEST #4");
        System.out.println();

        System.out.println("Layer 1 thickness:    "+plane_maker.getLayer1Thickness());
        System.out.println("Layer 2 thickness:    "+plane_maker.getLayer2Thickness());
        System.out.println("Layer 3 thickness:    "+plane_maker.getLayer3Thickness());
        System.out.println("Layer 2 element size: "+plane_maker.getLayer2ElementSize());
        System.out.println("Layer 2 type:         "+plane_maker.getLayer2Type());

        //END OF TEST #4
  /* ---------------------------------------------------------------------------------*/
        //TEST #5
        System.out.println();
        System.out.println("TEST #5");
        System.out.println();

        int plane_index = 5;
        String axis = "X";

        // Extraction of set of automata (located at distance less than maximal distance
        // from plane) from specimen
        ArrayList plane = plane_maker.extractPlane(plane_index, axis, _show_all_cells);

        System.out.println("Number of cells in plane: "+plane.size());

        // Cell with maximal parameters of plane
        max_cell = plane_maker.getMaxCellR3();

        // Cell with minimal parameters of plane
        min_cell = plane_maker.getMinCellR3();

        System.out.println("Min. coordinates:    "+min_cell.getCoordX()+" "+min_cell.getCoordY()+" "+min_cell.getCoordZ());
        System.out.println("Min. temperature:    "+min_cell.getTemperature());
        System.out.println("Min. eff. stress:    "+min_cell.getEffStress());
        System.out.println("Min. prin.stress:    "+min_cell.getPrinStress());
        System.out.println("Min. force moment X: "+min_cell.getForceMomentX());
        System.out.println("Min. force moment Y: "+min_cell.getForceMomentY());
        System.out.println("Min. force moment Z: "+min_cell.getForceMomentZ());
        System.out.println("Min. strain:         "+min_cell.getStrain());

        System.out.println("Max. coordinates:    "+max_cell.getCoordX()+" "+max_cell.getCoordY()+" "+max_cell.getCoordZ());
        System.out.println("Max. temperature:    "+max_cell.getTemperature());
        System.out.println("Max. eff. stress:    "+max_cell.getEffStress());
        System.out.println("Max. prin.stress:    "+max_cell.getPrinStress());
        System.out.println("Max. force moment X: "+max_cell.getForceMomentX());
        System.out.println("Max. force moment Y: "+max_cell.getForceMomentY());
        System.out.println("Max. force moment Z: "+max_cell.getForceMomentZ());
        System.out.println("Max. strain:         "+max_cell.getStrain());

        // Obtaining of cell from plane
        cell_in_plane = (VisualCellR3)plane.get(plane.size()/2);

        //Old coordinates of this cell
        old_coord_X = cell_in_plane.getCoordX();
        old_coord_Y = cell_in_plane.getCoordY();
        old_coord_Z = cell_in_plane.getCoordZ();

        System.out.println("Old 3D coordinates: "+old_coord_X+" "+old_coord_Y+" "+old_coord_Z);

        // Calculation of 2D coordinates of this point in new basis located in the plane
        pointR2 = plane_maker.calcCoordinatesR2(old_coord_X, old_coord_Y, old_coord_Z,
                                                             plane_maker.getPlaneEquationCoeffs());

        System.out.println("New 2D coordinates: "+pointR2.getComponent(0)+" "+pointR2.getComponent(1));
        //END OF TEST #5
  /* ---------------------------------------------------------------------------------*/
        //TEST #6
        System.out.println();
        System.out.println("TEST #6: extraction of line");
        System.out.println();

        // Two points located in line
        point_1 = new PointR3(0, 4.5, 0);
        point_2 = new PointR3(1, 4.5, 1);

        // Three lists corresponding to extracted cells
        ArrayList[] cell_lists = new ArrayList[3];

        // Maximal distance from extracted cell to given line
        if(plane_maker.getPackingType() == Common.HEXAGONAL_CLOSE_PACKING)
            max_distance = Math.sqrt(3.0/8.0);

        if(plane_maker.getPackingType() == Common.SIMPLE_CUBIC_PACKING)
            max_distance = 0.5;

        // Extraction of set of automata (located at distance less than maximal distance
        // from line) from specimen
        cell_lists = plane_maker.extractLine(point_1, point_2, max_distance, _show_all_cells);

        // Set of automata
        ArrayList cells = cell_lists[0];

        // Set of line 3D points corresponding to extracted cells
        ArrayList line_points = cell_lists[1];

        // Set of parameters corresponding to calculated line 3D points
        ArrayList line_params = cell_lists[2];

        // Number of cells in line
        int cell_number = cells.size();

        System.out.println("Number of cells in line: "+cell_number);

        // Cell with maximal parameters of line
        max_cell = plane_maker.getMaxCellR3();

        // Cell with minimal parameters of plane
        min_cell = plane_maker.getMinCellR3();

        System.out.println("Min. coordinates:    "+min_cell.getCoordX()+" "+min_cell.getCoordY()+" "+min_cell.getCoordZ());
        System.out.println("Min. temperature:    "+min_cell.getTemperature());
        System.out.println("Min. eff. stress:    "+min_cell.getEffStress());
        System.out.println("Min. prin.stress:    "+min_cell.getPrinStress());
        System.out.println("Min. force moment X: "+min_cell.getForceMomentX());
        System.out.println("Min. force moment Y: "+min_cell.getForceMomentY());
        System.out.println("Min. force moment Z: "+min_cell.getForceMomentZ());
        System.out.println("Min. strain:         "+min_cell.getStrain());

        System.out.println("Max. coordinates:    "+max_cell.getCoordX()+" "+max_cell.getCoordY()+" "+max_cell.getCoordZ());
        System.out.println("Max. temperature:    "+max_cell.getTemperature());
        System.out.println("Max. eff. stress:    "+max_cell.getEffStress());
        System.out.println("Max. prin.stress:    "+max_cell.getPrinStress());
        System.out.println("Max. force moment X: "+max_cell.getForceMomentX());
        System.out.println("Max. force moment Y: "+max_cell.getForceMomentY());
        System.out.println("Max. force moment Z: "+max_cell.getForceMomentZ());
        System.out.println("Max. strain:         "+max_cell.getStrain());

        // Number of extracted cells
        int line_cell_number = cells.size();

        // Obtaining of cell from line
        VisualCellR3 cell_in_line;
    //    cell_in_line = (VisualCellR3)cells.get(line_cell_number/2);

        // Extracted cell coordinates
        double cell_coord_X, cell_coord_Y, cell_coord_Z;

        // Line 3D point corresponding to extracted cell
        PointR3 line_pointR3;

        // Copordinates of line point corresponding to extracted cell
        double line_point_X, line_point_Y, line_point_Z;

        // Line parameter corresponding to obtained 3D point
        double line_param;

        // Obtaining of all cells from plane
        for(int cell_counter = 0; cell_counter<line_cell_number; cell_counter++)
        {
            // Extracted cell
            cell_in_line = (VisualCellR3)cells.get(cell_counter);

            // Cell coordinates
            cell_coord_X = (int)Math.round(cell_in_line.getCoordX()*1000)/1000.0;
            cell_coord_Y = (int)Math.round(cell_in_line.getCoordY()*1000)/1000.0;
            cell_coord_Z = (int)Math.round(cell_in_line.getCoordZ()*1000)/1000.0;
            System.out.print("Cell #"+(cell_counter+1)+". Centre point: "+
                             cell_coord_X+" "+cell_coord_Y+" "+cell_coord_Z+"; ");

            // Coordinates of line point nearest to this cell
            line_pointR3 = (PointR3)line_points.get(cell_counter);

            line_point_X = (int)Math.round(line_pointR3.getX()*1000)/1000.0;
            line_point_Y = (int)Math.round(line_pointR3.getY()*1000)/1000.0;
            line_point_Z = (int)Math.round(line_pointR3.getZ()*1000)/1000.0;
            System.out.print("line point: "+line_point_X+" "+line_point_Y+" "+line_point_Z+"; ");

            // Line parameter of line point nearest to cell
            line_param   = ((Double)line_params.get(cell_counter)).doubleValue();
            line_param   = (int)Math.round(line_param*1000)/1000.0;
            System.out.println("line parameter: "+line_param);
        }
        //END OF TEST #6
      /*-------------------------------------------------------------------------------*/
    }

    /** The method returns cell containing minimal coordinates and parameters
     * of cells in plane.
     * @return cell containing minimal coordinates and parameters of plane
     */
    public VisualCellR3 getMinCellR3()
    {
        return min_cellR3;
    }

    /** The method returns cell containing maximal coordinates and parameters
     * of cells in plane.
     * @return cell containing maximal coordinates and parameters of plane
     */
    public VisualCellR3 getMaxCellR3()
    {
        return max_cellR3;
    }

    /** The method extracts 3D-cells, which distance from plane
     * containing certain three 3D-points is less than maximal distance.
     * @param point_1 1st 3D-point contained in plane
     * @param point_2 2nd 3D-point contained in plane
     * @param point_3 3rd 3D-point contained in plane,
     * @param max_distance maximal distance from plane to each of extracted 3D-cells
     *        (expressed in diameters of cell)
     * @param _show_all_cells variable responsible for drawing of all cells
     * @return two sets of 3D-cells, which distance from plane
     *         containing certain three 3D-points is less than maximal distance:
     *         1st set contains cells located under the plane,
     *         2nd set contains cells located above the plane
     */
    public void findExtremeValues(boolean _show_all_cells)
    {
        // Total number of cells
        int cell_number = specimenR3.getRowsNum();        
        
        System.out.println("PlaneMaker.findExtremeValues(boolean _show_all_cells): total number of cells = " + cell_number);

        // Index of grain containing cell
        int grain_index;

        // Type of cell location
        byte location_type;

        /** Coordinate X of cell */
        double coord_X;

        /** Coordinate Y of cell */
        double coord_Y;

        /** Coordinate Z of cell */
        double coord_Z;

        /** Temperature of cell */
        double temperature;

        /** "Effective" stress of cell */
        double eff_stress;

        /** Principal stress of cell */
        double prin_stress;

        /** Coordinate X of force moment of cell */
        double force_moment_X;

        /** Coordinate Y of force moment of cell */
        double force_moment_Y;

        /** Coordinate Z of force moment of cell */
        double force_moment_Z;

        /** Cell state (elastic, plastic or damaged) */
        byte state;

        /** Strain of cell */
        double strain;

        /** Disclination density of cell */
        double discl_density;

        // Maximal coordinates and parameters of cells in plane
        double  max_coord_X        = 0,
                max_coord_Y        = 0,
                max_coord_Z        = 0,
                max_temperature    = init_max_temperature,
                max_eff_stress     = init_max_eff_stress,
                max_prin_stress    = 0,
                max_force_moment_X = 0,
                max_force_moment_Y = 0,
                max_force_moment_Z = 0,
                max_strain         = init_max_strain,
                max_discl_density  = 0;

        // Minimal coordinates and parameters of cells in plane
        double  min_coord_X        = 0,
                min_coord_Y        = 0,
                min_coord_Z        = 0,
                min_temperature    = init_min_temperature,
                min_eff_stress     = init_min_eff_stress,
                min_prin_stress    = 0,
                min_force_moment_X = 0,
                min_force_moment_Y = 0,
                min_force_moment_Z = 0,
                min_strain         = init_min_strain,
                min_discl_density  = 0;

        // Counter of inner cells
        int inner_cell_counter = 0;

        // Cycle over all cells in specimen
        for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
        {
          // Type of cell location
          location_type  = (new Byte(specimenR3.getCell(cell_counter, 1))).byteValue();

        //  if((location_type!=Common.OUTER_CELL)|(_show_all_cells))
          if((location_type==Common.INTRAGRANULAR_CELL)|(location_type==Common.INTERGRANULAR_CELL))
          {
            //Cell coordinates
            coord_X        = (new Double(specimenR3.getCell(cell_counter, 3))).doubleValue();
            coord_Y        = (new Double(specimenR3.getCell(cell_counter, 4))).doubleValue();
            coord_Z        = (new Double(specimenR3.getCell(cell_counter, 5))).doubleValue();

            //Cell parameters
            grain_index    = (new Integer(specimenR3.getCell(cell_counter, 2))).intValue();
            temperature    = (new Double(specimenR3.getCell(cell_counter, 6))).doubleValue();
            eff_stress     = (new Double(specimenR3.getCell(cell_counter, 7))).doubleValue();
            prin_stress    = (new Double(specimenR3.getCell(cell_counter, 8))).doubleValue();
            force_moment_X = (new Double(specimenR3.getCell(cell_counter, 9))).doubleValue();
            force_moment_Y = (new Double(specimenR3.getCell(cell_counter,10))).doubleValue();
            force_moment_Z = (new Double(specimenR3.getCell(cell_counter,11))).doubleValue();
            state          = (new Byte(specimenR3.getCell(cell_counter,12))).byteValue();
            strain         = (new Double(specimenR3.getCell(cell_counter, 13))).doubleValue();
            discl_density  = (new Double(specimenR3.getCell(cell_counter, 14))).doubleValue();

            // Calculation of minimal values
            if(coord_X<min_coord_X)
                min_coord_X     = coord_X;

            if(coord_Y<min_coord_Y)
                min_coord_Y     = coord_Y;

            if(coord_Z<min_coord_Z)
                min_coord_Z     = coord_Z;

            if(temperature<min_temperature)
                    min_temperature = temperature;

            if(eff_stress<min_eff_stress)
                min_eff_stress  = eff_stress;

            if(prin_stress<min_prin_stress)
                min_prin_stress = prin_stress;

            if(force_moment_X<min_force_moment_X)
                min_force_moment_X = force_moment_X;

            if(force_moment_Y<min_force_moment_Y)
                min_force_moment_Y = force_moment_Y;

            if(force_moment_Z<min_force_moment_Z)
                min_force_moment_Z = force_moment_Z;

            if(strain<min_strain)
                min_strain = strain;

            if(discl_density<min_discl_density)
                min_discl_density = discl_density;

            //Creation of the cell with minimal parameters
            min_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0,
                                 min_coord_X, min_coord_Y, min_coord_Z, min_temperature,
                                 min_eff_stress, min_prin_stress, min_force_moment_X,
                                 min_force_moment_Y, min_force_moment_Z, min_strain, min_discl_density);

            // Calculation of maximal values
            if(coord_X>max_coord_X)
                max_coord_X = coord_X;

            if(coord_Y>max_coord_Y)
                max_coord_Y = coord_Y;

            if(coord_Z>max_coord_Z)
                max_coord_Z = coord_Z;

            if(temperature>max_temperature)
                max_temperature = temperature;

            if(eff_stress>max_eff_stress)
                max_eff_stress = eff_stress;

            if(prin_stress>max_prin_stress)
                max_prin_stress = prin_stress;

            if(force_moment_X>max_force_moment_X)
                max_force_moment_X = force_moment_X;

            if(force_moment_Y>max_force_moment_Y)
                max_force_moment_Y = force_moment_Y;

            if(force_moment_Z>max_force_moment_Z)
                max_force_moment_Z = force_moment_Z;

            if(strain>max_strain)
                max_strain = strain;

            if(discl_density>max_discl_density)
                max_discl_density = discl_density;

            //Creation of the cell with maximal parameters
            max_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0,
                              max_coord_X, max_coord_Y, max_coord_Z, max_temperature,
                              max_eff_stress, max_prin_stress, max_force_moment_X,
                              max_force_moment_Y, max_force_moment_Z, max_strain, max_discl_density);

            // Calculation of number of cells in plane
            inner_cell_counter++;
          }
        }

        System.out.println();
        System.out.println("Number of inner cells: "+inner_cell_counter);
    }

    /** The method extracts 3D-cells, which distance from plane
     * containing certain three 3D-points is less than maximal distance.
     * @param point_1 1st 3D-point contained in plane
     * @param point_2 2nd 3D-point contained in plane
     * @param point_3 3rd 3D-point contained in plane,
     * @param max_distance maximal distance from plane to each of extracted 3D-cells
     *        (expressed in diameters of cell)
     * @param _show_all_cells variable responsible for drawing of all cells
     * @return two sets of 3D-cells, which distance from plane
     *         containing certain three 3D-points is less than maximal distance:
     *         1st set contains cells located under the plane,
     *         2nd set contains cells located above the plane
     */
    public ArrayList[] extractPlane(PointR3 point_1, PointR3 point_2, PointR3 point_3, 
                                    double max_distance, boolean _show_all_cells)
    {
        if(max_distance<0)
        {
            System.out.println("ERROR! The value of distance cannot be negative!");
            return null;
        }

        // Calculation of coefficients of equation of plane containing three 3D-points
        calcPlaneEquationCoeffs(point_1, point_2, point_3);

        // Coefficients of the equation
        double coeff_A = plane_coeffs[0];
        double coeff_B = plane_coeffs[1];
        double coeff_C = plane_coeffs[2];
        double coeff_D = plane_coeffs[3];

        // Positive value for normalization of the coefficients
        double norm = Math.sqrt(coeff_A*coeff_A+coeff_B*coeff_B+coeff_C*coeff_C);

        // Unit normal vector of plane
        VectorR3 norm_vector = new VectorR3(coeff_A/norm, coeff_B/norm, coeff_C/norm);

        // Total number of cells
        int cell_number = specimenR3.getRowsNum();
        
        System.out.println("PlaneMaker.extractPlane(PointR3 point_1, PointR3 point_2, PointR3 point_3, \n" +
"                                    double max_distance, boolean _show_all_cells): total number of cells = " + cell_number);

        VisualCellR3 cellR3;

        // The set contains cells located under the plane
        //on the distance less than the maximal one
        ArrayList high_cells = new ArrayList(0);

        // The set contains cells located above the plane
        //on the distance less than the maximal one
        ArrayList low_cells = new ArrayList(0);

        // Index of grain containing cell
        int grain_index;

        // Type of cell energy interaction with neighbours
        byte energy_type;

        // Type of cell location
        byte location_type;

        /** Coordinate X of cell */
        double coord_X;

        /** Coordinate Y of cell */
        double coord_Y;

        /** Coordinate Z of cell */
        double coord_Z;

        /** Temperature of cell */
        double temperature;

        /** "Effective" stress of cell */
        double eff_stress;

        /** Principal stress of cell */
        double prin_stress;

        /** Coordinate X of force moment of cell */
        double force_moment_X;

        /** Coordinate Y of force moment of cell */
        double force_moment_Y;

        /** Coordinate Z of force moment of cell */
        double force_moment_Z;

        /** Cell state (elastic, plastic or damaged) */
        byte state;

        /** Strain of cell */
        double strain;

        /** Disclination density of cell*/
        double discl_density;

        // Distance from cell to the plane
        double distance = 0;

        // Maximal coordinates and parameters of cells in plane
        double  max_coord_X=0,
                max_coord_Y=0,
                max_coord_Z=0,
                max_temperature=init_max_temperature,
                max_eff_stress=init_max_eff_stress,
                max_prin_stress=0,
                max_force_moment_X=0,
                max_force_moment_Y=0,
                max_force_moment_Z=0,
                max_strain = init_max_strain,
                max_discl_density = 0;

        // Minimal coordinates and parameters of cells in plane
        double  min_coord_X=0,
                min_coord_Y=0,
                min_coord_Z=0,
                min_temperature=init_min_temperature,
                min_eff_stress=init_min_eff_stress,
                min_prin_stress=0,
                min_force_moment_X=0,
                min_force_moment_Y=0,
                min_force_moment_Z=0,
                min_strain = init_min_strain,
                min_discl_density = 0;

        // Counter of cells in plane
        int plane_cells_counter = 0;

        // Cycle over all cells in specimen
        for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
        {
          // Type of cell energy interaction with neighbours
          energy_type  = (new Byte(specimenR3.getCell(cell_counter, 0))).byteValue();

          // Type of cell location
          location_type  = (new Byte(specimenR3.getCell(cell_counter, 1))).byteValue();

          if(((location_type!=Common.OUTER_CELL)&(energy_type!=Common.ADIABATIC_ADIABATIC_CELL))|(_show_all_cells))
          {
            //Cell coordinates
            coord_X        = (new Double(specimenR3.getCell(cell_counter, 3))).doubleValue();
            coord_Y        = (new Double(specimenR3.getCell(cell_counter, 4))).doubleValue();
            coord_Z        = (new Double(specimenR3.getCell(cell_counter, 5))).doubleValue();

            // Distance from the cell to the plane (can be negative)
            distance       = (coeff_A*coord_X + coeff_B*coord_Y + coeff_C*coord_Z + coeff_D)/norm;

            if(((distance< 0)&(distance>=-max_distance))|
               ((distance>=0)&(distance<  max_distance)))
       //     if(Math.abs(distance) <= max_distance)
            {
                //Cell parameters
                grain_index    = (new Integer(specimenR3.getCell(cell_counter, 2))).intValue();
                temperature    = (new Double(specimenR3.getCell(cell_counter, 6))).doubleValue();
                eff_stress     = (new Double(specimenR3.getCell(cell_counter, 7))).doubleValue();
                prin_stress    = (new Double(specimenR3.getCell(cell_counter, 8))).doubleValue();
                force_moment_X = (new Double(specimenR3.getCell(cell_counter, 9))).doubleValue();
                force_moment_Y = (new Double(specimenR3.getCell(cell_counter,10))).doubleValue();
                force_moment_Z = (new Double(specimenR3.getCell(cell_counter,11))).doubleValue();
                state          = (new Byte(specimenR3.getCell(cell_counter,12))).byteValue();
                strain         = (new Double(specimenR3.getCell(cell_counter, 13))).doubleValue();
                discl_density  = (new Double(specimenR3.getCell(cell_counter, 14))).doubleValue();

                //Calculation of coordinates of proection of the point to the plane
                coord_X = coord_X - distance*norm_vector.getX();
                coord_Y = coord_Y - distance*norm_vector.getY();
                coord_Z = coord_Z - distance*norm_vector.getZ();

                //Creation of the cell
                cellR3 = new VisualCellR3((String)grain_materials.get(grain_index-1), energy_type, grain_index, location_type, state, coord_X, coord_Y, coord_Z,
                                          temperature, eff_stress, prin_stress,
                                          force_moment_X, force_moment_Y, force_moment_Z, strain, discl_density);

                // Cells located under the plane
                if(distance>=0)
                    high_cells.add(cellR3);

                // Cells located above the plane
                if(distance<=0)
                    low_cells.add(cellR3);

                // Calculation of minimal values
                if//((coord_X<min_coord_X)|(plane_cells_counter==0))
                  (coord_X<min_coord_X)
                    min_coord_X = coord_X;

                if//((coord_Y<min_coord_Y)|(plane_cells_counter==0))
                  (coord_Y<min_coord_Y)
                    min_coord_Y = coord_Y;

                if//((coord_Z<min_coord_Z)|(plane_cells_counter==0))
                  (coord_Z<min_coord_Z)
                    min_coord_Z = coord_Z;

                if//((temperature<min_temperature)|(plane_cells_counter==0))
                  (temperature<min_temperature)
                    min_temperature = temperature;

                if//((eff_stress<min_eff_stress)|(plane_cells_counter==0))
                  (eff_stress<min_eff_stress)
                    min_eff_stress = eff_stress;

                if//((prin_stress<min_prin_stress)|(plane_cells_counter==0))
                  (prin_stress<min_prin_stress)
                    min_prin_stress = prin_stress;

                if//((force_moment_X<min_force_moment_X)|(plane_cells_counter==0))
                  (force_moment_X<min_force_moment_X)
                    min_force_moment_X = force_moment_X;

                if//((force_moment_Y<min_force_moment_Y)|(plane_cells_counter==0))
                  (force_moment_Y<min_force_moment_Y)
                    min_force_moment_Y = force_moment_Y;

                if//((force_moment_Z<min_force_moment_Z)|(plane_cells_counter==0))
                  (force_moment_Z<min_force_moment_Z)
                    min_force_moment_Z = force_moment_Z;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (strain<min_strain)
                    min_strain = strain;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (discl_density<min_discl_density)
                    min_discl_density = discl_density;

                //Creation of the cell with minimal parameters
                min_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, min_coord_X, min_coord_Y, min_coord_Z, min_temperature,
                     min_eff_stress, min_prin_stress, min_force_moment_X, min_force_moment_Y, min_force_moment_Z, min_strain, discl_density);

                // Calculation of maximal values
                if//((coord_X>max_coord_X)|(plane_cells_counter==0))
                    (coord_X>max_coord_X)
                    max_coord_X = coord_X;

                if//((coord_Y>max_coord_Y)|(plane_cells_counter==0))
                    (coord_Y>max_coord_Y)
                    max_coord_Y = coord_Y;

                if//((coord_Z>max_coord_Z)|(plane_cells_counter==0))
                    (coord_Z>max_coord_Z)
                    max_coord_Z = coord_Z;

                if//((temperature>max_temperature)|(plane_cells_counter==0))
                    (temperature>max_temperature)
                    max_temperature = temperature;

                if//((eff_stress>max_eff_stress)|(plane_cells_counter==0))
                    (eff_stress>max_eff_stress)
                    max_eff_stress = eff_stress;

                if//((prin_stress>max_prin_stress)|(plane_cells_counter==0))
                    (prin_stress>max_prin_stress)
                    max_prin_stress = prin_stress;

                if//((force_moment_X>max_force_moment_X)|(plane_cells_counter==0))
                    (force_moment_X>max_force_moment_X)
                    max_force_moment_X = force_moment_X;

                if//((force_moment_Y>max_force_moment_Y)|(plane_cells_counter==0))
                    (force_moment_Y>max_force_moment_Y)
                    max_force_moment_Y = force_moment_Y;

                if//((force_moment_Z>max_force_moment_Z)|(plane_cells_counter==0))
                    (force_moment_Z>max_force_moment_Z)
                    max_force_moment_Z = force_moment_Z;

                if//((strain<max_strain)|(plane_cells_counter==0))
                   (strain>max_strain)
                    max_strain = strain;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (discl_density>max_discl_density)
                    max_discl_density = discl_density;

                //Creation of the cell with maximal parameters
                max_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, max_coord_X, max_coord_Y, max_coord_Z, max_temperature,
                     max_eff_stress, max_prin_stress, max_force_moment_X, max_force_moment_Y, max_force_moment_Z, max_strain, max_discl_density);

                // Calculation of number of cells in plane
                plane_cells_counter++;
            }
          }
        }

        System.out.println();
        System.out.println("Number of cells in plane: "+plane_cells_counter);

        // Set of cells located under the plane and above it
        ArrayList[] cell_sets = new ArrayList[2];

        // Set of cells located under the plane on the distance less than the maximal one
        cell_sets[0] = high_cells;

        // Set of cells located above the plane on the distance less than the maximal one
        cell_sets[1] = low_cells;

        if(max_distance>=0)
        {
            return cell_sets;
        }
        else
            return null;
    }

    /** The method extracts 3D-cells with similar index I (index J, index K)
     * @param plane_number number of plane of cells along chosen axis
     * @param axis axis perpendicular to the extracted plane
     * @param _show_all_cells variable responsible for showing of all cells
     * @return 3D-cells with similar index I (index J, index K)
     */
    public ArrayList extractPlane(int plane_number, String axis, boolean _show_all_cells)
    {
        // Total number of cells
        int cell_number = specimenR3.getRowsNum();
        
        System.out.println("PlaneMaker.extractPlane(int plane_number, String axis, boolean _show_all_cells): total number of cells = " + cell_number);

        VisualCellR3 cellR3;

        // The set contains cells located under the plane
        //on the distance less than the maximal one
        ArrayList visual_cells = new ArrayList(0);

        // Type of cell energy interaction with neighbours
        byte energy_type;

        // Index of grain containing cell
        int grain_index;

        // Type of cell location
        byte location_type;

        /** Coordinate X of cell */
        double coord_X=0;

        /** Coordinate Y of cell */
        double coord_Y=0;

        /** Coordinate Z of cell */
        double coord_Z=0;

        /** Temperature of cell */
        double temperature;

        /** "Effective" stress of cell */
        double eff_stress;

        /** Principal stress of cell */
        double prin_stress;

        /** Coordinate X of force moment of cell */
        double force_moment_X;

        /** Coordinate Y of force moment of cell */
        double force_moment_Y;

        /** Coordinate Z of force moment of cell */
        double force_moment_Z;

        /** Cell state (elastic, plastic or damaged) */
        byte state;

        /** Strain of cell */
        double strain;

        /** Disclination density of cell */
        double discl_density;

        // Distance from cell to the plane
        double distance = 0;

        // Maximal coordinates and parameters of cells in plane
        double  max_coord_X=0,
                max_coord_Y=0,
                max_coord_Z=0,
                max_temperature=init_max_temperature,
                max_eff_stress=init_max_eff_stress,
                max_prin_stress=0,
                max_force_moment_X=0,
                max_force_moment_Y=0,
                max_force_moment_Z=0,
               max_strain = init_max_strain,
                min_discl_density = 0;


        // Minimal coordinates and parameters of cells in plane
        double  min_coord_X=0,
                min_coord_Y=0,
                min_coord_Z=0,
                min_temperature=init_min_temperature,
                min_eff_stress=init_min_eff_stress,
                min_prin_stress=0,
                min_force_moment_X=0,
                min_force_moment_Y=0,
                min_force_moment_Z=0,
                min_strain = init_min_strain,
                max_discl_density = 0;

        // Counter of cells in plane
        int plane_cells_counter = 0;

        // Triple index of cell
        Three indices;

        int index_I, index_J, index_K;

        // Coordinate of extracted plane
        double plane_coord = 0;
        int plane_cell_counter = 0;

        boolean first_cell = true;
        
        // Cycle over all cells in specimen
        for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
        {
          // Type of cell energy interaction with neighbours
          energy_type    = (new Byte(specimenR3.getCell(cell_counter, 0))).byteValue();

          // Type of cell location
          location_type  = (new Byte(specimenR3.getCell(cell_counter, 1))).byteValue();

          if((location_type != Common.OUTER_CELL & energy_type != Common.ADIABATIC_ADIABATIC_CELL) | _show_all_cells)
          {
            //Cell coordinates
            coord_X        = (new Double(specimenR3.getCell(cell_counter, 3))).doubleValue();
            coord_Y        = (new Double(specimenR3.getCell(cell_counter, 4))).doubleValue();
            coord_Z        = (new Double(specimenR3.getCell(cell_counter, 5))).doubleValue();

            // Indices of cell
            indices = calcTripleIndex(coord_X, coord_Y, coord_Z);

            index_I = indices.getI();
            index_J = indices.getJ();
            index_K = indices.getK();

       //     System.out.println(index_I+" "+index_J+" "+index_K);

            // Extracting of plane of cells with similar index I (index J, index K)
            if((axis.equals("X")&(index_I==plane_number))|
               (axis.equals("Y")&(index_J==plane_number))|
               (axis.equals("Z")&(index_K==plane_number)))
            {
                // Cell parameters
                grain_index    = (new Integer(specimenR3.getCell(cell_counter, 2))).intValue();
                temperature    = (new Double(specimenR3.getCell(cell_counter, 6))).doubleValue();
                eff_stress     = (new Double(specimenR3.getCell(cell_counter, 7))).doubleValue();
                prin_stress    = (new Double(specimenR3.getCell(cell_counter, 8))).doubleValue();
                force_moment_X = (new Double(specimenR3.getCell(cell_counter, 9))).doubleValue();
                force_moment_Y = (new Double(specimenR3.getCell(cell_counter,10))).doubleValue();
                force_moment_Z = (new Double(specimenR3.getCell(cell_counter,11))).doubleValue();
                state          = (new Byte(specimenR3.getCell(cell_counter,12))).byteValue();
                strain         = (new Double(specimenR3.getCell(cell_counter, 13))).doubleValue();
                discl_density  = (new Double(specimenR3.getCell(cell_counter, 14))).doubleValue();
                
                if(location_type == Common.OUTER_CELL | energy_type == Common.ADIABATIC_ADIABATIC_CELL)
                {
                    temperature    = 0;
                    eff_stress     = 0;
                    prin_stress    = 0;
                    force_moment_X = 0;
                    force_moment_Y = 0;
                    force_moment_Z = 0;
                    strain         = 0;
                    discl_density  = 0;
                } 
                
                //Creation of the cell
                cellR3 = new VisualCellR3((String)grain_materials.get(grain_index-1), energy_type, grain_index, location_type, state, coord_X, coord_Y, coord_Z,
                                          temperature, eff_stress, prin_stress,
                                          force_moment_X, force_moment_Y, force_moment_Z, strain, discl_density);
                
                plane_cell_counter++;
                
                if(plane_cell_counter % 100 == 0)
                    System.out.println("Plane # "+plane_number+"; cell # "+plane_cell_counter+"; coordinates: "+coord_X+" "+coord_Y+" "+coord_Z+
                                       "; discl_density = "+discl_density);
                
                // Cells located in the plane
                visual_cells.add(cellR3);

                if(first_cell)
                {
                    min_coord_X = coord_X;
                    min_coord_Y = coord_Y;
                    min_coord_Z = coord_Z;
                    min_temperature = temperature;
                    min_eff_stress  = eff_stress;
                    min_prin_stress = prin_stress;
                    min_force_moment_X = force_moment_X;
                    min_force_moment_Y = force_moment_Y;
                    min_force_moment_Z = force_moment_Z;
                    min_strain = strain;
                    min_discl_density = discl_density;

                    max_coord_X = coord_X;
                    max_coord_Y = coord_Y;
                    max_coord_Z = coord_Z;
                    max_temperature = temperature;
                    max_eff_stress  = eff_stress;
                    max_prin_stress = prin_stress;
                    max_force_moment_X = force_moment_X;
                    max_force_moment_Y = force_moment_Y;
                    max_force_moment_Z = force_moment_Z;
                    max_strain = strain;
                    max_discl_density = discl_density;

                    first_cell = false;
                }

                // Calculation of minimal values
                if//((coord_X<min_coord_X)|(plane_cells_counter==0))
                  (coord_X<min_coord_X)
                    min_coord_X = coord_X;

                if//((coord_Y<min_coord_Y)|(plane_cells_counter==0))
                  (coord_Y<min_coord_Y)
                    min_coord_Y = coord_Y;

                if//((coord_Z<min_coord_Z)|(plane_cells_counter==0))
                  (coord_Z<min_coord_Z)
                    min_coord_Z = coord_Z;

                if//((temperature<min_temperature)|(plane_cells_counter==0))
                  (temperature<min_temperature)
                    min_temperature = temperature;

                if//((eff_stress<min_eff_stress)|(plane_cells_counter==0))
                  (eff_stress<min_eff_stress)
                    min_eff_stress = eff_stress;

                if//((prin_stress<min_prin_stress)|(plane_cells_counter==0))
                  (prin_stress<min_prin_stress)
                    min_prin_stress = prin_stress;

                if//((force_moment_X<min_force_moment_X)|(plane_cells_counter==0))
                  (force_moment_X<min_force_moment_X)
                    min_force_moment_X = force_moment_X;

                if//((force_moment_Y<min_force_moment_Y)|(plane_cells_counter==0))
                  (force_moment_Y<min_force_moment_Y)
                    min_force_moment_Y = force_moment_Y;

                if//((force_moment_Z<min_force_moment_Z)|(plane_cells_counter==0))
                  (force_moment_Z<min_force_moment_Z)
                    min_force_moment_Z = force_moment_Z;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (strain<min_strain)
                    min_strain = strain;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (discl_density<min_discl_density)
                    min_discl_density = discl_density;

                //Creation of the cell with minimal parameters
                min_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, min_coord_X, min_coord_Y, min_coord_Z, min_temperature,
                     min_eff_stress, min_prin_stress, min_force_moment_X, min_force_moment_Y, min_force_moment_Z, min_strain, min_discl_density);

                // Calculation of maximal values
                if//((coord_X>max_coord_X)|(plane_cells_counter==0))
                    (coord_X>max_coord_X)
                    max_coord_X = coord_X;

                if//((coord_Y>max_coord_Y)|(plane_cells_counter==0))
                    (coord_Y>max_coord_Y)
                    max_coord_Y = coord_Y;

                if//((coord_Z>max_coord_Z)|(plane_cells_counter==0))
                    (coord_Z>max_coord_Z)
                    max_coord_Z = coord_Z;

                if//((temperature>max_temperature)|(plane_cells_counter==0))
                    (temperature>max_temperature)
                    max_temperature = temperature;

                if//((eff_stress>max_eff_stress)|(plane_cells_counter==0))
                    (eff_stress>max_eff_stress)
                    max_eff_stress = eff_stress;

                if//((prin_stress>max_prin_stress)|(plane_cells_counter==0))
                    (prin_stress>max_prin_stress)
                    max_prin_stress = prin_stress;

                if//((force_moment_X>max_force_moment_X)|(plane_cells_counter==0))
                    (force_moment_X>max_force_moment_X)
                    max_force_moment_X = force_moment_X;

                if//((force_moment_Y>max_force_moment_Y)|(plane_cells_counter==0))
                    (force_moment_Y>max_force_moment_Y)
                    max_force_moment_Y = force_moment_Y;

                if//((force_moment_Z>max_force_moment_Z)|(plane_cells_counter==0))
                    (force_moment_Z>max_force_moment_Z)
                    max_force_moment_Z = force_moment_Z;

                if//((strain<max_strain)|(plane_cells_counter==0))
                   (strain>max_strain)
                    max_strain = strain;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (discl_density>max_discl_density)
                    max_discl_density = discl_density;

                //Creation of the cell with maximal parameters
                max_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, max_coord_X, max_coord_Y, max_coord_Z, max_temperature,
                     max_eff_stress, max_prin_stress, max_force_moment_X, max_force_moment_Y, max_force_moment_Z, max_strain, max_discl_density);

                // Calculation of number of cells in plane
                plane_cells_counter++;
                
                if(axis.equals("X")) plane_coord = coord_X;
                if(axis.equals("Y")) plane_coord = coord_Y;
                if(axis.equals("Z")) plane_coord = coord_Z;
            }
          }
        }

        plane_coeffs = new double[4];
    
        plane_coeffs[0] = 0;
        plane_coeffs[1] = 0;
        plane_coeffs[2] = 0;

        if(axis.equals("X")) plane_coeffs[0] = 1;
        if(axis.equals("Y")) plane_coeffs[1] = 1;
        if(axis.equals("Z")) plane_coeffs[2] = 1;
        
        plane_coeffs[3] = -plane_coord;
        
        System.out.println("---------===============-------------------============");
        System.out.println("Number of cells in plane: "+plane_cells_counter);
        
        min_coord_X = min_cellR3.getCoordX();
        min_coord_Y = min_cellR3.getCoordY();
        min_coord_Z = min_cellR3.getCoordZ();
        
        max_coord_X = max_cellR3.getCoordX();
        max_coord_Y = max_cellR3.getCoordY();
        max_coord_Z = max_cellR3.getCoordZ();
        
        System.out.println("---------===============-------------------============");
        System.out.println("For plane: min_coord_X = "+min_coord_X);
        System.out.println("For plane: min_coord_Y = "+min_coord_Y);
        System.out.println("For plane: min_coord_Z = "+min_coord_Z);
        System.out.println("---------===============-------------------============");
        System.out.println("For plane: max_coord_X = "+max_coord_X);
        System.out.println("For plane: max_coord_Y = "+max_coord_Y);
        System.out.println("For plane: max_coord_Z = "+max_coord_Z);
        System.out.println("---------===============-------------------============");
        
        for(int vis_cell_counter = 0; vis_cell_counter < visual_cells.size(); vis_cell_counter++)
        {
          cellR3 = (VisualCellR3)visual_cells.get(vis_cell_counter);
          
          cellR3.setCoordX(cellR3.getCoordX() - (max_coord_X - min_coord_X)/2.0);
          cellR3.setCoordY(cellR3.getCoordY() - (max_coord_Y - min_coord_Y)/2.0);
          cellR3.setCoordZ(cellR3.getCoordZ() - (max_coord_Z - min_coord_Z)/2.0);
          
          if(vis_cell_counter == visual_cells.size()/3)
              System.out.println("Visual cell # "+vis_cell_counter+": coord_X = "+cellR3.getCoordX()+
                                 "; coord_Y = "+cellR3.getCoordY()+"; coord_Z = "+cellR3.getCoordZ());
          
          visual_cells.set(vis_cell_counter, cellR3);
        }
      /*
        min_cellR3.setCoordX(min_coord_X - (max_coord_X - min_coord_X)/2.0);
        min_cellR3.setCoordY(min_coord_Y - (max_coord_Y - min_coord_Y)/2.0);
        min_cellR3.setCoordZ(min_coord_Z - (max_coord_Z - min_coord_Z)/2.0);
        
        max_cellR3.setCoordX(max_coord_X - (max_coord_X - min_coord_X)/2.0);
        max_cellR3.setCoordY(max_coord_Y - (max_coord_Y - min_coord_Y)/2.0);
        max_cellR3.setCoordZ(max_coord_Z - (max_coord_Z - min_coord_Z)/2.0);
      */

        return visual_cells;
    }
    
    /** The method extracts 3D-cells, which distance from line
     * containing certain two 3D-points is less than maximal distance.
     * @param point_1 1st 3D-point contained in line
     * @param point_2 2nd 3D-point contained in line
     * @param max_distance maximal distance from line to each of extracted 3D-cells
     *        (expressed in diameters of cell)
     * @param _show_all_cells variable responsible for showing of all cells
     * @return 3 lists: 
     *      1. set of 3D-cells, which distance from line
     *         containing certain two 3D-points is less than maximal distance;
     *      2. set of line 3D points nearest to these cells;
     *      3. set of line parameters corresponding to these points.
     */
    public ArrayList[] extractLine(PointR3 point_1, PointR3 point_2, 
                                   double max_distance, boolean _show_all_cells)
    {
        if(max_distance<0)
        {
            System.out.println("ERROR! The value of distance cannot be negative!");
            return null;
        }
        
        // Parameter t corresponding to point r of line, which is nearest to 
        // center of certain cellular automaton: r = r0 + t*(r1 - r0),
        // where r1, r0 - coordinates of two certain points of line
        double line_param;
        
        // Coordinates of certain two points on line
        double point_1_X = point_1.getX(),
               point_1_Y = point_1.getY(),
               point_1_Z = point_1.getZ(),
               point_2_X = point_2.getX(),
               point_2_Y = point_2.getY(),
               point_2_Z = point_2.getZ();
        
        // Square of length of segment between two certain points of line
        double segm_length_sq = (point_2_X-point_1_X)*(point_2_X-point_1_X)+
                                (point_2_Y-point_1_Y)*(point_2_Y-point_1_Y)+
                                (point_2_Z-point_1_Z)*(point_2_Z-point_1_Z);

        if(segm_length_sq==0)
        {
            System.out.println("ERROR! Two points cannot be equal to each other!");
            return null;
        }

        // Coordinates of line point nearest to centre of current cell
        double nearest_point_X, nearest_point_Y, nearest_point_Z;

        // Total number of cells
        int cell_number = specimenR3.getRowsNum();
        System.out.println("PlaneMaker.extractLine(PointR3 point_1, PointR3 point_2, \n" +
"                                   double max_distance, boolean _show_all_cells): total number of cells = " + cell_number);

        // Current cell
        VisualCellR3 cellR3;
        
        // Line 3D point nearest to certain cell,
        // which distance to line is less than the maximal distance
        PointR3 line_pointR3;

        // The set contains cells, which distance from line
        // is less than the maximal distance.
        ArrayList real_cells = new ArrayList(0);
        
        // The set contains line 3D points nearest to cells,
        // which distance to line is less than the maximal distance.
        ArrayList line_points = new ArrayList(0);
        
        // The list contains parameters corresponding to line 3D points nearest to cells,
        // which distance to line is less than the maximal distance.
        ArrayList line_params = new ArrayList(0);

        // Index of grain containing cell
        int grain_index;

        // Type of cell energy interaction with neighbours
        byte energy_type;

        // Type of cell location
        byte location_type;

        /** Coordinate X of cell */
        double coord_X;

        /** Coordinate Y of cell */
        double coord_Y;

        /** Coordinate Z of cell */
        double coord_Z;

        /** Temperature of cell */
        double temperature;

        /** "Effective" stress of cell */
        double eff_stress;

        /** Principal stress of cell */
        double prin_stress;

        /** Coordinate X of force moment of cell */
        double force_moment_X;

        /** Coordinate Y of force moment of cell */
        double force_moment_Y;

        /** Coordinate Z of force moment of cell */
        double force_moment_Z;

        /** Cell state (elastic, plastic or damaged) */
        byte state;

        /** Strain of cell */
        double strain;

        /** Dicclination density of cell*/
        double discl_density;

        // Square of distance from cell to line
        double distance_sq = 0;

        // Maximal coordinates and parameters of cells in plane
        double  max_coord_X = Math.max(point_1_X, point_2_X),
                max_coord_Y = Math.max(point_1_Y, point_2_Y),
                max_coord_Z = Math.max(point_1_Z, point_2_Z),
                max_temperature=init_max_temperature,
                max_eff_stress=init_max_eff_stress,
                max_prin_stress=0,
                max_force_moment_X=0,
                max_force_moment_Y=0,
                max_force_moment_Z=0,
                max_strain = init_max_strain,
                max_discl_density = 0;

        // Minimal coordinates and parameters of cells in plane
        double  min_coord_X = Math.min(point_1_X, point_2_X),
                min_coord_Y = Math.min(point_1_Y, point_2_Y),
                min_coord_Z = Math.min(point_1_Z, point_2_Z),
                min_temperature=init_min_temperature,
                min_eff_stress=init_min_eff_stress,
                min_prin_stress=0,
                min_force_moment_X=0,
                min_force_moment_Y=0,
                min_force_moment_Z=0,
                min_strain = init_min_strain,
                min_discl_density = 0;

        // Counter of cells in line
        int line_cells_counter = 0;

        // The variable equals true if current cell is the first cell from line.
        boolean first_cell_on_line = true;

        // Cycle over all cells in specimen
        for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
        {
          // Type of cell energy interaction with neighbours
          energy_type    = (new Byte(specimenR3.getCell(cell_counter, 0))).byteValue();

          // Type of cell location
          location_type  = (new Byte(specimenR3.getCell(cell_counter, 1))).byteValue();

          if(((location_type!=Common.OUTER_CELL)&(energy_type!=Common.ADIABATIC_ADIABATIC_CELL))|(_show_all_cells))
          {
            // Cell coordinates
            coord_X        = (new Double(specimenR3.getCell(cell_counter, 3))).doubleValue();
            coord_Y        = (new Double(specimenR3.getCell(cell_counter, 4))).doubleValue();
            coord_Z        = (new Double(specimenR3.getCell(cell_counter, 5))).doubleValue();
            
            // Parameter of line point nearest to cell
            line_param = ((coord_X - point_1_X)*(point_2_X - point_1_X)+
                          (coord_Y - point_1_Y)*(point_2_Y - point_1_Y)+
                          (coord_Z - point_1_Z)*(point_2_Z - point_1_Z))/
                         segm_length_sq;

            nearest_point_X = point_1_X + line_param*(point_2_X - point_1_X);
            nearest_point_Y = point_1_Y + line_param*(point_2_Y - point_1_Y);
            nearest_point_Z = point_1_Z + line_param*(point_2_Z - point_1_Z);
            
            // Square of distance from cell to line
            distance_sq = (nearest_point_X - coord_X)*(nearest_point_X - coord_X)+
                          (nearest_point_Y - coord_Y)*(nearest_point_Y - coord_Y)+
                          (nearest_point_Z - coord_Z)*(nearest_point_Z - coord_Z);

            if(distance_sq < max_distance*max_distance)
       //     if(Math.abs(distance) <= max_distance)
            {
                //Cell parameters
                grain_index    = (new Integer(specimenR3.getCell(cell_counter, 2))).intValue();
                temperature    = (new Double(specimenR3.getCell(cell_counter, 6))).doubleValue();
                eff_stress     = (new Double(specimenR3.getCell(cell_counter, 7))).doubleValue();
                prin_stress    = (new Double(specimenR3.getCell(cell_counter, 8))).doubleValue();
                force_moment_X = (new Double(specimenR3.getCell(cell_counter, 9))).doubleValue();
                force_moment_Y = (new Double(specimenR3.getCell(cell_counter,10))).doubleValue();
                force_moment_Z = (new Double(specimenR3.getCell(cell_counter,11))).doubleValue();
                state          = (new Byte(specimenR3.getCell(cell_counter,12))).byteValue();
                strain         = (new Double(specimenR3.getCell(cell_counter, 13))).doubleValue();
                discl_density  = (new Double(specimenR3.getCell(cell_counter, 14))).doubleValue();

                //Creation of the cell with real coordinates
                cellR3 = new VisualCellR3((String)grain_materials.get(grain_index-1), energy_type, grain_index, location_type, state,
                                          coord_X, coord_Y, coord_Z,
                                          temperature, eff_stress, prin_stress,
                                          force_moment_X, force_moment_Y, force_moment_Z, strain, discl_density);

                // Addition of this cell to the list
                real_cells.add(cellR3);
                
                //Creation of the cell with coordinates of nearest point on line
                line_pointR3 = new PointR3(nearest_point_X, nearest_point_Y, nearest_point_Z);

                // Addition of this cell to the list
                line_points.add(line_pointR3);
                
                // Addition of parameter of line point nearest to cell to the list
                line_params.add(line_param);
                
                // Determination of maximal and minimal values
                if(first_cell_on_line)
                {
                    min_coord_X = max_coord_X = coord_X;
                    min_coord_Y = max_coord_Y = coord_Y;
                    min_coord_Z = max_coord_Z = coord_Z;

                    min_prin_stress    = prin_stress;
                    min_force_moment_X = force_moment_X;
                    min_force_moment_Y = force_moment_Y;
                    min_force_moment_Z = force_moment_Z;

                    max_prin_stress    = prin_stress;
                    max_force_moment_X = force_moment_X;
                    max_force_moment_Y = force_moment_Y;
                    max_force_moment_Z = force_moment_Z;

                    first_cell_on_line = false;
                }

                if(coord_X < min_coord_X)
                    min_coord_X = coord_X;

                if(coord_Y < min_coord_Y)
                    min_coord_Y = coord_Y;

                if(coord_Z < min_coord_Z)
                    min_coord_Z = coord_Z;

                if//((temperature<min_temperature)|(plane_cells_counter==0))
                  (temperature<min_temperature)
                    min_temperature = temperature;

                if//((eff_stress<min_eff_stress)|(plane_cells_counter==0))
                  (eff_stress<min_eff_stress)
                    min_eff_stress = eff_stress;

                if//((prin_stress<min_prin_stress)|(plane_cells_counter==0))
                  (prin_stress<min_prin_stress)
                    min_prin_stress = prin_stress;

                if//((force_moment_X<min_force_moment_X)|(plane_cells_counter==0))
                  (force_moment_X<min_force_moment_X)
                    min_force_moment_X = force_moment_X;

                if//((force_moment_Y<min_force_moment_Y)|(plane_cells_counter==0))
                  (force_moment_Y<min_force_moment_Y)
                    min_force_moment_Y = force_moment_Y;

                if//((force_moment_Z<min_force_moment_Z)|(plane_cells_counter==0))
                  (force_moment_Z<min_force_moment_Z)
                    min_force_moment_Z = force_moment_Z;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (strain<min_strain)
                    min_strain = strain;

                if//((strain<min_strain)|(plane_cells_counter==0))
                   (discl_density<min_discl_density)
                    min_discl_density = discl_density;

                //Creation of the cell with minimal parameters
                min_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, min_coord_X, min_coord_Y, min_coord_Z, min_temperature,
                     min_eff_stress, min_prin_stress, min_force_moment_X, min_force_moment_Y, min_force_moment_Z, min_strain, min_discl_density);

                if(coord_X > max_coord_X)
                    max_coord_X = coord_X;

                if(coord_Y > max_coord_Y)
                    max_coord_Y = coord_Y;

                if(coord_Z > max_coord_Z)
                    max_coord_Z = coord_Z;

                if//((temperature>max_temperature)|(plane_cells_counter==0))
                    (temperature>max_temperature)
                    max_temperature = temperature;

                if//((eff_stress>max_eff_stress)|(plane_cells_counter==0))
                    (eff_stress>max_eff_stress)
                    max_eff_stress = eff_stress;

                if//((prin_stress>max_prin_stress)|(plane_cells_counter==0))
                    (prin_stress>max_prin_stress)
                    max_prin_stress = prin_stress;

                if//((force_moment_X>max_force_moment_X)|(plane_cells_counter==0))
                    (force_moment_X>max_force_moment_X)
                    max_force_moment_X = force_moment_X;

                if//((force_moment_Y>max_force_moment_Y)|(plane_cells_counter==0))
                    (force_moment_Y>max_force_moment_Y)
                    max_force_moment_Y = force_moment_Y;

                if//((force_moment_Z>max_force_moment_Z)|(plane_cells_counter==0))
                    (force_moment_Z>max_force_moment_Z)
                    max_force_moment_Z = force_moment_Z;

                if//((strain<max_strain)|(plane_cells_counter==0))
                   (strain>max_strain)
                    max_strain = strain;

                if//((strain<max_strain)|(plane_cells_counter==0))
                   (discl_density>max_discl_density)
                    max_discl_density = discl_density;

                //Creation of the cell with maximal parameters
                max_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, max_coord_X, max_coord_Y, max_coord_Z, max_temperature,
                     max_eff_stress, max_prin_stress, max_force_moment_X, max_force_moment_Y, max_force_moment_Z, max_strain, max_discl_density);

                // Calculation of number of cells in line
                line_cells_counter++;
            }
          }
        }

        System.out.println();
        System.out.println("Number of cells in plane: "+line_cells_counter);
        
        // Minimal value of line parameter
        double min_line_param = 0;
        
        // Number of line parameters
        int param_number = line_params.size();

        // Length of segment between two chosen points on line
        double segm_length = Math.sqrt(segm_length_sq);

        // Transformation of list of line parameters:
        // 1. search of minimal and maximal line parameters
        for(int param_counter = 0; param_counter<param_number; param_counter++)
        {
            line_param = ((Double)line_params.get(param_counter)).doubleValue();

            if(param_counter == 0)
                min_line_param = line_param;
            else
            {
                if(line_param<min_line_param)
                    min_line_param = line_param;
            }
        }

        // 2. Determination of line parameter as distance from first line point
        //    to line point corresponding to this parameter
        for(int param_counter = 0; param_counter<param_number; param_counter++)
        {
            line_param = ((Double)line_params.get(param_counter)).doubleValue();
            line_param = (line_param - min_line_param)*segm_length;
            line_params.set(param_counter, line_param);
        }
        
        ArrayList[] lists = new ArrayList[3];
        
        lists[0] = real_cells;
        lists[1] = line_points;
        lists[2] = line_params;
        
        if(max_distance>=0)
        {
            return lists;
        }
        else
            return null;
    }
    
    /** The method returns 3D-cells located in 3 first layers from specimen boundary.
     * @param _show_all_cells variable responsible for drawing of all cells
     * @return 3D-cells located in 3 first layers from specimen boundary
     */
    public ArrayList getOuterVisualCellsR3(boolean _show_all_cells)
    {
        // Total number of cells
        int cell_number = specimenR3.getRowsNum();

        //TEST
        System.out.println("PlaneMaker.getOuterVisualCellsR3(boolean _show_all_cells): total number of cells = " + cell_number);

        // Visual cell
        VisualCellR3 cellR3;

        // Index of grain containing cell
        int grain_index;

        /** Coordinate X of cell */
        double coord_X;

        /** Coordinate Y of cell */
        double coord_Y;

        /** Coordinate Z of cell */
        double coord_Z;

        /** Type of cell energy interchange */
        byte energy_type;

        /** Type of cell location */
        byte location_type;

        /** Temperature of cell */
        double temperature;

        /** "Effective" stress of cell */
        double eff_stress;

        /** Principal stress of cell */
        double prin_stress;

        /** Coordinate X of force moment of cell */
        double force_moment_X;

        /** Coordinate Y of force moment of cell */
        double force_moment_Y;

        /** Coordinate Z of force moment of cell */
        double force_moment_Z;

        /** State of cell (elastic, plastic or damaged) */
        byte state;

        /** Strain of cell */
        double strain;
        
        /** Disclination density of cell*/
        double discl_density;

        // Distance from cell to the plane
        double distance = 0;

        // Maximal coordinates and parameters of cells in plane
        double  max_coord_X=0,
                max_coord_Y=0,
                max_coord_Z=0,
                max_temperature=init_max_temperature,
                max_eff_stress=init_max_eff_stress,
                max_prin_stress=0,
                max_force_moment_X=0,
                max_force_moment_Y=0,
                max_force_moment_Z=0,
                max_strain=init_max_strain,
                max_discl_density = 0;

        // Minimal coordinates and parameters of cells in plane
        double  min_coord_X=0,
                min_coord_Y=0,
                min_coord_Z=0,
                min_temperature=init_min_temperature,
                min_eff_stress=init_min_eff_stress,
                min_prin_stress=0,
                min_force_moment_X=0,
                min_force_moment_Y=0,
                min_force_moment_Z=0,
                min_strain=init_min_strain,
                min_discl_density = 0;

        // True if we consider first inner cell
 //       boolean first_inner_cell = true;

        // Number of visual cells
        int visual_cells_counter = 0;
        
        // Total number of cells
        int all_cells_counter = 0;

        // Set of visual cells returned
        ArrayList cell_set = new ArrayList(0);

        // Triple index of cell
        Three cell_indices;

        // Indices of cell
        int index_I, index_J, index_K;

        // Cycle over all cells in specimen
        for(int cell_counter = 0; cell_counter<cell_number; cell_counter++)
        {
          //Cell coordinates and parameters
          energy_type    = (new Byte(specimenR3.getCell(cell_counter, 0))).byteValue();
          location_type  = (new Byte(specimenR3.getCell(cell_counter, 1))).byteValue();

          cell_indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

          index_I = cell_indices.getI();
          index_J = cell_indices.getJ();
          index_K = cell_indices.getK();

          if((energy_type != Common.ADIABATIC_ADIABATIC_CELL & location_type != Common.OUTER_CELL)|_show_all_cells)
          {
            grain_index    = (new Integer(specimenR3.getCell(cell_counter, 2))).intValue();
            coord_X        = (new Double(specimenR3.getCell(cell_counter,  3))).doubleValue();
            coord_Y        = (new Double(specimenR3.getCell(cell_counter,  4))).doubleValue();
            coord_Z        = (new Double(specimenR3.getCell(cell_counter,  5))).doubleValue();
            temperature    = (new Double(specimenR3.getCell(cell_counter,  6))).doubleValue();
            eff_stress     = (new Double(specimenR3.getCell(cell_counter,  7))).doubleValue();
            prin_stress    = (new Double(specimenR3.getCell(cell_counter,  8))).doubleValue();
            force_moment_X = (new Double(specimenR3.getCell(cell_counter,  9))).doubleValue();
            force_moment_Y = (new Double(specimenR3.getCell(cell_counter, 10))).doubleValue();
            force_moment_Z = (new Double(specimenR3.getCell(cell_counter, 11))).doubleValue();
            state          = (new Byte  (specimenR3.getCell(cell_counter, 12))).byteValue();
            strain         = (new Double(specimenR3.getCell(cell_counter, 13))).doubleValue();
            discl_density  = (new Double(specimenR3.getCell(cell_counter, 14))).doubleValue();

            if(index_I <= layer_number | index_I >= cell_number_I-layer_number-1 |
               index_J <= layer_number | index_J >= cell_number_J-layer_number-1 |
               index_K <= layer_number | index_K >= cell_number_K-layer_number-1 )
                
            if(location_type == Common.INNER_BOUNDARY_CELL | 
               location_type == Common.INNER_BOUNDARY_INTERGRANULAR_CELL)
            {
                //Creation of the cell
                cellR3 = new VisualCellR3((String)grain_materials.get(grain_index-1), energy_type, grain_index, location_type, state,
                                      coord_X, coord_Y, coord_Z, temperature, eff_stress, prin_stress,
                                      force_moment_X, force_moment_Y, force_moment_Z, strain, discl_density);
                
                cell_set.add(cellR3);
                
                // Calculation of number of visual cells returned
                visual_cells_counter++;
            }
            
/*
            if(first_inner_cell)
            {
                min_coord_X = coord_X;
                min_coord_Y = coord_Y;
                min_coord_Z = coord_Z;
                min_temperature = temperature;
                min_eff_stress = eff_stress;
                min_prin_stress = prin_stress;
                min_force_moment_X = force_moment_X;
                min_force_moment_Y = force_moment_Y;
                min_force_moment_Z = force_moment_Z;

                max_coord_X = coord_X;
                max_coord_Y = coord_Y;
                max_coord_Z = coord_Z;
                max_temperature = temperature;
                max_eff_stress = eff_stress;
                max_prin_stress = prin_stress;
                max_force_moment_X = force_moment_X;
                max_force_moment_Y = force_moment_Y;
                max_force_moment_Z = force_moment_Z;

                first_inner_cell = false;
            }
*/
            
            // Calculation of minimal values
            if(coord_X<min_coord_X)                min_coord_X = coord_X;
            if(coord_Y<min_coord_Y)                min_coord_Y = coord_Y;
            if(coord_Z<min_coord_Z)                min_coord_Z = coord_Z;
            if(temperature<min_temperature)        min_temperature = temperature;
            if(eff_stress<min_eff_stress)          min_eff_stress  = eff_stress;
            if(prin_stress<min_prin_stress)        min_prin_stress = prin_stress;
            if(force_moment_X<min_force_moment_X)  min_force_moment_X = force_moment_X;
            if(force_moment_Y<min_force_moment_Y)  min_force_moment_Y = force_moment_Y;
            if(force_moment_Z<min_force_moment_Z)  min_force_moment_Z = force_moment_Z;
            if(strain<min_strain)                  min_strain = strain;
            if(discl_density<min_discl_density)    min_discl_density = discl_density;

            //Creation of the cell with minimal parameters
            min_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, min_coord_X, min_coord_Y, min_coord_Z, min_temperature,
                             min_eff_stress, min_prin_stress, min_force_moment_X, min_force_moment_Y, min_force_moment_Z, min_strain, min_discl_density);

            // Calculation of maximal values
            if(coord_X>max_coord_X)                max_coord_X        = coord_X;
            if(coord_Y>max_coord_Y)                max_coord_Y        = coord_Y;
            if(coord_Z>max_coord_Z)                max_coord_Z        = coord_Z;
            if(temperature>max_temperature)        max_temperature    = temperature;
            if(eff_stress>max_eff_stress)          max_eff_stress     = eff_stress;
            if(prin_stress>max_prin_stress)        max_prin_stress    = prin_stress;
            if(force_moment_X>max_force_moment_X)  max_force_moment_X = force_moment_X;
            if(force_moment_Y>max_force_moment_Y)  max_force_moment_Y = force_moment_Y;
            if(force_moment_Z>max_force_moment_Z)  max_force_moment_Z = force_moment_Z;
            if(strain>max_strain)                  max_strain         = strain;
            if(discl_density>max_discl_density)    max_discl_density  = discl_density;

            //Creation of the cell with maximal parameters
            max_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, max_coord_X, max_coord_Y, max_coord_Z, max_temperature,
                             max_eff_stress, max_prin_stress, max_force_moment_X, max_force_moment_Y, max_force_moment_Z, max_strain, max_discl_density);
            
            all_cells_counter++;
          }
        }

        System.out.println();
        System.out.println("Number of visual cells: "+visual_cells_counter+" = "+cell_set.size());
        System.out.println("Total number of cells:  "+all_cells_counter);
        System.out.println();

        // Translation of origin of coordinate system to the specimen centre
        for(int cell_counter = 0; cell_counter<cell_set.size(); cell_counter++)
        {
            cellR3 = (VisualCellR3)cell_set.get(cell_counter);
            cellR3.setCoordX(cellR3.getCoordX()+(min_coord_X - max_coord_X)/2);
            cellR3.setCoordY(cellR3.getCoordY()+(min_coord_Y - max_coord_Y)/2);
            cellR3.setCoordZ(cellR3.getCoordZ()+(min_coord_Z - max_coord_Z)/2);
            cell_set.set(cell_counter, cellR3);
        }
        
        return cell_set;
    }

    /** The method returns 3D-cells.
     * @param _show_all_cells variable responsible for drawing of all cells
     * @return 3D-cells located in 3 first layers from specimen boundary
     */
    public ArrayList getAllOuterVisualCellsR3(boolean _show_all_cells)
    {
        // Total number of cells
        int cell_number = specimenR3.getRowsNum();

        //TEST
        System.out.println("PlaneMaker.getAllOuterVisualCellsR3(boolean _show_all_cells): total number of cells = " + cell_number);
        System.out.println("PlaneMaker.getAllOuterVisualCellsR3(boolean _show_all_cells): _show_all_cells = " + _show_all_cells);

        // Visual cell
        VisualCellR3 cellR3;

        // Index of grain containing cell
        int grain_index;

        /** Coordinate X of cell */
        double coord_X;

        /** Coordinate Y of cell */
        double coord_Y;

        /** Coordinate Z of cell */
        double coord_Z;

        /** Type of cell energy interchange */
        byte energy_type;

        /** Type of cell location */
        byte location_type;

        /** Temperature of cell */
        double temperature;

        /** "Effective" stress of cell */
        double eff_stress;

        /** Principal stress of cell */
        double prin_stress;

        /** Coordinate X of force moment of cell */
        double force_moment_X;

        /** Coordinate Y of force moment of cell */
        double force_moment_Y;

        /** Coordinate Z of force moment of cell */
        double force_moment_Z;

        /** State of cell (elastic, plastic or damaged) */
        byte state;

        /** Strain of cell */
        double strain;

        /** Disclination density */
        double discl_density;

        // Distance from cell to the plane
        double distance = 0;

        // Maximal coordinates and parameters of cells in plane
        double  max_coord_X=0,
                max_coord_Y=0,
                max_coord_Z=0,
                max_temperature=init_max_temperature,
                max_eff_stress=init_max_eff_stress,
                max_prin_stress=0,
                max_force_moment_X=0,
                max_force_moment_Y=0,
                max_force_moment_Z=0,
                max_strain=init_max_strain,
                max_discl_density = 0;

        // Minimal coordinates and parameters of cells in plane
        double  min_coord_X=0,
                min_coord_Y=0,
                min_coord_Z=0,
                min_temperature=init_min_temperature,
                min_eff_stress=init_min_eff_stress,
                min_prin_stress=0,
                min_force_moment_X=0,
                min_force_moment_Y=0,
                min_force_moment_Z=0,
                min_strain=init_min_strain,
                min_discl_density = 0;

        // True if we consider first inner cell
 //       boolean first_inner_cell = true;

        // Number of visual cells returned
        int visual_cells_counter = 0;

        // Set of visual cells returned
        ArrayList cell_set = new ArrayList(0);

        // Triple index of cell
        Three cell_indices;

        // Indices of cell
        int index_I, index_J, index_K;

        // Cycle over all cells in specimen
        for(int cell_counter = 0; cell_counter < cell_number; cell_counter++)
        {
          //Cell coordinates and parameters
          energy_type  = (new Byte(specimenR3.getCell(cell_counter, 0))).byteValue();
          location_type  = (new Byte(specimenR3.getCell(cell_counter, 1))).byteValue();

          cell_indices = calcTripleIndex(cell_counter, cell_number_I, cell_number_J, cell_number_K);

          index_I = cell_indices.getI();
          index_J = cell_indices.getJ();
          index_K = cell_indices.getK();
          
          grain_index    = (new Integer(specimenR3.getCell(cell_counter,2))).intValue();

          if(grain_index > 16)
          if((energy_type != Common.ADIABATIC_ADIABATIC_CELL & location_type != Common.OUTER_CELL) | _show_all_cells)
          {
         //   grain_index    = (new Integer(specimenR3.getCell(cell_counter,2))).intValue();
            coord_X        = (new Double(specimenR3.getCell(cell_counter, 3))).doubleValue();
            coord_Y        = (new Double(specimenR3.getCell(cell_counter, 4))).doubleValue();
            coord_Z        = (new Double(specimenR3.getCell(cell_counter, 5))).doubleValue();
            temperature    = (new Double(specimenR3.getCell(cell_counter, 6))).doubleValue();
            eff_stress     = (new Double(specimenR3.getCell(cell_counter, 7))).doubleValue();
            prin_stress    = (new Double(specimenR3.getCell(cell_counter, 8))).doubleValue();
            force_moment_X = (new Double(specimenR3.getCell(cell_counter, 9))).doubleValue();
            force_moment_Y = (new Double(specimenR3.getCell(cell_counter,10))).doubleValue();
            force_moment_Z = (new Double(specimenR3.getCell(cell_counter,11))).doubleValue();
            state          = (new Byte  (specimenR3.getCell(cell_counter,12))).byteValue();
            strain         = (new Double(specimenR3.getCell(cell_counter, 13))).doubleValue();
            discl_density  = (new Double(specimenR3.getCell(cell_counter, 14))).doubleValue();

            //Creation of the cell
            cellR3 = new VisualCellR3((String)grain_materials.get(grain_index-1), energy_type, grain_index, location_type, state,
                                      coord_X, coord_Y, coord_Z, temperature, eff_stress, prin_stress,
                                      force_moment_X, force_moment_Y, force_moment_Z, strain, discl_density);

            cell_set.add(cellR3);

            // Calculation of minimal values
            if(coord_X<min_coord_X)                min_coord_X = coord_X;
            if(coord_Y<min_coord_Y)                min_coord_Y = coord_Y;
            if(coord_Z<min_coord_Z)                min_coord_Z = coord_Z;
            if(temperature<min_temperature)        min_temperature = temperature;
            if(eff_stress<min_eff_stress)          min_eff_stress  = eff_stress;
            if(prin_stress<min_prin_stress)        min_prin_stress = prin_stress;
            if(force_moment_X<min_force_moment_X)  min_force_moment_X = force_moment_X;
            if(force_moment_Y<min_force_moment_Y)  min_force_moment_Y = force_moment_Y;
            if(force_moment_Z<min_force_moment_Z)  min_force_moment_Z = force_moment_Z;
            if(strain<min_strain)                  min_strain = strain;
            if(discl_density<min_discl_density)    min_discl_density = discl_density;

            //Creation of the cell with minimal parameters
            min_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, min_coord_X, min_coord_Y, min_coord_Z, min_temperature,
                     min_eff_stress, min_prin_stress, min_force_moment_X, min_force_moment_Y, min_force_moment_Z, min_strain, min_discl_density);

            // Calculation of maximal values
            if(coord_X>max_coord_X)                max_coord_X = coord_X;
            if(coord_Y>max_coord_Y)                max_coord_Y = coord_Y;
            if(coord_Z>max_coord_Z)                max_coord_Z = coord_Z;
            if(temperature>max_temperature)        max_temperature = temperature;
            if(eff_stress>max_eff_stress)          max_eff_stress = eff_stress;
            if(prin_stress>max_prin_stress)        max_prin_stress = prin_stress;
            if(force_moment_X>max_force_moment_X)  max_force_moment_X = force_moment_X;
            if(force_moment_Y>max_force_moment_Y)  max_force_moment_Y = force_moment_Y;
            if(force_moment_Z>max_force_moment_Z)  max_force_moment_Z = force_moment_Z;
            if(strain>max_strain)                  max_strain = strain;
            if(discl_density>max_discl_density)    max_discl_density = discl_density;

            //Creation of the cell with maximal parameters
            max_cellR3 = new VisualCellR3(Common.NOT_AVAILABLE, (byte)0, 0, (byte)0, (byte)0, max_coord_X, max_coord_Y, max_coord_Z, max_temperature,
                     max_eff_stress, max_prin_stress, max_force_moment_X, max_force_moment_Y, max_force_moment_Z, max_strain, max_discl_density);

            // Calculation of number of visual cells returned
            visual_cells_counter++;
          }
        }

        System.out.println();
        System.out.println("Number of visual cells: "+visual_cells_counter);
        System.out.println();

        return cell_set;
    }

    /** The method calculates triple index of cell3D with certain single index.
     * @param intIndex single index of cell3D
     * @param size_X number of allowed values of 1st element of triple index.
     * @param size_Y number of allowed values of 2nd element of triple index.
     * @param size_Z number of allowed values of 3rd element of triple index.
     * @return triple index of cell3D
     */
    public Three calcTripleIndex(int intIndex, int size_X, int size_Y, int size_Z)
    {
        // Triple index of cell3D.
        Three tripleIndex = new Three();

        // Calculation of triple index of cell3D.
        if ((intIndex > -1) & (intIndex < size_X*size_Y*size_Z))
        {
            tripleIndex.setI((intIndex % (size_X*size_Y)) % size_X);
            tripleIndex.setJ((intIndex % (size_X*size_Y)) / size_X);
            tripleIndex.setK( intIndex / (size_X*size_Y));
        }

        return tripleIndex;
    }

    /** The method calculates triple index of visual cell3D with certain coordinates.
     * @param coord_X cell coordinate X
     * @param coord_Y cell coordinate X
     * @param coord_Z cell coordinate X
     * @return triple index of visual cell3D with certain coordinates
     */
    public Three calcTripleIndex(double coord_X, double coord_Y, double coord_Z)
    {
        // Visual cell radius
        double radius = 0.5;
        
        // Cell indices
        int index_I=-1, index_J=-1, index_K=-1;

        // Calculation of indices of cell in case of HCP
        if(packing_type == Common.HEXAGONAL_CLOSE_PACKING)
        {
            index_I = (int)Math.round((coord_X/radius - 1.0 - Math.sqrt(1.0/3.0))/Math.sqrt(3.0));
            index_J = (int)Math.round((coord_Y/radius - 1.5)*0.5);
            index_K = (int)Math.round((coord_Z/radius - 1.0)*Math.sqrt(3.0/8.0));
        }

        // Calculation of indices of cell in case of SCP
        if(packing_type == Common.SIMPLE_CUBIC_PACKING)
        {
            index_I = (int)Math.round((coord_X/radius - 1)/2);
            index_J = (int)Math.round((coord_Y/radius - 1)/2);
            index_K = (int)Math.round((coord_Z/radius - 1)/2);
        }

        Three indices = new Three(index_I, index_J, index_K);

        return indices;
    }

    /** The method calculates coefficients of equation of plane containing three 3D-points
     * (Ax + By + Cz + D = 0).
     * @param point_1 1st 3D-point
     * @param point_2 2nd 3D-point
     * @param point_3 3rd 3D-point
     */
    public void calcPlaneEquationCoeffs(PointR3 point_1, PointR3 point_2, PointR3 point_3)
    {
        // Coordinates of points
        double x1 = point_1.getX();
        double y1 = point_1.getY();
        double z1 = point_1.getZ();
        double x2 = point_2.getX();
        double y2 = point_2.getY();
        double z2 = point_2.getZ();
        double x3 = point_3.getX();
        double y3 = point_3.getY();
        double z3 = point_3.getZ();

        // Matrices for calculation of coefficients of plane equation
        DoubleSquareMatrix matrixA = new DoubleSquareMatrix(2);
        DoubleSquareMatrix matrixB = new DoubleSquareMatrix(2);
        DoubleSquareMatrix matrixC = new DoubleSquareMatrix(2);

        // Setting of elements to the matrices
        matrixA.setElement(0, 0, y2-y1); matrixA.setElement(0, 1, z2-z1);
        matrixA.setElement(1, 0, y3-y1); matrixA.setElement(1, 1, z3-z1);

        matrixB.setElement(0, 0, z2-z1); matrixB.setElement(0, 1, x2-x1);
        matrixB.setElement(1, 0, z3-z1); matrixB.setElement(1, 1, x3-x1);

        matrixC.setElement(0, 0, x2-x1); matrixC.setElement(0, 1, y2-y1);
        matrixC.setElement(1, 0, x3-x1); matrixC.setElement(1, 1, y3-y1);

        // Coefficients of plane equation
        double coeff_A = matrixA.det();
        double coeff_B = matrixB.det();
        double coeff_C = matrixC.det();

        // Calculation of the coefficients
        if((coeff_A!=0)|(coeff_B!=0)|(coeff_C!=0))
        {
            // Free coefficient of plane equation
            double coeff_D = -coeff_A*x1-coeff_B*y1-coeff_C*z1;

            // Creation of array of the coefficients
            plane_coeffs = new double[4];

            plane_coeffs[0] = coeff_A;
            plane_coeffs[1] = coeff_B;
            plane_coeffs[2] = coeff_C;
            plane_coeffs[3] = coeff_D;
        }
        else
            System.out.println("Error: these 3 points are located on the same line.");
    }

    /** The method returns coefficients of equation of plane containing three 3D-points
     * (Ax + By + Cz + D = 0).
     */
    public double[] getPlaneEquationCoeffs()
    {
        return plane_coeffs;
    }

    /** The method calculates 2D coordinates of 3D point located in plane
     * with certain coefficients of equation (Ax + By + Cz + D = 0).
     * These coordinates are calculated in basis located in the plane.
     * @param _coord_X coordinate X of 3D point
     * @param _coord_Y coordinate Y of 3D point
     * @param _coord_Z coordinate Z of 3D point
     * @param coeffs coefficients of plane equation
     */
    public DoubleVector calcCoordinatesR2(double _coord_X, double _coord_Y, double _coord_Z, double[] coeffs)
    {
        // Vectors of new basis located in the plane
        VectorR3 new_basis_vector_X = new VectorR3(0,0,0);
        VectorR3 new_basis_vector_Y = new VectorR3(0,0,0);

        // Point with coordinates in new basis
        PointR2 pointR2 = new PointR2(0,0);

        // Coefficients of plane equation (Ax + By + Cz + D = 0)
        double coeff_A = coeffs[0];
        double coeff_B = coeffs[1];
        double coeff_C = coeffs[2];
        double coeff_D = coeffs[3];

        System.out.println("Plane coefficients: "+coeff_A+" "+coeff_B+" "+coeff_C+" "+coeff_D);

        // Calculation of coordinates of Vectors of new basis located in the plane
        if((coeff_A!=0)&(coeff_B!=0)&(coeff_C!=0))
        {
            new_basis_vector_X.setX(-coeff_C/coeff_A);
            new_basis_vector_X.setY(0);
            new_basis_vector_X.setZ(1);

            new_basis_vector_Y.setX(coeff_A/coeff_C);
            new_basis_vector_Y.setY(-(coeff_A*coeff_A + coeff_C*coeff_C)/(coeff_B*coeff_C));
            new_basis_vector_Y.setZ(1);
        }

        if((coeff_A==0)&(coeff_B!=0)&(coeff_C!=0))
        {
            new_basis_vector_X.setX(1);
            new_basis_vector_X.setY(0);
            new_basis_vector_X.setZ(0);

            new_basis_vector_Y.setX(0);
            new_basis_vector_Y.setY( coeff_C/Math.sqrt(coeff_B*coeff_B + coeff_C*coeff_C));
            new_basis_vector_Y.setZ(-coeff_B/Math.sqrt(coeff_B*coeff_B + coeff_C*coeff_C));
        }

        if((coeff_A!=0)&(coeff_B==0)&(coeff_C!=0))
        {
            new_basis_vector_X.setX(-coeff_C/Math.sqrt(coeff_A*coeff_A + coeff_C*coeff_C));
            new_basis_vector_X.setY(0);
            new_basis_vector_X.setZ( coeff_A/Math.sqrt(coeff_A*coeff_A + coeff_C*coeff_C));

            new_basis_vector_Y.setX(0);
            new_basis_vector_Y.setY(1);
            new_basis_vector_Y.setZ(0);
        }

        if((coeff_A!=0)&(coeff_B!=0)&(coeff_C==0))
        {
            new_basis_vector_X.setX( coeff_B/Math.sqrt(coeff_A*coeff_A + coeff_B*coeff_B));
            new_basis_vector_X.setY(-coeff_A/Math.sqrt(coeff_A*coeff_A + coeff_B*coeff_B));
            new_basis_vector_X.setZ(0);

            new_basis_vector_Y.setX(0);
            new_basis_vector_Y.setY(0);
            new_basis_vector_Y.setZ(1);
        }

        if((coeff_A==0)&(coeff_B==0)&(coeff_C!=0))
        {
            new_basis_vector_X.setX(1);
            new_basis_vector_X.setY(0);
            new_basis_vector_X.setZ(0);

            new_basis_vector_Y.setX(0);
            new_basis_vector_Y.setY(1);
            new_basis_vector_Y.setZ(0);
        }

        if((coeff_A==0)&(coeff_B!=0)&(coeff_C==0))
        {
            new_basis_vector_X.setX(1);
            new_basis_vector_X.setY(0);
            new_basis_vector_X.setZ(0);

            new_basis_vector_Y.setX(0);
            new_basis_vector_Y.setY(0);
            new_basis_vector_Y.setZ(1);
        }

        if((coeff_A!=0)&(coeff_B==0)&(coeff_C==0))
        {
            new_basis_vector_X.setX(0);
            new_basis_vector_X.setY(1);
            new_basis_vector_X.setZ(0);

            new_basis_vector_Y.setX(0);
            new_basis_vector_Y.setY(0);
            new_basis_vector_Y.setZ(1);
        }

        // Coordinates of 1st vector of new basis
        double vectX_coordX = new_basis_vector_X.getX();
        double vectX_coordY = new_basis_vector_X.getY();
        double vectX_coordZ = new_basis_vector_X.getZ();

        // Coordinates of 2nd vector of new basis
        double vectY_coordX = new_basis_vector_Y.getX();
        double vectY_coordY = new_basis_vector_Y.getY();
        double vectY_coordZ = new_basis_vector_Y.getZ();

        // Vector of coordinates of point in old 3D basis
        DoubleVector old_coords = new DoubleVector(2);

        // Sum of squares of plane coefficients A, B, C
        double comb = coeff_A*coeff_A + coeff_B*coeff_B + coeff_C*coeff_C;

        /* Transition to the new 2D basis from old 3D basis*/

        // Matrix of new basis
        DoubleSquareMatrix new_basis = new DoubleSquareMatrix(2);

        // Transition of origin to plane
        _coord_X = _coord_X + coeff_A*coeff_D/comb;
        _coord_Y = _coord_Y + coeff_B*coeff_D/comb;
        _coord_Z = _coord_Z + coeff_C*coeff_D/comb;

        System.out.println("New 3D coordinates: "+_coord_X+" "+_coord_Y+" "+_coord_Z);

        // Setting of elements of the matrix
        new_basis.setElement(0, 0, vectX_coordX); new_basis.setElement(0, 1, vectY_coordX);
        new_basis.setElement(1, 0, vectX_coordY); new_basis.setElement(1, 1, vectY_coordY);

        DoubleVector new_coords;

        if(new_basis.det()!=0)
        {
            // Setting of changed coordinates to old 3D basis with new origin
            old_coords.setComponent(0, _coord_X);
            old_coords.setComponent(1, _coord_Y);

            // Calculation of new coordinates of point
            new_coords = (DoubleVector)new_basis.inverse().multiply(old_coords);

            //TEST
            System.out.println("New 3D coordinate Z: "+_coord_Z+" = "+
                               (new_coords.getComponent(0)*vectX_coordZ +
                                new_coords.getComponent(1)*vectY_coordZ));
        }
        else
        {
            // Setting of elements of the matrix
            new_basis.setElement(0, 0, vectX_coordX); new_basis.setElement(0, 1, vectY_coordX);
            new_basis.setElement(1, 0, vectX_coordZ); new_basis.setElement(1, 1, vectY_coordZ);

            if(new_basis.det()!=0)
            {
                // Setting of changed coordinates to old 3D basis with new origin
                old_coords.setComponent(0, _coord_X);
                old_coords.setComponent(1, _coord_Z);

                // Calculation of new coordinates of point
                new_coords = (DoubleVector)new_basis.inverse().multiply(old_coords);

                //TEST
                System.out.println("New 3D coordinate Y: "+_coord_Y+" = "+
                               (new_coords.getComponent(0)*vectX_coordY +
                                new_coords.getComponent(1)*vectY_coordY));
            }
            else
            {
                // Setting of elements of the matrix
                new_basis.setElement(0, 0, vectX_coordY); new_basis.setElement(0, 1, vectY_coordY);
                new_basis.setElement(1, 0, vectX_coordZ); new_basis.setElement(1, 1, vectY_coordZ);

                // Setting of changed coordinates to old 3D basis with new origin
                old_coords.setComponent(0, _coord_Y);
                old_coords.setComponent(1, _coord_Z);

                // Calculation of new coordinates of point
                new_coords = (DoubleVector)new_basis.inverse().multiply(old_coords);

                //TEST
                System.out.println("New 3D coordinate X: "+_coord_X+" = "+
                               (new_coords.getComponent(0)*vectX_coordX +
                                new_coords.getComponent(1)*vectY_coordX));
            }
        }

        return new_coords;
    }
}