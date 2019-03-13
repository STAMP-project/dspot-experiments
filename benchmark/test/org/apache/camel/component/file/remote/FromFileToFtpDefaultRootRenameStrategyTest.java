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


import java.io.File;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class FromFileToFtpDefaultRootRenameStrategyTest extends FtpServerTestSupport {
    @Test
    public void testFromFileToFtp() throws Exception {
        File expectedOnFtpServer = new File(((FtpServerTestSupport.FTP_ROOT_DIR) + "/logo.jpeg"));
        // the poller won't start for 1.5 seconds, so we check to make sure the file
        // is there first check 1 - is the file there (default root location)
        assertTrue(expectedOnFtpServer.exists());
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();
        // give our mock a chance to delete the file
        Thread.sleep(250);
        // assert the file is NOT there now
        assertTrue((!(expectedOnFtpServer.exists())));
    }
}

