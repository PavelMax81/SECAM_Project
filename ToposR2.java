package recGeometry;

/*
 * @(#)ToposR2.java version 1.0;       Jan 2003
 *
 * Copyright (c) 2002-2003 Dip Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about topos2D
 *
 *=============================================================
 *  Last changes :
 *         28 Jan 2003 by Pavel V. Maksimov
 *            File version 1.0.1
 */
 
 /** Class for info about topos2D
 * @authors: Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.0 - Jan 2003
 */
 
public class ToposR2 extends VectorR2 
{
    /** Start point2D of topos2D */
    PointR2 startPointR2;
    
    /** Vector2D of topos2D */
    VectorR2 vectorR2;
    
    /** The constructor of topos2D */
    public ToposR2(){}
 /** The constructor creates topos2D by coordinates of it's end points.
  * @param start_point_X coordinate X of start point of topos2D.
  * @param start_point_Y coordinate Y of start point of topos2D.
  * @param   end_point_X coordinate X of end point of topos2D.
  * @param   end_point_Y coordinate Y of end point of topos2D.
  */
  public ToposR2(double start_point_X, double start_point_Y,
	             double   end_point_X, double   end_point_Y)
  {
	PointR2 startPointR2 = new PointR2(start_point_X, start_point_Y);
	VectorR2 vectorR2 = new VectorR2(end_point_X-start_point_X, end_point_Y-start_point_Y);
  }
   /** The constructor creates coordinates of vector2D and start point2D of topos2D  
    * @param _startPointR2 start point2D of topos2D  
    * @param _vectorR2 vector2D of topos2D  
    */
public ToposR2(PointR2 _startPointR2, VectorR2 _vectorR2)
{
        super(_vectorR2);
        startPointR2 = new PointR2(_startPointR2);
        vectorR2 = new VectorR2(_vectorR2);
}
    /** The constructor creates topos2D  
    * @param _toposR2 topos2D  
    */
    public ToposR2(ToposR2 _toposR2)
{
      super(_toposR2.getVectorR2());
      startPointR2 = new PointR2(_toposR2.getStartPointR2());
      vectorR2 = new VectorR2(_toposR2.getVectorR2());
}
   /** The method returns end point2D of topos2D 
    * @return end point2D of topos2D
    */
    public PointR2 getEndPointR2() 
{
      PointR2 endPointR2 = new PointR2(startPointR2.getX() + vectorR2.getX(), 
                                       startPointR2.getY() + vectorR2.getY());
      return endPointR2;
}
   /** The method returns start point2D of topos2D 
    * @return start point2D of topos2D
    */
    public PointR2 getStartPointR2()
{
        return startPointR2;
}
   /** The method returns vector2D of topos2D 
    * @return vector2D of topos2D
    */
    public VectorR2 getVectorR2() 
{
      return vectorR2;
}
    /** The method sets start point2D of topos2D
    * @param _startPointR2 start point2D of topos2D  
    */
    public void setStartPointR2(PointR2 _startPointR2)
{
      startPointR2 = new PointR2(_startPointR2);
}
    /** The method sets vector2D of topos2D
    * @param _vectorR2 vector2D of topos2D  
    */
    public void setVectorR2(VectorR2 _vectorR2) 
    {   
       vectorR2 = new VectorR2(_vectorR2);        
    }
}
