package com.zegoggles.smssync.service;


import BackupJobs.CONTENT_TRIGGER_TAG;
import com.firebase.jobdispatcher.JobParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SmsJobServiceTest {
    private SmsJobService smsJobService;

    @Test
    public void testOnStartJob() {
        final JobParameters jobParameters = Mockito.mock(JobParameters.class);
        Mockito.when(jobParameters.getTag()).thenReturn(CONTENT_TRIGGER_TAG);
        boolean moreWork = smsJobService.onStartJob(jobParameters);
        assertThat(moreWork).isFalse();
    }

    @Test
    public void testOnStopJob() {
        final JobParameters jobParameters = Mockito.mock(JobParameters.class);
        boolean shouldRetry = smsJobService.onStopJob(jobParameters);
        assertThat(shouldRetry).isFalse();
    }
}

