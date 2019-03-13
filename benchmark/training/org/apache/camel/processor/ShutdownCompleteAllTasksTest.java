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
package org.apache.camel.processor;


import Exchange.BATCH_SIZE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class ShutdownCompleteAllTasksTest extends ContextTestSupport {
    private static String url = "file:target/data/pending?initialDelay=0&delay=10&synchronous=true";

    private static AtomicInteger counter = new AtomicInteger();

    private static CountDownLatch latch = new CountDownLatch(2);

    @Test
    public void testShutdownCompleteAllTasks() throws Exception {
        // give it 30 seconds to shutdown
        context.getShutdownStrategy().setTimeout(30);
        // start route
        context.getRouteController().startRoute("foo");
        MockEndpoint bar = getMockEndpoint("mock:bar");
        bar.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
        int batch = bar.getReceivedExchanges().get(0).getProperty(BATCH_SIZE, int.class);
        // wait for latch
        ShutdownCompleteAllTasksTest.latch.await(10, TimeUnit.SECONDS);
        // shutdown during processing
        context.stop();
        // should route all
        Assert.assertEquals("Should complete all messages", batch, ShutdownCompleteAllTasksTest.counter.get());
    }

    public static class MyProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            ShutdownCompleteAllTasksTest.counter.incrementAndGet();
            ShutdownCompleteAllTasksTest.latch.countDown();
        }
    }
}

