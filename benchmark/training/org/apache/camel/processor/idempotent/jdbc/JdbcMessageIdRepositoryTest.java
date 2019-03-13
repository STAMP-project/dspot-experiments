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
package org.apache.camel.processor.idempotent.jdbc;


import java.util.List;
import javax.sql.DataSource;
import org.apache.camel.EndpointInject;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;


public class JdbcMessageIdRepositoryTest extends CamelSpringTestSupport {
    protected static final String SELECT_ALL_STRING = "SELECT messageId FROM CAMEL_MESSAGEPROCESSED WHERE processorName = ?";

    protected static final String CLEAR_STRING = "DELETE FROM CAMEL_MESSAGEPROCESSED WHERE processorName = ?";

    protected static final String PROCESSOR_NAME = "myProcessorName";

    protected JdbcTemplate jdbcTemplate;

    protected DataSource dataSource;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @EndpointInject(uri = "mock:error")
    protected MockEndpoint errorEndpoint;

    @Test
    public void testDuplicateMessagesAreFilteredOut() throws Exception {
        resultEndpoint.expectedBodiesReceived("one", "two", "three");
        errorEndpoint.expectedMessageCount(0);
        template.sendBodyAndHeader("direct:start", "one", "messageId", "1");
        template.sendBodyAndHeader("direct:start", "two", "messageId", "2");
        template.sendBodyAndHeader("direct:start", "one", "messageId", "1");
        template.sendBodyAndHeader("direct:start", "two", "messageId", "2");
        template.sendBodyAndHeader("direct:start", "one", "messageId", "1");
        template.sendBodyAndHeader("direct:start", "three", "messageId", "3");
        assertMockEndpointsSatisfied();
        // all 3 messages should be in jdbc repo
        List<String> receivedMessageIds = jdbcTemplate.queryForList(JdbcMessageIdRepositoryTest.SELECT_ALL_STRING, String.class, JdbcMessageIdRepositoryTest.PROCESSOR_NAME);
        assertEquals(3, receivedMessageIds.size());
        assertTrue(receivedMessageIds.contains("1"));
        assertTrue(receivedMessageIds.contains("2"));
        assertTrue(receivedMessageIds.contains("3"));
    }

    @Test
    public void testFailedExchangesNotAdded() throws Exception {
        RouteBuilder interceptor = new RouteBuilder(context) {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("mock:result").process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        String id = exchange.getIn().getHeader("messageId", String.class);
                        if (id.equals("2")) {
                            throw new IllegalArgumentException("Damn I cannot handle id 2");
                        }
                    }
                });
            }
        };
        RouteDefinition routeDefinition = context.getRouteDefinition("JdbcMessageIdRepositoryTest");
        RouteReifier.adviceWith(routeDefinition, context, interceptor);
        // we send in 2 messages with id 2 that fails
        errorEndpoint.expectedMessageCount(2);
        resultEndpoint.expectedBodiesReceived("one", "three");
        template.sendBodyAndHeader("direct:start", "one", "messageId", "1");
        template.sendBodyAndHeader("direct:start", "two", "messageId", "2");
        template.sendBodyAndHeader("direct:start", "one", "messageId", "1");
        template.sendBodyAndHeader("direct:start", "two", "messageId", "2");
        template.sendBodyAndHeader("direct:start", "one", "messageId", "1");
        template.sendBodyAndHeader("direct:start", "three", "messageId", "3");
        assertMockEndpointsSatisfied();
        jdbcTemplate.update(JdbcMessageIdRepositoryTest.CLEAR_STRING, JdbcMessageIdRepositoryTest.PROCESSOR_NAME);
        // only message 1 and 3 should be in jdbc repo
        List<String> receivedMessageIds = jdbcTemplate.queryForList(JdbcMessageIdRepositoryTest.SELECT_ALL_STRING, String.class, JdbcMessageIdRepositoryTest.PROCESSOR_NAME);
        assertEquals(0, receivedMessageIds.size());
        assertFalse("Should not contain message 1", receivedMessageIds.contains("1"));
        assertFalse("Should not contain message 3", receivedMessageIds.contains("3"));
    }
}

