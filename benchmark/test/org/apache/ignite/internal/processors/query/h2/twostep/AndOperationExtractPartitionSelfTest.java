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
 * Partition pruning tests for AND operation.
 */
public class AndOperationExtractPartitionSelfTest extends AbstractIndexingCommonTest {
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
        try (FieldsQueryCursor<List<?>> cur = AndOperationExtractPartitionSelfTest.orgCache.query(new SqlFieldsQuery(("SELECT * FROM Organization org WHERE org._KEY = 'org1' AND " + "org._KEY IN (SELECT subOrg._KEY FROM Organization subOrg)")))) {
            assertNotNull(cur);
            List<List<?>> rows = cur.getAll();
            assertEquals(1, rows.size());
        }
    }

    /**
     *
     */
    @Test
    public void testEmptyList() {
        testAndOperator(Collections.emptyList(), null, 0L, ((AndOperationExtractPartitionSelfTest.NODES_COUNT) - 1));
    }

    /**
     *
     */
    @Test
    public void testSingleValueList() {
        testAndOperator(Collections.singletonList(((JoinSqlTestHelper.ORG) + 0)), null, 0L, 0);
        testAndOperator(Collections.singletonList(((JoinSqlTestHelper.ORG) + 1)), null, 1L, 1);
        testAndOperator(Collections.singletonList("ORG"), null, 0L, 0);
        testAndOperator(Collections.singletonList("?"), new String[]{ (JoinSqlTestHelper.ORG) + 0 }, 0L, 0);
        testAndOperator(Collections.singletonList("?"), new String[]{ (JoinSqlTestHelper.ORG) + 2 }, 1L, 1);
        testAndOperator(Collections.singletonList("?"), new String[]{ "ORG" }, 0L, 0);
        testBothSidesParameterized(Collections.singletonList("?"), new String[]{ "ORG", (JoinSqlTestHelper.ORG) + 1 }, 0L, 0);
        testBothSidesParameterized(Collections.singletonList("?"), new String[]{ "org2", (JoinSqlTestHelper.ORG) + 1 }, 1L, 1);
    }

    /**
     *
     */
    @Test
    public void testMultipleValueList() {
        testAndOperator(Arrays.asList(((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 3), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))))), null, 1, 1);
        testAndOperator(Arrays.asList("ORG", ((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 4), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))))), null, 0, 0);
        testAndOperator(Arrays.asList(((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 2), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "ORG"), null, 1, 1);
        testAndOperator(Arrays.asList(((JoinSqlTestHelper.ORG) + 0), ((JoinSqlTestHelper.ORG) + 1), "MID", ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), ((JoinSqlTestHelper.ORG) + 3)), null, 2, 2);
        final List<String> allArgs3 = Arrays.asList("?", "?", "?");
        final List<String> allArgs4 = Arrays.asList("?", "?", "?", "?");
        testAndOperator(allArgs3, new String[]{ (JoinSqlTestHelper.ORG) + 0, (JoinSqlTestHelper.ORG) + 1, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))) }, 1, 1);
        testAndOperator(allArgs4, new String[]{ "ORG", (JoinSqlTestHelper.ORG) + 2, (JoinSqlTestHelper.ORG) + 8, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))) }, 1, 1);
        testAndOperator(allArgs4, new String[]{ (JoinSqlTestHelper.ORG) + 1, (JoinSqlTestHelper.ORG) + 3, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), "ORG" }, 2, 2);
        testAndOperator(allArgs4, new String[]{ (JoinSqlTestHelper.ORG) + 0, "MID", (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), "ORG" }, 0, 0);
        testAndOperator(Arrays.asList("?", ((JoinSqlTestHelper.ORG) + 2), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "?"), new String[]{ (JoinSqlTestHelper.ORG) + 1, "ORG" }, 2, 2);
        testAndOperator(Arrays.asList("?", ((JoinSqlTestHelper.ORG) + 9), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "?"), new String[]{ (JoinSqlTestHelper.ORG) + 1, "ORG" }, 1, 1);
        testAndOperator(Arrays.asList("?", "?", ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "ORG"), new String[]{ (JoinSqlTestHelper.ORG) + 0, "MID" }, 0, 0);
        testBothSidesParameterized(allArgs3, new String[]{ (JoinSqlTestHelper.ORG) + 0, (JoinSqlTestHelper.ORG) + 1, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), (JoinSqlTestHelper.ORG) + 1 }, 1, 1);
        testBothSidesParameterized(allArgs4, new String[]{ "ORG", (JoinSqlTestHelper.ORG) + 2, (JoinSqlTestHelper.ORG) + 8, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), (JoinSqlTestHelper.ORG) + 1 }, 1, 1);
        testBothSidesParameterized(allArgs4, new String[]{ (JoinSqlTestHelper.ORG) + 1, (JoinSqlTestHelper.ORG) + 3, (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), "ORG", (JoinSqlTestHelper.ORG) + 1 }, 2, 2);
        testBothSidesParameterized(allArgs4, new String[]{ (JoinSqlTestHelper.ORG) + 0, "MID", (JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1))), "ORG", (JoinSqlTestHelper.ORG) + 1 }, 0, 0);
        testAndOperator(Arrays.asList("?", ((JoinSqlTestHelper.ORG) + 10), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "?"), new String[]{ (JoinSqlTestHelper.ORG) + 1, (JoinSqlTestHelper.ORG) + 2 }, 2, 2);
        testBothSidesParameterized(Arrays.asList("?", ((JoinSqlTestHelper.ORG) + 9), ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "?"), new String[]{ (JoinSqlTestHelper.ORG) + 2, "ORG", (JoinSqlTestHelper.ORG) + 1 }, 1, 1);
        testBothSidesParameterized(Arrays.asList("?", "?", ((JoinSqlTestHelper.ORG) + (String.valueOf(((JoinSqlTestHelper.ORG_COUNT) - 1)))), "ORG"), new String[]{ (JoinSqlTestHelper.ORG) + 0, "MID", (JoinSqlTestHelper.ORG) + 1 }, 0, 0);
    }
}

