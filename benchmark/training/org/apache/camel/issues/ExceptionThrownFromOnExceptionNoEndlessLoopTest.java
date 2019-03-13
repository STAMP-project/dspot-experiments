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


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Assert;
import org.junit.Test;


public class ExceptionThrownFromOnExceptionNoEndlessLoopTest extends ContextTestSupport {
    private static final AtomicInteger RETRY = new AtomicInteger();

    private static final AtomicInteger ON_EXCEPTION_RETRY = new AtomicInteger();

    private static final AtomicInteger ON_EXCEPTION_2_RETRY = new AtomicInteger();

    @Test
    public void testExceptionThrownFromOnExceptionNoEndlessLoopTest() throws Exception {
        ExceptionThrownFromOnExceptionNoEndlessLoopTest.RETRY.set(0);
        ExceptionThrownFromOnExceptionNoEndlessLoopTest.ON_EXCEPTION_RETRY.set(0);
        ExceptionThrownFromOnExceptionNoEndlessLoopTest.ON_EXCEPTION_2_RETRY.set(0);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(IOException.class).redeliveryDelay(0).maximumRedeliveries(3).to("mock:b").process(new Processor() {
                    @Override
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        ExceptionThrownFromOnExceptionNoEndlessLoopTest.ON_EXCEPTION_RETRY.incrementAndGet();
                        // exception thrown here, should not trigger the
                        // onException(IllegalArgumentException.class) as we would
                        // then go into endless loop
                        throw new IllegalArgumentException("Not supported");
                    }
                }).to("mock:c");
                onException(IllegalArgumentException.class).to("mock:d").process(new Processor() {
                    @Override
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        ExceptionThrownFromOnExceptionNoEndlessLoopTest.ON_EXCEPTION_2_RETRY.incrementAndGet();
                        throw new IOException("Some other IOException");
                    }
                }).to("mock:e");
                from("direct:start").to("direct:intermediate").to("mock:result");
                from("direct:intermediate").to("mock:a").process(new Processor() {
                    @Override
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        ExceptionThrownFromOnExceptionNoEndlessLoopTest.RETRY.incrementAndGet();
                        throw new IOException("IO error");
                    }
                }).to("mock:end");
            }
        });
        context.start();
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        getMockEndpoint("mock:c").expectedMessageCount(0);
        getMockEndpoint("mock:d").expectedMessageCount(0);
        getMockEndpoint("mock:e").expectedMessageCount(0);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:end").expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "Hello World");
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            IllegalArgumentException cause = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Not supported", cause.getMessage());
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals("Should try 4 times (1 first, 3 retry)", 4, ExceptionThrownFromOnExceptionNoEndlessLoopTest.RETRY.get());
        Assert.assertEquals("Should only invoke onException once", 1, ExceptionThrownFromOnExceptionNoEndlessLoopTest.ON_EXCEPTION_RETRY.get());
        Assert.assertEquals("Should not be invoked", 0, ExceptionThrownFromOnExceptionNoEndlessLoopTest.ON_EXCEPTION_2_RETRY.get());
    }
}

