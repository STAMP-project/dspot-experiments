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


import Exchange.HTTP_PATH;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


public class HttpPathTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void httpPath() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void httpPathHeader() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/"), new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_PATH, "search");
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void httpPathHeaderWithStaticQueryParams() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "?abc=123"), new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_PATH, "testWithQueryParams");
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void httpPathHeaderWithBaseSlashesAndWithStaticQueryParams() throws Exception {
        Exchange exchange = template.request(((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/") + "?abc=123"), new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_PATH, "/testWithQueryParams");
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void httpEscapedCharacters() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/test%20/path"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchange(exchange);
    }
}

