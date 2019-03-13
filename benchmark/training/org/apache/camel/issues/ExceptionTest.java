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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class ExceptionTest extends ContextTestSupport {
    @Test
    public void testExceptionWithoutHandler() throws Exception {
        MockEndpoint errorEndpoint = getMockEndpoint("mock:error");
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        MockEndpoint exceptionEndpoint = getMockEndpoint("mock:exception");
        errorEndpoint.expectedBodiesReceived("<exception/>");
        exceptionEndpoint.expectedMessageCount(0);
        resultEndpoint.expectedMessageCount(0);
        // we don't expect any thrown exception here as there's no onException clause defined for this test
        // so that the general purpose dead letter channel will come into the play and then when all the attempts
        // to redelivery fails the exchange will be moved to "mock:error" and then from the client point of
        // view the exchange is completed.
        template.sendBody("direct:start", "<body/>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionWithHandler() throws Exception {
        MockEndpoint errorEndpoint = getMockEndpoint("mock:error");
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        MockEndpoint exceptionEndpoint = getMockEndpoint("mock:exception");
        errorEndpoint.expectedMessageCount(0);
        exceptionEndpoint.expectedBodiesReceived("<exception/>");
        resultEndpoint.expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "<body/>");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // expected
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionWithLongHandler() throws Exception {
        MockEndpoint errorEndpoint = getMockEndpoint("mock:error");
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        MockEndpoint exceptionEndpoint = getMockEndpoint("mock:exception");
        errorEndpoint.expectedMessageCount(0);
        exceptionEndpoint.expectedBodiesReceived("<not-handled/>");
        resultEndpoint.expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "<body/>");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // expected
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLongRouteWithHandler() throws Exception {
        MockEndpoint errorEndpoint = getMockEndpoint("mock:error");
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        MockEndpoint exceptionEndpoint = getMockEndpoint("mock:exception");
        errorEndpoint.expectedMessageCount(0);
        exceptionEndpoint.expectedBodiesReceived("<exception/>");
        resultEndpoint.expectedMessageCount(0);
        try {
            template.sendBody("direct:start2", "<body/>");
            Assert.fail("Should have thrown exception");
        } catch (Exception e) {
            // expected
        }
        assertMockEndpointsSatisfied();
    }
}

