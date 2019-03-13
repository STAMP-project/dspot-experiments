/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.visitor;


import TableStat.Column;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLUnique;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class MySqlSchemaStatVisitorTest_pk_0 extends TestCase {
    public void test_0() throws Exception {
        String sql = "CREATE TABLE `m_dt` (\n" + (((((((((((((((((((("  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT \'\u4e3b\u952e\',\n" + "  `gmt_create` datetime NOT NULL COMMENT \'\u521b\u5efa\u65f6\u95f4\',\n") + "  `gmt_modified` datetime NOT NULL COMMENT \'\u4fee\u6539\u65f6\u95f4\',\n") + "  `instance_id` int(11) NOT NULL COMMENT \'\u5b9e\u4f8bid\',\n") + "  `schema_name` varchar(128) NOT NULL COMMENT \'\u6570\u636e\u5e93schema\u540d\u79f0\',\n") + "  `state` tinyint(4) NOT NULL DEFAULT \'0\' COMMENT \' x dd \',\n") + "  `busi_user` varchar(64) DEFAULT NULL COMMENT \'JDBC\u4e1a\u52a1\u7528\u6237\',\n") + "  `bzp` varchar(128) DEFAULT NULL COMMENT \'bcp\',\n") + "  `ecd` varchar(32) DEFAULT NULL COMMENT \'xxx\',\n") + "  `last_sync_time` datetime DEFAULT NULL COMMENT \'\u6700\u540e\u540c\u6b65\u65f6\u95f4\',\n") + "  `catalog_name` varchar(128) NOT NULL COMMENT \'\u7269\u7406\u5e93\u540d\u79f0\',\n") + "  `search_name` varchar(256) NOT NULL COMMENT \'\u7528\u4e8e\u641c\u7d22\uff0c\u533a\u5206\u4e0d\u540c\u6570\u636e\u5e93\u7684\u4e0d\u540c\u5b57\u6bb5\u4fe1\u606f\',\n") + "  `db_type` tinyint(4) NOT NULL COMMENT \'\u6570\u636e\u5e93\u7c7b\u578b\uff0c\u548cmeta_instance\u8868\u4e00\u81f4\',\n") + "  `et` varchar(32) NOT NULL DEFAULT \'\' COMMENT \'et\',\n") + "  `ae` varchar(32) DEFAULT NULL COMMENT \'ae\',\n") + "  PRIMARY KEY (`id`),\n") + "  UNIQUE KEY `uk_instanceid_schemaname` (`instance_id`,`catalog_name`,`schema_name`),\n") + "  KEY `idx_schema_name` (`schema_name`),\n") + "  KEY `idx_instance_id_state` (`instance_id`,`id`,`state`),\n") + "  KEY `idx_search_name` (`search_name`(255))\n") + ") ENGINE=InnoDB AUTO_INCREMENT=408695 DEFAULT CHARSET=utf8 COMMENT='??????'");
        // sql = "select columnName from table1 where id in (select id from table3 where name = ?)";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLCreateTableStatement stmt = ((SQLCreateTableStatement) (statementList.get(0)));
        Assert.assertEquals(1, statementList.size());
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        System.out.println(sql);
        System.out.println(("Tables : " + (visitor.getTables())));
        System.out.println(("fields : " + (visitor.getColumns())));
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(true, visitor.containsTable("m_dt"));
        Assert.assertEquals(15, visitor.getColumns().size());
        {
            TableStat.Column column = visitor.getColumn("m_dt", "id");
            TestCase.assertNotNull(column);
            TestCase.assertTrue(column.isPrimaryKey());
        }
        {
            TableStat.Column column = visitor.getColumn("m_dt", "schema_name");
            TestCase.assertNotNull(column);
            TestCase.assertTrue(column.isUnique());
        }
        {
            TableStat.Column column = visitor.getColumn("m_dt", "instance_id");
            TestCase.assertNotNull(column);
            TestCase.assertTrue(column.isUnique());
        }
        for (SQLTableElement element : stmt.getTableElementList()) {
            if (element instanceof SQLUnique) {
                SQLName name = ((SQLUnique) (element)).getName();
                if (name != null) {
                    String uniqueName = name.toString();
                }
            }
        }
        // Assert.assertEquals(true, visitor.getFields().contains(new
        // Column("users", "id")));
        // Assert.assertEquals(true, visitor.getFields().contains(new
        // Column("users", "name")));
    }
}

