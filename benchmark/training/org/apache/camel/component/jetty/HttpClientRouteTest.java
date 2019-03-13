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


import Exchange.HTTP_URI;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class HttpClientRouteTest extends BaseJettyTest {
    private int port1;

    private int port2;

    @Test
    public void testHttpRouteWithMessageHeader() throws Exception {
        testHttpClient("direct:start");
    }

    @Test
    public void testHttpRouteWithOption() throws Exception {
        testHttpClient("direct:start2");
    }

    @Test
    public void testHttpRouteWithQuery() throws Exception {
        MockEndpoint mockEndpoint = getMockEndpoint("mock:a");
        mockEndpoint.expectedBodiesReceived("@ query");
        template.sendBody("direct:start3", null);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testHttpRouteWithQueryByHeader() throws Exception {
        MockEndpoint mockEndpoint = getMockEndpoint("mock:a");
        mockEndpoint.expectedBodiesReceived("test");
        template.sendBody("direct:start4", "test");
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void testHttpRouteWithHttpURI() throws Exception {
        Exchange exchange = template.send((("http://localhost:" + (port2)) + "/querystring"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("");
                exchange.getIn().setHeader(HTTP_URI, (("http://localhost:" + (port2)) + "/querystring?id=test"));
            }
        });
        assertEquals("Get a wrong response.", "test", exchange.getOut().getBody(String.class));
    }
}

