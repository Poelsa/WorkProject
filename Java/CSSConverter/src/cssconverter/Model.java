package cssconverter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;

/**
 *
 * @author Joel
 */
public class Model {
    public List<Vec3D> vertices = new ArrayList<Vec3D>();
    public List<Vec3D> normals = new ArrayList<Vec3D>();
    public List<Vec2D> UV = new ArrayList<Vec2D>();
    public List<Face> faces = new ArrayList<Face>();
    
    public Model() {
    }
}
