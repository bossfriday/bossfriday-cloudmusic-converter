package cn.bossfriday.cloudmusic.converter.entities;

import lombok.Builder;
import lombok.Data;

/**
 * NcmMetaData
 *
 * @author chenx
 */
@Builder
@Data
public class NcmMetaData {

    /**
     * 10 bytes
     * 数据结构不明（忽略）
     */
    private byte[] magicHeader;

    /**
     * 4 bytes
     * AES128加密后的RC4密钥长度，字节是按小端排序。
     */
    private int keyLength;

    /**
     * var bytes（keyLength）
     * 用AES128加密后的RC4密钥。
     * 1. 先按字节对0x64进行异或。
     * 2. AES解密,去除填充部分。
     * 3. 去除最前面’neteasecloudmusic’17个字节，得到RC4密钥。
     */
    private byte[] rc4Key;

    /**
     * 4 bytes
     */
    private int musicInfoLength;

    /**
     * var bytes（musicInfoLength）
     * Json格式音乐信息数据。
     * 1. 按字节对0x63进行异或。
     * 2. 去除最前面22个字节。
     * 3. Base64进行解码。
     * 4. AES解密。
     * 6. 去除前面6个字节得到Json数据。
     */
    private MusicInfo musicInfo;
}
