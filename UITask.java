/*
 * @(#) UITask.java version 1.0.0;       May 12 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of task's data, wich contain default value of
 * task's parameters and default checked positions of task.
 * This class can load data from file in specific directory.
 * This class can set and get value of parameters and checked positions.
 *
 *=============================================================
 *  Last changes :
 *          26 August 2010 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.9
 *          Edit: Add textarea for task comments
 */

/** Class for creation bank of task's data, wich contain default value of
 *  task's parameters and default checked positions of task.
 *  This class can load data from file in specific directory.
 *  This class can set and get value of parameters and checked positions.
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - May 2009
 */

package interf;

import java.util.Properties;
import java.io.*;
import javafx.scene.control.TextArea;
import util.Common;

public class UITask
{
    /** This field is intended for creation a double value for remembering
     * in memory value of task parameters.
     */
    protected double time_step,total_time,response_rate;
    protected long output_file;

    /** This field is intended for remember task comments.
     */
    protected TextArea task_comments;
    
    /** This field is intended for creating boolean value for remembering
     * in memory checked position of corresponding fields of task,
     * such as "heat transfer","mechanical elating","recrystallization","crack formation".
     */
    protected boolean heat_transfer,recrystallization,mechanical_elating,crack_formation;
    protected boolean equilibrium_state,show_grain_bounds;
    protected boolean calc_heat_expansion, calc_force_moments;   
    protected boolean defects_influence;
    
    /** Boundary type:
     * 0 - homogeneous mechanical loading and homogeneous heating,
     * 1 - mechanical loading by indentor and homogeneous heating,
     * 2 - homogeneous mechanical loading of facets and heating of circle area,
     * 3 - mechanical bending and homogeneous heating,
     * 4 - mechanical shear and homogeneous heating.
     */
    protected int boundary_type;
    
    /** Type of functional dependence of boundary cell parameter on its coordinates.
     */
    protected int boundary_function_type;
    
    /** The returned value is true if anisotropy of specimen is simulated. 
     */
    protected boolean anisotropy;
    
    /** The coordinates of the specimen anisotropy vector
     */
    protected double anis_vector_X, anis_vector_Y, anis_vector_Z;
          
    /** The value of the specimen anisotropy coefficient
     */
    protected double spec_anis_coeff;

    /** Create default bank of data UITask
     */
    public UITask()
    {
        System.out.println("UITask: constructor creation start");

        // The default parameters
        time_step = 0.0;
        total_time = 0.0;
        output_file = 0;
        response_rate = 10.0;

        // The default checked position
        heat_transfer = false;
        recrystallization = false;
        mechanical_elating = false;
        crack_formation = false;
        equilibrium_state= false;
        show_grain_bounds = false;
        calc_heat_expansion = false;
        calc_force_moments = false;
        
        defects_influence = false;

        // Create default task comments
        task_comments = new TextArea("There is no comments!");

        // Set default boundary type
        boundary_type = 0;
        boundary_function_type = 0;
        
        anisotropy = true;
        anis_vector_X = 0;
        anis_vector_Y = 0;
        anis_vector_Z = 0;
        spec_anis_coeff = 0;
    }

    /** This constructor is intended for creation UITask by
     * assignment values of parameter UITask from existing UITask parameters
     * @param some_uitask - copy of UITask
     */
    public UITask(UITask some_uitask)
    {
        System.out.println("UITask: constructor(UITask some_uitask) creation start");

        // assignment double values of existing parameters
        time_step = some_uitask.getTimeStep();
        total_time = some_uitask.getTotalTime();
        output_file = some_uitask.getOutputFile();
        response_rate = some_uitask.getResponseRate();

        // assignment task comments of existing task comments
        task_comments = some_uitask.getTaskComments();

        // assignment existing checked position
        heat_transfer = some_uitask.getHeatTransfer();
        recrystallization = some_uitask.getRecrystallization();
        mechanical_elating = some_uitask.getMechanicalElating();
        crack_formation = some_uitask.getCrackFormation();
        equilibrium_state = some_uitask.getEquilibriumState();
        show_grain_bounds = some_uitask.getShowGrainBounds();
        calc_heat_expansion = some_uitask.getCalcHeatExpansion();
        calc_force_moments = some_uitask.getCalcForceMoments();
        defects_influence = some_uitask.getDefectsInfluence();

        // assignment existing boundary type
        boundary_type = some_uitask.getBoundaryType();
        boundary_function_type = some_uitask.getBoundaryFunctionType();
        // The parameters of the specimen anisotropy
        anisotropy = some_uitask.getAnisPresence();        
        anis_vector_X = some_uitask.getAnisVectorX();
        anis_vector_Y = some_uitask.getAnisVectorY();
        anis_vector_Z = some_uitask.getAnisVectorZ();
        spec_anis_coeff = some_uitask.getSpecimenAnisCoeff();
        
        if((anis_vector_X != 0)&(anis_vector_Y != 0)&(anis_vector_Z != 0)&(spec_anis_coeff != 0))
            anisotropy = true;
        else
            anisotropy = false;
    }

    /**
     * This constructor is intended for creating UITask data bank with
     * loading value of parameters and checked positions from file in
     * specific directory "task_file_name"
     * @param task_file_name - directory and name of readed file
     */

    public UITask(String task_file_name)
    {
        System.out.println("UITask: constructor(String task_file_name) creation start");

        /*
         * Read file in specific directory "task_file_name"
         */
        
        read(task_file_name);
    }

    /*
     * This method is intended for set boundary type of task
     * @param value - boundary type
     */

    public void setBoundaryType(int value)
    {
        boundary_type = value;
    }    
    
    /*
     * This method is intended for set boundary function type of task
     * @param value - boundary function type
     */

    public void setBoundaryFunctionType(int value)
    {
        boundary_function_type = value;
    }
    

    /*
     * Remember task comments
     * @param value - task comments
     */

    public void setTaskComments(TextArea value)
    {
        task_comments = value;
    }

    /*
     * Set value for response rate
     * @param value - value of response rate
     */

    public void setResponseRate(double value)
    {
        response_rate = value;
    }

       
    /** The method returns true if anisotropy of specimen is simulated.
     * @return the returned value is true if anisotropy of specimen is simulated
     */
    public boolean getAnisPresence()
    {
        return anisotropy;
    }    
    
    /** The method returns the coordinate X of the specimen anisotropy vector.
     * @return the coordinate X of the specimen anisotropy vector
     */
    public double getAnisVectorX()
    {
        return anis_vector_X;
    }    
    
    /** The method returns the coordinate Y of the specimen anisotropy vector.
     * @return the coordinate Y of the specimen anisotropy vector
     */
    public double getAnisVectorY()
    {
        return anis_vector_Y;
    }    
    
    /** The method returns the coordinate Z of the specimen anisotropy vector.
     * @return the coordinate Z of the specimen anisotropy vector
     */
    public double getAnisVectorZ()
    {
        return anis_vector_Z;
    }
    
    /** The method returns the value of the specimen anisotropy coefficient.
     * @return the value of the specimen anisotropy coefficient
     */
    public double getSpecimenAnisCoeff()
    {
        return spec_anis_coeff;
    }    
    
    /*
     * This method is intended for take boundary type of task
     * @param return - boundary type
     */

    public int getBoundaryType()
    {
        return boundary_type;
    }

    /*
     * This method is intended for take boundary function type of task
     * @param return - boundary function type
     */

    public int getBoundaryFunctionType()
    {
        return boundary_function_type;
    }
            
    /*
     * This method is intended for calculate or not heat expansion
     * @param value - calculate or not heat expansion
     */

    public void setCalcHeatExpansion(boolean value)
    {
        calc_heat_expansion = value;
    }

    /*
     * This method is intended for calculate or not force moments
     * @param value - calculate or not force moments
     */

    public void setCalcForceMoments(boolean value)
    {
        calc_force_moments = value;
    }

    /*
     * Get info about simulate or not heat expansion
     * @return info about simulate or not heat expansion
     */

    public boolean getCalcHeatExpansion()
    {
        return calc_heat_expansion;
    }
    
    /*
     * Get value of response rate
     * @return - value of response rate
     */

    public double getResponseRate()
    {
        return response_rate;
    }
        
    /** The method sets the boolean value, which is true if anisotropy of specimen is simulated.
     * @param the returned value is true if anisotropy of specimen is simulated
     */
    public void getAnisPresence(boolean _anisotropy)
    {
        anisotropy = _anisotropy;
    }    
    
    /** The method sets the coordinate X of the specimen anisotropy vector.
     * @param _anis_vector_X the coordinate X of the specimen anisotropy vector
     */
    public void setAnisVectorX(double _anis_vector_X)
    {
        anis_vector_X = _anis_vector_X;
    }    
    
    /** The method sets the coordinate Y of the specimen anisotropy vector.
     * @param _anis_vector_Y the coordinate Y of the specimen anisotropy vector
     */
    public void setAnisVectorY(double _anis_vector_Y)
    {
        anis_vector_Y = _anis_vector_Y;
    }    
    
    /** The method sets the coordinate Z of the specimen anisotropy vector.
     * @param _anis_vector_Z the coordinate Z of the specimen anisotropy vector
     */
    public void setAnisVectorZ(double _anis_vector_Z)
    {
        anis_vector_Z = _anis_vector_Z;
    }
    
    /** The method sets the value of the specimen anisotropy coefficient.
     * @param _spec_anis_coeff the value of the specimen anisotropy coefficient
     */
    public void getSpecimenAnisCoeff(double _spec_anis_coeff)
    {
        spec_anis_coeff = _spec_anis_coeff;
    }
    
    /** Get info about calculate or not force moments
     * @return calculate force moments or not
     */
    public boolean getCalcForceMoments()
    {
        return calc_force_moments;
    }
    
    /** The method returns value to determine whether influence of defects 
     * should be taken into account
     * @return value to determine whether influence of defects 
     *         should be taken into account
     */
    public boolean getDefectsInfluence()
    {
        return defects_influence;
    }
    
    /** The method sets value to determine whether influence of defects 
     * should be taken into account
     * @param  value to determine whether influence of defects 
     *         should be taken into account
     */
    public void setDefectsInfluence(boolean _defects_influence)
    {
        defects_influence = _defects_influence;
    }
    
    /** Get task comments
     * @param value - task comments
     * @return 
     */
    public TextArea getTaskComments()
    {
        return task_comments;
    }

    /** This method is intended for set new value of parameter "time step"
     * @param value - new value of time step
     */
    public void setTimeStep(double value)
    {
        time_step = value;
    }

    /** This method is intended for set new value of parameter "total time"
     * @param value - new value of total time
     */
    public void setTotalTime(double value)
    {
        total_time = value;
    }

    /** This method is intended for set new value of parameter "output file"
     * @param value - new value of output file
     */
    public void setOutputFile(long value)
    {
        output_file = value;
    }

    /** This method is intended for set checked or not position - "heat transfer"
     * @param value - value which responsible for checked or not position - "heat transfer"
     */
    public void setHeatTransfer(boolean value)
    {
        heat_transfer = value;
    }

    /** This method is intended for set checked or not position - "recrystallization"
     * @param value - value which responsible for checked or not position - "recrystallization"
     */
    public void setRecrystallization(boolean value)
    {
        recrystallization = value;
    }

    /*
     * This method is intended for set checked or not position - "mechanical elating"
     * @param value - value which responsible for checked or not position - "mechanical elating"
     */

    public void setMechanicalElating(boolean value)
    {
        mechanical_elating = value;
    }

    /*
     * This method is intended for set checked or not position - "crack formation"
     * @param value - value which responsible for checked or not position - "crack formation"
     */

    public void setCrackFormation(boolean value)
    {
        crack_formation = value;
    }

    /*
     * Set new value (selected or not) of equilibrium_state
     * @param value - checked or not
     */

    public void setEquilibriumSate(boolean value)
    {
        equilibrium_state = value;
    }

    /*
     * Set new value (selected or not) of show_grain_bounds
     * @param value - checked or not
     */

    public void setShowGrainBounds(boolean value)
    {
        show_grain_bounds = value;
    }

    /*
     * This method is intended for get value of parameter "time step"
     */

    public double getTimeStep()
    {
        return time_step;
    }

    /*
     * This method is intended for get value of parameter "total time"
     */

    public double getTotalTime()
    {
        return total_time;
    }

    /*
     * This method is intended for get value of parameter "output file"
     */

    public long getOutputFile()
    {
        return output_file;
    }

    /*
     * This method is intended for get information - checked or not position - "heat transfer"
     */
    
    public boolean getHeatTransfer()
    {
        return heat_transfer;
    }

    /*
     * This method is intended for get information - checked or not position - "recrystallization"
     */

    public boolean getRecrystallization()
    {
        return recrystallization;
    }

    /*
     * This method is intended for get information - checked or not position - "mechanical elating"
     */
    
    public boolean getMechanicalElating()
    {
        return mechanical_elating;
    }

    /*
     * This method is intended for get information - checked or not position - "crack formation"
     */

    public boolean getCrackFormation()
    {
        return crack_formation;
    }

    /*
     * Get value (checked or not) of equilibrium_state
     * @return - checked or not
     */

     public boolean getEquilibriumState()
    {
        return equilibrium_state;
    }

     /*
     * Get value (checked or not) of show_grain_bounds
     * @return - checked or not
     */

      public boolean getShowGrainBounds()
    {
        return show_grain_bounds;
    }

    /*
     * This method is intended for loading value of parameters
     * and checked positions from file in specific directory with
     * specific name "task_file_name"
     * @param task_file_name - directory and name of readed file
     */

    public void read(String task_file_name)
    {
        /** Read task comments if they exist
         */
        File file = new File(task_file_name.substring(0, task_file_name.length()-5)+"."+Common.TASK_COMMENTS_EXTENSION);
        FileReader     file_reader;
        BufferedReader buff_reader;
        String         line;
        String         text;
                
        if(file.exists())
        {
            try
            {
                if(task_comments==null)
                    task_comments = new TextArea();
                    
                file_reader = new FileReader(file);
                buff_reader = new BufferedReader(file_reader);
             //   line        = buff_reader.readLine();
                    
             //   while(line != null)
                while(buff_reader.ready())
                {
                    text        = task_comments.getText();
                    line        = buff_reader.readLine();
                    
                    if(!text.equals(""))
                        task_comments.setText(text+"\n"+line);
                    else
                        task_comments.setText(text + line);
                    
                //    line        = buff_reader.readLine();
                }
                
                file_reader.close();
            }
            catch(IOException io_ex)
            {
                System.out.println("Exception: "+io_ex);
            }
        }
        else
        {
            if(task_comments!=null)
                task_comments.setText("There is no comments!");
            else
                task_comments = new TextArea("There is no comments!");
        }

        /** read data from file
         */
        Properties loading_properties = new Properties();
        try
        {
            FileInputStream loader = new FileInputStream(task_file_name);
            System.out.println("TASK FILE ========= TASK FILE ========= TASK FILE ========= TASK FILE =========");
            System.out.println("task_file_name: "+task_file_name);
            loading_properties.load(loader);
            loader.close();
        }
        catch(IOException io_ex)
        {
            System.out.println("Exception: "+io_ex);
        }

        /** read value of parameters from file in directory "task_file_name"
         */
        time_step = Double.valueOf(loading_properties.getProperty(UICommon.TIME_STEP_PROPERTIES)).doubleValue();
        total_time = Double.valueOf(loading_properties.getProperty(UICommon.TOTAL_TIME_PROPERTIES)).doubleValue();
        output_file = Integer.valueOf(loading_properties.getProperty(UICommon.OUTPUT_FILE_PROPERTIES)).longValue();
        response_rate = Double.valueOf(loading_properties.getProperty(UICommon.TASK_RESPONSE_RATE_PROPERTIES)).doubleValue();

        /** read boundary type
         */
        boundary_type = Integer.valueOf(loading_properties.getProperty(UICommon.GLOBAL_BOUNDARY_TYPE_PROPERTIES)).intValue();               
        boundary_function_type = Integer.valueOf(loading_properties.getProperty(UICommon.BOUNDARY_FUNCTION_TYPE_PROPERTIES)).intValue();
        
        /*
        byte anis_presence = Byte.valueOf(loading_properties.getProperty(UICommon.ANISOTROPY_PRESENCE_PROPERTIES)).byteValue();
        anis_vector_X      = Double.valueOf(loading_properties.getProperty(UICommon.ANISOTROPY_VECTOR_X_PROPERTIES)).doubleValue();
        anis_vector_Y      = Double.valueOf(loading_properties.getProperty(UICommon.ANISOTROPY_VECTOR_Y_PROPERTIES)).doubleValue();
        anis_vector_Z      = Double.valueOf(loading_properties.getProperty(UICommon.ANISOTROPY_VECTOR_Z_PROPERTIES)).doubleValue();
        spec_anis_coeff    = Double.valueOf(loading_properties.getProperty(UICommon.SPECIMEN_ANIS_COEFF_PROPERTIES)).doubleValue();
        
        System.out.println("Anisotropy vector is determined: ");
        System.out.println("anis_vector_X   = "+anis_vector_X);
        System.out.println("anis_vector_Y   = "+anis_vector_Y);
        System.out.println("anis_vector_Z   = "+anis_vector_Z);
        System.out.println("spec_anis_coeff = "+spec_anis_coeff);
        */
        
        /** read checked positions
         */
        if (loading_properties.getProperty(UICommon.HEAT_TRANSFER_PROPERTIES).equals("1"))
            heat_transfer = true;
        if (loading_properties.getProperty(UICommon.HEAT_TRANSFER_PROPERTIES).equals("0"))
            heat_transfer = false;
        
        if (loading_properties.getProperty(UICommon.RECRYSTALLIZATION_PROPERTIES).equals("1"))
            recrystallization = true;
        if (loading_properties.getProperty(UICommon.RECRYSTALLIZATION_PROPERTIES).equals("0"))
            recrystallization = false;
        
        if (loading_properties.getProperty(UICommon.MECHANICAL_ELATING_PROPERTIES).equals("1"))
            mechanical_elating = true;
        if (loading_properties.getProperty(UICommon.MECHANICAL_ELATING_PROPERTIES).equals("0"))
            mechanical_elating = false;
        
        if (loading_properties.getProperty(UICommon.CRACK_FORMATION_PROPERTIES).equals("1"))
            crack_formation = true;
        if (loading_properties.getProperty(UICommon.CRACK_FORMATION_PROPERTIES).equals("0"))
            crack_formation = false;
        
        if (loading_properties.getProperty(UICommon.EQUILIBRIUM_STATE_PROPERTIES).equals("1"))
            equilibrium_state = true;        
        if (loading_properties.getProperty(UICommon.EQUILIBRIUM_STATE_PROPERTIES).equals("0"))
            equilibrium_state = false;
        
        if (loading_properties.getProperty(UICommon.SHOW_GRAIN_BOUNDS_PROPERTIES).equals("1"))
            show_grain_bounds = true;
        if (loading_properties.getProperty(UICommon.SHOW_GRAIN_BOUNDS_PROPERTIES).equals("0"))
            show_grain_bounds = false;
        
        if (loading_properties.getProperty(UICommon.CALC_HEAT_EXPANSION_PROPERTIES).equals("0"))
            calc_heat_expansion = false;
        if (loading_properties.getProperty(UICommon.CALC_HEAT_EXPANSION_PROPERTIES).equals("1"))
            calc_heat_expansion = true;
        
        if (loading_properties.getProperty(UICommon.CALC_FORCE_MOMENTS_PROPERTIES).equals("0"))
            calc_force_moments = false;
        if (loading_properties.getProperty(UICommon.CALC_FORCE_MOMENTS_PROPERTIES).equals("1"))
            calc_force_moments = true;
        
        if (loading_properties.getProperty(UICommon.DEFECTS_INFLUENCE_PROPERTIES).equals("0"))
            defects_influence = false;
        if (loading_properties.getProperty(UICommon.DEFECTS_INFLUENCE_PROPERTIES).equals("1"))
            defects_influence = true;
        
     // if(anis_presence == 0) anisotropy = false;
     // else                   anisotropy = true;
    }
}
