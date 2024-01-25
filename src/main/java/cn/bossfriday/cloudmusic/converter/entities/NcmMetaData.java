package cn.bossfriday.cloudmusic.converter.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NcmMetaData
 *
 * @author chenx
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NcmMetaData {

    /**
     * 头信息：10 bytes
     * 数据结构不明（忽略）
     */
    private byte[] magicHeader;

    /**
     * RC4密钥长度：4 bytes（字节小端排序）
     */
    private int rc4KeyLength;

    /**
     * RC4密钥：var bytes
     * 1. 按字节对0x64异或。
     * 2. AES解密，去除填充部分。
     * 3. rc4Key硬编码干扰盐字符串（17字节）：neteasecloudmusic
     * 4. RC4密钥: 剩余字节
     */
    private Rc4Key rc4Key;

    /**
     * 音乐信息长度：4 bytes（字节小端排序）
     */
    private int musicInfoLength;

    /**
     * 音乐信息：var bytes
     * 1. 按字节对0x63异或。
     * 2. 硬编码干扰盐字符串（22字节）：163 key(Don't modify):
     * 3. Base64进行解码。
     * 4. AES解密。
     * 5. 硬编码Json前缀干扰字符串(6字节)：music:
     * 6. Music对象Json字符串
     */
    private CloudMusic cloudMusic;

    /**
     * CRC: 4 bytes
     */
    private byte[] crc;

    /**
     * Gap: 5 bytes
     */
    private byte[] gap;

    /**
     * 专辑图片大小：4 bytes（字节小端排序）
     */
    private int albumImageSize;

    /**
     * 专辑图片：var bytes
     */
    private byte[] albumImage;

    /**
     * 音频数据
     */
    private byte[] audioData;
}
