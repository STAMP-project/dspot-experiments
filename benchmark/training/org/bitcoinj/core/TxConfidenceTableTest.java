/**
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.core;


import Threading.SAME_THREAD;
import TransactionConfidence.Factory;
import TransactionConfidence.Listener.ChangeReason.SEEN_PEERS;
import org.bitcoinj.testing.NetworkParameters;
import org.bitcoinj.testing.Transaction;
import org.junit.Assert;
import org.junit.Test;


public class TxConfidenceTableTest {
    private static final NetworkParameters UNITTEST = UnitTestParams.get();

    private Transaction tx1;

    private Transaction tx2;

    private PeerAddress address1;

    private PeerAddress address2;

    private PeerAddress address3;

    private TxConfidenceTable table;

    @Test
    public void pinHandlers() throws Exception {
        Transaction tx = TxConfidenceTableTest.UNITTEST.getDefaultSerializer().makeTransaction(tx1.bitcoinSerialize());
        Sha256Hash hash = tx.getTxId();
        table.seen(hash, address1);
        Assert.assertEquals(1, tx.getConfidence().numBroadcastPeers());
        final int[] seen = new int[1];
        tx.getConfidence().addEventListener(SAME_THREAD, new TransactionConfidence.Listener() {
            @Override
            public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                seen[0] = confidence.numBroadcastPeers();
            }
        });
        tx = null;
        System.gc();
        table.seen(hash, address2);
        Assert.assertEquals(2, seen[0]);
    }

    @Test
    public void events() throws Exception {
        final TransactionConfidence[] run = new TransactionConfidence.Listener.ChangeReason[1];
        tx1.getConfidence().addEventListener(SAME_THREAD, new TransactionConfidence.Listener() {
            @Override
            public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason) {
                run[0] = reason;
            }
        });
        table.seen(tx1.getTxId(), address1);
        Assert.assertEquals(SEEN_PEERS, run[0]);
        run[0] = null;
        table.seen(tx1.getTxId(), address1);
        Assert.assertNull(run[0]);
    }

    @Test
    public void testSeen() {
        PeerAddress peer = createMock(PeerAddress.class);
        Sha256Hash brokenHash = createMock(Sha256Hash.class);
        Sha256Hash correctHash = createMock(Sha256Hash.class);
        TransactionConfidence brokenConfidence = createMock(TransactionConfidence.class);
        expect(brokenConfidence.getTransactionHash()).andReturn(brokenHash);
        expect(brokenConfidence.markBroadcastBy(peer)).andThrow(new ArithmeticException("some error"));
        TransactionConfidence correctConfidence = createMock(TransactionConfidence.class);
        expect(correctConfidence.getTransactionHash()).andReturn(correctHash);
        expect(correctConfidence.markBroadcastBy(peer)).andReturn(true);
        correctConfidence.queueListeners(anyObject(TransactionConfidence.Listener.ChangeReason.class));
        expectLastCall();
        TransactionConfidence.Factory factory = createMock(Factory.class);
        expect(factory.createConfidence(brokenHash)).andReturn(brokenConfidence);
        expect(factory.createConfidence(correctHash)).andReturn(correctConfidence);
        replay(factory, brokenConfidence, correctConfidence);
        TxConfidenceTable table = new TxConfidenceTable(1, factory);
        try {
            table.seen(brokenHash, peer);
        } catch (ArithmeticException expected) {
            // do nothing
        }
        Assert.assertNotNull(table.seen(correctHash, peer));
    }

    @Test
    public void invAndDownload() throws Exception {
        // Base case: we see a transaction announced twice and then download it. The count is in the confidence object.
        Assert.assertEquals(0, table.numBroadcastPeers(tx1.getTxId()));
        table.seen(tx1.getTxId(), address1);
        Assert.assertEquals(1, table.numBroadcastPeers(tx1.getTxId()));
        table.seen(tx1.getTxId(), address2);
        Assert.assertEquals(2, table.numBroadcastPeers(tx1.getTxId()));
        Assert.assertEquals(2, tx2.getConfidence().numBroadcastPeers());
        // And now we see another inv.
        table.seen(tx1.getTxId(), address3);
        Assert.assertEquals(3, tx2.getConfidence().numBroadcastPeers());
        Assert.assertEquals(3, table.numBroadcastPeers(tx1.getTxId()));
    }
}

