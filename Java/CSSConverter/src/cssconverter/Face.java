package cssconverter;


import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Joel
 */
public class Face {
    public int a, b, c;
    public Vector3f UVIndices = new Vector3f();
    public Vector3f normalIndices = new Vector3f();
    public Vector3f normal = new Vector3f();
    
    public Face(int a, int b, int c, Vector3f UV, Vector3f normalIndices) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.UVIndices = UV;
        this.normalIndices = normalIndices;
    }
    
    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }
}
