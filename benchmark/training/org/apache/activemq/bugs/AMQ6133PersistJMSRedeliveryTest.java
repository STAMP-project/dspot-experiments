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


import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test loss of message on index rebuild when presistJMSRedelivered is on.
 */
public class AMQ6133PersistJMSRedeliveryTest {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6133PersistJMSRedeliveryTest.class);

    private static final String QUEUE_NAME = "test.queue";

    private BrokerService broker;

    @Test
    public void testPersistJMSRedeliveredMessageLossOnIndexRebuild() throws Exception {
        sendMessages();
        AMQ6133PersistJMSRedeliveryTest.LOG.info("#### Finished sending messages, test starting. ####");
        long msgCount = getProxyToQueue(AMQ6133PersistJMSRedeliveryTest.QUEUE_NAME).getQueueSize();
        final int ITERATIONS = 3;
        // Force some updates
        for (int i = 0; i < ITERATIONS; ++i) {
            AMQ6133PersistJMSRedeliveryTest.LOG.info("Consumer and Rollback iteration: {}", i);
            consumerAndRollback(i);
        }
        // Allow GC to run at least once.
        TimeUnit.SECONDS.sleep(20);
        restart();
        Assert.assertEquals(msgCount, getProxyToQueue(AMQ6133PersistJMSRedeliveryTest.QUEUE_NAME).getQueueSize());
        restartWithRecovery(getPersistentDir());
        Assert.assertEquals(msgCount, getProxyToQueue(AMQ6133PersistJMSRedeliveryTest.QUEUE_NAME).getQueueSize());
    }
}

