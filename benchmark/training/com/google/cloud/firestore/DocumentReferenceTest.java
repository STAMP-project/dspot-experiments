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


import BatchGetDocumentsResponse.Builder;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.Timestamp;
import com.google.cloud.com.google.protobuf.Timestamp;
import com.google.cloud.firestore.spi.v1.FirestoreRpc;
import com.google.common.collect.ImmutableList;
import com.google.firestore.v1.BatchGetDocumentsRequest;
import com.google.firestore.v1.BatchGetDocumentsResponse;
import com.google.firestore.v1.CommitRequest;
import com.google.firestore.v1.CommitResponse;
import com.google.firestore.v1.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class DocumentReferenceTest {
    @Spy
    private FirestoreImpl firestoreMock = new FirestoreImpl(FirestoreOptions.newBuilder().setProjectId("test-project").build(), Mockito.mock(FirestoreRpc.class));

    @Captor
    private ArgumentCaptor<CommitRequest> commitCapture;

    @Captor
    private ArgumentCaptor<BatchGetDocumentsRequest> getAllCapture;

    @Captor
    private ArgumentCaptor<ApiStreamObserver> streamObserverCapture;

    private DocumentReference documentReference;

    @Test
    public void equals() {
        DocumentReference doc1 = documentReference.collection("subcoll").document("doc");
        DocumentReference doc2 = documentReference.collection("subcoll").document("doc");
        Assert.assertEquals(doc1, doc2);
    }

    @Test
    public void getCollection() {
        CollectionReference collectionReference = documentReference.collection("subcoll");
        Assert.assertEquals("subcoll", collectionReference.getId());
    }

    @Test
    public void getPath() {
        Assert.assertEquals(LocalFirestoreHelper.DOCUMENT_PATH, documentReference.getPath());
    }

    @Test
    public void getParent() {
        CollectionReference collectionReference = documentReference.getParent();
        Assert.assertEquals("coll", collectionReference.getId());
    }

    @Test
    public void serializeBasicTypes() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_MAP).get();
        documentReference.set(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_OBJECT).get();
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_PROTO));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getAllValues().get(0));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getAllValues().get(1));
    }

    @Test
    public void serializeDocumentReference() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.map("docRef", ((Object) (documentReference)))).get();
        Map<String, Value> documentReferenceFields = new HashMap<>();
        documentReferenceFields.put("docRef", Value.newBuilder().setReferenceValue(LocalFirestoreHelper.DOCUMENT_NAME).build());
        LocalFirestoreHelper.assertCommitEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.set(documentReferenceFields)), commitCapture.getValue());
    }

    @Test
    public void deserializeBasicTypes() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_PROTO)).when(firestoreMock).streamRequest(getAllCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        Mockito.doReturn(true).when(firestoreMock).areTimestampsInSnapshotsEnabled();
        DocumentSnapshot snapshot = documentReference.get().get();
        Assert.assertEquals(snapshot.getData(), LocalFirestoreHelper.ALL_SUPPORTED_TYPES_MAP);
        LocalFirestoreHelper.AllSupportedTypes pojo = snapshot.toObject(LocalFirestoreHelper.AllSupportedTypes.class);
        Assert.assertEquals(new LocalFirestoreHelper.AllSupportedTypes(), pojo);
        Assert.assertEquals("bar", snapshot.get("foo"));
        Assert.assertEquals("bar", snapshot.get(FieldPath.of("foo")));
        Assert.assertTrue(snapshot.contains("foo"));
        Assert.assertTrue(snapshot.contains(FieldPath.of("foo")));
        Assert.assertEquals("bar", snapshot.getString("foo"));
        Assert.assertEquals("bar", snapshot.getString("objectValue.foo"));
        Assert.assertEquals("bar", snapshot.get(FieldPath.of("objectValue", "foo")));
        Assert.assertTrue(snapshot.contains("objectValue.foo"));
        Assert.assertTrue(snapshot.contains(FieldPath.of("objectValue", "foo")));
        Assert.assertEquals(((Double) (0.0)), snapshot.getDouble("doubleValue"));
        Assert.assertEquals(((Long) (0L)), snapshot.getLong("longValue"));
        Assert.assertEquals(true, snapshot.getBoolean("trueValue"));
        Assert.assertEquals(LocalFirestoreHelper.DATE, snapshot.getDate("dateValue"));
        Assert.assertEquals(LocalFirestoreHelper.TIMESTAMP, snapshot.getTimestamp("timestampValue"));
        Assert.assertEquals(LocalFirestoreHelper.BLOB, snapshot.getBlob("bytesValue"));
        Assert.assertEquals(LocalFirestoreHelper.BLOB.hashCode(), snapshot.getBlob("bytesValue").hashCode());
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, snapshot.getBlob("bytesValue").toBytes());
        Assert.assertEquals(LocalFirestoreHelper.GEO_POINT, snapshot.getGeoPoint("geoPointValue"));
        Assert.assertEquals(LocalFirestoreHelper.GEO_POINT.hashCode(), snapshot.getGeoPoint("geoPointValue").hashCode());
        Assert.assertEquals(LocalFirestoreHelper.GEO_POINT.getLatitude(), snapshot.getGeoPoint("geoPointValue").getLatitude(), 0);
        Assert.assertEquals(LocalFirestoreHelper.GEO_POINT.getLongitude(), snapshot.getGeoPoint("geoPointValue").getLongitude(), 0);
        Assert.assertNull(snapshot.get("nullValue"));
        Assert.assertTrue(snapshot.contains("nullValue"));
        Assert.assertTrue(snapshot.contains("objectValue.foo"));
        Assert.assertFalse(snapshot.contains("objectValue.bar"));
        Assert.assertTrue(snapshot.exists());
        Assert.assertEquals(Timestamp.ofTimeSecondsAndNanos(1, 2), snapshot.getCreateTime());
        Assert.assertEquals(Timestamp.ofTimeSecondsAndNanos(3, 4), snapshot.getUpdateTime());
        Assert.assertEquals(Timestamp.ofTimeSecondsAndNanos(5, 6), snapshot.getReadTime());
        Assert.assertEquals(LocalFirestoreHelper.get(), getAllCapture.getValue());
    }

    @Test
    public void deserializeDocumentReference() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.map("docRef", Value.newBuilder().setReferenceValue(LocalFirestoreHelper.DOCUMENT_NAME).build()))).when(firestoreMock).streamRequest(getAllCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        DocumentSnapshot snapshot = documentReference.get().get();
        Assert.assertEquals(documentReference, snapshot.getData().get("docRef"));
        Assert.assertEquals(documentReference, snapshot.getReference());
    }

    @Test
    public void deserializesDates() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_PROTO)).when(firestoreMock).streamRequest(getAllCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        DocumentSnapshot snapshot = documentReference.get().get();
        Mockito.doReturn(false).when(firestoreMock).areTimestampsInSnapshotsEnabled();
        Assert.assertEquals(LocalFirestoreHelper.DATE, snapshot.get("dateValue"));
        Assert.assertEquals(LocalFirestoreHelper.TIMESTAMP.toDate(), snapshot.get("timestampValue"));
        Assert.assertEquals(LocalFirestoreHelper.DATE, snapshot.getData().get("dateValue"));
        Assert.assertEquals(LocalFirestoreHelper.TIMESTAMP.toDate(), snapshot.getData().get("timestampValue"));
        Mockito.doReturn(true).when(firestoreMock).areTimestampsInSnapshotsEnabled();
        Assert.assertEquals(Timestamp.of(LocalFirestoreHelper.DATE), snapshot.get("dateValue"));
        Assert.assertEquals(LocalFirestoreHelper.TIMESTAMP, snapshot.get("timestampValue"));
        Assert.assertEquals(Timestamp.of(LocalFirestoreHelper.DATE), snapshot.getData().get("dateValue"));
        Assert.assertEquals(LocalFirestoreHelper.TIMESTAMP, snapshot.getData().get("timestampValue"));
    }

    @Test
    public void notFound() throws Exception {
        final BatchGetDocumentsResponse.Builder getDocumentResponse = BatchGetDocumentsResponse.newBuilder();
        getDocumentResponse.setMissing(LocalFirestoreHelper.DOCUMENT_NAME);
        getDocumentResponse.setReadTime(com.google.protobuf.Timestamp.newBuilder().setSeconds(5).setNanos(6));
        Mockito.doAnswer(LocalFirestoreHelper.streamingResponse(getDocumentResponse.build())).when(firestoreMock).streamRequest(getAllCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        DocumentSnapshot snapshot = documentReference.get().get();
        Assert.assertEquals(documentReference, snapshot.getReference());
        Assert.assertFalse(snapshot.exists());
        Assert.assertEquals(snapshot.getReadTime(), Timestamp.ofTimeSecondsAndNanos(5, 6));
        Assert.assertNull(snapshot.getData());
    }

    @Test
    public void deleteDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_DELETE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.delete().get();
        documentReference.delete(Precondition.updatedAt(Timestamp.ofTimeSecondsAndNanos(479978400, 123000000))).get();
        List<CommitRequest> commitRequests = commitCapture.getAllValues();
        LocalFirestoreHelper.assertCommitEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.delete()), commitRequests.get(0));
        com.google.firestore.v1.Precondition.Builder precondition = com.google.firestore.v1.Precondition.newBuilder();
        precondition.getUpdateTimeBuilder().setSeconds(479978400).setNanos(123000000);
        LocalFirestoreHelper.assertCommitEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.delete(precondition.build())), commitRequests.get(1));
    }

    @Test
    public void createDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.create(LocalFirestoreHelper.SINGLE_FIELD_MAP).get();
        documentReference.create(LocalFirestoreHelper.SINGLE_FIELD_OBJECT).get();
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.create(LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        List<CommitRequest> commitRequests = commitCapture.getAllValues();
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitRequests.get(0));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitRequests.get(1));
    }

    @Test
    public void createWithServerTimestamp() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.create(LocalFirestoreHelper.SERVER_TIMESTAMP_MAP).get();
        documentReference.create(LocalFirestoreHelper.SERVER_TIMESTAMP_OBJECT).get();
        CommitRequest create = LocalFirestoreHelper.commit(LocalFirestoreHelper.transform(LocalFirestoreHelper.CREATE_PRECONDITION, "foo", LocalFirestoreHelper.serverTimestamp(), "inner.bar", LocalFirestoreHelper.serverTimestamp()));
        List<CommitRequest> commitRequests = commitCapture.getAllValues();
        LocalFirestoreHelper.assertCommitEquals(create, commitRequests.get(0));
        LocalFirestoreHelper.assertCommitEquals(create, commitRequests.get(1));
    }

    @Test
    public void setWithServerTimestamp() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.FIELD_TRANSFORM_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.SERVER_TIMESTAMP_MAP).get();
        documentReference.set(LocalFirestoreHelper.SERVER_TIMESTAMP_OBJECT).get();
        CommitRequest set = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(LocalFirestoreHelper.SERVER_TIMESTAMP_PROTO), LocalFirestoreHelper.SERVER_TIMESTAMP_TRANSFORM);
        List<CommitRequest> commitRequests = commitCapture.getAllValues();
        LocalFirestoreHelper.assertCommitEquals(set, commitRequests.get(0));
        LocalFirestoreHelper.assertCommitEquals(set, commitRequests.get(1));
    }

    @Test
    public void updateWithServerTimestamp() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.FIELD_TRANSFORM_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.update(LocalFirestoreHelper.SERVER_TIMESTAMP_MAP).get();
        CommitRequest update = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(Collections.<String, Value>emptyMap(), Collections.singletonList("inner")), LocalFirestoreHelper.SERVER_TIMESTAMP_TRANSFORM);
        LocalFirestoreHelper.assertCommitEquals(update, commitCapture.getValue());
        documentReference.update("foo", FieldValue.serverTimestamp(), "inner.bar", FieldValue.serverTimestamp());
        update = LocalFirestoreHelper.commit(LocalFirestoreHelper.transform(LocalFirestoreHelper.UPDATE_PRECONDITION, "foo", LocalFirestoreHelper.serverTimestamp(), "inner.bar", LocalFirestoreHelper.serverTimestamp()));
        LocalFirestoreHelper.assertCommitEquals(update, commitCapture.getValue());
    }

    @Test
    public void mergeWithServerTimestamps() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.SERVER_TIMESTAMP_MAP, SetOptions.mergeFields("inner.bar")).get();
        documentReference.set(LocalFirestoreHelper.SERVER_TIMESTAMP_OBJECT, SetOptions.mergeFields("inner.bar")).get();
        CommitRequest set = LocalFirestoreHelper.commit(LocalFirestoreHelper.transform("inner.bar", LocalFirestoreHelper.serverTimestamp()));
        List<CommitRequest> commitRequests = commitCapture.getAllValues();
        LocalFirestoreHelper.assertCommitEquals(set, commitRequests.get(0));
        LocalFirestoreHelper.assertCommitEquals(set, commitRequests.get(1));
    }

    @Test
    public void setWithArrayUnion() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.FIELD_TRANSFORM_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.map("foo", FieldValue.arrayUnion("bar", LocalFirestoreHelper.map("foo", "baz")))).get();
        CommitRequest set = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(Collections.<String, Value>emptyMap()), LocalFirestoreHelper.transform("foo", LocalFirestoreHelper.arrayUnion(LocalFirestoreHelper.string("bar"), LocalFirestoreHelper.object("foo", LocalFirestoreHelper.string("baz")))));
        CommitRequest commitRequest = commitCapture.getValue();
        LocalFirestoreHelper.assertCommitEquals(set, commitRequest);
    }

    @Test
    public void setWithArrayRemove() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.FIELD_TRANSFORM_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.map("foo", FieldValue.arrayRemove("bar", LocalFirestoreHelper.map("foo", "baz")))).get();
        CommitRequest set = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(Collections.<String, Value>emptyMap()), LocalFirestoreHelper.transform("foo", LocalFirestoreHelper.arrayRemove(LocalFirestoreHelper.string("bar"), LocalFirestoreHelper.object("foo", LocalFirestoreHelper.string("baz")))));
        CommitRequest commitRequest = commitCapture.getValue();
        LocalFirestoreHelper.assertCommitEquals(set, commitRequest);
    }

    @Test
    public void serverTimestampInArray() {
        Map<String, Object> list = new HashMap<>();
        list.put("foo", ImmutableList.of(FieldValue.serverTimestamp()));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (FirestoreException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.serverTimestamp() is not supported inside of an array."));
        }
        list.clear();
        list.put("a", ImmutableList.of(ImmutableList.of("b", LocalFirestoreHelper.map("c", FieldValue.serverTimestamp()))));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (FirestoreException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.serverTimestamp() is not supported inside of an array."));
        }
    }

    @Test
    public void deleteInArray() {
        Map<String, Object> list = new HashMap<>();
        list.put("foo", ImmutableList.of(FieldValue.delete()));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.delete() is not supported at field 'foo.`0`'."));
        }
        list.clear();
        list.put("a", ImmutableList.of(ImmutableList.of("b", LocalFirestoreHelper.map("c", FieldValue.delete()))));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.delete() is not supported at field 'a.`0`.`1`.c'."));
        }
    }

    @Test
    public void arrayUnionInArray() {
        Map<String, Object> list = new HashMap<>();
        list.put("foo", ImmutableList.of(FieldValue.arrayUnion("foo")));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (FirestoreException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.arrayUnion() is not supported inside of an array."));
        }
        list.clear();
        list.put("a", ImmutableList.of(ImmutableList.of("b", LocalFirestoreHelper.map("c", FieldValue.arrayUnion("foo")))));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (FirestoreException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.arrayUnion() is not supported inside of an array."));
        }
    }

    @Test
    public void arrayUnionInArrayUnion() {
        Map<String, Object> data = new HashMap<>();
        data.put("foo", FieldValue.arrayUnion(FieldValue.arrayUnion("foo")));
        try {
            documentReference.create(data);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("Cannot use FieldValue.arrayUnion() as an argument at field 'foo'."));
        }
    }

    @Test
    public void deleteInArrayUnion() {
        Map<String, Object> data = new HashMap<>();
        data.put("foo", FieldValue.arrayUnion(FieldValue.delete()));
        try {
            documentReference.set(data, SetOptions.merge());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.delete() is not supported at field 'foo'."));
        }
    }

    @Test
    public void arrayRemoveInArray() {
        Map<String, Object> list = new HashMap<>();
        list.put("foo", ImmutableList.of(FieldValue.arrayRemove("foo")));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (FirestoreException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.arrayRemove() is not supported inside of an array."));
        }
        list.clear();
        list.put("a", ImmutableList.of(ImmutableList.of("b", LocalFirestoreHelper.map("c", FieldValue.arrayRemove("foo")))));
        try {
            documentReference.create(list);
            Assert.fail();
        } catch (FirestoreException e) {
            Assert.assertTrue(e.getMessage().endsWith("FieldValue.arrayRemove() is not supported inside of an array."));
        }
    }

    @Test
    public void setDocumentWithMerge() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.SINGLE_FIELD_MAP, SetOptions.merge()).get();
        documentReference.set(LocalFirestoreHelper.SINGLE_FIELD_OBJECT, SetOptions.merge()).get();
        documentReference.set(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_OBJECT, SetOptions.mergeFields("foo")).get();
        documentReference.set(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_OBJECT, SetOptions.mergeFields(Arrays.asList("foo"))).get();
        documentReference.set(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_OBJECT, SetOptions.mergeFieldPaths(Arrays.asList(FieldPath.of("foo")))).get();
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(LocalFirestoreHelper.SINGLE_FIELD_PROTO, Arrays.asList("foo")));
        for (int i = 0; i < 5; ++i) {
            LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getAllValues().get(i));
        }
    }

    @Test
    public void setDocumentWithEmptyMerge() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.map(), SetOptions.merge()).get();
        LocalFirestoreHelper.assertCommitEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.set(Collections.<String, Value>emptyMap(), Collections.<String>emptyList())), commitCapture.getValue());
    }

    @Test
    public void setDocumentWithNestedMerge() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.NESTED_CLASS_OBJECT, SetOptions.mergeFields("first.foo")).get();
        documentReference.set(LocalFirestoreHelper.NESTED_CLASS_OBJECT, SetOptions.mergeFields(Arrays.asList("first.foo"))).get();
        documentReference.set(LocalFirestoreHelper.NESTED_CLASS_OBJECT, SetOptions.mergeFieldPaths(Arrays.asList(FieldPath.of("first", "foo")))).get();
        Map<String, Value> nestedUpdate = new HashMap<>();
        Value.Builder nestedProto = Value.newBuilder();
        nestedProto.getMapValueBuilder().putAllFields(LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        nestedUpdate.put("first", nestedProto.build());
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(nestedUpdate, Arrays.asList("first.foo")));
        for (int i = 0; i < 3; ++i) {
            LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getAllValues().get(i));
        }
    }

    @Test
    public void setMultipleFieldsWithMerge() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.NESTED_CLASS_OBJECT, SetOptions.mergeFields("first.foo", "second.foo", "second.trueValue")).get();
        Map<String, Value> nestedUpdate = new HashMap<>();
        Value.Builder nestedProto = Value.newBuilder();
        nestedProto.getMapValueBuilder().putAllFields(LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        nestedUpdate.put("first", nestedProto.build());
        nestedProto.getMapValueBuilder().putFields("trueValue", Value.newBuilder().setBooleanValue(true).build());
        nestedUpdate.put("second", nestedProto.build());
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(nestedUpdate, Arrays.asList("first.foo", "second.foo", "second.trueValue")));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void setNestedMapWithMerge() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.NESTED_CLASS_OBJECT, SetOptions.mergeFields("first", "second")).get();
        Map<String, Value> nestedUpdate = new HashMap<>();
        Value.Builder nestedProto = Value.newBuilder();
        nestedProto.getMapValueBuilder().putAllFields(LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        nestedUpdate.put("first", nestedProto.build());
        nestedProto.getMapValueBuilder().putAllFields(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_PROTO);
        nestedUpdate.put("second", nestedProto.build());
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(nestedUpdate, Arrays.asList("first", "second")));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void mergeWithDotsInFieldName() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.map("a.b.c", LocalFirestoreHelper.map("d.e", "foo", "f.g", "bar")), SetOptions.mergeFieldPaths(Arrays.asList(FieldPath.of("a.b.c", "d.e")))).get();
        Map<String, Value> nestedUpdate = new HashMap<>();
        Value.Builder nestedProto = Value.newBuilder();
        nestedProto.getMapValueBuilder().putFields("d.e", Value.newBuilder().setStringValue("foo").build());
        nestedUpdate.put("a.b.c", nestedProto.build());
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(nestedUpdate, Arrays.asList("`a.b.c`.`d.e`")));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void extractFieldMaskFromMerge() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.set(LocalFirestoreHelper.NESTED_CLASS_OBJECT, SetOptions.merge()).get();
        Map<String, Value> nestedUpdate = new HashMap<>();
        Value.Builder nestedProto = Value.newBuilder();
        nestedProto.getMapValueBuilder().putAllFields(LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        nestedUpdate.put("first", nestedProto.build());
        nestedProto.getMapValueBuilder().putAllFields(LocalFirestoreHelper.ALL_SUPPORTED_TYPES_PROTO);
        nestedUpdate.put("second", nestedProto.build());
        List<String> updateMask = Arrays.asList("first.foo", "second.arrayValue", "second.bytesValue", "second.dateValue", "second.doubleValue", "second.falseValue", "second.foo", "second.geoPointValue", "second.infValue", "second.longValue", "second.nanValue", "second.negInfValue", "second.nullValue", "second.objectValue.foo", "second.timestampValue", "second.trueValue");
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.set(nestedUpdate, updateMask));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void updateDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.update(LocalFirestoreHelper.SINGLE_FIELD_MAP);
        documentReference.update("foo", "bar").get();
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(LocalFirestoreHelper.SINGLE_FIELD_PROTO, Collections.singletonList("foo")));
        for (CommitRequest request : commitCapture.getAllValues()) {
            LocalFirestoreHelper.assertCommitEquals(expectedCommit, request);
        }
    }

    @Test
    public void updateWithDotsInFieldName() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.update(LocalFirestoreHelper.map("a.b.c", ((Object) (LocalFirestoreHelper.map("d.e", "foo"))))).get();
        Map<String, Value> nestedUpdate = new HashMap<>();
        Value.Builder valueProto = Value.newBuilder();
        valueProto.getMapValueBuilder().putFields("d.e", Value.newBuilder().setStringValue("foo").build());
        Value.Builder cProto = Value.newBuilder();
        cProto.getMapValueBuilder().putFields("c", valueProto.build());
        Value.Builder bProto = Value.newBuilder();
        bProto.getMapValueBuilder().putFields("b", cProto.build());
        nestedUpdate.put("a", bProto.build());
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(nestedUpdate, Arrays.asList("a.b.c")));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void updateNestedMap() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.update("a.b", "foo", "a.c", FieldValue.delete()).get();
        Map<String, Value> nestedUpdate = new HashMap<>();
        Value.Builder valueProto = Value.newBuilder();
        valueProto.getMapValueBuilder().putFields("b", Value.newBuilder().setStringValue("foo").build());
        nestedUpdate.put("a", valueProto.build());
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(nestedUpdate, Arrays.asList("a.b", "a.c")));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void updateConflictingFields() throws Exception {
        try {
            documentReference.update("a.b", "foo", "a", "foo").get();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Detected ambiguous definition for field 'a'.");
        }
        try {
            documentReference.update("a.b", "foo", "a.b.c", "foo").get();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Detected ambiguous definition for field 'a.b'.");
        }
        try {
            documentReference.update("a.b", LocalFirestoreHelper.SINGLE_FIELD_MAP, "a", LocalFirestoreHelper.SINGLE_FIELD_MAP).get();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Detected ambiguous definition for field 'a'.");
        }
        try {
            documentReference.update("a.b", LocalFirestoreHelper.SINGLE_FIELD_MAP, "a.b.c", LocalFirestoreHelper.SINGLE_FIELD_MAP).get();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Detected ambiguous definition for field 'a.b'.");
        }
    }

    @Test
    public void deleteField() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.update("foo", "bar", "bar.foo", FieldValue.delete()).get();
        Value.Builder emptyMap = Value.newBuilder();
        emptyMap.getMapValueBuilder();
        Map<String, Value> fieldMap = new HashMap<>();
        fieldMap.put("foo", LocalFirestoreHelper.string("bar"));
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(fieldMap, Arrays.asList("foo", "bar.foo")));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void updateNestedDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        Map<String, Object> nestedObject = new HashMap<>();
        nestedObject.put("a", "b");
        nestedObject.put("c.d", "e");
        nestedObject.put("f.g.h", "i");
        Map<String, Value> expandedObject = new HashMap<>();
        expandedObject.put("a", LocalFirestoreHelper.string("b"));
        expandedObject.put("c", LocalFirestoreHelper.object("d", LocalFirestoreHelper.string("e")));
        expandedObject.put("f", LocalFirestoreHelper.object("g", LocalFirestoreHelper.object("h", LocalFirestoreHelper.string("i"))));
        documentReference.update(nestedObject).get();
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(expandedObject, new ArrayList(nestedObject.keySet())));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void updateDocumentWithTwoFields() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        documentReference.update("a", "b", "c", "d").get();
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(LocalFirestoreHelper.map("a", Value.newBuilder().setStringValue("b").build(), "c", Value.newBuilder().setStringValue("d").build()), Arrays.asList("a", "c")));
        LocalFirestoreHelper.assertCommitEquals(expectedCommit, commitCapture.getValue());
    }

    @Test
    public void updateDocumentWithPreconditions() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.SINGLE_WRITE_COMMIT_RESPONSE).when(firestoreMock).sendRequest(commitCapture.capture(), Matchers.<UnaryCallable<CommitRequest, CommitResponse>>any());
        Precondition options = Precondition.updatedAt(Timestamp.ofTimeSecondsAndNanos(479978400, 123000000));
        documentReference.update(LocalFirestoreHelper.SINGLE_FIELD_MAP, options).get();
        documentReference.update(options, "foo", "bar").get();
        com.google.firestore.v1.Precondition.Builder precondition = com.google.firestore.v1.Precondition.newBuilder();
        precondition.getUpdateTimeBuilder().setSeconds(479978400).setNanos(123000000);
        CommitRequest expectedCommit = LocalFirestoreHelper.commit(LocalFirestoreHelper.update(LocalFirestoreHelper.SINGLE_FIELD_PROTO, Collections.singletonList("foo"), precondition.build()));
        for (CommitRequest request : commitCapture.getAllValues()) {
            LocalFirestoreHelper.assertCommitEquals(expectedCommit, request);
        }
    }
}

