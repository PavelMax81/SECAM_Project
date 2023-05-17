package cellcore;
import java.util.*;
import java.io.*;
import grainsCore.*;
import util.*;

/*
 * @(#) InitialConditions.java version 1.0;       Feb 2008
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for setting of initial conditions of task.
 *
 *=============================================================
 *  Last changes :
 *         15 Oct 2008 by Pavel V. Maksimov (add of fields and methods "get-" and "set-" for 
 *               index of grain containing each cellular automaton).
 *            File version 1.0
 */

/** Class for setting of initial conditions of task.
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.0 - Feb 2008
 */

public class InitialConditions 
{    
    /** List of initial values of thermal energy of each cellular automaton
     */
    ArrayList initialThermalEnergies;
    
    /** List of initial values of mechanical energy of each cellular automaton
     */
    ArrayList initialMechEnergies;
    
    /** List of initial values of temperature of each cellular automaton
     */
    ArrayList initialTemperatures;
    
    /** List of initial values of three Euler angles of each cellular automaton
     */
    ArrayList initialEulerAngles;
    
    /** List of initial values of HAGB energy of each cellular automaton
     */
    ArrayList energiesHAGB;
    
    /** List of initial values of maximal mobility of each cellular automaton
     */
    ArrayList maxMobilities;
    
    /** List of initial values of limit of HAGB angles of each cellular automaton
     */
    ArrayList angleLimitsHAGB;
    
    /** List of initial values of heat conductivity of each cellular automaton
     */
    ArrayList heatConductivities;
    
    /** List of initial values of heat capacity of each cellular automaton
     */
    ArrayList heatCapacities;
    
    /** List of initial values of density of each cellular automaton
     */
    ArrayList densities;
    
    /** List of initial values of heat expansion coeffitient of each cellular automaton
     */
    ArrayList heatExpansionCoeffs;
    
    /** List of initial values of yield strain of each cellular automaton
     */
    ArrayList yieldStrains;
    
    /** List of indices of grains containing each cellular automaton
     */
    ArrayList grainIndices;

    /** List of types of cells depending on their location in grain and in specimen
     */
    ArrayList cellTypes;
    
    /** List of initial values of torsion energy of each cellular automaton
     */
    ArrayList initialTorsEnergies;
    
    /** The constructor reads values of physical parameters of each cellular automaton from file.
     * @param file_init_cond name of file containing information 
     *        about values of physical parameters of each cellular automaton
     */
    public InitialConditions(String file_init_cond)
    {
        readInitCond(file_init_cond);
    }
    
    /** The method reads values of physical parameters of each cellular automaton from file.
     * @param file_name name of file containing information about values 
     *        of physical parameters of each cellular automaton
     */
    public void readInitCond(String file_name)
    {                             
        initialThermalEnergies = new ArrayList();
        initialMechEnergies    = new ArrayList();
        initialTemperatures    = new ArrayList();  
     /*
        heatConductivities     = new ArrayList();    
        heatCapacities         = new ArrayList();    
        densities              = new ArrayList();
        heatExpansionCoeffs    = new ArrayList();
        yieldStrains           = new ArrayList();
     */
        grainIndices           = new ArrayList();
        cellTypes              = new ArrayList();
        initialTorsEnergies    = new ArrayList();
        
        String string = "";
        StringTokenizer st;
        String first_token;
        
        int cell_number = 0;
        int empty_strings_num = 0;
/*
        boolean show_grain_bounds;

        if(LoadProperties.SHOW_GRAIN_BOUNDS==1)
            show_grain_bounds=true;
        else
            show_grain_bounds=false;
  */      
        try
        {  
            BufferedReader br = new BufferedReader(new FileReader(file_name));
                                     
            System.out.println("Name of read init_cond_file:    "+file_name);
            
            while(br.ready())
            {
                string = br.readLine();
                st = new StringTokenizer(string);

             //   System.out.println(string);
                
                if(st.hasMoreTokens())
                {
                    first_token = st.nextToken();
                    
                    if(!first_token.equals("#"))
                    {
                        cell_number++;

                        // Reading of physical parameters of current cell
                        initialThermalEnergies.add(new Double(first_token).doubleValue());
                        initialMechEnergies.add(new Double(st.nextToken()).doubleValue());
                        initialTemperatures.add(new Double(st.nextToken()).doubleValue());              
                        grainIndices.add(new Integer(st.nextToken()).intValue());

                  //  if(st.hasMoreTokens())
                        cellTypes.add(new Byte(st.nextToken()).byteValue());
                   // else
               //       cellTypes.add((byte)Common.OUTER_CELL);
                        
                        if(st.hasMoreTokens())
                            initialTorsEnergies.add(new Double(st.nextToken()).doubleValue());
                        else
                            initialTorsEnergies.add(0.0);
                    }
                }
                else
                {
                    empty_strings_num++;
                    System.out.println("String "+(cell_number+empty_strings_num)+" in file "+file_name+" has no tokens!!!");
                }

            }

            System.out.println("Total number of cells: "+initialTemperatures.size()+" = "+cell_number);
            
           // System.out.println("Test cell number: "+cell_number);
            
            br.close();
        }
        catch(IOException io_exc)
        {
            System.out.println("Error: " + io_exc);
        }
    }
    
    /** The method returns list of initial values of thermal energy of each cellular automaton.
     * @return list of initial values of thermal energy of each cellular automaton
     */
    public ArrayList getInitialThermalEnergies()
    {
        return initialThermalEnergies;
    }
    
    /** The method returns list of initial values of mechanical energy of each cellular automaton.
     * @return list of initial values of mechanical energy of each cellular automaton
     */
    public ArrayList getInitialMechEnergies()
    {
        return initialMechEnergies;
    }
    
    /** The method returns list of initial values of torsion energy of each cell.
     * @return list of initial values of torsion energy of each cell
     */
    public ArrayList getInitialTorsEnergies()
    {
        return initialTorsEnergies;
    }
    
    /** The method returns list of initial values of temperature of each cellular automaton.
     * @return list of initial values of temperature of each cellular automaton
     */
    public ArrayList getInitialTemperatures()
    {        
        return initialTemperatures;
    }
    
    /** The method returns list of initial values of three Euler angles of each cellular automaton.
     * @return list of initial values of three Euler angles of each cellular automaton
     */
    public ArrayList getInitialEulerAngles()
    {
        return initialEulerAngles;
    }
    
    /** The method returns list of initial values of HAGB energy of each cellular automaton
     * @return list of initial values of HAGB energy of each cellular automaton
     */
    public ArrayList getEnergiesHAGB()
    {
        return energiesHAGB;
    }
    
    /** The method returns list of initial values of maximal mobility of each cellular automaton
     * @return list of initial values of maximal mobility of each cellular automaton
     */
    public ArrayList getMaxMobilities()
    {
        return maxMobilities;
    }
    
    /** The method returns list of initial values of limit of HAGB angles of each cellular automaton
     * @return list of initial values of limit of HAGB angles of each cellular automaton
     */
    public ArrayList getAngleLimitsHAGB()
    {
        return angleLimitsHAGB;
    }
    
    /** The method returns list of initial values of heat conductivity of each cellular automaton
     * @return list of initial values of heat conductivity of each cellular automaton
     */
    public ArrayList getHeatConductivities()
    {
        return heatConductivities;
    }
    
    /** The method returns list of initial values of heat capacity of each cellular automaton
     * @return list of initial values of heat capacity of each cellular automaton
     */
    public ArrayList getHeatCapacities()
    {
        return heatCapacities;
    }
    
    /** The method returns list of initial values of density of each cellular automaton
     * @return list of initial values of density of each cellular automaton
     */
    public ArrayList getDensities()
    {
        return densities;
    }
    
    /** The method returns list of initial values of heat expansion coeffitient of each cellular automaton
     * @return list of initial values of heat expansion coeffitient of each cellular automaton
     */
    public ArrayList getHeatExpCoeffs()
    {
        return heatExpansionCoeffs;
    }
    
    /** The method returns list of initial values of yield strain of each cellular automaton
     * @return list of initial values of yield strain of each cellular automaton
     */
    public ArrayList getYieldStrains()
    {
        return yieldStrains;
    }
    
    /** The method returns list of indices of grains containing each cellular automaton.
     * @return list of indices of grains containing each cellular automaton
     */
    public ArrayList getGrainIndices()
    {
        return grainIndices;
    }

    /** The method returns list of cell types.
     * @return list of cell types
     */
    public ArrayList getCellTypes()
    {
        return cellTypes;
    }
    
    /** The method sets list of initial values of thermal energy of each cellular automaton.
     * @param _initialThermalEnergies list of initial values of thermal energy of each cellular automaton
     */
    public void setInitialThermalEnergies(ArrayList _initialThermalEnergies)
    {
        initialThermalEnergies = new ArrayList(_initialThermalEnergies);
    }
    
    /** The method sets list of initial values of mechanical energy of each cellular automaton.
     * @param _initialMechEnergies list of initial values of mechanical energy of each cellular automaton
     */
    public void setInitialMechEnergies(ArrayList _initialMechEnergies)
    {
        initialMechEnergies = new ArrayList(_initialMechEnergies);
    }
    
    /** The method sets list of initial values of temperature of each cellular automaton.
     * @param _initialTemperatures list of initial values of temperature of each cellular automaton
     */
    public void setInitialTemperatures(ArrayList _initialTemperatures)
    {        
        initialTemperatures = new ArrayList(_initialTemperatures);
    }
    
    /** The method sets list of initial values of three Euler angles of each cellular automaton.
     *  @param _angles list of initial values of three Euler angles of each cellular automaton
     */
    public void setInitialEulerAngles(ArrayList _angles)
    {
        initialEulerAngles = new ArrayList(_angles);
    }
    
    /** The method sets list of initial values of HAGB energy of each cellular automaton.
     *  @param _energiesHAGB list of initial values of HAGB energy of each cellular automaton
     */
    public void setEnergiesHAGB(ArrayList _energiesHAGB)
    {
        energiesHAGB = new ArrayList(_energiesHAGB);
    }
    
    /** The method sets list of initial values of maximal mobility of each cellular automaton.
     *  @param _maxMobilities list of initial values of maximal mobility of each cellular automaton
     */
    public void setMaxMobilities(ArrayList _maxMobilities)
    {
        maxMobilities = new ArrayList(_maxMobilities);
    }
    
    /** The method sets list of initial values of limit of HAGB angles of each cellular automaton
     *  @param _angleLimits list of initial values of limit of HAGB angles of each cellular automaton
     */
    public void setAngleLimitsHAGB(ArrayList _angleLimits)
    {
        angleLimitsHAGB = new ArrayList(_angleLimits);
    }

    /** The method sets list of initial values of heat conductivity of each cellular automaton
     * @param _heatCond list of initial values of heat conductivity of each cellular automaton
     */
    public void setHeatConductivities(ArrayList _heatCond)
    {
        heatConductivities = new ArrayList(_heatCond);
    }
    
    /** The method sets list of initial values of heat capacity of each cellular automaton
     * @param _heatCap list of initial values of heat capacity of each cellular automaton
     */
    public void setHeatCapacities(ArrayList _heatCap)
    {
        heatCapacities = new ArrayList(_heatCap);
    }
    
    /** The method sets list of initial values of density of each cellular automaton
     * @param _densities initial values of density of each cellular automaton
     */
    public void setDensities(ArrayList _densities)
    {
        densities = new ArrayList(_densities);
    }
    
    /** The method sets list of initial values of heat expansion coeffitient of each cellular automaton
     * @param _heatExpCoeffs list of initial values of heat expansion coeffitient of each cellular automaton
     */
    public void setHeatExpCoeffs(ArrayList _heatExpCoeffs)
    {
        heatExpansionCoeffs = new ArrayList(_heatExpCoeffs);
    }
    
    /** The method sets list of initial values of yield strain of each cellular automaton
     * @param _yieldStrains list of initial values of yield strain of each cellular automaton
     */
    public void setYieldStrains(ArrayList _yieldStrains)
    {
        yieldStrains = new ArrayList(_yieldStrains);
    }

    /** The method sets list of indices of grains containing each cellular automaton.
     * @param _grainIndices list of indices of grains containing each cellular automaton.
     */
    public void setGrainIndices(ArrayList _grainIndices)
    {
        grainIndices = new ArrayList(_grainIndices);
    }

    /** The method sets list of cell types
     * @param _grainIndices list of cell types
     */
    public void setCellTypes(ArrayList _cellTypes)
    {
        cellTypes = new ArrayList(_cellTypes);
    }
}