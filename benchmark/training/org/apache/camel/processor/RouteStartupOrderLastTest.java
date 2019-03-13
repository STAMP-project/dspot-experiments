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


import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.RouteStartupOrder;
import org.junit.Assert;
import org.junit.Test;


public class RouteStartupOrderLastTest extends ContextTestSupport {
    @Test
    public void testRouteStartupOrderLast() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        // assert correct order
        DefaultCamelContext dcc = ((DefaultCamelContext) (context));
        List<RouteStartupOrder> order = dcc.getRouteStartupOrder();
        Assert.assertEquals(4, order.size());
        Assert.assertEquals("seda://foo", order.get(0).getRoute().getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://start", order.get(1).getRoute().getEndpoint().getEndpointUri());
        Assert.assertEquals("seda://bar", order.get(2).getRoute().getEndpoint().getEndpointUri());
        Assert.assertEquals("direct://bar", order.get(3).getRoute().getEndpoint().getEndpointUri());
    }
}

