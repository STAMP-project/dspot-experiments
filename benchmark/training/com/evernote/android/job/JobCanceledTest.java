package com.evernote.android.job;


import Job.Result.SUCCESS;
import android.support.annotation.NonNull;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static Result.SUCCESS;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class JobCanceledTest extends BaseJobManagerTest {
    @Test
    public void verifyOnCancelInvokedOnce() {
        final AtomicInteger onCancelCalled = new AtomicInteger(0);
        final Job job = new Job() {
            @NonNull
            @Override
            protected Result onRunJob(@NonNull
            Params params) {
                cancel();
                cancel();
                cancel();
                return SUCCESS;
            }

            @Override
            protected void onCancel() {
                onCancelCalled.incrementAndGet();
            }
        };
        manager().addJobCreator(new JobCreator() {
            @Override
            public Job create(@NonNull
            String tag) {
                return job;
            }
        });
        final String tag = "something";
        final int jobId = new JobRequest.Builder(tag).setExecutionWindow(200000L, 400000L).build().schedule();
        executeJob(jobId, SUCCESS);
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
        assertThat(manager().getJobRequest(jobId)).isNull();
        assertThat(manager().getJobRequest(jobId, true)).isNull();
        assertThat(job.isCanceled()).isTrue();
        assertThat(onCancelCalled.get()).isEqualTo(1);
    }

    @Test
    public void verifyOnCancelNotInvokedWhenFinished() {
        final AtomicInteger onCancelCalled = new AtomicInteger(0);
        final Job job = new Job() {
            @NonNull
            @Override
            protected Result onRunJob(@NonNull
            Params params) {
                return Result.SUCCESS;
            }

            @Override
            protected void onCancel() {
                onCancelCalled.incrementAndGet();
            }
        };
        manager().addJobCreator(new JobCreator() {
            @Override
            public Job create(@NonNull
            String tag) {
                return job;
            }
        });
        final String tag = "something";
        final int jobId = new JobRequest.Builder(tag).setExecutionWindow(200000L, 400000L).build().schedule();
        executeJob(jobId, SUCCESS);
        job.cancel();
        assertThat(manager().getAllJobRequestsForTag(tag)).isEmpty();
        assertThat(manager().getJobRequest(jobId)).isNull();
        assertThat(manager().getJobRequest(jobId, true)).isNull();
        assertThat(job.isCanceled()).isFalse();
        assertThat(onCancelCalled.get()).isEqualTo(0);
    }
}

