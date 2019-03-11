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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 *
 */
public class InOperationExtractPartitionSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final int NODES_COUNT = 8;

    /**
     *
     */
    private static IgniteCache<String, JoinSqlTestHelper.Organization> orgCache;

    /**
     *
     */
    private static LongAdder cnt = new LongAdder();

    /**
     *
     */
    @Test
    public void testAlternativeUsageOfIn() {
        try (FieldsQueryCursor<List<?>> cur = InOperationExtractPartitionSelfTest.orgCache.query(new SqlFieldsQuery("SELECT * FROM Organization org WHERE org._KEY IN (SELECT subOrg._KEY FROM Organization subOrg)"))) {
            assertNotNull(cur);
            List<List<?>> rows = cur.getAll();
            assertEquals(JoinSqlTestHelper.ORG_COUNT, rows.size());
        }
    }

    /**
     *
     */
    @Test
    public void testEmptyList() {
        testInOperator(Collections.emptyList(), null, 0L, ((InOperationExtractPartitionSelfTest.NODES_COUNT) - 1));
    }

    /**
     *
     */
    @Test
    public void testSingleValueList() {
        testInOperator(Collections.singletonList(((JoinSqlTestHelper.ORG) + 0)), null, 1L, 1);
        testInOperator(Collections.singletonList(((JoinSqlTestHelper.ORG) + 1)), null, 1L, 1);
        testInOperator(Collections.singletonList(((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))))), null, 1L, 1);
        testInOperator(Collections.singletonList("ORG"), null, 0L, 1);
        testInOperator(Collections.singletonList("?"), new String[]{ (JoinSqlTestHelper.ORG) + 0 }, 1L, 1);
        testInOperator(Collections.singletonList("?"), new String[]{ (JoinSqlTestHelper.ORG) + 2 }, 1L, 1);
        testInOperator(Collections.singletonList("?"), new String[]{ (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))) }, 1L, 1);
        testInOperator(Collections.singletonList("?"), new String[]{ "ORG" }, 0L, 1);
    }

    /**
     *
     */
    @Test
    public void testMultipleValueList() {
        testInOperator(Arrays.asList(((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 3), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))))), null, 3, 3);
        testInOperator(Arrays.asList("ORG", ((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 4), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))))), null, 3, 4);
        testInOperator(Arrays.asList(((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 5), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "ORG"), null, 3, 4);
        testInOperator(Arrays.asList(((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 6), "MID", ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "ORG"), null, 3, 5);
        final List<String> allArgs3 = Arrays.asList("?", "?", "?");
        final List<String> allArgs4 = Arrays.asList("?", "?", "?", "?");
        testInOperator(allArgs3, new String[]{ (JoinSqlTestHelper.ORG) + 0, (JoinSqlTestHelper.ORG) + 7, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))) }, 3, 3);
        testInOperator(allArgs4, new String[]{ "ORG", (JoinSqlTestHelper.ORG) + 0, (JoinSqlTestHelper.ORG) + 8, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))) }, 3, 4);
        testInOperator(allArgs4, new String[]{ (JoinSqlTestHelper.ORG) + 0, (JoinSqlTestHelper.ORG) + 9, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), "ORG" }, 3, 4);
        testInOperator(allArgs4, new String[]{ (JoinSqlTestHelper.ORG) + 0, "MID", (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), "ORG" }, 2, 4);
        testInOperator(Arrays.asList("?", ((JoinSqlTestHelper.ORG) + 9), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "?"), new String[]{ (JoinSqlTestHelper.ORG) + 0, "ORG" }, 3, 4);
        testInOperator(Arrays.asList("?", "?", ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "ORG"), new String[]{ (JoinSqlTestHelper.ORG) + 0, "MID" }, 2, 4);
    }
}

