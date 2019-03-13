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


import DeployMode.EMBEDDED_HA;
import DeployMode.ZOOKEEPER_HA;
import PortCoordination.JOURNAL_MIGRATION;
import PropertyKey.MASTER_JOURNAL_INIT_FROM_BACKUP;
import PropertyKey.ZOOKEEPER_SESSION_TIMEOUT;
import alluxio.AlluxioTestDirectory;
import alluxio.AlluxioURI;
import alluxio.ClientContext;
import alluxio.client.MetaMasterClient;
import alluxio.client.file.FileSystem;
import alluxio.conf.ServerConfiguration;
import alluxio.master.MasterClientContext;
import alluxio.multi.process.MultiProcessCluster;
import alluxio.testutils.BaseIntegrationTest;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test for migrating between UFS and embedded journals.
 */
public final class JournalMigrationIntegrationTest extends BaseIntegrationTest {
    private static final int NUM_DIRS = 10;

    @Test
    public void migration() throws Exception {
        MultiProcessCluster cluster = // Masters become primary faster
        MultiProcessCluster.newBuilder(JOURNAL_MIGRATION).setClusterName("journalMigration").setDeployMode(ZOOKEEPER_HA).setNumMasters(3).addProperty(ZOOKEEPER_SESSION_TIMEOUT, "1sec").build();
        cluster.start();
        try {
            FileSystem fs = cluster.getFileSystemClient();
            MetaMasterClient metaClient = new alluxio.client.RetryHandlingMetaMasterClient(MasterClientContext.newBuilder(ClientContext.create(ServerConfiguration.global())).setMasterInquireClient(cluster.getMasterInquireClient()).build());
            for (int i = 0; i < (JournalMigrationIntegrationTest.NUM_DIRS); i++) {
                fs.createDirectory(new AlluxioURI(("/dir" + i)));
            }
            File backupsDir = AlluxioTestDirectory.createTemporaryDirectory("backups");
            AlluxioURI zkBackup = metaClient.backup(backupsDir.getAbsolutePath(), false).getBackupUri();
            cluster.updateMasterConf(MASTER_JOURNAL_INIT_FROM_BACKUP, zkBackup.toString());
            // Migrate to embedded journal HA.
            cluster.stopMasters();
            cluster.formatJournal();
            cluster.updateDeployMode(EMBEDDED_HA);
            cluster.startMasters();
            Assert.assertEquals(JournalMigrationIntegrationTest.NUM_DIRS, fs.listStatus(new AlluxioURI("/")).size());
            // Migrate back to Zookeeper HA.
            cluster.stopMasters();
            cluster.formatJournal();
            cluster.updateDeployMode(ZOOKEEPER_HA);
            cluster.startMasters();
            Assert.assertEquals(JournalMigrationIntegrationTest.NUM_DIRS, fs.listStatus(new AlluxioURI("/")).size());
            cluster.notifySuccess();
        } finally {
            cluster.destroy();
        }
    }
}

