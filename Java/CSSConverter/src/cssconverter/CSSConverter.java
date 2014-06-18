/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cssconverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import toxi.geom.Quaternion;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;


public class CSSConverter {
    
    
    public static void main(String[] args) {
        
        int scale = 100;
        Vec2D sizeOfFace = new Vec2D();
        Vec3D centerOfFace = new Vec3D();
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
            
            Vec3D aTri = m.vertices.get((int)currentFace.a);
            Vec3D bTri = m.vertices.get((int)currentFace.b);
            Vec3D cTri = m.vertices.get((int)currentFace.c);
            
            Vec3D norm = currentFace.normal;
            Vec3D inVec = new Vec3D(0,0,1);
            
            Vec3D rotAxis = norm.cross(new Vec3D(0,1,0));
        }
    }
}