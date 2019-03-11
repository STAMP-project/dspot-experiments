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


import DocumentChange.Type.ADDED;
import Query.Direction.DESCENDING;
import StructuredQuery.Direction.ASCENDING;
import StructuredQuery.FieldFilter.Operator.ARRAY_CONTAINS;
import StructuredQuery.FieldFilter.Operator.EQUAL;
import StructuredQuery.FieldFilter.Operator.GREATER_THAN;
import StructuredQuery.FieldFilter.Operator.GREATER_THAN_OR_EQUAL;
import StructuredQuery.FieldFilter.Operator.LESS_THAN;
import StructuredQuery.FieldFilter.Operator.LESS_THAN_OR_EQUAL;
import StructuredQuery.UnaryFilter.Operator.IS_NAN;
import StructuredQuery.UnaryFilter.Operator.IS_NULL;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.spi.v1.FirestoreRpc;
import com.google.firestore.v1.RunQueryRequest;
import com.google.firestore.v1.Value;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
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
public class QueryTest {
    @Spy
    private FirestoreImpl firestoreMock = new FirestoreImpl(FirestoreOptions.newBuilder().setProjectId("test-project").build(), Mockito.mock(FirestoreRpc.class));

    @Captor
    private ArgumentCaptor<RunQueryRequest> runQuery;

    @Captor
    private ArgumentCaptor<ApiStreamObserver> streamObserverCapture;

    private Query query;

    @Test
    public void withLimit() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.limit(42).get().get();
        Assert.assertEquals(LocalFirestoreHelper.query(LocalFirestoreHelper.limit(42)), runQuery.getValue());
    }

    @Test
    public void withOffset() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.offset(42).get().get();
        Assert.assertEquals(LocalFirestoreHelper.query(LocalFirestoreHelper.offset(42)), runQuery.getValue());
    }

    @Test
    public void withFilter() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.whereEqualTo("foo", "bar").get().get();
        query.whereEqualTo("foo", null).get().get();
        query.whereEqualTo("foo", Double.NaN).get().get();
        query.whereEqualTo("foo", Float.NaN).get().get();
        query.whereGreaterThan("foo", "bar").get().get();
        query.whereGreaterThanOrEqualTo("foo", "bar").get().get();
        query.whereLessThan("foo", "bar").get().get();
        query.whereLessThanOrEqualTo("foo", "bar").get().get();
        query.whereArrayContains("foo", "bar").get().get();
        Iterator<RunQueryRequest> expected = Arrays.asList(LocalFirestoreHelper.query(LocalFirestoreHelper.filter(EQUAL)), LocalFirestoreHelper.query(LocalFirestoreHelper.unaryFilter(IS_NULL)), LocalFirestoreHelper.query(LocalFirestoreHelper.unaryFilter(IS_NAN)), LocalFirestoreHelper.query(LocalFirestoreHelper.unaryFilter(IS_NAN)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(GREATER_THAN)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(GREATER_THAN_OR_EQUAL)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(LESS_THAN)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(LESS_THAN_OR_EQUAL)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(ARRAY_CONTAINS))).iterator();
        for (RunQueryRequest actual : runQuery.getAllValues()) {
            Assert.assertEquals(expected.next(), actual);
        }
    }

    @Test
    public void withFieldPathFilter() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.whereEqualTo(FieldPath.of("foo"), "bar").get().get();
        query.whereGreaterThan(FieldPath.of("foo"), "bar").get().get();
        query.whereGreaterThanOrEqualTo(FieldPath.of("foo"), "bar").get().get();
        query.whereLessThan(FieldPath.of("foo"), "bar").get().get();
        query.whereLessThanOrEqualTo(FieldPath.of("foo"), "bar").get().get();
        query.whereArrayContains(FieldPath.of("foo"), "bar").get().get();
        Iterator<RunQueryRequest> expected = Arrays.asList(LocalFirestoreHelper.query(LocalFirestoreHelper.filter(EQUAL)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(GREATER_THAN)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(GREATER_THAN_OR_EQUAL)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(LESS_THAN)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(LESS_THAN_OR_EQUAL)), LocalFirestoreHelper.query(LocalFirestoreHelper.filter(ARRAY_CONTAINS))).iterator();
        for (RunQueryRequest actual : runQuery.getAllValues()) {
            Assert.assertEquals(expected.next(), actual);
        }
    }

    @Test
    public void withDocumentIdFilter() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.whereEqualTo(FieldPath.documentId(), "doc").get().get();
        RunQueryRequest expectedRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.filter(Operator.EQUAL, "__name__", Value.newBuilder().setReferenceValue(LocalFirestoreHelper.DOCUMENT_NAME).build()));
        Assert.assertEquals(expectedRequest, runQuery.getValue());
    }

    @Test
    public void withOrderBy() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy("foo").orderBy("foo.bar", DESCENDING).get().get();
        Assert.assertEquals(LocalFirestoreHelper.query(LocalFirestoreHelper.order("foo", ASCENDING), LocalFirestoreHelper.order("foo.bar", StructuredQuery.Direction.DESCENDING)), runQuery.getValue());
    }

    @Test
    public void withFieldPathOrderBy() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy(FieldPath.of("foo")).orderBy(FieldPath.of("foo", "bar"), DESCENDING).get().get();
        Assert.assertEquals(LocalFirestoreHelper.query(LocalFirestoreHelper.order("foo", ASCENDING), LocalFirestoreHelper.order("foo.bar", StructuredQuery.Direction.DESCENDING)), runQuery.getValue());
    }

    @Test
    public void withSelect() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.select(new String[]{  }).get().get();
        query.select("foo", "foo.bar").get().get();
        Iterator<RunQueryRequest> expectedQuery = Arrays.asList(LocalFirestoreHelper.query(LocalFirestoreHelper.select(FieldPath.documentId())), LocalFirestoreHelper.query(LocalFirestoreHelper.select("foo"), LocalFirestoreHelper.select("foo.bar"))).iterator();
        for (RunQueryRequest actual : runQuery.getAllValues()) {
            Assert.assertEquals(expectedQuery.next(), actual);
        }
    }

    @Test
    public void withFieldPathSelect() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.select(new FieldPath[]{  }).get().get();
        query.select(FieldPath.of("foo"), FieldPath.of("foo", "bar")).get().get();
        Iterator<RunQueryRequest> expectedQuery = Arrays.asList(LocalFirestoreHelper.query(LocalFirestoreHelper.select(FieldPath.documentId())), LocalFirestoreHelper.query(LocalFirestoreHelper.select("foo"), LocalFirestoreHelper.select("foo.bar"))).iterator();
        for (RunQueryRequest actual : runQuery.getAllValues()) {
            Assert.assertEquals(expectedQuery.next(), actual);
        }
    }

    @Test
    public void withDocumentSnapshotCursor() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.startAt(LocalFirestoreHelper.SINGLE_FIELD_SNAPSHOT).get();
        Value documentBoundary = Value.newBuilder().setReferenceValue(((query.getResourcePath().toString()) + "/doc")).build();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.order("__name__", ASCENDING), LocalFirestoreHelper.startAt(documentBoundary, true));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withDocumentIdAndDocumentSnapshotCursor() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy(FieldPath.documentId()).startAt(LocalFirestoreHelper.SINGLE_FIELD_SNAPSHOT).get();
        Value documentBoundary = Value.newBuilder().setReferenceValue(((query.getResourcePath().toString()) + "/doc")).build();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.order("__name__", ASCENDING), LocalFirestoreHelper.startAt(documentBoundary, true));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withExtractedDirectionForDocumentSnapshotCursor() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy("foo", DESCENDING).startAt(LocalFirestoreHelper.SINGLE_FIELD_SNAPSHOT).get();
        Value documentBoundary = Value.newBuilder().setReferenceValue(((query.getResourcePath().toString()) + "/doc")).build();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.order("foo", Direction.DESCENDING), LocalFirestoreHelper.order("__name__", StructuredQuery.Direction.DESCENDING), LocalFirestoreHelper.startAt(true), LocalFirestoreHelper.startAt(documentBoundary, true));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withInequalityFilterForDocumentSnapshotCursor() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.whereEqualTo("a", "b").whereGreaterThanOrEqualTo("foo", "bar").whereEqualTo("c", "d").startAt(LocalFirestoreHelper.SINGLE_FIELD_SNAPSHOT).get();
        Value documentBoundary = Value.newBuilder().setReferenceValue(((query.getResourcePath().toString()) + "/doc")).build();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.filter(Operator.EQUAL, "a", "b"), LocalFirestoreHelper.filter(Operator.GREATER_THAN_OR_EQUAL), LocalFirestoreHelper.filter(Operator.EQUAL, "c", "d"), LocalFirestoreHelper.order("foo", Direction.ASCENDING), LocalFirestoreHelper.order("__name__", ASCENDING), LocalFirestoreHelper.startAt(true), LocalFirestoreHelper.startAt(documentBoundary, true));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withEqualityFilterForDocumentSnapshotCursor() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.whereEqualTo("foo", "bar").startAt(LocalFirestoreHelper.SINGLE_FIELD_SNAPSHOT).get();
        Value documentBoundary = Value.newBuilder().setReferenceValue(((query.getResourcePath().toString()) + "/doc")).build();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.filter(Operator.EQUAL), LocalFirestoreHelper.order("__name__", ASCENDING), LocalFirestoreHelper.startAt(documentBoundary, true));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withStartAt() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy("foo").orderBy(FieldPath.documentId()).startAt("bar", "foo").get().get();
        Value documentBoundary = Value.newBuilder().setReferenceValue(((query.getResourcePath().toString()) + "/foo")).build();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.order("foo", ASCENDING), LocalFirestoreHelper.order("__name__", ASCENDING), LocalFirestoreHelper.startAt(true), LocalFirestoreHelper.startAt(documentBoundary, true));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withInvalidStartAt() throws Exception {
        try {
            query.orderBy(FieldPath.documentId()).startAt(42).get();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(("The corresponding value for FieldPath.documentId() must be a String or a " + "DocumentReference."), e.getMessage());
        }
        try {
            query.orderBy(FieldPath.documentId()).startAt("coll/doc/coll").get();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Only a direct child can be used as a query boundary. Found: 'coll/coll/doc/coll'", e.getMessage());
        }
        try {
            query.orderBy(FieldPath.documentId()).startAt(firestoreMock.document("foo/bar")).get();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("'foo/bar' is not part of the query result set and cannot be used as a query boundary.", e.getMessage());
        }
    }

    @Test
    public void withStartAfter() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy("foo").startAfter("bar").get().get();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.order("foo", ASCENDING), LocalFirestoreHelper.startAt(false));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withEndBefore() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy("foo").endBefore("bar").get().get();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.order("foo", ASCENDING), LocalFirestoreHelper.endAt(true));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test
    public void withEndAt() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        query.orderBy("foo").endAt("bar").get().get();
        RunQueryRequest queryRequest = LocalFirestoreHelper.query(LocalFirestoreHelper.order("foo", ASCENDING), LocalFirestoreHelper.endAt(false));
        Assert.assertEquals(queryRequest, runQuery.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void overspecifiedCursor() throws Exception {
        query.orderBy("foo").startAt("foo", "bar", "bar", "foo");
    }

    @Test(expected = IllegalStateException.class)
    public void orderByWithCursor() throws Exception {
        query.startAt("foo").orderBy("foo");
    }

    @Test
    public void getResult() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse(((LocalFirestoreHelper.DOCUMENT_NAME) + "1"), ((LocalFirestoreHelper.DOCUMENT_NAME) + "2"))).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        QuerySnapshot result = query.get().get();
        Assert.assertEquals(query, result.getQuery());
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(2, result.getDocuments().size());
        Iterator<QueryDocumentSnapshot> docIterator = result.iterator();
        Assert.assertEquals("doc1", docIterator.next().getId());
        Assert.assertEquals("doc2", docIterator.next().getId());
        Assert.assertFalse(docIterator.hasNext());
        Iterator<DocumentChange> changeIterator = result.getDocumentChanges().iterator();
        DocumentChange documentChange = changeIterator.next();
        Assert.assertEquals("doc1", documentChange.getDocument().getId());
        Assert.assertEquals(ADDED, documentChange.getType());
        Assert.assertEquals((-1), documentChange.getOldIndex());
        Assert.assertEquals(0, documentChange.getNewIndex());
        documentChange = changeIterator.next();
        Assert.assertEquals("doc2", documentChange.getDocument().getId());
        Assert.assertEquals(ADDED, documentChange.getType());
        Assert.assertEquals((-1), documentChange.getOldIndex());
        Assert.assertEquals(1, documentChange.getNewIndex());
        Assert.assertFalse(changeIterator.hasNext());
        Assert.assertEquals(Timestamp.ofTimeSecondsAndNanos(1, 2), result.getReadTime());
        Assert.assertEquals(Arrays.asList(LocalFirestoreHelper.SINGLE_FIELD_OBJECT, LocalFirestoreHelper.SINGLE_FIELD_OBJECT), result.toObjects(LocalFirestoreHelper.SINGLE_FIELD_OBJECT.getClass()));
        Assert.assertEquals(2, result.getDocumentChanges().size());
    }

    @Test
    public void streamResult() throws Exception {
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse(((LocalFirestoreHelper.DOCUMENT_NAME) + "1"), ((LocalFirestoreHelper.DOCUMENT_NAME) + "2"))).when(firestoreMock).streamRequest(runQuery.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        final Semaphore semaphore = new Semaphore(0);
        final Iterator<String> iterator = Arrays.asList("doc1", "doc2").iterator();
        query.stream(new ApiStreamObserver<DocumentSnapshot>() {
            @Override
            public void onNext(DocumentSnapshot documentSnapshot) {
                Assert.assertEquals(iterator.next(), documentSnapshot.getId());
            }

            @Override
            public void onError(Throwable throwable) {
                Assert.fail();
            }

            @Override
            public void onCompleted() {
                semaphore.release();
            }
        });
        semaphore.acquire();
    }

    @Test
    public void equalsTest() throws Exception {
        Assert.assertEquals(query.limit(42).offset(1337), query.offset(1337).limit(42));
        Assert.assertEquals(query.limit(42).offset(1337).hashCode(), query.offset(1337).limit(42).hashCode());
    }
}

