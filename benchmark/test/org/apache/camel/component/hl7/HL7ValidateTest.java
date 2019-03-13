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


import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.validation.ValidationException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class HL7ValidateTest extends CamelTestSupport {
    private HL7DataFormat hl7;

    @Test
    public void testUnmarshalFailed() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:unmarshal");
        mock.expectedMessageCount(0);
        String body = HL7ValidateTest.createHL7AsString();
        try {
            template.sendBody("direct:unmarshalFailed", body);
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(HL7Exception.class, e.getCause());
            assertIsInstanceOf(DataTypeException.class, e.getCause());
            assertTrue("Should be a validation error message", e.getCause().getMessage().startsWith("ca.uhn.hl7v2.validation.ValidationException: Validation failed:"));
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUnmarshalOk() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:unmarshal");
        mock.expectedMessageCount(1);
        String body = HL7ValidateTest.createHL7AsString();
        template.sendBody("direct:unmarshalOk", body);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUnmarshalOkCustom() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:unmarshal");
        mock.expectedMessageCount(1);
        String body = HL7ValidateTest.createHL7AsString();
        template.sendBody("direct:unmarshalOkCustom", body);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMarshalWithValidation() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:end");
        mock.expectedMessageCount(0);
        Message message = HL7ValidateTest.createADT01Message();
        try {
            template.sendBody("direct:start1", message);
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            assertIsInstanceOf(HL7Exception.class, e.getCause());
            assertIsInstanceOf(ValidationException.class, e.getCause().getCause());
            System.out.println(e.getCause().getCause().getMessage());
            assertTrue("Should be a validation error message", e.getCause().getCause().getMessage().startsWith("Validation failed:"));
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMarshalWithoutValidation() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:end");
        mock.expectedMessageCount(1);
        Message message = HL7ValidateTest.createADT01Message();
        template.sendBody("direct:start2", message);
        assertMockEndpointsSatisfied();
    }
}

