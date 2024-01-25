package cn.bossfriday.cloudmusic.converter.utils;

/**
 * CodecUtils
 *
 * @author chenx
 */
public class CodecUtils {

    private CodecUtils() {
        // just do nothing
    }

    /**
     * getIntByLittleEndian（4字节小端排序转Int）
     *
     * @param bytes
     * @return
     */
    public static int getIntByLittleEndian(byte[] bytes) {
        int len = 0;
        len |= bytes[0] & 0xff;
        len |= (bytes[1] & 0xff) << 8;
        len |= (bytes[2] & 0xff) << 16;
        len |= (bytes[3] & 0xff) << 24;

        return len;
    }
}
