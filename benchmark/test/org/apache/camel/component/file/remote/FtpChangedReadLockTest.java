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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class FtpChangedReadLockTest extends FtpServerTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(FtpChangedReadLockTest.class);

    @Test
    public void testChangedReadLock() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedFileExists("target/changed/out/slowfile.dat");
        writeSlowFile();
        assertMockEndpointsSatisfied();
        String content = context.getTypeConverter().convertTo(String.class, new File("target/changed/out/slowfile.dat"));
        String[] lines = content.split(LS);
        assertEquals("There should be 20 lines in the file", 20, lines.length);
        for (int i = 0; i < 20; i++) {
            assertEquals(("Line " + i), lines[i]);
        }
    }
}

