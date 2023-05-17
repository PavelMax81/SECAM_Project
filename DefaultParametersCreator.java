/**
 * @(#) DefaultParametersCreator.java version 1.0.0;       August 5 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation initial, boundary and task default parameters
 *
 *=============================================================
 *  Last changes :
 *          28 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.1.0
 *          Edit: Default boundary conditions creation
 */

/** Class for creation initial, boundary and task default parameters
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - August 2009
 */

package interf;

import java.io.File;
import util.Common;

public class DefaultParametersCreator
{
    /**
     * This field is intended for create files with default parameters
     */

    SaveParams default_params_saver;

    /**
     * This field is intended for controll exist or not file with default parameters
     */

    File default_params_file;

    /**
     * This field is intended foe write in main window all choosed parameters;
     */

    TransferredDataBank transfer_data_bank;

    /**
     * Create default params
     * @param receive_transfer_data_bank - transferred data bank coontained
     * text areas for write default params of initial, boundary conditions and tasks
     */

    public DefaultParametersCreator(TransferredDataBank receive_transfer_data_bank)
    {
        System.out.println("DefaultParametersCreator: constructor creation start");

        /*
         * Remember transferred data bank
         */

        transfer_data_bank = receive_transfer_data_bank;

        /*
         * default initial conditions write
         */

         default_params_file = new File(Common.INIT_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+Common.INIT_COND_NAME+"/"+Common.DEFAULT_INIT_COND_FILE_NAME+"."+Common.INIT_COND_EXTENSION);
         if(default_params_file.exists())
             default_params_file.delete();

         default_params_saver = new SaveParams(new UIInitialCondition(), transfer_data_bank.getUIInterface().getSpecimenPath(), Common.DEFAULT_INIT_COND_FILE_NAME);
         transfer_data_bank.getUIInterface().setInitCondPath(UICommon.DEFAULT_INIT_COND_FILE_NAME);

        /*
         * default boundary conditions write
         */

         default_params_file = new File(Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+Common.BOUND_COND_NAME+"/"+Common.DEFAULT_BOUND_COND_FILE_NAME+"."+Common.BOUND_COND_EXTENSION);
         if(default_params_file.exists())
             default_params_file.delete();
         
         transfer_data_bank.getUIInterface().setBoundCondPath(Common.DEFAULT_BOUND_COND_FILE_NAME);
         transfer_data_bank.setUIBoundaryCondition(new UIBoundaryCondition());
         default_params_saver = new SaveParams(transfer_data_bank);

        /*
         * default task write
         */

         default_params_file = new File(Common.TASK_PATH+"/"+Common.DEFAULT_TASK_FILE_NAME+"."+Common.TASK_EXTENSION);
         if(default_params_file.exists())
             default_params_file.delete();

         transfer_data_bank.setUISpecimen(new UISpecimen(Common.SPEC_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"."));
         transfer_data_bank.getUIInterface().setTaskPath(Common.DEFAULT_TASK_FILE_NAME);
         default_params_saver = new SaveParams(new UITask(), Common.DEFAULT_TASK_FILE_NAME, transfer_data_bank.getUIInterface(), transfer_data_bank.getUISpecimen(),
                                               transfer_data_bank.getUIBoundaryCondition(),"");
    }
}