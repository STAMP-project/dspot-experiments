/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.hazelcast;


import EntryEventType.ADDED;
import EntryEventType.EVICTED;
import EntryEventType.REMOVED;
import EntryEventType.UPDATED;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.hazelcast.listener.MapEntryListener;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastMapConsumerTest extends HazelcastCamelTestSupport {
    @Mock
    private IMap<Object, Object> map;

    @Captor
    private ArgumentCaptor<MapEntryListener<Object, Object>> argument;

    @Test
    public void testAdd() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:added");
        out.expectedMessageCount(1);
        Mockito.verify(map).addEntryListener(argument.capture(), ArgumentMatchers.eq(true));
        EntryEvent<Object, Object> event = new EntryEvent("foo", null, ADDED.getType(), "4711", "my-foo");
        argument.getValue().entryAdded(event);
        assertMockEndpointsSatisfied(5000, TimeUnit.MILLISECONDS);
        this.checkHeaders(out.getExchanges().get(0).getIn().getHeaders(), HazelcastConstants.ADDED);
    }

    @Test
    public void testEnict() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:evicted");
        out.expectedMessageCount(1);
        Mockito.verify(map).addEntryListener(argument.capture(), ArgumentMatchers.eq(true));
        EntryEvent<Object, Object> event = new EntryEvent("foo", null, EVICTED.getType(), "4711", "my-foo");
        argument.getValue().entryEvicted(event);
        assertMockEndpointsSatisfied(30000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testUpdate() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:updated");
        out.expectedMessageCount(1);
        Mockito.verify(map).addEntryListener(argument.capture(), ArgumentMatchers.eq(true));
        EntryEvent<Object, Object> event = new EntryEvent("foo", null, UPDATED.getType(), "4711", "my-foo");
        argument.getValue().entryUpdated(event);
        assertMockEndpointsSatisfied(5000, TimeUnit.MILLISECONDS);
        this.checkHeaders(out.getExchanges().get(0).getIn().getHeaders(), HazelcastConstants.UPDATED);
    }

    @Test
    public void testEvict() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:evicted");
        out.expectedMessageCount(1);
        Mockito.verify(map).addEntryListener(argument.capture(), ArgumentMatchers.eq(true));
        EntryEvent<Object, Object> event = new EntryEvent("foo", null, EVICTED.getType(), "4711", "my-foo");
        argument.getValue().entryEvicted(event);
        assertMockEndpointsSatisfied(5000, TimeUnit.MILLISECONDS);
        this.checkHeaders(out.getExchanges().get(0).getIn().getHeaders(), HazelcastConstants.EVICTED);
    }

    @Test
    public void testRemove() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:removed");
        out.expectedMessageCount(1);
        Mockito.verify(map).addEntryListener(argument.capture(), ArgumentMatchers.eq(true));
        EntryEvent<Object, Object> event = new EntryEvent("foo", null, REMOVED.getType(), "4711", "my-foo");
        argument.getValue().entryRemoved(event);
        assertMockEndpointsSatisfied(5000, TimeUnit.MILLISECONDS);
        this.checkHeaders(out.getExchanges().get(0).getIn().getHeaders(), HazelcastConstants.REMOVED);
    }
}

