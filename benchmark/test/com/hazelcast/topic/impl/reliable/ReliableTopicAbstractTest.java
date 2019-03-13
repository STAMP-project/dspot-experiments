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
package com.hazelcast.topic.impl.reliable;


import ReliableTopicService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.util.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public abstract class ReliableTopicAbstractTest extends HazelcastTestSupport {
    private static final int CAPACITY = 10;

    private ReliableTopicProxy<String> topic;

    private HazelcastInstance local;

    // ============== addMessageListener ==============================
    @Test(expected = NullPointerException.class)
    public void addMessageListener_whenNull() {
        topic.addMessageListener(null);
    }

    @Test
    public void addMessageListener() {
        String id = topic.addMessageListener(new ReliableMessageListenerMock());
        Assert.assertNotNull(id);
    }

    // ============== removeMessageListener ==============================
    @Test(expected = NullPointerException.class)
    public void removeMessageListener_whenNull() {
        topic.removeMessageListener(null);
    }

    @Test
    public void removeMessageListener_whenExisting() {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        String id = topic.addMessageListener(listener);
        boolean removed = topic.removeMessageListener(id);
        Assert.assertTrue(removed);
        topic.publish("1");
        // it should not receive any events
        HazelcastTestSupport.assertTrueDelayed5sec(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(0, listener.objects.size());
            }
        });
    }

    @Test
    public void removeMessageListener_whenNonExisting() {
        boolean result = topic.removeMessageListener(UUID.randomUUID().toString());
        Assert.assertFalse(result);
    }

    @Test
    public void removeMessageListener_whenAlreadyRemoved() {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        String id = topic.addMessageListener(listener);
        topic.removeMessageListener(id);
        boolean result = topic.removeMessageListener(id);
        Assert.assertFalse(result);
        topic.publish("1");
        // it should not receive any events
        HazelcastTestSupport.assertTrueDelayed5sec(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(0, listener.objects.size());
            }
        });
    }

    // ============================================
    @Test
    public void publishSingle() throws InterruptedException {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        topic.addMessageListener(listener);
        final String msg = "foobar";
        topic.publish(msg);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                HazelcastTestSupport.assertContains(listener.objects, msg);
            }
        });
    }

    @Test
    public void publishNull() throws InterruptedException {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        topic.addMessageListener(listener);
        topic.publish(null);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                HazelcastTestSupport.assertContains(listener.objects, null);
            }
        });
    }

    @Test
    public void publishMultiple() throws InterruptedException {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        topic.addMessageListener(listener);
        final List<String> items = new ArrayList<String>();
        for (int k = 0; k < 5; k++) {
            items.add(("" + k));
        }
        for (String item : items) {
            topic.publish(item);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(items, Arrays.asList(listener.objects.toArray()));
            }
        });
    }

    @Test
    public void testMessageFieldSetCorrectly() {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        topic.addMessageListener(listener);
        final long beforePublishTime = Clock.currentTimeMillis();
        topic.publish("foo");
        final long afterPublishTime = Clock.currentTimeMillis();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, listener.messages.size());
                Message<String> message = listener.messages.get(0);
                Assert.assertEquals("foo", message.getMessageObject());
                Member localMember = local.getCluster().getLocalMember();
                Assert.assertEquals(localMember, message.getPublishingMember());
                long actualPublishTime = message.getPublishTime();
                Assert.assertTrue((actualPublishTime >= beforePublishTime));
                Assert.assertTrue((actualPublishTime <= afterPublishTime));
            }
        });
    }

    // makes sure that when a listener is register, we don't see any messages being published before
    // it got registered. We'll only see the messages after it got registered.
    @Test
    public void testAlwaysStartAfterTail() {
        topic.publish("1");
        topic.publish("2");
        topic.publish("3");
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.sleepSeconds(5);
                topic.publish("4");
                topic.publish("5");
                topic.publish("6");
            }
        });
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        topic.addMessageListener(listener);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(Arrays.asList("4", "5", "6"), Arrays.asList(listener.objects.toArray()));
            }
        });
    }

    @Test
    public void statistics() {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        topic.addMessageListener(listener);
        final ITopic<Object> anotherTopic = local.getReliableTopic("anotherTopic");
        final int messageCount = 10;
        final LocalTopicStats localTopicStats = topic.getLocalTopicStats();
        for (int k = 0; k < messageCount; k++) {
            topic.publish("foo");
            anotherTopic.publish("foo");
        }
        Assert.assertEquals(messageCount, localTopicStats.getPublishOperationCount());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(messageCount, localTopicStats.getReceiveOperationCount());
            }
        });
        final ReliableTopicService reliableTopicService = HazelcastTestSupport.getNode(local).nodeEngine.getService(SERVICE_NAME);
        final Map<String, LocalTopicStats> stats = reliableTopicService.getStats();
        Assert.assertEquals(2, stats.size());
        Assert.assertEquals(messageCount, stats.get(topic.getName()).getPublishOperationCount());
        Assert.assertEquals(messageCount, stats.get(topic.getName()).getReceiveOperationCount());
        Assert.assertEquals(messageCount, stats.get(anotherTopic.getName()).getPublishOperationCount());
        Assert.assertEquals(0, stats.get(anotherTopic.getName()).getReceiveOperationCount());
    }

    @Test
    public void testDestroyTopicRemovesStatistics() {
        topic.publish("foobar");
        final ReliableTopicService reliableTopicService = HazelcastTestSupport.getNode(local).nodeEngine.getService(SERVICE_NAME);
        final Map<String, LocalTopicStats> stats = reliableTopicService.getStats();
        Assert.assertEquals(1, stats.size());
        Assert.assertEquals(1, stats.get(topic.getName()).getPublishOperationCount());
        topic.destroy();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(reliableTopicService.getStats().containsKey(topic.getName()));
            }
        });
    }
}

