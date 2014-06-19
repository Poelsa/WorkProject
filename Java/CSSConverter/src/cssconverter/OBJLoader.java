package cssconverter;


import java.io.BufferedReader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

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
                int offset = 0;
                while(line.split(" ")[offset+1].equals(""))
                    offset++;
                float x = Float.valueOf(line.split(" ")[offset+1]);
                float y = Float.valueOf(line.split(" ")[offset+2]);
                float z = Float.valueOf(line.split(" ")[offset+3]);
                m.vertices.add(new Vec3D(x,y,z));
            }
            else if(line.startsWith("vn ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);
                m.normals.add(new Vec3D(x,y,z));
            }
            else if(line.startsWith("vt ")) {
                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                m.UV.add(new Vec2D(x,y));
            }
            else if(line.startsWith("f ")) {
                int a = Integer.valueOf(line.split(" ")[1].split("/")[0])-1;
                int b = Integer.valueOf(line.split(" ")[2].split("/")[0])-1;
                int c = Integer.valueOf(line.split(" ")[3].split("/")[0])-1;
                Vec3D UVIndices = new Vec3D(Float.valueOf(line.split(" ")[1].split("/")[1])-1, 
                                                  Float.valueOf(line.split(" ")[2].split("/")[1])-1,
                                                  Float.valueOf(line.split(" ")[3].split("/")[1])-1);
                Vec3D NormalIndices = new Vec3D(Float.valueOf(line.split(" ")[1].split("/")[2])-1, 
                                                      Float.valueOf(line.split(" ")[2].split("/")[2])-1,
                                                      Float.valueOf(line.split(" ")[3].split("/")[2])-1);
                m.faces.add(new Face(a, b, c, UVIndices, NormalIndices));
            }
        }
        for(int i = 0; i < m.faces.size(); i++) {
            Vec3D norm = new Vec3D();
            
            Vec3D u = m.vertices.get(m.faces.get(i).b).sub( m.vertices.get(m.faces.get(i).a));
            Vec3D v = m.vertices.get(m.faces.get(i).c).sub( m.vertices.get(m.faces.get(i).a));
            
            norm.x = u.y*v.z - u.z*v.y;
            norm.y = u.z*v.x - u.x*v.z;
            norm.z = u.x*v.y - u.y*v.x;
            norm.normalize();
            m.faces.get(i).setNormal(norm);
        }
        reader.close();
        return m;
    }
}
