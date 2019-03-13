/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.block.meta;


import Constants.KB;
import alluxio.Constants;
import alluxio.MasterStorageTierAssoc;
import alluxio.StorageTierAssoc;
import alluxio.wire.WorkerInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link MasterWorkerInfo}.
 */
public final class MasterWorkerInfoTest {
    private static final List<String> STORAGE_TIER_ALIASES = Lists.newArrayList("MEM", "SSD");

    private static final StorageTierAssoc GLOBAL_STORAGE_TIER_ASSOC = new MasterStorageTierAssoc(MasterWorkerInfoTest.STORAGE_TIER_ALIASES);

    private static final Map<String, Long> TOTAL_BYTES_ON_TIERS = ImmutableMap.of("MEM", ((Constants.KB) * 3L), "SSD", ((Constants.KB) * 3L));

    private static final Map<String, Long> USED_BYTES_ON_TIERS = ImmutableMap.of("MEM", ((long) (KB)), "SSD", ((long) (KB)));

    private static final Set<Long> NEW_BLOCKS = Sets.newHashSet(1L, 2L);

    private MasterWorkerInfo mInfo;

    /**
     * The exception exptected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    /**
     * Tests the {@link MasterWorkerInfo#register(StorageTierAssoc, List, Map, Map, Set)} method.
     */
    @Test
    public void register() {
        Assert.assertEquals(MasterWorkerInfoTest.NEW_BLOCKS, mInfo.getBlocks());
        Assert.assertEquals(MasterWorkerInfoTest.TOTAL_BYTES_ON_TIERS, mInfo.getTotalBytesOnTiers());
        Assert.assertEquals(((Constants.KB) * 6L), mInfo.getCapacityBytes());
        Assert.assertEquals(MasterWorkerInfoTest.USED_BYTES_ON_TIERS, mInfo.getUsedBytesOnTiers());
        Assert.assertEquals(((Constants.KB) * 2L), mInfo.getUsedBytes());
    }

    /**
     * Tests the {@link MasterWorkerInfo#getFreeBytesOnTiers()} method.
     */
    @Test
    public void getFreeBytesOnTiers() {
        Assert.assertEquals(ImmutableMap.of("MEM", ((Constants.KB) * 2L), "SSD", ((Constants.KB) * 2L)), mInfo.getFreeBytesOnTiers());
    }

    /**
     * Tests that re-registering via
     * {@link MasterWorkerInfo#register(StorageTierAssoc, List, Map, Map, Set)} works.
     */
    @Test
    public void registerAgain() {
        Set<Long> newBlocks = Sets.newHashSet(3L);
        Set<Long> removedBlocks = mInfo.register(MasterWorkerInfoTest.GLOBAL_STORAGE_TIER_ASSOC, MasterWorkerInfoTest.STORAGE_TIER_ALIASES, MasterWorkerInfoTest.TOTAL_BYTES_ON_TIERS, MasterWorkerInfoTest.USED_BYTES_ON_TIERS, newBlocks);
        Assert.assertEquals(MasterWorkerInfoTest.NEW_BLOCKS, removedBlocks);
        Assert.assertEquals(newBlocks, mInfo.getBlocks());
    }

    /**
     * Tests that an exception is thrown when trying to use the
     * {@link MasterWorkerInfo#register(StorageTierAssoc, List, Map, Map, Set)} method with a
     * different number of tiers.
     */
    @Test
    public void registerWithDifferentNumberOfTiers() {
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(("totalBytesOnTiers and usedBytesOnTiers should have the same number of" + (" tiers as storageTierAliases, but storageTierAliases has 2 tiers, while" + " totalBytesOnTiers has 2 tiers and usedBytesOnTiers has 1 tiers")));
        mInfo.register(MasterWorkerInfoTest.GLOBAL_STORAGE_TIER_ASSOC, MasterWorkerInfoTest.STORAGE_TIER_ALIASES, MasterWorkerInfoTest.TOTAL_BYTES_ON_TIERS, ImmutableMap.of("SSD", ((long) (KB))), MasterWorkerInfoTest.NEW_BLOCKS);
    }

    /**
     * Tests the {@link MasterWorkerInfo#getBlocks()} method.
     */
    @Test
    public void blockOperation() {
        // add existing block
        mInfo.addBlock(1L);
        Assert.assertEquals(MasterWorkerInfoTest.NEW_BLOCKS, mInfo.getBlocks());
        // add a new block
        mInfo.addBlock(3L);
        Assert.assertTrue(mInfo.getBlocks().contains(3L));
        // remove block
        mInfo.removeBlock(3L);
        Assert.assertFalse(mInfo.getBlocks().contains(3L));
    }

    /**
     * Tests the {@link MasterWorkerInfo#generateWorkerInfo} method.
     */
    @Test
    public void workerInfoGeneration() {
        WorkerInfo workerInfo = mInfo.generateWorkerInfo(null, true);
        Assert.assertEquals(mInfo.getId(), workerInfo.getId());
        Assert.assertEquals(mInfo.getWorkerAddress(), workerInfo.getAddress());
        Assert.assertEquals("In Service", workerInfo.getState());
        Assert.assertEquals(mInfo.getCapacityBytes(), workerInfo.getCapacityBytes());
        Assert.assertEquals(mInfo.getUsedBytes(), workerInfo.getUsedBytes());
        Assert.assertEquals(mInfo.getStartTime(), workerInfo.getStartTimeMs());
    }

    /**
     * Tests the {@link MasterWorkerInfo#updateToRemovedBlock(boolean, long)} method.
     */
    @Test
    public void updateToRemovedBlock() {
        // remove a non-existing block
        mInfo.updateToRemovedBlock(true, 10L);
        Assert.assertTrue(mInfo.getToRemoveBlocks().isEmpty());
        // remove block 1
        mInfo.updateToRemovedBlock(true, 1L);
        Assert.assertTrue(mInfo.getToRemoveBlocks().contains(1L));
        // cancel the removal
        mInfo.updateToRemovedBlock(false, 1L);
        Assert.assertTrue(mInfo.getToRemoveBlocks().isEmpty());
        // actually remove 1 for real
        mInfo.updateToRemovedBlock(true, 1L);
        mInfo.removeBlock(1L);
        Assert.assertTrue(mInfo.getToRemoveBlocks().isEmpty());
    }

    /**
     * Tests the {@link MasterWorkerInfo#updateUsedBytes(Map)} method.
     */
    @Test
    public void updateUsedBytes() {
        Assert.assertEquals(((Constants.KB) * 2L), mInfo.getUsedBytes());
        Map<String, Long> usedBytesOnTiers = ImmutableMap.of("MEM", ((Constants.KB) * 2L), "SSD", ((long) (KB)));
        mInfo.updateUsedBytes(usedBytesOnTiers);
        Assert.assertEquals(usedBytesOnTiers, mInfo.getUsedBytesOnTiers());
        Assert.assertEquals(((Constants.KB) * 3L), mInfo.getUsedBytes());
    }

    /**
     * Tests the {@link MasterWorkerInfo#updateUsedBytes(String, long)} method.
     */
    @Test
    public void updateUsedBytesInTier() {
        Assert.assertEquals(((Constants.KB) * 2L), mInfo.getUsedBytes());
        mInfo.updateUsedBytes("MEM", ((Constants.KB) * 2L));
        Assert.assertEquals(((Constants.KB) * 3L), mInfo.getUsedBytes());
        Assert.assertEquals(((Constants.KB) * 2L), ((long) (mInfo.getUsedBytesOnTiers().get("MEM"))));
    }
}

