package cn.bossfriday.cloudmusic.converter;

import cn.bossfriday.cloudmusic.converter.entities.OperationType;
import cn.bossfriday.cloudmusic.converter.servcies.ConvertService;
import cn.bossfriday.cloudmusic.converter.servcies.RenameService;

import java.io.IOException;
import java.util.Objects;
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
    public static void main(String[] args) throws IOException {
        String operationCode = scanner("操作类型：\r\n格式转换（ncm格式转换）输入：1\r\n歌曲重命名（哈希打散同歌手歌曲）输入：2");
        OperationType operationType = OperationType.getByCode(operationCode);
        if (Objects.isNull(operationType)) {
            System.out.println("不支持的操作类型码!");
            return;
        }

        String srcDirPath = scanner("源文件夹路径（可选：ncm）");
        String destDirPath = scanner("目标文件夹路径（可选：ncm\\out）");

        if (operationType.equals(OperationType.CONVERT)) {
            ConvertService.convert(srcDirPath, destDirPath);
            return;
        }

        if (operationType.equals(OperationType.RENAME)) {
            RenameService.rename(srcDirPath, destDirPath);
            return;
        }
    }

    /**
     * scanner
     *
     * @param tip
     * @return
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(tip);

        return scanner.nextLine();
    }
}
