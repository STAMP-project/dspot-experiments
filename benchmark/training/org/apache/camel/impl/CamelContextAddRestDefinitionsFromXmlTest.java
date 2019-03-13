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


import java.util.List;
import javax.xml.bind.JAXBContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.junit.Assert;
import org.junit.Test;


public class CamelContextAddRestDefinitionsFromXmlTest extends ContextTestSupport {
    protected JAXBContext jaxbContext;

    @Test
    public void testAddRestDefinitionsFromXml() throws Exception {
        RestDefinition rest = loadRest("rest1.xml");
        Assert.assertNotNull(rest);
        Assert.assertEquals("foo", rest.getId());
        Assert.assertEquals(0, context.getRestDefinitions().size());
        context.getRestDefinitions().add(rest);
        Assert.assertEquals(1, context.getRestDefinitions().size());
        final List<RouteDefinition> routeDefinitions = rest.asRouteDefinition(context);
        for (final RouteDefinition routeDefinition : routeDefinitions) {
            context.addRouteDefinition(routeDefinition);
        }
        Assert.assertEquals(2, context.getRoutes().size());
        Assert.assertTrue("Route should be started", context.getRouteController().getRouteStatus("route1").isStarted());
        getMockEndpoint("mock:bar").expectedBodiesReceived("Hello World");
        template.sendBody("seda:get-say-hello-bar", "Hello World");
        assertMockEndpointsSatisfied();
    }
}

