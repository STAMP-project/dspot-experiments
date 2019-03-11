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


import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class ReadOnlyKeyValueStoreFacadeTest {
    @Mock
    private TimestampedKeyValueStore<String, String> mockedKeyValueTimestampStore;

    @Mock
    private KeyValueIterator<String, ValueAndTimestamp<String>> mockedKeyValueTimestampIterator;

    private ReadOnlyKeyValueStoreFacade<String, String> readOnlyKeyValueStoreFacade;

    @Test
    public void shouldReturnPlainValueOnGet() {
        expect(mockedKeyValueTimestampStore.get("key")).andReturn(ValueAndTimestamp.make("value", 42L));
        expect(mockedKeyValueTimestampStore.get("unknownKey")).andReturn(null);
        replay(mockedKeyValueTimestampStore);
        MatcherAssert.assertThat(readOnlyKeyValueStoreFacade.get("key"), Matchers.is("value"));
        Assert.assertNull(readOnlyKeyValueStoreFacade.get("unknownKey"));
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldReturnPlainKeyValuePairsForRangeIterator() {
        expect(mockedKeyValueTimestampIterator.next()).andReturn(KeyValue.pair("key1", ValueAndTimestamp.make("value1", 21L))).andReturn(KeyValue.pair("key2", ValueAndTimestamp.make("value2", 42L)));
        expect(mockedKeyValueTimestampStore.range("key1", "key2")).andReturn(mockedKeyValueTimestampIterator);
        replay(mockedKeyValueTimestampIterator, mockedKeyValueTimestampStore);
        final KeyValueIterator<String, String> iterator = readOnlyKeyValueStoreFacade.range("key1", "key2");
        MatcherAssert.assertThat(iterator.next(), Matchers.is(KeyValue.pair("key1", "value1")));
        MatcherAssert.assertThat(iterator.next(), Matchers.is(KeyValue.pair("key2", "value2")));
        verify(mockedKeyValueTimestampIterator, mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldReturnPlainKeyValuePairsForAllIterator() {
        expect(mockedKeyValueTimestampIterator.next()).andReturn(KeyValue.pair("key1", ValueAndTimestamp.make("value1", 21L))).andReturn(KeyValue.pair("key2", ValueAndTimestamp.make("value2", 42L)));
        expect(mockedKeyValueTimestampStore.all()).andReturn(mockedKeyValueTimestampIterator);
        replay(mockedKeyValueTimestampIterator, mockedKeyValueTimestampStore);
        final KeyValueIterator<String, String> iterator = readOnlyKeyValueStoreFacade.all();
        MatcherAssert.assertThat(iterator.next(), Matchers.is(KeyValue.pair("key1", "value1")));
        MatcherAssert.assertThat(iterator.next(), Matchers.is(KeyValue.pair("key2", "value2")));
        verify(mockedKeyValueTimestampIterator, mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldForwardApproximateNumEntries() {
        expect(mockedKeyValueTimestampStore.approximateNumEntries()).andReturn(42L);
        replay(mockedKeyValueTimestampStore);
        MatcherAssert.assertThat(readOnlyKeyValueStoreFacade.approximateNumEntries(), Matchers.is(42L));
        verify(mockedKeyValueTimestampStore);
    }
}

