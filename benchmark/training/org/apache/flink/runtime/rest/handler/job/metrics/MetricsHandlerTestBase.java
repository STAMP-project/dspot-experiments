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
package org.apache.flink.runtime.rest.handler.job.metrics;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.dispatcher.DispatcherGateway;
import org.apache.flink.runtime.rest.handler.legacy.metrics.MetricFetcher;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.job.metrics.Metric;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.util.TestLogger;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 * Unit test base class for subclasses of {@link AbstractMetricsHandler}.
 */
public abstract class MetricsHandlerTestBase<T extends AbstractMetricsHandler> extends TestLogger {
    private static final String TEST_METRIC_NAME = "test_counter";

    private static final int TEST_METRIC_VALUE = 1000;

    static final CompletableFuture<String> TEST_REST_ADDRESS = CompletableFuture.completedFuture("localhost:12345");

    static final Time TIMEOUT = Time.milliseconds(50);

    static final Map<String, String> TEST_HEADERS = Collections.emptyMap();

    @Mock
    MetricFetcher mockMetricFetcher;

    GatewayRetriever<DispatcherGateway> leaderRetriever;

    @Mock
    private DispatcherGateway mockDispatcherGateway;

    private T metricsHandler;

    private Map<String, String> pathParameters;

    /**
     * Tests that the metric with name defined under {@link #TEST_METRIC_NAME} can be retrieved
     * from the {@link MetricStore.ComponentMetricStore} returned from
     * {@link AbstractMetricsHandler#getComponentMetricStore(HandlerRequest, MetricStore)}.
     */
    @Test
    public void testGetMetric() throws Exception {
        @SuppressWarnings("unchecked")
        final CompletableFuture<MetricCollectionResponseBody> completableFuture = metricsHandler.handleRequest(new org.apache.flink.runtime.rest.handler.HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, Collections.emptyMap()), mockDispatcherGateway);
        Assert.assertTrue(completableFuture.isDone());
        final MetricCollectionResponseBody metricCollectionResponseBody = completableFuture.get();
        MatcherAssert.assertThat(metricCollectionResponseBody.getMetrics(), MetricsHandlerTestBase.hasSize(1));
        final Metric metric = metricCollectionResponseBody.getMetrics().iterator().next();
        MatcherAssert.assertThat(metric.getId(), MetricsHandlerTestBase.equalTo(getExpectedIdForMetricName(MetricsHandlerTestBase.TEST_METRIC_NAME)));
    }
}

