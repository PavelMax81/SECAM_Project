package grainsCore;
   /*
    * @(#) GrainsHCPSpecimenR3.java version 1.1.3;       Jan 2009
    *
    * Copyright (c) 2002-2008 DIP Labs, Ltd. All Rights Reserved.
    * 
    * Class for determination of specimen for modelling of fracture
    *
    *=============================================================
    *  Last changes :
    *         20 Jan 2009 by Pavel V. Maksimov (new type of states: int[])
    *            File version 1.1.3
    */

   /** Class for determination of specimen for modelling of fracture
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.1 - Dec 2006
    */

public class GrainsHCPSpecimenR3
{
    public int[] states;

    /** The constructor creates the list of cells.
     * @param cell_number_X number of cells on X-direction
     * @param cell_number_Y number of cells on Y-direction
     * @param cell_number_Z number of cells on Z-direction
     */
    public GrainsHCPSpecimenR3(int cell_number_X, int cell_number_Y, int cell_number_Z)
    {
        // each element of "states" is cell state
        states = new int[cell_number_X*cell_number_Y*cell_number_Z];
        byte undamaged_cell = GrainsHCPCommon.UNDAMAGED_CELL;
        int index = -1;
        
        /** Creation of the array of undamaged cells
         */
        for (int cell_counter3 = 0; cell_counter3 < cell_number_Z; cell_counter3++)
        for (int cell_counter2 = 0; cell_counter2 < cell_number_Y; cell_counter2++)
        for (int cell_counter1 = 0; cell_counter1 < cell_number_X; cell_counter1++)
        {
            index++;
            states[index] = undamaged_cell;
        }
    }

   /** The method returns the number of cells of the specimen.
    * @return the number of cells of the specimen.
    */
    public int size()
    {        
        return states.length;
    }

   /** The method sets the state of the cell with specified index.
    * @param index the index of the cell.
    * @param state new state of the cell.
    */
    public void set(int index, int state)
    {
        states[index] = state;
    }

   /** The method returns the state of the cell with specified index.
    * @param index the index of the cell.
    * @return the state of the cell with specified index.
    */
    public int get(int index)
    {
        return states[index];
    }
}