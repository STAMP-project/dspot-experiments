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
package com.hazelcast.ringbuffer.impl;


import RingbufferConfig.DEFAULT_CAPACITY;
import RingbufferProxy.MAX_BATCH_SIZE;
import com.hazelcast.config.Config;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.util.RootCauseMatcher;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static RingbufferProxy.MAX_BATCH_SIZE;


public abstract class RingbufferAbstractTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    protected IAtomicLong atomicLong;

    protected Ringbuffer<String> ringbuffer;

    private Config config;

    private HazelcastInstance local;

    private String name;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // ================ tailSequence ==========================================
    @Test
    public void tailSequence_whenNothingAdded() {
        long found = ringbuffer.tailSequence();
        Assert.assertEquals((-1), found);
    }

    // ================ remainingCapacity ==========================================
    // the actual logic of remaining capacity is tested directly in the RingbufferContainerTest. So we just need
    // to do some basic tests to see if the operation itself is set up correctly
    @Test
    public void remainingCapacity() {
        Assert.assertEquals(ringbuffer.capacity(), ringbuffer.remainingCapacity());
        ringbuffer.add("first");
        Assert.assertEquals(((ringbuffer.capacity()) - 1), ringbuffer.remainingCapacity());
        ringbuffer.add("second");
        Assert.assertEquals(((ringbuffer.capacity()) - 2), ringbuffer.remainingCapacity());
    }

    // ================ headSequence ==========================================
    @Test
    public void headSequence_whenNothingAdded() {
        long found = ringbuffer.headSequence();
        Assert.assertEquals(0, found);
    }

    // ================ capacity =========================================
    @Test
    public void capacity() {
        Assert.assertEquals(DEFAULT_CAPACITY, ringbuffer.capacity());
    }

    // ================ size =========================================
    @Test
    public void size_whenUnused() {
        long found = ringbuffer.size();
        Assert.assertEquals(0, found);
    }

    @Test
    public void size_whenSomeItemsAdded() throws Exception {
        // we are adding some items, but we don't go beyond the capacity
        // most of the testing already is done in the RingbufferContainerTest
        for (int k = 1; k <= 100; k++) {
            ringbuffer.add("foo");
            Assert.assertEquals(k, ringbuffer.size());
        }
    }

    // ================ add ==========================================
    @Test(expected = NullPointerException.class)
    public void addAsync_whenNullItem() {
        ringbuffer.addAsync(null, OverflowPolicy.FAIL);
    }

    @Test(expected = NullPointerException.class)
    public void addAsync_whenNullOverflowPolicy() {
        ringbuffer.addAsync("foo", null);
    }

    @Test
    public void addAsync_fail_whenSpace() throws Exception {
        long sequence = ringbuffer.addAsync("item", OverflowPolicy.FAIL).get();
        Assert.assertEquals(0, sequence);
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals(0, ringbuffer.tailSequence());
        Assert.assertEquals("item", ringbuffer.readOne(sequence));
    }

    @Test
    public void addAsync_fail_whenNoSpace() throws Exception {
        // fill the buffer with data
        for (int k = 0; k < (ringbuffer.capacity()); k++) {
            ringbuffer.add("old");
        }
        long oldHead = ringbuffer.headSequence();
        long oldTail = ringbuffer.tailSequence();
        long result = ringbuffer.addAsync("item", OverflowPolicy.FAIL).get();
        Assert.assertEquals((-1), result);
        Assert.assertEquals(oldHead, ringbuffer.headSequence());
        Assert.assertEquals(oldTail, ringbuffer.tailSequence());
        // verify that all the old data is still present.
        for (long seq = oldHead; seq <= oldTail; seq++) {
            Assert.assertEquals("old", ringbuffer.readOne(seq));
        }
    }

    @Test
    public void addAsync_overwrite_whenSpace() throws Exception {
        long sequence = ringbuffer.addAsync("item", OverflowPolicy.OVERWRITE).get();
        Assert.assertEquals(0, sequence);
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals(0, ringbuffer.tailSequence());
        Assert.assertEquals("item", ringbuffer.readOne(sequence));
    }

    @Test
    public void addAsync_whenOverwrite_andTTL() throws InterruptedException, ExecutionException {
        addAsync_whenOverwrite();
    }

    @Test
    public void addAsync_whenOverwrite_andNoTTL() throws InterruptedException, ExecutionException {
        addAsync_whenOverwrite();
    }

    // ================ add ==========================================
    @Test(expected = NullPointerException.class)
    public void add_whenNullItem() throws Exception {
        ringbuffer.add(null);
    }

    /**
     * In this test we are going to overwrite old data a few times (so we are going round
     * the ring a few times). At the end we expect to see the latest items; everything
     * that is overwritten should not be shown anymore.
     */
    @Test
    public void add_overwritingOldData() throws Exception {
        RingbufferConfig c = config.getRingbufferConfig(ringbuffer.getName());
        long lastSequence = ringbuffer.tailSequence();
        long expectedTailSeq = 0;
        for (long k = 0; k < (2 * (c.getCapacity())); k++) {
            long sequence = ringbuffer.add(("item-" + k));
            lastSequence = ringbuffer.tailSequence();
            Assert.assertEquals(expectedTailSeq, sequence);
            Assert.assertEquals(expectedTailSeq, lastSequence);
            Assert.assertEquals(expectedTailSeq, ringbuffer.tailSequence());
            expectedTailSeq++;
        }
        Assert.assertEquals(((lastSequence - (c.getCapacity())) + 1), ringbuffer.headSequence());
        // verifying the content
        for (long sequence = ringbuffer.headSequence(); sequence <= (ringbuffer.tailSequence()); sequence++) {
            Assert.assertEquals(("bad content at sequence:" + sequence), ("item-" + sequence), ringbuffer.readOne(sequence));
        }
    }

    // this test verifies that the add works correctly if we go round the ringbuffer many times.
    @Test
    public void add_manyTimesRoundTheRing() throws Exception {
        RingbufferConfig c = config.getRingbufferConfig(ringbuffer.getName());
        for (int iteration = 0; iteration < ((c.getCapacity()) * 100); iteration++) {
            long oldTail = ringbuffer.tailSequence();
            String item = "" + iteration;
            long sequence = ringbuffer.add(item);
            long expectedSequence = oldTail + 1;
            Assert.assertEquals(expectedSequence, sequence);
            Assert.assertEquals(expectedSequence, ringbuffer.tailSequence());
            if ((ringbuffer.tailSequence()) < (c.getCapacity())) {
                Assert.assertEquals(0, ringbuffer.headSequence());
            } else {
                Assert.assertEquals((((ringbuffer.tailSequence()) - (c.getCapacity())) + 1), ringbuffer.headSequence());
            }
            Assert.assertEquals(item, ringbuffer.readOne(expectedSequence));
        }
    }

    // ================== addAll ========================================
    @Test(expected = IllegalArgumentException.class)
    public void addAllAsync_whenCollectionTooLarge() {
        ringbuffer.addAllAsync(RingbufferAbstractTest.randomList(((MAX_BATCH_SIZE) + 1)), OverflowPolicy.OVERWRITE);
    }

    @Test(expected = NullPointerException.class)
    public void addAllAsync_whenNullCollection() {
        ringbuffer.addAllAsync(null, OverflowPolicy.OVERWRITE);
    }

    @Test(expected = NullPointerException.class)
    public void addAllAsync_whenCollectionContainsNullElement() {
        List<String> list = new LinkedList<String>();
        list.add(null);
        ringbuffer.addAllAsync(list, OverflowPolicy.OVERWRITE);
    }

    @Test(expected = NullPointerException.class)
    public void addAllAsync_whenNullOverflowPolicy() {
        ringbuffer.addAllAsync(new LinkedList<String>(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAllAsync_whenEmpty() throws Exception {
        ringbuffer.addAllAsync(new LinkedList<String>(), OverflowPolicy.OVERWRITE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readOne_futureSequence() throws Exception {
        for (int i = 0; i < (2 * (ringbuffer.capacity())); i++) {
            ringbuffer.add(String.valueOf(i));
        }
        ringbuffer.readOne(((ringbuffer.tailSequence()) + 2));
    }

    @Test(expected = StaleSequenceException.class)
    public void readOne_staleSequence() throws Exception {
        for (int i = 0; i < (2 * (ringbuffer.capacity())); i++) {
            ringbuffer.add(String.valueOf(i));
        }
        ringbuffer.readOne(((ringbuffer.headSequence()) - 1));
    }

    @Test
    public void addAllAsync_whenCollectionExceedsCapacity() throws Exception {
        RingbufferConfig c = config.getRingbufferConfig(ringbuffer.getName());
        long oldTailSeq = ringbuffer.tailSequence();
        List<String> items = RingbufferAbstractTest.randomList(((c.getCapacity()) + 20));
        long result = ringbuffer.addAllAsync(items, OverflowPolicy.OVERWRITE).get();
        Assert.assertEquals((oldTailSeq + (items.size())), ringbuffer.tailSequence());
        Assert.assertEquals((((ringbuffer.tailSequence()) - (c.getCapacity())) + 1), ringbuffer.headSequence());
        Assert.assertEquals(ringbuffer.tailSequence(), result);
    }

    @Test
    public void addAllAsync_whenObjectInMemoryFormat() throws Exception {
        List<String> items = RingbufferAbstractTest.randomList(10);
        long oldTailSeq = ringbuffer.tailSequence();
        long result = ringbuffer.addAllAsync(items, OverflowPolicy.OVERWRITE).get();
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals((oldTailSeq + (items.size())), ringbuffer.tailSequence());
        Assert.assertEquals(ringbuffer.tailSequence(), result);
        long startSequence = ringbuffer.headSequence();
        for (int k = 0; k < (items.size()); k++) {
            Assert.assertEquals(items.get(k), ringbuffer.readOne((startSequence + k)));
        }
    }

    @Test
    public void addAllAsync() throws Exception {
        List<String> items = RingbufferAbstractTest.randomList(10);
        long oldTailSeq = ringbuffer.tailSequence();
        long result = ringbuffer.addAllAsync(items, OverflowPolicy.OVERWRITE).get();
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals((oldTailSeq + (items.size())), ringbuffer.tailSequence());
        Assert.assertEquals(ringbuffer.tailSequence(), result);
        long startSequence = ringbuffer.headSequence();
        for (int k = 0; k < (items.size()); k++) {
            Assert.assertEquals(items.get(k), ringbuffer.readOne((startSequence + k)));
        }
    }

    // this test verifies that the addAllAsync works correctly if we go round the ringbuffer many times.
    @Test
    public void addAllAsync_manyTimesRoundTheRing() throws Exception {
        RingbufferConfig c = config.getRingbufferConfig(ringbuffer.getName());
        Random random = new Random();
        for (int iteration = 0; iteration < 1000; iteration++) {
            List<String> items = RingbufferAbstractTest.randomList(Math.max(1, random.nextInt(c.getCapacity())));
            long previousTailSeq = ringbuffer.tailSequence();
            long result = ringbuffer.addAllAsync(items, OverflowPolicy.OVERWRITE).get();
            Assert.assertEquals((previousTailSeq + (items.size())), ringbuffer.tailSequence());
            if ((ringbuffer.tailSequence()) < (c.getCapacity())) {
                Assert.assertEquals(0, ringbuffer.headSequence());
            } else {
                Assert.assertEquals((((ringbuffer.tailSequence()) - (c.getCapacity())) + 1), ringbuffer.headSequence());
            }
            Assert.assertEquals(ringbuffer.tailSequence(), result);
            long startSequence = previousTailSeq + 1;
            for (int k = 0; k < (items.size()); k++) {
                Assert.assertEquals(items.get(k), ringbuffer.readOne((startSequence + k)));
            }
        }
    }

    // ================== read ========================================
    @Test
    public void readOne() throws Exception {
        ringbuffer.add("first");
        ringbuffer.add("second");
        long oldHead = ringbuffer.headSequence();
        long oldTail = ringbuffer.tailSequence();
        Assert.assertEquals("first", ringbuffer.readOne(0));
        Assert.assertEquals("second", ringbuffer.readOne(1));
        // make sure that the head/tail of the ringbuffer have not changed.
        Assert.assertEquals(oldHead, ringbuffer.headSequence());
        Assert.assertEquals(oldTail, ringbuffer.tailSequence());
    }

    @Test
    public void readOne_whenObjectInMemoryFormat() throws Exception {
        ringbuffer.add("first");
        ringbuffer.add("second");
        long oldHead = ringbuffer.headSequence();
        long oldTail = ringbuffer.tailSequence();
        Assert.assertEquals("first", ringbuffer.readOne(0));
        Assert.assertEquals("second", ringbuffer.readOne(1));
        // make sure that the head/tail of the ringbuffer have not changed.
        Assert.assertEquals(oldHead, ringbuffer.headSequence());
        Assert.assertEquals(oldTail, ringbuffer.tailSequence());
    }

    @Test
    public void readOne_whenOneAfterTail_thenBlock() throws Exception {
        ringbuffer.add("1");
        ringbuffer.add("2");
        final long tail = ringbuffer.tailSequence();
        // first we do the invocation. This invocation is going to block
        final Future f = HazelcastTestSupport.spawn(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return ringbuffer.readOne((tail + 1));
            }
        });
        // then we check if the future is not going to complete.
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(f.isDone());
            }
        }, 2);
        // then we add the item
        ringbuffer.add("3");
        // and eventually the future should complete
        HazelcastTestSupport.assertCompletesEventually(f);
        Assert.assertEquals("3", f.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readOne_whenTooFarAfterTail_thenIllegalArgumentException() throws Exception {
        ringbuffer.add("1");
        ringbuffer.add("2");
        long tailSeq = ringbuffer.tailSequence();
        ringbuffer.readOne((tailSeq + 2));
    }

    @Test
    public void readOne_whenBeforeHead_thenStaleSequenceException() throws Exception {
        RingbufferConfig ringbufferConfig = config.getRingbufferConfig(ringbuffer.getName());
        for (int k = 0; k < ((ringbufferConfig.getCapacity()) * 2); k++) {
            ringbuffer.add("foo");
        }
        long headSeq = ringbuffer.headSequence();
        try {
            ringbuffer.readOne((headSeq - 1));
            Assert.fail();
        } catch (StaleSequenceException expected) {
            Assert.assertEquals(headSeq, expected.getHeadSeq());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void read_whenNegativeSequence_thenIllegalArgumentException() throws Exception {
        ringbuffer.readOne((-1));
    }

    // ==================== asyncReadOrWait ==============================
    @Test
    public void readManyAsync_whenReadingBeyondTail() throws InterruptedException, ExecutionException {
        ringbuffer.add("1");
        ringbuffer.add("2");
        long seq = (ringbuffer.tailSequence()) + 2;
        ICompletableFuture f = ringbuffer.readManyAsync(seq, 1, 1, null);
        try {
            f.get();
            Assert.fail();
        } catch (ExecutionException e) {
            HazelcastTestSupport.assertInstanceOf(IllegalArgumentException.class, e.getCause());
        }
    }

    @Test
    public void readyManyAsync_whenSomeWaitingForSingleItemNeeded() throws InterruptedException, ExecutionException {
        final ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(0, 1, 10, null);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(f.isDone());
            }
        }, 5);
        ringbuffer.add("1");
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        MatcherAssert.assertThat(f.get(), Matchers.contains("1"));
        Assert.assertEquals(1, resultSet.readCount());
    }

    @Test
    public void readManyAsync_whenSomeWaitingNeeded() throws InterruptedException, ExecutionException {
        final ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(0, 2, 10, null);
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(f.isDone());
            }
        }, 5);
        ringbuffer.add("1");
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(f.isDone());
            }
        }, 5);
        ringbuffer.add("2");
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        Assert.assertNotNull(resultSet);
        MatcherAssert.assertThat(f.get(), Matchers.contains("1", "2"));
        Assert.assertEquals(2, resultSet.readCount());
    }

    @Test
    public void readManyAsync_whenMinZeroAndItemAvailable() throws InterruptedException, ExecutionException {
        ringbuffer.add("1");
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(0, 0, 10, null);
        HazelcastTestSupport.assertCompletesEventually(f);
        MatcherAssert.assertThat(f.get(), Matchers.contains("1"));
    }

    @Test
    public void readManyAsync_whenMinZeroAndNoItemAvailable() throws InterruptedException, ExecutionException {
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(0, 0, 10, null);
        HazelcastTestSupport.assertCompletesEventually(f);
        Assert.assertEquals(0, f.get().readCount());
    }

    @Test
    public void readManyAsync_whenEnoughItems() throws InterruptedException, ExecutionException {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        ringbuffer.add("item4");
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(1, 1, 2, null);
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        Assert.assertNotNull(resultSet);
        Assert.assertThat(f.get(), Matchers.contains("item2", "item3"));
        Assert.assertEquals(2, resultSet.readCount());
    }

    @Test
    public void readManyAsync_whenEnoughItems_andObjectInMemoryFormat() throws InterruptedException, ExecutionException {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        ringbuffer.add("item4");
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(1, 1, 2, null);
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        Assert.assertNotNull(resultSet);
        MatcherAssert.assertThat(f.get(), Matchers.contains("item2", "item3"));
        Assert.assertEquals(2, resultSet.readCount());
    }

    public static class GoodStringFunction implements IFunction<String, Boolean> , Serializable {
        @Override
        public Boolean apply(String input) {
            return input.startsWith("good");
        }
    }

    @Test
    public void readManyAsync_withFilter() throws InterruptedException, ExecutionException {
        ringbuffer.add("good1");
        ringbuffer.add("bad1");
        ringbuffer.add("good2");
        ringbuffer.add("bad");
        ringbuffer.add("good3");
        ringbuffer.add("bad1");
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(0, 2, 10, new RingbufferAbstractTest.GoodStringFunction());
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        Assert.assertNotNull(resultSet);
        Assert.assertThat(f.get(), Matchers.contains("good1", "good2", "good3"));
        Assert.assertEquals(6, resultSet.readCount());
    }

    @Test
    public void readManyAsync_emptyBatchAndNoItems() throws Exception {
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(0, 0, 10, null);
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        Assert.assertEquals(0, f.get().readCount());
        Assert.assertEquals(0, resultSet.readCount());
    }

    @Test
    public void readManyAsync_whenHitsStale_shouldNotBeBlocked() throws Exception {
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(0, 1, 10, null);
        ringbuffer.addAllAsync(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), OverflowPolicy.OVERWRITE);
        expectedException.expect(new RootCauseMatcher(StaleSequenceException.class));
        f.get();
    }

    @Test
    public void readOne_whenHitsStale_shouldNotBeBlocked() {
        final CountDownLatch latch = new CountDownLatch(1);
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ringbuffer.readOne(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (StaleSequenceException e) {
                    latch.countDown();
                }
            }
        });
        consumer.start();
        ringbuffer.addAllAsync(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"), OverflowPolicy.OVERWRITE);
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void readManyAsync_whenMoreAvailableThanMinimumLessThanMaximum() throws InterruptedException, ExecutionException {
        ringbuffer.add("1");
        ringbuffer.add("2");
        ringbuffer.add("3");
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(ringbuffer.headSequence(), 2, 10, null);
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        MatcherAssert.assertThat(f.get(), Matchers.contains("1", "2", "3"));
        Assert.assertEquals(3, resultSet.readCount());
    }

    @Test
    public void readManyAsync_whenMoreAvailableThanMaximum() throws InterruptedException, ExecutionException {
        ringbuffer.add("1");
        ringbuffer.add("2");
        ringbuffer.add("3");
        ICompletableFuture<ReadResultSet<String>> f = ringbuffer.readManyAsync(ringbuffer.headSequence(), 1, 2, null);
        HazelcastTestSupport.assertCompletesEventually(f);
        ReadResultSet<String> resultSet = f.get();
        Assert.assertNotNull(resultSet);
        MatcherAssert.assertThat(f.get(), Matchers.contains("1", "2"));
        Assert.assertEquals(2, resultSet.readCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readManyAsync_whenMaxCountLargerThanCapacity() {
        ringbuffer.readManyAsync(0, 1, ((RingbufferConfig.DEFAULT_CAPACITY) + 1), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readManyAsync_whenStartSequenceNegative() {
        ringbuffer.readManyAsync((-1), 1, 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readManyAsync_whenMinCountNegative() {
        ringbuffer.readManyAsync(0, (-1), 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readManyAsync_whenMaxSmallerThanMinCount() {
        ringbuffer.readManyAsync(0, 5, 4, null);
    }

    @Test
    public void readManyAsync_whenMaxCountTooHigh() throws Exception {
        ringbuffer.readManyAsync(0, 1, MAX_BATCH_SIZE, null);
    }

    // ===================== destroy ==========================
    @Test
    public void destroy() throws Exception {
        ringbuffer.add("1");
        ringbuffer.destroy();
        ringbuffer = local.getRingbuffer(name);
        Assert.assertEquals(0, ringbuffer.headSequence());
        Assert.assertEquals((-1), ringbuffer.tailSequence());
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void destroy_whenBlockedThreads_thenDistributedObjectDestroyedException() throws Exception {
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.sleepSeconds(2);
                ringbuffer.destroy();
            }
        });
        InternalCompletableFuture f = ((InternalCompletableFuture) (ringbuffer.readManyAsync(0, 1, 1, null)));
        f.join();
    }

    // ===================== misc ==========================
    @Test
    public void test_toString() {
        String actual = ringbuffer.toString();
        String expected = String.format("Ringbuffer{name='%s'}", ringbuffer.getName());
        Assert.assertEquals(expected, actual);
    }
}

