/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.metrics;


import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.stats.Avg;
import org.apache.kafka.common.metrics.stats.Total;
import org.junit.Assert;
import org.junit.Test;


public class JmxReporterTest {
    @Test
    public void testJmxRegistration() throws Exception {
        Metrics metrics = new Metrics();
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            JmxReporter reporter = new JmxReporter();
            metrics.addReporter(reporter);
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=grp1")));
            Sensor sensor = metrics.sensor("kafka.requests");
            sensor.add(metrics.metricName("pack.bean1.avg", "grp1"), new Avg());
            sensor.add(metrics.metricName("pack.bean2.total", "grp2"), new Total());
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=grp1")));
            Assert.assertEquals(Double.NaN, server.getAttribute(new ObjectName(":type=grp1"), "pack.bean1.avg"));
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=grp2")));
            Assert.assertEquals(0.0, server.getAttribute(new ObjectName(":type=grp2"), "pack.bean2.total"));
            MetricName metricName = metrics.metricName("pack.bean1.avg", "grp1");
            String mBeanName = JmxReporter.getMBeanName("", metricName);
            Assert.assertTrue(reporter.containsMbean(mBeanName));
            metrics.removeMetric(metricName);
            Assert.assertFalse(reporter.containsMbean(mBeanName));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=grp1")));
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=grp2")));
            Assert.assertEquals(0.0, server.getAttribute(new ObjectName(":type=grp2"), "pack.bean2.total"));
            metricName = metrics.metricName("pack.bean2.total", "grp2");
            metrics.removeMetric(metricName);
            Assert.assertFalse(reporter.containsMbean(mBeanName));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=grp1")));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=grp2")));
        } finally {
            metrics.close();
        }
    }

    @Test
    public void testJmxRegistrationSanitization() throws Exception {
        Metrics metrics = new Metrics();
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            metrics.addReporter(new JmxReporter());
            Sensor sensor = metrics.sensor("kafka.requests");
            sensor.add(metrics.metricName("name", "group", "desc", "id", "foo*"), new Total());
            sensor.add(metrics.metricName("name", "group", "desc", "id", "foo+"), new Total());
            sensor.add(metrics.metricName("name", "group", "desc", "id", "foo?"), new Total());
            sensor.add(metrics.metricName("name", "group", "desc", "id", "foo:"), new Total());
            sensor.add(metrics.metricName("name", "group", "desc", "id", "foo%"), new Total());
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=group,id=\"foo\\*\"")));
            Assert.assertEquals(0.0, server.getAttribute(new ObjectName(":type=group,id=\"foo\\*\""), "name"));
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=group,id=\"foo+\"")));
            Assert.assertEquals(0.0, server.getAttribute(new ObjectName(":type=group,id=\"foo+\""), "name"));
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=group,id=\"foo\\?\"")));
            Assert.assertEquals(0.0, server.getAttribute(new ObjectName(":type=group,id=\"foo\\?\""), "name"));
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=group,id=\"foo:\"")));
            Assert.assertEquals(0.0, server.getAttribute(new ObjectName(":type=group,id=\"foo:\""), "name"));
            Assert.assertTrue(server.isRegistered(new ObjectName(":type=group,id=foo%")));
            Assert.assertEquals(0.0, server.getAttribute(new ObjectName(":type=group,id=foo%"), "name"));
            metrics.removeMetric(metrics.metricName("name", "group", "desc", "id", "foo*"));
            metrics.removeMetric(metrics.metricName("name", "group", "desc", "id", "foo+"));
            metrics.removeMetric(metrics.metricName("name", "group", "desc", "id", "foo?"));
            metrics.removeMetric(metrics.metricName("name", "group", "desc", "id", "foo:"));
            metrics.removeMetric(metrics.metricName("name", "group", "desc", "id", "foo%"));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=group,id=\"foo\\*\"")));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=group,id=foo+")));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=group,id=\"foo\\?\"")));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=group,id=\"foo:\"")));
            Assert.assertFalse(server.isRegistered(new ObjectName(":type=group,id=foo%")));
        } finally {
            metrics.close();
        }
    }
}

