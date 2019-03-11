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


import Code.ABORTED;
import Code.ALREADY_EXISTS;
import Code.CANCELLED;
import Code.DATA_LOSS;
import Code.DEADLINE_EXCEEDED;
import Code.FAILED_PRECONDITION;
import Code.INTERNAL;
import Code.INVALID_ARGUMENT;
import Code.NOT_FOUND;
import Code.OUT_OF_RANGE;
import Code.PERMISSION_DENIED;
import Code.RESOURCE_EXHAUSTED;
import Code.UNAUTHENTICATED;
import Code.UNAVAILABLE;
import Code.UNIMPLEMENTED;
import Code.UNKNOWN;
import Direction.DESCENDING;
import ListenResponse.Builder;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.cloud.firestore.spi.v1.FirestoreRpc;
import com.google.firestore.v1.ListenRequest;
import com.google.firestore.v1.ListenResponse;
import com.google.protobuf.ByteString;
import io.grpc.Status.Code;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WatchTest {
    /**
     * The Target ID used by the Java Firestore SDK.
     */
    private static final int TARGET_ID = 1;

    /**
     * The resume token used by this test harness.
     */
    private static final ByteString RESUME_TOKEN = ByteString.copyFromUtf8("token");

    /**
     * A counter of all document sent. Used to generate a unique update time.
     */
    private static int documentCount;

    @Spy
    private FirestoreRpc firestoreRpc = Mockito.mock(FirestoreRpc.class);

    @Spy
    private FirestoreImpl firestoreMock = new FirestoreImpl(FirestoreOptions.newBuilder().setProjectId("test-project").build(), firestoreRpc);

    @Captor
    private ArgumentCaptor<ApiStreamObserver<ListenResponse>> streamObserverCapture;

    /**
     * Executor that executes delayed tasks without delay.
     */
    private final ScheduledExecutorService immediateExecutor = new ScheduledThreadPoolExecutor(1) {
        @Override
        @Nonnull
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return super.schedule(command, 0, TimeUnit.MILLISECONDS);
        }
    };

    private BlockingQueue<ListenRequest> requests = new LinkedBlockingDeque<>();

    private BlockingQueue<FirestoreException> exceptions = new LinkedBlockingDeque<>();

    private BlockingQueue<DocumentSnapshot> documentSnapshots = new LinkedBlockingDeque<>();

    private BlockingQueue<QuerySnapshot> querySnapshots = new LinkedBlockingDeque<>();

    private Semaphore closes = new Semaphore(0);

    private QuerySnapshot lastSnapshot = null;

    private ListenerRegistration listenerRegistration;

    /**
     * Holder class for the expected state of a document in a snapshot.
     */
    static class SnapshotDocument {
        enum ChangeType {

            UNCHANGED,
            ADDED,
            REMOVED,
            MODIFIED;}

        WatchTest.SnapshotDocument.ChangeType type;

        String name;

        Map<String, Object> data;

        SnapshotDocument(WatchTest.SnapshotDocument.ChangeType type, String name, Map<String, Object> data) {
            this.type = type;
            this.name = name;
            this.data = data;
        }
    }

    @Test
    public void documentWatchChange() throws InterruptedException {
        addDocumentListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(snapshot());
        awaitDocumentSnapshot();
        send(doc("coll/doc", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitDocumentSnapshot("coll/doc", LocalFirestoreHelper.SINGLE_FIELD_MAP);
        send(doc("coll/doc", LocalFirestoreHelper.UPDATED_FIELD_PROTO));
        send(snapshot());
        awaitDocumentSnapshot("coll/doc", LocalFirestoreHelper.UPDATED_FIELD_MAP);
        send(docDelete("coll/doc"));
        send(snapshot());
        awaitDocumentSnapshot();
    }

    @Test
    public void documentWatchIgnoresNonMatchingDocument() throws InterruptedException {
        addDocumentListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/nondoc", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitDocumentSnapshot();
    }

    @Test
    public void documentWatchCombinesEventsForDocument() throws InterruptedException {
        addDocumentListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(doc("coll/doc", LocalFirestoreHelper.UPDATED_FIELD_PROTO));
        send(snapshot());
        awaitDocumentSnapshot("coll/doc", LocalFirestoreHelper.UPDATED_FIELD_MAP);
    }

    @Test
    public void queryWatchRemoveTarget() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(removeTarget(null));
        awaitException(null);
    }

    @Test
    public void queryWatchRemoveTargetWithStatus() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(removeTarget(ABORTED));
        awaitException(ABORTED);
    }

    @Test
    public void queryWatchReopensOnUnexceptedStreamEnd() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(snapshot());
        awaitQuerySnapshot();
        close();
        awaitClose();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchDoesntSendRaiseSnapshotOnReset() throws InterruptedException {
        // This test is meant to reproduce https://github.com/googleapis/google-cloud-dotnet/issues/2542
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(snapshot());
        awaitQuerySnapshot();
        close();
        awaitClose();
        awaitAddTarget();
        send(addTarget());
        send(current());
        // This should not raise a snapshot, since nothing has changed since the last snapshot.
        send(snapshot());
        send(doc("coll/doc", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        // Verify that we only receveived one snapshot.
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchDoesntReopenInactiveStream() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(snapshot());
        awaitQuerySnapshot();
        close();
        listenerRegistration.remove();
    }

    @Test
    public void queryWatchRetriesBasedOnErrorCode() throws InterruptedException {
        Map<Code, Boolean> expectRetry = new HashMap<>();
        expectRetry.put(CANCELLED, true);
        expectRetry.put(UNKNOWN, true);
        expectRetry.put(INVALID_ARGUMENT, false);
        expectRetry.put(DEADLINE_EXCEEDED, true);
        expectRetry.put(NOT_FOUND, false);
        expectRetry.put(ALREADY_EXISTS, false);
        expectRetry.put(PERMISSION_DENIED, false);
        expectRetry.put(RESOURCE_EXHAUSTED, true);
        expectRetry.put(FAILED_PRECONDITION, false);
        expectRetry.put(ABORTED, false);
        expectRetry.put(OUT_OF_RANGE, false);
        expectRetry.put(UNIMPLEMENTED, false);
        expectRetry.put(INTERNAL, true);
        expectRetry.put(UNAVAILABLE, true);
        expectRetry.put(DATA_LOSS, false);
        expectRetry.put(UNAUTHENTICATED, true);
        for (Map.Entry<Code, Boolean> entry : expectRetry.entrySet()) {
            addQueryListener();
            awaitAddTarget();
            send(addTarget());
            send(current());
            destroy(entry.getKey());
            if (entry.getValue()) {
                awaitAddTarget();
                send(addTarget());
                send(current());
                send(snapshot());
                awaitQuerySnapshot();
                listenerRegistration.remove();
            } else {
                awaitException(null);
            }
        }
    }

    @Test
    public void queryWatchHandlesDocumentChange() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(doc("coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_PROTO));
        send(doc("coll/doc3", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.MODIFIED, "coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc3", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(docDelete("coll/doc1"));
        send(docRemove("coll/doc3"));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.REMOVED, "coll/doc1", null), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.REMOVED, "coll/doc3", null));
    }

    @Test
    public void queryWatchReconnectsWithResumeToken() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        // This update gets ignored.
        send(doc("coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_PROTO));
        close();
        awaitResumeToken();
        send(addTarget());
        send(current());
        send(doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchSortsDocuments() throws InterruptedException {
        listenerRegistration = firestoreMock.collection("coll").orderBy("foo").orderBy("bar", DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable
            QuerySnapshot value, @Nullable
            FirestoreException error) {
                querySnapshots.add(value);
            }
        });
        ListenResponse[] documents = new ListenResponse[]{ doc("coll/doc1", LocalFirestoreHelper.map("foo", LocalFirestoreHelper.string("a"), "bar", LocalFirestoreHelper.string("b"))), doc("coll/doc2", LocalFirestoreHelper.map("foo", LocalFirestoreHelper.string("a"), "bar", LocalFirestoreHelper.string("a"))), doc("coll/doc3", LocalFirestoreHelper.map("foo", LocalFirestoreHelper.string("b"), "bar", LocalFirestoreHelper.string("b"))), doc("coll/doc5", LocalFirestoreHelper.map("foo", LocalFirestoreHelper.string("b"), "bar", LocalFirestoreHelper.string("a"))), // doc4 sorts after doc5 because __name__ uses the last specified sort direction.
        doc("coll/doc4", LocalFirestoreHelper.map("foo", LocalFirestoreHelper.string("b"), "bar", LocalFirestoreHelper.string("a"))) };
        awaitAddTarget();
        send(addTarget());
        send(current());
        // Send document in any order
        send(documents[4]);
        send(documents[3]);
        send(documents[2]);
        send(documents[0]);
        send(documents[1]);
        send(snapshot());
        QuerySnapshot querySnapshot = querySnapshots.take();
        verifyOrder(Arrays.asList("coll/doc1", "coll/doc2", "coll/doc3", "coll/doc5", "coll/doc4"), querySnapshot.getDocuments());
    }

    @Test
    public void queryWatchCombinesChangesForSameDocument() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(doc("coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_MAP));
        send(doc("coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_PROTO));
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.MODIFIED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchHandlesDeletingNonExistingDocument() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(docDelete("coll/doc2"));
        send(doc("coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.MODIFIED, "coll/doc1", LocalFirestoreHelper.UPDATED_FIELD_MAP));
    }

    @Test
    public void queryWatchHandlesDeletesAndAddInSingleSnapshot() throws InterruptedException {
        ListenResponse document = doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(document);
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(docDelete("coll/doc1"));
        send(doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(document);
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchHandlesAddAndDeleteInSingleSnapshot() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(snapshot());
        awaitQuerySnapshot();
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(docDelete("coll/doc1"));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchHandlesReset() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(reset());
        send(current());
        send(doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.REMOVED, "coll/doc1", null), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchHandlesFilterMatch() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(filter(1));
        send(doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchHandlesFilterMismatch() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(filter(2));
        awaitClose();
        awaitAddTarget();
        send(doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(current());
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.REMOVED, "coll/doc1", null), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
    }

    @Test
    public void queryWatchHandlesTargetRemoval() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO));
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        ListenResponse.Builder request = doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO).toBuilder();
        request.getDocumentChangeBuilder().clearTargetIds();
        request.getDocumentChangeBuilder().addRemovedTargetIds(WatchTest.TARGET_ID);
        send(request.build());
        send(snapshot());
        awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.REMOVED, "coll/doc1", null));
    }

    @Test
    public void queryWatchHandlesIgnoresDifferentTarget() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        ListenResponse.Builder request = doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO).toBuilder();
        request.getDocumentChangeBuilder().clearTargetIds();
        request.getDocumentChangeBuilder().addTargetIds(2);
        send(request.build());
        send(snapshot());
        awaitQuerySnapshot();
    }

    @Test
    public void emptySnapshotEquals() throws InterruptedException {
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(snapshot());
        QuerySnapshot firstSnapshot = awaitQuerySnapshot();
        restartWatch();
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(current());
        send(snapshot());
        QuerySnapshot secondSnapshot = awaitQuerySnapshot();
        Assert.assertEquals(firstSnapshot, firstSnapshot);
        Assert.assertEquals(firstSnapshot, secondSnapshot);
    }

    @Test
    public void snapshotWithChangesEquals() throws InterruptedException {
        ListenResponse doc1 = doc("coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        ListenResponse doc2 = doc("coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        ListenResponse doc3 = doc("coll/doc3", LocalFirestoreHelper.SINGLE_FIELD_PROTO);
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(doc1);
        send(current());
        send(snapshot());
        QuerySnapshot firstSnapshot = awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        send(doc2);
        send(doc3);
        send(snapshot());
        QuerySnapshot secondSnapshot = awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc3", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        Assert.assertNotEquals(secondSnapshot, firstSnapshot);
        send(docDelete("coll/doc3"));
        send(snapshot());
        QuerySnapshot thirdSnapshot = awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.REMOVED, "coll/doc3", null));
        Assert.assertNotEquals(thirdSnapshot, firstSnapshot);
        Assert.assertNotEquals(thirdSnapshot, secondSnapshot);
        restartWatch();
        addQueryListener();
        awaitAddTarget();
        send(addTarget());
        send(doc2);
        send(current());
        send(snapshot());
        QuerySnapshot currentSnapshot = awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        Assert.assertNotEquals(currentSnapshot, firstSnapshot);
        send(doc3);
        send(doc1);
        send(snapshot());
        currentSnapshot = awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.ADDED, "coll/doc3", LocalFirestoreHelper.SINGLE_FIELD_MAP));
        Assert.assertNotEquals(currentSnapshot, secondSnapshot);
        send(docDelete("coll/doc3"));
        send(snapshot());
        currentSnapshot = awaitQuerySnapshot(new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc1", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.UNCHANGED, "coll/doc2", LocalFirestoreHelper.SINGLE_FIELD_MAP), new WatchTest.SnapshotDocument(WatchTest.SnapshotDocument.ChangeType.REMOVED, "coll/doc3", null));
        Assert.assertEquals(currentSnapshot, thirdSnapshot);
    }
}

