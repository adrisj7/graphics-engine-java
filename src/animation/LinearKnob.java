package animation;

public class LinearKnob implements Knob {

    private int startFrame, endFrame;
    private float startVal, endVal;

    public LinearKnob(int startFrame, int endFrame, float startVal, float endVal) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.startVal = startVal;
        this.endVal = endVal;
    }

    @Override
    public float getValue(int frame) {
        return startVal + (endVal - startVal) * ((float)frame / (endFrame - startFrame));
    }

    @Override
    public boolean isActive(int frame) {
        return (startFrame <= frame && frame <= endFrame);
    }
    
    
}
