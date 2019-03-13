package io.dropwizard.lifecycle.setup;


import com.codahale.metrics.InstrumentedThreadFactory;
import io.dropwizard.util.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.slf4j.Logger;


public class ExecutorServiceBuilderTest {
    private static final String WARNING = "Parameter 'maximumPoolSize' is conflicting with unbounded work queues";

    private ExecutorServiceBuilder executorServiceBuilder;

    private Logger log;

    @Test
    public void testGiveAWarningAboutMaximumPoolSizeAndUnboundedQueue() {
        executorServiceBuilder.minThreads(4).maxThreads(8).build();
        Mockito.verify(log).warn(ExecutorServiceBuilderTest.WARNING);
    }

    @Test
    public void testGiveNoWarningAboutMaximumPoolSizeAndBoundedQueue() throws InterruptedException {
        ExecutorService exe = executorServiceBuilder.minThreads(4).maxThreads(8).workQueue(new ArrayBlockingQueue(16)).build();
        Mockito.verify(log, Mockito.never()).warn(ExecutorServiceBuilderTest.WARNING);
        assertCanExecuteAtLeast2ConcurrentTasks(exe);
    }

    /**
     * There should be no warning about using a Executors.newSingleThreadExecutor() equivalent
     *
     * @see java.util.concurrent.Executors#newSingleThreadExecutor()
     */
    @Test
    public void shouldNotWarnWhenSettingUpSingleThreadedPool() {
        executorServiceBuilder.minThreads(1).maxThreads(1).keepAliveTime(Duration.milliseconds(0)).workQueue(new LinkedBlockingQueue()).build();
        Mockito.verify(log, Mockito.never()).warn(ArgumentMatchers.anyString());
    }

    /**
     * There should be no warning about using a Executors.newCachedThreadPool() equivalent
     *
     * @see java.util.concurrent.Executors#newCachedThreadPool()
     */
    @Test
    public void shouldNotWarnWhenSettingUpCachedThreadPool() throws InterruptedException {
        ExecutorService exe = executorServiceBuilder.minThreads(0).maxThreads(Integer.MAX_VALUE).keepAliveTime(Duration.seconds(60)).workQueue(new SynchronousQueue()).build();
        Mockito.verify(log, Mockito.never()).warn(ArgumentMatchers.anyString());
        assertCanExecuteAtLeast2ConcurrentTasks(exe);// cached thread pools work right?

    }

    @Test
    public void shouldNotWarnWhenUsingTheDefaultConfiguration() {
        executorServiceBuilder.build();
        Mockito.verify(log, Mockito.never()).warn(ArgumentMatchers.anyString());
    }

    /**
     * Setting large max threads without large min threads is misleading on the default queue implementation
     * It should warn or work
     */
    @Test
    public void shouldBeAbleToExecute2TasksAtOnceWithLargeMaxThreadsOrBeWarnedOtherwise() {
        ExecutorService exe = executorServiceBuilder.maxThreads(Integer.MAX_VALUE).build();
        try {
            Mockito.verify(log).warn(ArgumentMatchers.anyString());
        } catch (WantedButNotInvoked error) {
            // no warning has been given so we should be able to execute at least 2 things at once
            assertCanExecuteAtLeast2ConcurrentTasks(exe);
        }
    }

    @Test
    public void shouldUseInstrumentedThreadFactory() {
        ExecutorService exe = executorServiceBuilder.build();
        final ThreadPoolExecutor castedExec = ((ThreadPoolExecutor) (exe));
        assertThat(castedExec.getThreadFactory()).isInstanceOf(InstrumentedThreadFactory.class);
    }
}

