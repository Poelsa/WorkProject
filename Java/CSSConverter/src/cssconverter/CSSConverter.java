/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cssconverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Quaternion;


public class CSSConverter {
    
    
    public static void main(String[] args) {
        
        int scale = 100;
        Vector2f sizeOfFace = new Vector2f();
        Vector3f centerOfFace = new Vector3f();
        Quaternion realToFlat = new Quaternion();
        Quaternion flatToReal = new Quaternion();
        
        //Load obj file
        Model m = null;
        try {
            m = OBJLoader.loadModel(new File("src/model/"));
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        for (int i = 0; i < m.faces.size(); i++) {
            Face currentFace = m.faces.get(i);
            
            Vector3f aTri = m.vertices.get((int)currentFace.a);
            Vector3f bTri = m.vertices.get((int)currentFace.b);
            Vector3f cTri = m.vertices.get((int)currentFace.c);
            
            Vector3f norm = currentFace.normal;
        }
    }
}