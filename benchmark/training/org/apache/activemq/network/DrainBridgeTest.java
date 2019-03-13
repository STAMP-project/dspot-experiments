/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.network;


import java.util.Arrays;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class DrainBridgeTest {
    @Test
    public void testDrain() throws Exception {
        prepareBrokerWithMessages();
        BrokerService target = prepareDrainTarget();
        BrokerService drainingBroker = new BrokerService();
        drainingBroker.setBrokerName("HOST");
        // add the draining bridge that subscribes to all queues and forwards on start - irrespective of demand
        NetworkConnector drainingNetworkConnector = drainingBroker.addNetworkConnector((("static:(" + (target.getTransportConnectorByScheme("tcp").getPublishableConnectString())) + ")"));
        drainingNetworkConnector.setStaticBridge(true);
        drainingNetworkConnector.setStaticallyIncludedDestinations(Arrays.asList(new ActiveMQDestination[]{ new ActiveMQQueue(">") }));
        // ensure replay back to the origin is allowed
        PolicyMap policyMap = new PolicyMap();
        PolicyEntry defaultEntry = new PolicyEntry();
        defaultEntry.setExpireMessagesPeriod(0);
        ConditionalNetworkBridgeFilterFactory filterFactory = new ConditionalNetworkBridgeFilterFactory();
        filterFactory.setReplayWhenNoConsumers(true);
        defaultEntry.setNetworkBridgeFilterFactory(filterFactory);
        policyMap.setDefaultEntry(defaultEntry);// applies to all destinations

        drainingBroker.setDestinationPolicy(policyMap);
        drainingBroker.start();
        System.out.println(((("Local count: " + (drainingBroker.getAdminView().getTotalMessageCount())) + ", target count:") + (target.getAdminView().getTotalMessageCount())));
        Assert.assertEquals("local messages", 22, drainingBroker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("no remote messages", 0, target.getAdminView().getTotalMessageCount());
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                System.out.println(((("Local count: " + (drainingBroker.getAdminView().getTotalMessageCount())) + ", target count:") + (target.getAdminView().getTotalMessageCount())));
                return (drainingBroker.getAdminView().getTotalMessageCount()) == 0L;
            }
        });
        Assert.assertEquals("no local messages", 0, drainingBroker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("remote messages", 22, target.getAdminView().getTotalMessageCount());
        Assert.assertEquals("number of queues match", drainingBroker.getAdminView().getQueues().length, target.getAdminView().getQueues().length);
        drainingBroker.stop();
        target.stop();
    }
}

