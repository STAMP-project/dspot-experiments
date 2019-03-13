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
package org.apache.camel.component.jms.issues;


import ExchangePattern.InOnly;
import ExchangePattern.InOut;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsInOnlyParameterTest extends CamelTestSupport {
    @Test
    public void testInOnly() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:result").message(0).header("JMSCorrelationID").isNull();
        getMockEndpoint("mock:result").message(0).exchangePattern().isEqualTo(InOut);
        getMockEndpoint("mock:in").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:in").message(0).header("JMSCorrelationID").isNull();
        getMockEndpoint("mock:in").message(0).exchangePattern().isEqualTo(InOnly);
        String out = template.requestBody("direct:start", "Hello World", String.class);
        assertEquals("Bye World", out);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testInOnlyWithJMSCorrelationID() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:result").message(0).header("JMSCorrelationID").isEqualTo("foobar");
        getMockEndpoint("mock:result").message(0).exchangePattern().isEqualTo(InOut);
        getMockEndpoint("mock:in").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:in").message(0).header("JMSCorrelationID").isEqualTo("foobar");
        getMockEndpoint("mock:in").message(0).exchangePattern().isEqualTo(InOnly);
        String out = template.requestBodyAndHeader("direct:start", "Hello World", "JMSCorrelationID", "foobar", String.class);
        assertEquals("Bye World", out);
        assertMockEndpointsSatisfied();
    }
}

