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
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Test;


public class FhirJsonDataFormatTest extends CamelTestSupport {
    private static final String PATIENT = "{\"resourceType\":\"Patient\"," + ("\"name\":[{\"family\":\"Holmes\",\"given\":[\"Sherlock\"]}]," + "\"address\":[{\"line\":[\"221b Baker St, Marylebone, London NW1 6XE, UK\"]}]}");

    private MockEndpoint mockEndpoint;

    @Test
    public void unmarshal() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:unmarshal", FhirJsonDataFormatTest.PATIENT);
        mockEndpoint.assertIsSatisfied();
        Exchange exchange = mockEndpoint.getExchanges().get(0);
        Patient patient = ((Patient) (exchange.getIn().getBody()));
        assertTrue("Patients should be equal!", patient.equalsDeep(getPatient()));
    }

    @Test
    public void marshal() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        Patient patient = getPatient();
        template.sendBody("direct:marshal", patient);
        mockEndpoint.assertIsSatisfied();
        Exchange exchange = mockEndpoint.getExchanges().get(0);
        InputStream inputStream = exchange.getIn().getBody(InputStream.class);
        IBaseResource iBaseResource = FhirContext.forDstu3().newJsonParser().parseResource(new InputStreamReader(inputStream));
        assertTrue("Patients should be equal!", patient.equalsDeep(((Base) (iBaseResource))));
    }
}

