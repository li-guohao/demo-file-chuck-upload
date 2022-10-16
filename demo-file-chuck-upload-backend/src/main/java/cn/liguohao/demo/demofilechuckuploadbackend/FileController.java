package cn.liguohao.demo.demofilechuckuploadbackend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(FileController.class);
    private final Environment environment;
    private Map<String, String> offsetTempChunkFileDirMap = new ConcurrentHashMap<>();

    public FileController(Environment environment) {
        this.environment = environment;
    }

    @PostMapping("/process")
    @ResponseBody
    public String getUploadUniqueLocation(HttpServletRequest request) {
        String uploadLength = request.getHeader("Upload-Length");
        LOGGER.info("uploadLength: {}", uploadLength);
        String location = UUID.randomUUID().toString().replaceAll("-", "");
        return location;
    }

    @PatchMapping("/patch/{unique}")
    @ResponseBody
    public void receiveChunkFile(@PathVariable("unique") String unique,
                                 HttpServletRequest request) throws IOException {
        LOGGER.info("unique: {}", unique);
        String uploadLength = request.getHeader("Upload-Length");
        LOGGER.info("uploadLength: {}", uploadLength);
        String uploadOffset = request.getHeader("Upload-Offset");
        LOGGER.info("uploadOffset: {}", uploadOffset);
        String uploadName = request.getHeader("Upload-Name");
        LOGGER.info("uploadName: {}", uploadName);

        File tempChunkFileCacheDir =
            new File(SystemVarKit.getOsCacheDirPath() + File.separator + unique);
        if (!tempChunkFileCacheDir.exists()) {
            tempChunkFileCacheDir.mkdirs();
            LOGGER.info("create temp dir: {}", tempChunkFileCacheDir);
        }

        ServletInputStream inputStream = request.getInputStream();
        byte[] bytes = inputStream.readAllBytes();
        Assert.notNull(bytes, "file bytes must not be null");
//        Assert.isTrue(bytes.length > 0, "file bytes must >0");

        Long offset = Long.parseLong(uploadOffset) + bytes.length;
        File uploadedChunkCacheFile = new File(tempChunkFileCacheDir + File.separator + offset);
        Files.write(Path.of(uploadedChunkCacheFile.toURI()), bytes);
        LOGGER.info("upload chunk[{}] to path: {}", uploadOffset,
            uploadedChunkCacheFile.getAbsolutePath());

        if (offset == Long.parseLong(uploadLength)) {
            String postfix = uploadName.substring(uploadName.lastIndexOf(".") + 1);
            meringTempChunkFile(unique, postfix);
        }
    }

    private void meringTempChunkFile(String unique, String postfix) throws IOException {
        LOGGER.info("All chunks upload has finish, will start merging files");
        String url = "";

        File targetFile =
            new File(FileConstants.uploadDirPath + File.separator + unique + "." + postfix);
        LOGGER.info("upload target file path: {}", targetFile.getAbsolutePath());

        String chunkFileDirPath = SystemVarKit.getOsCacheDirPath() + File.separator + unique;
        File chunkFileDir = new File(chunkFileDirPath);
        File[] files = chunkFileDir.listFiles();
        List<File> chunkFileList = Arrays.asList(files);
        // PS: 这里需要根据文件名(偏移量)升序, 不然合并的文件分片内容的顺序不正常
        Collections.sort(chunkFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long o1Offset = Long.parseLong(o1.getName());
                long o2Offset = Long.parseLong(o2.getName());
                if(o1Offset < o2Offset){
                    return -1;
                }else if(o1Offset > o2Offset){
                    return 1;
                }
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
        int targetFileWriteOffset = 0;
        for (File chunkFile : chunkFileList) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
                 FileInputStream fileInputStream = new FileInputStream(chunkFile);) {
                randomAccessFile.seek(targetFileWriteOffset);
                byte[] bytes = new byte[fileInputStream.available()];
                int read = fileInputStream.read(bytes);
                randomAccessFile.write(bytes);
                targetFileWriteOffset += read;
                LOGGER.info("[{}] current merge targetFileWriteOffset: {}", chunkFile.getName(), targetFileWriteOffset);
            }
        }

        url = path2url(targetFile.getAbsolutePath());

        LOGGER.info("Merging all chunk files success, url: {}", url);
    }

//    @PostMapping("/process")
//    @ResponseBody
//    public String uploadFileProcess(@RequestParam("file") MultipartFile multipartFile)
//        throws IOException {
//        Assert.notNull(multipartFile, "'multipartFile' must not be null");
//
//        String originalFilename = multipartFile.getOriginalFilename();
//        byte[] bytes = multipartFile.getBytes();
//        Assert.notNull(bytes, "file bytes must not be null");
//        Assert.isTrue(bytes.length > 0, "file bytes must >0");
//
//        FileConstants.initDir();
//        File uploadedFile = new File(FileConstants.uploadDirPath + File.separator + originalFilename);
//        Files.write(Path.of(uploadedFile.toURI()), bytes);
//
//        return path2url(uploadedFile.getAbsolutePath());
//    }


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
        File uploadedFile =
            new File(FileConstants.uploadDirPath + File.separator + originalFilename);
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
