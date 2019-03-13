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
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ChangeLoggingKeyValueBytesStoreTest {
    private final InMemoryKeyValueStore inner = new InMemoryKeyValueStore("kv");

    private final ChangeLoggingKeyValueBytesStore store = new ChangeLoggingKeyValueBytesStore(inner);

    private final Map<Object, Object> sent = new HashMap<>();

    private final Bytes hi = Bytes.wrap("hi".getBytes());

    private final Bytes hello = Bytes.wrap("hello".getBytes());

    private final byte[] there = "there".getBytes();

    private final byte[] world = "world".getBytes();

    @Test
    public void shouldWriteKeyValueBytesToInnerStoreOnPut() {
        store.put(hi, there);
        MatcherAssert.assertThat(inner.get(hi), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldLogChangeOnPut() {
        store.put(hi, there);
        MatcherAssert.assertThat(sent.get(hi), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldWriteAllKeyValueToInnerStoreOnPutAll() {
        store.putAll(Arrays.asList(KeyValue.pair(hi, there), KeyValue.pair(hello, world)));
        MatcherAssert.assertThat(inner.get(hi), CoreMatchers.equalTo(there));
        MatcherAssert.assertThat(inner.get(hello), CoreMatchers.equalTo(world));
    }

    @Test
    public void shouldLogChangesOnPutAll() {
        store.putAll(Arrays.asList(KeyValue.pair(hi, there), KeyValue.pair(hello, world)));
        MatcherAssert.assertThat(sent.get(hi), CoreMatchers.equalTo(there));
        MatcherAssert.assertThat(sent.get(hello), CoreMatchers.equalTo(world));
    }

    @Test
    public void shouldPropagateDelete() {
        store.put(hi, there);
        store.delete(hi);
        MatcherAssert.assertThat(inner.approximateNumEntries(), CoreMatchers.equalTo(0L));
        MatcherAssert.assertThat(inner.get(hi), CoreMatchers.nullValue());
    }

    @Test
    public void shouldReturnOldValueOnDelete() {
        store.put(hi, there);
        MatcherAssert.assertThat(store.delete(hi), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldLogKeyNullOnDelete() {
        store.put(hi, there);
        store.delete(hi);
        MatcherAssert.assertThat(sent.containsKey(hi), CoreMatchers.is(true));
        MatcherAssert.assertThat(sent.get(hi), CoreMatchers.nullValue());
    }

    @Test
    public void shouldWriteToInnerOnPutIfAbsentNoPreviousValue() {
        store.putIfAbsent(hi, there);
        MatcherAssert.assertThat(inner.get(hi), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldNotWriteToInnerOnPutIfAbsentWhenValueForKeyExists() {
        store.put(hi, there);
        store.putIfAbsent(hi, world);
        MatcherAssert.assertThat(inner.get(hi), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldWriteToChangelogOnPutIfAbsentWhenNoPreviousValue() {
        store.putIfAbsent(hi, there);
        MatcherAssert.assertThat(sent.get(hi), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldNotWriteToChangeLogOnPutIfAbsentWhenValueForKeyExists() {
        store.put(hi, there);
        store.putIfAbsent(hi, world);
        MatcherAssert.assertThat(sent.get(hi), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldReturnCurrentValueOnPutIfAbsent() {
        store.put(hi, there);
        MatcherAssert.assertThat(store.putIfAbsent(hi, world), CoreMatchers.equalTo(there));
    }

    @Test
    public void shouldReturnNullOnPutIfAbsentWhenNoPreviousValue() {
        MatcherAssert.assertThat(store.putIfAbsent(hi, there), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldReturnValueOnGetWhenExists() {
        store.put(hello, world);
        MatcherAssert.assertThat(store.get(hello), CoreMatchers.equalTo(world));
    }

    @Test
    public void shouldReturnNullOnGetWhenDoesntExist() {
        MatcherAssert.assertThat(store.get(hello), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

