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
import org.apache.camel.Navigate;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for navigating a route (runtime processors, not the model).
 */
public class NavigateRouteTest extends ContextTestSupport {
    private static List<Processor> processors = new ArrayList<>();

    @Test
    public void testNavigateRoute() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Navigate<Processor> nav = context.getRoutes().get(0).navigate();
        navigateRoute(nav);
        Assert.assertEquals("There should be 6 processors to navigate", 6, NavigateRouteTest.processors.size());
    }
}

