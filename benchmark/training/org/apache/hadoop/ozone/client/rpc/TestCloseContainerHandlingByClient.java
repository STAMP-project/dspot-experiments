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
package org.apache.hadoop.ozone.client.rpc;


import HddsProtos.ReplicationFactor.ONE;
import HddsProtos.ReplicationFactor.THREE;
import ReplicationType.RATIS;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.io.KeyOutputStream;
import org.apache.hadoop.ozone.client.io.OzoneInputStream;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.container.ContainerTestHelper;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests Close Container Exception handling by Ozone Client.
 */
public class TestCloseContainerHandlingByClient {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf = new OzoneConfiguration();

    private static OzoneClient client;

    private static ObjectStore objectStore;

    private static int chunkSize;

    private static int blockSize;

    private static String volumeName;

    private static String bucketName;

    private static String keyString;

    @Test
    public void testBlockWritesWithFlushAndClose() throws Exception {
        String keyName = getKeyName();
        OzoneOutputStream key = createKey(keyName, RATIS, 0);
        // write data more than 1 chunk
        byte[] data = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, ((TestCloseContainerHandlingByClient.chunkSize) + ((TestCloseContainerHandlingByClient.chunkSize) / 2))).getBytes(StandardCharsets.UTF_8);
        key.write(data);
        Assert.assertTrue(((key.getOutputStream()) instanceof KeyOutputStream));
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestCloseContainerHandlingByClient.volumeName).setBucketName(TestCloseContainerHandlingByClient.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(ONE).setKeyName(keyName).build();
        waitForContainerClose(keyName, key);
        key.write(data);
        key.flush();
        key.close();
        // read the key from OM again and match the length.The length will still
        // be the equal to the original data size.
        OmKeyInfo keyInfo = TestCloseContainerHandlingByClient.cluster.getOzoneManager().lookupKey(keyArgs);
        Assert.assertEquals((2 * (data.length)), keyInfo.getDataSize());
        // Written the same data twice
        String dataString = new String(data, StandardCharsets.UTF_8);
        dataString = dataString.concat(dataString);
        validateData(keyName, dataString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testBlockWritesCloseConsistency() throws Exception {
        String keyName = getKeyName();
        OzoneOutputStream key = createKey(keyName, RATIS, 0);
        // write data more than 1 chunk
        byte[] data = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, ((TestCloseContainerHandlingByClient.chunkSize) + ((TestCloseContainerHandlingByClient.chunkSize) / 2))).getBytes(StandardCharsets.UTF_8);
        key.write(data);
        Assert.assertTrue(((key.getOutputStream()) instanceof KeyOutputStream));
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestCloseContainerHandlingByClient.volumeName).setBucketName(TestCloseContainerHandlingByClient.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(ONE).setKeyName(keyName).build();
        waitForContainerClose(keyName, key);
        key.close();
        // read the key from OM again and match the length.The length will still
        // be the equal to the original data size.
        OmKeyInfo keyInfo = TestCloseContainerHandlingByClient.cluster.getOzoneManager().lookupKey(keyArgs);
        Assert.assertEquals(data.length, keyInfo.getDataSize());
        validateData(keyName, data);
    }

    @Test
    public void testMultiBlockWrites() throws Exception {
        String keyName = getKeyName();
        OzoneOutputStream key = createKey(keyName, RATIS, (4 * (TestCloseContainerHandlingByClient.blockSize)));
        KeyOutputStream keyOutputStream = ((KeyOutputStream) (key.getOutputStream()));
        // With the initial size provided, it should have preallocated 4 blocks
        Assert.assertEquals(4, keyOutputStream.getStreamEntries().size());
        // write data more than 1 chunk
        byte[] data = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, (3 * (TestCloseContainerHandlingByClient.blockSize))).getBytes(StandardCharsets.UTF_8);
        Assert.assertEquals(data.length, (3 * (TestCloseContainerHandlingByClient.blockSize)));
        key.write(data);
        Assert.assertTrue(((key.getOutputStream()) instanceof KeyOutputStream));
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestCloseContainerHandlingByClient.volumeName).setBucketName(TestCloseContainerHandlingByClient.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(ONE).setKeyName(keyName).build();
        waitForContainerClose(keyName, key);
        // write 1 more block worth of data. It will fail and new block will be
        // allocated
        key.write(ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, TestCloseContainerHandlingByClient.blockSize).getBytes(StandardCharsets.UTF_8));
        key.close();
        // read the key from OM again and match the length.The length will still
        // be the equal to the original data size.
        OmKeyInfo keyInfo = TestCloseContainerHandlingByClient.cluster.getOzoneManager().lookupKey(keyArgs);
        List<OmKeyLocationInfo> keyLocationInfos = keyInfo.getKeyLocationVersions().get(0).getBlocksLatestVersionOnly();
        // Though we have written only block initially, the close will hit
        // closeContainerException and remaining data in the chunkOutputStream
        // buffer will be copied into a different allocated block and will be
        // committed.
        Assert.assertEquals(4, keyLocationInfos.size());
        Assert.assertEquals((4 * (TestCloseContainerHandlingByClient.blockSize)), keyInfo.getDataSize());
        for (OmKeyLocationInfo locationInfo : keyLocationInfos) {
            Assert.assertEquals(TestCloseContainerHandlingByClient.blockSize, locationInfo.getLength());
        }
    }

    @Test
    public void testMultiBlockWrites2() throws Exception {
        String keyName = getKeyName();
        OzoneOutputStream key = createKey(keyName, RATIS, (2 * (TestCloseContainerHandlingByClient.blockSize)));
        KeyOutputStream keyOutputStream = ((KeyOutputStream) (key.getOutputStream()));
        Assert.assertTrue(((key.getOutputStream()) instanceof KeyOutputStream));
        // With the initial size provided, it should have pre allocated 2 blocks
        Assert.assertEquals(2, keyOutputStream.getStreamEntries().size());
        String dataString = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, (2 * (TestCloseContainerHandlingByClient.blockSize)));
        byte[] data = dataString.getBytes(StandardCharsets.UTF_8);
        key.write(data);
        // 2 block are completely written to the DataNode in 3 blocks.
        // Data of length half of chunkSize resides in the chunkOutput stream buffer
        String dataString2 = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, TestCloseContainerHandlingByClient.chunkSize);
        key.write(dataString2.getBytes(StandardCharsets.UTF_8));
        key.flush();
        String dataString3 = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, TestCloseContainerHandlingByClient.chunkSize);
        key.write(dataString3.getBytes(StandardCharsets.UTF_8));
        key.flush();
        String dataString4 = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, (((TestCloseContainerHandlingByClient.chunkSize) * 1) / 2));
        key.write(dataString4.getBytes(StandardCharsets.UTF_8));
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestCloseContainerHandlingByClient.volumeName).setBucketName(TestCloseContainerHandlingByClient.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(THREE).setKeyName(keyName).build();
        waitForContainerClose(keyName, key);
        key.close();
        // read the key from OM again and match the length.The length will still
        // be the equal to the original data size.
        OmKeyInfo keyInfo = TestCloseContainerHandlingByClient.cluster.getOzoneManager().lookupKey(keyArgs);
        // Though we have written only block initially, the close will hit
        // closeContainerException and remaining data in the chunkOutputStream
        // buffer will be copied into a different allocated block and will be
        // committed.
        String dataCommitted = dataString.concat(dataString2).concat(dataString3).concat(dataString4);
        Assert.assertEquals(dataCommitted.getBytes(StandardCharsets.UTF_8).length, keyInfo.getDataSize());
        validateData(keyName, dataCommitted.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testMultiBlockWrites3() throws Exception {
        String keyName = getKeyName();
        int keyLen = 4 * (TestCloseContainerHandlingByClient.blockSize);
        OzoneOutputStream key = createKey(keyName, RATIS, keyLen);
        KeyOutputStream keyOutputStream = ((KeyOutputStream) (key.getOutputStream()));
        // With the initial size provided, it should have preallocated 4 blocks
        Assert.assertEquals(4, keyOutputStream.getStreamEntries().size());
        // write data 3 blocks and one more chunk
        byte[] writtenData = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, keyLen).getBytes(StandardCharsets.UTF_8);
        byte[] data = Arrays.copyOfRange(writtenData, 0, ((3 * (TestCloseContainerHandlingByClient.blockSize)) + (TestCloseContainerHandlingByClient.chunkSize)));
        Assert.assertEquals(data.length, ((3 * (TestCloseContainerHandlingByClient.blockSize)) + (TestCloseContainerHandlingByClient.chunkSize)));
        key.write(data);
        Assert.assertTrue(((key.getOutputStream()) instanceof KeyOutputStream));
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestCloseContainerHandlingByClient.volumeName).setBucketName(TestCloseContainerHandlingByClient.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(ONE).setKeyName(keyName).build();
        waitForContainerClose(keyName, key);
        // write 3 more chunks worth of data. It will fail and new block will be
        // allocated. This write completes 4 blocks worth of data written to key
        data = Arrays.copyOfRange(writtenData, ((3 * (TestCloseContainerHandlingByClient.blockSize)) + (TestCloseContainerHandlingByClient.chunkSize)), keyLen);
        key.write(data);
        key.close();
        // read the key from OM again and match the length and data.
        OmKeyInfo keyInfo = TestCloseContainerHandlingByClient.cluster.getOzoneManager().lookupKey(keyArgs);
        List<OmKeyLocationInfo> keyLocationInfos = keyInfo.getKeyLocationVersions().get(0).getBlocksLatestVersionOnly();
        OzoneVolume volume = TestCloseContainerHandlingByClient.objectStore.getVolume(TestCloseContainerHandlingByClient.volumeName);
        OzoneBucket bucket = volume.getBucket(TestCloseContainerHandlingByClient.bucketName);
        OzoneInputStream inputStream = bucket.readKey(keyName);
        byte[] readData = new byte[keyLen];
        inputStream.read(readData);
        Assert.assertArrayEquals(writtenData, readData);
        // Though we have written only block initially, the close will hit
        // closeContainerException and remaining data in the chunkOutputStream
        // buffer will be copied into a different allocated block and will be
        // committed.
        long length = 0;
        for (OmKeyLocationInfo locationInfo : keyLocationInfos) {
            length += locationInfo.getLength();
        }
        Assert.assertEquals((4 * (TestCloseContainerHandlingByClient.blockSize)), length);
    }

    @Test
    public void testBlockWriteViaRatis() throws Exception {
        String keyName = getKeyName();
        OzoneOutputStream key = createKey(keyName, RATIS, 0);
        byte[] data = ContainerTestHelper.getFixedLengthString(TestCloseContainerHandlingByClient.keyString, ((TestCloseContainerHandlingByClient.chunkSize) + ((TestCloseContainerHandlingByClient.chunkSize) / 2))).getBytes(StandardCharsets.UTF_8);
        key.write(data);
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestCloseContainerHandlingByClient.volumeName).setBucketName(TestCloseContainerHandlingByClient.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(THREE).setKeyName(keyName).build();
        Assert.assertTrue(((key.getOutputStream()) instanceof KeyOutputStream));
        waitForContainerClose(keyName, key);
        // Again Write the Data. This will throw an exception which will be handled
        // and new blocks will be allocated
        key.write(data);
        key.flush();
        // The write will fail but exception will be handled and length will be
        // updated correctly in OzoneManager once the steam is closed
        key.close();
        OmKeyInfo keyInfo = TestCloseContainerHandlingByClient.cluster.getOzoneManager().lookupKey(keyArgs);
        String dataString = new String(data, StandardCharsets.UTF_8);
        dataString = dataString.concat(dataString);
        Assert.assertEquals((2 * (data.length)), keyInfo.getDataSize());
        validateData(keyName, dataString.getBytes(StandardCharsets.UTF_8));
    }
}

