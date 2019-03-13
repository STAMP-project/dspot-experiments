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
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.RuntimeEndpointRegistry;
import org.junit.Assert;
import org.junit.Test;


public class RuntimeEndpointRegistryTest extends ContextTestSupport {
    @Test
    public void testRuntimeEndpointRegistry() throws Exception {
        RuntimeEndpointRegistry registry = context.getRuntimeEndpointRegistry();
        Assert.assertEquals(0, registry.getAllEndpoints(false).size());
        // we have 2 at the start as we have all endpoints for the route consumers
        Assert.assertEquals(2, registry.getAllEndpoints(true).size());
        MockEndpoint mock = getMockEndpoint("mock:foo2");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("seda:foo", "Hello World", "slip", "mock:foo2");
        mock.assertIsSatisfied();
        Assert.assertEquals(4, registry.getAllEndpoints(true).size());
        Assert.assertEquals(3, registry.getEndpointsPerRoute("foo", true).size());
        Assert.assertEquals(1, registry.getEndpointsPerRoute("bar", true).size());
        mock = getMockEndpoint("mock:bar2");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("seda:bar", "Bye World", "slip", "mock:bar2");
        mock.assertIsSatisfied();
        Assert.assertEquals(6, registry.getAllEndpoints(true).size());
        Assert.assertEquals(3, registry.getEndpointsPerRoute("foo", true).size());
        Assert.assertEquals(3, registry.getEndpointsPerRoute("bar", true).size());
        // lets check the json
        String json = context.createRouteStaticEndpointJson(null);
        Assert.assertNotNull(json);
        log.info(json);
        Assert.assertTrue("Should have outputs", json.contains(" { \"uri\": \"mock://foo\" }"));
        Assert.assertTrue("Should have outputs", json.contains(" { \"uri\": \"mock://foo2\" }"));
        Assert.assertTrue("Should have outputs", json.contains(" { \"uri\": \"mock://bar\" }"));
        Assert.assertTrue("Should have outputs", json.contains(" { \"uri\": \"mock://bar2\" }"));
    }
}

