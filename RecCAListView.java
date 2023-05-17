/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf.demo_list_view;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Orientation;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author mdd
 */
public class RecCAListView extends ToolBar{
    
    TreeView tree;
    
    TreeItem<String> root = new TreeItem("ROOT");
    
    List<String> jocl_stresses, jocl_res;
    
    public RecCAListView(){
        initAllElements();
        this.setOrientation(Orientation.VERTICAL);
        this.getItems().add(tree);
    }
    
    private void initAllElements(){
        
        tree = new TreeView(root);
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        List<String> jocl_stresses = new ArrayList<>();
        jocl_stresses.add("");
        
    }
    
    private void initArrayLists(){
        
    }
    
}
