package image;

public class Color {

	private static final int RED_MASK = 0xFF0000, GREEN_MASK = 0xFF00, BLUE_MASK = 0xFF;

	private int color;

	public Color(int r, int g, int b) {
		color = (r << 16) + (g << 8) + b;
	}

	public int getR() {
		return (color & RED_MASK) >> 16;
	}

	public int getG() {
		return (color & GREEN_MASK) >> 8;		
	}

	public int getB() {
		return (color & BLUE_MASK);
	}

}
