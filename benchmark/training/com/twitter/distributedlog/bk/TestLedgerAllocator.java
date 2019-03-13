/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.bk;


import BookKeeper.DigestType.CRC32;
import CreateMode.PERSISTENT;
import KeeperException.Code.BADVERSION;
import Phase.ERROR;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.google.common.base.Charsets;
import com.twitter.distributedlog.BookKeeperClient;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.bk.SimpleLedgerAllocator.AllocationException;
import com.twitter.distributedlog.exceptions.ZKException;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.Transaction.OpListener;
import com.twitter.distributedlog.util.Utils;
import com.twitter.distributedlog.zk.DefaultZKOp;
import com.twitter.distributedlog.zk.ZKTransaction;
import com.twitter.util.Future;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.apache.bookkeeper.client.BKException;
import org.apache.bookkeeper.client.LedgerEntry;
import org.apache.bookkeeper.client.LedgerHandle;
import org.apache.bookkeeper.versioning.Versioned;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLedgerAllocator extends TestDistributedLogBase {
    private static final Logger logger = LoggerFactory.getLogger(TestLedgerAllocator.class);

    private static final String ledgersPath = "/ledgers";

    private static final OpListener<LedgerHandle> NULL_LISTENER = new OpListener<LedgerHandle>() {
        @Override
        public void onCommit(LedgerHandle r) {
            // no-op
        }

        @Override
        public void onAbort(Throwable t) {
            // no-op
        }
    };

    @Rule
    public TestName runtime = new TestName();

    private ZooKeeperClient zkc;

    private BookKeeperClient bkc;

    private DistributedLogConfiguration dlConf = new DistributedLogConfiguration();

    @Test(timeout = 60000)
    public void testBadVersionOnTwoAllocators() throws Exception {
        String allocationPath = "/allocation-bad-version";
        zkc.get().create(allocationPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        Stat stat = new Stat();
        byte[] data = zkc.get().getData(allocationPath, false, stat);
        Versioned<byte[]> allocationData = new Versioned<byte[]>(data, new org.apache.bookkeeper.meta.ZkVersion(stat.getVersion()));
        SimpleLedgerAllocator allocator1 = new SimpleLedgerAllocator(allocationPath, allocationData, newQuorumConfigProvider(dlConf), zkc, bkc);
        SimpleLedgerAllocator allocator2 = new SimpleLedgerAllocator(allocationPath, allocationData, newQuorumConfigProvider(dlConf), zkc, bkc);
        allocator1.allocate();
        // wait until allocated
        ZKTransaction txn1 = newTxn();
        LedgerHandle lh = FutureUtils.result(allocator1.tryObtain(txn1, TestLedgerAllocator.NULL_LISTENER));
        allocator2.allocate();
        ZKTransaction txn2 = newTxn();
        try {
            FutureUtils.result(allocator2.tryObtain(txn2, TestLedgerAllocator.NULL_LISTENER));
            Assert.fail("Should fail allocating on second allocator as allocator1 is starting allocating something.");
        } catch (ZKException zke) {
            Assert.assertEquals(BADVERSION, zke.getKeeperExceptionCode());
        }
        FutureUtils.result(txn1.execute());
        Utils.close(allocator1);
        Utils.close(allocator2);
        long eid = lh.addEntry("hello world".getBytes());
        lh.close();
        LedgerHandle readLh = bkc.get().openLedger(lh.getId(), CRC32, dlConf.getBKDigestPW().getBytes());
        Enumeration<LedgerEntry> entries = readLh.readEntries(eid, eid);
        int i = 0;
        while (entries.hasMoreElements()) {
            LedgerEntry entry = entries.nextElement();
            Assert.assertEquals("hello world", new String(entry.getEntry(), Charsets.UTF_8));
            ++i;
        } 
        Assert.assertEquals(1, i);
    }

    @Test(timeout = 60000)
    public void testAllocatorWithoutEnoughBookies() throws Exception {
        String allocationPath = "/allocator-without-enough-bookies";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(TestDistributedLogBase.conf);
        confLocal.setEnsembleSize(((TestDistributedLogBase.numBookies) * 2));
        confLocal.setWriteQuorumSize(((TestDistributedLogBase.numBookies) * 2));
        SimpleLedgerAllocator allocator1 = createAllocator(allocationPath, confLocal);
        allocator1.allocate();
        ZKTransaction txn1 = newTxn();
        try {
            FutureUtils.result(allocator1.tryObtain(txn1, TestLedgerAllocator.NULL_LISTENER));
            Assert.fail("Should fail allocating ledger if there aren't enough bookies");
        } catch (AllocationException ioe) {
            // expected
            Assert.assertEquals(ERROR, ioe.getPhase());
        }
        byte[] data = zkc.get().getData(allocationPath, false, null);
        Assert.assertEquals(0, data.length);
    }

    @Test(timeout = 60000)
    public void testSuccessAllocatorShouldDeleteUnusedledger() throws Exception {
        String allocationPath = "/allocation-delete-unused-ledger";
        zkc.get().create(allocationPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        Stat stat = new Stat();
        byte[] data = zkc.get().getData(allocationPath, false, stat);
        Versioned<byte[]> allocationData = new Versioned<byte[]>(data, new org.apache.bookkeeper.meta.ZkVersion(stat.getVersion()));
        SimpleLedgerAllocator allocator1 = new SimpleLedgerAllocator(allocationPath, allocationData, newQuorumConfigProvider(dlConf), zkc, bkc);
        allocator1.allocate();
        // wait until allocated
        ZKTransaction txn1 = newTxn();
        LedgerHandle lh1 = FutureUtils.result(allocator1.tryObtain(txn1, TestLedgerAllocator.NULL_LISTENER));
        // Second allocator kicks in
        stat = new Stat();
        data = zkc.get().getData(allocationPath, false, stat);
        allocationData = new Versioned<byte[]>(data, new org.apache.bookkeeper.meta.ZkVersion(stat.getVersion()));
        SimpleLedgerAllocator allocator2 = new SimpleLedgerAllocator(allocationPath, allocationData, newQuorumConfigProvider(dlConf), zkc, bkc);
        allocator2.allocate();
        // wait until allocated
        ZKTransaction txn2 = newTxn();
        LedgerHandle lh2 = FutureUtils.result(allocator2.tryObtain(txn2, TestLedgerAllocator.NULL_LISTENER));
        // should fail to commit txn1 as version is changed by second allocator
        try {
            FutureUtils.result(txn1.execute());
            Assert.fail("Should fail commit obtaining ledger handle from first allocator as allocator is modified by second allocator.");
        } catch (ZKException ke) {
            // as expected
        }
        FutureUtils.result(txn2.execute());
        Utils.close(allocator1);
        Utils.close(allocator2);
        // ledger handle should be deleted
        try {
            lh1.close();
            Assert.fail("LedgerHandle allocated by allocator1 should be deleted.");
        } catch (BKException bke) {
            // as expected
        }
        try {
            bkc.get().openLedger(lh1.getId(), CRC32, dlConf.getBKDigestPW().getBytes());
            Assert.fail("LedgerHandle allocated by allocator1 should be deleted.");
        } catch (BKException nslee) {
            // as expected
        }
        long eid = lh2.addEntry("hello world".getBytes());
        lh2.close();
        LedgerHandle readLh = bkc.get().openLedger(lh2.getId(), CRC32, dlConf.getBKDigestPW().getBytes());
        Enumeration<LedgerEntry> entries = readLh.readEntries(eid, eid);
        int i = 0;
        while (entries.hasMoreElements()) {
            LedgerEntry entry = entries.nextElement();
            Assert.assertEquals("hello world", new String(entry.getEntry(), Charsets.UTF_8));
            ++i;
        } 
        Assert.assertEquals(1, i);
    }

    @Test(timeout = 60000)
    public void testCloseAllocatorDuringObtaining() throws Exception {
        String allocationPath = "/allocation2";
        SimpleLedgerAllocator allocator = createAllocator(allocationPath);
        allocator.allocate();
        ZKTransaction txn = newTxn();
        // close during obtaining ledger.
        LedgerHandle lh = FutureUtils.result(allocator.tryObtain(txn, TestLedgerAllocator.NULL_LISTENER));
        Utils.close(allocator);
        byte[] data = zkc.get().getData(allocationPath, false, null);
        Assert.assertEquals(((Long) (lh.getId())), Long.valueOf(new String(data, Charsets.UTF_8)));
        // the ledger is not deleted
        bkc.get().openLedger(lh.getId(), CRC32, dlConf.getBKDigestPW().getBytes(Charsets.UTF_8));
    }

    @Test(timeout = 60000)
    public void testCloseAllocatorAfterAbort() throws Exception {
        String allocationPath = "/allocation3";
        SimpleLedgerAllocator allocator = createAllocator(allocationPath);
        allocator.allocate();
        ZKTransaction txn = newTxn();
        // close during obtaining ledger.
        LedgerHandle lh = FutureUtils.result(allocator.tryObtain(txn, TestLedgerAllocator.NULL_LISTENER));
        txn.addOp(DefaultZKOp.of(Op.setData("/unexistedpath", "data".getBytes(Charsets.UTF_8), (-1))));
        try {
            FutureUtils.result(txn.execute());
            Assert.fail("Should fail the transaction when setting unexisted path");
        } catch (ZKException ke) {
            // expected
        }
        Utils.close(allocator);
        byte[] data = zkc.get().getData(allocationPath, false, null);
        Assert.assertEquals(((Long) (lh.getId())), Long.valueOf(new String(data, Charsets.UTF_8)));
        // the ledger is not deleted.
        bkc.get().openLedger(lh.getId(), CRC32, dlConf.getBKDigestPW().getBytes(Charsets.UTF_8));
    }

    @Test(timeout = 60000)
    public void testConcurrentAllocation() throws Exception {
        String allcationPath = "/" + (runtime.getMethodName());
        SimpleLedgerAllocator allocator = createAllocator(allcationPath);
        allocator.allocate();
        ZKTransaction txn1 = newTxn();
        Future<LedgerHandle> obtainFuture1 = allocator.tryObtain(txn1, TestLedgerAllocator.NULL_LISTENER);
        ZKTransaction txn2 = newTxn();
        Future<LedgerHandle> obtainFuture2 = allocator.tryObtain(txn2, TestLedgerAllocator.NULL_LISTENER);
        Assert.assertTrue(obtainFuture2.isDefined());
        Assert.assertTrue(obtainFuture2.isThrow());
        try {
            FutureUtils.result(obtainFuture2);
            Assert.fail("Should fail the concurrent obtain since there is already a transaction obtaining the ledger handle");
        } catch (SimpleLedgerAllocator cbe) {
            // expected
        }
    }

    @Test(timeout = 60000)
    public void testObtainMultipleLedgers() throws Exception {
        String allocationPath = "/" + (runtime.getMethodName());
        SimpleLedgerAllocator allocator = createAllocator(allocationPath);
        int numLedgers = 10;
        Set<LedgerHandle> allocatedLedgers = new HashSet<LedgerHandle>();
        for (int i = 0; i < numLedgers; i++) {
            allocator.allocate();
            ZKTransaction txn = newTxn();
            LedgerHandle lh = FutureUtils.result(allocator.tryObtain(txn, TestLedgerAllocator.NULL_LISTENER));
            FutureUtils.result(txn.execute());
            allocatedLedgers.add(lh);
        }
        Assert.assertEquals(numLedgers, allocatedLedgers.size());
    }
}

