/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.blockmanagement;


import StorageType.ARCHIVE;
import StorageType.DISK;
import StorageType.RAM_DISK;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Class for testing {@link BlockStatsMXBean} implementation
 */
public class TestBlockStatsMXBean {
    private MiniDFSCluster cluster;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    public void testStorageTypeStats() throws Exception {
        Map<StorageType, StorageTypeStats> storageTypeStatsMap = cluster.getNamesystem().getBlockManager().getStorageTypeStats();
        Assert.assertTrue(storageTypeStatsMap.containsKey(RAM_DISK));
        Assert.assertTrue(storageTypeStatsMap.containsKey(DISK));
        Assert.assertTrue(storageTypeStatsMap.containsKey(ARCHIVE));
        StorageTypeStats storageTypeStats = storageTypeStatsMap.get(RAM_DISK);
        Assert.assertEquals(6, storageTypeStats.getNodesInService());
        storageTypeStats = storageTypeStatsMap.get(DISK);
        Assert.assertEquals(3, storageTypeStats.getNodesInService());
        storageTypeStats = storageTypeStatsMap.get(ARCHIVE);
        Assert.assertEquals(3, storageTypeStats.getNodesInService());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStorageTypeStatsJMX() throws Exception {
        URL baseUrl = new URL(cluster.getHttpUri(0));
        String result = TestBlockStatsMXBean.readOutput(new URL(baseUrl, "/jmx"));
        Map<String, Object> stat = ((Map<String, Object>) (JSON.parse(result)));
        Object[] beans = ((Object[]) (stat.get("beans")));
        Map<String, Object> blockStats = null;
        for (Object bean : beans) {
            Map<String, Object> map = ((Map<String, Object>) (bean));
            if (map.get("name").equals("Hadoop:service=NameNode,name=BlockStats")) {
                blockStats = map;
            }
        }
        Assert.assertNotNull(blockStats);
        Object[] storageTypeStatsList = ((Object[]) (blockStats.get("StorageTypeStats")));
        Assert.assertNotNull(storageTypeStatsList);
        Assert.assertEquals(3, storageTypeStatsList.length);
        Set<String> typesPresent = new HashSet<>();
        for (Object obj : storageTypeStatsList) {
            Map<String, Object> entry = ((Map<String, Object>) (obj));
            String storageType = ((String) (entry.get("key")));
            Map<String, Object> storageTypeStats = ((Map<String, Object>) (entry.get("value")));
            typesPresent.add(storageType);
            if ((storageType.equals("ARCHIVE")) || (storageType.equals("DISK"))) {
                Assert.assertEquals(3L, storageTypeStats.get("nodesInService"));
            } else
                if (storageType.equals("RAM_DISK")) {
                    Assert.assertEquals(6L, storageTypeStats.get("nodesInService"));
                } else {
                    Assert.fail();
                }

        }
        Assert.assertTrue(typesPresent.contains("ARCHIVE"));
        Assert.assertTrue(typesPresent.contains("DISK"));
        Assert.assertTrue(typesPresent.contains("RAM_DISK"));
    }

    @Test
    public void testStorageTypeStatsWhenStorageFailed() throws Exception {
        // The test uses DataNodeTestUtils#injectDataDirFailure() to simulate
        // volume failures which is currently not supported on Windows.
        PlatformAssumptions.assumeNotWindows();
        DFSTestUtil.createFile(cluster.getFileSystem(), new Path("/blockStatsFile1"), 1024, ((short) (1)), 0L);
        Map<StorageType, StorageTypeStats> storageTypeStatsMap = cluster.getNamesystem().getBlockManager().getStorageTypeStats();
        StorageTypeStats storageTypeStats = storageTypeStatsMap.get(RAM_DISK);
        Assert.assertEquals(6, storageTypeStats.getNodesInService());
        storageTypeStats = storageTypeStatsMap.get(DISK);
        Assert.assertEquals(3, storageTypeStats.getNodesInService());
        storageTypeStats = storageTypeStatsMap.get(ARCHIVE);
        Assert.assertEquals(3, storageTypeStats.getNodesInService());
        File dn1ArcVol1 = cluster.getInstanceStorageDir(0, 1);
        File dn2ArcVol1 = cluster.getInstanceStorageDir(1, 1);
        File dn3ArcVol1 = cluster.getInstanceStorageDir(2, 1);
        DataNodeTestUtils.injectDataDirFailure(dn1ArcVol1);
        DataNodeTestUtils.injectDataDirFailure(dn2ArcVol1);
        DataNodeTestUtils.injectDataDirFailure(dn3ArcVol1);
        try {
            DFSTestUtil.createFile(cluster.getFileSystem(), new Path("/blockStatsFile2"), 1024, ((short) (1)), 0L);
            Assert.fail("Should throw exception, becuase no DISK storage available");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("could only be written to 0 of the 1 minReplication"));
        }
        // wait for heartbeat
        Thread.sleep(6000);
        storageTypeStatsMap = cluster.getNamesystem().getBlockManager().getStorageTypeStats();
        Assert.assertFalse("StorageTypeStatsMap should not contain DISK Storage type", storageTypeStatsMap.containsKey(DISK));
        DataNodeTestUtils.restoreDataDirFromFailure(dn1ArcVol1);
        DataNodeTestUtils.restoreDataDirFromFailure(dn2ArcVol1);
        DataNodeTestUtils.restoreDataDirFromFailure(dn3ArcVol1);
        for (int i = 0; i < 3; i++) {
            cluster.restartDataNode(0, true);
        }
        // wait for heartbeat
        Thread.sleep(6000);
        storageTypeStatsMap = cluster.getNamesystem().getBlockManager().getStorageTypeStats();
        storageTypeStats = storageTypeStatsMap.get(RAM_DISK);
        Assert.assertEquals(6, storageTypeStats.getNodesInService());
        storageTypeStats = storageTypeStatsMap.get(DISK);
        Assert.assertEquals(3, storageTypeStats.getNodesInService());
        storageTypeStats = storageTypeStatsMap.get(ARCHIVE);
        Assert.assertEquals(3, storageTypeStats.getNodesInService());
    }
}

