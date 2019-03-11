package com.codahale.metrics.health.jvm;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.jvm.ThreadDeadlockDetector;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import org.mockito.Mockito;


public class ThreadDeadlockHealthCheckTest {
    @Test
    public void isHealthyIfNoThreadsAreDeadlocked() {
        final ThreadDeadlockDetector detector = Mockito.mock(ThreadDeadlockDetector.class);
        final ThreadDeadlockHealthCheck healthCheck = new ThreadDeadlockHealthCheck(detector);
        Mockito.when(detector.getDeadlockedThreads()).thenReturn(Collections.emptySet());
        assertThat(healthCheck.execute().isHealthy()).isTrue();
    }

    @Test
    public void isUnhealthyIfThreadsAreDeadlocked() {
        final Set<String> threads = new TreeSet<>();
        threads.add("one");
        threads.add("two");
        final ThreadDeadlockDetector detector = Mockito.mock(ThreadDeadlockDetector.class);
        final ThreadDeadlockHealthCheck healthCheck = new ThreadDeadlockHealthCheck(detector);
        Mockito.when(detector.getDeadlockedThreads()).thenReturn(threads);
        final HealthCheck.Result result = healthCheck.execute();
        assertThat(result.isHealthy()).isFalse();
        assertThat(result.getMessage()).isEqualTo("[one, two]");
    }

    @Test
    public void automaticallyUsesThePlatformThreadBeans() {
        final ThreadDeadlockHealthCheck healthCheck = new ThreadDeadlockHealthCheck();
        assertThat(healthCheck.execute().isHealthy()).isTrue();
    }
}

