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
import MessageFormatRecord.Blob_Version_V1;
import MessageFormatRecord.Blob_Version_V2;
import MessageFormatRecord.Message_Header_Invalid_Relative_Offset;
import MessageFormatRecord.Update_Format_V1;
import MessageFormatRecord.Update_Format_V2;
import MessageFormatRecord.Update_Format_V3;
import MessageFormatRecord.headerVersionToUse;
import TestUtils.RANDOM;
import UpdateRecord.Type.DELETE;
import UpdateRecord.Type.TTL_UPDATE;
import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.store.MockId;
import com.github.ambry.store.MockIdFactory;
import com.github.ambry.store.StoreKey;
import com.github.ambry.store.StoreKeyFactory;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.Crc32;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static BlobType.DataBlob;
import static MessageFormatRecord.Crc_Size;
import static MessageFormatRecord.Message_Header_Version_V1;
import static MessageFormatRecord.Message_Header_Version_V2;
import static MessageFormatRecord.Update_Version_V1;
import static MessageFormatRecord.Update_Version_V2;
import static MessageFormatRecord.Update_Version_V3;
import static MessageFormatRecord.headerVersionToUse;


public class MessageFormatInputStreamTest {
    private static short messageFormatHeaderVersionSaved;

    /**
     * Tests for {@link PutMessageFormatInputStream} in different versions.
     */
    @Test
    public void messageFormatRecordsTest() throws MessageFormatException, IOException {
        messageFormatRecordsTest(Blob_Version_V1, DataBlob, false);
        messageFormatRecordsTest(Blob_Version_V2, DataBlob, false);
        messageFormatRecordsTest(Blob_Version_V2, MetadataBlob, false);
        messageFormatRecordsTest(Blob_Version_V2, DataBlob, true);
        messageFormatRecordsTest(Blob_Version_V2, MetadataBlob, true);
    }

    /**
     * Tests for {@link DeleteMessageFormatInputStream} in different versions.
     */
    @Test
    public void messageFormatDeleteRecordTest() throws MessageFormatException, IOException {
        short[] versions = new short[]{ Update_Version_V1, Update_Version_V2, Update_Version_V3 };
        for (short version : versions) {
            StoreKey key = new MockId("id1");
            short accountId = Utils.getRandomShort(RANDOM);
            short containerId = Utils.getRandomShort(RANDOM);
            long deletionTimeMs = (SystemTime.getInstance().milliseconds()) + (RANDOM.nextInt());
            MessageFormatInputStream messageFormatStream;
            boolean useV2Header;
            int deleteRecordSize;
            if (version == (Update_Version_V1)) {
                messageFormatStream = new DeleteMessageFormatV1InputStream(key, accountId, containerId, deletionTimeMs);
                deleteRecordSize = Update_Format_V1.getRecordSize();
                useV2Header = false;
                // reset account, container ids and time
                accountId = Account.UNKNOWN_ACCOUNT_ID;
                containerId = Container.UNKNOWN_CONTAINER_ID;
                deletionTimeMs = Utils.Infinite_Time;
            } else
                if (version == (Update_Version_V2)) {
                    messageFormatStream = new DeleteMessageFormatV2InputStream(key, accountId, containerId, deletionTimeMs);
                    deleteRecordSize = Update_Format_V2.getRecordSize();
                    useV2Header = (headerVersionToUse) == (Message_Header_Version_V2);
                } else {
                    messageFormatStream = new DeleteMessageFormatInputStream(key, accountId, containerId, deletionTimeMs);
                    deleteRecordSize = Update_Format_V3.getRecordSize(DELETE);
                    useV2Header = (headerVersionToUse) == (Message_Header_Version_V2);
                }

            int headerSize = MessageFormatRecord.getHeaderSizeForVersion((useV2Header ? Message_Header_Version_V2 : Message_Header_Version_V1));
            Assert.assertEquals(("Unexpected size for version " + version), ((headerSize + deleteRecordSize) + (key.sizeInBytes())), messageFormatStream.getSize());
            // check header
            byte[] headerOutput = new byte[headerSize];
            messageFormatStream.read(headerOutput);
            ByteBuffer headerBuf = ByteBuffer.wrap(headerOutput);
            Assert.assertEquals((useV2Header ? Message_Header_Version_V2 : Message_Header_Version_V1), headerBuf.getShort());
            Assert.assertEquals(deleteRecordSize, headerBuf.getLong());
            // read encryption key relative offset
            if (useV2Header) {
                Assert.assertEquals(Message_Header_Invalid_Relative_Offset, headerBuf.getInt());
            }
            // blob properties relative offset
            Assert.assertEquals(Message_Header_Invalid_Relative_Offset, headerBuf.getInt());
            // delete record relative offset. This is the only relative offset with a valid value.
            Assert.assertEquals((headerSize + (key.sizeInBytes())), headerBuf.getInt());
            // user metadata relative offset
            Assert.assertEquals(Message_Header_Invalid_Relative_Offset, headerBuf.getInt());
            // blob relative offset
            Assert.assertEquals(Message_Header_Invalid_Relative_Offset, headerBuf.getInt());
            Crc32 crc = new Crc32();
            crc.update(headerOutput, 0, (headerSize - (Crc_Size)));
            Assert.assertEquals(crc.getValue(), headerBuf.getLong());
            // verify handle
            byte[] handleOutput = new byte[key.sizeInBytes()];
            messageFormatStream.read(handleOutput);
            Assert.assertArrayEquals(handleOutput, key.toBytes());
            // check delete record
            UpdateRecord updateRecord = MessageFormatRecord.deserializeUpdateRecord(messageFormatStream);
            Assert.assertEquals("Type of update record not DELETE", DELETE, updateRecord.getType());
            Assert.assertNotNull("DeleteSubRecord should not be null", updateRecord.getDeleteSubRecord());
            Assert.assertEquals("AccountId mismatch", accountId, updateRecord.getAccountId());
            Assert.assertEquals("ContainerId mismatch", containerId, updateRecord.getContainerId());
            Assert.assertEquals("DeletionTime mismatch", deletionTimeMs, updateRecord.getUpdateTimeInMs());
        }
    }

    /**
     * Test calling the no-arg read method
     *
     * @throws IOException
     * 		
     * @throws MessageFormatException
     * 		
     */
    @Test
    public void messageFormatNoArgReadTest() throws MessageFormatException, Exception {
        StoreKey key = new MockId("id1");
        StoreKeyFactory keyFactory = new MockIdFactory();
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        BlobProperties prop = new BlobProperties(10, "servid", accountId, containerId, false);
        byte[] encryptionKey = new byte[100];
        new Random().nextBytes(encryptionKey);
        byte[] usermetadata = new byte[1000];
        new Random().nextBytes(usermetadata);
        int blobContentSize = 2000;
        byte[] data = new byte[blobContentSize];
        new Random().nextBytes(data);
        ByteBufferInputStream stream = new ByteBufferInputStream(ByteBuffer.wrap(data));
        MessageFormatInputStream messageFormatStream = new PutMessageFormatInputStream(key, ByteBuffer.wrap(encryptionKey), prop, ByteBuffer.wrap(usermetadata), stream, blobContentSize, DataBlob);
        TestUtils.validateInputStreamContract(messageFormatStream);
        TestUtils.readInputStreamAndValidateSize(messageFormatStream, messageFormatStream.getSize());
    }

    /**
     * Tests for {@link TtlUpdateMessageFormatInputStream} in different versions.
     */
    @Test
    public void messageFormatTtlUpdateRecordTest() throws MessageFormatException, IOException {
        StoreKey key = new MockId("id1");
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        long ttlUpdateTimeMs = (SystemTime.getInstance().milliseconds()) + (RANDOM.nextInt());
        long updatedExpiryMs = ttlUpdateTimeMs + (RANDOM.nextInt());
        MessageFormatInputStream messageFormatStream = new TtlUpdateMessageFormatInputStream(key, accountId, containerId, updatedExpiryMs, ttlUpdateTimeMs);
        long ttlUpdateRecordSize = Update_Format_V3.getRecordSize(TTL_UPDATE);
        int headerSize = MessageFormatRecord.getHeaderSizeForVersion(headerVersionToUse);
        Assert.assertEquals(((headerSize + ttlUpdateRecordSize) + (key.sizeInBytes())), messageFormatStream.getSize());
        MessageFormatInputStreamTest.checkTtlUpdateMessage(messageFormatStream, ttlUpdateRecordSize, key, accountId, containerId, updatedExpiryMs, ttlUpdateTimeMs);
    }
}

