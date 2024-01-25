package cn.bossfriday.cloudmusic.converter;

import java.util.Scanner;

/**
 * Bootstrap
 *
 * @author chenx
 */
public class Bootstrap {

    /**
     * 网易云音乐转换器启动方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String srcDirPath = scanner("请输入网易云音乐文件夹路径");
        String destDirPath = scanner("请输入转换后目标文件夹路径");
        System.out.println(srcDirPath);
        System.out.println(destDirPath);
    }

    /**
     * scanner
     *
     * @param tip
     * @return
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(tip + "：");

        return scanner.nextLine();
    }
}
