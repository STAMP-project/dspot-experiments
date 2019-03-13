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
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class KeyValueIteratorFacadeTest {
    @Mock
    private KeyValueIterator<String, ValueAndTimestamp<String>> mockedKeyValueIterator;

    private KeyValueIteratorFacade<String, String> keyValueIteratorFacade;

    @Test
    public void shouldForwardHasNext() {
        expect(mockedKeyValueIterator.hasNext()).andReturn(true).andReturn(false);
        replay(mockedKeyValueIterator);
        Assert.assertTrue(keyValueIteratorFacade.hasNext());
        Assert.assertFalse(keyValueIteratorFacade.hasNext());
        verify(mockedKeyValueIterator);
    }

    @Test
    public void shouldForwardPeekNextKey() {
        expect(mockedKeyValueIterator.peekNextKey()).andReturn("key");
        replay(mockedKeyValueIterator);
        MatcherAssert.assertThat(keyValueIteratorFacade.peekNextKey(), Matchers.is("key"));
        verify(mockedKeyValueIterator);
    }

    @Test
    public void shouldReturnPlainKeyValuePairOnGet() {
        expect(mockedKeyValueIterator.next()).andReturn(new KeyValue("key", ValueAndTimestamp.make("value", 42L)));
        replay(mockedKeyValueIterator);
        MatcherAssert.assertThat(keyValueIteratorFacade.next(), Matchers.is(KeyValue.pair("key", "value")));
        verify(mockedKeyValueIterator);
    }

    @Test
    public void shouldCloseInnerIterator() {
        mockedKeyValueIterator.close();
        expectLastCall();
        replay(mockedKeyValueIterator);
        keyValueIteratorFacade.close();
        verify(mockedKeyValueIterator);
    }
}

