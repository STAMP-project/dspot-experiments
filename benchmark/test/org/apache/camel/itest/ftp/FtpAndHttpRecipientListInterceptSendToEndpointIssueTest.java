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
package org.apache.camel.itest.ftp;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.ftpserver.FtpServer;
import org.junit.Test;


/**
 *
 */
public class FtpAndHttpRecipientListInterceptSendToEndpointIssueTest extends CamelTestSupport {
    protected static int ftpPort;

    protected static int httpPort;

    protected FtpServer ftpServer;

    @Test
    public void testFtpAndHttpIssue() throws Exception {
        String ftp = ("ftp:localhost:" + (FtpAndHttpRecipientListInterceptSendToEndpointIssueTest.ftpPort)) + "/myapp?password=admin&username=admin";
        String http = ("http://localhost:" + (FtpAndHttpRecipientListInterceptSendToEndpointIssueTest.httpPort)) + "/myapp";
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:intercept").expectedMessageCount(3);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", ((("seda:foo," + ftp) + ",") + http));
        assertMockEndpointsSatisfied();
    }
}

