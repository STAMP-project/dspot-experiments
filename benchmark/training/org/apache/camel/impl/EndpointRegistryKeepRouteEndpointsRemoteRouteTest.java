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


public class EndpointRegistryKeepRouteEndpointsRemoteRouteTest extends ContextTestSupport {
    @Test
    public void testEndpointRegistryKeepRouteEndpointsRemoveRoute() throws Exception {
        Assert.assertTrue(((context.hasEndpoint("direct://start")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://start")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertTrue(((context.hasEndpoint("direct://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://bar")) != null));
        Assert.assertEquals(6, context.getEndpointRegistry().staticSize());
        // we dont have this endpoint yet
        Assert.assertFalse(((context.hasEndpoint("mock://unknown0")) != null));
        for (int i = 0; i < 50; i++) {
            template.sendBody(("mock:unknown" + i), ("Hello " + i));
        }
        Assert.assertEquals(6, context.getEndpointRegistry().staticSize());
        Assert.assertTrue(((context.hasEndpoint("direct://start")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://start")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertTrue(((context.hasEndpoint("direct://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://bar")) != null));
        // now stop and remove the bar route
        context.getRouteController().stopRoute("bar");
        context.removeRoute("bar");
        Assert.assertEquals(4, context.getEndpointRegistry().staticSize());
        Assert.assertTrue(((context.hasEndpoint("direct://start")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://start")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertFalse(((context.hasEndpoint("direct://bar")) != null));
        Assert.assertFalse(((context.hasEndpoint("log://bar")) != null));
    }
}

