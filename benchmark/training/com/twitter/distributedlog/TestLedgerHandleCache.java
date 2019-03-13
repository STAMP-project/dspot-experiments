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
package com.twitter.distributedlog;


import BKException.BKBookieHandleNotAvailableException;
import BKException.BKUnexpectedConditionException;
import BKException.ZKException;
import BookKeeper.DigestType.CRC32;
import LedgerHandleCache.RefCountedLedgerHandle;
import com.google.common.base.Charsets;
import com.twitter.distributedlog.util.FutureUtils;
import org.apache.bookkeeper.client.LedgerHandle;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link LedgerHandleCache}
 */
public class TestLedgerHandleCache extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestLedgerHandleCache.class);

    protected static String ledgersPath = "/ledgers";

    private ZooKeeperClient zkc;

    private BookKeeperClient bkc;

    @Test(timeout = 60000, expected = NullPointerException.class)
    public void testBuilderWithoutBKC() throws Exception {
        LedgerHandleCache.newBuilder().build();
    }

    @Test(timeout = 60000, expected = NullPointerException.class)
    public void testBuilderWithoutStatsLogger() throws Exception {
        LedgerHandleCache.newBuilder().bkc(bkc).conf(TestDistributedLogBase.conf).statsLogger(null).build();
    }

    @Test(timeout = 60000, expected = BKBookieHandleNotAvailableException.class)
    public void testOpenLedgerWhenBkcClosed() throws Exception {
        BookKeeperClient newBkc = BookKeeperClientBuilder.newBuilder().name("newBkc").zkc(zkc).ledgersPath(TestLedgerHandleCache.ledgersPath).dlConfig(TestDistributedLogBase.conf).build();
        LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(newBkc).conf(TestDistributedLogBase.conf).build();
        // closed the bkc
        newBkc.close();
        // open ledger after bkc closed.
        cache.openLedger(new LogSegmentMetadata.LogSegmentMetadataBuilder("", 2, 1, 1).setRegionId(1).build(), false);
    }

    @Test(timeout = 60000, expected = ZKException.class)
    public void testOpenLedgerWhenZkClosed() throws Exception {
        ZooKeeperClient newZkc = TestZooKeeperClientBuilder.newBuilder().name("zkc-openledger-when-zk-closed").zkServers(TestDistributedLogBase.zkServers).build();
        BookKeeperClient newBkc = BookKeeperClientBuilder.newBuilder().name("bkc-openledger-when-zk-closed").zkc(newZkc).ledgersPath(TestLedgerHandleCache.ledgersPath).dlConfig(TestDistributedLogBase.conf).build();
        try {
            LedgerHandle lh = newBkc.get().createLedger(CRC32, "zkcClosed".getBytes(Charsets.UTF_8));
            lh.close();
            newZkc.close();
            LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(newBkc).conf(TestDistributedLogBase.conf).build();
            // open ledger after zkc closed
            cache.openLedger(new LogSegmentMetadata.LogSegmentMetadataBuilder("", 2, lh.getId(), 1).setLogSegmentSequenceNo(lh.getId()).build(), false);
        } finally {
            newBkc.close();
        }
    }

    @Test(timeout = 60000, expected = BKUnexpectedConditionException.class)
    public void testReadLastConfirmedWithoutOpeningLedger() throws Exception {
        LedgerDescriptor desc = new LedgerDescriptor(9999, 9999, false);
        LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(bkc).conf(TestDistributedLogBase.conf).build();
        // read last confirmed
        cache.tryReadLastConfirmed(desc);
    }

    @Test(timeout = 60000, expected = BKUnexpectedConditionException.class)
    public void testReadEntriesWithoutOpeningLedger() throws Exception {
        LedgerDescriptor desc = new LedgerDescriptor(9999, 9999, false);
        LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(bkc).conf(TestDistributedLogBase.conf).build();
        // read entries
        cache.readEntries(desc, 0, 10);
    }

    @Test(timeout = 60000, expected = BKUnexpectedConditionException.class)
    public void testGetLastConfirmedWithoutOpeningLedger() throws Exception {
        LedgerDescriptor desc = new LedgerDescriptor(9999, 9999, false);
        LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(bkc).conf(TestDistributedLogBase.conf).build();
        // read entries
        cache.getLastAddConfirmed(desc);
    }

    @Test(timeout = 60000, expected = BKUnexpectedConditionException.class)
    public void testReadLastConfirmedAndEntryWithoutOpeningLedger() throws Exception {
        LedgerDescriptor desc = new LedgerDescriptor(9999, 9999, false);
        LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(bkc).conf(TestDistributedLogBase.conf).build();
        // read entries
        FutureUtils.bkResult(cache.asyncReadLastConfirmedAndEntry(desc, 1L, 200L, false));
    }

    @Test(timeout = 60000, expected = BKUnexpectedConditionException.class)
    public void testGetLengthWithoutOpeningLedger() throws Exception {
        LedgerDescriptor desc = new LedgerDescriptor(9999, 9999, false);
        LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(bkc).conf(TestDistributedLogBase.conf).build();
        // read entries
        cache.getLength(desc);
    }

    @Test(timeout = 60000)
    public void testOpenAndCloseLedger() throws Exception {
        LedgerHandle lh = bkc.get().createLedger(1, 1, 1, CRC32, TestDistributedLogBase.conf.getBKDigestPW().getBytes(Charsets.UTF_8));
        LedgerHandleCache cache = LedgerHandleCache.newBuilder().bkc(bkc).conf(TestDistributedLogBase.conf).build();
        LogSegmentMetadata segment = build();
        LedgerDescriptor desc1 = cache.openLedger(segment, false);
        Assert.assertTrue(cache.handlesMap.containsKey(desc1));
        LedgerHandleCache.RefCountedLedgerHandle refLh = cache.handlesMap.get(desc1);
        Assert.assertEquals(1, refLh.getRefCount());
        cache.openLedger(segment, false);
        Assert.assertTrue(cache.handlesMap.containsKey(desc1));
        Assert.assertEquals(2, refLh.getRefCount());
        // close the ledger
        cache.closeLedger(desc1);
        Assert.assertTrue(cache.handlesMap.containsKey(desc1));
        Assert.assertEquals(1, refLh.getRefCount());
        cache.closeLedger(desc1);
        Assert.assertFalse(cache.handlesMap.containsKey(desc1));
        Assert.assertEquals(0, refLh.getRefCount());
    }
}

