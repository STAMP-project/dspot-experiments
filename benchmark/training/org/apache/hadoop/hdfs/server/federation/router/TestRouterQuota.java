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
package org.apache.hadoop.hdfs.server.federation.router;


import HdfsConstants.QUOTA_RESET;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.QuotaUsage;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.DSQuotaExceededException;
import org.apache.hadoop.hdfs.protocol.NSQuotaExceededException;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.resolver.MountTableManager;
import org.apache.hadoop.hdfs.server.federation.resolver.MountTableResolver;
import org.apache.hadoop.hdfs.server.federation.store.protocol.UpdateMountTableEntryRequest;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests quota behaviors in Router-based Federation.
 */
public class TestRouterQuota {
    private static StateStoreDFSCluster cluster;

    private static MiniRouterDFSCluster.NamenodeContext nnContext1;

    private static MiniRouterDFSCluster.NamenodeContext nnContext2;

    private static MiniRouterDFSCluster.RouterContext routerContext;

    private static MountTableResolver resolver;

    private static final int BLOCK_SIZE = 512;

    @Test
    public void testNamespaceQuotaExceed() throws Exception {
        long nsQuota = 3;
        final FileSystem nnFs1 = TestRouterQuota.nnContext1.getFileSystem();
        final FileSystem nnFs2 = TestRouterQuota.nnContext2.getFileSystem();
        // Add two mount tables:
        // /nsquota --> ns0---testdir1
        // /nsquota/subdir --> ns1---testdir2
        nnFs1.mkdirs(new Path("/testdir1"));
        nnFs2.mkdirs(new Path("/testdir2"));
        MountTable mountTable1 = MountTable.newInstance("/nsquota", Collections.singletonMap("ns0", "/testdir1"));
        mountTable1.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).build());
        addMountTable(mountTable1);
        MountTable mountTable2 = MountTable.newInstance("/nsquota/subdir", Collections.singletonMap("ns1", "/testdir2"));
        mountTable2.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).build());
        addMountTable(mountTable2);
        final FileSystem routerFs = TestRouterQuota.routerContext.getFileSystem();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                boolean isNsQuotaViolated = false;
                try {
                    // create new directory to trigger NSQuotaExceededException
                    routerFs.mkdirs(new Path(("/nsquota/" + (UUID.randomUUID()))));
                    routerFs.mkdirs(new Path(("/nsquota/subdir/" + (UUID.randomUUID()))));
                } catch (NSQuotaExceededException e) {
                    isNsQuotaViolated = true;
                } catch (IOException ignored) {
                }
                return isNsQuotaViolated;
            }
        }, 5000, 60000);
        // mkdir in real FileSystem should be okay
        nnFs1.mkdirs(new Path(("/testdir1/" + (UUID.randomUUID()))));
        nnFs2.mkdirs(new Path(("/testdir2/" + (UUID.randomUUID()))));
        // delete/rename call should be still okay
        routerFs.delete(new Path("/nsquota"), true);
        routerFs.rename(new Path("/nsquota/subdir"), new Path("/nsquota/subdir"));
    }

    @Test
    public void testStorageSpaceQuotaaExceed() throws Exception {
        long ssQuota = 3071;
        final FileSystem nnFs1 = TestRouterQuota.nnContext1.getFileSystem();
        final FileSystem nnFs2 = TestRouterQuota.nnContext2.getFileSystem();
        // Add two mount tables:
        // /ssquota --> ns0---testdir3
        // /ssquota/subdir --> ns1---testdir4
        nnFs1.mkdirs(new Path("/testdir3"));
        nnFs2.mkdirs(new Path("/testdir4"));
        MountTable mountTable1 = MountTable.newInstance("/ssquota", Collections.singletonMap("ns0", "/testdir3"));
        mountTable1.setQuota(new RouterQuotaUsage.Builder().spaceQuota(ssQuota).build());
        addMountTable(mountTable1);
        MountTable mountTable2 = MountTable.newInstance("/ssquota/subdir", Collections.singletonMap("ns1", "/testdir4"));
        mountTable2.setQuota(new RouterQuotaUsage.Builder().spaceQuota(ssQuota).build());
        addMountTable(mountTable2);
        DFSClient routerClient = TestRouterQuota.routerContext.getClient();
        routerClient.create("/ssquota/file", true).close();
        routerClient.create("/ssquota/subdir/file", true).close();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                boolean isDsQuotaViolated = false;
                try {
                    // append data to trigger NSQuotaExceededException
                    appendData("/ssquota/file", routerClient, TestRouterQuota.BLOCK_SIZE);
                    appendData("/ssquota/subdir/file", routerClient, TestRouterQuota.BLOCK_SIZE);
                } catch (DSQuotaExceededException e) {
                    isDsQuotaViolated = true;
                } catch (IOException ignored) {
                }
                return isDsQuotaViolated;
            }
        }, 5000, 60000);
        // append data to destination path in real FileSystem should be okay
        appendData("/testdir3/file", TestRouterQuota.nnContext1.getClient(), TestRouterQuota.BLOCK_SIZE);
        appendData("/testdir4/file", TestRouterQuota.nnContext2.getClient(), TestRouterQuota.BLOCK_SIZE);
    }

    @Test
    public void testSetQuota() throws Exception {
        long nsQuota = 5;
        long ssQuota = 100;
        final FileSystem nnFs1 = TestRouterQuota.nnContext1.getFileSystem();
        final FileSystem nnFs2 = TestRouterQuota.nnContext2.getFileSystem();
        // Add two mount tables:
        // /setquota --> ns0---testdir5
        // /setquota/subdir --> ns1---testdir6
        nnFs1.mkdirs(new Path("/testdir5"));
        nnFs2.mkdirs(new Path("/testdir6"));
        MountTable mountTable1 = MountTable.newInstance("/setquota", Collections.singletonMap("ns0", "/testdir5"));
        mountTable1.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable1);
        // don't set quota for subpath of mount table
        MountTable mountTable2 = MountTable.newInstance("/setquota/subdir", Collections.singletonMap("ns1", "/testdir6"));
        addMountTable(mountTable2);
        RouterQuotaUpdateService updateService = TestRouterQuota.routerContext.getRouter().getQuotaCacheUpdateService();
        // ensure setQuota RPC call was invoked
        updateService.periodicInvoke();
        ClientProtocol client1 = TestRouterQuota.nnContext1.getClient().getNamenode();
        ClientProtocol client2 = TestRouterQuota.nnContext2.getClient().getNamenode();
        final QuotaUsage quota1 = client1.getQuotaUsage("/testdir5");
        final QuotaUsage quota2 = client2.getQuotaUsage("/testdir6");
        Assert.assertEquals(nsQuota, quota1.getQuota());
        Assert.assertEquals(ssQuota, quota1.getSpaceQuota());
        Assert.assertEquals(nsQuota, quota2.getQuota());
        Assert.assertEquals(ssQuota, quota2.getSpaceQuota());
    }

    @Test
    public void testGetQuota() throws Exception {
        long nsQuota = 10;
        long ssQuota = 100;
        final FileSystem nnFs1 = TestRouterQuota.nnContext1.getFileSystem();
        final FileSystem nnFs2 = TestRouterQuota.nnContext2.getFileSystem();
        // Add two mount tables:
        // /getquota --> ns0---/testdir7
        // /getquota/subdir1 --> ns0---/testdir7/subdir
        // /getquota/subdir2 --> ns1---/testdir8
        nnFs1.mkdirs(new Path("/testdir7"));
        nnFs1.mkdirs(new Path("/testdir7/subdir"));
        nnFs2.mkdirs(new Path("/testdir8"));
        MountTable mountTable1 = MountTable.newInstance("/getquota", Collections.singletonMap("ns0", "/testdir7"));
        mountTable1.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable1);
        MountTable mountTable2 = MountTable.newInstance("/getquota/subdir1", Collections.singletonMap("ns0", "/testdir7/subdir"));
        addMountTable(mountTable2);
        MountTable mountTable3 = MountTable.newInstance("/getquota/subdir2", Collections.singletonMap("ns1", "/testdir8"));
        addMountTable(mountTable3);
        // use router client to create new files
        DFSClient routerClient = TestRouterQuota.routerContext.getClient();
        routerClient.create("/getquota/file", true).close();
        routerClient.create("/getquota/subdir1/file", true).close();
        routerClient.create("/getquota/subdir2/file", true).close();
        ClientProtocol clientProtocol = TestRouterQuota.routerContext.getClient().getNamenode();
        RouterQuotaUpdateService updateService = TestRouterQuota.routerContext.getRouter().getQuotaCacheUpdateService();
        updateService.periodicInvoke();
        final QuotaUsage quota = clientProtocol.getQuotaUsage("/getquota");
        // the quota should be aggregated
        Assert.assertEquals(6, quota.getFileAndDirectoryCount());
    }

    @Test
    public void testStaleQuotaRemoving() throws Exception {
        long nsQuota = 20;
        long ssQuota = 200;
        String stalePath = "/stalequota";
        final FileSystem nnFs1 = TestRouterQuota.nnContext1.getFileSystem();
        // Add one mount tables:
        // /stalequota --> ns0---/testdir9
        nnFs1.mkdirs(new Path("/testdir9"));
        MountTable mountTable = MountTable.newInstance(stalePath, Collections.singletonMap("ns0", "/testdir9"));
        mountTable.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable);
        // Call periodicInvoke to ensure quota for stalePath was
        // loaded into quota manager.
        RouterQuotaUpdateService updateService = TestRouterQuota.routerContext.getRouter().getQuotaCacheUpdateService();
        updateService.periodicInvoke();
        // use quota manager to get its quota usage and do verification
        RouterQuotaManager quotaManager = TestRouterQuota.routerContext.getRouter().getQuotaManager();
        RouterQuotaUsage quota = quotaManager.getQuotaUsage(stalePath);
        Assert.assertEquals(nsQuota, quota.getQuota());
        Assert.assertEquals(ssQuota, quota.getSpaceQuota());
        // remove stale path entry
        removeMountTable(stalePath);
        updateService.periodicInvoke();
        // the stale entry should be removed and we will get null
        quota = quotaManager.getQuotaUsage(stalePath);
        Assert.assertNull(quota);
    }

    @Test
    public void testQuotaUpdating() throws Exception {
        long nsQuota = 30;
        long ssQuota = 1024;
        String path = "/updatequota";
        final FileSystem nnFs1 = TestRouterQuota.nnContext1.getFileSystem();
        // Add one mount table:
        // /updatequota --> ns0---/testdir10
        nnFs1.mkdirs(new Path("/testdir10"));
        MountTable mountTable = MountTable.newInstance(path, Collections.singletonMap("ns0", "/testdir10"));
        mountTable.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable);
        // Call periodicInvoke to ensure quota  updated in quota manager
        // and state store.
        RouterQuotaUpdateService updateService = TestRouterQuota.routerContext.getRouter().getQuotaCacheUpdateService();
        updateService.periodicInvoke();
        // verify initial quota value
        MountTable updatedMountTable = getMountTable(path);
        RouterQuotaUsage quota = updatedMountTable.getQuota();
        Assert.assertEquals(nsQuota, quota.getQuota());
        Assert.assertEquals(ssQuota, quota.getSpaceQuota());
        Assert.assertEquals(1, quota.getFileAndDirectoryCount());
        Assert.assertEquals(0, quota.getSpaceConsumed());
        // mkdir and write a new file
        final FileSystem routerFs = TestRouterQuota.routerContext.getFileSystem();
        routerFs.mkdirs(new Path(((path + "/") + (UUID.randomUUID()))));
        DFSClient routerClient = TestRouterQuota.routerContext.getClient();
        routerClient.create((path + "/file"), true).close();
        appendData((path + "/file"), routerClient, TestRouterQuota.BLOCK_SIZE);
        updateService.periodicInvoke();
        updatedMountTable = getMountTable(path);
        quota = updatedMountTable.getQuota();
        // verify if quota has been updated in state store
        Assert.assertEquals(nsQuota, quota.getQuota());
        Assert.assertEquals(ssQuota, quota.getSpaceQuota());
        Assert.assertEquals(3, quota.getFileAndDirectoryCount());
        Assert.assertEquals(TestRouterQuota.BLOCK_SIZE, quota.getSpaceConsumed());
    }

    @Test
    public void testQuotaSynchronization() throws IOException {
        long updateNsQuota = 3;
        long updateSsQuota = 4;
        FileSystem nnFs = TestRouterQuota.nnContext1.getFileSystem();
        nnFs.mkdirs(new Path("/testsync"));
        MountTable mountTable = MountTable.newInstance("/quotaSync", Collections.singletonMap("ns0", "/testsync"), Time.now(), Time.now());
        mountTable.setQuota(new RouterQuotaUsage.Builder().quota(1).spaceQuota(2).build());
        // Add new mount table
        addMountTable(mountTable);
        // ensure the quota is not set as updated value
        QuotaUsage realQuota = TestRouterQuota.nnContext1.getFileSystem().getQuotaUsage(new Path("/testsync"));
        Assert.assertNotEquals(updateNsQuota, realQuota.getQuota());
        Assert.assertNotEquals(updateSsQuota, realQuota.getSpaceQuota());
        // Call periodicInvoke to ensure quota  updated in quota manager
        // and state store.
        RouterQuotaUpdateService updateService = TestRouterQuota.routerContext.getRouter().getQuotaCacheUpdateService();
        updateService.periodicInvoke();
        mountTable.setQuota(new RouterQuotaUsage.Builder().quota(updateNsQuota).spaceQuota(updateSsQuota).build());
        UpdateMountTableEntryRequest updateRequest = UpdateMountTableEntryRequest.newInstance(mountTable);
        RouterClient client = TestRouterQuota.routerContext.getAdminClient();
        MountTableManager mountTableManager = client.getMountTableManager();
        mountTableManager.updateMountTableEntry(updateRequest);
        // verify if the quota is updated in real path
        realQuota = TestRouterQuota.nnContext1.getFileSystem().getQuotaUsage(new Path("/testsync"));
        Assert.assertEquals(updateNsQuota, realQuota.getQuota());
        Assert.assertEquals(updateSsQuota, realQuota.getSpaceQuota());
        // Clear the quota
        mountTable.setQuota(new RouterQuotaUsage.Builder().quota(QUOTA_RESET).spaceQuota(QUOTA_RESET).build());
        updateRequest = UpdateMountTableEntryRequest.newInstance(mountTable);
        client = TestRouterQuota.routerContext.getAdminClient();
        mountTableManager = client.getMountTableManager();
        mountTableManager.updateMountTableEntry(updateRequest);
        // verify if the quota is updated in real path
        realQuota = TestRouterQuota.nnContext1.getFileSystem().getQuotaUsage(new Path("/testsync"));
        Assert.assertEquals(QUOTA_RESET, realQuota.getQuota());
        Assert.assertEquals(QUOTA_RESET, realQuota.getSpaceQuota());
    }

    @Test
    public void testQuotaRefreshAfterQuotaExceed() throws Exception {
        long nsQuota = 3;
        long ssQuota = 100;
        final FileSystem nnFs1 = TestRouterQuota.nnContext1.getFileSystem();
        final FileSystem nnFs2 = TestRouterQuota.nnContext2.getFileSystem();
        // Add two mount tables:
        // /setquota1 --> ns0---testdir11
        // /setquota2 --> ns1---testdir12
        nnFs1.mkdirs(new Path("/testdir11"));
        nnFs2.mkdirs(new Path("/testdir12"));
        MountTable mountTable1 = MountTable.newInstance("/setquota1", Collections.singletonMap("ns0", "/testdir11"));
        mountTable1.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable1);
        MountTable mountTable2 = MountTable.newInstance("/setquota2", Collections.singletonMap("ns1", "/testdir12"));
        mountTable2.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable2);
        final FileSystem routerFs = TestRouterQuota.routerContext.getFileSystem();
        // Create directory to make directory count equals to nsQuota
        routerFs.mkdirs(new Path(("/setquota1/" + (UUID.randomUUID()))));
        routerFs.mkdirs(new Path(("/setquota1/" + (UUID.randomUUID()))));
        // create one more directory to exceed the nsQuota
        routerFs.mkdirs(new Path(("/setquota1/" + (UUID.randomUUID()))));
        RouterQuotaUpdateService updateService = TestRouterQuota.routerContext.getRouter().getQuotaCacheUpdateService();
        // Call RouterQuotaUpdateService#periodicInvoke to update quota cache
        updateService.periodicInvoke();
        // Reload the Router cache
        TestRouterQuota.resolver.loadCache(true);
        RouterQuotaManager quotaManager = TestRouterQuota.routerContext.getRouter().getQuotaManager();
        ClientProtocol client1 = TestRouterQuota.nnContext1.getClient().getNamenode();
        ClientProtocol client2 = TestRouterQuota.nnContext2.getClient().getNamenode();
        QuotaUsage quota1 = client1.getQuotaUsage("/testdir11");
        QuotaUsage quota2 = client2.getQuotaUsage("/testdir12");
        QuotaUsage cacheQuota1 = quotaManager.getQuotaUsage("/setquota1");
        QuotaUsage cacheQuota2 = quotaManager.getQuotaUsage("/setquota2");
        // Verify quota usage
        Assert.assertEquals(4, quota1.getFileAndDirectoryCount());
        Assert.assertEquals(4, cacheQuota1.getFileAndDirectoryCount());
        Assert.assertEquals(1, quota2.getFileAndDirectoryCount());
        Assert.assertEquals(1, cacheQuota2.getFileAndDirectoryCount());
        try {
            // create new directory to trigger NSQuotaExceededException
            routerFs.mkdirs(new Path(("/testdir11/" + (UUID.randomUUID()))));
            Assert.fail("Mkdir should be failed under dir /testdir11.");
        } catch (NSQuotaExceededException ignored) {
        }
        // Create directory under the other mount point
        routerFs.mkdirs(new Path(("/setquota2/" + (UUID.randomUUID()))));
        routerFs.mkdirs(new Path(("/setquota2/" + (UUID.randomUUID()))));
        // Call RouterQuotaUpdateService#periodicInvoke to update quota cache
        updateService.periodicInvoke();
        quota1 = client1.getQuotaUsage("/testdir11");
        cacheQuota1 = quotaManager.getQuotaUsage("/setquota1");
        quota2 = client2.getQuotaUsage("/testdir12");
        cacheQuota2 = quotaManager.getQuotaUsage("/setquota2");
        // Verify whether quota usage cache is update by periodicInvoke().
        Assert.assertEquals(4, quota1.getFileAndDirectoryCount());
        Assert.assertEquals(4, cacheQuota1.getFileAndDirectoryCount());
        Assert.assertEquals(3, quota2.getFileAndDirectoryCount());
        Assert.assertEquals(3, cacheQuota2.getFileAndDirectoryCount());
    }

    /**
     * Verify whether mount table and quota usage cache is updated properly.
     * {@link RouterQuotaUpdateService#periodicInvoke()} should be able to update
     * the cache and the mount table even if the destination directory for some
     * mount entry is not present in the filesystem.
     */
    @Test
    public void testQuotaRefreshWhenDestinationNotPresent() throws Exception {
        long nsQuota = 5;
        long ssQuota = 3 * (TestRouterQuota.BLOCK_SIZE);
        final FileSystem nnFs = TestRouterQuota.nnContext1.getFileSystem();
        // Add three mount tables:
        // /setdir1 --> ns0---testdir13
        // /setdir2 --> ns0---testdir14
        // Create destination directory
        nnFs.mkdirs(new Path("/testdir13"));
        nnFs.mkdirs(new Path("/testdir14"));
        MountTable mountTable = MountTable.newInstance("/setdir1", Collections.singletonMap("ns0", "/testdir13"));
        mountTable.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable);
        mountTable = MountTable.newInstance("/setdir2", Collections.singletonMap("ns0", "/testdir14"));
        mountTable.setQuota(new RouterQuotaUsage.Builder().quota(nsQuota).spaceQuota(ssQuota).build());
        addMountTable(mountTable);
        final DFSClient routerClient = TestRouterQuota.routerContext.getClient();
        // Create file
        routerClient.create("/setdir1/file1", true).close();
        routerClient.create("/setdir2/file2", true).close();
        // append data to the file
        appendData("/setdir1/file1", routerClient, TestRouterQuota.BLOCK_SIZE);
        appendData("/setdir2/file2", routerClient, TestRouterQuota.BLOCK_SIZE);
        RouterQuotaUpdateService updateService = TestRouterQuota.routerContext.getRouter().getQuotaCacheUpdateService();
        // Update quota cache
        updateService.periodicInvoke();
        // Reload the Router cache
        TestRouterQuota.resolver.loadCache(true);
        ClientProtocol client1 = TestRouterQuota.nnContext1.getClient().getNamenode();
        RouterQuotaManager quotaManager = TestRouterQuota.routerContext.getRouter().getQuotaManager();
        QuotaUsage quota1 = client1.getQuotaUsage("/testdir13");
        QuotaUsage quota2 = client1.getQuotaUsage("/testdir14");
        QuotaUsage cacheQuota1 = quotaManager.getQuotaUsage("/setdir1");
        QuotaUsage cacheQuota2 = quotaManager.getQuotaUsage("/setdir2");
        // Get quota details in mount table
        MountTable updatedMountTable = getMountTable("/setdir1");
        RouterQuotaUsage mountQuota1 = updatedMountTable.getQuota();
        updatedMountTable = getMountTable("/setdir2");
        RouterQuotaUsage mountQuota2 = updatedMountTable.getQuota();
        // Verify quota usage
        Assert.assertEquals(2, quota1.getFileAndDirectoryCount());
        Assert.assertEquals(2, cacheQuota1.getFileAndDirectoryCount());
        Assert.assertEquals(2, mountQuota1.getFileAndDirectoryCount());
        Assert.assertEquals(2, quota2.getFileAndDirectoryCount());
        Assert.assertEquals(2, cacheQuota2.getFileAndDirectoryCount());
        Assert.assertEquals(2, mountQuota2.getFileAndDirectoryCount());
        Assert.assertEquals(TestRouterQuota.BLOCK_SIZE, quota1.getSpaceConsumed());
        Assert.assertEquals(TestRouterQuota.BLOCK_SIZE, cacheQuota1.getSpaceConsumed());
        Assert.assertEquals(TestRouterQuota.BLOCK_SIZE, mountQuota1.getSpaceConsumed());
        Assert.assertEquals(TestRouterQuota.BLOCK_SIZE, quota2.getSpaceConsumed());
        Assert.assertEquals(TestRouterQuota.BLOCK_SIZE, cacheQuota2.getSpaceConsumed());
        Assert.assertEquals(TestRouterQuota.BLOCK_SIZE, mountQuota2.getSpaceConsumed());
        FileSystem routerFs = TestRouterQuota.routerContext.getFileSystem();
        // Remove destination directory for the mount entry
        routerFs.delete(new Path("/setdir1"), true);
        // Create file
        routerClient.create("/setdir2/file3", true).close();
        // append data to the file
        appendData("/setdir2/file3", routerClient, TestRouterQuota.BLOCK_SIZE);
        int updatedSpace = (TestRouterQuota.BLOCK_SIZE) + (TestRouterQuota.BLOCK_SIZE);
        // Update quota cache
        updateService.periodicInvoke();
        quota2 = client1.getQuotaUsage("/testdir14");
        cacheQuota1 = quotaManager.getQuotaUsage("/setdir1");
        cacheQuota2 = quotaManager.getQuotaUsage("/setdir2");
        // Get quota details in mount table
        updatedMountTable = getMountTable("/setdir1");
        mountQuota1 = updatedMountTable.getQuota();
        updatedMountTable = getMountTable("/setdir2");
        mountQuota2 = updatedMountTable.getQuota();
        // If destination is not present the quota usage should be reset to 0
        Assert.assertEquals(0, cacheQuota1.getFileAndDirectoryCount());
        Assert.assertEquals(0, mountQuota1.getFileAndDirectoryCount());
        Assert.assertEquals(0, cacheQuota1.getSpaceConsumed());
        Assert.assertEquals(0, mountQuota1.getSpaceConsumed());
        // Verify current quota usage for other mount entries
        Assert.assertEquals(3, quota2.getFileAndDirectoryCount());
        Assert.assertEquals(3, cacheQuota2.getFileAndDirectoryCount());
        Assert.assertEquals(3, mountQuota2.getFileAndDirectoryCount());
        Assert.assertEquals(updatedSpace, quota2.getSpaceConsumed());
        Assert.assertEquals(updatedSpace, cacheQuota2.getSpaceConsumed());
        Assert.assertEquals(updatedSpace, mountQuota2.getSpaceConsumed());
    }
}

