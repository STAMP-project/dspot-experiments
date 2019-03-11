package com.zegoggles.smssync.activity;


import RuntimeEnvironment.application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PreferenceTitlesTest {
    private PreferenceTitles subject;

    @Test
    public void testParseValidKey() {
        final int res = subject.getTitleRes("com.zegoggles.smssync.activity.fragments.AutoBackupSettings");
        assertThat(res).isGreaterThan(0);
        String resolved = application.getString(res);
        assertThat(resolved).isEqualTo("Auto backup settings");
    }

    @Test
    public void testInvalidKeyReturnsZero() {
        final int res = subject.getTitleRes("foo.bar.not.found");
        assertThat(res).isEqualTo(0);
    }
}

