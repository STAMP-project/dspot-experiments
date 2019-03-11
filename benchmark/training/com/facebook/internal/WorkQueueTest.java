/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.internal;


import WorkQueue.WorkItem;
import com.facebook.FacebookTestCase;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import junit.framework.Assert;
import org.junit.Test;


public class WorkQueueTest extends FacebookTestCase {
    @Test
    public void testEmptyValidate() {
        WorkQueue manager = new WorkQueue();
        manager.validate();
    }

    @Test
    public void testRunSomething() {
        WorkQueueTest.CountingRunnable run = new WorkQueueTest.CountingRunnable();
        Assert.assertEquals(0, run.getRunCount());
        WorkQueueTest.ScriptableExecutor executor = new WorkQueueTest.ScriptableExecutor();
        Assert.assertEquals(0, executor.getPendingCount());
        WorkQueue manager = new WorkQueue(1, executor);
        addActiveWorkItem(manager, run);
        Assert.assertEquals(1, executor.getPendingCount());
        Assert.assertEquals(0, run.getRunCount());
        executeNext(manager, executor);
        Assert.assertEquals(0, executor.getPendingCount());
        Assert.assertEquals(1, run.getRunCount());
    }

    @Test
    public void testRunSequence() {
        final int workTotal = 100;
        WorkQueueTest.CountingRunnable run = new WorkQueueTest.CountingRunnable();
        WorkQueueTest.ScriptableExecutor executor = new WorkQueueTest.ScriptableExecutor();
        WorkQueue manager = new WorkQueue(1, executor);
        for (int i = 0; i < workTotal; i++) {
            addActiveWorkItem(manager, run);
            Assert.assertEquals(1, executor.getPendingCount());
        }
        for (int i = 0; i < workTotal; i++) {
            Assert.assertEquals(1, executor.getPendingCount());
            Assert.assertEquals(i, run.getRunCount());
            executeNext(manager, executor);
        }
        Assert.assertEquals(0, executor.getPendingCount());
        Assert.assertEquals(workTotal, run.getRunCount());
    }

    @Test
    public void testRunParallel() {
        final int workTotal = 100;
        WorkQueueTest.CountingRunnable run = new WorkQueueTest.CountingRunnable();
        WorkQueueTest.ScriptableExecutor executor = new WorkQueueTest.ScriptableExecutor();
        WorkQueue manager = new WorkQueue(workTotal, executor);
        for (int i = 0; i < workTotal; i++) {
            Assert.assertEquals(i, executor.getPendingCount());
            addActiveWorkItem(manager, run);
        }
        for (int i = 0; i < workTotal; i++) {
            Assert.assertEquals((workTotal - i), executor.getPendingCount());
            Assert.assertEquals(i, run.getRunCount());
            executeNext(manager, executor);
        }
        Assert.assertEquals(0, executor.getPendingCount());
        Assert.assertEquals(workTotal, run.getRunCount());
    }

    @Test
    public void testSimpleCancel() {
        WorkQueueTest.CountingRunnable run = new WorkQueueTest.CountingRunnable();
        WorkQueueTest.ScriptableExecutor executor = new WorkQueueTest.ScriptableExecutor();
        WorkQueue manager = new WorkQueue(1, executor);
        addActiveWorkItem(manager, run);
        WorkQueue.WorkItem work1 = addActiveWorkItem(manager, run);
        cancelWork(manager, work1);
        Assert.assertEquals(1, executor.getPendingCount());
        executeNext(manager, executor);
        Assert.assertEquals(0, executor.getPendingCount());
    }

    @Test
    public void testMoveToFront() {
        final int firstCount = 8;
        final int highCount = 17;
        ArrayList<WorkQueue.WorkItem> highWorkItems = new ArrayList<WorkQueue.WorkItem>();
        WorkQueueTest.CountingRunnable highRun = new WorkQueueTest.CountingRunnable();
        WorkQueueTest.CountingRunnable firstRun = new WorkQueueTest.CountingRunnable();
        WorkQueueTest.CountingRunnable lowRun = new WorkQueueTest.CountingRunnable();
        WorkQueueTest.ScriptableExecutor executor = new WorkQueueTest.ScriptableExecutor();
        WorkQueue manager = new WorkQueue(firstCount, executor);
        for (int i = 0; i < firstCount; i++) {
            addActiveWorkItem(manager, firstRun);
        }
        int lowCount = 0;
        for (int h = 0; h < highCount; h++) {
            highWorkItems.add(addActiveWorkItem(manager, highRun));
            for (int l = 0; l < h; l++) {
                addActiveWorkItem(manager, lowRun);
                lowCount++;
            }
        }
        Assert.assertEquals(firstCount, executor.getPendingCount());
        for (WorkQueue.WorkItem highItem : highWorkItems) {
            prioritizeWork(manager, highItem);
        }
        for (int i = 0; i < firstCount; i++) {
            Assert.assertEquals(i, firstRun.getRunCount());
            executeNext(manager, executor);
        }
        for (int i = 0; i < highCount; i++) {
            Assert.assertEquals(i, highRun.getRunCount());
            executeNext(manager, executor);
        }
        for (int i = 0; i < lowCount; i++) {
            Assert.assertEquals(i, lowRun.getRunCount());
            executeNext(manager, executor);
        }
        Assert.assertEquals(firstCount, firstRun.getRunCount());
        Assert.assertEquals(highCount, highRun.getRunCount());
        Assert.assertEquals(lowCount, lowRun.getRunCount());
    }

    // Test cancelling running work item, completed work item
    @Test
    public void testThreadStress() {
        WorkQueue manager = new WorkQueue();
        ArrayList<WorkQueueTest.StressRunnable> runnables = new ArrayList<WorkQueueTest.StressRunnable>();
        final int threadCount = 20;
        for (int i = 0; i < threadCount; i++) {
            runnables.add(new WorkQueueTest.StressRunnable(manager, 20));
        }
        for (int i = 0; i < threadCount; i++) {
            manager.addActiveWorkItem(runnables.get(i));
        }
        for (int i = 0; i < threadCount; i++) {
            runnables.get(i).waitForDone();
        }
    }

    static class StressRunnable implements Runnable {
        static ArrayList<WorkQueue.WorkItem> tracked = new ArrayList<WorkQueue.WorkItem>();

        final WorkQueue manager;

        final SecureRandom random = new SecureRandom();

        final int iterationCount;

        int iterationIndex = 0;

        boolean isDone = false;

        StressRunnable(WorkQueue manager, int iterationCount) {
            this.manager = manager;
            this.iterationCount = iterationCount;
        }

        @Override
        public void run() {
            // Each iteration runs a random action against the WorkQueue.
            if (((iterationIndex)++) < (iterationCount)) {
                final int sleepWeight = 80;
                final int trackThisWeight = 10;
                final int prioritizeTrackedWeight = 6;
                final int validateWeight = 2;
                int weight = 0;
                final int n = random.nextInt((((sleepWeight + trackThisWeight) + prioritizeTrackedWeight) + validateWeight));
                WorkQueue.WorkItem workItem = manager.addActiveWorkItem(this);
                if (n < (weight += sleepWeight)) {
                    // Sleep
                    try {
                        Thread.sleep((n / 4));
                    } catch (InterruptedException e) {
                    }
                } else
                    if (n < (weight += trackThisWeight)) {
                        // Track this work item to activate later
                        synchronized(WorkQueueTest.StressRunnable.tracked) {
                            WorkQueueTest.StressRunnable.tracked.add(workItem);
                        }
                    } else
                        if (n < (weight += prioritizeTrackedWeight)) {
                            // Background all pending items, prioritize tracked items, and clear tracked list
                            ArrayList<WorkQueue.WorkItem> items = new ArrayList<WorkQueue.WorkItem>();
                            synchronized(WorkQueueTest.StressRunnable.tracked) {
                                items.addAll(WorkQueueTest.StressRunnable.tracked);
                                WorkQueueTest.StressRunnable.tracked.clear();
                            }
                            for (WorkQueue.WorkItem item : items) {
                                item.moveToFront();
                            }
                        } else {
                            // Validate
                            manager.validate();
                        }


            } else {
                // Also have all threads validate once they are done.
                manager.validate();
                synchronized(this) {
                    isDone = true;
                    this.notifyAll();
                }
            }
        }

        void waitForDone() {
            synchronized(this) {
                while (!(isDone)) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                } 
            }
        }
    }

    static class ScriptableExecutor implements Executor {
        private final ArrayList<Runnable> runnables = new ArrayList<Runnable>();

        int getPendingCount() {
            return runnables.size();
        }

        void runNext() {
            Assert.assertTrue(((runnables.size()) > 0));
            runnables.get(0).run();
            runnables.remove(0);
        }

        void runLast() {
            Assert.assertTrue(((runnables.size()) > 0));
            int index = (runnables.size()) - 1;
            runnables.get(index).run();
            runnables.remove(index);
        }

        @Override
        public void execute(Runnable runnable) {
            synchronized(this) {
                runnables.add(runnable);
            }
        }
    }

    static class CountingRunnable implements Runnable {
        private int runCount = 0;

        synchronized int getRunCount() {
            return runCount;
        }

        @Override
        public void run() {
            synchronized(this) {
                (runCount)++;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }
}

