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
package org.apache.camel.itest.sql;


import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Jms with JDBC idempotent consumer test.
 */
public class FromJmsToJdbcIdempotentConsumerToJmsTest extends CamelSpringTestSupport {
    protected JdbcTemplate jdbcTemplate;

    protected DataSource dataSource;

    @Test
    public void testJmsToJdbcJmsCommit() throws Exception {
        checkInitialState();
        // use a notify to know when the message is done
        NotifyBuilder notify = whenDone(1).create();
        // use mock during testing as well
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        template.sendBodyAndHeader("activemq2:queue:inbox", "A", "uid", 123);
        // assert mock and wait for the message to be done
        assertMockEndpointsSatisfied();
        assertTrue("Should complete 1 message", notify.matchesMockWaitTime());
        // check that there is a message in the database and JMS queue
        assertEquals(new Integer(1), jdbcTemplate.queryForObject("select count(*) from CAMEL_MESSAGEPROCESSED", Integer.class));
        Object out = consumer.receiveBody("activemq2:queue:outbox", 3000);
        assertEquals("DONE-A", out);
    }

    @Test
    public void testFilterIdempotent() throws Exception {
        checkInitialState();
        // use a notify to know when the message is done
        NotifyBuilder notify = whenDone(3).create();
        // use mock during testing as well
        getMockEndpoint("mock:a").expectedMessageCount(3);
        // there should be 1 duplicate
        getMockEndpoint("mock:b").expectedMessageCount(2);
        template.sendBodyAndHeader("activemq2:queue:inbox", "D", "uid", 111);
        template.sendBodyAndHeader("activemq2:queue:inbox", "E", "uid", 222);
        template.sendBodyAndHeader("activemq2:queue:inbox", "D", "uid", 111);
        // assert mock and wait for the message to be done
        assertMockEndpointsSatisfied();
        assertTrue("Should complete 3 messages", notify.matchesMockWaitTime());
        // check that there is two messages in the database and JMS queue
        assertEquals(new Integer(2), jdbcTemplate.queryForObject("select count(*) from CAMEL_MESSAGEPROCESSED", Integer.class));
        assertEquals("DONE-D", consumer.receiveBody("activemq2:queue:outbox", 3000));
        assertEquals("DONE-E", consumer.receiveBody("activemq2:queue:outbox", 3000));
    }

    @Test
    public void testRetryAfterException() throws Exception {
        checkInitialState();
        final AtomicInteger counter = new AtomicInteger();
        // use a notify to know when the message is done
        NotifyBuilder notify = whenDone(4).create();
        // use mock during testing as well
        getMockEndpoint("mock:a").expectedMessageCount(4);
        // there should be 1 duplicate
        getMockEndpoint("mock:b").expectedMessageCount(4);
        getMockEndpoint("mock:b").whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                if ((counter.getAndIncrement()) == 1) {
                    throw new ConnectException("Forced cannot send to AMQ queue");
                }
            }
        });
        template.sendBodyAndHeader("activemq2:queue:inbox", "D", "uid", 111);
        template.sendBodyAndHeader("activemq2:queue:inbox", "E", "uid", 222);
        template.sendBodyAndHeader("activemq2:queue:inbox", "F", "uid", 333);
        // assert mock and wait for the message to be done
        assertMockEndpointsSatisfied();
        assertTrue("Should complete 4 messages", notify.matchesMockWaitTime());
        // check that there is two messages in the database and JMS queue
        assertEquals(new Integer(3), jdbcTemplate.queryForObject("select count(*) from CAMEL_MESSAGEPROCESSED", Integer.class));
        assertEquals("DONE-D", consumer.receiveBody("activemq2:queue:outbox", 3000));
        assertEquals("DONE-E", consumer.receiveBody("activemq2:queue:outbox", 3000));
        assertEquals("DONE-F", consumer.receiveBody("activemq2:queue:outbox", 3000));
    }
}

