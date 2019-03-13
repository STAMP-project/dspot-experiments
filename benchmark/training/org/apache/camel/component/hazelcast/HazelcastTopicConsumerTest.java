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


import HazelcastConstants.RECEIVED;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastTopicConsumerTest extends HazelcastCamelTestSupport {
    @Mock
    private ITopic<String> topic;

    @Captor
    private ArgumentCaptor<MessageListener<String>> argument;

    @Test
    public void receive() throws InterruptedException {
        MockEndpoint out = getMockEndpoint("mock:received");
        out.expectedMessageCount(1);
        Mockito.verify(topic).addMessageListener(argument.capture());
        final Message<String> msg = new Message("foo", "foo", new Date().getTime(), null);
        argument.getValue().onMessage(msg);
        assertMockEndpointsSatisfied(2000, TimeUnit.MILLISECONDS);
        this.checkHeaders(out.getExchanges().get(0).getIn().getHeaders(), RECEIVED);
    }
}

