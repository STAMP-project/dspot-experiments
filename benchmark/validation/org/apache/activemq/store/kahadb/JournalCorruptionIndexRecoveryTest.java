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


import javax.jms.Destination;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class JournalCorruptionIndexRecoveryTest {
    private static final Logger LOG = LoggerFactory.getLogger(JournalCorruptionIndexRecoveryTest.class);

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    private final String payload = new String(new byte[1024]);

    private ActiveMQConnectionFactory cf = null;

    private BrokerService broker = null;

    private final Destination destination = new ActiveMQQueue("Test");

    private String connectionUri;

    private KahaDBPersistenceAdapter adapter;

    @Parameterized.Parameter(0)
    public byte fill = Byte.valueOf("3");

    @Test
    public void testRecoveryAfterCorruptionMiddle() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 4));
        corruptBatchMiddle(3);
        restartBroker();
        Assert.assertEquals("missing one message", 49, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 49, drainQueue(49));
    }

    @Test
    public void testRecoveryAfterCorruptionEnd() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 4));
        corruptBatchEnd(4);
        restartBroker();
        Assert.assertEquals("missing one message", 49, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 49, drainQueue(49));
    }

    @Test
    public void testRecoveryAfterCorruption() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 4));
        corruptBatchMiddle(3);
        corruptBatchEnd(4);
        restartBroker();
        Assert.assertEquals("missing one message", 48, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 48, drainQueue(48));
    }
}

