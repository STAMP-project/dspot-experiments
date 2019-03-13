/**
 * Copyright 2019 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.cloud;


import StoreErrorCodes.IOError;
import StoreErrorCodes.Store_Not_Started;
import Utils.Infinite_Time;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.commons.BlobId;
import com.github.ambry.store.MockMessageWriteSet;
import com.github.ambry.store.Store;
import com.github.ambry.store.StoreException;
import com.github.ambry.store.StoreKey;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test class testing behavior of CloudBlobStore class.
 */
public class CloudBlobStoreTest {
    private Store store;

    private CloudDestination dest;

    private PartitionId partitionId;

    private Random random = new Random();

    private short refAccountId = 50;

    private short refContainerId = 100;

    /**
     * Test the CloudBlobStore put method.
     */
    @Test
    public void testStorePuts() throws Exception {
        // Put blobs with and without expiration, in
        // TODO: use containers flagged/not for replication
        MockMessageWriteSet messageWriteSet = new MockMessageWriteSet();
        int count = 10;
        long expireTime = (System.currentTimeMillis()) + 10000;
        for (int j = 0; j < count; j++) {
            long size = (Math.abs(random.nextLong())) % 10000;
            addBlobToSet(messageWriteSet, size, Infinite_Time, refAccountId, refContainerId);
            addBlobToSet(messageWriteSet, size, expireTime, refAccountId, refContainerId);
        }
        store.put(messageWriteSet);
        Mockito.verify(dest, Mockito.times(count)).uploadBlob(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(InputStream.class));
    }

    /**
     * Test the CloudBlobStore delete method.
     */
    @Test
    public void testStoreDeletes() throws Exception {
        MockMessageWriteSet messageWriteSet = new MockMessageWriteSet();
        int count = 10;
        for (int j = 0; j < count; j++) {
            long size = 10;
            addBlobToSet(messageWriteSet, size, Infinite_Time, refAccountId, refContainerId);
        }
        store.delete(messageWriteSet);
        Mockito.verify(dest, Mockito.times(count)).deleteBlob(ArgumentMatchers.any(BlobId.class));
    }

    /**
     * Test the CloudBlobStore findMissingKeys method.
     */
    @Test
    public void testFindMissingKeys() throws Exception {
        int count = 10;
        List<StoreKey> keys = new ArrayList<>();
        for (int j = 0; j < count; j++) {
            BlobId blobId = getUniqueId(refAccountId, refContainerId);
            Mockito.when(dest.doesBlobExist(ArgumentMatchers.eq(blobId))).thenReturn(true);
            keys.add(blobId);
            blobId = getUniqueId(refAccountId, refContainerId);
            Mockito.when(dest.doesBlobExist(ArgumentMatchers.eq(blobId))).thenReturn(false);
            keys.add(blobId);
        }
        Set<StoreKey> missingKeys = store.findMissingKeys(keys);
        Mockito.verify(dest, Mockito.times((count * 2))).doesBlobExist(ArgumentMatchers.any(BlobId.class));
        Assert.assertEquals("Wrong number of missing keys", count, missingKeys.size());
    }

    /**
     * Test verifying behavior when store not started.
     */
    @Test
    public void testStoreNotStarted() throws Exception {
        // Create store and don't start it.
        CloudBlobStore idleStore = new CloudBlobStore(partitionId, null, dest);
        List<StoreKey> keys = Collections.singletonList(getUniqueId(refAccountId, refContainerId));
        MockMessageWriteSet messageWriteSet = new MockMessageWriteSet();
        addBlobToSet(messageWriteSet, 10, Infinite_Time, refAccountId, refContainerId);
        try {
            idleStore.put(messageWriteSet);
            Assert.fail("Store put should have failed.");
        } catch (StoreException e) {
            Assert.assertEquals(Store_Not_Started, e.getErrorCode());
        }
        try {
            idleStore.delete(messageWriteSet);
            Assert.fail("Store delete should have failed.");
        } catch (StoreException e) {
            Assert.assertEquals(Store_Not_Started, e.getErrorCode());
        }
        try {
            idleStore.findMissingKeys(keys);
            Assert.fail("Store findMissingKeys should have failed.");
        } catch (StoreException e) {
            Assert.assertEquals(Store_Not_Started, e.getErrorCode());
        }
    }

    /**
     * Test verifying exception handling behavior.
     */
    @Test
    public void testExceptionalDest() throws Exception {
        CloudDestination exDest = Mockito.mock(CloudDestination.class);
        Mockito.when(exDest.uploadBlob(ArgumentMatchers.any(BlobId.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(InputStream.class))).thenThrow(new CloudStorageException("ouch"));
        Mockito.when(exDest.deleteBlob(ArgumentMatchers.any(BlobId.class))).thenThrow(new CloudStorageException("ouch"));
        Mockito.when(exDest.doesBlobExist(ArgumentMatchers.any(BlobId.class))).thenThrow(new CloudStorageException("ouch"));
        CloudBlobStore exStore = new CloudBlobStore(partitionId, null, exDest);
        exStore.start();
        List<StoreKey> keys = Collections.singletonList(getUniqueId(refAccountId, refContainerId));
        MockMessageWriteSet messageWriteSet = new MockMessageWriteSet();
        addBlobToSet(messageWriteSet, 10, Infinite_Time, refAccountId, refContainerId);
        try {
            exStore.put(messageWriteSet);
            Assert.fail("Store put should have failed.");
        } catch (StoreException e) {
            Assert.assertEquals(IOError, e.getErrorCode());
        }
        try {
            exStore.delete(messageWriteSet);
            Assert.fail("Store delete should have failed.");
        } catch (StoreException e) {
            Assert.assertEquals(IOError, e.getErrorCode());
        }
        try {
            exStore.findMissingKeys(keys);
            Assert.fail("Store findMissingKeys should have failed.");
        } catch (StoreException e) {
            Assert.assertEquals(IOError, e.getErrorCode());
        }
    }
}

