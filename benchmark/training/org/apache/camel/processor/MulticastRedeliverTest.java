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


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class MulticastRedeliverTest extends ContextTestSupport {
    private static int counter;

    @Test
    public void testOk() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        template.sendBody("direct:test1", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testThrowExceptionAtA() throws Exception {
        MulticastRedeliverTest.counter = 0;
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(0);
        try {
            template.sendBody("direct:test2", "Hello World");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            Assert.assertEquals("Forced", e.getCause().getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals((1 + 3), MulticastRedeliverTest.counter);// first call + 3 redeliveries

    }

    @Test
    public void testThrowExceptionAtB() throws Exception {
        MulticastRedeliverTest.counter = 0;
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        getMockEndpoint("mock:c").expectedMessageCount(0);
        try {
            template.sendBody("direct:test3", "Hello World");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            Assert.assertEquals("Forced", e.getCause().getCause().getMessage());
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals((1 + 3), MulticastRedeliverTest.counter);// first call + 3 redeliveries

    }
}

