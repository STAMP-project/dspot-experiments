/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.status.monitoring;


import BaseSystemInfoCollector.DEFAULT_VALUE;
import java.util.List;
import java.util.Map;
import org.geoserver.status.monitoring.collector.MetricValue;
import org.geoserver.status.monitoring.collector.Metrics;
import org.geoserver.status.monitoring.collector.SystemInfoCollector;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SystemInfoCollectorTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    private static ClassPathXmlApplicationContext context;

    @Test
    public void testMetricCollector() throws Exception {
        Map<String, SystemInfoCollector> collectors = SystemInfoCollectorTest.context.getBeansOfType(SystemInfoCollector.class);
        Assert.assertEquals(1, collectors.size());
        SystemInfoCollector systemInfoCollector = collectors.values().iterator().next();
        // SystemInfoCollector systemInfoCollector = context.getBean(SystemInfoCollector.class);
        Metrics collected = systemInfoCollector.retrieveAllSystemInfo();
        List<MetricValue> metrics = collected.getMetrics();
        for (MetricValue m : metrics) {
            if (m.getAvailable()) {
                System.out.println((((((m.getName()) + " IS available -> ") + (m.getValue())) + " ") + (m.getUnit())));
            } else {
                System.err.println(((m.getName()) + " IS NOT available"));
            }
            collector.checkThat((("Metric for " + (m.getName())) + " available but value is not retrived"), (((m.getAvailable()) && (!(m.getValue().equals(DEFAULT_VALUE)))) || ((!(m.getAvailable())) && (m.getValue().equals(DEFAULT_VALUE)))), CoreMatchers.equalTo(true));
        }
    }
}

