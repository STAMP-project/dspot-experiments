/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import java.util.ArrayList;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ6094Test {
    private static Logger LOG = LoggerFactory.getLogger(AMQ6094Test.class);

    private BrokerService brokerService;

    private String connectionUri;

    @Test
    public void testQueueMemoryUsage() throws Exception {
        final ArrayList<AMQ6094Test.ThreadSlot> producerThreads = new ArrayList<>();
        final ArrayList<AMQ6094Test.ThreadSlot> consumerThreads = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            producerThreads.add(AMQ6094Test.runInThread(new AMQ6094Test.UnsafeRunnable() {
                @Override
                public void run() throws Exception {
                    AMQ6094Test.producer(connectionUri, "queueA");
                }
            }));

        for (int i = 0; i < 4; i++)
            consumerThreads.add(AMQ6094Test.runInThread(new AMQ6094Test.UnsafeRunnable() {
                @Override
                public void run() throws Exception {
                    AMQ6094Test.consumer(connectionUri, "queueA", 2500);
                }
            }));

        // kill and restart random threads
        for (int count = 0; count < 10; count++) {
            Thread.sleep(5000);
            final int i = ((int) ((Math.random()) * (consumerThreads.size())));
            final AMQ6094Test.ThreadSlot slot = consumerThreads.get(i);
            slot.thread.interrupt();
            consumerThreads.remove(i);
            consumerThreads.add(AMQ6094Test.runInThread(slot.runnable));
            Queue queue = ((Queue) (brokerService.getDestination(new ActiveMQQueue("queueA"))));
            AMQ6094Test.LOG.info(("cursorMemoryUsage: " + (queue.getMessages().getSystemUsage().getMemoryUsage().getUsage())));
            AMQ6094Test.LOG.info(("messagesStat: " + (queue.getDestinationStatistics().getMessages().getCount())));
        }
        // verify usage
        Queue queue = ((Queue) (brokerService.getDestination(new ActiveMQQueue("queueA"))));
        AMQ6094Test.LOG.info(("cursorMemoryUsage: " + (queue.getMessages().getSystemUsage().getMemoryUsage().getUsage())));
        AMQ6094Test.LOG.info(("messagesStat: " + (queue.getDestinationStatistics().getMessages().getCount())));
        // drain the queue
        for (AMQ6094Test.ThreadSlot threadSlot : producerThreads) {
            threadSlot.thread.interrupt();
            threadSlot.thread.join(4000);
        }
        for (AMQ6094Test.ThreadSlot threadSlot : consumerThreads) {
            threadSlot.thread.interrupt();
            threadSlot.thread.join(4000);
        }
        AMQ6094Test.consumer(connectionUri, "queueA", 2500, true);
        AMQ6094Test.LOG.info(("After drain, cursorMemoryUsage: " + (queue.getMessages().getSystemUsage().getMemoryUsage().getUsage())));
        AMQ6094Test.LOG.info(("messagesStat: " + (queue.getDestinationStatistics().getMessages().getCount())));
        Assert.assertEquals("Queue memory usage to 0", 0, queue.getMessages().getSystemUsage().getMemoryUsage().getUsage());
    }

    private static interface UnsafeRunnable {
        public void run() throws Exception;
    }

    public static class ThreadSlot {
        private AMQ6094Test.UnsafeRunnable runnable;

        private Thread thread;
    }
}

