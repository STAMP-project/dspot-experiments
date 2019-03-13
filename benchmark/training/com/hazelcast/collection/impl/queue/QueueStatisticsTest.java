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


import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class QueueStatisticsTest extends HazelcastTestSupport {
    @Test
    public void testItemCount() {
        IQueue<String> queue = newQueue();
        int items = 20;
        for (int i = 0; i < items; i++) {
            queue.offer(("item" + i));
        }
        LocalQueueStats stats = queue.getLocalQueueStats();
        Assert.assertEquals(20, stats.getOwnedItemCount());
        Assert.assertEquals(0, stats.getBackupItemCount());
    }

    @Test
    public void testOfferOperationCount() throws Exception {
        IQueue<String> queue = newQueue();
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        for (int i = 0; i < 10; i++) {
            queue.add(("item" + i));
        }
        for (int i = 0; i < 10; i++) {
            queue.put(("item" + i));
        }
        final LocalQueueStats stats = queue.getLocalQueueStats();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(30, stats.getOfferOperationCount());
            }
        });
    }

    @Test
    public void testRejectedOfferOperationCount() {
        IQueue<String> queue = newQueue_WithMaxSizeConfig(30);
        for (int i = 0; i < 30; i++) {
            queue.offer(("item" + i));
        }
        for (int i = 0; i < 10; i++) {
            queue.offer(("item" + i));
        }
        final LocalQueueStats stats = queue.getLocalQueueStats();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(10, stats.getRejectedOfferOperationCount());
            }
        });
    }

    @Test
    public void testPollOperationCount() throws Exception {
        IQueue<String> queue = newQueue();
        for (int i = 0; i < 30; i++) {
            queue.offer(("item" + i));
        }
        for (int i = 0; i < 10; i++) {
            queue.remove();
        }
        for (int i = 0; i < 10; i++) {
            queue.take();
        }
        for (int i = 0; i < 10; i++) {
            queue.poll();
        }
        final LocalQueueStats stats = queue.getLocalQueueStats();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(30, stats.getPollOperationCount());
            }
        });
    }

    @Test
    public void testEmptyPollOperationCount() {
        IQueue<String> queue = newQueue();
        for (int i = 0; i < 10; i++) {
            queue.poll();
        }
        final LocalQueueStats stats = queue.getLocalQueueStats();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(10, stats.getEmptyPollOperationCount());
            }
        });
    }

    @Test
    public void testOtherOperationCount() {
        IQueue<String> queue = newQueue();
        for (int i = 0; i < 30; i++) {
            queue.offer(("item" + i));
        }
        ArrayList<String> list = new ArrayList<String>();
        queue.drainTo(list);
        queue.addAll(list);
        queue.removeAll(list);
        final LocalQueueStats stats = queue.getLocalQueueStats();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(3, stats.getOtherOperationsCount());
            }
        });
    }

    @Test
    public void testAge() {
        IQueue<String> queue = newQueue();
        queue.offer("maxAgeItem");
        queue.offer("minAgeItem");
        LocalQueueStats stats = queue.getLocalQueueStats();
        long maxAge = stats.getMaxAge();
        long minAge = stats.getMinAge();
        long testAge = (maxAge + minAge) / 2;
        long avgAge = stats.getAvgAge();
        Assert.assertEquals(testAge, avgAge);
    }

    @Test
    public void testEventOperationCount() {
        IQueue<String> queue = newQueue();
        QueueStatisticsTest.TestListener listener = new QueueStatisticsTest.TestListener(30);
        queue.addItemListener(listener, true);
        for (int i = 0; i < 30; i++) {
            queue.offer(("item" + i));
        }
        for (int i = 0; i < 30; i++) {
            queue.poll();
        }
        final LocalQueueStats stats = queue.getLocalQueueStats();
        HazelcastTestSupport.assertOpenEventually(listener.addedLatch);
        HazelcastTestSupport.assertOpenEventually(listener.removedLatch);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(60, stats.getEventOperationCount());
            }
        });
    }

    private static class TestListener implements ItemListener<String> {
        final CountDownLatch addedLatch;

        final CountDownLatch removedLatch;

        TestListener(int latchCount) {
            addedLatch = new CountDownLatch(latchCount);
            removedLatch = new CountDownLatch(latchCount);
        }

        public void itemAdded(ItemEvent item) {
            addedLatch.countDown();
        }

        public void itemRemoved(ItemEvent item) {
            removedLatch.countDown();
        }
    }
}

