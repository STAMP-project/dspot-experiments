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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Stomp.NULL;


public class StompLoadTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompLoadTest.class);

    private static final int TASK_COUNT = 100;

    private static final int MSG_COUNT = 250;// AMQ-3819: Above 250 or so and the CPU goes bonkers with NOI+SSL.


    private ExecutorService executor;

    private CountDownLatch started;

    private CountDownLatch ready;

    private AtomicInteger receiveCount;

    @Test(timeout = (5 * 60) * 1000)
    public void testStompUnloadLoad() throws Exception {
        final List<StompConnection> taskConnections = new ArrayList<>();
        for (int i = 0; i < (StompLoadTest.TASK_COUNT); ++i) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    StompLoadTest.LOG.debug("Receive Thread Connecting to Broker.");
                    int numReceived = 0;
                    StompConnection connection = new StompConnection();
                    try {
                        stompConnect(connection);
                        connection.connect("system", "manager");
                    } catch (Exception e) {
                        StompLoadTest.LOG.error(("Caught Exception while connecting: " + (e.getMessage())));
                    }
                    taskConnections.add(connection);
                    try {
                        for (int i = 0; i < 10; i++) {
                            connection.subscribe(("/queue/test-" + i), "auto");
                            connection.subscribe(("/topic/test-" + i), "auto");
                        }
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("activemq.prefetchSize", "1");
                        connection.subscribe(("/topic/" + (getTopicName())), "auto", headers);
                        ready.await();
                        // Now that the main test thread is ready we wait a bit to let the tasks
                        // all subscribe and the CPU to settle a bit.
                        TimeUnit.SECONDS.sleep(3);
                        started.countDown();
                        while ((receiveCount.get()) != ((StompLoadTest.TASK_COUNT) * (StompLoadTest.MSG_COUNT))) {
                            // Read Timeout ends this task, we override the default here since there
                            // are so many threads running and we don't know how slow the test box is.
                            StompFrame frame = connection.receive(TimeUnit.SECONDS.toMillis(60));
                            Assert.assertNotNull(frame);
                            numReceived++;
                            if (((StompLoadTest.LOG.isDebugEnabled()) && ((numReceived % 50) == 0)) || (numReceived == (StompLoadTest.MSG_COUNT))) {
                                StompLoadTest.LOG.debug(("Receiver thread got message: " + (frame.getHeaders().get("message-id"))));
                            }
                            receiveCount.incrementAndGet();
                        } 
                    } catch (Exception e) {
                        if (numReceived != (StompLoadTest.MSG_COUNT)) {
                            StompLoadTest.LOG.warn(((("Receive task caught exception after receipt of [" + numReceived) + "] messages: ") + (e.getMessage())));
                        }
                    }
                }
            });
        }
        ready.countDown();
        Assert.assertTrue("Timed out waiting for receivers to start.", started.await(5, TimeUnit.MINUTES));
        String frame;
        // Lets still wait a bit to make sure all subscribers get a fair shake at
        // getting online before we send.  Account for slow Hudson machines
        TimeUnit.SECONDS.sleep(5);
        for (int ix = 0; ix < (StompLoadTest.MSG_COUNT); ix++) {
            frame = ((((((("SEND\n" + "destination:/topic/") + (getTopicName())) + "\nid:") + ix) + "\ncontent-length:5") + " \n\n") + "\u0001\u0002\u0000\u0004\u0005") + (NULL);
            stompConnection.sendFrame(frame);
        }
        StompLoadTest.LOG.info((("All " + (StompLoadTest.MSG_COUNT)) + " message have been sent, awaiting receipt."));
        Assert.assertTrue(((("Should get [" + ((StompLoadTest.TASK_COUNT) * (StompLoadTest.MSG_COUNT))) + "] message but was: ") + (receiveCount.get())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (receiveCount.get()) == ((StompLoadTest.TASK_COUNT) * (StompLoadTest.MSG_COUNT));
            }
        }, TimeUnit.MINUTES.toMillis(10)));
        StompLoadTest.LOG.info("Test Completed and all messages received, shutting down.");
        for (StompConnection taskConnection : taskConnections) {
            try {
                taskConnection.disconnect();
                taskConnection.close();
            } catch (Exception ex) {
            }
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
        stompDisconnect();
    }
}

