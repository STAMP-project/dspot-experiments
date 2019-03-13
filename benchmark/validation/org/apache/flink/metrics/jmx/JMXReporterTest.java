/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.metrics.jmx;


import ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.reporter.MetricReporter;
import org.apache.flink.metrics.util.TestHistogram;
import org.apache.flink.metrics.util.TestMeter;
import org.apache.flink.runtime.metrics.MetricRegistryConfiguration;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.groups.TaskManagerMetricGroup;
import org.apache.flink.runtime.metrics.util.TestReporter;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the JMXReporter.
 */
public class JMXReporterTest extends TestLogger {
    @Test
    public void testReplaceInvalidChars() {
        Assert.assertEquals("", JMXReporter.replaceInvalidChars(""));
        Assert.assertEquals("abc", JMXReporter.replaceInvalidChars("abc"));
        Assert.assertEquals("abc", JMXReporter.replaceInvalidChars("abc\""));
        Assert.assertEquals("abc", JMXReporter.replaceInvalidChars("\"abc"));
        Assert.assertEquals("abc", JMXReporter.replaceInvalidChars("\"abc\""));
        Assert.assertEquals("abc", JMXReporter.replaceInvalidChars("\"a\"b\"c\""));
        Assert.assertEquals("", JMXReporter.replaceInvalidChars("\"\"\"\""));
        Assert.assertEquals("____", JMXReporter.replaceInvalidChars("    "));
        Assert.assertEquals("ab_-(c)-", JMXReporter.replaceInvalidChars("\"ab ;(c)\'"));
        Assert.assertEquals("a_b_c", JMXReporter.replaceInvalidChars("a b c"));
        Assert.assertEquals("a_b_c_", JMXReporter.replaceInvalidChars("a b c "));
        Assert.assertEquals("a-b-c-", JMXReporter.replaceInvalidChars("a;b'c*"));
        Assert.assertEquals("a------b------c", JMXReporter.replaceInvalidChars("a,=;:?'b,=;:?'c"));
    }

    /**
     * Verifies that the JMXReporter properly generates the JMX table.
     */
    @Test
    public void testGenerateTable() {
        Map<String, String> vars = new HashMap<>();
        vars.put("key0", "value0");
        vars.put("key1", "value1");
        vars.put("\"key2,=;:?\'", "\"value2 (test),=;:?\'");
        Hashtable<String, String> jmxTable = JMXReporter.generateJmxTable(vars);
        Assert.assertEquals("value0", jmxTable.get("key0"));
        Assert.assertEquals("value1", jmxTable.get("key1"));
        Assert.assertEquals("value2_(test)------", jmxTable.get("key2------"));
    }

    /**
     * Verifies that multiple JMXReporters can be started on the same machine and register metrics at the MBeanServer.
     *
     * @throws Exception
     * 		if the attribute/mbean could not be found or the test is broken
     */
    @Test
    public void testPortConflictHandling() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), JMXReporter.class.getName());
        cfg.setString(((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.port"), "9020-9035");
        cfg.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), JMXReporter.class.getName());
        cfg.setString(((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.port"), "9020-9035");
        MetricRegistryImpl reg = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        TaskManagerMetricGroup mg = new TaskManagerMetricGroup(reg, "host", "tm");
        List<MetricReporter> reporters = reg.getReporters();
        Assert.assertTrue(((reporters.size()) == 2));
        MetricReporter rep1 = reporters.get(0);
        MetricReporter rep2 = reporters.get(1);
        Gauge<Integer> g1 = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 1;
            }
        };
        Gauge<Integer> g2 = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 2;
            }
        };
        rep1.notifyOfAddedMetric(g1, "rep1", new org.apache.flink.runtime.metrics.groups.FrontMetricGroup(0, new TaskManagerMetricGroup(reg, "host", "tm")));
        rep2.notifyOfAddedMetric(g2, "rep2", new org.apache.flink.runtime.metrics.groups.FrontMetricGroup(0, new TaskManagerMetricGroup(reg, "host", "tm")));
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName1 = new ObjectName(((JMXReporter.JMX_DOMAIN_PREFIX) + "taskmanager.rep1"), JMXReporter.generateJmxTable(mg.getAllVariables()));
        ObjectName objectName2 = new ObjectName(((JMXReporter.JMX_DOMAIN_PREFIX) + "taskmanager.rep2"), JMXReporter.generateJmxTable(mg.getAllVariables()));
        Assert.assertEquals(1, mBeanServer.getAttribute(objectName1, "Value"));
        Assert.assertEquals(2, mBeanServer.getAttribute(objectName2, "Value"));
        rep1.notifyOfRemovedMetric(g1, "rep1", null);
        rep1.notifyOfRemovedMetric(g2, "rep2", null);
        mg.close();
        reg.shutdown().get();
    }

    /**
     * Verifies that we can connect to multiple JMXReporters running on the same machine.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJMXAvailability() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString(METRICS_REPORTER_CLASS_SUFFIX, TestReporter.class.getName());
        cfg.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), JMXReporter.class.getName());
        cfg.setString(((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.port"), "9040-9055");
        cfg.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), JMXReporter.class.getName());
        cfg.setString(((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.port"), "9040-9055");
        MetricRegistryImpl reg = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        TaskManagerMetricGroup mg = new TaskManagerMetricGroup(reg, "host", "tm");
        List<MetricReporter> reporters = reg.getReporters();
        Assert.assertTrue(((reporters.size()) == 2));
        MetricReporter rep1 = reporters.get(0);
        MetricReporter rep2 = reporters.get(1);
        Gauge<Integer> g1 = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 1;
            }
        };
        Gauge<Integer> g2 = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 2;
            }
        };
        rep1.notifyOfAddedMetric(g1, "rep1", new org.apache.flink.runtime.metrics.groups.FrontMetricGroup(0, new TaskManagerMetricGroup(reg, "host", "tm")));
        rep2.notifyOfAddedMetric(g2, "rep2", new org.apache.flink.runtime.metrics.groups.FrontMetricGroup(1, new TaskManagerMetricGroup(reg, "host", "tm")));
        ObjectName objectName1 = new ObjectName(((JMXReporter.JMX_DOMAIN_PREFIX) + "taskmanager.rep1"), JMXReporter.generateJmxTable(mg.getAllVariables()));
        ObjectName objectName2 = new ObjectName(((JMXReporter.JMX_DOMAIN_PREFIX) + "taskmanager.rep2"), JMXReporter.generateJmxTable(mg.getAllVariables()));
        JMXServiceURL url1 = new JMXServiceURL((((("service:jmx:rmi://localhost:" + (getPort())) + "/jndi/rmi://localhost:") + (getPort())) + "/jmxrmi"));
        JMXConnector jmxCon1 = JMXConnectorFactory.connect(url1);
        MBeanServerConnection mCon1 = jmxCon1.getMBeanServerConnection();
        Assert.assertEquals(1, mCon1.getAttribute(objectName1, "Value"));
        Assert.assertEquals(2, mCon1.getAttribute(objectName2, "Value"));
        jmxCon1.close();
        JMXServiceURL url2 = new JMXServiceURL((((("service:jmx:rmi://localhost:" + (getPort())) + "/jndi/rmi://localhost:") + (getPort())) + "/jmxrmi"));
        JMXConnector jmxCon2 = JMXConnectorFactory.connect(url2);
        MBeanServerConnection mCon2 = jmxCon2.getMBeanServerConnection();
        Assert.assertEquals(1, mCon2.getAttribute(objectName1, "Value"));
        Assert.assertEquals(2, mCon2.getAttribute(objectName2, "Value"));
        rep1.notifyOfRemovedMetric(g1, "rep1", null);
        rep1.notifyOfRemovedMetric(g2, "rep2", null);
        jmxCon2.close();
        rep1.close();
        rep2.close();
        mg.close();
        reg.shutdown().get();
    }

    /**
     * Tests that histograms are properly reported via the JMXReporter.
     */
    @Test
    public void testHistogramReporting() throws Exception {
        MetricRegistryImpl registry = null;
        String histogramName = "histogram";
        try {
            Configuration config = new Configuration();
            config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "jmx_test.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), JMXReporter.class.getName());
            registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
            TaskManagerMetricGroup metricGroup = new TaskManagerMetricGroup(registry, "localhost", "tmId");
            TestHistogram histogram = new TestHistogram();
            metricGroup.histogram(histogramName, histogram);
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName((((JMXReporter.JMX_DOMAIN_PREFIX) + "taskmanager.") + histogramName), JMXReporter.generateJmxTable(metricGroup.getAllVariables()));
            MBeanInfo info = mBeanServer.getMBeanInfo(objectName);
            MBeanAttributeInfo[] attributeInfos = info.getAttributes();
            Assert.assertEquals(11, attributeInfos.length);
            Assert.assertEquals(histogram.getCount(), mBeanServer.getAttribute(objectName, "Count"));
            Assert.assertEquals(histogram.getStatistics().getMean(), mBeanServer.getAttribute(objectName, "Mean"));
            Assert.assertEquals(histogram.getStatistics().getStdDev(), mBeanServer.getAttribute(objectName, "StdDev"));
            Assert.assertEquals(histogram.getStatistics().getMax(), mBeanServer.getAttribute(objectName, "Max"));
            Assert.assertEquals(histogram.getStatistics().getMin(), mBeanServer.getAttribute(objectName, "Min"));
            Assert.assertEquals(histogram.getStatistics().getQuantile(0.5), mBeanServer.getAttribute(objectName, "Median"));
            Assert.assertEquals(histogram.getStatistics().getQuantile(0.75), mBeanServer.getAttribute(objectName, "75thPercentile"));
            Assert.assertEquals(histogram.getStatistics().getQuantile(0.95), mBeanServer.getAttribute(objectName, "95thPercentile"));
            Assert.assertEquals(histogram.getStatistics().getQuantile(0.98), mBeanServer.getAttribute(objectName, "98thPercentile"));
            Assert.assertEquals(histogram.getStatistics().getQuantile(0.99), mBeanServer.getAttribute(objectName, "99thPercentile"));
            Assert.assertEquals(histogram.getStatistics().getQuantile(0.999), mBeanServer.getAttribute(objectName, "999thPercentile"));
        } finally {
            if (registry != null) {
                registry.shutdown().get();
            }
        }
    }

    /**
     * Tests that meters are properly reported via the JMXReporter.
     */
    @Test
    public void testMeterReporting() throws Exception {
        MetricRegistryImpl registry = null;
        String meterName = "meter";
        try {
            Configuration config = new Configuration();
            config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "jmx_test.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), JMXReporter.class.getName());
            registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
            TaskManagerMetricGroup metricGroup = new TaskManagerMetricGroup(registry, "localhost", "tmId");
            TestMeter meter = new TestMeter();
            metricGroup.meter(meterName, meter);
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName((((JMXReporter.JMX_DOMAIN_PREFIX) + "taskmanager.") + meterName), JMXReporter.generateJmxTable(metricGroup.getAllVariables()));
            MBeanInfo info = mBeanServer.getMBeanInfo(objectName);
            MBeanAttributeInfo[] attributeInfos = info.getAttributes();
            Assert.assertEquals(2, attributeInfos.length);
            Assert.assertEquals(meter.getRate(), mBeanServer.getAttribute(objectName, "Rate"));
            Assert.assertEquals(meter.getCount(), mBeanServer.getAttribute(objectName, "Count"));
        } finally {
            if (registry != null) {
                registry.shutdown().get();
            }
        }
    }
}

