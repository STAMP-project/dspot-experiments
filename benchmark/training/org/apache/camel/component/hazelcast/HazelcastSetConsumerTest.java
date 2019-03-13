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


import HazelcastConstants.ADDED;
import HazelcastConstants.REMOVED;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.core.ItemListener;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastSetConsumerTest extends HazelcastCamelTestSupport {
    @Mock
    private ISet<String> set;

    @Captor
    private ArgumentCaptor<ItemListener<String>> argument;

    @Test
    public void add() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:added");
        out.expectedMessageCount(1);
        Mockito.verify(set).addItemListener(argument.capture(), ArgumentMatchers.eq(true));
        final ItemEvent<String> event = new ItemEvent("mm", ItemEventType.ADDED, "foo", null);
        argument.getValue().itemAdded(event);
        assertMockEndpointsSatisfied(2000, TimeUnit.MILLISECONDS);
        this.checkHeaders(out.getExchanges().get(0).getIn().getHeaders(), ADDED);
    }

    @Test
    public void remove() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:removed");
        out.expectedMessageCount(1);
        Mockito.verify(set).addItemListener(argument.capture(), ArgumentMatchers.eq(true));
        final ItemEvent<String> event = new ItemEvent("mm", ItemEventType.REMOVED, "foo", null);
        argument.getValue().itemRemoved(event);
        assertMockEndpointsSatisfied(2000, TimeUnit.MILLISECONDS);
        this.checkHeaders(out.getExchanges().get(0).getIn().getHeaders(), REMOVED);
    }
}

