/**
 * @(#) ControllGrainStructureCreation.java version 1.0.0;       October 1 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for control process of grain structure creation,
 * if creation was interrupted then fail contain info about it
 *
 *=============================================================
 *  Last changes :
 *          19 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.1
 *          Edit: System comments.
 */

/** Class for contrall process of grain structure creation,
 *  if creation was interrupted then fail contain info about it
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - September 2009
 */

package interf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import util.*;

public class ControllGrainStructureCreation
{
    /*
     * This field is intended for create controll file
     */

    File grain_controll_file;
    
    /**
     * This field is intended for write in grain structure controll file
     * info about was grain structure creation interrupted or not
     */

    BufferedWriter info_writer;

    public ControllGrainStructureCreation(String specimen_name)
    {
        System.out.println("ControllGrainStructureCreation: constructor creation start");
        /*
         * If file exist, then it must be rewrited
         */
         
        grain_controll_file = new File(Common.SPEC_PATH+"/"+specimen_name+"/"+Common.INIT_COND_NAME+"/"+specimen_name+"."+Common.CONTROLL_GRAIN_STRUCTURE_CREATOR_FILE_EXTENSION);
        if(grain_controll_file.exists())
            grain_controll_file.delete();

        /*
         * Rewrite file
         */

        try
        {
            grain_controll_file = new File(Common.SPEC_PATH+"/"+specimen_name+"/"+Common.INIT_COND_NAME+"/"+specimen_name+"."+Common.CONTROLL_GRAIN_STRUCTURE_CREATOR_FILE_EXTENSION);

            if(!grain_controll_file.exists())
                grain_controll_file.createNewFile();

            /*
             * Write info about was interrupted grain structure creation or not
             */

            info_writer = new BufferedWriter(new FileWriter(grain_controll_file));
            info_writer.write(UICommon.GRAIN_STRUCTURE_CREATOR_INFORMATION);
            info_writer.close();
        }

        catch(IOException e)
        {
        }
    }
    // Some new
}
