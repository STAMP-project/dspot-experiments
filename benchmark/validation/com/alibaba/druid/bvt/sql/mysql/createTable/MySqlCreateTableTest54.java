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


import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;
import org.junit.Test;


public class MySqlCreateTableTest54 extends MysqlTest {
    @Test
    public void test_UNSIGNED_ZEROFILL() throws Exception {
        String sql = "CREATE TABLE t1 (year YEAR(4), month INT(2) UNSIGNED ZEROFILL, day INT(2) UNSIGNED ZEROFILL);";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(3, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals(("CREATE TABLE t1 (" + ((("\n\tyear YEAR(4)," + "\n\tmonth INT(2) UNSIGNED ZEROFILL,") + "\n\tday INT(2) UNSIGNED ZEROFILL") + "\n)")), output);
    }

    @Test
    public void test_FOREIGN_KEY() throws Exception {
        String sql = "CREATE TABLE `t_activity_node` (" + ((((((((((((((((((((((((((("\n`id` bigint(20) NOT NULL," + "\n`sellerId` bigint(20) DEFAULT NULL,") + "\n`canvas_id` bigint(20) NOT NULL COMMENT \'\u753b\u5e03ID\',") + "\n`view_node_id` bigint(20) NOT NULL COMMENT \'\u5bf9\u5e94\u663e\u793a\u7684\u8282\u70b9id\',") + "\n`activity_type` int(11) NOT NULL COMMENT \'\u6d3b\u52a8\u7c7b\u578b\',") + "\n`node_type` int(11) NOT NULL COMMENT \'\u8282\u70b9\u7c7b\u578b\',") + "\n`node_title` varchar(200) NOT NULL COMMENT \'\u8282\u70b9\u6807\u9898\',") + "\n`status` int(11) NOT NULL DEFAULT \'0\' COMMENT \'\u9875\u9762\u7684\u663e\u793a\u72b6\u6001\',") + "\n`update_status` int(11) DEFAULT NULL COMMENT \'\u8282\u70b9\u521b\u5efa\u540e\u7684\u4fee\u6539\u72b6\u6001\',") + "\n`execute_status` int(11) DEFAULT NULL COMMENT \'\u8282\u70b9\u5f53\u524d\u7684\u6267\u884c\u72b6\u6001\',") + "\n`start_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n`end_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n`activity_start_time` datetime DEFAULT NULL COMMENT \'\u8425\u9500\u6d3b\u52a8\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n`activity_end_time` datetime DEFAULT NULL COMMENT \'\u8425\u9500\u6d3b\u52a8\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n`report_start_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u6548\u679c\u62a5\u544a\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n`report_end_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u6548\u679c\u62a5\u544a\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n`cron_rule` varchar(100) DEFAULT NULL COMMENT \'\u5468\u671f\u6027\u8425\u9500\u7684\u65f6\u95f4\u8868\u8fbe\u5f0f\',") + "\n`split_rule` varchar(1000) DEFAULT NULL COMMENT \'\u8282\u70b9\u62c6\u5206\u89c4\u5219\',") + "\n`search_json` varchar(5000) DEFAULT NULL COMMENT \'\u7d22\u5f15\u67e5\u627e\u7684\u6761\u4ef6\u5b57\u7b26\u4e32\',") + "\n`filter_json` varchar(1000) DEFAULT NULL COMMENT \'\u5404\u79cd\u8fc7\u6ee4\u6761\u4ef6\u7684\u8bbe\u7f6e\',") + "\n`buyer_count` int(11) DEFAULT NULL COMMENT \'\u8425\u9500\u7684\u4f1a\u5458\u6570\u91cf\',") + "\n`gmt_modified` datetime NOT NULL COMMENT \'\u6d3b\u52a8\u6700\u540e\u4fee\u6539\u65f6\u95f4\',") + "\n`gmt_create` datetime NOT NULL COMMENT \'\u6d3b\u52a8\u521b\u5efa\u65f6\u95f4\',") + "\nPRIMARY KEY (`id`),") + "\nKEY `canvas_id` (`canvas_id`),") + "\nKEY `sid_ty_time` (`sellerId`,`node_type`,`start_time`),") + "\nCONSTRAINT `t_activity_node_ibfk_1` FOREIGN KEY (`canvas_id`) REFERENCES `t_activity_canvas` (`id`) ON DELETE CASCADE") + "\n) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        Assert.assertEquals(2, visitor.getTables().size());
        Assert.assertEquals(24, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals(("CREATE TABLE `t_activity_node` (" + ((((((((((((((((((((((((((("\n\t`id` bigint(20) NOT NULL," + "\n\t`sellerId` bigint(20) DEFAULT NULL,") + "\n\t`canvas_id` bigint(20) NOT NULL COMMENT \'\u753b\u5e03ID\',") + "\n\t`view_node_id` bigint(20) NOT NULL COMMENT \'\u5bf9\u5e94\u663e\u793a\u7684\u8282\u70b9id\',") + "\n\t`activity_type` int(11) NOT NULL COMMENT \'\u6d3b\u52a8\u7c7b\u578b\',") + "\n\t`node_type` int(11) NOT NULL COMMENT \'\u8282\u70b9\u7c7b\u578b\',") + "\n\t`node_title` varchar(200) NOT NULL COMMENT \'\u8282\u70b9\u6807\u9898\',") + "\n\t`status` int(11) NOT NULL DEFAULT \'0\' COMMENT \'\u9875\u9762\u7684\u663e\u793a\u72b6\u6001\',") + "\n\t`update_status` int(11) DEFAULT NULL COMMENT \'\u8282\u70b9\u521b\u5efa\u540e\u7684\u4fee\u6539\u72b6\u6001\',") + "\n\t`execute_status` int(11) DEFAULT NULL COMMENT \'\u8282\u70b9\u5f53\u524d\u7684\u6267\u884c\u72b6\u6001\',") + "\n\t`start_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n\t`end_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n\t`activity_start_time` datetime DEFAULT NULL COMMENT \'\u8425\u9500\u6d3b\u52a8\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n\t`activity_end_time` datetime DEFAULT NULL COMMENT \'\u8425\u9500\u6d3b\u52a8\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n\t`report_start_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u6548\u679c\u62a5\u544a\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n\t`report_end_time` datetime DEFAULT NULL COMMENT \'\u8be5\u8282\u70b9\u6d3b\u52a8\u6548\u679c\u62a5\u544a\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n\t`cron_rule` varchar(100) DEFAULT NULL COMMENT \'\u5468\u671f\u6027\u8425\u9500\u7684\u65f6\u95f4\u8868\u8fbe\u5f0f\',") + "\n\t`split_rule` varchar(1000) DEFAULT NULL COMMENT \'\u8282\u70b9\u62c6\u5206\u89c4\u5219\',") + "\n\t`search_json` varchar(5000) DEFAULT NULL COMMENT \'\u7d22\u5f15\u67e5\u627e\u7684\u6761\u4ef6\u5b57\u7b26\u4e32\',") + "\n\t`filter_json` varchar(1000) DEFAULT NULL COMMENT \'\u5404\u79cd\u8fc7\u6ee4\u6761\u4ef6\u7684\u8bbe\u7f6e\',") + "\n\t`buyer_count` int(11) DEFAULT NULL COMMENT \'\u8425\u9500\u7684\u4f1a\u5458\u6570\u91cf\',") + "\n\t`gmt_modified` datetime NOT NULL COMMENT \'\u6d3b\u52a8\u6700\u540e\u4fee\u6539\u65f6\u95f4\',") + "\n\t`gmt_create` datetime NOT NULL COMMENT \'\u6d3b\u52a8\u521b\u5efa\u65f6\u95f4\',") + "\n\tPRIMARY KEY (`id`),") + "\n\tKEY `canvas_id` (`canvas_id`),") + "\n\tKEY `sid_ty_time` (`sellerId`, `node_type`, `start_time`),") + "\n\tCONSTRAINT `t_activity_node_ibfk_1` FOREIGN KEY (`canvas_id`) REFERENCES `t_activity_canvas` (`id`) ON DELETE CASCADE") + "\n) ENGINE = InnoDB CHARSET = utf8")), output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals(("create table `t_activity_node` (" + ((((((((((((((((((((((((((("\n\t`id` bigint(20) not null," + "\n\t`sellerId` bigint(20) default null,") + "\n\t`canvas_id` bigint(20) not null comment \'\u753b\u5e03ID\',") + "\n\t`view_node_id` bigint(20) not null comment \'\u5bf9\u5e94\u663e\u793a\u7684\u8282\u70b9id\',") + "\n\t`activity_type` int(11) not null comment \'\u6d3b\u52a8\u7c7b\u578b\',") + "\n\t`node_type` int(11) not null comment \'\u8282\u70b9\u7c7b\u578b\',") + "\n\t`node_title` varchar(200) not null comment \'\u8282\u70b9\u6807\u9898\',") + "\n\t`status` int(11) not null default \'0\' comment \'\u9875\u9762\u7684\u663e\u793a\u72b6\u6001\',") + "\n\t`update_status` int(11) default null comment \'\u8282\u70b9\u521b\u5efa\u540e\u7684\u4fee\u6539\u72b6\u6001\',") + "\n\t`execute_status` int(11) default null comment \'\u8282\u70b9\u5f53\u524d\u7684\u6267\u884c\u72b6\u6001\',") + "\n\t`start_time` datetime default null comment \'\u8be5\u8282\u70b9\u6d3b\u52a8\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n\t`end_time` datetime default null comment \'\u8be5\u8282\u70b9\u6d3b\u52a8\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n\t`activity_start_time` datetime default null comment \'\u8425\u9500\u6d3b\u52a8\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n\t`activity_end_time` datetime default null comment \'\u8425\u9500\u6d3b\u52a8\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n\t`report_start_time` datetime default null comment \'\u8be5\u8282\u70b9\u6d3b\u52a8\u6548\u679c\u62a5\u544a\u7684\u5f00\u59cb\u65f6\u95f4\',") + "\n\t`report_end_time` datetime default null comment \'\u8be5\u8282\u70b9\u6d3b\u52a8\u6548\u679c\u62a5\u544a\u7684\u7ed3\u675f\u65f6\u95f4\',") + "\n\t`cron_rule` varchar(100) default null comment \'\u5468\u671f\u6027\u8425\u9500\u7684\u65f6\u95f4\u8868\u8fbe\u5f0f\',") + "\n\t`split_rule` varchar(1000) default null comment \'\u8282\u70b9\u62c6\u5206\u89c4\u5219\',") + "\n\t`search_json` varchar(5000) default null comment \'\u7d22\u5f15\u67e5\u627e\u7684\u6761\u4ef6\u5b57\u7b26\u4e32\',") + "\n\t`filter_json` varchar(1000) default null comment \'\u5404\u79cd\u8fc7\u6ee4\u6761\u4ef6\u7684\u8bbe\u7f6e\',") + "\n\t`buyer_count` int(11) default null comment \'\u8425\u9500\u7684\u4f1a\u5458\u6570\u91cf\',") + "\n\t`gmt_modified` datetime not null comment \'\u6d3b\u52a8\u6700\u540e\u4fee\u6539\u65f6\u95f4\',") + "\n\t`gmt_create` datetime not null comment \'\u6d3b\u52a8\u521b\u5efa\u65f6\u95f4\',") + "\n\tprimary key (`id`),") + "\n\tkey `canvas_id` (`canvas_id`),") + "\n\tkey `sid_ty_time` (`sellerId`, `node_type`, `start_time`),") + "\n\tconstraint `t_activity_node_ibfk_1` foreign key (`canvas_id`) references `t_activity_canvas` (`id`) on delete cascade") + "\n) engine = InnoDB charset = utf8")), output);
        }
    }
}

