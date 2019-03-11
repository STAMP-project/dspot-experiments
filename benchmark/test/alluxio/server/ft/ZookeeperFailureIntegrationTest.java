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


import DeployMode.ZOOKEEPER_HA;
import PortCoordination.ZOOKEEPER_FAILURE;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import PropertyKey.USER_RPC_RETRY_BASE_SLEEP_MS;
import PropertyKey.USER_RPC_RETRY_MAX_DURATION;
import PropertyKey.USER_RPC_RETRY_MAX_NUM_RETRY;
import PropertyKey.USER_RPC_RETRY_MAX_SLEEP_MS;
import alluxio.ConfigurationRule;
import alluxio.conf.ServerConfiguration;
import alluxio.multi.process.MultiProcessCluster;
import alluxio.testutils.AlluxioOperationThread;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.util.CommonUtils;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration tests for Alluxio high availability when Zookeeper has failures.
 */
public class ZookeeperFailureIntegrationTest extends BaseIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperFailureIntegrationTest.class);

    @Rule
    public ConfigurationRule mConf = new ConfigurationRule(ImmutableMap.of(USER_BLOCK_SIZE_BYTES_DEFAULT, "1000", USER_RPC_RETRY_MAX_NUM_RETRY, "5", USER_RPC_RETRY_BASE_SLEEP_MS, "500", USER_RPC_RETRY_MAX_SLEEP_MS, "500", USER_RPC_RETRY_MAX_DURATION, "2500"), ServerConfiguration.global());

    public MultiProcessCluster mCluster;

    /* This test starts alluxio in HA mode, kills Zookeeper, waits for Alluxio to fail, then restarts
    Zookeeper. Alluxio should recover when Zookeeper is restarted.
     */
    @Test
    public void zkFailure() throws Exception {
        mCluster = MultiProcessCluster.newBuilder(ZOOKEEPER_FAILURE).setClusterName("ZookeeperFailure").setDeployMode(ZOOKEEPER_HA).setNumMasters(1).setNumWorkers(1).build();
        mCluster.start();
        AlluxioOperationThread thread = new AlluxioOperationThread(mCluster.getFileSystemClient());
        thread.start();
        CommonUtils.waitFor("a successful operation to be performed", () -> (thread.successes()) > 0);
        mCluster.stopZk();
        long zkStopTime = System.currentTimeMillis();
        CommonUtils.waitFor("operations to start failing", () -> (thread.getLatestFailure()) != null);
        Assert.assertFalse(rpcServiceAvailable());
        ZookeeperFailureIntegrationTest.LOG.info("First operation failed {}ms after stopping the Zookeeper cluster", ((System.currentTimeMillis()) - zkStopTime));
        final long successes = thread.successes();
        mCluster.restartZk();
        long zkStartTime = System.currentTimeMillis();
        CommonUtils.waitFor("another successful operation to be performed", () -> (thread.successes()) > successes);
        thread.interrupt();
        thread.join();
        ZookeeperFailureIntegrationTest.LOG.info("Recovered after {}ms", ((System.currentTimeMillis()) - zkStartTime));
        mCluster.notifySuccess();
    }
}

