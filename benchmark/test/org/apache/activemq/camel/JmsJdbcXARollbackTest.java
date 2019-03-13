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
package org.apache.activemq.camel;


import SharedDeadLetterStrategy.DEFAULT_DEAD_LETTER_QUEUE_NAME;
import java.sql.Connection;
import javax.transaction.TransactionManager;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.apache.camel.Exchange;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * shows rollback and redelivery dlq respected with external tm
 */
public class JmsJdbcXARollbackTest extends CamelSpringTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsJdbcXARollbackTest.class);

    BrokerService broker = null;

    int messageCount;

    @Test
    public void testConsumeRollback() throws Exception {
        Connection jdbcConn = initDb();
        initTMRef();
        sendJMSMessageToKickOffRoute();
        // should go to dlq eventually
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return consumedFrom(DEFAULT_DEAD_LETTER_QUEUE_NAME);
            }
        });
        assertEquals("message in db, commit to db worked", 0, dumpDb(jdbcConn));
        assertFalse("Nothing to to out q", consumedFrom("scp_transacted_out"));
    }

    static TransactionManager[] transactionManager = new TransactionManager[1];

    public static class MarkRollbackOnly {
        public String enrich(Exchange exchange) throws Exception {
            JmsJdbcXARollbackTest.LOG.info(("Got exchange: " + exchange));
            JmsJdbcXARollbackTest.LOG.info(("Got message: " + (getJmsMessage())));
            JmsJdbcXARollbackTest.LOG.info(("Current tx: " + (JmsJdbcXARollbackTest.transactionManager[0].getTransaction())));
            JmsJdbcXARollbackTest.LOG.info("Marking rollback only...");
            JmsJdbcXARollbackTest.transactionManager[0].getTransaction().setRollbackOnly();
            return "Some Text";
        }
    }
}

