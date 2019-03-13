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
package org.apache.hadoop.hbase.util.compaction;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionFileSystem;
import org.apache.hadoop.hbase.regionserver.StoreFileInfo;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ SmallTests.class })
public class TestMajorCompactionRequest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMajorCompactionRequest.class);

    private static final HBaseTestingUtility UTILITY = new HBaseTestingUtility();

    private static final String FAMILY = "a";

    private Path rootRegionDir;

    private Path regionStoreDir;

    @Test
    public void testStoresNeedingCompaction() throws Exception {
        // store files older than timestamp
        List<StoreFileInfo> storeFiles = mockStoreFiles(regionStoreDir, 5, 10);
        MajorCompactionRequest request = makeMockRequest(100, storeFiles, false);
        Optional<MajorCompactionRequest> result = request.createRequest(Mockito.mock(Configuration.class), Sets.newHashSet(TestMajorCompactionRequest.FAMILY));
        Assert.assertTrue(result.isPresent());
        // store files newer than timestamp
        storeFiles = mockStoreFiles(regionStoreDir, 5, 101);
        request = makeMockRequest(100, storeFiles, false);
        result = request.createRequest(Mockito.mock(Configuration.class), Sets.newHashSet(TestMajorCompactionRequest.FAMILY));
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testIfWeHaveNewReferenceFilesButOldStoreFiles() throws Exception {
        // this tests that reference files that are new, but have older timestamps for the files
        // they reference still will get compacted.
        TableName table = TableName.valueOf("TestMajorCompactor");
        TableDescriptor htd = TestMajorCompactionRequest.UTILITY.createTableDescriptor(table, Bytes.toBytes(TestMajorCompactionRequest.FAMILY));
        RegionInfo hri = RegionInfoBuilder.newBuilder(htd.getTableName()).build();
        HRegion region = HBaseTestingUtility.createRegionAndWAL(hri, rootRegionDir, TestMajorCompactionRequest.UTILITY.getConfiguration(), htd);
        Configuration configuration = Mockito.mock(Configuration.class);
        // the reference file timestamp is newer
        List<StoreFileInfo> storeFiles = mockStoreFiles(regionStoreDir, 4, 101);
        List<Path> paths = storeFiles.stream().map(StoreFileInfo::getPath).collect(Collectors.toList());
        // the files that are referenced are older, thus we still compact.
        HRegionFileSystem fileSystem = mockFileSystem(region.getRegionInfo(), true, storeFiles, 50);
        MajorCompactionRequest majorCompactionRequest = Mockito.spy(new MajorCompactionRequest(configuration, region.getRegionInfo(), Sets.newHashSet(TestMajorCompactionRequest.FAMILY), 100));
        Mockito.doReturn(Mockito.mock(Connection.class)).when(majorCompactionRequest).getConnection(ArgumentMatchers.eq(configuration));
        Mockito.doReturn(paths).when(majorCompactionRequest).getReferenceFilePaths(ArgumentMatchers.any(FileSystem.class), ArgumentMatchers.any(Path.class));
        Mockito.doReturn(fileSystem).when(majorCompactionRequest).getFileSystem(ArgumentMatchers.any(Connection.class));
        Set<String> result = majorCompactionRequest.getStoresRequiringCompaction(Sets.newHashSet("a"));
        Assert.assertEquals(TestMajorCompactionRequest.FAMILY, Iterables.getOnlyElement(result));
    }
}

