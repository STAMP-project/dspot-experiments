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
import org.apache.cxf.BusFactory;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.junit.Test;


public class CxfConsumerResponseTest extends CamelTestSupport {
    private static final String ECHO_OPERATION = "echo";

    private static final String ECHO_BOOLEAN_OPERATION = "echoBoolean";

    private static final String PING_OPERATION = "ping";

    private static final String TEST_MESSAGE = "Hello World!";

    private static int pingCounter;

    protected final String simpleEndpointAddress = ((("http://localhost:" + (CXFTestSupport.getPort1())) + "/") + (getClass().getSimpleName())) + "/test";

    protected final String simpleEndpointURI = (("cxf://" + (simpleEndpointAddress)) + "?serviceClass=org.apache.camel.component.cxf.HelloService") + "&publishedEndpointUrl=http://www.simple.com/services/test";

    // END SNIPPET: example
    @Test
    public void testInvokingServiceFromCXFClient() throws Exception {
        ClientProxyFactoryBean proxyFactory = new ClientProxyFactoryBean();
        ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
        clientBean.setAddress(simpleEndpointAddress);
        clientBean.setServiceClass(HelloService.class);
        clientBean.setBus(BusFactory.getDefaultBus());
        HelloService client = ((HelloService) (proxyFactory.create()));
        assertNotNull(client);
        String result = client.echo(CxfConsumerResponseTest.TEST_MESSAGE);
        assertEquals("We should get the echo string result from router", result, ("echo " + (CxfConsumerResponseTest.TEST_MESSAGE)));
        Boolean bool = client.echoBoolean(Boolean.TRUE);
        assertNotNull("The result should not be null", bool);
        assertEquals("We should get the echo boolean result from router ", bool.toString(), "true");
        int beforeCallingPing = CxfConsumerResponseTest.pingCounter;
        client.ping();
        int afterCallingPing = CxfConsumerResponseTest.pingCounter;
        assertTrue("The ping operation doesn't be called", ((afterCallingPing - beforeCallingPing) == 1));
    }
}

