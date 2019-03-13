package com.vip.saturn.job.sharding;


import SaturnExecutorsNode.LEADER_HOSTNODE_PATH;
import com.vip.saturn.job.utils.NestedZkUtils;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;


/**
 * Created by xiaopeng.he on 2016/7/6.
 */
public class NamespaceShardingServiceTest {
    private NestedZkUtils nestedZkUtils;

    @Test
    public void leadershipElectionTest() throws Exception {
        String namespace = "MyNamespace";
        CuratorFramework curatorFramework = nestedZkUtils.createClient(namespace);
        // ?????
        String ip1 = "127.0.0.1";
        CuratorFramework curatorFramework1 = nestedZkUtils.createClient(namespace);
        NamespaceShardingManager namespaceShardingManager1 = new NamespaceShardingManager(curatorFramework1, namespace, ip1, null, null);
        namespaceShardingManager1.start();
        Thread.sleep(1000);
        // ??leadership
        assertThat(new String(curatorFramework.getData().forPath(LEADER_HOSTNODE_PATH), "UTF-8")).isEqualTo(ip1);
        // ?????
        String ip2 = "127.0.0.2";
        CuratorFramework curatorFramework2 = nestedZkUtils.createClient(namespace);
        NamespaceShardingManager namespaceShardingManager2 = new NamespaceShardingManager(curatorFramework2, namespace, ip2, null, null);
        namespaceShardingManager2.start();
        Thread.sleep(1000);
        // ??leadership
        assertThat(new String(curatorFramework.getData().forPath(LEADER_HOSTNODE_PATH), "UTF-8")).isEqualTo(ip1);
        // ?????
        String ip3 = "127.0.0.3";
        CuratorFramework curatorFramework3 = nestedZkUtils.createClient(namespace);
        NamespaceShardingManager namespaceShardingManager3 = new NamespaceShardingManager(curatorFramework3, namespace, ip3, null, null);
        namespaceShardingManager3.start();
        Thread.sleep(1000);
        // ??leadership
        assertThat(new String(curatorFramework.getData().forPath(LEADER_HOSTNODE_PATH), "UTF-8")).isEqualTo(ip1);
        // ?????
        namespaceShardingManager1.stopWithCurator();
        Thread.sleep(1000);
        // ??leadership
        assertThat(new String(curatorFramework.getData().forPath(LEADER_HOSTNODE_PATH), "UTF-8")).isIn(ip2, ip3);
        // ?????
        namespaceShardingManager2.stopWithCurator();
        Thread.sleep(1000);
        // ??leadership
        assertThat(new String(curatorFramework.getData().forPath(LEADER_HOSTNODE_PATH), "UTF-8")).isEqualTo(ip3);
    }
}

