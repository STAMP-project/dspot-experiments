/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.topic;


import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.logging.ILogger;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestThread;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class ClientReliableTopicStressTest extends HazelcastTestSupport {
    private ILogger logger;

    private final AtomicBoolean stop = new AtomicBoolean();

    private ITopic<Long> topic;

    @Test
    public void test() throws InterruptedException {
        final ClientReliableTopicStressTest.StressMessageListener listener1 = new ClientReliableTopicStressTest.StressMessageListener(1);
        topic.addMessageListener(listener1);
        final ClientReliableTopicStressTest.StressMessageListener listener2 = new ClientReliableTopicStressTest.StressMessageListener(2);
        topic.addMessageListener(listener2);
        sleepSeconds(5);
        final ClientReliableTopicStressTest.ProduceThread produceThread = new ClientReliableTopicStressTest.ProduceThread();
        start();
        logger.info("Starting test");
        sleepAndStop(stop, TimeUnit.SECONDS.toSeconds(30));
        logger.info("Completed");
        assertSucceedsEventually();
        logger.info(("Number of items produced: " + (produceThread.send)));
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(produceThread.send, listener1.received);
                Assert.assertEquals(produceThread.send, listener2.received);
                Assert.assertEquals(0, listener1.failures);
                Assert.assertEquals(0, listener2.failures);
            }
        });
        logger.info("Done");
    }

    public class ProduceThread extends TestThread {
        private volatile long send = 0;

        @Override
        public void onError(Throwable t) {
            stop.set(true);
        }

        @Override
        public void doRun() throws Throwable {
            while (!(stop.get())) {
                topic.publish(send);
                (send)++;
                if (((send) % 10000) == 0) {
                    logger.info(("Publishing: " + (send)));
                }
            } 
        }
    }

    public class StressMessageListener implements MessageListener<Long> {
        private final int id;

        private long received = 0;

        private long failures = 0;

        public StressMessageListener(int id) {
            this.id = id;
        }

        @Override
        public void onMessage(Message<Long> message) {
            if (!(message.getMessageObject().equals(received))) {
                (failures)++;
            }
            if (((received) % 10000) == 0) {
                logger.info((((toString()) + " is at: ") + (received)));
            }
            (received)++;
        }

        @Override
        public String toString() {
            return "StressMessageListener-" + (id);
        }
    }
}

