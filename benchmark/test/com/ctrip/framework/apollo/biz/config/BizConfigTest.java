package com.ctrip.framework.apollo.biz.config;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class BizConfigTest {
    @Mock
    private ConfigurableEnvironment environment;

    private BizConfig bizConfig;

    @Test
    public void testReleaseMessageNotificationBatch() throws Exception {
        int someBatch = 20;
        Mockito.when(environment.getProperty("apollo.release-message.notification.batch")).thenReturn(String.valueOf(someBatch));
        Assert.assertEquals(someBatch, bizConfig.releaseMessageNotificationBatch());
    }

    @Test
    public void testReleaseMessageNotificationBatchWithDefaultValue() throws Exception {
        int defaultBatch = 100;
        Assert.assertEquals(defaultBatch, bizConfig.releaseMessageNotificationBatch());
    }

    @Test
    public void testReleaseMessageNotificationBatchWithInvalidNumber() throws Exception {
        int someBatch = -20;
        int defaultBatch = 100;
        Mockito.when(environment.getProperty("apollo.release-message.notification.batch")).thenReturn(String.valueOf(someBatch));
        Assert.assertEquals(defaultBatch, bizConfig.releaseMessageNotificationBatch());
    }

    @Test
    public void testReleaseMessageNotificationBatchWithNAN() throws Exception {
        String someNAN = "someNAN";
        int defaultBatch = 100;
        Mockito.when(environment.getProperty("apollo.release-message.notification.batch")).thenReturn(someNAN);
        Assert.assertEquals(defaultBatch, bizConfig.releaseMessageNotificationBatch());
    }

    @Test
    public void testCheckInt() throws Exception {
        int someInvalidValue = 1;
        int anotherInvalidValue = 2;
        int someValidValue = 3;
        int someDefaultValue = 10;
        int someMin = someInvalidValue + 1;
        int someMax = anotherInvalidValue - 1;
        Assert.assertEquals(someDefaultValue, bizConfig.checkInt(someInvalidValue, someMin, Integer.MAX_VALUE, someDefaultValue));
        Assert.assertEquals(someDefaultValue, bizConfig.checkInt(anotherInvalidValue, Integer.MIN_VALUE, someMax, someDefaultValue));
        Assert.assertEquals(someValidValue, bizConfig.checkInt(someValidValue, Integer.MIN_VALUE, Integer.MAX_VALUE, someDefaultValue));
    }
}

