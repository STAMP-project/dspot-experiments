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
package org.apache.camel.component.jms;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test that chained request/reply over JMS works in parallel mode with the splitter EIP.
 */
public class JmsSplitterParallelChainedTest extends CamelTestSupport {
    @Test
    public void testSplitParallel() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("A,B,C,D,E");
        getMockEndpoint("mock:reply").expectedBodiesReceivedInAnyOrder("Hi A", "Hi B", "Hi C", "Hi D", "Hi E");
        getMockEndpoint("mock:reply2").expectedBodiesReceivedInAnyOrder("Bye Hi A", "Bye Hi B", "Bye Hi C", "Bye Hi D", "Bye Hi E");
        getMockEndpoint("mock:split").expectedBodiesReceivedInAnyOrder("Bye Hi A", "Bye Hi B", "Bye Hi C", "Bye Hi D", "Bye Hi E");
        template.sendBody("direct:start", "A,B,C,D,E");
        assertMockEndpointsSatisfied();
    }
}

