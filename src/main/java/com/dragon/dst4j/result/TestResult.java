package com.dragon.dst4j.result;

/**
 * 插件测试结果，重点关注吞吐速度和iops
 * 注意：这个不是worker的测试结果
 */
public class TestResult {
    private String filePath;
    private int bufferSize;
    private long ioCount;
    private long iops;

    private double dataThroughputMB;
    private double dataThroughputMBps;

    private double averageCostSec;

    public int getBufferSize() {
        return bufferSize;
    }

    public TestResult setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;

    }

    public long getIoCount() {
        return ioCount;
    }

    public TestResult setIoCount(long ioCount) {
        this.ioCount = ioCount;
        return this;

    }

    public long getiops() {
        return iops;
    }

    public TestResult setiops(long iops) {
        this.iops = iops;
        return this;

    }

    public double getDataThroughputMB() {
        return dataThroughputMB;
    }

    public TestResult setDataThroughputMB(double dataThroughputMB) {
        this.dataThroughputMB = dataThroughputMB;
        return this;

    }

    public double getdataThroughputMBps() {
        return dataThroughputMBps;
    }

    public TestResult setdataThroughputMBps(double dataThroughputMBps) {
        this.dataThroughputMBps = dataThroughputMBps;
        return this;

    }

    public double getAverageCostSec() {
        return averageCostSec;
    }

    public TestResult setAverageCostSec(double averageCostSec) {
        this.averageCostSec = averageCostSec;
        return this;
    }

    public TestResult setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }
}
