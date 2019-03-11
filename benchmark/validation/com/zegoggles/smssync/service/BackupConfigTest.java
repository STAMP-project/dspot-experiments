package com.zegoggles.smssync.service;


import DataType.MMS;
import com.zegoggles.smssync.contacts.ContactGroup;
import com.zegoggles.smssync.mail.BackupImapStore;
import com.zegoggles.smssync.mail.DataType;
import java.util.EnumSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static BackupType.MANUAL;


@RunWith(RobolectricTestRunner.class)
public class BackupConfigTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckForDataTypesEmpty() throws Exception {
        new BackupConfig(Mockito.mock(BackupImapStore.class), 0, (-1), ContactGroup.EVERYBODY, MANUAL, EnumSet.noneOf(DataType.class), false);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckForDataTypesNull() throws Exception {
        new BackupConfig(Mockito.mock(BackupImapStore.class), 0, (-1), ContactGroup.EVERYBODY, MANUAL, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckForPositiveTry() throws Exception {
        new BackupConfig(Mockito.mock(BackupImapStore.class), (-1), (-1), ContactGroup.EVERYBODY, MANUAL, EnumSet.of(MMS), false);
    }
}

