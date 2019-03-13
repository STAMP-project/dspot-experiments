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
package org.apache.camel.component.jetty.jettyproducer;


import Exchange.HTTP_QUERY;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.junit.Test;


public class JettyHttpProducerQueryParamTest extends BaseJettyTest {
    private String url = ("jetty://http://127.0.0.1:" + (BaseJettyTest.getPort())) + "/cheese";

    @Test
    public void testQueryParameters() throws Exception {
        // give Jetty time to startup properly
        Thread.sleep(1000);
        Exchange exchange = template.request(((url) + "?quote=Camel%20rocks"), null);
        assertNotNull(exchange);
        String body = exchange.getOut().getBody(String.class);
        Map<?, ?> headers = exchange.getOut().getHeaders();
        assertEquals("Bye World", body);
        assertEquals("Carlsberg", headers.get("beer"));
    }

    @Test
    public void testQueryParametersWithHeader() throws Exception {
        // these tests does not run well on Windows
        if (isPlatform("windows")) {
            return;
        }
        // give Jetty time to startup properly
        Thread.sleep(1000);
        Exchange exchange = template.request(url, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_QUERY, "quote=Camel rocks");
            }
        });
        assertNotNull(exchange);
        String body = exchange.getOut().getBody(String.class);
        Map<?, ?> headers = exchange.getOut().getHeaders();
        assertEquals("Bye World", body);
        assertEquals("Carlsberg", headers.get("beer"));
    }
}

