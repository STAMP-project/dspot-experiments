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


import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.firestore.spi.v1.FirestoreRpc;
import com.google.firestore.v1.BatchGetDocumentsRequest;
import com.google.firestore.v1.ListCollectionIdsRequest;
import java.util.List;
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
public class FirestoreTest {
    @Spy
    private FirestoreImpl firestoreMock = new FirestoreImpl(FirestoreOptions.newBuilder().setProjectId("test-project").build(), Mockito.mock(FirestoreRpc.class));

    @Captor
    private ArgumentCaptor<BatchGetDocumentsRequest> getAllCapture;

    @Captor
    private ArgumentCaptor<ListCollectionIdsRequest> listCollectionIdsCapture;

    @Captor
    private ArgumentCaptor<ApiStreamObserver> streamObserverCapture;

    @Test
    public void encodeFieldPath() {
        Assert.assertEquals("foo", FieldPath.of("foo").getEncodedPath());
        Assert.assertEquals("foo.bar", FieldPath.of("foo", "bar").getEncodedPath());
        Assert.assertEquals("`.`", FieldPath.of(".").getEncodedPath());
        Assert.assertEquals("`\\``", FieldPath.of("`").getEncodedPath());
        Assert.assertEquals("foo.`.`.`\\\\`", FieldPath.of("foo", ".", "\\").getEncodedPath());
        Assert.assertEquals("`.\\\\.\\\\.`", FieldPath.of(".\\.\\.").getEncodedPath());
    }

    @Test
    public void illegalFieldPath() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.SINGLE_FIELD_PROTO)).when(firestoreMock).streamRequest(getAllCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        DocumentReference doc = firestoreMock.document("coll/doc");
        DocumentSnapshot snapshot = doc.get().get();
        char[] prohibited = new char[]{ '*', '~', '/', '[', ']' };
        for (char c : prohibited) {
            try {
                snapshot.contains((("foo" + c) + "bar"));
                Assert.fail();
            } catch (IllegalArgumentException e) {
                Assert.assertEquals("Use FieldPath.of() for field names containing '?*/[]'.", e.getMessage());
            }
        }
    }

    @Test
    public void exposesOptions() {
        Assert.assertEquals("test-project", firestoreMock.getOptions().getProjectId());
    }

    @Test
    public void getAll() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.SINGLE_FIELD_PROTO, LocalFirestoreHelper.SINGLE_FIELD_PROTO, LocalFirestoreHelper.SINGLE_FIELD_PROTO, LocalFirestoreHelper.SINGLE_FIELD_PROTO)).when(firestoreMock).streamRequest(getAllCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        DocumentReference doc1 = firestoreMock.document("coll/doc1");
        DocumentReference doc2 = firestoreMock.document("coll/doc2");
        DocumentReference doc3 = firestoreMock.document("coll/doc3");
        DocumentReference doc4 = firestoreMock.document("coll/doc4");
        List<DocumentSnapshot> snapshot = firestoreMock.getAll(doc1, doc2, doc4, doc3).get();
        Assert.assertEquals("doc1", snapshot.get(0).getId());
        Assert.assertEquals("doc2", snapshot.get(1).getId());
        // Note that we sort based on the order in the getAll() call.
        Assert.assertEquals("doc4", snapshot.get(2).getId());
        Assert.assertEquals("doc3", snapshot.get(3).getId());
    }

    @Test
    public void getAllWithFieldMask() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.SINGLE_FIELD_PROTO)).when(firestoreMock).streamRequest(getAllCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        DocumentReference doc1 = firestoreMock.document("coll/doc1");
        FieldMask fieldMask = FieldMask.of(FieldPath.of("foo", "bar"));
        firestoreMock.getAll(new DocumentReference[]{ doc1 }, fieldMask).get();
        BatchGetDocumentsRequest request = getAllCapture.getValue();
        Assert.assertEquals(1, request.getMask().getFieldPathsCount());
        Assert.assertEquals("foo.bar", request.getMask().getFieldPaths(0));
    }

    @Test
    public void arrayUnionEquals() {
        FieldValue arrayUnion1 = FieldValue.arrayUnion("foo", "bar");
        FieldValue arrayUnion2 = FieldValue.arrayUnion("foo", "bar");
        FieldValue arrayUnion3 = FieldValue.arrayUnion("foo", "baz");
        FieldValue arrayRemove = FieldValue.arrayRemove("foo", "bar");
        Assert.assertEquals(arrayUnion1, arrayUnion1);
        Assert.assertEquals(arrayUnion1, arrayUnion2);
        Assert.assertNotEquals(arrayUnion1, arrayUnion3);
        Assert.assertNotEquals(arrayUnion1, arrayRemove);
    }

    @Test
    public void arrayRemoveEquals() {
        FieldValue arrayRemove1 = FieldValue.arrayRemove("foo", "bar");
        FieldValue arrayRemove2 = FieldValue.arrayRemove("foo", "bar");
        FieldValue arrayRemove3 = FieldValue.arrayRemove("foo", "baz");
        FieldValue arrayUnion = FieldValue.arrayUnion("foo", "bar");
        Assert.assertEquals(arrayRemove1, arrayRemove1);
        Assert.assertEquals(arrayRemove1, arrayRemove2);
        Assert.assertNotEquals(arrayRemove1, arrayRemove3);
        Assert.assertNotEquals(arrayRemove1, arrayUnion);
    }
}

