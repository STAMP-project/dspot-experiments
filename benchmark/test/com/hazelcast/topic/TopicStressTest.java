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
package com.hazelcast.topic;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This test creates a cluster of HazelcastInstances and a bunch of Topics.
 * <p/>
 * On each instance, there is a listener for each topic.
 * <p/>
 * There is a bunch of threads, that selects a random instance with a random topic to publish a message (int) on.
 * <p/>
 * To verify that everything is fine, we expect that the total messages send by each topic is the same as the total
 * sum of messages receives by the topic listeners.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class TopicStressTest extends HazelcastTestSupport {
    public static final int PUBLISH_THREAD_COUNT = 10;

    public static final int NODE_COUNT = 10;

    public static final int TOPIC_COUNT = 10;

    public static final int RUNNING_TIME_SECONDS = 600;

    // if we set this value very low, it could be that events are dropped due to overload of the event queue
    public static final int MAX_PUBLISH_DELAY_MILLIS = 25;

    private HazelcastInstance[] instances;

    private CountDownLatch startLatch;

    private TopicStressTest.PublishThread[] publishThreads;

    private Map<String, List<TopicStressTest.MessageListenerImpl>> listenerMap;

    @Parameterized.Parameter
    public boolean multiThreadingEnabled;

    @Test(timeout = ((TopicStressTest.RUNNING_TIME_SECONDS) * 2) * 1000)
    public void test() throws Exception {
        startLatch.countDown();
        System.out.printf("Test is going to run for %s seconds\n", TopicStressTest.RUNNING_TIME_SECONDS);
        for (TopicStressTest.PublishThread thread : publishThreads) {
            thread.join();
        }
        System.out.println("All publish threads have completed");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int topicIndex = 0; topicIndex < (TopicStressTest.TOPIC_COUNT); topicIndex++) {
                    String topicName = getTopicName(topicIndex);
                    long expected = getExpectedCount(topicName);
                    long actual = getActualCount(topicName);
                    Assert.assertEquals((("Count for topic " + topicName) + " is not the same"), expected, actual);
                }
            }
        });
    }

    private class PublishThread extends Thread {
        private final Random random = new Random();

        private final Map<String, Long> messageCount = new HashMap<String, Long>();

        private final CountDownLatch startLatch;

        private PublishThread(CountDownLatch startLatch) {
            this.startLatch = startLatch;
        }

        @Override
        public void run() {
            try {
                startLatch.await();
                long endTimeMillis = getEndTimeMillis();
                while ((System.currentTimeMillis()) < endTimeMillis) {
                    HazelcastInstance instance = randomInstance();
                    ITopic<Integer> topic = randomTopic(instance);
                    int inc = random.nextInt(100);
                    topic.publish(inc);
                    updateCount(topic, inc);
                    randomSleep();
                } 
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        private long getEndTimeMillis() {
            return (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(TopicStressTest.RUNNING_TIME_SECONDS));
        }

        private void updateCount(ITopic<Integer> topic, int inc) {
            String topicName = topic.getName();
            Long count = messageCount.get(topicName);
            if (count == null) {
                count = 0L;
            }
            count += inc;
            messageCount.put(topicName, count);
        }

        private void randomSleep() {
            try {
                Thread.sleep(random.nextInt(TopicStressTest.MAX_PUBLISH_DELAY_MILLIS));
            } catch (InterruptedException ignored) {
            }
        }

        private ITopic<Integer> randomTopic(HazelcastInstance instance) {
            String randomTopicName = getTopicName(random.nextInt(TopicStressTest.TOPIC_COUNT));
            return instance.getTopic(randomTopicName);
        }

        private HazelcastInstance randomInstance() {
            int index = random.nextInt(instances.length);
            return instances[index];
        }
    }

    private class MessageListenerImpl implements MessageListener<Integer> {
        private final AtomicLong counter = new AtomicLong();

        @Override
        public void onMessage(Message<Integer> message) {
            int inc = message.getMessageObject();
            counter.addAndGet(inc);
        }
    }
}

