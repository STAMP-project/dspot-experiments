package com.birbit.android.jobqueue.test.jobmanager;


import TagConstraint.ANY;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelResult;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
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
public class CancelBeforeRunningTest extends JobManagerTestBase {
    @Test
    public void testCancelBeforeRunning() {
        JobManager jobManager = createJobManager();
        jobManager.stop();
        DummyJob nonPersistentJob = new DummyJob(new Params(0).addTags("dummyTag"));
        DummyJob persistentJob = new DummyJob(new Params(0).addTags("dummyTag").persist());
        jobManager.addJob(nonPersistentJob);
        jobManager.addJob(persistentJob);
        CancelResult result = jobManager.cancelJobs(ANY, "dummyTag");
        MatcherAssert.assertThat("both jobs should be cancelled", result.getCancelledJobs().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat("both jobs should be cancelled", result.getFailedToCancel().size(), CoreMatchers.is(0));
        for (Job j : result.getCancelledJobs()) {
            DummyJob job = ((DummyJob) (j));
            if (!(isPersistent())) {
                MatcherAssert.assertThat("job is still added", job.getOnAddedCnt(), CoreMatchers.is(1));
            }
            MatcherAssert.assertThat("job is cancelled", job.getOnCancelCnt(), CoreMatchers.is(1));
            MatcherAssert.assertThat("job is NOT run", job.getOnRunCnt(), CoreMatchers.is(0));
        }
    }

    public static CountDownLatch persistentJobLatch = new CountDownLatch(1);

    CountDownLatch nonPersistentJobLatch = new CountDownLatch(1);

    @Test
    public void testCancelBeforeRunningWithGroups() throws InterruptedException {
        JobManager jobManager = createJobManager();
        jobManager.stop();
        DummyJob nonPersistentJob = new DummyJob(new Params(0).addTags("dummyTag").groupBy("group1"));
        DummyJob persistentJob = new DummyJob(new Params(0).addTags("dummyTag").persist().groupBy("group2"));
        jobManager.addJob(nonPersistentJob);
        jobManager.addJob(persistentJob);
        CancelResult result = jobManager.cancelJobs(ANY, "dummyTag");
        MatcherAssert.assertThat("both jobs should be cancelled", result.getCancelledJobs().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat("both jobs should be cancelled", result.getFailedToCancel().size(), CoreMatchers.is(0));
        for (Job j : result.getCancelledJobs()) {
            DummyJob job = ((DummyJob) (j));
            if (!(isPersistent())) {
                MatcherAssert.assertThat("job is still added", job.getOnAddedCnt(), CoreMatchers.is(1));
            }
            MatcherAssert.assertThat("job is cancelled", job.getOnCancelCnt(), CoreMatchers.is(1));
            MatcherAssert.assertThat("job is NOT run", job.getOnRunCnt(), CoreMatchers.is(0));
        }
        MatcherAssert.assertThat("there should not be any jobs in the queue", jobManager.count(), CoreMatchers.is(0));
        jobManager.start();
        DummyJob nonPersistentJob2 = new DummyJob(new Params(0).addTags("dummyTag").groupBy("group1")) {
            @Override
            public void onRun() throws Throwable {
                super.onRun();
                nonPersistentJobLatch.countDown();
            }
        };
        DummyJob persistentJob2 = new CancelBeforeRunningTest.PersistentDummyJob(new Params(0).addTags("dummyTag").groupBy("group2"));
        jobManager.addJob(persistentJob2);
        jobManager.addJob(nonPersistentJob2);
        MatcherAssert.assertThat("a new job in the same group with canceled job should run", nonPersistentJobLatch.await(1, TimeUnit.SECONDS), CoreMatchers.is(true));
        MatcherAssert.assertThat("a new persistent job in the same group with canceled job should run", CancelBeforeRunningTest.persistentJobLatch.await(2, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    public static class PersistentDummyJob extends DummyJob {
        public PersistentDummyJob(Params params) {
            super(params.persist());
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            CancelBeforeRunningTest.persistentJobLatch.countDown();
        }
    }
}

