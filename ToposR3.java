package recGeometry;

/*
 * @(#)ToposR3.java version 1.0;       Jan 2003
 *
 * Copyright (c) 2002-2003 Dip Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about topos3D
 *
 *=============================================================
 *  Last changes :
 *         28 Jan 2003 by Pavel V. Maksimov
 *            File version 1.0.1
 */
 
 /** Class for info about topos3D
 * @authors: Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.0 - Jan 2003
 */
public class ToposR3 extends VectorR3
{
    /** Start point3D of topos3D */
    PointR3 startPointR3;
	
    /** Vector3D of topos3D */
    VectorR3 vectorR3;

    /** Empty constructor
     */
    public ToposR3()
    {
        super();
        startPointR3 = new PointR3(0,0,0);
    }

   /** The constructor creates coordinates of vector3D and start point3D of topos3D  
    * @param _startPointR3 start point3D of topos3D  
    * @param _vectorR3 vector3D of topos3D  
    */
    public ToposR3(PointR3 _startPointR3, VectorR3 _vectorR3)
    {
	super(_vectorR3);
	startPointR3 = new PointR3(_startPointR3);
        vectorR3 = new VectorR3(_vectorR3);
    }
    
    /** The constructor creates topos3D
     * @param _toposR3 topos3D
     */
    public ToposR3(ToposR3 _toposR3)
    {
      super(_toposR3.getVectorR3());
      startPointR3 = new PointR3(_toposR3.getStartPointR3());
      vectorR3 = new VectorR3(_toposR3.getVectorR3());
    }

    /** The method returns end point3D of topos3D
     * @return end point3D of topos3D
     */
    public PointR3 getEndPointR3()
    {
	PointR3 endPointR3 = new PointR3(startPointR3.getX() + vectorR3.getX(),
	                                 startPointR3.getY() + vectorR3.getY(),
	                                 startPointR3.getZ() + vectorR3.getZ());
	return endPointR3;
    }

   /** The method returns start point3D of topos3D 
    * @return start point3D of topos3D
    */
    public PointR3 getStartPointR3()
    {
	return startPointR3;
    }

   /** The method returns coordinate X of start point3D of topos3D
    * @return coordinate X of start point3D of topos3D
    */
    public double getStartPointCoordX()
    {
        return startPointR3.getX();
    }

   /** The method returns coordinate Y of start point3D of topos3D
    * @return coordinate Y of start point3D of topos3D
    */
    public double getStartPointCoordY()
    {
        return startPointR3.getY();
    }

   /** The method returns coordinate Z of start point3D of topos3D
    * @return coordinate Z of start point3D of topos3D
    */
    public double getStartPointCoordZ()
    {
        return startPointR3.getZ();
    }

   /** The method returns vector3D of topos3D 
    * @return vector3D of topos3D
    */
    public VectorR3 getVectorR3() 
    {
        return vectorR3;
    }

   /** The method returns coordinate X of vectorR3 of topos3D
    * @return coordinate X of vectorR3 of topos3D
    */
    public double getVectorCoordX()
    {
        return vectorR3.getX();
    }

   /** The method returns coordinate Y of vectorR3 of topos3D
    * @return coordinate Y of vectorR3 of topos3D
    */
    public double getVectorCoordY()
    {
        return vectorR3.getY();
    }

   /** The method returns coordinate Z of vectorR3 of topos3D
    * @return coordinate Z of vectorR3 of topos3D
    */
    public double getVectorCoordZ()
    {
        return vectorR3.getZ();
    }

    /** The method sets start point3D of topos3D
     * @param _startPointR3 start point3D of topos3D
     */
    public void setStartPointR3(PointR3 _startPointR3)
    {
        startPointR3 = new PointR3(_startPointR3);
    }
    
    /** The method sets vector3D of topos3D
     * @param _vectorR3 vector3D of topos3D
     */
    public void setVectorR3(VectorR3 _vectorR3) 
    {
        vectorR3 = new VectorR3(_vectorR3);
        vectorR3.setX(_vectorR3.getX());
    }

    /** The method compares this topos with sample topos: if this topos
     * equals sample topos then the method returns true, else - false.
     * @param _toposR3 sample topos
     * @return true if this topos equals sample topos, else - false
     */
    public boolean equalStartPoints(ToposR3 _toposR3)
    {
        boolean egual_start_points = false;

        PointR3 _pointR3 = _toposR3.getStartPointR3();

        if(startPointR3.getX() == _pointR3.getX())
        if(startPointR3.getY() == _pointR3.getY())
        if(startPointR3.getZ() == _pointR3.getZ())
            egual_start_points = true;

        return egual_start_points;
    }
}
