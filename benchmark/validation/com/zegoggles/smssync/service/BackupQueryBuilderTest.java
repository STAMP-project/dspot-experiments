package com.zegoggles.smssync.service;


import BackupQueryBuilder.Query;
import android.net.Uri;
import com.zegoggles.smssync.contacts.ContactGroupIds;
import com.zegoggles.smssync.mail.DataType;
import com.zegoggles.smssync.preferences.DataTypePreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BackupQueryBuilderTest {
    BackupQueryBuilder builder;

    @Mock
    DataTypePreferences dataTypePreferences;

    @Test
    public void shouldBuildQueryForSMS() throws Exception {
        BackupQueryBuilder.Query query = builder.buildQueryForDataType(DataType.SMS, null, 200);
        assertThat(query.uri).isEqualTo(Uri.parse("content://sms"));
        assertThat(query.projection).isNull();
        assertThat(query.selection).isEqualTo("date > ? AND type <> ?");
        assertThat(query.selectionArgs).asList().containsExactly("-1", "3");
        assertThat(query.sortOrder).isEqualTo("date LIMIT 200");
    }

    @Test
    public void shouldBuildQueryForSMSIncludingContactGroup() throws Exception {
        ContactGroupIds ids = new ContactGroupIds();
        ids.add(1L, 20L);
        BackupQueryBuilder.Query query = builder.buildQueryForDataType(DataType.SMS, ids, 200);
        assertThat(query.uri).isEqualTo(Uri.parse("content://sms"));
        assertThat(query.projection).isNull();
        assertThat(query.selection).isEqualTo("date > ? AND type <> ?  AND (type = 2 OR person IN (20))");
        assertThat(query.selectionArgs).asList().containsExactly("-1", "3");
        assertThat(query.sortOrder).isEqualTo("date LIMIT 200");
    }

    @Test
    public void shouldBuildQueryForMMS() throws Exception {
        BackupQueryBuilder.Query query = builder.buildQueryForDataType(DataType.MMS, null, 200);
        assertThat(query.uri).isEqualTo(Uri.parse("content://mms"));
        assertThat(query.projection).isNull();
        assertThat(query.selection).isEqualTo("date > ? AND m_type <> ?");
        assertThat(query.selectionArgs).asList().containsExactly("-1", "134");
        assertThat(query.sortOrder).isEqualTo("date LIMIT 200");
    }

    @Test
    public void shouldBuildQueryForMMSWithSyncedDate() throws Exception {
        long nowInSecs = System.currentTimeMillis();
        Mockito.when(dataTypePreferences.getMaxSyncedDate(DataType.MMS)).thenReturn(nowInSecs);
        BackupQueryBuilder.Query query = builder.buildQueryForDataType(DataType.MMS, null, 200);
        assertThat(query.uri).isEqualTo(Uri.parse("content://mms"));
        assertThat(query.projection).isNull();
        assertThat(query.selection).isEqualTo("date > ? AND m_type <> ?");
        assertThat(query.selectionArgs).asList().containsExactly(String.valueOf((nowInSecs / 1000L)), "134");
        assertThat(query.sortOrder).isEqualTo("date LIMIT 200");
    }

    @Test
    public void shouldBuildQueryForCallLog() throws Exception {
        BackupQueryBuilder.Query query = builder.buildQueryForDataType(DataType.CALLLOG, null, 200);
        assertThat(query.uri).isEqualTo(Uri.parse("content://call_log/calls"));
        assertThat(query.projection).asList().containsExactly("_id", "number", "duration", "date", "type");
        assertThat(query.selection).isEqualTo("date > ?");
        assertThat(query.selectionArgs).asList().containsExactly("-1");
        assertThat(query.sortOrder).isEqualTo("date LIMIT 200");
    }

    @Test
    public void shouldBuildMostRecentQueryForSMS() throws Exception {
        BackupQueryBuilder.Query query = builder.buildMostRecentQueryForDataType(DataType.SMS);
        assertThat(query.uri).isEqualTo(Uri.parse("content://sms"));
        assertThat(query.projection).asList().containsExactly("date");
        assertThat(query.selection).isEqualTo("type <> ?");
        assertThat(query.selectionArgs).asList().containsExactly("3");
        assertThat(query.sortOrder).isEqualTo("date DESC LIMIT 1");
    }

    @Test
    public void shouldBuildMostRecentQueryForMMS() throws Exception {
        BackupQueryBuilder.Query query = builder.buildMostRecentQueryForDataType(DataType.MMS);
        assertThat(query.uri).isEqualTo(Uri.parse("content://mms"));
        assertThat(query.projection).asList().containsExactly("date");
        assertThat(query.selection).isNull();
        assertThat(query.selectionArgs).isNull();
        assertThat(query.sortOrder).isEqualTo("date DESC LIMIT 1");
    }

    @Test
    public void shouldBuildMostRecentQueryForCallLog() throws Exception {
        BackupQueryBuilder.Query query = builder.buildMostRecentQueryForDataType(DataType.CALLLOG);
        assertThat(query.uri).isEqualTo(Uri.parse("content://call_log/calls"));
        assertThat(query.projection).asList().containsExactly("date");
        assertThat(query.selection).isNull();
        assertThat(query.selectionArgs).isNull();
        assertThat(query.sortOrder).isEqualTo("date DESC LIMIT 1");
    }
}

