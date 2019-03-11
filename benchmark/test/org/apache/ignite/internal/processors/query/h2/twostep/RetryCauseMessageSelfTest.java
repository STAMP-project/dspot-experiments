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
package org.apache.ignite.internal.processors.query.h2.twostep;


import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.cache.CacheException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.processors.cache.GridCacheContext;
import org.apache.ignite.internal.processors.cache.distributed.dht.GridReservable;
import org.apache.ignite.internal.processors.cache.distributed.dht.topology.GridDhtLocalPartition;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.query.h2.H2Utils;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.internal.processors.query.h2.twostep.msg.GridH2QueryRequest;
import org.apache.ignite.internal.util.GridSpinBusyLock;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test for 6 retry cases
 */
public class RetryCauseMessageSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final int NODES_COUNT = 2;

    /**
     *
     */
    private static final String ORG_SQL = "select * from Organization";

    /**
     *
     */
    private static final String ORG = "org";

    /**
     *
     */
    private IgniteCache<String, JoinSqlTestHelper.Person> personCache;

    /**
     *
     */
    private IgniteCache<String, JoinSqlTestHelper.Organization> orgCache;

    /**
     *
     */
    private IgniteH2Indexing h2Idx;

    /**
     * Failed to reserve partitions for query (cache is not found on local node)
     */
    @Test
    public void testSynthCacheWasNotFoundMessage() {
        GridMapQueryExecutor mapQryExec = GridTestUtils.getFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec");
        GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", new RetryCauseMessageSelfTest.MockGridMapQueryExecutor(null) {
            @Override
            public void onMessage(UUID nodeId, Object msg) {
                if (GridH2QueryRequest.class.isAssignableFrom(msg.getClass())) {
                    GridH2QueryRequest qryReq = ((GridH2QueryRequest) (msg));
                    qryReq.caches().add(Integer.MAX_VALUE);
                    startedExecutor.onMessage(nodeId, msg);
                    qryReq.caches().remove(((qryReq.caches().size()) - 1));
                } else
                    startedExecutor.onMessage(nodeId, msg);

            }
        }.insertRealExecutor(mapQryExec));
        SqlQuery<String, JoinSqlTestHelper.Person> qry = new SqlQuery<String, JoinSqlTestHelper.Person>(JoinSqlTestHelper.Person.class, JoinSqlTestHelper.JOIN_SQL).setArgs("Organization #0");
        qry.setDistributedJoins(true);
        try {
            personCache.query(qry).getAll();
        } catch (CacheException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Failed to reserve partitions for query (cache is not found on local node) ["));
            return;
        } finally {
            GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", mapQryExec);
        }
        fail();
    }

    /**
     * Failed to reserve partitions for query (group reservation failed)
     */
    @Test
    public void testGrpReservationFailureMessage() {
        final GridMapQueryExecutor mapQryExec = GridTestUtils.getFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec");
        final ConcurrentMap<PartitionReservationKey, GridReservable> reservations = RetryCauseMessageSelfTest.reservations(h2Idx);
        GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", new RetryCauseMessageSelfTest.MockGridMapQueryExecutor(null) {
            @Override
            public void onMessage(UUID nodeId, Object msg) {
                if (GridH2QueryRequest.class.isAssignableFrom(msg.getClass())) {
                    final PartitionReservationKey grpKey = new PartitionReservationKey(RetryCauseMessageSelfTest.ORG, null);
                    reservations.put(grpKey, new GridReservable() {
                        @Override
                        public boolean reserve() {
                            return false;
                        }

                        @Override
                        public void release() {
                        }
                    });
                }
                startedExecutor.onMessage(nodeId, msg);
            }
        }.insertRealExecutor(mapQryExec));
        SqlQuery<String, JoinSqlTestHelper.Person> qry = new SqlQuery<String, JoinSqlTestHelper.Person>(JoinSqlTestHelper.Person.class, JoinSqlTestHelper.JOIN_SQL).setArgs("Organization #0");
        qry.setDistributedJoins(true);
        try {
            personCache.query(qry).getAll();
        } catch (CacheException e) {
            assertTrue(e.getMessage().contains("Failed to reserve partitions for query (group reservation failed) ["));
            return;
        } finally {
            GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", mapQryExec);
        }
        fail();
    }

    /**
     * Failed to reserve partitions for query (partition of PARTITIONED cache cannot be reserved)
     */
    @Test
    public void testPartitionedCacheReserveFailureMessage() {
        GridMapQueryExecutor mapQryExec = GridTestUtils.getFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec");
        final GridKernalContext ctx = GridTestUtils.getFieldValue(mapQryExec, GridMapQueryExecutor.class, "ctx");
        GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", new RetryCauseMessageSelfTest.MockGridMapQueryExecutor(null) {
            @Override
            public void onMessage(UUID nodeId, Object msg) {
                if (GridH2QueryRequest.class.isAssignableFrom(msg.getClass())) {
                    GridH2QueryRequest qryReq = ((GridH2QueryRequest) (msg));
                    GridCacheContext<?, ?> cctx = ctx.cache().context().cacheContext(qryReq.caches().get(0));
                    GridDhtLocalPartition part = cctx.topology().localPartition(0, NONE, false);
                    AtomicLong aState = GridTestUtils.getFieldValue(part, GridDhtLocalPartition.class, "state");
                    long stateVal = aState.getAndSet(2);
                    startedExecutor.onMessage(nodeId, msg);
                    aState.getAndSet(stateVal);
                } else
                    startedExecutor.onMessage(nodeId, msg);

            }
        }.insertRealExecutor(mapQryExec));
        SqlQuery<String, JoinSqlTestHelper.Person> qry = new SqlQuery<String, JoinSqlTestHelper.Person>(JoinSqlTestHelper.Person.class, JoinSqlTestHelper.JOIN_SQL).setArgs("Organization #0");
        qry.setDistributedJoins(true);
        try {
            personCache.query(qry).getAll();
        } catch (CacheException e) {
            assertTrue(e.getMessage().contains(("Failed to reserve partitions for query (partition of PARTITIONED " + "cache is not found or not in OWNING state) ")));
            return;
        } finally {
            GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", mapQryExec);
        }
        fail();
    }

    /**
     * Failed to execute non-collocated query (will retry)
     */
    @Test
    public void testNonCollocatedFailureMessage() {
        final GridMapQueryExecutor mapQryExec = GridTestUtils.getFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec");
        final ConcurrentMap<PartitionReservationKey, GridReservable> reservations = RetryCauseMessageSelfTest.reservations(h2Idx);
        GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", new RetryCauseMessageSelfTest.MockGridMapQueryExecutor(null) {
            @Override
            public void onMessage(UUID nodeId, Object msg) {
                if (GridH2QueryRequest.class.isAssignableFrom(msg.getClass())) {
                    final PartitionReservationKey grpKey = new PartitionReservationKey(RetryCauseMessageSelfTest.ORG, null);
                    reservations.put(grpKey, new GridReservable() {
                        @Override
                        public boolean reserve() {
                            throw H2Utils.retryException("test retry exception");
                        }

                        @Override
                        public void release() {
                        }
                    });
                }
                startedExecutor.onMessage(nodeId, msg);
            }
        }.insertRealExecutor(mapQryExec));
        SqlQuery<String, JoinSqlTestHelper.Person> qry = new SqlQuery<String, JoinSqlTestHelper.Person>(JoinSqlTestHelper.Person.class, JoinSqlTestHelper.JOIN_SQL).setArgs("Organization #0");
        qry.setDistributedJoins(true);
        try {
            personCache.query(qry).getAll();
        } catch (CacheException e) {
            assertTrue(e.getMessage().contains("Failed to execute non-collocated query (will retry) ["));
            return;
        } finally {
            GridTestUtils.setFieldValue(h2Idx, IgniteH2Indexing.class, "mapQryExec", mapQryExec);
        }
        fail();
    }

    /**
     * Wrapper around @{GridMapQueryExecutor}
     */
    private abstract static class MockGridMapQueryExecutor extends GridMapQueryExecutor {
        /**
         * Wrapped executor
         */
        GridMapQueryExecutor startedExecutor;

        /**
         *
         *
         * @param startedExecutor
         * 		Started executor.
         * @return Mocked map query executor.
         */
        RetryCauseMessageSelfTest.MockGridMapQueryExecutor insertRealExecutor(GridMapQueryExecutor startedExecutor) {
            this.startedExecutor = startedExecutor;
            return this;
        }

        /**
         *
         *
         * @param busyLock
         * 		Busy lock.
         */
        MockGridMapQueryExecutor(GridSpinBusyLock busyLock) {
            super(busyLock);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void onMessage(UUID nodeId, Object msg) {
            startedExecutor.onMessage(nodeId, msg);
        }
    }
}

