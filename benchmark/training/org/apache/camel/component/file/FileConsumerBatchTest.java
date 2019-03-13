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


import Exchange.BATCH_INDEX;
import Exchange.BATCH_SIZE;
import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test for consuming a batch of files (multiple files in one consume)
 */
public class FileConsumerBatchTest extends ContextTestSupport {
    @Test
    public void testConsumeBatch() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceivedInAnyOrder("Hello World", "Bye World");
        template.sendBodyAndHeader("file://target/data/file-batch/", "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader("file://target/data/file-batch/", "Bye World", FILE_NAME, "bye.txt");
        // test header keys
        mock.message(0).exchangeProperty(BATCH_SIZE).isEqualTo(2);
        mock.message(0).exchangeProperty(BATCH_INDEX).isEqualTo(0);
        mock.message(1).exchangeProperty(BATCH_INDEX).isEqualTo(1);
        // start routes
        context.getRouteController().startAllRoutes();
        assertMockEndpointsSatisfied();
    }
}

