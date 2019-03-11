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
package com.google.cloud.datastore.it;


import Batch.Response;
import ResultType.ENTITY;
import ResultType.KEY;
import ResultType.PROJECTION_ENTITY;
import TransactionOptions.ReadOnly;
import ValueType.BOOLEAN;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Batch;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DatastoreReaderWriter;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.GqlQuery;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.LatLngValue;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.testing.RemoteDatastoreHelper;
import com.google.datastore.v1.TransactionOptions;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITDatastoreTest {
    private static final RemoteDatastoreHelper HELPER = RemoteDatastoreHelper.create();

    private static final DatastoreOptions OPTIONS = ITDatastoreTest.HELPER.getOptions();

    private static final Datastore DATASTORE = ITDatastoreTest.OPTIONS.getService();

    private static final String PROJECT_ID = ITDatastoreTest.OPTIONS.getProjectId();

    private static final String NAMESPACE = ITDatastoreTest.OPTIONS.getNamespace();

    private static final String KIND1 = "kind1";

    private static final String KIND2 = "kind2";

    private static final String KIND3 = "kind3";

    private static final NullValue NULL_VALUE = NullValue.of();

    private static final StringValue STR_VALUE = StringValue.of("str");

    private static final BooleanValue BOOL_VALUE = BooleanValue.newBuilder(false).setExcludeFromIndexes(true).build();

    private static final Key ROOT_KEY = Key.newBuilder(ITDatastoreTest.PROJECT_ID, "rootkey", "default").setNamespace(ITDatastoreTest.NAMESPACE).build();

    private static final IncompleteKey INCOMPLETE_KEY1 = IncompleteKey.newBuilder(ITDatastoreTest.ROOT_KEY, ITDatastoreTest.KIND1).setNamespace(ITDatastoreTest.NAMESPACE).build();

    private static final IncompleteKey INCOMPLETE_KEY2 = IncompleteKey.newBuilder(ITDatastoreTest.PROJECT_ID, ITDatastoreTest.KIND2).setNamespace(ITDatastoreTest.NAMESPACE).build();

    private static final Key KEY1 = Key.newBuilder(ITDatastoreTest.INCOMPLETE_KEY1, "name").build();

    private static final Key KEY2 = Key.newBuilder(ITDatastoreTest.KEY1, ITDatastoreTest.KIND2, 1).build();

    private static final Key KEY3 = Key.newBuilder(ITDatastoreTest.KEY2).setName("bla").setNamespace(ITDatastoreTest.NAMESPACE).build();

    private static final Key KEY4 = Key.newBuilder(ITDatastoreTest.KEY2).setName("newName1").setNamespace(ITDatastoreTest.NAMESPACE).build();

    private static final Key KEY5 = Key.newBuilder(ITDatastoreTest.KEY2).setName("newName2").setNamespace(ITDatastoreTest.NAMESPACE).build();

    private static final KeyValue KEY_VALUE = KeyValue.of(ITDatastoreTest.KEY1);

    private static final ListValue LIST_VALUE1 = ListValue.newBuilder().addValue(ITDatastoreTest.NULL_VALUE).addValue(ITDatastoreTest.STR_VALUE, ITDatastoreTest.BOOL_VALUE).build();

    private static final ListValue LIST_VALUE2 = ListValue.of(Collections.singletonList(ITDatastoreTest.KEY_VALUE));

    private static final ListValue EMPTY_LIST_VALUE = ListValue.of(Collections.<Value<?>>emptyList());

    private static final TimestampValue TIMESTAMP_VALUE = new TimestampValue(Timestamp.now());

    private static final LatLngValue LAT_LNG_VALUE = new LatLngValue(LatLng.of(37.422035, (-122.084124)));

    private static final FullEntity<IncompleteKey> PARTIAL_ENTITY1 = FullEntity.newBuilder(ITDatastoreTest.INCOMPLETE_KEY2).set("str", ITDatastoreTest.STR_VALUE).set("bool", ITDatastoreTest.BOOL_VALUE).set("list", ITDatastoreTest.LIST_VALUE1).build();

    private static final FullEntity<IncompleteKey> PARTIAL_ENTITY2 = FullEntity.newBuilder(ITDatastoreTest.PARTIAL_ENTITY1).remove("str").set("bool", true).set("list", ITDatastoreTest.LIST_VALUE1.get()).build();

    private static final FullEntity<IncompleteKey> PARTIAL_ENTITY3 = FullEntity.newBuilder(ITDatastoreTest.PARTIAL_ENTITY1).setKey(IncompleteKey.newBuilder(ITDatastoreTest.PROJECT_ID, ITDatastoreTest.KIND3).build()).build();

    private static final Entity ENTITY1 = Entity.newBuilder(ITDatastoreTest.KEY1).set("str", ITDatastoreTest.STR_VALUE).set("date", ITDatastoreTest.TIMESTAMP_VALUE).set("latLng", ITDatastoreTest.LAT_LNG_VALUE).set("bool", ITDatastoreTest.BOOL_VALUE).set("partial1", EntityValue.of(ITDatastoreTest.PARTIAL_ENTITY1)).set("list", ITDatastoreTest.LIST_VALUE2).set("emptyList", ITDatastoreTest.EMPTY_LIST_VALUE).build();

    private static final Entity ENTITY2 = Entity.newBuilder(ITDatastoreTest.ENTITY1).setKey(ITDatastoreTest.KEY2).remove("str").set("name", "Dan").setNull("null").set("age", 20).build();

    private static final Entity ENTITY3 = Entity.newBuilder(ITDatastoreTest.ENTITY1).setKey(ITDatastoreTest.KEY3).remove("str").set("null", ITDatastoreTest.NULL_VALUE).set("partial1", ITDatastoreTest.PARTIAL_ENTITY2).set("partial2", ITDatastoreTest.ENTITY2).build();

    @Rule
    public Timeout globalTimeout = Timeout.seconds(100);

    @Test
    public void testNewTransactionCommit() {
        Transaction transaction = ITDatastoreTest.DATASTORE.newTransaction();
        transaction.add(ITDatastoreTest.ENTITY3);
        Entity entity2 = Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().setNull("bla").build();
        transaction.update(entity2);
        transaction.delete(ITDatastoreTest.KEY1);
        transaction.commit();
        Assert.assertFalse(transaction.isActive());
        List<Entity> list = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.KEY1, ITDatastoreTest.KEY2, ITDatastoreTest.KEY3);
        Assert.assertNull(list.get(0));
        Assert.assertEquals(entity2, list.get(1));
        Assert.assertEquals(ITDatastoreTest.ENTITY3, list.get(2));
        Assert.assertEquals(3, list.size());
        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("FAILED_PRECONDITION", expected.getReason());
        }
        try {
            transaction.rollback();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("FAILED_PRECONDITION", expected.getReason());
        }
    }

    @Test
    public void testTransactionWithRead() {
        Transaction transaction = ITDatastoreTest.DATASTORE.newTransaction();
        Assert.assertNull(transaction.get(ITDatastoreTest.KEY3));
        transaction.add(ITDatastoreTest.ENTITY3);
        transaction.commit();
        Assert.assertEquals(ITDatastoreTest.ENTITY3, ITDatastoreTest.DATASTORE.get(ITDatastoreTest.KEY3));
        transaction = ITDatastoreTest.DATASTORE.newTransaction();
        Assert.assertEquals(ITDatastoreTest.ENTITY3, transaction.get(ITDatastoreTest.KEY3));
        // update entity3 during the transaction
        ITDatastoreTest.DATASTORE.put(Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().set("from", "datastore").build());
        transaction.update(Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().set("from", "transaction").build());
        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("ABORTED", expected.getReason());
        }
    }

    @Test
    public void testTransactionWithQuery() throws InterruptedException {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(ITDatastoreTest.KIND2).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.KEY2)).setNamespace(ITDatastoreTest.NAMESPACE).build();
        Transaction transaction = ITDatastoreTest.DATASTORE.newTransaction();
        QueryResults<Entity> results = transaction.run(query);
        Assert.assertTrue(results.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY2, results.next());
        Assert.assertFalse(results.hasNext());
        transaction.add(ITDatastoreTest.ENTITY3);
        transaction.commit();
        Assert.assertEquals(ITDatastoreTest.ENTITY3, ITDatastoreTest.DATASTORE.get(ITDatastoreTest.KEY3));
        transaction = ITDatastoreTest.DATASTORE.newTransaction();
        results = transaction.run(query);
        Assert.assertTrue(results.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY2, results.next());
        Assert.assertFalse(results.hasNext());
        transaction.delete(ITDatastoreTest.ENTITY3.getKey());
        // update entity2 during the transaction
        ITDatastoreTest.DATASTORE.put(Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().build());
        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("ABORTED", expected.getReason());
        }
    }

    @Test
    public void testNewTransactionRollback() {
        Transaction transaction = ITDatastoreTest.DATASTORE.newTransaction();
        transaction.add(ITDatastoreTest.ENTITY3);
        Entity entity2 = Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().setNull("bla").set("list3", StringValue.of("bla"), StringValue.newBuilder("bla").build()).build();
        transaction.update(entity2);
        transaction.delete(ITDatastoreTest.KEY1);
        transaction.rollback();
        transaction.rollback();// should be safe to repeat rollback calls

        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("FAILED_PRECONDITION", expected.getReason());
        }
        List<Entity> list = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.KEY1, ITDatastoreTest.KEY2, ITDatastoreTest.KEY3);
        Assert.assertEquals(ITDatastoreTest.ENTITY1, list.get(0));
        Assert.assertEquals(ITDatastoreTest.ENTITY2, list.get(1));
        Assert.assertNull(list.get(2));
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testNewBatch() {
        Batch batch = ITDatastoreTest.DATASTORE.newBatch();
        Entity entity1 = Entity.newBuilder(ITDatastoreTest.ENTITY1).clear().build();
        Entity entity2 = Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().setNull("bla").build();
        Entity entity4 = Entity.newBuilder(ITDatastoreTest.KEY4).set("value", StringValue.of("value")).build();
        Entity entity5 = Entity.newBuilder(ITDatastoreTest.KEY5).set("value", "value").build();
        List<Entity> entities = batch.add(entity4, ITDatastoreTest.PARTIAL_ENTITY2, entity5);
        Entity entity6 = entities.get(1);
        Assert.assertSame(entity4, entities.get(0));
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getNames(), entity6.getNames());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey().getProjectId(), entity6.getKey().getProjectId());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey().getNamespace(), entity6.getKey().getNamespace());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey().getAncestors(), entity6.getKey().getAncestors());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey().getKind(), entity6.getKey().getKind());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey(), IncompleteKey.newBuilder(entity6.getKey()).build());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey().getAncestors(), entity6.getKey().getAncestors());
        Assert.assertNotEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey(), entity6.getKey());
        Assert.assertSame(entity5, entities.get(2));
        batch.addWithDeferredIdAllocation(ITDatastoreTest.PARTIAL_ENTITY3);
        batch.put(ITDatastoreTest.ENTITY3, entity1, entity2);
        Batch.Response response = batch.submit();
        entities = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.KEY1, ITDatastoreTest.KEY2, ITDatastoreTest.KEY3, entity4.getKey(), entity5.getKey(), entity6.getKey());
        Assert.assertEquals(entity1, entities.get(0));
        Assert.assertEquals(entity2, entities.get(1));
        Assert.assertEquals(ITDatastoreTest.ENTITY3, entities.get(2));
        Assert.assertEquals(entity4, entities.get(3));
        Assert.assertEquals(entity5, entities.get(4));
        Assert.assertEquals(entity6, entities.get(5));
        Assert.assertEquals(6, entities.size());
        List<Key> generatedKeys = response.getGeneratedKeys();
        Assert.assertEquals(1, generatedKeys.size());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY3.getNames(), ITDatastoreTest.DATASTORE.get(generatedKeys.get(0)).getNames());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY3.getKey(), IncompleteKey.newBuilder(generatedKeys.get(0)).build());
        try {
            batch.submit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("FAILED_PRECONDITION", expected.getReason());
        }
        batch = ITDatastoreTest.DATASTORE.newBatch();
        batch.delete(entity4.getKey(), entity5.getKey(), entity6.getKey());
        batch.update(ITDatastoreTest.ENTITY1, ITDatastoreTest.ENTITY2, ITDatastoreTest.ENTITY3);
        batch.submit();
        entities = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.KEY1, ITDatastoreTest.KEY2, ITDatastoreTest.KEY3, entity4.getKey(), entity5.getKey(), entity6.getKey());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, entities.get(0));
        Assert.assertEquals(ITDatastoreTest.ENTITY2, entities.get(1));
        Assert.assertEquals(ITDatastoreTest.ENTITY3, entities.get(2));
        Assert.assertNull(entities.get(3));
        Assert.assertNull(entities.get(4));
        Assert.assertNull(entities.get(5));
        Assert.assertEquals(6, entities.size());
    }

    @Test
    public void testRunGqlQueryNoCasting() throws InterruptedException {
        Query<Entity> query1 = Query.newGqlQueryBuilder(ENTITY, ("select * from " + (ITDatastoreTest.KIND1))).setNamespace(ITDatastoreTest.NAMESPACE).build();
        Query<Entity> scQuery1 = Query.newEntityQueryBuilder().setNamespace(ITDatastoreTest.NAMESPACE).setKind(ITDatastoreTest.KIND1).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).build();
        Iterator<Entity> results1 = getStronglyConsistentResults(scQuery1, query1);
        Assert.assertTrue(results1.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, results1.next());
        Assert.assertFalse(results1.hasNext());
        ITDatastoreTest.DATASTORE.put(ITDatastoreTest.ENTITY3);
        Query<? extends Entity> query2 = Query.newGqlQueryBuilder(ENTITY, (("select * from " + (ITDatastoreTest.KIND2)) + " order by __key__")).setNamespace(ITDatastoreTest.NAMESPACE).build();
        Query<? extends Entity> scQuery2 = Query.newEntityQueryBuilder().setNamespace(ITDatastoreTest.NAMESPACE).setKind(ITDatastoreTest.KIND2).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).setOrderBy(OrderBy.asc("__key__")).build();
        Iterator<Entity> results2 = getStronglyConsistentResults(scQuery2, query2);
        Assert.assertTrue(results2.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY2, results2.next());
        Assert.assertTrue(results2.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY3, results2.next());
        Assert.assertFalse(results2.hasNext());
        query1 = Query.newGqlQueryBuilder(ENTITY, "select * from bla").setNamespace(ITDatastoreTest.NAMESPACE).build();
        scQuery1 = Query.newEntityQueryBuilder().setNamespace(ITDatastoreTest.NAMESPACE).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).setKind("bla").build();
        results1 = getStronglyConsistentResults(scQuery1, query1);
        Assert.assertFalse(results1.hasNext());
        Query<Key> keyOnlyQuery = Query.newGqlQueryBuilder(KEY, ("select __key__ from " + (ITDatastoreTest.KIND1))).setNamespace(ITDatastoreTest.NAMESPACE).build();
        Query<Key> scKeyOnlyQuery = Query.newKeyQueryBuilder().setNamespace(ITDatastoreTest.NAMESPACE).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).setKind(ITDatastoreTest.KIND1).build();
        Iterator<Key> keyOnlyResults = getStronglyConsistentResults(scKeyOnlyQuery, keyOnlyQuery);
        Assert.assertTrue(keyOnlyResults.hasNext());
        Assert.assertEquals(ITDatastoreTest.KEY1, keyOnlyResults.next());
        Assert.assertFalse(keyOnlyResults.hasNext());
        GqlQuery<ProjectionEntity> keyProjectionQuery = Query.newGqlQueryBuilder(PROJECTION_ENTITY, ("select __key__ from " + (ITDatastoreTest.KIND1))).setNamespace(ITDatastoreTest.NAMESPACE).build();
        Query<ProjectionEntity> scKeyProjectionQuery = Query.newProjectionEntityQueryBuilder().addProjection("__key__").setNamespace(ITDatastoreTest.NAMESPACE).setKind(ITDatastoreTest.KIND1).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).build();
        Iterator<ProjectionEntity> keyProjectionResult = getStronglyConsistentResults(scKeyProjectionQuery, keyProjectionQuery);
        Assert.assertTrue(keyProjectionResult.hasNext());
        ProjectionEntity projectionEntity = keyProjectionResult.next();
        Assert.assertEquals(ITDatastoreTest.KEY1, projectionEntity.getKey());
        Assert.assertTrue(projectionEntity.getNames().isEmpty());
        Assert.assertFalse(keyProjectionResult.hasNext());
    }

    @Test
    public void testRunGqlQueryWithCasting() throws InterruptedException {
        @SuppressWarnings("unchecked")
        Query<Entity> query1 = ((Query<Entity>) (Query.newGqlQueryBuilder(("select * from " + (ITDatastoreTest.KIND1))).setNamespace(ITDatastoreTest.NAMESPACE).build()));
        Query<Entity> scQuery1 = Query.newEntityQueryBuilder().setNamespace(ITDatastoreTest.NAMESPACE).setKind(ITDatastoreTest.KIND1).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).build();
        Iterator<Entity> results1 = getStronglyConsistentResults(scQuery1, query1);
        Assert.assertTrue(results1.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, results1.next());
        Assert.assertFalse(results1.hasNext());
        Query<?> query2 = Query.newGqlQueryBuilder(("select * from " + (ITDatastoreTest.KIND1))).setNamespace(ITDatastoreTest.NAMESPACE).build();
        QueryResults<?> results2 = ITDatastoreTest.DATASTORE.run(query2);
        Assert.assertSame(Entity.class, results2.getResultClass());
        Query<?> scQuery2 = Query.newEntityQueryBuilder().setNamespace(ITDatastoreTest.NAMESPACE).setKind(ITDatastoreTest.KIND1).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).build();
        Iterator<Entity> results3 = getStronglyConsistentResults(scQuery2, query2);
        Assert.assertTrue(results3.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, results3.next());
        Assert.assertFalse(results3.hasNext());
    }

    @Test
    public void testRunStructuredQuery() throws InterruptedException {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(ITDatastoreTest.KIND1).setOrderBy(OrderBy.asc("__key__")).build();
        Query<Entity> scQuery = Query.newEntityQueryBuilder().setKind(ITDatastoreTest.KIND1).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).setOrderBy(OrderBy.asc("__key__")).build();
        Iterator<Entity> results1 = getStronglyConsistentResults(scQuery, query);
        Assert.assertTrue(results1.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, results1.next());
        Assert.assertFalse(results1.hasNext());
        Query<Key> keyOnlyQuery = Query.newKeyQueryBuilder().setKind(ITDatastoreTest.KIND1).build();
        Query<Key> scKeyOnlyQuery = Query.newKeyQueryBuilder().setKind(ITDatastoreTest.KIND1).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).build();
        Iterator<Key> results2 = getStronglyConsistentResults(scKeyOnlyQuery, keyOnlyQuery);
        Assert.assertTrue(results2.hasNext());
        Assert.assertEquals(ITDatastoreTest.ENTITY1.getKey(), results2.next());
        Assert.assertFalse(results2.hasNext());
        StructuredQuery<ProjectionEntity> keyOnlyProjectionQuery = Query.newProjectionEntityQueryBuilder().setKind(ITDatastoreTest.KIND1).setProjection("__key__").build();
        StructuredQuery<ProjectionEntity> scKeyOnlyProjectionQuery = Query.newProjectionEntityQueryBuilder().setKind(ITDatastoreTest.KIND1).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).setProjection("__key__").build();
        Iterator<ProjectionEntity> results3 = getStronglyConsistentResults(scKeyOnlyProjectionQuery, keyOnlyProjectionQuery);
        Assert.assertTrue(results3.hasNext());
        ProjectionEntity projectionEntity = results3.next();
        Assert.assertEquals(ITDatastoreTest.ENTITY1.getKey(), projectionEntity.getKey());
        Assert.assertTrue(projectionEntity.getNames().isEmpty());
        Assert.assertFalse(results3.hasNext());
        StructuredQuery<ProjectionEntity> projectionQuery = Query.newProjectionEntityQueryBuilder().setKind(ITDatastoreTest.KIND2).setProjection("age").setFilter(PropertyFilter.gt("age", 18)).setDistinctOn("age").setOrderBy(OrderBy.asc("age")).setLimit(10).build();
        StructuredQuery<ProjectionEntity> scProjectionQuery = Query.newProjectionEntityQueryBuilder().setKind(ITDatastoreTest.KIND2).setFilter(PropertyFilter.hasAncestor(ITDatastoreTest.ROOT_KEY)).setProjection("age").setFilter(PropertyFilter.gt("age", 18)).setDistinctOn("age").setOrderBy(OrderBy.asc("age")).setLimit(10).build();
        Iterator<ProjectionEntity> results4 = getStronglyConsistentResults(scProjectionQuery, projectionQuery);
        Assert.assertTrue(results4.hasNext());
        ProjectionEntity entity = results4.next();
        Assert.assertEquals(ITDatastoreTest.ENTITY2.getKey(), entity.getKey());
        Assert.assertEquals(20, entity.getLong("age"));
        Assert.assertEquals(1, entity.getNames().size());
        Assert.assertFalse(results4.hasNext());
    }

    @Test
    public void testAllocateId() {
        KeyFactory keyFactory = ITDatastoreTest.DATASTORE.newKeyFactory().setKind(ITDatastoreTest.KIND1);
        IncompleteKey pk1 = keyFactory.newKey();
        Key key1 = ITDatastoreTest.DATASTORE.allocateId(pk1);
        Assert.assertEquals(key1.getProjectId(), pk1.getProjectId());
        Assert.assertEquals(key1.getNamespace(), pk1.getNamespace());
        Assert.assertEquals(key1.getAncestors(), pk1.getAncestors());
        Assert.assertEquals(key1.getKind(), pk1.getKind());
        Assert.assertTrue(key1.hasId());
        Assert.assertFalse(key1.hasName());
        Assert.assertEquals(Key.newBuilder(pk1, key1.getId()).build(), key1);
        Key key2 = ITDatastoreTest.DATASTORE.allocateId(pk1);
        Assert.assertNotEquals(key1, key2);
        Assert.assertEquals(Key.newBuilder(pk1, key2.getId()).build(), key2);
    }

    @Test
    public void testAllocateIdArray() {
        KeyFactory keyFactory = ITDatastoreTest.DATASTORE.newKeyFactory().setKind(ITDatastoreTest.KIND1);
        IncompleteKey incompleteKey1 = keyFactory.newKey();
        IncompleteKey incompleteKey2 = keyFactory.setKind(ITDatastoreTest.KIND2).addAncestors(PathElement.of(ITDatastoreTest.KIND1, 10)).newKey();
        List<Key> result = ITDatastoreTest.DATASTORE.allocateId(incompleteKey1, incompleteKey2, incompleteKey1);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(Key.newBuilder(incompleteKey1, result.get(0).getId()).build(), result.get(0));
        Assert.assertEquals(Key.newBuilder(incompleteKey1, result.get(2).getId()).build(), result.get(2));
        Assert.assertEquals(Key.newBuilder(incompleteKey2, result.get(1).getId()).build(), result.get(1));
    }

    @Test
    public void testGet() {
        Entity entity = ITDatastoreTest.DATASTORE.get(ITDatastoreTest.KEY3);
        Assert.assertNull(entity);
        entity = ITDatastoreTest.DATASTORE.get(ITDatastoreTest.KEY1);
        Assert.assertEquals(ITDatastoreTest.ENTITY1, entity);
        StringValue value1 = entity.getValue("str");
        Assert.assertEquals(ITDatastoreTest.STR_VALUE, value1);
        BooleanValue value2 = entity.getValue("bool");
        Assert.assertEquals(ITDatastoreTest.BOOL_VALUE, value2);
        ListValue value3 = entity.getValue("list");
        Assert.assertEquals(ITDatastoreTest.LIST_VALUE2, value3);
        TimestampValue value4 = entity.getValue("date");
        Assert.assertEquals(ITDatastoreTest.TIMESTAMP_VALUE, value4);
        LatLngValue value5 = entity.getValue("latLng");
        Assert.assertEquals(ITDatastoreTest.LAT_LNG_VALUE, value5);
        FullEntity<IncompleteKey> value6 = entity.getEntity("partial1");
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY1, value6);
        ListValue value7 = entity.getValue("emptyList");
        Assert.assertEquals(ITDatastoreTest.EMPTY_LIST_VALUE, value7);
        Assert.assertEquals(7, entity.getNames().size());
        Assert.assertFalse(entity.contains("bla"));
    }

    @Test
    public void testGetArrayNoDeferredResults() {
        ITDatastoreTest.DATASTORE.put(ITDatastoreTest.ENTITY3);
        Iterator<Entity> result = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.KEY1, Key.newBuilder(ITDatastoreTest.KEY1).setName("bla").build(), ITDatastoreTest.KEY2, ITDatastoreTest.KEY3).iterator();
        Assert.assertEquals(ITDatastoreTest.ENTITY1, result.next());
        Assert.assertNull(result.next());
        Assert.assertEquals(ITDatastoreTest.ENTITY2, result.next());
        Entity entity3 = result.next();
        Assert.assertEquals(ITDatastoreTest.ENTITY3, entity3);
        Assert.assertTrue(entity3.isNull("null"));
        Assert.assertFalse(entity3.getBoolean("bool"));
        Assert.assertEquals(ITDatastoreTest.LIST_VALUE2.get(), entity3.getList("list"));
        FullEntity<IncompleteKey> partial1 = entity3.getEntity("partial1");
        FullEntity<IncompleteKey> partial2 = entity3.getEntity("partial2");
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2, partial1);
        Assert.assertEquals(ITDatastoreTest.ENTITY2, partial2);
        Assert.assertEquals(BOOLEAN, entity3.getValue("bool").getType());
        Assert.assertEquals(ITDatastoreTest.LAT_LNG_VALUE, entity3.getValue("latLng"));
        Assert.assertEquals(ITDatastoreTest.EMPTY_LIST_VALUE, entity3.getValue("emptyList"));
        Assert.assertEquals(8, entity3.getNames().size());
        Assert.assertFalse(entity3.contains("bla"));
        try {
            entity3.getString("str");
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            // expected - no such property
        }
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testAddEntity() {
        List<Entity> keys = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.ENTITY1.getKey(), ITDatastoreTest.ENTITY3.getKey());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, keys.get(0));
        Assert.assertNull(keys.get(1));
        Assert.assertEquals(2, keys.size());
        try {
            ITDatastoreTest.DATASTORE.add(ITDatastoreTest.ENTITY1);
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            // expected;
        }
        List<Entity> entities = ITDatastoreTest.DATASTORE.add(ITDatastoreTest.ENTITY3, ITDatastoreTest.PARTIAL_ENTITY1, ITDatastoreTest.PARTIAL_ENTITY2);
        Assert.assertEquals(ITDatastoreTest.ENTITY3, ITDatastoreTest.DATASTORE.get(ITDatastoreTest.ENTITY3.getKey()));
        Assert.assertEquals(ITDatastoreTest.ENTITY3, entities.get(0));
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY1.getNames(), entities.get(1).getNames());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY1.getKey().getAncestors(), entities.get(1).getKey().getAncestors());
        Assert.assertNotNull(ITDatastoreTest.DATASTORE.get(entities.get(1).getKey()));
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getNames(), entities.get(2).getNames());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY2.getKey().getAncestors(), entities.get(2).getKey().getAncestors());
        Assert.assertNotNull(ITDatastoreTest.DATASTORE.get(entities.get(2).getKey()));
        for (Entity entity : entities) {
            ITDatastoreTest.DATASTORE.delete(entity.getKey());
        }
    }

    @Test
    public void testUpdate() {
        List<Entity> keys = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.ENTITY1.getKey(), ITDatastoreTest.ENTITY3.getKey());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, keys.get(0));
        Assert.assertNull(keys.get(1));
        Assert.assertEquals(2, keys.size());
        try {
            ITDatastoreTest.DATASTORE.update(ITDatastoreTest.ENTITY3);
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            // expected;
        }
        ITDatastoreTest.DATASTORE.add(ITDatastoreTest.ENTITY3);
        Assert.assertEquals(ITDatastoreTest.ENTITY3, ITDatastoreTest.DATASTORE.get(ITDatastoreTest.ENTITY3.getKey()));
        Entity entity3 = Entity.newBuilder(ITDatastoreTest.ENTITY3).clear().set("bla", new NullValue()).build();
        Assert.assertNotEquals(ITDatastoreTest.ENTITY3, entity3);
        ITDatastoreTest.DATASTORE.update(entity3);
        Assert.assertEquals(entity3, ITDatastoreTest.DATASTORE.get(ITDatastoreTest.ENTITY3.getKey()));
    }

    @Test
    public void testPut() {
        Entity updatedEntity = Entity.newBuilder(ITDatastoreTest.ENTITY1).set("new_property", 42L).build();
        Assert.assertEquals(updatedEntity, ITDatastoreTest.DATASTORE.put(updatedEntity));
        Assert.assertEquals(updatedEntity, ITDatastoreTest.DATASTORE.get(updatedEntity.getKey()));
        Entity entity2 = Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().set("bla", new NullValue()).build();
        Assert.assertNotEquals(ITDatastoreTest.ENTITY2, entity2);
        List<Entity> entities = ITDatastoreTest.DATASTORE.put(ITDatastoreTest.ENTITY1, entity2, ITDatastoreTest.ENTITY3, ITDatastoreTest.PARTIAL_ENTITY1);
        Assert.assertEquals(ITDatastoreTest.ENTITY1, entities.get(0));
        Assert.assertEquals(entity2, entities.get(1));
        Assert.assertEquals(ITDatastoreTest.ENTITY3, entities.get(2));
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY1.getNames(), entities.get(3).getNames());
        Assert.assertEquals(ITDatastoreTest.PARTIAL_ENTITY1.getKey().getAncestors(), entities.get(3).getKey().getAncestors());
        Assert.assertEquals(ITDatastoreTest.ENTITY1, ITDatastoreTest.DATASTORE.get(ITDatastoreTest.ENTITY1.getKey()));
        Assert.assertEquals(entity2, ITDatastoreTest.DATASTORE.get(entity2.getKey()));
        Assert.assertEquals(ITDatastoreTest.ENTITY3, ITDatastoreTest.DATASTORE.get(ITDatastoreTest.ENTITY3.getKey()));
        Entity entity = ITDatastoreTest.DATASTORE.get(entities.get(3).getKey());
        Assert.assertEquals(entities.get(3), entity);
        for (Entity entityToDelete : entities) {
            ITDatastoreTest.DATASTORE.delete(entityToDelete.getKey());
        }
    }

    @Test
    public void testDelete() {
        Iterator<Entity> keys = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.ENTITY1.getKey(), ITDatastoreTest.ENTITY2.getKey(), ITDatastoreTest.ENTITY3.getKey()).iterator();
        Assert.assertEquals(ITDatastoreTest.ENTITY1, keys.next());
        Assert.assertEquals(ITDatastoreTest.ENTITY2, keys.next());
        Assert.assertNull(keys.next());
        Assert.assertFalse(keys.hasNext());
        ITDatastoreTest.DATASTORE.delete(ITDatastoreTest.ENTITY1.getKey(), ITDatastoreTest.ENTITY2.getKey(), ITDatastoreTest.ENTITY3.getKey());
        keys = ITDatastoreTest.DATASTORE.fetch(ITDatastoreTest.ENTITY1.getKey(), ITDatastoreTest.ENTITY2.getKey(), ITDatastoreTest.ENTITY3.getKey()).iterator();
        Assert.assertNull(keys.next());
        Assert.assertNull(keys.next());
        Assert.assertNull(keys.next());
        Assert.assertFalse(keys.hasNext());
    }

    @Test
    public void testRunInTransaction() {
        Datastore.TransactionCallable<Integer> callable1 = new Datastore.TransactionCallable<Integer>() {
            private Integer attempts = 1;

            @Override
            public Integer run(DatastoreReaderWriter transaction) {
                transaction.get(ITDatastoreTest.KEY1);
                if ((attempts) < 2) {
                    ++(attempts);
                    throw new DatastoreException(10, "", "ABORTED", false, null);
                }
                return attempts;
            }
        };
        int result = ITDatastoreTest.DATASTORE.runInTransaction(callable1);
        Assert.assertEquals(result, 2);
        Datastore.TransactionCallable<Integer> callable2 = new Datastore.TransactionCallable<Integer>() {
            private Integer attempts = 1;

            @Override
            public Integer run(DatastoreReaderWriter transaction) {
                transaction.get(ITDatastoreTest.KEY1);
                if ((attempts) < 2) {
                    ++(attempts);
                    throw new DatastoreException(4, "", "DEADLINE_EXCEEDED", false, null);
                }
                return attempts;
            }
        };
        try {
            ITDatastoreTest.DATASTORE.runInTransaction(callable2);
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals(4, getCode());
        }
    }

    @Test
    public void testRunInTransactionReadWrite() {
        final Entity entity1 = Entity.newBuilder(ITDatastoreTest.ENTITY1).clear().setNull("bla").build();
        Datastore.TransactionCallable<Integer> callable1 = new Datastore.TransactionCallable<Integer>() {
            private Integer attempts = 1;

            @Override
            public Integer run(DatastoreReaderWriter transaction) {
                transaction.update(entity1);
                if ((attempts) < 2) {
                    ++(attempts);
                    throw new DatastoreException(10, "", "ABORTED", false, null);
                }
                return attempts;
            }
        };
        int result = ITDatastoreTest.DATASTORE.runInTransaction(callable1);
        Assert.assertEquals(result, 2);
        final Entity entity2 = Entity.newBuilder(ITDatastoreTest.ENTITY2).clear().setNull("bla").build();
        Datastore.TransactionCallable<Integer> callable2 = new Datastore.TransactionCallable<Integer>() {
            private Integer attempts = 1;

            @Override
            public Integer run(DatastoreReaderWriter transaction) {
                transaction.update(entity2);
                if ((attempts) < 2) {
                    ++(attempts);
                    throw new DatastoreException(10, "", "ABORTED", false, null);
                }
                return attempts;
            }
        };
        TransactionOptions readOnlyOptions = TransactionOptions.newBuilder().setReadOnly(ReadOnly.getDefaultInstance()).build();
        try {
            ITDatastoreTest.DATASTORE.runInTransaction(callable2, readOnlyOptions);
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals(3, getCode());
        }
    }

    @Test
    public void testSkippedResults() {
        Query<Key> query = Query.newKeyQueryBuilder().setOffset(Integer.MAX_VALUE).build();
        int numberOfEntities = ITDatastoreTest.DATASTORE.run(query).getSkippedResults();
        Assert.assertEquals(2, numberOfEntities);
    }
}

