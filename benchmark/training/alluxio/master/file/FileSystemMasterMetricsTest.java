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
package alluxio.master.file;


import MasterMetrics.FILES_PINNED;
import MasterMetrics.TOTAL_PATHS;
import MasterMetrics.UFS_CAPACITY_FREE;
import MasterMetrics.UFS_CAPACITY_TOTAL;
import MasterMetrics.UFS_CAPACITY_USED;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import UfsManager.UfsClient;
import UnderFileSystem.SpaceType.SPACE_FREE;
import UnderFileSystem.SpaceType.SPACE_TOTAL;
import UnderFileSystem.SpaceType.SPACE_USED;
import alluxio.conf.ServerConfiguration;
import alluxio.underfs.UfsManager;
import alluxio.underfs.UnderFileSystem;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for {@link DefaultFileSystemMaster.Metrics}.
 */
public class FileSystemMasterMetricsTest {
    private FileSystemMaster mFileSystemMaster;

    private UfsManager mUfsManager;

    @Test
    public void testMetricsFilesPinned() {
        Mockito.when(mFileSystemMaster.getNumberOfPinnedFiles()).thenReturn(100);
        Assert.assertEquals(100, getGauge(FILES_PINNED));
    }

    @Test
    public void testMetricsPathsTotal() {
        Mockito.when(mFileSystemMaster.getInodeCount()).thenReturn(90L);
        Assert.assertEquals(90L, getGauge(TOTAL_PATHS));
    }

    @Test
    public void testMetricsUfsCapacity() throws Exception {
        UfsManager.UfsClient client = Mockito.mock(UfsClient.class);
        UnderFileSystem ufs = Mockito.mock(UnderFileSystem.class);
        String ufsDataFolder = ServerConfiguration.get(MASTER_MOUNT_TABLE_ROOT_UFS);
        Mockito.when(ufs.getSpace(ufsDataFolder, SPACE_TOTAL)).thenReturn(1000L);
        Mockito.when(ufs.getSpace(ufsDataFolder, SPACE_USED)).thenReturn(200L);
        Mockito.when(ufs.getSpace(ufsDataFolder, SPACE_FREE)).thenReturn(800L);
        Mockito.when(client.acquireUfsResource()).thenReturn(new alluxio.resource.CloseableResource<UnderFileSystem>(ufs) {
            @Override
            public void close() {
            }
        });
        Mockito.when(mUfsManager.getRoot()).thenReturn(client);
        Assert.assertEquals(1000L, getGauge(UFS_CAPACITY_TOTAL));
        Assert.assertEquals(200L, getGauge(UFS_CAPACITY_USED));
        Assert.assertEquals(800L, getGauge(UFS_CAPACITY_FREE));
    }
}

