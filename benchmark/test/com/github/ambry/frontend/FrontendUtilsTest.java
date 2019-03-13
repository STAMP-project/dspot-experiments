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
package com.github.ambry.frontend;


import BlobId.BlobDataType;
import BlobId.BlobIdType;
import MockClusterMap.DEFAULT_PARTITION_CLASS;
import RestServiceErrorCode.BadRequest;
import TestUtils.RANDOM;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.rest.RestServiceException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link FrontendUtils}
 */
public class FrontendUtilsTest {
    /**
     * Tests {@link FrontendUtils#getBlobIdFromString(String, ClusterMap)}
     *
     * @throws IOException
     * 		
     * @throws RestServiceException
     * 		
     */
    @Test
    public void testGetBlobIdFromString() throws RestServiceException, IOException {
        // good path
        byte[] bytes = new byte[2];
        ClusterMap referenceClusterMap = new MockClusterMap();
        RANDOM.nextBytes(bytes);
        BlobId.BlobIdType referenceType = (RANDOM.nextBoolean()) ? BlobIdType.NATIVE : BlobIdType.CRAFTED;
        RANDOM.nextBytes(bytes);
        byte referenceDatacenterId = bytes[0];
        short referenceAccountId = getRandomShort(RANDOM);
        short referenceContainerId = getRandomShort(RANDOM);
        PartitionId referencePartitionId = referenceClusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0);
        boolean referenceIsEncrypted = RANDOM.nextBoolean();
        List<Short> versions = Arrays.stream(BlobId.getAllValidVersions()).filter(( version) -> version >= BlobId.BLOB_ID_V3).collect(Collectors.toList());
        for (short version : versions) {
            BlobId blobId = new BlobId(version, referenceType, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, referenceIsEncrypted, BlobDataType.DATACHUNK);
            BlobId regeneratedBlobId = FrontendUtils.getBlobIdFromString(blobId.getID(), referenceClusterMap);
            Assert.assertEquals("BlobId mismatch", blobId, regeneratedBlobId);
            assertBlobIdFieldValues(regeneratedBlobId, referenceType, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, ((version >= (BlobId.BLOB_ID_V4)) && referenceIsEncrypted));
            // bad path
            try {
                FrontendUtils.getBlobIdFromString(blobId.getID().substring(1), referenceClusterMap);
                Assert.fail("Should have thrown exception for bad blobId ");
            } catch (RestServiceException e) {
                Assert.assertEquals("RestServiceErrorCode mismatch", BadRequest, e.getErrorCode());
            }
        }
    }
}

