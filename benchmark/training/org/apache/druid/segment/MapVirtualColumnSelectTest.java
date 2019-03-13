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
package org.apache.druid.segment;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.select.SelectQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class MapVirtualColumnSelectTest {
    private final QueryRunner runner;

    public MapVirtualColumnSelectTest(QueryRunner runner) {
        this.runner = runner;
    }

    @Test
    public void testSerde() throws IOException {
        final ObjectMapper mapper = new DefaultObjectMapper();
        new DruidVirtualColumnsModule().getJacksonModules().forEach(mapper::registerModule);
        final MapVirtualColumn column = new MapVirtualColumn("keys", "values", "params");
        final String json = mapper.writeValueAsString(column);
        final VirtualColumn fromJson = mapper.readValue(json, VirtualColumn.class);
        Assert.assertEquals(column, fromJson);
    }

    @Test
    public void testBasic() {
        Druids.SelectQueryBuilder builder = testBuilder();
        List<Map> expectedResults = Arrays.asList(MapVirtualColumnTestBase.mapOf("dim", "a", "params.key1", "value1", "params.key3", "value3", "params.key5", null, "params", MapVirtualColumnTestBase.mapOf("key1", "value1", "key2", "value2", "key3", "value3")), MapVirtualColumnTestBase.mapOf("dim", "b", "params.key1", null, "params.key3", null, "params.key5", null, "params", MapVirtualColumnTestBase.mapOf("key4", "value4")), MapVirtualColumnTestBase.mapOf("dim", "c", "params.key1", "value1", "params.key3", null, "params.key5", "value5", "params", MapVirtualColumnTestBase.mapOf("key1", "value1", "key5", "value5")));
        List<VirtualColumn> virtualColumns = Collections.singletonList(new MapVirtualColumn("keys", "values", "params"));
        SelectQuery selectQuery = builder.dimensions(Collections.singletonList("dim")).metrics(Arrays.asList("params.key1", "params.key3", "params.key5", "params")).virtualColumns(virtualColumns).build();
        checkSelectQuery(selectQuery, expectedResults);
    }
}

