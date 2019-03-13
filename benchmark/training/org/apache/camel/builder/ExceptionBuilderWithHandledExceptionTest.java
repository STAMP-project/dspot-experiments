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
package org.apache.camel.builder;


import java.io.IOException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to test exception configuration
 */
public class ExceptionBuilderWithHandledExceptionTest extends ContextTestSupport {
    private static final String MESSAGE_INFO = "messageInfo";

    private static final String RESULT_QUEUE = "mock:result";

    private static final String ERROR_QUEUE = "mock:error";

    @Test
    public void testHandledException() throws Exception {
        MockEndpoint result = getMockEndpoint(ExceptionBuilderWithHandledExceptionTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderWithHandledExceptionTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderWithHandledExceptionTest.MESSAGE_INFO, "Handled exchange with NullPointerException");
        template.sendBody("direct:a", "Hello NPE");
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    @Test
    public void testHandledExceptionWithExpression() throws Exception {
        MockEndpoint result = getMockEndpoint(ExceptionBuilderWithHandledExceptionTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderWithHandledExceptionTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderWithHandledExceptionTest.MESSAGE_INFO, "Handled exchange with IOException");
        template.sendBodyAndHeader("direct:a", "Hello IOE", "foo", "bar");
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    @Test
    public void testUnhandledException() throws Exception {
        MockEndpoint result = getMockEndpoint(ExceptionBuilderWithHandledExceptionTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderWithHandledExceptionTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderWithHandledExceptionTest.MESSAGE_INFO, "Handled exchange with IOException");
        try {
            template.sendBodyAndHeader("direct:a", "Hello IOE", "foo", "something that does not match");
            Assert.fail("Should have thrown a IOException");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
            // expected, failure is not handled because predicate doesn't match
        }
        MockEndpoint.assertIsSatisfied(result, mock);
    }
}

