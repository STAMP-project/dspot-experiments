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


import Role.OWNER;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageRoles;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


public class ITBucketSnippets {
    private static final Logger log = Logger.getLogger(ITBucketSnippets.class.getName());

    private static final String BUCKET = RemoteStorageHelper.generateBucketName();

    private static final String USER_EMAIL = "test@test.com";

    private static final String BLOB1 = "blob1";

    private static final String BLOB2 = "blob2";

    private static final String BLOB3 = "blob3";

    private static final String BLOB4 = "blob4";

    private static Storage storage;

    private static BucketSnippets bucketSnippets;

    private static BucketIamSnippets bucketIamSnippets;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testBucket() throws InterruptedException {
        Assert.assertTrue(ITBucketSnippets.bucketSnippets.exists());
        Bucket bucket = ITBucketSnippets.bucketSnippets.reload();
        Assert.assertNotNull(bucket);
        Bucket updatedBucket = ITBucketSnippets.bucketSnippets.update();
        Assert.assertTrue(updatedBucket.versioningEnabled());
        Blob blob1 = ITBucketSnippets.bucketSnippets.createBlobFromByteArray(ITBucketSnippets.BLOB1);
        Assert.assertNotNull(blob1);
        Blob blob2 = ITBucketSnippets.bucketSnippets.createBlobFromByteArrayWithContentType(ITBucketSnippets.BLOB2);
        Assert.assertNotNull(blob2);
        Blob blob3 = ITBucketSnippets.bucketSnippets.createBlobFromInputStream(ITBucketSnippets.BLOB3);
        Assert.assertNotNull(blob3);
        Blob blob4 = ITBucketSnippets.bucketSnippets.createBlobFromInputStreamWithContentType(ITBucketSnippets.BLOB4);
        Assert.assertNotNull(blob4);
        Set<Blob> blobSet = Sets.newHashSet(ITBucketSnippets.bucketSnippets.listBlobs().iterateAll());
        while ((blobSet.size()) < 4) {
            Thread.sleep(500);
            blobSet = Sets.newHashSet(ITBucketSnippets.bucketSnippets.listBlobs().iterateAll());
        } 
        Assert.assertTrue(blobSet.contains(blob1));
        Assert.assertTrue(blobSet.contains(blob2));
        Assert.assertTrue(blobSet.contains(blob3));
        Assert.assertTrue(blobSet.contains(blob4));
        blob1 = ITBucketSnippets.bucketSnippets.getBlob(ITBucketSnippets.BLOB1, blob1.getGeneration());
        Assert.assertEquals(ITBucketSnippets.BLOB1, blob1.getName());
        List<Blob> blobs = ITBucketSnippets.bucketSnippets.getBlobFromStrings(ITBucketSnippets.BLOB2, ITBucketSnippets.BLOB3);
        Assert.assertEquals(ITBucketSnippets.BLOB2, blobs.get(0).getName());
        Assert.assertEquals(ITBucketSnippets.BLOB3, blobs.get(1).getName());
        blobs = ITBucketSnippets.bucketSnippets.getBlobFromStringIterable(ITBucketSnippets.BLOB3, ITBucketSnippets.BLOB4);
        Assert.assertEquals(ITBucketSnippets.BLOB3, blobs.get(0).getName());
        Assert.assertEquals(ITBucketSnippets.BLOB4, blobs.get(1).getName());
        // test ACLs
        Assert.assertNull(ITBucketSnippets.bucketSnippets.getAcl());
        Assert.assertNotNull(ITBucketSnippets.bucketSnippets.createAcl());
        Acl updatedAcl = ITBucketSnippets.bucketSnippets.updateAcl();
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        Set<Acl> acls = Sets.newHashSet(ITBucketSnippets.bucketSnippets.listAcls());
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertTrue(ITBucketSnippets.bucketSnippets.deleteAcl());
        Assert.assertNull(ITBucketSnippets.bucketSnippets.getAcl());
        // test default ACLs
        Assert.assertNull(ITBucketSnippets.bucketSnippets.getDefaultAcl());
        Assert.assertNotNull(ITBucketSnippets.bucketSnippets.createDefaultAcl());
        updatedAcl = ITBucketSnippets.bucketSnippets.updateDefaultAcl();
        Assert.assertEquals(OWNER, updatedAcl.getRole());
        acls = Sets.newHashSet(ITBucketSnippets.bucketSnippets.listDefaultAcls());
        Assert.assertTrue(acls.contains(updatedAcl));
        Assert.assertTrue(ITBucketSnippets.bucketSnippets.deleteDefaultAcl());
        Assert.assertNull(ITBucketSnippets.bucketSnippets.getDefaultAcl());
        thrown.expect(StorageException.class);
        Assert.assertTrue(ITBucketSnippets.bucketSnippets.delete());
    }

    @Test
    public void testListBucketIamMembers() {
        // Test an added Bucket-level IAM member is listed
        Policy policy = ITBucketSnippets.storage.getIamPolicy(ITBucketSnippets.BUCKET);
        policy = ITBucketSnippets.storage.setIamPolicy(ITBucketSnippets.BUCKET, policy.toBuilder().removeRole(StorageRoles.admin()).build());
        Assert.assertNull(policy.getBindings().get(StorageRoles.admin()));
        policy = ITBucketSnippets.storage.setIamPolicy(ITBucketSnippets.BUCKET, policy.toBuilder().addIdentity(StorageRoles.admin(), Identity.user(ITBucketSnippets.USER_EMAIL)).build());
        Assert.assertTrue(policy.getBindings().get(StorageRoles.admin()).contains(Identity.user(ITBucketSnippets.USER_EMAIL)));
        Policy snippetPolicy = ITBucketSnippets.bucketIamSnippets.listBucketIamMembers(ITBucketSnippets.BUCKET);
        Assert.assertTrue(snippetPolicy.getBindings().get(StorageRoles.admin()).contains(Identity.user(ITBucketSnippets.USER_EMAIL)));
    }

    @Test
    public void testAddBucketIamMemeber() {
        // Test a member is added to Bucket-level IAM
        Policy policy = ITBucketSnippets.storage.getIamPolicy(ITBucketSnippets.BUCKET);
        policy = ITBucketSnippets.storage.setIamPolicy(ITBucketSnippets.BUCKET, policy.toBuilder().removeRole(StorageRoles.admin()).build());
        Assert.assertNull(policy.getBindings().get(StorageRoles.admin()));
        Policy snippetPolicy = ITBucketSnippets.bucketIamSnippets.addBucketIamMember(ITBucketSnippets.BUCKET, StorageRoles.admin(), Identity.user(ITBucketSnippets.USER_EMAIL));
        Assert.assertTrue(snippetPolicy.getBindings().get(StorageRoles.admin()).contains(Identity.user(ITBucketSnippets.USER_EMAIL)));
    }

    @Test
    public void testRemoveBucketIamMember() {
        // Test a member is removed from Bucket-level IAM
        Policy policy = ITBucketSnippets.storage.getIamPolicy(ITBucketSnippets.BUCKET);
        policy = ITBucketSnippets.storage.setIamPolicy(ITBucketSnippets.BUCKET, policy.toBuilder().removeRole(StorageRoles.admin()).build());
        Assert.assertNull(policy.getBindings().get(StorageRoles.admin()));
        policy = ITBucketSnippets.storage.setIamPolicy(ITBucketSnippets.BUCKET, policy.toBuilder().addIdentity(StorageRoles.admin(), Identity.user(ITBucketSnippets.USER_EMAIL)).build());
        Assert.assertTrue(policy.getBindings().get(StorageRoles.admin()).contains(Identity.user(ITBucketSnippets.USER_EMAIL)));
        Policy snippetPolicy = ITBucketSnippets.bucketIamSnippets.removeBucketIamMember(ITBucketSnippets.BUCKET, StorageRoles.admin(), Identity.user(ITBucketSnippets.USER_EMAIL));
        Assert.assertNull(snippetPolicy.getBindings().get(StorageRoles.admin()));
    }
}

