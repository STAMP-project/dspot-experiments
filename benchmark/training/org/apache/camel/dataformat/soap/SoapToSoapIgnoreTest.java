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
package org.apache.camel.dataformat.soap;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class SoapToSoapIgnoreTest extends CamelTestSupport {
    private static SoapJaxbDataFormat soapjaxbModel;

    private static SoapJaxbDataFormat soapjaxbModelIgnoreUnmarshalled;

    private static Map<String, String> namespacePrefixMap;

    @Test
    public void testSoapMarshal() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:end");
        endpoint.setExpectedMessageCount(1);
        template.sendBody("direct:start", createRequest());
        assertMockEndpointsSatisfied();
        Exchange result = endpoint.assertExchangeReceived(0);
        byte[] body = ((byte[]) (result.getIn().getBody()));
        InputStream stream = new ByteArrayInputStream(body);
        SOAPMessage request = MessageFactory.newInstance().createMessage(null, stream);
        assertTrue("Expected no headers", ((null == (request.getSOAPHeader())) || (!(request.getSOAPHeader().extractAllHeaderElements().hasNext()))));
    }
}

