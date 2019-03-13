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
package org.apache.camel.component.file.remote;


import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class FtpProducerAllowNullBodyFileAlreadyExistTest extends FtpServerTestSupport {
    @Test
    public void testFileExistAppendAllowNullBody() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:appendTypeAppendResult");
        mock.expectedMessageCount(1);
        mock.expectedFileExists(((FtpServerTestSupport.FTP_ROOT_DIR) + "/allow/hello.txt"), "Hello world");
        template.sendBody("direct:appendTypeAppend", null);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFileExistOverrideAllowNullBody() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:appendTypeOverrideResult");
        mock.expectedMessageCount(1);
        mock.expectedFileExists(((FtpServerTestSupport.FTP_ROOT_DIR) + "/allow/hello.txt"), "");
        template.sendBody("direct:appendTypeOverride", null);
        assertMockEndpointsSatisfied();
    }
}

