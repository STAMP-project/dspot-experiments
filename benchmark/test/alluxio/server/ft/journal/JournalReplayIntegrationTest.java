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


import PropertyKey.MASTER_JOURNAL_TAILER_SHUTDOWN_QUIET_WAIT_TIME_MS;
import alluxio.AlluxioURI;
import alluxio.client.file.FileSystem;
import alluxio.client.file.URIStatus;
import alluxio.grpc.DeletePOptions;
import alluxio.master.LocalAlluxioCluster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests which perform master operations, then restart master and verify its state.
 */
public final class JournalReplayIntegrationTest extends BaseIntegrationTest {
    @Rule
    public LocalAlluxioClusterResource mClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(MASTER_JOURNAL_TAILER_SHUTDOWN_QUIET_WAIT_TIME_MS, 0).setNumWorkers(0).build();

    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    private LocalAlluxioCluster mCluster;

    private FileSystem mFs;

    @Test
    public void mountDeleteMount() throws Exception {
        AlluxioURI alluxioPath = new AlluxioURI("/mnt");
        AlluxioURI ufsPath = new AlluxioURI(mFolder.newFolder().getAbsolutePath());
        mFs.mount(alluxioPath, ufsPath);
        mFs.delete(alluxioPath, DeletePOptions.newBuilder().setRecursive(true).build());
        mFs.mount(alluxioPath, ufsPath);
        mCluster.restartMasters();
        mFs = mCluster.getClient();// need new client after restart

        List<URIStatus> status = mFs.listStatus(new AlluxioURI("/"));
        Assert.assertEquals(1, status.size());
        Assert.assertTrue(status.get(0).isMountPoint());
    }
}

