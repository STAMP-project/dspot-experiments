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
package org.apache.activemq.transport;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class TopicClusterTest extends TestCase implements MessageListener {
    protected static final int MESSAGE_COUNT = 50;

    protected static final int NUMBER_IN_CLUSTER = 3;

    private static final Logger LOG = LoggerFactory.getLogger(TopicClusterTest.class);

    protected Destination destination;

    protected boolean topic = true;

    protected AtomicInteger receivedMessageCount = new AtomicInteger(0);

    protected int deliveryMode = DeliveryMode.NON_PERSISTENT;

    protected MessageProducer[] producers;

    protected Connection[] connections;

    protected List<BrokerService> services = new ArrayList<BrokerService>();

    protected String groupId;

    /**
     *
     *
     * @throws Exception
     * 		
     */
    public void testSendReceive() throws Exception {
        for (int i = 0; i < (TopicClusterTest.MESSAGE_COUNT); i++) {
            TextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText(("MSG-NO:" + i));
            for (int x = 0; x < (producers.length); x++) {
                producers[x].send(textMessage);
            }
        }
        synchronized(receivedMessageCount) {
            if ((receivedMessageCount.get()) < (expectedReceiveCount())) {
                receivedMessageCount.wait(20000);
            }
        }
        // sleep a little - to check we don't get too many messages
        Thread.sleep(2000);
        TopicClusterTest.LOG.info(("GOT: " + (receivedMessageCount.get())));
        TestCase.assertEquals("Expected message count not correct", expectedReceiveCount(), receivedMessageCount.get());
    }
}

