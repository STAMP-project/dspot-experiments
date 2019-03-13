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
package org.apache.camel.processor.exceptionpolicy;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the when expression on the exception type.
 */
public class DefaultExceptionPolicyStrategyUsingWhenTest extends ContextTestSupport {
    private static final String ERROR_QUEUE = "mock:error";

    private static final String ERROR_USER_QUEUE = "mock:usererror";

    public static class MyUserException extends Exception {
        private static final long serialVersionUID = 1L;

        public MyUserException(String message) {
            super(message);
        }
    }

    @Test
    public void testNoWhen() throws Exception {
        MockEndpoint mock = getMockEndpoint(DefaultExceptionPolicyStrategyUsingWhenTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        try {
            template.sendBody("direct:a", "Hello Camel");
            Assert.fail("Should have thrown an Exception");
        } catch (Exception e) {
            // expected
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testWithWhen() throws Exception {
        MockEndpoint mock = getMockEndpoint(DefaultExceptionPolicyStrategyUsingWhenTest.ERROR_USER_QUEUE);
        mock.expectedMessageCount(1);
        try {
            template.sendBodyAndHeader("direct:a", "Hello Camel", "user", "admin");
            Assert.fail("Should have thrown an Exception");
        } catch (Exception e) {
            // expected
        }
        assertMockEndpointsSatisfied();
    }
}

