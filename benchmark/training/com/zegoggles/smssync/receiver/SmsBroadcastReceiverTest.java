package com.zegoggles.smssync.receiver;


import android.content.Context;
import android.content.Intent;
import com.zegoggles.smssync.preferences.AuthPreferences;
import com.zegoggles.smssync.preferences.Preferences;
import com.zegoggles.smssync.service.BackupJobs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SmsBroadcastReceiverTest {
    Context context;

    @Mock
    BackupJobs backupJobs;

    @Mock
    Preferences preferences;

    @Mock
    AuthPreferences authPreferences;

    SmsBroadcastReceiver receiver;

    @Test
    public void shouldScheduleIncomingBackupAfterIncomingMessage() throws Exception {
        mockScheduled();
        receiver.onReceive(context, new Intent().setAction("android.provider.Telephony.SMS_RECEIVED"));
        Mockito.verify(backupJobs, Mockito.times(1)).scheduleIncoming();
    }

    @Test
    public void shouldNotScheduleIfAutoBackupIsDisabled() throws Exception {
        mockScheduled();
        Mockito.when(preferences.isAutoBackupEnabled()).thenReturn(false);
        receiver.onReceive(context, new Intent().setAction("android.provider.Telephony.SMS_RECEIVED"));
        Mockito.verifyZeroInteractions(backupJobs);
    }

    @Test
    public void shouldNotScheduleIfLoginInformationIsNotSet() throws Exception {
        mockScheduled();
        Mockito.when(authPreferences.isLoginInformationSet()).thenReturn(false);
        receiver.onReceive(context, new Intent().setAction("android.provider.Telephony.SMS_RECEIVED"));
        Mockito.verifyZeroInteractions(backupJobs);
    }

    @Test
    public void shouldNotScheduleIfFirstBackupHasNotBeenRun() throws Exception {
        mockScheduled();
        Mockito.when(preferences.isFirstBackup()).thenReturn(true);
        receiver.onReceive(context, new Intent().setAction("android.provider.Telephony.SMS_RECEIVED"));
        Mockito.verifyZeroInteractions(backupJobs);
    }
}

