package com.birbit.android.jobqueue.test.jobmanager;


import JobManagerCallback.RESULT_CANCEL_CANCELLED_VIA_SHOULD_RE_RUN;
import JobManagerCallback.RESULT_CANCEL_CANCELLED_WHILE_RUNNING;
import JobManagerCallback.RESULT_CANCEL_REACHED_RETRY_LIMIT;
import JobManagerCallback.RESULT_FAIL_WILL_RETRY;
import JobManagerCallback.RESULT_SUCCEED;
import RetryConstraint.CANCEL;
import RetryConstraint.RETRY;
import TagConstraint.ANY;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.JobStatus;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.callback.JobManagerCallback;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CallbackTest extends JobManagerTestBase {
    @Test
    public void successNonPersistent() throws Throwable {
        JobManagerCallback callback = Mockito.mock(JobManagerCallback.class);
        final Job job = Mockito.spy(new CallbackTest.PublicJob(new Params(0)));
        Mockito.doNothing().when(job).onAdded();
        Mockito.doNothing().when(job).onRun();
        final JobManager jobManager = createJobManager();
        jobManager.addCallback(callback);
        waitUntilAJobIsDone(jobManager, new JobManagerTestBase.WaitUntilCallback() {
            @Override
            public void run() {
                jobManager.addJob(job);
            }

            @Override
            public void assertJob(Job job) {
            }
        });
        Mockito.verify(job).onAdded();
        Mockito.verify(job).onRun();
        Mockito.verify(callback).onJobAdded(job);
        Mockito.verify(callback).onJobRun(job, RESULT_SUCCEED);
    }

    @Test
    public void cancelViaRetryLimit() throws Throwable {
        final Throwable error = new Exception();
        JobManagerCallback callback = Mockito.mock(JobManagerCallback.class);
        final CallbackTest.PublicJob job = Mockito.spy(new CallbackTest.PublicJob(new Params(0)));
        Mockito.doNothing().when(job).onAdded();
        Mockito.doThrow(error).when(job).onRun();
        Mockito.doReturn(3).when(job).getRetryLimit();
        Mockito.doReturn(RETRY).when(job).shouldReRunOnThrowable(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        final JobManager jobManager = createJobManager();
        jobManager.addCallback(callback);
        waitUntilAJobIsDone(jobManager, new JobManagerTestBase.WaitUntilCallback() {
            @Override
            public void run() {
                jobManager.addJob(job);
            }

            @Override
            public void assertJob(Job job) {
            }
        });
        Mockito.verify(callback).onJobAdded(job);
        Mockito.verify(callback, Mockito.times(2)).onJobRun(job, RESULT_FAIL_WILL_RETRY);
        Mockito.verify(callback, Mockito.times(1)).onJobRun(job, RESULT_CANCEL_REACHED_RETRY_LIMIT);
        Mockito.verify(callback).onJobCancelled(job, false, error);
    }

    @Test
    public void cancelViaShouldReRun() throws Throwable {
        JobManagerCallback callback = Mockito.mock(JobManagerCallback.class);
        final CallbackTest.PublicJob job = Mockito.spy(new CallbackTest.PublicJob(new Params(0)));
        Mockito.doNothing().when(job).onAdded();
        Mockito.doThrow(new Exception()).when(job).onRun();
        Mockito.doReturn(3).when(job).getRetryLimit();
        Mockito.doReturn(CANCEL).when(job).shouldReRunOnThrowable(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        final JobManager jobManager = createJobManager();
        jobManager.addCallback(callback);
        waitUntilAJobIsDone(jobManager, new JobManagerTestBase.WaitUntilCallback() {
            @Override
            public void run() {
                jobManager.addJob(job);
            }

            @Override
            public void assertJob(Job job) {
            }
        });
        Mockito.verify(callback).onJobAdded(job);
        Mockito.verify(callback, Mockito.times(1)).onJobRun(job, RESULT_CANCEL_CANCELLED_VIA_SHOULD_RE_RUN);
        Mockito.verify(callback).onJobCancelled(ArgumentMatchers.eq(job), ArgumentMatchers.eq(false), ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void cancelViaCancelCall() throws Throwable {
        JobManagerCallback callback = Mockito.mock(JobManagerCallback.class);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        final Throwable[] jobError = new Throwable[1];
        CallbackTest.PublicJob job = Mockito.spy(new CallbackTest.PublicJob(new Params(1).addTags("tag1")) {
            @Override
            public void onRun() throws Throwable {
                startLatch.countDown();
                try {
                    Assert.assertThat(endLatch.await(30, TimeUnit.SECONDS), CoreMatchers.is(true));
                } catch (Throwable t) {
                    jobError[0] = t;
                }
                throw new Exception("blah");
            }
        });
        Mockito.doCallRealMethod().when(job).onRun();
        Mockito.doReturn(3).when(job).getRetryLimit();
        JobManager jobManager = createJobManager();
        jobManager.addCallback(callback);
        jobManager.addJob(job);
        Assert.assertThat(startLatch.await(30, TimeUnit.SECONDS), CoreMatchers.is(true));
        jobManager.cancelJobsInBackground(null, ANY, "tag1");
        // noinspection StatementWithEmptyBody
        while (!(isCancelled())) {
            // busy wait until cancel arrives
            // noinspection SLEEP_IN_CODE
            Thread.sleep(100);
        } 
        endLatch.countDown();
        while ((jobManager.getJobStatus(getId())) != (JobStatus.UNKNOWN)) {
            // busy wait until job cancel is handled
            // noinspection SLEEP_IN_CODE
            Thread.sleep(100);
        } 
        MatcherAssert.assertThat(jobError[0], CoreMatchers.nullValue());
        Mockito.verify(job, Mockito.times(0)).shouldReRunOnThrowable(ArgumentMatchers.any(Throwable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        jobManager.stopAndWaitUntilConsumersAreFinished();
        Mockito.verify(callback).onJobAdded(job);
        Mockito.verify(callback, Mockito.times(1)).onJobRun(job, RESULT_CANCEL_CANCELLED_WHILE_RUNNING);
        Mockito.verify(callback).onJobCancelled(job, true, null);
    }

    @Test
    public void successPersistent() throws Throwable {
        JobManagerCallback callback = Mockito.mock(JobManagerCallback.class);
        final Job job = new CallbackTest.PersistentDummyJob();
        final JobManager jobManager = createJobManager();
        jobManager.addCallback(callback);
        waitUntilAJobIsDone(jobManager, new JobManagerTestBase.WaitUntilCallback() {
            @Override
            public void run() {
                jobManager.addJob(job);
            }

            @Override
            public void assertJob(Job job) {
            }
        });
        Mockito.verify(callback).onJobAdded(ArgumentMatchers.any(CallbackTest.PersistentDummyJob.class));
        Mockito.verify(callback).onJobRun(ArgumentMatchers.any(CallbackTest.PersistentDummyJob.class), ArgumentMatchers.eq(RESULT_SUCCEED));
    }

    public static class PersistentDummyJob extends Job {
        public PersistentDummyJob() {
            super(new Params(1).persist());
        }

        @Override
        public void onAdded() {
        }

        @Override
        public void onRun() throws Throwable {
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            throw new RuntimeException("not expected to arrive here");
        }
    }

    public static class PublicJob extends Job {
        protected PublicJob(Params params) {
            super(params);
        }

        @Override
        public int getRetryLimit() {
            return super.getRetryLimit();
        }

        @Override
        public void onAdded() {
        }

        @Override
        public void onRun() throws Throwable {
        }

        @Override
        protected void onCancel(@CancelReason
        int cancelReason, @Nullable
        Throwable throwable) {
        }

        @Override
        protected RetryConstraint shouldReRunOnThrowable(@NonNull
        Throwable throwable, int runCount, int maxRunCount) {
            throw new UnsupportedOperationException("should not be called directly");
        }
    }
}

