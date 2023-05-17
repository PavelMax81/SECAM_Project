package grainsCore;

   /*
    * @(#)Pair.java version 1.0;       Jan 2003
    *
    * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
    * 
    * Class for info about pair of integers.
    *
    *=============================================================
    *  Last changes :
    *         20 Dec 2006 by Pavel V. Maksimov (change of the field names)
    *            File version 1.0.1
    */

   /** Class for info about pair of integers.
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.0 - Jan 2003
    */ 
    public class Pair 
    {
      /** First integer of pair */
       int indexI;

      /** Second integer of pair */
       int indexJ;
       
       /** First number of pair */	
       double firstDouble;
       
       /** Second real number of pair */	       
       double secondDouble;
       
       /** The constructor creates pair of integers 
        * those are equal to the given real numbers. 
        * @param _firstDouble first real number
        * @param _secondDouble second real number
        */
        public Pair(double _firstDouble, double _secondDouble) 
        {
            firstDouble = _firstDouble;
            secondDouble = _secondDouble;
        }
        
      /** The constructor creates pair of integers
       * @param _indexI first integer of pair
       * @param _indexJ second integer of pair
       */
       public Pair(int _indexI, int _indexJ)
       {
           indexI = _indexI;
           indexJ = _indexJ;
       }
	
      /** The constructor creates pair of integers
       * @param _pair pair of integers
       */
       public Pair(Pair _pair) 
       {
           indexI = _pair.getI();
           indexJ = _pair.getJ();
       }
       
      /** The empty constructor creates pair of integers.
       */
       public Pair() 
       {
       }
        
      /** The method checks if pair equals to other pair
       * @return true if pair equals to other pair
       * @param sample other pair
       */
       public boolean equals(Pair sample) 
       {
           if((indexI == sample.getI()) && (indexJ == sample.getJ()))
               return true;
           else
               return false;
       }
       
      /** The method returns first integer of pair
       * @return first integer of pair
       */
       public int getI()
       {
           return indexI;
       }
       
      /** The method returns second integer of pair
       * @return second integer of pair
       */
       public int getJ()
       {
           return indexJ;
       }
       
      /** The method sets first integer of pair
       * @param _indexI first integer of pair
       */
       public void setI(int _indexI)
       {
           indexI = _indexI;
       }
       
      /** The method sets second integer of pair
       * @param _indexJ second integer of pair
       */
       public void setJ(int _indexJ) 
       {
           indexJ = _indexJ;
       }
       
      /** The method returns first real number of pair
       * @return first real number of pair
       */
       public double getFirstDouble()
       {
           return firstDouble;
       }
       
      /** The method returns second real number of pair
       * @return second real number of pair
       */
       public double getSecondDouble()
       {
           return secondDouble;
       }
       
      /** The method sets first real number of pair
       * @param _firstDouble first number of pair
       */
       public void setFirstDouble(double _firstDouble)
       {
           firstDouble = _firstDouble;
       }
       
      /** The method sets second real number of pair
       * @param _secondDouble second number of pair
       */
       public void setSecondDouble(double _secondDouble)
       {
           secondDouble = _secondDouble;
       }
    }