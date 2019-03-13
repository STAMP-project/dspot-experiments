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
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.om;


import GenericTestUtils.LogCapturer;
import OzoneQuota.Units;
import ResultCodes.INTERNAL_ERROR;
import StorageType.DISK;
import java.util.LinkedList;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OzoneTestUtils;
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
 * Test for Ozone Manager ACLs.
 */
public class TestOmAcls {
    private static MiniOzoneCluster cluster = null;

    private static StorageHandler storageHandler;

    private static UserArgs userArgs;

    private static OMMetrics omMetrics;

    private static OzoneConfiguration conf;

    private static String clusterId;

    private static String scmId;

    private static String omId;

    private static LogCapturer logCapturer;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Tests the OM Initialization.
     */
    @Test
    public void testOMAclsPermissionDenied() throws Exception {
        String user0 = "testListVolumes-user-0";
        String adminUser = "testListVolumes-admin";
        final VolumeArgs createVolumeArgs;
        int i = 100;
        String user0VolName = (("Vol-" + user0) + "-") + i;
        createVolumeArgs = new VolumeArgs(user0VolName, TestOmAcls.userArgs);
        createVolumeArgs.setUserName(user0);
        createVolumeArgs.setAdminName(adminUser);
        createVolumeArgs.setQuota(new org.apache.hadoop.ozone.web.request.OzoneQuota(i, Units.GB));
        TestOmAcls.logCapturer.clearOutput();
        OzoneTestUtils.expectOmException(INTERNAL_ERROR, () -> TestOmAcls.storageHandler.createVolume(createVolumeArgs));
        Assert.assertTrue(TestOmAcls.logCapturer.getOutput().contains(("doesn't have CREATE " + "permission to access volume")));
        BucketArgs bucketArgs = new BucketArgs("bucket1", createVolumeArgs);
        bucketArgs.setAddAcls(new LinkedList());
        bucketArgs.setRemoveAcls(new LinkedList());
        bucketArgs.setStorageType(DISK);
        OzoneTestUtils.expectOmException(INTERNAL_ERROR, () -> TestOmAcls.storageHandler.createBucket(bucketArgs));
        Assert.assertTrue(TestOmAcls.logCapturer.getOutput().contains(("doesn't have CREATE " + "permission to access bucket")));
    }

    @Test
    public void testFailureInKeyOp() throws Exception {
        final VolumeArgs createVolumeArgs;
        String userName = "user" + (RandomStringUtils.randomNumeric(5));
        String adminName = "admin" + (RandomStringUtils.randomNumeric(5));
        createVolumeArgs = new VolumeArgs(userName, TestOmAcls.userArgs);
        createVolumeArgs.setUserName(userName);
        createVolumeArgs.setAdminName(adminName);
        createVolumeArgs.setQuota(new org.apache.hadoop.ozone.web.request.OzoneQuota(100, Units.GB));
        BucketArgs bucketArgs = new BucketArgs("bucket1", createVolumeArgs);
        bucketArgs.setAddAcls(new LinkedList());
        bucketArgs.setRemoveAcls(new LinkedList());
        bucketArgs.setStorageType(DISK);
        TestOmAcls.logCapturer.clearOutput();
        // write a key without specifying size at all
        String keyName = "testKey";
        KeyArgs keyArgs = new KeyArgs(keyName, bucketArgs);
        OzoneTestUtils.expectOmException(INTERNAL_ERROR, () -> TestOmAcls.storageHandler.newKeyWriter(keyArgs));
        Assert.assertTrue(TestOmAcls.logCapturer.getOutput().contains(("doesn't have READ permission" + " to access key")));
    }
}

