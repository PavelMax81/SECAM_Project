package cellcore;
import java.util.*;
import java.io.*;

/*
 * @(#) BoundaryConditions.java version 1.0;       Feb 2008
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for setting of boundary conditions of task.
 *
 *=============================================================
 *  Last changes :
 *         19 Feb 2008 by Pavel V. Maksimov (creation of class).
 *            File version 1.0
 */

/** Class for setting of boundary conditions of task.
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0 - Feb 2008
 */

public class BoundaryConditions 
{
    /** List of single indices of external boundary automata.
     */
    ArrayList boundaryIndices;
    
    /** List of minimal values of thermal energy of external boundary cells */
    ArrayList boundaryMinThermalEnergies;
    
    /** List of maximal values of thermal energy of external boundary cells */
    ArrayList boundaryThermalEnergies;
    
    /** List of minimal values of mechanical energy of external boundary cells */
    ArrayList boundaryMinMechEnergies;
    
    /** List of maximal values of mechanical energy of external boundary cells */
    ArrayList boundaryMechEnergies;
    
    /** Types of functional dependence of boundary cell parameter on time */
    ArrayList bound_time_function_types;
    
    /** Portions of total time period when boundary cells are loaded */
    ArrayList bound_load_time_portions;
    
    /** Portions of total time period when boundary cells are relaxed */
    ArrayList bound_relax_time_portions;
    
    /** List of boundary values of temperature of each external boundary autumaton
     */
//    ArrayList boundaryTemperatures;
    
    /** The constructor specifies boundary conditions of task: 
     * list of single indices of external boundary automata, 
     * list of boundary values of thermal energy of each external boundary autumaton,
     * list of boundary values of mechanical energy of each external boundary autumaton,
     * list of boundary values of temperature of each external boundary autumaton.
     * @param file_bound_cond name of file containing information about boundary conditions
     */
    public BoundaryConditions(String file_bound_cond)
    {
        readBoundCond(file_bound_cond);
    }
    
    /** The constructor specifies boundary conditions of task: 
     * list of single indices of external boundary automata, 
     * list of boundary values of thermal energy of each external boundary autumaton,
     * list of boundary values of mechanical energy of each external boundary autumaton,
     * list of boundary values of temperature of each external boundary autumaton.
     * @param bound_cond boundary conditions of task
     */
    public BoundaryConditions(BoundaryConditions bound_cond)
    {
        boundaryIndices            = new ArrayList(bound_cond.getBoundaryIndices());
        boundaryMinThermalEnergies = new ArrayList(bound_cond.getBoundaryMinThermalEnergies());
        boundaryThermalEnergies    = new ArrayList(bound_cond.getBoundaryThermalEnergies());
        boundaryMinMechEnergies    = new ArrayList(bound_cond.getBoundaryMinMechEnergies());
        boundaryMechEnergies       = new ArrayList(bound_cond.getBoundaryMechEnergies());
        
        // Types of functional dependence of boundary cell parameter on time
        bound_time_function_types = new ArrayList(bound_cond.getBoundTimeFunctionTypes());
        
        // Portions of total time period when boundary cells are loaded
        bound_load_time_portions  = new ArrayList(bound_cond.getBoundLoadTimePortions());
        
        // Portions of total time period when boundary cells are relaxed
        bound_relax_time_portions = new ArrayList(bound_cond.getBoundRelaxTimePortions());
    
//        boundaryTemperatures    = new ArrayList(bound_cond.getBoundaryTemperatures());
   //     System.out.println(boundaryTemperatures);
    }
    
    /** The method reads values of parameters of external boundary cells from file.
     * @param file_name name of file containing values of parameters of external boundary cells
     */
    private void readBoundCond(String file_name)
    {
        boundaryIndices           = new ArrayList();
        boundaryMinThermalEnergies= new ArrayList();
        boundaryThermalEnergies   = new ArrayList();
        boundaryMinMechEnergies   = new ArrayList();
        boundaryMechEnergies      = new ArrayList();
        bound_time_function_types = new ArrayList();
        bound_load_time_portions  = new ArrayList();
        bound_relax_time_portions = new ArrayList();
        
        String string = "";
        int boundIndex = -1;
        
        double minThermalEnergy;
        double thermalEnergy;
        double minMechEnergy;
        double mechEnergy;
        byte bound_time_function_type;
        double bound_load_time_portion;
        double bound_relax_time_portion;
        
        StringTokenizer st;
        String first_token;
        
        // Counter of boundary cells
        int bound_cell_counter = 0;
                
        try
        {  
            BufferedReader br = new BufferedReader(new FileReader(file_name));
            
            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);
                
                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                    {
                        // Reading of single index of external boundary cell from the file
                        boundIndex = new Integer(first_token).intValue();
                        boundaryIndices.add(boundIndex);
                    
                        // Reading of the values of thermal energy, mechanical energy 
                        // and temperature of external boundary cell from the file
                        minMechEnergy = new Double(st.nextToken()).doubleValue();
                        boundaryMinMechEnergies.add(minMechEnergy);
                        
                        mechEnergy = new Double(st.nextToken()).doubleValue();
                        boundaryMechEnergies.add(mechEnergy);
                        
                        minThermalEnergy = new Double(st.nextToken()).doubleValue();
                        boundaryMinThermalEnergies.add(minThermalEnergy);
                        
                        thermalEnergy = new Double(st.nextToken()).doubleValue();
                        boundaryThermalEnergies.add(thermalEnergy);
                        
                        bound_time_function_type = new Byte(st.nextToken()).byteValue();
                        bound_time_function_types.add(bound_time_function_type);
                        
                        bound_load_time_portion = new Double(st.nextToken()).doubleValue();
                        bound_load_time_portions.add(bound_load_time_portion);
                        
                        bound_relax_time_portion = new Double(st.nextToken()).doubleValue();
                        bound_relax_time_portions.add(bound_relax_time_portion);
                        
                        bound_cell_counter++;
                        
                        //  TEST
                      //  if(mechEnergy > 0.0 | minMechEnergy > 0.0)
                      //    System.out.println("Cell # "+boundIndex+": min_mech_param = "+minMechEnergy+"; mech_param = "+mechEnergy);
                    }
                }
            }
            
            br.close();
            
            //  TEST
            System.out.println("BoundaryConditions.readBoundCond(String file_name): Number of boundary cells: "+bound_cell_counter);
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }        
    }
    
    /** The method returns list of single indices of external boundary automata.
     * @return list of single indices of external boundary automata
     */
    public ArrayList getBoundaryIndices()
    {        
        return boundaryIndices;
    }
    
    /** The method returns list of minimal values of thermal energy of external boundary cells.
     * @return list of minimal values of thermal energy of external boundary cells
     */
    public ArrayList getBoundaryMinThermalEnergies()
    {
        return boundaryMinThermalEnergies;
    }
    
    /** The method returns list of maximal values of thermal energy of external boundary cells.
     * @return list of maximal values of thermal energy of external boundary cells
     */
    public ArrayList getBoundaryThermalEnergies()
    {
        return boundaryThermalEnergies;
    }
    
    /** The method returns list of values of minimal mechanical energy of external boundary cells.
     * @return list of values of minimal mechanical energy of external boundary cells
     */
    public ArrayList getBoundaryMinMechEnergies()
    {
        return boundaryMinMechEnergies;
    }
    
    /** The method returns list of values of maximal mechanical energy of external boundary cells.
     * @return list of values of maximal mechanical energy of external boundary cells
     */
    public ArrayList getBoundaryMechEnergies()
    {
        return boundaryMechEnergies;
    }
    
    /** The method returns list of types of functional dependence of boundary cell parameter on time.
     * @return list of values of types of functional dependence of boundary cell parameter on time
     */
    public ArrayList getBoundTimeFunctionTypes()
    {
        return bound_time_function_types;
    }
    
    /** The method returns list of portions of total time period when boundary cells are loaded.
     * @return list of portions of total time period when boundary cells are loaded
     */
    public ArrayList getBoundLoadTimePortions()
    {
        return bound_load_time_portions;
    }
    
    /** The method returns list of portions of total time period when boundary cells are relaxed.
     * @return list of portions of total time period when boundary cells are relaxed
     */
    public ArrayList getBoundRelaxTimePortions()
    {
        return bound_relax_time_portions;
    }
    
    /** The method sets list of single indices of external boundary automata.
     * @param _boundaryIndices list of single indices of external boundary automata
     */
    public void setBoundaryIndices(ArrayList _boundaryIndices)
    {
        boundaryIndices = new ArrayList(_boundaryIndices);
    }
    
    /** The method sets list of minimal values of thermal energy of external boundary cells.
     * @param _boundaryMinThermalEnergies list of minimal values of thermal energy of external boundary cells
     */
    public void setBoundaryMinThermalEnergies(ArrayList _boundaryMinThermalEnergies)
    {
        boundaryMinThermalEnergies = new ArrayList(_boundaryMinThermalEnergies);
    }
    
    /** The method sets list of maximal values of thermal energy of external boundary cells.
     * @param _boundaryThermalEnergies list of maximal values of thermal energy of external boundary cells
     */
    public void setBoundaryThermalEnergies(ArrayList _boundaryThermalEnergies)
    {
        boundaryThermalEnergies = new ArrayList(_boundaryThermalEnergies);
    }
    
    /** The method sets list of maximal values of mechanical energy of external boundary cells.
     * @param _boundaryMechEnergies list of maximal values of mechanical energy of external boundary cells
     */
    public void setBoundaryMechEnergies(ArrayList _boundaryMechEnergies)
    {
        boundaryMechEnergies = new ArrayList(_boundaryMechEnergies);
    }
    
    /** The method sets list of minimal values of mechanical energy of external boundary cells.
     * @param _boundaryMinMechEnergies list of minimal values of mechanical energy of external boundary cells
     */
    public void setBoundaryMinMechEnergies(ArrayList _boundaryMinMechEnergies)
    {
        boundaryMinMechEnergies = new ArrayList(_boundaryMinMechEnergies);
    }
    
    /** The method sets list of values of temperature of external boundary cells
     * @param _boundaryTemperatures list of values of temperature of external boundary cells
     */
/*
    public void setBoundaryTemperatures(ArrayList _boundaryTemperatures)
    {        
        boundaryTemperatures = new ArrayList(_boundaryTemperatures);
    }
 */
}