package com.alibaba.csp.sentinel.cluster.server.config;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Eric Zhao
 */
public class ClusterServerConfigManagerTest {
    @Test
    public void testIsValidTransportConfig() {
        ServerTransportConfig badConfig1 = new ServerTransportConfig().setPort((-1));
        ServerTransportConfig badConfig2 = new ServerTransportConfig().setPort(886622);
        ServerTransportConfig goodConfig1 = new ServerTransportConfig().setPort(23456);
        Assert.assertFalse(ClusterServerConfigManager.isValidTransportConfig(badConfig1));
        Assert.assertFalse(ClusterServerConfigManager.isValidTransportConfig(badConfig2));
        Assert.assertTrue(ClusterServerConfigManager.isValidTransportConfig(goodConfig1));
    }

    @Test
    public void testIsValidFlowConfig() {
        ServerFlowConfig badConfig1 = new ServerFlowConfig().setMaxAllowedQps((-2));
        ServerFlowConfig badConfig2 = new ServerFlowConfig().setIntervalMs(1000).setSampleCount(3);
        ServerFlowConfig badConfig3 = new ServerFlowConfig().setIntervalMs(1000).setSampleCount(0);
        Assert.assertFalse(ClusterServerConfigManager.isValidFlowConfig(badConfig1));
        Assert.assertFalse(ClusterServerConfigManager.isValidFlowConfig(badConfig2));
        Assert.assertFalse(ClusterServerConfigManager.isValidFlowConfig(badConfig3));
    }
}

