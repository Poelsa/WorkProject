package cssconverter;


import toxi.geom.Vec3D;

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
    public Vec3D UVIndices = new Vec3D();
    public Vec3D normalIndices = new Vec3D();
    public Vec3D normal = new Vec3D();
    
    public Face(int a, int b, int c, Vec3D UV, Vec3D normalIndices) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.UVIndices = UV;
        this.normalIndices = normalIndices;
    }
    
    public void setNormal(Vec3D normal) {
        this.normal = normal;
    }
}
