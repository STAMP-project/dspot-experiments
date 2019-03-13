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
import java.security.GeneralSecurityException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to test exception configuration
 */
public class ExceptionBuilderTest extends ContextTestSupport {
    private static final String MESSAGE_INFO = "messageInfo";

    private static final String RESULT_QUEUE = "mock:result";

    private static final String ERROR_QUEUE = "mock:error";

    private static final String BUSINESS_ERROR_QUEUE = "mock:badBusiness";

    private static final String SECURITY_ERROR_QUEUE = "mock:securityError";

    @Test
    public void testNPE() throws Exception {
        MockEndpoint result = getMockEndpoint(ExceptionBuilderTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderTest.MESSAGE_INFO, "Damm a NPE");
        try {
            template.sendBody("direct:a", "Hello NPE");
            Assert.fail("Should have thrown a NullPointerException");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof NullPointerException));
            // expected
        }
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    @Test
    public void testIOException() throws Exception {
        MockEndpoint result = getMockEndpoint(ExceptionBuilderTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderTest.MESSAGE_INFO, "Damm somekind of IO exception");
        try {
            template.sendBody("direct:a", "Hello IO");
            Assert.fail("Should have thrown a IOException");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
            // expected
        }
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    @Test
    public void testException() throws Exception {
        MockEndpoint result = getMockEndpoint(ExceptionBuilderTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderTest.MESSAGE_INFO, "Damm just exception");
        try {
            template.sendBody("direct:a", "Hello Exception");
            Assert.fail("Should have thrown a Exception");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof Exception));
            // expected
        }
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    @Test
    public void testMyBusinessException() throws Exception {
        MockEndpoint result = getMockEndpoint(ExceptionBuilderTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderTest.BUSINESS_ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderTest.MESSAGE_INFO, "Damm my business is not going to well");
        try {
            template.sendBody("direct:a", "Hello business");
            Assert.fail("Should have thrown a MyBusinessException");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof ExceptionBuilderTest.MyBusinessException));
            // expected
        }
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    @Test
    public void testSecurityConfiguredWithTwoExceptions() throws Exception {
        // test that we also handles a configuration with 2 or more exceptions
        MockEndpoint result = getMockEndpoint(ExceptionBuilderTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderTest.SECURITY_ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderTest.MESSAGE_INFO, "Damm some security error");
        try {
            template.sendBody("direct:a", "I am not allowed to do this");
            Assert.fail("Should have thrown a GeneralSecurityException");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof GeneralSecurityException));
            // expected
        }
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    @Test
    public void testSecurityConfiguredWithExceptionList() throws Exception {
        // test that we also handles a configuration with a list of exceptions
        MockEndpoint result = getMockEndpoint(ExceptionBuilderTest.RESULT_QUEUE);
        result.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint(ExceptionBuilderTest.ERROR_QUEUE);
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(ExceptionBuilderTest.MESSAGE_INFO, "Damm some access error");
        try {
            template.sendBody("direct:a", "I am not allowed to access this");
            Assert.fail("Should have thrown a GeneralSecurityException");
        } catch (RuntimeCamelException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalAccessException));
            // expected
        }
        MockEndpoint.assertIsSatisfied(result, mock);
    }

    public static class MyBaseBusinessException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class MyBusinessException extends ExceptionBuilderTest.MyBaseBusinessException {
        private static final long serialVersionUID = 1L;
    }
}

