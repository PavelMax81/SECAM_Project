package cellcore;
import recGeometry.*;
import java.util.*;
import java.io.*;

/*
 * @(#)ToposMeshR3.java version 1.0;       Apr 2011
 *
 * Copyright (c) 2002-2011 DIP Labs, Ltd. All Rights Reserved.
 *
 * Class for information about cubic mesh of toposes
 *
 *=============================================================
 * Last changes:
 *      13 April 2011 by Pavel V. Maksimov
 *         (creation of the class).
 *
 *      File version 1.0
 */

/** Class for information about cubic mesh of toposes
 * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.0 - Apr 2011
 */
public class ToposMeshR3 extends ArrayList
{
    // Three-dimensional array of toposes
    private ToposR3[][][] toposesR3;

    // Number of toposes along each axis
    int topos_number_I;
    int topos_number_J;
    int topos_number_K;
    
    int[][] neighbours2S;

    /** The constructor creates mesh of zero toposes.
     * @param topos_num_I number of toposes along axis X
     * @param topos_num_J number of toposes along axis Y
     * @param topos_num_K number of toposes along axis Z
     */
    public ToposMeshR3(int topos_num_I, int topos_num_J, int topos_num_K)
    {
        topos_number_I = topos_num_I;
        topos_number_J = topos_num_J;
        topos_number_K = topos_num_K;

        toposesR3 = new ToposR3[topos_number_I][topos_number_J][topos_number_K];

        for(int topos_counter_I = 0; topos_counter_I<topos_number_I; topos_counter_I++)
        for(int topos_counter_J = 0; topos_counter_J<topos_number_J; topos_counter_J++)
        for(int topos_counter_K = 0; topos_counter_K<topos_number_K; topos_counter_K++)
        {
            toposesR3[topos_counter_I][topos_counter_J][topos_counter_K] = new ToposR3(new PointR3(0, 0, 0), new VectorR3());
        }
    }

    /** The constructor creates mesh of toposes equaling to specified one.
     * @param _toposMeshR3 specified mesh of toposes
     */
    public ToposMeshR3(ToposMeshR3 _toposMeshR3)
    {
        for(int topos_counter_I = 0; topos_counter_I<topos_number_I; topos_counter_I++)
        for(int topos_counter_J = 0; topos_counter_J<topos_number_J; topos_counter_J++)
        for(int topos_counter_K = 0; topos_counter_K<topos_number_K; topos_counter_K++)
        {
            toposesR3[topos_counter_I][topos_counter_J][topos_counter_K] = new ToposR3(_toposMeshR3.getElement(topos_counter_I, topos_counter_J, topos_counter_K));
        }
    }

    /** The method creates mesh of toposes according to list of cells.
     * @param rec_cellsR3 list of cells
     */
    public ToposMeshR3(ArrayList bound_displ_vectors, ArrayList cell_pairs, ArrayList bound_coordinates, String writeFileName)
    {
            // Sorting of boundary points according to coordinates
            ArrayList sorted_displ_vectors = new ArrayList(0);
            ArrayList sorted_cell_pairs = new ArrayList(0);
            ArrayList sorted_bound_coords = new ArrayList(0);

            sorted_displ_vectors.add(bound_displ_vectors.get(0));
            sorted_cell_pairs.add(cell_pairs.get(0));
            sorted_bound_coords.add(bound_coordinates.get(0));

            VectorR3 curr_bound_coord;

            boolean element_is_added = false;

            int sorted_list_size = 0;

            VectorR3 bound_coord;

            // Deleting of equaling elements
            for(int bound_counter = 1; bound_counter<bound_coordinates.size(); bound_counter++)
            {
                bound_coord = (VectorR3)bound_coordinates.get(bound_counter);

                element_is_added = false;

                sorted_list_size = sorted_bound_coords.size();

                for(int sort_bound_counter = 0; sort_bound_counter<sorted_list_size; sort_bound_counter++)
                {
                  curr_bound_coord = (VectorR3)sorted_bound_coords.get(sort_bound_counter);

                  if(curr_bound_coord.equals(bound_coord))
                  {
                      sort_bound_counter = sorted_bound_coords.size();
                      element_is_added = true;
                  }
                }

                if(!element_is_added)
                    sorted_bound_coords.add(bound_coord);
            }

            sorted_list_size = sorted_bound_coords.size();

            VectorR3 bound_coord_1, bound_coord_2;

            int repl_counter = 1;

            // Sorting of elements
            while(repl_counter>0)
            {
                repl_counter = 0;

                for (int bound_counter = 0; bound_counter<sorted_bound_coords.size()-1; bound_counter++)
                {
                    bound_coord_1 = (VectorR3)sorted_bound_coords.get(bound_counter);
                    bound_coord_2 = (VectorR3)sorted_bound_coords.get(bound_counter+1);

                    if(bound_coord_1.moreThan(bound_coord_2))
                    {
                        sorted_bound_coords.set(bound_counter, bound_coord_2);
                        sorted_bound_coords.set(bound_counter+1, bound_coord_1);

                        repl_counter++;
                    }
                }
            }

            setNeighbours2S_SCP(sorted_bound_coords);

            try
            {
                BufferedWriter bw_vec  = new BufferedWriter(new FileWriter(writeFileName));

                // TEST: printing of sorted boundary coordinates
                /*
                for(int sort_bound_counter = 0; sort_bound_counter<sorted_bound_coords.size(); sort_bound_counter++)
                {
                    bound_coord = (VectorR3)sorted_bound_coords.get(sort_bound_counter);
                    
                    bw_vec.write(bound_coord.getX()+" "+bound_coord.getY()+" "+bound_coord.getZ()+"\n");
                    bw_vec.flush();
                }

                // TEST: printing of coordinates and displacement vectors for all boundary points
                bw_vec.write("2*"+sorted_bound_coords.size()+" = "+bound_displ_vectors.size()+"\n");
                bw_vec.flush();
                 * 
                 */

                for(int bound_cell_counter = 0; bound_cell_counter<sorted_bound_coords.size(); bound_cell_counter++)
                {
                    bound_coord = new VectorR3((VectorR3)sorted_bound_coords.get(neighbours2S[bound_cell_counter][0]));
                    bw_vec.write(bound_coord.getX()+" "+bound_coord.getY()+" "+bound_coord.getZ()+" | ");

                    bound_coord = new VectorR3((VectorR3)sorted_bound_coords.get(bound_cell_counter));
                    bw_vec.write(bound_coord.getX()+" "+bound_coord.getY()+" "+bound_coord.getZ()+"\n");

                    bw_vec.flush();
                }

                bw_vec.close();
            }
            catch(IOException io_exc)
            {
                System.out.println("Error: " + io_exc);
            }
            
            /*
            for(int bound_cell_counter = 0; bound_cell_counter<bound_coordinates.size(); bound_cell_counter++)
            {
                bound_coord = new VectorR3((VectorR3)bound_coordinates.get(bound_cell_counter));
                bw_vec.write(bound_coord.getX()+" "+bound_coord.getY()+" "+bound_coord.getZ()+" ");

                displ_vector = new VectorR3((VectorR3)bound_displ_vectors.get(bound_cell_counter));
                bw_vec.write(displ_vector.getX()+" "+displ_vector.getY()+" "+displ_vector.getZ()+"\n");

                bw_vec.flush();
            }

            bw_vec.close();
             *
             */
            // END OF TEST

    }
    
    /** The method sets neighbours at 2nd coordination sphere for boundary points in case of SCP
     * @param bound_coords
     */
    private void setNeighbours2S_SCP(ArrayList bound_coords)
    {
        int bound_point_number = bound_coords.size();
        neighbours2S = new int[bound_point_number][6];
        VectorR3 bound_coord, neighb_coord;
        double radius = 0;
        int neighb2S_counter = 0;
        double distance;
        
        for(int bound_coord_counter = 0; bound_coord_counter<bound_point_number; bound_coord_counter++)
        {
            bound_coord = (VectorR3)bound_coords.get(bound_coord_counter);
            
            if(bound_coord_counter == 0)
                radius = bound_coord.getZ();
            
            neighb2S_counter = 0;
            
            for(int neighb_counter = 0; neighb_counter<bound_point_number; neighb_counter++)
            {
                neighb_coord = (VectorR3)bound_coords.get(neighb_counter);
                
                distance = (residial(bound_coord, neighb_coord)).getLength();
                
                if(distance > 2*radius*0.99)
                if(distance < 2*radius*1.01)
                {
                    neighbours2S[bound_coord_counter][neighb2S_counter] = neighb_counter;
                    neighb2S_counter++;
                }
            }
        }
    }
    
    /** The method returns residial of two vectors (vect_1 - vect_2).
     * @param vect_1 minuend vector
     * @param vect_2 subtrahend vector
     * @return residial of two vectors (vect_1 - vect_2)
     */
    public VectorR3 residial(VectorR3 vect_1, VectorR3 vect_2)
    {            
        return new VectorR3 (vect_1.getX() - vect_2.getX(), 
                             vect_1.getY() - vect_2.getY(),
                             vect_1.getZ() - vect_2.getZ());
    }

    /** The method returns element of mesh of toposes with specified triple index.
     * @param index_I index I of element of mesh of toposes
     * @param index_J index J of element of mesh of toposes
     * @param index_K index K of element of mesh of toposes
     * @return element of mesh of toposes with specified triple index
     */
    public ToposR3 getElement(int index_I, int index_J, int index_K)
    {
        return toposesR3[index_I][index_J][index_K];
    }

    /** The method sets the value of the element with specified triple index.
     * @param index_I index I of the element of mesh of toposes
     * @param index_J index J of the element of mesh of toposes
     * @param index_K index K of the element of mesh of toposes
     * @param toposR3 the value of the element of mesh of toposes
     */
    public void setElement(int index_I, int index_J, int index_K, ToposR3 toposR3)
    {
        toposesR3[index_I][index_J][index_K] = new ToposR3(toposR3);
    }
}
