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


import java.sql.Connection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * shows broker 'once only delivery' and recovery with XA
 */
public class JmsJdbcXATest extends CamelSpringTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsJdbcXATest.class);

    BrokerService broker = null;

    int messageCount;

    @Test
    public void testRecoveryCommit() throws Exception {
        Connection jdbcConn = initDb();
        sendJMSMessageToKickOffRoute();
        JmsJdbcXATest.LOG.info("waiting for route to kick in, it will kill the broker on first 2pc commit");
        // will be stopped by the plugin on first 2pc commit
        broker.waitUntilStopped();
        assertEquals("message in db, commit to db worked", 1, dumpDb(jdbcConn));
        JmsJdbcXATest.LOG.info("Broker stopped, restarting...");
        broker = createBroker(false);
        broker.start();
        broker.waitUntilStarted();
        assertEquals("pending transactions", 1, broker.getBroker().getPreparedTransactions(null).length);
        // TM stays actively committing first message ack which won't get redelivered - xa once only delivery
        JmsJdbcXATest.LOG.info("waiting for recovery to complete");
        assertTrue("recovery complete in time", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getBroker().getPreparedTransactions(null).length) == 0;
            }
        }));
        // verify recovery complete
        assertEquals("recovery complete", 0, broker.getBroker().getPreparedTransactions(null).length);
        final Connection freshConnection = getJDBCConnection();
        assertTrue("did not get replay", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == (dumpDb(freshConnection));
            }
        }));
        assertEquals("still one message in db", 1, dumpDb(freshConnection));
        // let once complete ok
        sendJMSMessageToKickOffRoute();
        assertTrue("got second message", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 2 == (dumpDb(freshConnection));
            }
        }));
        assertEquals("two messages in db", 2, dumpDb(freshConnection));
    }
}

