package com.evernote.android.job;


import Build.VERSION_CODES;
import Job.Result.FAILURE;
import Job.Result.RESCHEDULE;
import Job.Result.SUCCESS;
import JobProxy.Common;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.evernote.android.job.test.DummyJobs;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import com.evernote.android.job.test.TestLogger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.annotation.Config;

import static Result.RESCHEDULE;
import static Result.SUCCESS;
import static com.evernote.android.job.test.DummyJobs.SuccessJob.TAG;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class JobExecutionTest extends BaseJobManagerTest {
    @Test
    public void testPeriodicJob() throws Exception {
        int jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build().schedule();
        executeJob(jobId, SUCCESS);
        // make sure job request is still around
        assertThat(manager().getAllJobRequestsForTag(TAG)).hasSize(1);
    }

    @Test
    public void testSimpleJob() throws Throwable {
        final int jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setExecutionWindow(200000L, 400000L).build().schedule();
        executeJob(jobId, SUCCESS);
        assertThat(manager().getAllJobRequestsForTag(TAG)).isEmpty();
        assertThat(manager().getJobRequest(jobId)).isNull();
        assertThat(manager().getJobRequest(jobId, true)).isNull();
    }

    @Test
    public void testStartedState() throws Throwable {
        int jobId = DummyJobs.createBuilder(DummyJobs.TwoSecondPauseJob.class).setExecutionWindow(300000, 400000).build().schedule();
        executeJobAsync(jobId, SUCCESS);
        // wait until the job is started
        Thread.sleep(100);
        // request should be in started state, running but not removed from DB
        JobRequest startedRequest = manager().getJobRequest(jobId, true);
        assertThat(startedRequest).isNotNull();
        assertThat(startedRequest.isStarted()).isTrue();
    }

    @Test
    public void testPeriodicJobNotInStartedState() throws Throwable {
        int jobId = DummyJobs.createBuilder(DummyJobs.TwoSecondPauseJob.class).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build().schedule();
        executeJobAsync(jobId, SUCCESS);
        // wait until the job is started
        Thread.sleep(100);
        // request should be in started state, running but not removed from DB
        JobRequest startedRequest = manager().getJobRequest(jobId, true);
        assertThat(startedRequest).isNotNull();
        assertThat(startedRequest.isStarted()).isFalse();
    }

    @Test
    public void verifyNoRaceConditionOneOff() throws Exception {
        final int jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setExecutionWindow(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(20)).build().schedule();
        final JobProxy.Common common = new JobProxy.Common(context(), TestLogger.INSTANCE, jobId);
        final JobRequest request = common.getPendingRequest(true, true);
        assertThat(request).isNotNull();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                common.executeJobRequest(request, null);
                latch.countDown();
            }
        }.start();
        assertThat(common.getPendingRequest(true, false)).isNull();
        assertThat(latch.await(3, TimeUnit.SECONDS)).isTrue();
        Thread.sleep(2000);
        assertThat(common.getPendingRequest(true, false)).isNull();
    }

    @Test
    public void verifyNoRaceConditionPeriodic() throws Exception {
        final int jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build().schedule();
        final JobProxy.Common common = new JobProxy.Common(context(), TestLogger.INSTANCE, jobId);
        final JobRequest request = common.getPendingRequest(true, true);
        assertThat(request).isNotNull();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                common.executeJobRequest(request, null);
                latch.countDown();
            }
        }.start();
        assertThat(common.getPendingRequest(true, false)).isNull();
        assertThat(latch.await(3, TimeUnit.SECONDS)).isTrue();
        Thread.sleep(2000);
        assertThat(common.getPendingRequest(true, false)).isNotNull();
    }

    @Test
    public void verifyPendingRequestNullWhenMarkedStated() {
        final int jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build().schedule();
        final JobProxy.Common common = new JobProxy.Common(context(), TestLogger.INSTANCE, jobId);
        assertThat(common.getPendingRequest(true, false)).isNotNull();
        assertThat(common.getPendingRequest(true, false)).isNotNull();
        JobRequest request = common.getPendingRequest(true, false);
        assertThat(request).isNotNull();
        common.markStarting(request);
        assertThat(common.getPendingRequest(true, false)).isNull();
    }

    @Test
    public void verifyCanceledJobNotRescheduled() {
        final AtomicBoolean onRescheduleCalled = new AtomicBoolean(false);
        final Job job = new Job() {
            @NonNull
            @Override
            protected Result onRunJob(@NonNull
            Params params) {
                manager().cancelAll();
                return RESCHEDULE;
            }

            @Override
            protected void onReschedule(int newJobId) {
                onRescheduleCalled.set(true);
            }
        };
        JobCreator jobCreator = new JobCreator() {
            @Override
            public Job create(@NonNull
            String tag) {
                return job;
            }
        };
        manager().addJobCreator(jobCreator);
        final String tag = "something";
        final int jobId = new JobRequest.Builder(tag).setExecutionWindow(200000L, 400000L).build().schedule();
        executeJob(jobId, RESCHEDULE);
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
        assertThat(manager().getJobRequest(jobId)).isNull();
        assertThat(manager().getJobRequest(jobId, true)).isNull();
        assertThat(onRescheduleCalled.get()).isFalse();
    }

    @Test
    public void verifySynchronizedAllowed() throws InterruptedException {
        final CountDownLatch start = new CountDownLatch(1);
        final Job job = new Job() {
            @NonNull
            @Override
            protected synchronized Result onRunJob(@NonNull
            Params params) {
                start.countDown();
                try {
                    Thread.sleep(8000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return SUCCESS;
            }
        };
        JobCreator jobCreator = new JobCreator() {
            @Override
            public Job create(@NonNull
            String tag) {
                return job;
            }
        };
        manager().addJobCreator(jobCreator);
        final int jobId = new JobRequest.Builder("something").setExecutionWindow(200000L, 400000L).build().schedule();
        executeJobAsync(jobId, SUCCESS);
        assertThat(start.await(2, TimeUnit.SECONDS)).isTrue();
        final CountDownLatch canceledWithin2Seconds = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                job.cancel();
                canceledWithin2Seconds.countDown();
            }
        }.start();
        assertThat(canceledWithin2Seconds.await(2, TimeUnit.MILLISECONDS)).isTrue();
    }

    @Test
    public void verifyReschedulingTransientJobsWorks() {
        Bundle extras = new Bundle();
        extras.putString("key", "hello");
        int previousJobId = DummyJobs.createBuilder(DummyJobs.RescheduleJob.class).setExecutionWindow(200000L, 400000L).setTransientExtras(extras).build().schedule();
        for (int i = 0; i < 5; i++) {
            executeJob(previousJobId, RESCHEDULE);
            assertThat(manager().getAllJobRequestsForTag(DummyJobs.RescheduleJob.TAG)).hasSize(1);
            JobRequest request = manager().getAllJobRequestsForTag(DummyJobs.RescheduleJob.TAG).iterator().next();
            assertThat(request.getJobId()).isNotEqualTo(previousJobId);
            assertThat(request.getTransientExtras().getString("key", null)).isEqualTo("hello");
            previousJobId = request.getJobId();
        }
    }

    @Test
    public void verifyNotFoundJobCanceledOneOff() {
        final String tag = "something";
        final int jobId = new JobRequest.Builder(tag).setExecutionWindow(TimeUnit.HOURS.toMillis(4), TimeUnit.HOURS.toMillis(5)).build().schedule();
        assertThat(manager().getAllJobRequestsForTag(tag)).hasSize(1);
        executeJob(jobId, FAILURE);
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
    }

    @Test
    public void verifyNotFoundJobCanceledExact() {
        final String tag = "something";
        final int jobId = new JobRequest.Builder(tag).setExact(TimeUnit.HOURS.toMillis(4)).build().schedule();
        assertThat(manager().getAllJobRequestsForTag(tag)).hasSize(1);
        executeJob(jobId, FAILURE);
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
    }

    @Test
    public void verifyNotFoundJobCanceledDailyJob() {
        final String tag = "something";
        int jobId = DailyJob.schedule(new JobRequest.Builder(tag), TimeUnit.HOURS.toMillis(5), TimeUnit.HOURS.toMillis(6));
        assertThat(manager().getAllJobRequestsForTag(tag)).hasSize(1);
        executeJob(jobId, FAILURE);
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
    }

    @Test
    public void verifyNotFoundJobCanceledPeriodic() {
        final String tag = "something";
        final int jobId = new JobRequest.Builder(tag).setPeriodic(TimeUnit.HOURS.toMillis(4)).build().schedule();
        assertThat(manager().getAllJobRequestsForTag(tag)).hasSize(1);
        executeJob(jobId, FAILURE);
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
    }

    @Test
    @Config(sdk = VERSION_CODES.M)
    public void verifyNotFoundJobCanceledPeriodicFlexSupport() {
        final String tag = "something";
        final int jobId = new JobRequest.Builder(tag).setPeriodic(TimeUnit.HOURS.toMillis(4)).build().schedule();
        assertThat(manager().getAllJobRequestsForTag(tag)).hasSize(1);
        executeJob(jobId, FAILURE);
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
    }
}

