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


import Exchange.EXCEPTION_CAUGHT;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class TryCatchWithSplitIssueTest extends ContextTestSupport {
    @Test
    public void testSplitWithErrorIsHandled() throws Exception {
        MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedBodiesReceived("James");
        error.message(0).exchangeProperty(EXCEPTION_CAUGHT).isNotNull();
        error.message(0).exchangeProperty(EXCEPTION_CAUGHT).method("getMessage").isEqualTo("This is a dummy error James!");
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedBodiesReceived("Hi Claus", "Hi Willem");
        template.sendBody("direct:start", "Claus@James@Willem");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSplitOnlyWithErrorIsHandled() throws Exception {
        MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedBodiesReceived("James");
        error.message(0).exchangeProperty(EXCEPTION_CAUGHT).isNotNull();
        error.message(0).exchangeProperty(EXCEPTION_CAUGHT).method("getMessage").isEqualTo("This is a dummy error James!");
        template.sendBody("direct:start", "James");
        assertMockEndpointsSatisfied();
    }

    public static class GenerateError {
        public String dummyException(String payload) throws Exception {
            if (payload.equals("James")) {
                throw new IllegalArgumentException("This is a dummy error James!");
            }
            return "Hi " + payload;
        }
    }
}

