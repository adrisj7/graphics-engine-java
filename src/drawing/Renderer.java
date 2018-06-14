package drawing;

import java.util.Iterator;
import java.util.LinkedList;

import buffers.TriangleBuffer;
import math.Matrix;
import math.Vector2f;
import math.Vector3f;
import shading.Light;
import util.FileHandler;

public class Renderer {

    private Image image;

    private Color drawColor;

    private float[][] zbuffer;

    private LinkedList<Light> lights;

    public Renderer(Image image) {
        this.image = image;
        this.drawColor = new Color(0, 0, 0);

        zbuffer = new float[image.getWidth()][image.getHeight()];

        lights = new LinkedList<>();
    }

    public Light addLight(Vector3f direction, Color ambient, Color color, Vector3f areflect, Vector3f dreflect,
            Vector3f sreflect) {
        Light l = new Light(direction,ambient,color,areflect,dreflect,sreflect);
        lights.add(l);
        return l;
    }

    public void plot(int x, int y, float z) {
        // Flip the y axis
        y = image.getHeight() - y;

        if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
            if (zbuffer[x][y] <= z) {
                image.setColor(x, y, drawColor.getR(), drawColor.getG(), drawColor.getB());
                zbuffer[x][y] = z;
            }
        }
    }

    public void setColor(Color color) {
        this.drawColor = color;
    }

    public void refill() {
        for (int yy = 0; yy < image.getHeight(); yy++) {
            for (int xx = 0; xx < image.getWidth(); xx++) {
                plot(xx, yy, Float.MAX_VALUE); // NOTE: this MAX_VALUE is cleared after calling clearZBuffer().
            }
        }
        clearZBuffer();
    }

    public void clearLights() {
        lights.clear();
    }

    public void drawLine(int x0, int y0, float z0, int x1, int y1, float z1) {

        // swapping
        if (x0 > x1) {
            int swap;
            float zswap;

            swap = x0;
            x0 = x1;
            x1 = swap;
            swap = y0;
            y0 = y1;
            y1 = swap;

            zswap = z0;
            z0 = z1;
            z1 = zswap;
        }

        int dx = x1 - x0;
        int dy = y1 - y0;
        int a = dy; // TODO: You can remove a and b...
        int b = -dx;
        int d;

        float dz = z1 - z0;

        if (dy >= 0) { // Quadrant 1 and 3
            // Octant 1 and 5
            if (dy <= dx) {
                d = 2 * a + b;
                while (x0 <= x1) {
                    float zNow = z0 + dz * (float) (x1 - x0) / (float) dx;
                    plot(x0, y0, zNow);
                    if (d > 0) {
                        y0++;
                        d += 2 * b;
                    }
                    x0++;
                    d += 2 * a;
                }
                // Octant 2 and 6
            } else {
                d = a + 2 * b;
                while (y0 <= y1) {
                    float zNow = z0 + dz * (float) (y1 - y0) / (float) dy;
                    plot(x0, y0, zNow);
                    if (d < 0) {
                        x0++;
                        d += 2 * a;
                    }
                    y0++;
                    d += 2 * b;
                }

            }
            return;
        } else { // Quadrant 2 and 4
            // Octant 4 and 8
            if (-dy <= dx) {
                d = -2 * a + b;
                while (x0 <= x1) {
                    float zNow = z0 + dz * (float) (x1 - x0) / (float) dx;
                    plot(x0, y0, zNow);
                    if (d > 0) {
                        y0--;
                        d += 2 * b;
                    }
                    x0++;
                    d -= 2 * a;
                }
                // Octant 2 and 6
            } else {
                d = -a + 2 * b;
                while (y0 >= y1) {
                    float zNow = z0 + dz * (float) (y1 - y0) / (float) dy;
                    plot(x0, y0, zNow);
                    if (d < 0) {
                        x0++;
                        d -= 2 * a;
                    }
                    y0--;
                    d += 2 * b;
                }
            }
            return;
        }
    }

    public void drawScanLine(int x0, int y, float z0, int x1, float z1) {
        int xmin = Math.min(x0, x1);
        int xmax = Math.max(x0, x1);
        float dzdx = (z1 - z0) / (float)(x1 - x0);
        for(int xx = xmin; xx <= xmax; xx++) {
            float zz = z0 + (xx-xmin)*dzdx;
            plot(xx, y, zz);
        }
    }

    // But with a texture!
    // ypercent: What percentage of the way we've passed VERTICALLY through our triangle
    public void drawScanLine(int x0, int y, float z0, int x1, float z1, 
            float ypercent, boolean secondPass, Vector2f ttop, Vector2f tmid, Vector2f tbot, Image texture, Color lightColor) {

        // Should be constant but whatever
//        float lightMixFactor = 0.5f;
        
        int xmin = Math.min(x0, x1);
        int xmax = Math.max(x0, x1);
        
        // The left coordinate to "scanline" from
        // The right coordinate to "scanline" from
        Vector2f uvLeft;
        Vector2f uvRight;

        if (secondPass) {
            // Get an interpolation between the mid and bot
            uvLeft = Vector2f.getDelta(tmid, ttop).multiply(ypercent).add(tmid);
        } else {
            // Get an interpolation between the top and mid
            uvLeft = Vector2f.getDelta(tbot, tmid).multiply(ypercent).add(tbot);            
        }
        // Get an interpolation between the top and bot
        uvRight = Vector2f.getDelta(tbot, ttop).multiply(ypercent).add(tbot);
//        System.out.println(ypercent + ", " + Vector2f.getDelta(tmid, ttop));

        float dzdx = (z1 - z0) / (float)(x1 - x0);
        for(int xx = xmin; xx <= xmax; xx++) {
            float zz = z0 + (xx-xmin)*dzdx;

            // Get our texture coordinate!

            // The percentage of this scanline
            float xpercent = (float) (xx - xmin) / (float)(xmax - xmin);
            Vector2f uvCurrent = Vector2f.getDelta(uvLeft, uvRight).multiply(xpercent).add(uvLeft);
            float u = uvCurrent.getX();
            float v = uvCurrent.getY();
            if (u < 1.0 && v < 1.0 && u >= 0 && v >= 0) {
//                System.out.println(u*image.getWidth() + ", " + v*image.getHeight());
                Color c = texture.getColor((int) (u*texture.getWidth()), (int) (v*texture.getHeight()));
                // Multiplicative blending?
                float rfactor = (float)lightColor.getR() / 255f;
                float gfactor = (float)lightColor.getG() / 255f;
                float bfactor = (float)lightColor.getB() / 255f;
                setColor(new Color((int)(rfactor*c.getR()), (int)(gfactor*c.getG()), (int)(bfactor*c.getB())));
            } else {
//                Color c = new Color(255, 0, 0);
//                setColor(c);
            }
            plot(xx, y, zz);
        }
    }

    public void drawTriangleBufferMesh(TriangleBuffer buffer) {
        Matrix mat = buffer.getPoints();
        LinkedList<Vector2f> texcoords = buffer.getTextureCoordinates();
        Iterator<Vector2f> texcoordIterator = texcoords.iterator();
        // Should we use textures?
        boolean useTexture = !texcoords.isEmpty();

        int col;
        for (col = 0; col < mat.getColCount(); col += 3) {
            if (col >= mat.getColCount() || col + 2 >= mat.getColCount())
                break;

            // Our main vertices
            Vector3f p0 = mat.getColumnVector(col);
            Vector3f p1 = mat.getColumnVector(col + 1);
            Vector3f p2 = mat.getColumnVector(col + 2);
            // Our main texture vertices
            Vector2f t0 = null,
                     t1 = null,
                     t2 = null;
            if (useTexture) {
                t0 = texcoordIterator.hasNext() ? texcoordIterator.next() : new Vector2f(0,0);
                t1 = texcoordIterator.hasNext() ? texcoordIterator.next() : new Vector2f(0,0);
                t2 = texcoordIterator.hasNext() ? texcoordIterator.next() : new Vector2f(0,0);
            }

            Vector3f d1 = Vector3f.getDelta(p0, p1);
            Vector3f d2 = Vector3f.getDelta(p0, p2);

            Vector3f normal = Vector3f.getCrossProduct(d1, d2);
            normal.normalize();
            Vector3f view = new Vector3f(0, 0, 1);
            float normalDotView = Vector3f.getDotProduct(normal, view);

            if (normalDotView <= 0) {
                continue; // Skip this triangle
            }

            Vector3f lightSum = new Vector3f(0,0,0);
            for(Light l : lights) {
                lightSum.add(l.getSurfaceLightingVec(normal, view));
            }
            Color lightColor = new Color( (int) lightSum.getX(), (int) lightSum.getY(), (int) lightSum.getZ() );
            //Color p = new Color((int) (255 * Math.random()), (int) (255 * Math.random()), (int) (255 * Math.random()));
            setColor(lightColor);

            // SCANLINE

            // Pick top, bottom and middle points
            Vector3f ptop, pmid, pbot; // y
            Vector2f ttop, tmid, tbot;
            if (p0.getY() > p1.getY() && p0.getY() > p2.getY()) {
                ptop = p0;
                ttop = t0;
                if (p1.getY() > p2.getY()) {
                    pmid = p1;
                    pbot = p2;
                    tmid = t1;
                    tbot = t2;
                } else {
                    pmid = p2;
                    pbot = p1;
                    tmid = t2;
                    tbot = t1;
                }
            } else if (p1.getY() > p0.getY() && p1.getY() > p2.getY()) {
                ptop = p1;
                ttop = t1;
                if (p2.getY() > p0.getY()) {
                    pmid = p2;
                    pbot = p0;
                    tmid = t2;
                    tbot = t0;
                } else {
                    pmid = p0;
                    pbot = p2;
                    tmid = t0;
                    tbot = t2;
                }
            } else {
                ptop = p2;
                ttop = t2;
                if (p1.getY() > p0.getY()) {
                    pmid = p1;
                    pbot = p0;
                    tmid = t1;
                    tbot = t0;
                } else {
                    pmid = p0;
                    pbot = p1;
                    tmid = t0;
                    tbot = t1;
                }
            }

            double x0, x1, z0, z1;
            double dx0, dx1, dz0, dz1;
            int y;
            int distance0, distance1, distance2;
            x0 = pbot.getX();
            x1 = pbot.getX();
            z0 = pbot.getZ();
            z1 = pbot.getZ();
            y = (int) (pbot.getY());

            distance0 = (int) (ptop.getY()) - y;
            distance1 = (int) (pmid.getY()) - y;
            distance2 = (int) (ptop.getY()) - (int) (pmid.getY());

            dx0 = distance0 > 0 ? (ptop.getX() - pbot.getX()) / distance0 : 0;
            dx1 = distance1 > 0 ? (pmid.getX() - pbot.getX()) / distance1 : 0;
            dz0 = distance0 > 0 ? (ptop.getZ() - pbot.getZ()) / distance0 : 0;
            dz1 = distance1 > 0 ? (pmid.getZ() - pbot.getZ()) / distance1 : 0;

            boolean flip = false;
            while (y <= (int) ptop.getY()) {
                if (useTexture) {
                    // A scanline, with texture support!
                    float ypercent;
                    if (flip) {
                        ypercent = ((float) (y) - pmid.getY()) / (float)(distance2);

                    } else {
                        ypercent = ((float) (y) - pbot.getY()) / (float)(distance1);
                    }
                   drawScanLine((int) x0, (int) y, (float) z0, (int) x1, (float) z1, ypercent, flip, ttop, tmid, tbot, buffer.getTexture(), lightColor);
                } else {
                    // Just a regular scanline
                    drawScanLine((int) x0, (int) y, (float) z0, (int) x1, (float) z1);
                }
                x0 += dx0;
                x1 += dx1;
                z0 += dz0;
                z1 += dz1;
                y++;

                if (!flip && y >= (int) (pmid.getY())) {
                    flip = true;
                    dx1 = distance2 > 0 ? (ptop.getX() - pmid.getX()) / distance2 : 0;
                    dz1 = distance2 > 0 ? (ptop.getY() - pmid.getY()) / distance2 : 0;
                    x1 = pmid.getX();
                    z1 = pmid.getZ();
                }
            }
        }
    }

    private void clearZBuffer() {
        for(int xx = 0; xx < zbuffer.length; xx++) {
            for(int yy = 0; yy < zbuffer[xx].length; yy++) {
                zbuffer[xx][yy] = -1 * Float.MAX_VALUE;
            }
        }
    }

    // LINE TEST
    // public static void main(String[] args) {
    // Image img = new Image(300, 300);
    // Renderer r = new Renderer(img);
    // r.setColor(new Color(255, 0, 0));
    // for(double theta = 0; theta <= 2*Math.PI; theta+= 2d*Math.PI/ 10d) {
    // int x0 = 150;
    // int y0 = 150;
    // int x1 = x0 + (int)(100d * Math.cos(theta));
    // int y1 = y0 + (int)(100d * Math.sin(theta));
    // r.drawLine(x0, y0, 0, x1, y1, 0);
    // }
    //
    // img.writeToPPM("images/linetest.ppm");
    // }

    // TRIANGLEBUFFER RENDERING TEST
    public static void main(String[] args) {

        Image img = new Image(300, 300);
        Renderer r = new Renderer(img);
        r.setColor(new Color(255, 255, 255));
        r.refill();

        // These aren't really adjusted...
        Vector3f areflect = new Vector3f(0.8f, 0.8f, 0.8f);
        Vector3f dreflect = new Vector3f(0.5f, 0.5f, 0.5f);
        Vector3f sreflect = new Vector3f(0.7f, 0.7f, 0.7f);
        r.addLight(new Vector3f(0, 1, 1), new Color(100, 100, 100), new Color(255, 255, 255), areflect, dreflect, sreflect);

        TriangleBuffer buff = new TriangleBuffer();
        buff.translate(100, 300, 100);
        buff.rotateY(10);
        buff.rotateX(10);
        buff.addBoxTextured(-50, -50, -50, 50, 50, 100, FileHandler.readImage("res/test.png"));
//          buff.addBox(-50, -50, -50, 50, 50, 100);

//        buff.translate(150, 150, 0);
//        buff.transformPush();
//            buff.rotateY(90);
//            buff.rotateX(20);
//            buff.addTorus(0, 0, 0, 30, 100);
//        buff.transformPop();
//        buff.rotateY(30);
//        buff.rotateX(-20);
//        buff.addSphere(0, 0, 0, 60);

        r.drawTriangleBufferMesh(buff);
        img.writeToPPM("images/test.ppm");

    }

}
