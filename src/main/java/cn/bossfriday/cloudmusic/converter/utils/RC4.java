package cn.bossfriday.cloudmusic.converter.utils;

/**
 * RC4
 * <p>
 * RC4 加密算法的核心思想是通过在初始状态下生成一个伪随机的字节流，然后将明文与这个字节流进行异或运算，从而得到密文。
 * 具体来说，RC4 算法包括两个主要步骤：
 * 1. 密钥调度算法（Key Scheduling Algorithm，KSA）：
 * * 使用初始状态的 S-box（置换盒: Substitution Box）。
 * * S-box 是一个包含 0 到 255 的数字的数组，初始状态下是有序的。
 * * 根据给定的密钥，通过对 S-box 的多次置换和交换来打乱其顺序，生成一个混乱的 S-box。
 * <p>
 * 2. 伪随机数生成算法（Pseudo-Random Generation Algorithm，PRGA）：
 * * 使用经过打乱的 S-box。
 * * 利用 S-box 生成一个伪随机的字节流，这个字节流被用作密钥流。
 * * 将明文与密钥流进行异或运算，得到密文。
 *
 * @author chenx
 */
public class RC4 {

    private final int[] sBox = new int[256];

    /**
     * RC4 KSA
     *
     * @param key
     */
    public void keySchedule(byte[] key) {
        int len = key.length;
        for (int i = 0; i < 256; i++) {
            this.sBox[i] = i;
        }

        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + this.sBox[i] + key[i % len]) & 0xff;
            int swap = this.sBox[i];

            this.sBox[i] = this.sBox[j];
            this.sBox[j] = swap;
        }
    }

    /**
     * RC4 PRGA
     *
     * @param data
     * @param length
     */
    public void randomGenerate(byte[] data, int length) {
        int i = 0;
        int j = 0;
        for (int k = 0; k < length; k++) {
            i = (k + 1) & 0xff;
            j = (this.sBox[i] + i) & 0xff;

            data[k] ^= this.sBox[(this.sBox[i] + this.sBox[j]) & 0xff];
        }
    }
}
