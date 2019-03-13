package com.birbit.android.jobqueue.test.jobmanager;


import TagConstraint.ANY;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelResult;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.log.JqLog;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CancelWhileRunningTest extends JobManagerTestBase {
    @Test
    public void testCancelBeforeRunning() throws InterruptedException {
        JobManager jobManager = createJobManager(minConsumerCount(5).timer(mockTimer));
        CancelWhileRunningTest.JobWithEndLatch nonPersistent1 = new CancelWhileRunningTest.JobWithEndLatch(new Params(0).addTags("dummyTag"), true);
        CancelWhileRunningTest.JobWithEndLatch nonPersistent2 = new CancelWhileRunningTest.JobWithEndLatch(new Params(0).addTags("dummyTag"), false);
        DummyJob persistentJob1 = new CancelWhileRunningTest.PersistentJobWithEndLatch(new Params(0).addTags("dummyTag"), false);
        DummyJob persistentJob2 = new CancelWhileRunningTest.PersistentJobWithEndLatch(new Params(0).addTags("dummyTag"), true);
        jobManager.addJob(nonPersistent1);
        jobManager.addJob(nonPersistent2);
        jobManager.addJob(persistentJob1);
        jobManager.addJob(persistentJob2);
        CancelWhileRunningTest.onStartLatch.await();
        nonPersistent1.onStartLatch.await();
        nonPersistent2.onStartLatch.await();
        final CancelResult[] resultHolder = new CancelResult[2];
        final CountDownLatch cancelLatch = new CountDownLatch(1);
        jobManager.cancelJobsInBackground(new CancelResult.AsyncCancelCallback() {
            @Override
            public void onCancelled(CancelResult cancelResult) {
                resultHolder[0] = cancelResult;
                cancelLatch.countDown();
            }
        }, ANY, "dummyTag");
        jobManager.cancelJobsInBackground(new CancelResult.AsyncCancelCallback() {
            @Override
            public void onCancelled(CancelResult cancelResult) {
                resultHolder[1] = cancelResult;
            }
        }, ANY, "dummyTag");
        MatcherAssert.assertThat("result should not arrive until existing jobs finish", cancelLatch.await(4, TimeUnit.SECONDS), CoreMatchers.is(false));
        CancelWhileRunningTest.onEndLatch.countDown();
        nonPersistent1.onEndLatch.countDown();
        nonPersistent2.onEndLatch.countDown();
        MatcherAssert.assertThat("when jobs in question are finished, cancel callback should be triggered", cancelLatch.await(10, TimeUnit.SECONDS), CoreMatchers.is(true));
        final CancelResult result = resultHolder[0];
        JqLog.d("cancelled jobs %s", result.getCancelledJobs());
        JqLog.d("failed to cancel %s", result.getFailedToCancel());
        MatcherAssert.assertThat("two jobs should be cancelled", result.getCancelledJobs().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat("two jobs should fail to cancel", result.getFailedToCancel().size(), CoreMatchers.is(2));
        for (Job j : result.getCancelledJobs()) {
            CancelWhileRunningTest.FailingJob job = ((CancelWhileRunningTest.FailingJob) (j));
            if (!(isPersistent())) {
                MatcherAssert.assertThat("job is still added", job.getOnAddedCnt(), CoreMatchers.is(1));
            }
            if (job.fail) {
                MatcherAssert.assertThat("job is cancelled", job.getOnCancelCnt(), CoreMatchers.is(1));
            } else {
                MatcherAssert.assertThat("job could not be cancelled", job.getOnCancelCnt(), CoreMatchers.is(0));
            }
        }
        for (Job j : result.getFailedToCancel()) {
            CancelWhileRunningTest.FailingJob job = ((CancelWhileRunningTest.FailingJob) (j));
            if (!(isPersistent())) {
                MatcherAssert.assertThat("job is still added", job.getOnAddedCnt(), CoreMatchers.is(1));
            }
            if (job.fail) {
                MatcherAssert.assertThat("job is cancelled", job.getOnCancelCnt(), CoreMatchers.is(1));
            } else {
                MatcherAssert.assertThat("job could not be cancelled", job.getOnCancelCnt(), CoreMatchers.is(0));
            }
        }
        MatcherAssert.assertThat("second cancel should not cancel anything", resultHolder[1].getCancelledJobs().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat("second cancel should not cancel anything", resultHolder[1].getFailedToCancel().size(), CoreMatchers.is(0));
    }

    public static CountDownLatch onStartLatch = new CountDownLatch(2);

    public static CountDownLatch onEndLatch = new CountDownLatch(1);

    public static class PersistentJobWithEndLatch extends CancelWhileRunningTest.FailingJob {
        public PersistentJobWithEndLatch(Params params, boolean fail) {
            super(params.persist(), fail);
        }

        @Override
        public void onRun() throws Throwable {
            JqLog.d("starting running %s", this);
            CancelWhileRunningTest.onStartLatch.countDown();
            CancelWhileRunningTest.onEndLatch.await();
            if (fail) {
                throw new RuntimeException("been asked to fail");
            }
            JqLog.d("finished w/ success %s", this);
        }
    }

    public static class JobWithEndLatch extends CancelWhileRunningTest.FailingJob {
        public final CountDownLatch onStartLatch = new CountDownLatch(1);

        public final CountDownLatch onEndLatch = new CountDownLatch(1);

        public JobWithEndLatch(Params params, boolean fail) {
            super(params, fail);
        }

        @Override
        public void onRun() throws Throwable {
            JqLog.d("starting running %s", this);
            onStartLatch.countDown();
            onEndLatch.await();
            if (fail) {
                throw new RuntimeException("been asked to fail");
            }
            JqLog.d("finished w/ success %s", this);
        }
    }

    public static class FailingJob extends DummyJob {
        private static int idCounter = 0;

        final boolean fail;

        final int id = (CancelWhileRunningTest.FailingJob.idCounter)++;

        public FailingJob(Params params, boolean fail) {
            super(params);
            this.fail = fail;
        }

        @Override
        public String toString() {
            return (((((getClass().getSimpleName()) + "[") + (id)) + "](") + (System.identityHashCode(this))) + ")";
        }
    }
}

