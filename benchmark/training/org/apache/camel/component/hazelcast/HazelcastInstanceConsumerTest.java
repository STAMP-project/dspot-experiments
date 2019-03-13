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
import com.hazelcast.core.Cluster;
import com.hazelcast.core.IList;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastInstanceConsumerTest extends HazelcastCamelTestSupport {
    @Mock
    private IList<String> list;

    @Mock
    private Cluster cluster;

    @Mock
    private Member member;

    private ArgumentCaptor<MembershipListener> argument;

    @Test
    public void testAddInstance() throws InterruptedException {
        MockEndpoint added = getMockEndpoint("mock:added");
        added.setExpectedMessageCount(1);
        Mockito.when(member.getSocketAddress()).thenReturn(new InetSocketAddress("foo.bar", 12345));
        Mockito.verify(cluster).addMembershipListener(argument.capture());
        MembershipEvent event = new MembershipEvent(cluster, member, MembershipEvent.MEMBER_ADDED, null);
        argument.getValue().memberAdded(event);
        assertMockEndpointsSatisfied(5000, TimeUnit.MILLISECONDS);
        // check headers
        Exchange ex = added.getExchanges().get(0);
        Map<String, Object> headers = ex.getIn().getHeaders();
        this.checkHeaders(headers, ADDED);
    }

    @Test
    public void testRemoveInstance() throws InterruptedException {
        MockEndpoint removed = getMockEndpoint("mock:removed");
        removed.setExpectedMessageCount(1);
        Mockito.when(member.getSocketAddress()).thenReturn(new InetSocketAddress("foo.bar", 12345));
        Mockito.verify(cluster).addMembershipListener(argument.capture());
        MembershipEvent event = new MembershipEvent(cluster, member, MembershipEvent.MEMBER_REMOVED, null);
        argument.getValue().memberRemoved(event);
        assertMockEndpointsSatisfied(5000, TimeUnit.MILLISECONDS);
        // check headers
        Exchange ex = removed.getExchanges().get(0);
        Map<String, Object> headers = ex.getIn().getHeaders();
        this.checkHeaders(headers, REMOVED);
    }
}

