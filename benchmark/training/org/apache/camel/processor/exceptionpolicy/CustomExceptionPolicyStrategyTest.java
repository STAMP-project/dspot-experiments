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


import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.OnExceptionDefinition;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test with a user plugged in exception policy to use instead of default.
 */
public class CustomExceptionPolicyStrategyTest extends ContextTestSupport {
    private static final String MESSAGE_INFO = "messageInfo";

    private static final String ERROR_QUEUE = "mock:error";

    public static class MyPolicyException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    // START SNIPPET e2
    public static class MyPolicy implements ExceptionPolicyStrategy {
        public OnExceptionDefinition getExceptionPolicy(Map<ExceptionPolicyKey, OnExceptionDefinition> exceptionPolicices, Exchange exchange, Throwable exception) {
            // This is just an example that always forces the exception type configured
            // with MyPolicyException to win.
            return exceptionPolicices.get(new ExceptionPolicyKey(null, CustomExceptionPolicyStrategyTest.MyPolicyException.class, null));
        }
    }

    // END SNIPPET e2
    @Test
    public void testCustomPolicy() throws Exception {
        MockEndpoint mock = getMockEndpoint(CustomExceptionPolicyStrategyTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(CustomExceptionPolicyStrategyTest.MESSAGE_INFO, "Damm my policy exception");
        try {
            template.sendBody("direct:a", "Hello Camel");
            Assert.fail("Should have thrown an exception");
        } catch (Exception e) {
            // expected
        }
        mock.assertIsSatisfied();
    }
}

