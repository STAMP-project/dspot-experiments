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
import java.util.concurrent.RejectedExecutionException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AsyncProcessorAwaitManagerInterruptWithRedeliveryTest extends ContextTestSupport {
    private CountDownLatch latch;

    private AsyncProcessorAwaitManagerInterruptWithRedeliveryTest.MyBean bean;

    @Test
    public void testAsyncAwaitInterrupt() throws Exception {
        context.getAsyncProcessorAwaitManager().getStatistics().setStatisticsEnabled(true);
        Assert.assertEquals(0, context.getAsyncProcessorAwaitManager().size());
        getMockEndpoint("mock:before").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:error").expectedMessageCount(0);
        createThreadToInterrupt();
        try {
            template.sendBody("direct:start", "Hello Camel");
            Assert.fail("Should throw exception");
        } catch (CamelExecutionException e) {
            RejectedExecutionException cause = TestSupport.assertIsInstanceOf(RejectedExecutionException.class, e.getCause());
            Assert.assertTrue(cause.getMessage().startsWith("Interrupted while waiting for asynchronous callback"));
        }
        assertMockEndpointsSatisfied();
        // Check we have not reached the full 5 re-deliveries
        Mockito.verify(bean, Mockito.atMost(4)).callMe();
        Assert.assertEquals(0, context.getAsyncProcessorAwaitManager().size());
        Assert.assertEquals(1, context.getAsyncProcessorAwaitManager().getStatistics().getThreadsBlocked());
        Assert.assertEquals(1, context.getAsyncProcessorAwaitManager().getStatistics().getThreadsInterrupted());
    }

    public static class MyBean {
        private CountDownLatch latch;

        public MyBean(CountDownLatch latch) {
            this.latch = latch;
        }

        public void callMe() throws Exception {
            latch.countDown();
            throw new Exception();
        }
    }
}

