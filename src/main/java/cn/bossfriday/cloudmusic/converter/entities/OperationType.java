package cn.bossfriday.cloudmusic.converter.entities;

import lombok.Getter;

/**
 * OperationType
 *
 * @author chenx
 */
@Getter
public enum OperationType {

    /**
     * Convert
     */
    CONVERT("1", "格式转换"),

    /**
     * Rename
     */
    RENAME("2", "重命名");

    private String code;
    private String desc;

    OperationType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * getByCode
     *
     * @param code
     * @return
     */
    public static OperationType getByCode(String code) {
        for (OperationType entry : OperationType.values()) {
            if (entry.getCode().equalsIgnoreCase(code)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * getTipMessage
     *
     * @return
     */
    public static String getTipMessage() {
        StringBuilder sb = new StringBuilder();
        for (OperationType entry : OperationType.values()) {
            sb.append(entry.getDesc() + ": " + entry.getCode());
            sb.append("\t");
        }

        return sb.toString();
    }
}
