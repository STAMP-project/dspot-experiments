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
import org.apache.camel.AggregationStrategy;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.junit.Test;


/**
 *
 */
public class FileMulticastDeleteTest extends ContextTestSupport {
    @Test
    public void testFileMulticastDelete() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("Got Hello World");
        getMockEndpoint("mock:bar").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBodyAndHeader("file:target/data/inbox", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    public class MyFileAggregator implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            // load data
            String data = newExchange.getIn().getBody(String.class);
            newExchange.getIn().setBody(("Got " + data));
            return newExchange;
        }
    }
}

