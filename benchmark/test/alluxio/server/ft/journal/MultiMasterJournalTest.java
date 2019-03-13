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


import Constants.FILE_SYSTEM_MASTER_NAME;
import alluxio.AlluxioURI;
import alluxio.master.MultiMasterLocalAlluxioCluster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.IntegrationTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class MultiMasterJournalTest extends BaseIntegrationTest {
    private MultiMasterLocalAlluxioCluster mCluster;

    @Test
    public void testCheckpointReplay() throws Exception {
        // Trigger a checkpoint.
        for (int i = 0; i < 10; i++) {
            mCluster.getClient().createFile(new AlluxioURI(("/" + i))).close();
        }
        IntegrationTestUtils.waitForCheckpoint(FILE_SYSTEM_MASTER_NAME);
        mCluster.restartMasters();
        Assert.assertEquals("The cluster should remember the 10 files", 10, mCluster.getClient().listStatus(new AlluxioURI("/")).size());
    }
}

