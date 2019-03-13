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
package org.apache.activemq.bugs;


import DeliveryMode.NON_PERSISTENT;
import Session.CLIENT_ACKNOWLEDGE;
import java.util.List;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.scheduler.Job;
import org.apache.activemq.broker.scheduler.JobScheduler;
import org.apache.activemq.store.kahadb.scheduler.JobSchedulerStoreImpl;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ7002Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ7002Test.class);

    protected ActiveMQConnection connection;

    protected ActiveMQConnectionFactory connectionFactory;

    private BrokerService brokerService;

    private JobSchedulerStoreImpl store;

    @Test
    public void TestDuplicateJobIDs() throws Exception {
        AMQ7002Test.ConsumerObject consumer1 = getConsumer(1);
        AMQ7002Test.ConsumerObject consumer2 = getConsumer(2);
        ActiveMQConnection producerConnection = ((ActiveMQConnection) (createConnection()));
        producerConnection.start();
        // Session session = producerConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Session session = producerConnection.createSession(false, CLIENT_ACKNOWLEDGE);
        Destination dest = session.createTopic("VirtualTopic.Orders");
        MessageProducer producer = session.createProducer(dest);
        TextMessage msg = session.createTextMessage("Test Me");
        producer.setDeliveryMode(NON_PERSISTENT);
        producer.send(msg);
        Message message1 = consumer1.getConsumer().receive();
        TestCase.assertNotNull("got message", message1);
        AMQ7002Test.LOG.info(("got: " + message1));
        Message message2 = consumer2.getConsumer().receive();
        TestCase.assertNotNull("got message", message2);
        AMQ7002Test.LOG.info(("got: " + message2));
        // Force rollback
        consumer1.getSession().rollback();
        consumer2.getSession().rollback();
        // Check the scheduled jobs here //
        Thread.sleep(2000);
        JobScheduler js = brokerService.getJobSchedulerStore().getJobScheduler("JMS");
        List<Job> jobList = js.getAllJobs();
        TestCase.assertNotNull(jobList);
        Assert.assertEquals(2, jobList.size());
        String jobId1 = jobList.get(0).getJobId();
        String jobId2 = jobList.get(1).getJobId();
        Assert.assertFalse("FAIL: JobIDs are duplicates!", jobId1.equals(jobId2));
    }

    private class ConsumerObject {
        Session session;

        MessageConsumer consumer;

        Connection connection;

        public ConsumerObject(Session session, MessageConsumer consumer, Connection connection) {
            this.session = session;
            this.consumer = consumer;
            this.connection = connection;
        }

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        public MessageConsumer getConsumer() {
            return consumer;
        }

        public void setConsumer(MessageConsumer consumer) {
            this.consumer = consumer;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }
    }
}

