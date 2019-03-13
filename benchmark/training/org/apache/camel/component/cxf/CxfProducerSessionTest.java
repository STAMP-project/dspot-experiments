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


import java.util.Collections;
import java.util.Map;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CxfProducerSessionTest extends CamelTestSupport {
    private static final int PORT = CXFTestSupport.getPort1();

    private static final String SIMPLE_SERVER_ADDRESS = ("http://127.0.0.1:" + (CxfProducerSessionTest.PORT)) + "/CxfProducerSessionTest/test";

    private static final String REQUEST_MESSAGE_EXPRESSION = "<ns1:echo xmlns:ns1=\"http://cxf.component.camel.apache.org/\"><arg0>${in.body}</arg0></ns1:echo>";

    private static final Map<String, String> NAMESPACES = Collections.singletonMap("ns1", "http://cxf.component.camel.apache.org/");

    private static final String PARAMETER_XPATH = "/ns1:echoResponse/return/text()";

    private String url = ("cxf://" + (CxfProducerSessionTest.SIMPLE_SERVER_ADDRESS)) + "?serviceClass=org.apache.camel.component.cxf.EchoService&dataFormat=PAYLOAD&synchronous=true";

    @Test
    public void testNoSession() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(2);
        String response = template.requestBody("direct:start", "World", String.class);
        assertEquals("New New World", response);
        response = template.requestBody("direct:start", "World", String.class);
        assertEquals("New New World", response);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExchangeSession() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(2);
        String response = template.requestBody("direct:exchange", "World", String.class);
        assertEquals("Old New World", response);
        response = template.requestBody("direct:exchange", "World", String.class);
        assertEquals("Old New World", response);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testInstanceSession() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(2);
        String response = template.requestBody("direct:instance", "World", String.class);
        assertEquals("Old New World", response);
        response = template.requestBody("direct:instance", "World", String.class);
        assertEquals("Old Old World", response);
        assertMockEndpointsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSessionWithInvalidPayload() throws Throwable {
        try {
            template.requestBody("direct:invalid", "World", String.class);
        } catch (CamelExecutionException e) {
            if ((e.getCause()) != null) {
                throw e.getCause();
            }
            throw e;
        }
    }
}

