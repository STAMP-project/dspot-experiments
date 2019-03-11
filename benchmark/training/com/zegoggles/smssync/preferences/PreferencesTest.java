package com.zegoggles.smssync.preferences;


import com.zegoggles.smssync.mail.DataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PreferencesTest {
    Preferences preferences;

    @Test
    public void shouldTestForFirstUse() throws Exception {
        assertThat(preferences.isFirstUse()).isTrue();
        assertThat(preferences.isFirstUse()).isFalse();
    }

    @Test
    public void shouldTestForFirstBackup() throws Exception {
        assertThat(preferences.isFirstBackup()).isTrue();
    }

    @Test
    public void shouldTestForFirstBackupSMS() throws Exception {
        preferences.getDataTypePreferences().setMaxSyncedDate(DataType.SMS, 1234);
        assertThat(preferences.isFirstBackup()).isFalse();
    }

    @Test
    public void shouldTestForFirstBackupMMS() throws Exception {
        preferences.getDataTypePreferences().setMaxSyncedDate(DataType.MMS, 1234);
        assertThat(preferences.isFirstBackup()).isFalse();
    }

    @Test
    public void shouldTestForFirstBackupCallLog() throws Exception {
        preferences.getDataTypePreferences().setMaxSyncedDate(DataType.CALLLOG, 1234);
        assertThat(preferences.isFirstBackup()).isFalse();
    }
}

