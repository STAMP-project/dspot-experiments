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


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.processor.internals.ProcessorContextImpl;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.test.NoOpRecordCollector;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class ChangeLoggingTimestampedWindowBytesStoreTest {
    private final TaskId taskId = new TaskId(0, 0);

    private final Map<Object, ValueAndTimestamp<Object>> sent = new HashMap<>();

    private final NoOpRecordCollector collector = new NoOpRecordCollector() {
        @Override
        public <K, V> void send(final String topic, final K key, final V value, final Headers headers, final Integer partition, final Long timestamp, final Serializer<K> keySerializer, final Serializer<V> valueSerializer) {
            sent.put(key, ValueAndTimestamp.make(value, timestamp));
        }
    };

    private final byte[] value = new byte[]{ 0 };

    private final byte[] valueAndTimestamp = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 42, 0 };

    private final Bytes bytesKey = Bytes.wrap(value);

    @Mock(type = MockType.NICE)
    private WindowStore<Bytes, byte[]> inner;

    @Mock(type = MockType.NICE)
    private ProcessorContextImpl context;

    private ChangeLoggingTimestampedWindowBytesStore store;

    @Test
    public void shouldLogPuts() {
        inner.put(bytesKey, valueAndTimestamp, 0);
        EasyMock.expectLastCall();
        init();
        store.put(bytesKey, valueAndTimestamp);
        Assert.assertArrayEquals(value, ((byte[]) (sent.get(WindowKeySchema.toStoreKeyBinary(bytesKey, 0, 0)).value())));
        Assert.assertEquals(42L, sent.get(WindowKeySchema.toStoreKeyBinary(bytesKey, 0, 0)).timestamp());
        EasyMock.verify(inner);
    }

    @Test
    public void shouldDelegateToUnderlyingStoreWhenFetching() {
        EasyMock.expect(inner.fetch(bytesKey, 0, 10)).andReturn(KeyValueIterators.emptyWindowStoreIterator());
        init();
        store.fetch(bytesKey, Instant.ofEpochMilli(0), Instant.ofEpochMilli(10));
        EasyMock.verify(inner);
    }

    @Test
    public void shouldDelegateToUnderlyingStoreWhenFetchingRange() {
        EasyMock.expect(inner.fetch(bytesKey, bytesKey, 0, 1)).andReturn(KeyValueIterators.emptyIterator());
        init();
        store.fetch(bytesKey, bytesKey, Instant.ofEpochMilli(0), Instant.ofEpochMilli(1));
        EasyMock.verify(inner);
    }

    @Test
    public void shouldRetainDuplicatesWhenSet() {
        store = new ChangeLoggingTimestampedWindowBytesStore(inner, true);
        inner.put(bytesKey, valueAndTimestamp, 0);
        EasyMock.expectLastCall().times(2);
        init();
        store.put(bytesKey, valueAndTimestamp);
        store.put(bytesKey, valueAndTimestamp);
        Assert.assertArrayEquals(value, ((byte[]) (sent.get(WindowKeySchema.toStoreKeyBinary(bytesKey, 0, 1)).value())));
        Assert.assertEquals(42L, sent.get(WindowKeySchema.toStoreKeyBinary(bytesKey, 0, 1)).timestamp());
        Assert.assertArrayEquals(value, ((byte[]) (sent.get(WindowKeySchema.toStoreKeyBinary(bytesKey, 0, 2)).value())));
        Assert.assertEquals(42L, sent.get(WindowKeySchema.toStoreKeyBinary(bytesKey, 0, 2)).timestamp());
        EasyMock.verify(inner);
    }
}

