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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.cache.CacheException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteClientReconnectAbstractTest;
import org.apache.ignite.internal.IgniteFutureTimeoutCheckedException;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.query.h2.twostep.messages.GridQueryNextPageResponse;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 *
 */
public class IgniteClientReconnectQueriesTest extends IgniteClientReconnectAbstractTest {
    /**
     *
     */
    public static final String QUERY_CACHE = "query";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryReconnect() throws Exception {
        Ignite cln = grid(serverCount());
        assertTrue(cln.cluster().localNode().isClient());
        final Ignite srv = clientRouter(cln);
        final IgniteCache<Integer, IgniteClientReconnectQueriesTest.Person> clnCache = cln.getOrCreateCache(IgniteClientReconnectQueriesTest.QUERY_CACHE);
        final IgniteCache<Integer, IgniteClientReconnectQueriesTest.Person> srvCache = srv.getOrCreateCache(IgniteClientReconnectQueriesTest.QUERY_CACHE);
        clnCache.put(1, new IgniteClientReconnectQueriesTest.Person(1, "name1", "surname1"));
        clnCache.put(2, new IgniteClientReconnectQueriesTest.Person(2, "name2", "surname2"));
        clnCache.put(3, new IgniteClientReconnectQueriesTest.Person(3, "name3", "surname3"));
        final SqlQuery<Integer, IgniteClientReconnectQueriesTest.Person> qry = new SqlQuery(IgniteClientReconnectQueriesTest.Person.class, "_key <> 0");
        qry.setPageSize(1);
        QueryCursor<Entry<Integer, IgniteClientReconnectQueriesTest.Person>> cur = clnCache.query(qry);
        reconnectClientNode(cln, srv, new Runnable() {
            @Override
            public void run() {
                srvCache.put(4, new IgniteClientReconnectQueriesTest.Person(4, "name4", "surname4"));
                try {
                    clnCache.query(qry);
                    fail();
                } catch (CacheException e) {
                    check(e);
                }
            }
        });
        List<Entry<Integer, IgniteClientReconnectQueriesTest.Person>> res = cur.getAll();
        assertNotNull(res);
        assertEquals(4, res.size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReconnectQueryInProgress() throws Exception {
        Ignite cln = grid(serverCount());
        assertTrue(cln.cluster().localNode().isClient());
        final Ignite srv = clientRouter(cln);
        final IgniteCache<Integer, IgniteClientReconnectQueriesTest.Person> clnCache = cln.getOrCreateCache(IgniteClientReconnectQueriesTest.QUERY_CACHE);
        clnCache.put(1, new IgniteClientReconnectQueriesTest.Person(1, "name1", "surname1"));
        clnCache.put(2, new IgniteClientReconnectQueriesTest.Person(2, "name2", "surname2"));
        clnCache.put(3, new IgniteClientReconnectQueriesTest.Person(3, "name3", "surname3"));
        blockMessage(GridQueryNextPageResponse.class);
        final SqlQuery<Integer, IgniteClientReconnectQueriesTest.Person> qry = new SqlQuery(IgniteClientReconnectQueriesTest.Person.class, "_key <> 0");
        qry.setPageSize(1);
        final QueryCursor<Entry<Integer, IgniteClientReconnectQueriesTest.Person>> cur1 = clnCache.query(qry);
        final IgniteInternalFuture<Object> fut = GridTestUtils.runAsync(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    cur1.getAll();
                } catch (CacheException e) {
                    checkAndWait(e);
                    return true;
                }
                return false;
            }
        });
        // Check that client waiting operation.
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return fut.get(200);
            }
        }, IgniteFutureTimeoutCheckedException.class, null);
        assertNotDone(fut);
        unblockMessage();
        reconnectClientNode(cln, srv, null);
        assertTrue(((Boolean) (fut.get(2, TimeUnit.SECONDS))));
        QueryCursor<Entry<Integer, IgniteClientReconnectQueriesTest.Person>> cur2 = clnCache.query(qry);
        assertEquals(3, cur2.getAll().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanQueryReconnect() throws Exception {
        Ignite cln = grid(serverCount());
        assertTrue(cln.cluster().localNode().isClient());
        final Ignite srv = clientRouter(cln);
        final IgniteCache<Integer, IgniteClientReconnectQueriesTest.Person> clnCache = cln.getOrCreateCache(IgniteClientReconnectQueriesTest.QUERY_CACHE);
        final IgniteCache<Integer, IgniteClientReconnectQueriesTest.Person> srvCache = srv.getOrCreateCache(IgniteClientReconnectQueriesTest.QUERY_CACHE);
        for (int i = 0; i < 10000; i++)
            clnCache.put(i, new IgniteClientReconnectQueriesTest.Person(i, ("name-" + i), ("surname-" + i)));

        final ScanQuery<Integer, IgniteClientReconnectQueriesTest.Person> scanQry = new ScanQuery();
        scanQry.setPageSize(1);
        scanQry.setFilter(new org.apache.ignite.lang.IgniteBiPredicate<Integer, IgniteClientReconnectQueriesTest.Person>() {
            @Override
            public boolean apply(Integer integer, IgniteClientReconnectQueriesTest.Person person) {
                return true;
            }
        });
        QueryCursor<Entry<Integer, IgniteClientReconnectQueriesTest.Person>> qryCursor = clnCache.query(scanQry);
        reconnectClientNode(cln, srv, new Runnable() {
            @Override
            public void run() {
                srvCache.put(10001, new IgniteClientReconnectQueriesTest.Person(10001, "name", "surname"));
                try {
                    clnCache.query(scanQry);
                    fail();
                } catch (CacheException e) {
                    check(e);
                }
            }
        });
        try {
            qryCursor.getAll();
            fail();
        } catch (CacheException e) {
            checkAndWait(e);
        }
        qryCursor = clnCache.query(scanQry);
        assertEquals(10001, qryCursor.getAll().size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanQueryReconnectInProgress1() throws Exception {
        scanQueryReconnectInProgress(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testScanQueryReconnectInProgress2() throws Exception {
        scanQueryReconnectInProgress(true);
    }

    /**
     *
     */
    public static class Person {
        /**
         *
         */
        @QuerySqlField
        public int id;

        /**
         *
         */
        @QuerySqlField
        public String name;

        /**
         *
         */
        @QuerySqlField
        public String surname;

        /**
         *
         *
         * @param id
         * 		Id.
         * @param name
         * 		Name.
         * @param surname
         * 		Surname.
         */
        public Person(int id, String name, String surname) {
            this.id = id;
            this.name = name;
            this.surname = surname;
        }

        /**
         *
         *
         * @return Id.
         */
        public int getId() {
            return id;
        }

        /**
         *
         *
         * @param id
         * 		Set id.
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         *
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         *
         * @return Surname.
         */
        public String getSurname() {
            return surname;
        }

        /**
         *
         *
         * @param surname
         * 		Surname.
         */
        public void setSurname(String surname) {
            this.surname = surname;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((!((o == null) || ((getClass()) != (o.getClass())))) && ((id) == (((IgniteClientReconnectQueriesTest.Person) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteClientReconnectQueriesTest.Person.class, this);
        }
    }
}

