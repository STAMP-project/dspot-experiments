package com.zegoggles.smssync.service;


import JobTrigger.ContentUriTrigger;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobTrigger;
import com.zegoggles.smssync.Consts;
import com.zegoggles.smssync.mail.DataType;
import com.zegoggles.smssync.preferences.DataTypePreferences;
import com.zegoggles.smssync.preferences.Preferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BackupJobsTest {
    private BackupJobs subject;

    @Mock
    private Preferences preferences;

    @Mock
    private DataTypePreferences dataTypePreferences;

    @Test
    public void shouldScheduleImmediate() throws Exception {
        Job job = subject.scheduleImmediate();
        verifyJobScheduled(job, (-1), "BROADCAST_INTENT");
    }

    @Test
    public void shouldScheduleRegular() throws Exception {
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(true);
        Mockito.when(preferences.getRegularTimeoutSecs()).thenReturn(2000);
        Job job = subject.scheduleRegular();
        verifyJobScheduled(job, 2000, "REGULAR");
    }

    @Test
    public void shouldScheduleContentUriTriggerForSMS() throws Exception {
        Job job = subject.scheduleContentTriggerJob();
        assertThat(job.getTrigger()).isInstanceOf(ContentUriTrigger.class);
        JobTrigger.ContentUriTrigger contentUriTrigger = ((JobTrigger.ContentUriTrigger) (job.getTrigger()));
        assertThat(contentUriTrigger.getUris()).containsExactly(new com.firebase.jobdispatcher.ObservedUri(Consts.SMS_PROVIDER, FLAG_NOTIFY_FOR_DESCENDANTS));
    }

    @Test
    public void shouldScheduleContentUriTriggerForCallLogIfEnabled() throws Exception {
        Mockito.when(preferences.isCallLogBackupAfterCallEnabled()).thenReturn(true);
        Mockito.when(dataTypePreferences.isBackupEnabled(DataType.CALLLOG)).thenReturn(true);
        Job job = subject.scheduleContentTriggerJob();
        assertThat(job.getTrigger()).isInstanceOf(ContentUriTrigger.class);
        JobTrigger.ContentUriTrigger contentUriTrigger = ((JobTrigger.ContentUriTrigger) (job.getTrigger()));
        assertThat(contentUriTrigger.getUris()).containsExactly(new com.firebase.jobdispatcher.ObservedUri(Consts.SMS_PROVIDER, FLAG_NOTIFY_FOR_DESCENDANTS), new com.firebase.jobdispatcher.ObservedUri(Consts.CALLLOG_PROVIDER, FLAG_NOTIFY_FOR_DESCENDANTS));
    }

    @Test
    public void shouldScheduleRegularJobAfterBootForOldScheduler() throws Exception {
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(true);
        Mockito.when(preferences.isUseOldScheduler()).thenReturn(true);
        Job job = subject.scheduleBootup();
        verifyJobScheduled(job, 60, "REGULAR");
    }

    @Test
    public void shouldScheduleNothingAfterBootForNewScheduler() throws Exception {
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(true);
        Mockito.when(preferences.isUseOldScheduler()).thenReturn(false);
        Job job = subject.scheduleBootup();
        assertThat(job).isNull();
    }

    @Test
    public void shouldCancelAllJobsAfterBootIfAutoBackupDisabled() throws Exception {
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(false);
        Job job = subject.scheduleBootup();
        assertThat(job).isNull();
    }

    @Test
    public void shouldScheduleIncoming() throws Exception {
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(true);
        Mockito.when(preferences.getIncomingTimeoutSecs()).thenReturn(2000);
        Job job = subject.scheduleIncoming();
        verifyJobScheduled(job, 2000, "INCOMING");
    }

    @Test
    public void shouldNotScheduleRegularBackupIfAutoBackupIsDisabled() throws Exception {
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(false);
        assertThat(subject.scheduleRegular()).isEqualTo(null);
    }

    @Test
    public void shouldNotScheduleIncomingBackupIfAutoBackupIsDisabled() throws Exception {
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(false);
        assertThat(subject.scheduleIncoming()).isEqualTo(null);
    }
}

