package xyz.giantdragon.dst4j.testWorker;

import xyz.giantdragon.dst4j.util.CommonUtil;
import xyz.giantdragon.dst4j.DiskTestPlugin;
import xyz.giantdragon.dst4j.testConfig.TestConfig;
import xyz.giantdragon.dst4j.result.WorkerResult;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;

/**
 * 提供一个基础的测试对象，实现了测试对象的基本行为
 */
public class BaseDiskTestWorker extends AbstractDiskTestWorker {

    public BaseDiskTestWorker(TestConfig config) {
        super(config);
    }

    /**
     * 用最快的速度来创建一个测试文件，内容为随机字节，这个主要是给读测试用的
     */
    @Override
    public void createTestFile(TestConfig config, Path filePath,boolean useRandData) throws IOException {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            long ioCount = 0;
            final int bufferSize = 4*1024*1024;
            long maxIONum = config.getFileSizeMB()/(bufferSize/1024/1024);
            ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
            // 使用随机数进行填充，速度很慢
            if (useRandData){
                while (ioCount < maxIONum) {
                    final long position = (long) ioCount * bufferSize;
                    buffer.clear();
                    if (useRandData){
                        // 填充随机字节
                        byte[] randomData = new byte[buffer.remaining()];
                        random.nextBytes(randomData);
                        buffer.put(randomData);
                        buffer.flip();
                    }
                    channel.write(buffer, position);
                    ioCount++;
                }
            }else {
                // 直接填充null数据
                while (ioCount < maxIONum) {
                    final long position = ioCount * bufferSize;
                    buffer.clear();
                    channel.write(buffer, position);
                    ioCount++;
                }
            }
            channel.force(true); // 确保数据落盘
        }
    }

    /**
     * 生成供测试用的文件通道
     * 读模式会先创建一个测试文件
     * 写模式会直接打开文件，不使用DSYC
     */
    @Override
    public AsynchronousFileChannel buildChannel(TestConfig config) throws IOException {
        String filePath = config.getFilePath() + TestConfig.TEST_FILE_NAME +"_thread-id-" + Thread.currentThread().getId() + "_" + DiskTestPlugin.ID;
        AsynchronousFileChannel channel;
        boolean isRead = config.getTestType() == TestConfig.TestType.SEQ_READ || config.getTestType() == TestConfig.TestType.RAND_READ;
        if (isRead) {
            createTestFile(config, Paths.get(filePath),useRandData);
            channel = AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
        } else {
            channel = AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        }
        return channel;
    }

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

    @Override
    protected long doSeqRead(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime) throws ExecutionException, InterruptedException {
        int ioCount = 0;
        while (ioCount < maxIONum && (System.nanoTime() - startTime) < maxTestDuration) {
            final long position = ioCount * bufferSize;
            channel.read(buffer, position).get();
            ioCount++;
        }
        return ioCount;
    }

    @Override
    protected long doSeqWrite(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime) throws ExecutionException, InterruptedException {
        int ioCount = 0;
        while (ioCount < maxIONum && (System.nanoTime() - startTime) < maxTestDuration) {
            final long position = ioCount * bufferSize;
            // 清空缓冲区并进行异步写入
            buffer.clear();
            channel.write(buffer, position).get();
            ioCount++;
        }
        return ioCount;
    }

    @Override
    protected long doRandRead(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime,long[] positionPool) throws ExecutionException, InterruptedException {
        int ioCount = 0;
        int poolSize = positionPool.length;
        while (ioCount < maxIONum && (System.nanoTime() - startTime) < maxTestDuration) {
            final long position = positionPool[ioCount % poolSize];
            buffer.clear();
            channel.read(buffer, position).get();
            ioCount++;
        }
        return ioCount;
    }

    @Override
    protected long doRandWrite(TestConfig config, AsynchronousFileChannel channel, ByteBuffer buffer, int bufferSize, int maxIONum, long maxTestDuration, long startTime,long[] positionPool) throws ExecutionException, InterruptedException {
        int ioCount = 0;
        int poolSize = positionPool.length;
        while (ioCount < maxIONum && (System.nanoTime() - startTime) < maxTestDuration) {
            final long position = positionPool[ioCount % poolSize];
            buffer.clear();
            channel.write(buffer, position);
            ioCount++;
        }
        return ioCount;
    }

    /**
     * 测试的核心逻辑，根据配置进行顺序读写或者随机读写
     * 返回结果为WorkerResult实例，可以根据这个结果来计算读写速度或IOPS
     */
    @Override
    public WorkerResult call() {
        long ioCount = 0;
        long startTime;
        WorkerResult res = CommonUtil.buildEmptyResult();
        try (AsynchronousFileChannel channel = buildChannel(config)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(config.getBufferSize());
            int maxIONum = config.getMaxIONum();
            long maxTestDuration = config.getMaxTestDuration();
            final int bufferSize = config.getBufferSize();
            int poolSize = Math.min(10000, maxIONum);
            long[] positionPool = buildPositionsPool(poolSize, config);
            startTime = System.nanoTime();
            switch (config.getTestType()) {
                case SEQ_READ:
                    ioCount = doSeqRead(config, channel, buffer, bufferSize, maxIONum, maxTestDuration, startTime);
                    break;
                case SEQ_WRITE:
                    ioCount = doSeqWrite(config, channel, buffer, bufferSize, maxIONum, maxTestDuration, startTime);
                    break;
                case RAND_READ:
                    ioCount = doRandRead(config, channel, buffer, bufferSize, maxIONum, maxTestDuration, startTime,positionPool);
                    break;
                case RAND_WRITE:
                    ioCount = doRandWrite(config, channel, buffer, bufferSize, maxIONum, maxTestDuration, startTime,positionPool);
                    break;
            }
            channel.force(true); // 强制刷新缓存
            long endTime = System.nanoTime();
            res.setDurationSec(endTime - startTime);
            res.setIoCount(ioCount);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            return res;
        }
    }
}
