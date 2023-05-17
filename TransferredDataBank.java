/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

/**
 *
 * @author dmitryb
 */
public class TransferredDataBank {
    
    RecCAInterface recca_interface;
    
    public void setRecCAInterface(RecCAInterface interf){
        this.recca_interface = interf;
    }
    
    public RecCAInterface getRecCAInterface(){
        return recca_interface;
    }
    
    UIInterface ui_interface;
    
    /** Transferred data of  specimen params
     */
    UISpecimen ui_specimen;

    /** Transferred data of  material params
     */
    UIMaterial ui_material;
    /** Transferred data of  initial conditions params
     */
    UIInitialCondition ui_init_cond;

    /** Transferred data of  boundary conditions params
     */
    UIBoundaryCondition ui_bound_cond;

    /** tank data bank */
    UITank ui_tank;
    
    /** Transferred data of  task params
     */
    UITask ui_task;
    
    /** Variable responsible for stochastic determination of new angles of grains */
    boolean determine_grain_angles;
    
    /** Create default UI and empty creatirn_button
     */
    public TransferredDataBank()
    {
        System.out.println("TransferredDataBank: constructor creation start");

        ui_interface = new UIInterface();
        
        ui_specimen = new UISpecimen();

        ui_material = new UIMaterial();
        
        ui_init_cond = new UIInitialCondition();

        ui_bound_cond = new UIBoundaryCondition();

        ui_tank = new UITank();

        ui_task = new UITask();
        
    }    
    
    /** The method returns the value of the variable 
     * responsible for stochastic determination of new angles of grains.
     * @return the value of the variable responsible for stochastic determination of new angles of grains
     */
    public boolean determine_grain_angles()
    {
        return determine_grain_angles;
    }    
    
    /** The method sets the value of the variable 
     * responsible for stochastic determination of new angles of grains.
     * @param value the value of the variable responsible for stochastic determination of new angles of grains
     */
    public void set_determine_grain_angles(boolean value)
    {
        determine_grain_angles = value;
    }
    
    /** Set ui interface
     * @param receive_ui_interface - data bank of all choosed param paths
     */
    public void setUIInterface(UIInterface receive_ui_interface)
    {
       ui_interface = receive_ui_interface;
    }
    
     /** Set receive_ui_specimen
     * @param receive_ui_specimen - specimen data bank
     */
    public void setUISpecimen(UISpecimen receive_ui_specimen)
    {
       ui_specimen = receive_ui_specimen;
    }

    /** Set receive_ui_material
     * @param receive_ui_material - material data bank
     */
    public void setUIMaterial(UIMaterial receive_ui_material)
    {
       ui_material = receive_ui_material;
    }
    
    /** Set receive_ui_init_cond
     * @param receive_ui_init_cond - init cond data bank
     */
    public void setUIInitialCondition(UIInitialCondition receive_ui_init_cond)
    {
       ui_init_cond = receive_ui_init_cond;
    }

    /** Set receive_ui_bound_cond
     * @param receive_ui_bound_cond - bound cond data bank
     */
    public void setUIBoundaryCondition(UIBoundaryCondition receive_ui_bound_cond)
    {
       ui_bound_cond = receive_ui_bound_cond;
    }

    /** Set receive_ui_tank
     * @param receive_ui_tank - tank data bank
     */
    public void setUITank(UITank receive_ui_tank)
    {
       ui_tank = receive_ui_tank;
    }

    /** Set receive_ui_task
     * @param receive_ui_task - task data bank
     */
    public void setUITask(UITask receive_ui_task)
    {
       ui_task = receive_ui_task;
    }
    
    /** Get data bank of all choosed param paths
     * @return - data bank of all choosed param paths
     */
    public UIInterface getUIInterface()
    {
        return ui_interface;
    }
    
    /** Get specimen data bank
     * @return - specimen data bank
     */
    public UISpecimen getUISpecimen()
    {
       return ui_specimen;
    }

    /**  Get material data bank
     * @return - material data bank
     */
    public UIMaterial getUIMaterial()
    {
       return ui_material;
    }
    
    /** Get init cond data bank
     * @return - init cond data bank
     */
    public UIInitialCondition getUIInitialCondition()
    {
       return ui_init_cond;
    }

    /** Get bound cond data bank
     * @return - bound cond data bank
     */
    public UIBoundaryCondition getUIBoundaryCondition()
    {
       return ui_bound_cond;
    }

    /** Get tank data bank
     * @return - tank data bank
     */
    public UITank getUITank()
    {
       return ui_tank;
    }

    /** Get task data bank
     * @return - task data bank
     */
    public UITask getUITask()
    {
       return ui_task;
    }
    
    boolean isWindows;
    
    public boolean isWindows(){
        return this.isWindows;
    }
    
    public void setIsWindows(boolean value){
        this.isWindows = value;
    }
    
}
