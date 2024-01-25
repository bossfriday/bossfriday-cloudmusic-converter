package cn.bossfriday.cloudmusic.converter.entities;

import com.google.gson.annotations.Expose;
import lombok.*;

import java.util.List;

/**
 * CloudMusic
 *
 * @author chenx
 */
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudMusic {

    @Expose(serialize = false, deserialize = false)
    private String hardCodeSalt;

    @Expose(serialize = false, deserialize = false)
    private String hardCodeJsonPrefix;

    private String musicId;

    private String musicName;

    private List<String[]> artist;

    private String albumId;

    private String album;

    private String albumPicDocId;

    private String albumPic;

    private int bitrate;

    private String mp3DocId;

    private long duration;

    private String mvId;

    private List<String> alias;

    private List<String> transNames;

    private String format;

    private int fee;

    private Privilege privilege;
}
