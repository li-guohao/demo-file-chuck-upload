package cn.liguohao.demo.demofilechuckuploadbackend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author guohao
 * @date 2022/10/17
 */
@Controller
@RequestMapping("/file")
public class FileController {
    private final Environment environment;

    public FileController(Environment environment) {
        this.environment = environment;
    }

    @PutMapping("/data")
    @ResponseBody
    public String uploadFileData(@RequestParam("file") MultipartFile multipartFile)
        throws IOException {
        Assert.notNull(multipartFile, "'multipartFile' must not be null");

        String originalFilename = multipartFile.getOriginalFilename();
        byte[] bytes = multipartFile.getBytes();
        Assert.notNull(bytes, "file bytes must not be null");
        Assert.isTrue(bytes.length > 0, "file bytes must >0");

        FileConstants.initDir();
        File uploadedFile = new File(FileConstants.uploadDirPath + File.separator + originalFilename);
        Files.write(Path.of(uploadedFile.toURI()), bytes);

        return path2url(uploadedFile.getAbsolutePath());
    }

    private String path2url(String path) {
        String url = "";
        String currentAppDirPath = SystemVarKit.getCurrentAppDirPath();
        String ipAddress = SystemVarKit.getIPAddress();
        String port = environment.getProperty("local.server.port");
        String baseUrl = "http://" + ipAddress + ":" + port;
        url = path.replace(currentAppDirPath, baseUrl);
        // 如果是ntfs目录URL，则需要替换下 \ 为 /
        if (url.indexOf("\\") > 0) {
            url = url.replace("\\", "/");
        }
        return url;
    }
}
