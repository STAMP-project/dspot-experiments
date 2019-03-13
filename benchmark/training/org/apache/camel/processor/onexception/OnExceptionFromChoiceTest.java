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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test for CAMEL-1188
 */
public class OnExceptionFromChoiceTest extends ContextTestSupport {
    private MyServiceBean myServiceBean;

    @Test
    public void testNoErrorWhen() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(0);
        getMockEndpoint("mock:func").expectedMessageCount(0);
        getMockEndpoint("mock:tech").expectedMessageCount(0);
        getMockEndpoint("mock:otherwise").expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint("mock:when");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "<order><type>myType</type><user>James</user></order>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFunctionalError() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(0);
        getMockEndpoint("mock:tech").expectedMessageCount(0);
        getMockEndpoint("mock:when").expectedMessageCount(0);
        getMockEndpoint("mock:otherwise").expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint("mock:func");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "<order><type>myType</type><user>Func</user></order>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTechnicalError() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(0);
        getMockEndpoint("mock:func").expectedMessageCount(0);
        getMockEndpoint("mock:when").expectedMessageCount(0);
        getMockEndpoint("mock:otherwise").expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint("mock:tech");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "<order><type>myType</type><user>Tech</user></order>");
        assertMockEndpointsSatisfied();
    }
}

