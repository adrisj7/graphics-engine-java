package buffers;

import math.Matrix;

public class TriangleBuffer extends PointBuffer {

    public static final int PARAMETRIC_ACCURACY = 20;

    public void addTriangle(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
        addPoint(x0, y0, z0);
        addPoint(x1, y1, z1);
        addPoint(x2, y2, z2);
    }

    /**
     * Try to delete this. <br>
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

    public Matrix genBox(float x, float y, float z, float xl, float yl, float zl) {
        TriangleBuffer boxBuff = new TriangleBuffer();

        // Cause I was drawing boxes the wrong way yo
        yl *= -1;

        // 3 faces from origin
        boxBuff.addTriangle(x + xl, y + yl, z, x + xl, y, z, x, y, z);
        boxBuff.addTriangle(x, y + yl, z, x + xl, y + yl, z, x, y, z);

        boxBuff.addTriangle(x + xl, y, z + zl, x, y, z, x + xl, y, z);
        boxBuff.addTriangle(x + xl, y, z + zl, x, y, z + zl, x, y, z);

        boxBuff.addTriangle(x, y, z + zl, x, y + yl, z + zl, x, y, z);
        boxBuff.addTriangle(x, y + yl, z + zl, x, y + yl, z, x, y, z);

        // 3 other faces from not the origin
        boxBuff.addTriangle(x + xl, y + yl, z + zl, x + xl, y, z, x + xl, y + yl, z);
        boxBuff.addTriangle(x + xl, y + yl, z + zl, x + xl, y, z + zl, x + xl, y, z);

        boxBuff.addTriangle(x + xl, y + yl, z + zl, x, y + yl, z + zl, x, y, z + zl);
        boxBuff.addTriangle(x + xl, y, z + zl, x + xl, y + yl, z + zl, x, y, z + zl);

        boxBuff.addTriangle(x + xl, y + yl, z + zl, x + xl, y + yl, z, x, y + yl, z);
        boxBuff.addTriangle(x, x + yl, z, x, y + yl, z + zl, x + xl, y + yl, z + zl);

        Matrix toCopy = new Matrix(0);
        boxBuff.getPoints().copyTo(toCopy);
        return toCopy;
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
}
