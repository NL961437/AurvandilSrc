package Aurvandil.Util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static File[] getUncommittedFiles() {
        File folder = new File(Paths.get(System.getProperty("user.dir")).resolve("UncommittedFiles").toString());
        return folder.listFiles();
    }

    public static void moveFile(File file) {
        try {
            String destination = Paths.get(System.getProperty("user.dir")).resolve("CommittedFiles") + "/";
            Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destination + file.getName()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
