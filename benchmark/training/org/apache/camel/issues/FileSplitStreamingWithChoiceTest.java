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
package org.apache.camel.issues;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class FileSplitStreamingWithChoiceTest extends ContextTestSupport {
    @Test
    public void testSplitStreamingWithChoice() throws Exception {
        getMockEndpoint("mock:other").expectedMessageCount(0);
        MockEndpoint mock = getMockEndpoint("mock:body");
        mock.expectedBodiesReceived("line1", "line2", "line3");
        // should be moved to this directory after we are done
        mock.expectedFileExists("target/data/filesplit/.camel/splitme.txt");
        String body = ((("line1" + (TestSupport.LS)) + "line2") + (TestSupport.LS)) + "line3";
        template.sendBodyAndHeader("file://target/data/filesplit", body, FILE_NAME, "splitme.txt");
        assertMockEndpointsSatisfied();
    }
}

