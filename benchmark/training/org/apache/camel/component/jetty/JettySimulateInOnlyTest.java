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
package org.apache.camel.component.jetty;


import ExchangePattern.InOnly;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class JettySimulateInOnlyTest extends BaseJettyTest {
    private static String route = "";

    @Test
    public void testSimulateInOnlyUsingWireTap() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // START SNIPPET: e1
                // and then construct a canned empty response
                // turn the route to in only as we do not want jetty to wait for the response
                // we can do this using the wiretap EIP pattern
                from("jetty://http://localhost:{{port}}/myserver").wireTap("direct:continue").transform(constant("OK"));
                from("direct:continue").delay(1500).process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        JettySimulateInOnlyTest.route += "B";
                    }
                }).to("mock:result");
                // END SNIPPET: e1
            }
        });
        context.start();
        JettySimulateInOnlyTest.route = "";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived("foo", "bar");
        String reply = template.requestBody("http://localhost:{{port}}/myserver?foo=bar", null, String.class);
        JettySimulateInOnlyTest.route += "A";
        assertEquals("OK", reply);
        assertMockEndpointsSatisfied();
        assertEquals("AB", JettySimulateInOnlyTest.route);
    }

    @Test
    public void testSimulateInOnly() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // and then construct a canned empty response
                // turn the route to in only as we do not want jetty to wait for the response
                // we can do this by changing the MEP and sending to a seda endpoint to spin off
                // a new thread continue doing the routing
                from("jetty://http://localhost:{{port}}/myserver").setExchangePattern(InOnly).to("seda:continue").transform(constant("OK"));
                from("seda:continue").delay(1000).process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        JettySimulateInOnlyTest.route += "B";
                    }
                }).to("mock:result");
            }
        });
        context.start();
        JettySimulateInOnlyTest.route = "";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived("foo", "bar");
        String reply = template.requestBody("http://localhost:{{port}}/myserver?foo=bar", null, String.class);
        JettySimulateInOnlyTest.route += "A";
        assertEquals("OK", reply);
        assertMockEndpointsSatisfied();
        assertEquals("AB", JettySimulateInOnlyTest.route);
    }
}

