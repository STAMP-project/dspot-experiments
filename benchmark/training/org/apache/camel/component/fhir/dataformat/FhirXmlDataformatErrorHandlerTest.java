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
package org.apache.camel.component.fhir.dataformat;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.Test;


public class FhirXmlDataformatErrorHandlerTest extends CamelTestSupport {
    private static final String INPUT = "<Patient><active value=\"true\"/><active value=\"false\"/></Patient>";

    private MockEndpoint mockEndpoint;

    private final FhirContext fhirContext = FhirContext.forDstu3();

    @Test(expected = DataFormatException.class)
    public void unmarshalParserErrorHandler() throws Throwable {
        try {
            template.sendBody("direct:unmarshalErrorHandlerStrict", FhirXmlDataformatErrorHandlerTest.INPUT);
        } catch (CamelExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void unmarshalLenientErrorHandler() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:unmarshalErrorHandlerLenient", FhirXmlDataformatErrorHandlerTest.INPUT);
        mockEndpoint.assertIsSatisfied();
        Exchange exchange = mockEndpoint.getExchanges().get(0);
        Patient patient = ((Patient) (exchange.getIn().getBody()));
        assertEquals(true, patient.getActive());
    }
}

