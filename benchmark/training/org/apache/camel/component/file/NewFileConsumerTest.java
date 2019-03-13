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
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Processor;
import org.junit.Test;


/**
 *
 */
public class NewFileConsumerTest extends ContextTestSupport {
    private NewFileConsumerTest.MyFileEndpoint myFile;

    @Test
    public void testNewFileConsumer() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/data/myfile", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        await("postPollCheck invocation").atMost(1, TimeUnit.SECONDS).until(myFile::isPost);
    }

    private class MyFileEndpoint extends FileEndpoint {
        private volatile boolean post;

        protected FileConsumer newFileConsumer(Processor processor, GenericFileOperations<File> operations) {
            return new FileConsumer(this, processor, operations, createGenericFileStrategy()) {
                @Override
                protected void postPollCheck(int polledMessages) {
                    post = true;
                }
            };
        }

        public boolean isPost() {
            return post;
        }
    }
}

