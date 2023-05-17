/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interf;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author DmitryB
 */
public class Dodecagon extends MeshView{
    
    public Dodecagon(){
        TriangleMesh mesh = new TriangleMesh();
        float h = 150;                    // Height
        float s = 300;                    // Side
        mesh.getPoints().addAll(
            0,    0,    0,            // Point 0 - Top
            0,    h,    -s/2,         // Point 1 - Front
            -s/2, h,    0,            // Point 2 - Left
            s/2,  h,    0,            // Point 3 - Back
            0,    h,    s/2           // Point 4 - Right
        );
        mesh.getFaces().addAll(
            0,0,  2,0,  1,0,          // Front left face
            0,0,  1,0,  3,0,          // Front right face
            0,0,  3,0,  4,0,          // Back right face
            0,0,  4,0,  2,0,          // Back left face
            4,0,  1,0,  2,0,          // Bottom rear face
            4,0,  3,0,  1,0           // Bottom front face
        );
        this.setMesh(mesh);
        this.setDrawMode(DrawMode.FILL);
        this.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
    }
    
}
