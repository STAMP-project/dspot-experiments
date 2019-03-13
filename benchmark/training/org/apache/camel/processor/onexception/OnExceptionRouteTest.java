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
package org.apache.camel.processor.onexception;


import java.io.IOException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test inspired by user forum.
 */
public class OnExceptionRouteTest extends ContextTestSupport {
    private MyOwnHandlerBean myOwnHandlerBean;

    private MyServiceBean myServiceBean;

    @Test
    public void testNoError() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "<order><type>myType</type><user>James</user></order>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFunctionalError() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        template.sendBody("direct:start", "<order><type>myType</type><user>Func</user></order>");
        assertMockEndpointsSatisfied();
        Assert.assertEquals("<order><type>myType</type><user>Func</user></order>", myOwnHandlerBean.getPayload());
    }

    @Test
    public void testTechnicalError() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(1);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        template.sendBody("direct:start", "<order><type>myType</type><user>Tech</user></order>");
        assertMockEndpointsSatisfied();
        // should not handle it
        Assert.assertNull(myOwnHandlerBean.getPayload());
    }

    @Test
    public void testErrorWhileHandlingException() throws Exception {
        // DLC does not handle the exception as we failed during processing in onException
        MockEndpoint error = getMockEndpoint("mock:error");
        error.expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "<order><type>myType</type><user>FuncError</user></order>");
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            // the myOwnHandlerBean throw exception while handling an exception
            IOException cause = TestSupport.assertIsInstanceOf(IOException.class, e.getCause());
            Assert.assertEquals("Damn something did not work", cause.getMessage());
        }
        assertMockEndpointsSatisfied();
        // should not handle it
        Assert.assertNull(myOwnHandlerBean.getPayload());
    }
}

