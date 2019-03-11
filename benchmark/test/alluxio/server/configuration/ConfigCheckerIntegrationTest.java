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
package alluxio.server.configuration;


import ConfigStatus.FAILED;
import ConfigStatus.WARN;
import MultiProcessCluster.DeployMode.ZOOKEEPER_HA;
import PortCoordination.CONFIG_CHECKER_MULTI_MASTERS;
import PortCoordination.CONFIG_CHECKER_MULTI_NODES;
import PortCoordination.CONFIG_CHECKER_MULTI_WORKERS;
import PortCoordination.CONFIG_CHECKER_UNSET_VS_SET;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_OPTION;
import Scope.MASTER;
import alluxio.Constants;
import alluxio.conf.PropertyKey;
import alluxio.grpc.Scope;
import alluxio.multi.process.MultiProcessCluster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.wire.ConfigCheckReport;
import alluxio.wire.InconsistentProperty;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test server-side configuration checker.
 */
public class ConfigCheckerIntegrationTest extends BaseIntegrationTest {
    private static final int WAIT_TIMEOUT_MS = 60 * (Constants.SECOND_MS);

    private static final int TEST_NUM_MASTERS = 2;

    private static final int TEST_NUM_WORKERS = 2;

    public MultiProcessCluster mCluster;

    @Test
    public void multiMasters() throws Exception {
        PropertyKey key = PropertyKey.MASTER_JOURNAL_FLUSH_TIMEOUT_MS;
        Map<Integer, Map<PropertyKey, String>> masterProperties = generatePropertyWithDifferentValues(ConfigCheckerIntegrationTest.TEST_NUM_MASTERS, key);
        mCluster = MultiProcessCluster.newBuilder(CONFIG_CHECKER_MULTI_MASTERS).setClusterName("ConfigCheckerMultiMastersTest").setNumMasters(ConfigCheckerIntegrationTest.TEST_NUM_MASTERS).setNumWorkers(0).setDeployMode(ZOOKEEPER_HA).setMasterProperties(masterProperties).build();
        mCluster.start();
        ConfigCheckReport report = getReport();
        Assert.assertEquals(WARN, report.getConfigStatus());
        Assert.assertThat(report.getConfigWarns().toString(), CoreMatchers.containsString(key.getName()));
        mCluster.notifySuccess();
    }

    @Test
    public void multiWorkers() throws Exception {
        PropertyKey key = PropertyKey.WORKER_FREE_SPACE_TIMEOUT;
        Map<Integer, Map<PropertyKey, String>> workerProperties = generatePropertyWithDifferentValues(ConfigCheckerIntegrationTest.TEST_NUM_WORKERS, key);
        mCluster = MultiProcessCluster.newBuilder(CONFIG_CHECKER_MULTI_WORKERS).setClusterName("ConfigCheckerMultiWorkersTest").setNumMasters(1).setNumWorkers(ConfigCheckerIntegrationTest.TEST_NUM_WORKERS).setWorkerProperties(workerProperties).build();
        mCluster.start();
        ConfigCheckReport report = getReport();
        Assert.assertEquals(WARN, report.getConfigStatus());
        Assert.assertThat(report.getConfigWarns().toString(), CoreMatchers.containsString(key.getName()));
        mCluster.notifySuccess();
    }

    @Test
    public void multiNodes() throws Exception {
        PropertyKey key = PropertyKey.UNDERFS_LISTING_LENGTH;
        // Prepare properties
        Map<Integer, Map<PropertyKey, String>> properties = generatePropertyWithDifferentValues(((ConfigCheckerIntegrationTest.TEST_NUM_MASTERS) + (ConfigCheckerIntegrationTest.TEST_NUM_WORKERS)), key);
        Map<Integer, Map<PropertyKey, String>> masterProperties = properties.entrySet().stream().filter(( entry) -> (entry.getKey()) < (TEST_NUM_MASTERS)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Integer, Map<PropertyKey, String>> workerProperties = properties.entrySet().stream().filter(( entry) -> (entry.getKey()) >= (TEST_NUM_MASTERS)).collect(Collectors.toMap(( entry) -> (entry.getKey()) - (TEST_NUM_MASTERS), Map.Entry::getValue));
        mCluster = MultiProcessCluster.newBuilder(CONFIG_CHECKER_MULTI_NODES).setClusterName("ConfigCheckerMultiNodesTest").setNumMasters(ConfigCheckerIntegrationTest.TEST_NUM_MASTERS).setNumWorkers(ConfigCheckerIntegrationTest.TEST_NUM_WORKERS).setDeployMode(ZOOKEEPER_HA).setMasterProperties(masterProperties).setWorkerProperties(workerProperties).build();
        mCluster.start();
        ConfigCheckReport report = getReport();
        Assert.assertEquals(FAILED, report.getConfigStatus());
        Assert.assertThat(report.getConfigErrors().toString(), CoreMatchers.containsString(key.getName()));
        mCluster.notifySuccess();
    }

    @Test
    public void unsetVsSet() throws Exception {
        Map<Integer, Map<PropertyKey, String>> masterProperties = ImmutableMap.of(1, ImmutableMap.of(MASTER_MOUNT_TABLE_ROOT_OPTION, "option"));
        mCluster = MultiProcessCluster.newBuilder(CONFIG_CHECKER_UNSET_VS_SET).setClusterName("ConfigCheckerUnsetVsSet").setNumMasters(2).setNumWorkers(0).setDeployMode(DeployMode.ZOOKEEPER_HA).setMasterProperties(masterProperties).build();
        mCluster.start();
        ConfigCheckReport report = getReport();
        Map<Scope, List<InconsistentProperty>> errors = report.getConfigErrors();
        Assert.assertTrue(errors.containsKey(MASTER));
        Assert.assertEquals(1, errors.get(MASTER).size());
        InconsistentProperty property = errors.get(MASTER).get(0);
        Assert.assertEquals(MASTER_MOUNT_TABLE_ROOT_OPTION.getName(), property.getName());
        Assert.assertTrue(property.getValues().containsKey(Optional.of("option")));
        Assert.assertTrue(property.getValues().containsKey(Optional.empty()));
        mCluster.notifySuccess();
    }
}

