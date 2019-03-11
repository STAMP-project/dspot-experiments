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


import java.io.File;
import java.io.FilenameFilter;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MKahaDBIndexLocationTest {
    private static final Logger LOG = LoggerFactory.getLogger(MKahaDBIndexLocationTest.class);

    private BrokerService broker;

    private final File testDataDir = new File("target/activemq-data/ConfigIndexDir");

    private final File kahaDataDir = new File(testDataDir, "log");

    private final File kahaIndexDir = new File(testDataDir, "index");

    private final ActiveMQQueue queue = new ActiveMQQueue("Qq");

    @Test
    public void testIndexDirExists() throws Exception {
        produceMessages();
        MKahaDBIndexLocationTest.LOG.info("Index dir is configured as: {}", kahaIndexDir);
        Assert.assertTrue(kahaDataDir.exists());
        Assert.assertTrue(kahaIndexDir.exists());
        String destName = MultiKahaDBPersistenceAdapter.nameFromDestinationFilter(queue);
        String[] index = new File(kahaIndexDir, destName).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                MKahaDBIndexLocationTest.LOG.info("Testing index filename: {}", name);
                return (name.endsWith("data")) || (name.endsWith("redo"));
            }
        });
        String[] journal = new File(kahaDataDir, destName).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                MKahaDBIndexLocationTest.LOG.info("Testing log filename: {}", name);
                return (name.endsWith("log")) || (name.equals("lock"));
            }
        });
        // Should be db.data and db.redo and nothing else.
        Assert.assertNotNull(index);
        Assert.assertEquals(2, index.length);
        // Should contain the initial log for the journal
        Assert.assertNotNull(journal);
        Assert.assertEquals(1, journal.length);
        stopBroker();
        createBroker();
        broker.start();
        broker.waitUntilStarted();
        consume();
    }
}

