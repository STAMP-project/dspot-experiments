/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.messageformat;


import BlobType.DataBlob;
import BlobType.MetadataBlob;
import MessageFormatErrorCodes.Data_Corrupt;
import MessageFormatRecord.BlobEncryptionKey_Format_V1;
import MessageFormatRecord.BlobProperties_Format_V1;
import MessageFormatRecord.Blob_Format_V1;
import MessageFormatRecord.Blob_Format_V2;
import MessageFormatRecord.MessageHeader_Format_V1;
import MessageFormatRecord.MessageHeader_Format_V2;
import MessageFormatRecord.Metadata_Content_Format_V2;
import MessageFormatRecord.UserMetadata_Format_V1;
import TestUtils.RANDOM;
import UpdateRecord.Type;
import UpdateRecord.Type.DELETE;
import Utils.Infinite_Time;
import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.store.MockIdFactory;
import com.github.ambry.store.StoreKey;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.Crc32;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.Test;


public class MessageFormatRecordTest {
    @Test
    public void deserializeTest() {
        try {
            {
                // Test message header V1
                ByteBuffer header = ByteBuffer.allocate(MessageHeader_Format_V1.getHeaderSize());
                MessageHeader_Format_V1.serializeHeader(header, 1000, 10, (-1), 20, 30);
                header.flip();
                MessageFormatRecord.MessageFormatRecord.MessageHeader_Format_V1 format = new MessageFormatRecord.MessageFormatRecord.MessageHeader_Format_V1(header);
                Assert.assertEquals(format.getMessageSize(), 1000);
                Assert.assertEquals(format.getBlobPropertiesRecordRelativeOffset(), 10);
                Assert.assertEquals(format.getUserMetadataRecordRelativeOffset(), 20);
                Assert.assertEquals(format.getBlobRecordRelativeOffset(), 30);
                // corrupt message header V1
                header.put(10, ((byte) (1)));
                format = new MessageFormatRecord.MessageFormatRecord.MessageHeader_Format_V1(header);
                try {
                    format.verifyHeader();
                    Assert.assertEquals(true, false);
                } catch (MessageFormatException e) {
                    Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
                }
            }
            {
                // Test message header V2
                ByteBuffer header = ByteBuffer.allocate(MessageHeader_Format_V2.getHeaderSize());
                MessageHeader_Format_V2.serializeHeader(header, 1000, 5, 10, (-1), 20, 30);
                header.flip();
                MessageFormatRecord.MessageFormatRecord.MessageHeader_Format_V2 format = new MessageFormatRecord.MessageFormatRecord.MessageHeader_Format_V2(header);
                Assert.assertEquals(format.getMessageSize(), 1000);
                Assert.assertEquals(format.getBlobEncryptionKeyRecordRelativeOffset(), 5);
                Assert.assertEquals(format.getBlobPropertiesRecordRelativeOffset(), 10);
                Assert.assertEquals(format.getUserMetadataRecordRelativeOffset(), 20);
                Assert.assertEquals(format.getBlobRecordRelativeOffset(), 30);
                // corrupt message header V2
                header.put(10, ((byte) (1)));
                format = new MessageFormatRecord.MessageFormatRecord.MessageHeader_Format_V2(header);
                try {
                    format.verifyHeader();
                    Assert.fail("Corrupt header verification should have failed");
                } catch (MessageFormatException e) {
                    Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
                }
            }
            // Test blob encryption key record
            ByteBuffer blobEncryptionKey = ByteBuffer.allocate(1000);
            new Random().nextBytes(blobEncryptionKey.array());
            ByteBuffer output = ByteBuffer.allocate(BlobEncryptionKey_Format_V1.getBlobEncryptionKeyRecordSize(blobEncryptionKey));
            BlobEncryptionKey_Format_V1.serializeBlobEncryptionKeyRecord(output, blobEncryptionKey);
            output.flip();
            ByteBuffer bufOutput = MessageFormatRecord.MessageFormatRecord.deserializeBlobEncryptionKey(new ByteBufferInputStream(output));
            Assert.assertArrayEquals(blobEncryptionKey.array(), bufOutput.array());
            // Corrupt encryption key record
            output.flip();
            Byte currentRandomByte = output.get(10);
            output.put(10, ((byte) (currentRandomByte + 1)));
            try {
                MessageFormatRecord.MessageFormatRecord.deserializeBlobEncryptionKey(new ByteBufferInputStream(output));
                Assert.fail("Encryption key record deserialization should have failed for corrupt data");
            } catch (MessageFormatException e) {
                Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
            }
            // Test usermetadata V1 record
            ByteBuffer usermetadata = ByteBuffer.allocate(1000);
            new Random().nextBytes(usermetadata.array());
            output = ByteBuffer.allocate(UserMetadata_Format_V1.getUserMetadataSize(usermetadata));
            UserMetadata_Format_V1.serializeUserMetadataRecord(output, usermetadata);
            output.flip();
            bufOutput = MessageFormatRecord.MessageFormatRecord.deserializeUserMetadata(new ByteBufferInputStream(output));
            Assert.assertArrayEquals(usermetadata.array(), bufOutput.array());
            // corrupt usermetadata record V1
            output.flip();
            currentRandomByte = output.get(10);
            output.put(10, ((byte) (currentRandomByte + 1)));
            try {
                MessageFormatRecord.MessageFormatRecord.deserializeUserMetadata(new ByteBufferInputStream(output));
                Assert.assertEquals(true, false);
            } catch (MessageFormatException e) {
                Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
            }
            // Test blob record V1
            ByteBuffer data = ByteBuffer.allocate(2000);
            new Random().nextBytes(data.array());
            long size = Blob_Format_V1.getBlobRecordSize(2000);
            ByteBuffer sData = ByteBuffer.allocate(((int) (size)));
            Blob_Format_V1.serializePartialBlobRecord(sData, 2000);
            sData.put(data);
            Crc32 crc = new Crc32();
            crc.update(sData.array(), 0, sData.position());
            sData.putLong(crc.getValue());
            sData.flip();
            BlobData blobData = MessageFormatRecord.MessageFormatRecord.deserializeBlob(new ByteBufferInputStream(sData));
            Assert.assertEquals(blobData.getSize(), 2000);
            byte[] verify = new byte[2000];
            blobData.getStream().read(verify);
            Assert.assertArrayEquals(verify, data.array());
            // corrupt blob record V1
            sData.flip();
            currentRandomByte = sData.get(10);
            sData.put(10, ((byte) (currentRandomByte + 1)));
            try {
                MessageFormatRecord.MessageFormatRecord.deserializeBlob(new ByteBufferInputStream(sData));
                Assert.assertEquals(true, false);
            } catch (MessageFormatException e) {
                Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
            }
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    /**
     * Tests {@link MessageFormatRecord#BlobProperties_Version_V1} for different versions of {@link BlobPropertiesSerDe}
     *
     * @throws IOException
     * 		
     * @throws MessageFormatException
     * 		
     */
    @Test
    public void testBlobPropertyV1() throws MessageFormatException, IOException {
        // Test Blob property Format V1 for all versions of BlobPropertiesSerDe
        short[] versions = new short[]{ VERSION_1, VERSION_2, VERSION_3 };
        for (short version : versions) {
            BlobProperties properties;
            long blobSize = RANDOM.nextLong();
            long ttl = RANDOM.nextInt();
            boolean isEncrypted = RANDOM.nextBoolean();
            if (version == (VERSION_1)) {
                properties = new BlobProperties(blobSize, "id", "member", "test", true, ttl, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, isEncrypted, null);
            } else {
                short accountId = Utils.getRandomShort(RANDOM);
                short containerId = Utils.getRandomShort(RANDOM);
                properties = new BlobProperties(blobSize, "id", "member", "test", true, ttl, accountId, containerId, isEncrypted, null);
            }
            ByteBuffer stream;
            if (version == (VERSION_1)) {
                stream = ByteBuffer.allocate(getBlobPropertiesV1RecordSize(properties));
                serializeBlobPropertiesV1Record(stream, properties);
            } else
                if (version == (VERSION_2)) {
                    stream = ByteBuffer.allocate(getBlobPropertiesV2RecordSize(properties));
                    serializeBlobPropertiesV2Record(stream, properties);
                } else {
                    stream = ByteBuffer.allocate(getBlobPropertiesRecordSize(properties));
                    BlobProperties_Format_V1.serializeBlobPropertiesRecord(stream, properties);
                }

            stream.flip();
            BlobProperties result = MessageFormatRecord.MessageFormatRecord.deserializeBlobProperties(new ByteBufferInputStream(stream));
            Assert.assertEquals(properties.getBlobSize(), result.getBlobSize());
            Assert.assertEquals(properties.getContentType(), result.getContentType());
            Assert.assertEquals(properties.getCreationTimeInMs(), result.getCreationTimeInMs());
            Assert.assertEquals(properties.getOwnerId(), result.getOwnerId());
            Assert.assertEquals(properties.getServiceId(), result.getServiceId());
            Assert.assertEquals(properties.getAccountId(), result.getAccountId());
            Assert.assertEquals(properties.getContainerId(), result.getContainerId());
            if (version > (VERSION_2)) {
                Assert.assertEquals(properties.isEncrypted(), result.isEncrypted());
            }
            // corrupt blob property V1 record
            stream.flip();
            stream.put(10, ((byte) ((stream.get(10)) + 1)));
            try {
                MessageFormatRecord.MessageFormatRecord.deserializeBlobProperties(new ByteBufferInputStream(stream));
                Assert.fail("Deserialization of BlobProperties should have failed ");
            } catch (MessageFormatException e) {
                Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
            }
        }
        // failure case
        BlobProperties properties = new BlobProperties(1000, "id", "member", "test", true, Utils.Infinite_Time, Account.UNKNOWN_ACCOUNT_ID, Container.UNKNOWN_CONTAINER_ID, false, null);
        ByteBuffer stream = ByteBuffer.allocate(((getBlobPropertiesRecordSize(properties)) - 10));
        try {
            BlobProperties_Format_V1.serializeBlobPropertiesRecord(stream, properties);
            Assert.fail("Serialization of BlobProperties should have failed since the buffer does not have sufficient space");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests UpdateRecord V1 for serialization and deserialization
     *
     * @throws IOException
     * 		
     * @throws MessageFormatException
     * 		
     */
    @Test
    public void testUpdateRecordV1() throws MessageFormatException, IOException {
        // Test update V1 record
        // irrespective of what values are set for acccountId, containerId and updateTimeMs, legacy values will be returned
        // with Update_Format_V1
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        long updateTimeMs = (SystemTime.getInstance().milliseconds()) + (RANDOM.nextInt());
        ByteBuffer updateRecord = ByteBuffer.allocate(Update_Format_V1.getRecordSize());
        Update_Format_V1.serialize(updateRecord, new UpdateRecord(accountId, containerId, updateTimeMs, new DeleteSubRecord()));
        updateRecord.flip();
        UpdateRecord deserializeUpdateRecord = MessageFormatRecord.MessageFormatRecord.deserializeUpdateRecord(new ByteBufferInputStream(updateRecord));
        Assert.assertEquals("AccountId mismatch ", UNKNOWN_ACCOUNT_ID, deserializeUpdateRecord.getAccountId());
        Assert.assertEquals("ContainerId mismatch ", UNKNOWN_CONTAINER_ID, deserializeUpdateRecord.getContainerId());
        Assert.assertEquals("DeletionTime mismatch ", Infinite_Time, deserializeUpdateRecord.getUpdateTimeInMs());
        Assert.assertEquals("Type of update record incorrect", DELETE, deserializeUpdateRecord.getType());
        Assert.assertNotNull("DeleteSubRecord is null", deserializeUpdateRecord.getDeleteSubRecord());
        // corrupt update V1 record
        updateRecord.flip();
        byte toCorrupt = updateRecord.get(10);
        updateRecord.put(10, ((byte) (toCorrupt + 1)));
        try {
            MessageFormatRecord.MessageFormatRecord.deserializeUpdateRecord(new ByteBufferInputStream(updateRecord));
            Assert.fail("Deserialization of a corrupt update record V1 should have failed ");
        } catch (MessageFormatException e) {
            Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
        }
    }

    /**
     * Tests UpdateRecord V2 for serialization and deserialization
     *
     * @throws IOException
     * 		
     * @throws MessageFormatException
     * 		
     */
    @Test
    public void testUpdateRecordV2() throws MessageFormatException, IOException {
        // Test update V2 record
        ByteBuffer updateRecord = ByteBuffer.allocate(Update_Format_V2.getRecordSize());
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        long updateTimeMs = (SystemTime.getInstance().milliseconds()) + (RANDOM.nextInt());
        Update_Format_V2.serialize(updateRecord, new UpdateRecord(accountId, containerId, updateTimeMs, new DeleteSubRecord()));
        updateRecord.flip();
        UpdateRecord deserializeUpdateRecord = MessageFormatRecord.MessageFormatRecord.deserializeUpdateRecord(new ByteBufferInputStream(updateRecord));
        Assert.assertEquals("AccountId mismatch ", accountId, deserializeUpdateRecord.getAccountId());
        Assert.assertEquals("ContainerId mismatch ", containerId, deserializeUpdateRecord.getContainerId());
        Assert.assertEquals("DeletionTime mismatch ", updateTimeMs, deserializeUpdateRecord.getUpdateTimeInMs());
        Assert.assertEquals("Type of update record incorrect", DELETE, deserializeUpdateRecord.getType());
        Assert.assertNotNull("DeleteSubRecord is null", deserializeUpdateRecord.getDeleteSubRecord());
        // corrupt update V2 record
        updateRecord.flip();
        byte toCorrupt = updateRecord.get(10);
        updateRecord.put(10, ((byte) (toCorrupt + 1)));
        try {
            MessageFormatRecord.MessageFormatRecord.deserializeUpdateRecord(new ByteBufferInputStream(updateRecord));
            Assert.fail("Deserialization of a corrupt update record V2 should have failed ");
        } catch (MessageFormatException e) {
            Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
        }
    }

    /**
     * Tests UpdateRecord V3 for serialization and deserialization
     *
     * @throws IOException
     * 		
     * @throws MessageFormatException
     * 		
     */
    @Test
    public void testUpdateRecordV3() throws MessageFormatException, IOException {
        // Test update V3 record
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        long updateTimeMs = (SystemTime.getInstance().milliseconds()) + (RANDOM.nextInt());
        long updatedExpiryTimesMs = (SystemTime.getInstance().milliseconds()) + (RANDOM.nextInt());
        for (UpdateRecord.Type type : Type.values()) {
            ByteBuffer updateRecordBuf = ByteBuffer.allocate(Update_Format_V3.getRecordSize(type));
            UpdateRecord updateRecord = null;
            switch (type) {
                case DELETE :
                    DeleteSubRecord deleteSubRecord = new DeleteSubRecord();
                    updateRecord = new UpdateRecord(accountId, containerId, updateTimeMs, deleteSubRecord);
                    break;
                case TTL_UPDATE :
                    TtlUpdateSubRecord ttlUpdateSubRecord = new TtlUpdateSubRecord(updatedExpiryTimesMs);
                    updateRecord = new UpdateRecord(accountId, containerId, updateTimeMs, ttlUpdateSubRecord);
                    break;
                default :
                    Assert.fail(("Unknown update record type: " + type));
            }
            Update_Format_V3.serialize(updateRecordBuf, updateRecord);
            updateRecordBuf.flip();
            UpdateRecord deserializeUpdateRecord = MessageFormatRecord.MessageFormatRecord.deserializeUpdateRecord(new ByteBufferInputStream(updateRecordBuf));
            Assert.assertEquals("AccountId mismatch ", accountId, deserializeUpdateRecord.getAccountId());
            Assert.assertEquals("ContainerId mismatch ", containerId, deserializeUpdateRecord.getContainerId());
            Assert.assertEquals("UpdateTime mismatch ", updateTimeMs, deserializeUpdateRecord.getUpdateTimeInMs());
            Assert.assertEquals("Type of update record incorrect", type, deserializeUpdateRecord.getType());
            switch (type) {
                case DELETE :
                    Assert.assertNotNull("DeleteSubRecord is null", deserializeUpdateRecord.getDeleteSubRecord());
                    break;
                case TTL_UPDATE :
                    TtlUpdateSubRecord ttlUpdateSubRecord = deserializeUpdateRecord.getTtlUpdateSubRecord();
                    Assert.assertNotNull("TtlUpdateSubRecord is null", ttlUpdateSubRecord);
                    Assert.assertEquals("Updated expiry time is incorrect", updatedExpiryTimesMs, ttlUpdateSubRecord.getUpdatedExpiryTimeMs());
                    break;
                default :
                    Assert.fail(("Unknown update record type: " + type));
            }
            // corrupt update V3 record
            updateRecordBuf.flip();
            byte toCorrupt = updateRecordBuf.get(10);
            updateRecordBuf.put(10, ((byte) (toCorrupt + 1)));
            try {
                MessageFormatRecord.MessageFormatRecord.deserializeUpdateRecord(new ByteBufferInputStream(updateRecordBuf));
                Assert.fail("Deserialization of a corrupt update record V3 should have failed ");
            } catch (MessageFormatException e) {
                Assert.assertEquals(e.getErrorCode(), Data_Corrupt);
            }
        }
    }

    @Test
    public void testMetadataContentRecordV2() throws MessageFormatException, IOException {
        // Test Metadata Blob V2
        List<StoreKey> keys = getKeys(60, 5);
        int[] chunkSizes = new int[]{ ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE), 15 };
        long[] totalSizes = new long[]{ ((long) (keys.size())) * (chunkSizes[0]), (((long) (keys.size())) * (chunkSizes[1])) - 11 };
        for (int i = 0; i < (chunkSizes.length); i++) {
            ByteBuffer metadataContent = getSerializedMetadataContentV2(chunkSizes[i], totalSizes[i], keys);
            CompositeBlobInfo compositeBlobInfo = deserializeMetadataContentV2(metadataContent, new MockIdFactory());
            Assert.assertEquals("Chunk size doesn't match", chunkSizes[i], compositeBlobInfo.getChunkSize());
            Assert.assertEquals("Total size doesn't match", totalSizes[i], compositeBlobInfo.getTotalSize());
            Assert.assertEquals("List of keys dont match", keys, compositeBlobInfo.getKeys());
            // no testing of corruption as the metadata content record doesn't have crc
        }
    }

    @Test
    public void testInvalidMetadataContentV2Fields() {
        List<StoreKey> keys = getKeys(60, 5);
        int[] chunkSizes = new int[]{ 0, 5, 10, 10 };
        long[] totalSizes = new long[]{ 5, -10, (10 * (keys.size())) - 10, (10 * (keys.size())) + 1 };
        for (int n = 0; n < (chunkSizes.length); n++) {
            try {
                MetadataContentSerDe.serializeMetadataContent(chunkSizes[n], totalSizes[n], keys);
                Assert.fail("Should have failed to serialize");
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Test
    public void testBlobRecordV2() throws MessageFormatException, IOException {
        // Test blob record V2 for Data Blob
        testBlobRecordV2(2000, DataBlob);
        // Test blob record V2 for Metadata Blob
        testBlobRecordV2(2000, MetadataBlob);
    }

    @Test
    public void testBlobRecordWithMetadataContentV2() throws MessageFormatException, IOException {
        // Test Blob V2 with actual metadata blob V2
        // construct metadata blob
        List<StoreKey> keys = getKeys(60, 5);
        int[] chunkSizes = new int[]{ ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE), 15 };
        long[] totalSizes = new long[]{ ((long) (keys.size())) * (chunkSizes[0]), (((long) (keys.size())) * (chunkSizes[1])) - 11 };
        for (int i = 0; i < (chunkSizes.length); i++) {
            ByteBuffer metadataContent = getSerializedMetadataContentV2(chunkSizes[i], totalSizes[i], keys);
            int metadataContentSize = Metadata_Content_Format_V2.getMetadataContentSize(keys.get(0).sizeInBytes(), keys.size());
            long blobSize = Blob_Format_V2.getBlobRecordSize(metadataContentSize);
            ByteBuffer blob = ByteBuffer.allocate(((int) (blobSize)));
            BlobData blobData = getBlobRecordV2(metadataContentSize, MetadataBlob, metadataContent, blob);
            Assert.assertEquals(metadataContentSize, blobData.getSize());
            byte[] verify = new byte[metadataContentSize];
            blobData.getStream().read(verify);
            Assert.assertArrayEquals("Metadata content mismatch", metadataContent.array(), verify);
            // deserialize and check for metadata contents
            metadataContent.rewind();
            CompositeBlobInfo compositeBlobInfo = deserializeMetadataContentV2(metadataContent, new MockIdFactory());
            Assert.assertEquals("Chunk size doesn't match", chunkSizes[i], compositeBlobInfo.getChunkSize());
            Assert.assertEquals("Total size doesn't match", totalSizes[i], compositeBlobInfo.getTotalSize());
            Assert.assertEquals("List of keys dont match", keys, compositeBlobInfo.getKeys());
            testBlobCorruption(blob, blobSize, metadataContentSize);
        }
    }
}

