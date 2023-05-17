package recGeometry;;

/*
 * @(#)PointR3.java version 1.1;       Nov 2011
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about point3D
 *
 *=============================================================
 *  Last changes :
 *         28 Jan 2003 by Pavel V. Maksimov;
 *         22 Nov 2011 by Dmitrii D. Moiseenko (Addition of "writeToString" method);
 *            File version 1.0.2
 */
 
 /** Class for info about point3D
 * @authors: Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.1 - Nov 2011
 */
 
public class PointR3 extends PointR2
{ 
  /** Coordinate z of point3D */
  double z;

/** The constructor creates coordinates of point3D
* @param _x  coordinate x of point3D
* @param _y  coordinate y of point3D
* @param _z  coordinate z of point3D
*/
    public PointR3(double _x,double _y,double _z)
{   
	  super(_x, _y);
          setX(_x);
          setY(_y);
          setZ(_z);
}
/** Constructor creates coordinates of point3D
* @param _pointR3 gives it's own coordinates x, y, z to point3D
*/
    public PointR3(PointR3 _pointR3)
{
      super(_pointR3.getX(), _pointR3.getY());
      x = _pointR3.getX();
      y = _pointR3.getY();
      z = _pointR3.getZ();
}
	/** The method returns coordinate z of point3D
    * @return  coordinate z of point3D
    */
    public double getZ()
	{
		return z;
	}
	/** The method sets coordinate z of point3D
	* @param _z coordinate z of point3D
    */
    public void setZ(double _z)
    {
        z=_z;
    }

    /** The method compares this point with sample point: if this point
     * is "more" than sample point then the method returns true, else - false.
     * @param _vectorR3 sample point
     * @return true if this point is "more" than sample point, else - false
     */
    public boolean moreThan(PointR3 _pointR3)
    {
        boolean this_point_is_more_than_sample_point = false;

        if(z  > _pointR3.getZ())
            this_point_is_more_than_sample_point = true;

        if(z  < _pointR3.getZ())
            this_point_is_more_than_sample_point = false;

        if(z == _pointR3.getZ())
        {
            if(y  > _pointR3.getY())
                this_point_is_more_than_sample_point = true;

            if(y  < _pointR3.getY())
                this_point_is_more_than_sample_point = false;

            if(y == _pointR3.getY())
            {
                if(x  > _pointR3.getX())
                    this_point_is_more_than_sample_point = true;

                if(x  < _pointR3.getX())
                    this_point_is_more_than_sample_point = false;

                if(x == _pointR3.getX())
                    this_point_is_more_than_sample_point = false;
            }
        }

        return this_point_is_more_than_sample_point;
    }

    /** The method compares this point with sample point: if this point
     * equals sample point then the method returns true, else - false.
     * @param _pointR3 sample point
     * @return true if this point equals sample point, else - false
     */
    public boolean equals(PointR3 _pointR3)
    {
        boolean this_point_equals_sample_point = false;

        if(x==_pointR3.getX())
            if(y==_pointR3.getY())
                if(z==_pointR3.getZ())
                    this_point_equals_sample_point = true;

        return this_point_equals_sample_point;
    }
    
    public String writeToString()
    {
          return Double.toString(x)+" "+Double.toString(y)+" "+Double.toString(z);
    }
}
