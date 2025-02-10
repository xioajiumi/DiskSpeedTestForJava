package com.dragon.dst4j.result;

/**
 * 这个对象用于保存测试的结果，其中ioCount为IO次数，durationSec为测试持续时间（秒/Second）
 * 支持两种构造方法，一种是传入纳秒（long），一种是传入秒（double），区别依据是类型
 */
public class WorkerResult {

    // 测试期间进行的io操作次数，可以关联bufferSize来算出来操作的数据量
    private long ioCount;

    // 测试持续时间，单位是秒，用于计算速度
    private double durationSec;

    public WorkerResult(double durationSec, long ioCount) {
        this.ioCount = ioCount;
        this.durationSec = durationSec;
    }

    public WorkerResult(long durationNano, long ioCount) {
        this.ioCount = ioCount;
        this.setDurationSec(durationNano);
    }

    public long getIoCount() {
        return ioCount;
    }

    public void setIoCount(long ioCount) {
        this.ioCount = ioCount;
    }

    public double getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(long durationNano) {
        this.durationSec = (double) durationNano / 1e9;
    }

    public void setDurationSec(double durationSec) {
        this.durationSec = durationSec;
    }
}
