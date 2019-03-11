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
package com.google.cloud.examples.storage.snippets;


import Acl.Role.OWNER;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ITBlobSnippets {
    private static final Logger log = Logger.getLogger(ITBlobSnippets.class.getName());

    private static final String BUCKET = RemoteStorageHelper.generateBucketName();

    private static final String BLOB = "blob";

    private static final byte[] EMPTY_CONTENT = new byte[0];

    private static final byte[] CONTENT = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    private static Storage storage;

    private static Blob blob;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBlob() throws IOException {
        BlobSnippets blobSnippets = new BlobSnippets(ITBlobSnippets.blob);
        Assert.assertTrue(blobSnippets.exists());
        Assert.assertArrayEquals(ITBlobSnippets.EMPTY_CONTENT, blobSnippets.getContent());
        try {
            Assert.assertNotNull(blobSnippets.reload());
            Assert.fail("Expected StorageException to be thrown");
        } catch (StorageException ex) {
            // expected
        }
        Blob updatedBlob = blobSnippets.update();
        Assert.assertEquals(ImmutableMap.of("key", "value"), updatedBlob.getMetadata());
        Blob copiedBlob = blobSnippets.copyToStrings(ITBlobSnippets.BUCKET, "copyBlob");
        Assert.assertNotNull(copiedBlob);
        copiedBlob.delete();
        copiedBlob = blobSnippets.copyToId(ITBlobSnippets.BUCKET, "copyBlob");
        Assert.assertNotNull(copiedBlob);
        copiedBlob.delete();
        copiedBlob = blobSnippets.copyToBucket(ITBlobSnippets.BUCKET);
        Assert.assertNotNull(copiedBlob);
        copiedBlob.delete();
        blobSnippets.reload();
        blobSnippets.writer();
        URL signedUrl = blobSnippets.signUrl();
        URLConnection connection = signedUrl.openConnection();
        byte[] readBytes = new byte[ITBlobSnippets.CONTENT.length];
        try (InputStream responseStream = connection.getInputStream()) {
            Assert.assertEquals(ITBlobSnippets.CONTENT.length, responseStream.read(readBytes));
            Assert.assertArrayEquals(ITBlobSnippets.CONTENT, readBytes);
        }
        signedUrl = blobSnippets.signUrlWithSigner(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        connection = signedUrl.openConnection();
        try (InputStream responseStream = connection.getInputStream()) {
            Assert.assertEquals(ITBlobSnippets.CONTENT.length, responseStream.read(readBytes));
            Assert.assertArrayEquals(ITBlobSnippets.CONTENT, readBytes);
        }
        Assert.assertFalse(blobSnippets.delete());
        blobSnippets = new BlobSnippets(ITBlobSnippets.storage.get(ITBlobSnippets.blob.getBucket(), ITBlobSnippets.blob.getName()));
        byte[] subcontent = blobSnippets.readContentRange(1, 8);
        Assert.assertArrayEquals("ello, W".getBytes(StandardCharsets.UTF_8), subcontent);
        Assert.assertNull(blobSnippets.getAcl());
        Assert.assertNotNull(blobSnippets.createAcl());
        Acl updatedAcl = blobSnippets.updateAcl();
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        Set<Acl> acls = Sets.newHashSet(blobSnippets.listAcls());
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertTrue(blobSnippets.deleteAcl());
        Assert.assertNull(blobSnippets.getAcl());
        ITBlobSnippets.storage.delete(BlobId.of(ITBlobSnippets.BUCKET, ITBlobSnippets.BLOB));
    }

    @Test
    public void testMoveBlob() throws IOException {
        BlobSnippets blobSnippets = new BlobSnippets(ITBlobSnippets.blob);
        Blob movedBlob = blobSnippets.moveTo(ITBlobSnippets.BUCKET, "moveBlob");
        Assert.assertNotNull(movedBlob);
        // Assert that the destination blob exists
        Iterator<Blob> blobs = ITBlobSnippets.storage.list(ITBlobSnippets.BUCKET).iterateAll().iterator();
        Blob moveBlob = blobs.next();
        Assert.assertEquals(ITBlobSnippets.BUCKET, moveBlob.getBucket());
        Assert.assertEquals("moveBlob", moveBlob.getName());
        // Assert that the old blob doesn't exist
        Assert.assertFalse(blobs.hasNext());
    }
}

