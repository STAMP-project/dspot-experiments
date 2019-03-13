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
package alluxio.client.fs;


import AlluxioMasterProcess.Factory;
import WritePType.MUST_CACHE;
import alluxio.ConfigurationRule;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.conf.ServerConfiguration;
import alluxio.master.AlluxioMasterProcess;
import alluxio.master.TestUtils;
import alluxio.network.TieredIdentityFactory;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.worker.WorkerProcess;
import alluxio.worker.block.BlockWorker;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;


/**
 * Integration tests for functionality relating to tiered identity.
 */
public class LocalFirstPolicyIntegrationTest extends BaseIntegrationTest {
    private ExecutorService mExecutor;

    @Rule
    public ConfigurationRule mConf = new ConfigurationRule(LocalFirstPolicyIntegrationTest.conf(), ServerConfiguration.global());

    @Test
    public void test() throws Exception {
        AlluxioMasterProcess master = Factory.create();
        WorkerProcess worker1 = AlluxioWorkerProcess.Factory.create(TieredIdentityFactory.fromString("node=node1,rack=rack1", ServerConfiguration.global()));
        WorkerProcess worker2 = AlluxioWorkerProcess.Factory.create(TieredIdentityFactory.fromString("node=node2,rack=rack2", ServerConfiguration.global()));
        runProcess(mExecutor, master);
        runProcess(mExecutor, worker1);
        runProcess(mExecutor, worker2);
        TestUtils.waitForReady(master);
        TestUtils.waitForReady(worker1);
        TestUtils.waitForReady(worker2);
        FileSystem fs = FileSystem.Factory.create(ServerConfiguration.global());
        // Write to the worker in node1
        {
            Whitebox.setInternalState(TieredIdentityFactory.class, "sInstance", TieredIdentityFactory.fromString("node=node1,rack=rack1", ServerConfiguration.global()));
            try {
                FileSystemTestUtils.createByteFile(fs, "/file1", MUST_CACHE, 100);
            } finally {
                Whitebox.setInternalState(TieredIdentityFactory.class, "sInstance", ((Object) (null)));
            }
            BlockWorker blockWorker1 = worker1.getWorker(BlockWorker.class);
            BlockWorker blockWorker2 = worker2.getWorker(BlockWorker.class);
            Assert.assertEquals(100, blockWorker1.getBlockStore().getBlockStoreMeta().getUsedBytes());
            Assert.assertEquals(0, blockWorker2.getBlockStore().getBlockStoreMeta().getUsedBytes());
        }
        // Write to the worker in rack2
        {
            Whitebox.setInternalState(TieredIdentityFactory.class, "sInstance", TieredIdentityFactory.fromString("node=node3,rack=rack2", ServerConfiguration.global()));
            try {
                FileSystemTestUtils.createByteFile(fs, "/file2", MUST_CACHE, 10);
            } finally {
                Whitebox.setInternalState(TieredIdentityFactory.class, "sInstance", ((Object) (null)));
            }
            BlockWorker blockWorker1 = worker1.getWorker(BlockWorker.class);
            BlockWorker blockWorker2 = worker2.getWorker(BlockWorker.class);
            Assert.assertEquals(100, blockWorker1.getBlockStore().getBlockStoreMeta().getUsedBytes());
            Assert.assertEquals(10, blockWorker2.getBlockStore().getBlockStoreMeta().getUsedBytes());
        }
    }
}

