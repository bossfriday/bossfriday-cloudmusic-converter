package cn.bossfriday.cloudmusic.converter.entities;

import lombok.*;

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
public class MusicInfo {

    /**
     * 音乐名
     */
    public String musicName;

    /**
     * 艺术家
     */
    public String[][] artist;

    /**
     * 专辑
     */
    public String album;

    /**
     * 格式
     */
    public String format;
}
