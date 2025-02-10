package com.dragon.dst4j.testWorker;

import com.dragon.dst4j.testConfig.TestConfig;

/**
 * 增加这一层是为了解耦，如果想微调测试方法，可以继承BaseDiskTestWorker，想大改就直接继承AbstractDiskTestWorker
 */
public class DiskTestWorker extends BaseDiskTestWorker {
    public DiskTestWorker(TestConfig config) {
        super(config);
    }
}