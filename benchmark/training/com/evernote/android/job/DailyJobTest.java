package com.evernote.android.job;


import DailyJob.EXTRA_END_MS;
import DailyJob.EXTRA_START_MS;
import Job.Result.SUCCESS;
import android.support.annotation.NonNull;
import com.evernote.android.job.test.DummyJobs;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import com.evernote.android.job.test.TestClock;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static DailyJobResult.SUCCESS;
import static com.evernote.android.job.test.DummyJobs.SuccessJob.TAG;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class DailyJobTest extends BaseJobManagerTest {
    @Test
    public void verifyScheduleInNextHourMinute0() {
        TestClock clock = new TestClock();
        clock.setTime(20, 0);
        verifyScheduleInNextHour(clock);
    }

    @Test
    public void verifyScheduleInNextHourMinute57() {
        TestClock clock = new TestClock();
        clock.setTime(20, 57);
        verifyScheduleInNextHour(clock);
    }

    @Test
    public void verifyScheduleOverMidnight8pm() {
        TestClock clock = new TestClock();
        clock.setTime(20, 0);
        verifyScheduleOverMidnight(clock);
    }

    @Test
    public void verifyScheduleOverMidnightAtMidnight() {
        TestClock clock = new TestClock();
        clock.setTime(0, 0);
        verifyScheduleOverMidnight(clock);
    }

    @Test
    public void verifyScheduleAndExecutionInFuture() {
        TestClock clock = new TestClock();
        clock.setTime(0, 0);
        verifyExecutionAndSuccessfulReschedule(clock, TimeUnit.HOURS.toMillis(2), TimeUnit.HOURS.toMillis(6));
    }

    @Test
    public void verifyScheduleAndExecutionInPast() {
        TestClock clock = new TestClock();
        clock.setTime(12, 0);
        verifyExecutionAndSuccessfulReschedule(clock, TimeUnit.HOURS.toMillis(2), TimeUnit.HOURS.toMillis(6));
    }

    @Test
    public void verifyScheduleAndExecutionStartInPast() {
        TestClock clock = new TestClock();
        clock.setTime(4, 0);
        verifyExecutionAndSuccessfulReschedule(clock, TimeUnit.HOURS.toMillis(2), TimeUnit.HOURS.toMillis(6));
    }

    @Test
    public void verifyScheduleAndExecutionExactStart() {
        TestClock clock = new TestClock();
        clock.setTime(13, 0);
        verifyExecutionAndSuccessfulReschedule(clock, TimeUnit.HOURS.toMillis(13), TimeUnit.HOURS.toMillis(14));
    }

    @Test
    public void verifyScheduleAndExecutionExactEnd() {
        TestClock clock = new TestClock();
        clock.setTime(14, 0);
        verifyExecutionAndSuccessfulReschedule(clock, TimeUnit.HOURS.toMillis(13), TimeUnit.HOURS.toMillis(14));
    }

    @Test
    public void verifyScheduleAndExecutionOverMidnight() {
        TestClock clock = new TestClock();
        clock.setTime(0, 0);
        verifyExecutionAndSuccessfulReschedule(clock, TimeUnit.HOURS.toMillis(23), TimeUnit.HOURS.toMillis(6));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyTooLargeValue() {
        long start = TimeUnit.HOURS.toMillis(24);
        long end = 1L;
        DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), start, end);
    }

    @Test
    public void verifyScheduledAtMidnight() {
        long start = 0;
        long end = 1L;
        DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), start, end);
        assertThat(manager().getAllJobRequests()).hasSize(1);
    }

    @Test
    public void verifyHasExtras() {
        long start = 0;
        long end = 1L;
        int jobId = DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), start, end);
        JobRequest request = manager().getJobRequest(jobId);
        assertThat(request).isNotNull();
        assertThat(request.getExtras().getLong(EXTRA_START_MS, (-1))).isEqualTo(0L);
        assertThat(request.getExtras().getLong(EXTRA_END_MS, (-1))).isEqualTo(1L);
        assertThat(request.getExtras().size()).isEqualTo(2);
    }

    @Test
    public void verifyExtraValuesAreOverwritten() {
        long start = 0;
        long end = 1L;
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("something", 9L);// make sure this value is not overwritten

        extras.putLong(EXTRA_START_MS, 9L);// make sure they're overwritten

        extras.putLong(EXTRA_END_MS, 9L);
        int jobId = DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setExtras(extras), start, end);
        JobRequest request = manager().getJobRequest(jobId);
        assertThat(request).isNotNull();
        assertThat(request.getExtras().getLong(EXTRA_START_MS, (-1))).isEqualTo(0L);
        assertThat(request.getExtras().getLong(EXTRA_END_MS, (-1))).isEqualTo(1L);
        assertThat(request.getExtras().size()).isEqualTo(3);
    }

    @Test
    public void verifyDailyJobIsNotExact() {
        long time = 1L;
        int jobId = DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), time, time);
        JobRequest request = manager().getJobRequest(jobId);
        assertThat(request).isNotNull();
        assertThat(request.isExact()).isFalse();
    }

    @Test
    public void verifyScheduledTwiceOverridesExisting() {
        long time = 1L;
        DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), time, time);
        DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), time, time);
        Set<JobRequest> requests = manager().getAllJobRequests();
        assertThat(requests).hasSize(1);
        assertThat(requests.iterator().next().getTag()).isEqualTo(TAG);
    }

    @Test
    public void verifyScheduledImmediatelyIsNotOverridden() {
        long time = 1L;
        DailyJob.startNowOnce(DummyJobs.createBuilder(DummyJobs.SuccessJob.class));
        DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), time, time);
        Set<JobRequest> requests = manager().getAllJobRequests();
        assertThat(requests).hasSize(2);
        for (JobRequest request : requests) {
            assertThat(request.getTag()).isEqualTo(TAG);
        }
    }

    @Test
    public void verifyImmediateExecution() {
        long time = 1L;
        int nowJobId = DailyJob.startNowOnce(DummyJobs.createBuilder(DummyJobs.SuccessJob.class));
        int normalJobId = DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessJob.class), time, time);
        assertThat(manager().getAllJobRequests()).hasSize(2);
        executeJob(nowJobId, SUCCESS);
        assertThat(manager().getAllJobRequests()).hasSize(1);
        assertThat(manager().getJobRequest(normalJobId)).isNotNull();
    }

    @Test
    public void verifyLastRunIsSet() {
        TestClock clock = new TestClock();
        clock.setTime(13, 0);
        JobRequest request = verifyExecutionAndSuccessfulReschedule(clock, 0, 1);
        assertThat(request.getLastRun()).isEqualTo(clock.currentTimeMillis());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void verifyEarlyExecution() {
        TestClock clock = new TestClock();
        clock.setTime(13, 0);
        JobRequest request = verifyExecutionAndSuccessfulReschedule(clock, TimeUnit.HOURS.toMillis(14), TimeUnit.HOURS.toMillis(15));
        assertThat(request.getStartMs()).isEqualTo(TimeUnit.HOURS.toMillis(25));
        assertThat(request.getEndMs()).isEqualTo(TimeUnit.HOURS.toMillis(26));
        int id = DailyJob.schedule(DummyJobs.createBuilder(DummyJobs.SuccessDailyJob.class), TimeUnit.HOURS.toMillis(14), TimeUnit.HOURS.toMillis(15));
        request = manager().getJobRequest(id);
        assertThat(request.getStartMs()).isEqualTo(TimeUnit.HOURS.toMillis(1));
        assertThat(request.getEndMs()).isEqualTo(TimeUnit.HOURS.toMillis(2));
    }

    @Test
    public void verifyRequirementsEnforcedSkipsJob() {
        long time = 1L;
        final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        manager().addJobCreator(new JobCreator() {
            @Override
            public Job create(@NonNull
            String tag) {
                return new DailyJob() {
                    @NonNull
                    @Override
                    protected DailyJobResult onRunDailyJob(@NonNull
                    Params params) {
                        atomicBoolean.set(false);
                        return SUCCESS;
                    }
                };
            }
        });
        int jobId = DailyJob.schedule(new JobRequest.Builder("any").setRequiresCharging(true).setRequirementsEnforced(true), time, time);
        assertThat(manager().getAllJobRequests()).hasSize(1);
        executeJob(jobId, SUCCESS);
        assertThat(manager().getAllJobRequests()).hasSize(1);
        assertThat(atomicBoolean.get()).isTrue();
        // now verify that the job is called without the requirement
        manager().cancelAll();
        jobId = DailyJob.schedule(new JobRequest.Builder("any").setRequiresCharging(false).setRequirementsEnforced(true), time, time);
        assertThat(manager().getAllJobRequests()).hasSize(1);
        executeJob(jobId, SUCCESS);
        assertThat(manager().getAllJobRequests()).hasSize(1);
        assertThat(atomicBoolean.get()).isFalse();
    }
}

