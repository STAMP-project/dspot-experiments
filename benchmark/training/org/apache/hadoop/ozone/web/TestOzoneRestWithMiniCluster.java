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
package org.apache.hadoop.ozone.web;


import org.apache.hadoop.hdds.client.ReplicationFactor;
import org.apache.hadoop.hdds.client.ReplicationType;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.protocol.ClientProtocol;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


/**
 * End-to-end testing of Ozone REST operations.
 */
public class TestOzoneRestWithMiniCluster {
    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static ClientProtocol client;

    private static ReplicationFactor replicationFactor = ReplicationFactor.ONE;

    private static ReplicationType replicationType = ReplicationType.RATIS;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testCreateAndGetVolume() throws Exception {
        createAndGetVolume();
    }

    @Test
    public void testCreateAndGetBucket() throws Exception {
        OzoneVolume volume = createAndGetVolume();
        createAndGetBucket(volume);
    }

    @Test
    public void testPutAndGetKey() throws Exception {
        String keyName = TestOzoneRestWithMiniCluster.nextId("key");
        String keyData = TestOzoneRestWithMiniCluster.nextId("data");
        OzoneVolume volume = createAndGetVolume();
        OzoneBucket bucket = createAndGetBucket(volume);
        putKey(bucket, keyName, keyData);
    }

    @Test
    public void testPutAndGetEmptyKey() throws Exception {
        String keyName = TestOzoneRestWithMiniCluster.nextId("key");
        String keyData = "";
        OzoneVolume volume = createAndGetVolume();
        OzoneBucket bucket = createAndGetBucket(volume);
        putKey(bucket, keyName, keyData);
    }

    @Test
    public void testPutAndGetMultiChunkKey() throws Exception {
        String keyName = TestOzoneRestWithMiniCluster.nextId("key");
        int keyDataLen = 3 * (OzoneConsts.CHUNK_SIZE);
        String keyData = TestOzoneRestWithMiniCluster.buildKeyData(keyDataLen);
        OzoneVolume volume = createAndGetVolume();
        OzoneBucket bucket = createAndGetBucket(volume);
        putKey(bucket, keyName, keyData);
    }

    @Test
    public void testPutAndGetMultiChunkKeyLastChunkPartial() throws Exception {
        String keyName = TestOzoneRestWithMiniCluster.nextId("key");
        int keyDataLen = ((int) (2.5 * (OzoneConsts.CHUNK_SIZE)));
        String keyData = TestOzoneRestWithMiniCluster.buildKeyData(keyDataLen);
        OzoneVolume volume = createAndGetVolume();
        OzoneBucket bucket = createAndGetBucket(volume);
        putKey(bucket, keyName, keyData);
    }

    @Test
    public void testReplaceKey() throws Exception {
        String keyName = TestOzoneRestWithMiniCluster.nextId("key");
        int keyDataLen = ((int) (2.5 * (OzoneConsts.CHUNK_SIZE)));
        String keyData = TestOzoneRestWithMiniCluster.buildKeyData(keyDataLen);
        OzoneVolume volume = createAndGetVolume();
        OzoneBucket bucket = createAndGetBucket(volume);
        putKey(bucket, keyName, keyData);
        // Replace key with data consisting of fewer chunks.
        keyDataLen = ((int) (1.5 * (OzoneConsts.CHUNK_SIZE)));
        keyData = TestOzoneRestWithMiniCluster.buildKeyData(keyDataLen);
        putKey(bucket, keyName, keyData);
        // Replace key with data consisting of more chunks.
        keyDataLen = ((int) (3.5 * (OzoneConsts.CHUNK_SIZE)));
        keyData = TestOzoneRestWithMiniCluster.buildKeyData(keyDataLen);
        putKey(bucket, keyName, keyData);
    }
}

