package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}
