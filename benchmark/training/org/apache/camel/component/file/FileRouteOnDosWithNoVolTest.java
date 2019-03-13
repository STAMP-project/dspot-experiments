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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Verify the standard file url paths on windows that are interpreted as the window's
 * url paths without the volume name will work on windows system.
 */
public class FileRouteOnDosWithNoVolTest extends ContextTestSupport {
    private String path;

    @Test
    public void testRouteFileToFile() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedFileExists(((path) + "/route/out/hello.txt"));
        template.sendBodyAndHeader((("file://" + (path)) + "/route/poller"), "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRouteFromFileOnly() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader((("file://" + (path)) + "/from/poller"), "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRouteToFileOnly() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedFileExists(((path) + "/to/out/hello.txt"));
        template.sendBodyAndHeader("direct:report", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }
}

