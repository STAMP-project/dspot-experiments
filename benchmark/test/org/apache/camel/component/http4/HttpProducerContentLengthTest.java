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


import Exchange.CONTENT_LENGTH;
import Exchange.CONTENT_TYPE;
import java.io.ByteArrayInputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.stream.ByteArrayInputStreamCache;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


public class HttpProducerContentLengthTest extends BaseHttpTest {
    private HttpServer localServer;

    private final String bodyContent = "{ \n \"content\"=\"This is content\" \n }";

    @Test
    public void testContentLengthStream() throws Exception {
        Exchange out = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/content-streamed?bridgeEndpoint=true"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(CONTENT_LENGTH, "1000");
                exchange.getIn().setHeader(CONTENT_TYPE, "application/json");
                exchange.getIn().setBody(new ByteArrayInputStreamCache(new ByteArrayInputStream(bodyContent.getBytes())));
            }
        });
        assertNotNull(out);
        assertFalse("Should not fail", out.isFailed());
    }

    @Test
    public void testContentLengthNotStreamed() throws Exception {
        Exchange out = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/content-not-streamed?bridgeEndpoint=true"), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(CONTENT_LENGTH, "1000");
                exchange.getIn().setHeader(CONTENT_TYPE, "application/json");
                exchange.getIn().setBody(bodyContent.getBytes());
            }
        });
        assertNotNull(out);
        assertFalse("Should not fail", out.isFailed());
    }
}

