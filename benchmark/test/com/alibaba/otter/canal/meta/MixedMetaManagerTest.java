package com.alibaba.otter.canal.meta;


import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.protocol.ClientIdentity;
import com.alibaba.otter.canal.protocol.position.Position;
import com.alibaba.otter.canal.protocol.position.PositionRange;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MixedMetaManagerTest extends AbstractMetaManagerTest {
    private ZkClientx zkclientx = new ZkClientx((((cluster1) + ";") + (cluster2)));

    @Test
    public void testSubscribeAll() {
        MixedMetaManager metaManager = new MixedMetaManager();
        ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
        zooKeeperMetaManager.setZkClientx(zkclientx);
        metaManager.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager.start();
        doSubscribeTest(metaManager);
        sleep(1000L);
        // ?????????????zk????
        MixedMetaManager metaManager2 = new MixedMetaManager();
        metaManager2.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager2.start();
        List<ClientIdentity> clients = metaManager2.listAllSubscribeInfo(destination);
        Assert.assertEquals(2, clients.size());
        metaManager.stop();
    }

    @Test
    public void testBatchAll() {
        MixedMetaManager metaManager = new MixedMetaManager();
        ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
        zooKeeperMetaManager.setZkClientx(zkclientx);
        metaManager.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager.start();
        doBatchTest(metaManager);
        sleep(1000L);
        // ?????????????zk????
        MixedMetaManager metaManager2 = new MixedMetaManager();
        metaManager2.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager2.start();
        Map<Long, PositionRange> ranges = metaManager2.listAllBatchs(clientIdentity);
        Assert.assertEquals(3, ranges.size());
        metaManager.clearAllBatchs(clientIdentity);
        ranges = metaManager.listAllBatchs(clientIdentity);
        Assert.assertEquals(0, ranges.size());
        metaManager.stop();
        metaManager2.stop();
    }

    @Test
    public void testCursorAll() {
        MixedMetaManager metaManager = new MixedMetaManager();
        ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
        zooKeeperMetaManager.setZkClientx(zkclientx);
        metaManager.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager.start();
        Position lastPosition = doCursorTest(metaManager);
        sleep(1000L);
        // ?????????????zk????
        MixedMetaManager metaManager2 = new MixedMetaManager();
        metaManager2.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager2.start();
        Position position = metaManager2.getCursor(clientIdentity);
        Assert.assertEquals(position, lastPosition);
        metaManager.stop();
    }
}

