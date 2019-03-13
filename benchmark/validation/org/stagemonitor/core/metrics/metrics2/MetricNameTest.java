package org.stagemonitor.core.metrics.metrics2;


import MetricName.MetricNameTemplate;
import org.junit.Assert;
import org.junit.Test;


public class MetricNameTest {
    @Test
    public void testEquals() {
        Assert.assertEquals(MetricName.name("foo").tag("bar", "baz").tag("qux", "quux").build(), MetricName.name("foo").tag("bar", "baz").tag("qux", "quux").build());
        Assert.assertEquals(MetricName.name("foo").tag("qux", "quux").tag("bar", "baz").build(), MetricName.name("foo").tag("bar", "baz").tag("qux", "quux").build());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(MetricName.name("foo").tag("bar", "baz").tag("qux", "quux").build().hashCode(), MetricName.name("foo").tag("bar", "baz").tag("qux", "quux").build().hashCode());
        Assert.assertEquals(MetricName.name("foo").tag("qux", "quux").tag("bar", "baz").build().hashCode(), MetricName.name("foo").tag("bar", "baz").tag("qux", "quux").build().hashCode());
    }

    @Test
    public void testTemplateSingleValue() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar");
        Assert.assertEquals(MetricName.name("foo").tag("bar", "baz").tag("qux", "quux").build(), metricNameTemplate.build("baz"));
        Assert.assertSame(metricNameTemplate.build("baz"), metricNameTemplate.build("baz"));
        Assert.assertNotEquals(metricNameTemplate.build("baz"), metricNameTemplate.build("baz2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTemplateSingleValueBuildMultipleValues() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar");
        metricNameTemplate.build("foo", "bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTemplateSingleValueEmptyValues() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar");
        metricNameTemplate.build();
    }

    @Test(expected = NullPointerException.class)
    public void testTemplateSingleValueNull() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar");
        metricNameTemplate.build(((String) (null)));
    }

    @Test
    public void testTemplateMultipleValues() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar", "qux");
        Assert.assertEquals(MetricName.name("foo").tag("bar", "baz").tag("qux", "q").build(), metricNameTemplate.build("baz", "q"));
        Assert.assertSame(metricNameTemplate.build("baz", "quux"), metricNameTemplate.build("baz", "quux"));
        Assert.assertNotEquals(metricNameTemplate.build("baz", "quux"), metricNameTemplate.build("baz2", "quux"));
    }

    @Test
    public void testTemplateMultipleValues2() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").templateFor("bar", "qux");
        Assert.assertEquals(MetricName.name("foo").tag("bar", "baz").tag("qux", "q").build(), metricNameTemplate.build("baz", "q"));
        Assert.assertSame(metricNameTemplate.build("baz", "quux"), metricNameTemplate.build("baz", "quux"));
        Assert.assertNotEquals(metricNameTemplate.build("baz", "quux"), metricNameTemplate.build("baz2", "quux"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTemplateMultipleValuesBuildEmptyValues() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar", "qux");
        metricNameTemplate.build("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTemplateMultipleValuesBuildTooFewValues() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar", "qux");
        metricNameTemplate.build("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTemplateMultipleValuesBuildTooManyValues() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar", "qux");
        metricNameTemplate.build("foo", "bar", "baz");
    }

    @Test
    public void testMetricNameNull() {
        MetricName.name("foo").tag("bar", null).tag("qux", null).build();
    }

    @Test
    public void testTemplateMultipleValuesNull() {
        final MetricName.MetricNameTemplate metricNameTemplate = MetricName.name("foo").tag("bar", "").tag("qux", "quux").templateFor("bar", "qux");
        metricNameTemplate.build(null, null);
    }
}

