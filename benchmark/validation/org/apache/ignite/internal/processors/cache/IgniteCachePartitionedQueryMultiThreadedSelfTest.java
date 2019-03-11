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
package org.apache.ignite.internal.processors.cache;


import Cache.Entry;
import CachePeekMode.ALL;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.util.tostring.GridToStringExclude;
import org.apache.ignite.internal.util.typedef.CAX;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for partitioned cache queries.
 */
public class IgniteCachePartitionedQueryMultiThreadedSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final boolean TEST_INFO = true;

    /**
     * Number of test grids (nodes). Should not be less than 2.
     */
    private static final int GRID_CNT = 3;

    /**
     * Don't start grid by default.
     */
    public IgniteCachePartitionedQueryMultiThreadedSelfTest() {
        super(false);
    }

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "TooBroadScope" })
    @Test
    public void testLuceneAndSqlMultithreaded() throws Exception {
        // ---------- Test parameters ---------- //
        int luceneThreads = 10;
        int sqlThreads = 10;
        long duration = 10 * 1000;
        final int logMod = 100;
        final IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj p1 = new IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj("Jon", 1500, "Master");
        final IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj p2 = new IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj("Jane", 2000, "Master");
        final IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj p3 = new IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj("Mike", 1800, "Bachelor");
        final IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj p4 = new IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj("Bob", 1900, "Bachelor");
        final IgniteCache<UUID, IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj> cache0 = grid(0).cache(DEFAULT_CACHE_NAME);
        cache0.put(p1.id(), p1);
        cache0.put(p2.id(), p2);
        cache0.put(p3.id(), p3);
        cache0.put(p4.id(), p4);
        assertEquals(4, cache0.localSize(ALL));
        assert (grid(0).cluster().nodes().size()) == (IgniteCachePartitionedQueryMultiThreadedSelfTest.GRID_CNT);
        final AtomicBoolean done = new AtomicBoolean();
        final AtomicLong luceneCnt = new AtomicLong();
        // Start lucene query threads.
        IgniteInternalFuture<?> futLucene = GridTestUtils.runMultiThreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                while (!(done.get())) {
                    QueryCursor<Entry<UUID, IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj>> master = cache0.query(new TextQuery(IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj.class, "Master"));
                    Collection<Entry<UUID, IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj>> entries = master.getAll();
                    checkResult(entries, p1, p2);
                    long cnt = luceneCnt.incrementAndGet();
                    if ((cnt % logMod) == 0)
                        info(("Executed LUCENE queries: " + cnt));

                } 
            }
        }, luceneThreads, "LUCENE-THREAD");
        final AtomicLong sqlCnt = new AtomicLong();
        // Start sql query threads.
        IgniteInternalFuture<?> futSql = GridTestUtils.runMultiThreadedAsync(new CAX() {
            @Override
            public void applyx() throws IgniteCheckedException {
                while (!(done.get())) {
                    QueryCursor<Entry<UUID, IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj>> bachelors = cache0.query(new SqlQuery(IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj.class, "degree = 'Bachelor'"));
                    Collection<Entry<UUID, IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj>> entries = bachelors.getAll();
                    checkResult(entries, p3, p4);
                    long cnt = sqlCnt.incrementAndGet();
                    if ((cnt % logMod) == 0)
                        info(("Executed SQL queries: " + cnt));

                } 
            }
        }, sqlThreads, "SQL-THREAD");
        Thread.sleep(duration);
        done.set(true);
        futLucene.get();
        futSql.get();
    }

    /**
     * Test class.
     */
    private static class PersonObj {
        /**
         *
         */
        @GridToStringExclude
        private UUID id = UUID.randomUUID();

        /**
         *
         */
        @QuerySqlField
        private String name;

        /**
         *
         */
        @QuerySqlField
        private int salary;

        /**
         *
         */
        @QuerySqlField
        @QueryTextField
        private String degree;

        /**
         * Required by {@link Externalizable}.
         */
        public PersonObj() {
            // No-op.
        }

        /**
         *
         *
         * @param name
         * 		Name.
         * @param salary
         * 		Salary.
         * @param degree
         * 		Degree.
         */
        PersonObj(String name, int salary, String degree) {
            assert name != null;
            assert salary > 0;
            assert degree != null;
            this.name = name;
            this.salary = salary;
            this.degree = degree;
        }

        /**
         *
         *
         * @return Id.
         */
        UUID id() {
            return id;
        }

        /**
         *
         *
         * @return Name.
         */
        String name() {
            return name;
        }

        /**
         *
         *
         * @return Salary.
         */
        double salary() {
            return salary;
        }

        /**
         *
         *
         * @return Degree.
         */
        String degree() {
            return degree;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((id.hashCode()) + (31 * (name.hashCode()))) + ((31 * 31) * (salary));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == (this))
                return true;

            if (!(obj instanceof IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj))
                return false;

            IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj that = ((IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj) (obj));
            return (((that.id.equals(id)) && (that.name.equals(name))) && ((that.salary) == (salary))) && (that.degree.equals(degree));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCachePartitionedQueryMultiThreadedSelfTest.PersonObj.class, this);
        }
    }
}

