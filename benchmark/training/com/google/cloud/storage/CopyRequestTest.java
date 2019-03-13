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


import Storage.CopyRequest;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CopyRequestTest {
    private static final String SOURCE_BUCKET_NAME = "b0";

    private static final String SOURCE_BLOB_NAME = "o0";

    private static final String TARGET_BUCKET_NAME = "b1";

    private static final String TARGET_BLOB_NAME = "o1";

    private static final String TARGET_BLOB_CONTENT_TYPE = "contentType";

    private static final BlobId SOURCE_BLOB_ID = BlobId.of(CopyRequestTest.SOURCE_BUCKET_NAME, CopyRequestTest.SOURCE_BLOB_NAME);

    private static final BlobId TARGET_BLOB_ID = BlobId.of(CopyRequestTest.TARGET_BUCKET_NAME, CopyRequestTest.TARGET_BLOB_NAME);

    private static final BlobInfo TARGET_BLOB_INFO = BlobInfo.newBuilder(CopyRequestTest.TARGET_BLOB_ID).setContentType(CopyRequestTest.TARGET_BLOB_CONTENT_TYPE).build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCopyRequest() {
        Storage.CopyRequest copyRequest1 = CopyRequest.newBuilder().setSource(CopyRequestTest.SOURCE_BLOB_ID).setSourceOptions(BlobSourceOption.generationMatch(1)).setTarget(CopyRequestTest.TARGET_BLOB_INFO, BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ)).build();
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest1.getSource());
        Assert.assertEquals(1, copyRequest1.getSourceOptions().size());
        Assert.assertEquals(BlobSourceOption.generationMatch(1), copyRequest1.getSourceOptions().get(0));
        Assert.assertEquals(CopyRequestTest.TARGET_BLOB_INFO, copyRequest1.getTarget());
        Assert.assertTrue(copyRequest1.overrideInfo());
        Assert.assertEquals(1, copyRequest1.getTargetOptions().size());
        Assert.assertEquals(BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ), copyRequest1.getTargetOptions().get(0));
        Storage.CopyRequest copyRequest2 = CopyRequest.newBuilder().setSource(CopyRequestTest.SOURCE_BUCKET_NAME, CopyRequestTest.SOURCE_BLOB_NAME).setTarget(CopyRequestTest.TARGET_BLOB_ID).build();
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest2.getSource());
        Assert.assertEquals(BlobInfo.newBuilder(CopyRequestTest.TARGET_BLOB_ID).build(), copyRequest2.getTarget());
        Assert.assertFalse(copyRequest2.overrideInfo());
        Storage.CopyRequest copyRequest3 = CopyRequest.newBuilder().setSource(CopyRequestTest.SOURCE_BLOB_ID).setTarget(CopyRequestTest.TARGET_BLOB_INFO, ImmutableList.of(BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ))).build();
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest3.getSource());
        Assert.assertEquals(CopyRequestTest.TARGET_BLOB_INFO, copyRequest3.getTarget());
        Assert.assertTrue(copyRequest3.overrideInfo());
        Assert.assertEquals(ImmutableList.of(BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ)), copyRequest3.getTargetOptions());
    }

    @Test
    public void testCopyRequestOf() {
        Storage.CopyRequest copyRequest1 = CopyRequest.of(CopyRequestTest.SOURCE_BLOB_ID, CopyRequestTest.TARGET_BLOB_INFO);
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest1.getSource());
        Assert.assertEquals(CopyRequestTest.TARGET_BLOB_INFO, copyRequest1.getTarget());
        Assert.assertTrue(copyRequest1.overrideInfo());
        Storage.CopyRequest copyRequest2 = CopyRequest.of(CopyRequestTest.SOURCE_BLOB_ID, CopyRequestTest.TARGET_BLOB_NAME);
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest2.getSource());
        Assert.assertEquals(BlobInfo.newBuilder(BlobId.of(CopyRequestTest.SOURCE_BUCKET_NAME, CopyRequestTest.TARGET_BLOB_NAME)).build(), copyRequest2.getTarget());
        Assert.assertFalse(copyRequest2.overrideInfo());
        Storage.CopyRequest copyRequest3 = CopyRequest.of(CopyRequestTest.SOURCE_BUCKET_NAME, CopyRequestTest.SOURCE_BLOB_NAME, CopyRequestTest.TARGET_BLOB_INFO);
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest3.getSource());
        Assert.assertEquals(CopyRequestTest.TARGET_BLOB_INFO, copyRequest3.getTarget());
        Assert.assertTrue(copyRequest3.overrideInfo());
        Storage.CopyRequest copyRequest4 = CopyRequest.of(CopyRequestTest.SOURCE_BUCKET_NAME, CopyRequestTest.SOURCE_BLOB_NAME, CopyRequestTest.TARGET_BLOB_NAME);
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest4.getSource());
        Assert.assertEquals(BlobInfo.newBuilder(BlobId.of(CopyRequestTest.SOURCE_BUCKET_NAME, CopyRequestTest.TARGET_BLOB_NAME)).build(), copyRequest4.getTarget());
        Assert.assertFalse(copyRequest4.overrideInfo());
        Storage.CopyRequest copyRequest5 = CopyRequest.of(CopyRequestTest.SOURCE_BLOB_ID, CopyRequestTest.TARGET_BLOB_ID);
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest5.getSource());
        Assert.assertEquals(BlobInfo.newBuilder(CopyRequestTest.TARGET_BLOB_ID).build(), copyRequest5.getTarget());
        Assert.assertFalse(copyRequest5.overrideInfo());
        Storage.CopyRequest copyRequest6 = CopyRequest.of(CopyRequestTest.SOURCE_BUCKET_NAME, CopyRequestTest.SOURCE_BLOB_NAME, CopyRequestTest.TARGET_BLOB_ID);
        Assert.assertEquals(CopyRequestTest.SOURCE_BLOB_ID, copyRequest6.getSource());
        Assert.assertEquals(BlobInfo.newBuilder(CopyRequestTest.TARGET_BLOB_ID).build(), copyRequest6.getTarget());
        Assert.assertFalse(copyRequest6.overrideInfo());
    }
}

