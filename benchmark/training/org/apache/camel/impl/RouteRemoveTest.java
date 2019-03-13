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


import ServiceStatus.Started;
import ServiceStatus.Stopped;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class RouteRemoveTest extends ContextTestSupport {
    @Test
    public void testStopRouteOnContext() throws Exception {
        Assert.assertEquals(Started, getStatus());
        Assert.assertEquals(Started, context.getRouteController().getRouteStatus("foo"));
        context.getRouteController().stopRoute("foo");
        Assert.assertEquals(Stopped, getStatus());
        Assert.assertEquals(Stopped, context.getRouteController().getRouteStatus("foo"));
    }

    @Test
    public void testRemove() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("A");
        template.sendBody("seda:foo", "A");
        assertMockEndpointsSatisfied();
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("foo").name());
        Assert.assertEquals(1, context.getRoutes().size());
        // must be stopped so we cant remove
        boolean removed = context.removeRoute("foo");
        Assert.assertFalse(removed);
        Assert.assertEquals(1, context.getRoutes().size());
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("foo").name());
        // remove route then
        context.getRouteController().stopRoute("foo");
        removed = context.removeRoute("foo");
        Assert.assertTrue(removed);
        Assert.assertEquals(0, context.getRoutes().size());
        Assert.assertNull(context.getRouteController().getRouteStatus("foo"));
    }
}

