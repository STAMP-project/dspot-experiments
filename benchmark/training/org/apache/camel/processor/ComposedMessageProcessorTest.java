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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Body;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


// END SNIPPET: e7
public class ComposedMessageProcessorTest extends ContextTestSupport {
    @SuppressWarnings("unchecked")
    @Test
    public void testValidatingCorrectOrder() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedHeaderReceived("orderId", "myorderid");
        List<ComposedMessageProcessorTest.OrderItem> order = Arrays.asList(new ComposedMessageProcessorTest.OrderItem[]{ new ComposedMessageProcessorTest.OrderItem("widget", 5), new ComposedMessageProcessorTest.OrderItem("gadget", 10) });
        template.sendBodyAndHeader("direct:start", order, "orderId", "myorderid");
        assertMockEndpointsSatisfied();
        List<ComposedMessageProcessorTest.OrderItem> validatedOrder = resultEndpoint.getExchanges().get(0).getIn().getBody(List.class);
        Assert.assertTrue(validatedOrder.get(0).valid);
        Assert.assertTrue(validatedOrder.get(1).valid);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testValidatingIncorrectOrder() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedHeaderReceived("orderId", "myorderid");
        // START SNIPPET: e1
        List<ComposedMessageProcessorTest.OrderItem> order = Arrays.asList(new ComposedMessageProcessorTest.OrderItem[]{ new ComposedMessageProcessorTest.OrderItem("widget", 500), new ComposedMessageProcessorTest.OrderItem("gadget", 200) });
        template.sendBodyAndHeader("direct:start", order, "orderId", "myorderid");
        // END SNIPPET: e1
        assertMockEndpointsSatisfied();
        List<ComposedMessageProcessorTest.OrderItem> validatedOrder = resultEndpoint.getExchanges().get(0).getIn().getBody(List.class);
        Assert.assertFalse(validatedOrder.get(0).valid);
        Assert.assertFalse(validatedOrder.get(1).valid);
    }

    // START SNIPPET: e3
    public static final class OrderItem {
        String type;// type of the item


        int quantity;// how many we want


        boolean valid;// whether that many items can be ordered


        public OrderItem(String type, int quantity) {
            this.type = type;
            this.quantity = quantity;
        }
    }

    // END SNIPPET: e3
    // END SNIPPET: e4
    public static final class OrderItemHelper {
        private OrderItemHelper() {
        }

        // START SNIPPET: e4
        public static boolean isWidget(@Body
        ComposedMessageProcessorTest.OrderItem orderItem) {
            return orderItem.type.equals("widget");
        }
    }

    /**
     * Bean that checks whether the specified number of widgets can be ordered
     */
    // START SNIPPET: e5
    public static final class WidgetInventory {
        public void checkInventory(@Body
        ComposedMessageProcessorTest.OrderItem orderItem) {
            Assert.assertEquals("widget", orderItem.type);
            if ((orderItem.quantity) < 10) {
                orderItem.valid = true;
            }
        }
    }

    // END SNIPPET: e5
    /**
     * Bean that checks whether the specified number of gadgets can be ordered
     */
    // START SNIPPET: e6
    public static final class GadgetInventory {
        public void checkInventory(@Body
        ComposedMessageProcessorTest.OrderItem orderItem) {
            Assert.assertEquals("gadget", orderItem.type);
            if ((orderItem.quantity) < 20) {
                orderItem.valid = true;
            }
        }
    }

    // END SNIPPET: e6
    /**
     * Aggregation strategy that re-assembles the validated OrderItems
     * into an order, which is just a List.
     */
    // START SNIPPET: e7
    public static final class MyOrderAggregationStrategy implements AggregationStrategy {
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }
            List<ComposedMessageProcessorTest.OrderItem> order = new ArrayList<>(2);
            order.add(oldExchange.getIn().getBody(ComposedMessageProcessorTest.OrderItem.class));
            order.add(newExchange.getIn().getBody(ComposedMessageProcessorTest.OrderItem.class));
            oldExchange.getIn().setBody(order);
            return oldExchange;
        }
    }
}

