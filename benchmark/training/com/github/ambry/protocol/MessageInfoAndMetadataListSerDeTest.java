/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.protocol;


import BlobId.BlobDataType;
import BlobId.BlobIdType;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.MockPartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.messageformat.MessageMetadata;
import com.github.ambry.store.MessageInfo;
import com.github.ambry.store.StoreKey;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static MessageInfoAndMetadataListSerde.VERSION_5;


/**
 * Tests {@link MessageInfoAndMetadataListSerde}
 */
@RunWith(Parameterized.class)
public class MessageInfoAndMetadataListSerDeTest {
    private final short serDeVersion;

    public MessageInfoAndMetadataListSerDeTest(short serDeVersion) {
        this.serDeVersion = serDeVersion;
    }

    @Test
    public void testSerDe() throws Exception {
        MockClusterMap mockMap = new MockClusterMap();
        MockPartitionId partitionId = new MockPartitionId();
        short[] accountIds = new short[]{ 100, 101, 102, 103 };
        short[] containerIds = new short[]{ 10, 11, 12, 13 };
        boolean[] isDeletedVals = new boolean[]{ false, true, false, true };
        boolean[] isTtlUpdatedVals = new boolean[]{ true, false, false, true };
        Long[] crcs = new Long[]{ null, 100L, Long.MIN_VALUE, Long.MAX_VALUE };
        StoreKey[] keys = new StoreKey[]{ new BlobId(TestUtils.getRandomElement(BlobId.getAllValidVersions()), BlobIdType.NATIVE, ((byte) (0)), accountIds[0], containerIds[0], partitionId, false, BlobDataType.DATACHUNK), new BlobId(TestUtils.getRandomElement(BlobId.getAllValidVersions()), BlobIdType.NATIVE, ((byte) (0)), accountIds[1], containerIds[1], partitionId, false, BlobDataType.DATACHUNK), new BlobId(TestUtils.getRandomElement(BlobId.getAllValidVersions()), BlobIdType.NATIVE, ((byte) (0)), accountIds[2], containerIds[2], partitionId, false, BlobDataType.DATACHUNK), new BlobId(TestUtils.getRandomElement(BlobId.getAllValidVersions()), BlobIdType.NATIVE, ((byte) (0)), accountIds[3], containerIds[3], partitionId, false, BlobDataType.DATACHUNK) };
        long[] blobSizes = new long[]{ 1024, 2048, 4096, 8192 };
        long[] times = new long[]{ SystemTime.getInstance().milliseconds(), (SystemTime.getInstance().milliseconds()) - 1, (SystemTime.getInstance().milliseconds()) + (TimeUnit.SECONDS.toMillis(5)), Utils.Infinite_Time };
        MessageMetadata[] messageMetadata = new MessageMetadata[4];
        messageMetadata[0] = new MessageMetadata(ByteBuffer.wrap(getRandomBytes(100)));
        messageMetadata[1] = new MessageMetadata(null);
        messageMetadata[2] = null;
        messageMetadata[3] = new MessageMetadata(ByteBuffer.wrap(getRandomBytes(200)));
        List<MessageInfo> messageInfoList = new ArrayList<>(4);
        List<MessageMetadata> messageMetadataList = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            messageInfoList.add(new MessageInfo(keys[i], blobSizes[i], isDeletedVals[i], isTtlUpdatedVals[i], times[i], crcs[i], accountIds[i], containerIds[i], times[i]));
            messageMetadataList.add(messageMetadata[i]);
        }
        // Serialize and then deserialize
        MessageInfoAndMetadataListSerde messageInfoAndMetadataListSerde = new MessageInfoAndMetadataListSerde(messageInfoList, messageMetadataList, serDeVersion);
        ByteBuffer buffer = ByteBuffer.allocate(messageInfoAndMetadataListSerde.getMessageInfoAndMetadataListSize());
        messageInfoAndMetadataListSerde.serializeMessageInfoAndMetadataList(buffer);
        buffer.flip();
        if ((serDeVersion) >= (VERSION_5)) {
            // should fail if the wrong version is provided
            try {
                MessageInfoAndMetadataListSerde.deserializeMessageInfoAndMetadataList(new DataInputStream(new ByteBufferInputStream(buffer)), mockMap, ((short) ((serDeVersion) - 1)));
                Assert.fail("Should have failed to deserialize");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do
            }
        }
        buffer.rewind();
        MessageInfoAndMetadataListSerde messageInfoAndMetadataList = MessageInfoAndMetadataListSerde.deserializeMessageInfoAndMetadataList(new DataInputStream(new ByteBufferInputStream(buffer)), mockMap, serDeVersion);
        // Verify
        List<MessageInfo> responseMessageInfoList = messageInfoAndMetadataList.getMessageInfoList();
        List<MessageMetadata> responseMessageMetadataList = messageInfoAndMetadataList.getMessageMetadataList();
        Assert.assertEquals(4, responseMessageInfoList.size());
        Assert.assertEquals(4, responseMessageMetadataList.size());
        for (int i = 0; i < 4; i++) {
            assertMessageInfoEquality(messageInfoList.get(i), responseMessageInfoList.get(i));
            assertMessageMetadataEquality(messageMetadataList.get(i), responseMessageMetadataList.get(i));
        }
    }
}

