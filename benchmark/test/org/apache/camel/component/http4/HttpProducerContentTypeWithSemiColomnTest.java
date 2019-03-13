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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


public class HttpProducerContentTypeWithSemiColomnTest extends BaseHttpTest {
    private static final String CONTENT_TYPE = "multipart/form-data;boundary=---------------------------j2radvtrk";

    private HttpServer localServer;

    @Test
    public void testContentTypeWithBoundary() throws Exception {
        Exchange out = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/content"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, HttpProducerContentTypeWithSemiColomnTest.CONTENT_TYPE);
                exchange.getIn().setBody("This is content");
            }
        });
        assertNotNull(out);
        assertFalse("Should not fail", out.isFailed());
        assertEquals(HttpProducerContentTypeWithSemiColomnTest.CONTENT_TYPE.replace(";", "; "), out.getOut().getBody(String.class));
    }

    @Test
    public void testContentTypeWithBoundaryWithIgnoreResponseBody() throws Exception {
        Exchange out = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/content?ignoreResponseBody=true"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, HttpProducerContentTypeWithSemiColomnTest.CONTENT_TYPE);
                exchange.getIn().setBody("This is content");
            }
        });
        assertNotNull(out);
        assertFalse("Should not fail", out.isFailed());
        assertNull(out.getOut().getBody());
    }
}

