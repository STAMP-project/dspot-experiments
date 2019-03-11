package com.codahale.metrics.health;


import HealthCheckFilter.ALL;
import org.junit.Test;
import org.mockito.Mockito;


public class HealthCheckFilterTest {
    @Test
    public void theAllFilterMatchesAllHealthChecks() {
        assertThat(ALL.matches("", Mockito.mock(HealthCheck.class))).isTrue();
    }
}

