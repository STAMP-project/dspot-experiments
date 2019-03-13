/**
 * Copyright (c) 2015 YCSB contributors All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db;


import CassandraCQLClient.YCSB_KEY;
import Status.NOT_FOUND;
import Status.OK;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.collect.Sets;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Integration tests for the Cassandra client
 */
public class CassandraCQLClientTest {
    // Change the default Cassandra timeout from 10s to 120s for slow CI machines
    private static final long timeout = 120000L;

    private static final String TABLE = "usertable";

    private static final String HOST = "localhost";

    private static final int PORT = 9142;

    private static final String DEFAULT_ROW_KEY = "user1";

    private CassandraCQLClient client;

    private Session session;

    @ClassRule
    public static CassandraCQLUnit cassandraUnit = new CassandraCQLUnit(new ClassPathCQLDataSet("ycsb.cql", "ycsb"), null, CassandraCQLClientTest.timeout);

    @Test
    public void testReadMissingRow() throws Exception {
        final HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        final Status status = client.read(CassandraCQLClientTest.TABLE, "Missing row", null, result);
        MatcherAssert.assertThat(result.size(), Matchers.is(0));
        MatcherAssert.assertThat(status, Matchers.is(NOT_FOUND));
    }

    @Test
    public void testRead() throws Exception {
        insertRow();
        final HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        final Status status = client.read(CassandraCQLClientTest.TABLE, CassandraCQLClientTest.DEFAULT_ROW_KEY, null, result);
        MatcherAssert.assertThat(status, Matchers.is(OK));
        MatcherAssert.assertThat(result.entrySet(), Matchers.hasSize(11));
        MatcherAssert.assertThat(result, Matchers.hasEntry("field2", null));
        final HashMap<String, String> strResult = new HashMap<String, String>();
        for (final Map.Entry<String, ByteIterator> e : result.entrySet()) {
            if ((e.getValue()) != null) {
                strResult.put(e.getKey(), e.getValue().toString());
            }
        }
        MatcherAssert.assertThat(strResult, Matchers.hasEntry(YCSB_KEY, CassandraCQLClientTest.DEFAULT_ROW_KEY));
        MatcherAssert.assertThat(strResult, Matchers.hasEntry("field0", "value1"));
        MatcherAssert.assertThat(strResult, Matchers.hasEntry("field1", "value2"));
    }

    @Test
    public void testReadSingleColumn() throws Exception {
        insertRow();
        final HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
        final Set<String> fields = Sets.newHashSet("field1");
        final Status status = client.read(CassandraCQLClientTest.TABLE, CassandraCQLClientTest.DEFAULT_ROW_KEY, fields, result);
        MatcherAssert.assertThat(status, Matchers.is(OK));
        MatcherAssert.assertThat(result.entrySet(), Matchers.hasSize(1));
        final Map<String, String> strResult = StringByteIterator.getStringMap(result);
        MatcherAssert.assertThat(strResult, Matchers.hasEntry("field1", "value2"));
    }

    @Test
    public void testInsert() throws Exception {
        final String key = "key";
        final Map<String, String> input = new HashMap<String, String>();
        input.put("field0", "value1");
        input.put("field1", "value2");
        final Status status = client.insert(CassandraCQLClientTest.TABLE, key, StringByteIterator.getByteIteratorMap(input));
        MatcherAssert.assertThat(status, Matchers.is(OK));
        // Verify result
        final Select selectStmt = QueryBuilder.select("field0", "field1").from(CassandraCQLClientTest.TABLE).where(QueryBuilder.eq(YCSB_KEY, key)).limit(1);
        final ResultSet rs = session.execute(selectStmt);
        final Row row = rs.one();
        MatcherAssert.assertThat(row, Matchers.notNullValue());
        MatcherAssert.assertThat(rs.isExhausted(), Matchers.is(true));
        MatcherAssert.assertThat(row.getString("field0"), Matchers.is("value1"));
        MatcherAssert.assertThat(row.getString("field1"), Matchers.is("value2"));
    }

    @Test
    public void testUpdate() throws Exception {
        insertRow();
        final Map<String, String> input = new HashMap<String, String>();
        input.put("field0", "new-value1");
        input.put("field1", "new-value2");
        final Status status = client.update(CassandraCQLClientTest.TABLE, CassandraCQLClientTest.DEFAULT_ROW_KEY, StringByteIterator.getByteIteratorMap(input));
        MatcherAssert.assertThat(status, Matchers.is(OK));
        // Verify result
        final Select selectStmt = QueryBuilder.select("field0", "field1").from(CassandraCQLClientTest.TABLE).where(QueryBuilder.eq(YCSB_KEY, CassandraCQLClientTest.DEFAULT_ROW_KEY)).limit(1);
        final ResultSet rs = session.execute(selectStmt);
        final Row row = rs.one();
        MatcherAssert.assertThat(row, Matchers.notNullValue());
        MatcherAssert.assertThat(rs.isExhausted(), Matchers.is(true));
        MatcherAssert.assertThat(row.getString("field0"), Matchers.is("new-value1"));
        MatcherAssert.assertThat(row.getString("field1"), Matchers.is("new-value2"));
    }

    @Test
    public void testDelete() throws Exception {
        insertRow();
        final Status status = client.delete(CassandraCQLClientTest.TABLE, CassandraCQLClientTest.DEFAULT_ROW_KEY);
        MatcherAssert.assertThat(status, Matchers.is(OK));
        // Verify result
        final Select selectStmt = QueryBuilder.select("field0", "field1").from(CassandraCQLClientTest.TABLE).where(QueryBuilder.eq(YCSB_KEY, CassandraCQLClientTest.DEFAULT_ROW_KEY)).limit(1);
        final ResultSet rs = session.execute(selectStmt);
        final Row row = rs.one();
        MatcherAssert.assertThat(row, Matchers.nullValue());
    }

    @Test
    public void testPreparedStatements() throws Exception {
        final int LOOP_COUNT = 3;
        for (int i = 0; i < LOOP_COUNT; i++) {
            testInsert();
            testUpdate();
            testRead();
            testReadSingleColumn();
            testReadMissingRow();
            testDelete();
        }
    }
}

