/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage;


import StorageRpc.Option;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.spi.v1.RpcBatch;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class StorageBatchTest {
    private static final BlobId BLOB_ID = BlobId.of("b1", "n1");

    private static final BlobId BLOB_ID_COMPLETE = BlobId.of("b1", "n1", 42L);

    private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder(StorageBatchTest.BLOB_ID).build();

    private static final BlobInfo BLOB_INFO_COMPLETE = BlobInfo.newBuilder(StorageBatchTest.BLOB_ID_COMPLETE).setMetageneration(42L).build();

    private static final BlobGetOption[] BLOB_GET_OPTIONS = new BlobGetOption[]{ BlobGetOption.generationMatch(42L), BlobGetOption.metagenerationMatch(42L) };

    private static final BlobSourceOption[] BLOB_SOURCE_OPTIONS = new BlobSourceOption[]{ BlobSourceOption.generationMatch(42L), BlobSourceOption.metagenerationMatch(42L) };

    private static final BlobTargetOption[] BLOB_TARGET_OPTIONS = new BlobTargetOption[]{ BlobTargetOption.generationMatch(), BlobTargetOption.metagenerationMatch() };

    private static final GoogleJsonError GOOGLE_JSON_ERROR = new GoogleJsonError();

    private StorageOptions optionsMock;

    private StorageRpc storageRpcMock;

    private RpcBatch batchMock;

    private StorageBatch storageBatch;

    private final Storage storage = EasyMock.createStrictMock(Storage.class);

    @Test
    public void testConstructor() {
        Assert.assertSame(batchMock, storageBatch.getBatch());
        Assert.assertSame(optionsMock, storageBatch.getOptions());
        Assert.assertSame(storageRpcMock, storageBatch.getStorageRpc());
    }

    @Test
    public void testDelete() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Void>> callback = Capture.newInstance();
        batchMock.addDelete(EasyMock.eq(StorageBatchTest.BLOB_INFO.toPb()), EasyMock.capture(callback), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.replay(batchMock);
        StorageBatchResult<Boolean> batchResult = storageBatch.delete(StorageBatchTest.BLOB_ID.getBucket(), StorageBatchTest.BLOB_ID.getName());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<Void> capturedCallback = callback.getValue();
        capturedCallback.onFailure(StorageBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a StorageExcetion on error.");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testDeleteWithOptions() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Void>> callback = Capture.newInstance();
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addDelete(EasyMock.eq(StorageBatchTest.BLOB_INFO.toPb()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        StorageBatchResult<Boolean> batchResult = storageBatch.delete(StorageBatchTest.BLOB_ID, StorageBatchTest.BLOB_SOURCE_OPTIONS);
        Assert.assertNotNull(callback.getValue());
        Assert.assertEquals(2, capturedOptions.getValue().size());
        for (BlobSourceOption option : StorageBatchTest.BLOB_SOURCE_OPTIONS) {
            Assert.assertEquals(option.getValue(), capturedOptions.getValue().get(option.getRpcOption()));
        }
        RpcBatch.Callback<Void> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(null);
        Assert.assertTrue(batchResult.get());
    }

    @Test
    public void testUpdate() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
        batchMock.addPatch(EasyMock.eq(StorageBatchTest.BLOB_INFO.toPb()), EasyMock.capture(callback), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.replay(batchMock);
        StorageBatchResult<Blob> batchResult = storageBatch.update(StorageBatchTest.BLOB_INFO);
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
        capturedCallback.onFailure(StorageBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a StorageExcetion on error.");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testUpdateWithOptions() {
        EasyMock.reset(storage, batchMock, optionsMock);
        EasyMock.expect(storage.getOptions()).andReturn(optionsMock).times(2);
        EasyMock.expect(optionsMock.getService()).andReturn(storage);
        Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addPatch(EasyMock.eq(StorageBatchTest.BLOB_INFO_COMPLETE.toPb()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock, storage, optionsMock);
        StorageBatchResult<Blob> batchResult = storageBatch.update(StorageBatchTest.BLOB_INFO_COMPLETE, StorageBatchTest.BLOB_TARGET_OPTIONS);
        Assert.assertNotNull(callback.getValue());
        Assert.assertEquals(2, capturedOptions.getValue().size());
        Assert.assertEquals(42L, capturedOptions.getValue().get(StorageBatchTest.BLOB_TARGET_OPTIONS[0].getRpcOption()));
        Assert.assertEquals(42L, capturedOptions.getValue().get(StorageBatchTest.BLOB_TARGET_OPTIONS[1].getRpcOption()));
        RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(StorageBatchTest.BLOB_INFO.toPb());
        Assert.assertEquals(new Blob(storage, new Blob.BuilderImpl(StorageBatchTest.BLOB_INFO)), batchResult.get());
    }

    @Test
    public void testGet() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
        batchMock.addGet(EasyMock.eq(StorageBatchTest.BLOB_INFO.toPb()), EasyMock.capture(callback), EasyMock.eq(ImmutableMap.<StorageRpc.Option, Object>of()));
        EasyMock.replay(batchMock);
        StorageBatchResult<Blob> batchResult = storageBatch.get(StorageBatchTest.BLOB_ID.getBucket(), StorageBatchTest.BLOB_ID.getName());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
        capturedCallback.onFailure(StorageBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a StorageExcetion on error.");
        } catch (StorageException ex) {
            // expected
        }
    }

    @Test
    public void testGetWithOptions() {
        EasyMock.reset(storage, batchMock, optionsMock);
        EasyMock.expect(storage.getOptions()).andReturn(optionsMock).times(2);
        EasyMock.expect(optionsMock.getService()).andReturn(storage);
        Capture<RpcBatch.Callback<StorageObject>> callback = Capture.newInstance();
        Capture<Map<StorageRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGet(EasyMock.eq(StorageBatchTest.BLOB_INFO.toPb()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(storage, batchMock, optionsMock);
        StorageBatchResult<Blob> batchResult = storageBatch.get(StorageBatchTest.BLOB_ID, StorageBatchTest.BLOB_GET_OPTIONS);
        Assert.assertNotNull(callback.getValue());
        Assert.assertEquals(2, capturedOptions.getValue().size());
        for (BlobGetOption option : StorageBatchTest.BLOB_GET_OPTIONS) {
            Assert.assertEquals(option.getValue(), capturedOptions.getValue().get(option.getRpcOption()));
        }
        RpcBatch.Callback<StorageObject> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(StorageBatchTest.BLOB_INFO.toPb());
        Assert.assertEquals(new Blob(storage, new Blob.BuilderImpl(StorageBatchTest.BLOB_INFO)), batchResult.get());
    }
}

