package com.zegoggles.smssync.compat;


import Build.VERSION_CODES;
import RuntimeEnvironment.application;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
public class SmsReceiverTest {
    private SmsReceiver subject;

    @Test
    @Config(sdk = VERSION_CODES.JELLY_BEAN)
    public void testOnReceivePreKitKat() {
        subject.onReceive(application, new Intent());
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void testOnReceiveKitKat() {
        subject.onReceive(application, new Intent());
    }

    @Test
    @Config(sdk = VERSION_CODES.JELLY_BEAN)
    public void testIsSmsBackupDefaultSmsAppPreKitKat() {
        assertThat(SmsReceiver.isSmsBackupDefaultSmsApp(application)).isFalse();
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void testIsSmsBackupDefaultSmsAppKitKat() {
        assertThat(SmsReceiver.isSmsBackupDefaultSmsApp(application)).isFalse();
    }
}

