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
package alluxio.server.ft;


import AlluxioURI.SEPARATOR;
import CommandType.Nothing;
import CommandType.Register;
import WritePType.THROUGH;
import alluxio.AlluxioURI;
import alluxio.Constants;
import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.client.file.URIStatus;
import alluxio.collections.Pair;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.DeletePOptions;
import alluxio.grpc.RegisterWorkerPOptions;
import alluxio.master.MultiMasterLocalAlluxioCluster;
import alluxio.master.PollingMasterInquireClient;
import alluxio.master.block.BlockMaster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.util.CommonUtils;
import alluxio.util.io.PathUtils;
import alluxio.wire.WorkerNetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jersey.repackaged.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("https://alluxio.atlassian.net/browse/ALLUXIO-2818")
public class MasterFaultToleranceIntegrationTest extends BaseIntegrationTest {
    // Fail if the cluster doesn't come up after this amount of time.
    private static final int CLUSTER_WAIT_TIMEOUT_MS = 120 * (Constants.SECOND_MS);

    private static final long WORKER_CAPACITY_BYTES = 10000;

    private static final int BLOCK_SIZE = 30;

    private static final int MASTERS = 5;

    private MultiMasterLocalAlluxioCluster mMultiMasterLocalAlluxioCluster = null;

    private FileSystem mFileSystem = null;

    @Test
    public void createFileFault() throws Exception {
        int clients = 10;
        List<Pair<Long, AlluxioURI>> answer = new ArrayList<>();
        for (int k = 0; k < clients; k++) {
            faultTestDataCreation(new AlluxioURI(("/data" + k)), answer);
        }
        faultTestDataCheck(answer);
        for (int kills = 0; kills < ((MasterFaultToleranceIntegrationTest.MASTERS) - 1); kills++) {
            Assert.assertTrue(mMultiMasterLocalAlluxioCluster.stopLeader());
            mMultiMasterLocalAlluxioCluster.waitForNewMaster(MasterFaultToleranceIntegrationTest.CLUSTER_WAIT_TIMEOUT_MS);
            waitForWorkerRegistration(AlluxioBlockStore.create(FileSystemContext.create(ServerConfiguration.global())), 1, MasterFaultToleranceIntegrationTest.CLUSTER_WAIT_TIMEOUT_MS);
            faultTestDataCheck(answer);
            faultTestDataCreation(new AlluxioURI(("/data_kills_" + kills)), answer);
        }
    }

    @Test
    public void deleteFileFault() throws Exception {
        // Kill leader -> create files -> kill leader -> delete files, repeat.
        List<Pair<Long, AlluxioURI>> answer = new ArrayList<>();
        for (int kills = 0; kills < ((MasterFaultToleranceIntegrationTest.MASTERS) - 1); kills++) {
            Assert.assertTrue(mMultiMasterLocalAlluxioCluster.stopLeader());
            mMultiMasterLocalAlluxioCluster.waitForNewMaster(MasterFaultToleranceIntegrationTest.CLUSTER_WAIT_TIMEOUT_MS);
            waitForWorkerRegistration(AlluxioBlockStore.create(FileSystemContext.create(ServerConfiguration.global())), 1, MasterFaultToleranceIntegrationTest.CLUSTER_WAIT_TIMEOUT_MS);
            if ((kills % 2) != 0) {
                // Delete files.
                faultTestDataCheck(answer);
                // We can not call mFileSystem.delete(mFileSystem.open(new
                // AlluxioURI(AlluxioURI.SEPARATOR))) because root node can not be deleted.
                for (URIStatus file : mFileSystem.listStatus(new AlluxioURI(AlluxioURI.SEPARATOR))) {
                    mFileSystem.delete(new AlluxioURI(file.getPath()), DeletePOptions.newBuilder().setRecursive(true).build());
                }
                answer.clear();
                faultTestDataCheck(answer);
            } else {
                // Create files.
                Assert.assertEquals(0, answer.size());
                faultTestDataCheck(answer);
                faultTestDataCreation(new AlluxioURI(PathUtils.concatPath(SEPARATOR, ("data_" + kills))), answer);
                faultTestDataCheck(answer);
            }
        }
    }

    @Test
    public void createFiles() throws Exception {
        int clients = 10;
        CreateFilePOptions option = CreateFilePOptions.newBuilder().setBlockSizeBytes(1024).setWriteType(THROUGH).build();
        for (int k = 0; k < clients; k++) {
            mFileSystem.createFile(new AlluxioURI(((AlluxioURI.SEPARATOR) + k)), option).close();
        }
        List<String> files = FileSystemTestUtils.listFiles(mFileSystem, SEPARATOR);
        Assert.assertEquals(clients, files.size());
        Collections.sort(files);
        for (int k = 0; k < clients; k++) {
            Assert.assertEquals(((AlluxioURI.SEPARATOR) + k), files.get(k));
        }
    }

    @Test
    public void killStandby() throws Exception {
        // If standby masters are killed(or node failure), current leader should not be affected and the
        // cluster should run properly.
        int leaderIndex = mMultiMasterLocalAlluxioCluster.getLeaderIndex();
        Assert.assertNotEquals((-1), leaderIndex);
        List<Pair<Long, AlluxioURI>> answer = new ArrayList<>();
        for (int k = 0; k < 5; k++) {
            faultTestDataCreation(new AlluxioURI(("/data" + k)), answer);
        }
        faultTestDataCheck(answer);
        for (int kills = 0; kills < ((MasterFaultToleranceIntegrationTest.MASTERS) - 1); kills++) {
            Assert.assertTrue(mMultiMasterLocalAlluxioCluster.stopStandby());
            CommonUtils.sleepMs(((Constants.SECOND_MS) * 2));
            // Leader should not change.
            Assert.assertEquals(leaderIndex, mMultiMasterLocalAlluxioCluster.getLeaderIndex());
            // Cluster should still work.
            faultTestDataCheck(answer);
            faultTestDataCreation(new AlluxioURI(("/data_kills_" + kills)), answer);
        }
    }

    @Test(timeout = 30000)
    public void queryStandby() throws Exception {
        List<InetSocketAddress> addresses = mMultiMasterLocalAlluxioCluster.getMasterAddresses();
        Collections.shuffle(addresses);
        PollingMasterInquireClient inquireClient = new PollingMasterInquireClient(addresses, ServerConfiguration.global());
        Assert.assertEquals(mMultiMasterLocalAlluxioCluster.getLocalAlluxioMaster().getAddress(), inquireClient.getPrimaryRpcAddress());
    }

    @Test
    public void workerReRegister() throws Exception {
        for (int kills = 0; kills < ((MasterFaultToleranceIntegrationTest.MASTERS) - 1); kills++) {
            Assert.assertTrue(mMultiMasterLocalAlluxioCluster.stopLeader());
            mMultiMasterLocalAlluxioCluster.waitForNewMaster(MasterFaultToleranceIntegrationTest.CLUSTER_WAIT_TIMEOUT_MS);
            AlluxioBlockStore store = AlluxioBlockStore.create(FileSystemContext.create(ServerConfiguration.global()));
            waitForWorkerRegistration(store, 1, (1 * (Constants.MINUTE_MS)));
            // If worker is successfully re-registered, the capacity bytes should not change.
            long capacityFound = store.getCapacityBytes();
            Assert.assertEquals(MasterFaultToleranceIntegrationTest.WORKER_CAPACITY_BYTES, capacityFound);
        }
    }

    @Test
    public void failoverWorkerRegister() throws Exception {
        // Stop the default cluster.
        after();
        // Create a new cluster, with no workers initially
        final MultiMasterLocalAlluxioCluster cluster = new MultiMasterLocalAlluxioCluster(2, 0);
        cluster.initConfiguration();
        cluster.start();
        try {
            // Get the first block master
            BlockMaster blockMaster1 = cluster.getLocalAlluxioMaster().getMasterProcess().getMaster(BlockMaster.class);
            // Register worker 1
            long workerId1a = blockMaster1.getWorkerId(new WorkerNetAddress().setHost("host1"));
            blockMaster1.workerRegister(workerId1a, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Collections.EMPTY_MAP, Collections.EMPTY_MAP, RegisterWorkerPOptions.getDefaultInstance());
            // Register worker 2
            long workerId2a = blockMaster1.getWorkerId(new WorkerNetAddress().setHost("host2"));
            blockMaster1.workerRegister(workerId2a, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Collections.EMPTY_MAP, Collections.EMPTY_MAP, RegisterWorkerPOptions.getDefaultInstance());
            Assert.assertEquals(2, blockMaster1.getWorkerCount());
            // Worker heartbeats should return "Nothing"
            Assert.assertEquals(Nothing, blockMaster1.workerHeartbeat(workerId1a, null, Collections.EMPTY_MAP, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Lists.newArrayList()).getCommandType());
            Assert.assertEquals(Nothing, blockMaster1.workerHeartbeat(workerId2a, null, Collections.EMPTY_MAP, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Lists.newArrayList()).getCommandType());
            Assert.assertTrue(cluster.stopLeader());
            cluster.waitForNewMaster(MasterFaultToleranceIntegrationTest.CLUSTER_WAIT_TIMEOUT_MS);
            // Get the new block master, after the failover
            BlockMaster blockMaster2 = cluster.getLocalAlluxioMaster().getMasterProcess().getMaster(BlockMaster.class);
            // Worker 2 tries to heartbeat (with original id), and should get "Register" in response.
            Assert.assertEquals(Register, blockMaster2.workerHeartbeat(workerId2a, null, Collections.EMPTY_MAP, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Lists.newArrayList()).getCommandType());
            // Worker 2 re-registers (and gets a new worker id)
            long workerId2b = blockMaster2.getWorkerId(new WorkerNetAddress().setHost("host2"));
            blockMaster2.workerRegister(workerId2b, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Collections.EMPTY_MAP, Collections.EMPTY_MAP, RegisterWorkerPOptions.getDefaultInstance());
            // Worker 1 tries to heartbeat (with original id), and should get "Register" in response.
            Assert.assertEquals(Register, blockMaster2.workerHeartbeat(workerId1a, null, Collections.EMPTY_MAP, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Lists.newArrayList()).getCommandType());
            // Worker 1 re-registers (and gets a new worker id)
            long workerId1b = blockMaster2.getWorkerId(new WorkerNetAddress().setHost("host1"));
            blockMaster2.workerRegister(workerId1b, Collections.EMPTY_LIST, Collections.EMPTY_MAP, Collections.EMPTY_MAP, Collections.EMPTY_MAP, RegisterWorkerPOptions.getDefaultInstance());
        } finally {
            cluster.stop();
        }
        // Start the default cluster.
        before();
    }
}

