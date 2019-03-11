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
package org.apache.hadoop.ozone.ozShell;


import OzoneConsts.OZONE_TIME_ZONE;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OzoneAcl;
import org.apache.hadoop.ozone.OzoneAcl.OzoneACLRights;
import org.apache.hadoop.ozone.OzoneAcl.OzoneACLType;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneKey;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.VolumeArgs;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.client.protocol.ClientProtocol;
import org.apache.hadoop.ozone.om.OMConfigKeys;
import org.apache.hadoop.ozone.om.exceptions.OMException;
import org.apache.hadoop.ozone.web.ozShell.Shell;
import org.apache.hadoop.ozone.web.request.OzoneQuota;
import org.apache.hadoop.ozone.web.response.BucketInfo;
import org.apache.hadoop.ozone.web.response.KeyInfo;
import org.apache.hadoop.ozone.web.response.VolumeInfo;
import org.apache.hadoop.ozone.web.utils.JsonUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test class specified for testing Ozone shell command.
 */
@RunWith(Parameterized.class)
public class TestOzoneShell {
    private static final Logger LOG = LoggerFactory.getLogger(TestOzoneShell.class);

    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static String url;

    private static File baseDir;

    private static OzoneConfiguration conf = null;

    private static MiniOzoneCluster cluster = null;

    private static ClientProtocol client = null;

    private static Shell shell = null;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private static final PrintStream OLD_OUT = System.out;

    private static final PrintStream OLD_ERR = System.err;

    @Parameterized.Parameter
    @SuppressWarnings("visibilitymodifier")
    public Class clientProtocol;

    @Test
    public void testCreateVolume() throws Exception {
        TestOzoneShell.LOG.info("Running testCreateVolume");
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        testCreateVolume(volumeName, "");
        volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        testCreateVolume(("/////" + volumeName), "");
        testCreateVolume("/////", "Volume name is required");
        testCreateVolume("/////vol/123", "Invalid volume name. Delimiters (/) not allowed in volume name");
    }

    /**
     * Test to create volume without specifying --user or -u.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateVolumeWithoutUser() throws Exception {
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        String[] args = new String[]{ "volume", "create", ((TestOzoneShell.url) + "/") + volumeName, "--root" };
        execute(TestOzoneShell.shell, args);
        String truncatedVolumeName = volumeName.substring(((volumeName.lastIndexOf('/')) + 1));
        OzoneVolume volumeInfo = TestOzoneShell.client.getVolumeDetails(truncatedVolumeName);
        Assert.assertEquals(truncatedVolumeName, volumeInfo.getName());
        Assert.assertEquals(UserGroupInformation.getCurrentUser().getUserName(), volumeInfo.getOwner());
    }

    @Test
    public void testDeleteVolume() throws Exception {
        TestOzoneShell.LOG.info("Running testDeleteVolume");
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        VolumeArgs volumeArgs = VolumeArgs.newBuilder().setOwner("bilbo").setQuota("100TB").build();
        TestOzoneShell.client.createVolume(volumeName, volumeArgs);
        OzoneVolume volume = TestOzoneShell.client.getVolumeDetails(volumeName);
        Assert.assertNotNull(volume);
        String[] args = new String[]{ "volume", "delete", ((TestOzoneShell.url) + "/") + volumeName };
        execute(TestOzoneShell.shell, args);
        String output = out.toString();
        Assert.assertTrue(output.contains((("Volume " + volumeName) + " is deleted")));
        // verify if volume has been deleted
        try {
            TestOzoneShell.client.getVolumeDetails(volumeName);
            Assert.fail("Get volume call should have thrown.");
        } catch (OMException e) {
            Assert.assertEquals(VOLUME_NOT_FOUND, e.getResult());
        }
        volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        volumeArgs = VolumeArgs.newBuilder().setOwner("bilbo").setQuota("100TB").build();
        TestOzoneShell.client.createVolume(volumeName, volumeArgs);
        volume = TestOzoneShell.client.getVolumeDetails(volumeName);
        Assert.assertNotNull(volume);
        // volumeName prefixed with /
        String volumeNameWithSlashPrefix = "/" + volumeName;
        args = new String[]{ "volume", "delete", ((TestOzoneShell.url) + "/") + volumeNameWithSlashPrefix };
        execute(TestOzoneShell.shell, args);
        output = out.toString();
        Assert.assertTrue(output.contains((("Volume " + volumeName) + " is deleted")));
        // verify if volume has been deleted
        try {
            TestOzoneShell.client.getVolumeDetails(volumeName);
            Assert.fail("Get volume call should have thrown.");
        } catch (OMException e) {
            Assert.assertEquals(VOLUME_NOT_FOUND, e.getResult());
        }
    }

    @Test
    public void testInfoVolume() throws Exception {
        TestOzoneShell.LOG.info("Running testInfoVolume");
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        VolumeArgs volumeArgs = VolumeArgs.newBuilder().setOwner("bilbo").setQuota("100TB").build();
        TestOzoneShell.client.createVolume(volumeName, volumeArgs);
        // volumeName supplied as-is
        String[] args = new String[]{ "volume", "info", ((TestOzoneShell.url) + "/") + volumeName };
        execute(TestOzoneShell.shell, args);
        String output = out.toString();
        Assert.assertTrue(output.contains(volumeName));
        Assert.assertTrue(((output.contains("createdOn")) && (output.contains(OZONE_TIME_ZONE))));
        // volumeName prefixed with /
        String volumeNameWithSlashPrefix = "/" + volumeName;
        args = new String[]{ "volume", "info", ((TestOzoneShell.url) + "/") + volumeNameWithSlashPrefix };
        execute(TestOzoneShell.shell, args);
        output = out.toString();
        Assert.assertTrue(output.contains(volumeName));
        Assert.assertTrue(((output.contains("createdOn")) && (output.contains(OZONE_TIME_ZONE))));
        // test infoVolume with invalid volume name
        args = new String[]{ "volume", "info", (((TestOzoneShell.url) + "/") + volumeName) + "/invalid-name" };
        executeWithError(TestOzoneShell.shell, args, ("Invalid volume name. " + "Delimiters (/) not allowed in volume name"));
        // get info for non-exist volume
        args = new String[]{ "volume", "info", (TestOzoneShell.url) + "/invalid-volume" };
        executeWithError(TestOzoneShell.shell, args, VOLUME_NOT_FOUND);
    }

    @Test
    public void testShellIncompleteCommand() throws Exception {
        TestOzoneShell.LOG.info("Running testShellIncompleteCommand");
        String expectedError = "Incomplete command";
        String[] args = new String[]{  };// executing 'ozone sh'

        executeWithError(TestOzoneShell.shell, args, expectedError, ("Usage: ozone sh [-hV] [--verbose] [-D=<String=String>]..." + " [COMMAND]"));
        args = new String[]{ "volume" };// executing 'ozone sh volume'

        executeWithError(TestOzoneShell.shell, args, expectedError, "Usage: ozone sh volume [-hV] [COMMAND]");
        args = new String[]{ "bucket" };// executing 'ozone sh bucket'

        executeWithError(TestOzoneShell.shell, args, expectedError, "Usage: ozone sh bucket [-hV] [COMMAND]");
        args = new String[]{ "key" };// executing 'ozone sh key'

        executeWithError(TestOzoneShell.shell, args, expectedError, "Usage: ozone sh key [-hV] [COMMAND]");
    }

    @Test
    public void testUpdateVolume() throws Exception {
        TestOzoneShell.LOG.info("Running testUpdateVolume");
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        String userName = "bilbo";
        VolumeArgs volumeArgs = VolumeArgs.newBuilder().setOwner("bilbo").setQuota("100TB").build();
        TestOzoneShell.client.createVolume(volumeName, volumeArgs);
        OzoneVolume vol = TestOzoneShell.client.getVolumeDetails(volumeName);
        Assert.assertEquals(userName, vol.getOwner());
        Assert.assertEquals(OzoneQuota.parseQuota("100TB").sizeInBytes(), vol.getQuota());
        String[] args = new String[]{ "volume", "update", ((TestOzoneShell.url) + "/") + volumeName, "--quota", "500MB" };
        execute(TestOzoneShell.shell, args);
        vol = TestOzoneShell.client.getVolumeDetails(volumeName);
        Assert.assertEquals(userName, vol.getOwner());
        Assert.assertEquals(OzoneQuota.parseQuota("500MB").sizeInBytes(), vol.getQuota());
        String newUser = "new-user";
        args = new String[]{ "volume", "update", ((TestOzoneShell.url) + "/") + volumeName, "--user", newUser };
        execute(TestOzoneShell.shell, args);
        vol = TestOzoneShell.client.getVolumeDetails(volumeName);
        Assert.assertEquals(newUser, vol.getOwner());
        // volume with / prefix
        String volumeWithPrefix = "/" + volumeName;
        String newUser2 = "new-user2";
        args = new String[]{ "volume", "update", ((TestOzoneShell.url) + "/") + volumeWithPrefix, "--user", newUser2 };
        execute(TestOzoneShell.shell, args);
        vol = TestOzoneShell.client.getVolumeDetails(volumeName);
        Assert.assertEquals(newUser2, vol.getOwner());
        // test error conditions
        args = new String[]{ "volume", "update", (TestOzoneShell.url) + "/invalid-volume", "--user", newUser };
        executeWithError(TestOzoneShell.shell, args, ResultCodes.VOLUME_NOT_FOUND);
        err.reset();
        args = new String[]{ "volume", "update", (TestOzoneShell.url) + "/invalid-volume", "--quota", "500MB" };
        executeWithError(TestOzoneShell.shell, args, ResultCodes.VOLUME_NOT_FOUND);
    }

    @Test
    public void testListVolume() throws Exception {
        TestOzoneShell.LOG.info("Running testListVolume");
        String protocol = clientProtocol.getName().toLowerCase();
        String commandOutput;
        String commandError;
        List<VolumeInfo> volumes;
        final int volCount = 20;
        final String user1 = "test-user-a-" + protocol;
        final String user2 = "test-user-b-" + protocol;
        // Create 20 volumes, 10 for user1 and another 10 for user2.
        for (int x = 0; x < volCount; x++) {
            String volumeName;
            String userName;
            if ((x % 2) == 0) {
                // create volume [test-vol0, test-vol2, ..., test-vol18] for user1
                userName = user1;
                volumeName = ("test-vol-" + protocol) + x;
            } else {
                // create volume [test-vol1, test-vol3, ..., test-vol19] for user2
                userName = user2;
                volumeName = ("test-vol-" + protocol) + x;
            }
            VolumeArgs volumeArgs = VolumeArgs.newBuilder().setOwner(userName).setQuota("100TB").build();
            TestOzoneShell.client.createVolume(volumeName, volumeArgs);
            OzoneVolume vol = TestOzoneShell.client.getVolumeDetails(volumeName);
            Assert.assertNotNull(vol);
        }
        String[] args = new String[]{ "volume", "list", (TestOzoneShell.url) + "/abcde", "--user", user1, "--length", "100" };
        executeWithError(TestOzoneShell.shell, args, "Invalid URI");
        err.reset();
        // test -length option
        args = new String[]{ "volume", "list", (TestOzoneShell.url) + "/", "--user", user1, "--length", "100" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        volumes = ((List<VolumeInfo>) (JsonUtils.toJsonList(commandOutput, VolumeInfo.class)));
        Assert.assertEquals(10, volumes.size());
        for (VolumeInfo volume : volumes) {
            Assert.assertEquals(volume.getOwner().getName(), user1);
            Assert.assertTrue(volume.getCreatedOn().contains(OZONE_TIME_ZONE));
        }
        out.reset();
        args = new String[]{ "volume", "list", (TestOzoneShell.url) + "/", "--user", user1, "--length", "2" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        volumes = ((List<VolumeInfo>) (JsonUtils.toJsonList(commandOutput, VolumeInfo.class)));
        Assert.assertEquals(2, volumes.size());
        // test --prefix option
        out.reset();
        args = new String[]{ "volume", "list", (TestOzoneShell.url) + "/", "--user", user1, "--length", "100", "--prefix", ("test-vol-" + protocol) + "1" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        volumes = ((List<VolumeInfo>) (JsonUtils.toJsonList(commandOutput, VolumeInfo.class)));
        Assert.assertEquals(5, volumes.size());
        // return volume names should be [test-vol10, test-vol12, ..., test-vol18]
        for (int i = 0; i < (volumes.size()); i++) {
            Assert.assertEquals(volumes.get(i).getVolumeName(), (("test-vol-" + protocol) + ((i + 5) * 2)));
            Assert.assertEquals(volumes.get(i).getOwner().getName(), user1);
        }
        // test -start option
        out.reset();
        args = new String[]{ "volume", "list", (TestOzoneShell.url) + "/", "--user", user2, "--length", "100", "--start", ("test-vol-" + protocol) + "15" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        volumes = ((List<VolumeInfo>) (JsonUtils.toJsonList(commandOutput, VolumeInfo.class)));
        Assert.assertEquals(2, volumes.size());
        Assert.assertEquals(volumes.get(0).getVolumeName(), (("test-vol-" + protocol) + "17"));
        Assert.assertEquals(volumes.get(1).getVolumeName(), (("test-vol-" + protocol) + "19"));
        Assert.assertEquals(volumes.get(0).getOwner().getName(), user2);
        Assert.assertEquals(volumes.get(1).getOwner().getName(), user2);
        // test error conditions
        err.reset();
        args = new String[]{ "volume", "list", (TestOzoneShell.url) + "/", "--user", user2, "--length", "-1" };
        executeWithError(TestOzoneShell.shell, args, "the length should be a positive number");
        err.reset();
        args = new String[]{ "volume", "list", (TestOzoneShell.url) + "/", "--user", user2, "--length", "invalid-length" };
        executeWithError(TestOzoneShell.shell, args, "For input string: \"invalid-length\"");
    }

    @Test
    public void testCreateBucket() throws Exception {
        TestOzoneShell.LOG.info("Running testCreateBucket");
        OzoneVolume vol = creatVolume();
        String bucketName = "bucket" + (RandomStringUtils.randomNumeric(5));
        String[] args = new String[]{ "bucket", "create", ((((TestOzoneShell.url) + "/") + (vol.getName())) + "/") + bucketName };
        execute(TestOzoneShell.shell, args);
        OzoneBucket bucketInfo = vol.getBucket(bucketName);
        Assert.assertEquals(vol.getName(), bucketInfo.getVolumeName());
        Assert.assertEquals(bucketName, bucketInfo.getName());
        // test create a bucket in a non-exist volume
        args = new String[]{ "bucket", "create", ((TestOzoneShell.url) + "/invalid-volume/") + bucketName };
        executeWithError(TestOzoneShell.shell, args, VOLUME_NOT_FOUND);
        // test createBucket with invalid bucket name
        args = new String[]{ "bucket", "create", (((((TestOzoneShell.url) + "/") + (vol.getName())) + "/") + bucketName) + "/invalid-name" };
        executeWithError(TestOzoneShell.shell, args, "Invalid bucket name. Delimiters (/) not allowed in bucket name");
    }

    @Test
    public void testDeleteBucket() throws Exception {
        TestOzoneShell.LOG.info("Running testDeleteBucket");
        OzoneVolume vol = creatVolume();
        String bucketName = "bucket" + (RandomStringUtils.randomNumeric(5));
        vol.createBucket(bucketName);
        OzoneBucket bucketInfo = vol.getBucket(bucketName);
        Assert.assertNotNull(bucketInfo);
        String[] args = new String[]{ "bucket", "delete", ((((TestOzoneShell.url) + "/") + (vol.getName())) + "/") + bucketName };
        execute(TestOzoneShell.shell, args);
        // verify if bucket has been deleted in volume
        try {
            vol.getBucket(bucketName);
            Assert.fail("Get bucket should have thrown.");
        } catch (OMException e) {
            Assert.assertEquals(BUCKET_NOT_FOUND, e.getResult());
        }
        // test delete bucket in a non-exist volume
        args = new String[]{ "bucket", "delete", (((TestOzoneShell.url) + "/invalid-volume") + "/") + bucketName };
        executeWithError(TestOzoneShell.shell, args, VOLUME_NOT_FOUND);
        err.reset();
        // test delete non-exist bucket
        args = new String[]{ "bucket", "delete", (((TestOzoneShell.url) + "/") + (vol.getName())) + "/invalid-bucket" };
        executeWithError(TestOzoneShell.shell, args, BUCKET_NOT_FOUND);
    }

    @Test
    public void testInfoBucket() throws Exception {
        TestOzoneShell.LOG.info("Running testInfoBucket");
        OzoneVolume vol = creatVolume();
        String bucketName = "bucket" + (RandomStringUtils.randomNumeric(5));
        vol.createBucket(bucketName);
        String[] args = new String[]{ "bucket", "info", ((((TestOzoneShell.url) + "/") + (vol.getName())) + "/") + bucketName };
        execute(TestOzoneShell.shell, args);
        String output = out.toString();
        Assert.assertTrue(output.contains(bucketName));
        Assert.assertTrue(((output.contains("createdOn")) && (output.contains(OZONE_TIME_ZONE))));
        // test infoBucket with invalid bucket name
        args = new String[]{ "bucket", "info", (((((TestOzoneShell.url) + "/") + (vol.getName())) + "/") + bucketName) + "/invalid-name" };
        executeWithError(TestOzoneShell.shell, args, "Invalid bucket name. Delimiters (/) not allowed in bucket name");
        // test get info from a non-exist bucket
        args = new String[]{ "bucket", "info", ((((TestOzoneShell.url) + "/") + (vol.getName())) + "/invalid-bucket") + bucketName };
        executeWithError(TestOzoneShell.shell, args, ResultCodes.BUCKET_NOT_FOUND);
    }

    @Test
    public void testUpdateBucket() throws Exception {
        TestOzoneShell.LOG.info("Running testUpdateBucket");
        OzoneVolume vol = creatVolume();
        String bucketName = "bucket" + (RandomStringUtils.randomNumeric(5));
        vol.createBucket(bucketName);
        OzoneBucket bucket = vol.getBucket(bucketName);
        int aclSize = bucket.getAcls().size();
        String[] args = new String[]{ "bucket", "update", ((((TestOzoneShell.url) + "/") + (vol.getName())) + "/") + bucketName, "--addAcl", "user:frodo:rw,group:samwise:r" };
        execute(TestOzoneShell.shell, args);
        String output = out.toString();
        Assert.assertTrue(((output.contains("createdOn")) && (output.contains(OZONE_TIME_ZONE))));
        bucket = vol.getBucket(bucketName);
        Assert.assertEquals((2 + aclSize), bucket.getAcls().size());
        OzoneAcl acl = bucket.getAcls().get(aclSize);
        Assert.assertTrue((((acl.getName().equals("frodo")) && ((acl.getType()) == (OzoneACLType.USER))) && ((acl.getRights()) == (OzoneACLRights.READ_WRITE))));
        args = new String[]{ "bucket", "update", ((((TestOzoneShell.url) + "/") + (vol.getName())) + "/") + bucketName, "--removeAcl", "user:frodo:rw" };
        execute(TestOzoneShell.shell, args);
        bucket = vol.getBucket(bucketName);
        acl = bucket.getAcls().get(aclSize);
        Assert.assertEquals((1 + aclSize), bucket.getAcls().size());
        Assert.assertTrue((((acl.getName().equals("samwise")) && ((acl.getType()) == (OzoneACLType.GROUP))) && ((acl.getRights()) == (OzoneACLRights.READ))));
        // test update bucket for a non-exist bucket
        args = new String[]{ "bucket", "update", (((TestOzoneShell.url) + "/") + (vol.getName())) + "/invalid-bucket", "--addAcl", "user:frodo:rw" };
        executeWithError(TestOzoneShell.shell, args, BUCKET_NOT_FOUND);
    }

    @Test
    public void testListBucket() throws Exception {
        TestOzoneShell.LOG.info("Running testListBucket");
        List<BucketInfo> buckets;
        String commandOutput;
        int bucketCount = 11;
        OzoneVolume vol = creatVolume();
        List<String> bucketNames = new ArrayList<>();
        // create bucket from test-bucket0 to test-bucket10
        for (int i = 0; i < bucketCount; i++) {
            String name = "test-bucket" + i;
            bucketNames.add(name);
            vol.createBucket(name);
            OzoneBucket bucket = vol.getBucket(name);
            Assert.assertNotNull(bucket);
        }
        // test listBucket with invalid volume name
        String[] args = new String[]{ "bucket", "list", (((TestOzoneShell.url) + "/") + (vol.getName())) + "/invalid-name" };
        executeWithError(TestOzoneShell.shell, args, ("Invalid volume name. " + "Delimiters (/) not allowed in volume name"));
        // test -length option
        args = new String[]{ "bucket", "list", ((TestOzoneShell.url) + "/") + (vol.getName()), "--length", "100" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        buckets = ((List<BucketInfo>) (JsonUtils.toJsonList(commandOutput, BucketInfo.class)));
        Assert.assertEquals(11, buckets.size());
        // sort bucket names since the return buckets isn't in created order
        Collections.sort(bucketNames);
        // return bucket names should be [test-bucket0, test-bucket1,
        // test-bucket10, test-bucket2, ,..., test-bucket9]
        for (int i = 0; i < (buckets.size()); i++) {
            Assert.assertEquals(buckets.get(i).getBucketName(), bucketNames.get(i));
            Assert.assertEquals(buckets.get(i).getVolumeName(), vol.getName());
            Assert.assertTrue(buckets.get(i).getCreatedOn().contains(OZONE_TIME_ZONE));
        }
        out.reset();
        args = new String[]{ "bucket", "list", ((TestOzoneShell.url) + "/") + (vol.getName()), "--length", "3" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        buckets = ((List<BucketInfo>) (JsonUtils.toJsonList(commandOutput, BucketInfo.class)));
        Assert.assertEquals(3, buckets.size());
        // return bucket names should be [test-bucket0,
        // test-bucket1, test-bucket10]
        Assert.assertEquals(buckets.get(0).getBucketName(), "test-bucket0");
        Assert.assertEquals(buckets.get(1).getBucketName(), "test-bucket1");
        Assert.assertEquals(buckets.get(2).getBucketName(), "test-bucket10");
        // test --prefix option
        out.reset();
        args = new String[]{ "bucket", "list", ((TestOzoneShell.url) + "/") + (vol.getName()), "--length", "100", "--prefix", "test-bucket1" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        buckets = ((List<BucketInfo>) (JsonUtils.toJsonList(commandOutput, BucketInfo.class)));
        Assert.assertEquals(2, buckets.size());
        // return bucket names should be [test-bucket1, test-bucket10]
        Assert.assertEquals(buckets.get(0).getBucketName(), "test-bucket1");
        Assert.assertEquals(buckets.get(1).getBucketName(), "test-bucket10");
        // test -start option
        out.reset();
        args = new String[]{ "bucket", "list", ((TestOzoneShell.url) + "/") + (vol.getName()), "--length", "100", "--start", "test-bucket7" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        buckets = ((List<BucketInfo>) (JsonUtils.toJsonList(commandOutput, BucketInfo.class)));
        Assert.assertEquals(2, buckets.size());
        Assert.assertEquals(buckets.get(0).getBucketName(), "test-bucket8");
        Assert.assertEquals(buckets.get(1).getBucketName(), "test-bucket9");
        // test error conditions
        err.reset();
        args = new String[]{ "bucket", "list", ((TestOzoneShell.url) + "/") + (vol.getName()), "--length", "-1" };
        executeWithError(TestOzoneShell.shell, args, "the length should be a positive number");
    }

    @Test
    public void testPutKey() throws Exception {
        TestOzoneShell.LOG.info("Running testPutKey");
        OzoneBucket bucket = creatBucket();
        String volumeName = bucket.getVolumeName();
        String bucketName = bucket.getName();
        String keyName = "key" + (RandomStringUtils.randomNumeric(5));
        String[] args = new String[]{ "key", "put", ((((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/") + keyName, createTmpFile() };
        execute(TestOzoneShell.shell, args);
        OzoneKey keyInfo = bucket.getKey(keyName);
        Assert.assertEquals(keyName, keyInfo.getName());
        // test put key in a non-exist bucket
        args = new String[]{ "key", "put", ((((TestOzoneShell.url) + "/") + volumeName) + "/invalid-bucket/") + keyName, createTmpFile() };
        executeWithError(TestOzoneShell.shell, args, BUCKET_NOT_FOUND);
    }

    @Test
    public void testGetKey() throws Exception {
        TestOzoneShell.LOG.info("Running testGetKey");
        String keyName = "key" + (RandomStringUtils.randomNumeric(5));
        OzoneBucket bucket = creatBucket();
        String volumeName = bucket.getVolumeName();
        String bucketName = bucket.getName();
        String dataStr = "test-data";
        OzoneOutputStream keyOutputStream = bucket.createKey(keyName, dataStr.length());
        keyOutputStream.write(dataStr.getBytes());
        keyOutputStream.close();
        String tmpPath = ((TestOzoneShell.baseDir.getAbsolutePath()) + "/testfile-") + (UUID.randomUUID().toString());
        String[] args = new String[]{ "key", "get", ((((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/") + keyName, tmpPath };
        execute(TestOzoneShell.shell, args);
        byte[] dataBytes = new byte[dataStr.length()];
        try (FileInputStream randFile = new FileInputStream(new File(tmpPath))) {
            randFile.read(dataBytes);
        }
        Assert.assertEquals(dataStr, DFSUtil.bytes2String(dataBytes));
        tmpPath = ((TestOzoneShell.baseDir.getAbsolutePath()) + (File.separatorChar)) + keyName;
        args = new String[]{ "key", "get", ((((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/") + keyName, TestOzoneShell.baseDir.getAbsolutePath() };
        execute(TestOzoneShell.shell, args);
        dataBytes = new byte[dataStr.length()];
        try (FileInputStream randFile = new FileInputStream(new File(tmpPath))) {
            randFile.read(dataBytes);
        }
        Assert.assertEquals(dataStr, DFSUtil.bytes2String(dataBytes));
    }

    @Test
    public void testDeleteKey() throws Exception {
        TestOzoneShell.LOG.info("Running testDeleteKey");
        String keyName = "key" + (RandomStringUtils.randomNumeric(5));
        OzoneBucket bucket = creatBucket();
        String volumeName = bucket.getVolumeName();
        String bucketName = bucket.getName();
        String dataStr = "test-data";
        OzoneOutputStream keyOutputStream = bucket.createKey(keyName, dataStr.length());
        keyOutputStream.write(dataStr.getBytes());
        keyOutputStream.close();
        OzoneKey keyInfo = bucket.getKey(keyName);
        Assert.assertEquals(keyName, keyInfo.getName());
        String[] args = new String[]{ "key", "delete", ((((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/") + keyName };
        execute(TestOzoneShell.shell, args);
        // verify if key has been deleted in the bucket
        TestOzoneShell.assertKeyNotExists(bucket, keyName);
        // test delete key in a non-exist bucket
        args = new String[]{ "key", "delete", ((((TestOzoneShell.url) + "/") + volumeName) + "/invalid-bucket/") + keyName };
        executeWithError(TestOzoneShell.shell, args, BUCKET_NOT_FOUND);
        err.reset();
        // test delete a non-exist key in bucket
        args = new String[]{ "key", "delete", (((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/invalid-key" };
        executeWithError(TestOzoneShell.shell, args, KEY_NOT_FOUND);
    }

    @Test
    public void testRenameKey() throws Exception {
        TestOzoneShell.LOG.info("Running testRenameKey");
        OzoneBucket bucket = creatBucket();
        OzoneKey oldKey = createTestKey(bucket);
        String oldName = oldKey.getName();
        String newName = oldName + ".new";
        String[] args = new String[]{ "key", "rename", String.format("%s/%s/%s", TestOzoneShell.url, oldKey.getVolumeName(), oldKey.getBucketName()), oldName, newName };
        execute(TestOzoneShell.shell, args);
        OzoneKey newKey = bucket.getKey(newName);
        Assert.assertEquals(oldKey.getCreationTime(), newKey.getCreationTime());
        Assert.assertEquals(oldKey.getDataSize(), newKey.getDataSize());
        TestOzoneShell.assertKeyNotExists(bucket, oldName);
    }

    @Test
    public void testInfoKeyDetails() throws Exception {
        TestOzoneShell.LOG.info("Running testInfoKey");
        String keyName = "key" + (RandomStringUtils.randomNumeric(5));
        OzoneBucket bucket = creatBucket();
        String volumeName = bucket.getVolumeName();
        String bucketName = bucket.getName();
        String dataStr = "test-data";
        OzoneOutputStream keyOutputStream = bucket.createKey(keyName, dataStr.length());
        keyOutputStream.write(dataStr.getBytes());
        keyOutputStream.close();
        String[] args = new String[]{ "key", "info", ((((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/") + keyName };
        // verify the response output
        execute(TestOzoneShell.shell, args);
        String output = out.toString();
        Assert.assertTrue(output.contains(keyName));
        Assert.assertTrue((((output.contains("createdOn")) && (output.contains("modifiedOn"))) && (output.contains(OZONE_TIME_ZONE))));
        Assert.assertTrue(((((output.contains("containerID")) && (output.contains("localID"))) && (output.contains("length"))) && (output.contains("offset"))));
        // reset stream
        out.reset();
        err.reset();
        // get the info of a non-exist key
        args = new String[]{ "key", "info", (((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/invalid-key" };
        // verify the response output
        // get the non-exist key info should be failed
        executeWithError(TestOzoneShell.shell, args, KEY_NOT_FOUND);
    }

    @Test
    public void testInfoDirKey() throws Exception {
        TestOzoneShell.LOG.info("Running testInfoKey for Dir Key");
        String dirKeyName = "test/";
        String keyNameOnly = "test";
        OzoneBucket bucket = creatBucket();
        String volumeName = bucket.getVolumeName();
        String bucketName = bucket.getName();
        String dataStr = "test-data";
        OzoneOutputStream keyOutputStream = bucket.createKey(dirKeyName, dataStr.length());
        keyOutputStream.write(dataStr.getBytes());
        keyOutputStream.close();
        String[] args = new String[]{ "key", "info", ((((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/") + dirKeyName };
        // verify the response output
        execute(TestOzoneShell.shell, args);
        String output = out.toString();
        Assert.assertTrue(output.contains(dirKeyName));
        Assert.assertTrue((((output.contains("createdOn")) && (output.contains("modifiedOn"))) && (output.contains(OZONE_TIME_ZONE))));
        args = new String[]{ "key", "info", ((((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/") + keyNameOnly };
        executeWithError(TestOzoneShell.shell, args, KEY_NOT_FOUND);
        out.reset();
        err.reset();
    }

    @Test
    public void testListKey() throws Exception {
        TestOzoneShell.LOG.info("Running testListKey");
        String commandOutput;
        List<KeyInfo> keys;
        int keyCount = 11;
        OzoneBucket bucket = creatBucket();
        String volumeName = bucket.getVolumeName();
        String bucketName = bucket.getName();
        String keyName;
        List<String> keyNames = new ArrayList<>();
        for (int i = 0; i < keyCount; i++) {
            keyName = "test-key" + i;
            keyNames.add(keyName);
            String dataStr = "test-data";
            OzoneOutputStream keyOutputStream = bucket.createKey(keyName, dataStr.length());
            keyOutputStream.write(dataStr.getBytes());
            keyOutputStream.close();
        }
        // test listKey with invalid bucket name
        String[] args = new String[]{ "key", "list", (((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName) + "/invalid-name" };
        executeWithError(TestOzoneShell.shell, args, ("Invalid bucket name. " + "Delimiters (/) not allowed in bucket name"));
        // test -length option
        args = new String[]{ "key", "list", ((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName, "--length", "100" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        keys = ((List<KeyInfo>) (JsonUtils.toJsonList(commandOutput, KeyInfo.class)));
        Assert.assertEquals(11, keys.size());
        // sort key names since the return keys isn't in created order
        Collections.sort(keyNames);
        // return key names should be [test-key0, test-key1,
        // test-key10, test-key2, ,..., test-key9]
        for (int i = 0; i < (keys.size()); i++) {
            Assert.assertEquals(keys.get(i).getKeyName(), keyNames.get(i));
            // verify the creation/modification time of key
            Assert.assertTrue(keys.get(i).getCreatedOn().contains(OZONE_TIME_ZONE));
            Assert.assertTrue(keys.get(i).getModifiedOn().contains(OZONE_TIME_ZONE));
        }
        out.reset();
        args = new String[]{ "key", "list", ((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName, "--length", "3" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        keys = ((List<KeyInfo>) (JsonUtils.toJsonList(commandOutput, KeyInfo.class)));
        Assert.assertEquals(3, keys.size());
        // return key names should be [test-key0, test-key1, test-key10]
        Assert.assertEquals(keys.get(0).getKeyName(), "test-key0");
        Assert.assertEquals(keys.get(1).getKeyName(), "test-key1");
        Assert.assertEquals(keys.get(2).getKeyName(), "test-key10");
        // test --prefix option
        out.reset();
        args = new String[]{ "key", "list", ((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName, "--length", "100", "--prefix", "test-key1" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        keys = ((List<KeyInfo>) (JsonUtils.toJsonList(commandOutput, KeyInfo.class)));
        Assert.assertEquals(2, keys.size());
        // return key names should be [test-key1, test-key10]
        Assert.assertEquals(keys.get(0).getKeyName(), "test-key1");
        Assert.assertEquals(keys.get(1).getKeyName(), "test-key10");
        // test -start option
        out.reset();
        args = new String[]{ "key", "list", ((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName, "--length", "100", "--start", "test-key7" };
        execute(TestOzoneShell.shell, args);
        commandOutput = out.toString();
        keys = ((List<KeyInfo>) (JsonUtils.toJsonList(commandOutput, KeyInfo.class)));
        Assert.assertEquals(keys.get(0).getKeyName(), "test-key8");
        Assert.assertEquals(keys.get(1).getKeyName(), "test-key9");
        // test error conditions
        err.reset();
        args = new String[]{ "key", "list", ((((TestOzoneShell.url) + "/") + volumeName) + "/") + bucketName, "--length", "-1" };
        executeWithError(TestOzoneShell.shell, args, "the length should be a positive number");
    }

    @Test
    public void testS3BucketMapping() throws IOException {
        String setOmAddress = (("--set=" + (OMConfigKeys.OZONE_OM_ADDRESS_KEY)) + "=") + (getOmAddress());
        String s3Bucket = "bucket1";
        String commandOutput;
        createS3Bucket("ozone", s3Bucket);
        // WHEN
        String[] args = new String[]{ setOmAddress, "bucket", "path", s3Bucket };
        execute(TestOzoneShell.shell, args);
        // THEN
        commandOutput = out.toString();
        String volumeName = TestOzoneShell.client.getOzoneVolumeName(s3Bucket);
        Assert.assertTrue(commandOutput.contains(("Volume name for S3Bucket is : " + volumeName)));
        Assert.assertTrue(commandOutput.contains((((((OzoneConsts.OZONE_URI_SCHEME) + "://") + s3Bucket) + ".") + volumeName)));
        out.reset();
        // Trying to get map for an unknown bucket
        args = new String[]{ setOmAddress, "bucket", "path", "unknownbucket" };
        executeWithError(TestOzoneShell.shell, args, S3_BUCKET_NOT_FOUND);
        // No bucket name
        args = new String[]{ setOmAddress, "bucket", "path" };
        executeWithError(TestOzoneShell.shell, args, "Missing required parameter");
        // Invalid bucket name
        args = new String[]{ setOmAddress, "bucket", "path", "/asd/multipleslash" };
        executeWithError(TestOzoneShell.shell, args, S3_BUCKET_NOT_FOUND);
    }

    @Test
    public void testS3Secret() throws Exception {
        String setOmAddress = (("--set=" + (OMConfigKeys.OZONE_OM_ADDRESS_KEY)) + "=") + (getOmAddress());
        err.reset();
        String outputFirstAttempt;
        String outputSecondAttempt;
        // First attempt: If secrets are not found in database, they will be created
        String[] args = new String[]{ setOmAddress, "s3", "getsecret" };
        execute(TestOzoneShell.shell, args);
        outputFirstAttempt = out.toString();
        // Extracting awsAccessKey & awsSecret value from output
        String[] output = outputFirstAttempt.split("\n");
        String awsAccessKey = output[0].split("=")[1];
        String awsSecret = output[1].split("=")[1];
        Assert.assertTrue((((awsAccessKey != null) && ((awsAccessKey.length()) > 0)) && ((awsSecret != null) && ((awsSecret.length()) > 0))));
        out.reset();
        // Second attempt: Since secrets were created in previous attempt, it
        // should return the same value
        args = new String[]{ setOmAddress, "s3", "getsecret" };
        execute(TestOzoneShell.shell, args);
        outputSecondAttempt = out.toString();
        // verifying if secrets from both attempts are same
        Assert.assertTrue(outputFirstAttempt.equals(outputSecondAttempt));
    }

    @Test
    public void testTokenCommands() throws Exception {
        String omAdd = (("--set=" + (OMConfigKeys.OZONE_OM_ADDRESS_KEY)) + "=") + (getOmAddress());
        List<String[]> shellCommands = new ArrayList<>(4);
        // Case 1: Execution will fail when security is disabled.
        shellCommands.add(new String[]{ omAdd, "token", "get" });
        shellCommands.add(new String[]{ omAdd, "token", "renew" });
        shellCommands.add(new String[]{ omAdd, "token", "cancel" });
        shellCommands.add(new String[]{ omAdd, "token", "print" });
        shellCommands.forEach(( cmd) -> execute(cmd, ("Error:Token operations " + "work only")));
        String security = ("-D=" + (OzoneConfigKeys.OZONE_SECURITY_ENABLED_KEY)) + "=true";
        // Case 2: Execution of get token will fail when security is enabled but
        // OzoneManager is not setup correctly.
        execute(new String[]{ omAdd, security, "token", "get" }, "Error: Get delegation token operation failed.");
        // Clear all commands.
        shellCommands.clear();
        // Case 3: Execution of renew/cancel/print token will fail as token file
        // doesn't exist.
        shellCommands.add(new String[]{ omAdd, security, "token", "renew" });
        shellCommands.add(new String[]{ omAdd, security, "token", "cancel" });
        shellCommands.add(new String[]{ omAdd, security, "token", "print" });
        shellCommands.forEach(( cmd) -> execute(cmd, ("token " + "operation failed as token file:")));
        // Create corrupt token file.
        File testPath = GenericTestUtils.getTestDir();
        Files.createDirectories(testPath.toPath());
        Path tokenFile = Paths.get(testPath.toString(), "token.txt");
        String question = RandomStringUtils.random(100);
        Files.write(tokenFile, question.getBytes());
        // Clear all commands.
        shellCommands.clear();
        String file = "-t=" + (tokenFile.toString());
        // Case 4: Execution of renew/cancel/print token will fail if token file
        // is corrupt.
        shellCommands.add(new String[]{ omAdd, security, "token", "renew", file });
        shellCommands.add(new String[]{ omAdd, security, "token", "cancel", file });
        shellCommands.add(new String[]{ omAdd, security, "token", "print", file });
        shellCommands.forEach(( cmd) -> executeWithError(TestOzoneShell.shell, cmd, EOFException.class));
    }
}

