/*OK
 * @(#) RecCASpecimenParams.java version 1.0.0;       June 23.06.2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for reading table data from specific file and create table with
 * this values
 *
 *=============================================================
 *  Last changes :
 *          24 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.3
 *          Edit: System comments
 */

/** Class for reading table data from specific file and create table with
 *  this values
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - June 2009
 */

package util;

import java.io.*;
import java.util.*;

public class TextTableFileReader extends BufferedReader
{
    /** This field is intended for remembering table data.
     */    
    protected Vector text_table;
    
    /** This field is intended for remembering file name.
     */    
    protected String table_file_name;

    /** This field is intended for remember  current file with current data.
     */
    protected File table_file;

    /**
     * 
     * @param file_name - file name with its location
     * @throws java.io.FileNotFoundException - catch errors duiring reading file
     */
    public TextTableFileReader(String file_name) throws FileNotFoundException
    {
        super(new FileReader(file_name));

        System.out.println("TextTableFileReader: constructor(String file_name) creation start");

        String string;

        StringTokenizer st;

        try
        {
            // Remember file name
            table_file_name = new String(file_name);

            // read table data
            text_table = new Vector();

            while(this.ready())
            {
                string = readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                    text_table.add(string);

                
            }
        }        
        catch(IOException io_exc)
        {
            System.out.println("ERROR: " + io_exc);
        }
    }

    /**
     *
     * @param current_file - current file with current data
     * @throws java.io.FileNotFoundException - catch errors duiring reading file
     */
    public TextTableFileReader(File current_file) throws FileNotFoundException
    {
        super(new FileReader(current_file));

        System.out.println("TextTableFileReader: constructor(File current_file) creation start");

        String string;

        StringTokenizer st;

        try
        {
            // Remember file
            table_file = current_file;

            // read table data
            text_table = new Vector();

            while(this.ready())
            {
                string = readLine();
                st = new StringTokenizer(string);

                if(st.hasMoreTokens())
                if(!st.nextToken().equals("#"))
                    text_table.add(string);
            }
        }
        catch(IOException io_exc)
        {
            System.out.println("ERROR: " + io_exc);
        }
    }

    /*
     * Convert table data to table (to StringVector type)
     * @return - table with data
     */
    public StringVector convertToTable()
    {
        System.out.println("TextTableFileReader: method: convertToTable: start");

        StringVector table;
        table = new StringVector(text_table);
        return table;
    }
}
