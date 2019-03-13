package io.dropwizard.jdbi3;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class JdbiHealthCheckTest {
    private static final String VALIDATION_QUERY = "select 1";

    private Jdbi jdbi;

    private Handle handle;

    private ExecutorService executorService;

    private JdbiHealthCheck sut;

    @Test
    public void testNoTimeoutReturnsHealthy() throws Exception {
        Mockito.when(handle.execute(JdbiHealthCheckTest.VALIDATION_QUERY)).thenReturn(0);
        HealthCheck.Result result = sut.check();
        assertThat(result.isHealthy()).isTrue();
    }

    @Test
    public void testItTimesOutProperly() throws Exception {
        Mockito.when(handle.execute(JdbiHealthCheckTest.VALIDATION_QUERY)).thenAnswer(((Answer<Integer>) (( invocation) -> {
            TimeUnit.SECONDS.sleep(10);
            return null;
        })));
        HealthCheck.Result result = sut.check();
        assertThat(result.isHealthy()).isFalse();
    }
}

