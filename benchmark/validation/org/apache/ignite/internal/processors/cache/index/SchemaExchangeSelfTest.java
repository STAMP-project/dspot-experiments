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


import QueryIndex.DFLT_INLINE_SIZE;
import org.apache.ignite.IgniteClientDisconnectedException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteClientReconnectAbstractTest;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.GridCacheAdapter;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.query.QueryUtils;
import org.apache.ignite.internal.util.lang.GridAbsPredicate;
import org.apache.ignite.internal.util.typedef.internal.CU;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Tests for schema exchange between nodes.
 */
public class SchemaExchangeSelfTest extends AbstractSchemaSelfTest {
    /**
     * Node on which filter should be applied (if any).
     */
    private static String filterNodeName;

    /**
     * Test propagation of empty query schema for static cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testEmptyStatic() throws Exception {
        checkEmpty(false);
    }

    /**
     * Test propagation of empty query schema for dynamic cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testEmptyDynamic() throws Exception {
        checkEmpty(true);
    }

    /**
     * Test propagation of non-empty query schema for static cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNonEmptyStatic() throws Exception {
        checkNonEmpty(false);
    }

    /**
     * Test propagation of non-empty query schema for dynamic cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNonEmptyDynamic() throws Exception {
        checkNonEmpty(true);
    }

    /**
     * Make sure that new metadata can be propagated after destroy.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDynamicRestarts() throws Exception {
        IgniteEx node1 = start(1, AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class);
        IgniteEx node2 = startNoCache(2);
        IgniteEx node3 = startClientNoCache(3);
        IgniteEx node4 = startClientNoCache(4);
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node1, node2);
        SchemaExchangeSelfTest.assertTypes(node3, AbstractSchemaSelfTest.ValueClass.class);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node3);
        node3.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node3);
        // Check restarts from the first node.
        destroySqlCache(node1);
        node1.getOrCreateCache(SchemaExchangeSelfTest.cacheConfiguration());
        SchemaExchangeSelfTest.assertTypes(node1);
        SchemaExchangeSelfTest.assertTypes(node2);
        SchemaExchangeSelfTest.assertTypes(node3);
        node1.destroyCache(AbstractSchemaSelfTest.CACHE_NAME);
        node1.getOrCreateCache(SchemaExchangeSelfTest.cacheConfiguration(AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.KeyClass2.class, SchemaExchangeSelfTest.ValueClass2.class));
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(node3, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node1, node2);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node3);
        node3.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node3);
        // Check restarts from the second node.
        node2.destroyCache(AbstractSchemaSelfTest.CACHE_NAME);
        node2.getOrCreateCache(SchemaExchangeSelfTest.cacheConfiguration());
        SchemaExchangeSelfTest.assertTypes(node1);
        SchemaExchangeSelfTest.assertTypes(node2);
        SchemaExchangeSelfTest.assertTypes(node3);
        node2.destroyCache(AbstractSchemaSelfTest.CACHE_NAME);
        node2.getOrCreateCache(SchemaExchangeSelfTest.cacheConfiguration(AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.KeyClass2.class, SchemaExchangeSelfTest.ValueClass2.class));
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(node3, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(node4, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node1, node2);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node3);
        node3.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node3);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node4);
        node4.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node4);
        // Make sure that joining node observes correct state.
        SchemaExchangeSelfTest.assertTypes(start(5), AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(startNoCache(6), AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(startClient(7), AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        IgniteEx node8 = startClientNoCache(8);
        SchemaExchangeSelfTest.assertTypes(node8, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node8);
        node8.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node8);
    }

    /**
     * Test client join for static cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientJoinStatic() throws Exception {
        checkClientJoin(false);
    }

    /**
     * Test client join for dynamic cache.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientJoinDynamic() throws Exception {
        checkClientJoin(true);
    }

    /**
     * Test client cache start (static).
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientCacheStartStatic() throws Exception {
        checkClientCacheStart(false);
    }

    /**
     * Test client cache start (dynamic).
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientCacheStartDynamic() throws Exception {
        checkClientCacheStart(true);
    }

    /**
     * Test behavior when node filter is set.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeFilter() throws Exception {
        SchemaExchangeSelfTest.filterNodeName = getTestIgniteInstanceName(1);
        IgniteEx node1 = start(1, AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class);
        IgniteEx node2 = start(2, AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class);
        IgniteEx node3 = startNoCache(3);
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node3, AbstractSchemaSelfTest.ValueClass.class);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node1, node2);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node3);
        node3.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node1, node3);
    }

    /**
     * Test client reconnect after server restart accompanied by schema change.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testServerRestartWithNewTypes() throws Exception {
        IgniteEx node1 = start(1, AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class);
        IgniteEx node2 = startClientNoCache(2);
        GridCacheContext<Object, Object> context0 = node2.context().cache().context().cacheContext(CU.cacheId(AbstractSchemaSelfTest.CACHE_NAME));
        node2.cache(AbstractSchemaSelfTest.CACHE_NAME);
        GridCacheContext<Object, Object> context = node2.context().cache().context().cacheContext(CU.cacheId(AbstractSchemaSelfTest.CACHE_NAME));
        GridCacheAdapter<Object, Object> entries = node2.context().cache().internalCache(AbstractSchemaSelfTest.CACHE_NAME);
        assertTrue(entries.active());
        node2.cache(AbstractSchemaSelfTest.CACHE_NAME);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class);
        stopGrid(1);
        assertTrue(GridTestUtils.waitForCondition(new GridAbsPredicate() {
            @Override
            public boolean apply() {
                return grid(2).context().clientDisconnected();
            }
        }, 10000L));
        IgniteFuture reconnFut = null;
        try {
            node2.cache(AbstractSchemaSelfTest.CACHE_NAME);
            fail();
        } catch (IgniteClientDisconnectedException e) {
            reconnFut = e.reconnectFuture();
        }
        node1 = start(1, AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.KeyClass2.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node1);
        reconnFut.get();
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class, SchemaExchangeSelfTest.ValueClass2.class);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node2);
        node2.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node2);
    }

    /**
     * Test client reconnect.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientReconnect() throws Exception {
        final IgniteEx node1 = start(1, AbstractSchemaSelfTest.KeyClass.class, AbstractSchemaSelfTest.ValueClass.class);
        SchemaExchangeSelfTest.assertTypes(node1, AbstractSchemaSelfTest.ValueClass.class);
        final IgniteEx node2 = startClientNoCache(2);
        SchemaExchangeSelfTest.assertTypes(node2, AbstractSchemaSelfTest.ValueClass.class);
        assertCacheNotStarted(AbstractSchemaSelfTest.CACHE_NAME, node2);
        node2.cache(AbstractSchemaSelfTest.CACHE_NAME);
        assertCacheStarted(AbstractSchemaSelfTest.CACHE_NAME, node2);
        IgniteClientReconnectAbstractTest.reconnectClientNode(log, node2, node1, new Runnable() {
            @Override
            public void run() {
                assertTrue(node2.context().clientDisconnected());
                final QueryIndex idx = AbstractSchemaSelfTest.index(AbstractSchemaSelfTest.IDX_NAME_1, AbstractSchemaSelfTest.field(AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED));
                try {
                    dynamicIndexCreate(node1, AbstractSchemaSelfTest.CACHE_NAME, AbstractSchemaSelfTest.TBL_NAME, idx, false, 0);
                } catch (Exception e) {
                    throw new IgniteException(e);
                }
            }
        });
        AbstractSchemaSelfTest.assertIndex(AbstractSchemaSelfTest.CACHE_NAME, QueryUtils.normalizeObjectName(AbstractSchemaSelfTest.TBL_NAME, true), QueryUtils.normalizeObjectName(AbstractSchemaSelfTest.IDX_NAME_1, false), DFLT_INLINE_SIZE, AbstractSchemaSelfTest.field(QueryUtils.normalizeObjectName(AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED, false)));
    }

    // TODO: Start/stop many nodes with static configs and dynamic start/stop.
    /**
     * Key class 2.
     */
    @SuppressWarnings("unused")
    private static class KeyClass2 {
        @QuerySqlField
        private String keyField2;
    }

    /**
     * Value class 2.
     */
    @SuppressWarnings("unused")
    private static class ValueClass2 {
        @QuerySqlField
        private String valField2;
    }
}

