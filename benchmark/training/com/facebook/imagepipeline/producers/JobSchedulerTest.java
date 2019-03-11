/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.IS_PLACEHOLDER;
import Consumer.NO_FLAGS;
import Consumer.Status;
import JobScheduler.JobStartExecutorSupplier;
import android.os.SystemClock;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.testing.FakeClock;
import com.facebook.imagepipeline.testing.TestExecutorService;
import com.facebook.imagepipeline.testing.TestScheduledExecutorService;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@Config(manifest = Config.NONE)
@PrepareForTest({ SystemClock.class, JobStartExecutorSupplier.class })
public class JobSchedulerTest {
    private static final int INTERVAL = 100;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private static class TestJobRunnable implements JobScheduler.JobRunnable {
        private static class Job {
            public final EncodedImage encodedImage;

            @Consumer.Status
            public final int status;

            public Job(EncodedImage encodedImage, @Consumer.Status
            int status) {
                this.encodedImage = EncodedImage.cloneOrNull(encodedImage);
                this.status = status;
            }
        }

        public final AtomicBoolean running = new AtomicBoolean();

        public final AtomicBoolean wait = new AtomicBoolean();

        public final AtomicBoolean fail = new AtomicBoolean();

        public final ArrayList<JobSchedulerTest.TestJobRunnable.Job> jobs = new ArrayList<>();

        @Override
        public void run(EncodedImage encodedImage, @Consumer.Status
        int status) {
            running.set(true);
            try {
                JobSchedulerTest.waitForCondition(wait, false);
                if (fail.get()) {
                    throw new RuntimeException();
                } else {
                    jobs.add(new JobSchedulerTest.TestJobRunnable.Job(encodedImage, status));
                }
            } finally {
                running.set(false);
            }
        }
    }

    private FakeClock mFakeClockForTime;

    private FakeClock mFakeClockForWorker;

    private TestExecutorService mTestExecutorService;

    private FakeClock mFakeClockForScheduled;

    private TestScheduledExecutorService mTestScheduledExecutorService;

    private JobSchedulerTest.TestJobRunnable mTestJobRunnable;

    private JobScheduler mJobScheduler;

    @Test
    public void testUpdate_Intermediate() {
        EncodedImage encodedImage = fakeEncodedImage();
        Assert.assertTrue(mJobScheduler.updateJob(encodedImage, NO_FLAGS));
        assertNotSame(encodedImage, mJobScheduler.mEncodedImage);
        JobSchedulerTest.assertReferencesEqual(encodedImage, mJobScheduler.mEncodedImage);
        org.junit.Assert.assertEquals(NO_FLAGS, mJobScheduler.mStatus);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
    }

    @Test
    public void testUpdate_Intermediate_Invalid() {
        Assert.assertTrue(mJobScheduler.updateJob(fakeEncodedImage(), NO_FLAGS));
        assertFalse(mJobScheduler.updateJob(null, NO_FLAGS));
        assertNotNull(mJobScheduler.mEncodedImage);
        org.junit.Assert.assertEquals(NO_FLAGS, mJobScheduler.mStatus);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
    }

    @Test
    public void testUpdate_Last() {
        EncodedImage encodedImage = fakeEncodedImage();
        Assert.assertTrue(mJobScheduler.updateJob(encodedImage, IS_LAST));
        assertNotSame(encodedImage, mJobScheduler.mEncodedImage);
        JobSchedulerTest.assertReferencesEqual(encodedImage, mJobScheduler.mEncodedImage);
        org.junit.Assert.assertEquals(IS_LAST, mJobScheduler.mStatus);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
    }

    @Test
    public void testUpdate_Placeholder() {
        EncodedImage encodedImage = fakeEncodedImage();
        Assert.assertTrue(mJobScheduler.updateJob(encodedImage, IS_PLACEHOLDER));
        assertNotSame(encodedImage, mJobScheduler.mEncodedImage);
        JobSchedulerTest.assertReferencesEqual(encodedImage, mJobScheduler.mEncodedImage);
        org.junit.Assert.assertEquals(IS_PLACEHOLDER, mJobScheduler.mStatus);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
    }

    @Test
    public void testUpdate_Last_Null() {
        Assert.assertTrue(mJobScheduler.updateJob(fakeEncodedImage(), NO_FLAGS));
        Assert.assertTrue(mJobScheduler.updateJob(null, IS_LAST));
        org.junit.Assert.assertEquals(null, mJobScheduler.mEncodedImage);
        org.junit.Assert.assertEquals(IS_LAST, mJobScheduler.mStatus);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
    }

    @Test
    public void testClear() throws Exception {
        EncodedImage encodedImage = fakeEncodedImage();
        mJobScheduler.updateJob(encodedImage, IS_LAST);
        mJobScheduler.clearJob();
        org.junit.Assert.assertEquals(null, mJobScheduler.mEncodedImage);
        encodedImage.close();
        assertNull(encodedImage.getByteBufferRef());
    }

    @Test
    public void testSchedule_Intermediate() {
        EncodedImage encodedImage = fakeEncodedImage();
        mJobScheduler.updateJob(encodedImage, NO_FLAGS);
        Assert.assertTrue(mJobScheduler.scheduleJob());
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(1, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
        mFakeClockForTime.incrementBy(1234);
        mFakeClockForWorker.incrementBy(1234);
        mFakeClockForScheduled.incrementBy(1234);
        assertEquals(1, mTestJobRunnable.jobs.size());
        JobSchedulerTest.assertJobsEqual(mTestJobRunnable.jobs.get(0), encodedImage, NO_FLAGS);
    }

    @Test
    public void testSchedule_Intermediate_Invalid() {
        mJobScheduler.updateJob(null, NO_FLAGS);
        assertFalse(mJobScheduler.scheduleJob());
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
    }

    @Test
    public void testSchedule_Last_Null() {
        mJobScheduler.updateJob(null, IS_LAST);
        Assert.assertTrue(mJobScheduler.scheduleJob());
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(1, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
        mFakeClockForTime.incrementBy(1234);
        mFakeClockForWorker.incrementBy(1234);
        mFakeClockForScheduled.incrementBy(1234);
        assertEquals(1, mTestJobRunnable.jobs.size());
        JobSchedulerTest.assertJobsEqual(mTestJobRunnable.jobs.get(0), null, IS_LAST);
    }

    @Test
    public void testSchedule_Last_Idle() throws Exception {
        EncodedImage encodedImage = fakeEncodedImage();
        mJobScheduler.updateJob(encodedImage, IS_LAST);
        org.junit.Assert.assertEquals(JobScheduler.JobState.IDLE, mJobScheduler.mJobState);
        Assert.assertTrue(mJobScheduler.scheduleJob());
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(1, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
        mFakeClockForTime.incrementBy(1234);
        mFakeClockForWorker.incrementBy(1234);
        mFakeClockForScheduled.incrementBy(1234);
        assertEquals(1, mTestJobRunnable.jobs.size());
        JobSchedulerTest.assertJobsEqual(mTestJobRunnable.jobs.get(0), encodedImage, IS_LAST);
        mTestJobRunnable.jobs.get(0).encodedImage.close();
        encodedImage.close();
        assertNull(encodedImage.getByteBufferRef());
    }

    @Test
    public void testSchedule_Last_Queued() {
        mJobScheduler.updateJob(fakeEncodedImage(), IS_LAST);
        Assert.assertTrue(mJobScheduler.scheduleJob());
        EncodedImage encodedImage2 = fakeEncodedImage();
        mJobScheduler.updateJob(encodedImage2, IS_LAST);
        org.junit.Assert.assertEquals(JobScheduler.JobState.QUEUED, mJobScheduler.mJobState);
        Assert.assertTrue(mJobScheduler.scheduleJob());
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(1, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
        mFakeClockForTime.incrementBy(1234);
        mFakeClockForWorker.incrementBy(1234);
        mFakeClockForScheduled.incrementBy(1234);
        assertEquals(1, mTestJobRunnable.jobs.size());
        JobSchedulerTest.assertJobsEqual(mTestJobRunnable.jobs.get(0), encodedImage2, IS_LAST);
    }

    @Test
    public void testSchedule_Last_Running_And_Pending() {
        EncodedImage encodedImage1 = fakeEncodedImage();
        mJobScheduler.updateJob(encodedImage1, IS_LAST);
        Assert.assertTrue(mJobScheduler.scheduleJob());
        final EncodedImage encodedImage2 = fakeEncodedImage();
        final EncodedImage encodedImage3 = fakeEncodedImage();
        Executors.newFixedThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                // wait until the job starts running
                JobSchedulerTest.waitForCondition(mTestJobRunnable.running, true);
                org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
                org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
                assertEquals(0, mTestJobRunnable.jobs.size());
                mJobScheduler.updateJob(encodedImage2, IS_LAST);
                org.junit.Assert.assertEquals(JobScheduler.JobState.RUNNING, mJobScheduler.mJobState);
                Assert.assertTrue(mJobScheduler.scheduleJob());
                org.junit.Assert.assertEquals(JobScheduler.JobState.RUNNING_AND_PENDING, mJobScheduler.mJobState);
                mJobScheduler.updateJob(encodedImage3, IS_LAST);
                org.junit.Assert.assertEquals(JobScheduler.JobState.RUNNING_AND_PENDING, mJobScheduler.mJobState);
                Assert.assertTrue(mJobScheduler.scheduleJob());
                org.junit.Assert.assertEquals(JobScheduler.JobState.RUNNING_AND_PENDING, mJobScheduler.mJobState);
                mTestJobRunnable.wait.set(false);
            }
        });
        // block running until the above code executed on another thread finishes
        mTestJobRunnable.wait.set(true);
        mFakeClockForTime.incrementBy(0);
        mFakeClockForScheduled.incrementBy(0);
        mFakeClockForWorker.incrementBy(0);// this line blocks

        org.junit.Assert.assertEquals(JobScheduler.JobState.QUEUED, mJobScheduler.mJobState);
        org.junit.Assert.assertEquals(1, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(1, mTestJobRunnable.jobs.size());
        JobSchedulerTest.assertJobsEqual(mTestJobRunnable.jobs.get(0), encodedImage1, IS_LAST);
        mFakeClockForTime.incrementBy(JobSchedulerTest.INTERVAL);
        mFakeClockForScheduled.incrementBy(JobSchedulerTest.INTERVAL);
        mFakeClockForWorker.incrementBy(JobSchedulerTest.INTERVAL);
        org.junit.Assert.assertEquals(JobScheduler.JobState.IDLE, mJobScheduler.mJobState);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(2, mTestJobRunnable.jobs.size());
        JobSchedulerTest.assertJobsEqual(mTestJobRunnable.jobs.get(1), encodedImage3, IS_LAST);
    }

    @Test
    public void testSchedule_TooSoon() {
        EncodedImage encodedImage1 = fakeEncodedImage();
        mJobScheduler.updateJob(encodedImage1, NO_FLAGS);
        mJobScheduler.scheduleJob();
        mFakeClockForTime.incrementBy(1234);
        mFakeClockForWorker.incrementBy(1234);
        mFakeClockForScheduled.incrementBy(1234);
        EncodedImage encodedImage2 = fakeEncodedImage();
        mJobScheduler.updateJob(encodedImage2, IS_LAST);
        mFakeClockForTime.incrementBy(((JobSchedulerTest.INTERVAL) - 5));
        mFakeClockForWorker.incrementBy(((JobSchedulerTest.INTERVAL) - 5));
        mFakeClockForScheduled.incrementBy(((JobSchedulerTest.INTERVAL) - 5));
        mJobScheduler.scheduleJob();
        mFakeClockForTime.incrementBy(0);
        mFakeClockForWorker.incrementBy(0);
        mFakeClockForScheduled.incrementBy(0);
        org.junit.Assert.assertEquals(1, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(5, mTestScheduledExecutorService.getScheduledQueue().getNextPendingCommandDelay());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(1, mTestJobRunnable.jobs.size());
        mFakeClockForTime.incrementBy(5);
        mFakeClockForWorker.incrementBy(5);
        mFakeClockForScheduled.incrementBy(5);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(1, mTestExecutorService.getPendingCount());
        assertEquals(1, mTestJobRunnable.jobs.size());
        mFakeClockForTime.incrementBy(0);
        mFakeClockForWorker.incrementBy(0);
        mFakeClockForScheduled.incrementBy(0);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(2, mTestJobRunnable.jobs.size());
        JobSchedulerTest.assertJobsEqual(mTestJobRunnable.jobs.get(1), encodedImage2, IS_LAST);
    }

    @Test
    public void testFailure() {
        mJobScheduler.updateJob(fakeEncodedImage(), NO_FLAGS);
        mJobScheduler.scheduleJob();
        mTestJobRunnable.fail.set(true);
        try {
            mFakeClockForTime.incrementBy(1234);
            mFakeClockForWorker.incrementBy(0);
            fail("job should have failed, but it didn't.");
        } catch (Exception e) {
            // expected
        }
        org.junit.Assert.assertEquals(JobScheduler.JobState.IDLE, mJobScheduler.mJobState);
        org.junit.Assert.assertEquals(0, mTestScheduledExecutorService.getPendingCount());
        org.junit.Assert.assertEquals(0, mTestExecutorService.getPendingCount());
        assertEquals(0, mTestJobRunnable.jobs.size());
    }
}

