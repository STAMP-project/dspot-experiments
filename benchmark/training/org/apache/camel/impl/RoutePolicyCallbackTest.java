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
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class RoutePolicyCallbackTest extends ContextTestSupport {
    protected RoutePolicyCallbackTest.MyRoutePolicy policy = new RoutePolicyCallbackTest.MyRoutePolicy();

    public static class MyRoutePolicy extends RoutePolicySupport {
        boolean begin;

        boolean done;

        boolean init;

        boolean remove;

        boolean resume;

        boolean start;

        boolean stop;

        boolean suspend;

        boolean doStart;

        boolean doStop;

        @Override
        public void onExchangeBegin(Route route, Exchange exchange) {
            begin = true;
        }

        @Override
        public void onExchangeDone(Route route, Exchange exchange) {
            done = true;
        }

        @Override
        public void onInit(Route route) {
            init = true;
        }

        @Override
        public void onRemove(Route route) {
            remove = true;
        }

        @Override
        public void onResume(Route route) {
            resume = true;
        }

        @Override
        public void onStart(Route route) {
            start = true;
        }

        @Override
        public void onStop(Route route) {
            stop = true;
        }

        @Override
        public void onSuspend(Route route) {
            suspend = true;
        }

        @Override
        protected void doStop() throws Exception {
            doStop = true;
        }

        @Override
        protected void doStart() throws Exception {
            doStart = true;
        }
    }

    @Test
    public void testCallback() throws Exception {
        policy = getAndInitMyRoutePolicy();
        Assert.assertTrue(policy.doStart);
        Assert.assertTrue(policy.init);
        Assert.assertFalse(policy.begin);
        Assert.assertFalse(policy.done);
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(policy.begin);
        Assert.assertTrue(policy.done);
        Assert.assertFalse(policy.suspend);
        context.getRouteController().suspendRoute("foo");
        Assert.assertTrue(policy.suspend);
        Assert.assertFalse(policy.resume);
        context.getRouteController().resumeRoute("foo");
        Assert.assertTrue(policy.resume);
        Assert.assertFalse(policy.stop);
        context.getRouteController().stopRoute("foo");
        Assert.assertTrue(policy.stop);
        // previously started, so force flag to be false
        policy.start = false;
        Assert.assertFalse(policy.start);
        context.getRouteController().startRoute("foo");
        Assert.assertTrue(policy.start);
        Assert.assertFalse(policy.remove);
        context.getRouteController().stopRoute("foo");
        context.removeRoute("foo");
        Assert.assertTrue(policy.remove);
        // stop camel which should stop policy as well
        context.stop();
        Assert.assertTrue(policy.doStop);
    }
}

