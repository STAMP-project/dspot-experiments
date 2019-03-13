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


/**
 * Unit test to verify that we can pool an ASCII file from the FTP Server and store it on a local file path
 */
public class FromFtpToAsciiFileTest extends FtpServerTestSupport {
    @Test
    public void testFtpRoute() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMinimumMessageCount(1);
        resultEndpoint.expectedBodiesReceived("Hello World from FTPServer");
        resultEndpoint.assertIsSatisfied();
        // assert the file
        File file = new File("target/ftptest/deleteme.txt");
        assertTrue("The ASCII file should exists", file.exists());
        assertTrue("File size wrong", ((file.length()) > 10));
    }
}

