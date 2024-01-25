package cn.bossfriday.cloudmusic.converter.entities;

import cn.bossfriday.cloudmusic.converter.utils.MurmurHashUtils;
import lombok.*;

import static cn.bossfriday.cloudmusic.converter.commons.Const.HASH_PREFIX_BEGIN;
import static cn.bossfriday.cloudmusic.converter.commons.Const.HASH_PREFIX_END;

/**
 * CloudMusicFileName
 *
 * @author chenx
 */
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudMusicFileName {

    private String actorName;

    private String musicName;

    private String extName;

    /**
     * getNewFileName
     *
     * @return
     */
    public String getNewFileName() {
        String result = this.musicName + " - " + this.actorName + "." + this.extName;
        int prefix = convertToRange(MurmurHashUtils.hash32(result), HASH_PREFIX_BEGIN, HASH_PREFIX_END);

        return prefix + " - " + result;
    }

    /**
     * convertToRange
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    private static int convertToRange(int value, int min, int max) {
        int range = max - min + 1;

        return ((value % range) + range) % range + min;
    }
}
