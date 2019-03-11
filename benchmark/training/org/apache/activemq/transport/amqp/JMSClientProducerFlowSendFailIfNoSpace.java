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
package org.apache.activemq.transport.amqp;


import Session.CLIENT_ACKNOWLEDGE;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.Assert;
import org.junit.Test;


public class JMSClientProducerFlowSendFailIfNoSpace extends JMSClientTestSupport {
    // used to test sendFailIfNoSpace on SystemUsage
    protected final AtomicBoolean gotResourceException = new AtomicBoolean(false);

    @Test(timeout = 60000)
    public void testPubisherRecoverAfterBlock() throws Exception {
        connection = createConnection();
        final Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        final Queue queueA = session.createQueue(name.getMethodName());
        final MessageProducer producer = session.createProducer(queueA);
        final AtomicBoolean keepGoing = new AtomicBoolean(true);
        Thread thread = new Thread("Filler") {
            @Override
            public void run() {
                while (keepGoing.get()) {
                    try {
                        producer.send(session.createTextMessage("Test message"));
                        if (gotResourceException.get()) {
                            // do not flood the broker with requests when full as we are
                            // sending async and they will be limited by the network buffers
                            Thread.sleep(200);
                        }
                    } catch (Exception e) {
                        // with async send, there will be no exceptions
                        AmqpTestSupport.LOG.info("Caught excepted exception: {}", e.getMessage());
                    }
                } 
            }
        };
        thread.start();
        waitForBlockedOrResourceLimit(new AtomicBoolean(false));
        // resourceException on second message, resumption if we
        // can receive 10
        MessageConsumer consumer = session.createConsumer(queueA);
        TextMessage msg;
        for (int idx = 0; idx < 10; ++idx) {
            msg = ((TextMessage) (consumer.receive(500)));
            if (msg != null) {
                msg.acknowledge();
            }
        }
        keepGoing.set(false);
    }

    @Test(timeout = 60000)
    public void testPubisherRecoverAfterBlockWithSyncSend() throws Exception {
        connection = createConnection(false, false);
        final Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        final Queue queueA = session.createQueue(name.getMethodName());
        final MessageProducer producer = session.createProducer(queueA);
        final AtomicBoolean keepGoing = new AtomicBoolean(true);
        final AtomicInteger exceptionCount = new AtomicInteger(0);
        Thread thread = new Thread("Filler") {
            @Override
            public void run() {
                while (keepGoing.get()) {
                    try {
                        producer.send(session.createTextMessage("Test message"));
                    } catch (JMSException jmsEx) {
                        AmqpTestSupport.LOG.debug("Client caught error: {} {}", jmsEx.getClass().getName(), jmsEx.getMessage());
                        gotResourceException.set(true);
                        exceptionCount.incrementAndGet();
                    }
                } 
            }
        };
        thread.start();
        waitForBlockedOrResourceLimit();
        // resourceException on second message, resumption if we
        // can receive 10
        MessageConsumer consumer = session.createConsumer(queueA);
        TextMessage msg;
        for (int idx = 0; idx < 10; ++idx) {
            msg = ((TextMessage) (consumer.receive(500)));
            if (msg != null) {
                msg.acknowledge();
            }
        }
        Assert.assertTrue("we were blocked at least 5 times", (5 < (exceptionCount.get())));
        keepGoing.set(false);
    }
}

