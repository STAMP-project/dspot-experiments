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
package org.apache.shardingsphere.core.merger.dql.groupby;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.merger.MergedResult;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.apache.shardingsphere.core.merger.dql.DQLMergeEngine;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class GroupByMemoryMergedResultTest {
    private DQLMergeEngine mergeEngine;

    private List<ResultSet> resultSets;

    private List<QueryResult> queryResults;

    private SelectStatement selectStatement;

    @Test
    public void assertNextForResultSetsAllEmpty() throws SQLException {
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextForSomeResultSetsEmpty() throws SQLException {
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        Mockito.when(resultSets.get(0).next()).thenReturn(true, false);
        Mockito.when(resultSets.get(0).getObject(1)).thenReturn(20);
        Mockito.when(resultSets.get(0).getObject(2)).thenReturn(0);
        Mockito.when(resultSets.get(0).getObject(3)).thenReturn(2);
        Mockito.when(resultSets.get(0).getObject(4)).thenReturn(2);
        Mockito.when(resultSets.get(0).getObject(5)).thenReturn(20);
        Mockito.when(resultSets.get(2).next()).thenReturn(true, true, false);
        Mockito.when(resultSets.get(2).getObject(1)).thenReturn(20, 30);
        Mockito.when(resultSets.get(2).getObject(2)).thenReturn(0);
        Mockito.when(resultSets.get(2).getObject(3)).thenReturn(2, 3);
        Mockito.when(resultSets.get(2).getObject(4)).thenReturn(2, 2, 3);
        Mockito.when(resultSets.get(2).getObject(5)).thenReturn(20, 20, 30);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertThat(((BigDecimal) (actual.getValue(1, Object.class))), CoreMatchers.is(new BigDecimal(30)));
        Assert.assertThat(((BigDecimal) (actual.getValue(2, Object.class))).intValue(), CoreMatchers.is(10));
        Assert.assertThat(((Integer) (actual.getValue(3, Object.class))), CoreMatchers.is(3));
        Assert.assertThat(((BigDecimal) (actual.getValue(4, Object.class))), CoreMatchers.is(new BigDecimal(3)));
        Assert.assertThat(((BigDecimal) (actual.getValue(5, Object.class))), CoreMatchers.is(new BigDecimal(30)));
        Assert.assertTrue(actual.next());
        Assert.assertThat(((BigDecimal) (actual.getValue(1, Object.class))), CoreMatchers.is(new BigDecimal(40)));
        Assert.assertThat(((BigDecimal) (actual.getValue(2, Object.class))).intValue(), CoreMatchers.is(10));
        Assert.assertThat(((Integer) (actual.getValue(3, Object.class))), CoreMatchers.is(2));
        Assert.assertThat(((BigDecimal) (actual.getValue(4, Object.class))), CoreMatchers.is(new BigDecimal(4)));
        Assert.assertThat(((BigDecimal) (actual.getValue(5, Object.class))), CoreMatchers.is(new BigDecimal(40)));
        Assert.assertFalse(actual.next());
    }
}

