package xyz.giantdragon.dst4j.testWorker;

import xyz.giantdragon.dst4j.testConfig.TestConfig;
import xyz.giantdragon.dst4j.result.WorkerResult;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * 测试对象的抽象类，定义了测试对象的基本行为，实现逻辑在BaseDiskTestWorker中
 */
public abstract class AbstractDiskTestWorker implements Callable<WorkerResult> {
    public TestConfig config;
    public boolean useRandData = false;

    // 禁止无参构造
    private AbstractDiskTestWorker() {
    }

    public AbstractDiskTestWorker(TestConfig config) {
        this.config = config;
    }

    public static Random random = new Random();

    public abstract void createTestFile(TestConfig config, Path filePath, boolean useRandData) throws IOException;

    public abstract AsynchronousFileChannel buildChannel(TestConfig config) throws IOException;

    protected abstract long doSeqRead(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime) throws ExecutionException, InterruptedException;

    protected abstract long doSeqWrite(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime) throws ExecutionException, InterruptedException;

    protected abstract long doRandRead(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime,long[] positionPool) throws ExecutionException, InterruptedException;

    protected abstract long doRandWrite(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime,long[] positionPool) throws ExecutionException, InterruptedException;

}
