package cn.bossfriday.cloudmusic.converter.servcies;

import cn.bossfriday.cloudmusic.converter.commons.ServiceRuntimeException;
import cn.bossfriday.cloudmusic.converter.entities.CloudMusic;
import cn.bossfriday.cloudmusic.converter.entities.NcmMetaData;
import cn.bossfriday.cloudmusic.converter.entities.Rc4Key;
import cn.bossfriday.cloudmusic.converter.utils.CodecUtils;
import cn.bossfriday.cloudmusic.converter.utils.FileUtils;
import cn.bossfriday.cloudmusic.converter.utils.GsonUtils;
import cn.bossfriday.cloudmusic.converter.utils.RC4;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static cn.bossfriday.cloudmusic.converter.commons.Const.*;

/**
 * NcmCodecService
 *
 * @author chenx
 */
@Slf4j
public class NcmCodecService {

    private static final byte[] CORE_KEY = {0x68, 0x7A, 0x48, 0x52, 0x41, 0x6D, 0x73, 0x6F, 0x35, 0x6B, 0x49, 0x6E, 0x62, 0x61, 0x78, 0x57};
    private static final byte[] MUSIC_KEY = {0x23, 0x31, 0x34, 0x6C, 0x6A, 0x6B, 0x5F, 0x21, 0x5C, 0x5D, 0x26, 0x30, 0x55, 0x3C, 0x27, 0x28};
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    private NcmCodecService() {
        // just do nothing
    }

    /**
     * decode
     *
     * @param srcFilePath
     */
    public static NcmMetaData decode(String srcFilePath) {
        try {
            File srcFile = new File(srcFilePath);
            if (!srcFile.exists()) {
                log.warn("srcFile not existed! file: {} ", srcFilePath);
                return null;
            }

            String fileExtName = FileUtils.getFileExtension(srcFile.getName());
            if (!fileExtName.equalsIgnoreCase(VALUE_NCM)) {
                return null;
            }

            try (FileInputStream fis = new FileInputStream(srcFilePath)) {
                byte[] magicHeader = readBytes(fis, LENGTH_MAGIC_HEADER);
                int rc4KeyLength = getLength(fis);
                Rc4Key rc4Key = getRc4Key(fis, rc4KeyLength);
                int musicInfoLength = getLength(fis);
                CloudMusic cloudMusic = getCloudMusic(fis, musicInfoLength);
                byte[] crc = readBytes(fis, LENGTH_CRC);
                byte[] gap = readBytes(fis, LENGTH_GAP);
                int albumImageSize = getLength(fis);
                byte[] albumImage = getAlbumImage(fis, albumImageSize);
                byte[] audioData = getAudioData(fis, rc4Key.getKey());

                return NcmMetaData.builder()
                        .magicHeader(magicHeader)
                        .rc4KeyLength(rc4KeyLength)
                        .rc4Key(rc4Key)
                        .musicInfoLength(musicInfoLength)
                        .cloudMusic(cloudMusic)
                        .crc(crc)
                        .gap(gap)
                        .albumImageSize(albumImageSize)
                        .albumImage(albumImage)
                        .audioData(audioData)
                        .build();
            }
        } catch (Exception ex) {
            log.error("NcmCodecService.decode() error!", ex);
        }

        throw new ServiceRuntimeException("NcmCodecService.decode() failed!");
    }

    /**
     * readBytes
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readBytes(FileInputStream inputStream, int length) throws IOException {
        byte[] bytes = new byte[length];
        inputStream.read(bytes, 0, length);

        return bytes;
    }

    /**
     * getKeyLength
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static int getLength(FileInputStream inputStream) throws IOException {
        byte[] bytes = new byte[4];
        inputStream.read(bytes, 0, 4);

        return CodecUtils.getIntByLittleEndian(bytes);
    }

    /**
     * getRc4Key
     *
     * @param inputStream
     * @param keyLength
     * @return
     */
    private static Rc4Key getRc4Key(FileInputStream inputStream, int keyLength) throws IOException {
        if (keyLength <= 0) {
            throw new ServiceRuntimeException("keyLength <= 0!");
        }

        byte[] bytes = new byte[keyLength];
        inputStream.read(bytes, 0, keyLength);

        /**
         * 1. 按字节对0x64异或。
         * 2. AES解密，去除填充部分。
         * 3. rc4Key硬编码干扰盐字符串（17字节）：neteasecloudmusic
         * 4. RC4密钥: 剩余字节
         */
        for (int i = 0; i < keyLength; i++) {
            bytes[i] ^= 0x64;
        }

        bytes = decrypt(bytes, CORE_KEY, TRANSFORMATION, ALGORITHM);

        String hardCodeSalt = new String(bytes, 0, LENGTH_RC_4_KEY_HARD_CODE_SALT, StandardCharsets.UTF_8);
        byte[] keyData = new byte[bytes.length - LENGTH_RC_4_KEY_HARD_CODE_SALT];
        System.arraycopy(bytes, 17, keyData, 0, keyData.length);

        return Rc4Key.builder()
                .hardCodeSalt(hardCodeSalt)
                .key(keyData)
                .build();
    }

    /**
     * getCloudMusic
     *
     * @param inputStream
     * @param musicInfoLength
     * @return
     * @throws IOException
     */
    private static CloudMusic getCloudMusic(FileInputStream inputStream, int musicInfoLength) throws IOException {
        /**
         * 音乐信息：var bytes
         * 1. 按字节对0x63异或。
         * 2. 硬编码干扰盐字符串（22字节）：163 key(Don't modify):
         * 3. Base64进行解码。
         * 4. AES解密。
         * 5. 硬编码Json前缀干扰字符串(6字节)：music:
         * 6. Music对象Json字符串
         */
        byte[] bytes = new byte[musicInfoLength];
        inputStream.read(bytes, 0, musicInfoLength);
        for (int i = 0; i < musicInfoLength; i++) {
            bytes[i] ^= 0x63;
        }

        byte[] partition1 = new byte[LENGTH_CLOUD_MUSIC_HARD_CODE_SALT];
        System.arraycopy(bytes, 0, partition1, 0, partition1.length);
        String hardCodesalt = new String(partition1);

        byte[] partition2 = new byte[bytes.length - LENGTH_CLOUD_MUSIC_HARD_CODE_SALT];
        System.arraycopy(bytes, LENGTH_CLOUD_MUSIC_HARD_CODE_SALT, partition2, 0, partition2.length);
        partition2 = Base64.getDecoder().decode(partition2);
        partition2 = decrypt(partition2, MUSIC_KEY, TRANSFORMATION, ALGORITHM);

        String hardCodeJsonPrefix = new String(partition2, 0, LENGTH_CLOUD_MUSIC_HARD_CODE_JSON_PREFIX, StandardCharsets.UTF_8);

        String json = new String(partition2, LENGTH_CLOUD_MUSIC_HARD_CODE_JSON_PREFIX, partition2.length - LENGTH_CLOUD_MUSIC_HARD_CODE_JSON_PREFIX, StandardCharsets.UTF_8);
        CloudMusic cloudMusic = GsonUtils.fromJson(json, CloudMusic.class);
        cloudMusic.setHardCodeSalt(hardCodesalt);
        cloudMusic.setHardCodeJsonPrefix(hardCodeJsonPrefix);


        return cloudMusic;
    }

    /**
     * getAlbumImage
     *
     * @param inputStream
     * @param albumImageSize
     * @return
     * @throws IOException
     */
    private static byte[] getAlbumImage(FileInputStream inputStream, int albumImageSize) throws IOException {
        byte[] imageData = new byte[albumImageSize];
        inputStream.read(imageData, 0, albumImageSize);

        return imageData;
    }

    /**
     * getAudioData
     *
     * @param inputStream
     * @param key
     * @return
     */
    private static byte[] getAudioData(FileInputStream inputStream, byte[] key) throws IOException {
        RC4 cr4 = new RC4();
        cr4.keySchedule(key);
        byte[] buffer = new byte[0x8000];
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (int len; (len = inputStream.read(buffer)) > 0; ) {
                cr4.randomGenerate(buffer, len);
                outputStream.write(buffer, 0, len);
            }

            return outputStream.toByteArray();
        }
    }

    /**
     * decrypt
     *
     * @param data
     * @return
     */
    private static byte[] decrypt(byte[] data, byte[] key, String transformation, String algorithm) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            return cipher.doFinal(data);
        } catch (Exception ex) {
            log.error("NcmCodecService.decrypt() error!", ex);
        }

        throw new ServiceRuntimeException("NcmCodecService.decrypt() error!");
    }
}
