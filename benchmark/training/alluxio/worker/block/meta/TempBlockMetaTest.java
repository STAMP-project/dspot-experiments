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


import alluxio.util.io.PathUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link TempBlockMeta}.
 */
public class TempBlockMetaTest {
    private static final long TEST_SESSION_ID = 2;

    private static final long TEST_BLOCK_ID = 9;

    private static final long TEST_BLOCK_SIZE = 100;

    private static final int TEST_TIER_ORDINAL = 0;

    private static final String TEST_TIER_ALIAS = "MEM";

    private static final long[] TEST_TIER_CAPACITY_BYTES = new long[]{ 100 };

    private static final String TEST_WORKER_DATA_FOLDER = "workertest";

    private String mTestDirPath;

    private String mTestBlockDirPath;

    private TempBlockMeta mTempBlockMeta;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    /**
     * Tests the {@link TempBlockMeta#getPath()} method.
     */
    @Test
    public void getPath() {
        Assert.assertEquals(PathUtils.concatPath(mTestBlockDirPath, ".tmp_blocks", ((TempBlockMetaTest.TEST_SESSION_ID) % 1024), String.format("%x-%x", TempBlockMetaTest.TEST_SESSION_ID, TempBlockMetaTest.TEST_BLOCK_ID)), mTempBlockMeta.getPath());
    }

    /**
     * Tests the {@link TempBlockMeta#getCommitPath()} method.
     */
    @Test
    public void getCommitPath() {
        Assert.assertEquals(PathUtils.concatPath(mTestBlockDirPath, TempBlockMetaTest.TEST_BLOCK_ID), mTempBlockMeta.getCommitPath());
    }

    /**
     * Tests the {@link TempBlockMeta#getSessionId()} method.
     */
    @Test
    public void getSessionId() {
        Assert.assertEquals(TempBlockMetaTest.TEST_SESSION_ID, mTempBlockMeta.getSessionId());
    }

    /**
     * Tests the {@link TempBlockMeta#setBlockSize(long)} method.
     */
    @Test
    public void setBlockSize() {
        Assert.assertEquals(TempBlockMetaTest.TEST_BLOCK_SIZE, mTempBlockMeta.getBlockSize());
        mTempBlockMeta.setBlockSize(1);
        Assert.assertEquals(1, mTempBlockMeta.getBlockSize());
        mTempBlockMeta.setBlockSize(100);
        Assert.assertEquals(100, mTempBlockMeta.getBlockSize());
    }
}

