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
package org.apache.camel.issues;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
// END SNIPPET: e2
public class StopRouteFromRouteTest extends Assert {
    final CountDownLatch latch = new CountDownLatch(1);

    // START SNIPPET: e1
    @Test
    public void testStopRouteFromRoute() throws Exception {
        // create camel, add routes, and start camel
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(createMyRoutes());
        context.start();
        Assert.assertTrue("Route myRoute should be started", context.getRouteController().getRouteStatus("myRoute").isStarted());
        Assert.assertTrue("Route bar should be started", context.getRouteController().getRouteStatus("bar").isStarted());
        // setup mock expectations for unit test
        MockEndpoint start = context.getEndpoint("mock:start", MockEndpoint.class);
        start.expectedMessageCount(1);
        MockEndpoint done = context.getEndpoint("mock:done", MockEndpoint.class);
        done.expectedMessageCount(1);
        // send a message to the route
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:start", "Hello Camel");
        // just wait a bit for the thread to stop the route
        latch.await(5, TimeUnit.SECONDS);
        // the route should now be stopped
        Assert.assertTrue("Route myRoute should be stopped", context.getRouteController().getRouteStatus("myRoute").isStopped());
        Assert.assertTrue("Route bar should be started", context.getRouteController().getRouteStatus("bar").isStarted());
        // stop camel
        context.stop();
        // unit test assertions
        start.assertIsSatisfied();
        done.assertIsSatisfied();
    }
}

