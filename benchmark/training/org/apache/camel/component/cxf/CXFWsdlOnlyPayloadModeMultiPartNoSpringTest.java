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


import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.wsdl_first.PersonMultiPartPortType;
import org.apache.camel.wsdl_first.PersonMultiPartService;
import org.junit.Test;


/**
 * Unit test that verifies multi part SOAP message functionality
 */
public class CXFWsdlOnlyPayloadModeMultiPartNoSpringTest extends CamelTestSupport {
    protected static int port1 = CXFTestSupport.getPort1();

    protected static int port2 = CXFTestSupport.getPort2();

    protected static final String SERVICE_NAME_PROP = "serviceName=";

    protected static final String PORT_NAME_PROP = "portName={http://camel.apache.org/wsdl-first}PersonMultiPartPort";

    protected static final String WSDL_URL_PROP = "wsdlURL=classpath:person.wsdl";

    protected static final String SERVICE_ADDRESS = ("http://localhost:" + (CXFWsdlOnlyPayloadModeMultiPartNoSpringTest.port1)) + "/CXFWsdlOnlyPayloadModeMultiPartNoSpringTest/PersonMultiPart";

    protected Endpoint endpoint;

    @Test
    public void testMultiPartMessage() {
        URL wsdlURL = getClass().getClassLoader().getResource("person.wsdl");
        PersonMultiPartService ss = new PersonMultiPartService(wsdlURL, QName.valueOf(getServiceName()));
        PersonMultiPartPortType client = ss.getPersonMultiPartPort();
        ((BindingProvider) (client)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CXFWsdlOnlyPayloadModeMultiPartNoSpringTest.port2)) + "/CXFWsdlOnlyPayloadModeMultiPartNoSpringTest/PersonMultiPart"));
        Holder<Integer> ssn = new Holder<>();
        ssn.value = 0;
        Holder<String> name = new Holder<>();
        name.value = "Unknown name";
        client.getPersonMultiPartOperation("foo", 0, name, ssn);
        assertEquals("New Person Name", name.value);
        assertTrue((123456789 == (ssn.value)));
    }
}

