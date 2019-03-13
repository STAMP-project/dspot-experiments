package com.alibaba.otter.canal.meta;


import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.protocol.ClientIdentity;
import com.alibaba.otter.canal.protocol.position.Position;
import com.alibaba.otter.canal.protocol.position.PositionRange;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class PeriodMixedMetaManagerTest extends AbstractMetaManagerTest {
    private ZkClientx zkclientx = new ZkClientx((((cluster1) + ";") + (cluster2)));

    @Test
    public void testSubscribeAll() {
        PeriodMixedMetaManager metaManager = new PeriodMixedMetaManager();
        ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
        zooKeeperMetaManager.setZkClientx(zkclientx);
        metaManager.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager.start();
        doSubscribeTest(metaManager);
        sleep(1000L);
        // ?????????????zk????
        PeriodMixedMetaManager metaManager2 = new PeriodMixedMetaManager();
        metaManager2.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager2.start();
        List<ClientIdentity> clients = metaManager2.listAllSubscribeInfo(destination);
        Assert.assertEquals(2, clients.size());
        metaManager.stop();
    }

    @Test
    public void testBatchAll() {
        PeriodMixedMetaManager metaManager = new PeriodMixedMetaManager();
        ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
        zooKeeperMetaManager.setZkClientx(zkclientx);
        metaManager.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager.start();
        doBatchTest(metaManager);
        metaManager.clearAllBatchs(clientIdentity);
        Map<Long, PositionRange> ranges = metaManager.listAllBatchs(clientIdentity);
        Assert.assertEquals(0, ranges.size());
        metaManager.stop();
    }

    @Test
    public void testCursorAll() {
        PeriodMixedMetaManager metaManager = new PeriodMixedMetaManager();
        ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
        zooKeeperMetaManager.setZkClientx(zkclientx);
        metaManager.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager.start();
        Position lastPosition = doCursorTest(metaManager);
        sleep(1000L);
        // ?????????????zk????
        PeriodMixedMetaManager metaManager2 = new PeriodMixedMetaManager();
        metaManager2.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager2.start();
        Position position = metaManager2.getCursor(clientIdentity);
        Assert.assertEquals(position, lastPosition);
        metaManager.stop();
    }
}

