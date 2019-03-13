/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.expression.reference.sys;


import DataTypes.BOOLEAN;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.STRING;
import RecoveryState.Stage.DONE;
import Reference.IndexType;
import RowGranularity.CLUSTER;
import RowGranularity.SHARD;
import SysShardsTableInfo.Columns;
import SysShardsTableInfo.Columns.ID;
import SysShardsTableInfo.IDENT;
import Version.LATEST;
import io.crate.expression.NestableInput;
import io.crate.expression.reference.ReferenceResolver;
import io.crate.expression.reference.sys.shard.ShardRecoveryStateExpression;
import io.crate.expression.reference.sys.shard.ShardRowContext;
import io.crate.metadata.IndexParts;
import io.crate.metadata.Reference;
import io.crate.metadata.RowGranularity;
import io.crate.metadata.Schemas;
import io.crate.metadata.shard.ShardReferenceResolver;
import io.crate.metadata.sys.SysShardsTableInfo;
import io.crate.metadata.table.ColumnPolicy;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.TestingHelpers;
import io.crate.types.IntegerType;
import io.crate.types.ObjectType;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.store.AlreadyClosedException;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.indices.recovery.RecoveryState;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("ConstantConditions")
public class SysShardsExpressionsTest extends CrateDummyClusterServiceUnitTest {
    private ReferenceResolver<?> resolver;

    private String indexName = "wikipedia_de";

    private IndexShard indexShard;

    private Schemas schemas;

    private String indexUUID;

    @Test
    public void testShardInfoLookup() throws Exception {
        Reference info = new Reference(new io.crate.metadata.ReferenceIdent(SysShardsTableInfo.IDENT, Columns.ID), RowGranularity.SHARD, IntegerType.INSTANCE, ColumnPolicy.STRICT, IndexType.NOT_ANALYZED, true);
        assertEquals(info, schemas.getTableInfo(IDENT).getReference(ID));
    }

    @Test
    public void testPathExpression() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.path", STRING, SHARD);
        NestableInput<String> shardPathExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertThat(shardPathExpression.value(), Matchers.is(TestingHelpers.resolveCanonicalString((("/dummy/" + (indexUUID)) + "/1"))));
    }

    @Test
    public void testId() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.id", INTEGER, SHARD);
        NestableInput<Integer> shardExpression = ((NestableInput<Integer>) (resolver.getImplementation(refInfo)));
        assertEquals(Integer.valueOf(1), shardExpression.value());
    }

    @Test
    public void testSize() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.size", LONG, SHARD);
        NestableInput<Long> shardExpression = ((NestableInput<Long>) (resolver.getImplementation(refInfo)));
        assertEquals(Long.valueOf(123456), shardExpression.value());
    }

    @Test
    public void testNumDocs() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.num_docs", LONG, SHARD);
        NestableInput<Long> shardExpression = ((NestableInput<Long>) (resolver.getImplementation(refInfo)));
        assertEquals(Long.valueOf(654321), shardExpression.value());
        // second call should throw Exception
        assertNull(shardExpression.value());
    }

    @Test
    public void testState() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.state", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("STARTED", shardExpression.value());
    }

    @Test
    public void testRoutingState() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.routing_state", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("RELOCATING", shardExpression.value());
    }

    @Test
    public void testPrimary() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.primary", BOOLEAN, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals(true, shardExpression.value());
    }

    @Test
    public void testRelocatingNode() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.relocating_node", STRING, CLUSTER);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("node_X", shardExpression.value());
    }

    @Test
    public void testTableName() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.table_name", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("wikipedia_de", shardExpression.value());
    }

    @Test
    public void testMinLuceneVersion() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.min_lucene_version", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals(LATEST.toString(), shardExpression.value());
        Mockito.doThrow(new AlreadyClosedException("Already closed")).when(indexShard).minimumCompatibleVersion();
        shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertThat(shardExpression.value(), Matchers.nullValue());
    }

    @Test
    public void testTableNameOfPartition() throws Exception {
        // expression should return the real table name
        indexName = IndexParts.toIndexName("doc", "wikipedia_de", "foo");
        prepare();
        Reference refInfo = TestingHelpers.refInfo("sys.shards.table_name", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("wikipedia_de", shardExpression.value());
        // reset indexName
        indexName = "wikipedia_de";
    }

    @Test
    public void testPartitionIdent() throws Exception {
        indexName = IndexParts.toIndexName("doc", "wikipedia_d1", "foo");
        prepare();
        Reference refInfo = TestingHelpers.refInfo("sys.shards.partition_ident", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("foo", shardExpression.value());
        // reset indexName
        indexName = "wikipedia_de";
    }

    @Test
    public void testPartitionIdentOfNonPartition() throws Exception {
        // expression should return NULL on non partitioned tables
        Reference refInfo = TestingHelpers.refInfo("sys.shards.partition_ident", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("", shardExpression.value());
    }

    @Test
    public void testOrphanPartition() throws Exception {
        indexName = IndexParts.toIndexName("doc", "wikipedia_d1", "foo");
        prepare();
        Reference refInfo = TestingHelpers.refInfo("sys.shards.orphan_partition", STRING, SHARD);
        NestableInput<Boolean> shardExpression = ((NestableInput<Boolean>) (resolver.getImplementation(refInfo)));
        assertEquals(true, shardExpression.value());
        // reset indexName
        indexName = "wikipedia_de";
    }

    @Test
    public void testSchemaName() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.schema_name", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("doc", shardExpression.value());
    }

    @Test
    public void testCustomSchemaName() throws Exception {
        indexName = "my_schema.wikipedia_de";
        prepare();
        Reference refInfo = TestingHelpers.refInfo("sys.shards.schema_name", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("my_schema", shardExpression.value());
        // reset indexName
        indexName = "wikipedia_de";
    }

    @Test
    public void testTableNameOfCustomSchema() throws Exception {
        // expression should return the real table name
        indexName = "my_schema.wikipedia_de";
        prepare();
        Reference refInfo = TestingHelpers.refInfo("sys.shards.table_name", STRING, SHARD);
        NestableInput<String> shardExpression = ((NestableInput<String>) (resolver.getImplementation(refInfo)));
        assertEquals("wikipedia_de", shardExpression.value());
        // reset indexName
        indexName = "wikipedia_de";
    }

    @Test
    public void testRecoveryShardField() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("sys.shards.recovery", ObjectType.untyped(), SHARD);
        NestableInput<Map<String, Object>> ref = ((NestableInput<Map<String, Object>>) (resolver.getImplementation(refInfo)));
        Map<String, Object> recovery = ref.value();
        assertEquals(DONE.name(), recovery.get("stage"));
        assertEquals(10000L, recovery.get("total_time"));
        Map<String, Object> expectedFiles = new HashMap<String, Object>() {
            {
                put("used", 2);
                put("reused", 1);
                put("recovered", 1);
                put("percent", 0.0F);
            }
        };
        assertEquals(expectedFiles, recovery.get("files"));
        Map<String, Object> expectedBytes = new HashMap<String, Object>() {
            {
                put("used", 2048L);
                put("reused", 1024L);
                put("recovered", 1024L);
                put("percent", 0.0F);
            }
        };
        assertEquals(expectedBytes, recovery.get("size"));
    }

    @Test
    public void testShardRecoveryStateExpressionNullRecoveryState() {
        Mockito.when(indexShard.recoveryState()).thenReturn(null);
        ShardRecoveryStateExpression shardRecoveryStateExpression = new ShardRecoveryStateExpression<Long>() {
            @Override
            public Long innerValue(RecoveryState recoveryState) {
                return recoveryState.getTimer().time();
            }
        };
        ShardRowContext shardRowContext = new ShardRowContext(indexShard, clusterService);
        shardRecoveryStateExpression.setNextRow(shardRowContext);
        assertNull(shardRecoveryStateExpression.value());
    }

    @Test
    public void testShardSizeExpressionWhenIndexShardHasBeenClosed() {
        IndexShard mock = mockIndexShard();
        Mockito.when(mock.storeStats()).thenReturn(null);
        ShardReferenceResolver resolver = new ShardReferenceResolver(schemas, new ShardRowContext(mock, clusterService));
        Reference refInfo = TestingHelpers.refInfo("sys.shards.size", LONG, SHARD);
        NestableInput<Long> shardSizeExpression = ((NestableInput<Long>) (resolver.getImplementation(refInfo)));
        assertThat(shardSizeExpression.value(), Matchers.is(0L));
    }
}

