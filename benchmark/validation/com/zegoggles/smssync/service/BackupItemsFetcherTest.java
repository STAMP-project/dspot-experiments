package com.zegoggles.smssync.service;


import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import com.zegoggles.smssync.mail.DataType;
import com.zegoggles.smssync.preferences.Preferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BackupItemsFetcherTest {
    BackupItemsFetcher fetcher;

    @Mock
    BackupQueryBuilder queryBuilder;

    @Mock
    ContentResolver resolver;

    Context context;

    Preferences preferences;

    @Test
    public void shouldGetItemsForDataType() throws Exception {
        preferences.getDataTypePreferences().setBackupEnabled(true, DataType.SMS);
        assertThat(fetcher.getItemsForDataType(DataType.SMS, null, (-1)).getCount()).isEqualTo(0);
        Mockito.verifyZeroInteractions(resolver);
    }

    @Test
    public void shouldCatchSQLiteExceptions() throws Exception {
        preferences.getDataTypePreferences().setBackupEnabled(true, DataType.SMS);
        Mockito.when(resolver.query(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString())).thenThrow(new SQLiteException());
        mockEmptyQuery();
        assertThat(fetcher.getItemsForDataType(DataType.SMS, null, (-1)).getCount()).isEqualTo(0);
    }

    @Test
    public void shouldCatchNullPointerExceptions() throws Exception {
        preferences.getDataTypePreferences().setBackupEnabled(true, DataType.SMS);
        Mockito.when(resolver.query(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString())).thenThrow(new NullPointerException());
        mockEmptyQuery();
        assertThat(fetcher.getItemsForDataType(DataType.SMS, null, (-1)).getCount()).isEqualTo(0);
    }

    @Test
    public void shouldReturnDefaultIfDataTypeCannotBeRead() throws Exception {
        for (DataType type : DataType.values()) {
            assertThat(fetcher.getMostRecentTimestamp(type)).isEqualTo((-1));
        }
    }

    @Test
    public void shouldGetMostRecentTimestampForItemTypeSMS() throws Exception {
        mockMostRecentTimestampForType(DataType.SMS, 23L);
        assertThat(fetcher.getMostRecentTimestamp(DataType.SMS)).isEqualTo(23L);
    }

    @Test
    public void shouldMostRecentTimestampForItemTypeMMS() throws Exception {
        mockMostRecentTimestampForType(DataType.MMS, 23L);
        assertThat(fetcher.getMostRecentTimestamp(DataType.MMS)).isEqualTo(23L);
    }

    @Test
    public void shouldGetMostRecentTimestampForItemTypeCallLog() throws Exception {
        mockMostRecentTimestampForType(DataType.CALLLOG, 23L);
        assertThat(fetcher.getMostRecentTimestamp(DataType.CALLLOG)).isEqualTo(23L);
    }
}

