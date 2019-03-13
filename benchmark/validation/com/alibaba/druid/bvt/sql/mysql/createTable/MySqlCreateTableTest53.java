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
package com.alibaba.druid.bvt.sql.mysql.createTable;


import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;
import org.junit.Test;


public class MySqlCreateTableTest53 extends MysqlTest {
    @Test
    public void test_primary_key_using_btree() throws Exception {
        String sql = "CREATE TABLE `ins_ebay_auth` ("// 
         + (((((((("`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '??id',"// 
         + "`usr_id` int(10) NOT NULL COMMENT '??????',")// 
         + "`status` char(1) COLLATE utf8_bin NOT NULL COMMENT '?? 0.???1.??',")// 
         + "`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'eBay???',")// 
         + "`ebay_name` varchar(50)  NOT NULL COMMENT 'eBay???',")// 
         + "`create_time` datetime NOT NULL COMMENT '????',")// 
         + "`invalid_time` datetime NOT NULL COMMENT '??????',") + "PRIMARY KEY USING BTREE (`auth_id`)")// 
         + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='INS_EBAY_AUTH';");
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ins_ebay_auth")));
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals(("CREATE TABLE `ins_ebay_auth` ("// 
         + (((((((("\n\t`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT \'\u4e3b\u952eid\',"// 
         + "\n\t`usr_id` int(10) NOT NULL COMMENT \'\u5916\u952e\uff0c\u7528\u6237\u8868\',")// 
         + "\n\t`status` char(1) COLLATE utf8_bin NOT NULL COMMENT \'\u72b6\u6001 0.\u6709\u6548?1.\u65e0\u6548\',")// 
         + "\n\t`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT \'eBay\u6388\u6743\u7801\',")// 
         + "\n\t`ebay_name` varchar(50) NOT NULL COMMENT \'eBay\u552f\u4e00\u540d\',")// 
         + "\n\t`create_time` datetime NOT NULL COMMENT \'\u6388\u6743\u65f6\u95f4\',")// 
         + "\n\t`invalid_time` datetime NOT NULL COMMENT \'\u6388\u6743\u5931\u6548\u65f6\u95f4\',")// 
         + "\n\tPRIMARY KEY USING BTREE (`auth_id`)") + "\n) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin COMMENT \'INS_EBAY_AUTH\'")), output);
    }

    @Test
    public void test_index_using_btree() throws Exception {
        String sql = "CREATE TABLE `ins_ebay_auth` ("// 
         + (((((((("`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '??id',"// 
         + "`usr_id` int(10) NOT NULL COMMENT '??????',")// 
         + "`status` char(1) COLLATE utf8_bin NOT NULL COMMENT '?? 0.???1.??',")// 
         + "`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'eBay???',")// 
         + "`ebay_name` varchar(50)  NOT NULL COMMENT 'eBay???',")// 
         + "`create_time` datetime NOT NULL COMMENT '????',")// 
         + "`invalid_time` datetime NOT NULL COMMENT '??????',") + "PRIMARY KEY USING BTREE (`auth_id`), INDEX `ind_usr_id` USING BTREE(`usr_id`)")// 
         + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='INS_EBAY_AUTH';");
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ins_ebay_auth")));
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals(("CREATE TABLE `ins_ebay_auth` ("// 
         + ((((((((("\n\t`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT \'\u4e3b\u952eid\',"// 
         + "\n\t`usr_id` int(10) NOT NULL COMMENT \'\u5916\u952e\uff0c\u7528\u6237\u8868\',")// 
         + "\n\t`status` char(1) COLLATE utf8_bin NOT NULL COMMENT \'\u72b6\u6001 0.\u6709\u6548?1.\u65e0\u6548\',")// 
         + "\n\t`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT \'eBay\u6388\u6743\u7801\',")// 
         + "\n\t`ebay_name` varchar(50) NOT NULL COMMENT \'eBay\u552f\u4e00\u540d\',")// 
         + "\n\t`create_time` datetime NOT NULL COMMENT \'\u6388\u6743\u65f6\u95f4\',")// 
         + "\n\t`invalid_time` datetime NOT NULL COMMENT \'\u6388\u6743\u5931\u6548\u65f6\u95f4\',")// 
         + "\n\tPRIMARY KEY USING BTREE (`auth_id`),") + "\n\tINDEX `ind_usr_id` USING BTREE(`usr_id`)") + "\n) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin COMMENT \'INS_EBAY_AUTH\'")), output);
    }

    @Test
    public void test_key_using_btree() throws Exception {
        String sql = "CREATE TABLE `ins_ebay_auth` ("// 
         + (((((((("`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '??id',"// 
         + "`usr_id` int(10) NOT NULL COMMENT '??????',")// 
         + "`status` char(1) COLLATE utf8_bin NOT NULL COMMENT '?? 0.???1.??',")// 
         + "`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'eBay???',")// 
         + "`ebay_name` varchar(50)  NOT NULL COMMENT 'eBay???',")// 
         + "`create_time` datetime NOT NULL COMMENT '????',")// 
         + "`invalid_time` datetime NOT NULL COMMENT '??????',") + "PRIMARY KEY USING BTREE (`auth_id`), KEY `ind_usr_id` USING BTREE (`usr_id`)")// 
         + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='INS_EBAY_AUTH';");
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ins_ebay_auth")));
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals(("CREATE TABLE `ins_ebay_auth` ("// 
         + ((((((((("\n\t`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT \'\u4e3b\u952eid\',"// 
         + "\n\t`usr_id` int(10) NOT NULL COMMENT \'\u5916\u952e\uff0c\u7528\u6237\u8868\',")// 
         + "\n\t`status` char(1) COLLATE utf8_bin NOT NULL COMMENT \'\u72b6\u6001 0.\u6709\u6548?1.\u65e0\u6548\',")// 
         + "\n\t`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT \'eBay\u6388\u6743\u7801\',")// 
         + "\n\t`ebay_name` varchar(50) NOT NULL COMMENT \'eBay\u552f\u4e00\u540d\',")// 
         + "\n\t`create_time` datetime NOT NULL COMMENT \'\u6388\u6743\u65f6\u95f4\',")// 
         + "\n\t`invalid_time` datetime NOT NULL COMMENT \'\u6388\u6743\u5931\u6548\u65f6\u95f4\',")// 
         + "\n\tPRIMARY KEY USING BTREE (`auth_id`),") + "\n\tKEY `ind_usr_id` USING BTREE (`usr_id`)") + "\n) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin COMMENT \'INS_EBAY_AUTH\'")), output);
    }
}

