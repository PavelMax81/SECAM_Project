package recGeometry;

/*
 * @(#)PointR2.java version 1.1;       Jan 2011
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about point2D
 *
 *=============================================================
 *  Last changes :
 *         27 Jan 2003 by Dmitrii D. Moiseenko;
 *         22 Nov 2011 by Dmitrii D. Moiseenko (Addition of "writeToString" method);
 *            File version 1.0.2
 */
 
 /** Class for info about point2D=========================================
 * @authors: Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.1 - Nov 2011
 */
 
public class PointR2 extends PointR1
{ 
  /** Coordinate y of point2D 
  */
  double y;
/** The constructor creates coordinates of point2D
* @param _x  coordinate x of point2D
* @param _y  coordinate y of point2D
*/
    public PointR2(double _x,double _y)
        {
                super(_x);
                y = _y; 
        
        }
/** The constructor creates coordinates of point2D
* @param _pointR2 gives it's own coordinates x, y to point2D
*/
    public PointR2(PointR2 _pointR2)
{
      super(_pointR2.getX());
      y = _pointR2.getY(); 
}
    /** The method returns coordinate y of point2D
    * @return  coordinate y of point2D
    */
    public double getY()
  {
    return y;
  }
    /** The method sets coordinate y of point2D
    * @param _y coordinate y of point2D
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
