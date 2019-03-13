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
package org.apache.camel.impl;


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class RouteRemove2Test extends ContextTestSupport {
    @Test
    public void testRemove() throws Exception {
        DefaultCamelContext defaultContext = ((DefaultCamelContext) (context));
        Assert.assertEquals("2 routes to start with", 2, context.getRoutes().size());
        Assert.assertEquals("2 routes to start with", 2, context.getRouteDefinitions().size());
        Assert.assertEquals("2 routes to start with", 2, defaultContext.getRouteStartupOrder().size());
        Assert.assertEquals("2 routes to start with", 2, defaultContext.getRouteServices().size());
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        template.sendBody("seda:foo", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("foo").name());
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("bar").name());
        // stop foo route
        context.getRouteController().stopRoute("foo");
        Assert.assertEquals("Stopped", context.getRouteController().getRouteStatus("foo").name());
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("bar").name());
        resetMocks();
        getMockEndpoint("mock:foo").expectedMessageCount(0);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        template.sendBody("seda:bar", "Hello World");
        assertMockEndpointsSatisfied();
        // remove foo route and bar should continue to be functional
        context.removeRoute("foo");
        Assert.assertEquals("There should be no foo route anymore", null, context.getRouteController().getRouteStatus("foo"));
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("bar").name());
        resetMocks();
        // the bar route should still be started and work
        getMockEndpoint("mock:foo").expectedMessageCount(0);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        template.sendBody("seda:bar", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals("1 routes to end with", 1, context.getRoutes().size());
        Assert.assertEquals("1 routes to end with", 1, context.getRouteDefinitions().size());
        Assert.assertEquals("1 routes to end with", 1, defaultContext.getRouteStartupOrder().size());
        Assert.assertEquals("1 routes to end with", 1, defaultContext.getRouteServices().size());
    }
}

