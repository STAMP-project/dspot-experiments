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
package org.apache.camel.component.controlbus;


import ServiceStatus.Started;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ControlBusStartRouteTest extends ContextTestSupport {
    @Test
    public void testControlBusStartStop() throws Exception {
        Assert.assertEquals("Stopped", context.getRouteController().getRouteStatus("foo").name());
        // store a pending message
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        template.sendBody("seda:foo", "Hello World");
        // start the route using control bus
        template.sendBody("controlbus:route?routeId=foo&action=start", null);
        assertMockEndpointsSatisfied();
        // now stop the route, using a header
        template.sendBody("controlbus:route?routeId=foo&action=stop", null);
        Assert.assertEquals("Stopped", context.getRouteController().getRouteStatus("foo").name());
    }

    @Test
    public void testControlBusSuspendResume() throws Exception {
        Assert.assertEquals("Stopped", context.getRouteController().getRouteStatus("foo").name());
        // store a pending message
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        template.sendBody("seda:foo", "Hello World");
        // start the route using control bus
        template.sendBody("controlbus:route?routeId=foo&action=start", null);
        assertMockEndpointsSatisfied();
        // now suspend the route, using a header
        template.sendBody("controlbus:route?routeId=foo&action=suspend", null);
        Assert.assertEquals("Suspended", context.getRouteController().getRouteStatus("foo").name());
        // now resume the route, using a header
        template.sendBody("controlbus:route?routeId=foo&action=resume", null);
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("foo").name());
    }

    @Test
    public void testControlBusStatus() throws Exception {
        Assert.assertEquals("Stopped", context.getRouteController().getRouteStatus("foo").name());
        String status = template.requestBody("controlbus:route?routeId=foo&action=status", null, String.class);
        Assert.assertEquals("Stopped", status);
        context.getRouteController().startRoute("foo");
        status = template.requestBody("controlbus:route?routeId=foo&action=status", null, String.class);
        Assert.assertEquals("Started", status);
    }

    @Test
    public void testControlBusCurrentRouteStatus() throws Exception {
        Assert.assertTrue(context.getRouteController().getRouteStatus("current").isStarted());
        MockEndpoint mock = getMockEndpoint("mock:current");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(Started.name());
        sendBody("seda:current", null);
        mock.assertIsSatisfied();
    }

    @Test
    public void testControlBusStatusLevelWarn() throws Exception {
        Assert.assertEquals("Stopped", context.getRouteController().getRouteStatus("foo").name());
        String status = template.requestBody("controlbus:route?routeId=foo&action=status&loggingLevel=WARN", null, String.class);
        Assert.assertEquals("Stopped", status);
        context.getRouteController().startRoute("foo");
        status = template.requestBody("controlbus:route?routeId=foo&action=status&loggingLevel=WARN", null, String.class);
        Assert.assertEquals("Started", status);
    }
}

