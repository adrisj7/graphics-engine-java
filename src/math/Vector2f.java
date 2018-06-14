package math;

public class Vector2f {
    private float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f add(Vector2f v) {
        this.x += v.x;
        this.y += v.y;

        return this;
    }

    public Vector2f multiply(float c) {
        this.x *= c;
        this.y *= c;

        return this;
    }

    public Vector2f multiplyComponents(Vector2f vec) {
        this.x *= vec.x;
        this.y *= vec.y;

        return this;
    }

    public float getMagnitudeSquared() {
        return x * x + y * y;
    }

    public float getMagnitude() {
        return (float) Math.sqrt(getMagnitudeSquared());
    }

    public Vector2f normalize() {
        float m = getMagnitude();
        if (m != 0) {
            x /= m;
            y /= m;
        }

        return this;
    }

    public Vector2f makeCopy() {
        return new Vector2f(x, y);
    }

    @Override
    public String toString() {
        return "[Vector (" + x + ", " + y + ")]";
    }

    // STATICS

    public static float getDotProduct(Vector2f v1, Vector2f v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public static Vector2f getDelta(Vector2f v1, Vector2f v2) {
        return new Vector2f(v2.x - v1.x, v2.y - v1.y);
    }

    // GETTERS AND SETTERS

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}
