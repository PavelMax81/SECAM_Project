package recGeometry;

/*
 * @(#)VectorR1.java version 1.1;       Nov 2011
 *
 * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
 * 
 * Class for info about the one-dimentional vector
 *
 *=============================================================
 *  Last changes :
 *         04 Feb 2003 by Pavel V. Maksimov(the method getLength());
 * 
 *         22 Nov 2011 by Dmitrii D. Moiseenko (Addition of "writeToString" method);
 * 
 *            File version 1.0.2
 */

 /** Class for info about the one-dimentional vector
 * @authors: Dmitrii D. Moiseenko & Pavel V. Maksimov
 * @version 1.1 - Nov 2011
 */

  public class VectorR1 
  {
      /** Coordinate  x of vector1D */
      double x;

      /** The constructor creates the one-dimensional vector.
       */
      public VectorR1(){}
      /** The constructor creates the one-dimensional vector.
       * @param _x the coordinate of the one-dimensional vector
       */
      public VectorR1(double _x)
      {
          x= _x;
      }
      
      /** The constructor creates the one-dimensional vector.
       * @param _vectorR1 gives it's own coordinate x to the one-dimensional vector
       */
      public VectorR1(VectorR1 _vectorR1)
      {
          x= _vectorR1.getX();
      }

      /** The method returns length of the one-dimensional vector.
       * @param x  coordinate of the one-dimensional vector
       * @return the length of the one-dimensional vector
       */
      public double getLength()
      {
          return Math.abs(getX());
      }
      /** The method returns coordinate x of the one-dimensional vector.
       * @return the coordinate x of the one-dimensional vector
       */
      public double getX()
      {
          return x;
      }
      /** The method sets coordinate x of the one-dimensional vector.
       * @param _x the coordinate of the one-dimensional vector
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
