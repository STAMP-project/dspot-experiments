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


public class HttpAuthenticationTest extends BaseHttpTest {
    private HttpServer localServer;

    private String user = "camel";

    private String password = "password";

    @Test
    public void basicAuthenticationShouldSuccess() throws Exception {
        Exchange exchange = template.request(((((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search?authUsername=") + (user)) + "&authPassword=") + (password)), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void basicAuthenticationPreemptiveShouldSuccess() throws Exception {
        Exchange exchange = template.request((((((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search?authUsername=") + (user)) + "&authPassword=") + (password)) + "&authenticationPreemptive=true"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchange(exchange);
    }

    @Test
    public void basicAuthenticationShouldFailWithoutCreds() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search?throwExceptionOnFailure=false"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchangeFailed(exchange);
    }

    @Test
    public void basicAuthenticationShouldFailWithWrongCreds() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/search?throwExceptionOnFailure=false&authUsername=camel&authPassword=wrong"), new Processor() {
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertExchangeFailed(exchange);
    }
}

