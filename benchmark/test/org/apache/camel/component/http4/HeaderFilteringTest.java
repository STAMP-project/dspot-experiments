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


import com.sun.net.httpserver.HttpServer;
import java.util.Collections;
import org.apache.camel.Producer;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RestConfiguration;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.junit.Test;


public class HeaderFilteringTest {
    private static final String BODY = "{\"example\":\"json\"}";

    private int port;

    private HttpServer server;

    @Test
    public void shouldFilterIncomingHttpHeadersInProducer() throws Exception {
        final HttpComponent http = new HttpComponent();
        final DefaultCamelContext context = new DefaultCamelContext();
        final Producer producer = http.createProducer(context, ("http://localhost:" + (port)), "GET", "/test", null, null, "application/json", "application/json", new RestConfiguration(), Collections.emptyMap());
        final DefaultExchange exchange = new DefaultExchange(context);
        final DefaultMessage in = new DefaultMessage(context);
        in.setHeader("Host", "www.not-localhost.io");
        in.setBody(HeaderFilteringTest.BODY);
        exchange.setIn(in);
        try {
            producer.process(exchange);
        } catch (final HttpOperationFailedException e) {
            fail(((e.getMessage()) + "\n%s"), e.getResponseBody());
        }
    }
}

