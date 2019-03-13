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
package org.apache.camel.processor;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.junit.Test;


public class SplitterStreamingUoWIssueTest extends ContextTestSupport {
    @Test
    public void testSplitterStreamingUoWIssue() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("A", "B", "C", "D", "E");
        getMockEndpoint("mock:result").expectedBodiesReceived("A,B,C,D,E");
        template.sendBodyAndHeader("file:target/data/splitter", "A,B,C,D,E", FILE_NAME, "splitme.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSplitterTwoFilesStreamingUoWIssue() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("A", "B", "C", "D", "E", "F", "G", "H", "I");
        getMockEndpoint("mock:result").expectedBodiesReceived("A,B,C,D,E", "F,G,H,I");
        template.sendBodyAndHeader("file:target/data/splitter", "A,B,C,D,E", FILE_NAME, "a.txt");
        template.sendBodyAndHeader("file:target/data/splitter", "F,G,H,I", FILE_NAME, "b.txt");
        assertMockEndpointsSatisfied();
    }
}

