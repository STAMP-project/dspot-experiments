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


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Assert;
import org.junit.Test;


public class RedeliveryDeadLetterErrorHandlerNoRedeliveryOnShutdownTest extends ContextTestSupport {
    private final AtomicInteger counter = new AtomicInteger();

    @Test
    public void testRedeliveryErrorHandlerNoRedeliveryOnShutdown() throws Exception {
        getMockEndpoint("mock:foo").expectedMessageCount(1);
        getMockEndpoint("mock:deadLetter").expectedMessageCount(1);
        getMockEndpoint("mock:deadLetter").setResultWaitTime(25000);
        template.sendBody("seda:foo", "Hello World");
        getMockEndpoint("mock:foo").assertIsSatisfied();
        // should not take long to stop the route
        StopWatch watch = new StopWatch();
        // sleep 0.5 seconds to do some redeliveries before we stop
        Thread.sleep(500);
        log.info("==== stopping route foo ====");
        context.getRouteController().stopRoute("foo");
        long taken = watch.taken();
        getMockEndpoint("mock:deadLetter").assertIsSatisfied();
        log.info("OnRedelivery processor counter {}", counter.get());
        Assert.assertTrue(("Should stop route faster, was " + taken), (taken < 5000));
        Assert.assertTrue(("Redelivery counter should be >= 20 and < 100, was: " + (counter.get())), (((counter.get()) >= 20) && ((counter.get()) < 100)));
    }

    private final class MyRedeliverProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            counter.incrementAndGet();
        }
    }
}

