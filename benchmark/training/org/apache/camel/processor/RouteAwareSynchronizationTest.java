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
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.spi.SynchronizationRouteAware;
import org.junit.Assert;
import org.junit.Test;


public class RouteAwareSynchronizationTest extends ContextTestSupport {
    private static final List<String> EVENTS = new ArrayList<>();

    @Test
    public void testRouteAwareSynchronization() throws Exception {
        RouteAwareSynchronizationTest.EVENTS.clear();
        Assert.assertEquals(0, RouteAwareSynchronizationTest.EVENTS.size());
        getMockEndpoint("mock:a").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:b").expectedBodiesReceived("Hello World");
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.addOnCompletion(new RouteAwareSynchronizationTest.MyRouteAware());
                exchange.getIn().setBody("Hello World");
            }
        });
        assertMockEndpointsSatisfied();
        Assert.assertEquals(5, RouteAwareSynchronizationTest.EVENTS.size());
        Assert.assertEquals("onBeforeRoute-start", RouteAwareSynchronizationTest.EVENTS.get(0));
        Assert.assertEquals("onBeforeRoute-foo", RouteAwareSynchronizationTest.EVENTS.get(1));
        Assert.assertEquals("onAfterRoute-foo", RouteAwareSynchronizationTest.EVENTS.get(2));
        Assert.assertEquals("onAfterRoute-start", RouteAwareSynchronizationTest.EVENTS.get(3));
        Assert.assertEquals("onComplete", RouteAwareSynchronizationTest.EVENTS.get(4));
    }

    private static final class MyRouteAware implements SynchronizationRouteAware {
        @Override
        public void onBeforeRoute(Route route, Exchange exchange) {
            RouteAwareSynchronizationTest.EVENTS.add(("onBeforeRoute-" + (route.getId())));
        }

        @Override
        public void onAfterRoute(Route route, Exchange exchange) {
            RouteAwareSynchronizationTest.EVENTS.add(("onAfterRoute-" + (route.getId())));
        }

        @Override
        public void onComplete(Exchange exchange) {
            RouteAwareSynchronizationTest.EVENTS.add("onComplete");
        }

        @Override
        public void onFailure(Exchange exchange) {
            RouteAwareSynchronizationTest.EVENTS.add("onFailure");
        }
    }
}

