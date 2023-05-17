package recGeometry;

/*
 * @(#)PointR1.java version 1.1;       Nov 2011
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about point1D
 *
 *=============================================================
 *  Last changes :
 *         22 Nov 2011 by Dmitrii D. Moiseenko
 *            File version 1.1.0
 *              (Addition of "writeToString" method)
 */

/** Class for info about point1D
* @author Dmitrii D. Moiseenko & Pavel V. Maksimov
* @version 1.1 - Nov 2011
*/ 

public class PointR1 
{
    /** Coordinate x of point1D
     */
    double x;
 
    /** The constructor creates coordinate x of point1D
     * @param _x  coordinate of point1D
     */
    public PointR1(double _x)
    {
        x = _x;
    }
    
    /** The constructor creates coordinates of point1D
     * @param _pointR1 gives it's own coordinate x to point1D
     */
    public PointR1(PointR1 _pointR1)
    {
        x = _pointR1.getX();
    }

    /** The method returns coordinate x of point1D
     * @return  coordinate x of point1D
     */
    public double getX()
    {
        return x;
    }

    /** The method sets coordinate x of point1D
     * @param _x coordinate x of point1D
     */
    public void setX(double _x)
    {
        x = _x;
    }

    public String writeToString()
    {
        return Double.toString(x);
    }
}
