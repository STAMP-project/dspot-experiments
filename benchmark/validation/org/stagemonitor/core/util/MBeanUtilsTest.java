package org.stagemonitor.core.util;


import com.codahale.metrics.Gauge;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class MBeanUtilsTest {
    private Metric2Registry metricRegistry = new Metric2Registry();

    @Test
    public void testRegisterMBean() throws Exception {
        MBeanUtils.registerMBean(MBeanUtils.queryMBean("java.lang:type=ClassLoading"), "LoadedClassCount", MetricName.name("jvm_class_count").build(), metricRegistry);
        final Gauge classLoadingGauge = metricRegistry.getGauges().get(MetricName.name("jvm_class_count").build());
        Assert.assertNotNull(classLoadingGauge);
        Assert.assertTrue((((Integer) (classLoadingGauge.getValue())) > 1));
    }
}

