package com.zegoggles.smssync;


import RuntimeEnvironment.application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AppTest {
    @Test
    public void shouldGetVersionCode() throws Exception {
        assertThat(App.getVersionCode(application)).isEqualTo(0);
    }

    @Test
    public void shouldTestOnSDCARD() throws Exception {
        assertThat(App.isInstalledOnSDCard(application)).isFalse();
    }
}

