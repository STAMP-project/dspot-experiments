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
import PortCoordination.BACKUP_RESTORE_EMBEDDED;
import PortCoordination.BACKUP_RESTORE_SINGLE;
import PortCoordination.BACKUP_RESTORE_ZK;
import PropertyKey.USER_METRICS_COLLECTION_ENABLED;
import PropertyKey.ZOOKEEPER_SESSION_TIMEOUT;
import alluxio.ConfigurationRule;
import alluxio.conf.PropertyKey;
import alluxio.conf.ServerConfiguration;
import alluxio.multi.process.MultiProcessCluster;
import alluxio.testutils.BaseIntegrationTest;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration test for backing up and restoring alluxio master.
 */
public final class JournalBackupIntegrationTest extends BaseIntegrationTest {
    public MultiProcessCluster mCluster;

    @Rule
    public ConfigurationRule mConf = new ConfigurationRule(new HashMap<PropertyKey, String>() {
        {
            put(USER_METRICS_COLLECTION_ENABLED, "false");
        }
    }, ServerConfiguration.global());

    // This test needs to stop and start master many times, so it can take up to a minute to complete.
    @Test
    public void backupRestoreZk() throws Exception {
        mCluster = // Masters become primary faster
        MultiProcessCluster.newBuilder(BACKUP_RESTORE_ZK).setClusterName("backupRestoreZk").setDeployMode(ZOOKEEPER_HA).setNumMasters(3).addProperty(ZOOKEEPER_SESSION_TIMEOUT, "1sec").build();
        backupRestoreTest(true);
    }

    @Test
    public void backupRestoreEmbedded() throws Exception {
        mCluster = MultiProcessCluster.newBuilder(BACKUP_RESTORE_EMBEDDED).setClusterName("backupRestoreEmbedded").setDeployMode(EMBEDDED_HA).setNumMasters(3).build();
        backupRestoreTest(true);
    }

    @Test
    public void backupRestoreSingleMaster() throws Exception {
        mCluster = MultiProcessCluster.newBuilder(BACKUP_RESTORE_SINGLE).setClusterName("backupRestoreSingle").setNumMasters(1).build();
        backupRestoreTest(false);
    }
}

