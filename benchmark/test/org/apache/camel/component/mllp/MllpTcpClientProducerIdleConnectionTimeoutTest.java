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
package org.apache.camel.component.mllp;


import java.util.concurrent.TimeUnit;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit.rule.mllp.MllpJUnitResourceException;
import org.apache.camel.test.junit.rule.mllp.MllpServerResource;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.mllp.Hl7TestMessageGenerator;
import org.junit.Rule;
import org.junit.Test;


public class MllpTcpClientProducerIdleConnectionTimeoutTest extends CamelTestSupport {
    static final int CONNECT_TIMEOUT = 500;

    static final int RECEIVE_TIMEOUT = 1000;

    static final int READ_TIMEOUT = 500;

    static final int IDLE_TIMEOUT = (MllpTcpClientProducerIdleConnectionTimeoutTest.RECEIVE_TIMEOUT) * 3;

    @Rule
    public MllpServerResource mllpServer = new MllpServerResource("localhost", AvailablePortFinder.getNextAvailable());

    @EndpointInject(uri = "direct://source")
    ProducerTemplate source;

    @EndpointInject(uri = "mock://complete")
    MockEndpoint complete;

    @EndpointInject(uri = "mock://write-ex")
    MockEndpoint writeEx;

    @EndpointInject(uri = "mock://receive-ex")
    MockEndpoint receiveEx;

    @Test(expected = MllpJUnitResourceException.class)
    public void testIdleConnectionTimeout() throws Exception {
        complete.expectedMessageCount(2);
        writeEx.expectedMessageCount(0);
        receiveEx.expectedMessageCount(0);
        NotifyBuilder done = whenCompleted(2).create();
        // Need to send one message to get the connection established
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        Thread.sleep(((MllpTcpClientProducerIdleConnectionTimeoutTest.IDLE_TIMEOUT) / 2));
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed two exchanges", done.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
        Thread.sleep(((long) ((MllpTcpClientProducerIdleConnectionTimeoutTest.IDLE_TIMEOUT) * 1.1)));
        mllpServer.checkClientConnections();
    }

    @Test
    public void testReconnectAfterIdleConnectionTimeout() throws Exception {
        complete.expectedMessageCount(3);
        writeEx.expectedMessageCount(0);
        receiveEx.expectedMessageCount(0);
        NotifyBuilder done = whenCompleted(2).create();
        // Need to send one message to get the connection established
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        Thread.sleep(((MllpTcpClientProducerIdleConnectionTimeoutTest.IDLE_TIMEOUT) / 2));
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed two exchanges", done.matches(5, TimeUnit.SECONDS));
        Thread.sleep(((long) ((MllpTcpClientProducerIdleConnectionTimeoutTest.IDLE_TIMEOUT) * 1.1)));
        try {
            mllpServer.checkClientConnections();
            fail("Should receive and exception for the closed connection");
        } catch (MllpJUnitResourceException expectedEx) {
            // Eat this
        }
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
        log.debug("Breakpoint");
    }
}

