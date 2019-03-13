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


public class CxfProducerSynchronousTest extends CamelTestSupport {
    private static final String SIMPLE_SERVER_ADDRESS = ("http://localhost:" + (CXFTestSupport.getPort1())) + "/CxfProducerSynchronousTest/test";

    private static final String REQUEST_MESSAGE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + (("<soap:Body><ns1:echo xmlns:ns1=\"http://cxf.component.camel.apache.org/\">" + "<arg0 xmlns=\"http://cxf.component.camel.apache.org/\">Hello World!</arg0>") + "</ns1:echo></soap:Body></soap:Envelope>");

    private static final String TEST_MESSAGE = "Hello World!";

    private static String beforeThreadName;

    private static String afterThreadName;

    private String url = ("cxf://" + (CxfProducerSynchronousTest.SIMPLE_SERVER_ADDRESS)) + "?serviceClass=org.apache.camel.component.cxf.HelloService&dataFormat=RAW&synchronous=true";

    @Test
    public void testSynchronous() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        String response = template.requestBody("direct:start", CxfProducerSynchronousTest.REQUEST_MESSAGE, String.class);
        assertTrue("It should has the echo message", ((response.indexOf(("echo " + (CxfProducerSynchronousTest.TEST_MESSAGE)))) > 0));
        assertTrue("It should has the echoResponse tag", ((response.indexOf("echoResponse")) > 0));
        assertMockEndpointsSatisfied();
        assertTrue("Should use same threads", CxfProducerSynchronousTest.beforeThreadName.equalsIgnoreCase(CxfProducerSynchronousTest.afterThreadName));
    }
}

