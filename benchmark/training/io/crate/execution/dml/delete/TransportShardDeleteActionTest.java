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
package io.crate.execution.dml.delete;


import TransportWriteAction.WritePrimaryResult;
import io.crate.execution.dml.ShardResponse;
import io.crate.metadata.RelationName;
import io.crate.metadata.Schemas;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.index.shard.ShardId;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TransportShardDeleteActionTest extends CrateDummyClusterServiceUnitTest {
    private static final RelationName TABLE_IDENT = new RelationName(Schemas.DOC_SCHEMA_NAME, "characters");

    private TransportShardDeleteAction transportShardDeleteAction;

    private IndexShard indexShard;

    private String indexUUID;

    @Test
    public void testKilledSetWhileProcessingItemsDoesNotThrowExceptionAndMustMarkItemPosition() throws Exception {
        ShardId shardId = new ShardId(TransportShardDeleteActionTest.TABLE_IDENT.indexNameOrAlias(), indexUUID, 0);
        final ShardDeleteRequest request = new ShardDeleteRequest(shardId, UUID.randomUUID());
        request.add(1, new ShardDeleteRequest.Item("1"));
        WritePrimaryResult<ShardDeleteRequest, ShardResponse> result = transportShardDeleteAction.processRequestItems(indexShard, request, new AtomicBoolean(true));
        assertThat(result.finalResponseIfSuccessful.failure(), Matchers.instanceOf(InterruptedException.class));
        assertThat(request.skipFromLocation(), Matchers.is(1));
    }

    @Test
    public void testReplicaOperationWillSkipItemsFromMarkedPositionOn() throws Exception {
        ShardId shardId = new ShardId(TransportShardDeleteActionTest.TABLE_IDENT.indexNameOrAlias(), indexUUID, 0);
        final ShardDeleteRequest request = new ShardDeleteRequest(shardId, UUID.randomUUID());
        request.add(1, new ShardDeleteRequest.Item("1"));
        request.skipFromLocation(1);
        // replica operation must skip all not by primary processed items
        transportShardDeleteAction.processRequestItemsOnReplica(indexShard, request);
        Mockito.verify(indexShard, Mockito.times(0)).applyDeleteOperationOnReplica(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(VersionType.class));
    }
}

