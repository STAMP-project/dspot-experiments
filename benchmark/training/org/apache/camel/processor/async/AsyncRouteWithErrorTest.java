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
package org.apache.camel.processor.async;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Assert;
import org.junit.Test;


public class AsyncRouteWithErrorTest extends ContextTestSupport {
    private static String route = "";

    @Test
    public void testAsyncRouteWithError() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        // send a request reply to the direct start endpoint
        try {
            template.requestBody("direct:start", "Hello");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            // expected an execution exception
            Assert.assertEquals("Damn forced by unit test", e.getCause().getMessage());
        }
        // we should run before the async processor that sets B
        AsyncRouteWithErrorTest.route += "A";
        assertMockEndpointsSatisfied();
        Assert.assertEquals("BA", AsyncRouteWithErrorTest.route);
    }

    @Test
    public void testAsyncRouteWithTypeConverted() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        // send a request reply to the direct start endpoint, but will use
        // future type converter that will wait for the response
        try {
            template.requestBody("direct:start", "Hello", String.class);
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            // expected an execution exception
            Assert.assertEquals("Damn forced by unit test", e.getCause().getMessage());
        }
        // we should wait for the async response as we ask for the result as a String body
        AsyncRouteWithErrorTest.route += "A";
        assertMockEndpointsSatisfied();
        Assert.assertEquals("BA", AsyncRouteWithErrorTest.route);
    }

    public static class MyProcessor implements Processor {
        public MyProcessor() {
        }

        public void process(Exchange exchange) throws Exception {
            AsyncRouteWithErrorTest.route += "B";
            Assert.assertEquals("Hello World", exchange.getIn().getBody());
            throw new IllegalArgumentException("Damn forced by unit test");
        }
    }
}

