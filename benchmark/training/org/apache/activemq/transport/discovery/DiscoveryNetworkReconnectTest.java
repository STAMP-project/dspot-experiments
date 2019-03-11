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
package org.apache.activemq.transport.discovery;


import java.net.URI;
import java.util.concurrent.Semaphore;
import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.transport.discovery.multicast.MulticastDiscoveryAgentFactory;
import org.apache.activemq.util.SocketProxy;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(JMock.class)
public class DiscoveryNetworkReconnectTest {
    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryNetworkReconnectTest.class);

    final int maxReconnects = 2;

    final String groupName = "GroupID-" + "DiscoveryNetworkReconnectTest";

    final String discoveryAddress = ("multicast://default?group=" + (groupName)) + "&initialReconnectDelay=1000";

    final Semaphore mbeanRegistered = new Semaphore(0);

    final Semaphore mbeanUnregistered = new Semaphore(0);

    BrokerService brokerA;

    BrokerService brokerB;

    Mockery context;

    ManagementContext managementContext;

    DiscoveryAgent agent;

    SocketProxy proxy;

    // ignore the hostname resolution component as this is machine dependent
    class NetworkBridgeObjectNameMatcher<T> extends BaseMatcher<T> {
        T name;

        NetworkBridgeObjectNameMatcher(T o) {
            name = o;
        }

        @Override
        public boolean matches(Object arg0) {
            ObjectName other = ((ObjectName) (arg0));
            ObjectName mine = ((ObjectName) (name));
            DiscoveryNetworkReconnectTest.LOG.info(((("Match: " + mine) + " vs: ") + other));
            if (!("networkConnectors".equals(other.getKeyProperty("connector")))) {
                return false;
            }
            return ((other.getKeyProperty("connector").equals(mine.getKeyProperty("connector"))) && ((other.getKeyProperty("networkBridge")) != null)) && ((mine.getKeyProperty("networkBridge")) != null);
        }

        @Override
        public void describeTo(Description arg0) {
            arg0.appendText(this.getClass().getName());
        }
    }

    @Test
    public void testMulicastReconnect() throws Exception {
        brokerB.addNetworkConnector(((discoveryAddress) + "&discovered.trace=true&discovered.wireFormat.maxInactivityDuration=1000&discovered.wireFormat.maxInactivityDurationInitalDelay=1000"));
        brokerB.start();
        brokerB.waitUntilStarted();
        // control multicast advertise agent to inject proxy
        agent = MulticastDiscoveryAgentFactory.createDiscoveryAgent(new URI(discoveryAddress));
        agent.registerService(proxy.getUrl().toString());
        agent.start();
        doReconnect();
    }

    @Test
    public void testSimpleReconnect() throws Exception {
        brokerB.addNetworkConnector((("simple://(" + (proxy.getUrl())) + ")?useExponentialBackOff=false&initialReconnectDelay=500&discovered.wireFormat.maxInactivityDuration=1000&discovered.wireFormat.maxInactivityDurationInitalDelay=1000"));
        brokerB.start();
        brokerB.waitUntilStarted();
        doReconnect();
    }
}

