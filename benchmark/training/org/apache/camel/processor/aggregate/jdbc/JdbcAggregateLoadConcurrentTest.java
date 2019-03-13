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
package org.apache.camel.processor.aggregate.jdbc;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JdbcAggregateLoadConcurrentTest extends AbstractJdbcAggregationTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcAggregateLoadConcurrentTest.class);

    private static final char[] KEYS = new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J' };

    private static final int SIZE = 500;

    @Test
    public void testLoadTestJdbcAggregate() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(10);
        mock.setResultWaitTime((50 * 1000));
        ExecutorService executor = Executors.newFixedThreadPool(10);
        JdbcAggregateLoadConcurrentTest.LOG.info((("Staring to send " + (JdbcAggregateLoadConcurrentTest.SIZE)) + " messages."));
        for (int i = 0; i < (JdbcAggregateLoadConcurrentTest.SIZE); i++) {
            final int value = 1;
            final int key = i % 10;
            executor.submit(new Callable<Object>() {
                public Object call() throws Exception {
                    char id = JdbcAggregateLoadConcurrentTest.KEYS[key];
                    JdbcAggregateLoadConcurrentTest.LOG.debug("Sending {} with id {}", value, id);
                    template.sendBodyAndHeader("direct:start", value, "id", ("" + id));
                    // simulate a little delay
                    Thread.sleep(3);
                    return null;
                }
            });
        }
        JdbcAggregateLoadConcurrentTest.LOG.info((("Sending all " + (JdbcAggregateLoadConcurrentTest.SIZE)) + " message done. Now waiting for aggregation to complete."));
        assertMockEndpointsSatisfied();
        executor.shutdownNow();
    }
}

