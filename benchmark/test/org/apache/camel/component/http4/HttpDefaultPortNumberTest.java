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
package org.apache.camel.component.http4;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("We cannot run this test as default port 80 is not allows on most boxes")
public class HttpDefaultPortNumberTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void testHttpConnectionWithTwoRoutesAndOneWithDefaultPort() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to((("http4://" + (localServer.getInetAddress().getHostName())) + "/search"));
                from("direct:dummy").to((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search"));
            }
        });
        context.start();
        Exchange exchange = template.request("direct:start", null);
        // note: the default portnumber will appear in the error message
        assertRefused(exchange, ":80");
    }

    @Test
    public void testHttpConnectionWithTwoRoutesAndAllPortsSpecified() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to((("http4://" + (localServer.getInetAddress().getHostName())) + ":80/search"));
                from("direct:dummy").to((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search"));
            }
        });
        context.start();
        Exchange exchange = template.request("direct:start", null);
        // specifying the defaultportnumber helps
        assertRefused(exchange, ":80");
    }

    @Test
    public void testHttpConnectionRefusedStoppedServer() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to((("http4://" + (localServer.getInetAddress().getHostName())) + "/search"));
                from("direct:dummy").to((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search"));
            }
        });
        context.start();
        localServer.stop();
        Exchange exchange = template.request("direct:start", null);
        assertRefused(exchange, ":80");
    }

    @Test
    public void testHttpConnectionRefusedRunningServer() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to((("http4://" + (localServer.getInetAddress().getHostName())) + "/search"));
            }
        });
        context.start();
        // server is runnning, but connecting to other port
        Exchange exchange = template.request("direct:start", null);
        assertRefused(exchange, ":80");
    }
}

