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
package org.apache.camel.component.cxf;


import java.io.InputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;


/**
 * Unit test for setting arbitrary payload in MESSAGE mode
 */
public class CxfDispatchMessageTest extends CxfDispatchTestSupport {
    @Test
    public void testDipatchMessage() throws Exception {
        final String name = "Tila";
        Exchange exchange = sendJaxWsDispatchMessage(name, false);
        assertEquals("The request should be handled sucessfully ", exchange.isFailed(), false);
        org.apache.camel.Message response = exchange.getOut();
        assertNotNull("The response message must not be null ", response);
        String value = decodeResponseFromMessage(response.getBody(InputStream.class), exchange);
        assertTrue("The response body must match the request ", value.endsWith(name));
    }

    @Test
    public void testDipatchMessageOneway() throws Exception {
        final String name = "Tila";
        Exchange exchange = sendJaxWsDispatchMessage(name, true);
        assertEquals("The request should be handled sucessfully ", exchange.isFailed(), false);
        org.apache.camel.Message response = exchange.getOut();
        assertNotNull("The response message must not be null ", response);
        assertNull("The response body must be null ", response.getBody());
    }
}

