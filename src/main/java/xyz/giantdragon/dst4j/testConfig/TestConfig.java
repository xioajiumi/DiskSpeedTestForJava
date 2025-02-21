package xyz.giantdragon.dst4j.testConfig;

import xyz.giantdragon.dst4j.util.CommonUtil;

/**
 * 测试配置类，用于保存测试的配置信息
 * 多种构造函数，方便使用与快速克隆
 */
public class TestConfig {
    private String filePath;
    private int fileSizeMB;
    private int bufferSize;

    private long maxTestDuration;
    private int maxIONum;
    private int threadNum;
    private TestType testType;

    public static final int BEST_FILE_SIZE_MB = 1 * 1024;
    public static final long BEST_MAX_TEST_DURATION = (long) (30 * 1e9);
    public static final int BEST_SEQ_BS = 1024 * 1024;
    public static final int BEST_RAND_BS = 4 * 1024;
    public static final int BEST_SEQ_WRITE_TN = 3;
    public static final int BEST_SEQ_READ_TN = 1;
    public static final int BEST_RAND_WRITE_TN = 2;
    public static final int BEST_RAND_READ_TN = 1;
    public static final int BEST_RAND_WRITE_BS = 8 * 1024;

    public static final String TEST_FILE_FOLDER = "DiskSpeedTestForJava";
    public static final String TEST_FILE_NAME = "testFile.dat";

    public TestType getTestType() {
        return testType;
    }

    public TestConfig setTestType(TestType testType) {
        this.testType = testType;
        return this;
    }

    public enum TestType {
        SEQ_READ,   // 顺序读
        SEQ_WRITE, // 顺序写
        RAND_READ, // 随机读
        RAND_WRITE,// 随机写
    }

    /**
     * 这个用于复制一个配置对象，一般是为了避免线程安全问题
     */
    public TestConfig(TestConfig config) {
        this.setFilePath(config.getFilePath());
        this.setFileSizeMB(config.getFileSizeMB());
        this.setBufferSize(config.getBufferSize());
        this.setMaxTestDuration(config.getMaxTestDuration());
        this.setThreadNum(config.getThreadNum());
        this.setTestType(config.getTestType());
        this.maxIONum = config.getMaxIONum();
    }

    /**
     * 简化版的构造函数
     * 构建后会自动调用推荐设置配置
     */
    public TestConfig(String filePath, TestType testType) {
        this.setFilePath(filePath);
        this.setTestType(testType);
    }

    /**
     * 常规构造函数
     *
     * @param filePath        文件路径
     * @param fileSizeMB
     * @param bufferSize
     * @param testType
     * @param maxTestDuration
     * @param threadNum
     */
    public TestConfig(String filePath, int fileSizeMB, int bufferSize, TestConfig.TestType testType, long maxTestDuration, int threadNum) {
        this.setFilePath(filePath);
        this.setFileSizeMB(fileSizeMB);
        this.setBufferSize(bufferSize);
        this.setMaxTestDuration(maxTestDuration);
        this.setThreadNum(threadNum);
        this.setTestType(testType);
    }

    public String getFilePath() {
        return filePath;
    }

    public TestConfig setFilePath(String filePath) {
        this.filePath = CommonUtil.reviseTestPath(filePath);
        return this;
    }

    public int getFileSizeMB() {
        return fileSizeMB;
    }

    public TestConfig setFileSizeMB(int fileSizeMB) {
        this.fileSizeMB = fileSizeMB;
        if (this.bufferSize != 0) {
            this.maxIONum = CommonUtil.calculateMaxIONum(fileSizeMB, bufferSize);
        }
        return this;

    }

    public int getBufferSize() {
        return bufferSize;
    }

    public TestConfig setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        if (this.fileSizeMB != 0) {
            this.maxIONum = CommonUtil.calculateMaxIONum(fileSizeMB, bufferSize);
        }
        return this;
    }

    public long getMaxTestDuration() {
        return maxTestDuration;

    }

    public TestConfig setMaxTestDuration(long maxTestDuration) {
        this.maxTestDuration = maxTestDuration;
        return this;

    }


    public int getThreadNum() {
        return threadNum;
    }

    public TestConfig setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    public int getMaxIONum() {
        return maxIONum;
    }
}
