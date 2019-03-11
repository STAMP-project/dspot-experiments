package org.stagemonitor.core;


import com.codahale.metrics.Counter;
import javax.management.ObjectInstance;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;
import org.stagemonitor.core.util.MBeanUtils;


public class JmxReportingTest {
    private Metric2Registry registry;

    @Test
    public void testReportToJmx() throws Exception {
        final Counter counter = registry.counter(MetricName.name("test_counter").tag("foo", "bar").build());
        final ObjectInstance objectInstance = MBeanUtils.queryMBeans("metrics:name=test_counter.bar").iterator().next();
        Assert.assertNotNull(objectInstance);
        Assert.assertEquals(0L, MBeanUtils.getValueFromMBean(objectInstance, "Count"));
        counter.inc();
        Assert.assertEquals(1L, MBeanUtils.getValueFromMBean(objectInstance, "Count"));
    }
}

