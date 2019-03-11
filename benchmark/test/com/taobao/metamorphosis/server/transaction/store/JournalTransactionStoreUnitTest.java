/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server.transaction.store;


import com.taobao.metamorphosis.network.PutCommand;
import com.taobao.metamorphosis.server.store.MessageStore;
import com.taobao.metamorphosis.server.transaction.BaseTransactionUnitTest;
import com.taobao.metamorphosis.server.transaction.TransactionRecoveryListener;
import com.taobao.metamorphosis.server.transaction.store.JournalTransactionStore.Tx;
import com.taobao.metamorphosis.server.utils.XIDGenerator;
import com.taobao.metamorphosis.transaction.LocalTransactionId;
import com.taobao.metamorphosis.transaction.XATransactionId;
import com.taobao.metamorphosis.utils.test.ConcurrentTestCase;
import com.taobao.metamorphosis.utils.test.ConcurrentTestTask;
import java.io.File;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static JournalStore.MAX_FILE_SIZE;


public class JournalTransactionStoreUnitTest extends BaseTransactionUnitTest {
    @Test
    public void testAddAddRollBackCloseRecover() throws Exception {
        final LocalTransactionId xid = new LocalTransactionId("test", 1);
        final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg1".getBytes(), xid, 0, 1), null);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg2".getBytes(), xid, 0, 2), null);
        final Tx tx = this.transactionStore.getInflyTx(xid);
        Assert.assertNotNull(tx);
        final PutCommand[] commands = tx.getRequests();
        Assert.assertNotNull(commands);
        Assert.assertEquals(2, commands.length);
        store.flush();
        // ????????
        Assert.assertEquals(0, store.getSizeInBytes());
        // rollback
        this.transactionStore.rollback(xid);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        store.flush();
        // ??????????????
        Assert.assertEquals(0, store.getSizeInBytes());
        this.tearDown();
        this.init(this.path);
        Assert.assertTrue(((this.journalStore.getCurrDataFile().getLength()) > 0));
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertEquals(0, this.messageStoreManager.getOrCreateMessageStore("topic1", 2).getSizeInBytes());
    }

    @Test
    public void testAddAddCloseRecover() throws Exception {
        final LocalTransactionId xid1 = new LocalTransactionId("test", 1);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg1".getBytes(), xid1, 0, 1), null);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg2".getBytes(), xid1, 0, 2), null);
        final LocalTransactionId xid2 = new LocalTransactionId("test", 2);
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg1".getBytes(), xid2, 0, 1), null);
        this.tearDown();
        this.init(this.path);
        Assert.assertTrue(((this.journalStore.getCurrDataFile().getLength()) > 0));
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        // ????????????
        Assert.assertEquals(0, store.getSizeInBytes());
        // ???TX??????
        final Tx tx1 = this.transactionStore.getInflyTx(xid1);
        Assert.assertNotNull(tx1);
        PutCommand[] commands = tx1.getRequests();
        Assert.assertNotNull(commands);
        Assert.assertEquals(2, commands.length);
        final Tx tx2 = this.transactionStore.getInflyTx(xid2);
        Assert.assertNotNull(tx2);
        commands = tx2.getRequests();
        Assert.assertNotNull(commands);
        Assert.assertEquals(1, commands.length);
        // recover????????
        this.transactionStore.recover(null);
        Assert.assertNull(this.transactionStore.getInflyTx(xid1));
        Assert.assertNull(this.transactionStore.getInflyTx(xid2));
        // ????????????????
        Assert.assertEquals(0, store.getSizeInBytes());
    }

    @Test
    public void testAddAddCommitCloseRecover() throws Exception {
        final LocalTransactionId xid = new LocalTransactionId("test", 1);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg1".getBytes(), xid, 0, 1), null);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg2".getBytes(), xid, 0, 2), null);
        final Tx tx = this.transactionStore.getInflyTx(xid);
        Assert.assertNotNull(tx);
        final PutCommand[] commands = tx.getRequests();
        Assert.assertNotNull(commands);
        Assert.assertEquals(2, commands.length);
        store.flush();
        // ????????
        Assert.assertEquals(0, store.getSizeInBytes());
        // rollback
        this.transactionStore.commit(xid, false);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        store.flush();
        // ??????
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
        this.tearDown();
        this.init(this.path);
        Assert.assertTrue(((this.journalStore.getCurrDataFile().getLength()) > 0));
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        // ?????store
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.assertMessages(store);
    }

    @Test
    public void testBeginCommitCloseRecover() throws Exception {
        final LocalTransactionId xid = new LocalTransactionId("test", 1);
        this.transactionStore.commit(xid, false);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        this.tearDown();
        this.init(this.path);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
    }

    @Test
    public void testAddAddPrepareCommitCloseRecover() throws Exception {
        final XATransactionId xid = XIDGenerator.createXID(99);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg1".getBytes(), xid, 0, 1), null);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg2".getBytes(), xid, 0, 2), null);
        Assert.assertNotNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNull(this.transactionStore.getPreparedTx(xid));
        // prepare
        this.transactionStore.prepare(xid);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNotNull(this.transactionStore.getPreparedTx(xid));
        store.flush();
        // ????????
        Assert.assertEquals(0, store.getSizeInBytes());
        // commit
        this.transactionStore.commit(xid, true);
        store.flush();
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNull(this.transactionStore.getPreparedTx(xid));
        // close and reopen
        this.tearDown();
        this.init(this.path);
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNull(this.transactionStore.getPreparedTx(xid));
        this.assertMessages(store);
    }

    @Test
    public void testAddAddPrepareRollbackCloseRecover() throws Exception {
        final XATransactionId xid = XIDGenerator.createXID(99);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg1".getBytes(), xid, 0, 1), null);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg2".getBytes(), xid, 0, 2), null);
        Assert.assertNotNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNull(this.transactionStore.getPreparedTx(xid));
        // prepare
        this.transactionStore.prepare(xid);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNotNull(this.transactionStore.getPreparedTx(xid));
        store.flush();
        // ????????
        Assert.assertEquals(0, store.getSizeInBytes());
        // rollback
        this.transactionStore.rollback(xid);
        store.flush();
        Assert.assertEquals(0, store.getSizeInBytes());
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNull(this.transactionStore.getPreparedTx(xid));
        // close and reopen
        this.tearDown();
        this.init(this.path);
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        Assert.assertEquals(0, store.getSizeInBytes());
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNull(this.transactionStore.getPreparedTx(xid));
    }

    @Test
    public void testAddAddPrepareCloseRecover() throws Exception {
        final XATransactionId xid = XIDGenerator.createXID(99);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        final PutCommand cmd1 = new PutCommand("topic1", 2, "msg1".getBytes(), xid, 0, 1);
        this.transactionStore.addMessage(store, 1, cmd1, null);
        final PutCommand cmd2 = new PutCommand("topic1", 2, "msg2".getBytes(), xid, 0, 2);
        this.transactionStore.addMessage(store, 1, cmd2, null);
        // prepare
        this.transactionStore.prepare(xid);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        Assert.assertNotNull(this.transactionStore.getPreparedTx(xid));
        store.flush();
        // ????????
        Assert.assertEquals(0, store.getSizeInBytes());
        // close and reopen
        this.tearDown();
        this.init(this.path);
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        Assert.assertEquals(0, store.getSizeInBytes());
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        // ???????prepare??
        Assert.assertNotNull(this.transactionStore.getPreparedTx(xid));
        // ??????????
        final Tx tx = this.transactionStore.getPreparedTx(xid);
        Assert.assertNotNull(tx);
        final PutCommand[] commands = tx.getRequests();
        Assert.assertNotNull(commands);
        Assert.assertEquals(2, commands.length);
        for (final PutCommand cmd : commands) {
            Assert.assertTrue(((cmd.equals(cmd1)) || (cmd.equals(cmd2))));
        }
        // recover
        this.transactionStore.recover(new TransactionRecoveryListener() {
            @Override
            public void recover(final XATransactionId id, final PutCommand[] addedMessages) {
                Assert.assertEquals(xid, id);
                Assert.assertArrayEquals(commands, addedMessages);
            }
        });
    }

    @Test
    public void testCheckpoint() throws Exception {
        // ????1
        final LocalTransactionId xid1 = new LocalTransactionId("session1", 1);
        final MessageStore store1 = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store1, 1, new PutCommand("topic1", 2, ("msg" + 2).getBytes(), xid1, 0, 1), null);
        // ????2
        final LocalTransactionId xid2 = new LocalTransactionId("session2", 1);
        final MessageStore store2 = this.messageStoreManager.getOrCreateMessageStore("topic1", 3);
        this.transactionStore.addMessage(store2, 1, new PutCommand("topic1", 3, ("msg" + 3).getBytes(), xid2, 0, 1), null);
        // ????3???????
        final LocalTransactionId xid3 = new LocalTransactionId("session3", 1);
        final MessageStore store3 = this.messageStoreManager.getOrCreateMessageStore("topic1", 0);
        this.transactionStore.addMessage(store3, 1, new PutCommand("topic1", 0, ("msg" + 0).getBytes(), xid3, 0, 1), null);
        this.transactionStore.commit(xid3, false);
        // ????checkpoint??????????1
        final JournalLocation location = this.transactionStore.checkpoint();
        final Tx tx = this.transactionStore.getInflyTx(xid1);
        Assert.assertEquals(location, tx.getLocation());
    }

    @Test
    public void concurrentTest() throws Exception {
        final Random rand = new Random();
        final AtomicInteger gen = new AtomicInteger();
        final ConcurrentTestCase testCase = new ConcurrentTestCase(100, 1000, new ConcurrentTestTask() {
            @Override
            public void run(final int index, final int times) throws Exception {
                final int id = gen.incrementAndGet();
                final LocalTransactionId xid = new LocalTransactionId("test", id);
                for (int j = 0; j < ((rand.nextInt(3)) + 1); j++) {
                    final int partition = rand.nextInt(10);
                    final MessageStore store = JournalTransactionStoreUnitTest.this.messageStoreManager.getOrCreateMessageStore("topic1", (partition % 10));
                    JournalTransactionStoreUnitTest.this.transactionStore.addMessage(store, 1, new PutCommand("topic1", partition, ("msg" + id).getBytes(), xid, 0, 1), null);
                }
                if ((id % 100) == 0) {
                    JournalTransactionStoreUnitTest.this.journalStore.checkpoint();
                }
                // commit
                JournalTransactionStoreUnitTest.this.transactionStore.commit(xid, false);
            }
        });
        testCase.start();
        System.out.println((("???????" + (testCase.getDurationInMillis())) + "ms"));
        for (int i = 0; i < 10; i++) {
            final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", i);
            Assert.assertTrue(((store.getSizeInBytes()) > 0));
        }
        Assert.assertEquals(0, this.transactionStore.getActiveTransactionCount());
        // ????
        this.tearDown();
        final long start = System.currentTimeMillis();
        this.init(this.path);
        System.out.println((("??????????:" + ((System.currentTimeMillis()) - start)) + "ms"));
        for (int i = 0; i < 10; i++) {
            final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", i);
            Assert.assertTrue(((store.getSizeInBytes()) > 0));
        }
        Assert.assertEquals(0, this.transactionStore.getActiveTransactionCount());
    }

    @Test
    public void testAddManyRollJournal() throws Exception {
        final Random rand = new Random();
        this.tearDown();
        final int oldSize = MAX_FILE_SIZE;
        MAX_FILE_SIZE = 512;
        try {
            this.init(this.path);
            for (int i = 0; i < 10000; i++) {
                final LocalTransactionId xid = new LocalTransactionId("test", i);
                // ???????????
                for (int j = 0; j < ((rand.nextInt(3)) + 1); j++) {
                    final int partition = rand.nextInt(10);
                    final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", (partition % 10));
                    this.transactionStore.addMessage(store, 1, new PutCommand("topic1", partition, ("msg" + i).getBytes(), xid, 0, 1), null);
                }
                // commit
                this.transactionStore.commit(xid, false);
            }
            // ??????roll??
            Assert.assertTrue(((this.journalStore.getCurrDataFile().getNumber()) > 1));
            Assert.assertEquals(1, this.journalStore.getDataFiles().size());
        } finally {
            MAX_FILE_SIZE = oldSize;
        }
    }

    @Test
    public void testAddManyCheckpointRecover() throws Exception {
        final Random rand = new Random();
        for (int i = 0; i < 10000; i++) {
            final LocalTransactionId xid = new LocalTransactionId("test", i);
            // ???????????
            for (int j = 0; j < ((rand.nextInt(3)) + 1); j++) {
                final int partition = rand.nextInt(10);
                final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", (partition % 10));
                this.transactionStore.addMessage(store, 1, new PutCommand("topic1", partition, ("msg" + i).getBytes(), xid, 0, 1), null);
            }
            if ((i % 100) == 0) {
                this.journalStore.checkpoint();
            }
            // commit
            this.transactionStore.commit(xid, false);
            if ((i % 77) == 0) {
                this.journalStore.checkpoint();
            }
        }
        // ????
        this.tearDown();
        final long start = System.currentTimeMillis();
        this.init(this.path);
        System.out.println(("??????????:" + ((System.currentTimeMillis()) - start)));
        for (int i = 0; i < 10; i++) {
            final MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", i);
            Assert.assertTrue(((store.getSizeInBytes()) > 0));
        }
        Assert.assertEquals(0, this.transactionStore.getActiveTransactionCount());
    }

    @Test
    public void testAddAddCommit_CloseReplayAppend_CloseRecover() throws Exception {
        final LocalTransactionId xid = new LocalTransactionId("test", 1);
        MessageStore store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg1".getBytes(), xid, 0, 1), null);
        this.transactionStore.addMessage(store, 1, new PutCommand("topic1", 2, "msg2".getBytes(), xid, 0, 2), null);
        Assert.assertNotNull(this.transactionStore.getInflyTx(xid));
        // commit
        this.transactionStore.commit(xid, false);
        store.flush();
        final long sizeInBytes = store.getSizeInBytes();
        Assert.assertTrue((sizeInBytes > 0));
        this.assertMessages(store);
        this.tearDown();
        // delete data
        System.out.println(((("?????:" + (this.path)) + (File.separator)) + (store.getDescription())));
        FileUtils.deleteDirectory(new File((((this.path) + (File.separator)) + (store.getDescription()))));
        this.init(this.path);
        // ???????????????,??????????
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        store.flush();
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
        Assert.assertEquals(sizeInBytes, store.getSizeInBytes());
        this.assertMessages(store);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
        // ????????????
        this.tearDown();
        this.init(this.path);
        store = this.messageStoreManager.getOrCreateMessageStore("topic1", 2);
        Assert.assertTrue(((store.getSizeInBytes()) > 0));
        Assert.assertEquals(sizeInBytes, store.getSizeInBytes());
        this.assertMessages(store);
        Assert.assertNull(this.transactionStore.getInflyTx(xid));
    }
}

