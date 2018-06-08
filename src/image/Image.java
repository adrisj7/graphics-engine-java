package image;

import util.FileHandler;

public class Image {

    private Color[][] grid;

    public Image(int width, int height) {
        grid = new Color[width][height];
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                grid[xx][yy] = new Color(0, 0, 0);
            }
        }
    }

    public int getWidth() {
        return grid.length;
    }

    public int getHeight() {
        return grid[0].length;
    }

    public Color getColor(int x, int y) {
        return grid[x][y];
    }

    public void setColor(int x, int y, int r, int g, int b) {
        grid[x][y] = new Color(r, g, b);
    }

    public void writeToPPM(String fname) {
        StringBuilder text = new StringBuilder("P3\n");
        text.append(getWidth()).append(" ").append(getHeight()).append("\n");
        text.append(255).append("\n");

        for (int yy = 0; yy < getHeight(); yy++) {
            for (int xx = 0; xx < getWidth(); xx++) {
                Color c = grid[xx][yy];
                text.append(String.format("%d %d %d ", c.getR(), c.getG(), c.getB()));
            }
            text.append("\n");
        }

        FileHandler.writeText(text.toString(), fname);
        System.out.println("Wrote image to " + fname + ".");
    }

    public Image clone() {
        Image clone = new Image(getWidth(), getHeight());
        for (int yy = 0; yy < getHeight(); yy++) {
            for (int xx = 0; xx < getWidth(); xx++) {
                Color c = getColor(xx, yy);
                clone.setColor(xx, yy, c.getR(), c.getG(), c.getB());
            }
        }

        return clone;
    }

    // TESTING BASIC IMAGE FUNCTIONALITY
    // public static void main(String[] args) {
    // Image img = new Image(300, 300);
    //
    // for(int xx = 10; xx < 150; xx++) {
    // for(int yy = 60; yy < 200; yy++) {
    // img.setColor(xx, yy, 255, 255, 0);
    // }
    // }
    //
    // img.writeToPPM("images/test.ppm");
    // }
}
