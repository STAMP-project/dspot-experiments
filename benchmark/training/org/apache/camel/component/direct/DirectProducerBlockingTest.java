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
package org.apache.camel.component.direct;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Assert;
import org.junit.Test;


public class DirectProducerBlockingTest extends ContextTestSupport {
    @Test
    public void testProducerBlocksForSuspendedConsumer() throws Exception {
        DirectEndpoint endpoint = getMandatoryEndpoint("direct:suspended", DirectEndpoint.class);
        endpoint.getConsumer().suspend();
        StopWatch watch = new StopWatch();
        try {
            template.sendBody("direct:suspended?block=true&timeout=500", "hello world");
            Assert.fail("Expected CamelExecutionException");
        } catch (CamelExecutionException e) {
            DirectConsumerNotAvailableException cause = TestSupport.assertIsInstanceOf(DirectConsumerNotAvailableException.class, e.getCause());
            TestSupport.assertIsInstanceOf(CamelExchangeException.class, cause);
            Assert.assertTrue(((watch.taken()) > 490));
        }
    }

    @Test
    public void testProducerBlocksWithNoConsumers() throws Exception {
        DirectEndpoint endpoint = getMandatoryEndpoint("direct:suspended", DirectEndpoint.class);
        endpoint.getConsumer().suspend();
        StopWatch watch = new StopWatch();
        try {
            template.sendBody("direct:start?block=true&timeout=500", "hello world");
            Assert.fail("Expected CamelExecutionException");
        } catch (CamelExecutionException e) {
            DirectConsumerNotAvailableException cause = TestSupport.assertIsInstanceOf(DirectConsumerNotAvailableException.class, e.getCause());
            TestSupport.assertIsInstanceOf(CamelExchangeException.class, cause);
            Assert.assertTrue(((watch.taken()) > 490));
        }
    }

    @Test
    public void testProducerBlocksResumeTest() throws Exception {
        context.getRouteController().suspendRoute("foo");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    log.info("Resuming consumer");
                    context.getRouteController().resumeRoute("foo");
                } catch (Exception e) {
                    // ignore
                }
            }
        });
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:suspended?block=true&timeout=1000", "hello world");
        assertMockEndpointsSatisfied();
        executor.shutdownNow();
    }
}

