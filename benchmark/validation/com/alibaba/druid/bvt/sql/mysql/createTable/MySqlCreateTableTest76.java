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
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;
import org.junit.Test;


public class MySqlCreateTableTest76 extends MysqlTest {
    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE `sys_msg_entry_0320` (\n" + (((((((((((((((((("  `provider_dsp_name` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u6d88\u606f\u63d0\u4f9b\u8005\u7684\u663e\u793a\u540d\uff0c\u63d0\u4f9b\u7ed9\u4e00\u4e9b\u9700\u8981\u7edf\u4e00\u53d1\u9001\u540d\u79f0\u7684\u7cfb\u7edf\u6d88\u606f\',\n" + "  `template_data` varchar(2048) NOT NULL /*!50616 COLUMN_FORMAT COMPRESSED */ COMMENT \'\u6a21\u677f\u6e32\u67d3\u65f6\u4f7f\u7528\u7684\u6570\u636e,key-value\u5bf9\',\n") + "  `template_merge_data` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u5bf9\u4e8e\u9700\u8981\u5408\u5e76\u7684\u6d88\u606f\uff0c\u88ab\u5408\u5e76\u7684\u53c2\u6570\u653e\u5728\u6b64\u5904\u7406\',\n") + "  `expiration_date` datetime NOT NULL COMMENT \'\u5931\u6548\u65e5\u671f\uff0c\u7cbe\u5ea6\u5230\u5929,\u67e5\u8be2\u548c\u4efb\u52a1\u5904\u7406\u65f6\u4f7f\u7528\',\n") + "  `merge_key` varchar(128) DEFAULT NULL COMMENT \'\u6d88\u606f\u5408\u5e76\u4e3b\u952e\uff0c\u5408\u5e76\u6210\u529f\u7684\u6d88\u606f\uff0c\u4e0d\u53c2\u4e0e\u8ba1\u6570\',\n") + "  `out_id` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u5916\u90e8\u5173\u8054\u6e90\u7684id\',\n") + "  `number_expiration_date` datetime DEFAULT NULL COMMENT \'\u8ba1\u6570\u8fc7\u671f\u65f6\u95f4\',\n") + "  `hidden` int(11) DEFAULT \'0\' COMMENT \'\u6807\u8bb0\u662f\u5426\u5728\u540a\u9876\u5c55\u793a\uff0c1\u8868\u793a\u9690\u85cf\uff0c0\u8868\u793a\u5c55\u793a\',\n") + "  `popup` tinyint(3) unsigned DEFAULT \'0\' COMMENT \'\u8868\u793a\u6d88\u606f\u63d0\u9192\u65b9\u5f0f:0-\u6570\u5b57\u63d0\u9192\uff0c1-layer\uff0c2-popup\',\n") + "  `tag_id` bigint(20) unsigned DEFAULT NULL COMMENT \'TAG\u6807\u5fd7\',\n") + "  `attribute` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u6d88\u606f\u7684\u4e00\u4e9b\u5c5e\u6027\',\n") + "  `original_msg_ids` varchar(1024) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u539f\u59cb\u6d88\u606fID\uff0c\u591a\u4e2aid\u7528\u534a\u89d2\u9017\u53f7\u9694\u5f00\',\n") + "  PRIMARY KEY (`id`),\n") + "  KEY `idx_expiration_date` (`expiration_date`),\n") + "  KEY `k_rid_hidd_cid` (`receiver_id`,`hidden`,`cat_id`,`expiration_date`),\n") + "  KEY `k_rid_aid_tid` (`receiver_id`,`app_id`,`type_id`,`expiration_date`),\n") + "  KEY `k_rid_stat_popup` (`receiver_id`,`status`,`popup`,`expiration_date`),\n") + "  KEY `k_tid` (`tag_id`)\n") + ") ENGINE=InnoDB AUTO_INCREMENT=167279613030 DEFAULT CHARSET=gbk COMMENT='?????'");
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        Column column = visitor.getColumn("sys_msg_entry_0320", "provider_dsp_name");
        Assert.assertNotNull(column);
        Assert.assertEquals("varchar", column.getDataType());
        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals(("CREATE TABLE `sys_msg_entry_0320` (\n" + (((((((((((((((((("\t`provider_dsp_name` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u6d88\u606f\u63d0\u4f9b\u8005\u7684\u663e\u793a\u540d\uff0c\u63d0\u4f9b\u7ed9\u4e00\u4e9b\u9700\u8981\u7edf\u4e00\u53d1\u9001\u540d\u79f0\u7684\u7cfb\u7edf\u6d88\u606f\',\n" + "\t`template_data` varchar(2048) NOT NULL /*!50616 COLUMN_FORMAT COMPRESSED */ COMMENT \'\u6a21\u677f\u6e32\u67d3\u65f6\u4f7f\u7528\u7684\u6570\u636e,key-value\u5bf9\',\n") + "\t`template_merge_data` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u5bf9\u4e8e\u9700\u8981\u5408\u5e76\u7684\u6d88\u606f\uff0c\u88ab\u5408\u5e76\u7684\u53c2\u6570\u653e\u5728\u6b64\u5904\u7406\',\n") + "\t`expiration_date` datetime NOT NULL COMMENT \'\u5931\u6548\u65e5\u671f\uff0c\u7cbe\u5ea6\u5230\u5929,\u67e5\u8be2\u548c\u4efb\u52a1\u5904\u7406\u65f6\u4f7f\u7528\',\n") + "\t`merge_key` varchar(128) DEFAULT NULL COMMENT \'\u6d88\u606f\u5408\u5e76\u4e3b\u952e\uff0c\u5408\u5e76\u6210\u529f\u7684\u6d88\u606f\uff0c\u4e0d\u53c2\u4e0e\u8ba1\u6570\',\n") + "\t`out_id` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u5916\u90e8\u5173\u8054\u6e90\u7684id\',\n") + "\t`number_expiration_date` datetime DEFAULT NULL COMMENT \'\u8ba1\u6570\u8fc7\u671f\u65f6\u95f4\',\n") + "\t`hidden` int(11) DEFAULT \'0\' COMMENT \'\u6807\u8bb0\u662f\u5426\u5728\u540a\u9876\u5c55\u793a\uff0c1\u8868\u793a\u9690\u85cf\uff0c0\u8868\u793a\u5c55\u793a\',\n") + "\t`popup` tinyint(3) UNSIGNED DEFAULT \'0\' COMMENT \'\u8868\u793a\u6d88\u606f\u63d0\u9192\u65b9\u5f0f:0-\u6570\u5b57\u63d0\u9192\uff0c1-layer\uff0c2-popup\',\n") + "\t`tag_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT \'TAG\u6807\u5fd7\',\n") + "\t`attribute` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u6d88\u606f\u7684\u4e00\u4e9b\u5c5e\u6027\',\n") + "\t`original_msg_ids` varchar(1024) /*!50616 COLUMN_FORMAT COMPRESSED */ DEFAULT NULL COMMENT \'\u539f\u59cb\u6d88\u606fID\uff0c\u591a\u4e2aid\u7528\u534a\u89d2\u9017\u53f7\u9694\u5f00\',\n") + "\tPRIMARY KEY (`id`),\n") + "\tKEY `idx_expiration_date` (`expiration_date`),\n") + "\tKEY `k_rid_hidd_cid` (`receiver_id`, `hidden`, `cat_id`, `expiration_date`),\n") + "\tKEY `k_rid_aid_tid` (`receiver_id`, `app_id`, `type_id`, `expiration_date`),\n") + "\tKEY `k_rid_stat_popup` (`receiver_id`, `status`, `popup`, `expiration_date`),\n") + "\tKEY `k_tid` (`tag_id`)\n") + ") ENGINE = InnoDB AUTO_INCREMENT = 167279613030 CHARSET = gbk COMMENT '?????'")), output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals(("create table `sys_msg_entry_0320` (\n" + (((((((((((((((((("\t`provider_dsp_name` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment \'\u6d88\u606f\u63d0\u4f9b\u8005\u7684\u663e\u793a\u540d\uff0c\u63d0\u4f9b\u7ed9\u4e00\u4e9b\u9700\u8981\u7edf\u4e00\u53d1\u9001\u540d\u79f0\u7684\u7cfb\u7edf\u6d88\u606f\',\n" + "\t`template_data` varchar(2048) not null /*!50616 COLUMN_FORMAT COMPRESSED */ comment \'\u6a21\u677f\u6e32\u67d3\u65f6\u4f7f\u7528\u7684\u6570\u636e,key-value\u5bf9\',\n") + "\t`template_merge_data` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment \'\u5bf9\u4e8e\u9700\u8981\u5408\u5e76\u7684\u6d88\u606f\uff0c\u88ab\u5408\u5e76\u7684\u53c2\u6570\u653e\u5728\u6b64\u5904\u7406\',\n") + "\t`expiration_date` datetime not null comment \'\u5931\u6548\u65e5\u671f\uff0c\u7cbe\u5ea6\u5230\u5929,\u67e5\u8be2\u548c\u4efb\u52a1\u5904\u7406\u65f6\u4f7f\u7528\',\n") + "\t`merge_key` varchar(128) default null comment \'\u6d88\u606f\u5408\u5e76\u4e3b\u952e\uff0c\u5408\u5e76\u6210\u529f\u7684\u6d88\u606f\uff0c\u4e0d\u53c2\u4e0e\u8ba1\u6570\',\n") + "\t`out_id` varchar(256) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment \'\u5916\u90e8\u5173\u8054\u6e90\u7684id\',\n") + "\t`number_expiration_date` datetime default null comment \'\u8ba1\u6570\u8fc7\u671f\u65f6\u95f4\',\n") + "\t`hidden` int(11) default \'0\' comment \'\u6807\u8bb0\u662f\u5426\u5728\u540a\u9876\u5c55\u793a\uff0c1\u8868\u793a\u9690\u85cf\uff0c0\u8868\u793a\u5c55\u793a\',\n") + "\t`popup` tinyint(3) unsigned default \'0\' comment \'\u8868\u793a\u6d88\u606f\u63d0\u9192\u65b9\u5f0f:0-\u6570\u5b57\u63d0\u9192\uff0c1-layer\uff0c2-popup\',\n") + "\t`tag_id` bigint(20) unsigned default null comment \'TAG\u6807\u5fd7\',\n") + "\t`attribute` varchar(512) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment \'\u6d88\u606f\u7684\u4e00\u4e9b\u5c5e\u6027\',\n") + "\t`original_msg_ids` varchar(1024) /*!50616 COLUMN_FORMAT COMPRESSED */ default null comment \'\u539f\u59cb\u6d88\u606fID\uff0c\u591a\u4e2aid\u7528\u534a\u89d2\u9017\u53f7\u9694\u5f00\',\n") + "\tprimary key (`id`),\n") + "\tkey `idx_expiration_date` (`expiration_date`),\n") + "\tkey `k_rid_hidd_cid` (`receiver_id`, `hidden`, `cat_id`, `expiration_date`),\n") + "\tkey `k_rid_aid_tid` (`receiver_id`, `app_id`, `type_id`, `expiration_date`),\n") + "\tkey `k_rid_stat_popup` (`receiver_id`, `status`, `popup`, `expiration_date`),\n") + "\tkey `k_tid` (`tag_id`)\n") + ") engine = InnoDB auto_increment = 167279613030 charset = gbk comment '?????'")), output);
        }
    }
}

