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


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ChangeLoggingTimestampedKeyValueBytesStoreTest {
    private final InMemoryKeyValueStore root = new InMemoryKeyValueStore("kv");

    private final ChangeLoggingTimestampedKeyValueBytesStore store = new ChangeLoggingTimestampedKeyValueBytesStore(root);

    private final Map<Object, ValueAndTimestamp<byte[]>> sent = new HashMap<>();

    private final Bytes hi = Bytes.wrap("hi".getBytes());

    private final Bytes hello = Bytes.wrap("hello".getBytes());

    private final ValueAndTimestamp<byte[]> there = ValueAndTimestamp.make("there".getBytes(), 97L);

    // timestamp is 97 what is ASCII of 'a'
    private final byte[] rawThere = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000athere".getBytes();

    private final ValueAndTimestamp<byte[]> world = ValueAndTimestamp.make("world".getBytes(), 98L);

    // timestamp is 98 what is ASCII of 'b'
    private final byte[] rawWorld = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000bworld".getBytes();

    @Test
    public void shouldWriteKeyValueBytesToInnerStoreOnPut() {
        store.put(hi, rawThere);
        MatcherAssert.assertThat(root.get(hi), CoreMatchers.equalTo(rawThere));
    }

    @Test
    public void shouldLogChangeOnPut() {
        store.put(hi, rawThere);
        final ValueAndTimestamp<byte[]> logged = sent.get(hi);
        MatcherAssert.assertThat(logged.value(), CoreMatchers.equalTo(there.value()));
        MatcherAssert.assertThat(logged.timestamp(), CoreMatchers.equalTo(there.timestamp()));
    }

    @Test
    public void shouldWriteAllKeyValueToInnerStoreOnPutAll() {
        store.putAll(Arrays.asList(KeyValue.pair(hi, rawThere), KeyValue.pair(hello, rawWorld)));
        MatcherAssert.assertThat(root.get(hi), CoreMatchers.equalTo(rawThere));
        MatcherAssert.assertThat(root.get(hello), CoreMatchers.equalTo(rawWorld));
    }

    @Test
    public void shouldLogChangesOnPutAll() {
        store.putAll(Arrays.asList(KeyValue.pair(hi, rawThere), KeyValue.pair(hello, rawWorld)));
        final ValueAndTimestamp<byte[]> logged = sent.get(hi);
        MatcherAssert.assertThat(logged.value(), CoreMatchers.equalTo(there.value()));
        MatcherAssert.assertThat(logged.timestamp(), CoreMatchers.equalTo(there.timestamp()));
        final ValueAndTimestamp<byte[]> logged2 = sent.get(hello);
        MatcherAssert.assertThat(logged2.value(), CoreMatchers.equalTo(world.value()));
        MatcherAssert.assertThat(logged2.timestamp(), CoreMatchers.equalTo(world.timestamp()));
    }

    @Test
    public void shouldPropagateDelete() {
        store.put(hi, rawThere);
        store.delete(hi);
        MatcherAssert.assertThat(root.approximateNumEntries(), CoreMatchers.equalTo(0L));
        MatcherAssert.assertThat(root.get(hi), CoreMatchers.nullValue());
    }

    @Test
    public void shouldReturnOldValueOnDelete() {
        store.put(hi, rawThere);
        MatcherAssert.assertThat(store.delete(hi), CoreMatchers.equalTo(rawThere));
    }

    @Test
    public void shouldLogKeyNullOnDelete() {
        store.put(hi, rawThere);
        store.delete(hi);
        MatcherAssert.assertThat(sent.containsKey(hi), CoreMatchers.is(true));
        MatcherAssert.assertThat(sent.get(hi), CoreMatchers.nullValue());
    }

    @Test
    public void shouldWriteToInnerOnPutIfAbsentNoPreviousValue() {
        store.putIfAbsent(hi, rawThere);
        MatcherAssert.assertThat(root.get(hi), CoreMatchers.equalTo(rawThere));
    }

    @Test
    public void shouldNotWriteToInnerOnPutIfAbsentWhenValueForKeyExists() {
        store.put(hi, rawThere);
        store.putIfAbsent(hi, rawWorld);
        MatcherAssert.assertThat(root.get(hi), CoreMatchers.equalTo(rawThere));
    }

    @Test
    public void shouldWriteToChangelogOnPutIfAbsentWhenNoPreviousValue() {
        store.putIfAbsent(hi, rawThere);
        final ValueAndTimestamp<byte[]> logged = sent.get(hi);
        MatcherAssert.assertThat(logged.value(), CoreMatchers.equalTo(there.value()));
        MatcherAssert.assertThat(logged.timestamp(), CoreMatchers.equalTo(there.timestamp()));
    }

    @Test
    public void shouldNotWriteToChangeLogOnPutIfAbsentWhenValueForKeyExists() {
        store.put(hi, rawThere);
        store.putIfAbsent(hi, rawWorld);
        final ValueAndTimestamp<byte[]> logged = sent.get(hi);
        MatcherAssert.assertThat(logged.value(), CoreMatchers.equalTo(there.value()));
        MatcherAssert.assertThat(logged.timestamp(), CoreMatchers.equalTo(there.timestamp()));
    }

    @Test
    public void shouldReturnCurrentValueOnPutIfAbsent() {
        store.put(hi, rawThere);
        MatcherAssert.assertThat(store.putIfAbsent(hi, rawWorld), CoreMatchers.equalTo(rawThere));
    }

    @Test
    public void shouldReturnNullOnPutIfAbsentWhenNoPreviousValue() {
        MatcherAssert.assertThat(store.putIfAbsent(hi, rawThere), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldReturnValueOnGetWhenExists() {
        store.put(hello, rawWorld);
        MatcherAssert.assertThat(store.get(hello), CoreMatchers.equalTo(rawWorld));
    }

    @Test
    public void shouldReturnNullOnGetWhenDoesntExist() {
        MatcherAssert.assertThat(store.get(hello), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

