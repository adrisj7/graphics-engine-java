package parser;

import java.util.HashMap;

import animation.Animation;
import animation.Knob;
import buffers.TriangleBuffer;
import image.Color;
import image.Image;
import image.Renderer;

/**
 * This holds everything we use to make the engine work. Basically, initialize
 * this and grab its objects, and you can do everything.
 * 
 */
public class Engine {
    private Image image;
    private Renderer renderer;
    private TriangleBuffer buffer;
    private Animation animation;

    // This should be removed... weird to have this here imo
    private Color ambient;
    private Color background;

    private String basename = "";

    private HashMap<String, Knob> knobs;

    public Engine() {
        image = null;
        renderer = null;
        ambient = null;
        background = null;
        animation = null;
        basename = "";

        buffer = new TriangleBuffer();
        knobs = new HashMap<>();
    }

    public void setBaseName(String basename) {
        this.basename = basename;
    }

    public void setImage(int width, int height) {
        image = new Image(width, height);
        renderer = new Renderer(image);
    }

    public void fillBackground() {
        renderer.setColor(background);
        renderer.refill();
    }

    public void setAmbient(Color ambient) {
        this.ambient = ambient;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public void setFrames(int frameCount) {
        animation = new Animation(frameCount);
    }

    public void addKnob(String name, Knob knob) {
        knobs.put(name, knob);
    }

    public HashMap<String, Knob> getKnobMap() {
        return knobs;
    }

    public Knob getKnob(String name) {
        return knobs.get(name);
    }

    public Image getImage() {
        return image;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public String getBaseName() {
        return basename;
    }

    public TriangleBuffer getBuffer() {
        return buffer;
    }

    public Color getAmbient() {
        return ambient;
    }

    public Color getBackground() {
        return background;
    }

    public Animation getAnimation() {
        return animation;
    }

}
