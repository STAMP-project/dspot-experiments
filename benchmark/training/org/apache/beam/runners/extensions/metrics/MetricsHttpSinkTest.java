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
package org.apache.beam.runners.extensions.metrics;


import com.sun.net.httpserver.HttpServer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.metrics.MetricsOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for MetricsHttpSink.
 */
public class MetricsHttpSinkTest {
    private static int port;

    private static List<String> messages = new ArrayList<>();

    private static HttpServer httpServer;

    private static CountDownLatch countDownLatch;

    @Test
    public void testWriteMetricsWithCommittedSupported() throws Exception {
        MetricQueryResults metricQueryResults = new CustomMetricQueryResults(true);
        MetricsOptions pipelineOptions = PipelineOptionsFactory.create().as(MetricsOptions.class);
        pipelineOptions.setMetricsHttpSinkUrl(String.format("http://localhost:%s", MetricsHttpSinkTest.port));
        MetricsHttpSink metricsHttpSink = new MetricsHttpSink(pipelineOptions);
        MetricsHttpSinkTest.countDownLatch = new CountDownLatch(1);
        metricsHttpSink.writeMetrics(metricQueryResults);
        MetricsHttpSinkTest.countDownLatch.await();
        String expected = "{\"counters\":[{\"attempted\":20,\"committed\":10,\"name\":{\"name\":\"n1\"," + (((((("\"namespace\":\"ns1\"},\"step\":\"s1\"}],\"distributions\":[{\"attempted\":" + "{\"count\":4,\"max\":9,\"mean\":6.25,\"min\":3,\"sum\":25},\"committed\":") + "{\"count\":2,\"max\":8,\"mean\":5.0,\"min\":5,\"sum\":10},\"name\":{\"name\":\"n2\",") + "\"namespace\":\"ns1\"},\"step\":\"s2\"}],\"gauges\":[{\"attempted\":{\"timestamp\":") + "\"1970-01-05T00:04:22.800Z\",\"value\":120},\"committed\":{\"timestamp\":") + "\"1970-01-05T00:04:22.800Z\",\"value\":100},\"name\":{\"name\":\"n3\",\"namespace\":") + "\"ns1\"},\"step\":\"s3\"}]}");
        Assert.assertEquals("Wrong number of messages sent to HTTP server", 1, MetricsHttpSinkTest.messages.size());
        Assert.assertEquals("Wrong messages sent to HTTP server", expected, MetricsHttpSinkTest.messages.get(0));
    }

    @Test
    public void testWriteMetricsWithCommittedUnSupported() throws Exception {
        MetricQueryResults metricQueryResults = new CustomMetricQueryResults(false);
        MetricsOptions pipelineOptions = PipelineOptionsFactory.create().as(MetricsOptions.class);
        pipelineOptions.setMetricsHttpSinkUrl(String.format("http://localhost:%s", MetricsHttpSinkTest.port));
        MetricsHttpSink metricsHttpSink = new MetricsHttpSink(pipelineOptions);
        MetricsHttpSinkTest.countDownLatch = new CountDownLatch(1);
        metricsHttpSink.writeMetrics(metricQueryResults);
        MetricsHttpSinkTest.countDownLatch.await();
        String expected = "{\"counters\":[{\"attempted\":20,\"name\":{\"name\":\"n1\"," + (((("\"namespace\":\"ns1\"},\"step\":\"s1\"}],\"distributions\":[{\"attempted\":" + "{\"count\":4,\"max\":9,\"mean\":6.25,\"min\":3,\"sum\":25},\"name\":{\"name\":\"n2\"") + ",\"namespace\":\"ns1\"},\"step\":\"s2\"}],\"gauges\":[{\"attempted\":{\"timestamp\":") + "\"1970-01-05T00:04:22.800Z\",\"value\":120},\"name\":{\"name\":\"n3\",\"namespace\":") + "\"ns1\"},\"step\":\"s3\"}]}");
        Assert.assertEquals("Wrong number of messages sent to HTTP server", 1, MetricsHttpSinkTest.messages.size());
        Assert.assertEquals("Wrong messages sent to HTTP server", expected, MetricsHttpSinkTest.messages.get(0));
    }
}

