/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.client.rpc;


import ReplicationFactor.ONE;
import ReplicationType.STAND_ALONE;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.protocolPB.StorageContainerLocationProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdds.security.token.BlockTokenVerifier;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneKey;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.io.OzoneInputStream;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.om.OzoneManager;
import org.apache.hadoop.test.LambdaTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class is to test all the public facing APIs of Ozone Client.
 */
public class TestSecureOzoneRpcClient extends TestOzoneRpcClient {
    private static MiniOzoneCluster cluster = null;

    private static OzoneClient ozClient = null;

    private static ObjectStore store = null;

    private static OzoneManager ozoneManager;

    private static StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient;

    private static final String SCM_ID = UUID.randomUUID().toString();

    private static File testDir;

    private static OzoneConfiguration conf;

    /**
     * Tests successful completion of following operations when grpc block
     * token is used.
     * 1. getKey
     * 2. writeChunk
     */
    @Test
    public void testPutKeySuccessWithBlockToken() throws Exception {
        String volumeName = UUID.randomUUID().toString();
        String bucketName = UUID.randomUUID().toString();
        long currentTime = Time.now();
        String value = "sample value";
        TestSecureOzoneRpcClient.store.createVolume(volumeName);
        OzoneVolume volume = TestSecureOzoneRpcClient.store.getVolume(volumeName);
        volume.createBucket(bucketName);
        OzoneBucket bucket = volume.getBucket(bucketName);
        for (int i = 0; i < 10; i++) {
            String keyName = UUID.randomUUID().toString();
            try (OzoneOutputStream out = bucket.createKey(keyName, value.getBytes().length, STAND_ALONE, ONE, new HashMap())) {
                out.write(value.getBytes());
            }
            OzoneKey key = bucket.getKey(keyName);
            Assert.assertEquals(keyName, key.getName());
            byte[] fileContent;
            try (OzoneInputStream is = bucket.readKey(keyName)) {
                fileContent = new byte[value.getBytes().length];
                is.read(fileContent);
            }
            Assert.assertTrue(verifyRatisReplication(volumeName, bucketName, keyName, STAND_ALONE, ONE));
            Assert.assertEquals(value, new String(fileContent));
            Assert.assertTrue(((key.getCreationTime()) >= currentTime));
            Assert.assertTrue(((key.getModificationTime()) >= currentTime));
        }
    }

    /**
     * Tests failure in following operations when grpc block token is
     * not present.
     * 1. getKey
     * 2. writeChunk
     */
    @Test
    public void testKeyOpFailureWithoutBlockToken() throws Exception {
        String volumeName = UUID.randomUUID().toString();
        String bucketName = UUID.randomUUID().toString();
        String value = "sample value";
        BlockTokenVerifier.setTestStub(true);
        TestSecureOzoneRpcClient.store.createVolume(volumeName);
        OzoneVolume volume = TestSecureOzoneRpcClient.store.getVolume(volumeName);
        volume.createBucket(bucketName);
        OzoneBucket bucket = volume.getBucket(bucketName);
        for (int i = 0; i < 10; i++) {
            String keyName = UUID.randomUUID().toString();
            try (OzoneOutputStream out = bucket.createKey(keyName, value.getBytes().length, STAND_ALONE, ONE, new HashMap())) {
                LambdaTestUtils.intercept(IOException.class, ("UNAUTHENTICATED: Fail " + "to find any token "), () -> out.write(value.getBytes()));
            }
            OzoneKey key = bucket.getKey(keyName);
            Assert.assertEquals(keyName, key.getName());
            LambdaTestUtils.intercept(IOException.class, ("Failed to authenticate" + " with GRPC XceiverServer with Ozone block token."), () -> bucket.readKey(keyName));
        }
        BlockTokenVerifier.setTestStub(false);
    }
}

