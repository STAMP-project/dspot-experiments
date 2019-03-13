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
package alluxio.master.file.replication;


import Constants.KB;
import alluxio.AlluxioURI;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.RegisterWorkerPOptions;
import alluxio.job.replicate.ReplicationHandler;
import alluxio.master.block.BlockMaster;
import alluxio.master.file.contexts.CreateFileContext;
import alluxio.master.file.meta.InodeTree;
import alluxio.master.metastore.InodeStore;
import alluxio.metrics.Metric;
import alluxio.security.authorization.Mode;
import alluxio.wire.WorkerNetAddress;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.ThreadSafe;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link ReplicationChecker}.
 */
public final class ReplicationCheckerTest {
    private static final String TEST_OWNER = "user1";

    private static final String TEST_GROUP = "";

    private static final Mode TEST_MODE = new Mode(((short) (493)));

    private static final AlluxioURI TEST_FILE_1 = new AlluxioURI("/test1");

    private static final AlluxioURI TEST_FILE_2 = new AlluxioURI("/test2");

    private static final List<Long> NO_BLOCKS = ImmutableList.of();

    private static final List<Metric> NO_METRICS = ImmutableList.of();

    private static final Map<String, List<Long>> NO_BLOCKS_ON_TIERS = ImmutableMap.of();

    private static final Map<Long, Integer> EMPTY = ImmutableMap.of();

    /**
     * A mock class of AdjustReplicationHandler, used to test the output of ReplicationChecker.
     */
    @ThreadSafe
    private static class MockHandler implements ReplicationHandler {
        private final Map<Long, Integer> mEvictRequests = Maps.newHashMap();

        private final Map<Long, Integer> mReplicateRequests = Maps.newHashMap();

        @Override
        public long evict(AlluxioURI uri, long blockId, int numReplicas) {
            mEvictRequests.put(blockId, numReplicas);
            return 0;
        }

        @Override
        public long replicate(AlluxioURI uri, long blockId, int numReplicas) {
            mReplicateRequests.put(blockId, numReplicas);
            return 0;
        }

        public Map<Long, Integer> getEvictRequests() {
            return mEvictRequests;
        }

        public Map<Long, Integer> getReplicateRequests() {
            return mReplicateRequests;
        }
    }

    private InodeStore mInodeStore;

    private InodeTree mInodeTree;

    private BlockMaster mBlockMaster;

    private ReplicationChecker mReplicationChecker;

    private ReplicationCheckerTest.MockHandler mMockReplicationHandler;

    private CreateFileContext mFileContext = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(KB).setMode(ReplicationCheckerTest.TEST_MODE.toProto())).setOwner(ReplicationCheckerTest.TEST_OWNER).setGroup(ReplicationCheckerTest.TEST_GROUP);

    private Set<Long> mKnownWorkers = Sets.newHashSet();

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    @Test
    public void heartbeatWhenTreeIsEmpty() throws Exception {
        mReplicationChecker.heartbeat();
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatFileWithinRange() throws Exception {
        mFileContext.getOptions().setReplicationMin(1).setReplicationMax(3);
        long blockId = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        // One replica, meeting replication min
        addBlockLocationHelper(blockId, 1);
        mReplicationChecker.heartbeat();
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
        // Two replicas, good
        heartbeatToAddLocationHelper(blockId, createWorkerHelper(1));
        mReplicationChecker.heartbeat();
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
        // Three replicas, meeting replication max, still good
        heartbeatToAddLocationHelper(blockId, createWorkerHelper(2));
        mReplicationChecker.heartbeat();
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatFileUnderReplicatedBy1() throws Exception {
        mFileContext.getOptions().setReplicationMin(1);
        long blockId = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        mReplicationChecker.heartbeat();
        Map<Long, Integer> expected = ImmutableMap.of(blockId, 1);
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(expected, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatFileUnderReplicatedBy10() throws Exception {
        mFileContext.getOptions().setReplicationMin(10);
        long blockId = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        mReplicationChecker.heartbeat();
        Map<Long, Integer> expected = ImmutableMap.of(blockId, 10);
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(expected, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatMultipleFilesUnderReplicated() throws Exception {
        mFileContext.getOptions().setReplicationMin(1);
        long blockId1 = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        mFileContext.getOptions().setReplicationMin(2);
        long blockId2 = createBlockHelper(ReplicationCheckerTest.TEST_FILE_2, mFileContext);
        mReplicationChecker.heartbeat();
        Map<Long, Integer> expected = ImmutableMap.of(blockId1, 1, blockId2, 2);
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(expected, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatFileUnderReplicatedAndLost() throws Exception {
        mFileContext.getOptions().setReplicationMin(2);
        long blockId = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        // Create a worker.
        long workerId = mBlockMaster.getWorkerId(new WorkerNetAddress().setHost("localhost").setRpcPort(80).setDataPort(81).setWebPort(82));
        mBlockMaster.workerRegister(workerId, Collections.singletonList("MEM"), ImmutableMap.of("MEM", 100L), ImmutableMap.of("MEM", 0L), ReplicationCheckerTest.NO_BLOCKS_ON_TIERS, RegisterWorkerPOptions.getDefaultInstance());
        mBlockMaster.commitBlock(workerId, 50L, "MEM", blockId, 20L);
        // Indicate that blockId is removed on the worker.
        mBlockMaster.workerHeartbeat(workerId, null, ImmutableMap.of("MEM", 0L), ImmutableList.of(blockId), ReplicationCheckerTest.NO_BLOCKS_ON_TIERS, ReplicationCheckerTest.NO_METRICS);
        mReplicationChecker.heartbeat();
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatFileOverReplicatedBy1() throws Exception {
        mFileContext.getOptions().setReplicationMax(1);
        long blockId = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        addBlockLocationHelper(blockId, 2);
        mReplicationChecker.heartbeat();
        Map<Long, Integer> expected = ImmutableMap.of(blockId, 1);
        Assert.assertEquals(expected, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatFileOverReplicatedBy10() throws Exception {
        mFileContext.getOptions().setReplicationMax(1);
        long blockId = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        addBlockLocationHelper(blockId, 11);
        mReplicationChecker.heartbeat();
        Map<Long, Integer> expected = ImmutableMap.of(blockId, 10);
        Assert.assertEquals(expected, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatMultipleFilesOverReplicated() throws Exception {
        mFileContext.getOptions().setReplicationMax(1);
        long blockId1 = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        mFileContext.getOptions().setReplicationMax(2);
        long blockId2 = createBlockHelper(ReplicationCheckerTest.TEST_FILE_2, mFileContext);
        addBlockLocationHelper(blockId1, 2);
        addBlockLocationHelper(blockId2, 4);
        mReplicationChecker.heartbeat();
        Map<Long, Integer> expected = ImmutableMap.of(blockId1, 1, blockId2, 2);
        Assert.assertEquals(expected, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(ReplicationCheckerTest.EMPTY, mMockReplicationHandler.getReplicateRequests());
    }

    @Test
    public void heartbeatFilesUnderAndOverReplicated() throws Exception {
        mFileContext.getOptions().setReplicationMin(2).setReplicationMax((-1));
        long blockId1 = createBlockHelper(ReplicationCheckerTest.TEST_FILE_1, mFileContext);
        mFileContext.getOptions().setReplicationMin(0).setReplicationMax(3);
        long blockId2 = createBlockHelper(ReplicationCheckerTest.TEST_FILE_2, mFileContext);
        addBlockLocationHelper(blockId1, 1);
        addBlockLocationHelper(blockId2, 5);
        mReplicationChecker.heartbeat();
        Map<Long, Integer> expected1 = ImmutableMap.of(blockId1, 1);
        Map<Long, Integer> expected2 = ImmutableMap.of(blockId2, 2);
        Assert.assertEquals(expected2, mMockReplicationHandler.getEvictRequests());
        Assert.assertEquals(expected1, mMockReplicationHandler.getReplicateRequests());
    }
}

