package keywhiz.service.resources;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.setup.Environment;
import java.util.TreeMap;
import javax.ws.rs.core.Response;
import keywhiz.KeywhizConfig;
import org.junit.Test;
import org.mockito.Mockito;


public class StatusResourceTest {
    HealthCheckRegistry registry;

    Environment environment;

    StatusResource status;

    KeywhizConfig keywhizConfig;

    @Test
    public void testStatusOk() throws Exception {
        Mockito.when(registry.runHealthChecks()).thenReturn(new TreeMap());
        Response r = status.get();
        assertThat(r.getStatus()).isEqualTo(200);
    }

    @Test
    public void testStatusWarn() throws Exception {
        TreeMap<String, HealthCheck.Result> map = new TreeMap<>();
        map.put("test", Result.unhealthy("failing"));
        Mockito.when(registry.runHealthChecks()).thenReturn(map);
        Response r = status.get();
        assertThat(r.getStatus()).isEqualTo(500);
    }
}

