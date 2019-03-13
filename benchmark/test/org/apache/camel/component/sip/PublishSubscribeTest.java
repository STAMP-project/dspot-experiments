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
package org.apache.camel.component.sip;


import Request.PUBLISH;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Test manually as CI server cannot run this test")
public class PublishSubscribeTest extends CamelTestSupport {
    private int port1;

    private int port2;

    private int port3;

    @EndpointInject(uri = "mock:neverland")
    private MockEndpoint unreachableEndpoint;

    @EndpointInject(uri = "mock:notification")
    private MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    private ProducerTemplate producerTemplate;

    @Test
    public void testPresenceAgentBasedPubSub() throws Exception {
        unreachableEndpoint.expectedMessageCount(0);
        resultEndpoint.expectedMinimumMessageCount(1);
        producerTemplate.sendBodyAndHeader(((("sip://agent@localhost:" + (port1)) + "?stackName=client&eventHeaderName=evtHdrName&eventId=evtid&fromUser=user2&fromHost=localhost&fromPort=") + (port3)), "EVENT_A", "REQUEST_METHOD", PUBLISH);
        assertMockEndpointsSatisfied();
    }
}

