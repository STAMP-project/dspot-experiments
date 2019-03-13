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
package org.apache.camel.component.hl7;


import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.ValidationContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class MessageValidatorTest extends CamelTestSupport {
    private ValidationContext defaultValidationContext;

    private ValidationContext customValidationContext;

    private HapiContext defaultContext;

    private HapiContext customContext;

    @Test
    public void testDefaultHapiContext() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test4");
        mock.expectedMessageCount(1);
        Message msg = MessageValidatorTest.createADT01Message();
        template.sendBody("direct:test4", msg);
        assertMockEndpointsSatisfied();
    }

    @Test(expected = CamelExecutionException.class)
    public void testCustomHapiContext() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test5");
        mock.expectedMessageCount(0);
        Message msg = MessageValidatorTest.createADT01Message();
        template.sendBody("direct:test5", msg);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDefaultValidationContext() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test1");
        mock.expectedMessageCount(1);
        Message msg = MessageValidatorTest.createADT01Message();
        template.sendBody("direct:test1", msg);
        assertMockEndpointsSatisfied();
    }

    @Test(expected = CamelExecutionException.class)
    public void testCustomValidationContext() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test2");
        mock.expectedMessageCount(0);
        Message msg = MessageValidatorTest.createADT01Message();
        template.sendBody("direct:test2", msg);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDynamicCustomValidationContext() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test3");
        mock.expectedMessageCount(1);
        Message msg = MessageValidatorTest.createADT01Message();
        template.sendBodyAndHeader("direct:test3", msg, "validator", defaultValidationContext);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDynamicDefaultHapiContext() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test6");
        mock.expectedMessageCount(1);
        Message msg = MessageValidatorTest.createADT01Message();
        msg.setParser(defaultContext.getPipeParser());
        template.sendBody("direct:test6", msg);
        assertMockEndpointsSatisfied();
    }

    @Test(expected = CamelExecutionException.class)
    public void testDynamicCustomHapiContext() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test6");
        mock.expectedMessageCount(0);
        Message msg = MessageValidatorTest.createADT01Message();
        msg.setParser(customContext.getPipeParser());
        template.sendBody("direct:test6", msg);
        assertMockEndpointsSatisfied();
    }
}

