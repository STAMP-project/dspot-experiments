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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.web.client;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hdds.client.ReplicationFactor;
import org.apache.hadoop.hdds.client.ReplicationType;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OzoneAcl;
import org.apache.hadoop.ozone.client.BucketArgs;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.VolumeArgs;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.client.protocol.ClientProtocol;
import org.apache.hadoop.ozone.web.utils.OzoneUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test Ozone Key Lifecycle.
 */
public class TestKeys {
    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static OzoneConfiguration conf;

    private static MiniOzoneCluster ozoneCluster = null;

    private static String path;

    private static ClientProtocol client = null;

    private static long currentTime;

    private static ReplicationFactor replicationFactor = ReplicationFactor.ONE;

    private static ReplicationType replicationType = ReplicationType.STAND_ALONE;

    static class PutHelper {
        private final ClientProtocol client;

        private final String dir;

        private final String keyName;

        private OzoneVolume vol;

        private OzoneBucket bucket;

        private File file;

        PutHelper(ClientProtocol client, String dir) {
            this(client, dir, OzoneUtils.getRequestID().toLowerCase());
        }

        PutHelper(ClientProtocol client, String dir, String key) {
            this.client = client;
            this.dir = dir;
            this.keyName = key;
        }

        public OzoneVolume getVol() {
            return vol;
        }

        public OzoneBucket getBucket() {
            return bucket;
        }

        public File getFile() {
            return file;
        }

        /**
         * This function is reused in all other tests.
         *
         * @return Returns the name of the new key that was created.
         * @throws OzoneException
         * 		
         */
        private String putKey() throws Exception {
            String volumeName = OzoneUtils.getRequestID().toLowerCase();
            VolumeArgs volumeArgs = VolumeArgs.newBuilder().setOwner("bilbo").setQuota("100TB").setAdmin("hdfs").build();
            client.createVolume(volumeName, volumeArgs);
            vol = client.getVolumeDetails(volumeName);
            String[] acls = new String[]{ "user:frodo:rw", "user:samwise:rw" };
            String bucketName = OzoneUtils.getRequestID().toLowerCase();
            List<OzoneAcl> aclList = Arrays.stream(acls).map(( acl) -> OzoneAcl.parseAcl(acl)).collect(Collectors.toList());
            BucketArgs bucketArgs = BucketArgs.newBuilder().setAcls(aclList).build();
            vol.createBucket(bucketName, bucketArgs);
            bucket = vol.getBucket(bucketName);
            String fileName = OzoneUtils.getRequestID().toLowerCase();
            file = TestKeys.createRandomDataFile(dir, fileName, 1024);
            try (OzoneOutputStream ozoneOutputStream = bucket.createKey(keyName, 0, TestKeys.replicationType, TestKeys.replicationFactor, new HashMap());InputStream fileInputStream = new FileInputStream(file)) {
                IOUtils.copy(fileInputStream, ozoneOutputStream);
            }
            return keyName;
        }
    }

    @Test
    public void testPutKey() throws Exception {
        // Test non-delimited keys
        TestKeys.runTestPutKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path));
        // Test key delimited by a random delimiter
        String delimiter = RandomStringUtils.randomAscii(1);
        TestKeys.runTestPutKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testPutAndGetKeyWithDnRestart() throws Exception {
        TestKeys.runTestPutAndGetKeyWithDnRestart(new TestKeys.PutHelper(TestKeys.client, TestKeys.path), TestKeys.ozoneCluster);
        String delimiter = RandomStringUtils.randomAscii(1);
        TestKeys.runTestPutAndGetKeyWithDnRestart(new TestKeys.PutHelper(TestKeys.client, TestKeys.path, TestKeys.getMultiPartKey(delimiter)), TestKeys.ozoneCluster);
    }

    @Test
    public void testPutAndGetKey() throws Exception {
        TestKeys.runTestPutAndGetKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path));
        String delimiter = RandomStringUtils.randomAscii(1);
        TestKeys.runTestPutAndGetKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testPutAndDeleteKey() throws Exception {
        TestKeys.runTestPutAndDeleteKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path));
        String delimiter = RandomStringUtils.randomAscii(1);
        TestKeys.runTestPutAndDeleteKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testPutAndListKey() throws Exception {
        TestKeys.runTestPutAndListKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path));
        String delimiter = RandomStringUtils.randomAscii(1);
        TestKeys.runTestPutAndListKey(new TestKeys.PutHelper(TestKeys.client, TestKeys.path, TestKeys.getMultiPartKey(delimiter)));
    }

    @Test
    public void testGetKeyInfo() throws Exception {
        TestKeys.runTestGetKeyInfo(new TestKeys.PutHelper(TestKeys.client, TestKeys.path));
        String delimiter = RandomStringUtils.randomAscii(1);
        TestKeys.runTestGetKeyInfo(new TestKeys.PutHelper(TestKeys.client, TestKeys.path, TestKeys.getMultiPartKey(delimiter)));
    }

    // Volume, bucket, keys info that helps for test create/delete keys.
    private static class BucketKeys {
        private Map<Pair<String, String>, List<String>> buckets;

        BucketKeys() {
            buckets = Maps.newHashMap();
        }

        void addKey(String volume, String bucket, String key) {
            // check if this bucket exists
            for (Map.Entry<Pair<String, String>, List<String>> entry : buckets.entrySet()) {
                if (entry.getKey().getValue().equals(bucket)) {
                    entry.getValue().add(key);
                    return;
                }
            }
            // bucket not exist
            Pair<String, String> newBucket = new ImmutablePair(volume, bucket);
            List<String> keyList = Lists.newArrayList();
            keyList.add(key);
            buckets.put(newBucket, keyList);
        }

        Set<Pair<String, String>> getAllBuckets() {
            return buckets.keySet();
        }

        List<String> getBucketKeys(String bucketName) {
            for (Map.Entry<Pair<String, String>, List<String>> entry : buckets.entrySet()) {
                if (entry.getKey().getValue().equals(bucketName)) {
                    return entry.getValue();
                }
            }
            return Lists.newArrayList();
        }

        int totalNumOfKeys() {
            int count = 0;
            for (Map.Entry<Pair<String, String>, List<String>> entry : buckets.entrySet()) {
                count += entry.getValue().size();
            }
            return count;
        }
    }
}

