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
import org.junit.Test;


/**
 * Unit test for writing done files
 */
public class FilerConsumerDualDoneFileNameTest extends ContextTestSupport {
    @Test
    public void testTwoDoneFile() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder("Hello World", "Bye World");
        template.sendBodyAndHeader("file:target/data/done?doneFileName=${file:name}.ready", "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader("file:target/data/done?doneFileName=${file:name}.ready", "Bye World", FILE_NAME, "bye.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOneDoneFileMissing() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBodyAndHeader("file:target/data/done?doneFileName=${file:name}.ready", "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader("file:target/data/done", "Bye World", FILE_NAME, "bye.txt");
        // give chance to poll 2nd file but it lacks the done file
        Thread.sleep(250);
        assertMockEndpointsSatisfied();
    }
}

