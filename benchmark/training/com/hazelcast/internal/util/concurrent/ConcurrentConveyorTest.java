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
package com.hazelcast.internal.util.concurrent;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConcurrentConveyorTest {
    static final int QUEUE_CAPACITY = 2;

    @Rule
    public ExpectedException excRule = ExpectedException.none();

    final ConcurrentConveyorTest.Item doneItem = new ConcurrentConveyorTest.Item();

    final ConcurrentConveyorTest.Item item1 = new ConcurrentConveyorTest.Item();

    final ConcurrentConveyorTest.Item item2 = new ConcurrentConveyorTest.Item();

    int queueCount;

    OneToOneConcurrentArrayQueue<ConcurrentConveyorTest.Item> defaultQ;

    ConcurrentConveyor<ConcurrentConveyorTest.Item> conveyor;

    private final List<ConcurrentConveyorTest.Item> batch = new ArrayList<ConcurrentConveyorTest.Item>(ConcurrentConveyorTest.QUEUE_CAPACITY);

    @Test(expected = IllegalArgumentException.class)
    public void mustPassSomeQueues() {
        ConcurrentConveyor.concurrentConveyor(doneItem);
    }

    @Test
    public void submitterGoneItem() {
        Assert.assertSame(doneItem, conveyor.submitterGoneItem());
    }

    @Test
    public void queueCount() {
        Assert.assertEquals(queueCount, conveyor.queueCount());
    }

    @Test
    public void getQueueAtIndex() {
        Assert.assertSame(defaultQ, conveyor.queue(0));
    }

    @Test
    public void when_offerToQueueZero_then_poll() {
        // when
        boolean didOffer = conveyor.offer(0, item1);
        // then
        Assert.assertTrue(didOffer);
        Assert.assertSame(item1, defaultQ.poll());
    }

    @Test
    public void when_offerToGivenQueue_then_poll() {
        // when
        boolean didOffer = conveyor.offer(defaultQ, item1);
        // then
        Assert.assertTrue(didOffer);
        Assert.assertSame(item1, defaultQ.poll());
    }

    @Test
    public void when_submitToGivenQueue_then_poll() {
        // when
        conveyor.submit(defaultQ, item1);
        // then
        Assert.assertSame(item1, defaultQ.poll());
    }

    @Test
    public void when_drainToList_then_listPopulated() {
        // given
        conveyor.offer(0, item1);
        conveyor.offer(0, item2);
        // when
        conveyor.drainTo(batch);
        // then
        Assert.assertEquals(Arrays.asList(item1, item2), batch);
    }

    @Test
    public void when_drainQueue1ToList_then_listPopulated() {
        // given
        conveyor.offer(1, item1);
        conveyor.offer(1, item2);
        // when
        conveyor.drainTo(1, batch);
        // then
        Assert.assertEquals(Arrays.asList(item1, item2), batch);
    }

    @Test
    public void when_drainQueue1ToListLimited_then_listHasLimitedItems() {
        // given
        conveyor.offer(1, item1);
        conveyor.offer(1, item2);
        // when
        conveyor.drainTo(1, batch, 1);
        // then
        Assert.assertEquals(Collections.singletonList(item1), batch);
    }

    @Test
    public void when_drainToListLimited_then_listHasLimitItems() {
        // given
        conveyor.offer(0, item1);
        conveyor.offer(0, item2);
        // when
        conveyor.drainTo(batch, 1);
        // then
        Assert.assertEquals(Collections.singletonList(item1), batch);
    }

    @Test
    public void when_drainerDone_then_offerToFullQueueFails() {
        // given
        Assert.assertTrue(conveyor.offer(1, item1));
        Assert.assertTrue(conveyor.offer(1, item2));
        // when
        conveyor.drainerDone();
        // then
        excRule.expect(ConcurrentConveyorException.class);
        conveyor.offer(1, item1);
    }

    @Test
    public void when_drainerDone_then_submitToFullQueueFails() {
        // given
        Assert.assertTrue(conveyor.offer(defaultQ, item1));
        Assert.assertTrue(conveyor.offer(defaultQ, item2));
        // when
        conveyor.drainerDone();
        // then
        excRule.expect(ConcurrentConveyorException.class);
        conveyor.submit(defaultQ, item1);
    }

    @Test
    public void when_interrupted_then_submitToFullQueueFails() {
        // given
        conveyor.drainerArrived();
        Assert.assertTrue(conveyor.offer(defaultQ, item1));
        Assert.assertTrue(conveyor.offer(defaultQ, item2));
        // when
        Thread.currentThread().interrupt();
        // then
        excRule.expect(ConcurrentConveyorException.class);
        conveyor.submit(defaultQ, item1);
    }

    @Test
    public void when_drainerLeavesThenArrives_then_offerDoesntFail() {
        // given
        Assert.assertTrue(conveyor.offer(defaultQ, item1));
        Assert.assertTrue(conveyor.offer(defaultQ, item2));
        conveyor.drainerDone();
        // when
        conveyor.drainerArrived();
        // then
        conveyor.offer(defaultQ, item1);
    }

    @Test
    public void when_drainerFails_then_offerFailsWithItsFailureAsCause() {
        // given
        Assert.assertTrue(conveyor.offer(1, item1));
        Assert.assertTrue(conveyor.offer(1, item2));
        // when
        Exception drainerFailure = new Exception("test failure");
        conveyor.drainerFailed(drainerFailure);
        // then
        try {
            conveyor.offer(1, item1);
            Assert.fail("Expected exception not thrown");
        } catch (ConcurrentConveyorException e) {
            Assert.assertSame(drainerFailure, e.getCause());
        }
    }

    @Test(expected = NullPointerException.class)
    public void when_callDrainerFailedWithNull_then_throwNPE() {
        conveyor.drainerFailed(null);
    }

    @Test
    public void when_drainerDone_then_isDrainerGoneReturnsTrue() {
        // when
        conveyor.drainerDone();
        // then
        Assert.assertTrue(conveyor.isDrainerGone());
    }

    @Test
    public void when_drainerFailed_then_isDrainerGoneReturnsTrue() {
        // when
        conveyor.drainerFailed(new Exception("test failure"));
        // then
        Assert.assertTrue(conveyor.isDrainerGone());
    }

    @Test
    public void when_backpressureOn_then_submitBlocks() throws InterruptedException {
        // given
        final AtomicBoolean flag = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                // when
                conveyor.backpressureOn();
                latch.countDown();
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
                // then
                Assert.assertFalse(flag.get());
                conveyor.backpressureOff();
            }
        }.start();
        latch.await();
        conveyor.submit(defaultQ, item1);
        flag.set(true);
    }

    @Test
    public void awaitDrainerGone_blocksUntilDrainerGone() throws InterruptedException {
        // given
        final AtomicBoolean flag = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                // when
                conveyor.drainerArrived();
                latch.countDown();
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
                // then
                Assert.assertFalse(flag.get());
                conveyor.drainerDone();
            }
        }.start();
        latch.await();
        conveyor.awaitDrainerGone();
        flag.set(true);
    }

    static class Item {}
}

