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
package org.apache.shardingsphere.core.merger.dql.pagination;


import java.sql.SQLException;
import java.util.List;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.merger.MergedResult;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.apache.shardingsphere.core.merger.dql.DQLMergeEngine;
import org.apache.shardingsphere.core.parsing.parser.context.limit.Limit;
import org.apache.shardingsphere.core.parsing.parser.context.limit.LimitValue;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.junit.Assert;
import org.junit.Test;


public final class TopAndRowNumberDecoratorMergedResultTest {
    private DQLMergeEngine mergeEngine;

    private List<QueryResult> queryResults;

    private SelectStatement selectStatement;

    @Test
    public void assertNextForSkipAll() throws SQLException {
        Limit limit = new Limit();
        limit.setOffset(new LimitValue(Integer.MAX_VALUE, (-1), true));
        selectStatement.setLimit(limit);
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextWithoutOffsetWithRowCount() throws SQLException {
        Limit limit = new Limit();
        limit.setRowCount(new LimitValue(5, (-1), false));
        selectStatement.setLimit(limit);
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(actual.next());
        }
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextWithOffsetWithoutRowCount() throws SQLException {
        Limit limit = new Limit();
        limit.setOffset(new LimitValue(2, (-1), true));
        selectStatement.setLimit(limit);
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(actual.next());
        }
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextWithOffsetBoundOpenedFalse() throws SQLException {
        Limit limit = new Limit();
        limit.setOffset(new LimitValue(2, (-1), false));
        limit.setRowCount(new LimitValue(4, (-1), false));
        selectStatement.setLimit(limit);
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertTrue(actual.next());
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertNextWithOffsetBoundOpenedTrue() throws SQLException {
        Limit limit = new Limit();
        limit.setOffset(new LimitValue(2, (-1), true));
        limit.setRowCount(new LimitValue(4, (-1), false));
        selectStatement.setLimit(limit);
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertTrue(actual.next());
        Assert.assertTrue(actual.next());
        Assert.assertTrue(actual.next());
        Assert.assertFalse(actual.next());
    }
}

