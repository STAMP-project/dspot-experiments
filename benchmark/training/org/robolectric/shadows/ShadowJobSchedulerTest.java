package org.robolectric.shadows;


import JobScheduler.RESULT_FAILURE;
import JobScheduler.RESULT_SUCCESS;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowJobSchedulerTest {
    private JobScheduler jobScheduler;

    private Application context;

    @Test
    public void getAllPendingJobs() {
        JobInfo jobInfo = new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build();
        jobScheduler.schedule(jobInfo);
        assertThat(jobScheduler.getAllPendingJobs()).contains(jobInfo);
    }

    @Test
    public void cancelAll() {
        jobScheduler.schedule(new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build());
        jobScheduler.schedule(new JobInfo.Builder(33, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build());
        assertThat(jobScheduler.getAllPendingJobs()).hasSize(2);
        jobScheduler.cancelAll();
        assertThat(jobScheduler.getAllPendingJobs()).isEmpty();
    }

    @Test
    public void cancelSingleJob() {
        jobScheduler.schedule(new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build());
        assertThat(jobScheduler.getAllPendingJobs()).isNotEmpty();
        jobScheduler.cancel(99);
        assertThat(jobScheduler.getAllPendingJobs()).isEmpty();
    }

    @Test
    public void cancelNonExistentJob() {
        jobScheduler.schedule(new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build());
        assertThat(jobScheduler.getAllPendingJobs()).isNotEmpty();
        jobScheduler.cancel(33);
        assertThat(jobScheduler.getAllPendingJobs()).isNotEmpty();
    }

    @Test
    public void schedule_success() {
        int result = jobScheduler.schedule(new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build());
        assertThat(result).isEqualTo(RESULT_SUCCESS);
    }

    @Test
    public void schedule_fail() {
        Shadows.shadowOf(jobScheduler).failOnJob(99);
        int result = jobScheduler.schedule(new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build());
        assertThat(result).isEqualTo(RESULT_FAILURE);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void getPendingJob_withValidId() {
        int jobId = 99;
        JobInfo originalJobInfo = new JobInfo.Builder(jobId, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build();
        jobScheduler.schedule(originalJobInfo);
        JobInfo retrievedJobInfo = jobScheduler.getPendingJob(jobId);
        assertThat(retrievedJobInfo).isEqualTo(originalJobInfo);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void getPendingJob_withInvalidId() {
        int jobId = 99;
        int invalidJobId = 100;
        JobInfo originalJobInfo = new JobInfo.Builder(jobId, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build();
        jobScheduler.schedule(originalJobInfo);
        JobInfo retrievedJobInfo = jobScheduler.getPendingJob(invalidJobId);
        assertThat(retrievedJobInfo).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void enqueue_success() {
        int result = jobScheduler.enqueue(new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build(), new android.app.job.JobWorkItem(new Intent()));
        assertThat(result).isEqualTo(RESULT_SUCCESS);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void enqueue_fail() {
        Shadows.shadowOf(jobScheduler).failOnJob(99);
        int result = jobScheduler.enqueue(new JobInfo.Builder(99, new android.content.ComponentName(context, "component_class_name")).setPeriodic(1000).build(), new android.app.job.JobWorkItem(new Intent()));
        assertThat(result).isEqualTo(RESULT_FAILURE);
    }
}

