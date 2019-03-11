package io.dropwizard.lifecycle;


import io.dropwizard.util.Duration;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ExecutorServiceManagerTest {
    private static final Duration TEST_DURATION = Duration.seconds(1L);

    private final ExecutorService exec;

    public ExecutorServiceManagerTest() {
        // This is called setUp every test
        this.exec = Mockito.mock(ExecutorService.class);
    }

    @Test
    public void testAccessors() {
        // This test verifies the accessors behave as advertised for other unit
        // tests.
        final String poolName = this.getClass().getSimpleName();
        final ExecutorServiceManager test = new ExecutorServiceManager(this.exec, ExecutorServiceManagerTest.TEST_DURATION, poolName);
        assertThat(test.getShutdownPeriod()).isSameAs(ExecutorServiceManagerTest.TEST_DURATION);
        assertThat(test.getPoolName()).isSameAs(poolName);
        assertThat(test.getExecutor()).isSameAs(this.exec);
    }

    @Test
    public void testManaged() throws Exception {
        final String poolName = this.getClass().getSimpleName();
        Mockito.when(this.exec.awaitTermination(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(true);
        final ExecutorServiceManager test = new ExecutorServiceManager(this.exec, ExecutorServiceManagerTest.TEST_DURATION, poolName);
        test.start();
        Mockito.verifyZeroInteractions(this.exec);
        test.stop();
        Mockito.verify(this.exec).shutdown();
        Mockito.verify(this.exec).awaitTermination(ExecutorServiceManagerTest.TEST_DURATION.getQuantity(), ExecutorServiceManagerTest.TEST_DURATION.getUnit());
    }

    @Test
    public void testManagedTimeout() throws Exception {
        final String poolName = this.getClass().getSimpleName();
        Mockito.when(this.exec.awaitTermination(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(false);
        final ExecutorServiceManager test = new ExecutorServiceManager(this.exec, ExecutorServiceManagerTest.TEST_DURATION, poolName);
        test.start();
        Mockito.verifyZeroInteractions(this.exec);
        test.stop();
        Mockito.verify(this.exec).shutdown();
        Mockito.verify(this.exec).awaitTermination(ExecutorServiceManagerTest.TEST_DURATION.getQuantity(), ExecutorServiceManagerTest.TEST_DURATION.getUnit());
    }
}

