package shading;

import image.Color;
import math.Vector3f;

public class Light {
    // FILLME!
    private Vector3f direction;

    // TODO: Figure out better naming
    private Color ambient;
    private Vector3f color; // So we can use vector operations

    // ambient, diffuse, and specular reflection
    private Vector3f areflect, dreflect, sreflect;

    public Light(Vector3f direction, Color ambient, Color color, Vector3f areflect, Vector3f dreflect,
            Vector3f sreflect) {
        this.direction = direction;
        this.ambient = ambient;
        this.areflect = areflect;
        this.dreflect = dreflect;
        this.sreflect = sreflect;

        this.color = new Vector3f(color.getR(), color.getG(), color.getB());

        direction.normalize();
    }

    private Vector3f calculateAmbient() {
        Vector3f result = new Vector3f(ambient.getR(), ambient.getG(), ambient.getB());
        result.multiplyComponents(areflect);

        return result;
    }

    private Vector3f calculateDiffuse(Vector3f normal) {
        Vector3f result = dreflect.makeCopy();
        result.multiplyComponents(color);

        float brightness = Vector3f.getDotProduct(normal, direction);

        if (brightness < 0)
            brightness = 0;

        result.multiply(brightness);

        return result;
    }

    private Vector3f calculateSpecular(Vector3f normal, Vector3f view) {
        Vector3f result = sreflect.makeCopy();
        result.multiplyComponents(color);

        float normDirDot = Vector3f.getDotProduct(normal, direction);
        if (normDirDot < 0)
            normDirDot = 0;

        Vector3f innerTerm = Vector3f.getDelta(normal.multiply(2.0f).multiply(normDirDot), direction);

        float secondBeforeExponentDot = Vector3f.getDotProduct(innerTerm, view);
        if (secondBeforeExponentDot < 0)
            secondBeforeExponentDot = 0;

        result.multiply((float) Math.pow(secondBeforeExponentDot, 12.0));

        return result;
    }

    public Vector3f getSurfaceLightingVec(Vector3f normal, Vector3f view) {
        Vector3f ambient = calculateAmbient();
        Vector3f diffuse = calculateDiffuse(normal);
        Vector3f specular = calculateSpecular(normal, view);

        Vector3f sum = ambient.add(diffuse).add(specular);

        return sum;
    }

    public Color getSurfaceLighting(Vector3f normal, Vector3f view) {
        Vector3f sum = getSurfaceLightingVec(normal, view);
        return new Color((int) sum.getX(), (int) sum.getY(), (int) sum.getZ());
    }
}
