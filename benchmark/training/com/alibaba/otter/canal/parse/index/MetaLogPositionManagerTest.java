package com.alibaba.otter.canal.parse.index;


import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import com.alibaba.otter.canal.meta.MixedMetaManager;
import com.alibaba.otter.canal.meta.ZooKeeperMetaManager;
import com.alibaba.otter.canal.protocol.ClientIdentity;
import com.alibaba.otter.canal.protocol.position.LogPosition;
import com.alibaba.otter.canal.protocol.position.PositionRange;
import org.junit.Assert;
import org.junit.Test;


public class MetaLogPositionManagerTest extends AbstractLogPositionManagerTest {
    private static final String MYSQL_ADDRESS = "127.0.0.1";

    private ZkClientx zkclientx = new ZkClientx((((cluster1) + ";") + (cluster2)));

    @Test
    public void testAll() {
        MixedMetaManager metaManager = new MixedMetaManager();
        ZooKeeperMetaManager zooKeeperMetaManager = new ZooKeeperMetaManager();
        zooKeeperMetaManager.setZkClientx(zkclientx);
        metaManager.setZooKeeperMetaManager(zooKeeperMetaManager);
        metaManager.start();
        MetaLogPositionManager logPositionManager = new MetaLogPositionManager(metaManager);
        logPositionManager.start();
        // ??meta??
        ClientIdentity client1 = new ClientIdentity(destination, ((short) (1)));
        metaManager.subscribe(client1);
        PositionRange range1 = buildRange(1);
        metaManager.updateCursor(client1, range1.getEnd());
        PositionRange range2 = buildRange(2);
        metaManager.updateCursor(client1, range2.getEnd());
        ClientIdentity client2 = new ClientIdentity(destination, ((short) (2)));
        metaManager.subscribe(client2);
        PositionRange range3 = buildRange(3);
        metaManager.updateCursor(client2, range3.getEnd());
        PositionRange range4 = buildRange(4);
        metaManager.updateCursor(client2, range4.getEnd());
        LogPosition logPosition = logPositionManager.getLatestIndexBy(destination);
        Assert.assertEquals(range2.getEnd(), logPosition);
        metaManager.stop();
        logPositionManager.stop();
    }
}

