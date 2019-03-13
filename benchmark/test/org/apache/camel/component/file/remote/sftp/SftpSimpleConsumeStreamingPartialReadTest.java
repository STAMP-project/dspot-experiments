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
package org.apache.camel.component.file.remote.sftp;


import Exchange.FILE_NAME;
import java.io.File;
import java.io.InputStream;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Tests that a file move can occur on the server even if the remote stream was only partially read.
 */
public class SftpSimpleConsumeStreamingPartialReadTest extends SftpServerTestSupport {
    @Test
    public void testSftpSimpleConsume() throws Exception {
        if (!(canTest())) {
            return;
        }
        String expected = "Hello World";
        // create file using regular file
        template.sendBodyAndHeader(("file://" + (SftpServerTestSupport.FTP_ROOT_DIR)), expected, FILE_NAME, "hello.txt");
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(FILE_NAME, "hello.txt");
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        GenericFile<?> remoteFile1 = ((GenericFile<?>) (mock.getExchanges().get(0).getIn().getBody()));
        assertTrue(((remoteFile1.getBody()) instanceof InputStream));
        // Wait a little bit for the move to finish.
        Thread.sleep(2000);
        File resultFile = new File((((SftpServerTestSupport.FTP_ROOT_DIR) + (File.separator)) + "failed"), "hello.txt");
        assertTrue(resultFile.exists());
        assertFalse(resultFile.isDirectory());
    }
}

