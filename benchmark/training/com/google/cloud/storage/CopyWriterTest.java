/**
 * Copyright 2015 Google LLC
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


import com.google.cloud.RestorableState;
import com.google.cloud.storage.spi.StorageRpcFactory;
import com.google.cloud.storage.spi.v1.StorageRpc;
import com.google.cloud.storage.spi.v1.StorageRpc.RewriteRequest;
import com.google.cloud.storage.spi.v1.StorageRpc.RewriteResponse;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class CopyWriterTest {
    private static final String SOURCE_BUCKET_NAME = "b";

    private static final String SOURCE_BLOB_NAME = "n";

    private static final String DESTINATION_BUCKET_NAME = "b1";

    private static final String DESTINATION_BLOB_NAME = "n1";

    private static final BlobId BLOB_ID = BlobId.of(CopyWriterTest.SOURCE_BUCKET_NAME, CopyWriterTest.SOURCE_BLOB_NAME);

    private static final BlobInfo BLOB_INFO = BlobInfo.newBuilder(CopyWriterTest.DESTINATION_BUCKET_NAME, CopyWriterTest.DESTINATION_BLOB_NAME).build();

    private static final BlobInfo RESULT_INFO = BlobInfo.newBuilder(CopyWriterTest.DESTINATION_BUCKET_NAME, CopyWriterTest.DESTINATION_BLOB_NAME).setContentType("type").build();

    private static final Map<StorageRpc.Option, ?> EMPTY_OPTIONS = ImmutableMap.of();

    private static final RewriteRequest REQUEST_WITH_OBJECT = new StorageRpc.RewriteRequest(CopyWriterTest.BLOB_ID.toPb(), CopyWriterTest.EMPTY_OPTIONS, true, CopyWriterTest.BLOB_INFO.toPb(), CopyWriterTest.EMPTY_OPTIONS, null);

    private static final RewriteRequest REQUEST_WITHOUT_OBJECT = new StorageRpc.RewriteRequest(CopyWriterTest.BLOB_ID.toPb(), CopyWriterTest.EMPTY_OPTIONS, false, CopyWriterTest.BLOB_INFO.toPb(), CopyWriterTest.EMPTY_OPTIONS, null);

    private static final RewriteResponse RESPONSE_WITH_OBJECT = new RewriteResponse(CopyWriterTest.REQUEST_WITH_OBJECT, null, 42L, false, "token", 21L);

    private static final RewriteResponse RESPONSE_WITHOUT_OBJECT = new RewriteResponse(CopyWriterTest.REQUEST_WITHOUT_OBJECT, null, 42L, false, "token", 21L);

    private static final RewriteResponse RESPONSE_WITH_OBJECT_DONE = new RewriteResponse(CopyWriterTest.REQUEST_WITH_OBJECT, CopyWriterTest.RESULT_INFO.toPb(), 42L, true, "token", 42L);

    private static final RewriteResponse RESPONSE_WITHOUT_OBJECT_DONE = new RewriteResponse(CopyWriterTest.REQUEST_WITHOUT_OBJECT, CopyWriterTest.RESULT_INFO.toPb(), 42L, true, "token", 42L);

    private StorageOptions options;

    private StorageRpcFactory rpcFactoryMock;

    private StorageRpc storageRpcMock;

    private CopyWriter copyWriter;

    private Blob result;

    @Test
    public void testRewriteWithObject() {
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITH_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITH_OBJECT_DONE);
        EasyMock.replay(storageRpcMock);
        copyWriter = new CopyWriter(options, CopyWriterTest.RESPONSE_WITH_OBJECT);
        Assert.assertEquals(result, copyWriter.getResult());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertEquals(42L, copyWriter.getTotalBytesCopied());
        Assert.assertEquals(42L, copyWriter.getBlobSize());
    }

    @Test
    public void testRewriteWithoutObject() {
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITHOUT_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITHOUT_OBJECT_DONE);
        EasyMock.replay(storageRpcMock);
        copyWriter = new CopyWriter(options, CopyWriterTest.RESPONSE_WITHOUT_OBJECT);
        Assert.assertEquals(result, copyWriter.getResult());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertEquals(42L, copyWriter.getTotalBytesCopied());
        Assert.assertEquals(42L, copyWriter.getBlobSize());
    }

    @Test
    public void testRewriteWithObjectMultipleRequests() {
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITH_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITH_OBJECT);
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITH_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITH_OBJECT_DONE);
        EasyMock.replay(storageRpcMock);
        copyWriter = new CopyWriter(options, CopyWriterTest.RESPONSE_WITH_OBJECT);
        Assert.assertEquals(result, copyWriter.getResult());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertEquals(42L, copyWriter.getTotalBytesCopied());
        Assert.assertEquals(42L, copyWriter.getBlobSize());
    }

    @Test
    public void testRewriteWithoutObjectMultipleRequests() {
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITHOUT_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITHOUT_OBJECT);
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITHOUT_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITHOUT_OBJECT_DONE);
        EasyMock.replay(storageRpcMock);
        copyWriter = new CopyWriter(options, CopyWriterTest.RESPONSE_WITHOUT_OBJECT);
        Assert.assertEquals(result, copyWriter.getResult());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertEquals(42L, copyWriter.getTotalBytesCopied());
        Assert.assertEquals(42L, copyWriter.getBlobSize());
    }

    @Test
    public void testSaveAndRestoreWithObject() {
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITH_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITH_OBJECT);
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITH_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITH_OBJECT_DONE);
        EasyMock.replay(storageRpcMock);
        copyWriter = new CopyWriter(options, CopyWriterTest.RESPONSE_WITH_OBJECT);
        copyWriter.copyChunk();
        Assert.assertTrue((!(copyWriter.isDone())));
        Assert.assertEquals(21L, copyWriter.getTotalBytesCopied());
        Assert.assertEquals(42L, copyWriter.getBlobSize());
        RestorableState<CopyWriter> rewriterState = copyWriter.capture();
        CopyWriter restoredRewriter = rewriterState.restore();
        Assert.assertEquals(result, restoredRewriter.getResult());
        Assert.assertTrue(restoredRewriter.isDone());
        Assert.assertEquals(42L, restoredRewriter.getTotalBytesCopied());
        Assert.assertEquals(42L, restoredRewriter.getBlobSize());
    }

    @Test
    public void testSaveAndRestoreWithoutObject() {
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITHOUT_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITHOUT_OBJECT);
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITHOUT_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITHOUT_OBJECT_DONE);
        EasyMock.replay(storageRpcMock);
        copyWriter = new CopyWriter(options, CopyWriterTest.RESPONSE_WITHOUT_OBJECT);
        copyWriter.copyChunk();
        Assert.assertTrue((!(copyWriter.isDone())));
        Assert.assertEquals(21L, copyWriter.getTotalBytesCopied());
        Assert.assertEquals(42L, copyWriter.getBlobSize());
        RestorableState<CopyWriter> rewriterState = copyWriter.capture();
        CopyWriter restoredRewriter = rewriterState.restore();
        Assert.assertEquals(result, restoredRewriter.getResult());
        Assert.assertTrue(restoredRewriter.isDone());
        Assert.assertEquals(42L, restoredRewriter.getTotalBytesCopied());
        Assert.assertEquals(42L, restoredRewriter.getBlobSize());
    }

    @Test
    public void testSaveAndRestoreWithResult() {
        EasyMock.expect(storageRpcMock.continueRewrite(CopyWriterTest.RESPONSE_WITH_OBJECT)).andReturn(CopyWriterTest.RESPONSE_WITH_OBJECT_DONE);
        EasyMock.replay(storageRpcMock);
        copyWriter = new CopyWriter(options, CopyWriterTest.RESPONSE_WITH_OBJECT);
        copyWriter.copyChunk();
        Assert.assertEquals(result, copyWriter.getResult());
        Assert.assertTrue(copyWriter.isDone());
        Assert.assertEquals(42L, copyWriter.getTotalBytesCopied());
        Assert.assertEquals(42L, copyWriter.getBlobSize());
        RestorableState<CopyWriter> rewriterState = copyWriter.capture();
        CopyWriter restoredRewriter = rewriterState.restore();
        Assert.assertEquals(result, restoredRewriter.getResult());
        Assert.assertTrue(restoredRewriter.isDone());
        Assert.assertEquals(42L, restoredRewriter.getTotalBytesCopied());
        Assert.assertEquals(42L, restoredRewriter.getBlobSize());
    }
}

