package com.evernote.android.job;


import Job.Result;
import Job.Result.FAILURE;
import Job.Result.RESCHEDULE;
import Job.Result.SUCCESS;
import com.evernote.android.job.test.DummyJobs;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class FailureCountTest extends BaseJobManagerTest {
    @Test
    public void incrementPeriodicJobFailureCount() {
        int jobId = DummyJobs.createBuilder(DummyJobs.FailureJob.class).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build().schedule();
        executeJob(jobId, FAILURE);
        assertThat(manager().getJobRequest(jobId).getFailureCount()).isEqualTo(1);
        resetJob(jobId);
        executeJob(jobId, FAILURE);
        assertThat(manager().getJobRequest(jobId).getFailureCount()).isEqualTo(2);
    }

    @Test
    public void incrementRescheduleJobFailureCount() {
        int jobId = DummyJobs.createBuilder(DummyJobs.RescheduleJob.class).setExecutionWindow(1000, 2000).build().schedule();
        executeJob(jobId, RESCHEDULE);
        DummyJobs.RescheduleJob job = ((DummyJobs.RescheduleJob) (manager().getJob(jobId)));
        jobId = job.getNewJobId();
        assertThat(manager().getJobRequest(jobId).getFailureCount()).isEqualTo(1);
        executeJob(jobId, RESCHEDULE);
        job = ((DummyJobs.RescheduleJob) (manager().getJob(jobId)));
        jobId = job.getNewJobId();
        assertThat(manager().getJobRequest(jobId).getFailureCount()).isEqualTo(2);
    }

    @Test
    public void verifyDeletedJobIsNotPersistedAgain() throws Exception {
        int jobId = DummyJobs.createBuilder(DummyJobs.TwoSecondPauseJob.class).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build().schedule();
        Future<Job.Result> future = executeJobAsync(jobId, SUCCESS);
        // wait until the job is started
        Thread.sleep(1000);
        assertThat(manager().getJob(jobId)).isNotNull();
        // will also cancel the running job
        manager().cancel(jobId);
        assertThat(future.get(3, TimeUnit.SECONDS)).isEqualTo(SUCCESS);
        assertThat(manager().getJobRequest(jobId)).isNull();
    }
}

