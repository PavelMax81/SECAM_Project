package recGeometry;
import JSci.maths.matrices.*;

/*
 * @(#)VectorR3.java version 1.1;       Nov 2011
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about the three-dimensional vector
 *
 *=============================================================
 *  Last changes :
 *          04 Feb 2003 by Pavel V. Maksimov(the method getLength());
 *
 *          11 Apr 2011 by Pavel V. Maksimov(methods add(), multiply(), normalize());
 * 
 *          22 Nov 2011 by Dmitrii D. Moiseenko (Addition of "writeToString" method);
 *
 *            File version 1.0.3
 */
 
 /** Class for info about the three-dimensional vector
 * @authors:  Pavel V. Maksimov & Dmitrii D. Moiseenko
 * @version 1.1 - Nov 2011
 */
 
public class VectorR3 extends VectorR2
{   
    /** Coordinate z of the three-dimensional vector */
    double z;

    /** The constructor creates the three-dimensional vector
    * @param _x  the coordinate x of the three-dimensional vector
    * @param _y  the coordinate y of the three-dimensional vector
    * @param _z  the coordinate z of the three-dimensional vector
    */
    public VectorR3(double _x, double _y, double _z)
    {
        super(_x, _y);
        setZ(_z);
    }

    /** The constructor creates the three-dimensional vector
    * @param _vectorR2 gives it's own coordinates x, y to the three-dimensional vector
    */
    public VectorR3(VectorR3 _vectorR3)
    {
        super(_vectorR3.getX(), _vectorR3.getY());
        setX(_vectorR3.getX());
        setY(_vectorR3.getY());
        setZ(_vectorR3.getZ());
    }

    /** Empty constructor of the class
     */
    public VectorR3()
    {
        super(0, 0);
        setZ(0);
    }
    
    /** The method returns the length of the three-dimensional vector
    * @param x  the coordinate x of the three-dimensional vector
    * @param y  the coordinate y of the three-dimensional vector
    * @param z  the coordinate z of the three-dimensional vector
    * @return the length of the three-dimensional vector
    */
    public double getLength()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /** The method returns the coordinate z of the three-dimensional vector
    * @return the coordinate z of the three-dimensional vector
    */
    public double getZ()
    {
        return z;
    }

    /** The method sets the coordinate z of the three-dimensional vector
    * @param _z the coordinate z of the three-dimensional vector
    */
    public void setZ(double _z)
    {
        z = _z;
    }
    
   /** The method sets the coordinates of the three-dimensional vector
    * @param _x the coordinate x of the three-dimensional vector
    * @param _y the coordinate y of the three-dimensional vector
    * @param _z the coordinate z of the three-dimensional vector
    */
    public void setCoordinates(double _x, double _y, double _z)
    {
        x = _x;
        y = _y;
        z = _z;
    }
    
    /** The method normalizes 3D vector
     */
    public void normalize()
    {
        // Vector length
        double length = getLength();

        // Normalization
        if(length != 0)
        {
          setX(x/length);
          setY(y/length);
          setZ(z/length);
        }
    }

    /** The method multiplies 3D vector by the factor
     * @param factor the factor of the vector
     */
    public void multiply(double factor)
    {
        // Multiplication by the factor
        setX(factor*getX());
        setY(factor*getY());
        setZ(factor*getZ());
    }
    
    /** The method multiplies 3D vector by the matrix.
     * @param matrix the matrix the vector is multiplied by
     */
    public void multiply(DoubleMatrix matrix)
    {
        double x_old = x;
        double y_old = y;
        double z_old = z;
        
        // Multiplication by the matrix
        setX(matrix.getElement(0, 0)*x_old + matrix.getElement(0, 1)*y_old + matrix.getElement(0, 2)*z_old);
        setY(matrix.getElement(1, 0)*x_old + matrix.getElement(1, 1)*y_old + matrix.getElement(1, 2)*z_old);
        setZ(matrix.getElement(2, 0)*x_old + matrix.getElement(2, 1)*y_old + matrix.getElement(2, 2)*z_old);
    }

    /** The method adds specified vector to this vector
     * @param added_vector specified vector
     */
    public void add(VectorR3 added_vector)
    {
        setX(x+added_vector.getX());
        setY(y+added_vector.getY());
        setZ(z+added_vector.getZ());
    }

    /** The method compares this vector with sample vector: if this vector
     * is "more" than sample vector then the method returns true, else - false.
     * @param _vectorR3 sample vector
     * @return true if this vector is "more" than sample vector, else - false
     */
    public boolean moreThan(VectorR3 _vectorR3)
    {
        boolean this_vec_is_more_than_sample_vec = false;

        if(z  > _vectorR3.getZ())
            this_vec_is_more_than_sample_vec = true;

        if(z  < _vectorR3.getZ())
            this_vec_is_more_than_sample_vec = false;

        if(z == _vectorR3.getZ())
        {
            if(y  > _vectorR3.getY())
                this_vec_is_more_than_sample_vec = true;

            if(y  < _vectorR3.getY())
                this_vec_is_more_than_sample_vec = false;

            if(y == _vectorR3.getY())
            {
                if(x  > _vectorR3.getX())
                    this_vec_is_more_than_sample_vec = true;

                if(x  < _vectorR3.getX())
                    this_vec_is_more_than_sample_vec = false;

                if(x == _vectorR3.getX())
                    this_vec_is_more_than_sample_vec = false;
            }
        }

        return this_vec_is_more_than_sample_vec;
    }

    /** The method compares this vector with sample vector: if this vector
     * equals sample vector then the method returns true, else - false.
     * @param _vectorR3 sample vector
     * @return true if this vector equals sample vector, else - false
     */
    public boolean equals(VectorR3 _vectorR3)
    {
        boolean this_vec_equals_sample_vec = false;

        if(x==_vectorR3.getX())
            if(y==_vectorR3.getY())
                if(z==_vectorR3.getZ())
                    this_vec_equals_sample_vec = true;

        return this_vec_equals_sample_vec;
    }
    
     public String writeToString()
    {
        return Double.toString(x)+" "+Double.toString(y)+" "+Double.toString(z);
    }
}