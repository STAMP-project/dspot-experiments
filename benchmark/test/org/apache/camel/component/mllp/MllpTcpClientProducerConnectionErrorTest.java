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
import org.apache.camel.test.junit.rule.mllp.MllpServerResource;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.mllp.Hl7TestMessageGenerator;
import org.junit.Rule;
import org.junit.Test;


public class MllpTcpClientProducerConnectionErrorTest extends CamelTestSupport {
    @Rule
    public MllpServerResource mllpServer = new MllpServerResource("localhost", AvailablePortFinder.getNextAvailable());

    @EndpointInject(uri = "direct://source")
    ProducerTemplate source;

    @EndpointInject(uri = "mock://target")
    MockEndpoint target;

    @EndpointInject(uri = "mock://complete")
    MockEndpoint complete;

    @EndpointInject(uri = "mock://write-ex")
    MockEndpoint writeEx;

    @EndpointInject(uri = "mock://connect-ex")
    MockEndpoint connectEx;

    @EndpointInject(uri = "mock://acknowledgement-ex")
    MockEndpoint acknowledgementEx;

    /**
     * The component should reconnect, so the route shouldn't see any errors.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionClosedBeforeSendingHL7Message() throws Exception {
        target.expectedMessageCount(2);
        complete.expectedMessageCount(2);
        connectEx.expectedMessageCount(0);
        writeEx.expectedMessageCount(0);
        acknowledgementEx.expectedMessageCount(0);
        NotifyBuilder oneDone = whenCompleted(1).create();
        NotifyBuilder twoDone = whenCompleted(2).create();
        // Need to send one message to get the connection established
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed an exchange", oneDone.matches(5, TimeUnit.SECONDS));
        mllpServer.closeClientConnections();
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed two exchanges", twoDone.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
    }

    /**
     * The component should reconnect, so the route shouldn't see any errors.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionResetBeforeSendingHL7Message() throws Exception {
        target.expectedMessageCount(2);
        complete.expectedMessageCount(2);
        connectEx.expectedMessageCount(0);
        writeEx.expectedMessageCount(0);
        acknowledgementEx.expectedMessageCount(0);
        NotifyBuilder oneDone = whenCompleted(1).create();
        NotifyBuilder twoDone = whenCompleted(2).create();
        // Need to send one message to get the connection established
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed an exchange", oneDone.matches(5, TimeUnit.SECONDS));
        mllpServer.resetClientConnections();
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed two exchanges", twoDone.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
    }

    @Test
    public void testConnectionClosedBeforeReadingAcknowledgement() throws Exception {
        target.expectedMessageCount(0);
        complete.expectedMessageCount(1);
        connectEx.expectedMessageCount(0);
        writeEx.expectedMessageCount(0);
        acknowledgementEx.expectedMessageCount(1);
        mllpServer.setCloseSocketBeforeAcknowledgementModulus(1);
        NotifyBuilder done = whenCompleted(1).create();
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed an exchange", done.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
    }

    @Test
    public void testConnectionResetBeforeReadingAcknowledgement() throws Exception {
        target.expectedMessageCount(0);
        complete.expectedMessageCount(1);
        connectEx.expectedMessageCount(0);
        writeEx.expectedMessageCount(0);
        acknowledgementEx.expectedMessageCount(1);
        mllpServer.setResetSocketBeforeAcknowledgementModulus(1);
        NotifyBuilder done = whenCompleted(1).create();
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed an exchange", done.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
    }

    @Test
    public void testServerShutdownBeforeSendingHL7Message() throws Exception {
        target.expectedMessageCount(1);
        complete.expectedMessageCount(2);
        connectEx.expectedMessageCount(0);
        NotifyBuilder done = whenCompleted(2).create();
        // Need to send one message to get the connection established
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        mllpServer.shutdown();
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed an exchange", done.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
        // Depending on the timing, either a write or a receive exception will be thrown
        assertEquals("Either a write or a receive exception should have been be thrown", 1, ((writeEx.getExchanges().size()) + (acknowledgementEx.getExchanges().size())));
    }

    @Test
    public void testConnectionCloseAndServerShutdownBeforeSendingHL7Message() throws Exception {
        target.expectedMessageCount(1);
        complete.expectedMessageCount(2);
        connectEx.expectedMessageCount(0);
        NotifyBuilder done = whenCompleted(2).create();
        // Need to send one message to get the connection established
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        mllpServer.closeClientConnections();
        mllpServer.shutdown();
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed an exchange", done.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
        // Depending on the timing, either a write or a receive exception will be thrown
        assertEquals("Either a write or a receive exception should have been be thrown", 1, ((writeEx.getExchanges().size()) + (acknowledgementEx.getExchanges().size())));
    }

    @Test
    public void testConnectionResetAndServerShutdownBeforeSendingHL7Message() throws Exception {
        target.expectedMessageCount(1);
        complete.expectedMessageCount(2);
        connectEx.expectedMessageCount(0);
        writeEx.expectedMessageCount(1);
        acknowledgementEx.expectedMessageCount(0);
        NotifyBuilder done = whenCompleted(2).create();
        // Need to send one message to get the connection established
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        mllpServer.resetClientConnections();
        mllpServer.shutdown();
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
        assertTrue("Should have completed an exchange", done.matches(5, TimeUnit.SECONDS));
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
    }
}

