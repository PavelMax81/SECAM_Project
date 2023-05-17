package corecalc;
   /*
    * @(#) FractureMain.java version 1.1;       Jan 2007
    *
    * Copyright (c) 2002-2007 DIP Labs, Ltd. All Rights Reserved.
    *
    * Class for realization of application simulating fracture.
    *
    *=============================================================
    *  Last changes :
    *         16 May 2007 by Pavel V. Maksimov (use of class "Date"
    *                for determination of time of execution of the program).
    *            File version 1.1
    */

   /** Class for realization of application simulating fracture.
    * @author Dmitrii D. Moiseenko & Pavel V. Maksimov
    * @version 1.1 - Jan 2006
    */

    import java.util.*;
    import grainsCore.*;
    import interf.UICommon;
    import javafx.scene.control.TextArea;
    import util.*;
    import java.io.*;

    public class GrainsHCPMain
    {
        /** The variable is responsible for showing of boundary cells of each grain. */
        protected byte   show_grain_bounds;

        /** Name of file with information about parameters of layer structure */
        protected String layers_file_name;

        /** Thickness of surface layer */
        protected double layer1_thickness;

        /** Thickness of upper intermediate layer */
        protected double layer2_thickness;

        /** Thickness of lower intermediate layer */
        protected double layer3_thickness;

        /** Type of intermediate layer */
        protected byte   layer2_type;

        /** Type of specimen according to its inner structure*/
        protected byte   specimen_type;

        /** Size of structure element of intermediate layer */
        protected double layer2_elem_size;        
        
        /** Type of distribution of grain embryos in surface layer */
        protected byte surface_layer_embryo_distr_type;
        
        /** Type of distribution of grain embryos in upper intermediate layer */
        protected byte upper_inter_layer_embryo_distr_type;
        
        /** Type of distribution of grain embryos in lower intermediate layer */
        protected byte lower_inter_layer_embryo_distr_type;
        
        /** Type of distribution of grain embryos in upper substrate */
        protected byte upper_substrate_embryo_distr_type;
        
        /** Type of distribution of grain embryos in lower substrate */
        protected byte lower_substrate_embryo_distr_type;

        /** The empty constructor of the class.
         * @param completed_steps text area
         */
        public GrainsHCPMain(TextArea completed_steps)
        {
            Date start_date = new Date();
       /*
            completed_steps.setText(completed_steps.getText());
            completed_steps.setText(completed_steps.getText()+"The time of start: " + start_date.getTime() + " milliseconds since 01.01.1970.");
            completed_steps.setText(completed_steps.getText());
        */
            String file_name;
            GrainsHCPTask grTask;
            
            /* Block 19 continue */
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(GrainsHCPCommon.TASK_PATH+
                                     GrainsHCPCommon.TASK_NAME_FILE+GrainsHCPCommon.TASK_EXTENSION));

                file_name = GrainsHCPCommon.TASK_PATH+br.readLine()+GrainsHCPCommon.TASK_EXTENSION;

                readTask(file_name, completed_steps);

                if(specimen_type == Common.MULTIGRANULAR_SPECIMEN)
                {
                    System.out.println("Specimen type: multigranular");
                    completed_steps.setText(completed_steps.getText()+"\nSpecimen type: multigranular");
                }

                if(specimen_type == Common.TRIPLE_LAYER_SPECIMEN)
                {
                    System.out.println("Specimen type: three layers");
                    completed_steps.setText(completed_steps.getText()+"\nSpecimen type: three layers");
                }
            }
            catch(IOException io_exc)
            {
                completed_steps.setText(completed_steps.getText()+"\nError: "+io_exc);
                System.out.println("Error: "+io_exc);
            }
            
            // True if surface layers will be simulated
            boolean layers = true;
            
            if(specimen_type == Common.MULTIGRANULAR_SPECIMEN)
            {
                //TEST
                layer1_thickness = 0;
                layer2_thickness = 0;
                layer3_thickness = 0;
                layer2_elem_size = 0;

                /* Block 20 */
                grTask = new GrainsHCPTask(layers, layer2_type, layer1_thickness, layer2_thickness, layer3_thickness, layer2_elem_size, completed_steps,
                                           surface_layer_embryo_distr_type, upper_inter_layer_embryo_distr_type, lower_inter_layer_embryo_distr_type,
                                           upper_substrate_embryo_distr_type, lower_substrate_embryo_distr_type);
            }
            
            // Creation of 2-layer specimen
          //  grTask = new GrainsHCPTask(Common.CREATE_SECTION_Y, 0.5);

            if(specimen_type == Common.TRIPLE_LAYER_SPECIMEN)
            {
            //    grTask = new GrainsHCPTask(layer2_type, layer1_thickness,
              //                             layer2_thickness, layer3_thickness, layer2_elem_size, completed_steps);

                /* Block 22 */
                System.out.println();
                System.out.println("surface_layer_embryo_distr_type:     "+surface_layer_embryo_distr_type);
                System.out.println("upper_inter_layer_embryo_distr_type: "+upper_inter_layer_embryo_distr_type);
                System.out.println("lower_inter_layer_embryo_distr_type: "+lower_inter_layer_embryo_distr_type);
                System.out.println("upper_substrate_embryo_distr_type:   "+upper_substrate_embryo_distr_type);
                System.out.println("lower_substrate_embryo_distr_type:   "+lower_substrate_embryo_distr_type);
                System.out.println();
                        
                grTask = new GrainsHCPTask(layers, layer2_type, layer1_thickness, layer2_thickness, layer3_thickness, layer2_elem_size, completed_steps,
                                           surface_layer_embryo_distr_type, upper_inter_layer_embryo_distr_type, lower_inter_layer_embryo_distr_type,
                                           upper_substrate_embryo_distr_type, lower_substrate_embryo_distr_type);
            }
            
            Date finish_date = new Date();
            
       //     completed_steps.setText(completed_steps.getText()+"\n\nThe time of finish: " + finish_date.getTime()  + " milliseconds since 01.01.1970.");
            
            long time_period = finish_date.getTime() - start_date.getTime();
            
            completed_steps.setText(completed_steps.getText()+"\n\nThe period of execution of the program: " + time_period + " milliseconds.");
        }

        /** The method starts the application simulating fracture.
         * @param args an array of command-line arguments
         */
        public static void main(java.lang.String[] args)
        {
            // Field is intended for write all completed steps in progress frame
            TextArea completed_steps = new TextArea();

            /* Block 19*/

            // Creation of grain structure
            GrainsHCPMain grMain = new GrainsHCPMain(completed_steps);
        }

        /** The method reads task parameters from the file.
         * @param file_name name of file containing task parameters
         * @param completed_steps text area
         */
        private void readTask(String file_name, TextArea completed_steps)
        {
            Properties task_properties = new Properties();

            try
            {
                FileInputStream fin = new FileInputStream(file_name);
                task_properties.load(fin);

                completed_steps.setText(completed_steps.getText()+"\nName of read file with task parameters: "+file_name);
                System.out.println("Name of read file with task parameters: "+file_name);

                show_grain_bounds = (new Byte(task_properties.getProperty("show_grain_bounds"))).byteValue();
                layers_file_name  = task_properties.getProperty("layers_file_name");

                fin.close();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
                System.out.println("ERROR: "+io_ex);
            }
            
      //      layers_file_name = "./user/spec_db/alum_10X10X10/init_cond_db/alum_10X10X10.lrs";
            
            // Reading of information about layers
            if(!layers_file_name.equals(Common.NOT_AVAILABLE))
            try
            {
                FileInputStream fin = new FileInputStream(layers_file_name);
                task_properties.load(fin);

                completed_steps.setText(completed_steps.getText()+"\nName of read file with information about layers: "+layers_file_name+"\n");
                System.out.println("Name of read file with information about layers: "+layers_file_name);
                
                layer1_thickness = (new Double(task_properties.getProperty("layer1_thickness"))).doubleValue();
                layer2_thickness = (new Double(task_properties.getProperty("layer2_thickness"))).doubleValue();
                layer3_thickness = (new Double(task_properties.getProperty("layer3_thickness"))).doubleValue();
                layer2_type      = (new Byte  (task_properties.getProperty("layer2_type"     ))).byteValue  ();
                layer2_elem_size = (new Double(task_properties.getProperty("layer2_elem_size"))).doubleValue();
                
          //      layer1_thickness = layer1_thickness/2;
          //      layer2_thickness = layer2_thickness/2;
          //      layer3_thickness = layer3_thickness/2;
                
                /** Type of distribution of grain embryos in surface layer */
                surface_layer_embryo_distr_type     = (new Byte (task_properties.getProperty(UICommon.SURFACE_LAYER_EMBRYO_DISTRIBUTION_TYPE))).byteValue();
                
                /** Type of distribution of grain embryos in upper intermediate layer */
                upper_inter_layer_embryo_distr_type = (new Byte (task_properties.getProperty(UICommon.UPPER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE))).byteValue();
        
                /** Type of distribution of grain embryos in lower intermediate layer */
                lower_inter_layer_embryo_distr_type = (new Byte (task_properties.getProperty(UICommon.LOWER_INTER_LAYER_EMBRYO_DISTRIBUTION_TYPE))).byteValue();
        
                /** Type of distribution of grain embryos in upper substrate */
                upper_substrate_embryo_distr_type   = (new Byte (task_properties.getProperty(UICommon.UPPER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE))).byteValue();
        
                /** Type of distribution of grain embryos in lower substrate */
                lower_substrate_embryo_distr_type   = (new Byte (task_properties.getProperty(UICommon.LOWER_SUBSTRATE_EMBRYO_DISTRIBUTION_TYPE))).byteValue();

                specimen_type = Common.TRIPLE_LAYER_SPECIMEN;

                fin.close();
            }
            catch(IOException io_ex)
            {
                completed_steps.setText(completed_steps.getText()+"\nERROR: "+io_ex);
                System.out.println("ERROR: "+io_ex);
            }
            else
            {
                specimen_type = Common.MULTIGRANULAR_SPECIMEN;

                completed_steps.setText(completed_steps.getText()+"\nThe specimen does not have layer structure!\n");
                System.out.println("The specimen does not have layer structure!");
            }
        }
    }