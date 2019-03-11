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
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.ServerStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.spi.v1.FirestoreRpc;
import com.google.firestore.v1.BatchGetDocumentsRequest;
import com.google.firestore.v1.DocumentMask;
import com.google.firestore.v1.Precondition.Builder;
import com.google.firestore.v1.Write;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
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
public class TransactionTest {
    @Spy
    private FirestoreRpc firestoreRpc = Mockito.mock(FirestoreRpc.class);

    @Spy
    private FirestoreImpl firestoreMock = new FirestoreImpl(FirestoreOptions.newBuilder().setProjectId("test-project").build(), firestoreRpc);

    @Captor
    private ArgumentCaptor<Message> requestCapture;

    @Captor
    private ArgumentCaptor<ApiStreamObserver<Message>> streamObserverCapture;

    private DocumentReference documentReference;

    private Query queryReference;

    private TransactionOptions options;

    @Test
    public void returnsValue() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(0, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                Assert.assertEquals("user_provided", Thread.currentThread().getName());
                return "foo";
            }
        }, options);
        Assert.assertEquals("foo", transaction.get());
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(2, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID), requests.get(1));
    }

    @Test
    public void canReturnNull() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo2"))).doReturn(LocalFirestoreHelper.commitResponse(0, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                return null;
            }
        }, options);
        Assert.assertEquals(null, transaction.get());
    }

    @Test
    public void rollbackOnCallbackError() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.rollbackResponse()).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) throws Exception {
                throw new Exception("Expected exception");
            }
        }, options);
        try {
            transaction.get();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().endsWith("Expected exception"));
        }
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(2, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.rollback(), requests.get(1));
    }

    @Test
    public void noRollbackOnBeginFailure() throws Exception {
        Mockito.doReturn(ApiFutures.immediateFailedFuture(new Exception("Expected exception"))).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                fail();
                return null;
            }
        }, options);
        try {
            transaction.get();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().endsWith("Expected exception"));
        }
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(1, requests.size());
    }

    @Test
    public void limitsRetriesWithFailure() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo1"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo2"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo3"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo4"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo5"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                return "foo";
            }
        }, options);
        try {
            transaction.get();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().endsWith("Transaction was cancelled because of too many retries."));
        }
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(10, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo1")), requests.get(1));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo1")), requests.get(2));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo2")), requests.get(3));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo2")), requests.get(4));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo3")), requests.get(5));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo3")), requests.get(6));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo4")), requests.get(7));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo4")), requests.get(8));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo5")), requests.get(9));
    }

    @Test
    public void limitsRetriesWithSuccess() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo1"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo2"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo3"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo4"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo5"))).doReturn(ApiFutures.immediateFailedFuture(new Exception())).doReturn(LocalFirestoreHelper.beginResponse(ByteString.copyFromUtf8("foo6"))).doReturn(LocalFirestoreHelper.commitResponse(0, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        final AtomicInteger retryCount = new AtomicInteger(1);
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                return "foo" + (retryCount.getAndIncrement());
            }
        }, TransactionOptions.create(options.getExecutor(), 6));
        Assert.assertEquals("foo6", transaction.get());
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(12, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo1")), requests.get(1));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo1")), requests.get(2));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo2")), requests.get(3));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo2")), requests.get(4));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo3")), requests.get(5));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo3")), requests.get(6));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo4")), requests.get(7));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo4")), requests.get(8));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo5")), requests.get(9));
        Assert.assertEquals(LocalFirestoreHelper.begin(ByteString.copyFromUtf8("foo5")), requests.get(10));
        Assert.assertEquals(LocalFirestoreHelper.commit(ByteString.copyFromUtf8("foo6")), requests.get(11));
    }

    @Test
    public void getDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(0, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.SINGLE_FIELD_PROTO)).when(firestoreMock).streamRequest(requestCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable<Message, Message>>any());
        ApiFuture<DocumentSnapshot> transaction = firestoreMock.runTransaction(new Transaction.Function<DocumentSnapshot>() {
            @Override
            public DocumentSnapshot updateCallback(Transaction transaction) throws InterruptedException, ExecutionException {
                return transaction.get(documentReference).get();
            }
        }, options);
        Assert.assertEquals("doc", transaction.get().getId());
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(3, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.get(LocalFirestoreHelper.TRANSACTION_ID), requests.get(1));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID), requests.get(2));
    }

    @Test
    public void getMultipleDocuments() throws Exception {
        final DocumentReference doc1 = firestoreMock.document("coll/doc1");
        final DocumentReference doc2 = firestoreMock.document("coll/doc2");
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(0, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.SINGLE_FIELD_PROTO)).when(firestoreMock).streamRequest(requestCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable<Message, Message>>any());
        ApiFuture<List<DocumentSnapshot>> transaction = firestoreMock.runTransaction(new Transaction.Function<List<DocumentSnapshot>>() {
            @Override
            public List<DocumentSnapshot> updateCallback(Transaction transaction) throws InterruptedException, ExecutionException {
                return transaction.getAll(doc1, doc2).get();
            }
        }, options);
        Assert.assertEquals(2, transaction.get().size());
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(3, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.getAll(LocalFirestoreHelper.TRANSACTION_ID, doc1.getResourcePath().toString(), doc2.getResourcePath().toString()), requests.get(1));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID), requests.get(2));
    }

    @Test
    public void getMultipleDocumentsWithFieldMask() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(0, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        Mockito.doAnswer(LocalFirestoreHelper.getAllResponse(LocalFirestoreHelper.SINGLE_FIELD_PROTO)).when(firestoreMock).streamRequest(requestCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable>any());
        final DocumentReference doc1 = firestoreMock.document("coll/doc1");
        final FieldMask fieldMask = FieldMask.of(FieldPath.of("foo", "bar"));
        ApiFuture<List<DocumentSnapshot>> transaction = firestoreMock.runTransaction(new Transaction.Function<List<DocumentSnapshot>>() {
            @Override
            public List<DocumentSnapshot> updateCallback(Transaction transaction) throws InterruptedException, ExecutionException {
                return transaction.getAll(new DocumentReference[]{ doc1 }, fieldMask).get();
            }
        }, options);
        transaction.get();
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(3, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        BatchGetDocumentsRequest expectedGetAll = LocalFirestoreHelper.getAll(LocalFirestoreHelper.TRANSACTION_ID, doc1.getResourcePath().toString());
        expectedGetAll = expectedGetAll.toBuilder().setMask(DocumentMask.newBuilder().addFieldPaths("foo.bar")).build();
        Assert.assertEquals(expectedGetAll, requests.get(1));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID), requests.get(2));
    }

    @Test
    public void getQuery() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(0, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        Mockito.doAnswer(LocalFirestoreHelper.queryResponse()).when(firestoreMock).streamRequest(requestCapture.capture(), streamObserverCapture.capture(), Matchers.<ServerStreamingCallable<Message, Message>>any());
        ApiFuture<QuerySnapshot> transaction = firestoreMock.runTransaction(new Transaction.Function<QuerySnapshot>() {
            @Override
            public QuerySnapshot updateCallback(Transaction transaction) throws InterruptedException, ExecutionException {
                return transaction.get(queryReference).get();
            }
        }, options);
        Assert.assertEquals(1, transaction.get().size());
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(3, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.query(LocalFirestoreHelper.TRANSACTION_ID), requests.get(1));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID), requests.get(2));
    }

    @Test
    public void updateDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(2, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                transaction.update(documentReference, LocalFirestoreHelper.SINGLE_FIELD_MAP);
                transaction.update(documentReference, "foo", "bar");
                return "foo";
            }
        }, options);
        Assert.assertEquals("foo", transaction.get());
        List<Write> writes = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            writes.add(LocalFirestoreHelper.update(LocalFirestoreHelper.SINGLE_FIELD_PROTO, Collections.singletonList("foo")));
        }
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(2, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID, writes.toArray(new Write[]{  })), requests.get(1));
    }

    @Test
    public void setDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(2, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                transaction.set(documentReference, LocalFirestoreHelper.SINGLE_FIELD_MAP).set(documentReference, LocalFirestoreHelper.SINGLE_FIELD_OBJECT);
                return "foo";
            }
        }, options);
        Assert.assertEquals("foo", transaction.get());
        List<Write> writes = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            writes.add(LocalFirestoreHelper.set(LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        }
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(2, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID, writes.toArray(new Write[]{  })), requests.get(1));
    }

    @Test
    public void createDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(2, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                transaction.create(documentReference, LocalFirestoreHelper.SINGLE_FIELD_MAP).create(documentReference, LocalFirestoreHelper.SINGLE_FIELD_OBJECT);
                return "foo";
            }
        }, options);
        Assert.assertEquals("foo", transaction.get());
        List<Write> writes = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            writes.add(LocalFirestoreHelper.create(LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        }
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(2, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID, writes.toArray(new Write[]{  })), requests.get(1));
    }

    @Test
    public void deleteDocument() throws Exception {
        Mockito.doReturn(LocalFirestoreHelper.beginResponse()).doReturn(LocalFirestoreHelper.commitResponse(2, 0)).when(firestoreMock).sendRequest(requestCapture.capture(), Matchers.<UnaryCallable<Message, Message>>any());
        ApiFuture<String> transaction = firestoreMock.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) {
                transaction.delete(documentReference);
                transaction.delete(documentReference, Precondition.updatedAt(Timestamp.ofTimeSecondsAndNanos(1, 2)));
                return "foo";
            }
        }, options);
        Assert.assertEquals("foo", transaction.get());
        List<Write> writes = new ArrayList<>();
        writes.add(LocalFirestoreHelper.delete());
        Builder precondition = com.google.firestore.v1.Precondition.newBuilder();
        precondition.getUpdateTimeBuilder().setSeconds(1).setNanos(2);
        writes.add(LocalFirestoreHelper.delete(precondition.build()));
        List<Message> requests = requestCapture.getAllValues();
        Assert.assertEquals(2, requests.size());
        Assert.assertEquals(LocalFirestoreHelper.begin(), requests.get(0));
        Assert.assertEquals(LocalFirestoreHelper.commit(LocalFirestoreHelper.TRANSACTION_ID, writes.toArray(new Write[]{  })), requests.get(1));
    }
}

