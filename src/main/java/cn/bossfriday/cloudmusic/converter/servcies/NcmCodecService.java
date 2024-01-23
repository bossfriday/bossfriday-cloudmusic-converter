package cn.bossfriday.cloudmusic.converter.servcies;

import cn.bossfriday.cloudmusic.converter.commons.ServiceRuntimeException;
import cn.bossfriday.cloudmusic.converter.entities.NcmMetaData;
import cn.bossfriday.cloudmusic.converter.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static cn.bossfriday.cloudmusic.converter.commons.Const.VALUE_NCM;

/**
 * NcmCodecService
 *
 * @author chenx
 */
@Slf4j
public class NcmCodecService {

    /**
     * 加密CR4密钥的密钥
     */
    private static final byte[] CORE_KEY = {0x68, 0x7A, 0x48, 0x52, 0x41, 0x6D, 0x73, 0x6F, 0x35, 0x6B, 0x49, 0x6E, 0x62, 0x61, 0x78, 0x57};

    /**
     * 加密MATA信息的密钥
     */
    private static final byte[] MATA_KEY = {0x23, 0x31, 0x34, 0x6C, 0x6A, 0x6B, 0x5F, 0x21, 0x5C, 0x5D, 0x26, 0x30, 0x55, 0x3C, 0x27, 0x28};

    /**
     * 转换(加密算法/加密模式/填充模式)
     */
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES";

    private NcmCodecService() {
        // just do nothing
    }

    /**
     * ncm2mp3
     *
     * @param srcFilePath
     */
    public static NcmMetaData ncm2mp3(String srcFilePath) {
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
                byte[] magicHeader = getMagicHeader(fis);
                int keyLength = getKeyLength(fis);
                byte[] rc4Key = getRc4Key(fis, keyLength);

                System.out.println(keyLength);
            }
        } catch (Exception ex) {
            log.error("Ncm2Mp3Service.ncm2mp3() error!", ex);
        }

        return null;
    }

    /**
     * getMagicHeader
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] getMagicHeader(FileInputStream inputStream) throws IOException {
        byte[] bytes = new byte[10];
        inputStream.read(bytes, 0, 10);

        return bytes;
    }

    /**
     * getKeyLength
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static int getKeyLength(FileInputStream inputStream) throws IOException {
        byte[] bytes = new byte[4];
        inputStream.read(bytes, 0, 4);

        return getKeyLength(bytes);
    }

    /**
     * getRc4Key
     *
     * @param inputStream
     * @param keyLength
     * @return
     */
    private static byte[] getRc4Key(FileInputStream inputStream, int keyLength) throws IOException {
        if (keyLength <= 0) {
            throw new ServiceRuntimeException("keyLength <= 0!");
        }

        byte[] bytes = new byte[keyLength];
        inputStream.read(bytes, 0, keyLength);

        /**
         * 1. 先按字节对0x64进行异或。
         * 2. AES解密,去除填充部分。
         * 3. 去除最前面’neteasecloudmusic’17个字节，得到RC4密钥。
         */
        for (int i = 0; i < keyLength; i++) {
            bytes[i] ^= 0x64;
        }

        bytes = decrypt(bytes, CORE_KEY, TRANSFORMATION, ALGORITHM);
        byte[] key = new byte[bytes.length - 17];
        System.arraycopy(bytes, 17, key, 0, key.length);

        return key;
    }

    /**
     * getKeyLength
     *
     * @param bytes
     * @return
     */
    public static int getKeyLength(byte[] bytes) {
        int len = 0;
        len |= bytes[0] & 0xff;
        len |= (bytes[1] & 0xff) << 8;
        len |= (bytes[2] & 0xff) << 16;
        len |= (bytes[3] & 0xff) << 24;

        return len;
    }

    /**
     * decrypt
     *
     * @param data
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key, String transformation, String algorithm) {
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

    public static void main(String[] args) {
        NcmCodecService.ncm2mp3("D:\\CloudMusic\\VipSongsDownload\\周传雄 - 黄昏.ncm");
    }
}
