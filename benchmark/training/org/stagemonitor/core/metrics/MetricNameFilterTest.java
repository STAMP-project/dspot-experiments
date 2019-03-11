package org.stagemonitor.core.metrics;


import com.codahale.metrics.Metric;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class MetricNameFilterTest {
    private final Metric mockMetric = Mockito.mock(Metric.class);

    @Test
    public void testNoTags() {
        MetricNameFilter includeFilter = MetricNameFilter.includePatterns(MetricName.name("foo").build());
        Assert.assertTrue(includeFilter.matches(MetricName.name("foo").build(), mockMetric));
        Assert.assertTrue(includeFilter.matches(MetricName.name("foo").tag("bar", "baz").build(), mockMetric));
        Assert.assertFalse(includeFilter.matches(MetricName.name("baz").tag("bar", "baz").build(), mockMetric));
    }

    @Test
    public void testInclusiveFilter() {
        MetricNameFilter includeFilter = MetricNameFilter.includePatterns(MetricName.name("foo").tag("bar", "baz").build());
        Assert.assertTrue(includeFilter.matches(MetricName.name("foo").tag("bar", "baz").build(), mockMetric));
        Assert.assertTrue(includeFilter.matches(MetricName.name("foo").tag("bar", "baz").tag("baz", "bar").build(), mockMetric));
        Assert.assertFalse(includeFilter.matches(MetricName.name("foo").tag("ba", "bar").build(), mockMetric));
        Assert.assertFalse(includeFilter.matches(MetricName.name("baz").tag("bar", "baz").build(), mockMetric));
    }

    @Test
    public void testExclusiveFilter() {
        MetricNameFilter includeFilter = MetricNameFilter.excludePatterns(MetricName.name("foo").tag("bar", "baz").build());
        Assert.assertFalse(includeFilter.matches(MetricName.name("foo").tag("bar", "baz").build(), mockMetric));
        Assert.assertFalse(includeFilter.matches(MetricName.name("foo").tag("bar", "baz").tag("baz", "bar").build(), mockMetric));
        Assert.assertTrue(includeFilter.matches(MetricName.name("foo").tag("ba", "bar").build(), mockMetric));
        Assert.assertTrue(includeFilter.matches(MetricName.name("baz").tag("bar", "baz").build(), mockMetric));
    }
}

