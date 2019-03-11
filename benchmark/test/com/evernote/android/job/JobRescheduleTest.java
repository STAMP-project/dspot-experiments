package com.evernote.android.job;


import Context.JOB_SCHEDULER_SERVICE;
import JobStorage.JOB_TABLE_NAME;
import PendingIntent.FLAG_NO_CREATE;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import com.evernote.android.job.v14.PlatformAlarmReceiver;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class JobRescheduleTest extends BaseJobManagerTest {
    @Test
    @Config(sdk = 21)
    public void verifyOneOffJobRescheduled() throws Exception {
        assertThat(manager().getAllJobRequests()).isEmpty();
        ContentValues contentValues = new JobRequest.Builder("tag").setExecutionWindow(40000, 50000).build().toContentValues();
        manager().getJobStorage().getDatabase().insert(JOB_TABLE_NAME, null, contentValues);
        Set<JobRequest> requests = manager().getAllJobRequests();
        assertThat(requests).isNotEmpty();
        int rescheduledJobs = new JobRescheduleService().rescheduleJobs(manager());
        assertThat(rescheduledJobs).isEqualTo(1);
    }

    @Test
    @Config(sdk = 21)
    public void verifyPeriodicJobRescheduled() throws Exception {
        assertThat(manager().getAllJobRequests()).isEmpty();
        ContentValues contentValues = new JobRequest.Builder("tag").setPeriodic(TimeUnit.HOURS.toMillis(1)).build().toContentValues();
        manager().getJobStorage().getDatabase().insert(JOB_TABLE_NAME, null, contentValues);
        Set<JobRequest> requests = manager().getAllJobRequests();
        assertThat(requests).isNotEmpty();
        JobScheduler scheduler = ((JobScheduler) (context().getSystemService(JOB_SCHEDULER_SERVICE)));
        assertThat(scheduler.getAllPendingJobs()).isEmpty();
        int rescheduledJobs = new JobRescheduleService().rescheduleJobs(manager());
        assertThat(rescheduledJobs).isEqualTo(1);
    }

    @Test
    @Config(sdk = 21)
    public void verifyExactJobRescheduled() throws Exception {
        assertThat(manager().getAllJobRequests()).isEmpty();
        ContentValues contentValues = new JobRequest.Builder("tag").setExact(TimeUnit.HOURS.toMillis(1)).build().toContentValues();
        manager().getJobStorage().getDatabase().insert(JOB_TABLE_NAME, null, contentValues);
        Set<JobRequest> requests = manager().getAllJobRequests();
        assertThat(requests).isNotEmpty();
        final int jobId = 1;
        Intent intent = new Intent(context(), PlatformAlarmReceiver.class);
        assertThat(PendingIntent.getBroadcast(context(), jobId, intent, FLAG_NO_CREATE)).isNull();
        int rescheduledJobs = new JobRescheduleService().rescheduleJobs(manager());
        assertThat(rescheduledJobs).isEqualTo(1);
        assertThat(PendingIntent.getBroadcast(context(), jobId, intent, FLAG_NO_CREATE)).isNotNull();
    }

    @Test
    public void verifyTransientJobNotRescheduled() throws Exception {
        assertThat(manager().getAllJobRequests()).isEmpty();
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        ContentValues contentValues = new JobRequest.Builder("tag").setExact(TimeUnit.HOURS.toMillis(1)).setTransientExtras(bundle).build().toContentValues();
        manager().getJobStorage().getDatabase().insert(JOB_TABLE_NAME, null, contentValues);
        Set<JobRequest> requests = manager().getAllJobRequests();
        assertThat(requests).isEmpty();
    }
}

