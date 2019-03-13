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
package org.apache.hadoop.hdfs.server.blockmanagement;


import BMSafeModeStatus.EXTENSION;
import BMSafeModeStatus.OFF;
import BMSafeModeStatus.PENDING_THRESHOLD;
import DFSConfigKeys.DFS_NAMENODE_SAFEMODE_EXTENSION_KEY;
import com.google.common.base.Supplier;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerSafeMode.BMSafeModeStatus;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * This test is for testing {@link BlockManagerSafeMode} package local APIs.
 *
 * They use heavily mocked objects, treating the {@link BlockManagerSafeMode}
 * as white-box. Tests are light-weight thus no multi-thread scenario or real
 * mini-cluster is tested.
 *
 * @see org.apache.hadoop.hdfs.TestSafeMode
 * @see org.apache.hadoop.hdfs.server.namenode.ha.TestHASafeMode
 * @see org.apache.hadoop.hdfs.TestSafeModeWithStripedFile
 */
public class TestBlockManagerSafeMode {
    private static final int DATANODE_NUM = 3;

    private static final long BLOCK_TOTAL = 10;

    private static final double THRESHOLD = 0.99;

    private static final long BLOCK_THRESHOLD = ((long) ((TestBlockManagerSafeMode.BLOCK_TOTAL) * (TestBlockManagerSafeMode.THRESHOLD)));

    private static final int EXTENSION = 1000;// 1 second


    private FSNamesystem fsn;

    private BlockManager bm;

    private DatanodeManager dn;

    private BlockManagerSafeMode bmSafeMode;

    /**
     * Test set block total.
     *
     * The block total is set which will call checkSafeMode for the first time
     * and bmSafeMode transfers from OFF to PENDING_THRESHOLD status
     */
    @Test(timeout = 30000)
    public void testInitialize() {
        Assert.assertFalse("Block manager should not be in safe mode at beginning.", bmSafeMode.isInSafeMode());
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Assert.assertEquals(PENDING_THRESHOLD, getSafeModeStatus());
        Assert.assertTrue(bmSafeMode.isInSafeMode());
    }

    /**
     * Test the state machine transition.
     *
     * We use separate test methods instead of a single one because the mocked
     * {@link #fsn} is accessed concurrently by the current thread and the
     * <code>smmthread</code> thread in the {@link BlockManagerSafeMode}. Stubbing
     * or verification of a shared mock from different threads is NOT the proper
     * way of testing, which leads to intermittent behavior. Meanwhile, previous
     * state transition may have side effects that should be reset before next
     * test case. For example, in {@link BlockManagerSafeMode}, there is a safe
     * mode monitor thread, which is created when BlockManagerSafeMode is
     * constructed. It will start the first time the safe mode enters extension
     * stage and will stop if the safe mode leaves to OFF state. Across different
     * test cases, this thread should be reset.
     */
    @Test(timeout = 10000)
    public void testCheckSafeMode1() {
        // stays in PENDING_THRESHOLD: pending block threshold
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        setSafeModeStatus(PENDING_THRESHOLD);
        for (long i = 0; i < (TestBlockManagerSafeMode.BLOCK_THRESHOLD); i++) {
            setBlockSafe(i);
            bmSafeMode.checkSafeMode();
            Assert.assertEquals(PENDING_THRESHOLD, getSafeModeStatus());
        }
    }

    /**
     * Check safe mode transition from PENDING_THRESHOLD to EXTENSION.
     */
    @Test(timeout = 10000)
    public void testCheckSafeMode2() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", Integer.MAX_VALUE);
        setSafeModeStatus(PENDING_THRESHOLD);
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        bmSafeMode.checkSafeMode();
        Assert.assertEquals(BMSafeModeStatus.EXTENSION, getSafeModeStatus());
    }

    /**
     * Check safe mode transition from PENDING_THRESHOLD to OFF.
     */
    @Test(timeout = 10000)
    public void testCheckSafeMode3() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", 0);
        setSafeModeStatus(PENDING_THRESHOLD);
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        bmSafeMode.checkSafeMode();
        Assert.assertEquals(OFF, getSafeModeStatus());
    }

    /**
     * Check safe mode stays in EXTENSION pending threshold.
     */
    @Test(timeout = 10000)
    public void testCheckSafeMode4() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        setBlockSafe(0);
        setSafeModeStatus(BMSafeModeStatus.EXTENSION);
        Whitebox.setInternalState(bmSafeMode, "extension", 0);
        bmSafeMode.checkSafeMode();
        Assert.assertEquals(BMSafeModeStatus.EXTENSION, getSafeModeStatus());
    }

    /**
     * Check safe mode stays in EXTENSION pending extension period.
     */
    @Test(timeout = 10000)
    public void testCheckSafeMode5() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", Integer.MAX_VALUE);
        setSafeModeStatus(BMSafeModeStatus.EXTENSION);
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        bmSafeMode.checkSafeMode();
        Assert.assertEquals(BMSafeModeStatus.EXTENSION, getSafeModeStatus());
    }

    /**
     * Check it will not leave safe mode during NN transitionToActive.
     */
    @Test(timeout = 10000)
    public void testCheckSafeMode6() {
        Mockito.doReturn(true).when(fsn).inTransitionToActive();
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", 0);
        setSafeModeStatus(PENDING_THRESHOLD);
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        bmSafeMode.checkSafeMode();
        Assert.assertEquals(PENDING_THRESHOLD, getSafeModeStatus());
    }

    /**
     * Check smmthread will leave safe mode if NN is not in transitionToActive.
     */
    @Test(timeout = 10000)
    public void testCheckSafeMode7() throws Exception {
        Mockito.doReturn(false).when(fsn).inTransitionToActive();
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", 5);
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        bmSafeMode.checkSafeMode();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (getSafeModeStatus()) == (BMSafeModeStatus.OFF);
            }
        }, 100, 10000);
    }

    /**
     * Test that the block safe increases up to block threshold.
     *
     * Once the block threshold is reached, the block manger leaves safe mode and
     * increment will be a no-op.
     * The safe mode status lifecycle: OFF -> PENDING_THRESHOLD -> OFF
     */
    @Test(timeout = 30000)
    public void testIncrementSafeBlockCount() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", 0);
        for (long i = 1; i <= (TestBlockManagerSafeMode.BLOCK_TOTAL); i++) {
            BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
            Mockito.doReturn(false).when(blockInfo).isStriped();
            bmSafeMode.incrementSafeBlockCount(1, blockInfo);
            assertSafeModeIsLeftAtThreshold(i);
        }
    }

    /**
     * Test that the block safe increases up to block threshold.
     *
     * Once the block threshold is reached, the block manger leaves safe mode and
     * increment will be a no-op.
     * The safe mode status lifecycle: OFF -> PENDING_THRESHOLD -> EXTENSION-> OFF
     */
    @Test(timeout = 30000)
    public void testIncrementSafeBlockCountWithExtension() throws Exception {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        for (long i = 1; i <= (TestBlockManagerSafeMode.BLOCK_TOTAL); i++) {
            BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
            Mockito.doReturn(false).when(blockInfo).isStriped();
            bmSafeMode.incrementSafeBlockCount(1, blockInfo);
            if (i < (TestBlockManagerSafeMode.BLOCK_THRESHOLD)) {
                Assert.assertTrue(bmSafeMode.isInSafeMode());
            }
        }
        waitForExtensionPeriod();
        Assert.assertFalse(bmSafeMode.isInSafeMode());
    }

    /**
     * Test that the block safe decreases the block safe.
     *
     * The block manager stays in safe mode.
     * The safe mode status lifecycle: OFF -> PENDING_THRESHOLD
     */
    @Test(timeout = 30000)
    public void testDecrementSafeBlockCount() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", 0);
        mockBlockManagerForBlockSafeDecrement();
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        for (long i = TestBlockManagerSafeMode.BLOCK_THRESHOLD; i > 0; i--) {
            BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
            bmSafeMode.decrementSafeBlockCount(blockInfo);
            Assert.assertEquals((i - 1), getblockSafe());
            Assert.assertTrue(bmSafeMode.isInSafeMode());
        }
    }

    /**
     * Test when the block safe increment and decrement interleave.
     *
     * Both the increment and decrement will be a no-op if the safe mode is OFF.
     * The safe mode status lifecycle: OFF -> PENDING_THRESHOLD -> OFF
     */
    @Test(timeout = 30000)
    public void testIncrementAndDecrementSafeBlockCount() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", 0);
        mockBlockManagerForBlockSafeDecrement();
        for (long i = 1; i <= (TestBlockManagerSafeMode.BLOCK_TOTAL); i++) {
            BlockInfo blockInfo = Mockito.mock(BlockInfo.class);
            Mockito.doReturn(false).when(blockInfo).isStriped();
            bmSafeMode.incrementSafeBlockCount(1, blockInfo);
            bmSafeMode.decrementSafeBlockCount(blockInfo);
            bmSafeMode.incrementSafeBlockCount(1, blockInfo);
            assertSafeModeIsLeftAtThreshold(i);
        }
    }

    /**
     * Test when the block safe increment and decrement interleave
     * for striped blocks.
     *
     * Both the increment and decrement will be a no-op if the safe mode is OFF.
     * The safe mode status lifecycle: OFF -> PENDING_THRESHOLD -> OFF
     */
    @Test(timeout = 30000)
    public void testIncrementAndDecrementStripedSafeBlockCount() {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        Whitebox.setInternalState(bmSafeMode, "extension", 0);
        // this number is used only by the decrementSafeBlockCount method
        final int liveReplicasWhenDecrementing = 1;
        final short realDataBlockNum = 2;
        mockBlockManagerForStripedBlockSafeDecrement(liveReplicasWhenDecrementing);
        for (long i = 1; i <= (TestBlockManagerSafeMode.BLOCK_TOTAL); i++) {
            BlockInfoStriped blockInfo = Mockito.mock(BlockInfoStriped.class);
            Mockito.when(blockInfo.getRealDataBlockNum()).thenReturn(realDataBlockNum);
            bmSafeMode.incrementSafeBlockCount(realDataBlockNum, blockInfo);
            bmSafeMode.decrementSafeBlockCount(blockInfo);
            bmSafeMode.incrementSafeBlockCount(realDataBlockNum, blockInfo);
            assertSafeModeIsLeftAtThreshold(i);
        }
    }

    /**
     * Test the safe mode monitor.
     *
     * The monitor will make block manager leave the safe mode after  extension
     * period.
     */
    @Test(timeout = 30000)
    public void testSafeModeMonitor() throws Exception {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        // PENDING_THRESHOLD -> EXTENSION
        bmSafeMode.checkSafeMode();
        Assert.assertTrue(bmSafeMode.isInSafeMode());
        waitForExtensionPeriod();
        Assert.assertFalse(bmSafeMode.isInSafeMode());
    }

    /**
     * Test block manager won't leave safe mode if datanode threshold is not met.
     */
    @Test(timeout = 30000)
    public void testDatanodeThreshodShouldBeMet() throws Exception {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        // All datanode have not registered yet.
        Mockito.when(dn.getNumLiveDataNodes()).thenReturn(1);
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        bmSafeMode.checkSafeMode();
        Assert.assertTrue(bmSafeMode.isInSafeMode());
        // The datanode number reaches threshold after all data nodes register
        Mockito.when(dn.getNumLiveDataNodes()).thenReturn(TestBlockManagerSafeMode.DATANODE_NUM);
        bmSafeMode.checkSafeMode();
        waitForExtensionPeriod();
        Assert.assertFalse(bmSafeMode.isInSafeMode());
    }

    /**
     * Test block manager won't leave safe mode if there are blocks with
     * generation stamp (GS) in future.
     */
    @Test(timeout = 30000)
    public void testStayInSafeModeWhenBytesInFuture() throws Exception {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        // Inject blocks with future GS
        injectBlocksWithFugureGS(100L);
        Assert.assertEquals(100L, bmSafeMode.getBytesInFuture());
        // safe blocks are enough
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        // PENDING_THRESHOLD -> EXTENSION
        bmSafeMode.checkSafeMode();
        Assert.assertFalse("Shouldn't leave safe mode in case of blocks with future GS! ", bmSafeMode.leaveSafeMode(false));
        Assert.assertTrue(("Leaving safe mode forcefully should succeed regardless of " + "blocks with future GS."), bmSafeMode.leaveSafeMode(true));
        Assert.assertEquals(("Number of blocks with future GS should have been cleared " + "after leaving safe mode"), 0L, bmSafeMode.getBytesInFuture());
        Assert.assertTrue(("Leaving safe mode should succeed after blocks with future GS " + "are cleared."), bmSafeMode.leaveSafeMode(false));
    }

    @Test(timeout = 10000)
    public void testExtensionConfig() {
        final Configuration conf = new HdfsConfiguration();
        bmSafeMode = new BlockManagerSafeMode(bm, fsn, false, conf);
        Assert.assertEquals(DFSConfigKeys.DFS_NAMENODE_SAFEMODE_EXTENSION_DEFAULT, bmSafeMode.extension);
        conf.set(DFS_NAMENODE_SAFEMODE_EXTENSION_KEY, "30000");
        bmSafeMode = new BlockManagerSafeMode(bm, fsn, false, conf);
        Assert.assertEquals(30000, bmSafeMode.extension);
        conf.set(DFS_NAMENODE_SAFEMODE_EXTENSION_KEY, "20s");
        bmSafeMode = new BlockManagerSafeMode(bm, fsn, false, conf);
        Assert.assertEquals((20 * 1000), bmSafeMode.extension);
        conf.set(DFS_NAMENODE_SAFEMODE_EXTENSION_KEY, "7m");
        bmSafeMode = new BlockManagerSafeMode(bm, fsn, false, conf);
        Assert.assertEquals(((7 * 60) * 1000), bmSafeMode.extension);
    }

    /**
     * Test get safe mode tip.
     */
    @Test(timeout = 30000)
    public void testGetSafeModeTip() throws Exception {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        String tip = bmSafeMode.getSafeModeTip();
        Assert.assertTrue(tip.contains(String.format(("The reported blocks %d needs additional %d blocks to reach the " + "threshold %.4f of total blocks %d.%n"), 0, TestBlockManagerSafeMode.BLOCK_THRESHOLD, TestBlockManagerSafeMode.THRESHOLD, TestBlockManagerSafeMode.BLOCK_TOTAL)));
        Assert.assertTrue(tip.contains(String.format(("The number of live datanodes %d has reached the " + "minimum number %d. "), dn.getNumLiveDataNodes(), TestBlockManagerSafeMode.DATANODE_NUM)));
        Assert.assertTrue(tip.contains(("Safe mode will be turned off automatically once " + "the thresholds have been reached.")));
        // safe blocks are enough
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        bmSafeMode.checkSafeMode();
        tip = bmSafeMode.getSafeModeTip();
        Assert.assertTrue(tip.contains(String.format(("The reported blocks %d has reached the threshold" + " %.4f of total blocks %d. "), getblockSafe(), TestBlockManagerSafeMode.THRESHOLD, TestBlockManagerSafeMode.BLOCK_TOTAL)));
        Assert.assertTrue(tip.contains(String.format(("The number of live datanodes %d has reached the " + "minimum number %d. "), dn.getNumLiveDataNodes(), TestBlockManagerSafeMode.DATANODE_NUM)));
        Assert.assertTrue(tip.contains(("In safe mode extension. Safe mode will be turned" + " off automatically in")));
        waitForExtensionPeriod();
        tip = bmSafeMode.getSafeModeTip();
        Assert.assertTrue(tip.contains(String.format(("The reported blocks %d has reached the threshold" + " %.4f of total blocks %d. "), getblockSafe(), TestBlockManagerSafeMode.THRESHOLD, TestBlockManagerSafeMode.BLOCK_TOTAL)));
        Assert.assertTrue(tip.contains(String.format(("The number of live datanodes %d has reached the " + "minimum number %d. "), dn.getNumLiveDataNodes(), TestBlockManagerSafeMode.DATANODE_NUM)));
        Assert.assertTrue(tip.contains("Safe mode will be turned off automatically soon"));
    }

    /**
     * Test get safe mode tip in case of blocks with future GS.
     */
    @Test(timeout = 30000)
    public void testGetSafeModeTipForBlocksWithFutureGS() throws Exception {
        bmSafeMode.activate(TestBlockManagerSafeMode.BLOCK_TOTAL);
        injectBlocksWithFugureGS(40L);
        String tip = bmSafeMode.getSafeModeTip();
        Assert.assertTrue(tip.contains(String.format(("The reported blocks %d needs additional %d blocks to reach the " + "threshold %.4f of total blocks %d.%n"), 0, TestBlockManagerSafeMode.BLOCK_THRESHOLD, TestBlockManagerSafeMode.THRESHOLD, TestBlockManagerSafeMode.BLOCK_TOTAL)));
        Assert.assertTrue(tip.contains(((((((("Name node detected blocks with generation stamps " + (("in future. This means that Name node metadata is inconsistent. " + "This can happen if Name node metadata files have been manually ") + "replaced. Exiting safe mode will cause loss of ")) + 40) + " byte(s). Please restart name node with ") + "right metadata or use \"hdfs dfsadmin -safemode forceExit\" ") + "if you are certain that the NameNode was started with the ") + "correct FsImage and edit logs. If you encountered this during ") + "a rollback, it is safe to exit with -safemode forceExit.")));
        Assert.assertFalse(tip.contains("Safe mode will be turned off"));
        // blocks with future GS were already injected before.
        setBlockSafe(TestBlockManagerSafeMode.BLOCK_THRESHOLD);
        tip = bmSafeMode.getSafeModeTip();
        Assert.assertTrue(tip.contains(String.format(("The reported blocks %d has reached the threshold" + " %.4f of total blocks %d. "), getblockSafe(), TestBlockManagerSafeMode.THRESHOLD, TestBlockManagerSafeMode.BLOCK_TOTAL)));
        Assert.assertTrue(tip.contains(((((((("Name node detected blocks with generation stamps " + (("in future. This means that Name node metadata is inconsistent. " + "This can happen if Name node metadata files have been manually ") + "replaced. Exiting safe mode will cause loss of ")) + 40) + " byte(s). Please restart name node with ") + "right metadata or use \"hdfs dfsadmin -safemode forceExit\" ") + "if you are certain that the NameNode was started with the ") + "correct FsImage and edit logs. If you encountered this during ") + "a rollback, it is safe to exit with -safemode forceExit.")));
        Assert.assertFalse(tip.contains("Safe mode will be turned off"));
    }
}

