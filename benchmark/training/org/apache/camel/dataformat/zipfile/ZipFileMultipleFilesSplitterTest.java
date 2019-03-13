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
package org.apache.camel.dataformat.zipfile;


import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ZipFileMultipleFilesSplitterTest extends ZipSplitterRouteTest {
    static final String PROCESSED_FILES_HEADER_NAME = "processedFiles";

    @Test
    public void testSplitter() throws InterruptedException {
        MockEndpoint processZipEntry = getMockEndpoint("mock:processZipEntry");
        MockEndpoint splitResult = getMockEndpoint("mock:splitResult");
        processZipEntry.expectedBodiesReceivedInAnyOrder("chau", "hi", "hola", "another_chiau", "another_hi");
        splitResult.expectedBodiesReceivedInAnyOrder("chiau.txt", "hi.txt", "hola.txt", "directoryOne/another_chiau.txt", "directoryOne/another_hi.txt");
        assertMockEndpointsSatisfied();
    }
}

