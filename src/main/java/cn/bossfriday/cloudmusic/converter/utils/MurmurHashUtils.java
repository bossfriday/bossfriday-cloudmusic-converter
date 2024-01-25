package cn.bossfriday.cloudmusic.converter.utils;

import cn.bossfriday.cloudmusic.converter.commons.ServiceRuntimeException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * MurmurHashUtils
 *
 * @author chenx
 */
public class MurmurHashUtils {

    private MurmurHashUtils() {
        // do nothing
    }

    /**
     * hash64
     *
     * @param key
     * @return
     */
    public static long hash64(String key) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(key)) {
            throw new ServiceRuntimeException("input key is null or empty!");
        }

        return hash64(key.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * hash64
     *
     * @param key
     * @return
     */
    public static long hash64(byte[] key) {
        return hash64A(key, 0x1234ABCD);
    }

    /**
     * hash32
     *
     * @param key
     * @return
     */
    public static int hash32(String key) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(key)) {
            throw new ServiceRuntimeException("input key is null or empty!");
        }

        return hash(key.getBytes(StandardCharsets.UTF_8), 0x1234ABCD);
    }

    /**
     * hash32
     *
     * @param key
     * @return
     */
    public static int hash32(byte[] key) {
        return hash(key, 0x1234ABCD);
    }

    /**
     * Hashes bytes in an array.
     *
     * @param data The bytes to hash.
     * @param seed The seed for the hash.
     * @return The 32 bit hash of the bytes in question.
     */
    public static int hash(byte[] data, int seed) {
        return hash(ByteBuffer.wrap(data), seed);
    }

    /**
     * Hashes bytes in part of an array.
     *
     * @param data   The data to hash.
     * @param offset Where to start munging.
     * @param length How many bytes to process.
     * @param seed   The seed to start with.
     * @return The 32-bit hash of the data in question.
     */
    public static int hash(byte[] data, int offset, int length, int seed) {
        return hash(ByteBuffer.wrap(data, offset, length), seed);
    }

    /**
     * Hashes the bytes in a buffer from the current position to the limit.
     *
     * @param buf  The bytes to hash.
     * @param seed The seed for the hash.
     * @return The 32 bit murmur hash of the bytes in the buffer.
     */
    public static int hash(ByteBuffer buf, int seed) {
        // save byte order for later restoration
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int m = 0x5bd1e995;
        int r = 24;

        int h = seed ^ buf.remaining();

        int k;
        while (buf.remaining() >= Integer.BYTES) {
            k = buf.getInt();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h *= m;
            h ^= k;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getInt();
            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        buf.order(byteOrder);
        return h;
    }

    public static long hash64A(byte[] data, int seed) {
        return hash64A(ByteBuffer.wrap(data), seed);
    }

    public static long hash64A(byte[] data, int offset, int length, int seed) {
        return hash64A(ByteBuffer.wrap(data, offset, length), seed);
    }

    public static long hash64A(ByteBuffer buf, int seed) {
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= Long.BYTES) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);

        return h;
    }
}
