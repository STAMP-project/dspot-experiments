/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona.concurrent;


import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ManyToOneConcurrentLinkedQueueTest {
    private final Queue<Integer> queue = new ManyToOneConcurrentLinkedQueue();

    @Test
    public void shouldBeEmpty() {
        Assert.assertTrue(queue.isEmpty());
        Assert.assertThat(queue.size(), Matchers.is(0));
    }

    @Test
    public void shouldNotBeEmpty() {
        queue.offer(1);
        Assert.assertFalse(queue.isEmpty());
        Assert.assertThat(queue.size(), Matchers.is(1));
    }

    @Test
    public void shouldFailToPoll() {
        Assert.assertNull(queue.poll());
    }

    @Test
    public void shouldOfferItem() {
        Assert.assertTrue(queue.offer(7));
        Assert.assertThat(queue.size(), Matchers.is(1));
    }

    @Test
    public void shouldExchangeItem() {
        final int testItem = 1;
        queue.offer(testItem);
        Assert.assertThat(queue.poll(), Matchers.is(testItem));
    }

    @Test
    public void shouldExchangeInFifoOrder() {
        final int numItems = 7;
        for (int i = 0; i < numItems; i++) {
            queue.offer(i);
        }
        Assert.assertThat(queue.size(), Matchers.is(numItems));
        for (int i = 0; i < numItems; i++) {
            Assert.assertThat(queue.poll(), Matchers.is(i));
        }
        Assert.assertThat(queue.size(), Matchers.is(0));
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void shouldExchangeInFifoOrderInterleaved() {
        final int numItems = 7;
        for (int i = 0; i < numItems; i++) {
            queue.offer(i);
            Assert.assertThat(queue.poll(), Matchers.is(i));
        }
        Assert.assertThat(queue.size(), Matchers.is(0));
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void shouldToString() {
        Assert.assertThat(queue.toString(), Matchers.is("{}"));
        for (int i = 0; i < 5; i++) {
            queue.offer(i);
        }
        Assert.assertThat(queue.toString(), Matchers.is("{0, 1, 2, 3, 4}"));
    }

    @Test(timeout = 10000)
    public void shouldTransferConcurrently() {
        final int count = 1000000;
        final int numThreads = 2;
        final Executor executor = Executors.newFixedThreadPool(numThreads);
        final Runnable producer = () -> {
            for (int i = 0, items = count / numThreads; i < items; i++) {
                queue.offer(i);
            }
        };
        for (int i = 0; i < numThreads; i++) {
            executor.execute(producer);
        }
        for (int i = 0; i < count; i++) {
            while (null == (queue.poll())) {
                // busy spin
            } 
        }
        Assert.assertTrue(queue.isEmpty());
    }
}

