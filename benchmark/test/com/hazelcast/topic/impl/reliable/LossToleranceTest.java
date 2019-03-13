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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LossToleranceTest extends HazelcastTestSupport {
    private static final String RELIABLE_TOPIC_NAME = "foo";

    private ReliableTopicProxy<String> topic;

    private Ringbuffer<ReliableTopicMessage> ringbuffer;

    private HazelcastInstance topicOwnerInstance;

    private HazelcastInstance topicBackupInstance;

    @Test
    public void whenNotLossTolerant_thenTerminate() {
        ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        listener.initialSequence = 0;
        listener.isLossTolerant = false;
        topic.publish("foo");
        // we add so many items that the items the listener wants to listen to, doesn't exist anymore
        for (; ;) {
            topic.publish("item");
            if ((ringbuffer.headSequence()) > (listener.initialSequence)) {
                break;
            }
        }
        topic.addMessageListener(listener);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(topic.runnersMap.isEmpty());
            }
        });
    }

    @Test
    public void whenLossTolerant_thenContinue() {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        listener.initialSequence = 0;
        listener.isLossTolerant = true;
        // we add so many items that the items the listener wants to listen to, doesn't exist anymore
        for (; ;) {
            topic.publish("item");
            if ((ringbuffer.headSequence()) > (listener.initialSequence)) {
                break;
            }
        }
        topic.addMessageListener(listener);
        topic.publish("newItem");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                HazelcastTestSupport.assertContains(listener.objects, "newItem");
                Assert.assertFalse(topic.runnersMap.isEmpty());
            }
        });
    }

    @Test
    public void whenLossTolerant_andOwnerCrashes_thenContinue() {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        listener.isLossTolerant = true;
        topic.addMessageListener(listener);
        topic.publish("item1");
        topic.publish("item2");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                HazelcastTestSupport.assertContains(listener.objects, "item1");
                HazelcastTestSupport.assertContains(listener.objects, "item2");
            }
        });
        TestUtil.terminateInstance(topicOwnerInstance);
        topic.publish("newItem");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                HazelcastTestSupport.assertContains(listener.objects, "newItem");
                Assert.assertFalse(topic.runnersMap.isEmpty());
            }
        });
    }
}

