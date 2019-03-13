/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.om;


import java.io.IOException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for S3 Bucket Manager.
 */
public class TestS3BucketManager {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private OzoneConfiguration conf;

    private OmMetadataManagerImpl metaMgr;

    private BucketManager bucketManager;

    private VolumeManager volumeManager;

    @Test
    public void testCreateS3Bucket() throws IOException {
        S3BucketManager s3BucketManager = new S3BucketManagerImpl(conf, metaMgr, volumeManager, bucketManager);
        s3BucketManager.createOzoneVolumeIfNeeded("bilbo");
        s3BucketManager.createS3Bucket("bilbo", "bucket");
        // This call should have created a ozone volume called s3bilbo and bucket
        // called s3bilbo/bucket.
        Assert.assertNotNull(volumeManager.getVolumeInfo("s3bilbo"));
        Assert.assertNotNull(bucketManager.getBucketInfo("s3bilbo", "bucket"));
        // recreating the same bucket should throw.
        thrown.expect(IOException.class);
        s3BucketManager.createS3Bucket("bilbo", "bucket");
    }

    @Test
    public void testOzoneVolumeNameForUser() throws IOException {
        S3BucketManager s3BucketManager = new S3BucketManagerImpl(conf, metaMgr, volumeManager, bucketManager);
        String userName = "ozone";
        String volumeName = s3BucketManager.getOzoneVolumeNameForUser(userName);
        Assert.assertEquals(((OzoneConsts.OM_S3_VOLUME_PREFIX) + userName), volumeName);
    }

    @Test
    public void testOzoneVolumeNameForUserFails() throws IOException {
        S3BucketManager s3BucketManager = new S3BucketManagerImpl(conf, metaMgr, volumeManager, bucketManager);
        String userName = null;
        try {
            String volumeName = s3BucketManager.getOzoneVolumeNameForUser(userName);
            Assert.fail("testOzoneVolumeNameForUserFails failed");
        } catch (NullPointerException ex) {
            GenericTestUtils.assertExceptionContains("UserName cannot be null", ex);
        }
    }

    @Test
    public void testDeleteS3Bucket() throws IOException {
        S3BucketManager s3BucketManager = new S3BucketManagerImpl(conf, metaMgr, volumeManager, bucketManager);
        s3BucketManager.createOzoneVolumeIfNeeded("ozone");
        s3BucketManager.createS3Bucket("ozone", "s3bucket");
        // This call should have created a ozone volume called s3ozone and bucket
        // called s3ozone/s3bucket.
        Assert.assertNotNull(volumeManager.getVolumeInfo("s3ozone"));
        Assert.assertNotNull(bucketManager.getBucketInfo("s3ozone", "s3bucket"));
        s3BucketManager.deleteS3Bucket("s3bucket");
        // Deleting non existing bucket should throw.
        thrown.expect(IOException.class);
        s3BucketManager.deleteS3Bucket("s3bucket");
    }

    @Test
    public void testGetS3BucketMapping() throws IOException {
        S3BucketManager s3BucketManager = new S3BucketManagerImpl(conf, metaMgr, volumeManager, bucketManager);
        s3BucketManager.createOzoneVolumeIfNeeded("bilbo");
        s3BucketManager.createS3Bucket("bilbo", "newBucket");
        String mapping = s3BucketManager.getOzoneBucketMapping("newBucket");
        Assert.assertTrue(mapping.startsWith("s3bilbo/"));
        Assert.assertTrue(mapping.endsWith("/newBucket"));
    }

    @Test
    public void testGetOzoneNames() throws IOException {
        S3BucketManager s3BucketManager = new S3BucketManagerImpl(conf, metaMgr, volumeManager, bucketManager);
        s3BucketManager.createOzoneVolumeIfNeeded("batman");
        s3BucketManager.createS3Bucket("batman", "gotham");
        String volumeName = s3BucketManager.getOzoneVolumeName("gotham");
        Assert.assertTrue(volumeName.equalsIgnoreCase("s3batman"));
        String bucketName = s3BucketManager.getOzoneBucketName("gotham");
        Assert.assertTrue(bucketName.equalsIgnoreCase("gotham"));
        // try to get a bucket that does not exist.
        thrown.expectMessage("No such S3 bucket.");
        s3BucketManager.getOzoneBucketMapping("raven");
    }

    /**
     * This tests makes sure bucket names are unique across users.
     */
    @Test
    public void testBucketNameAreUnique() throws IOException {
        S3BucketManager s3BucketManager = new S3BucketManagerImpl(conf, metaMgr, volumeManager, bucketManager);
        s3BucketManager.createOzoneVolumeIfNeeded("superman");
        s3BucketManager.createS3Bucket("superman", "metropolis");
        // recreating the same bucket  even with a different user will throw.
        thrown.expectMessage("Unable to create S3 bucket.");
        s3BucketManager.createS3Bucket("luthor", "metropolis");
    }
}

