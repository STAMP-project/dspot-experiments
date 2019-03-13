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
package org.apache.activemq.java;


import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceNotFoundException;
import org.apache.activemq.RuntimeConfigTestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.DiscoveryNetworkConnector;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.plugin.java.JavaRuntimeConfigurationBroker;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class JavaNetworkConnectorTest extends RuntimeConfigTestSupport {
    public static final int SLEEP = 2;// seconds


    private JavaRuntimeConfigurationBroker javaConfigBroker;

    @Test
    public void testNew() throws Exception {
        final BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("no network connectors", 0, brokerService.getNetworkConnectors().size());
        DiscoveryNetworkConnector nc = createNetworkConnector();
        javaConfigBroker.addNetworkConnector(nc);
        Assert.assertTrue("new network connectors", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getNetworkConnectors().size());
            }
        }));
        NetworkConnector networkConnector = brokerService.getNetworkConnectors().get(0);
        javaConfigBroker.addNetworkConnector(nc);
        TimeUnit.SECONDS.sleep(JavaNetworkConnectorTest.SLEEP);
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
        final BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("no network connectors", 0, brokerService.getNetworkConnectors().size());
        DiscoveryNetworkConnector nc = createNetworkConnector();
        javaConfigBroker.addNetworkConnector(nc);
        TimeUnit.SECONDS.sleep(JavaNetworkConnectorTest.SLEEP);
        Assert.assertEquals("one network connectors", 1, brokerService.getNetworkConnectors().size());
        // track the original
        NetworkConnector networkConnector = brokerService.getNetworkConnectors().get(0);
        Assert.assertEquals("network ttl is default", 1, networkConnector.getNetworkTTL());
        Assert.assertNotNull(networkConnector.getBrokerName());
        Assert.assertNotNull(networkConnector.getBrokerURL());
        nc.setNetworkTTL(2);
        javaConfigBroker.updateNetworkConnector(nc);
        TimeUnit.SECONDS.sleep(JavaNetworkConnectorTest.SLEEP);
        Assert.assertEquals("still one network connectors", 1, brokerService.getNetworkConnectors().size());
        NetworkConnector modNetworkConnector = brokerService.getNetworkConnectors().get(0);
        Assert.assertEquals("got ttl update", 2, modNetworkConnector.getNetworkTTL());
        // apply again - ensure no change
        javaConfigBroker.updateNetworkConnector(nc);
        Assert.assertEquals("no new network connectors", 1, brokerService.getNetworkConnectors().size());
        Assert.assertSame("same instance", modNetworkConnector, brokerService.getNetworkConnectors().get(0));
        Assert.assertFalse(modNetworkConnector.getBrokerName().isEmpty());
        Assert.assertNotNull(brokerService.getManagementContext().getObjectInstance(brokerService.createNetworkConnectorObjectName(modNetworkConnector)));
    }

    @Test
    public void testRemove() throws Exception {
        final BrokerService brokerService = new BrokerService();
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        Assert.assertEquals("no network connectors", 0, brokerService.getNetworkConnectors().size());
        DiscoveryNetworkConnector nc1 = new DiscoveryNetworkConnector();
        nc1.setUri(new URI("static:(tcp://localhost:5555)"));
        nc1.setNetworkTTL(1);
        nc1.setName("one");
        DiscoveryNetworkConnector nc2 = new DiscoveryNetworkConnector();
        nc2.setUri(new URI("static:(tcp://localhost:5555)"));
        nc2.setNetworkTTL(1);
        nc2.setName("two");
        javaConfigBroker.addNetworkConnector(nc1);
        javaConfigBroker.addNetworkConnector(nc2);
        TimeUnit.SECONDS.sleep(JavaNetworkConnectorTest.SLEEP);
        Assert.assertEquals("correct network connectors", 2, brokerService.getNetworkConnectors().size());
        javaConfigBroker.removeNetworkConnector(nc2);
        Assert.assertTrue("expected mod on time", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (brokerService.getNetworkConnectors().size());
            }
        }));
        NetworkConnector remainingNetworkConnector = brokerService.getNetworkConnectors().get(0);
        Assert.assertEquals("name match", "one", remainingNetworkConnector.getName());
        try {
            brokerService.getManagementContext().getObjectInstance(brokerService.createNetworkConnectorObjectName(nc2));
            Assert.fail("mbean for nc2 should not exist");
        } catch (InstanceNotFoundException e) {
            // should throw exception
        }
        Assert.assertNotNull(brokerService.getManagementContext().getObjectInstance(brokerService.createNetworkConnectorObjectName(nc1)));
    }
}

