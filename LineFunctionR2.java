package calcmath;
/*
 * @(#) LineFunctionR2.java version 1.0;       Apr 2008
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for creation of 2D line function on the rectangle 
 * with the following coordinates (y, x) of vertices: (0, 0), (0, 1), (1, 0), (1, 1).
 *
 *=============================================================
 *  Last changes :
 *         13 Apr 2008 by Pavel V. Maksimov (creation of class).
 *            File version 1.0
 */

/** Class for creation of 2D line function on the rectangle
 * with the following coordinates (y, x) of vertices: (0, 0), (0, 1), (1, 0), (1, 1).
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0 - Apr 2008
 */

public class LineFunctionR2 extends LineFunctionR1
{    
    /** The function value in the point (1, 0) */
    double value3;
    
    /** The function value in the point (1, 1) */
    double value4;
    
    /** The constructor creates 2D line function on the rectangle
     * with the following coordinates (y, x) of vertices: (0, 0), (0, 1), (1, 0), (1, 1).
     * @param _value1 the value of the function in the point (0, 0)
     * @param _value2 the value of the function in the point (0, 1)
     * @param _value3 the value of the function in the point (1, 0)
     * @param _value4 the value of the function in the point (1, 1)
     */
    protected LineFunctionR2(double _value1, double _value2, double _value3, double _value4)
    {
        // Setting of the function values in rectangle vertices 
        super(_value1, _value2);
        
        value3 = _value3;
        value4 = _value4;
    }
    
    /** The method calculates the value of 2D line function in the certain point of the rectangle.
     * @param coordY the coordinate Y of the point
     * @param coordX the coordinate X of the point
     * @return the value of 2D line function in the certain point of the rectangle
     */
    protected double calcValue(double coordY, double coordX)
    {
        // Calculation of the line function value in the point with coordinates (0, x)
        double value0X = calcValue(coordX);
        
        // Calculation of the line function value in the point with coordinates (1, x)
        setValue1(value3);
        setValue2(value4);
        
        double value1X = calcValue(coordX);
        
        // Calculation of the line function value in the given point with coordinates (y, x)
        setValue1(value0X);
        setValue2(value1X);
        
        return calcValue(coordY);
    }
    
    /** The method returns the function value in the point (1, 0).
     * @return the value of the function in the point (1, 0)
     */
    public double getValue3()
    {
        return value3;
    }
        
    /** The method returns the function value in the point (1, 1).
     * @return the value of the function in the point (1, 1)
     */
    public double getValue4()
    {
        return value4;
    }
    
    /** The method sets the function value in the point (1, 0).
     * @param _value3 the value of the function in the point (1, 0)
     */
    public void setValue3(double _value3)
    {
        value3 = _value3;
    }
        
    /** The method sets the function value in the point (1, 1).
     * @param _value4 the value of the function in the point (1, 1).
     */
    public void setValue4(double _value4)
    {
        value4 = _value4;
    }
}
