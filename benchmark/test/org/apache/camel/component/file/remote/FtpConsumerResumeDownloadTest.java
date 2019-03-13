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
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.IOConverter;
import org.junit.Test;


public class FtpConsumerResumeDownloadTest extends FtpServerTestSupport {
    @Test
    public void testResumeDownload() throws Exception {
        NotifyBuilder notify = whenDone(1).create();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello\nWorld\nI was here");
        // start route
        context.getRouteController().startRoute("myRoute");
        assertMockEndpointsSatisfied();
        assertTrue(notify.matchesMockWaitTime());
        // and the out file should exists
        File out = new File("target/out/hello.txt");
        assertTrue("file should exists", out.exists());
        assertEquals("Hello\nWorld\nI was here", IOConverter.toString(out, null));
        // now the lwd file should be deleted
        File local = new File("target/lwd/hello.txt");
        assertFalse("Local work file should have been deleted", local.exists());
        // and so the in progress
        File temp = new File("target/lwd/hello.txt.inprogress");
        assertFalse("Local work file should have been deleted", temp.exists());
    }
}

