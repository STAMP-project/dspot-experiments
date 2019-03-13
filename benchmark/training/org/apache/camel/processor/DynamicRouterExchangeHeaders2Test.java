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
import org.junit.Assert;
import org.junit.Test;


// END SNIPPET: e2
public class DynamicRouterExchangeHeaders2Test extends ContextTestSupport {
    private static List<String> bodies = new ArrayList<>();

    private static List<String> previouses = new ArrayList<>();

    @Test
    public void testDynamicRouter() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:a").expectedHeaderReceived("invoked", 1);
        getMockEndpoint("mock:b").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:b").expectedHeaderReceived("invoked", 2);
        getMockEndpoint("mock:c").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:c").expectedHeaderReceived("invoked", 2);
        getMockEndpoint("mock:foo").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:foo").expectedHeaderReceived("invoked", 3);
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:result").expectedHeaderReceived("invoked", 4);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(5, DynamicRouterExchangeHeaders2Test.bodies.size());
        Assert.assertEquals("Hello World", DynamicRouterExchangeHeaders2Test.bodies.get(0));
        Assert.assertEquals("Hello World", DynamicRouterExchangeHeaders2Test.bodies.get(1));
        Assert.assertEquals("Hello World", DynamicRouterExchangeHeaders2Test.bodies.get(2));
        Assert.assertEquals("Bye World", DynamicRouterExchangeHeaders2Test.bodies.get(3));
        Assert.assertEquals("Bye World", DynamicRouterExchangeHeaders2Test.bodies.get(4));
        Assert.assertEquals(4, DynamicRouterExchangeHeaders2Test.previouses.size());
        Assert.assertEquals("mock://a", DynamicRouterExchangeHeaders2Test.previouses.get(0));
        Assert.assertEquals("mock://c", DynamicRouterExchangeHeaders2Test.previouses.get(1));
        Assert.assertEquals("direct://foo", DynamicRouterExchangeHeaders2Test.previouses.get(2));
        Assert.assertEquals("mock://result", DynamicRouterExchangeHeaders2Test.previouses.get(3));
    }
}

