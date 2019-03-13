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
package com.hazelcast.instance;


import LifecycleState.SHUTDOWN;
import LifecycleState.SHUTTING_DOWN;
import LifecycleState.STARTED;
import LifecycleState.STARTING;
import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class LifeCycleListenerTest extends HazelcastTestSupport {
    @Test(timeout = 15 * 1000)
    public void testListenerNoDeadLock() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final CountDownLatch latch = new CountDownLatch(1);
        final Config config = new Config();
        config.addListenerConfig(new ListenerConfig(new LifeCycleListenerTest.MyLifecycleListener(latch)));
        factory.newHazelcastInstance(config);
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testListenerInvocationWhenNodeStarts() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final Config config = new Config();
        final LifeCycleListenerTest.EventCountingListener listener = new LifeCycleListenerTest.EventCountingListener();
        config.addListenerConfig(new ListenerConfig(listener));
        factory.newHazelcastInstance(config);
        Assert.assertEquals(STARTING, listener.events.get(0));
        Assert.assertEquals(STARTED, listener.events.get(1));
    }

    @Test
    public void testListenerInvocationWhenNodeShutsDown() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final Config config = new Config();
        final LifeCycleListenerTest.EventCountingListener listener = new LifeCycleListenerTest.EventCountingListener();
        config.addListenerConfig(new ListenerConfig(listener));
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        listener.events.clear();
        instance.getLifecycleService().shutdown();
        Assert.assertEquals(SHUTTING_DOWN, listener.events.get(0));
        Assert.assertEquals(SHUTDOWN, listener.events.get(1));
    }

    @Test
    public void testListenerInvocationWhenNodeTerminates() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final Config config = new Config();
        final LifeCycleListenerTest.EventCountingListener listener = new LifeCycleListenerTest.EventCountingListener();
        config.addListenerConfig(new ListenerConfig(listener));
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        listener.events.clear();
        instance.getLifecycleService().terminate();
        Assert.assertEquals(SHUTTING_DOWN, listener.events.get(0));
        Assert.assertEquals(SHUTDOWN, listener.events.get(1));
    }

    static class MyLifecycleListener implements LifecycleListener {
        private CountDownLatch latch;

        MyLifecycleListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            if ((event.getState()) == (LifecycleState.STARTED)) {
                Hazelcast.getHazelcastInstanceByName("_hzInstance_1_dev");
                latch.countDown();
            }
        }
    }

    static class EventCountingListener implements LifecycleListener {
        private final List<LifecycleState> events = new CopyOnWriteArrayList<LifecycleState>();

        @Override
        public void stateChanged(LifecycleEvent event) {
            events.add(event.getState());
        }
    }
}

