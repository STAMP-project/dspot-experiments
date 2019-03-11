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
package org.apache.activemq.broker.virtual;


import java.util.concurrent.CountDownLatch;
import javax.annotation.Resource;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "virtual-topic-network-test.xml" })
public class MessageDestinationVirtualTopicTest {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDestinationVirtualTopicTest.class);

    private SimpleMessageListener listener1;

    private SimpleMessageListener listener2;

    private SimpleMessageListener listener3;

    @Resource(name = "broker1")
    private BrokerService broker1;

    @Resource(name = "broker2")
    private BrokerService broker2;

    private MessageProducer producer;

    private Session session1;

    @Test
    public void testDestinationNames() throws Exception {
        MessageDestinationVirtualTopicTest.LOG.info("Started waiting for broker 1 and 2");
        broker1.waitUntilStarted();
        broker2.waitUntilStarted();
        MessageDestinationVirtualTopicTest.LOG.info("Broker 1 and 2 have started");
        init();
        // Create a monitor
        CountDownLatch monitor = new CountDownLatch(3);
        listener1.setCountDown(monitor);
        listener2.setCountDown(monitor);
        listener3.setCountDown(monitor);
        MessageDestinationVirtualTopicTest.LOG.info("Sending message");
        // Send a message on the topic
        TextMessage message = session1.createTextMessage("Hello World !");
        producer.send(message);
        MessageDestinationVirtualTopicTest.LOG.info("Waiting for message reception");
        // Wait the two messages in the related queues
        monitor.await();
        // Get the message destinations
        String lastJMSDestination2 = listener2.getLastJMSDestination();
        System.err.println(lastJMSDestination2);
        String lastJMSDestination1 = listener1.getLastJMSDestination();
        System.err.println(lastJMSDestination1);
        String lastJMSDestination3 = listener3.getLastJMSDestination();
        System.err.println(lastJMSDestination3);
        // The destination names
        Assert.assertEquals("queue://Consumer.D.VirtualTopic.T1", lastJMSDestination2);
        Assert.assertEquals("queue://Consumer.C.VirtualTopic.T1", lastJMSDestination1);
        Assert.assertEquals("topic://VirtualTopic.T2", lastJMSDestination3);
    }
}

