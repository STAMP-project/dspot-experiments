/**
 * Copyright 2017 Google LLC
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
package com.google.cloud.firestore;


import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.firestore.spi.v1.FirestoreRpc;
import com.google.firestore.v1.CommitRequest;
import com.google.firestore.v1.CommitResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CollectionReferenceTest {
    @Spy
    private FirestoreImpl firestoreMock = new FirestoreImpl(FirestoreOptions.newBuilder().setProjectId("test-project").build(), Mockito.mock(FirestoreRpc.class));

    @Captor
    private ArgumentCaptor<CommitRequest> argCaptor;

    private CollectionReference collectionReference;

    @Test
    public void getDocument() {
        DocumentReference documentReference = collectionReference.document("doc");
        Assert.assertEquals("doc", documentReference.getId());
        documentReference = collectionReference.document();
        Assert.assertEquals(20, documentReference.getId().length());
        try {
            collectionReference.document("");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("'path' must be a non-empty String"));
        }
    }

    @Test
    public void getParent() {
        DocumentReference documentReference = collectionReference.getParent();
        Assert.assertNull(documentReference);
        CollectionReference subcollection = collectionReference.document("doc").collection("subcoll");
        Assert.assertEquals("subcoll", subcollection.getId());
        Assert.assertEquals("coll/doc/subcoll", subcollection.getPath());
        documentReference = subcollection.getParent();
        Assert.assertEquals("doc", documentReference.getId());
    }

    @Test
    public void addDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(argCaptor.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        ApiFuture<DocumentReference> future = collectionReference.add(LocalFirestoreHelper.SINGLE_FIELD_MAP);
        DocumentReference documentReference = future.get();
        Assert.assertEquals(20, documentReference.getId().length());
        future = collectionReference.add(LocalFirestoreHelper.SINGLE_FIELD_OBJECT);
        documentReference = future.get();
        Assert.assertEquals(20, documentReference.getId().length());
        Assert.assertTrue(matchesIgnoresName(argCaptor.getValue()));
    }
}

