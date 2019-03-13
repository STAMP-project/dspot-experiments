/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.metrics;


import Level.DEBUG;
import Level.INFO;
import MetastoreConf.ConfVars.HIVE_CODAHALE_METRICS_REPORTER_CLASSES;
import MetastoreConf.ConfVars.HIVE_METRICS_REPORTER;
import MetastoreConf.ConfVars.METRICS_JSON_FILE_INTERVAL;
import MetastoreConf.ConfVars.METRICS_JSON_FILE_LOCATION;
import MetastoreConf.ConfVars.METRICS_REPORTERS;
import MetastoreConf.ConfVars.METRICS_SLF4J_LOG_FREQUENCY_MINS;
import MetastoreConf.ConfVars.METRICS_SLF4J_LOG_LEVEL;
import com.codahale.metrics.Counter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MetastoreUnitTest.class)
public class TestMetrics {
    private static final long REPORT_INTERVAL = 1;

    @Test
    public void slf4jReporter() throws Exception {
        Configuration conf = MetastoreConf.newMetastoreConf();
        MetastoreConf.setVar(conf, METRICS_REPORTERS, "slf4j");
        MetastoreConf.setTimeVar(conf, METRICS_SLF4J_LOG_FREQUENCY_MINS, TestMetrics.REPORT_INTERVAL, TimeUnit.SECONDS);
        // 1. Verify the default level (INFO)
        validateSlf4jReporter(conf, INFO);
        // 2. Verify an overridden level (DEBUG)
        MetastoreConf.setVar(conf, METRICS_SLF4J_LOG_LEVEL, "DEBUG");
        validateSlf4jReporter(conf, DEBUG);
    }

    @Test
    public void jsonReporter() throws Exception {
        File jsonReportFile = File.createTempFile("TestMetrics", ".json");
        String jsonFile = jsonReportFile.getAbsolutePath();
        Configuration conf = MetastoreConf.newMetastoreConf();
        MetastoreConf.setVar(conf, METRICS_REPORTERS, "json");
        MetastoreConf.setVar(conf, METRICS_JSON_FILE_LOCATION, jsonFile);
        MetastoreConf.setTimeVar(conf, METRICS_JSON_FILE_INTERVAL, TestMetrics.REPORT_INTERVAL, TimeUnit.SECONDS);
        Metrics.initialize(conf);
        Counter counter = Metrics.getOrCreateCounter("my-counter");
        for (int i = 0; i < 5; i++) {
            counter.inc();
            // Make sure it has a chance to dump it.
            Thread.sleep((((TestMetrics.REPORT_INTERVAL) * 1000) + (((TestMetrics.REPORT_INTERVAL) * 1000) / 2)));
            String json = new String(TestMetrics.MetricsTestUtils.getFileData(jsonFile, 200, 10));
            TestMetrics.MetricsTestUtils.verifyMetricsJson(json, TestMetrics.MetricsTestUtils.COUNTER, "my-counter", (i + 1));
        }
    }

    @Test
    public void allReporters() throws Exception {
        String jsonFile = ((System.getProperty("java.io.tmpdir")) + (System.getProperty("file.separator"))) + "TestMetricsOutput.json";
        Configuration conf = MetastoreConf.newMetastoreConf();
        MetastoreConf.setVar(conf, METRICS_REPORTERS, "json,jmx,console,hadoop");
        MetastoreConf.setVar(conf, METRICS_JSON_FILE_LOCATION, jsonFile);
        Metrics.initialize(conf);
        Assert.assertEquals(4, Metrics.getReporters().size());
    }

    @Test
    public void allReportersHiveConfig() throws Exception {
        String jsonFile = ((System.getProperty("java.io.tmpdir")) + (System.getProperty("file.separator"))) + "TestMetricsOutput.json";
        Configuration conf = MetastoreConf.newMetastoreConf();
        conf.set(HIVE_CODAHALE_METRICS_REPORTER_CLASSES.getHiveName(), ("org.apache.hadoop.hive.common.metrics.metrics2.JsonFileMetricsReporter," + (("org.apache.hadoop.hive.common.metrics.metrics2.JmxMetricsReporter," + "org.apache.hadoop.hive.common.metrics.metrics2.ConsoleMetricsReporter,") + "org.apache.hadoop.hive.common.metrics.metrics2.Metrics2Reporter")));
        MetastoreConf.setVar(conf, METRICS_JSON_FILE_LOCATION, jsonFile);
        Metrics.initialize(conf);
        Assert.assertEquals(4, Metrics.getReporters().size());
    }

    @Test
    public void allReportersOldHiveConfig() throws Exception {
        String jsonFile = ((System.getProperty("java.io.tmpdir")) + (System.getProperty("file.separator"))) + "TestMetricsOutput.json";
        Configuration conf = MetastoreConf.newMetastoreConf();
        conf.set(HIVE_METRICS_REPORTER.getHiveName(), "JSON_FILE,JMX,CONSOLE,HADOOP2");
        MetastoreConf.setVar(conf, METRICS_JSON_FILE_LOCATION, jsonFile);
        Metrics.initialize(conf);
        Assert.assertEquals(4, Metrics.getReporters().size());
    }

    @Test
    public void defaults() throws Exception {
        String jsonFile = ((System.getProperty("java.io.tmpdir")) + (System.getProperty("file.separator"))) + "TestMetricsOutput.json";
        Configuration conf = MetastoreConf.newMetastoreConf();
        MetastoreConf.setVar(conf, METRICS_JSON_FILE_LOCATION, jsonFile);
        Metrics.initialize(conf);
        Assert.assertEquals(2, Metrics.getReporters().size());
    }

    // Stolen from Hive's MetricsTestUtils.  Probably should break it out into it's own class.
    private static class MetricsTestUtils {
        static final TestMetrics.MetricsTestUtils.MetricsCategory COUNTER = new TestMetrics.MetricsTestUtils.MetricsCategory("counters", "count");

        static final TestMetrics.MetricsTestUtils.MetricsCategory TIMER = new TestMetrics.MetricsTestUtils.MetricsCategory("timers", "count");

        static final TestMetrics.MetricsTestUtils.MetricsCategory GAUGE = new TestMetrics.MetricsTestUtils.MetricsCategory("gauges", "value");

        static final TestMetrics.MetricsTestUtils.MetricsCategory METER = new TestMetrics.MetricsTestUtils.MetricsCategory("meters", "count");

        static class MetricsCategory {
            String category;

            String metricsHandle;

            MetricsCategory(String category, String metricsHandle) {
                this.category = category;
                this.metricsHandle = metricsHandle;
            }
        }

        static void verifyMetricsJson(String json, TestMetrics.MetricsTestUtils.MetricsCategory category, String metricsName, Object expectedValue) throws Exception {
            JsonNode jsonNode = TestMetrics.MetricsTestUtils.getJsonNode(json, category, metricsName);
            Assert.assertEquals(expectedValue.toString(), jsonNode.asText());
        }

        static JsonNode getJsonNode(String json, TestMetrics.MetricsTestUtils.MetricsCategory category, String metricsName) throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode categoryNode = rootNode.path(category.category);
            JsonNode metricsNode = categoryNode.path(metricsName);
            return metricsNode.path(category.metricsHandle);
        }

        static byte[] getFileData(String path, int timeoutInterval, int tries) throws Exception {
            File file = new File(path);
            do {
                Thread.sleep(timeoutInterval);
                tries--;
            } while ((tries > 0) && (!(file.exists())) );
            return Files.readAllBytes(Paths.get(path));
        }
    }
}

