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
import java.io.File;
import org.junit.Test;


public class FromFtpThirdPoolOkTest extends FtpServerTestSupport {
    private static int counter;

    private String body = "Hello World this file will be deleted";

    @Test
    public void testPollFileAndShouldBeDeletedAtThirdPoll() throws Exception {
        template.sendBodyAndHeader(getFtpUrl(), body, FILE_NAME, "hello.txt");
        getMockEndpoint("mock:result").expectedBodiesReceived(body);
        // 2 first attempt should fail
        getMockEndpoint("mock:error").expectedMessageCount(2);
        assertMockEndpointsSatisfied();
        // give time to delete file
        Thread.sleep(200);
        assertEquals(3, FromFtpThirdPoolOkTest.counter);
        // assert the file is deleted
        File file = new File(((FtpServerTestSupport.FTP_ROOT_DIR) + "/thirdpool/hello.txt"));
        assertFalse("The file should have been deleted", file.exists());
    }
}

