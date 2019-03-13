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
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MultiMap;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastMultimapConsumerTest extends HazelcastCamelTestSupport {
    @Mock
    private MultiMap<Object, Object> map;

    @Captor
    private ArgumentCaptor<EntryListener<Object, Object>> argument;

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

    /* mail from talip (hazelcast) on 21.02.2011: MultiMap doesn't support eviction yet. We can and should add this feature. */
    @Test
    public void testEvict() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:evicted");
        out.expectedMessageCount(1);
        Mockito.verify(map).addEntryListener(argument.capture(), ArgumentMatchers.eq(true));
        EntryEvent<Object, Object> event = new EntryEvent("foo", null, EVICTED.getType(), "4711", "my-foo");
        argument.getValue().entryEvicted(event);
        assertMockEndpointsSatisfied(30000, TimeUnit.MILLISECONDS);
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

