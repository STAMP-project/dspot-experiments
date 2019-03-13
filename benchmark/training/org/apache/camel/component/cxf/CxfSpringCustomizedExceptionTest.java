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
package org.apache.camel.component.cxf;


import CxfConstants.OPERATION_NAME;
import ExchangePattern.InOut;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.interceptor.Fault;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


public class CxfSpringCustomizedExceptionTest extends CamelTestSupport {
    private static final String EXCEPTION_MESSAGE = "This is an exception test message";

    private static final String DETAIL_TEXT = "This is a detail text node";

    private static final SoapFault SOAP_FAULT;

    private AbstractXmlApplicationContext applicationContext;

    static {
        // START SNIPPET: FaultDefine
        SOAP_FAULT = new SoapFault(CxfSpringCustomizedExceptionTest.EXCEPTION_MESSAGE, Fault.FAULT_CODE_CLIENT);
        Element detail = CxfSpringCustomizedExceptionTest.SOAP_FAULT.getOrCreateDetail();
        Document doc = detail.getOwnerDocument();
        Text tn = doc.createTextNode(CxfSpringCustomizedExceptionTest.DETAIL_TEXT);
        detail.appendChild(tn);
        // END SNIPPET: FaultDefine
    }

    @Test
    public void testInvokingServiceFromCamel() throws Exception {
        try {
            template.sendBodyAndHeader("direct:start", InOut, "hello world", OPERATION_NAME, "echo");
            fail("Should have thrown an exception");
        } catch (Exception ex) {
            Throwable result = ex.getCause();
            assertTrue("Exception is not instance of SoapFault", (result instanceof SoapFault));
            assertEquals("Expect to get right detail message", CxfSpringCustomizedExceptionTest.DETAIL_TEXT, getDetail().getTextContent());
            assertEquals("Expect to get right fault-code", "{http://schemas.xmlsoap.org/soap/envelope/}Client", getFaultCode().toString());
        }
    }

    public static class SOAPFaultFactory {
        public SoapFault getSoapFault() {
            return CxfSpringCustomizedExceptionTest.SOAP_FAULT;
        }
    }
}

