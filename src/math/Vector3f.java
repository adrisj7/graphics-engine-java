package math;

public class Vector3f {
    private float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f add(Vector3f v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;

        return this;
    }

    public Vector3f multiply(float c) {
        this.x *= c;
        this.y *= c;
        this.z *= c;

        return this;
    }

    public Vector3f multiplyComponents(Vector3f vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;

        return this;
    }

    public float getMagnitudeSquared() {
        return x * x + y * y + z * z;
    }

    public float getMagnitude() {
        return (float) Math.sqrt(getMagnitudeSquared());
    }

    public Vector3f normalize() {
        float m = getMagnitude();
        if (m != 0) {
            x /= m;
            y /= m;
            z /= m;
        }

        return this;
    }

    public Matrix toMatrixColumn() {
        Matrix m = new Matrix(1);
        m.setValue(0, 0, x);
        m.setValue(0, 1, y);
        m.setValue(0, 2, z);
        m.setValue(0, 3, 1); // IMPORTANT!

        return m;
    }

    public Vector3f makeCopy() {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "[Vector (" + x + ", " + y + ", " + z + ")]";
    }

    // STATICS

    public static float getDotProduct(Vector3f v1, Vector3f v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Vector3f getCrossProduct(Vector3f v1, Vector3f v2) {
        return new Vector3f(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }

    public static Vector3f getDelta(Vector3f v1, Vector3f v2) {
        return new Vector3f(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
    }

    // GETTERS AND SETTERS

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

}
