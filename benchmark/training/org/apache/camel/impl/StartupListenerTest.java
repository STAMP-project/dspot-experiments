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


import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.StartupListener;
import org.junit.Assert;
import org.junit.Test;


public class StartupListenerTest extends ContextTestSupport {
    private StartupListenerTest.MyStartupListener my = new StartupListenerTest.MyStartupListener();

    private static class MyStartupListener implements StartupListener {
        private int invoked;

        private boolean alreadyStarted;

        public void onCamelContextStarted(CamelContext context, boolean alreadyStarted) throws Exception {
            (invoked)++;
            this.alreadyStarted = alreadyStarted;
            if (alreadyStarted) {
                // the routes should already been started as we add the listener afterwards
                Assert.assertTrue(context.getRouteController().getRouteStatus("foo").isStarted());
            } else {
                // the routes should not have been started as they start afterwards
                Assert.assertTrue(context.getRouteController().getRouteStatus("foo").isStopped());
            }
        }

        public int getInvoked() {
            return invoked;
        }

        public boolean isAlreadyStarted() {
            return alreadyStarted;
        }
    }

    @Test
    public void testStartupListenerComponent() throws Exception {
        // and now the routes are started
        Assert.assertTrue(context.getRouteController().getRouteStatus("foo").isStarted());
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:foo", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(1, my.getInvoked());
        Assert.assertFalse(my.isAlreadyStarted());
    }

    @Test
    public void testStartupListenerComponentAlreadyStarted() throws Exception {
        // and now the routes are started
        Assert.assertTrue(context.getRouteController().getRouteStatus("foo").isStarted());
        StartupListenerTest.MyStartupListener other = new StartupListenerTest.MyStartupListener();
        context.addStartupListener(other);
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:foo", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(1, other.getInvoked());
        Assert.assertTrue(other.isAlreadyStarted());
    }
}

