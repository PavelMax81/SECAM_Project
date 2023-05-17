/*
 * @(#) UIInterface.java version 1.0.0;       June 13 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for creation bank of interface data, wich contain
 * pathes of choosed files (path of choosed specimen etc).
 *
 *=============================================================
 *  Last changes :
 *          1 April 2011 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.7
 *          Edit: Add name of choosed or created tank
 */

/** Class for creation bank of interface data, wich contain
 *  pathes of choosed files (path of choosed specimen etc).
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - June 2009
 */

package interf;

public class UIInterface
{

    protected String specimen_path;

    protected String init_cond_path;

    protected String bound_cond_path;

    protected String tank_path;

    protected String task_path;

    protected String created_internal_frame;

    protected String damaged_specimen_or_not;

    protected String special_specimen_path;

    public UIInterface()
    {
        System.out.println("UIInterface: constructor creation start");
        
        tank_path = UICommon.DEFAULT_BOUND_COND_FILE_NAME;
    }

    public UIInterface(UIInterface ui_interface)
    {
        System.out.println("UIInterface: constructor(UIInterface ui_interface) creation start");

        specimen_path = ui_interface.getSpecimenPath();

        task_path = ui_interface.getTaskPath();

        init_cond_path = ui_interface.getInitCondPath();

        bound_cond_path = ui_interface.getBoundCondPath();

        tank_path = ui_interface.getTankPath();

        created_internal_frame = ui_interface.getCreatedInternalFrame();

        damaged_specimen_or_not = ui_interface.getDamagedSpecimenOrNot();

        special_specimen_path = ui_interface.getSpecialSpecimenPath();
    }

    /*
     * Set path of choosed specimen
     * @param path - path of specimen
     */

    public void setSpecimenPath(String path)
    {
        specimen_path = path;
    }

    /*
     * Set path of special specimen
     * @param path - path of special specimen
     */

    public void setSpecialSpecimenPath(String path)
    {
        special_specimen_path = path;
    }

    public void setDamagedSpecimenOrNot(String value)
    {
        damaged_specimen_or_not = value;
    }

    /*
     * Set path of choosed init cond
     * @param path - path of init cond
     */

    public void setInitCondPath(String path)
    {
        init_cond_path = path;
    }

    /*
     * Set path of choosed bound cond
     * @param path - path of bound cond
     */

    public void setBoundCondPath(String path)
    {
        bound_cond_path = path;
    }

    /*
     * Set path of choosed tank
     * @param path - path of tank
     */

    public void setTankPath(String path)
    {
        tank_path = path;
    }

    /*
     * Set path of choosed task
     * @param path - path of task
     */

    public void setTaskPath(String path)
    {
        task_path = path;
    }

    /*
     * Set name of created internal frame
     * @param value - name of created internal frame
     */

    public void setCreatedInternalFrame(String value)
    {
        created_internal_frame = value;
    }

    /*
     * Get path of choosed specimen
     * @return - path of choosed specimen
     */

    public String getSpecimenPath()
    {
        return specimen_path;
    }

    /*
     * Get path of special specimen
     * @return - path of special specimen
     */

    public String getSpecialSpecimenPath()
    {
        return special_specimen_path;
    }

    /*
     * Get path of choosed init cond
     * @return - path of choosed init cond
     */

    public String getInitCondPath()
    {
        return init_cond_path;
    }

    /*
     * Get path of choosed bound cond
     * @return - path of choosed bound cond
     */

    public String getBoundCondPath()
    {
        return bound_cond_path;
    }

    /*
     * Get path of choosed tank
     * @return - path of choosed tank
     */

    public String getTankPath()
    {
        return tank_path;
    }

    public String getDamagedSpecimenOrNot()
    {
        return damaged_specimen_or_not;
    }

    /*
     * Get path of choosed task
     * @return - path of choosed task
     */

    public String getTaskPath()
    {
        return task_path;
    }

    /*
     * Get name of created internal frame
     * @return - name of created internal frame
     */

    public String getCreatedInternalFrame()
    {
        return created_internal_frame;
    }
}
