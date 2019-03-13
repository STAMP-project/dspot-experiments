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


import java.io.File;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class SftpConsumerDisconnectTest extends SftpServerTestSupport {
    private static final String SAMPLE_FILE_NAME_1 = String.format("sample-1-%s.txt", SftpConsumerDisconnectTest.class.getSimpleName());

    private static final String SAMPLE_FILE_NAME_2 = String.format("sample-2-%s.txt", SftpConsumerDisconnectTest.class.getSimpleName());

    private static final String SAMPLE_FILE_CHARSET = "iso-8859-1";

    private static final String SAMPLE_FILE_PAYLOAD = "abc";

    @Test
    public void testConsumeDelete() throws Exception {
        if (!(canTest())) {
            return;
        }
        // prepare sample file to be consumed by SFTP consumer
        createSampleFile(SftpConsumerDisconnectTest.SAMPLE_FILE_NAME_1);
        // Prepare expectations
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(SftpConsumerDisconnectTest.SAMPLE_FILE_PAYLOAD);
        context.getRouteController().startRoute("foo");
        // Check that expectations are satisfied
        assertMockEndpointsSatisfied();
        Thread.sleep(250);
        // File is deleted
        File deletedFile = new File((((SftpServerTestSupport.FTP_ROOT_DIR) + "/") + (SftpConsumerDisconnectTest.SAMPLE_FILE_NAME_1)));
        assertFalse(("File should have been deleted: " + deletedFile), deletedFile.exists());
    }

    @Test
    public void testConsumeMove() throws Exception {
        if (!(canTest())) {
            return;
        }
        // moved file after its processed
        String movedFile = ((SftpServerTestSupport.FTP_ROOT_DIR) + "/.camel/") + (SftpConsumerDisconnectTest.SAMPLE_FILE_NAME_2);
        // prepare sample file to be consumed by SFTP consumer
        createSampleFile(SftpConsumerDisconnectTest.SAMPLE_FILE_NAME_2);
        // Prepare expectations
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(SftpConsumerDisconnectTest.SAMPLE_FILE_PAYLOAD);
        // use mock to assert that the file will be moved there eventually
        mock.expectedFileExists(movedFile);
        context.getRouteController().startRoute("bar");
        // Check that expectations are satisfied
        assertMockEndpointsSatisfied();
    }
}

