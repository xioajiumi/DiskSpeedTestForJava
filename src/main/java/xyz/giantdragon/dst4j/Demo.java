package xyz.giantdragon.dst4j;

import xyz.giantdragon.dst4j.result.TestResult;
import xyz.giantdragon.dst4j.testConfig.TestConfig;
import xyz.giantdragon.dst4j.testWorker.DiskTestWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试用例
 */
public class Demo {
    public void test() {
        ExecutorService ioPool = null;
        try {
            ioPool = Executors.newFixedThreadPool(5); // 最好与你测试的config的threadNum一致
            DiskTestPlugin plugin = new DiskTestPlugin();
            String filePath1 = "G:";
            // TestConfig config1 = new TestConfig(filePath1, TestConfig.TestType.SEQ_READ);
            TestConfig config1 = new TestConfig(filePath1, TestConfig.TestType.SEQ_WRITE);
            DiskTestPlugin.setRecommendCommonConfig(config1);
            DiskTestPlugin.setRecommendRWConfig(config1);
            config1.setFileSizeMB(1024*2);
            TestResult testResult1 = plugin.runTest(config1, ioPool, DiskTestWorker.class);
            System.out.println("\u001B[34m" + "====IOPS:===="+testResult1.getiops() + "\u001B[0m");
            System.out.println("\u001B[34m" + "====Speed:===="+testResult1.getdataThroughputMBps() + "\u001B[0m");
            // another test example
            // TestConfig config2 = new TestConfig(config1);
            // config2.setFilePath("H:\\").setTestType(TestConfig.TestType.RAND_READ);
            // DiskTestPlugin.setRecommendRWConfig(config2).setFileSizeMB((int)(0.5*1024));
            // TestResult testResult2 = plugin.runTest(config2, ioPool);
            // System.out.println("\u001B[34m" + "====IOPS:===="+testResult2.getiops() + "\u001B[0m");
            // System.out.println("\u001B[34m" + "====Speed:===="+testResult2.getdataThroughputMBps() + "\u001B[0m");


        } catch (Exception e) {
            System.out.println("\u001B[34m" + "====DiskTestPluginRunningError===" + e.getMessage() + "\u001B[0m");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (ioPool != null) {
                ioPool.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Demo demo = new Demo();
        demo.test();
    }
}
