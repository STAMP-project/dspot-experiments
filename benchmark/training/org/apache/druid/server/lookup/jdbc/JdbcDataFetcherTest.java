/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.lookup.jdbc;


import TestDerbyConnector.DerbyConnectorRule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.metadata.MetadataStorageConnectorConfig;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.server.lookup.DataFetcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.skife.jdbi.v2.Handle;


public class JdbcDataFetcherTest {
    @Rule
    public final DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    Handle handle;

    private JdbcDataFetcher jdbcDataFetcher;

    private final String tableName = "tableName";

    private final String keyColumn = "keyColumn";

    private final String valueColumn = "valueColumn";

    private static final Map<String, String> lookupMap = ImmutableMap.of("foo", "bar", "bad", "bar", "how about that", "foo", "empty string", "");

    @Test
    public void testFetch() {
        Assert.assertEquals("null check", null, jdbcDataFetcher.fetch("baz"));
        assertMapLookup(JdbcDataFetcherTest.lookupMap, jdbcDataFetcher);
    }

    @Test
    public void testFetchAll() {
        ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
        jdbcDataFetcher.fetchAll().forEach(mapBuilder::put);
        Assert.assertEquals("maps should match", JdbcDataFetcherTest.lookupMap, mapBuilder.build());
    }

    @Test
    public void testFetchKeys() {
        ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
        jdbcDataFetcher.fetch(JdbcDataFetcherTest.lookupMap.keySet()).forEach(mapBuilder::put);
        Assert.assertEquals(JdbcDataFetcherTest.lookupMap, mapBuilder.build());
    }

    @Test
    public void testReverseFetch() {
        Assert.assertEquals("reverse lookup should match", Sets.newHashSet("foo", "bad"), Sets.newHashSet(jdbcDataFetcher.reverseFetchKeys("bar")));
        Assert.assertEquals("reverse lookup should match", Sets.newHashSet("how about that"), Sets.newHashSet(jdbcDataFetcher.reverseFetchKeys("foo")));
        Assert.assertEquals("reverse lookup should match", Sets.newHashSet("empty string"), Sets.newHashSet(jdbcDataFetcher.reverseFetchKeys("")));
        Assert.assertEquals("reverse lookup of none existing value should be empty list", Collections.EMPTY_LIST, jdbcDataFetcher.reverseFetchKeys("does't exist"));
    }

    @Test
    public void testSerDesr() throws IOException {
        JdbcDataFetcher jdbcDataFetcher = new JdbcDataFetcher(new MetadataStorageConnectorConfig(), "table", "keyColumn", "ValueColumn", 100);
        DefaultObjectMapper mapper = new DefaultObjectMapper();
        String jdbcDataFetcherSer = mapper.writeValueAsString(jdbcDataFetcher);
        Assert.assertEquals(jdbcDataFetcher, mapper.readerFor(DataFetcher.class).readValue(jdbcDataFetcherSer));
    }
}

