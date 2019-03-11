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


import TestUtils.RANDOM;
import com.github.ambry.store.MessageReadSet;
import com.github.ambry.store.MockId;
import com.github.ambry.store.Read;
import com.github.ambry.store.StoreKey;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static BlobType.DataBlob;
import static BlobType.MetadataBlob;
import static MessageFormatRecord.Blob_Version_V1;
import static MessageFormatRecord.Blob_Version_V2;
import static MessageFormatRecord.Crc_Size;
import static MessageFormatRecord.Message_Header_Version_V2;
import static MessageFormatRecord.UserMetadata_Version_V1;
import static MessageFormatRecord.headerVersionToUse;


@RunWith(Parameterized.class)
public class BlobStoreHardDeleteTest {
    private static short messageFormatHeaderVersionSaved;

    public BlobStoreHardDeleteTest(short headerVersionToUse) {
        headerVersionToUse = headerVersionToUse;
    }

    public class ReadImp implements Read {
        private BlobStoreHardDeleteTest.ReadImp.MockReadSet readSet = new BlobStoreHardDeleteTest.ReadImp.MockReadSet();

        private List<byte[]> recoveryInfoList = new ArrayList<byte[]>();

        private ByteBuffer buffer;

        private StoreKey[] keys = new StoreKey[]{ new MockId("id1"), new MockId("id2"), new MockId("id3"), new MockId("id4"), new MockId("id5"), new MockId("id6"), new MockId("id7") };

        public ArrayList<Long> initialize(short[] blobVersions, BlobType[] blobTypes) throws MessageFormatException, IOException {
            // write 3 new blob messages, and delete update messages. write the last
            // message that is partial
            final int ENCRYPTION_KEY_SIZE = 256;
            final int USERMETADATA_SIZE = 2000;
            final int BLOB_SIZE = 4000;
            byte[] encryptionKey = new byte[ENCRYPTION_KEY_SIZE];
            byte[] usermetadata = new byte[USERMETADATA_SIZE];
            byte[] blob = new byte[BLOB_SIZE];
            RANDOM.nextBytes(usermetadata);
            RANDOM.nextBytes(blob);
            RANDOM.nextBytes(encryptionKey);
            short accountId = Utils.getRandomShort(RANDOM);
            short containerId = Utils.getRandomShort(RANDOM);
            long updateTimeMs = (SystemTime.getInstance().milliseconds()) + (RANDOM.nextInt());
            BlobProperties blobProperties = new BlobProperties(BLOB_SIZE, "test", "mem1", "img", false, 9999, accountId, containerId, true, null);
            MessageFormatInputStream msg0 = getPutMessage(keys[0], ByteBuffer.wrap(encryptionKey), blobProperties, usermetadata, blob, BLOB_SIZE, blobVersions[0], blobTypes[0]);
            MessageFormatInputStream msg1 = getPutMessage(keys[1], null, blobProperties, usermetadata, blob, BLOB_SIZE, blobVersions[1], blobTypes[1]);
            MessageFormatInputStream msg2 = getPutMessage(keys[2], ByteBuffer.wrap(encryptionKey), blobProperties, usermetadata, blob, BLOB_SIZE, blobVersions[2], blobTypes[2]);
            MessageFormatInputStream msg3t;
            if ((MessageFormatRecord.headerVersionToUse) >= (Message_Header_Version_V2)) {
                msg3t = new TtlUpdateMessageFormatInputStream(keys[1], accountId, containerId, Utils.Infinite_Time, updateTimeMs);
            } else {
                msg3t = getPutMessage(keys[5], ByteBuffer.wrap(encryptionKey), blobProperties, usermetadata, blob, BLOB_SIZE, blobVersions[0], blobTypes[0]);
            }
            DeleteMessageFormatInputStream msg4d = new DeleteMessageFormatInputStream(keys[1], accountId, containerId, updateTimeMs);
            MessageFormatInputStream msg5 = getPutMessage(keys[3], ByteBuffer.wrap(encryptionKey), blobProperties, usermetadata, blob, BLOB_SIZE, blobVersions[3], blobTypes[3]);
            MessageFormatInputStream msg6 = getPutMessage(keys[4], ByteBuffer.wrap(encryptionKey), blobProperties, usermetadata, blob, BLOB_SIZE, blobVersions[4], blobTypes[4]);
            MessageFormatInputStream msg7t;
            if ((MessageFormatRecord.headerVersionToUse) >= (Message_Header_Version_V2)) {
                msg7t = new TtlUpdateMessageFormatInputStream(keys[0], accountId, containerId, Utils.Infinite_Time, updateTimeMs);
            } else {
                msg7t = getPutMessage(keys[6], ByteBuffer.wrap(encryptionKey), blobProperties, usermetadata, blob, BLOB_SIZE, blobVersions[0], blobTypes[0]);
            }
            buffer = ByteBuffer.allocate(((int) ((((((((msg0.getSize()) + (msg1.getSize())) + (msg2.getSize())) + (msg3t.getSize())) + (msg4d.getSize())) + (msg5.getSize())) + (msg6.getSize())) + (msg7t.getSize()))));
            ArrayList<Long> msgOffsets = new ArrayList<Long>();
            Long offset = 0L;
            msgOffsets.add(offset);
            offset += msg0.getSize();
            msgOffsets.add(offset);
            offset += msg1.getSize();
            msgOffsets.add(offset);
            offset += msg2.getSize();
            msgOffsets.add(offset);
            offset += msg3t.getSize();
            msgOffsets.add(offset);
            offset += msg4d.getSize();
            msgOffsets.add(offset);
            offset += msg5.getSize();
            msgOffsets.add(offset);
            offset += msg6.getSize();
            msgOffsets.add(offset);
            offset += msg7t.getSize();
            msgOffsets.add(offset);
            // msg0: A good message that will not be part of hard deletes.
            writeToBuffer(msg0, ((int) (msg0.getSize())));
            // msg1: A good message that will be part of hard deletes, but not part of recovery.
            readSet.addMessage(buffer.position(), keys[1], ((int) (msg1.getSize())));
            writeToBuffer(msg1, ((int) (msg1.getSize())));
            // msg2: A good message that will be part of hard delete, with recoveryInfo.
            readSet.addMessage(buffer.position(), keys[2], ((int) (msg2.getSize())));
            writeToBuffer(msg2, ((int) (msg2.getSize())));
            HardDeleteRecoveryMetadata hardDeleteRecoveryMetadata = new HardDeleteRecoveryMetadata(MessageFormatRecord.headerVersionToUse, UserMetadata_Version_V1, USERMETADATA_SIZE, blobVersions[2], blobTypes[2], BLOB_SIZE, keys[2]);
            recoveryInfoList.add(hardDeleteRecoveryMetadata.toBytes());
            // msg3t: TTL update record, Not part of readSet
            // OR
            // PUT record: A good message that will not be part of hard deletes.
            writeToBuffer(msg3t, ((int) (msg3t.getSize())));
            // msg4d: Delete Record. Not part of readSet.
            writeToBuffer(msg4d, ((int) (msg4d.getSize())));
            // msg5: A message with blob record corrupted that will be part of hard delete, with recoveryInfo.
            // This should succeed.
            readSet.addMessage(buffer.position(), keys[3], ((int) (msg5.getSize())));
            writeToBufferAndCorruptBlobRecord(msg5, ((int) (msg5.getSize())));
            hardDeleteRecoveryMetadata = new HardDeleteRecoveryMetadata(MessageFormatRecord.headerVersionToUse, UserMetadata_Version_V1, USERMETADATA_SIZE, blobVersions[3], blobTypes[3], BLOB_SIZE, keys[3]);
            recoveryInfoList.add(hardDeleteRecoveryMetadata.toBytes());
            // msg6: A message with blob record corrupted that will be part of hard delete, without recoveryInfo.
            // This should fail.
            readSet.addMessage(buffer.position(), keys[4], ((int) (msg6.getSize())));
            writeToBufferAndCorruptBlobRecord(msg6, ((int) (msg6.getSize())));
            // msg7t: TTL update record, Not part of readSet
            // OR
            // PUT record: A good message that will not be part of hard deletes.
            writeToBuffer(msg7t, ((int) (msg7t.getSize())));
            buffer.position(0);
            return msgOffsets;
        }

        private MessageFormatInputStream getPutMessage(StoreKey key, ByteBuffer encryptionKey, BlobProperties blobProperties, byte[] usermetadata, byte[] blob, int blobSize, short blobVersion, BlobType blobType) throws MessageFormatException {
            if (blobVersion == (Blob_Version_V2)) {
                return new PutMessageFormatInputStream(key, encryptionKey, blobProperties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(blob)), blobSize, blobType);
            } else {
                return new PutMessageFormatBlobV1InputStream(key, blobProperties, ByteBuffer.wrap(usermetadata), new ByteBufferInputStream(ByteBuffer.wrap(blob)), blobSize, blobType);
            }
        }

        private void writeToBuffer(MessageFormatInputStream stream, int sizeToWrite) throws IOException {
            long sizeWritten = 0;
            while (sizeWritten < sizeToWrite) {
                int read = stream.read(buffer.array(), buffer.position(), ((int) (sizeToWrite)));
                sizeWritten += read;
                buffer.position(((buffer.position()) + ((int) (sizeWritten))));
            } 
        }

        private void writeToBufferAndCorruptBlobRecord(MessageFormatInputStream stream, int sizeToWrite) throws IOException {
            long sizeWritten = 0;
            while (sizeWritten < sizeToWrite) {
                int read = stream.read(buffer.array(), buffer.position(), ((int) (sizeToWrite)));
                sizeWritten += read;
                buffer.position(((buffer.position()) + ((int) (sizeWritten))));
                // corrupt the last byte of the blob record, just before the crc, so the crc fails.
                int indexToCorrupt = ((buffer.position()) - (Crc_Size)) - 1;
                byte b = buffer.get(indexToCorrupt);
                b = ((byte) ((int) (~b)));
                buffer.put(indexToCorrupt, b);
            } 
        }

        @Override
        public void readInto(ByteBuffer bufferToWrite, long position) throws IOException {
            bufferToWrite.put(buffer.array(), ((int) (position)), bufferToWrite.remaining());
        }

        public int getSize() {
            return buffer.capacity();
        }

        public MessageReadSet getMessageReadSet() {
            return readSet;
        }

        public List<byte[]> getRecoveryInfoList() {
            return recoveryInfoList;
        }

        class Message {
            int position;

            StoreKey key;

            int size;

            Message(int position, StoreKey key, int size) {
                this.position = position;
                this.key = key;
                this.size = size;
            }
        }

        class MockReadSet implements MessageReadSet {
            List<BlobStoreHardDeleteTest.ReadImp.Message> messageList = new ArrayList<BlobStoreHardDeleteTest.ReadImp.Message>();

            void addMessage(int position, StoreKey key, int size) {
                messageList.add(new BlobStoreHardDeleteTest.ReadImp.Message(position, key, size));
            }

            @Override
            public long writeTo(int index, WritableByteChannel channel, long relativeOffset, long maxSize) throws IOException {
                buffer.position(((messageList.get(index).position) + ((int) (relativeOffset))));
                byte[] toReturn = new byte[Math.min(messageList.get(index).size, ((int) (maxSize)))];
                buffer.get(toReturn);
                return channel.write(ByteBuffer.wrap(toReturn));
            }

            @Override
            public int count() {
                return messageList.size();
            }

            @Override
            public long sizeInBytes(int index) {
                return messageList.get(index).size;
            }

            @Override
            public StoreKey getKeyAt(int index) {
                return messageList.get(index).key;
            }

            @Override
            public void doPrefetch(int index, long relativeOffset, long size) {
                return;
            }
        }
    }

    @Test
    public void blobStoreHardDeleteTestBlobV1() throws MessageFormatException, IOException {
        short[] blobVersions = new short[5];
        BlobType[] blobTypes = new BlobType[5];
        for (int i = 0; i < 5; i++) {
            blobVersions[i] = Blob_Version_V1;
            blobTypes[i] = DataBlob;
        }
        blobStoreHardDeleteTestUtil(blobVersions, blobTypes);
    }

    @Test
    public void blobStoreHardDeleteTestBlobV2Simple() throws MessageFormatException, IOException {
        short[] blobVersions = new short[5];
        BlobType[] blobTypes = new BlobType[5];
        for (int i = 0; i < 5; i++) {
            blobVersions[i] = MessageFormatRecord.Blob_Version_V2;
            blobTypes[i] = DataBlob;
        }
        // all blobs V2 with Data blob
        blobStoreHardDeleteTestUtil(blobVersions, blobTypes);
        blobVersions = new short[5];
        blobTypes = new BlobType[5];
        for (int i = 0; i < 5; i++) {
            blobVersions[i] = MessageFormatRecord.Blob_Version_V2;
            blobTypes[i] = MetadataBlob;
        }
        // all blobs V2 with Metadata blob
        blobStoreHardDeleteTestUtil(blobVersions, blobTypes);
    }

    @Test
    public void blobStoreHardDeleteTestBlobV2Mixed() throws MessageFormatException, IOException {
        short[] blobVersions = new short[5];
        BlobType[] blobTypes = new BlobType[5];
        blobVersions[0] = Blob_Version_V1;
        blobTypes[0] = DataBlob;
        blobVersions[1] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[1] = MetadataBlob;
        blobVersions[2] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[2] = DataBlob;
        blobVersions[3] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[3] = MetadataBlob;
        blobVersions[4] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[4] = DataBlob;
        blobStoreHardDeleteTestUtil(blobVersions, blobTypes);
        blobVersions = new short[5];
        blobTypes = new BlobType[5];
        blobVersions[0] = Blob_Version_V1;
        blobTypes[0] = DataBlob;
        blobVersions[1] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[1] = DataBlob;
        blobVersions[2] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[2] = MetadataBlob;
        blobVersions[3] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[3] = DataBlob;
        blobVersions[4] = MessageFormatRecord.Blob_Version_V2;
        blobTypes[4] = MetadataBlob;
        blobStoreHardDeleteTestUtil(blobVersions, blobTypes);
    }
}

