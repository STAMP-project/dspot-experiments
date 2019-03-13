package org.robolectric.shadows;


import android.os.Process;
import android.os.Process.THREAD_PRIORITY_AUDIO;
import android.os.Process.THREAD_PRIORITY_URGENT_AUDIO;
import android.os.Process.THREAD_PRIORITY_VIDEO;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test ShadowProcess
 */
@RunWith(AndroidJUnit4.class)
public class ShadowProcessTest {
    @Test
    public void shouldBeZeroWhenNotSet() {
        assertThat(Process.myPid()).isEqualTo(0);
    }

    @Test
    public void shouldGetMyPidAsSet() {
        ShadowProcess.setPid(3);
        assertThat(Process.myPid()).isEqualTo(3);
    }

    @Test
    public void shouldGetMyUidAsSet() {
        ShadowProcess.setUid(123);
        assertThat(Process.myUid()).isEqualTo(123);
    }

    @Test
    public void myTid_mainThread_returnsCurrentThreadId() {
        assertThat(Process.myTid()).isEqualTo(Thread.currentThread().getId());
    }

    @Test
    public void myTid_backgroundThread_returnsCurrentThreadId() throws Exception {
        AtomicBoolean ok = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            ok.set(((Process.myTid()) == (Thread.currentThread().getId())));
        });
        thread.start();
        thread.join();
        assertThat(ok.get()).isTrue();
    }

    @Test
    public void myTid_returnsDifferentValuesForDifferentThreads() throws Exception {
        AtomicInteger tid1 = new AtomicInteger(0);
        AtomicInteger tid2 = new AtomicInteger(0);
        Thread thread1 = new Thread(() -> {
            tid1.set(Process.myTid());
        });
        Thread thread2 = new Thread(() -> {
            tid2.set(Process.myTid());
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        assertThat(tid1).isNotEqualTo(tid2);
    }

    @Test
    public void getThreadPriority_notSet_returnsZero() {
        assertThat(Process.getThreadPriority(123)).isEqualTo(0);
    }

    @Test
    public void getThreadPriority_returnsThreadPriority() {
        Process.setThreadPriority(123, THREAD_PRIORITY_VIDEO);
        assertThat(Process.getThreadPriority(123)).isEqualTo(THREAD_PRIORITY_VIDEO);
    }

    @Test
    public void getThreadPriority_currentThread_returnsCurrentThreadPriority() {
        Process.setThreadPriority(THREAD_PRIORITY_AUDIO);
        assertThat(/* tid= */
        Process.getThreadPriority(0)).isEqualTo(THREAD_PRIORITY_AUDIO);
    }

    @Test
    public void setThreadPriorityOneArgument_setsCurrentThreadPriority() {
        Process.setThreadPriority(THREAD_PRIORITY_URGENT_AUDIO);
        assertThat(Process.getThreadPriority(Process.myTid())).isEqualTo(THREAD_PRIORITY_URGENT_AUDIO);
    }
}

