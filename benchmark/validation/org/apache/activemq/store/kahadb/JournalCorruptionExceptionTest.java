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
public class JournalCorruptionExceptionTest {
    private static final Logger LOG = LoggerFactory.getLogger(JournalCorruptionExceptionTest.class);

    private final String KAHADB_DIRECTORY = "target/activemq-data/";

    private final String payload = new String(new byte[1024]);

    private ActiveMQConnectionFactory cf = null;

    private BrokerService broker = null;

    private final Destination destination = new ActiveMQQueue("Test");

    private String connectionUri;

    private KahaDBPersistenceAdapter adapter;

    @Parameterized.Parameter(0)
    public byte fill = Byte.valueOf("3");

    @Parameterized.Parameter(1)
    public int fillLength = 10;

    @Test
    public void testIOExceptionOnCorruptJournalLocationRead() throws Exception {
        startBroker();
        produceMessagesToConsumeMultipleDataFiles(50);
        int numFiles = getNumberOfJournalFiles();
        Assert.assertTrue(("more than x files: " + numFiles), (numFiles > 4));
        corruptLocationAtDataFileIndex(3);
        Assert.assertEquals("missing one message", 50, broker.getAdminView().getTotalMessageCount());
        Assert.assertEquals("Drain", 0, drainQueue(50));
        Assert.assertTrue("broker stopping", broker.isStopping());
    }
}

