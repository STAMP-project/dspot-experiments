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
package org.apache.activemq.leveldb.test;


import java.io.File;
import java.util.ArrayList;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.leveldb.LevelDBStore;
import org.apache.activemq.leveldb.LevelDBStoreView;
import org.apache.activemq.leveldb.util.FileSupport;
import org.apache.activemq.store.MessageStore;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IndexRebuildTest {
    protected static final Logger LOG = LoggerFactory.getLogger(IndexRebuildTest.class);

    final int max = 30;

    final int toLeave = 5;

    ArrayList<LevelDBStore> stores = new ArrayList<LevelDBStore>();

    @Test(timeout = (1000 * 60) * 10)
    public void testRebuildIndex() throws Exception {
        File masterDir = new File("target/activemq-data/leveldb-rebuild");
        FileSupport.toRichFile(masterDir).recursiveDelete();
        final LevelDBStore store = new LevelDBStore();
        store.setDirectory(masterDir);
        store.setLogDirectory(masterDir);
        store.setLogSize((1024 * 10));
        store.start();
        stores.add(store);
        ArrayList<MessageId> inserts = new ArrayList<MessageId>();
        MessageStore ms = store.createQueueMessageStore(new ActiveMQQueue("TEST"));
        for (int i = 0; i < (max); i++) {
            inserts.add(ReplicationTestSupport.addMessage(ms, ("m" + i)).getMessageId());
        }
        int logFileCount = countLogFiles(store);
        Assert.assertTrue("more than one journal file", (logFileCount > 1));
        for (MessageId id : inserts.subList(0, ((inserts.size()) - (toLeave)))) {
            ReplicationTestSupport.removeMessage(ms, id);
        }
        LevelDBStoreView view = new LevelDBStoreView(store);
        view.compact();
        int reducedLogFileCount = countLogFiles(store);
        Assert.assertTrue("log files deleted", (logFileCount > reducedLogFileCount));
        store.stop();
        deleteTheIndex(store);
        Assert.assertEquals("log files remain", reducedLogFileCount, countLogFiles(store));
        // restart, recover and verify message read
        store.start();
        ms = store.createQueueMessageStore(new ActiveMQQueue("TEST"));
        Assert.assertEquals(((toLeave) + " messages remain"), toLeave, ReplicationTestSupport.getMessages(ms).size());
    }
}

