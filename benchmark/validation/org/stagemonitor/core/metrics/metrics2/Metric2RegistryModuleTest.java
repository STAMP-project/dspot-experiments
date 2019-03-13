package org.stagemonitor.core.metrics.metrics2;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class Metric2RegistryModuleTest {
    private ObjectMapper mapper;

    private Metric2Registry registry;

    @Test
    public void testCounter() throws Exception {
        registry.counter(MetricName.name("foo").tag("bar", "baz").build()).inc();
        registry.counter(MetricName.name("qux").tag("quux", "foo").build()).inc();
        Assert.assertEquals(("[" + (("{\"name\":\"foo\",\"tags\":{\"bar\":\"baz\"},\"values\":{\"count\":1}}," + "{\"name\":\"qux\",\"tags\":{\"quux\":\"foo\"},\"values\":{\"count\":1}}") + "]")), mapper.writeValueAsString(registry));
    }

    @Test
    public void testGauge() throws Exception {
        registry.register(MetricName.name("foo").tag("bar", "baz").build(), new com.codahale.metrics.Gauge<Double>() {
            @Override
            public Double getValue() {
                return 1.1;
            }
        });
        Assert.assertEquals("[{\"name\":\"foo\",\"tags\":{\"bar\":\"baz\"},\"values\":{\"value\":1.1}}]", mapper.writeValueAsString(registry));
    }
}

