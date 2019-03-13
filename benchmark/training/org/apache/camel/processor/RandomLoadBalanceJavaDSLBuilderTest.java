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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Navigate;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.junit.Assert;
import org.junit.Test;


/**
 * A crude unit test to navigate the route and build a Java DSL from the route definition
 */
public class RandomLoadBalanceJavaDSLBuilderTest extends RandomLoadBalanceTest {
    @Test
    public void testNavigateRouteAsJavaDSLWithNavigate() throws Exception {
        // this one navigate using the runtime route using the Navigate<Processor>
        StringBuilder sb = new StringBuilder();
        Route route = context.getRoutes().get(0);
        // the start of the route
        sb.append((("from(\"" + (route.getEndpoint().getEndpointUri())) + "\")"));
        // navigate the route and add Java DSL to the sb
        Navigate<Processor> nav = route.navigate();
        navigateRoute(nav, sb);
        // output the Java DSL
        Assert.assertEquals("from(\"direct://start\").loadBalance().random().to(\"mock://x\").to(\"mock://y\").to(\"mock://z\")", sb.toString());
    }

    @Test
    public void testNavigateRouteAsJavaDSL() throws Exception {
        // this one navigate using the route definition
        StringBuilder sb = new StringBuilder();
        RouteDefinition route = context.getRouteDefinitions().get(0);
        // the start of the route
        sb.append((("from(\"" + (route.getInputs().get(0).getUri())) + "\")"));
        // navigate the route and add Java DSL to the sb
        navigateDefinition(route, sb);
        // output the Java DSL
        Assert.assertEquals("from(\"direct://start\").loadBalance().random().to(\"mock://x\").to(\"mock://y\").to(\"mock://z\")", sb.toString());
    }
}

