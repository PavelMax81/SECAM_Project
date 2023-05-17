package calcmath;
/*
 * @(#) LineFunctionR1.java version 1.0;       Apr 2008
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for creation of 1D line function on the segment.
 *
 *=============================================================
 *  Last changes :
 *         13 Apr 2008 by Pavel V. Maksimov (creation of class).
 *            File version 1.0
 */

/** Class for creation of 1D line function on the segment.
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0 - Apr 2008
 */

public class LineFunctionR1
{    
    /** The coordinate of the left point of the segment */
    double coord1;
    
    /** The coordinate of the right point of the segment */
    double coord2;
    
    /** The function value in the left point of the segment */
    double value1;
    
    /** The function value in the right point of the segment */
    double value2;
    
    /** The constructor creates 1D line function on the certain segment.
     * @param _coord1 the coordinate of the left point of the segment
     * @param _coord2 the coordinate of the right point of the segment
     * @param _value1 the value of the function in the left point of the segment
     * @param _value2 the value of the function in the right point of the segment
     */
    protected LineFunctionR1(double _coord1, double _coord2, 
                          double _value1, double _value2)
    {   
        // Setting of coordinates of segment vertices 
        coord1 = _coord1;
        coord2 = _coord2;
        
        // Setting of function values in segment vertices 
        value1 = _value1;
        value2 = _value2;
    }
    
    /** The constructor creates 1D line function on the segment between 0 and 1.
     * @param _value1 the value of the function in the left point of the segment
     * @param _value2 the value of the function in the right point of the segment
     */
    protected LineFunctionR1(double _value1, double _value2)
    {       
        // Setting of the coordinates of segment vertices 
        coord1 = 0;
        coord2 = 1;
        
        // Setting of the function values in segment vertices 
        value1 = _value1;
        value2 = _value2;
    }
    
    /** The method calculates the value of 1D line function in the certain point of the segment.
     * @param coord the coordinate of the point of the segment.
     * @return the value of 1D line function in the certain point of the segment.
     */
    protected double calcValue(double coord)
    {
        return value2*(coord - coord1)/(coord2 - coord1) +
               value1 *(coord2 - coord)/(coord2 - coord1);
    }
    
    /** The method returns the function value in the left point of the segment.
     * @return the value of the function in the left point of the segment
     */
    public double getValue1()
    {
        return value1;
    }
        
    /** The method returns the function value in the right point of the segment.
     * @return the value of the function in the right point of the segment
     */
    public double getValue2()
    {
        return value2;
    }
    
    /** The method sets the function value in the left point of the segment.
     * @param _value1 the value of the function in the left point of the segment
     */
    public void setValue1(double _value1)
    {
        value1 = _value1;
    }
        
    /** The method sets the function value in the right point of the segment.
     * @param _value2 the value of the function in the right point of the segment
     */
    public void setValue2(double _value2)
    {
        value2 = _value2;
    }
}
