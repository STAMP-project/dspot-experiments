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


import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JdbcAggregateLoadTest extends AbstractJdbcAggregationTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcAggregateLoadTest.class);

    private static final int SIZE = 500;

    @Test
    public void testLoadTestJdbcAggregate() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.setResultWaitTime((50 * 1000));
        JdbcAggregateLoadTest.LOG.info((("Staring to send " + (JdbcAggregateLoadTest.SIZE)) + " messages."));
        for (int i = 0; i < (JdbcAggregateLoadTest.SIZE); i++) {
            final int value = 1;
            char id = 'A';
            JdbcAggregateLoadTest.LOG.debug("Sending {} with id {}", value, id);
            template.sendBodyAndHeader(("seda:start?size=" + (JdbcAggregateLoadTest.SIZE)), value, "id", ("" + id));
        }
        JdbcAggregateLoadTest.LOG.info((("Sending all " + (JdbcAggregateLoadTest.SIZE)) + " message done. Now waiting for aggregation to complete."));
        assertMockEndpointsSatisfied();
    }
}

