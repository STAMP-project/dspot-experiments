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
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.throttling.ThrottlingExceptionRoutePolicy;
import org.junit.Test;


public class ThrottlingExceptionRoutePolicyKeepOpenOnInitTest extends ContextTestSupport {
    private String url = "seda:foo?concurrentConsumers=20";

    private MockEndpoint result;

    private int size = 5;

    private ThrottlingExceptionRoutePolicy policy;

    @Test
    public void testThrottlingRoutePolicyStartWithAlwaysOpenOn() throws Exception {
        log.debug("---- sending some messages");
        for (int i = 0; i < (size); i++) {
            template.sendBody(url, ("Message " + i));
            Thread.sleep(3);
        }
        // gives time for policy half open check to run every second
        // and should not close b/c keepOpen is true
        Thread.sleep(500);
        // gives time for policy half open check to run every second
        // but it should never close b/c keepOpen is true
        result.expectedMessageCount(0);
        result.setResultWaitTime(1000);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testThrottlingRoutePolicyStartWithAlwaysOpenOnThenClose() throws Exception {
        for (int i = 0; i < (size); i++) {
            template.sendBody(url, ("Message " + i));
            Thread.sleep(3);
        }
        // gives time for policy half open check to run every second
        // and should not close b/c keepOpen is true
        Thread.sleep(500);
        result.expectedMessageCount(0);
        result.setResultWaitTime(1500);
        assertMockEndpointsSatisfied();
        // set keepOpen to false
        // now half open check will succeed
        policy.setKeepOpen(false);
        // gives time for policy half open check to run every second
        // and should close and get all the messages
        result.expectedMessageCount(5);
        result.setResultWaitTime(1500);
        assertMockEndpointsSatisfied();
    }
}

