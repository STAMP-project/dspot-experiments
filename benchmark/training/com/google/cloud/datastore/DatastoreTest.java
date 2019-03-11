/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.datastore;


import Batch.Response;
import QueryResultBatch.MoreResultsType;
import QueryResultBatch.MoreResultsType.NO_MORE_RESULTS;
import ReadConsistency.EVENTUAL_VALUE;
import ResultType.ENTITY;
import ResultType.KEY;
import ResultType.PROJECTION_ENTITY;
import RunQueryRequest.Builder;
import TransactionOptions.ReadWrite;
import ValueType.BOOLEAN;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.spi.DatastoreRpcFactory;
import com.google.cloud.datastore.spi.v1.DatastoreRpc;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.datastore.v1.BeginTransactionRequest;
import com.google.datastore.v1.BeginTransactionResponse;
import com.google.datastore.v1.CommitRequest;
import com.google.datastore.v1.CommitResponse;
import com.google.datastore.v1.EntityResult;
import com.google.datastore.v1.LookupRequest;
import com.google.datastore.v1.LookupResponse;
import com.google.datastore.v1.PartitionId;
import com.google.datastore.v1.ReadOptions;
import com.google.datastore.v1.RollbackRequest;
import com.google.datastore.v1.RollbackResponse;
import com.google.datastore.v1.RunQueryRequest;
import com.google.datastore.v1.RunQueryResponse;
import com.google.datastore.v1.TransactionOptions;
import com.google.protobuf.ByteString;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static DatastoreException.UNKNOWN_CODE;


@RunWith(JUnit4.class)
public class DatastoreTest {
    private static LocalDatastoreHelper helper = LocalDatastoreHelper.create(1.0);

    private static final DatastoreOptions options = DatastoreTest.helper.getOptions();

    private static final Datastore datastore = DatastoreTest.options.getService();

    private static final String PROJECT_ID = DatastoreTest.options.getProjectId();

    private static final String KIND1 = "kind1";

    private static final String KIND2 = "kind2";

    private static final String KIND3 = "kind3";

    private static final NullValue NULL_VALUE = NullValue.of();

    private static final StringValue STR_VALUE = StringValue.of("str");

    private static final BooleanValue BOOL_VALUE = BooleanValue.newBuilder(false).setExcludeFromIndexes(true).build();

    private static final IncompleteKey INCOMPLETE_KEY1 = IncompleteKey.newBuilder(DatastoreTest.PROJECT_ID, DatastoreTest.KIND1).build();

    private static final IncompleteKey INCOMPLETE_KEY2 = IncompleteKey.newBuilder(DatastoreTest.PROJECT_ID, DatastoreTest.KIND2).build();

    private static final Key KEY1 = Key.newBuilder(DatastoreTest.INCOMPLETE_KEY1, "name").build();

    private static final Key KEY2 = Key.newBuilder(DatastoreTest.KEY1, DatastoreTest.KIND2, 1).build();

    private static final Key KEY3 = Key.newBuilder(DatastoreTest.KEY2).setName("bla").build();

    private static final Key KEY4 = Key.newBuilder(DatastoreTest.KEY2).setName("newName1").build();

    private static final Key KEY5 = Key.newBuilder(DatastoreTest.KEY2).setName("newName2").build();

    private static final KeyValue KEY_VALUE = KeyValue.of(DatastoreTest.KEY1);

    private static final ListValue LIST_VALUE1 = ListValue.newBuilder().addValue(DatastoreTest.NULL_VALUE).addValue(DatastoreTest.STR_VALUE, DatastoreTest.BOOL_VALUE).build();

    private static final ListValue LIST_VALUE2 = ListValue.of(Collections.singletonList(DatastoreTest.KEY_VALUE));

    private static final ListValue EMPTY_LIST_VALUE = ListValue.of(Collections.<Value<?>>emptyList());

    private static final TimestampValue TIMESTAMP_VALUE = new TimestampValue(Timestamp.now());

    private static final LatLngValue LAT_LNG_VALUE = new LatLngValue(new LatLng(37.422035, (-122.084124)));

    private static final FullEntity<IncompleteKey> PARTIAL_ENTITY1 = FullEntity.newBuilder(DatastoreTest.INCOMPLETE_KEY2).set("str", DatastoreTest.STR_VALUE).set("bool", DatastoreTest.BOOL_VALUE).set("list", DatastoreTest.LIST_VALUE1).build();

    private static final FullEntity<IncompleteKey> PARTIAL_ENTITY2 = FullEntity.newBuilder(DatastoreTest.PARTIAL_ENTITY1).remove("str").set("bool", true).set("list", DatastoreTest.LIST_VALUE1.get()).build();

    private static final FullEntity<IncompleteKey> PARTIAL_ENTITY3 = FullEntity.newBuilder(DatastoreTest.PARTIAL_ENTITY1).setKey(IncompleteKey.newBuilder(DatastoreTest.PROJECT_ID, DatastoreTest.KIND3).build()).build();

    private static final Entity ENTITY1 = Entity.newBuilder(DatastoreTest.KEY1).set("str", DatastoreTest.STR_VALUE).set("date", DatastoreTest.TIMESTAMP_VALUE).set("latLng", DatastoreTest.LAT_LNG_VALUE).set("bool", DatastoreTest.BOOL_VALUE).set("partial1", EntityValue.of(DatastoreTest.PARTIAL_ENTITY1)).set("list", DatastoreTest.LIST_VALUE2).set("emptyList", DatastoreTest.EMPTY_LIST_VALUE).build();

    private static final Entity ENTITY2 = Entity.newBuilder(DatastoreTest.ENTITY1).setKey(DatastoreTest.KEY2).remove("str").set("name", "Dan").setNull("null").set("age", 20).build();

    private static final Entity ENTITY3 = Entity.newBuilder(DatastoreTest.ENTITY1).setKey(DatastoreTest.KEY3).remove("str").set("null", DatastoreTest.NULL_VALUE).set("partial1", DatastoreTest.PARTIAL_ENTITY2).set("partial2", DatastoreTest.ENTITY2).build();

    private DatastoreOptions rpcMockOptions;

    private DatastoreRpcFactory rpcFactoryMock;

    private DatastoreRpc rpcMock;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetOptions() {
        Assert.assertSame(DatastoreTest.options, DatastoreTest.datastore.getOptions());
    }

    @Test
    public void testNewTransactionCommit() {
        Transaction transaction = DatastoreTest.datastore.newTransaction();
        transaction.add(DatastoreTest.ENTITY3);
        Entity entity2 = Entity.newBuilder(DatastoreTest.ENTITY2).clear().setNull("bla").build();
        transaction.update(entity2);
        transaction.delete(DatastoreTest.KEY1);
        transaction.commit();
        List<Entity> list = DatastoreTest.datastore.fetch(DatastoreTest.KEY1, DatastoreTest.KEY2, DatastoreTest.KEY3);
        Assert.assertNull(list.get(0));
        Assert.assertEquals(entity2, list.get(1));
        Assert.assertEquals(DatastoreTest.ENTITY3, list.get(2));
        Assert.assertEquals(3, list.size());
        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException ex) {
            // expected to fail
        }
        try {
            transaction.rollback();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException ex) {
            // expected to fail
        }
        verifyNotUsable(transaction);
    }

    @Test
    public void testTransactionWithRead() {
        Transaction transaction = DatastoreTest.datastore.newTransaction();
        Assert.assertNull(transaction.get(DatastoreTest.KEY3));
        transaction.add(DatastoreTest.ENTITY3);
        transaction.commit();
        Assert.assertEquals(DatastoreTest.ENTITY3, DatastoreTest.datastore.get(DatastoreTest.KEY3));
        transaction = DatastoreTest.datastore.newTransaction();
        Assert.assertEquals(DatastoreTest.ENTITY3, transaction.get(DatastoreTest.KEY3));
        // update entity3 during the transaction
        DatastoreTest.datastore.put(Entity.newBuilder(DatastoreTest.ENTITY3).clear().build());
        transaction.update(DatastoreTest.ENTITY2);
        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("ABORTED", expected.getReason());
        }
    }

    @Test
    public void testTransactionWithQuery() {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(DatastoreTest.KIND2).setFilter(PropertyFilter.hasAncestor(DatastoreTest.KEY2)).build();
        Transaction transaction = DatastoreTest.datastore.newTransaction();
        QueryResults<Entity> results = transaction.run(query);
        Assert.assertEquals(DatastoreTest.ENTITY2, results.next());
        Assert.assertFalse(results.hasNext());
        transaction.add(DatastoreTest.ENTITY3);
        transaction.commit();
        Assert.assertEquals(DatastoreTest.ENTITY3, DatastoreTest.datastore.get(DatastoreTest.KEY3));
        transaction = DatastoreTest.datastore.newTransaction();
        results = transaction.run(query);
        Assert.assertEquals(DatastoreTest.ENTITY2, results.next());
        transaction.delete(DatastoreTest.ENTITY3.getKey());
        // update entity2 during the transaction
        DatastoreTest.datastore.put(Entity.newBuilder(DatastoreTest.ENTITY2).clear().build());
        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            Assert.assertEquals("ABORTED", expected.getReason());
        }
    }

    @Test
    public void testNewTransactionRollback() {
        Transaction transaction = DatastoreTest.datastore.newTransaction();
        transaction.add(DatastoreTest.ENTITY3);
        Entity entity2 = Entity.newBuilder(DatastoreTest.ENTITY2).clear().setNull("bla").set("list3", StringValue.of("bla"), StringValue.newBuilder("bla").build()).build();
        transaction.update(entity2);
        transaction.delete(DatastoreTest.KEY1);
        transaction.rollback();
        transaction.rollback();// should be safe to repeat rollback calls

        try {
            transaction.commit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException ex) {
            // expected to fail
        }
        verifyNotUsable(transaction);
        List<Entity> list = DatastoreTest.datastore.fetch(DatastoreTest.KEY1, DatastoreTest.KEY2, DatastoreTest.KEY3);
        Assert.assertEquals(DatastoreTest.ENTITY1, list.get(0));
        Assert.assertEquals(DatastoreTest.ENTITY2, list.get(1));
        Assert.assertNull(list.get(2));
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testRunInTransactionWithReadWriteOption() {
        EasyMock.expect(rpcMock.beginTransaction(EasyMock.anyObject(BeginTransactionRequest.class))).andReturn(BeginTransactionResponse.getDefaultInstance());
        EasyMock.expect(rpcMock.rollback(EasyMock.anyObject(RollbackRequest.class))).andReturn(RollbackResponse.getDefaultInstance()).once();
        EasyMock.expect(rpcMock.beginTransaction(EasyMock.anyObject(BeginTransactionRequest.class))).andReturn(BeginTransactionResponse.getDefaultInstance());
        EasyMock.expect(rpcMock.commit(EasyMock.anyObject(CommitRequest.class))).andReturn(CommitResponse.newBuilder().build());
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore mockDatastore = rpcMockOptions.getService();
        Datastore.TransactionCallable<Integer> callable = new Datastore.TransactionCallable<Integer>() {
            private Integer attempts = 1;

            @Override
            public Integer run(DatastoreReaderWriter transaction) {
                if ((attempts) < 2) {
                    ++(attempts);
                    throw new DatastoreException(10, "", "ABORTED", false, null);
                }
                return attempts;
            }
        };
        TransactionOptions options = TransactionOptions.newBuilder().setReadWrite(ReadWrite.getDefaultInstance()).build();
        Integer result = mockDatastore.runInTransaction(callable, options);
        EasyMock.verify(rpcFactoryMock, rpcMock);
        Assert.assertEquals(2, result.intValue());
    }

    @Test
    public void testNewBatch() {
        Batch batch = DatastoreTest.datastore.newBatch();
        Entity entity1 = Entity.newBuilder(DatastoreTest.ENTITY1).clear().build();
        Entity entity2 = Entity.newBuilder(DatastoreTest.ENTITY2).clear().setNull("bla").build();
        Entity entity4 = Entity.newBuilder(DatastoreTest.KEY4).set("value", StringValue.of("value")).build();
        Entity entity5 = Entity.newBuilder(DatastoreTest.KEY5).set("value", "value").build();
        List<Entity> entities = batch.add(entity4, DatastoreTest.PARTIAL_ENTITY2, entity5);
        Entity entity6 = entities.get(1);
        Assert.assertSame(entity4, entities.get(0));
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getProperties(), entity6.getProperties());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getKey().getProjectId(), entity6.getKey().getProjectId());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getKey().getNamespace(), entity6.getKey().getNamespace());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getKey().getAncestors(), entity6.getKey().getAncestors());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getKey().getKind(), entity6.getKey().getKind());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getKey(), IncompleteKey.newBuilder(entity6.getKey()).build());
        Assert.assertNotEquals(DatastoreTest.PARTIAL_ENTITY2.getKey().getPath(), entity6.getKey().getPath());
        Assert.assertNotEquals(DatastoreTest.PARTIAL_ENTITY2.getKey(), entity6.getKey());
        Assert.assertSame(entity5, entities.get(2));
        batch.addWithDeferredIdAllocation(DatastoreTest.PARTIAL_ENTITY3);
        batch.put(DatastoreTest.ENTITY3, entity1, entity2);
        Batch.Response response = batch.submit();
        entities = DatastoreTest.datastore.fetch(DatastoreTest.KEY1, DatastoreTest.KEY2, DatastoreTest.KEY3, entity4.getKey(), entity5.getKey(), entity6.getKey());
        Assert.assertEquals(entity1, entities.get(0));
        Assert.assertEquals(entity2, entities.get(1));
        Assert.assertEquals(DatastoreTest.ENTITY3, entities.get(2));
        Assert.assertEquals(entity4, entities.get(3));
        Assert.assertEquals(entity5, entities.get(4));
        Assert.assertEquals(entity6, entities.get(5));
        Assert.assertEquals(6, entities.size());
        List<Key> generatedKeys = response.getGeneratedKeys();
        Assert.assertEquals(1, generatedKeys.size());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY3.getProperties(), DatastoreTest.datastore.get(generatedKeys.get(0)).getProperties());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY3.getKey(), IncompleteKey.newBuilder(generatedKeys.get(0)).build());
        try {
            batch.submit();
            Assert.fail("Expecting a failure");
        } catch (DatastoreException ex) {
            // expected to fail
        }
        verifyNotUsable(batch);
        batch = DatastoreTest.datastore.newBatch();
        batch.delete(entity4.getKey(), entity5.getKey());
        batch.update(DatastoreTest.ENTITY1, DatastoreTest.ENTITY2, DatastoreTest.ENTITY3);
        batch.submit();
        entities = DatastoreTest.datastore.fetch(DatastoreTest.KEY1, DatastoreTest.KEY2, DatastoreTest.KEY3, entity4.getKey(), entity5.getKey());
        Assert.assertEquals(DatastoreTest.ENTITY1, entities.get(0));
        Assert.assertEquals(DatastoreTest.ENTITY2, entities.get(1));
        Assert.assertEquals(DatastoreTest.ENTITY3, entities.get(2));
        Assert.assertNull(entities.get(3));
        Assert.assertNull(entities.get(4));
        Assert.assertEquals(5, entities.size());
    }

    @Test
    public void testRunGqlQueryNoCasting() {
        Query<Entity> query1 = Query.newGqlQueryBuilder(ENTITY, ("select * from " + (DatastoreTest.KIND1))).build();
        QueryResults<Entity> results1 = DatastoreTest.datastore.run(query1);
        Assert.assertTrue(results1.hasNext());
        Assert.assertEquals(DatastoreTest.ENTITY1, results1.next());
        Assert.assertFalse(results1.hasNext());
        DatastoreTest.datastore.put(DatastoreTest.ENTITY3);
        Query<? extends Entity> query2 = Query.newGqlQueryBuilder(ENTITY, (("select * from " + (DatastoreTest.KIND2)) + " order by __key__")).build();
        QueryResults<? extends Entity> results2 = DatastoreTest.datastore.run(query2);
        Assert.assertTrue(results2.hasNext());
        Assert.assertEquals(DatastoreTest.ENTITY2, results2.next());
        Assert.assertTrue(results2.hasNext());
        Assert.assertEquals(DatastoreTest.ENTITY3, results2.next());
        Assert.assertFalse(results2.hasNext());
        query1 = Query.newGqlQueryBuilder(ENTITY, "select * from bla").build();
        results1 = DatastoreTest.datastore.run(query1);
        Assert.assertFalse(results1.hasNext());
        Query<Key> keyOnlyQuery = Query.newGqlQueryBuilder(KEY, ("select __key__ from " + (DatastoreTest.KIND1))).build();
        QueryResults<Key> keyOnlyResults = DatastoreTest.datastore.run(keyOnlyQuery);
        Assert.assertTrue(keyOnlyResults.hasNext());
        Assert.assertEquals(DatastoreTest.KEY1, keyOnlyResults.next());
        Assert.assertFalse(keyOnlyResults.hasNext());
        GqlQuery<ProjectionEntity> keyProjectionQuery = Query.newGqlQueryBuilder(PROJECTION_ENTITY, ("select __key__ from " + (DatastoreTest.KIND1))).build();
        QueryResults<ProjectionEntity> keyProjectionResult = DatastoreTest.datastore.run(keyProjectionQuery);
        Assert.assertTrue(keyProjectionResult.hasNext());
        ProjectionEntity projectionEntity = keyProjectionResult.next();
        Assert.assertEquals(DatastoreTest.KEY1, projectionEntity.getKey());
        Assert.assertTrue(projectionEntity.getProperties().isEmpty());
        Assert.assertFalse(keyProjectionResult.hasNext());
        GqlQuery<ProjectionEntity> projectionQuery = Query.newGqlQueryBuilder(PROJECTION_ENTITY, ("select str, date from " + (DatastoreTest.KIND1))).build();
        QueryResults<ProjectionEntity> projectionResult = DatastoreTest.datastore.run(projectionQuery);
        Assert.assertTrue(projectionResult.hasNext());
        projectionEntity = projectionResult.next();
        Assert.assertEquals("str", projectionEntity.getString("str"));
        Assert.assertEquals(DatastoreTest.TIMESTAMP_VALUE.get(), projectionEntity.getTimestamp("date"));
        Assert.assertEquals(2, projectionEntity.getNames().size());
        Assert.assertFalse(projectionResult.hasNext());
    }

    @Test
    public void testRunGqlQueryWithCasting() {
        @SuppressWarnings("unchecked")
        Query<Entity> query1 = ((Query<Entity>) (Query.newGqlQueryBuilder(("select * from " + (DatastoreTest.KIND1))).build()));
        QueryResults<Entity> results1 = DatastoreTest.datastore.run(query1);
        Assert.assertTrue(results1.hasNext());
        Assert.assertEquals(DatastoreTest.ENTITY1, results1.next());
        Assert.assertFalse(results1.hasNext());
        Query<?> query2 = Query.newGqlQueryBuilder(("select * from " + (DatastoreTest.KIND1))).build();
        QueryResults<?> results2 = DatastoreTest.datastore.run(query2);
        Assert.assertSame(Entity.class, results2.getResultClass());
        @SuppressWarnings("unchecked")
        QueryResults<Entity> results3 = ((QueryResults<Entity>) (results2));
        Assert.assertTrue(results3.hasNext());
        Assert.assertEquals(DatastoreTest.ENTITY1, results3.next());
        Assert.assertFalse(results3.hasNext());
    }

    @Test
    public void testGqlQueryPagination() throws DatastoreException {
        List<RunQueryResponse> responses = buildResponsesForQueryPagination();
        for (int i = 0; i < (responses.size()); i++) {
            EasyMock.expect(rpcMock.runQuery(EasyMock.anyObject(RunQueryRequest.class))).andReturn(responses.get(i));
        }
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore mockDatastore = rpcMockOptions.getService();
        QueryResults<Key> results = mockDatastore.run(Query.newGqlQueryBuilder(KEY, "select __key__ from *").build());
        int count = 0;
        while (results.hasNext()) {
            count += 1;
            results.next();
        } 
        Assert.assertEquals(count, 5);
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testRunStructuredQuery() {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(DatastoreTest.KIND1).setOrderBy(OrderBy.asc("__key__")).build();
        QueryResults<Entity> results1 = DatastoreTest.datastore.run(query);
        Assert.assertTrue(results1.hasNext());
        Assert.assertEquals(DatastoreTest.ENTITY1, results1.next());
        Assert.assertFalse(results1.hasNext());
        Query<Key> keyOnlyQuery = Query.newKeyQueryBuilder().setKind(DatastoreTest.KIND1).build();
        QueryResults<Key> results2 = DatastoreTest.datastore.run(keyOnlyQuery);
        Assert.assertTrue(results2.hasNext());
        Assert.assertEquals(DatastoreTest.ENTITY1.getKey(), results2.next());
        Assert.assertFalse(results2.hasNext());
        StructuredQuery<ProjectionEntity> keyOnlyProjectionQuery = Query.newProjectionEntityQueryBuilder().setKind(DatastoreTest.KIND1).setProjection("__key__").build();
        QueryResults<ProjectionEntity> results3 = DatastoreTest.datastore.run(keyOnlyProjectionQuery);
        Assert.assertTrue(results3.hasNext());
        ProjectionEntity projectionEntity = results3.next();
        Assert.assertEquals(DatastoreTest.ENTITY1.getKey(), projectionEntity.getKey());
        Assert.assertTrue(projectionEntity.getNames().isEmpty());
        Assert.assertFalse(results2.hasNext());
        StructuredQuery<ProjectionEntity> projectionQuery = Query.newProjectionEntityQueryBuilder().setKind(DatastoreTest.KIND2).setProjection("age").setFilter(PropertyFilter.gt("age", 18)).setDistinctOn("age").setOrderBy(OrderBy.asc("age")).setLimit(10).build();
        QueryResults<ProjectionEntity> results4 = DatastoreTest.datastore.run(projectionQuery);
        Assert.assertTrue(results4.hasNext());
        ProjectionEntity entity = results4.next();
        Assert.assertEquals(DatastoreTest.ENTITY2.getKey(), entity.getKey());
        Assert.assertEquals(20, entity.getLong("age"));
        Assert.assertEquals(1, entity.getProperties().size());
        Assert.assertFalse(results4.hasNext());
    }

    @Test
    public void testStructuredQueryPagination() throws DatastoreException {
        List<RunQueryResponse> responses = buildResponsesForQueryPagination();
        for (int i = 0; i < (responses.size()); i++) {
            EasyMock.expect(rpcMock.runQuery(EasyMock.anyObject(RunQueryRequest.class))).andReturn(responses.get(i));
        }
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        QueryResults<Key> results = datastore.run(Query.newKeyQueryBuilder().build());
        int count = 0;
        while (results.hasNext()) {
            count += 1;
            results.next();
        } 
        Assert.assertEquals(count, 5);
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testStructuredQueryPaginationWithMoreResults() throws DatastoreException {
        List<RunQueryResponse> responses = buildResponsesForQueryPagination();
        for (int i = 0; i < (responses.size()); i++) {
            EasyMock.expect(rpcMock.runQuery(EasyMock.anyObject(RunQueryRequest.class))).andReturn(responses.get(i));
        }
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        QueryResults<Key> results = datastore.run(Query.newKeyQueryBuilder().build());
        int count = 0;
        while (results.hasNext()) {
            count += 1;
            results.next();
        } 
        Assert.assertEquals(count, 5);
        Assert.assertEquals(NO_MORE_RESULTS, results.getMoreResults());
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testQueryPaginationWithLimit() throws DatastoreException {
        List<RunQueryResponse> responses = buildResponsesForQueryPaginationWithLimit();
        List<ByteString> endCursors = Lists.newArrayListWithCapacity(responses.size());
        for (RunQueryResponse response : responses) {
            EasyMock.expect(rpcMock.runQuery(EasyMock.anyObject(RunQueryRequest.class))).andReturn(response);
            if ((response.getBatch().getMoreResults()) != (MoreResultsType.NOT_FINISHED)) {
                endCursors.add(response.getBatch().getEndCursor());
            }
        }
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        int limit = 2;
        int totalCount = 0;
        Iterator<ByteString> cursorIter = endCursors.iterator();
        StructuredQuery<Entity> query = Query.newEntityQueryBuilder().setLimit(limit).build();
        while (true) {
            QueryResults<Entity> results = datastore.run(query);
            int resultCount = 0;
            while (results.hasNext()) {
                results.next();
                resultCount++;
                totalCount++;
            } 
            Assert.assertTrue(cursorIter.hasNext());
            Cursor expectedEndCursor = Cursor.copyFrom(cursorIter.next().toByteArray());
            Assert.assertEquals(expectedEndCursor, results.getCursorAfter());
            if (resultCount < limit) {
                break;
            }
            query = query.toBuilder().setStartCursor(results.getCursorAfter()).build();
        } 
        Assert.assertEquals(5, totalCount);
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testRunKeyQueryWithOffset() {
        Query<Key> query = Query.newKeyQueryBuilder().setOffset(Integer.MAX_VALUE).build();
        int numberOfEntities = DatastoreTest.datastore.run(query).getSkippedResults();
        Assert.assertEquals(2, numberOfEntities);
    }

    @Test
    public void testEventualConsistencyQuery() {
        ReadOptions readOption = ReadOptions.newBuilder().setReadConsistencyValue(EVENTUAL_VALUE).build();
        com.google.datastore.v1.GqlQuery query = com.google.datastore.v1.GqlQuery.newBuilder().setQueryString("FROM * SELECT *").build();
        RunQueryRequest.Builder expectedRequest = RunQueryRequest.newBuilder().setReadOptions(readOption).setGqlQuery(query).setPartitionId(PartitionId.newBuilder().setProjectId(DatastoreTest.PROJECT_ID).build());
        EasyMock.expect(rpcMock.runQuery(expectedRequest.build())).andReturn(RunQueryResponse.newBuilder().build());
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        datastore.run(Query.newGqlQueryBuilder("FROM * SELECT *").build(), ReadOption.eventualConsistency());
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testToUrlSafe() {
        byte[][] invalidUtf8 = new byte[][]{ new byte[]{ ((byte) (254)) }, new byte[]{ ((byte) (193)), ((byte) (191)) }, new byte[]{ ((byte) (192)) }, new byte[]{ ((byte) (128)) } };
        for (byte[] bytes : invalidUtf8) {
            Assert.assertFalse(ByteString.copyFrom(bytes).isValidUtf8());
            Cursor cursor = new Cursor(ByteString.copyFrom(bytes));
            Assert.assertEquals(cursor, Cursor.fromUrlSafe(cursor.toUrlSafe()));
        }
    }

    @Test
    public void testAllocateId() {
        KeyFactory keyFactory = DatastoreTest.datastore.newKeyFactory().setKind(DatastoreTest.KIND1);
        IncompleteKey pk1 = keyFactory.newKey();
        Key key1 = DatastoreTest.datastore.allocateId(pk1);
        Assert.assertEquals(key1.getProjectId(), pk1.getProjectId());
        Assert.assertEquals(key1.getNamespace(), pk1.getNamespace());
        Assert.assertEquals(key1.getAncestors(), pk1.getAncestors());
        Assert.assertEquals(key1.getKind(), pk1.getKind());
        Assert.assertTrue(key1.hasId());
        Assert.assertFalse(key1.hasName());
        Assert.assertEquals(Key.newBuilder(pk1, key1.getId()).build(), key1);
        Key key2 = DatastoreTest.datastore.allocateId(pk1);
        Assert.assertNotEquals(key1, key2);
        Assert.assertEquals(Key.newBuilder(pk1, key2.getId()).build(), key2);
        try {
            DatastoreTest.datastore.allocateId(key1);
            Assert.fail("Expecting a failure");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals(expected.getMessage(), "keys must be IncompleteKey instances");
        }
    }

    @Test
    public void testAllocateIdArray() {
        KeyFactory keyFactory = DatastoreTest.datastore.newKeyFactory().setKind(DatastoreTest.KIND1);
        IncompleteKey incompleteKey1 = keyFactory.newKey();
        IncompleteKey incompleteKey2 = keyFactory.setKind(DatastoreTest.KIND2).addAncestor(PathElement.of(DatastoreTest.KIND1, 10)).newKey();
        Key key3 = keyFactory.newKey("name");
        List<Key> result1 = DatastoreTest.datastore.allocateId(incompleteKey1, incompleteKey2, incompleteKey1);
        Assert.assertEquals(3, result1.size());
        Assert.assertEquals(Key.newBuilder(incompleteKey1, result1.get(0).getId()).build(), result1.get(0));
        Assert.assertEquals(Key.newBuilder(incompleteKey1, result1.get(2).getId()).build(), result1.get(2));
        Assert.assertEquals(Key.newBuilder(incompleteKey2, result1.get(1).getId()).build(), result1.get(1));
        try {
            DatastoreTest.datastore.allocateId(incompleteKey1, incompleteKey2, key3);
            Assert.fail("expecting a failure");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals(expected.getMessage(), "keys must be IncompleteKey instances");
        }
    }

    @Test
    public void testGet() {
        Entity entity = DatastoreTest.datastore.get(DatastoreTest.KEY3);
        Assert.assertNull(entity);
        entity = DatastoreTest.datastore.get(DatastoreTest.KEY1);
        Assert.assertEquals(DatastoreTest.ENTITY1, entity);
        StringValue value1 = entity.getValue("str");
        Assert.assertEquals(DatastoreTest.STR_VALUE, value1);
        BooleanValue value2 = entity.getValue("bool");
        Assert.assertEquals(DatastoreTest.BOOL_VALUE, value2);
        ListValue value3 = entity.getValue("list");
        Assert.assertEquals(DatastoreTest.LIST_VALUE2, value3);
        TimestampValue value4 = entity.getValue("date");
        Assert.assertEquals(DatastoreTest.TIMESTAMP_VALUE, value4);
        LatLngValue value5 = entity.getValue("latLng");
        Assert.assertEquals(DatastoreTest.LAT_LNG_VALUE, value5);
        FullEntity<IncompleteKey> value6 = entity.getEntity("partial1");
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY1, value6);
        ListValue value7 = entity.getValue("emptyList");
        Assert.assertEquals(DatastoreTest.EMPTY_LIST_VALUE, value7);
        Assert.assertEquals(7, entity.getNames().size());
        Assert.assertFalse(entity.contains("bla"));
    }

    @Test
    public void testLookupEventualConsistency() {
        ReadOptions readOption = ReadOptions.newBuilder().setReadConsistencyValue(EVENTUAL_VALUE).build();
        com.google.datastore.v1.Key key = com.google.datastore.v1.Key.newBuilder().setPartitionId(PartitionId.newBuilder().setProjectId(DatastoreTest.PROJECT_ID).build()).addPath(com.google.datastore.v1.Key.newBuilder().setKind("kind1").setName("name").build()).build();
        LookupRequest lookupRequest = LookupRequest.newBuilder().setReadOptions(readOption).addKeys(key).build();
        EasyMock.expect(rpcMock.lookup(lookupRequest)).andReturn(LookupResponse.newBuilder().build()).times(3);
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        datastore.get(DatastoreTest.KEY1, ReadOption.eventualConsistency());
        datastore.get(ImmutableList.of(DatastoreTest.KEY1), ReadOption.eventualConsistency());
        datastore.fetch(ImmutableList.of(DatastoreTest.KEY1), ReadOption.eventualConsistency());
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testGetArrayNoDeferredResults() {
        DatastoreTest.datastore.put(DatastoreTest.ENTITY3);
        Iterator<Entity> result = DatastoreTest.datastore.fetch(DatastoreTest.KEY1, Key.newBuilder(DatastoreTest.KEY1).setName("bla").build(), DatastoreTest.KEY2, DatastoreTest.KEY3).iterator();
        Assert.assertEquals(DatastoreTest.ENTITY1, result.next());
        Assert.assertNull(result.next());
        Assert.assertEquals(DatastoreTest.ENTITY2, result.next());
        Entity entity3 = result.next();
        Assert.assertEquals(DatastoreTest.ENTITY3, entity3);
        Assert.assertTrue(entity3.isNull("null"));
        Assert.assertFalse(entity3.getBoolean("bool"));
        Assert.assertEquals(DatastoreTest.LIST_VALUE2.get(), entity3.getList("list"));
        FullEntity<IncompleteKey> partial1 = entity3.getEntity("partial1");
        FullEntity<IncompleteKey> partial2 = entity3.getEntity("partial2");
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2, partial1);
        Assert.assertEquals(DatastoreTest.ENTITY2, partial2);
        Assert.assertEquals(BOOLEAN, entity3.getValue("bool").getType());
        Assert.assertEquals(DatastoreTest.LAT_LNG_VALUE, entity3.getValue("latLng"));
        Assert.assertEquals(DatastoreTest.EMPTY_LIST_VALUE, entity3.getValue("emptyList"));
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
    public void testGetArrayDeferredResults() throws DatastoreException {
        Set<Key> requestedKeys = new HashSet<>();
        requestedKeys.add(DatastoreTest.KEY1);
        requestedKeys.add(DatastoreTest.KEY2);
        requestedKeys.add(DatastoreTest.KEY3);
        requestedKeys.add(DatastoreTest.KEY4);
        requestedKeys.add(DatastoreTest.KEY5);
        Iterator<Entity> iter = createDatastoreForDeferredLookup().get(DatastoreTest.KEY1, DatastoreTest.KEY2, DatastoreTest.KEY3, DatastoreTest.KEY4, DatastoreTest.KEY5);
        Set<Key> keysOfFoundEntities = new HashSet<>();
        while (iter.hasNext()) {
            keysOfFoundEntities.add(iter.next().getKey());
        } 
        Assert.assertEquals(requestedKeys, keysOfFoundEntities);
    }

    @Test
    public void testFetchArrayDeferredResults() throws DatastoreException {
        List<Entity> foundEntities = createDatastoreForDeferredLookup().fetch(DatastoreTest.KEY1, DatastoreTest.KEY2, DatastoreTest.KEY3, DatastoreTest.KEY4, DatastoreTest.KEY5);
        Assert.assertEquals(foundEntities.get(0).getKey(), DatastoreTest.KEY1);
        Assert.assertEquals(foundEntities.get(1).getKey(), DatastoreTest.KEY2);
        Assert.assertEquals(foundEntities.get(2).getKey(), DatastoreTest.KEY3);
        Assert.assertEquals(foundEntities.get(3).getKey(), DatastoreTest.KEY4);
        Assert.assertEquals(foundEntities.get(4).getKey(), DatastoreTest.KEY5);
        Assert.assertEquals(foundEntities.size(), 5);
    }

    @Test
    public void testAddEntity() {
        List<Entity> keys = DatastoreTest.datastore.fetch(DatastoreTest.ENTITY1.getKey(), DatastoreTest.ENTITY3.getKey());
        Assert.assertEquals(DatastoreTest.ENTITY1, keys.get(0));
        Assert.assertNull(keys.get(1));
        Assert.assertEquals(2, keys.size());
        try {
            DatastoreTest.datastore.add(DatastoreTest.ENTITY1);
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            // expected;
        }
        List<Entity> entities = DatastoreTest.datastore.add(DatastoreTest.ENTITY3, DatastoreTest.PARTIAL_ENTITY1, DatastoreTest.PARTIAL_ENTITY2);
        Assert.assertEquals(DatastoreTest.ENTITY3, DatastoreTest.datastore.get(DatastoreTest.ENTITY3.getKey()));
        Assert.assertEquals(DatastoreTest.ENTITY3, entities.get(0));
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY1.getProperties(), entities.get(1).getProperties());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY1.getKey().getAncestors(), entities.get(1).getKey().getAncestors());
        Assert.assertNotNull(DatastoreTest.datastore.get(entities.get(1).getKey()));
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getProperties(), entities.get(2).getProperties());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY2.getKey().getAncestors(), entities.get(2).getKey().getAncestors());
        Assert.assertNotNull(DatastoreTest.datastore.get(entities.get(2).getKey()));
    }

    @Test
    public void testUpdate() {
        List<Entity> keys = DatastoreTest.datastore.fetch(DatastoreTest.ENTITY1.getKey(), DatastoreTest.ENTITY3.getKey());
        Assert.assertEquals(DatastoreTest.ENTITY1, keys.get(0));
        Assert.assertNull(keys.get(1));
        Assert.assertEquals(2, keys.size());
        try {
            DatastoreTest.datastore.update(DatastoreTest.ENTITY3);
            Assert.fail("Expecting a failure");
        } catch (DatastoreException expected) {
            // expected;
        }
        DatastoreTest.datastore.add(DatastoreTest.ENTITY3);
        Assert.assertEquals(DatastoreTest.ENTITY3, DatastoreTest.datastore.get(DatastoreTest.ENTITY3.getKey()));
        Entity entity3 = Entity.newBuilder(DatastoreTest.ENTITY3).clear().set("bla", new NullValue()).build();
        Assert.assertNotEquals(DatastoreTest.ENTITY3, entity3);
        DatastoreTest.datastore.update(entity3);
        Assert.assertEquals(entity3, DatastoreTest.datastore.get(DatastoreTest.ENTITY3.getKey()));
    }

    @Test
    public void testPut() {
        Entity updatedEntity = Entity.newBuilder(DatastoreTest.ENTITY1).set("new_property", 42L).build();
        Assert.assertEquals(updatedEntity, DatastoreTest.datastore.put(updatedEntity));
        Assert.assertEquals(updatedEntity, DatastoreTest.datastore.get(updatedEntity.getKey()));
        Entity entity2 = Entity.newBuilder(DatastoreTest.ENTITY2).clear().set("bla", new NullValue()).build();
        Assert.assertNotEquals(DatastoreTest.ENTITY2, entity2);
        List<Entity> entities = DatastoreTest.datastore.put(DatastoreTest.ENTITY1, entity2, DatastoreTest.ENTITY3, DatastoreTest.PARTIAL_ENTITY1);
        Assert.assertEquals(DatastoreTest.ENTITY1, entities.get(0));
        Assert.assertEquals(entity2, entities.get(1));
        Assert.assertEquals(DatastoreTest.ENTITY3, entities.get(2));
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY1.getProperties(), entities.get(3).getProperties());
        Assert.assertEquals(DatastoreTest.PARTIAL_ENTITY1.getKey().getAncestors(), entities.get(3).getKey().getAncestors());
        Assert.assertEquals(DatastoreTest.ENTITY1, DatastoreTest.datastore.get(DatastoreTest.ENTITY1.getKey()));
        Assert.assertEquals(entity2, DatastoreTest.datastore.get(entity2.getKey()));
        Assert.assertEquals(DatastoreTest.ENTITY3, DatastoreTest.datastore.get(DatastoreTest.ENTITY3.getKey()));
        Entity entity = DatastoreTest.datastore.get(entities.get(3).getKey());
        Assert.assertEquals(entities.get(3), entity);
    }

    @Test
    public void testDelete() {
        Iterator<Entity> keys = DatastoreTest.datastore.fetch(DatastoreTest.ENTITY1.getKey(), DatastoreTest.ENTITY2.getKey(), DatastoreTest.ENTITY3.getKey()).iterator();
        Assert.assertEquals(DatastoreTest.ENTITY1, keys.next());
        Assert.assertEquals(DatastoreTest.ENTITY2, keys.next());
        Assert.assertNull(keys.next());
        Assert.assertFalse(keys.hasNext());
        DatastoreTest.datastore.delete(DatastoreTest.ENTITY1.getKey(), DatastoreTest.ENTITY2.getKey(), DatastoreTest.ENTITY3.getKey());
        keys = DatastoreTest.datastore.fetch(DatastoreTest.ENTITY1.getKey(), DatastoreTest.ENTITY2.getKey(), DatastoreTest.ENTITY3.getKey()).iterator();
        Assert.assertNull(keys.next());
        Assert.assertNull(keys.next());
        Assert.assertNull(keys.next());
        Assert.assertFalse(keys.hasNext());
    }

    @Test
    public void testKeyFactory() {
        KeyFactory keyFactory = DatastoreTest.datastore.newKeyFactory().setKind(DatastoreTest.KIND1);
        Assert.assertEquals(DatastoreTest.INCOMPLETE_KEY1, keyFactory.newKey());
        Assert.assertEquals(IncompleteKey.newBuilder(DatastoreTest.INCOMPLETE_KEY1).setKind(DatastoreTest.KIND2).build(), DatastoreTest.datastore.newKeyFactory().setKind(DatastoreTest.KIND2).newKey());
        Assert.assertEquals(DatastoreTest.KEY1, keyFactory.newKey("name"));
        Assert.assertEquals(Key.newBuilder(DatastoreTest.KEY1).setId(2).build(), keyFactory.newKey(2));
    }

    @Test
    public void testRetryableException() throws Exception {
        LookupRequest requestPb = LookupRequest.newBuilder().addKeys(DatastoreTest.KEY1.toPb()).build();
        LookupResponse responsePb = LookupResponse.newBuilder().addFound(EntityResult.newBuilder().setEntity(DatastoreTest.ENTITY1.toPb())).build();
        EasyMock.expect(rpcMock.lookup(requestPb)).andThrow(new DatastoreException(14, "UNAVAILABLE", "UNAVAILABLE", null)).andReturn(responsePb);
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        Entity entity = datastore.get(DatastoreTest.KEY1);
        Assert.assertEquals(DatastoreTest.ENTITY1, entity);
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testNonRetryableException() throws Exception {
        LookupRequest requestPb = LookupRequest.newBuilder().addKeys(DatastoreTest.KEY1.toPb()).build();
        EasyMock.expect(rpcMock.lookup(requestPb)).andThrow(new DatastoreException(UNKNOWN_CODE, "denied", "PERMISSION_DENIED")).times(1);
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        thrown.expect(DatastoreException.class);
        thrown.expectMessage("denied");
        datastore.get(DatastoreTest.KEY1);
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }

    @Test
    public void testRuntimeException() throws Exception {
        LookupRequest requestPb = LookupRequest.newBuilder().addKeys(DatastoreTest.KEY1.toPb()).build();
        String exceptionMessage = "Artificial runtime exception";
        EasyMock.expect(rpcMock.lookup(requestPb)).andThrow(new RuntimeException(exceptionMessage));
        EasyMock.replay(rpcFactoryMock, rpcMock);
        Datastore datastore = rpcMockOptions.getService();
        thrown.expect(DatastoreException.class);
        thrown.expectMessage(exceptionMessage);
        datastore.get(DatastoreTest.KEY1);
        EasyMock.verify(rpcFactoryMock, rpcMock);
    }
}

