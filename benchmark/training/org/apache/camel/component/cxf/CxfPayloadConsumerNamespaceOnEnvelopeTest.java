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


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class CxfPayloadConsumerNamespaceOnEnvelopeTest extends CamelTestSupport {
    /* The request message is generated directly. The issue here is that the xsi
    and xs namespaces are defined on the SOAP envelope but are used within
    the payload. This can cause issues with some type conversions in PAYLOAD
    mode, as the Camel-CXF endpoint will return some kind of window within
    the StAX parsing (and the namespace definitions are outside).

    If some CXF proxy is used to send the message the namespaces will be
    defined within the payload (and everything works fine).
     */
    protected static final String RESPONSE_PAYLOAD = "<ns2:getTokenResponse xmlns:ns2=\"http://camel.apache.org/cxf/namespace\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" + "<return xsi:type=\"xs:string\">Return Value</return></ns2:getTokenResponse>";

    protected static final String REQUEST_MESSAGE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" + "<soap:Body><ns2:getToken xmlns:ns2=\"http://camel.apache.org/cxf/namespace\"><arg0 xsi:type=\"xs:string\">Send</arg0></ns2:getToken></soap:Body></soap:Envelope>";

    private AbstractXmlApplicationContext applicationContext;

    // Don't remove this, it initializes the CXFTestSupport class
    static {
        CXFTestSupport.getPort1();
        // Works without streaming...
        // System.setProperty("org.apache.camel.component.cxf.streaming", "false");
    }

    @Test
    public void testInvokeRouter() {
        Object returnValue = template.requestBody("direct:router", CxfPayloadConsumerNamespaceOnEnvelopeTest.REQUEST_MESSAGE);
        assertNotNull(returnValue);
        assertTrue((returnValue instanceof String));
        assertTrue(((String) (returnValue)).contains("Return Value"));
        assertTrue(((String) (returnValue)).contains("http://www.w3.org/2001/XMLSchema-instance"));
    }
}

