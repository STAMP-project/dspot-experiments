package com.zegoggles.smssync.service;


import BackupImapStore.BackupFolder;
import Consts.SMS_PROVIDER;
import DataType.SMS;
import Telephony.TextBasedSmsColumns.DATE;
import Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX;
import Telephony.TextBasedSmsColumns.TYPE;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import com.fsck.k9.mail.store.imap.ImapMessage;
import com.zegoggles.smssync.auth.TokenRefresher;
import com.zegoggles.smssync.mail.BackupImapStore;
import com.zegoggles.smssync.mail.MessageConverter;
import com.zegoggles.smssync.service.state.RestoreState;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class RestoreTaskTest {
    RestoreTask task;

    RestoreConfig config;

    @Mock
    BackupImapStore store;

    @Mock
    BackupFolder folder;

    @Mock
    SmsRestoreService service;

    @Mock
    RestoreState state;

    @Mock
    MessageConverter converter;

    @Mock
    ContentResolver resolver;

    @Mock
    TokenRefresher tokenRefresher;

    @Test
    public void shouldAcquireAndReleaseLocksDuringRestore() throws Exception {
        task.doInBackground(config);
        Mockito.verify(service).acquireLocks();
        Mockito.verify(service).releaseLocks();
    }

    @Test
    public void shouldVerifyStoreSettings() throws Exception {
        task.doInBackground(config);
        Mockito.verify(store).checkSettings();
    }

    @Test
    public void shouldCloseFolders() throws Exception {
        task.doInBackground(config);
        Mockito.verify(store).closeFolders();
    }

    @Test
    public void shouldRestoreItems() throws Exception {
        Date now = new Date();
        List<ImapMessage> messages = new ArrayList<ImapMessage>();
        ContentValues values = new ContentValues();
        values.put(TYPE, MESSAGE_TYPE_INBOX);
        values.put(DATE, now.getTime());
        ImapMessage mockMessage = Mockito.mock(ImapMessage.class);
        Mockito.when(mockMessage.getFolder()).thenReturn(folder);
        Mockito.when(converter.getDataType(mockMessage)).thenReturn(SMS);
        Mockito.when(converter.messageToContentValues(mockMessage)).thenReturn(values);
        messages.add(mockMessage);
        Mockito.when(folder.getMessages(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Date.class))).thenReturn(messages);
        Mockito.when(resolver.insert(SMS_PROVIDER, values)).thenReturn(Uri.parse("content://sms/123"));
        task.doInBackground(config);
        Mockito.verify(resolver).insert(SMS_PROVIDER, values);
        Mockito.verify(resolver).delete(Uri.parse("content://sms/conversations/-1"), null, null);
        assertThat(service.getPreferences().getDataTypePreferences().getMaxSyncedDate(SMS)).isEqualTo(now.getTime());
        assertThat(task.getSmsIds()).containsExactly("123");
        Mockito.verify(store).closeFolders();
    }
}

