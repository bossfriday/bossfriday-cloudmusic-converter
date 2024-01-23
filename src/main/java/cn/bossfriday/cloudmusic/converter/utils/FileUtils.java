package cn.bossfriday.cloudmusic.converter.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FileUtils
 *
 * @author chenx
 */
public class FileUtils {

    private FileUtils() {
        // do nothing
    }

    /**
     * cleanDirectory
     *
     * @param dirPath
     */
    public static void cleanDirectory(String dirPath) throws IOException {
        File directory = new File(dirPath);

        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                cleanDirectory(file.getAbsolutePath());
            } else {
                Files.delete(file.toPath());
            }
        }
    }

    /**
     * mergePaths
     *
     * @param basePath
     * @param fileName
     * @return
     */
    public static String mergePaths(String basePath, String fileName) {
        Path combinedPath = Paths.get(basePath, fileName);

        return combinedPath.toString();
    }
}
