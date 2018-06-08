package animation;

public class ZeroKnob implements Knob {

    @Override
    public float getValue(int frame) {
        return 0;
    }

    @Override
    public boolean isActive(int frame) {
        return false;
    }

}
