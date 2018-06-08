package math;

public class Matrix {

    private int colCount, rowCount;

    private float[][] values;

    public Matrix(int colCount, int rowCount) {
        this.colCount = colCount;
        this.rowCount = rowCount;

        values = new float[rowCount][colCount];
    }

    // Most of the time, we're dealing with 4x4 matrices
    public Matrix(int colCount) {
        this(colCount, 4);
    }

    public Matrix(float[][] values) {
        this.values = values;
        this.colCount = values.length;
        this.rowCount = values[0].length;
    }

    public float getValue(int col, int row) {
        return values[row][col];
    }

    public void setValue(int col, int row, float val) {
        values[row][col] = val;
    }

    public void fillWithIdentity() {
        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                if (col == row)
                    values[row][col] = 1;
                else
                    values[row][col] = 0;
            }
        }
    }

    /**
     * I think it's <br>
     * <u>this = mat x this</u>
     */
    public void multiply(Matrix mat) {
        Matrix dup = new Matrix(colCount, rowCount);
        copyTo(dup); // Need to copy since we're resetting the matrix as we go along
        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                values[row][col] = 0;
                for (int m = 0; m < rowCount; m++) {

                    float val1 = dup.getValue(col, m);
                    float val2 = mat.getValue(m, row);
                    values[row][col] += val1 * val2;
                }
            }
        }
    }

    public void growColumns(int newColumns) {
        // If we don't need to grow, no need to waste time doing so.
        if (newColumns == colCount) {
            return;
        }

        float[][] newValues = new float[rowCount][newColumns];
        
        float minCol = Math.min(colCount, newColumns);
        for (int col = 0; col < minCol; col++) {
            for (int row = 0; row < rowCount; row++) {
                newValues[row][col] = values[row][col];
            }
        }
        values = newValues;
        colCount = newColumns;
    }

    public void copyTo(Matrix mat) {
        mat.growColumns(colCount);
        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                float val = getValue(col, row);
                mat.setValue(col, row, val);
            }
        }

    }

    public Vector3f getColumnVector(int col) {
        return new Vector3f(values[0][col], values[1][col], values[2][col]);
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColCount() {
        return colCount;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[Matrix\n");
        for (int row = 0; row < rowCount; row++) {
            s.append("| ");
            for (int col = 0; col < colCount; col++) {
                s.append(String.format("%.02f ", values[row][col]));
            }
            s.append("|\n");
        }
        s.append("]");
        return s.toString();
    }

    // TESTING BASIC MATRIX MULTIPLICATION
    // public static void main(String[] args) {
    // // NOTE: Columns index comes first, so this is ROTATED-ish
    // float[][] vals1 = { {1, 2, 3},
    // {4, 5, 6},
    // {7, 8, 9}
    // };
    // float[][] vals2 = { {2, 4, 8},
    // {10, 12, 49},
    // {2, 4, 1}
    // };
    //
    // Matrix m1 = new Matrix(vals1);
    // Matrix m2 = new Matrix(vals2);
    // System.out.println("1: " + m1);
    // System.out.println("2: " + m2);
    //
    // m2.multiply(m1);
    //
    // System.out.println("2: " + m2);
    //
    // }
}
