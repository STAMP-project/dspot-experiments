package com.birbit.android.jobqueue.scheduling;


import Build.VERSION_CODES;
import RuntimeEnvironment.application;
import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobManager;
import com.google.android.gms.gcm.TaskParams;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@TargetApi(VERSION_CODES.LOLLIPOP)
public class GcmJobSchedulerServiceTest {
    JobManager mockJobManager;

    GcmJobSchedulerServiceTest.GcmJobSchedulerServiceImpl service;

    GcmScheduler mockScheduler;

    @Test
    public void createScheduler() {
        GcmScheduler scheduler = GcmJobSchedulerService.createSchedulerFor(application, GcmJobSchedulerServiceTest.GcmJobSchedulerServiceImpl.class);
        MatcherAssert.assertThat(((scheduler.serviceClass) == (GcmJobSchedulerServiceTest.GcmJobSchedulerServiceImpl.class)), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadScheduler() {
        GcmJobSchedulerService.createSchedulerFor(application, GcmJobSchedulerService.class);
    }

    @Test
    public void onStartJob() {
        TaskParams params = Mockito.mock(TaskParams.class);
        service.onRunTask(params);
        Mockito.verify(mockScheduler).onStartJob(params);
    }

    @Test
    public void onStartJobWithoutScheduler() {
        Mockito.when(mockJobManager.getScheduler()).thenReturn(null);
        TaskParams params = Mockito.mock(TaskParams.class);
        service.onRunTask(params);
        // test sanity
        Mockito.verify(mockScheduler, Mockito.never()).onStartJob(params);
    }

    public class GcmJobSchedulerServiceImpl extends GcmJobSchedulerService {
        @NonNull
        @Override
        protected JobManager getJobManager() {
            return mockJobManager;
        }
    }
}

