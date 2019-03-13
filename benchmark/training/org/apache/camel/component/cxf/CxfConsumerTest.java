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


import Exchange.CONTENT_TYPE;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.BusFactory;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.junit.Test;


public class CxfConsumerTest extends CamelTestSupport {
    protected static final String SIMPLE_ENDPOINT_ADDRESS = ("http://localhost:" + (CXFTestSupport.getPort1())) + "/CxfConsumerTest/test";

    protected static final String SIMPLE_ENDPOINT_URI = (("cxf://" + (CxfConsumerTest.SIMPLE_ENDPOINT_ADDRESS)) + "?serviceClass=org.apache.camel.component.cxf.HelloService") + "&publishedEndpointUrl=http://www.simple.com/services/test";

    private static final String ECHO_REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + ("<soap:Body><ns1:echo xmlns:ns1=\"http://cxf.component.camel.apache.org/\">" + "<arg0 xmlns=\"http://cxf.component.camel.apache.org/\">Hello World!</arg0></ns1:echo></soap:Body></soap:Envelope>");

    private static final String ECHO_OPERATION = "echo";

    private static final String ECHO_BOOLEAN_OPERATION = "echoBoolean";

    private static final String TEST_MESSAGE = "Hello World!";

    // END SNIPPET: example
    @Test
    public void testInvokingServiceFromCXFClient() throws Exception {
        ClientProxyFactoryBean proxyFactory = new ClientProxyFactoryBean();
        ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
        clientBean.setAddress(CxfConsumerTest.SIMPLE_ENDPOINT_ADDRESS);
        clientBean.setServiceClass(HelloService.class);
        clientBean.setBus(BusFactory.newInstance().createBus());
        HelloService client = ((HelloService) (proxyFactory.create()));
        String result = client.echo(CxfConsumerTest.TEST_MESSAGE);
        assertEquals("We should get the echo string result from router", result, ("echo " + (CxfConsumerTest.TEST_MESSAGE)));
        Boolean bool = client.echoBoolean(Boolean.TRUE);
        assertNotNull("The result should not be null", bool);
        assertEquals("We should get the echo boolean result from router ", bool.toString(), "true");
    }

    @Test
    public void testXmlDeclaration() throws Exception {
        String response = template.requestBodyAndHeader(CxfConsumerTest.SIMPLE_ENDPOINT_ADDRESS, CxfConsumerTest.ECHO_REQUEST, CONTENT_TYPE, "text/xml; charset=UTF-8", String.class);
        assertTrue("Can't find the xml declaration.", response.startsWith("<?xml version='1.0' encoding="));
    }

    @Test
    public void testPublishEndpointUrl() throws Exception {
        String response = template.requestBody(((CxfConsumerTest.SIMPLE_ENDPOINT_ADDRESS) + "?wsdl"), null, String.class);
        assertTrue("Can't find the right service location.", ((response.indexOf("http://www.simple.com/services/test")) > 0));
    }
}

