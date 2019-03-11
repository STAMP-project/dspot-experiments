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
package org.apache.kafka.streams.state.internals;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.JmxReporter;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class MeteredKeyValueStoreTest {
    private final TaskId taskId = new TaskId(0, 0);

    private final Map<String, String> tags = mkMap(mkEntry("client-id", "test"), mkEntry("task-id", taskId.toString()), mkEntry("scope-id", "metered"));

    @Mock(type = MockType.NICE)
    private KeyValueStore<Bytes, byte[]> inner;

    @Mock(type = MockType.NICE)
    private ProcessorContext context;

    private MeteredKeyValueStore<String, String> metered;

    private final String key = "key";

    private final Bytes keyBytes = Bytes.wrap(key.getBytes());

    private final String value = "value";

    private final byte[] valueBytes = value.getBytes();

    private final KeyValue<Bytes, byte[]> byteKeyValuePair = KeyValue.pair(keyBytes, valueBytes);

    private final Metrics metrics = new Metrics();

    @Test
    public void testMetrics() {
        init();
        final JmxReporter reporter = new JmxReporter("kafka.streams");
        metrics.addReporter(reporter);
        Assert.assertTrue(reporter.containsMbean(String.format("kafka.streams:type=stream-%s-metrics,client-id=%s,task-id=%s,%s-id=%s", "scope", "test", taskId.toString(), "scope", "metered")));
        Assert.assertTrue(reporter.containsMbean(String.format("kafka.streams:type=stream-%s-metrics,client-id=%s,task-id=%s,%s-id=%s", "scope", "test", taskId.toString(), "scope", "all")));
    }

    @Test
    public void shouldWriteBytesToInnerStoreAndRecordPutMetric() {
        inner.put(eq(keyBytes), aryEq(valueBytes));
        expectLastCall();
        init();
        metered.put(key, value);
        final KafkaMetric metric = metric("put-rate");
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    @Test
    public void shouldGetBytesFromInnerStoreAndReturnGetMetric() {
        expect(inner.get(keyBytes)).andReturn(valueBytes);
        init();
        MatcherAssert.assertThat(metered.get(key), CoreMatchers.equalTo(value));
        final KafkaMetric metric = metric("get-rate");
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    @Test
    public void shouldPutIfAbsentAndRecordPutIfAbsentMetric() {
        expect(inner.putIfAbsent(eq(keyBytes), aryEq(valueBytes))).andReturn(null);
        init();
        metered.putIfAbsent(key, value);
        final KafkaMetric metric = metric("put-if-absent-rate");
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPutAllToInnerStoreAndRecordPutAllMetric() {
        inner.putAll(anyObject(List.class));
        expectLastCall();
        init();
        metered.putAll(Collections.singletonList(KeyValue.pair(key, value)));
        final KafkaMetric metric = metric("put-all-rate");
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    @Test
    public void shouldDeleteFromInnerStoreAndRecordDeleteMetric() {
        expect(inner.delete(keyBytes)).andReturn(valueBytes);
        init();
        metered.delete(key);
        final KafkaMetric metric = metric("delete-rate");
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    @Test
    public void shouldGetRangeFromInnerStoreAndRecordRangeMetric() {
        expect(inner.range(keyBytes, keyBytes)).andReturn(new org.apache.kafka.test.KeyValueIteratorStub(Collections.singletonList(byteKeyValuePair).iterator()));
        init();
        final KeyValueIterator<String, String> iterator = metered.range(key, key);
        MatcherAssert.assertThat(iterator.next().value, CoreMatchers.equalTo(value));
        Assert.assertFalse(iterator.hasNext());
        iterator.close();
        final KafkaMetric metric = metric("range-rate");
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    @Test
    public void shouldGetAllFromInnerStoreAndRecordAllMetric() {
        expect(inner.all()).andReturn(new org.apache.kafka.test.KeyValueIteratorStub(Collections.singletonList(byteKeyValuePair).iterator()));
        init();
        final KeyValueIterator<String, String> iterator = metered.all();
        MatcherAssert.assertThat(iterator.next().value, CoreMatchers.equalTo(value));
        Assert.assertFalse(iterator.hasNext());
        iterator.close();
        final KafkaMetric metric = metric(new MetricName("all-rate", "stream-scope-metrics", "", tags));
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    @Test
    public void shouldFlushInnerWhenFlushTimeRecords() {
        inner.flush();
        expectLastCall().once();
        init();
        metered.flush();
        final KafkaMetric metric = metric("flush-rate");
        Assert.assertTrue((((Double) (metric.metricValue())) > 0));
        verify(inner);
    }

    private interface CachedKeyValueStore extends KeyValueStore<Bytes, byte[]> , CachedStateStore<byte[], byte[]> {}

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetFlushListenerOnWrappedCachingStore() {
        final MeteredKeyValueStoreTest.CachedKeyValueStore cachedKeyValueStore = mock(MeteredKeyValueStoreTest.CachedKeyValueStore.class);
        expect(cachedKeyValueStore.setFlushListener(anyObject(CacheFlushListener.class), eq(false))).andReturn(true);
        replay(cachedKeyValueStore);
        metered = new MeteredKeyValueStore(cachedKeyValueStore, "scope", new MockTime(), Serdes.String(), Serdes.String());
        Assert.assertTrue(metered.setFlushListener(null, false));
        verify(cachedKeyValueStore);
    }

    @Test
    public void shouldNotSetFlushListenerOnWrappedNoneCachingStore() {
        Assert.assertFalse(metered.setFlushListener(null, false));
    }
}

