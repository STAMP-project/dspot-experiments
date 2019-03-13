/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.om;


import StorageType.DISK;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.container.common.helpers.ExcludeList;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfoGroup;
import org.apache.hadoop.ozone.om.helpers.OpenKeySession;
import org.apache.hadoop.ozone.web.handlers.BucketArgs;
import org.apache.hadoop.ozone.web.handlers.KeyArgs;
import org.apache.hadoop.ozone.web.handlers.UserArgs;
import org.apache.hadoop.ozone.web.handlers.VolumeArgs;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * This class tests the versioning of blocks from OM side.
 */
public class TestOmBlockVersioning {
    private static MiniOzoneCluster cluster = null;

    private static UserArgs userArgs;

    private static OzoneConfiguration conf;

    private static OzoneManager ozoneManager;

    private static StorageHandler storageHandler;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testAllocateCommit() throws Exception {
        String userName = "user" + (RandomStringUtils.randomNumeric(5));
        String adminName = "admin" + (RandomStringUtils.randomNumeric(5));
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        String bucketName = "bucket" + (RandomStringUtils.randomNumeric(5));
        String keyName = "key" + (RandomStringUtils.randomNumeric(5));
        VolumeArgs createVolumeArgs = new VolumeArgs(volumeName, TestOmBlockVersioning.userArgs);
        createVolumeArgs.setUserName(userName);
        createVolumeArgs.setAdminName(adminName);
        TestOmBlockVersioning.storageHandler.createVolume(createVolumeArgs);
        BucketArgs bucketArgs = new BucketArgs(bucketName, createVolumeArgs);
        bucketArgs.setAddAcls(new LinkedList());
        bucketArgs.setRemoveAcls(new LinkedList());
        bucketArgs.setStorageType(DISK);
        TestOmBlockVersioning.storageHandler.createBucket(bucketArgs);
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(volumeName).setBucketName(bucketName).setKeyName(keyName).setDataSize(1000).build();
        // 1st update, version 0
        OpenKeySession openKey = TestOmBlockVersioning.ozoneManager.openKey(keyArgs);
        // explicitly set the keyLocation list before committing the key.
        keyArgs.setLocationInfoList(openKey.getKeyInfo().getLatestVersionLocations().getBlocksLatestVersionOnly());
        TestOmBlockVersioning.ozoneManager.commitKey(keyArgs, openKey.getId());
        OmKeyInfo keyInfo = TestOmBlockVersioning.ozoneManager.lookupKey(keyArgs);
        OmKeyLocationInfoGroup highestVersion = checkVersions(keyInfo.getKeyLocationVersions());
        Assert.assertEquals(0, highestVersion.getVersion());
        Assert.assertEquals(1, highestVersion.getLocationList().size());
        // 2nd update, version 1
        openKey = TestOmBlockVersioning.ozoneManager.openKey(keyArgs);
        // OmKeyLocationInfo locationInfo =
        // ozoneManager.allocateBlock(keyArgs, openKey.getId());
        // explicitly set the keyLocation list before committing the key.
        keyArgs.setLocationInfoList(openKey.getKeyInfo().getLatestVersionLocations().getBlocksLatestVersionOnly());
        TestOmBlockVersioning.ozoneManager.commitKey(keyArgs, openKey.getId());
        keyInfo = TestOmBlockVersioning.ozoneManager.lookupKey(keyArgs);
        highestVersion = checkVersions(keyInfo.getKeyLocationVersions());
        Assert.assertEquals(1, highestVersion.getVersion());
        Assert.assertEquals(2, highestVersion.getLocationList().size());
        // 3rd update, version 2
        openKey = TestOmBlockVersioning.ozoneManager.openKey(keyArgs);
        // this block will be appended to the latest version of version 2.
        OmKeyLocationInfo locationInfo = TestOmBlockVersioning.ozoneManager.allocateBlock(keyArgs, openKey.getId(), new ExcludeList());
        List<OmKeyLocationInfo> locationInfoList = openKey.getKeyInfo().getLatestVersionLocations().getBlocksLatestVersionOnly();
        Assert.assertTrue(((locationInfoList.size()) == 1));
        locationInfoList.add(locationInfo);
        keyArgs.setLocationInfoList(locationInfoList);
        TestOmBlockVersioning.ozoneManager.commitKey(keyArgs, openKey.getId());
        keyInfo = TestOmBlockVersioning.ozoneManager.lookupKey(keyArgs);
        highestVersion = checkVersions(keyInfo.getKeyLocationVersions());
        Assert.assertEquals(2, highestVersion.getVersion());
        Assert.assertEquals(4, highestVersion.getLocationList().size());
    }

    @Test
    public void testReadLatestVersion() throws Exception {
        String userName = "user" + (RandomStringUtils.randomNumeric(5));
        String adminName = "admin" + (RandomStringUtils.randomNumeric(5));
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        String bucketName = "bucket" + (RandomStringUtils.randomNumeric(5));
        String keyName = "key" + (RandomStringUtils.randomNumeric(5));
        VolumeArgs createVolumeArgs = new VolumeArgs(volumeName, TestOmBlockVersioning.userArgs);
        createVolumeArgs.setUserName(userName);
        createVolumeArgs.setAdminName(adminName);
        TestOmBlockVersioning.storageHandler.createVolume(createVolumeArgs);
        BucketArgs bucketArgs = new BucketArgs(bucketName, createVolumeArgs);
        bucketArgs.setAddAcls(new LinkedList());
        bucketArgs.setRemoveAcls(new LinkedList());
        bucketArgs.setStorageType(DISK);
        TestOmBlockVersioning.storageHandler.createBucket(bucketArgs);
        OmKeyArgs omKeyArgs = new OmKeyArgs.Builder().setVolumeName(volumeName).setBucketName(bucketName).setKeyName(keyName).setDataSize(1000).build();
        String dataString = RandomStringUtils.randomAlphabetic(100);
        KeyArgs keyArgs = new KeyArgs(volumeName, bucketName, keyName, TestOmBlockVersioning.userArgs);
        // this write will create 1st version with one block
        try (OutputStream stream = TestOmBlockVersioning.storageHandler.newKeyWriter(keyArgs)) {
            stream.write(dataString.getBytes());
        }
        byte[] data = new byte[dataString.length()];
        try (InputStream in = TestOmBlockVersioning.storageHandler.newKeyReader(keyArgs)) {
            in.read(data);
        }
        OmKeyInfo keyInfo = TestOmBlockVersioning.ozoneManager.lookupKey(omKeyArgs);
        Assert.assertEquals(dataString, DFSUtil.bytes2String(data));
        Assert.assertEquals(0, keyInfo.getLatestVersionLocations().getVersion());
        Assert.assertEquals(1, keyInfo.getLatestVersionLocations().getLocationList().size());
        // this write will create 2nd version, 2nd version will contain block from
        // version 1, and add a new block
        dataString = RandomStringUtils.randomAlphabetic(10);
        data = new byte[dataString.length()];
        try (OutputStream stream = TestOmBlockVersioning.storageHandler.newKeyWriter(keyArgs)) {
            stream.write(dataString.getBytes());
        }
        try (InputStream in = TestOmBlockVersioning.storageHandler.newKeyReader(keyArgs)) {
            in.read(data);
        }
        keyInfo = TestOmBlockVersioning.ozoneManager.lookupKey(omKeyArgs);
        Assert.assertEquals(dataString, DFSUtil.bytes2String(data));
        Assert.assertEquals(1, keyInfo.getLatestVersionLocations().getVersion());
        Assert.assertEquals(2, keyInfo.getLatestVersionLocations().getLocationList().size());
        dataString = RandomStringUtils.randomAlphabetic(200);
        data = new byte[dataString.length()];
        try (OutputStream stream = TestOmBlockVersioning.storageHandler.newKeyWriter(keyArgs)) {
            stream.write(dataString.getBytes());
        }
        try (InputStream in = TestOmBlockVersioning.storageHandler.newKeyReader(keyArgs)) {
            in.read(data);
        }
        keyInfo = TestOmBlockVersioning.ozoneManager.lookupKey(omKeyArgs);
        Assert.assertEquals(dataString, DFSUtil.bytes2String(data));
        Assert.assertEquals(2, keyInfo.getLatestVersionLocations().getVersion());
        Assert.assertEquals(3, keyInfo.getLatestVersionLocations().getLocationList().size());
    }
}

