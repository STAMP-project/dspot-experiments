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
package alluxio.job.replicate;


import alluxio.Constants;
import alluxio.job.JobIntegrationTest;
import alluxio.util.CommonUtils;
import alluxio.util.WaitForOptions;
import alluxio.wire.WorkerNetAddress;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link EvictDefinition}.
 */
public final class EvictIntegrationTest extends JobIntegrationTest {
    private static final String TEST_URI = "/test";

    private static final int TEST_BLOCK_SIZE = 100;

    private long mBlockId1;

    private long mBlockId2;

    private WorkerNetAddress mWorker;

    @Test
    public void evictBlock1() throws Exception {
        // run the evict job for full block mBlockId1
        waitForJobToFinish(mJobMaster.run(new EvictConfig(mBlockId1, 1)));
        CommonUtils.waitFor("block 1 to be evicted", () -> {
            try {
                return !(AdjustJobTestUtils.hasBlock(mBlockId1, mWorker, mFsContext));
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        }, WaitForOptions.defaults().setTimeoutMs((5 * (Constants.SECOND_MS))));
        // block 2 should not be evicted
        Assert.assertTrue(AdjustJobTestUtils.hasBlock(mBlockId2, mWorker, mFsContext));
    }

    @Test
    public void evictBlock2() throws Exception {
        // run the evict job for the last block mBlockId2
        waitForJobToFinish(mJobMaster.run(new EvictConfig(mBlockId2, 1)));
        CommonUtils.waitFor("block 2 to be evicted", () -> {
            try {
                return !(AdjustJobTestUtils.hasBlock(mBlockId2, mWorker, mFsContext));
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        }, WaitForOptions.defaults().setTimeoutMs((5 * (Constants.SECOND_MS))));
        // block 1 should not be evicted
        Assert.assertTrue(AdjustJobTestUtils.hasBlock(mBlockId1, mWorker, mFsContext));
    }
}

