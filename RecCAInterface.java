/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import interf.help.RecCAHelp;
import java.io.IOException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.Common;
import util.OSUtils;

/**
 *
 * @author dmitryb
 */
public class RecCAInterface extends Stage{
    
    TransferredDataBank transfer_data_bank;
    private double scene_width = Screen.getPrimary().getVisualBounds().getWidth();
    private double scene_height = Screen.getPrimary().getVisualBounds().getHeight();
    public BorderPane main_layout = new BorderPane();
    private Scene main_scene;
    
    /**
     * This field is intended for create params of initial conditions
     */

    RecCAInitialConditionsParams init_cond_params;

    /**
     * This field is intended for create params of boundary conditions
     */

    RecCABoundaryConditionsParams bound_cond_params;

    RecCATankParameters tank_cond_params;

    /**
     * This field is intended for create params of task
     */

    RecCATasksParams task_params;
    
    /**
     * This field is intended for creating layout with the list of files (specimens, materials, ...)
     */
    RecCAFileList list_of_some_files;
    RecCAResultsFilesList list_of_results;
    
    /**
     * This field is intended for create progress frame
     */

    StructureGeneratorAndRecrystallizator recrystallization;
    
    RecCASpecimenParams specimen_params;
    RecCAResultsParams results;
    RecCASpecimenPict spec_picture;
    
    FolderRemover list_element_remover;
    
    boolean is_spec_chosen, is_init_cond_chosen, is_bound_cond_chosen, is_tanks_chosen, is_task_chosen;
    
    public RecCAInterface(){
        System.out.println("RecCAInterface: constructor creation start");
        
        transfer_data_bank = new TransferredDataBank();
        
        transfer_data_bank.setIsWindows(OSUtils.isWindows());
        
        if(transfer_data_bank.isWindows){
            System.out.println("User OS: Windows");
        }
        else{
            System.out.println("User OS: Linux");
        }
        initMenuBar();
        handleEvents();
        main_layout.setTop(menu_bar);
        main_scene = new Scene(main_layout, scene_width, scene_height);
        main_scene.getStylesheets().add("./interf/SecamStyleFX.css");
        this.setScene(main_scene);
        this.setTitle("RecCA - FX");
        this.show();
        
        transfer_data_bank.setRecCAInterface(this);        
    }
    
    MenuBar menu_bar;
    Menu specimen, program, results_menu, help;
    MenuItem spec, init_cond,
             bound_cond, tank,
             task, run_program, show_results,
             help_menu_item, exit;
    
    private void initMenuBar(){
        System.out.println("RecCAInterface method initMenuBat(): start");
        
        menu_bar = new MenuBar();
        
        specimen = new Menu("Specimen");
        
        spec = new MenuItem("Specimen");
        
        init_cond = new MenuItem("Initial Conditions");
        bound_cond = new MenuItem("Boundary Conditions");
        tank = new MenuItem("Tank");
        task = new MenuItem("Task");
        
        program = new Menu("Program");
        
        run_program = new MenuItem("Run Program");
        run_program.setAccelerator(KeyCombination.keyCombination("F5"));
        
        exit = new MenuItem("Exit.");
        exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        exit.setOnAction(e -> Platform.exit());
        
        program.getItems().addAll(run_program, new SeparatorMenuItem(),exit);
        
        results_menu = new Menu("Results");
        show_results = new MenuItem("Show Results");
        
        results_menu.getItems().add(show_results);
        
        help = new Menu("Help");
        
        help_menu_item = new MenuItem("About");
        help_menu_item.setAccelerator(KeyCombination.keyCombination("F1"));
        
        help.getItems().add(help_menu_item);
        
        specimen.getItems().addAll(
                spec,
                new SeparatorMenuItem(),
                init_cond,
                bound_cond,
                tank, 
                new SeparatorMenuItem(),
                task
        );
        
        menu_bar.getMenus().addAll(
                program,
                specimen,
                results_menu,
                help
        );
    }
    
    public void handleEvents(){
        
        spec.setOnAction(e -> {
            System.out.println("Action performed: specimen");
            list_of_some_files = new RecCAFileList(transfer_data_bank, Common.SPEC_PATH, "");
            
            list_of_some_files.create_new.setOnAction(event -> {
                System.out.println("Action Performed: create new specimen");
                transfer_data_bank.setUISpecimen(new UISpecimen());
                specimen_params = new RecCASpecimenParams(transfer_data_bank);
                specimen_params.showAndWait();                
            });
            
            list_of_some_files.change.setOnAction(event -> {
                if(list_of_some_files.list.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR,"Nothing is chosen!").showAndWait();
                else{
                    String selected_val = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                    /*
                    * load parameters of existed specimen
                    */
                    UISpecimen existed_specimen_params = new UISpecimen(Common.SPEC_PATH+"/"+selected_val+"/"+selected_val+".");

                   /*
                    * remember specimen parameters in transfer data bank
                    */
                    transfer_data_bank.setUISpecimen(existed_specimen_params);

                   /*
                    * create internal frame with parameters
                    */
                    RecCASpecimenParams spec_params = new RecCASpecimenParams(transfer_data_bank);
                    spec_params.textfield_for_file_location.setText(selected_val);
                    spec_params.show();
                }                
            });
            
            list_of_some_files.ok.setOnAction(event -> {
                if(list_of_some_files.list.getSelectionModel().isEmpty())
                    new Alert(AlertType.ERROR, "Nothing is chosen").showAndWait();
                else{
                    String spec_name = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                    System.out.println("Selected Specimen : " + spec_name);
                    // Set path of lrs (special specimen) path = null
                    transfer_data_bank.getUIInterface().setSpecialSpecimenPath(null);

                   /*
                    * remember path of choosed specimen
                    */
                    transfer_data_bank.getUIInterface().setSpecimenPath(spec_name);
                    spec_picture = new RecCASpecimenPict(new UISpecimen(Common.SPEC_PATH+"/"+spec_name+"/"+spec_name+"."), transfer_data_bank);
                    spec_picture.specimen_param.setExpanded(true);
                    spec_picture.materials.setExpanded(true);
                    main_layout.setCenter(spec_picture);
                    /*
                    * Set default parameter values of initial, boundary conditions and also tasks
                    */
                    DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);
                    
                    is_spec_chosen = true;
                    list_of_some_files.close();
                }
            });
            
            list_of_some_files.delete.setOnAction(event -> {
                String selected = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                String path = System.getProperty("user.dir") + "\\user\\spec_db\\" + selected;
                System.out.println(path);
                list_of_some_files.deleteFolder(path);
                list_of_some_files.refreshList();
            });            
            
            list_of_some_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.getClickCount() == 2){
                        String spec_name = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                        System.out.println("Selected Specimen : " + spec_name);
                        // Set path of lrs (special specimen) path = null
                        transfer_data_bank.getUIInterface().setSpecialSpecimenPath(null);

                       /*
                        * remember path of choosed specimen
                        */
                        transfer_data_bank.getUIInterface().setSpecimenPath(spec_name);
                        spec_picture = new RecCASpecimenPict(new UISpecimen(Common.SPEC_PATH+"/"+spec_name+"/"+spec_name+"."), transfer_data_bank);
                        spec_picture.specimen_param.setExpanded(true);
                        spec_picture.materials.setExpanded(true);
                        main_layout.setCenter(spec_picture);
                        /*
                        * Set default parameter values of initial, boundary conditions and also tasks
                        */
                        DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);

                        is_spec_chosen = true;
                        list_of_some_files.close();                       
                    }
                }
            });
            
            list_of_some_files.list.setOnMousePressed(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    if(event.isSecondaryButtonDown()){
                        list_of_some_files.context_menu.show(list_of_some_files, event.getScreenX(), event.getScreenY());
                    }
                }
            });
            
            list_of_some_files.open_containing_folder.setOnAction(event -> {
                System.out.println("Open Containing folder event");
                String path =System.getProperty("dir") + "\\user\\spec_db\\" + list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                try {
                    Process p = new ProcessBuilder("explorer.exe", "/select,"+path).start();
                } catch (IOException ex) {
                    System.out.println("Error: can not open folder \'" +path + "\'");
                }
            });
            
            list_of_some_files.select_menu_item.setOnAction(event -> {
                System.out.println("Action Performed: select");
                String spec_name = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                System.out.println("Selected Specimen : " + spec_name);
                // Set path of lrs (special specimen) path = null
                transfer_data_bank.getUIInterface().setSpecialSpecimenPath(null);

               /*
                * remember path of choosed specimen
                */
                transfer_data_bank.getUIInterface().setSpecimenPath(spec_name);
                spec_picture = new RecCASpecimenPict(new UISpecimen(Common.SPEC_PATH+"/"+spec_name+"/"+spec_name+"."), transfer_data_bank);
                spec_picture.specimen_param.setExpanded(true);
                spec_picture.materials.setExpanded(true);
                main_layout.setCenter(spec_picture);
                /*
                * Set default parameter values of initial, boundary conditions and also tasks
                */
                DefaultParametersCreator default_parameters_creator = new DefaultParametersCreator(transfer_data_bank);

                is_spec_chosen = true;
                list_of_some_files.close();
            });

            list_of_some_files.change_menu_item.setOnAction(event -> {
                System.out.println("Action Performed: change");
                String selected_val = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                /*
                * load parameters of existed specimen
                */
                UISpecimen existed_specimen_params = new UISpecimen(Common.SPEC_PATH+"/"+selected_val+"/"+selected_val+".");

               /*
                * remember specimen parameters in transfer data bank
                */
                transfer_data_bank.setUISpecimen(existed_specimen_params);

               /*
                * create internal frame with parameters
                */
                RecCASpecimenParams spec_params = new RecCASpecimenParams(transfer_data_bank);
                spec_params.show();
            });

            list_of_some_files.delete_menu_item.setOnAction(event -> {
                System.out.println("Action Performed: delete");
            });
        });
        
        init_cond.setOnAction(e -> {
            System.out.println("Action Performed: initial conditions");
            
            if(transfer_data_bank.getUIInterface().getSpecimenPath() == null){
                new Alert(AlertType.ERROR, "Specimen is not choosen").show();
            }
            else{
                list_of_some_files = new RecCAFileList(transfer_data_bank, "init_cond", Common.INIT_COND_EXTENSION );
                
                list_of_some_files.create_new.setOnAction(event -> {
                    System.out.println("Action Performed: create new initial conditions");
                    transfer_data_bank.setUIInitialCondition(new UIInitialCondition());
                    init_cond_params = new RecCAInitialConditionsParams(transfer_data_bank);
                    init_cond_params.show();
                });
                list_of_some_files.ok.setOnAction(event -> {
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen").showAndWait();
                    else{
                        transfer_data_bank.getUIInterface().setInitCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                        spec_picture.setInitCond(new UIInitialCondition(Common.INIT_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+Common.INIT_COND_NAME+"/"+list_of_some_files.list.getSelectionModel().getSelectedItem()+"."+Common.INIT_COND_EXTENSION));
                        spec_picture.init_cond.setExpanded(true);
                        list_of_some_files.close();
                    }                    
                });
                list_of_some_files.change.setOnAction(event -> {
                    System.out.println("Action Performed: change initial conditions");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen").showAndWait();
                    else{
                        String selected = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                        transfer_data_bank.setUIInitialCondition(new UIInitialCondition(Common.INIT_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+Common.INIT_COND_NAME+"/"+selected+"."+Common.INIT_COND_EXTENSION));
                        transfer_data_bank.getUIInterface().setInitCondPath(selected);
                        RecCAInitialConditionsParams init_params = new RecCAInitialConditionsParams(transfer_data_bank);
                        init_params.show();
                        init_params.textfield_for_file_location.setText(selected);
                    }
                });
                list_of_some_files.delete.setOnAction(event -> {
                    System.out.println("Action Performed: delete initial conditions");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen").showAndWait();
                    else{
                        //TODO
                    }
                });
                
                list_of_some_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount() == 2){
                            transfer_data_bank.getUIInterface().setInitCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                            spec_picture.setInitCond(new UIInitialCondition(Common.INIT_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+Common.INIT_COND_NAME+"/"+list_of_some_files.list.getSelectionModel().getSelectedItem()+"."+Common.INIT_COND_EXTENSION));                    
                            spec_picture.init_cond.setExpanded(true);
                            list_of_some_files.close();
                        }
                    }
                });
                
                list_of_some_files.list.setOnMousePressed(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.isSecondaryButtonDown()){
                            list_of_some_files.context_menu.show(list_of_some_files, event.getScreenX(), event.getScreenY());
                        }
                    }
                });
                
                list_of_some_files.open_containing_folder.setOnAction(event -> {
                    System.out.println("Action performed: open containing folder");
                });
                
                list_of_some_files.select_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: select");
                    transfer_data_bank.getUIInterface().setInitCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                    spec_picture.setInitCond(new UIInitialCondition(Common.INIT_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+Common.INIT_COND_NAME+"/"+list_of_some_files.list.getSelectionModel().getSelectedItem()+"."+Common.INIT_COND_EXTENSION));
                    spec_picture.init_cond.setExpanded(true);
                    list_of_some_files.close();
                });
                
                list_of_some_files.change_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: change");
                    transfer_data_bank.setUIInitialCondition(new UIInitialCondition(Common.INIT_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+Common.INIT_COND_NAME+"/"+list_of_some_files.list.getSelectionModel().getSelectedItem()+"."+Common.INIT_COND_EXTENSION));
                    transfer_data_bank.getUIInterface().setInitCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                    RecCAInitialConditionsParams init_params = new RecCAInitialConditionsParams(transfer_data_bank);
                    init_params.show();
                });
                
                list_of_some_files.delete_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: delete");
                    //TODO
                });
            }            
        });
        
        bound_cond.setOnAction(e -> {
            System.out.println("Action Performed: boundary conditions");
            if(transfer_data_bank.getUIInterface().getInitCondPath() == null){
                new Alert(AlertType.ERROR, "Initial Conditions is not chosen").showAndWait();
            }
            else{
                list_of_some_files = new RecCAFileList(transfer_data_bank, "bound_cond", Common.BOUND_COND_EXTENSION );
                
                list_of_some_files.create_new.setOnAction(event -> {
                    System.out.println("Action Performed: create new boundary conditions");
                    transfer_data_bank.setUIBoundaryCondition(new UIBoundaryCondition());
                    bound_cond_params = new RecCABoundaryConditionsParams(transfer_data_bank);
                    bound_cond_params.show();
                });
                
                list_of_some_files.ok.setOnAction(event -> {
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen").showAndWait();
                    else{
                        transfer_data_bank.getUIInterface().setBoundCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                        spec_picture.setBoundCond(new UIBoundaryCondition(transfer_data_bank.getUIInterface()), transfer_data_bank.getUIInterface());
                        spec_picture.bound_cond.setExpanded(true);
                        list_of_some_files.close();
                    }                                        
                });
                
                list_of_some_files.change.setOnAction(event -> {
                    System.out.println("Action Performed: change boundary conditions");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen").showAndWait();
                    else{
                        String selected = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                        transfer_data_bank.getUIInterface().setBoundCondPath(selected);
                        transfer_data_bank.setUIBoundaryCondition(new UIBoundaryCondition(transfer_data_bank.getUIInterface()));
                        RecCABoundaryConditionsParams bound_params = new RecCABoundaryConditionsParams(transfer_data_bank);
                        bound_params.show();
                        bound_params.textfield_for_file_location.setText(selected);
                    }                    
                });
                
                list_of_some_files.delete.setOnAction(event -> {
                    System.out.println("Action Performed: delete boundary conditions");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen").showAndWait();
                    else{
                        //TODO
                    }
                });
                
                list_of_some_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount() == 2){
                            transfer_data_bank.getUIInterface().setBoundCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                            spec_picture.setBoundCond(new UIBoundaryCondition(transfer_data_bank.getUIInterface()), transfer_data_bank.getUIInterface());
                            spec_picture.bound_cond.setExpanded(true);
                            list_of_some_files.close();
                        }
                    }
                });
                
                list_of_some_files.list.setOnMousePressed(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.isSecondaryButtonDown()){
                            list_of_some_files.context_menu.show(list_of_some_files, event.getScreenX(), event.getScreenY());
                        }
                    }
                });
                
                list_of_some_files.open_containing_folder.setOnAction(event -> {
                    System.out.println("Action performed: open containing folder");
                });
                
                list_of_some_files.select_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: select");
                    transfer_data_bank.getUIInterface().setBoundCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                    spec_picture.setBoundCond(new UIBoundaryCondition(transfer_data_bank.getUIInterface()), transfer_data_bank.getUIInterface());
                    spec_picture.bound_cond.setExpanded(true);
                    list_of_some_files.close();
                });
                
                list_of_some_files.change_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: change");
                    transfer_data_bank.getUIInterface().setBoundCondPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                    transfer_data_bank.setUIBoundaryCondition(new UIBoundaryCondition(transfer_data_bank.getUIInterface()));
                    RecCABoundaryConditionsParams bound_params = new RecCABoundaryConditionsParams(transfer_data_bank);
                    bound_params.show();
                });
                
                list_of_some_files.delete_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: delete");
                    //TODO
                });
            }
        });
        
        tank.setOnAction(e -> {
            System.out.println("Action Performed: tank");
            if(transfer_data_bank.getUIInterface().getBoundCondPath()== null){
                new Alert(AlertType.ERROR, "Boundary Conditions is not chosen").showAndWait();
            }
            else{
                
                list_of_some_files = new RecCAFileList(transfer_data_bank, "tank", Common.TANK_EXTENSION );
                
                list_of_some_files.create_new.setOnAction(event -> {
                    System.out.println("Action Performed: create new tank");
                    transfer_data_bank.setUITank(new UITank());
                    tank_cond_params = new RecCATankParameters(transfer_data_bank);
                    tank_cond_params.show();
                });
                
                list_of_some_files.ok.setOnAction(event -> {
                    System.out.println("Action Performed: tank is selected");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen!").showAndWait();
                    else{
                        transfer_data_bank.getUIInterface().setTankPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                        transfer_data_bank.setUITank(new UITank(Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/bound_cond_db/"+transfer_data_bank.getUIInterface().getTankPath()+"."+Common.TANK_EXTENSION));
                        spec_picture.setTank(transfer_data_bank.getUITank(), transfer_data_bank.getUIInterface());
                        list_of_some_files.close();
                        spec_picture.tank.setExpanded(true);
                    }                                        
                });
                
                list_of_some_files.change.setOnAction(event -> {
                    System.out.println("Action Performed: change tank");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen!").showAndWait();
                    else{
                        String selected = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                        transfer_data_bank.getUIInterface().setTankPath(selected);
                        transfer_data_bank.setUITank(new UITank(Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/bound_cond_db/"+transfer_data_bank.getUIInterface().getTankPath()+"_tanks."+Common.TANK_EXTENSION));
                        System.out.println("path = "+Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/bound_cond_db/"+transfer_data_bank.getUIInterface().getTankPath()+"_tanks."+Common.TANK_EXTENSION);
                        RecCATankParameters tank_parameters = new RecCATankParameters(transfer_data_bank);
                        tank_parameters.show();
                        tank_parameters.textfield_for_file_location.setText(selected);
                    }                                        
                });
                
                list_of_some_files.delete.setOnAction(event -> {
                    System.out.println("Action Performed: delete tank");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen!").showAndWait();
                    else{
                        //TODO
                    }
                });
                
                list_of_some_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount() == 2){
                            transfer_data_bank.getUIInterface().setTankPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                            transfer_data_bank.setUITank(new UITank(Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/bound_cond_db/"+transfer_data_bank.getUIInterface().getTankPath()+"."+Common.TANK_EXTENSION));
                            spec_picture.setTank(transfer_data_bank.getUITank(), transfer_data_bank.getUIInterface());
                            list_of_some_files.close();
                            spec_picture.tank.setExpanded(true);
                        }
                    }
                });
                
                list_of_some_files.list.setOnMousePressed(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.isSecondaryButtonDown()){
                            list_of_some_files.context_menu.show(list_of_some_files, event.getScreenX(), event.getScreenY());
                        }
                    }
                });
                
                list_of_some_files.open_containing_folder.setOnAction(event -> {
                    System.out.println("Action performed: open containing folder");
                });
                
                list_of_some_files.select_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: select");
                    transfer_data_bank.getUIInterface().setTankPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                    transfer_data_bank.setUITank(new UITank(Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/bound_cond_db/"+transfer_data_bank.getUIInterface().getTankPath()+"."+Common.TANK_EXTENSION));
                    spec_picture.setTank(transfer_data_bank.getUITank(), transfer_data_bank.getUIInterface());
                    list_of_some_files.close();
                    spec_picture.tank.setExpanded(true);
                });
                
                list_of_some_files.change_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: change");
                    transfer_data_bank.getUIInterface().setTankPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                    transfer_data_bank.setUITank(new UITank(Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/bound_cond_db/"+transfer_data_bank.getUIInterface().getTankPath()+"_tanks."+Common.TANK_EXTENSION));
                    System.out.println("path = "+Common.BOUND_COND_PATH+"/"+transfer_data_bank.getUIInterface().getSpecimenPath()+"/bound_cond_db/"+transfer_data_bank.getUIInterface().getTankPath()+"_tanks."+Common.TANK_EXTENSION);
                    RecCATankParameters tank_parameters = new RecCATankParameters(transfer_data_bank);
                    tank_parameters.show();
                });
                
                list_of_some_files.delete_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: delete");
                });
            }
        });
        
        task.setOnAction(e -> {
            System.out.println("Action Performed: task");
            if(transfer_data_bank.getUIInterface().getSpecimenPath() == null){
                new Alert(AlertType.ERROR, "Specimen is not choosen").showAndWait();
            }
            else if(transfer_data_bank.getUIInterface().getInitCondPath() == null){
                new Alert(AlertType.ERROR, "Initial Conditions is not chosen").showAndWait();
            }
            else if(transfer_data_bank.getUIInterface().getBoundCondPath()== null){
                new Alert(AlertType.ERROR, "Boundary Conditions is not chosen").showAndWait();
            }
            else if(transfer_data_bank.getUIInterface().getTankPath()== null){
                new Alert(AlertType.ERROR, "Tank is not chosen").showAndWait();
            }
            else{
                
                list_of_some_files = new RecCAFileList(transfer_data_bank, "task", Common.TASK_EXTENSION );
                
                list_of_some_files.create_new.setOnAction(event -> {
                    System.out.println("Action Performed: create new task");
                    transfer_data_bank.setUITask(new UITask());
                    task_params = new RecCATasksParams(transfer_data_bank);
                    task_params.show();
                });
                
                list_of_some_files.ok.setOnAction(event -> {
                    System.out.println("Action Performed: task is selected");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen!").showAndWait();
                    else{
                        String task_name = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                        if(task_name!=null)
                        {
                           UICommon.task_name = task_name;

                           transfer_data_bank.getUIInterface().setTaskPath(task_name);
                           transfer_data_bank.setUITask(new UITask(Common.TASK_PATH+"/"+task_name+"."+Common.TASK_EXTENSION));
                           SaveParams task_save_in_file = new SaveParams(transfer_data_bank.getUITask(), transfer_data_bank.getUIInterface().getTaskPath(), transfer_data_bank.getUIInterface(), transfer_data_bank.getUISpecimen(),
                                   transfer_data_bank.getUIBoundaryCondition(), transfer_data_bank.getUIInterface().getTankPath());
                           spec_picture.setTask(transfer_data_bank.getUITask(), transfer_data_bank.getUIInterface());
                           list_of_some_files.close();
                           spec_picture.task.setExpanded(true);
                        }
                    }                                        
                });
                
                list_of_some_files.change.setOnAction(event -> {
                    System.out.println("Action Performed: change task");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen!").showAndWait();
                    else{
                        String selected = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                        /*
                        * load bank of choosed task data
                        */
                       transfer_data_bank.setUITask(new UITask(Common.TASK_PATH+"/"+selected+"."+Common.TASK_EXTENSION));

                       /*
                        * create frame with parameters of choosed task
                        */

                       transfer_data_bank.getUIInterface().setTaskPath(selected);
                       RecCATasksParams task_param = new RecCATasksParams(transfer_data_bank);
                       task_param.show();
                       task_param.textfield_for_file_location.setText(selected);
                    }                    
                });
                
                list_of_some_files.delete.setOnAction(event -> {
                    System.out.println("Action Performed: delete task");
                    if(list_of_some_files.list.getSelectionModel().isEmpty())
                        new Alert(AlertType.ERROR, "Nothing is chosen!").showAndWait();
                    else{
                        //TODO
                    }
                });
                
                list_of_some_files.list.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount() == 2){
                            String task_name = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                            if(task_name!=null)
                            {
                               UICommon.task_name = task_name;

                               transfer_data_bank.getUIInterface().setTaskPath(task_name);
                               transfer_data_bank.setUITask(new UITask(Common.TASK_PATH+"/"+task_name+"."+Common.TASK_EXTENSION));
                               SaveParams task_save_in_file = new SaveParams(transfer_data_bank.getUITask(), transfer_data_bank.getUIInterface().getTaskPath(), transfer_data_bank.getUIInterface(), transfer_data_bank.getUISpecimen(),
                                       transfer_data_bank.getUIBoundaryCondition(), transfer_data_bank.getUIInterface().getTankPath());
                               spec_picture.setTask(transfer_data_bank.getUITask(), transfer_data_bank.getUIInterface());
                               list_of_some_files.close();
                               spec_picture.task.setExpanded(true);
                            }
                        }
                    }
                });
                
                list_of_some_files.list.setOnMousePressed(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.isSecondaryButtonDown()){
                            list_of_some_files.context_menu.show(list_of_some_files, event.getScreenX(), event.getScreenY());
                        }
                    }
                });
                
                list_of_some_files.open_containing_folder.setOnAction(event -> {
                    System.out.println("Action performed: open containing folder");
                });
                
                list_of_some_files.select_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: select");
                    String task_name = list_of_some_files.list.getSelectionModel().getSelectedItem().toString();
                    if(task_name!=null)
                    {
                       UICommon.task_name = task_name;

                       transfer_data_bank.getUIInterface().setTaskPath(task_name);
                       transfer_data_bank.setUITask(new UITask(Common.TASK_PATH+"/"+task_name+"."+Common.TASK_EXTENSION));
                       SaveParams task_save_in_file = new SaveParams(transfer_data_bank.getUITask(), transfer_data_bank.getUIInterface().getTaskPath(), transfer_data_bank.getUIInterface(), transfer_data_bank.getUISpecimen(),
                               transfer_data_bank.getUIBoundaryCondition(), transfer_data_bank.getUIInterface().getTankPath());
                       spec_picture.setTask(transfer_data_bank.getUITask(), transfer_data_bank.getUIInterface());                       
                       list_of_some_files.close();
                       spec_picture.task.setExpanded(true);
                    }
                });
                
                list_of_some_files.change_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: change");
                    transfer_data_bank.setUITask(new UITask(Common.TASK_PATH+"/"+list_of_some_files.list.getSelectionModel().getSelectedItem()+"."+Common.TASK_EXTENSION));
                    /*
                     * create frame with parameters of choosed task
                     */
                    transfer_data_bank.getUIInterface().setTaskPath(list_of_some_files.list.getSelectionModel().getSelectedItem().toString());
                    RecCATasksParams task_param = new RecCATasksParams(transfer_data_bank);
                    task_param.show();
                });
                
                list_of_some_files.delete_menu_item.setOnAction(event -> {
                    System.out.println("Action Performed: delete");
                });
            }
        });

        run_program.setOnAction(e -> {
            System.out.println("Action Performed Run Program is pushed");
            if(transfer_data_bank.getUIInterface().getSpecimenPath() == null){
                new Alert(AlertType.ERROR, UICommon.NO_SPECIMEN_CHOOSED).show();
            }
            else{
               recrystallization = new StructureGeneratorAndRecrystallizator(transfer_data_bank, UICommon.RECRYSTALLIZATION_CREATION_NAME); 
            }            
        });
        
        show_results.setOnAction(e -> {
            System.out.println("Action Performed Show Results is pushed");
            results = new RecCAResultsParams(transfer_data_bank);
        });
        
        help_menu_item.setOnAction(e -> new RecCAHelp());
    }    
}
