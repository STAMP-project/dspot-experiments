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
package org.apache.flink.runtime.metrics;


import MetricOptions.SCOPE_DELIMITER;
import MetricOptions.SCOPE_NAMING_OPERATOR;
import MetricOptions.SCOPE_NAMING_TASK;
import MetricOptions.SCOPE_NAMING_TM;
import MetricOptions.SCOPE_NAMING_TM_JOB;
import akka.actor.ActorNotFound;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Metric;
import org.apache.flink.metrics.MetricConfig;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.metrics.reporter.MetricReporter;
import org.apache.flink.metrics.reporter.Scheduled;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.metrics.groups.MetricGroupTest;
import org.apache.flink.runtime.metrics.groups.TaskManagerMetricGroup;
import org.apache.flink.runtime.metrics.scope.ScopeFormats;
import org.apache.flink.runtime.metrics.util.TestReporter;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.FiniteDuration;


/**
 * Tests for the {@link MetricRegistryImpl}.
 */
public class MetricRegistryImplTest extends TestLogger {
    private static final char GLOBAL_DEFAULT_DELIMITER = '.';

    @Test
    public void testIsShutdown() throws Exception {
        MetricRegistryImpl metricRegistry = new MetricRegistryImpl(MetricRegistryConfiguration.defaultMetricRegistryConfiguration());
        Assert.assertFalse(metricRegistry.isShutdown());
        metricRegistry.shutdown().get();
        Assert.assertTrue(metricRegistry.isShutdown());
    }

    /**
     * Reporter that exposes whether open() was called.
     */
    protected static class TestReporter1 extends TestReporter {
        public static boolean wasOpened = false;

        @Override
        public void open(MetricConfig config) {
            MetricRegistryImplTest.TestReporter1.wasOpened = true;
        }
    }

    /**
     * Verifies that multiple reporters are instantiated correctly.
     */
    @Test
    public void testMultipleReporterInstantiation() throws Exception {
        Configuration config = new Configuration();
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter11.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter12.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test3.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter13.class.getName());
        MetricRegistryImpl metricRegistry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        Assert.assertTrue(((metricRegistry.getReporters().size()) == 3));
        Assert.assertTrue(MetricRegistryImplTest.TestReporter11.wasOpened);
        Assert.assertTrue(MetricRegistryImplTest.TestReporter12.wasOpened);
        Assert.assertTrue(MetricRegistryImplTest.TestReporter13.wasOpened);
        metricRegistry.shutdown().get();
    }

    /**
     * Reporter that exposes whether open() was called.
     */
    protected static class TestReporter11 extends TestReporter {
        public static boolean wasOpened = false;

        @Override
        public void open(MetricConfig config) {
            MetricRegistryImplTest.TestReporter11.wasOpened = true;
        }
    }

    /**
     * Reporter that exposes whether open() was called.
     */
    protected static class TestReporter12 extends TestReporter {
        public static boolean wasOpened = false;

        @Override
        public void open(MetricConfig config) {
            MetricRegistryImplTest.TestReporter12.wasOpened = true;
        }
    }

    /**
     * Reporter that exposes whether open() was called.
     */
    protected static class TestReporter13 extends TestReporter {
        public static boolean wasOpened = false;

        @Override
        public void open(MetricConfig config) {
            MetricRegistryImplTest.TestReporter13.wasOpened = true;
        }
    }

    /**
     * Reporter that exposes the {@link MetricConfig} it was given.
     */
    protected static class TestReporter2 extends TestReporter {
        static MetricConfig mc;

        @Override
        public void open(MetricConfig config) {
            MetricRegistryImplTest.TestReporter2.mc = config;
        }
    }

    /**
     * Verifies that reporters implementing the Scheduled interface are regularly called to report the metrics.
     */
    @Test
    public void testReporterScheduling() throws Exception {
        Configuration config = new Configuration();
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter3.class.getName());
        config.setString(((ConfigConstants.METRICS_REPORTER_PREFIX) + "test.arg1"), "hello");
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test.") + (ConfigConstants.METRICS_REPORTER_INTERVAL_SUFFIX)), "50 MILLISECONDS");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        long start = System.currentTimeMillis();
        // only start counting from now on
        MetricRegistryImplTest.TestReporter3.reportCount = 0;
        for (int x = 0; x < 10; x++) {
            Thread.sleep(100);
            int reportCount = MetricRegistryImplTest.TestReporter3.reportCount;
            long curT = System.currentTimeMillis();
            /**
             * Within a given time-frame T only T/500 reports may be triggered due to the interval between reports.
             * This value however does not not take the first triggered report into account (=> +1).
             * Furthermore we have to account for the mis-alignment between reports being triggered and our time
             * measurement (=> +1); for T=200 a total of 4-6 reports may have been
             * triggered depending on whether the end of the interval for the first reports ends before
             * or after T=50.
             */
            long maxAllowedReports = ((curT - start) / 50) + 2;
            Assert.assertTrue("Too many reports were triggered.", (maxAllowedReports >= reportCount));
        }
        Assert.assertTrue("No report was triggered.", ((MetricRegistryImplTest.TestReporter3.reportCount) > 0));
        registry.shutdown().get();
    }

    /**
     * Reporter that exposes how often report() was called.
     */
    protected static class TestReporter3 extends TestReporter implements Scheduled {
        public static int reportCount = 0;

        @Override
        public void report() {
            (MetricRegistryImplTest.TestReporter3.reportCount)++;
        }
    }

    /**
     * Verifies that reporters are notified of added/removed metrics.
     */
    @Test
    public void testReporterNotifications() throws Exception {
        Configuration config = new Configuration();
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter6.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter7.class.getName());
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        TaskManagerMetricGroup root = new TaskManagerMetricGroup(registry, "host", "id");
        root.counter("rootCounter");
        Assert.assertTrue(((MetricRegistryImplTest.TestReporter6.addedMetric) instanceof Counter));
        Assert.assertEquals("rootCounter", MetricRegistryImplTest.TestReporter6.addedMetricName);
        Assert.assertTrue(((MetricRegistryImplTest.TestReporter7.addedMetric) instanceof Counter));
        Assert.assertEquals("rootCounter", MetricRegistryImplTest.TestReporter7.addedMetricName);
        root.close();
        Assert.assertTrue(((MetricRegistryImplTest.TestReporter6.removedMetric) instanceof Counter));
        Assert.assertEquals("rootCounter", MetricRegistryImplTest.TestReporter6.removedMetricName);
        Assert.assertTrue(((MetricRegistryImplTest.TestReporter7.removedMetric) instanceof Counter));
        Assert.assertEquals("rootCounter", MetricRegistryImplTest.TestReporter7.removedMetricName);
        registry.shutdown().get();
    }

    /**
     * Reporter that exposes the name and metric instance of the last metric that was added or removed.
     */
    protected static class TestReporter6 extends TestReporter {
        static Metric addedMetric;

        static String addedMetricName;

        static Metric removedMetric;

        static String removedMetricName;

        @Override
        public void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group) {
            MetricRegistryImplTest.TestReporter6.addedMetric = metric;
            MetricRegistryImplTest.TestReporter6.addedMetricName = metricName;
        }

        @Override
        public void notifyOfRemovedMetric(Metric metric, String metricName, MetricGroup group) {
            MetricRegistryImplTest.TestReporter6.removedMetric = metric;
            MetricRegistryImplTest.TestReporter6.removedMetricName = metricName;
        }
    }

    /**
     * Reporter that exposes the name and metric instance of the last metric that was added or removed.
     */
    protected static class TestReporter7 extends TestReporter {
        static Metric addedMetric;

        static String addedMetricName;

        static Metric removedMetric;

        static String removedMetricName;

        @Override
        public void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group) {
            MetricRegistryImplTest.TestReporter7.addedMetric = metric;
            MetricRegistryImplTest.TestReporter7.addedMetricName = metricName;
        }

        @Override
        public void notifyOfRemovedMetric(Metric metric, String metricName, MetricGroup group) {
            MetricRegistryImplTest.TestReporter7.removedMetric = metric;
            MetricRegistryImplTest.TestReporter7.removedMetricName = metricName;
        }
    }

    /**
     * Verifies that the scope configuration is properly extracted.
     */
    @Test
    public void testScopeConfig() {
        Configuration config = new Configuration();
        config.setString(SCOPE_NAMING_TM, "A");
        config.setString(SCOPE_NAMING_TM_JOB, "B");
        config.setString(SCOPE_NAMING_TASK, "C");
        config.setString(SCOPE_NAMING_OPERATOR, "D");
        ScopeFormats scopeConfig = ScopeFormats.fromConfig(config);
        Assert.assertEquals("A", scopeConfig.getTaskManagerFormat().format());
        Assert.assertEquals("B", scopeConfig.getTaskManagerJobFormat().format());
        Assert.assertEquals("C", scopeConfig.getTaskFormat().format());
        Assert.assertEquals("D", scopeConfig.getOperatorFormat().format());
    }

    @Test
    public void testConfigurableDelimiter() throws Exception {
        Configuration config = new Configuration();
        config.setString(SCOPE_DELIMITER, "_");
        config.setString(SCOPE_NAMING_TM, "A.B.C.D.E");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "host", "id");
        Assert.assertEquals("A_B_C_D_E_name", tmGroup.getMetricIdentifier("name"));
        registry.shutdown().get();
    }

    @Test
    public void testConfigurableDelimiterForReporters() throws Exception {
        Configuration config = new Configuration();
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_SCOPE_DELIMITER)), "_");
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), TestReporter.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_SCOPE_DELIMITER)), "-");
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), TestReporter.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test3.") + (ConfigConstants.METRICS_REPORTER_SCOPE_DELIMITER)), "AA");
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test3.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), TestReporter.class.getName());
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        Assert.assertEquals(MetricRegistryImplTest.GLOBAL_DEFAULT_DELIMITER, registry.getDelimiter());
        Assert.assertEquals('_', registry.getDelimiter(0));
        Assert.assertEquals('-', registry.getDelimiter(1));
        Assert.assertEquals(MetricRegistryImplTest.GLOBAL_DEFAULT_DELIMITER, registry.getDelimiter(2));
        Assert.assertEquals(MetricRegistryImplTest.GLOBAL_DEFAULT_DELIMITER, registry.getDelimiter(3));
        Assert.assertEquals(MetricRegistryImplTest.GLOBAL_DEFAULT_DELIMITER, registry.getDelimiter((-1)));
        registry.shutdown().get();
    }

    @Test
    public void testConfigurableDelimiterForReportersInGroup() throws Exception {
        Configuration config = new Configuration();
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_SCOPE_DELIMITER)), "_");
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter8.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_SCOPE_DELIMITER)), "-");
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter8.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test3.") + (ConfigConstants.METRICS_REPORTER_SCOPE_DELIMITER)), "AA");
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test3.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter8.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test4.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter8.class.getName());
        config.setString(SCOPE_NAMING_TM, "A.B");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        List<MetricReporter> reporters = registry.getReporters();
        ((MetricRegistryImplTest.TestReporter8) (reporters.get(0))).expectedDelimiter = '_';// test1  reporter

        ((MetricRegistryImplTest.TestReporter8) (reporters.get(1))).expectedDelimiter = '-';// test2 reporter

        ((MetricRegistryImplTest.TestReporter8) (reporters.get(2))).expectedDelimiter = MetricRegistryImplTest.GLOBAL_DEFAULT_DELIMITER;// test3 reporter, because 'AA' - not correct delimiter

        ((MetricRegistryImplTest.TestReporter8) (reporters.get(3))).expectedDelimiter = MetricRegistryImplTest.GLOBAL_DEFAULT_DELIMITER;// for test4 reporter use global delimiter

        TaskManagerMetricGroup group = new TaskManagerMetricGroup(registry, "host", "id");
        group.counter("C");
        group.close();
        registry.shutdown().get();
        Assert.assertEquals(4, MetricRegistryImplTest.TestReporter8.numCorrectDelimitersForRegister);
        Assert.assertEquals(4, MetricRegistryImplTest.TestReporter8.numCorrectDelimitersForUnregister);
    }

    /**
     * Tests that the query actor will be stopped when the MetricRegistry is shut down.
     */
    @Test
    public void testQueryActorShutdown() throws Exception {
        final FiniteDuration timeout = new FiniteDuration(10L, TimeUnit.SECONDS);
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.defaultMetricRegistryConfiguration());
        final ActorSystem actorSystem = AkkaUtils.createDefaultActorSystem();
        registry.startQueryService(actorSystem, null);
        ActorRef queryServiceActor = registry.getQueryService();
        registry.shutdown().get();
        try {
            Await.result(actorSystem.actorSelection(queryServiceActor.path()).resolveOne(timeout), timeout);
            Assert.fail("The query actor should be terminated resulting in a ActorNotFound exception.");
        } catch (ActorNotFound e) {
            // we expect the query actor to be shut down
        }
    }

    /**
     * Reporter that verifies that the configured delimiter is applied correctly when generating the metric identifier.
     */
    public static class TestReporter8 extends TestReporter {
        char expectedDelimiter;

        public static int numCorrectDelimitersForRegister = 0;

        public static int numCorrectDelimitersForUnregister = 0;

        @Override
        public void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group) {
            String expectedMetric = ((("A" + (expectedDelimiter)) + "B") + (expectedDelimiter)) + "C";
            Assert.assertEquals(expectedMetric, group.getMetricIdentifier(metricName, this));
            Assert.assertEquals(expectedMetric, group.getMetricIdentifier(metricName));
            (MetricRegistryImplTest.TestReporter8.numCorrectDelimitersForRegister)++;
        }

        @Override
        public void notifyOfRemovedMetric(Metric metric, String metricName, MetricGroup group) {
            String expectedMetric = ((("A" + (expectedDelimiter)) + "B") + (expectedDelimiter)) + "C";
            Assert.assertEquals(expectedMetric, group.getMetricIdentifier(metricName, this));
            Assert.assertEquals(expectedMetric, group.getMetricIdentifier(metricName));
            (MetricRegistryImplTest.TestReporter8.numCorrectDelimitersForUnregister)++;
        }
    }

    @Test
    public void testExceptionIsolation() throws Exception {
        Configuration config = new Configuration();
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test1.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.FailingReporter.class.getName());
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test2.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), MetricRegistryImplTest.TestReporter7.class.getName());
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        Counter metric = new SimpleCounter();
        registry.register(metric, "counter", new MetricGroupTest.DummyAbstractMetricGroup(registry));
        Assert.assertEquals(metric, MetricRegistryImplTest.TestReporter7.addedMetric);
        Assert.assertEquals("counter", MetricRegistryImplTest.TestReporter7.addedMetricName);
        registry.unregister(metric, "counter", new MetricGroupTest.DummyAbstractMetricGroup(registry));
        Assert.assertEquals(metric, MetricRegistryImplTest.TestReporter7.removedMetric);
        Assert.assertEquals("counter", MetricRegistryImplTest.TestReporter7.removedMetricName);
        registry.shutdown().get();
    }

    /**
     * Reporter that throws an exception when it is notified of an added or removed metric.
     */
    protected static class FailingReporter extends TestReporter {
        @Override
        public void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group) {
            throw new RuntimeException();
        }

        @Override
        public void notifyOfRemovedMetric(Metric metric, String metricName, MetricGroup group) {
            throw new RuntimeException();
        }
    }
}

