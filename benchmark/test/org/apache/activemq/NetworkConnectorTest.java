/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq;


import javax.management.InstanceNotFoundException;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class NetworkConnectorTest extends RuntimeConfigTestSupport {
    String configurationSeed = "networkConnectorTest";

    @Test
    public void testNew() throws Exception {
        final String brokerConfig = (configurationSeed) + "-no-nc-broker";
        applyNewConfig(brokerConfig, RuntimeConfigTestSupport.EMPTY_UPDATABLE_CONFIG);
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("no network connectors", 0, brokerService.getNetworkConnectors().size());
        applyNewConfig(brokerConfig, ((configurationSeed) + "-one-nc"), RuntimeConfigTestSupport.SLEEP);
        Assert.assertTrue("new network connectors", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getNetworkConnectors().size());
            }
        }));
        // apply again - ensure no change
        NetworkConnector networkConnector = brokerService.getNetworkConnectors().get(0);
        applyNewConfig(brokerConfig, ((configurationSeed) + "-one-nc"));
        Assert.assertEquals("no new network connectors", 1, brokerService.getNetworkConnectors().size());
        Assert.assertSame("same instance", networkConnector, brokerService.getNetworkConnectors().get(0));
        // verify nested elements
        Assert.assertEquals("has exclusions", 2, networkConnector.getExcludedDestinations().size());
        Assert.assertEquals("one statically included", 1, networkConnector.getStaticallyIncludedDestinations().size());
        Assert.assertEquals("one dynamically included", 1, networkConnector.getDynamicallyIncludedDestinations().size());
        Assert.assertEquals("one durable", 1, networkConnector.getDurableDestinations().size());
        Assert.assertFalse(networkConnector.getBrokerName().isEmpty());
        Assert.assertNotNull(brokerService.getManagementContext().getObjectInstance(brokerService.createNetworkConnectorObjectName(networkConnector)));
    }

    @Test
    public void testMod() throws Exception {
        final String brokerConfig = (configurationSeed) + "-one-nc-broker";
        applyNewConfig(brokerConfig, ((configurationSeed) + "-one-nc"));
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("one network connectors", 1, brokerService.getNetworkConnectors().size());
        // track the original
        NetworkConnector networkConnector = brokerService.getNetworkConnectors().get(0);
        Assert.assertEquals("network ttl is default", 1, networkConnector.getNetworkTTL());
        applyNewConfig(brokerConfig, ((configurationSeed) + "-mod-one-nc"), RuntimeConfigTestSupport.SLEEP);
        Assert.assertEquals("still one network connectors", 1, brokerService.getNetworkConnectors().size());
        NetworkConnector modNetworkConnector = brokerService.getNetworkConnectors().get(0);
        Assert.assertEquals("got ttl update", 2, modNetworkConnector.getNetworkTTL());
        Assert.assertNotNull("got ssl", modNetworkConnector.getSslContext());
        // apply again - ensure no change
        applyNewConfig(brokerConfig, ((configurationSeed) + "-mod-one-nc"), RuntimeConfigTestSupport.SLEEP);
        Assert.assertEquals("no new network connectors", 1, brokerService.getNetworkConnectors().size());
        Assert.assertSame("same instance", modNetworkConnector, brokerService.getNetworkConnectors().get(0));
        Assert.assertFalse(modNetworkConnector.getBrokerName().isEmpty());
        Assert.assertNotNull(brokerService.getManagementContext().getObjectInstance(brokerService.createNetworkConnectorObjectName(modNetworkConnector)));
    }

    @Test
    public void testRemove() throws Exception {
        final String brokerConfig = (configurationSeed) + "-two-nc-broker";
        applyNewConfig(brokerConfig, ((configurationSeed) + "-two-nc"));
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("correct network connectors", 2, brokerService.getNetworkConnectors().size());
        NetworkConnector two = brokerService.getNetworkConnectors().get(1);
        applyNewConfig(brokerConfig, ((configurationSeed) + "-one-nc"), RuntimeConfigTestSupport.SLEEP);
        Assert.assertTrue("expected mod on time", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getNetworkConnectors().size());
            }
        }));
        NetworkConnector remainingNetworkConnector = brokerService.getNetworkConnectors().get(0);
        Assert.assertEquals("name match", "one", remainingNetworkConnector.getName());
        try {
            brokerService.getManagementContext().getObjectInstance(brokerService.createNetworkConnectorObjectName(two));
            Assert.fail("mbean for nc2 should not exist");
        } catch (InstanceNotFoundException e) {
            // should throw exception
        }
        Assert.assertNotNull(brokerService.getManagementContext().getObjectInstance(brokerService.createNetworkConnectorObjectName(remainingNetworkConnector)));
    }
}

