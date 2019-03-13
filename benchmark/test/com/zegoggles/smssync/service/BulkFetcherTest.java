package com.zegoggles.smssync.service;


import DataType.MMS;
import com.zegoggles.smssync.contacts.ContactGroupIds;
import com.zegoggles.smssync.mail.DataType;
import java.util.EnumSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BulkFetcherTest {
    @Mock
    BackupItemsFetcher fetcher;

    BulkFetcher bulkFetcher;

    @Test
    public void shouldFetchAllItems() throws Exception {
        Mockito.when(fetcher.getItemsForDataType(DataType.SMS, null, 50)).thenReturn(cursor(3));
        Mockito.when(fetcher.getItemsForDataType(DataType.MMS, null, 47)).thenReturn(cursor(5));
        BackupCursors cursors = bulkFetcher.fetch(EnumSet.of(DataType.SMS, DataType.MMS), null, 50);
        assertThat(cursors.count()).isEqualTo(8);
        assertThat(cursors.count(DataType.SMS)).isEqualTo(3);
        assertThat(cursors.count(DataType.MMS)).isEqualTo(5);
    }

    @Test
    public void shouldFetchAllItemsRespectingMaxItems() throws Exception {
        Mockito.when(fetcher.getItemsForDataType(DataType.SMS, null, 5)).thenReturn(cursor(5));
        BackupCursors cursors = bulkFetcher.fetch(EnumSet.of(DataType.SMS, DataType.MMS), null, 5);
        assertThat(cursors.count()).isEqualTo(5);
        assertThat(cursors.count(DataType.SMS)).isEqualTo(5);
        Mockito.verify(fetcher, Mockito.never()).getItemsForDataType(ArgumentMatchers.eq(MMS), ArgumentMatchers.any(ContactGroupIds.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldFetchAllItemsEmptyList() throws Exception {
        BackupCursors cursors = bulkFetcher.fetch(EnumSet.noneOf(DataType.class), null, 50);
        assertThat(cursors.count()).isEqualTo(0);
    }
}

