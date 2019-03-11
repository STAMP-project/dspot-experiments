package com.birbit.android.jobqueue.test.jobmanager;


import Build.VERSION_CODES;
import JobStatus.RUNNING;
import JobStatus.UNKNOWN;
import JobStatus.WAITING_NOT_READY;
import JobStatus.WAITING_READY;
import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.JobStatus;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.callback.JobManagerCallbackAdapter;
import com.birbit.android.jobqueue.test.jobs.DummyJob;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class JobStatusTest extends JobManagerTestBase {
    private static final String REQ_NETWORK_TAG = "reqNetwork";

    @TargetApi(VERSION_CODES.GINGERBREAD)
    @Test
    public void testJobStatus() throws InterruptedException {
        JobManagerTestBase.DummyNetworkUtilWithConnectivityEventSupport networkUtil = new JobManagerTestBase.DummyNetworkUtilWithConnectivityEventSupport();
        networkUtil.setNetworkStatus(DISCONNECTED, true);
        final JobManager jobManager = createJobManager(networkUtil(networkUtil).timer(mockTimer));
        jobManager.stop();
        List<Integer> networkRequiringJobIndices = new ArrayList<Integer>();
        Job[] jobs = new Job[]{ new DummyJob(new Params(0)), new DummyJob(new Params(0).persist()), new DummyJob(new Params(0).persist().requireNetwork().addTags(JobStatusTest.REQ_NETWORK_TAG)) };
        String[] ids = new String[jobs.length];
        networkRequiringJobIndices.add(2);
        for (int i = 0; i < (jobs.length); i++) {
            jobManager.addJob(jobs[i]);
            ids[i] = getId();
            JobStatus expectedStatus = ((!(networkUtil.isDisconnected())) || (!(networkRequiringJobIndices.contains(i)))) ? JobStatus.WAITING_READY : JobStatus.WAITING_NOT_READY;
            MatcherAssert.assertThat("job should have correct status after being added", jobManager.getJobStatus(ids[i]), CoreMatchers.is(expectedStatus));
        }
        // create an unknown id, ensure status for that
        boolean exists;
        String unknownId;
        do {
            unknownId = UUID.randomUUID().toString();
            exists = false;
            for (String id : ids) {
                if (unknownId.equals(id)) {
                    exists = true;
                }
            }
        } while (exists );
        for (boolean persistent : new boolean[]{ true, false }) {
            MatcherAssert.assertThat("job with unknown id should return as expected", jobManager.getJobStatus(unknownId), CoreMatchers.is(UNKNOWN));
        }
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        final JobManagerTestBase.DummyTwoLatchJob twoLatchJob = new JobManagerTestBase.DummyTwoLatchJob(new Params(0), startLatch, endLatch);
        jobManager.start();
        jobManager.addJob(twoLatchJob);
        final String jobId = getId();
        twoLatchJob.waitTillOnRun();
        final CountDownLatch twoLatchJobDone = new CountDownLatch(1);
        jobManager.addCallback(new JobManagerCallbackAdapter() {
            @Override
            public void onAfterJobRun(@NonNull
            Job job, int resultCode) {
                if ((job == twoLatchJob) && (resultCode == (RESULT_SUCCEED))) {
                    jobManager.removeCallback(this);
                    twoLatchJobDone.countDown();
                }
            }
        });
        MatcherAssert.assertThat("job should be in running state", jobManager.getJobStatus(jobId), CoreMatchers.is(RUNNING));
        startLatch.countDown();// let it run

        try {
            endLatch.await();// wait till it finishes

        } catch (InterruptedException ignored) {
        }
        twoLatchJobDone.await(1, TimeUnit.MINUTES);
        MatcherAssert.assertThat(("finished job should go to unknown state. id: " + jobId), jobManager.getJobStatus(jobId), CoreMatchers.is(UNKNOWN));
        // network requiring job should not be ready
        for (Integer i : networkRequiringJobIndices) {
            MatcherAssert.assertThat("network requiring job should still be not-ready", jobManager.getJobStatus(ids[i]), CoreMatchers.is(WAITING_NOT_READY));
        }
        jobManager.stop();
        networkUtil.setNetworkStatus(METERED, true);
        for (Integer i : networkRequiringJobIndices) {
            MatcherAssert.assertThat("network requiring job should still be ready after network is there", jobManager.getJobStatus(ids[i]), CoreMatchers.is(WAITING_READY));
        }
        final CountDownLatch networkRequiredLatch = new CountDownLatch(networkRequiringJobIndices.size());
        jobManager.addCallback(new JobManagerCallbackAdapter() {
            @Override
            public void onDone(@NonNull
            Job job) {
                if (job.getTags().contains(JobStatusTest.REQ_NETWORK_TAG)) {
                    networkRequiredLatch.countDown();
                }
            }
        });
        jobManager.start();
        networkRequiredLatch.await(1, TimeUnit.MINUTES);
        MatcherAssert.assertThat("jobs should finish", jobManager.count(), CoreMatchers.is(0));
        for (int i = 0; i < (jobs.length); i++) {
            // after all jobs finish, state should be unknown
            MatcherAssert.assertThat("all jobs finished, states should be unknown", jobManager.getJobStatus(ids[i]), CoreMatchers.is(UNKNOWN));
        }
        final long SHORT_SLEEP = 2000;
        Job[] delayedJobs = new Job[]{ new DummyJob(new Params(0).delayInMs(SHORT_SLEEP)), new DummyJob(new Params(0).delayInMs(SHORT_SLEEP).persist()), new DummyJob(new Params(0).delayInMs((SHORT_SLEEP * 10))), new DummyJob(new Params(0).delayInMs((SHORT_SLEEP * 10)).persist()) };
        String[] delayedIds = new String[delayedJobs.length];
        long start = mockTimer.nanoTime();
        for (int i = 0; i < (delayedJobs.length); i++) {
            jobManager.addJob(delayedJobs[i]);
            delayedIds[i] = getId();
        }
        for (int i = 0; i < (delayedJobs.length); i++) {
            MatcherAssert.assertThat(((("delayed job(" + i) + ") should receive not ready status. startMs:") + start), jobManager.getJobStatus(delayedIds[i]), CoreMatchers.is(WAITING_NOT_READY));
        }
        jobManager.stop();
        // sleep
        mockTimer.incrementMs((SHORT_SLEEP * 2));
        for (int i = 0; i < (delayedJobs.length); i++) {
            if ((delayedJobs[i].getDelayInMs()) == SHORT_SLEEP) {
                MatcherAssert.assertThat("when enough time passes, delayed jobs should move to ready state", jobManager.getJobStatus(delayedIds[i]), CoreMatchers.is(WAITING_READY));
            } else {
                MatcherAssert.assertThat("delayed job should receive not ready status until their time comes", jobManager.getJobStatus(delayedIds[i]), CoreMatchers.is(WAITING_NOT_READY));
            }
        }
    }
}

