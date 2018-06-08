package buffers;

import java.util.Stack;

import math.Matrix;
import math.Vector3f;

public class PointBuffer {

    private Matrix points;
    private Stack<Matrix> transformations;

    private int pointCount;

    public PointBuffer(Matrix points) {
        this.points = points;
        transformations = new Stack<>();

        Matrix global = new Matrix(4);
        global.fillWithIdentity();
        transformations.push(global);

        pointCount = 0;
    }

    public PointBuffer() {
        this(new Matrix(0));
    }

    public void addPoint(Vector3f point) {
        Matrix temp = point.toMatrixColumn();

        temp.multiply(transformations.peek());

        pointCount++;
        points.growColumns(pointCount);

        // Transfer values from the temp matrix to our matrix
        for (int i = 0; i < temp.getRowCount(); i++) {
            points.setValue(pointCount - 1, i, temp.getValue(0, i));
        }
    }

    public void addPoint(float x, float y, float z) {
        addPoint(new Vector3f(x, y, z));
    }

    public void addPoints(Matrix m) {
        for (int col = 0; col < m.getColCount(); col++) {
            addPoint(m.getColumnVector(col));
        }
    }

    // HELPER FUNCTION
    private void addTransform(float transMat[][]) {
        Matrix trans = new Matrix(transMat);

        // Multiply trans mat by our current transformation, and replace
        // our current transformation mat with that new mat
        Matrix top = transformations.peek();
        trans.multiply(top);
        transformations.push(trans);
    }

    public void transformSetIdentity() {
        transformations.peek().fillWithIdentity();
    }

    public void translate(float dx, float dy, float dz) {

        float trans_mat[][] = { 
                { 1, 0, 0, dx }, 
                { 0, 1, 0, dy }, 
                { 0, 0, 1, dz }, 
                { 0, 0, 0, 1 } 
            };
        addTransform(trans_mat);
    }

    public void scale(float sx, float sy, float sz) {

        float trans_mat[][] = { 
                { sx, 0, 0, 0 },
                { 0, sy, 0, 0 },
                { 0, 0, sz, 0 },
                { 0, 0, 0, 1 }
            };
        addTransform(trans_mat);
    }

    public void rotateX(float theta) {
        theta *= Math.PI / 180.0f;
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);

        float trans_mat[][] = { 
                { 1, 0, 0, 0 },
                { 0, c, s, 0 },
                { 0, -s, c, 0 },
                { 0, 0, 0, 1 }
            };
        addTransform(trans_mat);
    }

    public void rotateY(float theta) {
        theta *= Math.PI / 180.0f;
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);

        float trans_mat[][] = {
                { c, 0, s, 0 },
                { 0, 1, 0, 0 },
                { -s, 0, c, 0 },
                { 0, 0, 0, 1 }
            };
        addTransform(trans_mat);
    }

    public void rotateZ(float theta) {
        theta *= Math.PI / 180.0f;
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);

        float trans_mat[][] = {
                { c, s, 0, 0 },
                { -s, c, 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 1 }
            };
        addTransform(trans_mat);
    }

    public void transformPush() {
        Matrix copy = new Matrix(4);
        transformations.peek().copyTo(copy);
        transformations.push(copy);
    }

    public Matrix transformPop() {
        if (transformations.size() > 1) {
            return transformations.pop();
        }
        return null;
    }

    public void clear() {
        transformations.clear();
        points.growColumns(0);

        Matrix global = new Matrix(4);
        global.fillWithIdentity();
        transformations.push(global);

        pointCount = 0;
    }

    public Matrix getPoints() {
        return points;
    }

}
