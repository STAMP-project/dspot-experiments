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
package org.apache.camel.component.seda;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class SedaTimeoutTest extends ContextTestSupport {
    private int timeout = 100;

    @Test
    public void testSedaNoTimeout() throws Exception {
        Future<String> out = template.asyncRequestBody("seda:foo", "World", String.class);
        Assert.assertEquals("Bye World", out.get());
    }

    @Test
    public void testSedaTimeout() throws Exception {
        Future<String> out = template.asyncRequestBody(("seda:foo?timeout=" + (timeout)), "World", String.class);
        try {
            out.get();
            Assert.fail("Should have thrown an exception");
        } catch (ExecutionException e) {
            TestSupport.assertIsInstanceOf(CamelExecutionException.class, e.getCause());
            TestSupport.assertIsInstanceOf(ExchangeTimedOutException.class, e.getCause().getCause());
            SedaEndpoint se = ((SedaEndpoint) (context.getRoute("seda").getEndpoint()));
            Assert.assertNotNull("Consumer endpoint cannot be null", se);
            Assert.assertEquals("Timeout Exchanges should be removed from queue", 0, se.getCurrentQueueSize());
        }
    }

    @Test
    public void testSedaTimeoutWithStoppedRoute() throws Exception {
        context.getRouteController().stopRoute("seda");
        timeout = 500;
        testSedaTimeout();
    }
}

