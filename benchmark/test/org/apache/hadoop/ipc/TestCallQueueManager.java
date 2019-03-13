/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ipc;


import CallQueueOverflowException.KEEPALIVE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.ipc.CallQueueManager.CallQueueOverflowException;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestCallQueueManager {
    private CallQueueManager<TestCallQueueManager.FakeCall> manager;

    private Configuration conf = new Configuration();

    public class FakeCall implements Schedulable {
        public final int tag;// Can be used for unique identification


        private int priorityLevel;

        UserGroupInformation fakeUgi = UserGroupInformation.createRemoteUser("fakeUser");

        public FakeCall(int tag) {
            this.tag = tag;
        }

        @Override
        public UserGroupInformation getUserGroupInformation() {
            return fakeUgi;
        }

        @Override
        public int getPriorityLevel() {
            return priorityLevel;
        }

        public void setPriorityLevel(int level) {
            this.priorityLevel = level;
        }
    }

    /**
     * Putter produces FakeCalls
     */
    public class Putter implements Runnable {
        private final CallQueueManager<TestCallQueueManager.FakeCall> cq;

        public final int tag;

        public volatile int callsAdded = 0;// How many calls we added, accurate unless interrupted


        private final int maxCalls;

        private volatile boolean isRunning = true;

        public Putter(CallQueueManager<TestCallQueueManager.FakeCall> aCq, int maxCalls, int tag) {
            this.maxCalls = maxCalls;
            this.cq = aCq;
            this.tag = tag;
        }

        public void run() {
            try {
                // Fill up to max (which is infinite if maxCalls < 0)
                while ((isRunning) && (((callsAdded) < (maxCalls)) || ((maxCalls) < 0))) {
                    TestCallQueueManager.FakeCall call = new TestCallQueueManager.FakeCall(this.tag);
                    call.setPriorityLevel(cq.getPriorityLevel(call));
                    cq.put(call);
                    (callsAdded)++;
                } 
            } catch (InterruptedException e) {
                return;
            }
        }

        public void stop() {
            this.isRunning = false;
        }
    }

    /**
     * Taker consumes FakeCalls
     */
    public class Taker implements Runnable {
        private final CallQueueManager<TestCallQueueManager.FakeCall> cq;

        public final int tag;// if >= 0 means we will only take the matching tag, and put back


        // anything else
        public volatile int callsTaken = 0;// total calls taken, accurate if we aren't interrupted


        public volatile TestCallQueueManager.FakeCall lastResult = null;// the last thing we took


        private final int maxCalls;// maximum calls to take


        public Taker(CallQueueManager<TestCallQueueManager.FakeCall> aCq, int maxCalls, int tag) {
            this.maxCalls = maxCalls;
            this.cq = aCq;
            this.tag = tag;
        }

        public void run() {
            try {
                // Take while we don't exceed maxCalls, or if maxCalls is undefined (< 0)
                while (((callsTaken) < (maxCalls)) || ((maxCalls) < 0)) {
                    TestCallQueueManager.FakeCall res = cq.take();
                    if (((tag) >= 0) && ((res.tag) != (this.tag))) {
                        // This call does not match our tag, we should put it back and try again
                        cq.put(res);
                    } else {
                        (callsTaken)++;
                        lastResult = res;
                    }
                } 
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private static final Class<? extends BlockingQueue<TestCallQueueManager.FakeCall>> queueClass = CallQueueManager.convertQueueClass(LinkedBlockingQueue.class, TestCallQueueManager.FakeCall.class);

    private static final Class<? extends RpcScheduler> schedulerClass = CallQueueManager.convertSchedulerClass(DefaultRpcScheduler.class);

    @Test
    public void testCallQueueCapacity() throws InterruptedException {
        manager = new CallQueueManager<TestCallQueueManager.FakeCall>(TestCallQueueManager.queueClass, TestCallQueueManager.schedulerClass, false, 10, "", conf);
        assertCanPut(manager, 10, 20);// Will stop at 10 due to capacity

    }

    @Test
    public void testEmptyConsume() throws InterruptedException {
        manager = new CallQueueManager<TestCallQueueManager.FakeCall>(TestCallQueueManager.queueClass, TestCallQueueManager.schedulerClass, false, 10, "", conf);
        assertCanTake(manager, 0, 1);// Fails since it's empty

    }

    @Test
    public void testFcqBackwardCompatibility() throws InterruptedException {
        // Test BackwardCompatibility to ensure existing FCQ deployment still
        // work without explicitly specifying DecayRpcScheduler
        Configuration conf = new Configuration();
        final String ns = (CommonConfigurationKeys.IPC_NAMESPACE) + ".0";
        final String queueClassName = "org.apache.hadoop.ipc.FairCallQueue";
        conf.setStrings(((ns + ".") + (CommonConfigurationKeys.IPC_CALLQUEUE_IMPL_KEY)), queueClassName);
        // Specify only Fair Call Queue without a scheduler
        // Ensure the DecayScheduler will be added to avoid breaking.
        Class<? extends RpcScheduler> scheduler = Server.getSchedulerClass(ns, conf);
        Assert.assertTrue(scheduler.getCanonicalName().equals("org.apache.hadoop.ipc.DecayRpcScheduler"));
        Class<? extends BlockingQueue<TestCallQueueManager.FakeCall>> queue = ((Class<? extends BlockingQueue<TestCallQueueManager.FakeCall>>) (TestCallQueueManager.getQueueClass(ns, conf)));
        Assert.assertTrue(queue.getCanonicalName().equals(queueClassName));
        manager = new CallQueueManager<TestCallQueueManager.FakeCall>(queue, scheduler, false, 8, "", conf);
        // Default FCQ has 4 levels and the max capacity is 8
        assertCanPut(manager, 3, 3);
    }

    @Test
    public void testSchedulerWithoutFCQ() throws InterruptedException {
        Configuration conf = new Configuration();
        // Test DecayedRpcScheduler without FCQ
        // Ensure the default LinkedBlockingQueue can work with DecayedRpcScheduler
        final String ns = (CommonConfigurationKeys.IPC_NAMESPACE) + ".0";
        final String schedulerClassName = "org.apache.hadoop.ipc.DecayRpcScheduler";
        conf.setStrings(((ns + ".") + (CommonConfigurationKeys.IPC_SCHEDULER_IMPL_KEY)), schedulerClassName);
        Class<? extends BlockingQueue<TestCallQueueManager.FakeCall>> queue = ((Class<? extends BlockingQueue<TestCallQueueManager.FakeCall>>) (TestCallQueueManager.getQueueClass(ns, conf)));
        Assert.assertTrue(queue.getCanonicalName().equals(("java.util.concurrent." + "LinkedBlockingQueue")));
        manager = new CallQueueManager<TestCallQueueManager.FakeCall>(queue, Server.getSchedulerClass(ns, conf), false, 3, "", conf);
        // LinkedBlockingQueue with a capacity of 3 can put 3 calls
        assertCanPut(manager, 3, 3);
        // LinkedBlockingQueue with a capacity of 3 can't put 1 more call
        assertCanPut(manager, 0, 1);
    }

    @Test(timeout = 60000)
    public void testSwapUnderContention() throws InterruptedException {
        manager = new CallQueueManager<TestCallQueueManager.FakeCall>(TestCallQueueManager.queueClass, TestCallQueueManager.schedulerClass, false, 5000, "", conf);
        ArrayList<TestCallQueueManager.Putter> producers = new ArrayList<TestCallQueueManager.Putter>();
        ArrayList<TestCallQueueManager.Taker> consumers = new ArrayList<TestCallQueueManager.Taker>();
        HashMap<Runnable, Thread> threads = new HashMap<Runnable, Thread>();
        // Create putters and takers
        for (int i = 0; i < 1000; i++) {
            TestCallQueueManager.Putter p = new TestCallQueueManager.Putter(manager, (-1), (-1));
            Thread pt = new Thread(p);
            producers.add(p);
            threads.put(p, pt);
            pt.start();
        }
        for (int i = 0; i < 100; i++) {
            TestCallQueueManager.Taker t = new TestCallQueueManager.Taker(manager, (-1), (-1));
            Thread tt = new Thread(t);
            consumers.add(t);
            threads.put(t, tt);
            tt.start();
        }
        Thread.sleep(500);
        for (int i = 0; i < 5; i++) {
            manager.swapQueue(TestCallQueueManager.schedulerClass, TestCallQueueManager.queueClass, 5000, "", conf);
        }
        // Stop the producers
        for (TestCallQueueManager.Putter p : producers) {
            p.stop();
        }
        // Wait for consumers to wake up, then consume
        Thread.sleep(2000);
        Assert.assertEquals(0, manager.size());
        // Ensure no calls were dropped
        long totalCallsCreated = 0;
        for (TestCallQueueManager.Putter p : producers) {
            threads.get(p).interrupt();
        }
        for (TestCallQueueManager.Putter p : producers) {
            threads.get(p).join();
            totalCallsCreated += p.callsAdded;
        }
        long totalCallsConsumed = 0;
        for (TestCallQueueManager.Taker t : consumers) {
            threads.get(t).interrupt();
        }
        for (TestCallQueueManager.Taker t : consumers) {
            threads.get(t).join();
            totalCallsConsumed += t.callsTaken;
        }
        Assert.assertEquals(totalCallsConsumed, totalCallsCreated);
    }

    public static class ExceptionFakeCall implements Schedulable {
        public ExceptionFakeCall() {
            throw new IllegalArgumentException(("Exception caused by call queue " + "constructor.!!"));
        }

        @Override
        public UserGroupInformation getUserGroupInformation() {
            return null;
        }

        @Override
        public int getPriorityLevel() {
            return 0;
        }
    }

    public static class ExceptionFakeScheduler {
        public ExceptionFakeScheduler() {
            throw new IllegalArgumentException(("Exception caused by " + "scheduler constructor.!!"));
        }
    }

    private static final Class<? extends RpcScheduler> exceptionSchedulerClass = CallQueueManager.convertSchedulerClass(TestCallQueueManager.ExceptionFakeScheduler.class);

    private static final Class<? extends BlockingQueue<TestCallQueueManager.ExceptionFakeCall>> exceptionQueueClass = CallQueueManager.convertQueueClass(TestCallQueueManager.ExceptionFakeCall.class, TestCallQueueManager.ExceptionFakeCall.class);

    @Test
    public void testCallQueueConstructorException() throws InterruptedException {
        try {
            new CallQueueManager<TestCallQueueManager.ExceptionFakeCall>(TestCallQueueManager.exceptionQueueClass, TestCallQueueManager.schedulerClass, false, 10, "", new Configuration());
            Assert.fail();
        } catch (RuntimeException re) {
            Assert.assertTrue(((re.getCause()) instanceof IllegalArgumentException));
            Assert.assertEquals("Exception caused by call queue constructor.!!", re.getCause().getMessage());
        }
    }

    @Test
    public void testSchedulerConstructorException() throws InterruptedException {
        try {
            new CallQueueManager<TestCallQueueManager.FakeCall>(TestCallQueueManager.queueClass, TestCallQueueManager.exceptionSchedulerClass, false, 10, "", new Configuration());
            Assert.fail();
        } catch (RuntimeException re) {
            Assert.assertTrue(((re.getCause()) instanceof IllegalArgumentException));
            Assert.assertEquals("Exception caused by scheduler constructor.!!", re.getCause().getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCallQueueOverflowExceptions() throws Exception {
        RpcScheduler scheduler = Mockito.mock(RpcScheduler.class);
        BlockingQueue<Schedulable> queue = Mockito.mock(BlockingQueue.class);
        CallQueueManager<Schedulable> cqm = Mockito.spy(new CallQueueManager(queue, scheduler, false));
        Schedulable call = new TestCallQueueManager.FakeCall(0);
        // call queue exceptions passed threw as-is
        Mockito.doThrow(KEEPALIVE).when(queue).add(call);
        try {
            cqm.add(call);
            Assert.fail("didn't throw");
        } catch (CallQueueOverflowException cqe) {
            Assert.assertSame(KEEPALIVE, cqe);
        }
        // standard exception for blocking queue full converted to overflow
        // exception.
        Mockito.doThrow(new IllegalStateException()).when(queue).add(call);
        try {
            cqm.add(call);
            Assert.fail("didn't throw");
        } catch (Exception ex) {
            Assert.assertTrue(ex.toString(), (ex instanceof CallQueueOverflowException));
        }
        // backoff disabled, put is put to queue.
        Mockito.reset(queue);
        cqm.setClientBackoffEnabled(false);
        cqm.put(call);
        Mockito.verify(queue, Mockito.times(1)).put(call);
        Mockito.verify(queue, Mockito.times(0)).add(call);
        // backoff enabled, put is add to queue.
        Mockito.reset(queue);
        cqm.setClientBackoffEnabled(true);
        Mockito.doReturn(Boolean.FALSE).when(cqm).shouldBackOff(call);
        cqm.put(call);
        Mockito.verify(queue, Mockito.times(0)).put(call);
        Mockito.verify(queue, Mockito.times(1)).add(call);
        Mockito.reset(queue);
        // backoff is enabled, put + scheduler backoff = overflow exception.
        Mockito.reset(queue);
        cqm.setClientBackoffEnabled(true);
        Mockito.doReturn(Boolean.TRUE).when(cqm).shouldBackOff(call);
        try {
            cqm.put(call);
            Assert.fail("didn't fail");
        } catch (Exception ex) {
            Assert.assertTrue(ex.toString(), (ex instanceof CallQueueOverflowException));
        }
        Mockito.verify(queue, Mockito.times(0)).put(call);
        Mockito.verify(queue, Mockito.times(0)).add(call);
    }
}

