/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.network;


import javax.jms.MessageProducer;
import javax.jms.TemporaryQueue;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.TransportConnection;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class DynamicallyIncludedDestinationsDuplexNetworkTest extends SimpleNetworkTest {
    private static final int REMOTE_BROKER_TCP_PORT = 61617;

    @Test
    public void testTempQueues() throws Exception {
        TemporaryQueue temp = localSession.createTemporaryQueue();
        MessageProducer producer = localSession.createProducer(temp);
        producer.send(localSession.createTextMessage("test"));
        Thread.sleep(100);
        Assert.assertEquals("Destination not created", 1, remoteBroker.getAdminView().getTemporaryQueues().length);
        temp.delete();
        Thread.sleep(100);
        Assert.assertEquals("Destination not deleted", 0, remoteBroker.getAdminView().getTemporaryQueues().length);
    }

    @Test
    public void testDynamicallyIncludedDestinationsForDuplex() throws Exception {
        // Once the bridge is set up, we should see the filter used for the duplex end of the bridge
        // only subscribe to the specific destinations included in the <dynamicallyIncludedDestinations> list
        // so let's test that the filter is correct, let's also test the subscription on the localbroker
        // is correct
        // the bridge on the remote broker has the correct filter
        TransportConnection bridgeConnection = getDuplexBridgeConnectionFromRemote();
        Assert.assertNotNull(bridgeConnection);
        DemandForwardingBridge duplexBridge = getDuplexBridgeFromConnection(bridgeConnection);
        Assert.assertNotNull(duplexBridge);
        NetworkBridgeConfiguration configuration = getConfigurationFromNetworkBridge(duplexBridge);
        Assert.assertNotNull(configuration);
        Assert.assertFalse("This destinationFilter does not include ONLY the destinations specified in dynamicallyIncludedDestinations", configuration.getDestinationFilter().equals(((AdvisorySupport.CONSUMER_ADVISORY_TOPIC_PREFIX) + ">")));
        Assert.assertEquals("There are other patterns in the destinationFilter that shouldn't be there", "ActiveMQ.Advisory.Consumer.Queue.include.test.foo,ActiveMQ.Advisory.Consumer.Topic.include.test.bar", configuration.getDestinationFilter());
    }
}

