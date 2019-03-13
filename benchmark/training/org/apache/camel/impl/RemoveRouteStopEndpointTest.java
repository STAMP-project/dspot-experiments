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
import org.apache.camel.Endpoint;
import org.junit.Assert;
import org.junit.Test;


public class RemoveRouteStopEndpointTest extends ContextTestSupport {
    @Test
    public void testEndpointRegistryStopRouteEndpoints() throws Exception {
        Endpoint seda = context.hasEndpoint("seda://foo");
        Assert.assertNotNull(seda);
        Endpoint log = context.hasEndpoint("log://bar");
        Assert.assertNotNull(log);
        Assert.assertTrue("Should be started", isStarted());
        Assert.assertTrue("Should be started", isStarted());
        Assert.assertTrue(((context.hasEndpoint("seda:foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("seda:bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertTrue(((context.hasEndpoint("seda://stop")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://stop")) != null));
        // stop and remove bar route
        context.getRouteController().stopRoute("bar");
        context.removeRoute("bar");
        Assert.assertTrue(((context.hasEndpoint("seda://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://bar")) != null));
        Assert.assertFalse(((context.hasEndpoint("log://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertTrue(((context.hasEndpoint("seda://stop")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://stop")) != null));
        Assert.assertTrue("Should be started", isStarted());
        Assert.assertTrue("Should be stopped", isStopped());
        // stop and remove baz route
        context.getRouteController().stopRoute("baz");
        context.removeRoute("baz");
        Assert.assertTrue(((context.hasEndpoint("seda://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://bar")) != null));
        Assert.assertFalse(((context.hasEndpoint("log://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://stop")) != null));
        Assert.assertFalse(((context.hasEndpoint("mock://stop")) != null));
        // stop and remove foo route
        context.getRouteController().stopRoute("foo");
        context.removeRoute("foo");
        Assert.assertFalse(((context.hasEndpoint("seda://foo")) != null));
        Assert.assertFalse(((context.hasEndpoint("log://foo")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://bar")) != null));
        Assert.assertFalse(((context.hasEndpoint("log://bar")) != null));
        Assert.assertFalse(((context.hasEndpoint("mock://result")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://stop")) != null));
        Assert.assertFalse(((context.hasEndpoint("mock://stop")) != null));
        Assert.assertFalse("Should not be started", isStarted());
        Assert.assertFalse("Should not be started", isStarted());
    }

    @Test
    public void testEndpointRegistryStopRouteEndpointsContextStop() throws Exception {
        Endpoint seda = context.hasEndpoint("seda://foo");
        Assert.assertNotNull(seda);
        Endpoint log = context.hasEndpoint("log://bar");
        Assert.assertNotNull(log);
        Assert.assertTrue("Should be started", isStarted());
        Assert.assertTrue("Should be started", isStarted());
        Assert.assertTrue(((context.hasEndpoint("seda://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("seda://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertTrue(((context.hasEndpoint("seda://stop")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://stop")) != null));
        // stop and remove bar route
        context.getRouteController().stopRoute("bar");
        context.removeRoute("bar");
        Assert.assertTrue("Should be started", isStarted());
        Assert.assertTrue("Should be stopped", isStopped());
        Assert.assertTrue(((context.hasEndpoint("seda:foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://bar")) != null));
        Assert.assertFalse(((context.hasEndpoint("log://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertTrue(((context.hasEndpoint("seda://stop")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://stop")) != null));
        // stop and remove baz route
        context.getRouteController().stopRoute("baz");
        context.removeRoute("baz");
        Assert.assertTrue(((context.hasEndpoint("seda://foo")) != null));
        Assert.assertTrue(((context.hasEndpoint("log://foo")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://bar")) != null));
        Assert.assertFalse(((context.hasEndpoint("log://bar")) != null));
        Assert.assertTrue(((context.hasEndpoint("mock://result")) != null));
        Assert.assertFalse(((context.hasEndpoint("seda://stop")) != null));
        Assert.assertFalse(((context.hasEndpoint("mock://stop")) != null));
        // stop camel which should stop the endpoint
        context.stop();
        Assert.assertFalse("Should not be started", isStarted());
        Assert.assertFalse("Should not be started", isStarted());
    }
}

