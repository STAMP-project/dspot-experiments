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
package org.apache.camel.itest.jms;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsJaxbTest extends CamelTestSupport {
    @Test
    public void testOk() throws Exception {
        PurchaseOrder order = new PurchaseOrder();
        order.setName("Wine");
        order.setAmount(123.45);
        order.setPrice(2.22);
        MockEndpoint result = getMockEndpoint("mock:wine");
        result.expectedBodiesReceived(order);
        template.sendBody("jms:queue:in", "<purchaseOrder name='Wine' amount='123.45' price='2.22'/>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUnmarshalError() throws Exception {
        MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedBodiesReceived("<foo/>");
        getMockEndpoint("mock:invalid").expectedMessageCount(0);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBody("jms:queue:in", "<foo/>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testNotWine() throws Exception {
        PurchaseOrder order = new PurchaseOrder();
        order.setName("Beer");
        order.setAmount(2);
        order.setPrice(1.99);
        MockEndpoint error = getMockEndpoint("mock:invalid");
        error.expectedBodiesReceived(order);
        getMockEndpoint("mock:error").expectedMessageCount(0);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBody("jms:queue:in", "<purchaseOrder name='Beer' amount='2.0' price='1.99'/>");
        assertMockEndpointsSatisfied();
    }
}

