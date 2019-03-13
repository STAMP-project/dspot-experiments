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
import TopicOverloadPolicy.DISCARD_NEWEST;
import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ITopic;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.topic.TopicOverloadPolicy;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReliableTopicCreateTest extends HazelcastTestSupport {
    @Test
    public void testConstruction() {
        HazelcastInstance hz = createHazelcastInstance();
        RingbufferService ringbufferService = HazelcastTestSupport.getNodeEngineImpl(hz).getService(SERVICE_NAME);
        ReliableTopicProxy<String> topic = ((ReliableTopicProxy<String>) (hz.<String>getReliableTopic("foo")));
        final String name = (RingbufferService.TOPIC_RB_PREFIX) + "foo";
        Ringbuffer ringbuffer = hz.getRingbuffer(name);
        Assert.assertSame(ringbuffer, topic.ringbuffer);
        // make sure the ringbuffer and topic are hooked up correctly
        topic.publish("item1");
        topic.publish("item2");
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals(1, ringbuffer.tailSequence());
        final Map<ObjectNamespace, RingbufferContainer> containers = ringbufferService.getContainers().get(ringbufferService.getRingbufferPartitionId(name));
        final ObjectNamespace ns = RingbufferService.getRingbufferNamespace(ringbuffer.getName());
        Assert.assertEquals(1, containers.size());
        Assert.assertTrue(containers.containsKey(ns));
    }

    @Test
    public void testRingbufferConfiguration() {
        Config config = new Config();
        RingbufferConfig rbConfig = new RingbufferConfig("foo").setCapacity(21);
        config.addRingBufferConfig(rbConfig);
        HazelcastInstance hz = createHazelcastInstance(config);
        RingbufferService ringbufferService = HazelcastTestSupport.getNodeEngineImpl(hz).getService(SERVICE_NAME);
        ReliableTopicProxy topic = ((ReliableTopicProxy) (hz.getReliableTopic("foo")));
        Ringbuffer ringbuffer = hz.getRingbuffer(((RingbufferService.TOPIC_RB_PREFIX) + "foo"));
        Assert.assertSame(ringbuffer, topic.ringbuffer);
        Assert.assertEquals(21, ringbuffer.capacity());
        // triggers the creation
        ringbuffer.size();
        final Map<ObjectNamespace, RingbufferContainer> containers = ringbufferService.getContainers().get(ringbufferService.getRingbufferPartitionId(ringbuffer.getName()));
        final ObjectNamespace ns = RingbufferService.getRingbufferNamespace(ringbuffer.getName());
        Assert.assertEquals(1, containers.size());
        Assert.assertTrue(containers.containsKey(ns));
        RingbufferContainer container = containers.get(ns);
        Assert.assertEquals(rbConfig.getCapacity(), container.getConfig().getCapacity());
    }

    @Test
    public void testWildcardConfig() {
        Config config = new Config();
        config.addRingBufferConfig(new RingbufferConfig("foo*").setCapacity(10));
        config.addReliableTopicConfig(new ReliableTopicConfig("foo*").setTopicOverloadPolicy(TopicOverloadPolicy.DISCARD_NEWEST));
        HazelcastInstance hz = createHazelcastInstance(config);
        RingbufferService ringbufferService = HazelcastTestSupport.getNodeEngineImpl(hz).getService(SERVICE_NAME);
        ReliableTopicProxy<String> topic = ((ReliableTopicProxy<String>) (hz.<String>getReliableTopic("foo")));
        Ringbuffer ringbuffer = topic.ringbuffer;
        topic.publish("foo");
        ReliableTopicProxy proxy = HazelcastTestSupport.assertInstanceOf(ReliableTopicProxy.class, topic);
        Assert.assertEquals(proxy.overloadPolicy, DISCARD_NEWEST);
        final ConcurrentMap<Integer, Map<ObjectNamespace, RingbufferContainer>> containers = ringbufferService.getContainers();
        Assert.assertEquals(1, containers.size());
        final Map<ObjectNamespace, RingbufferContainer> partitionContainers = containers.get(ringbufferService.getRingbufferPartitionId(ringbuffer.getName()));
        final ObjectNamespace ns = RingbufferService.getRingbufferNamespace(ringbuffer.getName());
        Assert.assertTrue(partitionContainers.containsKey(ns));
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals(0, ringbuffer.tailSequence());
        Assert.assertEquals(10, ringbuffer.capacity());
    }

    @Test
    public void testConfiguredListenerInstance() {
        final ReliableMessageListenerMock messageListener = new ReliableMessageListenerMock();
        Config config = new Config();
        config.addReliableTopicConfig(new ReliableTopicConfig("foo*").addMessageListenerConfig(new ListenerConfig(messageListener)));
        HazelcastInstance hz = createHazelcastInstance(config);
        ITopic<String> topic = hz.getReliableTopic("foo");
        ReliableTopicProxy proxy = HazelcastTestSupport.assertInstanceOf(ReliableTopicProxy.class, topic);
        Assert.assertEquals(1, proxy.runnersMap.size());
        topic.publish("item");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                HazelcastTestSupport.assertContains(messageListener.objects, "item");
            }
        });
    }

    @Test
    public void testConfiguredListenerInstanceHazelcastInstanceAware() {
        final ReliableTopicCreateTest.InstanceAwareReliableMessageListenerMock messageListener = new ReliableTopicCreateTest.InstanceAwareReliableMessageListenerMock();
        Config config = new Config();
        config.addReliableTopicConfig(new ReliableTopicConfig("foo*").addMessageListenerConfig(new ListenerConfig(messageListener)));
        HazelcastInstance hz = createHazelcastInstance(config);
        ITopic<String> topic = hz.getReliableTopic("foo");
        ReliableTopicProxy proxy = HazelcastTestSupport.assertInstanceOf(ReliableTopicProxy.class, topic);
        Assert.assertEquals(1, proxy.runnersMap.size());
        Assert.assertNotNull(messageListener.hz);
        topic.publish("item");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                HazelcastTestSupport.assertContains(messageListener.objects, "item");
            }
        });
    }

    static class InstanceAwareReliableMessageListenerMock extends ReliableMessageListenerMock implements HazelcastInstanceAware {
        HazelcastInstance hz;

        @Override
        public void setHazelcastInstance(HazelcastInstance hz) {
            this.hz = hz;
        }
    }

    @Test
    public void testConfiguredListenerClass() {
        Config config = new Config();
        config.addReliableTopicConfig(new ReliableTopicConfig("foo*").addMessageListenerConfig(new ListenerConfig(ReliableMessageListenerMock.class.getName())));
        HazelcastInstance hz = createHazelcastInstance(config);
        ITopic topic = hz.getReliableTopic("foo");
        ReliableTopicProxy proxy = HazelcastTestSupport.assertInstanceOf(ReliableTopicProxy.class, topic);
        // check there is one listener.
        Assert.assertEquals(1, proxy.runnersMap.size());
        // check that the listener is of the right class.
        MessageRunner runner = ((MessageRunner) (proxy.runnersMap.values().iterator().next()));
        HazelcastTestSupport.assertInstanceOf(ReliableMessageListenerMock.class, runner.listener);
    }

    @Test(expected = HazelcastException.class)
    public void testConfiguredListenerClassNotMessageListener() {
        Config config = new Config();
        config.addReliableTopicConfig(new ReliableTopicConfig("foo*").addMessageListenerConfig(new ListenerConfig(String.class.getName())));
        HazelcastInstance hz = createHazelcastInstance(config);
        hz.getReliableTopic("foo");
        Assert.fail();
    }

    @Test(expected = HazelcastException.class)
    public void testConfiguredListenerClassNotExist() {
        Config config = new Config();
        config.addReliableTopicConfig(new ReliableTopicConfig("foo*").addMessageListenerConfig(new ListenerConfig("kfosajdajdksajdj")));
        HazelcastInstance hz = createHazelcastInstance(config);
        hz.getReliableTopic("foo");
        Assert.fail();
    }

    @Test
    public void testConfiguredListenerClassAndHazelcastInstanceAware() {
        Config config = new Config();
        config.addReliableTopicConfig(new ReliableTopicConfig("foo*").addMessageListenerConfig(new ListenerConfig(ReliableTopicCreateTest.InstanceAwareReliableMessageListenerMock.class.getName())));
        HazelcastInstance hz = createHazelcastInstance(config);
        ITopic topic = hz.getReliableTopic("foo");
        ReliableTopicProxy proxy = HazelcastTestSupport.assertInstanceOf(ReliableTopicProxy.class, topic);
        // check there is one listener.
        Assert.assertEquals(1, proxy.runnersMap.size());
        // check that the listener is of the right class.
        MessageRunner runner = ((MessageRunner) (proxy.runnersMap.values().iterator().next()));
        ReliableTopicCreateTest.InstanceAwareReliableMessageListenerMock mock = HazelcastTestSupport.assertInstanceOf(ReliableTopicCreateTest.InstanceAwareReliableMessageListenerMock.class, runner.listener);
        Assert.assertNotNull(mock.hz);
    }
}

