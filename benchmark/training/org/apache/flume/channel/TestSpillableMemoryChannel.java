/**
 * /*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel;


import FileChannelConfiguration.TRANSACTION_CAPACITY;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.flume.ChannelException;
import org.apache.flume.ChannelFullException;
import org.apache.flume.Transaction;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static SpillableMemoryChannel.defaultMemoryCapacity;
import static SpillableMemoryChannel.defaultOverflowCapacity;
import static SpillableMemoryChannel.defaultOverflowTimeout;


public class TestSpillableMemoryChannel {
    private SpillableMemoryChannel channel;

    @Rule
    public TemporaryFolder fileChannelDir = new TemporaryFolder();

    static class NullFound extends RuntimeException {
        public int expectedValue;

        public NullFound(int expected) {
            super((("Expected " + expected) + ",  but null found"));
            expectedValue = expected;
        }
    }

    static class TooManyNulls extends RuntimeException {
        private int nullsFound;

        public TooManyNulls(int count) {
            super(((("Total nulls found in thread (" + (Thread.currentThread().getName())) + ") : ") + count));
            nullsFound = count;
        }
    }

    @Test
    public void testPutTake() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "5");
        params.put("overflowCapacity", "5");
        params.put(TRANSACTION_CAPACITY, "5");
        startChannel(params);
        Transaction tx = channel.getTransaction();
        tx.begin();
        TestSpillableMemoryChannel.putN(0, 2, channel);
        tx.commit();
        tx.close();
        tx = channel.getTransaction();
        tx.begin();
        TestSpillableMemoryChannel.takeN(0, 2, channel);
        tx.commit();
        tx.close();
    }

    @Test
    public void testCapacityDisableOverflow() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "2");
        params.put("overflowCapacity", "0");// overflow is disabled effectively

        params.put("overflowTimeout", "0");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(0, 2, channel);
        boolean threw = false;
        try {
            TestSpillableMemoryChannel.transactionalPutN(2, 1, channel);
        } catch (ChannelException e) {
            threw = true;
        }
        Assert.assertTrue("Expecting ChannelFullException to be thrown", threw);
        TestSpillableMemoryChannel.transactionalTakeN(0, 2, channel);
        Transaction tx = channel.getTransaction();
        tx.begin();
        Assert.assertNull(channel.take());
        tx.commit();
        tx.close();
    }

    @Test
    public void testCapacityWithOverflow() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "2");
        params.put("overflowCapacity", "4");
        params.put(TRANSACTION_CAPACITY, "3");
        params.put("overflowTimeout", "0");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 2, channel);
        TestSpillableMemoryChannel.transactionalPutN(3, 2, channel);
        TestSpillableMemoryChannel.transactionalPutN(5, 2, channel);
        boolean threw = false;
        try {
            TestSpillableMemoryChannel.transactionalPutN(7, 2, channel);// cannot fit in channel

        } catch (ChannelFullException e) {
            threw = true;
        }
        Assert.assertTrue("Expecting ChannelFullException to be thrown", threw);
        TestSpillableMemoryChannel.transactionalTakeN(1, 2, channel);
        TestSpillableMemoryChannel.transactionalTakeN(3, 2, channel);
        TestSpillableMemoryChannel.transactionalTakeN(5, 2, channel);
    }

    @Test
    public void testRestart() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "2");
        params.put("overflowCapacity", "10");
        params.put(TRANSACTION_CAPACITY, "4");
        params.put("overflowTimeout", "0");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 2, channel);
        TestSpillableMemoryChannel.transactionalPutN(3, 2, channel);// goes in overflow

        restartChannel(params);
        // from overflow, as in memory stuff should be lost
        TestSpillableMemoryChannel.transactionalTakeN(3, 2, channel);
    }

    @Test
    public void testBasicStart() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "10000000");
        params.put("overflowCapacity", "20000000");
        params.put(TRANSACTION_CAPACITY, "10");
        params.put("overflowTimeout", "1");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(6, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(11, 5, channel);// these should go to overflow

        TestSpillableMemoryChannel.transactionalTakeN(1, 10, channel);
        TestSpillableMemoryChannel.transactionalTakeN(11, 5, channel);
    }

    @Test
    public void testOverflow() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "10");
        params.put("overflowCapacity", "20");
        params.put(TRANSACTION_CAPACITY, "10");
        params.put("overflowTimeout", "1");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(6, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(11, 5, channel);// these should go to overflow

        TestSpillableMemoryChannel.transactionalTakeN(1, 10, channel);
        TestSpillableMemoryChannel.transactionalTakeN(11, 5, channel);
    }

    @Test
    public void testDrainOrder() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "10");
        params.put("overflowCapacity", "10");
        params.put(TRANSACTION_CAPACITY, "5");
        params.put("overflowTimeout", "1");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(6, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(11, 5, channel);// into overflow

        TestSpillableMemoryChannel.transactionalPutN(16, 5, channel);// into overflow

        TestSpillableMemoryChannel.transactionalTakeN(1, 1, channel);
        TestSpillableMemoryChannel.transactionalTakeN(2, 5, channel);
        TestSpillableMemoryChannel.transactionalTakeN(7, 4, channel);
        TestSpillableMemoryChannel.transactionalPutN(20, 2, channel);
        TestSpillableMemoryChannel.transactionalPutN(22, 3, channel);
        TestSpillableMemoryChannel.transactionalTakeN(11, 3, channel);// from overflow

        TestSpillableMemoryChannel.transactionalTakeN(14, 5, channel);// from overflow

        TestSpillableMemoryChannel.transactionalTakeN(19, 2, channel);// from overflow

    }

    @Test
    public void testByteCapacity() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "1000");
        // configure to hold 8 events of 10 bytes each (plus 20% event header space)
        params.put("byteCapacity", "100");
        params.put("avgEventSize", "10");
        params.put("overflowCapacity", "20");
        params.put(TRANSACTION_CAPACITY, "10");
        params.put("overflowTimeout", "1");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 8, channel);// this wil max the byteCapacity

        TestSpillableMemoryChannel.transactionalPutN(9, 10, channel);
        TestSpillableMemoryChannel.transactionalPutN(19, 10, channel);// this will fill up the overflow

        boolean threw = false;
        try {
            TestSpillableMemoryChannel.transactionalPutN(11, 1, channel);// into overflow

        } catch (ChannelFullException e) {
            threw = true;
        }
        Assert.assertTrue("byteCapacity did not throw as expected", threw);
    }

    @Test
    public void testDrainingOnChannelBoundary() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "5");
        params.put("overflowCapacity", "15");
        params.put(TRANSACTION_CAPACITY, "10");
        params.put("overflowTimeout", "1");
        startChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(6, 5, channel);// into overflow

        TestSpillableMemoryChannel.transactionalPutN(11, 5, channel);// into overflow

        TestSpillableMemoryChannel.transactionalPutN(16, 5, channel);// into overflow

        TestSpillableMemoryChannel.transactionalTakeN(1, 3, channel);
        Transaction tx = channel.getTransaction();
        tx.begin();
        TestSpillableMemoryChannel.takeN(4, 2, channel);
        TestSpillableMemoryChannel.takeNull(channel);// expect null since next event is in overflow

        tx.commit();
        tx.close();
        TestSpillableMemoryChannel.transactionalTakeN(6, 5, channel);// from overflow

        TestSpillableMemoryChannel.transactionalTakeN(11, 5, channel);// from overflow

        TestSpillableMemoryChannel.transactionalTakeN(16, 2, channel);// from overflow

        TestSpillableMemoryChannel.transactionalPutN(21, 5, channel);
        tx = channel.getTransaction();
        tx.begin();
        TestSpillableMemoryChannel.takeN(18, 3, channel);
        // from overflow
        TestSpillableMemoryChannel.takeNull(channel);// expect null since next event is in primary

        tx.commit();
        tx.close();
        TestSpillableMemoryChannel.transactionalTakeN(21, 5, channel);
    }

    @Test
    public void testRollBack() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "100");
        params.put("overflowCapacity", "900");
        params.put(TRANSACTION_CAPACITY, "900");
        params.put("overflowTimeout", "0");
        startChannel(params);
        // 1 Rollback for Puts
        TestSpillableMemoryChannel.transactionalPutN(1, 5, channel);
        Transaction tx = channel.getTransaction();
        tx.begin();
        TestSpillableMemoryChannel.putN(6, 5, channel);
        tx.rollback();
        tx.close();
        TestSpillableMemoryChannel.transactionalTakeN(1, 5, channel);
        TestSpillableMemoryChannel.transactionalTakeNull(2, channel);
        // 2.  verify things back to normal after put rollback
        TestSpillableMemoryChannel.transactionalPutN(11, 5, channel);
        TestSpillableMemoryChannel.transactionalTakeN(11, 5, channel);
        // 3 Rollback for Takes
        TestSpillableMemoryChannel.transactionalPutN(16, 5, channel);
        tx = channel.getTransaction();
        tx.begin();
        TestSpillableMemoryChannel.takeN(16, 5, channel);
        TestSpillableMemoryChannel.takeNull(channel);
        tx.rollback();
        tx.close();
        TestSpillableMemoryChannel.transactionalTakeN_NoCheck(5, channel);
        // 4.  verify things back to normal after take rollback
        TestSpillableMemoryChannel.transactionalPutN(21, 5, channel);
        TestSpillableMemoryChannel.transactionalTakeN(21, 5, channel);
    }

    @Test
    public void testReconfigure() {
        // 1) bring up with small capacity
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "10");
        params.put("overflowCapacity", "0");
        params.put("overflowTimeout", "0");
        startChannel(params);
        Assert.assertTrue("overflowTimeout setting did not reconfigure correctly", ((channel.getOverflowTimeout()) == 0));
        Assert.assertTrue("memoryCapacity did not reconfigure correctly", ((channel.getMemoryCapacity()) == 10));
        Assert.assertTrue("overflowCapacity did not reconfigure correctly", channel.isOverflowDisabled());
        TestSpillableMemoryChannel.transactionalPutN(1, 10, channel);
        boolean threw = false;
        try {
            TestSpillableMemoryChannel.transactionalPutN(11, 10, channel);// should throw an error

        } catch (ChannelException e) {
            threw = true;
        }
        Assert.assertTrue(("Expected the channel to fill up and throw an exception, " + "but it did not throw"), threw);
        // 2) Resize and verify
        params = new HashMap<String, String>();
        params.put("memoryCapacity", "20");
        params.put("overflowCapacity", "0");
        reconfigureChannel(params);
        Assert.assertTrue("overflowTimeout setting did not reconfigure correctly", ((channel.getOverflowTimeout()) == (defaultOverflowTimeout)));
        Assert.assertTrue("memoryCapacity did not reconfigure correctly", ((channel.getMemoryCapacity()) == 20));
        Assert.assertTrue("overflowCapacity did not reconfigure correctly", channel.isOverflowDisabled());
        // pull out the values inserted prior to reconfiguration
        TestSpillableMemoryChannel.transactionalTakeN(1, 10, channel);
        TestSpillableMemoryChannel.transactionalPutN(11, 10, channel);
        TestSpillableMemoryChannel.transactionalPutN(21, 10, channel);
        threw = false;
        try {
            TestSpillableMemoryChannel.transactionalPutN(31, 10, channel);// should throw an error

        } catch (ChannelException e) {
            threw = true;
        }
        Assert.assertTrue(("Expected the channel to fill up and throw an exception, " + "but it did not throw"), threw);
        TestSpillableMemoryChannel.transactionalTakeN(11, 10, channel);
        TestSpillableMemoryChannel.transactionalTakeN(21, 10, channel);
        // 3) Reconfigure with empty config and verify settings revert to default
        params = new HashMap<String, String>();
        reconfigureChannel(params);
        Assert.assertTrue("overflowTimeout setting did not reconfigure correctly", ((channel.getOverflowTimeout()) == (defaultOverflowTimeout)));
        Assert.assertTrue("memoryCapacity did not reconfigure correctly", ((channel.getMemoryCapacity()) == (defaultMemoryCapacity)));
        Assert.assertTrue("overflowCapacity did not reconfigure correctly", ((channel.getOverflowCapacity()) == (defaultOverflowCapacity)));
        Assert.assertFalse("overflowCapacity did not reconfigure correctly", channel.isOverflowDisabled());
        // 4) Reconfiguring of  overflow
        params = new HashMap<String, String>();
        params.put("memoryCapacity", "10");
        params.put("overflowCapacity", "10");
        params.put("transactionCapacity", "5");
        params.put("overflowTimeout", "1");
        reconfigureChannel(params);
        TestSpillableMemoryChannel.transactionalPutN(1, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(6, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(11, 5, channel);
        TestSpillableMemoryChannel.transactionalPutN(16, 5, channel);
        threw = false;
        try {
            // should error out as both primary & overflow are full
            TestSpillableMemoryChannel.transactionalPutN(21, 5, channel);
        } catch (ChannelException e) {
            threw = true;
        }
        Assert.assertTrue("Expected the last insertion to fail, but it didn't.", threw);
        // reconfig the overflow
        params = new HashMap<String, String>();
        params.put("memoryCapacity", "10");
        params.put("overflowCapacity", "20");
        params.put("transactionCapacity", "10");
        params.put("overflowTimeout", "1");
        reconfigureChannel(params);
        // should succeed now as we have made room in the overflow
        TestSpillableMemoryChannel.transactionalPutN(21, 5, channel);
        TestSpillableMemoryChannel.transactionalTakeN(1, 10, channel);
        TestSpillableMemoryChannel.transactionalTakeN(11, 5, channel);
        TestSpillableMemoryChannel.transactionalTakeN(16, 5, channel);
        TestSpillableMemoryChannel.transactionalTakeN(21, 5, channel);
    }

    @Test
    public void testParallelSingleSourceAndSink() throws InterruptedException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "1000020");
        params.put("overflowCapacity", "0");
        params.put("overflowTimeout", "3");
        startChannel(params);
        // run source and sink concurrently
        Thread sourceThd = makePutThread("src", 1, 500000, 100, channel);
        Thread sinkThd = TestSpillableMemoryChannel.makeTakeThread("sink", 1, 500000, 100, channel);
        TestSpillableMemoryChannel.StopWatch watch = new TestSpillableMemoryChannel.StopWatch();
        sinkThd.start();
        sourceThd.start();
        sourceThd.join();
        sinkThd.join();
        watch.elapsed();
        System.out.println(("Max Queue size " + (channel.getMaxMemQueueSize())));
    }

    @Test
    public void testCounters() throws InterruptedException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "5000");
        params.put("overflowCapacity", "5000");
        params.put("transactionCapacity", "5000");
        params.put("overflowTimeout", "0");
        startChannel(params);
        Assert.assertTrue("channel.channelCounter should have started", ((channel.channelCounter.getStartTime()) > 0));
        // 1. fill up mem queue
        Thread sourceThd = makePutThread("src", 1, 5000, 2500, channel);
        sourceThd.start();
        sourceThd.join();
        Assert.assertEquals(5000, channel.getTotalStored());
        Assert.assertEquals(5000, channel.channelCounter.getChannelSize());
        Assert.assertEquals(5000, channel.channelCounter.getEventPutAttemptCount());
        Assert.assertEquals(5000, channel.channelCounter.getEventPutSuccessCount());
        // 2. empty mem queue
        Thread sinkThd = TestSpillableMemoryChannel.makeTakeThread("sink", 1, 5000, 1000, channel);
        sinkThd.start();
        sinkThd.join();
        Assert.assertEquals(0, channel.getTotalStored());
        Assert.assertEquals(0, channel.channelCounter.getChannelSize());
        Assert.assertEquals(5000, channel.channelCounter.getEventTakeAttemptCount());
        Assert.assertEquals(5000, channel.channelCounter.getEventTakeSuccessCount());
        // 3. fill up mem & overflow
        sourceThd = makePutThread("src", 1, 10000, 1000, channel);
        sourceThd.start();
        sourceThd.join();
        Assert.assertEquals(10000, channel.getTotalStored());
        Assert.assertEquals(10000, channel.channelCounter.getChannelSize());
        Assert.assertEquals(15000, channel.channelCounter.getEventPutAttemptCount());
        Assert.assertEquals(15000, channel.channelCounter.getEventPutSuccessCount());
        // 4. empty memory
        sinkThd = TestSpillableMemoryChannel.makeTakeThread("sink", 1, 5000, 1000, channel);
        sinkThd.start();
        sinkThd.join();
        Assert.assertEquals(5000, channel.getTotalStored());
        Assert.assertEquals(5000, channel.channelCounter.getChannelSize());
        Assert.assertEquals(10000, channel.channelCounter.getEventTakeAttemptCount());
        Assert.assertEquals(10000, channel.channelCounter.getEventTakeSuccessCount());
        // 5. empty overflow
        TestSpillableMemoryChannel.transactionalTakeN(5001, 1000, channel);
        TestSpillableMemoryChannel.transactionalTakeN(6001, 1000, channel);
        TestSpillableMemoryChannel.transactionalTakeN(7001, 1000, channel);
        TestSpillableMemoryChannel.transactionalTakeN(8001, 1000, channel);
        TestSpillableMemoryChannel.transactionalTakeN(9001, 1000, channel);
        Assert.assertEquals(0, channel.getTotalStored());
        Assert.assertEquals(0, channel.channelCounter.getChannelSize());
        Assert.assertEquals(15000, channel.channelCounter.getEventTakeAttemptCount());
        Assert.assertEquals(15000, channel.channelCounter.getEventTakeSuccessCount());
        // 6. now do it concurrently
        sourceThd = makePutThread("src1", 1, 5000, 1000, channel);
        Thread sourceThd2 = makePutThread("src2", 1, 5000, 500, channel);
        sinkThd = TestSpillableMemoryChannel.makeTakeThread_noCheck("sink1", 5000, 1000, channel);
        sourceThd.start();
        sourceThd2.start();
        sinkThd.start();
        sourceThd.join();
        sourceThd2.join();
        sinkThd.join();
        Assert.assertEquals(5000, channel.getTotalStored());
        Assert.assertEquals(5000, channel.channelCounter.getChannelSize());
        Thread sinkThd2 = TestSpillableMemoryChannel.makeTakeThread_noCheck("sink2", 2500, 500, channel);
        Thread sinkThd3 = TestSpillableMemoryChannel.makeTakeThread_noCheck("sink3", 2500, 1000, channel);
        sinkThd2.start();
        sinkThd3.start();
        sinkThd2.join();
        sinkThd3.join();
        Assert.assertEquals(0, channel.getTotalStored());
        Assert.assertEquals(0, channel.channelCounter.getChannelSize());
        Assert.assertEquals(25000, channel.channelCounter.getEventTakeSuccessCount());
        Assert.assertEquals(25000, channel.channelCounter.getEventPutSuccessCount());
        Assert.assertTrue("TakeAttempt channel counter value larger than expected", (25000 <= (channel.channelCounter.getEventTakeAttemptCount())));
        Assert.assertTrue("PutAttempt channel counter value larger than expected", (25000 <= (channel.channelCounter.getEventPutAttemptCount())));
    }

    @Test
    public void testParallelMultipleSourcesAndSinks() throws InterruptedException {
        int sourceCount = 8;
        int sinkCount = 8;
        int eventCount = 1000000;
        int batchSize = 100;
        Map<String, String> params = new HashMap<String, String>();
        params.put("memoryCapacity", "0");
        params.put("overflowCapacity", String.valueOf(eventCount));
        params.put("overflowTimeout", "3");
        startChannel(params);
        ArrayList<Thread> sinks = createSinkThreads(sinkCount, eventCount, batchSize);
        ArrayList<Thread> sources = createSourceThreads(sourceCount, eventCount, batchSize);
        TestSpillableMemoryChannel.StopWatch watch = new TestSpillableMemoryChannel.StopWatch();
        startThreads(sinks);
        startThreads(sources);
        joinThreads(sources);
        joinThreads(sinks);
        watch.elapsed();
        System.out.println(("Max Queue size " + (channel.getMaxMemQueueSize())));
        Assert.assertEquals(eventCount, channel.drainOrder.totalPuts);
        Assert.assertEquals("Channel not fully drained", 0, channel.getTotalStored());
        System.out.println("testParallelMultipleSourcesAndSinks done");
    }

    static class StopWatch {
        long startTime;

        public StopWatch() {
            startTime = System.currentTimeMillis();
        }

        public void elapsed() {
            elapsed(null);
        }

        public void elapsed(String suffix) {
            long elapsed = (System.currentTimeMillis()) - (startTime);
            if (suffix == null) {
                suffix = "";
            } else {
                suffix = ("{ " + suffix) + " }";
            }
            if (elapsed < 10000) {
                System.out.println((((((Thread.currentThread().getName()) + " : [ ") + elapsed) + " ms ].        ") + suffix));
            } else {
                System.out.println((((((Thread.currentThread().getName()) + " : [ ") + (elapsed / 1000)) + " sec ].       ") + suffix));
            }
        }
    }
}

