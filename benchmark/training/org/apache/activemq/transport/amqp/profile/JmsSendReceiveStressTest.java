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
package org.apache.activemq.transport.amqp.profile;


import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.transport.amqp.JMSClientTestSupport;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore("Use for profiling and memory testing")
public class JmsSendReceiveStressTest extends JMSClientTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsSendReceiveStressTest.class);

    public static final int PAYLOAD_SIZE = 64 * 1024;

    private final byte[] payload = new byte[JmsSendReceiveStressTest.PAYLOAD_SIZE];

    private final int parallelProducer = 1;

    private final int parallelConsumer = 1;

    private final Vector<Throwable> exceptions = new Vector<Throwable>();

    private JmsConnectionFactory factory;

    private final long NUM_SENDS = 1000000;

    @Test
    public void testProduceConsume() throws Exception {
        factory = new JmsConnectionFactory(getAmqpURI(getAmqpConnectionURIOptions()));
        factory.setForceAsyncAcks(true);
        factory.setForceAsyncSend(false);
        factory.setForceSyncSend(false);
        final AtomicLong sharedSendCount = new AtomicLong(NUM_SENDS);
        final AtomicLong sharedReceiveCount = new AtomicLong(NUM_SENDS);
        Thread.sleep(2000);
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(((parallelConsumer) + (parallelProducer)));
        for (int i = 0; i < (parallelConsumer); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        consumeMessages(sharedReceiveCount);
                    } catch (Throwable e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        for (int i = 0; i < (parallelProducer); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        publishMessages(sharedSendCount);
                    } catch (Throwable e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.MINUTES);
        Assert.assertTrue("Producers done in time", executorService.isTerminated());
        Assert.assertTrue(("No exceptions: " + (exceptions)), exceptions.isEmpty());
        double duration = (System.currentTimeMillis()) - start;
        JmsSendReceiveStressTest.LOG.info((("Duration:            " + duration) + "ms"));
        JmsSendReceiveStressTest.LOG.info((("Rate:                " + (((NUM_SENDS) * 1000) / duration)) + "m/s"));
    }
}

