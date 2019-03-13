package io.dropwizard.lifecycle.setup;


import com.codahale.metrics.InstrumentedThreadFactory;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.lifecycle.ExecutorServiceManager;
import io.dropwizard.util.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class ScheduledExecutorServiceBuilderTest {
    private static final Duration DEFAULT_SHUTDOWN_PERIOD = Duration.seconds(5L);

    private final LifecycleEnvironment le;

    @Nullable
    private ScheduledExecutorService execTracker;

    public ScheduledExecutorServiceBuilderTest() {
        this.execTracker = null;
        this.le = Mockito.mock(LifecycleEnvironment.class);
        Mockito.when(le.getMetricRegistry()).thenReturn(new MetricRegistry());
    }

    @Test
    public void testBasicInvocation() {
        final String poolName = this.getClass().getSimpleName();
        final ScheduledExecutorServiceBuilder test = new ScheduledExecutorServiceBuilder(this.le, poolName, false);
        this.execTracker = test.build();
        assertThat(this.execTracker).isInstanceOf(ScheduledThreadPoolExecutor.class);
        final ScheduledThreadPoolExecutor castedExec = ((ScheduledThreadPoolExecutor) (this.execTracker));
        assertThat(castedExec.getRemoveOnCancelPolicy()).isFalse();
        assertThat(castedExec.getThreadFactory()).isInstanceOf(InstrumentedThreadFactory.class);
        final ArgumentCaptor<ExecutorServiceManager> esmCaptor = ArgumentCaptor.forClass(ExecutorServiceManager.class);
        Mockito.verify(this.le).manage(esmCaptor.capture());
        final ExecutorServiceManager esmCaptured = esmCaptor.getValue();
        assertThat(esmCaptured.getExecutor()).isSameAs(this.execTracker);
        assertThat(esmCaptured.getShutdownPeriod()).isEqualTo(ScheduledExecutorServiceBuilderTest.DEFAULT_SHUTDOWN_PERIOD);
        assertThat(esmCaptured.getPoolName()).isSameAs(poolName);
    }

    @Test
    public void testRemoveOnCancelTrue() {
        final String poolName = this.getClass().getSimpleName();
        final ScheduledExecutorServiceBuilder test = new ScheduledExecutorServiceBuilder(this.le, poolName, false);
        this.execTracker = test.removeOnCancelPolicy(true).build();
        assertThat(this.execTracker).isInstanceOf(ScheduledThreadPoolExecutor.class);
        final ScheduledThreadPoolExecutor castedExec = ((ScheduledThreadPoolExecutor) (this.execTracker));
        assertThat(castedExec.getRemoveOnCancelPolicy()).isTrue();
        final ArgumentCaptor<ExecutorServiceManager> esmCaptor = ArgumentCaptor.forClass(ExecutorServiceManager.class);
        Mockito.verify(this.le).manage(esmCaptor.capture());
        final ExecutorServiceManager esmCaptured = esmCaptor.getValue();
        assertThat(esmCaptured.getExecutor()).isSameAs(this.execTracker);
        assertThat(esmCaptured.getShutdownPeriod()).isEqualTo(ScheduledExecutorServiceBuilderTest.DEFAULT_SHUTDOWN_PERIOD);
        assertThat(esmCaptured.getPoolName()).isSameAs(poolName);
    }

    @Test
    public void testRemoveOnCancelFalse() {
        final String poolName = this.getClass().getSimpleName();
        final ScheduledExecutorServiceBuilder test = new ScheduledExecutorServiceBuilder(this.le, poolName, false);
        this.execTracker = test.removeOnCancelPolicy(false).build();
        assertThat(this.execTracker).isInstanceOf(ScheduledThreadPoolExecutor.class);
        final ScheduledThreadPoolExecutor castedExec = ((ScheduledThreadPoolExecutor) (this.execTracker));
        assertThat(castedExec.getRemoveOnCancelPolicy()).isFalse();
        final ArgumentCaptor<ExecutorServiceManager> esmCaptor = ArgumentCaptor.forClass(ExecutorServiceManager.class);
        Mockito.verify(this.le).manage(esmCaptor.capture());
        final ExecutorServiceManager esmCaptured = esmCaptor.getValue();
        assertThat(esmCaptured.getExecutor()).isSameAs(this.execTracker);
        assertThat(esmCaptured.getShutdownPeriod()).isEqualTo(ScheduledExecutorServiceBuilderTest.DEFAULT_SHUTDOWN_PERIOD);
        assertThat(esmCaptured.getPoolName()).isSameAs(poolName);
    }

    @Test
    public void testPredefinedThreadFactory() {
        final ThreadFactory tfactory = Mockito.mock(ThreadFactory.class);
        final String poolName = this.getClass().getSimpleName();
        final ScheduledExecutorServiceBuilder test = new ScheduledExecutorServiceBuilder(this.le, poolName, tfactory);
        this.execTracker = test.removeOnCancelPolicy(false).build();
        assertThat(this.execTracker).isInstanceOf(ScheduledThreadPoolExecutor.class);
        final ScheduledThreadPoolExecutor castedExec = ((ScheduledThreadPoolExecutor) (this.execTracker));
        assertThat(castedExec.getRemoveOnCancelPolicy()).isFalse();
        assertThat(castedExec.getThreadFactory()).isInstanceOf(InstrumentedThreadFactory.class);
        final ArgumentCaptor<ExecutorServiceManager> esmCaptor = ArgumentCaptor.forClass(ExecutorServiceManager.class);
        Mockito.verify(this.le).manage(esmCaptor.capture());
        final ExecutorServiceManager esmCaptured = esmCaptor.getValue();
        assertThat(esmCaptured.getExecutor()).isSameAs(this.execTracker);
        assertThat(esmCaptured.getShutdownPeriod()).isEqualTo(ScheduledExecutorServiceBuilderTest.DEFAULT_SHUTDOWN_PERIOD);
        assertThat(esmCaptured.getPoolName()).isSameAs(poolName);
    }
}

