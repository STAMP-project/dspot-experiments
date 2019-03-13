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
package org.apache.activemq;


import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerReceiveWithTimeoutTest {
    private ActiveMQConnection connection;

    private BrokerService broker;

    private String connectionUri;

    /**
     * Test to check if consumer thread wakes up inside a receive(timeout) after
     * a message is dispatched to the consumer
     *
     * @throws javax.jms.JMSException
     * 		
     */
    @Test(timeout = 30000)
    public void testConsumerReceiveBeforeMessageDispatched() throws JMSException {
        connection.start();
        final Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final Queue queue = session.createQueue("test");
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    // wait for 10 seconds to allow consumer.receive to be run
                    // first
                    Thread.sleep(10000);
                    MessageProducer producer = session.createProducer(queue);
                    producer.send(session.createTextMessage("Hello"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        // Consume the message...
        MessageConsumer consumer = session.createConsumer(queue);
        Message msg = consumer.receive(60000);
        Assert.assertNotNull(msg);
        session.close();
    }

    /**
     * check if receive(timeout) does timeout when prefetch=0 and redeliveries=0
     * <p/>
     * send a message.
     * consume and rollback to ensure redeliverCount is incremented
     * try to consume message with a timeout.
     */
    @Test(timeout = 20000)
    public void testConsumerReceivePrefetchZeroRedeliveryZero() throws Exception {
        connection.start();
        // push message to queue
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("test.prefetch.zero");
        MessageProducer producer = session.createProducer(queue);
        TextMessage textMessage = session.createTextMessage("test Message");
        producer.send(textMessage);
        session.close();
        // consume and rollback - increase redelivery counter on message
        session = connection.createSession(true, SESSION_TRANSACTED);
        MessageConsumer consumer = session.createConsumer(queue);
        Message message = consumer.receive(2000);
        Assert.assertNotNull(message);
        session.rollback();
        session.close();
        // Reconnect with zero prefetch and zero redeliveries allowed.
        connection.close();
        connection = createConnection();
        connection.getPrefetchPolicy().setQueuePrefetch(0);
        connection.getRedeliveryPolicy().setMaximumRedeliveries(0);
        connection.start();
        // try consume with timeout - expect it to timeout and return NULL message
        session = connection.createSession(true, SESSION_TRANSACTED);
        consumer = session.createConsumer(queue);
        message = consumer.receive(3000);
        Assert.assertNull(message);
    }
}

