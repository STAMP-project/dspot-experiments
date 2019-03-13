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
package com.google.cloud.firestore.it;


import Query.Direction.DESCENDING;
import Type.ADDED;
import Type.MODIFIED;
import Type.REMOVED;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldMask;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.LocalFirestoreHelper;
import com.google.cloud.firestore.Precondition;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class ITSystemTest {
    private static final double DOUBLE_EPSILON = 1.0E-6;

    private final Map<String, Object> SINGLE_FIELD_MAP = LocalFirestoreHelper.SINGLE_FIELD_MAP;

    private final Map<String, Object> ALL_SUPPORTED_TYPES_MAP = LocalFirestoreHelper.ALL_SUPPORTED_TYPES_MAP;

    private final LocalFirestoreHelper.SingleField SINGLE_FIELD_OBJECT = LocalFirestoreHelper.SINGLE_FIELD_OBJECT;

    private final LocalFirestoreHelper.AllSupportedTypes ALL_SUPPORTED_TYPES_OBJECT = LocalFirestoreHelper.ALL_SUPPORTED_TYPES_OBJECT;

    @Rule
    public TestName testName = new TestName();

    private Firestore firestore;

    private CollectionReference randomColl;

    private DocumentReference randomDoc;

    @Test
    public void getAll() throws Exception {
        DocumentReference ref1 = randomColl.document("doc1");
        DocumentReference ref2 = randomColl.document("doc2");
        ApiFutures.allAsList(ImmutableList.of(ref1.set(SINGLE_FIELD_MAP), ref2.set(SINGLE_FIELD_MAP))).get();
        List<DocumentSnapshot> documentSnapshots = firestore.getAll(ref1, ref2).get();
        Assert.assertEquals(2, documentSnapshots.size());
        Assert.assertEquals("doc1", documentSnapshots.get(0).getId());
        Assert.assertEquals(SINGLE_FIELD_OBJECT, documentSnapshots.get(0).toObject(LocalFirestoreHelper.SingleField.class));
        Assert.assertEquals("doc2", documentSnapshots.get(1).getId());
        Assert.assertEquals(SINGLE_FIELD_OBJECT, documentSnapshots.get(1).toObject(LocalFirestoreHelper.SingleField.class));
    }

    @Test
    public void getAllWithFieldMask() throws Exception {
        DocumentReference ref = randomColl.document("doc1");
        ref.set(ALL_SUPPORTED_TYPES_MAP).get();
        List<DocumentSnapshot> documentSnapshots = firestore.getAll(new DocumentReference[]{ ref }, FieldMask.of("foo")).get();
        Assert.assertEquals(LocalFirestoreHelper.map("foo", "bar"), documentSnapshots.get(0).getData());
    }

    @Test
    public void addDocument() throws Exception {
        DocumentReference documentReference = randomColl.add(SINGLE_FIELD_MAP).get();
        Assert.assertEquals(20, documentReference.getId().length());
        DocumentSnapshot documentSnapshot = documentReference.get().get();
        Assert.assertEquals(SINGLE_FIELD_OBJECT, documentSnapshot.toObject(LocalFirestoreHelper.SingleField.class));
    }

    @Test
    public void createDocument() throws Exception {
        Assert.assertEquals(20, randomDoc.getId().length());
        randomDoc.create(SINGLE_FIELD_MAP).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertEquals(SINGLE_FIELD_OBJECT, documentSnapshot.toObject(LocalFirestoreHelper.SingleField.class));
    }

    @Test
    public void setDocument() throws Exception {
        Map<String, Object> nanNullMap = new HashMap<>();
        nanNullMap.put("nan", Double.NaN);
        nanNullMap.put("null", null);
        randomDoc.set(nanNullMap).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertEquals(((Double) (Double.NaN)), documentSnapshot.getDouble("nan"));
        Assert.assertEquals(null, documentSnapshot.get("null"));
    }

    @Test
    public void setDocumentWithMerge() throws Exception {
        Map<String, Object> originalMap = LocalFirestoreHelper.<String, Object>map("a.b", "c", "nested", LocalFirestoreHelper.map("d", "e"));
        Map<String, Object> updateMap = LocalFirestoreHelper.<String, Object>map("f.g", "h", "nested", LocalFirestoreHelper.map("i", "j"));
        Map<String, Object> resultMap = LocalFirestoreHelper.<String, Object>map("a.b", "c", "f.g", "h", "nested", LocalFirestoreHelper.map("d", "e", "i", "j"));
        randomDoc.set(originalMap).get();
        randomDoc.set(updateMap, SetOptions.merge()).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertEquals(resultMap, documentSnapshot.getData());
    }

    @Test
    public void mergeDocumentWithServerTimestamp() throws Exception {
        Map<String, Object> originalMap = LocalFirestoreHelper.<String, Object>map("a", "b");
        Map<String, Object> updateMap = LocalFirestoreHelper.map("c", ((Object) (FieldValue.serverTimestamp())));
        randomDoc.set(originalMap).get();
        randomDoc.set(updateMap, SetOptions.merge()).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertEquals("b", documentSnapshot.getString("a"));
        Assert.assertNotNull(documentSnapshot.getDate("c"));
    }

    @Test
    public void updateDocument() throws Exception {
        LocalFirestoreHelper.AllSupportedTypes expectedResult = new LocalFirestoreHelper.AllSupportedTypes();
        WriteResult writeResult = randomDoc.set(ALL_SUPPORTED_TYPES_MAP).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertEquals(expectedResult, documentSnapshot.toObject(LocalFirestoreHelper.AllSupportedTypes.class));
        randomDoc.update(Precondition.updatedAt(writeResult.getUpdateTime()), "foo", "updated").get();
        documentSnapshot = randomDoc.get().get();
        expectedResult.foo = "updated";
        Assert.assertEquals(expectedResult, documentSnapshot.toObject(LocalFirestoreHelper.AllSupportedTypes.class));
    }

    @Test(expected = ExecutionException.class)
    public void updateDocumentExists() throws Exception {
        randomDoc.update(SINGLE_FIELD_MAP).get();
    }

    @Test
    public void serverTimestamp() throws Exception {
        randomDoc.set(LocalFirestoreHelper.map("time", FieldValue.serverTimestamp())).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertTrue(((documentSnapshot.getDate("time").getTime()) > 0));
    }

    @Test
    public void timestampDoesntGetTruncatedDuringUpdate() throws Exception {
        DocumentReference documentReference = addDocument("time", Timestamp.ofTimeSecondsAndNanos(0, 123000));
        DocumentSnapshot documentSnapshot = documentReference.get().get();
        Timestamp timestamp = documentSnapshot.getTimestamp("time");
        documentReference.update("time", timestamp);
        documentSnapshot = documentReference.get().get();
        timestamp = documentSnapshot.getTimestamp("time");
        Assert.assertEquals(123000, timestamp.getNanos());
    }

    @Test
    public void fieldDelete() throws Exception {
        Map<String, Object> fields = new HashMap<>();
        fields.put("foo1", "bar1");
        fields.put("foo2", "bar2");
        randomDoc.set(fields).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertEquals("bar1", documentSnapshot.getString("foo1"));
        Assert.assertEquals("bar2", documentSnapshot.getString("foo2"));
        randomDoc.update("foo1", "bar3", "foo2", FieldValue.delete()).get();
        documentSnapshot = randomDoc.get().get();
        Assert.assertEquals("bar3", documentSnapshot.getString("foo1"));
        Assert.assertNull(documentSnapshot.getString("foo2"));
    }

    @Test
    public void deleteDocument() throws Exception {
        randomDoc.delete().get();
        WriteResult writeResult = randomDoc.set(ALL_SUPPORTED_TYPES_MAP).get();
        try {
            randomDoc.delete(Precondition.updatedAt(Timestamp.ofTimeSecondsAndNanos(1, 0))).get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getMessage().contains("FAILED_PRECONDITION"));
        }
        writeResult = randomDoc.delete(Precondition.updatedAt(writeResult.getUpdateTime())).get();
        DocumentSnapshot documentSnapshot = randomDoc.get().get();
        Assert.assertFalse(documentSnapshot.exists());
        Assert.assertTrue(((writeResult.getUpdateTime().getSeconds()) > 0));
    }

    @Test
    public void defaultQuery() throws Exception {
        addDocument("foo", "bar");
        addDocument("foo", "bar");
        QuerySnapshot querySnapshot = randomColl.get().get();
        Assert.assertEquals(2, querySnapshot.size());
        Iterator<QueryDocumentSnapshot> documents = querySnapshot.iterator();
        Assert.assertEquals("bar", documents.next().get("foo"));
        Assert.assertEquals("bar", documents.next().get("foo"));
    }

    @Test
    public void noResults() throws Exception {
        QuerySnapshot querySnapshot = randomColl.get().get();
        Assert.assertTrue(querySnapshot.isEmpty());
        Assert.assertNotNull(querySnapshot.getReadTime());
    }

    @Test
    public void queryWithMicrosecondPrecision() throws Exception {
        Timestamp microsecondTimestamp = Timestamp.ofTimeSecondsAndNanos(0, 123000);
        DocumentReference documentReference = addDocument("time", microsecondTimestamp);
        DocumentSnapshot documentSnapshot = documentReference.get().get();
        Query query = randomColl.whereEqualTo("time", microsecondTimestamp);
        QuerySnapshot querySnapshot = query.get().get();
        Assert.assertEquals(1, querySnapshot.size());
        // Using `.toDate()` truncates to millseconds, and hence the query doesn't match.
        query = randomColl.whereEqualTo("time", microsecondTimestamp.toDate());
        querySnapshot = query.get().get();
        Assert.assertEquals(0, querySnapshot.size());
    }

    @Test
    public void nestedQuery() throws Exception {
        randomColl = randomColl.document("foo").collection("bar");
        addDocument("foo", "bar");
        QuerySnapshot querySnapshot = randomColl.get().get();
        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
        Assert.assertTrue(documentSnapshot.getReference().getPath().contains("/foo/bar"));
    }

    @Test
    public void limitQuery() throws Exception {
        addDocument("foo", "bar");
        addDocument("foo", "bar");
        QuerySnapshot querySnapshot = randomColl.limit(1).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        Assert.assertEquals("bar", querySnapshot.getDocuments().get(0).get("foo"));
    }

    @Test
    public void offsetQuery() throws Exception {
        addDocument("foo", "bar");
        addDocument("foo", "bar");
        QuerySnapshot querySnapshot = randomColl.offset(1).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        Assert.assertEquals("bar", querySnapshot.getDocuments().get(0).get("foo"));
    }

    @Test
    public void orderByQuery() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.orderBy("foo").get().get();
        Iterator<QueryDocumentSnapshot> documents = querySnapshot.iterator();
        Assert.assertEquals(1L, documents.next().get("foo"));
        Assert.assertEquals(2L, documents.next().get("foo"));
        querySnapshot = randomColl.orderBy("foo", DESCENDING).get().get();
        documents = querySnapshot.iterator();
        Assert.assertEquals(2L, documents.next().get("foo"));
        Assert.assertEquals(1L, documents.next().get("foo"));
    }

    @Test
    public void selectQuery() throws Exception {
        addDocument("foo", 1, "bar", 2);
        QuerySnapshot querySnapshot = randomColl.select("foo").get().get();
        Assert.assertEquals(1, querySnapshot.size());
        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
        Assert.assertEquals(1L, documentSnapshot.get("foo"));
        Assert.assertNull(documentSnapshot.get("bar"));
    }

    @Test
    public void equalsQuery() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.whereEqualTo("foo", 1).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        Assert.assertEquals(1L, querySnapshot.getDocuments().get(0).get("foo"));
    }

    @Test
    public void greaterThanQuery() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.whereGreaterThan("foo", 1).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        Assert.assertEquals(2L, querySnapshot.getDocuments().get(0).get("foo"));
    }

    @Test
    public void greaterThanOrEqualQuery() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.whereGreaterThanOrEqualTo("foo", 1).get().get();
        Assert.assertEquals(2, querySnapshot.size());
    }

    @Test
    public void lessThanQuery() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.whereLessThan("foo", 2).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        Assert.assertEquals(1L, querySnapshot.getDocuments().get(0).get("foo"));
    }

    @Test
    public void lessThanOrEqualQuery() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.whereLessThanOrEqualTo("foo", 2).get().get();
        Assert.assertEquals(2, querySnapshot.size());
    }

    @Test
    public void unaryQuery() throws Exception {
        addDocument("foo", 1, "bar", Double.NaN);
        addDocument("foo", 2, "bar", null);
        QuerySnapshot querySnapshot = randomColl.whereEqualTo("bar", Double.NaN).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
        Assert.assertEquals(1L, documentSnapshot.get("foo"));
        Assert.assertEquals(Double.NaN, documentSnapshot.get("bar"));
        querySnapshot = randomColl.whereEqualTo("bar", null).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        documentSnapshot = querySnapshot.getDocuments().get(0);
        Assert.assertEquals(2L, documentSnapshot.get("foo"));
        Assert.assertNull(documentSnapshot.get("bar"));
    }

    @Test
    public void startAt() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.orderBy("foo").startAt(1).get().get();
        Assert.assertEquals(2, querySnapshot.size());
        Iterator<QueryDocumentSnapshot> documents = querySnapshot.iterator();
        Assert.assertEquals(1L, documents.next().get("foo"));
        Assert.assertEquals(2L, documents.next().get("foo"));
    }

    @Test
    public void startAfter() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.orderBy("foo").startAfter(1).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        Assert.assertEquals(2L, querySnapshot.getDocuments().get(0).get("foo"));
    }

    @Test
    public void endAt() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.orderBy("foo").endAt(2).get().get();
        Assert.assertEquals(2, querySnapshot.size());
        Iterator<QueryDocumentSnapshot> documents = querySnapshot.iterator();
        Assert.assertEquals(1L, documents.next().get("foo"));
        Assert.assertEquals(2L, documents.next().get("foo"));
    }

    @Test
    public void endBefore() throws Exception {
        addDocument("foo", 1);
        addDocument("foo", 2);
        QuerySnapshot querySnapshot = randomColl.orderBy("foo").endBefore(2).get().get();
        Assert.assertEquals(1, querySnapshot.size());
        Assert.assertEquals(1L, querySnapshot.getDocuments().get(0).get("foo"));
    }

    @Test
    public void failedTransaction() {
        try {
            firestore.runTransaction(new com.google.cloud.firestore.Transaction.Function<String>() {
                @Override
                public String updateCallback(Transaction transaction) {
                    throw new RuntimeException("User exception");
                }
            }).get();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().endsWith("User exception"));
        }
    }

    @Test
    public void successfulTransactionWithContention() throws Exception {
        final DocumentReference documentReference = addDocument("counter", 1);
        final CountDownLatch latch = new CountDownLatch(2);
        final AtomicInteger attempts = new AtomicInteger();
        // One of these transaction fails and has to be retried since they both acquire locks on the
        // same document, which they then modify.
        ApiFuture<String> firstTransaction = firestore.runTransaction(new Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) throws InterruptedException, ExecutionException {
                attempts.incrementAndGet();
                DocumentSnapshot documentSnapshot = transaction.get(documentReference).get();
                latch.countDown();
                latch.await();
                transaction.update(documentReference, "counter", ((documentSnapshot.getLong("counter")) + 1));
                return "foo";
            }
        });
        ApiFuture<String> secondTransaction = firestore.runTransaction(new com.google.cloud.firestore.Transaction.Function<String>() {
            @Override
            public String updateCallback(Transaction transaction) throws InterruptedException, ExecutionException {
                attempts.incrementAndGet();
                List<DocumentSnapshot> documentSnapshots = transaction.getAll(documentReference).get();
                latch.countDown();
                latch.await();
                transaction.update(documentReference, "counter", ((documentSnapshots.get(0).getLong("counter")) + 1));
                return "bar";
            }
        });
        Assert.assertEquals("foo", firstTransaction.get());
        Assert.assertEquals("bar", secondTransaction.get());
        Assert.assertEquals(3, attempts.intValue());
        Assert.assertEquals(3, ((long) (documentReference.get().get().getLong("counter"))));
    }

    @Test
    public void writeBatch() throws Exception {
        DocumentReference createDocument = randomColl.document();
        DocumentReference setDocument = randomColl.document();
        DocumentReference updateDocument = addDocument("foo", "bar");
        DocumentReference deleteDocument = addDocument("foo", "bar");
        WriteBatch batch = firestore.batch();
        batch.create(createDocument, SINGLE_FIELD_OBJECT);
        batch.set(setDocument, ALL_SUPPORTED_TYPES_OBJECT);
        batch.update(updateDocument, "foo", "foobar");
        batch.delete(deleteDocument);
        batch.commit().get();
        List<DocumentSnapshot> documentSnapshots = firestore.getAll(createDocument, setDocument, updateDocument, deleteDocument).get();
        Iterator<DocumentSnapshot> iterator = documentSnapshots.iterator();
        Assert.assertEquals(SINGLE_FIELD_OBJECT, iterator.next().toObject(LocalFirestoreHelper.SingleField.class));
        Assert.assertEquals(ALL_SUPPORTED_TYPES_OBJECT, iterator.next().toObject(LocalFirestoreHelper.AllSupportedTypes.class));
        Assert.assertEquals("foobar", iterator.next().get("foo"));
        Assert.assertFalse(iterator.next().exists());
    }

    @Test
    public void omitWriteResultForDocumentTransforms() throws InterruptedException, ExecutionException {
        WriteBatch batch = firestore.batch();
        batch.set(randomColl.document(), SINGLE_FIELD_MAP);
        batch.set(randomColl.document(), LocalFirestoreHelper.map("time", FieldValue.serverTimestamp()));
        List<WriteResult> writeResults = batch.commit().get();
        Assert.assertEquals(2, writeResults.size());
    }

    @Test
    public void listCollections() throws Exception {
        // We test with 21 collections since 20 collections are by default returned in a single paged
        // response.
        String[] collections = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" };
        Arrays.sort(collections);// Sort in alphabetical (non-numeric) order.

        WriteBatch batch = firestore.batch();
        for (String collection : collections) {
            batch.create(randomDoc.collection(collection).document("doc"), SINGLE_FIELD_OBJECT);
        }
        batch.commit().get();
        Iterable<CollectionReference> collectionRefs = randomDoc.listCollections();
        int count = 0;
        for (CollectionReference collectionRef : collectionRefs) {
            Assert.assertEquals(collections[(count++)], collectionRef.getId());
        }
        Assert.assertEquals(collections.length, count);
    }

    @Test
    public void listDocuments() throws Exception {
        // We test with 21 documents since 20 documents are by default returned in a single paged
        // response.
        String[] documents = new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" };
        Arrays.sort(documents);// Sort in alphabetical (non-numeric) order.

        WriteBatch batch = firestore.batch();
        for (String document : documents) {
            batch.create(randomColl.document(document), SINGLE_FIELD_OBJECT);
        }
        batch.commit().get();
        Iterable<DocumentReference> collectionRefs = randomColl.listDocuments();
        int count = 0;
        for (DocumentReference documentRef : collectionRefs) {
            Assert.assertEquals(documents[(count++)], documentRef.getId());
        }
        Assert.assertEquals(documents.length, count);
    }

    @Test
    public void listDocumentsListsMissingDocument() throws Exception {
        randomColl.document("missing/foo/bar").set(SINGLE_FIELD_MAP).get();
        Iterable<DocumentReference> collectionRefs = randomColl.listDocuments();
        Assert.assertEquals(randomColl.document("missing"), collectionRefs.iterator().next());
    }

    @Test
    public void addAndRemoveFields() throws InterruptedException, ExecutionException {
        Map<String, Object> expected = new HashMap<>();
        randomDoc.create(Collections.<String, Object>emptyMap()).get();
        Assert.assertEquals(expected, getData());
        randomDoc.delete().get();
        Assert.assertFalse(randomDoc.get().get().exists());
        randomDoc.create(LocalFirestoreHelper.map("a", LocalFirestoreHelper.map("b", "c"))).get();
        expected.put("a", LocalFirestoreHelper.map("b", "c"));
        Assert.assertEquals(expected, getData());
        randomDoc.set(Collections.<String, Object>emptyMap()).get();
        expected = LocalFirestoreHelper.map();
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.map("a", LocalFirestoreHelper.map("b", "c"))).get();
        expected.put("a", LocalFirestoreHelper.map("b", "c"));
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.map("a", LocalFirestoreHelper.map("d", "e")), SetOptions.merge()).get();
        getNestedMap(expected, "a").put("d", "e");
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.<String, Object>map("a", LocalFirestoreHelper.map("d", FieldValue.delete())), SetOptions.merge()).get();
        getNestedMap(expected, "a").remove("d");
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.<String, Object>map("a", LocalFirestoreHelper.map("b", FieldValue.delete())), SetOptions.merge()).get();
        getNestedMap(expected, "a").remove("b");
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.<String, Object>map("a", LocalFirestoreHelper.map("e", "foo")), SetOptions.merge()).get();
        getNestedMap(expected, "a").put("e", "foo");
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.map("f", "foo"), SetOptions.merge()).get();
        expected.put("f", "foo");
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.<String, Object>map("f", LocalFirestoreHelper.map("g", "foo")), SetOptions.merge()).get();
        expected.put("f", LocalFirestoreHelper.map("g", "foo"));
        Assert.assertEquals(expected, getData());
        randomDoc.update("f.h", "foo").get();
        getNestedMap(expected, "f").put("h", "foo");
        Assert.assertEquals(expected, getData());
        randomDoc.update("f.g", FieldValue.delete()).get();
        getNestedMap(expected, "f").remove("g");
        Assert.assertEquals(expected, getData());
        randomDoc.update("f.h", FieldValue.delete()).get();
        getNestedMap(expected, "f").remove("h");
        Assert.assertEquals(expected, getData());
        randomDoc.update("f", FieldValue.delete()).get();
        expected.remove("f");
        Assert.assertEquals(expected, getData());
        randomDoc.update("i.j", LocalFirestoreHelper.map()).get();
        expected.put("i", LocalFirestoreHelper.map("j", LocalFirestoreHelper.map()));
        Assert.assertEquals(expected, getData());
        randomDoc.update("i.j", LocalFirestoreHelper.map("k", "foo")).get();
        getNestedMap(expected, "i").put("j", LocalFirestoreHelper.map("k", "foo"));
        Assert.assertEquals(expected, getData());
        randomDoc.update("i.j", LocalFirestoreHelper.<String, Object>map("l", LocalFirestoreHelper.map("k", "foo"))).get();
        getNestedMap(expected, "i").put("j", LocalFirestoreHelper.map("l", LocalFirestoreHelper.map("k", "foo")));
        Assert.assertEquals(expected, getData());
        randomDoc.update("i.j", LocalFirestoreHelper.<String, Object>map("l", LocalFirestoreHelper.map())).get();
        getNestedMap(expected, "i").put("j", LocalFirestoreHelper.map("l", LocalFirestoreHelper.map()));
        Assert.assertEquals(expected, getData());
        randomDoc.update("i", FieldValue.delete()).get();
        expected.remove("i");
        Assert.assertEquals(expected, getData());
        randomDoc.update("a", FieldValue.delete()).get();
        expected.remove("a");
        Assert.assertEquals(expected, getData());
    }

    @Test
    public void addAndRemoveServerTimestamps() throws InterruptedException, ExecutionException {
        Map<String, Object> expected = new HashMap<>();
        randomDoc.create(LocalFirestoreHelper.map("time", FieldValue.serverTimestamp(), "a", LocalFirestoreHelper.map("b", FieldValue.serverTimestamp()))).get();
        Timestamp time = ((Timestamp) (getData().get("time")));
        expected.put("time", time);
        expected.put("a", LocalFirestoreHelper.map("b", time));
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.map("time", FieldValue.serverTimestamp(), "a", LocalFirestoreHelper.map("c", FieldValue.serverTimestamp()))).get();
        time = updateTime(expected);
        expected.put("a", LocalFirestoreHelper.map("c", time));
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.map("time", FieldValue.serverTimestamp(), "e", FieldValue.serverTimestamp()), SetOptions.merge()).get();
        time = updateTime(expected);
        expected.put("e", time);
        Assert.assertEquals(expected, getData());
        randomDoc.set(LocalFirestoreHelper.map("time", FieldValue.serverTimestamp(), "e", LocalFirestoreHelper.map("f", FieldValue.serverTimestamp())), SetOptions.merge()).get();
        time = updateTime(expected);
        expected.put("e", LocalFirestoreHelper.map("f", time));
        Assert.assertEquals(expected, getData());
        randomDoc.update("time", FieldValue.serverTimestamp(), "g.h", FieldValue.serverTimestamp()).get();
        time = updateTime(expected);
        expected.put("g", LocalFirestoreHelper.map("h", time));
        Assert.assertEquals(expected, getData());
        randomDoc.update("time", FieldValue.serverTimestamp(), "g.j", LocalFirestoreHelper.map("k", FieldValue.serverTimestamp())).get();
        time = updateTime(expected);
        getNestedMap(expected, "g").put("j", LocalFirestoreHelper.map("k", time));
        Assert.assertEquals(expected, getData());
    }

    @Test
    public void documentWatch() throws Exception {
        final DocumentReference documentReference = randomColl.document();
        final Semaphore semaphore = new Semaphore(0);
        ListenerRegistration registration = null;
        try {
            registration = documentReference.addSnapshotListener(new com.google.cloud.firestore.EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable
                DocumentSnapshot value, @Nullable
                FirestoreException error) {
                    try {
                        switch (semaphore.availablePermits()) {
                            case 0 :
                                Assert.assertFalse(value.exists());
                                documentReference.set(LocalFirestoreHelper.map("foo", "foo"));
                                break;
                            case 1 :
                                Assert.assertTrue(value.exists());
                                DocumentSnapshot documentSnapshot = documentReference.get().get();
                                Assert.assertEquals("foo", documentSnapshot.getString("foo"));
                                documentReference.set(LocalFirestoreHelper.map("foo", "bar"));
                                break;
                            case 2 :
                                Assert.assertTrue(value.exists());
                                documentSnapshot = documentReference.get().get();
                                Assert.assertEquals("bar", documentSnapshot.getString("foo"));
                                documentReference.delete();
                                break;
                            case 3 :
                                Assert.assertFalse(value.exists());
                                break;
                        }
                    } catch (Exception e) {
                        Assert.fail(e.getMessage());
                    }
                    semaphore.release();
                }
            });
            semaphore.acquire(4);
        } finally {
            if (registration != null) {
                registration.remove();
            }
        }
    }

    @Test
    public void queryWatch() throws Exception {
        final Semaphore semaphore = new Semaphore(0);
        ListenerRegistration registration = null;
        try {
            registration = randomColl.whereEqualTo("foo", "bar").addSnapshotListener(new com.google.cloud.firestore.EventListener<QuerySnapshot>() {
                DocumentReference ref1;

                DocumentReference ref2;

                @Override
                public void onEvent(@Nullable
                QuerySnapshot value, @Nullable
                FirestoreException error) {
                    try {
                        switch (semaphore.availablePermits()) {
                            case 0 :
                                Assert.assertTrue(value.isEmpty());
                                ref1 = randomColl.add(LocalFirestoreHelper.map("foo", "foo")).get();
                                ref2 = randomColl.add(LocalFirestoreHelper.map("foo", "bar")).get();
                                break;
                            case 1 :
                                Assert.assertEquals(1, value.size());
                                Assert.assertEquals(1, value.getDocumentChanges().size());
                                Assert.assertEquals(ADDED, value.getDocumentChanges().get(0).getType());
                                ref1.set(LocalFirestoreHelper.map("foo", "bar"));
                                break;
                            case 2 :
                                Assert.assertEquals(2, value.size());
                                Assert.assertEquals(1, value.getDocumentChanges().size());
                                Assert.assertEquals(ADDED, value.getDocumentChanges().get(0).getType());
                                ref1.set(LocalFirestoreHelper.map("foo", "bar", "bar", " foo"));
                                break;
                            case 3 :
                                Assert.assertEquals(2, value.size());
                                Assert.assertEquals(1, value.getDocumentChanges().size());
                                Assert.assertEquals(MODIFIED, value.getDocumentChanges().get(0).getType());
                                ref2.set(LocalFirestoreHelper.map("foo", "foo"));
                                break;
                            case 4 :
                                Assert.assertEquals(1, value.size());
                                Assert.assertEquals(1, value.getDocumentChanges().size());
                                Assert.assertEquals(REMOVED, value.getDocumentChanges().get(0).getType());
                                ref1.delete();
                                break;
                            case 5 :
                                Assert.assertTrue(value.isEmpty());
                                Assert.assertEquals(1, value.getDocumentChanges().size());
                                Assert.assertEquals(REMOVED, value.getDocumentChanges().get(0).getType());
                                break;
                        }
                    } catch (Exception e) {
                        Assert.fail(e.getMessage());
                    }
                    semaphore.release();
                }
            });
            semaphore.acquire(6);
        } finally {
            if (registration != null) {
                registration.remove();
            }
        }
    }

    @Test
    public void queryPaginationWithOrderByClause() throws InterruptedException, ExecutionException {
        WriteBatch batch = firestore.batch();
        for (int i = 0; i < 10; ++i) {
            batch.set(randomColl.document(), LocalFirestoreHelper.map("val", i));
        }
        batch.commit().get();
        Query query = randomColl.orderBy("val").limit(3);
        List<DocumentSnapshot> results = new ArrayList<>();
        int pageCount = paginateResults(query, results);
        Assert.assertEquals(4, pageCount);
        Assert.assertEquals(10, results.size());
    }

    @Test
    public void queryPaginationWithWhereClause() throws InterruptedException, ExecutionException {
        WriteBatch batch = firestore.batch();
        for (int i = 0; i < 10; ++i) {
            batch.set(randomColl.document(), LocalFirestoreHelper.map("val", i));
        }
        batch.commit().get();
        Query query = randomColl.whereGreaterThanOrEqualTo("val", 1).limit(3);
        List<DocumentSnapshot> results = new ArrayList<>();
        int pageCount = paginateResults(query, results);
        Assert.assertEquals(3, pageCount);
        Assert.assertEquals(9, results.size());
    }

    @Test
    public void arrayOperators() throws InterruptedException, ExecutionException {
        Query containsQuery = randomColl.whereArrayContains("foo", "bar");
        Assert.assertTrue(containsQuery.get().get().isEmpty());
        DocumentReference doc1 = randomColl.document();
        DocumentReference doc2 = randomColl.document();
        doc1.set(Collections.singletonMap("foo", ((Object) (FieldValue.arrayUnion("bar"))))).get();
        doc2.set(Collections.singletonMap("foo", ((Object) (FieldValue.arrayUnion("baz"))))).get();
        Assert.assertEquals(1, containsQuery.get().get().size());
        doc1.set(Collections.singletonMap("foo", ((Object) (FieldValue.arrayRemove("bar"))))).get();
        doc2.set(Collections.singletonMap("foo", ((Object) (FieldValue.arrayRemove("baz"))))).get();
        Assert.assertTrue(containsQuery.get().get().isEmpty());
    }

    @Test
    public void integerIncrement() throws InterruptedException, ExecutionException {
        DocumentReference docRef = randomColl.document();
        docRef.set(Collections.singletonMap("sum", ((Object) (1L)))).get();
        docRef.update("sum", FieldValue.increment(2)).get();
        DocumentSnapshot docSnap = docRef.get().get();
        Assert.assertEquals(3L, docSnap.get("sum"));
    }

    @Test
    public void floatIncrement() throws InterruptedException, ExecutionException {
        DocumentReference docRef = randomColl.document();
        docRef.set(Collections.singletonMap("sum", ((Object) (1.1)))).get();
        docRef.update("sum", FieldValue.increment(2.2)).get();
        DocumentSnapshot docSnap = docRef.get().get();
        Assert.assertEquals(3.3, ((Double) (docSnap.get("sum"))), ITSystemTest.DOUBLE_EPSILON);
    }
}

