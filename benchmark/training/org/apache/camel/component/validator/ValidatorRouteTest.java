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
package org.apache.camel.component.validator;


import ExchangePattern.InOut;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.processor.validation.NoXmlHeaderValidationException;
import org.junit.Assert;
import org.junit.Test;


public class ValidatorRouteTest extends ContextTestSupport {
    protected MockEndpoint validEndpoint;

    protected MockEndpoint finallyEndpoint;

    protected MockEndpoint invalidEndpoint;

    @Test
    public void testValidMessage() throws Exception {
        validEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBody("direct:start", "<mail xmlns='http://foo.com/bar'><subject>Hey</subject><body>Hello world!</body></mail>");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }

    @Test
    public void testValidMessageInHeader() throws Exception {
        validEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:startHeaders", null, "headerToValidate", "<mail xmlns='http://foo.com/bar'><subject>Hey</subject><body>Hello world!</body></mail>");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }

    @Test
    public void testInvalidMessage() throws Exception {
        invalidEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBody("direct:start", "<mail xmlns='http://foo.com/bar'><body>Hello world!</body></mail>");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }

    @Test
    public void testInvalidMessageInHeader() throws Exception {
        invalidEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:startHeaders", null, "headerToValidate", "<mail xmlns='http://foo.com/bar'><body>Hello world!</body></mail>");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }

    @Test
    public void testNullHeaderNoFail() throws Exception {
        validEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:startNullHeaderNoFail", null, "headerToValidate", null);
        MockEndpoint.assertIsSatisfied(validEndpoint);
    }

    @Test
    public void testNullHeader() throws Exception {
        validEndpoint.setExpectedMessageCount(0);
        Exchange in = resolveMandatoryEndpoint("direct:startNoHeaderException").createExchange(InOut);
        in.getIn().setBody(null);
        in.getIn().setHeader("headerToValidate", null);
        Exchange out = template.send("direct:startNoHeaderException", in);
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
        Exception exception = out.getException();
        Assert.assertTrue("Should be failed", out.isFailed());
        Assert.assertTrue("Exception should be correct type", (exception instanceof NoXmlHeaderValidationException));
        Assert.assertTrue("Exception should mention missing header", exception.getMessage().contains("headerToValidate"));
    }

    @Test
    public void testInvalideBytesMessage() throws Exception {
        invalidEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBody("direct:start", "<mail xmlns='http://foo.com/bar'><body>Hello world!</body></mail>".getBytes());
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }

    @Test
    public void testInvalidBytesMessageInHeader() throws Exception {
        invalidEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:startHeaders", null, "headerToValidate", "<mail xmlns='http://foo.com/bar'><body>Hello world!</body></mail>".getBytes());
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }

    @Test
    public void testUseNotASharedSchema() throws Exception {
        validEndpoint.expectedMessageCount(1);
        template.sendBody("direct:useNotASharedSchema", "<mail xmlns='http://foo.com/bar'><subject>Hey</subject><body>Hello world!</body></mail>");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }

    @Test
    public void testConcurrentUseNotASharedSchema() throws Exception {
        validEndpoint.expectedMessageCount(10);
        // latch for the 10 exchanges we expect
        final CountDownLatch latch = new CountDownLatch(10);
        // setup a task executor to be able send the messages in parallel
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    template.requestBody("direct:useNotASharedSchema", "<mail xmlns='http://foo.com/bar'><subject>Hey</subject><body>Hello world!</body></mail>");
                    latch.countDown();
                }
            });
        }
        try {
            // wait for test completion, timeout after 30 sec to let other unit test run to not wait forever
            Assert.assertTrue(latch.await(30000L, TimeUnit.MILLISECONDS));
            Assert.assertEquals("Latch should be zero", 0, latch.getCount());
        } finally {
            executor.shutdown();
        }
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
    }
}

