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


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class MulticastParallelStressTest extends ContextTestSupport {
    @Test
    public void testTwoMulticast() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("ABCD", "ABCD");
        mock.expectsAscending().header("id");
        template.sendBodyAndHeader("direct:start", "", "id", 1);
        template.sendBodyAndHeader("direct:start", "", "id", 2);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMoreMulticast() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(20);
        mock.expectsAscending().header("id");
        for (int i = 0; i < 20; i++) {
            template.sendBodyAndHeader("direct:start", "", "id", i);
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testConcurrencyParallelMulticast() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(20);
        // this time we cannot expect in order but there should be no duplicates
        mock.expectsNoDuplicates(TestSupport.header("id"));
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 20; i++) {
            final int index = i;
            executor.submit(new Callable<Object>() {
                public Object call() throws Exception {
                    template.sendBodyAndHeader("direct:start", "", "id", index);
                    return null;
                }
            });
        }
        assertMockEndpointsSatisfied();
        executor.shutdownNow();
    }
}

