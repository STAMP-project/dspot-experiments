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
import Utils.Infinite_Time;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Utils;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.github.ambry.messageformat.BlobPropertiesSerDe.BlobPropertiesSerDe.VERSION_1;
import static com.github.ambry.messageformat.BlobPropertiesSerDe.BlobPropertiesSerDe.VERSION_3;


/**
 * Basic tests for BlobProperties
 */
@RunWith(Parameterized.class)
public class BlobPropertiesTest {
    private final short version;

    public BlobPropertiesTest(short version) {
        this.version = version;
    }

    @Test
    public void basicTest() throws IOException {
        int blobSize = 100;
        String serviceId = "ServiceId";
        String ownerId = "OwnerId";
        String contentType = "ContentType";
        String externalAssetTag = "some-external-asset-tag";
        int timeToLiveInSeconds = 144;
        short accountId = Utils.getRandomShort(RANDOM);
        short containerId = Utils.getRandomShort(RANDOM);
        boolean isEncrypted = RANDOM.nextBoolean();
        short accountIdToExpect = ((version) == (VERSION_1)) ? UNKNOWN_ACCOUNT_ID : accountId;
        short containerIdToExpect = ((version) == (VERSION_1)) ? UNKNOWN_CONTAINER_ID : containerId;
        boolean encryptFlagToExpect = ((version) == (VERSION_3)) && isEncrypted;
        BlobProperties blobProperties = new BlobProperties(blobSize, serviceId, null, null, false, Utils.Infinite_Time, SystemTime.getInstance().milliseconds(), accountId, containerId, isEncrypted, externalAssetTag);
        System.out.println(blobProperties.toString());// Provide example of BlobProperties.toString()

        ByteBuffer serializedBuffer = serializeBlobPropertiesInVersion(blobProperties);
        blobProperties = BlobPropertiesSerDe.BlobPropertiesSerDe.getBlobPropertiesFromStream(new DataInputStream(new ByteBufferInputStream(serializedBuffer)));
        verifyBlobProperties(blobProperties, blobSize, serviceId, "", "", false, Infinite_Time, accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
        Assert.assertTrue(((blobProperties.getCreationTimeInMs()) > 0));
        Assert.assertTrue(((blobProperties.getCreationTimeInMs()) <= (System.currentTimeMillis())));
        blobProperties = new BlobProperties(blobSize, serviceId, null, null, false, Utils.Infinite_Time, SystemTime.getInstance().milliseconds(), accountId, containerId, isEncrypted, externalAssetTag);
        serializedBuffer = serializeBlobPropertiesInVersion(blobProperties);
        blobProperties = BlobPropertiesSerDe.BlobPropertiesSerDe.getBlobPropertiesFromStream(new DataInputStream(new ByteBufferInputStream(serializedBuffer)));
        verifyBlobProperties(blobProperties, blobSize, serviceId, "", "", false, Infinite_Time, accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
        blobProperties = new BlobProperties(blobSize, serviceId, ownerId, contentType, true, timeToLiveInSeconds, accountId, containerId, isEncrypted, externalAssetTag);
        System.out.println(blobProperties.toString());// Provide example of BlobProperties.toString()

        serializedBuffer = serializeBlobPropertiesInVersion(blobProperties);
        blobProperties = BlobPropertiesSerDe.BlobPropertiesSerDe.getBlobPropertiesFromStream(new DataInputStream(new ByteBufferInputStream(serializedBuffer)));
        verifyBlobProperties(blobProperties, blobSize, serviceId, ownerId, contentType, true, timeToLiveInSeconds, accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
        Assert.assertTrue(((blobProperties.getCreationTimeInMs()) > 0));
        Assert.assertTrue(((blobProperties.getCreationTimeInMs()) <= (System.currentTimeMillis())));
        long creationTimeMs = SystemTime.getInstance().milliseconds();
        blobProperties = new BlobProperties(blobSize, serviceId, ownerId, contentType, true, timeToLiveInSeconds, creationTimeMs, accountId, containerId, isEncrypted, "some-external-asset-tag");
        System.out.println(blobProperties.toString());// Provide example of BlobProperties.toString()

        serializedBuffer = serializeBlobPropertiesInVersion(blobProperties);
        blobProperties = BlobPropertiesSerDe.BlobPropertiesSerDe.getBlobPropertiesFromStream(new DataInputStream(new ByteBufferInputStream(serializedBuffer)));
        verifyBlobProperties(blobProperties, blobSize, serviceId, ownerId, contentType, true, timeToLiveInSeconds, accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
        Assert.assertEquals(blobProperties.getCreationTimeInMs(), creationTimeMs);
        long creationTimeInSecs = TimeUnit.MILLISECONDS.toSeconds(creationTimeMs);
        // valid TTLs
        long[] validTTLs = new long[]{ TimeUnit.HOURS.toSeconds(1), TimeUnit.HOURS.toSeconds(10), TimeUnit.HOURS.toSeconds(100), TimeUnit.DAYS.toSeconds(1), TimeUnit.DAYS.toSeconds(10), TimeUnit.DAYS.toSeconds(100), TimeUnit.DAYS.toSeconds((30 * 12)), TimeUnit.DAYS.toSeconds(((30 * 12) * 10)), ((Integer.MAX_VALUE) - creationTimeInSecs) - 1, (Integer.MAX_VALUE) - creationTimeInSecs, ((Integer.MAX_VALUE) - creationTimeInSecs) + 1, ((Integer.MAX_VALUE) - creationTimeInSecs) + 100, ((Integer.MAX_VALUE) - creationTimeInSecs) + 10000 };
        for (long ttl : validTTLs) {
            blobProperties = new BlobProperties(blobSize, serviceId, ownerId, contentType, true, ttl, creationTimeMs, accountId, containerId, isEncrypted, null);
            serializedBuffer = serializeBlobPropertiesInVersion(blobProperties);
            blobProperties = BlobPropertiesSerDe.BlobPropertiesSerDe.getBlobPropertiesFromStream(new DataInputStream(new ByteBufferInputStream(serializedBuffer)));
            verifyBlobProperties(blobProperties, blobSize, serviceId, ownerId, contentType, true, ttl, accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
        }
        blobProperties = new BlobProperties(blobSize, serviceId, null, null, false, timeToLiveInSeconds, creationTimeMs, accountId, containerId, isEncrypted, externalAssetTag);
        verifyBlobProperties(blobProperties, blobSize, serviceId, null, null, false, timeToLiveInSeconds, accountId, containerId, isEncrypted, externalAssetTag);
        blobProperties.setTimeToLiveInSeconds((timeToLiveInSeconds + 1));
        verifyBlobProperties(blobProperties, blobSize, serviceId, null, null, false, (timeToLiveInSeconds + 1), accountId, containerId, isEncrypted, externalAssetTag);
        serializedBuffer = serializeBlobPropertiesInVersion(blobProperties);
        blobProperties = BlobPropertiesSerDe.BlobPropertiesSerDe.getBlobPropertiesFromStream(new DataInputStream(new ByteBufferInputStream(serializedBuffer)));
        verifyBlobProperties(blobProperties, blobSize, serviceId, "", "", false, (timeToLiveInSeconds + 1), accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
        blobProperties.setBlobSize((blobSize + 1));
        verifyBlobProperties(blobProperties, (blobSize + 1), serviceId, "", "", false, (timeToLiveInSeconds + 1), accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
        serializedBuffer = serializeBlobPropertiesInVersion(blobProperties);
        blobProperties = BlobPropertiesSerDe.BlobPropertiesSerDe.getBlobPropertiesFromStream(new DataInputStream(new ByteBufferInputStream(serializedBuffer)));
        verifyBlobProperties(blobProperties, (blobSize + 1), serviceId, "", "", false, (timeToLiveInSeconds + 1), accountIdToExpect, containerIdToExpect, encryptFlagToExpect, null);
    }
}

