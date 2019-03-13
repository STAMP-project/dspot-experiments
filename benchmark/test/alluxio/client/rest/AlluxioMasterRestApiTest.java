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
package alluxio.client.rest;


import AlluxioMasterRestServiceHandler.QUERY_RAW_CONFIGURATION;
import PropertyKey.CONF_DIR;
import PropertyKey.Name.HOME;
import PropertyKey.WORKER_MEMORY_SIZE;
import RuntimeConstants.VERSION;
import ServiceType.MASTER_RPC;
import StartupConsistencyCheck.Status.COMPLETE;
import alluxio.conf.ServerConfiguration;
import alluxio.master.file.FileSystemMaster;
import alluxio.metrics.MetricsSystem;
import alluxio.testutils.master.MasterTestUtils;
import alluxio.testutils.underfs.UnderFileSystemTestUtils;
import alluxio.util.network.NetworkAddressUtils;
import alluxio.wire.Capacity;
import alluxio.wire.MountPointInfo;
import alluxio.wire.StartupConsistencyCheck;
import alluxio.wire.WorkerInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link AlluxioMasterRestServiceHandler}.
 */
public final class AlluxioMasterRestApiTest extends RestApiTest {
    private FileSystemMaster mFileSystemMaster;

    @Test
    public void getCapacity() throws Exception {
        long total = ServerConfiguration.getBytes(WORKER_MEMORY_SIZE);
        Capacity capacity = getInfo(RestApiTest.NO_PARAMS).getCapacity();
        Assert.assertEquals(total, capacity.getTotal());
        Assert.assertEquals(0, capacity.getUsed());
    }

    @Test
    public void getConfiguration() throws Exception {
        String home = "home";
        String rawConfDir = String.format("${%s}/conf", HOME);
        String resolvedConfDir = String.format("%s/conf", home);
        ServerConfiguration.set(PropertyKey.HOME, home);
        ServerConfiguration.set(CONF_DIR, rawConfDir);
        // with out any query parameter, configuration values are resolved.
        checkConfiguration(CONF_DIR, resolvedConfDir, RestApiTest.NO_PARAMS);
        // with QUERY_RAW_CONFIGURATION=false, configuration values are resolved.
        Map<String, String> params = new HashMap<>();
        params.put(QUERY_RAW_CONFIGURATION, "false");
        checkConfiguration(CONF_DIR, resolvedConfDir, params);
        // with QUERY_RAW_CONFIGURATION=true, configuration values are raw.
        params.put(QUERY_RAW_CONFIGURATION, "true");
        checkConfiguration(CONF_DIR, rawConfDir, params);
    }

    @Test
    public void getLostWorkers() throws Exception {
        List<WorkerInfo> lostWorkersInfo = getInfo(RestApiTest.NO_PARAMS).getLostWorkers();
        Assert.assertEquals(0, lostWorkersInfo.size());
    }

    @Test
    public void getMetrics() throws Exception {
        Assert.assertEquals(Long.valueOf(0), getInfo(RestApiTest.NO_PARAMS).getMetrics().get(MetricsSystem.getMetricName("CompleteFileOps")));
    }

    @Test
    public void getMountPoints() throws Exception {
        Map<String, MountPointInfo> mountTable = mFileSystemMaster.getMountTable();
        Map<String, MountPointInfo> mountPoints = getInfo(RestApiTest.NO_PARAMS).getMountPoints();
        Assert.assertEquals(mountTable.size(), mountPoints.size());
        for (Map.Entry<String, MountPointInfo> mountPoint : mountTable.entrySet()) {
            Assert.assertTrue(mountPoints.containsKey(mountPoint.getKey()));
            String expectedUri = mountPoints.get(mountPoint.getKey()).getUfsUri();
            String returnedUri = mountPoint.getValue().getUfsUri();
            Assert.assertEquals(expectedUri, returnedUri);
        }
    }

    @Test
    public void getRpcAddress() throws Exception {
        Assert.assertTrue(getInfo(RestApiTest.NO_PARAMS).getRpcAddress().contains(String.valueOf(NetworkAddressUtils.getPort(MASTER_RPC, ServerConfiguration.global()))));
    }

    @Test
    public void getStartTimeMs() throws Exception {
        Assert.assertTrue(((getInfo(RestApiTest.NO_PARAMS).getStartTimeMs()) > 0));
    }

    @Test
    public void getStartupConsistencyCheckStatus() throws Exception {
        MasterTestUtils.waitForStartupConsistencyCheck(mFileSystemMaster);
        StartupConsistencyCheck status = getInfo(RestApiTest.NO_PARAMS).getStartupConsistencyCheck();
        Assert.assertEquals(COMPLETE.toString().toLowerCase(), status.getStatus());
        Assert.assertEquals(0, status.getInconsistentUris().size());
    }

    @Test
    public void getTierCapacity() throws Exception {
        long total = ServerConfiguration.getBytes(WORKER_MEMORY_SIZE);
        Capacity capacity = getInfo(RestApiTest.NO_PARAMS).getTierCapacity().get("MEM");
        Assert.assertEquals(total, capacity.getTotal());
        Assert.assertEquals(0, capacity.getUsed());
    }

    @Test
    public void getUptimeMs() throws Exception {
        Assert.assertTrue(((getInfo(RestApiTest.NO_PARAMS).getUptimeMs()) > 0));
    }

    @Test
    public void getUfsCapacity() throws Exception {
        Capacity ufsCapacity = getInfo(RestApiTest.NO_PARAMS).getUfsCapacity();
        if (UnderFileSystemTestUtils.isObjectStorage(mFileSystemMaster.getUfsAddress())) {
            // Object storage ufs capacity is always invalid.
            Assert.assertEquals((-1), ufsCapacity.getTotal());
        } else {
            Assert.assertTrue(((ufsCapacity.getTotal()) > 0));
        }
    }

    @Test
    public void getWorkers() throws Exception {
        List<WorkerInfo> workerInfos = getInfo(RestApiTest.NO_PARAMS).getWorkers();
        Assert.assertEquals(1, workerInfos.size());
        WorkerInfo workerInfo = workerInfos.get(0);
        Assert.assertEquals(0, workerInfo.getUsedBytes());
        long bytes = ServerConfiguration.getBytes(WORKER_MEMORY_SIZE);
        Assert.assertEquals(bytes, workerInfo.getCapacityBytes());
    }

    @Test
    public void getVersion() throws Exception {
        Assert.assertEquals(VERSION, getInfo(RestApiTest.NO_PARAMS).getVersion());
    }
}

