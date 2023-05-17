/**
 * @(#) RecCA_RE.java version 1.0.0;       April 2.02.2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for simulation of recrystallization and processes of mechanical and thermal loading
 *
 *=============================================================
 *  Last changes :
 *          13 June 2009 by Gregory S. Bikineev, Pavel V. Maksimov, Dmitry D. Moiseenko
 *          File version 1.0.1
 */

/** Class for simulation of recrystallization and processes of mechanical and thermal loading
 *  @author Gregory S. Bikineev, Pavel V. Maksimov, Dmitry D. Moiseenko
 *  @version 1.0.2 - Nov 2016
 */
import corecalc.*;
import util.*;
import java.util.*;
import recGeometry.*;
import JSci.maths.vectors.*;
import paral_calc.*;

public class RecCA_RE
{
    /** Representative of class for simulation of recrystallization and processes
     * of mechanical and thermal loading is constructed
     * @param args
     */
    public RecCA_RE(String[] args)
    {
        RecrystallizationCA recrystCA;
        Paral_Calc_SECAM paral_calc_SECAM;
        
        // TEST
      //  JoclSample jocl_sample = new JoclSample();
        
        // Variable responsible for start of parallel algorythm
        boolean paral_calc = false;// true; // 
        
      //  if(false)
        if(!args[0].equals("PlaneCreation") & !args[0].equals("PlaneMaker"))
        {
            Date start_date = new Date();
            
            if(args[1].equals("paral_calc"))
              paral_calc = true;
            else
              paral_calc = false;

            /* Block 2:
             * Creation of representative of class for simulation of processes
             * of recrystallization, mechanical and thermal loading
             */
            if(paral_calc)
            {
              System.out.println("\nParallelized version 'Paral_Calc_SECAM' is started!!!\n");
                
              paral_calc_SECAM = new Paral_Calc_SECAM(args);
            }
            else
            {
              System.out.println("\nNon-parallelized version 'RecrystallizationCA' is started!!!\n");
              
              recrystCA = new RecrystallizationCA(args[0]);
            }
            
            Date finish_date = new Date();
            
            long time_period = finish_date.getTime() - start_date.getTime();

            System.out.println("The period of execution of the program: " + time_period + " ms.");
        }

        if(args[0].equals("PlaneCreation"))
        {
            System.out.println("START!!!");
            Date start_date = new Date();

            int time_step = (new Integer(args[2])).intValue();

            // Start of task
            PlaneCreation plane = new PlaneCreation(args[1], time_step, args[3]);

            System.out.println("FINISH!!!");
            Date finish_date = new Date();

            long time_period = finish_date.getTime() - start_date.getTime();

            System.out.println("The period of execution of the program: " + time_period + " ms.");
        }
    }
    
    /** The method creates representative of the class.
     * @param args description of task
     */
    public static void main(String[] args)
    {
        System.out.println("Name of task: "+args[0]);

        /* Block 1: Creation of representative of class for simulation
         * of recrystallization and processes of mechanical and thermal loading
         */
        RecCA_RE recCA_RE = new RecCA_RE(args);
        
        if(args[0].equals("PlaneMaker"))
        {
            // Name of task
            String task_name;

            // Number of time step
            String time_step_number;

            task_name = args[1]; // "heat_all";//  "tension_X";
            
            time_step_number = "10";
            
            System.out.println("Name of task: "+task_name+"; number of time steps: "+time_step_number+".");

            recCA_RE.makePlanes(task_name, time_step_number);
        }
    }

    /** The method creates planes of intersection of specimen at certain time step.
     * @param time_step_number number of time step
     */
    public void makePlanes(String task_name, String time_step_number)
    {
        // Name of file with parameters of cells at certain time step
        String file_name = "./user/task_db/"+task_name+"/"+task_name+"_"+time_step_number+".res";

        // Cell with maximal parameters of plane
        VisualCellR3 max_cell;

        // Cell with minimal parameters of plane
        VisualCellR3 min_cell;

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

        // Variable responsible for drawing of all cells:
        // if it equals false then boundary cells are not shown,
        // if it equals true then all cells are shown.
        boolean _show_all_cells = true;

        // Extraction of set of automata (located at distance less than maximal distance
        // from plane) from specimen
        planes = plane_maker.extractPlane(point_1, point_2, point_3, max_distance, _show_all_cells);

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
        String axis = "Z";

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
        point_1 = new PointR3(3.387, 4.75, 0);
        point_2 = new PointR3(3.387, 4.75, 1);

        // Three lists corresponding to extracted cells
        ArrayList[] cell_lists = new ArrayList[3];

        // Maximal distance from extracted cell to given line
        max_distance = Math.sqrt(3.0/8.0);

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
}