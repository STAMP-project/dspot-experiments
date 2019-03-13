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


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to verify that redelivery counters is working as expected.
 */
public class DeadLetterChannelRedeliveryTest extends ContextTestSupport {
    private static int counter;

    private static int redeliveryCounter;

    @Test
    public void testTwoRedeliveryTest() throws Exception {
        DeadLetterChannelRedeliveryTest.counter = 0;
        DeadLetterChannelRedeliveryTest.redeliveryCounter = 0;
        getMockEndpoint("mock:error").expectedMessageCount(1);
        template.sendBody("direct:two", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(3, DeadLetterChannelRedeliveryTest.counter);// One call + 2 re-deliveries

        Assert.assertEquals(2, DeadLetterChannelRedeliveryTest.redeliveryCounter);// 2 re-deliveries

    }

    @Test
    public void testNoRedeliveriesTest() throws Exception {
        DeadLetterChannelRedeliveryTest.counter = 0;
        DeadLetterChannelRedeliveryTest.redeliveryCounter = 0;
        getMockEndpoint("mock:error").expectedMessageCount(1);
        template.sendBody("direct:no", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(1, DeadLetterChannelRedeliveryTest.counter);// One call

        Assert.assertEquals(0, DeadLetterChannelRedeliveryTest.redeliveryCounter);// no redeliveries

    }

    @Test
    public void testOneRedeliveriesTest() throws Exception {
        DeadLetterChannelRedeliveryTest.counter = 0;
        DeadLetterChannelRedeliveryTest.redeliveryCounter = 0;
        getMockEndpoint("mock:error").expectedMessageCount(1);
        template.sendBody("direct:one", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(2, DeadLetterChannelRedeliveryTest.counter);// One call + 1 redelivery

        Assert.assertEquals(1, DeadLetterChannelRedeliveryTest.redeliveryCounter);// 1 redelivery

    }
}

