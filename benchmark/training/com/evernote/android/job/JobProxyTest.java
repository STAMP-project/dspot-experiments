package com.evernote.android.job;


import Context.ALARM_SERVICE;
import Context.JOB_SCHEDULER_SERVICE;
import JobRequest.NetworkType.CONNECTED;
import android.app.AlarmManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class JobProxyTest {
    @Test
    @Config(sdk = 21)
    public void verifyRecoverWithJobScheduler() {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        JobScheduler scheduler = getJobScheduler(applicationContext);
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(null, null, scheduler);
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        List<JobInfo> allPendingJobs = scheduler.getAllPendingJobs();
        assertThat(allPendingJobs).hasSize(1);
        JobInfo jobInfo = allPendingJobs.get(0);
        assertThat(jobInfo.getMinLatencyMillis()).isEqualTo(200000L);
        assertThat(jobInfo.getMaxExecutionDelayMillis()).isEqualTo(300000L);
    }

    @Test
    @Config(sdk = 21)
    public void verifyMaxExecutionDelayIsNotSetInJobScheduler() {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        JobScheduler scheduler = getJobScheduler(applicationContext);
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(scheduler);
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(3000L, 4000L).setRequiredNetworkType(CONNECTED).setRequirementsEnforced(true).build().schedule();
        List<JobInfo> allPendingJobs = scheduler.getAllPendingJobs();
        assertThat(allPendingJobs).hasSize(1);
        JobInfo jobInfo = allPendingJobs.get(0);
        assertThat(jobInfo.getMinLatencyMillis()).isEqualTo(3000L);
        assertThat(jobInfo.getMaxExecutionDelayMillis()).isGreaterThan(4000L);
    }

    @Test
    @Config(sdk = 21)
    public void verifyRecoverWithAlarmManager() throws Exception {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        AlarmManager alarmManager = getAlarmManager(applicationContext);
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(null);
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        verifyAlarmCount(alarmManager, 1);
    }

    @Test
    @Config(sdk = 21)
    public void verifyNoRecoverWithAlarmManager() throws Exception {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        AlarmManager alarmManager = getAlarmManager(applicationContext);
        Mockito.when(applicationContext.getSystemService(ALARM_SERVICE)).thenReturn(null);
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(null);
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        verifyAlarmCount(alarmManager, 0);
    }

    @Test
    @Config(sdk = 19)
    public void verifyRecoverWithAlarmManagerApi19() throws Exception {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        AlarmManager alarmManager = getAlarmManager(applicationContext);
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(null, null, alarmManager);
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        verifyAlarmCount(alarmManager, 1);
    }

    @Test
    @Config(sdk = 19)
    public void verifyNoRecoverWithAlarmManagerApi19() throws Exception {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        AlarmManager alarmManager = getAlarmManager(applicationContext);
        Mockito.when(applicationContext.getSystemService(ALARM_SERVICE)).thenReturn(null);
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        verifyAlarmCount(alarmManager, 0);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    @Config(sdk = 21)
    public void verifyRecoverWithoutBootPermissionJobScheduler() {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        JobScheduler scheduler = Mockito.spy(getJobScheduler(applicationContext));
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(scheduler);
        Mockito.doThrow(new IllegalArgumentException("Error: requested job be persisted without holding RECEIVE_BOOT_COMPLETED permission.")).when(scheduler).schedule(ArgumentMatchers.argThat(new ArgumentMatcher<JobInfo>() {
            @Override
            public boolean matches(JobInfo argument) {
                return argument.isPersisted();
            }
        }));
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        assertThat(scheduler.getAllPendingJobs()).hasSize(1);
        assertThat(scheduler.getAllPendingJobs().get(0).isPersisted()).isFalse();
    }

    @Test
    @Config(sdk = 21)
    public void verifyRecoverWithoutServiceJobScheduler() throws Exception {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        AlarmManager alarmManager = getAlarmManager(applicationContext);
        JobScheduler scheduler = Mockito.spy(getJobScheduler(applicationContext));
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(scheduler);
        Mockito.doThrow(new IllegalArgumentException("No such service ComponentInfo{com.evernote/com.evernote.android.job.v21.PlatformJobService}")).when(scheduler).schedule(ArgumentMatchers.any(JobInfo.class));
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        assertThat(scheduler.getAllPendingJobs()).isEmpty();
        verifyAlarmCount(alarmManager, 1);
    }

    @Test
    @Config(sdk = 21)
    public void verifyRecoverWithNpeInJobScheduler() throws Exception {
        Context context = BaseJobManagerTest.createMockContext();
        Context applicationContext = context.getApplicationContext();
        AlarmManager alarmManager = getAlarmManager(applicationContext);
        JobScheduler scheduler = Mockito.spy(getJobScheduler(applicationContext));
        Mockito.when(applicationContext.getSystemService(JOB_SCHEDULER_SERVICE)).thenReturn(scheduler);
        Mockito.doThrow(new NullPointerException("Attempt to invoke interface method 'int android.app.job.IJobScheduler.schedule(android.app.job.JobInfo)' on a null object reference")).when(scheduler).schedule(ArgumentMatchers.any(JobInfo.class));
        JobManager.create(context);
        new JobRequest.Builder("tag").setExecutionWindow(200000, 300000).build().schedule();
        assertThat(scheduler.getAllPendingJobs()).isEmpty();
        verifyAlarmCount(alarmManager, 1);
    }
}

