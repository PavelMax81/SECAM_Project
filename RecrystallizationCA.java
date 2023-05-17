package corecalc;

/*
 * @(#) RecrystallizationCA.java version 1.3;       Apr 2008
 *
 * Copyright (c) 2002-2008 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 * 
 * Class for start of solution of several tasks on simulation of processes in solid
 *
 *=============================================================
 *  Last changes :
 *        10 October 2008 by Dmitry D. Moiseenko & Pavel V. Maksimov 
 *         (small optimization and a few comments).
 *            File version 1.3.1
 */
import cellcore.*;
import java.util.*;
import java.io.*;
import util.*;
import javafx.scene.control.TextArea;
import recGeometry.VectorR3;

/** Class for start of solution of several tasks on simulation of processes in solid
 * @author Pavel V. Maksimov & Dmitry D. Moiseenko
 * @version 1.2 - Jan 2008
 */
public class RecrystallizationCA
{

    /** File with information about parameters of executed task */
    private static String task_file;

    Properties task_properties;

    /** Value of time step */
    double time_step;

    /** Total time of numerical experiment */
    double total_time;

    /** Number of output files */
    int output_file_number;

    /** Variables responsible for simulation of corresponding processes */
    byte simulate_heat_flows,
         simulate_mech_flows,
         simulate_recryst,
         simulate_cracks;

    /** This variable is responsible for state of specimen (equilibrium or nonequilibrium):
     * if state=0 then recrystallization proceeds under initial temperature,
     * if state=1 then recrystallization does not proceed under initial temperature.
     */
    byte equilibrium_state;

    /** This variable is responsible for simulation of heat expansion:
     * if it equals 0 then heat expansion is not simulated,
     * if it equals 1 then heat expansion is simulated.
     */
    byte simulate_heat_expansion;
    
    /** This variable is responsible for account of defects when calculating mobility
     */
    byte defects_influence;

    /** This variable is responsible for calculation of force moments at each time step:
     * if it equals 0 then force moments are not calculated,
     * if it equals 1 then force moments are calculated.
     */
    byte calc_force_moments;

    /** Effective rate of response on external action response_rate
     */
    double response_rate;
    
    // Variable responsible for account of anisotropy of simulated medium
    boolean anisotropy;
        
    // Vector of anisotropy of simulated medium
    VectorR3 anis_vector;
    
    // Coefficient of anisotropy of simulated medium
    double coeff_anisotropy;
    
    /** the coefficient for calculation of cell torsion energy change */
    double torsion_energy_coeff;
    
    /** Simulated part of specimen (finite element) */
    SpecimenR3_HCP specimenR3, localFE_specimenR3;

    /** Initial conditions of task */
    InitialConditions initCond;

    /** Boundary conditions of task */
    BoundaryConditions boundCond;

    /** Name of file with information about specimen geometrical properties */
    String specimen_file;

    /** Name of file with information about initial conditions */
    String init_cond_file;

    /** Name of file with information about boundary conditions */
    String bound_cond_file;

    /** Name of file containing temperatures in vertices of each finite element */
//    String temperatures_file;
    /** Name of file for writing of simulation results */
    String write_file_name;

    /** List of tasks for operations with finite elements */
    ArrayList tasks;

    /** List of values of heat conductivity for each finite element */
    ArrayList heatConductivities_FE;

    /** List of values of heat capacity for each finite element */
    ArrayList heatCapacities_FE;

    /** Sizes of finite element */
    double elementSizeI;
    double elementSizeJ;
    double elementSizeK;

    /** Total number of finite elements */
    int elementNumber;

    /** Number of finite elements along reference axes */
    int elementNumberI;
    int elementNumberJ;
    int elementNumberK;

    /** Minimal number of neighbour cells in adjacent grain necessary
     * for transition of "central" cell to adjacent grain */
    int min_neighbours_number;

    /** Type of interaction of boundary cells with neighbours */
    byte bound_interaction_type;

    BufferedReader br;

    /** Field intended for writing of all completed steps in progress frame
     */
    TextArea completed_steps;

    /** The constructor starts solution of several threads 
     * for simulation of processes in finite elements of solid.
     * @param task_name name of task
     */
    public RecrystallizationCA(String task_name)
    {
        // File with information about parameters of executed task
        task_file = Common.TASK_PATH + "/" + task_name + "." + Common.TASK_EXTENSION;
        
        // Name of file for writing of simulation results
        write_file_name = Common.TASK_PATH + "/" + task_name + "/" + task_name;

        System.out.println("TASK_FILE: " + task_file);

        /* Block 3:
         * a. reading of information about parameters of executed task from the file "*.seca";
         * b. creation of simulated specimen;
         * c. division of the specimen into system og finite elements (now the system consists of single FE):
         */
        // Reading of values of fields of the class from the file
        readTask(task_file);

        /** Creation of specimen3D */
        specimenR3 = new SpecimenR3_HCP(specimen_file, init_cond_file, bound_cond_file,
                                        time_step, total_time, output_file_number,
                                        simulate_heat_expansion, calc_force_moments, defects_influence, 
                                        response_rate, bound_interaction_type, min_neighbours_number,
                                        anisotropy, anis_vector, coeff_anisotropy, torsion_energy_coeff);

        System.out.println("SpecimenR3 size = " + specimenR3.size());
        //       DefCellR3 defCell;
        
        /* Numbers of CA along each axe in finite element 
        ("micro specimen" for Cellular automata)
        CA specimen -- single FE in Hybrid approach
         */
        elementSizeI = specimenR3.get_element_size_X();
        elementSizeJ = specimenR3.get_element_size_Y();
        elementSizeK = specimenR3.get_element_size_Z();

        //   Three elementSizes = new Three(elementSizeI, elementSizeJ, elementSizeK);

        /* Numbers of finite elements along reference axes */
        elementNumberI = (new Double(specimenR3.get_specimen_size_X() / elementSizeI)).intValue();
        elementNumberJ = (new Double(specimenR3.get_specimen_size_Y() / elementSizeJ)).intValue();
        elementNumberK = (new Double(specimenR3.get_specimen_size_Z() / elementSizeK)).intValue();

        /* Total number of finite elements in "macro specimen"*/
        elementNumber = elementNumberI * elementNumberJ * elementNumberK;

        //   TEST
        System.out.print("elementNumberI = " + elementNumberI);
        System.out.print(", elementNumberJ = " + elementNumberJ);
        System.out.println(", elementNumberK = " + elementNumberK + ".");

        System.out.print("elementSizeI = " + elementSizeI);
        System.out.print(", elementSizeJ = " + elementSizeJ);
        System.out.println(", elementSizeK = " + elementSizeK + ".");
        //   END OF TEST

        /* Triple index of finite element */
        Three elementIndices;

        /* Values of temperature in vertices of finite element */
        double[] temperatures = new double[8];

        /* A new CA task */
        Task task;

        // New lists containing CA tasks, heat capacities and heat conductivities of finite elements
        tasks = new ArrayList();
        heatCapacities_FE = new ArrayList();
        heatConductivities_FE = new ArrayList();
        
        /* Block 9 */
        //        try
        {
            /** A new buffering character-input stream */
//            BufferedReader br = new BufferedReader(new FileReader(temperatures_file));
            // Start of threads executing tasks for all FE.
            // (indexI, indexJ, indexK) corresponds to a single FE
            for (int indexK = 0; indexK < elementNumberK; indexK++) 
            for (int indexJ = 0; indexJ < elementNumberJ; indexJ++) 
            for (int indexI = 0; indexI < elementNumberI; indexI++) 
            {
                // TEST
                System.out.print("elementIndexI = " + indexI);
                System.out.print(", elementIndexJ = " + indexJ);
                System.out.println(", elementIndexK = " + indexK + ".");
                // END OF TEST

                elementIndices = new Three(indexI, indexJ, indexK);
                
                /* Creation of new local FE specimen for local task*/
                localFE_specimenR3 = new SpecimenR3_HCP(finElement(specimenR3, elementIndices,
                                                        elementSizeI, elementSizeJ, elementSizeK));

                /*  Setting of initial conditions of CA task */
                //       initCond = new InitialConditions(init_cond_file);

                /* Setting of boundary conditions of CA task */
                boundCond = new BoundaryConditions(bound_cond_file);

                /* Setting of initial and boundary conditions to CA "micro specimen" */
                // localFE_specimenR3.setInitialConditions(initCond);
           //     localFE_specimenR3.setBoundaryConditions(boundCond, false, 0);
                
                // Creation of new CA task, addition of the task to list of tasks
                task = new Task(localFE_specimenR3);//, temperatures);
                tasks.add(task);
            }
//            br.close();    
        }
        /*
        catch(IOException io_exc)
        {
        System.out.println(+"Error: " + io_exc);
        }
         */
        /*
        DefCellR3 cell;
        Three cellIndices;
        Three indicesFE;
        SpecimenR3_HCP taskElement;
        int cellNumberFE;
        byte cellState;        
         */
        //  BufferedWriter bw;

        /* Block 10 */

        //    try
        {
            //   bw = new BufferedWriter(new FileWriter(write_file_name+".txt"));

            // Start of tasks; creation of lists of values of heat capacity 
            // and heat conductivity of each finite element
            for (int taskCounter = 0; taskCounter < tasks.size(); taskCounter++)
            {
                task = (Task) tasks.get(taskCounter);
                task.run(write_file_name, simulate_heat_flows, simulate_mech_flows,
                         simulate_recryst, simulate_cracks, equilibrium_state);// +"_FE"+taskCounter);
          /*  
                // Finite element simulated by the task
                taskElement = new SpecimenR3_HCP(task.getSpecimenR3());
                
                // Number of cells in this finite element
                cellNumberFE = taskElement.size();

                // Triple index of this finite element
                indicesFE = new Three (taskElement.calcTripleIndex(taskCounter, 
                elementNumberI, elementNumberJ, elementNumberK));
                bw.write(" FE indexI = " + indicesFE.getI());
                bw.write(" FE indexJ = " + indicesFE.getJ());
                bw.write(" FE indexK = " + indicesFE.getK());

                bw.newLine();

          */

                //           heatCapacities_FE.add(task.getAverageHeatCapacityFE());
                //           heatConductivities_FE.add(task.getAverageHeatConductivityFE());
            }
        }
        /*      catch(IOException io_exc)
        {
        System.out.println(+"Error: " + io_exc);
        }
         */
    }

    /** The constructor starts solution of several threads
     * for simulation of processes in finite elements of solid.
     * @param _completed_steps field intended for writing
     *        of all completed steps in progress frame
     */
    public RecrystallizationCA(TextArea _completed_steps)
    {
        completed_steps = _completed_steps;

        try
        {
            br = new BufferedReader(new FileReader(Common.TASK_PATH + "/" +
                    Common.TASK_NAME_FILE + "." +
                    Common.TASK_EXTENSION));

            // Name of task
            String task_name = br.readLine();

            // File with information about parameters of executed task
            task_file = Common.TASK_PATH + "/" + task_name + "." + Common.TASK_EXTENSION;

            // Name of file for writing of simulation results
            write_file_name = Common.TASK_PATH + "/" + task_name + "/" + task_name;
        }
        catch (IOException io_exc)
        {
            completed_steps.setText(completed_steps.getText() + "\nError:" + io_exc);
        }

        completed_steps.setText(completed_steps.getText() + "\nTASK_FILE: " + task_file);

        // Reading of values of fields of the class from the file
        readTask(task_file);

        /** Creation of specimen3D */
        specimenR3 = new SpecimenR3_HCP(specimen_file, init_cond_file, bound_cond_file,
                                        time_step, total_time, output_file_number,
                                        simulate_heat_expansion, calc_force_moments, defects_influence,
                                        response_rate, bound_interaction_type, min_neighbours_number,
                                        anisotropy, anis_vector, coeff_anisotropy, torsion_energy_coeff);

        completed_steps.setText(completed_steps.getText() + "\nSpecimenR3 size = " + specimenR3.size());
        //       DefCellR3 defCell;

        /* Numbers of CA along each axe in finite element
        ("micro specimen" for Cellular automata)
        CA specimen -- single FE in Hybrid approach
         */
        elementSizeI = specimenR3.get_element_size_X();
        elementSizeJ = specimenR3.get_element_size_Y();
        elementSizeK = specimenR3.get_element_size_Z();

        //   Three elementSizes = new Three(elementSizeI, elementSizeJ, elementSizeK);

        /* Numbers of finite elements along reference axes */
        elementNumberI = (new Double(specimenR3.get_specimen_size_X() / elementSizeI)).intValue();
        elementNumberJ = (new Double(specimenR3.get_specimen_size_Y() / elementSizeJ)).intValue();
        elementNumberK = (new Double(specimenR3.get_specimen_size_Z() / elementSizeK)).intValue();

        /* Total number of finite elements in "macro specimen"*/
        elementNumber = elementNumberI * elementNumberJ * elementNumberK;

        //   TEST
        System.out.print("elementNumberI = " + elementNumberI);
        System.out.print(", elementNumberJ = " + elementNumberJ);
        System.out.print(", elementNumberK = " + elementNumberK + ".\n");

        System.out.print("elementSizeI = " + elementSizeI);
        System.out.print(", elementSizeJ = " + elementSizeJ);
        System.out.print(", elementSizeK = " + elementSizeK + ".\n");
        //   END OF TEST

        /* Triple index of finite element */
        Three elementIndices;

        /* Values of temperature in vertices of finite element */
        double[] temperatures = new double[8];

        /* A new CA task */
        Task task;

        /* New lists containing CA tasks,
        heat capacities and heat conductivities of finite elements*/
        tasks = new ArrayList();
        heatCapacities_FE = new ArrayList();
        heatConductivities_FE = new ArrayList();

//        try
        {
            /** A new buffering character-input stream */
//            BufferedReader br = new BufferedReader(new FileReader(temperatures_file));
            // Start of threads executing the tasks for each FE.
            // (indexI, indexJ, indexK) corresponds to a single FE
            for (int indexK = 0; indexK < elementNumberK; indexK++) {
                for (int indexJ = 0; indexJ < elementNumberJ; indexJ++) {
                    for (int indexI = 0; indexI < elementNumberI; indexI++) {
                        // TEST
                        System.out.print("elementIndexI = " + indexI);
                        System.out.print(", elementIndexJ = " + indexJ);
                        completed_steps.setText(completed_steps.getText() + ", elementIndexK = " + indexK + ".");
                        // END OF TEST

                        elementIndices = new Three(indexI, indexJ, indexK);

                        /* Creation of new local FE specimen for local task*/
                        localFE_specimenR3 = new SpecimenR3_HCP(finElement(specimenR3, elementIndices,
                                elementSizeI, elementSizeJ, elementSizeK));

                        /*  Setting of initial conditions of CA task */
                        //       initCond = new InitialConditions(init_cond_file);

                        /* Setting of boundary conditions of CA task */
                        boundCond = new BoundaryConditions(bound_cond_file);

                        /* Setting of initial and boundary conditions
                         * to CA "micro specimen" */
                        //     localFE_specimenR3.setInitialConditions(initCond);
                    //    localFE_specimenR3.setBoundaryConditions(boundCond, false, 0);
                        
                        /** Creation of new CA task, addition of the task to list of tasks, start of the task */
                        task = new Task(localFE_specimenR3);//, temperatures);
                        tasks.add(task);
                    }
                }
            }

//            br.close();
        }
        /*
        catch(IOException io_exc)
        {
        completed_steps.setText(completed_steps.getText()+"Error: " + io_exc);
        }
         */
        /*
        DefCellR3 cell;
        Three cellIndices;
        Three indicesFE;
        SpecimenR3_HCP taskElement;
        int cellNumberFE;
        byte cellState;
         */
        //  BufferedWriter bw;

        //    try
        {
            //   bw = new BufferedWriter(new FileWriter(write_file_name+".txt"));

            // Start of tasks; creation of lists of values of heat capacity
            // and heat conductivity of each finite element
            for (int taskCounter = 0; taskCounter < tasks.size(); taskCounter++) {
                task = (Task) tasks.get(taskCounter);
                task.run(write_file_name, simulate_heat_flows, simulate_mech_flows,
                        simulate_recryst, simulate_cracks, equilibrium_state);// +"_FE"+taskCounter);
          /*
                // Finite element simulated by the task
                taskElement = new SpecimenR3_HCP(task.getSpecimenR3());

                // Number of cells in this finite element
                cellNumberFE = taskElement.size();

                // Triple index of this finite element
                indicesFE = new Three (taskElement.calcTripleIndex(taskCounter,
                elementNumberI, elementNumberJ, elementNumberK));
                bw.write(" FE indexI = " + indicesFE.getI());
                bw.write(" FE indexJ = " + indicesFE.getJ());
                bw.write(" FE indexK = " + indicesFE.getK());

                bw.newLine();

                 */

                //           heatCapacities_FE.add(task.getAverageHeatCapacityFE());
                //           heatConductivities_FE.add(task.getAverageHeatConductivityFE());
            }
        }
        /*      catch(IOException io_exc)
        {
        completed_steps.setText(completed_steps.getText()+"Error: " + io_exc);
        }
         */
    }

    /** The method starts the solution of certain number of tasks 
     * on simulation of processes in solid
     * @param args an array of command-line arguments.
     */
    public static void main(java.lang.String[] args)
    {
    /*
        JTextArea completed_steps = new JTextArea();

        // Creation of grain structure
        System.out.println("CREATION OF GRAIN STRUCTURE IS STARTED!!!");
        System.out.println();

        GrainsHCPMain grMain = new GrainsHCPMain(completed_steps);

        System.out.println();
        System.out.println("CREATION OF GRAIN STRUCTURE IS FINISHED!!!");
        System.out.println();

        // Creation of files necessary for simulation of recrystallization
        System.out.println("CREATION OF FILES IS STARTED!!!");
        System.out.println();

        FileCreationHCP fileCreation = new FileCreationHCP(completed_steps);

        System.out.println();
        System.out.println("CREATION OF FILES IS FINISHED!!!");
        System.out.println();
  */
        // Simulation of recrystallization
        System.out.println("SIMULATION IS STARTED!!!");
        System.out.println();

        Date start_date = new Date();
        RecrystallizationCA recCA = new RecrystallizationCA(args[0]);
        Date finish_date = new Date();

        long time_period = finish_date.getTime() - start_date.getTime();
        System.out.println();
        System.out.println("The period of execution of the program: " + time_period + " ms.");

        System.out.println();
        System.out.println("SIMULATION IS FINISHED!!!");
        System.out.println();
    }

    /** The method reads values of fields of the class from the file.
     * @param file_name name of file containing values of fields of the class
     */
    private void readTask(String file_name)
    {
        task_properties = new Properties();

        try 
        {
            FileInputStream fin = new FileInputStream(file_name);
            task_properties.load(fin);
            fin.close();
            
            // Reading of file names
            specimen_file           = task_properties.getProperty("specimen_file");
            init_cond_file          = task_properties.getProperty("init_cond_file");
            bound_cond_file         = task_properties.getProperty("bound_cond_file");

            // Reading of task parameters
            time_step               = (new Double(task_properties.getProperty("time_step"))).doubleValue();
            total_time              = (new Double(task_properties.getProperty("total_time"))).doubleValue();
            output_file_number      = (new Integer(task_properties.getProperty("output_file_number"))).intValue();

            // Reading of variables responsible for simulation of corresponding processes
            simulate_heat_flows     = (new Byte(task_properties.getProperty("simulate_heat_flows"))).byteValue();
            simulate_mech_flows     = (new Byte(task_properties.getProperty("simulate_mech_flows"))).byteValue();
            simulate_recryst        = (new Byte(task_properties.getProperty("simulate_recryst"))).byteValue();
            simulate_cracks         = (new Byte(task_properties.getProperty("simulate_cracks"))).byteValue();

            // Reading of variables responsible for equilibrium state of specimen
            equilibrium_state       = (new Byte(task_properties.getProperty("equilibrium_state"))).byteValue();

            // Reading of variables responsible for simulation of heat expansion, account of defects
            // and calculation of force moments
            simulate_heat_expansion = (new Byte(task_properties.getProperty("simulate_heat_expansion"))).byteValue();
            
            defects_influence       = (new Byte(task_properties.getProperty("defects_influence"))).byteValue();
            calc_force_moments      = (new Byte(task_properties.getProperty("calc_force_moments"))).byteValue();

            // Effective rate of response on external action
            response_rate           = (new Double(task_properties.getProperty("response_rate"))).doubleValue();

            // Variable responsible for account of anisotropy of simulated medium            
            byte anis_presence      = (new Byte(task_properties.getProperty("anisotropy"))).byteValue();
        
            if(anis_presence==0) anisotropy = false;
            else                 anisotropy = true;
        
            // Vector of anisotropy of simulated medium
            double anis_vector_X    = (new Double(task_properties.getProperty("INNER_ANISOTROPY_COORD_X"))).doubleValue();
            double anis_vector_Y    = (new Double(task_properties.getProperty("INNER_ANISOTROPY_COORD_Y"))).doubleValue();
            double anis_vector_Z    = (new Double(task_properties.getProperty("INNER_ANISOTROPY_COORD_Z"))).doubleValue();
            
            anis_vector             = new VectorR3(anis_vector_X, anis_vector_Y, anis_vector_Z);
            
            // Coefficient of anisotropy of simulated medium
            coeff_anisotropy        = (new Double(task_properties.getProperty("COEFF_INNER_ANISOTROPY"))).doubleValue();
            
            // Type of interaction of boundary cells with neighbours
            bound_interaction_type  = (new Byte(task_properties.getProperty("bound_interaction_type"))).byteValue();
            
            // Minimal number of neighbour cells in adjacent grain necessary for cell transition to this adjacent grain
            min_neighbours_number   = (new Integer(task_properties.getProperty("min_neighbours_number"))).intValue();
            
            // the coefficient for calculation of cell torsion energy change
            torsion_energy_coeff    = (new Double(task_properties.getProperty("torsion_energy_coeff"))).doubleValue();
        } 
        catch (IOException io_ex) 
        {
            System.out.println("ERROR: " + io_ex);
        }
    }

    /** The method creates part of whole specimen (finite element) as a single specimen.
     * @param _specimenR3 whole specimen
     * @param indicesFE triple index of finite element
     * @param sizeFE_X size of finite element along X-axis.
     * @param sizeFE_Y size of finite element along Y-axis.
     * @param sizeFE_Z size of finite element along Z-axis.
     */
    public SpecimenR3_HCP finElement(SpecimenR3_HCP _specimenR3, Three indicesFE,
            double sizeFE_X, double sizeFE_Y, double sizeFE_Z)
    {
        Three cell_indices;
        int index;

        SpecimenR3_HCP finElement = new SpecimenR3_HCP(_specimenR3, sizeFE_X, sizeFE_Y, sizeFE_Z);
        
        finElement.setStepNumber(_specimenR3.getStepNumber());
        finElement.setTimeStep(_specimenR3.getTimeStep());
        finElement.setGrainsCluster(_specimenR3.getGrainsCluster());
        finElement.setOutputFileNumber(_specimenR3.getOutputFileNumber());

        // Indices of created finite element
        int elementIndexK = indicesFE.getK();
        int elementIndexJ = indicesFE.getJ();
        int elementIndexI = indicesFE.getI();

        // Setting of sizes of created finite element evaluated in corresponding automaton sizes
        int cellNumberI = (int) Math.round(sizeFE_X / _specimenR3.get_cell_size_X());
        int cellNumberJ = (int) Math.round(sizeFE_Y / _specimenR3.get_cell_size_Y());
        int cellNumberK = (int) Math.round(sizeFE_Z / _specimenR3.get_cell_size_Z());

        //TEST
        System.out.print  ("FE cellNumberK = " + cellNumberK);
        System.out.print  (", FE cellNumberJ = " + cellNumberJ);
        System.out.println(", FE cellNumberI = " + cellNumberI + ".");
        //END OF TEST  

        // Spatial cycle on automata located in created finite element.
        for (int indexK = cellNumberK * elementIndexK; indexK < cellNumberK * (elementIndexK + 1); indexK++)
        for (int indexJ = cellNumberJ * elementIndexJ; indexJ < cellNumberJ * (elementIndexJ + 1); indexJ++)
        for (int indexI = cellNumberI * elementIndexI; indexI < cellNumberI * (elementIndexI + 1); indexI++)
        {
            // Calculation of triple index of the cell with given indices
            cell_indices = new Three(indexI, indexJ, indexK);
            
            // Call of the cell with given indices from list of all cells of specimen
            index = _specimenR3.calcSingleIndex(cell_indices);

            // Addition of the cell to finite element
            finElement.add(_specimenR3.get(index));
        }

        finElement.setCellNumberK(cellNumberK);
        finElement.setCellNumberJ(cellNumberJ);
        finElement.setCellNumberI(cellNumberI);

        return finElement;
    }

    // THE FOLLOWING METHODS ARE NOT USED IN THIS CLASS.
    /** The method returns simulated part of specimen (finite element).
     * @return simulated part of specimen (finite element)
     */
    public SpecimenR3_HCP getSpecimenR3()
    {
        return specimenR3;
    }

    /** The method returns initial conditions of task.
     * @return initial conditions of task 
     */
    public InitialConditions getInitCond()
    {
        return initCond;
    }

    /** The method returns boundary conditions of task.
     * @return boundary conditions of task
     */
    public BoundaryConditions getBoundCond()
    {
        return boundCond;
    }

    /** The method returns value of time step.
     * @return value of time step 
     */
    public double get_time_step()
    {
        return time_step;
    }

    /** The method returns total time of numerical experiment.
     * @return total time of numerical experiment
     */
    public double get_total_time()
    {
        return total_time;
    }

    /** The method returns the number of output files.
     * @return the number of output files
     */
    public int get_output_file_number()
    {
        return output_file_number;
    }
    
    /** The method sets simulated part of specimen (finite element).
     * @param specimenFile file with parameters of simulated part of specimen (finite element)
     * @param initCondFile file with information on initial conditions
     * @param boundCondFile file with information on boundary conditions
     */
    public void setSpecimenR3(String specimenFile, String initCondFile, String boundCondFile)
    {
        specimenR3 = new SpecimenR3_HCP(specimenFile, initCondFile, boundCondFile,
                                        time_step, total_time, output_file_number,
                                        simulate_heat_expansion, calc_force_moments, defects_influence,
                                        response_rate, bound_interaction_type, min_neighbours_number,
                                        anisotropy, anis_vector, coeff_anisotropy, torsion_energy_coeff);
    }
    
    /** The method sets initial conditions of task.
     * @param _initCondFile file with initial conditions of task
     */
    public void setInitCond(String _initCondFile)
    {
        initCond = new InitialConditions(_initCondFile);
    }

    /** The method sets boundary conditions of task.
     * @param _boundCondFile file with boundary conditions of task
     */
    public void setBoundCond(String _boundCondFile)
    {
        boundCond = new BoundaryConditions(_boundCondFile);
    }

    /** The method creates file with initial conditions of task.
     */
    public void createInitCondFile()
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(init_cond_file));

            int size = specimenR3.size();
            Integer _size = new Integer(size);

            bw.write(_size.toString());
            bw.newLine();

            for (int cell_counter = 0; cell_counter < size; cell_counter++) {
                bw.write("30.0 0.0 300.0 2.0 1.0 2.0 2.0 1.0 2.0 401.0 390.0 8940.0 0.00002 0.01");
                bw.newLine();
            }

            bw.flush();
            bw.close();
        } catch (IOException io_ex) {
            System.out.println("ERROR: " + io_ex);
        }
    }

    /** The method creates file with boundary conditions of task.
     */
    public void createBoundCondFile()
    {
        // Index of boundary cell
        int bound_cell_index = -1;

        // Number of cells of specimen3D in direction of X-axis
        int cell_number_I = specimenR3.getCellNumberI();

        // Number of cells of specimen3D in direction of Y-axis
        int cell_number_J = specimenR3.getCellNumberJ();

        Integer bound_cell_number = 2 * cell_number_J;

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(bound_cond_file));
            //     BufferedWriter bw = new BufferedWriter(new FileWriter("./task/bound_cond_def_1.txt"));

            bw.write(bound_cell_number.toString());
            bw.newLine();

            for (int string_counter = 0; string_counter < cell_number_J; string_counter++) 
            {
                bound_cell_index = string_counter * cell_number_I;
                bw.write(bound_cell_index + " 0.0 0.0 2000.0");
                bw.newLine();

                bound_cell_index = (string_counter + 1) * cell_number_I - 1;
                bw.write(bound_cell_index + " 0.0 0.0 300.0");
                bw.newLine();
            }

            bw.flush();
            bw.close();
        } catch (IOException io_ex) {
            System.out.println("ERROR: " + io_ex);
        }
    }
}