package me.matthewe.suggestion.io.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Matthew E on 5/1/2018.
 */
public class FileUtils {
    public static String readFileToString(File file) {
        Path filePath = file.toPath();
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(bytes, StandardCharsets.UTF_8); // You can specify the charset if it's not UTF-8
    }

    public static boolean createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean writeStringToFile(File file, String string) {
        Path filePath = file.toPath();
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8); // You can specify the charset if needed
        try {
            Files.write(filePath, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true; // Return true if the write was successful
    }
}