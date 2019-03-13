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
package org.apache.camel.processor.async;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class AsyncEndpointEventNotifierTest extends ContextTestSupport {
    private final CountDownLatch latch = new CountDownLatch(1);

    private final AtomicLong time = new AtomicLong();

    @Test
    public void testAsyncEndpointEventNotifier() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");
        String reply = template.requestBody("direct:start", "Hello Camel", String.class);
        Assert.assertEquals("Bye Camel", reply);
        assertMockEndpointsSatisfied();
        Assert.assertTrue("Should count down", latch.await(10, TimeUnit.SECONDS));
        long delta = time.get();
        log.info(("ExchangeEventSent took ms: " + delta));
        Assert.assertTrue(("Should take about 250 millis sec, was: " + delta), (delta > 200));
    }
}

