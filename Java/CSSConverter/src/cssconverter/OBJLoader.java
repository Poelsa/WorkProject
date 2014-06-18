package cssconverter;


import java.io.BufferedReader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Joel
 */
public class OBJLoader {
    public static Model loadModel(File f) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        
        Model m = new Model();
        String line;
        
        while((line = reader.readLine()) != null) {
            if(line.startsWith("v ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.vertices.add(new Vector3f(x,y,z));
            }
            else if(line.startsWith("vn ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.normals.add(new Vector3f(x,y,z));
            }
            else if(line.startsWith("vt ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                m.UV.add(new Vector2f(x,y));
            }
            else if(line.startsWith("f ")) {
                int a = Integer.valueOf(line.split(" ")[1].split("/")[0]);
                int b = Integer.valueOf(line.split(" ")[2].split("/")[0]);
                int c = Integer.valueOf(line.split(" ")[3].split("/")[0]);
                Vector3f UVIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[1]), 
                                                  Float.valueOf(line.split(" ")[2].split("/")[1]),
                                                  Float.valueOf(line.split(" ")[3].split("/")[1]));
                Vector3f NormalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]), 
                                                      Float.valueOf(line.split(" ")[2].split("/")[2]),
                                                      Float.valueOf(line.split(" ")[3].split("/")[2]));
                m.faces.add(new Face(a, b, c, UVIndices, NormalIndices));
            }
        }
        for(int i = 0; i < m.faces.size(); i++) {
            Vector3f norm = new Vector3f();
            
            Vector3f u = Vector3f.sub(m.vertices.get(m.faces.get(i).b), m.vertices.get(m.faces.get(i).a), null);
            Vector3f v = Vector3f.sub(m.vertices.get(m.faces.get(i).c), m.vertices.get(m.faces.get(i).a), null);
            norm.x = u.y*v.z - u.z*v.y;
            norm.y = u.z*v.x - u.x*v.z;
            norm.z = u.x*v.y - u.y*v.x;
            
            m.faces.get(i).setNormal(norm);
        }
        reader.close();
        return m;
    }
}
