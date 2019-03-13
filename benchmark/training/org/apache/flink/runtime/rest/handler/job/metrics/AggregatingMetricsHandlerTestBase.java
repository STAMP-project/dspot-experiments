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


import MetricStore.ComponentMetricStore;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.dispatcher.DispatcherGateway;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.handler.legacy.metrics.MetricStore;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.job.metrics.AbstractAggregatedMetricsParameters;
import org.apache.flink.runtime.rest.messages.job.metrics.AggregatedMetric;
import org.apache.flink.runtime.rest.messages.job.metrics.AggregatedMetricsResponseBody;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test base for handlers that extend {@link AbstractAggregatingMetricsHandler}.
 */
public abstract class AggregatingMetricsHandlerTestBase<H extends AbstractAggregatingMetricsHandler<P>, P extends AbstractAggregatedMetricsParameters<?>> extends TestLogger {
    private static final CompletableFuture<String> TEST_REST_ADDRESS;

    private static final DispatcherGateway MOCK_DISPATCHER_GATEWAY;

    private static final GatewayRetriever<DispatcherGateway> LEADER_RETRIEVER;

    private static final Time TIMEOUT = Time.milliseconds(50);

    private static final Map<String, String> TEST_HEADERS = Collections.emptyMap();

    private static final Executor EXECUTOR = TestingUtils.defaultExecutor();

    static {
        TEST_REST_ADDRESS = CompletableFuture.completedFuture("localhost:12345");
        MOCK_DISPATCHER_GATEWAY = AggregatingMetricsHandlerTestBase.mock(DispatcherGateway.class);
        LEADER_RETRIEVER = new GatewayRetriever<DispatcherGateway>() {
            @Override
            public CompletableFuture<DispatcherGateway> getFuture() {
                return CompletableFuture.completedFuture(AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY);
            }
        };
    }

    private H handler;

    private MetricStore store;

    private Map<String, String> pathParameters;

    @Test
    public void getStores() throws Exception {
        {
            // test without filter
            HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, Collections.emptyMap());
            Collection<? extends MetricStore.ComponentMetricStore> subStores = handler.getStores(store, request);
            Assert.assertEquals(3, subStores.size());
            List<String> sortedMetrics1 = subStores.stream().map(( subStore) -> getMetric("abc.metric1")).filter(Objects::nonNull).sorted().collect(Collectors.toList());
            Assert.assertEquals(2, sortedMetrics1.size());
            Assert.assertEquals("1", sortedMetrics1.get(0));
            Assert.assertEquals("3", sortedMetrics1.get(1));
            List<String> sortedMetrics2 = subStores.stream().map(( subStore) -> getMetric("abc.metric2")).filter(Objects::nonNull).sorted().collect(Collectors.toList());
            Assert.assertEquals(1, sortedMetrics2.size());
            Assert.assertEquals("5", sortedMetrics2.get(0));
        }
        {
            // test with filter
            Tuple2<String, List<String>> filter = getFilter();
            Map<String, List<String>> queryParameters = new HashMap<>(4);
            queryParameters.put(filter.f0, filter.f1);
            HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, queryParameters);
            Collection<? extends MetricStore.ComponentMetricStore> subStores = handler.getStores(store, request);
            Assert.assertEquals(2, subStores.size());
            List<String> sortedMetrics1 = subStores.stream().map(( subStore) -> getMetric("abc.metric1")).filter(Objects::nonNull).sorted().collect(Collectors.toList());
            Assert.assertEquals(1, sortedMetrics1.size());
            Assert.assertEquals("1", sortedMetrics1.get(0));
            List<String> sortedMetrics2 = subStores.stream().map(( subStore) -> getMetric("abc.metric2")).filter(Objects::nonNull).sorted().collect(Collectors.toList());
            Assert.assertEquals(1, sortedMetrics2.size());
            Assert.assertEquals("5", sortedMetrics2.get(0));
        }
    }

    @Test
    public void testListMetrics() throws Exception {
        HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, Collections.emptyMap());
        AggregatedMetricsResponseBody response = handler.handleRequest(request, AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY).get();
        List<String> availableMetrics = response.getMetrics().stream().map(AggregatedMetric::getId).sorted().collect(Collectors.toList());
        Assert.assertEquals(2, availableMetrics.size());
        Assert.assertEquals("abc.metric1", availableMetrics.get(0));
        Assert.assertEquals("abc.metric2", availableMetrics.get(1));
    }

    @Test
    public void testMinAggregation() throws Exception {
        Map<String, List<String>> queryParams = new HashMap<>(4);
        queryParams.put("get", Collections.singletonList("abc.metric1"));
        queryParams.put("agg", Collections.singletonList("min"));
        HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, queryParams);
        AggregatedMetricsResponseBody response = handler.handleRequest(request, AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY).get();
        Collection<AggregatedMetric> aggregatedMetrics = response.getMetrics();
        Assert.assertEquals(1, aggregatedMetrics.size());
        AggregatedMetric aggregatedMetric = aggregatedMetrics.iterator().next();
        Assert.assertEquals("abc.metric1", aggregatedMetric.getId());
        Assert.assertEquals(1.0, aggregatedMetric.getMin(), 0.1);
        Assert.assertNull(aggregatedMetric.getMax());
        Assert.assertNull(aggregatedMetric.getSum());
        Assert.assertNull(aggregatedMetric.getAvg());
    }

    @Test
    public void testMaxAggregation() throws Exception {
        Map<String, List<String>> queryParams = new HashMap<>(4);
        queryParams.put("get", Collections.singletonList("abc.metric1"));
        queryParams.put("agg", Collections.singletonList("max"));
        HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, queryParams);
        AggregatedMetricsResponseBody response = handler.handleRequest(request, AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY).get();
        Collection<AggregatedMetric> aggregatedMetrics = response.getMetrics();
        Assert.assertEquals(1, aggregatedMetrics.size());
        AggregatedMetric aggregatedMetric = aggregatedMetrics.iterator().next();
        Assert.assertEquals("abc.metric1", aggregatedMetric.getId());
        Assert.assertEquals(3.0, aggregatedMetric.getMax(), 0.1);
        Assert.assertNull(aggregatedMetric.getMin());
        Assert.assertNull(aggregatedMetric.getSum());
        Assert.assertNull(aggregatedMetric.getAvg());
    }

    @Test
    public void testSumAggregation() throws Exception {
        Map<String, List<String>> queryParams = new HashMap<>(4);
        queryParams.put("get", Collections.singletonList("abc.metric1"));
        queryParams.put("agg", Collections.singletonList("sum"));
        HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, queryParams);
        AggregatedMetricsResponseBody response = handler.handleRequest(request, AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY).get();
        Collection<AggregatedMetric> aggregatedMetrics = response.getMetrics();
        Assert.assertEquals(1, aggregatedMetrics.size());
        AggregatedMetric aggregatedMetric = aggregatedMetrics.iterator().next();
        Assert.assertEquals("abc.metric1", aggregatedMetric.getId());
        Assert.assertEquals(4.0, aggregatedMetric.getSum(), 0.1);
        Assert.assertNull(aggregatedMetric.getMin());
        Assert.assertNull(aggregatedMetric.getMax());
        Assert.assertNull(aggregatedMetric.getAvg());
    }

    @Test
    public void testAvgAggregation() throws Exception {
        Map<String, List<String>> queryParams = new HashMap<>(4);
        queryParams.put("get", Collections.singletonList("abc.metric1"));
        queryParams.put("agg", Collections.singletonList("avg"));
        HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, queryParams);
        AggregatedMetricsResponseBody response = handler.handleRequest(request, AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY).get();
        Collection<AggregatedMetric> aggregatedMetrics = response.getMetrics();
        Assert.assertEquals(1, aggregatedMetrics.size());
        AggregatedMetric aggregatedMetric = aggregatedMetrics.iterator().next();
        Assert.assertEquals("abc.metric1", aggregatedMetric.getId());
        Assert.assertEquals(2.0, aggregatedMetric.getAvg(), 0.1);
        Assert.assertNull(aggregatedMetric.getMin());
        Assert.assertNull(aggregatedMetric.getMax());
        Assert.assertNull(aggregatedMetric.getSum());
    }

    @Test
    public void testMultipleAggregation() throws Exception {
        Map<String, List<String>> queryParams = new HashMap<>(4);
        queryParams.put("get", Collections.singletonList("abc.metric1"));
        queryParams.put("agg", Arrays.asList("min", "max", "avg"));
        HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, queryParams);
        AggregatedMetricsResponseBody response = handler.handleRequest(request, AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY).get();
        Collection<AggregatedMetric> aggregatedMetrics = response.getMetrics();
        Assert.assertEquals(1, aggregatedMetrics.size());
        AggregatedMetric aggregatedMetric = aggregatedMetrics.iterator().next();
        Assert.assertEquals("abc.metric1", aggregatedMetric.getId());
        Assert.assertEquals(1.0, aggregatedMetric.getMin(), 0.1);
        Assert.assertEquals(3.0, aggregatedMetric.getMax(), 0.1);
        Assert.assertEquals(2.0, aggregatedMetric.getAvg(), 0.1);
        Assert.assertNull(aggregatedMetric.getSum());
    }

    @Test
    public void testDefaultAggregation() throws Exception {
        Map<String, List<String>> queryParams = new HashMap<>(4);
        queryParams.put("get", Collections.singletonList("abc.metric1"));
        HandlerRequest<EmptyRequestBody, P> request = new HandlerRequest(EmptyRequestBody.getInstance(), getMessageHeaders().getUnresolvedMessageParameters(), pathParameters, queryParams);
        AggregatedMetricsResponseBody response = handler.handleRequest(request, AggregatingMetricsHandlerTestBase.MOCK_DISPATCHER_GATEWAY).get();
        Collection<AggregatedMetric> aggregatedMetrics = response.getMetrics();
        Assert.assertEquals(1, aggregatedMetrics.size());
        AggregatedMetric aggregatedMetric = aggregatedMetrics.iterator().next();
        Assert.assertEquals("abc.metric1", aggregatedMetric.getId());
        Assert.assertEquals(1.0, aggregatedMetric.getMin(), 0.1);
        Assert.assertEquals(3.0, aggregatedMetric.getMax(), 0.1);
        Assert.assertEquals(2.0, aggregatedMetric.getAvg(), 0.1);
        Assert.assertEquals(4.0, aggregatedMetric.getSum(), 0.1);
    }
}

