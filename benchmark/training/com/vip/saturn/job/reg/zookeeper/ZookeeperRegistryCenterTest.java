package com.vip.saturn.job.reg.zookeeper;


import com.vip.saturn.job.utils.SystemEnvProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.Assert;
import org.junit.Test;


public class ZookeeperRegistryCenterTest {
    private static final String NAMESPACE = "ut-saturn";

    private int PORT = 3181;

    private TestingServer testingServer;

    @Test
    public void testZkClientConfig() throws Exception {
        // default settings
        CuratorFramework client = initZk(("127.0.0.1:" + (PORT)), "ut-ns");
        Assert.assertEquals(20000L, client.getZookeeperClient().getConnectionTimeoutMs());
        Assert.assertEquals(20000L, client.getZookeeperClient().getZooKeeper().getSessionTimeout());
        ExponentialBackoffRetry retryPolicy = ((ExponentialBackoffRetry) (client.getZookeeperClient().getRetryPolicy()));
        Assert.assertEquals(3, retryPolicy.getN());
        // set VIP_SATURN_ZK_CLIENT_CONNECTION_TIMEOUT = true
        System.setProperty("VIP_SATURN_USE_UNSTABLE_NETWORK_SETTING", "true");
        SystemEnvProperties.loadProperties();
        client = initZk(("127.0.0.1:" + (PORT)), "ut-ns");
        Assert.assertEquals(40000L, client.getZookeeperClient().getConnectionTimeoutMs());
        Assert.assertEquals(40000L, client.getZookeeperClient().getZooKeeper().getSessionTimeout());
        retryPolicy = ((ExponentialBackoffRetry) (client.getZookeeperClient().getRetryPolicy()));
        Assert.assertEquals(9, retryPolicy.getN());
    }
}

