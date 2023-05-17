/*
 * @(#) RecCASpecimenParams.java version 1.0.0;       June 23.06.2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation data bank of material table.
 *
 *=============================================================
 *  Last changes :
 *          30 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.3
 *          Edit: System comments
 */

/** Class for creation data bank of material table.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - June 2009
 */

package util;
import interf.UICommon;
import java.util.*;

public class StringVector extends Vector
{

    /**
     * This field is intended for splitting all table data on individual cells
     */

    protected Vector[] rows;

    /**
     * This field is intended for remember in memory number of table rows and columns
     */

    protected int rows_number, columns_number;

    /**
     * Create vector
     */

    public StringVector()
    {
        super();

        System.out.println("StringVector: constructor creation start");
    }

    /*
     * Creat table with empty rows and columns
     * @param rows_num - number of rows
     * @param col_num - number of columns
     */

    public StringVector(int rows_num, int col_num)
    {
        System.out.println("StringVector: constructor(int rows_num, int col_num) creation start");

        rows_number = rows_num;
        columns_number = col_num;
        
        rows = new Vector[rows_number];

        for(int i=0; i<rows_number; i++)
        {
            rows[i] = new Vector();
            for(int j=0; j<columns_number; j++)
                rows[i].add(" ");
        }
    }

    /**
     * Splitting all table data on individual cells
     * @param init - table data
     */

    public StringVector(Vector init)
    {
        System.out.println("StringVector: constructor(Vector init) creation start");

        /*
         * Intend StringTokenizer for split data
         */

        StringTokenizer single_row_tk;

        /*
         * set number of rows
         */
                        
        rows_number = init.size();
        rows = new Vector[rows_number];

        /*
         * Split table data
         */

        for(int i=0; i<rows_number; i++)
        {
            rows[i] = new Vector();
            single_row_tk = new StringTokenizer((init.get(i)).toString());
            
            while(single_row_tk.hasMoreTokens())
            {
                rows[i].add(single_row_tk.nextToken());

            }

            /*
             * Table check on correct form
             */

            columns_number = rows[i].size();
            if(i>0)
               if(columns_number!=rows[i-1].size())
                   System.out.println(UICommon.CATCH_ERROR__NAME+UICommon.ERR_TABLE_UNCLEAR);
            
        }
    }
    
    /** Creation of table with empty cells or with params of existing table
     * @param str_vector - existing table with empty or with params cells
     */
    public StringVector(StringVector str_vector)
    {
        System.out.println("StringVector: constructor(StringVector str_vector) creation start");

        rows_number = str_vector.getRowsNum();
        columns_number = str_vector.getColumnsNum();

        rows = new Vector[rows_number];

        for(int i=0; i<rows_number; i++)
        {
            rows[i] = new Vector();
            rows[i] = str_vector.getRow(i);
        }
    }

    /*
     * This method is intended for getting value of interest table row
     * @param row_num - number of row
     */

    public Vector getRow(int row_num)
    {
        return rows[row_num];
    }

    /*
     * This method is intended for getting value of interest table column
     * @param col_num - number of column
     */

    public Vector getColumn(int col_num)
    {
        Vector column;
        column = new Vector();
        for (int i=0; i<rows_number; i++)
        {
            column.add(rows[i].get(col_num));
        }
        return column;
    }

    /*
     * This method is intended for getting value of interest table cell
     * @param row_num - number of interest row
     * @param col_num - number of interest column
     */
    public String getCell(int row_num, int col_num)
    {
        return (rows[row_num].get(col_num)).toString();
    }

    /*
     * This method is intended for getting number of table rows
     */
    public int getRowsNum()
    {
        System.out.println("StringVector: method: getRowsNum: start");

        return rows_number;
    }

    /*
     * This method is intended for getting number of table columns
     */
    public int getColumnsNum()
    {
        System.out.println("StringVector: method: getColumnsNum: start");

        return (columns_number);
    }
    
    /*
     * This method is intended for set value of interest table row
     * @param row_num - number of row
     * @param row - values of row
     */

    public void setRow(int row_num, Vector row)
    {
        System.out.println("StringVector: method: setRow: start");

        rows[row_num] = row;
    }

    /*
     * This method is intended for set value of interest table column
     * @param col_num - number of row
     * @param column - values of column
     */

    public void setColumn(int col_num, Vector column)
    {
        System.out.println("StringVector: method: setColumn: start");

        for (int i=0; i<rows_number; i++)
        {
            rows[i].set(col_num,column.get(i));
        }
    }

    /*
     * This method is intended for set value of interest table cell
     * @param row_num - number of row
     * @param col_num - number of column
     * @param value - value of cell
     */

    public void setCell(int row_num, int col_num, String value)
    {
        rows[row_num].set(col_num, value);
    }
    
    /*
     * This method is intended for set number of table rows
     * @param number_of_rows - number of row
     */

    public void setRowsNum(int number_of_rows)
    {
        System.out.println("StringVector: method: setRowsNum: start");

        rows_number = number_of_rows;
    }

    /*
     * This method is intended for set number of table columns
     * @param number_of_columns - number of columns
     */

    public void setColumnsNum(int number_of_columns)
    {
        System.out.println("StringVector: method: setColumnsNum: start");

        columns_number = number_of_columns;
    }

}
