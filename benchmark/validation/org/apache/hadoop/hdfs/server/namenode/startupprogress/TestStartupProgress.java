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
package org.apache.hadoop.hdfs.server.namenode.startupprogress;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.hadoop.hdfs.server.namenode.startupprogress.Status.Status.COMPLETE;


public class TestStartupProgress {
    private StartupProgress startupProgress;

    @Test(timeout = 10000)
    public void testCounter() {
        startupProgress.beginPhase(LOADING_FSIMAGE);
        Step loadingFsImageInodes = new Step(INODES);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageInodes);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_FSIMAGE, loadingFsImageInodes, 100L);
        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageInodes);
        Step loadingFsImageDelegationKeys = new Step(DELEGATION_KEYS);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_FSIMAGE, loadingFsImageDelegationKeys, 200L);
        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        startupProgress.endPhase(LOADING_FSIMAGE);
        startupProgress.beginPhase(LOADING_EDITS);
        Step loadingEditsFile = new Step("file", 1000L);
        startupProgress.beginStep(LOADING_EDITS, loadingEditsFile);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_EDITS, loadingEditsFile, 5000L);
        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals(100L, view.getCount(LOADING_FSIMAGE, loadingFsImageInodes));
        Assert.assertEquals(200L, view.getCount(LOADING_FSIMAGE, loadingFsImageDelegationKeys));
        Assert.assertEquals(5000L, view.getCount(LOADING_EDITS, loadingEditsFile));
        Assert.assertEquals(0L, view.getCount(SAVING_CHECKPOINT, new Step(INODES)));
        // Increment a counter again and check that the existing view was not
        // modified, but a new view shows the updated value.
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_EDITS, loadingEditsFile, 1000L);
        startupProgress.endStep(LOADING_EDITS, loadingEditsFile);
        startupProgress.endPhase(LOADING_EDITS);
        Assert.assertEquals(5000L, view.getCount(LOADING_EDITS, loadingEditsFile));
        view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals(6000L, view.getCount(LOADING_EDITS, loadingEditsFile));
    }

    @Test(timeout = 10000)
    public void testElapsedTime() throws Exception {
        startupProgress.beginPhase(LOADING_FSIMAGE);
        Step loadingFsImageInodes = new Step(INODES);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageInodes);
        Thread.sleep(50L);// brief sleep to fake elapsed time

        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageInodes);
        Step loadingFsImageDelegationKeys = new Step(DELEGATION_KEYS);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        Thread.sleep(50L);// brief sleep to fake elapsed time

        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        startupProgress.endPhase(LOADING_FSIMAGE);
        startupProgress.beginPhase(LOADING_EDITS);
        Step loadingEditsFile = new Step("file", 1000L);
        startupProgress.beginStep(LOADING_EDITS, loadingEditsFile);
        startupProgress.setTotal(LOADING_EDITS, loadingEditsFile, 10000L);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_EDITS, loadingEditsFile, 5000L);
        Thread.sleep(50L);// brief sleep to fake elapsed time

        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertTrue(((view.getElapsedTime()) > 0));
        Assert.assertTrue(((view.getElapsedTime(LOADING_FSIMAGE)) > 0));
        Assert.assertTrue(((view.getElapsedTime(LOADING_FSIMAGE, loadingFsImageInodes)) > 0));
        Assert.assertTrue(((view.getElapsedTime(LOADING_FSIMAGE, loadingFsImageDelegationKeys)) > 0));
        Assert.assertTrue(((view.getElapsedTime(LOADING_EDITS)) > 0));
        Assert.assertTrue(((view.getElapsedTime(LOADING_EDITS, loadingEditsFile)) > 0));
        Assert.assertTrue(((view.getElapsedTime(SAVING_CHECKPOINT)) == 0));
        Assert.assertTrue(((view.getElapsedTime(SAVING_CHECKPOINT, new Step(INODES))) == 0));
        // Brief sleep, then check that completed phases/steps have the same elapsed
        // time, but running phases/steps have updated elapsed time.
        long totalTime = view.getElapsedTime();
        long loadingFsImageTime = view.getElapsedTime(LOADING_FSIMAGE);
        long loadingFsImageInodesTime = view.getElapsedTime(LOADING_FSIMAGE, loadingFsImageInodes);
        long loadingFsImageDelegationKeysTime = view.getElapsedTime(LOADING_FSIMAGE, loadingFsImageInodes);
        long loadingEditsTime = view.getElapsedTime(LOADING_EDITS);
        long loadingEditsFileTime = view.getElapsedTime(LOADING_EDITS, loadingEditsFile);
        Thread.sleep(50L);
        Assert.assertTrue((totalTime < (view.getElapsedTime())));
        Assert.assertEquals(loadingFsImageTime, view.getElapsedTime(LOADING_FSIMAGE));
        Assert.assertEquals(loadingFsImageInodesTime, view.getElapsedTime(LOADING_FSIMAGE, loadingFsImageInodes));
        Assert.assertTrue((loadingEditsTime < (view.getElapsedTime(LOADING_EDITS))));
        Assert.assertTrue((loadingEditsFileTime < (view.getElapsedTime(LOADING_EDITS, loadingEditsFile))));
    }

    @Test(timeout = 10000)
    public void testFrozenAfterStartupCompletes() {
        // Do some updates and counter increments.
        startupProgress.beginPhase(LOADING_FSIMAGE);
        startupProgress.setFile(LOADING_FSIMAGE, "file1");
        startupProgress.setSize(LOADING_FSIMAGE, 1000L);
        Step step = new Step(INODES);
        startupProgress.beginStep(LOADING_FSIMAGE, step);
        startupProgress.setTotal(LOADING_FSIMAGE, step, 10000L);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_FSIMAGE, step, 100L);
        startupProgress.endStep(LOADING_FSIMAGE, step);
        startupProgress.endPhase(LOADING_FSIMAGE);
        // Force completion of phases, so that entire startup process is completed.
        for (Phase.Phase phase : EnumSet.allOf(Phase.Phase.class)) {
            if ((startupProgress.getStatus(phase)) != (COMPLETE)) {
                startupProgress.beginPhase(phase);
                startupProgress.endPhase(phase);
            }
        }
        StartupProgressView before = startupProgress.createView();
        // Attempt more updates and counter increments.
        startupProgress.beginPhase(LOADING_FSIMAGE);
        startupProgress.setFile(LOADING_FSIMAGE, "file2");
        startupProgress.setSize(LOADING_FSIMAGE, 2000L);
        startupProgress.beginStep(LOADING_FSIMAGE, step);
        startupProgress.setTotal(LOADING_FSIMAGE, step, 20000L);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_FSIMAGE, step, 100L);
        startupProgress.endStep(LOADING_FSIMAGE, step);
        startupProgress.endPhase(LOADING_FSIMAGE);
        // Also attempt a whole new step that wasn't used last time.
        startupProgress.beginPhase(LOADING_EDITS);
        Step newStep = new Step("file1");
        startupProgress.beginStep(LOADING_EDITS, newStep);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_EDITS, newStep, 100L);
        startupProgress.endStep(LOADING_EDITS, newStep);
        startupProgress.endPhase(LOADING_EDITS);
        StartupProgressView after = startupProgress.createView();
        // Expect that data was frozen after completion of entire startup process, so
        // second set of updates and counter increments should have had no effect.
        Assert.assertEquals(before.getCount(LOADING_FSIMAGE), after.getCount(LOADING_FSIMAGE));
        Assert.assertEquals(before.getCount(LOADING_FSIMAGE, step), after.getCount(LOADING_FSIMAGE, step));
        Assert.assertEquals(before.getElapsedTime(), after.getElapsedTime());
        Assert.assertEquals(before.getElapsedTime(LOADING_FSIMAGE), after.getElapsedTime(LOADING_FSIMAGE));
        Assert.assertEquals(before.getElapsedTime(LOADING_FSIMAGE, step), after.getElapsedTime(LOADING_FSIMAGE, step));
        Assert.assertEquals(before.getFile(LOADING_FSIMAGE), after.getFile(LOADING_FSIMAGE));
        Assert.assertEquals(before.getSize(LOADING_FSIMAGE), after.getSize(LOADING_FSIMAGE));
        Assert.assertEquals(before.getTotal(LOADING_FSIMAGE), after.getTotal(LOADING_FSIMAGE));
        Assert.assertEquals(before.getTotal(LOADING_FSIMAGE, step), after.getTotal(LOADING_FSIMAGE, step));
        Assert.assertFalse(after.getSteps(LOADING_EDITS).iterator().hasNext());
    }

    @Test(timeout = 10000)
    public void testInitialState() {
        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals(0L, view.getElapsedTime());
        Assert.assertEquals(0.0F, view.getPercentComplete(), 0.001F);
        List<Phase.Phase> phases = new ArrayList<Phase.Phase>();
        for (Phase.Phase phase : view.getPhases()) {
            phases.add(phase);
            Assert.assertEquals(0L, view.getElapsedTime(phase));
            Assert.assertNull(view.getFile(phase));
            Assert.assertEquals(0.0F, view.getPercentComplete(phase), 0.001F);
            Assert.assertEquals(Long.MIN_VALUE, view.getSize(phase));
            Assert.assertEquals(PENDING, view.getStatus(phase));
            Assert.assertEquals(0L, view.getTotal(phase));
            for (Step step : view.getSteps(phase)) {
                Assert.fail(String.format("unexpected step %s in phase %s at initial state", step, phase));
            }
        }
        Assert.assertArrayEquals(EnumSet.allOf(Phase.Phase.class).toArray(), phases.toArray());
    }

    @Test(timeout = 10000)
    public void testPercentComplete() {
        startupProgress.beginPhase(LOADING_FSIMAGE);
        Step loadingFsImageInodes = new Step(INODES);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageInodes);
        startupProgress.setTotal(LOADING_FSIMAGE, loadingFsImageInodes, 1000L);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_FSIMAGE, loadingFsImageInodes, 100L);
        Step loadingFsImageDelegationKeys = new Step(DELEGATION_KEYS);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        startupProgress.setTotal(LOADING_FSIMAGE, loadingFsImageDelegationKeys, 800L);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_FSIMAGE, loadingFsImageDelegationKeys, 200L);
        startupProgress.beginPhase(LOADING_EDITS);
        Step loadingEditsFile = new Step("file", 1000L);
        startupProgress.beginStep(LOADING_EDITS, loadingEditsFile);
        startupProgress.setTotal(LOADING_EDITS, loadingEditsFile, 10000L);
        StartupProgressTestHelper.incrementCounter(startupProgress, LOADING_EDITS, loadingEditsFile, 5000L);
        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals(0.167F, view.getPercentComplete(), 0.001F);
        Assert.assertEquals(0.167F, view.getPercentComplete(LOADING_FSIMAGE), 0.001F);
        Assert.assertEquals(0.1F, view.getPercentComplete(LOADING_FSIMAGE, loadingFsImageInodes), 0.001F);
        Assert.assertEquals(0.25F, view.getPercentComplete(LOADING_FSIMAGE, loadingFsImageDelegationKeys), 0.001F);
        Assert.assertEquals(0.5F, view.getPercentComplete(LOADING_EDITS), 0.001F);
        Assert.assertEquals(0.5F, view.getPercentComplete(LOADING_EDITS, loadingEditsFile), 0.001F);
        Assert.assertEquals(0.0F, view.getPercentComplete(SAVING_CHECKPOINT), 0.001F);
        Assert.assertEquals(0.0F, view.getPercentComplete(SAVING_CHECKPOINT, new Step(INODES)), 0.001F);
        // End steps/phases, and confirm that they jump to 100% completion.
        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageInodes);
        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        startupProgress.endPhase(LOADING_FSIMAGE);
        startupProgress.endStep(LOADING_EDITS, loadingEditsFile);
        startupProgress.endPhase(LOADING_EDITS);
        view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals(0.5F, view.getPercentComplete(), 0.001F);
        Assert.assertEquals(1.0F, view.getPercentComplete(LOADING_FSIMAGE), 0.001F);
        Assert.assertEquals(1.0F, view.getPercentComplete(LOADING_FSIMAGE, loadingFsImageInodes), 0.001F);
        Assert.assertEquals(1.0F, view.getPercentComplete(LOADING_FSIMAGE, loadingFsImageDelegationKeys), 0.001F);
        Assert.assertEquals(1.0F, view.getPercentComplete(LOADING_EDITS), 0.001F);
        Assert.assertEquals(1.0F, view.getPercentComplete(LOADING_EDITS, loadingEditsFile), 0.001F);
        Assert.assertEquals(0.0F, view.getPercentComplete(SAVING_CHECKPOINT), 0.001F);
        Assert.assertEquals(0.0F, view.getPercentComplete(SAVING_CHECKPOINT, new Step(INODES)), 0.001F);
    }

    @Test(timeout = 10000)
    public void testStatus() {
        startupProgress.beginPhase(LOADING_FSIMAGE);
        startupProgress.endPhase(LOADING_FSIMAGE);
        startupProgress.beginPhase(LOADING_EDITS);
        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals(COMPLETE, view.getStatus(LOADING_FSIMAGE));
        Assert.assertEquals(RUNNING, view.getStatus(LOADING_EDITS));
        Assert.assertEquals(PENDING, view.getStatus(SAVING_CHECKPOINT));
    }

    @Test(timeout = 10000)
    public void testStepSequence() {
        // Test that steps are returned in the correct sort order (by file and then
        // sequence number) by starting a few steps in a randomly shuffled order and
        // then asserting that they are returned in the expected order.
        Step[] expectedSteps = new Step[]{ new Step(INODES, "file1"), new Step(DELEGATION_KEYS, "file1"), new Step(INODES, "file2"), new Step(DELEGATION_KEYS, "file2"), new Step(INODES, "file3"), new Step(DELEGATION_KEYS, "file3") };
        List<Step> shuffledSteps = new ArrayList<Step>(Arrays.asList(expectedSteps));
        Collections.shuffle(shuffledSteps);
        startupProgress.beginPhase(SAVING_CHECKPOINT);
        for (Step step : shuffledSteps) {
            startupProgress.beginStep(SAVING_CHECKPOINT, step);
        }
        List<Step> actualSteps = new ArrayList<Step>(expectedSteps.length);
        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        for (Step step : view.getSteps(SAVING_CHECKPOINT)) {
            actualSteps.add(step);
        }
        Assert.assertArrayEquals(expectedSteps, actualSteps.toArray());
    }

    @Test(timeout = 10000)
    public void testThreadSafety() throws Exception {
        // Test for thread safety by starting multiple threads that mutate the same
        // StartupProgress instance in various ways.  We expect no internal
        // corruption of data structures and no lost updates on counter increments.
        int numThreads = 100;
        // Data tables used by each thread to determine values to pass to APIs.
        Phase.Phase[] phases = new Phase.Phase[]{ LOADING_FSIMAGE, LOADING_FSIMAGE, LOADING_EDITS, LOADING_EDITS };
        Step[] steps = new Step[]{ new Step(INODES), new Step(DELEGATION_KEYS), new Step(INODES), new Step(DELEGATION_KEYS) };
        String[] files = new String[]{ "file1", "file1", "file2", "file2" };
        long[] sizes = new long[]{ 1000L, 1000L, 2000L, 2000L };
        long[] totals = new long[]{ 10000L, 20000L, 30000L, 40000L };
        ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        try {
            for (int i = 0; i < numThreads; ++i) {
                final Phase.Phase phase = phases[(i % (phases.length))];
                final Step step = steps[(i % (steps.length))];
                final String file = files[(i % (files.length))];
                final long size = sizes[(i % (sizes.length))];
                final long total = totals[(i % (totals.length))];
                exec.submit(new Callable<Void>() {
                    @Override
                    public Void call() {
                        startupProgress.beginPhase(phase);
                        startupProgress.setFile(phase, file);
                        startupProgress.setSize(phase, size);
                        startupProgress.setTotal(phase, step, total);
                        StartupProgressTestHelper.incrementCounter(startupProgress, phase, step, 100L);
                        startupProgress.endStep(phase, step);
                        startupProgress.endPhase(phase);
                        return null;
                    }
                });
            }
        } finally {
            exec.shutdown();
            Assert.assertTrue(exec.awaitTermination(10000L, TimeUnit.MILLISECONDS));
        }
        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals("file1", view.getFile(LOADING_FSIMAGE));
        Assert.assertEquals(1000L, view.getSize(LOADING_FSIMAGE));
        Assert.assertEquals(10000L, view.getTotal(LOADING_FSIMAGE, new Step(INODES)));
        Assert.assertEquals(2500L, view.getCount(LOADING_FSIMAGE, new Step(INODES)));
        Assert.assertEquals(20000L, view.getTotal(LOADING_FSIMAGE, new Step(DELEGATION_KEYS)));
        Assert.assertEquals(2500L, view.getCount(LOADING_FSIMAGE, new Step(DELEGATION_KEYS)));
        Assert.assertEquals("file2", view.getFile(LOADING_EDITS));
        Assert.assertEquals(2000L, view.getSize(LOADING_EDITS));
        Assert.assertEquals(30000L, view.getTotal(LOADING_EDITS, new Step(INODES)));
        Assert.assertEquals(2500L, view.getCount(LOADING_EDITS, new Step(INODES)));
        Assert.assertEquals(40000L, view.getTotal(LOADING_EDITS, new Step(DELEGATION_KEYS)));
        Assert.assertEquals(2500L, view.getCount(LOADING_EDITS, new Step(DELEGATION_KEYS)));
    }

    @Test(timeout = 10000)
    public void testTotal() {
        startupProgress.beginPhase(LOADING_FSIMAGE);
        Step loadingFsImageInodes = new Step(INODES);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageInodes);
        startupProgress.setTotal(LOADING_FSIMAGE, loadingFsImageInodes, 1000L);
        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageInodes);
        Step loadingFsImageDelegationKeys = new Step(DELEGATION_KEYS);
        startupProgress.beginStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        startupProgress.setTotal(LOADING_FSIMAGE, loadingFsImageDelegationKeys, 800L);
        startupProgress.endStep(LOADING_FSIMAGE, loadingFsImageDelegationKeys);
        startupProgress.endPhase(LOADING_FSIMAGE);
        startupProgress.beginPhase(LOADING_EDITS);
        Step loadingEditsFile = new Step("file", 1000L);
        startupProgress.beginStep(LOADING_EDITS, loadingEditsFile);
        startupProgress.setTotal(LOADING_EDITS, loadingEditsFile, 10000L);
        startupProgress.endStep(LOADING_EDITS, loadingEditsFile);
        startupProgress.endPhase(LOADING_EDITS);
        StartupProgressView view = startupProgress.createView();
        Assert.assertNotNull(view);
        Assert.assertEquals(1000L, view.getTotal(LOADING_FSIMAGE, loadingFsImageInodes));
        Assert.assertEquals(800L, view.getTotal(LOADING_FSIMAGE, loadingFsImageDelegationKeys));
        Assert.assertEquals(10000L, view.getTotal(LOADING_EDITS, loadingEditsFile));
    }
}

