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
package org.apache.ignite.client;


import Cache.Entry;
import Config.DEFAULT_CACHE_NAME;
import Config.SERVER;
import java.lang.invoke.SerializedLambda;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryTypeConfiguration;
import org.apache.ignite.cache.query.Query;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Thin client functional tests.
 */
public class FunctionalQueryTest {
    /**
     * Per test timeout
     */
    @Rule
    public Timeout globalTimeout = new Timeout(((int) (GridTestUtils.DFLT_TEST_TIMEOUT)));

    /**
     * Tested API:
     * <ul>
     * <li>{@link ClientCache#query(Query)}</li>
     * </ul>
     */
    @Test
    public void testQueries() throws Exception {
        IgniteConfiguration srvCfg = Config.getServerConfiguration();
        // No peer class loading from thin clients: we need the server to know about this class to deserialize
        // ScanQuery filter.
        srvCfg.setBinaryConfiguration(new BinaryConfiguration().setTypeConfigurations(Arrays.asList(new BinaryTypeConfiguration(getClass().getName()), new BinaryTypeConfiguration(SerializedLambda.class.getName()))));
        try (Ignite ignored = Ignition.start(srvCfg);IgniteClient client = Ignition.startClient(FunctionalQueryTest.getClientConfiguration())) {
            ClientCache<Integer, Person> cache = client.getOrCreateCache(DEFAULT_CACHE_NAME);
            Map<Integer, Person> data = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toMap(( i) -> i, ( i) -> new Person(i, String.format("Person %s", i))));
            cache.putAll(data);
            int minId = ((data.size()) / 2) + 1;
            int pageSize = ((data.size()) - minId) / 3;
            int expSize = ((data.size()) - minId) + 1;// expected query result size

            // Expected result
            Map<Integer, Person> exp = data.entrySet().stream().filter(( e) -> (e.getKey()) >= minId).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            // Scan and SQL queries
            Collection<Query<Entry<Integer, Person>>> queries = Arrays.asList(new org.apache.ignite.cache.query.ScanQuery<Integer, Person>(( i, p) -> (p.getId()) >= minId).setPageSize(pageSize), new org.apache.ignite.cache.query.SqlQuery<Integer, Person>(Person.class, "id >= ?").setArgs(minId).setPageSize(pageSize));
            for (Query<Entry<Integer, Person>> qry : queries) {
                try (QueryCursor<Entry<Integer, Person>> cur = cache.query(qry)) {
                    List<Entry<Integer, Person>> res = cur.getAll();
                    Assert.assertEquals(String.format("Unexpected number of rows from %s", qry.getClass().getSimpleName()), expSize, res.size());
                    Map<Integer, Person> act = res.stream().collect(Collectors.toMap(Cache.Entry::getKey, Cache.Entry::getValue));
                    Assert.assertEquals(String.format("unexpected rows from %s", qry.getClass().getSimpleName()), exp, act);
                }
            }
            checkSqlFieldsQuery(cache, minId, pageSize, expSize, exp, true);
            checkSqlFieldsQuery(cache, minId, pageSize, expSize, exp, false);
        }
    }

    /**
     * Tested API:
     * <ul>
     * <li>{@link IgniteClient#query(SqlFieldsQuery)}</li>
     * </ul>
     */
    @Test
    public void testSql() throws Exception {
        try (Ignite ignored = Ignition.start(Config.getServerConfiguration());Ignite ignored2 = Ignition.start(Config.getServerConfiguration());IgniteClient client = Ignition.startClient(new ClientConfiguration().setAddresses(SERVER))) {
            client.query(new SqlFieldsQuery(String.format("CREATE TABLE IF NOT EXISTS Person (id INT PRIMARY KEY, name VARCHAR) WITH \"VALUE_TYPE=%s\"", Person.class.getName())).setSchema("PUBLIC")).getAll();
            final int KEY_COUNT = 10;
            for (int i = 0; i < KEY_COUNT; ++i) {
                int key = i;
                Person val = new Person(key, ("Person " + i));
                client.query(new SqlFieldsQuery("INSERT INTO Person(id, name) VALUES(?, ?)").setArgs(val.getId(), val.getName()).setSchema("PUBLIC")).getAll();
            }
            Object cachedName = client.query(new SqlFieldsQuery("SELECT name from Person WHERE id=?").setArgs(1).setSchema("PUBLIC")).getAll().iterator().next().iterator().next();
            Assert.assertEquals("Person 1", cachedName);
            List<List<?>> rows = client.query(new SqlFieldsQuery("SELECT * from Person WHERE id >= ?").setSchema("PUBLIC").setArgs(0).setPageSize(1)).getAll();
            Assert.assertEquals(KEY_COUNT, rows.size());
        }
    }

    /**
     *
     */
    @Test
    public void testGettingEmptyResultWhenQueryingEmptyTable() throws Exception {
        try (Ignite ignored = Ignition.start(Config.getServerConfiguration());IgniteClient client = Ignition.startClient(new ClientConfiguration().setAddresses(SERVER))) {
            final String TBL = "Person";
            client.query(new SqlFieldsQuery(String.format((("CREATE TABLE IF NOT EXISTS " + TBL) + " (id INT PRIMARY KEY, name VARCHAR) WITH \"VALUE_TYPE=%s\""), Person.class.getName())).setSchema("PUBLIC")).getAll();
            // IgniteClient#query() API
            List<List<?>> res = client.query(new SqlFieldsQuery(("SELECT * FROM " + TBL))).getAll();
            Assert.assertNotNull(res);
            Assert.assertEquals(0, res.size());
            // ClientCache#query(SqlFieldsQuery) API
            ClientCache<Integer, Person> cache = client.cache(("SQL_PUBLIC_" + (TBL.toUpperCase())));
            res = cache.query(new SqlFieldsQuery(("SELECT * FROM " + TBL))).getAll();
            Assert.assertNotNull(res);
            Assert.assertEquals(0, res.size());
            // ClientCache#query(ScanQuery) and ClientCache#query(SqlQuery) API
            Collection<Query<Entry<Integer, Person>>> queries = Arrays.asList(new org.apache.ignite.cache.query.ScanQuery(), new org.apache.ignite.cache.query.SqlQuery(Person.class, "1 = 1"));
            for (Query<Entry<Integer, Person>> qry : queries) {
                try (QueryCursor<Entry<Integer, Person>> cur = cache.query(qry)) {
                    List<Entry<Integer, Person>> res2 = cur.getAll();
                    Assert.assertNotNull(res2);
                    Assert.assertEquals(0, res2.size());
                }
            }
        }
    }
}

