/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.soap;


import HttpMethod.GET;
import Util.UTF_8;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.jaxb.JAXBContextFactory;
import java.io.IOException;
import java.util.Collections;
import javax.xml.soap.SOAPConstants;
import javax.xml.ws.soap.SOAPFaultException;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SOAPFaultDecoderTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void soapDecoderThrowsSOAPFaultException() throws IOException {
        thrown.expect(SOAPFaultException.class);
        thrown.expectMessage("Processing error");
        Response response = Response.builder().status(200).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(SOAPFaultDecoderTest.getResourceBytes("/samples/SOAP_1_2_FAULT.xml")).build();
        new SOAPDecoder.Builder().withSOAPProtocol(SOAPConstants.SOAP_1_2_PROTOCOL).withJAXBContextFactory(new JAXBContextFactory.Builder().build()).build().decode(response, Object.class);
    }

    @Test
    public void errorDecoderReturnsSOAPFaultException() throws IOException {
        Response response = Response.builder().status(400).reason("BAD REQUEST").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(SOAPFaultDecoderTest.getResourceBytes("/samples/SOAP_1_1_FAULT.xml")).build();
        Exception error = new SOAPErrorDecoder().decode("Service#foo()", response);
        Assertions.assertThat(error).isInstanceOf(SOAPFaultException.class).hasMessage("Message was not SOAP 1.1 compliant");
    }

    @Test
    public void errorDecoderReturnsFeignExceptionOn503Status() throws IOException {
        Response response = Response.builder().status(503).reason("Service Unavailable").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body("Service Unavailable", Util.UTF_8).build();
        Exception error = new SOAPErrorDecoder().decode("Service#foo()", response);
        Assertions.assertThat(error).isInstanceOf(FeignException.class).hasMessage("status 503 reading Service#foo()");
    }

    @Test
    public void errorDecoderReturnsFeignExceptionOnEmptyFault() throws IOException {
        Response response = Response.builder().status(500).reason("Internal Server Error").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(("<?xml version = \'1.0\' encoding = \'UTF-8\'?>\n" + (((((("<SOAP-ENV:Envelope\n" + "   xmlns:SOAP-ENV = \"http://schemas.xmlsoap.org/soap/envelope/\"\n") + "   xmlns:xsi = \"http://www.w3.org/1999/XMLSchema-instance\"\n") + "   xmlns:xsd = \"http://www.w3.org/1999/XMLSchema\">\n") + "   <SOAP-ENV:Body>\n") + "   </SOAP-ENV:Body>\n") + "</SOAP-ENV:Envelope>")), Util.UTF_8).build();
        Exception error = new SOAPErrorDecoder().decode("Service#foo()", response);
        Assertions.assertThat(error).isInstanceOf(FeignException.class).hasMessage("status 500 reading Service#foo()");
    }
}

