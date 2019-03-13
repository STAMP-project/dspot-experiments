package org.robolectric.shadows;


import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Debug;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowDebugTest {
    private static final String TRACE_FILENAME = "dmtrace.trace";

    private Context context;

    @Test
    public void initNoCrash() {
        assertThat(Debug.getNativeHeapAllocatedSize()).isAtLeast(0L);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getRuntimeStats() {
        assertThat(Debug.getRuntimeStats()).isNotNull();
    }

    @Test
    public void startStopTracingShouldWriteFile() {
        Debug.startMethodTracing(ShadowDebugTest.TRACE_FILENAME);
        Debug.stopMethodTracing();
        assertThat(new File(context.getExternalFilesDir(null), ShadowDebugTest.TRACE_FILENAME).exists()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void startStopTracingSamplingShouldWriteFile() {
        Debug.startMethodTracingSampling(ShadowDebugTest.TRACE_FILENAME, 100, 100);
        Debug.stopMethodTracing();
        assertThat(new File(context.getExternalFilesDir(null), ShadowDebugTest.TRACE_FILENAME).exists()).isTrue();
    }

    @Test
    public void startTracingShouldThrowIfAlreadyStarted() {
        Debug.startMethodTracing(ShadowDebugTest.TRACE_FILENAME);
        try {
            Debug.startMethodTracing(ShadowDebugTest.TRACE_FILENAME);
            Assert.fail("RuntimeException not thrown.");
        } catch (RuntimeException e) {
            // expected
        }
    }

    @Test
    public void stopTracingShouldThrowIfNotStarted() {
        try {
            Debug.stopMethodTracing();
            Assert.fail("RuntimeException not thrown.");
        } catch (RuntimeException e) {
            // expected
        }
    }
}

