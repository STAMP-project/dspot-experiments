package com.birbit.android.jobqueue.test.jobmanager;


import CancelReason.REACHED_DEADLINE;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HitDeadlineAfterException extends JobManagerTestBase {
    @Test
    public void failAndHitDeadline() throws InterruptedException {
        final JobManager jobManager = createJobManager();
        final AtomicBoolean calledShouldReRun = new AtomicBoolean(false);
        final AtomicInteger reason = new AtomicInteger();
        final DummyJob job = new DummyJob(new Params(0).overrideDeadlineToCancelInMs(100)) {
            @Override
            public void onRun() throws Throwable {
                super.onRun();
                mockTimer.incrementMs(150);
                throw new RuntimeException("why not fail");
            }

            @Override
            protected void onCancel(@CancelReason
            int cancelReason, @Nullable
            Throwable throwable) {
                reason.set(cancelReason);
                super.onCancel(cancelReason, throwable);
            }

            @Override
            protected RetryConstraint shouldReRunOnThrowable(@NonNull
            Throwable throwable, int runCount, int maxRunCount) {
                calledShouldReRun.set(true);
                return RetryConstraint.RETRY;
            }
        };
        waitUntilAJobIsDone(jobManager, new JobManagerTestBase.WaitUntilCallback() {
            @Override
            public void run() {
                jobManager.addJob(job);
            }

            @Override
            public void assertJob(Job job) {
            }
        });
        MatcherAssert.assertThat(reason.get(), CoreMatchers.is(REACHED_DEADLINE));
        MatcherAssert.assertThat(calledShouldReRun.get(), CoreMatchers.is(false));
    }
}

