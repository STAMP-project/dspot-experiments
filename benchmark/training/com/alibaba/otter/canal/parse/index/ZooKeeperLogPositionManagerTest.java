package com.alibaba.otter.canal.parse.index;


import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import org.junit.Test;


public class ZooKeeperLogPositionManagerTest extends AbstractLogPositionManagerTest {
    private ZkClientx zkclientx = new ZkClientx((((cluster1) + ";") + (cluster2)));

    @Test
    public void testAll() {
        ZooKeeperLogPositionManager logPositionManager = new ZooKeeperLogPositionManager(zkclientx);
        logPositionManager.start();
        doTest(logPositionManager);
        logPositionManager.stop();
    }
}

