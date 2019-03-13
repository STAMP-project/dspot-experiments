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


public final class ShowCreateTableMergedResultTest {
    private ShardingRule shardingRule;

    private List<QueryResult> queryResults;

    private ResultSet resultSet;

    private ShardingTableMetaData shardingTableMetaData;

    @Test
    public void assertNextForEmptyQueryResult() throws SQLException {
        ShowCreateTableMergedResult showCreateTableMergedResult = new ShowCreateTableMergedResult(shardingRule, new ArrayList<QueryResult>(), shardingTableMetaData);
        Assert.assertFalse(showCreateTableMergedResult.next());
    }

    @Test
    public void assertNextForTableRuleIsPresentForBackQuotes() throws SQLException {
        Mockito.when(resultSet.getObject(1)).thenReturn("table_0");
        Mockito.when(resultSet.getObject(2)).thenReturn(("CREATE TABLE `t_order` (\n" + (((((("  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + "  `order_id` int(11) NOT NULL COMMENT,\n") + "  `user_id` int(11) NOT NULL COMMENT,\n") + "  `status` tinyint(4) NOT NULL DEFAULT \'1\',\n") + "  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n") + "  PRIMARY KEY (`id`)\n") + ") ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 COLLATE=utf8_bin")));
        ShowCreateTableMergedResult showCreateTableMergedResult = new ShowCreateTableMergedResult(shardingRule, queryResults, shardingTableMetaData);
        Assert.assertTrue(showCreateTableMergedResult.next());
    }

    @Test
    public void assertNextForTableRuleIsPresentForNoBackQuotes() throws SQLException {
        Mockito.when(resultSet.getObject(1)).thenReturn("table_0");
        Mockito.when(resultSet.getObject(2)).thenReturn(("CREATE TABLE t_order (\n" + (((((("  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + "  `order_id` int(11) NOT NULL COMMENT,\n") + "  `user_id` int(11) NOT NULL COMMENT,\n") + "  `status` tinyint(4) NOT NULL DEFAULT \'1\',\n") + "  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n") + "  PRIMARY KEY (`id`)\n") + ") ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 COLLATE=utf8_bin")));
        ShowCreateTableMergedResult showCreateTableMergedResult = new ShowCreateTableMergedResult(shardingRule, queryResults, shardingTableMetaData);
        Assert.assertTrue(showCreateTableMergedResult.next());
    }
}

