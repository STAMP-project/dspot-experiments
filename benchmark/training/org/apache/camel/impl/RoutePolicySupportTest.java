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
import ServiceStatus.Suspended;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.junit.Assert;
import org.junit.Test;


public class RoutePolicySupportTest extends ContextTestSupport {
    private RoutePolicySupportTest.MyRoutePolicy policy = new RoutePolicySupportTest.MyRoutePolicy();

    public static class MyRoutePolicy extends RoutePolicySupport {}

    @Test
    public void testLifecycleCallbacks() throws Exception {
        Route route = context.getRoute("foo");
        Assert.assertEquals(Stopped, context.getRouteController().getRouteStatus("foo"));
        policy.startRoute(route);
        Assert.assertEquals(Started, context.getRouteController().getRouteStatus("foo"));
        policy.suspendRoute(route);
        Assert.assertEquals(Suspended, context.getRouteController().getRouteStatus("foo"));
        policy.resumeRoute(route);
        Assert.assertEquals(Started, context.getRouteController().getRouteStatus("foo"));
        policy.stopRoute(route);
        Assert.assertEquals(Stopped, context.getRouteController().getRouteStatus("foo"));
    }
}

