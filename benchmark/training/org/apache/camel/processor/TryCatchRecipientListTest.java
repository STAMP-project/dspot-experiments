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
package org.apache.camel.processor;


import Exchange.EXCEPTION_CAUGHT;
import org.apache.camel.ContextTestSupport;
import org.junit.Test;


public class TryCatchRecipientListTest extends ContextTestSupport {
    @Test
    public void testTryCatchTo() throws Exception {
        context.addRoutes(createTryCatchToRouteBuilder());
        context.start();
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedBodiesReceived("doCatch");
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        getMockEndpoint("mock:catch").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:catch").message(0).exchangeProperty(EXCEPTION_CAUGHT).isInstanceOf(IllegalArgumentException.class);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTryCatchRecipientList() throws Exception {
        context.addRoutes(createTryCatchRecipientListRouteBuilder());
        context.start();
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedBodiesReceived("doCatch");
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        getMockEndpoint("mock:catch").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:catch").message(0).exchangeProperty(EXCEPTION_CAUGHT).isInstanceOf(IllegalArgumentException.class);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDualTryCatchRecipientList() throws Exception {
        context.addRoutes(createDualTryCatchRecipientListRouteBuilder());
        context.start();
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:bar").expectedBodiesReceived("doCatch");
        getMockEndpoint("mock:result").expectedBodiesReceived("doCatch");
        getMockEndpoint("mock:result2").expectedBodiesReceived("doCatch2");
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        getMockEndpoint("mock:catch").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:catch").message(0).exchangeProperty(EXCEPTION_CAUGHT).isInstanceOf(IllegalArgumentException.class);
        getMockEndpoint("mock:catch2").expectedBodiesReceived("doCatch");
        getMockEndpoint("mock:catch2").message(0).exchangeProperty(EXCEPTION_CAUGHT).isInstanceOf(IllegalArgumentException.class);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTo() throws Exception {
        context.addRoutes(createToRouteBuilder());
        context.start();
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        getMockEndpoint("mock:dead").message(0).exchangeProperty(EXCEPTION_CAUGHT).isInstanceOf(IllegalArgumentException.class);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRecipientList() throws Exception {
        context.addRoutes(createRecipientListRouteBuilder());
        context.start();
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        getMockEndpoint("mock:dead").message(0).exchangeProperty(EXCEPTION_CAUGHT).isInstanceOf(IllegalArgumentException.class);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }
}

