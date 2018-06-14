package buffers;

import java.util.LinkedList;

import drawing.Image;
import math.Matrix;
import math.Vector2f;

public class TriangleBuffer extends PointBuffer {

    public static final int PARAMETRIC_ACCURACY = 20;

    private LinkedList<Vector2f> texCoordinates;
    private Image texture;

    public TriangleBuffer(Matrix matPoints) {
        super(matPoints);
        texCoordinates = new LinkedList<>();
    }

    public TriangleBuffer() {
        this(new Matrix(0));
    }

    // Optional
    public void addTexCoord(float u, float v) {
        texCoordinates.add(new Vector2f(u,v));
    }

    // Optional
    public void addTexCoordTriangle(float u0, float v0, float u1, float v1, float u2, float v2) {
        addTexCoord(u0, v0);
        addTexCoord(u1, v1);
        addTexCoord(u2, v2);
    }

    // Optional
    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public void addTriangle(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
        addPoint(x0, y0, z0);
        addPoint(x1, y1, z1);
        addPoint(x2, y2, z2);
    }

    /**
     * TODO: Try to delete this. <br>
     * <br>
     * I added it here because I was lazy
     */
    private void addTriangle(double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2,
            double z2) {
        // Be wary, if written incorrectly this is the easiest path to stupid infinite
        // recursion
        addTriangle(
                (float) x0, (float) y0, (float) z0, (float) x1, (float) y1, (float) z1, (float) x2, (float) y2,
                (float) z2
        );
    }

    public Matrix genBox(float x, float y, float z, float xs, float ys, float zs) {
        TriangleBuffer boxBuff = new TriangleBuffer();

        // 3 faces from origin
        float x1 = x+xs;
        float y1 = y-ys;
        float z1 = z-zs;

        //front
        boxBuff.addTriangle(x, y, z, x1, y1, z, x1, y, z);
        boxBuff.addTriangle(x, y, z, x, y1, z, x1, y1, z);

        //back
        boxBuff.addTriangle(x1, y, z1, x, y1, z1, x, y, z1);
        boxBuff.addTriangle(x1, y, z1, x1, y1, z1, x, y1, z1);

        //right side
        boxBuff.addTriangle(x1, y, z, x1, y1, z1, x1, y, z1);
        boxBuff.addTriangle(x1, y, z, x1, y1, z, x1, y1, z1);
        //left side
        boxBuff.addTriangle(x, y, z1, x, y1, z, x, y, z);
        boxBuff.addTriangle(x, y, z1, x, y1, z1, x, y1, z);

        //top
        boxBuff.addTriangle(x, y, z1, x1, y, z, x1, y, z1);
        boxBuff.addTriangle(x, y, z1, x, y, z, x1, y, z);
        //bottom
        boxBuff.addTriangle(x, y1, z, x1, y1, z1, x1, y1, z);
        boxBuff.addTriangle(x, y1, z, x, y1, z1, x1, y1, z1);


        Matrix toCopy = new Matrix(0);
        boxBuff.getPoints().copyTo(toCopy);
        return toCopy;
    }

    // Dear got delete this
    private void tempAddSquareTexture() {
        // Flat, it works
//        addTexCoordTriangle(0, 0, 1, 1, 0, 1);
//        addTexCoordTriangle(0, 0, 1, 1, 1, 0); 

        addTexCoordTriangle(0, 1, 0, 0, 1, 1);
//        addTexCoordTriangle(0, 0, 0, 0, 0, 0);
        addTexCoordTriangle( 1, 1, 0, 0,  1, 0);
    }
    public void addBoxTextured(float x, float y, float z, float xs, float ys, float zs, Image texture) {

        setTexture(texture);

        // 3 faces from origin
        float x1 = x+xs;
        float y1 = y-ys;
        float z1 = z-zs;

        tempAddSquareTexture();
        //front
        addTriangle(x, y, z, x1, y1, z, x1, y, z);
        addTriangle(x, y, z, x, y1, z, x1, y1, z);

        tempAddSquareTexture();
        //back
        addTriangle(x1, y, z1, x, y1, z1, x, y, z1);
        addTriangle(x1, y, z1, x1, y1, z1, x, y1, z1);

        tempAddSquareTexture();
        //right side
        addTriangle(x1, y, z, x1, y1, z1, x1, y, z1);
        addTriangle(x1, y, z, x1, y1, z, x1, y1, z1);

        tempAddSquareTexture();
        //left side
        addTriangle(x, y, z1, x, y1, z, x, y, z);
        addTriangle(x, y, z1, x, y1, z1, x, y1, z);

        tempAddSquareTexture();
        //top
        addTriangle(x, y, z1, x1, y, z, x1, y, z1);
        addTriangle(x, y, z1, x, y, z, x1, y, z);

        tempAddSquareTexture();
        //bottom
        addTriangle(x, y1, z, x1, y1, z1, x1, y1, z);
        addTriangle(x, y1, z, x, y1, z1, x1, y1, z1);
    }

    public Matrix addBox(float x, float y, float z, float xlength, float ylength, float zlength) {
        Matrix boxMat = genBox(x, y, z, xlength, ylength, zlength);
        addPoints(boxMat);
        return boxMat;
    }

    public Matrix genSphere(float x, float y, float z, float r) {
        TriangleBuffer sphereBuff = new TriangleBuffer();

        int theta_count, phi_count;
        double phi_factor = Math.PI * 2.0 / (double) PARAMETRIC_ACCURACY;
        double theta_factor = Math.PI / (double) PARAMETRIC_ACCURACY;
        for (phi_count = 0; phi_count < PARAMETRIC_ACCURACY; phi_count++) {
            double phi = (double) phi_count * phi_factor;
            for (theta_count = 0; theta_count <= PARAMETRIC_ACCURACY; theta_count++) {
                double theta = (double) theta_count * theta_factor;

                // Make a square, but in polar coordinates
                double p1x = x + r * Math.cos(theta);
                double p1y = y + r * Math.sin(theta) * Math.cos(phi);
                double p1z = z + r * Math.sin(theta) * Math.sin(phi);

                double p2x = x + r * Math.cos(theta + theta_factor);
                double p2y = y + r * Math.sin(theta + theta_factor) * Math.cos(phi);
                double p2z = z + r * Math.sin(theta + theta_factor) * Math.sin(phi);

                double p3x = x + r * Math.cos(theta + theta_factor);
                double p3y = y + r * Math.sin(theta + theta_factor) * Math.cos(phi + phi_factor);
                double p3z = z + r * Math.sin(theta + theta_factor) * Math.sin(phi + phi_factor);

                double p4x = x + r * Math.cos(theta);
                double p4y = y + r * Math.sin(theta) * Math.cos(phi + phi_factor);
                double p4z = z + r * Math.sin(theta) * Math.sin(phi + phi_factor);

                sphereBuff.addTriangle(p1x, p1y, p1z, p2x, p2y, p2z, p3x, p3y, p3z);
                // sphereBuff.addTriangle(p1x, p1y, p1z, p4x, p4y, p4z, p3x, p3y, p3z);

                sphereBuff.addTriangle(p3x, p3y, p3z, p4x, p4y, p4z, p1x, p1y, p1z);

            }
        }

        Matrix toCopy = new Matrix(0);
        sphereBuff.getPoints().copyTo(toCopy);
        return toCopy;

    }

    public void addSphere(float x, float y, float z, float r) {
        Matrix sphereMat = genSphere(x, y, z, r);
        addPoints(sphereMat);
    }

    public Matrix genTorus(float x, float y, float z, float rCircle, float rTorus) {
        TriangleBuffer torusBuff = new TriangleBuffer();

        int theta_count, phi_count;
        double angle_factor = Math.PI * 2.0 / (double) PARAMETRIC_ACCURACY;
        for (phi_count = 0; phi_count < PARAMETRIC_ACCURACY; phi_count++) {
            double phi = (double) phi_count * angle_factor;
            // Where our circle is to be drawn in the torus
            double c1x = rTorus * Math.cos(phi);
            double c1y = rTorus * Math.sin(phi);
            double c2x = rTorus * Math.cos(phi + angle_factor);
            double c2y = rTorus * Math.sin(phi + angle_factor);
            for (theta_count = 0; theta_count < PARAMETRIC_ACCURACY; theta_count++) {
                double theta = (double) theta_count * angle_factor;

                double p1x = x + c1x + rCircle * Math.sin(theta) * Math.cos(phi);
                double p1y = y + c1y + rCircle * Math.sin(theta) * Math.sin(phi);
                double p1z = z + rCircle * Math.cos(theta);

                double p2x = x + c1x + rCircle * Math.sin(theta + angle_factor) * Math.cos(phi);
                double p2y = y + c1y + rCircle * Math.sin(theta + angle_factor) * Math.sin(phi);
                double p2z = z + rCircle * Math.cos(theta + angle_factor);

                double p3x = x + c2x + rCircle * Math.sin(theta + angle_factor) * Math.cos(phi + angle_factor);
                double p3y = y + c2y + rCircle * Math.sin(theta + angle_factor) * Math.sin(phi + angle_factor);
                double p3z = z + rCircle * Math.cos(theta + angle_factor);

                double p4x = x + c2x + rCircle * Math.sin(theta) * Math.cos(phi + angle_factor);
                double p4y = y + c2y + rCircle * Math.sin(theta) * Math.sin(phi + angle_factor);
                double p4z = z + rCircle * Math.cos(theta);

                torusBuff.addTriangle(p1x, p1y, p1z, p2x, p2y, p2z, p3x, p3y, p3z);

                torusBuff.addTriangle(p3x, p3y, p3z, p4x, p4y, p4z, p1x, p1y, p1z);
                // torusBuff.addTriangle(p2x, p1y, p1z, p4x, p4y, p4z, p3x, p3y, p3z);

                // torusBuff.addPointyPoint(x + cx + px,y + cy + py,z + pz);
            }
        }

        Matrix toCopy = new Matrix(0);
        torusBuff.getPoints().copyTo(toCopy);
        return toCopy;
    }

    public void addTorus(float x, float y, float z, float rCircle, float rTorus) {
        Matrix torusMat = genTorus(x, y, z, rCircle, rTorus);
        addPoints(torusMat);
    }

    public LinkedList<Vector2f> getTextureCoordinates() {
        return texCoordinates;
    }

    public Image getTexture() {
        return texture;
    }
}
