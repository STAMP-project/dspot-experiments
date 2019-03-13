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
package org.apache.camel.component.netty4.http;


import Exchange.HTTP_QUERY;
import Exchange.HTTP_RAW_QUERY;
import Exchange.HTTP_URI;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class NettyHttpProducerBridgeTest extends BaseNettyTest {
    private int port1;

    private int port2;

    private int port3;

    @Test
    public void testProxy() throws Exception {
        String reply = template.requestBody((("netty4-http:http://localhost:" + (port1)) + "/foo"), "World", String.class);
        assertEquals("Bye World", reply);
    }

    @Test
    public void testBridgeWithQuery() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:query");
        mock.message(0).header(HTTP_RAW_QUERY).isEqualTo("x=%3B");
        mock.message(0).header(HTTP_QUERY).isEqualTo("x=;");
        template.request((("netty4-http:http://localhost:" + (port3)) + "/query?bridgeEndpoint=true"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_URI, "http://host:8080/");
                exchange.getIn().setHeader(HTTP_QUERY, "x=%3B");
            }
        });
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBridgeWithRawQueryAndQuery() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:query");
        mock.message(0).header(HTTP_RAW_QUERY).isEqualTo("x=%3B");
        mock.message(0).header(HTTP_QUERY).isEqualTo("x=;");
        template.request((("netty4-http:http://localhost:" + (port3)) + "/query?bridgeEndpoint=true"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_URI, "http://host:8080/");
                exchange.getIn().setHeader(HTTP_RAW_QUERY, "x=%3B");
                exchange.getIn().setHeader(HTTP_QUERY, "x=;");
            }
        });
        assertMockEndpointsSatisfied();
    }
}

