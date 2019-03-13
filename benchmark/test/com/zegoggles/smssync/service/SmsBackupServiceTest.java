package com.zegoggles.smssync.service;


import SmsSyncState.FINISHED_BACKUP;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.fsck.k9.mail.MessagingException;
import com.zegoggles.smssync.mail.DataType;
import com.zegoggles.smssync.preferences.AuthPreferences;
import com.zegoggles.smssync.preferences.DataTypePreferences;
import com.zegoggles.smssync.preferences.Preferences;
import com.zegoggles.smssync.service.exception.BackupDisabledException;
import com.zegoggles.smssync.service.exception.NoConnectionException;
import com.zegoggles.smssync.service.exception.RequiresLoginException;
import com.zegoggles.smssync.service.exception.RequiresWifiException;
import java.util.EnumSet;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowWifiManager;


@RunWith(RobolectricTestRunner.class)
public class SmsBackupServiceTest {
    SmsBackupService service;

    ShadowConnectivityManager shadowConnectivityManager;

    ShadowWifiManager shadowWifiManager;

    List<NotificationCompat.Builder> sentNotifications;

    @Mock
    AuthPreferences authPreferences;

    @Mock
    Preferences preferences;

    @Mock
    DataTypePreferences dataTypePreferences;

    @Mock
    BackupTask backupTask;

    @Mock
    BackupJobs backupJobs;

    @Test
    public void shouldTriggerBackupWithManualIntent() throws Exception {
        Intent intent = new Intent(BackupType.MANUAL.name());
        service.handleIntent(intent);
        Mockito.verify(backupTask).execute(ArgumentMatchers.any(BackupConfig.class));
    }

    @Test
    public void shouldCheckForConnectivityBeforeBackingUp() throws Exception {
        Intent intent = new Intent(BackupType.MANUAL.name());
        shadowConnectivityManager.setActiveNetworkInfo(null);
        service.handleIntent(intent);
        Mockito.verifyZeroInteractions(backupTask);
        assertThat(service.getState().exception).isInstanceOf(NoConnectionException.class);
    }

    @Test
    public void shouldNotCheckForConnectivityBeforeBackingUpWithNewScheduler() throws Exception {
        Mockito.when(preferences.isUseOldScheduler()).thenReturn(false);
        Intent intent = new Intent(BackupType.REGULAR.name());
        shadowConnectivityManager.setActiveNetworkInfo(null);
        shadowConnectivityManager.setBackgroundDataSetting(true);
        service.handleIntent(intent);
        Mockito.verify(backupTask).execute(ArgumentMatchers.any(BackupConfig.class));
    }

    @Test
    public void shouldCheckForWifiConnectivity() throws Exception {
        Intent intent = new Intent();
        Mockito.when(preferences.isWifiOnly()).thenReturn(true);
        shadowConnectivityManager.setBackgroundDataSetting(true);
        shadowWifiManager.setWifiEnabled(false);
        service.handleIntent(intent);
        Mockito.verifyZeroInteractions(backupTask);
        assertThat(service.getState().exception).isInstanceOf(RequiresWifiException.class);
    }

    @Test
    public void shouldCheckForWifiConnectivityAndNetworkType() throws Exception {
        Intent intent = new Intent();
        Mockito.when(preferences.isWifiOnly()).thenReturn(true);
        shadowConnectivityManager.setBackgroundDataSetting(true);
        shadowConnectivityManager.setActiveNetworkInfo(connectedViaEdge());
        shadowWifiManager.setWifiEnabled(true);
        service.handleIntent(intent);
        Mockito.verifyZeroInteractions(backupTask);
        assertThat(service.getState().exception).isInstanceOf(RequiresWifiException.class);
    }

    @Test
    public void shouldCheckForLoginCredentials() throws Exception {
        Intent intent = new Intent();
        Mockito.when(authPreferences.isLoginInformationSet()).thenReturn(false);
        shadowConnectivityManager.setBackgroundDataSetting(true);
        service.handleIntent(intent);
        Mockito.verifyZeroInteractions(backupTask);
        assertThat(service.getState().exception).isInstanceOf(RequiresLoginException.class);
    }

    @Test
    public void shouldCheckForEnabledDataTypes() throws Exception {
        Mockito.when(dataTypePreferences.enabled()).thenReturn(EnumSet.noneOf(DataType.class));
        Intent intent = new Intent();
        Mockito.when(authPreferences.isLoginInformationSet()).thenReturn(true);
        shadowConnectivityManager.setBackgroundDataSetting(true);
        service.handleIntent(intent);
        Mockito.verifyZeroInteractions(backupTask);
        assertThat(service.getState().exception).isInstanceOf(BackupDisabledException.class);
        assertThat(service.getState().state).isEqualTo(FINISHED_BACKUP);
    }

    @Test
    public void shouldPassInCorrectBackupConfig() throws Exception {
        Intent intent = new Intent(BackupType.MANUAL.name());
        ArgumentCaptor<BackupConfig> config = ArgumentCaptor.forClass(BackupConfig.class);
        service.handleIntent(intent);
        Mockito.verify(backupTask).execute(config.capture());
        BackupConfig backupConfig = config.getValue();
        assertThat(backupConfig.backupType).isEqualTo(BackupType.MANUAL);
        assertThat(backupConfig.currentTry).isEqualTo(0);
    }

    @Test
    public void shouldScheduleNextRegularBackupAfterFinished() throws Exception {
        shadowConnectivityManager.setBackgroundDataSetting(true);
        Intent intent = new Intent(BackupType.REGULAR.name());
        service.handleIntent(intent);
        Mockito.verify(backupTask).execute(ArgumentMatchers.any(BackupConfig.class));
        service.backupStateChanged(service.transition(FINISHED_BACKUP, null));
        Mockito.verify(backupJobs).scheduleRegular();
        assertThat(shadowOf(service).isStoppedBySelf()).isTrue();
        assertThat(shadowOf(service).isForegroundStopped()).isTrue();
    }

    @Test
    public void shouldCheckForValidStore() throws Exception {
        Mockito.when(authPreferences.getStoreUri()).thenReturn("invalid");
        Intent intent = new Intent(BackupType.MANUAL.name());
        service.handleIntent(intent);
        Mockito.verifyZeroInteractions(backupTask);
        assertThat(service.getState().exception).isInstanceOf(MessagingException.class);
    }

    @Test
    public void shouldNotifyUserAboutErrorInManualMode() throws Exception {
        Mockito.when(authPreferences.getStoreUri()).thenReturn("invalid");
        Intent intent = new Intent(BackupType.MANUAL.name());
        service.handleIntent(intent);
        Mockito.verifyZeroInteractions(backupTask);
        assertNotificationShown("SMSBackup+ error", "No valid IMAP URI: invalid");
        assertThat(shadowOf(service).isStoppedBySelf()).isTrue();
        assertThat(shadowOf(service).isForegroundStopped()).isTrue();
    }
}

