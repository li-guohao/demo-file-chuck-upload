package cn.liguohao.demo.demofilechuckuploadbackend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author guohao
 * @date 2022/10/17
 */
public class FileKit {

    public static Set<String> splitChunk(String fromFilePath, int chunkSize) {
        Set<String> chunkFilePaths = new HashSet<>();
        File fromFile = new File(fromFilePath);
        int chunkCount = (int) Math.ceil(fromFile.length() / (double) chunkSize);
        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(chunkCount, chunkCount * 3, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(chunkCount * 2));

        for (int i = 0; i < chunkCount; i++) {
            String partFileName = fromFile.getName() + "."
            + UUID.randomUUID().toString().replaceAll("-", "") + ".part";
            final int finalI = i;
            executor.execute(() -> {
                File toPartFile = new File(partFileName);
                try (RandomAccessFile randomAccessFile = new RandomAccessFile(fromFile, "r");
                     FileOutputStream fileOutputStream = new FileOutputStream(toPartFile)){
                    randomAccessFile.seek((long) finalI * chunkSize);
                    byte[] bytes = new byte[chunkSize];
                    int read = randomAccessFile.read(bytes);
                    fileOutputStream.write(bytes, 0, read);
                }  catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            chunkFilePaths.add(partFileName);
        }

        return chunkFilePaths;
    }

    public void mergeChunkFiles(String chunkDirPath, int chunkSize, String mergeFileName) {
        File chunkDir = new File(chunkDirPath);
        File[] files = chunkDir.listFiles();

        for (File file : files) {

        }
    }
}
