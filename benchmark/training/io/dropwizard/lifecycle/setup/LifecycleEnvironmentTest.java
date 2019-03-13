package io.dropwizard.lifecycle.setup;


import com.codahale.metrics.MetricRegistry;
import io.dropwizard.lifecycle.JettyManaged;
import io.dropwizard.lifecycle.Managed;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class LifecycleEnvironmentTest {
    private final LifecycleEnvironment environment = new LifecycleEnvironment(new MetricRegistry());

    @Test
    public void managesLifeCycleObjects() throws Exception {
        final LifeCycle lifeCycle = Mockito.mock(LifeCycle.class);
        environment.manage(lifeCycle);
        final ContainerLifeCycle container = new ContainerLifeCycle();
        environment.attach(container);
        assertThat(container.getBeans()).contains(lifeCycle);
    }

    @Test
    public void managesManagedObjects() throws Exception {
        final Managed managed = Mockito.mock(Managed.class);
        environment.manage(managed);
        final ContainerLifeCycle container = new ContainerLifeCycle();
        environment.attach(container);
        final Object bean = new java.util.ArrayList(container.getBeans()).get(0);
        assertThat(bean).isInstanceOf(JettyManaged.class);
        final JettyManaged jettyManaged = ((JettyManaged) (bean));
        assertThat(jettyManaged.getManaged()).isEqualTo(managed);
    }

    @Test
    public void scheduledExecutorServiceBuildsDaemonThreads() throws InterruptedException, ExecutionException {
        final ScheduledExecutorService executorService = environment.scheduledExecutorService("daemon-%d", true).build();
        final Future<Boolean> isDaemon = executorService.submit(() -> Thread.currentThread().isDaemon());
        assertThat(isDaemon.get()).isTrue();
    }

    @Test
    public void scheduledExecutorServiceBuildsUserThreadsByDefault() throws InterruptedException, ExecutionException {
        final ScheduledExecutorService executorService = environment.scheduledExecutorService("user-%d").build();
        final Future<Boolean> isDaemon = executorService.submit(() -> Thread.currentThread().isDaemon());
        assertThat(isDaemon.get()).isFalse();
    }

    @Test
    public void scheduledExecutorServiceThreadFactory() throws InterruptedException, ExecutionException {
        final String expectedName = "DropWizard ThreadFactory Test";
        final String expectedNamePattern = expectedName + "-%d";
        final ThreadFactory tfactory = buildThreadFactory(expectedNamePattern);
        final ScheduledExecutorService executorService = environment.scheduledExecutorService("DropWizard Service", tfactory).build();
        final Future<Boolean> isFactoryInUse = executorService.submit(() -> Thread.currentThread().getName().startsWith(expectedName));
        assertThat(isFactoryInUse.get()).isTrue();
    }

    @Test
    public void executorServiceThreadFactory() throws InterruptedException, ExecutionException {
        final String expectedName = "DropWizard ThreadFactory Test";
        final String expectedNamePattern = expectedName + "-%d";
        final ThreadFactory tfactory = buildThreadFactory(expectedNamePattern);
        final ExecutorService executorService = environment.executorService("Dropwizard Service", tfactory).build();
        final Future<Boolean> isFactoryInUse = executorService.submit(() -> Thread.currentThread().getName().startsWith(expectedName));
        assertThat(isFactoryInUse.get()).isTrue();
    }
}

