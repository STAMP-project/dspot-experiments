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


import Exchange.CONTENT_ENCODING;
import java.io.ByteArrayInputStream;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("TODO: investigate for Camel 3.0.  The test actally works fine, but the " + ("test needs to be verified as http4 supports gzip by default, so some tests may " + "have to be changed to stay meaningful."))
public class HttpGZipEncodingTest extends BaseJettyTest {
    private int port1;

    private int port2;

    @Test
    public void testHttpProducerWithGzip() throws Exception {
        String response = template.requestBodyAndHeader((("http://localhost:" + (port1)) + "/gzip?httpClientConfigurer=#configurer"), new ByteArrayInputStream("<Hello>World</Hello>".getBytes()), CONTENT_ENCODING, "gzip", String.class);
        assertEquals("The response is wrong", "<b>Hello World</b>", response);
    }

    @Test
    public void testGzipProxy() throws Exception {
        String response = template.requestBodyAndHeader((("http://localhost:" + (port2)) + "/route?httpClientConfigurer=#configurer"), new ByteArrayInputStream("<Hello>World</Hello>".getBytes()), CONTENT_ENCODING, "gzip", String.class);
        assertEquals("The response is wrong", "<b>Hello World</b>", response);
    }

    @Test
    public void testGzipProducerWithGzipData() throws Exception {
        String response = template.requestBodyAndHeader("direct:gzip", new ByteArrayInputStream("<Hello>World</Hello>".getBytes()), CONTENT_ENCODING, "gzip", String.class);
        assertEquals("The response is wrong", "<b>Hello World</b>", response);
    }

    @Test
    public void testGzipGet() throws Exception {
        String response = template.requestBodyAndHeader((("http://localhost:" + (port1)) + "/gzip"), null, "Accept-Encoding", "gzip", String.class);
        assertEquals("The response is wrong", "<b>Hello World for gzip</b>", response);
    }
}

