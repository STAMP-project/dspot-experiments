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
package org.apache.camel.model;


import java.io.InputStream;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.junit.Assert;
import org.junit.Test;


public class LoadRestFromXmlTest extends ContextTestSupport {
    @Test
    public void testLoadRestFromXml() throws Exception {
        Assert.assertNotNull("Existing foo route should be there", context.getRoute("foo"));
        Assert.assertEquals(2, context.getRoutes().size());
        // test that existing route works
        MockEndpoint foo = getMockEndpoint("mock:foo");
        foo.expectedBodiesReceived("Hello World");
        template.sendBody("direct:foo", "Hello World");
        foo.assertIsSatisfied();
        // load rest from XML and add them to the existing camel context
        InputStream is = getClass().getResourceAsStream("barRest.xml");
        RestsDefinition rests = context.loadRestsDefinition(is);
        context.addRestDefinitions(rests.getRests());
        for (final RestDefinition restDefinition : rests.getRests()) {
            List<RouteDefinition> routeDefinitions = restDefinition.asRouteDefinition(context);
            context.addRouteDefinitions(routeDefinitions);
        }
        Assert.assertNotNull("Loaded rest route should be there", context.getRoute("route1"));
        Assert.assertEquals(3, context.getRoutes().size());
        // test that loaded route works
        MockEndpoint bar = getMockEndpoint("mock:bar");
        bar.expectedBodiesReceived("Bye World");
        template.sendBody("seda:get-say-hello-bar", "Bye World");
        bar.assertIsSatisfied();
    }
}

