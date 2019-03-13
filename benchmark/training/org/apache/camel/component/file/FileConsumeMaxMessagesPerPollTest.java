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


import Exchange.BATCH_SIZE;
import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test for max messages per poll
 */
public class FileConsumeMaxMessagesPerPollTest extends ContextTestSupport {
    private String fileUrl = "file://target/data/poll/?initialDelay=0&delay=10&maxMessagesPerPoll=2";

    @Test
    public void testMaxMessagesPerPoll() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        // we should poll at most 2
        mock.expectedMinimumMessageCount(2);
        mock.message(0).exchangeProperty(BATCH_SIZE).isEqualTo(2);
        mock.message(1).exchangeProperty(BATCH_SIZE).isEqualTo(2);
        template.sendBodyAndHeader(fileUrl, "Bye World", FILE_NAME, "bye.txt");
        template.sendBodyAndHeader(fileUrl, "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader(fileUrl, "Godday World", FILE_NAME, "godday.txt");
        // start route
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
    }
}

