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


import java.net.ConnectException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ErrorHandlerOnExceptionRedeliveryAndHandledTest extends ContextTestSupport {
    private static String counter = "";

    @Test
    public void testRedeliveryCounterIsResetWhenHandled() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:other").expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "Hello World");
            // we tried to handle that exception but then another exception occurred
            // so this exchange failed with an exception
            Assert.fail("Should throw an exception");
        } catch (CamelExecutionException e) {
            ConnectException cause = TestSupport.assertIsInstanceOf(ConnectException.class, e.getCause());
            Assert.assertEquals("Cannot connect to bar server", cause.getMessage());
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals("123", ErrorHandlerOnExceptionRedeliveryAndHandledTest.counter);
    }
}

