package com.evernote.android.job;


import Job.Result.FAILURE;
import Job.Result.RESCHEDULE;
import Job.Result.SUCCESS;
import com.evernote.android.job.test.DummyJobs;
import com.evernote.android.job.test.JobRobolectricTestRunner;
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
public class LastRunTest extends BaseJobManagerTest {
    @Test
    public void updateLastRunPeriodicSuccess() throws Exception {
        testPeriodicJob(DummyJobs.SuccessJob.class, SUCCESS);
    }

    @Test
    public void updateLastRunPeriodicReschedule() throws Exception {
        testPeriodicJob(DummyJobs.RescheduleJob.class, RESCHEDULE);
    }

    @Test
    public void updateLastRunPeriodicFailure() throws Exception {
        testPeriodicJob(DummyJobs.FailureJob.class, FAILURE);
    }

    @Test
    public void updateLastRunReschedule() throws Exception {
        int jobId = DummyJobs.createBuilder(DummyJobs.RescheduleJob.class).setExecutionWindow(1000, 2000).build().schedule();
        assertThat(manager().getJobRequest(jobId).getLastRun()).isEqualTo(0);
        executeJob(jobId, RESCHEDULE);
        DummyJobs.RescheduleJob job = ((DummyJobs.RescheduleJob) (manager().getJob(jobId)));
        jobId = job.getNewJobId();
        long lastRun = manager().getJobRequest(jobId).getLastRun();
        assertThat(lastRun).isGreaterThan(0);
        Thread.sleep(2L);
        executeJob(jobId, RESCHEDULE);
        job = ((DummyJobs.RescheduleJob) (manager().getJob(jobId)));
        jobId = job.getNewJobId();
        assertThat(manager().getJobRequest(jobId).getLastRun()).isGreaterThan(lastRun);
    }

    @Test
    public void verifyTimeNotUpdatedSuccess() throws Exception {
        int jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setExecutionWindow(1000, 2000).build().schedule();
        JobRequest request = manager().getJobRequest(jobId);
        assertThat(request.getLastRun()).isEqualTo(0);
        executeJob(jobId, SUCCESS);
        assertThat(request.getLastRun()).isEqualTo(0);
    }

    @Test
    public void verifyTimeNotUpdatedFailure() throws Exception {
        int jobId = DummyJobs.createBuilder(DummyJobs.FailureJob.class).setExecutionWindow(1000, 2000).build().schedule();
        JobRequest request = manager().getJobRequest(jobId);
        assertThat(request.getLastRun()).isEqualTo(0);
        executeJob(jobId, FAILURE);
        assertThat(request.getLastRun()).isEqualTo(0);
    }
}

