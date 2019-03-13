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


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ThrottlingGroupingTest extends ContextTestSupport {
    private static final int INTERVAL = 500;

    private static final int MESSAGE_COUNT = 9;

    private static final int TOLERANCE = 50;

    @Test
    public void testGroupingWithSingleConstant() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World", "Bye World");
        getMockEndpoint("mock:dead").expectedBodiesReceived("Kaboom");
        template.sendBodyAndHeader("seda:a", "Kaboom", "max", null);
        template.sendBodyAndHeader("seda:a", "Hello World", "max", 2);
        template.sendBodyAndHeader("seda:a", "Bye World", "max", 2);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testGroupingWithDynamicHeaderExpression() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result2").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:dead").expectedBodiesReceived("Kaboom", "Saloon");
        getMockEndpoint("mock:resultdynamic").expectedBodiesReceived("Hello Dynamic World", "Bye Dynamic World");
        Map<String, Object> headers = new HashMap<String, Object>();
        template.sendBodyAndHeaders("seda:a", "Kaboom", headers);
        template.sendBodyAndHeaders("seda:a", "Saloon", headers);
        headers.put("max", "2");
        template.sendBodyAndHeaders("seda:a", "Hello World", headers);
        template.sendBodyAndHeaders("seda:b", "Bye World", headers);
        headers.put("max", "2");
        headers.put("key", "1");
        template.sendBodyAndHeaders("seda:c", "Hello Dynamic World", headers);
        headers.put("key", "2");
        template.sendBodyAndHeaders("seda:c", "Bye Dynamic World", headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSendLotsOfMessagesButOnly3GetThroughWithin2Seconds() throws Exception {
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:gresult", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(3);
        resultEndpoint.setResultWaitTime(2000);
        Map<String, Object> headers = new HashMap<String, Object>();
        for (int i = 0; i < 9; i++) {
            if ((i % 2) == 0) {
                headers.put("key", "1");
            } else {
                headers.put("key", "2");
            }
            template.sendBodyAndHeaders("seda:ga", (("<message>" + i) + "</message>"), headers);
        }
        // lets pause to give the requests time to be processed
        // to check that the throttle really does kick in
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testSendLotsOfMessagesSimultaneouslyButOnlyGetThroughAsConstantThrottleValue() throws Exception {
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:gresult", MockEndpoint.class);
        long elapsed = sendMessagesAndAwaitDelivery(ThrottlingGroupingTest.MESSAGE_COUNT, "direct:ga", ThrottlingGroupingTest.MESSAGE_COUNT, resultEndpoint);
        assertThrottlerTiming(elapsed, 5, ThrottlingGroupingTest.INTERVAL, ThrottlingGroupingTest.MESSAGE_COUNT);
    }

    @Test
    public void testConfigurationWithHeaderExpression() throws Exception {
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:gresult", MockEndpoint.class);
        resultEndpoint.expectedMessageCount(ThrottlingGroupingTest.MESSAGE_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(ThrottlingGroupingTest.MESSAGE_COUNT);
        try {
            sendMessagesWithHeaderExpression(executor, resultEndpoint, 5, ThrottlingGroupingTest.INTERVAL, ThrottlingGroupingTest.MESSAGE_COUNT);
        } finally {
            executor.shutdownNow();
        }
    }
}

