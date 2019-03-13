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


import javax.xml.bind.JAXBContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.junit.Assert;
import org.junit.Test;


public class CamelContextAddRouteDefinitionsFromXmlTest extends ContextTestSupport {
    protected JAXBContext jaxbContext;

    @Test
    public void testAddRouteDefinitionsFromXml() throws Exception {
        RouteDefinition route = loadRoute("route1.xml");
        Assert.assertNotNull(route);
        Assert.assertEquals("foo", route.getId());
        Assert.assertEquals(0, context.getRoutes().size());
        context.addRouteDefinition(route);
        Assert.assertEquals(1, context.getRoutes().size());
        Assert.assertTrue("Route should be started", context.getRouteController().getRouteStatus("foo").isStarted());
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRemoveRouteDefinitionsFromXml() throws Exception {
        RouteDefinition route = loadRoute("route1.xml");
        Assert.assertNotNull(route);
        Assert.assertEquals("foo", route.getId());
        Assert.assertEquals(0, context.getRoutes().size());
        context.addRouteDefinition(route);
        Assert.assertEquals(1, context.getRouteDefinitions().size());
        Assert.assertEquals(1, context.getRoutes().size());
        Assert.assertTrue("Route should be started", context.getRouteController().getRouteStatus("foo").isStarted());
        context.removeRouteDefinition(route);
        Assert.assertEquals(0, context.getRoutes().size());
        Assert.assertNull(context.getRouteController().getRouteStatus("foo"));
        Assert.assertEquals(0, context.getRouteDefinitions().size());
    }

    @Test
    public void testAddRouteDefinitionsFromXml2() throws Exception {
        RouteDefinition route = loadRoute("route2.xml");
        Assert.assertNotNull(route);
        Assert.assertEquals("foo", route.getId());
        Assert.assertEquals(0, context.getRoutes().size());
        context.addRouteDefinition(route);
        Assert.assertEquals(1, context.getRoutes().size());
        Assert.assertTrue("Route should be stopped", context.getRouteController().getRouteStatus("foo").isStopped());
        context.getRouteController().startRoute("foo");
        Assert.assertTrue("Route should be started", context.getRouteController().getRouteStatus("foo").isStarted());
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAddRouteDefinitionsFromXmlIsPrepared() throws Exception {
        RouteDefinition route = loadRoute("route1.xml");
        Assert.assertNotNull(route);
        Assert.assertEquals("foo", route.getId());
        Assert.assertEquals(0, context.getRoutes().size());
        context.addRouteDefinition(route);
        Assert.assertEquals(1, context.getRoutes().size());
        Assert.assertTrue("Route should be started", context.getRouteController().getRouteStatus("foo").isStarted());
        // should be prepared, check parents has been set
        Assert.assertNotNull("Parent should be set on outputs");
        route = context.getRouteDefinition("foo");
        for (ProcessorDefinition<?> output : route.getOutputs()) {
            Assert.assertNotNull("Parent should be set on output", output.getParent());
            Assert.assertEquals(route, output.getParent());
        }
    }

    @Test
    public void testAddRouteDefinitionsFromXml3() throws Exception {
        RouteDefinition route = loadRoute("route3.xml");
        Assert.assertNotNull(route);
        Assert.assertEquals("foo", route.getId());
        Assert.assertEquals(0, context.getRoutes().size());
        context.addRouteDefinition(route);
        Assert.assertEquals(1, context.getRoutes().size());
        Assert.assertTrue("Route should be started", context.getRouteController().getRouteStatus("foo").isStarted());
        getMockEndpoint("mock:foo").whenExchangeReceived(2, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new IllegalArgumentException("Damn"));
            }
        });
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:handled").expectedBodiesReceived("Bye World");
        template.sendBody("direct:start", "Hello World");
        template.sendBody("direct:start", "Bye World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAddRouteDefinitionsAfterExceptionFromXml() throws Exception {
        RouteDefinition route = loadRoute("route4_error.xml");
        Assert.assertNotNull(route);
        Assert.assertEquals("foo", route.getId());
        Assert.assertEquals(0, context.getRoutes().size());
        try {
            context.addRouteDefinition(route);
        } catch (Exception e) {
            // catch this is error to simulate test case!!!!
        }
        // load route with same id
        route = loadRoute("route4_ok.xml");
        Assert.assertNotNull(route);
        Assert.assertEquals("foo", route.getId());
        Assert.assertEquals(0, context.getRoutes().size());
        context.addRouteDefinition(route);
        Assert.assertEquals(1, context.getRoutes().size());
    }
}

