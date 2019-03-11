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
package org.apache.activemq.store.kahadb;


import Session.AUTO_ACKNOWLEDGE;
import java.io.File;
import java.io.FilenameFilter;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class KahaDBIndexLocationTest {
    private static final Logger LOG = LoggerFactory.getLogger(KahaDBIndexLocationTest.class);

    @Rule
    public TestName name = new TestName();

    private BrokerService broker;

    private final File testDataDir = new File("target/activemq-data/QueuePurgeTest");

    private final File kahaDataDir = new File(testDataDir, "kahadb");

    private final File kahaIndexDir = new File(testDataDir, "kahadb/index");

    @Test
    public void testIndexDirExists() throws Exception {
        KahaDBIndexLocationTest.LOG.info("Index dir is configured as: {}", kahaIndexDir);
        Assert.assertTrue(kahaDataDir.exists());
        Assert.assertTrue(kahaIndexDir.exists());
        String[] index = kahaIndexDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                KahaDBIndexLocationTest.LOG.info("Testing filename: {}", name);
                return (name.endsWith("data")) || (name.endsWith("redo"));
            }
        });
        String[] journal = kahaDataDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                KahaDBIndexLocationTest.LOG.info("Testing filename: {}", name);
                return (name.endsWith("log")) || (name.equals("lock"));
            }
        });
        produceMessages();
        // Should be db.data and db.redo and nothing else.
        Assert.assertNotNull(index);
        Assert.assertEquals(2, index.length);
        // Should contain the initial log for the journal and the lock.
        Assert.assertNotNull(journal);
        Assert.assertEquals(2, journal.length);
    }

    @Test
    public void testRestartWithDeleteWorksWhenIndexIsSeparate() throws Exception {
        produceMessages();
        restartBroker();
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?create=false");
        Connection connection = cf.createConnection();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(name.getMethodName());
        MessageConsumer consumer = session.createConsumer(queue);
        Assert.assertNull(consumer.receive(2000));
    }
}

