package recGeometry;

/*
 * @(#)ToposR1.java version 1.0;       Jan 2003
 *
 * Copyright (c) 2002-2003 Dip Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about topos1D
 *
 *=============================================================
 *  Last changes :
 *         28 Jan 2003 by Pavel V. Maksimov
 *            File version 1.0.1
 */
 
 /** Class for info about topos1D
 * @authors: Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.0 - Jan 2003
 */ 
public class ToposR1 extends VectorR1
{
        /** Start point1D of topos1D */
    PointR1 startPointR1;
        /** Vector1D of topos1D */
    VectorR1 vectorR1;

    /** The constructor creates vector1D and start point1D of topos1D  
    * @param _startPointR1 start point1D of topos1D  
    * @param _vectorR1 vector1D of topos1D  
    */
    public ToposR1(PointR1 _startPointR1, VectorR1 _vectorR1)
{
      super(_vectorR1);
      startPointR1 = new PointR1(_startPointR1);
}
    /** The constructor creates topos1D  
    * @param _toposR1 topos1D 
    */
    public ToposR1(ToposR1 _toposR1)
{
      super(_toposR1.getVectorR1());
      startPointR1 = new PointR1(_toposR1.getStartPointR1());
      vectorR1 = new VectorR1(_toposR1.getVectorR1());
}
/** The method returns end point1D of topos1D 
  * @return end point1D of topos1D
  */
public PointR1 getEndPointR1()
{
        double xEndR1 = startPointR1.getX() + getX();
        PointR1 endPointR1 = new PointR1(xEndR1);
        return endPointR1;
}
/** The method returns start point1D of topos1D 
  * @return start point1D of topos1D
  */
public PointR1 getStartPointR1()
{
        return startPointR1;
}
   /** The method returns vector1D of topos1D 
    * @return vector1D of topos1D
    */
    public VectorR1 getVectorR1() 
{
      return vectorR1;
}
    /** The method sets start point1D of topos1D
    * @param _startPointR1 start point1D of topos1D  
    */
    public void setStartPointR1(PointR1 _startPointR1)
{
      startPointR1 = new PointR1(_startPointR1);
}
    /** The method sets vector1D of topos1D
    * @param _vectorR1 vector1D of topos1D  
    */
    public void setVectorR1(VectorR1 _vectorR1) 
    {   
      vectorR1 = new VectorR1(_vectorR1);
    }
}
