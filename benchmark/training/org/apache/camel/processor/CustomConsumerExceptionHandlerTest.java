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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.spi.ExceptionHandler;
import org.junit.Assert;
import org.junit.Test;


public class CustomConsumerExceptionHandlerTest extends ContextTestSupport {
    private static final CountDownLatch LATCH = new CountDownLatch(1);

    @Test
    public void testDeadLetterChannelAlwaysHandled() throws Exception {
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        getMockEndpoint("mock:bar").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        template.sendBody("seda:foo", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertTrue("Should have been called", CustomConsumerExceptionHandlerTest.LATCH.await(5, TimeUnit.SECONDS));
    }

    private final class MyExceptionHandler implements ExceptionHandler {
        @Override
        public void handleException(Throwable exception) {
            CustomConsumerExceptionHandlerTest.LATCH.countDown();
        }

        @Override
        public void handleException(String message, Throwable exception) {
            CustomConsumerExceptionHandlerTest.LATCH.countDown();
        }

        @Override
        public void handleException(String message, Exchange exchange, Throwable exception) {
            CustomConsumerExceptionHandlerTest.LATCH.countDown();
        }
    }
}

