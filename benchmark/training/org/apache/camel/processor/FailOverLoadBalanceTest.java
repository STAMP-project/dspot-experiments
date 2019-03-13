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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class FailOverLoadBalanceTest extends ContextTestSupport {
    protected MockEndpoint x;

    protected MockEndpoint y;

    protected MockEndpoint z;

    public static class MyException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class MyAnotherException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class MyExceptionProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            throw new FailOverLoadBalanceTest.MyException();
        }
    }

    public static class MyAnotherExceptionProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            throw new FailOverLoadBalanceTest.MyAnotherException();
        }
    }

    @Test
    public void testThrowable() throws Exception {
        String body = "<one/>";
        MockEndpoint.expectsMessageCount(0, x, y);
        z.expectedBodiesReceived(body);
        sendMessage("direct:exception", "bar", body);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMyException() throws Exception {
        String body = "<two/>";
        MockEndpoint.expectsMessageCount(0, x, y, z);
        try {
            sendMessage("direct:customerException", "bar", body);
            Assert.fail("There should get the MyAnotherException");
        } catch (RuntimeCamelException ex) {
            // expect the exception here
            Assert.assertTrue("The cause should be MyAnotherException", ((ex.getCause()) instanceof FailOverLoadBalanceTest.MyAnotherException));
        }
        assertMockEndpointsSatisfied();
    }
}

