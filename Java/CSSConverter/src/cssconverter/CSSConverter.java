/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cssconverter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.imageio.ImageIO;
import toxi.geom.Matrix4x4;
import toxi.geom.Quaternion;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;


public class CSSConverter {
    
    
    public static void main(String[] args) {
        
        String modelPath = "src/model/decal.obj";
        String texturePath = "src/model/vinflaska_textur_small.jpg";
        String picsPath = "src/model";
        new File(picsPath).mkdirs();
        String name = "clickable";
        String outPath = "src/model/"+name+".txt";
        String outString = "";
        String onClickAction = "p212201LoadLink();";
        
        boolean savePics = false;
        boolean makeClickable = true;
        
        int scale = 25;
        Vec2D sizeOfFace = new Vec2D();
        Vec3D centerOfFace = new Vec3D();
        Vec3D triCenter = new Vec3D();
        Quaternion realToFlat = new Quaternion();
        Quaternion flatToReal = new Quaternion();
        Quaternion flatToInvReal = new Quaternion();
         
        
        
        //Load obj file
        Model m = null;
        try {
            m = OBJLoader.loadModel(new File(modelPath));
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        //Load texture file
        BufferedImage texture = null;
        try {
            texture = ImageIO.read(new File(texturePath));
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        int texWidth = texture.getWidth();
        int texHeight = texture.getHeight();
        
        float texMaxW=0;
        float texMaxH=0;
        
        float[] texWidths = new float[m.faces.size()];
        float[] texHeights = new float[m.faces.size()];
        float[] faceWidths = new float[m.faces.size()];
        float[] faceHeights = new float[m.faces.size()];
        Matrix4x4[] matrices = new Matrix4x4[m.faces.size()];
        BufferedImage[] warpedTriangles = new BufferedImage[m.faces.size()];
        
        
        
        outString += "<div id='"+name+"' style='top:250px;left:150px;position:absolute;-webkit-transform-style: preserve-3d;'>\n";
        
        for (int i = 0; i < m.faces.size(); i++) {
            Face currentFace = m.faces.get(i);
            
            Vec3D aTri = m.vertices.get((int)currentFace.a).copy();
            Vec3D bTri = m.vertices.get((int)currentFace.b).copy();
            Vec3D cTri = m.vertices.get((int)currentFace.c).copy();
            
            Vec3D norm = currentFace.normal;
            Vec3D invNorm = new Vec3D(currentFace.normal.x, -currentFace.normal.y, currentFace.normal.z);
            Vec3D inVec = new Vec3D(0,0,1); //tepm
            Vec3D calculatedUp = norm.copy(); //up
            
            Vec3D rotAxis = calculatedUp.cross(new Vec3D(0,1,0));
            
            calculatedUp = calculatedUp.rotateAroundAxis(rotAxis.normalize(), (float)Math.PI/2);
            realToFlat = Quaternion.getAlignmentQuat(inVec, norm);
            flatToReal = Quaternion.getAlignmentQuat(norm, inVec);
            flatToInvReal = Quaternion.getAlignmentQuat(invNorm, inVec);
            
            realToFlat.normalize();
            flatToReal.normalize();
            
            Vec3D aVertRotated = realToFlat.toMatrix4x4().applyTo(aTri);
            Vec3D bVertRotated = realToFlat.toMatrix4x4().applyTo(bTri);
            Vec3D cVertRotated = realToFlat.toMatrix4x4().applyTo(cTri);
            realToFlat.toMatrix4x4().applyToSelf(calculatedUp);
            //Normalize things?
            float angle = calculatedUp.angleBetween(new Vec3D(0,1,0), true);
            if(calculatedUp.x < 0)
                angle *= -1;
            //angle += Math.PI;
            aVertRotated.rotateAroundAxis(inVec, angle);
            bVertRotated.rotateAroundAxis(inVec, angle);
            cVertRotated.rotateAroundAxis(inVec, angle);
            
            //triCenter = aVertRotated.add(bVertRotated.add(cVertRotated)).scaleSelf(1/3).scaleSelf(scale);
            
            aVertRotated.scaleSelf(scale);
            bVertRotated.scaleSelf(scale);
            cVertRotated.scaleSelf(scale);
            
            float xMax = Math.max(aVertRotated.x, Math.max(bVertRotated.x, cVertRotated.x));
            float xMin = Math.min(aVertRotated.x, Math.min(bVertRotated.x, cVertRotated.x));
            float yMax = Math.max(aVertRotated.y, Math.max(bVertRotated.y, cVertRotated.y));
            float yMin = Math.min(aVertRotated.y, Math.min(bVertRotated.y, cVertRotated.y));
            float zMax = Math.max(aVertRotated.z, Math.max(bVertRotated.z, cVertRotated.z));
            float zMin = Math.min(aVertRotated.z, Math.min(bVertRotated.z, cVertRotated.z));
            
            centerOfFace = new Vec3D(((xMax+xMin)/2), ((yMax+yMin)/2), ((zMax+zMin)/2));
            
            triCenter = aVertRotated.add(bVertRotated.add(cVertRotated));
            triCenter.scaleSelf(1.0f/3.0f);
            
            float faceW = xMax - xMin;
            float faceH = yMax - yMin;
            sizeOfFace = new Vec2D(faceW, faceH);
            faceWidths[i] = faceW;
            faceHeights[i] = faceH;
            
            Vec2D aBUV = new Vec2D((aVertRotated.x-xMin)/faceW, 1-(aVertRotated.y-yMin)/faceH);
            Vec2D bBUV = new Vec2D((bVertRotated.x-xMin)/faceW, 1-(bVertRotated.y-yMin)/faceH);
            Vec2D cBUV = new Vec2D((cVertRotated.x-xMin)/faceW, 1-(cVertRotated.y-yMin)/faceH);
            
            centerOfFace.rotateAroundAxis(inVec, -angle);
            flatToReal.toMatrix4x4().applyToSelf(centerOfFace);
            triCenter.rotateAroundAxis(inVec, -angle);
            flatToReal.toMatrix4x4().applyToSelf(triCenter);
            
            
            //Texture stuff
            Vec2D aCoord = new Vec2D(texWidth*m.UV.get((int)currentFace.UVIndices.x).x, 
                         texHeight-(texHeight*m.UV.get((int)currentFace.UVIndices.x).y));
            Vec2D bCoord = new Vec2D(texWidth*m.UV.get((int)currentFace.UVIndices.y).x, 
                         texHeight-(texHeight*m.UV.get((int)currentFace.UVIndices.y).y));
            Vec2D cCoord = new Vec2D(texWidth*m.UV.get((int)currentFace.UVIndices.z).x, 
                         texHeight-(texHeight*m.UV.get((int)currentFace.UVIndices.z).y));
            
            float texXMax = Math.max(aCoord.x, Math.max(bCoord.x, cCoord.x));
            float texXMin = Math.min(aCoord.x, Math.min(bCoord.x, cCoord.x));
            float texYMax = Math.max(aCoord.y, Math.max(bCoord.y, cCoord.y));
            float texYMin = Math.min(aCoord.y, Math.min(bCoord.y, cCoord.y));
            
            float texW = texXMax - texXMin;
            float texH = texYMax - texYMin;
            
            Vec2D aLUV = new Vec2D(aCoord.x-texXMin, aCoord.y-texYMin);
            Vec2D bLUV = new Vec2D(bCoord.x-texXMin, bCoord.y-texYMin);
            Vec2D cLUV = new Vec2D(cCoord.x-texXMin, cCoord.y-texYMin);
            
            aBUV.scaleSelf(texW, texH);
            bBUV.scaleSelf(texW, texH);
            cBUV.scaleSelf(texW, texH);
            
            
            texMaxW += texW;
            if(texH>texMaxH)
                texMaxH = texH;
            
            texWidths[i] = texW;
            texHeights[i] = texH;
            //texture stuff, cut out images, deform triangles
            //get subimage(min, min, w, h)
            if(texW<1)
                texW += 1;
            if(texH<1)
                texH += 1;
            
            //send pixeldata and triangles to triangleWarper
            //System.out.print(i);
            
            //save warped triangle image
            if(savePics) {
                BufferedImage subTex = texture.getSubimage((int)texXMin, (int)texYMin, (int)texW, (int)texH);
                warpedTriangles[i] = WarpTriangle(subTex, new Vec2D[] {aLUV, bLUV, cLUV}, new Vec2D[] {aBUV, bBUV, cBUV});
                
            }
            
            Vec3D dirToCenter = new Vec3D(0,0,0).sub(centerOfFace);
            Vec3D dirFromTri = centerOfFace.sub(triCenter);
            centerOfFace.addSelf(dirToCenter.scale(1.5f/(scale)));
            centerOfFace.addSelf(dirFromTri.scale(1.0f/(scale*10)));
            
            //get rotation matrix
            Matrix4x4 matrix = new Matrix4x4().identity();
            Matrix4x4 matZ = new Matrix4x4().identity().getRotatedZ(angle);
            Matrix4x4 mat = flatToInvReal.toMatrix4x4();
            Matrix4x4 transMat = new Matrix4x4().identity().translate(centerOfFace);
            
            matrix.multiplySelf(matZ);
            matrix = mat.multiply(matrix);
            matrix = transMat.multiply(matrix);
            matrix.transpose();
            float epsilon = 0.001f;
            for(int r = 0; r < 4; r++) {
                for(int c = 0; c < 4; c++) {
                    if((matrix.matrix[r][c] > 0 && matrix.matrix[r][c] <= epsilon) 
                     || matrix.matrix[r][c] < 0 && matrix.matrix[r][c] >= -epsilon)
                        matrix.matrix[r][c] = 0;
                }
            }
            
            matrices[i] = matrix.copy();
        }
        
        int nrOfRows = (int)Math.sqrt(texMaxW/Math.ceil(texMaxH));
        int width = (int)texMaxW/nrOfRows;
        int height = (nrOfRows+1)*(int)Math.ceil(texMaxH);
        int currentRow = 0;
        int rowWidth = 0;
        BufferedImage finalImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        for (int i = 0; i < m.faces.size(); i++) {
            
            if(rowWidth+texWidths[i] > width)
            {
                currentRow++;
                rowWidth = 0;
            }
            
            int posX = rowWidth;
            int posY = currentRow*(int)Math.ceil(texMaxH);
            rowWidth += (int)Math.ceil(texWidths[i]);
            
            if(savePics) 
                finalImg.getRaster().setDataElements(posX, posY, warpedTriangles[i].getRaster());
            /*
            float scaleX = (width/texWidths[i]);
            float scaleY = (height/texHeights[i]);
            
            float relX = (float)posX/width;
            float relY = (float)posY/height;
            
            float newW = scaleX*faceWidths[i];
            float newH = scaleY*faceHeights[i];
            */
            //if(i==21)
            //    scaleX += 0;
            //background-image: url(triangulated_sheet.png); 
            //background-size: "+texWidths[i]+"px "+texHeights[i]+"px; 
            String style = "";
            String imgTag = "<div id='"+name+"_"+i+"' ";
            if(makeClickable) {
                style += "onClick='"+onClickAction+"' style='width: "+faceWidths[i]+"px; height: "+faceHeights[i]+"px; position: absolute; ";
            }
            else {
                style += "style='background-position: "+posX+"px "+posY+"px; background-size: "+texWidths[i]+"px "+texHeights[i]+"px; width: "+faceWidths[i]+"px; height: "+faceHeights[i]+"px; position: absolute; ";
            }
            String webkit = "-webkit-transform: translate3d(-50%,-50%,0px) ";
            String matr = "matrix3d("+matrices[i].matrix[0][0]+", "+matrices[i].matrix[0][1]+", "+matrices[i].matrix[0][2]+", "+matrices[i].matrix[0][3]+", "
                                     +matrices[i].matrix[1][0]+", "+matrices[i].matrix[1][1]+", "+matrices[i].matrix[1][2]+", "+matrices[i].matrix[1][3]+", "
                                     +matrices[i].matrix[2][0]+", "+matrices[i].matrix[2][1]+", "+matrices[i].matrix[2][2]+", "+matrices[i].matrix[2][3]+", "
                                     +matrices[i].matrix[3][0]+", "+-matrices[i].matrix[3][1]+", "+matrices[i].matrix[3][2]+", "+matrices[i].matrix[3][3]+");";
            
            style += webkit + matr + "'"; 
            String tempOut = imgTag + style + "></div>\n";
            
            outString += tempOut;
        }
        
        outString += "</div>\n";
        
        writeToOutput(outPath, outString);
        
        if(savePics) {
            try {
                File outputfile = new File(picsPath+"/triangulated_sheet_big.png");
                ImageIO.write(finalImg, "png", outputfile);
            } 
            catch (IOException e) {

            }
        }
    }
    
    public static void writeToOutput(String fileName, String string) {
        
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(fileName), "utf-8"));
            writer.write(string);
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
           try {writer.close();} catch (Exception ex) {}
        }
    }
    
    public static BufferedImage WarpTriangle(BufferedImage srcData, Vec2D[] srcTriangle, Vec2D[] dstTriangle) {
        BufferedImage result = new BufferedImage(srcData.getWidth(), srcData.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        boolean hasAlpha = srcData.isAlphaPremultiplied();
        float eps = 0.0001f;
        
        for(int x = 0; x < result.getWidth(); x++) {
            for(int y = 0; y < result.getHeight(); y++) {
                
                Vec2D uv = cartesian_to_barycentric(dstTriangle, new Vec2D(x,y));
                
                boolean xyInTriangle = (uv.x >= 0-eps) && (uv.y >= 0-eps) && (uv.x + uv.y <= 1+eps);
                
                if(xyInTriangle) {
                    Vec2D src_xy = barycentric_to_cartesian(srcTriangle, uv);
                    int xCoord = (int)(Math.floor(src_xy.x));
                    int yCoord = (int)(Math.floor(src_xy.y));
                    
                    xCoord = Math.max(0, Math.min(result.getWidth()-1, xCoord));
                    yCoord = Math.max(0, Math.min(result.getHeight()-1, yCoord));
                    Color pixelCol = new Color(srcData.getRGB(xCoord, yCoord), hasAlpha);
                    
                    result.setRGB(x, y, pixelCol.getRGB());
                }
                else {
                    result.setRGB(x, y, 0);
                }
            }
        }
        return result;
    }
    
    public static Vec2D cartesian_to_barycentric(Vec2D[] triangle, Vec2D xy) {
        Vec2D a = triangle[0];
        Vec2D b = triangle[1];
        Vec2D c = triangle[2];

        Vec2D v0 = c.sub(a);
        Vec2D v1 = b.sub(a);
        Vec2D v2 = xy.sub(a);

        // Compute dot products
        float dot00 = v0.dot(v0);
        float dot01 = v0.dot(v1);
        float dot02 = v0.dot(v2);
        float dot11 = v1.dot(v1);
        float dot12 = v1.dot(v2);

        // Compute barycentric coordinates
        float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;
        
        return new Vec2D(u,v);
    }
 
    public static Vec2D barycentric_to_cartesian(Vec2D[] triangle, Vec2D uv) {
        Vec2D a = triangle[0].copy();
        Vec2D ba = triangle[1].sub(a);
        Vec2D ca = triangle[2].sub(a);
        
        Vec2D ret = a.addSelf((ca.scaleSelf(uv.x)).addSelf(ba.scaleSelf(uv.y)));
        return ret;
    }
    
}