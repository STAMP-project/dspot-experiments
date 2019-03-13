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


import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class CxfPayLoadSoapHeaderViaCamelHeaderTest extends CxfPayLoadSoapHeaderTestAbstract {
    @Test
    public void testCreateSoapHeaderViaCamelHeaderForSoapRequest() throws Exception {
        String body = "<OrderRequest xmlns=\"http://camel.apache.org/pizza/types\"><Toppings><Topping>topping_value</Topping></Toppings></OrderRequest>";
        MockEndpoint mock = getMockEndpoint("mock:end");
        mock.expectedMessageCount(1);
        sendBody("direct:start", body);
        assertMockEndpointsSatisfied();
        Document message = mock.getExchanges().get(0).getIn().getMandatoryBody(Document.class);
        Element root = message.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("MinutesUntilReady");
        assertEquals(1, nodeList.getLength());
        Element elMinutesUntilReady = ((Element) (nodeList.item(0)));
        /**
         * the phone number 108 which is given in the SOAP header is added to
         * 100 which results in 208, see class
         * org.apache.camel.component.cxf.PizzaImpl.
         */
        assertEquals("208", elMinutesUntilReady.getTextContent());
    }
}

