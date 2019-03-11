/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.dml.upsert;


import IndexMetaData.SETTING_VERSION_CREATED;
import Mapper.BuilderContext;
import ShardUpsertRequest.Item;
import Translog.Location;
import TransportWriteAction.WritePrimaryResult;
import Version.CURRENT;
import io.crate.Constants;
import io.crate.exceptions.InvalidColumnNameException;
import io.crate.execution.ddl.SchemaUpdateClient;
import io.crate.execution.dml.ShardResponse;
import io.crate.execution.dml.upsert.ShardUpsertRequest.DuplicateKeyAction;
import io.crate.execution.jobs.TasksService;
import io.crate.metadata.Functions;
import io.crate.metadata.Reference;
import io.crate.metadata.RelationName;
import io.crate.metadata.RowGranularity;
import io.crate.metadata.Schemas;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.types.DataTypes;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.elasticsearch.cluster.action.shard.ShardStateAction;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.mapper.ContentPath;
import org.elasticsearch.index.mapper.Mapper;
import org.elasticsearch.index.mapper.ObjectMapper;
import org.elasticsearch.index.mapper.SourceToParse;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TransportShardUpsertActionTest extends CrateDummyClusterServiceUnitTest {
    private static final RelationName TABLE_IDENT = new RelationName(Schemas.DOC_SCHEMA_NAME, "characters");

    private static final String PARTITION_INDEX = asIndexName();

    private static final Reference ID_REF = new Reference(new io.crate.metadata.ReferenceIdent(TransportShardUpsertActionTest.TABLE_IDENT, "id"), RowGranularity.DOC, DataTypes.SHORT);

    private String charactersIndexUUID;

    private String partitionIndexUUID;

    static class TestingTransportShardUpsertAction extends TransportShardUpsertAction {
        public TestingTransportShardUpsertAction(Settings settings, ThreadPool threadPool, ClusterService clusterService, TransportService transportService, SchemaUpdateClient schemaUpdateClient, TasksService tasksService, IndicesService indicesService, ShardStateAction shardStateAction, Functions functions, Schemas schemas, IndexNameExpressionResolver indexNameExpressionResolver) {
            super(settings, threadPool, clusterService, transportService, schemaUpdateClient, tasksService, indicesService, shardStateAction, functions, schemas, indexNameExpressionResolver);
        }

        @Nullable
        @Override
        protected Location indexItem(ShardUpsertRequest request, ShardUpsertRequest.Item item, IndexShard indexShard, boolean tryInsertFirst, UpdateSourceGen updateSourceGen, InsertSourceGen insertSourceGen, boolean isRetry) throws Exception {
            throw new VersionConflictEngineException(indexShard.shardId(), Constants.DEFAULT_MAPPING_TYPE, item.id(), (((("document with id: " + (item.id())) + " already exists in '") + (request.shardId().getIndexName())) + '\''));
        }
    }

    private TransportShardUpsertAction transportShardUpsertAction;

    private IndexShard indexShard;

    @Test
    public void testExceptionWhileProcessingItemsNotContinueOnError() throws Exception {
        ShardId shardId = new ShardId(TransportShardUpsertActionTest.TABLE_IDENT.indexNameOrAlias(), charactersIndexUUID, 0);
        ShardUpsertRequest request = new ShardUpsertRequest.Builder("dummyUser", "dummySchema", TimeValue.timeValueSeconds(30), DuplicateKeyAction.UPDATE_OR_FAIL, false, null, new Reference[]{ TransportShardUpsertActionTest.ID_REF }, UUID.randomUUID(), false).newRequest(shardId);
        request.add(1, new ShardUpsertRequest.Item("1", null, new Object[]{ 1 }, null));
        WritePrimaryResult<ShardUpsertRequest, ShardResponse> result = transportShardUpsertAction.processRequestItems(indexShard, request, new AtomicBoolean(false));
        assertThat(result.finalResponseIfSuccessful.failure(), Matchers.instanceOf(VersionConflictEngineException.class));
    }

    @Test
    public void testExceptionWhileProcessingItemsContinueOnError() throws Exception {
        ShardId shardId = new ShardId(TransportShardUpsertActionTest.TABLE_IDENT.indexNameOrAlias(), charactersIndexUUID, 0);
        ShardUpsertRequest request = new ShardUpsertRequest.Builder("dummyUser", "dummySchema", TimeValue.timeValueSeconds(30), DuplicateKeyAction.UPDATE_OR_FAIL, true, null, new Reference[]{ TransportShardUpsertActionTest.ID_REF }, UUID.randomUUID(), false).newRequest(shardId);
        request.add(1, new ShardUpsertRequest.Item("1", null, new Object[]{ 1 }, null));
        WritePrimaryResult<ShardUpsertRequest, ShardResponse> result = transportShardUpsertAction.processRequestItems(indexShard, request, new AtomicBoolean(false));
        ShardResponse response = result.finalResponseIfSuccessful;
        assertThat(response.failures().size(), Matchers.is(1));
        assertThat(response.failures().get(0).message(), Matchers.is("[default][1]: version conflict, document with id: 1 already exists in 'characters'"));
    }

    @Test
    public void testValidateMapping() throws Exception {
        // Create valid nested mapping with underscore.
        Settings settings = Settings.builder().put(SETTING_VERSION_CREATED, CURRENT).build();
        Mapper.BuilderContext builderContext = new Mapper.BuilderContext(settings, new ContentPath());
        Mapper outerMapper = new ObjectMapper.Builder("valid").add(new ObjectMapper.Builder("_invalid")).build(builderContext);
        TransportShardUpsertAction.validateMapping(Arrays.asList(outerMapper).iterator(), false);
        // Create invalid mapping
        expectedException.expect(InvalidColumnNameException.class);
        expectedException.expectMessage("system column pattern");
        outerMapper = new ObjectMapper.Builder("_invalid").build(builderContext);
        TransportShardUpsertAction.validateMapping(Arrays.asList(outerMapper).iterator(), false);
    }

    @Test
    public void testKilledSetWhileProcessingItemsDoesNotThrowException() throws Exception {
        ShardId shardId = new ShardId(TransportShardUpsertActionTest.TABLE_IDENT.indexNameOrAlias(), charactersIndexUUID, 0);
        ShardUpsertRequest request = new ShardUpsertRequest.Builder("dummyUser", "dummySchema", TimeValue.timeValueSeconds(30), DuplicateKeyAction.UPDATE_OR_FAIL, false, null, new Reference[]{ TransportShardUpsertActionTest.ID_REF }, UUID.randomUUID(), false).newRequest(shardId);
        request.add(1, new ShardUpsertRequest.Item("1", null, new Object[]{ 1 }, null));
        WritePrimaryResult<ShardUpsertRequest, ShardResponse> result = transportShardUpsertAction.processRequestItems(indexShard, request, new AtomicBoolean(true));
        assertThat(result.finalResponseIfSuccessful.failure(), Matchers.instanceOf(InterruptedException.class));
    }

    @Test
    public void testItemsWithoutSourceAreSkippedOnReplicaOperation() throws Exception {
        ShardId shardId = new ShardId(TransportShardUpsertActionTest.TABLE_IDENT.indexNameOrAlias(), charactersIndexUUID, 0);
        ShardUpsertRequest request = new ShardUpsertRequest.Builder("dummyUser", "dummySchema", TimeValue.timeValueSeconds(30), DuplicateKeyAction.UPDATE_OR_FAIL, false, null, new Reference[]{ TransportShardUpsertActionTest.ID_REF }, UUID.randomUUID(), false).newRequest(shardId);
        request.add(1, new ShardUpsertRequest.Item("1", null, new Object[]{ 1 }, null));
        Mockito.reset(indexShard);
        // would fail with NPE if not skipped
        transportShardUpsertAction.processRequestItemsOnReplica(indexShard, request);
        Mockito.verify(indexShard, Mockito.times(0)).applyIndexOperationOnReplica(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(VersionType.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(SourceToParse.class));
    }
}

