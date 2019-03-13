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
package alluxio.server.ft.journal;


import PropertyKey.MASTER_JOURNAL_INIT_FROM_BACKUP;
import PropertyKey.MASTER_JOURNAL_TAILER_SHUTDOWN_QUIET_WAIT_TIME_MS;
import UfsPMode.READ_ONLY;
import WritePType.THROUGH;
import alluxio.AlluxioURI;
import alluxio.ClientContext;
import alluxio.client.file.FileSystemMasterClient;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.AccessControlException;
import alluxio.grpc.CreateDirectoryPOptions;
import alluxio.grpc.UpdateUfsModePOptions;
import alluxio.master.LocalAlluxioCluster;
import alluxio.master.MasterClientContext;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests checkpoints remain consistent with intended master state.
 */
public class JournalCheckpointIntegrationTest extends BaseIntegrationTest {
    @Rule
    public LocalAlluxioClusterResource mClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(MASTER_JOURNAL_TAILER_SHUTDOWN_QUIET_WAIT_TIME_MS, 0).setNumWorkers(0).build();

    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    public LocalAlluxioCluster mCluster;

    @Test
    public void recoverMounts() throws Exception {
        AlluxioURI alluxioMount1 = new AlluxioURI("/mount1");
        AlluxioURI alluxioMount2 = new AlluxioURI("/mount2");
        AlluxioURI fileMount1 = new AlluxioURI(mFolder.newFolder("1").getAbsolutePath());
        AlluxioURI fileMount2 = new AlluxioURI(mFolder.newFolder("2").getAbsolutePath());
        mCluster.getClient().mount(alluxioMount1, fileMount1);
        mCluster.getClient().mount(alluxioMount2, fileMount2);
        backupAndRestore();
        Assert.assertEquals(3, mCluster.getClient().getMountTable().size());
        mCluster.getClient().unmount(alluxioMount1);
        Assert.assertEquals(2, mCluster.getClient().getMountTable().size());
        ServerConfiguration.unset(MASTER_JOURNAL_INIT_FROM_BACKUP);
    }

    @Test
    public void recoverUfsState() throws Exception {
        FileSystemMasterClient client = new alluxio.client.file.RetryHandlingFileSystemMasterClient(MasterClientContext.newBuilder(ClientContext.create(ServerConfiguration.global())).build());
        client.updateUfsMode(new AlluxioURI(""), UpdateUfsModePOptions.newBuilder().setUfsMode(READ_ONLY).build());
        backupAndRestore();
        try {
            mCluster.getClient().createDirectory(new AlluxioURI("/test"), CreateDirectoryPOptions.newBuilder().setWriteType(THROUGH).build());
            Assert.fail("Expected an exception to be thrown");
        } catch (AccessControlException e) {
            // Expected
        }
    }
}

