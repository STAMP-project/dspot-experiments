/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache.index;


import IgniteQueryErrorCode.UNSUPPORTED_OPERATION;
import QueryIndex.DFLT_INLINE_SIZE;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryIndex;
import org.junit.Test;


/**
 * Tests for dynamic index creation.
 */
@SuppressWarnings({ "unchecked" })
public abstract class DynamicIndexAbstractBasicSelfTest extends DynamicIndexAbstractSelfTest {
    /**
     * Node index for regular server (coordinator).
     */
    protected static final int IDX_SRV_CRD = 0;

    /**
     * Node index for regular server (not coordinator).
     */
    protected static final int IDX_SRV_NON_CRD = 1;

    /**
     * Node index for regular client.
     */
    protected static final int IDX_CLI = 2;

    /**
     * Node index for server which doesn't pass node filter.
     */
    protected static final int IDX_SRV_FILTERED = 3;

    /**
     * Node index for client with near-only cache.
     */
    protected static final int IDX_CLI_NEAR_ONLY = 4;

    /**
     * Cache.
     */
    protected static final String STATIC_CACHE_NAME = "cache_static";

    /**
     * Test simple index create for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreatePartitionedAtomic() throws Exception {
        checkCreate(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test simple index create for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreatePartitionedAtomicNear() throws Exception {
        checkCreate(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test simple index create for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreatePartitionedTransactional() throws Exception {
        checkCreate(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test simple index create for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreatePartitionedTransactionalNear() throws Exception {
        checkCreate(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test simple index create for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateReplicatedAtomic() throws Exception {
        checkCreate(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test simple index create for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateReplicatedTransactional() throws Exception {
        checkCreate(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test composite index creation for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateCompositePartitionedAtomic() throws Exception {
        checkCreateComposite(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test composite index creation for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateCompositePartitionedAtomicNear() throws Exception {
        checkCreateComposite(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test composite index creation for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateCompositePartitionedTransactional() throws Exception {
        checkCreateComposite(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test composite index creation for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateCompositePartitionedTransactionalNear() throws Exception {
        checkCreateComposite(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test composite index creation for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateCompositeReplicatedAtomic() throws Exception {
        checkCreateComposite(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test composite index creation for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateCompositeReplicatedTransactional() throws Exception {
        checkCreateComposite(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test create when cache doesn't exist for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoCachePartitionedAtomic() throws Exception {
        checkCreateNotCache(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test create when cache doesn't exist for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoCachePartitionedAtomicNear() throws Exception {
        checkCreateNotCache(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test create when cache doesn't exist for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoCachePartitionedTransactional() throws Exception {
        checkCreateNotCache(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test create when cache doesn't exist for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoCachePartitionedTransactionalNear() throws Exception {
        checkCreateNotCache(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test create when cache doesn't exist for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoCacheReplicatedAtomic() throws Exception {
        checkCreateNotCache(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test create when cache doesn't exist for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoCacheReplicatedTransactional() throws Exception {
        checkCreateNotCache(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateNoTablePartitionedAtomic() throws Exception {
        checkCreateNoTable(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateNoTablePartitionedAtomicNear() throws Exception {
        checkCreateNoTable(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateNoTablePartitionedTransactional() throws Exception {
        checkCreateNoTable(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateNoTablePartitionedTransactionalNear() throws Exception {
        checkCreateNoTable(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test create when table doesn't exist for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateNoTableReplicatedAtomic() throws Exception {
        checkCreateNoTable(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test create when table doesn't exist for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateNoTableReplicatedTransactional() throws Exception {
        checkCreateNoTable(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoColumnPartitionedAtomic() throws Exception {
        checkCreateIndexNoColumn(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoColumnPartitionedAtomicNear() throws Exception {
        checkCreateIndexNoColumn(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoColumnPartitionedTransactional() throws Exception {
        checkCreateIndexNoColumn(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test create when table doesn't exist for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoColumnPartitionedTransactionalNear() throws Exception {
        checkCreateIndexNoColumn(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test create when table doesn't exist for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoColumnReplicatedAtomic() throws Exception {
        checkCreateIndexNoColumn(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test create when table doesn't exist for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexNoColumnReplicatedTransactional() throws Exception {
        checkCreateIndexNoColumn(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test index creation on aliased column for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexOnColumnWithAliasPartitionedAtomic() throws Exception {
        checkCreateIndexOnColumnWithAlias(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test index creation on aliased column for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexOnColumnWithAliasPartitionedAtomicNear() throws Exception {
        checkCreateIndexOnColumnWithAlias(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test index creation on aliased column for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexOnColumnWithAliasPartitionedTransactional() throws Exception {
        checkCreateIndexOnColumnWithAlias(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test index creation on aliased column for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateColumnWithAliasPartitionedTransactionalNear() throws Exception {
        checkCreateIndexOnColumnWithAlias(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test index creation on aliased column for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateColumnWithAliasReplicatedAtomic() throws Exception {
        checkCreateIndexOnColumnWithAlias(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test index creation on aliased column for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateColumnWithAliasReplicatedTransactional() throws Exception {
        checkCreateIndexOnColumnWithAlias(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Tests creating index with inline size for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithInlineSizePartitionedAtomic() throws Exception {
        checkCreateIndexWithInlineSize(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Tests creating index with inline size for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithInlineSizePartitionedAtomicNear() throws Exception {
        checkCreateIndexWithInlineSize(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Tests creating index with inline size for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithInlineSizePartitionedTransactional() throws Exception {
        checkCreateIndexWithInlineSize(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Tests creating index with inline size for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithInlineSizePartitionedTransactionalNear() throws Exception {
        checkCreateIndexWithInlineSize(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Tests creating index with inline size for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithInlineSizeReplicatedAtomic() throws Exception {
        checkCreateIndexWithInlineSize(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Tests creating index with inline size option for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithInlineSizeReplicatedTransactional() throws Exception {
        checkCreateIndexWithInlineSize(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Tests creating index with parallelism for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithParallelismPartitionedAtomic() throws Exception {
        checkCreateIndexWithParallelism(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Tests creating index with parallelism for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithParallelismPartitionedAtomicNear() throws Exception {
        checkCreateIndexWithParallelism(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Tests creating index with parallelism for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithParallelismPartitionedTransactional() throws Exception {
        checkCreateIndexWithParallelism(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Tests creating index with parallelism for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithParallelismPartitionedTransactionalNear() throws Exception {
        checkCreateIndexWithParallelism(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Tests creating index with parallelism for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithParallelismReplicatedAtomic() throws Exception {
        checkCreateIndexWithParallelism(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Tests creating index with parallelism option for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateIndexWithParallelismReplicatedTransactional() throws Exception {
        checkCreateIndexWithParallelism(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test simple index drop for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropPartitionedAtomic() throws Exception {
        checkDrop(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test simple index drop for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropPartitionedAtomicNear() throws Exception {
        checkDrop(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test simple index drop for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropPartitionedTransactional() throws Exception {
        checkDrop(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test simple index drop for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropPartitionedTransactionalNear() throws Exception {
        checkDrop(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test simple index drop for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropReplicatedAtomic() throws Exception {
        checkDrop(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test simple index drop for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropReplicatedTransactional() throws Exception {
        checkDrop(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test drop when there is no index for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoIndexPartitionedAtomic() throws Exception {
        checkDropNoIndex(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test drop when there is no index for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoIndexPartitionedAtomicNear() throws Exception {
        checkDropNoIndex(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test drop when there is no index for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoIndexPartitionedTransactional() throws Exception {
        checkDropNoIndex(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test drop when there is no index for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoIndexPartitionedTransactionalNear() throws Exception {
        checkDropNoIndex(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test drop when there is no index for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoIndexReplicatedAtomic() throws Exception {
        checkDropNoIndex(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test drop when there is no index for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoIndexReplicatedTransactional() throws Exception {
        checkDropNoIndex(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test drop when cache doesn't exist for PARTITIONED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoCachePartitionedAtomic() throws Exception {
        checkDropNoCache(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test drop when cache doesn't exist for PARTITIONED ATOMIC cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoCachePartitionedAtomicNear() throws Exception {
        checkDropNoCache(CacheMode.PARTITIONED, CacheAtomicityMode.ATOMIC, true);
    }

    /**
     * Test drop when cache doesn't exist for PARTITIONED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoCachePartitionedTransactional() throws Exception {
        checkDropNoCache(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test drop when cache doesn't exist for PARTITIONED TRANSACTIONAL cache with near cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoCachePartitionedTransactionalNear() throws Exception {
        checkDropNoCache(CacheMode.PARTITIONED, CacheAtomicityMode.TRANSACTIONAL, true);
    }

    /**
     * Test drop when cache doesn't exist for REPLICATED ATOMIC cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoCacheReplicatedAtomic() throws Exception {
        checkDropNoCache(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, false);
    }

    /**
     * Test drop when cache doesn't exist for REPLICATED TRANSACTIONAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDropNoCacheReplicatedTransactional() throws Exception {
        checkDropNoCache(CacheMode.REPLICATED, CacheAtomicityMode.TRANSACTIONAL, false);
    }

    /**
     * Test that operations fail on LOCAL cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFailOnLocalCache() throws Exception {
        for (Ignite node : Ignition.allGrids()) {
            if (!(node.configuration().isClientMode()))
                createSqlCache(node, localCacheConfiguration());

        }
        final QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED));
        DynamicIndexAbstractBasicSelfTest.assertIgniteSqlException(new AbstractSchemaSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                dynamicIndexCreate(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0);
            }
        }, UNSUPPORTED_OPERATION);
        AbstractSchemaSelfTest.assertNoIndex(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1);
        DynamicIndexAbstractBasicSelfTest.assertIgniteSqlException(new AbstractSchemaSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                dynamicIndexDrop(AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_LOCAL, true);
            }
        }, UNSUPPORTED_OPERATION);
    }

    /**
     * Test that operations work on statically configured cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNonSqlCache() throws Exception {
        final QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_2, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1));
        dynamicIndexCreate(DynamicIndexAbstractBasicSelfTest.STATIC_CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, true, 0);
        AbstractSchemaSelfTest.assertIndex(DynamicIndexAbstractBasicSelfTest.STATIC_CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1, DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED));
        dynamicIndexDrop(DynamicIndexAbstractBasicSelfTest.STATIC_CACHE_NAME, AbstractSchemaSelfTest.IDX_NAME_1, true);
        AbstractSchemaSelfTest.assertNoIndex(DynamicIndexAbstractBasicSelfTest.STATIC_CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, AbstractSchemaSelfTest.IDX_NAME_1);
    }

    /**
     * Test behavior depending on index name case sensitivity.
     */
    @Test
    public void testIndexNameCaseSensitivity() throws Exception {
        doTestIndexNameCaseSensitivity("myIdx", false);
        doTestIndexNameCaseSensitivity("myIdx", true);
    }
}

