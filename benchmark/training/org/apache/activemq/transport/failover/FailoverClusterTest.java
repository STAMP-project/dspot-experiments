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
package org.apache.activemq.transport.failover;


import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;


public class FailoverClusterTest extends TestCase {
    private static final int NUMBER = 10;

    private static final String BROKER_BIND_ADDRESS = "tcp://0.0.0.0:0";

    private static final String BROKER_A_NAME = "BROKERA";

    private static final String BROKER_B_NAME = "BROKERB";

    private BrokerService brokerA;

    private BrokerService brokerB;

    private String clientUrl;

    private final List<ActiveMQConnection> connections = new ArrayList<ActiveMQConnection>();

    public void testClusterConnectedAfterClients() throws Exception {
        createClients();
        if ((brokerB) == null) {
            brokerB = createBrokerB(getBindAddress());
        }
        Thread.sleep(3000);
        Set<String> set = new HashSet<String>();
        for (ActiveMQConnection c : connections) {
            set.add(c.getTransportChannel().getRemoteAddress());
        }
        TestCase.assertTrue(((set.size()) > 1));
    }

    public void testClusterURIOptionsStrip() throws Exception {
        createClients();
        if ((brokerB) == null) {
            // add in server side only url param, should not be propagated
            brokerB = createBrokerB(((getBindAddress()) + "?transport.closeAsync=false"));
        }
        Thread.sleep(3000);
        Set<String> set = new HashSet<String>();
        for (ActiveMQConnection c : connections) {
            set.add(c.getTransportChannel().getRemoteAddress());
        }
        TestCase.assertTrue(((set.size()) > 1));
    }

    public void testClusterConnectedBeforeClients() throws Exception {
        if ((brokerB) == null) {
            brokerB = createBrokerB(getBindAddress());
        }
        Thread.sleep(5000);
        createClients();
        Thread.sleep(2000);
        brokerA.stop();
        Thread.sleep(2000);
        URI brokerBURI = new URI(brokerB.getTransportConnectors().get(0).getPublishableConnectString());
        for (ActiveMQConnection c : connections) {
            String addr = c.getTransportChannel().getRemoteAddress();
            TestCase.assertTrue(((addr.indexOf(("" + (brokerBURI.getPort())))) > 0));
        }
    }
}

