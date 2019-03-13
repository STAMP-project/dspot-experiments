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


import java.util.concurrent.CountDownLatch;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.metrics.MetricsOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for MetricsGraphiteSink.
 */
public class MetricsGraphiteSinkTest {
    private static NetworkMockServer graphiteServer;

    private static int port;

    @Test
    public void testWriteMetricsWithCommittedSupported() throws Exception {
        MetricQueryResults metricQueryResults = new CustomMetricQueryResults(true);
        MetricsOptions pipelineOptions = PipelineOptionsFactory.create().as(MetricsOptions.class);
        pipelineOptions.setMetricsGraphitePort(MetricsGraphiteSinkTest.port);
        pipelineOptions.setMetricsGraphiteHost("127.0.0.1");
        MetricsGraphiteSink metricsGraphiteSink = new MetricsGraphiteSink(pipelineOptions);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MetricsGraphiteSinkTest.graphiteServer.setCountDownLatch(countDownLatch);
        metricsGraphiteSink.writeMetrics(metricQueryResults);
        countDownLatch.await();
        String join = String.join("\n", MetricsGraphiteSinkTest.graphiteServer.getMessages());
        String regexpr = "beam.counter.ns1.n1.committed.value 10 [0-9]+\\n" + (((((((((((("beam.counter.ns1.n1.attempted.value 20 [0-9]+\\n" + "beam.gauge.ns1.n3.committed.value 100 [0-9]+\\n") + "beam.gauge.ns1.n3.attempted.value 120 [0-9]+\\n") + "beam.distribution.ns1.n2.committed.min 5 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.min 3 [0-9]+\\n") + "beam.distribution.ns1.n2.committed.max 8 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.max 9 [0-9]+\\n") + "beam.distribution.ns1.n2.committed.count 2 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.count 4 [0-9]+\\n") + "beam.distribution.ns1.n2.committed.sum 10 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.sum 25 [0-9]+\\n") + "beam.distribution.ns1.n2.committed.mean 5.0 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.mean 6.25 [0-9]+");
        Assert.assertTrue(join.matches(regexpr));
    }

    @Test
    public void testWriteMetricsWithCommittedUnSupported() throws Exception {
        MetricQueryResults metricQueryResults = new CustomMetricQueryResults(false);
        MetricsOptions pipelineOptions = PipelineOptionsFactory.create().as(MetricsOptions.class);
        pipelineOptions.setMetricsGraphitePort(MetricsGraphiteSinkTest.port);
        pipelineOptions.setMetricsGraphiteHost("127.0.0.1");
        MetricsGraphiteSink metricsGraphiteSink = new MetricsGraphiteSink(pipelineOptions);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MetricsGraphiteSinkTest.graphiteServer.setCountDownLatch(countDownLatch);
        metricsGraphiteSink.writeMetrics(metricQueryResults);
        countDownLatch.await();
        String join = String.join("\n", MetricsGraphiteSinkTest.graphiteServer.getMessages());
        String regexpr = "beam.counter.ns1.n1.attempted.value 20 [0-9]+\\n" + ((((("beam.gauge.ns1.n3.attempted.value 120 [0-9]+\\n" + "beam.distribution.ns1.n2.attempted.min 3 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.max 9 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.count 4 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.sum 25 [0-9]+\\n") + "beam.distribution.ns1.n2.attempted.mean 6.25 [0-9]+");
        Assert.assertTrue(join.matches(regexpr));
    }
}

