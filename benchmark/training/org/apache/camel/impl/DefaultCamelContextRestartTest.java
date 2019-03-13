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


/**
 *
 */
public class DefaultCamelContextRestartTest extends ContextTestSupport {
    @Test
    public void testRestart() throws Exception {
        Assert.assertTrue(context.getStatus().isStarted());
        Assert.assertFalse(context.getStatus().isStopped());
        Assert.assertEquals(1, context.getRoutes().size());
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        // now stop
        context.stop();
        Assert.assertFalse(context.getStatus().isStarted());
        Assert.assertTrue(context.getStatus().isStopped());
        Assert.assertEquals(0, context.getRoutes().size());
        // now start
        context.start();
        Assert.assertTrue(context.getStatus().isStarted());
        Assert.assertFalse(context.getStatus().isStopped());
        Assert.assertEquals(1, context.getRoutes().size());
        // must obtain a new template
        template = context.createProducerTemplate();
        // should still work
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Bye World");
        assertMockEndpointsSatisfied();
    }
}

