package Aurvandil.Util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    /**
     * Get list of files from the "UncommittedFiles" folder
     * @return list of files
     */
    public static File[] getUncommittedFiles() {
        File folder = new File(Paths.get(System.getProperty("user.dir")).resolve("UncommittedFiles").toString());
        return folder.listFiles();
    }

    /**
     * Move a file to the "CommittedFiles" folder
     * @param file file to be moved
     */
    public static void moveFile(File file) {
        try {
            String destination = Paths.get(System.getProperty("user.dir")).resolve("CommittedFiles") + "/";
            Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destination + file.getName()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
