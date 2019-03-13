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
import org.apache.camel.Processor;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


public class HttpSOTimeoutTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void httpGet() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "?httpClient.SocketTimeout=5000"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void httpGetShouldThrowASocketTimeoutException() throws Exception {
        Exchange reply = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "?httpClient.SocketTimeout=1000"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        Exception e = reply.getException();
        assertNotNull("Should have thrown an exception", e);
    }

    @Test
    public void httpGetUriOption() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "?socketTimeout=5000"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void httpGetUriOptionShouldThrowASocketTimeoutException() throws Exception {
        Exchange reply = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "?socketTimeout=1000"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        Exception e = reply.getException();
        assertNotNull("Should have thrown an exception", e);
    }
}

