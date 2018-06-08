package animation;

import java.io.File;
import java.io.IOException;

import image.Image;

public class Animation {

    // Delay between frames, by default.
    private static final int FRAME_DELAY_DEFAULT = 3;

    private Image frames[];

    private int frameDelay;

    public Animation(int frameCount, int frameDelay) {
        frames = new Image[frameCount];
        this.frameDelay = frameDelay;
    }

    public Animation(int frameCount) {
        this(frameCount, FRAME_DELAY_DEFAULT);
    }

    public void setFrame(int frame, Image img) {
        frames[frame] = img.clone();
    }

    public void saveToGIF(String name) {
        String copy = name; // This is legal in java.
        if (copy.toLowerCase().indexOf(".gif") == -1) {
            System.err.println("[Animation.java] INVALID IMAGE NAME: " + name + ", MUST END IN \".gif\"");
        }

        String subName = name.substring(Math.max(name.lastIndexOf("/"), 0), name.lastIndexOf(".gif"));

        System.out.println("ANIMATION NAME: " + name + ", SUB: " + subName);

        // Make a directory for our images
        (new File("images/" + subName)).mkdir();

        // Make frames as images/"name"### where "name" is the subName (ex the subname
        // of "dir/potato.gif" is "potato")
        for (int frame = 0; frame < frames.length; frame++) {
            Image img = frames[frame];
            String frameFileName = String.format("images/%s/%s%03d.ppm", subName, subName, frame);
            img.writeToPPM(frameFileName);
            System.out.printf("Frame %d: %s\n", frame, frameFileName);
        }

        // Are we on windows, or unix?
        String osName = System.getProperty("os.name");
        boolean isWindows = osName.toLowerCase().indexOf("windows") != -1;

        try {
            if (isWindows) {
                Runtime.getRuntime().exec(
                    new String[] { 
                            "magick", 
                            "convert", 
                            "-delay", 
                            Integer.toString(frameDelay), 
                            "images/" + subName + "/" + subName + "*", // Every subimage / frame
                            name // The image we're saving to
                    }
                );
            } else {
                // TODO: They're both literally the same thing, minus one line. Figure this out pls
                Runtime.getRuntime().exec(
                    new String[] { 
                        "convert", 
                        "-delay", 
                        Integer.toString(frameDelay), "images/" + subName + "/" + subName + "*", // Every subimage / frame
                        name // The image we're saving to
                    }
                );
            }
        } catch (IOException e) {
            System.out.println("Uh oh, there was a problem converting all of these images to a gif!");
            e.printStackTrace();
        }
        
        System.out.println("Animation saved to " + name);
    }

    public int getFrameCount() {
        return frames.length;
    }

    // ANIMATION TEST: (*gasp* it worked on the first try! Well, if you don't count
    // implementing image cloning.)
    /*
     * public static void main(String[] args) { Animation a = new Animation(4);
     * Image img = new Image(100, 100); Renderer r = new Renderer(img);
     * 
     * for(int i = 0; i < a.getFrameCount(); i++) { r.setColor(new Color(255, 255,
     * 255)); r.refill(); r.setColor(new Color(255, 0, 0));
     * 
     * int height = 20 + 15 * i; r.drawLine(0, height, 0, 100, height, 0);
     * 
     * a.setFrame(i, img); }
     * 
     * a.saveToGIF("images/test.gif"); }
     */
}
