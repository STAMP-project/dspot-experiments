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
package org.apache.activemq.bugs;


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueView;
import org.junit.Assert;
import org.junit.Test;


public class MessageExpiryTimeDifferenceTest {
    public static final String QUEUE_NAME = "timeout.test";

    private ActiveMQConnection connection;

    private BrokerService broker;

    private String connectionUri;

    /**
     * if the client clock is slightly ahead of the brokers clock a message
     * could be expired on the client. When the expiry is sent to the broker it
     * checks if the message is also considered expired on the broker side.
     *
     * If the broker clock is behind the message could be considered not expired
     * on the broker and not removed from the broker's dispatched list. This
     * leaves the broker reporting a message inflight from the broker's
     * perspective even though the message has been expired on the
     * consumer(client) side
     *
     * The BrokerFlight is used to manipulate the expiration timestamp on the
     * message when it is sent and ack'd from the consumer to simulate a time
     * difference between broker and client in the unit test. This is rather
     * invasive but it it difficult to test this deterministically in a unit
     * test.
     */
    @Test(timeout = 30000)
    public void testInflightCountAfterExpiry() throws Exception {
        connection.start();
        // push message to queue
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("timeout.test");
        MessageProducer producer = session.createProducer(queue);
        TextMessage textMessage = session.createTextMessage(MessageExpiryTimeDifferenceTest.QUEUE_NAME);
        producer.send(textMessage);
        session.close();
        // try to consume message
        session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageConsumer consumer = session.createConsumer(queue);
        final CountDownLatch messageReceived = new CountDownLatch(1);
        // call consume in a separate thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Message message = null;
                try {
                    message = consumer.receive(1000);
                } catch (JMSException e) {
                    Assert.fail();
                }
                // message should be null as it should have expired and the
                // consumer.receive(timeout) should return null.
                Assert.assertNull(message);
                messageReceived.countDown();
            }
        });
        messageReceived.await(20, TimeUnit.SECONDS);
        QueueView queueView = getQueueView(broker, MessageExpiryTimeDifferenceTest.QUEUE_NAME);
        Assert.assertEquals("Should be No inflight messages", 0, queueView.getInFlightCount());
    }
}

