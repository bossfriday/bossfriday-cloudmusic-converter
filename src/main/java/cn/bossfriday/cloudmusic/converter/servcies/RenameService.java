package cn.bossfriday.cloudmusic.converter.servcies;

import cn.bossfriday.cloudmusic.converter.entities.CloudMusicFileName;
import cn.bossfriday.cloudmusic.converter.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RenameService
 *
 * @author chenx
 */
@Slf4j
public class RenameService {

    private static final String REG_GROUP_ACTOR_NAME = "actorName";
    private static final String REG_GROUP_SONG_NAME = "songName";
    private static final String REG_GROUP_EXTENSION_NAME = "extName";
    private static final String REG_SONG_FILE_NAME = "(?<" + REG_GROUP_ACTOR_NAME + ">.*?)\\s\\-\\s(?<" + REG_GROUP_SONG_NAME + ">.*?)\\.(?<" + REG_GROUP_EXTENSION_NAME + ">\\w+\\z)";

    private RenameService() {
        // do nothing
    }

    /**
     * rename
     * <p>
     * 先把歌曲名提前，然后用Hash打散为一个1-999的前缀
     * 例如：蔡健雅 - 红色高跟鞋.mp3  -->   123-红色高跟鞋 - 蔡健雅.mp3
     *
     * @param srcDirPath
     * @param destDirPath
     * @throws IOException
     */
    public static void rename(String srcDirPath, String destDirPath) throws IOException {
        File destDir = new File(destDirPath);
        if (!destDir.exists()) {
            destDir.mkdir();
        } else {
            FileUtils.cleanDirectory(destDirPath);
        }

        File[] files = new File(srcDirPath).listFiles();
        for (File srcFile : files) {
            try {
                if (srcFile.isDirectory()) {
                    continue;
                }

                CloudMusicFileName cloudMusicFileName = getCloudMusicFile(srcFile.getName());
                String destFileName = cloudMusicFileName.getNewFileName();
                String destFilePath = FileUtils.mergePaths(destDirPath, destFileName);
                File destFile = new File(destFilePath);
                Files.copy(srcFile.toPath(), destFile.toPath());

                log.info("[" + srcFile.getName() + "] -> [" + destFileName + "] done.");
            } catch (Exception ex) {
                log.error("RenameService.rename() error! file: " + srcFile.getName(), ex);
            }
        }
    }

    /**
     * getCloudMusicFile
     *
     * @param fileName
     * @return
     */
    private static CloudMusicFileName getCloudMusicFile(String fileName) {
        Pattern pattern = Pattern.compile(REG_SONG_FILE_NAME);
        Matcher matcher = pattern.matcher(fileName);

        String actorName = "";
        String songName = "";
        String extName = "";
        while (matcher.find()) {
            actorName = matcher.group(REG_GROUP_ACTOR_NAME);
            songName = matcher.group(REG_GROUP_SONG_NAME);
            extName = matcher.group(REG_GROUP_EXTENSION_NAME);
        }

        return new CloudMusicFileName(actorName, songName, extName);
    }
}
