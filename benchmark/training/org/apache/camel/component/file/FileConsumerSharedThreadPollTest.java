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
import java.util.concurrent.ScheduledExecutorService;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 *
 */
public class FileConsumerSharedThreadPollTest extends ContextTestSupport {
    private ScheduledExecutorService pool;

    @Test
    public void testSharedThreadPool() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        // thread thread name should be the same
        mock.message(0).header("threadName").isEqualTo(mock.message(1).header("threadName"));
        template.sendBodyAndHeader("file:target/data/a", "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader("file:target/data/b", "Bye World", FILE_NAME, "bye.txt");
        assertMockEndpointsSatisfied();
    }
}

