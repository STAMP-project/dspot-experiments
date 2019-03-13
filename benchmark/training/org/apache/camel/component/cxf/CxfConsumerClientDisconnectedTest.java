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


import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Test;


/**
 * UnitOfWork should complete even if client disconnected during the processing.
 */
public class CxfConsumerClientDisconnectedTest extends CamelTestSupport {
    private static final int PORT = CXFTestSupport.getPort1();

    private static final String CONTEXT = "/CxfConsumerClientDisconnectedTest";

    private static final String CXT = (CxfConsumerClientDisconnectedTest.PORT) + (CxfConsumerClientDisconnectedTest.CONTEXT);

    private String cxfRsEndpointUri = ((("cxf://http://localhost:" + (CxfConsumerClientDisconnectedTest.CXT)) + "/rest?synchronous=") + (isSynchronous())) + "&serviceClass=org.apache.camel.component.cxf.ServiceProvider&dataFormat=PAYLOAD";

    @Test
    public void testClientDisconnect() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        MockEndpoint onComplete = getMockEndpoint("mock:onComplete");
        onComplete.expectedMessageCount(1);
        TelnetClient telnetClient = new TelnetClient();
        telnetClient.connect("localhost", CxfConsumerClientDisconnectedTest.PORT);
        telnetClient.setTcpNoDelay(true);
        telnetClient.setReceiveBufferSize(1);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(telnetClient.getOutputStream()));
        writer.write((("GET " + (CxfConsumerClientDisconnectedTest.CONTEXT)) + "/rest/customerservice/customers HTTP/1.1\nhost: localhost\n\n"));
        writer.flush();
        telnetClient.disconnect();
        mock.assertIsSatisfied();
        onComplete.assertIsSatisfied();
    }
}

