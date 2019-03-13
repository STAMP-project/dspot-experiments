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
 * Unit test to test preMove with noop option.
 */
public class FromFtpPreMoveNoopTest extends FtpServerTestSupport {
    @Test
    public void testPreMoveNoop() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Hello World this file will be moved");
        mock.assertIsSatisfied();
        // and file should be kept there
        Thread.sleep(1000);
        File file = new File(((FtpServerTestSupport.FTP_ROOT_DIR) + "/movefile/work/hello.txt"));
        assertTrue("The file should exists", file.exists());
    }
}

