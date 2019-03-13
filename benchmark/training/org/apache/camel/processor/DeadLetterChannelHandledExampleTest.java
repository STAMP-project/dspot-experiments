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
package org.apache.camel.processor;


import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Headers;
import org.apache.camel.OutHeaders;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to verify that handled policy is working as expected for wiki documentation.
 */
// END SNIPPET: e3
public class DeadLetterChannelHandledExampleTest extends ContextTestSupport {
    @Test
    public void testOrderOK() throws Exception {
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedBodiesReceived("Order OK");
        result.expectedHeaderReceived("orderid", "123");
        MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedMessageCount(0);
        Object out = template.requestBodyAndHeader("direct:start", "Order: MacBook Pro", "customerid", "444");
        Assert.assertEquals("Order OK", out);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOrderERROR() throws Exception {
        MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedBodiesReceived("Order ERROR");
        error.expectedHeaderReceived("orderid", "failed");
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(0);
        Object out = template.requestBodyAndHeader("direct:start", "Order: kaboom", "customerid", "555");
        Assert.assertEquals("Order ERROR", out);
        assertMockEndpointsSatisfied();
    }

    // START SNIPPET: e2
    /**
     * Order service as a plain POJO class
     */
    public static class OrderService {
        /**
         * This method handle our order input and return the order
         *
         * @param in
         * 		the in headers
         * @param payload
         * 		the in payload
         * @param out
         * 		the out headers
         * @return the out payload
         * @throws OrderFailedException
         * 		is thrown if the order cannot be processed
         */
        public Object handleOrder(@Headers
        Map<?, ?> in, @Body
        String payload, @OutHeaders
        Map<String, Object> out) throws DeadLetterChannelHandledExampleTest.OrderFailedException {
            out.put("customerid", in.get("customerid"));
            if ("Order: kaboom".equals(payload)) {
                throw new DeadLetterChannelHandledExampleTest.OrderFailedException("Cannot order: kaboom");
            } else {
                out.put("orderid", "123");
                return "Order OK";
            }
        }

        /**
         * This method creates the response to the caller if the order could not be processed
         *
         * @param in
         * 		the in headers
         * @param payload
         * 		the in payload
         * @param out
         * 		the out headers
         * @return the out payload
         */
        public Object orderFailed(@Headers
        Map<?, ?> in, @Body
        String payload, @OutHeaders
        Map<String, Object> out) {
            out.put("customerid", in.get("customerid"));
            out.put("orderid", "failed");
            return "Order ERROR";
        }
    }

    // END SNIPPET: e2
    // START SNIPPET: e3
    /**
     * Exception thrown if the order cannot be processed
     */
    public static class OrderFailedException extends Exception {
        private static final long serialVersionUID = 1L;

        public OrderFailedException(String message) {
            super(message);
        }
    }
}

