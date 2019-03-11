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
package org.apache.kafka.streams.processor.internals;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.BatchingStateRestoreCallback;
import org.apache.kafka.streams.processor.StateRestoreCallback;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class StateRestoreCallbackAdapterTest {
    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowOnRestoreAll() {
        StateRestoreCallbackAdapter.adapt(mock(StateRestoreCallback.class)).restoreAll(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowOnRestore() {
        StateRestoreCallbackAdapter.adapt(mock(StateRestoreCallback.class)).restore(null, null);
    }

    @Test
    public void shouldPassRecordsThrough() {
        final ArrayList<ConsumerRecord<byte[], byte[]>> actual = new ArrayList<>();
        final RecordBatchingStateRestoreCallback callback = actual::addAll;
        final RecordBatchingStateRestoreCallback adapted = StateRestoreCallbackAdapter.adapt(callback);
        final byte[] key1 = new byte[]{ 1 };
        final byte[] value1 = new byte[]{ 2 };
        final byte[] key2 = new byte[]{ 3 };
        final byte[] value2 = new byte[]{ 4 };
        final List<ConsumerRecord<byte[], byte[]>> recordList = Arrays.asList(new ConsumerRecord("topic1", 0, 0L, key1, value1), new ConsumerRecord("topic2", 1, 1L, key2, value2));
        adapted.restoreBatch(recordList);
        validate(actual, recordList);
    }

    @Test
    public void shouldConvertToKeyValueBatches() {
        final ArrayList<KeyValue<byte[], byte[]>> actual = new ArrayList<>();
        final BatchingStateRestoreCallback callback = new BatchingStateRestoreCallback() {
            @Override
            public void restoreAll(final Collection<KeyValue<byte[], byte[]>> records) {
                actual.addAll(records);
            }

            @Override
            public void restore(final byte[] key, final byte[] value) {
                // unreachable
            }
        };
        final RecordBatchingStateRestoreCallback adapted = StateRestoreCallbackAdapter.adapt(callback);
        final byte[] key1 = new byte[]{ 1 };
        final byte[] value1 = new byte[]{ 2 };
        final byte[] key2 = new byte[]{ 3 };
        final byte[] value2 = new byte[]{ 4 };
        adapted.restoreBatch(Arrays.asList(new ConsumerRecord("topic1", 0, 0L, key1, value1), new ConsumerRecord("topic2", 1, 1L, key2, value2)));
        MatcherAssert.assertThat(actual, Is.is(Arrays.asList(new KeyValue(key1, value1), new KeyValue(key2, value2))));
    }

    @Test
    public void shouldConvertToKeyValue() {
        final ArrayList<KeyValue<byte[], byte[]>> actual = new ArrayList<>();
        final StateRestoreCallback callback = ( key, value) -> actual.add(new KeyValue<>(key, value));
        final RecordBatchingStateRestoreCallback adapted = StateRestoreCallbackAdapter.adapt(callback);
        final byte[] key1 = new byte[]{ 1 };
        final byte[] value1 = new byte[]{ 2 };
        final byte[] key2 = new byte[]{ 3 };
        final byte[] value2 = new byte[]{ 4 };
        adapted.restoreBatch(Arrays.asList(new ConsumerRecord("topic1", 0, 0L, key1, value1), new ConsumerRecord("topic2", 1, 1L, key2, value2)));
        MatcherAssert.assertThat(actual, Is.is(Arrays.asList(new KeyValue(key1, value1), new KeyValue(key2, value2))));
    }
}

