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
package org.apache.activemq.store.kahadb.disk.journal;


import java.util.concurrent.TimeUnit;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JournalMaxFileLengthChangeTest {
    private static final Logger LOG = LoggerFactory.getLogger(JournalMaxFileLengthChangeTest.class);

    private static int ONE_MB = 1024 * 1024;

    @Rule
    public TemporaryFolder dataDir = new TemporaryFolder();

    @Rule
    public Timeout globalTimeout = new Timeout(20, TimeUnit.SECONDS);

    private KahaDBStore store;

    /**
     * Test that reported size is correct if the maxFileLength grows
     * in between journal restarts.  Verify all messages still received.
     */
    @Test
    public void testMaxFileLengthGrow() throws Exception {
        MessageStore messageStore = createStore((8 * (JournalMaxFileLengthChangeTest.ONE_MB)));
        addMessages(messageStore, 4);
        long sizeBeforeChange = store.getJournal().getDiskSize();
        JournalMaxFileLengthChangeTest.LOG.info(("Journal size before: " + sizeBeforeChange));
        store.stop();
        messageStore = createStore((6 * (JournalMaxFileLengthChangeTest.ONE_MB)));
        verifyMessages(messageStore, 4);
        long sizeAfterChange = store.getJournal().getDiskSize();
        JournalMaxFileLengthChangeTest.LOG.info(("Journal size after: " + sizeAfterChange));
        // verify the size is the same - will be slightly different as checkpoint journal
        // commands are written but should be close
        Assert.assertEquals(sizeBeforeChange, sizeAfterChange, 4096);
    }

    /**
     * Test that reported size is correct if the maxFileLength shrinks
     * in between journal restarts.  Verify all messages still received.
     */
    @Test
    public void testMaxFileLengthShrink() throws Exception {
        MessageStore messageStore = createStore((8 * (JournalMaxFileLengthChangeTest.ONE_MB)));
        addMessages(messageStore, 4);
        long sizeBeforeChange = store.getJournal().getDiskSize();
        JournalMaxFileLengthChangeTest.LOG.info(("Journal size before: " + sizeBeforeChange));
        store.stop();
        messageStore = createStore((2 * (JournalMaxFileLengthChangeTest.ONE_MB)));
        verifyMessages(messageStore, 4);
        long sizeAfterChange = store.getJournal().getDiskSize();
        JournalMaxFileLengthChangeTest.LOG.info(("Journal size after: " + sizeAfterChange));
        // verify the size is the same - will be slightly different as checkpoint journal
        // commands are written but should be close
        Assert.assertEquals(sizeBeforeChange, sizeAfterChange, 4096);
    }
}

