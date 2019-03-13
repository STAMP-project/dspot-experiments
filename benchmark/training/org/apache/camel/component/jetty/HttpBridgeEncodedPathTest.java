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


import Exchange.HTTP_PATH;
import Exchange.HTTP_QUERY;
import Exchange.HTTP_RAW_QUERY;
import Exchange.HTTP_URI;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;


public class HttpBridgeEncodedPathTest extends BaseJettyTest {
    private int port1;

    private int port2;

    private int port3;

    private int port4;

    @Test
    public void testEncodedQuery() throws Exception {
        String response = template.requestBodyAndHeader((("http://localhost:" + (port2)) + "/test/hello?param1=%2B447777111222"), new ByteArrayInputStream("This is a test".getBytes()), "Content-Type", "text/plain", String.class);
        assertEquals("Get a wrong response", "param1=+447777111222", response);
    }

    @Test
    public void testEncodedPath() throws Exception {
        String path = ((URLEncoder.encode(" :/?#[]@!$", "UTF-8")) + "/") + (URLEncoder.encode("&'()+,;=", "UTF-8"));
        MockEndpoint mock = getMockEndpoint("mock:encodedPath");
        mock.message(0).header(HTTP_URI).isEqualTo(("/" + path));
        mock.message(0).header(HTTP_PATH).isEqualTo(path);
        mock.message(0).header(HTTP_QUERY).isNull();
        mock.message(0).header(HTTP_RAW_QUERY).isNull();
        // cannot use template as it automatically decodes some chars in the path
        HttpClient httpClient = new HttpClient();
        GetMethod httpGet = new GetMethod(((("http://localhost:" + (port4)) + "/test/") + path));
        int status = httpClient.executeMethod(httpGet);
        assertEquals("Get a wrong response status", 200, status);
        assertMockEndpointsSatisfied();
    }
}

