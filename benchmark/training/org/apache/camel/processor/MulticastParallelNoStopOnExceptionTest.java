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


import java.util.concurrent.ExecutorService;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class MulticastParallelNoStopOnExceptionTest extends ContextTestSupport {
    private ExecutorService service;

    @Test
    public void testMulticastParallelNoStopOnExceptionOk() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello");
        getMockEndpoint("mock:bar").expectedBodiesReceived("Hello");
        getMockEndpoint("mock:baz").expectedBodiesReceived("Hello");
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello");
        template.sendBody("direct:start", "Hello");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMulticastParalllelNoStopOnExceptionStop() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("Kaboom");
        getMockEndpoint("mock:bar").expectedMessageCount(0);
        // we do NOT stop so baz should also receive a message
        getMockEndpoint("mock:baz").expectedMessageCount(1);
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "Kaboom");
            Assert.fail("Should thrown an exception");
        } catch (CamelExecutionException e) {
            IllegalArgumentException cause = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Forced", cause.getMessage());
        }
        assertMockEndpointsSatisfied();
    }
}

