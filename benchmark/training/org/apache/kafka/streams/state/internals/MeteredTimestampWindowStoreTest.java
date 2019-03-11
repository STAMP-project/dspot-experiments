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


import Sensor.RecordingLevel.DEBUG;
import org.apache.kafka.common.metrics.MetricConfig;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class MeteredTimestampWindowStoreTest {
    private InternalMockProcessorContext context;

    @SuppressWarnings("unchecked")
    private final WindowStore<Bytes, byte[]> innerStoreMock = EasyMock.createNiceMock(WindowStore.class);

    private final MeteredTimestampedWindowStore<String, String> store = // any size
    new MeteredTimestampedWindowStore(innerStoreMock, 10L, "scope", new MockTime(), Serdes.String(), new ValueAndTimestampSerde(new SerdeThatDoesntHandleNull()));

    private final Metrics metrics = new Metrics(new MetricConfig().recordLevel(DEBUG));

    {
        EasyMock.expect(innerStoreMock.name()).andReturn("mocked-store").anyTimes();
    }

    @Test
    public void shouldCloseUnderlyingStore() {
        innerStoreMock.close();
        EasyMock.expectLastCall();
        EasyMock.replay(innerStoreMock);
        store.init(context, store);
        store.close();
        EasyMock.verify(innerStoreMock);
    }

    @Test
    public void shouldNotExceptionIfFetchReturnsNull() {
        EasyMock.expect(innerStoreMock.fetch(Bytes.wrap("a".getBytes()), 0)).andReturn(null);
        EasyMock.replay(innerStoreMock);
        store.init(context, store);
        Assert.assertNull(store.fetch("a", 0));
    }
}

