/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.gcp.datastore;


import CloningBehavior.DO_NOT_CLONE;
import CommitRequest.Mode.NON_TRANSACTIONAL;
import DatastoreV1.DATASTORE_BATCH_UPDATE_ENTITIES_LIMIT;
import DatastoreV1.DATASTORE_BATCH_UPDATE_ENTITIES_MIN;
import DatastoreV1.DATASTORE_BATCH_UPDATE_ENTITIES_START;
import DatastoreV1.Read;
import DatastoreV1.WriteBatcher;
import Query.Builder;
import com.google.datastore.v1.CommitRequest;
import com.google.datastore.v1.CommitResponse;
import com.google.datastore.v1.Entity;
import com.google.datastore.v1.GqlQuery;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Mutation;
import com.google.datastore.v1.PartitionId;
import com.google.datastore.v1.Query;
import com.google.datastore.v1.RunQueryRequest;
import com.google.datastore.v1.RunQueryResponse;
import com.google.datastore.v1.client.Datastore;
import com.google.datastore.v1.client.QuerySplitter;
import com.google.protobuf.Int32Value;
import com.google.rpc.Code;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.DatastoreWriterFn;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.DeleteEntity;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.DeleteEntityFn;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.DeleteKey;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.DeleteKeyFn;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.Read.ReadFn;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.Read.SplitQueryFn;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.Read.V1Options;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.UpsertFn;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.V1DatastoreFactory;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.Write;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataEvaluator;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static DatastoreV1.DATASTORE_BATCH_UPDATE_ENTITIES_START;
import static Read.DEFAULT_BUNDLE_SIZE_BYTES;
import static Read.QUERY_BATCH_LIMIT;
import static Read.getEstimatedSizeBytes;
import static Read.makeRequest;
import static Read.translateGqlQueryWithLimitCheck;


/**
 * Tests for {@link DatastoreV1}.
 */
@RunWith(JUnit4.class)
public class DatastoreV1Test {
    private static final String PROJECT_ID = "testProject";

    private static final String NAMESPACE = "testNamespace";

    private static final String KIND = "testKind";

    private static final Query QUERY;

    private static final String LOCALHOST = "localhost:9955";

    private static final String GQL_QUERY = "SELECT * from " + (DatastoreV1Test.KIND);

    private static final V1Options V_1_OPTIONS;

    static {
        Query.Builder q = Query.newBuilder();
        q.addKindBuilder().setName(DatastoreV1Test.KIND);
        QUERY = q.build();
        V_1_OPTIONS = V1Options.from(DatastoreV1Test.PROJECT_ID, DatastoreV1Test.NAMESPACE, null);
    }

    @Mock
    private Datastore mockDatastore;

    @Mock
    QuerySplitter mockQuerySplitter;

    @Mock
    V1DatastoreFactory mockDatastoreFactory;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBuildRead() throws Exception {
        DatastoreV1.Read read = DatastoreIO.v1().read().withProjectId(DatastoreV1Test.PROJECT_ID).withQuery(DatastoreV1Test.QUERY).withNamespace(DatastoreV1Test.NAMESPACE);
        Assert.assertEquals(DatastoreV1Test.QUERY, read.getQuery());
        Assert.assertEquals(DatastoreV1Test.PROJECT_ID, read.getProjectId().get());
        Assert.assertEquals(DatastoreV1Test.NAMESPACE, read.getNamespace().get());
    }

    @Test
    public void testBuildReadWithGqlQuery() throws Exception {
        DatastoreV1.Read read = DatastoreIO.v1().read().withProjectId(DatastoreV1Test.PROJECT_ID).withLiteralGqlQuery(DatastoreV1Test.GQL_QUERY).withNamespace(DatastoreV1Test.NAMESPACE);
        Assert.assertEquals(DatastoreV1Test.GQL_QUERY, read.getLiteralGqlQuery().get());
        Assert.assertEquals(DatastoreV1Test.PROJECT_ID, read.getProjectId().get());
        Assert.assertEquals(DatastoreV1Test.NAMESPACE, read.getNamespace().get());
    }

    /**
     * {@link #testBuildRead} but constructed in a different order.
     */
    @Test
    public void testBuildReadAlt() throws Exception {
        DatastoreV1.Read read = DatastoreIO.v1().read().withQuery(DatastoreV1Test.QUERY).withNamespace(DatastoreV1Test.NAMESPACE).withProjectId(DatastoreV1Test.PROJECT_ID).withLocalhost(DatastoreV1Test.LOCALHOST);
        Assert.assertEquals(DatastoreV1Test.QUERY, read.getQuery());
        Assert.assertEquals(DatastoreV1Test.PROJECT_ID, read.getProjectId().get());
        Assert.assertEquals(DatastoreV1Test.NAMESPACE, read.getNamespace().get());
        Assert.assertEquals(DatastoreV1Test.LOCALHOST, read.getLocalhost());
    }

    @Test
    public void testReadValidationFailsQueryAndGqlQuery() throws Exception {
        DatastoreV1.Read read = DatastoreIO.v1().read().withProjectId(DatastoreV1Test.PROJECT_ID).withLiteralGqlQuery(DatastoreV1Test.GQL_QUERY).withQuery(DatastoreV1Test.QUERY);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("withQuery() and withLiteralGqlQuery() are exclusive");
        read.expand(null);
    }

    @Test
    public void testReadValidationFailsQueryLimitZero() throws Exception {
        Query invalidLimit = Query.newBuilder().setLimit(Int32Value.newBuilder().setValue(0)).build();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid query limit 0: must be positive");
        DatastoreIO.v1().read().withQuery(invalidLimit);
    }

    @Test
    public void testReadValidationFailsQueryLimitNegative() throws Exception {
        Query invalidLimit = Query.newBuilder().setLimit(Int32Value.newBuilder().setValue((-5))).build();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid query limit -5: must be positive");
        DatastoreIO.v1().read().withQuery(invalidLimit);
    }

    @Test
    public void testReadDisplayData() {
        DatastoreV1.Read read = DatastoreIO.v1().read().withProjectId(DatastoreV1Test.PROJECT_ID).withQuery(DatastoreV1Test.QUERY).withNamespace(DatastoreV1Test.NAMESPACE);
        DisplayData displayData = DisplayData.from(read);
        Assert.assertThat(displayData, hasDisplayItem("projectId", DatastoreV1Test.PROJECT_ID));
        Assert.assertThat(displayData, hasDisplayItem("query", DatastoreV1Test.QUERY.toString()));
        Assert.assertThat(displayData, hasDisplayItem("namespace", DatastoreV1Test.NAMESPACE));
    }

    @Test
    public void testReadDisplayDataWithGqlQuery() {
        DatastoreV1.Read read = DatastoreIO.v1().read().withProjectId(DatastoreV1Test.PROJECT_ID).withLiteralGqlQuery(DatastoreV1Test.GQL_QUERY).withNamespace(DatastoreV1Test.NAMESPACE);
        DisplayData displayData = DisplayData.from(read);
        Assert.assertThat(displayData, hasDisplayItem("projectId", DatastoreV1Test.PROJECT_ID));
        Assert.assertThat(displayData, hasDisplayItem("gqlQuery", DatastoreV1Test.GQL_QUERY));
        Assert.assertThat(displayData, hasDisplayItem("namespace", DatastoreV1Test.NAMESPACE));
    }

    @Test
    public void testSourcePrimitiveDisplayData() {
        DisplayDataEvaluator evaluator = DisplayDataEvaluator.create();
        int numSplits = 98;
        PTransform<PBegin, PCollection<Entity>> read = DatastoreIO.v1().read().withProjectId(DatastoreV1Test.PROJECT_ID).withQuery(Query.newBuilder().build()).withNumQuerySplits(numSplits);
        String assertMessage = "DatastoreIO read should include the '%s' in its primitive display data";
        Set<DisplayData> displayData = evaluator.displayDataForPrimitiveSourceTransforms(read);
        Assert.assertThat(String.format(assertMessage, "project id"), displayData, Matchers.hasItem(hasDisplayItem("projectId", DatastoreV1Test.PROJECT_ID)));
        Assert.assertThat(String.format(assertMessage, "number of query splits"), displayData, Matchers.hasItem(hasDisplayItem("numQuerySplits", numSplits)));
    }

    @Test
    public void testWriteDisplayData() {
        Write write = DatastoreIO.v1().write().withProjectId(DatastoreV1Test.PROJECT_ID);
        DisplayData displayData = DisplayData.from(write);
        Assert.assertThat(displayData, hasDisplayItem("projectId", DatastoreV1Test.PROJECT_ID));
    }

    @Test
    public void testDeleteEntityDisplayData() {
        DeleteEntity deleteEntity = DatastoreIO.v1().deleteEntity().withProjectId(DatastoreV1Test.PROJECT_ID);
        DisplayData displayData = DisplayData.from(deleteEntity);
        Assert.assertThat(displayData, hasDisplayItem("projectId", DatastoreV1Test.PROJECT_ID));
    }

    @Test
    public void testDeleteKeyDisplayData() {
        DeleteKey deleteKey = DatastoreIO.v1().deleteKey().withProjectId(DatastoreV1Test.PROJECT_ID);
        DisplayData displayData = DisplayData.from(deleteKey);
        Assert.assertThat(displayData, hasDisplayItem("projectId", DatastoreV1Test.PROJECT_ID));
    }

    @Test
    public void testWritePrimitiveDisplayData() {
        DisplayDataEvaluator evaluator = DisplayDataEvaluator.create();
        PTransform<PCollection<Entity>, ?> write = DatastoreIO.v1().write().withProjectId("myProject");
        Set<DisplayData> displayData = evaluator.displayDataForPrimitiveTransforms(write);
        Assert.assertThat("DatastoreIO write should include the project in its primitive display data", displayData, Matchers.hasItem(hasDisplayItem("projectId")));
        Assert.assertThat("DatastoreIO write should include the upsertFn in its primitive display data", displayData, Matchers.hasItem(hasDisplayItem("upsertFn")));
    }

    @Test
    public void testDeleteEntityPrimitiveDisplayData() {
        DisplayDataEvaluator evaluator = DisplayDataEvaluator.create();
        PTransform<PCollection<Entity>, ?> write = DatastoreIO.v1().deleteEntity().withProjectId("myProject");
        Set<DisplayData> displayData = evaluator.displayDataForPrimitiveTransforms(write);
        Assert.assertThat("DatastoreIO write should include the project in its primitive display data", displayData, Matchers.hasItem(hasDisplayItem("projectId")));
        Assert.assertThat("DatastoreIO write should include the deleteEntityFn in its primitive display data", displayData, Matchers.hasItem(hasDisplayItem("deleteEntityFn")));
    }

    @Test
    public void testDeleteKeyPrimitiveDisplayData() {
        DisplayDataEvaluator evaluator = DisplayDataEvaluator.create();
        PTransform<PCollection<Key>, ?> write = DatastoreIO.v1().deleteKey().withProjectId("myProject");
        Set<DisplayData> displayData = evaluator.displayDataForPrimitiveTransforms(write);
        Assert.assertThat("DatastoreIO write should include the project in its primitive display data", displayData, Matchers.hasItem(hasDisplayItem("projectId")));
        Assert.assertThat("DatastoreIO write should include the deleteKeyFn in its primitive display data", displayData, Matchers.hasItem(hasDisplayItem("deleteKeyFn")));
    }

    /**
     * Test building a Write using builder methods.
     */
    @Test
    public void testBuildWrite() throws Exception {
        DatastoreV1.Write write = DatastoreIO.v1().write().withProjectId(DatastoreV1Test.PROJECT_ID);
        Assert.assertEquals(DatastoreV1Test.PROJECT_ID, write.getProjectId());
    }

    /**
     * Test the detection of complete and incomplete keys.
     */
    @Test
    public void testHasNameOrId() {
        Key key;
        // Complete with name, no ancestor
        key = makeKey("bird", "finch").build();
        Assert.assertTrue(DatastoreV1.isValidKey(key));
        // Complete with id, no ancestor
        key = makeKey("bird", 123).build();
        Assert.assertTrue(DatastoreV1.isValidKey(key));
        // Incomplete, no ancestor
        key = makeKey("bird").build();
        Assert.assertFalse(DatastoreV1.isValidKey(key));
        // Complete with name and ancestor
        key = makeKey("bird", "owl").build();
        key = makeKey(key, "bird", "horned").build();
        Assert.assertTrue(DatastoreV1.isValidKey(key));
        // Complete with id and ancestor
        key = makeKey("bird", "owl").build();
        key = makeKey(key, "bird", 123).build();
        Assert.assertTrue(DatastoreV1.isValidKey(key));
        // Incomplete with ancestor
        key = makeKey("bird", "owl").build();
        key = makeKey(key, "bird").build();
        Assert.assertFalse(DatastoreV1.isValidKey(key));
        key = makeKey().build();
        Assert.assertFalse(DatastoreV1.isValidKey(key));
    }

    /**
     * Test that entities with incomplete keys cannot be updated.
     */
    @Test
    public void testAddEntitiesWithIncompleteKeys() throws Exception {
        Key key = makeKey("bird").build();
        Entity entity = Entity.newBuilder().setKey(key).build();
        UpsertFn upsertFn = new UpsertFn();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Entities to be written to the Cloud Datastore must have complete keys");
        upsertFn.apply(entity);
    }

    /**
     * Test that entities with valid keys are transformed to upsert mutations.
     */
    @Test
    public void testAddEntities() throws Exception {
        Key key = makeKey("bird", "finch").build();
        Entity entity = Entity.newBuilder().setKey(key).build();
        UpsertFn upsertFn = new UpsertFn();
        Mutation exceptedMutation = makeUpsert(entity).build();
        Assert.assertEquals(upsertFn.apply(entity), exceptedMutation);
    }

    /**
     * Test that entities with incomplete keys cannot be deleted.
     */
    @Test
    public void testDeleteEntitiesWithIncompleteKeys() throws Exception {
        Key key = makeKey("bird").build();
        Entity entity = Entity.newBuilder().setKey(key).build();
        DeleteEntityFn deleteEntityFn = new DeleteEntityFn();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Entities to be deleted from the Cloud Datastore must have complete keys");
        deleteEntityFn.apply(entity);
    }

    /**
     * Test that entities with valid keys are transformed to delete mutations.
     */
    @Test
    public void testDeleteEntities() throws Exception {
        Key key = makeKey("bird", "finch").build();
        Entity entity = Entity.newBuilder().setKey(key).build();
        DeleteEntityFn deleteEntityFn = new DeleteEntityFn();
        Mutation exceptedMutation = makeDelete(entity.getKey()).build();
        Assert.assertEquals(deleteEntityFn.apply(entity), exceptedMutation);
    }

    /**
     * Test that incomplete keys cannot be deleted.
     */
    @Test
    public void testDeleteIncompleteKeys() throws Exception {
        Key key = makeKey("bird").build();
        DeleteKeyFn deleteKeyFn = new DeleteKeyFn();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Keys to be deleted from the Cloud Datastore must be complete");
        deleteKeyFn.apply(key);
    }

    /**
     * Test that valid keys are transformed to delete mutations.
     */
    @Test
    public void testDeleteKeys() {
        Key key = makeKey("bird", "finch").build();
        DeleteKeyFn deleteKeyFn = new DeleteKeyFn();
        Mutation exceptedMutation = makeDelete(key).build();
        Assert.assertEquals(deleteKeyFn.apply(key), exceptedMutation);
    }

    @Test
    public void testDatastoreWriteFnDisplayData() {
        DatastoreWriterFn datastoreWriter = new DatastoreWriterFn(DatastoreV1Test.PROJECT_ID, null);
        DisplayData displayData = DisplayData.from(datastoreWriter);
        Assert.assertThat(displayData, hasDisplayItem("projectId", DatastoreV1Test.PROJECT_ID));
    }

    /**
     * Tests {@link DatastoreWriterFn} with entities less than one batch.
     */
    @Test
    public void testDatatoreWriterFnWithOneBatch() throws Exception {
        datastoreWriterFnTest(100);
    }

    /**
     * Tests {@link DatastoreWriterFn} with entities of more than one batches, but not a multiple.
     */
    @Test
    public void testDatatoreWriterFnWithMultipleBatches() throws Exception {
        datastoreWriterFnTest((((DATASTORE_BATCH_UPDATE_ENTITIES_START) * 3) + 100));
    }

    /**
     * Tests {@link DatastoreWriterFn} with entities of several batches, using an exact multiple of
     * write batch size.
     */
    @Test
    public void testDatatoreWriterFnWithBatchesExactMultiple() throws Exception {
        datastoreWriterFnTest(((DATASTORE_BATCH_UPDATE_ENTITIES_START) * 2));
    }

    /**
     * Tests {@link DatastoreWriterFn} with large entities that need to be split into more batches.
     */
    @Test
    public void testDatatoreWriterFnWithLargeEntities() throws Exception {
        List<Mutation> mutations = new ArrayList<>();
        int entitySize = 0;
        for (int i = 0; i < 12; ++i) {
            Entity entity = Entity.newBuilder().setKey(makeKey(("key" + i), (i + 1))).putProperties("long", makeValue(new String(new char[900000])).setExcludeFromIndexes(true).build()).build();
            entitySize = entity.getSerializedSize();// Take the size of any one entity.

            mutations.add(makeUpsert(entity).build());
        }
        DatastoreWriterFn datastoreWriter = new DatastoreWriterFn(StaticValueProvider.of(DatastoreV1Test.PROJECT_ID), null, mockDatastoreFactory, new DatastoreV1Test.FakeWriteBatcher());
        DoFnTester<Mutation, Void> doFnTester = DoFnTester.of(datastoreWriter);
        doFnTester.setCloningBehavior(DO_NOT_CLONE);
        doFnTester.processBundle(mutations);
        // This test is over-specific currently; it requires that we split the 12 entity writes into 3
        // requests, but we only need each CommitRequest to be less than 10MB in size.
        int entitiesPerRpc = (DatastoreV1.DATASTORE_BATCH_UPDATE_BYTES_LIMIT) / entitySize;
        int start = 0;
        while (start < (mutations.size())) {
            int end = Math.min(mutations.size(), (start + entitiesPerRpc));
            CommitRequest.Builder commitRequest = CommitRequest.newBuilder();
            commitRequest.setMode(NON_TRANSACTIONAL);
            commitRequest.addAllMutations(mutations.subList(start, end));
            // Verify all the batch requests were made with the expected mutations.
            Mockito.verify(mockDatastore).commit(commitRequest.build());
            start = end;
        } 
    }

    /**
     * Tests {@link DatastoreWriterFn} with a failed request which is retried.
     */
    @Test
    public void testDatatoreWriterFnRetriesErrors() throws Exception {
        List<Mutation> mutations = new ArrayList<>();
        int numRpcs = 2;
        for (int i = 0; i < ((DATASTORE_BATCH_UPDATE_ENTITIES_START) * numRpcs); ++i) {
            mutations.add(makeUpsert(Entity.newBuilder().setKey(makeKey(("key" + i), (i + 1))).build()).build());
        }
        CommitResponse successfulCommit = CommitResponse.getDefaultInstance();
        Mockito.when(mockDatastore.commit(ArgumentMatchers.any(CommitRequest.class))).thenReturn(successfulCommit).thenThrow(new com.google.datastore.v1.client.DatastoreException("commit", Code.DEADLINE_EXCEEDED, "", null)).thenReturn(successfulCommit);
        DatastoreWriterFn datastoreWriter = new DatastoreWriterFn(StaticValueProvider.of(DatastoreV1Test.PROJECT_ID), null, mockDatastoreFactory, new DatastoreV1Test.FakeWriteBatcher());
        DoFnTester<Mutation, Void> doFnTester = DoFnTester.of(datastoreWriter);
        doFnTester.setCloningBehavior(DO_NOT_CLONE);
        doFnTester.processBundle(mutations);
    }

    /**
     * Tests {@link DatastoreV1.Read#getEstimatedSizeBytes} to fetch and return estimated size for a
     * query.
     */
    @Test
    public void testEstimatedSizeBytes() throws Exception {
        long entityBytes = 100L;
        // In seconds
        long timestamp = 1234L;
        RunQueryRequest latestTimestampRequest = Read.makeRequest(DatastoreV1Test.makeLatestTimestampQuery(DatastoreV1Test.NAMESPACE), DatastoreV1Test.NAMESPACE);
        RunQueryResponse latestTimestampResponse = DatastoreV1Test.makeLatestTimestampResponse(timestamp);
        // Per Kind statistics request and response
        RunQueryRequest statRequest = Read.makeRequest(DatastoreV1Test.makeStatKindQuery(DatastoreV1Test.NAMESPACE, timestamp), DatastoreV1Test.NAMESPACE);
        RunQueryResponse statResponse = DatastoreV1Test.makeStatKindResponse(entityBytes);
        Mockito.when(mockDatastore.runQuery(latestTimestampRequest)).thenReturn(latestTimestampResponse);
        Mockito.when(mockDatastore.runQuery(statRequest)).thenReturn(statResponse);
        Assert.assertEquals(entityBytes, getEstimatedSizeBytes(mockDatastore, DatastoreV1Test.QUERY, DatastoreV1Test.NAMESPACE));
        Mockito.verify(mockDatastore, Mockito.times(1)).runQuery(latestTimestampRequest);
        Mockito.verify(mockDatastore, Mockito.times(1)).runQuery(statRequest);
    }

    /**
     * Tests {@link SplitQueryFn} when number of query splits is specified.
     */
    @Test
    public void testSplitQueryFnWithNumSplits() throws Exception {
        int numSplits = 100;
        Mockito.when(mockQuerySplitter.getSplits(ArgumentMatchers.eq(DatastoreV1Test.QUERY), ArgumentMatchers.any(PartitionId.class), ArgumentMatchers.eq(numSplits), ArgumentMatchers.any(Datastore.class))).thenReturn(splitQuery(DatastoreV1Test.QUERY, numSplits));
        SplitQueryFn splitQueryFn = new SplitQueryFn(DatastoreV1Test.V_1_OPTIONS, numSplits, mockDatastoreFactory);
        DoFnTester<Query, Query> doFnTester = DoFnTester.of(splitQueryFn);
        /**
         * Although Datastore client is marked transient in {@link SplitQueryFn}, when injected through
         * mock factory using a when clause for unit testing purposes, it is not serializable because it
         * doesn't have a no-arg constructor. Thus disabling the cloning to prevent the doFn from being
         * serialized.
         */
        doFnTester.setCloningBehavior(DO_NOT_CLONE);
        List<Query> queries = doFnTester.processBundle(DatastoreV1Test.QUERY);
        Assert.assertEquals(queries.size(), numSplits);
        // Confirms that sub-queries are not equal to original when there is more than one split.
        for (Query subQuery : queries) {
            Assert.assertNotEquals(subQuery, DatastoreV1Test.QUERY);
        }
        Mockito.verify(mockQuerySplitter, Mockito.times(1)).getSplits(ArgumentMatchers.eq(DatastoreV1Test.QUERY), ArgumentMatchers.any(PartitionId.class), ArgumentMatchers.eq(numSplits), ArgumentMatchers.any(Datastore.class));
        Mockito.verifyZeroInteractions(mockDatastore);
    }

    /**
     * Tests {@link SplitQueryFn} when no query splits is specified.
     */
    @Test
    public void testSplitQueryFnWithoutNumSplits() throws Exception {
        // Force SplitQueryFn to compute the number of query splits
        int numSplits = 0;
        int expectedNumSplits = 20;
        long entityBytes = expectedNumSplits * (DEFAULT_BUNDLE_SIZE_BYTES);
        // In seconds
        long timestamp = 1234L;
        RunQueryRequest latestTimestampRequest = Read.makeRequest(DatastoreV1Test.makeLatestTimestampQuery(DatastoreV1Test.NAMESPACE), DatastoreV1Test.NAMESPACE);
        RunQueryResponse latestTimestampResponse = DatastoreV1Test.makeLatestTimestampResponse(timestamp);
        // Per Kind statistics request and response
        RunQueryRequest statRequest = Read.makeRequest(DatastoreV1Test.makeStatKindQuery(DatastoreV1Test.NAMESPACE, timestamp), DatastoreV1Test.NAMESPACE);
        RunQueryResponse statResponse = DatastoreV1Test.makeStatKindResponse(entityBytes);
        Mockito.when(mockDatastore.runQuery(latestTimestampRequest)).thenReturn(latestTimestampResponse);
        Mockito.when(mockDatastore.runQuery(statRequest)).thenReturn(statResponse);
        Mockito.when(mockQuerySplitter.getSplits(ArgumentMatchers.eq(DatastoreV1Test.QUERY), ArgumentMatchers.any(PartitionId.class), ArgumentMatchers.eq(expectedNumSplits), ArgumentMatchers.any(Datastore.class))).thenReturn(splitQuery(DatastoreV1Test.QUERY, expectedNumSplits));
        SplitQueryFn splitQueryFn = new SplitQueryFn(DatastoreV1Test.V_1_OPTIONS, numSplits, mockDatastoreFactory);
        DoFnTester<Query, Query> doFnTester = DoFnTester.of(splitQueryFn);
        doFnTester.setCloningBehavior(DO_NOT_CLONE);
        List<Query> queries = doFnTester.processBundle(DatastoreV1Test.QUERY);
        Assert.assertEquals(expectedNumSplits, queries.size());
        Mockito.verify(mockQuerySplitter, Mockito.times(1)).getSplits(ArgumentMatchers.eq(DatastoreV1Test.QUERY), ArgumentMatchers.any(PartitionId.class), ArgumentMatchers.eq(expectedNumSplits), ArgumentMatchers.any(Datastore.class));
        Mockito.verify(mockDatastore, Mockito.times(1)).runQuery(latestTimestampRequest);
        Mockito.verify(mockDatastore, Mockito.times(1)).runQuery(statRequest);
    }

    /**
     * Tests {@link DatastoreV1.Read.SplitQueryFn} when the query has a user specified limit.
     */
    @Test
    public void testSplitQueryFnWithQueryLimit() throws Exception {
        Query queryWithLimit = DatastoreV1Test.QUERY.toBuilder().setLimit(Int32Value.newBuilder().setValue(1)).build();
        SplitQueryFn splitQueryFn = new SplitQueryFn(DatastoreV1Test.V_1_OPTIONS, 10, mockDatastoreFactory);
        DoFnTester<Query, Query> doFnTester = DoFnTester.of(splitQueryFn);
        doFnTester.setCloningBehavior(DO_NOT_CLONE);
        List<Query> queries = doFnTester.processBundle(queryWithLimit);
        Assert.assertEquals(1, queries.size());
        Mockito.verifyNoMoreInteractions(mockDatastore);
        Mockito.verifyNoMoreInteractions(mockQuerySplitter);
    }

    /**
     * Tests {@link ReadFn} with a query limit less than one batch.
     */
    @Test
    public void testReadFnWithOneBatch() throws Exception {
        readFnTest(5);
    }

    /**
     * Tests {@link ReadFn} with a query limit more than one batch, and not a multiple.
     */
    @Test
    public void testReadFnWithMultipleBatches() throws Exception {
        readFnTest(((QUERY_BATCH_LIMIT) + 5));
    }

    /**
     * Tests {@link ReadFn} for several batches, using an exact multiple of batch size results.
     */
    @Test
    public void testReadFnWithBatchesExactMultiple() throws Exception {
        readFnTest((5 * (QUERY_BATCH_LIMIT)));
    }

    /**
     * Tests that {@link ReadFn} retries after an error.
     */
    @Test
    public void testReadFnRetriesErrors() throws Exception {
        // An empty query to read entities.
        Query query = Query.newBuilder().setLimit(Int32Value.newBuilder().setValue(1)).build();
        // Use mockResponseForQuery to generate results.
        Mockito.when(mockDatastore.runQuery(ArgumentMatchers.any(RunQueryRequest.class))).thenThrow(new com.google.datastore.v1.client.DatastoreException("RunQuery", Code.DEADLINE_EXCEEDED, "", null)).thenAnswer(( invocationOnMock) -> {
            Query q = ((RunQueryRequest) (invocationOnMock.getArguments()[0])).getQuery();
            return mockResponseForQuery(q);
        });
        ReadFn readFn = new ReadFn(DatastoreV1Test.V_1_OPTIONS, mockDatastoreFactory);
        DoFnTester<Query, Entity> doFnTester = DoFnTester.of(readFn);
        doFnTester.setCloningBehavior(DO_NOT_CLONE);
        doFnTester.processBundle(query);
    }

    @Test
    public void testTranslateGqlQueryWithLimit() throws Exception {
        String gql = "SELECT * from DummyKind LIMIT 10";
        String gqlWithZeroLimit = gql + " LIMIT 0";
        GqlQuery gqlQuery = GqlQuery.newBuilder().setQueryString(gql).setAllowLiterals(true).build();
        GqlQuery gqlQueryWithZeroLimit = GqlQuery.newBuilder().setQueryString(gqlWithZeroLimit).setAllowLiterals(true).build();
        RunQueryRequest gqlRequest = makeRequest(gqlQuery, DatastoreV1Test.V_1_OPTIONS.getNamespace());
        RunQueryRequest gqlRequestWithZeroLimit = makeRequest(gqlQueryWithZeroLimit, DatastoreV1Test.V_1_OPTIONS.getNamespace());
        Mockito.when(mockDatastore.runQuery(gqlRequestWithZeroLimit)).thenThrow(// dummy
        new com.google.datastore.v1.client.DatastoreException("runQuery", Code.INVALID_ARGUMENT, "invalid query", new RuntimeException()));
        Mockito.when(mockDatastore.runQuery(gqlRequest)).thenReturn(RunQueryResponse.newBuilder().setQuery(DatastoreV1Test.QUERY).build());
        Assert.assertEquals(translateGqlQueryWithLimitCheck(gql, mockDatastore, DatastoreV1Test.V_1_OPTIONS.getNamespace()), DatastoreV1Test.QUERY);
        Mockito.verify(mockDatastore, Mockito.times(1)).runQuery(gqlRequest);
        Mockito.verify(mockDatastore, Mockito.times(1)).runQuery(gqlRequestWithZeroLimit);
    }

    @Test
    public void testTranslateGqlQueryWithNoLimit() throws Exception {
        String gql = "SELECT * from DummyKind";
        String gqlWithZeroLimit = gql + " LIMIT 0";
        GqlQuery gqlQueryWithZeroLimit = GqlQuery.newBuilder().setQueryString(gqlWithZeroLimit).setAllowLiterals(true).build();
        RunQueryRequest gqlRequestWithZeroLimit = makeRequest(gqlQueryWithZeroLimit, DatastoreV1Test.V_1_OPTIONS.getNamespace());
        Mockito.when(mockDatastore.runQuery(gqlRequestWithZeroLimit)).thenReturn(RunQueryResponse.newBuilder().setQuery(DatastoreV1Test.QUERY).build());
        Assert.assertEquals(translateGqlQueryWithLimitCheck(gql, mockDatastore, DatastoreV1Test.V_1_OPTIONS.getNamespace()), DatastoreV1Test.QUERY);
        Mockito.verify(mockDatastore, Mockito.times(1)).runQuery(gqlRequestWithZeroLimit);
    }

    /**
     * Test options. *
     */
    public interface RuntimeTestOptions extends PipelineOptions {
        ValueProvider<String> getDatastoreProject();

        void setDatastoreProject(ValueProvider<String> value);

        ValueProvider<String> getGqlQuery();

        void setGqlQuery(ValueProvider<String> value);

        ValueProvider<String> getNamespace();

        void setNamespace(ValueProvider<String> value);
    }

    /**
     * Test to ensure that {@link ValueProvider} values are not accessed at pipeline construction time
     * when built with {@link DatastoreV1.Read#withQuery(Query)}.
     */
    @Test
    public void testRuntimeOptionsNotCalledInApplyQuery() {
        DatastoreV1Test.RuntimeTestOptions options = PipelineOptionsFactory.as(DatastoreV1Test.RuntimeTestOptions.class);
        Pipeline pipeline = TestPipeline.create(options);
        pipeline.apply(DatastoreIO.v1().read().withProjectId(options.getDatastoreProject()).withQuery(DatastoreV1Test.QUERY).withNamespace(options.getNamespace())).apply(DatastoreIO.v1().write().withProjectId(options.getDatastoreProject()));
    }

    /**
     * Test to ensure that {@link ValueProvider} values are not accessed at pipeline construction time
     * when built with {@link DatastoreV1.Read#withLiteralGqlQuery(String)}.
     */
    @Test
    public void testRuntimeOptionsNotCalledInApplyGqlQuery() {
        DatastoreV1Test.RuntimeTestOptions options = PipelineOptionsFactory.as(DatastoreV1Test.RuntimeTestOptions.class);
        Pipeline pipeline = TestPipeline.create(options);
        pipeline.apply(DatastoreIO.v1().read().withProjectId(options.getDatastoreProject()).withLiteralGqlQuery(options.getGqlQuery())).apply(DatastoreIO.v1().write().withProjectId(options.getDatastoreProject()));
    }

    @Test
    public void testWriteBatcherWithoutData() {
        DatastoreV1.WriteBatcher writeBatcher = new DatastoreV1.WriteBatcherImpl();
        writeBatcher.start();
        Assert.assertEquals(DATASTORE_BATCH_UPDATE_ENTITIES_START, writeBatcher.nextBatchSize(0));
    }

    @Test
    public void testWriteBatcherFastQueries() {
        DatastoreV1.WriteBatcher writeBatcher = new DatastoreV1.WriteBatcherImpl();
        writeBatcher.start();
        writeBatcher.addRequestLatency(0, 1000, 200);
        writeBatcher.addRequestLatency(0, 1000, 200);
        Assert.assertEquals(DATASTORE_BATCH_UPDATE_ENTITIES_LIMIT, writeBatcher.nextBatchSize(0));
    }

    @Test
    public void testWriteBatcherSlowQueries() {
        DatastoreV1.WriteBatcher writeBatcher = new DatastoreV1.WriteBatcherImpl();
        writeBatcher.start();
        writeBatcher.addRequestLatency(0, 10000, 200);
        writeBatcher.addRequestLatency(0, 10000, 200);
        Assert.assertEquals(100, writeBatcher.nextBatchSize(0));
    }

    @Test
    public void testWriteBatcherSizeNotBelowMinimum() {
        DatastoreV1.WriteBatcher writeBatcher = new DatastoreV1.WriteBatcherImpl();
        writeBatcher.start();
        writeBatcher.addRequestLatency(0, 30000, 50);
        writeBatcher.addRequestLatency(0, 30000, 50);
        Assert.assertEquals(DATASTORE_BATCH_UPDATE_ENTITIES_MIN, writeBatcher.nextBatchSize(0));
    }

    @Test
    public void testWriteBatcherSlidingWindow() {
        DatastoreV1.WriteBatcher writeBatcher = new DatastoreV1.WriteBatcherImpl();
        writeBatcher.start();
        writeBatcher.addRequestLatency(0, 30000, 50);
        writeBatcher.addRequestLatency(50000, 5000, 200);
        writeBatcher.addRequestLatency(100000, 5000, 200);
        Assert.assertEquals(200, writeBatcher.nextBatchSize(150000));
    }

    /**
     * A WriteBatcher for unit tests, which does no timing-based adjustments (so unit tests have
     * consistent results).
     */
    static class FakeWriteBatcher implements DatastoreV1.WriteBatcher {
        @Override
        public void start() {
        }

        @Override
        public void addRequestLatency(long timeSinceEpochMillis, long latencyMillis, int numMutations) {
        }

        @Override
        public int nextBatchSize(long timeSinceEpochMillis) {
            return DatastoreV1.DATASTORE_BATCH_UPDATE_ENTITIES_START;
        }
    }
}

