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
package alluxio.master.meta;


import MasterMetrics.FILES_PINNED;
import MetricsSystem.METRIC_REGISTRY;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import alluxio.ConfigurationRule;
import alluxio.RuntimeConstants;
import alluxio.conf.ServerConfiguration;
import alluxio.master.AlluxioMasterProcess;
import alluxio.master.MasterRegistry;
import alluxio.master.block.BlockMaster;
import alluxio.master.file.FileSystemMaster;
import alluxio.master.metrics.MetricsMaster;
import alluxio.metrics.MetricsSystem;
import alluxio.wire.WorkerInfo;
import alluxio.wire.WorkerNetAddress;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.collect.ImmutableMap;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


/**
 * Unit tests for {@link AlluxioMasterRestServiceHandler}.
 */
public final class AlluxioMasterRestServiceHandlerTest {
    private static final WorkerNetAddress NET_ADDRESS_1 = new WorkerNetAddress().setHost("localhost").setRpcPort(80).setDataPort(81).setWebPort(82);

    private static final WorkerNetAddress NET_ADDRESS_2 = new WorkerNetAddress().setHost("localhost").setRpcPort(83).setDataPort(84).setWebPort(85);

    private static final Map<String, List<Long>> NO_BLOCKS_ON_TIERS = ImmutableMap.of();

    private static final long UFS_SPACE_TOTAL = 100L;

    private static final long UFS_SPACE_USED = 25L;

    private static final long UFS_SPACE_FREE = 75L;

    private static final String TEST_PATH = "test://test";

    private static final Map<String, Long> WORKER1_TOTAL_BYTES_ON_TIERS = ImmutableMap.of("MEM", 10L, "SSD", 20L);

    private static final Map<String, Long> WORKER2_TOTAL_BYTES_ON_TIERS = ImmutableMap.of("MEM", 1000L, "SSD", 2000L);

    private static final Map<String, Long> WORKER1_USED_BYTES_ON_TIERS = ImmutableMap.of("MEM", 1L, "SSD", 2L);

    private static final Map<String, Long> WORKER2_USED_BYTES_ON_TIERS = ImmutableMap.of("MEM", 100L, "SSD", 200L);

    private AlluxioMasterProcess mMasterProcess;

    private BlockMaster mBlockMaster;

    private FileSystemMaster mFileSystemMaster;

    private MasterRegistry mRegistry;

    private AlluxioMasterRestServiceHandler mHandler;

    private MetricsMaster mMetricsMaster;

    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(new HashMap() {
        {
            put(MASTER_MOUNT_TABLE_ROOT_UFS, AlluxioMasterRestServiceHandlerTest.TEST_PATH);
        }
    }, ServerConfiguration.global());

    @Test
    public void getConfiguration() {
        Response response = mHandler.getConfiguration();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertTrue("Entry must be a SortedMap!", ((response.getEntity()) instanceof SortedMap));
            SortedMap<String, String> entry = ((SortedMap<String, String>) (response.getEntity()));
            Assert.assertFalse("Properties Map must be not empty!", entry.isEmpty());
        } finally {
            response.close();
        }
    }

    @Test
    public void getRpcAddress() {
        Mockito.when(mMasterProcess.getRpcAddress()).thenReturn(new InetSocketAddress("localhost", 8080));
        Response response = mHandler.getRpcAddress();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a String!", String.class, response.getEntity().getClass());
            String entry = ((String) (response.getEntity()));
            Assert.assertEquals("\"localhost/127.0.0.1:8080\"", entry);
        } finally {
            response.close();
        }
    }

    @Test
    public void getMetrics() {
        final int FILES_PINNED_TEST_VALUE = 100;
        String filesPinnedProperty = MetricsSystem.getMetricName(FILES_PINNED);
        Gauge<Integer> filesPinnedGauge = () -> FILES_PINNED_TEST_VALUE;
        MetricSet mockMetricsSet = Mockito.mock(MetricSet.class);
        Map<String, Metric> map = new HashMap<>();
        map.put(filesPinnedProperty, filesPinnedGauge);
        Mockito.when(mockMetricsSet.getMetrics()).thenReturn(map);
        METRIC_REGISTRY.registerAll(mockMetricsSet);
        Response response = mHandler.getMetrics();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertTrue("Entry must be a SortedMap!", ((response.getEntity()) instanceof SortedMap));
            SortedMap<String, Long> metricsMap = ((SortedMap<String, Long>) (response.getEntity()));
            Assert.assertFalse("Metrics Map must be not empty!", metricsMap.isEmpty());
            Assert.assertTrue((("Map must contain key " + filesPinnedProperty) + "!"), metricsMap.containsKey(filesPinnedProperty));
            Assert.assertEquals(FILES_PINNED_TEST_VALUE, metricsMap.get(filesPinnedProperty).longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getStartTimeMs() {
        Mockito.when(mMasterProcess.getStartTimeMs()).thenReturn(100L);
        Response response = mHandler.getStartTimeMs();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            Assert.assertEquals(100L, entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getUptimeMs() {
        Mockito.when(mMasterProcess.getUptimeMs()).thenReturn(100L);
        Response response = mHandler.getUptimeMs();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            Assert.assertEquals(100L, entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getVersion() {
        Response response = mHandler.getVersion();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a String!", String.class, response.getEntity().getClass());
            String entry = ((String) (response.getEntity()));
            Assert.assertEquals((("\"" + (RuntimeConstants.VERSION)) + "\""), entry);
        } finally {
            response.close();
        }
    }

    @Test
    public void getCapacityBytes() {
        Response response = mHandler.getCapacityBytes();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            long sum = 0;
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER1_TOTAL_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                sum = sum + totalBytes;
            }
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER2_TOTAL_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                sum = sum + totalBytes;
            }
            Assert.assertEquals(sum, entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getUsedBytes() {
        Response response = mHandler.getUsedBytes();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            long sum = 0;
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER1_USED_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                sum = sum + totalBytes;
            }
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER2_USED_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                sum = sum + totalBytes;
            }
            Assert.assertEquals(sum, entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getFreeBytes() {
        Response response = mHandler.getFreeBytes();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            long usedSum = 0;
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER1_USED_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                usedSum = usedSum + totalBytes;
            }
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER2_USED_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                usedSum = usedSum + totalBytes;
            }
            long totalSum = 0;
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER1_TOTAL_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                totalSum = totalSum + totalBytes;
            }
            for (Map.Entry<String, Long> entry1 : AlluxioMasterRestServiceHandlerTest.WORKER2_TOTAL_BYTES_ON_TIERS.entrySet()) {
                Long totalBytes = entry1.getValue();
                totalSum = totalSum + totalBytes;
            }
            Assert.assertEquals((totalSum - usedSum), entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getUfsCapacityBytes() {
        Response response = mHandler.getUfsCapacityBytes();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            Assert.assertEquals(AlluxioMasterRestServiceHandlerTest.UFS_SPACE_TOTAL, entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getUfsUsedBytes() {
        Response response = mHandler.getUfsUsedBytes();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            Assert.assertEquals(AlluxioMasterRestServiceHandlerTest.UFS_SPACE_USED, entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getUfsFreeBytes() {
        Response response = mHandler.getUfsFreeBytes();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Long!", Long.class, response.getEntity().getClass());
            Long entry = ((Long) (response.getEntity()));
            Assert.assertEquals(AlluxioMasterRestServiceHandlerTest.UFS_SPACE_FREE, entry.longValue());
        } finally {
            response.close();
        }
    }

    @Test
    public void getWorkerCount() {
        Response response = mHandler.getWorkerCount();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertEquals("Entry must be a Integer!", Integer.class, response.getEntity().getClass());
            Integer entry = ((Integer) (response.getEntity()));
            Assert.assertEquals(Integer.valueOf(2), entry);
        } finally {
            response.close();
        }
    }

    @Test
    public void getWorkerInfoList() {
        long worker1 = mBlockMaster.getWorkerId(AlluxioMasterRestServiceHandlerTest.NET_ADDRESS_1);
        long worker2 = mBlockMaster.getWorkerId(AlluxioMasterRestServiceHandlerTest.NET_ADDRESS_2);
        Set<Long> expected = new HashSet<>();
        expected.add(worker1);
        expected.add(worker2);
        Response response = mHandler.getWorkerInfoList();
        try {
            Assert.assertNotNull("Response must be not null!", response);
            Assert.assertNotNull("Response must have a entry!", response.getEntity());
            Assert.assertTrue("Entry must be a List!", ((response.getEntity()) instanceof List));
            @SuppressWarnings("unchecked")
            List<WorkerInfo> entry = ((List<WorkerInfo>) (response.getEntity()));
            Set<Long> actual = new HashSet<>();
            for (WorkerInfo info : entry) {
                actual.add(info.getId());
            }
            Assert.assertEquals(expected, actual);
        } finally {
            response.close();
        }
    }
}

