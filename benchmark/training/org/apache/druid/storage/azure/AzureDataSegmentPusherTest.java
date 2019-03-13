/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.storage.azure;


import AzureStorageDruidModule.INDEX_ZIP_FILE_NAME;
import AzureStorageDruidModule.SCHEME;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.storage.StorageException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.MapUtils;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class AzureDataSegmentPusherTest extends EasyMockSupport {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String containerName = "container";

    private static final String blobPath = "test/2015-04-12T00:00:00.000Z_2015-04-13T00:00:00.000Z/1/0/index.zip";

    private static final DataSegment dataSegment = new DataSegment("test", Intervals.of("2015-04-12/2015-04-13"), "1", ImmutableMap.of("containerName", AzureDataSegmentPusherTest.containerName, "blobPath", AzureDataSegmentPusherTest.blobPath), null, null, NoneShardSpec.instance(), 0, 1);

    private AzureStorage azureStorage;

    private AzureAccountConfig azureAccountConfig;

    private ObjectMapper jsonMapper;

    @Test
    public void testPush() throws Exception {
        testPushInternal(false, "foo/20150101T000000\\.000Z_20160101T000000\\.000Z/0/0/index\\.zip");
    }

    @Test
    public void testPushUseUniquePath() throws Exception {
        testPushInternal(true, "foo/20150101T000000\\.000Z_20160101T000000\\.000Z/0/0/[A-Za-z0-9-]{36}/index\\.zip");
    }

    @Test
    public void getAzurePathsTest() {
        AzureDataSegmentPusher pusher = new AzureDataSegmentPusher(azureStorage, azureAccountConfig);
        final String storageDir = pusher.getStorageDir(AzureDataSegmentPusherTest.dataSegment, false);
        final String azurePath = pusher.getAzurePath(AzureDataSegmentPusherTest.dataSegment, false);
        Assert.assertEquals(StringUtils.format("%s/%s", storageDir, INDEX_ZIP_FILE_NAME), azurePath);
    }

    @Test
    public void uploadDataSegmentTest() throws StorageException, IOException, URISyntaxException {
        AzureDataSegmentPusher pusher = new AzureDataSegmentPusher(azureStorage, azureAccountConfig);
        final int binaryVersion = 9;
        final File compressedSegmentData = new File("index.zip");
        final String azurePath = pusher.getAzurePath(AzureDataSegmentPusherTest.dataSegment, false);
        azureStorage.uploadBlob(compressedSegmentData, AzureDataSegmentPusherTest.containerName, azurePath);
        expectLastCall();
        replayAll();
        DataSegment pushedDataSegment = // empty file
        pusher.uploadDataSegment(AzureDataSegmentPusherTest.dataSegment, binaryVersion, 0, compressedSegmentData, azurePath);
        Assert.assertEquals(compressedSegmentData.length(), pushedDataSegment.getSize());
        Assert.assertEquals(binaryVersion, ((int) (pushedDataSegment.getBinaryVersion())));
        Map<String, Object> loadSpec = pushedDataSegment.getLoadSpec();
        Assert.assertEquals(SCHEME, MapUtils.getString(loadSpec, "type"));
        Assert.assertEquals(azurePath, MapUtils.getString(loadSpec, "blobPath"));
        verifyAll();
    }

    @Test
    public void getPathForHadoopTest() {
        AzureDataSegmentPusher pusher = new AzureDataSegmentPusher(azureStorage, azureAccountConfig);
        String hadoopPath = pusher.getPathForHadoop();
        Assert.assertEquals("wasbs://container@account.blob.core.windows.net/", hadoopPath);
    }

    @Test
    public void storageDirContainsNoColonsTest() {
        AzureDataSegmentPusher pusher = new AzureDataSegmentPusher(azureStorage, azureAccountConfig);
        DataSegment withColons = AzureDataSegmentPusherTest.dataSegment.withVersion("2018-01-05T14:54:09.295Z");
        String segmentPath = pusher.getStorageDir(withColons, false);
        Assert.assertFalse("Path should not contain any columns", segmentPath.contains(":"));
    }
}

