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
package org.apache.camel.component.cxf.spring;


import Exchange.FILE_NAME;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.cxf.endpoint.Server;
import org.junit.Test;


public class FileToCxfMessageDataFormatTest extends CamelSpringTestSupport {
    private static int port1 = CXFTestSupport.getPort1();

    private Server server;

    @Test
    public void testFileToCxfMessageDataFormat() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/filetocxf", createBody(), FILE_NAME, "payload.xml");
        assertMockEndpointsSatisfied();
        String out = mock.getReceivedExchanges().get(0).getIn().getBody(String.class);
        assertNotNull(out);
        log.info(("Reply payload as a String:\n" + out));
        assertTrue("Should invoke the echo operation", out.contains("echo Camel"));
    }
}

