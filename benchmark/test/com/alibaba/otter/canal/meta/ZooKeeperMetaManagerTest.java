package com.alibaba.otter.canal.meta;


import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.protocol.position.PositionRange;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ZooKeeperMetaManagerTest extends AbstractMetaManagerTest {
    private ZkClientx zkclientx = new ZkClientx((((cluster1) + ";") + (cluster2)));

    @Test
    public void testSubscribeAll() {
        ZooKeeperMetaManager metaManager = new ZooKeeperMetaManager();
        metaManager.setZkClientx(zkclientx);
        metaManager.start();
        doSubscribeTest(metaManager);
        metaManager.stop();
    }

    @Test
    public void testBatchAll() {
        ZooKeeperMetaManager metaManager = new ZooKeeperMetaManager();
        metaManager.setZkClientx(zkclientx);
        metaManager.start();
        doBatchTest(metaManager);
        metaManager.clearAllBatchs(clientIdentity);
        Map<Long, PositionRange> ranges = metaManager.listAllBatchs(clientIdentity);
        Assert.assertEquals(0, ranges.size());
        metaManager.stop();
    }

    @Test
    public void testCursorhAll() {
        ZooKeeperMetaManager metaManager = new ZooKeeperMetaManager();
        metaManager.setZkClientx(zkclientx);
        metaManager.start();
        doCursorTest(metaManager);
        metaManager.stop();
    }
}

