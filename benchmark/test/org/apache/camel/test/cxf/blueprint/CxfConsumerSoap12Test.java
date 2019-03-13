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
package org.apache.camel.test.cxf.blueprint;


import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.cxf.binding.BindingConfiguration;
import org.apache.cxf.binding.soap.SoapBindingConfiguration;
import org.apache.hello_world_soap_http.Greeter;
import org.junit.Test;


public class CxfConsumerSoap12Test extends CamelBlueprintTestSupport {
    private static final QName SERVICE_NAME = new QName("http://apache.org/hello_world_soap_http", "SOAPService");

    private static final QName PORT_NAME = new QName("http://apache.org/hello_world_soap_http", "SoapPort");

    @Test
    public void testCxfEndpointBeanDefinitionParser() {
        CxfEndpoint routerEndpoint = context.getEndpoint("routerEndpoint", CxfEndpoint.class);
        assertEquals("Got the wrong endpoint address", routerEndpoint.getAddress(), (("http://localhost:" + (CXFTestSupport.getPort1())) + "/CxfConsumerSoap12Test/router"));
        assertEquals("Got the wrong endpont service class", "org.apache.hello_world_soap_http.Greeter", routerEndpoint.getServiceClass().getName());
        BindingConfiguration binding = routerEndpoint.getBindingConfig();
        assertTrue("Got no soap binding", (binding instanceof SoapBindingConfiguration));
        assertEquals("Got the wrong soap version", "http://schemas.xmlsoap.org/wsdl/soap12/", getVersion().getBindingId());
        assertTrue("Mtom not enabled", isMtomEnabled());
    }

    @Test
    public void testInvokeGreeter() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        Service service = Service.create(CxfConsumerSoap12Test.SERVICE_NAME);
        service.addPort(CxfConsumerSoap12Test.PORT_NAME, "http://www.w3.org/2003/05/soap/bindings/HTTP/", (("http://localhost:" + (CXFTestSupport.getPort1())) + "/CxfConsumerSoap12Test/router"));
        Greeter greeter = service.getPort(CxfConsumerSoap12Test.PORT_NAME, Greeter.class);
        greeter.greetMeOneWay("test");
        assertMockEndpointsSatisfied();
    }
}

