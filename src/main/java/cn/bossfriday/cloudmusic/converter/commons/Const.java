package cn.bossfriday.cloudmusic.converter.commons;

/**
 * Const
 *
 * @author chenx
 */
public class Const {

    private Const() {
        // do nothing
    }

    public static final int HASH_PREFIX_BEGIN = 1;
    public static final int HASH_PREFIX_END = 999;

    public static final int LENGTH_MAGIC_HEADER = 10;
    public static final int LENGTH_CRC = 4;
    public static final int LENGTH_GAP = 5;
    public static final int LENGTH_CLOUD_MUSIC_HARD_CODE_SALT = 22;
    public static final int LENGTH_RC_4_KEY_HARD_CODE_SALT = 17;
    public static final int LENGTH_CLOUD_MUSIC_HARD_CODE_JSON_PREFIX = 6;

    public static final String VALUE_NCM = "ncm";
}
