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
package org.apache.camel.processor.aggregator;


import java.util.concurrent.ExecutorService;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class AggregateShutdownThreadPoolTest extends ContextTestSupport {
    private ExecutorService myPool;

    @Test
    public void testAggregateShutdownDefaultThreadPoolTest() throws Exception {
        getMockEndpoint("mock:aggregated").expectedBodiesReceived("A+B+C");
        template.sendBodyAndHeader("direct:foo", "A", "id", 123);
        template.sendBodyAndHeader("direct:foo", "B", "id", 123);
        template.sendBodyAndHeader("direct:foo", "C", "id", 123);
        assertMockEndpointsSatisfied();
        context.getRouteController().stopRoute("foo");
        resetMocks();
        context.getRouteController().startRoute("foo");
        getMockEndpoint("mock:aggregated").expectedBodiesReceived("D+E+F");
        template.sendBodyAndHeader("direct:foo", "D", "id", 123);
        template.sendBodyAndHeader("direct:foo", "E", "id", 123);
        template.sendBodyAndHeader("direct:foo", "F", "id", 123);
        assertMockEndpointsSatisfied();
        context.stop();
    }

    @Test
    public void testAggregateShutdownCustomThreadPoolTest() throws Exception {
        Assert.assertEquals(false, myPool.isShutdown());
        getMockEndpoint("mock:aggregated").expectedBodiesReceived("A+B+C");
        template.sendBodyAndHeader("direct:bar", "A", "id", 123);
        template.sendBodyAndHeader("direct:bar", "B", "id", 123);
        template.sendBodyAndHeader("direct:bar", "C", "id", 123);
        assertMockEndpointsSatisfied();
        Assert.assertEquals(false, myPool.isShutdown());
        context.getRouteController().stopRoute("bar");
        Assert.assertEquals(false, myPool.isShutdown());
        resetMocks();
        context.getRouteController().startRoute("bar");
        Assert.assertEquals(false, myPool.isShutdown());
        getMockEndpoint("mock:aggregated").expectedBodiesReceived("D+E+F");
        template.sendBodyAndHeader("direct:bar", "D", "id", 123);
        template.sendBodyAndHeader("direct:bar", "E", "id", 123);
        template.sendBodyAndHeader("direct:bar", "F", "id", 123);
        assertMockEndpointsSatisfied();
        Assert.assertEquals(false, myPool.isShutdown());
        context.stop();
        // now it should be shutdown when CamelContext is stopped/shutdown
        Assert.assertEquals(true, myPool.isShutdown());
    }
}

