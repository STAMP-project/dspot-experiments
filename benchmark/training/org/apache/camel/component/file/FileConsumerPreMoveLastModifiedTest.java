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


import Exchange.FILE_LAST_MODIFIED;
import Exchange.FILE_LENGTH;
import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class FileConsumerPreMoveLastModifiedTest extends ContextTestSupport {
    @Test
    public void testPreMoveLastModified() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("file://target/data/premove", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    private static class LastModifiedCheckerProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            Assert.assertTrue(((exchange.getIn().getHeader(FILE_LAST_MODIFIED, Long.class)) > 0L));
            Assert.assertTrue(((exchange.getIn().getHeader(FILE_LENGTH, Long.class)) > 0L));
        }
    }
}

