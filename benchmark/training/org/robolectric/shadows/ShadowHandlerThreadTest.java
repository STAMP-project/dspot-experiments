package org.robolectric.shadows;


import android.os.HandlerThread;
import android.os.Looper;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowHandlerThreadTest {
    private HandlerThread handlerThread;

    @Test
    public void shouldReturnLooper() throws Exception {
        handlerThread = new HandlerThread("test");
        handlerThread.start();
        Assert.assertNotNull(handlerThread.getLooper());
        Assert.assertNotSame(handlerThread.getLooper(), ApplicationProvider.getApplicationContext().getMainLooper());
    }

    @Test
    public void shouldReturnNullIfThreadHasNotBeenStarted() throws Exception {
        handlerThread = new HandlerThread("test");
        Assert.assertNull(handlerThread.getLooper());
    }

    @Test
    public void shouldQuitLooperAndThread() throws Exception {
        handlerThread = new HandlerThread("test");
        Thread.setDefaultUncaughtExceptionHandler(new ShadowHandlerThreadTest.MyUncaughtExceptionHandler());
        handlerThread.setUncaughtExceptionHandler(new ShadowHandlerThreadTest.MyUncaughtExceptionHandler());
        handlerThread.start();
        Assert.assertTrue(handlerThread.isAlive());
        Assert.assertTrue(handlerThread.quit());
        handlerThread.join();
        Assert.assertFalse(handlerThread.isAlive());
        handlerThread = null;
    }

    @Test
    public void shouldStopThreadIfLooperIsQuit() throws Exception {
        handlerThread = new HandlerThread("test1");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Assert.assertFalse(Shadows.shadowOf(looper).quit);
        looper.quit();
        handlerThread.join();
        Assert.assertFalse(handlerThread.isAlive());
        Assert.assertTrue(Shadows.shadowOf(looper).quit);
        handlerThread = null;
    }

    @Test
    public void shouldCallOnLooperPrepared() throws Exception {
        final Boolean[] wasCalled = new Boolean[]{ false };
        final CountDownLatch latch = new CountDownLatch(1);
        handlerThread = new HandlerThread("test") {
            @Override
            protected void onLooperPrepared() {
                wasCalled[0] = true;
                latch.countDown();
            }
        };
        handlerThread.start();
        try {
            Assert.assertNotNull(handlerThread.getLooper());
            latch.await(1, TimeUnit.SECONDS);
            Assert.assertTrue(wasCalled[0]);
        } finally {
            handlerThread.quit();
        }
    }

    private static class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            e.printStackTrace();
        }
    }
}

