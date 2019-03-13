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
package org.apache.camel.processor.routingslip;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class RoutingSlipWithExceptionTest extends ContextTestSupport {
    protected static final String ANSWER = "answer";

    protected static final String ROUTING_SLIP_HEADER = "destinations";

    protected RoutingSlipWithExceptionTest.MyBean myBean = new RoutingSlipWithExceptionTest.MyBean();

    private MockEndpoint endEndpoint;

    private MockEndpoint exceptionEndpoint;

    private MockEndpoint exceptionSettingEndpoint;

    private MockEndpoint aEndpoint;

    @Test
    public void testNoException() throws Exception {
        endEndpoint.expectedMessageCount(1);
        exceptionEndpoint.expectedMessageCount(0);
        aEndpoint.expectedMessageCount(1);
        sendRoutingSlipWithNoExceptionThrowingComponent();
        assertEndpointsSatisfied();
    }

    @Test
    public void testWithExceptionThrowingComponentFirstInList() throws Exception {
        endEndpoint.expectedMessageCount(0);
        exceptionEndpoint.expectedMessageCount(1);
        aEndpoint.expectedMessageCount(0);
        sendRoutingSlipWithExceptionThrowingComponentFirstInList();
        assertEndpointsSatisfied();
    }

    @Test
    public void testWithExceptionThrowingComponentSecondInList() throws Exception {
        endEndpoint.expectedMessageCount(0);
        exceptionEndpoint.expectedMessageCount(1);
        aEndpoint.expectedMessageCount(1);
        sendRoutingSlipWithExceptionThrowingComponentSecondInList();
        assertEndpointsSatisfied();
    }

    @Test
    public void testWithExceptionSettingComponentFirstInList() throws Exception {
        endEndpoint.expectedMessageCount(0);
        exceptionEndpoint.expectedMessageCount(1);
        aEndpoint.expectedMessageCount(0);
        sendRoutingSlipWithExceptionSettingComponentFirstInList();
        assertEndpointsSatisfied();
    }

    @Test
    public void testWithExceptionSettingComponentSecondInList() throws Exception {
        endEndpoint.expectedMessageCount(0);
        exceptionEndpoint.expectedMessageCount(1);
        aEndpoint.expectedMessageCount(1);
        sendRoutingSlipWithExceptionSettingComponentSecondInList();
        assertEndpointsSatisfied();
    }

    public static class MyBean {
        public MyBean() {
        }

        public void throwException() throws Exception {
            throw new Exception("Throw me!");
        }
    }
}

