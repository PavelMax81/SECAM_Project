package grainsCore;

   /*
    * @(#)Three.java version 1.0;       Feb 2003
    *
    * Copyright (c) 2002-2003 DIP Labs, Ltd. All Rights Reserved.
    * 
    * Class for info about three of integers.
    *
    *=============================================================
    *  Last changes :
    *         20 Dec 2006 by Pavel V. Maksimov (change of the field names)
    *            File version 1.0.1
    */

   /** Class for info about three of integers.
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.0 - Feb 2003
    */

    public class Three extends Pair
    {
       /** Third integer of three */
        int indexK;
        
       /** The constructor creates three of integers 
        * those are equal to the given real numbers. 
        * @param _indexI first real number
        * @param _indexJ second real number   
        * @param _indexK third real number
        */
        public Three(double _indexI, double _indexJ, double _indexK)
        {
            super (_indexI, _indexJ);
            indexK = (int) _indexK;
        }
   
       /** The constructor creates three of integers.
        * @param _indexI first integer of three
        * @param _indexJ second integer of three
        * @param _indexK third integer of three
        */
        public Three(int _indexI, int _indexJ, int _indexK)
        {
            super (_indexI, _indexJ);
            indexK = _indexK;
        }
       
       /** The constructor creates three of integers.
        * @param _three three of integers
        */
        public Three(Three _three) 
        {
            super(_three.getI(), _three.getJ());
            indexK = _three.getK();
        }
        
       /** The empty constructor creates three of integers.
        */
        public Three() 
        {
        }
        
       /** The method checks if three equals to other three
        * @return true if three equals to other three
        * @param sample other three
        */
        public boolean equals(Three sample) 
        {
            if((indexI == sample.getI()) && (indexJ == sample.getJ()) && (indexK == sample.getK()))
                return true;
            else
                return false;
        }
        
       /** The method returns third integer of three
        * @return third integer of three
        */             
        public int getK()
        {
            return indexK;
        }
      
       /** The method sets third integer of three
        * @param _indexK third number of three
        */
        public void setK(int _indexK)
        {
            indexK = _indexK;
        }
    }