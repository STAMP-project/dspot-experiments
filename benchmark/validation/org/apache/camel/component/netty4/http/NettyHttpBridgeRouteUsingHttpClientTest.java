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
package org.apache.camel.component.netty4.http;


import Exchange.CONTENT_TYPE;
import java.io.ByteArrayInputStream;
import org.apache.camel.RuntimeCamelException;
import org.junit.Test;


public class NettyHttpBridgeRouteUsingHttpClientTest extends BaseNettyTest {
    private int port1;

    private int port2;

    @Test
    public void testBridge() throws Exception {
        String response = template.requestBodyAndHeader((("http://localhost:" + (port2)) + "/test/hello"), new ByteArrayInputStream("This is a test".getBytes()), "Content-Type", "application/xml", String.class);
        assertEquals("Get a wrong response", "/", response);
        response = template.requestBody((("http://localhost:" + (port1)) + "/hello/world"), "hello", String.class);
        assertEquals("Get a wrong response", "/hello/world", response);
        try {
            template.requestBody((("http://localhost:" + (port2)) + "/hello/world"), "hello", String.class);
            fail("Expect exception here!");
        } catch (Exception ex) {
            assertTrue("We should get a RuntimeCamelException", (ex instanceof RuntimeCamelException));
        }
    }

    @Test
    public void testSendFormRequestMessage() throws Exception {
        String out = template.requestBodyAndHeader((("http://localhost:" + (port2)) + "/form"), "username=abc&pass=password", CONTENT_TYPE, "application/x-www-form-urlencoded", String.class);
        assertEquals("Get a wrong response message", "username=abc&pass=password", out);
    }
}

