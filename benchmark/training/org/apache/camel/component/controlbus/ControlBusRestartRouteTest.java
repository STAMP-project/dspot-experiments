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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.junit.Assert;
import org.junit.Test;


public class ControlBusRestartRouteTest extends ContextTestSupport {
    private ControlBusRestartRouteTest.MyRoutePolicy myRoutePolicy = new ControlBusRestartRouteTest.MyRoutePolicy();

    @Test
    public void testControlBusRestart() throws Exception {
        Assert.assertEquals(1, myRoutePolicy.getStart());
        Assert.assertEquals(0, myRoutePolicy.getStop());
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("foo").name());
        template.sendBody("controlbus:route?routeId=foo&action=restart&restartDelay=0", null);
        Assert.assertEquals("Started", context.getRouteController().getRouteStatus("foo").name());
        Assert.assertEquals(2, myRoutePolicy.getStart());
        Assert.assertEquals(1, myRoutePolicy.getStop());
    }

    private final class MyRoutePolicy extends RoutePolicySupport {
        private int start;

        private int stop;

        @Override
        public void onStart(Route route) {
            (start)++;
        }

        @Override
        public void onStop(Route route) {
            (stop)++;
        }

        public int getStart() {
            return start;
        }

        public int getStop() {
            return stop;
        }
    }
}

