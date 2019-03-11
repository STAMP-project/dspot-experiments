package com.birbit.android.jobqueue.scheduling;


import Build.VERSION_CODES;
import RuntimeEnvironment.application;
import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.BuildConfig;
import com.birbit.android.jobqueue.JobManager;
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
public class FrameworkJobSchedulerServiceTest {
    JobManager mockJobManager;

    FrameworkJobSchedulerServiceTest.FrameworkJobSchedulerServiceImpl service;

    FrameworkScheduler mockScheduler;

    @Test
    public void createScheduler() {
        FrameworkScheduler scheduler = FrameworkJobSchedulerService.createSchedulerFor(application, FrameworkJobSchedulerServiceTest.FrameworkJobSchedulerServiceImpl.class);
        MatcherAssert.assertThat(((scheduler.serviceImpl) == (FrameworkJobSchedulerServiceTest.FrameworkJobSchedulerServiceImpl.class)), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBadScheduler() {
        FrameworkJobSchedulerService.createSchedulerFor(application, FrameworkJobSchedulerService.class);
    }

    @Test
    public void onCreate() {
        service.onCreate();
        Mockito.verify(mockScheduler).setJobService(service);
    }

    @Test
    public void onCreateWithoutScheduler() {
        Mockito.when(mockJobManager.getScheduler()).thenReturn(null);
        service.onCreate();
        // test sanity
        Mockito.verify(mockScheduler, Mockito.never()).setJobService(service);
    }

    @Test
    public void onDestroy() {
        service.onDestroy();
        Mockito.verify(mockScheduler).setJobService(null);
    }

    @Test
    public void onDestroyWithoutScheduler() {
        Mockito.when(mockJobManager.getScheduler()).thenReturn(null);
        service.onDestroy();
        // test sanity
        Mockito.verify(mockScheduler, Mockito.never()).setJobService(null);
    }

    @Test
    public void onStartJob() {
        JobParameters params = Mockito.mock(JobParameters.class);
        service.onStartJob(params);
        Mockito.verify(mockScheduler).onStartJob(params);
    }

    @Test
    public void onStartJobWithoutScheduler() {
        Mockito.when(mockJobManager.getScheduler()).thenReturn(null);
        JobParameters params = Mockito.mock(JobParameters.class);
        service.onStartJob(params);
        // test sanity
        Mockito.verify(mockScheduler, Mockito.never()).onStartJob(params);
    }

    @Test
    public void onStopJob() {
        JobParameters params = Mockito.mock(JobParameters.class);
        service.onStopJob(params);
        Mockito.verify(mockScheduler).onStopJob(params);
    }

    @Test
    public void onStopJobWithoutScheduler() {
        Mockito.when(mockJobManager.getScheduler()).thenReturn(null);
        JobParameters params = Mockito.mock(JobParameters.class);
        service.onStopJob(params);
        // test sanity
        Mockito.verify(mockScheduler, Mockito.never()).onStopJob(params);
    }

    public class FrameworkJobSchedulerServiceImpl extends FrameworkJobSchedulerService {
        @NonNull
        @Override
        protected JobManager getJobManager() {
            return mockJobManager;
        }
    }
}

