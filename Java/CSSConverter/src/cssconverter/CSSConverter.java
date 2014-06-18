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
        Vec3D triCenter;
        Quaternion realToFlat = new Quaternion();
        Quaternion flatToReal = new Quaternion();
        
        int texWidth = 2048;
        int texHeight = 2048;
        
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
            Vec3D inVec = new Vec3D(0,0,1); //tepm
            Vec3D calculatedUp = norm; //up
            
            Vec3D rotAxis = calculatedUp.cross(new Vec3D(0,1,0));
            
            calculatedUp.rotateAroundAxis(rotAxis, (float)-Math.PI/2);
            realToFlat = Quaternion.getAlignmentQuat(norm, inVec);
            flatToReal = Quaternion.getAlignmentQuat(inVec, norm);
            
            Vec3D aVertRotated = realToFlat.toMatrix4x4().applyTo(aTri);
            Vec3D bVertRotated = realToFlat.toMatrix4x4().applyTo(bTri);
            Vec3D cVertRotated = realToFlat.toMatrix4x4().applyTo(cTri);
            realToFlat.toMatrix4x4().applyToSelf(calculatedUp);
            //Normalize things?
            float angle = calculatedUp.angleBetween(new Vec3D(0,1,0), true);
            if(calculatedUp.x < 0)
                angle *= -1;
            
            aVertRotated.rotateAroundAxis(inVec, angle);
            bVertRotated.rotateAroundAxis(inVec, angle);
            cVertRotated.rotateAroundAxis(inVec, angle);
            
            triCenter = aVertRotated.add(bVertRotated.add(cVertRotated)).scale(1/3).scale(scale);
            
            aVertRotated.scale(scale);
            bVertRotated.scale(scale);
            cVertRotated.scale(scale);
            
            float xMax = Math.max(aVertRotated.x, Math.max(bVertRotated.x, cVertRotated.x));
            float xMin = Math.min(aVertRotated.x, Math.min(bVertRotated.x, cVertRotated.x));
            float yMax = Math.max(aVertRotated.y, Math.max(bVertRotated.y, cVertRotated.y));
            float yMin = Math.min(aVertRotated.y, Math.min(bVertRotated.y, cVertRotated.y));
            float zMax = Math.max(aVertRotated.z, Math.max(bVertRotated.z, cVertRotated.z));
            float zMin = Math.min(aVertRotated.z, Math.min(bVertRotated.z, cVertRotated.z));
            
            centerOfFace = new Vec3D(((xMax+xMin)/2), ((yMax+yMin)/2), ((zMax+zMin)/2));
            
            float faceW = xMax - xMin;
            float faceH = yMax - yMin;
            sizeOfFace = new Vec2D(faceW, faceH);
            
            Vec2D aBUV = new Vec2D((aVertRotated.x-xMin)/faceW, 1-(aVertRotated.y-yMin)/faceH);
            Vec2D bBUV = new Vec2D((bVertRotated.x-xMin)/faceW, 1-(bVertRotated.y-yMin)/faceH);
            Vec2D cBUV = new Vec2D((cVertRotated.x-xMin)/faceW, 1-(cVertRotated.y-yMin)/faceH);
            
            centerOfFace.rotateAroundAxis(inVec, -angle);
            flatToReal.toMatrix4x4().applyToSelf(centerOfFace);
            triCenter.rotateAroundAxis(inVec, -angle);
            flatToReal.toMatrix4x4().applyToSelf(triCenter);
            
            
            //Texture stuff
            Vec2D aCoord = new Vec2D(texWidth*m.UV.get((int)currentFace.UVIndices.x).x, 
                                 1-(texHeight*m.UV.get((int)currentFace.UVIndices.x).y));
            Vec2D bCoord = new Vec2D(texWidth*m.UV.get((int)currentFace.UVIndices.y).x, 
                                 1-(texHeight*m.UV.get((int)currentFace.UVIndices.y).y));
            Vec2D cCoord = new Vec2D(texWidth*m.UV.get((int)currentFace.UVIndices.z).x, 
                                 1-(texHeight*m.UV.get((int)currentFace.UVIndices.z).y));
            
            float texXMax = Math.max(aCoord.x, Math.max(bCoord.x, cCoord.x));
            float texXMin = Math.min(aCoord.y, Math.min(bCoord.y, cCoord.y));
            float texYMax = Math.max(aCoord.x, Math.max(bCoord.x, cCoord.x));
            float texYMin = Math.min(aCoord.y, Math.min(bCoord.y, cCoord.y));
        }
    }
}