# 背景
网易云音乐下载的歌曲格式多为ncm，这种格式除了用网易云音乐App其他播放器无法播放。于是想寻求其格式转换的工具，搜索了下确实有相关工具，不过很多山寨工具需要收费。然后又试图寻求其他方式，经过一番调研发现这玩意其实早已经被人扒光了到处裸奔（我估摸着这个事情就是千防万防，家贼难防。因为基于目前ncm文件的加密方式，要根据其加密字节流去反推加密过程好像有点难，不知道搞破解的同学怎么看这个事情）。
例如：《网易云VIP音乐NCM文件转MP3,C语言版本》https://blog.csdn.net/y123456wydhckd/article/details/128368486；
于是索性自己动手搞个JAVA版的，然后顺带加上个重命名的功能。网易云音乐文件命名规则是：歌手名开头，这样下载后默认的排序就是按照歌手名，一直连着听某一个咖咖的歌谁都会觉得无趣（我一般都是开车的时候用U盘听歌），重命名的做法是：增加一个按文件名进行murmur哈希的前缀值，从而起到一个哈希散开的效果。
# 1. 网易云音乐文件加密概要
1. 自定义了一个紧凑的数据结构，例如：10字节头、4字节密钥长度等；
2. 自定义数据结构中的关键信息又进行自定义加密及编码，例如：音频数据加密密钥、音乐信息等；
3. 自定义数据结构中加入了一些无用硬编码干扰盐字符串，例如：neteasecloudmusic、163 key(Don't modify):等；
4. NCM格式的加密文件中除了音频数据外还包含音乐附属信息，例如：专辑，歌手，歌名，图片等；
5. 音频数据的加密密钥位于加密文件的自身之中，加密算法为性能相对AES较好的非标RC4加密算法；

从以上几点可以看出其实网易云音乐NCM文件的加密其实基于都是一些经典老套路：

> * **通过自定义数据结构的方式将密钥置于加密文件自身之中，同时加入一些无关的盐做为干扰（网易云音乐有点low，都是一些硬编码的玩意）；**
> * **出于加解密效率考虑，选择了以异或运算为主的替换类加密算法（可以把异或运算理解为不进位的二进制加法，效率自然超高，对相同的东西进行二次异或即可得到原始值，因此基于异或的解密只需再来一次即可）；**
> * **对于密钥的管理则选择安全性较高的高级加密算法（网易云音乐选择的AES，一般来说对于密钥的管理推荐使用非对称的RSA或者ECC及其变种）；**

# 2. RC4简介
RC4 加密算法的核心思想是通过在初始状态下生成一个伪随机的字节流，然后将明文与这个字节流进行异或运算，从而得到密文。具体来说，RC4 算法包括两个主要步骤：
**1. 密钥调度算法（Key Scheduling Algorithm，KSA）：**
* 使用初始状态的 S-box（置换盒：Substitution Box）。
* S-box 是一个包含 0 到 255 的数字的数组，初始状态下是有序的。
* 根据给定的密钥，通过对 S-box 的多次置换和交换来打乱其顺序，生成一个混乱的 S-box。

**2. 伪随机数生成算法（Pseudo-Random Generation Algorithm，PRGA）：**
* 使用经过打乱的 S-box。
* 利用 S-box 生成一个伪随机的字节流，这个字节流被用作密钥流。
* 将明文与密钥流进行异或运算，得到密文。

在 Java 中，RC4 算法的实现通常并不包含在标准的 Java 加密库中，一个常用的 Java 加密库是 Bouncy Castle，Bouncy Castle 提供了丰富的密码学算法支持，包括 RC4。如果要使用Bouncy Castle进行标准RC4加解密，java11可以添加依赖（bouncycastle jdk版本太多了）：
```xml
<dependency>
	<groupId>org.bouncycastle</groupId>
	<artifactId>bcprov-jdk15to18</artifactId>
	<version>1.76</version>
</dependency>
```

需要补充说明的是：网易云音乐对于音频数据加密的RC4是一个非标的RC4，标准RC4中S-box（置换盒）是一个字节的数组，因此这里只能自己实现：

```java
package cn.bossfriday.cloudmusic.converter.cipher;

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
     * RC4密钥调度（RC4-KSA：Key Scheduling Algorithm）
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
     * RC4伪随机数生成（RC4-PRGA：Pseudo-Random Generation Algorithm）
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
```

# 3. 源码及运行

> **[https://github.com/bossfriday/bossfriday-cloudmusic-converter](https://github.com/bossfriday/bossfriday-cloudmusic-converter)**

为了方便大家本地调试，在项目中的ncm目录中已经放了3个ncm歌曲，大家可以按照以下方式进行本地调试：
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/17cf3e000304448d82461f86a2619acf.png)


