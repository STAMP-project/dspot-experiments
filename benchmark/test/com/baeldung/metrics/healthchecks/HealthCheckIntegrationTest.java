package com.baeldung.metrics.healthchecks;


import HealthCheck.Result;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HealthCheckIntegrationTest {
    @Test
    public void whenUseHealthCheck_thenHealthChecked() {
        HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
        healthCheckRegistry.register("db", new DatabaseHealthCheck());
        healthCheckRegistry.register("uc", new UserCenterHealthCheck());
        Assert.assertThat(healthCheckRegistry.getNames().size(), CoreMatchers.equalTo(2));
        Map<String, HealthCheck.Result> results = healthCheckRegistry.runHealthChecks();
        Assert.assertFalse(results.isEmpty());
        results.forEach(( k, v) -> assertTrue(v.isHealthy()));
        healthCheckRegistry.unregister("uc");
        Assert.assertThat(healthCheckRegistry.getNames().size(), CoreMatchers.equalTo(1));
    }
}

