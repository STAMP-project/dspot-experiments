/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.core;


import java.math.BigInteger;
import org.ethereum.config.SystemProperties;
import org.ethereum.crypto.ECKey;
import org.ethereum.datasource.inmem.HashMapDB;
import org.ethereum.db.prune.Pruner;
import org.ethereum.db.prune.Segment;
import org.ethereum.util.ByteUtil;
import org.ethereum.util.blockchain.SolidityContract;
import org.ethereum.util.blockchain.StandaloneBlockchain;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Anton Nashatyrev on 05.07.2016.
 */
public class PruneTest {
    @Test
    public void testJournal1() throws Exception {
        HashMapDB<byte[]> db = new HashMapDB();
        JournalSource<byte[]> journalDB = new JournalSource(db);
        Pruner pruner = new Pruner(journalDB.getJournal(), db);
        pruner.init();
        PruneTest.put(journalDB, "11");
        PruneTest.put(journalDB, "22");
        PruneTest.put(journalDB, "33");
        pruner.feed(journalDB.commitUpdates(ByteUtil.intToBytes(1)));
        PruneTest.checkKeys(db.getStorage(), "11", "22", "33");
        PruneTest.put(journalDB, "22");
        PruneTest.delete(journalDB, "33");
        PruneTest.put(journalDB, "44");
        pruner.feed(journalDB.commitUpdates(ByteUtil.intToBytes(2)));
        PruneTest.checkKeys(db.getStorage(), "11", "22", "33", "44");
        pruner.feed(journalDB.commitUpdates(ByteUtil.intToBytes(12)));
        Segment segment = new Segment(0, ByteUtil.intToBytes(0), ByteUtil.intToBytes(0));
        segment.startTracking().addMain(1, ByteUtil.intToBytes(1), ByteUtil.intToBytes(0)).addItem(1, ByteUtil.intToBytes(2), ByteUtil.intToBytes(0)).addMain(2, ByteUtil.intToBytes(12), ByteUtil.intToBytes(1)).commit();
        pruner.prune(segment);
        PruneTest.checkKeys(db.getStorage(), "11", "22", "33");
        PruneTest.put(journalDB, "22");
        PruneTest.delete(journalDB, "33");
        PruneTest.put(journalDB, "44");
        pruner.feed(journalDB.commitUpdates(ByteUtil.intToBytes(3)));
        PruneTest.checkKeys(db.getStorage(), "11", "22", "33", "44");
        PruneTest.delete(journalDB, "22");
        PruneTest.put(journalDB, "33");
        PruneTest.delete(journalDB, "44");
        pruner.feed(journalDB.commitUpdates(ByteUtil.intToBytes(4)));
        PruneTest.checkKeys(db.getStorage(), "11", "22", "33", "44");
        segment = new Segment(0, ByteUtil.intToBytes(0), ByteUtil.intToBytes(0));
        segment.startTracking().addMain(1, ByteUtil.intToBytes(3), ByteUtil.intToBytes(0)).commit();
        pruner.prune(segment);
        PruneTest.checkKeys(db.getStorage(), "11", "22", "33", "44");
        segment = new Segment(0, ByteUtil.intToBytes(0), ByteUtil.intToBytes(0));
        segment.startTracking().addMain(1, ByteUtil.intToBytes(4), ByteUtil.intToBytes(0)).commit();
        pruner.prune(segment);
        PruneTest.checkKeys(db.getStorage(), "11", "33");
    }

    @Test
    public void simpleTest() throws Exception {
        final int pruneCount = 3;
        SystemProperties.getDefault().overrideParams("database.prune.enabled", "true", "database.prune.maxDepth", ("" + pruneCount), "mine.startNonce", "0");
        StandaloneBlockchain bc = new StandaloneBlockchain();
        ECKey alice = ECKey.fromPrivate(BigInteger.TEN);
        ECKey bob = ECKey.fromPrivate(BigInteger.ONE);
        // System.out.println("Gen root: " + Hex.toHexString(bc.getBlockchain().getBestBlock().getStateRoot()));
        bc.createBlock();
        Block b0 = bc.getBlockchain().getBestBlock();
        bc.sendEther(alice.getAddress(), convert(3, Unit.ETHER));
        Block b1_1 = bc.createBlock();
        bc.sendEther(alice.getAddress(), convert(3, Unit.ETHER));
        Block b1_2 = bc.createForkBlock(b0);
        bc.sendEther(alice.getAddress(), convert(3, Unit.ETHER));
        Block b1_3 = bc.createForkBlock(b0);
        bc.sendEther(alice.getAddress(), convert(3, Unit.ETHER));
        Block b1_4 = bc.createForkBlock(b0);
        bc.sendEther(bob.getAddress(), convert(5, Unit.ETHER));
        bc.createBlock();
        bc.sendEther(alice.getAddress(), convert(3, Unit.ETHER));
        bc.createForkBlock(b1_2);
        for (int i = 0; i < 9; i++) {
            bc.sendEther(alice.getAddress(), convert(3, Unit.ETHER));
            bc.sendEther(bob.getAddress(), convert(5, Unit.ETHER));
            bc.createBlock();
        }
        byte[][] roots = new byte[pruneCount + 1][];
        for (int i = 0; i < (pruneCount + 1); i++) {
            long bNum = (bc.getBlockchain().getBestBlock().getNumber()) - i;
            Block b = bc.getBlockchain().getBlockByNumber(bNum);
            roots[i] = b.getStateRoot();
        }
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), roots);
        long bestBlockNum = bc.getBlockchain().getBestBlock().getNumber();
        Assert.assertEquals(convert(30, Unit.ETHER), bc.getBlockchain().getRepository().getBalance(alice.getAddress()));
        Assert.assertEquals(convert(50, Unit.ETHER), bc.getBlockchain().getRepository().getBalance(bob.getAddress()));
        {
            Block b1 = bc.getBlockchain().getBlockByNumber((bestBlockNum - 1));
            Repository r1 = bc.getBlockchain().getRepository().getSnapshotTo(b1.getStateRoot());
            Assert.assertEquals(convert((3 * 9), Unit.ETHER), r1.getBalance(alice.getAddress()));
            Assert.assertEquals(convert((5 * 9), Unit.ETHER), r1.getBalance(bob.getAddress()));
        }
        {
            Block b1 = bc.getBlockchain().getBlockByNumber((bestBlockNum - 2));
            Repository r1 = bc.getBlockchain().getRepository().getSnapshotTo(b1.getStateRoot());
            Assert.assertEquals(convert((3 * 8), Unit.ETHER), r1.getBalance(alice.getAddress()));
            Assert.assertEquals(convert((5 * 8), Unit.ETHER), r1.getBalance(bob.getAddress()));
        }
        {
            Block b1 = bc.getBlockchain().getBlockByNumber((bestBlockNum - 3));
            Repository r1 = bc.getBlockchain().getRepository().getSnapshotTo(b1.getStateRoot());
            Assert.assertEquals(convert((3 * 7), Unit.ETHER), r1.getBalance(alice.getAddress()));
            Assert.assertEquals(convert((5 * 7), Unit.ETHER), r1.getBalance(bob.getAddress()));
        }
        {
            // this state should be pruned already
            Block b1 = bc.getBlockchain().getBlockByNumber((bestBlockNum - 6));
            Repository r1 = bc.getBlockchain().getRepository().getSnapshotTo(b1.getStateRoot());
            Assert.assertEquals(BigInteger.ZERO, r1.getBalance(alice.getAddress()));
            Assert.assertEquals(BigInteger.ZERO, r1.getBalance(bob.getAddress()));
        }
    }

    static HashMapDB<byte[]> stateDS;

    @Test
    public void contractTest() throws Exception {
        // checks that pruning doesn't delete the nodes which were 're-added' later
        // e.g. when a contract variable assigned value V1 the trie acquires node with key K1
        // then if the variable reassigned value V2 the trie acquires new node with key K2
        // and the node K1 is not needed anymore and added to the prune list
        // we should avoid situations when the value V1 is back, the node K1 is also back to the trie
        // but erroneously deleted later as was in the prune list
        final int pruneCount = 3;
        SystemProperties.getDefault().overrideParams("database.prune.enabled", "true", "database.prune.maxDepth", ("" + pruneCount));
        StandaloneBlockchain bc = new StandaloneBlockchain();
        SolidityContract contr = bc.submitNewContract(("contract Simple {" + (("  uint public n;" + "  function set(uint _n) { n = _n; } ") + "}")));
        bc.createBlock();
        // add/remove/add in the same block
        contr.callFunction("set", 187649984473770L);
        contr.callFunction("set", 206414982921147L);
        contr.callFunction("set", 187649984473770L);
        bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr.callConstFunction("n")[0]);
        // force prune
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr.callConstFunction("n")[0]);
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                contr.callFunction("set", 206414982921147L);
                for (int k = 0; k < j; k++) {
                    bc.createBlock();
                }
                if (j > 0)
                    Assert.assertEquals(BigInteger.valueOf(206414982921147L), contr.callConstFunction("n")[0]);

                contr.callFunction("set", 187649984473770L);
                for (int k = 0; k < i; k++) {
                    bc.createBlock();
                }
                Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr.callConstFunction("n")[0]);
            }
        }
        byte[][] roots = new byte[pruneCount + 1][];
        for (int i = 0; i < (pruneCount + 1); i++) {
            long bNum = (bc.getBlockchain().getBestBlock().getNumber()) - i;
            Block b = bc.getBlockchain().getBlockByNumber(bNum);
            roots[i] = b.getStateRoot();
        }
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), roots);
    }

    @Test
    public void twoContractsTest() throws Exception {
        final int pruneCount = 3;
        SystemProperties.getDefault().overrideParams("database.prune.enabled", "true", "database.prune.maxDepth", ("" + pruneCount));
        String src = "contract Simple {" + ((("  uint public n;" + "  function set(uint _n) { n = _n; } ") + "  function inc() { n++; } ") + "}");
        StandaloneBlockchain bc = new StandaloneBlockchain();
        Block b0 = bc.getBlockchain().getBestBlock();
        SolidityContract contr1 = bc.submitNewContract(src);
        SolidityContract contr2 = bc.submitNewContract(src);
        Block b1 = bc.createBlock();
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b1.getStateRoot(), b0.getStateRoot());
        // add/remove/add in the same block
        contr1.callFunction("set", 187649984473770L);
        contr2.callFunction("set", 187649984473770L);
        Block b2 = bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr2.callConstFunction("n")[0]);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b2.getStateRoot(), b1.getStateRoot(), b0.getStateRoot());
        contr2.callFunction("set", 206414982921147L);
        Block b3 = bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(206414982921147L), contr2.callConstFunction("n")[0]);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b3.getStateRoot(), b2.getStateRoot(), b1.getStateRoot(), b0.getStateRoot());
        // force prune
        Block b4 = bc.createBlock();
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b4.getStateRoot(), b3.getStateRoot(), b2.getStateRoot(), b1.getStateRoot());
        Block b5 = bc.createBlock();
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b5.getStateRoot(), b4.getStateRoot(), b3.getStateRoot(), b2.getStateRoot());
        Block b6 = bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(206414982921147L), contr2.callConstFunction("n")[0]);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b6.getStateRoot(), b5.getStateRoot(), b4.getStateRoot(), b3.getStateRoot());
        contr1.callFunction("set", 187649984473770L);
        contr2.callFunction("set", 187649984473770L);
        Block b7 = bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr2.callConstFunction("n")[0]);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b7.getStateRoot(), b6.getStateRoot(), b5.getStateRoot(), b4.getStateRoot());
        contr1.callFunction("set", 206414982921147L);
        Block b8 = bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(206414982921147L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr2.callConstFunction("n")[0]);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b8.getStateRoot(), b7.getStateRoot(), b6.getStateRoot(), b5.getStateRoot());
        contr2.callFunction("set", 206414982921147L);
        Block b8_ = bc.createForkBlock(b7);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b8.getStateRoot(), b8_.getStateRoot(), b7.getStateRoot(), b6.getStateRoot(), b5.getStateRoot());
        Block b9_ = bc.createForkBlock(b8_);
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(206414982921147L), contr2.callConstFunction("n")[0]);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b9_.getStateRoot(), b8.getStateRoot(), b8_.getStateRoot(), b7.getStateRoot(), b6.getStateRoot());
        Block b9 = bc.createForkBlock(b8);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b9.getStateRoot(), b9_.getStateRoot(), b8.getStateRoot(), b8_.getStateRoot(), b7.getStateRoot(), b6.getStateRoot());
        Block b10 = bc.createForkBlock(b9);
        Assert.assertEquals(BigInteger.valueOf(206414982921147L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr2.callConstFunction("n")[0]);
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b10.getStateRoot(), b9.getStateRoot(), b9_.getStateRoot(), b8.getStateRoot(), b8_.getStateRoot(), b7.getStateRoot());
        Block b11 = bc.createForkBlock(b10);
        Assert.assertEquals(BigInteger.valueOf(206414982921147L), contr1.callConstFunction("n")[0]);
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr2.callConstFunction("n")[0]);
        /* b9_.getStateRoot(), */
        checkPruning(bc.getStateDS(), bc.getPruningStateDS(), b11.getStateRoot(), b10.getStateRoot(), b9.getStateRoot(), b8.getStateRoot());
    }

    @Test
    public void branchTest() throws Exception {
        final int pruneCount = 3;
        SystemProperties.getDefault().overrideParams("database.prune.enabled", "true", "database.prune.maxDepth", ("" + pruneCount));
        StandaloneBlockchain bc = new StandaloneBlockchain();
        SolidityContract contr = bc.submitNewContract(("contract Simple {" + (("  uint public n;" + "  function set(uint _n) { n = _n; } ") + "}")));
        Block b1 = bc.createBlock();
        contr.callFunction("set", 187649984473770L);
        Block b2 = bc.createBlock();
        contr.callFunction("set", 206414982921147L);
        Block b2_ = bc.createForkBlock(b1);
        bc.createForkBlock(b2);
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        bc.createBlock();
        Assert.assertEquals(BigInteger.valueOf(187649984473770L), contr.callConstFunction("n")[0]);
    }

    @Test
    public void storagePruneTest() throws Exception {
        final int pruneCount = 3;
        SystemProperties.getDefault().overrideParams("details.inmemory.storage.limit", "200", "database.prune.enabled", "true", "database.prune.maxDepth", ("" + pruneCount));
        StandaloneBlockchain bc = new StandaloneBlockchain();
        BlockchainImpl blockchain = ((BlockchainImpl) (bc.getBlockchain()));
        // RepositoryImpl repository = (RepositoryImpl) blockchain.getRepository();
        // HashMapDB storageDS = new HashMapDB();
        // repository.getDetailsDataStore().setStorageDS(storageDS);
        SolidityContract contr = bc.submitNewContract(("contract Simple {" + (((("  uint public n;" + "  mapping(uint => uint) largeMap;") + "  function set(uint _n) { n = _n; } ") + "  function put(uint k, uint v) { largeMap[k] = v; }") + "}")));
        Block b1 = bc.createBlock();
        int entriesForExtStorage = 100;
        for (int i = 0; i < entriesForExtStorage; i++) {
            contr.callFunction("put", i, i);
            if ((i % 100) == 0)
                bc.createBlock();

        }
        bc.createBlock();
        blockchain.flush();
        contr.callFunction("put", 1000000, 1);
        bc.createBlock();
        blockchain.flush();
        for (int i = 0; i < 100; i++) {
            contr.callFunction("set", i);
            bc.createBlock();
            blockchain.flush();
            System.out.println((((bc.getStateDS().getStorage().size()) + ", ") + (bc.getStateDS().getStorage().size())));
        }
        System.out.println("Done");
    }
}

