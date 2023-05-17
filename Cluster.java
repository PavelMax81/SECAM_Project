package grainsCore;

   /*
    * @(#) Cluster.java version 1.1;       Dec 2006
    *
    * Copyright (c) 2002-2006 DIP Labs, Ltd. All Rights Reserved.
    * 
    * Class for info about cluster of cell3D for simulation of fracture.
    *
    *=============================================================
    *  Last changes:
    *      13 Jun 2007 by Pavel V. Maksimov (change of the constructor "Cluster(_cluster)")
    *            File version 1.1
    */

   /** Class for info about cluster of cell3D for simulation of fracture.
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.1 - Dec 2006
    */

import java.util.*;

public class Cluster extends ArrayList
{
    /** The number of the cluster.
     */
    int number;
    
    /** The capacity of the cluster.
     */
    int capacity;
    
    /** The constructor creates the cluster of cell3D for simulation of fracture.
     * @param _cluster the cluster of cell3D for simulation of fracture.
     */
    public Cluster(Cluster _cluster)
    {
        for (int cellCounter = 0; cellCounter < _cluster.size(); cellCounter++)
        {
            add(_cluster.get(cellCounter));
        }
    }
    
    /** The constructor creates the cluster of cell3D for simulation of fracture.
     */
    public Cluster()
    {
        number   = 0;
        capacity = 0;
    }
    
    /** The constructor creates the cluster of cell3D for simulation of fracture.
     * @param _number the number of the cluster.
     * @param _capacity the capacity of the cluster.
     */
    public Cluster(int _number, int _capacity) 
    {
        number   = _number;
        capacity = _capacity;
    }   
    
    /** The method returns the number of the cluster.
     * @return the number of the cluster.
     */
    public int getNumber()
    {
        return number;    
    }
    
    /** The method set the number of the cluster.
     * @param _number the number of the cluster.
     */
    public void setNumber(int _number)
    {
        number = _number;
    }
    
    /** The method returns the capacity of the cluster.
     * @return the capacity of the cluster.
     */
    public int getCapacity()
    {
        return capacity;    
    }
    
    /** The method set the capacity of the cluster.
     * @param _capacity the capacity of the cluster.
     */
    public void setCapacity(int _capacity)
    {
        capacity = _capacity;
    } 
}
