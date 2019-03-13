package samples.powermockito.junit4.largemethod;


import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.largemethod.MethodExceedingJvmLimit;


@RunWith(PowerMockRunner.class)
@PrepareForTest(MethodExceedingJvmLimit.class)
public class LargeMethodTest {
    @Test
    public void largeMethodShouldBeOverridden() {
        try {
            MethodExceedingJvmLimit.init();
            Assert.fail("Method should be overridden and exception should be thrown");
        } catch (Exception e) {
            Assert.assertSame(IllegalAccessException.class, e.getClass());
            Assert.assertTrue(e.getMessage().contains("Method was too large and after instrumentation exceeded JVM limit"));
        }
    }

    @Test
    public void largeMethodShouldBeAbleToBeSuppressed() {
        suppress(PowerMockito.method(MethodExceedingJvmLimit.class, "init"));
        Assert.assertNull("Suppressed method should return: null", MethodExceedingJvmLimit.init());
    }

    @Test
    public void largeMethodShouldBeAbleToBeMocked() {
        mockStatic(MethodExceedingJvmLimit.class);
        Mockito.when(MethodExceedingJvmLimit.init()).thenReturn("ok");
        Assert.assertEquals("Mocked method should return: ok", "ok", MethodExceedingJvmLimit.init());
        verifyStatic(MethodExceedingJvmLimit.class);
        MethodExceedingJvmLimit.init();
    }

    @Test(expected = IllegalStateException.class)
    public void largeMethodShouldBeAbleToBeMockedAndThrowException() {
        mockStatic(MethodExceedingJvmLimit.class);
        Mockito.when(MethodExceedingJvmLimit.init()).thenThrow(new IllegalStateException());
        MethodExceedingJvmLimit.init();
        verifyStatic(MethodExceedingJvmLimit.class);
        MethodExceedingJvmLimit.init();
    }
}

