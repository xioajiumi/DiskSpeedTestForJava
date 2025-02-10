package com.dragon.dst4j.util;


import com.dragon.dst4j.testConfig.TestConfig;
import com.dragon.dst4j.result.WorkerResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonUtil {
    /**
     * 构建一个空的结果，用于避免空指针异常
     */
    public static WorkerResult buildEmptyResult() {
        return new WorkerResult(0, 0);
    }

    ;

    /**
     * 提前维护好一个随机位置的池子，避免在运行时每次都生成随机位置
     * 已经处理好了边界问题，直接用就行
     */
    public static long[] buildPositionsPool(int poolSize, TestConfig config) {
        long[] positions = new long[poolSize];
        long fileSizeBytes = (long) config.getFileSizeMB() * 1014 * 1024 - config.getBufferSize();
        for (int i = 0; i < poolSize; i++) {
            positions[i] = (long) (Math.random() * fileSizeBytes); // 随机位置
        }
        return positions;
    }

    /**
     * 对传进来的文件路径进行简单的修正，比如分隔符问题等等
     */
    public static String reviseTestPath(String filePath) {
        String path = "";
        if (filePath.endsWith(File.separator + TestConfig.TEST_FILE_FOLDER + File.separator)) {
            path = filePath;
        } else if (filePath.endsWith(File.separator + TestConfig.TEST_FILE_FOLDER)) {
            path = filePath + File.separator;
        } else if (filePath.endsWith(File.separator)) {
            path = filePath + TestConfig.TEST_FILE_FOLDER + File.separator;
        } else {
            path = filePath + File.separator + TestConfig.TEST_FILE_FOLDER + File.separator;
        }
        return path;
    }

    ;

    public static int calculateMaxIONum(int fileSizeMB, int bufferSize) {
        return (int) ((long) fileSizeMB * 1024 * 1024 / bufferSize);
    }

    /**
     * 清空路径下的所有文件，并不删除文件夹本身
     */
    public static void clearFolderFiles(String folderPath) {
        Path path = Paths.get(folderPath);
        try {
            Files.list(path).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空路径下的包含某个字符串的文件，并不删除文件夹本身
     */
    public static void clearFolderFilesContains(String folderPath, String subString) {
        Path path = Paths.get(folderPath);
        try {
            Files.list(path).forEach(p -> {
                try {
                    if (p.getFileName().toString().contains(subString)) {
                        Files.delete(p);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
