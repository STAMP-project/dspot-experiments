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
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class SagaFailuresTest extends ContextTestSupport {
    private AtomicInteger maxFailures;

    @Test
    public void testCompensationAfterFailures() throws Exception {
        maxFailures = new AtomicInteger(2);
        MockEndpoint compensate = getMockEndpoint("mock:compensate");
        compensate.expectedMessageCount(1);
        sendBody("direct:saga-compensate", "hello");
        compensate.assertIsSatisfied();
    }

    @Test
    public void testNoCompensationAfterMaxFailures() throws Exception {
        maxFailures = new AtomicInteger(3);
        MockEndpoint compensate = getMockEndpoint("mock:compensate");
        compensate.expectedMessageCount(1);
        compensate.setResultWaitTime(200);
        sendBody("direct:saga-compensate", "hello");
        compensate.assertIsNotSatisfied();
    }

    @Test
    public void testCompletionAfterFailures() throws Exception {
        maxFailures = new AtomicInteger(2);
        MockEndpoint complete = getMockEndpoint("mock:complete");
        complete.expectedMessageCount(1);
        MockEndpoint end = getMockEndpoint("mock:end");
        end.expectedBodiesReceived("hello");
        sendBody("direct:saga-complete", "hello");
        complete.assertIsSatisfied();
        end.assertIsSatisfied();
    }

    @Test
    public void testNoCompletionAfterMaxFailures() throws Exception {
        maxFailures = new AtomicInteger(3);
        MockEndpoint complete = getMockEndpoint("mock:complete");
        complete.expectedMessageCount(1);
        complete.setResultWaitTime(200);
        MockEndpoint end = getMockEndpoint("mock:end");
        end.expectedBodiesReceived("hello");
        sendBody("direct:saga-complete", "hello");
        complete.assertIsNotSatisfied();
        end.assertIsSatisfied();
    }
}

