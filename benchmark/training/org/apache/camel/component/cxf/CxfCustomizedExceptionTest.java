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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.IOHelper;
import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.Fault;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


public class CxfCustomizedExceptionTest extends CamelTestSupport {
    protected static final String SERVICE_CLASS = "serviceClass=org.apache.camel.component.cxf.HelloService";

    private static final String EXCEPTION_MESSAGE = "This is an exception test message";

    private static final String DETAIL_TEXT = "This is a detail text node";

    private static final SoapFault SOAP_FAULT;

    static {
        // START SNIPPET: FaultDefine
        SOAP_FAULT = new SoapFault(CxfCustomizedExceptionTest.EXCEPTION_MESSAGE, Fault.FAULT_CODE_CLIENT);
        Element detail = CxfCustomizedExceptionTest.SOAP_FAULT.getOrCreateDetail();
        Document doc = detail.getOwnerDocument();
        Text tn = doc.createTextNode(CxfCustomizedExceptionTest.DETAIL_TEXT);
        detail.appendChild(tn);
        // END SNIPPET: FaultDefine
    }

    protected String routerAddress = ((("http://localhost:" + (CXFTestSupport.getPort1())) + "/") + (getClass().getSimpleName())) + "/router";

    protected String routerEndpointURI = (("cxf://" + (routerAddress)) + "?") + (CxfCustomizedExceptionTest.SERVICE_CLASS);

    protected String serviceURI = (("cxf://" + (routerAddress)) + "?") + (CxfCustomizedExceptionTest.SERVICE_CLASS);

    private Bus bus;

    @Test
    public void testInvokingServiceFromCamel() throws Exception {
        Object result = template.sendBodyAndHeader("direct:start", InOut, "hello world", OPERATION_NAME, "echo");
        assertTrue("Exception is not instance of SoapFault", (result instanceof SoapFault));
        assertEquals("Expect to get right detail message", CxfCustomizedExceptionTest.DETAIL_TEXT, getDetail().getTextContent());
        assertEquals("Expect to get right fault-code", "{http://schemas.xmlsoap.org/soap/envelope/}Client", getFaultCode().toString());
    }

    @Test
    public void testInvokingServiceFromCXFClient() throws Exception {
        ClientProxyFactoryBean proxyFactory = new ClientProxyFactoryBean();
        ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
        clientBean.setAddress(routerAddress);
        clientBean.setServiceClass(HelloService.class);
        clientBean.setBus(bus);
        HelloService client = ((HelloService) (proxyFactory.create()));
        try {
            client.echo("hello world");
            fail("Expect to get an exception here");
        } catch (Exception e) {
            assertEquals("Expect to get right exception message", CxfCustomizedExceptionTest.EXCEPTION_MESSAGE, e.getMessage());
            assertTrue("Exception is not instance of SoapFault", (e instanceof SoapFault));
            assertEquals("Expect to get right detail message", CxfCustomizedExceptionTest.DETAIL_TEXT, getDetail().getTextContent());
            // In CXF 2.1.2 , the fault code is per spec , the below fault-code is for SOAP 1.1
            assertEquals("Expect to get right fault-code", "{http://schemas.xmlsoap.org/soap/envelope/}Client", getFaultCode().toString());
        }
    }

    @Test
    public void testInvokingServiceFromHTTPURL() throws Exception {
        URL url = new URL(routerAddress);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/xml");
        // Send POST data
        OutputStream out = urlConnection.getOutputStream();
        // copy the message out
        InputStream is = this.getClass().getResourceAsStream("SimpleSoapRequest.xml");
        IOHelper.copy(is, out);
        out.flush();
        is.close();
        // check the response code
        try {
            urlConnection.getInputStream();
            fail("We except an IOException here");
        } catch (IOException exception) {
            assertTrue(exception.getMessage().contains("500"));
        }
    }
}

