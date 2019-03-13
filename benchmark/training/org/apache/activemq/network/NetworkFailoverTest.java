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


import DeliveryMode.NON_PERSISTENT;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.DestinationDoesNotExistException;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


public class NetworkFailoverTest extends TestCase {
    protected static final int MESSAGE_COUNT = 10;

    private static final Logger LOG = LoggerFactory.getLogger(NetworkFailoverTest.class);

    protected AbstractApplicationContext context;

    protected Connection localConnection;

    protected Connection remoteConnection;

    protected BrokerService localBroker;

    protected BrokerService remoteBroker;

    protected Session localSession;

    protected Session remoteSession;

    protected ActiveMQQueue included = new ActiveMQQueue("include.test.foo");

    private final AtomicInteger replyToNonExistDest = new AtomicInteger(0);

    private final AtomicInteger roundTripComplete = new AtomicInteger(0);

    private final AtomicInteger remoteDLQCount = new AtomicInteger(0);

    public void testRequestReply() throws Exception {
        final MessageProducer remoteProducer = remoteSession.createProducer(null);
        MessageConsumer remoteConsumer = remoteSession.createConsumer(included);
        remoteConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                final TextMessage textMsg = ((TextMessage) (msg));
                try {
                    String payload = (("REPLY: " + (textMsg.getText())) + ", ") + (textMsg.getJMSMessageID());
                    Destination replyTo;
                    replyTo = msg.getJMSReplyTo();
                    textMsg.clearBody();
                    textMsg.setText(payload);
                    NetworkFailoverTest.LOG.info("*** Sending response: {}", textMsg.getText());
                    remoteProducer.send(replyTo, textMsg);
                    NetworkFailoverTest.LOG.info(("replied with: " + (textMsg.getJMSMessageID())));
                } catch (DestinationDoesNotExistException expected) {
                    // been removed but not yet recreated
                    replyToNonExistDest.incrementAndGet();
                    try {
                        NetworkFailoverTest.LOG.info(("NED: " + (textMsg.getJMSMessageID())));
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    NetworkFailoverTest.LOG.warn("*** Responder listener caught exception: ", e);
                    e.printStackTrace();
                }
            }
        });
        Queue tempQueue = localSession.createTemporaryQueue();
        MessageProducer requestProducer = localSession.createProducer(included);
        requestProducer.setDeliveryMode(NON_PERSISTENT);
        MessageConsumer requestConsumer = localSession.createConsumer(tempQueue);
        // track remote dlq for forward failures
        MessageConsumer dlqconsumer = remoteSession.createConsumer(new ActiveMQQueue(SharedDeadLetterStrategy.DEFAULT_DEAD_LETTER_QUEUE_NAME));
        dlqconsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    NetworkFailoverTest.LOG.info(("dlq " + (message.getJMSMessageID())));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                remoteDLQCount.incrementAndGet();
            }
        });
        // allow for consumer infos to perculate arround
        Thread.sleep(2000);
        long done = (System.currentTimeMillis()) + ((NetworkFailoverTest.MESSAGE_COUNT) * 6000);
        int i = 0;
        while (((NetworkFailoverTest.MESSAGE_COUNT) > (((roundTripComplete.get()) + (remoteDLQCount.get())) + (replyToNonExistDest.get()))) && (done > (System.currentTimeMillis()))) {
            if (i < (NetworkFailoverTest.MESSAGE_COUNT)) {
                String payload = "test msg " + i;
                i++;
                TextMessage msg = localSession.createTextMessage(payload);
                msg.setJMSReplyTo(tempQueue);
                requestProducer.send(msg);
                NetworkFailoverTest.LOG.info((("Sent: " + (msg.getJMSMessageID())) + ", Failing over"));
                handleTransportFailure(new IOException("Forcing failover from test"));
            }
            TextMessage result = ((TextMessage) (requestConsumer.receive(5000)));
            if (result != null) {
                NetworkFailoverTest.LOG.info(((("Got reply: " + (result.getJMSMessageID())) + ", ") + (result.getText())));
                roundTripComplete.incrementAndGet();
            }
        } 
        NetworkFailoverTest.LOG.info(((((("complete: " + (roundTripComplete.get())) + ", remoteDLQCount: ") + (remoteDLQCount.get())) + ", replyToNonExistDest: ") + (replyToNonExistDest.get())));
        TestCase.assertEquals(((((("complete:" + (roundTripComplete.get())) + ", remoteDLQCount: ") + (remoteDLQCount.get())) + ", replyToNonExistDest: ") + (replyToNonExistDest.get())), NetworkFailoverTest.MESSAGE_COUNT, (((roundTripComplete.get()) + (remoteDLQCount.get())) + (replyToNonExistDest.get())));
    }
}

