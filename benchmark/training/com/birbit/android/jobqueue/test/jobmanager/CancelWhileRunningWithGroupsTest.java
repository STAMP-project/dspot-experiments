package com.birbit.android.jobqueue.test.jobmanager;


import TagConstraint.ANY;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.CancelResult;
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
public class CancelWhileRunningWithGroupsTest extends JobManagerTestBase {
    public static CountDownLatch[] endLatches = new CountDownLatch[]{ new CountDownLatch(2), new CountDownLatch(2) };

    public static CountDownLatch[] startLatches = new CountDownLatch[]{ new CountDownLatch(2), new CountDownLatch(2) };

    @Test
    public void testCancelBeforeRunning() throws InterruptedException {
        JobManager jobManager = createJobManager(minConsumerCount(5).timer(mockTimer));
        CancelWhileRunningWithGroupsTest.DummyJobWithLatches nonPersistentJob = new CancelWhileRunningWithGroupsTest.DummyJobWithLatches(0, new Params(1).addTags("dummyTag").groupBy("group1"));
        jobManager.addJob(nonPersistentJob);
        CancelWhileRunningWithGroupsTest.DummyJobWithLatches persistentJob = new CancelWhileRunningWithGroupsTest.DummyJobWithLatches(0, new Params(1).addTags("dummyTag").groupBy("group2").persist());
        jobManager.addJob(persistentJob);
        MatcherAssert.assertThat("both jobs should start", CancelWhileRunningWithGroupsTest.startLatches[0].await(2, TimeUnit.SECONDS), CoreMatchers.is(true));
        final CancelResult[] cancelResults = new CancelResult[1];
        final CountDownLatch resultLatch = new CountDownLatch(1);
        CancelWhileRunningWithGroupsTest.startLatches[0].await(2, TimeUnit.SECONDS);
        jobManager.cancelJobsInBackground(new CancelResult.AsyncCancelCallback() {
            @Override
            public void onCancelled(CancelResult cancelResult) {
                cancelResults[0] = cancelResult;
                resultLatch.countDown();
            }
        }, ANY, "dummyTag");
        while (!(isCancelled())) {
            // wait
        } 
        CancelWhileRunningWithGroupsTest.endLatches[0].countDown();
        CancelWhileRunningWithGroupsTest.endLatches[0].countDown();
        MatcherAssert.assertThat("result should come after jobs end", resultLatch.await(2, TimeUnit.SECONDS), CoreMatchers.is(true));
        MatcherAssert.assertThat("no jobs should be canceled", cancelResults[0].getCancelledJobs().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat("both jobs should fail to cancel", cancelResults[0].getFailedToCancel().size(), CoreMatchers.is(2));
        jobManager.addJob(new CancelWhileRunningWithGroupsTest.DummyJobWithLatches(1, new Params(1).addTags("dummyTag").groupBy("group1")));
        jobManager.addJob(new CancelWhileRunningWithGroupsTest.DummyJobWithLatches(1, new Params(1).addTags("dummyTag").groupBy("group2").persist()));
        MatcherAssert.assertThat("new jobs with canceled groups should start", CancelWhileRunningWithGroupsTest.startLatches[1].await(10, TimeUnit.SECONDS), CoreMatchers.is(true));
        CancelWhileRunningWithGroupsTest.endLatches[1].countDown();
        CancelWhileRunningWithGroupsTest.endLatches[1].countDown();
    }

    public static class DummyJobWithLatches extends DummyJob {
        final int latchIndex;

        public DummyJobWithLatches(int latchIndex, Params params) {
            super(params);
            this.latchIndex = latchIndex;
        }

        @Override
        public void onRun() throws Throwable {
            super.onRun();
            CancelWhileRunningWithGroupsTest.startLatches[latchIndex].countDown();
            CancelWhileRunningWithGroupsTest.endLatches[latchIndex].await(10, TimeUnit.SECONDS);
        }
    }
}

