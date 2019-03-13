/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.merger.dql.iterator;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.merger.MergedResult;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.apache.shardingsphere.core.merger.dql.DQLMergeEngine;
import org.apache.shardingsphere.core.merger.fixture.TestQueryResult;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class IteratorStreamMergedResultTest {
    private DQLMergeEngine mergeEngine;

    private List<QueryResult> queryResults;

    private SelectStatement selectStatement;

    @Test
    public void assertNextForResultSetsAllEmpty() throws SQLException {
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextForResultSetsAllNotEmpty() throws SQLException {
        for (QueryResult each : queryResults) {
            Mockito.when(each.next()).thenReturn(true, false);
        }
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertTrue(actual.next());
        Assert.assertTrue(actual.next());
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextForFirstResultSetsNotEmptyOnly() throws SQLException {
        Mockito.when(queryResults.get(0).next()).thenReturn(true, false);
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextForMiddleResultSetsNotEmpty() throws SQLException {
        Mockito.when(queryResults.get(1).next()).thenReturn(true, false);
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextForLastResultSetsNotEmptyOnly() throws SQLException {
        Mockito.when(queryResults.get(2).next()).thenReturn(true, false);
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextForMix() throws SQLException {
        queryResults.add(new TestQueryResult(Mockito.mock(ResultSet.class)));
        queryResults.add(new TestQueryResult(Mockito.mock(ResultSet.class)));
        queryResults.add(new TestQueryResult(Mockito.mock(ResultSet.class)));
        Mockito.when(queryResults.get(1).next()).thenReturn(true, false);
        Mockito.when(queryResults.get(3).next()).thenReturn(true, false);
        Mockito.when(queryResults.get(5).next()).thenReturn(true, false);
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertTrue(actual.next());
        Assert.assertTrue(actual.next());
        Assert.assertFalse(actual.next());
    }
}

