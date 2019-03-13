package org.robolectric;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RoboSettingsTest {
    private boolean originalUseGlobalScheduler;

    @Test
    public void isUseGlobalScheduler_defaultFalse() {
        Assert.assertFalse(RoboSettings.isUseGlobalScheduler());
    }

    @Test
    public void setUseGlobalScheduler() {
        RoboSettings.setUseGlobalScheduler(true);
        Assert.assertTrue(RoboSettings.isUseGlobalScheduler());
    }
}

