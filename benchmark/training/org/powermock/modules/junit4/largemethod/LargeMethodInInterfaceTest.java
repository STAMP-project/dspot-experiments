package org.powermock.modules.junit4.largemethod;


import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.powermockito.junit4.largemethod.InterfaceMethodExceedingJvmLimit;


@RunWith(PowerMockRunner.class)
@PrepareForTest(InterfaceMethodExceedingJvmLimit.class)
public class LargeMethodInInterfaceTest {
    @Test
    public void largeMethodShouldBeOverridden() {
        try {
            InterfaceMethodExceedingJvmLimit.init();
            Assert.fail("Method should be overridden and exception should be thrown");
        } catch (Exception e) {
            Assert.assertSame(IllegalAccessException.class, e.getClass());
            Assert.assertTrue(e.getMessage().contains("Method was too large and after instrumentation exceeded JVM limit"));
        }
    }

    @Test
    public void largeMethodShouldBeAbleToBeSuppressed() {
        suppress(PowerMockito.method(InterfaceMethodExceedingJvmLimit.class, "init"));
        Assert.assertNull("Suppressed method should return: null", InterfaceMethodExceedingJvmLimit.init());
    }

    @Test
    public void largeMethodShouldBeAbleToBeMocked() {
        mockStatic(InterfaceMethodExceedingJvmLimit.class);
        Mockito.when(InterfaceMethodExceedingJvmLimit.init()).thenReturn("ok");
        Assert.assertEquals("Mocked method should return: ok", "ok", InterfaceMethodExceedingJvmLimit.init());
        verifyStatic(InterfaceMethodExceedingJvmLimit.class);
        InterfaceMethodExceedingJvmLimit.init();
    }

    @Test(expected = IllegalStateException.class)
    public void largeMethodShouldBeAbleToBeMockedAndThrowException() {
        mockStatic(InterfaceMethodExceedingJvmLimit.class);
        Mockito.when(InterfaceMethodExceedingJvmLimit.init()).thenThrow(new IllegalStateException());
        InterfaceMethodExceedingJvmLimit.init();
        verifyStatic(InterfaceMethodExceedingJvmLimit.class);
        InterfaceMethodExceedingJvmLimit.init();
    }
}

