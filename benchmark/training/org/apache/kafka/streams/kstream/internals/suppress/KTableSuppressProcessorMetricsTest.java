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
package org.apache.kafka.streams.kstream.internals.suppress;


import java.time.Duration;
import java.util.Map;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.internals.Change;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.internals.ProcessorNode;
import org.apache.kafka.streams.state.internals.InMemoryTimeOrderedKeyValueBuffer;
import org.apache.kafka.test.MockInternalProcessorContext;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;

import static BufferConfig.maxRecords;


@SuppressWarnings("PointlessArithmeticExpression")
public class KTableSuppressProcessorMetricsTest {
    private static final long ARBITRARY_LONG = 5L;

    private static final MetricName EVICTION_TOTAL_METRIC = new MetricName("suppression-emit-total", "stream-processor-node-metrics", "The total number of occurrence of suppression-emit operations.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("processor-node-id", "testNode")));

    private static final MetricName EVICTION_RATE_METRIC = new MetricName("suppression-emit-rate", "stream-processor-node-metrics", "The average number of occurrence of suppression-emit operation per second.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("processor-node-id", "testNode")));

    private static final MetricName BUFFER_SIZE_AVG_METRIC = new MetricName("suppression-buffer-size-avg", "stream-buffer-metrics", "The average size of buffered records.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("buffer-id", "test-store")));

    private static final MetricName BUFFER_SIZE_CURRENT_METRIC = new MetricName("suppression-buffer-size-current", "stream-buffer-metrics", "The current size of buffered records.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("buffer-id", "test-store")));

    private static final MetricName BUFFER_SIZE_MAX_METRIC = new MetricName("suppression-buffer-size-max", "stream-buffer-metrics", "The max size of buffered records.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("buffer-id", "test-store")));

    private static final MetricName BUFFER_COUNT_AVG_METRIC = new MetricName("suppression-buffer-count-avg", "stream-buffer-metrics", "The average count of buffered records.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("buffer-id", "test-store")));

    private static final MetricName BUFFER_COUNT_CURRENT_METRIC = new MetricName("suppression-buffer-count-current", "stream-buffer-metrics", "The current count of buffered records.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("buffer-id", "test-store")));

    private static final MetricName BUFFER_COUNT_MAX_METRIC = new MetricName("suppression-buffer-count-max", "stream-buffer-metrics", "The max count of buffered records.", mkMap(mkEntry("client-id", "mock-processor-context-virtual-thread"), mkEntry("task-id", "0_0"), mkEntry("buffer-id", "test-store")));

    @Test
    public void shouldRecordMetrics() {
        final String storeName = "test-store";
        final StateStore buffer = new InMemoryTimeOrderedKeyValueBuffer.Builder(storeName).withLoggingDisabled().build();
        final KTableSuppressProcessor<String, Long> processor = new KTableSuppressProcessor(((SuppressedInternal<String>) (Suppressed.<String>untilTimeLimit(Duration.ofDays(100), maxRecords(1)))), storeName, Serdes.String(), new org.apache.kafka.streams.kstream.internals.FullChangeSerde(Serdes.Long()));
        final MockInternalProcessorContext context = new MockInternalProcessorContext();
        context.setCurrentNode(new ProcessorNode("testNode"));
        buffer.init(context, buffer);
        processor.init(context);
        final long timestamp = 100L;
        setRecordMetadata("", 0, 0L, null, timestamp);
        final String key = "longKey";
        final Change<Long> value = new Change(null, KTableSuppressProcessorMetricsTest.ARBITRARY_LONG);
        processor.process(key, value);
        {
            final Map<MetricName, ? extends Metric> metrics = context.metrics().metrics();
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.EVICTION_RATE_METRIC, Is.is(0.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.EVICTION_TOTAL_METRIC, Is.is(0.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_SIZE_AVG_METRIC, Is.is(25.5));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_SIZE_CURRENT_METRIC, Is.is(51.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_SIZE_MAX_METRIC, Is.is(51.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_COUNT_AVG_METRIC, Is.is(0.5));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_COUNT_CURRENT_METRIC, Is.is(1.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_COUNT_MAX_METRIC, Is.is(1.0));
        }
        setRecordMetadata("", 0, 1L, null, (timestamp + 1));
        processor.process("key", value);
        {
            final Map<MetricName, ? extends Metric> metrics = context.metrics().metrics();
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.EVICTION_RATE_METRIC, Matchers.greaterThan(0.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.EVICTION_TOTAL_METRIC, Is.is(1.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_SIZE_AVG_METRIC, Is.is(49.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_SIZE_CURRENT_METRIC, Is.is(47.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_SIZE_MAX_METRIC, Is.is(98.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_COUNT_AVG_METRIC, Is.is(1.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_COUNT_CURRENT_METRIC, Is.is(1.0));
            verifyMetric(metrics, KTableSuppressProcessorMetricsTest.BUFFER_COUNT_MAX_METRIC, Is.is(2.0));
        }
    }
}

