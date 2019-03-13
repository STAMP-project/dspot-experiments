package com.codahale.metrics.jvm;


import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Locale;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ThreadDeadlockDetectorTest {
    private final ThreadMXBean threads = Mockito.mock(ThreadMXBean.class);

    private final ThreadDeadlockDetector detector = new ThreadDeadlockDetector(threads);

    @Test
    public void returnsAnEmptySetIfNoThreadsAreDeadlocked() {
        Mockito.when(threads.findDeadlockedThreads()).thenReturn(null);
        assertThat(detector.getDeadlockedThreads()).isEmpty();
    }

    @Test
    public void returnsASetOfThreadsIfAnyAreDeadlocked() {
        final ThreadInfo thread1 = Mockito.mock(ThreadInfo.class);
        Mockito.when(thread1.getThreadName()).thenReturn("thread1");
        Mockito.when(thread1.getLockName()).thenReturn("lock2");
        Mockito.when(thread1.getLockOwnerName()).thenReturn("thread2");
        Mockito.when(thread1.getStackTrace()).thenReturn(new StackTraceElement[]{ new StackTraceElement("Blah", "bloo", "Blah.java", 150), new StackTraceElement("Blah", "blee", "Blah.java", 100) });
        final ThreadInfo thread2 = Mockito.mock(ThreadInfo.class);
        Mockito.when(thread2.getThreadName()).thenReturn("thread2");
        Mockito.when(thread2.getLockName()).thenReturn("lock1");
        Mockito.when(thread2.getLockOwnerName()).thenReturn("thread1");
        Mockito.when(thread2.getStackTrace()).thenReturn(new StackTraceElement[]{ new StackTraceElement("Blah", "blee", "Blah.java", 100), new StackTraceElement("Blah", "bloo", "Blah.java", 150) });
        final long[] ids = new long[]{ 1, 2 };
        Mockito.when(threads.findDeadlockedThreads()).thenReturn(ids);
        Mockito.when(threads.getThreadInfo(ArgumentMatchers.eq(ids), ArgumentMatchers.anyInt())).thenReturn(new ThreadInfo[]{ thread1, thread2 });
        assertThat(detector.getDeadlockedThreads()).containsOnly(String.format(Locale.US, ("thread1 locked on lock2 (owned by thread2):%n" + ("\t at Blah.bloo(Blah.java:150)%n" + "\t at Blah.blee(Blah.java:100)%n"))), String.format(Locale.US, ("thread2 locked on lock1 (owned by thread1):%n" + ("\t at Blah.blee(Blah.java:100)%n" + "\t at Blah.bloo(Blah.java:150)%n"))));
    }

    @Test
    public void autoDiscoversTheThreadMXBean() {
        assertThat(new ThreadDeadlockDetector().getDeadlockedThreads()).isNotNull();
    }
}

