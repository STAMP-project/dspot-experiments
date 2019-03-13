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
 * Unit test for consuming multiple directories.
 */
public class FileConsumeMoveRelativeNameTest extends ContextTestSupport {
    private String fileUrl = "file://target/data/multidir/?initialDelay=0&delay=10&recursive=true&move=.done/${file:name}.old";

    @Test
    public void testMultiDir() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceivedInAnyOrder("Bye World", "Hello World", "Goodday World");
        mock.expectedFileExists("target/data/multidir/.done/bye.txt.old");
        mock.expectedFileExists("target/data/multidir/.done/sub/hello.txt.old");
        mock.expectedFileExists("target/data/multidir/.done/sub/sub2/goodday.txt.old");
        template.sendBodyAndHeader(fileUrl, "Bye World", FILE_NAME, "bye.txt");
        template.sendBodyAndHeader(fileUrl, "Hello World", FILE_NAME, "sub/hello.txt");
        template.sendBodyAndHeader(fileUrl, "Goodday World", FILE_NAME, "sub/sub2/goodday.txt");
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
    }
}

