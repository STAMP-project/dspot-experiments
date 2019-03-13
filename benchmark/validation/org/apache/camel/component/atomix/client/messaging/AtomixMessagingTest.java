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
package org.apache.camel.component.atomix.client.messaging;


import AtomixClientConstants.CHANNEL_NAME;
import AtomixClientConstants.MEMBER_NAME;
import AtomixClientConstants.RESOURCE_ACTION;
import AtomixMessaging.Action.BROADCAST;
import AtomixMessaging.Action.DIRECT;
import java.util.UUID;
import org.apache.camel.EndpointInject;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.component.atomix.client.AtomixClientTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class AtomixMessagingTest extends AtomixClientTestSupport {
    private static final String NODE_NAME = UUID.randomUUID().toString();

    @EndpointInject(uri = "direct:start")
    private FluentProducerTemplate template;

    // ************************************
    // Test
    // ************************************
    @Test
    public void testMessaging() throws Exception {
        MockEndpoint mock1 = getMockEndpoint("mock:member-1");
        mock1.expectedMessageCount(2);
        mock1.expectedBodiesReceived("direct-message", "broadcast-message");
        MockEndpoint mock2 = getMockEndpoint("mock:member-2");
        mock2.expectedMessageCount(1);
        mock2.expectedBodiesReceived("broadcast-message");
        template.clearAll().withHeader(RESOURCE_ACTION, DIRECT).withHeader(MEMBER_NAME, "member-1").withHeader(CHANNEL_NAME, "channel").withBody("direct-message").send();
        template.clearAll().withHeader(RESOURCE_ACTION, BROADCAST).withHeader(CHANNEL_NAME, "channel").withBody("direct-message").send();
    }
}

