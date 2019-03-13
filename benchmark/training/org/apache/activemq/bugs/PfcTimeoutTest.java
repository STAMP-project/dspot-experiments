/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueView;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PfcTimeoutTest {
    private static final Logger LOG = LoggerFactory.getLogger(PfcTimeoutTest.class);

    private static final String TRANSPORT_URL = "tcp://0.0.0.0:0";

    private static final String DESTINATION = "testQ1";

    @Test
    public void testTransactedSendWithTimeout() throws Exception {
        BrokerService broker = createBroker();
        broker.waitUntilStarted();
        CountDownLatch gotTimeoutException = new CountDownLatch(1);
        try {
            int sendTimeout = 5000;
            // send 3 messages that will trigger producer flow and the 3rd send
            // times out after 10 seconds and rollback transaction
            sendMessages(broker, gotTimeoutException, sendTimeout, 3);
            Assert.assertTrue(gotTimeoutException.await((sendTimeout * 2), TimeUnit.MILLISECONDS));
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }

    @Test
    public void testTransactedSendWithTimeoutRollbackUsage() throws Exception {
        BrokerService broker = createBroker();
        broker.waitUntilStarted();
        CountDownLatch gotTimeoutException = new CountDownLatch(1);
        try {
            int sendTimeout = 5000;
            // send 3 messages that will trigger producer flow and the 3rd send
            // times out after 10 seconds and rollback transaction
            int numberOfMessageSent = sendMessages(broker, gotTimeoutException, sendTimeout, 3);
            Assert.assertTrue(gotTimeoutException.await((sendTimeout * 2), TimeUnit.MILLISECONDS));
            // empty queue by consuming contents
            consumeMessages(broker, numberOfMessageSent);
            QueueView queueView = getQueueView(broker, PfcTimeoutTest.DESTINATION);
            long queueSize = queueView.getQueueSize();
            long memoryUsage = queueView.getCursorMemoryUsage();
            PfcTimeoutTest.LOG.info(("queueSize after test = " + queueSize));
            PfcTimeoutTest.LOG.info(("memoryUsage after test = " + memoryUsage));
            Assert.assertEquals("queue size after test ", 0, queueSize);
            Assert.assertEquals("memory size after test ", 0, memoryUsage);
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }
}

