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
package org.apache.activemq.store.jdbc;


import Session.AUTO_ACKNOWLEDGE;
import java.util.List;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.Message;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JDBCTablePrefixAssignedTest {
    private static final Logger LOG = LoggerFactory.getLogger(JDBCTablePrefixAssignedTest.class);

    private BrokerService service;

    @Test
    public void testTablesHave() throws Exception {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?create=false");
        ActiveMQConnection connection = ((ActiveMQConnection) (cf.createConnection()));
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("TEST.FOO");
        MessageProducer producer = session.createProducer(destination);
        for (int i = 0; i < 10; ++i) {
            producer.send(session.createTextMessage("test"));
        }
        producer.close();
        connection.close();
        List<Message> queuedMessages = null;
        try {
            queuedMessages = dumpMessages();
        } catch (Exception ex) {
            JDBCTablePrefixAssignedTest.LOG.info("Caught ex: ", ex);
            Assert.fail("Should not have thrown an exception");
        }
        Assert.assertNotNull(queuedMessages);
        Assert.assertEquals("Should have found 10 messages", 10, queuedMessages.size());
    }
}

