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


import Exchange.FILE_NAME;
import Exchange.OVERRULE_FILE_NAME;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 *
 */
public class RemoteFileProduceOverruleOnlyOnceTest extends FtpServerTestSupport {
    @Test
    public void testFileToFtp() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put(FILE_NAME, "/sub/hello.txt");
        headers.put(OVERRULE_FILE_NAME, "/sub/ruled.txt");
        template.sendBodyAndHeaders("direct:input", "Hello World", headers);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedHeaderReceived(FILE_NAME, "/sub/hello.txt");
        mock.expectedFileExists(((FtpServerTestSupport.FTP_ROOT_DIR) + "/out/sub/ruled.txt"), "Hello World");
        mock.expectedFileExists("target/out/sub/hello.txt", "Hello World");
        assertMockEndpointsSatisfied();
    }
}

