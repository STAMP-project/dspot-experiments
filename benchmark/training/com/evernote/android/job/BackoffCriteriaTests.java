package com.evernote.android.job;


import Context.ALARM_SERVICE;
import Context.JOB_SCHEDULER_SERVICE;
import Job.Result.RESCHEDULE;
import JobRequest.BackoffPolicy.EXPONENTIAL;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import com.evernote.android.job.test.DummyJobs;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import com.evernote.android.job.test.TestClock;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class BackoffCriteriaTests extends BaseJobManagerTest {
    @Test
    public void verifyBackoffCriteriaIsAppliedForImmediatelyStartedJobs() {
        JobConfig.setClock(new TestClock());
        AlarmManager alarmManager = Mockito.mock(AlarmManager.class);
        JobScheduler jobScheduler = Mockito.mock(JobScheduler.class);
        Mockito.doReturn(alarmManager).when(context()).getSystemService(ArgumentMatchers.eq(ALARM_SERVICE));
        Mockito.doReturn(jobScheduler).when(context()).getSystemService(ArgumentMatchers.eq(JOB_SCHEDULER_SERVICE));
        int jobId = DummyJobs.createBuilder(DummyJobs.RescheduleJob.class).setBackoffCriteria(5000L, EXPONENTIAL).startNow().build().schedule();
        // this uses the JobIntentService under the hood, so verify that the JobScheduler was used for Android O and above
        Mockito.verify(jobScheduler).enqueue(ArgumentMatchers.any(JobInfo.class), ArgumentMatchers.any(JobWorkItem.class));
        executeJob(jobId, RESCHEDULE);
        jobId = manager().getAllJobRequests().iterator().next().getJobId();// because the job was rescheduled and its ID changed

        // make sure that this method was not called again, because with the backoff criteria we have a delay
        Mockito.verify(jobScheduler, Mockito.times(1)).enqueue(ArgumentMatchers.any(JobInfo.class), ArgumentMatchers.any(JobWorkItem.class));
        // instead the AlarmManager should be used
        Mockito.verify(alarmManager).setExactAndAllowWhileIdle(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(5000L), ArgumentMatchers.any(PendingIntent.class));
        executeJob(jobId, RESCHEDULE);
        Mockito.verify(jobScheduler, Mockito.times(1)).enqueue(ArgumentMatchers.any(JobInfo.class), ArgumentMatchers.any(JobWorkItem.class));
        Mockito.verify(alarmManager).setExactAndAllowWhileIdle(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(10000L), ArgumentMatchers.any(PendingIntent.class));
    }
}

