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


import RingbufferService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReliableTopicDestroyTest extends HazelcastTestSupport {
    public static final String RELIABLE_TOPIC_NAME = "foo";

    private ITopic<String> topic;

    private HazelcastInstance member;

    @Test
    public void whenDestroyedThenListenersTerminate() {
        final ReliableMessageListenerMock listener = new ReliableMessageListenerMock();
        topic.addMessageListener(listener);
        HazelcastTestSupport.sleepSeconds(4);
        topic.destroy();
        System.out.println("Destroyed; now a bit of waiting");
        HazelcastTestSupport.sleepSeconds(4);
        listener.clean();
        topic.publish("foo");
        // it should not receive any events.
        HazelcastTestSupport.assertTrueDelayed5sec(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, listener.objects.size());
            }
        });
    }

    @Test
    public void whenDestroyedThenRingbufferRemoved() {
        topic.publish("foo");
        topic.destroy();
        final RingbufferService ringbufferService = HazelcastTestSupport.getNodeEngineImpl(getMember()).getService(SERVICE_NAME);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final String name = (RingbufferService.TOPIC_RB_PREFIX) + (ReliableTopicDestroyTest.RELIABLE_TOPIC_NAME);
                final Map<ObjectNamespace, RingbufferContainer> partitionContainers = ringbufferService.getContainers().get(ringbufferService.getRingbufferPartitionId(name));
                Assert.assertNotNull(partitionContainers);
                Assert.assertFalse(partitionContainers.containsKey(RingbufferService.getRingbufferNamespace(name)));
            }
        });
    }
}

