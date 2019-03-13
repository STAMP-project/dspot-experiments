package org.robolectric.android;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowSystemClock;
import org.robolectric.util.ReflectionHelpers.ClassParameter;


/**
 * Integration tests for Android interceptors.
 */
@RunWith(AndroidJUnit4.class)
public class AndroidInterceptorsIntegrationTest {
    @Test
    public void systemLogE_shouldWriteToStderr() throws Throwable {
        PrintStream stderr = System.err;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(stream);
        System.setErr(printStream);
        try {
            AndroidInterceptorsIntegrationTest.invokeDynamic(System.class, "logE", void.class, ClassParameter.from(String.class, "hello"));
            AndroidInterceptorsIntegrationTest.invokeDynamic(System.class, "logE", void.class, ClassParameter.from(String.class, "world"), ClassParameter.from(Throwable.class, new Throwable("throw")));
            assertThat(stream.toString()).isEqualTo(String.format(("System.logE: hello%n" + "System.logE: worldjava.lang.Throwable: throw%n")));
        } finally {
            System.setErr(stderr);
        }
    }

    @Test
    public void systemNanoTime_shouldReturnShadowClockTime() throws Throwable {
        ShadowSystemClock.setNanoTime(123456);
        long nanoTime = AndroidInterceptorsIntegrationTest.invokeDynamic(System.class, "nanoTime", long.class);
        assertThat(nanoTime).isEqualTo(123456);
    }

    @Test
    public void systemCurrentTimeMillis_shouldReturnShadowClockTime() throws Throwable {
        ShadowSystemClock.setNanoTime(TimeUnit.MILLISECONDS.toNanos(54321));
        long currentTimeMillis = AndroidInterceptorsIntegrationTest.invokeDynamic(System.class, "currentTimeMillis", long.class);
        assertThat(currentTimeMillis).isEqualTo(54321);
    }
}

