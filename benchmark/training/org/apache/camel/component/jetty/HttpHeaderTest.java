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


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;


public class HttpHeaderTest extends BaseJettyTest {
    @Test
    public void testHttpHeaders() throws Exception {
        String result = template.requestBody("direct:start", "hello", String.class);
        assertEquals("Should send a right http header to the server.", "Find the key!", result);
    }

    @Test
    public void testServerHeader() throws Exception {
        Exchange ex = template.request("http://localhost:{{port}}/server/mytest", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // Do nothing here
            }
        });
        assertNotNull(ex.getOut().getHeader("Server"));
        assertNull(ex.getOut().getHeader("Date"));
        ex = template.request("http://localhost:{{port2}}/server/mytest", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // Do nothing here
            }
        });
        assertNull(ex.getOut().getHeader("Server"));
        assertNotNull(ex.getOut().getHeader("Date"));
    }
}

