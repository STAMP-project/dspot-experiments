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
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class TimestampedKeyValueStoreBuilderTest {
    @Mock(type = MockType.NICE)
    private KeyValueBytesStoreSupplier supplier;

    @Mock(type = MockType.NICE)
    private KeyValueStore<Bytes, byte[]> inner;

    private TimestampedKeyValueStoreBuilder<String, String> builder;

    @Test
    public void shouldHaveMeteredStoreAsOuterStore() {
        final TimestampedKeyValueStore<String, String> store = builder.build();
        MatcherAssert.assertThat(store, IsInstanceOf.instanceOf(MeteredTimestampedKeyValueStore.class));
    }

    @Test
    public void shouldHaveChangeLoggingStoreByDefault() {
        final TimestampedKeyValueStore<String, String> store = builder.build();
        MatcherAssert.assertThat(store, IsInstanceOf.instanceOf(MeteredTimestampedKeyValueStore.class));
        final StateStore next = wrapped();
        MatcherAssert.assertThat(next, IsInstanceOf.instanceOf(ChangeLoggingTimestampedKeyValueBytesStore.class));
    }

    @Test
    public void shouldNotHaveChangeLoggingStoreWhenDisabled() {
        final TimestampedKeyValueStore<String, String> store = builder.withLoggingDisabled().build();
        final StateStore next = wrapped();
        MatcherAssert.assertThat(next, CoreMatchers.equalTo(inner));
    }

    @Test
    public void shouldHaveCachingStoreWhenEnabled() {
        final TimestampedKeyValueStore<String, String> store = builder.withCachingEnabled().build();
        final StateStore wrapped = wrapped();
        MatcherAssert.assertThat(store, IsInstanceOf.instanceOf(MeteredTimestampedKeyValueStore.class));
        MatcherAssert.assertThat(wrapped, IsInstanceOf.instanceOf(CachingKeyValueStore.class));
    }

    @Test
    public void shouldHaveChangeLoggingStoreWhenLoggingEnabled() {
        final TimestampedKeyValueStore<String, String> store = builder.withLoggingEnabled(Collections.emptyMap()).build();
        final StateStore wrapped = wrapped();
        MatcherAssert.assertThat(store, IsInstanceOf.instanceOf(MeteredTimestampedKeyValueStore.class));
        MatcherAssert.assertThat(wrapped, IsInstanceOf.instanceOf(ChangeLoggingTimestampedKeyValueBytesStore.class));
        MatcherAssert.assertThat(((WrappedStateStore) (wrapped)).wrapped(), CoreMatchers.equalTo(inner));
    }

    @Test
    public void shouldHaveCachingAndChangeLoggingWhenBothEnabled() {
        final TimestampedKeyValueStore<String, String> store = builder.withLoggingEnabled(Collections.emptyMap()).withCachingEnabled().build();
        final WrappedStateStore caching = ((WrappedStateStore) (((WrappedStateStore) (store)).wrapped()));
        final WrappedStateStore changeLogging = ((WrappedStateStore) (caching.wrapped()));
        MatcherAssert.assertThat(store, IsInstanceOf.instanceOf(MeteredTimestampedKeyValueStore.class));
        MatcherAssert.assertThat(caching, IsInstanceOf.instanceOf(CachingKeyValueStore.class));
        MatcherAssert.assertThat(changeLogging, IsInstanceOf.instanceOf(ChangeLoggingTimestampedKeyValueBytesStore.class));
        MatcherAssert.assertThat(changeLogging.wrapped(), CoreMatchers.equalTo(inner));
    }

    @SuppressWarnings("all")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerIfInnerIsNull() {
        new TimestampedKeyValueStoreBuilder(null, Serdes.String(), Serdes.String(), new MockTime());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerIfKeySerdeIsNull() {
        new TimestampedKeyValueStoreBuilder(supplier, null, Serdes.String(), new MockTime());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerIfValueSerdeIsNull() {
        new TimestampedKeyValueStoreBuilder(supplier, Serdes.String(), null, new MockTime());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerIfTimeIsNull() {
        new TimestampedKeyValueStoreBuilder(supplier, Serdes.String(), Serdes.String(), null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerIfMetricsScopeIsNull() {
        new TimestampedKeyValueStoreBuilder(supplier, Serdes.String(), Serdes.String(), new MockTime());
    }
}

