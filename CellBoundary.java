package cellcore;
import util.*;
import recGeometry.*;
import JSci.maths.matrices.*;

/* @(#) CellBoundary.java version 1.0;       Feb 2013
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about cell3D boundary
 *
 *=============================================================
 *  Last changes :
 *        19 February 2013 by Pavel V. Maksimov (creation of the class).
 *        File version 1.0
 */

/** Class for info about cell3D boundary
* @author Dmitrii D. Moiseenko & Pavel V. Maksimov
* @version 1.0 - Nov 2013
*/
public class CellBoundary
{
    /** Stress at cell boundary.
     */ 
    public double stress;
    
    /** Velocity of matter flow through cell boundary.
     */
    public double velocity;
    
    /** Mobility of matter at boundary.
     */
    public double mobility;
    
    /** Coordinates of centre of cell boundary.
     */
    public double coord_X, coord_Y, coord_Z;
    
    /** Single indices of neighbour cells.
     */
    public int first_neighb_index, second_neighb_index;
    
    /** Normal vector for cell boundary
     */
    public VectorR3 norm_vector;
    
    /** Empty constructor of the cell3D boundary.
     */
    public CellBoundary()
    {
        stress   = 0;
        velocity = 0;
        coord_X  = 0;
        coord_Y  = 0;
        coord_Z  = 0;
        first_neighb_index  = -1;
        second_neighb_index = -1;
        norm_vector = new VectorR3();
    }
    
    /** Constructor creates cell3D boundary according to data about two neighbour cells and its single indices.
     * @param first_cell first neighbour cell
     * @param _first_cell_index single index of first neighbour cell
     * @param second_cell second neighbour cell
     * @param _second_cell_index single index of second neighbour cell
     */
    public CellBoundary(RecCellR3 first_cell, RecCellR3 second_cell, int _first_cell_index, int _second_cell_index)
    {
        stress   = second_cell.getMechStress() - first_cell.getMechStress();
        velocity = 0;
        
        VectorR3 first_cell_coords  = first_cell.getCoordinates();
        VectorR3 second_cell_coords = second_cell.getCoordinates();
        
        coord_X  = (first_cell_coords.getX() + second_cell_coords.getX())/2;
        coord_Y  = (first_cell_coords.getY() + second_cell_coords.getY())/2;
        coord_Z  = (first_cell_coords.getZ() + second_cell_coords.getZ())/2;
        
        first_neighb_index  = _first_cell_index;
        second_neighb_index = _second_cell_index;
        
        norm_vector         = new VectorR3(second_cell_coords.getX() - first_cell_coords.getX(), 
                                           second_cell_coords.getY() - first_cell_coords.getY(),
                                           second_cell_coords.getZ() - first_cell_coords.getZ());
        
        norm_vector.normalize();
    }
    
    /** Constructor creates cell3D boundary as a copy of existing sample of cell3D boundary.
     * @param cell_boundary existing sample of cell3D boundary
     */
    public CellBoundary(CellBoundary cell_boundary)
    {
        stress   = cell_boundary.getStress();
        velocity = cell_boundary.getVelocity();
        
        coord_X  = cell_boundary.getCoordX();
        coord_Y  = cell_boundary.getCoordY();
        coord_Z  = cell_boundary.getCoordZ();
        
        first_neighb_index  = cell_boundary.getFirstNeighbIndex();
        second_neighb_index = cell_boundary.getSecondNeighbIndex();
        
        norm_vector         = new VectorR3(cell_boundary.getNormVectorR3());
    }
    
    /** The method returns stress at cell boundary.
     * @return stress at cell boundary
     */
    public double getStress()
    {
        return stress;
    }
    
    /** The method returns velocity of matter flow through cell boundary.
     * @return velocity of matter flow through cell boundary
     */
    public double getVelocity()
    {
        return velocity;
    }    
    
    /** The method sets stress at cell boundary.
     * @param _stress stress at cell boundary
     */
    public void setStress(double _stress)
    {
        stress = _stress;
    }
    
    /** The method sets velocity of matter flow through cell boundary.
     * @param _velocity velocity of matter flow through cell boundary
     */
    public void setVelocity(double _velocity)
    {
        velocity = _velocity;
    }    
    
    /** The method returns coordinate X of centre of cell boundary.
     * @return coordinate X of centre of cell boundary
     */
    public double getCoordX()
    {
        return coord_X;
    }    
    
    /** The method returns coordinate Y of centre of cell boundary.
     * @return coordinate Y of centre of cell boundary
     */
    public double getCoordY()
    {
        return coord_Y;
    }    
    
    /** The method returns coordinate Z of centre of cell boundary.
     * @return coordinate Z of centre of cell boundary
     */
    public double getCoordZ()
    {
        return coord_Z;
    }    
    
    /** The method returns index of first neighbour cell
     * @return index of first neighbour cell
     */
    public int getFirstNeighbIndex()
    {
        return first_neighb_index;
    }    
    
    /** The method returns index of second neighbour cell.
     * @return index of second neighbour cell
     */
    public int getSecondNeighbIndex()
    {
        return second_neighb_index;
    }
    
    /** The method returns normal vector for cell boundary.
     * @return normal vector for cell boundary
     */
    public VectorR3 getNormVectorR3()
    {
        return norm_vector;
    }
}