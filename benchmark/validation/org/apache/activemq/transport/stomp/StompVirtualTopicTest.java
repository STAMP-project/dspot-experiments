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
package org.apache.activemq.transport.stomp;


import Stomp.Headers.Response.RECEIPT_ID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.management.ObjectName;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


public class StompVirtualTopicTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompVirtualTopicTest.class);

    private static final int NUM_MSGS = 30000;

    private String failMsg = null;

    @Test(timeout = 90000)
    public void testStompOnVirtualTopics() throws Exception {
        StompVirtualTopicTest.LOG.info("Running Stomp Producer");
        stompConnect();
        StompVirtualTopicTest.StompConsumer consumerWorker = new StompVirtualTopicTest.StompConsumer(this);
        Thread consumer = new Thread(consumerWorker);
        StringBuilder payload = new StringBuilder();
        for (int i = 0; i < 128; ++i) {
            payload.append('*');
        }
        consumer.start();
        consumerWorker.awaitStartCompleted();
        Thread.sleep(500);
        stompConnection.sendFrame((("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (NULL)));
        StompFrame frame = stompConnection.receive();
        Assert.assertTrue(frame.toString().startsWith("CONNECTED"));
        for (int i = 0; i < ((StompVirtualTopicTest.NUM_MSGS) - 1); i++) {
            stompConnection.send("/topic/VirtualTopic.FOO", ((("Hello World {" + (i + 1)) + "} ") + (payload.toString())));
        }
        StompVirtualTopicTest.LOG.info("Sending last packet with receipt header");
        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put("receipt", "1234");
        stompConnection.appendHeaders(headers);
        String msg = ((("SEND\n" + ((("destination:/topic/VirtualTopic.FOO\n" + "receipt: msg-1\n") + "\n\n") + "Hello World {")) + ((StompVirtualTopicTest.NUM_MSGS) - 1)) + "}") + (NULL);
        stompConnection.sendFrame(msg);
        msg = stompConnection.receiveFrame();
        Assert.assertTrue(msg.contains("RECEIPT"));
        stompConnection.disconnect();
        TimeUnit.MILLISECONDS.sleep(100);
        stompConnection.close();
        StompVirtualTopicTest.LOG.info("Stomp Producer finished. Waiting for consumer to join.");
        // Wait for consumer to shut down
        consumer.join(45000);
        StompVirtualTopicTest.LOG.info("Test finished.");
        // check if consumer set failMsg, then let the test fail.
        if (null != (failMsg)) {
            StompVirtualTopicTest.LOG.error(failMsg);
            Assert.fail(failMsg);
        }
    }

    class StompConsumer implements Runnable {
        final Logger log = LoggerFactory.getLogger(StompVirtualTopicTest.StompConsumer.class);

        private StompVirtualTopicTest parent = null;

        private final CountDownLatch latch = new CountDownLatch(1);

        private final HashSet<String> received = new HashSet<String>();

        private final HashSet<String> dups = new HashSet<String>();

        public StompConsumer(StompVirtualTopicTest ref) {
            parent = ref;
        }

        public void awaitStartCompleted() {
            try {
                this.latch.await();
            } catch (InterruptedException e) {
            }
        }

        @Override
        public void run() {
            StompVirtualTopicTest.LOG.info("Running Stomp Consumer");
            StompConnection stompConnection = new StompConnection();
            int counter = 0;
            try {
                stompConnection.open("localhost", StompVirtualTopicTest.this.port);
                stompConnection.sendFrame((("CONNECT\n" + ("login:system\n" + "passcode:manager\n\n")) + (Stomp.NULL)));
                StompFrame frame = stompConnection.receive();
                Assert.assertTrue(frame.toString().startsWith("CONNECTED"));
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("receipt", "sub-1");
                stompConnection.subscribe("/queue/Consumer.A.VirtualTopic.FOO", "auto", headers);
                String receipt = stompConnection.receiveFrame();
                Assert.assertTrue("Should have read a receipt for subscribe", receipt.contains("RECEIPT"));
                Assert.assertTrue("Receipt contains receipt-id", ((receipt.indexOf(RECEIPT_ID)) >= 0));
                latch.countDown();
                for (counter = 0; counter < (StompVirtualTopicTest.NUM_MSGS); counter++) {
                    frame = stompConnection.receive(15000);
                    log.trace(("Received msg with content: " + (frame.getBody())));
                    if (!(received.add(frame.getBody()))) {
                        dups.add(frame.getBody());
                    }
                }
                // another receive should not return any more msgs
                try {
                    frame = stompConnection.receive(3000);
                    Assert.assertNull(frame);
                } catch (Exception e) {
                    StompVirtualTopicTest.LOG.info(((("Correctly received " + e) + " while trying to consume an additional msg.") + " This is expected as the queue should be empty now."));
                }
                // in addition check QueueSize using JMX
                long queueSize = reportQueueStatistics();
                if (queueSize != 0) {
                    parent.setFail("QueueSize not 0 after test has finished.");
                }
                log.debug((((("Stomp Consumer Received " + counter) + " of ") + (StompVirtualTopicTest.NUM_MSGS)) + " messages. Check QueueSize in JMX and try to browse the queue."));
                if (!(dups.isEmpty())) {
                    for (String msg : dups) {
                        StompVirtualTopicTest.LOG.debug(("Received duplicate message: " + msg));
                    }
                    parent.setFail((((("Received " + (StompVirtualTopicTest.NUM_MSGS)) + " messages but ") + (dups.size())) + " were dups."));
                }
            } catch (Exception ex) {
                log.error(((((ex.getMessage()) + " after consuming ") + counter) + " msgs."));
                try {
                    reportQueueStatistics();
                } catch (Exception e) {
                }
                parent.setFail((((("Stomp Consumer received " + counter) + " of ") + (StompVirtualTopicTest.NUM_MSGS)) + " messages. Check QueueSize in JMX and try to browse the queue."));
            } finally {
                try {
                    stompConnection.disconnect();
                    TimeUnit.MILLISECONDS.sleep(100);
                    stompConnection.close();
                } catch (Exception e) {
                    log.error("unexpected exception on sleep", e);
                }
            }
            log.info("Test Finished.");
        }

        private long reportQueueStatistics() throws Exception {
            ObjectName queueViewMBeanName = new ObjectName(("org.apache.activemq:destinationType=Queue" + (",destinationName=Consumer.A.VirtualTopic.FOO" + ",type=Broker,brokerName=localhost")));
            QueueViewMBean queue = ((QueueViewMBean) (brokerService.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
            StompVirtualTopicTest.LOG.info(((((((("Consumer.A.VirtualTopic.FOO Inflight: " + (queue.getInFlightCount())) + ", enqueueCount: ") + (queue.getEnqueueCount())) + ", dequeueCount: ") + (queue.getDequeueCount())) + ", dispatchCount: ") + (queue.getDispatchCount())));
            return queue.getQueueSize();
        }
    }
}

