package util;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import drawing.Image;

public class FileHandler {

    public static void writeText(String text, String fname) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            System.err.println("You have a problem writing your file. Nothing I can really do here");
            e.printStackTrace();
        }
    }

    public static String readText(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.err.println("Reading from " + path + " failed!");
            e.printStackTrace();
        }
        return "";
    }

    public static Image readImage(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            return Image.toImage(img);
        } catch(IOException e) {
            System.err.println("Failed to read image at path " + path + ".");
        }
        return new Image(0,0);
    }
}
