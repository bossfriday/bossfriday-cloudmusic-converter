package cn.bossfriday.cloudmusic.converter.servcies;

import cn.bossfriday.cloudmusic.converter.commons.ServiceRuntimeException;
import cn.bossfriday.cloudmusic.converter.entities.CloudMusic;
import cn.bossfriday.cloudmusic.converter.entities.CloudMusicFileName;
import cn.bossfriday.cloudmusic.converter.entities.NcmMetaData;
import cn.bossfriday.cloudmusic.converter.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * ConvertService
 *
 * @author chenx
 */
@Slf4j
public class ConvertService {

    private ConvertService() {
        // do nothing
    }

    /**
     * convert
     *
     * @param srcDirPath
     * @param destDirPath
     */
    public static void convert(String srcDirPath, String destDirPath) throws IOException {
        File destDir = new File(destDirPath);
        if (!destDir.exists()) {
            destDir.mkdir();
        } else {
            FileUtils.cleanDirectory(destDirPath);
        }

        File[] files = new File(srcDirPath).listFiles();
        for (File srcFile : files) {
            try {
                NcmMetaData ncmMetaData = NcmCodecService.decode(srcFile);
                if (Objects.isNull(ncmMetaData)) {
                    continue;
                }

                String fileName = getFileName(ncmMetaData.getCloudMusic());
                String filePath = FileUtils.mergePaths(destDirPath, fileName);
                FileUtils.writeFile(ncmMetaData.getAudioData(), new File(filePath));
                log.info("[" + srcFile.getName() + "] -> [" + fileName + "] done.");
            } catch (Exception ex) {
                log.error("ConvertService.convert() error!", ex);
            }
        }
    }

    /**
     * getFileName
     *
     * @param cloudMusic
     * @return
     */
    private static String getFileName(CloudMusic cloudMusic) {
        if (Objects.isNull(cloudMusic)) {
            throw new ServiceRuntimeException("cloudMusic is null!");
        }

        String musicName = cloudMusic.getMusicName();
        String actorName = getArtName(cloudMusic.getArtist());
        String extName = cloudMusic.getFormat();
        CloudMusicFileName cloudMusicFileName = new CloudMusicFileName(actorName, musicName, extName);

        return cloudMusicFileName.getNewFileName();
    }

    /**
     * getArtName
     *
     * @param artist
     * @return
     */
    private static String getArtName(List<String[]> artist) {
        if (CollectionUtils.isEmpty(artist)) {
            return "";
        }

        String artName = "";
        for (String[] entry : artist) {
            artName += entry[0] + " ";
        }

        return artName;
    }
}
