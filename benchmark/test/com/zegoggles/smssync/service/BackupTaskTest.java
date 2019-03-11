package com.zegoggles.smssync.service;


import BackupImapStore.BackupFolder;
import DataType.SMS;
import SmsSyncState.CALC;
import SmsSyncState.ERROR;
import SmsSyncState.FINISHED_BACKUP;
import SmsSyncState.LOGIN;
import android.content.Context;
import android.database.Cursor;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.store.imap.XOAuth2AuthenticationFailedException;
import com.zegoggles.smssync.auth.TokenRefreshException;
import com.zegoggles.smssync.auth.TokenRefresher;
import com.zegoggles.smssync.contacts.ContactAccessor;
import com.zegoggles.smssync.contacts.ContactGroup;
import com.zegoggles.smssync.mail.BackupImapStore;
import com.zegoggles.smssync.mail.DataType;
import com.zegoggles.smssync.mail.MessageConverter;
import com.zegoggles.smssync.preferences.AuthPreferences;
import com.zegoggles.smssync.preferences.DataTypePreferences;
import com.zegoggles.smssync.preferences.Preferences;
import com.zegoggles.smssync.service.state.BackupState;
import java.util.EnumSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static BackupType.SKIP;


@RunWith(RobolectricTestRunner.class)
public class BackupTaskTest {
    BackupTask task;

    BackupConfig config;

    Context context;

    @Mock
    BackupImapStore store;

    @Mock
    BackupFolder folder;

    @Mock
    SmsBackupService service;

    @Mock
    BackupState state;

    @Mock
    BackupItemsFetcher fetcher;

    @Mock
    MessageConverter converter;

    @Mock
    CalendarSyncer syncer;

    @Mock
    AuthPreferences authPreferences;

    @Mock
    DataTypePreferences dataTypePreferences;

    @Mock
    Preferences preferences;

    @Mock
    ContactAccessor accessor;

    @Mock
    TokenRefresher tokenRefresher;

    @Test
    public void shouldAcquireAndReleaseLocksDuringBackup() throws Exception {
        mockAllFetchEmpty();
        task.doInBackground(config);
        Mockito.verify(service).acquireLocks();
        Mockito.verify(service).releaseLocks();
        Mockito.verify(service).transition(FINISHED_BACKUP, null);
    }

    @Test
    public void shouldVerifyStoreSettings() throws Exception {
        mockFetch(DataType.SMS, 1);
        Mockito.when(converter.convertMessages(ArgumentMatchers.any(Cursor.class), ArgumentMatchers.eq(DataType.SMS))).thenReturn(result(DataType.SMS, 1));
        Mockito.when(store.getFolder(DataType.SMS, dataTypePreferences)).thenReturn(folder);
        task.doInBackground(config);
        Mockito.verify(store).checkSettings();
    }

    @Test
    public void shouldBackupItems() throws Exception {
        mockFetch(DataType.SMS, 1);
        Mockito.when(converter.convertMessages(ArgumentMatchers.any(Cursor.class), ArgumentMatchers.eq(DataType.SMS))).thenReturn(result(DataType.SMS, 1));
        Mockito.when(store.getFolder(ArgumentMatchers.notNull(DataType.class), ArgumentMatchers.same(dataTypePreferences))).thenReturn(folder);
        BackupState finalState = task.doInBackground(config);
        Mockito.verify(folder).appendMessages(ArgumentMatchers.anyListOf(Message.class));
        Mockito.verify(service).transition(LOGIN, null);
        Mockito.verify(service).transition(CALC, null);
        assertThat(finalState).isNotNull();
        assertThat(finalState.isFinished()).isTrue();
        assertThat(finalState.currentSyncedItems).isEqualTo(1);
        assertThat(finalState.itemsToSync).isEqualTo(1);
        assertThat(finalState.backupType).isEqualTo(config.backupType);
    }

    @Test
    public void shouldBackupMultipleTypes() throws Exception {
        mockFetch(DataType.SMS, 1);
        mockFetch(DataType.MMS, 2);
        Mockito.when(store.getFolder(ArgumentMatchers.notNull(DataType.class), ArgumentMatchers.same(dataTypePreferences))).thenReturn(folder);
        Mockito.when(converter.convertMessages(ArgumentMatchers.any(Cursor.class), ArgumentMatchers.any(DataType.class))).thenReturn(result(DataType.SMS, 1));
        BackupState finalState = task.doInBackground(getBackupConfig(EnumSet.of(DataType.SMS, DataType.MMS)));
        assertThat(finalState.currentSyncedItems).isEqualTo(3);
        Mockito.verify(folder, Mockito.times(3)).appendMessages(ArgumentMatchers.anyListOf(Message.class));
    }

    @Test
    public void shouldCreateFoldersLazilyOnlyForNeededTypes() throws Exception {
        mockFetch(DataType.SMS, 1);
        Mockito.when(converter.convertMessages(ArgumentMatchers.any(Cursor.class), ArgumentMatchers.eq(DataType.SMS))).thenReturn(result(DataType.SMS, 1));
        Mockito.when(store.getFolder(ArgumentMatchers.notNull(DataType.class), ArgumentMatchers.same(dataTypePreferences))).thenReturn(folder);
        task.doInBackground(config);
        Mockito.verify(store).getFolder(DataType.SMS, dataTypePreferences);
        Mockito.verify(store, Mockito.never()).getFolder(DataType.MMS, dataTypePreferences);
        Mockito.verify(store, Mockito.never()).getFolder(DataType.CALLLOG, dataTypePreferences);
    }

    @Test
    public void shouldCloseImapFolderAfterBackup() throws Exception {
        mockFetch(DataType.SMS, 1);
        Mockito.when(converter.convertMessages(ArgumentMatchers.any(Cursor.class), ArgumentMatchers.eq(DataType.SMS))).thenReturn(result(DataType.SMS, 1));
        Mockito.when(store.getFolder(ArgumentMatchers.notNull(DataType.class), ArgumentMatchers.same(dataTypePreferences))).thenReturn(folder);
        task.doInBackground(config);
        Mockito.verify(store).closeFolders();
    }

    @Test
    public void shouldCreateNoFoldersIfNoItemsToBackup() throws Exception {
        mockFetch(DataType.SMS, 0);
        task.doInBackground(config);
        Mockito.verifyZeroInteractions(store);
    }

    @Test
    public void shouldSkipItems() throws Exception {
        Mockito.when(fetcher.getMostRecentTimestamp(ArgumentMatchers.any(DataType.class))).thenReturn((-23L));
        BackupState finalState = task.doInBackground(new BackupConfig(store, 0, 100, new ContactGroup((-1)), SKIP, EnumSet.of(DataType.SMS), false));
        Mockito.verify(dataTypePreferences).setMaxSyncedDate(SMS, (-23));
        Mockito.verifyZeroInteractions(dataTypePreferences);
        assertThat(finalState).isNotNull();
        assertThat(finalState.isFinished()).isTrue();
    }

    @Test
    public void shouldHandleAuthErrorAndTokenCannotBeRefreshed() throws Exception {
        mockFetch(DataType.SMS, 1);
        Mockito.when(converter.convertMessages(ArgumentMatchers.any(Cursor.class), ArgumentMatchers.notNull(DataType.class))).thenReturn(result(DataType.SMS, 1));
        XOAuth2AuthenticationFailedException exception = Mockito.mock(XOAuth2AuthenticationFailedException.class);
        Mockito.when(exception.getStatus()).thenReturn(400);
        Mockito.when(store.getFolder(ArgumentMatchers.notNull(DataType.class), ArgumentMatchers.same(dataTypePreferences))).thenThrow(exception);
        Mockito.doThrow(new TokenRefreshException("failed")).when(tokenRefresher).refreshOAuth2Token();
        task.doInBackground(config);
        Mockito.verify(tokenRefresher, Mockito.times(1)).refreshOAuth2Token();
        Mockito.verify(service).transition(ERROR, exception);
        // make sure locks only get acquired+released once
        Mockito.verify(service).acquireLocks();
        Mockito.verify(service).releaseLocks();
    }

    @Test
    public void shouldHandleAuthErrorAndTokenCouldBeRefreshed() throws Exception {
        mockFetch(DataType.SMS, 1);
        Mockito.when(converter.convertMessages(ArgumentMatchers.any(Cursor.class), ArgumentMatchers.notNull(DataType.class))).thenReturn(result(DataType.SMS, 1));
        XOAuth2AuthenticationFailedException exception = Mockito.mock(XOAuth2AuthenticationFailedException.class);
        Mockito.when(exception.getStatus()).thenReturn(400);
        Mockito.when(store.getFolder(ArgumentMatchers.notNull(DataType.class), ArgumentMatchers.same(dataTypePreferences))).thenThrow(exception);
        Mockito.when(service.getBackupImapStore()).thenReturn(store);
        task.doInBackground(config);
        Mockito.verify(tokenRefresher).refreshOAuth2Token();
        Mockito.verify(service, Mockito.times(2)).transition(LOGIN, null);
        Mockito.verify(service, Mockito.times(2)).transition(CALC, null);
        Mockito.verify(service).transition(ERROR, exception);
        // make sure locks only get acquired+released once
        Mockito.verify(service).acquireLocks();
        Mockito.verify(service).releaseLocks();
    }
}

