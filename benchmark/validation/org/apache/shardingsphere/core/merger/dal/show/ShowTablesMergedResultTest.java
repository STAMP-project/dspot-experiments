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
package org.apache.shardingsphere.core.merger.dal.show;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ShowTablesMergedResultTest {
    private ShardingRule shardingRule;

    private List<QueryResult> queryResults;

    private ResultSet resultSet;

    private ShardingTableMetaData shardingTableMetaData;

    @Test
    public void assertNextForEmptyQueryResult() throws SQLException {
        ShowTablesMergedResult showTablesMergedResult = new ShowTablesMergedResult(shardingRule, new ArrayList<QueryResult>(), shardingTableMetaData);
        Assert.assertFalse(showTablesMergedResult.next());
    }

    @Test
    public void assertNextForActualTableNameInTableRule() throws SQLException {
        Mockito.when(resultSet.getObject(1)).thenReturn("table_0");
        ShowTablesMergedResult showTablesMergedResult = new ShowTablesMergedResult(shardingRule, queryResults, shardingTableMetaData);
        Assert.assertTrue(showTablesMergedResult.next());
    }

    @Test
    public void assertNextForActualTableNameNotInTableRuleWithDefaultDataSource() throws SQLException {
        Mockito.when(resultSet.getObject(1)).thenReturn("table");
        ShowTablesMergedResult showTablesMergedResult = new ShowTablesMergedResult(shardingRule, queryResults, shardingTableMetaData);
        Assert.assertTrue(showTablesMergedResult.next());
    }

    @Test
    public void assertNextForActualTableNameNotInTableRuleWithoutDefaultDataSource() throws SQLException {
        Mockito.when(resultSet.getObject(1)).thenReturn("table_3");
        ShowTablesMergedResult showTablesMergedResult = new ShowTablesMergedResult(shardingRule, queryResults, shardingTableMetaData);
        Assert.assertFalse(showTablesMergedResult.next());
    }
}

