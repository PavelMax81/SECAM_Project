package calcmath;
/*
 * @(#) LineFunctionR3.java version 1.0;       Apr 2008
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for creation of 3D line function on the parallelepiped with the following 
 * coordinates (z, y, x) of vertices: (0,0,0), (0,0,1), (0,1,0), (0,1,1), (1,0,0), (1,0,1), (1,1,0), (1,1,1).
 *
 *=============================================================
 *  Last changes :
 *         13 Apr 2008 by Pavel V. Maksimov (creation of class).
 *            File version 1.0
 */

/** Class for creation of 3D line function on the parallelepiped with the following 
 * coordinates (z, y, x) of vertices: (0,0,0), (0,0,1), (0,1,0), (0,1,1), (1,0,0), (1,0,1), (1,1,0), (1,1,1).
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0 - Apr 2008
 */

public class LineFunctionR3 extends LineFunctionR2 
{    
    /** The function value in the point (1, 0, 0) */
    double value5;
    
    /** The function value in the point (1, 0, 1) */
    double value6;
    
    /** The function value in the point (1, 1, 0) */
    double value7;
    
    /** The function value in the point (1, 1, 1) */
    double value8;
    
    /** The constructor creates 3D line function on the parallelepiped with the following 
     * coordinates (z, y, x) of vertices: (0,0,0), (0,0,1), (0,1,0), (0,1,1), (1,0,0), (1,0,1), (1,1,0), (1,1,1).
     * @param _value1 the function value in the point (0, 0, 0)
     * @param _value2 the function value in the point (0, 0, 1)
     * @param _value3 the function value in the point (0, 1, 0)
     * @param _value4 the function value in the point (0, 1, 1)
     * @param _value5 the function value in the point (1, 0, 0)
     * @param _value6 the function value in the point (1, 0, 1)
     * @param _value7 the function value in the point (1, 1, 0)
     * @param _value8 the function value in the point (1, 1, 1)
     */
    public LineFunctionR3(double _value1, double _value2, double _value3, double _value4,
                          double _value5, double _value6, double _value7, double _value8)
    {
        // Setting of the function values in vertices of the parallelepiped
        super(_value1, _value2, _value3, _value4);
        
        value5 = _value5;
        value6 = _value6;
        value7 = _value7;
        value8 = _value8;
    }
    
    /** The method calculates the value of 3D line function in the certain point of the parallelepiped.
     * @param coordZ the coordinate Z of the point
     * @param coordY the coordinate Y of the point
     * @param coordX the coordinate X of the point
     * @return the value of 3D line function in the certain point of the parallelepiped
     */
    public double calcValue(double coordZ, double coordY, double coordX)
    {
        // Calculation of the line function value in the point with coordinates (0, y, x)
        double value0YX = calcValue(coordY, coordX);
        
        // Calculation of the line function value in the point with coordinates (1, y, x)
        setValue1(value5);
        setValue2(value6);
        setValue3(value7);
        setValue4(value8);
        
        double value1YX = calcValue(coordY, coordX);
        
        // Calculation of the line function value in the given point with coordinates (z, y, x)
        setValue1(value0YX);
        setValue2(value1YX);
        
        return calcValue(coordZ);
    }
    
    /** The method returns the function value in the point (1, 0, 0).
     * @return the value of the function in the point (1, 0, 0)
     */
    public double getValue5()
    {
        return value5;
    }
        
    /** The method returns the function value in the point (1, 0, 1).
     * @return the value of the function in the point (1, 0, 1)
     */
    public double getValue6()
    {
        return value6;
    }
    
        /** The method returns the function value in the point (1, 1, 0).
     * @return the value of the function in the point (1, 1, 0)
     */
    public double getValue7()
    {
        return value7;
    }
        
    /** The method returns the function value in the point (1, 1, 1).
     * @return the value of the function in the point (1, 1, 1)
     */
    public double getValue8()
    {
        return value8;
    }
    
    /** The method sets the function value in the point (1, 0, 0).
     * @param _value5 the value of the function in the point (1, 0, 0)
     */
    public void setValue5(double _value5)
    {
        value5 = _value5;
    }
        
    /** The method sets the function value in the point (1, 0, 1).
     * @param _value6 the value of the function in the point (1, 0, 1)
     */
    public void setValue6(double _value6)
    {
        value6 = _value6;
    }
    
    /** The method sets the function value in the point (1, 1, 0).
     * @param _value7 the value of the function in the point (1, 1, 0)
     */
    public void setValue7(double _value7)
    {
        value7 = _value7;
    }
        
    /** The method sets the function value in the point (1, 1, 1).
     * @param _value8 the value of the function in the point (1, 1, 1)
     */
    public void setValue8(double _value8)
    {
        value8 = _value8;
    }
}
