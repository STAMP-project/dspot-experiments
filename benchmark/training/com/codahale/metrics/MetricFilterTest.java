package com.codahale.metrics;


import MetricFilter.ALL;
import org.junit.Test;
import org.mockito.Mockito;


public class MetricFilterTest {
    @Test
    public void theAllFilterMatchesAllMetrics() {
        assertThat(ALL.matches("", Mockito.mock(Metric.class))).isTrue();
    }

    @Test
    public void theStartsWithFilterMatches() {
        assertThat(MetricFilter.startsWith("foo").matches("foo.bar", Mockito.mock(Metric.class))).isTrue();
        assertThat(MetricFilter.startsWith("foo").matches("bar.foo", Mockito.mock(Metric.class))).isFalse();
    }

    @Test
    public void theEndsWithFilterMatches() {
        assertThat(MetricFilter.endsWith("foo").matches("foo.bar", Mockito.mock(Metric.class))).isFalse();
        assertThat(MetricFilter.endsWith("foo").matches("bar.foo", Mockito.mock(Metric.class))).isTrue();
    }

    @Test
    public void theContainsFilterMatches() {
        assertThat(MetricFilter.contains("foo").matches("bar.foo.bar", Mockito.mock(Metric.class))).isTrue();
        assertThat(MetricFilter.contains("foo").matches("bar.bar", Mockito.mock(Metric.class))).isFalse();
    }
}

