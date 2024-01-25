package cn.bossfriday.cloudmusic.converter.utils;

import cn.bossfriday.cloudmusic.converter.commons.ServiceRuntimeException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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

    /**
     * getFileExtensionName
     *
     * @param fileName
     * @return
     */
    public static String getFileExtension(String fileName) {
        if (StringUtils.isEmpty(fileName) || fileName.indexOf(".") < 0) {
            return "";
        }

        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            return "";
        }

        return fileName.substring(index + 1).toLowerCase();
    }

    /**
     * writeFile
     *
     * @param data
     * @param file
     * @throws IOException
     */
    public static void writeFile(byte[] data, File file) throws IOException {
        if (ArrayUtils.isEmpty(data)) {
            throw new ServiceRuntimeException("data is empty!");
        }

        if (Objects.isNull(file)) {
            throw new ServiceRuntimeException("file is null!");
        }

        if (file.exists()) {
            throw new ServiceRuntimeException("file existed already! file: " + file.getAbsolutePath());
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(data);
        }
    }
}
