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
package alluxio.worker.block.meta;


import alluxio.worker.block.BlockMetadataManagerView;
import com.google.common.collect.Lists;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


/**
 * Tests for the {@link StorageDirView} class.
 */
public class StorageDirViewTest {
    private static final int TEST_TIER_LEVEL = 0;

    private static final int TEST_DIR = 0;

    private static final long TEST_SESSION_ID = 2;

    private static final long TEST_BLOCK_ID = 9;

    private static final long TEST_TEMP_BLOCK_ID = 10;

    private static final long TEST_BLOCK_SIZE = 20;

    private StorageDir mTestDir;

    private StorageDirView mTestDirView;

    private StorageTierView mTestTierView;

    private BlockMetadataManagerView mMetaManagerView;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    /**
     * Tests the {@link StorageDirView#getDirViewIndex()} method.
     */
    @Test
    public void getDirViewIndex() {
        Assert.assertEquals(mTestDir.getDirIndex(), mTestDirView.getDirViewIndex());
    }

    /**
     * Tests the {@link StorageDirView#getParentTierView()} method.
     */
    @Test
    public void getParentTierView() {
        Assert.assertEquals(mTestTierView, mTestDirView.getParentTierView());
    }

    /**
     * Tests the {@link StorageDirView#toBlockStoreLocation()} method.
     */
    @Test
    public void toBlockStoreLocation() {
        Assert.assertEquals(mTestDir.toBlockStoreLocation(), mTestDirView.toBlockStoreLocation());
    }

    /**
     * Tests the {@link StorageDirView#getCapacityBytes()} method.
     */
    @Test
    public void getCapacityBytes() {
        Assert.assertEquals(mTestDir.getCapacityBytes(), mTestDirView.getCapacityBytes());
    }

    /**
     * Tests the {@link StorageDirView#getAvailableBytes()} method.
     */
    @Test
    public void getAvailableBytes() {
        Assert.assertEquals(mTestDir.getAvailableBytes(), mTestDirView.getAvailableBytes());
    }

    /**
     * Tests the {@link StorageDirView#getCommittedBytes()} method.
     */
    @Test
    public void getCommittedBytes() {
        Assert.assertEquals(mTestDir.getCommittedBytes(), mTestDirView.getCommittedBytes());
    }

    /**
     * Tests the {@link StorageDirView#getEvictableBlocks()} method.
     */
    @Test
    public void getEvictableBlocks() throws Exception {
        // When test dir is empty, expect no block to be evictable
        Assert.assertEquals(0, mTestDirView.getEvitableBytes());
        Assert.assertTrue(mTestDirView.getEvictableBlocks().isEmpty());
        // Add one block to test dir, expect this block to be evictable
        BlockMeta blockMeta = new BlockMeta(StorageDirViewTest.TEST_BLOCK_ID, StorageDirViewTest.TEST_BLOCK_SIZE, mTestDir);
        mTestDir.addBlockMeta(blockMeta);
        Assert.assertEquals(StorageDirViewTest.TEST_BLOCK_SIZE, mTestDirView.getEvitableBytes());
        Assert.assertThat(mTestDirView.getEvictableBlocks(), CoreMatchers.is(((List<BlockMeta>) (Lists.newArrayList(blockMeta)))));
        // Lock this block, expect this block to be non-evictable
        Mockito.when(mMetaManagerView.isBlockPinned(StorageDirViewTest.TEST_BLOCK_ID)).thenReturn(false);
        Mockito.when(mMetaManagerView.isBlockLocked(StorageDirViewTest.TEST_BLOCK_ID)).thenReturn(true);
        Assert.assertEquals(0, mTestDirView.getEvitableBytes());
        Assert.assertTrue(mTestDirView.getEvictableBlocks().isEmpty());
        // Pin this block, expect this block to be non-evictable
        Mockito.when(mMetaManagerView.isBlockPinned(StorageDirViewTest.TEST_BLOCK_ID)).thenReturn(true);
        Mockito.when(mMetaManagerView.isBlockLocked(StorageDirViewTest.TEST_BLOCK_ID)).thenReturn(false);
        Assert.assertEquals(0, mTestDirView.getEvitableBytes());
        Assert.assertTrue(mTestDirView.getEvictableBlocks().isEmpty());
        // Release pin/lock, expect this block to be evictable
        Mockito.when(mMetaManagerView.isBlockPinned(StorageDirViewTest.TEST_BLOCK_ID)).thenReturn(false);
        Mockito.when(mMetaManagerView.isBlockLocked(StorageDirViewTest.TEST_BLOCK_ID)).thenReturn(false);
        Assert.assertEquals(StorageDirViewTest.TEST_BLOCK_SIZE, mTestDirView.getEvitableBytes());
        Assert.assertThat(mTestDirView.getEvictableBlocks(), CoreMatchers.is(((List<BlockMeta>) (Lists.newArrayList(blockMeta)))));
    }

    /**
     * Tests the {@link StorageDirView#createTempBlockMeta(long, long, long)} method.
     */
    @Test
    public void createTempBlockMeta() {
        TempBlockMeta tempBlockMeta = mTestDirView.createTempBlockMeta(StorageDirViewTest.TEST_SESSION_ID, StorageDirViewTest.TEST_TEMP_BLOCK_ID, StorageDirViewTest.TEST_BLOCK_SIZE);
        Assert.assertEquals(StorageDirViewTest.TEST_SESSION_ID, tempBlockMeta.getSessionId());
        Assert.assertEquals(StorageDirViewTest.TEST_TEMP_BLOCK_ID, tempBlockMeta.getBlockId());
        Assert.assertEquals(StorageDirViewTest.TEST_BLOCK_SIZE, tempBlockMeta.getBlockSize());
        Assert.assertEquals(mTestDir, tempBlockMeta.getParentDir());
    }
}

