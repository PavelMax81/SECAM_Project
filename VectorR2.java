package recGeometry;

/*
 * @(#)VectorR2.java version 1.1;       Nov 2011
 *
 * Copyright (c) 2002-2003 Dip Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about the two-dimentional vector
 *
 *=============================================================
 *  Last changes :
 *          04 Feb 2003 by Pavel V. Maksimov(the method getLength());
 * 
 *          22 Nov 2011 by Dmitrii D. Moiseenko  (Addition of "writeToString" method);
 * 
 *            File version 1.0.2
 */
 
 /** Class for info about the two-dimentional vector
 * @authors: Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.1 - Nov 2011
 */
 
public class VectorR2 extends VectorR1
{
    /** Coordinate y of vector2D */
    double y;

    /** The constructor creates the two-dimensional vector
     */
    public VectorR2()
    {}
    
    /** The constructor creates the two-dimensional vector
     * @param _x  the coordinate x of the two-dimensional vector
     * @param _y  the coordinate y of the two-dimensional vector
     */
    public VectorR2(double _x, double _y)
    {
        super(_x);
        setY(_y);
    }
    
    /** The constructor creates the two-dimensional vector
     * @param _vectorR2 gives it's own coordinates x, y to the two-dimensional vector
     */
    public VectorR2(VectorR2 _vectorR2)
    {
        super(_vectorR2.getX());
        y = _vectorR2.getY();
        setY(_vectorR2.getY());
    }

    /** The method returns length of the two-dimensional vector
     * @param x  the coordinate x of the two-dimensional vector
     * @param y  the coordinate y of the two-dimensional vector
     * @return  the length of the two-dimensional vector
     */
    public double getLength()
    {
	  return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
    }
    
    /** The method returns coordinate y of the two-dimensional vector
     * @return the coordinate y of the two-dimensional vector
     */
    public double getY()
    {
        return y;
    }

    /** The method sets the coordinate y of the two-dimensional vector
     * @param _y  the coordinate y of the two-dimensional vector
     */
    public void setY(double _y) 
    {   
      y = _y;
    }
     public String writeToString()
    {
        return Double.toString(x)+" "+Double.toString(y);
    }
}
