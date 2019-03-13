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
package org.apache.camel.itest.async;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class HttpAsyncDslTest extends CamelTestSupport {
    private static volatile String order = "";

    @Test
    public void testRequestOnly() throws Exception {
        getMockEndpoint("mock:validate").expectedMessageCount(1);
        // even though its request only the message is still continued being processed
        getMockEndpoint("mock:order").expectedMessageCount(1);
        template.sendBody("jms:queue:order", "Order: Camel in Action");
        HttpAsyncDslTest.order += "C";
        assertMockEndpointsSatisfied();
        // B should be last (either ABC or BAC depending on threading)
        assertEquals(3, HttpAsyncDslTest.order.length());
        assertTrue(HttpAsyncDslTest.order.endsWith("B"));
    }

    @Test
    public void testRequestReply() throws Exception {
        getMockEndpoint("mock:validate").expectedMessageCount(1);
        // even though its request only the message is still continued being processed
        getMockEndpoint("mock:order").expectedMessageCount(1);
        String response = template.requestBody("jms:queue:order", "Order: Camel in Action", String.class);
        HttpAsyncDslTest.order += "C";
        assertMockEndpointsSatisfied();
        // should be in strict ABC order as we do request/reply
        assertEquals("ABC", HttpAsyncDslTest.order);
        assertEquals("Order OK", response);
    }

    public static class MyValidateOrderBean {
        public void validateOrder(byte[] payload) {
            HttpAsyncDslTest.order += "A";
            // noop
        }
    }

    public static class MyHandleOrderBean {
        public String handleOrder(String message) {
            HttpAsyncDslTest.order += "B";
            return "Order OK";
            // noop
        }
    }
}

