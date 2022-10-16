package cn.liguohao.demo.demofilechuckuploadbackend;

import java.io.File;

/**
 * @author guohao
 * @date 2022/10/17
 */
public interface FileConstants {
    String UPLOAD = "upload";
    String uploadDirPath = SystemVarKit.getCurrentAppDirPath() + File.separator + UPLOAD;

    static void initDir() {
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

    }
}
