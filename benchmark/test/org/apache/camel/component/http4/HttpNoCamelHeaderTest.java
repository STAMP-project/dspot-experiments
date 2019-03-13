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


import Exchange.CONTENT_TYPE;
import Exchange.FILE_NAME;
import Exchange.TO_ENDPOINT;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


public class HttpNoCamelHeaderTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void testNoCamelHeader() throws Exception {
        Exchange out = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/hello"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(CONTENT_TYPE, "text/plain");
                exchange.getIn().setHeader(FILE_NAME, "hello.txt");
                exchange.getIn().setBody("This is content");
            }
        });
        assertNotNull(out);
        assertFalse("Should not fail", out.isFailed());
        assertEquals("dude", out.getOut().getHeader("MyApp"));
        assertNull(out.getOut().getHeader(TO_ENDPOINT));
    }
}

