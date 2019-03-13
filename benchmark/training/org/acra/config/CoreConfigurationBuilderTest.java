package org.acra.config;


import android.app.Application;
import org.acra.annotation.AcraCore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 *
 *
 * @author F43nd1r
 * @since 01.02.18
 */
@RunWith(RobolectricTestRunner.class)
public class CoreConfigurationBuilderTest {
    @Test
    public void enabled() {
        Assert.assertTrue(new CoreConfigurationBuilder(new CoreConfigurationBuilderTest.AnnotatedClass()).enabled());
        Assert.assertFalse(new CoreConfigurationBuilder(new CoreConfigurationBuilderTest.NonAnnotatedClass()).enabled());
    }

    @AcraCore
    private static class AnnotatedClass extends Application {}

    private static class NonAnnotatedClass extends Application {}
}

