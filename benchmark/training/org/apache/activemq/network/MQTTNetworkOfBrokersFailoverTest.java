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
package org.apache.activemq.network;


import QoS.AT_LEAST_ONCE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerTestSupport;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by ceposta
 * <a href="http://christianposta.com/blog>http://christianposta.com/blog</a>.
 */
public class MQTTNetworkOfBrokersFailoverTest extends NetworkTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MQTTNetworkOfBrokersFailoverTest.class);

    private int localBrokerMQTTPort = -1;

    private int remoteBrokerMQTTPort = -1;

    @Test
    public void testNoStaleSubscriptionAcrossNetwork() throws Exception {
        // before we get started, we want an async way to be able to know when
        // the durable consumer has been networked so we can assert that it indeed
        // would have a durable subscriber. for example, when we subscribe on remote broker,
        // a network-sub would be created on local broker and we want to listen for when that
        // even happens. we do that with advisory messages and a latch:
        CountDownLatch consumerNetworked = listenForConsumersOn(broker);
        // create a subscription with Clean == 0 (durable sub for QoS==1 && QoS==2)
        // on the remote broker. this sub should still be there after we disconnect
        MQTT remoteMqtt = createMQTTTcpConnection("foo", false, remoteBrokerMQTTPort);
        BlockingConnection remoteConn = remoteMqtt.blockingConnection();
        remoteConn.connect();
        remoteConn.subscribe(new Topic[]{ new Topic("foo/bar", QoS.AT_LEAST_ONCE) });
        assertTrue("No destination detected!", consumerNetworked.await(1, TimeUnit.SECONDS));
        assertQueueExistsOn(remoteBroker, "Consumer.foo_AT_LEAST_ONCE.VirtualTopic.foo.bar");
        assertQueueExistsOn(broker, "Consumer.foo_AT_LEAST_ONCE.VirtualTopic.foo.bar");
        remoteConn.disconnect();
        // now we reconnect the same sub on the local broker, again with clean==0
        MQTT localMqtt = createMQTTTcpConnection("foo", false, localBrokerMQTTPort);
        BlockingConnection localConn = localMqtt.blockingConnection();
        localConn.connect();
        localConn.subscribe(new Topic[]{ new Topic("foo/bar", QoS.AT_LEAST_ONCE) });
        // now let's connect back up to remote broker and send a message
        remoteConn = remoteMqtt.blockingConnection();
        remoteConn.connect();
        remoteConn.publish("foo/bar", "Hello, World!".getBytes(), AT_LEAST_ONCE, false);
        // now we should see that message on the local broker because the subscription
        // should have been properly networked... we'll give a sec of grace for the
        // networking and forwarding to have happened properly
        Message msg = localConn.receive(100, TimeUnit.SECONDS);
        assertNotNull(msg);
        msg.ack();
        String response = new String(msg.getPayload());
        assertEquals("Hello, World!", response);
        assertEquals("foo/bar", msg.getTopic());
        // Now... we SHOULD NOT see a message on the remote broker because we already
        // consumed it on the local broker... having the same message on the remote broker
        // would effectively give us duplicates in a distributed topic scenario:
        remoteConn.subscribe(new Topic[]{ new Topic("foo/bar", QoS.AT_LEAST_ONCE) });
        msg = remoteConn.receive(500, TimeUnit.MILLISECONDS);
        assertNull("We have duplicate messages across the cluster for a distributed topic", msg);
    }
}

