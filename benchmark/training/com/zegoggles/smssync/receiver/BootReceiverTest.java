package com.zegoggles.smssync.receiver;


import Intent.ACTION_BOOT_COMPLETED;
import RuntimeEnvironment.application;
import android.content.Intent;
import com.zegoggles.smssync.service.BackupJobs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BootReceiverTest {
    @Mock
    BackupJobs backupJobs;

    BootReceiver receiver;

    @Test
    public void shouldScheduleBootupBackupAfterBootup() throws Exception {
        receiver.onReceive(application, new Intent().setAction(ACTION_BOOT_COMPLETED));
        Mockito.verify(backupJobs, Mockito.times(1)).scheduleBootup();
    }
}

