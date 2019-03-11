/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.??See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.??The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.??You may obtain a copy of the License at
 *
 * ???? http://www.apache.org/licenses/LICENSE-2.0
 *
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
import java.util.HashMap;
import java.util.UUID;
import org.apache.hadoop.crypto.key.kms.server.MiniKMS;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.protocolPB.StorageContainerLocationProtocolClientSideTranslatorPB;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.BucketArgs;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneKey;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.io.OzoneInputStream;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.om.OzoneManager;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class is to test all the public facing APIs of Ozone Client.
 */
public class TestOzoneAtRestEncryption extends TestOzoneRpcClient {
    private static MiniOzoneCluster cluster = null;

    private static MiniKMS miniKMS;

    private static OzoneClient ozClient = null;

    private static ObjectStore store = null;

    private static OzoneManager ozoneManager;

    private static StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient;

    private static final String SCM_ID = UUID.randomUUID().toString();

    private static File testDir;

    private static OzoneConfiguration conf;

    private static final String TEST_KEY = "key1";

    @Test
    public void testPutKeyWithEncryption() throws Exception {
        String volumeName = UUID.randomUUID().toString();
        String bucketName = UUID.randomUUID().toString();
        long currentTime = Time.now();
        String value = "sample value";
        TestOzoneAtRestEncryption.store.createVolume(volumeName);
        OzoneVolume volume = TestOzoneAtRestEncryption.store.getVolume(volumeName);
        BucketArgs bucketArgs = BucketArgs.newBuilder().setBucketEncryptionKey(TestOzoneAtRestEncryption.TEST_KEY).build();
        volume.createBucket(bucketName, bucketArgs);
        OzoneBucket bucket = volume.getBucket(bucketName);
        for (int i = 0; i < 1; i++) {
            String keyName = UUID.randomUUID().toString();
            try (OzoneOutputStream out = bucket.createKey(keyName, value.getBytes("UTF-8").length, STAND_ALONE, ONE, new HashMap())) {
                out.write(value.getBytes("UTF-8"));
            }
            OzoneKey key = bucket.getKey(keyName);
            Assert.assertEquals(keyName, key.getName());
            byte[] fileContent;
            int len = 0;
            try (OzoneInputStream is = bucket.readKey(keyName)) {
                fileContent = new byte[value.getBytes("UTF-8").length];
                len = is.read(fileContent);
            }
            Assert.assertEquals(len, value.length());
            Assert.assertTrue(verifyRatisReplication(volumeName, bucketName, keyName, STAND_ALONE, ONE));
            Assert.assertEquals(value, new String(fileContent, "UTF-8"));
            Assert.assertTrue(((key.getCreationTime()) >= currentTime));
            Assert.assertTrue(((key.getModificationTime()) >= currentTime));
        }
    }
}

