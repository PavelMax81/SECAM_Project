/*
 * @(#) FolderRemover.java version 1.0.0;       October 2 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for delete any folder
 *
 *=============================================================
 *  Last changes :
 *          30 December 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.2
 *          Edit: Change deepness from range 2 to range 3
 */

/** Class for delete any folder
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - October 2009
 */

package interf;

import java.io.File;

public class FolderRemover
{
    /**
     * This field is intended for do all operations with 
     * folder which will be deleted
     */
    
    File folder;
    
    /**
     * This field is intended for serial removal of all files
     * in range 1,2 and range 3
     */
    
    File range_1_file_remover, range_2_file_remover,
            range_3_file_remover;

    /**
     * This field is intended for presentation files of 1st, 2d and 3d ranges
     * in array of string type
     */

    String [] names_of_1_ranged_files, names_of_2_ranged_files,
            names_of_3_ranged_files;

    /**
     * Delete folder
     * @param directory - directory of folder
     */

    public FolderRemover(String directory)
    {
        System.out.println("FolderRemover: constructor creation start");

        /*
         * init_cond_db folder removed at first with all content
         */

        folder = new File(directory);

        if(folder.isDirectory())
        {
            /*
             * Read all content of folder
             */

            names_of_1_ranged_files = new String[folder.list().length];
            for(int k = 0; k < folder.list().length; k++)
            {
                names_of_1_ranged_files[k] = new String(folder.list()[k]);
            }

            /*
             * Delete each file in each folder
             */

            for(int i=0;i<names_of_1_ranged_files.length;i++)
            {


                range_1_file_remover = new File(folder.toString()+"/"+names_of_1_ranged_files[i]);

                if(range_1_file_remover.isDirectory())
                {
                    names_of_2_ranged_files = new String[range_1_file_remover.list().length];
                    for(int k = 0; k < range_1_file_remover.list().length; k++)
                    {
                        names_of_2_ranged_files[k] = new String(range_1_file_remover.list()[k]);
                    }

                    for(int j = 0; j < names_of_2_ranged_files.length; j++)
                    {
                        range_2_file_remover = new File(folder.toString()+"/"+names_of_1_ranged_files[i]+"/"+names_of_2_ranged_files[j]);
                        
                        if(range_2_file_remover.isDirectory())
                        {
                            names_of_3_ranged_files = new String[range_2_file_remover.list().length];
                            for(int p = 0; p < range_2_file_remover.list().length; p++)
                            {
                                names_of_3_ranged_files[p] = new String(range_2_file_remover.list()[p]);
                            }
                            
                            for(int q = 0; q < names_of_3_ranged_files.length; q++)
                            {
                                range_3_file_remover = new File(folder.toString()+"/"+names_of_1_ranged_files[i]+"/"+names_of_2_ranged_files[j]+"/"+names_of_3_ranged_files[q]);
                                
                                range_3_file_remover.delete();
                            }
                        }
                        else
                        {
                            range_2_file_remover.delete();
                        }

                        range_2_file_remover.delete();
                    }
                    range_1_file_remover.delete();
                }
                else
                {
                    range_1_file_remover.delete();
                }
            }

            /*
             * Delete folder itself
             */

            folder.delete();
        }
        else
            folder.delete();
    }
}
