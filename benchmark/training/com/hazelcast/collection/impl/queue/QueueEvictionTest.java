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
package com.hazelcast.collection.impl.queue;


import com.hazelcast.config.Config;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueueEvictionTest extends HazelcastTestSupport {
    @Test
    public void testQueueEviction_whenTtlIsSet_thenTakeThrowsException() throws Exception {
        String queueName = HazelcastTestSupport.randomString();
        Config config = new Config();
        config.getQueueConfig(queueName).setEmptyQueueTtl(2);
        HazelcastInstance hz = createHazelcastInstance(config);
        IQueue<Object> queue = hz.getQueue(queueName);
        try {
            Assert.assertTrue(queue.offer("item"));
            Assert.assertEquals("item", queue.poll());
            queue.take();
            Assert.fail();
        } catch (DistributedObjectDestroyedException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testQueueEviction_whenTtlIsZero_thenListenersAreNeverthelessExecuted() throws Exception {
        String queueName = HazelcastTestSupport.randomString();
        Config config = new Config();
        config.getQueueConfig(queueName).setEmptyQueueTtl(0);
        HazelcastInstance hz = createHazelcastInstance(config);
        final CountDownLatch latch = new CountDownLatch(2);
        hz.addDistributedObjectListener(new DistributedObjectListener() {
            public void distributedObjectCreated(DistributedObjectEvent event) {
                latch.countDown();
            }

            public void distributedObjectDestroyed(DistributedObjectEvent event) {
                latch.countDown();
            }
        });
        IQueue<Object> queue = hz.getQueue(queueName);
        Assert.assertTrue(queue.offer("item"));
        Assert.assertEquals("item", queue.poll());
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }
}

