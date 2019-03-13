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
package org.apache.flink.runtime.rest.handler.legacy.metrics;


import MetricDumpSerialization.MetricSerializationResult;
import MetricOptions.METRIC_FETCHER_UPDATE_INTERVAL;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.metrics.dump.MetricDumpSerialization;
import org.apache.flink.runtime.metrics.dump.MetricQueryService;
import org.apache.flink.runtime.webmonitor.RestfulGateway;
import org.apache.flink.runtime.webmonitor.TestingRestfulGateway;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.runtime.webmonitor.retriever.MetricQueryServiceGateway;
import org.apache.flink.runtime.webmonitor.retriever.MetricQueryServiceRetriever;
import org.apache.flink.runtime.webmonitor.retriever.TestingMetricQueryServiceGateway;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the MetricFetcher.
 */
public class MetricFetcherTest extends TestLogger {
    @Test
    public void testUpdate() {
        final Time timeout = Time.seconds(10L);
        // ========= setup TaskManager =================================================================================
        JobID jobID = new JobID();
        ResourceID tmRID = ResourceID.generate();
        // ========= setup JobManager ==================================================================================
        final String jmMetricQueryServicePath = "/jm/" + (MetricQueryService.METRIC_QUERY_SERVICE_NAME);
        final String tmMetricQueryServicePath = (("/tm/" + (MetricQueryService.METRIC_QUERY_SERVICE_NAME)) + "_") + (tmRID.getResourceIdString());
        final TestingRestfulGateway restfulGateway = new TestingRestfulGateway.Builder().setRequestMultipleJobDetailsSupplier(() -> CompletableFuture.completedFuture(new MultipleJobsDetails(Collections.emptyList()))).setRequestMetricQueryServicePathsSupplier(() -> CompletableFuture.completedFuture(Collections.singleton(jmMetricQueryServicePath))).setRequestTaskManagerMetricQueryServicePathsSupplier(() -> CompletableFuture.completedFuture(Collections.singleton(Tuple2.of(tmRID, tmMetricQueryServicePath)))).build();
        final GatewayRetriever<RestfulGateway> retriever = () -> CompletableFuture.completedFuture(restfulGateway);
        // ========= setup QueryServices ================================================================================
        final MetricQueryServiceGateway jmQueryService = new TestingMetricQueryServiceGateway.Builder().setQueryMetricsSupplier(() -> CompletableFuture.completedFuture(new MetricDumpSerialization.MetricSerializationResult(new byte[0], new byte[0], new byte[0], new byte[0], 0, 0, 0, 0))).build();
        MetricDumpSerialization.MetricSerializationResult requestMetricsAnswer = MetricFetcherTest.createRequestDumpAnswer(tmRID, jobID);
        final MetricQueryServiceGateway tmQueryService = new TestingMetricQueryServiceGateway.Builder().setQueryMetricsSupplier(() -> CompletableFuture.completedFuture(requestMetricsAnswer)).build();
        final MetricQueryServiceRetriever queryServiceRetriever = ( path) -> {
            if (path.equals(jmMetricQueryServicePath)) {
                return CompletableFuture.completedFuture(jmQueryService);
            } else
                if (path.equals(tmMetricQueryServicePath)) {
                    return CompletableFuture.completedFuture(tmQueryService);
                } else {
                    throw new IllegalArgumentException("Unexpected argument.");
                }

        };
        // ========= start MetricFetcher testing =======================================================================
        MetricFetcher fetcher = new MetricFetcherImpl(retriever, queryServiceRetriever, Executors.directExecutor(), timeout, METRIC_FETCHER_UPDATE_INTERVAL.defaultValue());
        // verify that update fetches metrics and updates the store
        fetcher.update();
        MetricStore store = fetcher.getMetricStore();
        synchronized(store) {
            Assert.assertEquals("7", store.getJobManagerMetricStore().getMetric("abc.hist_min"));
            Assert.assertEquals("6", store.getJobManagerMetricStore().getMetric("abc.hist_max"));
            Assert.assertEquals("4.0", store.getJobManagerMetricStore().getMetric("abc.hist_mean"));
            Assert.assertEquals("0.5", store.getJobManagerMetricStore().getMetric("abc.hist_median"));
            Assert.assertEquals("5.0", store.getJobManagerMetricStore().getMetric("abc.hist_stddev"));
            Assert.assertEquals("0.75", store.getJobManagerMetricStore().getMetric("abc.hist_p75"));
            Assert.assertEquals("0.9", store.getJobManagerMetricStore().getMetric("abc.hist_p90"));
            Assert.assertEquals("0.95", store.getJobManagerMetricStore().getMetric("abc.hist_p95"));
            Assert.assertEquals("0.98", store.getJobManagerMetricStore().getMetric("abc.hist_p98"));
            Assert.assertEquals("0.99", store.getJobManagerMetricStore().getMetric("abc.hist_p99"));
            Assert.assertEquals("0.999", store.getJobManagerMetricStore().getMetric("abc.hist_p999"));
            Assert.assertEquals("x", store.getTaskManagerMetricStore(tmRID.toString()).metrics.get("abc.gauge"));
            Assert.assertEquals("5.0", store.getJobMetricStore(jobID.toString()).metrics.get("abc.jc"));
            Assert.assertEquals("2", store.getTaskMetricStore(jobID.toString(), "taskid").metrics.get("2.abc.tc"));
            Assert.assertEquals("1", store.getTaskMetricStore(jobID.toString(), "taskid").metrics.get("2.opname.abc.oc"));
        }
    }

    @Test
    public void testLongUpdateInterval() {
        final long updateInterval = 1000L;
        final AtomicInteger requestMetricQueryServicePathsCounter = new AtomicInteger(0);
        final RestfulGateway restfulGateway = createRestfulGateway(requestMetricQueryServicePathsCounter);
        final MetricFetcher fetcher = createMetricFetcher(updateInterval, restfulGateway);
        fetcher.update();
        fetcher.update();
        Assert.assertThat(requestMetricQueryServicePathsCounter.get(), Matchers.is(1));
    }

    @Test
    public void testShortUpdateInterval() throws InterruptedException {
        final long updateInterval = 1L;
        final AtomicInteger requestMetricQueryServicePathsCounter = new AtomicInteger(0);
        final RestfulGateway restfulGateway = createRestfulGateway(requestMetricQueryServicePathsCounter);
        final MetricFetcher fetcher = createMetricFetcher(updateInterval, restfulGateway);
        fetcher.update();
        final long start = System.currentTimeMillis();
        long difference = 0L;
        while (difference <= updateInterval) {
            Thread.sleep((2L * updateInterval));
            difference = (System.currentTimeMillis()) - start;
        } 
        fetcher.update();
        Assert.assertThat(requestMetricQueryServicePathsCounter.get(), Matchers.is(2));
    }
}

