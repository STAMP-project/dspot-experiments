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
package org.apache.kafka.streams.internals;


import ConsumerRecord.NO_TIMESTAMP;
import java.util.Arrays;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class KeyValueStoreFacadeTest {
    @Mock
    private TimestampedKeyValueStore<String, String> mockedKeyValueTimestampStore;

    @Mock
    private KeyValueIterator<String, ValueAndTimestamp<String>> mockedKeyValueTimestampIterator;

    private KeyValueStoreFacade<String, String> keyValueStoreFacade;

    @Test
    public void shouldForwardInit() {
        final ProcessorContext context = mock(ProcessorContext.class);
        final StateStore store = mock(StateStore.class);
        mockedKeyValueTimestampStore.init(context, store);
        expectLastCall();
        replay(mockedKeyValueTimestampStore);
        keyValueStoreFacade.init(context, store);
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldPutWithUnknownTimestamp() {
        mockedKeyValueTimestampStore.put("key", ValueAndTimestamp.make("value", NO_TIMESTAMP));
        expectLastCall();
        replay(mockedKeyValueTimestampStore);
        keyValueStoreFacade.put("key", "value");
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldPutIfAbsentWithUnknownTimestamp() {
        expect(mockedKeyValueTimestampStore.putIfAbsent("key", ValueAndTimestamp.make("value", NO_TIMESTAMP))).andReturn(null).andReturn(ValueAndTimestamp.make("oldValue", 42L));
        replay(mockedKeyValueTimestampStore);
        Assert.assertNull(keyValueStoreFacade.putIfAbsent("key", "value"));
        MatcherAssert.assertThat(keyValueStoreFacade.putIfAbsent("key", "value"), CoreMatchers.is("oldValue"));
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldPutAllWithUnknownTimestamp() {
        mockedKeyValueTimestampStore.put("key1", ValueAndTimestamp.make("value1", NO_TIMESTAMP));
        mockedKeyValueTimestampStore.put("key2", ValueAndTimestamp.make("value2", NO_TIMESTAMP));
        expectLastCall();
        replay(mockedKeyValueTimestampStore);
        keyValueStoreFacade.putAll(Arrays.asList(KeyValue.pair("key1", "value1"), KeyValue.pair("key2", "value2")));
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldDeleteAndReturnPlainValue() {
        expect(mockedKeyValueTimestampStore.delete("key")).andReturn(null).andReturn(ValueAndTimestamp.make("oldValue", 42L));
        replay(mockedKeyValueTimestampStore);
        Assert.assertNull(keyValueStoreFacade.delete("key"));
        MatcherAssert.assertThat(keyValueStoreFacade.delete("key"), CoreMatchers.is("oldValue"));
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldForwardFlush() {
        mockedKeyValueTimestampStore.flush();
        expectLastCall();
        replay(mockedKeyValueTimestampStore);
        keyValueStoreFacade.flush();
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldForwardClose() {
        mockedKeyValueTimestampStore.close();
        expectLastCall();
        replay(mockedKeyValueTimestampStore);
        keyValueStoreFacade.close();
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldReturnName() {
        expect(mockedKeyValueTimestampStore.name()).andReturn("name");
        replay(mockedKeyValueTimestampStore);
        MatcherAssert.assertThat(keyValueStoreFacade.name(), CoreMatchers.is("name"));
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldReturnIsPersistent() {
        expect(mockedKeyValueTimestampStore.persistent()).andReturn(true).andReturn(false);
        replay(mockedKeyValueTimestampStore);
        MatcherAssert.assertThat(keyValueStoreFacade.persistent(), CoreMatchers.is(true));
        MatcherAssert.assertThat(keyValueStoreFacade.persistent(), CoreMatchers.is(false));
        verify(mockedKeyValueTimestampStore);
    }

    @Test
    public void shouldReturnIsOpen() {
        expect(mockedKeyValueTimestampStore.isOpen()).andReturn(true).andReturn(false);
        replay(mockedKeyValueTimestampStore);
        MatcherAssert.assertThat(keyValueStoreFacade.isOpen(), CoreMatchers.is(true));
        MatcherAssert.assertThat(keyValueStoreFacade.isOpen(), CoreMatchers.is(false));
        verify(mockedKeyValueTimestampStore);
    }
}

