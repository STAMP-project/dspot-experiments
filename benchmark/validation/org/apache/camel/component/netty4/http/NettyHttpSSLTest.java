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


import java.util.Properties;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class NettyHttpSSLTest extends BaseNettyTest {
    private static final String NULL_VALUE_MARKER = CamelTestSupport.class.getCanonicalName();

    protected Properties originalValues = new Properties();

    @Test
    public void testSSLInOutWithNettyConsumer() throws Exception {
        // ibm jdks dont have sun security algorithms
        if (isJavaVendor("ibm")) {
            return;
        }
        getMockEndpoint("mock:input").expectedBodiesReceived("Hello World");
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("netty4-http:https://localhost:{{port}}?ssl=true&passphrase=changeit&keyStoreResource=jsse/localhost.ks&trustStoreResource=jsse/localhost.ks").to("mock:input").process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        javax.net.ssl.SSLSession session = exchange.getIn().getHeader(NettyConstants.NETTY_SSL_SESSION, javax.net.ssl.SSLSession.class);
                        if (session != null) {
                            exchange.getOut().setBody("Bye World");
                        } else {
                            exchange.getOut().setBody("Cannot start conversion without SSLSession");
                        }
                    }
                });
            }
        });
        context.start();
        String out = template.requestBody("https://localhost:{{port}}", "Hello World", String.class);
        assertEquals("Bye World", out);
        assertMockEndpointsSatisfied();
    }
}

