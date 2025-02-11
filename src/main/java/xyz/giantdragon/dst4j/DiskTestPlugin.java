package xyz.giantdragon.dst4j;

import xyz.giantdragon.dst4j.result.TestResult;
import xyz.giantdragon.dst4j.result.WorkerResult;
import xyz.giantdragon.dst4j.testConfig.TestConfig;
import xyz.giantdragon.dst4j.testWorker.AbstractDiskTestWorker;
import xyz.giantdragon.dst4j.util.CommonUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 测试程序的入口
 */
public class DiskTestPlugin {
    public static final String ID = UUID.randomUUID().toString().substring(0, 6);

    /**
     * 测试的核心逻辑，注意异常处理
     */
    public TestResult runTest(TestConfig config, ExecutorService ioPool, Class<? extends AbstractDiskTestWorker> workerClass) throws IOException, InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 创建测试文件夹
        String folderPath = config.getFilePath();
        Path path = Paths.get(folderPath);
        Files.createDirectories(path);
        CommonUtil.clearFolderFilesContains(folderPath, ID);
        // 提交测试任务
        List<Future<WorkerResult>> futures = new ArrayList<>();
        List<WorkerResult> workerResults = new ArrayList<>();
        for (int i = 0; i < config.getThreadNum(); i++) {
            AbstractDiskTestWorker worker = workerClass.getDeclaredConstructor(TestConfig.class).newInstance(config);
            Future<WorkerResult> submit = ioPool.submit(worker);
            futures.add(submit);
        }
        // 收集任务结果
        for (Future<WorkerResult> future : futures) {
            try {
                workerResults.add(future.get());  // 获取每个线程的I/O操作次数
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        TestResult testResult = buildTestResult(workerResults, config);
        CommonUtil.clearFolderFilesContains(folderPath, ID);
        return testResult;

    }

    /**
     * 构建测试结果，方便进一步分析
     */
    public TestResult buildTestResult(List<WorkerResult> workerResults, TestConfig config) {
        TestResult res = new TestResult();
        long ioCount = 0;
        double totalSecs = 0;
        // 统计结果
        for (WorkerResult workerResult : workerResults) {
            ioCount += workerResult.getIoCount();
            totalSecs += workerResult.getDurationSec();
        }
        double averageCost = totalSecs / workerResults.size();
        double dataOperatedMB = ioCount * (config.getBufferSize() / 1024.0 / 1024.0);
        double throughputMBps = dataOperatedMB / averageCost;
        long iops = (long) (ioCount / averageCost);
        res.setBufferSize(config.getBufferSize());
        res.setIoCount(ioCount);
        res.setiops(iops);
        res.setAverageCostSec(averageCost);
        res.setDataThroughputMB(dataOperatedMB);
        res.setdataThroughputMBps(throughputMBps);
        res.setFilePath(config.getFilePath());
        return res;
    }


    /**
     * 这个是针对读写测试类型来进行推荐配置设置的
     */
    public static TestConfig setRecommendRWConfig(TestConfig.TestType testType, TestConfig config) {
        config.setTestType(testType);
        switch (testType) {
            case SEQ_READ:
                config.setThreadNum(TestConfig.BEST_SEQ_READ_TN);
                config.setBufferSize(TestConfig.BEST_SEQ_BS);
                break;
            case SEQ_WRITE:
                config.setThreadNum(TestConfig.BEST_SEQ_WRITE_TN);
                config.setBufferSize(TestConfig.BEST_SEQ_BS);
                break;
            case RAND_READ:
                config.setThreadNum(TestConfig.BEST_RAND_READ_TN);
                config.setBufferSize(TestConfig.BEST_RAND_BS);
                break;
            case RAND_WRITE:
                config.setThreadNum(TestConfig.BEST_RAND_WRITE_TN);
                config.setBufferSize(TestConfig.BEST_RAND_WRITE_BS);
                break;
        }

        return config;
    }

    /**
     * 自动根据读写类型进行推荐配置设置
     */
    public static TestConfig setRecommendRWConfig(TestConfig config) {
        if (config.getTestType() != null) {
            return setRecommendRWConfig(config.getTestType(), config);
        } else {
            throw new RuntimeException("Please set test type first!");
        }
    }

    ;

    /**
     * 这个是进行一般化的设置推荐
     */
    protected static TestConfig setRecommendCommonConfig(TestConfig config) {
        config.setMaxTestDuration(TestConfig.BEST_MAX_TEST_DURATION);
        config.setFileSizeMB(TestConfig.BEST_FILE_SIZE_MB);
        return config;
    }

    ;


}
