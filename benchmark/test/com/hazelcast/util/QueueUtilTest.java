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
package com.hazelcast.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.LinkedList;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueueUtilTest {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(QueueUtil.class);
    }

    @Test
    public void drainQueueToZero() {
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int i = 0; i < 100; i++) {
            queue.offer(i);
        }
        int drained = QueueUtil.drainQueue(queue);
        Assert.assertEquals(100, drained);
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void drainQueueToNonZero() {
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int i = 0; i < 100; i++) {
            queue.offer(i);
        }
        int drained = QueueUtil.drainQueue(queue, 50, null);
        Assert.assertEquals(50, drained);
        Assert.assertEquals(50, queue.size());
    }

    @Test
    public void drainQueueToZeroWithPredicate() {
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int i = 0; i < 100; i++) {
            queue.offer(i);
        }
        int drained = QueueUtil.drainQueue(queue, new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) == 0;
            }
        });
        Assert.assertEquals(50, drained);
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void drainQueueToNonZeroWithPredicate() {
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int i = 0; i < 100; i++) {
            queue.offer(i);
        }
        int drained = QueueUtil.drainQueue(queue, 50, new com.hazelcast.util.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return (integer % 2) == 0;
            }
        });
        Assert.assertEquals(25, drained);
        Assert.assertEquals(50, queue.size());
    }
}

