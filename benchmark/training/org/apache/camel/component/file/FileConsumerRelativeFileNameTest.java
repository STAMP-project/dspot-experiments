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
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class FileConsumerRelativeFileNameTest extends ContextTestSupport {
    @Test
    public void testValidFilenameOnExchange() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        // should have file name header set
        mock.allMessages().header(FILE_NAME).isNotNull();
        // the file name is also starting with target/data/filename-consumer
        template.sendBodyAndHeader("file:target/data/filename-consumer", "Hello World", FILE_NAME, "target/data/filename-consumer-hello.txt");
        template.sendBodyAndHeader("file:target/data/filename-consumer", "Bye World", FILE_NAME, "target/data/filename-consumer-bye.txt");
        context.getRouteController().startAllRoutes();
        assertMockEndpointsSatisfied();
        // and expect name to contain target/data/filename-consumer-XXX.txt
        TestSupport.assertDirectoryEquals("target/data/filename-consumer-bye.txt", mock.getReceivedExchanges().get(0).getIn().getHeader(FILE_NAME, String.class));
        TestSupport.assertDirectoryEquals("target/data/filename-consumer-hello.txt", mock.getReceivedExchanges().get(1).getIn().getHeader(FILE_NAME, String.class));
    }
}

