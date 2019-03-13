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
package org.apache.camel.component.jms;


import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit test for Camel loadbalancer failover with JMS
 */
public class JmsLoadBalanceFailoverWithForceSendOriginalJmsMessageTest extends CamelTestSupport {
    private boolean forceSendOriginalMessage = true;

    @Test
    public void testFailover() throws Exception {
        MockEndpoint oneMock = getMockEndpoint("mock:one");
        MockEndpoint threeMock = getMockEndpoint("mock:three");
        MockEndpoint twoMock = getMockEndpoint("mock:two");
        MockEndpoint resultMock = getMockEndpoint("mock:result");
        oneMock.expectedMessageCount(1);
        oneMock.expectedHeaderReceived("foo", "bar");
        twoMock.expectedMessageCount(1);
        twoMock.expectedHeaderReceived("foo", "bar");
        threeMock.expectedMessageCount(0);
        resultMock.expectedMessageCount(1);
        resultMock.expectedHeaderReceived("foo", "bar");
        String out = template.requestBodyAndHeader("jms:queue:start", "Hello World", "foo", "bar", String.class);
        assertEquals("Hello Back", out);
        assertMockEndpointsSatisfied();
        // we should get an ActiveMQTextMessage with the body and custom header intact
        assertEquals(ActiveMQTextMessage.class, oneMock.getExchanges().get(0).getIn().getBody().getClass());
        assertEquals("Hello World", getText());
        assertEquals("bar", getStringProperty("foo"));
        assertEquals(ActiveMQTextMessage.class, twoMock.getExchanges().get(0).getIn().getBody().getClass());
        assertEquals("Hello World", getText());
        assertEquals("bar", getStringProperty("foo"));
        // reset mocks
        oneMock.reset();
        twoMock.reset();
        threeMock.reset();
        resultMock.reset();
        // the round robin should now be at three so one and two should be skipped
        oneMock.expectedMessageCount(0);
        twoMock.expectedMessageCount(0);
        threeMock.expectedMessageCount(1);
        threeMock.expectedHeaderReceived("foo", "bar");
        resultMock.expectedMessageCount(1);
        resultMock.expectedHeaderReceived("foo", "bar");
        out = template.requestBodyAndHeader("jms:queue:start", "Hello World", "foo", "bar", String.class);
        assertEquals("Bye World", out);
        assertMockEndpointsSatisfied();
        assertEquals(ActiveMQTextMessage.class, threeMock.getExchanges().get(0).getIn().getBody().getClass());
        assertEquals("Hello World", getText());
        assertEquals("bar", getStringProperty("foo"));
    }
}

