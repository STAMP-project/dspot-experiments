/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.config;


import BrokerCapacityConfigFileResolver.CAPACITY_CONFIG_FILE;
import Resource.NW_IN;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link BrokerCapacityConfigFileResolver}
 */
public class BrokerCapacityConfigFileResolverTest {
    @Test
    public void testParseConfigFile() {
        BrokerCapacityConfigResolver configResolver = new BrokerCapacityConfigFileResolver();
        Map<String, String> configs = new HashMap<>();
        String fileName = this.getClass().getClassLoader().getResource("testCapacityConfig.json").getFile();
        configs.put(CAPACITY_CONFIG_FILE, fileName);
        configResolver.configure(configs);
        Assert.assertEquals(200000.0, configResolver.capacityForBroker("", "", 0).capacity().get(NW_IN), 0.01);
        Assert.assertEquals(100000.0, configResolver.capacityForBroker("", "", 2).capacity().get(NW_IN), 0.01);
        try {
            configResolver.capacityForBroker("", "", (-1));
            Assert.fail("Should have thrown exception for negative broker id");
        } catch (IllegalArgumentException e) {
            // let it go
        }
        Assert.assertTrue(configResolver.capacityForBroker("", "", 2).isEstimated());
        Assert.assertTrue(((configResolver.capacityForBroker("", "", 2).estimationInfo().length()) > 0));
    }
}

